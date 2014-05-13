package com.pocketleague.manager.db.tables;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.db.DatabaseHelper;

@DatabaseTable
public class Team {
	public static final String NAME = "name";
	public static final String TEAM_SIZE = "team_size";
	public static final String COLOR = "color";
	public static final String IS_ACTIVE = "is_active";
	public static final String IS_FAVORITE = "is_favorite";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false)
	private String name;

	@DatabaseField
	private int team_size;

	@DatabaseField
	private int color;

	@DatabaseField
	private boolean is_active = true;

	@DatabaseField
	private boolean is_favorite = false;

	@ForeignCollectionField
	ForeignCollection<TeamMember> team_members;

	Team() {
	}

	public Team(String team_name, int team_size, int color) {
		super();
		this.name = team_name;
		this.team_size = team_size;
		this.color = color;
	}

	public static Dao<Team, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<Team, Long> d = null;
		try {
			d = helper.getTeamDao();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get team dao: ", e);
		}
		return d;
	}

	public long getId() {
		return id;
	}

	public String getTeamName() {
		return name;
	}

	public void setTeamName(String team_name) {
		this.name = team_name;
	}

	public int getTeamSize() {
		return team_size;
	}

	public void setTeamSize(int team_size) {
		this.team_size = team_size;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean getIsActive() {
		return is_active;
	}

	public void setIsActive(boolean is_active) {
		this.is_active = is_active;
	}

	public boolean getIsFavorite() {
		return is_favorite;
	}

	public void setIsFavorite(boolean is_favorite) {
		this.is_favorite = is_favorite;
	}
}
