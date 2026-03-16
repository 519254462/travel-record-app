package com.travelrecord.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelrecord.app.data.repository.TravelRepository

/**
 * Factory for creating TravelListViewModel with dependencies
 */
class TravelListViewModelFactory(
    private val repository: TravelRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelListViewModel::class.java)) {
            return TravelListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}