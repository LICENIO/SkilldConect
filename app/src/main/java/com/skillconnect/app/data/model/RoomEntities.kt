package com.skillconnect.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val password: String,
    val role: String, // "Aprender", "Enseñar", "Ambos"
    val initials: String,
    val rating: Double = 4.9,
    val classesCount: Int = 32,
    val exchangesCount: Int = 18
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val name: String,
    val icon: String,
    val accentColor: String
) {
    fun toDomain() = Category(name, icon, accentColor)
    companion object {
        fun fromDomain(domain: Category) = CategoryEntity(domain.name, domain.icon, domain.accentColor)
    }
}

@Entity(tableName = "mentors")
data class MentorEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val initials: String,
    val specialty: String,
    val rating: Double,
    val reviews: Int,
    val price: Int,
    val modeCsv: String,
    val type: String,
    val experience: String,
    val description: String,
    val availabilityCsv: String,
    val accentColor: String
) {
    fun toDomain(): Mentor {
        val modes = if (modeCsv.isBlank()) emptyList() else modeCsv.split(",")
        val avails = if (availabilityCsv.isBlank()) emptyList() else availabilityCsv.split(",")
        return Mentor(id, name, initials, specialty, rating, reviews, price, modes, type, experience, description, avails, accentColor)
    }

    companion object {
        fun fromDomain(domain: Mentor) = MentorEntity(
            id = domain.id,
            name = domain.name,
            initials = domain.initials,
            specialty = domain.specialty,
            rating = domain.rating,
            reviews = domain.reviews,
            price = domain.price,
            modeCsv = domain.mode.joinToString(","),
            type = domain.type,
            experience = domain.experience,
            description = domain.description,
            availabilityCsv = domain.availability.joinToString(","),
            accentColor = domain.accentColor
        )
    }
}

@Entity(tableName = "exchanges")
data class ExchangeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subtitle: String,
    val initials: String
) {
    fun toDomain() = Exchange(title, subtitle, initials)
    companion object {
        fun fromDomain(domain: Exchange) = ExchangeEntity(title = domain.title, subtitle = domain.subtitle, initials = domain.initials)
    }
}

@Entity(tableName = "chat_threads", primaryKeys = ["userEmail", "id"])
data class ChatThreadEntity(
    val userEmail: String,
    val id: Int,
    val name: String,
    val initials: String,
    val lastMessage: String,
    val time: String,
    val unread: Int
) {
    fun toDomain(messages: List<ChatMessage>) = ChatThread(id, name, initials, lastMessage, time, unread, messages.toMutableList())
    companion object {
        fun fromDomain(userEmail: String, domain: ChatThread) = ChatThreadEntity(
            userEmail = userEmail,
            id = domain.id,
            name = domain.name,
            initials = domain.initials,
            lastMessage = domain.lastMessage,
            time = domain.time,
            unread = domain.unread
        )
    }
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val chatId: Int,
    val fromMe: Boolean,
    val text: String
) {
    fun toDomain() = ChatMessage(fromMe, text)
    companion object {
        fun fromDomain(userEmail: String, chatId: Int, domain: ChatMessage) = ChatMessageEntity(
            userEmail = userEmail,
            chatId = chatId,
            fromMe = domain.fromMe,
            text = domain.text
        )
    }
}

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val title: String,
    val time: String,
    val tag: String,
    val initials: String,
    val categoryTab: String // "Clases", "Mentorias", "Intercambios"
) {
    fun toDomain() = CalendarEvent(title, time, tag, initials)
    companion object {
        fun fromDomain(userEmail: String, categoryTab: String, domain: CalendarEvent) = CalendarEventEntity(
            userEmail = userEmail,
            title = domain.title,
            time = domain.time,
            tag = domain.tag,
            initials = domain.initials,
            categoryTab = categoryTab
        )
    }
}

@Entity(tableName = "user_skills", primaryKeys = ["userEmail", "name"])
data class UserSkillEntity(
    val userEmail: String,
    val name: String,
    val level: String
) {
    fun toDomain() = UserSkill(name, level)
    companion object {
        fun fromDomain(userEmail: String, domain: UserSkill) = UserSkillEntity(userEmail, domain.name, domain.level)
    }
}

@Entity(tableName = "learning_items", primaryKeys = ["userEmail", "name"])
data class LearningItemEntity(
    val userEmail: String,
    val name: String,
    val teacher: String,
    val progress: Int
) {
    fun toDomain() = LearningItem(name, teacher, progress)
    companion object {
        fun fromDomain(userEmail: String, domain: LearningItem) = LearningItemEntity(userEmail, domain.name, domain.teacher, domain.progress)
    }
}

@Entity(tableName = "achievements", primaryKeys = ["userEmail", "name"])
data class AchievementEntity(
    val userEmail: String,
    val name: String,
    val description: String,
    val unlocked: Boolean
) {
    fun toDomain() = Achievement(name, description, unlocked)
    companion object {
        fun fromDomain(userEmail: String, domain: Achievement) = AchievementEntity(userEmail, domain.name, domain.description, domain.unlocked)
    }
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val title: String,
    val description: String,
    val time: String,
    val unread: Boolean
) {
    fun toDomain() = NotificationItem(title, description, time, unread)
    companion object {
        fun fromDomain(userEmail: String, domain: NotificationItem) = NotificationEntity(
            userEmail = userEmail,
            title = domain.title,
            description = domain.description,
            time = domain.time,
            unread = domain.unread
        )
    }
}
