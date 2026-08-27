package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DownloadItem
import com.example.data.model.MediaType
import com.example.engine.downloader.DownloadEngine
import com.example.ui.components.GlassCard
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
import com.example.viewmodel.LibraryFilter
import com.example.viewmodel.MainViewModel
import java.io.File

@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val libraryItems by viewModel.libraryMediaItems.collectAsState()
    val currentFilter by viewModel.libraryFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var itemToRename by remember { mutableStateOf<DownloadItem?>(null) }
    var itemToDelete by remember { mutableStateOf<DownloadItem?>(null) }
    var itemToShowInfo by remember { mutableStateOf<DownloadItem?>(null) }
    var renameInputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search & Filter Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("بحث في مكتبة المقاطع والأغاني...", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextMuted
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("library_search_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips (All, Videos, Audio, Favorites)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .padding(4.dp)
        ) {
            val filters = listOf(
                LibraryFilter.ALL,
                LibraryFilter.VIDEOS,
                LibraryFilter.AUDIO,
                LibraryFilter.FAVORITES
            )

            filters.forEach { filter ->
                val isSelected = currentFilter == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyanPrimary else Color.Transparent)
                        .clickable { viewModel.setLibraryFilter(filter) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.labelAr,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) DarkCanvas else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (libraryItems.isEmpty()) {
            // Empty Library State
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
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (searchQuery.isNotBlank()) "لم يتم العثور على نتائج للبحث" else "المكتبة فارغة حالياً",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (searchQuery.isNotBlank()) "جرب البحث بكلمات أخرى"
                        else "قم بتحميل مقاطع فيديو أو ملفات صوتية وستظهر منظمة هنا تلقائياً",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    if (searchQuery.isBlank()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { viewModel.setTab(AppTab.HOME) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = DarkCanvas
                            )
                        ) {
                            Text("تنزيل أول ملف الآن", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
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
                items(libraryItems, key = { it.id }) { item ->
                    LibraryMediaCard(
                        item = item,
                        onPlay = { viewModel.playMedia(item) },
                        onToggleFavorite = { viewModel.toggleFavorite(item) },
                        onRename = {
                            itemToRename = item
                            renameInputText = item.title
                        },
                        onDelete = { itemToDelete = item },
                        onShowInfo = { itemToShowInfo = item },
                        onShare = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = if (item.mediaType == MediaType.AUDIO_MP3 || item.mediaType == MediaType.AUDIO_M4A) "audio/*" else "video/*"
                                putExtra(Intent.EXTRA_SUBJECT, item.title)
                                putExtra(Intent.EXTRA_TEXT, "تم التحميل بواسطة تطبيق OmniGrab:\n${item.title}\n${item.url}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "مشاركة الملف"))
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Rename Dialog
    itemToRename?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToRename = null },
            containerColor = DarkSurface,
            title = { Text("إعادة تسمية الملف", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameInputText,
                    onValueChange = { renameInputText = it },
                    label = { Text("اسم الملف الجديد", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameInputText.isNotBlank()) {
                            viewModel.renameItem(item.id, renameInputText.trim())
                        }
                        itemToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = DarkCanvas)
                ) {
                    Text("حفظ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToRename = null }) {
                    Text("إلغاء", color = TextSecondary)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            containerColor = DarkSurface,
            title = { Text("حذف الملف من المكتبة", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في حذف \"${item.title}\" من التطبيق ومساحة التخزين؟",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLibraryItem(item)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError, contentColor = Color.White)
                ) {
                    Text("حذف نهائي", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("إلغاء", color = TextSecondary)
                }
            }
        )
    }

    // File Info Dialog
    itemToShowInfo?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToShowInfo = null },
            containerColor = DarkSurface,
            title = { Text("تفاصيل الملف", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    InfoRow("العنوان:", item.title)
                    InfoRow("المنصة:", item.platform.title)
                    InfoRow("الصيغة والجودة:", "${item.mediaType.labelAr} (${item.quality})")
                    InfoRow("الحجم:", DownloadEngine.formatBytes(item.totalBytes))
                    InfoRow("المسار:", item.localFilePath.ifBlank { "مجلد التنزيلات الداخلي" })
                }
            },
            confirmButton = {
                Button(
                    onClick = { itemToShowInfo = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = DarkCanvas)
                ) {
                    Text("إغلاق", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 12.sp, color = TextPrimary)
    }
}

@Composable
fun LibraryMediaCard(
    item: DownloadItem,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onShowInfo: () -> Unit,
    onShare: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val isAudio = item.mediaType == MediaType.AUDIO_MP3 || item.mediaType == MediaType.AUDIO_M4A

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .testTag("library_item_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        backgroundColor = DarkSurface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with Play Button
            Box(
                modifier = Modifier
                    .size(68.dp)
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
                }

                // Play Overlay Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isAudio) Icons.Default.MusicNote else Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Format Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = item.mediaType.extension.uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAudio) VioletAccent else CyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
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
                        text = "${item.quality} • ${DownloadEngine.formatBytes(item.totalBytes)}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Favorite Button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (item.isFavorite) RoseError else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            // More Options Dropdown
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DarkSurfaceElevated)
                ) {
                    DropdownMenuItem(
                        text = { Text("تشغيل", color = TextPrimary) },
                        onClick = {
                            showMenu = false
                            onPlay()
                        },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, null, tint = CyanPrimary) }
                    )
                    DropdownMenuItem(
                        text = { Text("مشاركة", color = TextPrimary) },
                        onClick = {
                            showMenu = false
                            onShare()
                        },
                        leadingIcon = { Icon(Icons.Default.Share, null, tint = TextSecondary) }
                    )
                    DropdownMenuItem(
                        text = { Text("إعادة تسمية", color = TextPrimary) },
                        onClick = {
                            showMenu = false
                            onRename()
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = TextSecondary) }
                    )
                    DropdownMenuItem(
                        text = { Text("تفاصيل الملف", color = TextPrimary) },
                        onClick = {
                            showMenu = false
                            onShowInfo()
                        },
                        leadingIcon = { Icon(Icons.Default.Info, null, tint = TextSecondary) }
                    )
                    DropdownMenuItem(
                        text = { Text("حذف", color = RoseError) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = RoseError) }
                    )
                }
            }
        }
    }
}
