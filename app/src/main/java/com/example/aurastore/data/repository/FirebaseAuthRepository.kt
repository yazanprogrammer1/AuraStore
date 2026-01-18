package com.example.aurastore.data.repository

import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.User
import com.example.aurastore.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.Error("Login failed: Unknown error")
            
            // Fetch User Profile from Firestore to get Wishlist
            val document = db.collection("users").document(firebaseUser.uid).get().await()
            val wishlist = if (document.exists()) {
                 (document.get("wishlist") as? List<String>) ?: emptyList()
            } else {
                emptyList()
            }

            Resource.Success(
                User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    name = firebaseUser.displayName ?: "User",
                    wishlist = wishlist,
                    token = "firebase_token_placeholder"
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Login failed")
        }
    }

    override suspend fun register(email: String, password: String, name: String): Resource<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = auth.currentUser ?: return Resource.Error("Registration failed")
            
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                displayName = name
            }
            firebaseUser.updateProfile(profileUpdates).await()
            
            // Create Firestore Document
            val user = User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                wishlist = emptyList()
            )
            db.collection("users").document(firebaseUser.uid).set(user).await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override suspend fun authenticate(): Resource<Unit> {
        return if (auth.currentUser != null) {
            Resource.Success(Unit)
        } else {
            Resource.Error("User not logged in")
        }
    }

    override fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun toggleWishlist(productId: String): Resource<Boolean> {
        val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
        val userRef = db.collection("users").document(currentUser.uid)

        return try {
            val document = userRef.get().await()
            val currentWishlist = (document.get("wishlist") as? List<String>) ?: emptyList()
            
            if (currentWishlist.contains(productId)) {
                userRef.update("wishlist", com.google.firebase.firestore.FieldValue.arrayRemove(productId)).await()
                Resource.Success(false) // Removed
            } else {
                userRef.update("wishlist", com.google.firebase.firestore.FieldValue.arrayUnion(productId)).await()
                Resource.Success(true) // Added
            }
        } catch (e: Exception) {
             // Fallback: If document doesn't exist, create it
            try {
                 val initialWishlist = listOf(productId)
                 val user = User(
                    id = currentUser.uid,
                    email = currentUser.email ?: "",
                    name = currentUser.displayName ?: "User",
                    wishlist = initialWishlist
                )
                userRef.set(user).await()
                Resource.Success(true)
            } catch (e2: Exception) {
                Resource.Error(e2.localizedMessage ?: "Failed to update wishlist")
            }
        }
    }

    override suspend fun isProductInWishlist(productId: String): Resource<Boolean> {
        val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
        val userRef = db.collection("users").document(currentUser.uid)
        
        return try {
            val document = userRef.get().await()
            val currentWishlist = (document.get("wishlist") as? List<String>) ?: emptyList()
            Resource.Success(currentWishlist.contains(productId))
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to check wishlist")
        }
    }

    override suspend fun getWishlist(): Resource<List<String>> {
        val currentUser = auth.currentUser ?: return Resource.Error("User not logged in")
        return try {
            val document = db.collection("users").document(currentUser.uid).get().await()
            val wishlist = (document.get("wishlist") as? List<String>) ?: emptyList()
            Resource.Success(wishlist)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch wishlist")
        }
    }
}
