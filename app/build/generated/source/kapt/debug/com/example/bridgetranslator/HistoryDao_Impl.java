package com.example.bridgetranslator;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HistoryDao_Impl implements HistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HistoryEntity> __insertionAdapterOfHistoryEntity;

  private final EntityDeletionOrUpdateAdapter<HistoryEntity> __deletionAdapterOfHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetStarred;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public HistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHistoryEntity = new EntityInsertionAdapter<HistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `history` (`id`,`sourceText`,`resultText`,`sourceLangCode`,`targetLangCode`,`timestamp`,`isStarred`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getSourceText() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getSourceText());
        }
        if (entity.getResultText() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getResultText());
        }
        if (entity.getSourceLangCode() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSourceLangCode());
        }
        if (entity.getTargetLangCode() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTargetLangCode());
        }
        statement.bindLong(6, entity.getTimestamp());
        final int _tmp = entity.isStarred() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__deletionAdapterOfHistoryEntity = new EntityDeletionOrUpdateAdapter<HistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `history` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HistoryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfSetStarred = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE history SET isStarred = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM history";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final HistoryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHistoryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final HistoryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfHistoryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setStarred(final long id, final boolean starred,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetStarred.acquire();
        int _argIndex = 1;
        final int _tmp = starred ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfSetStarred.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
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
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<HistoryEntity>> getAllFlow() {
    final String _sql = "SELECT * FROM history ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"history"}, new Callable<List<HistoryEntity>>() {
      @Override
      @NonNull
      public List<HistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSourceText = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceText");
          final int _cursorIndexOfResultText = CursorUtil.getColumnIndexOrThrow(_cursor, "resultText");
          final int _cursorIndexOfSourceLangCode = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceLangCode");
          final int _cursorIndexOfTargetLangCode = CursorUtil.getColumnIndexOrThrow(_cursor, "targetLangCode");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIsStarred = CursorUtil.getColumnIndexOrThrow(_cursor, "isStarred");
          final List<HistoryEntity> _result = new ArrayList<HistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSourceText;
            if (_cursor.isNull(_cursorIndexOfSourceText)) {
              _tmpSourceText = null;
            } else {
              _tmpSourceText = _cursor.getString(_cursorIndexOfSourceText);
            }
            final String _tmpResultText;
            if (_cursor.isNull(_cursorIndexOfResultText)) {
              _tmpResultText = null;
            } else {
              _tmpResultText = _cursor.getString(_cursorIndexOfResultText);
            }
            final String _tmpSourceLangCode;
            if (_cursor.isNull(_cursorIndexOfSourceLangCode)) {
              _tmpSourceLangCode = null;
            } else {
              _tmpSourceLangCode = _cursor.getString(_cursorIndexOfSourceLangCode);
            }
            final String _tmpTargetLangCode;
            if (_cursor.isNull(_cursorIndexOfTargetLangCode)) {
              _tmpTargetLangCode = null;
            } else {
              _tmpTargetLangCode = _cursor.getString(_cursorIndexOfTargetLangCode);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpIsStarred;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarred);
            _tmpIsStarred = _tmp != 0;
            _item = new HistoryEntity(_tmpId,_tmpSourceText,_tmpResultText,_tmpSourceLangCode,_tmpTargetLangCode,_tmpTimestamp,_tmpIsStarred);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
