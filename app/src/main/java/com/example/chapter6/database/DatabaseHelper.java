package com.example.chapter6.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final String DB_NAME = "todolist.db";
	private static final int DB_VERSION = 2;

	public DatabaseHelper(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(SQLStatements.CREATE_NOTES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		for(int i = oldVersion; i < newVersion; i++)
		{
			if(i == 1)
			{
				db.execSQL(SQLStatements.ADD_PRIORITY_COLUMN);
				break;
			}
		}
	}
}
