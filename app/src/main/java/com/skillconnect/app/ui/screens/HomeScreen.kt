package com.skillconnect.app.ui.screens

import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
        // Banner Superior Vibrante Celeste Eléctrico
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFF0099FF), Color(0xFF0066FF))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("¡Hola, ${user?.name ?: "Usuario"}! 👋", fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                        Text("¿Qué quieres aprender hoy?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier.clickable { onNavigate("profile") }
                    ) {
                        NeumorphicLogo(
                            initials = user?.initials ?: "VR",
                            size = 50.dp,
                            backgroundColor = NeumorphicColors.accentYellow,
                            textColor = NeumorphicColors.text
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable { onNavigate("search") }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar", tint = NeumorphicColors.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Busca una habilidad o trueque, ej. Guitarra, Python...", color = NeumorphicColors.muted, fontSize = 14.sp)
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

            // Tarjeta Destacada de Trueque e Intercambio de Habilidades
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("exchange") },
                backgroundColor = Color(0xFFEFF6FF)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(NeumorphicColors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Trueque", tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trueque de Habilidades 🔄", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Enseña lo que sabes y aprende gratis lo que necesitas.", fontSize = 12.sp, color = NeumorphicColors.muted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Categorías", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
            Spacer(modifier = Modifier.height(12.dp))
            
            Column {
                val chunks = viewModel.categories.chunked(2)
                chunks.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        pair.forEach { category ->
                            NeumorphicCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .height(90.dp)
                                    .clickable { onNavigate("search") },
                                backgroundColor = Color.White
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(category.icon, fontSize = 22.sp, color = NeumorphicColors.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(category.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
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
                Text("Profesores recomendados", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
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
                Text("Intercambios recientes (Trueques)", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphicColors.primary,
                    modifier = Modifier.clickable { onNavigate("search") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

