package com.btsl.voms.vomsreport.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomsreport.web.VomsReportForm;

public class VomsRptPostgresQry implements VomsRptQry {
	private Log _log = LogFactory.getLog(this.getClass().getName());
	//private String className = "VomsRptOracleQry";
	
@Override
	public PreparedStatement getVCDetailQuery(Connection con,VomsReportForm vomsReportForm) {
		
	final String methodName ="getVCDetailQuery";
	 StringBuilder selectQueryBuff = new StringBuilder(
			"SELECT  vdrd.serial_no , u.msisdn,");
	selectQueryBuff
			.append("TO_CHAR(").append(DownloadCSVReports.getDateForCalendarType("vv.LAST_CONSUMED_ON"))
			.append(" ) LAST_CONSUMED_ON ,");
	
	selectQueryBuff
	.append("TO_CHAR(").append(DownloadCSVReports.getDateForCalendarType("vdrd.DELIVER_DATE "))
	.append(" ) DELIVER_DATE ,");
	
	selectQueryBuff.append(" (CASE WHEN (vdrd.voucher_status = 'SL' and vv.CURRENT_STATUS = 'CU') then 'CONSUMED' WHEN  (vdrd.voucher_status = 'SL' and vv.CURRENT_STATUS != 'CU') then 'SOLD' end) voucher_status ,") ;
	
	selectQueryBuff.append("  u.firstname ,vv.mrp/ ")
	.append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(" mrp,");
	selectQueryBuff
			.append("TO_CHAR(").append(DownloadCSVReports.getDateForCalendarType(" vdrd.credit_sale_date"))
			.append(" ) credit_sale_date , vdrd.branch_code, vdrd.payment_id_number, vdrd.txn_id, vdrd.card_number, vdrd.terminal_code, vdrd.terminal_type");		
	
	selectQueryBuff.append(
			" FROM users u, voms_vouchers vv, voms_daily_report_details vdrd ");
	
	selectQueryBuff.append(
			" WHERE vdrd.user_id = u.user_id AND vv.serial_no = vdrd.serial_no and vdrd.SERIAL_NO between ? and ? ");
	
	 String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmt = null;
	 
	try {
		if (_log.isDebugEnabled()) {
           _log.debug(methodName,sqlSelect);	          
         	
		}			
        pstmt = con.prepareStatement(sqlSelect);
		int i= 0;
		pstmt.setString(++i, vomsReportForm.getFromSerial());
		pstmt.setString(++i, vomsReportForm.getToSerial());			
        
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	 
	return pstmt;			
	}

@Override
public PreparedStatement getSoldVCCardQuery(Connection con, VomsReportForm vomsReportForm) {
	final String methodName ="getSoldVCCardQuery";
	StringBuilder selectQueryBuff = new StringBuilder(
			"SELECT rownum, vdrd.serial_no , u.msisdn,");
	selectQueryBuff
			.append("TO_CHAR(").append(DownloadCSVReports.getDateForCalendarType("vv.LAST_CONSUMED_ON"))
			.append(" ) LAST_CONSUMED_ON ,");
	
	selectQueryBuff
	.append("TO_CHAR(").append(DownloadCSVReports.getDateForCalendarType("vdrd.DELIVER_DATE "))
	.append(" ) DELIVER_DATE ,");
	
	selectQueryBuff.append(" (CASE WHEN (vdrd.voucher_status = 'SL' and vv.CURRENT_STATUS = 'CU') then 'CONSUMED' WHEN  (vdrd.voucher_status = 'SL' and vv.CURRENT_STATUS != 'CU') then 'SOLD' end) voucher_status ,") ;
	
	selectQueryBuff.append("  u.firstname ,vv.mrp/ ")
	.append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(" mrp,");
	selectQueryBuff
			.append("TO_CHAR(").append(DownloadCSVReports.getDateForCalendarType(" vdrd.credit_sale_date"))
			.append(" ) credit_sale_date , vdrd.branch_code, vdrd.payment_id_number, vdrd.txn_id, vdrd.card_number, vdrd.terminal_code, vdrd.terminal_type");		
	
	selectQueryBuff.append(
			" FROM users u, voms_vouchers vv, voms_daily_report_details vdrd ");
	
	selectQueryBuff.append(
			" WHERE vdrd.user_id = u.user_id AND vv.serial_no = vdrd.serial_no ");
	
	
	selectQueryBuff.append(" and vdrd.credit_sale_date >= to_date( ? ) and vdrd.credit_sale_date <= to_date( ? ) " );
	
	
	
	if (!BTSLUtil.isNullString(vomsReportForm.getDomainCode()) && !BTSLUtil.isNullString(vomsReportForm.getUserID() ))
	{
		selectQueryBuff.append(" and vdrd.user_id = ? ");
	}
	
	
	if (! BTSLUtil.isNullString( vomsReportForm.getMrp() ))
	{
		selectQueryBuff.append(" and ( vv.mrp/ ").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(" ) = ?");
	}
	
	if (! BTSLUtil.isNullString(vomsReportForm.getType() ))
	{
	selectQueryBuff.append(" and vdrd.terminal_type = ? ");
	}
	
	if (! BTSLUtil.isNullString(vomsReportForm.getVoucherStatus() ))
	{
	selectQueryBuff.append(" and vdrd.voucher_status = ? ");
	}
	
	
	 String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmt = null;
	 
	try {
		if (_log.isDebugEnabled()) {
         _log.debug(methodName,sqlSelect);	          
       	
		}			
      pstmt = con.prepareStatement(sqlSelect);
      int i= 0;
      Date fromdt= null;
      Date todt = null;
      
      try {
		fromdt = BTSLUtil.getDateFromDateString(vomsReportForm.getFromDate(), "MM/dd/yyyy" );
		todt = BTSLUtil.getDateFromDateString(vomsReportForm.getToDate(), "MM/dd/yyyy" );
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		_log.errorTrace(methodName, e);
	}
      
      if (_log.isDebugEnabled()) {
  		_log.debug(methodName, " Entered with 4 : fromdt " + fromdt + " todt " + todt);
  	}
      
      
      pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(fromdt));
      pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(todt));
      
      
      if (!BTSLUtil.isNullString(vomsReportForm.getDomainCode()) &&! BTSLUtil.isNullString( vomsReportForm.getUserID() ))
		pstmt.setString(++i, vomsReportForm.getUserID());
      
      if (! BTSLUtil.isNullString( vomsReportForm.getMrp() ))
		pstmt.setString(++i, vomsReportForm.getMrp());			
      
      if (! BTSLUtil.isNullString( vomsReportForm.getType() ))
  		pstmt.setString(++i, vomsReportForm.getType());
      

      if (! BTSLUtil.isNullString( vomsReportForm.getVoucherStatus() ))
  		pstmt.setString(++i, vomsReportForm.getVoucherStatus());	
      
      
      
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	 
	return pstmt;		
}

@Override
public PreparedStatement getVCDeliveryHistoryQuery(Connection con, VomsReportForm thisForm) {

	final String methodName ="getVCDeliveryHistoryQuery";
	
	StringBuilder selectQueryBuff = new StringBuilder("select ").append( DownloadCSVReports.getDateForCalendarType("CTRF.transfer_date")).append(" transfer_date, ")
			.append(DownloadCSVReports.getDateForCalendarType("VB.created_date")).append(" create_date , ");
	selectQueryBuff.append(" U.user_name user_name,VP.MRP / ")
	.append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(" denomination ," );
	selectQueryBuff.append(" VB.TOTAL_NO_OF_VOUCHERS Quantity,VB.FROM_SERIAL_NO,VB.TO_SERIAL_NO ");
	selectQueryBuff.append(" FROM CHANNEL_TRANSFERS CTRF, USERS U,LOOKUPS L, LOOKUPS L1,LOOKUPS L2,DOMAINS D, CATEGORIES C,voms_batches VB,voms_products VP ");
	selectQueryBuff.append(" WHERE CTRF.TYPE = 'O2C' AND CTRF.close_date>= ? AND CTRF.close_date<= ?");
	selectQueryBuff.append(" AND CTRF.network_code = ? AND CTRF.transfer_id=vb.ext_txn_no(+) AND CTRF.domain_code IN ( '")
			.append(thisForm.getDomainListString()).append("') AND CTRF.domain_code = D.domain_code");
	selectQueryBuff.append(" AND CTRF.transfer_sub_type='V' AND CTRF.receiver_category_code = CTRF.receiver_category_code ")
	.append("AND CTRF.to_user_id = CASE ?  WHEN 'ALL' THEN CTRF.to_user_id ELSE ? END AND U.user_id(+) = CASE CTRF.to_user_id WHEN 'OPT' THEN '' ELSE CTRF.to_user_id END");
	selectQueryBuff.append(" AND C.category_code = CTRF.receiver_category_code AND CTRF.transfer_category=L2.lookup_code  AND L2.lookup_type='TRFTY' ")
	.append("AND L.lookup_type(+) ='TRFT' AND L.lookup_code(+) = CTRF.transfer_sub_type AND CTRF.status = 'CLOSE' AND L1.lookup_code(+) = CTRF.status AND L1.lookup_type(+) = 'CTSTA' ");
	selectQueryBuff.append(" AND vb.product_id=vp.product_id(+) AND CTRF.grph_domain_code IN (SELECT GD1.grph_domain_code FROM  GEOGRAPHICAL_DOMAINS GD1 ");
	selectQueryBuff.append(" WHERE GD1.status IN('Y','S') CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code");
	selectQueryBuff.append(" START WITH GD1.grph_domain_code IN (SELECT UG1.grph_domain_code");
	selectQueryBuff.append(" FROM USER_GEOGRAPHIES UG1 WHERE UG1.grph_domain_code = CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END");
	selectQueryBuff.append(" AND UG1.user_id=?)) ORDER BY 1 desc");	
	
	
	String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmtSelect = null;
	try {
		if(_log.isDebugEnabled())
		{
			_log.debug(methodName, " "+ Constants.getProperty("report.systemdatetime.format")+" , "+ BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getFromDateTime())+" , "+ BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getToDateTime()));
			_log.debug(methodName, "Select Query: "+sqlSelect);
		}

