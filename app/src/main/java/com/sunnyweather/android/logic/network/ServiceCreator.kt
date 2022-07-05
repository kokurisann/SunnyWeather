package com.sunnyweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//为了使用PlaceService接口，需要创建ServiceCreator单例类
object ServiceCreator {

    //彩云API的访问网址
    private const val BASE_URL = "https://api.caiyunapp.com/"

    //构建Retrofit对象，baseUrl设置根路径，addConverterFactory设置转换库
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //调用该函数创建Retrofit网络服务
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    //内联函数，省略class.java
    inline fun <reified T> create(): T = create(T::class.java)

}