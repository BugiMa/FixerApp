package com.example.fixerapp.repo

import com.example.fixerapp.api.FixerApi
import com.example.fixerapp.data.remote.ExchangeRatesResponse
import com.example.fixerapp.domain.ExchangeRate
import retrofit2.Response
import javax.inject.Inject

class FixerAppRepository @Inject constructor(
    private val api: FixerApi
): IFixerAppRepository {

    override suspend fun getExchangeRatesByDate(date: String): Response<ExchangeRatesResponse> {
        return api.getExchangeRatesByDate(date)
    }

}