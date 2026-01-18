package com.example.aurastore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.OrderItem
import com.example.aurastore.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cartItemsState = MutableStateFlow<Resource<List<OrderItem>>>(Resource.Loading())
    val cartItemsState = _cartItemsState.asStateFlow()
    
    // Calculated total
    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount = _totalAmount.asStateFlow()

    init {
        getCartItems()
    }

    fun getCartItems() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { result ->
                _cartItemsState.value = result
                if (result is Resource.Success) {
                    calculateTotal(result.data ?: emptyList())
                }
            }
        }
    }
    
    private fun calculateTotal(items: List<OrderItem>) {
        _totalAmount.value = items.sumOf { it.price * it.quantity }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, newQuantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
    }
}
