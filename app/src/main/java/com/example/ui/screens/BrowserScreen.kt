package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.PlatformType
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import com.example.viewmodel.MainViewModel

@Composable
fun BrowserScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val browserUrl by viewModel.browserUrl.collectAsState()
    val sniffedMediaList by viewModel.sniffedMedia.collectAsState()
    var inputUrlText by remember { mutableStateOf(browserUrl) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val bookmarks = listOf(
        Pair("YouTube", "https://m.youtube.com"),
        Pair("TikTok", "https://www.tiktok.com"),
        Pair("Instagram", "https://www.instagram.com"),
        Pair("SoundCloud", "https://m.soundcloud.com"),
        Pair("Twitter / X", "https://mobile.twitter.com"),
        Pair("Google", "https://www.google.com")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // URL Bar & Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { webViewInstance?.goBack() },
                    enabled = webViewInstance?.canGoBack() == true,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }

                IconButton(
                    onClick = { webViewInstance?.reload() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = TextPrimary)
                }

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedTextField(
                    value = inputUrlText,
                    onValueChange = { inputUrlText = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("browser_url_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                var target = inputUrlText.trim()
                                if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                    target = "https://www.google.com/search?q=${target.replace(" ", "+")}"
                                }
                                viewModel.setBrowserUrl(target)
                                webViewInstance?.loadUrl(target)
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Go", tint = CyanPrimary)
                        }
                    },
                    singleLine = true
                )
            }

            // Quick Bookmarks Carousel
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarks) { (name, url) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated)
                            .clickable {
                                inputUrlText = url
                                viewModel.setBrowserUrl(url)
                                webViewInstance?.loadUrl(url)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = name, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // In-App WebView
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    url?.let {
                                        inputUrlText = it
                                        viewModel.setBrowserUrl(it)
                                    }
                                }
                            }
                            loadUrl(browserUrl)
                            webViewInstance = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Sniffer Detected Media Floating Banner
        AnimatedVisibility(
            visible = sniffedMediaList.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            sniffedMediaList.firstOrNull()?.let { media ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(DarkSurface, DarkSurfaceElevated)
                            )
                        )
                        .border(1.5.dp, CyanPrimary, RoundedCornerShape(18.dp))
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "تم التقاط الوسائط من الصفحة!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                                Text(
                                    text = media.title,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.setUrlInput(media.originalUrl)
                                viewModel.extractMedia(media.originalUrl)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = DarkCanvas
                            ),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تحميل MP4/MP3", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
