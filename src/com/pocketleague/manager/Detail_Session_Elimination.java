package com.pocketleague.manager;

import java.sql.SQLException;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.pocketleague.manager.backend.BracketHolder;
import com.pocketleague.manager.backend.Detail_Session_Base;
import com.pocketleague.manager.db.tables.SessionMember;
import com.pocketleague.manager.enums.SessionType;

public class Detail_Session_Elimination extends Detail_Session_Base {
	private static final String LOGTAG = "Detail_Session_DblElim";

	public void createSessionLayout() {
		setContentView(R.layout.activity_detail_session_singleelim);
		ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		boolean isDblElim = s.getSessionType() == SessionType.DBL_ELIM;
		bracketHolder = new BracketHolder(sv, s, isDblElim) {
			@Override
			public void onClick(View v) {
				mInfo = getMatchInfo(v.getId());
				log("gId: " + mInfo.gameId + ", create: " + mInfo.allowCreate
						+ ", view: " + mInfo.allowView + ", marquee: "
						+ mInfo.title);
				mActionMode = Detail_Session_Elimination.this
						.startActionMode(new ActionBarCallBack());
				v.setSelected(true);
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();

		// TODO: move this to bracket.java
		try {
			for (SessionMember sm : bracketHolder.sMembers) {
				smDao.update(sm);
			}
		} catch (SQLException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	public void refreshDetails() {
		if (bracketHolder != null) {
			bracketHolder.refreshBrackets();
		}
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
			if (mInfo.allowCreate) {
				mItm.setTitle("Create");
			} else if (mInfo.allowView) {
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
				matchAction();
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

	private void matchAction() {
		Intent intent = new Intent(this, NewGame.class);
		intent.putExtra("p1", mInfo.p1Id);
		intent.putExtra("p2", mInfo.p2Id);
		intent.putExtra("sId", sId);
		intent.putExtra("vId", s.getCurrentVenue().getId());
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
}
