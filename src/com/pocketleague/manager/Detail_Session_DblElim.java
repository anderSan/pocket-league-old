package com.pocketleague.manager;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pocketleague.manager.backend.BracketHolder;
import com.pocketleague.manager.backend.Detail_Session_Base;

public class Detail_Session_DblElim extends Detail_Session_Base {
	private static final String LOGTAG = "Detail_Session_DblElim";

	public void createSessionLayout() {
		setContentView(R.layout.activity_detail_session_singleelim);
		ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		matchText = (TextView) findViewById(R.id.sDet_match);
		loadMatch = (Button) findViewById(R.id.sDet_loadMatch);

		bracketHolder = new BracketHolder(sv, s, true) {
			@Override
			public void onClick(View v) {
				mInfo = getMatchInfo(v.getId());
				log("gId: " + mInfo.gameId + ", create: " + mInfo.allowCreate
						+ ", view: " + mInfo.allowView + ", marquee: "
						+ mInfo.marquee);
				matchText.setText(mInfo.marquee);
				if (mInfo.allowCreate) {
					loadMatch.setVisibility(View.VISIBLE);
					loadMatch.setText("Create");
					loadMatch.setOnClickListener(mCreateMatchListener);
					loadMatch.setOnLongClickListener(null);
				} else if (mInfo.allowView) {
					loadMatch.setVisibility(View.VISIBLE);
					loadMatch.setText("View Match");
					loadMatch.setOnClickListener(mViewMatchListener);
					loadMatch.setOnLongClickListener(mMatchGIPListener);
				} else {
					loadMatch.setVisibility(View.GONE);
				}
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();

		// TODO: move this to bracket.java
		// try {
		// for (SessionMember sm: sMembers) {
		// smDao.update(sm);
		// }
		// }
		// catch (SQLException e) {
		// Toast.makeText(getApplicationContext(), e.getMessage(),
		// Toast.LENGTH_LONG).show();
		// }
	}

	public void refreshDetails() {
		if (bracketHolder != null) {
			bracketHolder.refreshBrackets();
		}
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
