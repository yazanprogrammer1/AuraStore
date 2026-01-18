package com.example.aurastore.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.rotate
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aurastore.common.Resource
import com.example.aurastore.ui.theme.*
import com.example.aurastore.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val searchState by viewModel.searchState.collectAsState()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(AuraDarkBg)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AuraTextSecondary)
                    }

                    AuraTextField(
                        value = query,
                        onValueChange = { 
                            query = it
                            viewModel.onSearchQueryChange(it) 
                        },
                        label = "Search luxury items...",
                        icon = Icons.Default.Search,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { /* Done */ })
                    )

                    if (query.isNotEmpty()) {
                        IconButton(onClick = { 
                            query = ""
                            viewModel.onSearchQueryChange("")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = AuraTextSecondary)
                        }
                    }
                }
            }
        },
        containerColor = AuraDarkBg
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when(val state = searchState) {
                is Resource.Loading -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator(color = AuraGold)
                     }
                }
                is Resource.Error -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         Text(state.message ?: "Search failed", color = Color.Red)
                     }
                }
                is Resource.Success -> {
                    val products = state.data ?: emptyList()
                    if (products.isEmpty() && query.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No items found", color = AuraTextSecondary)
                        }
                    } else if (products.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(products) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                    width = 18.dp
                                )
                            }
                        }
                    } else {
                        // Empty State / Suggestions
                         LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Trending Section
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AuraGold, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Trending Now", style = MaterialTheme.typography.titleMedium, color = AuraTextPrimary)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(listOf("Rolex", "Gucci", "Dior", "Gold", "Perfume")) { trend ->
                                        SuggestionChip(
                                            onClick = { 
                                                query = trend 
                                                viewModel.onSearchQueryChange(trend)
                                            },
                                            label = { Text(trend, color = AuraTextPrimary) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = AuraSurface,
                                                labelColor = AuraTextPrimary
                                            ),
                                            border = BorderStroke(1.dp, AuraTextSecondary.copy(alpha=0.3f))
                                        )
                                    }
                                }
                            }
                
                            // Recent Searches
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = AuraTextSecondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Recent Searches", style = MaterialTheme.typography.titleMedium, color = AuraTextPrimary)
                                }
                            }
                
                            items(listOf("Vintage Watch", "Leather Wallet")) { recent ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            query = recent
                                            viewModel.onSearchQueryChange(recent)
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(recent, color = AuraTextSecondary, modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = AuraTextSecondary.copy(alpha=0.3f), modifier = Modifier.size(16.dp).rotate(135f)) // Arrow pointing up-left
                                }
                                Divider(color = AuraSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}
