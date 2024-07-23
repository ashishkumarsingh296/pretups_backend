package com.btsl.db.query.oracle;

import com.btsl.pretups.p2p.subscriber.processes.ScheduledMultipleCreditTransferProcessQry;

public class ScheduledMultipleCreditTransferProcessOracleQry implements ScheduledMultipleCreditTransferProcessQry{

	@Override
	public String scheduleRecordsSelectFromBarredMsisdn() {
		 final StringBuffer selBarrd = new StringBuffer("SELECT 1 FROM barred_msisdns WHERE ");
         selBarrd.append("module=? AND network_code=? ");
         selBarrd.append("AND msisdn=? AND user_type=? ");// OR
         // user_type='BOTH'
         selBarrd.append("AND rownum =1 ");
		return selBarrd.toString();
	}

}
