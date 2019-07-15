package com.receiptit.model

data class LoginResponse (var result: String, var message: String, var auth: Boolean,
                          var authToken: String)