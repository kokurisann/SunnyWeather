package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//统一的数据源访问入口，对所有的网络请求的API进行封装
object SunnyWeatherNetwork {

    //创建一个PlaceService接口的动态管理对象
    private val placeService = ServiceCreator.create<PlaceService>()

    //创建一个WeatherService接口的动态管理对象
    private val weatherService = ServiceCreator.create<WeatherService>()

    //发起搜索城市数据的请求
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    //发起获取实时天气数据的请求
    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).await()

    //发起获取每日天气数据的请求
    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()

    //使用协程简化Retrofit回调函数的写法
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}