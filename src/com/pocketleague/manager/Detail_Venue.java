package com.pocketleague.manager;

import java.sql.SQLException;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.backend.MenuContainerActivity;
import com.pocketleague.manager.db.tables.Venue;

public class Detail_Venue extends MenuContainerActivity {
	private static final String LOGTAG = "Detail_Venue";
	Long vId;
	Venue v;
	Dao<Venue, Long> vDao;

	TextView tv_venueName;
	TextView tv_venueId;
	CheckBox cb_isFavorite;
	Switch sw_isActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_venue);

		Intent intent = getIntent();
		vId = intent.getLongExtra("VID", -1);

		vDao = Venue.getDao(this);

		tv_venueName = (TextView) findViewById(R.id.vDet_name);
		tv_venueId = (TextView) findViewById(R.id.vDet_id);
		cb_isFavorite = (CheckBox) findViewById(R.id.vDet_isFavorite);
		cb_isFavorite.setOnClickListener(favoriteClicked);
		sw_isActive = (Switch) findViewById(R.id.vDet_isActive);
		sw_isActive.setOnClickListener(activeClicked);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem fav = menu.add(R.string.menu_modify);
		fav.setIcon(R.drawable.ic_action_edit);
		fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		Intent intent = new Intent(this, NewVenue.class);
		intent.putExtra("VID", vId);

		fav.setIntent(intent);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		refreshDetails();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshDetails();
	}

	public void refreshDetails() {
		if (vId != -1) {
			try {

				v = vDao.queryForId(vId);
			} catch (SQLException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

		tv_venueName.setText(v.getName());
		tv_venueId.setText(String.valueOf(v.getId()));
		cb_isFavorite.setChecked(v.getIsFavorite());
		sw_isActive.setChecked(v.getIsActive());
	}

	private OnClickListener favoriteClicked = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (vId != -1) {
				v.setIsFavorite(((CheckBox) view).isChecked());
				updateVenue();
			}
		}
	};

	private OnClickListener activeClicked = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (vId != -1) {
				v.setIsActive(((Switch) view).isChecked());
				updateVenue();
			}
		}
	};

	private void updateVenue() {
		try {
			vDao.update(v);
		} catch (SQLException e) {
			loge("Could not update venue", e);
			e.printStackTrace();
		}
	}
}
