package com.redveloper.stock_market.di

import com.redveloper.stock_market.data.csv.CSVParser
import com.redveloper.stock_market.data.csv.CompanyListingsParser
import com.redveloper.stock_market.data.csv.IntradayInfoParser
import com.redveloper.stock_market.data.repository.StockRepositoryImpl
import com.redveloper.stock_market.domain.model.CompanyListing
import com.redveloper.stock_market.domain.model.IntradayInfo
import com.redveloper.stock_market.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>
}