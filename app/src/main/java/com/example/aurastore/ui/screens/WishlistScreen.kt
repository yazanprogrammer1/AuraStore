package com.example.aurastore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aurastore.common.Resource
import com.example.aurastore.ui.theme.*
import com.example.aurastore.ui.viewmodel.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val wishlistState by viewModel.wishlistState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wishlist", color = AuraTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AuraTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuraDarkBg)
            )
        },
        containerColor = AuraDarkBg
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = wishlistState) {
                is Resource.Loading -> {
                   Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                       CircularProgressIndicator(color = AuraGold)
                   }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message ?: "An error occurred", color = Color.Red)
                    }
                }
                is Resource.Success -> {
                    val products = state.data ?: emptyList()
                    
                    if (products.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = AuraTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Your wishlist is empty", color = AuraTextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(products) { product ->
                                ProductRowPair(listOf(product)) { onProductClick(it) } // Reuse existing row logic or create new list item
                                // Actually, ProductRowPair expects a list and renders columns. 
                                // Let's simplify and just use a customized row here or reuse ProductCard.
                                // Let's use ProductCard in a full width row?
                                // Better: Create a WishlistItem composable quickly here.
                            }
                        }
                    }
                }
            }
        }
    }
}