		pstmtSelect = con.prepareStatement(sqlSelect);
			int i= 0;
			pstmtSelect.setTimestamp(++i, BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getFromDateTime()));
			pstmtSelect.setTimestamp(++i, BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getToDateTime()));
			pstmtSelect.setString(++i, thisForm.getNetworkCode());
			pstmtSelect.setString(++i, thisForm.getUserID());
			pstmtSelect.setString(++i, thisForm.getUserID());
			pstmtSelect.setString(++i, thisForm.getZoneCode());
			pstmtSelect.setString(++i, thisForm.getZoneCode());
			pstmtSelect.setString(++i, thisForm.getLoginUserID());	
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	 
	return pstmtSelect;			
	
}

@Override
public PreparedStatement getVCSoldSummaryCityQuery(Connection con, VomsReportForm thisForm) {
	final String methodName ="getVCSoldSummaryCityQuery";
	StringBuilder selectQueryBuff = new StringBuilder("select * from ( SELECT VDD.city_code,VDD.PRODUCT_ID pd,VP.MRP ")
		.append(" FROM voms_daily_report_details VDD,voms_products VP ")
		.append(" WHERE VDD.PRODUCT_ID = VP.PRODUCT_ID ");	
	selectQueryBuff.append(" AND VDD.CREDIT_SALE_DATE >= TO_DATE( ? ) ");
	selectQueryBuff.append(" AND VDD.CREDIT_SALE_DATE <= TO_DATE( ? ) ");
	selectQueryBuff.append(" AND VDD.VOUCHER_STATUS= ? ");
	selectQueryBuff.append(" ) pivot ( ")
		.append(" count(pd) for mrp in ( " +thisForm.getProductMRP()+ "))")	
		.append(" order by city_code ");
	String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmtSelect = null;
	try {
		if(_log.isDebugEnabled())
		{
			_log.debug(methodName, "Select Query: "+sqlSelect);
		}

		pstmtSelect = con.prepareStatement(sqlSelect);
		int i= 0;
		Date fromdt= null;
	    Date todt = null;
	    ResultSet rs = null;
	      try {
			fromdt = BTSLUtil.getDateFromDateString(thisForm.getFromDate(), "dd/MM/yyyy" );
			todt = BTSLUtil.getDateFromDateString(thisForm.getToDate(), "dd/MM/yyyy" );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			_log.errorTrace(methodName, e);
		}
	      
	      if (_log.isDebugEnabled()) {
	  		_log.debug(methodName, " : fromdt " + fromdt + " todt " + todt);
	  	}
	      
		pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(fromdt));
		pstmtSelect.setDate(++i,  BTSLUtil.getSQLDateFromUtilDate(todt));
		pstmtSelect.setString(++i,  PretupsI.VOUCHER_SOLD_STATUS);
		
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	return pstmtSelect;		
}

