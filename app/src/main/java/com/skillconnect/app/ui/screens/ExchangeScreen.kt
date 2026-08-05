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
fun ExchangeScreen(viewModel: SkillConnectViewModel, onFinished: () -> Unit, onBack: () -> Unit) {
    var chosenUserEmail by remember { 
        mutableStateOf(viewModel.selectedCloudUserEmail.ifEmpty { null }) 
    }
    
    val selectedUser = viewModel.cloudUsers.find { it.email == chosenUserEmail }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicTopBar("Trueque de Habilidades", onBack = {
            if (selectedUser != null && viewModel.selectedCloudUserEmail.isEmpty()) {
                chosenUserEmail = null
            } else {
                onBack()
            }
        })

        if (selectedUser == null) {
            // PASO 1: Lista de usuarios disponibles para trueque
            Text(
                text = "Usuarios disponibles para Trueque 🔄",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphicColors.text
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selecciona a la persona con la que deseas intercambiar habilidades gratis.",
                fontSize = 13.sp,
                color = NeumorphicColors.muted
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.cloudUsers.isEmpty()) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = NeumorphicColors.muted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay usuarios disponibles para trueque",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NeumorphicColors.text,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aún no hay otros usuarios registrados en la red. Invita a tus compañeros a unirse a SkillConnect.",
                            fontSize = 13.sp,
                            color = NeumorphicColors.muted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                viewModel.cloudUsers.forEach { user ->
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { chosenUserEmail = user.email },
                        backgroundColor = Color.White
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NeumorphicLogo(user.initials.ifEmpty { "SC" }, size = 48.dp, backgroundColor = NeumorphicColors.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (user.teachSkills.isNotEmpty()) "Enseña: ${user.teachSkills.joinToString(", ")}" else "Conocimiento variado",
                                    fontSize = 12.sp,
                                    color = NeumorphicColors.muted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "★ ${user.rating} · Trueque Gratuito",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeumorphicColors.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeumorphicColors.primary)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Trueque", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // PASO 2: Formulario de propuesta para el usuario seleccionado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Propuesta para ${selectedUser.name.split(" ").first()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphicColors.text
                )
                if (viewModel.selectedCloudUserEmail.isEmpty()) {
                    Text(
                        text = "Cambiar usuario",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphicColors.primary,
                        modifier = Modifier.clickable { chosenUserEmail = null }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeumorphicLogo(selectedUser.initials.ifEmpty { "SC" }, size = 46.dp, backgroundColor = NeumorphicColors.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(selectedUser.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                        Text(
                            text = if (selectedUser.teachSkills.isNotEmpty()) "Enseña: ${selectedUser.teachSkills.joinToString(", ")}" else "Usuario SkillConnect",
                            fontSize = 13.sp,
                            color = NeumorphicColors.muted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            var teachInput by remember(selectedUser.email) { mutableStateOf("Diseño gráfico") }
            var learnInput by remember(selectedUser.email) { mutableStateOf(selectedUser.teachSkills.firstOrNull() ?: "Programación") }
            var noteInput by remember(selectedUser.email) { mutableStateOf("Hola ${selectedUser.name.split(" ").first()}, me encantaría intercambiar habilidades contigo.") }

            Text("¿Qué enseñas tú?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.muted)
            Spacer(modifier = Modifier.height(6.dp))
            NeumorphicTextField(value = teachInput, onValueChange = { teachInput = it }, placeholder = "Ej. Dibujo, Alemán, Guitarra...")

            Spacer(modifier = Modifier.height(16.dp))

            Text("¿Qué deseas aprender de ${selectedUser.name.split(" ").first()}?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.muted)
            Spacer(modifier = Modifier.height(6.dp))
            NeumorphicTextField(value = learnInput, onValueChange = { learnInput = it }, placeholder = "Ej. Python, Inglés, Marketing...")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Mensaje personalizado", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.muted)
            Spacer(modifier = Modifier.height(6.dp))
            NeumorphicTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                placeholder = "Preséntate y propone tu idea...",
                singleLine = false
            )

            Spacer(modifier = Modifier.height(28.dp))

            NeumorphicButton(
                onClick = {
                    if (teachInput.isBlank() || learnInput.isBlank()) {
                        Toast.makeText(context, "Por favor completa qué enseñas y qué deseas aprender", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.requestExchangeWithUser(selectedUser.email, teachInput, learnInput, noteInput)
                        Toast.makeText(context, "¡Propuesta de trueque enviada a ${selectedUser.name}!", Toast.LENGTH_SHORT).show()
                        onFinished()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar propuesta a ${selectedUser.name.split(" ").first()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

