package com.pocketleague.manager.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pocketleague.manager.db.tables.Game;
import com.pocketleague.manager.db.tables.SessionMember;
import com.pocketleague.manager.enums.BrDrawable;
import com.pocketleague.manager.enums.BrNodeType;

public class Bracket {
	public static String LOGTAG = "Bracket";

	public boolean isDoubleElim;
	public String labelText = "";
	private int headerIdOffset = 0;
	private int matchIdOffset = 0;
	public int lastHeaderId;
	public int lastMatchId;
	private int nLeafs;
	private Map<Long, Integer> smIdMap = new HashMap<Long, Integer>();
	private Map<Integer, SessionMember> smSeedMap = new HashMap<Integer, SessionMember>();
	private List<Integer> matchIds = new ArrayList<Integer>();
	private List<Integer> sm1Idcs = new ArrayList<Integer>();
	private List<BrNodeType> sm1Types = new ArrayList<BrNodeType>();
	private List<Integer> sm2Idcs = new ArrayList<Integer>();
	private List<BrNodeType> sm2Types = new ArrayList<BrNodeType>();
	private List<Long> gameIds = new ArrayList<Long>();
	private RelativeLayout rl;

	public Bracket(List<SessionMember> sMembers, RelativeLayout rl) {
		// fast check that nLeafs is a power of two
		this.nLeafs = sMembers.size();
		this.rl = rl;
		this.lastHeaderId = 2 + getTier(nLeafs - 1) + headerIdOffset;
		this.lastMatchId = nLeafs - 1 + matchIdOffset;
		assert (nLeafs & (nLeafs - 1)) == 0;
		seed(sMembers);

		int seed;
		for (SessionMember sm : sMembers) {
			seed = sm.getSeed();
			if (seed >= 0 && !smSeedMap.containsKey(seed)) {
				smIdMap.put(sm.getTeam().getId(), seed);
				smSeedMap.put(seed, sm);
			}
		}
	}

	/**
	 * Use this constructor for a respawn bracket. ie, members will come from
	 * another bracket after losing or reaching the top tier. The insane loops
	 * generate a tiered losers bracket.
	 */
	public Bracket(int nLeafs, boolean isFinal, RelativeLayout rl) {
		// fast check that nLeafs is a power of two
		if (isFinal) {
			this.nLeafs = 4;
		} else {
			this.nLeafs = (int) Math.pow(2, 2 * factorTwos(nLeafs) - 1);
		}
		this.rl = rl;
		this.lastHeaderId = 2 + getTier(this.nLeafs - 1) + headerIdOffset;
		this.lastMatchId = this.nLeafs - 1 + matchIdOffset;
		assert (nLeafs & (nLeafs - 1)) == 0;

		if (isFinal) {
			seed(nLeafs);
		} else {
			seed();
		}
	}

	private void removeMatch(int pos) {
		matchIds.remove(pos);
		sm1Idcs.remove(pos);
		sm1Types.remove(pos);
		sm2Idcs.remove(pos);
		sm2Types.remove(pos);
		gameIds.remove(pos);
	}

	public void changeOffsets(int headerIdOffset, int matchIdOffset) {
		assert matchIdOffset - this.matchIdOffset >= 0;
		this.headerIdOffset = headerIdOffset;
		this.matchIdOffset = matchIdOffset;
		this.lastHeaderId = 2 + getTier(nLeafs - 1) + headerIdOffset;
		this.lastMatchId = nLeafs - 1 + matchIdOffset;
	}

	public void buildBracket(Context context, OnClickListener mListener) {
		buildBracket(context, 150, -1, -1, mListener);
	}

	public void buildBracket(Context context, int tierWidth, int aboveViewId,
			int columnViewId, OnClickListener mListener) {
		TextView tv;

		// lay out the bracket
		makeInvisibleHeaders(300, tierWidth, aboveViewId, columnViewId);

		for (Integer mPos = 0; mPos < length(); mPos++) {
			// upper half of match
			tv = makeHalfMatchView(context, mPos, true);
			tv.setOnClickListener(mListener);
			if (sm1Types.get(mPos) == BrNodeType.TIP
					|| sm1Types.get(mPos) == BrNodeType.RESPAWN) {
				addViewToLayout(tv, true);
			} else {
				addViewToLayout(tv, false);
			}

			// lower half of match
			if (sm2Types.get(mPos) != BrNodeType.NA) {
				tv = makeHalfMatchView(context, mPos, false);
				tv.setOnClickListener(mListener);
				if (sm2Types.get(mPos) == BrNodeType.TIP
						|| sm2Types.get(mPos) == BrNodeType.RESPAWN) {
					addViewToLayout(tv, true);
				} else {
					addViewToLayout(tv, false);
				}
			}
		}
	}

