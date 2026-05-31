package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parent_accounts")
data class ParentAccount(
    @PrimaryKey val email: String,
    val name: String,
    val securityCode: String, // 4-digit code to access parent settings
    val isCurrentActive: Boolean = false
)

@Entity(tableName = "child_profiles")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parentEmail: String,
    val name: String,
    val ageGroup: String, // "3-5 Years", "6-8 Years", "9-12 Years", "13-15 Years"
    val petId: String, // "puppy", "cat", "rabbit", "panda", "dinosaur", "unicorn", "turtle", "fox"
    val petCustomName: String,
    val coins: Int = 100, // Initial coins
    val stars: Int = 5,
    val xp: Int = 0,
    val level: Int = 1,
    val petHappiness: Int = 80, // 0-100
    val petEnergy: Int = 80,    // 0-100
    val petHunger: Int = 40,    // 0-100 (0 is perfect, 100 is starving)
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val title: String,
    val category: String, // "Daily Habits", "Learning", "Chores", "Self-Growth"
    val coinsReward: Int = 20,
    val starsReward: Int = 2,
    val xpReward: Int = 15,
    val isCompleted: Boolean = false,
    val isCompletedCount: Int = 0, // Cumulative completions
    val isCustomCreated: Boolean = false, // Created by parent
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "savings_progress")
data class SavingsProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val amount: Int,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "water_log")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val glassesDrank: Int,
    val goalGlasses: Int = 6,
    val dateString: String, // "YYYY-MM-DD"To simplify daily checks
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_log")
data class LearningLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val type: String, // "Word", "Fact", "Skill"
    val content: String, // e.g. "Metamorphosis"
    val details: String, // e.g. "A change of the form or nature of a thing into a completely different one"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memory_journal")
data class MemoryJournal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val description: String,
    val mood: String, // Emoji representation e.g. "😀", "😎", "😴", "🥳", "🎨"
    val photoAvatarType: String, // Simulated selfie stickers, e.g. "hat_pup", "super_bunny", etc.
    val hasVoiceNote: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pet_accessories")
data class PetAccessory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val name: String, // "Cool Hat", "Wizard Hat", "Red BowTie", "Funny Glasses", "Spacesuit", "Superhero Cape", "Tasty Bone", "Yummy Fish", "Magic Carrot"
    val type: String, // "hat", "glasses", "clothes", "toy", "food"
    val costCoins: Int,
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false,
    val emoji: String
)
