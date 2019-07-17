package com.receiptit.network.model.user

import java.util.*

data class UserInfo(var user_id: Int, var password: String, var email: String, var firstName: String,
                    var lastName: String, var createdAt: String, var updatedAt: String)