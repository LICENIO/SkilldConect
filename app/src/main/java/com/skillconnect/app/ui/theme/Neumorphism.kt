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

// Paleta de colores Neumórfica
object NeumorphicColors {
    val bg = Color(0xFFECF0F3)
    val text = Color(0xFF2C3E50)
    val muted = Color(0xFF7F8C8D)
    val primary = Color(0xFF246BFE)
    val green = Color(0xFF19A974)
    val orange = Color(0xFFFF8A3D)
    val purple = Color(0xFF7C5CFF)
    val pink = Color(0xFFE05297)
    val teal = Color(0xFF00A3A3)
    
    // Sombras neumórficas
    val lightShadow = Color(0xFFFFFFFF)
    val darkShadow = Color(0xFFD1D9E6)
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
