package com.pocketleague.manager.db.tables;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pocketleague.manager.db.DatabaseHelper;

@DatabaseTable
public class Player implements Comparable<Player> {
	public static final String NICK_NAME = "nickname";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String IS_LEFT_HANDED = "is_left_handed";
	public static final String IS_RIGHT_HANDED = "is_right_handed";
	public static final String IS_LEFT_FOOTED = "is_left_footed";
	public static final String IS_RIGHT_FOOTED = "is_right_footed";
	public static final String HEIGHT = "height_cm";
	public static final String WEIGHT = "weight_kg";
	public static final String COLOR = "color";
	public static final String IS_ACTIVE = "is_active";
	public static final String IS_FAVORITE = "is_favorite";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, unique = true)
	private String nickname;

	@DatabaseField()
	private String first_name;

	@DatabaseField()
	private String last_name;

	@DatabaseField
	private boolean is_left_handed;

	@DatabaseField
	private boolean is_right_handed;

	@DatabaseField
	private boolean is_left_footed;

	@DatabaseField
	private boolean is_right_footed;

	@DatabaseField
	private int height_cm;

	@DatabaseField
	private int weight_kg;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	private byte[] image_bytes;

	@DatabaseField
	private int color;

	@DatabaseField
	private boolean is_active = true;

	@DatabaseField
	private boolean is_favorite = false;

	Player() {
	}

	public Player(String nickname, int color) {
		super();
		this.nickname = nickname;
		this.color = color;
	}

	public Player(String nickname, String first_name, String last_name,
			boolean is_left_handed, boolean is_right_handed,
			boolean is_left_footed, boolean is_right_footed, int height_cm,
			int weight_kg, byte[] image_bytes, int color) {
		super();
		this.nickname = nickname;
		this.first_name = first_name;
		this.last_name = last_name;
		this.is_left_handed = is_left_handed;
		this.is_right_handed = is_right_handed;
		this.is_left_footed = is_left_footed;
		this.is_right_footed = is_right_footed;
		this.height_cm = height_cm;
		this.weight_kg = weight_kg;
		this.image_bytes = image_bytes;
		this.color = color;
	}

	public static Dao<Player, Long> getDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		Dao<Player, Long> d = null;
		try {
			d = helper.getPlayerDao();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get player dao: ", e);
		}
		return d;
	}

	public long getId() {
		return id;
	}

	// public void setId(long id) {
	// this.id = id;
	// }

	public String getNickName() {
		return nickname;
	}

	public void setNickName(String nickName) {
		this.nickname = nickName;
	}

	public String getFirstName() {
		return first_name;
	}

	public void setFirstName(String firstName) {
		this.first_name = firstName;
	}

	public String getLastName() {
		return last_name;
	}

	public void setLastName(String lastName) {
		this.last_name = lastName;
	}

	public boolean getIsLeftHanded() {
		return is_left_handed;
	}

	public void setIsLeftHanded(boolean is_left_handed) {
		this.is_left_handed = is_left_handed;
	}

	public boolean getIsRightHanded() {
		return is_right_handed;
	}

	public void setIsRightHanded(boolean is_right_handed) {
		this.is_right_handed = is_right_handed;
	}

	public boolean getIsLeftFooted() {
		return is_left_footed;
	}

	public void setIsLeftFooted(boolean is_left_footed) {
		this.is_left_footed = is_left_footed;
	}

	public boolean getIsRightFooted() {
		return is_right_footed;
	}

	public void setIsRightFooted(boolean is_right_footed) {
		this.is_right_footed = is_right_footed;
	}

	public int getHeight_cm() {
		return height_cm;
	}

	public void setHeight_cm(int height_cm) {
		this.height_cm = height_cm;
	}

	public int getWeight_kg() {
		return weight_kg;
	}

	public void setWeight_kg(int weight_kg) {
		this.weight_kg = weight_kg;
	}

	public byte[] getImageBytes() {
		return image_bytes;
	}

	public void setImageBytes(byte[] image_bytes) {
		this.image_bytes = image_bytes;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean getIsActive() {
		return is_active;
	}

	public void setIsActive(boolean is_active) {
		this.is_active = is_active;
	}

	public boolean getIsFavorite() {
		return is_favorite;
	}

	public void setIsFavorite(boolean is_favorite) {
		this.is_favorite = is_favorite;
	}

	// =========================================================================
	// Additional methods
	// =========================================================================

	public String getDisplayName() {
		return first_name + " \"" + nickname + "\" " + last_name;
	}

	public int compareTo(Player another) {
		if (id < another.id) {
			return -1;
		} else if (id == another.id) {
			return 0;
		} else {
			return 1;
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof Player))
			return false;
		Player another = (Player) o;
		if (id == another.id) {
			return true;
		} else {
			return false;
		}
	}

	// public static long getIdByNames(String nick, Context context)
	// throws SQLException {
	// Player p = getByNames(nick, context);
	// if (p == null) {
	// return -1;
	// }
	// return p.getId();
	// }
	//
	// public static Player getByNames(String nick, Context context)
	// throws SQLException {
	// List<Player> pList = null;
	// HashMap<String, Object> m = buildNameMap(nick);
	//
	// pList = getDao(context).queryForFieldValuesArgs(m);
	// if (pList.isEmpty()) {
	// return null;
	// } else {
	// return pList.get(0);
	// }
	// }
	//
	public static boolean exists(String first, String last, String nick,
			Context context) throws SQLException {
		if (first == null || last == null || nick == null) {
			return false;
		}
		List<Player> pList = null;
		// HashMap<String, Object> m = buildNameMap(first, last, nick);

		// pList = getDao(context).queryForFieldValuesArgs(m);
		// if (pList.isEmpty()) {
		// return false;
		// } else {
		return true;
		// }
	}

	public boolean exists(Context context) throws SQLException {
		return exists(first_name, last_name, nickname, context);
	}

	public static List<Player> getAll(Context context) throws SQLException {
		Dao<Player, Long> d = Player.getDao(context);
		List<Player> players = new ArrayList<Player>();
		for (Player p : d) {
			players.add(p);
		}
		return players;
	}
}
