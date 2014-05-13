package com.pocketleague.manager.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class PlayerBadge {

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(foreign = true)
	private Player player;

	@DatabaseField(foreign = true)
	private Session session;

	@DatabaseField(canBeNull = false)
	private int badgeType;

	PlayerBadge() {
	}

	public PlayerBadge(Player player, Session session, int badgeType) {
		super();
		this.player = player;
		this.session = session;
		this.badgeType = badgeType;
	}

	public PlayerBadge(Player player, int badgeType) {
		super();
		this.player = player;
		this.badgeType = badgeType;
	}

	// public static Dao<PlayerBadge, Long> getDao(Context context)
	// throws SQLException {
	// DatabaseHelper helper = new DatabaseHelper(context);
	// Dao<PlayerBadge, Long> d = helper.getPlayerBadgeDao();
	// return d;
	// }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
