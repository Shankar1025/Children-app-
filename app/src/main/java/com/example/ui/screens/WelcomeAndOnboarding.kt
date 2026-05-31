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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeoBrutalButton
import com.example.ui.components.NeoBrutalCard
import com.example.ui.components.NeoBrutalTextField
import com.example.ui.theme.*
import com.example.viewmodel.PetBuddyViewModel
import com.example.viewmodel.Screen

@Composable
fun WelcomeScreen(viewModel: PetBuddyViewModel) {
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
            // Big Cartoon Mascot Frame
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .rotate(-4f)
                    .background(color = NeoPink, shape = RoundedCornerShape(24.dp))
                    .border(width = 4.dp, color = BrutalBlack, shape = RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐶🐱🐹",
                    fontSize = 55.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text Title in Large Neo-Brutalist display font
            Text(
                text = "PETBUDDY\nKIDS!",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 46.sp,
                textAlign = TextAlign.Center,
                color = BrutalBlack,
                modifier = Modifier.rotate(2f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Track your daily habits, carry out healthy missions, earn magical pet tokens, and watch your companion grow!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = DarkGrey,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Main CTA Button
            NeoBrutalButton(
                onClick = { viewModel.navigateTo(Screen.ChoosePet) },
                backgroundColor = NeoYellow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Adopt My Pet Buddy! 🚀",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom parent help note
            Text(
                text = "Parental Setup and Passcode controls included",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ChoosePetScreen(viewModel: PetBuddyViewModel) {
    val petsList = listOf(
        Pair("puppy", "🐶 Puppy"),
        Pair("cat", "🐱 Kitten"),
        Pair("rabbit", "🐰 Rabbit"),
        Pair("panda", "🐼 Panda"),
        Pair("dinosaur", "🦖 Dino"),
        Pair("unicorn", "🦄 Unicorn"),
        Pair("turtle", "🐢 Turtle"),
        Pair("fox", "🦊 Fox")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalPaper)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Large Heading Card
        NeoBrutalCard(
            backgroundColor = NeoBlue,
            shadowOffset = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "STEP 1: CHOOSE A BUDDY! ✨",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Pick your favorite companion pet to join your daily task journey!",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = BrutalBlack,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Grid of pets
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(petsList) { pet ->
                // Colorful grid card representing the choice
                val isSelected = viewModel.tempSelectedPet.collectAsState().value == pet.first
                val cardBg = if (isSelected) NeoYellow else Color.White

                NeoBrutalCard(
                    backgroundColor = cardBg,
                    shadowOffset = 6.dp,
                    onClick = { viewModel.selectPet(pet.first) },
                    modifier = Modifier.aspectRatio(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Large pet icon (emoji)
                        val emojiOnly = pet.second.substringBefore(" ").trim()
                        val petName = pet.second.substringAfter(" ").trim()

                        Text(
                            text = emojiOnly,
                            fontSize = 58.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Pet name label
                        Text(
                            text = petName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = BrutalBlack
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeoBrutalButton(
            onClick = { viewModel.navigateTo(Screen.ChooseAge) },
            backgroundColor = NeoGreen,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Next: Choose My Age 🧸 ➡️",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = BrutalBlack
            )
        }
    }
}

@Composable
fun ChooseAgeScreen(viewModel: PetBuddyViewModel) {
    val activeAge = viewModel.tempSelectedAge.collectAsState().value
    val ageGroups = listOf(
        "3-5 Years" to "👼 Toddler Habits (Brush teeth, collect toys, drink water)",
        "6-8 Years" to "🎒 Early Learner (Homework check, helpful chores, reading)",
        "9-12 Years" to "⚽ Growing Kid (Exercise, focusing, daily study tracker)",
        "13-15 Years" to "🚀 Teen Star (Workouts, schedule management, growth)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalPaper)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Large Heading Card
        NeoBrutalCard(
            backgroundColor = NeoPink,
            shadowOffset = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "STEP 2: CHOOSE YOUR AGE! 🎂",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This automatically seeds appropriate daily habits and goals tailored directly for you!",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGrey,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Column of choices
        ageGroups.forEach { group ->
            val isSelected = activeAge == group.first
            val cardBg = if (isSelected) NeoYellow else Color.White

            NeoBrutalCard(
                backgroundColor = cardBg,
                shadowOffset = 4.dp,
                onClick = { viewModel.tempSelectedAge.value = group.first },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom heavy check dot
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = if (isSelected) BrutalBlack else Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(width = 3.dp, color = BrutalBlack, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Text(text = "✓", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.first,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = BrutalBlack
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = group.second,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGrey
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NeoBrutalButton(
                onClick = { viewModel.navigateBack() },
                backgroundColor = Color.White,
                modifier = Modifier.weight(0.35f)
            ) {
                Text(text = "⬅️ Back", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            NeoBrutalButton(
                onClick = { viewModel.navigateTo(Screen.ParentSetup) },
                backgroundColor = NeoGreen,
                modifier = Modifier.weight(0.65f)
            ) {
                Text(
                    text = "Next: Setup Parent 👨‍👩‍👧 ➡️",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
            }
        }
    }
}

@Composable
fun ParentSetupScreen(viewModel: PetBuddyViewModel) {
    var parentName by remember { mutableStateOf("") }
    var parentEmail by remember { mutableStateOf("") }
    var parentCode by remember { mutableStateOf("") }
    var childName by remember { mutableStateOf("") }
    var petCustomName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalPaper)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Heading card
        NeoBrutalCard(
            backgroundColor = NeoOrange,
            shadowOffset = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "PASSPORT & PARENT GATEWAY 🗺️",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter a guardian security code to authorize tasks and monitor daily habits safely.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGrey,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Fields inside Brutal inputs
        Text("Parent Guardianship Info:", fontWeight = FontWeight.Black, fontSize = 16.sp, color = BrutalBlack)
        Spacer(modifier = Modifier.height(6.dp))

        NeoBrutalTextField(
            value = parentName,
            onValueChange = { parentName = it },
            placeholderText = "Parent's First Name (e.g. Sarah)"
        )
        Spacer(modifier = Modifier.height(12.dp))

        NeoBrutalTextField(
            value = parentEmail,
            onValueChange = { parentEmail = it },
            placeholderText = "Parent Email Account"
        )
        Spacer(modifier = Modifier.height(12.dp))

        NeoBrutalTextField(
            value = parentCode,
            onValueChange = { if (it.length <= 4) parentCode = it },
            placeholderText = "4-Digit Parent Security Code",
            keyboardType = KeyboardType.Number,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text("Child Adventurer & Companion Info:", fontWeight = FontWeight.Black, fontSize = 16.sp, color = BrutalBlack)
        Spacer(modifier = Modifier.height(6.dp))

        NeoBrutalTextField(
            value = childName,
            onValueChange = { childName = it },
            placeholderText = "Child's Play Name (e.g. Leo)"
        )
        Spacer(modifier = Modifier.height(12.dp))

        NeoBrutalTextField(
            value = petCustomName,
            onValueChange = { petCustomName = it },
            placeholderText = "Give your Pet a Cute Name! (e.g. Barnaby)"
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NeoBrutalButton(
                onClick = { viewModel.navigateBack() },
                backgroundColor = Color.White,
                modifier = Modifier.weight(0.35f)
            ) {
                Text(text = "⬅️ Back", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            NeoBrutalButton(
                onClick = {
                    viewModel.setupParentAndFirstChild(
                        parentName = parentName,
                        parentEmail = parentEmail,
                        passcode = parentCode,
                        childName = childName,
                        petCustomName = petCustomName
                    )
                },
                backgroundColor = NeoYellow,
                modifier = Modifier.weight(0.65f)
            ) {
                Text(
                    text = "Adopt & Fly! 🚀🎨",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack
                )
            }
        }
    }
}
