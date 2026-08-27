package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0.1f) }
    var currentStatusText by remember { mutableStateOf("جاري تشغيل محرك التنزيل فائق السرعة...") }
    var isReady by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    val scaleAnim = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))

        delay(400)
        progress = 0.35f
        currentStatusText = "فحص بروتوكولات المنصات (YouTube, TikTok, Insta)..."

        delay(450)
        progress = 0.70f
        currentStatusText = "تجهيز مكتبة الصوتيات والفيديوهات المنظمة..."

        delay(500)
        progress = 1.0f
        currentStatusText = "اكتملت التهيئة بنجاح! جاهز للتنزيل"
        isReady = true

        delay(600)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        DarkCanvas,
                        DarkSurface,
                        DarkCanvas
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background subtle ambient glow circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(CyanPrimary.copy(alpha = 0.15f), Color.Transparent)
                ),
                radius = size.width * 0.5f,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .scale(scaleAnim.value)
        ) {
            // Glowing Orbital Logo Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                // Orbital rotating ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                ) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                CyanPrimary,
                                VioletAccent,
                                IndigoAccent,
                                Color.Transparent,
                                CyanPrimary
                            )
                        ),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner Glow Logo Container
                Box(
                    modifier = Modifier
                        .size(126.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(DarkSurfaceElevated, DarkCanvas)
                            )
                        )
                        .border(2.dp, CyanPrimary.copy(alpha = 0.5f), CircleShape)
                        .shadow(16.dp, CircleShape, spotColor = CyanPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = R.drawable.img_splash_logo,
                        contentDescription = "App Splash Logo",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name Title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OmniGrab",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyanPrimary, IndigoAccent)
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MAX SPEED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCanvas
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "تحميل الفيديوهات والأغاني من جميع المنصات بدقة فائقة",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Loading Progress Bar
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = CyanPrimary,
                    trackColor = DarkSurfaceElevated
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentStatusText,
                        fontSize = 11.sp,
                        color = if (isReady) EmeraldSuccess else TextMuted,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Enter Button (if user wants to skip immediately or auto finishes)
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
                    .testTag("splash_enter_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = DarkCanvas
                )
            ) {
                Text(
                    text = "دخول التطبيق",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
