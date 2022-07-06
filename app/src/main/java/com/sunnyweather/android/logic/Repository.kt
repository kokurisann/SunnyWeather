package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

//仓库类，负责判断调用方请求的数据应该从本地数据源获取还是网络数据源获取
//本案例中没有进行本地缓存的实现，所以请求数据都使用网络数据源获取
//使用LiveData
object Repository {

    //获取Places数据
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    //获取实时天气数据与每日天气数据
    //使用协程保证两个网络请求都响应后再进行程序的后续操作
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }

            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException("realtime response status is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}")
                )
            }
        }
    }

    //fire()函数简化try-catch代码块以及emit()等操作步骤，使用LiveData的方法直接调用该方法即可
    @Suppress("SameParameterValue")
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) = liveData(context) {
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    //保存与获取选中的Place数据
    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}