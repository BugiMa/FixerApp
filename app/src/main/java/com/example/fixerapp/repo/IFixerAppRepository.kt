package com.example.fixerapp.repo

import com.example.fixerapp.data.remote.ExchangeRatesResponse
import retrofit2.Response

interface IFixerAppRepository {
    suspend fun getExchangeRatesByDate(date: String): Response<ExchangeRatesResponse>
}