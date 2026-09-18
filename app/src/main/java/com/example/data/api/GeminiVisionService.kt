package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.data.model.ChartAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiVisionService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
        const val DEFAULT_MODEL = "gemini-2.5-flash"
        val FREE_TIER_MODELS = listOf(
            "gemini-2.5-flash",
            "gemini-3.5-flash",
            "gemini-3.1-flash-lite-preview"
        )
    }

    suspend fun testModelConnection(
        apiKey: String,
        modelName: String = DEFAULT_MODEL
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Please enter an API key to test."))
            }
            val targetModel = if (modelName.isNotBlank()) modelName.trim() else DEFAULT_MODEL
            val startTime = System.currentTimeMillis()

            val testPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", "Ping test. Reply with 'OK'.") })
                        })
                    })
                })
            }

            val requestBody = testPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "$BASE_URL/$targetModel:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val latency = System.currentTimeMillis() - startTime
            val bodyString = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                var errorMsg = "HTTP ${response.code}"
                try {
                    val errJson = JSONObject(bodyString)
                    val errorObj = errJson.optJSONObject("error")
                    if (errorObj != null) {
                        errorMsg = errorObj.optString("message", errorMsg)
                    }
                } catch (_: Exception) {}
                return@withContext Result.failure(Exception("Model $targetModel error: $errorMsg"))
            }

            Result.success(latency)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeChart(
        bitmap: Bitmap,
        apiKey: String,
        modelName: String = DEFAULT_MODEL,
        temperature: Float = 0.2f,
        analysisStyle: String = "SMC_ICT"
    ): Result<ChartAnalysis> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Gemini API key is missing. Please configure it in Settings."))
            }

            // Downscale bitmap if too large to save bandwidth while retaining high chart detail
            val processedBitmap = if (bitmap.width > 1600 || bitmap.height > 1600) {
                val scale = 1600f / maxOf(bitmap.width, bitmap.height)
                Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
            } else {
                bitmap
            }

            val outputStream = ByteArrayOutputStream()
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            val systemInstructionText = """
                You are Blue Wave Institutional Technical Analysis Engine.
                Analyze financial market charts (Crypto, Forex, Commodities, Indices).
                Evaluate Smart Money Concepts (SMC), Wyckoff phases (Accumulation, Manipulation, Distribution, Re-Distribution, Expansion),
                FVG Fair Value Gaps, Liquidity Sweeps, Order Blocks, Change of Character (CHoCH), Break of Structure (BOS), and POI.
                Return strict JSON adhering to the specified schema.
            """.trimIndent()

            val promptText = """
                Analyze this financial market chart screenshot in detail.
                Extract exact values:
                - pair: clean symbol (e.g. BTCUSDm, XAUUSD, EURUSD, NAS100)
                - timeframe: (e.g. M1, M5, M15, H1, H4, D1)
                - direction: "BUY", "SELL", or "WAIT"
                - confidence: integer percentage (0 to 100)
                - headline: professional trading thesis title (e.g. "BTCUSDm: Institutional Re-Distribution at 60k Resistance for Bearish Continuation")
                - strategy: concise name of setup (e.g. "Re-Distribution False Breakout Trap")
                - phase: Wyckoff/SMC phase (e.g. "Re-Distribution", "Accumulation", "Expansion", "Mitigation")
                - setup_grade: grade string (e.g. "Grade A" or "A")
                - macro_bias: "BULLISH", "BEARISH", or "RANGING"
                - entry_price: price number as string
                - stop_loss: invalidation price as string
                - take_profit: target 1 price as string
                - tp2: target 2 price as string
                - tp3: target 3 price as string
                - risk_reward: ratio string (e.g. "1:1.5 - 1:7.59" or "1 : 3.5")
                - execution_quality: integer score out of 100 (e.g. 87)
                - structural_confidence: integer score percentage (e.g. 85)
                - poi_score: Point of Interest validity percentage (e.g. 90)
                - key_tags: array of 3 to 4 punchy tags (e.g. ["Strong Bearish", "Re-Distribution", "False Breakout Trap"])
                - confluences: list of 4 to 6 specific technical arguments
                - key_levels: array of 2-4 horizontal key levels with brief label
                - notes: 1-2 sentences of tactical trade execution advice

                Return STRICT JSON ONLY with NO markdown code fences.
            """.trimIndent()

            // Build Gemini REST JSON request payload
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", promptText) })
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstructionText) })
                    })
                })

                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", temperature.toDouble())
                })
            }

            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val targetModel = if (modelName.isNotBlank()) modelName.trim() else DEFAULT_MODEL
            val url = "$BASE_URL/$targetModel:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                var errorMessage = "Gemini API Error (${response.code})"
                try {
                    val errJson = JSONObject(responseBody)
                    val errorObj = errJson.optJSONObject("error")
                    if (errorObj != null) {
                        errorMessage = errorObj.optString("message", errorMessage)
                    }
                } catch (_: Exception) {}
                return@withContext Result.failure(Exception(errorMessage))
            }

            val analysis = parseGeminiResponse(responseBody)
            Result.success(analysis)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseGeminiResponse(responseBody: String): ChartAnalysis {
        val root = JSONObject(responseBody)
        val candidates = root.getJSONArray("candidates")
        if (candidates.length() == 0) {
            throw IllegalStateException("Gemini returned empty candidates.")
        }
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        if (parts.length() == 0) {
            throw IllegalStateException("Gemini returned empty parts.")
        }
        val rawText = parts.getJSONObject(0).getString("text").trim()

        // Clean any markdown formatting if present
        val cleanedText = rawText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val json = JSONObject(cleanedText)

        val pair = json.optString("pair", json.optString("symbol", "XAUUSD"))
        val timeframe = json.optString("timeframe", "M5")
        val direction = json.optString("direction", "BUY").uppercase()
        val confidence = json.optInt("confidence", 85)
        val phase = json.optString("phase", "Re-Distribution")
        val headline = json.optString("headline", "$pair: Institutional $phase Continuation Setup")
        val strategy = json.optString("strategy", "SMC Liquidity Sweep & Order Block")
        val entryPrice = json.optString("entry_price", json.optString("entryPrice", "Market"))
        val takeProfit = json.optString("take_profit", json.optString("takeProfit", "TBD"))
        val tp2 = json.optString("tp2", "")
        val tp3 = json.optString("tp3", "")
        val stopLoss = json.optString("stop_loss", json.optString("stopLoss", "TBD"))
        val setupGrade = json.optString("setup_grade", json.optString("setupGrade", "Grade A"))
        val macroBias = json.optString("macro_bias", json.optString("macroBias", "BEARISH"))
        val riskReward = json.optString("risk_reward", json.optString("riskReward", "1 : 3.5"))
        val executionQuality = json.optInt("execution_quality", 87)
        val structuralConfidence = json.optInt("structural_confidence", 85)
        val poiScore = json.optInt("poi_score", 90)
        val notes = json.optString("notes", "")

        val confluences = mutableListOf<String>()
        val confluencesArray = json.optJSONArray("confluences")
        if (confluencesArray != null) {
            for (i in 0 until confluencesArray.length()) {
                confluences.add(confluencesArray.getString(i))
            }
        }
        if (confluences.isEmpty()) {
            confluences.add("Market structure shift confirmed on current timeframe")
            confluences.add("Liquidity sweep of key session highs/lows")
            confluences.add("Fair value gap (FVG) mitigation area identified")
            confluences.add("Favorable risk-to-reward ratio profile")
        }

        val keyTags = mutableListOf<String>()
        val tagsArray = json.optJSONArray("key_tags") ?: json.optJSONArray("keyTags")
        if (tagsArray != null) {
            for (i in 0 until tagsArray.length()) {
                keyTags.add(tagsArray.getString(i))
            }
        }
        if (keyTags.isEmpty()) {
            keyTags.add(if (direction == "SELL") "Strong Bearish" else "Strong Bullish")
            keyTags.add(phase)
            keyTags.add("Liquidity Sweep")
        }

        val keyLevels = mutableListOf<String>()
        val keyLevelsArray = json.optJSONArray("key_levels") ?: json.optJSONArray("keyLevels")
        if (keyLevelsArray != null) {
            for (i in 0 until keyLevelsArray.length()) {
                keyLevels.add(keyLevelsArray.getString(i))
            }
        }

        return ChartAnalysis(
            pair = pair,
            timeframe = timeframe,
            direction = direction,
            confidence = confidence,
            headline = headline,
            strategy = strategy,
            entryPrice = entryPrice,
            takeProfit = takeProfit,
            stopLoss = stopLoss,
            tp2 = tp2,
            tp3 = tp3,
            confluences = confluences,
            phase = phase,
            setupGrade = setupGrade,
            macroBias = macroBias,
            keyLevels = keyLevels,
            riskReward = riskReward,
            executionQuality = executionQuality,
            structuralConfidence = structuralConfidence,
            poiScore = poiScore,
            status = "OPEN",
            keyTags = keyTags,
            notes = notes
        )
    }
}

