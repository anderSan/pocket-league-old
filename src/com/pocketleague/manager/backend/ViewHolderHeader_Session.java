package com.pocketleague.manager.backend;

import java.util.ArrayList;

import com.pocketleague.manager.enums.SessionType;

public class ViewHolderHeader_Session {
	private String name;
	private SessionType session_type;
	private ArrayList<ViewHolder_Session> sessionList = new ArrayList<ViewHolder_Session>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SessionType getSessionType() {
		return session_type;
	}

	public void setSessionType(SessionType session_type) {
		this.session_type = session_type;
		this.name = session_type.toString();
	}

	public ArrayList<ViewHolder_Session> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<ViewHolder_Session> sessionList) {
		this.sessionList = sessionList;
	}
}
