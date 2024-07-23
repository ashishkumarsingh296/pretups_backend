package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.C2STransferRptQry;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportForm;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public class C2STransferDetailsOracleQry implements C2STransferRptQry{

	private Log log = LogFactory.getLog(this.getClass().getName());	
	private int i=1;	
	private static final String DATEPATTERN = "report.dateformat";
	private static final String DATETIMEPATTERN = "report.datetimeformat";
	private static final String FROMDATETIME = "FromDateTime";
	private static final String TODATETIME = "ToDateTime";	
	private static final String TRANSFERSTATUS = "TransferStatus";
	private static final String SERVICETYPE = "ServiceType";
	private static final String ZONECODE= "ZoneCode";
	private static final String DOMAINCODE= "DomainCode";
	private static final String PARENTCATEGORYCODE= "ParentCategoryCode";
	private static final String LOGINUSERID= "LoginUserID";
	private static final String NETWORKCODE= "NetworkCode";
	private static final String USERID= "UserId";
	
	@Override
	public PreparedStatement loadC2sTransferChannelUserReport(Connection con,UsersReportModel usersReportModel) {
		
		final String methodName ="loadC2sTransferChannelUserReport";
		 final StringBuilder strBuff = new StringBuilder(); 		
		 strBuff.append("SELECT CT.transfer_status status, CT.transfer_id, TO_CHAR(CT.transfer_date, ?)transfer_date, TO_CHAR(CT.transfer_date_time, ?)transfer_date_time, N.network_name, U.user_name, CT.transfer_value, KV.VALUE transfer_status, ST.NAME service_name, CT.sender_msisdn, L.selector_name subService_name, CT.receiver_transfer_value, CT.receiver_access_fee, CT.receiver_bonus_value, CT.request_gateway_type,CT.error_code,CTI.service_class_id,SC.service_class_name,CT.RECEIVER_MSISDN, SMA.USER_SID FROM C2S_TRANSFERS_OLD CT, NETWORKS N, KEY_VALUES KV, SERVICE_TYPE ST, SERVICE_TYPE_SELECTOR_MAPPING L, USERS U, CATEGORIES CAT, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD ,C2S_TRANSFER_ITEMS CTI ,SERVICE_CLASSES SC,SUBSCRIBER_MSISDN_ALIAS SMA WHERE CTI.transfer_id=CT.transfer_id AND CTI.sno=2 AND SC.service_class_id(+)=CTI.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.KEY AND KV.TYPE = 'C2S_STATUS' AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.SERVICE_TYPE = ST.SERVICE_TYPE AND CT.SERVICE_TYPE = CASE ? WHEN 'ALL' THEN CT.SERVICE_TYPE ELSE ? END AND CT.sub_service = L.selector_code AND CT.SERVICE_TYPE = L.SERVICE_TYPE AND CT.network_code = ? AND CT.transfer_date >= ? AND CT.transfer_date <= ? AND CT.sender_id IN(SELECT U11.user_id FROM USERS U11 WHERE U11.user_id=CASE ? WHEN 'ALL' THEN U11.user_id ELSE ? END CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?) AND CT.sender_id = U.user_id AND CAT.category_code = CASE ? WHEN 'ALL' THEN CAT.category_code ELSE ? END AND CAT.category_code = U.category_code AND CAT.sequence_no >= ? AND CAT.domain_code = ? AND U.user_id = UG.user_id AND CT.RECEIVER_MSISDN=SMA.MSISDN(+) AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id=?)) ORDER BY CT.transfer_date_time Desc");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 
		try {
			if (log.isDebugEnabled()) {
                log.debug("loadC2sTransferChannelUserReport",sqlSelect);	          
                log.debug(FROMDATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));				
				log.debug(TODATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));					           	          
	            log.debug(TRANSFERSTATUS,usersReportModel.getTransferStatus());
	            log.debug(SERVICETYPE,usersReportModel.getServiceType());
	            log.debug(ZONECODE,usersReportModel.getZoneCode());
	            log.debug(DOMAINCODE,usersReportModel.getDomainCode());
	            log.debug(PARENTCATEGORYCODE,usersReportModel.getParentCategoryCode());
	            log.debug(LOGINUSERID,usersReportModel.getLoginUserID());
	            log.debug(NETWORKCODE,usersReportModel.getNetworkCode());	
	            log.debug(USERID,usersReportModel.getUserID());
            }			
             pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(DATEPATTERN));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getNetworkCode());
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));     			 
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));			 
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getLoginUserID());			 
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getCategorySeqNo()); 
			 pstmt.setString(i++, usersReportModel.getDomainCode());			 
			 pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());				
	         
		} catch (SQLException |ParseException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmt;			
	}

	@Override
	public PreparedStatement loadC2sTransferChannelUserStaffReport(	Connection con, UsersReportModel usersReportModel) {
		
		final String methodName ="loadC2sTransferChannelUserStaffReport";
		 final StringBuilder strBuff = new StringBuilder(); 		
		 strBuff.append("SELECT CT.transfer_status status, CT.transfer_id, TO_CHAR(CT.transfer_date, ?)transfer_date, TO_CHAR(CT.transfer_date_time, ?)transfer_date_time, N.network_name, U.user_name, UC.user_name initiator_user, CT.transfer_value, KV.value transfer_status, ST.name service_name, CT.receiver_msisdn, CT.sender_msisdn, L.selector_name subService_name, CT.receiver_transfer_value, CT.receiver_access_fee, CT.receiver_bonus_value, CT.request_gateway_type,CT.error_code,CTI.service_class_id,SC.service_class_name FROM c2s_transfers_old CT, networks N, key_values KV, service_type ST, service_type_selector_mapping L, users U, users UC, categories CAT, user_geographies UG, geographical_domains GD ,c2s_transfer_items CTI ,service_classes SC WHERE CTI.transfer_id=CT.transfer_id AND CTI.sno=2 AND SC.service_class_id(+)=CTI.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.key AND KV.type = 'C2S_STATUS' AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.service_type = ST.service_type AND CT.service_type = case ? when 'ALL' then CT.service_type else ? end AND CT.sub_service = L.selector_code AND CT.service_type = L.service_type AND CT.network_code = ? AND CT.transfer_date >= ? AND CT.transfer_date <= ? AND CT.sender_id IN(SELECT U11.user_id FROM users U11 WHERE U11.user_id=CASE ? WHEN 'ALL' THEN U11.user_id ELSE ? END CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?) AND CT.sender_id = U.user_id AND CAT.category_code = case ? when 'ALL' then CAT.category_code else ? end AND CAT.category_code = U.category_code AND CAT.sequence_no >= ? AND CAT.domain_code = ? AND U.user_id = UG.user_id AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =case ? when 'ALL' then UG1.grph_domain_code else ? end AND UG1.user_id=?)) ORDER BY CT.transfer_date_time desc");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 
		try {
			if (log.isDebugEnabled()) {
             log.debug("loadC2sTransferChannelUserStaffReport",sqlSelect);	          
             log.debug(FROMDATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));				
				log.debug(TODATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));					           	          
	            log.debug(TRANSFERSTATUS,usersReportModel.getTransferStatus());
	            log.debug(SERVICETYPE,usersReportModel.getServiceType());
	            log.debug(ZONECODE,usersReportModel.getZoneCode());
	            log.debug(DOMAINCODE,usersReportModel.getDomainCode());
	            log.debug(PARENTCATEGORYCODE,usersReportModel.getParentCategoryCode());
	            log.debug(LOGINUSERID,usersReportModel.getLoginUserID());
	            log.debug(NETWORKCODE,usersReportModel.getNetworkCode());	
	            log.debug(USERID,usersReportModel.getUserID());
           }
			 pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(DATEPATTERN));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getNetworkCode());
			 pstmt.setTimestamp(i++, usersReportModel.getFromDateTimestamp()); 
			 pstmt.setTimestamp(i++, usersReportModel.getToDateTimestamp()); 					 
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getLoginUserID());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setInt(i++, Integer.valueOf(usersReportModel.getCategorySeqNo())); 
			 pstmt.setString(i++, usersReportModel.getDomainCode());
			 pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());		
	         
		} catch (SQLException |ParseException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmt;			
	}

	@Override
	public PreparedStatement loadC2sTransferChannelUserNewReport(Connection con, UsersReportModel usersReportModel) {		
				
		 final String methodName ="loadC2sTransferChannelUserNewReport";
		 final StringBuilder strBuff = new StringBuilder();
		//local_index_missing
		 strBuff.append("SELECT CT.transfer_status status, CT.transfer_id, TO_CHAR(CT.transfer_date, ?)transfer_date, TO_CHAR(CT.transfer_date_time, ?) transfer_date_time,N.network_name, U.user_name, CT.transfer_value, KV.VALUE transfer_status, ST.NAME service_name, (Case when CT.subs_sid IS NULL then CT.receiver_msisdn else CT.subs_sid end) Reciever_MSISDN, CT.sender_msisdn, L.selector_name subService_name, CT.receiver_transfer_value, CT.receiver_access_fee, CT.receiver_bonus_value, CT.request_gateway_type,CT.error_code,CT.service_class_id,SC.service_class_name,UP.PRIMARY_NUMBER MSISDN_TYPE, CT.REVERSAL_ID FROM C2S_TRANSFERS CT, NETWORKS N, KEY_VALUES KV, SERVICE_TYPE ST, SERVICE_TYPE_SELECTOR_MAPPING L, USERS U, CATEGORIES CAT, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD ,SERVICE_CLASSES SC,USER_PHONES UP WHERE SC.service_class_id(+)=CT.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.KEY AND KV.TYPE = 'C2S_STATUS' AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.SERVICE_TYPE = ST.SERVICE_TYPE AND CT.SERVICE_TYPE = CASE ? WHEN 'ALL' THEN CT.SERVICE_TYPE ELSE ? END AND CT.sub_service = L.selector_code AND CT.SERVICE_TYPE = L.SERVICE_TYPE AND CT.network_code = ? AND CT.TRANSFER_DATE >= TRUNC(TO_DATE(?,?)) AND CT.TRANSFER_DATE <= TRUNC(TO_DATE(?,?)) AND CT.TRANSFER_DATE_TIME >= TO_DATE(?,?) AND CT.TRANSFER_DATE_TIME <= TO_DATE(?,?) AND CT.sender_id IN ( SELECT U11.user_id FROM users U11 WHERE U11.user_id=CASE ? WHEN 'ALL' THEN U11.user_id ELSE ? END CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=? ) AND CT.sender_id = U.user_id AND CAT.category_code = CASE ? WHEN 'ALL' THEN CAT.category_code ELSE ? END AND CAT.category_code = U.category_code AND CAT.domain_code = ? AND U.user_id = UG.user_id AND U.user_id=UP.USER_ID AND CT.SENDER_MSISDN=UP.MSISDN AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id=?)) ORDER BY CT.transfer_date_time Desc");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 
		try {
			if (log.isDebugEnabled()) {
                log.debug("loadC2sTransferChannelUserNewReport",sqlSelect);	          
                log.debug(FROMDATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));				
				log.debug(TODATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));					           	          
	            log.debug(TRANSFERSTATUS,usersReportModel.getTransferStatus());
	            log.debug(SERVICETYPE,usersReportModel.getServiceType());
	            log.debug(ZONECODE,usersReportModel.getZoneCode());
	            log.debug(DOMAINCODE,usersReportModel.getDomainCode());
	            log.debug(PARENTCATEGORYCODE,usersReportModel.getParentCategoryCode());
	            log.debug(LOGINUSERID,usersReportModel.getLoginUserID());
	            log.debug(NETWORKCODE,usersReportModel.getNetworkCode());	
	            log.debug(USERID,usersReportModel.getUserID());
          }
			 pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(DATEPATTERN));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getNetworkCode());
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));   
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));   
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getLoginUserID());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getDomainCode());
			 pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());				
	         
		} catch (SQLException |ParseException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmt;			
	}

	@Override
	public PreparedStatement loadC2sTransferChannelUserStaffNewReport(Connection con, UsersReportModel usersReportModel) {		
	
		 final String methodName ="loadC2sTransferChannelUserStaffNewReport";
		 final StringBuilder strBuff = new StringBuilder();
		 //local_index_missing
		 strBuff.append("SELECT CT.transfer_status status, CT.transfer_id, TO_CHAR(CT.transfer_date, ?)transfer_date, TO_CHAR(CT.transfer_date_time, ?) transfer_date_time, N.network_name, U.user_name, UC.user_name initiator_user, CT.transfer_value, KV.value transfer_status, ST.name service_name, CT.receiver_msisdn, CT.sender_msisdn, L.selector_name subService_name, CT.receiver_transfer_value, CT.receiver_access_fee, CT.receiver_bonus_value, CT.request_gateway_type,CT.error_code,CT.service_class_id,SC.service_class_name FROM c2s_transfers CT, networks N, key_values KV, service_type ST, service_type_selector_mapping L, users U, users UC, categories CAT, user_geographies UG, geographical_domains GD,service_classes SC WHERE SC.service_class_id(+)=CT.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.key AND KV.type = 'C2S_STATUS' AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.service_type = ST.service_type AND CT.service_type = case ? when 'ALL' then CT.service_type else ? end AND CT.sub_service = L.selector_code AND CT.service_type = L.service_type AND CT.network_code = ? AND CT.transfer_date >= ? AND CT.transfer_date <= ? AND CT.sender_id IN(SELECT U11.user_id FROM users U11 WHERE U11.user_id=CASE ? WHEN 'ALL' THEN U11.user_id ELSE ? END CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?) AND CT.sender_id = U.user_id AND CT.active_user_id=UC.user_id AND CAT.category_code = case ? when 'ALL' then CAT.category_code else ? end AND CAT.category_code = U.category_code AND CAT.sequence_no >= ? AND CAT.domain_code = ? AND U.user_id = UG.user_id AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =case ? when 'ALL' then UG1.grph_domain_code else ? end AND UG1.user_id=?)) ORDER BY CT.transfer_date_time desc");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 
		try {
			if (log.isDebugEnabled()) {
               log.debug("loadC2sTransferChannelUserStaffNewReport",sqlSelect);	          
               log.debug(FROMDATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));				
				log.debug(TODATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));					           	          
	            log.debug(TRANSFERSTATUS,usersReportModel.getTransferStatus());
	            log.debug(SERVICETYPE,usersReportModel.getServiceType());
	            log.debug(ZONECODE,usersReportModel.getZoneCode());
	            log.debug(DOMAINCODE,usersReportModel.getDomainCode());
	            log.debug(PARENTCATEGORYCODE,usersReportModel.getParentCategoryCode());
	            log.debug(LOGINUSERID,usersReportModel.getLoginUserID());
	            log.debug(NETWORKCODE,usersReportModel.getNetworkCode());	
	            log.debug(USERID,usersReportModel.getUserID());
         }
			 pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(DATEPATTERN));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getNetworkCode());
			 pstmt.setTimestamp(i++, usersReportModel.getFromDateTimestamp()); 
			 pstmt.setTimestamp(i++, usersReportModel.getToDateTimestamp()); 			 
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getLoginUserID());	
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setInt(i++, Integer.valueOf(usersReportModel.getCategorySeqNo())); 
			 pstmt.setString(i++, usersReportModel.getDomainCode());
			 pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());				
	         
		} catch (SQLException |ParseException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmt;			
	}

	@Override
	public PreparedStatement loadC2sTransferNewReport(Connection con,UsersReportModel usersReportModel) {
		
		 final String methodName ="loadC2sTransferNewReport";
		 final StringBuilder strBuff = new StringBuilder();
		 //local_index_missing
		 strBuff.append("SELECT CT.transfer_status status, CT.transfer_id, TO_CHAR(CT.transfer_date, ?)transfer_date,TO_CHAR(CT.transfer_date_time, ?) transfer_date_time, N.network_name, U.user_name, CT.transfer_value, KV.VALUE transfer_status, ST.NAME service_name, (Case when CT.subs_sid IS NULL then CT.receiver_msisdn else CT.subs_sid end) Reciever_MSISDN, CT.sender_msisdn, L.selector_name subService_name, CT.receiver_transfer_value, CT.receiver_access_fee, CT.receiver_bonus_value, CT.request_gateway_type,CT.error_code,CT.service_class_id,SC.service_class_name,UP.PRIMARY_NUMBER MSISDN_TYPE, CT.REVERSAL_ID, CT.MULTICURRENCY_DETAIL,U.external_code FROM C2S_TRANSFERS CT, NETWORKS N, KEY_VALUES KV, SERVICE_TYPE ST, SERVICE_TYPE_SELECTOR_MAPPING L, USERS U, CATEGORIES CAT, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD ,SERVICE_CLASSES SC,USER_PHONES UP WHERE SC.service_class_id(+)=CT.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.KEY AND KV.TYPE = 'C2S_STATUS' AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.SERVICE_TYPE = ST.SERVICE_TYPE AND CT.SERVICE_TYPE = CASE ? WHEN 'ALL' THEN CT.SERVICE_TYPE ELSE ? END AND CT.sub_service = L.selector_code AND CT.SERVICE_TYPE = L.SERVICE_TYPE AND CT.network_code = ? AND CT.TRANSFER_DATE >= TRUNC(TO_DATE(?,?)) AND CT.TRANSFER_DATE <= TRUNC(TO_DATE(?,?)) AND CT.TRANSFER_DATE_TIME >= TO_DATE(?,?) AND CT.TRANSFER_DATE_TIME <= TO_DATE(?,?) AND CT.sender_id = CASE ? WHEN 'ALL' THEN CT.sender_id ELSE ? END AND CT.sender_id = U.user_id AND CAT.category_code = CASE ? WHEN 'ALL' THEN CAT.category_code ELSE ? END AND CAT.category_code = U.category_code AND CAT.domain_code = ? AND U.user_id = UG.user_id AND U.user_id=UP.USER_ID AND CT.SENDER_MSISDN=UP.MSISDN AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id=?)) ORDER BY CT.transfer_date_time Desc");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 
		try {
			if (log.isDebugEnabled()) {
              log.debug("loadC2sTransferNewReport",sqlSelect);	          
              log.debug(FROMDATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));				
				log.debug(TODATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));					           	          
	            log.debug(TRANSFERSTATUS,usersReportModel.getTransferStatus());
	            log.debug(SERVICETYPE,usersReportModel.getServiceType());
	            log.debug(ZONECODE,usersReportModel.getZoneCode());
	            log.debug(DOMAINCODE,usersReportModel.getDomainCode());
	            log.debug(PARENTCATEGORYCODE,usersReportModel.getParentCategoryCode());
	            log.debug(LOGINUSERID,usersReportModel.getLoginUserID());
	            log.debug(NETWORKCODE,usersReportModel.getNetworkCode());	
	            log.debug(USERID,usersReportModel.getUserID());
        }
			 pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(DATEPATTERN));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getNetworkCode());
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));   
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));   
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getUserID());			 
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getDomainCode());
			 pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());		
	         
		} catch (SQLException |ParseException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmt;			
	}

	@Override
	public PreparedStatement loadC2sTransferReport(Connection con,UsersReportModel usersReportModel) {
		
		 final String methodName ="loadC2sTransferReport";
		 final StringBuilder strBuff = new StringBuilder(); 		
		 strBuff.append("SELECT CT.transfer_status status, CT.transfer_id, TO_CHAR(CT.transfer_date, ?)transfer_date,TO_CHAR(CT.transfer_date_time, ?)transfer_date_time, N.network_name, U.user_name, CT.transfer_value, KV.VALUE transfer_status, ST.NAME service_name, CT.receiver_msisdn, CT.sender_msisdn, L.selector_name subService_name, CT.receiver_transfer_value, CT.receiver_access_fee, CT.receiver_bonus_value, CT.request_gateway_type,CT.error_code,CTI.service_class_id,SC.service_class_name,UP.PRIMARY_NUMBER MSISDN_TYPE FROM C2S_TRANSFERS_OLD CT, NETWORKS N, KEY_VALUES KV, SERVICE_TYPE ST, SERVICE_TYPE_SELECTOR_MAPPING L, USERS U, CATEGORIES CAT, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD ,C2S_TRANSFER_ITEMS CTI ,SERVICE_CLASSES SC,USER_PHONES UP WHERE CTI.transfer_id(+)=CT.transfer_id AND CTI.sno(+)=2 AND SC.service_class_id(+)=CTI.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.KEY AND KV.TYPE = 'C2S_STATUS' AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.SERVICE_TYPE = ST.SERVICE_TYPE AND CT.SERVICE_TYPE = CASE ? WHEN 'ALL' THEN CT.SERVICE_TYPE ELSE ? END AND CT.sub_service = L.selector_code AND CT.SERVICE_TYPE = L.SERVICE_TYPE AND CT.network_code = ? AND CT.TRANSFER_DATE >= TRUNC(TO_DATE(?,?)) AND CT.TRANSFER_DATE <= TRUNC(TO_DATE(?,?)) AND CT.TRANSFER_DATE_TIME >= TO_DATE(?,?) AND CT.TRANSFER_DATE_TIME <= TO_DATE(?,?) AND CT.sender_id = CASE ? WHEN 'ALL' THEN CT.sender_id ELSE ? END AND CT.sender_id = U.user_id AND CAT.category_code = CASE ? WHEN 'ALL' THEN CAT.category_code ELSE ? END AND CAT.category_code = U.category_code AND CAT.domain_code = ? AND U.user_id = UG.user_id AND U.user_id=UP.USER_ID AND CT.SENDER_MSISDN=UP.MSISDN AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id=?)) order by CT.transfer_date_time desc");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 
		try {
			if (log.isDebugEnabled()) {
                log.debug("loadC2sTransferReport",sqlSelect);	          
                log.debug(FROMDATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));				
				log.debug(TODATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));					           	          
	            log.debug(TRANSFERSTATUS,usersReportModel.getTransferStatus());
	            log.debug(SERVICETYPE,usersReportModel.getServiceType());
	            log.debug(ZONECODE,usersReportModel.getZoneCode());
	            log.debug(DOMAINCODE,usersReportModel.getDomainCode());
	            log.debug(PARENTCATEGORYCODE,usersReportModel.getParentCategoryCode());
	            log.debug(LOGINUSERID,usersReportModel.getLoginUserID());
	            log.debug(NETWORKCODE,usersReportModel.getNetworkCode());	
	            log.debug(USERID,usersReportModel.getUserID());
       }
			 pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(DATEPATTERN));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getNetworkCode());
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));   
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));   
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getDomainCode());
			 pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());		
	         
		} catch (SQLException |ParseException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmt;			
	}

	@Override
	public PreparedStatement loadC2sTransferStaffNewReport(Connection con,UsersReportModel usersReportModel) {
		
		final String methodName ="loadC2sTransferStaffNewReport";
		 final StringBuilder strBuff = new StringBuilder();
		 //local_index_missing
		 strBuff.append("SELECT CT.transfer_status status, CT.transfer_id, TO_CHAR(CT.transfer_date, ?)transfer_date, TO_CHAR(CT.transfer_date_time, ?) transfer_date_time, N.network_name, U.user_name, UC.user_name initiator_user, CT.transfer_value, KV.value transfer_status, ST.name service_name, CT.receiver_msisdn, CT.sender_msisdn, L.selector_name subService_name, CT.receiver_transfer_value, CT.receiver_access_fee, CT.receiver_bonus_value, CT.request_gateway_type,CT.error_code,CT.service_class_id,SC.service_class_name FROM c2s_transfers CT, networks N, key_values KV, service_type ST, service_type_selector_mapping L, users U, users UC, categories CAT, user_geographies UG, geographical_domains GD,service_classes SC WHERE SC.service_class_id(+)=CT.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.key AND KV.type = 'C2S_STATUS' AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.service_type = ST.service_type AND CT.service_type = case ? when 'ALL' then CT.service_type else ? end AND CT.sub_service = L.selector_code AND CT.service_type = L.service_type AND CT.network_code = ? AND CT.transfer_date >= ? AND CT.transfer_date <= ? AND CT.sender_id = case ? when 'ALL' then CT.sender_id else ? end AND CT.active_user_id=UC.user_id AND CT.sender_id = U.user_id AND CAT.category_code = case ? when 'ALL' then CAT.category_code else ? end AND CAT.category_code = U.category_code AND CAT.domain_code = ? AND U.user_id = UG.user_id AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =case ? when 'ALL' then UG1.grph_domain_code else ? end AND UG1.user_id=?)) ORDER BY CT.transfer_date_time Desc");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 
		try {
			if (log.isDebugEnabled()) {
               log.debug("loadC2sTransferStaffNewReport",sqlSelect);	          
               log.debug(FROMDATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));				
				log.debug(TODATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));					           	          
	            log.debug(TRANSFERSTATUS,usersReportModel.getTransferStatus());
	            log.debug(SERVICETYPE,usersReportModel.getServiceType());
	            log.debug(ZONECODE,usersReportModel.getZoneCode());
	            log.debug(DOMAINCODE,usersReportModel.getDomainCode());
	            log.debug(PARENTCATEGORYCODE,usersReportModel.getParentCategoryCode());
	            log.debug(LOGINUSERID,usersReportModel.getLoginUserID());
	            log.debug(NETWORKCODE,usersReportModel.getNetworkCode());	
	            log.debug(USERID,usersReportModel.getUserID());
      }
			 pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(DATEPATTERN));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getNetworkCode());
			 pstmt.setTimestamp(i++, usersReportModel.getFromDateTimestamp()); 
			 pstmt.setTimestamp(i++, usersReportModel.getToDateTimestamp()); 				 
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getDomainCode());
			 pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());		
	         
		} catch (SQLException |ParseException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmt;			
	}

	@Override
	public PreparedStatement loadC2sTransferStaffReport(Connection con,	UsersReportModel usersReportModel) {		
		
		final String methodName ="loadC2sTransferStaffReport";
		 final StringBuilder strBuff = new StringBuilder(); 		
		 strBuff.append("SELECT CT.transfer_status status, CT.transfer_id, TO_CHAR(CT.transfer_date, ?)transfer_date, TO_CHAR(CT.transfer_date_time, ?) transfer_date_time, N.network_name, U.user_name, UC.user_name initiator_user, CT.transfer_value, KV.value transfer_status, ST.name service_name, CT.receiver_msisdn, CT.sender_msisdn, L.selector_name subService_name, CT.receiver_transfer_value, CT.receiver_access_fee, CT.receiver_bonus_value, CT.request_gateway_type,CT.error_code,CTI.service_class_id,SC.service_class_name FROM c2s_transfers_old CT, networks N, key_values KV, service_type ST, service_type_selector_mapping L, users U, users UC, categories CAT, user_geographies UG, geographical_domains GD ,c2s_transfer_items CTI ,service_classes SC WHERE CTI.transfer_id=CT.transfer_id AND CTI.sno=2 AND SC.service_class_id(+)=CTI.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.key AND KV.type = 'C2S_STATUS' AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.service_type = ST.service_type AND CT.service_type = case ? when 'ALL' then CT.service_type else ? end AND CT.sub_service = L.selector_code AND CT.service_type = L.service_type AND CT.network_code = ? AND CT.transfer_date >= ? AND CT.transfer_date <= ? AND CT.sender_id = case ? when 'ALL' then CT.sender_id else ? end AND CT.sender_id = U.user_id AND CAT.category_code = case ? when 'ALL' then CAT.category_code else ? end AND CAT.category_code = U.category_code AND CAT.domain_code = ? AND U.user_id = UG.user_id AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies ug1 WHERE UG1.grph_domain_code =case ? when 'ALL' then UG1.grph_domain_code else ? end AND UG1.user_id=?)) ORDER BY CT.transfer_date_time Desc");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 
		try {
			if (log.isDebugEnabled()) {
              log.debug("loadC2sTransferStaffReport",sqlSelect);	          
              log.debug(FROMDATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));				
				log.debug(TODATETIME,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));					           	          
	            log.debug(TRANSFERSTATUS,usersReportModel.getTransferStatus());
	            log.debug(SERVICETYPE,usersReportModel.getServiceType());
	            log.debug(ZONECODE,usersReportModel.getZoneCode());
	            log.debug(DOMAINCODE,usersReportModel.getDomainCode());
	            log.debug(PARENTCATEGORYCODE,usersReportModel.getParentCategoryCode());
	            log.debug(LOGINUSERID,usersReportModel.getLoginUserID());
	            log.debug(NETWORKCODE,usersReportModel.getNetworkCode());	
	            log.debug(USERID,usersReportModel.getUserID());
     }
			 pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(DATEPATTERN));
			 pstmt.setString(i++,Constants.getProperty(DATETIMEPATTERN));
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getTransferStatus());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getServiceType());
			 pstmt.setString(i++, usersReportModel.getNetworkCode());
			 pstmt.setTimestamp(i++, usersReportModel.getFromDateTimestamp()); 
			 pstmt.setTimestamp(i++, usersReportModel.getToDateTimestamp()); 		 
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getUserID());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			 pstmt.setString(i++, usersReportModel.getDomainCode());
			 pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());			
	         
		} catch (SQLException |ParseException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmt;		
	}

	@Override
	public PreparedStatement getEtopBundleChargeQuery(Connection con, UsersReportForm form) {
		// TODO Auto-generated method stub
		final String methodName ="getEtopBundleChargeQuery";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue(); 
		final StringBuilder sqlBuff = new StringBuilder(
				"Select TO_CHAR(").append(DownloadCSVReports.getDateTimeForCalendarType("CT.transfer_date_time"))
				.append(" ) transfer_date_time ,");
			sqlBuff.append( " u.user_name, u.payment_type, ");
		sqlBuff.append(
				" (CT.quantity /").append(amountMultFactor).append(") requested_quantity,");
		sqlBuff.append(
				" (CT.receiver_transfer_value /").append(amountMultFactor).append(") paid_amount,");
		sqlBuff.append(
				" (CT.receiver_access_fee/").append(amountMultFactor).append(") applied_charge,");
		sqlBuff.append(
				" ((ct.receiver_tax1_value + ct.receiver_tax2_value)/").append(amountMultFactor).append(") tax_value,");
		
		sqlBuff.append(
				" (adj.transfer_value/").append(amountMultFactor).append(") commission,");
		
		sqlBuff.append("KV.VALUE transfer_status, ST.NAME service_name, (Case  when CT.subs_sid IS ");
		sqlBuff.append(
				"NULL then CT.receiver_msisdn else CT.subs_sid  end) Reciever_MSISDN, CT.sender_msisdn,L.selector_name subService_name ,ct.card_group_code card_group ");
		
		sqlBuff.append(
				" FROM C2S_TRANSFERS CT, NETWORKS N, KEY_VALUES KV,SERVICE_TYPE ST, ");
		sqlBuff.append(
				"SERVICE_TYPE_SELECTOR_MAPPING L , USERS U,  CATEGORIES CAT, USER_GEOGRAPHIES UG,GEOGRAPHICAL_DOMAINS GD ,SERVICE_CLASSES SC,USER_PHONES UP , adjustments ADJ ");
		sqlBuff.append(
				" WHERE " );
		
		sqlBuff.append( " CT.TRANSFER_DATE_TIME >= ? AND CT.TRANSFER_DATE_TIME <= ? AND ");
		sqlBuff.append( " SC.service_class_id(+)=CT.service_class_id AND CT.receiver_network_code = N.network_code AND CT.transfer_status = KV.KEY AND KV.TYPE = 'C2S_STATUS' ");
		sqlBuff.append(
				"AND CT.transfer_status= CASE ? WHEN 'ALL' THEN CT.transfer_status ELSE ? END AND CT.SERVICE_TYPE = ST.SERVICE_TYPE ");
		sqlBuff.append(
				"AND CT.SERVICE_TYPE = CASE ?  WHEN 'ALL' THEN CT.SERVICE_TYPE ELSE  ? END AND CT.sub_service = L.selector_code ");
		sqlBuff.append(
				"AND CT.SERVICE_TYPE = L.SERVICE_TYPE AND CT.network_code = ? ");
		
		sqlBuff.append(" AND ct.sub_service = CASE ?  WHEN 'ALL' THEN ct.sub_service ELSE ? END ");
		
		if(!BTSLUtil.isNullString(form.getCardGroupCode()))
			sqlBuff.append(" AND ct.CARD_GROUP_CODE = ? ");
		
		if(!BTSLUtil.isNullString(form.getFromAmount()) && !BTSLUtil.isNullString(form.getToAmount()))
			sqlBuff.append(" AND ct.TRANSFER_VALUE >= ?   AND ct.TRANSFER_VALUE <= ?");
		
		
		if(!BTSLUtil.isNullString(form.getToMsisdn()))
			sqlBuff.append(" AND ct.RECEIVER_MSISDN = ? ");
        
		if(form.getUserType().equals(PretupsI.CHANNEL_USER_TYPE))
		{
			sqlBuff.append(" AND CT.sender_id IN(SELECT U11.user_id FROM users U11 WHERE U11.user_id=CASE ? WHEN 'ALL' THEN U11.user_id ELSE ? END "
					+ "CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?) ");
		}
		else
		{
			sqlBuff.append(" AND CT.sender_id =  CASE ?  WHEN 'ALL' THEN CT.sender_id ELSE ? END ");
		}
		sqlBuff.append(
				"AND CT.sender_id = U.user_id AND CAT.category_code = CASE ?  WHEN 'ALL' THEN CAT.category_code ELSE  ? END AND CAT.category_code = U.category_code ");
		sqlBuff.append(
				"AND CAT.domain_code = ? AND U.user_id = UG.user_id AND U.user_id=UP.USER_ID AND CT.SENDER_MSISDN = UP.MSISDN AND UG.grph_domain_code = GD.grph_domain_code ");
		sqlBuff.append(
				"AND UG.grph_domain_code IN (SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		sqlBuff.append(
				"START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN ");
		sqlBuff.append("UG1.grph_domain_code ELSE  ? END AND UG1.user_id=?))  AND adj.reference_id = ct.transfer_id AND adj.commission_type = ? AND adj.user_category != ? ORDER BY CT.transfer_date_time Desc ");
		
		String sqlSelect=sqlBuff.toString();		
		 PreparedStatement pstmtSelect = null;
		 
		 String subService="ALL";
			
			if(!BTSLUtil.isNullString(form.getCardGroupSubServiceID()) && !form.getCardGroupSubServiceID().equals(PretupsI.ALL))
				{
					String[] s = form.getCardGroupSubServiceID().split(":");
					subService=s[1];
					
				}
		 
		try {
			if (log.isDebugEnabled()) {
             log.debug("getEtopBundleChargeQuery",sqlSelect);	          
           
				}
			pstmtSelect = con.prepareStatement(sqlSelect);
			 
			 pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getFromDateTime()));
				pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getToDateTime()));
				
				pstmtSelect.setString(i++, form.getTransferStatus());
				pstmtSelect.setString(i++, form.getTransferStatus());
				pstmtSelect.setString(i++, form.getServiceTypeId());
				pstmtSelect.setString(i++, form.getServiceTypeId());
				pstmtSelect.setString(i++, form.getNetworkCode());
			
				pstmtSelect.setString(i++, subService);
				pstmtSelect.setString(i++, subService);
				
				if(!BTSLUtil.isNullString(form.getCardGroupCode()))
					pstmtSelect.setString(i++, form.getCardGroupCode());
				
				if(!BTSLUtil.isNullString(form.getFromAmount()) && !BTSLUtil.isNullString(form.getToAmount()))
				{
					
					pstmtSelect.setInt(i++, Integer.parseInt((form.getFromAmount()))*amountMultFactor);
					pstmtSelect.setInt(i++, Integer.parseInt((form.getToAmount()))*amountMultFactor);
				}
				
				if(!BTSLUtil.isNullString(form.getToMsisdn()))
				{
					pstmtSelect.setString(i++, form.getToMsisdn());
					
				}
				
				if(form.getUserType().equals(PretupsI.CHANNEL_USER_TYPE))
				{
					pstmtSelect.setString(i++, form.getUserID());
					pstmtSelect.setString(i++, form.getUserID());
					pstmtSelect.setString(i++, form.getLoginUserID());
				}
				else
				{
					pstmtSelect.setString(i++, form.getUserID());
					pstmtSelect.setString(i++, form.getUserID());
				}
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getDomainCode());
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getLoginUserID());
				
				pstmtSelect.setString(i++, PretupsI.NORMAL_COMMISSION);
				pstmtSelect.setString(i, PretupsI.BCU_USER);
			 
			 		
	         
		} catch (SQLException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmtSelect;
	}
	
	
	
	//
	
	@Override
	public PreparedStatement getC2SIncreaseDecreaseQuery(Connection con, UsersReportForm form) {
		// TODO Auto-generated method stub
		final String methodName ="getC2SIncreaseDecreaseQuery";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue(); 
		final StringBuilder sqlBuff = new StringBuilder(
				"Select TO_CHAR(").append(DownloadCSVReports.getDateTimeForCalendarType("CT.transfer_date_time"))
				.append(" ) transfer_date_time ,");
			sqlBuff.append( " u.user_name distributor_name , ct.sender_msisdn  distributor_msisdn , ");
		sqlBuff.append( " (CT.transfer_value /").append(amountMultFactor).append(") amount_of_transaction,");
		
		sqlBuff.append( " (ct.sender_post_balance /").append(amountMultFactor).append(") post_balance ," );
				sqlBuff.append(" ct.transfer_id transfer_id , u.payment_type payment_type ");
		
		
		sqlBuff.append(
				" FROM c2s_transfers ct, networks n, key_values kv, ");
		sqlBuff.append(
				"service_type st,service_type_selector_mapping l,users u,categories cat,user_geographies ug,geographical_domains gd,service_classes sc,user_phones UP ");
		sqlBuff.append(
				" WHERE " );
		
		sqlBuff.append( " ct.TRANSFER_DATE_TIME >= ? AND ct.TRANSFER_DATE_TIME <= ? AND ");
		if(!BTSLUtil.isNullString(form.getTransferNumber()))
		sqlBuff.append( " ct.TRANSFER_ID = ? AND ");
		
		sqlBuff.append( " sc.service_class_id(+)=ct.service_class_id AND ct.receiver_network_code = n.network_code AND ct.transfer_status = kv.KEY AND kv.TYPE = 'C2S_STATUS' ");
		sqlBuff.append(
				" AND ct.SERVICE_TYPE = st.SERVICE_TYPE ");
		sqlBuff.append(
				"AND CT.SERVICE_TYPE = CASE ?  WHEN 'ALL' THEN ct.SERVICE_TYPE ELSE  ? END AND ct.sub_service = l.selector_code ");
		sqlBuff.append(
				"AND ct.SERVICE_TYPE = l.SERVICE_TYPE AND ct.network_code = ? ");
		
		
			sqlBuff.append(" AND ct.sender_id =  CASE ?  WHEN 'ALL' THEN ct.sender_id ELSE ? END ");
		
		sqlBuff.append(
				"AND ct.sender_id = U.user_id AND cat.category_code = CASE ?  WHEN 'ALL' THEN cat.category_code ELSE  ? END AND cat.category_code = u.category_code ");
		sqlBuff.append(
				"AND cat.domain_code = ? AND u.user_id = ug.user_id AND u.user_id=UP.USER_ID AND ct.SENDER_MSISDN = UP.MSISDN AND ug.grph_domain_code = gd.grph_domain_code ");
		sqlBuff.append(
				"AND ug.grph_domain_code IN (SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		sqlBuff.append(
				"START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN ");
		sqlBuff.append("UG1.grph_domain_code ELSE  ? END AND UG1.user_id=?)) ORDER BY CT.transfer_date_time Desc ");
		
		String sqlSelect=sqlBuff.toString();		
		PreparedStatement pstmtSelect = null;
		 
		try {
			if (log.isDebugEnabled()) {
             log.debug("getC2SIncreaseDecreaseQuery",sqlSelect);	          
           
				}
			pstmtSelect = con.prepareStatement(sqlSelect);
			 
			 	pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getFromDateTime()));
				pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getToDateTime()));
				
				if(!BTSLUtil.isNullString(form.getTransferNumber()))
					pstmtSelect.setString(i++, form.getTransferNumber());
				
				
				if(form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SRC))
				{
					pstmtSelect.setString(i++, PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
					pstmtSelect.setString(i++, PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
				}
				else if (form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SBRC))
				{
					pstmtSelect.setString(i++, PretupsI.SERVICE_TYPE_VAS_RECHARGE);
					pstmtSelect.setString(i++, PretupsI.SERVICE_TYPE_VAS_RECHARGE);
				}
				pstmtSelect.setString(i++, form.getNetworkCode());
				pstmtSelect.setString(i++, form.getUserID());
				pstmtSelect.setString(i++, form.getUserID());
				
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getDomainCode());
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i, form.getLoginUserID());
				
			
		} catch (SQLException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmtSelect;
	}

	@Override
	public PreparedStatement getC2CWithRevIncreaseDecreaseQuery(Connection con, UsersReportForm form) {
		
		final String methodName ="getC2CWithRevIncreaseDecreaseQuery";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue(); 
		final StringBuilder sqlBuff = new StringBuilder(
				"Select TO_CHAR(").append(DownloadCSVReports.getDateTimeForCalendarType("NVL (ctrf.close_date, ctrf.created_on) "))
				.append(" ) transfer_date_time ,");
			sqlBuff.append( " u.user_name distributor_name , u.msisdn  distributor_msisdn , ");
		sqlBuff.append( " (cti.required_quantity /").append(amountMultFactor).append(") amount_of_transaction,");
		
		sqlBuff.append( " (cti.sender_post_stock /").append(amountMultFactor).append(") post_balance ," );
				sqlBuff.append(" ctrf.transfer_id transfer_id , ctrf.PMT_INST_TYPE payment_type ");
		
		
		sqlBuff.append(
				" FROM  geographical_domains gd, channel_transfers ctrf,channel_transfers_items cti,products p,lookups l,categories cat,user_geographies ug,users u ");
		
		sqlBuff.append(
				" WHERE " );
		sqlBuff.append( " ctrf.TYPE = 'C2C'  ");
		sqlBuff.append( " AND NVL (ctrf.close_date, ctrf.created_on)  >= ? AND NVL (ctrf.close_date, ctrf.created_on)  <= ? ");
		
		sqlBuff.append( " AND ctrf.network_code = ?   ");
		
		if(!BTSLUtil.isNullString(form.getTransferNumber()))
		sqlBuff.append( " AND ct.ctrf.transfer_id = ?  ");
		
		sqlBuff.append(" AND ctrf.sender_category_code = cat.category_code AND cat.category_code = u.category_code ");
		sqlBuff.append(" AND cat.category_code = CASE ? WHEN 'ALL' THEN cat.category_code ELSE ? END ");
		sqlBuff.append(" AND cat.domain_code = ? ");
		sqlBuff.append(" AND l.lookup_type = 'TRFT' AND l.lookup_code = ctrf.transfer_sub_type AND ctrf.status = 'CLOSE' ");
		
		
			sqlBuff.append("   AND ctrf.transfer_sub_type = CASE ? WHEN 'ALL' THEN ctrf.transfer_sub_type  ELSE ?  END ");
		
		sqlBuff.append(" AND  ctrf.FROM_USER_ID=  CASE ? WHEN 'ALL' THEN ctrf.FROM_USER_ID ELSE ? END ");
		sqlBuff.append(" AND ctrf.FROM_USER_ID = u.user_id AND ctrf.transfer_id = cti.transfer_id AND cti.product_code = p.product_code AND u.user_id = ug.user_id  ");
		sqlBuff.append(
				"   AND ug.grph_domain_code =gd.grph_domain_code");
		sqlBuff.append(
				" AND ug.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains gd1 WHERE status IN ('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START ");
		sqlBuff.append(" WITH grph_domain_code IN ( SELECT grph_domain_code FROM user_geographies ug1 WHERE ug1.grph_domain_code =  (CASE ? WHEN 'ALL' THEN ug1.grph_domain_code ELSE ? END) ");
		
		sqlBuff.append(" AND ug1.user_id = ? )) ");
		
		String sqlSelect=sqlBuff.toString();		
		PreparedStatement pstmtSelect = null;
		 
		try {
			if (log.isDebugEnabled()) {
             log.debug("getC2CWithRevIncreaseDecreaseQuery",sqlSelect);	          
           
				}
			pstmtSelect = con.prepareStatement(sqlSelect);
			 
			 	pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getFromDateTime()));
				pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getToDateTime()));
				
				pstmtSelect.setString(i++, form.getNetworkCode());
				
				if(!BTSLUtil.isNullString(form.getTransferNumber()))
					pstmtSelect.setString(i++, form.getTransferNumber());
				
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getDomainCode());
				
				if(form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2CWITHDRW))
				{
					pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
					pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
				}
				else if (form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2CREVSAL))
				{
					pstmtSelect.setString(i++, PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE);
					pstmtSelect.setString(i++, PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE);
				}
				else if (form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2C))
				{
					pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
					pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				}
				
				pstmtSelect.setString(i++, form.getUserID());
				pstmtSelect.setString(i++, form.getUserID());
				
				
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getLoginUserID());
				
			
		} catch (SQLException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmtSelect;
	}

	@Override
	public PreparedStatement getO2CWithRevIncreaseDecreaseQuery(Connection con, UsersReportForm form) {
		final String methodName ="getO2CWithRevIncreaseDecreaseQuery";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue(); 
		final StringBuilder sqlBuff = new StringBuilder(
				"Select TO_CHAR(").append(DownloadCSVReports.getDateTimeForCalendarType("NVL (ctrf.close_date, ctrf.created_on) "))
				.append(" ) transfer_date_time ,");
			sqlBuff.append( " u.user_name distributor_name , u.msisdn  distributor_msisdn , ");
		sqlBuff.append( " (cti.required_quantity /").append(amountMultFactor).append(") amount_of_transaction,");
		
		sqlBuff.append( " (cti.sender_post_stock /").append(amountMultFactor).append(") post_balance ," );
				sqlBuff.append(" ctrf.transfer_id transfer_id , ctrf.PMT_INST_TYPE payment_type ");
		
		
		sqlBuff.append(
				" FROM  geographical_domains gd, channel_transfers ctrf,channel_transfers_items cti,products p,lookups l,categories cat,user_geographies ug,users u ");
		
		sqlBuff.append(
				" WHERE " );
		sqlBuff.append( " ctrf.TYPE = 'O2C'  ");
		sqlBuff.append( " AND NVL (ctrf.close_date, ctrf.created_on)  >= ? AND NVL (ctrf.close_date, ctrf.created_on)  <= ? ");
		
		sqlBuff.append( " AND ctrf.network_code = ?   ");
		
		if(!BTSLUtil.isNullString(form.getTransferNumber()))
		sqlBuff.append( " AND ctrf.transfer_id = ?  ");
		
		sqlBuff.append(" AND ctrf.sender_category_code = cat.category_code AND cat.category_code = u.category_code ");
		sqlBuff.append(" AND cat.category_code = CASE ? WHEN 'ALL' THEN cat.category_code ELSE ? END ");
		sqlBuff.append(" AND cat.domain_code = ? ");
		sqlBuff.append(" AND l.lookup_type = 'TRFT' AND l.lookup_code = ctrf.transfer_sub_type AND ctrf.status = 'CLOSE' ");
		
		sqlBuff.append("   AND ctrf.transfer_sub_type = CASE ? WHEN 'ALL' THEN ctrf.transfer_sub_type  ELSE ?  END ");
		
		sqlBuff.append(" AND  ctrf.FROM_USER_ID=  CASE ? WHEN 'ALL' THEN ctrf.FROM_USER_ID ELSE ? END ");
		sqlBuff.append(" AND ctrf.FROM_USER_ID = u.user_id AND ctrf.transfer_id = cti.transfer_id AND cti.product_code = p.product_code AND u.user_id = ug.user_id  ");
		sqlBuff.append(
				"   AND ug.grph_domain_code =gd.grph_domain_code");
		sqlBuff.append(
				" AND ug.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains gd1 WHERE status IN ('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START ");
		sqlBuff.append(" WITH grph_domain_code IN ( SELECT grph_domain_code FROM user_geographies ug1 WHERE ug1.grph_domain_code =  (CASE ? WHEN 'ALL' THEN ug1.grph_domain_code ELSE ? END) ");
		
		sqlBuff.append(" AND ug1.user_id = ? )) ");
		
		String sqlSelect=sqlBuff.toString();		
		PreparedStatement pstmtSelect = null;
		 
		try {
			if (log.isDebugEnabled()) {
             log.debug("getO2CWithRevIncreaseDecreaseQuery",sqlSelect);	          
           
				}
			pstmtSelect = con.prepareStatement(sqlSelect);
			 
			 	pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getFromDateTime()));
				pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getToDateTime()));
				
				pstmtSelect.setString(i++, form.getNetworkCode());
				
				if(!BTSLUtil.isNullString(form.getTransferNumber()))
					pstmtSelect.setString(i++, form.getTransferNumber());
				
				
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getDomainCode());
				
				if(form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CWITHDRW))
				{
					pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
					pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
				}
				else if (form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CREVSAL))
				{
					pstmtSelect.setString(i++, PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE);
					pstmtSelect.setString(i++, PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE);
				}
				
				pstmtSelect.setString(i++, form.getUserID());
				pstmtSelect.setString(i++, form.getUserID());
				
				
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getLoginUserID());
				
			
		} catch (SQLException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmtSelect;
	}
	
	@Override
	public PreparedStatement getO2CPaymentTypeIncreaseDecreaseQuery(Connection con, UsersReportForm form) {
		final String methodName ="getO2CPaymentTypeIncreaseDecreaseQuery";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue(); 
		final StringBuilder sqlBuff = new StringBuilder(
				"Select TO_CHAR(").append(DownloadCSVReports.getDateTimeForCalendarType("NVL (ctrf.close_date, ctrf.created_on) "))
				.append(" ) transfer_date_time ,");
			sqlBuff.append( " u.user_name distributor_name , u.msisdn  distributor_msisdn , ");
		sqlBuff.append( " (cti.required_quantity /").append(amountMultFactor).append(") amount_of_transaction,");
		
		sqlBuff.append( " (cti.sender_post_stock /").append(amountMultFactor).append(") post_balance ," );
				sqlBuff.append(" ctrf.transfer_id transfer_id , ctrf.PMT_INST_TYPE payment_type ");
		
		
		sqlBuff.append(
				" FROM  geographical_domains gd, channel_transfers ctrf,channel_transfers_items cti,products p,lookups l,categories cat,user_geographies ug,users u ");
		
		sqlBuff.append(
				" WHERE " );
		sqlBuff.append( " ctrf.TYPE = 'O2C'  ");
		sqlBuff.append( " AND NVL (ctrf.close_date, ctrf.created_on)  >= ? AND NVL (ctrf.close_date, ctrf.created_on)  <= ? ");
		
		sqlBuff.append( " AND ctrf.network_code = ?   ");
		
		if(!BTSLUtil.isNullString(form.getTransferNumber()))
		sqlBuff.append( " AND ctrf.transfer_id = ?  ");
		
		if(!form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CFOC))
		sqlBuff.append( " AND ctrf.PMT_INST_TYPE = ?  ");
		
		sqlBuff.append("   AND ctrf.transfer_sub_type = CASE ? WHEN 'ALL' THEN ctrf.transfer_sub_type  ELSE ?  END ");
		
		sqlBuff.append(" AND ctrf.RECEIVER_CATEGORY_CODE = cat.category_code AND cat.category_code = u.category_code ");
		sqlBuff.append(" AND cat.category_code = CASE ? WHEN 'ALL' THEN cat.category_code ELSE ? END ");
		sqlBuff.append(" AND cat.domain_code = ? ");
		sqlBuff.append(" AND l.lookup_type = 'TRFT' AND l.lookup_code = ctrf.transfer_sub_type AND ctrf.status = 'CLOSE' ");
		
		
		sqlBuff.append(" AND  ctrf.to_user_id=  CASE ? WHEN 'ALL' THEN ctrf.to_user_id ELSE ? END ");
		sqlBuff.append(" AND ctrf.to_user_id = u.user_id AND ctrf.transfer_id = cti.transfer_id AND cti.product_code = p.product_code AND u.user_id = ug.user_id  ");
		sqlBuff.append(
				"   AND ug.grph_domain_code =gd.grph_domain_code");
		sqlBuff.append(
				" AND ug.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains gd1 WHERE status IN ('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START ");
		sqlBuff.append(" WITH grph_domain_code IN ( SELECT grph_domain_code FROM user_geographies ug1 WHERE ug1.grph_domain_code =  (CASE ? WHEN 'ALL' THEN ug1.grph_domain_code ELSE ? END) ");
		
		sqlBuff.append(" AND ug1.user_id = ? )) ");
		
		String sqlSelect=sqlBuff.toString();		
		PreparedStatement pstmtSelect = null;
		 
		try {
			if (log.isDebugEnabled()) {
             log.debug("getO2CPaymentTypeIncreaseDecreaseQuery",sqlSelect);	          
           
				}
			pstmtSelect = con.prepareStatement(sqlSelect);
			 
			 	pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getFromDateTime()));
				pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getToDateTime()));
				
				pstmtSelect.setString(i++, form.getNetworkCode());
				
				if(!BTSLUtil.isNullString(form.getTransferNumber()))
					pstmtSelect.setString(i++, form.getTransferNumber());
				
				if(!BTSLUtil.isNullString(form.getKindOfTransaction())&& !form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CFOC))
				{
					if(form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CASH))
						pstmtSelect.setString(i++, PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH);
					else if(form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CCONGMNT))
						pstmtSelect.setString(i++, PretupsI.PAYMENT_INSTRUMENT_TYPE_CONS);
					else if(form.getKindOfTransaction().equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CONLINE))
						pstmtSelect.setString(i++, PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE);
				}
				
				pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				
				
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getDomainCode());
				
				pstmtSelect.setString(i++, form.getUserID());
				pstmtSelect.setString(i++, form.getUserID());
				
				
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getLoginUserID());
				
			
		} catch (SQLException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmtSelect;
	}
	
	@Override
	public PreparedStatement getTotalSalesQuery(Connection con,UsersReportForm thisForm) {
		
		final String methodName ="getTotalSalesQuery";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();			
			/*StringBuilder selectQueryBuff = new StringBuilder("SELECT ").append( DownloadCSVReports.getDateForCalendarType("V.summary_date")).append(" summary_date, ").append(" T.name VNAME, V.PRODUCTION_NETWORK_CODE NWCODE,");
			selectQueryBuff.append("V.denomination/ ")
					.append(amountMultFactor).append(" denomination,");
			selectQueryBuff.append("V.TOTAL_GENERATED GENCOUNT, V.TOTAL_ON_HOLD OHCOUNT, V.TOTAL_ENABLED ENCOUNT, V.TOTAL_SUSPENDED SCOUNT ,V.TOTAL_EXPIRED  EXCOUNT, V.TOTAL_STOLEN  STCOUNT , V.TOTAL_DAMAGED DACOUNT , V.TOTAL_CONSUMED CUCOUNT, V.TOTAL_WAREHOUSE WHCOUNT, V.TOTAL_PRINTING PRCOUNT,V.voucher_type VTYPE,V.product_id");
			selectQueryBuff.append(" FROM VOMS_VOUCHER_DAILY_SUMMARY V, VOMS_TYPES T ");
			selectQueryBuff.append(" WHERE  V.summary_date >=?")
			.append(" AND V.summary_date <=?");
			selectQueryBuff.append(" AND T.voucher_type=V.voucher_type");
			selectQueryBuff.append(" ORDER BY V.summary_date");*/
			
			StringBuilder selectQueryBuff = new StringBuilder("SELECT ").append( DownloadCSVReports.getDateForCalendarType("d.trans_date")).append(" trans_date, ").append("U.user_name NAME, U.msisdn MSISDN, d.USER_ID, d.O2C_TRANSFER_IN_AMOUNT/")
					.append(amountMultFactor).append(" BOUGHT,");
			selectQueryBuff.append("(select COALESCE(sum(SENDER_TRANSFER_AMOUNT),0) from daily_c2s_trans_details dd where dd.trans_date=d.trans_date and dd.user_id=d.user_id and dd.SERVICE_TYPE='RC')/ ")
			.append(amountMultFactor).append(" RECHARGE,");
			selectQueryBuff.append("(select COALESCE(sum(SENDER_TRANSFER_AMOUNT),0) from daily_c2s_trans_details dd where dd.trans_date=d.trans_date and dd.user_id=d.user_id and dd.SERVICE_TYPE='VAS')/ ")
			.append(amountMultFactor).append(" BUNDLE,");
			selectQueryBuff.append("(COALESCE (SUM (d.c2c_transfer_out_amount), 0)+COALESCE (SUM (d.c2c_withdraw_in_amount), 0)+COALESCE (SUM (d.c2s_transfer_in_amount), 0)+COALESCE (SUM (d.c2c_reverse_in_amount), 0))/")
			.append(amountMultFactor).append(" OTHERS, ");
			selectQueryBuff.append("d.CLOSING_BALANCE/ ")
			.append(amountMultFactor).append("CLOSING_BALANCE");
			selectQueryBuff.append(" from daily_chnl_trans_main d, users U");
			selectQueryBuff.append(" where d.trans_date >=?")
			.append(" and d.trans_date <=?");
			selectQueryBuff.append(" and d.user_id=CASE ? WHEN 'ALL' THEN d.user_id else ? END");
			selectQueryBuff.append(" and U.user_id=d.user_id");
			selectQueryBuff.append(" group by d.trans_date,d.USER_ID, d.C2S_TRANSFER_OUT_AMOUNT, d.O2C_TRANSFER_IN_AMOUNT,d.CLOSING_BALANCE,U.user_name, U.msisdn");
			selectQueryBuff.append(" order by d.USER_ID,d.trans_date desc");
			
			String sqlSelect=selectQueryBuff.toString();
			 PreparedStatement pstmtSelect = null;
			try {
				if(log.isDebugEnabled())
				{
					log.debug(methodName, " "+ Constants.getProperty("report.systemdatetime.format")+" , "+ BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getFromDateTime())+" , "+ BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getToDateTime())+","+thisForm.getUserID());
					log.debug(methodName, "Select Query: "+sqlSelect);
				}
						
				pstmtSelect = con.prepareStatement(sqlSelect);
					int i= 0;
					pstmtSelect.setTimestamp(++i, BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getFromDateTime()));
					pstmtSelect.setTimestamp(++i, BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getToDateTime()));
					pstmtSelect.setString(++i, thisForm.getUserID());
					pstmtSelect.setString(++i, thisForm.getUserID());
					
			} catch (SQLException e) {
				 log.errorTrace(methodName, e);
			} 	 		 
			 
			return pstmtSelect;				
	}
	
	@Override
	public PreparedStatement getTotalC2SQuery(Connection con,UsersReportForm thisForm) {
		
		final String methodName ="getTotalSalesQuery";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();			
			/*StringBuilder selectQueryBuff = new StringBuilder("SELECT ").append( DownloadCSVReports.getDateForCalendarType("V.summary_date")).append(" summary_date, ").append(" T.name VNAME, V.PRODUCTION_NETWORK_CODE NWCODE,");
			selectQueryBuff.append("V.denomination/ ")
					.append(amountMultFactor).append(" denomination,");
			selectQueryBuff.append("V.TOTAL_GENERATED GENCOUNT, V.TOTAL_ON_HOLD OHCOUNT, V.TOTAL_ENABLED ENCOUNT, V.TOTAL_SUSPENDED SCOUNT ,V.TOTAL_EXPIRED  EXCOUNT, V.TOTAL_STOLEN  STCOUNT , V.TOTAL_DAMAGED DACOUNT , V.TOTAL_CONSUMED CUCOUNT, V.TOTAL_WAREHOUSE WHCOUNT, V.TOTAL_PRINTING PRCOUNT,V.voucher_type VTYPE,V.product_id");
			selectQueryBuff.append(" FROM VOMS_VOUCHER_DAILY_SUMMARY V, VOMS_TYPES T ");
			selectQueryBuff.append(" WHERE  V.summary_date >=?")
			.append(" AND V.summary_date <=?");
			selectQueryBuff.append(" AND T.voucher_type=V.voucher_type");
			selectQueryBuff.append(" ORDER BY V.summary_date");*/
			
			StringBuilder selectQueryBuff = new StringBuilder("SELECT ").append( DownloadCSVReports.getDateForCalendarType("d.trans_date")).append(" trans_date, ");
					
			selectQueryBuff.append("(select COALESCE(sum(SENDER_TRANSFER_AMOUNT),0) from daily_c2s_trans_details dd where dd.trans_date=d.trans_date and dd.SERVICE_TYPE='RC')/ ")
			.append(amountMultFactor).append(" RECHARGE,");
			selectQueryBuff.append("(select COALESCE(sum(SENDER_TRANSFER_AMOUNT),0) from daily_c2s_trans_details dd where dd.trans_date=d.trans_date and dd.SERVICE_TYPE='VAS')/ ")
			.append(amountMultFactor).append(" BUNDLE");

			selectQueryBuff.append(" from daily_chnl_trans_main d");
			selectQueryBuff.append(" where d.trans_date >=?")
			.append(" and d.trans_date <=?");
			selectQueryBuff.append(" group by d.trans_date");
			selectQueryBuff.append(" order by d.trans_date desc");
			
			String sqlSelect=selectQueryBuff.toString();
			 PreparedStatement pstmtSelect = null;
			try {
				if(log.isDebugEnabled())
				{
					log.debug(methodName, " "+ Constants.getProperty("report.systemdatetime.format")+" , "+ BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getFromDateTime())+" , "+ BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getToDateTime())+","+thisForm.getUserID());
					log.debug(methodName, "Select Query: "+sqlSelect);
				}
						
				pstmtSelect = con.prepareStatement(sqlSelect);
					int i= 0;
					pstmtSelect.setTimestamp(++i, BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getFromDateTime()));
					pstmtSelect.setTimestamp(++i, BTSLUtil.getSQLDateTimeFromUtilDate(thisForm.getToDateTime()));
					
			} catch (SQLException e) {
				 log.errorTrace(methodName, e);
			} 	 		 
			 
			return pstmtSelect;				
	}
	
	
	@Override
	public PreparedStatement getO2CPaymentTypeQuery(Connection con, UsersReportForm form) {
		final String methodName ="getO2CPaymentTypeQuery";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue(); 
		final StringBuilder sqlBuff = new StringBuilder(
				"Select u.user_name distributor_name, u.msisdn distributor_msisdn, ");
				
				sqlBuff.append( "     ctrf.PMT_INST_SOURCE bank_name, ctrf.pmt_inst_type payment_type, ");
				sqlBuff.append( " (ctrf.requested_quantity /").append(amountMultFactor).append(") requested_amount ,");
		
				sqlBuff.append( " (ctrf.requested_quantity  /").append(amountMultFactor).append(") unit_amount ," );
				sqlBuff.append("  CASE cti.tax1_type WHEN 'PCT' THEN ((1 / 1 + (cti.tax1_rate / 100))) ");
				sqlBuff.append("   WHEN 'AMT'  THEN (100 / (100 + cti.tax1_value)) END unit_price, ");
				sqlBuff.append("  '0' total_amount , ");
				
				sqlBuff.append(" case ctrf.DUAL_COMM_TYPE when 'NC' then cti.COMMISSION_RATE else 0  end discount, ");
				sqlBuff.append(" case ctrf.DUAL_COMM_TYPE when 'PC' then cti.COMMISSION_RATE else 0  end extra_charge, ");
				sqlBuff.append("  '0'  discount_amount,'0' extra_charge_amount,'0' amount_after_discount , '0' amount_after_extra_charge, ");
				sqlBuff.append(" cti.TAX1_VALUE tax,  '0' amount_paid,  ");
				
				sqlBuff.append(" TO_CHAR(").append(DownloadCSVReports.getDateTimeForCalendarType("ctrf.close_date ")).append(" ) payment_date , ");
				sqlBuff.append(" ctrf.PMT_INST_NO transaction_bank_number,ctrf.STATUS bank_status,ctrf.PMT_INST_STATUS payment_status, ");
				sqlBuff.append(" ctrf.FIRST_APPROVER_REMARKS first_approver_remarks,ctrf.SECOND_APPROVER_REMARKS SECOND_APPROVER_REMARKS, ctrf.THIRD_APPROVER_REMARKS THIRD_APPROVER_REMARKS ");
				
				
				
				sqlBuff.append(
				" FROM  geographical_domains gd, channel_transfers ctrf,channel_transfers_items cti,products p,lookups l,categories cat,user_geographies ug,users u ");
		
				sqlBuff.append(
				" WHERE " );
				sqlBuff.append( " ctrf.TYPE = 'O2C'  ");
				sqlBuff.append( " AND NVL (ctrf.close_date, ctrf.created_on)  >= ? AND NVL (ctrf.close_date, ctrf.created_on)  <= ? ");
		
				sqlBuff.append( " AND ctrf.network_code = ?   ");
		
				sqlBuff.append( " AND ctrf.PMT_INST_TYPE = ?  ");
		
				if(!BTSLUtil.isNullString(form.getPaymentGatewayType()))
				sqlBuff.append( " AND ctrf.PMT_INST_SOURCE = ?  ");
				
				sqlBuff.append("   AND ctrf.transfer_sub_type = CASE ? WHEN 'ALL' THEN ctrf.transfer_sub_type  ELSE ?  END ");
				
				sqlBuff.append(" AND ctrf.RECEIVER_CATEGORY_CODE = cat.category_code AND cat.category_code = u.category_code ");
				sqlBuff.append(" AND cat.category_code = CASE ? WHEN 'ALL' THEN cat.category_code ELSE ? END ");
				sqlBuff.append(" AND cat.domain_code = ? ");
				sqlBuff.append(" AND l.lookup_type = 'TRFT' AND l.lookup_code = ctrf.transfer_sub_type AND ctrf.status = 'CLOSE' ");
				
				
				sqlBuff.append(" AND  ctrf.to_user_id=  CASE ? WHEN 'ALL' THEN ctrf.to_user_id ELSE ? END ");
				sqlBuff.append(" AND ctrf.to_user_id = u.user_id AND ctrf.transfer_id = cti.transfer_id AND cti.product_code = p.product_code AND u.user_id = ug.user_id  ");
				sqlBuff.append(
						"   AND ug.grph_domain_code =gd.grph_domain_code");
				sqlBuff.append(
						" AND ug.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains gd1 WHERE status IN ('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START ");
				sqlBuff.append(" WITH grph_domain_code IN ( SELECT grph_domain_code FROM user_geographies ug1 WHERE ug1.grph_domain_code =  (CASE ? WHEN 'ALL' THEN ug1.grph_domain_code ELSE ? END) ");
				
				sqlBuff.append(" AND ug1.user_id = ? )) ");
		
		String sqlSelect=sqlBuff.toString();		
		PreparedStatement pstmtSelect = null;
		 
		try {
			if (log.isDebugEnabled()) {
             log.debug("getO2CPaymentTypeQuery",sqlSelect);	          
           
				}
			pstmtSelect = con.prepareStatement(sqlSelect);
			 
			 	pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getFromDateTime()));
				pstmtSelect.setTimestamp(i++, BTSLUtil.getSQLDateTimeFromUtilDate(form.getToDateTime()));
				
				pstmtSelect.setString(i++, form.getNetworkCode());
				
				pstmtSelect.setString(i++, form.getPaymentInstCode());
			
				if(!BTSLUtil.isNullString(form.getPaymentGatewayType()))
					pstmtSelect.setString(i++, form.getPaymentGatewayType());
				
				
				pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				
				
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getParentCategoryCode());
				pstmtSelect.setString(i++, form.getDomainCode());
				
				pstmtSelect.setString(i++, form.getUserID());
				pstmtSelect.setString(i++, form.getUserID());
				
				
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getZoneCode());
				pstmtSelect.setString(i++, form.getLoginUserID());
				
			
		} catch (SQLException e) {
			 log.errorTrace(methodName, e);
		} 	 		 
		 
		return pstmtSelect;
	}


}
