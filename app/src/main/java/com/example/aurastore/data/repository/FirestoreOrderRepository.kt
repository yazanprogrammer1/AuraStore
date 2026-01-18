package com.example.aurastore.data.repository

import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Order
import com.example.aurastore.domain.model.OrderItem
import com.example.aurastore.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import java.util.UUID

class FirestoreOrderRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : OrderRepository {

    override suspend fun createOrder(items: List<OrderItem>, total: Double, address: String): Resource<Order> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val orderId = UUID.randomUUID().toString()
            
            val order = Order(
                id = orderId,
                userId = userId,
                items = items,
                totalAmount = total,
                status = "Pending",
                createdAt = System.currentTimeMillis().toString(),
                trackingHistory = listOf("Order Placed")
            )

            db.collection("orders").document(orderId).set(order).await()
            Resource.Success(order)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create order")
        }
    }

    override suspend fun getOrders(): Resource<List<Order>> {
         return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val snapshot = db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val orders = snapshot.toObjects(Order::class.java)
            Resource.Success(orders)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch orders")
        }
    }
    
    override suspend fun getOrderById(id: String): Resource<Order> {
         return try {
            val snapshot = db.collection("orders").document(id).get().await()
            val order = snapshot.toObject(Order::class.java)
            if (order != null) {
                Resource.Success(order)
            } else {
                Resource.Error("Order not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch order")
        }
    }
}
