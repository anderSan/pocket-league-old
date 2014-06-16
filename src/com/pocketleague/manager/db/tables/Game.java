package com.pocketleague.manager.db.tables;

import java.sql.SQLException;
import java.util.Date;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.db.DatabaseHelper;

@DatabaseTable
public class Game {
	public static final String SESSION = "session_id";
	public static final String VENUE = "venue_id";
	public static final String DATE_PLAYED = "date_played";
	public static final String IS_COMPLETE = "is_complete";
	public static final String IS_TRACKED = "is_tracked";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(uniqueCombo = true)
	private long id_in_session;

	@DatabaseField(foreign = true, uniqueCombo = true)
	private Session session;

	@DatabaseField(foreign = true)
	private Venue venue;

	@DatabaseField(canBeNull = false)
	private Date date_played;

	@DatabaseField
	private boolean is_complete = false;

	@DatabaseField
	private boolean is_tracked = true;

	@ForeignCollectionField
	ForeignCollection<GameMember> game_members;

	public Game() {
	}

	public Game(Session session, long id_in_session, Venue venue,
			Date date_played, boolean is_tracked) {
		super();
		this.session = session;
		this.id_in_session = id_in_session;
		this.venue = venue;
		this.date_played = date_played;
		this.is_tracked = is_tracked;
	}

	public Game(Session session, long id_in_session, Venue venue,
			boolean is_tracked) {
		super();
		this.session = session;
		this.id_in_session = id_in_session;
		this.venue = venue;
		this.date_played = new Date();
		this.is_tracked = is_tracked;
	}

	public static Dao<Game, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<Game, Long> d = null;
		try {
			d = helper.getGameDao();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get game dao: ", e);
		}
		return d;
	}

	public long getId() {
		return id;
	}

	// public void setId(long id) {
	// this.id = id;
	// }

	public long getIdInSession() {
		return id_in_session;
	}

	// public void setIdInSession(long id_in_session) {
	// this.id_in_session = id_in_session;
	// }

	public Session getSession() {
		return session;
	}

	// public void setSession(Session session) {
	// this.session = session;
	// }

	public Venue getVenue() {
		return venue;
	}

	// public void setVenue(Venue venue) {
	// this.venue = venue;
	// }

	public Date getDatePlayed() {
		return date_played;
	}

	// public void setDatePlayed(Date date_played) {
	// this.date_played = date_played;
	// }

	public boolean getIsComplete() {
		return is_complete;
	}

	// public void setIsComplete(boolean is_complete) {
	// this.is_complete = is_complete;
	// }

	public boolean getIsTracked() {
		return is_tracked;
	}

	// public void setIsTracked(boolean is_tracked) {
	// this.is_tracked = is_tracked;
	// }

	public ForeignCollection<GameMember> getGameMembers() {
		return game_members;
	}

	// =========================================================================
	// Additional methods
	// =========================================================================
}
