package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE agentName = :agentName ORDER BY timestamp ASC")
    fun getMessagesByAgent(agentName: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}

@Dao
interface LearningCourseDao {
    @Query("SELECT * FROM learning_courses ORDER BY timestamp DESC")
    fun getAllCourses(): Flow<List<LearningCourse>>

    @Query("SELECT * FROM learning_courses WHERE id = :id")
    suspend fun getCourseById(id: Int): LearningCourse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: LearningCourse): Long

    @Update
    suspend fun updateCourse(course: LearningCourse)

    @Query("DELETE FROM learning_courses WHERE id = :id")
    suspend fun deleteCourseById(id: Int)
}

@Dao
interface ResearchReportDao {
    @Query("SELECT * FROM research_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ResearchReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ResearchReport): Long

    @Query("DELETE FROM research_reports WHERE id = :id")
    suspend fun deleteReportById(id: Int)
}

@Dao
interface AutomatedTaskDao {
    @Query("SELECT * FROM automated_tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<AutomatedTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AutomatedTask): Long

    @Update
    suspend fun updateTask(task: AutomatedTask)

    @Query("DELETE FROM automated_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)
}
