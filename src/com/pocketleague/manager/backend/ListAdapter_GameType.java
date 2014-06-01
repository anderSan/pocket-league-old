package com.pocketleague.manager.backend;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pocketleague.manager.R;

public class ListAdapter_GameType extends ArrayAdapter<ViewHolder_GameType> {
	private Context context;
	private int layoutResourceId;
	private List<ViewHolder_GameType> gametype_list = new ArrayList<ViewHolder_GameType>();

	public ListAdapter_GameType(Context context, int layoutResourceId,
			List<ViewHolder_GameType> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.gametype_list = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View gridView;
		TextView textView;
		ImageView imageView;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.grid_item, null);
			gridView.setTag(gametype_list.get(position).getGameType());
		} else {
			gridView = convertView;
		}

		textView = (TextView) gridView.findViewById(R.id.grid_text);
		imageView = (ImageView) gridView.findViewById(R.id.grid_image);

		textView.setText(gametype_list.get(position).getName());
		imageView.setImageResource(gametype_list.get(position).getDrawableId());

		return gridView;
	}

	@Override
	public int getCount() {
		return gametype_list.size();
	}

	@Override
	public ViewHolder_GameType getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}
}