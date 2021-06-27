package com.kontrakanprojects.tyreom.view.main.ui.estimasi_pengiriman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.tyreom.R
import com.kontrakanprojects.tyreom.databinding.FragmentEstimasiPengirimanBinding
import com.kontrakanprojects.tyreom.model.estimasi_pengiriman.ResultsEstimasiPengiriman
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.session.UserPreference
import com.kontrakanprojects.tyreom.utils.showMessage
import com.kontrakanprojects.tyreom.view.main.MainActivity
import com.kontrakanprojects.tyreom.view.main.ui.estimasi_pengiriman.adapter.ListEstimasiPengirimanAdapter
import com.kontrakanprojects.tyreom.view.main.ui.stock.detail.DetailStockBarangFragment
import www.sanju.motiontoast.MotionToast

class EstimasiPengirimanFragment : Fragment() {

    private lateinit var viewModel: EstimasiPengirimanViewModel
    private var _binding: FragmentEstimasiPengirimanBinding? = null
    private val binding get() = _binding!!
    private lateinit var listEstimasiPengirimanAdapter: ListEstimasiPengirimanAdapter
    private lateinit var user: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EstimasiPengirimanViewModel::class.java)
        _binding = FragmentEstimasiPengirimanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = UserPreference(requireContext())
        val idRole = user.getUser().idRole
        if (idRole == MainActivity.ROLE_ADMIN) {
            binding.fabEstimasiPengiriman.visibility = View.VISIBLE
        }

        initAdapter()
        networkConnection()
        observeEstimasiPengiriman()
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
        listEstimasiPengirimanAdapter = ListEstimasiPengirimanAdapter()
        with(binding.rvEstimasiPengiriman) {
            setHasFixedSize(true)
            adapter = listEstimasiPengirimanAdapter
        }
    }

    private fun moveDetail() {
        listEstimasiPengirimanAdapter.setOnItemClickCallBack(object :
            ListEstimasiPengirimanAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsEstimasiPengiriman: ResultsEstimasiPengiriman) {
                val toDetailEstimasiPengiriman =
                    EstimasiPengirimanFragmentDirections.actionNavigationEstimasiToDetailEstimasiPengirimanFragment()
                        .apply {
                            codeSize = resultsEstimasiPengiriman.codeSize!!
                            request = DetailStockBarangFragment.REQUEST_EDIT
                        }
                findNavController().navigate(toDetailEstimasiPengiriman)
            }
        })

        binding.fabEstimasiPengiriman.setOnClickListener {
            val toDetailEstimasiPengiriman =
                EstimasiPengirimanFragmentDirections.actionNavigationEstimasiToDetailEstimasiPengirimanFragment()
                    .apply {
                        request = DetailStockBarangFragment.REQUEST_ADD
                    }
            findNavController().navigate(toDetailEstimasiPengiriman)
        }
    }

    private fun observeEstimasiPengiriman() {
        isLoading(true)
        viewModel.getEstimasiPengiriman().observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    listEstimasiPengirimanAdapter.setData(result)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}