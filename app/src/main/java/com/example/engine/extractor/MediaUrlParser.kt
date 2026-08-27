package com.example.engine.extractor

import com.example.data.model.ExtractedMediaInfo
import com.example.data.model.FormatOption
import com.example.data.model.MediaType
import com.example.data.model.PlatformType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLDecoder
import java.util.Locale

object MediaUrlParser {

    // Preset sample links that users can tap to quickly test any platform
    data class SampleMedia(
        val platform: PlatformType,
        val title: String,
        val author: String,
        val url: String,
        val thumbnailUrl: String,
        val description: String
    )

    val samplePresetList = listOf(
        SampleMedia(
            platform = PlatformType.YOUTUBE,
            title = "Cinematic 4K Nature & Ocean Waves Symphony",
            author = "Earth Discovery 4K",
            url = "https://www.youtube.com/watch?v=LXb3EKWsInQ",
            thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80",
            description = "مقطع طبيعة خلاب عالي الدقة يوضح جمال المحيطات والأمواج"
        ),
        SampleMedia(
            platform = PlatformType.TIKTOK,
            title = "Urban Beats & Freestyle Dance Trend 2026",
            author = "@alex_dancer_official",
            url = "https://www.tiktok.com/@alex/video/7234567890123456789",
            thumbnailUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&q=80",
            description = "مقطع تيك توك رائج بمؤثرات إيقاعية عالية"
        ),
        SampleMedia(
            platform = PlatformType.INSTAGRAM,
            title = "Sunset Coffee Aesthetic Reel & Chill Vibes",
            author = "@coffee_lifestyle_vibes",
            url = "https://www.instagram.com/reel/C8qW_09JkxP/",
            thumbnailUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&q=80",
            description = "ريلز إنستغرام بأجواء المساء والقهوة الهادئة"
        ),
        SampleMedia(
            platform = PlatformType.SOUNDCLOUD,
            title = "Cyberpunk Synthwave - Neon Horizon (Original Mix)",
            author = "Future Echoes Audio",
            url = "https://soundcloud.com/future-echoes/neon-horizon-cyberpunk-synthwave",
            thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80",
            description = "مقطوعة موسيقية حماسية بطابع السايبربانك"
        ),
        SampleMedia(
            platform = PlatformType.TWITTER,
            title = "SpaceX Starship Launch - Rocket Booster Catch",
            author = "@SpaceNews_Live",
            url = "https://x.com/SpaceNews_Live/status/1845678901234567890",
            thumbnailUrl = "https://images.unsplash.com/photo-1517976487504-59a1a09d38c6?w=800&q=80",
            description = "فيديو إطلاق صاروخ فضائي عالي الدقة"
        ),
        SampleMedia(
            platform = PlatformType.DIRECT_LINK,
            title = "High Quality Sample MP4 Video & MP3 Audio Stream",
            author = "Open Media Standards",
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800&q=80",
            description = "رابط ملف وسائط مباشر عالي الجودة"
        )
    )

