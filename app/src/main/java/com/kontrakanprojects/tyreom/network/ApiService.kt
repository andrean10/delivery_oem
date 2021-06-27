package com.kontrakanprojects.tyreom.network

import com.kontrakanprojects.tyreom.model.admin.ResponseAdmin
import com.kontrakanprojects.tyreom.model.auth.ResponseAuth
import com.kontrakanprojects.tyreom.model.do_pengiriman.ResponseDOPengiriman
import com.kontrakanprojects.tyreom.model.estimasi_pengiriman.ResponseEstimasiPengiriman
import com.kontrakanprojects.tyreom.model.pegawai.ResponseKaryawan
import com.kontrakanprojects.tyreom.model.stock_barang.ResponseStockBarang
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /** ROUTE AUTH **/

    @FormUrlEncoded
    @POST("auth/login")
    fun login(@FieldMap params: HashMap<String, String>): Call<ResponseAuth>

    @GET("admin/{id}")
    fun detailAdmin(@Path("id") idAdmin: Int): Call<ResponseAdmin>

    @FormUrlEncoded
    @PATCH("admin/{id}")
    fun editAdmin(
        @Path("id") idAdmin: Int,
        @FieldMap params: HashMap<String, String>
    ): Call<ResponseAdmin>

    /** PIC OE Setting **/

    @GET("pic-oe")
    fun picOE(): Call<ResponseKaryawan>

    @GET("pic-oe/{nik}")
    fun detailPicOE(@Path("nik") nik: Int): Call<ResponseKaryawan>

    @FormUrlEncoded
    @POST("pic-oe")
    fun addPicOE(@FieldMap params: HashMap<String, String>): Call<ResponseKaryawan>

    @FormUrlEncoded
    @PATCH("pic-oe/{nik}")
    fun editPicOE(
        @Path("nik") nik: Int,
        @FieldMap params: HashMap<String, String>
    ): Call<ResponseKaryawan>

    @DELETE("pic-oe/{nik}")
    fun deletePicOE(@Path("nik") nik: Int): Call<ResponseKaryawan>

    /** PIC Stock Control **/

    @GET("pic-sto")
    fun picSto(): Call<ResponseKaryawan>

    @GET("pic-sto/{nik}")
    fun detailPicSto(@Path("nik") nik: Int): Call<ResponseKaryawan>

    @FormUrlEncoded
    @POST("pic-sto")
    fun addPicSto(@FieldMap params: HashMap<String, String>): Call<ResponseKaryawan>

    @FormUrlEncoded
    @PATCH("pic-sto/{nik}")
    fun editPicSto(
        @Path("nik") nik: Int,
        @FieldMap params: HashMap<String, String>
    ): Call<ResponseKaryawan>

    @DELETE("pic-sto/{nik}")
    fun deletePicSto(@Path("nik") nik: Int): Call<ResponseKaryawan>

    /** PIC Prepare Suplay **/

    @GET("pic-pre")
    fun picPre(): Call<ResponseKaryawan>

    @GET("pic-pre/{nik}")
    fun detailPicPre(@Path("nik") nik: Int): Call<ResponseKaryawan>

    @FormUrlEncoded
    @POST("pic-pre")
    fun addPicPre(@FieldMap params: HashMap<String, String>): Call<ResponseKaryawan>

    @FormUrlEncoded
    @PATCH("pic-pre/{nik}")
    fun editPicPre(
        @Path("nik") nik: Int,
        @FieldMap params: HashMap<String, String>
    ): Call<ResponseKaryawan>

    @DELETE("pic-pre/{nik}")
    fun deletePicPre(@Path("nik") nik: Int): Call<ResponseKaryawan>

    /** Stock Barang **/

    @GET("stock-barang")
    fun stockBarang(): Call<ResponseStockBarang>

    @GET("stock-barang/{code_size}")
    fun detailStockBarang(@Path("code_size") codeSize: Int): Call<ResponseStockBarang>

    @FormUrlEncoded
    @POST("stock-barang")
    fun addStockBarang(@FieldMap params: HashMap<String, String>): Call<ResponseStockBarang>

    @FormUrlEncoded
    @PUT("stock-barang/{code_size}")
    fun editStockBarang(
        @Path("code_size") codeSize: Int,
        @FieldMap params: HashMap<String, String>
    ): Call<ResponseStockBarang>

    @DELETE("stock-barang/{code_size}")
    fun deleteStockBarang(@Path("code_size") codeSize: Int): Call<ResponseStockBarang>

    /** DO Pengiriman **/

    @GET("do-pengiriman")
    fun doPengiriman(): Call<ResponseDOPengiriman>

    @GET("do-pengiriman/{no_do}")
    fun detailDOPengiriman(@Path("no_do") noDO: Int): Call<ResponseDOPengiriman>

    @FormUrlEncoded
    @POST("do-pengiriman")
    fun addDOPengiriman(@FieldMap params: HashMap<String, String>): Call<ResponseDOPengiriman>

    @FormUrlEncoded
    @PUT("do-pengiriman/{no_do}")
    fun editDOPengiriman(
        @Path("no_do") noDO: Int,
        @FieldMap params: HashMap<String, String>
    ): Call<ResponseDOPengiriman>

    @DELETE("do-pengiriman/{no_do}")
    fun deleteDOPengiriman(@Path("no_do") noDO: Int): Call<ResponseDOPengiriman>

    /** Estimasi Pengiriman **/

    @GET("estimasi-pengiriman")
    fun estimasiPengiriman(): Call<ResponseEstimasiPengiriman>

    @GET("estimasi-pengiriman/{code_size}")
    fun detailEstimasiPengiriman(@Path("code_size") codeSize: Int): Call<ResponseEstimasiPengiriman>

    @FormUrlEncoded
    @POST("estimasi-pengiriman")
    fun addEstimasiPengiriman(@FieldMap params: HashMap<String, String>): Call<ResponseEstimasiPengiriman>

    @FormUrlEncoded
    @PUT("estimasi-pengiriman/{code_size}")
    fun editEstimasiPengiriman(
        @Path("code_size") codeSize: Int,
        @FieldMap params: HashMap<String, String>
    ): Call<ResponseEstimasiPengiriman>

    @DELETE("estimasi-pengiriman/{code_size}")
    fun deleteEstimasiPengiriman(@Path("code_size") codeSize: Int): Call<ResponseEstimasiPengiriman>
}