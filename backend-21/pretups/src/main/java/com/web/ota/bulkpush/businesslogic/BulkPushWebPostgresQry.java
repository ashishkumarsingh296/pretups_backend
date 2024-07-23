package com.web.ota.bulkpush.businesslogic;

public class BulkPushWebPostgresQry implements BulkPushWebQry {

	@Override
	public String getMobileNosInJobQry(String m_batchIdList[]) {
		StringBuilder sqlLoadBuf = new StringBuilder();
		sqlLoadBuf
				.append("SELECT ota.MSISDN MSISDN, TRANSACTION_ID, pk.DECRYPT_KEY AS KEY, MESSAGE ,OPERATION ");
		sqlLoadBuf.append(" FROM ota_job_database ota,pos_keys pk  ");
		sqlLoadBuf.append(" WHERE job_id=? AND batch_id IN (");
		for (int i = 0; i < m_batchIdList.length; i++) {
			sqlLoadBuf.append(" ?");
			if (i != m_batchIdList.length - 1) {
				sqlLoadBuf.append(",");
			}
		}
		sqlLoadBuf.append(")");
		sqlLoadBuf
				.append(" AND ota.MSISDN=pk.MSISDN AND (ota.status<>'SENT' OR ota.status is null) ");
		return sqlLoadBuf.toString();

	}

}
