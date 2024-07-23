package com.btsl.pretups.channel.profile.businesslogic;

public enum OperationTypeEnum {
	
	UNBLOCK_PASSWORD (1),
	UNBLOCK_AND_SEND_PASSWORD (2),
	SEND_PASSWORD (3),
	RESET_PASSWORD (4);	
	int id;
	
	OperationTypeEnum(int id)
	{
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static OperationTypeEnum getOperation(int id)
	{
		for (OperationTypeEnum l : OperationTypeEnum.values()) {
	          if (l.getId() == id) 
	        	  return l;
		}
			return null;
	}
}
