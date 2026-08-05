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

// Sistema de color premium unificado — Azul Índigo + Violeta eléctrico
// Paleta reducida a 8 tokens para máxima coherencia visual

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object NeumorphicColors {
    // ── Paleta "Ocean Deep" — Blue #3B82F6 → Teal #06B6D4 ────────────
    var bg          by mutableStateOf(Color(0xFFF0F7FF)) // Azul-blanco muy limpio
    var surface     by mutableStateOf(Color(0xFFFFFFFF)) // Blanco puro — tarjetas
    var surfaceAlt  by mutableStateOf(Color(0xFFE0EFFF)) // Azul hielo suave

    var text        by mutableStateOf(Color(0xFF0F172A)) // Slate-900 oscuro impactante
    var muted       by mutableStateOf(Color(0xFF64748B)) // Slate-500 neutro elegante

    var primary     by mutableStateOf(Color(0xFF3B82F6)) // Blue-500 — el azul perfecto
    var accent      by mutableStateOf(Color(0xFF06B6D4)) // Cyan-500 — teal eléctrico

    var headerBg    by mutableStateOf(Color(0xFF3B82F6))
    var bottomNavBg by mutableStateOf(Color(0xFF1E3A5F))

    var lightShadow by mutableStateOf(Color(0xFFFFFFFF))
    var darkShadow  by mutableStateOf(Color(0xFFBDD5F0)) // Sombra azulada muy suave

    val accentYellow get() = Color(0xFFFBBF24) // ámbar que combina con teal
    val green        get() = Color(0xFF10B981) // esmeralda que armoniza
    val orange       get() = Color(0xFFF97316)

    fun setDarkMode(isDark: Boolean) {
        if (isDark) {
            // Dark Mode — azul noche profundo
            bg         = Color(0xFF0D1829) // Azul noche profundo
            surface    = Color(0xFF162032) // Azul marino oscuro
            surfaceAlt = Color(0xFF1E2D42)

            text       = Color(0xFFE2F0FF) // Blanco azulado suave
            muted      = Color(0xFF7FA8C9) // Azul gris claro

            primary    = Color(0xFF60A5FA) // Blue-400 — más luminoso sobre oscuro
            accent     = Color(0xFF22D3EE) // Cyan-400 — teal brillante

            headerBg   = Color(0xFF0D1829)
            bottomNavBg= Color(0xFF0D1829)

            lightShadow = Color(0xFF1E2D42)
            darkShadow  = Color(0xFF050C14)

        } else {
            bg         = Color(0xFFF0F7FF)
            surface    = Color(0xFFFFFFFF)
            surfaceAlt = Color(0xFFE0EFFF)

            text       = Color(0xFF0F172A)
            muted      = Color(0xFF64748B)

            primary    = Color(0xFF3B82F6)
            accent     = Color(0xFF06B6D4)

            headerBg   = Color(0xFF3B82F6)
            bottomNavBg= Color(0xFF1E3A5F)

            lightShadow = Color(0xFFFFFFFF)
            darkShadow  = Color(0xFFBDD5F0)
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
