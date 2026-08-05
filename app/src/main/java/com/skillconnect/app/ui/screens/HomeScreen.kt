package com.skillconnect.app.ui.screens

import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.skillconnect.app.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.skillconnect.app.data.model.Mentor
import com.skillconnect.app.data.repository.CloudCourse
import com.skillconnect.app.ui.components.*
import com.skillconnect.app.ui.theme.NeumorphicColors
import com.skillconnect.app.ui.theme.neumorphic
import com.skillconnect.app.viewmodel.SkillConnectViewModel
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(viewModel: SkillConnectViewModel, onNavigate: (String) -> Unit) {
    val user = viewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
    ) {
        // Banner Superior — Gradiente premium Azul Índigo → Violeta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ButtonGradients.PrimaryBlueViolet)
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "¡Hola, ${user?.name ?: "Usuario"}!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.80f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "¿Qué quieres aprender hoy?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    val myEmail = viewModel.activeUserEmail ?: ""
                    val pendingCount = viewModel.cloudRequests.count {
                        it.recipientEmail.equals(myEmail, ignoreCase = true) && it.status == "PENDIENTE"
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Botón de notificaciones con micro-animación
                        val notifInteraction = remember { MutableInteractionSource() }
                        val notifPressed by notifInteraction.collectIsPressedAsState()
                        val notifScale by animateFloatAsState(
                            targetValue = if (notifPressed) 0.88f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMedium),
                            label = "notif_scale"
                        )
                        Box(
                            modifier = Modifier
                                .scale(notifScale)
                                .clickable(
                                    interactionSource = notifInteraction,
                                    indication = null
                                ) { onNavigate("notifications") }
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            if (pendingCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Color(0xFFFF4757), CircleShape)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$pendingCount",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier.clickable { onNavigate("profile") }
                        ) {
                            NeumorphicLogo(
                                initials = user?.initials ?: "VR",
                                size = 46.dp,
                                backgroundColor = Color.White.copy(alpha = 0.25f),
                                textColor = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Barra de búsqueda con glassmorphism sutil
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .clickable { onNavigate("search") }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar", tint = NeumorphicColors.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Busca habilidades o trueques...",
                        color = NeumorphicColors.muted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Tarjeta Destacada de Trueque — con gradiente y micro-animación
            val truequeInteraction = remember { MutableInteractionSource() }
            val truequePressed by truequeInteraction.collectIsPressedAsState()
            val truequeScale by animateFloatAsState(
                targetValue = if (truequePressed) 0.97f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "trueque_scale"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(truequeScale)
                    .neumorphic(cornerRadius = 18.dp)
                    .background(ButtonGradients.PrimaryBlueViolet, RoundedCornerShape(18.dp))
                    .clickable(
                        interactionSource = truequeInteraction,
                        indication = null
                    ) { onNavigate("exchange") }
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White.copy(alpha = 0.20f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Trueque",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Trueque de Habilidades",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            "Enseña lo que sabes y aprende gratis.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.80f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.70f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Categorías",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeumorphicColors.text,
                letterSpacing = (-0.3).sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Column {
                val chunks = viewModel.categories.chunked(2)
                chunks.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        pair.forEach { category ->
                            val catInteraction = remember { MutableInteractionSource() }
                            val catPressed by catInteraction.collectIsPressedAsState()
                            val catScale by animateFloatAsState(
                                targetValue = if (catPressed) 0.94f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "cat_scale"
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .height(92.dp)
                                    .scale(catScale)
                                    .neumorphic(cornerRadius = 18.dp)
                                    .background(NeumorphicColors.surface, RoundedCornerShape(18.dp))
                                    .clickable(
                                        interactionSource = catInteraction,
                                        indication = null
                                    ) { onNavigate("search") }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    val categoryIcon = when (category.name) {
                                        "Programación" -> Icons.Default.Code
                                        "Idiomas" -> Icons.Default.Language
                                        "Arte" -> Icons.Default.Palette
                                        "Música" -> Icons.Default.MusicNote
                                        "Matemáticas" -> Icons.Default.Calculate
                                        "Cocina" -> Icons.Default.Restaurant
                                        "Tecnología" -> Icons.Default.Computer
                                        "Marketing" -> Icons.Default.TrendingUp
                                        else -> Icons.Default.Category
                                    }
                                    Icon(
                                        imageVector = categoryIcon,
                                        contentDescription = category.name,
                                        tint = NeumorphicColors.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        category.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeumorphicColors.text,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Profesores recomendados",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeumorphicColors.text,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeumorphicColors.primary,
                    modifier = Modifier.clickable { onNavigate("search") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val topMentors = viewModel.cloudUsers.sortedByDescending { it.rating }.take(4)
                items(topMentors) { user ->
                    NeumorphicCard(
                        modifier = Modifier
                            .width(200.dp)
                            .clickable {
                                viewModel.selectedCloudUserEmail = user.email
                                viewModel.fetchReviewsForMentor(user.email)
                                onNavigate("mentor")
                            },
                        backgroundColor = Color.White
                    ) {
                        Column {
                            NeumorphicLogo(
                                initials = user.initials,
                                backgroundColor = NeumorphicColors.primary,
                                size = 50.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(user.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text(user.teachSkills.joinToString(", "), fontSize = 12.sp, color = NeumorphicColors.muted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "★ ${user.rating} (${user.reviewCount}) · ${if (user.hourlyRate == 0) "Gratis" else "S/ ${user.hourlyRate}"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeumorphicColors.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Intercambios recientes",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeumorphicColors.text,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeumorphicColors.primary,
                    modifier = Modifier.clickable { onNavigate("search") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (viewModel.cloudExchanges.isEmpty()) {
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onNavigate("exchange") },
                    backgroundColor = Color.White
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFEFF6FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = NeumorphicColors.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sin publicaciones de trueque aún",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeumorphicColors.text
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Toca aquí para proponer tu primer trueque de habilidades.",
                                fontSize = 12.sp,
                                color = NeumorphicColors.muted
                            )
                        }
                    }
                }
            } else {
                viewModel.cloudExchanges.forEach { exchange ->
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        backgroundColor = Color.White
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(exchange.initials, size = 44.dp, backgroundColor = NeumorphicColors.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(exchange.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Text(exchange.subtitle, fontSize = 12.sp, color = NeumorphicColors.muted)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

