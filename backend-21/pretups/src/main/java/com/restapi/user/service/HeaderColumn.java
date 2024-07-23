package com.restapi.user.service;

public class HeaderColumn {
	public HeaderColumn(String columnName, String displayName) {
		super();
		this.columnName = columnName;
		this.displayName = displayName;
	}
	
	public HeaderColumn()
	{
		
	}

	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	private String displayName;
}
