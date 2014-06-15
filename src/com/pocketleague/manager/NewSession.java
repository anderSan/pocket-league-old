package com.pocketleague.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.gametypes.GameRule;
import com.pocketleague.gametypes.GameType;
import com.pocketleague.manager.backend.MenuContainerActivity;
import com.pocketleague.manager.backend.SpinnerAdapter;
import com.pocketleague.manager.db.tables.Session;
import com.pocketleague.manager.db.tables.SessionMember;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.db.tables.Venue;
import com.pocketleague.manager.enums.SessionType;

public class NewSession extends MenuContainerActivity {
	Long sId;
	Session s;
	Dao<Session, Long> sDao;
	Dao<SessionMember, Long> smDao;

	Button btn_create;
	TextView tv_name;
	Spinner sp_sessionType;
	Spinner sp_ruleSet;
	Spinner sp_venues;
	TextView tv_num_selected;
	ListView lv_roster;
	CheckBox cb_isFavorite;

	List<Team> teams = new ArrayList<Team>();
	List<Integer> teamIdxList = new ArrayList<Integer>();
	List<String> teamNames = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);

		sDao = Session.getDao(this);
		smDao = SessionMember.getDao(this);

		btn_create = (Button) findViewById(R.id.button_createSession);
		tv_name = (TextView) findViewById(R.id.editText_sessionName);
		sp_sessionType = (Spinner) findViewById(R.id.newSession_sessionType);
		sp_ruleSet = (Spinner) findViewById(R.id.newSession_ruleSet);
		sp_venues = (Spinner) findViewById(R.id.newSession_venues);
		tv_num_selected = (TextView) findViewById(R.id.tv_num_selected);
		lv_roster = (ListView) findViewById(R.id.newSession_teamSelection);
		cb_isFavorite = (CheckBox) findViewById(R.id.newSession_isFavorite);

		List<String> sessionTypes = new ArrayList<String>();
		for (SessionType st : SessionType.values()) {
			sessionTypes.add(st.toString());
		}
		ArrayAdapter<String> stAdapter = new SpinnerAdapter(this,
				android.R.layout.simple_spinner_item, sessionTypes,
				Arrays.asList(SessionType.values()));
		sp_sessionType.setAdapter(stAdapter);

		List<String> ruleSetDescriptions = new ArrayList<String>();
		GameType currentGameType = getCurrentGameType();
		for (GameRule gr : currentGameType.toGameRules()) {
			ruleSetDescriptions.add(gr.toRuleSet().getDescription());
		}
		ArrayAdapter<String> rsAdapter = new SpinnerAdapter(this,
				android.R.layout.simple_spinner_item, ruleSetDescriptions,
				currentGameType.toGameRules());
		sp_ruleSet.setAdapter(rsAdapter);

		try {
			List<Venue> venues = Venue.getDao(this).queryForAll();
			List<String> venueNames = new ArrayList<String>();
			for (Venue v : venues) {
				venueNames.add(v.getName());
			}
			ArrayAdapter<String> vAdapter = new SpinnerAdapter(this,
					android.R.layout.simple_spinner_dropdown_item, venueNames,
					venues);
			sp_venues.setAdapter(vAdapter);
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		try {
			teams = Team.getDao(this).queryForAll();
			teamNames.clear();
			for (Team t : teams) {
				teamNames.add(t.getTeamName());
			}
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		updateRosterCheckList();
		lv_roster.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView arg0, View view, int pos,
					long arg3) {
				if (teamIdxList.contains(pos)) {
					teamIdxList.remove((Integer) pos);
				} else {
					teamIdxList.add(pos);
				}
				tv_num_selected.setText(teamIdxList.size() + " selected");
			}
		});

		Intent intent = getIntent();
		sId = intent.getLongExtra("SID", -1);
		if (sId != -1) {
			loadSessionValues();
		}
	}

	private void loadSessionValues() {
		try {
			s = sDao.queryForId(sId);
			btn_create.setText("Modify");
			tv_name.setText(s.getSessionName());
			sp_sessionType.setVisibility(View.GONE);
			sp_ruleSet.setVisibility(View.GONE);
			cb_isFavorite.setChecked(s.getIsFavorite());

			// TODO: if loading a session, show player/team names or hide
			// box but dont allow session roster to change or bad things
			// could happen!
			teamNames.clear();
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void updateRosterCheckList() {
		lv_roster.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, teamNames));
	}

	public void doneButtonPushed(View view) {
		String session_name = tv_name.getText().toString().trim();
		if (session_name.isEmpty()) {
			Toast.makeText(this, "Session name is required.", Toast.LENGTH_LONG)
					.show();
		} else {
			SessionType session_type = (SessionType) sp_sessionType
					.getSelectedView().getTag();
			GameRule game_rule = (GameRule) sp_ruleSet.getSelectedView()
					.getTag();
			Venue current_venue = (Venue) sp_venues.getSelectedView().getTag();
			Boolean is_favorite = cb_isFavorite.isChecked();

			if (sId != -1) {
				modifySession(session_name, current_venue, is_favorite);
			} else {
				createSession(session_name, game_rule, session_type,
						current_venue, is_favorite);
			}
		}
	}

	private void createSession(String session_name, GameRule game_rule,
			SessionType session_type, Venue current_venue, boolean is_favorite) {
		int team_size = teamIdxList.size();
		Session newSession = new Session(session_name, getCurrentGameType(),
				game_rule, session_type, team_size, current_venue);
		newSession.setIsFavorite(is_favorite);

		List<Team> roster = new ArrayList<Team>();
		for (Integer teamIdx : teamIdxList) {
			roster.add(teams.get(teamIdx));
		}
		roster = seedRoster(roster);

		try {
			sDao.create(newSession);
			int seed = 0;
			for (Team t : roster) {
				SessionMember sm = new SessionMember(newSession, t, seed);
				smDao.create(sm);
				seed++;
			}
			Toast.makeText(this, "Session created!", Toast.LENGTH_SHORT).show();
			finish();
		} catch (SQLException e) {
			loge("Could not create session", e);
			Toast.makeText(this, "Could not create session.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void modifySession(String session_name, Venue current_venue,
			boolean is_favorite) {
		s.setSessionName(session_name);
		s.setCurrentVenue(current_venue);
		s.setIsFavorite(is_favorite);

		try {
			sDao.update(s);
			Toast.makeText(this, "Session modified.", Toast.LENGTH_SHORT)
					.show();
			finish();
		} catch (SQLException e) {
			e.printStackTrace();
			loge("Could not modify session", e);
			Toast.makeText(this, "Could not modify session.",
					Toast.LENGTH_SHORT).show();
		}
	}

	public List<Team> seedRoster(List<Team> roster) {
		// only random seeding so far...
		Collections.shuffle(roster);

		return roster;
	}

}
