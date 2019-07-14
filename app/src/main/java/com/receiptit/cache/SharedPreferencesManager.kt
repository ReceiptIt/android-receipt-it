package com.receiptit.cache

import android.content.Context

//TODO: how to use SharedPreferenfes in ViewModel
class SharedPreferencesManager(context: Context) {
    val PREF_FILENAME = "com.receiptit.prefs"
    val prefs = context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE)
}