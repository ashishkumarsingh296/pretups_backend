/*
 * @# ServiceKeywordDAO.java
 * This class used in the SERVICE KEYWORD MODULE
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 10, 2005 sandeep.goel Initial creation
 * June 16, 2005 avinash.kamthan Added method loadServiceCache()
 * 05/12/2006 Gurjeet Bedi CR 000009 Sub Keyword Fields
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.servicekeyword.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.common.TypesI;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/**
 */
public class ServiceKeywordDAO {
    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for ServiceDAO.
     */
    public ServiceKeywordDAO() {
        super();
    }

    /**
     * Method loadServiceTypeList. This method is used to load the list of the
     * service_type from the table.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceTypeList(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeList", "Entered:");
        ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT service_type,name,sub_keyword_applicable FROM service_type WHERE status='Y' ORDER BY name");
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeList", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                listValueVO.setOtherInfo(rs.getString("sub_keyword_applicable"));
                serviceTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadServiceTypeList", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceTypeList", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeList", "Exiting:list size=" + serviceTypeList.size());
        }
        return serviceTypeList;
    }

    /**
     * Method loadServiceTypeData. This method is used to load the full details
     * of the specified serviceType.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceTypeData(Connection p_con, String p_serviceType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeData", "Entered:serviceType=" + p_serviceType);
        ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ServiceKeywordVO serviceKeywordVO = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT LKP.lookup_name module_name, SK.service_type,keyword,req_interface_type,service_port, ");
            selectQuery.append("SK.name,SK.status,menu,sub_menu,allowed_version,SK.modify_allowed,SK.modified_on,service_keyword_id,ST.sub_keyword_applicable,SK.sub_keyword ");
            selectQuery.append("FROM service_keywords SK,service_type ST,lookups LKP ");
            selectQuery.append("WHERE SK.service_type=? ");
            selectQuery.append("AND ST.service_type=SK.service_type ");
            selectQuery.append("AND LKP.lookup_code=ST.module AND LKP.lookup_type=? AND SK.status <> ? ");
            selectQuery.append("ORDER BY service_type ");
            String query = selectQuery.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeData", "Query=" + query);
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_serviceType);
            pstmtSelect.setString(2, PretupsI.MODULE_TYPE);
            pstmtSelect.setString(3, PretupsI.STATUS_DELETE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceKeywordVO = new ServiceKeywordVO();
                serviceKeywordVO.setServiceType(rs.getString("service_type"));
                serviceKeywordVO.setKeyword(rs.getString("keyword"));
                serviceKeywordVO.setInterface(rs.getString("req_interface_type"));
                serviceKeywordVO.setReceivePort(rs.getString("service_port"));
                // serviceKeywordVO.setResponseCode(rs.getString("res_interface_code"));
                serviceKeywordVO.setName(rs.getString("name"));
                serviceKeywordVO.setStatus(rs.getString("status"));
                serviceKeywordVO.setMenu(rs.getString("menu"));
                serviceKeywordVO.setSubMenu(rs.getString("sub_menu"));
                serviceKeywordVO.setAllowedVersion(rs.getString("allowed_version"));
                serviceKeywordVO.setModifyAllowed(rs.getString("modify_allowed"));
                serviceKeywordVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                serviceKeywordVO.setServiceKeywordID(rs.getString("service_keyword_id"));
                serviceKeywordVO.setModuleDesc(rs.getString("module_name"));

                serviceKeywordVO.setSubKeyword(rs.getString("sub_keyword"));
                if (TypesI.YES.equals(rs.getString("sub_keyword_applicable")))
                    serviceKeywordVO.setSubKeywordApplicable(true);

                serviceTypeList.add(serviceKeywordVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadServiceTypeData", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeData]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeData", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceTypeData", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeData", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeData", "Exiting:list size=" + serviceTypeList.size());
        }
        return serviceTypeList;
    }

    /**
     * Method addServiceType. This method is used to add a new record in the
     * table.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceKeywordVO
     *            ServiceKeywordVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addServiceType(Connection p_con, ServiceKeywordVO p_serviceKeywordVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addServiceType", "Entered:p_serviceKeywordVO=" + p_serviceKeywordVO);
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO service_keywords(service_type,keyword,req_interface_type,service_port,name,status,menu,sub_menu,");
            insertQuery.append("allowed_version,modify_allowed,created_by,created_on,modified_by,modified_on,service_keyword_id,sub_keyword) ");
            insertQuery.append("VALUES(?,UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,UPPER(?)) ");
            String query = insertQuery.toString();
            if (_log.isDebugEnabled())
                _log.debug("addServiceType", "Query=" + query);
            // commented for DB2 pstmtInsert = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(query);
            int i = 1;
            pstmtInsert.setString(i++, p_serviceKeywordVO.getServiceType());

            // for multilanguage support
            // commented for DB2 pstmtInsert.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(i++, p_serviceKeywordVO.getKeyword());

            pstmtInsert.setString(i++, p_serviceKeywordVO.getInterface());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getReceivePort());
            // pstmtInsert.setString(i++, p_serviceKeywordVO.getResponseCode());

            // for multilanguage support
            // commented for DB2 pstmtInsert.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(i++, p_serviceKeywordVO.getName());

            pstmtInsert.setString(i++, p_serviceKeywordVO.getStatus());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getMenu());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getSubMenu());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getAllowedVersion());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getModifyAllowed());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceKeywordVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_serviceKeywordVO.getModifiedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceKeywordVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_serviceKeywordVO.getServiceKeywordID());

            // commented for DB2 pstmtInsert.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(i++, p_serviceKeywordVO.getSubKeyword());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("addServiceType", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[addServiceType]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addServiceType", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("addServiceType", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[addServiceType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addServiceType", "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addServiceType", "Exiting:return=" + addCount);
        }
        return addCount;
    }

    /**
     * Method updateServiceType.
     * This method is used to update the data in the table.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceKeywordVO
     *            ServiceKeywordVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateServiceType(Connection p_con, ServiceKeywordVO p_serviceKeywordVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateServiceType", "Entered:p_serviceKeywordVO=" + p_serviceKeywordVO);
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE  service_keywords SET keyword=UPPER(?),req_interface_type=?,service_port=?,name=?,status=?,");
            updateQuery.append("menu=?,sub_menu=?,allowed_version=?,modified_by=?,modified_on=?,modify_allowed=?,sub_keyword=UPPER(?) ");
            updateQuery.append("WHERE service_keyword_id=? ");
            String query = updateQuery.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateServiceType", "Query=" + query);

            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(query);
            int i = 1;
            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getKeyword());

            pstmtUpdate.setString(i++, p_serviceKeywordVO.getInterface());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getReceivePort());
            // pstmtUpdate.setString(4, p_serviceKeywordVO.getResponseCode());

            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getName());

            pstmtUpdate.setString(i++, p_serviceKeywordVO.getStatus());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getMenu());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getSubMenu());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getAllowedVersion());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceKeywordVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getModifyAllowed());
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getSubKeyword());

            pstmtUpdate.setString(i++, p_serviceKeywordVO.getServiceKeywordID());

            // for the checking is the record modified during the transaction.
            boolean modified = this.isRecordModified(p_con, p_serviceKeywordVO.getLastModifiedTime(), p_serviceKeywordVO.getServiceKeywordID());
            if (modified) {
                throw new BTSLBaseException(this, "updateServiceType", "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error("updateServiceType", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateServiceType]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateServiceType", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateServiceType", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateServiceType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateServiceType", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateServiceType", "Exiting:return=" + updateCount);
        }
        return updateCount;
    }

    /**
     * Method isRecordModified.
     * This method is used to check that is the record modified during the
     * processing.
     * 
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_key
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_key) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isRecordModified", "Entered:p_oldlastModified=" + p_oldlastModified + ",p_key=" + p_key);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        StringBuffer sqlRecordModified = new StringBuffer();
        sqlRecordModified.append("SELECT modified_on FROM service_keywords ");
        sqlRecordModified.append("WHERE service_keyword_id=? ");
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled())
                _log.debug("isRecordModified", "QUERY=" + sqlRecordModified);
            String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_key);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record is modified by other
            // person and the
            // modification is done on the value of the primary key.
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified)
                modified = true;
        }// end of try
        catch (SQLException sqe) {
            _log.error("isRecordModified", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[isRecordModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isRecordModified", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("isRecordModified", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[isRecordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isRecordModified", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isRecordModified", "Exititng:modified=" + modified);
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method isServiceKeywordExist.
     * This method is used to chech the existance of the servicekeyword in the
     * table
     * to maintain the uniqueness of the data record.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceKeywordVO
     *            ServiceKeywordVO
     * @param p_flag
     *            boolean
     * @return boolean
     * @throws BTSLBaseException
     *             CR 000009 Sub Keyword Fields Change
     */
    public boolean isServiceKeywordExist(Connection p_con, ServiceKeywordVO p_serviceKeywordVO, boolean p_flag) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isServiceKeywordExist", "Entered:p_serviceKeywordVO=" + p_serviceKeywordVO + ",p_flag=" + p_flag);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        StringBuffer sqlRecordModified = new StringBuffer();
        String query = null;
        try {
            sqlRecordModified.append("SELECT 1 FROM service_keywords ");
            sqlRecordModified.append("WHERE req_interface_type=? AND keyword=UPPER(?) ");
            if (p_serviceKeywordVO.isSubKeywordApplicable()) {
                sqlRecordModified.append("AND sub_keyword =UPPER(?) ");
                sqlRecordModified.append("AND service_port=?   ");
            } else {
                // sqlRecordModified.append("AND service_type=?  ");
                sqlRecordModified.append("AND service_port=?   ");
            }
            if (p_flag)// for the update record method
            {
                sqlRecordModified.append("AND service_keyword_id !=? ");
            }
            query = sqlRecordModified.toString();
            if (_log.isDebugEnabled())
                _log.debug("isServiceKeywordExist", "QUERY=" + sqlRecordModified);
            pstmtSelect = p_con.prepareStatement(query);
            int i = 1;
            // pstmtSelect.setString(i++, p_serviceKeywordVO.getServiceType());
            pstmtSelect.setString(i++, p_serviceKeywordVO.getInterface());
            pstmtSelect.setString(i++, p_serviceKeywordVO.getKeyword());
            if (p_serviceKeywordVO.isSubKeywordApplicable()) {
                pstmtSelect.setString(i++, p_serviceKeywordVO.getSubKeyword());
                pstmtSelect.setString(i++, p_serviceKeywordVO.getReceivePort());
            } else {
                pstmtSelect.setString(i++, p_serviceKeywordVO.getReceivePort());
            }

            if (p_flag)
                pstmtSelect.setString(i++, p_serviceKeywordVO.getServiceKeywordID());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error("isServiceKeywordExist", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[isServiceKeywordExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isServiceKeywordExist", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("isServiceKeywordExist", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[isServiceKeywordExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isServiceKeywordExist", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isServiceKeywordExist", "Exititng:isExist=" + isExist);
        }// end of finally
        return isExist;
    }// end isServiceKeywordExist

    /**
     * To load the service cache data
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap loadServiceCache() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceCache()", "Entered");
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap serviceMap = new HashMap();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT");
        strBuff.append(" SK.keyword, ST.module, SK.req_interface_type, ST.type, ST.message_format, ");
        strBuff.append(" SK.service_port, ST.service_type, ST.request_handler, ST.unregistered_access_allowed, ");
        strBuff.append(" ST.error_key, SK.allowed_version, SK.status, SK.modified_on as keywordmodified, ");
        strBuff.append(" ST.modified_on  as keywordtypemodified , ST.name name , ST.external_interface extintface, ST.use_interface_language,ST.group_type,ST.sub_keyword_applicable,SK.sub_keyword,ST.REQUEST_PARAM ");
        strBuff.append(" FROM  ");
        strBuff.append(" service_type ST , service_keywords SK ");
        strBuff.append(" WHERE  ");
        strBuff.append(" ST.service_type = SK.service_type AND SK.status <> 'N' AND ST.status <> 'N' ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServiceCache()", "QUERY sqlSelect=" + sqlSelect);

        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ServiceKeywordCacheVO cacheVO = null;
            ServiceKeywordCacheVO cacheVO2 = null;
            ArrayList subKeywordList = null;
            while (rs.next()) {
                cacheVO = new ServiceKeywordCacheVO();

                cacheVO.setKeyword(rs.getString("keyword"));
                cacheVO.setModule(rs.getString("module"));
                cacheVO.setRequestInterfaceType(rs.getString("req_interface_type"));
                cacheVO.setType(rs.getString("type"));
                String servicePort = rs.getString("service_port");
                cacheVO.setServiceType(rs.getString("service_type"));
                cacheVO.setRequestHandlerClass(rs.getString("request_handler"));
                cacheVO.setErrorKey(rs.getString("error_key"));
                cacheVO.setAllowedVersion(rs.getString("allowed_version"));
                cacheVO.setStatus(rs.getString("status"));
                cacheVO.setModifiedServiceType(rs.getDate("keywordmodified"));
                cacheVO.setModifiedOnServiceKeyword(rs.getDate("keywordtypemodified"));
                cacheVO.setModifiedServiceTypeTimestamp(rs.getTimestamp("keywordmodified"));
                cacheVO.setModifiedOnServiceKeywordTimestamp(rs.getTimestamp("keywordtypemodified"));
                cacheVO.setExternalInterface(rs.getString("extintface"));
                cacheVO.setUnregisteredAccessAllowed(rs.getString("unregistered_access_allowed"));
                cacheVO.setName(rs.getString("name"));
                cacheVO.setMessageFormat(rs.getString("message_format"));
                cacheVO.setUseInterfaceLanguage(rs.getString("use_interface_language"));
                cacheVO.setGroupType(rs.getString("group_type"));
                cacheVO.setRequestParam(rs.getString("REQUEST_PARAM"));
                if (TypesI.YES.equals(rs.getString("sub_keyword_applicable")))
                    cacheVO.setSubKeywordApplicable(true);
                cacheVO.setSubKeyword(rs.getString("sub_keyword"));
                String port[] = servicePort.split(",");

                for (int i = 0; i < port.length; i++) {
                    cacheVO2 = new ServiceKeywordCacheVO(cacheVO);
                    cacheVO2.setServerPort(port[i]);
                    String key = cacheVO2.getKeyword() + "_" + cacheVO2.getModule() + "_" + cacheVO2.getRequestInterfaceType() + "_" + cacheVO2.getServerPort();
                    if (serviceMap.containsKey(key)) {
                        subKeywordList = cacheVO2.getSubKeywordList();
                        if (subKeywordList == null)
                            subKeywordList = new ArrayList();
                        subKeywordList.add(cacheVO2);
                        cacheVO2.setSubKeywordList(subKeywordList);
                        serviceMap.put(key, cacheVO2);
                    } else {
                        serviceMap.put(key, cacheVO2);
                    }
                }
                // String key = cacheVO.getKeyword() + "_" +
                // cacheVO.getModule()+ "_" + cacheVO.getRequestInterfaceType()
                // + "_"+ cacheVO.getServerPort();
                // serviceMap.put(key, cacheVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadServiceCache()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceCache()", "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException)
                throw (BTSLBaseException) ex;
            _log.error("loadServiceCache()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServiceCache()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadServiceCache()", "Exiting: Service Map size=" + serviceMap.size());
            }
        }
        return serviceMap;
    }

    /**
     * method loadServiceTypeListForNetworkServices
     * This method to load services having external interface
     * 
     * @param p_con
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public ArrayList loadServiceTypeListForNetworkServices(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeListForNetworkServices", "Entered:");
        ArrayList serviceTypeList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT service_type, description,module ");
            selectQuery.append("FROM  service_type ");
            selectQuery.append("WHERE external_interface='Y' AND status ='Y' ");
            selectQuery.append("ORDER BY description ");
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeListForNetworkServices", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            serviceTypeList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("description"), rs.getString("module") + ":" + rs.getString("service_type"));
                serviceTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadServiceTypeListForNetworkServices", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListForNetworkServices]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeListForNetworkServices", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceTypeListForNetworkServices", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListForNetworkServices]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeListForNetworkServices", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeListForNetworkServices", "Exiting:list size=" + serviceTypeList.size());
        }
        return serviceTypeList;
    }

    /**
     * To load the service types data
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * 
     */
    public HashMap loadServiceTypeCache() throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeCache()", "Entered");

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap serviceTypesMap = new HashMap();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT ST.module,ST.type, ST.message_format, ");
        strBuff.append(" ST.service_type, ST.request_handler, ST.unregistered_access_allowed,ST.file_parser, ");
        strBuff.append(" ST.error_key,  ST.modified_on  as keywordtypemodified , ST.name name , ST.external_interface extintface, ST.use_interface_language,ST.group_type,ST.sub_keyword_applicable,ST.REQUEST_PARAM ");
        strBuff.append(" FROM service_type ST  WHERE ST.status <> 'N' ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeCache()", "QUERY sqlSelect=" + sqlSelect);

        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ServiceKeywordCacheVO cacheVO = null;
            String key = null;
            while (rs.next()) {
                cacheVO = new ServiceKeywordCacheVO();
                cacheVO.setModule(rs.getString("module"));
                cacheVO.setType(rs.getString("type"));
                cacheVO.setServiceType(rs.getString("service_type"));
                cacheVO.setRequestHandlerClass(rs.getString("request_handler"));
                cacheVO.setModifiedOnServiceKeyword(rs.getDate("keywordtypemodified"));
                cacheVO.setModifiedOnServiceKeywordTimestamp(rs.getTimestamp("keywordtypemodified"));
                cacheVO.setExternalInterface(rs.getString("extintface"));
                cacheVO.setUnregisteredAccessAllowed(rs.getString("unregistered_access_allowed"));
                cacheVO.setMessageFormat(rs.getString("message_format"));
                cacheVO.setUseInterfaceLanguage(rs.getString("use_interface_language"));
                cacheVO.setGroupType(rs.getString("group_type"));
                cacheVO.setFileParser(rs.getString("file_parser"));
                cacheVO.setRequestParam(rs.getString("REQUEST_PARAM"));
                if (TypesI.YES.equals(rs.getString("sub_keyword_applicable")))
                    cacheVO.setSubKeywordApplicable(true);
                key = cacheVO.getServiceType() + "_" + cacheVO.getModule();
                serviceTypesMap.put(key, cacheVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadServiceTypeCache()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeCache()", "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException)
                throw (BTSLBaseException) ex;
            _log.error("loadServiceTypeCache()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeCache()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadServiceTypeCache()", "Exiting: Service Map size=" + serviceTypesMap.size());
            }
        }
        return serviceTypesMap;
    }

    /**
     * method loadAllServiceTypeList
     * This method to load services having Status not N
     * 
     * @param p_con
     * @param p_status
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public ArrayList loadAllServiceTypeList(Connection p_con, String p_status) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadAllServiceTypeList", "Entered:");
        ArrayList serviceTypeList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            // module_code, service_type, sender_network, status,
            // language1_message, language2_message, modified_on
            selectQuery.append("SELECT module_code, service_type, sender_network, ");
            selectQuery.append("language1_message, language2_message, modified_on ");
            selectQuery.append("FROM  network_services ");
            selectQuery.append("WHERE status = ? ");
            // selectQuery.append("WHERE sender_network = ? ");
            selectQuery.append("ORDER BY module_code ");
            if (_log.isDebugEnabled())
                _log.debug("loadAllServiceTypeList", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_status);
            rs = pstmtSelect.executeQuery();
            ServiceKeywordVO serviceKeywordVO = null;
            serviceTypeList = new ArrayList();
            while (rs.next()) {
                serviceKeywordVO = new ServiceKeywordVO();
                serviceKeywordVO.setModuleCode(rs.getString("module_code"));
                serviceKeywordVO.setServiceType(rs.getString("service_type"));
                serviceKeywordVO.setSender_network(rs.getString("sender_network"));
                serviceKeywordVO.setLanguage1Message(rs.getString("language1_message"));
                serviceKeywordVO.setLanguage2Message(rs.getString("language2_message"));
                serviceKeywordVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                serviceTypeList.add(serviceKeywordVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadAllServiceTypeList", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadAllServiceTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadAllServiceTypeList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadAllServiceTypeList", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadAllServiceTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadAllServiceTypeList", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadAllServiceTypeList", "Exiting:list size=" + serviceTypeList.size());
        }
        return serviceTypeList;
    }

    /**
     * Method for updateServiceTypeStatus.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            java.util.ArrayList
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateServiceTypeStatus(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        if (_log.isDebugEnabled()) {
            _log.debug("updateServiceTypeStatus", "Entered: p_voList= " + p_voList);
        }

        try {
            // checking the modified status of all the networks one by one
            int listSize = 0;
            int count = 0;
            boolean modified = false;

            if (p_voList != null) {
                listSize = p_voList.size();
            }

            for (int i = 0; i < listSize; i++) {
                ServiceKeywordVO serviceKeywordVO = (ServiceKeywordVO) p_voList.get(i);
                modified = this.recordModified(p_con, serviceKeywordVO.getServiceType(), serviceKeywordVO.getModuleCode(), serviceKeywordVO.getSender_network(), serviceKeywordVO.getLastModified());

                // if modified = true mens record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                } else {
                    count++;
                }
            }
            count = p_voList.size();

            // if count== p_voList means no record is updated
            if ((p_voList != null) && (count == p_voList.size())) {
                count = 0;
                StringBuffer strBuff = new StringBuffer();

                strBuff.append("Update network_services SET status =?, modified_by =?, modified_on =? ");
                strBuff.append(" WHERE service_type=? AND module_code =? AND sender_network=? ");

                String updateQuery = strBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug("updateServiceTypeStatus", "Query sqlUpdate:" + updateQuery);
                }

                // commented for DB2 psmtUpdate = (OraclePreparedStatement)
                // p_con.prepareStatement(updateQuery);
                psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
                for (int i = 0; i < listSize; i++) {
                    ServiceKeywordVO serviceKeywordVO = (ServiceKeywordVO) p_voList.get(i);

                    psmtUpdate.setString(1, serviceKeywordVO.getStatus());
                    psmtUpdate.setString(2, serviceKeywordVO.getModifiedBy());
                    psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(serviceKeywordVO.getModifiedOn()));
                    psmtUpdate.setString(4, serviceKeywordVO.getServiceType());
                    psmtUpdate.setString(5, serviceKeywordVO.getModuleCode());
                    psmtUpdate.setString(6, serviceKeywordVO.getSender_network());

                    updateCount = psmtUpdate.executeUpdate();

                    psmtUpdate.clearParameters();

                    // check the status of the update
                    if (updateCount > 0) {
                        count++;
                    }
                }

                if (count == p_voList.size())
                    updateCount = 1;
                else
                    updateCount = 0;
            }
        } // end of try
        catch (BTSLBaseException be) {
            _log.error("updateServiceTypeStatus", "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error("updateServiceTypeStatus", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateServiceTypeStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateServiceTypeStatus", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("updateServiceTypeStatus", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateNetworkStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateServiceTypeStatus", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("updateServiceTypeStatus", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param con
     *            Connection
     * @param serviceTypeCode
     *            String
     * @param oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String serviceTypeCode, String module, String networkCode, long oldLastModified) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("recordModified", " Entered: serviceTypeCode= " + serviceTypeCode + " module= " + module + " oldLastModified= " + oldLastModified);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM network_services WHERE service_type=? AND module_code = ? AND sender_network = ? ";
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", "QUERY: sqlselect= " + sqlRecordModified);
            }
            // create a prepared statement and execute it
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, serviceTypeCode);
            pstmt.setString(2, module);
            pstmt.setString(3, networkCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", " old=" + oldLastModified);
                _log.debug("recordModified", " new=" + newLastModified.getTime());
            }
            if (newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error("recordModified", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("recordModified", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.processing");
        } // end of catch

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method loadServiceTypeListForServiceSelector. This method is used to load
     * the list of the
     * service_type from the table.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceTypeListForServiceSelector(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeListForServiceSelector", "Entered:");
        ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT service_type,name,sub_keyword_applicable FROM service_type WHERE status='Y' and external_interface='Y' and module='C2S' ORDER BY name");
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeListForServiceSelector", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                listValueVO.setOtherInfo(rs.getString("sub_keyword_applicable"));
                serviceTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadServiceTypeListForServiceSelector", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListForServiceSelector]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeListForServiceSelector", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceTypeListForServiceSelector", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListForServiceSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeListForServiceSelector", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeListForServiceSelector", "Exiting:list size=" + serviceTypeList.size());
        }
        return serviceTypeList;
    }

}
