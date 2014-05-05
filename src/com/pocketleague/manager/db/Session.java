package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.enums.GameType;
import com.pocketleague.manager.enums.SessionType;

@DatabaseTable
public class Session {
	public static final String IS_ACTIVE = "is_active";
	public static final String IS_FAVORITE = "is_favorite";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false)
	private String name;

	@DatabaseField(canBeNull = false)
	public GameType game_type;

	@DatabaseField(canBeNull = false)
	public SessionType session_type;

	@DatabaseField
	private int team_size = 1;

	@DatabaseField
	private int ruleset_id;

	@DatabaseField
	private boolean is_active = true;

	@DatabaseField
	private boolean is_favorite = false;

	@ForeignCollectionField
	ForeignCollection<Game> games;

	public Session() {
	}

	public Session(String session_name, SessionType session_type,
			int ruleset_id, Date startDate, boolean isTeam) {
		super();
		this.name = session_name;
		this.session_type = session_type;
		this.ruleset_id = ruleset_id;
	}

	public static Dao<Session, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<Session, Long> d = null;
		try {
			d = helper.getSessionDao();
		} catch (SQLException e) {
			throw new RuntimeException("Couldn't get session dao: ", e);
		}
		return d;
	}

	public static List<Session> getAll(Context context) throws SQLException {
		Dao<Session, Long> d = Session.getDao(context);
		List<Session> sessions = new ArrayList<Session>();
		for (Session s : d) {
			sessions.add(s);
		}
		return sessions;
	}

	public long getId() {
		return id;
	}

	public String getSessionName() {
		return name;
	}

	public void setSessionName(String sessionName) {
		this.name = sessionName;
	}

	public SessionType getSessionType() {
		return session_type;
	}

	public void setSessionType(SessionType session_type) {
		this.session_type = session_type;
	}

	public int getRuleSetId() {
		return ruleset_id;
	}

	public boolean getIsActive() {
		return is_active;
	}

	public void setIsActive(boolean is_active) {
		this.is_active = is_active;
	}

	public ForeignCollection<Game> getGames() {
		return games;
	}
}
