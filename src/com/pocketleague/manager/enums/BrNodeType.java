package com.pocketleague.manager.enums;

/** Enum for the nodes in a bracket */
public enum BrNodeType {
	TIP("Tip", -1),
	WIN("Win", -2),
	LOSS("Loss", -3),
	BYE("Bye", -4),
	UNSET("Unset", -5),
	RESPAWN("Respawn", -6),
	NA("N/A", -7),

	UPPER("Upper)", 1000),
	LOWER("Lower", 2000),
	U2L("Upper to Lower difference", 1000),
	L2U("Lower to Upper difference", -1000),
	MOD("Modulus", 1000);

	private String node_label;
	private int node_value;

	private BrNodeType(String label, int value) {
		node_label = label;
		node_value = value;
	}

	@Override
	public String toString() {
		return node_label;
	}

	public int value() {
		return node_value;
	}
}