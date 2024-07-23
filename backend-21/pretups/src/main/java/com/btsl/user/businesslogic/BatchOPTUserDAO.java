package com.btsl.user.businesslogic;

/*
 * @# BatchOPTUserDAO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Shishupal Singh Mar 19, 2007 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 * This class use for batch user creation.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.IDGenerator;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchesLog;
import com.btsl.pretups.logging.OperatorUserLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.user.web.BatchOPTUserForm;

/**
 * @author shishupal.singh
 * 
 */
public class BatchOPTUserDAO {
    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private static final Log log = LogFactory.getLog(BatchOPTUserDAO.class.getName());

    /**
     * Method for loading Division List.
     * Used in(Users Action)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_divDeptType
     *            String
     * @param p_status
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     * @author Shishupal Singh
     */
    public ArrayList loadDivisionDeptList(Connection p_con, String p_divDeptType, String p_status) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_divDeptType=");
        	loggerValue.append(p_divDeptType);
        	loggerValue.append(" p_status=");
        	loggerValue.append(p_status);
        	log.debug("loadDivisionDeptList", "Entered p_divDeptType=" + p_divDeptType + " p_status=" + p_status);
        }
        final String METHOD_NAME = "loadDivisionDeptList";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT D1.divdept_id DIVDEPT_ID, D1.divdept_name DIVDEPT_NAME, D1.parent_id PARENT_ID, ");
        strBuff.append(" D1.STATUS,D2.divdept_name PARENT_NAME FROM division_department D1,division_department D2 ");
        strBuff.append(" where D2.divdept_id=D1.parent_id AND D1.status = ? and D1.DIVDEPT_TYPE=? ");
        strBuff.append(" ORDER BY D1.divdept_name");

        if (log.isDebugEnabled()) {
            log.debug("loadDivisionDeptList", "QUERY sqlSelect=" + strBuff.toString());
        }
        ArrayList batchOPTUserVOList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(1, p_status);
            pstmt.setString(2, p_divDeptType);
            rs = pstmt.executeQuery();
            BatchOPTUserVO batchOPTUserVO = null;
            while (rs.next()) {
                batchOPTUserVO = new BatchOPTUserVO();
                batchOPTUserVO.setDivdeptName(SqlParameterEncoder.encodeParams(rs.getString("DIVDEPT_NAME")));
                batchOPTUserVO.setDivdeptID(SqlParameterEncoder.encodeParams(rs.getString("DIVDEPT_ID")));
                batchOPTUserVO.setParentID(SqlParameterEncoder.encodeParams(rs.getString("PARENT_ID")));
                batchOPTUserVO.setParentName("PARENT_NAME");
                batchOPTUserVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("STATUS")));
                batchOPTUserVOList.add(batchOPTUserVO);
            }

        } catch (SQLException sqe) {
            log.error("loadDivisionDeptList", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[loadDivisionDeptList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadDivisionDeptList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error("loadDivisionDeptList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[loadDivisionDeptList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadDivisionDeptList", "error.general.processing",ex);
        } finally {
            
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("loadDivisionDeptList", "Exiting: divisionDeptList size=" + batchOPTUserVOList.size());
            }
        }
        return batchOPTUserVOList;
    }

    /**
     * Method :loadGeographyList
     * Method for loading Division List.
     * Used in(Users Action)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_divDeptType
     *            String
     * @param p_status
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     * @author Shishupal Singh
     */
    public ArrayList loadGeographyList(Connection con, BatchOPTUserForm theForm, UserVO userSessionVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadGeographyList", "Entered");
        }
        ArrayList geographyList = null;
        ArrayList<UserGeographiesVO> networkList =null;
        final String METHOD_NAME = "loadGeographyList";
        try {
            GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            
            /*
             * 1)if graph_domain_type of the session user and added user are
             * same
             * then the geography list of the new user = session user
             * geographies list
             * 2)if sequence no is 1 means category is Network Admin
             * load the session network details
             * 3)load the list of all geographies on the basis of parent domain
             * code
             * 4)need to perform search, so prepare the list for search
             */
            // 1
            if (userSessionVO.getCategoryVO().getGrphDomainType().equals(theForm.getCategoryVO().getGrphDomainType())) {
            	//the case when we are adding operator users in batch: super network admin or super customer care from super admin. Instead of assigning geographies , networks are assigned.
            	if((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(theForm.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(theForm.getCategoryCode())))
                {
                	networkList = geographicalDomainWebDAO.loadNetworkList(con);
                	theForm.setNetworkList(networkList);
                	
                }
            	else if(TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(theForm.getCategoryCode()))
            	{
            		//the case when we are adding operator user in batch: super channel admin from super admin. Geographies are assigned irrespective of network code
            		 geographyList = geographicalDomainWebDAO.loadGeographyListForSuperChannelAdmin(con,theForm.getCategoryVO().getGrphDomainType());
            		 theForm.setGeographicalList(geographyList);
            		 
            		 if (geographyList != null && !geographyList.isEmpty())
            		 {
                      /*
                       * set the grphDoaminTypeName on the form GrphDomainTypeName
                       * is same for all VO's in list
                       */
            			 UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
            			 theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
            		 }
            	}
            	else
            	{
                geographyList = userSessionVO.getGeographicalAreaList();
                theForm.setGeographicalList(geographyList);
                if (geographyList != null && !geographyList.isEmpty()) {
                    /*
                     * set the grphDoaminTypeName on the form
                     * GrphDomainTypeName is same for all VO's in list
                     */
                    UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
                    theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                }
            	}
            }
            // 2
            else if (theForm.getCategoryVO().getGrphDomainSequenceNo() == 1) {
                UserGeographiesVO geographyVO = null;
                geographyList = new ArrayList();
                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(userSessionVO.getNetworkID());
                geographyVO.setGraphDomainName(userSessionVO.getNetworkName());
                geographyVO.setGraphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
                theForm.setGrphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
                geographyList.add(geographyVO);

                theForm.setGeographicalList(geographyList);
            }
            // 3
            if ((userSessionVO.getCategoryVO().getGrphDomainSequenceNo() + 1) == theForm.getCategoryVO().getGrphDomainSequenceNo()) {
                geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), theForm.getParentDomainCode(), "%");
                theForm.setGeographicalList(geographyList);
                if (geographyList != null && !geographyList.isEmpty()) {
                    /*
                     * set the grphDoaminTypeName on the form
                     * GrphDomainTypeName is same for all VO's in list
                     */
                    UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
                    theForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                }
            }
            // 4
            else {
                ArrayList list = geographicalDomainWebDAO.loadDomainTypes(con, userSessionVO.getCategoryVO().getGrphDomainSequenceNo(), theForm.getCategoryVO().getGrphDomainSequenceNo());
                if (list != null && !list.isEmpty()) {
                    theForm.setDomainSearchList(list);
                    theForm.setSearchDomainTextArrayCount();
                    theForm.setSearchDomainCodeCount();
                }
            }

        } catch (Exception ex) {
            log.error("loadGeographyList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[geographyList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadGeographyList", "error.general.processing",ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("loadGeographyList", "Exiting: geographyList ");
            }
        }
        if(!((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(theForm.getCategoryCode())) || (TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(theForm.getCategoryCode()))))
        return geographyList;
        else
        return networkList;
    }

    /**
     * Method :addOperatorUserList
     * This method check the data base validation of initiate channel user
     * and after validation insert into channel user related tables.
     * 
     * @param p_con
     *            Connection
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @param p_userVO
     *            TODO
     * @param p_userDetailList
     *            ArrayList
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Shishupal Singh
     */
    public ArrayList addOperatorUserList(Connection p_con, ArrayList p_operatorUserDetailList, MessageResources p_messages, Locale p_locale, UserVO p_userVO, String p_fileName) throws BTSLBaseException {
    	final String METHOD_NAME = "addOperatorUserList";
    	if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userDetailList.size()=");
        	loggerValue.append(p_operatorUserDetailList.size());
        	loggerValue.append(" p_messages=");
        	loggerValue.append(p_messages);
        	loggerValue.append(" p_locale=");
        	loggerValue.append(p_locale);
        	loggerValue.append(" p_fileName: ");
        	loggerValue.append(p_fileName);
            log.debug(METHOD_NAME,loggerValue );
        }
        
        boolean batchIdFlag = true;
        String batchID = null;
        int commitCounter = 0, updateCount = 0;
        long idCounter = 0;
        int commitNumber = 0;
        int userPaddingLength = 0;
        // Email for pin & password
        BTSLMessages btslPushMessage = null;
        PushMessage pushMessage = null;
        String subject = null;
        EmailSendToUser emailSendToUser = null;
        try {
            commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            commitNumber = 100;
        }
        try {
            userPaddingLength = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            userPaddingLength = 10;
        }
        IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
        OperatorUtilI operatorUtil = null;
        ArrayList dbErrorList = new ArrayList();
        ListValueVO errorVO = null;
        StringBuilder strBuff = null;
        PreparedStatement pstmtSelectLoginID = null;
        PreparedStatement pstmtSelectExternalCode = null;
        PreparedStatement psmtBatchInsert = null;
        PreparedStatement psmtBatchUpdate = null;
        // commented for DB2OraclePreparedStatement psmtUsersInsert = null;
        PreparedStatement psmtUsersInsert = null;
        PreparedStatement psmtGeographiesInsert = null;
        PreparedStatement psmtRolesInsert = null;
        PreparedStatement psmtDomainInsert = null;
        PreparedStatement psmtProductInsert = null;
        PreparedStatement psmtBatchGeographyInsert = null;
        PreparedStatement psmtUserVoucherTypeInsert = null;
        PreparedStatement psmtUserVoucherSegmentInsert = null;
        

        ResultSet rsSelectLoginID = null;
        ResultSet rsUserMsisdn = null;
        ResultSet rsExternalCode = null;
        int insertUsersCount = 0, insertGeographiesCount = 0, insertRolesCount = 0, insertDomainCount = 0, insertProductCount = 0, insertUserVoucherType=0, insertUserVoucherSegment=0;
        try {
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[addChannelUserList]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
            }
            // Email for pin & password
            Locale defaultLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            // ========================Unique check for login id
            strBuff = new StringBuilder("SELECT 1 FROM users U WHERE U.login_id=? ");
            String selectLoginIDQuery = strBuff.toString();
            pstmtSelectLoginID = p_con.prepareStatement(selectLoginIDQuery);

            // =========================Unique check for external code
            StringBuilder selectExternalCode = new StringBuilder("SELECT 1 FROM users WHERE external_code= ?");
            String selecExternalCodeQuery = selectExternalCode.toString();
            pstmtSelectExternalCode = p_con.prepareStatement(selecExternalCodeQuery);

            // batches insert
            strBuff = new StringBuilder("INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, ");
            strBuff.append("network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
            strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            String insertBatchQuery = strBuff.toString();
            psmtBatchInsert = p_con.prepareStatement(insertBatchQuery);

            // update batches table
            strBuff = new StringBuilder("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
            String updateBatchQuery = strBuff.toString();
            psmtBatchUpdate = p_con.prepareStatement(updateBatchQuery);

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO users (user_id,user_name,network_code,login_id,password,category_code,");
            strBuff.append("parent_id,owner_id,allowed_ip,allowed_days,from_time,to_time,employee_code,status,email,");
            strBuff.append("contact_no,designation,division,department,msisdn,user_type,created_by,created_on,");
            strBuff.append("modified_by,modified_on,address1,address2,city,state,country,ssn,user_name_prefix, ");
            strBuff.append("external_code,short_name,user_code,appointment_date,previous_status, creation_type, ");
            strBuff.append(" batch_id,firstname,lastname,authentication_allowed) ");// fname,lname
                                                                                    // added
                                                                                    // by
                                                                                    // deepika
                                                                                    // aggarwal
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertUsersQuery = strBuff.toString();
            psmtUsersInsert = p_con.prepareStatement(insertUsersQuery);
            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_geographies (user_id,");
            strBuff.append("grph_domain_code)");
            strBuff.append(" values (?,?)");
            String insertGeographiesQuery = strBuff.toString();
            psmtGeographiesInsert = p_con.prepareStatement(insertGeographiesQuery);

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_roles (user_id,");
            strBuff.append("role_code) values (?,?)");
            String insertRolesQuery = strBuff.toString();
            psmtRolesInsert = p_con.prepareStatement(insertRolesQuery);

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_domains (user_id,");
            strBuff.append("domain_code) values (?,?)");
            String insertDomainQuery = strBuff.toString();
            psmtDomainInsert = p_con.prepareStatement(insertDomainQuery);

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_product_types (user_id,");
            strBuff.append("product_type) values (?,?)");
            String insertProductQuery = strBuff.toString();
            psmtProductInsert = p_con.prepareStatement(insertProductQuery);

            // Batch geographies insert
            strBuff = new StringBuilder("INSERT INTO batch_geographies (batch_id, geography_code) ");
            strBuff.append(" VALUES(?,?)");
            String insertBatchGeographyQuery = strBuff.toString();
            psmtBatchGeographyInsert = p_con.prepareStatement(insertBatchGeographyQuery);

            // insert into user voucher type
            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO USER_VOUCHERTYPES (user_id,");
            strBuff.append("VOUCHER_TYPE)");
            strBuff.append(" values (?,?)");
            String insertUserVoucherTypeQuery = strBuff.toString();
            psmtUserVoucherTypeInsert = p_con.prepareStatement(insertUserVoucherTypeQuery);
            
            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO USER_VOUCHER_SEGMENTS (user_id,");
            strBuff.append("VOUCHER_SEGMENT)");
            strBuff.append(" values (?,?)");
            String insertUserVoucherSegmentQuery = strBuff.toString();
            psmtUserVoucherSegmentInsert = p_con.prepareStatement(insertUserVoucherSegmentQuery);
            
            
            
            Iterator batchOPTUserVOListItr = p_operatorUserDetailList.iterator();

            BatchOPTUserVO batchOPTUserVO = null;
            ArrayList geographyList = new ArrayList();
            Start: while (batchOPTUserVOListItr.hasNext()) {
                batchOPTUserVO = (BatchOPTUserVO) batchOPTUserVOListItr.next();
                batchOPTUserVO.setBatchID(batchID);

                // ====================== Validation 1: Check for login_id
                // uniqueness, if login id already exists mark error.
                pstmtSelectLoginID.setString(1, batchOPTUserVO.getLoginID());
                rsSelectLoginID = pstmtSelectLoginID.executeQuery();
                if (rsSelectLoginID.next()) {
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.loginiduniqueerr", new String[] { batchOPTUserVO.getLoginID() }));
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.loginiduniqueerr", batchOPTUserVO.getLoginID()));
                    continue Start;
                }
                // ====================== end login_id validation here

                // ======================Validation 5: Check for the uniqueness
                // of the External code (if given)
                if (!BTSLUtil.isNullString(batchOPTUserVO.getExternalCode())) {
                    pstmtSelectExternalCode.setString(1, batchOPTUserVO.getExternalCode().trim());
                    rsExternalCode = pstmtSelectExternalCode.executeQuery();
                    if (rsExternalCode.next()) {
                        errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.externalcodeuniqueerr", new String[] { batchOPTUserVO.getExternalCode() }));
                        dbErrorList.add(errorVO);
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.externalcodeuniqueerr", batchOPTUserVO.getExternalCode()));
                        continue Start;
                    }
                }
                // ====================== end external code validation here

                if (batchIdFlag) {
                    // one time entry into batches table
                    batchID = operatorUtil.formatBatchesID(batchOPTUserVO.getNetworkID(), PretupsI.BATCH_OPT_USR_ID_PREFIX, new Date(), IDGenerator.getNextID(PretupsI.BATCH_OPT_USR_ID_PREFIX, BTSLUtil.getFinancialYear(), batchOPTUserVO.getNetworkID()));
                    psmtBatchInsert.setString(1, batchID);
                    psmtBatchInsert.setString(2, PretupsI.BATCH_OPT_USR_BATCH_TYPE);
                    psmtBatchInsert.setInt(3, p_operatorUserDetailList.size());
                    psmtBatchInsert.setString(4, batchOPTUserVO.getBatchName());
                    psmtBatchInsert.setString(5, batchOPTUserVO.getNetworkID());
                    psmtBatchInsert.setString(6, PretupsI.USR_BATCH_STATUS_UNDERPROCESS);
                    psmtBatchInsert.setString(7, batchOPTUserVO.getCreatedBy());
                    psmtBatchInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(batchOPTUserVO.getCreatedOn()));
                    psmtBatchInsert.setString(9, batchOPTUserVO.getModifiedBy());
                    psmtBatchInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(batchOPTUserVO.getModifiedOn()));
                    psmtBatchInsert.setString(11, p_fileName);
                    if (psmtBatchInsert.executeUpdate() <= 0) {
                        p_con.rollback();
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.err.batchnotcreated"));
                        throw new BTSLBaseException(this, "addOperatorUserList", "user.batchoptuser.processuploadedfile.err.batchnotcreated", "selectCategory");
                    }
                    batchIdFlag = false;
                    // First time make the user_id
                    idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCreatedOn());
                }

                if (commitCounter > commitNumber)// After 100 record commit the
                                                 // records
                {
                    // after 100 records update the last_no of the ids table for
                    // user_id
                    idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCreatedOn(), idCounter - 1);
                    p_con.commit();
                    // after 100 records pick the last_no from the ids table for
                    // user_id
                    idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCreatedOn());
                    commitCounter = 0;// reset commit counter
                }
                batchOPTUserVO.setUserID(this.generateUserId(batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCategoryVO().getUserIdPrefix(), idCounter, userPaddingLength));
                batchOPTUserVO.setBatchID(batchID);

                // insert user info
                insertUsersCount = this.addOperatorUser(psmtUsersInsert, batchOPTUserVO);
                if (insertUsersCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() }));
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.userinsertfail", batchOPTUserVO.getUserName()));
                    continue Start;
                }

                // insert geography info
                insertGeographiesCount = this.addUserGeographyList(psmtGeographiesInsert, batchOPTUserVO);
                if (insertGeographiesCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() }));
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.userinsertfail", batchOPTUserVO.getUserName()));
                    continue Start;
                }// ==============================================================================================
                else// make the array list of geography code
                {
                    boolean flag = false;
                    Iterator geographyListItr = null;
                    for (int i = 0; i < batchOPTUserVO.getGeographyArrList().length; i++) {

                        // =============================
                        flag = false;
                        geographyListItr = geographyList.iterator();
                        while (geographyListItr.hasNext()) {
                            if (((String) geographyListItr.next()).equalsIgnoreCase(batchOPTUserVO.getGeographyArrList()[i])) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag == false) {
                            geographyList.add(batchOPTUserVO.getGeographyArrList()[i]);
                            // =============================
                        }
                    }
                }
                // ==============================================================================================

                // insert roles info
                insertRolesCount = this.addUserRolesList(psmtRolesInsert, batchOPTUserVO);
                if (insertRolesCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() }));
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.userinsertfail", batchOPTUserVO.getUserName()));
                    continue Start;
                }

                // insert domain info
                if (batchOPTUserVO.getCategoryVO().getDomainAllowed().equalsIgnoreCase(PretupsI.YES) && PretupsI.DOMAINS_ASSIGNED.equals(batchOPTUserVO.getCategoryVO().getFixedDomains())) {
                    insertDomainCount = this.addUserDomainList(psmtDomainInsert, batchOPTUserVO);
                    if (insertDomainCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() }));
                        dbErrorList.add(errorVO);
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.userinsertfail", batchOPTUserVO.getUserName()));
                        continue Start;
                    }
                }

                // insert product info
                if (batchOPTUserVO.getCategoryVO().getProductTypeAllowed().equalsIgnoreCase(PretupsI.YES)) {
                    insertProductCount = this.addUserProductList(psmtProductInsert, batchOPTUserVO);
                    if (insertProductCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() }));
                        dbErrorList.add(errorVO);
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.userinsertfail", batchOPTUserVO.getUserName()));
                        continue Start;
                    }
                }
                
                
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
                {
             // insert user voucher type info
                insertUserVoucherType = this.addVoucherTypeList(psmtUserVoucherTypeInsert, batchOPTUserVO);
                if (insertUserVoucherType < 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() }));
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.userinsertfail", batchOPTUserVO.getUserName()));
                    continue Start;
                }
                
                }
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
	                insertUserVoucherSegment = this.addSegmentList(psmtUserVoucherSegmentInsert, batchOPTUserVO);
	                if (insertUserVoucherSegment < 0) {
	                    p_con.rollback();
	                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() }));
	                    dbErrorList.add(errorVO);
	                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + p_messages.getMessage("user.batchoptuser.processuploadedfile.msg.error.userinsertfail", batchOPTUserVO.getUserName()));
	                    continue Start;
	                }
                }
                
                commitCounter++;
                idCounter++;
                updateCount++;
                // Email for pin & password- push the message
                if (batchOPTUserVO.getStatus().equalsIgnoreCase(PretupsI.YES)) {
                    String[] arrArray = { batchOPTUserVO.getLoginID(), "", BTSLUtil.decryptText(batchOPTUserVO.getPassword()) };
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                    if (!BTSLUtil.isNullString(batchOPTUserVO.getMsisdn())) {
                        pushMessage = new PushMessage(batchOPTUserVO.getMsisdn(), btslPushMessage, "", "", defaultLocale, p_userVO.getNetworkID(), "SMS will be delivered shortly");
                        pushMessage.push();
                    }
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(batchOPTUserVO.getEmail())) {
                        subject = p_messages.getMessage(p_locale, "subject.user.reg.bulk.massage", batchOPTUserVO.getUserName());
                        emailSendToUser = new EmailSendToUser(subject, btslPushMessage, p_locale, p_userVO.getNetworkID(), "Email will be delivered shortly", batchOPTUserVO, p_userVO);
                        emailSendToUser.sendMail();
                    }
                }
                OperatorUserLog.log("BATCH_OPT_USR_INITIATE", batchOPTUserVO, p_userVO, "Add Operator user from bulk");
            } // end while loop
            if (updateCount > 0) {
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCreatedOn(), idCounter - 1);
                Iterator geographyListItr = geographyList.iterator();
                while (geographyListItr.hasNext()) {
                    psmtBatchGeographyInsert.clearParameters();
                    psmtBatchGeographyInsert.setString(1, batchID);
                    psmtBatchGeographyInsert.setString(2, ((String) geographyListItr.next()).toUpperCase());
                    if (psmtBatchGeographyInsert.executeUpdate() <= 0) {
                        p_con.rollback();
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=When inserting batch_geographics table");
                        continue;
                    }
                }
                // update batches table for updating updateCount on the batch
                // size
                psmtBatchUpdate.setInt(1, updateCount);
                psmtBatchUpdate.setString(2, PretupsI.USR_BATCH_STATUS_CLOSE);
                psmtBatchUpdate.setString(3, batchID);
                psmtBatchUpdate.executeUpdate();
                // ==================================================================================================
                p_con.commit();
            } else {
                p_con.rollback();
            }
            errorVO = new ListValueVO("BATCHID", "", batchID);
            dbErrorList.add(errorVO);

        } // end of try
        catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error("addOperatorUserList", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addOperatorUserList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addOperatorUserList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error("addOperatorUserList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addOperatorUserList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "addOperatorUserList", "error.general.processing",ex);
        } finally {
            try {
                if (pstmtSelectLoginID != null) {
                    pstmtSelectLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectExternalCode != null) {
                    pstmtSelectExternalCode.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtBatchInsert != null) {
                    psmtBatchInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtBatchUpdate != null) {
                    psmtBatchUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUsersInsert != null) {
                    psmtUsersInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtGeographiesInsert != null) {
                    psmtGeographiesInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtRolesInsert != null) {
                    psmtRolesInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtDomainInsert != null) {
                    psmtDomainInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtProductInsert != null) {
                    psmtProductInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserVoucherSegmentInsert != null) {
                	psmtUserVoucherSegmentInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserVoucherTypeInsert != null) {
                	psmtUserVoucherTypeInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtBatchGeographyInsert != null) {
                    psmtBatchGeographyInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectLoginID != null) {
                    rsSelectLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsUserMsisdn != null) {
                    rsUserMsisdn.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsExternalCode != null) {
                    rsExternalCode.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            if (log.isDebugEnabled()) {
                log.debug("addOperatorUserList", "Exiting: insertCount=" + insertUsersCount);
            }
        }
        return dbErrorList;

    }

    /**
     * @param p_psmtInsert
     * @param p_batchOPTUserVO
     * @return insertCount
     * @throws BTSLBaseException
     * @author shishupal.singh
     */
    private int addOperatorUser(PreparedStatement p_psmtInsert, BatchOPTUserVO p_batchOPTUserVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("addOperatorUser", "Entered :");
        }
        int insertCount = 0;
        final String METHOD_NAME = "addOperatorUser";
        try {
            p_psmtInsert.setString(1, p_batchOPTUserVO.getUserID());
            // commented for DB2 p_psmtInsert.setFormOfUse(2,

            p_psmtInsert.setString(2, p_batchOPTUserVO.getUserName().trim());
            p_psmtInsert.setString(3, p_batchOPTUserVO.getNetworkID());
            p_psmtInsert.setString(4, p_batchOPTUserVO.getLoginID());
            p_psmtInsert.setString(5, p_batchOPTUserVO.getPassword());
            p_psmtInsert.setString(6, p_batchOPTUserVO.getCategoryCode());
            p_psmtInsert.setString(7, p_batchOPTUserVO.getParentID());
            p_psmtInsert.setString(8, p_batchOPTUserVO.getOwnerID());
            p_psmtInsert.setString(9, p_batchOPTUserVO.getAllowedIps());
            p_psmtInsert.setString(10, p_batchOPTUserVO.getAllowedDays());
            p_psmtInsert.setString(11, p_batchOPTUserVO.getFromTime());
            p_psmtInsert.setString(12, p_batchOPTUserVO.getToTime());
            p_psmtInsert.setString(13, p_batchOPTUserVO.getEmpCode());
            p_psmtInsert.setString(14, p_batchOPTUserVO.getStatus());
            p_psmtInsert.setString(15, p_batchOPTUserVO.getEmail());
            p_psmtInsert.setString(16, p_batchOPTUserVO.getContactNo());

            p_psmtInsert.setString(17, p_batchOPTUserVO.getDesignation());
            p_psmtInsert.setString(18, p_batchOPTUserVO.getDivisionCode());
            p_psmtInsert.setString(19, p_batchOPTUserVO.getDepartmentCode());
            p_psmtInsert.setString(20, p_batchOPTUserVO.getMsisdn());
            p_psmtInsert.setString(21, p_batchOPTUserVO.getUserType());
            p_psmtInsert.setString(22, p_batchOPTUserVO.getCreatedBy());
            p_psmtInsert.setTimestamp(23, BTSLUtil.getTimestampFromUtilDate(p_batchOPTUserVO.getCreatedOn()));
            p_psmtInsert.setString(24, p_batchOPTUserVO.getModifiedBy());
            p_psmtInsert.setTimestamp(25, BTSLUtil.getTimestampFromUtilDate(p_batchOPTUserVO.getModifiedOn()));

            p_psmtInsert.setString(26, p_batchOPTUserVO.getAddress1());

            p_psmtInsert.setString(27, p_batchOPTUserVO.getAddress2());

            p_psmtInsert.setString(28, p_batchOPTUserVO.getCity());

            p_psmtInsert.setString(29, p_batchOPTUserVO.getState());
 
            p_psmtInsert.setString(30, p_batchOPTUserVO.getCountry());
            p_psmtInsert.setString(31, p_batchOPTUserVO.getSsn());

            p_psmtInsert.setString(32, p_batchOPTUserVO.getUserNamePrefix());
            p_psmtInsert.setString(33, p_batchOPTUserVO.getExternalCode());

            p_psmtInsert.setString(34, p_batchOPTUserVO.getShortName());
            p_psmtInsert.setString(35, p_batchOPTUserVO.getUserCode());
            if (p_batchOPTUserVO.getAppointmentDate() != null) {
                p_psmtInsert.setTimestamp(36, BTSLUtil.getTimestampFromUtilDate(p_batchOPTUserVO.getAppointmentDate()));
            } else {
                p_psmtInsert.setTimestamp(36, null);
            }
            p_psmtInsert.setString(37, p_batchOPTUserVO.getPreviousStatus());
            p_psmtInsert.setString(38, p_batchOPTUserVO.getCreationType());
            p_psmtInsert.setString(39, p_batchOPTUserVO.getBatchID());

            p_psmtInsert.setString(40, BTSLUtil.NullToString(p_batchOPTUserVO.getFirstName()).trim());
            p_psmtInsert.setString(41, BTSLUtil.NullToString(p_batchOPTUserVO.getLastName()).trim());
            if (BTSLUtil.isNullString(p_batchOPTUserVO.getAuthTypeAllowed())) {
                p_psmtInsert.setString(42, PretupsI.NO);
            } else {
                p_psmtInsert.setString(42, p_batchOPTUserVO.getAuthTypeAllowed());
            }

            insertCount = p_psmtInsert.executeUpdate();

        } catch (SQLException sqe) {
            log.error("addOperatorUser", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addOperatorUser]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addOperatorUser", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error("addOperatorUser", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addOperatorUser]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "addOperatorUser", "error.general.processing",ex);
        } finally {
            try {
                p_psmtInsert.clearParameters();
            } catch (SQLException sqle) {
                log.errorTrace(METHOD_NAME, sqle);
            }
            if (log.isDebugEnabled()) {
                log.debug("addOperatorUser", "Exiting: insertCount=" + insertCount);
            }
        }
        return insertCount;
    }

    /**
     * Method for inserting User Geographies Info.
     * 
     * @param p_psmtInsert
     *            PreparedStatement
     * @param p_batchOPTUserVO
     *            BatchOPTUserVO
     * @return insertCount int
     * @exception BTSLBaseException
     * @author shishupal.singh
     */
    private int addUserGeographyList(PreparedStatement p_psmtInsert, BatchOPTUserVO p_batchOPTUserVO) throws BTSLBaseException {
        int insertCount = 0;
        final String METHOD_NAME = "addUserGeographyList";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_batchOPTUserVO= ");
        	loggerValue.append(p_batchOPTUserVO.toString());
            log.debug(METHOD_NAME,loggerValue);
        }
        try {
            if ((p_batchOPTUserVO != null && p_batchOPTUserVO.getGeographyArrList() != null && p_batchOPTUserVO.getGeographyArrList().length > 0)) {
                for (int i = 0, j = p_batchOPTUserVO.getGeographyArrList().length; i < j; i++) {
                    if (!BTSLUtil.isNullString(p_batchOPTUserVO.getGeographyArrList()[i])) {
                        p_psmtInsert.setString(1, p_batchOPTUserVO.getUserID());
                        p_psmtInsert.setString(2, p_batchOPTUserVO.getGeographyArrList()[i].toUpperCase());
                        insertCount += p_psmtInsert.executeUpdate();
                    }
                }
            }
        } // end of try
        catch (SQLException sqle) {
            insertCount = 0;
            log.error(METHOD_NAME, "SQLException: " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addUserGeographyList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            insertCount = 0;
            log.error(METHOD_NAME, "Exception: " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addUserGeographyList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } // end of catch
        finally {
            try {
                p_psmtInsert.clearParameters();
            } catch (SQLException sqle) {
                log.errorTrace(METHOD_NAME, sqle);
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }

    /**
     * Method for inserting User Geographies Info.
     * 
     * @param p_psmtInsert
     *            PreparedStatement
     * @param p_batchOPTUserVO
     *            BatchOPTUserVO
     * @return insertCount int
     * @exception BTSLBaseException
     * @author shishupal.singh
     */
    private int addUserRolesList(PreparedStatement p_psmtInsert, BatchOPTUserVO p_batchOPTUserVO) throws BTSLBaseException {
        int insertCount = 0;
        final String METHOD_NAME = "addUserRolesList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:");
        }
        try {
            if ((p_batchOPTUserVO != null && p_batchOPTUserVO.getRolesArrList() != null && p_batchOPTUserVO.getRolesArrList().length > 0)) {
                for (int i = 0, j = p_batchOPTUserVO.getRolesArrList().length; i < j; i++) {
                    if (!BTSLUtil.isNullString(p_batchOPTUserVO.getRolesArrList()[i])) {
                        p_psmtInsert.setString(1, p_batchOPTUserVO.getUserID());
                        p_psmtInsert.setString(2, p_batchOPTUserVO.getRolesArrList()[i].toUpperCase());
                        insertCount = p_psmtInsert.executeUpdate();
                    }
                }
            }

        } // end of try
        catch (SQLException sqle) {
            insertCount = 0;
            log.error(METHOD_NAME, "SQLException: " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addUserRolesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            insertCount = 0;
            log.error(METHOD_NAME, "Exception: " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addUserRolesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } // end of catch
        finally {
            try {
                p_psmtInsert.clearParameters();
            } catch (SQLException sqle) {
                log.errorTrace(METHOD_NAME, sqle);
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }

    /**
     * Method for inserting User Geographies Info.
     * 
     * @param p_psmtInsert
     *            PreparedStatement
     * @param p_batchOPTUserVO
     *            BatchOPTUserVO
     * @return insertCount int
     * @exception BTSLBaseException
     * @author shishupal.singh
     */
    public int addUserDomainList(PreparedStatement p_psmtInsert, BatchOPTUserVO p_batchOPTUserVO) throws BTSLBaseException {
        int insertCount = 0;
        final String METHOD_NAME = "addUserDomainList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:");
        }
        try {
            if ((p_batchOPTUserVO != null && p_batchOPTUserVO.getDomainArrList() != null && p_batchOPTUserVO.getDomainArrList().length > 0)) {
                for (int i = 0, j = p_batchOPTUserVO.getDomainArrList().length; i < j; i++) {
                    if (!BTSLUtil.isNullString(p_batchOPTUserVO.getDomainArrList()[i])) {
                        p_psmtInsert.setString(1, p_batchOPTUserVO.getUserID());
                        p_psmtInsert.setString(2, p_batchOPTUserVO.getDomainArrList()[i].toUpperCase());
                        insertCount = p_psmtInsert.executeUpdate();
                    }
                }
            }
        } // end of try
        catch (SQLException sqle) {
            insertCount = 0;
            log.error(METHOD_NAME, "SQLException: " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addUserDomainList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            insertCount = 0;
            log.error(METHOD_NAME, "Exception: " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addUserDomainList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } // end of catch
        finally {
            try {
                p_psmtInsert.clearParameters();
            } catch (SQLException sqle) {
                log.errorTrace(METHOD_NAME, sqle);
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }

    /**
     * Method for inserting User Products Info.
     * 
     * @param p_psmtInsert
     *            PreparedStatement
     * @param p_batchOPTUserVO
     *            BatchOPTUserVO
     * @return insertCount int
     * @exception BTSLBaseException
     * @author shishupal.singh
     */
    public int addUserProductList(PreparedStatement p_psmtInsert, BatchOPTUserVO p_batchOPTUserVO) throws BTSLBaseException {

        int insertCount = 0;
        final String METHOD_NAME = "addUserProductList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:");
        }
        try {
            if ((p_batchOPTUserVO != null && p_batchOPTUserVO.getProductArrList() != null && p_batchOPTUserVO.getProductArrList().length > 0)) {
                for (int i = 0, j = p_batchOPTUserVO.getProductArrList().length; i < j; i++) {
                    if (!BTSLUtil.isNullString(p_batchOPTUserVO.getProductArrList()[i])) {
                        p_psmtInsert.setString(1, p_batchOPTUserVO.getUserID());
                        p_psmtInsert.setString(2, p_batchOPTUserVO.getProductArrList()[i].toUpperCase());
                        insertCount = p_psmtInsert.executeUpdate();
                    }
                }
            }

        } // end of try
        catch (SQLException sqle) {
            insertCount = 0;
            log.error(METHOD_NAME, "SQLException: " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addUserProductsList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            insertCount = 0;
            log.error(METHOD_NAME, "Exception: " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addUserProductsList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } // end of catch
        finally {
            try {
                p_psmtInsert.clearParameters();
            } catch (SQLException sqle) {
                log.errorTrace(METHOD_NAME, sqle);
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * @param p_networkCode
     * @param p_prefix
     * @param p_counter
     * @param p_userPaddingLength
     * @return id
     * @throws Exception
     * @author shishupal.singh
     */
    private String generateUserId(String p_networkCode, String p_prefix, long p_counter, int p_userPaddingLength) throws Exception {
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_prefix=");
        	loggerValue.append(p_prefix);
        	loggerValue.append(" p_counter=");
        	loggerValue.append(p_counter);
            log.debug("generateUserId", loggerValue);
        }
        String id = BTSLUtil.padZeroesToLeft(p_counter + "", p_userPaddingLength);
        id = p_networkCode + p_prefix + id;
        if (log.isDebugEnabled()) {
            log.debug("generateUserId", "Exiting id=" + id);
        }
        return id;
    }

    /**
     * @param p_con
     * @param loginID
     * @param p_SessionUserVO
     * @param p_modifyDate
     * @return invalidLoginIDStr
     * @throws BTSLBaseException
     */
    public String[] confirmBatchDelete(Connection p_con, String[] loginID, UserVO p_SessionUserVO, Date p_modifyDate) throws BTSLBaseException {
        final String METHOD_NAME = "confirmBatchDelete";
        PreparedStatement psmtSelectLogin = null;
        PreparedStatement psmtSelectAuth = null;
        PreparedStatement psmtUpdate = null;
        StringBuilder strSelectLogin = new StringBuilder("SELECT status, user_id FROM users WHERE UPPER(login_id)=UPPER(?)");
        BatchOPTUserQry batchOPTUserQry = (BatchOPTUserQry)ObjectProducer.getObject(QueryConstants.BATCH_OPT_USER_QRY, QueryConstants.QUERY_PRODUCER);
        String strSelectAuth=batchOPTUserQry.confirmBatchDeleteQry();
        String strUpdate = "UPDATE users SET login_id=?, status=?, previous_status=?,modified_by=?,modified_on=? WHERE UPPER(login_id)=UPPER(?)";
        ResultSet rs_login_id = null;
        ResultSet rs_Authorised = null;

        String login_id = null;
        String status = null;
        String user_id = null;
        int updateCount = 0;
        StringBuilder invalidLoginID = new StringBuilder();
        StringBuilder validLoginID = new StringBuilder();
        String loginIDStr[] = new String[2];
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: login_id Size= ");
        	loggerValue.append(loginID.length);
        	loggerValue.append(" ,strSelectLogin=");
        	loggerValue.append(strSelectLogin);
        	loggerValue.append(" ,Query : strUpdate=");
        	loggerValue.append(strUpdate);
        
            log.debug(METHOD_NAME, loggerValue);
        }

        try {
            psmtSelectLogin = p_con.prepareStatement(strSelectLogin.toString());
            psmtSelectAuth = p_con.prepareStatement(strSelectAuth.toString());
            psmtUpdate = p_con.prepareStatement(strUpdate);
            UserVO userVO = new UserVO();
            for (int i = 0, j = loginID.length; i < j; i++) {
                login_id = loginID[i];
                psmtSelectLogin.clearParameters();
                psmtSelectLogin.setString(1, login_id);
                rs_login_id = psmtSelectLogin.executeQuery();
                if (rs_login_id.next()) {
                    status = rs_login_id.getString("status");
                    user_id = rs_login_id.getString("user_id");
                } else {
                    invalidLoginID.append(login_id);
                    invalidLoginID.append(",");
                    OperatorUserLog.log("BATCHOPTUSRDELETE", userVO, p_SessionUserVO, "Invalid login id :" + login_id);
                    continue;
                }
                psmtSelectAuth.clearParameters();
                psmtSelectAuth.setString(1, p_SessionUserVO.getNetworkID());
                psmtSelectAuth.setString(2, p_SessionUserVO.getCategoryCode());
                psmtSelectAuth.setString(3, p_SessionUserVO.getUserID());
                psmtSelectAuth.setString(4, p_SessionUserVO.getUserID());
                psmtSelectAuth.setString(5, p_SessionUserVO.getCategoryCode());
                psmtSelectAuth.setString(6, login_id);
                rs_Authorised = psmtSelectAuth.executeQuery();
                if (!rs_Authorised.next()) {
                    invalidLoginID.append(login_id);
                    invalidLoginID.append(",");
                    OperatorUserLog.log("BATCHOPTUSRDELETE", userVO, p_SessionUserVO, "You are not authorised to delete this login id :" + login_id);
                    continue;
                }

                psmtUpdate.clearParameters();
                psmtUpdate.setString(1, user_id);
                psmtUpdate.setString(2, PretupsI.USER_STATUS_DELETED);
                psmtUpdate.setString(3, status);
                psmtUpdate.setString(4, p_SessionUserVO.getUserID());
                psmtUpdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_modifyDate));
                psmtUpdate.setString(6, login_id);
                updateCount = psmtUpdate.executeUpdate();

                if (updateCount < 0) {
                    invalidLoginID.append(login_id);
                    invalidLoginID.append(",");
                    OperatorUserLog.log("BATCHOPTUSRDELETE", userVO, p_SessionUserVO, "You can not delete this login id :" + login_id);
                    continue;
                } else {
                    validLoginID.append(login_id);
                    validLoginID.append(",");
                }
                OperatorUserLog.log("BATCHOPTUSRDELETE", userVO, p_SessionUserVO, "Sucessfully deleted, login id :" + login_id);
            }
            if (invalidLoginID.length() > 1) {
                loginIDStr[0] = invalidLoginID.toString().substring(0, (invalidLoginID.toString().length() - 1));
            }
            if (validLoginID.length() > 1) {
                loginIDStr[1] = validLoginID.toString().substring(0, (validLoginID.toString().length() - 1));
            }
        } catch (SQLException sql) {
            log.error("confirmBatchDelete", "SQLException: " + sql.getMessage());
            log.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[confirmBatchDelete]", "", "", "", "SQL Exception:" + sql.getMessage());
            throw new BTSLBaseException(this, "confirmBatchDelete", "error.general.sql.processing",sql);

        } // end of catch
        catch (Exception e) {
            log.error("confirmBatchDelete", "Exception: " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[confirmBatchDelete]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "confirmBatchDelete", "error.general.processing",e);

        } // end of catch
        finally {
        	try{
            	if (rs_login_id!= null){
            		rs_login_id.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing result set.", e);
            }
        	try{
            	if (rs_Authorised!= null){
            		rs_Authorised.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing result set.", e);
            }
            try{
                if (psmtSelectLogin!= null){
                	psmtSelectLogin.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            try{
                if (psmtSelectAuth!= null){
                	psmtSelectAuth.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            try{
                if (psmtUpdate!= null){
                	psmtUpdate.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            
            if (log.isDebugEnabled()) {
                log.debug("confirmBatchDelete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return loginIDStr;
    }

    /**
     * Method for inserting User service type Info.
     * 
     * @param p_psmtInsert
     *            PreparedStatement
     * @param p_batchOPTUserVO
     *            BatchOPTUserVO
     * @return insertCount int
     * @exception BTSLBaseException
     * @author shishupal.singh
     */
    private int addVoucherTypeList(PreparedStatement p_psmtInsert, BatchOPTUserVO p_batchOPTUserVO) throws BTSLBaseException {
        int insertCount = 0;
        final String METHOD_NAME = "addVoucherTypeList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:");
        }
        try {
            if ((p_batchOPTUserVO != null && p_batchOPTUserVO.getVouchertypeArrList() != null && p_batchOPTUserVO.getVouchertypeArrList().length > 0)) {
                for (int i = 0, j = p_batchOPTUserVO.getVouchertypeArrList().length; i < j; i++) {
                    if (!BTSLUtil.isNullString(p_batchOPTUserVO.getVouchertypeArrList()[i])) {
                        p_psmtInsert.setString(1, p_batchOPTUserVO.getUserID());
                        p_psmtInsert.setString(2, p_batchOPTUserVO.getVouchertypeArrList()[i]);
                        insertCount = p_psmtInsert.executeUpdate();
                    }
                }
            }

        } // end of try
        catch (SQLException sqle) {
            insertCount = -1;
            log.error(METHOD_NAME, "SQLException: " + sqle.getMessage());
            log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addVoucherTypeList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            insertCount = -1;
            log.error(METHOD_NAME, "Exception: " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addVoucherTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } // end of catch
        finally {
            try {
                p_psmtInsert.clearParameters();
            } catch (SQLException sqle) {
                log.errorTrace(METHOD_NAME, sqle);
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }
    
    private int addSegmentList(PreparedStatement p_psmtInsert, BatchOPTUserVO p_batchOPTUserVO) throws BTSLBaseException {
        int insertCount = 0;
        final String methodName = "addSegmentList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        try {
            if ((p_batchOPTUserVO != null && p_batchOPTUserVO.getSegmentArrList() != null && p_batchOPTUserVO.getSegmentArrList().length > 0)) {
                for (int i = 0, j = p_batchOPTUserVO.getSegmentArrList().length; i < j; i++) {
                    if (!BTSLUtil.isNullString(p_batchOPTUserVO.getSegmentArrList()[i])) {
                        p_psmtInsert.setString(1, p_batchOPTUserVO.getUserID());
                        p_psmtInsert.setString(2, p_batchOPTUserVO.getSegmentArrList()[i]);
                        insertCount = p_psmtInsert.executeUpdate();
                    }
                }
            }

        } // end of try
        catch (SQLException sqle) {
            insertCount = -1;
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addSegmentList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            insertCount = -1;
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addSegmentList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
            try {
                p_psmtInsert.clearParameters();
            } catch (SQLException sqle) {
                log.errorTrace(methodName, sqle);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }
    
    public ArrayList addBatchOperatorUserList(Connection p_con, ArrayList p_operatorUserDetailList, Locale p_locale, UserVO p_userVO, String batchName, String p_fileName, Integer totRec) throws BTSLBaseException {
    	final String METHOD_NAME = "addOperatorUserList";
    	if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userDetailList.size()=");
        	loggerValue.append(p_operatorUserDetailList.size());
        	loggerValue.append(" p_locale=");
        	loggerValue.append(p_locale);
        	loggerValue.append(" p_fileName: ");
        	loggerValue.append(p_fileName);
            log.debug(METHOD_NAME,loggerValue );
        }
        
        boolean batchIdFlag = true;
        String batchID = p_fileName;
        int commitCounter = 0, updateCount = 0;
        long idCounter = 0;
        int commitNumber = 0;
        int userPaddingLength = 0;
        // Email for pin & password
        BTSLMessages btslPushMessage = null;
        PushMessage pushMessage = null;
        String subject = null;
        EmailSendToUser emailSendToUser = null;
        try {
            commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            commitNumber = 100;
        }
        try {
            userPaddingLength = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            userPaddingLength = 10;
        }
        IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
        OperatorUtilI operatorUtil = null;
        ArrayList dbErrorList = new ArrayList();
        ListValueVO errorVO = null;
        StringBuilder strBuff = null;
        PreparedStatement pstmtSelectLoginID = null;
        PreparedStatement pstmtSelectExternalCode = null;
        PreparedStatement psmtBatchInsert = null;
        PreparedStatement psmtBatchUpdate = null;
        // commented for DB2OraclePreparedStatement psmtUsersInsert = null;
        PreparedStatement psmtUsersInsert = null;
        PreparedStatement psmtGeographiesInsert = null;
        PreparedStatement psmtRolesInsert = null;
        PreparedStatement psmtDomainInsert = null;
        PreparedStatement psmtProductInsert = null;
        PreparedStatement psmtBatchGeographyInsert = null;
        PreparedStatement psmtUserVoucherTypeInsert = null;
        PreparedStatement psmtUserVoucherSegmentInsert = null;
        

        ResultSet rsSelectLoginID = null;
        ResultSet rsUserMsisdn = null;
        ResultSet rsExternalCode = null;
        int insertUsersCount = 0, insertGeographiesCount = 0, insertRolesCount = 0, insertDomainCount = 0, insertProductCount = 0, insertUserVoucherType=0, insertUserVoucherSegment=0;
        try {
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[addChannelUserList]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
            }
            // Email for pin & password
            Locale defaultLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            // ========================Unique check for login id
            strBuff = new StringBuilder("SELECT 1 FROM users U WHERE U.login_id=? ");
            String selectLoginIDQuery = strBuff.toString();
            pstmtSelectLoginID = p_con.prepareStatement(selectLoginIDQuery);

            // =========================Unique check for external code
            StringBuilder selectExternalCode = new StringBuilder("SELECT 1 FROM users WHERE external_code= ?");
            String selecExternalCodeQuery = selectExternalCode.toString();
            pstmtSelectExternalCode = p_con.prepareStatement(selecExternalCodeQuery);

            // batches insert
            strBuff = new StringBuilder("INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, ");
            strBuff.append("network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
            strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            String insertBatchQuery = strBuff.toString();
            psmtBatchInsert = p_con.prepareStatement(insertBatchQuery);

            // update batches table
            strBuff = new StringBuilder("UPDATE batches SET batch_size=?, status=?, approved_records=?, rejected_records=? WHERE batch_id=? ");
            String updateBatchQuery = strBuff.toString();
            psmtBatchUpdate = p_con.prepareStatement(updateBatchQuery);

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO users (user_id,user_name,network_code,login_id,password,category_code,");
            strBuff.append("parent_id,owner_id,allowed_ip,allowed_days,from_time,to_time,employee_code,status,email,");
            strBuff.append("contact_no,designation,division,department,msisdn,user_type,created_by,created_on,");
            strBuff.append("modified_by,modified_on,address1,address2,city,state,country,ssn,user_name_prefix, ");
            strBuff.append("external_code,short_name,user_code,appointment_date,previous_status, creation_type, ");
            strBuff.append(" batch_id,firstname,lastname,authentication_allowed) ");// fname,lname
                                                                                    // added
                                                                                    // by
                                                                                    // deepika
                                                                                    // aggarwal
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertUsersQuery = strBuff.toString();
            psmtUsersInsert = p_con.prepareStatement(insertUsersQuery);
            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_geographies (user_id,");
            strBuff.append("grph_domain_code)");
            strBuff.append(" values (?,?)");
            String insertGeographiesQuery = strBuff.toString();
            psmtGeographiesInsert = p_con.prepareStatement(insertGeographiesQuery);

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_roles (user_id,");
            strBuff.append("role_code) values (?,?)");
            String insertRolesQuery = strBuff.toString();
            psmtRolesInsert = p_con.prepareStatement(insertRolesQuery);

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_domains (user_id,");
            strBuff.append("domain_code) values (?,?)");
            String insertDomainQuery = strBuff.toString();
            psmtDomainInsert = p_con.prepareStatement(insertDomainQuery);

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO user_product_types (user_id,");
            strBuff.append("product_type) values (?,?)");
            String insertProductQuery = strBuff.toString();
            psmtProductInsert = p_con.prepareStatement(insertProductQuery);

            // Batch geographies insert
            strBuff = new StringBuilder("INSERT INTO batch_geographies (batch_id, geography_code) ");
            strBuff.append(" VALUES(?,?)");
            String insertBatchGeographyQuery = strBuff.toString();
            psmtBatchGeographyInsert = p_con.prepareStatement(insertBatchGeographyQuery);

            // insert into user voucher type
            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO USER_VOUCHERTYPES (user_id,");
            strBuff.append("VOUCHER_TYPE)");
            strBuff.append(" values (?,?)");
            String insertUserVoucherTypeQuery = strBuff.toString();
            psmtUserVoucherTypeInsert = p_con.prepareStatement(insertUserVoucherTypeQuery);
            
            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO USER_VOUCHER_SEGMENTS (user_id,");
            strBuff.append("VOUCHER_SEGMENT)");
            strBuff.append(" values (?,?)");
            String insertUserVoucherSegmentQuery = strBuff.toString();
            psmtUserVoucherSegmentInsert = p_con.prepareStatement(insertUserVoucherSegmentQuery);
            
            
            
            Iterator batchOPTUserVOListItr = p_operatorUserDetailList.iterator();

            BatchOPTUserVO batchOPTUserVO = null;
            ArrayList geographyList = new ArrayList();
            Start: while (batchOPTUserVOListItr.hasNext()) {
                batchOPTUserVO = (BatchOPTUserVO) batchOPTUserVOListItr.next();
                batchOPTUserVO.setBatchID(batchID);

                // ====================== Validation 1: Check for login_id
                // uniqueness, if login id already exists mark error.
                pstmtSelectLoginID.setString(1, batchOPTUserVO.getLoginID());
                rsSelectLoginID = pstmtSelectLoginID.executeQuery();
                if (rsSelectLoginID.next()) {
                	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.loginiduniqueerr", new String[] { batchOPTUserVO.getLoginID() });
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                    continue Start;
                }
                // ====================== end login_id validation here

                // ======================Validation 5: Check for the uniqueness
                // of the External code (if given)
                if (!BTSLUtil.isNullString(batchOPTUserVO.getExternalCode())) {
                    pstmtSelectExternalCode.setString(1, batchOPTUserVO.getExternalCode().trim());
                    rsExternalCode = pstmtSelectExternalCode.executeQuery();
                    if (rsExternalCode.next()) {
                    	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.externalcodeuniqueerr", new String[] {batchOPTUserVO.getExternalCode()});
                        errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
                        dbErrorList.add(errorVO);
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                        continue Start;
                    }
                }
                // ====================== end external code validation here

                if (batchIdFlag) {
                    // one time entry into batches table
                    batchID = operatorUtil.formatBatchesID(p_userVO.getNetworkID(), PretupsI.BATCH_OPT_USR_ID_PREFIX, new Date(), IDGenerator.getNextID(PretupsI.BATCH_OPT_USR_BATCH_ID, BTSLUtil.getFinancialYear(), p_userVO.getNetworkID()));
                    psmtBatchInsert.setString(1, batchID);
                    psmtBatchInsert.setString(2, PretupsI.BATCH_OPT_USR_BATCH_TYPE);
                    psmtBatchInsert.setInt(3, p_operatorUserDetailList.size());
                    psmtBatchInsert.setString(4, batchName);
                    psmtBatchInsert.setString(5, batchOPTUserVO.getNetworkID());
                    psmtBatchInsert.setString(6, PretupsI.USR_BATCH_STATUS_UNDERPROCESS);
                    psmtBatchInsert.setString(7, batchOPTUserVO.getCreatedBy());
                    psmtBatchInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(batchOPTUserVO.getCreatedOn()));
                    psmtBatchInsert.setString(9, batchOPTUserVO.getModifiedBy());
                    psmtBatchInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(batchOPTUserVO.getModifiedOn()));
                    psmtBatchInsert.setString(11, p_fileName);
                    if (psmtBatchInsert.executeUpdate() <= 0) {
                        p_con.rollback();
                    	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.err.batchnotcreated", null);
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                        throw new BTSLBaseException(this, "addOperatorUserList", "user.batchoptuser.processuploadedfile.err.batchnotcreated", "selectCategory");
                    }
                    batchIdFlag = false;
                    // First time make the user_id
                    idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCreatedOn());
                }

                if (commitCounter > commitNumber)// After 100 record commit the
                                                 // records
                {
                    // after 100 records update the last_no of the ids table for
                    // user_id
                    idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCreatedOn(), idCounter - 1);
                    p_con.commit();
                    // after 100 records pick the last_no from the ids table for
                    // user_id
                    idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCreatedOn());
                    commitCounter = 0;// reset commit counter
                }
                batchOPTUserVO.setUserID(this.generateUserId(batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCategoryVO().getUserIdPrefix(), idCounter, userPaddingLength));
                batchOPTUserVO.setBatchID(batchID);

                // insert user info
                insertUsersCount = this.addOperatorUser(psmtUsersInsert, batchOPTUserVO);
                if (insertUsersCount <= 0) {
                    p_con.rollback();
                	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() });
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                    continue Start;
                }

                // insert geography info
                insertGeographiesCount = this.addUserGeographyList(psmtGeographiesInsert, batchOPTUserVO);
                if (insertGeographiesCount <= 0) {
                    p_con.rollback();
                	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() });
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                    continue Start;
                }// ==============================================================================================
                else// make the array list of geography code
                {
                    boolean flag = false;
                    Iterator geographyListItr = null;
                    for (int i = 0; i < batchOPTUserVO.getGeographyArrList().length; i++) {

                        // =============================
                        flag = false;
                        geographyListItr = geographyList.iterator();
                        while (geographyListItr.hasNext()) {
                            if (((String) geographyListItr.next()).equalsIgnoreCase(batchOPTUserVO.getGeographyArrList()[i])) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag == false) {
                            geographyList.add(batchOPTUserVO.getGeographyArrList()[i]);
                            // =============================
                        }
                    }
                }
                // ==============================================================================================

                // insert roles info
                insertRolesCount = this.addUserRolesList(psmtRolesInsert, batchOPTUserVO);
                if (insertRolesCount <= 0) {
                    p_con.rollback();
                	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() });
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                    continue Start;
                }

                // insert domain info
                if (batchOPTUserVO.getCategoryVO().getDomainAllowed().equalsIgnoreCase(PretupsI.YES) && PretupsI.DOMAINS_ASSIGNED.equals(batchOPTUserVO.getCategoryVO().getFixedDomains())) {
                    insertDomainCount = this.addUserDomainList(psmtDomainInsert, batchOPTUserVO);
                    if (insertDomainCount <= 0) {
                        p_con.rollback();
                    	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() });
                        errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
                        dbErrorList.add(errorVO);
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                        continue Start;
                    }
                }

                // insert product info
                if (batchOPTUserVO.getCategoryVO().getProductTypeAllowed().equalsIgnoreCase(PretupsI.YES)) {
                    insertProductCount = this.addUserProductList(psmtProductInsert, batchOPTUserVO);
                    if (insertProductCount <= 0) {
                        p_con.rollback();
                    	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() });
                        errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
                        dbErrorList.add(errorVO);
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                        continue Start;
                    }
                }
                
                
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
                {
             // insert user voucher type info
                insertUserVoucherType = this.addVoucherTypeList(psmtUserVoucherTypeInsert, batchOPTUserVO);
                if (insertUserVoucherType < 0) {
                    p_con.rollback();
                	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() });
                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
                    dbErrorList.add(errorVO);
                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
                    continue Start;
                }
                
                }
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
	                insertUserVoucherSegment = this.addSegmentList(psmtUserVoucherSegmentInsert, batchOPTUserVO);
	                if (insertUserVoucherSegment < 0) {
	                    p_con.rollback();
	                	String error = RestAPIStringParser.getMessage(p_locale, "user.batchoptuser.processuploadedfile.msg.error.userinsertfail", new String[] { batchOPTUserVO.getUserName() });
	                    errorVO = new ListValueVO("", batchOPTUserVO.getRecordNumber(), error);
	                    dbErrorList.add(errorVO);
	                    BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=" + error);
	                    continue Start;
	                }
                }
                
                commitCounter++;
                idCounter++;
                updateCount++;
                // Email for pin & password- push the message
                if (batchOPTUserVO.getStatus().equalsIgnoreCase(PretupsI.YES)) {
                    String[] arrArray = { batchOPTUserVO.getLoginID(), "", BTSLUtil.decryptText(batchOPTUserVO.getPassword()) };
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                    if (!BTSLUtil.isNullString(batchOPTUserVO.getMsisdn())) {
                        pushMessage = new PushMessage(batchOPTUserVO.getMsisdn(), btslPushMessage, "", "", defaultLocale, p_userVO.getNetworkID(), "SMS will be delivered shortly");
                        pushMessage.push();
                    }
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(batchOPTUserVO.getEmail())) {
	                	subject = RestAPIStringParser.getMessage(p_locale, "subject.user.reg.bulk.message", new String[] { batchOPTUserVO.getUserName() });
                        emailSendToUser = new EmailSendToUser(subject, btslPushMessage, p_locale, p_userVO.getNetworkID(), "Email will be delivered shortly", batchOPTUserVO, p_userVO);
                        emailSendToUser.sendMail();
                    }
                }
                OperatorUserLog.log("BATCH_OPT_USR_INITIATE", batchOPTUserVO, p_userVO, "Add Operator user from bulk");
            } // end while loop
            if (updateCount > 0) {
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, batchOPTUserVO.getNetworkID(), batchOPTUserVO.getCreatedOn(), idCounter - 1);
                Iterator geographyListItr = geographyList.iterator();
                while (geographyListItr.hasNext()) {
                    psmtBatchGeographyInsert.clearParameters();
                    psmtBatchGeographyInsert.setString(1, batchID);
                    psmtBatchGeographyInsert.setString(2, ((String) geographyListItr.next()).toUpperCase());
                    if (psmtBatchGeographyInsert.executeUpdate() <= 0) {
                        p_con.rollback();
                        BatchesLog.operatorUserLog("BATCH_OPT_USR_INITIATE", batchOPTUserVO, null, "Fail :=When inserting batch_geographics table");
                        continue;
                    }
                }
                // update batches table for updating updateCount on the batch
                // size
                psmtBatchUpdate.setInt(1, totRec);
                psmtBatchUpdate.setString(2, PretupsI.USR_BATCH_STATUS_CLOSE);
                psmtBatchUpdate.setInt(3, updateCount);
                psmtBatchUpdate.setInt(4, totRec-updateCount);
                psmtBatchUpdate.setString(5, batchID);
                psmtBatchUpdate.executeUpdate();
                // ==================================================================================================
                p_con.commit();
            } else {
                p_con.rollback();
            }
            errorVO = new ListValueVO("BATCHID", "", batchID);
            dbErrorList.add(errorVO);

        } // end of try
        catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error("addOperatorUserList", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addOperatorUserList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addOperatorUserList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error("addOperatorUserList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[addOperatorUserList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "addOperatorUserList", "error.general.processing",ex);
        } finally {
            try {
                if (pstmtSelectLoginID != null) {
                    pstmtSelectLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectExternalCode != null) {
                    pstmtSelectExternalCode.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtBatchInsert != null) {
                    psmtBatchInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtBatchUpdate != null) {
                    psmtBatchUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUsersInsert != null) {
                    psmtUsersInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtGeographiesInsert != null) {
                    psmtGeographiesInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtRolesInsert != null) {
                    psmtRolesInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtDomainInsert != null) {
                    psmtDomainInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtProductInsert != null) {
                    psmtProductInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserVoucherSegmentInsert != null) {
                	psmtUserVoucherSegmentInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserVoucherTypeInsert != null) {
                	psmtUserVoucherTypeInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtBatchGeographyInsert != null) {
                    psmtBatchGeographyInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectLoginID != null) {
                    rsSelectLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsUserMsisdn != null) {
                    rsUserMsisdn.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsExternalCode != null) {
                    rsExternalCode.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            if (log.isDebugEnabled()) {
                log.debug("addOperatorUserList", "Exiting: insertCount=" + insertUsersCount);
            }
        }
        return dbErrorList;

    }
    
}
