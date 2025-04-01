package com.redveloper.stock_market.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CompanyListingEntity::class],
    version = 1
)
abstract class StockDatabaes: RoomDatabase() {

    abstract val dao: StockDao
}