@Override
public PreparedStatement getVCSoldSummaryChannelQuery(Connection con, VomsReportForm thisForm) {
	final String methodName ="getVCSoldSummaryChannelQuery";
	
	StringBuilder selectQueryBuff = new StringBuilder("select * from ( SELECT U.user_name as bank_name,L.lookup_name as channel_name,VDD.PRODUCT_ID pd,VP.MRP ")
		.append(" FROM voms_daily_report_details VDD,voms_products VP,users U,LOOKUPS L ")
		.append(" WHERE VDD.PRODUCT_ID = VP.PRODUCT_ID ");	
	selectQueryBuff.append("AND VDD.CREDIT_SALE_DATE >=  TO_DATE( ? ) ")
		.append("AND VDD.CREDIT_SALE_DATE <= TO_DATE( ? ) ");
	selectQueryBuff.append(" AND VDD.VOUCHER_STATUS= ? ");
	selectQueryBuff.append(" AND VDD.user_id=U.user_id AND  U.status <> ? ")
		.append(" AND VDD.TERMINAL_TYPE=L.LOOKUP_CODE AND L.LOOKUP_TYPE= 'TTYPE' ");
	selectQueryBuff.append(" ) pivot ( ")
		.append(" count(pd) for mrp in ( " +thisForm.getProductMRP()+ "))")	
		.append(" order by bank_name,channel_name ");
	String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmtSelect = null;
	try {
		if(_log.isDebugEnabled())
		{
			_log.debug(methodName, "Select Query: "+sqlSelect);
		}

		pstmtSelect = con.prepareStatement(sqlSelect);
		int i= 0;
		Date fromdt= null;
	    Date todt = null;
	      
	      try {
			fromdt = BTSLUtil.getDateFromDateString(thisForm.getFromDate(), "dd/MM/yyyy" );
			todt = BTSLUtil.getDateFromDateString(thisForm.getToDate(), "dd/MM/yyyy" );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			_log.errorTrace(methodName, e);
		}
	      
	      if (_log.isDebugEnabled()) {
	  		_log.debug(methodName, " : fromdt " + fromdt + " todt " + todt);
	  	}
	      
		pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(fromdt));
		pstmtSelect.setDate(++i,  BTSLUtil.getSQLDateFromUtilDate(todt));
		pstmtSelect.setString(++i,  PretupsI.VOUCHER_SOLD_STATUS);
		pstmtSelect.setString(++i, PretupsI.USER_STATUS_DELETED);
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	return pstmtSelect;		
  }
