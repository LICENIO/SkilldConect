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
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(110.dp)
                    .neumorphic(cornerRadius = 55.dp, offset = 4.dp, shadowRadius = 8.dp)
                    .clip(CircleShape)
            )
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
                modifier = Modifier.fillMaxWidth(0.8f),
                gradientBrush = ButtonGradients.PrimaryBlueViolet
            ) {
                Text("Comenzar", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, letterSpacing = 0.3.sp)
            }
        }
    }
}

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
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(88.dp)
                    .neumorphic(cornerRadius = 44.dp, offset = 2.dp, shadowRadius = 4.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Todo el mundo tiene algo que enseñar y todo el mundo tiene algo que aprender",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeumorphicColors.text,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                letterSpacing = (-0.5).sp
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
                modifier = Modifier.fillMaxWidth(),
                gradientBrush = ButtonGradients.PrimaryBlueViolet
            ) {
                Text("Iniciar sesión", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            NeumorphicButton(
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth(),
                gradientBrush = ButtonGradients.AccentViolet
            ) {
                Text("Crear cuenta", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun LoginScreen(viewModel: SkillConnectViewModel, onSuccess: () -> Unit, onRegister: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf(viewModel.savedUser?.email ?: "") }
    var isChangingAccount by remember { mutableStateOf(viewModel.savedUser == null) }

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var showBiometricRegisterDialog by remember { mutableStateOf(false) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotNewPassword by remember { mutableStateOf("") }
    var inputOtp by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("") }
    var forgotStep by remember { mutableStateOf(1) } // 1: Email, 2: OTP, 3: New Password

    val shakeOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    var isPasswordError by remember { mutableStateOf(false) }


    fun triggerBiometricAuth() {
        if (activity == null) {
            Toast.makeText(context, "Error: Actividad no encontrada", Toast.LENGTH_SHORT).show()
            return
        }

        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
        
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(context, "Biometría no disponible o no configurada", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(context, "¡Hola de nuevo!", Toast.LENGTH_SHORT).show()
                            onSuccess()

                        } else {
                            showBiometricRegisterDialog = true
                        }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Error: $errString", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "Ocurrió un error al iniciar biometría", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(viewModel.savedUser) {
        if (viewModel.savedUser != null) {
            triggerBiometricAuth()
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
                
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(90.dp)
                        .neumorphic(cornerRadius = 45.dp, offset = 5.dp, shadowRadius = 10.dp)
                        .clip(CircleShape)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = viewModel.savedUser?.let { "Hola, ${it.name}" } ?: "Bienvenido",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeumorphicColors.text,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Aprende, enseña y conecta con expertos.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphicColors.muted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                if (!isChangingAccount && viewModel.savedUser != null) {
                    Text(
                        text = "Cambiar de cuenta",
                        color = NeumorphicColors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { 
                                isChangingAccount = true
                                email = "" 
                            }
                            .padding(bottom = 24.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Campo: Correo
                if (isChangingAccount) {
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
                }

                // Campo: Contraseña
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Contraseña", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
                    Spacer(modifier = Modifier.height(8.dp))
                    NeumorphicTextField(
                        value = password,
                        onValueChange = { 
                            password = it 
                            if (isPasswordError) isPasswordError = false
                        },
                        placeholder = "Ingresa tu clave",
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.offset(x = shakeOffset.value.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = if (isPasswordError) Color.Red else NeumorphicColors.primary, modifier = Modifier.size(20.dp))
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
                        modifier = Modifier.clickable { 
                            forgotEmail = email
                            forgotStep = 1
                            showForgotPasswordDialog = true 
                        }
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
                            if (email.isNotBlank() && password.length >= 6) {
                                isLoading = true
                                scope.launch {
                                    val result = viewModel.loginWithEmail(email, password, rememberMe)
                                    isLoading = false
                                    when (result) {
                                        SkillConnectViewModel.LoginResult.Success -> {
                                            Toast.makeText(context, "¡Hola de nuevo!", Toast.LENGTH_SHORT).show()
                                            onSuccess()
                                        }
                                        SkillConnectViewModel.LoginResult.UserNotFound -> {
                                            Toast.makeText(context, "No existe ninguna cuenta con este correo.", Toast.LENGTH_LONG).show()
                                        }
                                        SkillConnectViewModel.LoginResult.IncorrectPassword -> {
                                            isPasswordError = true
                                            Toast.makeText(context, "La contraseña es incorrecta. Inténtalo de nuevo.", Toast.LENGTH_LONG).show()
                                            launch {
                                                shakeOffset.animateTo(15f, animationSpec = androidx.compose.animation.core.tween(50))
                                                shakeOffset.animateTo(-15f, animationSpec = androidx.compose.animation.core.tween(50))
                                                shakeOffset.animateTo(15f, animationSpec = androidx.compose.animation.core.tween(50))
                                                shakeOffset.animateTo(0f, animationSpec = androidx.compose.animation.core.tween(50))
                                            }
                                        }
                                        SkillConnectViewModel.LoginResult.NetworkError -> {
                                            Toast.makeText(context, "No pudimos conectar con Firebase. Revisa internet o la configuracion.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Completa los campos correctamente", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        gradientBrush = if (isLoading) null else ButtonGradients.PrimaryBlueViolet,
                        backgroundColor = if (isLoading) Color.LightGray else NeumorphicColors.primary
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Validando...", color = Color.White, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("Entrar ahora", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
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
        
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = {
                    Text("Recuperar cuenta", fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                },
                text = {
                    Column {
                        Text(
                            "Escribe el correo de la cuenta. Firebase enviara un enlace seguro para crear una nueva contrasena.",
                            fontSize = 14.sp,
                            color = NeumorphicColors.muted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        NeumorphicTextField(
                            value = forgotEmail,
                            onValueChange = { forgotEmail = it },
                            placeholder = "ejemplo@correo.com",
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = NeumorphicColors.primary, modifier = Modifier.size(20.dp))
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            if (viewModel.sendPasswordReset(forgotEmail)) {
                                Toast.makeText(context, "Revisa tu correo para restablecer la contrasena.", Toast.LENGTH_LONG).show()
                                showForgotPasswordDialog = false
                            } else {
                                Toast.makeText(context, "No se pudo enviar el correo. Verifica la cuenta o Firebase.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }) {
                        Text("Enviar enlace")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) { Text("Cancelar") }
                }
            )
        }

        if (false && showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { 
                    val titleText = when (forgotStep) {
                        1 -> "Recuperar Contraseña"
                        2 -> "Código de Seguridad"
                        else -> "Nueva Contraseña"
                    }
                    Text(titleText, fontWeight = FontWeight.Bold, color = NeumorphicColors.text) 
                },
                text = {
                    Column {
                        when (forgotStep) {
                            1 -> {
                                Text("Ingresa tu correo para buscar tu cuenta.", fontSize = 14.sp, color = NeumorphicColors.muted)
                                Spacer(modifier = Modifier.height(16.dp))
                                NeumorphicTextField(
                                    value = forgotEmail,
                                    onValueChange = { forgotEmail = it },
                                    placeholder = "ejemplo@correo.com"
                                )
                            }
                            2 -> {
                                Text("Hemos enviado un código de 6 dígitos a tu correo. Ingrésalo aquí.", fontSize = 14.sp, color = NeumorphicColors.muted)
                                Spacer(modifier = Modifier.height(16.dp))
                                NeumorphicTextField(
                                    value = inputOtp,
                                    onValueChange = { if (it.length <= 6) inputOtp = it },
                                    placeholder = "123456"
                                )
                            }
                            3 -> {
                                Text("Crea una nueva contraseña para tu cuenta.", fontSize = 14.sp, color = NeumorphicColors.muted)
                                Spacer(modifier = Modifier.height(16.dp))
                                NeumorphicTextField(
                                    value = forgotNewPassword,
                                    onValueChange = { forgotNewPassword = it },
                                    placeholder = "Nueva Contraseña",
                                    visualTransformation = PasswordVisualTransformation()
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            when (forgotStep) {
                                1 -> {
                                    if (forgotEmail.isNotBlank()) {
                                        val result = viewModel.loginWithEmail(forgotEmail, "dummy", false)
                                        if (result == SkillConnectViewModel.LoginResult.UserNotFound) {
                                            Toast.makeText(context, "Correo no encontrado", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Generar OTP
                                            generatedOtp = (100000..999999).random().toString()
                                            forgotStep = 2 // Pasar a OTP
                                            // Simular envío de correo
                                            Toast.makeText(context, "📩 SIMULADOR DE CORREO:\nTu código es $generatedOtp", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                2 -> {
                                    if (inputOtp == generatedOtp) {
                                        forgotStep = 3 // Pasar a Nueva Contraseña
                                        Toast.makeText(context, "Código verificado", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Código incorrecto", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                3 -> {
                                    if (forgotNewPassword.length >= 6) {
                                        val success = viewModel.sendPasswordReset(forgotEmail)
                                        if (success) {
                                            Toast.makeText(context, "Contraseña actualizada. Inicia sesión.", Toast.LENGTH_LONG).show()
                                            showForgotPasswordDialog = false
                                        } else {
                                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }) {
                        Text(if (forgotStep == 3) "Guardar" else "Continuar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) { Text("Cancelar") }
                }
            )
        }
        
        if (showBiometricRegisterDialog) {
            var newName by remember { mutableStateOf("") }
            var newPassword by remember { mutableStateOf("") }
            var newPasswordVisible by remember { mutableStateOf(false) }
            
            // Lógica de fuerza de contraseña
            val strength = when {
                newPassword.length < 6 -> "Débil"
                newPassword.length >= 6 && newPassword.any { it.isDigit() } && newPassword.any { it.isUpperCase() } -> "Fuerte"
                else -> "Media"
            }
            val strengthColor = when (strength) {
                "Débil" -> Color.Red
                "Fuerte" -> Color(0xFF4CAF50)
                else -> Color(0xFFFFC107)
            }
            
            AlertDialog(
                onDismissRequest = { showBiometricRegisterDialog = false },
                title = { Text("Completar Registro", fontWeight = FontWeight.Bold, color = NeumorphicColors.text) },
                text = {
                    Column {
                        Text("Ingresa tus datos para vincular tu huella.", fontSize = 14.sp, color = NeumorphicColors.muted)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Nombre", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
                        Spacer(modifier = Modifier.height(8.dp))
                        NeumorphicTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            placeholder = "Tu nombre"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Contraseña", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeumorphicColors.text)
                        Spacer(modifier = Modifier.height(8.dp))
                        NeumorphicTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            placeholder = "Contraseña",
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = NeumorphicColors.muted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        )
                        if (newPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Text("Seguridad: $strength", fontSize = 12.sp, color = strengthColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                confirmButton = {
                    NeumorphicButton(onClick = {
                        if (newName.isNotBlank() && newPassword.length >= 6) {
                            scope.launch {
                                viewModel.registerAfterBiometric(newName, newPassword)
                                showBiometricRegisterDialog = false
                                Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                        } else {
                            Toast.makeText(context, "Completa los campos (Contraseña mín. 6 caracteres)", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBiometricRegisterDialog = false }) {
                        Text("Cancelar", color = NeumorphicColors.primary, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = NeumorphicColors.bg,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }

}

@Composable
fun RegisterScreen(viewModel: SkillConnectViewModel, onSuccess: () -> Unit, onLogin: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selection by remember { mutableStateOf("Aprender") }
    val options = listOf("Aprender", "Enseñar", "Ambos")
    
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val scope = rememberCoroutineScope()

    fun triggerBiometricRegister() {
        if (activity == null) {
            Toast.makeText(context, "Error: Actividad no encontrada", Toast.LENGTH_SHORT).show()
            return
        }

        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
        
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(context, "Biometría no disponible o no configurada", Toast.LENGTH_SHORT).show()
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
                        val success = viewModel.quickRegisterWithBiometrics()
                        if (success) {
                            Toast.makeText(context, "Cuenta rápida creada con éxito", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            Toast.makeText(context, "Error al crear cuenta rápida", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Registro Rápido")
            .setSubtitle("Crea una cuenta rápida con tu huella")
            .setNegativeButtonText("Cancelar")
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Toast.makeText(context, "Ocurrió un error al iniciar biometría", Toast.LENGTH_SHORT).show()
        }
    }

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
            NeumorphicTextField(value = password, onValueChange = { password = it }, placeholder = "Mínimo 6 caracteres", visualTransformation = PasswordVisualTransformation())

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
                    if (password.length < 6) {
                        Toast.makeText(context, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch {
                            val registerResult = viewModel.registerUser(name, email, password, selection)
                            if (registerResult == SkillConnectViewModel.RegisterResult.Success) {
                                Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            } else {
                                Toast.makeText(context, "Error: verifica tus datos y conexión", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                gradientBrush = ButtonGradients.PrimaryBlueViolet
            ) {
                Text("Registrarme", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            NeumorphicButton(
                onClick = { triggerBiometricRegister() },
                modifier = Modifier.fillMaxWidth(),
                gradientBrush = ButtonGradients.AccentViolet
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Registro Rápido con Huella", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
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

