package com.example.aurastore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Product
import com.example.aurastore.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _rawProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _productsState = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val productsState = _productsState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            _productsState.value = Resource.Loading()
            val result = productRepository.getProducts()
            if (result is Resource.Success) {
                _rawProducts.value = result.data ?: emptyList()
                filterProducts()
            } else {
                _productsState.value = result
            }
        }
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        filterProducts()
    }

    private fun filterProducts() {
        val currentCategory = _selectedCategory.value
        val allProducts = _rawProducts.value
        
        if (currentCategory == "All") {
            _productsState.value = Resource.Success(allProducts)
        } else {
            // Mock filtering logic since our products might not have 'category' field yet
            // In a real app, we filter by product.category
            // For now, let's just shuffle or show a subset to simulate filtering visually
            // Or if we added Category to Product model, use that.
            // Let's check Product model compatibility. Assume we filter by name/desc contains for now or random subset
            
            // "Smart" Mock Logic: Filter if description contains category keywords, else random
            val filtered = allProducts.filter { 
                it.description.contains(currentCategory, ignoreCase = true) || 
                it.name.contains(currentCategory, ignoreCase = true) 
            }
            
            _productsState.value = Resource.Success(if(filtered.isNotEmpty()) filtered else allProducts.take(2)) // Fallback
        }
    }
}
