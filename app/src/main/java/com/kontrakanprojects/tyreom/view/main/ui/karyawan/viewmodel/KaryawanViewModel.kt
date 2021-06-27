package com.kontrakanprojects.tyreom.view.main.ui.karyawan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.tyreom.model.pegawai.ResponseKaryawan
import com.kontrakanprojects.tyreom.network.ApiConfig
import com.kontrakanprojects.tyreom.view.main.ui.karyawan.listkaryawan.ListKaryawanFragment
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KaryawanViewModel : ViewModel() {

    private var _karyawan: MutableLiveData<ResponseKaryawan>? = null

    private val TAG = KaryawanViewModel::class.simpleName

    fun getKaryawan(role: Int): LiveData<ResponseKaryawan> {
        _karyawan = MutableLiveData<ResponseKaryawan>()
        karyawan(role)
        return _karyawan as MutableLiveData<ResponseKaryawan>
    }

    fun getDetailKaryawan(role: Int, nik: Int): LiveData<ResponseKaryawan> {
        _karyawan = MutableLiveData<ResponseKaryawan>()
        karyawan(role, nik)
        return _karyawan as MutableLiveData<ResponseKaryawan>
    }

    fun addKaryawan(role: Int, params: HashMap<String, String>): LiveData<ResponseKaryawan> {
        _karyawan = MutableLiveData<ResponseKaryawan>()
        karyawan(role, params = params)
        return _karyawan as MutableLiveData<ResponseKaryawan>
    }

    fun editKaryawan(
        role: Int,
        nik: Int,
        params: HashMap<String, String>
    ): LiveData<ResponseKaryawan> {
        _karyawan = MutableLiveData<ResponseKaryawan>()
        karyawan(role, nik, params)
        return _karyawan as MutableLiveData<ResponseKaryawan>
    }

    fun deleteKaryawan(role: Int, nik: Int): LiveData<ResponseKaryawan> {
        _karyawan = MutableLiveData<ResponseKaryawan>()
        karyawan(role, nik, isDelete = true)
        return _karyawan as MutableLiveData<ResponseKaryawan>
    }

    // perbaiki untuk api config hapus karyawan
    private fun karyawan(
        role: Int,
        nik: Int? = null,
        params: HashMap<String, String>? = null,
        isDelete: Boolean = false
    ) {
        val client = when (role) {
            ListKaryawanFragment.PIC_STO -> {
                if (nik != null) {
                    if (params != null) {
                        ApiConfig.getApiService().editPicSto(nik, params)
                    } else {
                        if (isDelete) {
                            ApiConfig.getApiService().deletePicSto(nik)
                        } else {
                            ApiConfig.getApiService().detailPicSto(nik) // detail
                        }
                    }
                } else {
                    if (params != null) {
                        ApiConfig.getApiService().addPicSto(params) // add
                    } else {
                        ApiConfig.getApiService().picSto() // list
                    }
                }
            }
            ListKaryawanFragment.PIC_PRE -> {
                if (nik != null) {
                    if (params != null) {
                        ApiConfig.getApiService().editPicPre(nik, params)
                    } else {
                        if (isDelete) {
                            ApiConfig.getApiService().deletePicPre(nik)
                        } else {
                            ApiConfig.getApiService().detailPicPre(nik) // detail
                        }
                    }
                } else {
                    if (params != null) {
                        ApiConfig.getApiService().addPicPre(params) // add
                    } else {
                        ApiConfig.getApiService().picPre() // list
                    }
                }
            }
            ListKaryawanFragment.PIC_OE -> {
                if (nik != null) {
                    if (params != null) {
                        ApiConfig.getApiService().editPicOE(nik, params)
                    } else {
                        if (isDelete) {
                            ApiConfig.getApiService().deletePicOE(nik)
                        } else {
                            ApiConfig.getApiService().detailPicOE(nik) // detail
                        }
                    }
                } else {
                    if (params != null) {
                        ApiConfig.getApiService().addPicOE(params) // add
                    } else {
                        ApiConfig.getApiService().picOE() // list
                    }
                }
            }
            else -> null
        }

        client?.enqueue(object : Callback<ResponseKaryawan> {
            override fun onResponse(
                call: Call<ResponseKaryawan>,
                response: Response<ResponseKaryawan>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _karyawan?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseKaryawan = ResponseKaryawan(message = message, status = status)
                    _karyawan?.postValue(responseKaryawan)

                    Log.e(TAG, "onFailure: $responseKaryawan")
                }
            }

            override fun onFailure(call: Call<ResponseKaryawan>, t: Throwable) {
                _karyawan?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}