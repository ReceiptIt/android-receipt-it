package com.receiptit.model

data class AuthenticationResponse (var result: String, var message: String, var auth: Boolean,
                                   var authToken: String)