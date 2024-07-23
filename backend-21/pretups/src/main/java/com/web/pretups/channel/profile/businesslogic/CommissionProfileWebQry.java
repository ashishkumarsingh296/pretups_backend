package com.web.pretups.channel.profile.businesslogic;


public interface CommissionProfileWebQry {
	
	public String loadCommissionProfileListQry();
	public String loadCommissionProfileSetVersionQry();
	public String loadCommissionProfileSetVersionQry1(String geoCode, String gradeCode ,String status);

	public String loadCommissionProfileSetVersionQryTwo();
	public String loadCommissionProfileSetVersionQryForViewDetail();

}
