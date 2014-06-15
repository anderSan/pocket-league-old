package com.pocketleague.manager.backend;

import java.sql.SQLException;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.Detail_Game;
import com.pocketleague.manager.NewGame;
import com.pocketleague.manager.NewSession;
import com.pocketleague.manager.R;
import com.pocketleague.manager.backend.Bracket.MatchInfo;
import com.pocketleague.manager.db.tables.Session;
import com.pocketleague.manager.db.tables.SessionMember;

public class Detail_Session_Base extends MenuContainerActivity {
	private static final String LOGTAG = "Detail_Session";
	public Long sId;
	public Session s;
	public Dao<Session, Long> sDao;
	public Dao<SessionMember, Long> smDao;
	public BracketHolder bracketHolder = null;
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

		// TODO: move this to bracket.java
		// try {
		// for (SessionMember sm : sMembers) {
		// smDao.update(sm);
		// }
		// } catch (SQLException e) {
		// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		// }
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

	private OnClickListener mViewMatchListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(v.getContext(), Detail_Game.class);
			intent.putExtra("GID", mInfo.gameId);
			startActivity(intent);
		}
	};

	private OnLongClickListener mMatchGIPListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			// Intent intent = new Intent(v.getContext(), GameInProgress.class);
			// intent.putExtra("GID", mInfo.gameId);
			// startActivity(intent);
			// finish();
			return true;
		}
	};

	private OnClickListener mCreateMatchListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(v.getContext(), NewGame.class);
			intent.putExtra("p1", mInfo.p1Id);
			intent.putExtra("p2", mInfo.p2Id);
			intent.putExtra("sId", sId);
			startActivity(intent);
		}
	};
}
