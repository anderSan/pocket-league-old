package com.pocketleague.manager.db.tables;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.db.DatabaseHelper;

@DatabaseTable
public class SessionMember {
	public static final String SESSION = "session_id";
	public static final String TEAM = "team_id";
	public static final String TEAM_SEED = "team_seed";
	public static final String TEAM_RANK = "team_rank";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, uniqueCombo = true, foreign = true)
	private Session session;

	@DatabaseField(uniqueCombo = true, foreign = true)
	private Team team;

	@DatabaseField(canBeNull = false)
	private int team_seed;

	@DatabaseField(canBeNull = false)
	private int team_rank;

	public SessionMember() {
	}

	public SessionMember(int team_seed, int team_rank) {
		// for dummy member creation
		super();
		this.team_seed = team_seed;
		this.team_rank = team_rank;
	}

	public SessionMember(Session session, Team team, int team_seed) {
		super();
		this.session = session;
		this.team = team;
		this.team_seed = team_seed;
		this.team_rank = 0;
	}

	public SessionMember(Session session, Team team, int team_seed,
			int team_rank) {
		super();
		this.session = session;
		this.team = team;
		this.team_seed = team_seed;
		this.team_rank = team_rank;
	}

	public static Dao<SessionMember, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<SessionMember, Long> d = null;
		try {
			d = helper.getSessionMemberDao();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get session member dao: ", e);
		}
		return d;
	}

	public Session getSessionId() {
		return session;
	}

	public Team getTeam() {
		return team;
	}

	// public void setTeam(Team team) {
	// this.team = team;
	// }

	public int getSeed() {
		return team_seed;
	}

	// public void setSeed(int team_seed) {
	// this.team_seed = team_seed;
	// }

	public int getRank() {
		return team_rank;
	}

	public void setRank(int team_rank) {
		this.team_rank = team_rank;
	}

	// =========================================================================
	// Additional methods
	// =========================================================================

	// public int compareTo(SessionMember another) {
	// if (id < another.id) {
	// return -1;
	// } else if (id == another.id) {
	// return 0;
	// } else {
	// return 1;
	// }
	// }
	//
	// public boolean equals(Object o) {
	// if (!(o instanceof SessionMember))
	// return false;
	// SessionMember another = (SessionMember) o;
	// if (id == another.id) {
	// return true;
	// } else {
	// return false;
	// }
	// }
}
