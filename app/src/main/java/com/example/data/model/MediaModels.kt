package com.example.data.model

enum class MediaType(val labelAr: String, val labelEn: String, val extension: String) {
    VIDEO_MP4("فيديو MP4", "Video MP4", "mp4"),
    VIDEO_HD("فيديو عالي الدقة HD", "Video Full HD", "mp4"),
    AUDIO_MP3("صوت MP3", "Audio MP3", "mp3"),
    AUDIO_M4A("صوت M4A", "Audio M4A", "m4a")
}

enum class PlatformType(
    val title: String,
    val iconName: String,
    val brandHex: Long,
    val domains: List<String>
) {
    YOUTUBE("YouTube", "youtube", 0xFFFF0000, listOf("youtube.com", "youtu.be")),
    TIKTOK("TikTok", "tiktok", 0xFF00F2FE, listOf("tiktok.com")),
    INSTAGRAM("Instagram", "instagram", 0xFFE1306C, listOf("instagram.com", "instagr.am")),
    TWITTER("X / Twitter", "twitter", 0xFF1DA1F2, listOf("twitter.com", "x.com")),
    FACEBOOK("Facebook", "facebook", 0xFF1877F2, listOf("facebook.com", "fb.watch", "fb.com")),
    SOUNDCLOUD("SoundCloud", "soundcloud", 0xFFFF5500, listOf("soundcloud.com")),
    PINTEREST("Pinterest", "pinterest", 0xFFBD081C, listOf("pinterest.com", "pin.it")),
    DIRECT_LINK("رابط مباشر / ويب", "web", 0xFF00E5FF, listOf("http", "https"))
}

enum class DownloadStatus(val labelAr: String, val labelEn: String) {
    QUEUED("في قائمة الانتظار", "Queued"),
    DOWNLOADING("جارٍ التحميل", "Downloading"),
    PAUSED("متوقف مؤقتاً", "Paused"),
    COMPLETED("اكتمل التحميل", "Completed"),
    FAILED("فشل التحميل", "Failed"),
    CANCELLED("ملغي", "Cancelled")
}

data class FormatOption(
    val formatId: String,
    val title: String,
    val mediaType: MediaType,
    val qualityLabel: String, // e.g. "1080p Full HD", "720p HD", "320 kbps (High Quality)", "128 kbps"
    val estimatedSizeBytes: Long,
    val downloadUrl: String,
    val isAudioOnly: Boolean = false,
    val isRecommended: Boolean = false
)

data class ExtractedMediaInfo(
    val originalUrl: String,
    val title: String,
    val author: String,
    val durationText: String,
    val durationSeconds: Long,
    val thumbnailUrl: String,
    val platform: PlatformType,
    val formats: List<FormatOption>,
    val description: String = ""
)

data class DownloadItem(
    val id: String,
    val url: String,
    val downloadUrl: String,
    val title: String,
    val author: String = "",
    val thumbnailUrl: String = "",
    val platform: PlatformType = PlatformType.DIRECT_LINK,
    val mediaType: MediaType = MediaType.VIDEO_MP4,
    val quality: String = "1080p HD",
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val progress: Float = 0f, // 0.0 to 1.0
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val speedBytesPerSec: Long = 0L,
    val etaSeconds: Long = 0L,
    val localFilePath: String = "",
    val durationSeconds: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)
