package com.example.ahmad.footbalmatch.data.retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {

    companion object {
        fun getClient(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.banghasan.com/quran/format/json/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
    }
}