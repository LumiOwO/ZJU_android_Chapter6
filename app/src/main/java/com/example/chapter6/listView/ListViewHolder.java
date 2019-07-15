package com.example.chapter6.listView;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chapter6.MainActivity;
import com.example.chapter6.R;
import com.example.chapter6.activities.DetailActivity;
import com.example.chapter6.model.Note;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ListViewHolder extends RecyclerView.ViewHolder
{
	// widgets
	private CheckBox mCheckBox;
	private TextView mTextPreview;
	private TextView mTimeView;
	private ImageButton mDeleteButton;

	private View mItemView;

	public ListViewHolder(@NonNull View itemView)
	{
		super(itemView);

		mCheckBox = itemView.findViewById(R.id.item_checkbox);
		mTextPreview = itemView.findViewById(R.id.item_previewText);
		mTimeView = itemView.findViewById(R.id.item_timeText);
		mDeleteButton = itemView.findViewById(R.id.item_delete_btn);

		mItemView = itemView.findViewById(R.id.item);
	}

	public void bindView(final Note note)
	{
		// set text
		mTextPreview.setText(note.getText());
		// set time
		mTimeView.setText(
				new SimpleDateFormat(Note.TIME_FORMAT, Locale.getDefault())
						.format(note.getEditTime().getTime()));

		// set checked
		mCheckBox.setOnCheckedChangeListener(null);
		mCheckBox.setChecked(note.isDone());
		mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				note.setDone(isChecked);
				Activity activity = getActivity(buttonView);
				updateNoteInMain((MainActivity)activity, note);
			}
		});
		// set text style
		int textColor = 0;
		int flags = 0;
		if(note.isDone())
		{
			textColor = Color.GRAY;
			flags = mTextPreview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;
		}
		else
		{
			flags = mTextPreview.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG;
			if(note.getPriority() == Note.PRIORITY_HIGH)
				textColor = Note.COLOR_PRIORITY_HIGH;
			else if(note.getPriority() == Note.PRIORITY_MID)
				textColor = Note.COLOR_PRIORITY_MID;
			else if(note.getPriority() == Note.PRIORITY_LOW)
				textColor = Note.COLOR_PRIORITY_LOW;
			else
				textColor = Color.BLACK;
		}
		mTextPreview.setTextColor(textColor);
		mTextPreview.setPaintFlags(flags);

		// set delete button
		mDeleteButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Activity activity = getActivity(v);
				deleteNoteInMain((MainActivity)activity, note);
			}
		});

		// set detail activity launcher
		mItemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Activity activity = getActivity(v);
				DetailActivity.launch(activity, note);
			}
		});

	}

	private void updateNoteInMain(MainActivity activity, Note note)
	{
		activity.updateNote(note);
	}

	private void deleteNoteInMain(MainActivity activity, Note note)
	{
		activity.deleteNote(note);
	}

	private Activity getActivity(View view) {
		Activity ret = null;

		Context context = view.getContext();
		while (context instanceof ContextWrapper) {
			if (context instanceof Activity) {
				ret = (Activity)context;
				break;
			}
			context = ((ContextWrapper)context).getBaseContext();
		}
		return ret;
	}
}
