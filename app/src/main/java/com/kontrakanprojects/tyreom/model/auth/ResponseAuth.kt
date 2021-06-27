package com.kontrakanprojects.tyreom.model.auth

import com.google.gson.annotations.SerializedName

data class ResponseAuth(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: ResultAuth? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class ResultAuth(

    @field:SerializedName("nik")
    val nik: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("id_admin")
    val idAdmin: Int? = null,

    @field:SerializedName("id_role")
    val idRole: Int? = null,

    @field:SerializedName("nama_admin")
    val namaAdmin: String? = null,

    @field:SerializedName("nama_karyawan")
    val namaKaryawan: String? = null
)
