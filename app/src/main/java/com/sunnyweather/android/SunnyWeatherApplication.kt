package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

//全局获取Context
class SunnyWeatherApplication : Application() {

    companion object {
        //调用天气API所需的TOKEN
        const val TOKEN = "kr7vt3BNhgZ58cIz"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}