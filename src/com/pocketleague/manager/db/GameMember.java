package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class GameMember implements Comparable<GameMember> {
	public static final String GAME = "game_id";
	public static final String PLAYER = "player_id";
	public static final String FACTION = "faction_id";
	public static final String END_RANK = "end_ranking";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, uniqueCombo = true, foreign = true)
	private Game game;

	@DatabaseField(canBeNull = false, uniqueCombo = true, foreign = true)
	private Player player;

	@DatabaseField()
	private int faction_id;

	@DatabaseField(canBeNull = false)
	private int end_ranking;

	public GameMember() {
	}

	public GameMember(Game game, Player player, int faction_id) {
		super();
		this.game = game;
		this.player = player;
		this.faction_id = faction_id;
	}

	public static Dao<GameMember, Long> getDao(Context context)
			throws SQLException {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<GameMember, Long> d = helper.getGameMemberDao();
		return d;
	}

	public static List<GameMember> getAll(Context context) throws SQLException {
		Dao<GameMember, Long> d = GameMember.getDao(context);
		List<GameMember> gameMembers = new ArrayList<GameMember>();
		for (GameMember s : d) {
			gameMembers.add(s);
		}
		return gameMembers;
	}

	public long getId() {
		return id;
	}

	public Game getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	public int get_faction() {
		return faction_id;
	}

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
