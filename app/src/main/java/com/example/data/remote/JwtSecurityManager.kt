package com.example.data.remote

import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object JwtSecurityManager {
    private const val TAG = "JwtSecurityManager"
    private const val DEFAULT_SECRET = "omni_agent_os_super_secure_secret_key_2026"
    
    // In-memory security logs to display in the UI
    private val _securityLogs = MutableList(0) { "" }
    val securityLogs: List<String> get() = _securityLogs.toList()

    var isProxyEnabled: Boolean = true
    var activeToken: String = ""
    var currentSecret: String = DEFAULT_SECRET

    init {
        // Generate a valid token on startup
        generateToken("habu23585@gmail.com", "researcher")
    }

    fun addLog(message: String) {
        val timeStamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        _securityLogs.add(0, "[$timeStamp] $message")
        if (_securityLogs.size > 100) {
            _securityLogs.removeLast()
        }
    }

    fun clearLogs() {
        _securityLogs.clear()
        addLog("Security logs cleared. API Gateway active.")
    }

    fun generateToken(email: String, role: String, expirationMinutes: Int = 60): String {
        try {
            val header = JSONObject().apply {
                put("alg", "HS256")
                put("typ", "JWT")
            }.toString()

            val now = System.currentTimeMillis()
            val exp = now + (expirationMinutes * 60 * 1000)

            val payload = JSONObject().apply {
                put("sub", "omni_user_12345")
                put("email", email)
                put("role", role)
                put("iss", "nextjs-proxy-auth")
                put("iat", now / 1000)
                put("exp", exp / 1000)
                put("scopes", "gemini:query,search:execute")
            }.toString()

            val headerBase64 = base64UrlEncode(header.toByteArray(StandardCharsets.UTF_8))
            val payloadBase64 = base64UrlEncode(payload.toByteArray(StandardCharsets.UTF_8))

            val signature = hmacSha256("$headerBase64.$payloadBase64", currentSecret)
            val signatureBase64 = base64UrlEncode(signature)

            val token = "$headerBase64.$payloadBase64.$signatureBase64"
            activeToken = token
            addLog("JWT Generated successfully for $email (Role: $role). Token starts with: ${token.take(15)}...")
            return token
        } catch (e: Exception) {
            Log.e(TAG, "Error generating JWT", e)
            addLog("Error generating JWT: ${e.message}")
            return ""
        }
    }

    fun verifyToken(token: String): VerificationResult {
        try {
            addLog("API GATEWAY: Intercepted incoming request. Validating token...")
            
            val parts = token.split(".")
            if (parts.size != 3) {
                addLog("VALIDATION FAILURE: Token structure invalid. Must have 3 dot-separated segments.")
                return VerificationResult.Failure("Invalid token structure (must have 3 parts).")
            }

            val headerStr = String(base64UrlDecode(parts[0]), StandardCharsets.UTF_8)
            val payloadStr = String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8)
            val providedSignature = parts[2]

            // Recreate signature
            val calculatedSignature = hmacSha256("${parts[0]}.${parts[1]}", currentSecret)
            val calculatedSignatureBase64 = base64UrlEncode(calculatedSignature)

            if (providedSignature != calculatedSignatureBase64) {
                addLog("VALIDATION FAILURE: Signature mismatch! Token has been tampered with or signed with an incorrect key.")
                return VerificationResult.Failure("Invalid signature. Token integrity compromised.")
            }

            val payloadJson = JSONObject(payloadStr)
            val exp = payloadJson.optLong("exp", 0)
            val currentUnixTime = System.currentTimeMillis() / 1000

            if (exp > 0 && currentUnixTime > exp) {
                addLog("VALIDATION FAILURE: Token has expired! Expired at: ${Date(exp * 1000)}")
                return VerificationResult.Failure("Token expired on ${Date(exp * 1000)}.")
            }

            val email = payloadJson.optString("email", "unknown")
            val role = payloadJson.optString("role", "user")
            
            addLog("VALIDATION SUCCESS: JWT fully authentic. Subject: $email, Role: $role. Routing request...")
            return VerificationResult.Success(email, role, payloadJson)

        } catch (e: Exception) {
            Log.e(TAG, "JWT verification exception", e)
            addLog("VALIDATION EXCEPTION: ${e.message ?: "Unknown decoding error"}")
            return VerificationResult.Failure(e.message ?: "Decoding failed")
        }
    }

    private fun base64UrlEncode(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)
    }

    private fun base64UrlDecode(str: String): ByteArray {
        return Base64.decode(str, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)
    }

    private fun hmacSha256(data: String, secret: String): ByteArray {
        val hmacKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(hmacKey)
        return mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
    }

    sealed class VerificationResult {
        data class Success(val email: String, val role: String, val claims: JSONObject) : VerificationResult()
        data class Failure(val reason: String) : VerificationResult()
    }
}
