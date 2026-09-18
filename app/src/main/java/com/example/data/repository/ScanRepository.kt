package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.api.GeminiVisionService
import com.example.data.local.ApiKeyStorage
import com.example.data.local.ScanDao
import com.example.data.local.ScanEntity
import com.example.data.model.ChartAnalysis
import kotlinx.coroutines.flow.Flow

class ScanRepository(
    private val scanDao: ScanDao,
    private val apiKeyStorage: ApiKeyStorage,
    private val geminiService: GeminiVisionService
) {
    val allScans: Flow<List<ScanEntity>> = scanDao.getAllScans()

    fun searchScans(query: String): Flow<List<ScanEntity>> = scanDao.searchScans(query)

    suspend fun saveScan(analysis: ChartAnalysis, imageUri: String? = null): Long {
        return scanDao.insertScan(ScanEntity.fromAnalysis(analysis, imageUri))
    }

    suspend fun deleteScan(scan: ScanEntity) {
        scanDao.deleteScan(scan)
    }

    suspend fun clearAllScans() {
        scanDao.clearAllScans()
    }

    fun getApiKey(): String = apiKeyStorage.getApiKey()

    fun saveApiKey(key: String) = apiKeyStorage.saveApiKey(key)

    fun hasApiKey(): Boolean = apiKeyStorage.hasApiKey()

    fun getSelectedModel(): String = apiKeyStorage.getSelectedModel()

    fun saveSelectedModel(model: String) = apiKeyStorage.saveSelectedModel(model)

    fun getTemperature(): Float = apiKeyStorage.getTemperature()

    fun saveTemperature(temp: Float) = apiKeyStorage.saveTemperature(temp)

    fun getBackgroundMode(): String = apiKeyStorage.getBackgroundMode()

    fun saveBackgroundMode(mode: String) = apiKeyStorage.saveBackgroundMode(mode)

    suspend fun updateScanStatus(id: Long, status: String) {
        scanDao.updateScanStatus(id, status)
    }

    suspend fun testModelConnection(apiKey: String, model: String): Result<Long> {
        return geminiService.testModelConnection(apiKey, model)
    }

    suspend fun analyzeChart(bitmap: Bitmap): Result<ChartAnalysis> {
        val apiKey = apiKeyStorage.getApiKey()
        val model = apiKeyStorage.getSelectedModel()
        val temp = apiKeyStorage.getTemperature()
        val style = apiKeyStorage.getAnalysisStyle()
        return geminiService.analyzeChart(bitmap, apiKey, model, temp, style)
    }
}

