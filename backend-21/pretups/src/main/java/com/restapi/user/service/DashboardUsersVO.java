package com.restapi.user.service;

import java.util.ArrayList;
import java.util.Date;

public class DashboardUsersVO {

	public int id;
	public String name;
	public String login;
	public String email;
	public String avatarUrl;
	public boolean isAdmin;
	public boolean isDisabled;
	public Date lastSeenAt;
	public String lastSeenAtAge;
	public ArrayList<String> authLabels;
}