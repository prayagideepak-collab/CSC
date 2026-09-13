package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag

@Composable
fun LiveAlertsTicker() {
    val marqueeText = "📌 NEW JOB: UPSSSC PET (Start: 01 Oct | Last: 25 Oct)   |   🎟️ ADMIT CARD: SSC CGL Tier-2 (Exam: 02 Nov)   |   🏆 RESULT: Railway RRB NTPC Final"
    
    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    val offsetX = infiniteTransition.animateFloat(
        initialValue = 800f,
        targetValue = -1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "marqueeOffset"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_alerts_ticker"),
        color = Color(0xFF0F172A), // #0F172A matching Flutter header concept
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFDC2626), // Red badge matching Flutter concept
                modifier = Modifier.fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LIVE ALERTS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clipToBounds()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = marqueeText,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .offset(x = offsetX.value.dp)
                )
            }
        }
    }
}
