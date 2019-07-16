package com.receiptit.network.retrofit

import android.content.Context
import retrofit2.Call
import retrofit2.Response
import java.net.HttpURLConnection


class RetrofitCallback<T>(private var listener: RetrofitCallbackListener<T>) : retrofit2.Callback<T> {

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        listener.onFailure(call, t)
    }

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        when {
            response?.let { isCallSuccess(it) }!! -> listener.onResponseSuccess(call, response)
//            isCallUnauthorized(response) -> listener.onResponseUnauthorized(call, response)
            else -> listener.onResponseError(call, response)
        }
    }

    private fun isCallSuccess(response: Response<T>): Boolean {
        val httpStatusCode = response.code()
        return httpStatusCode == HttpURLConnection.HTTP_OK
    }

//    private fun isCallUnauthorized(response: Response<T>): Boolean {
//        val httpStatusCode = response.code()
//        return httpStatusCode == HttpURLConnection.HTTP_UNAUTHORIZED
//    }

}