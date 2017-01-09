package com.iron.dragon.sportstogether;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.iron.dragon.sportstogether.util.Const;

public class MyContentProvider extends ContentProvider {
    private DbHelper mDbHelper;
    private static final String DB_NAME = "sports_db";
    private static final int DB_VER = 0;
    private SQLiteDatabase mDb;
    public static final Uri CONTENT_URI = Uri.parse(Const.CONTENT_URI_STR);

    public MyContentProvider() {
    }

    @Override
    public String getType(Uri uri) {
        return Const.CONTENT_URI_STR;
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new DbHelper(getContext(), DB_NAME, null, DB_VER);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        mDb = mDbHelper.getReadableDatabase();
        Cursor c = mDb.query(DbHelper.TABLE, new String[](DbHelper.COLUMN_MESSAGE), null, null, null, null, "date asc");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mDb = mDbHelper.getWritableDatabase();
        long row = mDb.insert(DbHelper.TABLE, null, values);
        if(row>0){
            Uri notiUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(notiUri, null);
            return notiUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        mDb = mDbHelper.getWritableDatabase();
        int count = 0;
        count = mDb.delete(DbHelper.TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        mDb = mDbHelper.getWritableDatabase();
        int count = 0;
        count = mDb.update(DbHelper.TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }



    /**
     * data base helper
     */
    public static class DbHelper extends SQLiteOpenHelper{
        static final String TAG = "Dbhelper";
        public static final String TABLE = "chat";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SENDER = "sender";
        public static final String COLUMN_RECEIVER = "receiver";
        public static final String COLUMN_MESSAGE = "message";

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_DATE + " long, not null "
                    + COLUMN_SENDER + " text not null, "
                    + COLUMN_RECEIVER + " text not null, "
                    + COLUMN_MESSAGE + " text not null, "
                    + "UNIQUE(" + COLUMN_DATE + ") ON CONFLICT REPLACE"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP "+TABLE+" IF EXISTS");
            onCreate(db);
        }
    }
}
