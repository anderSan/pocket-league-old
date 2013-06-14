package com.ultimatepolish.polishscorebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.ultimatepolish.scorebookdb.Player;
import com.ultimatepolish.scorebookdb.Session;
import com.ultimatepolish.scorebookdb.SessionMember;
import com.ultimatepolish.scorebookdb.SessionType;
import com.ultimatepolish.scorebookdb.Team;

public class NewSession extends MenuContainerActivity {
	Long sId;
	Session s;
	Dao<Session, Long> sDao;
	
	TextView name;
	Spinner sessionTypeSpinner;
	CheckBox isTeamCB;
	CheckBox isActiveCB;
	ListView rosterCheckList;
	List<Player> players = new ArrayList<Player>();
	List<Integer> playerIdxList = new ArrayList<Integer>();
	List<String> playerNames = new ArrayList<String>();
	List<Team> teams = new ArrayList<Team>();
	List<Integer> teamIdxList = new ArrayList<Integer>();
	List<String> teamNames = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);
		
		name = (TextView) findViewById(R.id.editText_sessionName);
		Button createButton = (Button) findViewById(R.id.button_createSession);		
		sessionTypeSpinner = (Spinner) findViewById(R.id.newSession_sessionType);
		isTeamCB = (CheckBox) findViewById(R.id.newSession_isTeam);
		isActiveCB = (CheckBox) findViewById(R.id.newSession_isActive);
		
		
		List<String> sessionTypes = new ArrayList<String>();
		sessionTypes.add(SessionType.typeString[SessionType.LEAGUE]);
		sessionTypes.add(SessionType.typeString[SessionType.LADDER]);
		sessionTypes.add(SessionType.typeString[SessionType.SNGL_ELIM]);
		sessionTypes.add(SessionType.typeString[SessionType.DBL_ELIM]);
		ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_dropdown_item,
				sessionTypes);
		sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sessionTypeSpinner.setAdapter(sAdapter);
		
		try{
			players = Player.getAll(getApplicationContext());
			playerNames.clear();
			for(Player p: players){
				playerNames.add(p.getFirstName() + " " + p.getLastName());
			}
			teams = Team.getAll(getApplicationContext());
			teamNames.clear();
			for(Team t: teams){
				teamNames.add(t.getTeamName());
			}
		}
		catch (SQLException e){
			Toast.makeText(getApplicationContext(), 
					e.getMessage(), 
					Toast.LENGTH_LONG).show();
		}
		
		Intent intent = getIntent();
		sId = intent.getLongExtra("SID", -1);
		if (sId != -1){
			try{
				sDao = Session.getDao(getApplicationContext());
				s = sDao.queryForId(sId);
				createButton.setText("Modify");
				name.setText(s.getSessionName());
				sessionTypeSpinner.setVisibility(View.GONE);
				isTeamCB.setVisibility(View.GONE);
				isActiveCB.setVisibility(View.VISIBLE);
				isActiveCB.setChecked(s.getIsActive());
				
				// TODO: if loading a session, show player/team names or hide box
				// but dont allow session roster to change or bad things could happen!
				playerNames.clear();
				teamNames.clear();
			}
			catch (SQLException e){
				Toast.makeText(getApplicationContext(), 
						e.getMessage(), 
						Toast.LENGTH_LONG).show();
			}
		}
		
		rosterCheckList = (ListView) findViewById(R.id.newSession_playerSelection);
        rosterCheckList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        updateRosterCheckList();
        rosterCheckList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View view, int pos, long arg3) 
            {
            	if (isTeamCB.isChecked()) {
	                if(teamIdxList.contains(pos))
	                {
	                    teamIdxList.remove((Integer) pos);
	                } else {
	                    teamIdxList.add(pos);
	                }
            	} else {
            		if(playerIdxList.contains(pos))
	                {
	                    playerIdxList.remove((Integer) pos);
	                } else {
	                    playerIdxList.add(pos);
	                }
            	}
                
            	String strText = "";
                
            	if (isTeamCB.isChecked()) {
            		Collections.sort(teamIdxList);
            		for(int i=0 ; i < teamIdxList.size(); i++)                   
                        strText += teams.get(teamIdxList.get(i)).getTeamName() + ",";
            	} else {
            		Collections.sort(playerIdxList);
            		for(int i=0 ; i < playerIdxList.size(); i++)                   
                        strText += players.get(playerIdxList.get(i)).getFirstName() + ",";
            	}
                
                Toast.makeText(getApplicationContext(), "Item Clicked: "+ strText, Toast.LENGTH_SHORT).show();
            }
        });
        
        isTeamCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        		updateRosterCheckList();
        		if (isChecked) {
        			for (Integer t: teamIdxList) {
        				rosterCheckList.setItemChecked(t, true);
        			}
        		} else {
        			for (Integer p: playerIdxList) {
        				rosterCheckList.setItemChecked(p, true);
        			}
        		}
            }
        });
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void updateRosterCheckList() {
		if (isTeamCB.isChecked()) {
			rosterCheckList.setAdapter(new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_multiple_choice, teamNames));
		} else {
			rosterCheckList.setAdapter(new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_multiple_choice, playerNames));
		}
	}
	
	public void createNewSession(View view) {
		Context context = getApplicationContext();
		Session session= null;
    	String sessionName = null;
    	int sessionType = 0;
    	Date startDate;
    	Boolean isTeam;
    	Boolean isActive = true;
    	
    	List<SessionMember> sMembers = new ArrayList<SessionMember>();
    	
    	// get the session name
    	String st;
    	st = name.getText().toString().trim().toLowerCase(Locale.US);
    	if (!st.isEmpty()){
    		sessionName = st;
    	}
    	
    	// get the session type
    	switch (sessionTypeSpinner.getSelectedItemPosition()) {
    	case 0:
    		// is league
    		sessionType = SessionType.LEAGUE;
    		break;
    	case 1:
    		// is ladder
    		sessionType = SessionType.LADDER;
    		break;
    	case 2:
    		// is single elimination tourny
    		sessionType = SessionType.SNGL_ELIM;
    		break;
    	case 3:
    		// is double elimination tourny
    		sessionType = SessionType.DBL_ELIM;
    		break;
    	}
    	
    	// get the start date    	
    	startDate = new Date();
    	
    	// get isTeam
    	isTeam = isTeamCB.isChecked();
    	
    	// get isActive
    	isActive = isActiveCB.isChecked();
    	
    	// make the new session or modify an existing one
    	if (sId != -1) {
    		s.setSessionName(sessionName);
    		s.setIsActive(isActive);
    		
    		try {
				sDao.update(s);
				Toast.makeText(context, "Session modified.", Toast.LENGTH_SHORT).show();
				finish();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(PolishScorebook.class.getName(), "Could not modify session.", e);
				Toast.makeText(context, "Could not modify session.", Toast.LENGTH_SHORT).show();
			}
    	} else {
    		// create the session
    		session = new Session(sessionName, sessionType, startDate, isTeam);
    		    		
        	try{
        		Dao<Session, Long> dao = getHelper().getSessionDao();
    	   		dao.create(session);
    	   		Toast.makeText(context, "Session created!", Toast.LENGTH_SHORT).show();
    		   	}
    		 catch (SQLException e){
    			 Log.e(PolishScorebook.class.getName(), "Could not create session.", e);
    			 Toast.makeText(context, "Could not create session.", Toast.LENGTH_SHORT).show();
    		   	}
        	
        	// convert the indices from the roster list to actual players or teams
        	if (isTeam) {
        		List<Team> roster = new ArrayList<Team>();
        		for (Integer teamIdx: teamIdxList) {
        			roster.add(teams.get(teamIdx));
        		}
        		
        		roster = seedRoster(roster);
        		
        		int ii = 0;
        		for (Team t: roster) {
        			sMembers.add(new SessionMember(session, t, ii));
        			ii++;
        		}
        	} else {
        		List<Player> roster = new ArrayList<Player>();
        		for (Integer playerIdx: playerIdxList) {
        			roster.add(players.get(playerIdx));
        		}
        	}

        	// create the session members
    		
        	
        	try{
        		Dao<SessionMember, Long> smDao = getHelper().getSessionMemberDao();
        		for (SessionMember sm: sMembers) {
        			smDao.create(sm);
        		}
    	   		finish();
    		   	}
    		 catch (SQLException e){
    			 Log.e(PolishScorebook.class.getName(), "Could not create session member.", e);
    			 Toast.makeText(context, "Could not create session member.", Toast.LENGTH_SHORT).show();
    		   	}
    	}
    	
    }
	
	public List seedRoster(List roster) {
		// only random seeding so far...
    	Collections.shuffle(roster);
    	
		return roster;
	}

}
