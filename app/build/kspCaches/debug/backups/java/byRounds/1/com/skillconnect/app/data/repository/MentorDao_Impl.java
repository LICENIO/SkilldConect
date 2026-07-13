package com.skillconnect.app.data.repository;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.skillconnect.app.data.model.MentorEntity;
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
public final class MentorDao_Impl implements MentorDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MentorEntity> __insertionAdapterOfMentorEntity;

  public MentorDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMentorEntity = new EntityInsertionAdapter<MentorEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `mentors` (`id`,`name`,`initials`,`specialty`,`rating`,`reviews`,`price`,`modeCsv`,`type`,`experience`,`description`,`availabilityCsv`,`accentColor`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MentorEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getInitials());
        statement.bindString(4, entity.getSpecialty());
        statement.bindDouble(5, entity.getRating());
        statement.bindLong(6, entity.getReviews());
        statement.bindLong(7, entity.getPrice());
        statement.bindString(8, entity.getModeCsv());
        statement.bindString(9, entity.getType());
        statement.bindString(10, entity.getExperience());
        statement.bindString(11, entity.getDescription());
        statement.bindString(12, entity.getAvailabilityCsv());
        statement.bindString(13, entity.getAccentColor());
      }
    };
  }

  @Override
  public Object insertAll(final List<MentorEntity> mentors,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMentorEntity.insert(mentors);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<MentorEntity>> $completion) {
    final String _sql = "SELECT * FROM mentors";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MentorEntity>>() {
      @Override
      @NonNull
      public List<MentorEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInitials = CursorUtil.getColumnIndexOrThrow(_cursor, "initials");
          final int _cursorIndexOfSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "specialty");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviews = CursorUtil.getColumnIndexOrThrow(_cursor, "reviews");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfModeCsv = CursorUtil.getColumnIndexOrThrow(_cursor, "modeCsv");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfExperience = CursorUtil.getColumnIndexOrThrow(_cursor, "experience");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAvailabilityCsv = CursorUtil.getColumnIndexOrThrow(_cursor, "availabilityCsv");
          final int _cursorIndexOfAccentColor = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColor");
          final List<MentorEntity> _result = new ArrayList<MentorEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MentorEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInitials;
            _tmpInitials = _cursor.getString(_cursorIndexOfInitials);
            final String _tmpSpecialty;
            _tmpSpecialty = _cursor.getString(_cursorIndexOfSpecialty);
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviews;
            _tmpReviews = _cursor.getInt(_cursorIndexOfReviews);
            final int _tmpPrice;
            _tmpPrice = _cursor.getInt(_cursorIndexOfPrice);
            final String _tmpModeCsv;
            _tmpModeCsv = _cursor.getString(_cursorIndexOfModeCsv);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpExperience;
            _tmpExperience = _cursor.getString(_cursorIndexOfExperience);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpAvailabilityCsv;
            _tmpAvailabilityCsv = _cursor.getString(_cursorIndexOfAvailabilityCsv);
            final String _tmpAccentColor;
            _tmpAccentColor = _cursor.getString(_cursorIndexOfAccentColor);
            _item = new MentorEntity(_tmpId,_tmpName,_tmpInitials,_tmpSpecialty,_tmpRating,_tmpReviews,_tmpPrice,_tmpModeCsv,_tmpType,_tmpExperience,_tmpDescription,_tmpAvailabilityCsv,_tmpAccentColor);
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
  public Object getMentorById(final int id, final Continuation<? super MentorEntity> $completion) {
    final String _sql = "SELECT * FROM mentors WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MentorEntity>() {
      @Override
      @Nullable
      public MentorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInitials = CursorUtil.getColumnIndexOrThrow(_cursor, "initials");
          final int _cursorIndexOfSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "specialty");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviews = CursorUtil.getColumnIndexOrThrow(_cursor, "reviews");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfModeCsv = CursorUtil.getColumnIndexOrThrow(_cursor, "modeCsv");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfExperience = CursorUtil.getColumnIndexOrThrow(_cursor, "experience");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAvailabilityCsv = CursorUtil.getColumnIndexOrThrow(_cursor, "availabilityCsv");
          final int _cursorIndexOfAccentColor = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColor");
          final MentorEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpInitials;
            _tmpInitials = _cursor.getString(_cursorIndexOfInitials);
            final String _tmpSpecialty;
            _tmpSpecialty = _cursor.getString(_cursorIndexOfSpecialty);
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviews;
            _tmpReviews = _cursor.getInt(_cursorIndexOfReviews);
            final int _tmpPrice;
            _tmpPrice = _cursor.getInt(_cursorIndexOfPrice);
            final String _tmpModeCsv;
            _tmpModeCsv = _cursor.getString(_cursorIndexOfModeCsv);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpExperience;
            _tmpExperience = _cursor.getString(_cursorIndexOfExperience);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpAvailabilityCsv;
            _tmpAvailabilityCsv = _cursor.getString(_cursorIndexOfAvailabilityCsv);
            final String _tmpAccentColor;
            _tmpAccentColor = _cursor.getString(_cursorIndexOfAccentColor);
            _result = new MentorEntity(_tmpId,_tmpName,_tmpInitials,_tmpSpecialty,_tmpRating,_tmpReviews,_tmpPrice,_tmpModeCsv,_tmpType,_tmpExperience,_tmpDescription,_tmpAvailabilityCsv,_tmpAccentColor);
          } else {
            _result = null;
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
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM mentors";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
