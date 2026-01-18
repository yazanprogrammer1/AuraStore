package com.example.aurastore.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Product
import com.example.aurastore.domain.repository.ProductRepository
import com.example.aurastore.ui.navigation.ProductDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: com.example.aurastore.domain.repository.AuthRepository,
    private val cartRepository: com.example.aurastore.domain.repository.CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _productState = MutableStateFlow<Resource<Product>>(Resource.Loading())
    val productState = _productState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()
    
    // Track productId to use in toggle
    private var currentProductId: String? = null

    init {
        val productId = savedStateHandle.get<String>("productId")
        if (productId != null) {
            currentProductId = productId
            getProduct(productId)
            checkIfFavorite(productId)
        } else {
             _productState.value = Resource.Error("Invalid Product ID")
        }
    }

    private fun getProduct(id: String) {
        viewModelScope.launch {
            _productState.value = Resource.Loading()
            _productState.value = productRepository.getProductById(id)
        }
    }
    
    private fun checkIfFavorite(productId: String) {
        viewModelScope.launch {
            val result = authRepository.isProductInWishlist(productId)
            if (result is Resource.Success) {
                _isFavorite.value = result.data ?: false
            } else {
                 // Fallback or log error
            }
        }
    }
    
    fun toggleFavorite() {
        val productId = currentProductId ?: return
        viewModelScope.launch {
            // Optimistic update
            _isFavorite.value = !_isFavorite.value
            
            val result = authRepository.toggleWishlist(productId)
            if (result is Resource.Error) {
                // Revert if failed
                _isFavorite.value = !_isFavorite.value
            } else if (result is Resource.Success) {
                _isFavorite.value = result.data ?: false
            }
        }
    }
    
    fun addToCart(onSuccess: () -> Unit) {
        val product = _productState.value.data ?: return
        viewModelScope.launch {
            val orderItem = com.example.aurastore.domain.model.OrderItem(
                productId = product.id,
                productName = product.name,
                price = product.price,
                quantity = 1,
                imageUrl = product.imageUrls.firstOrNull() ?: ""
            )
            val result = cartRepository.addToCart(orderItem)
            if (result is Resource.Success) {
                onSuccess()
            }
        }
    }
}
