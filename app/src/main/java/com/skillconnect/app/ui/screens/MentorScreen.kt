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
fun MentorScreen(
    viewModel: SkillConnectViewModel,
    onBooking: () -> Unit,
    onExchange: () -> Unit,
    onBack: () -> Unit
) {
    val user = viewModel.cloudUsers.find { it.email == viewModel.selectedCloudUserEmail } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicTopBar("Perfil del experto", onBack)
        
        Spacer(modifier = Modifier.height(8.dp))

        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeumorphicColors.primary
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NeumorphicLogo(
                    initials = user.initials.ifEmpty { "VR" },
                    size = 86.dp,
                    textColor = NeumorphicColors.primary,
                    backgroundColor = Color.White
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(user.teachSkills.joinToString(", "), fontSize = 15.sp, color = Color.White.copy(alpha = 0.9f))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("★ ${user.rating}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("${user.reviewCount} reseñas", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(if (user.hourlyRate == 0) "Gratis" else "S/ ${user.hourlyRate}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("por hora", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text("Sobre el experto", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(8.dp))
        Text(user.description.ifEmpty { "Sin descripción." }, fontSize = 15.sp, color = NeumorphicColors.muted)

        Spacer(modifier = Modifier.height(22.dp))

        Text("Disponibilidad", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(user.availability) { time ->
                NeumorphicChip(text = time, selected = false, onClick = {})
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
        Text("Cursos", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(8.dp))
        if (user.courses.isEmpty()) {
            Text("No ofrece cursos informativos.", fontSize = 14.sp, color = NeumorphicColors.muted)
        } else {
            user.courses.forEach { course ->
                NeumorphicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Column {
                        Text(course.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NeumorphicColors.text)
                        Text(course.description, fontSize = 13.sp, color = NeumorphicColors.muted)
                        Text("Precio: S/ ${course.price}", fontSize = 13.sp, color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Reseñas
        Text("Reseñas recientes", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))
        val reviews = viewModel.selectedMentorReviews
        if (reviews.isEmpty()) {
            Text("Sin reseñas aún.", fontSize = 14.sp, color = NeumorphicColors.muted)
        } else {
            reviews.take(3).forEach { review ->
                NeumorphicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(review.reviewerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("★ ${review.rating}", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
                        }
                        Text(review.comment, fontSize = 13.sp, color = NeumorphicColors.muted)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            NeumorphicButton(
                onClick = onExchange,
                modifier = Modifier.weight(1f),
                backgroundColor = Color.White
            ) {
                Text("Proponer Trueque", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            NeumorphicButton(
                onClick = onBooking,
                modifier = Modifier.weight(1f)
            ) {
                Text("Reservar Clase", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

