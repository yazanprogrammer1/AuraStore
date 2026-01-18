package com.example.aurastore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Order
import com.example.aurastore.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _ordersState = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val ordersState = _ordersState.asStateFlow()

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            _ordersState.value = Resource.Loading()
            _ordersState.value = orderRepository.getOrders()
        }
    }
}
