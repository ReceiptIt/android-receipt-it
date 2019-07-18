package com.receiptit.network.model.user

import java.io.Serializable

data class UserInfo(var user_id: Int, var password: String, var email: String, var first_name: String,
                    var last_name: String, var createdAt: String, var updatedAt: String): Serializable