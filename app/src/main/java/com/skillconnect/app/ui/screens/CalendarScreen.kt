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
fun CalendarScreen(viewModel: SkillConnectViewModel, activeTab: String, onTabChange: (String) -> Unit) {
    val tabs = listOf("Clases", "Mentorías", "Intercambios")
    val events = viewModel.cloudCalendar[activeTab].orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        Text("Calendario", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(14.dp))

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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(events) { event ->
                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeumorphicLogo(event.initials, size = 46.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(event.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text("${event.time} · ${event.tag}", fontSize = 13.sp, color = NeumorphicColors.muted)
                        }
                    }
                }
            }
        }
    }
}

