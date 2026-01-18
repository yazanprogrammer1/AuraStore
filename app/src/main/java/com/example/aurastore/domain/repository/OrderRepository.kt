package com.example.aurastore.domain.repository

import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Order
import com.example.aurastore.domain.model.OrderItem

interface OrderRepository {
    suspend fun createOrder(items: List<OrderItem>, total: Double, address: String): Resource<Order>
    suspend fun getOrders(): Resource<List<Order>>
    suspend fun getOrderById(id: String): Resource<Order>
}
