package com.ultimatepolish.scorebookdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import android.content.Context;

public class ActiveGame {

	private ArrayList<Throw> tArray;
	private int activeIdx;
	private Game g;
	private Context context;
	
	private  Dao<Game, Long> gDao;
	private Dao<Throw, Long> tDao;
	
	
	public ActiveGame(Game g, Context context) {
		super();
		this.g = g;
		
		gDao = Game.getDao(context);
		tDao = Throw.getDao(context);
		
		try{
			tArray = g.getThrowList(context);
		}
		catch (SQLException e){
			throw new RuntimeException("couldn't get throws for game "+g.getId()+": ", e);
		}
		
		activeIdx = 0;
		if (tArray.size()>0){
			activeIdx = tArray.size()-1;
		}
		updateScoresFrom(0);
	}
	
	private Throw makeNextThrow(){
		Throw t = g.makeNewThrow(nThrows());
		return t;
	}
	
	private void updateScoresFrom(int idx){
		Throw t,u;
		for (int i=idx; i<nThrows();i++){
			t = getThrow(i);
			if (i == 0) {
				t.setInitialScores();
				t.setOffenseFireCount(0);
				t.setDefenseFireCount(0);
			} else {
				u = getPreviousThrow(t);
				t.setInitialScores(u);
				t.setFireCounts(u);
			}
		}
		updateGameScore();
	}
	private void updateGameScore(){
		int[] scores = {0,0};
		if (nThrows()>0){
			Throw lastThrow = getThrow(nThrows()-1);
			if (Throw.isP1Throw(lastThrow)){
				scores = lastThrow.getFinalScores();
			}
			else{
				int[] tmp = lastThrow.getFinalScores();
				scores[1] = tmp[0];
				scores[0] = tmp[1];
			}
		}
		g.setFirstPlayerScore(scores[0]);
		g.setSecondPlayerScore(scores[1]);
	}
	
	private void saveThrow(Throw t){
	    HashMap<String,Object> m = t.getQueryMap();
	    List<Throw> tList = new ArrayList<Throw>();
		try{
			 tList= tDao.queryForFieldValuesArgs(m);
		}
		catch (SQLException e){
			throw new RuntimeException("could not query for throw "+t.getThrowIdx()+", game " + t.getGame().getId());
		}
		try{
			if (tList.isEmpty()){
				assert g.isValidThrow(t): "invalid throw for index "+t.getThrowIdx()+", not saving";
				tDao.create(t);
			}
			else{
				assert g.isValidThrow(t): "invalid throw for index "+t.getThrowIdx()+", not updating";
				t.setId(tList.get(0).getId());
				tDao.update(t);
			}
		}
		catch (SQLException e){
			throw new RuntimeException("could not create/update throw "+t.getThrowIdx()+", game "+t.getGame().getId());
		}
	}
	
	public void saveAllThrows(){
		updateScoresFrom(0);
		for(Throw t: tArray){
			saveThrow(t);
		}
	}
	
	public void saveGame(){
		try{
			gDao.update(g);
		}
		catch(SQLException e){
			throw new RuntimeException("could not save game "+g.getId());
		}
	}
	
	public Throw getActiveThrow(){
		return getThrow(activeIdx);
	}
	public void updateActiveThrow(Throw t){
		setThrow(activeIdx, t);
	}
	
	public void setThrow(int idx, Throw t){
		if (idx<0){
			throw new RuntimeException("must have positive throw index, not: "+idx);
		}
		else if (idx>=0 && idx<nThrows()){
			t.setThrowIdx(idx);
			assert g.isValidThrow(t): "invalid throw for index "+idx;
			t = tArray.set(idx,t);
			saveThrow(t);
		}
		else if (idx==nThrows()){
			t.setThrowIdx(idx);
			assert g.isValidThrow(t): "invalid throw for index "+idx;
			tArray.add(t);
			saveThrow(t);
		}
		else if (idx>nThrows()){
			throw new RuntimeException("cannot set throw "+idx+" in the far future");
		}
		updateScoresFrom(idx);
	}
	
	public Throw getThrow(int idx){
		Throw t = null;
		if (idx<0){
			throw new RuntimeException("must have positive throw index, not: "+idx);
		}
		else if (idx>=0 && idx<nThrows()){
			t = tArray.get(idx);
		}
		else if (idx==nThrows()){
			t = makeNextThrow();
			if (idx == 0) {
				t.setInitialScores();
			} else {
				Throw u = getPreviousThrow(t);
				t.setInitialScores(u);
			}
			
			tArray.add(t);
		}
		else if (idx>nThrows()){
			throw new RuntimeException("cannot get throw "+idx+" from the far future");
		}
		if (t==null){
			throw new NullPointerException("Got invalid throw for index "+idx);
		}
		return t;
	}
	public Throw getPreviousThrow(Throw t){
		Throw u = null;
		int idx = t.getThrowIdx();
		if (idx<=0){
			u.setInitialScores();
		}
		else if (idx>0 && idx<=nThrows()){
			u = tArray.get(idx-1);
		}
		else if (idx>nThrows()){
			throw new RuntimeException("cannot get predecessor of throw "+idx+" from the far future");
		}
		if (u==null){
			throw new NullPointerException("Got invalid predecessor for throw index "+idx);
		}
		return u;
	}
	

	
	public int nThrows(){
		return tArray.size();
	}
	public ArrayList<Throw> getThrows() {
		return tArray;
	}

	public int getActiveIdx() {
		return activeIdx;
	}

	public void setActiveIdx(int activeIdx) {
		this.activeIdx = activeIdx;
	}


	public Game getGame() {
		return g;
	}


	public void setGame(Game g) {
		this.g = g;
	}


	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
}
