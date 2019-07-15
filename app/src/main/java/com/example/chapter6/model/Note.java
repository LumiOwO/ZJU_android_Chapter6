package com.example.chapter6.model;

import android.graphics.Color;

import com.example.chapter6.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Note implements Serializable
{
	// priorities
	public static final int PRIORITY_HIGH = 3;
	public static final int PRIORITY_MID  = 2;
	public static final int PRIORITY_LOW  = 1;
	public static final int PRIORITY_NONE = 0;

	// colors
	public static final int COLOR_PRIORITY_HIGH = Color.rgb(255,0,0);
	public static final int COLOR_PRIORITY_MID = Color.rgb(220, 220, 0);
	public static final int COLOR_PRIORITY_LOW = Color.rgb(58, 91, 200);

	public static final String TIME_FORMAT = "最近修改于   yyyy年M月d日 HH:mm:ss";

	private long mID;
	private String mText;
	private Calendar mEditTime = Calendar.getInstance();
	private int mPriority = PRIORITY_NONE;
	private boolean mDone = false;

	public Note(long id)
	{
		mID = id;
	}

	public long getID()
	{
		return mID;
	}

	public String getText()
	{
		return mText;
	}

	public void setText(String mText)
	{
		this.mText = mText;
	}

	public Calendar getEditTime()
	{
		return mEditTime;
	}

	public void setEditTime(Calendar mCreateTime)
	{
		this.mEditTime = mCreateTime;
	}

	public int getPriority()
	{
		return mPriority;
	}

	public void setPriority(int mPriority)
	{
		this.mPriority = mPriority;
	}

	public boolean isDone()
	{
		return mDone;
	}

	public void setDone(boolean mDone)
	{
		this.mDone = mDone;
	}

	public String getPriorityText()
	{
		String ret = "";
		if(mPriority == PRIORITY_HIGH)
			ret += "高";
		else if(mPriority == PRIORITY_MID)
			ret += "高";
		else if(mPriority == PRIORITY_LOW)
			ret += "高";
		else if(mPriority == PRIORITY_NONE)
			ret += "无";

		return ret;
	}

	public int getPriorityColor()
	{
		int color = Color.BLACK;
		if(mPriority == PRIORITY_HIGH)
			color = COLOR_PRIORITY_HIGH;
		else if(mPriority == PRIORITY_MID)
			color = COLOR_PRIORITY_MID;
		else if(mPriority == PRIORITY_LOW)
			color = COLOR_PRIORITY_LOW;

		return color;
	}

	public String getEditTimeText()
	{
		return new SimpleDateFormat(Note.TIME_FORMAT, Locale.getDefault())
						.format(mEditTime.getTime());
	}

	public int getPriorityBtnID()
	{
		int ret = 0;
		if(mPriority == PRIORITY_HIGH)
			ret = R.id.btn_priority_high;
		else if(mPriority == PRIORITY_MID)
			ret = R.id.btn_priority_mid;
		else if(mPriority == PRIORITY_LOW)
			ret = R.id.btn_priority_low;
		else if(mPriority == PRIORITY_NONE)
			ret = R.id.btn_priority_none;

		return ret;
	}
}
