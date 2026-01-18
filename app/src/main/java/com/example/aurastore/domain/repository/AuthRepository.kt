package com.example.aurastore.domain.repository

import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(email: String, password: String, name: String): Resource<Unit>
    suspend fun authenticate(): Resource<Unit>
    fun isLoggedIn(): Boolean
    suspend fun toggleWishlist(productId: String): Resource<Boolean> // Returns new state (true=added, false=removed)
    suspend fun isProductInWishlist(productId: String): Resource<Boolean>
    suspend fun getWishlist(): Resource<List<String>>
    fun logout()
}
