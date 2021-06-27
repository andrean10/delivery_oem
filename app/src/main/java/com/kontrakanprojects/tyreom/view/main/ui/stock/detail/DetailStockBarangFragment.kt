package com.kontrakanprojects.tyreom.view.main.ui.stock.detail

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
import com.kontrakanprojects.tyreom.databinding.FragmentDetailStockBarangBinding
import com.kontrakanprojects.tyreom.model.stock_barang.ResultsStockBarang
import com.kontrakanprojects.tyreom.network.NetworkConnection
import com.kontrakanprojects.tyreom.utils.hideKeyboard
import com.kontrakanprojects.tyreom.utils.showMessage
import com.kontrakanprojects.tyreom.view.main.ui.stock.StockViewModel
import www.sanju.motiontoast.MotionToast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DetailStockBarangFragment : Fragment(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentDetailStockBarangBinding? = null
    private val binding get() = _binding!!

    //    private val viewModel by activityViewModels<StockViewModel>()
    private lateinit var viewModel: StockViewModel

    private var codeSizeParams = 0
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

    private val TAG = DetailStockBarangFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        _binding = FragmentDetailStockBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailStockBarangFragmentArgs.fromBundle(arguments as Bundle)
        request = args.request
        codeSizeParams = args.codeSize

        lateinit var titleToolbar: String
        lateinit var btnDescription: String
        when (request) {
            REQUEST_ADD -> {
                titleToolbar = getString(R.string.tambah_stok_barang)
                btnDescription = getString(R.string.save)
            }
            REQUEST_EDIT -> {
                titleToolbar = getString(R.string.edit_stok_barang)
                btnDescription = getString(R.string.update)
                isEdit = true
                binding.tiCodeSize.visibility = View.GONE // code size di hilangkan
                observeDetailStokBarang(codeSizeParams)
            }
        }

        setToolbarTitle(titleToolbar, btnDescription)
        watcherQuery()
        networkConnection()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edt_tanggal -> {
                getDateCalendar()
                DatePickerDialog(
                    requireContext(),
                    this@DetailStockBarangFragment,
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
                    edtCodeSize.setOnClickListener(this@DetailStockBarangFragment)
                    edtQuantity.setOnClickListener(this@DetailStockBarangFragment)
                    edtWeekly.setOnClickListener(this@DetailStockBarangFragment)
                    edtTanggal.setOnClickListener(this@DetailStockBarangFragment)
                    btnSave.setOnClickListener(this@DetailStockBarangFragment)
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

    private fun observeDetailStokBarang(codeSize: Int) {
        isLoading(true)
        viewModel.getDetailStockBarang(codeSize).observe(viewLifecycleOwner, { response ->
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

    private fun prepareEdit(result: ResultsStockBarang) {
        with(binding) {
            edtCodeSize.setText(result.codeSize.toString())
            edtQuantity.setText(result.quantity)
            edtWeekly.setText(result.weekly.toString())

            // format date ke local indonesia
            val formatDate = formateDate(
                "yyyy-MM-dd", result.tanggal!!,
                "dd-MMMM-yyyy"
            )
            edtTanggal.setText(formatDate)
        }
    }

    private fun save() {
        with(binding) {
            val codeSize = edtCodeSize.text?.trim().toString()
            val quantity = edtQuantity.text?.trim().toString()
            val weekly = edtWeekly.text?.trim().toString()
            val tanggal = edtTanggal.text?.trim().toString()
            // format inputan tanggal pekerjaan
            val formatedDate = formateDate("dd-MMMM-yyyy", tanggal, "yyyy-MM-dd")

            checkValue(codeSize, tiCodeSize, NOT_NULL)
            checkValue(quantity, tiQuantity, NOT_NULL)
            checkValue(weekly, tiWeekly, NOT_NULL)
            checkValue(tanggal, tiTanggal, NOT_NULL)

            if (valid) {
                val params = hashMapOf(
                    "code_size" to codeSize,
                    "quantity" to quantity,
                    "weekly" to weekly,
                    "tanggal" to formatedDate
                )

                if (!isEdit) {
                    addStokBarang(params)
                } else {
                    editStokBarang(params)
                }
            }
        }
    }

    private fun watcherQuery() {
        with(binding) {
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

            edtWeekly.addTextChangedListener(object : TextWatcher {
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
                        tiWeekly.isErrorEnabled = false
                    } else {
                        tiWeekly.isErrorEnabled = true
                        tiWeekly.error = NOT_NULL
                    }
                }
            })
        }
    }

    private fun addStokBarang(newData: HashMap<String, String>) {
        isLoading(true, isSave = true)
        viewModel.addStockBarang(newData).observe(viewLifecycleOwner, { response ->
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

    private fun editStokBarang(newData: HashMap<String, String>) {
        isLoading(true, isSave = true)
        viewModel.editStockBarang(codeSizeParams, newData).observe(viewLifecycleOwner, { response ->
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

    private fun deleteStockBarang(codeSize: Int) {
        isLoading(true)
        viewModel.deleteStockBarang(codeSize).observe(viewLifecycleOwner, { response ->
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
        binding.edtTanggal.setText(formatedDate)
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
        alertDialogBuilder.setTitle(getString(R.string.delete_stok_barang_title))
            .setMessage(getString(R.string.delete_stok_barang_body))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                deleteStockBarang(codeSizeParams)
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