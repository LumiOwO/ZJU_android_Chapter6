package com.example.chapter6;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.chapter6.activities.NewNoteActivity;
import com.example.chapter6.database.DatabaseHelper;
import com.example.chapter6.database.SQLStatements;
import com.example.chapter6.activities.DebugActivity;
import com.example.chapter6.listView.ListAdapter;
import com.example.chapter6.model.Note;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
	private static final int REQUEST_CODE_NEWNOTE = 251;

	private static final boolean ORDER_BY_ASC = true;
	private static final boolean ORDER_BY_DESC = false;
	private static final boolean ORDER_BY_TIME = true;
	private static final boolean ORDER_BY_PRIORITY = false;

	// database
	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDatabase;
	// recycler view
	private RecyclerView mRecyclerView;
	private ListAdapter mListAdapter;
	// order button
	private Button mOrderBtn;
	private Button mOrderTypeBtn;
	private boolean mOrder;
	private boolean mOrderType;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set add button
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				NewNoteActivity.launch(MainActivity.this, REQUEST_CODE_NEWNOTE);
			}
		});

		// set database
		mDBHelper = new DatabaseHelper(this);
		mDatabase = mDBHelper.getWritableDatabase();

		// set recycler view
		mRecyclerView = findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(
				new LinearLayoutManager(this));
		mRecyclerView.addItemDecoration(
				new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		mListAdapter = new ListAdapter();
		mRecyclerView.setAdapter(mListAdapter);

		// set order buttons
		mOrderBtn = findViewById(R.id.order_btn);
		mOrderBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setOrder(!mOrder);
				refreshView();
			}
		});
		mOrderTypeBtn = findViewById(R.id.orderType_btn);
		mOrderTypeBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setOrderType(!mOrderType);
				refreshView();
			}
		});

		// init view
		setOrder(ORDER_BY_DESC);
		setOrderType(ORDER_BY_PRIORITY);
		refreshView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_debug) {
			DebugActivity.launch(MainActivity.this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_NEWNOTE && resultCode == Activity.RESULT_OK)
			refreshView();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		refreshView();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// free database resources
		mDatabase.close();
		mDatabase = null;
		mDBHelper.close();
		mDBHelper = null;
	}

	private void refreshView()
	{
		mListAdapter.refreshView(loadNotes());
	}

	private List<Note> loadNotes()
	{
		// get order
		String orderText = null;
		if(mOrder == ORDER_BY_ASC)
			orderText = " ASC";
		else if(mOrder == ORDER_BY_DESC)
			orderText = " DESC";

		// get order type
		String orderTypeText = null;
		if(mOrderType == ORDER_BY_PRIORITY)
			orderTypeText = SQLStatements.COLUMN_PRIORITY;
		else if(mOrderType == ORDER_BY_TIME)
			orderTypeText = SQLStatements.COLUMN_DATE;

		List<Note> ret = new ArrayList<Note>();
		Cursor cursor = null;
		if(mDatabase != null) try
		{
			// first, select item that have not done
			cursor = mDatabase.query(
					SQLStatements.TABLE_NAME,
					null,
					SQLStatements.COLUMN_STATE + "=?",
					new String[]{String.valueOf(0)},
					null,
					null,
					orderTypeText + orderText
					);
			ret.addAll(getNoteFromCursor(cursor));

			// then select item that have done
			cursor = mDatabase.query(
					SQLStatements.TABLE_NAME,
					null,
					SQLStatements.COLUMN_STATE + "=?",
					new String[]{String.valueOf(1)},
					null,
					null,
					orderTypeText + orderText
			);
			ret.addAll(getNoteFromCursor(cursor));


		} catch (Exception e) {
			e.printStackTrace();
		}

		if(cursor != null)
			cursor.close();

		return ret;
	}

	private List<Note> getNoteFromCursor(Cursor cursor)
	{
		List<Note> ret = new ArrayList<>();
		while(cursor.moveToNext())
		{
			// get information
			long id = cursor.getLong(cursor.getColumnIndex(SQLStatements._ID));
			String text = cursor.getString(cursor.getColumnIndex(SQLStatements.COLUMN_CONTENT));
			long dateMs = cursor.getLong(cursor.getColumnIndex(SQLStatements.COLUMN_DATE));
			int intState = cursor.getInt(cursor.getColumnIndex(SQLStatements.COLUMN_STATE));
			int intPriority = cursor.getInt(cursor.getColumnIndex(SQLStatements.COLUMN_PRIORITY));

			// create note
			Note note = new Note(id);
			// set text
			note.setText(text);
			// set done
			note.setDone(intState == 1);
			// set create time
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(dateMs);
			note.setEditTime(calendar);
			// set priority
			note.setPriority(intPriority);

			ret.add(note);
		}
		return ret;
	}

	public void updateNote(Note note)
	{
		ContentValues values = new ContentValues();
		values.put(SQLStatements.COLUMN_STATE, note.isDone()? 1: 0);

		int rows = mDatabase.update(
				SQLStatements.TABLE_NAME, values,
				SQLStatements._ID + "=?",
				new String[]{String.valueOf(note.getID())}
		);

		if (rows > 0)
			refreshView();
	}

	public void deleteNote(Note note)
	{
		int rows = mDatabase.delete(
				SQLStatements.TABLE_NAME,
				SQLStatements._ID + "=?",
				new String[]{String.valueOf(note.getID())}
		);

		if (rows > 0)
			refreshView();
	}

	public void setOrder(boolean order)
	{
		// set order control signal
		mOrder = order;

		// set button text
		if(order == ORDER_BY_ASC)
			mOrderBtn.setText("升序");
		else if(order == ORDER_BY_DESC)
			mOrderBtn.setText("降序");
	}

	public void setOrderType(boolean orderType)
	{
		// set order type control signal
		mOrderType = orderType;

		// set button text
		if(orderType == ORDER_BY_PRIORITY)
			mOrderTypeBtn.setText("按优先级排序");
		else if(orderType == ORDER_BY_TIME)
			mOrderTypeBtn.setText("按修改时间排序");

	}
}