@Override
public PreparedStatement getVCSoldSummarySoldDateQuery(Connection con, VomsReportForm thisForm) {
	final String methodName ="getVCSoldSummarySoldDateQuery";
	
	StringBuilder selectQueryBuff = new StringBuilder("select * from ( SELECT VDD.credit_sale_Date,VDD.PRODUCT_ID pd,VP.MRP ")
		.append(" FROM voms_daily_report_details VDD,voms_products VP ")
		.append(" WHERE VDD.PRODUCT_ID = VP.PRODUCT_ID ");	
	selectQueryBuff.append(" AND VDD.CREDIT_SALE_DATE >=  TO_DATE( ? ) ");
	selectQueryBuff.append(" AND VDD.CREDIT_SALE_DATE <=  TO_DATE( ? ) ");
	selectQueryBuff.append(" AND VDD.VOUCHER_STATUS= ? ");
	selectQueryBuff.append(" ) pivot ( ")
		.append(" count(pd) for mrp in ( " +thisForm.getProductMRP()+ "))")	
		.append(" order by credit_sale_Date ");
	String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmtSelect = null;
	try {
		if(_log.isDebugEnabled())
		{
			_log.debug(methodName, "Select Query: "+sqlSelect);
		}

		pstmtSelect = con.prepareStatement(sqlSelect);
		int i= 0;
		Date fromdt= null;
	    Date todt = null;
	      
	      try {
			fromdt = BTSLUtil.getDateFromDateString(thisForm.getFromDate(), "dd/MM/yyyy" );
			todt = BTSLUtil.getDateFromDateString(thisForm.getToDate(), "dd/MM/yyyy" );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			_log.errorTrace(methodName, e);
		}
	      
	      if (_log.isDebugEnabled()) {
	  		_log.debug(methodName, " : fromdt " + fromdt + " todt " + todt);
	  	}
	      
		pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(fromdt));
		pstmtSelect.setDate(++i,  BTSLUtil.getSQLDateFromUtilDate(todt));
		pstmtSelect.setString(++i,  PretupsI.VOUCHER_SOLD_STATUS);
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	return pstmtSelect;		
  }

