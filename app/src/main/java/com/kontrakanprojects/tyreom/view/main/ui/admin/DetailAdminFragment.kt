package com.kontrakanprojects.tyreom.view.main.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.kontrakanprojects.tyreom.R
import com.kontrakanprojects.tyreom.databinding.FragmentDetailAdminBinding
import com.kontrakanprojects.tyreom.model.admin.ResultAdmin
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.utils.showMessage
import www.sanju.motiontoast.MotionToast

class DetailAdminFragment : Fragment(), View.OnClickListener {

    private val viewModel by viewModels<DetailAdminViewModel>()
    private var _binding: FragmentDetailAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var etUiUpdated: TextView
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View

    private var idAdmin = 0
    private var valid = true

    companion object {
        private const val OLD_PASSWORD_IS_REQUIRED = "Password Lama Harus Di Isi"
        private const val NEW_PASSWORD_IS_REQUIRED = "Password Baru Harus Di Isi"
        private const val NEWAGAIN_PASSWORD_IS_REQUIRED = "Password Baru Harus Di Isi Ulang"
        private const val WRONG_OLD_PASSWORD = "Password Lama Tidak Sesuai!"
        private const val WRONG_NEW_PASSWORD_AGAIN = "Password Baru Tidak Sesuai!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
        private const val NOT_NULL = "Field tidak boleh kosong!"
    }

    private val TAG = DetailAdminFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idAdmin = DetailAdminFragmentArgs.fromBundle(arguments as Bundle).idAdmin

