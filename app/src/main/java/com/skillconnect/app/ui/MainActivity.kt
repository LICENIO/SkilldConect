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
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
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
        val repository = SkillConnectRepository(database, applicationContext)
        viewModel = SkillConnectViewModel(repository)
        viewModel.loadData()

        window.statusBarColor = NeumorphicColors.headerBg.toArgb()
        window.navigationBarColor = NeumorphicColors.bottomNavBg.toArgb()

        setContent {
            AppContent(viewModel)
        }
    }
}

@Composable
fun AppContent(viewModel: SkillConnectViewModel) {
    var currentScreen by rememberSaveable { mutableStateOf("splash") }
    val history = rememberSaveable(saver = listSaver(
        save = { it.toList() },
        restore = { mutableStateListOf<String>().apply { addAll(it) } }
    )) { mutableStateListOf<String>() }
    val navScreens = setOf("home", "search", "messages", "calendar", "profile")

    var activeSearchFilter by rememberSaveable { mutableStateOf("Todos") }
    var activeCalendarTab by rememberSaveable { mutableStateOf("Clases") }

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
                "splash" -> SplashScreen(onFinished = { 
                    if (viewModel.activeUserEmail != null) {
                        navigateTo("home")
                    } else if (viewModel.savedUser != null) {
                        navigateTo("login")
                    } else {
                        navigateTo("welcome")
                    }
                })

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
                    onMentorSelect = { email ->
                        viewModel.selectedCloudUserEmail = email
                        navigateTo("mentor")
                    },
                    onBack = { navigateBack() }
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
                    onChatSelect = { email ->
                        viewModel.selectedCloudUserEmail = email
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
                    onTabChange = { activeCalendarTab = it },
                    onNavigate = { navigateTo(it) }
                )
                "profile" -> ProfileScreen(viewModel, onNavigate = { navigateTo(it) })
                "skills" -> SkillsScreen(viewModel, onBack = { navigateBack() })
                "learning" -> LearningScreen(viewModel, onBack = { navigateBack() })
                "settings" -> SettingsScreen(viewModel, onBack = { navigateBack() })
                "statistics" -> StatisticsScreen(onBack = { navigateBack() })
                "achievements" -> AchievementsScreen(viewModel, onBack = { navigateBack() })
                "notifications" -> NotificationsScreen(viewModel, onNavigate = { navigateTo(it) }, onBack = { navigateBack() })
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
            .background(
                androidx.compose.ui.graphics.Brush.horizontalGradient(
                    listOf(Color(0xFF155E75), Color(0xFF0EA5A3))
                )
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (label, screen, icon) ->
            val selected = currentScreen == screen

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onNavigate(screen) }
                    .padding(vertical = 4.dp, horizontal = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (selected) Color.White.copy(alpha = 0.25f) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) Color.White else Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) Color.White else Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}
