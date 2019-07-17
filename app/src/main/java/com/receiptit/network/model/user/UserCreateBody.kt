package com.receiptit.network.model.user

data class UserCreateBody (var password: String, var email: String, var firstName: String,
                           var lastName: String)