package com.pocketleague.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.backend.ListAdapter_GameScore;
import com.pocketleague.manager.backend.MenuContainerActivity;
import com.pocketleague.manager.backend.ViewHolder_GameScore;
import com.pocketleague.manager.db.tables.Game;
import com.pocketleague.manager.db.tables.GameMember;
import com.pocketleague.manager.db.tables.Session;

public class Quick_Game extends MenuContainerActivity {
	Long gId;
	Game g;
	Session s;
	List<GameMember> g_members;
	Dao<Game, Long> gDao;
	Dao<GameMember, Long> gmDao;
	Dao<Session, Long> sDao;

	private ListView lv;
	private ListAdapter_GameScore scoreAdapter;
	List<ViewHolder_GameScore> game_scores = new ArrayList<ViewHolder_GameScore>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quick_game);
		lv = (ListView) findViewById(R.id.lv_gameMembers);

		Intent intent = getIntent();
		gId = intent.getLongExtra("gId", -1);

		if (gId != -1) {
			try {
				gDao = Game.getDao(this);
				gmDao = GameMember.getDao(this);
				sDao = Session.getDao(this);

				g = gDao.queryForId(gId);
				gDao.refresh(g);
				s = g.getSession();
				sDao.refresh(s);

			} catch (SQLException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		scoreAdapter = new ListAdapter_GameScore(this,
				R.layout.list_item_gamescore, game_scores, s.getRuleSet()
						.getScoreType());
		lv.setAdapter(scoreAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshMemberListing();
	}

	private void refreshMemberListing() {
		ViewHolder_GameScore gs;
		for (GameMember gm : g.getGameMembers()) {
			try {
				gmDao.refresh(gm);
			} catch (SQLException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
			gs = new ViewHolder_GameScore(gm.getTeam().getTeamName());
			game_scores.add(gs);
		}
		scoreAdapter.notifyDataSetChanged();
	}

	public void doneButtonPushed(View v) {

	}
}
