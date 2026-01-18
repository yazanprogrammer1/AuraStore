package com.example.aurastore.domain.repository

import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): Resource<List<Product>>
    suspend fun getProductById(productId: String): Resource<Product>
    suspend fun searchProducts(query: String): Resource<List<Product>>
    suspend fun getProductsByIds(productIds: List<String>): Resource<List<Product>>
}
