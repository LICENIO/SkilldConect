package com.skillconnect.app.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Paleta de colores Neumórfica con Azul Celeste Eléctrico vibrante como la imagen enviada

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object NeumorphicColors {
    var bg by mutableStateOf(Color(0xFFF7FAFC))
    var surface by mutableStateOf(Color(0xFFFFFFFF))
    var surfaceAlt by mutableStateOf(Color(0xFFEAF4F4))
    var text by mutableStateOf(Color(0xFF0F172A))
    var muted by mutableStateOf(Color(0xFF64748B))
    var primary by mutableStateOf(Color(0xFF0088FF))
    var headerBg by mutableStateOf(Color(0xFF0088FF))
    var bottomNavBg by mutableStateOf(Color(0xFF0077FF))
    var accentYellow by mutableStateOf(Color(0xFFFDE68A))
    var accentCyan by mutableStateOf(Color(0xFF38BDF8))
    var green by mutableStateOf(Color(0xFF10B981))
    var orange by mutableStateOf(Color(0xFFF97316))
    var purple by mutableStateOf(Color(0xFF8B5CF6))
    var pink by mutableStateOf(Color(0xFFEC4899))
    var teal by mutableStateOf(Color(0xFF00A896))
    
    var lightShadow by mutableStateOf(Color(0xFFFFFFFF))
    var darkShadow by mutableStateOf(Color(0xFFD5DBE5))

    fun setDarkMode(isDark: Boolean) {
        if (isDark) {
            bg = Color(0xFF1E1E1E)
            surface = Color(0xFF2A2A2A)
            surfaceAlt = Color(0xFF333333)
            text = Color(0xFFF7FAFC)
            muted = Color(0xFFA0AABF)
            primary = Color(0xFF38BDF8) // Brighter for dark mode
            headerBg = Color(0xFF1E1E1E)
            bottomNavBg = Color(0xFF1E1E1E)
            
            lightShadow = Color(0xFF2C2C2C) // Destello oscuro
            darkShadow = Color(0xFF111111) // Sombra profunda
        } else {
            bg = Color(0xFFF7FAFC)
            surface = Color(0xFFFFFFFF)
            surfaceAlt = Color(0xFFEAF4F4)
            text = Color(0xFF0F172A)
            muted = Color(0xFF64748B)
            primary = Color(0xFF0088FF)
            headerBg = Color(0xFF0088FF)
            bottomNavBg = Color(0xFF0077FF)
            
            lightShadow = Color(0xFFFFFFFF)
            darkShadow = Color(0xFFD5DBE5)
        }
    }
}


// Modificador neumórfico personalizado para efectos Soft UI
fun Modifier.neumorphic(
    lightShadowColor: Color = NeumorphicColors.lightShadow,
    darkShadowColor: Color = NeumorphicColors.darkShadow,
    shadowRadius: Dp = 6.dp,
    offset: Dp = 5.dp,
    isInnerShadow: Boolean = false,
    cornerRadius: Dp = 16.dp
): Modifier = this.drawBehind {
    val size = this.size
    val radiusPx = cornerRadius.toPx()
    val shadowRadiusPx = shadowRadius.toPx()
    val offsetPx = offset.toPx()

    if (shadowRadiusPx <= 0f) return@drawBehind

    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
        }

        if (!isInnerShadow) {
            // Sombra blanca superior-izquierda (luz)
            paint.color = lightShadowColor.toArgb()
            paint.maskFilter = BlurMaskFilter(shadowRadiusPx, BlurMaskFilter.Blur.NORMAL)
            canvas.nativeCanvas.drawRoundRect(
                -offsetPx,
                -offsetPx,
                size.width - offsetPx,
                size.height - offsetPx,
                radiusPx,
                radiusPx,
                paint
            )

            // Sombra gris inferior-derecha (oscuridad)
            paint.color = darkShadowColor.toArgb()
            paint.maskFilter = BlurMaskFilter(shadowRadiusPx, BlurMaskFilter.Blur.NORMAL)
            canvas.nativeCanvas.drawRoundRect(
                offsetPx,
                offsetPx,
                size.width + offsetPx,
                size.height + offsetPx,
                radiusPx,
                radiusPx,
                paint
            )
        } else {
            // Efecto hundido (sombra interior)
            canvas.save()
            val path = android.graphics.Path().apply {
                addRoundRect(
                    0f, 0f, size.width, size.height,
                    radiusPx, radiusPx, android.graphics.Path.Direction.CW
                )
            }
            canvas.nativeCanvas.clipPath(path)

            // Sombra interior oscura en el borde superior e izquierdo
            paint.color = darkShadowColor.toArgb()
            paint.maskFilter = BlurMaskFilter(shadowRadiusPx, BlurMaskFilter.Blur.NORMAL)
            canvas.nativeCanvas.drawRoundRect(
                -offsetPx,
                -offsetPx,
                size.width,
                size.height,
                radiusPx,
                radiusPx,
                paint
            )

            // Sombra interior clara en el borde inferior y derecho
            paint.color = lightShadowColor.toArgb()
            paint.maskFilter = BlurMaskFilter(shadowRadiusPx, BlurMaskFilter.Blur.NORMAL)
            canvas.nativeCanvas.drawRoundRect(
                offsetPx,
                offsetPx,
                size.width + offsetPx * 2,
                size.height + offsetPx * 2,
                radiusPx,
                radiusPx,
                paint
            )

            canvas.restore()
        }
    }
}
