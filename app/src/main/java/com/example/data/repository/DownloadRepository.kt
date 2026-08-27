package com.example.data.repository

import android.content.Context
import com.example.data.db.DownloadDao
import com.example.data.db.DownloadEntity
import com.example.data.model.DownloadItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class DownloadRepository(
    private val downloadDao: DownloadDao,
    private val context: Context
) {
    val allDownloads: Flow<List<DownloadItem>> = downloadDao.getAllDownloads().map { list ->
        list.map { it.toDomain() }
    }

    val completedMedia: Flow<List<DownloadItem>> = downloadDao.getCompletedMedia().map { list ->
        list.map { it.toDomain() }
    }

    val activeDownloads: Flow<List<DownloadItem>> = downloadDao.getActiveDownloads().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun insertOrUpdate(item: DownloadItem) {
        downloadDao.insertOrUpdate(DownloadEntity.fromDomain(item))
    }

    suspend fun getDownloadById(id: String): DownloadItem? {
        return downloadDao.getDownloadById(id)?.toDomain()
    }

    suspend fun deleteDownload(item: DownloadItem, deleteFileFromStorage: Boolean = true) {
        downloadDao.deleteById(item.id)
        if (deleteFileFromStorage && item.localFilePath.isNotBlank()) {
            try {
                val file = File(item.localFilePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        downloadDao.updateFavorite(id, isFavorite)
    }

    suspend fun renameDownload(id: String, newTitle: String) {
        downloadDao.updateTitle(id, newTitle)
    }

    fun getStorageUsage(): Pair<Long, Long> {
        // Returns (usedByDownloadsBytes, availableDeviceBytes)
        val downloadDir = context.getExternalFilesDir(null) ?: context.filesDir
        var usedBytes = 0L
        downloadDir.walkTopDown().forEach { file ->
            if (file.isFile) {
                usedBytes += file.length()
            }
        }
        val freeBytes = downloadDir.freeSpace
        return Pair(usedBytes, freeBytes)
    }
}
