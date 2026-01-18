package com.example.aurastore.domain.repository

import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.OrderItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<Resource<List<OrderItem>>>
    suspend fun addToCart(item: OrderItem): Resource<Unit>
    suspend fun removeFromCart(productId: String): Resource<Unit>
    suspend fun updateQuantity(productId: String, quantity: Int): Resource<Unit>
    suspend fun clearCart(): Resource<Unit>
}
