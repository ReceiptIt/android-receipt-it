package com.receiptit.network.model.user

import java.util.*

data class UserInfo(var userId: Int, var password: String, var email: String, var firstName: String,
                    var lastName: String, var createdAt: String, var updatedAt: String)