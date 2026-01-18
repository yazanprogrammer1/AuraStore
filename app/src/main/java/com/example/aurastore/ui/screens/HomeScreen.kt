package com.example.aurastore.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.model.Product
import com.example.aurastore.ui.theme.*
import com.example.aurastore.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit, // Updated to pass ID
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val productsState by viewModel.productsState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { AuraBottomNavigation(onCartClick, onProfileClick) },
        containerColor = AuraDarkBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Banner
            item {
                BannerCarousel()
            }

            // Categories
            item {
                CategorySection(selectedCategory) { viewModel.onCategorySelected(it) }
            }

            when (val state = productsState) {
                is Resource.Loading -> {
                    item {
                        ShimmerLoading()
                    }
                }
                is Resource.Error -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Error: ${state.message}", color = Color.Red)
                        }
                    }
                }
                is Resource.Success -> {
                    val products = state.data ?: emptyList()
                    
                    // Flash Sale Header
                    item {
                        SectionHeader(title = "Flash Sale ⚡", action = "View All")
                    }

                    // Flash Sale Items (Horizontal) - Just taking first 5 for now
                    item {
                        FlashSaleRow(products.take(5)) { productId -> onProductClick(productId) }
                    }

                    // Recommended Header
                    item {
                        SectionHeader(title = "Recommended for You", action = "See More")
                    }

                    // Recommended Grid (Simulated with Rows of 2)
                    // Grouping products into pairs
                     val pairs = products.chunked(2)
                     items(pairs) { pair ->
                         ProductRowPair(pair) { productId -> onProductClick(productId) }
                     }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AuraMidnight)
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // Location & Logo Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "AURA",
                color = AuraGold,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { },
            color = AuraSurface
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = AuraTextSecondary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Search luxury products...", color = AuraTextSecondary)
            }
        }
    }
}

@Composable
fun BannerCarousel() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    
    // Auto-scroll logic
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }
    
    Column(modifier = Modifier.padding(top = 16.dp)) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (page == 0) listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)) // Purple
                            else if (page == 1) listOf(Color(0xFFD4AF37), Color(0xFFA88624)) // Gold
                            else listOf(Color(0xFF000000), Color(0xFF434343)) // Black
                        )
                    )
            ) {
                // Content
                Column(
                    modifier = Modifier.align(Alignment.CenterStart).padding(24.dp)
                ) {
                     Text(
                        text = if(page==0) "Summer Collection" else if(page==1) "Luxury Watches" else "Black Friday",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if(page==0) "50% OFF" else if(page==1) "New Arrivals" else "Coming Soon",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Page Indicators
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) AuraGold else AuraTextSecondary.copy(alpha=0.3f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(if (pagerState.currentPage == iteration) 10.dp else 8.dp)
                )
            }
        }
    }
}

@Composable
fun CategorySection(
    selectedCategory: String,
    onCategoryClick: (String) -> Unit
) {
    val categories = listOf("All", "Fashion", "Tech", "Beauty", "Home", "Sports")
    
    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onCategoryClick(category) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AuraGold else AuraSurface)
                            .border(1.dp, if (isSelected) AuraGold else AuraGold.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Icon placeholder (using first letter)
                        Text(
                            text = category.take(1),
                            color = if (isSelected) AuraMidnight else AuraGold,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = category, 
                        color = if (isSelected) AuraGold else AuraTextSecondary, 
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = AuraTextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(action, color = AuraGold, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun FlashSaleRow(products: List<Product>, onProductClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(product = product, width = 140.dp, onClick = { onProductClick(product.id) })
        }
    }
}

@Composable
fun ProductRowPair(pair: List<Product>, onProductClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        pair.forEach { product ->
            Box(modifier = Modifier.weight(1f)) { 
                ProductCard(product = product, width = 200.dp, onClick = { onProductClick(product.id) }) 
            }
        }
        // If odd number, fill empty space
        if (pair.size < 2) {
             Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ProductCard(product: Product, width: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(width)
            .wrapContentHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(width) // Square image
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                 AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(product.imageUrls.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Discount Badge (Mock)
                if (product.price > 100) { // Simple logic for "Sale"
                    Surface(
                        color = Color(0xFFE53935), // Red
                        shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "SALE", // Could compute %
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Icon(
                    Icons.Outlined.ShoppingCart, 
                    contentDescription = null, 
                    modifier = Modifier.size(48.dp)
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                        .background(AuraMidnight.copy(alpha=0.7f), CircleShape)
                        .padding(8.dp),
                    tint = AuraGold
                )
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    color = AuraTextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${product.price}",
                        color = AuraGold,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AuraBottomNavigation(onCartClick: () -> Unit, onProfileClick: () -> Unit) {
    NavigationBar(
        containerColor = AuraMidnight,
        contentColor = AuraGold
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AuraGold,
                indicatorColor = AuraGold.copy(alpha = 0.2f),
                unselectedIconColor = AuraTextSecondary
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Category, contentDescription = null) },
            label = { Text("Categories") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AuraGold,
                unselectedIconColor = AuraTextSecondary
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onCartClick,
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("Cart") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AuraGold,
                unselectedIconColor = AuraTextSecondary
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AuraGold,
                unselectedIconColor = AuraTextSecondary
            )
        )
    }
}

@Composable
fun ShimmerProductItem(brush: Brush) {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}

@Composable
fun ShimmerLoading() {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.1f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.1f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Column {
        repeat(3) {
            ShimmerProductItem(brush = brush)
        }
    }
}
