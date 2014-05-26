package com.pocketleague.manager.backend;

import com.pocketleague.gametypes.GameType;

public class ViewHolder_GameType {
	private GameType gametype;
	public String name;
	private int drawable_id;

	public GameType getGameType() {
		return gametype;
	}

	public void setGameType(GameType gametype) {
		this.gametype = gametype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDrawableId() {
		return drawable_id;
	}

	public void setDrawableId(int drawable_id) {
		this.drawable_id = drawable_id;
	}
}
