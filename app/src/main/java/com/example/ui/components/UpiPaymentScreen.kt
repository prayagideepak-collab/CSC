package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UpiPaymentScreen(
    selectedAmount: Int,
    customAmount: String,
    upiStatus: String,
    onSelectAmount: (Int) -> Unit,
    onCustomAmountChange: (String) -> Unit,
    onSetUpiStatus: (String) -> Unit,
    onShareWhatsAppScreenshot: (String) -> Unit
) {
    val finalAmount = if (customAmount.isNotBlank()) (customAmount.toIntOrNull() ?: selectedAmount) else selectedAmount
    val upiIntentUri = "upi://pay?pa=9235939809@okbizaxis&pn=PrayagiKendra&am=$finalAmount&cu=INR"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Official UPI Payment Section",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "VPA: 9235939809@okbizaxis (Prayagi Kendra)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(12.dp))

        // QR Code Representation Card
        Card(
            modifier = Modifier.size(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Official QR",
                    modifier = Modifier.size(90.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scan & Pay ₹$finalAmount",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "9235939809@okbizaxis",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Amount Selector
        Text(text = "Select Service Amount:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(50, 150, 300).forEach { amt ->
                Button(
                    onClick = { onSelectAmount(amt) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedAmount == amt && customAmount.isBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("upi_amt_$amt")
                ) {
                    Text(
                        "₹$amt",
                        color = if (selectedAmount == amt && customAmount.isBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = customAmount,
            onValueChange = onCustomAmountChange,
            label = { Text("Or Enter Custom Amount (₹)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("custom_upi_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Payment status & WhatsApp screenshot share action
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = if (upiStatus == "Paid") Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (upiStatus == "Paid") {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Paid", tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Payment Status: PAID (₹$finalAmount)", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    } else {
                        Text("Payment Status: PENDING", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSetUpiStatus("Paid") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ive_paid_btn")
                    ) {
                        Text("I've Paid")
                    }

                    Button(
                        onClick = {
                            val msg = "Hello Prayagi Kendra, I have completed the UPI payment of ₹$finalAmount to 9235939809@okbizaxis. Here is my payment screenshot for service processing."
                            onShareWhatsAppScreenshot(msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_screenshot_whatsapp_btn")
                    ) {
                        Text("Share on WhatsApp")
                    }
                }
            }
        }
    }
}
