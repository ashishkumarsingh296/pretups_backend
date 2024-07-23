package com.btsl.pretups.channel.profile.businesslogic;

public enum CategoryTypeEnum {

	SE(1),
	RET(2),
	AG(3);

	int id;
	
	CategoryTypeEnum(int id)
	{
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static CategoryTypeEnum getOperation(int id)
	{
		for (CategoryTypeEnum l : CategoryTypeEnum.values()) {
	          if (l.getId() == id) 
	        	  return l;
		}
			return null;
	}
}
