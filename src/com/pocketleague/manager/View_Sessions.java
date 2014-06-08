package com.pocketleague.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Switch;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.backend.ListAdapter_Session;
import com.pocketleague.manager.backend.ViewHolderHeader_Session;
import com.pocketleague.manager.backend.ViewHolder_Session;
import com.pocketleague.manager.db.OrmLiteFragment;
import com.pocketleague.manager.db.tables.Session;
import com.pocketleague.manager.enums.SessionType;

public class View_Sessions extends OrmLiteFragment {
	private static final String LOGTAG = "View_Sessions";

	private LinkedHashMap<String, ViewHolderHeader_Session> sHash = new LinkedHashMap<String, ViewHolderHeader_Session>();
	private ArrayList<ViewHolderHeader_Session> statusList = new ArrayList<ViewHolderHeader_Session>();
	private ListAdapter_Session sessionAdapter;
	private ExpandableListView elv;
	private Switch sw_open;
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
		rootView = inflater.inflate(R.layout.activity_view_listing, container,
				false);

		elv = (ExpandableListView) rootView.findViewById(R.id.dbListing);
		sessionAdapter = new ListAdapter_Session(context, statusList);
		elv.setAdapter(sessionAdapter);
		expandAll();
		elv.setOnChildClickListener(elvItemClicked);
		elv.setOnGroupClickListener(elvGroupClicked);

		sw_open = (Switch) rootView.findViewById(R.id.sw_showActive);
		sw_open.setTextOn("Open");
		sw_open.setTextOff("Closed");
		sw_open.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				refreshSessionListing();
			}
		});
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = getActivity();
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem fav = menu.add("New Session");
		fav.setIcon(R.drawable.ic_menu_add);
		fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		fav.setIntent(new Intent(context, NewSession.class));
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshSessionListing();
	}

	private void expandAll() {
		// method to expand all groups
		int count = sessionAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			elv.expandGroup(i);
		}
	}

	private void collapseAll() {
		// method to collapse all groups
		int count = sessionAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			elv.collapseGroup(i);
		}
	}

	protected void refreshSessionListing() {
		sHash.clear();
		statusList.clear();

		// add all the statii to the headers
		for (SessionType st : SessionType.values()) {
			addStatus(st);
		}

		boolean show_open = sw_open.isChecked();
		// add all the sessions
		try {
			Dao<Session, Long> sDao = getHelper().getSessionDao();
			List<Session> sessions = sDao.queryBuilder().where()
					.eq(Session.IS_ACTIVE, show_open).and()
					.eq(Session.GAME_TYPE, getCurrentGameType()).query();
			for (Session s : sessions) {
				addSession(String.valueOf(s.getId()), s.getSessionName(),
						s.getSessionType());
			}
		} catch (SQLException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e(View_Sessions.class.getName(),
					"Retrieval of sessions failed", e);
		}

		expandAll();
		sessionAdapter.notifyDataSetChanged(); // required in case the list has
												// changed
	}

	private OnChildClickListener elvItemClicked = new OnChildClickListener() {
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			// get the group header
			ViewHolderHeader_Session statusInfo = statusList.get(groupPosition);
			SessionType session_type = statusInfo.getSessionType();
			// get the child info
			ViewHolder_Session sessionInfo = statusInfo.getSessionList().get(
					childPosition);
			// display it or do something with it
			Toast.makeText(context, "Selected " + sessionInfo.getName(),
					Toast.LENGTH_SHORT).show();

			// load the game in progress screen
			Long sId = Long.valueOf(sessionInfo.getId());
			Intent intent = new Intent(context, session_type.toClass());
			intent.putExtra("SID", sId);
			startActivity(intent);
			return false;
		}
	};
	private OnGroupClickListener elvGroupClicked = new OnGroupClickListener() {
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {

			// ViewHolderHeader_Session statusInfo =
			// statusList.get(groupPosition);
			// Toast.makeText(context, "Tapped " + statusInfo.getName(),
			// Toast.LENGTH_SHORT).show();
			return false;
		}
	};

	private void addStatus(SessionType st) {
		ViewHolderHeader_Session vhh_Session = new ViewHolderHeader_Session();
		vhh_Session.setSessionType(st);
		statusList.add(vhh_Session);
		sHash.put(st.name(), vhh_Session);
	}

	private void addSession(String session_id, String session_name,
			SessionType session_type) {

		ViewHolderHeader_Session statusInfo = sHash.get(session_type.name());
		ArrayList<ViewHolder_Session> sessionList = statusInfo.getSessionList();

		// create a new child and add that to the group
		ViewHolder_Session sessionInfo = new ViewHolder_Session();
		sessionInfo.setId(session_id);
		sessionInfo.setName(session_name);
		sessionList.add(sessionInfo);
		statusInfo.setSessionList(sessionList);
	}
}
