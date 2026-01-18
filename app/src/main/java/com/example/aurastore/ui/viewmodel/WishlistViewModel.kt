package com.example.aurastore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Product
import com.example.aurastore.domain.repository.AuthRepository
import com.example.aurastore.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _wishlistState = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val wishlistState = _wishlistState.asStateFlow()

    init {
        getWishlist()
    }

    fun getWishlist() {
        viewModelScope.launch {
            _wishlistState.value = Resource.Loading()
            
            val wishlistResult = authRepository.getWishlist()
            if (wishlistResult is Resource.Success) {
                val productIds = wishlistResult.data ?: emptyList()
                if (productIds.isEmpty()) {
                    _wishlistState.value = Resource.Success(emptyList())
                } else {
                    val productsResult = productRepository.getProductsByIds(productIds)
                    _wishlistState.value = productsResult
                }
            } else {
                _wishlistState.value = Resource.Error(wishlistResult.message ?: "Failed to load wishlist")
            }
        }
    }
}
