package com.skillconnect.app.data.repository

import androidx.room.*
import com.skillconnect.app.data.model.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUser(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY email DESC LIMIT 1")
    suspend fun getLastRegisteredUser(): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int
}

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}

@Dao
interface MentorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mentors: List<MentorEntity>)

    @Query("SELECT * FROM mentors")
    suspend fun getAll(): List<MentorEntity>

    @Query("SELECT * FROM mentors WHERE id = :id")
    suspend fun getMentorById(id: Int): MentorEntity?

    @Query("SELECT COUNT(*) FROM mentors")
    suspend fun getCount(): Int
}

@Dao
interface ExchangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exchange: ExchangeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exchanges: List<ExchangeEntity>)

    @Query("SELECT * FROM exchanges ORDER BY id DESC")
    suspend fun getAll(): List<ExchangeEntity>
}

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreads(threads: List<ChatThreadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("SELECT * FROM chat_threads WHERE userEmail = :userEmail ORDER BY id ASC")
    suspend fun getThreads(userEmail: String): List<ChatThreadEntity>

    @Query("SELECT * FROM chat_messages WHERE userEmail = :userEmail AND chatId = :chatId ORDER BY id ASC")
    suspend fun getMessagesForThread(userEmail: String, chatId: Int): List<ChatMessageEntity>

    @Query("UPDATE chat_threads SET lastMessage = :message, time = :time WHERE userEmail = :userEmail AND id = :chatId")
    suspend fun updateThreadLastMessage(userEmail: String, chatId: Int, message: String, time: String)

    @Query("UPDATE chat_threads SET unread = unread + 1 WHERE userEmail = :userEmail AND id = :chatId")
    suspend fun incrementUnread(userEmail: String, chatId: Int)

    @Query("UPDATE chat_threads SET unread = 0 WHERE userEmail = :userEmail AND id = :chatId")
    suspend fun clearUnread(userEmail: String, chatId: Int)

    @Query("SELECT COUNT(*) FROM chat_threads WHERE userEmail = :userEmail")
    suspend fun getThreadsCount(userEmail: String): Int
}

@Dao
interface CalendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CalendarEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<CalendarEventEntity>)

    @Query("SELECT * FROM calendar_events WHERE userEmail = :userEmail AND categoryTab = :tab ORDER BY id ASC")
    suspend fun getEventsByTab(userEmail: String, tab: String): List<CalendarEventEntity>

    @Query("SELECT COUNT(*) FROM calendar_events WHERE userEmail = :userEmail")
    suspend fun getEventsCount(userEmail: String): Int
}

@Dao
interface SkillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(skill: UserSkillEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skills: List<UserSkillEntity>)

    @Query("SELECT * FROM user_skills WHERE userEmail = :userEmail")
    suspend fun getAll(userEmail: String): List<UserSkillEntity>
}

@Dao
interface LearningDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LearningItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LearningItemEntity>)

    @Query("SELECT * FROM learning_items WHERE userEmail = :userEmail")
    suspend fun getAll(userEmail: String): List<LearningItemEntity>
}

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Query("SELECT * FROM achievements WHERE userEmail = :userEmail")
    suspend fun getAll(userEmail: String): List<AchievementEntity>

    @Query("UPDATE achievements SET unlocked = :unlocked WHERE userEmail = :userEmail AND name = :name")
    suspend fun updateAchievementUnlocked(userEmail: String, name: String, unlocked: Boolean)
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("SELECT * FROM notifications WHERE userEmail = :userEmail ORDER BY id DESC")
    suspend fun getAll(userEmail: String): List<NotificationEntity>
}
