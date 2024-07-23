package com.btsl.pretups.processes.businesslogic;

import com.btsl.util.Constants;

public class ProcessStatusOracleQry implements ProcessStatusQry{
	@Override
	public String loadProcessDetailNetworkWiseWithWaitQry(){
	String sqlSelect ="SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval,network_code,record_count FROM process_status WHERE process_id=? and network_code=? for update WAIT "+Constants.getProperty("CARDGROUP_SYSTEM_TIMEOUT");
	return sqlSelect;
	}
}
