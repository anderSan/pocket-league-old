package com.ultimatepolish.polishscorebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.ultimatepolish.scorebookdb.Bracket;
import com.ultimatepolish.scorebookdb.Player;
import com.ultimatepolish.scorebookdb.Session;
import com.ultimatepolish.scorebookdb.SessionMember;
import com.ultimatepolish.scorebookdb.SessionType;

public class Detail_Session extends MenuContainerActivity {
	Long sId;
	Session s;
	Dao<Session, Long> sDao;
	Bracket bracket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		sId = intent.getLongExtra("SID", -1);
		
		if (sId != -1){
			try{
				sDao = Session.getDao(getApplicationContext());
				
				s = sDao.queryForId(sId);
		    }
			catch (SQLException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		
		if (s.sessionType == SessionType.SNGL_ELIM) {
			setContentView(R.layout.activity_detail_session_singleelim);
			ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);
			
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			bracket = new Bracket(sv, s, false, metrics.widthPixels);
			sv.addView(bracket.rl);
		} else {
			setContentView(R.layout.activity_detail_session);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.modifyButton).setVisible(true);
		return true;
	}
	
	@Override
	public void openModifyActivity() {
		Intent intent = new Intent(getApplicationContext(), NewSession.class);
        intent.putExtra("SID", sId);
        startActivity(intent);
    }
	
	@Override
    protected void onRestart(){
    	super.onRestart();
    }
	
    @Override
    protected void onResume(){
    	super.onResume();
    	refreshDetails();
    	if (s.sessionType == SessionType.SNGL_ELIM) {
    		bracket.refreshSingleElimBracket();
		}
    }

    @Override
	protected void onPause() {
		super.onPause();
		
		// TODO: move this to bracket.java
//		try {
//			for (SessionMember sm: sMembers) {
//				smDao.update(sm);
//			}
//		}
//		catch (SQLException e) {
//			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//		}
	}
    
	public void refreshDetails(){
		TextView sName = (TextView) findViewById(R.id.sDet_name);
		sName.setText(s.getSessionName());
		
		TextView sId = (TextView) findViewById(R.id.sDet_id);
		sId.setText(String.valueOf(s.getId()));
		
		TextView sType = (TextView) findViewById(R.id.sDet_type);
		sType.setText(SessionType.typeString[s.getSessionType()]);
		
		TextView sStartDate = (TextView) findViewById(R.id.sDet_startDate);
		sStartDate.setText("Start date: " + String.valueOf(s.getStartDate()));
		
		TextView sEndDate = (TextView) findViewById(R.id.sDet_endDate);
		sEndDate.setText("End date: " + String.valueOf(s.getEndDate()));
		
		TextView sIsTeam = (TextView) findViewById(R.id.sDet_isTeam);
		if (s.getIsTeam()) {
			sIsTeam.setText("Doubles session");
		} else {
			sIsTeam.setText("Singles session");
		}
		
		TextView sIsActive = (TextView) findViewById(R.id.sDet_isActive);
		if (s.getIsActive()) {
			sIsActive.setText("This session is active");
		} else {
			sIsActive.setText("This session is no longer active");
		}	
	}	
}
