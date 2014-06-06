package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.pocketleague.manager.db.tables.Player;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.db.tables.TeamMember;

public class DatabaseCommonQueue extends OrmLiteBaseActivity<DatabaseHelper> {
	public static String LOGTAG = "DatabaseCommonQueue";

	public static Team findPlayerSoloTeam(Context context, Player p)
			throws SQLException {
		Dao<Team, Long> tDao = Team.getDao(context);
		Dao<TeamMember, Long> tmDao = TeamMember.getDao(context);

		QueryBuilder<TeamMember, Long> tmQb = tmDao.queryBuilder();
		tmQb.where().eq(TeamMember.PLAYER, p);
		QueryBuilder<Team, Long> tQb = tDao.queryBuilder();
		tQb.where().eq(Team.TEAM_SIZE, 1);

		List<Team> results = tQb.join(tmQb).query();
		if (results.size() > 1) {
			log("Warning: Multiple teams found for player " + p.getNickName());
			Toast.makeText(context,
					"Warning: Multiple teams found for player!",
					Toast.LENGTH_SHORT).show();
		}

		return results.get(0);
	}

	public static void log(String msg) {
		Log.i(LOGTAG, msg);
	}

	public static void logd(String msg) {
		Log.d(LOGTAG, msg);
	}

	public static void loge(String msg, Exception e) {
		Log.e(LOGTAG, msg + ": " + e.getMessage());
	}
}
