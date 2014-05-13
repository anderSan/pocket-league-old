package com.pocketleague.manager.db.tables;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.db.DatabaseHelper;

@DatabaseTable
public class Venue {
	public static final String NAME = "name";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ZIP_CODE = "zip_code";
	public static final String IS_ACTIVE = "is_active";
	public static final String IS_FAVORITE = "is_favorite";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, unique = true)
	private String name;

	@DatabaseField
	private long latitude;

	@DatabaseField
	private long longitude;

	@DatabaseField
	private long zipCode;

	@DatabaseField
	private boolean is_active = true;

	@DatabaseField
	private boolean is_favorite = false;

	Venue() {
	}

	public Venue(String name) {
		super();
		this.name = name;
	}

	public static Dao<Venue, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<Venue, Long> d = null;
		try {
			d = helper.getVenueDao();
		} catch (SQLException e) {
			throw new RuntimeException("Couldn't get venue dao: ", e);
		}
		return d;
	}

	public static List<Venue> getAll(Context context) throws SQLException {
		Dao<Venue, Long> d = Venue.getDao(context);
		List<Venue> venues = new ArrayList<Venue>();
		for (Venue v : d) {
			venues.add(v);
		}
		return venues;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String venue_name) {
		this.name = venue_name;
	}

	public boolean exists(Context context) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getIsActive() {
		return is_active;
	}

	public void setIsActive(boolean is_active) {
		this.is_active = is_active;
	}
}
