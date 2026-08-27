package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.engine.downloader.DownloadEngine
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
import com.example.viewmodel.AppTab

@Composable
fun TopNavBar(
    totalSpeedBytes: Long,
    activeDownloadsCount: Int,
    onSpeedBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity & Logo
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.img_app_icon,
                    contentDescription = "OmniGrab Logo",
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OmniGrab",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CyanPrimary, VioletAccent)
                                )
                            )
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "PRO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkCanvas
                        )
                    }
                }
                Text(
                    text = "محمل الفيديوهات والأغاني الشامل",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }

        // Live Speed Gauge Pill Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (totalSpeedBytes > 0) CyanPrimary.copy(alpha = 0.15f)
                    else DarkSurfaceElevated
                )
                .border(
                    1.dp,
                    if (totalSpeedBytes > 0) CyanPrimary.copy(alpha = 0.6f) else DarkSurfaceBorder,
                    RoundedCornerShape(20.dp)
                )
                .clickable { onSpeedBadgeClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Speed",
                    tint = if (totalSpeedBytes > 0) CyanPrimary else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (totalSpeedBytes > 0) DownloadEngine.formatSpeed(totalSpeedBytes) else "0 KB/s",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalSpeedBytes > 0) CyanPrimary else TextSecondary
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    activeDownloadsCount: Int,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationItem(AppTab.HOME, "الرئيسية", Icons.Default.Home),
        NavigationItem(AppTab.DOWNLOADS, "التنزيلات", Icons.Default.Download, hasBadge = activeDownloadsCount > 0, badgeText = "$activeDownloadsCount"),
        NavigationItem(AppTab.LIBRARY, "المكتبة", Icons.Default.Folder),
        NavigationItem(AppTab.BROWSER, "المتصفح", Icons.Default.Language),
        NavigationItem(AppTab.SETTINGS, "الإعدادات", Icons.Default.Settings)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = CyanPrimary)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentTab == item.tab
                val animatedBgColor by animateColorAsState(
                    targetValue = if (isSelected) CyanPrimary.copy(alpha = 0.15f) else Color.Transparent,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "tabBg"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(animatedBgColor)
                        .clickable { onTabSelected(item.tab) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("tab_${item.tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (item.hasBadge) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = CyanPrimary,
                                        contentColor = DarkCanvas
                                    ) {
                                        Text(text = item.badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) CyanPrimary else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) CyanPrimary else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyanPrimary else TextMuted
                        )
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val tab: AppTab,
    val title: String,
    val icon: ImageVector,
    val hasBadge: Boolean = false,
    val badgeText: String = ""
)
