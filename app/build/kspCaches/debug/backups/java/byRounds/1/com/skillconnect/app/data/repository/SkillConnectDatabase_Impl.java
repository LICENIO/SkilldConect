package com.skillconnect.app.data.repository;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SkillConnectDatabase_Impl extends SkillConnectDatabase {
  private volatile UserDao _userDao;

  private volatile CategoryDao _categoryDao;

  private volatile MentorDao _mentorDao;

  private volatile ExchangeDao _exchangeDao;

  private volatile ChatDao _chatDao;

  private volatile CalendarDao _calendarDao;

  private volatile SkillDao _skillDao;

  private volatile LearningDao _learningDao;

  private volatile AchievementDao _achievementDao;

  private volatile NotificationDao _notificationDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`email` TEXT NOT NULL, `name` TEXT NOT NULL, `password` TEXT NOT NULL, `role` TEXT NOT NULL, `initials` TEXT NOT NULL, `rating` REAL NOT NULL, `classesCount` INTEGER NOT NULL, `exchangesCount` INTEGER NOT NULL, PRIMARY KEY(`email`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`name` TEXT NOT NULL, `icon` TEXT NOT NULL, `accentColor` TEXT NOT NULL, PRIMARY KEY(`name`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `mentors` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `initials` TEXT NOT NULL, `specialty` TEXT NOT NULL, `rating` REAL NOT NULL, `reviews` INTEGER NOT NULL, `price` INTEGER NOT NULL, `modeCsv` TEXT NOT NULL, `type` TEXT NOT NULL, `experience` TEXT NOT NULL, `description` TEXT NOT NULL, `availabilityCsv` TEXT NOT NULL, `accentColor` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exchanges` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `subtitle` TEXT NOT NULL, `initials` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_threads` (`userEmail` TEXT NOT NULL, `id` INTEGER NOT NULL, `name` TEXT NOT NULL, `initials` TEXT NOT NULL, `lastMessage` TEXT NOT NULL, `time` TEXT NOT NULL, `unread` INTEGER NOT NULL, PRIMARY KEY(`userEmail`, `id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userEmail` TEXT NOT NULL, `chatId` INTEGER NOT NULL, `fromMe` INTEGER NOT NULL, `text` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `calendar_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userEmail` TEXT NOT NULL, `title` TEXT NOT NULL, `time` TEXT NOT NULL, `tag` TEXT NOT NULL, `initials` TEXT NOT NULL, `categoryTab` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_skills` (`userEmail` TEXT NOT NULL, `name` TEXT NOT NULL, `level` TEXT NOT NULL, PRIMARY KEY(`userEmail`, `name`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `learning_items` (`userEmail` TEXT NOT NULL, `name` TEXT NOT NULL, `teacher` TEXT NOT NULL, `progress` INTEGER NOT NULL, PRIMARY KEY(`userEmail`, `name`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `achievements` (`userEmail` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `unlocked` INTEGER NOT NULL, PRIMARY KEY(`userEmail`, `name`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userEmail` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `time` TEXT NOT NULL, `unread` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9dd8c99c446ad9226dcc4e615171e3ad')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `mentors`");
        db.execSQL("DROP TABLE IF EXISTS `exchanges`");
        db.execSQL("DROP TABLE IF EXISTS `chat_threads`");
        db.execSQL("DROP TABLE IF EXISTS `chat_messages`");
        db.execSQL("DROP TABLE IF EXISTS `calendar_events`");
        db.execSQL("DROP TABLE IF EXISTS `user_skills`");
        db.execSQL("DROP TABLE IF EXISTS `learning_items`");
        db.execSQL("DROP TABLE IF EXISTS `achievements`");
        db.execSQL("DROP TABLE IF EXISTS `notifications`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(8);
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("initials", new TableInfo.Column("initials", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("rating", new TableInfo.Column("rating", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("classesCount", new TableInfo.Column("classesCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("exchangesCount", new TableInfo.Column("exchangesCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.skillconnect.app.data.model.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(3);
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("icon", new TableInfo.Column("icon", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("accentColor", new TableInfo.Column("accentColor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.skillconnect.app.data.model.CategoryEntity).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsMentors = new HashMap<String, TableInfo.Column>(13);
        _columnsMentors.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("initials", new TableInfo.Column("initials", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("specialty", new TableInfo.Column("specialty", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("rating", new TableInfo.Column("rating", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("reviews", new TableInfo.Column("reviews", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("price", new TableInfo.Column("price", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("modeCsv", new TableInfo.Column("modeCsv", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("experience", new TableInfo.Column("experience", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("availabilityCsv", new TableInfo.Column("availabilityCsv", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMentors.put("accentColor", new TableInfo.Column("accentColor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMentors = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMentors = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMentors = new TableInfo("mentors", _columnsMentors, _foreignKeysMentors, _indicesMentors);
        final TableInfo _existingMentors = TableInfo.read(db, "mentors");
        if (!_infoMentors.equals(_existingMentors)) {
          return new RoomOpenHelper.ValidationResult(false, "mentors(com.skillconnect.app.data.model.MentorEntity).\n"
                  + " Expected:\n" + _infoMentors + "\n"
                  + " Found:\n" + _existingMentors);
        }
        final HashMap<String, TableInfo.Column> _columnsExchanges = new HashMap<String, TableInfo.Column>(4);
        _columnsExchanges.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExchanges.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExchanges.put("subtitle", new TableInfo.Column("subtitle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExchanges.put("initials", new TableInfo.Column("initials", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExchanges = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExchanges = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExchanges = new TableInfo("exchanges", _columnsExchanges, _foreignKeysExchanges, _indicesExchanges);
        final TableInfo _existingExchanges = TableInfo.read(db, "exchanges");
        if (!_infoExchanges.equals(_existingExchanges)) {
          return new RoomOpenHelper.ValidationResult(false, "exchanges(com.skillconnect.app.data.model.ExchangeEntity).\n"
                  + " Expected:\n" + _infoExchanges + "\n"
                  + " Found:\n" + _existingExchanges);
        }
        final HashMap<String, TableInfo.Column> _columnsChatThreads = new HashMap<String, TableInfo.Column>(7);
        _columnsChatThreads.put("userEmail", new TableInfo.Column("userEmail", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("id", new TableInfo.Column("id", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("initials", new TableInfo.Column("initials", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("lastMessage", new TableInfo.Column("lastMessage", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("time", new TableInfo.Column("time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatThreads.put("unread", new TableInfo.Column("unread", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatThreads = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChatThreads = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChatThreads = new TableInfo("chat_threads", _columnsChatThreads, _foreignKeysChatThreads, _indicesChatThreads);
        final TableInfo _existingChatThreads = TableInfo.read(db, "chat_threads");
        if (!_infoChatThreads.equals(_existingChatThreads)) {
          return new RoomOpenHelper.ValidationResult(false, "chat_threads(com.skillconnect.app.data.model.ChatThreadEntity).\n"
                  + " Expected:\n" + _infoChatThreads + "\n"
                  + " Found:\n" + _existingChatThreads);
        }
        final HashMap<String, TableInfo.Column> _columnsChatMessages = new HashMap<String, TableInfo.Column>(5);
        _columnsChatMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("userEmail", new TableInfo.Column("userEmail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("chatId", new TableInfo.Column("chatId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("fromMe", new TableInfo.Column("fromMe", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChatMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChatMessages = new TableInfo("chat_messages", _columnsChatMessages, _foreignKeysChatMessages, _indicesChatMessages);
        final TableInfo _existingChatMessages = TableInfo.read(db, "chat_messages");
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "chat_messages(com.skillconnect.app.data.model.ChatMessageEntity).\n"
                  + " Expected:\n" + _infoChatMessages + "\n"
                  + " Found:\n" + _existingChatMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsCalendarEvents = new HashMap<String, TableInfo.Column>(7);
        _columnsCalendarEvents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalendarEvents.put("userEmail", new TableInfo.Column("userEmail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalendarEvents.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalendarEvents.put("time", new TableInfo.Column("time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalendarEvents.put("tag", new TableInfo.Column("tag", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalendarEvents.put("initials", new TableInfo.Column("initials", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalendarEvents.put("categoryTab", new TableInfo.Column("categoryTab", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCalendarEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCalendarEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCalendarEvents = new TableInfo("calendar_events", _columnsCalendarEvents, _foreignKeysCalendarEvents, _indicesCalendarEvents);
        final TableInfo _existingCalendarEvents = TableInfo.read(db, "calendar_events");
        if (!_infoCalendarEvents.equals(_existingCalendarEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "calendar_events(com.skillconnect.app.data.model.CalendarEventEntity).\n"
                  + " Expected:\n" + _infoCalendarEvents + "\n"
                  + " Found:\n" + _existingCalendarEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsUserSkills = new HashMap<String, TableInfo.Column>(3);
        _columnsUserSkills.put("userEmail", new TableInfo.Column("userEmail", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSkills.put("name", new TableInfo.Column("name", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSkills.put("level", new TableInfo.Column("level", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserSkills = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserSkills = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserSkills = new TableInfo("user_skills", _columnsUserSkills, _foreignKeysUserSkills, _indicesUserSkills);
        final TableInfo _existingUserSkills = TableInfo.read(db, "user_skills");
        if (!_infoUserSkills.equals(_existingUserSkills)) {
          return new RoomOpenHelper.ValidationResult(false, "user_skills(com.skillconnect.app.data.model.UserSkillEntity).\n"
                  + " Expected:\n" + _infoUserSkills + "\n"
                  + " Found:\n" + _existingUserSkills);
        }
        final HashMap<String, TableInfo.Column> _columnsLearningItems = new HashMap<String, TableInfo.Column>(4);
        _columnsLearningItems.put("userEmail", new TableInfo.Column("userEmail", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningItems.put("name", new TableInfo.Column("name", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningItems.put("teacher", new TableInfo.Column("teacher", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningItems.put("progress", new TableInfo.Column("progress", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLearningItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLearningItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLearningItems = new TableInfo("learning_items", _columnsLearningItems, _foreignKeysLearningItems, _indicesLearningItems);
        final TableInfo _existingLearningItems = TableInfo.read(db, "learning_items");
        if (!_infoLearningItems.equals(_existingLearningItems)) {
          return new RoomOpenHelper.ValidationResult(false, "learning_items(com.skillconnect.app.data.model.LearningItemEntity).\n"
                  + " Expected:\n" + _infoLearningItems + "\n"
                  + " Found:\n" + _existingLearningItems);
        }
        final HashMap<String, TableInfo.Column> _columnsAchievements = new HashMap<String, TableInfo.Column>(4);
        _columnsAchievements.put("userEmail", new TableInfo.Column("userEmail", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("name", new TableInfo.Column("name", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("unlocked", new TableInfo.Column("unlocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAchievements = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAchievements = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAchievements = new TableInfo("achievements", _columnsAchievements, _foreignKeysAchievements, _indicesAchievements);
        final TableInfo _existingAchievements = TableInfo.read(db, "achievements");
        if (!_infoAchievements.equals(_existingAchievements)) {
          return new RoomOpenHelper.ValidationResult(false, "achievements(com.skillconnect.app.data.model.AchievementEntity).\n"
                  + " Expected:\n" + _infoAchievements + "\n"
                  + " Found:\n" + _existingAchievements);
        }
        final HashMap<String, TableInfo.Column> _columnsNotifications = new HashMap<String, TableInfo.Column>(6);
        _columnsNotifications.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("userEmail", new TableInfo.Column("userEmail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("time", new TableInfo.Column("time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("unread", new TableInfo.Column("unread", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotifications = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotifications = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotifications = new TableInfo("notifications", _columnsNotifications, _foreignKeysNotifications, _indicesNotifications);
        final TableInfo _existingNotifications = TableInfo.read(db, "notifications");
        if (!_infoNotifications.equals(_existingNotifications)) {
          return new RoomOpenHelper.ValidationResult(false, "notifications(com.skillconnect.app.data.model.NotificationEntity).\n"
                  + " Expected:\n" + _infoNotifications + "\n"
                  + " Found:\n" + _existingNotifications);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "9dd8c99c446ad9226dcc4e615171e3ad", "61c6a9a0a0e4973b1d87beb46b18fff5");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","categories","mentors","exchanges","chat_threads","chat_messages","calendar_events","user_skills","learning_items","achievements","notifications");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `mentors`");
      _db.execSQL("DELETE FROM `exchanges`");
      _db.execSQL("DELETE FROM `chat_threads`");
      _db.execSQL("DELETE FROM `chat_messages`");
      _db.execSQL("DELETE FROM `calendar_events`");
      _db.execSQL("DELETE FROM `user_skills`");
      _db.execSQL("DELETE FROM `learning_items`");
      _db.execSQL("DELETE FROM `achievements`");
      _db.execSQL("DELETE FROM `notifications`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MentorDao.class, MentorDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExchangeDao.class, ExchangeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatDao.class, ChatDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CalendarDao.class, CalendarDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SkillDao.class, SkillDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LearningDao.class, LearningDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AchievementDao.class, AchievementDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotificationDao.class, NotificationDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public MentorDao mentorDao() {
    if (_mentorDao != null) {
      return _mentorDao;
    } else {
      synchronized(this) {
        if(_mentorDao == null) {
          _mentorDao = new MentorDao_Impl(this);
        }
        return _mentorDao;
      }
    }
  }

  @Override
  public ExchangeDao exchangeDao() {
    if (_exchangeDao != null) {
      return _exchangeDao;
    } else {
      synchronized(this) {
        if(_exchangeDao == null) {
          _exchangeDao = new ExchangeDao_Impl(this);
        }
        return _exchangeDao;
      }
    }
  }

  @Override
  public ChatDao chatDao() {
    if (_chatDao != null) {
      return _chatDao;
    } else {
      synchronized(this) {
        if(_chatDao == null) {
          _chatDao = new ChatDao_Impl(this);
        }
        return _chatDao;
      }
    }
  }

  @Override
  public CalendarDao calendarDao() {
    if (_calendarDao != null) {
      return _calendarDao;
    } else {
      synchronized(this) {
        if(_calendarDao == null) {
          _calendarDao = new CalendarDao_Impl(this);
        }
        return _calendarDao;
      }
    }
  }

  @Override
  public SkillDao skillDao() {
    if (_skillDao != null) {
      return _skillDao;
    } else {
      synchronized(this) {
        if(_skillDao == null) {
          _skillDao = new SkillDao_Impl(this);
        }
        return _skillDao;
      }
    }
  }

  @Override
  public LearningDao learningDao() {
    if (_learningDao != null) {
      return _learningDao;
    } else {
      synchronized(this) {
        if(_learningDao == null) {
          _learningDao = new LearningDao_Impl(this);
        }
        return _learningDao;
      }
    }
  }

  @Override
  public AchievementDao achievementDao() {
    if (_achievementDao != null) {
      return _achievementDao;
    } else {
      synchronized(this) {
        if(_achievementDao == null) {
          _achievementDao = new AchievementDao_Impl(this);
        }
        return _achievementDao;
      }
    }
  }

  @Override
  public NotificationDao notificationDao() {
    if (_notificationDao != null) {
      return _notificationDao;
    } else {
      synchronized(this) {
        if(_notificationDao == null) {
          _notificationDao = new NotificationDao_Impl(this);
        }
        return _notificationDao;
      }
    }
  }
}
