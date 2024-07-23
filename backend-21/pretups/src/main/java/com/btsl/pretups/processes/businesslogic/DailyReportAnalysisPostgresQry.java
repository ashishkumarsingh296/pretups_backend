package com.btsl.pretups.processes.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchInputVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

/**
 * @author satakshi.gaur
 *
 */
public class DailyReportAnalysisPostgresQry implements DailyReportAnalysisQry {
	private Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public StringBuilder loadC2SFailRechargeQry() {
		//local_index_implemented
		StringBuilder qrySelect = new StringBuilder("SELECT CT.network_code, N.network_name, ST.name service_type_name,CT.service_type, (COALESCE(KV.value ,CT.error_code)||'('||CT.error_code||')') error_code, P.product_name,CT.product_code, ");
	    qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) month_count, ");
	    qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) month_amount, ");
	    qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) day_count, ");
	    qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) day_amount ");
	    qrySelect.append("FROM c2s_transfers CT left outer join key_values KV on  (CT.error_code=KV.key and KV.type=?), service_type ST, products P, networks N ");
	    qrySelect.append("WHERE CT.transfer_date>=?::timestamp AND CT.transfer_date<=?::timestamp AND CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
	    qrySelect.append("AND CT.product_code=P.product_code ");
	    qrySelect.append("AND  CT.service_type =? ");
	    qrySelect.append("AND CT.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ? END ");
	    qrySelect.append("AND CT.transfer_status=? ");
	    qrySelect.append("GROUP BY  CT.network_code, N.network_name, ST.name, CT.service_type, CT.error_code, KV.value, CT.product_code,P.product_name ");
	    return qrySelect;
	}

	@Override
	public PreparedStatement loadC2SRecevierRequestQry(Connection con, Date fromDatePassed, Date toDatePassed, String networkCode, String service) {
		StringBuilder qrySelect = new StringBuilder("SELECT CR.network_code, N.network_name, ST.name service_type_name,CR.service_type, (COALESCE(KV.value ,CR.message_code)||'('||CR.message_code||')') error_code, ");
        qrySelect.append("SUM(CASE TO_CHAR(CR.created_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) month_count, ");
        qrySelect.append("SUM(CASE TO_CHAR(CR.created_date,'dd/mm/yy') WHEN TO_CHAR(?,'dd/mm/yy') THEN 1 ELSE 0 END ) day_count ");
        qrySelect.append("FROM c2s_receiver_requests CR left outer join key_values KV on CR.message_code=KV.key and KV.type=?, service_type ST, networks N ");
        qrySelect.append("WHERE CR.service_type=ST.service_type AND CR.service_type=? ");
        qrySelect.append("AND CR.network_code=N.network_code  AND CR.message_code<>? ");
        qrySelect.append("AND CR.network_code = CASE ? WHEN '" + PretupsI.ALL + "' THEN  CR.network_code ELSE ? END ");
        qrySelect.append("AND date_trunc('day',CR.created_date::TIMESTAMP)>=? AND date_trunc('day',CR.created_date::TIMESTAMP)<=? ");
        qrySelect.append("GROUP BY CR.network_code, N.network_name, ST.name, CR.service_type,KV.value ,CR.message_code ");
        if (log.isDebugEnabled()) {
            log.debug("DailyReportAnalysisPostgresQry : loadC2SRecevierRequestQry", "Select qrySelect:" + qrySelect);
        }
        PreparedStatement prepSelect = null;
        try {
		prepSelect = con.prepareStatement(qrySelect.toString());
		if (log.isDebugEnabled()) {
            log.debug("DailyReportAnalysisPostgresQry : loadC2SRecevierRequestQry", "Select query :" + prepSelect);
		}
        int i = 1;
        java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(fromDatePassed);
        java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(toDatePassed);
        prepSelect.setDate(i, toDate);
        i++;
        prepSelect.setDate(i, toDate);
        i++;
        prepSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
        i++;
        prepSelect.setString(i, service);
        i++;
        prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        i++;
        prepSelect.setString(i, networkCode);
        i++;
        prepSelect.setString(i, networkCode);
        i++;
        prepSelect.setDate(i, fromDate);
        i++;
        prepSelect.setDate(i, toDate);
        } catch (SQLException e) {
			log.errorTrace("loadC2SRecevierRequestQry", e);
		}
        return prepSelect;
	}

	@Override
	public StringBuilder loadTotalC2SRecevierRequestQry() {
		StringBuilder qrySelect = new StringBuilder(" SELECT CR.network_code, N.network_name,CR.service_type,ST.name service_type_name, SUM(CASE TO_CHAR(CR.created_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) total_month_count, ");
        qrySelect.append("SUM(CASE TO_CHAR(CR.created_date,'dd/mm/yy') WHEN TO_CHAR(?,'dd/mm/yy') THEN 1 ELSE 0 END ) total_day_count, ");
        qrySelect.append("SUM(CASE CR.message_code WHEN ? THEN (CASE TO_CHAR(CR.created_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) success_month_count, ");
        qrySelect.append("SUM(CASE CR.message_code WHEN ? THEN (CASE TO_CHAR(CR.created_date,'dd/mm/yy') WHEN TO_CHAR(?,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END)success_day_count, ");
        qrySelect.append("SUM(CASE  WHEN (CR.message_code!=? AND CR.message_code IS NOT NULL ) THEN (CASE TO_CHAR(CR.created_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) fail_month_count, ");
        qrySelect.append("SUM(CASE  WHEN (CR.message_code!=? AND CR.message_code IS NOT NULL ) THEN (CASE TO_CHAR(CR.created_date,'dd/mm/yy') WHEN TO_CHAR(?,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END)fail_day_count ");
        qrySelect.append("FROM c2s_receiver_requests CR, networks N, service_type ST  WHERE CR.service_type=ST.service_type AND CR.network_code=N.network_code AND CR.service_type=? ");
        qrySelect.append("AND CR.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN CR.network_code ELSE ? END ");
        qrySelect.append("AND date_trunc('day',CR.created_date::TIMESTAMP)>=? AND date_trunc('day',CR.created_date::TIMESTAMP)<=? GROUP BY CR.network_code, N.network_name,CR.service_type,ST.name ");
		return qrySelect;
	}

	@Override
	public StringBuilder loadP2PFailRechargeQry() {
		StringBuilder qrySelect = new StringBuilder("SELECT CT.network_code, N.network_name ,ST.name service_type_name,CT.service_type, (COALESCE(KV.value ,CT.error_code)||'('||CT.error_code||')') error_code, P.product_name, CT.product_code, ");
        qrySelect.append(" SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) month_count, ");
        qrySelect.append(" TO_CHAR((SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END)/" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() + "),'99999999999999999999999999999999999') month_amount, ");
        qrySelect.append(" SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) day_count, ");
        qrySelect.append(" TO_CHAR((SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END)/" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() + "),'99999999999999999999999999999999999') day_amount ");
        qrySelect.append(" FROM subscriber_transfers CT left outer join key_values KV on (CT.error_code=KV.key and KV.type=?) , service_type ST, products P, networks N ");
        qrySelect.append(" WHERE  CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
        qrySelect.append(" AND CT.product_code=P.product_code  ");
        qrySelect.append(" AND CT.service_type=? ");
        qrySelect.append(" AND CT.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ? END ");
        qrySelect.append(" AND CT.transfer_status=? AND CT.transfer_date>=?::timestamp AND CT.transfer_date<=?::timestamp ");
        qrySelect.append(" GROUP BY CT.network_code, N.network_name, CT.service_type, ST.name, CT.error_code, KV.value, P.product_name, CT.product_code ");
		return qrySelect;
	}

	@Override
	public PreparedStatement loadP2PRecevierRequestQry(Connection con,
			Date fromDatePassed, Date toDatePassed, String networkCode,
			String service) {
		PreparedStatement prepSelect = null;
		StringBuilder qrySelect = new StringBuilder(" SELECT CR.network_code, N.network_name, ST.name service_type_name,CR.service_type, (COALESCE(KV.value ,CR.message_code)||'('||CR.message_code||')') error_code, ");
        qrySelect.append(" SUM(CASE TO_CHAR(CR.created_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) month_count,  ");
        qrySelect.append(" SUM(CASE TO_CHAR(CR.created_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) day_count ");
        qrySelect.append(" FROM p2p_receiver_requests CR left outer join key_values KV on (CR.message_code=KV.key and KV.type=?), service_type ST, networks N ");
        qrySelect.append(" WHERE CR.service_type=ST.service_type AND CR.service_type=? ");
        qrySelect.append(" AND CR.network_code=N.network_code  AND CR.message_code<>? ");
        qrySelect.append(" AND CR.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN CR.network_code ELSE ? END ");
        qrySelect.append(" AND date_trunc('day',CR.created_date::TIMESTAMP)>=? AND date_trunc('day',CR.created_date::TIMESTAMP)<=? ");
        qrySelect.append(" GROUP BY  CR.network_code, N.network_name, CR.service_type, ST.name, KV.value ,CR.message_code ");
        if (log.isDebugEnabled()) {
            log.debug("loadP2PRecevierRequestQry", "Select qrySelect:" + qrySelect);
        }
        try{
        prepSelect = con.prepareStatement(qrySelect.toString());
        int i = 1;
        java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(fromDatePassed);
        java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(toDatePassed);
        prepSelect.setDate(i, toDate);
        i++;
        prepSelect.setDate(i, toDate);
        i++;
        prepSelect.setString(i, PretupsI.P2P_ERRCODE_VALUS);
        i++;
        prepSelect.setString(i, service);
        i++;
        prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        i++;
        prepSelect.setString(i, networkCode);
        i++;
        prepSelect.setString(i, networkCode);
        i++;
        prepSelect.setDate(i, fromDate);
        i++;
        prepSelect.setDate(i, toDate);
        }
        catch(SQLException e){
        	log.errorTrace("loadP2PRecevierRequestQry", e);
        }
		return prepSelect;
	}

	@Override
	public StringBuilder loadTotalP2PRecevierRequestQry() {
		StringBuilder qrySelect = new StringBuilder(" SELECT CR.network_code, N.network_name,CR.service_type,ST.name service_type_name, SUM(CASE TO_CHAR(CR.created_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) total_month_count, ");
        qrySelect.append(" SUM(CASE TO_CHAR(CR.created_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) total_day_count, ");
        qrySelect.append(" SUM(CASE CR.message_code WHEN ? THEN (CASE TO_CHAR(CR.created_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) success_month_count, ");
        qrySelect.append(" SUM(CASE CR.message_code WHEN ? THEN (CASE TO_CHAR(CR.created_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END)success_day_count, ");
        qrySelect.append(" SUM(CASE  WHEN (CR.message_code!=? AND CR.message_code IS NOT NULL ) THEN (CASE TO_CHAR(CR.created_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) fail_month_count, ");
        qrySelect.append(" SUM(CASE  WHEN (CR.message_code!=? AND CR.message_code IS NOT NULL ) THEN (CASE TO_CHAR(CR.created_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END)fail_day_count ");
        qrySelect.append(" FROM p2p_receiver_requests CR, networks N, service_type ST  WHERE CR.service_type=ST.service_type AND CR.network_code=N.network_code AND CR.service_type =? ");
        qrySelect.append(" AND CR.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN CR.network_code ELSE ? END ");
        qrySelect.append(" AND date_trunc('day',CR.created_date::TIMESTAMP)>=? AND date_trunc('day',CR.created_date::TIMESTAMP)<=? GROUP BY CR.network_code, N.network_name,CR.service_type,ST.name ");
		return qrySelect;
	}

	@Override
	public StringBuilder loadC2SReceiverRequestHourlyQry(Date date) {
		StringBuilder qrySelect = null;
		try{
			String dateStr = BTSLUtil.getDateStringFromDate(date);
	        String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
	        if (BTSLUtil.isNullString(format)) {
	            format = PretupsI.DATE_FORMAT;
	        }
	        qrySelect = new StringBuilder("SELECT ");
	        qrySelect.append("SUM(CASE date_trunc('day',CT.created_date::TIMESTAMP) WHEN TO_DATE('" + dateStr + "','" + format + "')  THEN 1 ELSE 0 END ) total_count, ");
	        qrySelect.append("SUM(CASE CT.message_code WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE date_trunc('day',CT.created_date::TIMESTAMP) WHEN TO_DATE('" + dateStr + "','" + format + "')THEN 1 ELSE 0 END ) ELSE 0 END) success_count, ");
	        String count = "";
	        String count1 = "";
	        format = format + " HH24:MI:SS";
	        for (int i = 0; i < 24; i++) {
	            if (i < 10) {
	                count = "0" + i;
	            } else {
	                count = "" + i;
	            }
	            if ((i + 1) < 10) {
	                count1 = "0" + (i + 1);
	            } else if (i == 23) {
	                count1 = "" + (i);
	            } else {
	                count1 = "" + (i + 1);
	            }
	            if (i < 23) {
	                qrySelect.append("SUM(CASE WHEN ((CT.created_date >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.created_date<=TO_DATE('" + dateStr + " " + count1 + ":00:00', '" + format + "'))) THEN 1 ELSE 0 END ) total_count" + (i + 1) + ", ");
	                qrySelect.append("SUM(CASE CT.message_code WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE WHEN ((CT.created_date >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.created_date<=TO_DATE('" + dateStr + " " + count1 + ":00:00' ,'" + format + "'))) THEN 1 ELSE 0 END ) ELSE 0 END) success_count" + (i + 1) + ", ");
	            } else {
	                qrySelect.append("SUM(CASE WHEN ((CT.created_date >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.created_date<=TO_DATE('" + dateStr + " " + count1 + ":59:59', '" + format + "'))) THEN 1 ELSE 0 END ) total_count" + (i + 1) + ", ");
	                qrySelect.append("SUM(CASE CT.message_code WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE WHEN ((CT.created_date >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.created_date<=TO_DATE('" + dateStr + " " + count1 + ":59:59' ,'" + format + "'))) THEN 1 ELSE 0 END ) ELSE 0 END) success_count" + (i + 1) + ", ");
	            }
	        }
	        qrySelect.append(" CT.network_code, N.network_name,CT.service_type, ST.name service_type_name ");
	        qrySelect.append(" FROM c2s_receiver_requests  CT, service_type ST, networks N ");
	        qrySelect.append("WHERE  CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
	        qrySelect.append("AND CT.service_type =? ");
	        qrySelect.append("AND CT.network_code=CASE ?  WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ?  END ");
	        qrySelect.append("AND date_trunc('day',CT.created_date::TIMESTAMP)=? GROUP BY CT.network_code, N.network_name, CT.service_type, ST.name");
        
		}
		catch (Exception e){
			log.errorTrace("loadC2SReceiverRequestHourlyQry", e);
		}
		return qrySelect;
	}

	@Override
	public StringBuilder loadP2PReceiverRequestHourlyQry(Date date) {
		StringBuilder qrySelect = null;
		try{
			String dateStr = BTSLUtil.getDateStringFromDate(date);
            String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
            if (BTSLUtil.isNullString(format)) {
                format = PretupsI.DATE_FORMAT;
            }
            qrySelect = new StringBuilder("SELECT ");
            qrySelect.append("SUM(CASE date_trunc('day',CT.created_date::TIMESTAMP) WHEN TO_DATE('" + dateStr + "','" + format + "')  THEN 1 ELSE 0 END ) total_count, ");
            qrySelect.append("SUM(CASE CT.message_code WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE date_trunc('day',CT.created_date::TIMESTAMP) WHEN TO_DATE('" + dateStr + "','" + format + "')THEN 1 ELSE 0 END ) ELSE 0 END) success_count, ");
            String count = "";
            String count1 = "";
            format = format + " HH24:MI:SS";
            for (int i = 0; i < 24; i++) {
                if (i < 10) {
                    count = "0" + i;
                } else {
                    count = "" + i;
                }
                if ((i + 1) < 10) {
                    count1 = "0" + (i + 1);
                } else if (i == 23) {
                    count1 = "" + (i);
                } else {
                    count1 = "" + (i + 1);
                }
                if (i < 23) {
                    qrySelect.append("SUM(CASE WHEN ((CT.created_date >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.created_date<=TO_DATE('" + dateStr + " " + count1 + ":00:00', '" + format + "'))) THEN 1 ELSE 0 END ) total_count" + (i + 1) + ", ");
                    qrySelect.append("SUM(CASE CT.message_code WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE WHEN ((CT.created_date >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.created_date<=TO_DATE('" + dateStr + " " + count1 + ":00:00' ,'" + format + "'))) THEN 1 ELSE 0 END ) ELSE 0 END) success_count" + (i + 1) + ", ");
                } else {
                    qrySelect.append("SUM(CASE WHEN ((CT.created_date >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.created_date<=TO_DATE('" + dateStr + " " + count1 + ":59:59', '" + format + "'))) THEN 1 ELSE 0 END ) total_count" + (i + 1) + ", ");
                    qrySelect.append("SUM(CASE CT.message_code WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE WHEN ((CT.created_date >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.created_date<=TO_DATE('" + dateStr + " " + count1 + ":59:59' ,'" + format + "'))) THEN 1 ELSE 0 END ) ELSE 0 END) success_count" + (i + 1) + ", ");
                }
            }
            qrySelect.append(" CT.network_code, N.network_name,CT.service_type, ST.name service_type_name ");
            qrySelect.append(" FROM p2p_receiver_requests  CT, service_type ST, networks N ");
            qrySelect.append("WHERE  CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
            qrySelect.append("AND CT.service_type =? ");
            qrySelect.append("AND CT.network_code=CASE ?  WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ?  END ");
            qrySelect.append("AND date_trunc('day',CT.created_date::TIMESTAMP)=? GROUP BY CT.network_code, N.network_name, CT.service_type, ST.name");

		}
		catch (Exception e){
			log.errorTrace("loadC2SReceiverRequestHourlyQry", e);
		}
		return qrySelect;
	}

	@Override
	public StringBuilder loadCountsForNtwrkTransferQry() {
		StringBuilder strBuff = new StringBuilder(" SELECT COALESCE(Sum(nsti.amount),0) amt,count(nsti.txn_no) cnt FROM network_stock_transactions NST, ");
        strBuff.append(" network_stock_trans_items NSTI WHERE NST.txn_no=NSTI.txn_no AND NSTI.product_code=? ");
        strBuff.append(" AND NST.txn_status=? AND NST.entry_type=? AND NST.network_code=? AND NST.network_code_for=? ");
        strBuff.append(" AND date_trunc('day',NST.modified_on::TIMESTAMP) >=date_trunc('day',?::TIMESTAMP) AND date_trunc('day',NST.modified_on::TIMESTAMP) <=date_trunc('day',?::TIMESTAMP) ");
		return strBuff ;
	}
	
	@Override
	public StringBuilder loadChannelServiceCountsQry() {
		StringBuilder strBuff = new StringBuilder(" SELECT SUM(transfer_mrp) amt,count(CT.transfer_id) cnt");
        strBuff.append(" FROM channel_transfers CT,channel_transfers_items CTI WHERE CT.transfer_id=CTI.transfer_id");
        strBuff.append(" AND CT.transfer_date >=date_trunc('day',?::TIMESTAMP) AND CT.transfer_date <=date_trunc('day',?::TIMESTAMP) AND CTI.PRODUCT_CODE=?  ");
        strBuff.append(" AND CT.network_code=? AND CT.status=? AND type=? AND transfer_sub_type=?  ");
		return strBuff;
	}

	@Override
	public StringBuilder loadC2SServiceCountsQry() {
		//local_index_implemented
		StringBuilder strBuff = new StringBuilder(" SELECT Sum(transfer_value) amt,count(transfer_id) cnt");
        strBuff.append(" FROM c2s_transfers WHERE transfer_date >=date_trunc('day',?::TIMESTAMP) AND transfer_date <=date_trunc('day',?::TIMESTAMP) AND ");
        strBuff.append(" transfer_status=? AND network_code=? AND product_code=?  ");
        strBuff.append(" AND service_type=? ");
		return strBuff;
	}
	
	@Override
	public StringBuilder loadCountsForP2PServicesQry() {
		StringBuilder strBuff = new StringBuilder(" SELECT SUM(transfer_value) amt,COUNT(transfer_id) cnt FROM subscriber_transfers  ");
	        strBuff.append(" WHERE transfer_status=? ");
	        strBuff.append(" AND network_code=? AND product_code=?  AND service_type=?   ");
	        strBuff.append(" AND transfer_date >=date_trunc('day',?::TIMESTAMP) AND transfer_date <=date_trunc('day',?::TIMESTAMP)  ");
		return strBuff;
	}
	
	@Override
	public StringBuilder loadChannelActivUserCountsQry(Date fromDate,
			Date toDate) {
		StringBuilder strBuff = new StringBuilder(" SELECT Count(DISTINCT (UTC.user_id )) cnt");
        strBuff.append(" FROM user_transfer_counts UTC ,users U WHERE U.user_id =UTC.user_id AND U.network_code = ? ");
        strBuff.append(" AND UTC.last_out_time::TIMESTAMP >=?::timestamp AND UTC.last_out_time::TIMESTAMP <=?::timestamp    ");

        if (fromDate.equals(toDate)) {
            strBuff.append(" AND UTC.daily_subscriber_out_count >= 1  ");
        } else {
            strBuff.append(" AND UTC.monthly_subscriber_out_count >= 1  ");
        }
		return strBuff;
	}
	
    public StringBuilder loadC2SRechargeQry(){   
    	//local_index_implemented
		StringBuilder qrySelect = new StringBuilder("SELECT CT.network_code,CT.service_type,CAT.category_code,I.interface_id, ");
		qrySelect.append("N.network_name,ST.name service_type_name,CAT.category_name,I.interface_description, ");
    	qrySelect.append("SUM(CASE WHEN TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')  THEN 1 ELSE 0 END ) daily_total_count, ");
    	qrySelect.append("SUM(CASE WHEN TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  THEN 1 ELSE 0 END ) monthly_total_count, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='200' AND TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END) daily_success_count, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='200' AND TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END) monthly_success_count, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='206' AND TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')THEN 1 ELSE 0 END ) daily_fail_count, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='206' AND TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END) monthly_fail_count, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='250' AND TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')THEN 1 ELSE 0 END ) daily_ambiguous_count, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='250' AND TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END) monthly_ambiguous_count, ");
    	qrySelect.append("SUM(CASE WHEN TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')  THEN CT.transfer_value ELSE 0 END) daily_total_amount, ");
    	qrySelect.append("SUM(CASE WHEN TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  THEN CT.transfer_value ELSE 0 END) monthly_total_amount, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='200' AND TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')THEN CT.transfer_value ELSE 0 END) daily_success_amount, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='200' AND TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN CT.transfer_value ELSE 0 END) monthly_success_amount, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='206' AND TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')THEN CT.transfer_value ELSE 0 END) daily_fail_amount, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='206' AND TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN CT.transfer_value ELSE 0 END) monthly_fail_amount, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='250' AND TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')THEN CT.transfer_value ELSE 0 END) daily_ambiguous_amount, ");
    	qrySelect.append("SUM(CASE WHEN CT.transfer_status='250' AND TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN CT.transfer_value ELSE 0 END) monthly_ambiguous_amount ");
    	qrySelect.append("FROM c2s_transfers CT, networks N, service_type ST,categories CAT,interfaces I ");
    	qrySelect.append("WHERE CT.transfer_date>=?::timestamp AND CT.transfer_date<=?::timestamp AND CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
    	qrySelect.append("AND CT.sender_category=CAT.category_code AND I.interface_id=CT.interface_id  ");
    	qrySelect.append("GROUP BY CT.network_code, N.network_name, CT.service_type, ST.name, ");
    	qrySelect.append("CAT.category_code, CAT.category_name,I.interface_id,I.interface_description ");
    	qrySelect.append("ORDER BY N.network_name,ST.name,CAT.category_name,I.interface_description ");
	
    	return qrySelect;
	}
    
	public StringBuilder loadTotalC2SRechargeQry()
	{		
		//local_index_implemented
		StringBuilder qrySelect = new StringBuilder("SELECT CT.network_code, N.network_name, CT.service_type, ST.name service_type_name, ");
        qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')  THEN 1 ELSE 0 END ) daily_total_count, ");
        qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  THEN 1 ELSE 0 END ) monthly_total_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')THEN 1 ELSE 0 END ) ELSE 0 END) daily_success_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END ) ELSE 0 END) monthly_success_count ");
        qrySelect.append("FROM c2s_transfers CT, networks N, service_type ST WHERE CT.transfer_date>=?::timestamp AND CT.transfer_date<=?::timestamp AND CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
        qrySelect.append("AND CT.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ? END ");
        qrySelect.append("AND CT.service_type =? ");
        qrySelect.append("GROUP BY CT.network_code, N.network_name,CT.service_type,ST.name ");
        
        return qrySelect;
	}
	
	public StringBuilder loadC2STransferSummaryProductQry()
	{
		//local_index_implemented
		StringBuilder qrySelect = new StringBuilder("SELECT CT.network_code, N.network_name, CT.service_type, ST.name service_type_name, P.product_name,CT.product_code, ");
        qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) total_month_count,  ");
        qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) total_month_amount, ");
        qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) total_day_count, ");
        qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) total_day_amount, ");

        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) success_month_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END) success_month_amount, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END) success_day_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END) success_day_amount, ");

        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) fail_month_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END) fail_month_amount, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END) fail_day_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END) fail_day_amount, ");

        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) ambigous_month_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END) ambigous_month_amount, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END) ambigous_day_count,");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END) ambigous_day_amount, ");

        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) underprocess_month_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END) underprocess_month_amount, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END) underprocess_day_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END) underprocess_day_amount ");

        qrySelect.append(" FROM c2s_transfers CT,products P, networks N, service_type ST WHERE  CT.transfer_date>=?::timestamp AND CT.transfer_date<=?::timestamp AND CT.network_code=N.network_code ");
        qrySelect.append(" AND CT.product_code=P.product_code AND CT.service_type = ST.service_type AND CT.service_type=? ");
        qrySelect.append(" AND CT.network_code=CASE ?  WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ?  END ");
        qrySelect.append(" GROUP BY CT.network_code, N.network_name, CT.service_type, ST.name, CT.product_code,P.product_name ORDER BY CT.network_code,CT.service_type");
        return qrySelect;
	}
	
	
	public StringBuilder loadTotalP2PRechargeQry()
	{
		StringBuilder qrySelect = new StringBuilder("SELECT CT.network_code, N.network_name, CT.service_type, ST.name service_type_name, ");
         qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')  THEN 1 ELSE 0 END ) daily_total_count, ");
         qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  THEN 1 ELSE 0 END ) monthly_total_count, ");
         qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')THEN 1 ELSE 0 END ) ELSE 0 END) daily_success_count, ");
         qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END ) ELSE 0 END) monthly_success_count ");
         qrySelect.append("FROM subscriber_transfers CT, networks N, service_type ST WHERE CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
         qrySelect.append("AND CT.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ? END ");
         qrySelect.append("AND CT.service_type =? AND CT.transfer_date>=?::timestamp AND CT.transfer_date<=?::timestamp ");
         qrySelect.append("GROUP BY CT.network_code, N.network_name, CT.service_type,ST.name ");
         return qrySelect;
	}
	
	public StringBuilder loadP2PTransferSummaryProductQry()
	{
		StringBuilder qrySelect = new StringBuilder("SELECT CT.network_code, N.network_name, CT.service_type, ST.name service_type_name, P.product_name, CT.product_code, ");
        qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) total_month_count,  ");
        // qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) total_month_amount, ");
        qrySelect.append("TO_CHAR((SUM(CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END)/" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() + "),'99999999999999999999999999999999999') total_month_amount, ");
        qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) total_day_count, ");
        // qrySelect.append("SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) total_day_amount, ");
        qrySelect.append("TO_CHAR((SUM(CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END)/" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() + "),'99999999999999999999999999999999999') total_day_amount, ");

        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) success_month_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END) success_month_amount, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END) success_day_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END) success_day_amount, ");

        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) fail_month_count, ");
        // qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END) fail_month_amount, ");
        qrySelect.append("TO_CHAR((SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END)/" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() + "),'99999999999999999999999999999999999') fail_month_amount, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END) fail_day_count, ");
        // qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END) fail_day_amount, ");
        qrySelect.append("TO_CHAR((SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END)/" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() + "),'99999999999999999999999999999999999') fail_day_amount, ");

        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) ambigous_month_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END) ambigous_month_amount, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END)  ambigous_day_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END) ambigous_day_amount, ");

        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN 1 ELSE 0 END) ELSE 0 END) underprocess_month_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') THEN CT.transfer_value ELSE 0  END) ELSE 0 END) underprocess_month_amount, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN 1 ELSE 0 END ) ELSE 0 END) underprocess_day_count, ");
        qrySelect.append("SUM(CASE CT.transfer_status WHEN ? THEN (CASE TO_CHAR(CT.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy') THEN CT.transfer_value ELSE 0 END) ELSE 0 END) underprocess_day_amount ");

        qrySelect.append(" FROM subscriber_transfers CT,products P, networks N, service_type ST WHERE CT.network_code=N.network_code ");
        qrySelect.append(" AND CT.product_code=P.product_code AND CT.service_type=ST.service_type AND CT.service_type=? ");
        qrySelect.append(" AND CT.network_code=CASE ?  WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ?  END AND CT.transfer_date>=?::timestamp AND CT.transfer_date<=?::timestamp  ");
        qrySelect.append(" GROUP BY CT.network_code, N.network_name, CT.service_type, ST.name, CT.product_code, P.product_name ORDER BY CT.network_code,CT.service_type ");
        return qrySelect;
	}

	public StringBuilder loadP2PRechargeQry()
	{
		StringBuilder qrySelect = new StringBuilder("SELECT STR.network_code,STR.service_type,SI.interface_id sender_interface_id,RI.interface_id receiver_interface_id, ");
        qrySelect.append("N.network_name,ST.name service_type_name,SI.interface_description sender_interface_desc,RI.interface_description receiver_interface_desc, ");
        qrySelect.append("SUM(CASE TO_CHAR(STR.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')  THEN 1 ELSE 0 END ) daily_total_count, ");
        qrySelect.append("SUM(CASE TO_CHAR(STR.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  THEN 1 ELSE 0 END ) monthly_total_count, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '200' THEN (CASE TO_CHAR(STR.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')THEN 1 ELSE 0 END ) ELSE 0 END) daily_success_count, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '200' THEN (CASE TO_CHAR(STR.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END ) ELSE 0 END) monthly_success_count, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '206' THEN (CASE TO_CHAR(STR.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')THEN 1 ELSE 0 END ) ELSE 0 END) daily_fail_count, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '206' THEN (CASE TO_CHAR(STR.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END ) ELSE 0 END) monthly_fail_count, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '250' THEN (CASE TO_CHAR(STR.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')THEN 1 ELSE 0 END ) ELSE 0 END) daily_ambiguous_count, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '250' THEN (CASE TO_CHAR(STR.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END ) ELSE 0 END) monthly_ambiguous_count, ");
        qrySelect.append("SUM(CASE TO_CHAR(STR.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')  THEN STR.transfer_value ELSE 0 END ) daily_total_amount, ");
        qrySelect.append("SUM(CASE TO_CHAR(STR.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  THEN STR.transfer_value ELSE 0 END ) monthly_total_amount, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '200' THEN (CASE TO_CHAR(STR.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')THEN STR.transfer_value ELSE 0 END ) ELSE 0 END) daily_success_amount, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '200' THEN (CASE TO_CHAR(STR.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN STR.transfer_value ELSE 0 END ) ELSE 0 END) monthly_success_amount, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '206' THEN (CASE TO_CHAR(STR.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')THEN STR.transfer_value ELSE 0 END ) ELSE 0 END) daily_fail_amount, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '206' THEN (CASE TO_CHAR(STR.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN STR.transfer_value ELSE 0 END ) ELSE 0 END) monthly_fail_amount, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '250' THEN (CASE TO_CHAR(STR.transfer_date,'dd/mm/yy') WHEN TO_CHAR(?::timestamp,'dd/mm/yy')THEN STR.transfer_value ELSE 0 END ) ELSE 0 END) daily_ambiguous_amount, ");
        qrySelect.append("SUM(CASE STR.transfer_status WHEN '250' THEN (CASE TO_CHAR(STR.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  WHEN TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN STR.transfer_value ELSE 0 END ) ELSE 0 END) monthly_ambiguous_amount ");
        qrySelect.append("FROM subscriber_transfers STR, networks N, service_type ST,interfaces SI,interfaces RI,transfer_items STI,transfer_items RTI ");
        qrySelect.append("WHERE STR.service_type=ST.service_type AND STR.network_code=N.network_code ");
        qrySelect.append("AND STR.transfer_id=STI.transfer_id AND STR.transfer_id=RTI.transfer_id ");
        qrySelect.append("AND STI.user_type=? AND RTI.user_type=? ");
        qrySelect.append("AND STI.SNO=1 AND RTI.SNO=2 ");
        qrySelect.append("AND STI.interface_id=SI.interface_id AND RTI.interface_id=RI.interface_id ");
        // qrySelect.append("AND STR.network_code=CASE ? WHEN '"+PretupsI.ALL+"' THEN STR.network_code ELSE ? END ");
        qrySelect.append("AND STR.transfer_date>=?::timestamp AND STR.transfer_date<=?::timestamp ");
        qrySelect.append("GROUP BY STR.network_code,STR.service_type,SI.interface_id,RI.interface_id, ");
        qrySelect.append("N.network_name,ST.name,SI.interface_description,RI.interface_description ");
        qrySelect.append("ORDER BY N.network_name,ST.name,SI.interface_description ,RI.interface_description");
        return qrySelect;
	}
	
	public StringBuilder loadInterfaceWiseC2SRechargeQry()
	{
		//local_index_implemented
		StringBuilder qrySelect = new StringBuilder("SELECT I.interface_id,I.interface_description, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')  THEN 1 ELSE 0 END ) daily_total_validation, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND CT.validation_status='200' THEN 1 ELSE 0 END) daily_total_credit, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')  THEN 1 ELSE 0 END ) monthly_total_validation, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND CT.validation_status='200' THEN 1 ELSE 0 END) monthly_total_credit, ");
         qrySelect.append("SUM(CASE WHEN CT.validation_status<>'200' AND TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy')THEN 1 ELSE 0 END ) daily_fail_validation, ");
         qrySelect.append("SUM(CASE WHEN CT.DEBIT_STATUS<>'200' AND TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND CT.validation_status='200' THEN 1 ELSE 0 END) daily_fail_credit, ");
         qrySelect.append("SUM(CASE WHEN CT.validation_status<>'200' AND TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')THEN 1 ELSE 0 END ) monthly_fail_validation, ");
         qrySelect.append("SUM(CASE WHEN CT.DEBIT_STATUS<>'200' AND TO_CHAR(CT.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND CT.validation_status='200' THEN 1 ELSE 0 END) monthly_fail_credit ");
         qrySelect.append("FROM interfaces I,c2s_transfers CT WHERE  ");
         qrySelect.append(" CT.transfer_date>=?::timestamp AND CT.transfer_date<=?::timestamp AND I.interface_id=CT.interface_id ");
         qrySelect.append("GROUP BY I.interface_id,I.interface_description ORDER BY I.interface_description");
         return qrySelect;
	}
	
	public StringBuilder loadInterfaceWiseP2PRechargeQry()
	{
		StringBuilder qrySelect = new StringBuilder("SELECT I.interface_id,I.interface_description, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND TI.user_type=? THEN 1 ELSE 0 END ) daily_total_sender_val, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND TI.user_type=? AND TI.update_status<>'212' AND TI.update_status IS NOT NULL THEN 1 ELSE 0 END ) daily_total_sender_debit, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND TI.user_type=? AND TI.validation_status IS NOT NULL AND TI.validation_status<>'200' THEN 1 ELSE 0 END ) daily_fail_sender_val, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND TI.user_type=? AND TI.update_status IS NOT NULL AND TI.update_status<>'200' AND TI.update_status<>'212' THEN 1 ELSE 0 END ) daily_fail_sender_debit, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND TI.user_type=? AND TI.validation_status IS NOT NULL THEN 1 ELSE 0 END ) daily_total_rec_val, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND TI.user_type=? AND TI.update_status IS NOT NULL AND TI.update_status<>'212' THEN 1 ELSE 0 END ) daily_total_rec_credit, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND TI.user_type=? AND TI.validation_status<>'200' AND TI.validation_status IS NOT NULL THEN 1 ELSE 0 END ) daily_fail_rec_val, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp,'dd/mm/yy') AND TI.user_type=? AND TI.update_status IS NOT NULL AND TI.update_status<>'212' AND TI.update_status<>'200' THEN 1 ELSE 0 END ) daily_fail_rec_credit, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND TI.user_type=? THEN 1 ELSE 0 END ) monthly_total_sender_val, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND TI.user_type=? AND TI.update_status<>'212' AND TI.update_status IS NOT NULL THEN 1 ELSE 0 END ) monthly_total_sender_debit, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND TI.user_type=? AND TI.validation_status IS NOT NULL AND TI.validation_status<>'200' THEN 1 ELSE 0 END ) monthly_fail_sender_val, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND TI.user_type=? AND TI.update_status IS NOT NULL AND TI.update_status<>'200' AND TI.update_status<>'212' THEN 1 ELSE 0 END ) monthly_fail_sender_debit, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND TI.user_type=? AND TI.validation_status IS NOT NULL THEN 1 ELSE 0 END ) monthly_total_rec_val, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND TI.user_type=? AND TI.update_status IS NOT NULL AND TI.update_status<>'212' THEN 1 ELSE 0 END ) monthly_total_rec_credit, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND TI.user_type=? AND TI.validation_status<>'200' AND TI.validation_status IS NOT NULL THEN 1 ELSE 0 END ) monthly_fail_rec_val, ");
         qrySelect.append("SUM(CASE WHEN TO_CHAR(TI.transfer_date,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "')=TO_CHAR(?::timestamp,'" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR)) + "') AND TI.user_type=? AND TI.update_status IS NOT NULL AND TI.update_status<>'212' AND TI.update_status<>'200' THEN 1 ELSE 0 END ) monthly_fail_rec_credit ");
         qrySelect.append("FROM interfaces I,transfer_items TI WHERE I.interface_id=TI.interface_id ");
         qrySelect.append("AND TI.transfer_date>=?::timestamp AND TI.transfer_date<=?::timestamp ");
         qrySelect.append("GROUP BY I.interface_id,I.interface_description ORDER BY I.interface_description");
         return qrySelect;
	}
	
	public StringBuilder loadC2SServiceInterfaceRechargeQry()
	{
		//local_index_implemented
		StringBuilder qrySelect = new StringBuilder("SELECT CT.network_code,CT.service_type,I.interface_id, ");
        qrySelect.append("N.network_name,ST.name service_type_name,I.interface_description, ");
        qrySelect.append("SUM(1) daily_total_count, ");
        qrySelect.append("SUM(CASE WHEN CT.transfer_status='200' THEN 1 ELSE 0 END) daily_success_count, ");
        qrySelect.append("SUM(CASE WHEN CT.transfer_status='206' THEN 1 ELSE 0 END ) daily_fail_count, ");
        qrySelect.append("SUM(CASE WHEN CT.transfer_status='250' THEN 1 ELSE 0 END ) daily_ambiguous_count, ");
        qrySelect.append("SUM(CASE WHEN CT.transfer_status='205' THEN 1 ELSE 0 END ) daily_underprocess_count, ");
        qrySelect.append("SUM(CT.transfer_value) daily_total_amount, ");
        qrySelect.append("SUM(CASE WHEN CT.transfer_status='200' THEN CT.transfer_value ELSE 0 END) daily_success_amount, ");
        qrySelect.append("SUM(CASE WHEN CT.transfer_status='206' THEN CT.transfer_value ELSE 0 END) daily_fail_amount, ");
        qrySelect.append("SUM(CASE WHEN CT.transfer_status='250' THEN CT.transfer_value ELSE 0 END) daily_ambiguous_amount, ");
        qrySelect.append("SUM(CASE WHEN CT.transfer_status='205' THEN CT.transfer_value ELSE 0 END) daily_underprocess_amount ");
        qrySelect.append("FROM c2s_transfers CT, networks N, service_type ST,interfaces I ");
        qrySelect.append("WHERE TO_CHAR(CT.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp ,'dd/mm/yy') AND CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
        qrySelect.append("AND I.interface_id=CT.interface_id ");
        qrySelect.append("AND CT.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ? END ");
        qrySelect.append("GROUP BY CT.network_code, N.network_name, CT.service_type, ST.name, ");
        qrySelect.append("I.interface_id,I.interface_description ");
        qrySelect.append("ORDER BY N.network_name,ST.name,I.interface_description ");
        return qrySelect;
	}
	
	public StringBuilder loadP2PServiceInterfaceRechargeQry()
	{
		StringBuilder qrySelect = new StringBuilder("SELECT STR.network_code,STR.service_type,SI.interface_id sender_interface_id,RI.interface_id receiver_interface_id, ");
        qrySelect.append("N.network_name,ST.name service_type_name,SI.interface_description sender_interface_desc,RI.interface_description receiver_interface_desc, ");
        qrySelect.append("SUM(1) daily_total_count, ");
        qrySelect.append("SUM(CASE WHEN STR.transfer_status='200' THEN 1 ELSE 0 END ) daily_success_count, ");
        qrySelect.append("SUM(CASE WHEN STR.transfer_status='206' THEN 1 ELSE 0 END ) daily_fail_count, ");
        qrySelect.append("SUM(CASE WHEN STR.transfer_status='250' THEN 1 ELSE 0 END ) daily_ambiguous_count, ");
        qrySelect.append("SUM(CASE WHEN STR.transfer_status='205' THEN 1 ELSE 0 END ) daily_underprocess_count, ");
        qrySelect.append("SUM(STR.transfer_value) daily_total_amount, ");
        qrySelect.append("SUM(CASE WHEN STR.transfer_status='200' THEN STR.transfer_value ELSE 0 END ) daily_success_amount, ");
        qrySelect.append("SUM(CASE WHEN STR.transfer_status='206' THEN STR.transfer_value ELSE 0 END ) daily_fail_amount, ");
        qrySelect.append("SUM(CASE WHEN STR.transfer_status='250' THEN STR.transfer_value ELSE 0 END ) daily_ambiguous_amount, ");
        qrySelect.append("SUM(CASE WHEN STR.transfer_status='205' THEN STR.transfer_value ELSE 0 END ) daily_underprocess_amount ");
        qrySelect.append("FROM subscriber_transfers STR, networks N, service_type ST,interfaces SI,interfaces RI,transfer_items STI,transfer_items RTI ");
        qrySelect.append("WHERE STR.service_type=ST.service_type AND STR.network_code=N.network_code ");
        qrySelect.append("AND STR.transfer_id=STI.transfer_id AND STR.transfer_id=RTI.transfer_id ");
        qrySelect.append("AND STI.user_type=? AND RTI.user_type=? ");
        qrySelect.append("AND STI.interface_id=SI.interface_id AND RTI.interface_id=RI.interface_id ");
        qrySelect.append("AND TO_CHAR(STR.transfer_date,'dd/mm/yy')=TO_CHAR(?::timestamp ,'dd/mm/yy') ");
        qrySelect.append("AND STR.network_code=CASE ? WHEN '" + PretupsI.ALL + "' THEN STR.network_code ELSE ? END ");
        qrySelect.append("GROUP BY STR.network_code,STR.service_type,SI.interface_id,RI.interface_id, ");
        qrySelect.append("N.network_name,ST.name,SI.interface_description,RI.interface_description ");
        qrySelect.append("ORDER BY N.network_name,ST.name,SI.interface_description ,RI.interface_description");
        return qrySelect;
	}
	
	@Override
	public StringBuilder searchPassBookDetailsQry(PassbookSearchInputVO passbookSearchInputVO) {

		final String methodName = "searchPassBookDetailsQry";
        StringBuilder qrySelect = new StringBuilder();

        qrySelect.append("SELECT CT.trans_date transfer_date, P.product_name product_name, U.user_name, U.msisdn, U.external_code, UP.user_name parent_name, ");
        qrySelect.append("UP.msisdn parent_msisdn, PC.CATEGORY_NAME parentcategoryName, OU.user_name ownerName, CAT.CATEGORY_NAME USERCATEGORY, ");
        qrySelect.append("GD.grph_domain_name as USEGEOGRPHY, GP.user_name grand_name, GP.msisdn grand_msisdn, GD1.GRPH_DOMAIN_NAME ParentGeography, ");
        qrySelect.append("GD2.GRPH_DOMAIN_NAME owner_geo, CT.opening_balance, CT.closing_balance, OC.category_name ownercategoryName, ou.msisdn ownermsisdn, ");
        qrySelect.append("CT.O2C_TRANSFER_IN_COUNT o2cTransferCount, CT.o2c_transfer_in_amount o2cTransferAmount, CT.O2C_RETURN_OUT_COUNT o2cReturnCount, ");
        qrySelect.append("CT.O2C_RETURN_OUT_AMOUNT o2cReturnAmount, CT.O2C_WITHDRAW_OUT_COUNT o2cWithdrawCount, CT.O2C_WITHDRAW_OUT_AMOUNT o2cWithdrawAmount, ");
        qrySelect.append("CT.C2C_TRANSFER_IN_COUNT c2cTransfer_InCount, CT.C2C_TRANSFER_IN_AMOUNT c2cTransfer_InAmount, CT.C2C_TRANSFER_OUT_COUNT c2cTransfer_OutCount, ");
        qrySelect.append("CT.C2C_TRANSFER_OUT_AMOUNT c2cTransfer_OutAmount, CT.C2C_RETURN_IN_COUNT c2cTransferRet_InCount, CT.C2C_RETURN_IN_AMOUNT c2cTransferRet_InAmount, ");
        qrySelect.append("CT.C2C_RETURN_OUT_COUNT c2cTransferRet_OutCount, CT.C2C_RETURN_OUT_AMOUNT c2cTransferRet_OutAmount, CT.C2C_WITHDRAW_IN_COUNT c2cTransferWithdraw_InCount, ");
        qrySelect.append("CT.C2C_WITHDRAW_IN_AMOUNT c2cTransferWithdraw_InAmount, CT.C2C_WITHDRAW_OUT_COUNT c2cTransferWithdraw_OutCount, CT.c2s_transfer_out_COUNT, ");
        qrySelect.append("CT.c2s_transfer_IN_COUNT, CT.c2s_transfer_IN_AMOUNT, CT.c2s_transfer_out_amount, CT.o2c_return_out_amount, CT.o2c_withdraw_out_amount, ");
        qrySelect.append("CT.c2c_transfer_out_amount, CT.c2c_withdraw_out_amount, CT.c2c_return_out_amount, CT.c2c_return_in_amount, CT.c2c_transfer_in_amount, CT.trans_date, ");
        qrySelect.append("OC.CATEGORY_NAME, GC.CATEGORY_NAME, CT.DIFFERENTIAL as COMMISSION ");
        qrySelect.append("FROM (WITH RECURSIVE cte_connect_by AS (SELECT 1 AS level, s.* FROM USERS s WHERE user_id = ? ");
        qrySelect.append("UNION ALL SELECT level + 1 AS level, s.* FROM cte_connect_by r INNER JOIN USERS s ON r.user_id = s.parent_id) ");
        qrySelect.append("SELECT user_id, parent_id, owner_id FROM CTE_CONNECT_BY) X, DAILY_CHNL_TRANS_MAIN CT, USERS U, CATEGORIES CAT, USER_GEOGRAPHIES UG, ");
        qrySelect.append("GEOGRAPHICAL_DOMAINS GD, PRODUCTS P, USERS UP, USERS GP, USERS OU, USER_GEOGRAPHIES UGG, USER_GEOGRAPHIES UGW, GEOGRAPHICAL_DOMAINS GD1, ");
        qrySelect.append("GEOGRAPHICAL_DOMAINS GD2, CATEGORIES PC, CATEGORIES OC, CATEGORIES GC ");
        qrySelect.append("WHERE X.user_id = CT.user_id AND CT.user_id = U.user_id AND P.product_code = CT.product_code AND CAT.category_code = U.category_code ");
        qrySelect.append("AND U.user_id = UG.user_id AND UG.grph_domain_code = GD.grph_domain_code AND UP.USER_ID = CASE X.parent_id WHEN 'ROOT' THEN X.user_id ");
        qrySelect.append("ELSE X.parent_id END AND GP.USER_ID = CASE UP.parent_id WHEN 'ROOT' THEN UP.user_id ELSE UP.parent_id END AND OU.USER_ID = X.OWNER_ID ");
        qrySelect.append("AND UGG.user_id = GP.USER_ID AND UGG.GRPH_DOMAIN_CODE = GD1.GRPH_DOMAIN_CODE AND UGW.USER_ID = OU.USER_ID ");
        qrySelect.append("AND UGW.GRPH_DOMAIN_CODE = GD2.GRPH_DOMAIN_CODE AND PC.CATEGORY_CODE = UP.CATEGORY_CODE AND GC.CATEGORY_CODE = GP.CATEGORY_CODE ");
        qrySelect.append("AND OC.CATEGORY_CODE = OU.CATEGORY_CODE AND CAT.category_code = CASE 'ALL' WHEN 'ALL' THEN CAT.category_code ELSE 'ALL' END ");
        qrySelect.append("AND CT.trans_date >= ? AND CT.trans_date <= ? AND CT.user_id = ? AND CT.network_code = ? ");
        qrySelect.append("AND p.product_code = CASE ? WHEN 'ALL' THEN p.product_code ELSE ? END ");
        qrySelect.append("ORDER BY CT.trans_date DESC, CT.product_code ASC");

        if(log.isDebugEnabled()) {
            log.debug(methodName, qrySelect.toString());
        }


        return qrySelect;

	}

	@Override
	public StringBuilder getDataFromReportMasterByID(String reportID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkDB() {
		// TODO Auto-generated method stub
		return "POSTGRES";
	}
}
