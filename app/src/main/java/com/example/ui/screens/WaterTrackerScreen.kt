package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AnimatedWaterGlass
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel

@Composable
fun WaterTrackerScreen(viewModel: PetBuddyViewModel) {
    val child by viewModel.activeChild.collectAsState()
    val waterLog by viewModel.waterLogToday.collectAsState()
    val waterHistory by viewModel.waterHistory.collectAsState()

    // Calculate goals
    val drank = waterLog?.glassesDrank ?: 0
    val goal = waterLog?.goalGlasses ?: when (child?.ageGroup) {
        "3-5 Years" -> 4
        "6-8 Years" -> 6
        "9-12 Years" -> 8
        else -> 8
    }

    // Streaks calculations
    val consecutiveDays = waterHistory.filter { it.glassesDrank >= it.goalGlasses }.size
    val dailyStreak = if (drank >= goal) consecutiveDays.coerceAtLeast(1) else consecutiveDays

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
                text = "Water Tracker 💧",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // MAIN HERO COMPONENT: ANIMATED WAVE CUP
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedWaterGlass(glassesFilled = drank, goal = goal)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ADHUSTMENT CONTROLS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeoBrutalButton(
                onClick = { viewModel.reduceWater() },
                backgroundColor = NeoPink,
                shadowOffset = 4.dp
            ) {
                Text("Spill 1 Glass 💧 ⬇", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.width(20.dp))

            NeoBrutalButton(
                onClick = { viewModel.drinkGlass() },
                backgroundColor = NeoBlue,
                shadowOffset = 4.dp
            ) {
                Text("Drink 1 Glass! 🥛 ⬆", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // WATER STREAKS CARD
        NeoBrutalCard(
            backgroundColor = NeoGreen,
            shadowOffset = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "🔥 HYDRATION STREAK CALENDAR",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = BrutalBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "You are currently on a $dailyStreak-day water streak! Keep drinking consecutive days to hit the [Water Hero] badge category!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrutalBlack
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Fun row of calendar days
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    days.forEachIndexed { i, dName ->
                        // Simulate completed days
                        val checkDayVal = i < dailyStreak || (i == 4 && drank >= goal)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (checkDayVal) BrutalBlack else Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dName,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (checkDayVal) Color.White else Color.Gray
                                )
                                Text(
                                    text = if (checkDayVal) "💧" else "⚪",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SCIENCE TIP CARD
        NeoBrutalCard(
            backgroundColor = Color.White,
            shadowOffset = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .rotate(-1f)
        ) {
            Column {
                Text(
                    text = "💡 Cool Water Fact!",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = BrutalBlack
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your brain is made of about 73% water! Drinking water helps you think faster, run speedier, and gives ${child?.petCustomName ?: "your pet"} a great energy boost to prevent sleepiness!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGrey
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
