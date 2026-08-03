package com.skillconnect.app.data.model

data class Category(
    val name: String,
    val icon: String,
    val accentColor: String
)

data class Mentor(
    val id: Int,
    val name: String,
    val initials: String,
    val specialty: String,
    val rating: Double,
    val reviews: Int,
    val price: Int,
    val mode: List<String>,
    val type: String,
    val experience: String,
    val description: String,
    val availability: List<String>,
    val accentColor: String
)

data class Exchange(
    val title: String,
    val subtitle: String,
    val initials: String
)

data class ChatThread(
    val id: Int,
    val name: String,
    val initials: String,
    val lastMessage: String,
    val time: String,
    val unread: Int,
    val messages: MutableList<ChatMessage>
)

data class ChatMessage(
    val fromMe: Boolean,
    val text: String
)

data class CalendarEvent(
    val title: String,
    val time: String,
    val tag: String,
    val initials: String
)

data class UserSkill(
    val name: String,
    val level: String
)

data class LearningItem(
    val name: String,
    val teacher: String,
    val progress: Int
)

data class Achievement(
    val name: String,
    val description: String,
    val unlocked: Boolean
)

data class NotificationItem(
    val title: String,
    val description: String,
    val time: String,
    val unread: Boolean
)



