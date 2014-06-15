package com.pocketleague.manager.db.tables;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.gametypes.GameRule;
import com.pocketleague.gametypes.GameType;
import com.pocketleague.gametypes.RuleSet;
import com.pocketleague.manager.db.DatabaseHelper;
import com.pocketleague.manager.enums.SessionType;

@DatabaseTable
public class Session {
	public static final String NAME = "name";
	public static final String GAME_TYPE = "game_type";
	public static final String GAME_RULES = "game_rules";
	public static final String SESSION_TYPE = "session_type";
	public static final String TEAM_SIZE = "team_size";
	public static final String IS_ACTIVE = "is_active";
	public static final String IS_FAVORITE = "is_favorite";
	public static final String CURRENT_VENUE = "current_venue";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, unique = true)
	private String name;

	@DatabaseField(canBeNull = false)
	private GameType game_type;

	@DatabaseField
	private GameRule game_rule;

	@DatabaseField(canBeNull = false)
	private SessionType session_type;

	@DatabaseField
	private int team_size = 1;

	@DatabaseField
	private boolean is_active = true;

	@DatabaseField
	private boolean is_favorite = false;

	@DatabaseField(foreign = true)
	private Venue current_venue;

	@ForeignCollectionField
	ForeignCollection<Game> games;

	@ForeignCollectionField
	ForeignCollection<SessionMember> session_members;

	public Session() {
	}

	public Session(String session_name, GameType game_type, GameRule game_rule,
			SessionType session_type, int team_size, Venue current_venue) {
		super();
		this.name = session_name;
		this.game_type = game_type;
		this.game_rule = game_rule;
		this.session_type = session_type;
		this.team_size = team_size;
		this.current_venue = current_venue;
	}

	public static Dao<Session, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<Session, Long> d = null;
		try {
			d = helper.getSessionDao();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get session dao: ", e);
		}
		return d;
	}

	public long getId() {
		return id;
	}

	// public void setId(long id) {
	// this.id = id;
	// }

	public String getSessionName() {
		return name;
	}

	public void setSessionName(String session_name) {
		this.name = session_name;
	}

	public GameType getGameType() {
		return game_type;
	}

	public void setGameType(GameType game_type) {
		this.game_type = game_type;
	}

	public GameRule getGameRule() {
		return game_rule;
	}

	public void setGameRules(GameRule game_rule) {
		this.game_rule = game_rule;
	}

	public SessionType getSessionType() {
		return session_type;
	}

	public void setSessionType(SessionType session_type) {
		this.session_type = session_type;
	}

	public int getTeamSize() {
		return team_size;
	}

	public void setTeamSize(int team_size) {
		this.team_size = team_size;
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

	public Venue getCurrentVenue() {
		return current_venue;
	}

	public void setCurrentVenue(Venue current_venue) {
		this.current_venue = current_venue;
	}

	public ForeignCollection<Game> getGames() {
		return games;
	}

	public ForeignCollection<SessionMember> getSessionMembers() {
		return session_members;
	}

	// =========================================================================
	// Additional methods
	// =========================================================================

	public RuleSet getRuleSet() {
		return game_rule.toRuleSet();
	}
}
