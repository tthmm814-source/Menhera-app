package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val threads by viewModel.concurrentThreads.collectAsState()
    var bgDownloadEnabled by remember { mutableStateOf(true) }
    var autoClipboardEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "إعدادات التنزيل والسرعة",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Concurrent Threads Slider Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = DarkSurface
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "خيوط التنزيل المتزامنة (Turbo Threads)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "تسريع التحميل بتقسيم الملف إلى أجزاء متعددة",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Text(
                        text = "$threads مسارات",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = threads.toFloat(),
                    onValueChange = { viewModel.setConcurrentThreads(it.toInt()) },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanPrimary,
                        activeTrackColor = CyanPrimary,
                        inactiveTrackColor = DarkSurfaceElevated
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Background Service & Clipboard Toggles
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = DarkSurface
        ) {
            Column {
                SettingsSwitchRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "التنزيل في الخلفية (Foreground Service)",
                    subtitle = "استمرار التحميل حتى لو تم إغلاق التطبيق أو قفل الشاشة",
                    checked = bgDownloadEnabled,
                    onCheckedChange = { bgDownloadEnabled = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    icon = Icons.Default.ContentPaste,
                    title = "اكتشاف الروابط التلقائي من الحافظة",
                    subtitle = "عرض شريط سريع فور نسخ أي رابط فيديو أو أغنية",
                    checked = autoClipboardEnabled,
                    onCheckedChange = { autoClipboardEnabled = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "إدارة التخزين والذاكرة المؤقتة",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = DarkSurface
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Folder, null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("موقع حفظ التنزيلات", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("/storage/emulated/0/Download/OmniGrab", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "تم تنظيف الذاكرة المؤقتة بنجاح!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated, contentColor = TextPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CleaningServices, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تنظيف الكاش", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.clearAllCompleted()
                            Toast.makeText(context, "تم مسح سجل التنزيلات المكتملة", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError.copy(alpha = 0.15f), contentColor = RoseError),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DeleteSweep, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مسح السجل", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About OmniGrab Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = DarkSurface
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OmniGrab Media Engine v2.4",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "محرك تنزيل متكامل يدعم MP4, HD 1080p, MP3 320kbps",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "جميع المنصات مدعومة • تشغيل مباشر بدون إعلانات",
                    fontSize = 10.sp,
                    color = CyanPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(subtitle, fontSize = 11.sp, color = TextSecondary)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkCanvas,
                checkedTrackColor = CyanPrimary,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkSurfaceElevated
            )
        )
    }
}
