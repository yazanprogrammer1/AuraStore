package com.example.aurastore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.example.aurastore.domain.model.Order
import com.example.aurastore.ui.theme.*
import com.example.aurastore.ui.viewmodel.OrderHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onBackClick: () -> Unit,
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val ordersState by viewModel.ordersState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", color = AuraTextPrimary) },
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
            when (val state = ordersState) {
                is Resource.Loading -> {
                   Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                       CircularProgressIndicator(color = AuraGold)
                   }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message ?: "Failed to load orders", color = Color.Red)
                        Button(onClick = { viewModel.getOrders() }) { Text("Retry") }
                    }
                }
                is Resource.Success -> {
                    val orders = state.data ?: emptyList()
                    
                    if (orders.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = AuraTextSecondary.copy(alpha=0.5f), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No orders pending", color = AuraTextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(orders) { order ->
                                OrderCard(order)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AuraSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id.take(8).uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = AuraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${order.totalAmount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = AuraGold,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status: ${order.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if(order.status == "Pending") AuraGold else Color.Green
                )
                Text(
                    text = formatDate(order.createdAt), // Need date logic
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = AuraTextSecondary.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))
            order.items.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                     Text("${item.quantity}x ${item.productName}", color = AuraTextPrimary, modifier = Modifier.weight(1f))
                     Text("$${item.price}", color = AuraTextSecondary)
                }
            }
        }
    }
}

fun formatDate(timestampStr: String): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val netDate = Date(timestampStr.toLong())
        sdf.format(netDate)
    } catch (e: Exception) {
        "Recent"
    }
}
