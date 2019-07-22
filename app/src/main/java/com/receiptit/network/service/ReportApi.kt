package com.receiptit.network.service

import com.receiptit.network.model.report.ReportGetProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApi {


    @GET("/report/product")
    fun getProductListDuringGivenPeriod(@Query("userId") userId: Int, @Query("startDate") startDate: String,
                                        @Query("endDate") endDate: String): Call<ReportGetProductResponse>

}