package com.example.aurastore.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val avatarUrl: String? = null,
    val token: String? = null, // Add token for compatibility
    val wishlist: List<String> = emptyList()
)
