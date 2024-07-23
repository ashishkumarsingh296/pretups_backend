package com.btsl.pretups.processes.businesslogic;

public class ProcessStatusPostgresQry implements ProcessStatusQry{
	@Override
	public String loadProcessDetailNetworkWiseWithWaitQry(){
	String sqlSelect ="SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval,network_code,record_count FROM process_status WHERE process_id=? and network_code=? for update ";
	return sqlSelect;
	}
}
