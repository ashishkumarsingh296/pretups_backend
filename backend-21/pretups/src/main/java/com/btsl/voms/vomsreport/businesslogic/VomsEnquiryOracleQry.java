package com.btsl.voms.vomsreport.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;

public class VomsEnquiryOracleQry implements VomsEnquiryQry {
	private Log _log = LogFactory.getLog(this.getClass().getName());
	private String className = "VomsEnquiryOracleQry";
	

	@Override
	public PreparedStatement getVoucherEnquiry_oldQry(Connection p_con,
			String p_voucherType, String p_serialNo) throws SQLException {
		PreparedStatement pstmt = null;
		final String methodName = className + "#getVoucherEnquiry_old";
		String tablename = null;
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
			boolean matchFound = BTSLUtil.validateTableName(p_voucherType);
			if (!matchFound) {
				try {
					throw new BTSLBaseException(this, methodName,
							"error.not.a.valid.voucher.type");
				} catch (BTSLBaseException e) {
					_log.errorTrace(methodName, e);
				}
			}
			tablename = "voms_" + p_voucherType + "_vouchers";
		} else {
			tablename = "voms_vouchers";
		}
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT DISTINCT  V.serial_no SERIALNO,nvl(V.generation_batch_no,'') GENBATCHNO, L.lookup_name STATUS,STSM.selector_name,  ");
		strBuff.append("nvl(V.enable_batch_no,'') ENBATHNO,nvl(V.sale_batch_no,'') SLBATCHNO,V.attempt_used ATTEMPTUSED,P.mrp MRP,");
		strBuff.append("nvl(to_char(V.expiry_date,'dd/mm/yy'),'') EXPIRYDATE,nvl(V.last_consumed_by,'') LASTCONSUMEDBY,to_char(V.consume_before,'dd/mm/yy') CONSUMEBEFORE,V.total_value_used TOTALVALUEUSED,");
		strBuff.append("nvl(to_char(V.last_consumed_on,'dd/mm/yy hh24:mi:ss'),'') LASTCONSUMEDON,");
		strBuff.append("nvl(V.modified_by,'') MODIFIEDBY,to_char(V.modified_on,'dd/mm/yy hh24:mi:ss') MODIFIEDON,nvl(V.LAST_CONSUMED_OPTION,'') LASTCONSUMEDOPTION ,");
		strBuff.append("nvl(PRODLOC.network_name,'') PRODLOCNAME,nvl(USERLOC.network_name,'') LASTUSERLOCNAME,");
		strBuff.append("nvl(P.product_name,'') PRODUCTNAME, P.talktime TALKTIME,P.validity VALIDITY,nvl(C.category_name,'') CATEGORYNAME, nvl(C.category_type,'') CATEGORYTYPE, nvl(vt.name,'') DOMAINNAME,");
		strBuff.append("nvl(to_char(V.created_on,'dd/mm/yy hh24:mi:ss'),'') GENERATEDON,nvl(to_char(BATCH.created_on,'dd/mm/yy hh24:mi:ss'),'') ENABLEDON,U.lookup_name PREVSTAT,V.last_attempt_no LASTATTEMPTNO,V.attempt_type ATTEMPTTYPE,");
		strBuff.append("nvl(V.last_consumed_by,'')FIRSTCONSUMEDBY,to_char(V.last_consumed_on,'dd/mm/yy hh24:mi:ss') FIRSTCONSUMEDON,V.one_time_usage ONETIMEUSAGE,V.No_of_requests TOTALNOOFREQUEST,V.Last_request_attempt_no LASTREQUESTATTEMPTNO,");
		strBuff.append("PC.attempt_allowed ATTEMPTALLOWED,PC.total_value_allowed TOTALVALUEALLOWED,C2ST.receiver_msisdn RECEIVERMSISDN,C2ST.sender_msisdn SENDERMSISDN ");
		strBuff.append("FROM "
				+ tablename
				+ " V,lookups L,lookups U,voms_batches BATCH,voms_products P,voms_product_consumption PC,voms_categories C,");
		strBuff.append("VOMS_VTYPE_SERVICE_MAPPING vvsm,voms_types vt, networks PRODLOC,networks USERLOC,c2s_transfers_old C2ST, service_type_selector_mapping STSM ");
		strBuff.append("WHERE V.current_status=L.lookup_code  AND V.previous_status=U.lookup_code(+) AND L.LOOKUP_TYPE=? AND V.serial_no=? ");
		strBuff.append("AND V.enable_batch_no=BATCH.batch_no(+) AND V.product_id=P.product_id ");
		strBuff.append("AND P.category_id=C.category_id AND vt.voucher_type=vvsm.voucher_type AND vvsm.voucher_type=C.voucher_type ");
		strBuff.append("AND C.voucher_type=vt.voucher_type AND c.service_id= vvsm.service_id ");
		strBuff.append("AND vvsm.sub_service=STSM.selector_code AND STSM.service_type=vvsm.service_type ");
		strBuff.append("AND STSM.service_type=? AND vvsm.service_type=?  AND STSM.status= 'Y' AND vt.status= 'Y'");
		strBuff.append(" AND V.production_network_code =PRODLOC.network_code AND V.user_network_code=USERLOC.network_code(+) ");
		strBuff.append("AND (V.product_id=PC.product_id(+) and v.production_network_code=PC.production_network_code(+)) AND V.last_transaction_id=C2ST.transfer_id(+)");
		String sqlVoucher = strBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug("getVoucherEnquiry", "Query1 :" + sqlVoucher);
		}
		pstmt = p_con.prepareStatement(sqlVoucher);
		pstmt.setString(1, VOMSI.LOOKUP_VOUCHER_STATUS);
		pstmt.setString(2, p_serialNo);
		pstmt.setString(3, PretupsI.SERVICE_TYPE_EVD);
		pstmt.setString(4, PretupsI.SERVICE_TYPE_EVD);
		return pstmt;

	}

	@Override
	public String getVoucherEnquiry_oldSelectQry() {
		StringBuilder strBuff2 = new StringBuilder();
		strBuff2.append("SELECT U.lookup_name STATUS,VU.TOTAL_VALUE_USED TOTALVALUEUSED,nvl(USERLOC.network_name,'') USERLOCNAME , ");
		strBuff2.append(" L.lookup_name PREVIOUS_STATUS,VU.VALIDITY,VU.PURPOSE_ID,VU.CONSUMED_OPTION,VU.ATTEMPT_NO,VU.ATTEMPT_TYPE,VU.PREVIOUS_BALANCE,VU.NEW_BALANCE,");
		strBuff2.append(" VU.CONSUMED_BY,to_char(CONSUMED_ON,'dd/mm/yy hh24:mi:ss')CONSUMED_ON,VU.REQUESTED_BY,VU.USAGE_STRING,VU.TALK_TIME,VU.GRACE_PERIOD,VU.VALUE_USED,VU.REQUEST_SOURCE, VU.REQUEST_PARTNER_ID ");
		strBuff2.append(" FROM voms_voucher_usage VU,networks USERLOC,lookups L,lookups U");
		strBuff2.append(" WHERE VU.PREVIOUS_STATUS=L.lookup_code AND VU.STATUS=U.lookup_code AND SERIAL_NO=? AND VU.user_network_code=USERLOC.network_code(+) order by VU.ATTEMPT_NO desc");

		return strBuff2.toString();

	}

	@Override
	public String getVoucherEnquiry_newQry(String p_voucherType) {
		//local_index_missing
		final String methodName = className + "#getVoucherEnquiry_new";
		String tablename = null;
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
			boolean matchFound = BTSLUtil.validateTableName(p_voucherType);
			if (!matchFound) {
				try {
					throw new BTSLBaseException(this, methodName,
							"error.not.a.valid.voucher.type");
				} catch (BTSLBaseException e) {
					_log.errorTrace(methodName, e);
				}
			}
			tablename = "voms_" + p_voucherType + "_vouchers";
		} else {
			tablename = "voms_vouchers";
		}
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT DISTINCT  V.serial_no SERIALNO,nvl(V.generation_batch_no,'') GENBATCHNO, L.lookup_name STATUS,STSM.selector_name, ");
		strBuff.append(" V.SOLD_DATE SOLD_DATE, V.SOLD_STATUS SOLD_STATUS, L.LOOKUP_CODE STATUS_CODE, ");//make changes in postgres query as well
		strBuff.append("nvl(V.enable_batch_no,'') ENBATHNO,nvl(V.sale_batch_no,'') SLBATCHNO,V.attempt_used ATTEMPTUSED,P.mrp MRP,");
		strBuff.append("nvl(to_char(V.expiry_date,'dd/mm/yy'),'') EXPIRYDATE,nvl(V.last_consumed_by,'') LASTCONSUMEDBY,to_char(V.consume_before,'dd/mm/yy') CONSUMEBEFORE,V.total_value_used TOTALVALUEUSED,");
		strBuff.append("V.LAST_CONSUMED_ON LASTCONSUMEDON,");
		strBuff.append("nvl(V.modified_by,'') MODIFIEDBY,V.modified_on MODIFIEDON,nvl(V.LAST_CONSUMED_OPTION,'') LASTCONSUMEDOPTION ,");
		strBuff.append("nvl(PRODLOC.network_name,'') PRODLOCNAME,nvl(USERLOC.network_name,'') LASTUSERLOCNAME,");
		strBuff.append("nvl(P.product_name,'') PRODUCTNAME, P.talktime TALKTIME,P.validity VALIDITY,nvl(C.category_name,'') CATEGORYNAME, nvl(C.category_type,'') CATEGORYTYPE, nvl(vt.name,'') DOMAINNAME,");
		strBuff.append(" V.created_on GENERATEDON, BATCH.created_on ENABLEDON,U.lookup_name PREVSTAT,V.last_attempt_no LASTATTEMPTNO,V.attempt_type ATTEMPTTYPE,");
		strBuff.append("nvl(V.last_consumed_by,'')FIRSTCONSUMEDBY,V.first_consumed_on FIRSTCONSUMEDON,V.one_time_usage ONETIMEUSAGE,V.No_of_requests TOTALNOOFREQUEST,V.Last_request_attempt_no LASTREQUESTATTEMPTNO,");
		strBuff.append("PC.attempt_allowed ATTEMPTALLOWED,PC.total_value_allowed TOTALVALUEALLOWED,V.SUBSCRIBER_ID,C2ST.sender_msisdn SENDERMSISDN ");
		strBuff.append(",nvl(V.voucher_segment,'') VOUCHERSEGMENT,V.voucher_type VOUCHERTYPE ");
		strBuff.append("FROM "
				+ tablename
				+ " V,lookups L,lookups U,voms_batches BATCH,voms_products P,voms_product_consumption PC,voms_categories C,");
		strBuff.append("VOMS_VTYPE_SERVICE_MAPPING vvsm,voms_types vt, networks PRODLOC,networks USERLOC, service_type_selector_mapping STSM ,c2s_transfers C2ST ");
		strBuff.append("WHERE V.current_status=L.lookup_code  AND V.previous_status=U.lookup_code(+) AND L.LOOKUP_TYPE=? AND V.serial_no=? AND V.last_transaction_id=C2ST.transfer_id(+) ");

		if (!BTSLUtil.isNullString(p_voucherType)) {
			strBuff.append("and vt.VOUCHER_TYPE=?");
		}
		strBuff.append("AND V.user_network_code=? ");
		strBuff.append("AND V.enable_batch_no=BATCH.batch_no(+) AND V.product_id=P.product_id ");
		strBuff.append("AND P.category_id=C.category_id AND vt.voucher_type=vvsm.voucher_type AND vvsm.voucher_type=C.voucher_type ");
		strBuff.append("AND C.voucher_type=vt.voucher_type AND c.service_id= vvsm.service_id ");
		strBuff.append("AND vvsm.sub_service=STSM.selector_code AND STSM.service_type=vvsm.service_type ");
		strBuff.append(" AND V.production_network_code =PRODLOC.network_code AND V.user_network_code=USERLOC.network_code(+) ");
		strBuff.append("AND (V.product_id=PC.product_id(+) and v.production_network_code=PC.production_network_code(+)) ");

		return strBuff.toString();

	}

	@Override
	public String getVoucherEnquiry_newSelectQry() {
		StringBuilder strBuff2 = new StringBuilder();
		strBuff2.append("SELECT U.lookup_name STATUS,VU.TOTAL_VALUE_USED TOTALVALUEUSED,nvl(USERLOC.network_name,'') USERLOCNAME , ");
		strBuff2.append(" L.lookup_name PREVIOUS_STATUS,VU.VALIDITY,VU.PURPOSE_ID,VU.CONSUMED_OPTION,VU.ATTEMPT_NO,VU.ATTEMPT_TYPE,VU.PREVIOUS_BALANCE,VU.NEW_BALANCE,");
		strBuff2.append(" VU.CONSUMED_BY,to_char(CONSUMED_ON,'dd/mm/yy hh24:mi:ss')CONSUMED_ON,VU.REQUESTED_BY,VU.USAGE_STRING,VU.TALK_TIME,VU.GRACE_PERIOD,VU.VALUE_USED,VU.REQUEST_SOURCE, VU.REQUEST_PARTNER_ID ");
		strBuff2.append(" FROM voms_voucher_usage VU,networks USERLOC,lookups L,lookups U");
		strBuff2.append(" WHERE VU.PREVIOUS_STATUS=L.lookup_code AND VU.STATUS=U.lookup_code AND SERIAL_NO=? AND VU.user_network_code=USERLOC.network_code(+) order by VU.ATTEMPT_NO desc");
		return strBuff2.toString();

	}

	@Override
	public String loadReconcillationReportListQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT V.serial_no,P.mrp,P.product_name,V.last_consumed_by, VVSM.service_id,VVSM.voucher_type,");
		strBuff.append(" V.last_consumed_on,V.last_transaction_id,PRODLOC.network_name PRODUCTION_LOCATION,USERLOC.network_name USER_LOCATION,CAT.VOUCHER_SEGMENT");
		strBuff.append(" FROM voms_vouchers V,voms_products P,networks PRODLOC, Voms_Categories CAT ,voms_vtype_service_mapping VVSM,networks USERLOC ");
		strBuff.append(" WHERE V.current_status='RC' AND V.last_consumed_on BETWEEN ? AND ?");
		strBuff.append(" AND V.production_network_code=DECODE(?,'ALL',V.production_network_code,?)");
		strBuff.append(" AND V.production_network_code=PRODLOC.network_code");
		strBuff.append(" AND V.user_network_code=USERLOC.network_code");
		strBuff.append(" AND V.product_id=P.product_id ");
		strBuff.append(" AND CAT.category_id=P.category_id ");
		strBuff.append(" AND VVSM.voucher_type= ? ");
		strBuff.append(" AND VVSM.service_id=CAT.service_id ");
		strBuff.append(" AND V.product_id=DECODE(?,'ALL',V.product_id,?) AND V.product_id=P.product_id AND P.category_id=DECODE(?,'ALL',P.category_id,?)");
		strBuff.append(" ORDER BY V.serial_no");
		return strBuff.toString();

	}

	@Override
	public String loadUserCategoryList() {
		StringBuilder strBuff = new StringBuilder( " SELECT   vt.voucher_type, vt.NAME, vt.service_type_mapping , vt.status ");
        strBuff.append(" FROM voms_types vt, user_vouchertypes uv ");
        strBuff.append(" WHERE  vt.status = 'Y' AND uv.user_id = ? AND uv.voucher_type = vt.voucher_type AND uv.status = 'Y' GROUP BY vt.voucher_type, vt.NAME, vt.status ,vt.service_type_mapping ");
        return strBuff.toString();
	}
	@Override
	public String loadMRPofVomsProducts(){
		StringBuilder strBuff = new StringBuilder(" select listagg( mrp,', ') within group(order by mrp) mrp from ");
	    strBuff.append(" (select distinct vp.mrp from  ");
	    strBuff.append(" voms_products VP , voms_categories VC ,Voms_types VT  where VP.CATEGORY_ID=VC.CATEGORY_ID  AND VT.VOUCHER_TYPE=VC.VOUCHER_TYPE AND VC.VOUCHER_TYPE=  ? )");
	    return strBuff.toString();		    
	}

}
