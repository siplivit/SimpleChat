package com.vitaliksv.simplechat.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class MyDBAdapter{
	
	private static final int DATABASE_VESION = 1;
	private static final String DATABASE_NAME = "chat.db";

	private static final String DATABASE_TABLE_MESSAGES = "messages";

	private static final String KEY_MESSAGES_ID = "_id";
	private static final int ID_MESSAGE_COLUMN = 0;

	public static final String MESSAGES_TEXT = "text";
	private static final int MESSAGES_TEXT_COLUMN = 1;

	public static final String MESSAGES_TIME = "time";
	private static final int MESSAGES_TIME_COLUMN = 2;

	public static final String MESSAGES_USER = "user";
	public static final int MESSAGES_USER_COLUMN = 3;
	
	
	private static final String DB_MESSAGES_TABLE_CREATE = "create table "
			+ DATABASE_TABLE_MESSAGES + " ( " 
			+ KEY_MESSAGES_ID + " integer primary key autoincrement, "
			+ MESSAGES_TEXT + " text, "  
			+ MESSAGES_TIME + " text, " 
			+ MESSAGES_USER + " integer" + ");";
	
	
	
	private MyDBHelper dBHelper;
	private SQLiteDatabase db;
	private final Context context;

	public MyDBAdapter(Context _context) {
		context = _context;
		dBHelper = new MyDBHelper(context, DATABASE_NAME, null, DATABASE_VESION);
	}

	public MyDBAdapter open() throws SQLException {

		try {
			db = dBHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			db = dBHelper.getReadableDatabase();
		}
		return this;
	}

	public void close() {
		if (db != null) {
			db.close();
		}
	}

	public Cursor getAllEntries() {
		Cursor mCursor = db.query(DATABASE_TABLE_MESSAGES, null, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public long addMessageToDB(Message message) {

		long result;
		ContentValues cv = new ContentValues();
		cv.put(MESSAGES_TEXT, message.getMessage());
		cv.put(MESSAGES_TIME, message.getDate());
		cv.put(MESSAGES_USER, message.getUserID());

		db.beginTransaction();
	    try {
	    	result = db.insert(DATABASE_TABLE_MESSAGES, null, cv);
	      db.setTransactionSuccessful();
	    } finally {
	      db.endTransaction();
	    }
	    return result;
	}

	public int getNumberOfMessages() {

		return getAllEntries().getCount();
	}
	
	public void clearHistory() {

		db.beginTransaction();
		try {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MESSAGES);
			db.execSQL(DB_MESSAGES_TABLE_CREATE);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	private class MyDBHelper extends SQLiteOpenHelper {

		public MyDBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.beginTransaction();
			try {
				_db.execSQL(DB_MESSAGES_TABLE_CREATE);
				_db.setTransactionSuccessful();
			} finally {
				_db.endTransaction();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			_db.beginTransaction();
			try {
				_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MESSAGES);
				_db.setTransactionSuccessful();
			} finally {
				_db.endTransaction();
			}
			onCreate(_db);
		}
	}
}
