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
import kotlinx.coroutines.flow.collect
import com.skillconnect.app.data.repository.FirebaseUserRepository
import com.skillconnect.app.data.repository.FirebaseChatRepository
import com.skillconnect.app.data.repository.UserProfile
import com.skillconnect.app.data.repository.CloudMessage
import com.skillconnect.app.data.repository.AuthFailure
import com.skillconnect.app.data.repository.FirebaseAuthRepository

import com.skillconnect.app.data.repository.FirebaseCalendarRepository
import com.skillconnect.app.data.repository.FirebaseExchangeRepository
import com.skillconnect.app.data.repository.FirebaseReviewRepository
import com.skillconnect.app.data.repository.CloudReview
import com.skillconnect.app.data.repository.CloudCourse
import com.skillconnect.app.data.repository.CloudCalendarEvent
import com.skillconnect.app.data.repository.CloudExchange
class SkillConnectViewModel(
    private val repository: SkillConnectRepository
) : ViewModel() {
    private val authRepository = FirebaseAuthRepository()
    private val firebaseUserRepository = FirebaseUserRepository()
    private val firebaseChatRepository = FirebaseChatRepository()
    private val firebaseCalendarRepository = FirebaseCalendarRepository()
    private val firebaseExchangeRepository = FirebaseExchangeRepository()
    private val firebaseReviewRepository = FirebaseReviewRepository()

    var cloudUsers by mutableStateOf<List<UserProfile>>(emptyList())
        private set
    var currentCloudUser by mutableStateOf<UserProfile?>(null)
        private set

    var currentChatMessages by mutableStateOf<List<CloudMessage>>(emptyList())
        private set

    
    var cloudExchanges by mutableStateOf<List<CloudExchange>>(emptyList())
        private set
    var cloudCalendar by mutableStateOf<Map<String, List<CloudCalendarEvent>>>(emptyMap())
        private set

    init {
        viewModelScope.launch {
            firebaseUserRepository.getRealtimeUsers().collect { users ->
                cloudUsers = users
                activeUserEmail?.let { email -> 
                    currentCloudUser = users.find { it.email == email } 
                }
            }
        }
        viewModelScope.launch {
            firebaseExchangeRepository.getExchanges().collect { exchangesList ->
                cloudExchanges = exchangesList
            }
        }
    }


    
    var selectedMentorReviews by mutableStateOf<List<CloudReview>>(emptyList())
        private set
var selectedCloudUserEmail by mutableStateOf("")
    var selectedMentorId by mutableStateOf(1) // For legacy UI components
    var activeChatId by mutableStateOf(1)

    // Estado del usuario activo
    var activeUserEmail by mutableStateOf<String?>(null)
        private set
    var currentUser by mutableStateOf<UserEntity?>(null)
        private set

    // Usuario previamente guardado (para login rápido Yape)
    var savedUser by mutableStateOf<UserEntity?>(null)
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
            val firebaseEmail = authRepository.currentUser?.email?.trim()?.lowercase()
            val lastUser = repository.getLastRegisteredUser()
            if (firebaseEmail != null) {
                activateAccount(firebaseEmail, rememberMe = true, fallbackName = authRepository.currentUser?.displayName)
            } else if (lastUser != null) {
                savedUser = lastUser
                // NOTA: Ya no logueamos automáticamente (activeUserEmail = lastUser.email)
                // para obligar al usuario a usar la huella (estilo Yape).
            }
        }
    }

    suspend fun refreshAll() {
        categories = repository.getCategories()
        mentors = repository.getMentors()
        exchanges = repository.getExchanges()

        val email = activeUserEmail
        if (email != null) {
                        viewModelScope.launch {
                firebaseCalendarRepository.getEvents(email, "Clases").collect { ev ->
                    val newMap = cloudCalendar.toMutableMap()
                    newMap["Clases"] = ev
                    cloudCalendar = newMap
                }
            }
            viewModelScope.launch {
                firebaseCalendarRepository.getEvents(email, "Intercambios").collect { ev ->
                    val newMap = cloudCalendar.toMutableMap()
                    newMap["Intercambios"] = ev
                    cloudCalendar = newMap
                }
            }
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
    
    sealed class LoginResult {
        object Success : LoginResult()
        object UserNotFound : LoginResult()
        object IncorrectPassword : LoginResult()
        object NetworkError : LoginResult()
    }

    sealed class RegisterResult {
        object Success : RegisterResult()
        object InvalidData : RegisterResult()
        object EmailAlreadyInUse : RegisterResult()
        object InvalidEmail : RegisterResult()
        object NetworkError : RegisterResult()
    }

    private fun initialsFromName(name: String): String {
        return name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifEmpty { "SC" }
    }

    private suspend fun activateAccount(
        cleanEmail: String,
        rememberMe: Boolean,
        fallbackName: String? = null
    ): UserEntity {
        val cloudProfile = firebaseUserRepository.getUserProfile(cleanEmail)
        val displayName = cloudProfile?.name ?: fallbackName?.takeIf { it.isNotBlank() } ?: "Usuario SkillConnect"
        val initials = cloudProfile?.initials?.takeIf { it.isNotBlank() } ?: initialsFromName(displayName)
        val role = cloudProfile?.role ?: "Ambos"

        var user = repository.getUserByEmail(cleanEmail)
        if (user == null) {
            user = UserEntity(
                email = cleanEmail,
                name = displayName,
                password = "firebase_managed",
                role = role,
                initials = initials
            )
            repository.registerUser(user)
            repository.initializeUserSeeds(cleanEmail)
        } else if (user.name != displayName || user.initials != initials || user.role != role) {
            user = user.copy(name = displayName, initials = initials, role = role)
            repository.updateUser(user)
        }

        if (rememberMe) {
            repository.saveLastLoggedInEmail(user.email)
        } else {
            repository.clearLastLoggedInEmail()
        }

        activeUserEmail = user.email
        currentCloudUser = cloudProfile
        currentUser = user
        savedUser = if (rememberMe) user else null
        refreshAll()
        return user
    }

    suspend fun loginWithEmail(email: String, password: String, rememberMe: Boolean = true): LoginResult {
        val cleanEmail = email.trim().lowercase()
        val result = authRepository.login(cleanEmail, password)
        
        return if (result.isSuccess) {
            activateAccount(cleanEmail, rememberMe, fallbackName = result.getOrNull()?.displayName)
            LoginResult.Success
        } else {
            when (authRepository.loginFailure(result.exceptionOrNull())) {
                AuthFailure.UserNotFound -> LoginResult.UserNotFound
                AuthFailure.InvalidCredentials -> LoginResult.IncorrectPassword
                else -> LoginResult.NetworkError
            }
        }
    }

    suspend fun sendPasswordReset(email: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty()) return false
        return authRepository.sendPasswordReset(cleanEmail).isSuccess
    }


    suspend fun loginWithBiometrics(): Boolean {
        val lastUser = repository.getLastRegisteredUser()
        val firebaseEmail = authRepository.currentUser?.email?.trim()?.lowercase()
        return if (lastUser != null && firebaseEmail == lastUser.email) {
            activateAccount(lastUser.email, rememberMe = true, fallbackName = lastUser.name)
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
        
        return registerUser(name, email, password, "Ambos") == RegisterResult.Success
    }

    suspend fun registerAfterBiometric(name: String, password: String): Boolean {
        val randomId = (10000..99999).random()
        val email = "huella$randomId@skillconnect.app"
        return registerUser(name, email, password, "Ambos") == RegisterResult.Success
    }


    suspend fun registerUser(name: String, email: String, password: String, role: String): RegisterResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty() || password.length < 6 || name.trim().isEmpty()) return RegisterResult.InvalidData

        val existing = repository.getUserByEmail(cleanEmail)
        if (existing != null) return RegisterResult.EmailAlreadyInUse // Correo ya registrado localmente

        val result = authRepository.register(cleanEmail, password)
        if (result.isFailure) {
            return when (authRepository.registerFailure(result.exceptionOrNull())) {
                AuthFailure.EmailAlreadyInUse -> RegisterResult.EmailAlreadyInUse
                AuthFailure.InvalidCredentials -> RegisterResult.InvalidEmail
                else -> RegisterResult.NetworkError
            }
        }

        val initials = initialsFromName(name)

        val newUser = UserEntity(
            email = cleanEmail,
            name = name.trim(),
            password = "firebase_managed",
            role = role,
            initials = initials
        )

                val userProfile = UserProfile(
            email = cleanEmail,
            name = name.trim(),
            initials = initials,
            role = role
        )
        viewModelScope.launch {
            firebaseUserRepository.saveUserProfile(userProfile)
        }

        repository.registerUser(newUser)
        repository.initializeUserSeeds(cleanEmail) // Crear semillas específicas
        repository.saveLastLoggedInEmail(newUser.email)
        
        activeUserEmail = newUser.email
        currentCloudUser = firebaseUserRepository.getUserProfile(cleanEmail)
        currentUser = newUser
        savedUser = newUser
        refreshAll()
        return RegisterResult.Success
    }

    fun updateUserName(newName: String) {
        val current = currentUser ?: return
        val initials = newName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
        val updatedUser = current.copy(name = newName, initials = initials)
        
        viewModelScope.launch {
            repository.updateUser(updatedUser)
            currentUser = updatedUser
            savedUser = updatedUser
            refreshAll()
        }
    }

    
    fun updateCloudProfile(profile: UserProfile) {
        if (profile.email.isBlank()) return
        viewModelScope.launch {
            firebaseUserRepository.saveUserProfile(profile)
            currentCloudUser = profile
        }
    }

    fun listenToChat(chatId: String) {
        viewModelScope.launch {
            firebaseChatRepository.getMessages(chatId).collect { msgs ->
                currentChatMessages = msgs
            }
        }
    }

    fun sendCloudMessage(chatId: String, text: String) {
        activeUserEmail?.let {
            firebaseChatRepository.sendMessage(chatId, it, text)
        }
    }

    fun logout() {
        authRepository.logout()
        activeUserEmail = null
        currentUser = null
        viewModelScope.launch {
            refreshAll()
        }
    }

    // --- ACCIONES DEL USUARIO CONECTADO ---

    fun selectedMentor(): com.skillconnect.app.data.model.Mentor {
        val user = cloudUsers.find { it.email == selectedCloudUserEmail }
        return if (user != null) {
            com.skillconnect.app.data.model.Mentor(
                id = 1,
                name = user.name,
                initials = user.initials.ifEmpty { "VR" },
                specialty = if (user.teachSkills.isNotEmpty()) user.teachSkills.first() else "General",
                rating = 5.0,
                reviews = 0,
                price = user.hourlyRate,
                mode = listOf("Virtual"),
                type = if (user.hourlyRate == 0) "intercambio" else "pagado",
                experience = user.description,
                description = user.description,
                availability = listOf("Por coordinar"),
                accentColor = "#4CAF50"
            )
        } else {
            com.skillconnect.app.data.model.Mentor(
                id = 1, name = "Cargando...", initials = "", specialty = "", rating = 0.0, reviews = 0, price = 0, mode = emptyList(), type = "", experience = "", description = "", availability = emptyList(), accentColor = "#7C5CFF"
            )
        }
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
        val userProfile = cloudUsers.find { it.email == selectedCloudUserEmail }
        
        viewModelScope.launch {
            val event = CloudCalendarEvent(
                title = "Clase con ${userProfile?.name ?: "Mentor"}",
                time = "$date - $hour",
                tag = mode,
                initials = userProfile?.initials ?: "VR",
                categoryTab = "Clases"
            )
            firebaseCalendarRepository.addEvent(email, event)
        }
    }
    fun requestExchange(mentorId: Int, teachSkill: String, learnSkill: String, message: String) {
        val email = activeUserEmail ?: return
        val userProfile = cloudUsers.find { it.email == selectedCloudUserEmail }

        viewModelScope.launch {
            val event = CloudCalendarEvent(
                title = "$teachSkill por $learnSkill con ${userProfile?.name ?: "Usuario"}",
                time = "Petición Pendiente",
                tag = "Intercambio",
                initials = userProfile?.initials ?: "VR",
                categoryTab = "Intercambios"
            )
            firebaseCalendarRepository.addEvent(email, event)

            val exchange = CloudExchange(
                title = "${currentUser?.name ?: "Usuario"} enseña $teachSkill",
                subtitle = "Busca $learnSkill",
                initials = currentUser?.initials ?: "VR"
            )
            firebaseExchangeRepository.addExchange(exchange)
        }
    }

