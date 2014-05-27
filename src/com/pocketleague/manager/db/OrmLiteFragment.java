package com.pocketleague.manager.db;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class OrmLiteFragment extends Fragment {
	public static String LOGTAG = "OrmLiteFragment";
	public static final String APP_PREFS = "PocketLeaguePreferences";

	private SharedPreferences settings;
	public SharedPreferences.Editor prefs_editor;
	private DatabaseHelper databaseHelper = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		settings = this.getActivity().getSharedPreferences(APP_PREFS, 0);
		prefs_editor = settings.edit();
	}

	protected DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(getActivity(),
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	public String getPreference(String pref_name, String pref_default) {
		return settings.getString(pref_name, pref_default);
	}

	public void setPreference(String pref_name, String pref_value) {
		prefs_editor.putString(pref_name, pref_value);
		prefs_editor.commit();
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