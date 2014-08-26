package com.akavel.bookshelf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BooksStorage extends SQLiteOpenHelper {
	private static final int CURRENT_VERSION = 1;
	
	static String TITLE = "title";
	static String AUTHOR = "author";
	static String PATH = "path";
	static String SHELVES = "shelves";

	public BooksStorage(Context context) {
		super(context, "bookshelf.db", null, CURRENT_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		onUpgrade(db, 0, CURRENT_VERSION);
		
		ContentValues sample = new ContentValues();
		sample.put(TITLE, "Dżuma");
		sample.put(AUTHOR, "Albert Camus");
		sample.put(PATH, "/mnt/foo/bar/dzuma.epub");
		db.insert(SHELVES, null, sample);		
	}
	
	private Boolean targets(int oldv, int newv, int targetv) {
		return oldv <= (targetv-1) && targetv <= newv;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldv, int newv) {
		if (targets(oldv, newv, 1))
			db.execSQL(
					"CREATE TABLE shelves (" +
					"	_id INT PRIMARY KEY," + // column name used by Android helper classes
					"	title TEXT NOT NULL," +
					"	author TEXT NOT NULL," +
					"	path TEXT NOT NULL UNIQUE," +
					"	lastOpened INT," +
					"	shelf INT DEFAULT 0" +
					");");
	}

	public Cursor openCursor(int shelf) {
		String s = Integer.toString(shelf);
		//throw new RuntimeException(s);
		//FIXME: as described in Android docs, don't use getReadableDatabase() in GUI thread
		//FIXME: handle multithreaded access to DB somehow
		return getReadableDatabase().rawQuery("SELECT title, author, path, _id FROM shelves WHERE shelf=? ORDER BY title", new String[] {s});
		//return getReadableDatabase().query(SHELVES, new String[]{TITLE, AUTHOR, PATH}, "shelf=?", new String[]{s}, null, null, TITLE);
	}
}
