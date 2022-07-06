package com.sunnyweather.android.logic.model

//将Realtime和Daily两种数据对象封装，二者构成了天气功能的数据模型
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)
