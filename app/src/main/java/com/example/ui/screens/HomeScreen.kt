package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.PlatformType
import com.example.engine.downloader.DownloadEngine
import com.example.engine.extractor.MediaUrlParser
import com.example.ui.components.GlassCard
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
import com.example.viewmodel.ExtractionState
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val urlInput by viewModel.urlInput.collectAsState()
    val extractionState by viewModel.extractionState.collectAsState()
    val clipboardUrl by viewModel.clipboardDetectedUrl.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    val totalSpeed by viewModel.totalLiveSpeed.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Clipboard Auto-Detected Link Banner
        AnimatedVisibility(
            visible = clipboardUrl != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            clipboardUrl?.let { link ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(IndigoAccent.copy(alpha = 0.25f), CyanPrimary.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "تم اكتشاف رابط من الحافظة!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = link,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row {
                            Button(
                                onClick = { viewModel.extractMedia(link) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanPrimary,
                                    contentColor = DarkCanvas
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("تحميل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { viewModel.dismissClipboardPrompt() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Dismiss",
                                    tint = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Primary URL Input Box
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = DarkSurface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ضع رابط الفيديو أو الأغنية",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Outlined Input with Paste & Clear
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { viewModel.setUrlInput(it) },
                    placeholder = {
                        Text(
                            text = "https://www.youtube.com/... أو TikTok / Instagram",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("url_input_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (urlInput.isNotBlank()) {
                                IconButton(onClick = { viewModel.setUrlInput("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextMuted
                                    )
                                }
                            }
                            IconButton(
                                onClick = { viewModel.pasteFromClipboard() },
                                modifier = Modifier.testTag("paste_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste",
                                    tint = CyanPrimary
                                )
                            }
                        }
                    },
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Extract Button
                Button(
                    onClick = { viewModel.extractMedia() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("extract_button"),
                    shape = RoundedCornerShape(16.dp),
                    enabled = urlInput.isNotBlank() && extractionState !is ExtractionState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = DarkCanvas,
                        disabledContainerColor = DarkSurfaceElevated,
                        disabledContentColor = TextMuted
                    )
                ) {
                    if (extractionState is ExtractionState.Loading) {
                        CircularProgressIndicator(
                            color = DarkCanvas,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جاري استخراج وتحليل الوسائط...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تحليل واختيار جودة التنزيل (MP4 / MP3)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Extraction Error banner if any
                if (extractionState is ExtractionState.Error) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoseError.copy(alpha = 0.15f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = (extractionState as ExtractionState.Error).message,
                            fontSize = 12.sp,
                            color = RoseError,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Supported Platforms Strip
        Text(
            text = "المنصات المدعومة بالكامل",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val platforms = listOf(
                Pair(PlatformType.YOUTUBE, "YouTube"),
                Pair(PlatformType.TIKTOK, "TikTok"),
                Pair(PlatformType.INSTAGRAM, "Instagram"),
                Pair(PlatformType.TWITTER, "X / Twitter"),
                Pair(PlatformType.FACEBOOK, "Facebook"),
                Pair(PlatformType.SOUNDCLOUD, "SoundCloud"),
                Pair(PlatformType.PINTEREST, "Pinterest"),
                Pair(PlatformType.DIRECT_LINK, "روابط مباشرة")
            )

            platforms.forEach { (platform, name) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(1.dp, Color(platform.brandHex).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable {
                            // Find sample for this platform and load it for easy testing
                            val sample = MediaUrlParser.samplePresetList.firstOrNull { it.platform == platform }
                            sample?.let {
                                viewModel.setUrlInput(it.url)
                                viewModel.extractMedia(it.url)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(platform.brandHex))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Preset Sample Links Carousel (One-Tap Test Downloads)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "مقاطع جاهزة للاختبار الفوري (1-انقر للتحميل)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(MediaUrlParser.samplePresetList) { sample ->
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(18.dp))
                        .clickable {
                            viewModel.setUrlInput(sample.url)
                            viewModel.extractMedia(sample.url)
                        }
                        .padding(12.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                        ) {
                            AsyncImage(
                                model = sample.thumbnailUrl,
                                contentDescription = sample.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Platform tag
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(6.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(sample.platform.brandHex))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = sample.platform.title,
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = sample.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = sample.author,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanPrimary.copy(alpha = 0.15f))
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تحميل الآن",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Features Highlights Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurfaceElevated)
                .padding(16.dp)
        ) {
            Text(
                text = "مميزات OmniGrab الاحترافية",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FeaturePill(Icons.Default.FileDownload, "تحميل في الخلفية", "استمرار التنزيل عند قفل الشاشة")
                Spacer(modifier = Modifier.width(8.dp))
                FeaturePill(Icons.Default.MusicNote, "استخراج MP3 320k", "صوت نقي فائق الجودة")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FeaturePill(Icons.Default.HighQuality, "فيديو HD 1080p", "أعلى دقة بدون ضياع")
                Spacer(modifier = Modifier.width(8.dp))
                FeaturePill(Icons.Default.PlayArrow, "مشغل وسائط مدمج", "تشغيل فوري داخل التطبيق")
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(CyanPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = desc,
                fontSize = 9.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
