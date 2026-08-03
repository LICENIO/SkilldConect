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
    val user = viewModel.cloudUsers.find { it.email == viewModel.selectedCloudUserEmail } ?: return
    var teachInput by remember { mutableStateOf("Diseño gráfico") }
    var learnInput by remember { mutableStateOf(user.teachSkills.firstOrNull() ?: "") }
    var noteInput by remember { mutableStateOf("Hola, me encantaría intercambiar habilidades contigo.") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicTopBar("Solicitar intercambio", onBack)

        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NeumorphicLogo(user.initials.ifEmpty { "VR" }, size = 46.dp, backgroundColor = NeumorphicColors.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(user.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text(user.teachSkills.joinToString(", "), fontSize = 13.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text("¿Qué enseñas tú?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.muted)
        Spacer(modifier = Modifier.height(6.dp))
        NeumorphicTextField(value = teachInput, onValueChange = { teachInput = it }, placeholder = "Ej. Dibujo, Alemán...")

        Spacer(modifier = Modifier.height(16.dp))

        Text("¿Qué deseas aprender?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.muted)
        Spacer(modifier = Modifier.height(6.dp))
        NeumorphicTextField(value = learnInput, onValueChange = { learnInput = it }, placeholder = "La especialidad del mentor")

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
                viewModel.requestExchange(1, teachInput, learnInput, noteInput)
                Toast.makeText(context, "Solicitud de intercambio enviada", Toast.LENGTH_SHORT).show()
                onFinished()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar propuesta de trueque", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

