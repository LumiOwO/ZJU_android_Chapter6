package com.example.chapter6.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chapter6.R;
import com.example.chapter6.model.Note;

public class DetailActivity extends AppCompatActivity
{
	public static final int EDIT_NOTE_REQUEST = 9999;

	public static void launch(Activity activity, Note note)
	{
		Intent intent = new Intent(activity, DetailActivity.class);
		intent.putExtra("note", note);

		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		// get note
		final Note note = (Note)getIntent().getSerializableExtra("note");

		// get widgets
		TextView detailText = findViewById(R.id.detail_text);
		TextView priorityText = findViewById(R.id.detail_priority);
		TextView editTimeText = findViewById(R.id.detail_editTime);
		Button editButton = findViewById(R.id.btn_edit);

		// set detail text
		detailText.setText(note.getText());
		// set priority text
		priorityText.setText(note.getPriorityText());
		priorityText.setTextColor(note.getPriorityColor());
		// set edit time text
		editTimeText.setText(note.getEditTimeText());

		// set edit button callback
		editButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				NewNoteActivity.launch(DetailActivity.this, EDIT_NOTE_REQUEST, note);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK)
			this.finish();
	}
}
