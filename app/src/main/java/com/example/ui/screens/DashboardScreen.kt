package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ChildProfile
import com.example.ui.components.CutePetComponent
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.components.NeoBrutalProgressBar
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel
import com.example.viewmodel.Screen

@Composable
fun DashboardScreen(viewModel: PetBuddyViewModel) {
    val child by viewModel.activeChild.collectAsState()
    val profiles by viewModel.children.collectAsState()
    val accessories by viewModel.accessories.collectAsState()

    val tasks by viewModel.tasks.collectAsState()
    val waterLog by viewModel.waterLogToday.collectAsState()
    val savingsHistory by viewModel.savings.collectAsState()
    val memoriesHistory by viewModel.memories.collectAsState()
    val learningLogs by viewModel.learning.collectAsState()

    var showProfileSwitcher by remember { mutableStateOf(false) }

    // Dialog state for adding a new sibling child on-the-spot
    var showAddSiblingDialog by remember { mutableStateOf(false) }
    var newChildName by remember { mutableStateOf("") }
    var newChildAgeGroup by remember { mutableStateOf("6-8 Years") }
    var newChildPetType by remember { mutableStateOf("panda") }
    var newChildPetCustomName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalPaper)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // --- BENTO GRID TOP HEADER BAR ---
            child?.let { activeKid ->
                NeoBrutalCard(
                    backgroundColor = Color.White,
                    shadowOffset = 4.dp,
                    onClick = { showProfileSwitcher = !showProfileSwitcher },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Avatar and Profile Details
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val petEmoji = when(activeKid.petId.lowercase()) {
                                "panda" -> "🐼"
                                "fox" -> "🦊"
                                "unicorn" -> "🦄"
                                "dinosaur" -> "🦖"
                                "puppy" -> "🐶"
                                "cat" -> "🐱"
                                "rabbit" -> "🐰"
                                "turtle" -> "🐢"
                                else -> "🦁"
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color = NeoPink, shape = RoundedCornerShape(50))
                                    .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(petEmoji, fontSize = 20.sp)
                            }

                            Column {
                                Text(
                                    text = "LEVEL ${activeKid.level}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Gray
                                )
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = activeKid.name.uppercase(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrutalBlack
                                    )
                                    Text(
                                        text = "▼",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrutalBlack
                                    )
                                }
                            }
                        }

                        // Right: Stats and Coins Badges
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                                    .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("⭐", fontSize = 12.sp)
                                    Text("${activeKid.stars}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                                    .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🪙", fontSize = 12.sp)
                                    Text("${activeKid.coins}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                                }
                            }
                        }
                    }
                }
            }

            // Expanded Sibling switcher
            if (showProfileSwitcher) {
                Spacer(modifier = Modifier.height(12.dp))
                NeoBrutalCard(
                    backgroundColor = Color.White,
                    shadowOffset = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Family Profiles (Parent Account):",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = BrutalBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        profiles.forEach { childProfile ->
                            val isCurrent = childProfile.id == viewModel.activeChildId.collectAsState().value
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) NeoYellow.copy(alpha = 0.4f) else Color.Transparent)
                                    .clickable {
                                        viewModel.switchActiveChild(childProfile.id)
                                        showProfileSwitcher = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isCurrent) "⭐" else "👤",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${childProfile.name} (Age ${childProfile.ageGroup.substringBefore(" ")})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BrutalBlack
                                    )
                                    Text(
                                        text = "Companion: ${childProfile.petCustomName}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Add Sibling Button
                        NeoBrutalButton(
                            onClick = {
                                newChildName = ""
                                newChildPetCustomName = ""
                                showAddSiblingDialog = true
                            },
                            backgroundColor = NeoGreen,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Add Child Profile", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LEVEL XP PROGRESS BAR ---
            child?.let { activeKid ->
                val levelNeededXp = activeKid.level * 100
                val progress = activeKid.xp.toFloat() / levelNeededXp

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Pet Growth Meter:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = BrutalBlack
                        )
                        Text(
                            text = "${activeKid.xp} / ${levelNeededXp} XP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    NeoBrutalProgressBar(progress = progress, color = NeoGreen)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- CORE PET COMPANION PORTRAIT ---
            child?.let { activeKid ->
                CutePetComponent(child = activeKid, equippedAccessories = accessories)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- PET CARE FEED / PLAY / SLEEP BUTTONS ---
            child?.let { activeKid ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Feed
                    NeoBrutalButton(
                        onClick = { viewModel.feedPetFast() },
                        backgroundColor = NeoOrange,
                        modifier = Modifier.weight(1f),
                        shadowOffset = 4.dp
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🍎", fontSize = 22.sp)
                            Text("Feed Custom\n(15 🪙)", fontSize = 11.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                        }
                    }

                    // Play
                    NeoBrutalButton(
                        onClick = { viewModel.playWithPetFast() },
                        backgroundColor = NeoYellow,
                        modifier = Modifier.weight(1f),
                        shadowOffset = 4.dp
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚽", fontSize = 22.sp)
                            Text("Play Game\n(+5 XP)", fontSize = 11.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                        }
                    }

                    // Sleep
                    NeoBrutalButton(
                        onClick = { viewModel.putPetToSleep() },
                        backgroundColor = NeoBlue,
                        modifier = Modifier.weight(1f),
                        shadowOffset = 4.dp
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("😴", fontSize = 22.sp)
                            Text("Put to Sleep\n(Full Energy)", fontSize = 11.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "KID QUEST JOURNAL & DISCOVERIES:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Grid of fast destinations (Bento Grid Style)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Row 1: Habit Mission & Water Tracker
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    // Habit missions card
                    val doneTasks = tasks.count { it.isCompleted }
                    val totalTasks = tasks.size
                    NeoBrutalCard(
                        backgroundColor = NeoGreen,
                        onClick = { viewModel.navigateTo(Screen.Tasks) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("📋 Habit Missions", fontSize = 15.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                            Text("GOAL: $doneTasks/$totalTasks DONE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = BrutalBlack.copy(alpha = 0.7f))
                            
                            // Mini mission checklist items
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (tasks.isEmpty()) {
                                    Text("No tasks yet!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrutalBlack.copy(alpha = 0.6f))
                                } else {
                                    tasks.take(2).forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp))
                                                .padding(4.dp)
                                        ) {
                                            Text(
                                                text = if (item.isCompleted) "✓" else "○",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = BrutalBlack
                                            )
                                            Text(
                                                text = item.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrutalBlack,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Water Tracker Card
                    val waterDrank = waterLog?.glassesDrank ?: 0
                    val waterGoal = waterLog?.goalGlasses ?: 8
                    NeoBrutalCard(
                        backgroundColor = Color.White,
                        onClick = { viewModel.navigateTo(Screen.WaterTracker) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("💧 Water Tracker", fontSize = 15.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                            Text("DRANK: $waterDrank/$waterGoal CUPS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                            
                            // Mini water visual cups grid
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                repeat(5) { index ->
                                    val isFilled = index < waterDrank
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(
                                                color = if (isFilled) NeoBlue else Color.LightGray.copy(alpha = 0.4f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .border(width = 1.dp, color = BrutalBlack, shape = RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }
                    }
                }

                // Row 2: Piggy Savings & Brain Learnings
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    // Piggy card
                    val totalSaved = savingsHistory.sumOf { it.amount }
                    NeoBrutalCard(
                        backgroundColor = NeoOrange,
                        onClick = { viewModel.navigateTo(Screen.SavingsTracker) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🐷 Piggy Bank", fontSize = 15.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                            Text("SAVED: $$totalSaved", fontSize = 20.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                            Text("Grow your physical vault!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrutalBlack.copy(alpha = 0.7f))
                        }
                    }

                    // Brain Boosters card
                    val factsCount = learningLogs.size
                    NeoBrutalCard(
                        backgroundColor = NeoPink,
                        onClick = { viewModel.navigateTo(Screen.LearningTracker) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🧠 Brain Boost", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("$factsCount FACTS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Trivia & facts logged!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }

                // Row 3: Memories & Pet Store
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    // Memories card
                    val diaryCount = memoriesHistory.size
                    NeoBrutalCard(
                        backgroundColor = NeoYellow,
                        onClick = { viewModel.navigateTo(Screen.MemoryJournal) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("📸 Memory Diary", fontSize = 15.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                            Text("$diaryCount SNAPS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                            Text("Moods & Voice notes!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrutalBlack.copy(alpha = 0.7f))
                        }
                    }

                    // Sparkly Shop Card
                    val activeAccessories = accessories.filter { it.isEquipped }.size
                    NeoBrutalCard(
                        backgroundColor = Color.White,
                        onClick = { viewModel.navigateTo(Screen.PetShop) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🛍️ Sparkly Shop", fontSize = 15.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                            Text("WEARING: $activeAccessories", fontSize = 20.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                            Text("Hats & Glasses!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- CHROME FOR PUSH NOTIFICATION SIMULATORS ---
            NeoBrutalCard(
                backgroundColor = LightGrey,
                shadowOffset = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "🔔 DEMO: PUSH NOTIFICATION SIMULATORS",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = BrutalBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap any button to test and trigger immediate child notifications as beautiful in-app alerting banners!",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, shape = RoundedCornerShape(10.dp))
                                .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.triggerNotification(
                                        "Puppy Alert! 🐶",
                                        "Your puppy Barnaby is waiting! Click to play a quick tag match!"
                                    )
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎮 Play Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrutalBlack, textAlign = TextAlign.Center)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, shape = RoundedCornerShape(10.dp))
                                .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.triggerNotification(
                                        "Time to Drink! 💧",
                                        "Time to drink water! Support your Water Hero Streak goal today!"
                                    )
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💧 Water Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrutalBlack, textAlign = TextAlign.Center)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, shape = RoundedCornerShape(10.dp))
                                .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.triggerNotification(
                                        "Daily Missions Ready! 🎒",
                                        "Today's custom habit checklist is ready! Collect +50 coins."
                                    )
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📋 Habit Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrutalBlack, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BOTTOM PARENT LOCK & CINEMA ADS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NeoBrutalButton(
                    onClick = { viewModel.navigateTo(Screen.ParentDashboard) },
                    backgroundColor = NeoOrange,
                    modifier = Modifier.weight(1.1f),
                    shadowOffset = 4.dp
                ) {
                    Text("👨‍👩‍👧 Parent Control Panel", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                }

                NeoBrutalButton(
                    onClick = { viewModel.navigateTo(Screen.AdMobReward) },
                    backgroundColor = NeoYellow,
                    modifier = Modifier.weight(0.9f),
                    shadowOffset = 4.dp
                ) {
                    Text("🎬 Free Rewards Cinema", fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // --- ADD SIBLING DIALOG DIRECT INJECTION ---
    if (showAddSiblingDialog) {
        AlertDialog(
            onDismissRequest = { showAddSiblingDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (newChildName.isNotBlank() && newChildPetCustomName.isNotBlank()) {
                            viewModel.addNewChildProfile(
                                name = newChildName,
                                ageGroup = newChildAgeGroup,
                                petId = newChildPetType,
                                petCustomName = newChildPetCustomName
                            )
                            showAddSiblingDialog = false
                            showProfileSwitcher = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrutalBlack)
                ) {
                    Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSiblingDialog = false }) {
                    Text("Cancel", color = BrutalBlack, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("Adopt Sibling Pet Companion! 🍼", fontWeight = FontWeight.Black, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newChildName,
                        onValueChange = { newChildName = it },
                        label = { Text("Sibling Name") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newChildPetCustomName,
                        onValueChange = { newChildPetCustomName = it },
                        label = { Text("Pet Nickname") },
                        singleLine = true
                    )

                    Text("Selected Pet type:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("panda" to "🐼", "fox" to "🦊", "unicorn" to "🦄", "dinosaur" to "🦖").forEach { item ->
                            val activeVal = newChildPetType == item.first
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = if (activeVal) NeoYellow else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(8.dp))
                                    .clickable { newChildPetType = item.first }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.second, fontSize = 20.sp)
                            }
                        }
                    }

                    Text("Selected Age group:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("3-5 Years", "6-8 Years", "9-12 Years", "13-15 Years").forEach { gr ->
                            val activeVal = newChildAgeGroup == gr
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (activeVal) NeoPink else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(width = 1.dp, color = BrutalBlack, shape = RoundedCornerShape(8.dp))
                                    .clickable { newChildAgeGroup = gr }
                                    .padding(vertical = 4.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(gr.substringBefore(" "), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        )
    }
}
