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
fun MessagesScreen(viewModel: SkillConnectViewModel, onChatSelect: (String) -> Unit) {
    val contacts = viewModel.cloudUsers.filter { it.email != viewModel.activeUserEmail }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Contactos y Mensajes", onBack = {})
        Spacer(modifier = Modifier.height(16.dp))

        if (contacts.isEmpty()) {
            Text("Aún no hay otros usuarios en la red.", color = NeumorphicColors.muted, fontSize = 14.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(contacts) { contact ->
                    NeumorphicCard(
                        modifier = Modifier.fillMaxWidth().clickable { onChatSelect(contact.email) },
                        backgroundColor = Color.White
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(contact.initials.ifEmpty { "VR" }, size = 48.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Text("Toca para chatear", fontSize = 13.sp, color = NeumorphicColors.primary)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = NeumorphicColors.muted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    val myEmail = viewModel.activeUserEmail ?: ""
    val theirEmail = viewModel.selectedCloudUserEmail
    val chatId = if (myEmail < theirEmail) "$myEmail-$theirEmail" else "$theirEmail-$myEmail"

    var messageText by remember { mutableStateOf("") }
    val msgs = viewModel.currentChatMessages
    
    LaunchedEffect(chatId) {
        viewModel.listenToChat(chatId)
    }

    val contact = viewModel.cloudUsers.find { it.email == theirEmail }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
    ) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            backgroundColor = Color.White
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = NeumorphicColors.text,
                    modifier = Modifier.clickable { onBack() }.padding(8.dp)
                )
                NeumorphicLogo(contact?.initials ?: "C", size = 40.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(contact?.name ?: "Chat", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("En línea", fontSize = 12.sp, color = NeumorphicColors.primary)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(msgs) { msg ->
                val fromMe = msg.senderEmail == myEmail
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (fromMe) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (fromMe) NeumorphicColors.primary else Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                            .widthIn(max = 250.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = if (fromMe) Color.White else NeumorphicColors.text,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                NeumorphicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = "Escribe un mensaje..."
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(NeumorphicColors.primary, RoundedCornerShape(14.dp))
                    .clickable {
                        if (messageText.isNotBlank()) {
                            viewModel.sendCloudMessage(chatId, messageText)
                            messageText = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
            }
        }
    }
}

