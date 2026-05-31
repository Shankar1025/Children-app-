package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.components.NeoBrutalTextField
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel
import kotlinx.coroutines.delay

@Composable
fun MemoryJournalScreen(viewModel: PetBuddyViewModel) {
    val memoriesList by viewModel.memories.collectAsState()

    var journalText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😀") }
    var selectedSticker by remember { mutableStateOf("👑 Hat Pup") }

    // Simulated Recording States
    var isRecordingSimulated by remember { mutableStateOf(false) }
    var recordTimeSeconds by remember { mutableStateOf(0) }
    var hasVoiceRecorded by remember { mutableStateOf(false) }

    // Incrementing counter during voice simulation
    LaunchedEffect(isRecordingSimulated) {
        if (isRecordingSimulated) {
            recordTimeSeconds = 0
            while (isRecordingSimulated) {
                delay(1000)
                recordTimeSeconds++
            }
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
                text = "Memory Diary 📸",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MEMORY DIARY FORM WRAPPER
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                NeoBrutalCard(
                    backgroundColor = Color.White,
                    shadowOffset = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "✍️ SCRAPBOOK TODAY'S MEMORIES:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = BrutalBlack
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Mood selection row
                        Text("Mood check today:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("😀", "😎", "🥳", "🎨", "😴", "🧁").forEach { emoji ->
                                val acts = selectedMood == emoji
                                val emojiBg = if (acts) NeoYellow else LightGrey
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(emojiBg, shape = RoundedCornerShape(12.dp))
                                        .border(width = 2.5.dp, color = BrutalBlack, shape = RoundedCornerShape(12.dp))
                                        .clickable { selectedMood = emoji },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 24.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Selfie Sticker selecting
                        Text("Choose selfie sticker overlay:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("👑 Hat Pup", "🦸 Hero Bun", "🚀 Galaxy Dino", "🎀 Bow Kitten").forEach { sticker ->
                                val isSelected = selectedSticker == sticker
                                val stickBg = if (isSelected) NeoPink else Color.White
                                val stickColor = if (isSelected) Color.White else BrutalBlack
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(stickBg, shape = RoundedCornerShape(10.dp))
                                        .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                        .clickable { selectedSticker = sticker }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stickerStr(sticker),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = stickColor,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // JOURNAL NOTE FIELD
                        Text("Tell us what you did today:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGrey)
                        Spacer(modifier = Modifier.height(4.dp))
                        NeoBrutalTextField(
                            value = journalText,
                            onValueChange = { journalText = it },
                            placeholderText = "Today custom play story... (e.g. Built a gigantic blanket fort with Sarah! 🏰)",
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- SIMULATED CASSETTE VOICE RECORDER CARD ---
                        NeoBrutalCard(
                            backgroundColor = LightGrey,
                            shadowOffset = 2.dp,
                            modifier = Modifier.fillMaxWidth().rotate(0.5f)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📻", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Retro Tape Locker (Voice Memo)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Cassette drawing representation
                                Box(
                                    modifier = Modifier
                                        .size(width = 220.dp, height = 70.dp)
                                        .background(BrutalBlack, shape = RoundedCornerShape(12.dp))
                                        .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left reel spinning
                                        Box(
                                            modifier = Modifier
                                                .rotate(if (isRecordingSimulated) recordTimeSeconds * 30f else 0f)
                                                .size(34.dp)
                                                .background(Color.White, shape = RoundedCornerShape(17.dp))
                                                .border(width = 3.dp, color = Color.Gray, shape = RoundedCornerShape(17.dp))
                                        )
                                        Spacer(modifier = Modifier.width(50.dp))
                                        // Right reel spinning
                                        Box(
                                            modifier = Modifier
                                                .rotate(if (isRecordingSimulated) recordTimeSeconds * 30f else 0f)
                                                .size(34.dp)
                                                .background(Color.White, shape = RoundedCornerShape(17.dp))
                                                .border(width = 3.dp, color = Color.Gray, shape = RoundedCornerShape(17.dp))
                                        )
                                    }
                                    // Timer label on cassette face
                                    val minutes = recordTimeSeconds / 60
                                    val seconds = recordTimeSeconds % 60
                                    val timerStr = String.format("%02d:%02d", minutes, seconds)
                                    Text(
                                        text = if (isRecordingSimulated) "● REC  $timerStr" else if (hasVoiceRecorded) "READY  🎤" else "EMPTY TAPE",
                                        color = if (isRecordingSimulated) NeoPink else Color.Yellow,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (!isRecordingSimulated) {
                                        // Record Button
                                        Button(
                                            onClick = {
                                                isRecordingSimulated = true
                                                hasVoiceRecorded = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeoPink)
                                        ) {
                                            Text("● Record Simulated", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                        }
                                    } else {
                                        // Stop Button
                                        Button(
                                            onClick = {
                                                isRecordingSimulated = false
                                                hasVoiceRecorded = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeoGreen)
                                        ) {
                                            Text("■ Stop & Save tape", fontWeight = FontWeight.Bold, color = BrutalBlack, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // SUBMIT SCRAPBOOK NOTE
                        NeoBrutalButton(
                            onClick = {
                                viewModel.saveMemory(
                                    description = journalText,
                                    mood = selectedMood,
                                    photoAvatarType = selectedSticker,
                                    hasVoiceSim = hasVoiceRecorded
                                )
                                journalText = ""
                                hasVoiceRecorded = false
                                recordTimeSeconds = 0
                            },
                            backgroundColor = NeoGreen,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Seal in Memory Scrapbook! 📸🔐", fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // PAST SCRAPBOOK JOURNAL LIST
            item {
                Text(
                    text = "📔 MY LOGGED MEMORIES CHEST:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            if (memoriesList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Historical scrapbook chest completely empty! Save your first memory file.",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(memoriesList) { memory ->
                    NeoBrutalCard(
                        backgroundColor = Color.White,
                        shadowOffset = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .rotate(if (memory.id % 2 == 0) 0.5f else -0.5f)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Polar Selfie sticker circle
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(NeoYellow, shape = RoundedCornerShape(12.dp))
                                        .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(stickerStr(memory.photoAvatarType), fontSize = 34.sp)
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Today Mood: ${memory.mood}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrutalBlack
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = memory.description,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGrey
                                    )
                                }
                            }

                            // If voice recorded, show cassette play bar
                            if (memory.hasVoiceNote) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = NeoYellow, shape = RoundedCornerShape(10.dp))
                                        .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.triggerNotification(
                                                "Playing Tape... 🎙️🎶",
                                                "Playing simulated voice note: '${memory.description.take(15)}...' (${memory.voiceDurationSeconds}s)"
                                            )
                                        }
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("▶", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Play Saved Voice Note Tape (${memory.voiceDurationSeconds} seconds)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = BrutalBlack
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper to provide sticker emojis
private fun stickerStr(sticker: String): String {
    return when {
        sticker.contains("Hat") -> "👑"
        sticker.contains("Hero") -> "🦸"
        sticker.contains("Galaxy") -> "🚀"
        else -> "🎀"
    }
}
