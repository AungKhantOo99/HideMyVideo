package com.ako.hidemyvideo.Helper

import android.content.Context
import com.ako.hidemyvideo.R

class SharePref(private val context: Context) {

    private val sharedPreferences = context
        .getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    private val editor = sharedPreferences.edit()


    fun setPassword(password: String) {
        editor.putString("password", password).apply()
    }

    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    fun setRecoverPhase(phase: String){
        editor.putString("phase", phase).apply()
    }

    fun getRecoverPhase():String?{
        return sharedPreferences.getString("phase", null)
    }
}