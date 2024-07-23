package com.btsl.db.query.postgres;

import com.btsl.pretups.p2p.subscriber.processes.ScheduledMultipleCreditTransferProcessQry;

public class ScheduledMultipleCreditTransferProcessPostgresQry implements ScheduledMultipleCreditTransferProcessQry{
	@Override
	public String scheduleRecordsSelectFromBarredMsisdn() {
		 final StringBuffer selBarrd = new StringBuffer("SELECT 1 FROM barred_msisdns WHERE ");
         selBarrd.append("module=? AND network_code=? ");
         selBarrd.append("AND msisdn=? AND user_type=?  limit 1  ");// OR
         // user_type='BOTH'
		return selBarrd.toString();
	}

}
