package com.pocketleague.manager.backend;

import java.util.ArrayList;
import java.util.List;

public class ViewHolderHeader_Player {
	private String name;
	private List<ViewHolder_Player> playerList = new ArrayList<ViewHolder_Player>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ViewHolder_Player> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<ViewHolder_Player> playerList) {
		this.playerList = playerList;
	}
}
