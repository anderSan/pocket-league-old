package com.pocketleague.manager.backend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pocketleague.manager.R;

public class NavDrawerAdapter extends ArrayAdapter<NavDrawerItem> {
	private Context context;
	private int layoutId;
	private NavDrawerItem[] drawerItems;

	public NavDrawerAdapter(Context context, int layoutId,
			NavDrawerItem[] drawerItems) {
		super(context, layoutId, drawerItems);
		this.context = context;
		this.layoutId = layoutId;

		this.drawerItems = drawerItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		NavDrawerItem item = getItem(position);
		TextView tvLabel = null;
		ImageView ivIcon = null;
		TextView tvCounter = null;

		if (!item.isHeader) {
			if (v == null) {
				int layout = R.layout.nav_drawer_item;
				v = LayoutInflater.from(getContext()).inflate(layout, null);
			}

			tvLabel = (TextView) v.findViewById(R.id.navLabel);
			ivIcon = (ImageView) v.findViewById(R.id.navIcon);
			tvCounter = (TextView) v.findViewById(R.id.navCounter);

			if (item != null) {
				if (tvLabel != null) {
					tvLabel.setText(item.label);
					v.setTag(item.label);
				}

				if (tvCounter != null) {
					if (item.counter > 0) {
						tvCounter.setText("" + item.counter);
					} else {
						tvCounter.setText("");
					}
				}

				if (ivIcon != null) {
					if (item.iconId > 0) {
						ivIcon.setVisibility(View.VISIBLE);
						ivIcon.setImageResource(item.iconId);
					} else {
						ivIcon.setVisibility(View.GONE);
					}
				}
			}
		} else {
			if (v == null) {
				int layout = R.layout.nav_drawer_header;
				v = LayoutInflater.from(getContext()).inflate(layout, null);
			}
		}

		return v;
	}
}