package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ChildProfile
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.components.NeoBrutalProgressBar
import com.example.ui.components.NeoBrutalTextField
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel

@Composable
fun ParentDashboardScreen(viewModel: PetBuddyViewModel) {
    val parent by viewModel.parentAccount.collectAsState()
    val profiles by viewModel.children.collectAsState()

    var unlockedParentDashboard by remember { mutableStateOf(false) }
    var enteredPasscode by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf(false) }

    val activeSelectedChildId by viewModel.parentSelectedChildId.collectAsState()
    val selectedChild = profiles.find { it.id == activeSelectedChildId }

    // Task and metrics calculated for parent
    val childTasks by viewModel.tasks.collectAsState()
    val childSavings by viewModel.savings.collectAsState()
    val childLearnings by viewModel.learning.collectAsState()

    // Seeds child selection on startup
    LaunchedEffect(profiles) {
        if (activeSelectedChildId == -1 && profiles.isNotEmpty()) {
            viewModel.parentSelectedChildId.value = profiles.first().id
        }
    }

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
                Text("◀", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Parent Cockpit 👨‍👩‍👧",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!unlockedParentDashboard) {
            // UNLOCK SECURE CODES VIEW
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(NeoPink, shape = RoundedCornerShape(20.dp))
                        .border(width = 3.dp, color = BrutalBlack, shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔐", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "GUARDIAN LOGIN REQUIRED",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )

                Text(
                    text = "A Parental Security check ensures children do not delete goals or modify custom rewards without supervision.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                NeoBrutalTextField(
                    value = enteredPasscode,
                    onValueChange = { if (it.length <= 4) enteredPasscode = it },
                    placeholderText = "Enter 4-Digit Parent Code",
                    keyboardType = KeyboardType.Number,
                    isPassword = true
                )

                if (codeError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Invalid Passcode. Enter the same code from onboarding setup.",
                        color = NeoPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                NeoBrutalButton(
                    onClick = {
                        val authParent = parent
                        if (authParent != null && enteredPasscode == authParent.securityCode) {
                            unlockedParentDashboard = true
                            codeError = false
                        } else {
                            codeError = true
                            viewModel.triggerNotification("Security Gate", "Access denied. passcodes must match!", false)
                        }
                    },
                    backgroundColor = NeoYellow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Unlock Parent Portal 📂✨", fontSize = 16.sp, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { viewModel.navigateBack() }) {
                    Text("◀ Go Back to Child View", fontWeight = FontWeight.Bold, color = BrutalBlack)
                }
            }
        } else {
            // FULL PARENT DASHBOARD DISCOVERY SUITE
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header profile capsule containing emails
                NeoBrutalCard(
                    backgroundColor = Color.White,
                    shadowOffset = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Administered By: ${parent?.name ?: "Parent"}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Connected Email: ${parent?.email ?: "parent@petbuddy.com"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kids Selector Tabs
                Text(
                    text = "SELECT SIBLING ADVENTURER TO MONITOR:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profiles.forEach { kid ->
                        val isSel = kid.id == activeSelectedChildId
                        val bg = if (isSel) NeoYellow else Color.White
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(bg, shape = RoundedCornerShape(12.dp))
                                .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.parentSelectedChildId.value = kid.id
                                    // Change ViewModel active selection so data flows appropriately!
                                    viewModel.switchActiveChild(kid.id)
                                }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(kid.name, fontSize = 14.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // METRICS & PROGRESS REPORTS DETAILS
                selectedChild?.let { kid ->
                    Text(
                        text = "📈 PROGRESS & HEALTH FOR ${kid.name.uppercase()}:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = BrutalBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Card with numbers
                    NeoBrutalCard(
                        backgroundColor = Color.White,
                        shadowOffset = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Completed Tasks Rate
                            val totalT = childTasks.size
                            val completedT = childTasks.filter { it.isCompleted }.size
                            val completionRate = if (totalT == 0) 0f else completedT.toFloat() / totalT

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Habit Missions Done:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                                Text("$completedT / $totalT completed (${(completionRate * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                            NeoBrutalProgressBar(progress = completionRate, color = NeoGreen, height = 14.dp)

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            // Cumulative Saving
                            val totalSavesVal = childSavings.sumOf { it.amount }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Saved Vault:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                                Text("$ $totalSavesVal logged savings", fontSize = 12.sp, fontWeight = FontWeight.Black, color = NeoGreen)
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            // Total Wordfact learnings count
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Intelligence logs acquired:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                                Text("${childLearnings.size} Words, facts or skills", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ADVANCED PARENTAL CONTROLS: EDIT HABIT MISSIONS DIRECT TRIGGER
                    Text(
                        text = "📋 DIRECT TASK OVERRIDES FOR ${kid.name.uppercase()}:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = BrutalBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    NeoBrutalCard(
                        backgroundColor = NeoOrange.copy(alpha = 0.2f),
                        shadowOffset = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Parents have full access of task control panels. Click to redirect or perform actions in child screen.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGrey
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            NeoBrutalButton(
                                onClick = { viewModel.navigateTo(viewModel.currentScreen.value) // refresh link
                                    viewModel.navigateTo(viewModel.currentScreen.value) // redundant trigger to flush view
                                    viewModel.navigateTo(com.example.viewmodel.Screen.Tasks)
                                },
                                backgroundColor = NeoYellow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open Mission Manager Panel ⬅️", fontSize = 13.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Child Safety Features explanationcard
                NeoBrutalCard(
                    backgroundColor = NeoGreen.copy(alpha = 0.15f),
                    shadowOffset = 4.dp,
                    modifier = Modifier.fillMaxWidth().rotate(-0.5f)
                ) {
                    Column {
                        Text(
                            text = "🛡️ CHILD SAFETY & LOCAL COMPLIANCE",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = BrutalBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PetBuddy Kids adheres strictly to child identity protections: No data ever leaves this device, camera captures are simulated using fun vector stickers, and outbound communication has been fully blocked to ensure perfect local safety sandbox compliance. We protect your kids' focus!",
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                NeoBrutalButton(
                    onClick = { unlockedParentDashboard = false },
                    backgroundColor = BrutalBlack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lock Portal & Go Back", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
