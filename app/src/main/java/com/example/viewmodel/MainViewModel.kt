package com.example.viewmodel

import android.app.Application
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.ExtractedMediaInfo
import com.example.data.model.FormatOption
import com.example.data.model.MediaType
import com.example.data.model.PlatformType
import com.example.data.repository.DownloadRepository
import com.example.engine.downloader.DownloadEngine
import com.example.engine.extractor.MediaUrlParser
import com.example.player.MediaPlayerManager
import com.example.service.DownloadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

enum class AppTab(val titleAr: String, val titleEn: String) {
    HOME("الرئيسية", "Home"),
    DOWNLOADS("التنزيلات", "Downloads"),
    LIBRARY("المكتبة", "Library"),
    BROWSER("المتصفح", "Browser"),
    SETTINGS("الإعدادات", "Settings")
}

enum class LibraryFilter(val labelAr: String, val labelEn: String) {
    ALL("الكل", "All"),
    VIDEOS("فيديوهات", "Videos"),
    AUDIO("أغاني وصوتيات", "Music"),
    FAVORITES("المفضلة", "Favorites")
}

sealed class ExtractionState {
    object Idle : ExtractionState()
    object Loading : ExtractionState()
    data class Success(val mediaInfo: ExtractedMediaInfo) : ExtractionState()
    data class Error(val message: String) : ExtractionState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = DownloadRepository(database.downloadDao(), application)
    val downloadEngine = DownloadEngine(application, repository, viewModelScope)
    val playerManager = MediaPlayerManager(application, viewModelScope)

    // Splash screen state
    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible = _isSplashVisible.asStateFlow()

    // Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab = _currentTab.asStateFlow()

    // Input URL and parser state
    private val _urlInput = MutableStateFlow("")
    val urlInput = _urlInput.asStateFlow()

    private val _extractionState = MutableStateFlow<ExtractionState>(ExtractionState.Idle)
    val extractionState = _extractionState.asStateFlow()

    private val _selectedFormat = MutableStateFlow<FormatOption?>(null)
    val selectedFormat = _selectedFormat.asStateFlow()

    private val _showFormatSheet = MutableStateFlow(false)
    val showFormatSheet = _showFormatSheet.asStateFlow()

    // Library filtering and search
    private val _libraryFilter = MutableStateFlow(LibraryFilter.ALL)
    val libraryFilter = _libraryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Video Player Dialog state
    private val _activeVideoPlayerItem = MutableStateFlow<DownloadItem?>(null)
    val activeVideoPlayerItem = _activeVideoPlayerItem.asStateFlow()

    // Clipboard auto-detected link
    private val _clipboardDetectedUrl = MutableStateFlow<String?>(null)
    val clipboardDetectedUrl = _clipboardDetectedUrl.asStateFlow()

    // In-app browser current URL & sniffer state
    private val _browserUrl = MutableStateFlow("https://www.google.com")
    val browserUrl = _browserUrl.asStateFlow()

    private val _sniffedMedia = MutableStateFlow<List<ExtractedMediaInfo>>(emptyList())
    val sniffedMedia = _sniffedMedia.asStateFlow()

    // Download threads setting
    private val _concurrentThreads = MutableStateFlow(3)
    val concurrentThreads = _concurrentThreads.asStateFlow()

