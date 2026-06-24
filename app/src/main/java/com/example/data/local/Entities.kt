package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val agentName: String, // "General", "Website Builder", "Learning", "Research", "Task"
    val sender: String, // "user", "agent"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "text", // "text", "website_code", "lesson", "quiz", "research_report"
    val meta: String? = null // Moshi-encoded or custom JSON metadata
)

@Entity(tableName = "learning_courses")
data class LearningCourse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectName: String,
    val difficulty: String,
    val progress: Float = 0.0f,
    val curriculumJson: String, // JSON list of modules/lessons
    val currentLessonIndex: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "research_reports")
data class ResearchReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val summary: String,
    val content: String,
    val sourcesJson: String, // JSON list of sources
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "automated_tasks")
data class AutomatedTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val status: String, // "Pending", "Running", "Completed", "Failed"
    val triggerType: String, // "Manual", "Schedule", "API"
    val actionDetails: String,
    val timestamp: Long = System.currentTimeMillis()
)
