package com.redveloper.stock_market.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redveloper.stock_market.domain.repository.StockRepository
import com.redveloper.stock_market.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingViewModel @Inject constructor(
    private val repository: StockRepository
): ViewModel(){

    var state by mutableStateOf(CompanyListingState())
    private var searchJob: Job? = null

    init{
        getCompanyListing()
    }

    fun onEvent(event: CompanyListingEvent){
        when(event){
            is CompanyListingEvent.Refresh -> {
                getCompanyListing(fetchFromRemote = true)
            }
            is CompanyListingEvent.OnSearchQueryChange -> {
                state = state.copy(
                    searchQuery = event.query
                )
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getCompanyListing()
                }
            }
        }
    }

    private fun getCompanyListing(
        query: String = state.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ){
        viewModelScope.launch {
            repository
                .getCompanyListings(fetchFromRemote, query)
                .collect{ result ->
                    when(result){
                        is Resource.Success -> {
                            result?.data?.let { listing ->
                                state = state.copy(
                                    companies = listing
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}