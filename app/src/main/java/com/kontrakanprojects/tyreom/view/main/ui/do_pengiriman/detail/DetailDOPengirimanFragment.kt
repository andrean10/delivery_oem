package com.kontrakanprojects.tyreom.view.main.ui.do_pengiriman.detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.kontrakanprojects.tyreom.R
import com.kontrakanprojects.tyreom.databinding.FragmentDetailDoPengirimanBinding
import com.kontrakanprojects.tyreom.model.do_pengiriman.ResultsDOPengiriman
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.utils.hideKeyboard
import com.kontrakanprojects.tyreom.utils.showMessage
import com.kontrakanprojects.tyreom.view.main.ui.do_pengiriman.DOPengirimanViewModel
import www.sanju.motiontoast.MotionToast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DetailDOPengirimanFragment : Fragment(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: DOPengirimanViewModel
    private var _binding: FragmentDetailDoPengirimanBinding? = null
    private val binding get() = _binding!!

    private var noDOParams = 0
    private var request = 0
    private var isEdit = false
    private var valid = true

    private var dayOfMonth = 0
    private var month = 0
    private var years = 0

    companion object {
        const val REQUEST_ADD = 100
        const val REQUEST_EDIT = 200
        private const val NOT_NULL = "Field tidak boleh kosong!"
    }

    private val TAG = DetailDOPengirimanFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(DOPengirimanViewModel::class.java)
        _binding = FragmentDetailDoPengirimanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailDOPengirimanFragmentArgs.fromBundle(arguments as Bundle)
        request = args.request
        noDOParams = args.noDo

        lateinit var titleToolbar: String
        lateinit var btnDescription: String
        when (request) {
            REQUEST_ADD -> {
                titleToolbar = getString(R.string.tambah_do_pengiriman)
                btnDescription = getString(R.string.save)
            }
            REQUEST_EDIT -> {
                titleToolbar = getString(R.string.edit_do_pengiriman)
                btnDescription = getString(R.string.update)
                isEdit = true
                binding.tiNoDo.visibility = View.GONE // code size di hilangkan
                observeDetailDOPengiriman(noDOParams)
            }
        }

        setToolbarTitle(titleToolbar, btnDescription)
        watcherQuery()
        networkConnection()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edt_tanggal_do -> {
                getDateCalendar()
                DatePickerDialog(
                    requireContext(),
                    this@DetailDOPengirimanFragment,
                    years,
                    month,
                    dayOfMonth
                ).show()
                hideKeyboard(requireActivity())
            }
            R.id.btn_save -> save()
        }
    }

    private fun networkConnection() {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                with(binding) {
                    edtNoDo.setOnClickListener(this@DetailDOPengirimanFragment)
                    edtQuantity.setOnClickListener(this@DetailDOPengirimanFragment)
                    edtCodeSize.setOnClickListener(this@DetailDOPengirimanFragment)
                    edtTanggalDo.setOnClickListener(this@DetailDOPengirimanFragment)
                    btnSave.setOnClickListener(this@DetailDOPengirimanFragment)
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

    private fun observeDetailDOPengiriman(noDo: Int) {
        isLoading(true)
        viewModel.getDetailDOPengiriman(noDo).observe(viewLifecycleOwner, { response ->
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

    private fun prepareEdit(result: ResultsDOPengiriman) {
        with(binding) {
            edtNoDo.setText(result.noDo.toString())
            edtQuantity.setText(result.quantity)
            edtCodeSize.setText(result.codeSize.toString())

            // format date ke local indonesia
            val formatDate = formateDate(
                "yyyy-MM-dd", result.tanggalDo!!,
                "dd-MMMM-yyyy"
            )
            edtTanggalDo.setText(formatDate)
        }
    }

    private fun save() {
        with(binding) {
            val noDo = edtNoDo.text?.trim().toString()
            val quantity = edtQuantity.text?.trim().toString()
            val codeSize = edtCodeSize.text?.trim().toString()
            val tanggalDO = edtTanggalDo.text?.trim().toString()
            // format inputan tanggal pekerjaan
            val formatedDate = formateDate("dd-MMMM-yyyy", tanggalDO, "yyyy-MM-dd")

            checkValue(noDo, tiNoDo, NOT_NULL)
            checkValue(quantity, tiQuantity, NOT_NULL)
            checkValue(codeSize, tiCodeSize, NOT_NULL)
            checkValue(tanggalDO, tiTanggalDo, NOT_NULL)

            if (valid) {
                val params = hashMapOf(
                    "no_do" to noDo,
                    "quantity" to quantity,
                    "code_size" to codeSize,
                    "tanggal_do" to formatedDate
                )

                if (!isEdit) {
                    addDOPengiriman(params)
                } else {
                    editDOPengiriman(params)
                }
            }
        }
    }

    private fun watcherQuery() {
        with(binding) {
            edtNoDo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! > 0) {
                        tiNoDo.isErrorEnabled = false
                    } else {
                        tiNoDo.isErrorEnabled = true
                        tiNoDo.error = NOT_NULL
                    }
                }
            })

            edtQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! > 0) {
                        tiQuantity.isErrorEnabled = false
                    } else {
                        tiQuantity.isErrorEnabled = true
                        tiQuantity.error = NOT_NULL
                    }
                }
            })

            edtCodeSize.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! > 0) {
                        tiCodeSize.isErrorEnabled = false
                    } else {
                        tiCodeSize.isErrorEnabled = true
                        tiCodeSize.error = NOT_NULL
                    }
                }
            })
        }
    }

    private fun addDOPengiriman(newData: HashMap<String, String>) {
        isLoading(true, isSave = true)
        viewModel.addDOPengiriman(newData).observe(viewLifecycleOwner, { response ->
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

    private fun editDOPengiriman(newData: HashMap<String, String>) {
        isLoading(true, isSave = true)
        viewModel.editDOPengiriman(noDOParams, newData).observe(viewLifecycleOwner, { response ->
            isLoading(false, isSave = true)
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

    private fun deleteDOPengiriman(noDo: Int) {
        isLoading(true)
        viewModel.deleteDOPengiriman(noDo).observe(viewLifecycleOwner, { response ->
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

    private fun getDateCalendar() {
        val cal = Calendar.getInstance()
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        years = cal.get(Calendar.YEAR)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val saveYears = year
        val savedMonth = month + 1
        val savedDay = dayOfMonth
        val date = "$savedDay-$savedMonth-$saveYears"
        val formatedDate = formateDate("d-MM-yyyy", date, "dd-MMMM-yyyy")
        binding.edtTanggalDo.setText(formatedDate)
    }

    private fun formateDate(pattern: String, date: String, format: String): String {
        var formattedTime = ""
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        try {
            val parseDate = sdf.parse(date)
            formattedTime = SimpleDateFormat(format).format(parseDate)
        } catch (e: ParseException) {
            Log.e(TAG, "formateDate: ${e.printStackTrace()}")
        }
        return formattedTime
    }

    private fun isLoading(status: Boolean, isSave: Boolean = false) {
        with(binding) {
            if (isSave) {
                if (status) {
                    btnSave.visibility = View.GONE
                    progressSave.visibility = View.VISIBLE
                } else {
                    btnSave.visibility = View.VISIBLE
                    progressSave.visibility = View.GONE
                }
            } else {
                if (status) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun checkValue(
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

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_do_pengiriman_title))
            .setMessage(getString(R.string.delete_do_pengiriman_barang_body))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                deleteDOPengiriman(noDOParams)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (request == REQUEST_EDIT) {
            inflater.inflate(R.menu.delete, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.delete -> showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle(titleToolbar: String, btnDescription: String) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.include.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = titleToolbar
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        binding.btnSave.text = btnDescription
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}