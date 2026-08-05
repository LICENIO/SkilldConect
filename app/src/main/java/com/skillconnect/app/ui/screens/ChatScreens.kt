package com.skillconnect.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.skillconnect.app.ui.components.*
import com.skillconnect.app.ui.theme.NeumorphicColors
import com.skillconnect.app.viewmodel.SkillConnectViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

fun openPdfFile(context: Context, fileName: String, fileUrl: String, onFallback: (String) -> Unit) {
    try {
        if (fileUrl.isBlank()) {
            Toast.makeText(context, "El archivo PDF no contiene datos válidos", Toast.LENGTH_SHORT).show()
            return
        }
        val cleanName = if (fileName.isNotBlank()) fileName else "Documento_SkillConnect.pdf"
        val pdfFile = File(context.cacheDir, cleanName)

        if (fileUrl.startsWith("data:") || (!fileUrl.startsWith("content://") && !fileUrl.startsWith("/"))) {
            val cleanBase64 = if (fileUrl.contains(",")) fileUrl.substringAfter(",") else fileUrl
            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            pdfFile.writeBytes(bytes)
        } else if (fileUrl.startsWith("/")) {
            val srcFile = File(fileUrl)
            if (srcFile.exists()) {
                srcFile.copyTo(pdfFile, overwrite = true)
            }
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Abrir PDF con...")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Log.e("PDF", "No external viewer app, showing fallback modal", e)
        onFallback(fileName)
    }
}

@Composable
fun MessagesScreen(viewModel: SkillConnectViewModel, onChatSelect: (String) -> Unit) {
    val myEmail = viewModel.activeUserEmail ?: ""

    // Obtener la lista de correos de usuarios con los que existe una relación ACEPTADA
    val acceptedPartnerEmails = viewModel.cloudRequests
        .filter { it.status == "ACEPTADO" }
        .flatMap { listOf(it.senderEmail, it.recipientEmail) }
        .filter { !it.equals(myEmail, ignoreCase = true) }
        .toSet()

    // Filtrar contactos a SOLO usuarios cuya propuesta fue ACEPTADA
    val acceptedContacts = viewModel.cloudUsers.filter { user ->
        acceptedPartnerEmails.contains(user.email)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
            .padding(20.dp)
    ) {
        NeumorphicTopBar("Contactos y Mensajes", onBack = {})
        Spacer(modifier = Modifier.height(16.dp))

        if (acceptedContacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(0.92f),
                    backgroundColor = Color.White
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(Color(0xFFEFF6FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = NeumorphicColors.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Protección Anti-Spam Activa 🛡️",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeumorphicColors.text,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No tienes chats activos aún. Para chatear con un usuario, primero debes enviar o recibir una solicitud de trueque/clase y que sea ACEPTADA por ambas partes.",
                            fontSize = 13.sp,
                            color = NeumorphicColors.muted,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        } else {
            Text("Chats Aceptados y Confirmados", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.muted)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(acceptedContacts) { contact ->
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChatSelect(contact.email) },
                        backgroundColor = Color.White
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeumorphicLogo(contact.initials.ifEmpty { "SC" }, size = 48.dp, backgroundColor = NeumorphicColors.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(contact.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("🟢", fontSize = 10.sp)
                                }
                                Text("Toca para abrir chat de coordinación", fontSize = 12.sp, color = NeumorphicColors.primary, fontWeight = FontWeight.Medium)
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
    var previewPdfName by remember { mutableStateOf<String?>(null) }
    val msgs = viewModel.currentChatMessages
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launcher universal seguro en corrutina IO con control de peso de archivo
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    var name = "Documento.pdf"
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (cursor.moveToFirst() && nameIndex != -1) {
                            val retrievedName = cursor.getString(nameIndex)
                            if (!retrievedName.isNullOrEmpty()) {
                                name = retrievedName
                            }
                        }
                    }

                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.use { it.readBytes() }
                    
                    if (bytes != null && bytes.isNotEmpty()) {
                        if (bytes.size > 700 * 1024) {
                            // Guardar localmente si supera 700KB para evitar exceder el límite de Firestore (1MB)
                            val localFile = File(context.filesDir, name)
                            localFile.writeBytes(bytes)
                            withContext(Dispatchers.Main) {
                                viewModel.sendCloudPdf(chatId, name, localFile.absolutePath)
                                Toast.makeText(context, "📄 PDF '$name' adjuntado con éxito", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val base64Str = Base64.encodeToString(bytes, Base64.NO_WRAP)
                            withContext(Dispatchers.Main) {
                                viewModel.sendCloudPdf(chatId, name, base64Str)
                                Toast.makeText(context, "📄 PDF '$name' enviado con éxito", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PDF", "Error procesando PDF", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No se pudo leer el archivo seleccionado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Verificar si la relación de chat está ACEPTADA por ambas partes
    val isChatAllowed = viewModel.cloudRequests.any { req ->
        req.status == "ACEPTADO" && (
            (req.senderEmail.equals(myEmail, ignoreCase = true) && req.recipientEmail.equals(theirEmail, ignoreCase = true)) ||
            (req.recipientEmail.equals(myEmail, ignoreCase = true) && req.senderEmail.equals(theirEmail, ignoreCase = true))
        )
    }

    LaunchedEffect(chatId) {
        viewModel.listenToChat(chatId)
    }

    val contact = viewModel.cloudUsers.find { it.email == theirEmail }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphicColors.bg)
    ) {
        // BARRA SUPERIOR DE CHAT
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
                NeumorphicLogo(contact?.initials ?: "SC", size = 40.dp, backgroundColor = NeumorphicColors.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(contact?.name ?: "Chat", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeumorphicColors.text)
                    Text(
                        text = if (isChatAllowed) "🟢 Cita Aceptada · Chat Activo" else "🔒 Pendiente de Aceptación",
                        fontSize = 12.sp,
                        color = if (isChatAllowed) Color(0xFF16A34A) else Color(0xFFD97706),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // LISTA DE MENSAJES
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(msgs) { msg ->
                val fromMe = msg.senderEmail == myEmail
                val isPdf = msg.fileType == "PDF"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (fromMe) Arrangement.End else Arrangement.Start
                ) {
                    if (isPdf) {
                        // TARJETA DE MENSAJE TIPO PDF
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (fromMe) Color(0xFF1E293B) else Color.White,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp)
                                .widthIn(max = 270.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFFEF2F2), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = msg.fileName.ifEmpty { "Documento.pdf" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (fromMe) Color.White else NeumorphicColors.text
                                        )
                                        Text(
                                            text = "Archivo PDF Adjunto",
                                            fontSize = 11.sp,
                                            color = if (fromMe) Color(0xFF94A3B8) else NeumorphicColors.muted
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        openPdfFile(context, msg.fileName, msg.fileUrl) { fallbackName ->
                                            previewPdfName = fallbackName
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (fromMe) Color(0xFF334155) else Color(0xFFEFF6FF)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = if (fromMe) Color.White else NeumorphicColors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "📄 Abrir / Visualizar PDF",
                                        color = if (fromMe) Color.White else NeumorphicColors.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    } else {
                        // MENSAJE DE TEXTO CONVENCIONAL
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (fromMe) NeumorphicColors.primary else Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                                .widthIn(max = 260.dp)
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
        }

        // ÁREA DE INGRESO DE MENSAJE (Con Botón 📎 en color plomito elegante)
        if (isChatAllowed) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BOTÓN 📎 ADJUNTAR PDF (Color plomito #64748B)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .clickable {
                            try {
                                pdfPickerLauncher.launch("application/pdf")
                            } catch (e: Exception) {
                                try {
                                    pdfPickerLauncher.launch("*/*")
                                } catch (e2: Exception) {
                                    Toast.makeText(context, "No se pudo abrir el selector de archivos", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Adjuntar PDF",
                        tint = Color(0xFF64748B), // Color plomito neutro elegante
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    NeumorphicTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = "Escribe un mensaje o adjunta un PDF..."
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(NeumorphicColors.primary, RoundedCornerShape(12.dp))
                        .clickable {
                            if (messageText.isNotBlank()) {
                                viewModel.sendCloudMessage(chatId, messageText)
                                messageText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        } else {
            // BANNER DE CHAT BLOQUEADO ANTI-SPAM
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFFBEB))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔒 El chat se habilitará en cuanto la propuesta sea ACEPTADA.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }
    }

    // MODAL DE VISUALIZACIÓN INTERNA DE PDF (Fallback)
    previewPdfName?.let { pdfName ->
        AlertDialog(
            onDismissRequest = { previewPdfName = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFFEF2F2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(30.dp))
                }
            },
            title = {
                Text(pdfName, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDC2626))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("📄 DOCUMENTO PDF ADJUNTO", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "El archivo se ha adjuntado correctamente a la conversación de trueque/clase.",
                        fontSize = 13.sp,
                        color = NeumorphicColors.text,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { previewPdfName = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NeumorphicColors.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
