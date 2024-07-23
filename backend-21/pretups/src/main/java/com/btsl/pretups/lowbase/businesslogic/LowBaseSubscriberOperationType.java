package com.btsl.pretups.lowbase.businesslogic;

public enum LowBaseSubscriberOperationType {
	DELETE("D"),
	UPDATE("U");
	
	private final String type;
	
	LowBaseSubscriberOperationType (String type){
		this.type=type;
	}
	
	
	public static LowBaseSubscriberOperationType fromType(String type){
		
		for(LowBaseSubscriberOperationType s : values()){
			if(s.type.equals(type))
			{
				return s;
			}
		}
		
		throw new IllegalArgumentException("Invalid Operation type for Low Base Subscriber");
		
	}
}
