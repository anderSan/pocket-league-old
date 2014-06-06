package com.pocketleague.manager;

import java.sql.SQLException;
import java.util.ArrayList;
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
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.backend.MenuContainerActivity;
import com.pocketleague.manager.db.tables.Player;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.db.tables.TeamMember;

public class NewTeam extends MenuContainerActivity {
	Long tId;
	Team t;
	Dao<Team, Long> tDao;
	Dao<TeamMember, Long> tmDao;

	Button btn_create;
	TextView tv_name;
	TextView tv_num_selected;
	ListView lv_roster;
	CheckBox cb_isFavorite;

	List<Player> players = new ArrayList<Player>();
	List<Integer> playerIdxList = new ArrayList<Integer>();
	List<String> playerNames = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_team);

		tDao = Team.getDao(this);
		tmDao = TeamMember.getDao(this);

		btn_create = (Button) findViewById(R.id.button_createTeam);
		tv_name = (TextView) findViewById(R.id.editText_teamName);
		tv_num_selected = (TextView) findViewById(R.id.tv_num_selected);
		lv_roster = (ListView) findViewById(R.id.newTeam_playerSelection);
		cb_isFavorite = (CheckBox) findViewById(R.id.newTeam_isFavorite);

		try {
			players = Player.getDao(this).queryForAll();
			playerNames.clear();
			for (Player p : players) {
				playerNames.add(p.getNickName());
			}
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		updateRosterCheckList();
		lv_roster.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView arg0, View view, int pos,
					long arg3) {
				if (playerIdxList.contains(pos)) {
					playerIdxList.remove((Integer) pos);
				} else {
					playerIdxList.add(pos);
				}
				tv_num_selected.setText(playerIdxList.size() + " selected");
			}
		});

		Intent intent = getIntent();
		tId = intent.getLongExtra("TID", -1);
		if (tId != -1) {
			loadTeamValues();
		}
	}

	private void loadTeamValues() {
		try {
			t = tDao.queryForId(tId);
			btn_create.setText("Modify");
			tv_name.setText(t.getTeamName());
			lv_roster.setVisibility(View.GONE);
			cb_isFavorite.setChecked(t.getIsFavorite());

			// TODO: if loading a team, show players but dont allow team
			// size to change
			playerNames.clear();
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void updateRosterCheckList() {
		lv_roster
				.setAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_multiple_choice,
						playerNames));
	}

	public void doneButtonPushed(View view) {
		String team_name = tv_name.getText().toString().trim();
		if (team_name.isEmpty()) {
			Toast.makeText(this, "Team name is required.", Toast.LENGTH_LONG)
					.show();
		} else {
			Boolean is_favorite = cb_isFavorite.isChecked();
			int team_color = getResources().getColor(R.color.Aqua);

			if (tId != -1) {
				modifyTeam(team_name, team_color, true, is_favorite);
			} else {
				createTeam(team_name, team_color, is_favorite);
			}
		}
	}

	private void createTeam(String team_name, int team_color,
			boolean is_favorite) {
		Team newTeam = new Team(team_name, playerIdxList.size(), team_color,
				is_favorite);

		try {
			if (playerIdxList.size() == 1) {
				Toast.makeText(this, "Cannot create a team with one player.",
						Toast.LENGTH_SHORT).show();
			} else if (newTeam.exists(this)) {
				Toast.makeText(this, "Team already exists.", Toast.LENGTH_SHORT)
						.show();
			} else {
				try {
					tDao.create(newTeam);
					for (Integer playerIdx : playerIdxList) {
						Player p = players.get(playerIdx);
						TeamMember tm = new TeamMember(newTeam, p);
						tmDao.create(tm);
					}
					Toast.makeText(this, "Team created!", Toast.LENGTH_SHORT)
							.show();
					finish();
				} catch (SQLException ee) {
					loge("Could not create team", ee);
					Toast.makeText(this, "Could not create team.",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			loge("Could not test for existence of team", e);
		}
	}

	private void modifyTeam(String team_name, int team_color,
			boolean is_active, boolean is_favorite) {
		t.setTeamName(team_name);
		t.setColor(team_color);
		t.setIsActive(is_active);
		t.setIsFavorite(is_favorite);
		try {
			tDao.update(t);
			Toast.makeText(this, "Team modified.", Toast.LENGTH_SHORT).show();
			finish();
		} catch (SQLException e) {
			e.printStackTrace();
			loge("Could not modify team", e);
			Toast.makeText(this, "Could not modify team.", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
