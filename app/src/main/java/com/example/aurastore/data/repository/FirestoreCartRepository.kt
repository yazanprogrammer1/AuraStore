package com.example.aurastore.data.repository

import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.OrderItem
import com.example.aurastore.domain.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreCartRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CartRepository {

    override fun getCartItems(): Flow<Resource<List<OrderItem>>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(Resource.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val cartCollection = db.collection("users").document(currentUser.uid).collection("cart")
        
        val listener = cartCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error(error.localizedMessage ?: "Failed to listen to cart"))
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val items = snapshot.toObjects(OrderItem::class.java)
                trySend(Resource.Success(items))
            } else {
                trySend(Resource.Success(emptyList()))
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun addToCart(item: OrderItem): Resource<Unit> {
        val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
        val cartCollection = db.collection("users").document(currentUser.uid).collection("cart")

        return try {
            // Check if item exists to update quantity?
            // For simplicity in this robust MVP, we'll overwrite or just set.
            // Ideally we check if it exists and increment.
            // Let's do a quick check? No, let's just use set() for now, user can update quantity in cart.
            // Actually, if I add again, usually it increments.
            // Let's implement increment logic.
            
            val docRef = cartCollection.document(item.productId)
            val doc = docRef.get().await()
            
            if (doc.exists()) {
                val existingItem = doc.toObject(OrderItem::class.java)
                if (existingItem != null) {
                    val newQuantity = existingItem.quantity + item.quantity
                    docRef.update("quantity", newQuantity).await()
                } else {
                    docRef.set(item).await()
                }
            } else {
                docRef.set(item).await()
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add to cart")
        }
    }

    override suspend fun removeFromCart(productId: String): Resource<Unit> {
        val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
        return try {
            db.collection("users").document(currentUser.uid).collection("cart").document(productId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove item")
        }
    }

    override suspend fun updateQuantity(productId: String, quantity: Int): Resource<Unit> {
        val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
        return try {
            if (quantity <= 0) {
                removeFromCart(productId)
            } else {
                db.collection("users").document(currentUser.uid).collection("cart").document(productId)
                    .update("quantity", quantity).await()
                Resource.Success(Unit)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update quantity")
        }
    }

    override suspend fun clearCart(): Resource<Unit> {
         val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
         val cartCollection = db.collection("users").document(currentUser.uid).collection("cart")
         return try {
             val snapshot = cartCollection.get().await()
             val batch = db.batch()
             for (doc in snapshot.documents) {
                 batch.delete(doc.reference)
             }
             batch.commit().await()
             Resource.Success(Unit)
         } catch (e: Exception) {
             Resource.Error(e.message ?: "Failed to clear cart")
         }
    }
}