	public void setFinalsRespawnText() {
		TextView tv;
		tv = (TextView) rl.findViewById(matchIdOffset + BrNodeType.UPPER);
		tv.setText("(W)");

		tv = (TextView) rl.findViewById(matchIdOffset + BrNodeType.LOWER);
		tv.setText("(L)");

		tv = (TextView) rl.findViewById(2 + matchIdOffset + BrNodeType.LOWER);
		tv.setText("(W)");
	}

	public void copyBracketMaps(Bracket br) {
		this.smIdMap = br.smIdMap;
		this.smSeedMap = br.smSeedMap;
	}

	public void seedFromParentBracket(Bracket br) {
		copyBracketMaps(br);

		int respawnId;
		for (int idx = 0; idx < matchIds.size() - 1; idx++) {
			respawnId = sm1Idcs.get(idx);
			if (respawnId >= 0 && !br.matchIds.contains(respawnId)) {
				sm1Types.set(idx, BrNodeType.BYE);
				sm1Idcs.set(idx, -1);
			}
			respawnId = sm2Idcs.get(idx);
			if (respawnId >= 0 && !br.matchIds.contains(respawnId)) {
				sm2Types.set(idx, BrNodeType.BYE);
				sm2Idcs.set(idx, -1);
			}
		}
		byeByes();
	}

	public void respawnFromParentBracket(Bracket br) {
		int respawnId;
		BrNodeType nodeType;
		int matchIdx;

		logMatchList("Matches before respawn: ");

		for (int idx = 0; idx < matchIds.size() - 1; idx++) {
			respawnId = sm1Idcs.get(idx);
			nodeType = sm1Types.get(idx);
			matchIdx = br.matchIds.indexOf(respawnId);
			Log.i(LOGTAG,
					"Upper. respawnId: " + respawnId + " ("
							+ nodeType.toString() + "), matchIdx: " + matchIdx);
			if (matchIdx >= 0) {
				if (nodeType == BrNodeType.RESPAWN) {
					if (br.sm1Types.get(matchIdx) == BrNodeType.LOSS) {
						sm1Idcs.set(idx, br.sm1Idcs.get(matchIdx));
						sm1Types.set(idx, BrNodeType.TIP);
					} else if (br.sm2Types.get(matchIdx) == BrNodeType.LOSS) {
						sm1Idcs.set(idx, br.sm2Idcs.get(matchIdx));
						sm1Types.set(idx, BrNodeType.TIP);
					} else if (br.sm2Types.get(matchIdx) == BrNodeType.NA
							&& br.sm1Types.get(matchIdx) == BrNodeType.TIP) {
						sm1Idcs.set(idx, br.sm1Idcs.get(matchIdx));
						sm1Types.set(idx, BrNodeType.TIP);
					}
				}
			}

			respawnId = sm2Idcs.get(idx);
			nodeType = sm2Types.get(idx);
			matchIdx = br.matchIds.indexOf(respawnId);
			Log.i(LOGTAG,
					"Lower. respawnId: " + respawnId + " ("
							+ nodeType.toString() + "), matchIdx: " + matchIdx);
			if (matchIdx >= 0) {
				if (nodeType == BrNodeType.RESPAWN) {
					if (br.sm1Types.get(matchIdx) == BrNodeType.LOSS) {
						sm2Idcs.set(idx, br.sm1Idcs.get(matchIdx));
						sm2Types.set(idx, BrNodeType.TIP);
					} else if (br.sm2Types.get(matchIdx) == BrNodeType.LOSS) {
						sm2Idcs.set(idx, br.sm2Idcs.get(matchIdx));
						sm2Types.set(idx, BrNodeType.TIP);
					} else if (br.sm2Types.get(matchIdx) == BrNodeType.NA
							&& br.sm1Types.get(matchIdx) == BrNodeType.TIP) {
						sm2Idcs.set(idx, br.sm1Idcs.get(matchIdx));
						sm2Types.set(idx, BrNodeType.TIP);
					}
				}
			}
		}

		logMatchList("Matches after respawn: ");
	}

	private void makeInvisibleHeaders(int baseWidth, int tierWidth,
			int aboveViewId, int columnViewId) {
		// invisible headers are for spacing the bracket.
		TextView tv;
		RelativeLayout.LayoutParams lp;
		Context context = rl.getContext();
		int vwHeight = 0;

		// bracket label view
		tv = new TextView(context);
		tv.setText(labelText);
		tv.setTextAppearance(context, android.R.style.TextAppearance_Large);
		tv.setId(1 + headerIdOffset);
		tv.setPadding(0, 40, 0, 0);
		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);

