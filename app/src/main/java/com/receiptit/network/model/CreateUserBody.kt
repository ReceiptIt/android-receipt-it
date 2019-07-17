package com.receiptit.network.model

data class CreateUserBody (var resutl: String, var message: String, var userInfo: UserInfo,
                           var auth: String, var authToken: String)