package com.ultimatepolish.scorebookdb.enums;

import java.util.HashMap;
import java.util.Map;

import com.ultimatepolish.scorebookdb.rulesets.RuleSet;
import com.ultimatepolish.scorebookdb.rulesets.RuleSet00;
import com.ultimatepolish.scorebookdb.rulesets.RuleSet01;

public final class RuleType {
	public static final RuleSet RS00 = new RuleSet00();
	public static final RuleSet RS01 = new RuleSet01();

	public static final Map<Integer, RuleSet> map = new HashMap<Integer, RuleSet>() {
		{
			put(0, new RuleSet00());
			put(1, new RuleSet01());
		}
	};
}
