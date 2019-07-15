package com.receiptit.model

data class LogoutResponse(var result: String, var message: String, var auth: Boolean, var authToken: String?)