    // Combined active downloads (DB + real-time live download state)
    val activeDownloads = combine(
        repository.allDownloads,
        downloadEngine.liveDownloads
    ) { dbList, liveMap ->
        // Merge DB list with memory live updates
        val map = dbList.associateBy { it.id }.toMutableMap()
        liveMap.forEach { (id, liveItem) ->
            map[id] = liveItem
        }
        map.values.filter {
            it.status == DownloadStatus.DOWNLOADING ||
            it.status == DownloadStatus.QUEUED ||
            it.status == DownloadStatus.PAUSED
        }.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Completed media items for Library
    val libraryMediaItems = combine(
        repository.allDownloads,
        downloadEngine.liveDownloads,
        _libraryFilter,
        _searchQuery
    ) { dbList, liveMap, filter, query ->
        val map = dbList.associateBy { it.id }.toMutableMap()
        liveMap.forEach { (id, liveItem) ->
            map[id] = liveItem
        }
        val completed = map.values.filter { it.status == DownloadStatus.COMPLETED }

        completed.filter { item ->
            val matchesFilter = when (filter) {
                LibraryFilter.ALL -> true
                LibraryFilter.VIDEOS -> item.mediaType == MediaType.VIDEO_MP4 || item.mediaType == MediaType.VIDEO_HD
                LibraryFilter.AUDIO -> item.mediaType == MediaType.AUDIO_MP3 || item.mediaType == MediaType.AUDIO_M4A
                LibraryFilter.FAVORITES -> item.isFavorite
            }
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.author.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total live download speed
    val totalLiveSpeed = downloadEngine.totalSpeedBytesPerSec

    init {
        // Observe downloads to update Foreground Service notification
        viewModelScope.launch {
            downloadEngine.liveDownloads.collect { map ->
                val active = map.values.firstOrNull { it.status == DownloadStatus.DOWNLOADING }
                if (active != null) {
                    val speed = DownloadEngine.formatSpeed(active.speedBytesPerSec)
                    DownloadService.start(
                        getApplication(),
                        active.title,
                        (active.progress * 100).toInt(),
                        speed
                    )
                } else {
                    DownloadService.stop(getApplication())
                }
            }
        }
    }

    fun dismissSplash() {
        _isSplashVisible.value = false
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setUrlInput(url: String) {
        _urlInput.value = url
    }

    fun pasteFromClipboard() {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
            val item = clipboard.primaryClip?.getItemAt(0)
            val text = item?.text?.toString()?.trim() ?: ""
            if (text.startsWith("http://") || text.startsWith("https://")) {
                _urlInput.value = text
                extractMedia(text)
            }
        }
    }

    fun checkClipboardForMediaUrl() {
        try {
            val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()?.trim() ?: ""
                if ((text.startsWith("http://") || text.startsWith("https://")) && text != _urlInput.value) {
                    _clipboardDetectedUrl.value = text
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    fun dismissClipboardPrompt() {
        _clipboardDetectedUrl.value = null
    }

    fun extractMedia(url: String = _urlInput.value) {
        val cleanUrl = url.trim()
        if (cleanUrl.isBlank()) return

        _urlInput.value = cleanUrl
        _extractionState.value = ExtractionState.Loading
        _clipboardDetectedUrl.value = null

        viewModelScope.launch {
            val result = MediaUrlParser.parseUrl(cleanUrl)
            result.onSuccess { info ->
                _extractionState.value = ExtractionState.Success(info)
                _selectedFormat.value = info.formats.firstOrNull { it.isRecommended } ?: info.formats.firstOrNull()
                _showFormatSheet.value = true
            }.onFailure { error ->
                _extractionState.value = ExtractionState.Error(error.localizedMessage ?: "تعذر استخراج الرابط، يرجى التأكد من صحته")
            }
        }
    }

    fun selectFormat(format: FormatOption) {
        _selectedFormat.value = format
    }

    fun setShowFormatSheet(show: Boolean) {
        _showFormatSheet.value = show
    }

    fun startDownload(extractedInfo: ExtractedMediaInfo, format: FormatOption) {
        _showFormatSheet.value = false
        val downloadItem = DownloadItem(
            id = UUID.randomUUID().toString(),
            url = extractedInfo.originalUrl,
            downloadUrl = format.downloadUrl,
            title = extractedInfo.title,
            author = extractedInfo.author,
            thumbnailUrl = extractedInfo.thumbnailUrl,
            platform = extractedInfo.platform,
            mediaType = format.mediaType,
            quality = format.qualityLabel,
            status = DownloadStatus.DOWNLOADING,
            progress = 0.05f,
            totalBytes = format.estimatedSizeBytes,
            durationSeconds = extractedInfo.durationSeconds
        )

        downloadEngine.startDownload(downloadItem)
        // Switch to Downloads tab to see real-time progress
        _currentTab.value = AppTab.DOWNLOADS
    }

    fun pauseDownload(id: String) {
        downloadEngine.pauseDownload(id)
    }

    fun resumeDownload(id: String) {
        downloadEngine.resumeDownload(id)
    }

    fun cancelDownload(id: String) {
        downloadEngine.cancelDownload(id)
    }

    fun retryDownload(item: DownloadItem) {
        downloadEngine.retryDownload(item)
    }

    fun deleteLibraryItem(item: DownloadItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDownload(item, deleteFileFromStorage = true)
        }
        if (playerManager.currentPlayingItem.value?.id == item.id) {
            playerManager.stop()
        }
    }

    fun toggleFavorite(item: DownloadItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(item.id, !item.isFavorite)
        }
    }

    fun renameItem(id: String, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.renameDownload(id, newTitle)
        }
    }

    fun setLibraryFilter(filter: LibraryFilter) {
        _libraryFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playMedia(item: DownloadItem) {
        if (item.mediaType == MediaType.VIDEO_MP4 || item.mediaType == MediaType.VIDEO_HD) {
            // Open full-screen video player dialog
            _activeVideoPlayerItem.value = item
        } else {
            // Play with built-in audio player
            playerManager.playItem(item)
        }
    }

    fun closeVideoPlayer() {
        _activeVideoPlayerItem.value = null
    }

    fun setBrowserUrl(url: String) {
        _browserUrl.value = url
        // Sniff page for media
        viewModelScope.launch {
            val result = MediaUrlParser.parseUrl(url)
            result.onSuccess { info ->
                _sniffedMedia.value = listOf(info)
            }.onFailure {
                _sniffedMedia.value = emptyList()
            }
        }
    }

    fun setConcurrentThreads(threads: Int) {
        _concurrentThreads.value = threads.coerceIn(1, 5)
    }

    fun clearAllCompleted() {
        viewModelScope.launch(Dispatchers.IO) {
            database.downloadDao().clearCompleted()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