@Override
public PreparedStatement getVCSoldSummaryBankQuery(Connection con, VomsReportForm thisForm) {
	final String methodName ="getVCSoldSummarySoldDateQuery";
	
	StringBuilder selectQueryBuff = new StringBuilder("select * from ( SELECT U.user_name as bank_name,VDD.PRODUCT_ID pd,VP.MRP ")
		.append(" FROM voms_daily_report_details VDD,voms_products VP,users U ")
		.append(" WHERE VDD.PRODUCT_ID = VP.PRODUCT_ID ");	
	selectQueryBuff.append(" AND VDD.CREDIT_SALE_DATE >=  TO_DATE( ? ) ");
	selectQueryBuff.append(" AND VDD.CREDIT_SALE_DATE <=  TO_DATE( ? ) ");
	selectQueryBuff.append(" AND VDD.VOUCHER_STATUS= ? ")
		.append(" AND VDD.user_id=U.user_id AND  U.status <> ? ");
	selectQueryBuff.append(" ) pivot ( ")
		.append(" count(pd) for mrp in ( " +thisForm.getProductMRP()+ "))")	
		.append(" order by bank_name ");
	String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmtSelect = null;
	try {
		if(_log.isDebugEnabled())
		{
			_log.debug(methodName, "Select Query: "+sqlSelect);
		}

		pstmtSelect = con.prepareStatement(sqlSelect);
		int i= 0;
		Date fromdt= null;
	    Date todt = null;
	      
	      try {
			fromdt = BTSLUtil.getDateFromDateString(thisForm.getFromDate(), "dd/MM/yyyy" );
			todt = BTSLUtil.getDateFromDateString(thisForm.getToDate(), "dd/MM/yyyy" );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			_log.errorTrace(methodName, e);
		}
	      
	      if (_log.isDebugEnabled()) {
	  		_log.debug(methodName, " : fromdt " + fromdt + " todt " + todt);
	  	}
	      
		pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(fromdt));
		pstmtSelect.setDate(++i,  BTSLUtil.getSQLDateFromUtilDate(todt));
		pstmtSelect.setString(++i,  PretupsI.VOUCHER_SOLD_STATUS);
		pstmtSelect.setString(++i, PretupsI.USER_STATUS_DELETED);
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	return pstmtSelect;		
  }

