package com.kontrakanprojects.tyreom.view.main.ui.do_pengiriman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.tyreom.R
import com.kontrakanprojects.tyreom.databinding.FragmentDoPengirimanBinding
import com.kontrakanprojects.tyreom.model.do_pengiriman.ResultsDOPengiriman
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.session.UserPreference
import com.kontrakanprojects.tyreom.utils.showMessage
import com.kontrakanprojects.tyreom.view.main.MainActivity
import com.kontrakanprojects.tyreom.view.main.ui.do_pengiriman.adapter.ListDOPengirimanAdapter
import com.kontrakanprojects.tyreom.view.main.ui.stock.detail.DetailStockBarangFragment
import www.sanju.motiontoast.MotionToast

class DOPengirimanFragment : Fragment() {

    private lateinit var viewModel: DOPengirimanViewModel
    private var _binding: FragmentDoPengirimanBinding? = null
    private val binding get() = _binding!!
    private lateinit var listDOPengirimanAdapter: ListDOPengirimanAdapter
    private lateinit var user: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(DOPengirimanViewModel::class.java)
        _binding = FragmentDoPengirimanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = UserPreference(requireContext())
        val idRole = user.getUser().idRole
        if (idRole == MainActivity.ROLE_ADMIN) {
            binding.fabDoPengiriman.visibility = View.VISIBLE
        }

        initAdapter()
        networkConnection()
        observeDOPengiriman()
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
        listDOPengirimanAdapter = ListDOPengirimanAdapter()
        with(binding.rvDoPengiriman) {
            setHasFixedSize(true)
            adapter = listDOPengirimanAdapter
        }
    }

    private fun moveDetail() {
        listDOPengirimanAdapter.setOnItemClickCallBack(object :
            ListDOPengirimanAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsDOPengiriman: ResultsDOPengiriman) {
                val toDetailDOPengiriman =
                    DOPengirimanFragmentDirections.actionNavigationDoPengirimanToDetailDOPengirimanFragment()
                        .apply {
                            noDo = resultsDOPengiriman.noDo!!
                            request = DetailStockBarangFragment.REQUEST_EDIT
                        }
                findNavController().navigate(toDetailDOPengiriman)
            }
        })

        binding.fabDoPengiriman.setOnClickListener {
            val toDetailKaryawan =
                DOPengirimanFragmentDirections.actionNavigationDoPengirimanToDetailDOPengirimanFragment()
                    .apply {
                        request = DetailStockBarangFragment.REQUEST_ADD
                    }
            findNavController().navigate(toDetailKaryawan)
        }
    }

    private fun observeDOPengiriman() {
        isLoading(true)
        viewModel.getDOPengiriman().observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    listDOPengirimanAdapter.setData(result)
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