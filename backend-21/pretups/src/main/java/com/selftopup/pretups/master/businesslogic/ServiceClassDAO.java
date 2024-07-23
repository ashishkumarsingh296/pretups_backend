/*
 * #ServiceClassDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 13, 2005 Amit Ruwali Initial creation
 * Nov 18,2005 Sandeep Goel Customization
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.master.businesslogic;

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
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class ServiceClassDAO {
    private Log _log = LogFactory.getFactory().getInstance(ServiceClassDAO.class.getName());

    /**
     * Method loadInterfaceTypesList.
     * This method is used to load the Interface
     * Types(interface_category:interface_type_id) from interface_types
     * table
     * 
     * @param p_con
     *            Connection
     * @param String
     *            p_networkCode
     * @return interfaceList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadInterfaceTypesList(Connection p_con) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceTypesList", "Entering");

        ArrayList interfaceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT interface_type_id,interface_name,interface_category FROM interface_types ORDER BY interface_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceTypesList", "Select Query " + selectQuery);
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                interfaceList.add(new ListValueVO(rs.getString("interface_name"), rs.getString("interface_category") + ":" + rs.getString("interface_type_id")));
            }

            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceTypesList", "Query Executed " + selectQuery);
        } catch (SQLException sqle) {
            _log.error("loadInterfaceTypesList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceTypesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceTypesList", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadInterfaceTypesList", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceTypesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceTypesList", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceTypesList", "Exiting Size " + interfaceList.size());
        }
        return interfaceList;
    }

    /**
     * Method loadInterfaceList.
     * This method is used to load the interface code and name from the
     * interfaces and
     * interface_network_mapping table
     * 
     * @param p_con
     *            Connection
     * @param String
     *            p_networkCode
     * @return interfaceList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadInterfaceList(Connection p_con, String p_networkCode) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceList", "Entering parms p_networkCode:" + p_networkCode);

        ArrayList interfaceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT DISTINCT i.interface_id,i.interface_description,IT.interface_category,IT.interface_type_id ");
        selectQueryBuff.append(" FROM interfaces i,interface_types IT ");
        selectQueryBuff.append(" WHERE IT.interface_type_id=i.interface_type_id");
        selectQueryBuff.append(" AND IT.interface_type_id=i.interface_type_id AND i.status <> ? ");
        selectQueryBuff.append(" ORDER BY i.interface_description");

        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceList", "Select Query " + selectQuery);
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            // pstmtSelect.setString(1,p_networkCode);
            // pstmtSelect.setString(1,PretupsI.INTERFACE_CATEGORY_PREPAID);
            // pstmtSelect.setString(2,PretupsI.INTERFACE_CATEGORY_POSTPAID);
            pstmtSelect.setString(1, PretupsI.STATUS_DELETE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                interfaceList.add(new ListValueVO(rs.getString("interface_description"), rs.getString("interface_category") + ":" + rs.getString("interface_type_id") + "|" + rs.getString("interface_id")));
            }

            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceList", "Query Executed " + selectQuery);
        } catch (SQLException sqle) {
            _log.error("loadInterfaceList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceList", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadInterfaceList", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceList", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceList", "Exiting Size " + interfaceList.size());
        }
        return interfaceList;
    }

    /**
     * Method loadServiceClassDetails.
     * This method is used to load the service class details from the
     * service_classes table
     * according to the interface id
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceCode
     *            String
     * @return interfaceList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadServiceClassDetails(Connection p_con, String p_interfaceCode) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassDetails", "Entering parms  p_interfaceCode:" + p_interfaceCode);

        ArrayList serviceClassList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sc.service_class_id,sc.service_class_code, ");
        selectQueryBuff.append("lk.lookup_name,sc.service_class_name,sc.interface_id,sc.created_on,sc.created_by,sc.status,");
        selectQueryBuff.append("sc.modified_on,sc.modified_by,sc.p2p_sender_suspend, sc.p2p_receiver_suspend, ");
        selectQueryBuff.append("sc.c2s_receiver_suspend, sc.p2p_sender_allowed_status, sc.p2p_sender_denied_status, ");
        selectQueryBuff.append("sc.p2p_receiver_allowed_status, sc.p2p_receiver_denied_status, ");
        selectQueryBuff.append("sc.c2s_receiver_allowed_status, sc.c2s_receiver_denied_status ");
        selectQueryBuff.append("FROM service_classes sc,lookups lk WHERE ");
        selectQueryBuff.append("interface_id=? AND sc.status<>'N' AND lk.lookup_type=? AND lk.lookup_code=sc.status ");
        selectQueryBuff.append("ORDER BY service_class_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceList", "Select Query " + selectQuery);
        try {
            serviceClassList = new ArrayList();
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_interfaceCode);
            pstmtSelect.setString(2, PretupsI.STATUS_TYPE);
            rs = pstmtSelect.executeQuery();
            int index = 0;
            ServiceClassVO serviceClassVO = null;
            while (rs.next()) {
                serviceClassVO = new ServiceClassVO();
                serviceClassVO.setServiceClassId(rs.getString("service_class_id"));
                serviceClassVO.setServiceClassCode(rs.getString("service_class_code"));
                serviceClassVO.setServiceClassName(rs.getString("service_class_name"));
                serviceClassVO.setInterfaceCode(rs.getString("interface_id"));
                serviceClassVO.setP2pSenderSuspend(rs.getString("p2p_sender_suspend"));
                serviceClassVO.setP2pReceiverSuspend(rs.getString("p2p_receiver_suspend"));
                serviceClassVO.setC2sReceiverSuspend(rs.getString("c2s_receiver_suspend"));
                serviceClassVO.setP2pSenderAllowedStatus(rs.getString("p2p_sender_allowed_status"));
                serviceClassVO.setP2pSenderDeniedStatus(rs.getString("p2p_sender_denied_status"));
                serviceClassVO.setP2pReceiverAllowedStatus(rs.getString("p2p_receiver_allowed_status"));
                serviceClassVO.setP2pReceiverDeniedStatus(rs.getString("p2p_receiver_denied_status"));
                serviceClassVO.setC2sReceiverAllowedStatus(rs.getString("c2s_receiver_allowed_status"));
                serviceClassVO.setC2sReceiverDeniedStatus(rs.getString("c2s_receiver_denied_status"));

                serviceClassVO.setModifiedOn(rs.getDate("modified_on"));
                serviceClassVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                serviceClassVO.setStatus(rs.getString("status"));
                serviceClassVO.setStatusName(rs.getString("lookup_name"));
                serviceClassVO.setRadioIndex(index);
                serviceClassList.add(serviceClassVO);
                index++;
            }
        } catch (SQLException sqle) {
            _log.error("loadServiceClassDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceList", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadServiceClassDetails", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassDetails", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassDetails", "Exiting Size() " + serviceClassList.size());
        }
        return serviceClassList;
    }

    /**
     * Method addServiceClass.
     * This method is used to add the service class details in service_classes
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_serviceClassVO
     *            ServiceClassVO
     * @return addCount int
     * @throws BTSLBaseException
     */

    public int addServiceClass(Connection p_con, ServiceClassVO p_serviceClassVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addServiceClass", "Entering VO " + p_serviceClassVO);
        int addCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO service_classes (service_class_id,service_class_code");
        insertQueryBuff.append(",service_class_name,interface_id,created_on,created_by,modified_on,modified_by,");
        insertQueryBuff.append("status,p2p_sender_suspend, p2p_receiver_suspend, c2s_receiver_suspend, ");
        insertQueryBuff.append("p2p_sender_allowed_status, p2p_sender_denied_status, p2p_receiver_allowed_status, ");
        insertQueryBuff.append("p2p_receiver_denied_status, c2s_receiver_allowed_status, c2s_receiver_denied_status )");
        insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("addServiceClass", "Insert Query= " + insertQuery);
        try {
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            int i = 1;
            pstmtInsert.setString(i++, p_serviceClassVO.getServiceClassId());
            pstmtInsert.setString(i++, p_serviceClassVO.getServiceClassCode());
            // commented for DB2
            // pstmtInsert.setFormOfUse(i,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(i++, p_serviceClassVO.getServiceClassName());
            pstmtInsert.setString(i++, p_serviceClassVO.getInterfaceCode());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceClassVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_serviceClassVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceClassVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_serviceClassVO.getModifiedBy());
            pstmtInsert.setString(i++, p_serviceClassVO.getStatus());

            pstmtInsert.setString(i++, p_serviceClassVO.getP2pSenderSuspend());
            pstmtInsert.setString(i++, p_serviceClassVO.getP2pReceiverSuspend());
            pstmtInsert.setString(i++, p_serviceClassVO.getC2sReceiverSuspend());
            pstmtInsert.setString(i++, p_serviceClassVO.getP2pSenderAllowedStatus());
            pstmtInsert.setString(i++, p_serviceClassVO.getP2pSenderDeniedStatus());
            pstmtInsert.setString(i++, p_serviceClassVO.getP2pReceiverAllowedStatus());
            pstmtInsert.setString(i++, p_serviceClassVO.getP2pReceiverDeniedStatus());
            pstmtInsert.setString(i++, p_serviceClassVO.getC2sReceiverAllowedStatus());
            pstmtInsert.setString(i++, p_serviceClassVO.getC2sReceiverDeniedStatus());

            addCount = pstmtInsert.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("addServiceClass", "Query Executed= " + insertQuery);
        }

        catch (SQLException sqle) {
            _log.error("addServiceClass", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[addServiceClass]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addServiceClass", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("addServiceClass", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[addServiceClass]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addServiceClass", "error.general.processing");
        }

        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addServiceClass()", " Exiting addCount " + addCount);
        }

        return addCount;
    }

    /**
     * Method modifyServiceClass.
     * This method is used to Modify the service class detais in the
     * service_classes table
     * 
     * @param p_con
     *            Connection
     * @param p_serviceClassVO
     *            ServiceClassVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int modifyServiceClass(Connection p_con, ServiceClassVO p_serviceClassVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("modifyServiceClass", "Entering VO " + p_serviceClassVO);

        int updateCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        boolean modified = this.recordModified(p_con, p_serviceClassVO.getServiceClassId(), p_serviceClassVO.getLastModified());
        try {
            StringBuffer updateQueryBuff = new StringBuffer("UPDATE service_classes SET service_class_code=?,service_class_name=?,");
            updateQueryBuff.append("modified_on=?,modified_by=?,status=?,p2p_sender_suspend=?, ");
            updateQueryBuff.append("p2p_receiver_suspend=? , c2s_receiver_suspend=? , p2p_sender_allowed_status=? ");
            updateQueryBuff.append(",p2p_sender_denied_status=? , p2p_receiver_allowed_status=? , ");
            updateQueryBuff.append("p2p_receiver_denied_status=? ,c2s_receiver_allowed_status=? ,c2s_receiver_denied_status=? ");
            updateQueryBuff.append(" WHERE interface_id=? AND service_class_id=?");
            String updateQuery = updateQueryBuff.toString();
            // commented for DB2 pstmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(updateQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            int i = 1;
            pstmtUpdate.setString(i++, p_serviceClassVO.getServiceClassCode());
            // commented for DB2
            // pstmtUpdate.setFormOfUse(i,OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_serviceClassVO.getServiceClassName());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceClassVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_serviceClassVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_serviceClassVO.getStatus());

            pstmtUpdate.setString(i++, p_serviceClassVO.getP2pSenderSuspend());
            pstmtUpdate.setString(i++, p_serviceClassVO.getP2pReceiverSuspend());
            pstmtUpdate.setString(i++, p_serviceClassVO.getC2sReceiverSuspend());
            pstmtUpdate.setString(i++, p_serviceClassVO.getP2pSenderAllowedStatus());
            pstmtUpdate.setString(i++, p_serviceClassVO.getP2pSenderDeniedStatus());
            pstmtUpdate.setString(i++, p_serviceClassVO.getP2pReceiverAllowedStatus());
            pstmtUpdate.setString(i++, p_serviceClassVO.getP2pReceiverDeniedStatus());
            pstmtUpdate.setString(i++, p_serviceClassVO.getC2sReceiverAllowedStatus());
            pstmtUpdate.setString(i++, p_serviceClassVO.getC2sReceiverDeniedStatus());

            pstmtUpdate.setString(i++, p_serviceClassVO.getInterfaceCode());
            pstmtUpdate.setString(i++, p_serviceClassVO.getServiceClassId());
            if (modified)
                throw new BTSLBaseException(this, "modifyServiceClass", "error.modify.true");
            updateCount = pstmtUpdate.executeUpdate();

        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error("modifyServiceClass", " SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[modifyServiceClass]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "modifyServiceClass", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("modifyServiceClass", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[modifyServiceClass]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "modifyServiceClass", "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("modifyServiceClass", "Exiting updateCount " + updateCount);
        }

        return updateCount;
    }

    /**
     * Method deleteServiceClass.
     * This method is used to soft delete the service class detais in the
     * service_classes table
     * 
     * @param p_con
     *            Connection
     * @param p_serviceClassVO
     *            ServiceClassVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int deleteServiceClass(Connection p_con, ServiceClassVO p_serviceClassVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("deleteServiceClass", "Entering VO " + p_serviceClassVO);

        int deleteCount = -1;
        PreparedStatement pstmtDelete = null;

        boolean modified = this.recordModified(p_con, p_serviceClassVO.getServiceClassId(), p_serviceClassVO.getLastModified());
        try {
            if (modified)
                throw new BTSLBaseException(this, "deleteServiceClass", "error.modify.true");

            StringBuffer updateQueryBuff = new StringBuffer("UPDATE service_classes SET");
            updateQueryBuff.append(" status=?,modified_on=?,modified_by=?");
            updateQueryBuff.append(" WHERE service_class_id=?");
            String updateQuery = updateQueryBuff.toString();
            pstmtDelete = p_con.prepareStatement(updateQuery);
            pstmtDelete.setString(1, PretupsI.SERVICE_CLASS_STATUS_DELETE);
            pstmtDelete.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_serviceClassVO.getModifiedOn()));
            pstmtDelete.setString(3, p_serviceClassVO.getModifiedBy());
            pstmtDelete.setString(4, p_serviceClassVO.getServiceClassId());
            deleteCount = pstmtDelete.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("deleteServiceClass", "Query Executed= " + updateQuery);
        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error("deleteServiceClass", " SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[deleteServiceClass]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "modifyServiceClass", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("deleteServiceClass", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[deleteServiceClass]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "modifyServiceClass", "error.general.processing");
        }

        finally {
            try {
                if (pstmtDelete != null)
                    pstmtDelete.close();
            } catch (Exception e) {
            	 _log.error("deleteServiceClass", " Exception " + e.getMessage());
            	 
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteServiceClass", "Exiting deleteCount " + deleteCount);
        }

        return deleteCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @return boolean
     * @param con
     *            Connection
     * @param String
     * @param oldlastModified
     *            Long
     * @exception BTSLBaseException
     */

    public boolean recordModified(Connection p_con, String p_serviceClassId, long p_oldLastModified) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("recordModified", "Entered: p_serviceClassId= " + p_serviceClassId + "p_oldLastModified= " + p_oldLastModified);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM service_classes WHERE service_class_id=?";
        Timestamp newLastModified = null;

        if ((p_oldLastModified) == 0)
            return false;
        try {
            _log.info("recordModified()", "QUERY: sqlselect= " + sqlRecordModified);
            pstmt = p_con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_serviceClassId);
            rs = pstmt.executeQuery();
            if (rs.next())
                newLastModified = rs.getTimestamp("modified_on");

            if (_log.isDebugEnabled()) {
                _log.debug("recordModified()", " old=" + p_oldLastModified);
                _log.debug("recordModified()", " new=" + newLastModified.getTime());
            }

            if (newLastModified.getTime() != p_oldLastModified)
                modified = true;

        }

        catch (SQLException sqle) {
            _log.error("recordModified()", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified()", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("recordModified()", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "recordModified()", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("recordModified()", "Exception " + ex.getMessage());
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.error("recordModified()", "Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified()", "Exititng modified=" + modified);
            }
        }
        return modified;
    }

    /**
     * Method isExistsServiceNameForAdd
     * This method is used before adding the record in the service_classes table
     * it will check for the uniqueness of the constraint service_class_name
     * according to interface id and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_serviceVO
     *            ServiceClassVO
     * @param p_method
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsServiceNameForAdd(Connection p_con, ServiceClassVO p_serviceVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceNameForAdd", "Entered VO::" + p_serviceVO);

        boolean found = false;
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes ss,interface_types it,interfaces i ");
        sqlBuff.append("WHERE UPPER(service_class_name)=UPPER(?) AND it.interface_category=? ");
        sqlBuff.append("AND it.interface_type_id=i.interface_type_id AND ss.interface_id=i.interface_id AND ss.status<>'N'");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceNameForAdd", "Select Query::" + selectQuery);

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for
            // DB2pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_serviceVO.getServiceClassName());
            pstmtSelect.setString(2, p_serviceVO.getInterfaceCategory());
            rs = pstmtSelect.executeQuery();

            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error("isExistsServiceNameForAdd", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceNameForAdd]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsServiceNameForAdd", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isExistsServiceNameForAdd", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceNameForAdd]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExistsServiceNameForAdd", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            	_log.error("isExistsServiceNameForAdd", "Exception " + ex.getMessage());
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            	_log.error("isExistsServiceNameForAdd", "Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("isExistsServiceNameForAdd", "Exiting isExists found=" + found);
        }
        return found;
    }

    /**
     * Method isExistsServiceNameForModify
     * This method is used before modifying the record in the service_classes
     * table
     * it will check for the uniqueness of the constraint service_class_name
     * according to interface id and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_serviceVO
     *            ServiceClassVO
     * @param p_method
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsServiceNameForModify(Connection p_con, ServiceClassVO p_serviceVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceNameForModify", "Entered VO::" + p_serviceVO);

        boolean found = false;
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes ss,interface_types it,interfaces i ");
        sqlBuff.append("WHERE UPPER(service_class_name)=UPPER(?) AND it.interface_category=? ");
        sqlBuff.append("AND it.interface_type_id=i.interface_type_id AND ss.interface_id=i.interface_id AND ss.service_class_id!=? AND ss.status<>'N'");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceNameForModify", "Select Query::" + selectQuery);

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for DB2
            // pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_serviceVO.getServiceClassName());
            pstmtSelect.setString(2, p_serviceVO.getInterfaceCategory());
            pstmtSelect.setString(3, p_serviceVO.getServiceClassId());
            rs = pstmtSelect.executeQuery();

            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error("isExistsServiceNameForModify", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceNameForModify]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsServiceNameForModify", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isExistsServiceNameForModify", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceNameForModify]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExistsServiceNameForModify", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            	  _log.error("isExistsServiceNameForModify", "SQLException " + ex.getMessage()); }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            	  _log.error("isExistsServiceNameForModify", "SQLException " + e.getMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("isExistsServiceNameForModify", "Exiting isExists found=" + found);
            }
        }
        return found;
    }

    /**
     * Method loadServiceClassList.
     * This method is to load all the service classes.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_interfaceCategory
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceClassList(Connection p_con, String p_interfaceCategory) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassList", "Entered p_interfaceCategory=" + p_interfaceCategory);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        ArrayList serviceClassVOList = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT service_class_code,service_class_name,interface_category,service_class_id ");
            selectQuery.append("FROM service_classes S,interfaces I,interface_types IT ");
            selectQuery.append("WHERE S.interface_id = I.interface_id AND ");
            selectQuery.append("I.interface_type_id = IT.interface_type_id AND interface_category IN(" + p_interfaceCategory + ") AND S.status <> ? ");
            selectQuery.append("ORDER BY interface_category ");
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassList", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, PretupsI.SERVICE_CLASS_STATUS_DELETE);

            rs = pstmtSelect.executeQuery();
            serviceClassVOList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("service_class_name") + "(" + rs.getString("service_class_code") + ")", rs.getString("interface_category") + ":" + rs.getString("service_class_id"));
                // listValueVO.setStatusType(rs.getString("statustype"));
                serviceClassVOList.add(listValueVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadServiceClassList", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceClassList", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassList", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            	 _log.error("loadServiceClassList", "Exception:" + ex.getMessage());
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            	 _log.error("loadServiceClassList", "Exception:" + ex.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassList", "Exiting:serviceClassVOList.size()=" + serviceClassVOList.size());
        }
        return serviceClassVOList;
    }

    /**
     * Method isTransferRulesExistsForServiceClass.
     * This method is used to check in the transfer rules table the service
     * class id
     * corresponding to network if it founds then this method return true else
     * false
     * 
     * @param p_con
     *            Connection
     * @param p_serviceClassVO
     *            ServiceClassVO
     * @return found boolean
     * @throws BTSLBaseException
     */

    public boolean isTransferRulesExistsForServiceClass(Connection p_con, ServiceClassVO p_serviceClassVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isTransferRulesExistsForServiceClass", "Entering VO " + p_serviceClassVO);

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT 1 FROM transfer_rules WHERE ");
        selectQueryBuff.append("sender_service_class_id=? OR receiver_service_class_id=? ");
        selectQueryBuff.append("AND network_code=? AND status<>'N'");
        String insertQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("isTransferRulesExistsForServiceClass", " Query= " + insertQuery);

        try {
            pstmtSelect = p_con.prepareStatement(insertQuery);
            pstmtSelect.setString(1, p_serviceClassVO.getServiceClassId());
            pstmtSelect.setString(2, p_serviceClassVO.getServiceClassId());
            pstmtSelect.setString(3, p_serviceClassVO.getNetworkCode());

            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error("isTransferRulesExistsForServiceClass", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isTransferRulesExistsForServiceClass]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isTransferRulesExistsForServiceClass", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isTransferRulesExistsForServiceClass", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isTransferRulesExistsForServiceClass]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isTransferRulesExistsForServiceClass", "error.general.processing");
        }

        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            	 _log.error("isTransferRulesExistsForServiceClass", " Exception " + ex.getMessage());
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            	 _log.error("isTransferRulesExistsForServiceClass", " Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("isTransferRulesExistsForServiceClass", " Exiting found= " + found);
        }

        return found;
    }

    /**
     * Method isExistsServiceCodeForAdd
     * This method is used before adding the record in the service_classes table
     * it will check for the uniqueness of the constraint service_class_code
     * according to interface id and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_serviceVO
     *            ServiceClassVO
     * @param p_method
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsServiceCodeForAdd(Connection p_con, ServiceClassVO p_serviceVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceCodeForAdd", "Entered VO::" + p_serviceVO);

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes WHERE interface_id=?");
        sqlBuff.append(" AND service_class_code=? AND status<>'N'");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceCodeForAdd", "Select Query::" + selectQuery);
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceVO.getInterfaceCode());
            pstmtSelect.setString(2, p_serviceVO.getServiceClassCode());
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        } catch (SQLException sqle) {
            _log.error("isExistsServiceCodeForAdd", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceCodeForAdd]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsServiceCodeForAdd", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isExistsServiceCodeForAdd", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceCodeForAdd]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExistsServiceCodeForAdd", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("isExistsServiceCodeForAdd", "Exiting isExists found=" + found);
            }
        }
        return found;
    }

    /**
     * Method isExistsServiceCodeForModify
     * This method is used before modifying the record in the service_classes
     * table
     * it will check for the uniqueness of the constraint service_class_code
     * according to interface id and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param p_serviceVO
     *            ServiceClassVO
     * @param p_method
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isExistsServiceCodeForModify(Connection p_con, ServiceClassVO p_serviceVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceCodeForModify", "Entered VO::" + p_serviceVO);

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes WHERE interface_id=?");
        sqlBuff.append(" AND service_class_code=? AND service_class_id !=? AND status<>'N'");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceCodeForModify", "Select Query::" + selectQuery);

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceVO.getInterfaceCode());
            pstmtSelect.setString(2, p_serviceVO.getServiceClassCode());
            pstmtSelect.setString(3, p_serviceVO.getServiceClassId());
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        }

        catch (SQLException sqle) {
            _log.error("isExistsServiceCodeForModify", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceCodeForModify]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsServiceCodeForModify", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isExistsServiceCodeForModify", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceCodeForModify]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExistsServiceCodeForModify", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            	  _log.error("isExistsServiceCodeForModify", "Exception " + ex.getMessage());
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            	_log.error("isExistsServiceCodeForModify", "Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("isExistsServiceCodeForModify", "Exiting isExists found=" + found);
            }
        }
        return found;
    }

    /**
     * Method to load the service class ID based on the Code returned from the
     * Interface
     * 
     * @param p_con
     * @param p_serviceClass
     * @param p_interfaceID
     * @return
     * @throws BTSLBaseException
     */
    public ServiceClassVO loadServiceClassInfoByCode(Connection p_con, String p_serviceClass, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassInfoByCode", "Entered p_serviceClass:" + p_serviceClass + " p_interfaceID:" + p_interfaceID);
        PreparedStatement pstmtSelect = null;
        ServiceClassVO serviceClassVO = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT service_class_id,");
            selectQueryBuff.append(" service_class_name,status,p2p_sender_suspend, p2p_receiver_suspend, ");
            selectQueryBuff.append(" c2s_receiver_suspend, p2p_sender_allowed_status, p2p_sender_denied_status, ");
            selectQueryBuff.append(" p2p_receiver_allowed_status, p2p_receiver_denied_status, ");
            selectQueryBuff.append(" c2s_receiver_allowed_status, c2s_receiver_denied_status");
            selectQueryBuff.append(" FROM service_classes");
            selectQueryBuff.append(" WHERE service_class_code=? AND interface_id=? AND status<> ? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassInfoByCode", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceClass);
            pstmtSelect.setString(2, p_interfaceID);
            pstmtSelect.setString(3, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                serviceClassVO = new ServiceClassVO();
                serviceClassVO.setServiceClassId(rs.getString("service_class_id"));
                serviceClassVO.setServiceClassCode(p_serviceClass);
                serviceClassVO.setServiceClassName(rs.getString("service_class_name"));
                serviceClassVO.setStatus(rs.getString("status"));

                serviceClassVO.setP2pSenderSuspend(rs.getString("p2p_sender_suspend"));
                serviceClassVO.setP2pReceiverSuspend(rs.getString("p2p_receiver_suspend"));
                serviceClassVO.setC2sReceiverSuspend(rs.getString("c2s_receiver_suspend"));
                serviceClassVO.setP2pSenderAllowedStatus(rs.getString("p2p_sender_allowed_status"));
                serviceClassVO.setP2pSenderDeniedStatus(rs.getString("p2p_sender_denied_status"));
                serviceClassVO.setP2pReceiverAllowedStatus(rs.getString("p2p_receiver_allowed_status"));
                serviceClassVO.setP2pReceiverDeniedStatus(rs.getString("p2p_receiver_denied_status"));
                serviceClassVO.setC2sReceiverAllowedStatus(rs.getString("c2s_receiver_allowed_status"));
                serviceClassVO.setC2sReceiverDeniedStatus(rs.getString("c2s_receiver_denied_status"));
            }
            return serviceClassVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadServiceClassInfoByCode", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCode]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassInfoByCode", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadServiceClassInfoByCode", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassInfoByCode", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            	   _log.error("loadServiceClassInfoByCode", "Exception " + e.getMessage());
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            	   _log.error("loadServiceClassInfoByCode", "Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassInfoByCode", "Exiting serviceClassVO:" + serviceClassVO);
        }// end of finally
    }

    /**
     * Date : Mar 7, 2007
     * Discription :load the service class list.
     * Method : loadServiceClassList
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @return ArrayList
     * @author ved.sharma
     */
    public ArrayList loadServiceClassList(Connection p_con) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassList", "Entering ");

        ArrayList serviceClassList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT sc.service_class_id,sc.service_class_code, ");
        selectQueryBuff.append("lk.lookup_name,sc.service_class_name,sc.interface_id,sc.created_on,sc.created_by,sc.status,");
        selectQueryBuff.append("sc.modified_on,sc.modified_by,sc.p2p_sender_suspend, sc.p2p_receiver_suspend, ");
        selectQueryBuff.append("sc.c2s_receiver_suspend, sc.p2p_sender_allowed_status, sc.p2p_sender_denied_status, ");
        selectQueryBuff.append("sc.p2p_receiver_allowed_status, sc.p2p_receiver_denied_status, ");
        selectQueryBuff.append("sc.c2s_receiver_allowed_status, sc.c2s_receiver_denied_status, I.external_id ");
        selectQueryBuff.append("FROM service_classes sc,lookups lk, interfaces I WHERE  I.interface_id=SC.interface_id ");
        selectQueryBuff.append("AND sc.status<>'N' AND lk.lookup_type=? AND lk.lookup_code=sc.status ");
        selectQueryBuff.append("ORDER BY service_class_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceList", "Select Query " + selectQuery);
        try {
            serviceClassList = new ArrayList();
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.STATUS_TYPE);
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("service_class_name") + "(" + rs.getString("service_class_code") + ")", rs.getString("interface_id") + ":" + rs.getString("service_class_code"));
                listValueVO.setOtherInfo(rs.getString("external_id"));
                serviceClassList.add(listValueVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadServiceClassList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceList", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadServiceClassList", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassList", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            	  _log.error("loadServiceClassList", " Exception " + ex.getMessage());
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            	  _log.error("loadServiceClassList", " Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassList", "Exiting Size() " + serviceClassList.size());
        }
        return serviceClassList;
    }

    /**
     * Method to load the interface details for service class ID
     * 
     * @param p_con
     * @param p_serviceClass
     * @return
     * @throws BTSLBaseException
     */
    public InterfaceVO loadInterfaceDetailsForServiceClassID(Connection p_con, String p_serviceClass) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceDetailsForServiceClassID", "Entered p_serviceClass:" + p_serviceClass);
        PreparedStatement pstmtSelect = null;
        InterfaceVO interafceVO = new InterfaceVO();
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT i.interface_type_id FROM interfaces i, service_classes sc");
            selectQueryBuff.append(" WHERE sc.service_class_id=? AND sc.interface_id=i.interface_id ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceDetailsForServiceClassID", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceClass);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                interafceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
            }
            return interafceVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadInterfaceDetailsForServiceClassID", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceDetailsForServiceClassID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceDetailsForServiceClassID", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadInterfaceDetailsForServiceClassID", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceDetailsForServiceClassID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceDetailsForServiceClassID", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            	_log.error("loadInterfaceDetailsForServiceClassID", "Exception " + e.getMessage());
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            	_log.error("loadInterfaceDetailsForServiceClassID", "Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceDetailsForServiceClassID", "Exiting interafceVO:" + interafceVO);
        }// end of finally
    }

    /**
     * Method loadServiceClassNames.
     * This method is to load all the service classes.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceStr
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author vipul
     */

    public ArrayList loadServiceClassNames(Connection p_con, String p_serviceStr) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassNames", "Entered p_serviceStr:" + p_serviceStr);
        ArrayList serviceClassList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer selectQueryBuff = new StringBuffer("SELECT service_class_id,service_class_code,service_class_name ");
        selectQueryBuff.append("FROM service_classes where service_class_id in (" + p_serviceStr + ")");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceList", "Select Query " + selectQuery);
        try {
            serviceClassList = new ArrayList();
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();

            ServiceClassVO serviceClassVO = null;
            while (rs.next()) {
                serviceClassVO = new ServiceClassVO();
                serviceClassVO.setServiceClassId(rs.getString("service_class_id"));
                serviceClassVO.setServiceClassCode(rs.getString("service_class_code"));
                serviceClassVO.setServiceClassName(rs.getString("service_class_name"));
                serviceClassList.add(serviceClassVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadServiceClassNames", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassNames]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassNames", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("loadServiceClassNames", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassNames]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassNames", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            	  _log.error("loadServiceClassNames", " Exception " + ex.getMessage());
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            	  _log.error("loadServiceClassNames", " Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassNames", "Exiting Size() " + serviceClassList.size());
        }
        return serviceClassList;
    }

    /**
     * Method to load the service class ID based on the Code returned from the
     * Interface along with ALL as code
     * 
     * @param p_con
     * @param p_serviceClass
     * @param p_interfaceID
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap<String, ServiceClassVO> loadServiceClassInfoByCodeWithAll(Connection p_con, String p_serviceClass, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassInfoByCodeWithAll", "Entered p_serviceClass:" + p_serviceClass + " p_interfaceID:" + p_interfaceID);
        PreparedStatement pstmtSelect = null;
        ServiceClassVO serviceClassVO = null;
        HashMap<String, ServiceClassVO> serviceMap = new HashMap<String, ServiceClassVO>();
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT service_class_id,service_class_code,");
            selectQueryBuff.append(" service_class_name,status,p2p_sender_suspend, p2p_receiver_suspend, ");
            selectQueryBuff.append(" c2s_receiver_suspend, p2p_sender_allowed_status, p2p_sender_denied_status, ");
            selectQueryBuff.append(" p2p_receiver_allowed_status, p2p_receiver_denied_status, ");
            selectQueryBuff.append(" c2s_receiver_allowed_status, c2s_receiver_denied_status");
            selectQueryBuff.append(" FROM service_classes");
            selectQueryBuff.append(" WHERE (service_class_code=? OR service_class_code=? ) AND interface_id=? AND status<> ? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassInfoByCodeWithAll", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i, p_serviceClass);
            pstmtSelect.setString(++i, PretupsI.ALL);
            pstmtSelect.setString(++i, p_interfaceID);
            pstmtSelect.setString(++i, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceClassVO = new ServiceClassVO();
                serviceClassVO.setServiceClassId(rs.getString("service_class_id"));
                serviceClassVO.setServiceClassCode(rs.getString("service_class_code"));
                serviceClassVO.setServiceClassName(rs.getString("service_class_name"));
                serviceClassVO.setStatus(rs.getString("status"));

                serviceClassVO.setP2pSenderSuspend(rs.getString("p2p_sender_suspend"));
                serviceClassVO.setP2pReceiverSuspend(rs.getString("p2p_receiver_suspend"));
                serviceClassVO.setC2sReceiverSuspend(rs.getString("c2s_receiver_suspend"));
                serviceClassVO.setP2pSenderAllowedStatus(rs.getString("p2p_sender_allowed_status"));
                serviceClassVO.setP2pSenderDeniedStatus(rs.getString("p2p_sender_denied_status"));
                serviceClassVO.setP2pReceiverAllowedStatus(rs.getString("p2p_receiver_allowed_status"));
                serviceClassVO.setP2pReceiverDeniedStatus(rs.getString("p2p_receiver_denied_status"));
                serviceClassVO.setC2sReceiverAllowedStatus(rs.getString("c2s_receiver_allowed_status"));
                serviceClassVO.setC2sReceiverDeniedStatus(rs.getString("c2s_receiver_denied_status"));
                serviceMap.put(serviceClassVO.getServiceClassCode(), serviceClassVO);
            }
            return serviceMap;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadServiceClassInfoByCodeWithAll", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCodeWithAll]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassInfoByCodeWithAll", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadServiceClassInfoByCodeWithAll", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCodeWithAll]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassInfoByCodeWithAll", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            	_log.error("loadServiceClassInfoByCodeWithAll", "Exception " + e.getMessage());
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            	_log.error("loadServiceClassInfoByCodeWithAll", "Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassInfoByCodeWithAll", "Exiting serviceClassVO:" + serviceClassVO);
        }// end of finally
    }

    /**
     * Method to load the service class ID based on the Code returned from the
     * Interface along with ALL as code
     * 
     * @return HashMap<String, ServiceClassVO>
     * @throws BTSLBaseException
     */
    public HashMap<String, ServiceClassVO> loadServiceClassInfoByCodeWithAll() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassInfoByCodeWithAll", "Entered ");
        PreparedStatement pstmtSelect = null;
        Connection con = null;
        ServiceClassVO serviceClassVO = null;
        HashMap<String, ServiceClassVO> serviceMap = new HashMap<String, ServiceClassVO>();
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT service_class_id, service_class_code,");
            selectQueryBuff.append(" service_class_name, interface_id, status, p2p_sender_suspend, p2p_receiver_suspend, ");
            selectQueryBuff.append(" c2s_receiver_suspend, p2p_sender_allowed_status, p2p_sender_denied_status, ");
            selectQueryBuff.append(" p2p_receiver_allowed_status, p2p_receiver_denied_status, ");
            selectQueryBuff.append(" c2s_receiver_allowed_status, c2s_receiver_denied_status");
            selectQueryBuff.append(" FROM service_classes");
            selectQueryBuff.append(" WHERE status<> ? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassInfoByCodeWithAll", "select query:" + selectQuery);
            con = OracleUtil.getSingleConnection();
            pstmtSelect = con.prepareStatement(selectQuery);
            int i = 1;
            // pstmtSelect.setString(i, p_serviceClass);
            // pstmtSelect.setString(++i, PretupsI.ALL);
            // pstmtSelect.setString(++i, p_interfaceID);
            pstmtSelect.setString(i, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceClassVO = new ServiceClassVO();
                serviceClassVO.setServiceClassId(rs.getString("service_class_id"));
                serviceClassVO.setServiceClassCode(rs.getString("service_class_code"));
                serviceClassVO.setServiceClassName(rs.getString("service_class_name"));
                serviceClassVO.setInterfaceCode(rs.getString("interface_id"));
                serviceClassVO.setStatus(rs.getString("status"));

                serviceClassVO.setP2pSenderSuspend(rs.getString("p2p_sender_suspend"));
                serviceClassVO.setP2pReceiverSuspend(rs.getString("p2p_receiver_suspend"));
                serviceClassVO.setC2sReceiverSuspend(rs.getString("c2s_receiver_suspend"));
                serviceClassVO.setP2pSenderAllowedStatus(rs.getString("p2p_sender_allowed_status"));
                serviceClassVO.setP2pSenderDeniedStatus(rs.getString("p2p_sender_denied_status"));
                serviceClassVO.setP2pReceiverAllowedStatus(rs.getString("p2p_receiver_allowed_status"));
                serviceClassVO.setP2pReceiverDeniedStatus(rs.getString("p2p_receiver_denied_status"));
                serviceClassVO.setC2sReceiverAllowedStatus(rs.getString("c2s_receiver_allowed_status"));
                serviceClassVO.setC2sReceiverDeniedStatus(rs.getString("c2s_receiver_denied_status"));
                serviceMap.put(serviceClassVO.getServiceClassCode() + "_" + serviceClassVO.getInterfaceCode(), serviceClassVO);
            }
            return serviceMap;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadServiceClassInfoByCodeWithAll", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCodeWithAll]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassInfoByCodeWithAll", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadServiceClassInfoByCodeWithAll", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCodeWithAll]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassInfoByCodeWithAll", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            	  _log.error("loadServiceClassInfoByCodeWithAll", "Exception " + e.getMessage());
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            	  _log.error("loadServiceClassInfoByCodeWithAll", "Exception " + e.getMessage());
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassInfoByCodeWithAll", "Exiting serviceClassVO:" + serviceClassVO);
        }// end of finally
    }

    /**
     * Method isServiceCodeExists
     * This method is used before adding the record in the service_classes table
     * it will check for the uniqueness of the constraint service_class_code
     * according to interface id and returns true if record exists in table
     * 
     * @return boolean
     * @param p_con
     *            Connection
     * @param String
     *            p_serviceClassCode
     * @param p_method
     *            String
     * @exception BTSLBaseException
     * @return boolean
     */

    public boolean isServiceCodeExists(Connection p_con, String p_serviceClassCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isServiceCodeExists", "Entered service class code::" + p_serviceClassCode);

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes WHERE ");
        sqlBuff.append(" service_class_code=? AND status<>'N'");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isServiceCodeExists", "Select Query::" + selectQuery);
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceClassCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        } catch (SQLException sqle) {
            _log.error("isServiceCodeExists", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isServiceCodeExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isServiceCodeExists", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("isServiceCodeExists", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isServiceCodeExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isServiceCodeExists", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            	  _log.error("isServiceCodeExists", "Exception " + ex.getMessage());
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            	  _log.error("isServiceCodeExists", "Exception " + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("isServiceCodeExists", "Exiting isExists found=" + found);
        }
        return found;
    }

}
