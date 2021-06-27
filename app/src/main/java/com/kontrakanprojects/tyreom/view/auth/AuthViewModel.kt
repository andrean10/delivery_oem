package com.kontrakanprojects.tyreom.view.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.tyreom.model.auth.ResponseAuth
import com.kontrakanprojects.tyreom.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private var _login: MutableLiveData<ResponseAuth>? = null

    private val TAG = AuthViewModel::class.simpleName

    fun login(params: HashMap<String, String>): LiveData<ResponseAuth> {
        _login = MutableLiveData<ResponseAuth>()
        getLogin(params)
        return _login as MutableLiveData<ResponseAuth>
    }

    private fun getLogin(params: HashMap<String, String>) {
        val client = ApiConfig.getApiService().login(params)
        client.enqueue(object : Callback<ResponseAuth> {
            override fun onResponse(call: Call<ResponseAuth>, response: Response<ResponseAuth>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _login?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseAuth = ResponseAuth(message = message, status = status)
                    _login?.postValue(responseAuth)

                    Log.e(TAG, "onFailure: $responseAuth")
                }
            }

            override fun onFailure(call: Call<ResponseAuth>, t: Throwable) {
                _login?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}