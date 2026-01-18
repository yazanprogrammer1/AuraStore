package com.example.aurastore.data.repository

import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Product
import com.example.aurastore.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreProductRepository @Inject constructor(
    private val db: FirebaseFirestore
) : ProductRepository {

    private val productsCollection = db.collection("products")

    override suspend fun searchProducts(query: String): Resource<List<Product>> {
        return try {
            val snapshot = productsCollection
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .await()
            val products = snapshot.toObjects(Product::class.java)
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Search failed")
        }
    }

    override suspend fun getProductsByIds(productIds: List<String>): Resource<List<Product>> {
        if (productIds.isEmpty()) return Resource.Success(emptyList())
        
        return try {
            // Firestore 'in' query supports up to 10 items.
            // For larger lists, we need to batch or fetch individually.
            // For this verified implementation, we'll fetch individually for robustness if list > 10,
            // or just use 'whereIn' if small. Let's assume small for now, but handle chunking if needed.
            // Actually, for a robust "Luxury" app, let's just fetch all needed docs.
            // A simple implementation for now:
            
            val products = mutableListOf<Product>()
            // Using chunks of 10 to respect Firestore limit
            productIds.chunked(10).forEach { chunk ->
                val snapshot = productsCollection.whereIn("id", chunk).get().await()
                products.addAll(snapshot.toObjects(Product::class.java))
            }
            
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch wishlist items")
        }
    }

    override suspend fun getProducts(): Resource<List<Product>> {
        return try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.toObjects(Product::class.java)
            
            if (products.isEmpty()) {
                // Seed if empty (for Demo purposes)
                seedProducts()
                // Return seeded products for immediate feedback
                val seededSnapshot = db.collection("products").get().await()
                return Resource.Success(seededSnapshot.toObjects(Product::class.java))
            }
            
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch products")
        }
    }

    override suspend fun getProductById(id: String): Resource<Product> {
        return try {
            val snapshot = db.collection("products").document(id).get().await()
            val product = snapshot.toObject(Product::class.java)
            if (product != null) {
                Resource.Success(product)
            } else {
                Resource.Error("Product not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch product")
        }
    }

    private suspend fun seedProducts() {
        val sampleProducts = listOf(
            Product(
                id = "1",
                name = "Aura Wireless Headphones",
                description = "Premium noise cancelling headphones with 30h battery life.",
                price = 299.99,
                imageUrls = listOf("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=2940&auto=format&fit=crop"),
                category = "Electronics"
            ),
            Product(
                id = "2",
                name = "Mechanical Keyboard",
                description = "RGB Mechanical keyboard with distinct tactile feedback.",
                price = 149.99,
                imageUrls = listOf("https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?q=80&w=2940&auto=format&fit=crop"),
                category = "Electronics"
            ),
             Product(
                id = "3",
                name = "Smart Watch Series 7",
                description = "Advanced health monitoring and fitness tracking.",
                price = 399.99,
                imageUrls = listOf("https://images.unsplash.com/photo-1546868871-7041f2a55e12?q=80&w=2864&auto=format&fit=crop"),
                category = "Wearables"
            ),
            Product(
                id = "4",
                name = "Modern Desk Lamp",
                description = "Minimalist LED desk lamp with adjustable brightness.",
                price = 45.00,
                imageUrls = listOf("https://images.unsplash.com/photo-1507473888900-52e1adad54cd?q=80&w=2787&auto=format&fit=crop"),
                category = "Home"
            )
        )
        
        sampleProducts.forEach { product ->
            db.collection("products").document(product.id).set(product).await()
        }
    }
}
