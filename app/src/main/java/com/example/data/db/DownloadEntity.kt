package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.MediaType
import com.example.data.model.PlatformType

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String,
    val url: String,
    val downloadUrl: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String,
    val platformName: String,
    val mediaTypeName: String,
    val quality: String,
    val statusName: String,
    val progress: Float,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val speedBytesPerSec: Long,
    val etaSeconds: Long,
    val localFilePath: String,
    val durationSeconds: Long,
    val createdAt: Long,
    val isFavorite: Boolean,
    val errorMessage: String?
) {
    fun toDomain(): DownloadItem {
        val platform = try {
            PlatformType.valueOf(platformName)
        } catch (e: Exception) {
            PlatformType.DIRECT_LINK
        }

        val mediaType = try {
            MediaType.valueOf(mediaTypeName)
        } catch (e: Exception) {
            MediaType.VIDEO_MP4
        }

        val status = try {
            DownloadStatus.valueOf(statusName)
        } catch (e: Exception) {
            DownloadStatus.QUEUED
        }

        return DownloadItem(
            id = id,
            url = url,
            downloadUrl = downloadUrl,
            title = title,
            author = author,
            thumbnailUrl = thumbnailUrl,
            platform = platform,
            mediaType = mediaType,
            quality = quality,
            status = status,
            progress = progress,
            downloadedBytes = downloadedBytes,
            totalBytes = totalBytes,
            speedBytesPerSec = speedBytesPerSec,
            etaSeconds = etaSeconds,
            localFilePath = localFilePath,
            durationSeconds = durationSeconds,
            createdAt = createdAt,
            isFavorite = isFavorite,
            errorMessage = errorMessage
        )
    }

    companion object {
        fun fromDomain(item: DownloadItem): DownloadEntity {
            return DownloadEntity(
                id = item.id,
                url = item.url,
                downloadUrl = item.downloadUrl,
                title = item.title,
                author = item.author,
                thumbnailUrl = item.thumbnailUrl,
                platformName = item.platform.name,
                mediaTypeName = item.mediaType.name,
                quality = item.quality,
                statusName = item.status.name,
                progress = item.progress,
                downloadedBytes = item.downloadedBytes,
                totalBytes = item.totalBytes,
                speedBytesPerSec = item.speedBytesPerSec,
                etaSeconds = item.etaSeconds,
                localFilePath = item.localFilePath,
                durationSeconds = item.durationSeconds,
                createdAt = item.createdAt,
                isFavorite = item.isFavorite,
                errorMessage = item.errorMessage
            )
        }
    }
}
