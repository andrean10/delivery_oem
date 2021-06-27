package com.kontrakanprojects.tyreom.model.stock_barang

import com.google.gson.annotations.SerializedName

data class ResponseStockBarang(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsStockBarang>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class ResultsStockBarang(

    @field:SerializedName("code_size")
    val codeSize: Int? = null,

    @field:SerializedName("quantity")
    val quantity: String? = null,

    @field:SerializedName("tanggal")
    val tanggal: String? = null,

    @field:SerializedName("weekly")
    val weekly: Int? = null
)
