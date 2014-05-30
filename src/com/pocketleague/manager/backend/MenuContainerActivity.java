package com.pocketleague.manager.backend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.pocketleague.gametypes.GameType;
import com.pocketleague.manager.db.DatabaseHelper;

public class MenuContainerActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	public static String LOGTAG = "MenuContainer";
	public static final String APP_PREFS = "PocketLeaguePreferences";

	private SharedPreferences settings;
	private SharedPreferences.Editor prefs_editor;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = this.getSharedPreferences(APP_PREFS, 0);
		prefs_editor = settings.edit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public String getPreference(String pref_name, String pref_default) {
		return settings.getString(pref_name, pref_default);
	}

	public void setPreference(String pref_name, String pref_value) {
		prefs_editor.putString(pref_name, pref_value);
		prefs_editor.commit();
	}

	public GameType getCurrentGameType() {
		return GameType.valueOf(getPreference("currentGameType",
				GameType.UNDEFINED.toString()));
	}

	public void setCurrentGameType(GameType gametype) {
		setPreference("currentGameType", gametype.toString());
	}

	public void log(String msg) {
		Log.i(LOGTAG, msg);
	}

	public void logd(String msg) {
		Log.d(LOGTAG, msg);
	}

	public void loge(String msg, Exception e) {
		Log.e(LOGTAG, msg + ": " + e.getMessage());
	}
}
