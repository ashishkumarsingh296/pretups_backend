package com.btsl.pretups.channel.reports.businesslogic;

/*
 * @# ChannelUserReportDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Sep 15, 2005 Ved Prakash Sharma Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class ChannelUserReportDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());
    private ChannelUserReportQry channelUserReportQry;
    
    public ChannelUserReportDAO(){
    	channelUserReportQry = (ChannelUserReportQry)ObjectProducer.getObject(QueryConstants.CHANNEL_USER_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * by ved
     * Method :loadUserListBasisOfZoneDomainCategory
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pUserCategory
     *            String
     * @param pDomainCode
     *            String
     * @param pZoneCode
     *            String
     * @param pUserName
     *            String
     * @param pUserID
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserListBasisOfZoneDomainCategory(Connection pCon, String pUserCategory, String pDomainCode, String pZoneCode, String pUserName, String pUserID) throws BTSLBaseException {
        final String methodName = "loadUserListBasisOfZoneDomainCategory";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pUserCategory=");
        	loggerValue.append(pUserCategory);
        	loggerValue.append(", pDomainCode=");
        	loggerValue.append(pDomainCode);
        	loggerValue.append(", pZoneCode=" );
        	loggerValue.append(pZoneCode);
        	loggerValue.append(", pUserName=");
        	loggerValue.append(pUserName);
        	loggerValue.append(", pUserID");
        	loggerValue.append(pUserID);
        	
            log.debug(methodName,loggerValue );
        }
        final ArrayList userList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserTransferVO channelUserTransferVO = null;
        try {
            pstmtSelect  = channelUserReportQry.loadUserListBasisOfZoneDomainCategoryQry(pCon,SqlParameterEncoder.encodeParams(pUserCategory),
            		SqlParameterEncoder.encodeParams(pDomainCode),SqlParameterEncoder.encodeParams(pZoneCode),SqlParameterEncoder.encodeParams(pUserID),
            		SqlParameterEncoder.encodeParams(pUserName));
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                channelUserTransferVO = new ChannelUserTransferVO();
                channelUserTransferVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                channelUserTransferVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                userList.add(channelUserTransferVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListBasisOfZoneDomainCategory]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListBasisOfZoneDomainCategory]", "", "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userList size =");
            	loggerValue.append( userList.size());
                log.debug(methodName,  loggerValue);
            }
        }
        return userList;
    }

    /**
     * by ved
     * Method :loadUserListBasisOfZoneDomainCategoryHierarchy
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pUserCategory
     *            String
     * @param pDomainCode
     *            String
     * @param pZoneCode
     *            String
     * @param pUserName
     *            String
     * @param pUserID
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserListBasisOfZoneDomainCategoryHierarchy(Connection pCon, String pUserCategory, String pDomainCode, String pZoneCode, String pUserName, String pUserID) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserListBasisOfZoneDomainCategoryHierarchy";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pUserCategory=");
        	loggerValue.append(pUserCategory);
        	loggerValue.append(", pDomainCode=");
        	loggerValue.append(pDomainCode);
        	loggerValue.append( ", pZoneCode=");
        	loggerValue.append(	pZoneCode);	
        	loggerValue.append(", pUserName=");
        	loggerValue.append(pUserName);
        	loggerValue.append(", pUserID");
        	loggerValue.append(pUserID);
            log.debug("loadUserListBasisOfZoneDomainCategoryHierarchy",loggerValue );
        }
        final ArrayList userList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserTransferVO channelUserTransferVO = null;
        
        try {
        	pstmtSelect = channelUserReportQry.loadUserListBasisOfZoneDomainCategoryHierarchyQry(pCon, SqlParameterEncoder.encodeParams(pUserCategory), SqlParameterEncoder.encodeParams(pDomainCode),
        			SqlParameterEncoder.encodeParams(pZoneCode), SqlParameterEncoder.encodeParams(pUserID),SqlParameterEncoder.encodeParams(pUserName));

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                channelUserTransferVO = new ChannelUserTransferVO();
                channelUserTransferVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                channelUserTransferVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                userList.add(channelUserTransferVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error("loadUserListBasisOfZoneDomainCategoryHierarchy",  loggerValue);
            log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListBasisOfZoneDomainCategory]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserListBasisOfZoneDomainCategoryHierarchy", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error("loadUserListBasisOfZoneDomainCategoryHierarchy",  loggerValue );
            log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListBasisOfZoneDomainCategory]", "", "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, "loadUserListBasisOfZoneDomainCategoryHierarchy", "error.general.processing");
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
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userList size =" );
            	loggerValue.append(userList.size());
                log.debug("loadUserListBasisOfZoneDomainCategoryHierarchy", loggerValue);
            }
        }
        return userList;
    }

    /**
     * by ved
     * Method :loadUserListOnZoneDomainCategory
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pUserCategory
     *            String
     * @param pZoneCode
     *            String
     * @param pFromUserID
     *            String
     * @param pUserName
     *            String
     * @param pUserID
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserListOnZoneDomainCategory(Connection pCon, String pUserCategory, String pZoneCode, String pFromUserID, String pUserName, String ploginuserID, String domainCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserListOnZoneDomainCategory";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pUserCategory=");
        	loggerValue.append(pUserCategory);
        	loggerValue.append( ", pFromUserID=");
        	loggerValue.append(pFromUserID);
        	loggerValue.append(", pZoneCode="  );
        	loggerValue.append(pZoneCode);
        	loggerValue.append(", pUserName=");
        	loggerValue.append(pUserName);
        	loggerValue.append(", ploginuserID" );
        	loggerValue.append(ploginuserID);
        	loggerValue.append("   domainCode");
        	loggerValue.append(domainCode);
            log.debug("loadUserListOnZoneDomainCategory",loggerValue );
        }
        final ArrayList userList = new ArrayList();

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        
        try {
           
           pstmtSelect=channelUserReportQry.loadUserListOnZoneDomainCategoryQry(SqlParameterEncoder.encodeParams(pFromUserID), SqlParameterEncoder.encodeParams(pUserName), pCon, SqlParameterEncoder.encodeParams(pUserCategory), 
        		   SqlParameterEncoder.encodeParams(domainCode), SqlParameterEncoder.encodeParams(ploginuserID),SqlParameterEncoder.encodeParams(pZoneCode));
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                userList.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("user_name")),SqlParameterEncoder.encodeParams(rs.getString("user_id"))));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error("loadUserListOnZoneDomainCategory",loggerValue);
            log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListOnZoneDomainCategory]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, "loadUserListOnZoneDomainCategory", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error("loadUserListOnZoneDomainCategory", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListOnZoneDomainCategory]", "", "", "",loggerValue.toString() );
            throw new BTSLBaseException(this, "loadUserListOnZoneDomainCategory", "error.general.processing");
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
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
             	loggerValue.append("Exiting: userList size =");
             	loggerValue.append(userList.size());
                log.debug("loadUserListOnZoneDomainCategory",  loggerValue.toString());
            }
        }
        return userList;
    }

    /**
     * by ved
     * Method :loadUserListOnZoneCategoryHierarchy
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pUserCategory
     *            String
     * @param pZoneCode
     *            String
     * @param pFromUserID
     *            String
     * @param pUserName
     *            String
     * @param pUserID
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserListOnZoneCategoryHierarchy(Connection pCon, String pUserCategory, String pZoneCode, String pUserName, String ploginuserID, String domainCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserListOnZoneCategoryHierarchy";
        if (log.isDebugEnabled()) {
            log.debug(
                "loadUserListOnZoneCategoryHierarchy",
                "Entered: pUserCategory=" + pUserCategory + ",  pZoneCode=" + pZoneCode + ", pUserName=" + pUserName + ", ploginuserID" + ploginuserID + "    domainCode" + domainCode);
        }
        final ArrayList userList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        try {
        	pstmtSelect =channelUserReportQry.loadUserListOnZoneCategoryHierarchyQry(pCon, SqlParameterEncoder.encodeParams(pUserCategory),SqlParameterEncoder.encodeParams(domainCode),
        			SqlParameterEncoder.encodeParams(pZoneCode), SqlParameterEncoder.encodeParams(ploginuserID),SqlParameterEncoder.encodeParams(pUserName));
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                userList.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("user_name")),SqlParameterEncoder.encodeParams( rs.getString("user_id"))));
            }
        } catch (SQLException sqe) {
            log.error("loadUserListOnZoneCategoryHierarchy", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListOnZoneCategoryHierarchy]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserListOnZoneCategoryHierarchy", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadUserListOnZoneCategoryHierarchy", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListOnZoneDomainCategory]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserListOnZoneCategoryHierarchy", "error.general.processing");
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
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug("loadUserListOnZoneCategoryHierarchy", "Exiting: userList size =" + userList.size());
            }
        }
        return userList;
    }

    /**
     * by ved
     * Method :loadUserListWithOwnerIDOnZoneCategoryHierarchy
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pUserCategory
     *            String
     * @param pZoneCode
     *            String
     * @param pFromUserID
     *            String
     * @param pUserName
     *            String
     * @param pUserID
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserListWithOwnerIDOnZoneCategoryHierarchy(Connection pCon, String pUserCategory, String pZoneCode, String pUserName, String ploginuserID, String domainCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserListWithOwnerIDOnZoneCategoryHierarchy";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pUserCategory=");
        	loggerValue.append(pUserCategory);
        	loggerValue.append(",  pZoneCode=");
        	loggerValue.append(pZoneCode);
        	loggerValue.append(", pUserName=");
        	loggerValue.append(pUserName);
        	loggerValue.append(", ploginuserID");
        	loggerValue.append(ploginuserID);
        	loggerValue.append("domainCode");
        	loggerValue.append(domainCode);
            log.debug("loadUserListWithOwnerIDOnZoneCategoryHierarchy",loggerValue.toString());
        }
        final ArrayList userList = new ArrayList();

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        try {
        	pstmtSelect =channelUserReportQry.loadUserListWithOwnerIDOnZoneCategoryHierarchy(pCon, pUserCategory, domainCode, pZoneCode, ploginuserID, pUserName);
            
            rs = pstmtSelect.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserName(rs.getString("user_name"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userList.add(userVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : " );
        	loggerValue.append(sqe);
            log.error("loadUserListWithOwnerIDOnZoneCategoryHierarchy",loggerValue.toString());
            log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:" );
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListWithOwnerIDOnZoneCategoryHierarchy]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, "loadUserListWithOwnerIDOnZoneCategoryHierarchy", "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exception:" );
         	loggerValue.append(ex);
            log.error("loadUserListWithOwnerIDOnZoneCategoryHierarchy",  loggerValue);
            log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
         	loggerValue.append("Exception:" );
         	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListOnZoneDomainCategory]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserListWithOwnerIDOnZoneCategoryHierarchy", "error.general.processing");
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
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
              	loggerValue.append("Exiting: userList size =" );
              	loggerValue.append(userList.size());
                log.debug("loadUserListWithOwnerIDOnZoneCategoryHierarchy",  loggerValue );
            }
        }
        return userList;
    }

    /**
     * by ved
     * Method :loadUserListWithOwnerIDOnZoneDomainCategory
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pUserCategory
     *            String
     * @param pZoneCode
     *            String
     * @param pFromUserID
     *            String
     * @param pUserName
     *            String
     * @param pUserID
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserListWithOwnerIDOnZoneDomainCategory(Connection pCon, String pUserCategory, String pZoneCode, String pFromUserID, String pUserName, String ploginuserID, String domainCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserListWithOwnerIDOnZoneDomainCategory";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pUserCategory=");
        	loggerValue.append(pUserCategory);
        	loggerValue.append(", pFromUserID=");
        	loggerValue.append(pFromUserID);
        	loggerValue.append(", pZoneCode=");
        	loggerValue.append(pZoneCode);
        	loggerValue.append(", pUserName=");
        	loggerValue.append(pUserName);
        	loggerValue.append(", ploginuserID");
        	loggerValue.append(ploginuserID);
        	loggerValue.append("domainCode");
        	loggerValue.append(domainCode);
            log.debug(
                "loadUserListWithOwnerIDOnZoneDomainCategory",loggerValue );
        }
        final ArrayList userList = new ArrayList();

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        try {
           
        	pstmtSelect= channelUserReportQry.loadUserListWithOwnerIDOnZoneDomainCategoryQry(pCon, pUserCategory, domainCode, pZoneCode, ploginuserID, pFromUserID, pUserName);
            rs = pstmtSelect.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserName(rs.getString("user_name"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userList.add(userVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error("loadUserListWithOwnerIDOnZoneDomainCategory",  loggerValue);
            log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListWithOwnerIDOnZoneDomainCategory]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserListWithOwnerIDOnZoneDomainCategory", "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
         	loggerValue.append("SQL Exception:");
         	loggerValue.append(ex);
            log.error("loadUserListWithOwnerIDOnZoneDomainCategory", loggerValue);
            log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
         	loggerValue.append("Exception:");
         	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListWithOwnerIDOnZoneDomainCategory]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserListWithOwnerIDOnZoneDomainCategory", "error.general.processing");
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
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
             	loggerValue.append("Exiting: userList size =");
             	loggerValue.append(userList.size());
                log.debug("loadUserListWithOwnerIDOnZoneDomainCategory",  loggerValue);
            }
        }
        return userList;
    }

    /**
     * by ved
     * Method :loadUserList
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pUserCategory
     *            String
     * @param pZoneCode
     *            String
     * @param pFromUserID
     *            String
     * @param pUserName
     *            String
     * @param pUserID
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserList(Connection pCon, String UserCategory, String FromUserID, String UserName, String Networkcode, String domainCode) throws BTSLBaseException {
    	String pUserCategory = SqlParameterEncoder.encodeParams(UserCategory);
    	String pFromUserID = SqlParameterEncoder.encodeParams(FromUserID);
    	String pUserName = SqlParameterEncoder.encodeParams(UserName);
    	String pNetworkcode = SqlParameterEncoder.encodeParams(Networkcode);
    	String pdomainCode = SqlParameterEncoder.encodeParams(domainCode);
        final String METHOD_NAME = "loadUserList";
        
        if (log.isDebugEnabled()) {
            log.debug(
                "loadUserList",
                "Entered: pUserCategory=" + pUserCategory + ", pFromUserID=" + pFromUserID + ", pUserName=" + pUserName + ", pNetworkcode: =" + pNetworkcode + "   domainCode=" + pdomainCode);
        }
        final ArrayList userList = new ArrayList();
       
        
       
        final String userCategory = pUserCategory.replaceAll("'", "");
        final String ss = userCategory.replaceAll("\" ", "");
        final String[] mUserCategory = ss.split(",");
        final StringBuilder strBuff = new StringBuilder(" SELECT U.user_id, U.user_name ");
        strBuff.append(" FROM users U, categories CAT ");
        strBuff.append(" WHERE U.category_code IN (");
        for (int i = 0; i < mUserCategory.length; i++) {
            strBuff.append(" ?");
            if (i != mUserCategory.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        strBuff.append(" AND CAT.domain_code = ?");
        strBuff.append(" AND CAT.category_code = U.category_code ");
        strBuff.append(" AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND U.STATUS <> 'N' ");
        strBuff.append(" AND U.network_code = ? ");
        if (!BTSLUtil.isNullString(pFromUserID)) {
            strBuff.append(" AND U.user_id <> ?");
        }
        if (!BTSLUtil.isNullString(pUserName)) {
            strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        }
        strBuff.append(" ORDER BY U.user_name");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadUserList", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);) {
           
            
            int i = 0;
            for (int x = 0; x < mUserCategory.length; x++) {
                pstmtSelect.setString(++i, mUserCategory[x]);
            }
            pstmtSelect.setString(++i, pdomainCode);
            pstmtSelect.setString(++i, pNetworkcode);
            if (!BTSLUtil.isNullString(pFromUserID)) {
                pstmtSelect.setString(++i, pFromUserID);
            }
            if (!BTSLUtil.isNullString(pUserName)) {
              
                pstmtSelect.setString(++i, pUserName + "%");
            }
           try( ResultSet rs = pstmtSelect.executeQuery();)
           {
            while (rs.next()) {
                userList.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("user_name")),SqlParameterEncoder.encodeParams(rs.getString("user_id"))));
            }
        } 
        }catch (SQLException sqe) {
            log.error("loadUserList", "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadUserList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserList", "error.general.processing");
        } finally {
        
        	
            if (log.isDebugEnabled()) {
                log.debug("loadUserList", "Exiting: userList size =" + userList.size());
            }
        }
        return userList;
    }

    /**
     * Date : Jan 19, 2007
     * Discription :
     * Method : loadKeyValuesList
     * 
     * @param pCon
     * @param p_isAllKey
     * @param p_type
     * @param p_inKeys
     * @throws BTSLBaseException
     * @return ArrayList
     * @author ved.sharma
     */
    public ArrayList loadKeyValuesList(Connection pCon, boolean p_isAllKey, String p_type, String p_inKeys) throws BTSLBaseException {
        final String METHOD_NAME = "loadKeyValuesList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_isAllKey=");
        	loggerValue.append(p_isAllKey);
        	loggerValue.append(", p_type=" );
        	loggerValue.append(p_type);
        	loggerValue.append(", p_inKeys=");
        	loggerValue.append(p_inKeys);
            log.debug("loadKeyValuesList",  loggerValue);
        }
        final ArrayList list = new ArrayList();
      
        
       
        final StringBuilder strBuff = new StringBuilder("SELECT key, value, type, text1 FROM key_values WHERE type=? ");
        if (!p_isAllKey) {
            strBuff.append("AND key IN (" + p_inKeys + ") ");
        }
        strBuff.append("ORDER BY key");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug("loadKeyValuesList", loggerValue );
        }
        try (PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);){
            
            
            pstmtSelect.setString(1, p_type);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("value"), rs.getString("key")));
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error("loadKeyValuesList",  loggerValue );
            log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append( sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadKeyValuesList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadKeyValuesList", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error("loadKeyValuesList",  loggerValue );
            log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadKeyValuesList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadKeyValuesList", "error.general.processing");
        } finally {
        
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userList size =");
            	loggerValue.append(list.size());
                log.debug("loadKeyValuesList",  loggerValue );
            }
        }
        return list;
    }

    
    /**
     * Loads geography, domain_code or voucherTypes list for a particular ChannelAdmin
     * @param pCon
     * @param userId
     * @param columnName
     * @return
     * @throws BTSLBaseException
     */
    
    public ArrayList<String> loadGeogDomainVoucherTypeList(Connection pCon, String userId, String columnName) throws BTSLBaseException {
    	
        final String methodName = "loadGeogDomainVoucherTypeList";
        
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(userId);
        }

        ArrayList<String> listToReturn = new ArrayList<String>();
      
        
         StringBuffer strBuff = new StringBuffer(" ");
        
        if(columnName != null) {
        	
        	if(columnName.equalsIgnoreCase("GRPH_DOMAIN_CODE")) {
        		
        		strBuff = channelUserReportQry.prepareUserGeographyQuery();
        		
        	}else if(columnName.equalsIgnoreCase("DOMAIN_CODE")) {
        		
        		strBuff.append("select   ");
                strBuff.append(" distinct   ");
                strBuff.append(" "+columnName+"  ");
                strBuff.append(" from  ");
                strBuff.append(" USER_DOMAINS UD  ");
                strBuff.append(" WHERE   ");
                strBuff.append(" UD.USER_ID = ?  ");
                
                
        	}else if(columnName.equalsIgnoreCase("VOUCHER_TYPE")) {

        		strBuff.append("select   ");
                strBuff.append(" distinct   ");
                strBuff.append(" "+columnName+"  ");
                strBuff.append(" from  ");
                strBuff.append(" USER_VOUCHERTYPES UV  ");
                strBuff.append(" WHERE   ");
                strBuff.append(" UV.USER_ID = ?  ");
                
        	} 
        	
        }
        
        
        		
       
        try (PreparedStatement pstmtSelect = pCon.prepareStatement(strBuff.toString());){
            
            
            pstmtSelect.setString(1, userId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
            	listToReturn.add(rs.getString(columnName));
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append( sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadGeogDomainVoucherTypeList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadValuesList", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadGeogDomainVoucherTypeList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadValuesList", "error.general.processing");
        } finally {
        
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: "+methodName);
                log.debug(methodName,  loggerValue );
            }
        }
        return listToReturn;
    }

    
}
