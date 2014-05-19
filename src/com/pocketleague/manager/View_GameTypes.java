package com.pocketleague.manager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.pocketleague.gametypes.GameType;
import com.pocketleague.manager.backend.ListAdapter_GameType;
import com.pocketleague.manager.backend.NavigationInterface;
import com.pocketleague.manager.backend.ViewHolder_GameType;
import com.pocketleague.manager.db.OrmLiteFragment;

public class View_GameTypes extends OrmLiteFragment {
	private static final String LOGTAG = "View_GameTypes";
	NavigationInterface mNav;

	private ListAdapter_GameType gameTypeAdapter;
	private List<ViewHolder_GameType> gametypes_list = new ArrayList<ViewHolder_GameType>();
	private GridView gv;
	private Switch viewAllGames;
	private View rootView;
	private Context context;

	private SharedPreferences.Editor prefs_editor;

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
		// gv.setOnClickListener(elvItemClicked);
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

		SharedPreferences settings = this.getActivity().getSharedPreferences(
				APP_PREFS, 0);
		GameType currentGameType = GameType.valueOf(settings.getString(
				"currentGameType", GameType.UNDEFINED.name()));

		Toast.makeText(context, "Game type is " + currentGameType.name(),
				Toast.LENGTH_SHORT).show();
		prefs_editor = settings.edit();

		try {
			mNav = (NavigationInterface) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement NavigationInterface");
		}
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
			gtvh.setName(gt.name());
			gtvh.setDrawableId(gt.toDrawableId());
			gametypes_list.add(gtvh);
		}

		// required if list has changed
		gameTypeAdapter.notifyDataSetChanged();
	}
	// private OnChildClickListener elvItemClicked = new OnChildClickListener()
	// {
	// public boolean onChildClick(ExpandableListView parent, View v,
	// int groupPosition, int childPosition, long id) {
	//
	// // get the group header
	// ViewHolderHeader_Game sessionInfo = sessionList.get(groupPosition);
	// // get the child info
	// ViewHolder_Game gameInfo = sessionInfo.getGameList().get(
	// childPosition);
	// // display it or do something with it
	// Toast.makeText(
	// context,
	// "Selected " + sessionInfo.getName() + "/"
	// + String.valueOf(gameInfo.getId()),
	// Toast.LENGTH_SHORT).show();
	//
	// prefs_editor
	// .putString("currentGameType", GameType.BILLIARDS.name());
	// prefs_editor.commit();
	//
	// Toast.makeText(context,
	// "Game type is now " + GameType.BILLIARDS.name(),
	// Toast.LENGTH_SHORT).show();
	//
	// // load the game in progress screen
	// // Long gId = Long.valueOf(gameInfo.getId());
	// // mNav.loadGame(gId);
	// return true;
	// }
	// };

	// private OnItemLongClickListener elvItemLongClicked = new
	// OnItemLongClickListener() {
	// @Override
	// public boolean onItemLongClick(AdapterView<?> parent, View view,
	// int position, long id) {
	// if (ExpandableListView.getPackedPositionType(id) ==
	// ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
	// int groupPosition = ExpandableListView
	// .getPackedPositionGroup(id);
	// int childPosition = ExpandableListView
	// .getPackedPositionChild(id);
	//
	// // get the group header
	// ViewHolderHeader_Game sessionInfo = sessionList
	// .get(groupPosition);
	// // get the child info
	// ViewHolder_Game gameInfo = sessionInfo.getGameList().get(
	// childPosition);
	// // display it or do something with it
	// Toast.makeText(
	// context,
	// "Selected " + sessionInfo.getName() + "/"
	// + String.valueOf(gameInfo.getId()),
	// Toast.LENGTH_SHORT).show();
	//
	// // load the game in progress screen
	// Long gid = Long.valueOf(gameInfo.getId());
	// Intent intent = new Intent(context, Detail_Game.class);
	// intent.putExtra("GID", gid);
	// startActivity(intent);
	// return true;
	// }
	// return false;
	// }
	// };

	// private void addGameType(GameType gameId, String p1, String p2, String
	// score) {
	// logd("addGame() - adding game " + gameId);
	// // find the index of the session header
	// ViewHolderHeader_Game sessionInfo = sHash.get(sort);
	// List<ViewHolder_Game> gameList = sessionInfo.getGameList();
	//
	// // create a new child and add that to the group
	// ViewHolder_Game gameInfo = new ViewHolder_Game();
	// gameInfo.setId(gameId);
	// gameInfo.setPlayerOne(p1);
	// gameInfo.setPlayerTwo(p2);
	// gameInfo.setScore(score);
	// gameList.add(gameInfo);
	// sessionInfo.setGameList(gameList);
	// }
}
