package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class SessionMember implements Comparable<SessionMember> {
	public static final String SESSION = "session_id";
	public static final String PLAYER = "player_id";
	public static final String PLAYER_SEED = "player_seed";
	public static final String PLAYER_RANK = "player_rank";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, uniqueCombo = true, foreign = true)
	private Session session;

	@DatabaseField(uniqueCombo = true, foreign = true)
	private Player player;

	@DatabaseField()
	private int faction;

	@DatabaseField(canBeNull = false)
	private int player_seed;

	@DatabaseField(canBeNull = false)
	public int player_rank;

	public SessionMember() {
	}

	public SessionMember(int player_seed, int player_rank) {
		// for dummy member creation
		super();
		this.player_seed = player_seed;
		this.player_rank = player_rank;
	}

	public SessionMember(Session session, Player player, int player_seed) {
		super();
		this.session = session;
		this.player = player;
		this.player_seed = player_seed;
		this.player_rank = 0;
	}

	public SessionMember(Session session, Player player, int player_seed,
			int player_rank) {
		super();
		this.session = session;
		this.player = player;
		this.player_seed = player_seed;
		this.player_rank = player_rank;
	}

	public static Dao<SessionMember, Long> getDao(Context context)
			throws SQLException {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<SessionMember, Long> d = helper.getSessionMemberDao();
		return d;
	}

	public static List<SessionMember> getAll(Context context)
			throws SQLException {
		Dao<SessionMember, Long> d = SessionMember.getDao(context);
		List<SessionMember> sessionMembers = new ArrayList<SessionMember>();
		for (SessionMember s : d) {
			sessionMembers.add(s);
		}
		return sessionMembers;
	}

	public long getId() {
		return id;
	}

	public Session getSessionId() {
		return session;
	}

	public Player getPlayer() {
		return player;
	}

	// public void setPlayer(Player player) {
	// this.player = player;
	// }

	public int getSeed() {
		return player_seed;
	}

	public int compareTo(SessionMember another) {
		if (id < another.id) {
			return -1;
		} else if (id == another.id) {
			return 0;
		} else {
			return 1;
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof SessionMember))
			return false;
		SessionMember another = (SessionMember) o;
		if (id == another.id) {
			return true;
		} else {
			return false;
		}
	}
}
