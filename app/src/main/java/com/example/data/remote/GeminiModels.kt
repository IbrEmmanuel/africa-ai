package com.example.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null,
    val tools: List<GeminiTool>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiTool(
    val googleSearch: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null,
    val groundingMetadata: GeminiGroundingMetadata? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGroundingMetadata(
    val webSearchQueries: List<String>? = null,
    val groundingChunks: List<GeminiGroundingChunk>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGroundingChunk(
    val web: GeminiWebSource? = null
)

@JsonClass(generateAdapter = true)
data class GeminiWebSource(
    val uri: String? = null,
    val title: String? = null
)
