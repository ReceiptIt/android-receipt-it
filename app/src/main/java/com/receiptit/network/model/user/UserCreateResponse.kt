package com.receiptit.network.model.user

data class UserCreateResponse (var result: String, var message: String, var userInfo: UserInfo, var auth: String,
                               var authToken: String)