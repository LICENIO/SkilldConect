package com.skillconnect.app.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.skillconnect.app.data.repository.UserProfile
import com.skillconnect.app.ui.components.*
import com.skillconnect.app.ui.theme.NeumorphicColors
import com.skillconnect.app.viewmodel.SkillConnectViewModel

// ─────────────────────────────────────────────────────────────────────────────
// PANTALLA PRINCIPAL DEL PERFIL
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ProfileScreen(viewModel: SkillConnectViewModel, onNavigate: (String) -> Unit) {
    val user = viewModel.currentCloudUser ?: UserProfile(
        email = viewModel.currentUser?.email ?: "",
        name  = viewModel.currentUser?.name  ?: "Usuario SkillConnect",
        initials = viewModel.currentUser?.initials ?: "SC",
        role  = viewModel.currentUser?.role  ?: "Ambos"
    )

    var showEditDialog   by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val context  = LocalContext.current
    val activity = context as? FragmentActivity

    // ── Biometría ────────────────────────────────────────────────────────────
    fun triggerBiometric() {
        if (activity == null) { Toast.makeText(context, "Actividad no disponible", Toast.LENGTH_SHORT).show(); return }
        val bm = BiometricManager.from(context)
        if (bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK) != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(context, "No hay biometría configurada en este dispositivo", Toast.LENGTH_LONG).show(); return
        }
        val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(r: BiometricPrompt.AuthenticationResult) {
                    Toast.makeText(context, "✅ Huella dactilar vinculada", Toast.LENGTH_SHORT).show()
                }
                override fun onAuthenticationError(code: Int, msg: CharSequence) {
                    Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                }
                override fun onAuthenticationFailed() {
                    Toast.makeText(context, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                }
            })
        try {
            prompt.authenticate(BiometricPrompt.PromptInfo.Builder()
                .setTitle("Vincular Huella Digital")
                .setSubtitle("Escanea tu huella para inicio rápido")
                .setNegativeButtonText("Cancelar")
                .build())
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir biometría", Toast.LENGTH_SHORT).show()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .verticalScroll(rememberScrollState())
    ) {
        // ══ HEADER CON GRADIENTE ════════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0055CC), Color(0xFF0099FF), NeumorphicColors.bg)
                    )
                )
                .padding(bottom = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circular con borde brillante
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFF8C00))),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(NeumorphicColors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.initials.ifEmpty { "SC" },
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    user.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    user.email,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )

                Spacer(Modifier.height(10.dp))

                // Badge de Rol
                val (roleText, roleColor) = when (user.role) {
                    "Aprender" -> Pair("📖 Estudiante", Color(0xFF10B981))
                    "Enseñar"  -> Pair("👨‍🏫 Mentor",    Color(0xFFF59E0B))
                    else       -> Pair("🔄 Aprende & Enseña", Color(0xFF8B5CF6))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(roleColor)
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text(roleText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(Modifier.height(18.dp))

                // ─ Estadísticas rápidas ──────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatItem("★ ${user.rating}", "${user.reviewCount} reseñas")
                    ProfileStatDivider()
                    ProfileStatItem(
                        if (user.hourlyRate == 0) "Gratis" else "S/ ${user.hourlyRate}",
                        "por hora"
                    )
                    ProfileStatDivider()
                    ProfileStatItem(
                        "${user.teachSkills.size}",
                        "habilidades"
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Botón Editar Perfil
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable { showEditDialog = true }
                        .padding(horizontal = 28.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, null, tint = NeumorphicColors.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Editar Perfil Profesional", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(4.dp))
            }
        }

        // ══ CUERPO ══════════════════════════════════════════════════════════
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Spacer(Modifier.height(10.dp))

            // ─ Sobre mí ───────────────────────────────────────────────────
            ProfileSectionTitle("Sobre mí")
            Spacer(Modifier.height(6.dp))
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = user.description.ifEmpty { "Agrega una presentación sobre ti para que los demás te conozcan mejor." },
                    fontSize = 14.sp,
                    color = if (user.description.isNotEmpty()) NeumorphicColors.text else NeumorphicColors.muted,
                    lineHeight = 22.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            // ─ Habilidades que enseña ────────────────────────────────────
            if (user.teachSkills.isNotEmpty()) {
                ProfileSectionTitle("Habilidades que Enseño")
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(user.teachSkills) { skill ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFF0055CC), Color(0xFF0099FF)))
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(skill, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ─ Disponibilidad ─────────────────────────────────────────────
            if (user.availability.isNotEmpty() && user.availability != listOf("Por coordinar")) {
                ProfileSectionTitle("Disponibilidad Horaria")
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(user.availability) { slot ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0FFF4))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(slot, color = Color(0xFF065F46), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ─ Acceso Rápido ──────────────────────────────────────────────
            ProfileSectionTitle("Mi Actividad")
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileQuickCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.School,
                    iconColor = Color(0xFF0088FF),
                    iconBg   = Color(0xFFE0F0FF),
                    title    = "Mis Habilidades",
                    subtitle = "${viewModel.skills.size} registradas",
                    onClick  = { onNavigate("skills") }
                )
                ProfileQuickCard(
                    modifier = Modifier.weight(1f),
                    icon    = Icons.Default.Book,
                    iconColor = Color(0xFF8B5CF6),
                    iconBg   = Color(0xFFF5F3FF),
                    title   = "Aprendiendo",
                    subtitle = "${viewModel.learning.size} en curso",
                    onClick = { onNavigate("learning") }
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileQuickCard(
                    modifier = Modifier.weight(1f),
                    icon    = Icons.Default.BarChart,
                    iconColor = Color(0xFF10B981),
                    iconBg   = Color(0xFFECFDF5),
                    title   = "Estadísticas",
                    subtitle = "Ver progreso",
                    onClick = { onNavigate("statistics") }
                )
                ProfileQuickCard(
                    modifier = Modifier.weight(1f),
                    icon    = Icons.Default.EmojiEvents,
                    iconColor = Color(0xFFF59E0B),
                    iconBg   = Color(0xFFFFFBEB),
                    title   = "Logros",
                    subtitle = "Mis insignias",
                    onClick = { onNavigate("achievements") }
                )
            }

            Spacer(Modifier.height(24.dp))

            // ─ Configuración y Seguridad ──────────────────────────────────
            ProfileSectionTitle("Configuración y Seguridad")
            Spacer(Modifier.height(10.dp))

            // Notificaciones
            NeumorphicCard(modifier = Modifier.fillMaxWidth().clickable { onNavigate("notifications") }) {
                ProfileSettingRow(
                    icon = Icons.Default.Notifications,
                    iconColor = NeumorphicColors.primary,
                    iconBg = NeumorphicColors.primary.copy(alpha = 0.1f),
                    title = "Centro de Notificaciones",
                    subtitle = "Alertas de mentorías e intercambios"
                ) {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            notificationsEnabled = it
                            Toast.makeText(context, if (it) "Notificaciones activadas" else "Silenciadas", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeumorphicColors.primary)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Biometría
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                ProfileSettingRow(
                    icon = Icons.Default.Fingerprint,
                    iconColor = Color(0xFF0284C7),
                    iconBg = Color(0xFFE0F2FE),
                    title = "Autenticación Biométrica",
                    subtitle = "Inicio de sesión rápido con huella"
                ) {
                    Button(
                        onClick = { triggerBiometric() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Activar", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Configuración General
            NeumorphicCard(modifier = Modifier.fillMaxWidth().clickable { onNavigate("settings") }) {
                ProfileSettingRow(
                    icon = Icons.Default.Settings,
                    iconColor = Color(0xFF64748B),
                    iconBg = Color(0xFFF1F5F9),
                    title = "Configuración General",
                    subtitle = "Idioma, notificaciones, modo oscuro"
                ) {
                    Icon(Icons.Default.ChevronRight, null, tint = NeumorphicColors.muted)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Eliminar cuenta
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth().clickable { showDeleteDialog = true },
                backgroundColor = Color(0xFFFFF1F2)
            ) {
                ProfileSettingRow(
                    icon = Icons.Default.DeleteForever,
                    iconColor = Color(0xFFDC2626),
                    iconBg = Color(0xFFFEE2E2),
                    title = "Eliminar Cuenta",
                    subtitle = "Borrar permanentemente tu perfil"
                ) {
                    Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFDC2626))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Cerrar Sesión
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFDC2626), Color(0xFFEF4444)))
                    )
                    .clickable { viewModel.logout() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    // ══ DIÁLOGO: EDITAR PERFIL ══════════════════════════════════════════════
    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.saveProfile(updated)
                Toast.makeText(context, "✅ Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                showEditDialog = false
            }
        )
    }

    // ══ DIÁLOGO: CONFIRMAR ELIMINAR ═════════════════════════════════════════
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(Icons.Default.Warning, null, tint = Color(0xFFDC2626), modifier = Modifier.size(40.dp))
            },
            title = {
                Text("¿Eliminar cuenta?", fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626), textAlign = TextAlign.Center)
            },
            text = {
                Text(
                    "Esta acción eliminará permanentemente tu cuenta, perfil, intercambios y todos tus datos de SkillConnect.\n\n¿Estás seguro?",
                    fontSize = 14.sp, color = NeumorphicColors.text, textAlign = TextAlign.Center
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

// ─────────────────────────────────────────────────────────────────────────────
// DIÁLOGO DE EDICIÓN PROFESIONAL (Pantalla Completa)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EditProfileDialog(
    user: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    // Estados del formulario
    var editName  by remember { mutableStateOf(user.name) }
    var editRole  by remember { mutableStateOf(user.role.ifEmpty { "Ambos" }) }
    var editDesc  by remember { mutableStateOf(user.description) }
    var editRate  by remember { mutableStateOf(if (user.hourlyRate == 0) "" else user.hourlyRate.toString()) }

    // Categorías seleccionadas (multi-select)
    val allCategories = listOf(
        "🎵 Música", "💻 Programación", "🌍 Idiomas", "🎨 Arte y Diseño",
        "📐 Matemáticas", "📸 Fotografía", "🍳 Cocina", "🏋️ Deporte y Salud",
        "📈 Marketing", "💼 Negocios", "🔧 Electrónica", "✍️ Escritura"
    )
    val selectedCategories = remember {
        mutableStateListOf<String>().also { list ->
            user.teachSkills.forEach { skill ->
                val match = allCategories.firstOrNull { it.contains(skill, ignoreCase = true) || skill.contains(it.substringAfter(" "), ignoreCase = true) }
                if (match != null) list.add(match) else if (skill.isNotBlank()) list.add(skill)
            }
        }
    }
    var customCategory by remember { mutableStateOf("") }
    var showCustomCategoryField by remember { mutableStateOf(false) }

    // Disponibilidad horaria
    val context = LocalContext.current
    val availabilitySlots = remember {
        mutableStateListOf<String>().also { list ->
            user.availability.filter { it != "Por coordinar" }.forEach { list.add(it) }
        }
    }

    val calendar = remember { java.util.Calendar.getInstance() }
    val timePickerDialog = remember {
        android.app.TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val days = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val hr12 = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                val timeStr = String.format("%02d:%02d %s", hr12, selectedMinute, amPm)
                if (!availabilitySlots.contains(timeStr)) availabilitySlots.add(timeStr)
            },
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE),
            false
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeumorphicColors.bg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ─ Header del Diálogo ─────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF0055CC), Color(0xFF0099FF)))
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                        Text(
                            "Editar Perfil Profesional",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(onClick = {
                            val rawCategories = selectedCategories.map { cat ->
                                cat.substringAfter(" ").trim()
                            }.filter { it.isNotBlank() }
                            val finalAvail = availabilitySlots.toList().ifEmpty { listOf("Por coordinar") }
                            onSave(
                                user.copy(
                                    name        = editName.trim(),
                                    role        = editRole,
                                    description = editDesc.trim(),
                                    hourlyRate  = editRate.toIntOrNull() ?: 0,
                                    teachSkills = rawCategories,
                                    availability = finalAvail
                                )
                            )
                        }) {
                            Icon(Icons.Default.Check, null, tint = Color.White)
                        }
                    }
                }

                // ─ Cuerpo scrollable ─────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(Modifier.height(20.dp))

                    // ── 1. Nombre ─────────────────────────────────────────
                    EditSectionHeader(icon = Icons.Default.Person, title = "Nombre Completo")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Tu nombre público") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = NeumorphicColors.primary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeumorphicColors.primary,
                            focusedLabelColor  = NeumorphicColors.primary
                        )
                    )

                    Spacer(Modifier.height(22.dp))

                    // ── 2. Rol ────────────────────────────────────────────
                    EditSectionHeader(icon = Icons.Default.SwapHoriz, title = "¿Cuál es tu rol?")
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            Triple("Aprender", "📖", Color(0xFF10B981)),
                            Triple("Enseñar",  "👨‍🏫", Color(0xFFF59E0B)),
                            Triple("Ambos",    "🔄", Color(0xFF8B5CF6))
                        ).forEach { (role, emoji, color) ->
                            val selected = editRole == role
                            val bgColor by animateColorAsState(
                                if (selected) color else NeumorphicColors.surface,
                                animationSpec = tween(200)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        width = 2.dp,
                                        color  = if (selected) color else NeumorphicColors.darkShadow,
                                        shape  = RoundedCornerShape(14.dp)
                                    )
                                    .background(bgColor)
                                    .clickable { editRole = role }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(emoji, fontSize = 20.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        role,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else NeumorphicColors.text
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    // ── 3. Categorías (chips multi-select) ────────────────
                    EditSectionHeader(icon = Icons.Default.Category, title = "Habilidades que Enseñas")
                    Spacer(Modifier.height(4.dp))
                    Text("Selecciona todas las categorías que apliquen", fontSize = 12.sp, color = NeumorphicColors.muted)
                    Spacer(Modifier.height(10.dp))

                    // Chips en grid (2 columnas simulado con rows)
                    val chunked = allCategories.chunked(2)
                    chunked.forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { cat ->
                                val isSelected = selectedCategories.contains(cat)
                                val chipBg by animateColorAsState(
                                    if (isSelected) NeumorphicColors.primary else NeumorphicColors.surface,
                                    animationSpec = tween(150)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            1.dp,
                                            if (isSelected) NeumorphicColors.primary else NeumorphicColors.darkShadow,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .background(chipBg)
                                        .clickable {
                                            if (isSelected) selectedCategories.remove(cat)
                                            else selectedCategories.add(cat)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                        }
                                        Text(
                                            cat,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else NeumorphicColors.text,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            // Si par tiene 1 solo elemento, rellenar espacio
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }

                    // Opción "Otro" para agregar categoría personalizada
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (showCustomCategoryField) NeumorphicColors.primary else NeumorphicColors.darkShadow,
                                RoundedCornerShape(12.dp)
                            )
                            .background(if (showCustomCategoryField) NeumorphicColors.primary.copy(alpha = 0.08f) else NeumorphicColors.surface)
                            .clickable { showCustomCategoryField = !showCustomCategoryField }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (showCustomCategoryField) Icons.Default.Remove else Icons.Default.Add,
                            null,
                            tint = NeumorphicColors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Agregar otra categoría personalizada",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeumorphicColors.primary
                        )
                    }

                    if (showCustomCategoryField) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customCategory,
                                onValueChange = { customCategory = it },
                                label = { Text("Ej. Ajedrez, Robótica...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeumorphicColors.primary,
                                    focusedLabelColor  = NeumorphicColors.primary
                                )
                            )
                            IconButton(
                                onClick = {
                                    val trimmed = customCategory.trim()
                                    if (trimmed.isNotBlank() && !selectedCategories.contains(trimmed)) {
                                        selectedCategories.add(trimmed)
                                        customCategory = ""
                                        showCustomCategoryField = false
                                    }
                                },
                                modifier = Modifier
                                    .background(NeumorphicColors.primary, CircleShape)
                                    .size(48.dp)
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                            }
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    // ── 4. Disponibilidad Horaria ─────────────────────────
                    EditSectionHeader(icon = Icons.Default.Schedule, title = "Disponibilidad Horaria")
                    Spacer(Modifier.height(4.dp))
                    Text("Toca el botón para añadir horarios de disponibilidad", fontSize = 12.sp, color = NeumorphicColors.muted)
                    Spacer(Modifier.height(10.dp))

                    // Horarios ya agregados
                    if (availabilitySlots.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            availabilitySlots.toList().forEach { slot ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF0FFF4))
                                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccessTime, null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Text(slot, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF065F46))
                                    }
                                    IconButton(
                                        onClick = { availabilitySlots.remove(slot) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    // Botón agregar horario
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF10B981), Color(0xFF059669))
                                )
                            )
                            .clickable { timePickerDialog.show() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddAlarm, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Añadir Horario Disponible", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    // ── 5. Tarifa por hora (solo números) ─────────────────
                    EditSectionHeader(icon = Icons.Default.AttachMoney, title = "Tarifa por Hora")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editRate,
                        onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) editRate = it },
                        label = { Text("Tarifa en S/ (0 para servicio gratuito)") },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeumorphicColors.primary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("S/", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeumorphicColors.primary,
                            focusedLabelColor  = NeumorphicColors.primary
                        ),
                        supportingText = {
                            if (editRate.isEmpty() || editRate == "0") {
                                Text("⭐ Servicio gratuito / Intercambio de habilidades", color = Color(0xFF10B981), fontSize = 11.sp)
                            } else {
                                Text("💰 S/ $editRate por hora de clase", color = NeumorphicColors.primary, fontSize = 11.sp)
                            }
                        }
                    )

                    Spacer(Modifier.height(22.dp))

                    // ── 6. Descripción / Presentación ─────────────────────
                    EditSectionHeader(icon = Icons.Default.Notes, title = "Sobre mí / Presentación")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Cuéntanos quién eres y qué ofreces") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        minLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeumorphicColors.primary,
                            focusedLabelColor  = NeumorphicColors.primary
                        ),
                        supportingText = {
                            Text("${editDesc.length} caracteres", color = NeumorphicColors.muted, fontSize = 11.sp)
                        }
                    )

                    Spacer(Modifier.height(28.dp))

                    // Botón Guardar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0055CC), Color(0xFF0099FF))
                                )
                            )
                            .clickable {
                                val rawCategories = selectedCategories.map { cat ->
                                    if (cat.contains(" ")) cat.substringAfter(" ").trim() else cat
                                }.filter { it.isNotBlank() }
                                val finalAvail = availabilitySlots.toList().ifEmpty { listOf("Por coordinar") }
                                onSave(
                                    user.copy(
                                        name         = editName.trim(),
                                        role         = editRole,
                                        description  = editDesc.trim(),
                                        hourlyRate   = editRate.toIntOrNull() ?: 0,
                                        teachSkills  = rawCategories,
                                        availability = finalAvail
                                    )
                                )
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Guardar Perfil", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTES AUXILIARES PRIVADOS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
    }
}

@Composable
private fun ProfileStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = NeumorphicColors.text)
}

@Composable
private fun ProfileQuickCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    NeumorphicCard(modifier = modifier.clickable(onClick = onClick)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text, textAlign = TextAlign.Center)
            Text(subtitle, fontSize = 11.sp, color = NeumorphicColors.muted, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProfileSettingRow(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
            Text(subtitle, fontSize = 12.sp, color = NeumorphicColors.muted)
        }
        trailing()
    }
}

@Composable
private fun EditSectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NeumorphicColors.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NeumorphicColors.text)
    }
}
