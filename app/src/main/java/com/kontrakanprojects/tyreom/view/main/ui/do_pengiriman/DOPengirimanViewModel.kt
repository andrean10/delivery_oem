package com.kontrakanprojects.tyreom.view.main.ui.do_pengiriman

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.tyreom.model.do_pengiriman.ResponseDOPengiriman
import com.kontrakanprojects.tyreom.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DOPengirimanViewModel : ViewModel() {

    private var _doPengiriman: MutableLiveData<ResponseDOPengiriman>? = null

    private val TAG = DOPengirimanViewModel::class.simpleName

    fun getDOPengiriman(): LiveData<ResponseDOPengiriman> {
        _doPengiriman = MutableLiveData()
        doPengiriman()
        return _doPengiriman as MutableLiveData<ResponseDOPengiriman>
    }

    fun getDetailDOPengiriman(noDO: Int): LiveData<ResponseDOPengiriman> {
        _doPengiriman = MutableLiveData()
        doPengiriman(noDO)
        return _doPengiriman as MutableLiveData<ResponseDOPengiriman>
    }

    fun addDOPengiriman(params: HashMap<String, String>): LiveData<ResponseDOPengiriman> {
        _doPengiriman = MutableLiveData()
        doPengiriman(params = params)
        return _doPengiriman as MutableLiveData<ResponseDOPengiriman>
    }

    fun editDOPengiriman(
        noDO: Int,
        params: HashMap<String, String>
    ): LiveData<ResponseDOPengiriman> {
        _doPengiriman = MutableLiveData()
        doPengiriman(noDO, params)
        return _doPengiriman as MutableLiveData<ResponseDOPengiriman>
    }

    fun deleteDOPengiriman(noDO: Int): LiveData<ResponseDOPengiriman> {
        _doPengiriman = MutableLiveData()
        doPengiriman(noDO, isDelete = true)
        return _doPengiriman as MutableLiveData<ResponseDOPengiriman>
    }

    private fun doPengiriman(
        noDO: Int? = null,
        params: HashMap<String, String>? = null,
        isDelete: Boolean = false
    ) {
        val client = if (noDO != null) {
            if (params != null) {
                ApiConfig.getApiService().editDOPengiriman(noDO, params)
            } else {
                if (isDelete) {
                    ApiConfig.getApiService().deleteDOPengiriman(noDO)
                } else {
                    ApiConfig.getApiService().detailDOPengiriman(noDO)
                }
            }
        } else {
            if (params != null) {
                ApiConfig.getApiService().addDOPengiriman(params)
            } else {
                ApiConfig.getApiService().doPengiriman()
            }
        }

        client.enqueue(object : Callback<ResponseDOPengiriman> {
            override fun onResponse(
                call: Call<ResponseDOPengiriman>,
                response: Response<ResponseDOPengiriman>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _doPengiriman?.postValue(result!!)

                    Log.d(TAG, "onResponse: $result")
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseDOPengiriman =
                        ResponseDOPengiriman(message = message, status = status)
                    _doPengiriman?.postValue(responseDOPengiriman)

                    Log.e(TAG, "onFailure: $responseDOPengiriman")
                }
            }

            override fun onFailure(call: Call<ResponseDOPengiriman>, t: Throwable) {
                _doPengiriman?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}