package com.receiptit.network.service

import com.receiptit.network.model.SimpleResponse
import com.receiptit.network.model.image.ReceiptImageCreaetResponse
import com.receiptit.network.model.image.ReceiptImageRetrieveResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ImageApi {

    @Multipart
    @POST("/receipt/{receiptId}/image")
    fun createReceiptImage(@Part file: MultipartBody.Part,
                           @Path("receiptId") receiptId: Int ):
            Call<ReceiptImageCreaetResponse>

    @GET("/receipt/{receiptId}/image")
    fun getReceiptImage(@Path("receiptId") receiptId: Int): Call<ReceiptImageRetrieveResponse>

    @DELETE("/receipt/{receiptId}/image")
    fun deleteReceiptImage(@Path("receiptId") receiptId: Int): Call<SimpleResponse>
}