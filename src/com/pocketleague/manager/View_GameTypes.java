package com.pocketleague.manager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.pocketleague.gametypes.GameType;
import com.pocketleague.manager.backend.ListAdapter_GameType;
import com.pocketleague.manager.backend.ViewHolder_GameType;
import com.pocketleague.manager.db.OrmLiteFragment;

public class View_GameTypes extends OrmLiteFragment {
	private static final String LOGTAG = "View_GameTypes";

	private ListAdapter_GameType gameTypeAdapter;
	private List<ViewHolder_GameType> gametypes_list = new ArrayList<ViewHolder_GameType>();
	private GridView gv;
	private Switch viewAllGames;
	private View rootView;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_view_gametypes,
				container, false);

		gv = (GridView) rootView.findViewById(R.id.gametypes_view);
		gameTypeAdapter = new ListAdapter_GameType(context, R.layout.grid_item,
				gametypes_list);
		gv.setAdapter(gameTypeAdapter);
		gv.setOnItemClickListener(gvItemClicked);
		// gv.setOnItemLongClickListener(elvItemLongClicked);

		viewAllGames = new Switch(context);
		viewAllGames.setTextOff("Unfinished");
		viewAllGames.setTextOn("All");
		viewAllGames.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshGameTypesListing();
			}
		});

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 20, 0, 20);
		// lp.addRule(RelativeLayout.BELOW, R.id.dbListing);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		((RelativeLayout) rootView).addView(viewAllGames, lp);
		rootView.requestLayout();
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = getActivity();
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem fav = menu.add("New Game");
		fav.setIcon(R.drawable.ic_menu_add);
		fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		fav.setIntent(new Intent(context, NewGame.class));
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshGameTypesListing();
	}

	protected void refreshGameTypesListing() {
		gametypes_list.clear();
		// add all the sessions to the headers

		for (GameType gt : GameType.values()) {
			ViewHolder_GameType gtvh = new ViewHolder_GameType();
			gtvh.setGameType(gt);
			gtvh.setName(gt.toString());
			gtvh.setDrawableId(gt.toDrawableId());
			gametypes_list.add(gtvh);
		}

		// required if list has changed
		gameTypeAdapter.notifyDataSetChanged();
	}

	private OnItemClickListener gvItemClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			GameType gt = (GameType) view.getTag();
			setCurrentGameType(gt);
			mNav.viewSessions();
		}
	};
}
