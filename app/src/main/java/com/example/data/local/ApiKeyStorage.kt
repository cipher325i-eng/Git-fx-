package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

class ApiKeyStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("chart_scanner_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_GEMINI_API_KEY = "user_gemini_api_key"
        private const val KEY_MODEL = "selected_gemini_model"
        private const val KEY_TEMPERATURE = "model_temperature"
        private const val KEY_STYLE = "analysis_style"
        private const val KEY_BG_MODE = "background_mode"
    }

    fun saveApiKey(apiKey: String) {
        prefs.edit().putString(KEY_GEMINI_API_KEY, apiKey.trim()).apply()
    }

    fun getApiKey(): String {
        val saved = prefs.getString(KEY_GEMINI_API_KEY, "")?.trim().orEmpty()
        if (saved.isNotBlank()) return saved

        // Fallback to BuildConfig if available and not placeholder
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") {
            return buildKey
        }
        return ""
    }

    fun hasApiKey(): Boolean {
        return getApiKey().isNotBlank()
    }

    fun getSelectedModel(): String {
        return prefs.getString(KEY_MODEL, "gemini-2.5-flash") ?: "gemini-2.5-flash"
    }

    fun saveSelectedModel(model: String) {
        prefs.edit().putString(KEY_MODEL, model.trim()).apply()
    }

    fun getTemperature(): Float {
        return prefs.getFloat(KEY_TEMPERATURE, 0.2f)
    }

    fun saveTemperature(temp: Float) {
        prefs.edit().putFloat(KEY_TEMPERATURE, temp).apply()
    }

    fun getAnalysisStyle(): String {
        return prefs.getString(KEY_STYLE, "SMC_ICT") ?: "SMC_ICT"
    }

    fun saveAnalysisStyle(style: String) {
        prefs.edit().putString(KEY_STYLE, style).apply()
    }

    fun getBackgroundMode(): String {
        return prefs.getString(KEY_BG_MODE, "AUTO") ?: "AUTO"
    }

    fun saveBackgroundMode(mode: String) {
        prefs.edit().putString(KEY_BG_MODE, mode).apply()
    }
}
