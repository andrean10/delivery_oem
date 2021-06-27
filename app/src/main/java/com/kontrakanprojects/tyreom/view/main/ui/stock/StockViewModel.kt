package com.kontrakanprojects.tyreom.view.main.ui.stock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.tyreom.model.stock_barang.ResponseStockBarang
import com.kontrakanprojects.tyreom.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockViewModel : ViewModel() {

    private var _stockBarang: MutableLiveData<ResponseStockBarang>? = null

    private val TAG = StockViewModel::class.simpleName

    fun getStockBarang(): LiveData<ResponseStockBarang> {
        _stockBarang = MutableLiveData()
        stockBarang()
        return _stockBarang as MutableLiveData<ResponseStockBarang>
    }

    fun getDetailStockBarang(codeSize: Int): LiveData<ResponseStockBarang> {
        _stockBarang = MutableLiveData()
        stockBarang(codeSize)
        return _stockBarang as MutableLiveData<ResponseStockBarang>
    }

    fun addStockBarang(params: HashMap<String, String>): LiveData<ResponseStockBarang> {
        _stockBarang = MutableLiveData()
        stockBarang(params = params)
        return _stockBarang as MutableLiveData<ResponseStockBarang>
    }

    fun editStockBarang(
        codeSize: Int,
        params: HashMap<String, String>
    ): LiveData<ResponseStockBarang> {
        _stockBarang = MutableLiveData()
        stockBarang(codeSize, params)
        return _stockBarang as MutableLiveData<ResponseStockBarang>
    }

    fun deleteStockBarang(codeSize: Int): LiveData<ResponseStockBarang> {
        _stockBarang = MutableLiveData()
        stockBarang(codeSize, isDelete = true)
        return _stockBarang as MutableLiveData<ResponseStockBarang>
    }

    private fun stockBarang(
        codeSize: Int? = null,
        params: HashMap<String, String>? = null,
        isDelete: Boolean = false
    ) {
        val client = if (codeSize != null) {
            if (params != null) {
                ApiConfig.getApiService().editStockBarang(codeSize, params)
            } else {
                if (isDelete) {
                    ApiConfig.getApiService().deleteStockBarang(codeSize)
                } else {
                    ApiConfig.getApiService().detailStockBarang(codeSize)
                }
            }
        } else {
            if (params != null) {
                ApiConfig.getApiService().addStockBarang(params)
            } else {
                ApiConfig.getApiService().stockBarang()
            }
        }

        client.enqueue(object : Callback<ResponseStockBarang> {
            override fun onResponse(
                call: Call<ResponseStockBarang>,
                response: Response<ResponseStockBarang>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _stockBarang?.postValue(result!!)

                    Log.d(TAG, "onResponse: $result")
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseStockBarang =
                        ResponseStockBarang(message = message, status = status)
                    _stockBarang?.postValue(responseStockBarang)

                    Log.e(TAG, "onFailure: $responseStockBarang")
                }
            }

            override fun onFailure(call: Call<ResponseStockBarang>, t: Throwable) {
                _stockBarang?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}