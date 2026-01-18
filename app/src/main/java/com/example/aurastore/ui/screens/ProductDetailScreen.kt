package com.example.aurastore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurastore.ui.theme.*

@Composable
fun ProductDetailScreen(
    onBackClick: () -> Unit,
    viewModel: com.example.aurastore.ui.viewmodel.ProductDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var isFavorite by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val productState by viewModel.productState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(AuraDarkBg)) {
        when(val state = productState) {
            is com.example.aurastore.common.Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AuraGold)
                }
            }
            is com.example.aurastore.common.Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                   Text(text = state.message ?: "Error", color = Color.Red)
                   Button(onClick = onBackClick, modifier = Modifier.padding(top=16.dp)) { Text("Go Back") }
                }
            }
            is com.example.aurastore.common.Resource.Success -> {
                val product = state.data!!
                
                // Parallax Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .graphicsLayer {
                            alpha = 1f - (scrollState.value.toFloat() / 1000f).coerceIn(0f, 1f)
                            translationY = 0.5f * scrollState.value
                        }
                ) {
                    // Actual Image
                    coil.compose.AsyncImage(
                        model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(product.imageUrls.firstOrNull())
                        .crossfade(true)
                        .build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Gradient Overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        AuraDarkBg
                                    ),
                                    startY = 300f
                                )
                            )
                    )
                }

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AuraMidnight.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    IconButton(
                        onClick = { /* Share */ },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AuraMidnight.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(top = 350.dp) // Offset for parallax
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        color = AuraMidnight
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // Header: Title & Rating
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = AuraTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = AuraGold, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("4.8 (120 reviews)", style = MaterialTheme.typography.labelMedium, color = AuraTextSecondary)
                                    }
                                }
                                
                                IconButton(onClick = { isFavorite = !isFavorite }) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (isFavorite) Color.Red else AuraTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Price
                            Text(
                                text = "$${product.price}",
                                style = MaterialTheme.typography.displaySmall,
                                color = AuraGold,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Color Selector (Mock)
                            Text("Select Color", style = MaterialTheme.typography.titleMedium, color = AuraTextPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                listOf(Color.Black, Color(0xFF1565C0), Color(0xFF2E7D32)).forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(2.dp, AuraTextPrimary, CircleShape)
                                            .clickable { }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Description
                            Text("Description", style = MaterialTheme.typography.titleMedium, color = AuraTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AuraTextSecondary,
                                lineHeight = 24.sp
                            )
                            
                            Spacer(modifier = Modifier.height(100.dp)) // Spacing for bottom bar
                        }
                    }
                }

                // Sticky Bottom Bar
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    color = AuraSurface,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Price", style = MaterialTheme.typography.labelMedium, color = AuraTextSecondary)
                            Text("$${product.price}", style = MaterialTheme.typography.titleLarge, color = AuraTextPrimary, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = { 
                                viewModel.addToCart {
                                    android.widget.Toast.makeText(context, "Added to Cart!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraGold),
                            modifier = Modifier.height(50.dp).width(160.dp)
                        ) {
                            Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = AuraMidnight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart", color = AuraMidnight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            null -> { Box(modifier = Modifier.fillMaxSize()) }
        }
    }
}
