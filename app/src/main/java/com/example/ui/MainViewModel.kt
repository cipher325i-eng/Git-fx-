package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiVisionService
import com.example.data.local.ApiKeyStorage
import com.example.data.local.AppDatabase
import com.example.data.local.ScanEntity
import com.example.data.model.ChartAnalysis
import com.example.data.model.SampleCharts
import com.example.data.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val apiKeyStorage = ApiKeyStorage(application)
    private val geminiService = GeminiVisionService()
    val repository = ScanRepository(database.scanDao(), apiKeyStorage, geminiService)

    // Current Navigation Tab (0: Home, 1: Scan, 2: Settings)
    private val _selectedTab = MutableStateFlow(1) // Default to Scan screen for instant action
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Scan Screen State
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _currentAnalysis = MutableStateFlow<ChartAnalysis?>(null)
    val currentAnalysis: StateFlow<ChartAnalysis?> = _currentAnalysis.asStateFlow()

    private val _scanError = MutableStateFlow<String?>(null)
    val scanError: StateFlow<String?> = _scanError.asStateFlow()

    private val _showMissingKeyDialog = MutableStateFlow(false)
    val showMissingKeyDialog: StateFlow<Boolean> = _showMissingKeyDialog.asStateFlow()

    // Settings Screen State
    private val _apiKeyInput = MutableStateFlow(repository.getApiKey())
    val apiKeyInput: StateFlow<String> = _apiKeyInput.asStateFlow()

    private val _selectedModel = MutableStateFlow(repository.getSelectedModel())
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _customModelInput = MutableStateFlow(
        if (GeminiVisionService.FREE_TIER_MODELS.contains(repository.getSelectedModel())) "" else repository.getSelectedModel()
    )
    val customModelInput: StateFlow<String> = _customModelInput.asStateFlow()

    private val _temperature = MutableStateFlow(repository.getTemperature())
    val temperature: StateFlow<Float> = _temperature.asStateFlow()

    private val _backgroundMode = MutableStateFlow(repository.getBackgroundMode())
    val backgroundMode: StateFlow<String> = _backgroundMode.asStateFlow()

    private val _apiKeySavedToast = MutableStateFlow(false)
    val apiKeySavedToast: StateFlow<Boolean> = _apiKeySavedToast.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    private val _connectionTestResult = MutableStateFlow<String?>(null)
    val connectionTestResult: StateFlow<String?> = _connectionTestResult.asStateFlow()

    // Home / Journal Filter & History
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filter by Direction: ALL, BUY, SELL, WAIT
    private val _directionFilter = MutableStateFlow("ALL")
    val directionFilter: StateFlow<String> = _directionFilter.asStateFlow()

    val historyScans: StateFlow<List<ScanEntity>> = combine(
        repository.allScans,
        _searchQuery,
        _directionFilter
    ) { scans, query, direction ->
        scans.filter { scan ->
            val matchesQuery = query.isBlank() ||
                    scan.pair.contains(query, ignoreCase = true) ||
                    scan.headline.contains(query, ignoreCase = true) ||
                    scan.strategy.contains(query, ignoreCase = true)

            val matchesDirection = when (direction.uppercase()) {
                "ALL" -> true
                "BUY" -> scan.direction.equals("BUY", ignoreCase = true)
                "SELL" -> scan.direction.equals("SELL", ignoreCase = true)
                "WAIT" -> scan.direction.equals("WAIT", ignoreCase = true) || scan.direction.equals("NEUTRAL", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesDirection
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawAllScans = repository.allScans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Preload default BTCUSDm sample chart from user screenshot
        loadSampleChart("btcusdm_m5")
        // Seed default sample into DB if empty so Journal is immediately populated!
        viewModelScope.launch {
            kotlinx.coroutines.delay(300)
            val sampleSetup = SampleCharts.getSampleAnalysis("btcusdm_m5")
            repository.saveScan(sampleSetup, null)
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDirectionFilter(direction: String) {
        _directionFilter.value = direction
    }

    fun updateApiKeyInput(key: String) {
        _apiKeyInput.value = key
    }

    fun selectModel(model: String) {
        _selectedModel.value = model
        repository.saveSelectedModel(model)
    }

    fun updateCustomModelInput(model: String) {
        _customModelInput.value = model
        if (model.isNotBlank()) {
            _selectedModel.value = model.trim()
            repository.saveSelectedModel(model.trim())
        }
    }

    fun updateTemperature(temp: Float) {
        _temperature.value = temp
        repository.saveTemperature(temp)
    }

    fun setBackgroundMode(mode: String) {
        _backgroundMode.value = mode
        repository.saveBackgroundMode(mode)
    }

    fun testConnection() {
        val key = _apiKeyInput.value.ifBlank { repository.getApiKey() }
        val model = _selectedModel.value
        _isTestingConnection.value = true
        _connectionTestResult.value = null

        viewModelScope.launch {
            val result = repository.testModelConnection(key, model)
            _isTestingConnection.value = false
            result.onSuccess { latency ->
                _connectionTestResult.value = "Success! $model responding (${latency}ms)"
            }.onFailure { err ->
                _connectionTestResult.value = "Connection failed: ${err.message}"
            }
        }
    }

    fun saveApiKey() {
        repository.saveApiKey(_apiKeyInput.value)
        repository.saveSelectedModel(_selectedModel.value)
        repository.saveTemperature(_temperature.value)
        _apiKeySavedToast.value = true
    }

    fun dismissToast() {
        _apiKeySavedToast.value = false
    }

    fun updateScanStatus(scanId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateScanStatus(scanId, newStatus)
        }
    }


    fun dismissMissingKeyDialog() {
        _showMissingKeyDialog.value = false
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        try {
            val context = getApplication<Application>()
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            _selectedBitmap.value = bitmap
            _scanError.value = null
        } catch (e: Exception) {
            _scanError.value = "Failed to load image: ${e.localizedMessage}"
        }
    }

    fun loadSampleChart(sampleId: String) {
        val sample = SampleCharts.samples.find { it.id == sampleId } ?: SampleCharts.samples.first()
        val isBullish = sample.id != "btcusdm_m5"
        val bitmap = SampleCharts.generateSampleChartBitmap(sample.pair, sample.timeframe, isBullish)
        _selectedBitmap.value = bitmap
        _selectedImageUri.value = null
        _scanError.value = null
        _currentAnalysis.value = SampleCharts.getSampleAnalysis(sampleId)
    }

    fun clearSelectedImage() {
        _selectedBitmap.value = null
        _selectedImageUri.value = null
        _currentAnalysis.value = null
        _scanError.value = null
    }

    fun scanChart() {
        // 1. Check if user has saved Gemini API Key
        if (!repository.hasApiKey()) {
            _showMissingKeyDialog.value = true
            return
        }

        val bitmap = _selectedBitmap.value
        if (bitmap == null) {
            _scanError.value = "Please select or capture a chart screenshot first."
            return
        }

        _isScanning.value = true
        _scanError.value = null

        viewModelScope.launch {
            val result = repository.analyzeChart(bitmap)
            _isScanning.value = false

            result.onSuccess { analysis ->
                _currentAnalysis.value = analysis
                // Automatically save scan to local Room database history
                repository.saveScan(analysis, _selectedImageUri.value?.toString())
            }.onFailure { error ->
                _scanError.value = error.message ?: "Failed to analyze chart screenshot."
            }
        }
    }

    fun deleteScan(scan: ScanEntity) {
        viewModelScope.launch {
            repository.deleteScan(scan)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAllScans()
        }
    }
}
