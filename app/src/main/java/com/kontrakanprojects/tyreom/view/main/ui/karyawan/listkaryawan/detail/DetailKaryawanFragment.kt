package com.kontrakanprojects.tyreom.view.main.ui.karyawan.listkaryawan.detail

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kontrakanprojects.tyreom.R
import com.kontrakanprojects.tyreom.databinding.FragmentDetailKaryawanBinding
import com.kontrakanprojects.tyreom.model.pegawai.ResultsKaryawan
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.session.UserPreference
import com.kontrakanprojects.tyreom.utils.showMessage
import com.kontrakanprojects.tyreom.view.main.ui.karyawan.viewmodel.KaryawanViewModel
import www.sanju.motiontoast.MotionToast
import java.util.*
import kotlin.collections.HashMap

class DetailKaryawanFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDetailKaryawanBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<KaryawanViewModel>()

    private lateinit var etUiUpdated: TextView
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View

    private var role = 0
    private var nik = 0
    private var request = 0
    private var isEdit = false
    private var isDetailKaryawan = false
    private var valid = true

    companion object {
        const val ROLE_ADMIN = 1
        const val REQUEST_ADD = 100
        const val REQUEST_EDIT = 200
        private const val OLD_PASSWORD_IS_REQUIRED = "Password Lama Harus Di Isi"
        private const val NEW_PASSWORD_IS_REQUIRED = "Password Baru Harus Di Isi"
        private const val NEWAGAIN_PASSWORD_IS_REQUIRED = "Password Baru Harus Di Isi Ulang"
        private const val WRONG_OLD_PASSWORD = "Password Lama Tidak Sesuai!"
        private const val WRONG_NEW_PASSWORD_AGAIN = "Password Baru Tidak Sesuai!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
        private const val NOT_NULL = "Field tidak boleh kosong!"
    }

    private val TAG = DetailKaryawanFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailKaryawanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailKaryawanFragmentArgs.fromBundle(arguments as Bundle)
        role = args.role
        nik = args.nik
        request = args.request
        isDetailKaryawan = args.isDetail

        lateinit var titleToolbar: String
        with(binding) {
            when (request) {
                REQUEST_ADD -> titleToolbar = getString(R.string.tambah_karyawan)
                REQUEST_EDIT -> {
                    titleToolbar = getString(R.string.edit_karyawan)
                    isEdit = true
                    btnSaveKaryawan.visibility = View.GONE
                }
            }
        }

        setToolbarTitle(titleToolbar)
        networkConnection(role, nik)
    }

    override fun onClick(v: View?) {
        with(binding) {
            when (v?.id) {
                R.id.layoutFullName -> {
                    val itemProfile = tvKaryawanName.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleName)
                    etUiUpdated = tvKaryawanName
                    showBottomSheet(itemProfile, titleSheet, "nama_karyawan", true)
                }
                R.id.layoutNIK -> {
                    val itemProfile = tvKaryawanNIK.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleNIK)
                    etUiUpdated = tvKaryawanNIK
                    showBottomSheet(itemProfile, titleSheet, "nik", true, isEditNIK = true)
                }
                R.id.layoutPassword -> {
                    val itemProfile = tvKaryawanPassword.text.toString().trim()
                    etUiUpdated = tvKaryawanPassword
                    showBottomSheet(
                        itemProfile, param = "password", isEditProfile = true,
                        isEditPassword = true
                    )
                }
                R.id.btn_save_karyawan -> prepareAdd()
            }
        }
    }

    private fun networkConnection(role: Int, nik: Int) {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                if (isEdit) {
                    observeDetailKaryawan(role, nik)
                }

                with(binding) {
                    val userPreference = UserPreference(requireContext()).getUser().idRole
                    if (userPreference == ROLE_ADMIN) {
                        layoutNIK.setOnClickListener(this@DetailKaryawanFragment)
                    } else {
                        icEditNIK.visibility = View.GONE // hilangkan visible edit pada nik
                    }

                    layoutFullName.setOnClickListener(this@DetailKaryawanFragment)
                    layoutPassword.setOnClickListener(this@DetailKaryawanFragment)
                    btnSaveKaryawan.setOnClickListener(this@DetailKaryawanFragment)
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

    private fun observeDetailKaryawan(role: Int, nik: Int) {
        isLoading(true)
        viewModel.getDetailKaryawan(role, nik).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    prepareEdit(result!![0])
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

    private fun prepareAdd() {
        with(binding) {
            val namaLengkap = tvKaryawanName.text.toString().trim()
            val nik = tvKaryawanNIK.text.toString().trim()
            val password = tvKaryawanPassword.text.toString().trim()

            val params = hashMapOf(
                "nik" to nik,
                "nama_karyawan" to namaLengkap,
                "password" to password
            )

            checkValue(namaLengkap, tvKaryawanName)
            checkValue(nik, tvKaryawanNIK)
            checkValue(password, tvKaryawanPassword)

            if (valid) {
                addKaryawan(role, params)
            } else {
                showMessage(
                    requireActivity(),
                    getString(R.string.text_warning),
                    getString(R.string.message_warning_add_karyawan),
                    MotionToast.TOAST_WARNING
                )
            }
        }
    }

    private fun prepareEdit(result: ResultsKaryawan) {
        with(binding) {
            tvKaryawanNIK.text = result.nik.toString()
            tvKaryawanName.text = result.namaKaryawan
            tvKaryawanPassword.text = result.password
        }
    }

    private fun showBottomSheet(
        itemProfile: String? = null, titleSheet: String? = null,
        param: String? = null, isEditProfile: Boolean,
        isEditPassword: Boolean = false, isEditNIK: Boolean = false
    ) {
        bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)

        initLayoutBottomSheet(isEditProfile, isEditPassword)

        // untuk edit profile bukan foto
        saveBottomSheet(
            itemProfile,
            titleSheet,
            param,
            isEditProfile,
            isEditNIK,
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
        isEditNIK: Boolean = false,
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

                if (isEditNIK) {
                    bottomSheetView.findViewById<TextInputEditText>(R.id.edtInput).inputType =
                        InputType.TYPE_CLASS_NUMBER
                }

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

                if (isDetailKaryawan || isEdit) {
                    edtOldPassword.visibility = View.VISIBLE
                }

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

                // init password laa
                var oldPassword = ""
                val newPassword = edtNewPassword.editText?.text.toString().trim()
                val newPasswordAgain = edtNewPasswordAgain.editText?.text.toString().trim()

                if (isEdit) { // jika tambah data karyawan tampilkan field untuk user password lama
                    edtOldPassword.visibility = View.VISIBLE
                    oldPassword = edtOldPassword.editText?.text.toString().trim()
                    checkValueBottomSheet(oldPassword, edtOldPassword, OLD_PASSWORD_IS_REQUIRED)
                }

                // cek kondisi field pada password
                checkValueBottomSheet(newPassword, edtNewPassword, NEW_PASSWORD_IS_REQUIRED)
                checkValueBottomSheet(
                    newPasswordAgain, edtNewPasswordAgain,
                    NEWAGAIN_PASSWORD_IS_REQUIRED
                )

                if (valid) {
                    // kirim data password baru ke viewmodel
                    // apakah password lama sesuai dengan password di edittext jika iya teruskan
                    // cek dulu apakah di edit atau tidak
                    if (!isEdit) {
                        if (newPassword == newPasswordAgain) { // jika password match
                            // get input password
                            getInput = newPassword // teruskan ke variabel getinput untuk di submit
                            valid = true
                        } else {
                            edtNewPasswordAgain.error = WRONG_NEW_PASSWORD_AGAIN
                            valid = false
                            return@setOnClickListener
                        }
                    } else {
                        if (oldPassword == itemProfile) {
                            if (newPassword == newPasswordAgain) { // jika password match
                                // get input password
                                getInput =
                                    newPassword // teruskan ke variabel getinput untuk di submit
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
            }

            // set ke hashmap
            parameters[param!!] = getInput

            if (valid) {
                // cek request
                loadingInBottomSheet(true)
                if (isEdit) {
                    // save dan observe (hide btn save)
                    editKaryawan(role, nik, getInput, parameters)
                } else {
                    etUiUpdated.text = getInput
                    bottomSheetDialog.dismiss()
                }
            } else {
                loadingInBottomSheet(false)
            }
        }
    }

    private fun addKaryawan(role: Int, newData: HashMap<String, String>) {
        isLoading(true, isSave = true)
        viewModel.addKaryawan(role, newData).observe(viewLifecycleOwner, { response ->
            isLoading(false, isSave = true)
            if (response != null) {
                if (response.status == 201) {
                    showMessage(
                        requireActivity(), getString(R.string.success),
                        response.message, MotionToast.TOAST_SUCCESS
                    )

                    findNavController().navigateUp()
                } else {
                    showMessage(
                        requireActivity(), getString(R.string.failed), response.message,
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

    private fun editKaryawan(
        role: Int,
        nik: Int,
        newInputData: String? = null,
        newData: HashMap<String, String>
    ) {
        isLoading(true)
        viewModel.editKaryawan(role, nik, newData).observe(viewLifecycleOwner, { response ->
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

    private fun deleteKaryawan(role: Int, nik: Int) {
        isLoading(true)
        viewModel.deleteKaryawan(role, nik).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    showMessage(
                        requireActivity(), getString(R.string.success),
                        response.message, MotionToast.TOAST_SUCCESS
                    )

                    findNavController().navigateUp()
                } else {
                    showMessage(
                        requireActivity(), getString(R.string.failed), response.message,
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

    private fun isLoading(status: Boolean, isSave: Boolean = false) {
        with(binding) {
            if (isSave) {
                if (status) {
                    btnSaveKaryawan.visibility = View.GONE
                    progressSave.visibility = View.VISIBLE
                } else {
                    btnSaveKaryawan.visibility = View.VISIBLE
                    progressSave.visibility = View.GONE
                }
            } else {
                if (status) {
                    pbLoading.visibility = View.VISIBLE
                } else {
                    pbLoading.visibility = View.GONE
                }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (isEdit) {
            if (!isDetailKaryawan) {
                inflater.inflate(R.menu.delete, menu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.delete -> showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_karyawan_title))
            .setMessage(getString(R.string.delete_karyawan_body))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                deleteKaryawan(role, nik)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setToolbarTitle(titleToolbar: String) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = titleToolbar
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}