fun addSkill(name: String, level: String) {
        val email = activeUserEmail ?: return
        viewModelScope.launch {
            repository.insertSkill(email, UserSkill(name, level))
            refreshAll()
        }
    }

    
    fun saveProfile(updatedProfile: UserProfile) {
        if (updatedProfile.email.isBlank()) return
        viewModelScope.launch {
            firebaseUserRepository.saveUserProfile(updatedProfile)
            currentCloudUser = updatedProfile

            val localUser = currentUser
            if (localUser != null) {
                val updatedLocalUser = localUser.copy(
                    name = updatedProfile.name,
                    role = updatedProfile.role,
                    initials = updatedProfile.initials.ifBlank { initialsFromName(updatedProfile.name) }
                )
                repository.updateUser(updatedLocalUser)
                currentUser = updatedLocalUser
                savedUser = updatedLocalUser
            }
        }
    }


    fun fetchReviewsForMentor(email: String) {
        viewModelScope.launch {
            firebaseReviewRepository.getReviews(email).collect { revs ->
                selectedMentorReviews = revs
            }
        }
    }

    fun addReview(targetEmail: String, rating: Double, comment: String) {
        val myProfile = currentCloudUser ?: return
        viewModelScope.launch {
            val review = CloudReview(
                reviewerEmail = myProfile.email,
                reviewerName = myProfile.name,
                rating = rating,
                comment = comment
            )
            firebaseReviewRepository.addReview(targetEmail, review)
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
