package com.kontrakanprojects.tyreom.model.admin

import com.google.gson.annotations.SerializedName

data class ResponseAdmin(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultAdmin>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class ResultAdmin(

    @field:SerializedName("nik")
    val username: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("id_admin")
    val idAdmin: Int? = null,

    @field:SerializedName("id_role")
    val idRole: Int? = null,

    @field:SerializedName("nama_admin")
    val namaAdmin: String? = null
)
