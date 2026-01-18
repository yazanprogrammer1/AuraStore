package com.example.aurastore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.OrderItem
import com.example.aurastore.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first // Added import
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: com.example.aurastore.domain.repository.CartRepository
) : ViewModel() {

    private val _orderState = MutableStateFlow<Resource<Unit>?>(null)
    val orderState = _orderState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    
    // We can expose cart total here if needed for UI, or assume UI calculates it or reused CartViewModel
    // For simplicity, let's just fetch in createOrder.
    // Ideally CheckoutScreen should observe Cart or CheckoutViewModel's copy of valid items.
    
    private val _checkoutTotal = MutableStateFlow(0.0)
    val checkoutTotal = _checkoutTotal.asStateFlow()
    
    init {
        calculateTotal()
    }
    
    private fun calculateTotal() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { result ->
                if (result is Resource.Success) {
                    val items = result.data ?: emptyList()
                    _checkoutTotal.value = items.sumOf { it.price * it.quantity }
                }
            }
        }
    }

    fun createOrder(
        address: String,
        cardHolder: String
    ) {
        viewModelScope.launch {
            _orderState.value = Resource.Loading()
            
            // Let's assume we can collect one item.
            var items: List<OrderItem> = emptyList()
            // We need a way to get the current value from the Flow synchronously or use a collected state
            // Let's just collect once.
            try {
                // Determine items from repository
                // Since getCartItems returns a Flow, we collect it.
                // NOTE: transform latest value.
                cartRepository.getCartItems().collect { result ->
                     if (result is Resource.Success) {
                         items = result.data ?: emptyList()
                         // Break collection? No, collect is indefinite.
                         // We need `first()` operator on the flow. 
                         throw Exception("CartLoaded") // Flow abortion or use first()
                     } else if (result is Resource.Error) {
                         throw Exception(result.message)
                     }
                }
            } catch (e: Exception) {
                if (e.message != "CartLoaded") {
                     _orderState.value = Resource.Error("Failed to load cart: ${e.message}")
                     return@launch
                }
            }
            
            // Wait, using collect inside launch and throwing exception is bad practice.
            // 1. Fetch Cart Items
            val resultResource = cartRepository.getCartItems().firstOrNull() ?: Resource.Error("Failed to fetch cart")
            
            if (resultResource is Resource.Error) {
                _orderState.value = Resource.Error(resultResource.message ?: "Failed to load cart")
                return@launch
            }
            items = resultResource.data ?: emptyList()
            
            if (items.isEmpty()) {
                _orderState.value = Resource.Error("Cart is empty")
                return@launch
            }

            val total = items.sumOf { it.price * it.quantity }
            
            // Combine address and card holder for simple storage
            val fullAddress = "$address (Card Holder: $cardHolder)"
            
            val result = orderRepository.createOrder(items, total, fullAddress)
            
            if (result is Resource.Success) {
                // Clear Cart
                cartRepository.clearCart()
                
                _orderState.value = Resource.Success(Unit)
                _uiEvent.send(UiEvent.OrderPlaced)
            } else {
                _orderState.value = Resource.Error(result.message ?: "Available")
                _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Order failed"))
            }
        }
    }

    sealed class UiEvent {
        object OrderPlaced : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
