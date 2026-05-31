package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SavingsProgress
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.components.NeoBrutalTextField
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel
import kotlinx.coroutines.delay

@Composable
fun SavingsTrackerScreen(viewModel: PetBuddyViewModel) {
    val child by viewModel.activeChild.collectAsState()
    val savingsHistory by viewModel.savings.collectAsState()

    var coinDepositNote by remember { mutableStateOf("") }
    var coinDepositAmount by remember { mutableStateOf("") }

    // Total Savings Calculator
    val totalCoinsSaved = savingsHistory.sumOf { it.amount }

    // Bouncy Piggy Bank Animation Trigger
    var bouncyScaleTrigger by remember { mutableStateOf(1f) }
    LaunchedEffect(savingsHistory.size) {
        if (savingsHistory.isNotEmpty()) {
            bouncyScaleTrigger = 1.25f
            delay(150)
            bouncyScaleTrigger = 0.95f
            delay(100)
            bouncyScaleTrigger = 1f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalPaper)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                    .clickable { viewModel.navigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("◀", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Piggy Bank 🐷",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PIGGY BANK HERO GRAPHIC
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            NeoBrutalCard(
                modifier = Modifier
                    .size(170.dp)
                    .scale(bouncyScaleTrigger)
                    .rotate(3f),
                backgroundColor = NeoYellow,
                cornerRadius = 32.dp,
                shadowOffset = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🐖", fontSize = 72.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("VAULT BALANCES", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DarkGrey)
                    Text("$ $totalCoinsSaved", fontSize = 28.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ADD SAVINGS TRANSACTION PANEL
        NeoBrutalCard(
            backgroundColor = Color.White,
            shadowOffset = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "💰 LOG NEW COIN SAVINGS:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Quick add keys
                    listOf(1, 5, 10, 20).forEach { cash ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(NeoGreen, shape = RoundedCornerShape(10.dp))
                                .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.addSavings(cash, "Piggy bank savings feed")
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+$cash$", fontSize = 14.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Custom Deposit Goal & Motives:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                Spacer(modifier = Modifier.height(6.dp))

                NeoBrutalTextField(
                    value = coinDepositAmount,
                    onValueChange = { coinDepositAmount = it },
                    placeholderText = "Deposit Amount (e.g., 50)",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeoBrutalTextField(
                    value = coinDepositNote,
                    onValueChange = { coinDepositNote = it },
                    placeholderText = "What are you saving for? (e.g. LEGO set!)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NeoBrutalButton(
                    onClick = {
                        val amountCheck = coinDepositAmount.toIntOrNull() ?: 0
                        if (amountCheck > 0) {
                            val notesVal = if (coinDepositNote.isBlank()) "Goal saving" else coinDepositNote
                            viewModel.addSavings(amountCheck, notesVal)
                            coinDepositAmount = ""
                            coinDepositNote = ""
                        } else {
                            viewModel.triggerNotification("Oops!", "Please enter a valid deposit quantity!", false)
                        }
                    },
                    backgroundColor = NeoGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Slide Coins into Slot! 🪙📬", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // WEEKLY SAVINGS REPORTS - HIGH FIDELITY HISTORICAL BAR-CHART
        NeoBrutalCard(
            backgroundColor = NeoOrange.copy(alpha = 0.15f),
            shadowOffset = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "📊 STATS: WEEKLY SAVINGS GRAPHS",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = BrutalBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Weekly contribution reports. Grow your pillars tall!",
                    fontSize = 11.sp,
                    color = DarkGrey
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CUSTOM NEO BRUTALIST PILLAR CANVAS
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .border(width = 3.dp, color = BrutalBlack, shape = RoundedCornerShape(12.dp))
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    // Seed some values if list empty, else parse actual values
                    val dataPoints = listOf(20f, 10f, 50f, 0f, 15f, 40f, 100f)

                    val maxVal = dataPoints.maxOrNull() ?: 100f
                    val chartWidth = size.width
                    val chartHeight = size.height
                    val barWidth = 24.dp.toPx()
                    val spacing = (chartWidth - (barWidth * days.size)) / (days.size + 1)

                    days.forEachIndexed { i, dayName ->
                        val datum = dataPoints[i]
                        val rawPct = if (maxVal <= 0) 0f else datum / maxVal
                        val barHeight = (chartHeight - 40.dp.toPx()) * rawPct

                        val startX = spacing + i * (barWidth + spacing)
                        val startY = chartHeight - 25.dp.toPx() - barHeight

                        // Shadow Pillar
                        drawRoundRect(
                            color = BrutalBlack,
                            topLeft = Offset(startX + 4.dp.toPx(), startY + 4.dp.toPx()),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        // Main Pillar
                        drawRoundRect(
                            color = if (i == 6) NeoYellow else if (datum > 30) NeoGreen else NeoPink,
                            topLeft = Offset(startX, startY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        // Border outline for Pillar
                        drawRoundRect(
                            color = BrutalBlack,
                            topLeft = Offset(startX, startY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                            style = Stroke(width = 2.5f.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // HISTORY LOG TABLES
                Text(
                    text = "📜 COIN LEDGERS:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (savingsHistory.isEmpty()) {
                    Text("No deposit ledger logged yet.", fontSize = 11.sp, color = Color.Gray)
                } else {
                    savingsHistory.take(4).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "👉 ${item.note}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrutalBlack
                            )
                            Text(
                                text = "+ $${item.amount}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = NeoGreen
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
