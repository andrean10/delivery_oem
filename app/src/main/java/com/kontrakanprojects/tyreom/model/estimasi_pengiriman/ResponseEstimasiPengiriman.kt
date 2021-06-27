package com.kontrakanprojects.tyreom.model.estimasi_pengiriman

import com.google.gson.annotations.SerializedName

data class ResponseEstimasiPengiriman(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsEstimasiPengiriman>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class ResultsEstimasiPengiriman(

    @field:SerializedName("code_size")
    val codeSize: Int? = null,

    @field:SerializedName("quantity")
    val quantity: String? = null,

    @field:SerializedName("tanggal")
    val tanggal: String? = null
)
