package com.skillconnect.app.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.skillconnect.app.ui.components.*
import com.skillconnect.app.ui.theme.NeumorphicColors
import com.skillconnect.app.viewmodel.SkillConnectViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(viewModel: SkillConnectViewModel, onNavigate: (String) -> Unit) {
    val user = viewModel.currentCloudUser ?: com.skillconnect.app.data.repository.UserProfile(
        email = viewModel.currentUser?.email ?: "",
        name = viewModel.currentUser?.name ?: "Usuario SkillConnect",
        initials = viewModel.currentUser?.initials ?: "SC",
        role = viewModel.currentUser?.role ?: "Ambos"
    )

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Estados de edición
    var editName by remember { mutableStateOf(user.name) }
    var editRole by remember { mutableStateOf(user.role) }
    var editTeach by remember { mutableStateOf(user.teachSkills.joinToString(", ")) }
    var editAvail by remember { mutableStateOf(user.availability.joinToString(", ")) }
    var editRate by remember { mutableStateOf(user.hourlyRate.toString()) }
    var editDesc by remember { mutableStateOf(user.description) }

    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val scope = rememberCoroutineScope()

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

    val predefinedCategories = listOf("Programación", "Música", "Idiomas", "Arte", "Tecnología", "Marketing", "Otro")
    var expandedCategory by remember { mutableStateOf(false) }

    // Función para registrar/probar biometría
    fun triggerBiometricRegistration() {
        if (activity == null) {
            Toast.makeText(context, "Error: No se encontró la actividad para autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(context, "Tu dispositivo no tiene biometría configurada o no la soporta", Toast.LENGTH_LONG).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context, "¡Huella dactilar vinculada correctamente!", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Error biométrico: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Huella no reconocida. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Vincular Huella Digital")
            .setSubtitle("Escanea tu huella para habilitar el inicio de sesión rápido")
            .setNegativeButtonText("Cancelar")
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo iniciar el escáner biométrico", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ENCABEZADO DE PERFIL
        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                NeumorphicLogo(user.initials.ifEmpty { "SC" }, size = 96.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text(user.email, fontSize = 14.sp, color = NeumorphicColors.muted)

                Spacer(modifier = Modifier.height(10.dp))

                // Badge de Rol (Aprender, Enseñar, Ambos)
                val roleBadgeText = when (user.role) {
                    "Aprender" -> "📖 Miembro Estudiante"
                    "Enseñar" -> "👨‍🏫 Mentor / Profesor"
                    else -> "🔄 Aprende y Enseña"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeumorphicColors.primary.copy(alpha = 0.12f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = roleBadgeText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphicColors.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("★ ${user.rating}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                        Text("${user.reviewCount} reseñas", fontSize = 12.sp, color = NeumorphicColors.muted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(if (user.hourlyRate == 0) "Gratis" else "S/ ${user.hourlyRate}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                        Text("por hora", fontSize = 12.sp, color = NeumorphicColors.muted)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                NeumorphicButton(
                    onClick = {
                        editName = user.name
                        editRole = user.role.ifEmpty { "Ambos" }
                        editTeach = user.teachSkills.joinToString(", ")
                        editAvail = user.availability.joinToString(", ")
                        editRate = user.hourlyRate.toString()
                        editDesc = user.description
                        showEditDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Perfil y Rol", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // SECCIÓN: SOBRE MÍ
        Text("Sobre mí", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(6.dp))
        Text(user.description.ifEmpty { "Presiona 'Editar Perfil y Rol' para agregar una breve presentación sobre ti." }, fontSize = 14.sp, color = NeumorphicColors.muted)

        Spacer(modifier = Modifier.height(22.dp))

        // SECCIÓN: ESPECIALIDADES
        Text("Habilidades que Enseño", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val skills = user.teachSkills.ifEmpty { listOf("Sin habilidades especificadas") }
            items(skills) { skill ->
                NeumorphicChip(text = skill, selected = true, onClick = {})
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECCIÓN: ACCESO RÁPIDO A ACTIVIDADES
        Text("Mi Actividad y Aprendizaje", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NeumorphicCard(
                modifier = Modifier.weight(1f).clickable { onNavigate("skills") },
                backgroundColor = Color.White
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Icon(Icons.Default.School, contentDescription = null, tint = NeumorphicColors.primary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Mis Habilidades", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("${viewModel.skills.size} registradas", fontSize = 11.sp, color = NeumorphicColors.muted)
                }
            }
            NeumorphicCard(
                modifier = Modifier.weight(1f).clickable { onNavigate("learning") },
                backgroundColor = Color.White
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = NeumorphicColors.primary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Aprendiendo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("${viewModel.learning.size} temas en curso", fontSize = 11.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NeumorphicCard(
                modifier = Modifier.weight(1f).clickable { onNavigate("statistics") },
                backgroundColor = Color.White
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Icon(Icons.Default.BarChart, contentDescription = null, tint = NeumorphicColors.primary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Estadísticas", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("Ver progreso", fontSize = 11.sp, color = NeumorphicColors.muted)
                }
            }
            NeumorphicCard(
                modifier = Modifier.weight(1f).clickable { onNavigate("achievements") },
                backgroundColor = Color.White
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = NeumorphicColors.primary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Logros / Insignias", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("Insignias ganadas", fontSize = 11.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECCIÓN: CONFIGURACIÓN DE CUENTA Y SEGURIDAD
        Text("Configuración y Seguridad", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))

        // Centro de Notificaciones
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth().clickable { onNavigate("notifications") },
            backgroundColor = Color.White
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).background(NeumorphicColors.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = NeumorphicColors.primary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Centro de Notificaciones", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("Revisa tus alertas de mentorías e intercambios", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { 
                        notificationsEnabled = it
                        Toast.makeText(context, if (it) "Notificaciones activadas" else "Notificaciones silenciadas", Toast.LENGTH_SHORT).show()
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeumorphicColors.primary)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Autenticación Biométrica (Huella Digital)
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).background(Color(0xFFE0F2FE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFF0284C7))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Autenticación Biométrica", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("Vincular huella digital para acceso rápido", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
                Button(
                    onClick = { triggerBiometricRegistration() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Probar", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Eliminar Cuenta
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth().clickable { showDeleteDialog = true },
            backgroundColor = Color(0xFFFEF2F2)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).background(Color(0xFFFEE2E2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC2626))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Eliminar Cuenta", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    Text("Borrar permanentemente tu perfil y datos", fontSize = 12.sp, color = Color(0xFF991B1B))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFDC2626))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // BOTÓN CERRAR SESIÓN
        NeumorphicButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFEF4444)
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // DIÁLOGO: EDITAR PERFIL Y ROL
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Perfil y Rol", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("¿Cuál es tu rol principal?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Aprender", "Enseñar", "Ambos").forEach { roleOption ->
                            val selected = editRole == roleOption
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) NeumorphicColors.primary else Color(0xFFF3F4F6))
                                    .clickable { editRole = roleOption }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when(roleOption) {
                                        "Aprender" -> "📖 Aprender"
                                        "Enseñar" -> "👨‍🏫 Enseñar"
                                        else -> "🔄 Ambos"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else NeumorphicColors.text
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Habilidades que Enseñas (Categorías)", fontSize = 13.sp, color = NeumorphicColors.muted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                            .clickable { expandedCategory = true }
                            .padding(14.dp)
                    ) {
                        Text(if (editTeach.isNotBlank()) editTeach else "Seleccionar categoría...")
                        DropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            predefinedCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        editTeach = if (editTeach.isBlank()) cat else "$editTeach, $cat"
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Disponibilidad", fontSize = 13.sp, color = NeumorphicColors.muted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = editAvail,
                            onValueChange = { editAvail = it },
                            label = { Text("Ej. Lun 5pm, Sáb 10am") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { timePickerDialog.show() },
                            modifier = Modifier.background(NeumorphicColors.primary, CircleShape)
                        ) {
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
                        label = { Text("Tarifa por hora (S/ 0 para gratis)") }, 
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Sobre mí / Presentación") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = user.copy(
                            name = editName,
                            role = editRole,
                            teachSkills = editTeach.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            hourlyRate = editRate.toIntOrNull() ?: 0,
                            description = editDesc,
                            availability = editAvail.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        )
                        viewModel.saveProfile(updated)
                        Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeumorphicColors.primary)
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // DIÁLOGO: CONFIRMAR ELIMINAR CUENTA
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(36.dp)) },
            title = { Text("¿Eliminar cuenta permanentemente?", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)) },
            text = {
                Text(
                    text = "Esta acción eliminará irreversiblemente tu cuenta, perfil, solicitudes de trueque y datos asociados de SkillConnect.\n\n¿Estás seguro de que deseas continuar?",
                    fontSize = 14.sp,
                    color = NeumorphicColors.text
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount()
                        Toast.makeText(context, "Tu cuenta ha sido eliminada.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Sí, Eliminar Mi Cuenta", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = NeumorphicColors.muted)
                }
            }
        )
    }
}
