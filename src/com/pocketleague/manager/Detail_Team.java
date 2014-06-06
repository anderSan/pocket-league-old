package com.pocketleague.manager;

import java.sql.SQLException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.backend.MenuContainerActivity;
import com.pocketleague.manager.db.tables.Player;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.db.tables.TeamMember;

public class Detail_Team extends MenuContainerActivity {
	private static final String LOGTAG = "Detail_Team";
	Long tId;
	Team t;
	Dao<Team, Long> tDao;
	Dao<TeamMember, Long> tmDao;
	Dao<Player, Long> pDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_team);

		Intent intent = getIntent();
		tId = intent.getLongExtra("TID", -1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem fav = menu.add(R.string.menu_modify);
		fav.setIcon(R.drawable.ic_action_edit);
		fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		Intent intent = new Intent(this, NewTeam.class);
		intent.putExtra("TID", tId);

		fav.setIntent(intent);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		refreshDetails();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshDetails();
	}

	public void refreshDetails() {
		String memberNicks = "";
		if (tId != -1) {
			try {
				tDao = Team.getDao(this);
				t = tDao.queryForId(tId);

				pDao = Player.getDao(this);
				tmDao = TeamMember.getDao(this);
				List<TeamMember> memberList = tmDao.queryBuilder().where()
						.eq(TeamMember.TEAM, tId).query();

				for (TeamMember tm : memberList) {
					pDao.refresh(tm.getPlayer());
					memberNicks = memberNicks.concat(tm.getPlayer()
							.getNickName() + ", ");
				}
				if (memberNicks.length() == 0) {
					memberNicks = "Anonymous team (no members).";
				} else {
					memberNicks = memberNicks.substring(0,
							memberNicks.length() - 2) + ".";
				}
			} catch (SQLException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		TextView tName = (TextView) findViewById(R.id.tDet_name);
		tName.setText(t.getTeamName());

		TextView teamId = (TextView) findViewById(R.id.tDet_id);
		teamId.setText(String.valueOf(t.getId()));

		TextView tv_members = (TextView) findViewById(R.id.tDet_members);
		tv_members.setText(memberNicks);

		TextView tWinRatio = (TextView) findViewById(R.id.tDet_winRatio);
		// tWinRatio.setText(String.valueOf(t.getnWins()) + "/" +
		// String.valueOf(t.getnLosses()));

		TextView tIsActive = (TextView) findViewById(R.id.teamDet_isActive);
		if (t.getIsActive()) {
			tIsActive.setText("This team is active");
		} else {
			tIsActive.setText("This team is retired");
		}
	}
}
