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
fun BookingScreen(viewModel: SkillConnectViewModel, onFinished: () -> Unit, onBack: () -> Unit) {
    val user = viewModel.cloudUsers.find { it.email == viewModel.selectedCloudUserEmail } ?: return
    val dates = listOf("Lun 6", "Mar 7", "Mié 8", "Jue 9", "Vie 10")
    val hours = listOf("9:00 AM", "11:00 AM", "3:00 PM", "5:00 PM")
    val modes = listOf("Virtual", "Presencial")
    
    var selectedDate by remember { mutableStateOf("Mié 8") }
    var selectedHour by remember { mutableStateOf("3:00 PM") }
    var selectedMode by remember { mutableStateOf("Virtual") }
    
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicTopBar("Reservar clase", onBack)

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

        Text("Selecciona una fecha", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dates) { date ->
                NeumorphicChip(text = date, selected = selectedDate == date, onClick = { selectedDate = date })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Hora disponible", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(hours) { hour ->
                NeumorphicChip(text = hour, selected = selectedHour == hour, onClick = { selectedHour = hour })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Modalidad", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(modes) { mode ->
                NeumorphicChip(text = mode, selected = selectedMode == mode, onClick = { selectedMode = mode })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Precio por hora", fontSize = 15.sp, color = NeumorphicColors.muted)
                Text(
                    text = if (user.hourlyRate == 0) "Gratis" else "S/ ${user.hourlyRate}.00",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphicColors.text
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        NeumorphicButton(
            onClick = {
                viewModel.bookClass(1, selectedDate, selectedHour, selectedMode) // mentorId unused now
                Toast.makeText(context, "Clase reservada con éxito", Toast.LENGTH_SHORT).show()
                onFinished()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

