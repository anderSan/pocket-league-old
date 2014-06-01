package com.pocketleague.manager.backend;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SpinnerAdapter extends ArrayAdapter<String> {
	private Context context;
	private int layoutResourceId;
	private List<String> strings = new ArrayList<String>();
	private List<?> tags;

	public SpinnerAdapter(Context context, int layoutResourceId,
			List<String> data, List<?> tags) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.strings = data;
		this.tags = tags;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		v.setTag(tags.get(position));

		return v;
	}

	@Override
	public int getCount() {
		return strings.size();
	}

	@Override
	public String getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}
}