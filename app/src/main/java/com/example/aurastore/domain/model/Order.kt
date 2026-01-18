package com.example.aurastore.domain.model

data class Order(
    val id: String = "",
    val userId: String = "", // Added userId
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0, // Renamed from totalPrice to match Repo/Convention
    val status: String = "PENDING", // Changed enum to String for easier Firestore compilation initially
    val createdAt: String = "", // Changed to String for easy readability or Long
    val trackingHistory: List<String> = emptyList()
)

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val imageUrl: String = ""
)

enum class OrderStatus {
    PLACED, CONFIRMED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
}
