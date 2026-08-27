package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.downloader.DownloadEngine
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@Composable
fun SpeedMeterHeader(
    totalSpeedBytes: Long,
    activeCount: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = DarkSurface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(CyanPrimary, IndigoAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Speed",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "سرعة التحميل اللحظية",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = DownloadEngine.formatSpeed(totalSpeedBytes),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (totalSpeedBytes > 0) CyanPrimary else TextPrimary
                            )
                            if (totalSpeedBytes > 0) {
                                Text(
                                    text = " • بث مباشر",
                                    fontSize = 11.sp,
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Active Downloads Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activeCount > 0) CyanPrimary.copy(alpha = 0.15f) else DarkSurfaceElevated)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (activeCount > 0) EmeraldSuccess else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$activeCount نشط",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeCount > 0) CyanPrimary else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Real-Time Network Waveform Visualizer
            NetworkSpeedWave(
                speedBytes = totalSpeedBytes,
                isActive = activeCount > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            )
        }
    }
}

@Composable
fun NetworkSpeedWave(
    speedBytes: Long,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        val wavePath = Path()
        wavePath.moveTo(0f, midY)

        val amplitude = if (isActive && speedBytes > 0) (height * 0.38f).coerceAtMost(16.dp.toPx()) else 2.dp.toPx()
        val points = 24
        val step = width / points

        for (i in 0..points) {
            val x = i * step
            val angle = (x / width) * 4 * Math.PI.toFloat() + phase
            val y = midY + Math.sin(angle.toDouble()).toFloat() * amplitude
            if (i == 0) {
                wavePath.moveTo(x, y)
            } else {
                wavePath.lineTo(x, y)
            }
        }

        drawPath(
            path = wavePath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    CyanPrimary.copy(alpha = 0.2f),
                    CyanPrimary,
                    VioletAccent,
                    IndigoAccent,
                    CyanPrimary.copy(alpha = 0.2f)
                )
            ),
            style = Stroke(
                width = if (isActive) 3.dp.toPx() else 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}
