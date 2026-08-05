package com.skillconnect.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.skillconnect.app.data.repository.CloudCalendarEvent
import com.skillconnect.app.ui.components.*
import com.skillconnect.app.ui.theme.NeumorphicColors
import com.skillconnect.app.viewmodel.SkillConnectViewModel

@Composable
fun CalendarScreen(
    viewModel: SkillConnectViewModel,
    activeTab: String,
    onTabChange: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val tabs = listOf("Clases", "Mentorías", "Intercambios")
    val context = LocalContext.current
    val myEmail = viewModel.activeUserEmail ?: ""

    // 1. Filtrar las solicitudes en Firestore para esta pestaña
    val tabRequests = viewModel.cloudRequests.filter { req ->
        (activeTab == "Intercambios" && req.type == "TRUEQUE") ||
        (activeTab != "Intercambios" && req.type == "CLASE")
    }

    // 2. Convertir cada solicitud a 1 sola tarjeta de agenda (Estado único que se actualiza in-situ)
    val requestEvents = tabRequests.map { req ->
        val isRecipient = req.recipientEmail.equals(myEmail, ignoreCase = true)
        val partnerEmail = if (isRecipient) req.senderEmail else req.recipientEmail
        val partnerName = if (isRecipient) req.senderName else req.recipientName
        val partnerInitials = if (isRecipient) req.senderInitials else partnerName.take(2).uppercase()

        val eventTitle = if (req.type == "TRUEQUE") {
            "Trueque: ${req.teachSkill} 🔄 ${req.learnSkill}"
        } else {
            "Clase de Mentoría con $partnerName"
        }

        CloudCalendarEvent(
            id = req.id,
            title = eventTitle,
            time = if (req.dateOrTime.isNotBlank()) req.dateOrTime else "Por coordinar",
            tag = if (req.type == "TRUEQUE") "Trueque" else "Clase",
            initials = partnerInitials,
            categoryTab = activeTab,
            partnerEmail = partnerEmail,
            partnerName = partnerName,
            status = req.status
        )
    }

    // 3. Eventos guardados manualmente en la agenda local
    val cloudEvents = viewModel.cloudCalendar[activeTab].orEmpty()

    // 4. Lista consolidada sin duplicados por ID
    val allEvents = (requestEvents + cloudEvents).distinctBy { if (it.id.isNotBlank()) it.id else it.title }

    var selectedEvent by remember { mutableStateOf<CloudCalendarEvent?>(null) }
    var eventToDelete by remember { mutableStateOf<CloudCalendarEvent?>(null) }

    fun tryOpenChat(partnerEmail: String, isAccepted: Boolean) {
        if (!isAccepted) {
            Toast.makeText(
                context,
                "🔒 El chat se habilitará en cuanto ambas partes ACEPTEN la propuesta.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (partnerEmail.isNotBlank()) {
            viewModel.selectedCloudUserEmail = partnerEmail
            onNavigate("chat")
        } else {
            val fallbackContact = viewModel.cloudUsers.firstOrNull { it.email != myEmail }
            if (fallbackContact != null) {
                viewModel.selectedCloudUserEmail = fallbackContact.email
                onNavigate("chat")
            } else {
                Toast.makeText(context, "No se encontró contacto directo para este evento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        Text("Agenda y Citas", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Al aceptar la solicitud, la misma tarjeta se actualiza a ACEPTADO y activa el chat.", fontSize = 12.sp, color = NeumorphicColors.muted)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabs.forEach { tab ->
                NeumorphicChip(
                    text = tab,
                    selected = activeTab == tab,
                    onClick = { onTabChange(tab) },
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (allEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    backgroundColor = Color.White
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = NeumorphicColors.muted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No tienes $activeTab agendadas",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeumorphicColors.text,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cuando propongas o recibas una solicitud de trueque/clase, aparecerá aquí.",
                            fontSize = 12.sp,
                            color = NeumorphicColors.muted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(allEvents) { event ->
                    val isAccepted = event.status == "ACEPTADO"
                    val isPending = event.status == "PENDIENTE"

                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEvent = event },
                        backgroundColor = when {
                            isAccepted -> Color(0xFFF0FDF4)
                            isPending -> Color(0xFFFFFBEB)
                            else -> Color(0xFFFEF2F2)
                        }
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                NeumorphicLogo(
                                    initials = event.initials.ifEmpty { "SC" },
                                    size = 46.dp,
                                    backgroundColor = if (isAccepted) NeumorphicColors.primary else Color.LightGray
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when {
                                                        isAccepted -> Color(0xFF16A34A)
                                                        isPending -> Color(0xFFD97706)
                                                        else -> Color(0xFFDC2626)
                                                    }
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = when {
                                                    isAccepted -> "🟢 CONFIRMADO"
                                                    isPending -> "🟡 PENDIENTE"
                                                    else -> "🔴 RECHAZADO"
                                                },
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(event.tag, fontSize = 11.sp, color = NeumorphicColors.muted, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(event.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                    Text("🕒 ${event.time}", fontSize = 13.sp, color = NeumorphicColors.muted)
                                }

                                // Botón 🗑️ de eliminación directa
                                IconButton(
                                    onClick = { eventToDelete = event }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Eliminar de la agenda",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // BOTÓN ÚNICO DE CHAT
                            if (isAccepted) {
                                Button(
                                    onClick = { tryOpenChat(event.partnerEmail, isAccepted = true) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeumorphicColors.primary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (event.partnerName.isNotBlank()) "💬 Chat con ${event.partnerName.split(" ").first()}" else "💬 Abrir Chat de Coordinación",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { tryOpenChat(event.partnerEmail, isAccepted = false) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isPending) "🔒 Chat Bloqueado (Esperando Aceptación)" else "🔒 Chat Deshabilitado",
                                        color = Color(0xFF4B5563),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO DETALLADO DEL EVENTO
    selectedEvent?.let { event ->
        val isAccepted = event.status == "ACEPTADO"
        val isPending = event.status == "PENDIENTE"

        AlertDialog(
            onDismissRequest = { selectedEvent = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isAccepted) Color(0xFFDCFCE7) else if (isPending) Color(0xFFFEF3C7) else Color(0xFFFEE2E2),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isAccepted) Icons.Default.CheckCircle else if (isPending) Icons.Default.HourglassTop else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (isAccepted) Color(0xFF16A34A) else if (isPending) Color(0xFFD97706) else Color(0xFFDC2626),
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isAccepted) Color(0xFF16A34A) else if (isPending) Color(0xFFD97706) else Color(0xFFDC2626)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when {
                                isAccepted -> "🟢 SOLICITUD ACEPTADA Y CONFIRMADA"
                                isPending -> "🟡 PENDIENTE DE ACEPTACIÓN"
                                else -> "🔴 SOLICITUD RECHAZADA"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("📅 Horario: ${event.time}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📍 Tipo: ${event.tag}", fontSize = 13.sp, color = NeumorphicColors.muted)

                    if (event.partnerName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("👤 Compañero: ${event.partnerName}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            val targetEv = event
                            selectedEvent = null
                            eventToDelete = targetEv
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🗑️ Eliminar esta Cita de mi Agenda", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                if (isAccepted) {
                    Button(
                        onClick = {
                            val pEmail = event.partnerEmail
                            selectedEvent = null
                            tryOpenChat(pEmail, isAccepted = true)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeumorphicColors.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("💬 Abrir Chat de Coordinación", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            selectedEvent = null
                            tryOpenChat("", isAccepted = false)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🔒 Chat Bloqueado", color = Color(0xFF4B5563), fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEvent = null }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar", color = NeumorphicColors.muted)
                }
            }
        )
    }

    // DIÁLOGO DE CONFIRMACIÓN DE ELIMINACIÓN
    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(36.dp))
            },
            title = {
                Text("¿Eliminar cita de la agenda?", fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
            },
            text = {
                Text(
                    "Esta acción quitará el evento '${event.title}' de tu calendario.",
                    fontSize = 13.sp,
                    color = NeumorphicColors.muted,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val evId = event.id
                        eventToDelete = null
                        viewModel.deleteCalendarEvent(evId, evId)
                        Toast.makeText(context, "Evento eliminado de tu agenda", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sí, Eliminar de la Agenda", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar", color = NeumorphicColors.muted)
                }
            }
        )
    }
}
