package com.example.aurastore.di

import com.example.aurastore.data.repository.FirestoreCartRepository
import com.example.aurastore.domain.repository.AuthRepository
import com.example.aurastore.domain.repository.CartRepository
import com.example.aurastore.domain.repository.OrderRepository
import com.example.aurastore.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepository: com.example.aurastore.data.repository.FirebaseAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        firestoreProductRepository: com.example.aurastore.data.repository.FirestoreProductRepository
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        firestoreOrderRepository: com.example.aurastore.data.repository.FirestoreOrderRepository
    ): OrderRepository
    
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        firestoreCartRepository: FirestoreCartRepository
    ): CartRepository
}
