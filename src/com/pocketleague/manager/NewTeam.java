package com.pocketleague.manager;

import java.sql.SQLException;
import java.util.ArrayList;
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
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.R.color;
import com.pocketleague.manager.backend.MenuContainerActivity;
import com.pocketleague.manager.db.tables.Player;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.db.tables.TeamMember;

public class NewTeam extends MenuContainerActivity {
	Long tId;
	Team t;
	Dao<Team, Long> tDao;

	TextView name;
	ListView rosterCheckList;
	int p1_pos = 0;
	int p2_pos = 1;
	CheckBox isActiveCB;

	List<Player> players = new ArrayList<Player>();
	List<Integer> playerIdxList = new ArrayList<Integer>();
	List<String> playerNames = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_team);

		name = (TextView) findViewById(R.id.editText_teamName);
		Button createButton = (Button) findViewById(R.id.button_createTeam);
		isActiveCB = (CheckBox) findViewById(R.id.newTeam_isActive);

		try {
			players = Player.getDao(this).queryForAll();
			playerNames.clear();
			for (Player p : players) {
				playerNames.add(p.getFirstName() + " " + p.getLastName());
			}
		} catch (SQLException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		Intent intent = getIntent();
		tId = intent.getLongExtra("TID", -1);
		if (tId != -1) {
			try {
				tDao = Team.getDao(this);
				t = tDao.queryForId(tId);
				createButton.setText("Modify");
				name.setText(t.getTeamName());
				isActiveCB.setVisibility(View.VISIBLE);
				isActiveCB.setChecked(t.getIsActive());

				// TODO: if loading a team, show players but dont allow team
				// size to change
				playerNames.clear();
			} catch (SQLException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

		rosterCheckList = (ListView) findViewById(R.id.newTeam_playerSelection);
		rosterCheckList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		updateRosterCheckList();
		rosterCheckList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView arg0, View view, int pos,
					long arg3) {
				if (playerIdxList.contains(pos)) {
					playerIdxList.remove((Integer) pos);
				} else {
					playerIdxList.add(pos);
				}
			}
		});
	}

	public void updateRosterCheckList() {
		rosterCheckList
				.setAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_multiple_choice,
						playerNames));
	}

	public void createNewTeam(View view) {
		Context context = getApplicationContext();
		Team newTeam = null;
		String teamName = null;

		String s = name.getText().toString().trim().toLowerCase(Locale.US);
		if (!s.isEmpty()) {
			teamName = s;
		}

		if (tId != -1) {
			t.setTeamName(teamName);
			t.setIsActive(isActiveCB.isChecked());
			try {
				tDao.update(t);
				Toast.makeText(context, "Team modified.", Toast.LENGTH_SHORT)
						.show();
				finish();
			} catch (SQLException e) {
				e.printStackTrace();
				loge("Could not modify team", e);
				Toast.makeText(context, "Could not modify team.",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			newTeam = new Team(teamName, playerIdxList.size(), color.Aqua);

			try {
				Dao<Team, Long> dao = getHelper().getTeamDao();
				dao.create(newTeam);
				Toast.makeText(context, "Team created!", Toast.LENGTH_SHORT)
						.show();
			} catch (SQLException e) {
				loge("Could not create team", e);
				boolean team_exists = false;
				try {
					team_exists = newTeam.exists(context);
					if (team_exists) {
						Toast.makeText(context, "Team already exists.",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "Could not create team.",
								Toast.LENGTH_SHORT).show();
					}
				} catch (SQLException ee) {
					Toast.makeText(context, ee.getMessage(), Toast.LENGTH_LONG)
							.show();
					loge("Could not test for existence of team", ee);
				}
			}

			try {
				Dao<TeamMember, Long> tmDao = getHelper().getTeamMemberDao();
				for (Integer playerIdx : playerIdxList) {
					Player p = players.get(playerIdx);
					TeamMember tm = new TeamMember(newTeam, p);
					tmDao.create(tm);
				}
				finish();
			} catch (SQLException e) {
				loge("Could not create team member.", e);
				Toast.makeText(context, "Could not create team member.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
