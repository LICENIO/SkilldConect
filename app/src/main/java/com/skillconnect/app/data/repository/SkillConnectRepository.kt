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

        // 2. Población global de mentores
        if (mentorDao.getCount() == 0) {
            val mockMentors = listOf(
                Mentor(
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
                    description = "Músico profesional con formación en conservatorio. Ayuda a principiantes a perder el miedo al instrumento con clases prácticas.",
                    availability = listOf("Lun 5pm", "Mié 6pm", "Sáb 10am"),
                    accentColor = "#7C5CFF"
                ),
                Mentor(
                    id = 2,
                    name = "Ana Fuentes",
                    initials = "AF",
                    specialty = "Excel y análisis de datos",
                    rating = 4.8,
                    reviews = 96,
                    price = 0,
                    mode = listOf("Virtual"),
                    type = "gratis",
                    experience = "5 años en finanzas corporativas",
                    description = "Contadora apasionada por enseñar Excel de forma clara, práctica y orientada a casos reales.",
                    availability = listOf("Mar 7pm", "Jue 7pm", "Dom 11am"),
                    accentColor = "#19A974"
                ),
                Mentor(
                    id = 3,
                    name = "Bruno Salas",
                    initials = "BS",
                    specialty = "Python y ciencia de datos",
                    rating = 5.0,
                    reviews = 210,
                    price = 60,
                    mode = listOf("Virtual"),
                    type = "pagado",
                    experience = "Ingeniero de software, 9 años",
                    description = "Enseñanza de Python desde cero hasta proyectos de machine learning con ejercicios guiados.",
                    availability = listOf("Lun 8pm", "Vie 6pm", "Sáb 3pm"),
                    accentColor = "#246BFE"
                ),
                Mentor(
                    id = 4,
                    name = "Lucía Torres",
                    initials = "LT",
                    specialty = "Inglés conversacional",
                    rating = 4.7,
                    reviews = 74,
                    price = 0,
                    mode = listOf("Virtual", "Presencial"),
                    type = "intercambio",
                    experience = "Traductora certificada",
                    description = "Práctica conversacional para ganar confianza, mejorar pronunciación y sostener conversaciones reales.",
                    availability = listOf("Mar 4pm", "Jue 5pm"),
                    accentColor = "#FF8A3D"
                ),
                Mentor(
                    id = 5,
                    name = "Diego Ramos",
                    initials = "DR",
                    specialty = "Fotografía digital",
                    rating = 4.9,
                    reviews = 58,
                    price = 35,
                    mode = listOf("Presencial"),
                    type = "pagado",
                    experience = "Fotógrafo freelance",
                    description = "Clases de composición, luz y edición con salidas fotográficas y crítica personalizada.",
                    availability = listOf("Sáb 9am", "Dom 9am"),
                    accentColor = "#E05297"
                ),
                Mentor(
                    id = 6,
                    name = "Marina Chávez",
                    initials = "MC",
                    specialty = "Diseño gráfico e Illustrator",
                    rating = 4.6,
                    reviews = 41,
                    price = 0,
                    mode = listOf("Virtual"),
                    type = "intercambio",
                    experience = "Diseñadora freelance",
                    description = "Enseñanza de composición, marca visual e Illustrator a cambio de guitarra o idiomas.",
                    availability = listOf("Lun 6pm", "Mié 5pm"),
                    accentColor = "#00A3A3"
                )
            ).map { MentorEntity.fromDomain(it) }
            mentorDao.insertAll(mockMentors)
        }

        // 3. Población global de intercambios en la Home
        if (exchangeDao.getAll().isEmpty()) {
            val mockExchanges = listOf(
                Exchange("Camila enseña Yoga", "Busca Piano", "CY"),
                Exchange("Jorge enseña Programación web", "Busca Alemán", "JW"),
                Exchange("Sofía enseña Repostería", "Busca Marketing digital", "SR")
            ).map { ExchangeEntity.fromDomain(it) }
            exchangeDao.insertAll(mockExchanges)
        }
    }

    // Inicializar datos semilla para un usuario específico (para nuevos registrados)
    suspend fun initializeUserSeeds(email: String) {
        // Evitar duplicaciones
        if (skillDao.getAll(email).isNotEmpty()) return

        // Habilidades semilla
        val mockSkills = listOf(
            UserSkill("Diseño gráfico", "Avanzado"),
            UserSkill("Excel", "Intermedio"),
            UserSkill("Fotografía", "Intermedio"),
            UserSkill("Inglés", "Principiante")
        ).map { UserSkillEntity.fromDomain(email, it) }
        skillDao.insertAll(mockSkills)

        // Cursos semilla
        val mockLearning = listOf(
            LearningItem("Guitarra para principiantes", "Carlos Medina", 65),
            LearningItem("Python desde cero", "Bruno Salas", 30),
            LearningItem("Fotografía digital", "Diego Ramos", 88)
        ).map { LearningItemEntity.fromDomain(email, it) }
        learningDao.insertAll(mockLearning)

        // Logros semilla
        val mockAchievements = listOf(
            Achievement("Aprendiz", "Completa tu primera clase", true),
            Achievement("Mentor", "Enseña 10 horas", true),
            Achievement("Experto", "Alcanza 4.9 de calificación", true),
            Achievement("Colaborador", "Realiza 5 intercambios", true),
            Achievement("Racha semanal", "Conéctate 7 días seguidos", false),
            Achievement("Políglota", "Aprende 3 idiomas distintos", false)
        ).map { AchievementEntity.fromDomain(email, it) }
        achievementDao.insertAll(mockAchievements)

        // Notificaciones semilla
        val mockNotifications = listOf(
            NotificationItem("Nueva solicitud de intercambio", "Marina quiere intercambiar diseño por guitarra", "Hace 10 min", true),
            NotificationItem("Nuevo mensaje de Carlos", "Perfecto, nos vemos el sábado a las 10am", "Hace 25 min", true),
            NotificationItem("Pago confirmado", "Tu clase con Bruno fue pagada exitosamente", "Hace 2 h", true),
            NotificationItem("Recordatorio de clase", "Fotografía con Diego es mañana a las 9am", "Hace 5 h", false)
        ).map { NotificationEntity.fromDomain(email, it) }
        notificationDao.insertAll(mockNotifications)

        // Calendario semilla
        val mockClasses = listOf(
            CalendarEvent("Guitarra con Carlos Medina", "Hoy - 4:00 PM", "Virtual", "CM"),
            CalendarEvent("Fotografía con Diego Ramos", "Mañana - 9:00 AM", "Presencial", "DR")
        )
        val mockMentorias = listOf(
            CalendarEvent("Excel avanzado con Ana Fuentes", "Jue - 7:00 PM", "Gratuita", "AF")
        )
        val mockIntercambios = listOf(
            CalendarEvent("Diseño por guitarra con Marina Chávez", "Sáb - 11:00 AM", "Intercambio", "MC")
        )
        calendarDao.insertAll(mockClasses.map { CalendarEventEntity.fromDomain(email, "Clases", it) })
        calendarDao.insertAll(mockMentorias.map { CalendarEventEntity.fromDomain(email, "Mentorías", it) })
        calendarDao.insertAll(mockIntercambios.map { CalendarEventEntity.fromDomain(email, "Intercambios", it) })

        // Chats semilla
        val mockThreads = listOf(
            ChatThread(
                1,
                "Carlos Medina",
                "CM",
                "Perfecto, nos vemos el sábado a las 10am",
                "9:41",
                2,
                mutableListOf(
                    ChatMessage(false, "Hola Valeria, vi tu solicitud de clase de guitarra."),
                    ChatMessage(true, "Hola Carlos, me encantaría aprender contigo."),
                    ChatMessage(false, "Tengo disponibilidad este sábado a las 10am. ¿Te sirve?"),
                    ChatMessage(true, "Me sirve perfecto."),
                    ChatMessage(false, "Perfecto, nos vemos el sábado a las 10am.")
                )
            ),
            ChatThread(
                2,
                "Ana Fuentes",
                "AF",
                "Te comparto la plantilla de Excel",
                "Ayer",
                0,
                mutableListOf(
                    ChatMessage(false, "Hola, aquí está el resumen de la clase de ayer."),
                    ChatMessage(false, "Te comparto la plantilla de Excel que armamos."),
                    ChatMessage(true, "Muchas gracias Ana, quedó muy clara la explicación.")
                )
            ),
            ChatThread(
                3,
                "Bruno Salas",
                "BS",
                "¿Pudiste correr el script?",
                "Ayer",
                1,
                mutableListOf(
                    ChatMessage(false, "¿Pudiste correr el script que revisamos?"),
                    ChatMessage(true, "Sí, funcionó perfecto. ¡Gracias!")
                )
            )
        )
        chatDao.insertThreads(mockThreads.map { ChatThreadEntity.fromDomain(email, it) })
        mockThreads.forEach { thread ->
            chatDao.insertMessages(thread.messages.map { ChatMessageEntity.fromDomain(email, thread.id, it) })
        }
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
