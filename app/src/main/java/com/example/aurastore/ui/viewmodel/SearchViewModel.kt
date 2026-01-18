package com.example.aurastore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Product
import com.example.aurastore.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<Product>>>(Resource.Success(emptyList()))
    val searchState = _searchState.asStateFlow()
    
    // For debouncing
    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        if (query.isBlank()) {
            _searchState.value = Resource.Success(emptyList())
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L) // 500ms debounce
            _searchState.value = Resource.Loading()
            val result = productRepository.searchProducts(query)
            _searchState.value = result
        }
    }
}
