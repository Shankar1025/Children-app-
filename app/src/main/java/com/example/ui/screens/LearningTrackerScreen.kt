package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.components.NeoBrutalTextField
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel

@Composable
fun LearningTrackerScreen(viewModel: PetBuddyViewModel) {
    val learningList by viewModel.learning.collectAsState()

    var learningType by remember { mutableStateOf("Word") } // "Word", "Fact", "Skill"
    var learnContent by remember { mutableStateOf("") }
    var learnDetails by remember { mutableStateOf("") }

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
                text = "Brain Academy 🧠",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // INPUT CONSOLE FOR LEARNINGS
        NeoBrutalCard(
            backgroundColor = Color.White,
            shadowOffset = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "📖 LOG NEW INTELLIGENCE ACQUISITION:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Tab Switchers (brutalist styles)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Word" to "📝 New Word", "Fact" to "💡 Cool Fact", "Skill" to "🎨 New Skill").forEach { tab ->
                        val isSel = learningType == tab.first
                        val tabBg = if (isSel) NeoYellow else LightGrey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(tabBg, shape = RoundedCornerShape(10.dp))
                                .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                .clickable { learningType = tab.first }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(tab.second, fontSize = 11.sp, fontWeight = FontWeight.Black, color = BrutalBlack)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val nameLabel = when (learningType) {
                    "Word" -> "What is the new Word? (e.g. Gravity)"
                    "Fact" -> "What is the Fact topic? (e.g. Blue Whales)"
                    else -> "What is the Skill? (e.g. Tied my shoes!)"
                }

                val detailsLabel = when (learningType) {
                    "Word" -> "What does this word mean?"
                    "Fact" -> "Details or Cool facts. (e.g. Big hearts!)"
                    else -> "How did you carry this out? (e.g. Loop to loop)"
                }

                Text(nameLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                Spacer(modifier = Modifier.height(4.dp))
                NeoBrutalTextField(
                    value = learnContent,
                    onValueChange = { learnContent = it },
                    placeholderText = "Enter Topic Title..."
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(detailsLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                Spacer(modifier = Modifier.height(4.dp))
                NeoBrutalTextField(
                    value = learnDetails,
                    onValueChange = { learnDetails = it },
                    placeholderText = "Explain or define details here...",
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                NeoBrutalButton(
                    onClick = {
                        if (learnContent.isNotBlank()) {
                            viewModel.saveLearnedTopic(learningType, learnContent, learnDetails)
                            learnContent = ""
                            learnDetails = ""
                        } else {
                            viewModel.triggerNotification("Oops!", "Please fill in the topic title!", false)
                        }
                    },
                    backgroundColor = NeoPink,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seal in Brain Academy! 🧠🔒", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // HISTORICAL ACHIEVEMENTS DRAWER
        Text(
            text = "🌟 MY BRAIN DISCOVERY CHEST:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = BrutalBlack,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (learningList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Brain Chest is empty! Log your first word or fact to start growing your pet and collecting stars!",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(learningList) { record ->
                    val (recBg, emoji) = when (record.type) {
                        "Word" -> NeoYellow to "📝"
                        "Fact" -> NeoBlue to "💡"
                        else -> NeoGreen to "🎨"
                    }

                    NeoBrutalCard(
                        backgroundColor = recBg,
                        shadowOffset = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .rotate(if (record.type == "Fact") 0.5f else -0.5f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(emoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "[${record.type}] - ${record.content}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrutalBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = record.details,
                                    fontSize = 12.sp,
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
