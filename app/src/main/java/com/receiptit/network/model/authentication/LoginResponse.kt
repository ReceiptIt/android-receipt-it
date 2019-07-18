package com.receiptit.network.model.authentication

import com.receiptit.network.model.user.UserInfo
import java.io.Serializable

data class LoginResponse (var result: String, var message: String, var auth: Boolean,
                          var authToken: String, var userInfo: UserInfo):Serializable