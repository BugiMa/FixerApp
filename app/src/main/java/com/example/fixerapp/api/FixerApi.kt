package com.example.fixerapp.api

import com.example.fixerapp.data.remote.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FixerApi {

    @GET("/api/{date}")
    suspend fun getExchangeRatesByDate(
        @Path("date") date: String,
        @Query("access_key") accessKey: String = "540c68cfcfc2c4790954968917f4dbab"
    ): Response<ExchangeRatesResponse>
}