		if (aboveViewId > 0) {
			lp.addRule(RelativeLayout.BELOW, aboveViewId);

		}
		if (labelText == "") {
			tv.setHeight(0);
		}
		rl.addView(tv, lp);

		// horizontal rule
		tv = new TextView(context);
		tv.setHeight(2);
		// tv.setId(1 + headerIdOffset);
		tv.setPadding(10, 10, 10, 10);
		tv.setBackgroundColor(Color.BLACK);
		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
		lp.addRule(RelativeLayout.BELOW, 1 + headerIdOffset);

		rl.addView(tv, lp);

		// header for the labeled brackets on tier 0
		tv = new TextView(context);
		tv.setWidth(baseWidth);
		tv.setHeight(vwHeight);
		tv.setId(2 + headerIdOffset);
		tv.setText(String.valueOf(2 + headerIdOffset));
		tv.setTextColor(Color.WHITE);
		tv.setBackgroundColor(Color.BLACK);
		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		if (columnViewId > 0) {
			lp.addRule(RelativeLayout.ALIGN_RIGHT, columnViewId);
		} else {
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
		}

		lp.addRule(RelativeLayout.BELOW, 1 + headerIdOffset);
		lp.setMargins(0, 10, 0, 0);

		rl.addView(tv, lp);

		// headers for the remaining tiers
		Integer nTiers = factorTwos(nLeafs);

		// tier width = (screen width - label width - arbitrary side spacing) /
		// number of tiers
		// tierWidth = (svWidth - 350 - 100) / nTiers;

