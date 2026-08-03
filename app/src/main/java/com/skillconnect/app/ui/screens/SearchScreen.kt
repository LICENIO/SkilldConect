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
fun SearchScreen(
    viewModel: SkillConnectViewModel,
    filter: String,
    onFilterChange: (String) -> Unit,
    onMentorSelect: (String) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filters = listOf("Todos", "Gratis", "Pagado", "Intercambio")

    Column(modifier = Modifier.fillMaxSize().background(NeumorphicColors.bg)) {
        NeumorphicTopBar("Buscar en la nube", onBack)
        
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            NeumorphicTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Ej. Piano, Python, Inglés..."
            )

            Spacer(modifier = Modifier.height(14.dp))
            val results = viewModel.cloudUsers.filter { u ->
                val q = query.trim().lowercase()
                val matchQuery = q.isEmpty() || u.name.lowercase().contains(q) || u.teachSkills.any { it.lowercase().contains(q) } || u.courses.any { it.title.lowercase().contains(q) }
                val matchFilter = filter == "Todos" || 
                    (filter == "Gratis" && u.hourlyRate == 0) ||
                    (filter == "Pagado" && u.hourlyRate > 0)
                matchQuery && matchFilter && u.email != viewModel.activeUserEmail
            }.sortedByDescending { it.rating }

            Text("${results.size} usuarios encontrados", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.muted)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(results) { user ->
                    NeumorphicCard(
                        modifier = Modifier.fillMaxWidth().clickable { onMentorSelect(user.email) },
                        backgroundColor = Color.White
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(
                                initials = user.initials.ifEmpty { "VR" },
                                size = 46.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Text(
                                    if (user.teachSkills.isNotEmpty()) user.teachSkills.joinToString(", ") else "Usuario nuevo",
                                    fontSize = 13.sp,
                                    color = NeumorphicColors.muted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (user.hourlyRate == 0) "Gratis o Intercambio" else "S/ ${user.hourlyRate} por hora",
                                    fontSize = 13.sp,
                                    color = NeumorphicColors.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

