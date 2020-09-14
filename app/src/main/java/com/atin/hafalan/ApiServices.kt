package com.atin.hafalan

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiServices {

    @GET("{file}")
    fun downloadFile(@Path("file") file: String?): Call<ResponseBody>?
}