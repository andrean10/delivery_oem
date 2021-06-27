package com.kontrakanprojects.tyreom.model.do_pengiriman

import com.google.gson.annotations.SerializedName

data class ResponseDOPengiriman(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsDOPengiriman>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class ResultsDOPengiriman(

    @field:SerializedName("code_size")
    val codeSize: Int? = null,

    @field:SerializedName("quantity")
    val quantity: String? = null,

    @field:SerializedName("no_do")
    val noDo: Int? = null,

    @field:SerializedName("tanggal_do")
    val tanggalDo: String? = null
)