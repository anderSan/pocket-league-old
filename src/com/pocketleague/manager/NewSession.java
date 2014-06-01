package com.pocketleague.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
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
import com.pocketleague.manager.enums.SessionType;

public class NewSession extends MenuContainerActivity {
	Long sId;
	Session s;
	Dao<Session, Long> sDao;

	TextView name;
	Spinner spinner_sessionType;
	Spinner spinner_ruleSet;
	CheckBox isActiveCB;
	ListView rosterCheckList;

	List<Team> teams = new ArrayList<Team>();
	List<Integer> teamIdxList = new ArrayList<Integer>();
	List<String> teamNames = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);

		name = (TextView) findViewById(R.id.editText_sessionName);
		Button createButton = (Button) findViewById(R.id.button_createSession);

		spinner_sessionType = (Spinner) findViewById(R.id.newSession_sessionType);
		List<String> sessionTypes = new ArrayList<String>();
		for (SessionType st : SessionType.values()) {
			sessionTypes.add(st.toString());
		}
		ArrayAdapter<String> stAdapter = new SpinnerAdapter(this,
				android.R.layout.simple_spinner_dropdown_item, sessionTypes,
				Arrays.asList(SessionType.values()));
		stAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_sessionType.setAdapter(stAdapter);

		spinner_ruleSet = (Spinner) findViewById(R.id.newSession_spinner_ruleSet);
		List<String> ruleSetDescriptions = new ArrayList<String>();
		GameType currentGameType = getCurrentGameType();
		for (GameRule gr : currentGameType.toGameRules()) {
			ruleSetDescriptions.add(gr.toRuleSet().getDescription());
		}
		ArrayAdapter<String> rsAdapter = new SpinnerAdapter(this,
				android.R.layout.simple_spinner_dropdown_item,
				ruleSetDescriptions, currentGameType.toGameRules());
		rsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_ruleSet.setAdapter(rsAdapter);

		isActiveCB = (CheckBox) findViewById(R.id.newSession_isActive);

		try {
			teams = Team.getDao(this).queryForAll();
			teamNames.clear();
			for (Team t : teams) {
				teamNames.add(t.getTeamName());
			}
		} catch (SQLException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		Intent intent = getIntent();
		sId = intent.getLongExtra("SID", -1);
		if (sId != -1) {
			try {
				sDao = Session.getDao(this);
				s = sDao.queryForId(sId);
				createButton.setText("Modify");
				name.setText(s.getSessionName());
				spinner_sessionType.setVisibility(View.GONE);
				spinner_ruleSet.setVisibility(View.GONE);
				isActiveCB.setVisibility(View.VISIBLE);
				isActiveCB.setChecked(s.getIsActive());

				// TODO: if loading a session, show player/team names or hide
				// box but dont allow session roster to change or bad things
				// could happen!
				teamNames.clear();
			} catch (SQLException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

		rosterCheckList = (ListView) findViewById(R.id.newSession_playerSelection);
		rosterCheckList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		updateRosterCheckList();
		rosterCheckList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView arg0, View view, int pos,
					long arg3) {

				if (teamIdxList.contains(pos)) {
					teamIdxList.remove((Integer) pos);
				} else {
					teamIdxList.add(pos);
				}

				// String strText = "";
				//
				// Collections.sort(teamIdxList);
				// for (int i = 0; i < teamIdxList.size(); i++)
				// strText += teams.get(teamIdxList.get(i)).getTeamName()
				// + ",";
			}
		});
	}

	public void updateRosterCheckList() {
		rosterCheckList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, teamNames));
	}

	public void createNewSession(View view) {
		Context context = getApplicationContext();
		Session session = null;
		String session_name = null;
		SessionType session_type;
		GameRule game_rule;
		Boolean is_active = true;

		List<SessionMember> sMembers = new ArrayList<SessionMember>();

		// get the session name
		String st;
		st = name.getText().toString().trim().toLowerCase(Locale.US);
		if (!st.isEmpty()) {
			session_name = st;
		}

		// get the session type
		session_type = (SessionType) spinner_sessionType.getSelectedView()
				.getTag();

		// get the game rules
		game_rule = (GameRule) spinner_ruleSet.getSelectedView().getTag();

		int team_size = 1;

		// make the new session or modify an existing one
		if (sId != -1) {
			s.setSessionName(session_name);
			s.setIsActive(isActiveCB.isChecked());

			try {
				sDao.update(s);
				Toast.makeText(context, "Session modified.", Toast.LENGTH_SHORT)
						.show();
				finish();
			} catch (SQLException e) {
				e.printStackTrace();
				loge("Could not modify session", e);
				Toast.makeText(context, "Could not modify session.",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			// create the session
			session = new Session(session_name, getCurrentGameType(),
					game_rule, session_type, team_size);

			try {
				sDao = getHelper().getSessionDao();
				sDao.create(session);
				Toast.makeText(context, "Session created!", Toast.LENGTH_SHORT)
						.show();
			} catch (SQLException e) {
				loge("Could not create session", e);
				Toast.makeText(context, "Could not create session.",
						Toast.LENGTH_SHORT).show();
			}

			// convert the indices from the roster list to actual teams

			List<Team> roster = new ArrayList<Team>();
			for (Integer teamIdx : teamIdxList) {
				roster.add(teams.get(teamIdx));
			}

			roster = seedRoster(roster);

			int ii = 0;
			for (Team t : roster) {
				sMembers.add(new SessionMember(session, t, ii));
				ii++;
			}

			// create the session members
			try {
				Dao<SessionMember, Long> smDao = getHelper()
						.getSessionMemberDao();
				for (SessionMember sm : sMembers) {
					smDao.create(sm);
				}
				finish();
			} catch (SQLException e) {
				loge("Could not create session member", e);
				Toast.makeText(context, "Could not create session member.",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public List<Team> seedRoster(List<Team> roster) {
		// only random seeding so far...
		Collections.shuffle(roster);

		return roster;
	}

}