        setToolbarTitle()
        networkConnection()
    }

    override fun onClick(v: View?) {
        with(binding) {
            when (v?.id) {
                R.id.layoutFullName -> {
                    val itemProfile = tvAdminName.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleName)
                    etUiUpdated = tvAdminName
                    showBottomSheet(itemProfile, titleSheet, "nama_admin", true)
                }
                R.id.layoutNIK -> {
                    val itemProfile = tvAdminUsername.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleNIK)
                    etUiUpdated = tvAdminUsername
                    showBottomSheet(itemProfile, titleSheet, "nik", true)
                }
                R.id.layoutPassword -> {
                    val itemProfile = tvAdminPassword.text.toString().trim()
                    etUiUpdated = tvAdminPassword
                    showBottomSheet(
                        itemProfile, param = "password", isEditProfile = true,
                        isEditPassword = true
                    )
                }
            }
        }
    }

    private fun networkConnection() {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                observeDetailAdmin()

                with(binding) {
                    layoutNIK.setOnClickListener(this@DetailAdminFragment)
                    layoutFullName.setOnClickListener(this@DetailAdminFragment)
                    layoutPassword.setOnClickListener(this@DetailAdminFragment)
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

    private fun observeDetailAdmin() {
        isLoading(true)
        viewModel.getDetailAdmin(idAdmin).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    if (result != null) {
                        prepareEdit(result[0])
                    }
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

    private fun prepareEdit(result: ResultAdmin) {
        with(binding) {
            tvAdminUsername.text = result.username.toString()
            tvAdminName.text = result.namaAdmin
            tvAdminPassword.text = result.password
        }
    }

    private fun showBottomSheet(
        itemProfile: String? = null, titleSheet: String? = null,
        param: String? = null, isEditProfile: Boolean,
        isEditPassword: Boolean = false
    ) {
        bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)

        initLayoutBottomSheet(isEditProfile, isEditPassword)

        // untuk edit profile bukan foto
        saveBottomSheet(
            itemProfile,
            titleSheet,
            param,
            isEditProfile,
            isEditPassword,
            bottomSheetDialog
        )

        // set ke view
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun initLayoutBottomSheet(isEditProfile: Boolean, isEditPassword: Boolean) {
        if (isEditProfile) {
            bottomSheetView = if (isEditPassword) { // change password in profile
                LayoutInflater.from(requireContext()).inflate(
                    R.layout.bottom_sheet_editprofilepass,
                    activity?.findViewById(R.id.bottomSheetEditProfilePass)
                )
            } else { // change all data profile but no password
                LayoutInflater.from(requireContext()).inflate(
                    R.layout.bottom_sheet_editprofile,
                    activity?.findViewById(R.id.bottomSheetEditProfile)
                )
            }
        }
    }

    private fun saveBottomSheet(
        itemProfile: String?,
        titleSheet: String?,
        param: String? = null,
        isEditProfile: Boolean,
        isEditPassword: Boolean = false,
        bottomSheetDialog: BottomSheetDialog,
    ) {
        if (isEditProfile) {
            // ubah title head bottomsheet selain password
            // set data ke bottomsheet
            var edtInput: TextInputLayout? = null
            if (!isEditPassword) { // init bukan edit password
                val tvTitle: TextView = bottomSheetView.findViewById(R.id.titleInput)
                edtInput = bottomSheetView.findViewById(R.id.tiEditProfile)
                tvTitle.text = titleSheet

                // watcher text
                edtInput.editText?.addTextChangedListener { s ->
                    if (s.isNullOrEmpty()) {
                        edtInput.error = NOT_NULL
                    } else {
                        edtInput.error = null
                        valid = true
                    }
                }

                edtInput.editText?.setText(itemProfile)
            } else { // init password
                val edtOldPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiOldPassword)
                val edtNewPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPassword)
                val edtNewPasswordAgain: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPasswordAgain)

                edtOldPassword.visibility = View.VISIBLE

                edtOldPassword.editText?.addTextChangedListener { s ->
                    if (s?.length!! < 5) {
                        edtOldPassword.error = MIN_COUNTER_LENGTH_PASS
                    } else {
                        edtOldPassword.error = null
                        valid = true
                    }
                }

                edtNewPassword.editText?.addTextChangedListener { s ->
                    if (s?.length!! < 5) {
                        edtNewPassword.error = MIN_COUNTER_LENGTH_PASS
                    } else {
                        edtNewPassword.error = null
                        valid = true
                    }
                }

                edtNewPasswordAgain.editText?.addTextChangedListener { s ->
                    if (s?.length!! < 5) {
                        edtNewPasswordAgain.error = MIN_COUNTER_LENGTH_PASS
                    } else {
                        edtNewPasswordAgain.error = null
                        valid = true
                    }
                }
            }

            // save ke viewmodel
            save(edtInput, itemProfile, param, isEditPassword)

            // cancel bottomsheet
            val btnCancel: Button = bottomSheetView.findViewById(R.id.btnCancel)
            btnCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun save(
        edtInput: TextInputLayout?, itemProfile: String?, param: String?,
        isEditPassword: Boolean = false,
    ) {
        // save data
        val btnSave: Button = bottomSheetView.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            val parameters = HashMap<String, String>()
            var getInput = ""

            if (!isEditPassword) { // cek jika bukan edit password arahkan kesini
                val inputData = edtInput?.editText?.text.toString().trim()
                checkValueBottomSheet(inputData, edtInput!!, NOT_NULL)

                if (valid) {
                    getInput = inputData
                }
            } else { // ambil inputan jika edit password
                // init
                val edtOldPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiOldPassword)
                val edtNewPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPassword)
                val edtNewPasswordAgain: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPasswordAgain)
                edtOldPassword.visibility = View.VISIBLE

                // init password laa
                val oldPassword = edtOldPassword.editText?.text.toString().trim()
                val newPassword = edtNewPassword.editText?.text.toString().trim()
                val newPasswordAgain = edtNewPasswordAgain.editText?.text.toString().trim()

                // cek kondisi field pada password
                checkValueBottomSheet(oldPassword, edtOldPassword, OLD_PASSWORD_IS_REQUIRED)
                checkValueBottomSheet(newPassword, edtNewPassword, NEW_PASSWORD_IS_REQUIRED)
                checkValueBottomSheet(
                    newPasswordAgain,
                    edtNewPasswordAgain,
                    NEWAGAIN_PASSWORD_IS_REQUIRED
                )

                if (valid) {
                    if (oldPassword == itemProfile) {
                        if (newPassword == newPasswordAgain) { // jika password match
                            // get input password
                            getInput = newPassword // teruskan ke variabel getinput untuk di submit
                            valid = true
                        } else {
                            edtNewPasswordAgain.error = WRONG_NEW_PASSWORD_AGAIN
                            valid = false
                            return@setOnClickListener
                        }
                    } else { // password lama tidak cocok
                        edtOldPassword.error = WRONG_OLD_PASSWORD
                        valid = false
                        return@setOnClickListener
                    }
                }
            }

            // set ke hashmap
            parameters[param!!] = getInput

            if (valid) {
                // cek request
                loadingInBottomSheet(true)
                editAdmin(getInput, parameters)
            } else {
                loadingInBottomSheet(false)
            }
        }
    }

    private fun editAdmin(newInputData: String? = null, newData: HashMap<String, String>) {
        isLoading(true)
        viewModel.editAdmin(idAdmin, newData).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    etUiUpdated.text = newInputData
                    bottomSheetDialog.dismiss()
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        response.message,
                        MotionToast.TOAST_ERROR
                    )

                    bottomSheetView.findViewById<ProgressBar>(R.id.progressBar).visibility =
                        View.GONE
                    bottomSheetView.findViewById<Button>(R.id.btnSave).visibility =
                        View.VISIBLE
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

    // loading in bottomsheet
    private fun loadingInBottomSheet(isLoading: Boolean) {
        val progressBarSheet: ProgressBar = bottomSheetView.findViewById(R.id.progressBar)
        val btnSave: Button = bottomSheetView.findViewById(R.id.btnSave)

        if (isLoading) {
            btnSave.visibility = View.INVISIBLE
            progressBarSheet.visibility = View.VISIBLE
        } else {
            btnSave.visibility = View.VISIBLE
            progressBarSheet.visibility = View.GONE
        }
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

    private fun checkValue(value: String?, textView: TextView) {
        if (value.isNullOrEmpty()) {
            textView.error = NOT_NULL
            valid = false
            return
        }
    }

    private fun checkValueBottomSheet(
        value: String?,
        edtInput: TextInputLayout,
        messageError: String,
    ) {
        if (value.isNullOrEmpty()) {
            edtInput.error = messageError
            valid = false
            return
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                getString(R.string.detail_admin)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}