		int[] vwColor = { Color.RED, Color.BLUE, Color.GREEN };
		for (Integer i = 0; i < nTiers; i++) {
			tv = new TextView(context);
			lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			if (headerIdOffset != 0 && i == nTiers - 1) {
				lp.addRule(RelativeLayout.ALIGN_RIGHT, headerIdOffset);
			} else {
				tv.setWidth(tierWidth);
			}
			tv.setHeight(vwHeight);
			tv.setId(i + 3 + headerIdOffset);
			tv.setText(String.valueOf(i + 3 + headerIdOffset));
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundColor(vwColor[i % 3]);

			lp.addRule(RelativeLayout.ALIGN_BASELINE, 2 + headerIdOffset);
			lp.addRule(RelativeLayout.RIGHT_OF, i + 2 + headerIdOffset);
			lp.setMargins(-14, 0, 0, 0);
			rl.addView(tv, lp);
		}
	}

	public TextView makeHalfMatchView(Context context, int idx, Boolean upper) {
		TextView tv = new TextView(context);

		int matchId = matchIds.get(idx);
		BrNodeType smType = BrNodeType.TIP;
		SessionMember sm = null;
		String drwStr = "";
		int drwColor = Color.LTGRAY;

		if (upper) {
			drwStr += "upper";
			tv.setId(matchId + matchIdOffset + BrNodeType.UPPER);
			smType = sm1Types.get(idx);
			if (smType == BrNodeType.TIP) {
				sm = smSeedMap.get(sm1Idcs.get(idx));
				tv.setText("(" + String.valueOf(sm.getSeed() + 1) + ") "
						+ sm.getTeam().getTeamName());
				drwStr += "_labeled";
				drwColor = sm.getTeam().getColor();
			} else if (smType == BrNodeType.RESPAWN) {
				tv.setText("(" + (char) (sm1Idcs.get(idx) + 65) + ") ");
				drwStr += "_labeled";
			}
			if (sm2Types.get(idx) == BrNodeType.NA) {
				drwStr = "endpoint";
			}
		} else {
			drwStr += "lower";
			tv.setId(matchId + matchIdOffset + BrNodeType.LOWER);
			smType = sm2Types.get(idx);
			if (smType == BrNodeType.TIP) {
				sm = smSeedMap.get(sm2Idcs.get(idx));
				tv.setText("(" + String.valueOf(sm.getSeed() + 1) + ") "
						+ sm.getTeam().getTeamName());
				drwStr += "_labeled";
				drwColor = sm.getTeam().getColor();
			} else if (smType == BrNodeType.RESPAWN) {
				tv.setText("(" + (char) (sm2Idcs.get(idx) + 65) + ") ");
				drwStr += "_labeled";
			}
		}
		tv.setBackgroundResource(BrDrawable.map.get(drwStr));
		tv.getBackground().setColorFilter(drwColor, Mode.MULTIPLY);
		tv.setGravity(Gravity.RIGHT);
		tv.setTextAppearance(context, android.R.style.TextAppearance_Medium);

		return tv;
	}

	public void addViewToLayout(TextView tv, Boolean isLabeled) {
		Integer matchId = tv.getId() % BrNodeType.MOD - matchIdOffset;
		Boolean upper = isUpperView(tv.getId() - matchIdOffset);
		Integer tier = getTier(matchId);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		if (!isLabeled) {
			lp.addRule(RelativeLayout.ALIGN_LEFT, tier + 2 + headerIdOffset);
			if (tier == getTier(nLeafs - 1)) {
				lp.setMargins(0, -25, 0, 0);
			} else if (upper) {
				Integer topParentMatch = getUpperMatchParent(matchId);
				lp.addRule(RelativeLayout.ALIGN_BOTTOM, topParentMatch
						+ matchIdOffset + BrNodeType.LOWER);
				lp.setMargins(0, -2, 0, 0);
			} else {
				Integer bottomParentMatch = getUpperMatchParent(matchId) + 1;
				lp.addRule(RelativeLayout.ABOVE, bottomParentMatch
						+ matchIdOffset + BrNodeType.LOWER);
				lp.setMargins(0, 0, 0, -2);
			}
		} else {
			if (upper) {
				lp.setMargins(0, 8, 0, 0);
			} else {
				lp.setMargins(0, 0, 0, 8);
			}
		}

		lp.addRule(RelativeLayout.ALIGN_RIGHT, tier + 2 + headerIdOffset);
		lp.addRule(RelativeLayout.BELOW, findViewAbove(tv.getId()));

		rl.addView(tv, lp);
	}

	public void refreshViews() {
		TextView tv;
		int matchId;
		int viewId;
		int drwColor;
		String drwString;
		BrNodeType smType;
		boolean isLabeled;

		for (int idx = 0; idx < length(); idx++) {
			matchId = matchIds.get(idx) + matchIdOffset;

			// match upper view
			viewId = matchId + BrNodeType.UPPER;
			smType = sm1Types.get(idx);
			tv = (TextView) rl.findViewById(viewId);
			drwString = "upper";
			drwColor = Color.LTGRAY;
			isLabeled = tv.getText() != "";

			if (smType != BrNodeType.UNSET && smType != BrNodeType.RESPAWN) {
				drwColor = smSeedMap.get(sm1Idcs.get(idx)).getTeam().getColor();
			}
			if (smType == BrNodeType.LOSS) {
				drwString += "_eliminated";
			}
			if (isLabeled) {
				if (sm1Types.get(idx) != BrNodeType.RESPAWN) {
					SessionMember sm = smSeedMap.get(sm1Idcs.get(idx));
					// String nickname = "(" + String.valueOf(sm.getSeed() + 1)
					// + ") " + sm.getTeam().getTeamName();
					// tv.setText(nickname);
				}
				drwString += "_labeled";
				if (smLost(sm1Idcs.get(idx))) {
					tv.setPaintFlags(tv.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
				}
			}
			if (sm2Types.get(idx) == BrNodeType.NA) {
				drwString = "endpoint";
			}
			tv.setBackgroundResource(BrDrawable.map.get(drwString));
			tv.getBackground().setColorFilter(drwColor, Mode.MULTIPLY);

			// match lower view
			viewId = matchId + BrNodeType.LOWER;
			smType = sm2Types.get(idx);

			if (smType != BrNodeType.NA) {
				tv = (TextView) rl.findViewById(viewId);
				drwString = "lower";
				drwColor = Color.LTGRAY;
				isLabeled = tv.getText() != "";

				if (smType != BrNodeType.UNSET && smType != BrNodeType.RESPAWN) {
					drwColor = smSeedMap.get(sm2Idcs.get(idx)).getTeam()
							.getColor();
				}
				if (smType == BrNodeType.LOSS) {
					drwString += "_eliminated";
				}
				if (isLabeled) {
					if (sm2Types.get(idx) != BrNodeType.RESPAWN) {
						SessionMember sm = smSeedMap.get(sm2Idcs.get(idx));
						// String nickname = "("
						// + String.valueOf(sm.getSeed() + 1) + ") "
						// + sm.getTeam().getTeamName();
						// tv.setText(nickname);
					}
					drwString += "_labeled";
					if (smLost(sm2Idcs.get(idx))) {
						tv.setPaintFlags(tv.getPaintFlags()
								| Paint.STRIKE_THRU_TEXT_FLAG);
					}
				}
				tv.setBackgroundResource(BrDrawable.map.get(drwString));
				tv.getBackground().setColorFilter(drwColor, Mode.MULTIPLY);
			}
		}
	}

	private int getTier(int viewId) {
		// can take view idx or match idx
		int matchId = viewId % BrNodeType.MOD;
		return ((Double) Math.floor(-Math.log(1 - ((double) matchId) / nLeafs)
				/ Math.log(2))).intValue();
	}

	private int getTopMatchOfTier(int tier) {
		return (int) (nLeafs * (1 - Math.pow(2, -tier)));
	}

	private int getUpperMatchParent(int bracketIdx) {
		// can take bracket idx or match idx
		Integer matchIdx = bracketIdx % BrNodeType.MOD;
		Integer tier = getTier(matchIdx);
		Integer topOfTier = getTopMatchOfTier(tier);
		Integer topOfPrevTier = getTopMatchOfTier(tier - 1);

		Integer topParentMatch = topOfPrevTier + 2 * (matchIdx - topOfTier);
		return topParentMatch;
	}

	private int getChildViewId(int bracketIdx) {
		// this can take in a bracket or match idx
		Integer matchIdx = bracketIdx % BrNodeType.MOD;
		Integer tier = getTier(matchIdx);
		Integer topOfTier = getTopMatchOfTier(tier);
		Integer topOfNextTier = getTopMatchOfTier(tier + 1);

		Integer childBracket = topOfNextTier + (matchIdx - topOfTier) / 2
				+ BrNodeType.UPPER;
		if (matchIdx % 2 != 0) {
			childBracket += 1000;
		}
		return childBracket;
	}

	public int findViewAbove(int viewId) {
		Integer matchId = viewId % BrNodeType.MOD - matchIdOffset;

		Integer viewAboveId = headerIdOffset + 2;

		if (!isUpperView(viewId)) {
			viewAboveId = matchId + matchIdOffset + BrNodeType.UPPER;
		} else {
			Integer baseId = matchId;
			if (getTier(matchId) > 0) {
				baseId = getUpperMatchParent(matchId);
			}
			if (matchIds.contains(baseId) && baseId != matchId) {
				viewAboveId = baseId + matchIdOffset + BrNodeType.UPPER;
			} else {
				while (getTier(baseId) > 0) {
					baseId = getUpperMatchParent(baseId);
				}
				if (baseId > 0) {
					// have to keep track of upper/lower now
					baseId += BrNodeType.LOWER - 1; // lower arm of the match
					// above
					while (!matchIds.contains(baseId % BrNodeType.MOD)) {
						baseId = getChildViewId(baseId);
					}
					viewAboveId = baseId + matchIdOffset;
				}
			}
		}

		Log.i(LOGTAG, "viewId: " + viewId + " placed below " + viewAboveId);
		return viewAboveId;
	}

	private boolean isUpperView(int viewId) {
		assert viewId >= 1000;
		if (viewId < 2000) {
			return true;
		} else {
			return false;
		}
	}

	private boolean smLost(int smIdx) {
		boolean hasLost = false;
		int idx1 = sm1Idcs.lastIndexOf(smIdx);
		int idx2 = sm2Idcs.lastIndexOf(smIdx);
		if (idx1 > idx2) {
			if (sm1Types.get(idx1) == BrNodeType.LOSS) {
				hasLost = true;
			}
		} else {
			if (sm2Types.get(idx2) == BrNodeType.LOSS) {
				hasLost = true;
			}
		}
		return hasLost;
	}

	private void seed(List<SessionMember> sMembers) {
		int sm1Seed;
		int sm2Seed;

		// seed the lowest tier
		for (Integer ii = 0; ii < nLeafs; ii += 2) {
			sm1Seed = sMembers.get(ii).getSeed();
			sm2Seed = sMembers.get(ii + 1).getSeed();

			matchIds.add(ii / 2);
			sm1Idcs.add(sm1Seed);
			sm1Types.add(BrNodeType.TIP);
			sm2Idcs.add(sm2Seed);
			if (sm2Seed == BrNodeType.BYE.value()) {
				sm2Types.add(BrNodeType.BYE);
			} else {
				sm2Types.add(BrNodeType.TIP);
			}
			gameIds.add((long) -1);
		}

		// add the rest of the matches
		for (int ii = nLeafs / 2; ii < nLeafs; ii++) {
			matchIds.add(ii);
			sm1Idcs.add(-1);
			sm1Types.add(BrNodeType.UNSET);
			sm2Idcs.add(-1);
			sm2Types.add(BrNodeType.UNSET);
			gameIds.add((long) -1);
		}

		// last match is actually just the winner
		sm2Types.set(nLeafs - 1, BrNodeType.NA);

		byeByes();
	}

	private void seed() {
		List<Integer> idA = new ArrayList<Integer>();
		idA.add(1);
		idA.add(1);
		List<Integer> idB = new ArrayList<Integer>();
		idB.add(2);
		List<Integer> idC = new ArrayList<Integer>();
		idC.add(1);
		idC.add(3);
		idA.addAll(idB);
		idA.addAll(idC);

		int last;
		for (int ii = 1; ii <= (factorTwos(nLeafs) - 3) / 2; ii++) {
			idB.addAll(idB);
			last = idB.size() - 1;
			idB.set(last, (int) (idB.get(last) + Math.pow(2, 2 * ii)));

			idC.addAll(idC);
			last = idC.size() - 1;
			idC.set(last, (int) (idC.get(last) + Math.pow(2, 2 * ii + 1)));

			idA.addAll(idB);
			idA.addAll(idC);
		}

		int matchId = -1;
		int respawnId = 0;
		int tier;
		for (int ii = idA.size() - 1; ii >= 0; ii--) {
			matchId += idA.get(ii);
			matchIds.add(matchId);

			tier = getTier(matchId);
			if (tier % 2 == 0) {
				sm1Idcs.add(respawnId);
				respawnId++;
				sm1Types.add(BrNodeType.RESPAWN);

				sm2Idcs.add(-1);
				if (tier == 0) {

					sm2Types.add(BrNodeType.BYE);
				} else {
					sm2Types.add(BrNodeType.UNSET);
				}
			} else {
				sm1Idcs.add(-1);
				sm1Types.add(BrNodeType.UNSET);
				sm2Idcs.add(-1);
				sm2Types.add(BrNodeType.UNSET);
			}
			gameIds.add((long) -1);
		}

		// For every other tier, the order is swapped
		int ii = (int) (Math.pow(2, (factorTwos(nLeafs) - 3) / 2));
		int idxA = 3 * ii;
		int idxB;

		if (ii > 0) {
			while (idxA < sm1Idcs.size() - 1) {
				for (int jj = 0; jj < ii / 2; jj++) {
					idxB = (int) (idxA + ii - 1 - jj);
					Collections.swap(sm1Idcs, idxA + jj, idxB);
				}
				idxA += (9 * ii) / 4;
				ii /= 4;
			}
		}

		// last match is actually just the winner
		sm2Types.set(sm2Types.size() - 1, BrNodeType.NA);
		byeByes();
	}

	private void seed(int baseSize) {
		int idxW = baseSize - 1;
		int idxL = (int) (Math.pow(2, 2 * factorTwos(baseSize) - 1) - 1);
		Log.i(LOGTAG, "winner base: " + idxW + ", loser base: " + idxL);

		matchIds.addAll(Arrays.asList(0, 2, 3));
		for (int idx = 0; idx < matchIds.size(); idx++) {
			matchIds.set(idx, matchIds.get(idx) + matchIdOffset);
		}

		sm1Idcs.addAll(Arrays.asList(idxW, -1, -1));
		sm1Types.addAll(Arrays.asList(BrNodeType.RESPAWN, BrNodeType.UNSET,
				BrNodeType.UNSET));

		sm2Idcs.addAll(Arrays.asList(idxL, idxW, -1));
		sm2Types.addAll(Arrays.asList(BrNodeType.RESPAWN, BrNodeType.RESPAWN,
				BrNodeType.NA));

		long dumbId = -1;
		gameIds.addAll(Arrays.asList(dumbId, dumbId, dumbId));
	}

	private void byeByes() {
		// remove of bye matches
		int childViewId;
		int childMatchId;
		int childIdx;
		int promoteIdx;
		BrNodeType promoteType;

		// promote players with a bye
		for (int ii = 0; ii < matchIds.size(); ii++) {
			if (sm1Types.get(ii) == BrNodeType.BYE
					|| sm2Types.get(ii) == BrNodeType.BYE) {
				childViewId = getChildViewId(matchIds.get(ii));
				childMatchId = childViewId % BrNodeType.MOD;
				childIdx = matchIds.indexOf(childMatchId);

				if (sm1Types.get(ii) == BrNodeType.BYE) {
					promoteIdx = sm2Idcs.get(ii);
					promoteType = sm2Types.get(ii);
					sm2Types.set(ii, BrNodeType.BYE);
				} else {
					promoteIdx = sm1Idcs.get(ii);
					promoteType = sm1Types.get(ii);
					sm1Types.set(ii, BrNodeType.BYE);
				}

				if (isUpperView(childViewId)) {
					sm1Idcs.set(childIdx, promoteIdx);
					sm1Types.set(childIdx, promoteType);
				} else {
					sm2Idcs.set(childIdx, promoteIdx);
					sm2Types.set(childIdx, promoteType);
				}
			}
		}

		// now go back through and remove all matches with two bye players
		for (int ii = matchIds.size() - 1; ii >= 0; ii--) {
			if (sm1Types.get(ii) == BrNodeType.BYE
					&& sm2Types.get(ii) == BrNodeType.BYE) {
				removeMatch(ii);
			}
		}

		logMatchList("Matches after removing byes: ");
	}

	public List<Game> matchMatches(List<Game> sGames) {
		long gId;
		int smASeed;
		int smBSeed;

		Iterator<Game> gIt = sGames.iterator();
		while (gIt.hasNext()) {
			Game g = gIt.next();
			gId = g.getId();
			// Log.i(LOGTAG, "Game " + gId + ". "
			// + g.getFirstPlayer().getFirstName() + " vs "
			// + g.getSecondPlayer().getFirstName());
			// smASeed = smIdMap.get(g.getFirstPlayer().getId());
			// smBSeed = smIdMap.get(g.getSecondPlayer().getId());

			if (gameIds.contains(gId)) {
				int idx = gameIds.indexOf(gId);
				// assert hasSm(idx, smASeed) && hasSm(idx, smBSeed);
				gIt.remove();
			} else {
				int nMatches = length();
				for (int idx = 0; idx < nMatches; idx++) {
					// if (hasSm(idx, smASeed) && hasSm(idx, smBSeed)
					// && gameIds.get(idx) == -1) {
					// Log.i(LOGTAG, "Matching game " + gId + " to match "
					// + matchIds.get(idx));
					// gameIds.set(idx, gId);
					// gIt.remove();
					// break;
					// }
				}
			}

			if (g.getIsComplete() && gameIds.contains(gId)) {
				// smASeed = smIdMap.get(g.getWinner().getId());
				// promoteWinner(gameIds.indexOf(g.getId()), smASeed);
			}
		}
		return sGames;
	}

	private void promoteWinner(int idx, int wIdx) {
		assert gameIds.get(idx) != -1;
		boolean sm1Wins = true;

		if (wIdx == sm2Idcs.get(idx)) {
			sm1Wins = false;
		} else {
			assert wIdx == sm1Idcs.get(idx);
		}

		if (sm1Wins) {
			sm1Types.set(idx, BrNodeType.WIN);
			sm2Types.set(idx, BrNodeType.LOSS);
			wIdx = sm1Idcs.get(idx);
		} else {
			sm1Types.set(idx, BrNodeType.LOSS);
			sm2Types.set(idx, BrNodeType.WIN);
			wIdx = sm2Idcs.get(idx);
		}

		int childViewId = getChildViewId(matchIds.get(idx));
		int childIdx = matchIds.indexOf(childViewId % BrNodeType.MOD);

		if (isUpperView(childViewId)) {
			sm1Idcs.set(childIdx, wIdx);
			sm1Types.set(childIdx, BrNodeType.TIP);
		} else {
			sm2Idcs.set(childIdx, wIdx);
			sm2Types.set(childIdx, BrNodeType.TIP);
		}

		// if a player was promoted to play against self, as in finals
		if (sm1Idcs.get(childIdx) == sm2Idcs.get(childIdx)
				&& sm1Types.get(childIdx) == sm2Types.get(childIdx)) {
			promoteSelf(childIdx);
		}
	}

	private void promoteSelf(int idx) {
		// sm1Types.set(idx, BrNodeType.WIN);
		// sm2Types.set(idx, BrNodeType.LOSS);
		int wIdx = sm1Idcs.get(idx);

		int childViewId = getChildViewId(matchIds.get(idx));
		int childIdx = matchIds.indexOf(childViewId % BrNodeType.MOD);
		sm1Idcs.set(childIdx, wIdx);
		sm1Types.set(childIdx, BrNodeType.TIP);
	}

	public Boolean hasView(int viewId) {
		int matchId = viewId % BrNodeType.MOD - matchIdOffset;
		if (matchIds.contains(matchId)) {
			return true;
		} else {
			return false;
		}
	}

	private Boolean hasSm(int idx, int smIdx) {
		Boolean hasSm = false;
		if (sm1Idcs.get(idx) == smIdx
				&& sm1Types.get(idx) != BrNodeType.RESPAWN) {
			hasSm = true;
		} else if (sm2Idcs.get(idx) == smIdx
				&& sm2Types.get(idx) != BrNodeType.RESPAWN) {
			hasSm = true;
		}
		return hasSm;
	}

	public Integer lowestViewId() {
		int viewId = nLeafs / 2 - 1 + matchIdOffset + BrNodeType.LOWER;

		while (!matchIds.contains(viewId % BrNodeType.MOD)) {
			viewId = getChildViewId(viewId);
			Log.i(LOGTAG, "then " + viewId);
		}
		return viewId;
	}

	public MatchInfo getMatchInfo(int viewId) {
		MatchInfo mInfo = new MatchInfo();
		int matchId = viewId % BrNodeType.MOD - matchIdOffset;
		if (matchIds.contains(matchId)) {
			int idx = matchIds.indexOf(matchId);
			long game_id = gameIds.get(idx);
			mInfo.setIdInSession(game_id);

			BrNodeType sm1Type = sm1Types.get(idx);
			BrNodeType sm2Type = sm2Types.get(idx);

			if (sm1Type == BrNodeType.TIP || sm1Type == BrNodeType.WIN
					|| sm1Type == BrNodeType.LOSS) {
				mInfo.setTeam1(smSeedMap.get(sm1Idcs.get(idx)).getTeam());
			}

			if (sm2Type == BrNodeType.TIP || sm2Type == BrNodeType.WIN
					|| sm2Type == BrNodeType.LOSS) {
				mInfo.setTeam2(smSeedMap.get(sm2Idcs.get(idx)).getTeam());
			}

			// upper team
			String title = "";
			if (sm1Type == BrNodeType.UNSET || sm1Type == BrNodeType.RESPAWN) {
				title += "Unknown";
			} else {
				title += smSeedMap.get(sm1Idcs.get(idx)).getTeam()
						.getTeamName();
				if (sm1Type == BrNodeType.WIN) {
					title += " (W)";
				} else if (sm1Type == BrNodeType.LOSS) {
					title += " (L)";
				}
			}

			// lower team
			if (sm2Type == BrNodeType.UNSET || sm2Type == BrNodeType.RESPAWN) {
				title += " -vs- Unknown";
			} else if (sm2Type == BrNodeType.NA) {
				title += ", bracket winner.";
			} else {
				title += " -vs- "
						+ smSeedMap.get(sm2Idcs.get(idx)).getTeam()
								.getTeamName();
				if (sm2Type == BrNodeType.WIN) {
					title += " (W)";
				} else if (sm2Type == BrNodeType.LOSS) {
					title += " (L)";
				}
			}

			int id_in_session = matchId + matchIdOffset;
			String subtitle = "id: " + id_in_session + ", match: "
					+ matchIds.get(idx) + ", gameId: " + game_id;

			mInfo.title = title;
			mInfo.subtitle = subtitle;

			// boolean allow_create = false;
			// if (sm1Type == BrNodeType.TIP && sm2Type == BrNodeType.TIP) {
			// allow_create = true;
			// }

			// boolean allow_view = false;
			// if (sm1Type == BrNodeType.WIN || sm1Type == BrNodeType.LOSS) {
			// assert sm2Type == BrNodeType.WIN || sm2Type == BrNodeType.LOSS;
			// allow_view = true;
			// }
		}
		return mInfo;
	}

	public int length() {
		assert matchIds.size() == sm1Idcs.size();
		assert matchIds.size() == sm1Types.size();
		assert matchIds.size() == sm2Idcs.size();
		assert matchIds.size() == sm2Types.size();
		assert matchIds.size() == gameIds.size();
		return matchIds.size();
	}

	/** find n such that 2**n >= p */
	public static Integer factorTwos(int p) {
		Integer n = 1;
		while (Math.pow(2, n) < p) {
			n++;
		}
		return n;
	}

	private void logMatchList(String header) {
		Log.i(LOGTAG, header);
		for (int ii = 0; ii < matchIds.size(); ii++) {
			Log.i(LOGTAG,
					"(" + ii + ") Match " + matchIds.get(ii) + ", p1Seed: "
							+ sm1Idcs.get(ii) + " ("
							+ sm1Types.get(ii).toString() + "), p2Seed: "
							+ sm2Idcs.get(ii) + " ("
							+ sm2Types.get(ii).toString() + "), gId: "
							+ gameIds.get(ii));
		}
	}
}
