package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class TeamBadge {

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(foreign = true)
	private Team team;

	@DatabaseField(foreign = true)
	private Session session;

	@DatabaseField(canBeNull = false)
	private int badgeType;

	TeamBadge() {
	}

	public TeamBadge(Team team, Session session, int badgeType) {
		super();
		this.team = team;
		this.session = session;
		this.badgeType = badgeType;
	}

	public TeamBadge(Team team, int badgeType) {
		super();
		this.team = team;
		this.badgeType = badgeType;
	}

	public static Dao<TeamBadge, Long> getDao(Context context)
			throws SQLException {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<TeamBadge, Long> d = helper.getTeamBadgeDao();
		return d;
	}

	public static List<TeamBadge> getAll(Context context) throws SQLException {
		Dao<TeamBadge, Long> d = TeamBadge.getDao(context);
		List<TeamBadge> badges = new ArrayList<TeamBadge>();
		for (TeamBadge b : d) {
			badges.add(b);
		}
		return badges;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
