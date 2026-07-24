package com.skillconnect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillconnect.app.ui.theme.NeumorphicColors
import com.skillconnect.app.ui.theme.neumorphic

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = Color.White,
    isSunken: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .neumorphic(cornerRadius = cornerRadius, isInnerShadow = isSunken, darkShadowColor = Color(0xFFD0D7E5))
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .padding(16.dp),
        content = content
    )
}

// Presets de gradientes de alto contraste inspirados en la imagen del usuario (Pill Buttons)
object ButtonGradients {
    // 1. "SHARE NOW" Style (Fucsia Magenta ➔ Naranja Carmesí ➔ Dorado Cálido) - ¡MÁXIMO DESTACA SOBRE AZUL!
    val SunsetGold = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFFEC4899), Color(0xFFFF5E00), Color(0xFFFFB800))
    )

    // 2. "PLAY NOW" Style (Fucsia Vívido ➔ Rojo Coral)
    val CoralRose = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFFF43F5E), Color(0xFFFF5252))
    )

    // 3. "SIGN UP" Style (Púrpura Violeta ➔ Azul Lavanda ➔ Celeste)
    val VioletCyan = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFFA855F7), Color(0xFF6366F1), Color(0xFF38BDF8))
    )

    // 4. "MORE INFO" Style (Azul Cobalto ➔ Celeste ➔ Menta Esmeralda)
    val BlueEmerald = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFF0284C7), Color(0xFF06B6D4), Color(0xFF10B981))
    )
}

@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 25.dp, // Forma de píldora (Pill shape) idéntica a la imagen
    backgroundColor: Color = NeumorphicColors.primary,
    gradientBrush: androidx.compose.ui.graphics.Brush? = ButtonGradients.SunsetGold,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundModifier = if (gradientBrush != null) {
        Modifier.background(gradientBrush, RoundedCornerShape(cornerRadius))
    } else {
        Modifier.background(backgroundColor, RoundedCornerShape(cornerRadius))
    }

    Box(
        modifier = modifier
            .neumorphic(cornerRadius = cornerRadius, isInnerShadow = isPressed, darkShadowColor = Color(0xFFC8D1E0))
            .then(backgroundModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 15.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
fun NeumorphicIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconColor: Color = Color.White,
    backgroundColor: Color = NeumorphicColors.primary,
    size: Dp = 44.dp,
    hasShadow: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowModifier = if (hasShadow) {
        Modifier.neumorphic(cornerRadius = size / 2, isInnerShadow = isPressed, darkShadowColor = Color(0xFFC8D1E0))
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .then(shadowModifier)
            .background(backgroundColor, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun NeumorphicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val heightModifier = if (singleLine) Modifier.height(54.dp) else Modifier.height(100.dp)
    
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = TextStyle(
            color = NeumorphicColors.text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        ),
        modifier = modifier
            .fillMaxWidth()
            .then(heightModifier)
            .neumorphic(isInnerShadow = true, cornerRadius = 16.dp, darkShadowColor = Color(0xFFC0C9D9))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = if (singleLine) 0.dp else 12.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = NeumorphicColors.muted,
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    trailingIcon()
                }
            }
        }
    )
}

@Composable
fun NeumorphicChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .neumorphic(cornerRadius = 18.dp, isInnerShadow = selected)
            .background(
                if (selected) NeumorphicColors.primary else Color.White,
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else NeumorphicColors.text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun NeumorphicProgressBar(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    barColor: Color = NeumorphicColors.accentYellow,
    height: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .neumorphic(isInnerShadow = true, cornerRadius = height / 2)
            .background(Color(0xFFE2E8F0), RoundedCornerShape(height / 2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(barColor, RoundedCornerShape(height / 2))
        )
    }
}

@Composable
fun NeumorphicLogo(
    initials: String,
    size: Dp = 64.dp,
    textColor: Color = Color.White,
    backgroundColor: Color = NeumorphicColors.primary,
    elevation: Dp = 4.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .neumorphic(cornerRadius = size / 2, offset = elevation / 2, shadowRadius = elevation)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = textColor,
            fontSize = (size.value / 3).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NeumorphicTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkHeader: Boolean = true
) {
    if (isDarkHeader) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFF0099FF), Color(0xFF0066FF))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NeumorphicIconButton(
                    icon = Icons.Default.ArrowBack,
                    onClick = onBack,
                    contentDescription = "Volver",
                    backgroundColor = Color.White.copy(alpha = 0.25f),
                    iconColor = Color.White,
                    size = 40.dp,
                    hasShadow = false
                )
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NeumorphicIconButton(
                icon = Icons.Default.ArrowBack,
                onClick = onBack,
                contentDescription = "Volver"
            )
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphicColors.text,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}
