package com.restapi.user.service;

import java.util.ArrayList;



public class DashboardPermissionVO {

	private String id;
    
	public ArrayList<Item> items;

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


}
