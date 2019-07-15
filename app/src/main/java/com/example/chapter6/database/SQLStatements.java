package com.example.chapter6.database;

public final class SQLStatements
{
	public static final String TABLE_NAME = "note";

	public static final String _ID = "_id";
	public static final String _COUNT = "_count";

	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_STATE = "state";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_PRIORITY = "priority";

	public static final String CREATE_NOTES =
			"CREATE TABLE " + TABLE_NAME
					+ "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_DATE + " INTEGER, "
					+ COLUMN_STATE + " INTEGER, "
					+ COLUMN_CONTENT + " TEXT, "
					+ COLUMN_PRIORITY + " INTEGER)";

	public static final String ADD_PRIORITY_COLUMN =
			"ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_PRIORITY + " INTEGER";

	private SQLStatements() {}
}
