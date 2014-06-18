package com.pocketleague.manager.backend;

import com.pocketleague.manager.db.tables.Team;

public class MatchInfo {
	private long id_in_session = -1;
	private long game_id = -1;
	private Team team1;
	private Team team2;
	public String title = "";
	public String subtitle = "";
	private boolean creatable = false;
	private boolean viewable = false;

	MatchInfo(long id_in_session) {
		this.id_in_session = id_in_session;
	}

	MatchInfo(long id_in_session, long game_id, Team team1, Team team2) {
		this.id_in_session = id_in_session;
		this.game_id = game_id;
		this.team1 = team1;
		this.team2 = team2;
	}

	public long getIdInSession() {
		return id_in_session;
	}

	public void setIdInSession(long id_in_session) {
		this.id_in_session = id_in_session;
	}

	public long getGameId() {
		return game_id;
	}

	public void setGameId(long game_id) {
		this.game_id = game_id;
		if (game_id > 0) {
			this.viewable = true;
		}
	}

	public Team getTeam1() {
		return team1;
	}

	public void setTeam1(Team team1) {
		this.team1 = team1;
		if (team1 != null && team2 != null && game_id < 1) {
			this.creatable = true;
		}
	}

	public Team getTeam2() {
		return team2;
	}

	public void setTeam2(Team team2) {
		this.team2 = team2;
		if (team1 != null && team2 != null && game_id < 1) {
			this.creatable = true;
		}
	}

	public boolean getCreatable() {
		return creatable;
	}

	public boolean getViewable() {
		return viewable;
	}

}
