package com.skillconnect.app.ui

import android.os.Bundle
import android.view.Window
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.skillconnect.app.data.repository.SkillConnectDatabase
import com.skillconnect.app.data.repository.SkillConnectRepository
import com.skillconnect.app.ui.screens.*
import com.skillconnect.app.ui.theme.NeumorphicColors
import com.skillconnect.app.ui.theme.neumorphic
import com.skillconnect.app.viewmodel.SkillConnectViewModel

class MainActivity : FragmentActivity() {
    private lateinit var viewModel: SkillConnectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        
        val database = SkillConnectDatabase.getDatabase(applicationContext)
        val repository = SkillConnectRepository(database)
        viewModel = SkillConnectViewModel(repository)
        viewModel.loadData()

        window.statusBarColor = NeumorphicColors.bg.toArgb()
        window.navigationBarColor = Color.White.toArgb()

        setContent {
            AppContent(viewModel)
        }
    }
}

@Composable
fun AppContent(viewModel: SkillConnectViewModel) {
    var currentScreen by remember { mutableStateOf("splash") }
    val history = remember { mutableStateListOf<String>() }
    val navScreens = setOf("home", "search", "messages", "calendar", "profile")

    var activeSearchFilter by remember { mutableStateOf("Todos") }
    var activeCalendarTab by remember { mutableStateOf("Clases") }

    val activeEmail = viewModel.activeUserEmail

    // Redirección reactiva si se cierra sesión
    LaunchedEffect(activeEmail) {
        if (activeEmail == null && currentScreen !in setOf("splash", "welcome", "login", "register")) {
            history.clear()
            currentScreen = "welcome"
        }
    }

    fun navigateTo(screen: String, remember: Boolean = true) {
        if (remember && currentScreen != screen) {
            history.add(currentScreen)
        }
        currentScreen = screen
        
        if (screen == "chat") {
            viewModel.clearChatUnread(viewModel.activeChatId)
        }
    }

    fun navigateBack() {
        if (history.isNotEmpty()) {
            currentScreen = history.removeAt(history.lastIndex)
        }
    }

    BackHandler(enabled = history.isNotEmpty()) {
        navigateBack()
    }

    Scaffold(
        bottomBar = {
            if (currentScreen in navScreens) {
                NeumorphicBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { navigateTo(it, remember = true) }
                )
            }
        },
        containerColor = NeumorphicColors.bg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "splash" -> SplashScreen(onFinished = { navigateTo("welcome") })
                "welcome" -> WelcomeScreen(
                    onLogin = { navigateTo("login") },
                    onRegister = { navigateTo("register") }
                )
                "login" -> LoginScreen(
                    viewModel = viewModel,
                    onSuccess = { navigateTo("home") },
                    onRegister = { navigateTo("register") },
                    onBack = { navigateBack() }
                )
                "register" -> RegisterScreen(
                    viewModel = viewModel,
                    onSuccess = { navigateTo("home") },
                    onLogin = { navigateTo("login") },
                    onBack = { navigateBack() }
                )
                "home" -> HomeScreen(viewModel, onNavigate = { navigateTo(it) })
                "search" -> SearchScreen(
                    viewModel = viewModel,
                    filter = activeSearchFilter,
                    onFilterChange = { activeSearchFilter = it },
                    onMentorSelect = { mentorId ->
                        viewModel.selectedMentorId = mentorId
                        navigateTo("mentor")
                    }
                )
                "mentor" -> MentorScreen(
                    viewModel = viewModel,
                    onBooking = { navigateTo("booking") },
                    onExchange = { navigateTo("exchange") },
                    onBack = { navigateBack() }
                )
                "booking" -> BookingScreen(
                    viewModel = viewModel,
                    onFinished = { navigateTo("home") },
                    onBack = { navigateBack() }
                )
                "exchange" -> ExchangeScreen(
                    viewModel = viewModel,
                    onFinished = { navigateTo("home") },
                    onBack = { navigateBack() }
                )
                "messages" -> MessagesScreen(
                    viewModel = viewModel,
                    onChatSelect = { chatId ->
                        viewModel.activeChatId = chatId
                        navigateTo("chat")
                    }
                )
                "chat" -> ChatScreen(
                    viewModel = viewModel,
                    onBack = { navigateBack() }
                )
                "calendar" -> CalendarScreen(
                    viewModel = viewModel,
                    activeTab = activeCalendarTab,
                    onTabChange = { activeCalendarTab = it }
                )
                "profile" -> ProfileScreen(viewModel, onNavigate = { navigateTo(it) })
                "skills" -> SkillsScreen(viewModel, onBack = { navigateBack() })
                "learning" -> LearningScreen(viewModel, onBack = { navigateBack() })
                "settings" -> SettingsScreen(viewModel, onBack = { navigateBack() })
                "statistics" -> StatisticsScreen(onBack = { navigateBack() })
                "achievements" -> AchievementsScreen(viewModel, onBack = { navigateBack() })
                "notifications" -> NotificationsScreen(viewModel, onBack = { navigateBack() })
                "ai" -> AIScreen(
                    viewModel = viewModel,
                    onMentorFound = { mentorId ->
                        viewModel.selectedMentorId = mentorId
                        navigateTo("mentor")
                    },
                    onBack = { navigateBack() }
                )
            }
        }
    }
}

@Composable
fun NeumorphicBottomNav(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Triple("Inicio", "home", Icons.Default.Home),
        Triple("Buscar", "search", Icons.Default.Search),
        Triple("Mensajes", "messages", Icons.Default.MailOutline),
        Triple("Agenda", "calendar", Icons.Default.DateRange),
        Triple("Perfil", "profile", Icons.Default.Person)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .neumorphic(
                lightShadowColor = Color.White,
                darkShadowColor = Color(0xFFD1D9E6),
                shadowRadius = 8.dp,
                offset = 4.dp,
                cornerRadius = 0.dp
            )
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (label, screen, icon) ->
            val selected = currentScreen == screen

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigate(screen) }
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .neumorphic(
                            isInnerShadow = selected,
                            cornerRadius = 12.dp,
                            shadowRadius = if (selected) 2.dp else 4.dp,
                            offset = if (selected) 2.dp else 4.dp
                        )
                        .background(if (selected) NeumorphicColors.bg else Color.White, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) NeumorphicColors.primary else NeumorphicColors.muted,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) NeumorphicColors.primary else NeumorphicColors.muted
                )
            }
        }
    }
}
