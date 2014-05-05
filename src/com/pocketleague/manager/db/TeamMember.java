package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class TeamMember implements Comparable<TeamMember> {
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

	public static Dao<TeamMember, Long> getDao(Context context)
			throws SQLException {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<TeamMember, Long> d = helper.getTeamMemberDao();
		return d;
	}

	public static List<TeamMember> getAll(Context context) throws SQLException {
		Dao<TeamMember, Long> d = TeamMember.getDao(context);
		List<TeamMember> teamMembers = new ArrayList<TeamMember>();
		for (TeamMember s : d) {
			teamMembers.add(s);
		}
		return teamMembers;
	}

	public long getId() {
		return id;
	}

	public Team getTeamId() {
		return team;
	}

	public Player getPlayer() {
		return player;
	}

	public int compareTo(TeamMember another) {
		if (id < another.id) {
			return -1;
		} else if (id == another.id) {
			return 0;
		} else {
			return 1;
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof TeamMember))
			return false;
		TeamMember another = (TeamMember) o;
		if (id == another.id) {
			return true;
		} else {
			return false;
		}
	}
}
