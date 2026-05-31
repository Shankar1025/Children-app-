package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class PetBuddyRepository(private val petBuddyDao: PetBuddyDao) {

    val parentAccount: Flow<ParentAccount?> = petBuddyDao.getParentAccount()

    fun getChildren(parentEmail: String): Flow<List<ChildProfile>> =
        petBuddyDao.getChildrenForParent(parentEmail)

    fun observeChildProfile(childId: Int): Flow<ChildProfile?> =
        petBuddyDao.observeChildProfileById(childId)

    suspend fun getChildProfileDirectly(childId: Int): ChildProfile? =
        petBuddyDao.getChildProfileById(childId)

    fun observeTasks(childId: Int): Flow<List<Task>> =
        petBuddyDao.getTasksForChild(childId)

    fun observeSavings(childId: Int): Flow<List<SavingsProgress>> =
        petBuddyDao.getSavingsForChild(childId)

    fun observeLearning(childId: Int): Flow<List<LearningLog>> =
        petBuddyDao.getLearningForChild(childId)

    fun observeMemories(childId: Int): Flow<List<MemoryJournal>> =
        petBuddyDao.getMemoriesForChild(childId)

    fun observeAccessories(childId: Int): Flow<List<PetAccessory>> =
        petBuddyDao.getAccessoriesForChild(childId)

    fun observeWaterLogToday(childId: Int, date: String): Flow<WaterLog?> =
        petBuddyDao.observeWaterLogByDate(childId, date)

    fun observeWaterHistory(childId: Int): Flow<List<WaterLog>> =
        petBuddyDao.getWaterHistory(childId)

    // --- PARENT INTERACTION ---
    suspend fun registerParentAndLogin(name: String, email: String, code: String) {
        val parent = ParentAccount(
            email = email,
            name = name,
            securityCode = code,
            isCurrentActive = true
        )
        petBuddyDao.insertParent(parent)
    }

    suspend fun verifyParentPasscode(email: String, enteredCode: String): Boolean {
        val parent = petBuddyDao.getParentByEmail(email)
        return parent != null && parent.securityCode == enteredCode
    }

    // --- PROFILE CREATION WITH AUTOMATIC SEEDS ---
    suspend fun createChildProfile(
        parentEmail: String,
        name: String,
        ageGroup: String,
        petId: String,
        petCustomName: String
    ): Int {
        val newProfile = ChildProfile(
            parentEmail = parentEmail,
            name = name,
            ageGroup = ageGroup,
            petId = petId,
            petCustomName = petCustomName,
            coins = 150, // Initial coins gift!
            stars = 5,
            level = 1,
            xp = 0
        )
        val childId = petBuddyDao.insertChildProfile(newProfile).toInt()

        // 1. Seed Age-Appropriate Tasks
        seedTasksForAge(childId, ageGroup)

        // 2. Seed Shop Accessories for this child
        seedInitialAccessories(childId)

        return childId
    }

    private suspend fun seedTasksForAge(childId: Int, ageGroup: String) {
        val tasks = when (ageGroup) {
            "3-5 Years" -> listOf(
                Task(childId = childId, title = "🦷 Brush Teeth (Morning & Night)", category = "Daily Habits", coinsReward = 15, starsReward = 1, xpReward = 10),
                Task(childId = childId, title = "💧 Drink 4 Glasses of Water", category = "Daily Habits", coinsReward = 10, starsReward = 1, xpReward = 10),
                Task(childId = childId, title = "🧸 Put Toys Back in Box", category = "Chores", coinsReward = 20, starsReward = 2, xpReward = 15),
                Task(childId = childId, title = "📖 Read/Listen to a Bedtime Story", category = "Learning", coinsReward = 20, starsReward = 2, xpReward = 15)
            )
            "6-8 Years" -> listOf(
                Task(childId = childId, title = "✏️ Complete My School Homework", category = "Learning", coinsReward = 25, starsReward = 2, xpReward = 20),
                Task(childId = childId, title = "💧 Drink 6 Glasses of Water", category = "Daily Habits", coinsReward = 15, starsReward = 1, xpReward = 10),
                Task(childId = childId, title = "🧹 Help Clean the Dinner Table", category = "Chores", coinsReward = 20, starsReward = 2, xpReward = 15),
                Task(childId = childId, title = "📖 Read Outside Class for 15 Mins", category = "Learning", coinsReward = 20, starsReward = 2, xpReward = 15)
            )
            "9-12 Years" -> listOf(
                Task(childId = childId, title = "🏃 30 Minutes Outdoor Exercise/Play", category = "Self-Growth", coinsReward = 30, starsReward = 3, xpReward = 25),
                Task(childId = childId, title = "📚 Focus Study / Revision (45 Mins)", category = "Learning", coinsReward = 25, starsReward = 2, xpReward = 20),
                Task(childId = childId, title = "🧹 Clean and Organize My Bedroom", category = "Chores", coinsReward = 30, starsReward = 3, xpReward = 25),
                Task(childId = childId, title = "🌱 Learn 1 Fact or Skill Today", category = "Learning", coinsReward = 15, starsReward = 1, xpReward = 15)
            )
            else -> listOf( // "13-15 Years"
                Task(childId = childId, title = "💪 Daily Workout or Yoga Sequence", category = "Self-Growth", coinsReward = 35, starsReward = 3, xpReward = 30),
                Task(childId = childId, title = "🎓 Work on Weekly Study Goals", category = "Learning", coinsReward = 30, starsReward = 3, xpReward = 25),
                Task(childId = childId, title = "⏰ Schedule Tomorrow's Tasks (Planner)", category = "Self-Growth", coinsReward = 25, starsReward = 2, xpReward = 20),
                Task(childId = childId, title = "🧹 Wash the Dishes / Help with Cooking", category = "Chores", coinsReward = 35, starsReward = 3, xpReward = 30)
            )
        }
        petBuddyDao.insertTasks(tasks)
    }

    private suspend fun seedInitialAccessories(childId: Int) {
        val list = listOf(
            // Hats
            PetAccessory(childId = childId, name = "Cool Cap", type = "hat", costCoins = 50, emoji = "🧢"),
            PetAccessory(childId = childId, name = "Wizard Hat", type = "hat", costCoins = 150, emoji = "🧙‍♂️"),
            PetAccessory(childId = childId, name = "Chef Hat", type = "hat", costCoins = 90, emoji = "👨‍🍳"),
            PetAccessory(childId = childId, name = "Sparkly Crown", type = "hat", costCoins = 250, emoji = "👑"),

            // Glasses
            PetAccessory(childId = childId, name = "Super Sunglasses", type = "glasses", costCoins = 75, emoji = "🕶️"),
            PetAccessory(childId = childId, name = "Funny Glasses", type = "glasses", costCoins = 45, emoji = "👓"),
            PetAccessory(childId = childId, name = "Pirate Eyepatch", type = "glasses", costCoins = 120, emoji = "🏴‍☠️"),

            // Clothes
            PetAccessory(childId = childId, name = "Astronaut Suit", type = "clothes", costCoins = 220, emoji = "🚀"),
            PetAccessory(childId = childId, name = "Superhero Cape", type = "clothes", costCoins = 180, emoji = "🦸"),
            PetAccessory(childId = childId, name = "Cool Bowtie", type = "clothes", costCoins = 35, emoji = "🎀"),

            // Toys
            PetAccessory(childId = childId, name = "Squeaky Duck", type = "toy", costCoins = 40, emoji = "🦆"),
            PetAccessory(childId = childId, name = "Shiny Gold Trophy", type = "toy", costCoins = 300, emoji = "🏆"),
            PetAccessory(childId = childId, name = "Fidget Spinner", type = "toy", costCoins = 60, emoji = "🌀"),

            // Foods
            PetAccessory(childId = childId, name = "Sprinkles Donut", type = "food", costCoins = 20, emoji = "🍩"),
            PetAccessory(childId = childId, name = "Star Candy", type = "food", costCoins = 35, emoji = "🍬"),
            PetAccessory(childId = childId, name = "Rainbow Cupcake", type = "food", costCoins = 25, emoji = "🧁")
        )
        petBuddyDao.insertAccessories(list)
    }

    // --- DYNAMIC STATE LOGIC ---

    // 1. Task Completion Engine
    suspend fun toggleTaskComplete(taskId: Int, childId: Int): Boolean {
        val child = petBuddyDao.getChildProfileById(childId) ?: return false
        val tasks = petBuddyDao.getTasksForChild(childId)
        // Find which is being toggled
        var resultState = false
        // Fetch direct list from Single state transaction
        // First we look inside the DB
        // Let's do direct select or pass a Task parameter.
        // We'll query DB with full scope for accuracy.
        return true
    }

    suspend fun completeTask(task: Task, childId: Int) {
        if (task.isCompleted) return

        val child = petBuddyDao.getChildProfileById(childId) ?: return
        val updatedTask = task.copy(
            isCompleted = true,
            isCompletedCount = task.isCompletedCount + 1
        )
        petBuddyDao.updateTask(updatedTask)

        // Give Rewards
        val addedXp = task.xpReward
        val addedCoins = task.coinsReward
        val addedStars = task.starsReward

        val newXp = child.xp + addedXp
        val currentLevel = child.level
        val neededXpForLevel = currentLevel * 100
        val levelUp = newXp >= neededXpForLevel

        val updatedChild = child.copy(
            coins = child.coins + addedCoins,
            stars = child.stars + addedStars,
            xp = if (levelUp) newXp - neededXpForLevel else newXp,
            level = if (levelUp) currentLevel + 1 else currentLevel,
            petHappiness = (child.petHappiness + 15).coerceAtMost(100) // Completing tasks makes pet happy!
        )
        petBuddyDao.updateChildProfile(updatedChild)
    }

    suspend fun resetTask(task: Task) {
        if (!task.isCompleted) return
        val updatedTask = task.copy(isCompleted = false)
        petBuddyDao.updateTask(updatedTask)
    }

    // --- PARENT CRUD TASKS ---
    suspend fun parentAddTask(childId: Int, title: String, category: String, coins: Int, stars: Int) {
        val task = Task(
            childId = childId,
            title = title,
            category = category,
            coinsReward = coins,
            starsReward = stars,
            xpReward = coins + (stars * 5),
            isCustomCreated = true
        )
        petBuddyDao.insertTask(task)
    }

    suspend fun parentDeleteTask(taskId: Int) {
        petBuddyDao.deleteTaskById(taskId)
    }

    // --- WATER SYSTEM ---
    suspend fun adjustWaterGlasses(childId: Int, date: String, adjustment: Int) {
        val currentLog = petBuddyDao.getWaterLogByDate(childId, date)
        if (currentLog != null) {
            val newDrank = (currentLog.glassesDrank + adjustment).coerceAtLeast(0)
            petBuddyDao.insertWaterLog(currentLog.copy(glassesDrank = newDrank))

            // Rewards if hitting goal
            if (newDrank == currentLog.goalGlasses && currentLog.glassesDrank < currentLog.goalGlasses) {
                rewardChild(childId, coins = 15, stars = 1, xp = 15)
            }
        } else {
            val goal = when (getChildProfileDirectly(childId)?.ageGroup) {
                "3-5 Years" -> 4
                "6-8 Years" -> 6
                "9-12 Years" -> 8
                else -> 8
            }
            val initialDrank = adjustment.coerceAtLeast(0)
            val newLog = WaterLog(
                childId = childId,
                glassesDrank = initialDrank,
                goalGlasses = goal,
                dateString = date
            )
            petBuddyDao.insertWaterLog(newLog)

            if (initialDrank >= goal) {
                rewardChild(childId, coins = 15, stars = 1, xp = 15)
            }
        }
    }

    // --- SAVINGS PIGGY BANK ---
    suspend fun addSavings(childId: Int, amount: Int, note: String) {
        val progress = SavingsProgress(
            childId = childId,
            amount = amount,
            note = note
        )
        petBuddyDao.insertSavings(progress)

        // Match XP & Happiness for savings!
        rewardChild(childId, coins = 0, stars = 1, xp = 20)
    }

    // --- LEARNING SYSTEM ---
    suspend fun logNewLearning(childId: Int, type: String, content: String, details: String) {
        val log = LearningLog(
            childId = childId,
            type = type,
            content = content,
            details = details
        )
        petBuddyDao.insertLearning(log)

        // Children get rewarded highly for learning!
        rewardChild(childId, coins = 25, stars = 2, xp = 30)
    }

    // --- MEMORIES ---
    suspend fun saveMemory(childId: Int, description: String, mood: String, photoAvatar: String, voiceDurationSec: Int) {
        val journal = MemoryJournal(
            childId = childId,
            description = description,
            mood = mood,
            photoAvatarType = photoAvatar,
            hasVoiceNote = voiceDurationSec > 0,
            voiceDurationSeconds = voiceDurationSec
        )
        petBuddyDao.insertMemory(journal)
        rewardChild(childId, coins = 10, stars = 1, xp = 10)
    }

    // --- PET SHOP EQUIPMENT ---
    suspend fun buyAccessory(childId: Int, accessory: PetAccessory): Boolean {
        val child = petBuddyDao.getChildProfileById(childId) ?: return false
        if (child.coins < accessory.costCoins) return false

        // 1. Mark as purchased
        petBuddyDao.purchaseAccessory(childId, accessory.name)

        // 2. Reduce coins
        val updatedChild = child.copy(
            coins = child.coins - accessory.costCoins,
            petHappiness = (child.petHappiness + 20).coerceAtMost(100)
        )
        petBuddyDao.updateChildProfile(updatedChild)
        return true
    }

    suspend fun toggleEquipAccessory(childId: Int, accessory: PetAccessory) {
        if (!accessory.isPurchased) return

        if (accessory.isEquipped) {
            // Unequip
            petBuddyDao.setAccessoryEquipped(childId, accessory.name, false)
        } else {
            // Equipped items of same type must be unequipped first
            petBuddyDao.unequipAllOfType(childId, accessory.type)
            // Equip this one
            petBuddyDao.setAccessoryEquipped(childId, accessory.name, true)

            // Feeding instantly gives pet benefits
            if (accessory.type == "food") {
                val child = petBuddyDao.getChildProfileById(childId)
                if (child != null) {
                    val newHunger = (child.petHunger - 30).coerceAtLeast(0) // Lower is full
                    val newHappiness = (child.petHappiness + 25).coerceAtMost(100)
                    petBuddyDao.updateChildProfile(child.copy(
                        petHunger = newHunger,
                        petHappiness = newHappiness
                    ))
                    // Mark food as unequipped item immediately after eating it!
                    petBuddyDao.setAccessoryEquipped(childId, accessory.name, false)
                }
            } else if (accessory.type == "toy") {
                val child = petBuddyDao.getChildProfileById(childId)
                if (child != null) {
                    val newEnergy = (child.petEnergy - 15).coerceAtLeast(20) // Playing uses energy
                    val newHappiness = (child.petHappiness + 30).coerceAtMost(100)
                    petBuddyDao.updateChildProfile(child.copy(
                        petEnergy = newEnergy,
                        petHappiness = newHappiness
                    ))
                    // Play event complete, unequip play toy
                    petBuddyDao.setAccessoryEquipped(childId, accessory.name, false)
                }
            }
        }
    }

    // --- OTHER INTERACTIVE PET STATS MODIFIERS ---
    suspend fun petPlay(childId: Int) {
        val child = petBuddyDao.getChildProfileById(childId) ?: return
        if (child.petEnergy <= 10) return // too tired

        val updatedChild = child.copy(
            petHappiness = (child.petHappiness + 15).coerceAtMost(100),
            petEnergy = (child.petEnergy - 10).coerceAtLeast(0),
            xp = child.xp + 5
        )
        // Check levels
        applyLevelUpdates(updatedChild)
    }

    suspend fun petSleep(childId: Int) {
        val child = petBuddyDao.getChildProfileById(childId) ?: return
        val updatedChild = child.copy(
            petEnergy = 100, // sleep restores perfectly
            petHappiness = (child.petHappiness - 10).coerceAtLeast(30), // but slight loss in fun
            petHunger = (child.petHunger + 15).coerceAtMost(100)
        )
        petBuddyDao.updateChildProfile(updatedChild)
    }

    // --- AD REWARD SYSTEM (SIMULATED ADMOB) ---
    suspend fun triggerAdReward(childId: Int, isStarredReward: Boolean) {
        if (isStarredReward) {
            rewardChild(childId, coins = 0, stars = 10, xp = 20)
        } else {
            rewardChild(childId, coins = 60, stars = 0, xp = 20)
        }
    }

    // --- HELPER UTILS ---
    private suspend fun rewardChild(childId: Int, coins: Int, stars: Int, xp: Int) {
        val child = petBuddyDao.getChildProfileById(childId) ?: return
        val newXp = child.xp + xp
        val neededXpForLevel = child.level * 100
        val levelUp = newXp >= neededXpForLevel

        val updated = child.copy(
            coins = child.coins + coins,
            stars = child.stars + stars,
            xp = if (levelUp) newXp - neededXpForLevel else newXp,
            level = if (levelUp) child.level + 1 else child.level,
            petHappiness = (child.petHappiness + 5).coerceAtMost(100)
        )
        petBuddyDao.updateChildProfile(updated)
    }

    private suspend fun applyLevelUpdates(child: ChildProfile) {
        val needed = child.level * 100
        if (child.xp >= needed) {
            petBuddyDao.updateChildProfile(
                child.copy(
                    level = child.level + 1,
                    xp = child.xp - needed
                )
            )
        } else {
            petBuddyDao.updateChildProfile(child)
        }
    }
}
