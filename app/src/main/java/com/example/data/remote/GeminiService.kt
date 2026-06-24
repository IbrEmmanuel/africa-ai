package com.example.data.remote

import com.example.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

class GeminiClient {
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val defaultModel = "gemini-3.5-flash"

    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        jsonMode: Boolean = false,
        history: List<GeminiContent> = emptyList(),
        model: String = defaultModel,
        enableSearch: Boolean = false
    ): String {
        // Enforce JWT API Proxy layer validation if enabled
        if (JwtSecurityManager.isProxyEnabled) {
            val verificationResult = JwtSecurityManager.verifyToken(JwtSecurityManager.activeToken)
            if (verificationResult is JwtSecurityManager.VerificationResult.Failure) {
                return "🔒 [API Gateway HTTP 401 Unauthorized]\n\n" +
                        "Authentication validation failed before forwarding to external services.\n" +
                        "Reason: ${verificationResult.reason}\n\n" +
                        "Please regenerate a valid JWT token in the Security Dashboard to resume secure communication."
            }
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("GeminiClient", "API Key is missing or using placeholder!")
            return "Error: Gemini API key is not configured. Please add your key to the Secrets panel in AI Studio."
        }

        val contents = if (history.isNotEmpty()) {
            history + GeminiContent(parts = listOf(GeminiPart(text = prompt)), role = "user")
        } else {
            listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)), role = "user"))
        }

        val config = if (jsonMode) {
            GeminiGenerationConfig(temperature = 0.2f, responseMimeType = "application/json")
        } else {
            GeminiGenerationConfig(temperature = 0.7f)
        }

        val sysInstructionContent = systemInstruction?.let {
            GeminiContent(parts = listOf(GeminiPart(text = it)))
        }

        val toolsList = if (enableSearch) {
            listOf(GeminiTool(googleSearch = emptyMap()))
        } else {
            null
        }

        val request = GeminiRequest(
            contents = contents,
            generationConfig = config,
            systemInstruction = sysInstructionContent,
            tools = toolsList
        )

        return try {
            val response = RetrofitClient.service.generateContent(model, apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No response from Gemini"

            if (enableSearch) {
                val groundingMetadata = response.candidates?.firstOrNull()?.groundingMetadata
                val realSources = mutableListOf<Map<String, String>>()
                groundingMetadata?.groundingChunks?.forEach { chunk ->
                    val web = chunk.web
                    val uri = web?.uri
                    val title = web?.title
                    if (!uri.isNullOrEmpty() && !title.isNullOrEmpty()) {
                        realSources.add(mapOf("title" to title, "url" to uri))
                    }
                }

                if (realSources.isNotEmpty()) {
                    val jsonArray = JSONArray()
                    realSources.forEach { src ->
                        val obj = JSONObject()
                        obj.put("title", src["title"])
                        obj.put("url", src["url"])
                        jsonArray.put(obj)
                    }
                    val sourcesToken = "\n\n[SOURCES_START]\n${jsonArray.toString(2)}\n[SOURCES_END]"

                    val cleanedText = if (text.contains("[SOURCES_START]")) {
                        text.substringBefore("[SOURCES_START]") + sourcesToken
                    } else {
                        text + sourcesToken
                    }
                    cleanedText
                } else {
                    text
                }
            } else {
                text
            }
        } catch (e: Exception) {
            Log.e("GeminiClient", "Error calling Gemini API: ${e.message}", e)
            "Error: ${e.message ?: "Unknown error occurred"}"
        }
    }
}
