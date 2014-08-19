package com.pocketleague.manager.backend;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pocketleague.gametypes.ScoreType;
import com.pocketleague.manager.R;

public class ListAdapter_GameScore extends ArrayAdapter<ViewHolder_GameScore> {
	private Context context;
	private int layoutResourceId;
	private List<ViewHolder_GameScore> gamescore_list = new ArrayList<ViewHolder_GameScore>();
	private ScoreType scoretype;

	public ListAdapter_GameScore(Context context, int layoutResourceId,
			List<ViewHolder_GameScore> data, ScoreType scoretype) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.gamescore_list = data;
		this.scoretype = scoretype;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView;
		TextView textView;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = new View(context);
			itemView = inflater.inflate(R.layout.list_item_gamescore, null);
		} else {
			itemView = convertView;
		}

		textView = (TextView) itemView.findViewById(R.id.tv_memberName);
		textView.setText(gamescore_list.get(position).getMemberName());

		return itemView;
	}

	@Override
	public int getCount() {
		return gamescore_list.size();
	}

	@Override
	public ViewHolder_GameScore getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}
}