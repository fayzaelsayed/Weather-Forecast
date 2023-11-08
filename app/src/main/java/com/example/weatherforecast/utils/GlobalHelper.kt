package com.example.weatherforecast.utils

import android.content.Context
import android.content.SharedPreferences

class GlobalHelper(context: Context) {
    private  var ctx: Context
    private  var sharedPreferences: SharedPreferences
    var editSharedPreferences: SharedPreferences.Editor

    companion object{
        const val sharedPreferences_name = "my_pref"
    }

    init {
        this.ctx = context
        sharedPreferences = context.getSharedPreferences(sharedPreferences_name, Context.MODE_PRIVATE)
        editSharedPreferences = sharedPreferences.edit()

    }
    fun setSharedPreferences(name:String, value:String): Boolean{
        return try {
            editSharedPreferences.putString(name,value)
            editSharedPreferences.commit()
            true
        }catch (e:Exception){
            false
        }
    }
    fun setBooleanSharedPreferences(name:String, value:Boolean): Boolean{
        return try {
            editSharedPreferences.putBoolean(name,value)
            editSharedPreferences.commit()
            true
        }catch (e:Exception){
            false
        }
    }
    fun getSharedPreferences(name:String, defaultValue:String): String{
        return sharedPreferences.getString(name, defaultValue)!!
    }

    fun getBooleanSharedPreferences(name:String, defaultValue:Boolean): Boolean{
        return sharedPreferences.getBoolean(name, defaultValue)
    }
}