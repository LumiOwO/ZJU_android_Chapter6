package com.example.chapter6.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.chapter6.R;
import com.example.chapter6.database.DatabaseHelper;
import com.example.chapter6.database.SQLStatements;
import com.example.chapter6.model.Note;

import java.util.Calendar;

public class NewNoteActivity extends AppCompatActivity
{
	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDatabase;

	// widgets
	private EditText mEditText;
	private RadioGroup mRadioGroup;

	private boolean isEdit = false;
	private Note mNote = null;

	public static void launch(Activity activity, int requestCode)
	{
		launch(activity, requestCode, null);
	}

	public static void launch(Activity activity, int requestCode, Note note)
	{
		Intent intent = new Intent(activity, NewNoteActivity.class);
		intent.putExtra("requestCode", requestCode);
		intent.putExtra("note", note);

		activity.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newnote);

		// init database
		mDBHelper = new DatabaseHelper(this);
		mDatabase = mDBHelper.getWritableDatabase();

		mEditText = findViewById(R.id.newNote_text);
		mRadioGroup = findViewById(R.id.newNote_radioGroup);

		// check whether it's an edit request
		isEdit = getIntent().getIntExtra("requestCode", 0)
				== DetailActivity.EDIT_NOTE_REQUEST;
		// if is edit, set the content of note to the current view
		if(isEdit)
		{
			mNote = (Note)getIntent().getSerializableExtra("note");
			mEditText.setText(mNote.getText());

			RadioButton radioButton = findViewById(mNote.getPriorityBtnID());
			radioButton.setChecked(true);
		}

		// set button callback
		final Button button = findViewById(R.id.btn_confirm);
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String text = mEditText.getText().toString();
				if (text.isEmpty())
					makeToast(getString(R.string.text_empty));
				else {
					// get priority, default is none
					int buttonId = mRadioGroup.getCheckedRadioButtonId();
					int priority = Note.PRIORITY_NONE;
					if(buttonId == R.id.btn_priority_high)
						priority = Note.PRIORITY_HIGH;
					else if(buttonId == R.id.btn_priority_mid)
						priority = Note.PRIORITY_MID;
					else if(buttonId == R.id.btn_priority_low)
						priority = Note.PRIORITY_LOW;
					else if(buttonId == R.id.btn_priority_none)
						priority = Note.PRIORITY_NONE;

					// get other infomation
					ContentValues values = new ContentValues();
					values.put(SQLStatements.COLUMN_CONTENT, text);
					values.put(SQLStatements.COLUMN_STATE, 0);
					values.put(SQLStatements.COLUMN_DATE, Calendar.getInstance().getTimeInMillis());
					values.put(SQLStatements.COLUMN_PRIORITY, priority);

					// exec database operation
					if (isEdit) {
						editNote(values);
					} else {
						insertNote(values);
					}
				}
			}
		});
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

	private void makeToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	private void editNote(ContentValues values)
	{
		int rows = mDatabase.update(
				SQLStatements.TABLE_NAME, values,
				SQLStatements._ID + "=?",
				new String[]{String.valueOf(mNote.getID())}
		);
		if (rows > 0)
		{
			setResult(Activity.RESULT_OK);
			makeToast(getString(R.string.newnote_success));
			NewNoteActivity.this.finish();
		}
		else
		{
			makeToast(getString(R.string.db_error));
		}
	}

	private void insertNote(ContentValues values)
	{
		long rowId = mDatabase.insert(SQLStatements.TABLE_NAME, null, values);
		if(rowId == -1)
		{
			makeToast(getString(R.string.db_error));
		}
		else
		{
			setResult(Activity.RESULT_OK);
			makeToast(getString(R.string.newnote_success));
			NewNoteActivity.this.finish();
		}
	}



}
