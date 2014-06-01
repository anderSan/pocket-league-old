package com.pocketleague.manager;

import java.sql.SQLException;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.pocketleague.manager.backend.MenuContainerActivity;
import com.pocketleague.manager.db.tables.Player;
import com.pocketleague.manager.db.tables.Team;
import com.pocketleague.manager.db.tables.TeamMember;

public class NewPlayer extends MenuContainerActivity {
	Long pId;
	Player p;
	Team t;
	Dao<Player, Long> pDao;
	Dao<Team, Long> tDao;
	Dao<TeamMember, Long> tmDao;

	Button btn_create;
	TextView tv_nick;
	TextView tv_name;
	TextView tv_weight;
	TextView tv_height;
	CheckBox cb_lh;
	CheckBox cb_rh;
	CheckBox cb_lf;
	CheckBox cb_rf;
	Button btn_color;
	int player_color = Color.BLACK;
	CheckBox cb_isActive;
	CheckBox cb_isFavorite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_player);

		pDao = Player.getDao(this);
		tDao = Team.getDao(this);
		tmDao = TeamMember.getDao(this);

		btn_create = (Button) findViewById(R.id.button_createPlayer);
		tv_nick = (TextView) findViewById(R.id.editText_nickname);
		tv_name = (TextView) findViewById(R.id.editText_playerName);
		tv_weight = (TextView) findViewById(R.id.editText_weight);
		tv_height = (TextView) findViewById(R.id.editText_height);
		cb_lh = (CheckBox) findViewById(R.id.checkBox_leftHanded);
		cb_rh = (CheckBox) findViewById(R.id.checkBox_rightHanded);
		cb_lf = (CheckBox) findViewById(R.id.checkBox_leftFooted);
		cb_rf = (CheckBox) findViewById(R.id.checkBox_rightFooted);
		btn_color = (Button) findViewById(R.id.newPlayer_colorPicker);
		cb_isActive = (CheckBox) findViewById(R.id.newPlayer_isActive);
		cb_isFavorite = (CheckBox) findViewById(R.id.newPlayer_isFavorite);

		Intent intent = getIntent();
		pId = intent.getLongExtra("PID", -1);
		if (pId != -1) {
			loadPlayerValues();
		}
	}

	private void loadPlayerValues() {
		try {
			p = pDao.queryForId(pId);
			t = findPlayerTeam(p);
			btn_create.setText("Modify");
			tv_nick.setText(p.getNickName());
			tv_name.setText(p.getFirstName() + " " + p.getLastName());
			tv_weight.setText(String.valueOf(p.getWeight_kg()));
			tv_height.setText(String.valueOf(p.getHeight_cm()));
			cb_lh.setChecked(p.getIsLeftHanded());
			cb_rh.setChecked(p.getIsRightHanded());
			cb_lf.setChecked(p.getIsLeftFooted());
			cb_rf.setChecked(p.getIsRightFooted());
			btn_color.setBackgroundColor(p.getColor());
			player_color = p.getColor();
			cb_isActive.setVisibility(View.VISIBLE);
			cb_isActive.setChecked(p.getIsActive());
			cb_isFavorite.setChecked(p.getIsFavorite());
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void doneButtonPushed(View view) {

		String nickname = tv_nick.getText().toString().trim();
		if (nickname.isEmpty()) {
			Toast.makeText(this, "Nickname is required.", Toast.LENGTH_LONG)
					.show();
		} else {
			String[] names = tv_name.getText().toString().split("\\s+", 2);
			String first_name = names[0];
			String last_name = "";
			if (names.length >= 2) {
				last_name = names[1];
			}

			int weight_kg = 0;
			String s = tv_weight.getText().toString().trim();
			if (!s.isEmpty()) {
				weight_kg = Integer.parseInt(s);
			}

			int height_cm = 0;
			s = tv_height.getText().toString().trim();
			if (!s.isEmpty()) {
				height_cm = Integer.parseInt(s);
			}

			Boolean lh = cb_lh.isChecked();
			Boolean rh = cb_rh.isChecked();
			Boolean lf = cb_lf.isChecked();
			Boolean rf = cb_rf.isChecked();

			byte[] emptyImage = new byte[0];

			Boolean is_active = cb_isActive.isChecked();
			Boolean is_favorite = cb_isFavorite.isChecked();

			if (pId != -1) {
				modifyPlayer(nickname, first_name, last_name, lh, rh, lf, rf,
						height_cm, weight_kg, emptyImage, is_active,
						is_favorite);
			} else {
				createPlayer(nickname, first_name, last_name, lh, rh, lf, rf,
						height_cm, weight_kg, emptyImage, is_favorite);
			}
		}
	}

	private void createPlayer(String nickname, String first_name,
			String last_name, boolean lh, boolean rh, boolean lf, boolean rf,
			int height_cm, int weight_kg, byte[] image, boolean is_favorite) {

		Player newPlayer = new Player(nickname, first_name, last_name, lh, rh,
				lf, rf, height_cm, weight_kg, image, player_color, is_favorite);
		Team newTeam = new Team(nickname, 1, player_color, is_favorite);
		TeamMember newTeamMember = new TeamMember(newTeam, newPlayer);

		try {
			if (newPlayer.exists(this) || newTeam.exists(this)) {
				Toast.makeText(this, "Player already exists.",
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					pDao.create(newPlayer);
					tDao.create(newTeam);
					tmDao.create(newTeamMember);
					Toast.makeText(this, "Player created!", Toast.LENGTH_SHORT)
							.show();
					finish();
				} catch (SQLException ee) {
					loge("Could not create player", ee);
					Toast.makeText(this, "Could not create player.",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (SQLException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			loge("Could not test for existence of player or team", e);
		}
	}

	private void modifyPlayer(String nickname, String first_name,
			String last_name, boolean lh, boolean rh, boolean lf, boolean rf,
			int height_cm, int weight_kg, byte[] image, boolean is_active,
			boolean is_favorite) {

		p.setNickName(nickname);
		p.setFirstName(first_name);
		p.setLastName(last_name);
		p.setIsLeftHanded(lh);
		p.setIsRightHanded(rh);
		p.setIsLeftFooted(lf);
		p.setIsRightFooted(rf);
		p.setWeight_kg(weight_kg);
		p.setHeight_cm(height_cm);
		p.setColor(player_color);
		p.setIsActive(is_active);
		p.setIsFavorite(is_favorite);

		t.setTeamName(nickname);
		t.setIsActive(is_active);
		t.setIsFavorite(is_favorite);
		try {
			pDao.update(p);
			tDao.update(t);
			Toast.makeText(this, "Player modified.", Toast.LENGTH_SHORT).show();
			finish();
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(this, "Could not modify player.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private Team findPlayerTeam(Player p) throws SQLException {
		QueryBuilder<TeamMember, Long> tmQb = tmDao.queryBuilder();
		tmQb.where().eq(TeamMember.PLAYER, p);
		QueryBuilder<Team, Long> tQb = tDao.queryBuilder();
		tQb.where().eq(Team.TEAM_SIZE, 1);
		// join with the order query
		List<Team> results = tQb.join(tmQb).query();
		if (results.size() > 1) {
			log("Warning: Multiple teams found for player " + p.getNickName());
			Toast.makeText(this, "Warning: Multiple teams found for player!",
					Toast.LENGTH_SHORT).show();
		}

		return results.get(0);
	}

	public void showColorPicker(View view) {
		// initialColor is the initially-selected color to be shown in the
		// rectangle on the left of the arrow.
		// for example, 0xff000000 is black, 0xff0000ff is blue. Please be aware
		// of the initial 0xff which is the alpha.
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, player_color,
				new OnAmbilWarnaListener() {
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						player_color = color;
						btn_color.setBackgroundColor(color);
					}

					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// cancel was selected by the user
					}
				});

		dialog.show();
	}
}
