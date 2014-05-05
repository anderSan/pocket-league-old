package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.enums.GameType;

@DatabaseTable
public class Game {
	public static final String RULESET = "ruleset_id";
	public static final String SESSION = "session_id";
	public static final String VENUE = "venue_id";
	public static final String DATE_PLAYED = "date_played";
	public static final String IS_COMPLETE = "is_complete";
	public static final String IS_TRACKED = "is_tracked";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false)
	private GameType gametype;

	@DatabaseField(canBeNull = false)
	public int ruleset_id;

	@DatabaseField(foreign = true)
	private Session session;

	@DatabaseField(foreign = true)
	private Venue venue;

	@DatabaseField(canBeNull = false)
	private Date date_played;

	@DatabaseField
	private boolean is_complete = false;

	@DatabaseField
	private boolean is_tracked = true;

	public Game() {
		super();
	}

	public Game(Session session, Venue venue, int ruleset, boolean is_tracked,
			Date date_played) {
		super();
		this.session = session;
		this.venue = venue;
		this.ruleset_id = ruleset;
		this.is_tracked = is_tracked;
		this.date_played = date_played;

	}

	public Game(Session session, Venue venue, int ruleset, boolean is_tracked) {
		super();
		this.session = session;
		this.venue = venue;
		this.ruleset_id = ruleset;
		this.is_tracked = is_tracked;
		this.date_played = new Date();
	}

	public static Dao<Game, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<Game, Long> d = null;
		try {
			d = helper.getGameDao();
		} catch (SQLException e) {
			throw new RuntimeException("Couldn't get game dao: ", e);
		}
		return d;
	}

	public static List<Game> getAll(Context context) throws SQLException {
		Dao<Game, Long> d = Game.getDao(context);
		List<Game> games = new ArrayList<Game>();
		for (Game g : d) {
			games.add(g);
		}
		return games;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public Date getDatePlayed() {
		return date_played;
	}

	public void setDatePlayed(Date date_played) {
		this.date_played = date_played;
	}

	public boolean getIsComplete() {
		return is_complete;
	}

	public void setIsComplete(boolean isComplete) {
		this.is_complete = isComplete;
	}

	public boolean getIsTracked() {
		return is_tracked;
	}

	// public Player getWinner() {
	// // TODO: should raise an error if game is not complete
	// Player winner = firstPlayer;
	// if (getSecondPlayerScore() > getFirstPlayerScore()) {
	// winner = secondPlayer;
	// }
	// return winner;
	// }

	// public Player getLoser() {
	// Player loser = firstPlayer;
	// if (getSecondPlayerScore() < getFirstPlayerScore()) {
	// loser = secondPlayer;
	// }
	// return loser;
	// }
}
