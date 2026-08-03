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
fun SkillsScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var newSkillName by remember { mutableStateOf("") }
    var newSkillLevel by remember { mutableStateOf("Intermedio") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeumorphicColors.bg)
                .padding(20.dp)
        ) {
            NeumorphicTopBar("Mis habilidades", onBack)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.skills) { skill ->
                    NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(skill.name.take(2).uppercase(), size = 46.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(skill.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Text(skill.level, fontSize = 13.sp, color = NeumorphicColors.muted)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            NeumorphicButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar habilidad", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
            }
        }
        
        if (showAddDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(0.85f).padding(16.dp),
                    cornerRadius = 24.dp
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Agregar Habilidad", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeumorphicColors.text)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        NeumorphicTextField(
                            value = newSkillName,
                            onValueChange = { newSkillName = it },
                            placeholder = "Nombre de la habilidad (ej. Piano)"
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("Principiante", "Intermedio", "Avanzado").forEach { level ->
                                NeumorphicChip(
                                    text = level,
                                    selected = newSkillLevel == level,
                                    onClick = { newSkillLevel = level }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            NeumorphicButton(
                                onClick = {
                                    showAddDialog = false
                                    newSkillName = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar", color = NeumorphicColors.muted, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            NeumorphicButton(
                                onClick = {
                                    if (newSkillName.isNotBlank()) {
                                        viewModel.addSkill(newSkillName, newSkillLevel)
                                        showAddDialog = false
                                        newSkillName = ""
                                        Toast.makeText(context, "Habilidad guardada en base de datos", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Por favor escribe un nombre", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Guardar", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LearningScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Mis aprendizajes", onBack)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.learning) { item ->
                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(item.name.take(2).uppercase(), size = 46.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Text("${item.teacher} - ${item.progress}%", fontSize = 13.sp, color = NeumorphicColors.muted)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        NeumorphicProgressBar(progress = item.progress / 100f)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    var darkMode by remember { mutableStateOf(false) }
    var notificationState by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Configuración", onBack)

        Spacer(modifier = Modifier.height(10.dp))

        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo oscuro", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Switch(checked = darkMode, onCheckedChange = { 
                    darkMode = it 
                    NeumorphicColors.setDarkMode(it)
                })
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notificaciones", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Switch(checked = notificationState, onCheckedChange = { notificationState = it })
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Idioma", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("Español", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = NeumorphicColors.primary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.logout() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Cerrar sesión", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    Text("Salir de tu cuenta actual de forma segura", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.Red)
            }
        }
    }
}

@Composable
fun StatisticsScreen(onBack: () -> Unit) {
    val weeklyProgress = listOf(0.70f, 0.45f, 0.88f, 0.54f, 0.78f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicTopBar("Estadísticas", onBack)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("86h", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("Horas enseñadas", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("54h", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("Horas aprendidas", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("18", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("Intercambios", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("4.9", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("Calificación", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Actividad semanal", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))

        weeklyProgress.forEachIndexed { idx, progress ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Día ${idx + 1}", fontSize = 13.sp, color = NeumorphicColors.muted, modifier = Modifier.width(56.dp))
                NeumorphicProgressBar(progress = progress, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AchievementsScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Logros", onBack)

        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFEAF8F2)
        ) {
            Column {
                Text("9/14 desbloqueados", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text("Sigue aprendiendo y enseñando para completar tus insignias.", fontSize = 13.sp, color = NeumorphicColors.muted)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.achievements) { ach ->
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (ach.unlocked) Color.White else Color(0xFFF3F4F6)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (ach.unlocked) NeumorphicColors.primary.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (ach.unlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (ach.unlocked) NeumorphicColors.primary else NeumorphicColors.muted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(ach.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text(ach.description, fontSize = 13.sp, color = NeumorphicColors.muted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Notificaciones", onBack)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.notifications) { item ->
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (item.unread) Color(0xFFEEF5FF) else Color.White
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (item.unread) NeumorphicColors.primary.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.unread) Icons.Default.NotificationImportant else Icons.Default.Notifications,
                                contentDescription = null,
                                tint = if (item.unread) NeumorphicColors.primary else NeumorphicColors.muted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text(item.description, fontSize = 13.sp, color = NeumorphicColors.muted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(item.time, fontSize = 11.sp, color = NeumorphicColors.muted, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

