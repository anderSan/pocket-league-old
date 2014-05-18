package com.pocketleague.manager.backend;

import android.app.Application;

import com.pocketleague.manager.enums.GameType;

public class ManagerApplication extends Application {

	private GameType current_gametype;

	public GameType getCurrentGameType() {
		return current_gametype;
	}

	public void setCurrentGameType(GameType gametype) {
		this.current_gametype = gametype;
	}
}
