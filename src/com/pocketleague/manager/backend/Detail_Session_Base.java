package com.pocketleague.manager.backend;

import java.sql.SQLException;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.NewSession;
import com.pocketleague.manager.Quick_Game;
import com.pocketleague.manager.R;
import com.pocketleague.manager.db.tables.Game;
import com.pocketleague.manager.db.tables.GameMember;
import com.pocketleague.manager.db.tables.Session;
import com.pocketleague.manager.db.tables.SessionMember;
import com.pocketleague.manager.db.tables.Team;

public class Detail_Session_Base extends MenuContainerActivity {
	private static final String LOGTAG = "Detail_Session";
	public Long sId;
	public Session s;
	public Dao<Session, Long> sDao;
	public Dao<SessionMember, Long> smDao;
	public Dao<GameMember, Long> gmDao;
	public MatchInfo mInfo;
	public ActionMode mActionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		sId = intent.getLongExtra("SID", -1);

		if (sId != -1) {
			try {
				sDao = Session.getDao(this);
				smDao = SessionMember.getDao(this);
				gmDao = GameMember.getDao(this);

				s = sDao.queryForId(sId);
			} catch (SQLException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		createSessionLayout();
	}

	public void createSessionLayout() {
		setContentView(R.layout.activity_detail_session);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem fav = menu.add(R.string.menu_modify);
		fav.setIcon(R.drawable.ic_action_edit);
		fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		Intent intent = new Intent(this, NewSession.class);
		intent.putExtra("SID", sId);

		fav.setIntent(intent);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshBaseDetails();
		refreshDetails();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void refreshBaseDetails() {
		TextView sName = (TextView) findViewById(R.id.sDet_name);
		TextView sId = (TextView) findViewById(R.id.sDet_id);
		TextView sessionRuleSet = (TextView) findViewById(R.id.sDet_ruleSet);
		Switch sIsActive = (Switch) findViewById(R.id.sDet_isActive);

		sName.setText(s.getSessionName());
		sId.setText(String.valueOf(s.getId()));
		sessionRuleSet.setText(s.getRuleSet().getName());
		sIsActive.setChecked(s.getIsActive());
	}

	public void refreshDetails() {

	}

	public class ActionBarCallBack implements ActionMode.Callback {
		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);

			mode.setTitle(mInfo.title);
			mode.setSubtitle(mInfo.subtitle);

			MenuItem mItm = menu.findItem(R.id.action_match);
			if (mInfo.getCreatable()) {
				mItm.setTitle("Create");
			} else if (mInfo.getViewable()) {
				mItm.setTitle("Load");
			} else {
				mItm.setVisible(false);
			}
			return true;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_match:
				if (mInfo.getCreatable()) {
					createMatch();
				} else if (mInfo.getViewable()) {
					loadMatch(mInfo.getGameId());
				}
				mode.finish();
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}
	}

	private void createMatch() {
		Intent intent = new Intent(this, Quick_Game.class);
		Dao<Game, Long> gDao = Game.getDao(this);
		Dao<GameMember, Long> gmDao = GameMember.getDao(this);
		Dao<Team, Long> tDao = Team.getDao(this);

		Game g = new Game(s, mInfo.getIdInSession(), s.getCurrentVenue(), false);
		GameMember t1 = new GameMember(g, mInfo.getTeam1());
		GameMember t2 = new GameMember(g, mInfo.getTeam2());

		try {
			gDao.setObjectCache(true);
			gDao.create(g);
			gmDao.create(t1);
			gmDao.create(t2);
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		intent.putExtra("gId", g.getId());
		startActivity(intent);

		// load a game that is in progress
		// Intent intent = new Intent(v.getContext(), GameInProgress.class);
		// intent.putExtra("GID", mInfo.gameId);
		// startActivity(intent);
		// finish();

		// load a game that is finished
		// Intent intent = new Intent(v.getContext(), Detail_Game.class);
		// intent.putExtra("GID", mInfo.gameId);
		// startActivity(intent);
	}

	private void loadMatch(long game_id) {
		Intent intent = new Intent(this, Quick_Game.class);
		intent.putExtra("gId", game_id);
		startActivity(intent);
	}
}
