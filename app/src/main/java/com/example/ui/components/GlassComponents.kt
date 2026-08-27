package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = DarkSurface,
    borderColor: Color = DarkSurfaceBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(elevation, shape = shape, spotColor = Color(0x3300E5FF))
            .clip(shape)
            .border(BorderStroke(borderWidth, borderColor), shape),
        color = backgroundColor,
        shape = shape
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun NeonGlowBox(
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF00E5FF),
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        DarkSurfaceElevated,
                        DarkSurface
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            glowColor.copy(alpha = 0.6f),
                            Color(0x1AFFFFFF),
                            glowColor.copy(alpha = 0.2f)
                        )
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}
