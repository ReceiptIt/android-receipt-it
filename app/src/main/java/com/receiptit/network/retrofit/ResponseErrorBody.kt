package com.receiptit.network.retrofit

import org.json.JSONObject

class ResponseErrorBody(json: String): JSONObject(json) {
    fun getErrorMessage(): String {
        return this.optString("message")
    }
}