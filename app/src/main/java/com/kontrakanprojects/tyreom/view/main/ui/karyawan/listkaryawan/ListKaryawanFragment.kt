package com.kontrakanprojects.tyreom.view.main.ui.karyawan.listkaryawan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.tyreom.R
import com.kontrakanprojects.tyreom.databinding.FragmentListKaryawanBinding
import com.kontrakanprojects.tyreom.model.pegawai.ResultsKaryawan
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.utils.showMessage
import com.kontrakanprojects.tyreom.view.main.ui.karyawan.adapter.ListKaryawanAdapter
import com.kontrakanprojects.tyreom.view.main.ui.karyawan.listkaryawan.detail.DetailKaryawanFragment
import com.kontrakanprojects.tyreom.view.main.ui.karyawan.viewmodel.KaryawanViewModel
import www.sanju.motiontoast.MotionToast

class ListKaryawanFragment : Fragment() {

    private var _binding: FragmentListKaryawanBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<KaryawanViewModel>()
    private lateinit var karyawanAdapter: ListKaryawanAdapter

    private var idRole = 0

    companion object {
        const val PIC_STO = 2
        const val PIC_OE = 3
        const val PIC_PRE = 4
    }

    private val TAG = ListKaryawanFragment::class.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListKaryawanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idRole = ListKaryawanFragmentArgs.fromBundle(arguments as Bundle).role

        initAdapter()
        networkConnection(idRole)
    }

    private fun initAdapter() {
        karyawanAdapter = ListKaryawanAdapter()
        with(binding.rvListKaryawan) {
            setHasFixedSize(true)
            adapter = karyawanAdapter
        }
    }

    private fun moveDetail() {
        karyawanAdapter.setOnItemClickCallBack(object : ListKaryawanAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsKaryawan: ResultsKaryawan) {
                val toDetailKaryawan =
                    ListKaryawanFragmentDirections.actionListKaryawanFragmentToDetailKaryawanFragment()
                        .apply {
                            role = idRole
                            nik = resultsKaryawan.nik!!
                            request = DetailKaryawanFragment.REQUEST_EDIT
                        }
                findNavController().navigate(toDetailKaryawan)
            }
        })

        binding.fabDaftarKaryawan.setOnClickListener {
            val toDetailKaryawan =
                ListKaryawanFragmentDirections.actionListKaryawanFragmentToDetailKaryawanFragment()
                    .apply {
                        role = idRole
                        request = DetailKaryawanFragment.REQUEST_ADD
                    }
            findNavController().navigate(toDetailKaryawan)
        }
    }

    private fun observeKaryawan(role: Int) {
        isLoading(true)
        viewModel.getKaryawan(role).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    karyawanAdapter.setData(result)
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

    private fun networkConnection(role: Int) {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
//            switchLayout(isConnected)
//            refresh(isConnected)
//            scroll()
//            } else {
//            switchLayout(isConnected)
//            refresh(isConnected)
//            }

            if (isConnected) {
                if (karyawanAdapter.getData().isNullOrEmpty()) {
                    observeKaryawan(role)
                }

                moveDetail()
            } else {
                showMessage(
                    requireActivity(),
                    getString(R.string.no_connection),
                    style = MotionToast.TOAST_NO_INTERNET
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