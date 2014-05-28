package com.pocketleague.manager.backend;

public class ViewHolder_Player {
	public String id;
	public String name;
	public String nickName;
	public int playerColor;

	public String getId() {
		return id;
	}

	public void setId(String playerId) {
		this.id = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String playerName) {
		this.name = playerName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String playerNickName) {
		this.nickName = playerNickName;
	}

	public Integer getColor() {
		return playerColor;
	}

	public void setColor(Integer playerColor) {
		this.playerColor = playerColor;
	}
}
