package com.pocketleague.manager.enums;

/** Enum for the different session types */
public enum SessionType {
	OPEN("Open"),
	LEAGUE("League"),
	LADDER("Ladder"),
	SNGL_ELIM("Single-elimination Tournament"),
	DBL_ELIM("Double-elimination Tournament");

	private String sessiontype_label;

	private SessionType(String label) {
		sessiontype_label = label;
	}

	@Override
	public String toString() {
		return sessiontype_label;
	}
}
