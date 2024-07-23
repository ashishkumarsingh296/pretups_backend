package com.btsl.pretups.processes;

/**
 * PinPasswordAlertQry
 * @author sadhan.k
 *
 */
public interface PinPasswordAlertQry {
	
	public String p2pPinAlert(int p_alertDays,int p2pChangePinDays);
	
	public String c2sPinAlert();
	
	public String channelPasswordAlert();
	
	public String operatorPasswordAlert();

}
