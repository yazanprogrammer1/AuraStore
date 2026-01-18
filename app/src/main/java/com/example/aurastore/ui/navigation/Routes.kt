package com.example.aurastore.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object HomeRoute

@Serializable
data class ProductDetailRoute(val productId: String)

@Serializable
object CartRoute

@Serializable
object CheckoutRoute

@Serializable
object ProfileRoute

@Serializable
object SearchRoute

@Serializable
object WishlistRoute

@Serializable
object OrdersRoute
