package com.skillconnect.app.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.skillconnect.app.data.model.*

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        MentorEntity::class,
        ExchangeEntity::class,
        ChatThreadEntity::class,
        ChatMessageEntity::class,
        CalendarEventEntity::class,
        UserSkillEntity::class,
        LearningItemEntity::class,
        AchievementEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SkillConnectDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun mentorDao(): MentorDao
    abstract fun exchangeDao(): ExchangeDao
    abstract fun chatDao(): ChatDao
    abstract fun calendarDao(): CalendarDao
    abstract fun skillDao(): SkillDao
    abstract fun learningDao(): LearningDao
    abstract fun achievementDao(): AchievementDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: SkillConnectDatabase? = null

        fun getDatabase(context: Context): SkillConnectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkillConnectDatabase::class.java,
                    "skillconnect_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
