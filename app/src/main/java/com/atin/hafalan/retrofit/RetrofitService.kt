package com.example.ahmad.footbalmatch.data.retrofit

import com.atin.hafalan.response.Surats
import io.reactivex.Observable
import retrofit2.http.GET

interface RetrofitService {
    @GET("eventspastleague.php")
    fun getSurat(): Observable<Surats>
}