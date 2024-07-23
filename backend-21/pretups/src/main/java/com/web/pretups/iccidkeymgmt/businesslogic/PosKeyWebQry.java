package com.web.pretups.iccidkeymgmt.businesslogic;

public interface PosKeyWebQry {
	
	public String isIccExistQry();
	public String isNewIccExistQry();
	public String loadPosKeyDetailsForICCIDAndMsisdnQry( boolean isICCID, boolean isHistory);

}