@Override
public PreparedStatement getStatisticsRechargeQuery(Connection con,VomsReportForm vomsReportForm) {
	
	final String methodName ="getStatisticsRechargeQuery";
	 StringBuilder selectQueryBuff = new StringBuilder(
			"SELECT  ");
	selectQueryBuff
			.append("TO_CHAR(").append(DownloadCSVReports.getDateForCalendarType("vv.LAST_CONSUMED_ON"))
			.append(" ) LAST_CONSUMED_ON ,");
	
	selectQueryBuff.append(" vv.consumed_gateway_code, vv.generation_batch_no, vv.user_network_code, vv.mrp, COUNT (vv.generation_batch_no) AS numberofrecharge, (vv.mrp * COUNT (vv.generation_batch_no)) AS totalrecharge , ");
	selectQueryBuff.append(" (CASE WHEN vv.VOUCHER_TYPE = 'PLI'  then 'Physical' WHEN vv.VOUCHER_TYPE = 'PLI'  then 'Physical' WHEN vv.VOUCHER_TYPE = 'TPLI'  then 'On demand Physical' WHEN  vv.VOUCHER_TYPE = 'TELV'  then 'On demand  Electronics'  end) voucher_type    ") ;
	selectQueryBuff.append( " FROM voms_vouchers vv ");
	
	selectQueryBuff.append( " WHERE vv.status = 'CU' AND vv.voucher_type = ? ");
	//selectQueryBuff.append(  " and  TO_CHAR (vv.first_consumed_on, 'dd/MM/yy') >=  ? ");
	//selectQueryBuff.append(  " and  TO_CHAR (vv.first_consumed_on, 'dd/MM/yy') <=  ? "); 
	
	 
	
	 selectQueryBuff.append(  " and vv.first_consumed_on  >=   TO_DATE( ? )  ");
	 selectQueryBuff.append(  " and vv.first_consumed_on  <=   TO_DATE( ? )  "); 
	 
	 
	selectQueryBuff.append( " GROUP BY vv.generation_batch_no, vv.user_network_code,vv.mrp,vv.consumed_gateway_code,vv.generation_batch_no, TO_CHAR (TO_CHAR (vv.last_consumed_on, 'yyyy/mm/dd', 'nls_calendar=persian' )) ,voucher_type  ) ");
	
	 String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmt = null;
	 
	try {
		if (_log.isDebugEnabled()) {
            _log.debug(methodName,sqlSelect);	          
          	
		}
		
		Date fromdt= null;
	    Date todt = null;
	    
	    
	    if (_log.isDebugEnabled()) {
	  		_log.debug(methodName, " : passed fromdt " + vomsReportForm.getFromDate() + " passed todt " + vomsReportForm.getToDate() );
	  	}
	      
	      try {
			fromdt = BTSLUtil.getDateFromDateString(vomsReportForm.getFromDate(), "MM/dd/yyyy" );
			todt = BTSLUtil.getDateFromDateString(vomsReportForm.getToDate(), "MM/dd/yyyy" );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			 _log.errorTrace(methodName, e);
		}
	      
	      if (_log.isDebugEnabled()) {
	  		_log.debug(methodName, " : fromdt " + fromdt + " todt " + todt);
	  	}
		
         pstmt = con.prepareStatement(sqlSelect);
		int i= 0;
		pstmt.setString(++i, vomsReportForm.getVoucherType());
		
		
		
		pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(fromdt));
		pstmt.setDate(++i,  BTSLUtil.getSQLDateFromUtilDate(todt));
		
         
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	 
	return pstmt;			
}

