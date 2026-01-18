package com.example.aurastore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aurastore.ui.theme.AuraStoreTheme

import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aurastore.ui.navigation.LoginRoute
import com.example.aurastore.ui.navigation.HomeRoute
import com.example.aurastore.ui.screens.LoginScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraStoreTheme {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController = navController, startDestination = LoginRoute) {
                            composable<LoginRoute> {
                                LoginScreen(
                                    onLoginClick = {
                                        navController.navigate(HomeRoute) {
                                            popUpTo(LoginRoute) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            
                            composable<HomeRoute> {
                                com.example.aurastore.ui.screens.HomeScreen(
                                    onProductClick = { productId -> 
                                        navController.navigate(com.example.aurastore.ui.navigation.ProductDetailRoute(productId)) 
                                    },
                                    onCartClick = { navController.navigate(com.example.aurastore.ui.navigation.CartRoute) },
                                    onProfileClick = { navController.navigate(com.example.aurastore.ui.navigation.ProfileRoute) },
                                    onSearchClick = { navController.navigate(com.example.aurastore.ui.navigation.SearchRoute) }
                                )
                            }
                            
                            composable<com.example.aurastore.ui.navigation.ProductDetailRoute> {
                                com.example.aurastore.ui.screens.ProductDetailScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            
                            composable<com.example.aurastore.ui.navigation.CartRoute> {
                                com.example.aurastore.ui.screens.CartScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onCheckoutClick = { navController.navigate(com.example.aurastore.ui.navigation.CheckoutRoute) }
                                )
                            }
                            
                            composable<com.example.aurastore.ui.navigation.CheckoutRoute> {
                                com.example.aurastore.ui.screens.CheckoutScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onPaymentSuccess = {
                                        // Navigate back to Home and clear stack
                                        navController.navigate(HomeRoute) {
                                            popUpTo(HomeRoute) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            
                            composable<com.example.aurastore.ui.navigation.ProfileRoute> {
                                com.example.aurastore.ui.screens.ProfileScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onWishlistClick = { navController.navigate(com.example.aurastore.ui.navigation.WishlistRoute) },
                                    onOrdersClick = { navController.navigate(com.example.aurastore.ui.navigation.OrdersRoute) }
                                )
                            }
                            
                            composable<com.example.aurastore.ui.navigation.OrdersRoute> {
                                com.example.aurastore.ui.screens.OrderHistoryScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            
                            composable<com.example.aurastore.ui.navigation.WishlistRoute> {
                                com.example.aurastore.ui.screens.WishlistScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onProductClick = { productId ->
                                        navController.navigate(com.example.aurastore.ui.navigation.ProductDetailRoute(productId))
                                    }
                                )
                            }
                            
                            composable<com.example.aurastore.ui.navigation.SearchRoute> {
                                com.example.aurastore.ui.screens.SearchScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onProductClick = { productId -> 
                                        navController.navigate(com.example.aurastore.ui.navigation.ProductDetailRoute(productId)) 
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AuraStoreTheme {
        Greeting("Android")
    }
}