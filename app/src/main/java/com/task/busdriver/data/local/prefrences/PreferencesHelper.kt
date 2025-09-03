package com.example.myapplication3.data.local.prefrences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class PreferencesHelper @Inject constructor(private val context: Context) {
    private val sharedPreferences : SharedPreferences by lazy{
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }
    fun putString(key : String, value : String){
        sharedPreferences.edit { putString(key, value) }
    }
    fun getString(key: String,default: String?) : String?{
       return sharedPreferences.getString(key,default)
    }
}