package com.example.fixerapp.domain

class ExchangeRate (
    val date: String = "",
    val rate: Pair<String, Double> = Pair("", 0.0)
)