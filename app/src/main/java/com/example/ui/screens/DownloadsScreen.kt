package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.MediaType
import com.example.engine.downloader.DownloadEngine
import com.example.ui.components.GlassCard
import com.example.ui.components.SpeedMeterHeader
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.RoseError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import com.example.viewmodel.AppTab
import com.example.viewmodel.MainViewModel

@Composable
fun DownloadsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    val totalSpeed by viewModel.totalLiveSpeed.collectAsState()

    var selectedFilterTab by remember { mutableStateOf(0) } // 0: All, 1: Active, 2: Paused

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Speed Meter & Live Network Visualizer Header
        SpeedMeterHeader(
            totalSpeedBytes = totalSpeed,
            activeCount = activeDownloads.count { it.status == DownloadStatus.DOWNLOADING },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .padding(4.dp)
        ) {
            val tabs = listOf(
                "الكل (${activeDownloads.size})",
                "قيد التحميل (${activeDownloads.count { it.status == DownloadStatus.DOWNLOADING }})",
                "متوقف مؤقتاً (${activeDownloads.count { it.status == DownloadStatus.PAUSED }})"
            )

            tabs.forEachIndexed { index, title ->
                val isSelected = selectedFilterTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyanPrimary else Color.Transparent)
                        .clickable { selectedFilterTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) DarkCanvas else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredList = activeDownloads.filter {
            when (selectedFilterTab) {
                1 -> it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED
                2 -> it.status == DownloadStatus.PAUSED
                else -> true
            }
        }

        if (filteredList.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "لا توجد تنزيلات نشطة حالياً",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "انسخ رابط أي فيديو أو أغنية من YouTube, TikTok أو Instagram وابدأ التحميل بسرعة قصوى",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { viewModel.setTab(AppTab.HOME) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = DarkCanvas
                        )
                    ) {
                        Text("الذهاب للرئيسية وإضافة رابط", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    DownloadCard(
                        item = item,
                        onPause = { viewModel.pauseDownload(item.id) },
                        onResume = { viewModel.resumeDownload(item.id) },
                        onCancel = { viewModel.cancelDownload(item.id) },
                        onRetry = { viewModel.retryDownload(item) },
                        onPlay = { viewModel.playMedia(item) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun DownloadCard(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onPlay: () -> Unit
) {
    val isDownloading = item.status == DownloadStatus.DOWNLOADING
    val isPaused = item.status == DownloadStatus.PAUSED
    val isFailed = item.status == DownloadStatus.FAILED
    val isCompleted = item.status == DownloadStatus.COMPLETED

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("download_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = DarkSurface,
        borderColor = if (isDownloading) CyanPrimary.copy(alpha = 0.4f) else DarkSurfaceBorder
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Media Thumbnail
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.thumbnailUrl.isNotBlank()) {
                        AsyncImage(
                            model = item.thumbnailUrl,
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = if (item.mediaType == MediaType.AUDIO_MP3 || item.mediaType == MediaType.AUDIO_M4A)
                                Icons.Default.MusicNote else Icons.Default.Videocam,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Format Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.8f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = item.mediaType.extension.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Quality Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(item.platform.brandHex).copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = item.platform.title,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(item.platform.brandHex)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.quality,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Quick Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isDownloading) {
                        IconButton(
                            onClick = onPause,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AmberWarning.copy(alpha = 0.15f))
                                .testTag("pause_download_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pause",
                                tint = AmberWarning,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else if (isPaused) {
                        IconButton(
                            onClick = onResume,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.15f))
                                .testTag("resume_download_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else if (isFailed) {
                        IconButton(
                            onClick = onRetry,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RoseError.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = RoseError,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .testTag("cancel_download_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancel",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Progress Bar
            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = when {
                    isDownloading -> CyanPrimary
                    isPaused -> AmberWarning
                    isFailed -> RoseError
                    else -> EmeraldSuccess
                },
                trackColor = DarkSurfaceElevated
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Live Metrics Strip (Moment by Moment Speed, Size, ETA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Percentage & Transferred Bytes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${(item.progress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDownloading) CyanPrimary else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${DownloadEngine.formatBytes(item.downloadedBytes)} / ${DownloadEngine.formatBytes(item.totalBytes)})",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Live Speed & ETA Badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isDownloading && item.speedBytesPerSec > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyanPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = DownloadEngine.formatSpeed(item.speedBytesPerSec),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary
                                )
                            }
                        }

                        if (item.etaSeconds > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "متبقي ${DownloadEngine.formatEta(item.etaSeconds)}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    } else if (isPaused) {
                        Text(
                            text = "متوقف مؤقتاً",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberWarning
                        )
                    } else if (isFailed) {
                        Text(
                            text = item.errorMessage ?: "خطأ في التنزيل",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoseError
                        )
                    }
                }
            }
        }
    }
}
