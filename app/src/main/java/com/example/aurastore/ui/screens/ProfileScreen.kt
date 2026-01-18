package com.example.aurastore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurastore.ui.theme.*

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBg)
    ) {
        // Luxury Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AuraMidnight, Color(0xFF1E1E2E))
                    )
                )
        ) {
            // Background Orbs
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-50).dp)
                    .size(200.dp)
                    .background(AuraGold.copy(alpha = 0.05f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with Premium Ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .border(2.dp, AuraGold, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MD", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Mohammed Dubai",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AuraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gold Member",
                    style = MaterialTheme.typography.labelMedium,
                    color = AuraGold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu Options
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ProfileMenuItem(
                icon = Icons.Outlined.ShoppingBag, 
                title = "My Orders", 
                subtitle = "View and track orders",
                onClick = onOrdersClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProfileMenuItem(
                icon = Icons.Outlined.FavoriteBorder, 
                title = "Wishlist", 
                subtitle = "Your favorite items",
                onClick = onWishlistClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProfileMenuItem(icon = Icons.Outlined.LocationOn, title = "Shipping Addresses", subtitle = "Manage delivery locations")
            Spacer(modifier = Modifier.height(12.dp))
            ProfileMenuItem(icon = Icons.Outlined.Settings, title = "Settings", subtitle = "App preferences")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Logout
            Surface(
                onClick = {},
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha=0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null, tint = Color.Red.copy(alpha=0.8f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", color = Color.Red.copy(alpha=0.8f), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Surface(
        color = AuraSurface,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AuraMidnight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AuraGold, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = AuraTextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = AuraTextSecondary, style = MaterialTheme.typography.labelSmall)
            }
            
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AuraTextSecondary)
        }
    }
}
