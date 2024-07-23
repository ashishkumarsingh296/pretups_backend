/*
 * @# ServiceGpMgmtDAO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Rajdeep Deb September 25, 2009 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2009 Comviva Technologies.
 */
package com.btsl.pretups.servicegpmgt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

// import oracle.jdbc.PreparedStatement;


import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class ServiceGpMgmtDAO  {

    /*
     * this method inserts the new service group and returns the number of rows
     * inserted
     * 
     * @param con
     * 
     * @param serviceidmgmtvo
     * 
     * @return addCount
     * 
     * @author Lalit
     */
    public int addServiceGroup(Connection con, ServiceGpMgmtVO serviceGpMgmtVO) throws BTSLBaseException {
    	  final String methodName = "addServiceGroup";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:serviceGpMgmtVO=" + serviceGpMgmtVO);
        }
      
        PreparedStatement pstmtInsert = null;
        Date date = new Date();
        int addCount = 0;
        try {
            boolean isnameexist = isGroupNameExist(con, serviceGpMgmtVO.getGroupName(), PretupsI.SERVICE_ID_MAPPING_ADD, serviceGpMgmtVO.getGroupId());
            boolean iscodeexist = isGroupCodeExist(con, serviceGpMgmtVO.getGroupCode(), PretupsI.SERVICE_ID_MAPPING_ADD, serviceGpMgmtVO.getGroupId());
            if (isnameexist) {
                return -2;
            }
            if (iscodeexist) {
                return -3;
            }

            if ((!isnameexist) && (!iscodeexist)) {
                StringBuilder insertQuery = new StringBuilder();
                insertQuery.append("INSERT INTO SERVICE_PROVIDER_GROUPS(group_id, group_name, group_code,");
                insertQuery.append("status, created_on, created_by, modified_on,modified_by,network_code) ");
                insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?)");
                String query = insertQuery.toString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("addServiceGroup", "Query=" + query);
                }
                pstmtInsert = (PreparedStatement) con.prepareStatement(query);

                pstmtInsert.setString(1, serviceGpMgmtVO.getGroupId());
                pstmtInsert.setString(2, serviceGpMgmtVO.getGroupName());
                pstmtInsert.setString(3, serviceGpMgmtVO.getGroupCode());
                pstmtInsert.setString(4, serviceGpMgmtVO.getStatus());
                pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(date));
                pstmtInsert.setString(6, serviceGpMgmtVO.getCreatedBy());
                pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(date));
                pstmtInsert.setString(8, serviceGpMgmtVO.getModifiedBy());
                pstmtInsert.setString(9, serviceGpMgmtVO.getNetworkCode());

                addCount = pstmtInsert.executeUpdate();

            }

        } catch (SQLException sqe) {
            LOG.error("addServiceGroup", "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[addServiceGroup]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addServiceGroup", "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error("addServiceGroup", "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[addServiceGroup]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addServiceGroup", "error.general.sql.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("addServiceGroup", "Exiting:return=" + addCount);
            }
        }
        return addCount;
    }

    /*
     * this method modify an existing service group in the system
     * 
     * @param con
     * 
     * @param serviceidmgmtvo
     * 
     * @return modifyCount
     * 
     * @author Lalit
     */
    public int modifyServiceGroup(Connection con, ServiceGpMgmtVO serviceidmgmtvo) throws BTSLBaseException {
        final String methodName = "modifyServiceGroup";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_cellidmgmtvo=" + serviceidmgmtvo);
        }
    
        PreparedStatement pstmtInsert = null;
        int modifyCount = 0;
        Date date = new Date();
        try {
            boolean isnameexist = isGroupNameExist(con, serviceidmgmtvo.getGroupName(), PretupsI.SERVICE_ID_MAPPING_MODIFY, serviceidmgmtvo.getGroupId());
            boolean iscodeexist = isGroupCodeExist(con, serviceidmgmtvo.getGroupCode(), PretupsI.SERVICE_ID_MAPPING_MODIFY, serviceidmgmtvo.getGroupId());
            if (isnameexist) {
                return -2;
            }
            if (iscodeexist) {
                return -3;
            }
            if ((!isnameexist) && (!iscodeexist)) {
                StringBuilder insertQuery = new StringBuilder();
                insertQuery.append("UPDATE SERVICE_PROVIDER_GROUPS SET group_name=? , group_code=? , status=? ,");
                insertQuery.append("modified_on=? , modified_by=?  WHERE group_id=? ");
                String query = insertQuery.toString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("modifyServiceGroup", "Query=" + query);
                }
                pstmtInsert = (PreparedStatement) con.prepareStatement(query);
                pstmtInsert.setString(1, serviceidmgmtvo.getGroupName());
                pstmtInsert.setString(2, serviceidmgmtvo.getGroupCode());
                pstmtInsert.setString(3, serviceidmgmtvo.getStatus());
                pstmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                pstmtInsert.setString(5, serviceidmgmtvo.getModifiedBy());
                pstmtInsert.setString(6, serviceidmgmtvo.getGroupId());

                modifyCount = pstmtInsert.executeUpdate();
            }

        } catch (SQLException sqe) {
            LOG.error("modifyServiceGroup", "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[modifyServiceGroup]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "modifyServiceGroup", "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error("modifyServiceGroup", "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[modifyServiceGroup]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "modifyServiceGroup", "error.general.sql.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("modifyServiceGroup", "Exiting:return=" + modifyCount);
            }
        }
        return modifyCount;
    }

    /*
     * this method deletes an existing service group in the system
     * 
     * @param con
     * 
     * @param serviceidmgmtvo
     * 
     * @return deleteCount
     * 
     * @author Lalit
     */
    public int deleteServcieGroup(Connection con, ServiceGpMgmtVO serviceidmgmtvo) throws BTSLBaseException {
        final String methodName = "deleteServcieGroup";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_cellidmgmtvo=" + serviceidmgmtvo);
        }
    
        PreparedStatement pstmtInsert = null;
        int deleteCount = 0;
        boolean isServiceGroupActive = isServcieGroupExist(con, serviceidmgmtvo.getGroupId());
        try {
            if (isServiceGroupActive) {
                return -2;
            }
            if (!isServiceGroupActive) {
                StringBuilder insertQuery = new StringBuilder();
                insertQuery.append("UPDATE SERVICE_PROVIDER_GROUPS SET status = 'N' WHERE group_id=?");
                String query = insertQuery.toString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("deleteServcieGroup", "Query=" + query);
                }
                pstmtInsert = (PreparedStatement) con.prepareStatement(query);
                pstmtInsert.setString(1, serviceidmgmtvo.getGroupId());
                deleteCount = pstmtInsert.executeUpdate();
            }
        } catch (SQLException sqe) {
            LOG.error("deleteServcieGroup", "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[deleteServcieGroup]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "deleteServcieGroup", "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error("deleteserviceGroup", "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[deleteServcieGroup]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteServcieGroup", "error.general.sql.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }

            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("deleteServcieGroup", "Exiting:return=" + deleteCount);
            }
        }
        return deleteCount;
    }

    /*
     * this method check the association of a service group to any service
     * 
     * @param con
     * 
     * @param groupid
     * 
     * @return isExist
     * 
     * @author lalit
     */
    public boolean isServcieGroupExist(Connection con, String groupid) throws BTSLBaseException {
    	   final String methodName = "isServcieGroupExist";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_grphDomainCode=" + groupid);
        }
     
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        StringBuilder sqlRecordExist = new StringBuilder();
        try {
            sqlRecordExist.append("SELECT 1 FROM  SERVICE_PROVIDER  ");
            sqlRecordExist.append("WHERE group_id=? AND status <> 'N'");
            pstmtSelect = con.prepareStatement(sqlRecordExist.toString());
            pstmtSelect.setString(1, groupid);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            LOG.error("isServcieGroupExist", "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[isServcieGroupExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isServcieGroupExist", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("isServcieGroupExist", "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[isServcieGroupExist]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isServcieGroupExist", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("isServcieGroupExist", "Exititng:isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }

    /*
     * this method checks the existence of the group name in the system
     * 
     * @param con
     * 
     * @param p_groupName
     * 
     * @return isExist
     * 
     * @author Lalit
     */
    public boolean isGroupNameExist(Connection con, String p_groupName, String optmode, String groupid) throws BTSLBaseException {
    	   final String methodName = "isGroupNameExist";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_groupName=" + p_groupName);
        }
     
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs = null;
        boolean isExist = false;
        StringBuilder sqlRecordExist = new StringBuilder();
        try {
            if (PretupsI.SERVICE_ID_MAPPING_ADD.equals(optmode)) {
                sqlRecordExist.append("SELECT 1 FROM  SERVICE_PROVIDER_GROUPS ");
                sqlRecordExist.append("WHERE group_name=? ");
                sqlRecordExist.append("AND status <> 'N'");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("isGroupNameExist", "QUERY=" + sqlRecordExist);
                }
                pstmtSelect = con.prepareStatement(sqlRecordExist.toString());
                pstmtSelect.setString(1, p_groupName);
            }
            if (PretupsI.SERVICE_ID_MAPPING_MODIFY.equals(optmode)) {
                sqlRecordExist.append("SELECT 1 FROM  SERVICE_PROVIDER_GROUPS ");
                sqlRecordExist.append("WHERE group_name=? ");
                sqlRecordExist.append("AND status <> 'N' AND group_id <> ?");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("isGroupNameExist", "QUERY=" + sqlRecordExist);
                }
                pstmtSelect1 = con.prepareStatement(sqlRecordExist.toString());
                pstmtSelect1.setString(1, p_groupName);
                pstmtSelect1.setString(2, groupid);
            }
            rs = pstmtSelect1.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            LOG.error("isGroupNameExist", "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[isGroupNameExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isGroupNameExist", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("isGroupNameExist", "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[isGroupNameExist]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isGroupNameExist", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            try{
            	if (pstmtSelect1!= null){
            		pstmtSelect1.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("isGroupNameExist", "Exititng:isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }

    /*
     * this method is to check the existence of the group code in the system
     * 
     * @param con
     * 
     * @param p_groupCode
     * 
     * @return isExist
     * 
     * @author Lalit
     */
    public boolean isGroupCodeExist(Connection con, String p_groupCode, String optmode, String groupid) throws BTSLBaseException {
    	final String methodName = "isGroupCodeExist";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_groupCode=" + p_groupCode);
        }
        
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs = null;
        boolean isExist = false;
        StringBuilder sqlRecordExist = new StringBuilder();
        try {
            if (PretupsI.SERVICE_ID_MAPPING_ADD.equals(optmode)) {
                sqlRecordExist.append("SELECT 1 FROM  SERVICE_PROVIDER_GROUPS ");
                sqlRecordExist.append("WHERE group_code=? ");
                sqlRecordExist.append("AND status <> 'N'");

                if (LOG.isDebugEnabled()) {
                    LOG.debug("isGroupCodeExist", "QUERY=" + sqlRecordExist);
                }

                pstmtSelect = con.prepareStatement(sqlRecordExist.toString());
                pstmtSelect.setString(1, p_groupCode);
            }
            if (PretupsI.SERVICE_ID_MAPPING_MODIFY.equals(optmode)) {
                sqlRecordExist.append("SELECT 1 FROM  SERVICE_PROVIDER_GROUPS ");
                sqlRecordExist.append("WHERE group_code=? ");
                sqlRecordExist.append("AND status <> 'N' AND group_id <> ?");

                if (LOG.isDebugEnabled()) {
                    LOG.debug("isGroupCodeExist", "QUERY=" + sqlRecordExist);
                }

                pstmtSelect1 = con.prepareStatement(sqlRecordExist.toString());
                pstmtSelect1.setString(1, p_groupCode);
                pstmtSelect1.setString(2, groupid);
            }
            rs = pstmtSelect1.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            LOG.error("isGroupCodeExist", "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[isGroupCodeExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isGroupCodeExist", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("isGroupCodeExist", "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[isGroupCodeExist]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isGroupCodeExist", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            try{
            	if (pstmtSelect1!= null){
            		pstmtSelect1.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("isGroupCodeExist", "Exititng:isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }

    /*
     * this method loads the service group list ..
     * 
     * @param con
     * 
     * @return serviceGroupList
     * 
     * @author Lalit
     */
    public ArrayList<ServiceGpMgmtVO> getServiceGroupList(Connection con, String networkCode) throws BTSLBaseException {
    	  final String methodName = "getServiceGroupList";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:");
        }
      
        ArrayList<ServiceGpMgmtVO> serviceGroupList = new ArrayList<ServiceGpMgmtVO>();
        PreparedStatement pstmtSelect = null;
        ServiceGpMgmtVO servicemgmtVO = null;
        ResultSet rs = null;
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT group_id,group_name, group_code,status,");
            selectQuery.append("created_on,created_by,modified_on,modified_by,network_code");
            selectQuery.append(" FROM SERVICE_PROVIDER_GROUPS");
            selectQuery.append(" WHERE status <> 'N' AND network_code=? order by UPPER(group_name)");
            if (LOG.isDebugEnabled()) {
                LOG.debug("getServiceGroupList", "Query=" + selectQuery);
            }
            pstmtSelect = con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                servicemgmtVO = new ServiceGpMgmtVO();
                servicemgmtVO.setGroupId(rs.getString("group_id"));
                servicemgmtVO.setGroupName(rs.getString("group_name"));
                servicemgmtVO.setGroupCode(rs.getString("group_code"));
                servicemgmtVO.setStatus(rs.getString("status"));
                if (PretupsI.STATUS_ACTIVE.equals(rs.getString("status"))) {
                    servicemgmtVO.setStatusDescription(PretupsI.SERVICE_ACTIVE_STATUS);
                }
                if (PretupsI.STATUS_SUSPEND.equals(rs.getString("status"))) {
                    servicemgmtVO.setStatusDescription(PretupsI.SERVICE_SUSPEND_STATUS);
                }
                servicemgmtVO.setCreatedOn(BTSLUtil.getDateStringFromDate(rs.getDate("created_on")));
                servicemgmtVO.setCreatedBy(rs.getString("created_by"));
                servicemgmtVO.setModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
                servicemgmtVO.setModifiedBy(rs.getString("modified_by"));
                servicemgmtVO.setNetworkCode(rs.getString("network_code"));
                //
                servicemgmtVO.setRadioIndex("0");
                serviceGroupList.add(servicemgmtVO);
            }
        } catch (SQLException sqe) {
            LOG.error("getServiceGroupList", "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[getServiceGroupList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getServiceGroupList", "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error("getServiceGroupList", "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[getServiceGroupList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getServiceGroupList", "error.general.sql.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("getServiceGroupList", "Exiting:list size=" + serviceGroupList.size());
            }
        }

        return serviceGroupList;
    }

    private Log LOG = LogFactory.getLog(this.getClass().getName());

    /**
     * Method :addServiceGroupAndServiceIdMapping
     * This method check the data base validation
     * and after validation insert into channel user related tables.
     * 
     * @param con
     *            Connection
     * @param serviceIdVOList
     *            ArrayList
     * @param messages
     *            MessageResources
     * @param locale
     *            Locale
     * @param p_userVO
     *            TODO
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Lalit
     */
    public ArrayList addServiceGroupAndServiceIdMapping(Connection con, ArrayList serviceIdVOList, MessageResources messages, Locale locale, UserVO p_userVO, String fileName) throws BTSLBaseException {
    	 final String methodName = "addServiceGroupAndServiceIdMapping";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered " + fileName);
        }
       
        ArrayList<ListValueVO> errorList = new ArrayList<ListValueVO>();
        ListValueVO errorVO = null;
        int commitCounter = 0;
        PreparedStatement pstmtSelectServiceID = null, pstmtInsertServiceID = null;
        ResultSet rsServiceID = null;
        // service id validation of uniqueness
        StringBuilder selectServiceIdDuplicate = new StringBuilder("SELECT 1 from SERVICE_PROVIDER SP ");
        selectServiceIdDuplicate.append(" WHERE SP.SERVICE_PROVIDER_NAME = ? AND SP.status <> 'N'");

        // Insert into the database
        StringBuilder insertServiceIdTable = new StringBuilder("INSERT INTO SERVICE_PROVIDER (group_id, SERVICE_PROVIDER_NAME, status, created_on,");
        insertServiceIdTable.append("created_by, network_code, file_name) VALUES(?,?,?,?,?,?,?)");
        if (LOG.isDebugEnabled()) {
            LOG.debug("addServiceGroupAndServiceIdMapping", "SelectServiceID Query =" + selectServiceIdDuplicate);
            LOG.debug("addServiceGroupAndServiceIdMapping", "InsertServiceID Query =" + insertServiceIdTable);
        }
        try {
            pstmtSelectServiceID = con.prepareStatement(selectServiceIdDuplicate.toString());
            pstmtInsertServiceID = con.prepareStatement(insertServiceIdTable.toString());
            ListSorterUtil sort = new ListSorterUtil();
            serviceIdVOList = (ArrayList) sort.doSort("serviceName", null, serviceIdVOList);
            ServiceGpMgmtVO serviceGpMgmtVO = null;

            for (int i = 0, length = serviceIdVOList.size(); i < length; i++) {
                serviceGpMgmtVO = (ServiceGpMgmtVO) serviceIdVOList.get(i);
                Date date = new Date();
                pstmtSelectServiceID.setString(1, serviceGpMgmtVO.getServiceName());
                rsServiceID = pstmtSelectServiceID.executeQuery();
                if (rsServiceID.next()) {
                    errorVO = new ListValueVO("", serviceGpMgmtVO.getRecordNumber(), messages.getMessage(locale, "servicegroup.upload.validate.file.msg.error.serviceiduniqueerr", new String[] { serviceGpMgmtVO.getServiceName() }));
                    errorList.add(errorVO);
                    continue;
                }
                pstmtInsertServiceID.clearParameters();
                pstmtInsertServiceID.setString(1, serviceGpMgmtVO.getGroupId());
                pstmtInsertServiceID.setString(2, serviceGpMgmtVO.getServiceName());
                pstmtInsertServiceID.setString(3, serviceGpMgmtVO.getStatus());
                pstmtInsertServiceID.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                pstmtInsertServiceID.setString(5, p_userVO.getActiveUserID());
                pstmtInsertServiceID.setString(6, p_userVO.getNetworkID());
                pstmtInsertServiceID.setString(7, serviceGpMgmtVO.getFileName());
                if (pstmtInsertServiceID.executeUpdate() > 0) {
                    con.commit();
                    commitCounter++;
                    continue;
                } else {
                    con.rollback();
                    continue;
                }
            }
        } catch (SQLException sqe) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error("addServiceGroupAndServiceIdMapping", "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[addServiceGroupAndServiceIdMapping]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addServiceGroupAndServiceIdMapping", "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error("addServiceGroupAndServiceIdMapping", "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[addServiceGroupAndServiceIdMapping]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "addServiceGroupAndServiceIdMapping", "error.general.processing");
        } finally {
            try {
                if (pstmtInsertServiceID != null) {
                    pstmtInsertServiceID.close();
                }
                if (pstmtSelectServiceID != null) {
                    pstmtSelectServiceID.close();
                }
                if (rsServiceID != null) {
                    rsServiceID.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("addServiceGroupAndServiceIdMapping ", " Exiting count for inserted service id mapping = " + commitCounter);
        }
        return errorList;
    }

    /**
     * Method :loadServiceidDeatilsVOList
     * This method check the data base validation
     * and after validation insert into channel user related tables.
     * 
     * @param con
     *            Connection
     * @param networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author lalit
     */

    public ArrayList loadServiceidDeatilsVOList(Connection con, String networkCode) throws BTSLBaseException {
    	final String methodName = "loadServiceidDeatilsVOList";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered ");
        }
        
        ArrayList<ServiceGpMgmtVO> detailsList = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ServiceGpMgmtVO serviceGpMgmtVO = null;
        try {
            StringBuilder strBuff = new StringBuilder("SELECT sp.GROUP_ID,sp.SERVICE_PROVIDER_NAME,spg.GROUP_NAME,sp.STATUS,sp.CREATED_BY,sp.CREATED_ON,sp.MODIFIED_BY,sp.MODIFIED_ON,sp.FILE_NAME ");
            strBuff.append("FROM SERVICE_PROVIDER sp,SERVICE_PROVIDER_GROUPS spg ");
            strBuff.append("WHERE sp.network_code=? AND sp.GROUP_ID=spg.GROUP_ID AND sp.STATUS <> 'N' ORDER BY spg.GROUP_ID");
            if (LOG.isDebugEnabled()) {
                LOG.debug("loadServiceidDeatilsVOList ", " SQL Query " + strBuff.toString());
            }
            pstmt = con.prepareStatement(strBuff.toString());
            pstmt.setString(1, networkCode);
            rs = pstmt.executeQuery();
            detailsList = new ArrayList<ServiceGpMgmtVO>();
            while (rs.next()) {
                serviceGpMgmtVO = new ServiceGpMgmtVO();
                serviceGpMgmtVO.setGroupId(SqlParameterEncoder.encodeParams(rs.getString("group_id")));
                serviceGpMgmtVO.setServiceName(SqlParameterEncoder.encodeParams(rs.getString("SERVICE_PROVIDER_NAME")));
                serviceGpMgmtVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                serviceGpMgmtVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                serviceGpMgmtVO.setCreatedOn(BTSLUtil.getDateStringFromDate(rs.getDate("created_on")));
                if (rs.getString("file_name") != null && rs.getDate("modified_on") != null) {
                    serviceGpMgmtVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                    serviceGpMgmtVO.setModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
                }
                serviceGpMgmtVO.setFileName(SqlParameterEncoder.encodeParams(rs.getString("file_name")));
                detailsList.add(serviceGpMgmtVO);
            }
        } catch (SQLException sqe) {
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[loadServiceidDeatilsVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceidDeatilsVOList", "error.general.sql.processing");
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[loadServiceidDeatilsVOList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceidDeatilsVOList", "error.general.processing");
        } finally {
            try {
            	if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                
            } catch (Exception sqe) {
                LOG.errorTrace(methodName, sqe);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadServiceidDeatilsVOList ", " Exiting " + detailsList);
        }
        return detailsList;
    }

    public String getServiceGroupFromServiceId(Connection con, String serviceId) throws BTSLBaseException {
    	final String methodName = "getServiceGroupFromServiceId";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: serviceId=" + serviceId);
        }
        
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String serviceGroup = null;
        StringBuilder sqlStmt = new StringBuilder();
        try {
            sqlStmt.append("SELECT spg.group_id  FROM  SERVICE_PROVIDER sp, SERVICE_PROVIDER_GROUPS spg ");
            sqlStmt.append("WHERE sp.group_id= spg.group_id and sp.SERVICE_PROVIDER_NAME=? AND sp.status=? and spg.status=?");
            pstmtSelect = con.prepareStatement(sqlStmt.toString());
            pstmtSelect.setString(1, serviceId);
            pstmtSelect.setString(2, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(3, PretupsI.STATUS_ACTIVE);

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                serviceGroup = rs.getString("group_id");
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[getServiceGroupFromServiceId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error("getServiceGroupFromServiceId", "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[getServiceGroupFromServiceId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                LOG.error(methodName, "Exception while closing resultSet/PrepareStatement:" + ex.toString());
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exititng: serviceGroup=" + serviceGroup);
            }
        }
        return serviceGroup;
    }

    /**
     * @author lalit
     * @param con
     * @param serviceVOLstMap
     * @param messages
     * @param locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList reAssociateServiceGroupIdWithServiceId(Connection con, HashMap serviceVOLstMap, MessageResources messages, Locale locale, String fileName) throws BTSLBaseException {
        final String methodName = "reAssociateServiceGroupIdWithServiceId";

    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered ");
        }
        ArrayList errorList = null;
        ArrayList tempErrorList = null;
        ArrayList modifyServiceIdStatusList = null;
        ArrayList reAssociateServiceGroupIdList = null;
        try {
            if (serviceVOLstMap.containsKey(PretupsI.SERVICE_ID_MODIFY_STATUS)) {
                modifyServiceIdStatusList = (ArrayList) serviceVOLstMap.get(PretupsI.SERVICE_ID_MODIFY_STATUS);
            }
            if (serviceVOLstMap.containsKey(PretupsI.SERVICE_ID_REASSOCIATE_SERVICEGRPID)) {
                reAssociateServiceGroupIdList = (ArrayList) serviceVOLstMap.get(PretupsI.SERVICE_ID_REASSOCIATE_SERVICEGRPID);
            }
            tempErrorList = new ArrayList();
            errorList = new ArrayList();
            if (modifyServiceIdStatusList != null && !modifyServiceIdStatusList.isEmpty()) {
                tempErrorList = this.modifyServiceIdStatus(con, modifyServiceIdStatusList, messages, locale, fileName);
                if (!(tempErrorList == null) && !tempErrorList.isEmpty()) {
                    errorList.addAll(tempErrorList);
                    tempErrorList = null;
                }
            }
            if (reAssociateServiceGroupIdList != null && !reAssociateServiceGroupIdList.isEmpty()) {
                tempErrorList = this.reAssociateServiceGroupId(con, reAssociateServiceGroupIdList, messages, locale, fileName);
                if (!(tempErrorList == null) && !tempErrorList.isEmpty()) {
                    errorList.addAll(tempErrorList);
                    tempErrorList = null;
                }
            }

        } catch (Exception e) {
            LOG.error(methodName, "Errors" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[reAssociateServiceGroupIdWithServiceId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting ");
        }
        return errorList;
    }

    /**
     * @author Lalit
     * @param con
     * @param p_modifyServiceIdStatusList
     * @param messages
     * @param locale
     * @return
     * @throws BTSLBaseException
     */
    private ArrayList modifyServiceIdStatus(Connection con, ArrayList p_modifyServiceIdStatusList, MessageResources messages, Locale locale, String fileName) throws BTSLBaseException {
    	 final String methodName = "modifyServiceIdStatus";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_modifyServiceIdStatusList size = " + p_modifyServiceIdStatusList.size());
        }
       
        ArrayList<ListValueVO> errorList = new ArrayList<ListValueVO>();
        PreparedStatement pstmt = null;
        ServiceGpMgmtVO serviceGpMgmtVO = null;
        ListValueVO errorVO = null;
        int modStatusCount = 0;
        Date date = new Date();
        StringBuilder modStatusQuery = new StringBuilder("UPDATE SERVICE_PROVIDER SP SET SP.status=?, SP.modified_on=?,SP.modified_by=?, SP.file_name=? WHERE SP.SERVICE_PROVIDER_NAME=? AND SP.group_id=? AND SP.status=?");
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Modify Status Query  =  " + modStatusQuery);
        }
        try {
            pstmt = con.prepareStatement(modStatusQuery.toString());
            for (int i = 0; i < p_modifyServiceIdStatusList.size(); i++) {
                serviceGpMgmtVO = (ServiceGpMgmtVO) p_modifyServiceIdStatusList.get(i);
                pstmt.setString(1, serviceGpMgmtVO.getModstatus());
                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                pstmt.setString(3, serviceGpMgmtVO.getModifiedBy());
                pstmt.setString(4, fileName);
                pstmt.setString(5, serviceGpMgmtVO.getServiceName());
                pstmt.setString(6, serviceGpMgmtVO.getGroupId());
                pstmt.setString(7, serviceGpMgmtVO.getStatus());

                int update = pstmt.executeUpdate();
                if (update > 0) {
                    modStatusCount++;
                } else {
                    errorVO = new ListValueVO("", (new Integer(serviceGpMgmtVO.getRecordNumber())).toString(), messages.getMessage(locale, "servicegroup.reassociate.dberror.nomatchfound"));
                    errorList.add(errorVO);
                    continue;
                }
            }
            // Commit the DB if any statement updated into DB.
            if (modStatusCount > 0) {
                con.commit();
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "Service Id status could not modified " + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(methodName, "Service Id status could not modified " + e.getMessage());
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.error(methodName, "Exception while closing resultSet/PrepareStatement:" + ex.toString());
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting " + modStatusCount);
            }
        }
        return errorList;
    }

    /**
     * @author nand.sahu
     * @param con
     * @param p_reAssociateServiceGroupIdList
     * @param messages
     * @param locale
     * @return
     * @throws BTSLBaseException
     */
    private ArrayList reAssociateServiceGroupId(Connection con, ArrayList p_reAssociateServiceGroupIdList, MessageResources messages, Locale locale, String fileName) throws BTSLBaseException {
    	  final String methodName = "reAssociateServiceGroupId";
    	if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_reAssociateCellGroupIdList size = " + p_reAssociateServiceGroupIdList.size());
        }
      
        ArrayList<ListValueVO> errorList = new ArrayList<ListValueVO>();
        PreparedStatement pstmt = null;
        ServiceGpMgmtVO serviceGpMgmtVO = null;
        ListValueVO errorVO = null;
        int reAssociateCount = 0;
        Date date = new Date();
        StringBuilder modStatusQuery = new StringBuilder("UPDATE SERVICE_PROVIDER SP SET SP.group_id=?, SP.modified_on=?,SP.modified_by=?, SP.file_name=? WHERE SP.SERVICE_PROVIDER_NAME=? AND SP.group_id=? AND SP.status<>'N'");
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Modify Status Query  =  " + modStatusQuery);
        }
        try {
            pstmt = con.prepareStatement(modStatusQuery.toString());
            for (int i = 0; i < p_reAssociateServiceGroupIdList.size(); i++) {
                serviceGpMgmtVO = (ServiceGpMgmtVO) p_reAssociateServiceGroupIdList.get(i);
                pstmt.setString(1, serviceGpMgmtVO.getNewGroupId());
                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                pstmt.setString(3, serviceGpMgmtVO.getModifiedBy());
                pstmt.setString(4, fileName);
                pstmt.setString(5, serviceGpMgmtVO.getServiceName());
                pstmt.setString(6, serviceGpMgmtVO.getGroupId());

                int update = pstmt.executeUpdate();
                if (update > 0) {
                    reAssociateCount++;
                } else {
                    errorVO = new ListValueVO("", (new Integer(serviceGpMgmtVO.getRecordNumber())).toString(), messages.getMessage(locale, "servicegroup.reassociate.dberror.nomatchfound"));
                    errorList.add(errorVO);
                    continue;
                }
            }
            // Commit the DB if any statement updated into DB.
            if (reAssociateCount > 0) {
                con.commit();
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "Service Id could not re-associated " + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error("reAssociateServiceGroupId ", "Service Id could not re-associated " + e.getMessage());
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.error(methodName, "Exception while closing resultSet/PrepareStatement:" + ex.toString());
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting " + reAssociateCount);
            }
        }
        return errorList;
    }
    
    
    
    
    
    public ArrayList<ServiceGpMgmtVO> validateServiceGroupList(Connection con, String networkCode,String serviceGroupID) throws BTSLBaseException {
  	  final String methodName = "getServiceGroupList";
  	if (LOG.isDebugEnabled()) {
          LOG.debug(methodName, "Entered:");
      }
    
      ArrayList<ServiceGpMgmtVO> serviceGroupList = new ArrayList<ServiceGpMgmtVO>();
      PreparedStatement pstmtSelect = null;
      ServiceGpMgmtVO servicemgmtVO = null;
      ResultSet rs = null;
      try {
          StringBuilder selectQuery = new StringBuilder();
          selectQuery.append("SELECT group_id,group_name, group_code,status,");
          selectQuery.append("created_on,created_by,modified_on,modified_by,network_code");
          selectQuery.append(" FROM SERVICE_PROVIDER_GROUPS");
          selectQuery.append(" WHERE status <> 'N' AND network_code=?  AND group_id=? ");
          if (LOG.isDebugEnabled()) {
              LOG.debug("getServiceGroupList", "Query=" + selectQuery);
          }
          pstmtSelect = con.prepareStatement(selectQuery.toString());
          pstmtSelect.setString(1, networkCode);
          pstmtSelect.setString(2, serviceGroupID);
          rs = pstmtSelect.executeQuery();
          while (rs.next()) {
              servicemgmtVO = new ServiceGpMgmtVO();
              servicemgmtVO.setGroupId(rs.getString("group_id"));
              servicemgmtVO.setGroupName(rs.getString("group_name"));
              servicemgmtVO.setGroupCode(rs.getString("group_code"));
              servicemgmtVO.setStatus(rs.getString("status"));
              if (PretupsI.STATUS_ACTIVE.equals(rs.getString("status"))) {
                  servicemgmtVO.setStatusDescription(PretupsI.SERVICE_ACTIVE_STATUS);
              }
              if (PretupsI.STATUS_SUSPEND.equals(rs.getString("status"))) {
                  servicemgmtVO.setStatusDescription(PretupsI.SERVICE_SUSPEND_STATUS);
              }
              servicemgmtVO.setCreatedOn(BTSLUtil.getDateStringFromDate(rs.getDate("created_on")));
              servicemgmtVO.setCreatedBy(rs.getString("created_by"));
              servicemgmtVO.setModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
              servicemgmtVO.setModifiedBy(rs.getString("modified_by"));
              servicemgmtVO.setNetworkCode(rs.getString("network_code"));
              //
              servicemgmtVO.setRadioIndex("0");
              serviceGroupList.add(servicemgmtVO);
          }
      } catch (SQLException sqe) {
          LOG.error("getServiceGroupList", "SQLException:" + sqe.getMessage());
          LOG.errorTrace(methodName, sqe);
          EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[getServiceGroupList]", "", "", "", "SQL Exception:" + sqe.getMessage());
          throw new BTSLBaseException(this, "getServiceGroupList", "error.general.sql.processing");
      } catch (Exception e) {
          LOG.error("getServiceGroupList", "Exception:" + e.getMessage());
          LOG.errorTrace(methodName, e);
          EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceGpMgmtDAO[getServiceGroupList]", "", "", "", "SQL Exception:" + e.getMessage());
          throw new BTSLBaseException(this, "getServiceGroupList", "error.general.sql.processing");
      } finally {
          try {
              if (rs != null) {
                  rs.close();
              }
              if (pstmtSelect != null) {
                  pstmtSelect.close();
              }
          } catch (Exception ex) {
              LOG.errorTrace(methodName, ex);
          }
          if (LOG.isDebugEnabled()) {
              LOG.debug("getServiceGroupList", "Exiting:list size=" + serviceGroupList.size());
          }
      }

      return serviceGroupList;
  }

    
    
    
}
