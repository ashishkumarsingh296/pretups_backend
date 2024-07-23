package com.btsl.db.query.postgres;

import java.text.SimpleDateFormat;

import com.btsl.pretups.processes.HourlyCountDetailAlertQry;
import com.ibm.icu.util.Calendar;

public class HourlyCountDetailAlertPostgresQry implements HourlyCountDetailAlertQry{

	@Override
	public String transactionDetailIfC2SModule(SimpleDateFormat formatter,SimpleDateFormat formatter1,Calendar p_now, Calendar working ) {
		final StringBuilder queryC2STrans = new StringBuilder(" SELECT NETWORK_CODE,SERVICE_TYPE, ");
		queryC2STrans
		.append(" SUM(CASE WHEN TRANSFER_STATUS='206' AND transfer_date_time >= (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))-1/24) AND transfer_date_time < (to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) failed_hour_count, ");
		queryC2STrans
		.append(" SUM(CASE WHEN TRANSFER_STATUS='200' AND transfer_date_time >= (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))-1/24) AND transfer_date_time < (to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) success_hour_count, ");
		queryC2STrans
		.append(" SUM(CASE WHEN (TRANSFER_STATUS='200' OR TRANSFER_STATUS='206') AND transfer_date_time >= (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))-1/24) AND transfer_date_time < (to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) total_hour_count, ");
		queryC2STrans
		.append(" SUM(CASE WHEN (TRANSFER_STATUS='200' OR TRANSFER_STATUS='206') AND transfer_date_time >= (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))-1/24) AND transfer_date_time < (to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN TRANSFER_VALUE ELSE 0 END) total_hour_amount, ");
		queryC2STrans
		.append(" SUM(CASE WHEN TRANSFER_STATUS='206' AND transfer_date_time < (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) failed_day_count, ");
		queryC2STrans
		.append(" SUM(CASE WHEN TRANSFER_STATUS='200' AND transfer_date_time < (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) success_day_count, ");
		queryC2STrans
		.append(" SUM(CASE WHEN (TRANSFER_STATUS='200' OR TRANSFER_STATUS='206') AND transfer_date_time < (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) total_day_count, ");
		queryC2STrans
		.append(" SUM(CASE WHEN (TRANSFER_STATUS='200' OR TRANSFER_STATUS='206') AND transfer_date_time < (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN TRANSFER_VALUE ELSE 0 END) total_day_amount ");
		queryC2STrans.append(" FROM C2S_TRANSFERS ");
		queryC2STrans
		.append(" WHERE (TRANSFER_STATUS='200' or TRANSFER_STATUS='206') and transfer_date = TO_DATE('" + formatter1.format(working.getTime()) + "','dd/MM/yyyy') ");
		queryC2STrans
		.append(" and transfer_date_time<(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
				.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) ");
		queryC2STrans.append(" GROUP BY NETWORK_CODE,SERVICE_TYPE ");
		return queryC2STrans.toString();
	}
	
	@Override
	public String transactionDetailIfP2PModule(SimpleDateFormat formatter,
			SimpleDateFormat formatter1, Calendar p_now, Calendar working) {
		 final StringBuilder queryP2PTrans = new StringBuilder(" SELECT NETWORK_CODE,SERVICE_TYPE, ");
         queryP2PTrans
             .append(" SUM(CASE WHEN TRANSFER_STATUS='206' AND transfer_date_time >= (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))-1/24) AND transfer_date_time < (to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) failed_hour_count, ");
         queryP2PTrans
             .append(" SUM(CASE WHEN TRANSFER_STATUS='200' AND transfer_date_time >= (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))-1/24) AND transfer_date_time < (to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) success_hour_count, ");
         queryP2PTrans
             .append(" SUM(CASE WHEN (TRANSFER_STATUS='200' OR TRANSFER_STATUS='206') AND transfer_date_time >= (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))-1/24) AND transfer_date_time < (to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) total_hour_count, ");
         queryP2PTrans
             .append(" SUM(CASE WHEN (TRANSFER_STATUS='200' OR TRANSFER_STATUS='206') AND transfer_date_time >= (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))-1/24) AND transfer_date_time < (to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN TRANSFER_VALUE ELSE 0 END) total_hour_amount, ");
         queryP2PTrans
             .append(" SUM(CASE WHEN TRANSFER_STATUS='206' AND transfer_date_time < (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) failed_day_count, ");
         queryP2PTrans
             .append(" SUM(CASE WHEN TRANSFER_STATUS='200' AND transfer_date_time < (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) success_day_count, ");
         queryP2PTrans
             .append(" SUM(CASE WHEN (TRANSFER_STATUS='200' OR TRANSFER_STATUS='206') AND transfer_date_time < (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN 1 ELSE 0 END) total_day_count, ");
         queryP2PTrans
             .append(" SUM(CASE WHEN (TRANSFER_STATUS='200' OR TRANSFER_STATUS='206') AND transfer_date_time < (to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) THEN TRANSFER_VALUE ELSE 0 END) total_day_amount ");
         queryP2PTrans.append(" FROM SUBSCRIBER_TRANSFERS ");
         queryP2PTrans
             .append(" WHERE (TRANSFER_STATUS='200' or TRANSFER_STATUS='206') and transfer_date = TO_DATE('" + formatter1.format(working.getTime()) + "','dd/MM/yyyy') "); // to_date('"+formatter.format(now.getTime())+"','dd/MM/yyyy
         // HH:mi:ss
         // am')
         queryP2PTrans
             .append(" and transfer_date_time<(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am')-(TO_CHAR(to_date('" + formatter
                 .format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'mi')::integer/(24*60))-(TO_CHAR(to_date('" + formatter.format(p_now.getTime()) + "','dd/MM/yyyy HH:mi:ss am'),'ss')::integer/(24*60*60))) ");
         queryP2PTrans.append(" GROUP BY NETWORK_CODE,SERVICE_TYPE ");
 
		return queryP2PTrans.toString();
	}

}
