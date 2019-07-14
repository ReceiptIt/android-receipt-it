package com.receiptit.login

import androidx.lifecycle.*

/***
 * https://stackoverflow.com/questions/50876372/live-data-and-2-way-data-binding-custom-setter-not-being-called
 */

class LoginViewModel : ViewModel() {

    private val mutableUsername = MutableLiveData<String>().apply { value = "" }
    val username = MediatorLiveData<String>().apply {
        addSource(mutableUsername) { value ->
            setValue(value)
        }
    }.also { it.observeForever { /* empty */ } }

    private val mutablePassword = MutableLiveData<String>().apply { value = "" }
    val password = MediatorLiveData<String>().apply {
        addSource(mutablePassword) { value ->
            setValue(value)
        }
    }.also { it.observeForever { /* empty */ } }

    private val mutableRememberMe = MutableLiveData<Boolean>().apply { value = false }
    val rememberMe = MediatorLiveData<Boolean>().apply {
        addSource(mutableRememberMe) { value ->
            setValue(value)
        }
    }.also { it.observeForever { /* empty */ } }

    private val mutableEnableLogin = MutableLiveData<String>().apply { value = "fuck" }
    val enableLogin = MediatorLiveData<String>().apply {
        addSource(mutableEnableLogin) { value ->
            setValue(value)
        }
    }.also { it.observeForever { /* empty */ } }

    //
    fun isUserInfoValid(): Boolean {
        return !(password.value.toString() == "" || username.value.toString() == "")
    }

    fun isRememberMe(): Boolean {
        return rememberMe.value!!
    }
}