package com.pocketleague.manager.backend;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.pocketleague.manager.db.tables.Game;
import com.pocketleague.manager.db.tables.Session;
import com.pocketleague.manager.db.tables.SessionMember;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.enums.BrNodeType;

public class BracketHolder implements View.OnClickListener {
	public static String LOGTAG = "BracketHolder";
	public Context context;
	private Session s;
	private RelativeLayout rl;
	public List<SessionMember> sMembers = new ArrayList<SessionMember>();
	private Boolean isDoubleElim;
	private Bracket wBr; // winners bracket
	private Bracket lBr; // losers bracket
	private Bracket fBr; // finals bracket

	Dao<Session, Long> sDao;
	Dao<SessionMember, Long> smDao;
	Dao<Team, Long> tDao;
	Dao<Game, Long> gDao;

	public BracketHolder(ScrollView sv, Session s, Boolean isDoubleElim) {
		super();
		this.context = sv.getContext();
		this.s = s;
		this.isDoubleElim = isDoubleElim;

		try {
			sDao = Session.getDao(context);
			smDao = SessionMember.getDao(context);
			tDao = Team.getDao(context);
			gDao = Game.getDao(context);

			// get all the session members
			QueryBuilder<Session, Long> sQue = sDao.queryBuilder();
			sQue.where().eq("id", s.getId());
			QueryBuilder<SessionMember, Long> smQue = smDao.queryBuilder();
			sMembers = smQue.join(sQue).orderBy(SessionMember.TEAM_SEED, true)
					.query();
			for (SessionMember member : sMembers) {
				tDao.refresh(member.getTeam());
			}
		} catch (SQLException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		rl = new RelativeLayout(context);

		foldRoster();
		wBr = new Bracket(sMembers, rl);
		if (isDoubleElim) {
			wBr.labelText = "Winners Bracket";
		}
		wBr.buildBracket(context, this);

		if (isDoubleElim) {
			lBr = new Bracket(sMembers.size(), false, rl);
			lBr.changeOffsets(wBr.lastHeaderId, 0);
			lBr.labelText = "Losers Bracket";
			lBr.seedFromParentBracket(wBr);
			lBr.buildBracket(context, 82, wBr.lowestViewId(), 1, this);

			fBr = new Bracket(sMembers.size(), true, rl);
			fBr.changeOffsets(lBr.lastHeaderId, lBr.lastMatchId + 1);
			fBr.labelText = "Finals";
			fBr.copyBracketMaps(wBr);
			fBr.buildBracket(context, 150, lBr.lowestViewId(), 1, this);
			fBr.setFinalsRespawnText();
		}
		sv.addView(rl);
	}

	public void refreshBrackets() {
		// get all the completed games for the session
		List<Game> sGamesList = new ArrayList<Game>();
		try {
			Log.i(LOGTAG, "session id is " + s.getId());
			sGamesList = gDao.queryBuilder().orderBy(Game.ID_IN_SESSION, true)
					.where().eq(Game.SESSION, s.getId()).query();
			for (Game g : sGamesList) {
				gDao.refresh(g);
			}
		} catch (SQLException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		sGamesList = wBr.matchMatches(sGamesList);
		wBr.refreshViews();

		if (isDoubleElim) {
			lBr.respawnFromParentBracket(wBr);
			sGamesList = lBr.matchMatches(sGamesList);
			lBr.refreshViews();

			fBr.respawnFromParentBracket(wBr);
			fBr.respawnFromParentBracket(lBr);
			sGamesList = fBr.matchMatches(sGamesList);
			fBr.refreshViews();
		}
		assert sGamesList.isEmpty();
	}

	public void foldRoster() {
		// expand the list size to the next power of two
		Integer n = Bracket.factorTwos(sMembers.size());

		SessionMember dummy_sMember = new SessionMember(BrNodeType.BYE.value(),
				-1000);

		while (sMembers.size() < Math.pow(2, n)) {
			sMembers.add(dummy_sMember);
		}
		List<SessionMember> tempRoster = new ArrayList<SessionMember>();
		for (Integer i = 0; i < n - 1; i++) {
			tempRoster.clear();
			for (Integer j = 0; j < sMembers.size() / Math.pow(2, i + 1); j++) {
				tempRoster.addAll(sMembers.subList(j * (int) Math.pow(2, i),
						(j + 1) * (int) Math.pow(2, i)));
				tempRoster.addAll(sMembers.subList(sMembers.size() - (j + 1)
						* (int) Math.pow(2, i), sMembers.size() - (j)
						* (int) Math.pow(2, i)));
			}
			sMembers.clear();
			sMembers.addAll(tempRoster);
		}
	}

	public MatchInfo getMatchInfo(int viewId) {
		MatchInfo mInfo = wBr.getMatchInfo(viewId);

		if (isDoubleElim) {
			if (lBr.hasView(viewId)) {
				mInfo = lBr.getMatchInfo(viewId);
			} else if (fBr.hasView(viewId)) {
				mInfo = fBr.getMatchInfo(viewId);
			}
		}

		return mInfo;
	}

	@Override
	public void onClick(View v) {
		Log.i(LOGTAG, "View " + v.getId() + " was clicked");
	}
}
