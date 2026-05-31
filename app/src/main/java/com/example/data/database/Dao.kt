package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PetBuddyDao {

    // --- PARENT ---
    @Query("SELECT * FROM parent_accounts LIMIT 1")
    fun getParentAccount(): Flow<ParentAccount?>

    @Query("SELECT * FROM parent_accounts WHERE email = :email LIMIT 1")
    suspend fun getParentByEmail(email: String): ParentAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParent(parent: ParentAccount)

    @Query("UPDATE parent_accounts SET isCurrentActive = :isActive WHERE email = :email")
    suspend fun setParentActive(email: String, isActive: Boolean)

    // --- CHILD PROFILES ---
    @Query("SELECT * FROM child_profiles WHERE parentEmail = :parentEmail")
    fun getChildrenForParent(parentEmail: String): Flow<List<ChildProfile>>

    @Query("SELECT * FROM child_profiles WHERE id = :id LIMIT 1")
    suspend fun getChildProfileById(id: Int): ChildProfile?

    @Query("SELECT * FROM child_profiles WHERE id = :id LIMIT 1")
    fun observeChildProfileById(id: Int): Flow<ChildProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChildProfile(child: ChildProfile): Long

    @Update
    suspend fun updateChildProfile(child: ChildProfile)

    // --- TASKS ---
    @Query("SELECT * FROM tasks WHERE childId = :childId ORDER BY createdTimestamp ASC")
    fun getTasksForChild(childId: Int): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    // --- SAVINGS ---
    @Query("SELECT * FROM savings_progress WHERE childId = :childId ORDER BY timestamp DESC")
    fun getSavingsForChild(childId: Int): Flow<List<SavingsProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavings(savings: SavingsProgress)

    // --- WATER ---
    @Query("SELECT * FROM water_log WHERE childId = :childId AND dateString = :date ORDER BY timestamp DESC LIMIT 1")
    suspend fun getWaterLogByDate(childId: Int, date: String): WaterLog?

    @Query("SELECT * FROM water_log WHERE childId = :childId AND dateString = :date ORDER BY timestamp DESC LIMIT 1")
    fun observeWaterLogByDate(childId: Int, date: String): Flow<WaterLog?>

    @Query("SELECT * FROM water_log WHERE childId = :childId ORDER BY dateString DESC LIMIT 30")
    fun getWaterHistory(childId: Int): Flow<List<WaterLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLog)

    // --- LEARNING LOG ---
    @Query("SELECT * FROM learning_log WHERE childId = :childId ORDER BY timestamp DESC")
    fun getLearningForChild(childId: Int): Flow<List<LearningLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearning(log: LearningLog)

    // --- JOURNAL MEMORIES ---
    @Query("SELECT * FROM memory_journal WHERE childId = :childId ORDER BY timestamp DESC")
    fun getMemoriesForChild(childId: Int): Flow<List<MemoryJournal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryJournal)

    // --- ACCESSORIES ---
    @Query("SELECT * FROM pet_accessories WHERE childId = :childId")
    fun getAccessoriesForChild(childId: Int): Flow<List<PetAccessory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccessory(accessory: PetAccessory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccessories(accessories: List<PetAccessory>)

    @Query("UPDATE pet_accessories SET isPurchased = 1 WHERE childId = :childId AND name = :accessoryName")
    suspend fun purchaseAccessory(childId: Int, accessoryName: String)

    @Query("UPDATE pet_accessories SET isEquipped = :isEq WHERE childId = :childId AND name = :accessoryName")
    suspend fun setAccessoryEquipped(childId: Int, accessoryName: String, isEq: Boolean)

    @Query("UPDATE pet_accessories SET isEquipped = 0 WHERE childId = :childId AND type = :accessoryType")
    suspend fun unequipAllOfType(childId: Int, accessoryType: String)
}
