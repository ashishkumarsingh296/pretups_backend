package com.txn.pretups.p2p.subscriber.businesslogic;

public class SubscriberTxnOracleQry implements SubscriberTxnQry {
	@Override
	public String loadBuddyDetailsByScheduleTypeQry(){
		StringBuilder selectQueryBuff = new StringBuilder();

        selectQueryBuff.append("SELECT batch.batch_id, batch.parent_id, batch.list_name, batch.status batch_status,");
        selectQueryBuff.append(" batch.schedule_type, batch.no_of_schedule,");
        selectQueryBuff.append(" batch.sender_service_class, batch.batch_total_record,");
        selectQueryBuff.append(" batch.created_on, batch.modified_on, batch.execution_count,");
        selectQueryBuff.append(" batch.created_by, batch.modified_by,");
        // added by harsh for scheduling credit transfer list
        selectQueryBuff.append(" batch.schedule_date ,");

        selectQueryBuff.append(" p2psub.msisdn, p2psub.LANGUAGE,p2psub.pin,");
        selectQueryBuff.append(" p2psub.country,p2psub.network_code,pb.buddy_msisdn, pb.status buddy_status, pb.preferred_amount,");
        selectQueryBuff.append(" pb.successive_failure_count, pb.selector_code,");
        // entries fetch for writing into relevant logs by harsh
        selectQueryBuff.append(" pb.BUDDY_LAST_TRANSFER_ID, pb.BUDDY_LAST_TRANSFER_ON,");
        selectQueryBuff.append(" pb.BUDDY_LAST_TRANSFER_TYPE, pb.LAST_TRANSFER_AMOUNT");
        // end added by harsh
        selectQueryBuff.append(" FROM p2p_buddies pb, p2p_batches batch, p2p_subscribers p2psub");
        // added by harsh for scheduling credit transfer list
        selectQueryBuff.append(" WHERE trunc(batch.schedule_date) <= trunc(sysdate)");
        selectQueryBuff.append(" AND p2psub.status in ('Y','S')");
        selectQueryBuff.append(" AND batch.schedule_type = ?");
        selectQueryBuff.append(" AND batch.status = 'Y'");
        selectQueryBuff.append(" AND batch.parent_id = p2psub.user_id");
        selectQueryBuff.append(" AND pb.parent_id = batch.parent_id");
        selectQueryBuff.append(" AND pb.list_name = batch.list_name");
        selectQueryBuff.append(" AND batch.execution_count < batch.no_of_schedule");
        selectQueryBuff.append(" ORDER BY batch.batch_id");
		return selectQueryBuff.toString();
	}

}
