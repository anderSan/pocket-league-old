package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Team {
	public static final String TEAM_NAME = "name";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false)
	private String name;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	byte[] image_bytes;

	@DatabaseField
	private boolean is_active = true;

	@DatabaseField
	private boolean is_favorite = false;

	Team() {
	}

	public Team(String team_name) {
		super();
		this.name = team_name;
	}

	// public static Dao<Team, Long> getDao(Context context) throws
	// SQLException{
	// DatabaseHelper helper = new DatabaseHelper(context);
	// Dao<Team, Long> d = helper.getTeamDao();
	// return d;
	// }

	public static boolean exists(String teamName, Context context)
			throws SQLException {
		if (teamName == null) {
			return false;
		}
		List<Team> teamList = null;
		// HashMap<String,Object> m = buildNameMap(teamName);
		//
		// teamList = getDao(context).queryForFieldValuesArgs(m);
		// if (teamList.isEmpty()){
		return false;
		// }
		// else{
		// return true;
		// }
	}

	public boolean exists(Context context) throws SQLException {
		return exists(name, context);
	}

	// public static List<Team> getAll(Context context) throws SQLException{
	// Dao<Team, Long> d = Team.getDao(context);
	// List<Team> teams = new ArrayList<Team>();
	// for(Team t:d){
	// teams.add(t);
	// }
	// return teams;
	// }

	public long getId() {
		return id;
	}

	public String getTeamName() {
		return name;
	}

	public void setTeamName(String teamName) {
		this.name = teamName;
	}

	public byte[] getImageBytes() {
		return image_bytes;
	}

	public void setImageBytes(byte[] image_bytes) {
		this.image_bytes = image_bytes;
	}

	public boolean getIsActive() {
		return is_active;
	}

	public void setIsActive(boolean is_active) {
		this.is_active = is_active;
	}
}
