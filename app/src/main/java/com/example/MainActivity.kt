package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import com.example.data.model.DownloadStatus
import com.example.ui.components.AudioPlayerBottomBar
import com.example.ui.components.BottomNavBar
import com.example.ui.components.FormatQualityPickerSheet
import com.example.ui.components.TopNavBar
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppTab
import com.example.viewmodel.ExtractionState
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Set RTL Arabic layout direction for smooth native Arabic experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    OmniGrabApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkClipboardForMediaUrl()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniGrabApp(viewModel: MainViewModel) {
    val isSplashVisible by viewModel.isSplashVisible.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    val totalSpeed by viewModel.totalLiveSpeed.collectAsState()
    val extractionState by viewModel.extractionState.collectAsState()
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val showFormatSheet by viewModel.showFormatSheet.collectAsState()
    val activeVideoItem by viewModel.activeVideoPlayerItem.collectAsState()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (isSplashVisible) {
        SplashScreen(onDismiss = { viewModel.dismissSplash() })
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkCanvas),
            containerColor = DarkCanvas,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopNavBar(
                    totalSpeedBytes = totalSpeed,
                    activeDownloadsCount = activeDownloads.count { it.status == DownloadStatus.DOWNLOADING },
                    onSpeedBadgeClick = { viewModel.setTab(AppTab.DOWNLOADS) }
                )
            },
            bottomBar = {
                Box {
                    BottomNavBar(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) },
                        activeDownloadsCount = activeDownloads.count { it.status == DownloadStatus.DOWNLOADING }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tab screens transition
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tabContent"
                ) { tab ->
                    when (tab) {
                        AppTab.HOME -> HomeScreen(viewModel = viewModel)
                        AppTab.DOWNLOADS -> DownloadsScreen(viewModel = viewModel)
                        AppTab.LIBRARY -> LibraryScreen(viewModel = viewModel)
                        AppTab.BROWSER -> BrowserScreen(viewModel = viewModel)
                        AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }

                // Docked In-App Audio Player (floating right above bottom nav bar)
                AudioPlayerBottomBar(
                    playerManager = viewModel.playerManager,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Modal Format & Quality Picker Bottom Sheet
        if (showFormatSheet && extractionState is ExtractionState.Success) {
            val mediaInfo = (extractionState as ExtractionState.Success).mediaInfo
            FormatQualityPickerSheet(
                mediaInfo = mediaInfo,
                selectedFormat = selectedFormat,
                onFormatSelected = { viewModel.selectFormat(it) },
                onConfirmDownload = { info, format ->
                    viewModel.startDownload(info, format)
                },
                onDismiss = { viewModel.setShowFormatSheet(false) }
            )
        }

        // In-App Video Player Dialog
        activeVideoItem?.let { item ->
            VideoPlayerDialog(
                item = item,
                onDismiss = { viewModel.closeVideoPlayer() }
            )
        }
    }
}
