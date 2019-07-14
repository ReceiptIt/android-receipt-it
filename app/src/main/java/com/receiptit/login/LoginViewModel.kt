package com.receiptit.login

import androidx.lifecycle.*
import com.receiptit.singleLiveEvent.SingleLiveEvent

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

    /***
     * https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
     */
    private val singleIsUserInfoValidEvent = SingleLiveEvent<Any>()
    val isUserInfoValidEvent : LiveData<Any>
    get() = singleIsUserInfoValidEvent

    private val singleUserLoginSuccessEvent = SingleLiveEvent<Any>()
    val userLoginSuccessEvent : LiveData<Any>
    get() = singleUserLoginSuccessEvent

    private val singleUserLoginFailEvent = SingleLiveEvent<Any>()
    val userLoginFailEvent : LiveData<Any>
        get() = singleUserLoginFailEvent


    private fun isUserInfoValid(): Boolean {
        return !(password.value.toString() == "" || username.value.toString() == "")
    }

    private fun isRememberMe(): Boolean {
        return rememberMe.value!!
    }

    private fun rememberUser() {
        if (isRememberMe()) {
            storeUserInfoIntoCache()
        }
    }

    //TODO: store user info into cache
    private fun storeUserInfoIntoCache() {
    }

    //TODO: network request and progress bar
    private fun login() {
        rememberUser()
        //user login success
        singleUserLoginSuccessEvent.call()
        //user login fail
//        singleUserLoginFailEvent.call()
    }

    fun onClick() {
        if (!isUserInfoValid()) {
            singleIsUserInfoValidEvent.call()
        } else {
            login()
        }
    }


}