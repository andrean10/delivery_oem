package com.kontrakanprojects.tyreom.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import com.kontrakanprojects.tyreom.R
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

//fun View.snackbar(message: String) {
//    Snackbar.make(
//        this,
//        message,
//        Snackbar.LENGTH_LONG
//    ).also { snackbar ->
////        snackbar.setAction("OKE") {
////            snackbar.dismiss()
////        }
//
//        snackbar.view.apply {
//            setBackgroundColor()
//        }
//    }.show()
//}

fun showMessage(
    activity: Activity,
    title: String,
    message: String = "Cek Koneksi Internet Dan Coba Lagi!",
    style: String
) {
    MotionToast.darkColorToast(
        activity,
        title,
        message,
        style,
        MotionToast.GRAVITY_BOTTOM,
        MotionToast.LONG_DURATION,
        ResourcesCompat.getFont(activity, R.font.helvetica_regular)
    )
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun createPartFromString(descriptionString: String): RequestBody {
    return descriptionString.toRequestBody(MultipartBody.FORM)
}

fun formateDate(date: String, format: String, TAG: String): String {
    var formattedTime = ""
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    try {
        val parseDate = sdf.parse(date)
        formattedTime = SimpleDateFormat(format, Locale.getDefault()).format(parseDate)
    } catch (e: ParseException) {
        Log.e(TAG, "formateDate: ${e.printStackTrace()}")
    }
    return formattedTime
}

fun parseDateToddMMyyyy(time: String): String? {
    val inputPattern = "yyyy-MM-dd"
    val outputPattern = "dd-MM-yyyy"
    val inputFormat = SimpleDateFormat(inputPattern)
    val outputFormat = SimpleDateFormat(outputPattern)

    var str: String? = null
    try {
        val date = inputFormat.parse(time)
        str = outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return str
}