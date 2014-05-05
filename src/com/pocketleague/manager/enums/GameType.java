package com.pocketleague.manager.enums;

public enum GameType {
	BILLIARDS("Billiards"),
	POLISH_HORSESHOES("Polish Horseshoes"),
	UNDEFINED("Undefined");

	private String gametype_label;

	private GameType(String label) {
		gametype_label = label;
	}

	@Override
	public String toString() {
		return gametype_label;
	}
}