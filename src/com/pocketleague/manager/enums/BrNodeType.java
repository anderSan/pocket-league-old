package com.pocketleague.manager.enums;

/** Enum for the nodes in a bracket */
public enum BrNodeType {
	TIP("Tip", -1),
	WIN("Win", -2),
	LOSS("Loss", -3),
	BYE("Bye", -4),
	UNSET("Unset", -5),
	RESPAWN("Respawn", -6),
	NA("N/A", -7);

	private String node_label;
	private int node_value;

	public static final int UPPER = 1000;
	public static final int LOWER = 2000;
	public static final int U2L = 1000;
	public static final int L2U = -1000;
	public static final int MOD = 1000;

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