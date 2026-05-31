package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Task
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.components.NeoBrutalTextField
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel

@Composable
fun TasksScreen(viewModel: PetBuddyViewModel) {
    val child by viewModel.activeChild.collectAsState()
    val parent by viewModel.parentAccount.collectAsState()
    val tasksList by viewModel.tasks.collectAsState()

    // Passcode states
    var isParentUnlocked by remember { mutableStateOf(false) }
    var enteredPasscode by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    // Custom Task Creators
    var customTaskTitle by remember { mutableStateOf("") }
    var customTaskCategory by remember { mutableStateOf("Chores") }
    var customTaskCoins by remember { mutableStateOf("25") }
    var customTaskStars by remember { mutableStateOf("2") }

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
                Text("◀", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrutalBlack)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Mission Control 🚀",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Age Category Prompt
        child?.let {
            NeoBrutalCard(
                backgroundColor = NeoYellow,
                shadowOffset = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Habit Category: [${it.ageGroup}] — Complete missions to make ${it.petCustomName} happy!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SCROLLABLE TASK LIST
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tasksList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No missions created yet! Tell your Parent to add custom tasks using the Parent Panel at the bottom!",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(tasksList) { task ->
                    val taskBg = if (task.isCompleted) NeoGreen.copy(alpha = 0.5f) else Color.White
                    val decor = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None

                    NeoBrutalCard(
                        backgroundColor = taskBg,
                        shadowOffset = if (task.isCompleted) 2.dp else 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .rotate(if (task.isCompleted) 0.5f else -0.5f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Heavy checkbox
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = if (task.isCompleted) BrutalBlack else Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(width = 3.dp, color = BrutalBlack, shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (task.isCompleted) {
                                            viewModel.resetTaskForDemo(task)
                                        } else {
                                            viewModel.completeTask(task)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (task.isCompleted) {
                                    Text("✓", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrutalBlack,
                                    textDecoration = decor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Rewards: +${task.coinsReward}🪙  +${task.starsReward}⭐",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGrey
                                )
                            }

                            // Parent-Only Delete Button (if custom task & Parent mode is unlocked!)
                            if (isParentUnlocked && task.isCustomCreated) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(NeoPink, shape = RoundedCornerShape(8.dp))
                                        .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(8.dp))
                                        .clickable { viewModel.parentDeleteTask(task.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🗑️", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- PARENT IN-PAGE CUSTOM TASK PANEL GATED BY 4-DIGIT CODE ---
        if (!isParentUnlocked) {
            NeoBrutalCard(
                backgroundColor = LightGrey,
                shadowOffset = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔒", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Custom Missions? (Parent Only)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = BrutalBlack
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            NeoBrutalTextField(
                                value = enteredPasscode,
                                onValueChange = { if (it.length <= 4) enteredPasscode = it },
                                placeholderText = "Enter Parent Code (4 Digits)",
                                keyboardType = KeyboardType.Number,
                                isPassword = true
                            )
                        }

                        NeoBrutalButton(
                            onClick = {
                                val actualParent = parent
                                if (actualParent != null && enteredPasscode == actualParent.securityCode) {
                                    isParentUnlocked = true
                                    passwordError = false
                                } else {
                                    passwordError = true
                                    viewModel.triggerNotification("Lock Check!", "Incorrect 4-digit code!", false)
                                }
                            },
                            backgroundColor = NeoPink,
                            shadowOffset = 3.dp
                        ) {
                            Text("Unlock", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (passwordError) {
                        Text(
                            text = "Wrong passcode. Check Parent Config or retry.",
                            color = NeoPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            // Unlocked Parent CRUD Controls
            NeoBrutalCard(
                backgroundColor = NeoOrange.copy(alpha = 0.2f),
                shadowOffset = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛠️ PARENT MISSION CREATOR ACTIVE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = BrutalBlack
                        )
                        Text(
                            text = "[Lock Panel]",
                            color = NeoPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isParentUnlocked = false
                                enteredPasscode = ""
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    NeoBrutalTextField(
                        value = customTaskTitle,
                        onValueChange = { customTaskTitle = it },
                        placeholderText = "Mission Name (e.g., Water the Garden 🪴)"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row of parameters
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Category switcher dropdown emulator
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, shape = RoundedCornerShape(10.dp))
                                .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    customTaskCategory = if (customTaskCategory == "Chores") "Learning" else if (customTaskCategory == "Learning") "Daily Habits" else "Chores"
                                }
                                .padding(10.dp)
                        ) {
                            Text("📁 $customTaskCategory", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                        }

                        // Coins Reward
                        Box(modifier = Modifier.weight(0.5f)) {
                            NeoBrutalTextField(
                                value = customTaskCoins,
                                onValueChange = { customTaskCoins = it },
                                placeholderText = "Coins",
                                keyboardType = KeyboardType.Number
                            )
                        }

                        // Stars Reward
                        Box(modifier = Modifier.weight(0.5f)) {
                            NeoBrutalTextField(
                                value = customTaskStars,
                                onValueChange = { customTaskStars = it },
                                placeholderText = "Stars",
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    NeoBrutalButton(
                        onClick = {
                            val cLimit = customTaskCoins.toIntOrNull() ?: 20
                            val sLimit = customTaskStars.toIntOrNull() ?: 2
                            child?.let { activeKid ->
                                viewModel.parentSelectedChildId.value = activeKid.id
                                viewModel.parentAddTask(customTaskTitle, customTaskCategory, cLimit, sLimit)
                                customTaskTitle = ""
                            }
                        },
                        backgroundColor = NeoGreen,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Custom Task To Kid's List! 🎉", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
