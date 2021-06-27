package com.kontrakanprojects.tyreom.view.main.ui.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.tyreom.model.admin.ResponseAdmin
import com.kontrakanprojects.tyreom.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAdminViewModel : ViewModel() {

    private var _admin: MutableLiveData<ResponseAdmin>? = null

    private val TAG = DetailAdminViewModel::class.simpleName

    fun getDetailAdmin(idAdmin: Int): LiveData<ResponseAdmin> {
        _admin = MutableLiveData<ResponseAdmin>()
        admin(idAdmin)
        return _admin as MutableLiveData<ResponseAdmin>
    }

    fun editAdmin(idAdmin: Int, params: HashMap<String, String>): LiveData<ResponseAdmin> {
        _admin = MutableLiveData<ResponseAdmin>()
        admin(idAdmin, params)
        return _admin as MutableLiveData<ResponseAdmin>
    }

    private fun admin(idAdmin: Int, params: HashMap<String, String>? = null) {
        val client = if (params != null) {
            ApiConfig.getApiService().editAdmin(idAdmin, params)
        } else {
            ApiConfig.getApiService().detailAdmin(idAdmin)
        }

        client.enqueue(object : Callback<ResponseAdmin> {
            override fun onResponse(
                call: Call<ResponseAdmin>,
                response: Response<ResponseAdmin>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _admin?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseAdmin = ResponseAdmin(message = message, status = status)
                    _admin?.postValue(responseAdmin)

                    Log.e(TAG, "onFailure: $responseAdmin")
                }
            }

            override fun onFailure(call: Call<ResponseAdmin>, t: Throwable) {
                _admin?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}