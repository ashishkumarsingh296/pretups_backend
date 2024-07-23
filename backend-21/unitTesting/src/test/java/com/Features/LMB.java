package com.Features;

import com.sshmanager.SSHService;
import com.utils.Log;

public class LMB {
		
	public void OfflineSettlementScript()
	{
		Log.info("Trying to execute Voucher Burn Rate Script");
		SSHService.executeScript("settlement.sh");
		Log.info("Voucher Burn Rate Script executed successfully");
	}

}
