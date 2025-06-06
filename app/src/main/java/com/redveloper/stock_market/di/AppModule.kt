package com.redveloper.stock_market.di

import android.app.Application
import androidx.room.Room
import com.redveloper.stock_market.data.local.StockDatabaes
import com.redveloper.stock_market.data.remote.StockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStockApi(): StockApi{
        return Retrofit.Builder()
            .baseUrl(StockApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideStockDatabase(app: Application): StockDatabaes{
        return Room.databaseBuilder(
            app,
            StockDatabaes::class.java,
            "stockdb.db"
        ).build()
    }
}