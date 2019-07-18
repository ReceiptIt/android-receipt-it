package com.receiptit.network.model.user

data class UserUpdateBody (var password: String? = null, var email: String? = null, var first_name: String? = null,
                           var last_name: String? = null)