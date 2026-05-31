package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.database.PetAccessory
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.components.NeoBrutalProgressBar
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel

@Composable
fun PetShopScreen(viewModel: PetBuddyViewModel) {
    val child by viewModel.activeChild.collectAsState()
    val accessoriesList by viewModel.accessories.collectAsState()

    var activeTab by remember { mutableStateOf("hat") } // "hat", "glasses", "clothes", "toy", "food"

    val filteredItems = accessoriesList.filter { it.type == activeTab }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalPaper)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "Sparkly Shop 🛍️",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
            }

            // Purse Balances indicator
            child?.let {
                Box(
                    modifier = Modifier
                        .background(NeoYellow, shape = RoundedCornerShape(12.dp))
                        .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("🪙 ${it.coins}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CATEGORY FILTER SWITCH TABS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf("hat" to "🧢", "glasses" to "🕶️", "clothes" to "🦸", "toy" to "🧸", "food" to "🍩")
            tabs.forEach { tab ->
                val isSel = activeTab == tab.first
                val tabBg = if (isSel) NeoPink else Color.White
                val borderW = if (isSel) 3.dp else 1.5.dp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(tabBg, shape = RoundedCornerShape(12.dp))
                        .border(width = borderW, color = BrutalBlack, shape = RoundedCornerShape(12.dp))
                        .clickable { activeTab = tab.first }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(tab.second, fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GRID OF FILTERED STORE ITEMS
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredItems) { item ->
                val itemBg = if (item.isEquipped) NeoYellow else if (item.isPurchased) LightGrey else Color.White

                NeoBrutalCard(
                    backgroundColor = itemBg,
                    shadowOffset = if (item.isEquipped) 2.dp else 4.dp,
                    modifier = Modifier.aspectRatio(0.9f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(item.emoji, fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Black, color = BrutalBlack)

                        Spacer(modifier = Modifier.height(4.dp))

                        if (!item.isPurchased) {
                            // PRICE BUY KEY
                            Box(
                                modifier = Modifier
                                    .background(NeoGreen, shape = RoundedCornerShape(10.dp))
                                    .border(width = 1.5.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                    .clickable { viewModel.purchaseItem(item) }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("${item.costCoins} 🪙 Buy", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        } else {
                            // EQUIPPED STATUS BUTTONS
                            Box(
                                modifier = Modifier
                                    .background(if (item.isEquipped) BrutalBlack else Color.White, shape = RoundedCornerShape(10.dp))
                                    .border(width = 1.5.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                    .clickable { viewModel.toggleAccessory(item) }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (item.isEquipped) "Equipped ✓" else "Wear It",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (item.isEquipped) Color.White else BrutalBlack
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
fun AdMobRewardScreen(viewModel: PetBuddyViewModel) {
    val isAdRunning by viewModel.isAdRunning.collectAsState()
    val adProgress by viewModel.adProgress.collectAsState()

    var adRewardsStarsType by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalPaper)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Retro projector / movie camera visual card
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .rotate(-5f)
                    .background(NeoOrange, shape = RoundedCornerShape(24.dp))
                    .border(width = 4.dp, color = BrutalBlack, shape = RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎬🍿🍿", fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "REWARDS CINEMA! 🍿",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Watch short playful cartoon commercials. Earn free pet companion spending tokens securely! Zero interstitial intrusive popup interruptions.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = DarkGrey,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ad player active view
            if (isAdRunning) {
                NeoBrutalCard(
                    backgroundColor = Color.White,
                    shadowOffset = 6.dp,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎬 STREAMING PARTNER VIDEO AD...",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = BrutalBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Simulating Google AdMob Rewarded SDK interface. Keep watching for full rewards payout!",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        // Custom percentage loader bar
                        NeoBrutalProgressBar(progress = adProgress, color = NeoYellow)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${(adProgress * 100).toInt()}% loaded",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = BrutalBlack
                        )
                    }
                }
            } else {
                // CHOICES TO RUN ADS
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Ad 1: Watch for Coins
                    NeoBrutalCard(
                        backgroundColor = Color.White,
                        shadowOffset = 4.dp,
                        onClick = {
                            adRewardsStarsType = false
                            viewModel.runRewardedAd(false)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪙", fontSize = 42.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("COIN SPLASH! 🪙🎬", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("Click to view simulated ad stream and receive +60 PetBuddy Coins!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                    }

                    // Ad 2: Watch for Stars
                    NeoBrutalCard(
                        backgroundColor = Color.White,
                        shadowOffset = 4.dp,
                        onClick = {
                            adRewardsStarsType = true
                            viewModel.runRewardedAd(true)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 42.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("STAR DECKER! ⭐🎬", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("Click to view simulated ad stream and receive +10 Gold stars! (Premium)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            NeoBrutalButton(
                onClick = { viewModel.navigateBack() },
                backgroundColor = Color.White,
                modifier = Modifier.width(150.dp)
            ) {
                Text("✖ Close Cinema", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
