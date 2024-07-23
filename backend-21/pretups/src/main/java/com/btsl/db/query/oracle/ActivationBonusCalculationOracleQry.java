package com.btsl.db.query.oracle;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.ActivationBonusCalculationQry;
import com.btsl.util.Constants;

public class ActivationBonusCalculationOracleQry implements ActivationBonusCalculationQry {

	@Override
	public String createTempTableQry() {
		  final StringBuilder qryBuffer = new StringBuilder(" CREATE TABLE C2S_TRANSFERS_TEMP ");
	      qryBuffer.append(" TRANSFER_ID VARCHAR2(20), TRANSFER_DATE DATE, TRANSFER_DATE_TIME DATE, NETWORK_CODE VARCHAR2(2), ");
	      qryBuffer.append(" PRODUCT_CODE VARCHAR2(10), RECEIVER_MSISDN VARCHAR2(15), TRANSFER_VALUE NUMBER(20,0), SERVICE_TYPE VARCHAR2(10), ");
	      qryBuffer.append(" RECONCILIATION_DATE DATE, PROCESSED VARCHAR2(1) DEFAULT 'N', TXN_CALCULATION_DONE VARCHAR2(1) DEFAULT 'N' ");
	      return qryBuffer.toString();
	}

	@Override
	public String dropTempTableQry() {
		  final StringBuilder qryBuffer = new StringBuilder();
          qryBuffer.append(" DROP table C2S_TRANSFERS_TEMP CASCADE CONSTRAINTS ");
          return qryBuffer.toString();
	}

	@Override
	public String selectFromC2STransferTemp() {
		 StringBuilder qryBuffer = new StringBuilder();
         qryBuffer.append(" SELECT transfer_id,transfer_date,transfer_date_time,network_code, ");
         qryBuffer.append(" product_code,receiver_msisdn,transfer_value,service_type, ");
         qryBuffer.append(" reconciliation_date,processed,txn_calculation_done FROM ");
         qryBuffer.append(" C2S_TRANSFERS_TEMP WHERE rownum<=? AND processed='N' ");
		return qryBuffer.toString();
	}

	@Override
	public String selectFromBonusQry() {
		StringBuilder  qryBuffer = new StringBuilder();
           qryBuffer.append(" SELECT profile_type,user_id_or_msisdn,points, ");
           qryBuffer.append(" bucket_code,product_code,points_date,last_redemption_id,last_redemption_on, ");
           qryBuffer.append(" last_allocation_type,last_allocated_on,created_on,created_by,modified_on, ");
           qryBuffer.append(" modified_by,transfer_id FROM BONUS WHERE user_id_or_msisdn=? AND profile_type='ACT' ");
           // DB220120123for update WITH RS
           if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
               qryBuffer.append(" AND product_code=? AND points_date=? AND bucket_code='1' FOR UPDATE OF points WITH RS");
           } else {
               qryBuffer.append(" AND product_code=? AND points_date=? AND bucket_code='1' FOR UPDATE OF points ");
           }
		return qryBuffer.toString();
	}

}
