package com.kontrakanprojects.tyreom.view.main.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.tyreom.R
import com.kontrakanprojects.tyreom.databinding.FragmentSettingBinding
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.session.UserPreference
import com.kontrakanprojects.tyreom.utils.showMessage
import com.kontrakanprojects.tyreom.view.auth.AuthActivity
import com.kontrakanprojects.tyreom.view.main.MainActivity
import com.kontrakanprojects.tyreom.view.main.ui.karyawan.listkaryawan.detail.DetailKaryawanFragment
import www.sanju.motiontoast.MotionToast

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkConnection()
    }

    private fun networkConnection() {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                with(binding) {
                    btnProfile.setOnClickListener { moveToDetailProfile() }

                    btnKeluar.setOnClickListener { logOut() }
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

    private fun moveToDetailProfile() {
        val user = UserPreference(requireContext()).getUser()
        // profile untuk admin
        if (user.idRole == MainActivity.ROLE_ADMIN) {
            val toDetailAdmin =
                SettingFragmentDirections.actionNavigationSettingToDetailAdminFragment()
            toDetailAdmin.idAdmin = user.idUser ?: 0
            findNavController().navigate(toDetailAdmin)
        } else {
            // profile untuk karyawan
            val toDetailKaryawan =
                SettingFragmentDirections.actionNavigationSettingToDetailKaryawanFragment().apply {
                    request = DetailKaryawanFragment.REQUEST_EDIT
                    nik = user.idUser ?: 0
                    role = user.idRole ?: 0
                    isDetail = true
                }

            findNavController().navigate(toDetailKaryawan)
        }
    }

    private fun logOut() {
        UserPreference(requireContext()).apply {
            removeLogin()
            removeUser()
        }

        startActivity(Intent(requireContext(), AuthActivity::class.java))
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}