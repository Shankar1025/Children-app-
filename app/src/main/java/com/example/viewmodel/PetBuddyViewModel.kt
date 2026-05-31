package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.PetBuddyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class Screen {
    object Welcome : Screen()
    object ChoosePet : Screen()
    object ChooseAge : Screen()
    object ParentSetup : Screen()
    object Dashboard : Screen()
    object Tasks : Screen()
    object WaterTracker : Screen()
    object SavingsTracker : Screen()
    object LearningTracker : Screen()
    object MemoryJournal : Screen()
    object PetShop : Screen()
    object ParentDashboard : Screen()
    object AdMobReward : Screen()
}

data class InAppNotification(
    val title: String,
    val message: String,
    val isSuccess: Boolean = true,
    val id: Long = System.currentTimeMillis()
)

class PetBuddyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = PetBuddyRepository(database.petBuddyDao())

    // --- NAVIGATION ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Welcome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val navStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        navStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (navStack.isNotEmpty()) {
            _currentScreen.value = navStack.removeAt(navStack.size - 1)
        } else {
            _currentScreen.value = Screen.Welcome
        }
    }

    // --- PARENT / LOGGED IN STATE ---
    val parentAccount: StateFlow<ParentAccount?> = repository.parentAccount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All children of the logged in parent
    @OptIn(ExperimentalCoroutinesApi::class)
    val children: StateFlow<List<ChildProfile>> = parentAccount
        .flatMapLatest { parent ->
            if (parent != null) {
                repository.getChildren(parent.email)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- ACTIVE CHILD PROFILE ---
    private val _activeChildId = MutableStateFlow<Int>(-1)
    val activeChildId: StateFlow<Int> = _activeChildId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeChild: StateFlow<ChildProfile?> = _activeChildId
        .flatMapLatest { id ->
            if (id != -1) {
                repository.observeChildProfile(id)
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- REACTIVE OBSERVABLE STATS FOR ACTIVE CHILD ---
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = _activeChildId
        .flatMapLatest { id ->
            if (id != -1) repository.observeTasks(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val savings: StateFlow<List<SavingsProgress>> = _activeChildId
        .flatMapLatest { id ->
            if (id != -1) repository.observeSavings(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val learning: StateFlow<List<LearningLog>> = _activeChildId
        .flatMapLatest { id ->
            if (id != -1) repository.observeLearning(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val memories: StateFlow<List<MemoryJournal>> = _activeChildId
        .flatMapLatest { id ->
            if (id != -1) repository.observeMemories(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val accessories: StateFlow<List<PetAccessory>> = _activeChildId
        .flatMapLatest { id ->
            if (id != -1) repository.observeAccessories(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Water
    private val todayString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    @OptIn(ExperimentalCoroutinesApi::class)
    val waterLogToday: StateFlow<WaterLog?> = _activeChildId
        .flatMapLatest { id ->
            if (id != -1) {
                repository.observeWaterLogToday(id, todayString)
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val waterHistory: StateFlow<List<WaterLog>> =  _activeChildId
        .flatMapLatest { id ->
            if (id != -1) repository.observeWaterHistory(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- TEMP REGISTRATION / ONBOARDING FLOW STATES ---
    var tempSelectedPet = MutableStateFlow("puppy")
    var tempSelectedAge = MutableStateFlow("6-8 Years")

    // --- IN-APP NOTIFICATION SYSTEM ---
    val notifications = mutableStateListOf<InAppNotification>()

    fun triggerNotification(title: String, message: String, isSuccess: Boolean = true) {
        viewModelScope.launch {
            val notif = InAppNotification(title, message, isSuccess)
            notifications.add(notif)
            // Auto disappear after 4 seconds
            kotlinx.coroutines.delay(4000)
            notifications.remove(notif)
        }
    }

    // --- ONBOARDING ACTIONS ---
    fun selectPet(petId: String) {
        tempSelectedPet.value = petId
        navigateTo(Screen.ChooseAge)
    }

    fun selectAge(ageGroup: String) {
        tempSelectedAge.value = ageGroup
        navigateTo(Screen.ParentSetup)
    }

    fun setupParentAndFirstChild(
        parentName: String,
        parentEmail: String,
        passcode: String,
        childName: String,
        petCustomName: String
    ) {
        viewModelScope.launch {
            if (parentName.isBlank() || parentEmail.isBlank() || passcode.isBlank() || childName.isBlank() || petCustomName.isBlank()) {
                triggerNotification("Oops!", "Please fill in all details!", false)
                return@launch
            }
            // 1. Create Parent Account
            repository.registerParentAndLogin(parentName, parentEmail, passcode)

            // 2. Create Child profile
            val childId = repository.createChildProfile(
                parentEmail = parentEmail,
                name = childName,
                ageGroup = tempSelectedAge.value,
                petId = tempSelectedPet.value,
                petCustomName = petCustomName
            )

            // Select active child
            _activeChildId.value = childId
            triggerNotification("Yay! 🎉", "Welcome to PetBuddy Kids, $childName!", true)

            // Jump to dashboard
            _currentScreen.value = Screen.Dashboard
        }
    }

    // --- PROFILE ACTIONS ---
    fun switchActiveChild(childId: Int) {
        _activeChildId.value = childId
        val childName = children.value.find { it.id == childId }?.name ?: "Buddy"
        triggerNotification("Profile Switched 🎒", "Now playing as $childName!", true)
    }

    fun addNewChildProfile(name: String, ageGroup: String, petId: String, petCustomName: String) {
        viewModelScope.launch {
            val parent = parentAccount.value ?: return@launch
            val childId = repository.createChildProfile(
                parentEmail = parent.email,
                name = name,
                ageGroup = ageGroup,
                petId = petId,
                petCustomName = petCustomName
            )
            triggerNotification("New Companion Created 🌟", "Meet $petCustomName!", true)
            // Set active
            _activeChildId.value = childId
        }
    }

    // --- GAME ACTIONS ---
    fun completeTask(task: Task) {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                // Find previous levels to check for level-ups
                val beforeChild = activeChild.value
                repository.completeTask(task, childId)

                // Trigger celebration
                triggerNotification(
                    "Task Completed! ✨",
                    "Earned +${task.coinsReward} Coins 🪙 and +${task.starsReward} Stars ⭐!"
                )

                // Verify level up after delay
                kotlinx.coroutines.delay(100)
                val afterChild = activeChild.value
                if (beforeChild != null && afterChild != null && afterChild.level > beforeChild.level) {
                    triggerNotification(
                        "LEVEL UP! 🌟🎈",
                        "${afterChild.petCustomName} evolved to Level ${afterChild.level}! Great job!",
                        true
                    )
                }
            }
        }
    }

    fun resetTaskForDemo(task: Task) {
        viewModelScope.launch {
            repository.resetTask(task)
        }
    }

    // --- WATER TRACTION ---
    fun drinkGlass() {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                repository.adjustWaterGlasses(childId, todayString, 1)
                triggerNotification("Gulp! 💧", "Great job! Drink more to keep your pet fresh!", true)
            }
        }
    }

    fun reduceWater() {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                repository.adjustWaterGlasses(childId, todayString, -1)
            }
        }
    }

    // --- PIGGY SAVINGS ---
    fun addSavings(amount: Int, note: String) {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                repository.addSavings(childId, amount, note)
                triggerNotification(
                    "Savings Logged! 🪙🐷",
                    "Saved $amount to your Piggy Bank! Earned +1 Star ⭐",
                    true
                )
            }
        }
    }

    // --- FACTS / LEARNING ---
    fun saveLearnedTopic(type: String, content: String, details: String) {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                if (content.isBlank()) {
                    triggerNotification("Uh oh!", "Learning topic cannot be empty!", false)
                    return@launch
                }
                repository.logNewLearning(childId, type, content, details)
                triggerNotification(
                    "Brain Power Boosted! 🧠",
                    "Unlocked ${type}: '$content'! Earned +25 Coins 🪙 & +2 Stars ⭐!",
                    true
                )
            }
        }
    }

    // --- MEMORIES JOURNAL ---
    fun saveMemory(description: String, mood: String, photoAvatarType: String, hasVoiceSim: Boolean) {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                if (description.isBlank()) {
                    triggerNotification("Memory Empty", "Tell us what you did today! ✍️", false)
                    return@launch
                }
                val duration = if (hasVoiceSim) (5..45).random() else 0
                repository.saveMemory(childId, description, mood, photoAvatarType, duration)
                triggerNotification(
                    "Memory Saved! 📸❤️",
                    "Your memory was locked into your diary!",
                    true
                )
            }
        }
    }

    // --- PET SHOP ACCESSORIES ---
    fun purchaseItem(accessory: PetAccessory) {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                val success = repository.buyAccessory(childId, accessory)
                if (success) {
                    triggerNotification(
                        "Purchased! 🛍️✨",
                        "Unlocked '${accessory.name}'! Happiness is boosted!",
                        true
                    )
                } else {
                    triggerNotification("Not Enough Coins! 🪙", "Keep saving and doing tasks to afford this!", false)
                }
            }
        }
    }

    fun toggleAccessory(accessory: PetAccessory) {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                val pre = accessory.isEquipped
                repository.toggleEquipAccessory(childId, accessory)
                if (!pre) {
                    if (accessory.type == "food") {
                        triggerNotification("Crunch Chomp! 🍩🍖", "${activeChild.value?.petCustomName} loved the tasty treat!", true)
                    } else if (accessory.type == "toy") {
                        triggerNotification("Squeak bounce! 🧸🎮", "Playing with the ${accessory.name} raised happiness!", true)
                    } else {
                        triggerNotification("Accessorized! 😎👑", "Equipped ${accessory.name} on your pet!", true)
                    }
                } else {
                    triggerNotification("Unequipped!", "Removed ${accessory.name}.", true)
                }
            }
        }
    }

    // --- AD REWARDS ---
    private val _adProgress = MutableStateFlow(0f)
    val adProgress: StateFlow<Float> = _adProgress.asStateFlow()

    private val _isAdRunning = MutableStateFlow(false)
    val isAdRunning: StateFlow<Boolean> = _isAdRunning.asStateFlow()

    fun runRewardedAd(isStarsReward: Boolean) {
        viewModelScope.launch {
            _isAdRunning.value = true
            _adProgress.value = 0f
            // Count up 100 steps
            for (i in 1..100) {
                kotlinx.coroutines.delay(25) // 2.5 seconds total simulated ad length
                _adProgress.value = i / 100f
            }
            _isAdRunning.value = false
            triggerNotification(
                "Simulated Ad Complete! 🎬🍿",
                "Rewarding you with high-tier coins/stars!",
                true
            )
            val childId = _activeChildId.value
            if (childId != -1) {
                repository.triggerAdReward(childId, isStarsReward)
                if (isStarsReward) {
                    triggerNotification("Simulated Ad Award! 🌟", "+10 Stars and +20 XP awarded to your wallet!", true)
                } else {
                    triggerNotification("Simulated Ad Award! 🪙", "+60 Coins and +20 XP awarded to your wallet!", true)
                }
            }
            navigateBack()
        }
    }

    // --- OTHER INTERACTIVE ACTIONS ---
    fun feedPetFast() {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                val child = activeChild.value ?: return@launch
                if (child.coins < 15) {
                    triggerNotification("No coins for food!", "You need 15 coins to buy snacks!", false)
                    return@launch
                }
                // Check if already completely full
                if (child.petHunger <= 0) {
                    triggerNotification("Already full!", "${child.petCustomName} can't eat another bite!", false)
                    return@launch
                }
                // Quick treat
                val accessoriesList = accessories.value
                val foodItem = accessoriesList.find { it.type == "food" && it.isPurchased }
                if (foodItem != null) {
                    toggleAccessory(foodItem)
                } else {
                    // Try to buy a default Sprinkles Donut (20 coins)
                    val defaultDonut = accessoriesList.find { it.name == "Sprinkles Donut" }
                    if (defaultDonut != null) {
                        val bought = repository.buyAccessory(childId, defaultDonut)
                        if (bought) {
                            val refreshedDonut = defaultDonut.copy(isPurchased = true)
                            toggleAccessory(refreshedDonut)
                        } else {
                            triggerNotification("Oops!", "Save up to buy snacks from the shop!", false)
                        }
                    }
                }
            }
        }
    }

    fun playWithPetFast() {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                val child = activeChild.value ?: return@launch
                if (child.petEnergy <= 15) {
                    triggerNotification("Too tired! 😴", "Let ${child.petCustomName} sleep to recover energy first!", false)
                    return@launch
                }
                repository.petPlay(childId)
                triggerNotification(
                    "Yay! 🎮⚽",
                    "You played tag with ${child.petCustomName}! Happiness up, slightly tired.",
                    true
                )
            }
        }
    }

    fun putPetToSleep() {
        viewModelScope.launch {
            val childId = _activeChildId.value
            if (childId != -1) {
                repository.petSleep(childId)
                triggerNotification("Zzz... 🌙🌠", "${activeChild.value?.petCustomName} is resting. Energy is fully restored!", true)
            }
        }
    }

    // --- PARENT DASHBOARD ADMIN CONTROLS ---
    val parentSelectedChildId = MutableStateFlow<Int>(-1)

    fun parentAddTask(title: String, category: String, coins: Int, stars: Int) {
        viewModelScope.launch {
            val childId = parentSelectedChildId.value
            if (childId != -1 && title.isNotBlank()) {
                repository.parentAddTask(childId, title, category, coins, stars)
                triggerNotification("Task Created! 🔨", "Parent added custom task: '$title'", true)
            }
        }
    }

    fun parentDeleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.parentDeleteTask(taskId)
            triggerNotification("Task Deleted 🗑️", "Parent removed the task.", true)
        }
    }

    // --- INITIAL SEEDING TRIGGER ON FIRST EVER BOOT ---
    init {
        viewModelScope.launch {
            // Check if active parent account already exists in DB to resume
            parentAccount.first {
                if (it != null) {
                    val firstChild = repository.getChildren(it.email).first().firstOrNull()
                    if (firstChild != null) {
                        _activeChildId.value = firstChild.id
                        _currentScreen.value = Screen.Dashboard
                    } else {
                        // Needs onboarding, choose favorite pet
                        _currentScreen.value = Screen.Welcome
                    }
                } else {
                    _currentScreen.value = Screen.Welcome
                }
                true
            }
        }
    }
}
