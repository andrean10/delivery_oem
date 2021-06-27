package com.kontrakanprojects.tyreom.view.main.ui.estimasi_pengiriman

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.tyreom.model.estimasi_pengiriman.ResponseEstimasiPengiriman
import com.kontrakanprojects.tyreom.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EstimasiPengirimanViewModel : ViewModel() {

    private var _estimasiPengiriman: MutableLiveData<ResponseEstimasiPengiriman>? = null

    private val TAG = EstimasiPengirimanViewModel::class.simpleName

    fun getEstimasiPengiriman(): LiveData<ResponseEstimasiPengiriman> {
        _estimasiPengiriman = MutableLiveData()
        estimasiPengiriman()
        return _estimasiPengiriman as MutableLiveData<ResponseEstimasiPengiriman>
    }

    fun getDetailEstimasiPengiriman(code_size: Int): LiveData<ResponseEstimasiPengiriman> {
        _estimasiPengiriman = MutableLiveData()
        estimasiPengiriman(code_size)
        return _estimasiPengiriman as MutableLiveData<ResponseEstimasiPengiriman>
    }

    fun addEstimasiPengiriman(params: HashMap<String, String>): LiveData<ResponseEstimasiPengiriman> {
        _estimasiPengiriman = MutableLiveData()
        estimasiPengiriman(params = params)
        return _estimasiPengiriman as MutableLiveData<ResponseEstimasiPengiriman>
    }

    fun editEstimasiPengiriman(
        code_size: Int,
        params: HashMap<String, String>
    ): LiveData<ResponseEstimasiPengiriman> {
        _estimasiPengiriman = MutableLiveData()
        estimasiPengiriman(code_size, params)
        return _estimasiPengiriman as MutableLiveData<ResponseEstimasiPengiriman>
    }

    fun deleteEstimasiPengiriman(code_size: Int): LiveData<ResponseEstimasiPengiriman> {
        _estimasiPengiriman = MutableLiveData()
        estimasiPengiriman(code_size, isDelete = true)
        return _estimasiPengiriman as MutableLiveData<ResponseEstimasiPengiriman>
    }

    private fun estimasiPengiriman(
        code_size: Int? = null,
        params: HashMap<String, String>? = null,
        isDelete: Boolean = false
    ) {
        val client = if (code_size != null) {
            if (params != null) {
                ApiConfig.getApiService().editEstimasiPengiriman(code_size, params)
            } else {
                if (isDelete) {
                    ApiConfig.getApiService().deleteEstimasiPengiriman(code_size)
                } else {
                    ApiConfig.getApiService().detailEstimasiPengiriman(code_size)
                }
            }
        } else {
            if (params != null) {
                ApiConfig.getApiService().addEstimasiPengiriman(params)
            } else {
                ApiConfig.getApiService().estimasiPengiriman()
            }
        }

        client.enqueue(object : Callback<ResponseEstimasiPengiriman> {
            override fun onResponse(
                call: Call<ResponseEstimasiPengiriman>,
                response: Response<ResponseEstimasiPengiriman>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _estimasiPengiriman?.postValue(result!!)

                    Log.d(TAG, "onResponse: $result")
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseEstimasiPengiriman =
                        ResponseEstimasiPengiriman(message = message, status = status)
                    _estimasiPengiriman?.postValue(responseEstimasiPengiriman)

                    Log.e(TAG, "onFailure: $responseEstimasiPengiriman")
                }
            }

            override fun onFailure(call: Call<ResponseEstimasiPengiriman>, t: Throwable) {
                _estimasiPengiriman?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}