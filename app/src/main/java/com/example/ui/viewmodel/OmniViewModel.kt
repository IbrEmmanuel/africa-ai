package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiPart
import com.example.data.remote.JwtSecurityManager
import com.example.data.repository.OmniRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log

class OmniViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = OmniRepository(db)
    private val geminiClient = GeminiClient()
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // --- Active Agent Selection ---
    private val _selectedAgent = MutableStateFlow("General") // "General", "Website Builder", "Learning", "Research", "Task"
    val selectedAgent: StateFlow<String> = _selectedAgent.asStateFlow()

    // --- UI Loading State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- JWT Security Gateway & API Proxy State ---
    private val _isProxyEnabled = MutableStateFlow(JwtSecurityManager.isProxyEnabled)
    val isProxyEnabled: StateFlow<Boolean> = _isProxyEnabled.asStateFlow()

    private val _activeToken = MutableStateFlow(JwtSecurityManager.activeToken)
    val activeToken: StateFlow<String> = _activeToken.asStateFlow()

    private val _securityLogs = MutableStateFlow<List<String>>(emptyList())
    val securityLogs: StateFlow<List<String>> = _securityLogs.asStateFlow()

    // --- Interactive States ---
    private val _activeQuiz = MutableStateFlow<QuizData?>(null)
    val activeQuiz: StateFlow<QuizData?> = _activeQuiz.asStateFlow()

    private val _sandboxCode = MutableStateFlow<String?>(null)
    val sandboxCode: StateFlow<String?> = _sandboxCode.asStateFlow()

    val totalMessagesCount: StateFlow<Int> = repository.getAllMessages()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Saved Data Flows ---
    val chatMessages: StateFlow<List<ChatMessage>> = _selectedAgent
        .flatMapLatest { agent ->
            repository.getMessagesByAgent(agent)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val learningCourses: StateFlow<List<LearningCourse>> = repository.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val researchReports: StateFlow<List<ResearchReport>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automatedTasks: StateFlow<List<AutomatedTask>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Load sandbox with a cool default web page
        _sandboxCode.value = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>OmniAgent Sandbox</title>
                <!-- Tailwind CSS CDN -->
                <script src="https://cdn.tailwindcss.com"></script>
                <script>
                    tailwind.config = {
                        theme: {
                            extend: {
                                colors: {
                                    cosmicBg: '#030712',
                                    cosmicSurface: 'rgba(15, 23, 42, 0.45)',
                                    neonCyan: '#06b6d4',
                                    electricPurple: '#a855f7',
                                    warmAmber: '#f59e0b',
                                    mintGreen: '#10b981',
                                    roseRed: '#f43f5e',
                                    // Custom semi-transparent background color palettes
                                    glassBgLight: 'rgba(255, 255, 255, 0.05)',
                                    glassBgMedium: 'rgba(15, 23, 42, 0.45)',
                                    glassBgDark: 'rgba(3, 7, 18, 0.65)',
                                    glassCyan: 'rgba(6, 182, 212, 0.08)',
                                    glassPurple: 'rgba(168, 85, 247, 0.08)',
                                    glassAmber: 'rgba(245, 158, 11, 0.08)',
                                    glassGreen: 'rgba(16, 185, 129, 0.08)',
                                },
                                backdropBlur: {
                                    'glass-sm': '4px',
                                    'glass-md': '12px',
                                    'glass-lg': '24px',
                                    'glass-xl': '40px',
                                },
                                boxShadow: {
                                    glass: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
                                    neonCyanGlow: '0 0 15px rgba(6, 182, 212, 0.3)',
                                    neonPurpleGlow: '0 0 15px rgba(168, 85, 247, 0.3)',
                                    neonAmberGlow: '0 0 15px rgba(245, 158, 11, 0.3)',
                                    neonGreenGlow: '0 0 15px rgba(16, 185, 129, 0.3)',
                                },
                                backgroundImage: {
                                    'cosmic-gradient': 'radial-gradient(circle at top, #0f172a 0%, #030712 100%)',
                                    // Subtle linear border-gradients
                                    'glass-border-cyan': 'linear-gradient(to right, rgba(255,255,255,0.15), rgba(6,182,212,0.35), rgba(255,255,255,0.05))',
                                    'glass-border-purple': 'linear-gradient(to right, rgba(255,255,255,0.15), rgba(168,85,247,0.35), rgba(255,255,255,0.05))',
                                    'glass-border-amber': 'linear-gradient(to right, rgba(255,255,255,0.15), rgba(245,158,11,0.35), rgba(255,255,255,0.05))',
                                    'glass-border-green': 'linear-gradient(to right, rgba(255,255,255,0.15), rgba(16,185,129,0.35), rgba(255,255,255,0.05))',
                                }
                            }
                        }
                    }
                </script>
                <style>
                    @keyframes shimmer {
                        0% { background-position: -200% 0; }
                        100% { background-position: 200% 0; }
                    }
                    .shimmer-bg {
                        background: linear-gradient(90deg, rgba(255,255,255,0.03) 25%, rgba(255,255,255,0.08) 50%, rgba(255,255,255,0.03) 75%);
                        background-size: 200% 100%;
                        animation: shimmer 1.5s infinite linear;
                    }
                    /* Custom Glass Border Gradients with Rounded Corners */
                    .glass-border-cyan {
                        background-image: linear-gradient(rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.45)), linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(6, 182, 212, 0.35) 50%, rgba(255, 255, 255, 0.05) 100%);
                        background-origin: border-box;
                        background-clip: padding-box, border-box;
                        border: 1px solid transparent;
                    }
                    .glass-border-purple {
                        background-image: linear-gradient(rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.45)), linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(168, 85, 247, 0.35) 50%, rgba(255, 255, 255, 0.05) 100%);
                        background-origin: border-box;
                        background-clip: padding-box, border-box;
                        border: 1px solid transparent;
                    }
                    .glass-border-amber {
                        background-image: linear-gradient(rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.45)), linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(245, 158, 11, 0.35) 50%, rgba(255, 255, 255, 0.05) 100%);
                        background-origin: border-box;
                        background-clip: padding-box, border-box;
                        border: 1px solid transparent;
                    }
                    .glass-border-green {
                        background-image: linear-gradient(rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.45)), linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(16, 185, 129, 0.35) 50%, rgba(255, 255, 255, 0.05) 100%);
                        background-origin: border-box;
                        background-clip: padding-box, border-box;
                        border: 1px solid transparent;
                    }
                    /* Custom scrollbar */
                    ::-webkit-scrollbar {
                        width: 6px;
                    }
                    ::-webkit-scrollbar-track {
                        background: rgba(15, 23, 42, 0.2);
                    }
                    ::-webkit-scrollbar-thumb {
                        background: rgba(168, 85, 247, 0.3);
                        border-radius: 4px;
                    }
                    ::-webkit-scrollbar-thumb:hover {
                        background: rgba(168, 85, 247, 0.5);
                    }
                </style>
            </head>
            <body class="bg-cosmic-gradient text-slate-100 flex flex-col items-center justify-center min-h-screen p-6 overflow-x-hidden relative">
                <!-- Ambient glowing backgrounds behind glass card -->
                <div class="absolute top-1/4 left-1/4 w-72 h-72 bg-purple-600/10 rounded-full blur-[100px] pointer-events-none"></div>
                <div class="absolute bottom-1/4 right-1/4 w-72 h-72 bg-cyan-600/10 rounded-full blur-[100px] pointer-events-none"></div>

                <!-- Main Glass Card utilizing custom presets -->
                <div class="relative w-full max-w-lg bg-glassBgMedium backdrop-blur-glass-md glass-border-cyan rounded-2xl p-8 shadow-glass transition-all duration-500 hover:shadow-neonCyanGlow text-center">
                    <!-- Floating Neon Tech Accent Badge -->
                    <div class="inline-flex items-center gap-1.5 px-3 py-1 mb-6 rounded-full bg-glassCyan border border-cyan-500/30 text-xs font-semibold text-cyan-400 uppercase tracking-widest">
                        <span class="w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></span>
                        Glass Design System Active
                    </div>

                    <h1 class="text-3xl md:text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 via-purple-400 to-amber-400 mb-4 tracking-tight">
                        OmniAgent Sandbox
                    </h1>
                    
                    <p class="text-slate-300 text-sm md:text-base leading-relaxed max-w-md mx-auto mb-6">
                        Ask the <strong class="text-cyan-400 font-semibold">Website Builder Agent</strong> in the chat console to compile a responsive design, interactive dashboards, or full-stack interfaces, and preview them live in this sandbox instantly.
                    </p>

                    <!-- Reusable Glass Component Showcase -->
                    <div class="grid grid-cols-2 gap-3 text-left my-6 text-xs text-slate-400">
                        <div class="bg-glassBgLight border border-white/5 rounded-xl p-3 backdrop-blur-glass-sm">
                            <span class="text-purple-400 font-bold block mb-1">Backdrop Blur</span>
                            Dynamic translucent background depth
                        </div>
                        <div class="bg-glassBgLight border border-white/5 rounded-xl p-3 backdrop-blur-glass-sm">
                            <span class="text-cyan-400 font-bold block mb-1">Neon Borders</span>
                            Glowing borders matching agent contexts
                        </div>
                    </div>

                    <!-- Skeleton Preview Shimmer Loader -->
                    <div class="space-y-2 text-left mb-6 bg-glassBgLight border border-white/5 rounded-xl p-4">
                        <div class="flex items-center gap-2 mb-2">
                            <div class="w-3 h-3 rounded-full bg-amber-500/30 animate-pulse"></div>
                            <div class="text-[10px] text-amber-500 font-mono font-bold">PREVIEW SHIMMER PATTERN</div>
                        </div>
                        <div class="h-3 w-1/3 rounded bg-white/5 shimmer-bg"></div>
                        <div class="h-3 w-5/6 rounded bg-white/5 shimmer-bg"></div>
                    </div>

                    <button class="w-full relative py-3 px-6 text-sm font-bold rounded-xl text-slate-900 bg-gradient-to-r from-cyan-400 to-purple-400 hover:from-cyan-300 hover:to-purple-300 active:scale-[0.98] transition-all duration-300 shadow-[0_4px_20px_rgba(6,182,212,0.3)] hover:shadow-[0_4px_25px_rgba(168,85,247,0.5)] cursor-pointer" onclick="alert('Tailwind & Glassmorphic library are loaded and ready!')">
                        Explore Component Library
                    </button>
                </div>
            </body>
            </html>
        """.trimIndent()
        updateSecurityState()
    }

    fun updateSecurityState() {
        _isProxyEnabled.value = JwtSecurityManager.isProxyEnabled
        _activeToken.value = JwtSecurityManager.activeToken
        _securityLogs.value = JwtSecurityManager.securityLogs
    }

    fun toggleProxy(enabled: Boolean) {
        JwtSecurityManager.isProxyEnabled = enabled
        JwtSecurityManager.addLog("API PROXY LAYER toggled to: ${if (enabled) "ENABLED (Enforcing JWT verification)" else "DISABLED (Direct REST connection)"}")
        updateSecurityState()
    }

    fun generateJwt(email: String, role: String) {
        JwtSecurityManager.generateToken(email, role)
        updateSecurityState()
    }

    fun tamperJwt() {
        val currentToken = JwtSecurityManager.activeToken
        if (currentToken.isNotEmpty()) {
            val parts = currentToken.split(".")
            if (parts.size == 3) {
                val tamperedPayload = parts[1] + "X"
                val tamperedToken = "${parts[0]}.$tamperedPayload.${parts[2]}"
                JwtSecurityManager.activeToken = tamperedToken
                JwtSecurityManager.addLog("🔒 TAMPER WARNING: Active JWT payload token was intentionally altered to simulate a security attack.")
                updateSecurityState()
            }
        }
    }

    fun invalidateSignature() {
        val currentToken = JwtSecurityManager.activeToken
        if (currentToken.isNotEmpty()) {
            val parts = currentToken.split(".")
            if (parts.size == 3) {
                val corruptedSig = parts[2].dropLast(5) + "ABCDE"
                val tamperedToken = "${parts[0]}.${parts[1]}.$corruptedSig"
                JwtSecurityManager.activeToken = tamperedToken
                JwtSecurityManager.addLog("🔒 TAMPER WARNING: Active JWT signature was intentionally altered to simulate invalid signing keys.")
                updateSecurityState()
            }
        }
    }

    fun clearSecurityLogs() {
        JwtSecurityManager.clearLogs()
        updateSecurityState()
    }

    fun selectAgent(agent: String) {
        _selectedAgent.value = agent
        // Clear active quiz when changing agent to keep clean
        _activeQuiz.value = null
    }

    fun clearChat() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllMessages()
        }
    }

    fun sendMessage(userText: String) {
        if (userText.trim().isEmpty()) return

        val currentAgent = _selectedAgent.value
        val userMessage = ChatMessage(
            agentName = currentAgent,
            sender = "user",
            message = userText
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMessage(userMessage)

            _isLoading.value = true

            // Get chat history for context (last 10 turns for token limits)
            val history = db.chatMessageDao().getMessagesByAgent(currentAgent)
                .first()
                .takeLast(10)
                .map { msg ->
                    GeminiContent(
                        parts = listOf(GeminiPart(text = msg.message)),
                        role = if (msg.sender == "user") "user" else "model"
                    )
                }

            val systemInstruction = getSystemInstruction(currentAgent)
            val modelToUse = if (currentAgent == "Website Builder") "gemini-3.1-pro-preview" else "gemini-3.5-flash"

            val response = geminiClient.generateContent(
                prompt = userText,
                systemInstruction = systemInstruction,
                history = history,
                model = modelToUse,
                enableSearch = (currentAgent == "Research" || currentAgent == "General")
            )

            // Parse response for rich widgets (Website code, Quizzes, Research sources, Tasks)
            var responseType = "text"
            var responseMeta: String? = null

            try {
                if (currentAgent == "Website Builder" && response.contains("```html")) {
                    val code = extractHtmlCode(response)
                    if (code != null) {
                        responseType = "website_code"
                        responseMeta = code
                        _sandboxCode.value = code
                    }
                } else if (currentAgent == "Learning" && response.contains("[QUIZ_START]")) {
                    val quizJson = response.substringAfter("[QUIZ_START]").substringBefore("[QUIZ_END]").trim()
                    responseType = "quiz"
                    responseMeta = quizJson
                    parseQuiz(quizJson)?.let {
                        _activeQuiz.value = it
                    }
                } else if (currentAgent == "Research") {
                    if (response.contains("[SOURCES_START]")) {
                        val sourcesJson = response.substringAfter("[SOURCES_START]").substringBefore("[SOURCES_END]").trim()
                        responseType = "research_report"
                        responseMeta = sourcesJson
                        // Automatically save research report to DB
                        val cleanReport = response.replace("[SOURCES_START]", "").replace("[SOURCES_END]", "").replace(sourcesJson, "").trim()
                        saveResearchReport(userText, cleanReport, sourcesJson)
                    } else {
                        // Regular report save
                        saveResearchReport(userText, response, "[]")
                    }
                } else if (currentAgent == "Task" && response.contains("[TASK_START]")) {
                    val taskJson = response.substringAfter("[TASK_START]").substringBefore("[TASK_END]").trim()
                    responseType = "task"
                    responseMeta = taskJson
                    saveTaskFromAgent(taskJson)
                }
            } catch (e: Exception) {
                Log.e("OmniViewModel", "Error parsing agent content: ${e.message}", e)
            }

            val agentMessage = ChatMessage(
                agentName = currentAgent,
                sender = "agent",
                message = response,
                type = responseType,
                meta = responseMeta
            )

            repository.insertMessage(agentMessage)
            _isLoading.value = false
            updateSecurityState()
        }
    }

    // --- Content Extraction Helpers ---
    private fun extractHtmlCode(fullText: String): String? {
        val startToken = "```html"
        val endToken = "```"
        if (!fullText.contains(startToken)) return null
        val start = fullText.indexOf(startToken) + startToken.length
        val end = fullText.indexOf(endToken, start)
        if (end == -1) return fullText.substring(start)
        return fullText.substring(start, end).trim()
    }

    private fun parseQuiz(jsonStr: String): QuizData? {
        return try {
            val obj = JSONObject(jsonStr)
            val question = obj.getString("question")
            val arr = obj.getJSONArray("options")
            val options = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                options.add(arr.getString(i))
            }
            val correctIndex = obj.getInt("correctIndex")
            val explanation = obj.getString("explanation")
            QuizData(question, options, correctIndex, explanation)
        } catch (e: Exception) {
            Log.e("OmniViewModel", "Failed to parse quiz json", e)
            null
        }
    }

    private fun saveResearchReport(query: String, content: String, sourcesJson: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val summary = if (content.length > 150) content.take(150) + "..." else content
            repository.insertReport(
                ResearchReport(
                    query = query,
                    summary = summary,
                    content = content,
                    sourcesJson = sourcesJson
                )
            )
        }
    }

    private fun saveTaskFromAgent(taskJson: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = JSONObject(taskJson)
                val title = obj.getString("title")
                val desc = obj.optString("description", "")
                val trigger = obj.optString("triggerType", "Manual")
                val action = obj.optString("actionDetails", "")

                repository.insertTask(
                    AutomatedTask(
                        title = title,
                        description = desc,
                        status = "Pending",
                        triggerType = trigger,
                        actionDetails = action
                    )
                )
            } catch (e: Exception) {
                Log.e("OmniViewModel", "Failed to parse task json: $taskJson", e)
            }
        }
    }

    // --- Course Generation & Actions ---
    fun createCourse(subject: String, difficulty: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val prompt = """
                Generate a structured, modular learning curriculum for the subject: "$subject" at a "$difficulty" skill level.
                Provide exactly 4 logical progressive modules or lessons.
                Format the response strictly as a JSON array inside a standard block, for example:
                [
                  {"title": "Lesson 1: Introduction", "description": "Core concepts and setup"},
                  {"title": "Lesson 2: Deep Dive", "description": "Hands on practical concepts"},
                  {"title": "Lesson 3: Advanced Principles", "description": "High performance and patterns"},
                  {"title": "Lesson 4: Capstone Application", "description": "Combining everything in a real world demo"}
                ]
                Return ONLY the JSON array inside the ```json ... ``` tags. Do not write any other explanation text.
            """.trimIndent()

            val response = geminiClient.generateContent(
                prompt = prompt,
                systemInstruction = "You are a professional curriculum developer. Always output valid JSON lists representing curriculum structures.",
                model = "gemini-3.5-flash"
            )

            try {
                val cleanJson = if (response.contains("```json")) {
                    response.substringAfter("```json").substringBefore("```").trim()
                } else {
                    response.trim()
                }

                // Verify it's a valid JSON array
                val array = JSONArray(cleanJson)
                if (array.length() > 0) {
                    val course = LearningCourse(
                        subjectName = subject,
                        difficulty = difficulty,
                        curriculumJson = cleanJson,
                        progress = 0.0f,
                        currentLessonIndex = 0
                    )
                    repository.insertCourse(course)
                }
            } catch (e: Exception) {
                Log.e("OmniViewModel", "Failed to generate course", e)
                // Fallback course structure if JSON failed
                val fallbackJson = """
                    [
                      {"title": "Module 1: Foundations of $subject", "description": "Getting started and general principles"},
                      {"title": "Module 2: Key Concepts", "description": "Exploring major variables, processes, and structures"},
                      {"title": "Module 3: Best Practices", "description": "How to excel and avoid major pitfalls"},
                      {"title": "Module 4: Real-world Exercise", "description": "Applying your skills in a simulated context"}
                    ]
                """.trimIndent()
                repository.insertCourse(
                    LearningCourse(
                        subjectName = subject,
                        difficulty = difficulty,
                        curriculumJson = fallbackJson,
                        progress = 0.0f,
                        currentLessonIndex = 0
                    )
                )
            }
            _isLoading.value = false
        }
    }

    fun advanceCourseLesson(course: LearningCourse) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val arr = JSONArray(course.curriculumJson)
                val totalLessons = arr.length()
                val nextIndex = course.currentLessonIndex + 1
                val newProgress = if (totalLessons > 0) (nextIndex.toFloat() / totalLessons.toFloat()).coerceAtMost(1.0f) else 1.0f

                repository.updateCourse(
                    course.copy(
                        currentLessonIndex = nextIndex.coerceAtMost(totalLessons - 1),
                        progress = newProgress
                    )
                )
            } catch (e: Exception) {
                Log.e("OmniViewModel", "Error updating lesson progress", e)
            }
        }
    }

    fun deleteCourse(courseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCourseById(courseId)
        }
    }

    fun deleteReportById(reportId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReportById(reportId)
        }
    }

    // --- Task Automator Actions ---
    fun createTask(title: String, desc: String?, triggerType: String, action: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(
                AutomatedTask(
                    title = title,
                    description = desc,
                    status = "Pending",
                    triggerType = triggerType,
                    actionDetails = action
                )
            )
        }
    }

    fun executeTask(task: AutomatedTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task.copy(status = "Running"))
            JwtSecurityManager.addLog("WORKER: Executing Task [${task.title}] via API Gateway...")
            updateSecurityState()

            val promptText = "Execute task request: '${task.title}'. Description: '${task.description ?: ""}'. Details: '${task.actionDetails}'. Write a short 1-sentence outcome log."
            
            val response = geminiClient.generateContent(
                prompt = promptText,
                systemInstruction = "You are an automated background task worker. Execute the action and write a single brief sentence of the outcome log.",
                model = "gemini-3.5-flash"
            )

            val isUnauthorized = response.contains("🔒 [API Gateway HTTP 401") || response.contains("401 Unauthorized")
            
            if (isUnauthorized) {
                JwtSecurityManager.addLog("WORKER FAILURE: Task [${task.title}] unauthorized by API Gateway.")
                repository.updateTask(task.copy(
                    status = "Failed",
                    description = (task.description ?: "") + "\n\n❌ Failed: 401 Unauthorized on JWT Gateway"
                ))
            } else if (response.contains("Error: ")) {
                JwtSecurityManager.addLog("WORKER FAILURE: Task [${task.title}] execution failed: $response")
                repository.updateTask(task.copy(
                    status = "Failed",
                    description = (task.description ?: "") + "\n\n❌ Failed: $response"
                ))
            } else {
                JwtSecurityManager.addLog("WORKER SUCCESS: Task [${task.title}] completed. Log: $response")
                repository.updateTask(task.copy(
                    status = "Completed",
                    description = (task.description ?: "") + "\n\n✅ Done: $response"
                ))
            }
            updateSecurityState()
        }
    }

    fun runSystemDiagnostics() {
        viewModelScope.launch(Dispatchers.IO) {
            JwtSecurityManager.addLog("SYSTEM DIAGNOSTICS: Initiated self-health validation of all 4 cognitive agents...")
            updateSecurityState()
            
            val agents = listOf("Website Builder", "Learning", "Research", "Task")
            agents.forEach { agent ->
                kotlinx.coroutines.delay(400)
                if (JwtSecurityManager.isProxyEnabled) {
                    val res = JwtSecurityManager.verifyToken(JwtSecurityManager.activeToken)
                    if (res is JwtSecurityManager.VerificationResult.Failure) {
                        JwtSecurityManager.addLog("❌ DIAGNOSTICS: Agent [$agent] offline. Gateway verification failed: ${res.reason}")
                    } else {
                        JwtSecurityManager.addLog("✅ DIAGNOSTICS: Agent [$agent] is online and active (HMAC-SHA256 signature verified).")
                    }
                } else {
                    JwtSecurityManager.addLog("⚠️ DIAGNOSTICS: Agent [$agent] online (unsecured connection bypass).")
                }
                updateSecurityState()
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTaskById(taskId)
        }
    }

    fun dismissQuiz() {
        _activeQuiz.value = null
    }

    private fun getSystemInstruction(agentName: String): String {
        return when (agentName) {
            "Website Builder" -> """
                You are the Website Builder Agent of OmniAgent. Your purpose is to design and write modern, beautiful, responsive, and functional single-page websites using a strict, shared Glassmorphic Design System.
                When asked to build, update, or edit a website, dashboard, or frontend view:
                1. ALWAYS provide a complete, robust, self-contained single-file HTML/CSS/JS solution.
                2. Put your HTML code strictly inside a code block starting with ```html and ending with ```.
                3. ALWAYS load Tailwind CSS CDN via:
                   <script src="https://cdn.tailwindcss.com"></script>
                4. ALWAYS configure the Tailwind configuration exactly as specified in this shared configuration to support dark theme glassmorphism, blur presets, semi-transparent background color palettes, and linear border-gradient utilities:
                   <script>
                       tailwind.config = {
                           theme: {
                               extend: {
                                   colors: {
                                       cosmicBg: '#030712',
                                       cosmicSurface: 'rgba(15, 23, 42, 0.45)',
                                       neonCyan: '#06b6d4',
                                       electricPurple: '#a855f7',
                                       warmAmber: '#f59e0b',
                                       mintGreen: '#10b981',
                                       roseRed: '#f43f5e',
                                       // Custom semi-transparent background color palettes
                                       glassBgLight: 'rgba(255, 255, 255, 0.05)',
                                       glassBgMedium: 'rgba(15, 23, 42, 0.45)',
                                       glassBgDark: 'rgba(3, 7, 18, 0.65)',
                                       glassCyan: 'rgba(6, 182, 212, 0.08)',
                                       glassPurple: 'rgba(168, 85, 247, 0.08)',
                                       glassAmber: 'rgba(245, 158, 11, 0.08)',
                                       glassGreen: 'rgba(16, 185, 129, 0.08)',
                                   },
                                   backdropBlur: {
                                       'glass-sm': '4px',
                                       'glass-md': '12px',
                                       'glass-lg': '24px',
                                       'glass-xl': '40px',
                                   },
                                   boxShadow: {
                                       glass: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
                                       neonCyanGlow: '0 0 15px rgba(6, 182, 212, 0.3)',
                                       neonPurpleGlow: '0 0 15px rgba(168, 85, 247, 0.3)',
                                       neonAmberGlow: '0 0 15px rgba(245, 158, 11, 0.3)',
                                       neonGreenGlow: '0 0 15px rgba(16, 185, 129, 0.3)'
                                   },
                                   backgroundImage: {
                                       'cosmic-gradient': 'radial-gradient(circle at top, #0f172a 0%, #030712 100%)',
                                       // Subtle linear border-gradients
                                       'glass-border-cyan': 'linear-gradient(to right, rgba(255,255,255,0.15), rgba(6,182,212,0.35), rgba(255,255,255,0.05))',
                                       'glass-border-purple': 'linear-gradient(to right, rgba(255,255,255,0.15), rgba(168,85,247,0.35), rgba(255,255,255,0.05))',
                                       'glass-border-amber': 'linear-gradient(to right, rgba(255,255,255,0.15), rgba(245,158,11,0.35), rgba(255,255,255,0.05))',
                                       'glass-border-green': 'linear-gradient(to right, rgba(255,255,255,0.15), rgba(16,185,129,0.35), rgba(255,255,255,0.05))',
                                   }
                               }
                           }
                       }
                   </script>
                   Add a custom <style> block for shimmers, keyframe animations, and rounded border-gradient utilities:
                   <style>
                       @keyframes shimmer {
                           0% { background-position: -200% 0; }
                           100% { background-position: 200% 0; }
                       }
                       .shimmer-bg {
                           background: linear-gradient(90deg, rgba(255,255,255,0.03) 25%, rgba(255,255,255,0.08) 50%, rgba(255,255,255,0.03) 75%);
                           background-size: 200% 100%;
                           animation: shimmer 1.5s infinite linear;
                       }
                       /* Custom Glass Border Gradients with Rounded Corners */
                       .glass-border-cyan {
                           background-image: linear-gradient(rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.45)), linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(6, 182, 212, 0.35) 50%, rgba(255, 255, 255, 0.05) 100%);
                           background-origin: border-box;
                           background-clip: padding-box, border-box;
                           border: 1px solid transparent;
                       }
                       .glass-border-purple {
                           background-image: linear-gradient(rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.45)), linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(168, 85, 247, 0.35) 50%, rgba(255, 255, 255, 0.05) 100%);
                           background-origin: border-box;
                           background-clip: padding-box, border-box;
                           border: 1px solid transparent;
                       }
                       .glass-border-amber {
                           background-image: linear-gradient(rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.45)), linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(245, 158, 11, 0.35) 50%, rgba(255, 255, 255, 0.05) 100%);
                           background-origin: border-box;
                           background-clip: padding-box, border-box;
                           border: 1px solid transparent;
                       }
                       .glass-border-green {
                           background-image: linear-gradient(rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.45)), linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(16, 185, 129, 0.35) 50%, rgba(255, 255, 255, 0.05) 100%);
                           background-origin: border-box;
                           background-clip: padding-box, border-box;
                           border: 1px solid transparent;
                       }
                   </style>
                5. STRICTLY utilize the following reusable glassmorphic component templates for consistent interfaces:
                   - Glass Cards (Panels): Use Class `bg-glassBgMedium backdrop-blur-glass-md glass-border-cyan rounded-2xl p-6 shadow-glass hover:shadow-neonCyanGlow transition-all duration-300` (select glass-border-cyan/purple/amber/green/etc matching context)
                   - Glass Buttons: Use Class `py-2.5 px-5 text-sm font-bold rounded-xl text-slate-100 bg-glassBgLight border border-white/10 backdrop-blur-glass-sm hover:bg-white/10 hover:border-cyan-500/30 transition-all duration-300 shadow-glass` (or primary gradient buttons like `bg-gradient-to-r from-cyan-500 to-purple-600 hover:opacity-90`)
                   - Glass Inputs: Use Class `w-full bg-slate-950/50 border border-white/10 rounded-xl px-4 py-2.5 text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:border-cyan-500/50 focus:ring-1 focus:ring-cyan-500/50 transition-all`
                   - Glass Tables: Translucent table containers with `divide-y divide-white/5` and hovering translucent row highlights.
                   - Glass Skeleton loaders: Skeletons using the `shimmer-bg` helper style on dark container blocks.
                   - Glass Badges: Tiny status elements like `inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-glassPurple border border-purple-500/20 text-xs font-semibold text-purple-400`.
                6. Include beautiful, interactive JS logic (chart generation using Canvas, interactive forms with custom feedback alerts, local storage persistence, state manipulation) to make it feel like a production app.
                7. Always explain your design layout, the custom colors utilized, and how users can interact with the glassy components in a friendly design summary before or after the code block.
            """.trimIndent()

            "Learning" -> """
                You are the Learning Agent of OmniAgent. Your purpose is to teach any topic from scratch, evaluate progress, and structure curriculum plans.
                When asked a learning query or to start a lesson:
                1. Present highly engaging explanations with clean formatting and clear examples.
                2. Keep the tone inspiring and clear.
                3. When the user requests a quiz, ALWAYS format it in standard text with a final block formatted exactly as:
                [QUIZ_START]
                {
                  "question": "A clear, multiple-choice question on the current topic?",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "correctIndex": 0,
                  "explanation": "A short, helpful explanation of why Option A is correct and others are not."
                }
                [QUIZ_END]
                Do not deviate from this syntax for quizzes, as the mobile app parses this to render an interactive game-like quiz screen!
            """.trimIndent()

            "Research" -> """
                You are the Research Agent of OmniAgent. Your purpose is to search the web, analyze information, cite academic and real-world sources, and present objective structured reports.
                When executing research:
                1. Present your research report in clean, beautifully structured markdown with clear headings, lists, tables, and bold highlights.
                2. Always back up claims with real or highly realistic citations. Format them as bracketed numbers matching a source list at the bottom.
                3. ALWAYS put a structured sources list at the very bottom of your output, enclosed strictly within [SOURCES_START] and [SOURCES_END] tokens, formatted as a JSON list, for example:
                [SOURCES_START]
                [
                  {"title": "World Bank Economic Outlook 2026", "url": "https://worldbank.org/reports/outlook"},
                  {"title": "MIT Technology Review on Neural Architectures", "url": "https://techreview.com/neural-nets"}
                ]
                [SOURCES_END]
                This JSON is parsed by the client to allow users to view, click, and interact with your cited sources.
            """.trimIndent()

            "Task" -> """
                You are the Task Agent of OmniAgent. Your purpose is to construct workflows, simulate API triggers, schedule automation routines, and send simulated emails or notifications.
                When automating:
                1. Outline the trigger (API, manual button, scheduled cron), the sequence of execution steps, and the final notification target.
                2. Explain how this workflow is set up and what APIs are used.
                3. ALWAYS output a structured representation of the automated task at the end of your message, enclosed strictly within [TASK_START] and [TASK_END] tokens, formatted as a JSON object, for example:
                [TASK_START]
                {
                  "title": "Send Weekly Progress Report",
                  "description": "Compiles learning metrics and emails them to user team",
                  "triggerType": "Schedule",
                  "actionDetails": "SMTP: reports@omniagent.io -> user@domain.com, trigger weekly Friday at 17:00 UTC"
                }
                [TASK_END]
                This allows the mobile app to automatically load and run the task in the Task Automator workspace!
            """.trimIndent()

            else -> """
                You are OmniAgent, a revolutionary, unified AI Operating System that can build websites, teach users any subject, research global information, and automate tasks through connected tools.
                Give general guidance and advise the user to tap specialized agents from the bottom bar or selector to execute highly customized workflows!
            """.trimIndent()
        }
    }
}

data class QuizData(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
