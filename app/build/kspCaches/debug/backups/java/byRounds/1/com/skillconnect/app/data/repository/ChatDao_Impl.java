package com.skillconnect.app.data.repository;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.skillconnect.app.data.model.ChatMessageEntity;
import com.skillconnect.app.data.model.ChatThreadEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ChatDao_Impl implements ChatDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChatThreadEntity> __insertionAdapterOfChatThreadEntity;

  private final EntityInsertionAdapter<ChatMessageEntity> __insertionAdapterOfChatMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateThreadLastMessage;

  private final SharedSQLiteStatement __preparedStmtOfIncrementUnread;

  private final SharedSQLiteStatement __preparedStmtOfClearUnread;

  public ChatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatThreadEntity = new EntityInsertionAdapter<ChatThreadEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chat_threads` (`userEmail`,`id`,`name`,`initials`,`lastMessage`,`time`,`unread`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatThreadEntity entity) {
        statement.bindString(1, entity.getUserEmail());
        statement.bindLong(2, entity.getId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getInitials());
        statement.bindString(5, entity.getLastMessage());
        statement.bindString(6, entity.getTime());
        statement.bindLong(7, entity.getUnread());
      }
    };
    this.__insertionAdapterOfChatMessageEntity = new EntityInsertionAdapter<ChatMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chat_messages` (`id`,`userEmail`,`chatId`,`fromMe`,`text`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatMessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUserEmail());
        statement.bindLong(3, entity.getChatId());
        final int _tmp = entity.getFromMe() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindString(5, entity.getText());
      }
    };
    this.__preparedStmtOfUpdateThreadLastMessage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE chat_threads SET lastMessage = ?, time = ? WHERE userEmail = ? AND id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementUnread = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE chat_threads SET unread = unread + 1 WHERE userEmail = ? AND id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearUnread = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE chat_threads SET unread = 0 WHERE userEmail = ? AND id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertThreads(final List<ChatThreadEntity> threads,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChatThreadEntity.insert(threads);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMessage(final ChatMessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChatMessageEntity.insert(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMessages(final List<ChatMessageEntity> messages,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChatMessageEntity.insert(messages);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateThreadLastMessage(final String userEmail, final int chatId,
      final String message, final String time, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateThreadLastMessage.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, message);
        _argIndex = 2;
        _stmt.bindString(_argIndex, time);
        _argIndex = 3;
        _stmt.bindString(_argIndex, userEmail);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, chatId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateThreadLastMessage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementUnread(final String userEmail, final int chatId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementUnread.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userEmail);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, chatId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementUnread.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearUnread(final String userEmail, final int chatId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearUnread.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userEmail);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, chatId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearUnread.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getThreads(final String userEmail,
      final Continuation<? super List<ChatThreadEntity>> $completion) {
    final String _sql = "SELECT * FROM chat_threads WHERE userEmail = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userEmail);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ChatThreadEntity>>() {
      @Override
      @NonNull
      public List<ChatThreadEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInitials = CursorUtil.getColumnIndexOrThrow(_cursor, "initials");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfUnread = CursorUtil.getColumnIndexOrThrow(_cursor, "unread");
          final List<ChatThreadEntity> _result = new ArrayList<ChatThreadEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatThreadEntity _item;
            final String _tmpUserEmail;
            _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInitials;
            _tmpInitials = _cursor.getString(_cursorIndexOfInitials);
            final String _tmpLastMessage;
            _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            final String _tmpTime;
            _tmpTime = _cursor.getString(_cursorIndexOfTime);
            final int _tmpUnread;
            _tmpUnread = _cursor.getInt(_cursorIndexOfUnread);
            _item = new ChatThreadEntity(_tmpUserEmail,_tmpId,_tmpName,_tmpInitials,_tmpLastMessage,_tmpTime,_tmpUnread);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMessagesForThread(final String userEmail, final int chatId,
      final Continuation<? super List<ChatMessageEntity>> $completion) {
    final String _sql = "SELECT * FROM chat_messages WHERE userEmail = ? AND chatId = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userEmail);
    _argIndex = 2;
    _statement.bindLong(_argIndex, chatId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
          final int _cursorIndexOfChatId = CursorUtil.getColumnIndexOrThrow(_cursor, "chatId");
          final int _cursorIndexOfFromMe = CursorUtil.getColumnIndexOrThrow(_cursor, "fromMe");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpUserEmail;
            _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
            final int _tmpChatId;
            _tmpChatId = _cursor.getInt(_cursorIndexOfChatId);
            final boolean _tmpFromMe;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfFromMe);
            _tmpFromMe = _tmp != 0;
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            _item = new ChatMessageEntity(_tmpId,_tmpUserEmail,_tmpChatId,_tmpFromMe,_tmpText);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getThreadsCount(final String userEmail,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM chat_threads WHERE userEmail = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userEmail);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
