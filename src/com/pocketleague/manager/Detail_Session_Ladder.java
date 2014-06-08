package com.pocketleague.manager;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.pocketleague.manager.backend.Detail_Session_Base;

public class Detail_Session_Ladder extends Detail_Session_Base {
	private static final String LOGTAG = "Detail_Session_Ladder";

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
