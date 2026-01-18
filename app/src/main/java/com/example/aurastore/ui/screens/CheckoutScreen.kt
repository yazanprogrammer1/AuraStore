package com.example.aurastore.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurastore.ui.theme.AuraGold
import com.example.aurastore.ui.theme.AuraMidnight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: com.example.aurastore.ui.viewmodel.CheckoutViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") } // Added Address field
    var isFlipped by remember { mutableStateOf(false) }

    val orderState by viewModel.orderState.collectAsState()
    val checkoutTotal by viewModel.checkoutTotal.collectAsState()
    val isProcessing = orderState is com.example.aurastore.common.Resource.Loading

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "cardFlip"
    )

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    // ...

    Button(
        onClick = {
            viewModel.createOrder(
                address = address.ifBlank { "Unknown Address" },
                cardHolder = cardHolder
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isProcessing && checkoutTotal > 0,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AuraGold, contentColor = AuraMidnight)
    ) {
        if (isProcessing) {
            CircularProgressIndicator(color = AuraMidnight, modifier = Modifier.size(24.dp))
        } else {
            Text("PAY $${checkoutTotal}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CreditCardFront(number: String, holder: String, expiry: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(AuraMidnight, Color(0xFF2C3E50), AuraGold.copy(alpha = 0.3f))
                )
            )
            .padding(24.dp)
    ) {
        // Chip
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(40.dp, 30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFDAA520)))
                )
        )

        // Visa Logo Placeholder
        Text(
            "VISA",
            modifier = Modifier.align(Alignment.TopEnd),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            fontSize = 24.sp
        )

        // Number
        Text(
            text = number.padEnd(16, '*').chunked(4).joinToString(" "),
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp
        )

        // Details
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Card Holder", color = Color.Gray, fontSize = 10.sp)
                Text(
                    holder.ifEmpty { "YOUR NAME" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Column {
                Text("Expires", color = Color.Gray, fontSize = 10.sp)
                Text(
                    expiry.ifEmpty { "MM/YY" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CreditCardBack(cvv: String, modifier: Modifier.() -> Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(AuraMidnight)
            .modifier() // Apply rotation fix
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(30.dp))
            // Black Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Black)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // CVV Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .height(40.dp)
                        .background(Color.Gray)
                )
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .height(40.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cvv,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
