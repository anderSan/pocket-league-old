package com.pocketleague.manager.db.tables;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.db.DatabaseHelper;

@DatabaseTable
public class TeamMember {
	public static final String TEAM = "team_id";
	public static final String PLAYER = "player_id";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, uniqueCombo = true, foreign = true)
	private Team team;

	@DatabaseField(uniqueCombo = true, foreign = true)
	private Player player;

	public TeamMember() {
	}

	public TeamMember(Team team, Player player) {
		super();
		this.team = team;
		this.player = player;
	}

	public static Dao<TeamMember, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<TeamMember, Long> d = null;
		try {
			d = helper.getTeamMemberDao();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get team member dao: ", e);
		}
		return d;
	}

	public Team getTeam() {
		return team;
	}

	public Player getPlayer() {
		return player;
	}
}
