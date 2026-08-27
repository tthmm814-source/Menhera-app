package com.example.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.data.model.DownloadItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class MediaPlayerManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _currentPlayingItem = MutableStateFlow<DownloadItem?>(null)
    val currentPlayingItem = _currentPlayingItem.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs = _durationMs.asStateFlow()

    private val _isLooping = MutableStateFlow(false)
    val isLooping = _isLooping.asStateFlow()

    fun playItem(item: DownloadItem) {
        if (_currentPlayingItem.value?.id == item.id && mediaPlayer != null) {
            resume()
            return
        }

        stop()
        _currentPlayingItem.value = item

        try {
            val uri = when {
                item.localFilePath.startsWith("content://") -> Uri.parse(item.localFilePath)
                item.localFilePath.isNotBlank() && File(item.localFilePath).exists() -> Uri.fromFile(File(item.localFilePath))
                item.downloadUrl.isNotBlank() -> Uri.parse(item.downloadUrl)
                else -> Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                isLooping = _isLooping.value
                setOnPreparedListener { mp ->
                    _durationMs.value = mp.duration.toLong().coerceAtLeast(1000L)
                    mp.start()
                    _isPlaying.value = true
                    startProgressTracker()
                }
                setOnCompletionListener {
                    if (!_isLooping.value) {
                        _isPlaying.value = false
                        _currentPositionMs.value = 0L
                    }
                }
                setOnErrorListener { _, _, _ ->
                    _isPlaying.value = false
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            it.start()
            _isPlaying.value = true
            startProgressTracker()
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let {
            it.seekTo(positionMs.toInt())
            _currentPositionMs.value = positionMs
        }
    }

    fun forward10Seconds() {
        val target = (_currentPositionMs.value + 10000L).coerceAtMost(_durationMs.value)
        seekTo(target)
    }

    fun rewind10Seconds() {
        val target = (_currentPositionMs.value - 10000L).coerceAtLeast(0L)
        seekTo(target)
    }

    fun toggleLoop() {
        val newLoop = !_isLooping.value
        _isLooping.value = newLoop
        mediaPlayer?.isLooping = newLoop
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPositionMs.value = 0L
        _durationMs.value = 0L
        _currentPlayingItem.value = null
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch(Dispatchers.Main) {
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let { mp ->
                    try {
                        if (mp.isPlaying) {
                            _currentPositionMs.value = mp.currentPosition.toLong()
                        }
                    } catch (e: Exception) {
                        // ignore
                    }
                }
                delay(300)
            }
        }
    }

    fun release() {
        stop()
    }
}
