package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class OmniRepository(private val db: AppDatabase) {

    private val chatDao = db.chatMessageDao()
    private val courseDao = db.learningCourseDao()
    private val reportDao = db.researchReportDao()
    private val taskDao = db.automatedTaskDao()

    // --- Chat Messages ---
    fun getAllMessages(): Flow<List<ChatMessage>> = chatDao.getAllMessages()
    fun getMessagesByAgent(agentName: String): Flow<List<ChatMessage>> = chatDao.getMessagesByAgent(agentName)
    suspend fun insertMessage(message: ChatMessage): Long = chatDao.insertMessage(message)
    suspend fun clearAllMessages() = chatDao.clearAllMessages()

    // --- Learning Courses ---
    fun getAllCourses(): Flow<List<LearningCourse>> = courseDao.getAllCourses()
    suspend fun getCourseById(id: Int): LearningCourse? = courseDao.getCourseById(id)
    suspend fun insertCourse(course: LearningCourse): Long = courseDao.insertCourse(course)
    suspend fun updateCourse(course: LearningCourse) = courseDao.updateCourse(course)
    suspend fun deleteCourseById(id: Int) = courseDao.deleteCourseById(id)

    // --- Research Reports ---
    fun getAllReports(): Flow<List<ResearchReport>> = reportDao.getAllReports()
    suspend fun insertReport(report: ResearchReport): Long = reportDao.insertReport(report)
    suspend fun deleteReportById(id: Int) = reportDao.deleteReportById(id)

    // --- Automated Tasks ---
    fun getAllTasks(): Flow<List<AutomatedTask>> = taskDao.getAllTasks()
    suspend fun insertTask(task: AutomatedTask): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: AutomatedTask) = taskDao.updateTask(task)
    suspend fun deleteTaskById(id: Int) = taskDao.deleteTaskById(id)
}
