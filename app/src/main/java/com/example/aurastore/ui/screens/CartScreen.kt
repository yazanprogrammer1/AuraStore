package com.example.aurastore.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurastore.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    viewModel: com.example.aurastore.ui.viewmodel.CartViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val cartItemsState by viewModel.cartItemsState.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Cart", color = AuraTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AuraTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AuraDarkBg)
            )
        },
        bottomBar = {
            if (totalAmount > 0) {
                CartSummary(
                    subtotal = totalAmount,
                    onCheckout = onCheckoutClick
                )
            }
        },
        containerColor = AuraDarkBg
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = cartItemsState) {
                 is com.example.aurastore.common.Resource.Loading -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator(color = AuraGold)
                     }
                 }
                 is com.example.aurastore.common.Resource.Error -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         Text(state.message ?: "Failed to load cart", color = Color.Red)
                     }
                 }
                 is com.example.aurastore.common.Resource.Success -> {
                     val cartItems = state.data ?: emptyList()
                     if (cartItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Your cart is empty", color = AuraTextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(
                                items = cartItems,
                                key = { _, item -> item.productId }
                            ) { _, item ->
                                SwipeToDeleteItem(
                                    item = item,
                                    onDelete = { viewModel.removeFromCart(item.productId) },
                                    onQuantityChange = { change ->
                                        val newQty = item.quantity + change
                                        if (newQty > 0) {
                                            viewModel.updateQuantity(item.productId, newQty)
                                        }
                                    }
                                )
                            }
                        }
                    }
                 }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteItem(
    item: com.example.aurastore.domain.model.OrderItem,
    onDelete: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                isRemoved = true
                onDelete()
                true
            } else {
                false
            }
        }
    )

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(),
        modifier = Modifier.fillMaxWidth()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Color.Red.copy(alpha = 0.8f)
                } else {
                    Color.Transparent
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(color)
                        .padding(end = 24.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                }
            },
            content = {
                CartItemCard(item, onQuantityChange)
            }
        )
    }
}

@Composable
fun CartItemCard(item: com.example.aurastore.domain.model.OrderItem, onQuantityChange: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Placeholder
            // Use Coil for real image if available
             if (item.imageUrl.isNotEmpty()) {
                coil.compose.AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("IMG", color = AuraTextSecondary, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleMedium,
                    color = AuraTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${item.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuraGold,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(AuraDarkBg, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).clickable { onQuantityChange(-1) },
                    tint = AuraTextSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${item.quantity}",
                    color = AuraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).clickable { onQuantityChange(1) },
                    tint = AuraTextSecondary
                )
            }
        }
    }
}

@Composable
fun CartSummary(subtotal: Double, onCheckout: () -> Unit) {
    Surface(
        color = AuraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Subtotal", color = AuraTextSecondary)
                Text("$$subtotal", color = AuraTextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Shipping", color = AuraTextSecondary)
                Text("Free", color = AuraGold, fontWeight = FontWeight.Bold)
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp), color = AuraTextSecondary.copy(alpha = 0.2f))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total", style = MaterialTheme.typography.titleLarge, color = AuraTextPrimary)
                Text("$$subtotal", style = MaterialTheme.typography.titleLarge, color = AuraGold, fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AuraGold, contentColor = AuraMidnight)
            ) {
                Text("PROCEED TO CHECKOUT", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}
