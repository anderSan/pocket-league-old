package com.pocketleague.manager.db.tables;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.db.DatabaseHelper;

@DatabaseTable
public class GameMember implements Comparable<GameMember> {
	public static final String GAME = "game_id";
	public static final String TEAM = "team_id";
	public static final String SCORE = "score";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, uniqueCombo = true, foreign = true)
	private Game game;

	@DatabaseField(canBeNull = false, uniqueCombo = true, foreign = true)
	private Team team;

	@DatabaseField(canBeNull = false)
	private int score;

	public GameMember() {
	}

	public GameMember(Game game, Team team) {
		super();
		this.game = game;
		this.team = team;
	}

	public static Dao<GameMember, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<GameMember, Long> d = null;
		try {
			d = helper.getGameMemberDao();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get game member dao: ", e);
		}
		return d;
	}

	public long getId() {
		return id;
	}

	public Game getGame() {
		return game;
	}

	public Team getTeam() {
		return team;
	}

	// =========================================================================
	// Additional methods
	// =========================================================================

	public int compareTo(GameMember another) {
		if (id < another.id) {
			return -1;
		} else if (id == another.id) {
			return 0;
		} else {
			return 1;
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof GameMember))
			return false;
		GameMember another = (GameMember) o;
		if (id == another.id) {
			return true;
		} else {
			return false;
		}
	}
}