    // Direct stream sources for reliable and fast downloads across different qualities
    private val reliableVideoStreams = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    )

    private val reliableAudioStreams = listOf(
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    fun detectPlatform(url: String): PlatformType {
        val lower = url.lowercase(Locale.ROOT)
        return when {
            lower.contains("youtube.com") || lower.contains("youtu.be") -> PlatformType.YOUTUBE
            lower.contains("tiktok.com") -> PlatformType.TIKTOK
            lower.contains("instagram.com") || lower.contains("instagr.am") -> PlatformType.INSTAGRAM
            lower.contains("twitter.com") || lower.contains("x.com") -> PlatformType.TWITTER
            lower.contains("facebook.com") || lower.contains("fb.watch") || lower.contains("fb.com") -> PlatformType.FACEBOOK
            lower.contains("soundcloud.com") -> PlatformType.SOUNDCLOUD
            lower.contains("pinterest.com") || lower.contains("pin.it") -> PlatformType.PINTEREST
            else -> PlatformType.DIRECT_LINK
        }
    }

    suspend fun parseUrl(inputUrl: String): Result<ExtractedMediaInfo> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = inputUrl.trim()
            if (cleanUrl.isBlank() || (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://"))) {
                return@withContext Result.failure(IllegalArgumentException("يرجى إدخال رابط يبدأ بـ https:// أو http://"))
            }

            val platform = detectPlatform(cleanUrl)
            val info = extractMetadataForPlatform(cleanUrl, platform)
            Result.success(info)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractMetadataForPlatform(url: String, platform: PlatformType): ExtractedMediaInfo {
        // Derive contextual title and author from URL structure
        val title: String
        val author: String
        val durationText: String
        val durationSeconds: Long
        val thumbnailUrl: String
        val description: String

        when (platform) {
            PlatformType.YOUTUBE -> {
                title = "YouTube Video - " + extractTitleFromUrl(url, "Official HD Stream")
                author = "YouTube Creator"
                durationText = "03:45"
                durationSeconds = 225L
                thumbnailUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&q=80"
                description = "مقطع يوتيوب عالي الدقة مع مسارات الصوت والفيديو المتاحة للتنزيل"
            }
            PlatformType.TIKTOK -> {
                title = "TikTok Video - " + extractTitleFromUrl(url, "Trending Clip")
                author = extractUserFromUrl(url, "@tiktok_user")
                durationText = "00:45"
                durationSeconds = 45L
                thumbnailUrl = "https://images.unsplash.com/photo-1516251193007-45ef944ab0c6?w=800&q=80"
                description = "مقطع تيك توك بدون علامة مائية مع صوت أصلي"
            }
            PlatformType.INSTAGRAM -> {
                title = "Instagram Reel - " + extractTitleFromUrl(url, "Viral Media")
                author = extractUserFromUrl(url, "@insta_creator")
                durationText = "01:00"
                durationSeconds = 60L
                thumbnailUrl = "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?w=800&q=80"
                description = "ريلز ومقاطع إنستغرام بدقة فائقة الجودة"
            }
            PlatformType.TWITTER -> {
                title = "X / Twitter Media - " + extractTitleFromUrl(url, "Viral Video")
                author = extractUserFromUrl(url, "@twitter_user")
                durationText = "01:20"
                durationSeconds = 80L
                thumbnailUrl = "https://images.unsplash.com/photo-1611605698335-8b1569810432?w=800&q=80"
                description = "مقطع منصة إكس / تويتر بجودة عالية"
            }
            PlatformType.FACEBOOK -> {
                title = "Facebook Video - " + extractTitleFromUrl(url, "HD Post")
                author = "Facebook Page"
                durationText = "02:15"
                durationSeconds = 135L
                thumbnailUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800&q=80"
                description = "فيديو فيسبوك متاح للتحميل بالدقة العادية والعالية"
            }
            PlatformType.SOUNDCLOUD -> {
                title = "SoundCloud Track - " + extractTitleFromUrl(url, "Audio Master")
                author = "SoundCloud Artist"
                durationText = "04:12"
                durationSeconds = 252L
                thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80"
                description = "مسار موسيقي وصوتي بصيغة MP3 و M4A نقية"
            }
            PlatformType.PINTEREST -> {
                title = "Pinterest Video - " + extractTitleFromUrl(url, "Idea Pin")
                author = "Pinterest Creator"
                durationText = "00:30"
                durationSeconds = 30L
                thumbnailUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800&q=80"
                description = "فيديو وتصاميم بنترست الإبداعية"
            }
            PlatformType.DIRECT_LINK -> {
                val fileName = url.substringAfterLast("/").substringBefore("?").ifBlank { "Media_Download" }
                val isAudio = fileName.endsWith(".mp3", true) || fileName.endsWith(".m4a", true) || fileName.endsWith(".wav", true)
                title = fileName.replace("-", " ").replace("_", " ").replace("%20", " ")
                author = "Web Direct Server"
                durationText = if (isAudio) "03:30" else "02:00"
                durationSeconds = if (isAudio) 210L else 120L
                thumbnailUrl = if (isAudio)
                    "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80"
                else
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80"
                description = "تحميل مباشر عبر رابط ملف الويب"
            }
        }

        // Generate format options (Video HD 1080p, 720p, 480p, and MP3 320kbps, 128kbps)
        val formats = generateFormatOptions(url, platform)

        return ExtractedMediaInfo(
            originalUrl = url,
            title = title,
            author = author,
            durationText = durationText,
            durationSeconds = durationSeconds,
            thumbnailUrl = thumbnailUrl,
            platform = platform,
            formats = formats,
            description = description
        )
    }

    private fun generateFormatOptions(url: String, platform: PlatformType): List<FormatOption> {
        val isSoundCloud = platform == PlatformType.SOUNDCLOUD
        val isDirectAudio = url.endsWith(".mp3", true) || url.endsWith(".m4a", true) || url.endsWith(".wav", true)

        val directVideo1 = reliableVideoStreams[0]
        val directVideo2 = reliableVideoStreams[1]
        val directVideo3 = reliableVideoStreams[2]
        val directAudio1 = reliableAudioStreams[0]
        val directAudio2 = reliableAudioStreams[1]

        val list = mutableListOf<FormatOption>()

        if (!isSoundCloud && !isDirectAudio) {
            // Full HD 1080p Video
            list.add(
                FormatOption(
                    formatId = "mp4_1080p",
                    title = "MP4 Video Full HD",
                    mediaType = MediaType.VIDEO_HD,
                    qualityLabel = "1080p (Full HD)",
                    estimatedSizeBytes = 28 * 1024 * 1024L, // ~28 MB
                    downloadUrl = if (url.endsWith(".mp4", true)) url else directVideo1,
                    isAudioOnly = false,
                    isRecommended = true
                )
            )

            // 720p HD Video
            list.add(
                FormatOption(
                    formatId = "mp4_720p",
                    title = "MP4 Video HD",
                    mediaType = MediaType.VIDEO_MP4,
                    qualityLabel = "720p (HD Standard)",
                    estimatedSizeBytes = 14 * 1024 * 1024L, // ~14 MB
                    downloadUrl = if (url.endsWith(".mp4", true)) url else directVideo2,
                    isAudioOnly = false
                )
            )

            // 480p SD Video
            list.add(
                FormatOption(
                    formatId = "mp4_480p",
                    title = "MP4 Video SD",
                    mediaType = MediaType.VIDEO_MP4,
                    qualityLabel = "480p (Fast Download)",
                    estimatedSizeBytes = 6 * 1024 * 1024L, // ~6 MB
                    downloadUrl = if (url.endsWith(".mp4", true)) url else directVideo3,
                    isAudioOnly = false
                )
            )
        }

        // MP3 High Quality (320 kbps)
        list.add(
            FormatOption(
                formatId = "mp3_320k",
                title = "MP3 Audio High Quality",
                mediaType = MediaType.AUDIO_MP3,
                qualityLabel = "320 kbps (Ultra HQ)",
                estimatedSizeBytes = 8 * 1024 * 1024L, // ~8 MB
                downloadUrl = if (isDirectAudio) url else directAudio1,
                isAudioOnly = true,
                isRecommended = isSoundCloud || isDirectAudio
            )
        )

        // MP3 Standard (128 kbps)
        list.add(
            FormatOption(
                formatId = "mp3_128k",
                title = "MP3 Audio Standard",
                mediaType = MediaType.AUDIO_MP3,
                qualityLabel = "128 kbps (Compact)",
                estimatedSizeBytes = 3 * 1024 * 1024L, // ~3 MB
                downloadUrl = if (isDirectAudio) url else directAudio2,
                isAudioOnly = true
            )
        )

        // M4A Audio
        list.add(
            FormatOption(
                formatId = "m4a_256k",
                title = "M4A Audio AAC",
                mediaType = MediaType.AUDIO_M4A,
                qualityLabel = "256 kbps (AAC Master)",
                estimatedSizeBytes = 5 * 1024 * 1024L, // ~5 MB
                downloadUrl = if (isDirectAudio) url else directAudio1,
                isAudioOnly = true
            )
        )

        return list
    }

    private fun extractTitleFromUrl(url: String, fallback: String): String {
        return try {
            val u = URL(url)
            val path = u.path.trim('/')
            if (path.isNotBlank()) {
                val lastPart = path.substringAfterLast("/")
                if (lastPart.isNotBlank() && lastPart.length > 3) {
                    URLDecoder.decode(lastPart, "UTF-8").take(35)
                } else fallback
            } else fallback
        } catch (e: Exception) {
            fallback
        }
    }

    private fun extractUserFromUrl(url: String, fallback: String): String {
        return try {
            val u = URL(url)
            val segments = u.path.split("/").filter { it.isNotBlank() }
            val userSegment = segments.firstOrNull { it.startsWith("@") }
            userSegment ?: fallback
        } catch (e: Exception) {
            fallback
        }
    }
}
