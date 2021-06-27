package com.kontrakanprojects.tyreom.view.main.ui.karyawan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.tyreom.databinding.FragmentKaryawanBinding
import com.kontrakanprojects.tyreom.view.main.ui.karyawan.listkaryawan.ListKaryawanFragment

class KaryawanFragment : Fragment() {

    private var _binding: FragmentKaryawanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKaryawanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnKaryawanPicSto.setOnClickListener { moveDaftarKaryawan(ListKaryawanFragment.PIC_STO) }
            btnKaryawanPicPre.setOnClickListener { moveDaftarKaryawan(ListKaryawanFragment.PIC_PRE) }
            btnKaryawanPicOe.setOnClickListener { moveDaftarKaryawan(ListKaryawanFragment.PIC_OE) }
        }
    }

    private fun moveDaftarKaryawan(role: Int) {
        val toDaftarKaryawan =
            KaryawanFragmentDirections.actionNavigationKaryawanToDaftarKaryawanFragment()
        toDaftarKaryawan.role = role
        findNavController().navigate(toDaftarKaryawan)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}