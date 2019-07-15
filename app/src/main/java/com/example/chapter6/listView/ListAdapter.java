package com.example.chapter6.listView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chapter6.R;
import com.example.chapter6.model.Note;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter
{
	private List<Note> mList;

	public ListAdapter()
	{
		mList = new ArrayList<Note>();
	}

	public void refreshView(List<Note> list)
	{
		mList.clear();
		if(list != null)
			mList.addAll(list);

		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
	{
		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.item_note, viewGroup, false);
		return new ListViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i)
	{
		ListViewHolder holder = (ListViewHolder)viewHolder;
		holder.bindView(mList.get(i));
	}

	@Override
	public int getItemCount()
	{
		return mList.size();
	}
}
