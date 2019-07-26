package com.receiptit.network

import android.text.TextUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceGenerator {

    private const val API_BASE_URL = NetworkConstant.BASE_URL
    private const val IMAGE_PROCESSOR_URL = NetworkConstant.IMAGE_PROCESSOR_URL
    private var authToken: String? = null

    private val httpClient = OkHttpClient.Builder()

    private val builder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private var retrofit = builder.build()

    private val imageProcessorBuilder = Retrofit.Builder()
        .baseUrl(IMAGE_PROCESSOR_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private var imageProcessorRetrofit = imageProcessorBuilder.build()

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, authToken)
    }

    private fun <S> createService(serviceClass: Class<S>, authToken: String?): S {
        if (!TextUtils.isEmpty(authToken)) {
            val interceptor = authToken?.let { AuthenticationInterceptor(it) }

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)

                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }
        return retrofit.create(serviceClass)
    }

    fun <S> createAuthenticationService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

    fun <S> createImageProcessorService(serviceClass: Class<S>): S {
        if (!TextUtils.isEmpty(authToken)) {
            val interceptor = authToken?.let { AuthenticationInterceptor(it) }

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)

                imageProcessorBuilder.client(httpClient.build())
                imageProcessorRetrofit = imageProcessorBuilder.build()
            }
        }
        return imageProcessorRetrofit.create(serviceClass)
    }

    fun storeAuthToken(token: String) {
        authToken = token
    }
}