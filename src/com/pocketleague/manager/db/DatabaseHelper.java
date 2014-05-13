package com.pocketleague.manager.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.pocketleague.manager.R;
import com.pocketleague.manager.db.tables.Game;
import com.pocketleague.manager.db.tables.GameMember;
import com.pocketleague.manager.db.tables.Player;
import com.pocketleague.manager.db.tables.Session;
import com.pocketleague.manager.db.tables.SessionMember;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.db.tables.TeamMember;
import com.pocketleague.manager.db.tables.Venue;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "pocketleague.db";
	private static final int DATABASE_VERSION = 1;

	private Dao<Game, Long> gameDao;
	private Dao<GameMember, Long> gameMemberDao;
	private Dao<Player, Long> playerDao;
	// private Dao<PlayerBadge, Long> playerBadgeDao;
	private Dao<Session, Long> sessionDao;
	private Dao<SessionMember, Long> sessionMemberDao;
	private Dao<Team, Long> teamDao;
	// private Dao<TeamBadge, Long> teamBadgeDao;
	private Dao<TeamMember, Long> teamMemberDao;
	private Dao<Venue, Long> venueDao;

	private List<Class> tableClasses = new ArrayList<Class>();

	private Context myContext;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION,
				R.raw.ormlite_config);
		tableClasses.add(Game.class);
		tableClasses.add(GameMember.class);
		tableClasses.add(Player.class);
		// tableClasses.add(PlayerBadge.class);
		tableClasses.add(Session.class);
		tableClasses.add(SessionMember.class);
		tableClasses.add(Team.class);
		// tableClasses.add(TeamBadge.class);
		tableClasses.add(TeamMember.class);
		tableClasses.add(Venue.class);

		myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource) {
		Log.i("DatabaseHelper.onCreate()", "Attempting to create db... ");
		try {
			createAll(connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(),
					"Unable to create database: ", e);
		}
	}

	@Override
	public void onUpgrade(final SQLiteDatabase sqliteDatabase,
			final ConnectionSource connectionSource, int oldVer,
			final int newVer) {
		Log.i("DatabaseHelper.onUpgrade()",
				"Attempting to upgrade from version " + oldVer + " to version "
						+ newVer + ".");

		switch (oldVer) {
		// case 9:
		// increment_09(sqliteDatabase, connectionSource);
		// case 10:
		// increment_10(sqliteDatabase, connectionSource);
		// break;
		default:
			try {
				dropAll(connectionSource);
				createAll(connectionSource);
			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(),
						"Unable to upgrade database from version " + oldVer
								+ " to " + newVer + ": ", e);

			}
		}
	}

	private void increment_09(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource) {
		try {
			Log.i("DatabaseHelper.increment_09",
					"Attempting to upgrade from version 09 to version 10.");
			Dao<Game, Long> gDao = getGameDao();
			Dao<Player, Long> pDao = getPlayerDao();
			Dao<Session, Long> sDao = getSessionDao();
			Dao<Venue, Long> vDao = getVenueDao();

			// DatabaseUpgrader.increment_09(connectionSource, gDao, pDao, sDao,
			// vDao, tDao);

			createAll();

		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(),
					"Unable to upgrade database from version " + 9 + " to "
							+ 10 + ": ", e);
		}
	}

	private void increment_10(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource) {
		try {
			Log.i("DatabaseHelper.increment_10",
					"Attempting to upgrade from version 10 to version 11.");
			// throw table
			Dao<Game, Long> gDao = getGameDao();
			// DatabaseUpgrader.increment_10(connectionSource, gDao, tDao);

		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(),
					"Unable to upgrade database from version " + 10 + " to "
							+ 11 + ": ", e);
		}
	}

	public void createAll() {
		try {
			createAll(getConnectionSource());
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.toString(), e.getMessage());
			throw new RuntimeException("Could not create tables: ", e);
		}
	}

	public void dropAll() {
		try {
			dropAll(getConnectionSource());
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.toString(), e.getMessage());
			throw new RuntimeException("Could not drop tables: ", e);
		}
	}

	protected void createAll(ConnectionSource connection_source)
			throws SQLException {
		for (Class c : tableClasses) {
			TableUtils.createTableIfNotExists(connection_source, c);
		}
	}

	protected void dropAll(ConnectionSource connection_source)
			throws SQLException {
		for (Class c : tableClasses) {
			TableUtils.dropTable(connection_source, c, true);
		}
	}

	public Dao<Game, Long> getGameDao() throws SQLException {
		if (gameDao == null) {
			gameDao = getDao(Game.class);
		}
		return gameDao;
	}

	public Dao<GameMember, Long> getGameMemberDao() throws SQLException {
		if (gameMemberDao == null) {
			gameMemberDao = getDao(GameMember.class);
		}
		return gameMemberDao;
	}

	public Dao<Player, Long> getPlayerDao() throws SQLException {
		if (playerDao == null) {
			playerDao = getDao(Player.class);
		}
		return playerDao;
	}

	// public Dao<PlayerBadge, Long> getPlayerBadgeDao() throws SQLException {
	// if (playerBadgeDao == null) {
	// playerBadgeDao = getDao(PlayerBadge.class);
	// }
	// return playerBadgeDao;
	// }

	public Dao<Session, Long> getSessionDao() throws SQLException {
		if (sessionDao == null) {
			sessionDao = getDao(Session.class);
		}
		return sessionDao;
	}

	public Dao<SessionMember, Long> getSessionMemberDao() throws SQLException {
		if (sessionMemberDao == null) {
			sessionMemberDao = getDao(SessionMember.class);
		}
		return sessionMemberDao;
	}

	public Dao<Team, Long> getTeamDao() throws SQLException {
		if (teamDao == null) {
			teamDao = getDao(Team.class);
		}
		return teamDao;
	}

	// public Dao<TeamBadge, Long> getTeamBadgeDao() throws SQLException {
	// if (teamBadgeDao == null) {
	// teamBadgeDao = getDao(TeamBadge.class);
	// }
	// return teamBadgeDao;
	// }

	public Dao<TeamMember, Long> getTeamMemberDao() throws SQLException {
		if (teamMemberDao == null) {
			teamMemberDao = getDao(TeamMember.class);
		}
		return teamMemberDao;
	}

	public Dao<Venue, Long> getVenueDao() throws SQLException {
		if (venueDao == null) {
			venueDao = getDao(Venue.class);
		}
		return venueDao;
	}
}
