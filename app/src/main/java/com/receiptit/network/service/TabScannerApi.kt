package com.receiptit.network.service

import com.receiptit.network.model.tabScanner.TabScannerGetReceiptProcessedResponse
import com.receiptit.network.model.tabScanner.TabScannerUploadReceiptImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface TabScannerApi{

    @Multipart
    @POST("/SsByinGW7h7nveWtYyfFKBH52LK6kTTjxORBffkVBngPiibL7efacf5QrkysM9RR/process")
    fun uploadReceiptImage(@Part file: MultipartBody.Part): Call<TabScannerUploadReceiptImageResponse>

    @GET("/SsByinGW7h7nveWtYyfFKBH52LK6kTTjxORBffkVBngPiibL7efacf5QrkysM9RR/result/{token}")
    fun getReceiptImageProcessed(@Path("token") token: String) : Call<TabScannerGetReceiptProcessedResponse>
}