package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.player.MediaPlayerManager
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@Composable
fun AudioPlayerBottomBar(
    playerManager: MediaPlayerManager,
    modifier: Modifier = Modifier
) {
    val currentItem by playerManager.currentPlayingItem.collectAsState()
    val isPlaying by playerManager.isPlaying.collectAsState()
    val currentPositionMs by playerManager.currentPositionMs.collectAsState()
    val durationMs by playerManager.durationMs.collectAsState()
    val isLooping by playerManager.isLooping.collectAsState()

    var isExpanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = currentItem != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        currentItem?.let { item ->
            val progress = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                DarkSurfaceElevated,
                                DarkCanvas
                            )
                        )
                    )
            ) {
                // Mini progress line
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = CyanPrimary,
                    trackColor = Color(0x33FFFFFF)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Artwork Thumbnail
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCanvas),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.thumbnailUrl.isNotBlank()) {
                            AsyncImage(
                                model = item.thumbnailUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(46.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${formatDuration(currentPositionMs)} / ${formatDuration(durationMs)} • ${item.author}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }

                    // 10s Rewind
                    IconButton(
                        onClick = { playerManager.rewind10Seconds() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Play / Pause Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary)
                            .clickable { playerManager.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = DarkCanvas,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 10s Forward
                    IconButton(
                        onClick = { playerManager.forward10Seconds() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Close Button
                    IconButton(
                        onClick = { playerManager.stop() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Expanded seeker & loop controls
                if (isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Slider(
                            value = currentPositionMs.toFloat(),
                            onValueChange = { playerManager.seekTo(it.toLong()) },
                            valueRange = 0f..durationMs.toFloat().coerceAtLeast(1000f),
                            colors = SliderDefaults.colors(
                                thumbColor = CyanPrimary,
                                activeTrackColor = CyanPrimary,
                                inactiveTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatDuration(currentPositionMs),
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            IconButton(
                                onClick = { playerManager.toggleLoop() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                    contentDescription = "Loop",
                                    tint = if (isLooping) CyanPrimary else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = formatDuration(durationMs),
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
