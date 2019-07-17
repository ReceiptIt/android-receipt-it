package com.receiptit.network.model.authentication

data class LoginResponse (var result: String, var message: String, var auth: Boolean,
                          var authToken: String)