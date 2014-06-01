package com.pocketleague.manager;

import java.sql.SQLException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.pocketleague.manager.backend.MenuContainerActivity;
import com.pocketleague.manager.db.tables.Venue;

public class NewVenue extends MenuContainerActivity {
	Long vId;
	Venue v;
	Dao<Venue, Long> vDao;

	Button btn_create;
	TextView tv_name;
	CheckBox cb_isActive;
	CheckBox cb_isFavorite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_venue);

		vDao = Venue.getDao(this);

		btn_create = (Button) findViewById(R.id.button_createVenue);
		tv_name = (TextView) findViewById(R.id.editText_venueName);
		cb_isActive = (CheckBox) findViewById(R.id.newVenue_isActive);
		cb_isFavorite = (CheckBox) findViewById(R.id.newVenue_isFavorite);

		Intent intent = getIntent();
		vId = intent.getLongExtra("VID", -1);
		if (vId != -1) {
			loadVenueValues();
		}
	}

	private void loadVenueValues() {
		try {
			v = vDao.queryForId(vId);
			btn_create.setText("Modify");
			tv_name.setText(v.getName());
			cb_isActive.setVisibility(View.VISIBLE);
			cb_isActive.setChecked(v.getIsActive());
			cb_isFavorite.setChecked(v.getIsFavorite());
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void doneButtonPushed(View view) {
		String venue_name = tv_name.getText().toString().trim();
		if (venue_name.isEmpty()) {
			Toast.makeText(this, "Venue name is required.", Toast.LENGTH_LONG)
					.show();
		} else {
			Boolean is_active = cb_isActive.isChecked();
			Boolean is_favorite = cb_isFavorite.isChecked();

			if (vId != -1) {
				modifyVenue(venue_name, is_active, is_favorite);
			} else {
				createVenue(venue_name, is_favorite);
			}
		}
	}

	private void createVenue(String venue_name, boolean is_favorite) {
		Venue newVenue = new Venue(venue_name, is_favorite);

		try {
			vDao.create(newVenue);
			Toast.makeText(this, "Venue created!", Toast.LENGTH_SHORT).show();
			finish();
		} catch (SQLException e) {
			loge("Could not create venue", e);
			Toast.makeText(this, "Could not create venue.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void modifyVenue(String venue_name, boolean is_active,
			boolean is_favorite) {
		v.setName(venue_name);
		v.setIsActive(is_active);
		v.setIsFavorite(is_favorite);
		try {
			vDao.update(v);
			Toast.makeText(this, "Venue modified.", Toast.LENGTH_SHORT).show();
			finish();
		} catch (SQLException e) {
			e.printStackTrace();
			loge("Could not modify venue", e);
			Toast.makeText(this, "Could not modify venue.", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
