package com.skillconnect.app.ui.screens

import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.skillconnect.app.ui.components.*
import com.skillconnect.app.ui.theme.NeumorphicColors
import com.skillconnect.app.ui.theme.neumorphic
import com.skillconnect.app.viewmodel.SkillConnectViewModel
import kotlinx.coroutines.launch

// --- PANTALLA: SPLASH ---
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(28.dp)
        ) {
            NeumorphicLogo("SC", size = 110.dp, backgroundColor = NeumorphicColors.primary, elevation = 8.dp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SkillConnect",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphicColors.text
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Aprende. Enseña. Conecta.",
                fontSize = 16.sp,
                color = NeumorphicColors.muted
            )
            Spacer(modifier = Modifier.height(48.dp))
            NeumorphicButton(
                onClick = onFinished,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Comenzar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// --- PANTALLA: BIENVENIDA (WELCOME) ---
@Composable
fun WelcomeScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NeumorphicLogo("SC", size = 88.dp, backgroundColor = NeumorphicColors.primary)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Todo el mundo tiene algo que enseñar y todo el mundo tiene algo que aprender",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphicColors.text,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Intercambia habilidades, encuentra mentores gratuitos o reserva clases particulares con expertos reales.",
                fontSize = 15.sp,
                color = NeumorphicColors.muted,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White
        ) {
            Column {
                Text(
                    text = "Mentorías, clases e intercambios",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeumorphicColors.text
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Un solo espacio seguro para aprender y compartir lo que sabes.",
                    fontSize = 13.sp,
                    color = NeumorphicColors.muted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            NeumorphicButton(
                onClick = onLogin,
                gradientBrush = ButtonGradients.SunsetGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            NeumorphicButton(
                onClick = onRegister,
                gradientBrush = ButtonGradients.VioletCyan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- PANTALLA: LOGIN (CON INICIO REAL POR HUELLA / CORREO) ---
@Composable
fun LoginScreen(viewModel: SkillConnectViewModel, onSuccess: () -> Unit, onRegister: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("valeria.rios@ejemplo.com") }
    var password by remember { mutableStateOf("12345678") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var showSimulatedAuth by remember { mutableStateOf(false) }

    fun triggerBiometricAuth() {
        if (activity == null) {
            showSimulatedAuth = true
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    scope.launch {
                        val authSuccess = viewModel.loginWithBiometrics()
                        if (authSuccess) {
                            Toast.makeText(context, "¡Bienvenida de nuevo!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            showSimulatedAuth = true
                        }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showSimulatedAuth = true
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Inicia sesión usando tu huella digital")
            .setNegativeButtonText("Usar Contraseña")
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            showSimulatedAuth = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeumorphicTopBar("Acceso a SkillConnect", onBack, isDarkHeader = true)
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Logo Animado o Destacado
                NeumorphicLogo(
                    initials = "SC", 
                    size = 90.dp, 
                    backgroundColor = NeumorphicColors.primary,
                    elevation = 10.dp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Bienvenida de nuevo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeumorphicColors.text,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Aprende, enseña y conecta con expertos.",
                    fontSize = 15.sp,
                    color = NeumorphicColors.muted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 32.dp),
                    textAlign = TextAlign.Center
                )

                // Campo: Correo
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Correo electrónico", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
                    Spacer(modifier = Modifier.height(8.dp))
                    NeumorphicTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "ejemplo@correo.com",
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = NeumorphicColors.primary, modifier = Modifier.size(20.dp))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo: Contraseña
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Contraseña", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
                    Spacer(modifier = Modifier.height(8.dp))
                    NeumorphicTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Ingresa tu clave",
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeumorphicColors.primary, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = NeumorphicColors.muted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fila: Recordarme y Olvidé mi clave
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeumorphicColors.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recordarme", fontSize = 13.sp, color = NeumorphicColors.text)
                    }
                    Text(
                        text = "¿Olvidaste tu clave?",
                        fontSize = 13.sp,
                        color = NeumorphicColors.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { /* logic */ }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Ingresar + Huella
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeumorphicButton(
                        onClick = {
                            if (email.isNotBlank() && password.length >= 4) {
                                isLoading = true
                                scope.launch {
                                    val success = viewModel.loginWithEmail(email, password)
                                    isLoading = false
                                    if (success) {
                                        Toast.makeText(context, "¡Hola de nuevo!", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    } else {
                                        Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Completa los campos correctamente", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        gradientBrush = if (isLoading) null else ButtonGradients.SunsetGold,
                        backgroundColor = if (isLoading) Color.LightGray else NeumorphicColors.primary
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Validando...", color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Entrar ahora", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    // Botón Huella Digital Mejorado
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .neumorphic(cornerRadius = 16.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .clickable { triggerBiometricAuth() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Huella",
                            tint = NeumorphicColors.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Login Social (Google)
                NeumorphicButton(
                    onClick = { /* Google Login logic */ },
                    modifier = Modifier.fillMaxWidth(),
                    gradientBrush = null,
                    backgroundColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle, // Simula icono de Google
                        contentDescription = null,
                        tint = Color(0xFFEA4335),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continuar con Google", color = NeumorphicColors.text, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("¿Eres nueva? ", color = NeumorphicColors.muted, fontSize = 14.sp)
                    Text(
                        text = "Crea una cuenta aquí",
                        color = NeumorphicColors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onRegister() }
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Modal de Huella Mejorado
        if (showSimulatedAuth) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showSimulatedAuth = false },
                contentAlignment = Alignment.Center
            ) {
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp)
                        .clickable(enabled = false) { },
                    cornerRadius = 28.dp,
                    backgroundColor = Color.White
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Acceso Biométrico",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = NeumorphicColors.text
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Toca el sensor para verificar tu identidad",
                            fontSize = 14.sp,
                            color = NeumorphicColors.muted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .neumorphic(cornerRadius = 50.dp, shadowRadius = 10.dp)
                                .background(NeumorphicColors.bg, CircleShape)
                                .clickable {
                                    scope.launch {
                                        val success = viewModel.loginWithBiometrics()
                                        if (success) {
                                            showSimulatedAuth = false
                                            Toast.makeText(context, "Huella verificada", Toast.LENGTH_SHORT).show()
                                            onSuccess()
                                        } else {
                                            Toast.makeText(context, "Registra una cuenta primero", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Huella",
                                tint = NeumorphicColors.primary,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "Cancelar",
                            color = NeumorphicColors.muted,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showSimulatedAuth = false }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- PANTALLA: REGISTRO (REGISTER) ---
@Composable
fun RegisterScreen(viewModel: SkillConnectViewModel, onSuccess: () -> Unit, onLogin: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selection by remember { mutableStateOf("Aprender") }
    val options = listOf("Aprender", "Enseñar", "Ambos")
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
    ) {
        NeumorphicTopBar("Crear cuenta", onBack, isDarkHeader = true)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Nombre completo", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
            Spacer(modifier = Modifier.height(6.dp))
            NeumorphicTextField(value = name, onValueChange = { name = it }, placeholder = "Tu nombre")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Correo electrónico", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
            Spacer(modifier = Modifier.height(6.dp))
            NeumorphicTextField(value = email, onValueChange = { email = it }, placeholder = "correo@ejemplo.com")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Contraseña", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
            Spacer(modifier = Modifier.height(6.dp))
            NeumorphicTextField(value = password, onValueChange = { password = it }, placeholder = "Mínimo 4 caracteres", visualTransformation = PasswordVisualTransformation())

            Spacer(modifier = Modifier.height(22.dp))

            Text("¿Qué deseas hacer en SkillConnect?", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NeumorphicColors.text)
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                options.forEach { option ->
                    NeumorphicChip(
                        text = option,
                        selected = selection == option,
                        onClick = { selection = option },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            NeumorphicButton(
                onClick = {
                    scope.launch {
                        val success = viewModel.registerUser(name, email, password, selection)
                        if (success) {
                            Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            Toast.makeText(context, "Error: correo ya registrado o datos incompletos", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarme", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿Ya tienes cuenta? Inicia sesión aquí",
                color = NeumorphicColors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogin() }
            )
        }
    }
}

// --- PANTALLA: INICIO (HOME) ---
@Composable
fun HomeScreen(viewModel: SkillConnectViewModel, onNavigate: (String) -> Unit) {
    val user = viewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
    ) {
        // Banner Superior Vibrante Celeste Eléctrico
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFF0099FF), Color(0xFF0066FF))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("¡Hola, ${user?.name ?: "Valeria"}! 👋", fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                        Text("¿Qué quieres aprender hoy?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier.clickable { onNavigate("profile") }
                    ) {
                        NeumorphicLogo(
                            initials = user?.initials ?: "VR",
                            size = 50.dp,
                            backgroundColor = NeumorphicColors.accentYellow,
                            textColor = NeumorphicColors.text
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable { onNavigate("search") }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar", tint = NeumorphicColors.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Busca una habilidad o trueque, ej. Guitarra, Python...", color = NeumorphicColors.muted, fontSize = 14.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Tarjeta Destacada de Trueque e Intercambio de Habilidades
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("exchange") },
                backgroundColor = Color(0xFFEFF6FF)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(NeumorphicColors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Trueque", tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trueque de Habilidades 🔄", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Enseña lo que sabes y aprende gratis lo que necesitas.", fontSize = 12.sp, color = NeumorphicColors.muted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Categorías", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
            Spacer(modifier = Modifier.height(12.dp))
            
            Column {
                val chunks = viewModel.categories.chunked(2)
                chunks.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        pair.forEach { category ->
                            NeumorphicCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .height(90.dp)
                                    .clickable { onNavigate("search") },
                                backgroundColor = Color.White
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(category.icon, fontSize = 22.sp, color = NeumorphicColors.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(category.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("ai") },
                backgroundColor = NeumorphicColors.primary
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = NeumorphicColors.accentYellow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Asistente SkillMatch", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Encuentra al mentor ideal según tus metas con inteligencia artificial.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Profesores recomendados", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphicColors.primary,
                    modifier = Modifier.clickable { onNavigate("search") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(viewModel.mentors.take(4)) { mentor ->
                    NeumorphicCard(
                        modifier = Modifier
                            .width(200.dp)
                            .clickable {
                                viewModel.selectedMentorId = mentor.id
                                onNavigate("mentor")
                            },
                        backgroundColor = Color.White
                    ) {
                        Column {
                            NeumorphicLogo(
                                initials = mentor.initials,
                                backgroundColor = Color(android.graphics.Color.parseColor(mentor.accentColor)),
                                size = 50.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(mentor.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text(mentor.specialty, fontSize = 12.sp, color = NeumorphicColors.muted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "★ ${mentor.rating} · ${if (mentor.price == 0) "Gratis" else "S/ ${mentor.price}"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeumorphicColors.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Intercambios recientes (Trueques)", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphicColors.primary,
                    modifier = Modifier.clickable { onNavigate("search") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            viewModel.exchanges.forEach { exchange ->
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    backgroundColor = Color.White
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeumorphicLogo(exchange.initials, size = 44.dp, backgroundColor = NeumorphicColors.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(exchange.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text(exchange.subtitle, fontSize = 12.sp, color = NeumorphicColors.muted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- PANTALLA: SEARCH ---
@Composable
fun SearchScreen(
    viewModel: SkillConnectViewModel,
    filter: String,
    onFilterChange: (String) -> Unit,
    onMentorSelect: (Int) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filters = listOf("Todos", "Virtual", "Presencial", "Gratis", "Intercambio", "Pagado")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
    ) {
        NeumorphicTopBar("Explorar Mentores", onBack, isDarkHeader = true)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            NeumorphicTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Ej. Piano, Python, Inglés..."
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filters) { f ->
                    NeumorphicChip(
                        text = f,
                        selected = filter == f,
                        onClick = { onFilterChange(f) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val results = viewModel.searchMentors(query, filter)
            Text("${results.size} resultados encontrados", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.muted)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(results) { mentor ->
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMentorSelect(mentor.id) },
                        backgroundColor = Color.White
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(
                                initials = mentor.initials,
                                backgroundColor = Color(android.graphics.Color.parseColor(mentor.accentColor)),
                                size = 46.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(mentor.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Text(
                                    "${mentor.specialty} · ★ ${mentor.rating}",
                                    fontSize = 13.sp,
                                    color = NeumorphicColors.muted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (mentor.price == 0) "Gratis" else "S/ ${mentor.price} por hora",
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

// --- PANTALLA: MENTOR PROFILE ---
@Composable
fun MentorScreen(
    viewModel: SkillConnectViewModel,
    onBooking: () -> Unit,
    onExchange: () -> Unit,
    onBack: () -> Unit
) {
    val mentor = viewModel.selectedMentor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicTopBar("Perfil del mentor", onBack)
        
        Spacer(modifier = Modifier.height(8.dp))

        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(android.graphics.Color.parseColor(mentor.accentColor))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NeumorphicLogo(
                    initials = mentor.initials,
                    size = 86.dp,
                    textColor = Color(android.graphics.Color.parseColor(mentor.accentColor)),
                    backgroundColor = Color.White
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(mentor.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(mentor.specialty, fontSize = 15.sp, color = Color.White.copy(alpha = 0.9f))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("★ ${mentor.rating}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("${mentor.reviews} reseñas", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(if (mentor.price == 0) "Gratis" else "S/ ${mentor.price}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("por hora", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text("Sobre el mentor", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(8.dp))
        Text(mentor.description, fontSize = 15.sp, color = NeumorphicColors.muted)

        Spacer(modifier = Modifier.height(22.dp))

        Text("Disponibilidad", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(mentor.availability) { time ->
                NeumorphicChip(text = time, selected = false, onClick = {})
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        NeumorphicButton(onClick = onBooking, modifier = Modifier.fillMaxWidth()) {
            Text("Reservar clase", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        NeumorphicButton(
            onClick = onExchange,
            gradientBrush = ButtonGradients.VioletCyan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Solicitar intercambio (Trueque)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

// --- PANTALLA: RESERVAS (BOOKING) ---
@Composable
fun BookingScreen(viewModel: SkillConnectViewModel, onFinished: () -> Unit, onBack: () -> Unit) {
    val mentor = viewModel.selectedMentor()
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
                NeumorphicLogo(mentor.initials, size = 46.dp, backgroundColor = Color(android.graphics.Color.parseColor(mentor.accentColor)))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(mentor.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text(mentor.specialty, fontSize = 13.sp, color = NeumorphicColors.muted)
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
                    text = if (mentor.price == 0) "Gratis" else "S/ ${mentor.price}.00",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphicColors.text
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        NeumorphicButton(
            onClick = {
                viewModel.bookClass(mentor.id, selectedDate, selectedHour, selectedMode)
                Toast.makeText(context, "Clase reservada con éxito", Toast.LENGTH_SHORT).show()
                onFinished()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// --- PANTALLA: INTERCAMBIOS (EXCHANGE REQUEST) ---
@Composable
fun ExchangeScreen(viewModel: SkillConnectViewModel, onFinished: () -> Unit, onBack: () -> Unit) {
    val mentor = viewModel.selectedMentor()
    var teachInput by remember { mutableStateOf("Diseño gráfico") }
    var learnInput by remember { mutableStateOf(mentor.specialty) }
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
                NeumorphicLogo(mentor.initials, size = 46.dp, backgroundColor = Color(android.graphics.Color.parseColor(mentor.accentColor)))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(mentor.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text(mentor.specialty, fontSize = 13.sp, color = NeumorphicColors.muted)
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
                viewModel.requestExchange(mentor.id, teachInput, learnInput, noteInput)
                Toast.makeText(context, "Solicitud de intercambio enviada", Toast.LENGTH_SHORT).show()
                onFinished()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar propuesta de trueque", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// --- PANTALLA: MENSAJES (MESSAGES THREADS) ---
@Composable
fun MessagesScreen(viewModel: SkillConnectViewModel, onChatSelect: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        Text("Mensajes", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(viewModel.chats) { chat ->
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChatSelect(chat.id) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeumorphicLogo(chat.initials, size = 46.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(chat.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text("${chat.lastMessage} · ${chat.time}", fontSize = 12.sp, color = NeumorphicColors.muted)
                            if (chat.unread > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${chat.unread} mensajes nuevos", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLA: CHAT ---
@Composable
fun ChatScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    val chat = viewModel.chatById(viewModel.activeChatId)
    var textMessage by remember { mutableStateOf("") }
    val listState = rememberScrollState()

    LaunchedEffect(chat.messages.size) {
        listState.animateScrollTo(listState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
    ) {
        NeumorphicTopBar(chat.name, onBack, modifier = Modifier.padding(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(listState)
        ) {
            chat.messages.forEach { msg ->
                val bubbleColor = if (msg.fromMe) NeumorphicColors.primary else Color.White
                val textColor = if (msg.fromMe) Color.White else NeumorphicColors.text
                val align = if (msg.fromMe) Alignment.End else Alignment.Start

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = align
                ) {
                    Box(
                        modifier = Modifier
                            .neumorphic(
                                cornerRadius = 18.dp,
                                lightShadowColor = if (msg.fromMe) Color.Transparent else NeumorphicColors.lightShadow,
                                darkShadowColor = if (msg.fromMe) Color.Transparent else NeumorphicColors.darkShadow
                            )
                            .background(bubbleColor, RoundedCornerShape(18.dp))
                            .padding(12.dp)
                            .widthIn(max = 260.dp)
                    ) {
                        Text(msg.text, color = textColor, fontSize = 15.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeumorphicTextField(
                value = textMessage,
                onValueChange = { textMessage = it },
                placeholder = "Escribe un mensaje...",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            NeumorphicButton(
                onClick = {
                    if (textMessage.isNotBlank()) {
                        viewModel.addMessage(textMessage)
                        textMessage = ""
                    }
                }
            ) {
                Text("Enviar", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- PANTALLA: CALENDARIO ---
@Composable
fun CalendarScreen(viewModel: SkillConnectViewModel, activeTab: String, onTabChange: (String) -> Unit) {
    val tabs = listOf("Clases", "Mentorías", "Intercambios")
    val events = viewModel.calendar[activeTab].orEmpty()

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

// --- PANTALLA: PERFIL (PROFILE - COMPLETO CON CONEXIÓN REAL Y CERRAR SESIÓN) ---
@Composable
fun ProfileScreen(viewModel: SkillConnectViewModel, onNavigate: (String) -> Unit) {
    val user = viewModel.currentUser
    val options = listOf(
        "Mis habilidades" to "skills",
        "Mis aprendizajes" to "learning",
        "Estadísticas" to "statistics",
        "Logros" to "achievements",
        "Configuración" to "settings"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NeumorphicLogo(user?.initials ?: "VR", size = 88.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(user?.name ?: "Valeria Ríos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text(
                    text = if (user != null) "Rol: ${user.role}" else "Diseñadora UX - Aprendiz de guitarra",
                    fontSize = 14.sp,
                    color = NeumorphicColors.muted
                )
                Spacer(modifier = Modifier.height(16.dp))
                NeumorphicButton(onClick = { /* Edit */ }) {
                    Text("Editar perfil", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val stats = listOf(
                "${user?.rating ?: 4.9}" to "Calificación",
                "${user?.classesCount ?: 32}" to "Clases",
                "${user?.exchangesCount ?: 18}" to "Intercambios"
            )
            stats.forEach { (valStr, cap) ->
                NeumorphicCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(valStr, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                        Text(cap, fontSize = 12.sp, color = NeumorphicColors.muted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        options.forEach { (label, dest) ->
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onNavigate(dest) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón neumórfico para Cerrar Sesión con gradiente Rojo Coral y texto Blanco
        NeumorphicButton(
            onClick = { viewModel.logout() },
            gradientBrush = ButtonGradients.CoralRose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// --- PANTALLA: MIS HABILIDADES ---
@Composable
fun SkillsScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var newSkillName by remember { mutableStateOf("") }
    var newSkillLevel by remember { mutableStateOf("Intermedio") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeumorphicColors.bg)
                .padding(20.dp)
        ) {
            NeumorphicTopBar("Mis habilidades", onBack)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.skills) { skill ->
                    NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(skill.name.take(2).uppercase(), size = 46.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(skill.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Text(skill.level, fontSize = 13.sp, color = NeumorphicColors.muted)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            NeumorphicButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar habilidad", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
            }
        }
        
        if (showAddDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(0.85f).padding(16.dp),
                    cornerRadius = 24.dp
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Agregar Habilidad", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeumorphicColors.text)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        NeumorphicTextField(
                            value = newSkillName,
                            onValueChange = { newSkillName = it },
                            placeholder = "Nombre de la habilidad (ej. Piano)"
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("Principiante", "Intermedio", "Avanzado").forEach { level ->
                                NeumorphicChip(
                                    text = level,
                                    selected = newSkillLevel == level,
                                    onClick = { newSkillLevel = level }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            NeumorphicButton(
                                onClick = {
                                    showAddDialog = false
                                    newSkillName = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar", color = NeumorphicColors.muted, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            NeumorphicButton(
                                onClick = {
                                    if (newSkillName.isNotBlank()) {
                                        viewModel.addSkill(newSkillName, newSkillLevel)
                                        showAddDialog = false
                                        newSkillName = ""
                                        Toast.makeText(context, "Habilidad guardada en base de datos", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Por favor escribe un nombre", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Guardar", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLA: MIS APRENDIZAJES ---
@Composable
fun LearningScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Mis aprendizajes", onBack)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.learning) { item ->
                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(item.name.take(2).uppercase(), size = 46.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                Text("${item.teacher} - ${item.progress}%", fontSize = 13.sp, color = NeumorphicColors.muted)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        NeumorphicProgressBar(progress = item.progress / 100f)
                    }
                }
            }
        }
    }
}

// --- PANTALLA: CONFIGURACIÓN (SETTINGS - CON ACCESO A CERRAR SESIÓN) ---
@Composable
fun SettingsScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    var darkMode by remember { mutableStateOf(false) }
    var notificationState by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Configuración", onBack)

        Spacer(modifier = Modifier.height(10.dp))

        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo oscuro", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Switch(checked = darkMode, onCheckedChange = { darkMode = it })
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notificaciones", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Switch(checked = notificationState, onCheckedChange = { notificationState = it })
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Idioma", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text("Español", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = NeumorphicColors.primary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.logout() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Cerrar sesión", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    Text("Salir de tu cuenta actual de forma segura", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.Red)
            }
        }
    }
}

// --- PANTALLA: ESTADÍSTICAS (STATISTICS) ---
@Composable
fun StatisticsScreen(onBack: () -> Unit) {
    val weeklyProgress = listOf(0.70f, 0.45f, 0.88f, 0.54f, 0.78f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicTopBar("Estadísticas", onBack)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("86h", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("Horas enseñadas", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("54h", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("Horas aprendidas", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("18", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("Intercambios", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
            NeumorphicCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("4.9", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.primary)
                    Text("Calificación", fontSize = 12.sp, color = NeumorphicColors.muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Actividad semanal", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))

        weeklyProgress.forEachIndexed { idx, progress ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Día ${idx + 1}", fontSize = 13.sp, color = NeumorphicColors.muted, modifier = Modifier.width(56.dp))
                NeumorphicProgressBar(progress = progress, modifier = Modifier.weight(1f))
            }
        }
    }
}

// --- PANTALLA: LOGROS (ACHIEVEMENTS) ---
@Composable
fun AchievementsScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Logros", onBack)

        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFEAF8F2)
        ) {
            Column {
                Text("9/14 desbloqueados", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                Text("Sigue aprendiendo y enseñando para completar tus insignias.", fontSize = 13.sp, color = NeumorphicColors.muted)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.achievements) { ach ->
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (ach.unlocked) Color.White else Color(0xFFF3F4F6)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (ach.unlocked) NeumorphicColors.primary.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (ach.unlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (ach.unlocked) NeumorphicColors.primary else NeumorphicColors.muted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(ach.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text(ach.description, fontSize = 13.sp, color = NeumorphicColors.muted)
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLA: NOTIFICACIONES (NOTIFICATIONS) ---
@Composable
fun NotificationsScreen(viewModel: SkillConnectViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Notificaciones", onBack)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.notifications) { item ->
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (item.unread) Color(0xFFEEF5FF) else Color.White
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (item.unread) NeumorphicColors.primary.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.unread) Icons.Default.NotificationImportant else Icons.Default.Notifications,
                                contentDescription = null,
                                tint = if (item.unread) NeumorphicColors.primary else NeumorphicColors.muted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                            Text(item.description, fontSize = 13.sp, color = NeumorphicColors.muted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(item.time, fontSize = 11.sp, color = NeumorphicColors.muted, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLA: SKILLMATCH (AI ASSISTANT) ---
@Composable
fun AIScreen(viewModel: SkillConnectViewModel, onMentorFound: (Int) -> Unit, onBack: () -> Unit) {
    var searchPrompt by remember { mutableStateOf("Quiero aprender guitarra los fines de semana") }
    val context = LocalContext.current
    val suggestions = listOf("Guitarra", "Inglés", "Excel", "Python")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NeumorphicTopBar("SkillMatch AI", onBack)

        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFEAF8F2)
        ) {
            Column {
                Text(
                    "¿Qué quieres aprender?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphicColors.text
                )
                Text(
                    "Describe tu meta y SkillMatch sugerirá mentores por modalidad, horario y presupuesto en base a IA.",
                    fontSize = 13.sp,
                    color = NeumorphicColors.muted
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text("Tu búsqueda", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.muted)
        Spacer(modifier = Modifier.height(8.dp))
        NeumorphicTextField(value = searchPrompt, onValueChange = { searchPrompt = it }, placeholder = "Describe tu meta...")

        Spacer(modifier = Modifier.height(18.dp))

        NeumorphicButton(
            onClick = {
                Toast.makeText(context, "SkillMatch: Encontramos 2 coincidencias", Toast.LENGTH_SHORT).show()
                onMentorFound(1)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeumorphicColors.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Buscar coincidencias", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Sugerencias rápidas", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { suggestion ->
                NeumorphicChip(
                    text = suggestion,
                    selected = searchPrompt.contains(suggestion, ignoreCase = true),
                    onClick = { searchPrompt = "Quiero aprender $suggestion" },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
