package com.receiptit.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class LoginViewModel :ViewModel() {
    private var mutableUsername = MutableLiveData<String>().apply { value = "110" }
    private var mutablePassword = MutableLiveData<String>().apply { value = "" }
    private var mutableRememberMe = MutableLiveData<Boolean>().apply { value = false }

    private val username: LiveData<String> = mutableUsername
    private val password: LiveData<String> = mutablePassword
    private val rememberMe: LiveData<Boolean> = mutableRememberMe


    fun getUsername(): LiveData<String> {
        return username
    }

    fun getPassword(): LiveData<String> {
        return password
    }

    fun getRememberMe(): LiveData<Boolean> {
        return rememberMe
    }


//
//    fun setPassword(pass: String) {
//        password.value = pass
//    }
//
//
//    fun setEnablelogin(enableString: String) {
//        enableLogin.value = enableString
//    }
//
////    @Bindable
//    fun getUsername(): String {
//        return username.value.toString()
//    }
//
////    @Bindable
//    fun getPassword(): String {
//        return password.value.toString()
//    }
//
////    @Bindable
//    fun getEnableLogin(): String {
//        return enableLogin.value.toString()
//    }
//
////    @Bindable
//    fun getRememberMe(): Boolean? {
//        return rememberMe.value
//    }

//    fun onClick() {
////        if (username.value == null || password.value == null)
//
//            _enableLogin.postValue(getUsername().value)
////        else
////            enableLogin.postValue(  "Good to go")
//    }

}