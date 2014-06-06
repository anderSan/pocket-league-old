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
import com.pocketleague.manager.db.DatabaseCommonQueue;
import com.pocketleague.manager.db.tables.Player;
import com.pocketleague.manager.db.tables.Team;

public class Detail_Player extends MenuContainerActivity {
	private static final String LOGTAG = "Detail_Player";
	Long pId;
	Player p;
	Team t;
	Dao<Player, Long> pDao;
	Dao<Team, Long> tDao;

	TextView tv_playerName;
	TextView tv_playerId;
	TextView tv_height;
	TextView tv_weight;
	TextView tv_handed;
	TextView tv_footed;
	CheckBox cb_isFavorite;
	Switch sw_isActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_player);

		Intent intent = getIntent();
		pId = intent.getLongExtra("PID", -1);

		pDao = Player.getDao(this);
		tDao = Team.getDao(this);

		tv_playerName = (TextView) findViewById(R.id.pDet_name);
		tv_playerId = (TextView) findViewById(R.id.pDet_id);
		tv_height = (TextView) findViewById(R.id.pDet_height);
		tv_weight = (TextView) findViewById(R.id.pDet_weight);
		tv_handed = (TextView) findViewById(R.id.pDet_handed);
		tv_footed = (TextView) findViewById(R.id.pDet_footed);
		cb_isFavorite = (CheckBox) findViewById(R.id.pDet_isFavorite);
		cb_isFavorite.setOnClickListener(favoriteClicked);
		sw_isActive = (Switch) findViewById(R.id.pDet_isActive);
		sw_isActive.setOnClickListener(activeClicked);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem fav = menu.add(R.string.menu_modify);
		fav.setIcon(R.drawable.ic_action_edit);
		fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		Intent intent = new Intent(this, NewPlayer.class);
		intent.putExtra("PID", pId);

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
		if (pId != -1) {
			try {
				p = pDao.queryForId(pId);
				t = DatabaseCommonQueue.findPlayerSoloTeam(this, p);
			} catch (SQLException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		tv_playerName.setText(p.getNickName() + " (" + p.getFirstName() + ' '
				+ p.getLastName() + ")");
		tv_playerId.setText(String.valueOf(p.getId()));
		tv_height.setText("Height: " + String.valueOf(p.getHeight()) + " cm");
		tv_weight.setText("Weight: " + String.valueOf(p.getWeight()) + " kg");
		if (p.getIsLeftHanded()) {
			if (p.getIsRightHanded()) {
				tv_handed.setText("L + R");
			} else {
				tv_handed.setText("L");
			}
		} else {
			tv_handed.setText("R");
		}

		if (p.getIsLeftFooted()) {
			if (p.getIsRightFooted()) {
				tv_footed.setText("L + R");
			} else {
				tv_footed.setText("L");
			}
		} else {
			tv_footed.setText("R");
		}

		cb_isFavorite.setChecked(p.getIsFavorite());
		sw_isActive.setChecked(p.getIsActive());
	}

	private OnClickListener favoriteClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (pId != -1) {
				boolean is_favorite = ((CheckBox) v).isChecked();
				p.setIsFavorite(is_favorite);
				t.setIsFavorite(is_favorite);
				updatePlayer();
			}
		}
	};

	private OnClickListener activeClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (pId != -1) {
				boolean is_active = ((Switch) v).isChecked();
				p.setIsActive(is_active);
				t.setIsActive(is_active);
				updatePlayer();
			}
		}
	};

	private void updatePlayer() {
		try {
			pDao.update(p);
			tDao.update(t);
		} catch (SQLException e) {
			loge("Could not update player", e);
			e.printStackTrace();
		}
	}
}
