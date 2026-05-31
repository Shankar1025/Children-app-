package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ChildProfile
import com.example.data.database.PetAccessory
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CutePetComponent(
    child: ChildProfile,
    equippedAccessories: List<PetAccessory>,
    modifier: Modifier = Modifier
) {
    // Breathing/Pulsing Animation
    val infiniteTransition = rememberInfiniteTransition(label = "pet_breathing")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )
    val breathOffsetRaw by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_offset_raw"
    )
    val breathOffset = breathOffsetRaw.dp

    // Eating shaking animation state
    var chewShakeCount by remember { mutableStateOf(0) }
    var previousHunger by remember { mutableStateOf(child.petHunger) }

    LaunchedEffect(child.petHunger) {
        if (child.petHunger < previousHunger) {
            // Hunger decreased means pet was fed! Trigger happy chewing
            repeat(4) {
                chewShakeCount++
                delay(120)
            }
            chewShakeCount = 0
        }
        previousHunger = child.petHunger
    }

    val chewRotation = if (chewShakeCount > 0) {
        if (chewShakeCount % 2 == 0) -8f else 8f
    } else 0f

    // Find equipped hat/glasses/clothes/toy
    val equippedHat = equippedAccessories.find { it.type == "hat" && it.isEquipped }
    val equippedGlasses = equippedAccessories.find { it.type == "glasses" && it.isEquipped }
    val equippedClothes = equippedAccessories.find { it.type == "clothes" && it.isEquipped }

    // Derive pet emoji
    val baseEmoji = when (child.petId.lowercase()) {
        "puppy" -> "🐶"
        "cat" -> "🐱"
        "rabbit" -> "🐰"
        "panda" -> "🐼"
        "dinosaur" -> "🦖"
        "unicorn" -> "🦄"
        "turtle" -> "🐢"
        "fox" -> "🦊"
        else -> "🐶"
    }

    // Determine background color based on petType
    val petBgColor = when (child.petId.lowercase()) {
        "puppy" -> NeoYellow
        "cat" -> NeoPink
        "rabbit" -> NeoGreen
        "panda" -> LightGrey
        "dinosaur" -> NeoGreen
        "unicorn" -> NeoPink
        "turtle" -> NeoBlue
        "fox" -> NeoOrange
        else -> NeoYellow
    }

    // Pet Evolution level logic
    val evolutionTitle = when {
        child.level >= 10 -> "Titan Sovereign 🏆⚡"
        child.level >= 7 -> "Master Companion Elite 🌟🛡️"
        child.level >= 4 -> "Super Buddy Hero 🦸🔥"
        child.level >= 2 -> "Growing Buddy 🌱💫"
        else -> "Baby Companion 🍼🧸"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Active Companion Evolution Badge
            Box(
                modifier = Modifier
                    .background(color = BrutalBlack, shape = RoundedCornerShape(10.dp))
                    .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = evolutionTitle,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Pet Container with brutalist drop shadow and border
            NeoBrutalCard(
                modifier = Modifier
                    .size(200.dp)
                    .scale(if (child.petEnergy <= 20) 0.95f else breathScale)
                    .rotate(if (child.petEnergy <= 20) -15f else chewRotation),
                backgroundColor = petBgColor,
                cornerRadius = 32.dp,
                shadowOffset = 8.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Floating sleepy indicators if resting
                    if (child.petEnergy <= 20) {
                        Text(
                            text = "Zzz...",
                            color = BrutalBlack,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-10).dp, y = 10.dp)
                        )
                    }

                    // Floating hearts/stars if extremely happy
                    if (child.petHappiness >= 85) {
                        Text(
                            text = "✨💖✨",
                            fontSize = 24.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 10.dp, y = (10).dp)
                        )
                    }

                    // Base Pet Emoji
                    Text(
                        text = baseEmoji,
                        fontSize = 110.sp,
                        modifier = Modifier
                            .offset(y = breathOffset)
                            .align(Alignment.Center)
                    )

                    // Overlay 1: Equipped Clothes
                    if (equippedClothes != null) {
                        Text(
                            text = equippedClothes.emoji,
                            fontSize = 42.sp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (-5).dp)
                        )
                    }

                    // Overlay 2: Equipped Glasses
                    if (equippedGlasses != null) {
                        Text(
                            text = equippedGlasses.emoji,
                            fontSize = 44.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(y = (-15).dp)
                        )
                    }

                    // Overlay 3: Equipped Hat
                    if (equippedHat != null) {
                        Text(
                            text = equippedHat.emoji,
                            fontSize = 46.sp,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-20).dp)
                                .rotate(-4f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Stats Summary Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Happiness
                StatIndicatorBadge(
                    label = "Happiness",
                    value = "${child.petHappiness}/100",
                    color = NeoPink,
                    emoji = "🥰"
                )
                // Energy
                StatIndicatorBadge(
                    label = "Energy",
                    value = "${child.petEnergy}/100",
                    color = NeoYellow,
                    emoji = "🔋"
                )
                // Hunger
                StatIndicatorBadge(
                    label = "Hunger",
                    value = if (child.petHunger <= 20) "Full 🍗" else if (child.petHunger <= 60) "Peckish 🍎" else "Starving! 🚨",
                    color = if (child.petHunger <= 50) NeoGreen else NeoOrange,
                    emoji = "😋"
                )
            }
        }
    }
}

@Composable
fun StatIndicatorBadge(
    label: String,
    value: String,
    color: Color,
    emoji: String
) {
    Box(
        modifier = Modifier
            .background(color = color, shape = RoundedCornerShape(12.dp))
            .border(width = 2.dp, color = BrutalBlack, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$emoji $label",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BrutalBlack
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )
        }
    }
}
