/**
 * @(#)UserDAO.java
 *                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 * 
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Mohit Goel 22/06/2005 Initial Creation
 *                  Sandeep Goel 12/12/2005 Modification
 *                  Shashank Gaur 29/03/2013 Modification(Barred For Deletion)
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  This class is used for User Insertion/Updation
 * 
 */
package com.btsl.user.businesslogic;

import static com.btsl.db.util.DBConstants.PHONE_PROFILE;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;

// commented for DB2import oracle.jdbc.OraclePreparedStatement;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.BalanceVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTotalTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STotalTrnsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelUserDTO;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;
import com.btsl.pretups.channel.transfer.businesslogic.GetDomainCategoryMsg;
import com.btsl.pretups.channel.transfer.businesslogic.GetParentOwnerProfileReq;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldReportDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchInputVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistSearchRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistoryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.StaffUserDTO;
import com.btsl.pretups.channel.transfer.businesslogic.TransactionalData;
import com.btsl.pretups.channel.transfer.businesslogic.TransactionalDataResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserHierarchyVO;
import com.btsl.pretups.channel.transfer.requesthandler.ChannelUserUnderParentVO;
import com.btsl.pretups.channel.transfer.requesthandler.GetDomainCatParentCatParentUserMsg;
import com.btsl.pretups.channel.transfer.requesthandler.GetParentOwnerProfileRespVO;
import com.btsl.pretups.channeluser.businesslogic.ApplistReqVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.DailyReportAnalysisQry;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ChildUserVO;
import com.btsl.pretups.util.LDAPUtilI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.BooleanOperator;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;
import com.btsl.util.SqlParameterEncoder;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.restapi.channelAdmin.ChannelUserListByParentResponseVO;
import com.restapi.channelAdmin.ChannelUserListByParntReqVO;
import com.restapi.channelAdmin.ChannelUserListResponseVO;
import com.restapi.channelAdmin.StaffUserListByParentResponseVO;
import com.restapi.channelAdmin.StaffUserListByParntReqVO;
import com.restapi.user.service.DashboardPermissionVO;
import com.restapi.user.service.Item;

/**
 * 
 */
public class UserDAO {
	 
    public static boolean flag = false;
    private static final String EXCEPTION = "EXCEPTION: ";
    private static final String SQL_EXCEPTION = "SQL EXCEPTION: ";
    private static final String QUERY_KEY = "Query: ";
    

    /**
     * Commons Logging instance.
     */
    private final Log LOG = LogFactory.getLog(this.getClass().getName());
    private UserQry userQry = (UserQry)ObjectProducer.getObject(QueryConstants.USER_QRY, QueryConstants.QUERY_PRODUCER);
    private DailyReportAnalysisQry dailyRptAnalysiQry = (DailyReportAnalysisQry)ObjectProducer.getObject(QueryConstants.DAILY_REPORT_ANALYSIS, QueryConstants.QUERY_PRODUCER);
    /**
     * Method for checking Is LoginId already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_loginId
     *            String
     * @param p_userId
     *            String
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isUserLoginExist(Connection p_con, String p_loginId, String p_userId) throws BTSLBaseException {
        final String methodName = "isUserLoginExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginId=");
        	loggerValue.append(p_loginId);
        	loggerValue.append("p_userId=");
        	loggerValue.append(p_userId);
        	LOG.debug(methodName, loggerValue);
        }
        
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

        if (BTSLUtil.isNullString(p_userId)) {
            strBuff.append("SELECT login_id FROM users WHERE UPPER(login_id) = UPPER(?)");
        } else {
            strBuff.append("SELECT login_id FROM users WHERE UPPER(login_id) = UPPER(?) and user_id != ?");
        }

        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            

            if (BTSLUtil.isNullString(p_userId)) {
                pstmt.setString(1, p_loginId);
            } else {
                pstmt.setString(1, p_loginId);
                pstmt.setString(2, p_userId);
            }
           try( ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        
        	LogFactory.printLog(methodName, "Exiting: existFlag=" + existFlag, LOG);
        }
    }
    
    
    
    
    /**
     * Method for checking Is LoginId already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_loginId
     *            String
     * @param p_userId
     *            String
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isUserLoginExistForModification(Connection p_con, String p_loginId, String excludeLoginID) throws BTSLBaseException {
        final String methodName = "isUserLoginExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginId=");
        	loggerValue.append(p_loginId);
        	loggerValue.append("Modify loginID=");
        	loggerValue.append(excludeLoginID);
        	LOG.debug(methodName, loggerValue);
        }
        
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

       
            strBuff.append("SELECT login_id FROM users WHERE UPPER(login_id) = UPPER(?) and UPPER(login_id) != UPPER(?)");
       

        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            

                pstmt.setString(1, p_loginId);
                pstmt.setString(2, excludeLoginID);  // modification logiID

           try( ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExistForModification]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExistForModification]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        
        	LogFactory.printLog(methodName, "Exiting: existFlag=" + existFlag, LOG);
        }
    }


    /**
     * Method for inserting User.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            UserVO
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addUser(Connection p_con, UserVO p_userVO) throws BTSLBaseException {
        // commented for DB2OraclePreparedStatement psmtInsert = null;
    	String allowdUsrTypCreation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION);
        int insertCount = 0;
        final String methodName = "addUser";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userVO=");
        	loggerValue.append(p_userVO);
        	LOG.debug(methodName, loggerValue);
        }
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO users (user_id,user_name,network_code,");
            strBuff.append("login_id,password,category_code,parent_id,");
            strBuff.append("owner_id,allowed_ip,allowed_days,");
            strBuff.append("from_time,to_time,employee_code,");
            strBuff.append("status,email,contact_no,");
            strBuff.append("designation,division,department,msisdn,user_type,");
            strBuff.append("created_by,created_on,modified_by,modified_on,address1, ");
            strBuff.append("address2,city,state,country,ssn,user_name_prefix,external_code,short_name,user_code,appointment_date, ");
            strBuff.append("previous_status,pswd_reset,rsaflag,contact_person,longitude,latitude,company,fax,firstname,lastname,CREATION_TYPE,AUTHENTICATION_ALLOWED, "); // added
			strBuff.append("allowd_usr_typ_creation,document_type,document_no");
		      if(!BTSLUtil.isNullString(p_userVO.getPaymentTypes()))
		          strBuff.append( ",payment_type");
			strBuff.append( " ) ");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
             if(!BTSLUtil.isNullString(p_userVO.getPaymentTypes()))
            	 strBuff.append(",?");
            strBuff.append( " ) ");
            
            String insertQuery = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            // commented for DB2psmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            try( PreparedStatement  psmtInsert = p_con.prepareStatement(insertQuery);)
            {
            psmtInsert.setString(1, p_userVO.getUserID());

            // commented for DB2psmtInsert.setFormOfUse(2,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(2, p_userVO.getUserName());

            psmtInsert.setString(3, p_userVO.getNetworkID());
            psmtInsert.setString(4, p_userVO.getLoginID());
            psmtInsert.setString(5, p_userVO.getPassword());
            psmtInsert.setString(6, p_userVO.getCategoryCode());
            psmtInsert.setString(7, p_userVO.getParentID());
            psmtInsert.setString(8, p_userVO.getOwnerID());
            psmtInsert.setString(9, p_userVO.getAllowedIps());
            psmtInsert.setString(10, p_userVO.getAllowedDays());
            psmtInsert.setString(11, p_userVO.getFromTime());
            psmtInsert.setString(12, p_userVO.getToTime());
            psmtInsert.setString(13, p_userVO.getEmpCode());
            psmtInsert.setString(14, p_userVO.getStatus());
            psmtInsert.setString(15, p_userVO.getEmail());
            psmtInsert.setString(16, p_userVO.getContactNo());
            // commented for DB2psmtInsert.setFormOfUse(17,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(17, p_userVO.getDesignation());
            psmtInsert.setString(18, p_userVO.getDivisionCode());
            psmtInsert.setString(19, p_userVO.getDepartmentCode());
            psmtInsert.setString(20, p_userVO.getMsisdn());
            psmtInsert.setString(21, p_userVO.getUserType());
            psmtInsert.setString(22, p_userVO.getCreatedBy());
            psmtInsert.setTimestamp(23, BTSLUtil.getTimestampFromUtilDate(p_userVO.getCreatedOn()));
            psmtInsert.setString(24, p_userVO.getModifiedBy());
            psmtInsert.setTimestamp(25, BTSLUtil.getTimestampFromUtilDate(p_userVO.getModifiedOn()));
            // commented for DB2psmtInsert.setFormOfUse(26,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(26, p_userVO.getAddress1());
            // commented for DB2psmtInsert.setFormOfUse(27,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(27, p_userVO.getAddress2());
            // commented for DB2psmtInsert.setFormOfUse(28,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(28, p_userVO.getCity());
            // commented for DB2psmtInsert.setFormOfUse(29,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(29, p_userVO.getState());
            // commented for DB2psmtInsert.setFormOfUse(30,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(30, p_userVO.getCountry());
            psmtInsert.setString(31, p_userVO.getSsn());
            // commented for DB2psmtInsert.setFormOfUse(32,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(32, p_userVO.getUserNamePrefix());
            psmtInsert.setString(33, p_userVO.getExternalCode());
            // commented for DB2psmtInsert.setFormOfUse(34,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(34, p_userVO.getShortName());
            psmtInsert.setString(35, p_userVO.getUserCode());

            if (p_userVO.getAppointmentDate() != null) {
                psmtInsert.setTimestamp(36, BTSLUtil.getTimestampFromUtilDate(p_userVO.getAppointmentDate()));
            } else {
                psmtInsert.setTimestamp(36, null);
            }
            psmtInsert.setString(37, p_userVO.getPreviousStatus());
            psmtInsert.setString(38, PretupsI.YES);
            psmtInsert.setString(39, p_userVO.getRsaFlag());
            psmtInsert.setString(40, p_userVO.getContactPerson());
            // added by nilesh:user profile updation based on langitude and
            // latitude
            psmtInsert.setString(41, p_userVO.getLongitude());
            psmtInsert.setString(42, p_userVO.getLatitude());
            // deepika aggarwal changes
            psmtInsert.setString(43, p_userVO.getCompany());
            psmtInsert.setString(44, p_userVO.getFax());
            psmtInsert.setString(45, p_userVO.getFirstName());
            psmtInsert.setString(46, p_userVO.getLastName());
            if (p_userVO.getCreationType() != null && (p_userVO.getCreationType().equalsIgnoreCase(PretupsI.STK_SYSTEM_USR_CREATION_TYPE))) {
                psmtInsert.setString(47, p_userVO.getCreationType());
            } else {
                psmtInsert.setString(47, PretupsI.MULTIPLE_ENTRY_ALLOWED);
            }
            // end added by deepika aggarwal
            // added by abhilasha for Authentication Type
            if (p_userVO.getAuthTypeAllowed() != null) {
                psmtInsert.setString(48, p_userVO.getAuthTypeAllowed());
            } else {
                psmtInsert.setString(48, PretupsI.NO);
            }
            
            if ((PretupsI.YES).equals(allowdUsrTypCreation)) {
				psmtInsert.setString(49, p_userVO.getAllowedUserTypeCreation());
			}else{
				psmtInsert.setString(49, "");
			}
            psmtInsert.setString(50, p_userVO.getDocumentType());
            psmtInsert.setString(51, p_userVO.getDocumentNo());
           // psmtInsert.setString(52, p_userVO.getPaymentType());
            if(!BTSLUtil.isNullString(p_userVO.getPaymentTypes()))
            psmtInsert.setString(52, p_userVO.getPaymentTypes());
            insertCount = psmtInsert.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUser]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUser]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, LOG);
           
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting User Phone Info.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_phoneList
     *            ArrayList
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addUserPhoneList(Connection p_con, List p_phoneList) throws BTSLBaseException {
         
        int insertCount = 0;
        final String methodName = "addUserPhoneList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_phoneList.size()=");
        	loggerValue.append(p_phoneList.size());
        	LOG.debug(methodName, loggerValue);
        }
       
        try {
            if ((p_phoneList != null)) {
                StringBuilder strBuff = new StringBuilder();

                strBuff.append("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
                strBuff.append("description,primary_number,sms_pin,pin_required,");
                strBuff.append("phone_profile,phone_language,country,invalid_pin_count,");
                strBuff.append("last_transaction_status,last_transaction_on,pin_modified_on,");
                strBuff.append("created_by,created_on,modified_by,modified_on, last_transfer_id,");
                strBuff.append(" prefix_id,last_transfer_type,pin_reset) values ");
                strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                String insertQuery = strBuff.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(insertQuery);
        			LOG.debug(methodName, loggerValue);
        		}
                try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
                {
                UserPhoneVO userPhoneVO = null;
                for (int i = 0, j = p_phoneList.size(); i < j; i++) {
                    userPhoneVO = (UserPhoneVO) p_phoneList.get(i);

                    psmtInsert.setString(1, userPhoneVO.getUserPhonesId());
                    psmtInsert.setString(2, userPhoneVO.getMsisdn());
                    psmtInsert.setString(3, userPhoneVO.getUserId());
                    psmtInsert.setString(4, userPhoneVO.getDescription());
                    psmtInsert.setString(5, userPhoneVO.getPrimaryNumber());
                    psmtInsert.setString(6, userPhoneVO.getSmsPin());
                    psmtInsert.setString(7, userPhoneVO.getPinRequired());
                    psmtInsert.setString(8, userPhoneVO.getPhoneProfile());
                    psmtInsert.setString(9, userPhoneVO.getPhoneLanguage());
                    psmtInsert.setString(10, userPhoneVO.getCountry());
                    psmtInsert.setInt(11, userPhoneVO.getInvalidPinCount());
                    psmtInsert.setString(12, userPhoneVO.getLastTransactionStatus());
                    if (userPhoneVO.getLastTransactionOn() != null) {
                        psmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getLastTransactionOn()));
                    } else {
                        psmtInsert.setTimestamp(13, null);
                    }
                    psmtInsert.setTimestamp(14, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getPinModifiedOn()));
                    psmtInsert.setString(15, userPhoneVO.getCreatedBy());
                    psmtInsert.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getCreatedOn()));
                    psmtInsert.setString(17, userPhoneVO.getModifiedBy());
                    psmtInsert.setTimestamp(18, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getModifiedOn()));
                    psmtInsert.setString(19, userPhoneVO.getLastTransferID());
                    psmtInsert.setLong(20, userPhoneVO.getPrefixID());
                    psmtInsert.setString(21, userPhoneVO.getLastTransferType());
                    if (userPhoneVO.getPinReset() != null) {
                    	psmtInsert.setString(22, userPhoneVO.getPinReset());
                    } else {
                    	psmtInsert.setString(22, PretupsI.YES);
                    }

                    insertCount = psmtInsert.executeUpdate();

                    psmtInsert.clearParameters();
                    // check the status of the update
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
        } 
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUserPhoneList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUserPhoneList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, LOG);
          
        } // end of finally

        return insertCount;
    }
    public int addUserPhoneListMappGw(Connection p_con, ArrayList p_phoneList) throws BTSLBaseException {
        
        int insertCount = 0;
        final String methodName = "addUserPhoneListMappGw";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_phoneList.size()=");
        	loggerValue.append(p_phoneList.size());
        	LOG.debug(methodName, loggerValue);
        }
       
        try {
            if ((p_phoneList != null)) {
                StringBuilder strBuff = new StringBuilder();

                strBuff.append("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
                strBuff.append("description,primary_number,sms_pin,pin_required,");
                strBuff.append("phone_profile,phone_language,country,invalid_pin_count,");
                strBuff.append("last_transaction_status,last_transaction_on,pin_modified_on,");
                strBuff.append("created_by,created_on,modified_by,modified_on, last_transfer_id,");
                strBuff.append(" prefix_id,last_transfer_type,pin_reset,mhash,imei) values ");
                strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                String insertQuery = strBuff.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(insertQuery);
        			LOG.debug(methodName, loggerValue);
        		}
                try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
                {
                UserPhoneVO userPhoneVO = null;
                for (int i = 0, j = p_phoneList.size(); i < j; i++) {
                    userPhoneVO = (UserPhoneVO) p_phoneList.get(i);

                    psmtInsert.setString(1, userPhoneVO.getUserPhonesId());
                    psmtInsert.setString(2, userPhoneVO.getMsisdn());
                    psmtInsert.setString(3, userPhoneVO.getUserId());
                    psmtInsert.setString(4, userPhoneVO.getDescription());
                    psmtInsert.setString(5, userPhoneVO.getPrimaryNumber());
                    psmtInsert.setString(6, userPhoneVO.getSmsPin());
                    psmtInsert.setString(7, userPhoneVO.getPinRequired());
                    psmtInsert.setString(8, userPhoneVO.getPhoneProfile());
                    psmtInsert.setString(9, userPhoneVO.getPhoneLanguage());
                    psmtInsert.setString(10, userPhoneVO.getCountry());
                    psmtInsert.setInt(11, userPhoneVO.getInvalidPinCount());
                    psmtInsert.setString(12, userPhoneVO.getLastTransactionStatus());
                    if (userPhoneVO.getLastTransactionOn() != null) {
                        psmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getLastTransactionOn()));
                    } else {
                        psmtInsert.setTimestamp(13, null);
                    }
                    psmtInsert.setTimestamp(14, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getPinModifiedOn()));
                    psmtInsert.setString(15, userPhoneVO.getCreatedBy());
                    psmtInsert.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getCreatedOn()));
                    psmtInsert.setString(17, userPhoneVO.getModifiedBy());
                    psmtInsert.setTimestamp(18, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getModifiedOn()));
                    psmtInsert.setString(19, userPhoneVO.getLastTransferID());
                    psmtInsert.setLong(20, userPhoneVO.getPrefixID());
                    psmtInsert.setString(21, userPhoneVO.getLastTransferType());
                    if (userPhoneVO.getPinReset() != null) {
                    	psmtInsert.setString(22, userPhoneVO.getPinReset());
                    } else {
                    	psmtInsert.setString(22, PretupsI.YES);
                    }
                    psmtInsert.setString(23, userPhoneVO.getMhash());
                    psmtInsert.setString(24, userPhoneVO.getImei());
                    insertCount = psmtInsert.executeUpdate();

                    psmtInsert.clearParameters();
                    // check the status of the update
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
        } 
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUserPhoneList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUserPhoneList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, LOG);
          
        } // end of finally

        return insertCount;
    }
    /**
     * Method for checking Is MSISDN already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_msisdn
     *            String
     * @param p_userId
     *            String
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isMSISDNExist(Connection p_con, String p_msisdn, String p_userId) throws BTSLBaseException {

        final String methodName = "isMSISDNExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append("p_userId=");
        	loggerValue.append(p_userId);
        	LOG.debug(methodName, loggerValue);
        }
        
        
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();
        if (BTSLUtil.isNullString(p_userId))// add mode
        {
            strBuff.append("SELECT UP.msisdn,U.status from user_phones UP,users U ");
            strBuff.append(" WHERE UP.msisdn = ? AND UP.user_id = U.user_id  AND U.status <> 'N' AND U.status <> 'C'");
        } else// edit mode
        {
            strBuff.append("SELECT UP.msisdn,U.status from user_phones UP,users U ");
            strBuff.append(" WHERE UP.msisdn = ? AND U.user_id != ? AND UP.user_id = U.user_id  ");
            strBuff.append(" AND U.status <> 'N' AND U.status <> 'C'");
        }
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
       
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_msisdn);
            if (!BTSLUtil.isNullString(p_userId))// add mode
            {
                pstmt.setString(2, p_userId);
            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isMSISDNExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isMSISDNExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            LogFactory.printLog(methodName, "Exiting: existFlag=" + existFlag, LOG);
        }
    }
    
    public boolean isMSISDNExistOptUser(Connection p_con, String p_msisdn) throws BTSLBaseException {

        final String methodName = "isMSISDNExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	LOG.debug(methodName, loggerValue);
        }
        
        
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT U.msisdn, U.status from users U");
        strBuff.append(" WHERE U.msisdn = ?");
        strBuff.append(" AND U.status <> 'N' AND U.status <> 'C'");
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
       
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_msisdn);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isMSISDNExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isMSISDNExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            LogFactory.printLog(methodName, "Exiting: existFlag=" + existFlag, LOG);
        }
    }
    

    /**
     * Method for loading Users Phone List.(that are assigned to the user)
     * from the table user_phones
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserPhoneList(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserPhoneList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	loggerValue.append("p_userId=");
        	loggerValue.append(p_userId);
        	LOG.debug(methodName, loggerValue);
        }
       
        String pinPasswordEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT up.user_phones_id,up.msisdn,user_id,");
        strBuff.append("up.description,up.primary_number,up.sms_pin,up.pin_required,");
        strBuff.append("up.phone_profile,up.phone_language,up.country,up.invalid_pin_count,");
        strBuff.append("up.last_transaction_status,up.last_transaction_on,up.pin_modified_on,");
        strBuff.append("up.created_by,up.created_on,up.modified_by,up.modified_on, up.last_transfer_id,");
        strBuff.append(" up.last_transfer_type,sp.profile_name ");
        strBuff.append("FROM user_phones up,stk_profiles sp WHERE up.user_id = ?  AND up.phone_profile=sp.profile_code");

        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        ArrayList list = new ArrayList();
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userId);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            UserPhoneVO phoneVO = null;
            while (rs.next()) {
                phoneVO = new UserPhoneVO();
                phoneVO.setUserPhonesId(rs.getString("user_phones_id"));
                phoneVO.setMsisdn(rs.getString("msisdn"));
                phoneVO.setUserId(rs.getString("user_id"));
                phoneVO.setDescription(rs.getString("description"));
                phoneVO.setPrimaryNumber(rs.getString("primary_number"));
                phoneVO.setSmsPin(rs.getString("sms_pin"));
                if (!BTSLUtil.isNullString(phoneVO.getSmsPin())) {
                    /*
                     * modified by ashishT
                     * to set default **** as the pin on jsp.
                     */
                    if ("SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
                        phoneVO.setShowSmsPin("****");
                        phoneVO.setConfirmSmsPin("****");
                    }
                    // set the default value *****
                    else {
                        phoneVO.setShowSmsPin(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin())));
                        phoneVO.setConfirmSmsPin(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin())));
                    }
                }
                phoneVO.setPinRequired(rs.getString("pin_required"));
                phoneVO.setPhoneProfile(rs.getString(PHONE_PROFILE));
                phoneVO.setPhoneLanguage(rs.getString("phone_language"));
                phoneVO.setCountry(rs.getString("country"));
                phoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                phoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
                phoneVO.setLastTransactionOn(rs.getTimestamp("last_transaction_on"));
                phoneVO.setPinModifiedOn(rs.getTimestamp("pin_modified_on"));
                phoneVO.setCreatedBy(rs.getString("created_by"));
                phoneVO.setCreatedOn(rs.getTimestamp("created_on"));
                phoneVO.setModifiedBy(rs.getString("modified_by"));
                phoneVO.setModifiedOn(rs.getTimestamp("modified_on"));
                phoneVO.setLastTransferID(rs.getString("last_transfer_id"));
                phoneVO.setLastTransferType(rs.getString("last_transfer_type"));
                phoneVO.setPhoneProfileDesc(rs.getString("profile_name"));
                list.add(phoneVO);
            }

        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            LogFactory.printLog(methodName, "Exiting: userPhoneList size=" + list.size(), LOG);
        }
        return list;
    }

    /**
     * Method for Updating User Info.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            UserVO
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateUser(Connection p_con, UserVO p_userVO) throws BTSLBaseException {
        // commented for DB2OraclePreparedStatement psmtUpdate = null;
    	String allowdUsrTypCreation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION); 
        int updateCount = 0;
        final String methodName = "updateUser";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userVO=");
        	loggerValue.append(p_userVO);
        	LOG.debug(methodName, loggerValue);
        }
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE users SET login_id = ?,password = ?,");
            strBuff.append("allowed_ip = ?,allowed_days = ?,");
            strBuff.append("from_time = ?,to_time = ?,employee_code = ?,");
            strBuff.append("status = ?,email = ?,pswd_modified_on = ?,contact_no = ?,");
            strBuff.append("designation = ?,division = ?,department = ?,msisdn = ?,");
            strBuff.append("modified_by = ?,modified_on = ?,address1 = ?, ");
            strBuff.append("address2 = ?,city = ?,state = ?,country = ?,ssn = ?,user_name_prefix = ?, ");
            strBuff.append("external_code = ?,short_name = ?, ");
            strBuff.append("level1_approved_by = ?,level1_approved_on = ?, ");
            strBuff.append("level2_approved_by = ?,level2_approved_on = ?,user_code = ? ,network_code = ?, ");
            strBuff.append("contact_person = ?,");
            // Diwakar
            strBuff.append(" user_name = ?,appointment_date = ?,previous_status = ?,longitude = ?,latitude = ?, company=?,fax=?,firstname=?,lastname=?,rsaflag = ?,AUTHENTICATION_ALLOWED=? "); // added
            if (!BTSLUtil.isNullString(p_userVO.getRemarks())) {
                strBuff.append(" , remarks = ? ");
            }
            
            if((PretupsI.YES).equals(allowdUsrTypCreation)) {
				strBuff.append(" , ALLOWD_USR_TYP_CREATION = ? ");
			}
            strBuff.append(" , document_type = ? , document_no = ? , payment_type = ? ");
            strBuff.append(" WHERE user_id = ? "); 
            String updateQuery = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(updateQuery);
    			LOG.debug(methodName, loggerValue);
    		}
           

            // commented for DB2psmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(updateQuery);
            
        	int a=0;
            try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            if (p_userVO.getStatus().equals(PretupsI.USER_STATUS_CANCELED)) {
                psmtUpdate.setString(++a, p_userVO.getUserID());
            } else {
                psmtUpdate.setString(++a, p_userVO.getLoginID());
            }
            psmtUpdate.setString(++a, p_userVO.getPassword());
            psmtUpdate.setString(++a, p_userVO.getAllowedIps());
            psmtUpdate.setString(++a, p_userVO.getAllowedDays());
            psmtUpdate.setString(++a, p_userVO.getFromTime());
            psmtUpdate.setString(++a, p_userVO.getToTime());
            psmtUpdate.setString(++a, p_userVO.getEmpCode());
            psmtUpdate.setString(++a, p_userVO.getStatus());
            psmtUpdate.setString(++a, p_userVO.getEmail());

            if (p_userVO.getPasswordModifiedOn() != null) {
                psmtUpdate.setTimestamp(++a, BTSLUtil.getTimestampFromUtilDate(p_userVO.getPasswordModifiedOn()));
            } else {
                psmtUpdate.setTimestamp(++a, null);
            }
            psmtUpdate.setString(++a, p_userVO.getContactNo());

            // commented for DB2psmtUpdate.setFormOfUse(12,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getDesignation());

            psmtUpdate.setString(++a, p_userVO.getDivisionCode());
            psmtUpdate.setString(++a, p_userVO.getDepartmentCode());
            psmtUpdate.setString(++a, p_userVO.getMsisdn());
            psmtUpdate.setString(++a, p_userVO.getModifiedBy());
            psmtUpdate.setTimestamp(++a, BTSLUtil.getTimestampFromUtilDate(p_userVO.getModifiedOn()));
            // commented for DB2psmtUpdate.setFormOfUse(18,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getAddress1());
            // commented for DB2psmtUpdate.setFormOfUse(19,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getAddress2());
            // commented for DB2psmtUpdate.setFormOfUse(20,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getCity());
            // commented for DB2psmtUpdate.setFormOfUse(21,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getState());
            // commented for DB2psmtUpdate.setFormOfUse(22,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getCountry());
            psmtUpdate.setString(++a, p_userVO.getSsn());
            // commented for DB2psmtUpdate.setFormOfUse(24,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getUserNamePrefix());
            psmtUpdate.setString(++a, p_userVO.getExternalCode());
            // commented for DB2psmtUpdate.setFormOfUse(26,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getShortName());
            psmtUpdate.setString(++a, p_userVO.getLevel1ApprovedBy());
            if (p_userVO.getLevel1ApprovedOn() != null) {
                psmtUpdate.setTimestamp(++a, BTSLUtil.getTimestampFromUtilDate(p_userVO.getLevel1ApprovedOn()));
            } else {
                psmtUpdate.setTimestamp(++a, null);
            }

            psmtUpdate.setString(++a, p_userVO.getLevel2ApprovedBy());
            if (p_userVO.getLevel2ApprovedOn() != null) {
                psmtUpdate.setTimestamp(++a, BTSLUtil.getTimestampFromUtilDate(p_userVO.getLevel2ApprovedOn()));
            } else {
                psmtUpdate.setTimestamp(++a, null);
            }

            psmtUpdate.setString(++a, p_userVO.getUserCode());
            psmtUpdate.setString(++a, p_userVO.getNetworkID());
            psmtUpdate.setString(++a, p_userVO.getContactPerson());
            // commented for DB2psmtUpdate.setFormOfUse(34,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++a, p_userVO.getUserName());
            if (p_userVO.getAppointmentDate() != null) {
                psmtUpdate.setTimestamp(++a, BTSLUtil.getTimestampFromUtilDate(p_userVO.getAppointmentDate()));
            } else {
                psmtUpdate.setTimestamp(++a, null);
            }
            psmtUpdate.setString(++a, p_userVO.getPreviousStatus());
            // added by nilesh:longitude and latitude for user profile updation
            psmtUpdate.setString(++a, p_userVO.getLongitude());
            psmtUpdate.setString(++a, p_userVO.getLatitude());
            // Added by Deepika aggarwal
            psmtUpdate.setString(++a, p_userVO.getCompany());
            psmtUpdate.setString(++a, p_userVO.getFax());
            psmtUpdate.setString(++a, p_userVO.getFirstName());
            psmtUpdate.setString(++a, p_userVO.getLastName());
            // end added by deepika aggarwal
            psmtUpdate.setString(++a, p_userVO.getRsaFlag());
            // Diwakar
            if (BTSLUtil.isNullString(p_userVO.getAuthTypeAllowed())) {
                psmtUpdate.setString(++a, PretupsI.NO);
            } else {
                psmtUpdate.setString(++a, p_userVO.getAuthTypeAllowed());
            }
            // Diwakar
            if (!BTSLUtil.isNullString(p_userVO.getRemarks())) {
                psmtUpdate.setString(++a, p_userVO.getRemarks());
                
            } 
            if((PretupsI.YES).equals(allowdUsrTypCreation)) {
    				psmtUpdate.setString(++a,  p_userVO.getAllowedUserTypeCreation());
    			}
            psmtUpdate.setString(++a, p_userVO.getDocumentType());
            psmtUpdate.setString(++a, p_userVO.getDocumentNo());
            //psmtUpdate.setString(++a, p_userVO.getPaymentType());
            psmtUpdate.setString(++a, p_userVO.getPaymentTypes());
             psmtUpdate.setString(++a, p_userVO.getUserID());
            
            // psmtUpdate.setString(45, p_userVO.getUserID());

            // check wehther the record already updated or not
            boolean modified = this.recordModified(p_con, p_userVO.getUserID(), p_userVO.getLastModified());
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }

            updateCount = psmtUpdate.executeUpdate();
        } // end of try
        }catch (BTSLBaseException be) {
            LOG.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUser]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUser]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	LogFactory.printLog(methodName,  "Exiting: updateCount=" + updateCount, LOG);
        } // end of finally
        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @param p_oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection p_con, String p_userId, long p_oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_oldLastModified=");
        	loggerValue.append(p_oldLastModified);
        	loggerValue.append("p_userId=");
        	loggerValue.append(p_userId);
        	LOG.debug(methodName, loggerValue);
        }
         
       
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM users WHERE user_id = ?";
        Timestamp newLastModified = null;
        if (p_oldLastModified == 0) {
            return false;
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlRecordModified);) {
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlRecordModified);
    			LOG.debug(methodName, loggerValue);
    		}
            // create a prepared statement and execute it
         
            pstmt.setString(1, p_userId);
            try(ResultSet rs = pstmt.executeQuery();)
	        {
	            if (rs.next()) {
	                newLastModified = rs.getTimestamp("modified_on");
	            }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(methodName, " old=" + p_oldLastModified);
	                LOG.debug(methodName, " new=" + newLastModified.getTime());
	            }
	            if (newLastModified.getTime() != p_oldLastModified) {
	                modified = true;
	            }
	            OracleUtil.closeQuietly(rs);
	            return modified;
	        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[recordModified]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[recordModified]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch

        finally {
        	
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method to change the password of a user
     * 
     * @param p_con
     * @param p_userId
     * @param p_newPassword
     * @param p_pswdModifiedOn
     * @return int
     * @throws BTSLBaseException
     */
    public int changePassword(Connection p_con, String p_userId, String p_newPassword, Date p_pswdModifiedOn, String p_modifiedBy, String sessionPassword) throws BTSLBaseException {
        final String methodName = "changePassword";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_modifiedBy=");
        	loggerValue.append(p_modifiedBy);
        	loggerValue.append("p_userId=");
        	loggerValue.append(p_userId);
        	LOG.debug(methodName, loggerValue);
        }
       
        int updateCount = 0;
        try {

        	String queryUpdate = "";
        	if(BTSLUtil.isNullString(sessionPassword)){
        		queryUpdate = "UPDATE users SET pswd_modified_on=?,password=?,modified_on= ?, modified_by= ?, PSWD_RESET=? WHERE user_id = ? ";
        	}else{
        		queryUpdate = "UPDATE users SET pswd_modified_on=?,password=?,modified_on= ?, modified_by= ?, PSWD_RESET=? WHERE user_id = ? and password = ?";
        	}
            
            try( PreparedStatement pstmtUpdate = p_con.prepareStatement(queryUpdate);)
            {
            pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_pswdModifiedOn));
            pstmtUpdate.setString(2, p_newPassword);
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_pswdModifiedOn));
            pstmtUpdate.setString(4, p_modifiedBy);
            pstmtUpdate.setString(5, "N");
            pstmtUpdate.setString(6, p_userId);
            if(!BTSLUtil.isNullString(sessionPassword)){
            	pstmtUpdate.setString(7, sessionPassword);
        	}
            updateCount = pstmtUpdate.executeUpdate();
        } 
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[changePassword]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[changePassword]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug("changePassword()", " Exiting with updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method for loading User Info for a particular user
     * by UserId .
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    /*
     * public UserVO loadUserByUserId(Connection p_con,String p_userId) throws
     * BTSLBaseException
     * {
     * if (LOG.isDebugEnabled())
     * {
     * LOG.debug("loadUserByUserId", "Entered p_userId="+p_userId);
     * }
     * PreparedStatement pstmt = null;
     * ResultSet rs = null;
     * StringBuilder strBuff = new StringBuilder();
     * UserVO userVO = new UserVO();
     * 
     * strBuff.append(" SELECT user_id,user_name,network_code,");
     * strBuff.append("login_id,password,category_code,parent_id,");
     * strBuff.append("owner_id,allowed_ip,allowed_days,");
     * strBuff.append("from_time,to_time,employee_code,");
     * strBuff.append("status,email,pswd_modified_on,contact_no,");
     * strBuff.append("designation,division,department,msisdn,user_type,");
     * strBuff.append("created_by,created_on,modified_by,modified_on,address1, ")
     * ;
     * strBuff.append("address2,city,state,country,ssn,user_name_prefix, ");
     * strBuff.append(
     * "external_code,short_name,level1_approved_by,level1_approved_on,");
     * strBuff.append("level2_approved_by,level2_approved_on,user_code ");
     * strBuff.append("FROM users WHERE user_id = ? ");
     * 
     * String sqlSelect = strBuff.toString();
     * if (LOG.isDebugEnabled())
     * {
     * LOG.debug("loadUserByUserId", "QUERY sqlSelect=" + sqlSelect);
     * }
     * 
     * try
     * {
     * pstmt = p_con.prepareStatement(sqlSelect);
     * pstmt.setString(1, p_userId);
     * 
     * rs = pstmt.executeQuery();
     * 
     * while (rs.next())
     * {
     * userVO = new UserVO();
     * userVO.setUserID(rs.getString("user_id"));
     * userVO.setUserName(rs.getString("user_name"));
     * userVO.setNetworkID(rs.getString("network_code"));
     * userVO.setLoginID(rs.getString("login_id"));
     * userVO.setPassword(rs.getString("password"));
     * userVO.setCategoryCode(rs.getString("category_code"));
     * userVO.setParentID(rs.getString("parent_id"));
     * userVO.setOwnerID(rs.getString("owner_id"));
     * userVO.setAllowedIps(rs.getString("allowed_ip"));
     * userVO.setAllowedDays(rs.getString("allowed_days"));
     * userVO.setFromTime(rs.getString("from_time"));
     * userVO.setToTime(rs.getString("to_time"));
     * userVO.setEmpCode(rs.getString("employee_code"));
     * userVO.setStatus(rs.getString("status"));
     * userVO.setEmail(rs.getString("email"));
     * userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
     * userVO.setContactNo(rs.getString("contact_no"));
     * userVO.setDesignation(rs.getString("designation"));
     * userVO.setDivisionCode(rs.getString("division"));
     * userVO.setDepartmentCode(rs.getString("department"));
     * userVO.setMsisdn(rs.getString("msisdn"));
     * userVO.setUserType(rs.getString("user_type"));
     * userVO.setCreatedBy(rs.getString("created_by"));
     * userVO.setCreatedOn(rs.getTimestamp("created_on"));
     * userVO.setModifiedBy(rs.getString("modified_by"));
     * userVO.setModifiedOn(rs.getTimestamp("modified_on"));
     * userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
     * userVO.setAddress1(rs.getString("address1"));
     * userVO.setAddress2(rs.getString("address2"));
     * userVO.setCity(rs.getString("city"));
     * userVO.setState(rs.getString("state"));
     * userVO.setCountry(rs.getString("country"));
     * userVO.setSsn(rs.getString("ssn"));
     * userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
     * userVO.setExternalCode(rs.getString("external_code"));
     * userVO.setShortName(rs.getString("short_name"));
     * userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
     * userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
     * userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
     * userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
     * userVO.setUserCode(rs.getString("user_code"));
     * 
     * }
     * } catch (SQLException sqe)
     * {
     * LOG.error("loadUserByUserId", "SQLException : " + sqe);
     * sqe.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.FATAL,"UserDAO[loadUserByUserId]","","","",
     * "SQL Exception:"+sqe.getMessage());
     * throw new BTSLBaseException(this, "loadUserByUserId",
     * "error.general.sql.processing");
     * } catch (Exception ex)
     * {
     * LOG.error("loadUserByUserId", "Exception : " + ex);
     * ex.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"UserDAO[loadUserByUserId]","","","","Exception:"
     * +ex.getMessage());
     * throw new BTSLBaseException(this, "loadUserByUserId",
     * "error.general.processing");
     * } finally
     * {
     * try{if (rs != null){rs.close();}} catch (Exception e){}
     * try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
     * if (LOG.isDebugEnabled())
     * {
     * LOG.debug("loadUserByUserId", "Exiting: userVO=" + userVO);
     * }
     * }
     * return userVO;
     * }
     */

    /**
     * This method moved in the CommisionDAO(till this DAO is not prepared)
     * 
     * Method for loading Commision Profile for a particular category
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCommisionProfileListByCategoryID(Connection p_con, String p_categoryCode, String p_networkCode) throws BTSLBaseException {
        flag = true;
        final String methodName = "loadCommisionProfileListByCategoryID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	LOG.debug(methodName, loggerValue);
        }
         
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT comm_profile_set_id, comm_profile_set_name");
        strBuff.append(" FROM commission_profile_set WHERE category_code = ? ");
        strBuff.append(" AND network_code = ? AND status != 'N'");
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_networkCode);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            CommissionProfileSetVO commProfSetVO = null;
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("comm_profile_set_name"), rs.getString("comm_profile_set_id")));
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadCommisionProfileListByCategoryID]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadCommisionProfileListByCategoryID]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userProductsList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method loadUsersDetails.
     * This method is used to load all the information used to display Operator
     * User view
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public UserVO loadUsersDetails(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "loadUsersDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	LOG.debug(methodName, loggerValue);
        }
        
       
        UserVO userVO = null;
        String sqlSelect = userQry.loadUsersDetailsQry();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, p_msisdn);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                userVO = new ChannelUserVO();
                userVO.setUserID(rs.getString("usr_user_id"));
                userVO.setUserName(rs.getString("usr_user_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("passwd"));
                userVO.setCategoryCode(rs.getString("usr_category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("usr_status"));
                userVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                userVO.setCompany(rs.getString("company"));
                userVO.setFax(rs.getString("fax"));
                userVO.setFirstName(rs.getString("firstname"));
                userVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("usr_msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));

                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));


                userVO.setCategoryVO(categoryVO);

                userVO.setParentName(rs.getString("parent_name"));
                userVO.setParentMsisdn(rs.getString("parent_msisdn"));
                userVO.setParentCategoryName(rs.getString("parent_cat"));

                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                userVO.setOwnerCategoryName(rs.getString("owner_cat"));
               
                userVO.setDomainList(new UserDAO().loadDomainListByUserId(p_con,userVO.getUserID()));

            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } 
        return userVO;
    }
    public ArrayList loadDomainListByUserId(Connection p_con,String userID) throws BTSLBaseException
    {
    	 DomainDAO domainDAO = new DomainDAO();
    	 return domainDAO.loadDomainListByUserId(p_con, userID);
    	 
    }
    /************************ Method used while deleting User ********************/

    /**
     * Method for checking Is Child User Status is Active or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * 
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isChildUserActive(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "isChildUserActive";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	loggerValue.append("p_userId=");
        	loggerValue.append(BTSLUtil.maskParam(p_userId));
        	LOG.debug(methodName, loggerValue);
        }
        
          
        boolean existFlag = false;

        String sqlSelect = userQry.isChildUserActiveQry();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_userId);
            pstmt.setString(2, PretupsI.CHANNEL_USER_TYPE);
            pstmt.setString(3, p_userId);
            try(ResultSet rs = pstmt.executeQuery();){
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isChildUserActive]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isChildUserActive]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for checking Is User balance is present or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * 
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isUserBalanceExist(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "isUserBalanceExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	loggerValue.append("p_userId=");
        	loggerValue.append(BTSLUtil.maskParam(p_userId));
        	LOG.debug(methodName, loggerValue);
        }
        
        
        int balance = 0;
        boolean balanceFlag = false;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT balance FROM user_balances ");
        strBuff.append(" WHERE  user_id = ? ");

        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userId);

            try(ResultSet rs = pstmt.executeQuery();)
            {
          while (rs.next()) {
                balance = rs.getInt("balance");
                if(balance>0) {
					balanceFlag = true;
					break;
				}
            }
            return balanceFlag;
            
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserBalanceExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserBalanceExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: balanceFlag=" + balanceFlag);
            }
        }
    }

    /**
     * Method for Deleting/Suspending User Information from Users Table
     * (This is soft delete just update the status, set status =
     * N = delete.
     * S= Suspend.
     * SR = Suspend Request
     * DR = Delete Request
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            ArrayList(consist of UserVO)
     * 
     * @return deleteCount int
     * @throws BTSLBaseException
     */
    public int deleteSuspendUser(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
         
        int deleteCount = 0;

        final String methodName = "deleteSuspendUser";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	loggerValue.append("p_voList.size()=");
        	loggerValue.append(p_voList.size());
        	LOG.debug(methodName, loggerValue);
        }

        try {
            boolean modified = false;
            int listSize = p_voList.size();
            UserVO userVO = null;
            for (int i = 0; i < listSize; i++) {
                userVO = (UserVO) p_voList.get(i);
                modified = this.recordModified(p_con, userVO.getUserID(), userVO.getLastModified());

                // if modified = true means record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                }
            }

            StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE users SET status = ?, previous_status = ? , modified_by = ?,");
            strBuff.append(" modified_on = ? WHERE user_id = ?");

            StringBuilder strBuffDel = new StringBuilder();
            strBuffDel.append("UPDATE users SET status = ?, previous_status = ? , modified_by = ?,");
            strBuffDel.append(" modified_on = ?,login_id=? WHERE user_id = ?");

            // added by vikram

            StringBuilder strBuffDelStaff = new StringBuilder();
            strBuffDelStaff.append("UPDATE users SET status = ?, previous_status = ? , modified_by = ?,");
            strBuffDelStaff.append(" modified_on = ?, login_id=user_id WHERE user_id in (select user_id from users where parent_id= ? and user_type= ? and status not in ('N','C')) ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(strBuff.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(strBuffDel.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(strBuffDelStaff.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmtOther = p_con.prepareStatement(strBuff.toString());
            		PreparedStatement pstmtDelete = p_con.prepareStatement(strBuffDel.toString());
            		PreparedStatement pstmtDeleteStaff = p_con.prepareStatement(strBuffDelStaff.toString());)
            {
            for (int i = 0; i < listSize; i++) {
                userVO = (UserVO) p_voList.get(i);
                if (PretupsI.USER_STATUS_DELETED.equals(userVO.getStatus())) {
                    pstmtDelete.setString(1, userVO.getStatus());
                    pstmtDelete.setString(2, userVO.getPreviousStatus());
                    pstmtDelete.setString(3, userVO.getModifiedBy());
                    pstmtDelete.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(userVO.getModifiedOn()));
                    pstmtDelete.setString(5, userVO.getUserID());
                    pstmtDelete.setString(6, userVO.getUserID());
                    deleteCount = pstmtDelete.executeUpdate();
                    pstmtDelete.clearParameters();

                    // for staff deletion
                    pstmtDeleteStaff.setString(1, userVO.getStatus());
                    pstmtDeleteStaff.setString(2, userVO.getPreviousStatus());
                    pstmtDeleteStaff.setString(3, userVO.getModifiedBy());
                    pstmtDeleteStaff.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(userVO.getModifiedOn()));
                    pstmtDeleteStaff.setString(5, userVO.getUserID());
                    pstmtDeleteStaff.setString(6, PretupsI.STAFF_USER_TYPE);
                    int deleteStaffCount = pstmtDeleteStaff.executeUpdate();
                    if (deleteStaffCount < 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.processing");
                    }

                } else {
                    pstmtOther.setString(1, userVO.getStatus());
                    pstmtOther.setString(2, userVO.getPreviousStatus());
                    pstmtOther.setString(3, userVO.getModifiedBy());
                    pstmtOther.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(userVO.getModifiedOn()));
                    pstmtOther.setString(5, userVO.getUserID());
                    deleteCount = pstmtOther.executeUpdate();
                    pstmtOther.clearParameters();
                }// end if

                // check the status of the update
                if (deleteCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.processing");
                }
            }
        } 
        }// end of try
        catch (BTSLBaseException be) {
            LOG.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deleteSuspendUser]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deleteSuspendUser]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method loadUserPhoneVO()
     * This method is to load the user' Phone Vo to send the sms to him.
     * 
     * @param p_con
     * @param p_userId
     * @return UserPhoneVO
     * @throws BTSLBaseException
     */
    public UserPhoneVO loadUserPhoneVO(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserPhoneVO";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	loggerValue.append("p_userId=");
        	loggerValue.append(BTSLUtil.maskParam(p_userId));
        	LOG.debug(methodName, loggerValue);
        }
        
         
        StringBuilder strBuff = new StringBuilder(" SELECT user_phones_id,msisdn,user_id,");
        strBuff.append("description,primary_number,sms_pin,pin_required,");
        strBuff.append("phone_profile,phone_language,country,invalid_pin_count,");
        strBuff.append("last_transaction_status,last_transaction_on,pin_modified_on,");
        strBuff.append("created_by,created_on,modified_by,modified_on, last_transfer_id, first_invalid_pin_time, ");
        strBuff.append(" pin_reset,last_transfer_type ");
        strBuff.append("FROM user_phones WHERE user_id = ? AND primary_number = 'Y'");

        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        UserPhoneVO phoneVO = null;
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_userId);
           try(ResultSet rs = pstmt.executeQuery();)
           {

            if (rs.next()) {
                phoneVO = new UserPhoneVO();
                phoneVO.setUserPhonesId(rs.getString("user_phones_id"));
                phoneVO.setMsisdn(rs.getString("msisdn"));
                phoneVO.setUserId(rs.getString("user_id"));
                phoneVO.setDescription(rs.getString("description"));
                phoneVO.setPrimaryNumber(rs.getString("primary_number"));
                phoneVO.setSmsPin(rs.getString("sms_pin"));
                if (!BTSLUtil.isNullString(phoneVO.getSmsPin())) {
                    // set the default value *****
                    phoneVO.setShowSmsPin(BTSLUtil.getDefaultPasswordNumeric(phoneVO.getSmsPin()));
                    phoneVO.setConfirmSmsPin(BTSLUtil.getDefaultPasswordNumeric(phoneVO.getSmsPin()));
                }
                phoneVO.setPinRequired(rs.getString("pin_required"));
                phoneVO.setPhoneProfile(rs.getString(PHONE_PROFILE));
                phoneVO.setPhoneLanguage(rs.getString("phone_language"));
                phoneVO.setCountry(rs.getString("country"));
                phoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                phoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
                phoneVO.setLastTransactionOn(rs.getTimestamp("last_transaction_on"));
                phoneVO.setPinModifiedOn(rs.getTimestamp("pin_modified_on"));
                phoneVO.setCreatedBy(rs.getString("created_by"));
                phoneVO.setCreatedOn(rs.getTimestamp("created_on"));
                phoneVO.setModifiedBy(rs.getString("modified_by"));
                phoneVO.setModifiedOn(rs.getTimestamp("modified_on"));
                phoneVO.setFirstInvalidPinTime(rs.getTimestamp("first_invalid_pin_time"));
                phoneVO.setLastTransferID(rs.getString("last_transfer_id"));
                phoneVO.setLastTransferType(rs.getString("last_transfer_type"));
                phoneVO.setPinReset(rs.getString("pin_reset"));
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userPhoneList size=" + phoneVO);
            }
        }

        return phoneVO;
    }

    /**
     * Method for loading Phone Profile a particular category
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadPhoneProfileList(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadPhoneProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	loggerValue.append("p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	LOG.debug(methodName, loggerValue);
        }
        
         
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT profile_name, profile_code");
        strBuff.append(" FROM stk_profiles WHERE category_code = ? ");

        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        ArrayList list = new ArrayList();
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_categoryCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("profile_name"), rs.getString("profile_code")));
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadPhoneProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadPhoneProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userProductsList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method to load the user details from employee code
     * 
     * @param p_con
     * @param p_empCode
     * @param p_catCode
     * @param locale
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public ChannelUserVO loadUserDetailsByEmpcode(Connection p_con, String p_empCode, String p_catCode, Locale locale) throws Exception {
        final String methodName = "loadUserDetailsByEmpcode";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_empCode=");
        	loggerValue.append(p_empCode);
        	loggerValue.append("p_catCode=");
        	loggerValue.append(p_catCode);
        	loggerValue.append("locale=");
        	loggerValue.append(locale);
        	LOG.debug(methodName, loggerValue);
        }
        
       
        String sqlBuffer =userQry.loadUserDetailsByEmpcodeQry(p_catCode);
        ChannelUserVO channelUserVO = null;
        try( PreparedStatement pstmt = p_con.prepareStatement(sqlBuffer);) {
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlBuffer);
    			LOG.debug(methodName, loggerValue);
    		}
            
            int i = 0;
            pstmt.setString(++i, p_empCode);
            if(!BTSLUtil.isNullString(p_catCode))
            {
            	pstmt.setString(++i, p_catCode);
            }
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(++i, PretupsI.STATUS_DELETE);

           try( ResultSet rs = pstmt.executeQuery();){
            String userID;
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                userID = rs.getString("user_id");
                channelUserVO.setUserID(userID);
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setNetworkName(rs.getString("network_name"));
                channelUserVO.setReportHeaderName(rs.getString("report_header_name"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(p_catCode);
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setParentName(rs.getString("parent_name"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setValidRequestURLs(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                if (rs.getTimestamp("last_login_on") != null) {
                    channelUserVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                }
                channelUserVO.setEmpCode(p_empCode);
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                channelUserVO.setCreatedBy(userID);
                channelUserVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(userID);
                if (rs.getTimestamp("created_on") != null) {
                    channelUserVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                }
                // channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setUserType(rs.getString("user_type"));
                // channelUserVO.setInSuspend(rs.getString("in_suspend"));
                // channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setNetworkStatus(rs.getString("networkstatus"));
                LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    channelUserVO.setMessage(rs.getString("language_1_message"));
                } else {
                    channelUserVO.setMessage(rs.getString("language_2_message"));
                }
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));
                channelUserVO.setDomainName(rs.getString("domain_name"));
                channelUserVO.setDomainStatus(rs.getString("domainstatus"));
                // channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                // channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                // channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
                if (rs.getTimestamp("password_count_updated_on") != null) {
                    channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));
                }
                // channelUserVO.setSmsPin(rs.getString("sms_pin"));
                // channelUserVO.setPinRequired(rs.getString("pin_required"));
                // channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setRestrictedMsisdnAllow(rs.getString("restricted_msisdn_allow"));
                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setCategoryStatus(rs.getString("categorystatus"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setMaxLoginCount(rs.getLong("max_login_count"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));

                categoryVO.setCategoryStatus(rs.getString("catstatus"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));

                channelUserVO.setCategoryVO(categoryVO);
                try {
                    channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    channelUserVO.setPasswordModifiedOn(null);
                }
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(p_con, channelUserVO.getUserID()));
            }
           }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByEmpcode]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByEmpcode]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUloadUserDetailsByEmpcode", "error.general.processing",ex);
        } 
       
        return channelUserVO;
    }

    /**
     * Check for existance of Assigned role
     * 
     * @param con
     * @param userId
     * @param p_roleCode
     * @param p_domainType
     * @return boolean
     */
    public boolean isAssignedRoleAndExist(Connection p_con, String p_userID, String p_roleCode, String p_domainType) {
        final String methodName = "isAssignedRoleAndExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append("p_roleCode=");
        	loggerValue.append(p_roleCode);
        	loggerValue.append("p_domainType=");
        	loggerValue.append(p_domainType);
        	LOG.debug(methodName, loggerValue);
        }
        PreparedStatement psmt = null;
        
        ResultSet rs = null;
        ResultSet rsIsExist = null;
        boolean roleStatus = false;
        try {
            // query whether user has group role or not
            StringBuilder isGroupRoleQueryBuff = new StringBuilder("SELECT 1 FROM USER_ROLES,ROLES ");
            isGroupRoleQueryBuff.append("WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
            isGroupRoleQueryBuff.append("AND group_role='Y'AND (ROLES.status IS NULL OR ROLES.status='Y')");

            // query to validate if the role assigned is not group type
            StringBuilder queryBuff = new StringBuilder("SELECT 1 FROM roles R, user_roles UR WHERE UR.user_id=? AND UR.role_code=? ");
            queryBuff.append(" AND R.domain_type=? AND R.role_code=UR.role_code AND (R.status IS NULL OR R.status='Y')");

            // query to validate if the role assigned is group type
            StringBuilder groupRoleQueryBuff = new StringBuilder("SELECT 1 FROM user_roles UR, roles R,group_roles GR, roles R1 WHERE UR.user_id=? AND R1.role_code=? AND R.domain_type=? ");
            groupRoleQueryBuff.append("AND UR.role_code=R.role_code AND R.group_role='Y' AND (R.status IS NULL OR R.status='Y') AND R.role_code=GR.group_role_code AND GR.role_code=R1.role_code AND R1.status='Y' AND R1.domain_type=? ");

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(" groupRoleQueryBuff : ");
    			loggerValue.append(groupRoleQueryBuff);
    			LOG.debug(methodName, loggerValue);
    		}
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(" isGroupRoleQueryBuff : ");
    			loggerValue.append(isGroupRoleQueryBuff);
    			LOG.debug(methodName, loggerValue);
    		}
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(" notGroupRoleQuery:");
    			loggerValue.append(queryBuff.toString());
    			LOG.debug(methodName, loggerValue);
    		}

            try( PreparedStatement psmtIsExist = p_con.prepareStatement(isGroupRoleQueryBuff.toString());)
            {
            psmtIsExist.setString(1, p_userID);
            psmtIsExist.setString(2, p_domainType);

            rsIsExist = psmtIsExist.executeQuery();
            if (rsIsExist.next()) {
                psmt = p_con.prepareStatement(groupRoleQueryBuff.toString());
                psmt.setString(4, p_domainType);
            } else {
                psmt = p_con.prepareStatement(queryBuff.toString());
            }
            psmt.setString(1, p_userID);
            psmt.setString(2, p_roleCode);
            psmt.setString(3, p_domainType);

            rs = psmt.executeQuery();
            while (rs.next()) {
                roleStatus = true;
            }
        }
        }catch (Exception ex2) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex2.getMessage());
			LOG.error(methodName, loggerValue);
            roleStatus = false;
        } finally {
        	try{
            	if (rsIsExist!= null){
            		rsIsExist.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
        	
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
                if (psmt!= null){
                	psmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting role status=" + roleStatus);
            }
        } // end of finally
        return roleStatus;
    }

    /**
     * Check for existance of fixed role
     * 
     * @param con
     * @param p_categoryCode
     * @param p_roleCode
     * @param p_domainType
     * @return boolean
     */
    public boolean isFixedRoleAndExist(java.sql.Connection p_con, String p_categoryCode, String p_roleCode, String p_domainType) {
        final String methodName = "isFixedRoleAndExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append("p_roleCode=");
        	loggerValue.append(p_roleCode);
        	loggerValue.append("p_domainType=");
        	loggerValue.append(p_domainType);
        	LOG.debug(methodName, loggerValue);
        }
         
        
        boolean roleStatus = false;
        try {
            StringBuilder queryBuff = new StringBuilder("SELECT 1 FROM category_roles CR,roles R WHERE CR.category_code=? AND CR.role_code=?");
            queryBuff.append(" AND R.domain_type=? AND CR.role_code=R.role_code AND (R.status IS NULL OR R.status='Y')");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(queryBuff.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement psmt = p_con.prepareStatement(queryBuff.toString());)
            {
            psmt.setString(1, p_categoryCode);
            psmt.setString(2, p_roleCode);
            psmt.setString(3, p_domainType);
            try(ResultSet rs = psmt.executeQuery();)
            {
            while (rs.next()) {
                roleStatus = true;
            }
        } 
            }
        }catch (Exception ex2) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex2.getMessage());
			LOG.error(methodName, loggerValue);
            roleStatus = false;
        } finally {
        	LOG.debug(methodName, "inside finally block");
        } // end of finally
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting role status=" + roleStatus);
        }
        return roleStatus;
    }

    /**
     * Date : May 2, 2007
     * Discription :
     * Method : loadUserDetailsForUserID
     * 
     * @param p_con
     * @param p_userID
     * @throws BTSLBaseException
     * @return ChannelUserVO
     * @author ved.sharma
     */
    public ChannelUserVO loadUserDetailsFormUserID(Connection p_con, String p_userID) throws BTSLBaseException {
        final String methodName = "loadUserDetailsFormUserID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userID=");
        	loggerValue.append(BTSLUtil.maskParam(p_userID));
        	LOG.debug(methodName, loggerValue);
        }
        
        ChannelUserVO channelUserVO = null;
         
        try {
            
            String selectQuery = userQry.loadUserDetailsFormUserIDQry();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_userID);
            pstmtSelect.setString(2, PretupsI.USER_STATUS_TYPE);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                channelUserVO.setCreationType(SqlParameterEncoder.encodeParams(rs.getString("creation_type")));
                channelUserVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                channelUserVO.setNetworkID(SqlParameterEncoder.encodeParams(rs.getString("network_code")));
                channelUserVO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
                channelUserVO.setPassword(SqlParameterEncoder.encodeParams(rs.getString("password")));
                channelUserVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                channelUserVO.setParentID(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
                channelUserVO.setOwnerID(SqlParameterEncoder.encodeParams(rs.getString("owner_id")));
                channelUserVO.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("msisdn")));
                channelUserVO.setPinRequired(SqlParameterEncoder.encodeParams(rs.getString("pin_required")));
                channelUserVO.setSmsPin(SqlParameterEncoder.encodeParams(rs.getString("sms_pin")));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setEmpCode(SqlParameterEncoder.encodeParams(rs.getString("employee_code")));
                channelUserVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("userstatus")));
                channelUserVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                channelUserVO.setContactPerson(SqlParameterEncoder.encodeParams(rs.getString("contact_person")));
                channelUserVO.setContactNo(SqlParameterEncoder.encodeParams(rs.getString("contact_no")));
                channelUserVO.setDesignation(SqlParameterEncoder.encodeParams(rs.getString("designation")));
                channelUserVO.setDivisionCode(SqlParameterEncoder.encodeParams(rs.getString("division")));
                channelUserVO.setDepartmentCode(SqlParameterEncoder.encodeParams(rs.getString("department")));
                channelUserVO.setUserType(SqlParameterEncoder.encodeParams(rs.getString("user_type")));
                channelUserVO.setInSuspend(SqlParameterEncoder.encodeParams(rs.getString("in_suspend")));
                channelUserVO.setOutSuspened(SqlParameterEncoder.encodeParams(rs.getString("out_suspend")));
                channelUserVO.setAddress1(SqlParameterEncoder.encodeParams(rs.getString("address1")));
                channelUserVO.setAddress2(SqlParameterEncoder.encodeParams(rs.getString("address2")));
                channelUserVO.setCity(SqlParameterEncoder.encodeParams(rs.getString("city")));
                channelUserVO.setState(SqlParameterEncoder.encodeParams(rs.getString("state")));
                channelUserVO.setCountry(SqlParameterEncoder.encodeParams(rs.getString("country")));
                // Added for RSA Authentication
                channelUserVO.setRsaFlag(SqlParameterEncoder.encodeParams(rs.getString("rsaflag")));
                channelUserVO.setSsn(SqlParameterEncoder.encodeParams(rs.getString("ssn")));
                channelUserVO.setUserNamePrefix(SqlParameterEncoder.encodeParams(rs.getString("user_name_prefix")));
                channelUserVO.setExternalCode(SqlParameterEncoder.encodeParams(rs.getString("external_code")));
                channelUserVO.setUserCode(SqlParameterEncoder.encodeParams(rs.getString("user_code")));
                channelUserVO.setShortName(SqlParameterEncoder.encodeParams(rs.getString("short_name")));
                channelUserVO.setReferenceID(SqlParameterEncoder.encodeParams(rs.getString("reference_id")));
                channelUserVO.setDomainID(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
                channelUserVO.setEmail(SqlParameterEncoder.encodeParams(rs.getString("email")));
                // Added by deepika aggarwal
                channelUserVO.setCompany(SqlParameterEncoder.encodeParams(rs.getString("company")));
                channelUserVO.setFax(SqlParameterEncoder.encodeParams(rs.getString("fax")));
                channelUserVO.setFirstName(SqlParameterEncoder.encodeParams(rs.getString("firstname")));
                channelUserVO.setLastName(SqlParameterEncoder.encodeParams(rs.getString("lastname")));
                // end added by deepika aggarwal
                channelUserVO.setAllowedIps(SqlParameterEncoder.encodeParams(rs.getString("allowed_ip")));
                channelUserVO.setAllowedDays(SqlParameterEncoder.encodeParams(rs.getString("allowed_days")));
                channelUserVO.setAppointmentDate(rs.getDate("appointment_date"));
                channelUserVO.setFromTime(SqlParameterEncoder.encodeParams(rs.getString("from_time")));
                channelUserVO.setToTime(SqlParameterEncoder.encodeParams(rs.getString("to_time")));
                channelUserVO.setAuthTypeAllowed((SqlParameterEncoder.encodeParams(rs.getString("AUTHENTICATION_ALLOWED"))));
                channelUserVO.setTransferProfileID(SqlParameterEncoder.encodeParams(rs.getString("transfer_profile_id")));
                channelUserVO.setCommissionProfileSetID(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_id")));
                channelUserVO.setUserGrade(SqlParameterEncoder.encodeParams(rs.getString("user_grade")));

                // for Zebra and Tango by sanjeew date 06/07/07
                channelUserVO.setApplicationID(SqlParameterEncoder.encodeParams(rs.getString("application_id")));
                channelUserVO.setMpayProfileID(SqlParameterEncoder.encodeParams(rs.getString("mpay_profile_id")));
                channelUserVO.setUserProfileID(SqlParameterEncoder.encodeParams(rs.getString("user_profile_id")));
                channelUserVO.setMcommerceServiceAllow(SqlParameterEncoder.encodeParams(rs.getString("mcommerce_service_allow")));
                // End Zebra and Tango
                channelUserVO.setPrefixId(rs.getLong("PREFIX_ID"));
                channelUserVO.setStatusDesc(SqlParameterEncoder.encodeParams(rs.getString("lookup_name")));
                channelUserVO.setPhoneProfile(SqlParameterEncoder.encodeParams(rs.getString(PHONE_PROFILE)));
                channelUserVO.setPinRequired(SqlParameterEncoder.encodeParams(rs.getString("pin_required")));
                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                categoryVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                categoryVO.setDomainCodeforCategory(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
                categoryVO.setSequenceNumber(rs.getInt("catseq"));
                categoryVO.setGrphDomainType(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type")));
                categoryVO.setSmsInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("sms_interface_allowed")));
                categoryVO.setWebInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("web_interface_allowed")));
                categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
                categoryVO.setHierarchyAllowed(SqlParameterEncoder.encodeParams(rs.getString("hierarchy_allowed")));
                categoryVO.setAgentAllowed(SqlParameterEncoder.encodeParams(rs.getString("agent_allowed")));
                categoryVO.setCategoryType(SqlParameterEncoder.encodeParams(rs.getString("category_type")));
                categoryVO.setRestrictedMsisdns(SqlParameterEncoder.encodeParams(rs.getString("restricted_msisdns")));
                categoryVO.setTransferToListOnly(SqlParameterEncoder.encodeParams(rs.getString("transfertolistonly")));
                // added by harsh to display created by & created on when staff
                // user is selected by username
                channelUserVO.setCreatedByUserName(SqlParameterEncoder.encodeParams(rs.getString("created_by_name")));
                channelUserVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn()));
                channelUserVO.setCategoryVO(categoryVO);

                channelUserVO.setLanguage(SqlParameterEncoder.encodeParams(rs.getString("phone_language")));
                channelUserVO.setCountryCode(SqlParameterEncoder.encodeParams(rs.getString("coun")));
                
                channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
                channelUserVO.setParentLoginID(SqlParameterEncoder.encodeParams(rs.getString("parentloginID"))); 

            }
            return channelUserVO;
        }
            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting channelUserVO:" + channelUserVO);
            }
        }// end of finally
    }

    /**
     * Method for checking Pasword or already exist in Pin_Password_history
     * table or not.
     * 
     * @author santanu.mohanty
     * @param p_con
     *            java.sql.Connection
     * @param p_modificationType
     *            String
     * @param p_userId
     *            String
     * @param p_Msisdn
     *            String
     * @param p_newPassword
     *            String
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean checkPasswordHistory(Connection p_con, String p_modificationType, String p_userId, String p_Msisdn, String p_newPassword) throws BTSLBaseException {
        final String methodName = "checkPasswordHistory";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_modificationType=");
        	loggerValue.append(p_modificationType);
        	loggerValue.append(",p_userId=");
        	loggerValue.append(BTSLUtil.maskParam(p_userId));
        	loggerValue.append(",p_Msisdn=");
        	loggerValue.append(p_Msisdn);
        	LOG.debug(methodName, loggerValue);
        }
        int prevPinNotAllow = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue();
        int prevPasswordNotAllow = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue();
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

        if (p_modificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
            strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
            strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND user_id=? ) temp  WHERE rn <= ? ");
            strBuff.append(" ORDER BY modified_on DESC ");
        } else {
            strBuff.append(" SELECT pin_or_password,modified_on  FROM (SELECT pin_or_password,modified_on, row_number()  over (ORDER BY modified_on DESC) rn  ");
            strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND user_id=? and msisdn_or_loginid= ? )  temp  WHERE rn <= ? ");
            strBuff.append(" ORDER BY modified_on DESC ");
        }
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {

            
            pstmt.setString(1, p_modificationType);
            pstmt.setString(2, p_userId);
            if (p_modificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
                pstmt.setInt(3, prevPasswordNotAllow);
            } else {
                pstmt.setString(3, p_Msisdn);
                pstmt.setInt(4, prevPinNotAllow);
            }
           try(ResultSet rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
                if (rs.getString("Pin_or_Password").equals(p_newPassword)) {
                    existFlag = true;
                    break;
                }
            }
            return existFlag;
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Date : Dec 11, 2007
     * Discription :
     * Method : updateInsertDeleteUserPhoneList
     * 
     * @param p_con
     * @param p_phoneList
     * @throws BTSLBaseException
     * @return int
     * @author ved.sharma
     */
    public int updateInsertDeleteUserPhoneList(Connection p_con, ArrayList p_phoneList) throws BTSLBaseException {
       
        
        int insertCount = 0;
        int updateCount = 0;
        int deleteCount = 0;
        final String methodName = "updateInsertDeleteUserPhoneList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_phoneList.size()=");
        	loggerValue.append(p_phoneList.size());
        	loggerValue.append(",p_phoneList=");
        	loggerValue.append(p_phoneList);
        	LOG.debug(methodName, loggerValue);
        }
        try {
            if ((p_phoneList != null)) {
                StringBuilder deleteStrBuff = new StringBuilder("DELETE FROM user_phones  WHERE user_phones_id=? ");
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Query deleteStrBuff:" + deleteStrBuff);
                }
               try(  PreparedStatement psmtDelete = p_con.prepareStatement(deleteStrBuff.toString());)
               {
                StringBuilder updateStrBuff = new StringBuilder("UPDATE user_phones SET msisdn=?, primary_number=?, ");
                updateStrBuff.append("description=?,sms_pin=?,pin_required=?,phone_profile=?,phone_language=?,country=?,invalid_pin_count=?,");
                updateStrBuff.append("pin_modified_on=?,modified_by=?,modified_on=?,prefix_id=? ");
                updateStrBuff.append(" WHERE user_phones_id=?");
                String updateQuery = updateStrBuff.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateQuery);
        			LOG.debug(methodName, loggerValue);
        		}
                try( PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
                {
                StringBuilder insertStrBuff = new StringBuilder("INSERT INTO user_phones (user_phones_id,");
                insertStrBuff.append("msisdn,user_id,description,primary_number,sms_pin,pin_required,");
                insertStrBuff.append("phone_profile,phone_language,country,invalid_pin_count,");
                insertStrBuff.append("last_transaction_status,last_transaction_on,pin_modified_on,");
                insertStrBuff.append("created_by,created_on,modified_by,modified_on, last_transfer_id,");
                insertStrBuff.append(" prefix_id,last_transfer_type) values ");
                insertStrBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                String insertQuery = insertStrBuff.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(insertQuery);
        			LOG.debug(methodName, loggerValue);
        		}

                try( PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
                {
                StringBuilder selectPhoneExistsStrBuff = new StringBuilder("SELECT 1 from user_phones UP,users U ");
                selectPhoneExistsStrBuff.append(" WHERE UP.msisdn = ? AND U.user_id = ? AND UP.user_id = U.user_id  ");
                selectPhoneExistsStrBuff.append(" AND U.status <> 'N' AND U.status <> 'C'");
                String selectPhoneExistsQuery = selectPhoneExistsStrBuff.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(selectPhoneExistsQuery);
        			LOG.debug(methodName, loggerValue);
        		}

                try( PreparedStatement psmtSelectPhone = p_con.prepareStatement(selectPhoneExistsQuery);)
                {
                String updateSIMTxnIDQuery = "UPDATE user_phones SET temp_transfer_id =? WHERE msisdn=? ";
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateSIMTxnIDQuery);
        			LOG.debug(methodName, loggerValue);
        		}

                try( PreparedStatement psmtSIMTxnID = p_con.prepareStatement(updateSIMTxnIDQuery);)
                {
                boolean phoneExists = false;
                int intex = 0;
                UserPhoneVO userPhoneVO = null;
                for (int i = 0, j = p_phoneList.size(); i < j; i++) {
                    userPhoneVO = (UserPhoneVO) p_phoneList.get(i);
                    intex = 0;
                    phoneExists = false;
                    if (!BTSLUtil.isNullString(userPhoneVO.getOperationType()) && "D".equals(userPhoneVO.getOperationType())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "DELETE    userPhoneVO=          " + userPhoneVO);
                        }

                        psmtDelete.setString(1, userPhoneVO.getUserPhonesId());
                        deleteCount = psmtDelete.executeUpdate();
                        psmtDelete.clearParameters();
                    } else if (!BTSLUtil.isNullString(userPhoneVO.getOperationType()) && "U".equals(userPhoneVO.getOperationType())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "Update    userPhoneVO=          " + userPhoneVO);
                        }

                        psmtSelectPhone.setString(1, userPhoneVO.getMsisdn());
                        psmtSelectPhone.setString(2, userPhoneVO.getUserId());
                        try(ResultSet rsSelectPhone = psmtSelectPhone.executeQuery();)
                        {
                        if (rsSelectPhone.next()) {
                            phoneExists = true;
                        }

                        psmtUpdate.setString(++intex, userPhoneVO.getMsisdn());
                        psmtUpdate.setString(++intex, userPhoneVO.getPrimaryNumber());
                        psmtUpdate.setString(++intex, userPhoneVO.getDescription());
                        psmtUpdate.setString(++intex, userPhoneVO.getSmsPin());
                        psmtUpdate.setString(++intex, userPhoneVO.getPinRequired());
                        psmtUpdate.setString(++intex, userPhoneVO.getPhoneProfile());
                        psmtUpdate.setString(++intex, userPhoneVO.getPhoneLanguage());
                        psmtUpdate.setString(++intex, userPhoneVO.getCountry());
                        psmtUpdate.setInt(++intex, userPhoneVO.getInvalidPinCount());
                        psmtUpdate.setTimestamp(++intex, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getPinModifiedOn()));
                        psmtUpdate.setString(++intex, userPhoneVO.getModifiedBy());
                        psmtUpdate.setTimestamp(++intex, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getModifiedOn()));
                        psmtUpdate.setLong(++intex, userPhoneVO.getPrefixID());
                        psmtUpdate.setString(++intex, userPhoneVO.getUserPhonesId());

                        updateCount = psmtUpdate.executeUpdate();
                        psmtUpdate.clearParameters();

                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "phoneExists = " + phoneExists);
                        }
                        if (!phoneExists) {
                            psmtSIMTxnID.setString(1, PretupsI.UPD_SIM_TXN_ID);
                            psmtSIMTxnID.setString(2, userPhoneVO.getMsisdn());

                            updateCount = psmtSIMTxnID.executeUpdate();
                            psmtSIMTxnID.clearParameters();
                        }
                    } }else if (!BTSLUtil.isNullString(userPhoneVO.getOperationType()) && "I".equals(userPhoneVO.getOperationType())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "Insert    userPhoneVO=          " + userPhoneVO);
                        }
                        psmtInsert.setString(++intex, userPhoneVO.getUserPhonesId());
                        psmtInsert.setString(++intex, userPhoneVO.getMsisdn());
                        psmtInsert.setString(++intex, userPhoneVO.getUserId());
                        psmtInsert.setString(++intex, userPhoneVO.getDescription());
                        psmtInsert.setString(++intex, userPhoneVO.getPrimaryNumber());
                        psmtInsert.setString(++intex, userPhoneVO.getSmsPin());
                        psmtInsert.setString(++intex, userPhoneVO.getPinRequired());
                        psmtInsert.setString(++intex, userPhoneVO.getPhoneProfile());
                        psmtInsert.setString(++intex, userPhoneVO.getPhoneLanguage());
                        psmtInsert.setString(++intex, userPhoneVO.getCountry());
                        psmtInsert.setInt(++intex, userPhoneVO.getInvalidPinCount());
                        psmtInsert.setString(++intex, userPhoneVO.getLastTransactionStatus());
                        if (userPhoneVO.getLastTransactionOn() != null) {
                            psmtInsert.setTimestamp(++intex, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getLastTransactionOn()));
                        } else {
                            psmtInsert.setTimestamp(++intex, null);
                        }
                        psmtInsert.setTimestamp(++intex, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getPinModifiedOn()));
                        psmtInsert.setString(++intex, userPhoneVO.getCreatedBy());
                        psmtInsert.setTimestamp(++intex, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getCreatedOn()));
                        psmtInsert.setString(++intex, userPhoneVO.getModifiedBy());
                        psmtInsert.setTimestamp(++intex, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getModifiedOn()));
                        psmtInsert.setString(++intex, userPhoneVO.getLastTransferID());
                        psmtInsert.setLong(++intex, userPhoneVO.getPrefixID());
                        psmtInsert.setString(++intex, userPhoneVO.getLastTransferType());

                        insertCount = psmtInsert.executeUpdate();

                        psmtInsert.clearParameters();
                        // check the status of the update
                        if (insertCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                        }
                    }
                }
            }
        }
        }
        }
        }
        }
        }
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateInsertDeleteUserPhoneList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateInsertDeleteUserPhoneList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	         
        	
        
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: insertCount+updateCount+deleteCount=" + insertCount + updateCount + deleteCount);
            }
        } // end of finally

        return insertCount + updateCount + deleteCount;
    }

    
    /**
     * 
     * @param userId
     * @param con
     * @return
     */
	public String retrieveEmail(String userId, Connection con) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String qry = "SELECT EMAIL FROM USERS WHERE USER_ID = ? ";

		try {

			pstmt = con.prepareStatement(qry);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					return rs.getString(1);
				}
			}
		} catch (Exception e) {
			LOG.debug("retrieveMsisdn", "Exception while fetching emailID " + e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					LOG.debug("retrieveMsisdn", "Could not close preparedstatement " + e);
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					LOG.debug("retrieveMsisdn", "Could not close resultSet " + e);
				}
			}
		}

		return null;
	}

	
	public String retrieveMsisdn(String userId, Connection con) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String qry = "SELECT MSISDN FROM USERS WHERE USER_ID = ? ";

		try {

			pstmt = con.prepareStatement(qry);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					return rs.getString(1);
				}
			}
		} catch (Exception e) {
			LOG.debug("retrieveMsisdn", "Exception while fetching emailID " + e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					LOG.debug("retrieveMsisdn", "Could not close preparedstatement " + e);
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					LOG.debug("retrieveMsisdn", "Could not close resultSet " + e);
				}
			}
		}

		return null;
	}

	
    public UserPhoneVO loadUserAnyPhoneVO(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "loadUserAnyPhoneVO";
        // Added by Amit Raheja for NNP changes
        try {
            OperatorUtilI operatorUtil = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
            p_msisdn = operatorUtil.getSystemFilteredMSISDN(p_msisdn);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserAnyPhoneVO]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        // Addition ends
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	LOG.debug(methodName, loggerValue);
        }
         
         
        StringBuilder strBuff = new StringBuilder(" SELECT ph.user_phones_id,ph.msisdn,ph.user_id,");
        strBuff.append(" ph.description,ph.primary_number,ph.sms_pin,ph.pin_required,");
        strBuff.append(" ph.phone_profile,ph.phone_language,ph.country,ph.invalid_pin_count,ph.first_invalid_pin_time,");
        strBuff.append(" ph.last_transaction_status,ph.last_transaction_on,ph.pin_modified_on,");
        strBuff.append(" ph.created_by,ph.created_on,ph.modified_by,ph.modified_on, ph.last_transfer_id, ph.pin_reset,");
        strBuff.append(" last_transfer_type FROM user_phones ph, users u WHERE ph.msisdn = ? ");
        strBuff.append(" AND ph.USER_ID= u.USER_ID AND u.STATUS<>'N'AND u.status <> 'C'");

        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        UserPhoneVO phoneVO = null;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_msisdn);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                phoneVO = new UserPhoneVO();
                phoneVO.setUserPhonesId(rs.getString("user_phones_id"));
                phoneVO.setMsisdn(rs.getString("msisdn"));
                phoneVO.setUserId(rs.getString("user_id"));
                phoneVO.setDescription(rs.getString("description"));
                phoneVO.setPrimaryNumber(rs.getString("primary_number"));
                phoneVO.setSmsPin(rs.getString("sms_pin"));
                phoneVO.setFirstInvalidPinTime(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("first_invalid_pin_time")));
                if (!BTSLUtil.isNullString(phoneVO.getSmsPin())) {
                    // set the default value *****
                    phoneVO.setShowSmsPin(BTSLUtil.getDefaultPasswordNumeric(phoneVO.getSmsPin()));
                    phoneVO.setConfirmSmsPin(BTSLUtil.getDefaultPasswordNumeric(phoneVO.getSmsPin()));
                }
                phoneVO.setPinRequired(rs.getString("pin_required"));
                phoneVO.setPhoneProfile(rs.getString(PHONE_PROFILE));
                phoneVO.setPhoneLanguage(rs.getString("phone_language"));
                phoneVO.setCountry(rs.getString("country"));
                phoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                phoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
                phoneVO.setLastTransactionOn(rs.getTimestamp("last_transaction_on"));
                phoneVO.setPinModifiedOn(rs.getTimestamp("pin_modified_on"));
                phoneVO.setCreatedBy(rs.getString("created_by"));
                phoneVO.setCreatedOn(rs.getTimestamp("created_on"));
                phoneVO.setModifiedBy(rs.getString("modified_by"));
                phoneVO.setModifiedOn(rs.getTimestamp("modified_on"));
                phoneVO.setLastTransferID(rs.getString("last_transfer_id"));
                phoneVO.setLastTransferType(rs.getString("last_transfer_type"));
                phoneVO.setPinReset(rs.getString("pin_reset"));
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserPhoneVO", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userPhoneList size=" + phoneVO);
            }
        }

        return phoneVO;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @param p_phoneId
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public String loadParentMsisdn(Connection p_con, String p_userId, String p_phoneId) throws BTSLBaseException {
        final String methodName = "loadParentMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_phoneId=");
        	loggerValue.append(p_phoneId);
        	loggerValue.append("p_userId=");
        	loggerValue.append(BTSLUtil.maskParam(p_userId));
        	LOG.debug(methodName, loggerValue);
        }
       
        StringBuilder strBuff = new StringBuilder();
        String parentMsisdn = null;

        try {
            strBuff.append(" SELECT UP.MSISDN FROM  USERS U, USER_PHONES UP WHERE U.user_id=?   ");
            strBuff.append(" AND U.parent_id=UP.user_id AND UP.primary_number='Y' ");
            String qry = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qry);
    			LOG.debug(methodName, loggerValue);
    		}
           try( PreparedStatement pstmt = p_con.prepareStatement(qry);)
           {
            pstmt.setString(1, p_userId);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                parentMsisdn = rs.getString("MSISDN");
            }
        } 
           }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadParentMsisdn]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadParentMsisdn]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: parentMsisdn " + parentMsisdn);
            }
        }
        return parentMsisdn;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method updates the users created through
     *         external API based when default user
     *         configuration cache is modified.
     *         Method : updateUsersFromCache
     * @throws BTSLBaseException
     * @return
     */

    public int updateUsersFromCache(HashMap<String, Object> p_modifiedMap, String p_networkID) throws BTSLBaseException {
        final String methodName = "updateUsersFromCache";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_modifiedMap=");
        	loggerValue.append(p_modifiedMap);
        	loggerValue.append(",p_networkID=");
        	loggerValue.append(p_networkID);
        	LOG.debug(methodName, loggerValue);
        }

        Iterator<String> iterator = null;
        String sqlSelectUser = null;
        String sqlUpdateGrade = null;
        String sqlUpdateProfile = null;
        String sqlUpdateComm = null;
        String sqlInsertRoles = null;
        String sqlDeleteRoles = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelectUser = null;
        PreparedStatement pstmtUpdateGrade = null;
        PreparedStatement pstmtUpdateProfile = null;
        PreparedStatement pstmtUpdateComm = null;
        PreparedStatement pstmtInsertRoles = null;
        PreparedStatement pstmtDeleteRoles = null;
        Connection con = null;
        int updateCount = 0;
        int deleteCount = 0;
        int insertCount = 0;

        iterator = p_modifiedMap.keySet().iterator();

        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT U.USER_ID FROM USERS U,USER_PHONES UP ");
        strBuff.append("WHERE U.USER_ID=UP.USER_ID AND U.CREATION_TYPE=? ");
        strBuff.append("AND U.CATEGORY_CODE=? AND UP.LAST_TRANSFER_ID IS NULL ");
        sqlSelectUser = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlSelectUser:");
			loggerValue.append(sqlSelectUser);
			LOG.debug(methodName, loggerValue);
		}
        strBuff.delete(0, strBuff.length());

        strBuff.append("DELETE FROM USER_ROLES WHERE USER_ID IN ");
        strBuff.append("(SELECT U.USER_ID FROM USERS U,USER_PHONES UP ");
        strBuff.append("WHERE U.USER_ID=UP.USER_ID AND U.CREATION_TYPE=? ");
        strBuff.append("AND U.CATEGORY_CODE=? AND UP.LAST_TRANSFER_ID IS NULL) ");
        sqlDeleteRoles = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlDeleteRoles:");
			loggerValue.append(sqlDeleteRoles);
			LOG.debug(methodName, loggerValue);
		}
        strBuff.delete(0, strBuff.length());

        strBuff.append("INSERT INTO USER_ROLES (USER_ID,ROLE_CODE,GATEWAY_TYPES) VALUES (?,?,'WEB') ");
        sqlInsertRoles = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlInsertRoles:");
			loggerValue.append(sqlInsertRoles);
			LOG.debug(methodName, loggerValue);
		}
        strBuff.delete(0, strBuff.length());
        strBuff.append("UPDATE CHANNEL_USERS  ");
        strBuff.append("SET USER_GRADE=? WHERE USER_ID IN ");
        strBuff.append("(SELECT U.USER_ID FROM USERS U,USER_PHONES UP ");
        strBuff.append("WHERE U.USER_ID=UP.USER_ID AND U.CREATION_TYPE=? ");
        strBuff.append("AND U.CATEGORY_CODE=? AND UP.LAST_TRANSFER_ID IS NULL) ");
        sqlUpdateGrade = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlUpdateGrade:");
			loggerValue.append(sqlUpdateGrade);
			LOG.debug(methodName, loggerValue);
		}
        strBuff.delete(0, strBuff.length());

        strBuff.append("UPDATE CHANNEL_USERS  ");
        strBuff.append("SET TRANSFER_PROFILE_ID=? WHERE USER_ID IN ");
        strBuff.append("(SELECT U.USER_ID FROM USERS U,USER_PHONES UP ");
        strBuff.append("WHERE U.USER_ID=UP.USER_ID AND U.CREATION_TYPE=? ");
        strBuff.append("AND U.CATEGORY_CODE=? AND UP.LAST_TRANSFER_ID IS NULL) ");
        sqlUpdateProfile = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlUpdateProfile:");
			loggerValue.append(sqlUpdateProfile);
			LOG.debug(methodName, loggerValue);
		}
        strBuff.delete(0, strBuff.length());

        strBuff.append("UPDATE CHANNEL_USERS  ");
        strBuff.append("SET COMM_PROFILE_SET_ID=? WHERE USER_ID IN ");
        strBuff.append("(SELECT U.USER_ID FROM USERS U,USER_PHONES UP ");
        strBuff.append("WHERE U.USER_ID=UP.USER_ID AND U.CREATION_TYPE=? ");
        strBuff.append("AND U.CATEGORY_CODE=? AND UP.LAST_TRANSFER_ID IS NULL) ");
        sqlUpdateComm = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlUpdateComm:");
			loggerValue.append(sqlUpdateComm);
			LOG.debug(methodName, loggerValue);
		}
        try {
            con = OracleUtil.getSingleConnection();
            pstmtSelectUser = con.prepareStatement(sqlSelectUser);
            pstmtDeleteRoles = con.prepareStatement(sqlDeleteRoles);
            pstmtInsertRoles = con.prepareStatement(sqlInsertRoles);
            pstmtUpdateProfile = con.prepareStatement(sqlUpdateProfile);
            pstmtUpdateComm = con.prepareStatement(sqlUpdateComm);
            pstmtUpdateGrade = con.prepareStatement(sqlUpdateGrade);
            int i = 1;
            String[] keySeparator;
            String key;
            String categoryCode;

            while (iterator.hasNext()) {
                keySeparator = iterator.next().split("_");
                categoryCode = keySeparator[0];
                key = keySeparator[0] + "_" + keySeparator[1];

                if (((Boolean) PreferenceCache.getControlPreference(PreferenceI.USR_DEF_CONFIG_UPDATE_REQ, p_networkID, categoryCode)).booleanValue()) {
                    if ("COMPRF".equals(keySeparator[1])) {
                        i = 1;
                        pstmtUpdateComm.setString(i, ((String) p_modifiedMap.get(key)));
                        pstmtUpdateComm.setString(++i, PretupsI.EXTERNAL_USR_CREATION_TYPE);
                        pstmtUpdateComm.setString(++i, categoryCode);
                        updateCount += pstmtUpdateComm.executeUpdate();
                    } else if ("TRFPRF".equals(keySeparator[1])) {
                        i = 1;
                        pstmtUpdateProfile.setString(i, ((String) p_modifiedMap.get(key)));
                        pstmtUpdateProfile.setString(++i, PretupsI.EXTERNAL_USR_CREATION_TYPE);
                        pstmtUpdateProfile.setString(++i, categoryCode);
                        updateCount += pstmtUpdateProfile.executeUpdate();
                    } else if ("GRDCODE".equals(keySeparator[1])) {
                        i = 1;
                        pstmtUpdateGrade.setString(i, ((String) p_modifiedMap.get(key)));
                        pstmtUpdateGrade.setString(++i, PretupsI.EXTERNAL_USR_CREATION_TYPE);
                        pstmtUpdateGrade.setString(++i, categoryCode);
                        updateCount += pstmtUpdateGrade.executeUpdate();
                    } else if ("ROLECODE".equals(keySeparator[1])) {
                        i = 1;
                        pstmtDeleteRoles.setString(i, PretupsI.EXTERNAL_USR_CREATION_TYPE);
                        pstmtDeleteRoles.setString(++i, categoryCode);
                        deleteCount += pstmtDeleteRoles.executeUpdate();

                        i = 1;
                        pstmtSelectUser.setString(i, PretupsI.EXTERNAL_USR_CREATION_TYPE);
                        pstmtSelectUser.setString(++i, categoryCode);
                        rs = pstmtSelectUser.executeQuery();
                        while (rs.next()) {
                            i = 1;
                            pstmtInsertRoles.setString(i, rs.getString("USER_ID"));
                            pstmtInsertRoles.setString(++i, ((String) p_modifiedMap.get(key)));
                            insertCount += pstmtInsertRoles.executeUpdate();
                        }
                    }
                }
            }
            con.commit();
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            try {
                con.rollback();
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDefaultConfigCache]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            try {
                con.rollback();
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDefaultConfigCache]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtDeleteRoles!= null){
        			pstmtDeleteRoles.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtInsertRoles!= null){
        			pstmtInsertRoles.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtSelectUser!= null){
        			pstmtSelectUser.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtUpdateComm!= null){
        			pstmtUpdateComm.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtUpdateGrade!= null){
        			pstmtUpdateGrade.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtUpdateProfile!= null){
        			pstmtUpdateProfile.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	OracleUtil.closeQuietly(con);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
        return (updateCount + deleteCount + insertCount);
    }

    /**
     * @author ankur.dhawan
     *         Description : This method loads the default User configuration
     *         for users created through
     *         external API in the cache
     *         Method : loadUserDefaultConfigCache
     * @throws BTSLBaseException
     * @return HashMap
     */
    public HashMap<String, Object> loadUserDefaultConfigCache() throws BTSLBaseException {
        final String methodName = "loadUserDefaultConfigCache";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered");
        	LOG.debug(methodName, loggerValue);
        }

        HashMap<String, Object> userMap = new HashMap<String, Object>();
        Connection con = null;
        String sqlSelectGrade = null;
        String sqlSelectComm = null;
        String sqlSelectRoles = null;
        String sqlSelectProfile = null;
        ResultSet rs = null;
        String key = null;

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT CG.CATEGORY_CODE, CG.GRADE_CODE AS DEF_GRADE_C0DE ");
        strBuff.append("FROM CHANNEL_GRADES CG ");
        strBuff.append("WHERE IS_DEFAULT_GRADE=? AND CG.STATUS=? ");
        sqlSelectGrade = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlSelectGrade:");
			loggerValue.append(sqlSelectGrade);
			LOG.debug(methodName, loggerValue);
		}
       
        strBuff.delete(0, strBuff.length());

        strBuff.append("SELECT CPS.CATEGORY_CODE, CPS.COMM_PROFILE_SET_ID AS DEF_COMM_PROFILE_ID ");
        strBuff.append("FROM COMMISSION_PROFILE_SET CPS ");
        strBuff.append("WHERE IS_DEFAULT=? AND CPS.STATUS=? ");
        sqlSelectComm = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlSelectComm:");
			loggerValue.append(sqlSelectComm);
			LOG.debug(methodName, loggerValue);
		}
       
        strBuff.delete(0, strBuff.length());

        
    	String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
		boolean tcpOn = false;
		Set<String> uniqueTransProfileId = new HashSet();

		if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
			tcpOn = true;
		}
		String sqlSelect = null;
		java.util.List<HashMap<String, String>> resultSet = null;
		
		if (tcpOn) {

			SearchCriteria searchCriteria = new SearchCriteria("IS_DEFAULT", Operator.EQUALS, PretupsI.YES,
					ValueType.STRING,null).
					addCriteria(new SearchCriteria("STATUS", Operator.EQUALS, PretupsI.YES,
					ValueType.STRING,null), BooleanOperator.AND);
					
			
			
			resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE", new HashSet<String>(Arrays.asList("profile_id","CATEGORY_CODE")), searchCriteria);
        	
			
		} else {

			strBuff.append("SELECT TP.CATEGORY_CODE, TP.PROFILE_ID AS DEF_PROFILE_ID ");
			strBuff.append("FROM TRANSFER_PROFILE TP ");
			strBuff.append("WHERE IS_DEFAULT=? AND TP.STATUS=? ");
		}    
	
		
        
        sqlSelectProfile = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlSelectProfile:");
			loggerValue.append(sqlSelectProfile);
			LOG.debug(methodName, loggerValue);
		}
        
        strBuff.delete(0, strBuff.length());
        strBuff.append("SELECT CR.CATEGORY_CODE ,R.ROLE_CODE AS DEF_GRP_ROLE ");
        strBuff.append("FROM CATEGORY_ROLES CR, ROLES R ");
        strBuff.append("WHERE CR.ROLE_CODE=R.ROLE_CODE ");
        strBuff.append("AND R.STATUS=? AND R.GROUP_ROLE=? AND R.IS_DEFAULT=? ");
        sqlSelectRoles = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append( "Query sqlSelectRoles:");
			loggerValue.append(sqlSelectRoles);
			LOG.debug(methodName, loggerValue);
		}
       
        try {
            con = OracleUtil.getSingleConnection();
            try(PreparedStatement pstmtSelectGrade = con.prepareStatement(sqlSelectGrade);
            	PreparedStatement pstmtSelectComm = con.prepareStatement(sqlSelectComm);
            	PreparedStatement pstmtSelectProfile = con.prepareStatement(sqlSelectProfile);
            	PreparedStatement pstmtSelectRoles = con.prepareStatement(sqlSelectRoles);){
            int i = 1;
            pstmtSelectGrade.setString(i, PretupsI.YES);
            pstmtSelectGrade.setString(++i, PretupsI.YES);
            try{
            rs = pstmtSelectGrade.executeQuery();
            while (rs.next()) {
                key = rs.getString("CATEGORY_CODE") + PretupsI.USR_CACHE_GRDCODE_SUFFIX;
                userMap.put(key, rs.getString("DEF_GRADE_C0DE"));
            }
            }
            finally{
            	if(rs!=null)
            		rs.close();
            }
            rs=null;
            i = 1;
            pstmtSelectComm.setString(i, PretupsI.YES);
            pstmtSelectComm.setString(++i, PretupsI.YES);
            try{
            rs = pstmtSelectComm.executeQuery();
            while (rs.next()) {
                key = rs.getString("CATEGORY_CODE") + PretupsI.USR_CACHE_COMPRF_SUFFIX;
                userMap.put(key, rs.getString("DEF_COMM_PROFILE_ID"));
            }
            }
            finally{
            	if(rs!=null)
            		rs.close();
            }
            rs=null;
            
            if(!tcpOn) {
            i = 1;
            pstmtSelectProfile.setString(i, PretupsI.YES);
            pstmtSelectProfile.setString(++i, PretupsI.YES);
            try
            {
            rs = pstmtSelectProfile.executeQuery();
            while (rs.next()) {
                key = rs.getString("CATEGORY_CODE") + PretupsI.USR_CACHE_TRFPRF_SUFFIX;
                userMap.put(key, rs.getString("DEF_PROFILE_ID"));
            }
            }
            finally{
            	if(rs!=null)
            		rs.close();
            }

				} else {

					key = resultSet.get(0).get("categoryCode") + PretupsI.USR_CACHE_TRFPRF_SUFFIX;
					userMap.put(key, rs.getString("DEF_PROFILE_ID"));
				}

            rs=null;
            i = 1;
            pstmtSelectRoles.setString(i, PretupsI.YES);
            pstmtSelectRoles.setString(++i, PretupsI.YES);
            pstmtSelectRoles.setString(++i, PretupsI.YES);
            rs = pstmtSelectRoles.executeQuery();
            while (rs.next()) {
                key = rs.getString("CATEGORY_CODE") + PretupsI.USR_CACHE_ROLECODE_SUFFIX;
                userMap.put(key, rs.getString("DEF_GRP_ROLE"));
            }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDefaultConfigCache]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDefaultConfigCache]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	
        	OracleUtil.closeQuietly(con);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userMap.size()=" + userMap.size());
            }

        }
        return userMap;
    }

    /**
     * Method to load the user details from employee code
     * 
     * @author diwakar
     * @param p_con
     * @param p_empCode
     * @param p_catCode
     * @param locale
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public ChannelUserVO loadUserDetailsByEmpcode(Connection p_con, String p_empCode) throws Exception {
        final String methodName = "loadUserDetailsByEmpcode";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_empCode=");
        	loggerValue.append(BTSLUtil.maskParam(p_empCode));
        	LOG.debug(methodName, loggerValue);
        }
         
        
        String sqlBuffer=userQry.loadUserDetailsByEmpcodeQuery();
        ChannelUserVO channelUserVO = null;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlBuffer);) {
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlBuffer);
    			LOG.debug(methodName, loggerValue);
    		}
            
            int i = 0;
            pstmt.setString(++i, p_empCode);
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(++i, PretupsI.STATUS_DELETE);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            String userID;
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                userID = rs.getString("user_id");
                channelUserVO.setUserID(userID);
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setNetworkName(rs.getString("network_name"));
                channelUserVO.setReportHeaderName(rs.getString("report_header_name"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setParentName(rs.getString("parent_name"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setValidRequestURLs(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                if (rs.getTimestamp("last_login_on") != null) {
                    channelUserVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                }
                channelUserVO.setEmpCode(p_empCode);
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                channelUserVO.setCreatedBy(userID);
                channelUserVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(userID);
                if (rs.getTimestamp("created_on") != null) {
                    channelUserVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                }
                // channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setUserType(rs.getString("user_type"));
                // channelUserVO.setInSuspend(rs.getString("in_suspend"));
                // channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setNetworkStatus(rs.getString("networkstatus"));
                /*
                 * LocaleMasterVO
                 * localeVO=LocaleMasterCache.getLocaleDetailsFromlocale
                 * (locale);
                 * if(PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
                 * {
                 * channelUserVO.setMessage(rs.getString("language_1_message"));
                 * }
                 * else
                 * {
                 * channelUserVO.setMessage(rs.getString("language_2_message"));
                 * }
                 */
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));
                channelUserVO.setDomainName(rs.getString("domain_name"));
                channelUserVO.setDomainStatus(rs.getString("domainstatus"));
                // channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                // channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                // channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
                if (rs.getTimestamp("password_count_updated_on") != null) {
                    channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));
                }
                // channelUserVO.setSmsPin(rs.getString("sms_pin"));
                // channelUserVO.setPinRequired(rs.getString("pin_required"));
                // channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setRestrictedMsisdnAllow(rs.getString("restricted_msisdn_allow"));
                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setCategoryStatus(rs.getString("categorystatus"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setMaxLoginCount(rs.getLong("max_login_count"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));

                categoryVO.setCategoryStatus(rs.getString("catstatus"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));

                channelUserVO.setCategoryVO(categoryVO);
                try {
                    channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    channelUserVO.setPasswordModifiedOn(null);
                }
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(p_con, channelUserVO.getUserID()));
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByEmpcode]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByEmpcode]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUloadUserDetailsByEmpcode", "error.general.processing",ex);
        } finally {
        	LOG.info(methodName, "inside finally");
        }
        LOG.debug("loadUserDetailsByEmpcode ", " Exiting channelUserVO=" + channelUserVO);
        return channelUserVO;
    }

    /**
     * Date : May 2, 2007
     * Discription :
     * Method : loadUserDetailsForUserID
     * 
     * @author diwakar
     * @param p_con
     * @param p_userID
     * @throws BTSLBaseException
     * @return ChannelUserVO
     * @author ved.sharma
     */
    public ChannelUserVO loadUserDetailsByLoginId(Connection p_con, String p_loginId) throws BTSLBaseException {
        final String methodName = "loadUserDetailsByLoginId";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginId=");
        	loggerValue.append(BTSLUtil.maskParam(p_loginId));
        	LOG.debug(methodName, loggerValue);
        }
        
        ChannelUserVO channelUserVO = null;
       
        try {
            
            String selectQuery = userQry.loadUserDetailsByLoginIdQry();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_loginId);
            pstmtSelect.setString(2, PretupsI.USER_STATUS_TYPE);
           try( ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setCreationType(rs.getString("creation_type"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                channelUserVO.setSmsPin(rs.getString("sms_pin"));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("userstatus"));
                //added by vamshi user previous_status
                channelUserVO.setPreviousStatus(rs.getString("previous_status"));
                channelUserVO.setCreatedBy(rs.getString("created_by"));
                channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(rs.getString("modified_by"));
                channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                // Added for RSA Authentication
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setAppointmentDate(rs.getDate("appointment_date"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));

                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));

                // for Zebra and Tango by sanjeew date 06/07/07
                channelUserVO.setApplicationID(rs.getString("application_id"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                // End Zebra and Tango
                channelUserVO.setPrefixId(rs.getLong("PREFIX_ID"));
                channelUserVO.setStatusDesc(rs.getString("lookup_name"));
                channelUserVO.setPhoneProfile(rs.getString(PHONE_PROFILE));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("catseq"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                channelUserVO.setCategoryVO(categoryVO);
                channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                channelUserVO.setLevel1ApprovedOn(rs.getDate("level1_approved_on"));
                channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                channelUserVO.setLevel1ApprovedOn(rs.getDate("level2_approved_on"));
            }
            return channelUserVO;
        }
        }// end of try
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByLoginId]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByLoginId]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	
        
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting channelUserVO:" + channelUserVO);
            }
        }// end of finally
    }

    /**
     * Date : May 2, 2007
     * Discription :
     * Method : loadUserDetailsForUserID
     * 
     * @author diwakar
     * @param p_con
     * @param p_userID
     * @throws BTSLBaseException
     * @return ChannelUserVO
     * @author ved.sharma
     */
    public ChannelUserVO loadUserDetailsByMsisdn(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "loadUserDetailsByMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	LOG.debug(methodName, loggerValue);
        }
        ChannelUserVO channelUserVO = null;
       
        try {           
            String selectQuery = userQry.loadUserDetailsByMsisdnQry();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_msisdn);
            pstmtSelect.setString(2, PretupsI.USER_STATUS_TYPE);
            try( ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                channelUserVO.setActiveUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                channelUserVO.setCreationType(SqlParameterEncoder.encodeParams(rs.getString("creation_type")));
                channelUserVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                channelUserVO.setNetworkID(SqlParameterEncoder.encodeParams(rs.getString("network_code")));
                channelUserVO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
                channelUserVO.setPassword(SqlParameterEncoder.encodeParams(rs.getString("password")));
                channelUserVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                channelUserVO.setParentID(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
                channelUserVO.setOwnerID(SqlParameterEncoder.encodeParams(rs.getString("owner_id")));
                channelUserVO.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("msisdn")));
                channelUserVO.setPinRequired(SqlParameterEncoder.encodeParams(rs.getString("pin_required")));
                channelUserVO.setSmsPin(SqlParameterEncoder.encodeParams(rs.getString("sms_pin")));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setEmpCode(SqlParameterEncoder.encodeParams(rs.getString("employee_code")));
                channelUserVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("userstatus")));
                channelUserVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                channelUserVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                channelUserVO.setContactPerson(SqlParameterEncoder.encodeParams(rs.getString("contact_person")));
                channelUserVO.setContactNo(SqlParameterEncoder.encodeParams(rs.getString("contact_no")));
                channelUserVO.setDesignation(SqlParameterEncoder.encodeParams(rs.getString("designation")));
                channelUserVO.setDivisionCode(SqlParameterEncoder.encodeParams(rs.getString("division")));
                channelUserVO.setDepartmentCode(SqlParameterEncoder.encodeParams(rs.getString("department")));
                channelUserVO.setUserType(SqlParameterEncoder.encodeParams(rs.getString("user_type")));
                channelUserVO.setInSuspend(SqlParameterEncoder.encodeParams(rs.getString("in_suspend")));
                channelUserVO.setOutSuspened(SqlParameterEncoder.encodeParams(rs.getString("out_suspend")));
                channelUserVO.setAddress1(SqlParameterEncoder.encodeParams(rs.getString("address1")));
                channelUserVO.setAddress2(SqlParameterEncoder.encodeParams(rs.getString("address2")));
                channelUserVO.setCity(SqlParameterEncoder.encodeParams(rs.getString("city")));
                channelUserVO.setState(SqlParameterEncoder.encodeParams(rs.getString("state")));
                channelUserVO.setCountry(SqlParameterEncoder.encodeParams(rs.getString("country")));
                // Added for RSA Authentication
                channelUserVO.setRsaFlag(SqlParameterEncoder.encodeParams(rs.getString("rsaflag")));
                channelUserVO.setSsn(SqlParameterEncoder.encodeParams(rs.getString("ssn")));
                channelUserVO.setUserNamePrefix(SqlParameterEncoder.encodeParams(rs.getString("user_name_prefix")));
                channelUserVO.setExternalCode(SqlParameterEncoder.encodeParams(rs.getString("external_code")));
                channelUserVO.setUserCode(SqlParameterEncoder.encodeParams(rs.getString("user_code")));
                channelUserVO.setShortName(SqlParameterEncoder.encodeParams(rs.getString("short_name")));
                channelUserVO.setReferenceID(SqlParameterEncoder.encodeParams(rs.getString("reference_id")));
                channelUserVO.setDomainID(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
                channelUserVO.setEmail(SqlParameterEncoder.encodeParams(rs.getString("email")));
                // Added by deepika aggarwal
                channelUserVO.setCompany(SqlParameterEncoder.encodeParams(rs.getString("company")));
                channelUserVO.setFax(SqlParameterEncoder.encodeParams(rs.getString("fax")));
                channelUserVO.setFirstName(SqlParameterEncoder.encodeParams(rs.getString("firstname")));
                channelUserVO.setLastName(SqlParameterEncoder.encodeParams(rs.getString("lastname")));
                // end added by deepika aggarwal
                channelUserVO.setAllowedIps(SqlParameterEncoder.encodeParams(rs.getString("allowed_ip")));
                channelUserVO.setAllowedDays(SqlParameterEncoder.encodeParams(rs.getString("allowed_days")));
                channelUserVO.setAppointmentDate(rs.getDate("appointment_date"));
                channelUserVO.setFromTime(SqlParameterEncoder.encodeParams(rs.getString("from_time")));
                channelUserVO.setToTime(SqlParameterEncoder.encodeParams(rs.getString("to_time")));

                channelUserVO.setTransferProfileID(SqlParameterEncoder.encodeParams(rs.getString("transfer_profile_id")));
                channelUserVO.setCommissionProfileSetID(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_id")));
                channelUserVO.setUserGrade(SqlParameterEncoder.encodeParams(rs.getString("user_grade")));

                // for Zebra and Tango by sanjeew date 06/07/07
                channelUserVO.setApplicationID(SqlParameterEncoder.encodeParams(rs.getString("application_id")));
                channelUserVO.setMpayProfileID(SqlParameterEncoder.encodeParams(rs.getString("mpay_profile_id")));
                channelUserVO.setUserProfileID(SqlParameterEncoder.encodeParams(rs.getString("user_profile_id")));
                channelUserVO.setMcommerceServiceAllow(SqlParameterEncoder.encodeParams(rs.getString("mcommerce_service_allow")));
                // End Zebra and Tango
                channelUserVO.setPrefixId(rs.getLong("PREFIX_ID"));
                channelUserVO.setStatusDesc(SqlParameterEncoder.encodeParams(rs.getString("lookup_name")));
                channelUserVO.setPhoneProfile(SqlParameterEncoder.encodeParams(rs.getString(PHONE_PROFILE)));
                channelUserVO.setPinRequired(SqlParameterEncoder.encodeParams(rs.getString("pin_required")));
                channelUserVO.setUserPhonesId(SqlParameterEncoder.encodeParams(rs.getString("user_phones_id")));
                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                categoryVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                categoryVO.setDomainCodeforCategory(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
                categoryVO.setSequenceNumber(rs.getInt("catseq"));
                categoryVO.setGrphDomainType(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type")));
                categoryVO.setSmsInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("sms_interface_allowed")));
                categoryVO.setWebInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("web_interface_allowed")));
                categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
                categoryVO.setHierarchyAllowed(SqlParameterEncoder.encodeParams(rs.getString("hierarchy_allowed")));
                categoryVO.setAgentAllowed(SqlParameterEncoder.encodeParams(rs.getString("agent_allowed")));
                categoryVO.setCategoryType(SqlParameterEncoder.encodeParams(rs.getString("category_type")));
                categoryVO.setRestrictedMsisdns(SqlParameterEncoder.encodeParams(rs.getString("restricted_msisdns")));
                categoryVO.setTransferToListOnly(SqlParameterEncoder.encodeParams(rs.getString("transfertolistonly")));
                channelUserVO.setCategoryVO(categoryVO);
                channelUserVO.setGeographicalCode(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_code")));
                
                            }
            return channelUserVO;
        }
            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByMsisdn]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByMsisdn]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting channelUserVO:" + channelUserVO);
            }
        }// end of finally
    }
    /*loading category through MSISDN to delete user where user status*/
    public String userCategoryFromMSISDNforDelete(Connection p_con, String p_userMSISDN){
        final String methodName = "userCategoryFromMSISDN";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userMSISDN=");
        	loggerValue.append(p_userMSISDN);
        	LOG.debug(methodName, loggerValue);
        }

        String parentCat = "";
        try {
        	String status = PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_DELETE_REQUEST + "','" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','"  +  PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST + "','" + PretupsI.USER_STATUS_BARRED + "','" + PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE + "'";
        
        	StringBuilder sbf = new StringBuilder("SELECT U.category_code FROM USERS U, CATEGORIES CAT");
            sbf.append(" WHERE U.category_code=CAT.category_code");
            sbf.append(" AND U.msisdn='" + p_userMSISDN + "'");
            sbf.append(" AND U.user_type='" + PretupsI.USER_TYPE_CHANNEL + "'");
            sbf.append(" AND u.status NOT IN (" + status + ") ");
            sbf.append(" AND CAT.status='" + PretupsI.YES + "'");

            String sqlSelect = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
	            try(ResultSet res = pstmt.executeQuery();){
		            if (res.next()) {
		                parentCat = res.getString("category_code");
		            }
		            OracleUtil.closeQuietly(res);
	            }
	            OracleUtil.closeQuietly(pstmt);
            }
        } catch (SQLException e) {
            LOG.errorTrace(methodName, e);
        } finally {
        	if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exit: parentCat=");
            	loggerValue.append(parentCat);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return parentCat;
    }

    // Added by Vinay Singh to get only the user category on the basis of user
    // MSISDN as earler it was not there.
    public String userCategoryFromMSISDN(Connection p_con, String p_userMSISDN) {
        final String methodName = "userCategoryFromMSISDN";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userMSISDN=");
        	loggerValue.append(p_userMSISDN);
        	LOG.debug(methodName, loggerValue);
        }

        String parentCat = "";
        try {
            StringBuilder sbf = new StringBuilder("SELECT U.category_code FROM USERS U, CATEGORIES CAT");
            sbf.append(" WHERE U.category_code=CAT.category_code");
            sbf.append(" AND U.msisdn='" + p_userMSISDN + "'");
            sbf.append(" AND U.user_type='" + PretupsI.USER_TYPE_CHANNEL + "'");
            sbf.append(" AND u.status IN ('" + PretupsI.YES + "','" + PretupsI.SUSPEND + "')");
            sbf.append(" AND CAT.status='" + PretupsI.YES + "'");

            String sqlSelect = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
            	try (ResultSet res = pstmt.executeQuery();){
	                if (res.next()) {
	                    parentCat = res.getString("category_code");
	                }
	                OracleUtil.closeQuietly(res);
            	}
            	OracleUtil.closeQuietly(pstmt);
            }
            
        } catch (SQLException e) {
            LOG.errorTrace(methodName, e);
        } finally {
        	if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exit: parentCat=");
            	loggerValue.append(parentCat);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return parentCat;
    }

    /**
     * @author diwakar
     * @param p_con
     * @param p_userMSISDN
     * @param parentExtCode
     * @return
     */
    public String channelAdminUserCategoryFromMSISDN(Connection p_con, String p_userMSISDN, String parentExtCode) {
        final String methodName = "channelAdminUserCategoryFromMSISDN";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userMSISDN=");
        	loggerValue.append(p_userMSISDN);
        	LOG.debug(methodName, loggerValue);
        }

        String parentCat = "";
        try {
            StringBuilder sbf = new StringBuilder("SELECT U.category_code FROM USERS U, CATEGORIES CAT");
            sbf.append(" WHERE U.category_code=CAT.category_code");
            // sbf.append(" AND U.category_code='"+PretupsI.OPERATOR_CATEGORY+"'");
            // Changed on 21-02-2104
            if (!BTSLUtil.isNullString(p_userMSISDN)) {
                sbf.append(" AND U.msisdn='" + p_userMSISDN + "'");
            }
            if (!BTSLUtil.isNullString(parentExtCode)) {
                sbf.append(" AND U.EXTERNAL_CODE='" + parentExtCode + "'");
            }
            // Ended Here
            // sbf.append(" AND U.user_type='"+PretupsI.CATEGORY_USER_TYPE+"'");
            sbf.append(" AND U.user_type='" + PretupsI.USER_TYPE_CHANNEL + "'");
            sbf.append(" AND u.status IN ('" + PretupsI.YES + "','" + PretupsI.SUSPEND + "','" + PretupsI.USER_STATUS_PREACTIVE + "')");
            sbf.append(" AND CAT.status='" + PretupsI.YES + "'");

            String sqlSelect = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
            	try (ResultSet res = pstmt.executeQuery();){
		            if (res.next()) {
		                parentCat = res.getString("category_code");
		            }
		            OracleUtil.closeQuietly(res);
            	}
            	OracleUtil.closeQuietly(pstmt);
            }
        } catch (SQLException e) {
            LOG.errorTrace(methodName, e);
        } finally {
        	if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exit: parentCat=");
            	loggerValue.append(parentCat);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return parentCat;
    }

    public Map<String, UserStatusVO> loadUserStatusDetails() throws BTSLBaseException {
    	final String methodName = "loadUserStatusDetails";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered");
        	LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        UserStatusVO userStatusVO = null;
        Connection con = null;
        Map<String, UserStatusVO> map = new HashMap<String, UserStatusVO>();
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("Select network_code, user_type, category_code, gateway_allowed, user_sender_allowed , user_sender_denied, user_sender_suspended, user_receiver_allowed, user_receiver_denied, ");
        strBuff.append("user_receiver_suspended,web_login_allowed,web_login_denied from user_allowed_status ");
        String sqlSelect = strBuff.toString();
        
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try {
            con = OracleUtil.getSingleConnection();
            pstmtSelect = con.prepareStatement(sqlSelect);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                userStatusVO = new UserStatusVO();
                userStatusVO.setNetworkCode(rs.getString("network_code"));
                userStatusVO.setCategoryCode(rs.getString("category_code"));
                userStatusVO.setGatewayType(rs.getString("gateway_allowed"));
                userStatusVO.setUserType(rs.getString("user_type"));
                userStatusVO.setUserSenderAllowed(rs.getString("user_sender_allowed"));
                userStatusVO.setUserSenderDenied(rs.getString("user_sender_denied"));
                userStatusVO.setUserSenderSuspended(rs.getString("user_sender_suspended"));
                userStatusVO.setUserReceiverAllowed(rs.getString("user_receiver_allowed"));
                userStatusVO.setUserReceiverDenied(rs.getString("user_receiver_denied"));
                userStatusVO.setUserReceiverSuspended(rs.getString("user_receiver_suspended"));
                userStatusVO.setWebLoginAllowed(rs.getString("web_login_allowed"));
                userStatusVO.setWebLoginDenied(rs.getString("web_login_denied"));

                map.put(userStatusVO.getNetworkCode() + "_" + userStatusVO.getCategoryCode() + "_" + userStatusVO.getUserType() + "_" + userStatusVO.getGatewayType(), userStatusVO);

            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserStatusDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserStatusDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	OracleUtil.closeQuietly(con);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userVO=" + userStatusVO);
            }
        }
        return map;
    }
    
    
    public ChannelUserVO loadAllUserDetailsByLoginID(java.sql.Connection p_con, String p_loginID ) throws SQLException, BTSLBaseException {
        final String METHOD_NAME = "loadUserDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginId=");
        	loggerValue.append(BTSLUtil.maskParam(p_loginID));
        	LOG.debug(METHOD_NAME, loggerValue);
        }
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);   
        
        //StringBuilder sqlBuffer = new StringBuilder(" SELECT uowner.user_name owner_name, uparent.user_name parent_name, u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
        
        String sqlBuffer=userQry.loadAllUserDetailsByLoginIDQry();
            StringBuilder strBuffer = new StringBuilder("select user_id,user_name from users where user_id in (?,?)");

        ChannelUserVO channelUserVO = null;
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlBuffer);){
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlBuffer);
    			LOG.debug(METHOD_NAME, loggerValue);
    		}
            
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(strBuffer.toString());
    			LOG.debug(METHOD_NAME, loggerValue);
    		}
            try(PreparedStatement pstmt1 = p_con.prepareStatement(strBuffer.toString());){
            int i = 0;
            pstmt.setString(++i, p_loginID.toUpperCase());
            // pstmt.setString(++i,BTSLUtil.encryptText(p_password));
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(++i, PretupsI.STATUS_DELETE);

           try(  ResultSet rs = pstmt.executeQuery();)
           {
            String userID;
            String uid;
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                userID = rs.getString("user_id");
                channelUserVO.setUserID(userID);
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setNetworkName(rs.getString("network_name"));
                channelUserVO.setReportHeaderName(rs.getString("report_header_name"));
                channelUserVO.setLoginID(p_loginID);
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                //channelUserVO.setParentName(rs.getString("parent_name"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                //channelUserVO.setOwnerName(rs.getString("owner_name"));
                        pstmt1.setString(1, channelUserVO.getParentID());
                        pstmt1.setString(2, channelUserVO.getOwnerID());
                        try(ResultSet rs1 = pstmt1.executeQuery();)
                        {
                        while(rs1.next()) {
                              uid = rs1.getString("user_id");
                              //String pid = rs1.getString("user_id");
                              if(PretupsI.ROOT_PARENT_ID.equals(channelUserVO.getParentID()))
                                    channelUserVO.setParentName(PretupsI.ROOT_PARENT_ID);
                              else if(PretupsI.SYSTEM.equals(channelUserVO.getParentID()))
                                    channelUserVO.setParentName(PretupsI.SYSTEM);
                              else if(channelUserVO.getParentID().equals(uid))
                                    channelUserVO.setParentName(rs1.getString("user_name"));
                              if(channelUserVO.getOwnerID().equals(uid))
                                    channelUserVO.setOwnerName(rs1.getString("user_name"));
                        }
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setValidRequestURLs(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                if (rs.getTimestamp("last_login_on") != null)
                    channelUserVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setEmail(rs.getString("email"));

                // Added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal

                channelUserVO.setCreatedBy(userID);
                channelUserVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(userID);
                // if(rs.getTimestamp("created_on") !=null)
                channelUserVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("modified_on")));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                // Authentication Allowed
                channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setNetworkStatus(rs.getString("networkstatus"));
                if (isTrfRuleUserLevelAllow)
                    channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                if (lmsAppl)
                    channelUserVO.setLmsProfile(rs.getString("lms_profile"));
                if (optInOutAllow) {
                    channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
                }
                channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));
                channelUserVO.setDomainName(rs.getString("domain_name"));
                channelUserVO.setDomainStatus(rs.getString("domainstatus"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));

                // for Zebra and Tango by sanjeew date 06/07/07
                channelUserVO.setApplicationID(rs.getString("application_id"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
                channelUserVO.setAccessType(rs.getString("access_type"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setPhoneProfile(rs.getString("PHONE_PROFILE"));
                // End Zebra and Tango
                channelUserVO.setPasswordReset(rs.getString("PSWD_RESET"));
                if (rs.getTimestamp("password_count_updated_on") != null)
                    channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));
                channelUserVO.setSmsPin(rs.getString("sms_pin"));
                channelUserVO.setPinReset(rs.getString("pin_reset"));// rahul.d
                                                                     // for
                                                                     // korek
                channelUserVO.setPrefixId(rs.getLong("PREFIX_ID"));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setRestrictedMsisdnAllow(rs.getString("restricted_msisdn_allow"));
                channelUserVO.setActiveUserID(userID);
				channelUserVO.setAppointmentDate(rs.getDate("APPOINTMENT_DATE"));
				
                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setCategoryStatus(rs.getString("categorystatus"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setMaxLoginCount(rs.getLong("max_login_count"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));

                categoryVO.setCategoryStatus(rs.getString("catstatus"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("SMS_INTERFACE_ALLOWED"));
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                channelUserVO.setCategoryVO(categoryVO);
                try {
                    channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    LOG.errorTrace(METHOD_NAME, e);
                    channelUserVO.setPasswordModifiedOn(null);
                }
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(p_con, channelUserVO.getUserID()));
                GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
                // load the geographies info from the user_geographies
                ArrayList geographyList = _geographyDAO.loadUserGeographyList(p_con, channelUserVO.getUserID(), channelUserVO.getNetworkID());
                channelUserVO.setGeographicalAreaList(geographyList);
                // load the domain of the user that are associated with it
                DomainDAO domainDAO = new DomainDAO();
                channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(p_con, channelUserVO.getUserID()));
            }
        }
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.processing",ex);
        } finally {
        	LOG.info(METHOD_NAME, "inside finally");
        }
        LOG.debug("loadUserDetails ::", " Exiting channelUserVO=" + channelUserVO);
        return channelUserVO;
    }

    public ChannelUserVO loadAllUserDetailsByExternalCode(java.sql.Connection p_con, String external_code ) throws SQLException, BTSLBaseException {
        final String METHOD_NAME = "loadUserDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: external_code=");
        	loggerValue.append(external_code);
        	LOG.debug(METHOD_NAME, loggerValue);
        }
        PreparedStatement pstmt = null;
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);        
        ResultSet rs1 = null;
        //StringBuilder sqlBuffer = new StringBuilder(" SELECT uowner.user_name owner_name, uparent.user_name parent_name, u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
       String sqlBuffer=userQry.loadAllUserDetailsByExternalCodeQry();

            StringBuilder strBuffer = new StringBuilder("select user_id,user_name from users where user_id in (?,?)");

        ChannelUserVO channelUserVO = null;
        try {
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlBuffer);
    			LOG.debug(METHOD_NAME, loggerValue);
    		}
			pstmt = p_con.prepareStatement(sqlBuffer);
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(strBuffer.toString());
    			LOG.debug(METHOD_NAME, loggerValue);
    		}
            try(PreparedStatement pstmt1 = p_con.prepareStatement(strBuffer.toString());)
            {
            int i = 0;
            pstmt.setString(++i, external_code.toUpperCase());
            // pstmt.setString(++i,BTSLUtil.encryptText(p_password));
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(++i, PretupsI.STATUS_DELETE);

            try( ResultSet rs = pstmt.executeQuery();)
            {
            String userID;
            String uid;
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                userID = rs.getString("user_id");
                channelUserVO.setUserID(userID);
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setNetworkName(rs.getString("network_name"));
                channelUserVO.setReportHeaderName(rs.getString("report_header_name"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                //channelUserVO.setParentName(rs.getString("parent_name"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                //channelUserVO.setOwnerName(rs.getString("owner_name"));
                        pstmt1.setString(1, channelUserVO.getParentID());
                        pstmt1.setString(2, channelUserVO.getOwnerID());
                        rs1 = pstmt1.executeQuery();
                        while(rs1.next()) {
                              uid = rs1.getString("user_id");
                              //String pid = rs1.getString("user_id");
                              if(PretupsI.ROOT_PARENT_ID.equals(channelUserVO.getParentID()))
                                    channelUserVO.setParentName(PretupsI.ROOT_PARENT_ID);
                              else if(PretupsI.SYSTEM.equals(channelUserVO.getParentID()))
                                    channelUserVO.setParentName(PretupsI.SYSTEM);
                              else if(channelUserVO.getParentID().equals(uid))
                                    channelUserVO.setParentName(rs1.getString("user_name"));
                              if(channelUserVO.getOwnerID().equals(uid))
                                    channelUserVO.setOwnerName(rs1.getString("user_name"));
                        }
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setValidRequestURLs(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                if (rs.getTimestamp("last_login_on") != null)
                    channelUserVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setEmail(rs.getString("email"));

                // Added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal

                channelUserVO.setCreatedBy(userID);
                channelUserVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(userID);
                // if(rs.getTimestamp("created_on") !=null)
                channelUserVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("modified_on")));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                // Authentication Allowed
                channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setNetworkStatus(rs.getString("networkstatus"));
                if (isTrfRuleUserLevelAllow)
                    channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                if (lmsAppl)
                    channelUserVO.setLmsProfile(rs.getString("lms_profile"));
                if (optInOutAllow) {
                    channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
                }
                channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));
                channelUserVO.setDomainName(rs.getString("domain_name"));
                channelUserVO.setDomainStatus(rs.getString("domainstatus"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));

                // for Zebra and Tango by sanjeew date 06/07/07
                channelUserVO.setApplicationID(rs.getString("application_id"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
                channelUserVO.setAccessType(rs.getString("access_type"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setPhoneProfile(rs.getString("PHONE_PROFILE"));
                // End Zebra and Tango
                channelUserVO.setPasswordReset(rs.getString("PSWD_RESET"));
                if (rs.getTimestamp("password_count_updated_on") != null)
                    channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));
                channelUserVO.setSmsPin(rs.getString("sms_pin"));
                channelUserVO.setPinReset(rs.getString("pin_reset"));// rahul.d
                                                                     // for
                                                                     // korek
                channelUserVO.setPrefixId(rs.getLong("PREFIX_ID"));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setRestrictedMsisdnAllow(rs.getString("restricted_msisdn_allow"));
                channelUserVO.setActiveUserID(userID);
                channelUserVO.setUserPhonesId(rs.getString("user_phones_id"));
                CategoryVO categoryVO = CategoryVO.getInstance();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setCategoryStatus(rs.getString("categorystatus"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setMaxLoginCount(rs.getLong("max_login_count"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));

                categoryVO.setCategoryStatus(rs.getString("catstatus"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("SMS_INTERFACE_ALLOWED"));
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                channelUserVO.setCategoryVO(categoryVO);
                try {
                    channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    LOG.errorTrace(METHOD_NAME, e);
                    channelUserVO.setPasswordModifiedOn(null);
                }
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(p_con, channelUserVO.getUserID()));
            }
        } 
            }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.processing",ex);
        } finally {
			try{
            	if (rs1!= null){
            		rs1.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
        	
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if(LOG.isDebugEnabled()){
        		LOG.debug("loadUserDetails ::", " Exiting channelUserVO=" + channelUserVO);
        	}
        }
        return channelUserVO;
    }
    // added for channes user transfer
    public boolean userStatusUpdate(Connection p_con,String p_userId,String p_status) throws BTSLBaseException
	{
		final String methodName = "userStatusUpdate";
		 StringBuilder loggerValue= new StringBuilder();
		 if (LOG.isDebugEnabled()) {
			 loggerValue.setLength(0);
			 loggerValue.append("Entered: p_userId=");
			 loggerValue.append(BTSLUtil.maskParam(p_userId));
			 LOG.debug(methodName, loggerValue);
		 }

		StringBuilder strBuff = new StringBuilder();
		String userMsisdn=null;
		int updateCount=0;
		boolean isStatusChange=false;
		Date currentDate = new Date();
		
		try
		{
			if(p_status.equals(PretupsI.STATUS_ACTIVE)){
				strBuff.append(" UPDATE users SET status ='"+PretupsI.STATUS_SUSPEND+"' , modified_by=?, modified_on=?  WHERE user_id = ? ");
			}else if(p_status.equals(PretupsI.STATUS_SUSPEND)){
				strBuff.append(" UPDATE users SET status ='"+PretupsI.STATUS_ACTIVE+"' , modified_by=?, modified_on=? WHERE user_id = ? ");
			}
			String updateQuery = strBuff.toString();		   
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}
		    try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
		    {
		    psmtUpdate.setString(1,PretupsI.SYSTEM);
		    psmtUpdate.setTimestamp(2,BTSLUtil.getTimestampFromUtilDate(currentDate));
		    psmtUpdate.setString(3,p_userId);
		   updateCount = psmtUpdate.executeUpdate();
		    if(updateCount>0){
		    	isStatusChange=true;
		    	p_con.commit();
		    }else{
		    	p_con.rollback();
		    	 }
		} 
		}catch (SQLException sqe)
		{
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserDAO[userStatusUpdate]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		} catch (Exception ex)
		{
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName,ex);
			try{
        		if(p_con!=null) 
        			p_con.rollback();
        		}
        	catch(Exception e1){
        		 LOG.error(methodName,"Exceptin:e="+e1);
        	}
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserDAO[userStatusUpdate]","","","","Exception:"+ex.getMessage());
		   throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		} finally
		{
        				
			if (LOG.isDebugEnabled())
			{
				LOG.debug(methodName, "Exiting: userMsisdn " +userMsisdn);
			}
		}
		return isStatusChange;
	}
    // added for channel user transfer
    /**
     * @author Naveen
     * @param p_con
     * @param p_userMSISDN
     * @param parentExtCode 
     * @return
     */
    public String fetchMSISDNbyOriginId(Connection p_con,String p_OriginID,String p_ExtCode)throws BTSLBaseException
    {
    	final String methodName = "fetchMSISDNbyOriginId";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_OriginID=");
        	loggerValue.append(p_OriginID);
        	loggerValue.append(",p_ExtCode=");
        	loggerValue.append(p_ExtCode);
        	LOG.debug(methodName, loggerValue);
        }

    	String parentMSISDN="";
    	
    	 
    	try 
    	{
    		StringBuilder sbf=new StringBuilder("SELECT u.msisdn from Users u,USER_PHONES up");
    		sbf.append(" where u.origin_id=? And u.external_code=? ");
    		sbf.append("And u.status <>'"+PretupsI.NO+"'");
    		sbf.append(" And u.user_id = up.user_id and up.primary_number='"+PretupsI.YES+"'");
    		String sqlSelect = sbf.toString();
    	    		
    		if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
    		
    		try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
    		{
    		pstmtSelect.setString(1, p_OriginID);
    		pstmtSelect.setString(2, p_ExtCode);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if(rs.next())
				parentMSISDN = rs.getString("msisdn");
		} 
    	}
    	}
    	catch (SQLException e) 
    	{
    		LOG.errorTrace(methodName,e);
    		throw new BTSLBaseException(this,methodName, "error.general.sql.processing",e);
		}
    	catch (Exception ex)
		{
    		loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserDAO[fetchMSISDNbyOriginId]","","","","Exception:"+ex.getMessage());
		   throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		}
		finally{
			LOG.debug(methodName, "inside finally");
		}
    	return parentMSISDN;
    }
    
    /**
     * Method to load the user name against a userID
     * 
     * @param con
     * @param userID
     * @return
     * @throws SQLExceptionR
     * @throws Exception
     */
    public String loadUserName(java.sql.Connection con, String userID) throws BTSLBaseException {
    	final String methodName = "loadUserName";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadUserName():: Entered with userID:");
        	msg.append(userID);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("select user_id,user_name from users where user_id=?");
        String userName = null;
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	pstmt.setString(1, userID);
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		if (rs.next()) {
            			userName = rs.getString("user_name");
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
			try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + userName);
            }
		}
            
            return userName;
    }



    
    public ArrayList<UserHierarchyVO> fetchUserHierarchy(Connection p_con, String msisdn, String loginId) throws BTSLBaseException {
        final String methodName = "fetchMSISDNbyOriginId";
        String searchCriteria= null;
        
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: loginId=");
        	loggerValue.append(BTSLUtil.maskParam(loginId));
        	
        	loggerValue.append("Entered: msisdn=");
        	loggerValue.append(msisdn);
        	
        	
        	LOG.debug(methodName, loggerValue);
        }
        
        ArrayList<UserHierarchyVO>  userHierarchyVOList = new ArrayList<UserHierarchyVO>();
        try {
            
            String selectQuery = null;
            
            if(msisdn != null && msisdn.trim().length() > 0) {
            	
            	searchCriteria = "MSISDN";
            	selectQuery = userQry.fetchUserHierarchy(searchCriteria);
            }else {
            	searchCriteria = "LOGINID";
            	selectQuery = userQry.fetchUserHierarchy(searchCriteria);
            }
            
            
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            
            if(msisdn != null && msisdn.trim().length() > 0) {
            pstmtSelect.setString(1, msisdn);
           }else {
        	  pstmtSelect.setString(1, loginId);
           }
            
           try( ResultSet rs = pstmtSelect.executeQuery();)
           {

            	while(rs.next()) {
            	UserHierarchyVO  userHierarchyVO = new UserHierarchyVO();
            	
            	userHierarchyVO.setMsisdn(rs.getString("MSISDN"));
            	userHierarchyVO.setFirstName(rs.getString("FIRSTNAME"));
            	userHierarchyVO.setLastName(rs.getString("LASTNAME"));
            	userHierarchyVO.setCategoryCode(rs.getString("CATEGORY_CODE"));
            	userHierarchyVO.setLoginId(rs.getString("LOGIN_ID"));
            	userHierarchyVO.setCategoryName(rs.getString("CATEGORY_NAME"));
            	
            	
            	userHierarchyVOList.add(userHierarchyVO);
            }
            return userHierarchyVOList;
        }
        }// end of try
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO["+methodName+"]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO["+methodName+"]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	
        
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting channelUserVO:" + userHierarchyVOList);
            }
        }// end of finally
    }
    
    public ArrayList<UserHierarchyVO> recentC2cTxn(Connection p_con, String msisdn, String loginId) throws BTSLBaseException{

        final String methodName = "recentC2cTxn";
        int recentC2CTxn = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RECENT_C2C_TXN))).intValue();        
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn=");
        	loggerValue.append(msisdn);
        	LOG.debug(methodName, loggerValue);
        }
        
        ArrayList<UserHierarchyVO>  recentC2cTxnList = new ArrayList<UserHierarchyVO>();
        try {
        	 String selectQuery = null;
        	 String data = null;
        	 if(!BTSLUtil.isNullString(msisdn))
        	 {
        		 data = "MSISDN";
        	 }
        	 else{
        		 data = "LOGINID";
        	 }
        	 selectQuery = userQry.fetchRecentC2cTxn(data);
        	  if(LOG.isDebugEnabled()){
      			loggerValue.setLength(0);
      			loggerValue.append(QUERY_KEY);
      			loggerValue.append(selectQuery);
      			LOG.debug(methodName, loggerValue);
      		}
              try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
              {
            	  pstmtSelect.setString(1, PretupsI.C2C_VOUCHER_STATUS_CLOSE);
            	  pstmtSelect.setString(2, PretupsI.TRANSFER_TYPE_C2C);
            	  if(!BTSLUtil.isNullString(msisdn)){
            		  pstmtSelect.setString(3, msisdn);
            	  }
            	  else{
            		  pstmtSelect.setString(3, loginId);
            	  }
            	  try( ResultSet rs = pstmtSelect.executeQuery();)
                  {
            		int i = 0;
            		Map<String,UserHierarchyVO> resultSet = new LinkedHashMap<String,UserHierarchyVO>();
            		while(rs.next()){
            			if(!resultSet.containsKey(rs.getString("MSISDN"))){
            				UserHierarchyVO  userHierarchyVO = new UserHierarchyVO();
            				userHierarchyVO.setMsisdn(rs.getString("MSISDN"));
            				userHierarchyVO.setFirstName(rs.getString("FIRSTNAME"));
                           	userHierarchyVO.setLastName(rs.getString("LASTNAME"));
                           	userHierarchyVO.setLoginId(rs.getString("LOGIN_ID"));
                           	userHierarchyVO.setCategoryName(rs.getString("CATEGORY_NAME"));
                           	userHierarchyVO.setUserNamePrefix(rs.getString("LOOKUP_NAME"));
                           	userHierarchyVO.setUserNamePrefixCode(rs.getString("USER_NAME_PREFIX"));
                        	userHierarchyVO.setCategoryCode(rs.getString("CATEGORY_CODE"));
                        	resultSet.put(rs.getString("MSISDN"),userHierarchyVO);
                        	i++;
                           	if(i==recentC2CTxn){
                           		break;
                           	}
            			}
            		}
            		for(UserHierarchyVO usVo : resultSet.values()){
            			recentC2cTxnList.add(usVo);
            		}
              }
            	  return recentC2cTxnList;
              }
        	 
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO["+methodName+"]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO["+methodName+"]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	
        
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting channelUserVO:" + recentC2cTxnList);
            }
        }// end of finally
    
    }
    
    public Map<String,String> c2ctottrftxn(Connection pCon,String userId, Date fromDate, Date toDate) throws BTSLBaseException{

        final String methodName = "c2ctottrftxn";
       
        
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(userId);
        	loggerValue.append("Entered: fromDate=");
        	loggerValue.append(fromDate);
        	loggerValue.append("Entered: toDate=");
        	loggerValue.append(toDate);
        	LOG.debug(methodName, loggerValue);
        }
        
        ArrayList<C2CTotalTransferVO>  c2ctrftxnList = new ArrayList<C2CTotalTransferVO>();
        try {
        	 String selectQuery = null;
        	 selectQuery = userQry.fetchC2cTrfData();
        	  if(LOG.isDebugEnabled()){
      			loggerValue.setLength(0);
      			loggerValue.append(QUERY_KEY);
      			loggerValue.append(selectQuery);
      			LOG.debug(methodName, loggerValue);
      		}
        	  Map<String,String> resultSet = new LinkedHashMap<String,String>();
              try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);)
              {
            	  int i = 1;
            	  pstmtSelect.setString(i++, userId);
            	  pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            	  pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(toDate));
            	  try( ResultSet rs = pstmtSelect.executeQuery();)
                  {
            		while(rs.next()){
            			resultSet.put(rs.getString("SERVICE_TYPE"), rs.getString("TOTAL"));
            		}
                  }
              }
              return resultSet;
        }
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO["+methodName+"]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO["+methodName+"]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	
        
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting c2ctottrftxn:" + c2ctrftxnList);
            }
        }// end of finally
    
    }
    public long totalTranBetweenDate(Connection p_con, String userId, Date fromDate, Date toDate)
			throws BTSLBaseException {
		final String methodName = "totalTranBetweenDate";

		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: userId=");
			loggerValue.append(userId);

			
			loggerValue.append("Entered: fromDate=");
			loggerValue.append(fromDate);
			
			loggerValue.append("Entered: toDate=");
			loggerValue.append(toDate);

			LOG.debug(methodName, loggerValue);
		}
		
		C2STotalTrnsVO c2STotalTrnsVO = new C2STotalTrnsVO();

		try {

			String selectQuery = null;
			selectQuery = userQry.fetchTotalTrans();
			

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}
			try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
				int i=0;
				i++;
				pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
				i++;
				pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
				i++;
				pstmtSelect.setString(i, userId);
				long result = -1;
				
				

				try (ResultSet rs = pstmtSelect.executeQuery();) {

					while (rs.next()) {
						result = rs.getLong("TXNCOUNT");
					}
					return result;
				}
			} // end of try
		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[" + methodName + "]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[" + methodName + "]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.processing", e);
		} // end of catch
		finally {

			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting totalTrnsVO:" + c2STotalTrnsVO);
			}
		} // end of finally
	}
    
    /**
  	 * @param con
  	 * @param msisdn
  	 * @param fromDate
  	 * @param toDate
  	 * @param status
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public LinkedHashMap<String, Object> loadTransactionDetails(java.sql.Connection con, String msisdn, String msisdn2,Date fromDate,Date toDate,String status,RequestVO p_requestVO) throws BTSLBaseException {
    	final String methodName = "loadTransactionDetails";
    	TotalTransactionsDetailedViewResponseVO totalTransactionsDetailedViewResponseVO ;
        ArrayList<TotalTransactionsDetailedViewResponseVO> transactionList=new ArrayList<TotalTransactionsDetailedViewResponseVO>();
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadTransactionDetails():: Entered with : ");
        	msg.append(" msisdn:: " + msisdn);
        	msg.append(" msisdn2:: " + msisdn2);
        	msg.append(" fromDate:: " + fromDate);
        	msg.append(" toDate:: " + toDate);
        	msg.append(" status:: " + status);
        	msg.append(" p_requestVO:: " + p_requestVO.toString());
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        PreparedStatement pstmt = null;
        StringBuilder strBuffer = null;
        if(BTSLUtil.isNullString(msisdn2)) {
    		strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE TRANSFER_DATE BETWEEN ? AND ?  AND TRANSFER_STATUS = ? AND SENDER_MSISDN = ? ORDER BY TRANSFER_DATE_TIME DESC");

        }
        else {
    		strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE TRANSFER_DATE BETWEEN ?  AND ?  AND SENDER_MSISDN = ? AND RECEIVER_MSISDN = ? AND TRANSFER_STATUS = ? ORDER BY TRANSFER_DATE_TIME DESC");

        }
        LOG.debug(methodName, "Query = " + strBuffer.toString());
		String stat="";
		if(status.equalsIgnoreCase("PASS")) {stat="200";}
		if(status.equalsIgnoreCase("FAIL")){stat="206";}
		try {
			
			
        	pstmt = con.prepareStatement(strBuffer.toString());
        	if(BTSLUtil.isNullString(msisdn2)){
        		int i = 0;
	            ++i;
	        	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
	        	++i;
	        	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
	        	++i;
	        	pstmt.setString(i, stat);
	        	pstmt.setString(++i, msisdn);
        	}
			else {
				int i = 0;
	            ++i;
	        	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
	        	++i;
	        	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
	        	pstmt.setString(++i, msisdn);
	        	++i;
	        	pstmt.setString(i, msisdn2);
	        	++i;
	        	pstmt.setString(i, stat);
			}
        	
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			totalTransactionsDetailedViewResponseVO=new TotalTransactionsDetailedViewResponseVO();
            			totalTransactionsDetailedViewResponseVO.setTransactionId(rs.getString("TRANSFER_ID"));
            			totalTransactionsDetailedViewResponseVO.setRecieverMsisdn(rs.getString("RECEIVER_MSISDN"));	
            			totalTransactionsDetailedViewResponseVO.setStatus(stat);
            			totalTransactionsDetailedViewResponseVO.setRechargeDateTime(rs.getString("TRANSFER_DATE_TIME"));
            			totalTransactionsDetailedViewResponseVO.setServiceType(rs.getString("SERVICE_TYPE"));
            			totalTransactionsDetailedViewResponseVO.setRechargeAmount(rs.getString("TRANSFER_VALUE"));
            			transactionList.add(totalTransactionsDetailedViewResponseVO);
            			
            			
            			
            		}
            		if(BTSLUtil.isNullOrEmptyList(transactionList)) {
            			p_requestVO.setSuccessTxn(false);
            			p_requestVO.setSenderReturnMessage("NO Details Found For Input");
            		}else {
            			
            			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
            		}
            		map.put("data", transactionList);
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    
    /**
  	 * @param con
  	 * @param msisdn
  	 * @param fromDate
  	 * @param toDate
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public LinkedHashMap<String, Object> loadAllTransactionDetails(java.sql.Connection con, String msisdn, String msisdn2,Date fromDate,Date toDate,RequestVO p_requestVO) throws BTSLBaseException {
    	final String methodName = "loadAllTransactionDetails";
    	TotalTransactionsDetailedViewResponseVO totalTransactionsDetailedViewResponseVO ;
        ArrayList<TotalTransactionsDetailedViewResponseVO> transactionList=new ArrayList<TotalTransactionsDetailedViewResponseVO>();
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadTransactionDetails():: Entered with totalTransactionsDetailedViewResponseVO:");
        	msg.append(" msisdn:: " + msisdn);
        	msg.append(" msisdn2:: " + msisdn2);
        	msg.append(" fromDate:: " + fromDate);
        	msg.append(" toDate:: " + toDate);
        	msg.append(" p_requestVO:: " + p_requestVO.toString());
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        PreparedStatement pstmt = null;
        StringBuilder strBuffer=null;
        if(BTSLUtil.isNullString(msisdn2)) {
   		 
        	strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE TRANSFER_DATE BETWEEN ?  AND ? AND SENDER_MSISDN = ?  ORDER BY TRANSFER_DATE_TIME DESC");
        }
        else {
   		 strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE TRANSFER_DATE BETWEEN ?  AND ?  AND SENDER_MSISDN = ? AND RECEIVER_MSISDN = ? ORDER BY TRANSFER_DATE_TIME DESC");
        }
        LOG.debug(methodName, "Query = " + strBuffer.toString());
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	if(BTSLUtil.isNullString(msisdn2)) {
            	int i = 0;
                ++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            	++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
            	pstmt.setString(++i, msisdn);
        	}
            else {
            	int i = 0;
                ++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            	++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
            	pstmt.setString(++i, msisdn);
            	++i;
            	pstmt.setString(i, msisdn2);
            }

        	
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			totalTransactionsDetailedViewResponseVO=new TotalTransactionsDetailedViewResponseVO();
            			totalTransactionsDetailedViewResponseVO.setTransactionId(rs.getString("TRANSFER_ID"));
            			totalTransactionsDetailedViewResponseVO.setRecieverMsisdn(rs.getString("RECEIVER_MSISDN"));	
            			totalTransactionsDetailedViewResponseVO.setStatus(rs.getString("TRANSFER_STATUS"));
            			totalTransactionsDetailedViewResponseVO.setRechargeDateTime(rs.getString("TRANSFER_DATE_TIME"));
            			totalTransactionsDetailedViewResponseVO.setServiceType(rs.getString("SERVICE_TYPE"));
            			totalTransactionsDetailedViewResponseVO.setRechargeAmount(rs.getString("TRANSFER_VALUE"));
            			transactionList.add(totalTransactionsDetailedViewResponseVO);
            			
            			
            			
            		}
            		if(BTSLUtil.isNullOrEmptyList(transactionList)) {
            			p_requestVO.setSuccessTxn(false);
            			p_requestVO.setSenderReturnMessage("NO Details Found For Input");
            		}else {
            			
            			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
            		}
            		map.put("data", transactionList);
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
        		LOG.debug(methodName, "Exiting userName:" + map);
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    /**
  	 * @param con
  	 * @param msisdn
  	 * @param fromDate
  	 * @param toDate
  	 * @param fromRow
  	 * @param toRow
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public LinkedHashMap<String, Object> loadAllTransDetPagn(java.sql.Connection con, String msisdn, String msisdn2,Date fromDate,Date toDate,RequestVO p_requestVO,int fromRow,int toRow) throws BTSLBaseException {
    	final String methodName = "loadAllTransDetPagn";
    	TotalTransactionsDetailedViewResponseVO totalTransactionsDetailedViewResponseVO ;
        ArrayList<TotalTransactionsDetailedViewResponseVO> transactionList=new ArrayList<TotalTransactionsDetailedViewResponseVO>();
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadAllTransDetPagn():: Entered with :");
        	msg.append(" msisdn:: " + msisdn);
        	msg.append(" msisdn2:: " + msisdn2);
        	msg.append(" fromDate:: " + fromDate);
        	msg.append(" toDate:: " + toDate);
        	msg.append(" p_requestVO:: " + p_requestVO.toString());
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        PreparedStatement pstmt = null;
        StringBuilder strBuffer=null;
        if(BTSLUtil.isNullString(msisdn2)) {
        	strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE TRANSFER_DATE BETWEEN ?  AND ? AND SENDER_MSISDN = ? ORDER BY TRANSFER_DATE_TIME DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        else {		 
        	strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE TRANSFER_DATE BETWEEN ?  AND ?  AND SENDER_MSISDN = ? AND RECEIVER_MSISDN = ? ORDER BY TRANSFER_DATE_TIME DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        LOG.debug(methodName, "Query = " + strBuffer.toString());
		//offset is from where to start paging
		//fetch is how many no. of rows u want from offset
			int offSet=fromRow-1;
			int fetch=toRow-fromRow+1;
			String offSetStr= Integer.toString(offSet);
			String fetchStr=Integer.toString(fetch);
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	if(BTSLUtil.isNullString(msisdn2)) {
            	int i = 0;
                ++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            	++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
            	pstmt.setString(++i, msisdn);
            	++i;
            	pstmt.setString(i, offSetStr);
            	++i;
            	pstmt.setString(i, fetchStr);
        	}
        	else {
            	int i = 0;
                ++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            	++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
            	++i;
            	pstmt.setString(i, msisdn);
            	pstmt.setString(++i, msisdn2);
            	++i;
            	pstmt.setString(i, offSetStr);
            	++i;
            	pstmt.setString(i, fetchStr);
        	}

        	
        	
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			totalTransactionsDetailedViewResponseVO=new TotalTransactionsDetailedViewResponseVO();
            			totalTransactionsDetailedViewResponseVO.setTransactionId(rs.getString("TRANSFER_ID"));
            			totalTransactionsDetailedViewResponseVO.setRecieverMsisdn(rs.getString("RECEIVER_MSISDN"));	
            			totalTransactionsDetailedViewResponseVO.setStatus(rs.getString("TRANSFER_STATUS"));
            			totalTransactionsDetailedViewResponseVO.setRechargeDateTime(rs.getString("TRANSFER_DATE_TIME"));
            			totalTransactionsDetailedViewResponseVO.setServiceType(rs.getString("SERVICE_TYPE"));
            			totalTransactionsDetailedViewResponseVO.setRechargeAmount(rs.getString("TRANSFER_VALUE"));
            			transactionList.add(totalTransactionsDetailedViewResponseVO);
            			
            			
            			
            		}
            		if(BTSLUtil.isNullOrEmptyList(transactionList)) {
            			p_requestVO.setSuccessTxn(false);
            			p_requestVO.setSenderReturnMessage("NO Details Found For Input");
            		}else {
            			
            			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
            		}
            		map.put("data", transactionList);
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting userName:" + map);
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    
    /**
  	 * @param con
  	 * @param msisdn
  	 * @param fromDate
  	 * @param toDate
  	 * @param fromRow
  	 * @param to
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public LinkedHashMap<String, Object> loadTranDetPagn(java.sql.Connection con, String msisdn, String msisdn2,Date fromDate,Date toDate,String status,RequestVO p_requestVO,int fromRow,int toRow) throws BTSLBaseException {
    	final String methodName = "loadTranDetPagn";
    	TotalTransactionsDetailedViewResponseVO totalTransactionsDetailedViewResponseVO ;
        ArrayList<TotalTransactionsDetailedViewResponseVO> transactionList=new ArrayList<TotalTransactionsDetailedViewResponseVO>();
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadTransactionDetails():: Entered with totalTransactionsDetailedViewResponseVO:");
        	msg.append(" msisdn:: " + msisdn);
        	msg.append(" msisdn2:: " + msisdn2);
        	msg.append(" fromDate:: " + fromDate);
        	msg.append(" toDate:: " + toDate);
        	msg.append(" status:: " + status);
        	msg.append(" p_requestVO:: " + p_requestVO.toString());
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        PreparedStatement pstmt = null;
        StringBuilder strBuffer = null;
        if(BTSLUtil.isNullString(msisdn2)) {
    		strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE TRANSFER_DATE BETWEEN ?  AND ? AND SENDER_MSISDN = ? AND TRANSFER_STATUS = ? ORDER BY TRANSFER_DATE_TIME DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        else {
    		strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE TRANSFER_DATE BETWEEN ?  AND ?  AND SENDER_MSISDN = ? AND RECEIVER_MSISDN = ? AND TRANSFER_STATUS = ? ORDER BY TRANSFER_DATE_TIME DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        LOG.debug(methodName, "Query = " + strBuffer.toString());
		String stat="";
		if(status.equalsIgnoreCase("PASS")) {stat="200";}
		if(status.equalsIgnoreCase("FAIL")){stat="206";}
		//offset is from where to start paging
		//fetch is how many no. of rows u want from offset
			int offSet=fromRow-1;
			int fetch=toRow-fromRow+1;
			String offSetStr= Integer.toString(offSet);
			String fetchStr=Integer.toString(fetch);
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	if(BTSLUtil.isNullString(msisdn2)) {
        	  	int i = 0;
                ++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            	++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
            	pstmt.setString(++i, msisdn);
            	++i;
            	pstmt.setString(i, stat);
            	++i;
            	pstmt.setString(i, offSetStr);
            	++i;
            	pstmt.setString(i, fetchStr);
        	}
        	else{
        	  	int i = 0;
                ++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            	++i;
            	pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
            	pstmt.setString(++i, msisdn);
            	++i;
            	pstmt.setString(i, msisdn2);
            	++i;
            	pstmt.setString(i, stat);
            	++i;
            	pstmt.setString(i, offSetStr);
            	++i;
            	pstmt.setString(i, fetchStr);
        	}
      
        	
        	
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			totalTransactionsDetailedViewResponseVO=new TotalTransactionsDetailedViewResponseVO();
            			totalTransactionsDetailedViewResponseVO.setTransactionId(rs.getString("TRANSFER_ID"));
            			totalTransactionsDetailedViewResponseVO.setRecieverMsisdn(rs.getString("RECEIVER_MSISDN"));	
            			totalTransactionsDetailedViewResponseVO.setStatus(rs.getString("TRANSFER_STATUS"));
            			totalTransactionsDetailedViewResponseVO.setRechargeDateTime(rs.getString("TRANSFER_DATE_TIME"));
            			totalTransactionsDetailedViewResponseVO.setServiceType(rs.getString("SERVICE_TYPE"));
            			totalTransactionsDetailedViewResponseVO.setRechargeAmount(rs.getString("TRANSFER_VALUE"));
            			transactionList.add(totalTransactionsDetailedViewResponseVO);
            			
            			
            			
            		}
            		if(BTSLUtil.isNullOrEmptyList(transactionList)) {
            			p_requestVO.setSuccessTxn(false);
            			p_requestVO.setSenderReturnMessage("NO Details Found For Input");
            		}else {
            			
            			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
            		}
            		map.put("data", transactionList);
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    /**
  	 * @param con
  	 * @param msisdn
  	 * @param fromDate
  	 * @param toDate
  	 * @param fromRow
  	 * @param to
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public LinkedHashMap<String, Object> loadTranDetPagntranxid(java.sql.Connection con, String msisdn, String msisdn2,Date fromDate,Date toDate,String status,RequestVO p_requestVO,String transactionId,int toRow) throws BTSLBaseException {
    	final String methodName = "loadTranDetPagntranxid";
    	TotalTransactionsDetailedViewResponseVO totalTransactionsDetailedViewResponseVO ;
        ArrayList<TotalTransactionsDetailedViewResponseVO> transactionList=new ArrayList<TotalTransactionsDetailedViewResponseVO>();
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadTransactionDetails():: Entered with totalTransactionsDetailedViewResponseVO:");
        	msg.append(" msisdn:: " + msisdn);
        	msg.append(" msisdn2:: " + msisdn2);
        	msg.append(" fromDate:: " + fromDate);
        	msg.append(" toDate:: " + toDate);
        	msg.append(" status:: " + status);
        	msg.append(" p_requestVO:: " + p_requestVO.toString());
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
      //  PreparedStatement pstmt1 = null;

        PreparedStatement pstmt = null;
        StringBuilder strBuffer = null;
       // StringBuilder strBuffer1 = new StringBuilder("SELECT TRANSFER_DATE_TIME FROM C2S_TRANSFERS WHERE TRANSFER_ID= ?");
        if(BTSLUtil.isNullString(msisdn2)) {
   		 strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE  TRANSFER_STATUS = ? AND SENDER_MSISDN = ? AND TRANSFER_DATE_TIME< = (SELECT TRANSFER_DATE_TIME FROM C2S_TRANSFERS WHERE TRANSFER_ID= ?)ORDER BY TRANSFER_DATE_TIME DESC OFFSET 1 ROWS FETCH NEXT ? ROWS ONLY");
        }
        else {
   		 strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE  SENDER_MSISDN = ? AND RECEIVER_MSISDN = ? AND TRANSFER_STATUS= ? AND TRANSFER_DATE_TIME< = (SELECT TRANSFER_DATE_TIME FROM C2S_TRANSFERS WHERE TRANSFER_ID= ?)ORDER BY TRANSFER_DATE_TIME DESC OFFSET 1 ROWS FETCH NEXT ? ROWS ONLY");
        }
        LOG.debug(methodName, "Query = " + strBuffer.toString());
		String stat="";
		if(status.equalsIgnoreCase("PASS")) {stat="200";}
		if(status.equalsIgnoreCase("FAIL")){stat="206";}
		
		//fetch is how many no. of rows u want from the transactionid provided
			
			int fetch=toRow;
			String fetchStr=Integer.toString(fetch);
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	if(BTSLUtil.isNullString(msisdn2)) {
        		int i = 0;
            	 ++i;
            	pstmt.setString(i, stat);
            	pstmt.setString(++i, msisdn);
            	++i;
            	pstmt.setString(i, transactionId);
            	++i;
            	pstmt.setString(i, fetchStr);
        	}
        	else {
        		int i = 0;
        		++i;
            	pstmt.setString(i, msisdn);
            	pstmt.setString(++i, msisdn2);
            	 ++i;
            	pstmt.setString(i, stat);
            	++i;
            	pstmt.setString(i, transactionId);
            	++i;
            	pstmt.setString(i, fetchStr);
        	}
        	
        	
        	
        	
            try(ResultSet rs = pstmt.executeQuery();	)
            	{
            		while (rs.next()) {
            			totalTransactionsDetailedViewResponseVO=new TotalTransactionsDetailedViewResponseVO();
            			totalTransactionsDetailedViewResponseVO.setTransactionId(rs.getString("TRANSFER_ID"));
            			totalTransactionsDetailedViewResponseVO.setRecieverMsisdn(rs.getString("RECEIVER_MSISDN"));	
            			totalTransactionsDetailedViewResponseVO.setStatus(rs.getString("TRANSFER_STATUS"));
            			totalTransactionsDetailedViewResponseVO.setRechargeDateTime(rs.getString("TRANSFER_DATE_TIME"));
            			totalTransactionsDetailedViewResponseVO.setServiceType(rs.getString("SERVICE_TYPE"));
            			totalTransactionsDetailedViewResponseVO.setRechargeAmount(rs.getString("TRANSFER_VALUE"));
            			transactionList.add(totalTransactionsDetailedViewResponseVO);
            			
            			
            			
            		}
            		if(BTSLUtil.isNullOrEmptyList(transactionList)) {
            			p_requestVO.setSuccessTxn(false);
            			p_requestVO.setSenderReturnMessage("NO Details Found For Input");
            		}else {
            			
            			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
            		}
            		map.put("data", transactionList);
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    public LinkedHashMap<String, Object> loadAllTranDetPagntranxid(java.sql.Connection con,String msisdn, String msisdn2,Date fromDate,Date toDate,String status,RequestVO p_requestVO,String transactionId,int toRow) throws BTSLBaseException {
    	final String methodName = "loadAllTranDetPagntranxid";
    	TotalTransactionsDetailedViewResponseVO totalTransactionsDetailedViewResponseVO ;
        ArrayList<TotalTransactionsDetailedViewResponseVO> transactionList=new ArrayList<TotalTransactionsDetailedViewResponseVO>();
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadAllTranDetPagntranxid():: Entered with ");
        	msg.append(" msisdn:: " + msisdn);
        	msg.append(" msisdn2:: " + msisdn2);
        	msg.append(" fromDate:: " + fromDate);
        	msg.append(" toDate:: " + toDate);
        	msg.append(" status:: " + status);
        	msg.append(" p_requestVO:: " + p_requestVO.toString());
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
      //  PreparedStatement pstmt1 = null;

        PreparedStatement pstmt = null;
        StringBuilder strBuffer =null;
        if(BTSLUtil.isNullString(msisdn2)) {
      		 strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE  SENDER_MSISDN = ? AND TRANSFER_DATE_TIME < = (SELECT TRANSFER_DATE_TIME FROM C2S_TRANSFERS WHERE TRANSFER_ID= ?)ORDER BY TRANSFER_DATE_TIME DESC OFFSET 1 ROWS FETCH NEXT ? ROWS ONLY");
        } else {
   		 strBuffer = new StringBuilder("SELECT TRANSFER_ID,RECEIVER_MSISDN,TRANSFER_DATE_TIME,TRANSFER_STATUS,SERVICE_TYPE,TRANSFER_VALUE FROM C2S_TRANSFERS  WHERE  SENDER_MSISDN = ? AND RECEIVER_MSISDN = ? AND TRANSFER_DATE_TIME < = (SELECT TRANSFER_DATE_TIME FROM C2S_TRANSFERS WHERE TRANSFER_ID= ?)ORDER BY TRANSFER_DATE_TIME DESC OFFSET 1 ROWS FETCH NEXT ? ROWS ONLY");
        }
        LOG.debug(methodName, "Query = " + strBuffer.toString());
			
			int fetch=toRow;
			String fetchStr=Integer.toString(fetch);
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	if(BTSLUtil.isNullString(msisdn2)) {
        		int i = 0;
        		
            	++i;
            	pstmt.setString(i, msisdn);
            	pstmt.setString(++i, transactionId);
            	++i;
            	pstmt.setString(i, fetchStr);
        		
        	}else {
        		
        		int i = 0;
        		++i;
            	pstmt.setString(i, msisdn);
            	pstmt.setString(++i, msisdn2);
            	++i;
            	pstmt.setString(i, transactionId);
            	++i;
            	pstmt.setString(i, fetchStr);
        		
        	}
        	
        	
        	
        	
            try(ResultSet rs = pstmt.executeQuery();	)
            	{
            		while (rs.next()) {
            			totalTransactionsDetailedViewResponseVO=new TotalTransactionsDetailedViewResponseVO();
            			totalTransactionsDetailedViewResponseVO.setTransactionId(rs.getString("TRANSFER_ID"));
            			totalTransactionsDetailedViewResponseVO.setRecieverMsisdn(rs.getString("RECEIVER_MSISDN"));	
            			totalTransactionsDetailedViewResponseVO.setStatus(rs.getString("TRANSFER_STATUS"));
            			totalTransactionsDetailedViewResponseVO.setRechargeDateTime(rs.getString("TRANSFER_DATE_TIME"));
            			totalTransactionsDetailedViewResponseVO.setServiceType(rs.getString("SERVICE_TYPE"));
            			totalTransactionsDetailedViewResponseVO.setRechargeAmount(rs.getString("TRANSFER_VALUE"));
            			transactionList.add(totalTransactionsDetailedViewResponseVO);
            			
            			
            			
            		}
            		if(BTSLUtil.isNullOrEmptyList(transactionList)) {
            			p_requestVO.setSuccessTxn(false);
            			p_requestVO.setSenderReturnMessage("NO Details Found For Input");
            		}else {
            			
            			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
            		}
            		map.put("data", transactionList);
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    public LinkedHashMap<Date, PassbookDetailsVO> loadStockSalesC2C(java.sql.Connection con, PassbookDetailsVO passbookDetailsVO) throws BTSLBaseException {
    	final String methodName = "loadStockSalesC2C";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadStockSalesC2C():: Entered with passbookDetailsVO:");
        	msg.append(passbookDetailsVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        final LinkedHashMap<Date, PassbookDetailsVO> map = new LinkedHashMap<Date, PassbookDetailsVO>();
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("SELECT CHANNEL_TRANSFERS.REQUESTED_QUANTITY,CHANNEL_TRANSFERS.TRANSFER_DATE FROM CHANNEL_TRANSFERS WHERE status = 'CLOSE' AND FROM_USER_ID= ? AND TRANSFER_SUB_TYPE='T' AND TRANSFER_DATE BETWEEN ? and ? ");
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, passbookDetailsVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getToDate()));
        	long c2cStockSales=0;
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			if(map.containsKey(rs.getDate("transfer_date")))
            			{
            				PassbookDetailsVO passbookDetailsVO1=map.get(rs.getDate("transfer_date"));
            				c2cStockSales = passbookDetailsVO1.getC2CStockSales()+ rs.getLong("REQUESTED_QUANTITY");
            				passbookDetailsVO1.setC2CStockSales(c2cStockSales);
            				map.put(rs.getDate("TRANSFER_DATE"), passbookDetailsVO1);
            			}
            			else{
            				PassbookDetailsVO passbookDetailsVO1 = new PassbookDetailsVO();
            				passbookDetailsVO1.setC2CStockSales(rs.getLong("REQUESTED_QUANTITY"));
            				map.put(rs.getDate("TRANSFER_DATE"), passbookDetailsVO1);
            			}
            			
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    public LinkedHashMap<Date, PassbookDetailsVO> loadStockSalesC2S(java.sql.Connection con, PassbookDetailsVO passbookDetailsVO,LinkedHashMap<Date, PassbookDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadStockSalesC2S";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadStockSalesC2S():: Entered with passbookDetailsVO:");
        	msg.append(passbookDetailsVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("SELECT transfer_value,transfer_date FROM C2S_TRANSFERS WHERE C2S_TRANSFERS.TRANSFER_STATUS = '200' "
				+ "AND SENDER_ID= ? AND TRANSFER_DATE BETWEEN ? and ? ");
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, passbookDetailsVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getToDate()));
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			if(map.containsKey(rs.getDate("transfer_date")))
            			{
            				PassbookDetailsVO passbookDetailsVO1=map.get(rs.getDate("transfer_date"));
            				passbookDetailsVO1.setC2SStockSales(rs.getLong("transfer_value"));
            				map.put(rs.getDate("transfer_date"),passbookDetailsVO1);
            			}
            			else
            			{
            				PassbookDetailsVO passbookDetailsVO1 = new PassbookDetailsVO();
            				passbookDetailsVO1.setC2SStockSales(rs.getLong("transfer_value"));
            			map.put(rs.getDate("transfer_date"),passbookDetailsVO1);
            			}
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + passbookDetailsVO);
            }
        }
        return map;
    }
    
    public  LinkedHashMap<Date, PassbookDetailsVO>  loadCommissionQtyC2C(java.sql.Connection con, PassbookDetailsVO passbookDetailsVO,LinkedHashMap<Date, PassbookDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadCommissionQtyC2C";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadCommissionQtyC2C():: Entered with passbookDetailsVO:");
        	msg.append(passbookDetailsVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("SELECT CHANNEL_TRANSFERS_ITEMS.commission_value,CHANNEL_TRANSFERS.transfer_date FROM CHANNEL_TRANSFERS_ITEMS,CHANNEL_TRANSFERS WHERE CHANNEL_TRANSFERS_ITEMS.TRANSFER_ID =CHANNEL_TRANSFERS.TRANSFER_ID AND CHANNEL_TRANSFERS.TRANSFER_SUB_TYPE='T' AND CHANNEL_TRANSFERS.TO_USER_ID = ? AND CHANNEL_TRANSFERS.status = 'CLOSE' "
				+ "AND CHANNEL_TRANSFERS.TRANSFER_DATE BETWEEN ? and ? ");
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, passbookDetailsVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getToDate()));
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			if(map.containsKey(rs.getDate("transfer_date")))
            			{
            				PassbookDetailsVO passbookDetailsVO1=map.get(rs.getDate("transfer_date"));
            				passbookDetailsVO1.setCommissionValue(rs.getLong("commission_value")+map.get(rs.getDate("transfer_date")).getCommissionValue());
            				map.put(rs.getDate("transfer_date"),passbookDetailsVO1);
            			}
            			else
            			{
            				PassbookDetailsVO passbookDetailsVO1 = new PassbookDetailsVO();
            				passbookDetailsVO1.setCommissionValue(rs.getLong("commission_value"));
            				map.put(rs.getDate("transfer_date"),passbookDetailsVO1);
            			}
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
			try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    
    public LinkedHashMap<Date, PassbookDetailsVO> loadCommissionQtyC2S(java.sql.Connection con, PassbookDetailsVO passbookDetailsVO,LinkedHashMap<Date, PassbookDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadCommissionQtyC2S";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadCommissionQtyC2S():: Entered with passbookDetailsVO:");
        	msg.append(passbookDetailsVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("SELECT ADJUSTMENTS.margin_amount,C2S_TRANSFERS.transfer_date FROM ADJUSTMENTS ,C2S_TRANSFERS WHERE ADJUSTMENTS.REFERENCE_ID = C2S_TRANSFERS.TRANSFER_ID AND C2S_TRANSFERS.TRANSFER_STATUS = '200' AND C2S_TRANSFERS.SENDER_ID=? AND C2S_TRANSFERS.TRANSFER_DATE BETWEEN ? and ? ");
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, passbookDetailsVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getToDate()));
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			if(map.containsKey(rs.getDate("transfer_date")))
            			{
            			PassbookDetailsVO passbookDetailsVO1=map.get(rs.getDate("transfer_date"));
        				passbookDetailsVO1.setMarginAmount(rs.getLong("margin_amount")+map.get(rs.getDate("transfer_date")).getMarginAmount());
        				map.put(rs.getDate("transfer_date"),passbookDetailsVO1);
            			}
            			else
            			{
            				PassbookDetailsVO passbookDetailsVO1 = new PassbookDetailsVO();
            				passbookDetailsVO1.setMarginAmount(rs.getLong("margin_amount"));
            				map.put(rs.getDate("transfer_date"),passbookDetailsVO1);
            			}
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
			try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    
    public LinkedHashMap<Date, PassbookDetailsVO> loadStockPurchaseC2C(java.sql.Connection con, PassbookDetailsVO passbookDetailsVO,LinkedHashMap<Date, PassbookDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadStockPurchaseC2C";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadStockPurchaseC2C():: Entered with passbookDetailsVO:");
        	msg.append(passbookDetailsVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("SELECT CHANNEL_TRANSFERS.REQUESTED_QUANTITY,CHANNEL_TRANSFERS.transfer_date FROM CHANNEL_TRANSFERS WHERE status = 'CLOSE' AND TO_USER_ID= ? AND TRANSFER_SUB_TYPE='T' AND TRANSFER_DATE BETWEEN ? and ? ");
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, passbookDetailsVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getToDate()));
        	long stockPurchase = 0;
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	while (rs.next()) {
        			if(map.containsKey(rs.getDate("transfer_date")))
        			{
        				PassbookDetailsVO passbookDetailsVO1=map.get(rs.getDate("transfer_date"));
        				stockPurchase = passbookDetailsVO1.getStockPurchase()+ rs.getLong("REQUESTED_QUANTITY");
        				passbookDetailsVO1.setStockPurchase(stockPurchase);
        				map.put(rs.getDate("TRANSFER_DATE"), passbookDetailsVO1);
        			}
        			else{
        				PassbookDetailsVO passbookDetailsVO1 = new PassbookDetailsVO();
        				passbookDetailsVO1.setStockPurchase(rs.getLong("REQUESTED_QUANTITY"));
        				map.put(rs.getDate("TRANSFER_DATE"), passbookDetailsVO1);
        			}
        			
        		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
			try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    
    public LinkedHashMap<Date, PassbookDetailsVO> loadClosingBalance(java.sql.Connection con, PassbookDetailsVO passbookDetailsVO,LinkedHashMap<Date, PassbookDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadClosingBalance";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadClosingBalance():: Entered with passbookDetailsVO:");
        	msg.append(passbookDetailsVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("SELECT balance,balance_date FROM USER_DAILY_BALANCES WHERE USER_ID = ? ");
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, passbookDetailsVO.getUserID());
        	
        	long balance = 0;
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			if(map.containsKey(rs.getDate("balance_date")))
            			{
            				PassbookDetailsVO passbookDetailsVO1=map.get(rs.getDate("balance_date"));
            				balance = passbookDetailsVO1.getClosingBalance()+ rs.getLong("balance");
            				passbookDetailsVO1.setClosingBalance(balance);
            				map.put(rs.getDate("balance_date"), passbookDetailsVO1);
            			}
            			else{
            				PassbookDetailsVO passbookDetailsVO1 = new PassbookDetailsVO();
            				passbookDetailsVO1.setClosingBalance(rs.getLong("balance"));
            				map.put(rs.getDate("balance_date"), passbookDetailsVO1);
            			}
            		}
            	}
			TreeMap<Date, PassbookDetailsVO> treeMap = new TreeMap<>();
        	treeMap.putAll(map);
        	Date firstDate = treeMap.firstKey();
        	Date lastDate=DateUtils.addDays(passbookDetailsVO.getToDate(), 1);
        	
        	Date current = firstDate;
        	Date previous = DateUtils.addDays(firstDate, -1);
            while (current.before(lastDate)) {
              if(treeMap.containsKey(current))
              {
            	PassbookDetailsVO passbookDetailsVOcurrent= treeMap.get(current);
              if(treeMap.containsKey(previous))
            	{
            	  PassbookDetailsVO passbookprev = treeMap.get(previous);
            	  passbookDetailsVOcurrent.setOpeningBalance(passbookprev.getClosingBalance());
						if ("0".equalsIgnoreCase(String.valueOf(passbookDetailsVOcurrent.getClosingBalance())))// last date where txn
															// took place but
															// not updated in
															// user_daily_balance
						{
							    PreparedStatement pstmt1 = null;
								StringBuilder strBuffer1 = new StringBuilder("SELECT balance FROM USER_BALANCES WHERE USER_ID = ? ");
								pstmt1 = con.prepareStatement(strBuffer1.toString());
					        	int i1 = 0;
					            ++i1;
					        	pstmt1.setString(i1, passbookDetailsVO.getUserID());
								try(ResultSet rs = pstmt1.executeQuery();)
				            	{
						        	long balance1 = 0;
				            		while (rs.next()) {
				            			balance1+=rs.getLong("balance");
				            		}
				            		passbookDetailsVOcurrent.setClosingBalance(balance1);
				            	}
						}
            	}
              else
              {
            	  passbookDetailsVOcurrent.setOpeningBalance(0);//first entry opening balance will always be 0.
              }
            	map.put(current, passbookDetailsVOcurrent);
              }
              else
              {
            	  PassbookDetailsVO passbookprev=treeMap.get(previous);
            	  PassbookDetailsVO newPass=new PassbookDetailsVO();
            	  newPass.setOpeningBalance(passbookprev.getClosingBalance());
            	  newPass.setClosingBalance(passbookprev.getClosingBalance());
            	  map.put(current, newPass);
            	  treeMap.put(current, newPass);
              }
              
              previous=current;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(current);
                calendar.add(Calendar.DATE, 1);
                current = calendar.getTime();
            }
            OracleUtil.closeQuietly(pstmt);
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
    	}
        return map;
    }
    
    
    
    public LinkedHashMap<Date, PassbookDetailsVO>  loadWithdrawBalance(java.sql.Connection con, PassbookDetailsVO passbookDetailsVO,LinkedHashMap<Date, PassbookDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadWithdrawBalance";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadWithdrawBalance():: Entered with passbookDetailsVO:");
        	msg.append(passbookDetailsVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("SELECT CHANNEL_TRANSFERS.REQUESTED_QUANTITY,CHANNEL_TRANSFERS.transfer_date FROM CHANNEL_TRANSFERS WHERE status = 'CLOSE' AND TO_USER_ID= ?  AND TRANSFER_SUB_TYPE='W' AND TRANSFER_DATE BETWEEN ? and ? ");
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, passbookDetailsVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getToDate()));
        	long requestedQty = 0;
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	while (rs.next()) {
        			if(map.containsKey(rs.getDate("transfer_date")))
        			{
        				PassbookDetailsVO passbookDetailsVO1=map.get(rs.getDate("transfer_date"));
        				requestedQty = passbookDetailsVO1.getWithdrawBalance()+ rs.getLong("REQUESTED_QUANTITY");
        				passbookDetailsVO1.setWithdrawBalance(requestedQty);
        				map.put(rs.getDate("transfer_date"), passbookDetailsVO1);
        			}
        			else{
        				PassbookDetailsVO passbookDetailsVO1 = new PassbookDetailsVO();
        				passbookDetailsVO1.setWithdrawBalance(rs.getLong("REQUESTED_QUANTITY"));
        				map.put(rs.getDate("transfer_date"), passbookDetailsVO1);
        			}
        		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
			try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    public LinkedHashMap<Date, PassbookDetailsVO> loadReturnBalance(java.sql.Connection con, PassbookDetailsVO passbookDetailsVO,LinkedHashMap<Date, PassbookDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadReturnBalance";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadReturnBalance():: Entered with passbookDetailsVO:");
        	msg.append(passbookDetailsVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
		StringBuilder strBuffer = new StringBuilder("SELECT CHANNEL_TRANSFERS.REQUESTED_QUANTITY,CHANNEL_TRANSFERS.transfer_date FROM CHANNEL_TRANSFERS WHERE status = 'CLOSE' AND FROM_USER_ID= ? AND TRANSFER_SUB_TYPE='R' AND TRANSFER_DATE BETWEEN ?  and ? ");
        try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, passbookDetailsVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookDetailsVO.getToDate()));
        	long requestedQty = 0;
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	while (rs.next()) {
        			if(map.containsKey(rs.getDate("transfer_date")))
        			{
        				PassbookDetailsVO passbookDetailsVO1=map.get(rs.getDate("transfer_date"));
        				requestedQty = passbookDetailsVO1.getReturnBalance()+ rs.getLong("REQUESTED_QUANTITY");
        				passbookDetailsVO1.setReturnBalance(requestedQty);
        				map.put(rs.getDate("transfer_date"), passbookDetailsVO1);
        			}
        			else{
        				PassbookDetailsVO passbookDetailsVO1 = new PassbookDetailsVO();
        				passbookDetailsVO1.setReturnBalance(rs.getLong("REQUESTED_QUANTITY"));
        				map.put(rs.getDate("transfer_date"), passbookDetailsVO1);
        			}
        		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
			try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }
        return map;
    }
    
    public void loadUserIncomeC2CandO2C(java.sql.Connection con,TotalUserIncomeDetailsVO totalIncomeDetailsViewVO ,LinkedHashMap<Date, TotalUserIncomeDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadUserIncomeC2CandO2C";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadUserIncomeC2CandO2C: Entered with loadUserIncomeC2CandO2C");
        	msg.append(totalIncomeDetailsViewVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        long totalincomec2co2c = 0;
        long totalbasecomm = 0;
        long totalcbc = 0;
        try {
        	
        	String selectQuery = null;
			selectQuery = userQry.loadUserIncomeC2CandO2CQry();
			
        	pstmt = con.prepareStatement(selectQuery);
        	int i = 0;
            ++i;
        	pstmt.setString(i, totalIncomeDetailsViewVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(totalIncomeDetailsViewVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(totalIncomeDetailsViewVO.getToDate()));
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	while (rs.next()) {
        			if(map.containsKey(rs.getDate("transfer_date")))
        			{
        				TotalUserIncomeDetailsVO totalIncomeDetailsViewVO2=map.get(rs.getDate("transfer_date"));
        				totalIncomeDetailsViewVO2.setCbc(rs.getLong("cbc"));
        				totalIncomeDetailsViewVO2.setBaseCommission(rs.getLong("netcom") - totalIncomeDetailsViewVO2.getCbc());
        				map.put(rs.getDate("transfer_date"), totalIncomeDetailsViewVO2);
        			}
        			else{
        				TotalUserIncomeDetailsVO totalIncomeDetailsViewVO2 = new TotalUserIncomeDetailsVO();
        				totalIncomeDetailsViewVO2.setCbc(rs.getLong("cbc"));
        				totalIncomeDetailsViewVO2.setBaseCommission(rs.getLong("netcom") - totalIncomeDetailsViewVO2.getCbc());
        				map.put(rs.getDate("transfer_date"), totalIncomeDetailsViewVO2);
        			}
        			totalcbc+=rs.getLong("cbc");
        	        totalbasecomm+=(rs.getLong("netcom") - rs.getLong("cbc"));
        			totalincomec2co2c+=rs.getLong("netcom");
        		}
            	totalIncomeDetailsViewVO.setToatalincomec2co2c(totalincomec2co2c);
            	totalIncomeDetailsViewVO.setTotalBaseCom(totalbasecomm);
            	totalIncomeDetailsViewVO.setTotalCbc(totalcbc);
            }
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserIncomeC2CandO2C]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserIncomeC2CandO2C]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
			try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }    
    }
    
    
    public void loadUserIncomeC2S(java.sql.Connection con,TotalUserIncomeDetailsVO totalIncomeDetailsViewVO,LinkedHashMap<Date, TotalUserIncomeDetailsVO> map) throws BTSLBaseException {
    	final String methodName = "loadUserIncomeC2S";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadUserIncomeC2S: Entered with loadUserIncomeC2S");
        	msg.append(totalIncomeDetailsViewVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        long totalincomec2s = 0;
        long totalcac = 0; 
        long totaladdbasecomm = 0;
        
        try {
        	
        	String selectQuery = null;
			selectQuery = userQry.loadUserIncomeC2SQry();
			
        	pstmt = con.prepareStatement(selectQuery.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, totalIncomeDetailsViewVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(totalIncomeDetailsViewVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(totalIncomeDetailsViewVO.getToDate()));
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	while (rs.next()) {
        			if(map.containsKey(rs.getDate("transfer_date")))
        			{
        				TotalUserIncomeDetailsVO totalIncomeDetailsViewVO2=map.get(rs.getDate("transfer_date"));
        				totalIncomeDetailsViewVO2.setCac(rs.getLong("cac"));
        				totalIncomeDetailsViewVO2.setAdditionalCommission(rs.getLong("margin") - totalIncomeDetailsViewVO2.getCac());
        				map.put(rs.getDate("transfer_date"), totalIncomeDetailsViewVO2);
        			}
        			else{
        				TotalUserIncomeDetailsVO totalIncomeDetailsViewVO2 = new TotalUserIncomeDetailsVO();
        				totalIncomeDetailsViewVO2.setCac(rs.getLong("cac"));
        				totalIncomeDetailsViewVO2.setAdditionalCommission(rs.getLong("margin") - totalIncomeDetailsViewVO2.getCac());
        				map.put(rs.getDate("transfer_date"), totalIncomeDetailsViewVO2);
        			}
        			totalcac+=rs.getLong("cac");
        			totaladdbasecomm+=(rs.getLong("margin") - rs.getLong("cac"));
        			totalincomec2s+=rs.getLong("margin");
            	}
            	totalIncomeDetailsViewVO.setCac(totalcac);
            	totalIncomeDetailsViewVO.setTotalAdditionalBaseCom(totaladdbasecomm);
            	totalIncomeDetailsViewVO.setTotalincomec2s(totalincomec2s);
            }
            
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserIncomeC2S]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserIncomeC2S]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
		try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + map);
            }
        }   
    }
    public void loadUserTotalIncomeDetailsBetweenRange(java.sql.Connection con,TotalUserIncomeDetailsVO totalIncomeDetailsViewVO,Date fromDate, Date toDate) throws BTSLBaseException {
    	final String methodName = "loadUserTotalIncomeDetailsBetweenRange";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("loadUserTotalIncomeDetailsBetweenRange: Entered with loadUserTotalIncomeDetailsBetweenRange");
        	msg.append(totalIncomeDetailsViewVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        long totalincome = 0;
        long totalcac = 0; 
        long totaladdbasecomm = 0;
        long totalcbc = 0; 
        long totalbasecomm = 0;
        try {
        	String selectQuery = null;
			selectQuery = userQry.loadUserTotalIncomeDetailsBetweenRangeC2SQry();
			
        	pstmt = con.prepareStatement(selectQuery);
        	int i = 0;
            ++i;
        	pstmt.setString(i, totalIncomeDetailsViewVO.getUserID());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(fromDate));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(toDate));
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	 if (rs.next()) 
                 {
            	totalcac = rs.getLong("cac");
            	totaladdbasecomm = rs.getLong("margin") - rs.getLong("cac");
                 }
               }
            String selectQuery1 = null;
			selectQuery1 = userQry.loadUserTotalIncomeDetailsBetweenRangeC2CAndO2CQry();
			
    		pstmt2 = con.prepareStatement(selectQuery1);
    		i = 0;
            ++i;
         	pstmt2.setString(i, totalIncomeDetailsViewVO.getUserID());
         	++i;
         	pstmt2.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(fromDate));
         	++i;
         	pstmt2.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(toDate));
             try(ResultSet rs = pstmt2.executeQuery();)
             	{
             	 if (rs.next()) 
                  {
             	totalcbc = rs.getLong("cbc");
             	totalbasecomm = rs.getLong("netcom") - rs.getLong("cbc");
                  }
                }
             totalincome = totalcbc+totalcac+totalbasecomm+totaladdbasecomm;
             totalIncomeDetailsViewVO.setPreviousTotalIncome(totalincome);
             totalIncomeDetailsViewVO.setPreviousTotalCbc(totalcbc);
             totalIncomeDetailsViewVO.setPreviousTotalCac(totalcac);
             totalIncomeDetailsViewVO.setPreviousTotalBaseComm(totalbasecomm);
             totalIncomeDetailsViewVO.setPreviousTotalAdditionalBaseCom(totaladdbasecomm); 
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserTotalIncomeDetailsBetweenRange]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserTotalIncomeDetailsBetweenRange]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting Method name :" + methodName);
            }
        }  
    }
    /**
  	 * @param con
  	 * @param request
  	 * @param userDetailsCount
  	 * @param userDetailsList
  	 * @param minLength
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public void autoCompleteUserDetails(Connection p_con, AutoCompleteUserDetailsRequestVO request, int userDetailsCount,
    		List<AutoCompleteUserDetailsResponseVO> userDetailsList, int minLength)
			throws BTSLBaseException {
		final String methodName = "autoCompleteUserDetails";

		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: autoCompleteUserDetails=");
			loggerValue.append(userDetailsCount);

			LOG.debug(methodName, loggerValue);
		}
		
		AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO=null;

		try {
			String identifierType =null;
			String identifierValue = null;
			if((request.getMsisdn2()).length() >= minLength) {
				identifierValue = request.getMsisdn2();
				identifierType = "MSISDN";
			}
			else if((request.getLoginId2()).length() >= minLength) {
				identifierValue =request.getLoginId2();
				identifierType = "LOGIN_ID";
			}
			else {
				identifierValue = request.getUserName2();
				identifierType = "USER_NAME";
			}
			

			String selectQuery = null;
			selectQuery = userQry.fetchUserDetails(request, identifierType);
			

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}
			
			try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
				int i=0;
				++i;
				if(request.isSpecificSearch())	pstmtSelect.setString(i, identifierValue);
				else pstmtSelect.setString(i, "%" + identifierValue + "%");
				
				if(!"".equalsIgnoreCase(request.getCategory())){
					++i;
					pstmtSelect.setString(i, request.getCategory());
				}
				if(!"".equalsIgnoreCase(request.getDomain())){
					++i;
					pstmtSelect.setString(i, request.getDomain());
				}
				
				if(userDetailsCount!=-1) {
					++i;
					pstmtSelect.setInt(i, (userDetailsCount));
				}
				
		
				try (ResultSet rs = pstmtSelect.executeQuery();) {
					
					while (rs.next()) {
						autoCompleteUserDetailsResponseVO= new AutoCompleteUserDetailsResponseVO();
						autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
						autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
						autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
						autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
						userDetailsList.add(autoCompleteUserDetailsResponseVO);			
						}

				}
			} // end of try
		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[" + methodName + "]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[" + methodName + "]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.processing", e);
		} // end of catch
		finally {

			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting methodName:" + methodName);
			}
		} // end of finally
	}
    /**
     * To get the payment types of the user
     * @param con
     * @param userId
     * @return
     * @throws BTSLBaseException
     */
    public String getPaymentTypes(Connection con, String userId) throws BTSLBaseException{

    	final String methodName = "getPaymentType";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("getPaymentType: Entered with userId");
        	msg.append(userId);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        String paymentType = null;
        PreparedStatement pstmt = null;
        StringBuilder strBuffer = new StringBuilder(" SELECT payment_type from users where user_id = ?");
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	int i = 0;
            ++i;
        	pstmt.setString(i, userId);
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	 if (rs.next()) 
                 {
            		 paymentType = rs.getString("PAYMENT_TYPE");
                 }
               }
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserTotalIncomeDetailsBetweenRange]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserTotalIncomeDetailsBetweenRange]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting Method name :" + methodName);
            }
        }  
            return paymentType;
    }
    /**
  	 * @param con
  	 * @param domaincode
  	 *  @param domainCodeName
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public GetDomainCategoryResponseVO loadDomainCategory(java.sql.Connection con,String domainCode,String domainCodeName,RequestVO p_requestVO) throws BTSLBaseException {
    	final String methodName = "loadDomainCategory";
    	GetDomainCategoryMsg getDomainCategoryMsg ;
        ArrayList<GetDomainCategoryMsg> transactionList=new ArrayList<GetDomainCategoryMsg>();
        //final ArrayList<ArrayList<GetDomainCategoryMsg>> result = new ArrayList<ArrayList<GetDomainCategoryMsg>>();
        final  GetDomainCategoryResponseVO result = new GetDomainCategoryResponseVO();

    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered loadDomainCategory");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        result.setDomainCode(domainCode);
        result.setDomainCodeName(domainCodeName);
        PreparedStatement pstmt = null;
        String selectQuery=null;
        if(domainCode.equalsIgnoreCase("OPT")) {
        	selectQuery = userQry.fetchDomainCatFrOpt();
        }
        else {selectQuery = userQry.fetchDomainCat();}
		
        

		try {
        	pstmt = con.prepareStatement(selectQuery);
        	
            	int i = 0;
                ++i;
                pstmt.setString(i, domainCode);
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			getDomainCategoryMsg=new GetDomainCategoryMsg();
            			getDomainCategoryMsg.setCategoryCode(rs.getString("TO_CATEGORY"));
            			getDomainCategoryMsg.setCategoryName(rs.getString("CATEGORY_NAME"));
            			transactionList.add(getDomainCategoryMsg);
            		
            		}
            		if(BTSLUtil.isNullOrEmptyList(transactionList)) {
            			p_requestVO.setSuccessTxn(false);
            			p_requestVO.setSenderReturnMessage("NO Details Found For Input");
            		}else {
            			
            			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
            		}
            		
            		result.setCategoryList(transactionList);
            		
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + result);
            }
        } 
            return result;
    }

    
    /**this method gives domain name for give domain code or vice versa
  	 * @param con
  	 * @param domaincode
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public String getDomainNameOrCode(java.sql.Connection con,String domainCode,String domainName) throws BTSLBaseException {
    	final String methodName = "getDomainNameOrCode";
       String result="";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered getDomainName:");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        PreparedStatement pstmt = null;
       
        StringBuilder strBuffer = null;
        
        if(!BTSLUtil.isNullString(domainCode)) {
        	strBuffer = new StringBuilder("SELECT DOMAIN_NAME FROM domains WHERE DOMAIN_CODE= ?");
        }
        else if(!BTSLUtil.isNullString(domainName)) {
        	strBuffer = new StringBuilder("SELECT DOMAIN_CODE FROM domains WHERE  DOMAIN_NAME= ?");
        }
    	
        
    	

  
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	 if(!BTSLUtil.isNullString(domainCode)) {
        		 int i = 0;
                 ++i;
                 pstmt.setString(i, domainCode);
             }
             else if(!BTSLUtil.isNullString(domainName)) {
            	 int i = 0;
                 ++i;
                 pstmt.setString(i, domainName);
             }
            	
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			 if(!BTSLUtil.isNullString(domainCode)) {
            				 result=SqlParameterEncoder.encodeParams(rs.getString("DOMAIN_NAME"));
                         }
                         else if(!BTSLUtil.isNullString(domainName)) {
                        	 result=SqlParameterEncoder.encodeParams(rs.getString("DOMAIN_CODE"));
                         }
            			
            		}
            		
            		
        
            		
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getDomainNameOrCode]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getDomainNameOrCode]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + result);
            }
        }
            return result;
    }

    public boolean validateUserLoginIdorMsisdn(String loginId, String msisdn,String networkCode, Connection con) throws BTSLBaseException{
    	

    	final String methodName = "validateUserLoginIdorMsisdn";
    	boolean valid = false;
       
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered validateUserLoginIdorMsisdn:");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        PreparedStatement pstmt = null;
       
        StringBuilder strBuffer = null;
    	strBuffer = new StringBuilder("SELECT 1 FROM users WHERE  NETWORK_CODE=? AND STATUS=?");
    	if(!BTSLUtil.isNullString(msisdn)){
    		strBuffer.append("AND MSISDN = ?");
    	}
    	else if(!BTSLUtil.isNullString(loginId)){
    		strBuffer.append("AND LOGIN_ID = ?");
    	}
  
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	
            	int i = 0;
               
                pstmt.setString(++i, networkCode);
                pstmt.setString(++i,PretupsI.USER_STATUS_ACTIVE);
                if(!BTSLUtil.isNullString(msisdn)){
                	 pstmt.setString(++i, msisdn);
            	}
            	else if(!BTSLUtil.isNullString(loginId)){
            		 pstmt.setString(++i, loginId);
            	}
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		if (rs.next()) {
            			valid = true;
            		}
            		OracleUtil.closeQuietly(rs);
            		}
           OracleUtil.closeQuietly(pstmt);
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + valid);
            }
        } 
            return valid;
    
    }
    
public boolean validateUserForActOrPreActLoginIdorMsisdn(String loginId, String msisdn,String networkCode, Connection con) throws BTSLBaseException{
    	

    	final String methodName = "validateUserForActOrPreActLoginIdorMsisdn";
    	boolean valid = false;
       
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered validateUserLoginIdorMsisdn:");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        PreparedStatement pstmt = null;
       
        StringBuilder strBuffer = null;
    	strBuffer = new StringBuilder("SELECT 1 FROM users WHERE  NETWORK_CODE=? ");
    	if(!BTSLUtil.isNullString(msisdn)){
    		strBuffer.append("AND MSISDN = ?");
    	}
    	else if(!BTSLUtil.isNullString(loginId)){
    		strBuffer.append("AND LOGIN_ID = ?");
    	}
  
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	
            	int i = 0;
               
                pstmt.setString(++i, networkCode);
                if(!BTSLUtil.isNullString(msisdn)){
                	 pstmt.setString(++i, msisdn);
            	}
            	else if(!BTSLUtil.isNullString(loginId)){
            		 pstmt.setString(++i, loginId);
            	}
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		if (rs.next()) {
            			valid = true;
            		}
            		OracleUtil.closeQuietly(rs);
            		}
           OracleUtil.closeQuietly(pstmt);
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + valid);
            }
        } 
            return valid;
    
    }
    
    
    
    /**
     * This method gets all the channel users that come within the hierarchy 
     * @param con
     * @param userName
     * @param userDomain
     * @param userCategoryCode
     * @param userGeography
     * @param msisdn2
     * @param loginId2
     * @param fromRow
     * @param toRow
     * @return
     * @throws BTSLBaseException
     */
 public GetChannelUsersListResponseVo getChannelUsersList(java.sql.Connection con,String userDomain,
    		String userCategoryCode,String userGeography,String userId,String status, boolean selfAllowed) throws BTSLBaseException {
    	final String methodName = "getChannelUsersList";
    	GetChannelUsersMsg getChannelUsersMsg ;
        ArrayList<GetChannelUsersMsg> channelUsersList=new ArrayList<GetChannelUsersMsg>();
        //final ArrayList<ArrayList<GetDomainCategoryMsg>> result = new ArrayList<ArrayList<GetDomainCategoryMsg>>();
        final  GetChannelUsersListResponseVo result = new GetChannelUsersListResponseVo();
        
        HashMap<String,GetChannelUsersMsg> resultMap = new HashMap<String,GetChannelUsersMsg>();
     
        
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered getChannelUsersList");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
       /* result.setDomainCode(domainCode);
        result.setDomainCodeName(domainCodeName);*/
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        StringBuilder strBuffer = null;
        ArrayList <ProductVO> productList = new ArrayList<ProductVO>();
        	
       
		try {

			String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
			boolean tcpOn = false;
			Set<String> uniqueTransProfileId = new HashSet();

			if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
				tcpOn = true;
			}
			String sqlSelect = null;
			HashMap<String, HashMap<String, String>> tcpMap = null;
			
			
			if (tcpOn) {

				pstmt = userQry.getChannelUsersListTcpQry(con, userDomain, userCategoryCode, userGeography, userId,
						status);
				
				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")),
	 					ValueType.STRING);
	         	
	         	
	         	
	         	tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
	         	
			} else {

				pstmt = userQry.getChannelUsersListQry(con, userDomain, userCategoryCode, userGeography, userId,
						status, selfAllowed);
			}
			
			
			//Hard coding to be removed later. Committing now for UI.
			pstmt1 = con.prepareStatement("select product_code,product_name from products where status = 'Y' and product_type = 'PREPROD'");
        	
			try(ResultSet rs1 = pstmt1.executeQuery();)
			{
				
				while (rs1.next()) {
					ProductVO productVO = new ProductVO();
					
					productVO.setProductCode(rs1.getString("product_code"));
					productVO.setProductName(rs1.getString("product_name"));
					
					productList.add(productVO);
				}
			}
			
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		
            		while (rs.next()) {
            			
            			if((!selfAllowed && userId.equalsIgnoreCase(rs.getString("USER_ID"))) || rs.getString("STATUS").equals("C") || rs.getString("STATUS").equals("N")) 
            			{
            				continue;
            			}
            			else {
            					if(resultMap.get(rs.getString("USER_ID")) != null)
            					{
            						GetChannelUsersMsg object = resultMap.get(rs.getString("USER_ID"));
            						
            						boolean toBeAdded = true;
            						
            						for(BalanceVO balanceVO:object.getBalanceList())
            						{
            							if(balanceVO.getProductName()!=null && balanceVO.getProductName().equals(rs.getString("product_name")))
            							{
            								toBeAdded = false;
            								break;
            							}
            						}
            						if(toBeAdded)
            						{
            							BalanceVO balanceVO = new BalanceVO();
    		                 			balanceVO.setBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
    		                 			balanceVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));
    		                 			
    		                 			object.getBalanceList().add(balanceVO);
            						}
            						
            					}
            					else
		            			{		
		            				getChannelUsersMsg=new GetChannelUsersMsg();
		                 			getChannelUsersMsg.setUserName(SqlParameterEncoder.encodeParams(rs.getString("USER_NAME")));
		                 			getChannelUsersMsg.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("MSISDN")));
		                 			getChannelUsersMsg.setUserType(SqlParameterEncoder.encodeParams(rs.getString("USER_TYPE")));

		                 			
		                 			
		                 			
		                 			if(rs.getString("BALANCE")!=null)
		                 			{
		                 				List<BalanceVO> balanceList = new ArrayList<BalanceVO>();
		                 				BalanceVO balanceVO = new BalanceVO();
		                 				balanceVO.setBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
			                 			balanceVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));
			                 			
			                 			balanceList.add(balanceVO);
			                 			getChannelUsersMsg.setBalanceList(balanceList);
		                 			}
		                 			
		                 			
		                 		  //  getChannelUsersMsg.seteTopUpBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
		                 			 if(rs.getString("STATUS").equals("Y")) {
		                 				getChannelUsersMsg.setStatus("Active"); 
		                 			 }
		                 			 else if(rs.getString("STATUS").equals("W")) {
		                 				getChannelUsersMsg.setStatus("New");
		                 			 }
		                 			 else if(rs.getString("STATUS").equals("C")) {
		                 				getChannelUsersMsg.setStatus("Cancelled");
		                 			 }
		                 			 else if(rs.getString("STATUS").equals("A")) {
		                 				getChannelUsersMsg.setStatus("Approved");
		                 			 }
		                 			 else if(rs.getString("STATUS").equals("N")) {
		                 				getChannelUsersMsg.setStatus("Deleted");
		                 			 }
		                 			 else if(rs.getString("STATUS").equals("S")) {
		                 				getChannelUsersMsg.setStatus("Suspended");
		                 			 }
		                 			 else if(rs.getString("STATUS").equals("EX")) {
		                 				getChannelUsersMsg.setStatus("Expired");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("D")) {
		                 				getChannelUsersMsg.setStatus("Deregistered");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("B")) {
		                 				getChannelUsersMsg.setStatus("Block");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("SR")) {
		                 				getChannelUsersMsg.setStatus("Suspend Request");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("DR")) {
		                 				getChannelUsersMsg.setStatus("Delete Request");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("BR")) {
		                 				getChannelUsersMsg.setStatus("Bar Request");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("BD")) {
		                 				getChannelUsersMsg.setStatus("Barred");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("BA")) {
		                 				getChannelUsersMsg.setStatus("Bar Approve");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("CH")) {
		                 				getChannelUsersMsg.setStatus("Churned");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("DE")) {
		                 				getChannelUsersMsg.setStatus("Deactivated");
		                 			 }
		                 			else if(rs.getString("STATUS").equals("PA")) {
		                 				getChannelUsersMsg.setStatus("Pre Active");
		                 			 }
		                 			
		                 			getChannelUsersMsg.setStatusCode(rs.getString("STATUS"));
		                 			
		                 			
		                 			 getChannelUsersMsg.setDomain(SqlParameterEncoder.encodeParams(rs.getString("DOMAIN_NAME")));
		                 			 getChannelUsersMsg.setCategory(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
		                 			getChannelUsersMsg.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
		                 			 
		                 			if(BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(rs.getString("parent_name")))) {
		                 				getChannelUsersMsg.setParentName("ROOT");
		                 			 }
		                 			 else 
		                 			 {
		                 				getChannelUsersMsg.setParentName(SqlParameterEncoder.encodeParams(rs.getString("parent_name")));
		                 			 }
		                 			getChannelUsersMsg.setOwnerName(SqlParameterEncoder.encodeParams(rs.getString("owner_name")));
//		                 			getChannelUsersMsg.setLastModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
		                 			getChannelUsersMsg.setLastModifiedDateTime(rs.getTimestamp("modified_on"));
							if (!tcpOn) {
								getChannelUsersMsg.setTransactionProfile(
										SqlParameterEncoder.encodeParams(rs.getString("profile_name")));
							} else {

								getChannelUsersMsg.setTransactionProfile(SqlParameterEncoder.encodeParams(
										tcpMap.get(rs.getString("TRANSFER_PROFILE_ID")).get("Name")));

							}
		                 			getChannelUsersMsg.setCommissionProfile(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_name")));
		                 			getChannelUsersMsg.setContactPerson(SqlParameterEncoder.encodeParams(rs.getString("contact_person")));
		                 			getChannelUsersMsg.setLastTxnDatTime(rs.getTimestamp("last_transfer_on"));
		                 			getChannelUsersMsg.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
		                 			getChannelUsersMsg.setGrade(SqlParameterEncoder.encodeParams(rs.getString("user_grade")));
		                 			getChannelUsersMsg.setRegisteredDateTime(rs.getTimestamp("created_on"));
		                 			getChannelUsersMsg.setGeography(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_name")));
		                 			getChannelUsersMsg.setParentID(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
		                 			getChannelUsersMsg.setUserID(SqlParameterEncoder.encodeParams(rs.getString("USER_ID")));
		                 			channelUsersList.add(getChannelUsersMsg);
		                 			resultMap.put(rs.getString("USER_ID"), getChannelUsersMsg);
		              			
		            			}	
            			}
            		}		
            	}
            
			for(GetChannelUsersMsg listObject:channelUsersList)
			{
				//LOG.debug(methodName, "Entering for user: "+ listObject.getLoginID());
				if(listObject.getBalanceList() == null)
				{
					//LOG.debug(methodName, "No balances exist for user: "+ listObject.getLoginID());
					List<BalanceVO> balanceList = new ArrayList<BalanceVO>();
					
					for (ProductVO productVO:productList)
					{
						BalanceVO balanceVO = new BalanceVO();
						balanceVO.setProductName(productVO.getProductName());
						balanceList.add(balanceVO);
					}
					listObject.setBalanceList(balanceList);
				}
				else if(listObject.getBalanceList().size() == productList.size())
				{
					//LOG.debug(methodName, "All balances exist for user: "+ listObject.getLoginID());
					
				}
				else if(listObject.getBalanceList().size() != productList.size())
				{
					//LOG.debug(methodName, "Some balances exist for user: "+ listObject.getLoginID());

					for (ProductVO productVO:productList)
					{
						//LOG.debug(methodName, "Entering for product: "+ productVO.getProductName());
						boolean isFound = false;
						for (BalanceVO balanceVO:listObject.getBalanceList())
						{
							if(balanceVO.getProductName().equalsIgnoreCase(productVO.getProductName()))
							{
								//LOG.debug(methodName, "Product found: "+ productVO.getProductName());
								isFound = true;
							}
						}
						if(!isFound)
						{
							LOG.debug(methodName, "Product not found: "+ productVO.getProductName());
							BalanceVO balanceVO = new BalanceVO();
							balanceVO.setProductName(productVO.getProductName());
							listObject.getBalanceList().add(balanceVO);
						}
						
					}
					
				}
				
				//LOG.debug(methodName, "Sorting for user: "+ listObject.getLoginID());
				Collections.sort(listObject.getBalanceList(), new Comparator<BalanceVO>() {
					
					
				  
					@Override
					public int compare(BalanceVO arg0, BalanceVO arg1) {

						return arg1.getProductName().compareTo(arg0.getProductName());
					}
				});
			}
			
			result.setChannelUsersList(channelUsersList);
            
            
            		
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                //LOG.debug(methodName, "Exiting userName:" + result);
            }
        }
            return result;
    } 
  /**
   * This method gives the categoryName for CategoryCode or vice versa
   * @param con
   * @param categoryCode
   * @return
   * @throws BTSLBaseException
   */
    public String getCategoryNameFromCatCode(java.sql.Connection con,String categoryCode,String categoryName) throws BTSLBaseException {

    	final String methodName = "getCategoryNameFromCatCode";
    	GetDomainCategoryMsg getDomainCategoryMsg ;
       String result="";
       
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered getCategoryNameFromCatCode:");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        PreparedStatement pstmt = null;
       
        StringBuilder strBuffer = null;
        if(!BTSLUtil.isNullString(categoryCode)) {
        	strBuffer = new StringBuilder("SELECT CATEGORY_NAME FROM CATEGORIES WHERE CATEGORY_CODE= ?");
        }
        else if(!BTSLUtil.isNullString(categoryName)) {
        	strBuffer = new StringBuilder("SELECT CATEGORY_CODE FROM CATEGORIES WHERE CATEGORY_NAME= ?");
        }
    	

  
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	 if(!BTSLUtil.isNullString(categoryCode)) {
        		 int i = 0;
                 ++i;
                 pstmt.setString(i, categoryCode);
             }
             else if(!BTSLUtil.isNullString(categoryName)) {
            	 int i = 0;
                 ++i;
                 pstmt.setString(i, categoryName);
             }
            	
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			
            			
            			if(!BTSLUtil.isNullString(categoryCode)) {
            				result=SqlParameterEncoder.encodeParams(rs.getString("CATEGORY_NAME"));            	        }
            	        else if(!BTSLUtil.isNullString(categoryName)) {
            	        	result=SqlParameterEncoder.encodeParams(rs.getString("CATEGORY_CODE"));            	        }
            		}
            		
            		
        
            		
            	}
            OracleUtil.closeQuietly(pstmt);
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting getCategoryNameFromCatCode:" + result);
            }
        }
            return result;
    
    	
    }
    
    /**this method gives domain name for give domain code 
  	 * @param con
  	 * @param domaincode
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public String getDomainName(java.sql.Connection con,String domainCode) throws BTSLBaseException {
    	final String methodName = "getDomainNameOrCode";
       String result="";
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered getDomainName:");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        PreparedStatement pstmt = null;
       
        StringBuilder strBuffer = null;
        
      
        	strBuffer = new StringBuilder("SELECT DOMAIN_NAME FROM domains WHERE DOMAIN_CODE= ?");
      
		try {
        	pstmt = con.prepareStatement(strBuffer.toString());
        	
        		 int i = 0;
                 ++i;
                 pstmt.setString(i, domainCode);
             
            	
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			
            				 result=rs.getString("DOMAIN_NAME");
                        	}
            		
            		
        
            		
            	}
            OracleUtil.closeQuietly(pstmt);
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getDomainNameOrCode]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getDomainNameOrCode]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + result);
            }
        }
            return result;
    }

    /**this method gives list of users within logged in users hierarchy 
  	 * @param con
  	 * @param domaincode
  	 * @return
  	 * @throws BTSLBaseException
  	 */
    public ArrayList<GetDomainCatParentCatParentUserMsg> getUsersInHierachyWithCat(java.sql.Connection con,String catCode,String userId,String searchChar) throws BTSLBaseException {
    	final String methodName = "getUsersInHierachyWithCat";
    	GetDomainCatParentCatParentUserMsg user =null;
       ArrayList<GetDomainCatParentCatParentUserMsg> result=new ArrayList();
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered getUsersInHierachyWithCat:");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
         	PreparedStatement pstmtSelect = null;
      
		try {
			pstmtSelect = userQry.getUsersInHierachyWithCatQry(con,catCode,userId);
        	
        	
            try(ResultSet rs = pstmtSelect.executeQuery();)
            	{
            		while (rs.next()) {
            			 user=new GetDomainCatParentCatParentUserMsg();
            			 user.setParentUserName(rs.getString("USER_NAME"));
            			 user.setParentUserId(rs.getString("USER_ID"));
            			 user.setParentMsisdn(rs.getString("MSISDN"));
            			 user.setParentLoginId(rs.getString("LOGIN_ID"));
            			 user.setUserStatus(rs.getString("STATUS"));
            		     result.add(user);
                        	
            		}
            		
            		
        
            		
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getUsersInHierachyWithCat]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getUsersInHierachyWithCat]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + result);
            }
        }
            return result;
    }

    
    
    
    /**
     * Method : Generate new token and persist into database
     * @param p_con
     * @param oAuthUser
     * @return
     * @throws BTSLBaseException
     */
    public int refreshToken(Connection p_con, OAuthRefTokenReq oAuthRefTokenReq, String encodedTokenNew, String encodedTokenRefNew) throws Exception {
        // commented for DB2OraclePreparedStatement psmtInsert = null;
       
        int insertCount = 0;
        final String methodName = "refreshToken";
        StringBuilder loggerValue= new StringBuilder();
        Date currentDate = new Date();
        
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: oAuthRefTokenReq=");
        	loggerValue.append(oAuthRefTokenReq);
        	LOG.debug(methodName, loggerValue);
        }
        try {
                	
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT * from user_token ");
            strBuff.append("where refresh_token = ? ");
            
            
            String query = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(query);
    			LOG.debug(methodName, loggerValue);
    		}
            try( PreparedStatement  pstmt = p_con.prepareStatement(query);)
            {

           
            	pstmt.setString(1, oAuthRefTokenReq.getRefreshToken());

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
            	
            	Calendar calMod = Calendar.getInstance();
            	java.util.Date modifiedTime = rs.getTimestamp("modified_on");
            	calMod.setTime(modifiedTime);
            	calMod.add(Calendar.SECOND, rs.getInt("refresh_token_time_to_live"));
            	Date dMod =  calMod.getTime();
            	
            	Calendar calCur = Calendar.getInstance();
            	Date dCur =  calCur.getTime();
            	
            	if(dCur.before(dMod) == false) {
                    throw new Exception("Refresh Token has been expired!");
                }
            	
            	
            	StringBuilder strBuff2 = new StringBuilder();
                strBuff2.append("UPDATE USER_TOKEN UT ");
                strBuff2.append("SET UT.REFRESH_TOKEN  = ?, UT.TOKEN = ?, UT.MODIFIED_ON = ? WHERE UT.REFRESH_TOKEN = ? ");
                
                
                String query2 = strBuff2.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(query2);
        			LOG.debug(methodName, loggerValue);
        		}
                try( PreparedStatement  pstmt2 = p_con.prepareStatement(query2);)
                {
                	int ind = 1;
                	pstmt2.setString(ind, encodedTokenRefNew);
                	
                	ind++;
                	pstmt2.setString(ind, encodedTokenNew);
                	
                	ind++;
                	pstmt2.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));
                    
                	
                	ind++;
                	pstmt2.setString(ind, oAuthRefTokenReq.getRefreshToken());
                	
                	pstmt2.executeUpdate();
                	p_con.commit();
                }

            	
            }else {
            	
                    throw new Exception("Invalid Refresh Token!");
                
            }
            }
            
        } 
        
        	
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[generateToken]", "", "", "", loggerValue.toString());
            throw sqle;
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[generateToken]", "", "", "", loggerValue.toString());
            throw e;
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, LOG);
           
        } // end of finally

        return insertCount;
    }

    
    /**
     * Method : generate new token and persist into database
     * @param p_con
     * @param oAuthUser
     * @return
     * @throws BTSLBaseException
     */
    public int generateToken(Connection p_con,OAuthTokenReq oAuthTokenReq,  OAuthTokenRes oAuthTokenRes, String tExp, String rtExp) throws Exception {
        // commented for DB2OraclePreparedStatement psmtInsert = null;
       
        int insertCount = 0;
        final String methodName = "generateToken";
        StringBuilder loggerValue= new StringBuilder();
        Date currentDate = new Date();
        
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: oAuthTokenReq=");
        	loggerValue.append(oAuthTokenReq);
        	LOG.debug(methodName, loggerValue);
        }
        try {
        	
        	
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_token (login_id,");
            strBuff.append("msisdn,request_Gateway_Type, request_Gateway_Code, request_Gateway_Login_Id, service_Port, token , request_Gateway_password, source_type, pin,"
            		+ " created_on, modified_on, time_to_live, password, ext_code, refresh_token, refresh_token_time_to_live");
			strBuff.append( " ) ");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            
            String insertQuery = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try( PreparedStatement  psmtInsert = p_con.prepareStatement(insertQuery);)
            {
            //TODO: i	
            int ind = 1;	
            psmtInsert.setString(ind, oAuthTokenReq.getLoginId());

            ind++;
            psmtInsert.setString(ind, oAuthTokenReq.getMsisdn());

            ind++;
            psmtInsert.setString(ind, oAuthTokenReq.getReqGatewayType());
            
            ind++;
            psmtInsert.setString(ind, oAuthTokenReq.getReqGatewayCode());
            
            ind++;
            psmtInsert.setString(ind, oAuthTokenReq.getReqGatewayLoginId());
            
            ind++;
            psmtInsert.setString(ind, oAuthTokenReq.getServicePort());
            
            ind++;
            psmtInsert.setString(ind, oAuthTokenRes.getToken());
            
            ind++;
            psmtInsert.setString(ind, BTSLUtil.encryptText(oAuthTokenReq.getReqGatewayPassword()));
            
            ind++;
            psmtInsert.setString(ind, oAuthTokenReq.getSourceType());
            
            ind++;
            psmtInsert.setString(ind, BTSLUtil.encryptText(oAuthTokenReq.getPin()));
            
            ind++;
            psmtInsert.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));
            
            ind++;
            psmtInsert.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));
            
            ind++;
            psmtInsert.setString(ind, tExp);
            
            ind++;
            psmtInsert.setString(ind, BTSLUtil.encryptText(oAuthTokenReq.getPassword()));
            
            ind++;
            psmtInsert.setString(ind, oAuthTokenReq.getExtCode());
            
            ind++;
            psmtInsert.setString(ind, oAuthTokenRes.getRefreshToken());
            

            ind++;
            psmtInsert.setString(ind, rtExp);
            
            insertCount = psmtInsert.executeUpdate();
            p_con.commit();
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            
            throw sqle;
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            
            throw e;
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, LOG);
           
        } // end of finally

        return insertCount;
    }


    public void validateToken(Connection p_con, String token) throws Exception {
       
        int insertCount = 0;
        final String methodName = "validateToken";
        StringBuilder loggerValue= new StringBuilder();
       
        try {
        	
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT * from user_token ");
            strBuff.append("where token = ? ");
            
            
            String query = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(query);
    			LOG.debug(methodName, loggerValue);
    		}
            try( PreparedStatement  pstmt = p_con.prepareStatement(query);)
            {

           
            	pstmt.setString(1, token);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
            	
            	Calendar calMod = Calendar.getInstance();
            	java.util.Date modifiedTime = rs.getTimestamp("modified_on");
            	calMod.setTime(modifiedTime);
            	calMod.add(Calendar.SECOND, rs.getInt("time_to_live"));
            	Date dMod =  calMod.getTime();
            	
            	Calendar calCur = Calendar.getInstance();
            	Date dCur =  calCur.getTime();
            	
            	
            	if(dCur.before(dMod) == false) {
            		 throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MAPP_TOKEN_EXPIRED, PretupsI.RESPONSE_FAIL,null);
                }
            	
            }else {
            	
            	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MAPP_INVALID_TOKEN, PretupsI.RESPONSE_FAIL,null);
                
            }
            }
            
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            
            throw sqle;
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            
            throw e;
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, LOG);
           
        } // end of finally

        
    }
    
    public void validateToken(Connection p_con, OAuthUser oAuthUser, String token) throws Exception {
        // commented for DB2OraclePreparedStatement psmtInsert = null;
       
        int insertCount = 0;
        final String methodName = "validateToken";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userVO=");
        	loggerValue.append(oAuthUser);
        	LOG.debug(methodName, loggerValue);
        }
        try {
        	
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT * from user_token ");
            strBuff.append("where token = ? ");
            
            
            String query = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(query);
    			LOG.debug(methodName, loggerValue);
    		}
            try( PreparedStatement  pstmt = p_con.prepareStatement(query);)
            {

           
            	pstmt.setString(1, token);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
            	
            	Calendar calMod = Calendar.getInstance();
            	java.util.Date modifiedTime = rs.getTimestamp("modified_on");
            	calMod.setTime(modifiedTime);
            	calMod.add(Calendar.SECOND, rs.getInt("time_to_live"));
            	Date dMod =  calMod.getTime();
            	
            	Calendar calCur = Calendar.getInstance();
            	Date dCur =  calCur.getTime();
            	
            	
            	
            	
            	
            	
            	if(dCur.before(dMod) == false) {
            		 throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MAPP_TOKEN_EXPIRED);
                }
            	
            	
            	
            	oAuthUser.setReqGatewayType(rs.getString("request_gateway_type"));
            	oAuthUser.setReqGatewayCode(rs.getString("request_gateway_code"));
            	oAuthUser.setReqGatewayLoginId(rs.getString("request_gateway_login_id"));
            	oAuthUser.setServicePort(rs.getString("service_port"));
            	oAuthUser.setReqGatewayPassword(BTSLUtil.decryptText(rs.getString("request_gateway_password")));
            	oAuthUser.setSourceType(rs.getString("source_type"));
            	
            	
            	
            	oAuthUser.getData().setLoginid(rs.getString("login_id"));
            	oAuthUser.getData().setMsisdn(rs.getString("msisdn"));
            	oAuthUser.getData().setExtcode(rs.getString("ext_code"));
            	oAuthUser.getData().setPin(BTSLUtil.decryptText(rs.getString("pin")));
            	oAuthUser.getData().setPassword(BTSLUtil.decryptText(rs.getString("password")));
            	
            	
            	
            }else {
            	
            	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MAPP_INVALID_TOKEN);
                
            }
            }
            
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            
            throw sqle;
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            
            throw e;
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, LOG);
           
        } // end of finally

        
    }
	
    public UserVO getPinPassword(Connection con,String loginId) throws BTSLBaseException {
    	final String methodName = "getPinPassword";
    	UserVO userVO = new UserVO();
    	StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("get():: Entered getPinPassword:");
        	
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
         	PreparedStatement pstmtSelect = null;
      
		try {
			pstmtSelect = userQry.getPinPassword(con, loginId);
        	
        	
            try(ResultSet rs = pstmtSelect.executeQuery();)
            	{
            		if (rs.next()) {
            			userVO.setPassword(rs.getString("password"));
            			userVO.setActiveUserPin(rs.getString("sms_pin"));
            		}
            		
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getUsersInHierachyWithCat]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getUsersInHierachyWithCat]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + userVO);
            }
            
            return userVO;
    }
    
    public UserVO loadUsersDetailsByLoginID(Connection p_con, String loginId) throws BTSLBaseException {
        final String methodName = "loadUsersDetailsByLoginID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: loginId=");
        	loggerValue.append(BTSLUtil.maskParam(loginId));
        	LOG.debug(methodName, loggerValue);
        }
        
       
        UserVO userVO = new UserVO();
        PreparedStatement pstmtSelect = null;
        
        try{
        	pstmtSelect = userQry.loadUserDetailsByLoginId(p_con, loginId);
        	
        
        try(ResultSet rs = pstmtSelect.executeQuery();)
    	{
    		if (rs.next()) {
    			userVO.setLoginID(rs.getString("login_id"));
    			userVO.setMsisdn(rs.getString("msisdn"));
    			userVO.setExternalCode(rs.getString("external_code"));
    			userVO.setStatus(rs.getString("status"));
    			userVO.setUserID(rs.getString("user_id"));
				userVO.setCategoryCode(rs.getString("category_code"));
    			userVO.setNetworkID(rs.getString("network_code"));
                userVO.setUserName(rs.getString("user_name"));
    		}
    		
    	}        
        }
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } 
        return userVO;
    }

    /**
     * 
     * @param p_con
     * @param identifierType
     * @param identifierValue
     * @param pinOrPass
     * @return
     * @throws BTSLBaseException
     */
    public UserVO loadUsersDetailsByidentifierType(Connection p_con, String identifierType, String identifierValue, String pinOrPass, HttpServletRequest req) throws BTSLBaseException {
        final String methodName = "loadUsersDetailsByLoginID";
        StringBuilder loggerValue= new StringBuilder();
        UserVO userVO = null;
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: identifierValue=");
        	loggerValue.append(identifierValue);
        	LOG.debug(methodName, loggerValue);
        }
        
        PreparedStatement pstmtSelect = null;
        
        try{
        	//check ldap 
        	// if yes autheticate
        	// if auth success then set password or pin
        	
			if (identifierType != null && identifierType.equalsIgnoreCase("loginid")) {
				LoginDAO _loginDAO = new LoginDAO();
			
				ChannelUserVO channelUserVO = _loginDAO.loadUserDetails(p_con, identifierValue, pinOrPass,
						BTSLUtil.getBTSLLocale(req));
				if (((channelUserVO.getCategoryVO()).getAuthenticationType()).equalsIgnoreCase(PretupsI.AUTH_TYPE_LDAP)
						&& channelUserVO.getAuthTypeAllowed().equalsIgnoreCase(PretupsI.YES)) {

				      
					//authenticate LDAP
					
					LDAPUtilI ldapUtili = null;
		            try {
		                ldapUtili = (LDAPUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.LDAP_UTIL_CLASS)).newInstance();
		            } catch (Exception e) {
		                LOG.errorTrace("static", e);
		            }
		            
		            if (ldapUtili.authenticateUser(identifierValue, pinOrPass)) {
	                    //LDAP Auth Success
	                	//Now change it with database value
	                    pinOrPass = BTSLUtil.decryptText(channelUserVO.getPassword());
	  	              
	                	
	                } else {
	                    throw new BTSLBaseException("login.ldapauth.error.unauthorize", "index");
	                }
		            
				}
			}
        	pstmtSelect = userQry.loadUserDetailsBydentifierType(p_con, identifierType, identifierValue, pinOrPass);
        	
        
        try(ResultSet rs = pstmtSelect.executeQuery();)
    	{
    		if (rs.next()) {
    			userVO =  new UserVO();
    			userVO.setLoginID(rs.getString(1));
    			userVO.setMsisdn(rs.getString(2));
    			userVO.setExternalCode(rs.getString(3));
    			userVO.setUserID(rs.getString(4));
    			userVO.setValidRequestURLs(rs.getString(5));
    		}
    		
    	}        
        }
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } 
        return userVO;
    }

    
    public void loadUsertransactionalData(Connection p_con, String userId, String transferType, String transferSubType, Date fromDate, Date toDate, Date prevFromDate, Date prevToDate,TransactionalDataResponseVO transactionalDataResponseVO) throws BTSLBaseException {
        final String methodName = "loadUsertransactionalData";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(userId);
        	loggerValue.append("transferType=");
        	loggerValue.append(transferType);
        	loggerValue.append("transferSubType=");
        	loggerValue.append(transferSubType);
        	loggerValue.append("fromDate=");
        	loggerValue.append(fromDate);
        	loggerValue.append("toDate");
        	loggerValue.append(toDate);
        	loggerValue.append("prefromDate=");
        	loggerValue.append(prevFromDate);
        	loggerValue.append("prevtoDate");
        	loggerValue.append(prevToDate);
        	LOG.debug(methodName, loggerValue);
        }
        
   
        PreparedStatement pstmtSelect = null;
		PreparedStatement pstmtSelect1 = null;
		ResultSet rs=null;
		ResultSet rs1=null;
		try {

			String selectQuery = null;
			selectQuery = userQry.loadTransactionData();
			

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}
			
			
				pstmtSelect = p_con.prepareStatement(selectQuery);
				pstmtSelect1 = p_con.prepareStatement(selectQuery);
				int i=0;
				i++;
				pstmtSelect.setString(i, transferType);
				i++;
				pstmtSelect.setString(i, transferSubType);
				i++;
				pstmtSelect.setString(i, userId);
				i++;
				pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
				i++;
				pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
			
				i=0;
				i++;
				pstmtSelect1.setString(i, transferType);
				i++;
				pstmtSelect1.setString(i, transferSubType);
				i++;
				pstmtSelect1.setString(i, userId);
				i++;
				pstmtSelect1.setDate(i, BTSLUtil.getSQLDateFromUtilDate(prevFromDate));
				i++;
				pstmtSelect1.setDate(i, BTSLUtil.getSQLDateFromUtilDate(prevToDate));
				
				rs = pstmtSelect.executeQuery();
				rs1 = pstmtSelect1.executeQuery();
				TransactionalData transactionaldata =  new TransactionalData();
				TransactionalData transactionaldata1 =  new TransactionalData();
				transactionalDataResponseVO.setInTransactionData(transactionaldata);
				transactionalDataResponseVO.setOutTransactionData(transactionaldata1);
				if (rs.next()) {
					 
				
						SimpleDateFormat rdf = new SimpleDateFormat(((String)PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
						transactionalDataResponseVO.getInTransactionData().setFromDate(rdf.format(fromDate));
						transactionalDataResponseVO.getInTransactionData().setToDate(rdf.format(toDate));
						transactionalDataResponseVO.getInTransactionData().setPreviousFromDate(rdf.format(prevFromDate));
						transactionalDataResponseVO.getInTransactionData().setPreviousToDate(rdf.format(prevToDate));
						transactionalDataResponseVO.getInTransactionData().setTotalCount(rs.getString("INCOUNT"));
						transactionalDataResponseVO.getInTransactionData().setTotalValue(PretupsBL.getDisplayAmount((rs.getDouble("INAMOUNT"))));
						transactionalDataResponseVO.getOutTransactionData().setFromDate(rdf.format(fromDate));
						transactionalDataResponseVO.getOutTransactionData().setToDate(rdf.format(toDate));
						transactionalDataResponseVO.getOutTransactionData().setPreviousFromDate(rdf.format(prevFromDate));
						transactionalDataResponseVO.getOutTransactionData().setPreviousToDate(rdf.format(prevToDate));
						transactionalDataResponseVO.getOutTransactionData().setTotalCount(rs.getString("OUTCOUNT"));
						transactionalDataResponseVO.getOutTransactionData().setTotalValue(PretupsBL.getDisplayAmount((rs.getDouble("OUTAMOUNT"))));
				}
						
						if(rs1.next()) {
						transactionalDataResponseVO.getInTransactionData().setLastMonthCount(rs1.getString("INCOUNT"));
						transactionalDataResponseVO.getInTransactionData().setLastMonthValue(PretupsBL.getDisplayAmount((rs1.getDouble("INAMOUNT"))));
						transactionalDataResponseVO.getOutTransactionData().setLastMonthCount(rs1.getString("OUTCOUNT"));
						transactionalDataResponseVO.getOutTransactionData().setLastMonthValue(PretupsBL.getDisplayAmount((rs1.getDouble("OUTAMOUNT"))));
					}
				
				
			
			
		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[" + methodName + "]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[" + methodName + "]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.processing", e);
		} // end of catch
		finally {

			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting totalTrnsVO:" + transactionalDataResponseVO);
			}
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
			
			try{
        		if (rs1!= null){
        			rs1.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
			
        	try{
        		if (pstmtSelect!= null){
        			pstmtSelect.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtSelect1!= null){
        			pstmtSelect1.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
		} // end of finally
    }

    

    public List<PassbookSearchRecordVO> searchPassbookDetailList(java.sql.Connection con, PassbookSearchInputVO passbookSearchInputVO) throws BTSLBaseException {
    	final String methodName = "searchPassbookDetailList";
    	StringBuffer msg=new StringBuffer("");
    	StringBuffer loggerValue=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("searchPassbookDetailList():: Entered with passbookSearchInputVO:");
        	msg.append(passbookSearchInputVO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(passbookSearchInputVO.getUserId());
           	loggerValue.append("fromDate=");
        	loggerValue.append(passbookSearchInputVO.getFromDate());
        	loggerValue.append("toDate");
        	loggerValue.append(passbookSearchInputVO.getToDate());

        	
        	LOG.debug(methodName, loggerValue);
        }
        
     
        PreparedStatement pstmt = null;
        StringBuilder sqlSelect = dailyRptAnalysiQry.searchPassBookDetailsQry(passbookSearchInputVO);
      
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect.toString());
			LOG.debug(methodName, loggerValue);
		}
        List<PassbookSearchRecordVO> listPassbookSearchVo = new ArrayList<>();
        try {
        	pstmt = con.prepareStatement(sqlSelect.toString());
        	int i = 0;
        	if(BTSLUtil.isNullObject(con.getClientInfo().get("ApplicationName"))) {
        	++i;
	     	pstmt.setString(i, passbookSearchInputVO.getUserId());
           	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookSearchInputVO.getFromDate()));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookSearchInputVO.getToDate()));
        	++i;
	     	pstmt.setString(i, passbookSearchInputVO.getUserId());
	         ++i;
	     	pstmt.setString(i, passbookSearchInputVO.getNetworkCode());
	         ++i;
            pstmt.setString(i, passbookSearchInputVO.getProductCode());
            ++i;
            pstmt.setString(i, passbookSearchInputVO.getProductCode());
        	}
        	else {
        		++i;
    	     	pstmt.setString(i, passbookSearchInputVO.getUserId());
                ++i;
                pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookSearchInputVO.getFromDate()));
                ++i;
                pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(passbookSearchInputVO.getToDate()));
                ++i;
                pstmt.setString(i, passbookSearchInputVO.getUserId());
                ++i;
                pstmt.setString(i, passbookSearchInputVO.getNetworkCode());
                ++i;
                pstmt.setString(i, passbookSearchInputVO.getProductCode());
                ++i;
                pstmt.setString(i, passbookSearchInputVO.getProductCode());

            }
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            		while (rs.next()) {
            			PassbookSearchRecordVO passbookSearchRecordVO = new PassbookSearchRecordVO();
            			passbookSearchRecordVO.setTransDate(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT))));
            			passbookSearchRecordVO.setProductName(rs.getString("product_name")); 
            			passbookSearchRecordVO.setUserName(rs.getString("user_name"));
            			passbookSearchRecordVO.setUserMobilenumber(rs.getString("msisdn"));
            			passbookSearchRecordVO.setUserCategory(rs.getString("USERCATEGORY"));
            			passbookSearchRecordVO.setUserGeography(rs.getString("USEGEOGRPHY"));
            			//passbookSearchRecordVO.setExternalCode(rs.getString("externalcode"));
            			passbookSearchRecordVO.setParentName(rs.getString("parent_name"));
            			passbookSearchRecordVO.setParentMobilenumber(rs.getString("parent_msisdn"));
            			passbookSearchRecordVO.setParentCategory(rs.getString("parentcategoryName"));
            			passbookSearchRecordVO.setParentGeography(rs.getString("ParentGeography"));
            			passbookSearchRecordVO.setOwnerName(rs.getString("ownerName"));
            			passbookSearchRecordVO.setOwnerGeography(rs.getString("owner_geo"));
            			passbookSearchRecordVO.setOwnerCategory(rs.getString("ownercategoryName"));
            			passbookSearchRecordVO.setOwnerMobileNumber(rs.getString("ownermsisdn"));
            			passbookSearchRecordVO.setO2cTransferCount(String.valueOf(PretupsBL
             						.getDisplayAmount(rs.getLong("o2cTransferCount"))));
            			passbookSearchRecordVO.setO2cTransferAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cTransferAmount"))));
            			passbookSearchRecordVO.setO2cReturnCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cReturnCount"))));
            			passbookSearchRecordVO.setO2cReturnAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cReturnAmount"))));
            			passbookSearchRecordVO.setO2cWithdrawCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cWithdrawCount"))));
            			passbookSearchRecordVO.setO2cWithdrawAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cWithdrawAmount"))));	
            			passbookSearchRecordVO.setO2cWithdrawAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cWithdrawAmount"))));	
            			passbookSearchRecordVO.setC2cTransfer_InCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_InCount"))));
            			passbookSearchRecordVO.setC2cTransfer_InAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_InAmount"))));
            			passbookSearchRecordVO.setC2cTransfer_OutCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_OutCount"))));
            			passbookSearchRecordVO.setC2cTransfer_OutAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_OutAmount"))));
            			passbookSearchRecordVO.setC2cTransfer_OutAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_OutAmount"))));
            			passbookSearchRecordVO.setC2cTransferRet_InCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferRet_InCount"))));
            			passbookSearchRecordVO.setC2cTransferRet_InAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferRet_InAmount"))));
            			passbookSearchRecordVO.setC2cTransferRet_OutCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferRet_OutCount"))));
            			passbookSearchRecordVO.setC2cTransferRet_OutAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferRet_OutAmount"))));
            			passbookSearchRecordVO.setC2cTransferWithdraw_InCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferWithdraw_InCount"))));
            			passbookSearchRecordVO.setC2cTransferWithdraw_InAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferWithdraw_InAmount"))));
            			passbookSearchRecordVO.setC2cTransferWithdraw_OutAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2c_withdraw_out_amount"))));
            			passbookSearchRecordVO.setC2sTransfer_amount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2s_transfer_out_amount"))));
            			passbookSearchRecordVO.setC2sTransfer_count(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2s_transfer_out_COUNT"))));
            			passbookSearchRecordVO.setOpeningBalance(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("opening_balance"))));
            			passbookSearchRecordVO.setClosingBalance(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("closing_balance"))));
            			passbookSearchRecordVO.setAdditionalcommissionAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("COMMISSION"))));
            			//(opening balance + stock bought - stock return- channel transfers + channel return - C2S transfer)		
            		Long calculateReconStatus = rs.getLong("opening_balance") + 
            				rs.getLong("o2cTransferAmount") - 
            				(rs.getLong("o2cReturnAmount") + rs.getLong("o2cWithdrawAmount")) - 
            				(rs.getLong("c2cTransfer_OutAmount") + rs.getLong("c2cTransferWithdraw_OutCount") + rs.getLong("c2cTransferRet_OutAmount"))+
            				(rs.getLong("c2cTransferWithdraw_InAmount")  + rs.getLong("c2cTransferRet_InAmount") + rs.getLong("c2cTransfer_InAmount") )
            				-rs.getLong("c2s_transfer_out_amount"); 
            		
            		 if(rs.getLong("closing_balance")==calculateReconStatus) {
            			 passbookSearchRecordVO.setReconStatus("N"); 
            		 }else {
            			 passbookSearchRecordVO.setReconStatus("Y");
            		 }
            			
            			listPassbookSearchVo.add(passbookSearchRecordVO);
            		}
            	}
            OracleUtil.closeQuietly(pstmt);
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + listPassbookSearchVo);
            }
        }    
            return listPassbookSearchVo;
    }
    
    
    
    /**this method returns list of lowthreshold details
  	 * @param con
  	 * @param lowThreshHoldReportDTO
  	 * @return List<LowThreshHoldRecordVO>
  	 * @throws BTSLBaseException
  	 */
    public List<LowThreshHoldRecordVO> getLowThreshHoltRptDetails(java.sql.Connection con, LowThreshHoldReportDTO lowThreshHoldReportDTO) throws BTSLBaseException {
    	final String methodName = "getLowThreshHoltRptDetails";
    	final String datePattern="report.datetimeformat";
    	StringBuffer msg=new StringBuffer("");
    	StringBuffer loggerValue=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("getLowThreshHoltRptDetails():: Entered with lowThreshHoldReportDTO:");
        	msg.append(lowThreshHoldReportDTO);
        	
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(lowThreshHoldReportDTO.getUserID());
           	loggerValue.append("fromDate=");
        	loggerValue.append(lowThreshHoldReportDTO.getFromDate());
        	loggerValue.append("toDate");
        	loggerValue.append(lowThreshHoldReportDTO.getToDate());

        	
        	LOG.debug(methodName, loggerValue);
        }
        
     
        PreparedStatement pstmt = null;
        String sqlSelect = userQry.getLowthreshHoldReportQry(lowThreshHoldReportDTO);
      
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        List<LowThreshHoldRecordVO> listLowThreshHoldRecordVO = new ArrayList<>();
        try {
        	pstmt = con.prepareStatement(sqlSelect.toString());
        	int i = 0;
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(lowThreshHoldReportDTO.getFromDate() + PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues())));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(lowThreshHoldReportDTO.getToDate() + PretupsRptUIConsts.REPORT_TO_TIME.getReportValues())));
        	++i;
        	pstmt.setString(i, lowThreshHoldReportDTO.getExtnwcode());
        	
/*
        	++i;
        	pstmt.setString(i, PretupsI.ALL);

        	++i;
        	pstmt.setString(i, PretupsI.ALL); */
        	++i;
           	pstmt.setString(i, lowThreshHoldReportDTO.getUserID());
            
        	
        	++i;
        	pstmt.setString(i, lowThreshHoldReportDTO.getThreshhold());
        	
        	++i;
        	pstmt.setString(i, lowThreshHoldReportDTO.getThreshhold());
        	
    		 ++i;
         	pstmt.setString(i, lowThreshHoldReportDTO.getCategory());
         	 ++i;
          	pstmt.setString(i, lowThreshHoldReportDTO.getCategory());
          	 ++i;
         	pstmt.setString(i, lowThreshHoldReportDTO.getDomain());
          	 ++i;
          	pstmt.setString(i, lowThreshHoldReportDTO.getGeography());
          	 ++i;
           	pstmt.setString(i, lowThreshHoldReportDTO.getGeography());
            ++i;
           	pstmt.setString(i, lowThreshHoldReportDTO.getUserID());
               	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	
            		while (rs.next()) {
            			
            			LowThreshHoldRecordVO lowThreshHoldRecordVO = new LowThreshHoldRecordVO();
            			lowThreshHoldRecordVO.setUserName(rs.getString("USER_NAME"));
            			lowThreshHoldRecordVO.setMobileNumber(rs.getString("MSISDN"));
            			lowThreshHoldRecordVO.setUserStatus(rs.getString("USERS_TATUS"));
            			String dateformat =BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("ENTRY_DATE_TIME") + " " +rs.getTime("ENTRY_DATE_TIME")));
            			lowThreshHoldRecordVO.setDateTime(dateformat);  
            			lowThreshHoldRecordVO.setTransactionID(rs.getString("TRANSACTION_ID"));
            			lowThreshHoldRecordVO.setTransferType(rs.getString("TRANSFER_TYPE"));
            			lowThreshHoldRecordVO.setCategoryName(rs.getString("CATEGORY_NAME"));
            			lowThreshHoldRecordVO.setProductName(rs.getString("PRODUCT_NAME"));
            			   long previousBalance = rs.getLong("PREVIOUS_BALANCE");

            			    if (previousBalance!= 0 && !rs.wasNull()) {
            			    	lowThreshHoldRecordVO.setPreviousBalance( String.valueOf(PretupsBL
                						.getDisplayAmount(rs.getLong("PREVIOUS_BALANCE"))));
           			    } else {
            			    	lowThreshHoldRecordVO.setPreviousBalance("0");           			    
            		             }
            		
            			    long currentBalance = rs.getLong("CURRENT_BALANCE");
            			   
            			    if (currentBalance!= 0 && !rs.wasNull()) {
            			    	lowThreshHoldRecordVO.setCurrentBalance(String.valueOf(PretupsBL
                						.getDisplayAmount(rs.getLong("CURRENT_BALANCE"))));
            			    }else {
            			    	lowThreshHoldRecordVO.setCurrentBalance("0"); 	
            			    } 
            			    lowThreshHoldRecordVO.setThreshHold(rs.getString("THRSHOLD_TYPE"));
            			    lowThreshHoldRecordVO.setThresholdValue(String.valueOf(PretupsBL
            						.getDisplayAmount(rs.getLong("THRESHOLD_VALUE"))));
            			    
            			listLowThreshHoldRecordVO.add(lowThreshHoldRecordVO);
            		}
            		
            	}
            OracleUtil.closeQuietly(pstmt);
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + listLowThreshHoldRecordVO);
            }
            
            return listLowThreshHoldRecordVO;
    }
    

    
    /**this method returns list of Pin password history details
  	 * @param con
  	 * @param pinPassHistoryReqDTO
  	 * @return List<PinPassHistSearchRecordVO>
  	 * @throws BTSLBaseException
  	 */
    public List<PinPassHistSearchRecordVO> getPinPassHistDetails(java.sql.Connection con, PinPassHistoryReqDTO pinPassHistoryReqDTO) throws BTSLBaseException {
    	final String methodName = "getPinPassHistDetails";
    	StringBuffer msg=new StringBuffer("");
    	StringBuffer loggerValue=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
        	msg.append("getPinPassHistDetails():: Entered with pinPassHistoryReqDTO:");
        	msg.append(pinPassHistoryReqDTO);
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
        
        
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(pinPassHistoryReqDTO.getUserId());
           	loggerValue.append("fromDate=");
        	loggerValue.append(pinPassHistoryReqDTO.getFromDate());
        	loggerValue.append("toDate");
        	loggerValue.append(pinPassHistoryReqDTO.getToDate());
        	LOG.debug(methodName, loggerValue);
        }
        
     
        PreparedStatement pstmt = null;
        String sqlSelect = userQry.getPinPassHistReportQry(pinPassHistoryReqDTO);
      
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        List<PinPassHistSearchRecordVO> listPinPassHistorySearchRecordVO = new ArrayList<>();
        try {
        	pstmt = con.prepareStatement(sqlSelect.toString());
        	int i = 0;
        	
        	
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(pinPassHistoryReqDTO.getFromDate() + PretupsRptUIConsts.REPORT_FROM_TIME )));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(pinPassHistoryReqDTO.getToDate() + PretupsRptUIConsts.REPORT_TO_TIME)));
        	
        	++i;
        	pstmt.setString(i, pinPassHistoryReqDTO.getExtnwcode());
        	
        	++i;
           	pstmt.setString(i, pinPassHistoryReqDTO.getUserId());
            
        	++i;
        	pstmt.setString(i, pinPassHistoryReqDTO.getUserType());
        	
        	++i;
        	pstmt.setString(i, pinPassHistoryReqDTO.getUserType());
        	
        	++i;
        	pstmt.setString(i, pinPassHistoryReqDTO.getReqType());
        	
        	++i;
         	pstmt.setString(i, pinPassHistoryReqDTO.getCategoryCode());
         	 ++i;
          	pstmt.setString(i, pinPassHistoryReqDTO.getCategoryCode());
          	 ++i;
         	pstmt.setString(i, pinPassHistoryReqDTO.getDomain());
            ++i;
           	pstmt.setString(i, pinPassHistoryReqDTO.getUserId());
               	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	
            		while (rs.next()) {
            			
            			PinPassHistSearchRecordVO pinPassHistSearchRecordVO = new PinPassHistSearchRecordVO();
            			pinPassHistSearchRecordVO.setUserName(rs.getString("USER_NAME"));
            			pinPassHistSearchRecordVO.setMsisdnOrLoginID(rs.getString("MSISDN_OR_LOGINID"));
            			pinPassHistSearchRecordVO.setModifiedOn(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("MODIFIED_ON"), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT)))));
            			pinPassHistSearchRecordVO.setMoidifiedBy(rs.getString("MODIFIED_BY"));
            			listPinPassHistorySearchRecordVO.add(pinPassHistSearchRecordVO);
            		}
            		
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getPinPassHistDetails]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getPinPassHistDetails]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + listPinPassHistorySearchRecordVO);
            }
            
            return listPinPassHistorySearchRecordVO;
    }
    
    
    
    public List<HashMap<String, String>> fetchData(Connection p_con,String tableName, String searchCriteria, String columns)throws BTSLBaseException
    {
    	final String methodName = "fetchData";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: searchCriteria=");
        	loggerValue.append(searchCriteria);
        	loggerValue.append(",columnvalues=");
        	loggerValue.append(columns);
        	LOG.debug(methodName, loggerValue);
        }

/*        String search= "";
    	
        if(searchCriteria != null) {
    	Set<String> keys = searchCriteria.keySet();
    	for(String key: keys) {
    		search = " "+search + " "+key+" "+searchCriteria.get(key)+" AND";
    	}
        }
        
        if(search!= null && search.trim().length() > 0 && search.endsWith("AND")) {
    		search = search.substring(0, search.lastIndexOf("AND"));
    		
    	}
    	*/
        
        
    	
        List<HashMap<String, String>> columnValuesList = new ArrayList<HashMap<String, String>>();
        
    	
    	String[] columnsArr = columns.split(",");
    	
    
    	
		try {
			StringBuilder sbf = null ;

			if(searchCriteria == null || searchCriteria.trim().equalsIgnoreCase("()")) {
				 sbf = new StringBuilder(
						"SELECT " + columns + " from " + tableName );
	
			}else {
				 sbf = new StringBuilder(
						"SELECT " + columns + " from " + tableName + " WHERE " + searchCriteria + " ");
				
			}
			String sqlSelect = sbf.toString();

			System.out.println("sqlSelect "+sqlSelect);
			try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {

				try (ResultSet rs = pstmtSelect.executeQuery();) {
					while (rs.next()) {
						HashMap<String, String> columnValues = new HashMap<String, String>();
						for(int i = 0; i < columnsArr.length ; i++) {
							
							columnValues.put(columnsArr[i], rs.getString(columnsArr[i]));
						}
						
						columnValuesList.add(columnValues);
					}
						
				}
			}
		}
    	catch (SQLException e) 
    	{
    		LOG.errorTrace(methodName,e);
    		throw new BTSLBaseException(this,methodName, "error.general.sql.processing",e);
		}
    	catch (Exception ex)
		{
    		loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserDAO["+methodName+"]","","","","Exception:"+ex.getMessage());
		   throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		}
		finally{
			LOG.debug(methodName, "inside finally");
		}
    	return columnValuesList;
    }

    
    public GetParentOwnerProfileRespVO getParentOwnerProfileInfo(java.sql.Connection con, GetParentOwnerProfileReq getParentOwnerProfileReq) throws BTSLBaseException {
    	final String methodName = "getParentOwnerProfileInfo";
    	StringBuffer msg=new StringBuffer("");
    	StringBuffer loggerValue=new StringBuffer("");
    	GetParentOwnerProfileRespVO getParentOwnerProfileRespVO =null;
        if (LOG.isDebugEnabled())
        {
        	msg.append("getParentOwnerProfileReq():: Entered with getParentOwnerProfileReq:");
        	msg.append(getParentOwnerProfileReq.toString());
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
     
        PreparedStatement pstmt = null;
        String sqlSelect = userQry.getParentOwnerInfo();
      
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect.toString());
			LOG.debug(methodName, loggerValue);
		}
        
        try {
        	pstmt = con.prepareStatement(sqlSelect.toString());
        	int i = 0;
         	++i;
        	pstmt.setString(i, getParentOwnerProfileReq.getUserId());
        	++i;
        	pstmt.setString(i, getParentOwnerProfileReq.getUserId());
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	
            		if(rs.next()) {
            			getParentOwnerProfileRespVO=new GetParentOwnerProfileRespVO();
            			getParentOwnerProfileRespVO.setUserName(rs.getString("USER_NAME"));
            			getParentOwnerProfileRespVO.setStatus(rs.getString("status"));
            			getParentOwnerProfileRespVO.setMsisdn(rs.getString("msisdn"));
            			getParentOwnerProfileRespVO.setGrade(rs.getString("grade"));
            			getParentOwnerProfileRespVO.setEmailID(rs.getString("emailID"));
            			
                       	StringBuilder sb = new StringBuilder();
                      	 if(!BTSLUtil.isNullString(rs.getString("address1")) ) {
                      		sb.append(rs.getString("address1"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                      	if(!BTSLUtil.isNullString(rs.getString("address2")) ) {
                      		sb.append(rs.getString("address2"));
                      		sb.append(PretupsI.COMMA);
                      	  }

                   	if(!BTSLUtil.isNullString(rs.getString("city")) ) {
                      		sb.append(rs.getString("city"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	if(!BTSLUtil.isNullString(rs.getString("state")) ) {
                      		sb.append(rs.getString("state"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	if(!BTSLUtil.isNullString(rs.getString("country")) ) {
                      		sb.append(rs.getString("country"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	String address="";
                	if(sb.toString().length()>0) {
    	              	int lastloc =sb.toString().length()-1;
    	               	 address = sb.deleteCharAt(lastloc).toString();
                	}
                   	
            			getParentOwnerProfileRespVO.setAddress(address);
            			getParentOwnerProfileRespVO.setParentName(rs.getString("parent_name"));
            			getParentOwnerProfileRespVO.setParentUserID(rs.getString("PARENTUSERID"));
            			getParentOwnerProfileRespVO.setParentMobileNumber(rs.getString("parent_msisdn"));
            			getParentOwnerProfileRespVO.setParentCategoryName(rs.getString("Parent_category_name"));
            			getParentOwnerProfileRespVO.setOwnerName(rs.getString("owner_name"));
            			getParentOwnerProfileRespVO.setOwnerMobileNumber(rs.getString("owner_msisdn"));
            			getParentOwnerProfileRespVO.setOwnerCategoryName(rs.getString("Owner_Category"));
            			getParentOwnerProfileRespVO.setUserNamePrefix(rs.getString("USER_NAME_PREFIX"));
            			getParentOwnerProfileRespVO.setShortName(rs.getString("SHORT_NAME"));
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "GetParentOwnerProfileRespVO");
            }
            
            return getParentOwnerProfileRespVO;
    }  
    public GetParentOwnerProfileRespVO getParentOwnerProfileInfoForAllUsers(java.sql.Connection con, GetParentOwnerProfileReq getParentOwnerProfileReq) throws BTSLBaseException {
    	final String methodName = "getParentOwnerProfileInfo";
    	StringBuffer msg=new StringBuffer("");
    	StringBuffer loggerValue=new StringBuffer("");
    	GetParentOwnerProfileRespVO getParentOwnerProfileRespVO =null;
        if (LOG.isDebugEnabled())
        {
        	msg.append("getParentOwnerProfileReq():: Entered with getParentOwnerProfileReq:");
        	msg.append(getParentOwnerProfileReq.toString());
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
     
        PreparedStatement pstmt = null;
        String sqlSelect = userQry.getParentOwnerInfoForAllUsers();
      
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect.toString());
			LOG.debug(methodName, loggerValue);
		}
        
        try {
        	pstmt = con.prepareStatement(sqlSelect.toString());
        	int i = 0;
         	++i;
        	pstmt.setString(i, getParentOwnerProfileReq.getUserId());
        	++i;
        	pstmt.setString(i, getParentOwnerProfileReq.getUserId());
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	
            		if (rs.next()) {
            			getParentOwnerProfileRespVO=new GetParentOwnerProfileRespVO();
            			getParentOwnerProfileRespVO.setUserName(rs.getString("USER_NAME"));
            			getParentOwnerProfileRespVO.setStatus(rs.getString("status"));
            			getParentOwnerProfileRespVO.setMsisdn(rs.getString("msisdn"));
            		//	getParentOwnerProfileRespVO.setGrade(rs.getString("grade"));
            			getParentOwnerProfileRespVO.setEmailID(rs.getString("emailID"));
            			
                       	StringBuilder sb = new StringBuilder();
                      	 if(!BTSLUtil.isNullString(rs.getString("address1")) ) {
                      		sb.append(rs.getString("address1"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                      	if(!BTSLUtil.isNullString(rs.getString("address2")) ) {
                      		sb.append(rs.getString("address2"));
                      		sb.append(PretupsI.COMMA);
                      	  }

                   	if(!BTSLUtil.isNullString(rs.getString("city")) ) {
                      		sb.append(rs.getString("city"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	if(!BTSLUtil.isNullString(rs.getString("state")) ) {
                      		sb.append(rs.getString("state"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	if(!BTSLUtil.isNullString(rs.getString("country")) ) {
                      		sb.append(rs.getString("country"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	String address="";
                	if(sb.toString().length()>0) {
    	              	int lastloc =sb.toString().length()-1;
    	               	 address = sb.deleteCharAt(lastloc).toString();
                	}
                   	
            			
            			
            			getParentOwnerProfileRespVO.setAddress(address);
            			getParentOwnerProfileRespVO.setParentName(rs.getString("parent_name"));
            			getParentOwnerProfileRespVO.setParentUserID(rs.getString("PARENTUSERID"));
            			getParentOwnerProfileRespVO.setParentMobileNumber(rs.getString("parent_msisdn"));
            			getParentOwnerProfileRespVO.setParentCategoryName(rs.getString("Parent_category_name"));
            			getParentOwnerProfileRespVO.setOwnerName(rs.getString("owner_name"));
            			getParentOwnerProfileRespVO.setOwnerMobileNumber(rs.getString("owner_msisdn"));
            			getParentOwnerProfileRespVO.setOwnerCategoryName(rs.getString("Owner_Category"));
            			getParentOwnerProfileRespVO.setUserNamePrefix(rs.getString("USER_NAME_PREFIX"));
            			getParentOwnerProfileRespVO.setShortName(rs.getString("SHORT_NAME"));
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "GetParentOwnerProfileRespVO");
            }
            
            return getParentOwnerProfileRespVO;
    }  
public ChannelUserVO loadUserDetailsCompletelyByMsisdn(Connection p_con, String p_msisdn) throws BTSLBaseException {
    final String methodName = "loadUserDetailsCompletelyByMsisdn";
    StringBuilder loggerValue= new StringBuilder();
    if (LOG.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("Entered: p_msisdn=");
    	loggerValue.append(p_msisdn);
    	LOG.debug(methodName, loggerValue);
    }
    ChannelUserVO channelUserVO = null;
    UserPhoneVO phoneVO=null;
   
    try {           
        String selectQuery = userQry.loadUserDetailsCompletelyByMsisdnQry();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
        {
        pstmtSelect.setString(1, p_msisdn);
        pstmtSelect.setString(2, PretupsI.USER_STATUS_TYPE);
        try( ResultSet rs = pstmtSelect.executeQuery();)
        {
        if (rs.next()) {
            channelUserVO = ChannelUserVO.getInstance();
            channelUserVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
            channelUserVO.setActiveUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
            channelUserVO.setCreationType(SqlParameterEncoder.encodeParams(rs.getString("creation_type")));
            channelUserVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
            channelUserVO.setNetworkID(SqlParameterEncoder.encodeParams(rs.getString("network_code")));
            channelUserVO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
            channelUserVO.setPassword(SqlParameterEncoder.encodeParams(rs.getString("password")));
            channelUserVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
            channelUserVO.setParentID(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
            channelUserVO.setOwnerID(SqlParameterEncoder.encodeParams(rs.getString("owner_id")));
            channelUserVO.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("msisdn")));
            channelUserVO.setPinRequired(SqlParameterEncoder.encodeParams(rs.getString("pin_required")));
            channelUserVO.setSmsPin(SqlParameterEncoder.encodeParams(rs.getString("sms_pin")));
            channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
            channelUserVO.setEmpCode(SqlParameterEncoder.encodeParams(rs.getString("employee_code")));
            channelUserVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("userstatus")));
            channelUserVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
            channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
            channelUserVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
            channelUserVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
            channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
            channelUserVO.setContactPerson(SqlParameterEncoder.encodeParams(rs.getString("contact_person")));
            channelUserVO.setContactNo(SqlParameterEncoder.encodeParams(rs.getString("contact_no")));
            channelUserVO.setDesignation(SqlParameterEncoder.encodeParams(rs.getString("designation")));
            channelUserVO.setDivisionCode(SqlParameterEncoder.encodeParams(rs.getString("division")));
            channelUserVO.setDepartmentCode(SqlParameterEncoder.encodeParams(rs.getString("department")));
            channelUserVO.setUserType(SqlParameterEncoder.encodeParams(rs.getString("user_type")));
            channelUserVO.setInSuspend(SqlParameterEncoder.encodeParams(rs.getString("in_suspend")));
            channelUserVO.setOutSuspened(SqlParameterEncoder.encodeParams(rs.getString("out_suspend")));
            channelUserVO.setAddress1(SqlParameterEncoder.encodeParams(rs.getString("address1")));
            channelUserVO.setAddress2(SqlParameterEncoder.encodeParams(rs.getString("address2")));
            channelUserVO.setCity(SqlParameterEncoder.encodeParams(rs.getString("city")));
            channelUserVO.setState(SqlParameterEncoder.encodeParams(rs.getString("state")));
            channelUserVO.setCountry(SqlParameterEncoder.encodeParams(rs.getString("country")));
           
            try {
                channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
            } catch (Exception e) {
                channelUserVO.setPasswordModifiedOn(null);
            }
            try {
            	 channelUserVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
            } catch (Exception e) {
                channelUserVO.setPasswordModifiedOn(null);
            }
           
         
            // Added for RSA Authentication
            channelUserVO.setRsaFlag(SqlParameterEncoder.encodeParams(rs.getString("rsaflag")));
            channelUserVO.setSsn(SqlParameterEncoder.encodeParams(rs.getString("ssn")));
            channelUserVO.setUserNamePrefix(SqlParameterEncoder.encodeParams(rs.getString("user_name_prefix")));
            channelUserVO.setExternalCode(SqlParameterEncoder.encodeParams(rs.getString("external_code")));
            channelUserVO.setUserCode(SqlParameterEncoder.encodeParams(rs.getString("user_code")));
            channelUserVO.setShortName(SqlParameterEncoder.encodeParams(rs.getString("short_name")));
            channelUserVO.setReferenceID(SqlParameterEncoder.encodeParams(rs.getString("reference_id")));
            channelUserVO.setDomainID(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
            channelUserVO.setEmail(SqlParameterEncoder.encodeParams(rs.getString("email")));
            // Added by deepika aggarwal
            channelUserVO.setCompany(SqlParameterEncoder.encodeParams(rs.getString("company")));
            channelUserVO.setFax(SqlParameterEncoder.encodeParams(rs.getString("fax")));
            channelUserVO.setFirstName(SqlParameterEncoder.encodeParams(rs.getString("firstname")));
            channelUserVO.setLastName(SqlParameterEncoder.encodeParams(rs.getString("lastname")));
            // end added by deepika aggarwal
            channelUserVO.setAllowedIps(SqlParameterEncoder.encodeParams(rs.getString("allowed_ip")));
            channelUserVO.setAllowedDays(SqlParameterEncoder.encodeParams(rs.getString("allowed_days")));
            channelUserVO.setAppointmentDate(rs.getDate("appointment_date"));
            channelUserVO.setFromTime(SqlParameterEncoder.encodeParams(rs.getString("from_time")));
            channelUserVO.setToTime(SqlParameterEncoder.encodeParams(rs.getString("to_time")));

            channelUserVO.setTransferProfileID(SqlParameterEncoder.encodeParams(rs.getString("transfer_profile_id")));
            channelUserVO.setCommissionProfileSetID(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_id")));
            channelUserVO.setUserGrade(SqlParameterEncoder.encodeParams(rs.getString("user_grade")));

            // for Zebra and Tango by sanjeew date 06/07/07
            channelUserVO.setApplicationID(SqlParameterEncoder.encodeParams(rs.getString("application_id")));
            channelUserVO.setMpayProfileID(SqlParameterEncoder.encodeParams(rs.getString("mpay_profile_id")));
            channelUserVO.setUserProfileID(SqlParameterEncoder.encodeParams(rs.getString("user_profile_id")));
            channelUserVO.setMcommerceServiceAllow(SqlParameterEncoder.encodeParams(rs.getString("mcommerce_service_allow")));
            // End Zebra and Tango
            channelUserVO.setPrefixId(rs.getLong("PREFIX_ID"));
            channelUserVO.setStatusDesc(SqlParameterEncoder.encodeParams(rs.getString("lookup_name")));
            channelUserVO.setPhoneProfile(SqlParameterEncoder.encodeParams(rs.getString(PHONE_PROFILE)));
            channelUserVO.setPinRequired(SqlParameterEncoder.encodeParams(rs.getString("pin_required")));
            channelUserVO.setUserPhonesId(SqlParameterEncoder.encodeParams(rs.getString("user_phones_id")));
            CategoryVO categoryVO = CategoryVO.getInstance();
            categoryVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
            categoryVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
            categoryVO.setDomainCodeforCategory(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
            categoryVO.setSequenceNumber(rs.getInt("catseq"));
            categoryVO.setGrphDomainType(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type")));
            categoryVO.setSmsInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("sms_interface_allowed")));
            categoryVO.setWebInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("web_interface_allowed")));
            categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
            categoryVO.setHierarchyAllowed(SqlParameterEncoder.encodeParams(rs.getString("hierarchy_allowed")));
            categoryVO.setAgentAllowed(SqlParameterEncoder.encodeParams(rs.getString("agent_allowed")));
            categoryVO.setCategoryType(SqlParameterEncoder.encodeParams(rs.getString("category_type")));
            categoryVO.setRestrictedMsisdns(SqlParameterEncoder.encodeParams(rs.getString("restricted_msisdns")));
            categoryVO.setTransferToListOnly(SqlParameterEncoder.encodeParams(rs.getString("transfertolistonly")));
            channelUserVO.setCategoryVO(categoryVO);
            channelUserVO.setGeographicalCode(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_code")));
            //created userphonevo object
            phoneVO = new UserPhoneVO();
            phoneVO.setUserPhonesId(rs.getString("user_phones_id"));
            phoneVO.setMsisdn(rs.getString("msisdn"));
            phoneVO.setUserId(rs.getString("user_id"));
            phoneVO.setDescription(rs.getString("description"));
            phoneVO.setPrimaryNumber(rs.getString("primary_number"));
            phoneVO.setSmsPin(rs.getString("sms_pin"));
            if (!BTSLUtil.isNullString(phoneVO.getSmsPin())) {
                // set the default value *****
                phoneVO.setShowSmsPin(BTSLUtil.getDefaultPasswordNumeric(phoneVO.getSmsPin()));
                phoneVO.setConfirmSmsPin(BTSLUtil.getDefaultPasswordNumeric(phoneVO.getSmsPin()));
            }
            phoneVO.setPinRequired(rs.getString("pin_required"));
            phoneVO.setPhoneProfile(rs.getString(PHONE_PROFILE));
            phoneVO.setPhoneLanguage(rs.getString("phone_language"));
            phoneVO.setCountry(rs.getString("country"));
            phoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
            phoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
            phoneVO.setLastTransactionOn(rs.getTimestamp("last_transaction_on"));
            try {
            	phoneVO.setPinModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pin_modified_on")));
            } catch (Exception e) {
                channelUserVO.setPasswordModifiedOn(null);
            } 
            phoneVO.setCreatedBy(rs.getString("created_by"));
            phoneVO.setCreatedOn(rs.getTimestamp("created_on"));
            phoneVO.setModifiedBy(rs.getString("modified_by"));
            phoneVO.setModifiedOn(rs.getTimestamp("modified_on"));
            phoneVO.setFirstInvalidPinTime(rs.getTimestamp("first_invalid_pin_time"));
            phoneVO.setLastTransferID(rs.getString("last_transfer_id"));
            phoneVO.setLastTransferType(rs.getString("last_transfer_type"));
            phoneVO.setPinReset(rs.getString("pin_reset"));
            channelUserVO.setUserPhoneVO(phoneVO);
             }
        return channelUserVO;
    }
        }
    }// end of try
    catch (SQLException sqle) {
    	loggerValue.setLength(0);
		loggerValue.append(SQL_EXCEPTION);
		loggerValue.append(sqle.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, sqle);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByMsisdn]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
    }// end of catch
    catch (Exception e) {
    	loggerValue.setLength(0);
		loggerValue.append(EXCEPTION);
		loggerValue.append(e.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsByMsisdn]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, methodName, "error.general.processing",e);
    }// end of catch
    finally {
    	
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting channelUserVO:" + channelUserVO);
        }
    }// end of finally
  }



public ChildUserVO checkUserUnderLoggedInUserCategory(Connection p_con,String loggedInUserID,String childUserID) throws BTSLBaseException {
    final String methodName = "checkUserUnderLoggedInUserCategory";
    StringBuilder loggerValue= new StringBuilder();
    if (LOG.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("Entered: loggedINUserID=");
    	loggerValue.append(loggedInUserID);
    	loggerValue.append("Entered: childUserID=");
    	loggerValue.append(childUserID);
    	LOG.debug(methodName, loggerValue);
    }
    ChildUserVO childUserVO = null;
    
    
   
    try {           
        String selectQuery = userQry.checkChildUserUnderLoggedInUserQry();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
        {
        pstmtSelect.setString(1, childUserID);
        pstmtSelect.setString(2, loggedInUserID);
        try( ResultSet rs = pstmtSelect.executeQuery();)
        {
        if (rs.next()) {
        	childUserVO = childUserVO.getInstance();
        	childUserVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
        	childUserVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("USER_name")));
        	childUserVO.setUserCategory(SqlParameterEncoder.encodeParams(rs.getString("CATEGORY_CODE")));
        	childUserVO.setParentUserID(rs.getString("parent_id"));
        	childUserVO.setParentUserID(rs.getString("owner_id"));         }
        return childUserVO;
    }
        }
    }// end of try
    catch (SQLException sqle) {
    	loggerValue.setLength(0);
		loggerValue.append(SQL_EXCEPTION);
		loggerValue.append(sqle.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, sqle);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkUserUnderLoggedInUserCategory]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
    }// end of catch
    catch (Exception e) {
    	loggerValue.setLength(0);
		loggerValue.append(EXCEPTION);
		loggerValue.append(e.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkUserUnderLoggedInUserCategory]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, methodName, "error.general.processing",e);
    }// end of catch
    finally {
    	
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting childUserVO:" + childUserVO);
        }
    }// end of finally
	
	
	
}

public ArrayList loadOwnerUserList(Connection p_con, String parentGraphDomainCode,String ownerName, String domainCode, String statusUsed, String status) throws BTSLBaseException {
    final String methodName = "loadOwnerUserList";
    if (LOG.isDebugEnabled()) {
    	StringBuffer msg=new StringBuffer("");
    	msg.append("Entered  p_parentGraphDomainCode= ");
    	msg.append(parentGraphDomainCode);
    	msg.append(", ownerName= ");
    	msg.append(ownerName);
    	msg.append(", p_domainCode= ");
    	msg.append(domainCode);
    	msg.append(", p_stausUsed= ");
    	msg.append(statusUsed);        	
    	msg.append(", p_staus= ");
    	msg.append(status);
    	
    	String message=msg.toString();
    	LOG.debug(methodName,message);
    }
    // commented for DB2OraclePreparedStatement pstmt = null;
    String p_parentGraphDomainCode = SqlParameterEncoder.encodeParams(parentGraphDomainCode);
    String p_ownerName = SqlParameterEncoder.encodeParams(ownerName);
    String p_domainCode = SqlParameterEncoder.encodeParams(domainCode);
    String p_statusUsed = SqlParameterEncoder.encodeParams(statusUsed);
    String p_status = SqlParameterEncoder.encodeParams(status);
    final String sqlSelect = userQry.loadOwnerUserListQry(p_statusUsed, p_status);
    if (LOG.isDebugEnabled()) {
    	LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
    }
    final ArrayList list = new ArrayList();
    try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
        // commented for DB2pstmt =
        // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
        
        int i = 1;
        // commented for DB2pstmt.setFormOfUse(i,
        // OraclePreparedStatement.FORM_NCHAR);
        pstmt.setString(i++, p_ownerName);
        if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            pstmt.setString(i++, p_status);
        }
        pstmt.setString(i++, p_domainCode);
        pstmt.setString(i++, p_parentGraphDomainCode);
        try(ResultSet rs = pstmt.executeQuery();)
        {
        UserVO userVO = null;
        while (rs.next()) {
            userVO = new UserVO();
            userVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
            userVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
            userVO.setOwnerID(SqlParameterEncoder.encodeParams(rs.getString("owner_id")));
            userVO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
            userVO.setGeographicalCode(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_code")));
            list.add(userVO);
        }

    } 
        if(list == null || list.isEmpty()) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_RECORDS_FOUND);
		}
    }catch (SQLException sqe) {
    	LOG.error(methodName, "SQLException : " + sqe);
    	LOG.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadOwnerUserList]", "", "", "",
            "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
    } catch (Exception ex) {
    	LOG.error(methodName, "Exception : " + ex);
    	LOG.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadOwnerUserList]", "", "", "",
            "Exception:" + ex.getMessage());
        throw new BTSLBaseException(this, methodName, ex.getMessage());
    } finally {

        if (LOG.isDebugEnabled()) {
        	LOG.debug(methodName, "Exiting: domainList size=" + list.size());
        }
    }
    return list;
}




/**
 * This method will load primary phoneVO of a user
 * 
 * @param p_con
 * @param p_msisdn
 * @return
 * @throws BTSLBaseException
 */
public UserPhoneVO loadUserPrimayPhoneVO(Connection p_con, String p_userID) throws BTSLBaseException {
    final String methodName = "loadUserPrimayPhoneVO";
    StringBuilder loggerValue= new StringBuilder();
    if (LOG.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("Entered: p_userID=");
    	loggerValue.append(p_userID);
    	LOG.debug(methodName, loggerValue);
    }
     
     
    StringBuilder strBuff = new StringBuilder(" SELECT ph.user_phones_id,ph.msisdn, ph.user_id,");
    strBuff.append(" ph.description,ph.primary_number,ph.sms_pin,ph.pin_required,");
    strBuff.append(" ph.phone_profile,ph.phone_language,ph.country,ph.invalid_pin_count,ph.first_invalid_pin_time,");
    strBuff.append(" ph.last_transaction_status,ph.last_transaction_on,ph.pin_modified_on,");
    strBuff.append(" ph.created_by,ph.created_on,ph.modified_by,ph.modified_on, ph.last_transfer_id,");
    strBuff.append(" last_transfer_type FROM user_phones ph, users u WHERE ph.user_id = ? AND ph.primary_number = ? ");
    strBuff.append(" AND ph.USER_ID= u.USER_ID AND u.STATUS<>'N'AND u.status <> 'C'");

    String sqlSelect = strBuff.toString();
    if(LOG.isDebugEnabled()){
		loggerValue.setLength(0);
		loggerValue.append(QUERY_KEY);
		loggerValue.append(sqlSelect);
		LOG.debug(methodName, loggerValue);
	}
    UserPhoneVO phoneVO = null;
    try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
        
        pstmt.setString(1, p_userID);
        pstmt.setString(2, "Y");
       try(ResultSet rs = pstmt.executeQuery();)
       {
        if (rs.next()) {
            phoneVO = new UserPhoneVO();
            phoneVO.setUserPhonesId(rs.getString("user_phones_id"));
            phoneVO.setMsisdn(rs.getString("msisdn"));
            phoneVO.setUserId(rs.getString("user_id"));
            phoneVO.setDescription(rs.getString("description"));
            phoneVO.setPrimaryNumber(rs.getString("primary_number"));
            phoneVO.setSmsPin(rs.getString("sms_pin"));
            phoneVO.setFirstInvalidPinTime(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("first_invalid_pin_time")));
            if (!BTSLUtil.isNullString(phoneVO.getSmsPin())) {
                // set the default value *****
                phoneVO.setShowSmsPin(BTSLUtil.getDefaultPasswordNumeric(phoneVO.getSmsPin()));
                phoneVO.setConfirmSmsPin(BTSLUtil.getDefaultPasswordNumeric(phoneVO.getSmsPin()));
            }
            phoneVO.setPinRequired(rs.getString("pin_required"));
            phoneVO.setPhoneProfile(rs.getString(PHONE_PROFILE));
            phoneVO.setPhoneLanguage(rs.getString("phone_language"));
            phoneVO.setCountry(rs.getString("country"));
            phoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
            phoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
            phoneVO.setLastTransactionOn(rs.getTimestamp("last_transaction_on"));
            phoneVO.setPinModifiedOn(rs.getTimestamp("pin_modified_on"));
            phoneVO.setCreatedBy(rs.getString("created_by"));
            phoneVO.setCreatedOn(rs.getTimestamp("created_on"));
            phoneVO.setModifiedBy(rs.getString("modified_by"));
            phoneVO.setModifiedOn(rs.getTimestamp("modified_on"));
            phoneVO.setLastTransferID(rs.getString("last_transfer_id"));
            phoneVO.setLastTransferType(rs.getString("last_transfer_type"));
        }
    } 
    }catch (SQLException sqe) {
    	loggerValue.setLength(0);
		loggerValue.append(SQL_EXCEPTION);
		loggerValue.append(sqe.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, "loadUserPhoneVO", "error.general.sql.processing",sqe);
    } catch (Exception ex) {
    	loggerValue.setLength(0);
		loggerValue.append(EXCEPTION);
		loggerValue.append(ex.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
    } finally {
    	
        if (LOG.isDebugEnabled()) {
            //LOG.debug(methodName, "Exiting: userPhoneList size=" + phoneVO);
        }
    }

    return phoneVO;
}

public ChannelUserListResponseVO getChannelUsersList1(java.sql.Connection con, String userDomain,
		String userCategoryCode, String userGeography, String userId, String status, boolean selfAllowed)
		throws BTSLBaseException {
	final String methodName = "getChannelUsersList1";
	GetChannelUsersMsg getChannelUsersMsg;
	ArrayList<GetChannelUsersMsg> channelUsersList = new ArrayList<GetChannelUsersMsg>();
	final ChannelUserListResponseVO result = new ChannelUserListResponseVO();
	HashMap<String, GetChannelUsersMsg> resultMap = new HashMap<String, GetChannelUsersMsg>();
	StringBuffer msg = new StringBuffer("");
	if (LOG.isDebugEnabled()) {
		msg.append("get():: Entered getChannelUsersList1");

		String message = msg.toString();
		LOG.debug(methodName, message);
	}

	PreparedStatement pstmt = null;
	PreparedStatement pstmt1 = null;
	StringBuilder strBuffer = null;
	ArrayList<ProductVO> productList = new ArrayList<ProductVO>();

	try {

		boolean tcpOn = false;
		Set<String> uniqueTransProfileId = new HashSet();

		String sqlSelect = null;
		HashMap<String, HashMap<String, String>> tcpMap = null;

		pstmt = userQry.getChannelUsersListQry1(con, userDomain, userCategoryCode, userGeography, userId, status,
				selfAllowed);

		pstmt1 = con.prepareStatement(
				"select product_code,product_name from products where status = 'Y' and product_type = 'PREPROD'");

		try (ResultSet rs1 = pstmt1.executeQuery();) {

			while (rs1.next()) {
				ProductVO productVO = new ProductVO();

				productVO.setProductCode(rs1.getString("product_code"));
				productVO.setProductName(rs1.getString("product_name"));

				productList.add(productVO);
			}
		}

		try (ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {

				if ((!selfAllowed && userId.equalsIgnoreCase(rs.getString("USER_ID")))
						|| rs.getString("STATUS").equals("C") || rs.getString("STATUS").equals("N")) {
					continue;
				} else {
					if (resultMap.get(rs.getString("USER_ID")) != null) {
						GetChannelUsersMsg object = resultMap.get(rs.getString("USER_ID"));

						boolean toBeAdded = true;

						for (BalanceVO balanceVO : object.getBalanceList()) {
							if (balanceVO.getProductName().equals(rs.getString("product_name"))) {
								toBeAdded = false;
								break;
							}
						}
						if (toBeAdded) {
							BalanceVO balanceVO = new BalanceVO();
							balanceVO
									.setBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
							balanceVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));

							object.getBalanceList().add(balanceVO);
						}

					} else {
						getChannelUsersMsg = new GetChannelUsersMsg();
						getChannelUsersMsg.setUserName(SqlParameterEncoder.encodeParams(rs.getString("USER_NAME")));
						getChannelUsersMsg.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("MSISDN")));
						getChannelUsersMsg.setUserType(SqlParameterEncoder.encodeParams(rs.getString("USER_TYPE")));

						if (rs.getString("BALANCE") != null) {
							List<BalanceVO> balanceList = new ArrayList<BalanceVO>();
							BalanceVO balanceVO = new BalanceVO();
							balanceVO
									.setBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
							balanceVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));

							balanceList.add(balanceVO);
							getChannelUsersMsg.setBalanceList(balanceList);
						}

						if (rs.getString("STATUS").equals("Y")) {
							getChannelUsersMsg.setStatus("Active");
						} else if (rs.getString("STATUS").equals("W")) {
							getChannelUsersMsg.setStatus("New");
						} else if (rs.getString("STATUS").equals("C")) {
							getChannelUsersMsg.setStatus("Cancelled");
						} else if (rs.getString("STATUS").equals("A")) {
							getChannelUsersMsg.setStatus("Approved");
						} else if (rs.getString("STATUS").equals("N")) {
							getChannelUsersMsg.setStatus("Deleted");
						} else if (rs.getString("STATUS").equals("S")) {
							getChannelUsersMsg.setStatus("Suspended");
						} else if (rs.getString("STATUS").equals("EX")) {
							getChannelUsersMsg.setStatus("Expired");
						} else if (rs.getString("STATUS").equals("D")) {
							getChannelUsersMsg.setStatus("Deregistered");
						} else if (rs.getString("STATUS").equals("B")) {
							getChannelUsersMsg.setStatus("Block");
						} else if (rs.getString("STATUS").equals("SR")) {
							getChannelUsersMsg.setStatus("Suspend Request");
						} else if (rs.getString("STATUS").equals("DR")) {
							getChannelUsersMsg.setStatus("Delete Request");
						} else if (rs.getString("STATUS").equals("BR")) {
							getChannelUsersMsg.setStatus("Bar Request");
						} else if (rs.getString("STATUS").equals("BD")) {
							getChannelUsersMsg.setStatus("Barred");
						} else if (rs.getString("STATUS").equals("BA")) {
							getChannelUsersMsg.setStatus("Bar Approve");
						} else if (rs.getString("STATUS").equals("CH")) {
							getChannelUsersMsg.setStatus("Churned");
						} else if (rs.getString("STATUS").equals("DE")) {
							getChannelUsersMsg.setStatus("Deactivated");
						} else if (rs.getString("STATUS").equals("PA")) {
							getChannelUsersMsg.setStatus("Pre Active");
						}

						getChannelUsersMsg.setStatusCode(rs.getString("STATUS"));

						getChannelUsersMsg.setDomain(SqlParameterEncoder.encodeParams(rs.getString("DOMAIN_NAME")));
						getChannelUsersMsg.setCategory(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
						getChannelUsersMsg
								.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));

						if (BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(rs.getString("parent_name")))) {
							getChannelUsersMsg.setParentName("ROOT");
						} else {
							getChannelUsersMsg
									.setParentName(SqlParameterEncoder.encodeParams(rs.getString("parent_name")));
						}
						getChannelUsersMsg.setOwnerName(SqlParameterEncoder.encodeParams(rs.getString("owner_name")));
						getChannelUsersMsg
								.setLastModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
						getChannelUsersMsg.setLastModifiedDateTime(rs.getTimestamp("modified_on"));
						if (!tcpOn) {
							getChannelUsersMsg.setTransactionProfile(
									SqlParameterEncoder.encodeParams(rs.getString("profile_name")));
						} else {

							getChannelUsersMsg.setTransactionProfile(SqlParameterEncoder
									.encodeParams(tcpMap.get(rs.getString("TRANSFER_PROFILE_ID")).get("Name")));

						}
						getChannelUsersMsg.setCommissionProfile(
								SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_name")));
						getChannelUsersMsg
								.setContactPerson(SqlParameterEncoder.encodeParams(rs.getString("contact_person")));
						getChannelUsersMsg.setLastTxnDatTime(rs.getTimestamp("last_transfer_on"));
						getChannelUsersMsg.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
						getChannelUsersMsg.setGrade(SqlParameterEncoder.encodeParams(rs.getString("user_grade")));
						getChannelUsersMsg.setRegisteredDateTime(rs.getTimestamp("created_on"));
						getChannelUsersMsg
								.setGeography(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_name")));
						getChannelUsersMsg.setParentID(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
						getChannelUsersMsg.setUserID(SqlParameterEncoder.encodeParams(rs.getString("USER_ID")));
						if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("modified_on"))) {
						getChannelUsersMsg.setLastModified(rs.getTimestamp("modified_on").toString());
						}
						if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("created_on"))) {
						getChannelUsersMsg.setRegistredDate(rs.getTimestamp("created_on").toString());
						}
						if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("last_transfer_on"))) {
						getChannelUsersMsg.setLastTransaction(rs.getTimestamp("last_transfer_on").toString());
						}
						channelUsersList.add(getChannelUsersMsg);
						resultMap.put(rs.getString("USER_ID"), getChannelUsersMsg);

					}
				}
			}
		}

		for (GetChannelUsersMsg listObject : channelUsersList) {
			LOG.debug(methodName, "Entering for user: " + listObject.getLoginID());
			if (listObject.getBalanceList() == null) {
				LOG.debug(methodName, "No balances exist for user: " + listObject.getLoginID());
				List<BalanceVO> balanceList = new ArrayList<BalanceVO>();

				for (ProductVO productVO : productList) {
					BalanceVO balanceVO = new BalanceVO();
					balanceVO.setProductName(productVO.getProductName());
					balanceList.add(balanceVO);
				}
				listObject.setBalanceList(balanceList);
			} else if (listObject.getBalanceList().size() == productList.size()) {
				LOG.debug(methodName, "All balances exist for user: " + listObject.getLoginID());

			} else if (listObject.getBalanceList().size() != productList.size()) {
				LOG.debug(methodName, "Some balances exist for user: " + listObject.getLoginID());

				for (ProductVO productVO : productList) {
					LOG.debug(methodName, "Entering for product: " + productVO.getProductName());
					boolean isFound = false;
					for (BalanceVO balanceVO : listObject.getBalanceList()) {
						if (balanceVO.getProductName().equalsIgnoreCase(productVO.getProductName())) {
							LOG.debug(methodName, "Product found: " + productVO.getProductName());
							isFound = true;
						}
					}
					if (!isFound) {
						LOG.debug(methodName, "Product not found: " + productVO.getProductName());
						BalanceVO balanceVO = new BalanceVO();
						balanceVO.setProductName(productVO.getProductName());
						listObject.getBalanceList().add(balanceVO);
					}

				}

			}

			LOG.debug(methodName, "Sorting for user: " + listObject.getLoginID());
			Collections.sort(listObject.getBalanceList(), new Comparator<BalanceVO>() {

				@Override
				public int compare(BalanceVO arg0, BalanceVO arg1) {

					return arg1.getProductName().compareTo(arg0.getProductName());
				}
			});
		}

		result.setChannelUsersList(channelUsersList);

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting userName:" + result);
		}
	}
	return result;

}

/**
 * This method will load primary phoneVO of a user
 * 
 * @param p_con
 * @param p_msisdn
 * @return
 * @throws BTSLBaseException
 */
public List<UserApprovalVO> loadApprovalListbyCreater(Connection p_con, ApplistReqVO applistReqVO) throws BTSLBaseException {
    final String methodName = "loadUserPrimayPhoneVO";
    StringBuilder loggerValue= new StringBuilder();
    
  String status=null; 
    List<UserApprovalVO> listUserApprovals = new ArrayList<UserApprovalVO>();
    if (LOG.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("Entered: p_userID=");
    	loggerValue.append(applistReqVO.getLoggedInUserUserid());
    	LOG.debug(methodName, loggerValue);
    }
    String sqlSelect = userQry.loadApprovalListbyCreater(applistReqVO);
    if(LOG.isDebugEnabled()){
		loggerValue.setLength(0);
		loggerValue.append(QUERY_KEY);
		loggerValue.append(sqlSelect);
		LOG.debug(methodName, loggerValue);
	}
    UserApprovalVO userApprovalVO; 
    try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
    	
    	if(PretupsI.LOGIN_ID_TAB.equals(applistReqVO.getReqTab())){
    		pstmt.setString(1, applistReqVO.getLoginID());	
    	}else if(PretupsI.MSISDN_TAB.equals(applistReqVO.getReqTab())) {
    		pstmt.setString(1, applistReqVO.getMobileNumber());
    	}else { //Advanced tab;
    		pstmt.setString(1, applistReqVO.getCategory());
    		pstmt.setString(2, applistReqVO.getCategory());
    		pstmt.setString(3, applistReqVO.getGeography());
    		pstmt.setString(4, applistReqVO.getGeography());
    		pstmt.setString(5, applistReqVO.getLoggedInUserUserid());
    	}
    	
    	if(null==applistReqVO.getStatus()) {
    		status=PretupsI.USER_STATUS_NEW;
			//pstmt.setString(5, PretupsI.USER_STATUS_NEW);	
		}else {
			status =applistReqVO.getStatus();
			//pstmt.setString(5, applistReqVO.getStatus());
		}
    	
    	if(!PretupsI.ADVANCED_TAB.equals(applistReqVO.getReqTab())) {
    		pstmt.setString(2, status);
    		pstmt.setString(3, applistReqVO.getLoggedInUserUserid());
          }else {
        	  pstmt.setString(6, status);
        	  pstmt.setString(7, applistReqVO.getLoggedInUserUserid());
          }
    
       try(ResultSet rs = pstmt.executeQuery();)
       {
        while (rs.next()) {
        	userApprovalVO = new UserApprovalVO();
        	userApprovalVO.setUserID(rs.getString("USER_ID"));
        	userApprovalVO.setLoginID(rs.getString("login_id"));
        	userApprovalVO.setUserName(rs.getString("USER_NAME"));
        	userApprovalVO.setMsisdn(rs.getString("MSISDN"));
        	userApprovalVO.setCreatedBY(rs.getString("CREATEDBY"));
        	userApprovalVO.setUserStatus(rs.getString("statusDesc"));
        	listUserApprovals.add(userApprovalVO);
        }
    } 
    }catch (SQLException sqe) {
    	loggerValue.setLength(0);
		loggerValue.append(SQL_EXCEPTION);
		loggerValue.append(sqe.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, "loadUserPhoneVO", "error.general.sql.processing",sqe);
    } catch (Exception ex) {
    	loggerValue.setLength(0);
		loggerValue.append(EXCEPTION);
		loggerValue.append(ex.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
    } finally {
    	
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting: userPhoneList size=" + listUserApprovals);
        }
    }

    return listUserApprovals;
}

    public List<UserApprovalVO> loadApprovalListbyCreaterAdvance(Connection p_con, ApplistReqVO applistReqVO) throws BTSLBaseException {
        final String methodName = "loadApprovalListbyCreaterAdvance";
        StringBuilder loggerValue= new StringBuilder();

        String status=null;
        List<UserApprovalVO> listUserApprovals = new ArrayList<UserApprovalVO>();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_userID=");
            loggerValue.append(applistReqVO.getLoggedInUserUserid());
            LOG.debug(methodName, loggerValue);
        }
        String sqlSelect = userQry.loadApprovalListbyCreaterAdvance(applistReqVO);
        if(LOG.isDebugEnabled()){
            loggerValue.setLength(0);
            loggerValue.append(QUERY_KEY);
            loggerValue.append(sqlSelect);
            LOG.debug(methodName, loggerValue);
        }
        UserApprovalVO userApprovalVO;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            int i=1;
            pstmt.setString(i++,applistReqVO.getUserType());
            pstmt.setString(i++,PretupsI.MANUAL_USR_CREATION_TYPE);
            pstmt.setString(i++, applistReqVO.getCategory());
            pstmt.setString(i++, applistReqVO.getCategory());
            pstmt.setString(i++, applistReqVO.getGeography());
            pstmt.setString(i++, applistReqVO.getGeography());

            if(null==applistReqVO.getStatus()) {
                status=PretupsI.USER_STATUS_NEW;
            }else {
                status =applistReqVO.getStatus();
            }
            pstmt.setString(i++, status);

            try(ResultSet rs = pstmt.executeQuery();)
            {
                while (rs.next()) {
                    userApprovalVO = new UserApprovalVO();
                    userApprovalVO.setUserID(rs.getString("USER_ID"));
                    userApprovalVO.setLoginID(rs.getString("login_id"));
                    userApprovalVO.setUserName(rs.getString("USER_NAME"));
                    userApprovalVO.setMsisdn(rs.getString("MSISDN"));
                    userApprovalVO.setCreatedBY(rs.getString("CREATEDBY"));
                    userApprovalVO.setUserStatus(rs.getString("statusDesc"));
                    listUserApprovals.add(userApprovalVO);
                }
            }
        }catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append(SQL_EXCEPTION);
            loggerValue.append(sqe.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserPhoneVO", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(EXCEPTION);
            loggerValue.append(ex.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userPhoneList size=" + listUserApprovals);
            }
        }

        return listUserApprovals;
    }
    public List<UserApprovalVO> loadApprovalListbyCreaterMob(Connection p_con, ApplistReqVO applistReqVO) throws BTSLBaseException {
        final String methodName = "loadApprovalListbyCreaterMob";
        StringBuilder loggerValue= new StringBuilder();

        String status=null;
        List<UserApprovalVO> listUserApprovals = new ArrayList<UserApprovalVO>();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_userID=");
            loggerValue.append(applistReqVO.getLoggedInUserUserid());
            LOG.debug(methodName, loggerValue);
        }
        String sqlSelect = userQry.loadApprovalListbyCreaterMob(applistReqVO);
        if(LOG.isDebugEnabled()){
            loggerValue.setLength(0);
            loggerValue.append(QUERY_KEY);
            loggerValue.append(sqlSelect);
            LOG.debug(methodName, loggerValue);
        }
        UserApprovalVO userApprovalVO;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {

            if(PretupsI.LOGIN_ID_TAB.equals(applistReqVO.getReqTab())){
                pstmt.setString(1, applistReqVO.getLoginID());
            }else if(PretupsI.MSISDN_TAB.equals(applistReqVO.getReqTab())) {
                pstmt.setString(1, applistReqVO.getMobileNumber());
            }
            if(null==applistReqVO.getStatus()) {
                status=PretupsI.USER_STATUS_NEW;
            }else {
                status =applistReqVO.getStatus();
            }
            pstmt.setString(2, status);
            pstmt.setString(3, PretupsI.MANUAL_USR_CREATION_TYPE);

            try(ResultSet rs = pstmt.executeQuery();)
            {
                while (rs.next()) {
                    userApprovalVO = new UserApprovalVO();
                    userApprovalVO.setUserID(rs.getString("USER_ID"));
                    userApprovalVO.setLoginID(rs.getString("login_id"));
                    userApprovalVO.setUserName(rs.getString("USER_NAME"));
                    userApprovalVO.setMsisdn(rs.getString("MSISDN"));
                    userApprovalVO.setCreatedBY(rs.getString("CREATEDBY"));
                    userApprovalVO.setUserStatus(rs.getString("statusDesc"));
                    listUserApprovals.add(userApprovalVO);
                }
            }
        }catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append(SQL_EXCEPTION);
            loggerValue.append(sqe.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserPhoneVO", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(EXCEPTION);
            loggerValue.append(ex.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: userPhoneList size=" + listUserApprovals);
            }
        }

        return listUserApprovals;
    }


public ArrayList<DashboardPermissionVO> loaduserPermission(Connection p_con, HashMap<String, String> userIdLoginMap) throws BTSLBaseException {
	
    final String methodName = "loaduserPermission";
    StringBuilder loggerValue= new StringBuilder();
    
   List<UserApprovalVO> listUserApprovals = new ArrayList<UserApprovalVO>();
   
   HashMap<String, ArrayList<String>> rolesUsersMap = new HashMap<String, ArrayList<String>>();
   
   ArrayList<DashboardPermissionVO> dashboardVOList =  new ArrayList<DashboardPermissionVO>(); 
    StringBuilder strBuff = new StringBuilder("SELECT  DISTINCT u.login_id, \r\n" + 
    		"r.role_code " + 
    		"         FROM " + 
    		"         roles r ," + 
    		"     \r\n" + 
    		"         user_roles ur, " + 
    		"         users u\r\n" + 
    		"         WHERE r.status != 'N' " + 
    		"         AND r.ROLE_CODE like '%RPT'" + 
    		"         AND u.user_id = ur.user_id" + 
    		
    		"         AND ur.role_code = r.role_code");
 
    
    String dashboardIdsStr = Constants.getProperty("GRAFANA_DASHBOARD_IDS_MAP");
    
    HashMap<String, String> dashoardRoleIdMap = new HashMap<String, String>();
    String[] dashboardIds = dashboardIdsStr.split(",");
    
    for(String keyValues: dashboardIds) {
    	dashoardRoleIdMap.put(keyValues.split("=")[0], keyValues.split("=")[1]);
    }
    
     
    String sqlSelect = strBuff.toString();
    
    try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
    	
    	   try(ResultSet rs = pstmt.executeQuery();)
       {
        while (rs.next()) {
        	
        	String key = dashoardRoleIdMap.get(rs.getString("role_code")) ; 
        	String grafanaUserId = userIdLoginMap.get(rs.getString("login_id"));
        	
        	if(key != null && grafanaUserId != null) {
        	if(rolesUsersMap.get(key) == null) {
        		ArrayList<String> list = new ArrayList<String>();
        		list.add(grafanaUserId);
        		rolesUsersMap.put(key, list);	
        	}else {
        		rolesUsersMap.get(key).add(grafanaUserId);
        		rolesUsersMap.put(key, rolesUsersMap.get(key));
        	}
        	}
        }
        
        
				for (String key : rolesUsersMap.keySet()) {
					DashboardPermissionVO dashboardVOObj = new DashboardPermissionVO() ; 
					ArrayList<Item> items = new ArrayList<Item>();
					ArrayList<String> userIds = rolesUsersMap.get(key);
					for (String userId : userIds) {
						Item item = new Item();
						item.setPermission(4);// ADMIN
						item.setUserId(userId);
						items.add(item);
					}
					dashboardVOObj.setItems(items);
					dashboardVOObj.setId(key);
					dashboardVOList.add(dashboardVOObj);
				}
        
        
    } 
    }catch (SQLException sqe) {
    	loggerValue.setLength(0);
		loggerValue.append(SQL_EXCEPTION);
		loggerValue.append(sqe.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, "loadUserPhoneVO", "error.general.sql.processing",sqe);
    } catch (Exception ex) {
    	loggerValue.setLength(0);
		loggerValue.append(EXCEPTION);
		loggerValue.append(ex.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserPhoneVO]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
    } 

    return dashboardVOList;
}



public int changeUserStatusForBatchAll(Connection con, List<UserVO> list) throws BTSLBaseException {
	 final String methodName = "changeUserStatusForBatchAll";
    if (LOG.isDebugEnabled()) {
    	LOG.debug(methodName, "QUERY sqlUpdate");
    }
    final ResultSet rs = null;
    int updateCount = 0;
    UserVO userVO = null;
   
    try {
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("UPDATE USERS SET STATUS=?,previous_status=?,REMARKS=?,MODIFIED_ON=?,MODIFIED_BY =? ");
        strBuff.append(" WHERE MSISDN=? ");
        final String updateQuery = strBuff.toString();

        if (LOG.isDebugEnabled()) {
        	LOG.debug(methodName, "Query sqlUpdate:" + updateQuery);
        }

        try(PreparedStatement pstmtModify = con.prepareStatement(updateQuery);)
        {
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
           
            userVO = new UserVO();
            userVO = (list.get(i));
            pstmtModify.setString(1, userVO.getStatus());
            pstmtModify.setString(2, userVO.getPreviousStatus());
            pstmtModify.setString(3, userVO.getRemarks());
            pstmtModify.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(userVO.getModifiedOn()));
            pstmtModify.setString(5, userVO.getModifiedBy());
            pstmtModify.setString(6, userVO.getMsisdn());
            updateCount += pstmtModify.executeUpdate();
            pstmtModify.clearParameters();
        }
    }
    }
    catch (SQLException sqe) {
    	LOG.error(methodName, "SQLException : " + sqe);
    	LOG.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
            "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    } catch (Exception ex) {
    	LOG.error(methodName, "Exception : " + ex);
    	LOG.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
            "Exception:" + ex.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.processing");
    } finally {
        if (LOG.isDebugEnabled()) {
        	LOG.debug(methodName, "Exiting: userVO=" + userVO);
        }
    }
    return updateCount;
}




public void updateLoggedInNetworkCode(String networkCode, Connection con, String userId) throws BTSLBaseException {
	// TODO Auto-generated method stub
    // commented for DB2OraclePreparedStatement psmtUpdate = null;
	
	
    final String methodName = "updateLoggedInNetworkCode";
    StringBuilder loggerValue= new StringBuilder();
    if (LOG.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("Entered: p_userVO=");
    	//loggerValue.append(p_userVO);
    	LOG.debug(methodName, loggerValue);
    }
    int updateCount=0;
    try {
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("UPDATE users SET network_code = ?");
        strBuff.append(" WHERE user_id = ? ");
        String updateQuery = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
       

        // commented for DB2psmtUpdate =
        // (OraclePreparedStatement)p_con.prepareStatement(updateQuery);
        
    	int a=0;
        try(PreparedStatement psmtUpdate = con.prepareStatement(updateQuery);)
        {
        	psmtUpdate.setString(++a, networkCode);
        	psmtUpdate.setString(++a, userId);
        	
        	updateCount = psmtUpdate.executeUpdate();
        	con.commit();
        	//psmtUpdate.clearParameters();
       } // end of try
    }
    catch (SQLException sqle) {
    	loggerValue.setLength(0);
		loggerValue.append(SQL_EXCEPTION);
		loggerValue.append(sqle.getMessage());
		LOG.error(methodName, loggerValue);
        LOG.errorTrace(methodName, sqle);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUser]", "", "", "", loggerValue.toString());
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
    }
    catch (Exception be) {
        LOG.error(methodName, "BTSLBaseException:" + be.toString());
        throw be;
    } 
     // end of catch
//    catch (BTSLBaseException e) {
//    	loggerValue.setLength(0);
//		loggerValue.append(EXCEPTION);
//		loggerValue.append(e.getMessage());
//		LOG.error(methodName, loggerValue);
//        LOG.errorTrace(methodName, e);
//        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUser]", "", "", "", loggerValue.toString());
//        throw new BTSLBaseException(this, methodName, "error.general.processing",e);
//    } // end of catch
    finally {
    	
    	LogFactory.printLog(methodName,  "Exiting: updateCount=" + updateCount, LOG);
    } // end of finally
    
	
}




public ChannelUserListByParentResponseVO getChannelUsersListbyParent(java.sql.Connection con, ChannelUserListByParntReqVO requestVO)
		throws BTSLBaseException {
	final String methodName = "getChannelUsersListbyParent";
	StringBuffer msg=new StringBuffer("");
	ChannelUserDTO channelUserDTO;
	ArrayList<ChannelUserDTO> channelUsersList = new ArrayList<ChannelUserDTO>();
	final ChannelUserListByParentResponseVO result = new ChannelUserListByParentResponseVO();
	if (LOG.isDebugEnabled()) {
		msg.append("get():: Entered getChannelUsersListbyParent");

		String message = msg.toString();
		LOG.debug(methodName, message);
	}

	PreparedStatement pstmt = null;
	
	

	try {
		String sqlSelect = null;
		pstmt = userQry.getChannelUserListByParentQry1(con, requestVO.getDomain(), requestVO.getUserCategory(), requestVO.getGeography(), requestVO.getParentUserID(), requestVO.getUserName() ,requestVO.getOwnerUserID()
				);
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {

					channelUserDTO = new ChannelUserDTO();
					channelUserDTO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("USER_NAME")));
					channelUserDTO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
					channelUserDTO.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("MSISDN")));
					channelUserDTO.setUserType(SqlParameterEncoder.encodeParams(rs.getString("USER_TYPE")));
					channelUserDTO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
					channelUserDTO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("statusdesc")));
								channelUsersList.add(channelUserDTO);
		

					}
				}
			
		

				result.setChannelUsersList(channelUsersList);

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[getChannelUsersListbyParent]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR, sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[getChannelUsersListbyParent]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR, e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting userName:" + result);
		}
	}
	return result;

}



public StaffUserListByParentResponseVO getStaffUsersListbyParent(java.sql.Connection con, StaffUserListByParntReqVO requestVO)
		throws BTSLBaseException {
	final String methodName = "getStaffUsersListbyParent";
	StringBuffer msg=new StringBuffer("");
	StaffUserDTO staffUserDTO;
	ArrayList<StaffUserDTO> staffUsersList = new ArrayList<StaffUserDTO>();
	final StaffUserListByParentResponseVO result = new StaffUserListByParentResponseVO();
	if (LOG.isDebugEnabled()) {
		msg.append("get():: Entered getStaffUsersListbyParent");

		String message = msg.toString();
		LOG.debug(methodName, message);
	}

	PreparedStatement pstmt = null;
	try {
		String sqlSelect = null;
		pstmt = userQry.getStaffUserListByParentQry1(con, requestVO	);
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {

					staffUserDTO = new StaffUserDTO();
					staffUserDTO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("USER_NAME")));
					staffUserDTO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
					staffUserDTO.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("MSISDN")));
					staffUserDTO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
					staffUserDTO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("statusdesc")));
					staffUserDTO.setDomain(SqlParameterEncoder.encodeParams(rs.getString("domain_name")));
					staffUserDTO.setParentLoginID(SqlParameterEncoder.encodeParams(rs.getString("parentLoginID")));
					staffUserDTO.setChannelUserName(SqlParameterEncoder.encodeParams(rs.getString("parent_name")));
					staffUserDTO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
					staffUserDTO.setOwnerName(SqlParameterEncoder.encodeParams(rs.getString("owner_name")));
					staffUsersList.add(staffUserDTO);
		

					}
				}
				result.setStaffuserList(staffUsersList);

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[getStaffUsersListbyParent]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR, sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[getStaffUsersListbyParent]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR, e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting userName:" + result);
		}
	}
	return result;

}




public PagesUI fetchPagesUI(Connection p_con, String userId, String domainType, String categoryCode, String fixedRoles,  String pageType, String tabName)
		throws BTSLBaseException {
	final String methodName = "fetchPagesUI";
	StringBuffer msg=new StringBuffer("");

	PreparedStatement psmtIsExist = null;
    ResultSet rsIsExist = null;
    boolean groupRole = false;	

	StringBuilder isGroupRoleQueryBuff = new StringBuilder("SELECT 1 FROM USER_ROLES,ROLES ");
    isGroupRoleQueryBuff.append("WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
    isGroupRoleQueryBuff.append("AND group_role='Y'AND (ROLES.status IS NULL OR ROLES.status='Y')");
    isGroupRoleQueryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
   

	PagesUI pagesUI = new PagesUI();
	PreparedStatement pstmt = null;
	try {
		
		psmtIsExist = p_con.prepareStatement(isGroupRoleQueryBuff.toString());
		psmtIsExist.setString(1, userId);
		psmtIsExist.setString(2, domainType);

		rsIsExist = psmtIsExist.executeQuery();
		if (rsIsExist.next()) {
			groupRole = true;
		}else {
			groupRole = false;
		}
		
		String sqlSelect = null;
	
        if ("Y".equalsIgnoreCase(fixedRoles)) {
        	sqlSelect = userQry.fetchPagesUIRolesFixed(pageType, tabName , groupRole);//Handle Group role case
        } else {
        	sqlSelect = userQry.fetchPagesUIRoles(pageType, tabName , groupRole);//Handle Group role case
        }
        
        
		 
		
		if(LOG.isDebugEnabled())
			LOG.debug(methodName, sqlSelect);
		
		pstmt = p_con.prepareStatement(sqlSelect);
			if ("Y".equalsIgnoreCase(fixedRoles)) {
				pstmt.setString(1, categoryCode);
				pstmt.setString(2, domainType);
				pstmt.setString(3, domainType);
				pstmt.setString(4, categoryCode);
				if(pageType !=null) {
					pstmt.setString(5, pageType);
				}	

			} else {
				pstmt.setString(1, userId);
				pstmt.setString(2, domainType);
				pstmt.setString(3, categoryCode);
				pstmt.setString(4, domainType);
				pstmt.setString(5, categoryCode);
				if(pageType !=null) {
					pstmt.setString(6, pageType);
				}
			}
			
		String parentPageCode = null;
		
			try (ResultSet rs = pstmt.executeQuery();) {
				String prevModule = null;
				Page page = null;
				
				while (rs.next()) {

					parentPageCode = rs.getString(7);
					
					if(parentPageCode != null && parentPageCode.equalsIgnoreCase("ROOT")) {
						
						if(page != null) {
							List<Page> pages = pagesUI.getPages();
							
							if(pages == null) {
								pages = new ArrayList<Page>();
							}
							pages.add(page);
							
							pagesUI.setPages(pages);
							
						}
						
						page = new Page();
						page.setId(rs.getString(1));
						page.setName(rs.getString(4));
						page.setImage(rs.getString(6));
						page.setPath(rs.getString(5));
						List<SubPage> subPages = new ArrayList<SubPage>();
						page.setSubPages(subPages);
					}else {
						SubPage subPage = new SubPage();
						subPage.setId(rs.getString(1));
						subPage.setName(rs.getString(4));
						//subPage.setImage(rs.getString(6));
						subPage.setRouterLink(rs.getString(5));
						
						List<SubPage> subPageList = page.getSubPages();
                        if(subPageList == null || subPageList.isEmpty()){
                            page.setPath(rs.getString(5));
                        }
						subPageList.add(subPage);
						
						page.setSubPages(subPageList);
						
					}
					
					
					
					
					}
				
				List<Page> pages = pagesUI.getPages();
				
				if(pages == null) {
					pages = new ArrayList<Page>();
				}
				pages.add(page);
				
				pagesUI.setPages(pages);
				
				
			}
			
			
	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchPagesUI]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchPagesUI]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting" );
		}
	}
	return pagesUI;

}

public String fetchTabsUI(Connection p_con, String userId, String domainType, String categoryCode, String fixedRoles,  String pageType, String tabName)
		throws BTSLBaseException {
	final String methodName = "fetchPagesUI";
	StringBuffer msg=new StringBuffer("");
	PreparedStatement psmtIsExist = null;
    ResultSet rsIsExist = null;
    boolean groupRole = false;

	StringBuilder isGroupRoleQueryBuff = new StringBuilder("SELECT 1 FROM USER_ROLES,ROLES ");
    isGroupRoleQueryBuff.append("WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
    isGroupRoleQueryBuff.append("AND group_role='Y'AND (ROLES.status IS NULL OR ROLES.status='Y')");
    isGroupRoleQueryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
	HashSet tabs =  new HashSet<String>();
	String tab="";
	PreparedStatement pstmt = null;
	try {
		String sqlSelect = null;
		psmtIsExist = p_con.prepareStatement(isGroupRoleQueryBuff.toString());
		psmtIsExist.setString(1, userId);
		psmtIsExist.setString(2, domainType);

		rsIsExist = psmtIsExist.executeQuery();
		if (rsIsExist.next()) {
			groupRole = true;
		}else {
			groupRole = false;
		}
		
        if ("Y".equalsIgnoreCase(fixedRoles)) {
        	sqlSelect = userQry.fetchPagesUIRolesFixed(pageType, tabName , groupRole);//Handle Group role case
        } else {
        	sqlSelect = userQry.fetchPagesUIRoles(pageType, tabName , groupRole);//Handle Group role case
        }
		
		if(LOG.isDebugEnabled())
			LOG.debug(methodName, sqlSelect);
		
		System.out.println("sqlSelect  >>>> "+sqlSelect);
		
		pstmt = p_con.prepareStatement(sqlSelect);
			if ("Y".equalsIgnoreCase(fixedRoles)) {
				pstmt.setString(1, categoryCode);
				pstmt.setString(2, domainType);
				pstmt.setString(3, domainType);
				pstmt.setString(4, categoryCode);
				if(pageType !=null) {
					pstmt.setString(5, pageType);
				}	

			} else {
				pstmt.setString(1, userId);
				pstmt.setString(2, domainType);
				pstmt.setString(3, categoryCode);
				pstmt.setString(4, domainType);
				pstmt.setString(5, categoryCode);
				if(pageType !=null) {
					pstmt.setString(6, pageType);
				}
			}
			
			tab="{ \"tabs\": ";
			
			
			String temp="";
			
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					tabs.add(rs.getString(14));
					//temp = temp + "\""+rs.getString(14)+"\",";
				}
				
			}
			Iterator itr = tabs.iterator() ; 
			
			while(itr.hasNext()) {
				temp = temp + "\""+itr.next()+"\",";
			}
			temp = temp.substring(0, temp.lastIndexOf(","));
			
			tab = tab+ "[ "+temp+" ] }";
	} catch (SQLException sqle) {
		sqle.printStackTrace();
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchPagesUI]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		e.printStackTrace();
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchPagesUI]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting" );
		}
	}
	return tab;

}

public RulesUIMapping fetchUserRulesUI(Connection p_con)
		throws BTSLBaseException {
	final String methodName = "fetchUserRolesUI";
	StringBuffer msg=new StringBuffer("");


	RulesUIMapping rulesUIMapping = new RulesUIMapping();
	PreparedStatement pstmt = null;
	try {
		String sqlSelect = null;
		
		sqlSelect = "SELECT RULE_ID, " + 
				"RULE_EXPRESSION  "+
				"FROM RULE_UI_MAPPING\r\n" ;
	
		List<RuleId> ruleIds = new ArrayList<RuleId>();
		
		pstmt = p_con.prepareStatement(sqlSelect);
			try (ResultSet rs = pstmt.executeQuery();) {
				String prevModule = null;
				
				while (rs.next()) {

					String ruleId1 = rs.getString(1);
					String expression = rs.getString(2);
					
					RuleId ruleId = new RuleId();
					ruleId.setRuleId(ruleId1);
					ruleId.setExpression(expression);
					ruleIds.add(ruleId);
					
					}
				}
			
			rulesUIMapping.setRuleIds(ruleIds);

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchUserRolesUI]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchUserRolesUI]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting" );
		}
	}
	return rulesUIMapping;

}


public RuleRoleCodes fetchUserRolesUI(Connection p_con)
		throws BTSLBaseException {
	final String methodName = "fetchUserRolesUI";
	StringBuffer msg=new StringBuffer("");


	RuleRoleCodes ruleRoleCodesP = new RuleRoleCodes();
	PreparedStatement pstmt = null;
	try {
		String sqlSelect = null;
		sqlSelect = "SELECT ROLE_UI_PARENT_ID,\r\n" + 
				"ROLE_UI_ID,\r\n" + 
				"RULE_CODE\r\n" + 
				"\r\n" + 
				"FROM ROLE_UI_MAPPING\r\n" + 
				"\r\n" + 
				"ORDER BY ROLE_UI_PARENT_ID";
		
	
		List<RuleRoleCode> ruleRoleCodes = new ArrayList<RuleRoleCode>();
		RuleRoleCode ruleRoleCode = null;
		List<RuleRole> ruleRoleList = null;
	
		
		pstmt = p_con.prepareStatement(sqlSelect);
			try (ResultSet rs = pstmt.executeQuery();) {
				String prevModule = null;
				
				while (rs.next()) {

					String module = rs.getString(1);
					String roleUid = rs.getString(2);
					String roleCode = rs.getString(3);
					
					if(prevModule == null) {
						ruleRoleCode = new RuleRoleCode();
						ruleRoleList = new ArrayList<RuleRole>();
						prevModule = module;
					}else if(!prevModule.equalsIgnoreCase(module)) {
						prevModule = module;
						ruleRoleCodes.add(ruleRoleCode);
						ruleRoleCode.setRuleRole(ruleRoleList);
						
						
						ruleRoleCode = new RuleRoleCode();
						ruleRoleList = new ArrayList<RuleRole>();
						
					}
					
					ruleRoleCode.setModule(module);
					RuleRole ruleRole = new RuleRole();
					ruleRole.setRuleId(roleUid);
					ruleRole.setRoleCode(roleCode);
					ruleRoleList.add(ruleRole);
					//List<RuleRole> ruleRole = new ArrayList<RuleRole>();
					
					}
				}
			
			ruleRoleCodes.add(ruleRoleCode);
			ruleRoleCode.setRuleRole(ruleRoleList);
			
			ruleRoleCodesP.setRuleRoleCodes(ruleRoleCodes);

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchUserRolesUI]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchUserRolesUI]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting" );
		}
	}
	return ruleRoleCodesP;

}





public ReportTemplatesResponse fetchReportsTemplates(Connection p_con, Set p_roleCodeSet)
		throws BTSLBaseException {
	final String methodName = "ReportTemplatesResponse";
	StringBuffer msg=new StringBuffer("");

	ReportTemplatesResponse response = null;
	ArrayList<ReportsTemplate> reportsTemplates = new ArrayList<ReportsTemplate>();
	
	PreparedStatement pstmt = null;
	try {
		String sqlSelect = null;
//		sqlSelect = "SELECT RPT_ID, RPT_DESCRIPTION,RPT_MODE, JSON_TEMPLATE FROM REPORT_TEMPLATES_CLOB";
        sqlSelect = "SELECT\n" +
                "\trtc.RPT_ID,\n" +
                "\trtc.RPT_DESCRIPTION,\n" +
                "\trtc.RPT_MODE,\n" +
                "\trrm.ROLE_CODE, \n" +
                "\trtc.JSON_TEMPLATE\n" +
                "FROM\n" +
                "\tREPORT_TEMPLATES_CLOB rtc ,\n" +
                "\tREPORT_ROLE_MAPPING rrm \n" +
                "\tWHERE rrm.RPT_ID = rtc.RPT_ID";
		pstmt = p_con.prepareStatement(sqlSelect);
			try (ResultSet rs = pstmt.executeQuery();) {
				response = new ReportTemplatesResponse() ;
				while (rs.next()) {
                    if(!p_roleCodeSet.contains(rs.getString(4))){
                        continue;
                    }
					ReportsTemplate template = new ReportsTemplate();
					template.setRptId(rs.getString(1));
					template.setRptDesc(rs.getString(2));
                    template.setMode(rs.getString(3));
                    String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
                    if(dbConnected.equalsIgnoreCase(QueryConstants.DB_ORACLE)){
                        Clob jsonClob = rs.getClob(5);
                        Reader r = jsonClob.getCharacterStream();
                        StringBuffer buffer = new StringBuffer();
                        int ch;
                        while ((ch = r.read())!=-1) {
                            buffer.append(""+(char)ch);
                        }
                        template.setJsonTemplate(buffer.toString());
                    }else{
                        template.setJsonTemplate(rs.getString(5));
                    }

					reportsTemplates.add(template);
				}
				}
			response.setReportsTemplates(reportsTemplates);

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchReportsTemplates]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchReportsTemplates]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting" );
		}
	}
	return response;

}






public String generatePDF(ArrayList<String> rowHeader,ArrayList<ArrayList<String>>  rows, String filePath, String fileName, String fileType) {
	final String methodName="generatePDF";
	String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	Locale locale = new Locale(lang, country);
	String emptyRow[]= {" "," "," "," "," "," "};
	
	String fileContentString=null;
	Document document = new Document();

	File file = new File(filePath+fileName+fileType);
		
	try
	
	{
	
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath+fileName+fileType));  
		document.open();
		Font sectionHeader = new Font();
		sectionHeader.setStyle(Font.BOLD); 
		sectionHeader.setSize(8);  
		Font myfont = new Font();
 		  myfont.setStyle(Font.NORMAL); 
		  myfont.setSize(6);  
		  Font headerFont = new Font(); 
		  headerFont.setStyle(Font.BOLD);   
		  headerFont.setSize(6);  
		//PdfPTable table = new PdfPTable(rowHeader.size()); // 3 columns.
		  
		  PdfPTable table = new PdfPTable(5); // 3 columns.
		//table.setWidthPercentage(100); //Width 100%
		table.setSpacingBefore(10f); //Space before table
		table.setSpacingAfter(10f); //Space after table
		table.getDefaultCell().setBorder(Rectangle.BOX);  
		
		//PdfPCell cell=null;
		//Set Column widths 
		float[] columnWidths =   {1f, 1f, 1f,1f,1f}; //new float[rowHeader.size()];
		
		/*for(int i=0; i < columnWidths.length ; i++) {
			columnWidths[i] = 1f;
		}
		*/
		//table.setTotalWidth(columnWidths);
		table.setWidths(columnWidths);
		
		for(int i=0;i<rowHeader.size();i++) {
		PdfPCell cell = new PdfPCell(new Paragraph(rowHeader.get(i),sectionHeader));
		cell.setColspan(1);     
		cell.setBorder(Rectangle.NO_BORDER); 
		cell.setBorderColor(BaseColor.BLUE);
		cell.setPaddingLeft(10); 
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		}
		
			if (rows != null && rows.size() > 0) {
				for (int i = 0; i < rows.size(); i++) {

					for (int j = 0; j < rows.get(i).size(); j++) {
						PdfPCell cell = new PdfPCell(new Paragraph(rows.get(i).get(j), headerFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setBorderColor(BaseColor.BLUE);
						cell.setPaddingLeft(10);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						table.addCell(cell);
					}
				}
			}
	
			
		int maxColumnsInCustomReport = Integer.parseInt(Constants.getProperty("MAX_COLUMNS_CUSTOM_REPORT"));	
		//document.add(table);
		
		/*BufferedReader br = new BufferedReader(new FileReader("D://columns.txt"));
		
		String str="";
		String col="";
		
		while((str=br.readLine()) != null) {
			col = str;
		}
		
		br.close();
		*/
		int noOfcoloumn = 10;
		PdfPTable mytable = new PdfPTable(maxColumnsInCustomReport);
		
		
		for(int i=0; i < rowHeader.size() ; i++) {
			
			if( i < maxColumnsInCustomReport) {
			PdfPCell cell = new PdfPCell(new Paragraph(rowHeader.get(i), headerFont));
			mytable .addCell(cell); //Row 1 Strat
			}
	    
		}
		
		for(int i=0; i < rows.size() ; i++) {
			for(int j=0; j < rows.get(i).size() ; j++) {
				
				if(j < maxColumnsInCustomReport) {
				PdfPCell cell = new PdfPCell(new Paragraph(rows.get(i).get(j), headerFont));
				mytable .addCell(cell); //Row 1 Strat
				}
		    
			}
	}
		
		
	    document.add(mytable);
		
		document.close();
		writer.close();
		
		byte[] fileContent = Files.readAllBytes(file.toPath());
        fileContentString = Base64.getEncoder().encodeToString(fileContent);
        return fileContentString;
		
	} catch (Exception e)
	{
		e.printStackTrace();
	}
	
 return null;
}


public String fetchCommonReport(Connection p_con, CommonReportRequest request)




		throws BTSLBaseException {
	final String methodName = "fetchCommonReport";
	StringBuffer msg=new StringBuffer("");

	HashMap<String, String> paramsMap = new HashMap<String, String>();
	
	for(Param param: request.getParams()) {
	
		if(param.getParam()!= null && param.getParam().contains("-")) {
			
			paramsMap.put(param.getParam().split("-")[0], param.getValue().split("-")[0]);
			paramsMap.put(param.getParam().split("-")[1], param.getValue().split("-")[1]);
			
		}else {
			paramsMap.put(param.getParam(), param.getValue());
		}
		
	}
	
	
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	String response="";
	
	String responseColumnInfo ="";
	
	
	BufferedWriter csvBw = null;
	
	boolean columnsPrepared = false;

	SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
	Date date = new Date();
	
	String filePath =Constants.getProperty("COMMON_REPORTS_PATH") ;
	String fileName = request.getReport_template()+""+formatter.format(date)+""+request.getFileType();
	
	ArrayList<String> rowHeader = new ArrayList<String>();
	ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
	
	
	
	
	try {
		
		boolean download = Boolean.parseBoolean(request.getDownload());
		String fileType = request.getFileType() ;
		
		if(download) {
        	if(fileType != null &&  fileType.equalsIgnoreCase(".CSV")) {
		csvBw = new BufferedWriter(new FileWriter(filePath + "" + fileName));
        	}
		}
		//String sqlSelect = "SELECT RPT_QUERY FROM REPORT_TEMPLATES WHERE RPT_ID = ?";
		String sqlSelect = "SELECT RTA.ACTION  \r\n" + 
				"FROM \r\n" + 
				"REPORT_TEMPLATES RT,\r\n" + 
				"REPORT_TEMPLATES_ACTION RTA\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"WHERE\r\n" + 
				"\r\n" + 
				"RT.RPT_ID= RTA.RPT_ID\r\n" + 
				"AND RT.RPT_ID = ? " + 
				"AND RTA.PANEL_ID = ? " ;
		
		pstmt = p_con.prepareStatement(sqlSelect);
		pstmt.setString(1, request.getReport_template());
		pstmt.setString(2, request.getActivePanelId());
		
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					
					String query = rs.getString(1);
					
					
					
					System.out.println("query  "+query);
					
					for(String key: paramsMap.keySet()) {
						query = query.replaceAll(":"+key, "'"+paramsMap.get(key)+"'");
					}
					
					System.out.println("query  "+query);
					
					pstmt2 = p_con.prepareStatement(query);
					try (ResultSet rs2 = pstmt2.executeQuery();) {
						
						
						ResultSetMetaData resultSetMetaData = rs2.getMetaData();
						final int columnCount = resultSetMetaData.getColumnCount();
						String responseInFile="";
						
						
						while (rs2.next()) {
							responseInFile = "";
							String[] values = new String[columnCount];
							response = response+" {";
							ArrayList<String> row = new ArrayList<String>();
						    for (int i = 1; i <= columnCount; i++) {
						        String value= rs2.getString(i);
						        
						        String columnsName = resultSetMetaData.getColumnName(i);
						        
						        response = response+"\""+columnsName+"\":\""+value+"\",";
						        
						        if(download) {
						        	if(fileType != null &&  fileType.equalsIgnoreCase(".CSV")) {
						        			responseInFile = responseInFile+value+",";
						        			
						        	}else  if(fileType != null &&  fileType.equalsIgnoreCase(".PDF")) {
					        			
					        			row.add(value);
								    } 
						        }
						        
						        if(columnsPrepared == false) {
						        responseColumnInfo = responseColumnInfo+"{\"title\":\""+columnsName+"\",";
						        responseColumnInfo = responseColumnInfo+"\"data\":\""+columnsName+"\"},";
						        	if(download) {
						        		rowHeader.add(columnsName);
						        	}
						        	}
						        }
						    
							if (fileType != null && fileType.equalsIgnoreCase(".PDF")) {

								rows.add(row);
							}
						    
						    
						    
						  //  rows
						        
						    //}
						    
						    
						    if(download) {
					        	if(fileType != null && fileType.equalsIgnoreCase(".CSV")) {
					        			csvBw.write(responseInFile);
					        			csvBw.newLine();
					        	}
					        }
						    response = response.substring(0,response.length()-1);
						    if(columnsPrepared == false) {
						    responseColumnInfo = ""+responseColumnInfo.substring(0,responseColumnInfo.length()-1)+"";
						    columnsPrepared=true;
						    }
						    
						    response = response+" },";
						}
						
						response = response.substring(0,response.length()-1);
						
						
						if(download) {

							if (fileType != null) {

								if (fileType.equalsIgnoreCase(".csv")) {
									csvBw.close();

									File fileNew = new File(filePath + "" + fileName);
									byte[] fileContent = FileUtils.readFileToByteArray(fileNew);

									return "{\"encodedBody\": \"" + Base64.getEncoder().encodeToString(fileContent)
											+ "\" , \"fileType\": \"" + fileType + "\", \"fileName\" : \"" + fileName
											+ "\" }";
								} else if (fileType.equalsIgnoreCase(".pdf")) {

								 	

								return "{\"encodedBody\": \"" + generatePDF(rowHeader, rows, filePath, fileName, fileType)
										+ "\" , \"fileType\": \"" + fileType + "\", \"fileName\" : \"" + fileName
										+ "\" }";
								
								
								}
							}

						}else {
						
							response = "{\r\n" + 
									"\r\n" + 
									"\"reportDetails\":["+response+"],"
											+ "\"columnsInfo\":["+responseColumnInfo+"]"
											+ ""
											+ " }";
							
							
						}
				        
				        
					}
				}
				
			
			}

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchCommonReport]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchCommonReport]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		try {
			if (csvBw != null) {
				csvBw.close();
			}
		} catch (Exception e) {
			LOG.error("An error occurred closing statement.", e);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting" );
		}
	}
	
	
	return response;

}










public String fetchCommonReportVariables(Connection p_con, CommonReportVariablesRequest request)

		throws BTSLBaseException {
	final String methodName = "fetchCommonReportVariables";
	StringBuffer msg=new StringBuffer("");
	
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	String response = "";

	HashMap<String, String> paramsMap = new HashMap<String, String>();

	for (Param param : request.getCommonParams()) {

		if (param.getParam() != null && param.getParam().contains("-")) {

			paramsMap.put(param.getParam().split("-")[0], param.getValue().split("-")[0]);
			paramsMap.put(param.getParam().split("-")[1], param.getValue().split("-")[1]);

		} else {
			paramsMap.put(param.getParam(), param.getValue());
		}

	}

	
	
	try {
		
		
		String sqlSelect = "SELECT VARIABLE_QRY FROM REPORT_TEMPLATES_VARIABLES WHERE VAR_ID = ? ";
			
		pstmt = p_con.prepareStatement(sqlSelect);
		pstmt.setString(1, request.getComponetsglobalId());
		
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					String query = rs.getString(1);
					
					for(String key: paramsMap.keySet()) {
						query = query.replaceAll(":"+key, "'"+paramsMap.get(key)+"'");
					}
					
					for(LOVList lovObj: request.getlOVList()) {
						query = query.replaceAll(":"+lovObj.getGlobalId(), "'"+lovObj.getCode()+"'");
					}
					
					
					pstmt2 = p_con.prepareStatement(query);
					
					response="{ \"LOVList\": [ ";
					try (ResultSet rs2 = pstmt2.executeQuery();) {
						while (rs2.next()) {
							String code = rs2.getString(1);
						
						String value = rs2.getString(2);
						
						response =  response +"{ \"code\": \""+code+"\",  \"value\":  \""+value+"\"},";
						}
						
						response = response.substring(0, response.lastIndexOf(","));
					}
					
					response= response+" ] } ";
				}
				
			
			}

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchCommonReport]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[fetchCommonReport]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting" );
		}
	}
	
	
	return response;

}




/**this method gives list of users within logged in users hierarchy 
	 * @param con
	 * @param domaincode
	 * @return
	 * @throws BTSLBaseException
	 */
public ArrayList<ChannelUserUnderParentVO> checkChannelUnderParentHierarchy(java.sql.Connection con,
		String channelUserLoginId, String parentUserId) throws BTSLBaseException {
	final String methodName = "checkChannelUnderParentHierarchy";
	ChannelUserUnderParentVO user = null;
	ArrayList<ChannelUserUnderParentVO> result = new ArrayList();
	StringBuffer msg = new StringBuffer("");
	if (LOG.isDebugEnabled()) {
		msg.append("get():: Entered checkChannelUnderParentHierarchy:");

		String message = msg.toString();
		LOG.debug(methodName, message);
	}
	PreparedStatement pstmtSelect = null;

	try {
		pstmtSelect = userQry.checkChannelUserUnderParent(con, channelUserLoginId, parentUserId);

		try (ResultSet rs = pstmtSelect.executeQuery();) {
			while (rs.next()) {
				user = new ChannelUserUnderParentVO();
				user.setParentUserName(rs.getString("USER_NAME"));
				user.setParentUserId(rs.getString("USER_ID"));
				user.setParentMsisdn(rs.getString("MSISDN"));
				user.setParentLoginId(rs.getString("LOGIN_ID"));
				user.setUserStatus(rs.getString("STATUS"));
				result.add(user);

			}

		}
	} // end of try
	catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[checkChannelUnderParentHierarchy]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR, sqle);
	} // end of catch
	catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[checkChannelUnderParentHierarchy]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR, e);
	} // end of catch
	finally {
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting userName:" + result);
		}
	}
	return result;
}


public int addStaffUserPhoneList(Connection p_con, ArrayList p_phoneList) throws SQLException, BTSLBaseException {
	final String methodName = "addStaffUserPhoneList";
	int insertCount = 0;
	int intex = 0;
	StringBuilder strBuff = new StringBuilder();

	strBuff.append("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
	strBuff.append("description,primary_number,sms_pin,pin_required,");
	strBuff.append("phone_profile,phone_language,country,invalid_pin_count,");
	strBuff.append("last_transaction_status,last_transaction_on,pin_modified_on,");
	strBuff.append("created_by,created_on,modified_by,modified_on, last_transfer_id,");
	strBuff.append(" prefix_id,last_transfer_type,pin_reset) values ");
	strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

	String insertQuery = strBuff.toString();
	StringBuilder loggerValue = new StringBuilder();
	if (LOG.isDebugEnabled()) {
		loggerValue.setLength(0);
		loggerValue.append(QUERY_KEY);
		loggerValue.append(insertQuery);
		LOG.debug(methodName, loggerValue);
	}

	UserPhoneVO userPhoneVO = null;

	try {
		for (int i = 0, j = p_phoneList.size(); i < j; i++) {
			userPhoneVO = (UserPhoneVO) p_phoneList.get(i);
			intex = 1;
			try (PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);) {

				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "Insert    userPhoneVO=          " + userPhoneVO);
				}

				psmtInsert.setString(intex++, userPhoneVO.getUserPhonesId());
				psmtInsert.setString(intex++, userPhoneVO.getMsisdn());
				psmtInsert.setString(intex++, userPhoneVO.getUserId());
				psmtInsert.setString(intex++, userPhoneVO.getDescription());
				psmtInsert.setString(intex++, userPhoneVO.getPrimaryNumber());
				psmtInsert.setString(intex++, userPhoneVO.getSmsPin());
				psmtInsert.setString(intex++, userPhoneVO.getPinRequired());
				psmtInsert.setString(intex++, userPhoneVO.getPhoneProfile());
				psmtInsert.setString(intex++, userPhoneVO.getPhoneLanguage());
				psmtInsert.setString(intex++, userPhoneVO.getCountry());
				psmtInsert.setInt(intex++, userPhoneVO.getInvalidPinCount());
				psmtInsert.setString(intex++, userPhoneVO.getLastTransactionStatus());
				if (userPhoneVO.getLastTransactionOn() != null) {
					psmtInsert.setTimestamp(intex++,
							BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getLastTransactionOn()));
				} else {
					psmtInsert.setTimestamp(intex++, null);
				}
				psmtInsert.setTimestamp(intex++, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getPinModifiedOn()));
				psmtInsert.setString(intex++, userPhoneVO.getCreatedBy());
				psmtInsert.setTimestamp(intex++, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getCreatedOn()));
				psmtInsert.setString(intex++, userPhoneVO.getModifiedBy());
				psmtInsert.setTimestamp(intex++, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getModifiedOn()));
				psmtInsert.setString(intex++, userPhoneVO.getLastTransferID());
				psmtInsert.setLong(intex++, userPhoneVO.getPrefixID());
				psmtInsert.setString(intex++, userPhoneVO.getLastTransferType());
				if (userPhoneVO.getPinReset() != null) {
                	psmtInsert.setString(intex++, userPhoneVO.getPinReset());
                } else {
                	psmtInsert.setString(intex++, PretupsI.YES);
                }
				insertCount = psmtInsert.executeUpdate();
				psmtInsert.clearParameters();
			}
			// check the status of the update
			if (insertCount <= 0) {
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}

		}

	} catch (BTSLBaseException be) {
		throw be;
	} catch (SQLException sqle) {
		loggerValue.setLength(0);
		loggerValue.append(SQL_EXCEPTION);
		loggerValue.append(sqle.getMessage());
		LOG.error(methodName, loggerValue);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[addStaffUserPhoneList]", "", "", "", loggerValue.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} // end of catch
	catch (Exception e) {
		loggerValue.setLength(0);
		loggerValue.append(EXCEPTION);
		loggerValue.append(e.getMessage());
		LOG.error(methodName, loggerValue);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[addStaffUserPhoneList]", "", "", "", loggerValue.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} // end of catch
	finally {
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting: addStaffUserPhoneList insertCount =" + insertCount);
		}
	}
	return insertCount;	
}






public int deleteUserPhoneList(Connection con, String userID) throws BTSLBaseException {
	int deleteCount = 0;
	UserVO userVO = null;
	final String methodName = "deleteUserPhoneList";
	if (LOG.isDebugEnabled()) {
		LOG.debug(methodName, "QUERY sqldelete");
	}
	try {
		// delete from USER_VOUCHERTYPES table
		StringBuilder strBuff = new StringBuilder("delete from USER_PHONES where user_id = ?");
		String deleteQuery = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Query sqlDelete:" + deleteQuery);
		}
		PreparedStatement psmtDelete5 = con.prepareStatement(deleteQuery);
		psmtDelete5.setString(1, userID);
		deleteCount = psmtDelete5.executeUpdate();
	} catch (SQLException sqe) {
		LOG.error(methodName, "SQLException : " + sqe);
		LOG.errorTrace(methodName, sqe);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
				EventLevelI.FATAL, "VomsProductDAO[deleteUserPhoneList]", "", "", "",
				"SQL Exception:" + sqe.getMessage());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
	} catch (Exception ex) {
		LOG.error(methodName, "Exception : " + ex);
		LOG.errorTrace(methodName, ex);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
				EventLevelI.FATAL, "VomsProductDAO[deleteUserPhoneList]", "", "", "",
				"Exception:" + ex.getMessage());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
	} finally {
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting: userVO=" + userVO);
		}
	}
	return deleteCount;
}



public int updatemsisdn(Connection con, String userID,String msisdn) throws BTSLBaseException {
	int updateCount = 0;
	UserVO userVO = null;
	final String methodName = "updatemsisdn";
	if (LOG.isDebugEnabled()) {
		LOG.debug(methodName, "QUERY sqldelete");
	}
	try {
		// Update user table table
		StringBuilder strBuff = new StringBuilder("update users set msisdn= ? where user_id = ?");
		String deleteQuery = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Query sqlDelete:" + deleteQuery);
		}
		PreparedStatement psmtDelete5 = con.prepareStatement(deleteQuery);
		psmtDelete5.setString(1, msisdn);
		psmtDelete5.setString(2, userID);
		updateCount = psmtDelete5.executeUpdate();
	} catch (SQLException sqe) {
		LOG.error(methodName, "SQLException : " + sqe);
		LOG.errorTrace(methodName, sqe);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
				EventLevelI.FATAL, "UserDAO[deleteUserPhoneList]", "", "", "",
				"SQL Exception:" + sqe.getMessage());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
	} catch (Exception ex) {
		LOG.error(methodName, "Exception : " + ex);
		LOG.errorTrace(methodName, ex);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
				EventLevelI.FATAL, "UserDAO[deleteUserPhoneList]", "", "", "",
				"Exception:" + ex.getMessage());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
	} finally {
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting: userVO=" + userVO);
		}
	}
	return updateCount;
}




/*
**
* Method loadUsersDetails.
* This method is used to load all the information used to display Operator
* User view
* 
* @param p_con
*            Connection
* @param loginID
*            String
* @return UserVO
* @throws BTSLBaseException
*/
public UserVO loadUsersDetailsfromLoginID(Connection p_con, String loginID) throws BTSLBaseException {
   final String methodName = "loadUsersDetailsfromLoginID";
   StringBuilder loggerValue= new StringBuilder();
   if (LOG.isDebugEnabled()) {
   	loggerValue.setLength(0);
   	loggerValue.append("Entered: loginID=");
   	loggerValue.append(loginID);
   	LOG.debug(methodName, loggerValue);
   }
   
  
   UserVO userVO = null;
   String sqlSelect = userQry.loadUsersDetailsQryFromLoginID();
   if(LOG.isDebugEnabled()){
		loggerValue.setLength(0);
		loggerValue.append(QUERY_KEY);
		loggerValue.append(sqlSelect);
		LOG.debug(methodName, loggerValue);
	}
   try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
      
       pstmtSelect.setString(1, loginID);
       try(ResultSet rs = pstmtSelect.executeQuery();)
       {
       if (rs.next()) {
           userVO = new ChannelUserVO();
           userVO.setUserID(rs.getString("usr_user_id"));
           userVO.setUserName(rs.getString("usr_user_name"));
           userVO.setNetworkID(rs.getString("network_code"));
           userVO.setLoginID(rs.getString("login_id"));
           userVO.setPassword(rs.getString("passwd"));
           userVO.setCategoryCode(rs.getString("usr_category_code"));
           userVO.setParentID(rs.getString("parent_id"));
           userVO.setOwnerID(rs.getString("owner_id"));
           userVO.setAllowedIps(rs.getString("allowed_ip"));
           userVO.setAllowedDays(rs.getString("allowed_days"));
           userVO.setFromTime(rs.getString("from_time"));
           userVO.setToTime(rs.getString("to_time"));
           userVO.setEmpCode(rs.getString("employee_code"));
           userVO.setStatus(rs.getString("usr_status"));
           userVO.setEmail(rs.getString("email"));
           // Added by deepika aggarwal
           userVO.setCompany(rs.getString("company"));
           userVO.setFax(rs.getString("fax"));
           userVO.setFirstName(rs.getString("firstname"));
           userVO.setLastName(rs.getString("lastname"));
           // end added by deepika aggarwal
           userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
           userVO.setContactNo(rs.getString("contact_no"));
           userVO.setDesignation(rs.getString("designation"));
           userVO.setDivisionCode(rs.getString("division"));
           userVO.setDepartmentCode(rs.getString("department"));
           userVO.setMsisdn(rs.getString("usr_msisdn"));
           userVO.setUserType(rs.getString("user_type"));
           userVO.setCreatedBy(rs.getString("created_by"));
           userVO.setCreatedOn(rs.getTimestamp("created_on"));
           userVO.setModifiedBy(rs.getString("modified_by"));
           userVO.setModifiedOn(rs.getTimestamp("modified_on"));
           userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
           userVO.setAddress1(rs.getString("address1"));
           userVO.setAddress2(rs.getString("address2"));
           userVO.setCity(rs.getString("city"));
           userVO.setState(rs.getString("state"));
           userVO.setCountry(rs.getString("country"));
           userVO.setSsn(rs.getString("ssn"));
           userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
           userVO.setExternalCode(rs.getString("external_code"));
           userVO.setShortName(rs.getString("short_name"));
           userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
           userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
           userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
           userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
           userVO.setUserCode(rs.getString("user_code"));
           userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));

           CategoryVO categoryVO = CategoryVO.getInstance();
           categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
           categoryVO.setCategoryName(rs.getString("category_name"));
           categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
           categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
           categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
           categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
           categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
           categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
           categoryVO.setFixedRoles(rs.getString("fixed_roles"));
           categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
           categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));

           userVO.setCategoryVO(categoryVO);

           userVO.setParentName(rs.getString("parent_name"));
           userVO.setParentMsisdn(rs.getString("parent_msisdn"));
           userVO.setParentCategoryName(rs.getString("parent_cat"));

           userVO.setOwnerName(rs.getString("owner_name"));
           userVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
           userVO.setOwnerCategoryName(rs.getString("owner_cat"));
          
           userVO.setDomainList(new UserDAO().loadDomainListByUserId(p_con,userVO.getUserID()));

       }
   }
   }catch (SQLException sqe) {
   	loggerValue.setLength(0);
		loggerValue.append(SQL_EXCEPTION);
		loggerValue.append(sqe.getMessage());
		LOG.error(methodName, loggerValue);
       LOG.errorTrace(methodName, sqe);
       EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "", loggerValue.toString());
       throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
   } catch (Exception ex) {
   	loggerValue.setLength(0);
		loggerValue.append(EXCEPTION);
		loggerValue.append(ex.getMessage());
		LOG.error(methodName, loggerValue);
       LOG.errorTrace(methodName, ex);
       EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "", loggerValue.toString());
       throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
   } 
   return userVO;
}



public  Long getTotalUsersUnderCategory(Connection p_con,String categoryCode) throws BTSLBaseException {
	
	
	final String methodName = "getTotalUsersUnderCategory";
	   StringBuilder loggerValue= new StringBuilder();
	   if (LOG.isDebugEnabled()) {
	   	loggerValue.setLength(0);
	   	loggerValue.append("Entered: categoryCode=");
	   	loggerValue.append(categoryCode);
	   	LOG.debug(methodName, loggerValue);
	   }
	   
	  
	   UserVO userVO = null;
	   Long recCount =0l;
	   String sqlSelect = "select count(*) as cnt from users where category_code =?";
	   if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
	   try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
	      
	       pstmtSelect.setString(1, categoryCode);
	       try(ResultSet rs = pstmtSelect.executeQuery();)
	       {
	       if (rs.next()) {
	    	   recCount= rs.getLong("cnt"); 
	       }
	           
	       }         
	       }catch (SQLException sqe) {
	    	   	loggerValue.setLength(0);
	    			loggerValue.append(SQL_EXCEPTION);
	    			loggerValue.append(sqe.getMessage());
	    			LOG.error(methodName, loggerValue);
	    	       LOG.errorTrace(methodName, sqe);
	    	       EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getTotalUsersUnderCategory]", "", "", "", loggerValue.toString());
	    	       throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	    	   } catch (Exception ex) {
	    	   	loggerValue.setLength(0);
	    			loggerValue.append(EXCEPTION);
	    			loggerValue.append(ex.getMessage());
	    			LOG.error(methodName, loggerValue);
	    	       LOG.errorTrace(methodName, ex);
	    	       EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[v]", "", "", "", loggerValue.toString());
	    	       throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	    	   }
	    
	
	return recCount;
	
}





public ChannelUserListResponseVO getChannelUsersList2(java.sql.Connection con, String userDomain,
		String userCategoryCode, String userGeography, String userId, String status, boolean selfAllowed,boolean onlyChannelUser)
		throws BTSLBaseException {
	final String methodName = "getChannelUsersList2";
	GetChannelUsersMsg getChannelUsersMsg;
	ArrayList<GetChannelUsersMsg> channelUsersList = new ArrayList<GetChannelUsersMsg>();
	final ChannelUserListResponseVO result = new ChannelUserListResponseVO();
	HashMap<String, GetChannelUsersMsg> resultMap = new HashMap<String, GetChannelUsersMsg>();
	StringBuffer msg = new StringBuffer("");
	if (LOG.isDebugEnabled()) {
		msg.append("get():: Entered getChannelUsersList2");

		String message = msg.toString();
		LOG.debug(methodName, message);
	}

	PreparedStatement pstmt = null;
	PreparedStatement pstmt1 = null;
	StringBuilder strBuffer = null;


	try {

		boolean tcpOn = false;
		Set<String> uniqueTransProfileId = new HashSet();

		String sqlSelect = null;
		HashMap<String, HashMap<String, String>> tcpMap = null;

		pstmt = userQry.getChannelUsersListQry2(con, userDomain, userCategoryCode, userGeography, userId, status,
				selfAllowed,onlyChannelUser);

		try (ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {

				if ((!selfAllowed && userId.equalsIgnoreCase(rs.getString("USER_ID")))
						|| rs.getString("STATUS").equals("C") || rs.getString("STATUS").equals("N")) {
					continue;
				} else {
					if (resultMap.get(rs.getString("USER_ID")) != null) {
						GetChannelUsersMsg object = resultMap.get(rs.getString("USER_ID"));

						boolean toBeAdded = true;

						for (BalanceVO balanceVO : object.getBalanceList()) {
							if (balanceVO.getProductName().equals(rs.getString("product_name"))) {
								toBeAdded = false;
								break;
							}
						}
						if (toBeAdded) {
							BalanceVO balanceVO = new BalanceVO();
							balanceVO
									.setBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
							balanceVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));

							object.getBalanceList().add(balanceVO);
						}

					} else {
						getChannelUsersMsg = new GetChannelUsersMsg();
						getChannelUsersMsg.setUserName(SqlParameterEncoder.encodeParams(rs.getString("USER_NAME")));
						getChannelUsersMsg.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("MSISDN")));
						getChannelUsersMsg.setUserType(SqlParameterEncoder.encodeParams(rs.getString("USER_TYPE")));

						if (rs.getString("BALANCE") != null) {
							
							List<BalanceVO> balanceList = new ArrayList<BalanceVO>();
							BalanceVO balanceVO = new BalanceVO();
							balanceVO
									.setBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
							balanceVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));

							balanceList.add(balanceVO);
							getChannelUsersMsg.setBalanceList(balanceList);
						}

						if (rs.getString("STATUS").equals("Y")) {
							getChannelUsersMsg.setStatus("Active");
						} else if (rs.getString("STATUS").equals("W")) {
							getChannelUsersMsg.setStatus("New");
						} else if (rs.getString("STATUS").equals("C")) {
							getChannelUsersMsg.setStatus("Cancelled");
						} else if (rs.getString("STATUS").equals("A")) {
							getChannelUsersMsg.setStatus("Approved");
						} else if (rs.getString("STATUS").equals("N")) {
							getChannelUsersMsg.setStatus("Deleted");
						} else if (rs.getString("STATUS").equals("S")) {
							getChannelUsersMsg.setStatus("Suspended");
						} else if (rs.getString("STATUS").equals("EX")) {
							getChannelUsersMsg.setStatus("Expired");
						} else if (rs.getString("STATUS").equals("D")) {
							getChannelUsersMsg.setStatus("Deregistered");
						} else if (rs.getString("STATUS").equals("B")) {
							getChannelUsersMsg.setStatus("Block");
						} else if (rs.getString("STATUS").equals("SR")) {
							getChannelUsersMsg.setStatus("Suspend Request");
						} else if (rs.getString("STATUS").equals("DR")) {
							getChannelUsersMsg.setStatus("Delete Request");
						} else if (rs.getString("STATUS").equals("BR")) {
							getChannelUsersMsg.setStatus("Bar Request");
						} else if (rs.getString("STATUS").equals("BD")) {
							getChannelUsersMsg.setStatus("Barred");
						} else if (rs.getString("STATUS").equals("BA")) {
							getChannelUsersMsg.setStatus("Bar Approve");
						} else if (rs.getString("STATUS").equals("CH")) {
							getChannelUsersMsg.setStatus("Churned");
						} else if (rs.getString("STATUS").equals("DE")) {
							getChannelUsersMsg.setStatus("Deactivated");
						} else if (rs.getString("STATUS").equals("PA")) {
							getChannelUsersMsg.setStatus("Pre Active");
						}

						getChannelUsersMsg.setStatusCode(rs.getString("STATUS"));

						getChannelUsersMsg.setDomain(SqlParameterEncoder.encodeParams(rs.getString("DOMAIN_NAME")));
						getChannelUsersMsg.setCategory(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
						getChannelUsersMsg
								.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));

						if (BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(rs.getString("parent_name")))) {
							getChannelUsersMsg.setParentName("ROOT");
						} else {
							getChannelUsersMsg
									.setParentName(SqlParameterEncoder.encodeParams(rs.getString("parent_name")));
						}
						getChannelUsersMsg.setOwnerName(SqlParameterEncoder.encodeParams(rs.getString("owner_name")));
						getChannelUsersMsg
								.setLastModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
						getChannelUsersMsg.setLastModifiedDateTime(rs.getTimestamp("modified_on"));
						if (!tcpOn) {
							getChannelUsersMsg.setTransactionProfile(
									SqlParameterEncoder.encodeParams(rs.getString("profile_name")));
						} else {

							getChannelUsersMsg.setTransactionProfile(SqlParameterEncoder
									.encodeParams(tcpMap.get(rs.getString("TRANSFER_PROFILE_ID")).get("Name")));

						}
						getChannelUsersMsg.setCommissionProfile(
								SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_name")));
						getChannelUsersMsg
								.setContactPerson(SqlParameterEncoder.encodeParams(rs.getString("contact_person")));
						getChannelUsersMsg.setLastTxnDatTime(rs.getTimestamp("last_transfer_on"));
						getChannelUsersMsg.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
						getChannelUsersMsg.setGrade(SqlParameterEncoder.encodeParams(rs.getString("user_grade")));
						getChannelUsersMsg.setRegisteredDateTime(rs.getTimestamp("created_on"));
						getChannelUsersMsg
								.setGeography(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_name")));
						getChannelUsersMsg.setParentID(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
						getChannelUsersMsg.setUserID(SqlParameterEncoder.encodeParams(rs.getString("USER_ID")));
						if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("modified_on"))) {
						getChannelUsersMsg.setLastModified(rs.getTimestamp("modified_on").toString());
						}
						if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("created_on"))) {
						getChannelUsersMsg.setRegistredDate(rs.getTimestamp("created_on").toString());
						}
						if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("last_transfer_on"))) {
						getChannelUsersMsg.setLastTransaction(rs.getTimestamp("last_transfer_on").toString());
						}
						channelUsersList.add(getChannelUsersMsg);
						resultMap.put(rs.getString("USER_ID"), getChannelUsersMsg);

					}
				}
			}
		}


		result.setChannelUsersList(channelUsersList);

	} catch (SQLException sqle) {
		msg.setLength(0);
		msg.append(SQL_EXCEPTION);
		msg.append(sqle.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
	} catch (Exception e) {
		msg.setLength(0);
		msg.append(EXCEPTION);
		msg.append(e.getMessage());
		LOG.error(methodName, msg);
		LOG.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
		throw new BTSLBaseException(this, methodName, "error.general.processing", e);
	} finally {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			LOG.error("An error occurred closing statement.", e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exiting userName:" + result);
		}
	}
	return result;

}

public String loadUserNamebyMsisdn(java.sql.Connection con, String msisdn) throws BTSLBaseException {
        final String methodName = "loadUserNamebyMsisdn";
        StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
            msg.append("loadUserName():: Entered with msisdn:");
            msg.append(msisdn);

            String message = msg.toString();
            LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        StringBuilder strBuffer = new StringBuilder("SELECT user_name FROM users WHERE msisdn = ? FETCH FIRST 1 ROWS ONLY");
        String userName = null;
        try {
            pstmt = con.prepareStatement(strBuffer.toString());
            pstmt.setString(1, msisdn);
            try(ResultSet rs = pstmt.executeQuery();)
            {
                if (rs.next()) {
                    userName = rs.getString("user_name");
                }
            }
        }// end of try
        catch (SQLException sqle) {
            msg.setLength(0);
            msg.append(SQL_EXCEPTION);
            msg.append(sqle.getMessage());
            LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserNamebyMsisdn]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            msg.setLength(0);
            msg.append(EXCEPTION);
            msg.append(e.getMessage());
            LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserNamebyMsisdn]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try{
                if (pstmt!= null){
                    pstmt.close();
                }
            }
            catch (SQLException e){
                LOG.error("An error occurred closing statement.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + userName);
            }
        }

        return userName;
    }

    public String loadUserNamebyLoginId(String loginId) throws BTSLBaseException {
        final String methodName = "loadUserNamebyLoginId";
        StringBuffer msg=new StringBuffer("");
        if (LOG.isDebugEnabled())
        {
            msg.append("loadUserName():: Entered with msisdn:");
            msg.append(loginId);

            String message = msg.toString();
            LOG.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        MComConnectionI mcomCon = null;
        Connection con = null;
        StringBuilder strBuffer = new StringBuilder("SELECT user_name FROM users WHERE login_id = ?");
        String userName = null;
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            pstmt = con.prepareStatement(strBuffer.toString());
            pstmt.setString(1, loginId);
            try(ResultSet rs = pstmt.executeQuery();)
            {
                if (rs.next()) {
                    userName = rs.getString("user_name");
                }
            }
        }// end of try
        catch (SQLException sqle) {
            msg.setLength(0);
            msg.append(SQL_EXCEPTION);
            msg.append(sqle.getMessage());
            LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserNamebyMsisdn]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            msg.setLength(0);
            msg.append(EXCEPTION);
            msg.append(e.getMessage());
            LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserNamebyMsisdn]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try{
                if (pstmt!= null){
                    pstmt.close();
                }
                if(mcomCon!=null){
                    mcomCon.close(methodName);
                }
            }
            catch (SQLException e){
                LOG.error("An error occurred closing statement.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + userName);
            }
        }

        return userName;
    }

    public ChannelUserListResponseVO getChannelUsersListCCE(java.sql.Connection con, String userDomain,
                                                            String userCategoryCode, String userGeography, String userId, String status, boolean selfAllowed,boolean onlyChannelUser)
            throws BTSLBaseException {
        final String methodName = "getChannelUsersList2";
        GetChannelUsersMsg getChannelUsersMsg;
        ArrayList<GetChannelUsersMsg> channelUsersList = new ArrayList<GetChannelUsersMsg>();
        final ChannelUserListResponseVO result = new ChannelUserListResponseVO();
        HashMap<String, GetChannelUsersMsg> resultMap = new HashMap<String, GetChannelUsersMsg>();
        StringBuffer msg = new StringBuffer("");
        if (LOG.isDebugEnabled()) {
            msg.append("get():: Entered getChannelUsersList2");

            String message = msg.toString();
            LOG.debug(methodName, message);
        }

        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        StringBuilder strBuffer = null;


        try {

            boolean tcpOn = false;
            Set<String> uniqueTransProfileId = new HashSet();

            String sqlSelect = null;
            HashMap<String, HashMap<String, String>> tcpMap = null;

            pstmt = userQry.getChannelUsersListQryCCE(con, userDomain, userCategoryCode, userGeography, userId, status,
                    selfAllowed,onlyChannelUser);

            try (ResultSet rs = pstmt.executeQuery();) {

                while (rs.next()) {

                    if ((!selfAllowed && userId.equalsIgnoreCase(rs.getString("USER_ID")))
                            || rs.getString("STATUS").equals("C") || rs.getString("STATUS").equals("N")) {
                        continue;
                    } else {
                        if (resultMap.get(rs.getString("USER_ID")) != null) {
                            GetChannelUsersMsg object = resultMap.get(rs.getString("USER_ID"));

                            boolean toBeAdded = true;

                            for (BalanceVO balanceVO : object.getBalanceList()) {
                                if (balanceVO.getProductName().equals(rs.getString("product_name"))) {
                                    toBeAdded = false;
                                    break;
                                }
                            }
                            if (toBeAdded) {
                                BalanceVO balanceVO = new BalanceVO();
                                balanceVO
                                        .setBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
                                balanceVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));

                                object.getBalanceList().add(balanceVO);
                            }

                        } else {
                            getChannelUsersMsg = new GetChannelUsersMsg();
                            getChannelUsersMsg.setUserName(SqlParameterEncoder.encodeParams(rs.getString("USER_NAME")));
                            getChannelUsersMsg.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("MSISDN")));
                            getChannelUsersMsg.setUserType(SqlParameterEncoder.encodeParams(rs.getString("USER_TYPE")));

                            if (rs.getString("BALANCE") != null) {

                                List<BalanceVO> balanceList = new ArrayList<BalanceVO>();
                                BalanceVO balanceVO = new BalanceVO();
                                balanceVO
                                        .setBalance(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("BALANCE"))));
                                balanceVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));

                                balanceList.add(balanceVO);
                                getChannelUsersMsg.setBalanceList(balanceList);
                            }

                            if (rs.getString("STATUS").equals("Y")) {
                                getChannelUsersMsg.setStatus("Active");
                            } else if (rs.getString("STATUS").equals("W")) {
                                getChannelUsersMsg.setStatus("New");
                            } else if (rs.getString("STATUS").equals("C")) {
                                getChannelUsersMsg.setStatus("Cancelled");
                            } else if (rs.getString("STATUS").equals("A")) {
                                getChannelUsersMsg.setStatus("Approved");
                            } else if (rs.getString("STATUS").equals("N")) {
                                getChannelUsersMsg.setStatus("Deleted");
                            } else if (rs.getString("STATUS").equals("S")) {
                                getChannelUsersMsg.setStatus("Suspended");
                            } else if (rs.getString("STATUS").equals("EX")) {
                                getChannelUsersMsg.setStatus("Expired");
                            } else if (rs.getString("STATUS").equals("D")) {
                                getChannelUsersMsg.setStatus("Deregistered");
                            } else if (rs.getString("STATUS").equals("B")) {
                                getChannelUsersMsg.setStatus("Block");
                            } else if (rs.getString("STATUS").equals("SR")) {
                                getChannelUsersMsg.setStatus("Suspend Request");
                            } else if (rs.getString("STATUS").equals("DR")) {
                                getChannelUsersMsg.setStatus("Delete Request");
                            } else if (rs.getString("STATUS").equals("BR")) {
                                getChannelUsersMsg.setStatus("Bar Request");
                            } else if (rs.getString("STATUS").equals("BD")) {
                                getChannelUsersMsg.setStatus("Barred");
                            } else if (rs.getString("STATUS").equals("BA")) {
                                getChannelUsersMsg.setStatus("Bar Approve");
                            } else if (rs.getString("STATUS").equals("CH")) {
                                getChannelUsersMsg.setStatus("Churned");
                            } else if (rs.getString("STATUS").equals("DE")) {
                                getChannelUsersMsg.setStatus("Deactivated");
                            } else if (rs.getString("STATUS").equals("PA")) {
                                getChannelUsersMsg.setStatus("Pre Active");
                            }

                            getChannelUsersMsg.setStatusCode(rs.getString("STATUS"));

                            getChannelUsersMsg.setDomain(SqlParameterEncoder.encodeParams(rs.getString("DOMAIN_NAME")));
                            getChannelUsersMsg.setCategory(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                            getChannelUsersMsg
                                    .setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));

                            if (BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(rs.getString("parent_name")))) {
                                getChannelUsersMsg.setParentName("ROOT");
                            } else {
                                getChannelUsersMsg
                                        .setParentName(SqlParameterEncoder.encodeParams(rs.getString("parent_name")));
                            }
                            getChannelUsersMsg.setOwnerName(SqlParameterEncoder.encodeParams(rs.getString("owner_name")));
                            getChannelUsersMsg
                                    .setLastModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                            getChannelUsersMsg.setLastModifiedDateTime(rs.getTimestamp("modified_on"));
                            if (!tcpOn) {
                                getChannelUsersMsg.setTransactionProfile(
                                        SqlParameterEncoder.encodeParams(rs.getString("profile_name")));
                            } else {

                                getChannelUsersMsg.setTransactionProfile(SqlParameterEncoder
                                        .encodeParams(tcpMap.get(rs.getString("TRANSFER_PROFILE_ID")).get("Name")));

                            }
                            getChannelUsersMsg.setCommissionProfile(
                                    SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_name")));
                            getChannelUsersMsg
                                    .setContactPerson(SqlParameterEncoder.encodeParams(rs.getString("contact_person")));
                            getChannelUsersMsg.setLastTxnDatTime(rs.getTimestamp("last_transfer_on"));
                            getChannelUsersMsg.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
                            getChannelUsersMsg.setGrade(SqlParameterEncoder.encodeParams(rs.getString("user_grade")));
                            getChannelUsersMsg.setRegisteredDateTime(rs.getTimestamp("created_on"));
                            getChannelUsersMsg
                                    .setGeography(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_name")));
                            getChannelUsersMsg.setParentID(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
                            getChannelUsersMsg.setUserID(SqlParameterEncoder.encodeParams(rs.getString("USER_ID")));
                            if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("modified_on"))) {
                                getChannelUsersMsg.setLastModified(rs.getTimestamp("modified_on").toString());
                            }
                            if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("created_on"))) {
                                getChannelUsersMsg.setRegistredDate(rs.getTimestamp("created_on").toString());
                            }
                            if(!BTSLUtil.isNullorEmpty(rs.getTimestamp("last_transfer_on"))) {
                                getChannelUsersMsg.setLastTransaction(rs.getTimestamp("last_transfer_on").toString());
                            }
                            channelUsersList.add(getChannelUsersMsg);
                            resultMap.put(rs.getString("USER_ID"), getChannelUsersMsg);

                        }
                    }
                }
            }


            result.setChannelUsersList(channelUsersList);

        } catch (SQLException sqle) {
            msg.setLength(0);
            msg.append(SQL_EXCEPTION);
            msg.append(sqle.getMessage());
            LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
        } catch (Exception e) {
            msg.setLength(0);
            msg.append(EXCEPTION);
            msg.append(e.getMessage());
            LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing", e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing statement.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userName:" + result);
            }
        }
        return result;

    }

    /**
     * Method :loadUserIdFromCreatedByField
     * This method load cratedy by from Batches table.
     *
     * @param con
     *            java.sql.Connection
     * @param userId
     *            java.lang.String
     * @return UserVO
     * @throws BTSLBaseException
     */

    public UserVO loadUserDetailsFromUserId(Connection con, String userId) throws BTSLBaseException {
        final String methodName = "loadUserDetailsFromUserId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: userId=" + userId);
        }

        ResultSet rs = null;
        UserVO userVO = null;
        final StringBuffer strBuff = new StringBuffer("SELECT U.category_code, U.user_type FROM users U");
        strBuff.append(" WHERE U.user_id=? ");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelectRoles=" + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);){

            int i = 1;
            pstmtSelect.setString(i++, userId);
            rs = pstmtSelect.executeQuery();


            while (rs.next()) {
                userVO = new UserVO();
                userVO.setCategoryCode(rs.getString("category_code"));
                userVO.setUserType(rs.getString("user_type"));
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                    "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
        return userVO;
    }
}







