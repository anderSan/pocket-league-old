package com.pocketleague.manager;

import java.sql.SQLException;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.pocketleague.manager.backend.BracketHolder;
import com.pocketleague.manager.backend.Detail_Session_Base;
import com.pocketleague.manager.db.tables.SessionMember;
import com.pocketleague.manager.enums.SessionType;

public class Detail_Session_Elimination extends Detail_Session_Base {
	private static final String LOGTAG = "Detail_Session_DblElim";
	private BracketHolder bracketHolder = null;

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
				log("gId: " + mInfo.getIdInSession() + ", create: "
						+ mInfo.getCreatable() + ", view: "
						+ mInfo.getViewable() + ", marquee: " + mInfo.title
						+ ", " + mInfo.subtitle);
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
}