@Override
public PreparedStatement getStatisticsServiceQuery(Connection con,VomsReportForm vomsReportForm) {
	
	final String methodName ="getStatisticsServiceQuery";
	 StringBuilder selectQueryBuff = new StringBuilder("select ").append( DownloadCSVReports.getDateForCalendarType("st.transfer_date")).append(" transfer_date, ");
			 selectQueryBuff.append(" COUNT (*) AS totalrecharge,");
	
	selectQueryBuff.append(" SUM (CASE WHEN st.transfer_status = '200' THEN 1 ELSE 0 END ) totalsuccessrecharge,  ");
	selectQueryBuff.append(" ( (SUM (CASE WHEN st.transfer_status = '200' THEN 1 ELSE 0 END))/COUNT (*))*100 as percentage  ") ;
	selectQueryBuff.append( " FROM subscriber_transfers st ");
	
	selectQueryBuff.append(  " where st.TRANSFER_DATE   >=   TO_DATE( ? )  ");
	selectQueryBuff.append(  " and st.TRANSFER_DATE   <=   TO_DATE( ? )  "); 
	selectQueryBuff.append( " GROUP BY st.transfer_date   ");
	
	 String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmt = null;
	 
	try {
		if (_log.isDebugEnabled()) {
            _log.debug(methodName,sqlSelect);	          
          	
		}
		
		Date fromdt= null;
	    Date todt = null;
	    
	    
	    if (_log.isDebugEnabled()) {
	  		_log.debug(methodName, " : passed fromdt " + vomsReportForm.getFromDate() + " passed todt " + vomsReportForm.getToDate() );
	  	}
	      
	      try {
			fromdt = BTSLUtil.getDateFromDateString(vomsReportForm.getFromDate(), "MM/dd/yyyy" );
			todt = BTSLUtil.getDateFromDateString(vomsReportForm.getToDate(), "MM/dd/yyyy" );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			 _log.errorTrace(methodName, e);
		}
	      
	      if (_log.isDebugEnabled()) {
	  		_log.debug(methodName, " : fromdt " + fromdt + " todt " + todt);
	  	}
		
         pstmt = con.prepareStatement(sqlSelect);
		int i= 0;
		
		
		pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(fromdt));
		pstmt.setDate(++i,  BTSLUtil.getSQLDateFromUtilDate(todt));
		
         
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	 
	return pstmt;			
}

@Override
public PreparedStatement getVCStatisticsQuery(Connection con, VomsReportForm thisForm) {
	final String methodName ="getVCDeliveryHistoryQuery";
	
	
	StringBuilder selectQueryBuff = new StringBuilder("SELECT ").append( DownloadCSVReports.getDateForCalendarType("V.summary_date")).append(" summary_date, ").append(" T.name VNAME, V.PRODUCTION_NETWORK_CODE NWCODE,");
	selectQueryBuff.append("V.denomination/ ")
			.append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(" denomination,");
	selectQueryBuff.append("V.TOTAL_GENERATED GENCOUNT, V.TOTAL_ON_HOLD OHCOUNT, V.TOTAL_ENABLED ENCOUNT, V.TOTAL_SUSPENDED SCOUNT ,V.TOTAL_EXPIRED  EXCOUNT, V.TOTAL_STOLEN  STCOUNT , V.TOTAL_DAMAGED DACOUNT , V.TOTAL_CONSUMED CUCOUNT, V.TOTAL_WAREHOUSE WHCOUNT, V.TOTAL_PRINTING PRCOUNT,V.voucher_type VTYPE,V.product_id");
	selectQueryBuff.append(" FROM VOMS_VOUCHER_DAILY_SUMMARY V, VOMS_TYPES T ");
	selectQueryBuff.append(" WHERE  V.summary_date >=?")
	.append(" AND V.summary_date <=?");
	selectQueryBuff.append(" AND T.voucher_type=V.voucher_type");
	selectQueryBuff.append(" ORDER BY V.summary_date");
	
	String sqlSelect=selectQueryBuff.toString();
	 PreparedStatement pstmtSelect = null;
	try {
		if(_log.isDebugEnabled())
		{
			_log.debug(methodName, " "+ Constants.getProperty("report.systemdatetime.format")+" , "+ BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getFromDateTime())+" , "+ BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getToDateTime()));
			_log.debug(methodName, "Select Query: "+sqlSelect);
		}
				
		pstmtSelect = con.prepareStatement(sqlSelect);
			int i= 0;
			pstmtSelect.setTimestamp(++i, BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getFromDateTime()));
			pstmtSelect.setTimestamp(++i, BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getToDateTime()));
	
	} catch (SQLException e) {
		 _log.errorTrace(methodName, e);
	} 	 		 
	 
	return pstmtSelect;		
}

}
