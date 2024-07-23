package com.web.ota.services.businesslogic;

public interface ServicesWebQry {
	
	public String loadLatestMinorVersionQry();
	public String loadLatestMajorVersionQry();
	public String loadSmscDetailsQry();
	public String getSimProfileInfoQry();
	public String loadLatestSIMServiceListForSearchQry(String networkCode,boolean isall);
	public StringBuilder loadLangParametersQry();
}
