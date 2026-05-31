package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeoBrutalCard
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel
import com.example.viewmodel.Screen

@Composable
fun PetBuddyApp(
    viewModel: PetBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val currentRoute by viewModel.currentScreen.collectAsState()
    val listNotifs = viewModel.notifications

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrutalPaper)
    ) {
        // Core view routing switcher
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentRoute) {
                is Screen.Welcome -> WelcomeScreen(viewModel)
                is Screen.ChoosePet -> ChoosePetScreen(viewModel)
                is Screen.ChooseAge -> ChooseAgeScreen(viewModel)
                is Screen.ParentSetup -> ParentSetupScreen(viewModel)
                is Screen.Dashboard -> DashboardScreen(viewModel)
                is Screen.Tasks -> TasksScreen(viewModel)
                is Screen.WaterTracker -> WaterTrackerScreen(viewModel)
                is Screen.SavingsTracker -> SavingsTrackerScreen(viewModel)
                is Screen.LearningTracker -> LearningTrackerScreen(viewModel)
                is Screen.MemoryJournal -> MemoryJournalScreen(viewModel)
                is Screen.PetShop -> PetShopScreen(viewModel)
                is Screen.ParentDashboard -> ParentDashboardScreen(viewModel)
                is Screen.AdMobReward -> AdMobRewardScreen(viewModel)
            }
        }

        // --- FLOATING IN-APP NOTIFICATION OVERLAY PANEL ---
        if (listNotifs.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .safeDrawingPadding()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                listNotifs.forEach { info ->
                    NeoBrutalCard(
                        backgroundColor = if (info.isSuccess) NeoYellow else NeoPink,
                        borderColor = BrutalBlack,
                        shadowColor = BrutalBlack,
                        shadowOffset = 4.dp,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (info.isSuccess) "🎉" else "🚨",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = info.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrutalBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = info.message,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGrey
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
