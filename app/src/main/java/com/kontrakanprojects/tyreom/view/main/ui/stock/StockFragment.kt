package com.kontrakanprojects.tyreom.view.main.ui.stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.tyreom.R
import com.kontrakanprojects.tyreom.databinding.FragmentStockBinding
import com.kontrakanprojects.tyreom.model.stock_barang.ResultsStockBarang
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.session.UserPreference
import com.kontrakanprojects.tyreom.utils.showMessage
import com.kontrakanprojects.tyreom.view.main.MainActivity
import com.kontrakanprojects.tyreom.view.main.ui.stock.adapter.ListStockAdapter
import com.kontrakanprojects.tyreom.view.main.ui.stock.detail.DetailStockBarangFragment
import www.sanju.motiontoast.MotionToast

class StockFragment : Fragment() {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StockViewModel
    private lateinit var listStockAdapter: ListStockAdapter
    private lateinit var user: UserPreference

    private val TAG = StockFragment::class.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        _binding = FragmentStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = UserPreference(requireContext())

        val idRole = user.getUser().idRole
        if (idRole == MainActivity.ROLE_ADMIN) {
            binding.fabStockBarang.visibility = View.VISIBLE
        }

        initAdapter()
        networkConnection()
        observeStockBarang()
    }

    private fun networkConnection() {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                val idRole = user.getUser().idRole
                if (idRole == MainActivity.ROLE_ADMIN) {
                    moveDetail()
                }
            } else {
                showMessage(
                    requireActivity(),
                    getString(R.string.no_connection),
                    style = MotionToast.TOAST_NO_INTERNET
                )
            }
        })
    }

    private fun initAdapter() {
        listStockAdapter = ListStockAdapter()
        with(binding.rvStockBarang) {
            setHasFixedSize(true)
            adapter = listStockAdapter
        }
    }

    private fun moveDetail() {
        listStockAdapter.setOnItemClickCallBack(object : ListStockAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsStockBarang: ResultsStockBarang) {
                val toDetailStockBarang =
                    StockFragmentDirections.actionNavigationStockToDetailStockBarangFragment()
                        .apply {
                            codeSize = resultsStockBarang.codeSize!!
                            request = DetailStockBarangFragment.REQUEST_EDIT
                        }
                findNavController().navigate(toDetailStockBarang)
            }
        })

        binding.fabStockBarang.setOnClickListener {
            val toDetailKaryawan =
                StockFragmentDirections.actionNavigationStockToDetailStockBarangFragment().apply {
                    request = DetailStockBarangFragment.REQUEST_ADD
                }
            findNavController().navigate(toDetailKaryawan)
        }
    }

    private fun observeStockBarang() {
        isLoading(true)
        viewModel.getStockBarang().observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    listStockAdapter.setData(result)
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        response.message,
                        MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(
                    requireActivity(),
                    getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR
                )
            }
        })
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}