package com.example.aurastore.domain.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val originalPrice: Double? = null,
    val discountNode: Int? = null,
    val imageUrls: List<String> = emptyList(),
    val category: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isFlashSale: Boolean = false,
    val stock: Int = 100
)
