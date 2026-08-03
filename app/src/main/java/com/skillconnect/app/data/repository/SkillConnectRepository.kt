package com.skillconnect.app.data.repository

import android.content.Context
import com.skillconnect.app.data.model.*

class SkillConnectRepository(private val db: SkillConnectDatabase, private val context: Context) {
    private val prefs = context.getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE)

    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val mentorDao = db.mentorDao()
    private val exchangeDao = db.exchangeDao()
    private val chatDao = db.chatDao()
    private val calendarDao = db.calendarDao()
    private val skillDao = db.skillDao()
    private val learningDao = db.learningDao()
    private val achievementDao = db.achievementDao()
    private val notificationDao = db.notificationDao()

    suspend fun initializeIfNeeded() {
        // 1. Población global de categorías
        if (categoryDao.getCount() == 0) {
            val mockCategories = listOf(
                Category("Programación", "{ }", "#246BFE"),
                Category("Idiomas", "Aa", "#19A974"),
                Category("Arte", "*", "#E05297"),
                Category("Música", "♪", "#7C5CFF"),
                Category("Matemáticas", "√", "#FF8A3D"),
                Category("Cocina", "+", "#E0A100"),
                Category("Tecnología", "</>", "#00A3A3"),
                Category("Marketing", "%", "#EF4444")
            ).map { CategoryEntity.fromDomain(it) }
            categoryDao.insertAll(mockCategories)
        }

    }

    // Inicializar datos semilla para un usuario específico (para nuevos registrados)
    suspend fun initializeUserSeeds(email: String) {
        // REMOVED FOR FIREBASE MIGRATION
    }

    // --- MÉTODOS DE BASE DE DATOS FILTRADOS POR USUARIO ---

    suspend fun getUser(email: String, password: String): UserEntity? = userDao.getUser(email, password)

    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    fun saveLastLoggedInEmail(email: String) {
        prefs.edit().putString("last_logged_in_email", email).apply()
    }

    fun clearLastLoggedInEmail() {
        prefs.edit().remove("last_logged_in_email").apply()
    }


    suspend fun getLastRegisteredUser(): UserEntity? {
        val lastEmail = prefs.getString("last_logged_in_email", null)
        if (lastEmail != null) {
            val user = userDao.getUserByEmail(lastEmail)
            if (user != null) return user
        }
        return userDao.getLastRegisteredUser()
    }

    suspend fun registerUser(user: UserEntity) {
        userDao.insert(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.insert(user)
    }


    suspend fun getCategories(): List<Category> = categoryDao.getAll().map { it.toDomain() }

    suspend fun getMentors(): List<Mentor> = mentorDao.getAll().map { it.toDomain() }

    suspend fun getMentorById(id: Int): Mentor = mentorDao.getMentorById(id)?.toDomain() ?: getMentors().first()

    suspend fun getExchanges(): List<Exchange> = exchangeDao.getAll().map { it.toDomain() }

    suspend fun getChats(email: String): List<ChatThread> {
        return chatDao.getThreads(email).map { threadEntity ->
            val messages = chatDao.getMessagesForThread(email, threadEntity.id).map { it.toDomain() }
            threadEntity.toDomain(messages)
        }
    }

    suspend fun getCalendar(email: String): Map<String, List<CalendarEvent>> {
        return mapOf(
            "Clases" to calendarDao.getEventsByTab(email, "Clases").map { it.toDomain() },
            "Mentorías" to calendarDao.getEventsByTab(email, "Mentorías").map { it.toDomain() },
            "Intercambios" to calendarDao.getEventsByTab(email, "Intercambios").map { it.toDomain() }
        )
    }

    suspend fun getSkills(email: String): List<UserSkill> = skillDao.getAll(email).map { it.toDomain() }

    suspend fun getLearning(email: String): List<LearningItem> = learningDao.getAll(email).map { it.toDomain() }

    suspend fun getAchievements(email: String): List<Achievement> = achievementDao.getAll(email).map { it.toDomain() }

    suspend fun getNotifications(email: String): List<NotificationItem> = notificationDao.getAll(email).map { it.toDomain() }

    suspend fun insertMessage(email: String, chatId: Int, fromMe: Boolean, text: String) {
        chatDao.insertMessage(ChatMessageEntity(userEmail = email, chatId = chatId, fromMe = fromMe, text = text))
        chatDao.updateThreadLastMessage(email, chatId, text, "Ahora")
        if (!fromMe) {
            chatDao.incrementUnread(email, chatId)
        }
    }

    suspend fun insertCalendarEvent(email: String, tab: String, event: CalendarEvent) {
        calendarDao.insert(CalendarEventEntity.fromDomain(email, tab, event))
    }

    suspend fun insertExchange(exchange: Exchange) {
        exchangeDao.insert(ExchangeEntity.fromDomain(exchange))
    }

    suspend fun insertNotification(email: String, title: String, description: String, time: String, unread: Boolean) {
        notificationDao.insert(NotificationEntity(userEmail = email, title = title, description = description, time = time, unread = unread))
    }

    suspend fun insertSkill(email: String, skill: UserSkill) {
        skillDao.insert(UserSkillEntity.fromDomain(email, skill))
    }

    suspend fun clearUnread(email: String, chatId: Int) {
        chatDao.clearUnread(email, chatId)
    }
}
