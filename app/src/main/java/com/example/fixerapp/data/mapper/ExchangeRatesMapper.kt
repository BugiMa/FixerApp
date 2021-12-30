package com.example.fixerapp.data.mapper

import com.example.fixerapp.data.remote.ExchangeRatesResponse
import com.example.fixerapp.domain.ExchangeRate
import kotlin.math.roundToInt
import kotlin.reflect.full.memberProperties

fun ExchangeRatesResponse.toDomain(): List<ExchangeRate> {
    val ratesList = mutableListOf<ExchangeRate>()
    val rates = this.rates
    rates::class.memberProperties.forEach{ prop ->
        ratesList.add(
            ExchangeRate(
                this.date,
                Pair(prop.name, ((prop.getter.call(rates) as Double) * 1000.0).roundToInt() / 1000.0)
            )
        )
    }
    return ratesList
}
