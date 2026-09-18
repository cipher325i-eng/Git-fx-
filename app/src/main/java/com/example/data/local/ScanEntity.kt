package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ChartAnalysis
import org.json.JSONArray

@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val pair: String,
    val timeframe: String,
    val direction: String,
    val confidence: Int,
    val headline: String = "",
    val strategy: String,
    val entryPrice: String,
    val takeProfit: String,
    val stopLoss: String,
    val tp2: String = "",
    val tp3: String = "",
    val confluencesJson: String,
    val phase: String,
    val setupGrade: String,
    val macroBias: String,
    val keyLevelsJson: String,
    val riskReward: String,
    val executionQuality: Int = 87,
    val structuralConfidence: Int = 85,
    val poiScore: Int = 90,
    val status: String = "OPEN", // OPEN, WIN, LOSS
    val keyTagsJson: String = "[]",
    val notes: String = "",
    val imageUri: String? = null
) {
    fun toChartAnalysis(): ChartAnalysis {
        val confluencesList = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(confluencesJson)
            for (i in 0 until jsonArray.length()) {
                confluencesList.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {}

        val keyLevelsList = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(keyLevelsJson)
            for (i in 0 until jsonArray.length()) {
                keyLevelsList.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {}

        val tagsList = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(keyTagsJson)
            for (i in 0 until jsonArray.length()) {
                tagsList.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {}
        if (tagsList.isEmpty()) {
            tagsList.add(if (direction.equals("SELL", ignoreCase = true)) "Strong Bearish" else "Strong Bullish")
            tagsList.add(phase)
            tagsList.add("Institutional Setup")
        }

        val resolvedHeadline = if (headline.isNotBlank()) headline else "$pair: Institutional $phase Setup for $direction Continuation"

        return ChartAnalysis(
            pair = pair,
            timeframe = timeframe,
            direction = direction,
            confidence = confidence,
            headline = resolvedHeadline,
            strategy = strategy,
            entryPrice = entryPrice,
            takeProfit = takeProfit,
            stopLoss = stopLoss,
            tp2 = tp2,
            tp3 = tp3,
            confluences = confluencesList,
            phase = phase,
            setupGrade = setupGrade,
            macroBias = macroBias,
            keyLevels = keyLevelsList,
            riskReward = riskReward,
            executionQuality = executionQuality,
            structuralConfidence = structuralConfidence,
            poiScore = poiScore,
            status = status,
            keyTags = tagsList,
            notes = notes
        )
    }

    companion object {
        fun fromAnalysis(analysis: ChartAnalysis, imageUri: String? = null): ScanEntity {
            val confluencesArray = JSONArray()
            analysis.confluences.forEach { confluencesArray.put(it) }

            val keyLevelsArray = JSONArray()
            analysis.keyLevels.forEach { keyLevelsArray.put(it) }

            val keyTagsArray = JSONArray()
            analysis.keyTags.forEach { keyTagsArray.put(it) }

            val finalHeadline = if (analysis.headline.isNotBlank()) {
                analysis.headline
            } else {
                "${analysis.pair}: Institutional ${analysis.phase} Setup for ${analysis.direction} Continuation"
            }

            return ScanEntity(
                pair = analysis.pair,
                timeframe = analysis.timeframe,
                direction = analysis.direction,
                confidence = analysis.confidence,
                headline = finalHeadline,
                strategy = analysis.strategy,
                entryPrice = analysis.entryPrice,
                takeProfit = analysis.takeProfit,
                stopLoss = analysis.stopLoss,
                tp2 = analysis.tp2,
                tp3 = analysis.tp3,
                confluencesJson = confluencesArray.toString(),
                phase = analysis.phase,
                setupGrade = analysis.setupGrade,
                macroBias = analysis.macroBias,
                keyLevelsJson = keyLevelsArray.toString(),
                riskReward = analysis.riskReward,
                executionQuality = analysis.executionQuality,
                structuralConfidence = analysis.structuralConfidence,
                poiScore = analysis.poiScore,
                status = analysis.status,
                keyTagsJson = keyTagsArray.toString(),
                notes = analysis.notes,
                imageUri = imageUri
            )
        }
    }
}

