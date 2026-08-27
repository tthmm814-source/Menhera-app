package com.example.engine.downloader

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.data.model.MediaType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object MediaStorageManager {

    private const val TAG = "MediaStorageManager"

    /**
     * Creates an output stream directly to the public MediaStore (Gallery / Movies / Music)
     * so that files immediately appear in Google Photos, Samsung Gallery, and File Managers.
     */
    fun createPublicMediaTarget(
        context: Context,
        fileName: String,
        mediaType: MediaType
    ): Pair<OutputStream?, String> {
        val isVideo = mediaType == MediaType.VIDEO_MP4 || mediaType == MediaType.VIDEO_HD
        val isAudio = mediaType == MediaType.AUDIO_MP3 || mediaType == MediaType.AUDIO_M4A

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    if (isVideo) "video/mp4" else if (mediaType == MediaType.AUDIO_MP3) "audio/mpeg" else "audio/mp4"
                )
                val relativePath = if (isVideo) {
                    "${Environment.DIRECTORY_MOVIES}/OmniGrab"
                } else if (isAudio) {
                    "${Environment.DIRECTORY_MUSIC}/OmniGrab"
                } else {
                    "${Environment.DIRECTORY_DOWNLOADS}/OmniGrab"
                }
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val collectionUri: Uri = if (isVideo) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else if (isAudio) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }

            return try {
                val uri = context.contentResolver.insert(collectionUri, contentValues)
                if (uri != null) {
                    val stream = context.contentResolver.openOutputStream(uri)
                    Pair(stream, uri.toString())
                } else {
                    fallbackFileTarget(context, fileName, mediaType)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed inserting to MediaStore, falling back", e)
                fallbackFileTarget(context, fileName, mediaType)
            }
        } else {
            return fallbackFileTarget(context, fileName, mediaType)
        }
    }

    /**
     * Mark pending MediaStore item as completed and scan with MediaScannerConnection.
     */
    fun finalizePublicMedia(
        context: Context,
        targetPathOrUri: String,
        mediaType: MediaType
    ) {
        val isVideo = mediaType == MediaType.VIDEO_MP4 || mediaType == MediaType.VIDEO_HD
        val mimeType = if (isVideo) "video/mp4" else if (mediaType == MediaType.AUDIO_MP3) "audio/mpeg" else "audio/mp4"

        try {
            if (targetPathOrUri.startsWith("content://")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val uri = Uri.parse(targetPathOrUri)
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.IS_PENDING, 0)
                    }
                    context.contentResolver.update(uri, values, null, null)
                }
            } else {
                val file = File(targetPathOrUri)
                if (file.exists()) {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(file.absolutePath),
                        arrayOf(mimeType)
                    ) { path, uri ->
                        Log.d(TAG, "Scanned $path -> $uri into Android Gallery")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finalizing media", e)
        }
    }

    private fun fallbackFileTarget(
        context: Context,
        fileName: String,
        mediaType: MediaType
    ): Pair<OutputStream?, String> {
        val isVideo = mediaType == MediaType.VIDEO_MP4 || mediaType == MediaType.VIDEO_HD
        val isAudio = mediaType == MediaType.AUDIO_MP3 || mediaType == MediaType.AUDIO_M4A

        val baseDir = if (isVideo) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        } else if (isAudio) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

        val targetDir = File(baseDir, "OmniGrab").apply {
            if (!exists()) mkdirs()
        }

        val targetFile = if (targetDir.exists() && targetDir.canWrite()) {
            File(targetDir, fileName)
        } else {
            val fallbackDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
            File(fallbackDir, fileName)
        }

        return try {
            val stream = FileOutputStream(targetFile)
            Pair(stream, targetFile.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "Failed creating FileOutputStream", e)
            Pair(null, "")
        }
    }
}
