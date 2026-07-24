package com.skillconnect.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillconnect.app.data.model.*
import com.skillconnect.app.data.repository.SkillConnectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SkillConnectViewModel(
    private val repository: SkillConnectRepository
) : ViewModel() {
    var selectedMentorId by mutableStateOf(1)
    var activeChatId by mutableStateOf(1)

    // Estado del usuario activo
    var activeUserEmail by mutableStateOf<String?>(null)
        private set
    var currentUser by mutableStateOf<UserEntity?>(null)
        private set

    // Estados reactivos para la UI
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set
    var mentors by mutableStateOf<List<Mentor>>(emptyList())
        private set
    var exchanges by mutableStateOf<List<Exchange>>(emptyList())
        private set
    var chats by mutableStateOf<List<ChatThread>>(emptyList())
        private set
    var calendar by mutableStateOf<Map<String, List<CalendarEvent>>>(emptyMap())
        private set
    var skills by mutableStateOf<List<UserSkill>>(emptyList())
        private set
    var learning by mutableStateOf<List<LearningItem>>(emptyList())
        private set
    var achievements by mutableStateOf<List<Achievement>>(emptyList())
        private set
    var notifications by mutableStateOf<List<NotificationItem>>(emptyList())
        private set

    fun loadData() {
        viewModelScope.launch {
            repository.initializeIfNeeded()
            // Por defecto, intentar iniciar con el último usuario registrado
            val lastUser = repository.getLastRegisteredUser()
            if (lastUser != null) {
                activeUserEmail = lastUser.email
                currentUser = lastUser
                refreshAll()
            }
        }
    }

    suspend fun refreshAll() {
        categories = repository.getCategories()
        mentors = repository.getMentors()
        exchanges = repository.getExchanges()

        val email = activeUserEmail
        if (email != null) {
            chats = repository.getChats(email)
            calendar = repository.getCalendar(email)
            skills = repository.getSkills(email)
            learning = repository.getLearning(email)
            achievements = repository.getAchievements(email)
            notifications = repository.getNotifications(email)
        } else {
            chats = emptyList()
            calendar = emptyMap()
            skills = emptyList()
            learning = emptyList()
            achievements = emptyList()
            notifications = emptyList()
        }
    }

    // --- LÓGICA DE INICIO DE SESIÓN Y REGISTRO ---

    suspend fun loginWithEmail(email: String, password: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val user = repository.getUser(cleanEmail, password)
        return if (user != null) {
            activeUserEmail = user.email
            currentUser = user
            refreshAll()
            true
        } else {
            false
        }
    }

    suspend fun loginWithBiometrics(): Boolean {
        val lastUser = repository.getLastRegisteredUser()
        return if (lastUser != null) {
            activeUserEmail = lastUser.email
            currentUser = lastUser
            refreshAll()
            true
        } else {
            false
        }
    }

    suspend fun quickRegisterWithBiometrics(): Boolean {
        val randomId = (10000..99999).random()
        val email = "huella$randomId@skillconnect.app"
        val name = "Usuario Rápido"
        val password = "biometric_quick_password"
        
        return registerUser(name, email, password, "Ambos")
    }


    suspend fun registerUser(name: String, email: String, password: String, role: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty() || password.length < 4 || name.trim().isEmpty()) return false

        val existing = repository.getUserByEmail(cleanEmail)
        if (existing != null) return false // Correo ya registrado

        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .let { if (it.isEmpty()) "VR" else it }

        val newUser = UserEntity(
            email = cleanEmail,
            name = name.trim(),
            password = password,
            role = role,
            initials = initials
        )

        repository.registerUser(newUser)
        repository.initializeUserSeeds(cleanEmail) // Crear semillas específicas
        
        activeUserEmail = newUser.email
        currentUser = newUser
        refreshAll()
        return true
    }

    fun logout() {
        activeUserEmail = null
        currentUser = null
        viewModelScope.launch {
            refreshAll()
        }
    }

    // --- ACCIONES DEL USUARIO CONECTADO ---

    fun selectedMentor(): Mentor {
        return mentors.firstOrNull { it.id == selectedMentorId }
            ?: mentors.firstOrNull()
            ?: Mentor(
                id = 1,
                name = "Carlos Medina",
                initials = "CM",
                specialty = "Guitarra y composición",
                rating = 4.9,
                reviews = 128,
                price = 45,
                mode = listOf("Virtual", "Presencial"),
                type = "pagado",
                experience = "7 años enseñando música",
                description = "Músico profesional con formación en conservatorio.",
                availability = listOf("Lun 5pm", "Mié 6pm"),
                accentColor = "#7C5CFF"
            )
    }

    fun searchMentors(query: String, filter: String): List<Mentor> {
        val normalized = query.trim().lowercase()
        return mentors.filter { mentor ->
            val matchesQuery = normalized.isBlank() ||
                mentor.name.lowercase().contains(normalized) ||
                mentor.specialty.lowercase().contains(normalized)
            val matchesFilter = filter == "Todos" ||
                mentor.type.equals(filter.lowercase(), ignoreCase = true) ||
                mentor.mode.any { it.equals(filter.lowercase(), ignoreCase = true) }
            matchesQuery && matchesFilter
        }
    }

    fun chatById(id: Int) = chats.firstOrNull { it.id == id } 
        ?: chats.firstOrNull() 
        ?: ChatThread(1, "Carlos Medina", "CM", "", "", 0, mutableListOf())

    fun addMessage(text: String) {
        val cleanText = text.trim()
        val email = activeUserEmail ?: return
        if (cleanText.isEmpty()) return
        
        viewModelScope.launch {
            repository.insertMessage(email, activeChatId, fromMe = true, text = cleanText)
            refreshAll()
            
            delay(1000)
            repository.insertMessage(email, activeChatId, fromMe = false, text = "Genial, te confirmo en breve.")
            refreshAll()
        }
    }

    fun bookClass(mentorId: Int, date: String, hour: String, mode: String) {
        val email = activeUserEmail ?: return
        viewModelScope.launch {
            val mentor = repository.getMentorById(mentorId)
            val event = CalendarEvent(
                title = "Clase con ${mentor.name}",
                time = "$date - $hour",
                tag = mode,
                initials = mentor.initials
            )
            repository.insertCalendarEvent(email, "Clases", event)

            val notification = NotificationItem(
                title = "Clase reservada",
                description = "Tu clase de ${mentor.specialty} con ${mentor.name} ha sido agendada.",
                time = "Hace un momento",
                unread = true
            )
            repository.insertNotification(email, notification.title, notification.description, notification.time, notification.unread)
            
            refreshAll()
        }
    }

    fun requestExchange(mentorId: Int, teachSkill: String, learnSkill: String, message: String) {
        val email = activeUserEmail ?: return
        viewModelScope.launch {
            val mentor = repository.getMentorById(mentorId)
            
            val event = CalendarEvent(
                title = "$teachSkill por $learnSkill con ${mentor.name}",
                time = "Petición Pendiente",
                tag = "Intercambio",
                initials = mentor.initials
            )
            repository.insertCalendarEvent(email, "Intercambios", event)

            val exchange = Exchange(
                title = "${currentUser?.name ?: "Usuario"} enseña $teachSkill",
                subtitle = "Busca $learnSkill",
                initials = currentUser?.initials ?: "VR"
            )
            repository.insertExchange(exchange)

            val notification = NotificationItem(
                title = "Solicitud de intercambio",
                description = "Enviada a ${mentor.name} para intercambiar $teachSkill por $learnSkill",
                time = "Hace un momento",
                unread = true
            )
            repository.insertNotification(email, notification.title, notification.description, notification.time, notification.unread)

            refreshAll()
        }
    }

    fun addSkill(name: String, level: String) {
        val email = activeUserEmail ?: return
        viewModelScope.launch {
            repository.insertSkill(email, UserSkill(name, level))
            refreshAll()
        }
    }

    fun clearChatUnread(id: Int) {
        val email = activeUserEmail ?: return
        viewModelScope.launch {
            repository.clearUnread(email, id)
            refreshAll()
        }
    }
}
