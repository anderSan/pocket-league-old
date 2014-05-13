package com.pocketleague.manager.db;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.pocketleague.manager.db.tables.Game;
import com.pocketleague.manager.db.tables.GameMember;
import com.pocketleague.manager.db.tables.Player;
import com.pocketleague.manager.db.tables.Session;
import com.pocketleague.manager.db.tables.SessionMember;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.db.tables.TeamMember;
import com.pocketleague.manager.db.tables.Venue;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
	private static final Class<?>[] classes = new Class[] { Game.class,
			GameMember.class, Player.class, Session.class, SessionMember.class,
			Team.class, TeamMember.class, Venue.class };

	public static void main(String[] args) throws Exception {
		writeConfigFile("ormlite_config.txt", classes);
	}
}
