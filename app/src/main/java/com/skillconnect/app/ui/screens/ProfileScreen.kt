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
fun ProfileScreen(viewModel: SkillConnectViewModel, onNavigate: (String) -> Unit) {
    val user = viewModel.currentCloudUser ?: com.skillconnect.app.data.repository.UserProfile(
        email = viewModel.currentUser?.email ?: "",
        name = viewModel.currentUser?.name ?: "Cargando...",
        initials = viewModel.currentUser?.initials ?: ""
    )
    var showEditDialog by remember { mutableStateOf(false) }
    var showCourseDialog by remember { mutableStateOf(false) }
    var editingCourse by remember { mutableStateOf<CloudCourse?>(null) }
    
    // Edit states
    var editName by remember { mutableStateOf(user.name) }
    var editTeach by remember { mutableStateOf(user.teachSkills.joinToString(", ")) }
    var editAvail by remember { mutableStateOf(user.availability.joinToString(", ")) }
    var editRate by remember { mutableStateOf(user.hourlyRate.toString()) }
    var editDesc by remember { mutableStateOf(user.description) }
    var courseTitle by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    var coursePrice by remember { mutableStateOf("") }
    var courseSyllabus by remember { mutableStateOf("") }

    fun openCourseDialog(course: CloudCourse? = null) {
        editingCourse = course
        courseTitle = course?.title.orEmpty()
        courseDescription = course?.description.orEmpty()
        coursePrice = course?.price?.takeIf { it > 0 }?.toString().orEmpty()
        courseSyllabus = course?.syllabus?.joinToString("\n").orEmpty()
        showCourseDialog = true
    }

    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = calendar.get(java.util.Calendar.MINUTE)

    val timePickerDialog = android.app.TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val amPm = if (selectedHour >= 12) "PM" else "AM"
            val hr12 = if (selectedHour % 12 == 0) 12 else selectedHour % 12
            val timeStr = String.format("%02d:%02d %s", hr12, selectedMinute, amPm)
            if (editAvail.isBlank()) editAvail = timeStr
            else editAvail += ", $timeStr"
        }, hour, minute, false
    )

    // Categoria Selector
    val predefinedCategories = listOf("Música", "Idiomas", "Tecnología", "Marketing", "Otro")
    var expandedCategory by remember { mutableStateOf(false) }
    var customCategory by remember { mutableStateOf("") }
    var isCustomCategory by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado con imagen
        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                NeumorphicLogo(user.initials.ifEmpty { "VR" }, size = 96.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(user.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text(user.email, fontSize = 15.sp, color = NeumorphicColors.muted)
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("★ ${user.rating}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                        Text("${user.reviewCount} reseñas", fontSize = 13.sp, color = NeumorphicColors.muted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(if (user.hourlyRate == 0) "Gratis" else "S/${user.hourlyRate}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                        Text("por hora", fontSize = 13.sp, color = NeumorphicColors.muted)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                
                NeumorphicButton(onClick = {
                    editName = user.name
                    editTeach = user.teachSkills.joinToString(", ")
                    editAvail = user.availability.joinToString(", ")
                    editRate = user.hourlyRate.toString()
                    editDesc = user.description
                    isCustomCategory = false
                    customCategory = ""
                    showEditDialog = true
                }, modifier = Modifier.fillMaxWidth(0.7f)) {
                    Text("Editar Perfil Profesional", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Sobre mí", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(8.dp))
        Text(user.description.ifEmpty { "Aún no has agregado una descripción." }, fontSize = 15.sp, color = NeumorphicColors.muted)

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Especialidades / Categorías", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val skills = user.teachSkills.ifEmpty { listOf("Ninguna") }
            items(skills) {
                NeumorphicChip(text = it, selected = true, onClick = {})
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Disponibilidad", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val avails = user.availability.ifEmpty { listOf("Por coordinar") }
            items(avails) {
                NeumorphicChip(text = it, selected = false, onClick = {})
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Mis Cursos Informativos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))
        if (user.courses.isEmpty()) {
            Text("No has agregado cursos todavía.", fontSize = 15.sp, color = NeumorphicColors.muted)
        } else {
            user.courses.forEach { course ->
                NeumorphicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column {
                        Text(course.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeumorphicColors.text)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(course.description, fontSize = 14.sp, color = NeumorphicColors.muted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Precio: S/ ${course.price}", fontSize = 14.sp, color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(34.dp))
        NeumorphicButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFF44336)
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Perfil Profesional", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text("Categoría", fontSize = 14.sp, color = NeumorphicColors.muted)
                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)).clickable { expandedCategory = true }.padding(16.dp)) {
                        Text(if (isCustomCategory) "Otro" else editTeach.ifBlank { "Seleccionar..." })
                        androidx.compose.material3.DropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                            predefinedCategories.forEach { cat ->
                                androidx.compose.material3.DropdownMenuItem(text = { Text(cat) }, onClick = {
                                    if (cat == "Otro") {
                                        isCustomCategory = true
                                        editTeach = ""
                                    } else {
                                        isCustomCategory = false
                                        editTeach = cat
                                    }
                                    expandedCategory = false
                                })
                            }
                        }
                    }
                    if (isCustomCategory) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = customCategory, onValueChange = { customCategory = it; editTeach = it }, label = { Text("Escribe tu categoría") }, modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Disponibilidad", fontSize = 14.sp, color = NeumorphicColors.muted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = editAvail, onValueChange = { editAvail = it }, label = { Text("Ej. Lun 5pm") }, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.IconButton(onClick = { timePickerDialog.show() }, modifier = Modifier.background(NeumorphicColors.primary, CircleShape)) {
                            Icon(Icons.Default.Schedule, "Reloj", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editRate,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                editRate = it 
                            }
                        }, 
                        label = { Text("Tarifa por hora (S/)") }, 
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = editDesc, onValueChange = { editDesc = it }, label = { Text("Sobre mí") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    val updated = user.copy(
                        name = editName,
                        teachSkills = editTeach.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        hourlyRate = editRate.toIntOrNull() ?: 0,
                        description = editDesc,
                        availability = editAvail.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    )
                    viewModel.saveProfile(updated)
                    showEditDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

