package com.shardul.esewazone.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ConfirmationScreen(
    productNames: Array<String>,
    productPrices: Array<String>,
    deliveryAddress: String,
    paymentOption: String,
    vehicleNumber: String,
    deliveryCharge: String,
    totalAmount: String,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmation", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "CONFIRM",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = Color(0xFFF4F6F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Payment Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    for (i in productNames.indices) {
                        val name = productNames.getOrNull(i) ?: "Item"
                        val price = productPrices.getOrNull(i) ?: "0.00"

                        Text(
                            text = "Product item(${i + 1})",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        DetailRow(label = "Name", value = name, isBoldValue = true)
                        DetailRow(label = "Price", value = price, isBoldValue = true)
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    }

                    DetailRow(label = "Delivery Address", value = deliveryAddress, isBoldValue = true)
                    DetailRow(label = "Payment Option", value = paymentOption, isBoldValue = true)
                    DetailRow(label = "Vehicle Number", value = vehicleNumber, isBoldValue = true)
                    DetailRow(label = "Delivery Charge", value = deliveryCharge, isBoldValue = true)

                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Paying Amount",
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = totalAmount,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, isBoldValue: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(
            modifier = Modifier.weight(1f)
                .padding(start = 16.dp)
                .alignByBaseline(),
            textAlign = TextAlign.End,
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = if (isBoldValue) FontWeight.Bold else FontWeight.Normal
        )
    }
}