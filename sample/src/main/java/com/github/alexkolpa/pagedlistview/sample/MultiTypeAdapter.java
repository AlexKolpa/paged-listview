package com.github.alexkolpa.pagedlistview.sample;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MultiTypeAdapter extends BaseAdapter {

	List<String> items;

	public MultiTypeAdapter() {
		items = new ArrayList<>();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public String getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return position % 2 == 0 ? 0 : 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null) {
			convertView = new TextView(parent.getContext());
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			convertView.setPadding(20, 20, 20, 20);
			convertView.setLayoutParams(params);
		}

		((TextView) convertView).setText(getItem(position));

		return convertView;
	}

	public void addAll(List<String> newItems) {
		items.addAll(newItems);
		notifyDataSetChanged();
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}
}
