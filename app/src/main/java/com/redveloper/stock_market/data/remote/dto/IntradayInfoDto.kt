package com.redveloper.stock_market.data.remote.dto

data class IntradayInfoDto(
    val timestamp: String,
    val close: Double
)