package com.redveloper.stock_market.data.repository

import com.redveloper.stock_market.data.csv.CSVParser
import com.redveloper.stock_market.data.local.StockDatabaes
import com.redveloper.stock_market.data.mapper.toCompanyListing
import com.redveloper.stock_market.data.mapper.toCompanyListingEntity
import com.redveloper.stock_market.data.remote.StockApi
import com.redveloper.stock_market.domain.model.CompanyListing
import com.redveloper.stock_market.domain.repository.StockRepository
import com.redveloper.stock_market.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabaes,
    val companyListingsParser: CSVParser<CompanyListing>
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow{
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if(shouldJustLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            }
            catch (e: IOException){
                e.printStackTrace()
                emit(Resource.Error("couldn't load data"))
                null
            }
            catch (e: HttpException){
                e.printStackTrace()
                emit(Resource.Error("couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insetCompanyListings(listings.map { it.toCompanyListingEntity() })

                emit(Resource.Success(
                    data = dao.searchCompanyListing("").map { it.toCompanyListing() }
                ))

                emit(Resource.Loading(false))
            }
        }
    }
}