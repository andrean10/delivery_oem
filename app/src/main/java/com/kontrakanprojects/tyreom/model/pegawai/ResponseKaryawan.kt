package com.kontrakanprojects.tyreom.model.pegawai

import com.google.gson.annotations.SerializedName

data class ResponseKaryawan(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsKaryawan>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class ResultsKaryawan(

    @field:SerializedName("nik")
    val nik: Int? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("nama_karyawan")
    val namaKaryawan: String? = null,

    @field:SerializedName("id_role")
    val idRole: Int? = null
)
