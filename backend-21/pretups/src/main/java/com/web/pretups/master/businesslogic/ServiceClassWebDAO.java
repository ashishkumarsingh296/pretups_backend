package com.web.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.ServiceClassVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class ServiceClassWebDAO {

    private Log _log = LogFactory.getFactory().getInstance(ServiceClassWebDAO.class.getName());

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

        final String methodName = "loadInterfaceTypesList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering");
        }

        final ArrayList interfaceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer selectQueryBuff = new StringBuffer("SELECT interface_type_id,interface_name,interface_category FROM interface_types ORDER BY interface_name");
        final String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query " + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                interfaceList.add(new ListValueVO(rs.getString("interface_name"), rs.getString("interface_type_id") + ":" + rs.getString("interface_type_id")));
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed " + selectQuery);
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceTypesList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceTypesList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Size " + interfaceList.size());
            }
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

        final String methodName = "loadInterfaceList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering parms p_networkCode:" + p_networkCode);
        }

        final ArrayList interfaceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer selectQueryBuff = new StringBuffer("SELECT DISTINCT i.interface_id,i.interface_description,IT.interface_category,IT.interface_type_id ");
        selectQueryBuff.append(" FROM interfaces i,interface_types IT ");
        selectQueryBuff.append(" WHERE IT.interface_type_id=i.interface_type_id");
        selectQueryBuff.append(" AND IT.interface_type_id=i.interface_type_id AND i.status <> ? ");
        selectQueryBuff.append(" ORDER BY i.interface_description");

        final String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query " + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            // pstmtSelect.setString(1,p_networkCode);
            // pstmtSelect.setString(1,PretupsI.INTERFACE_CATEGORY_PREPAID);
            // pstmtSelect.setString(2,PretupsI.INTERFACE_CATEGORY_POSTPAID);
            pstmtSelect.setString(1, PretupsI.STATUS_DELETE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                interfaceList.add(new ListValueVO(rs.getString("interface_description"),
                    rs.getString("interface_type_id") + ":" + rs.getString("interface_type_id") + "|" + rs.getString("interface_id")));
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed " + selectQuery);
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Size " + interfaceList.size());
            }
        }
        return interfaceList;
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
        final String methodName = "addServiceClass";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering VO " + p_serviceClassVO);
        }
        int addCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        final StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO service_classes (service_class_id,service_class_code");
        insertQueryBuff.append(",service_class_name,interface_id,created_on,created_by,modified_on,modified_by,");
        insertQueryBuff.append("status,p2p_sender_suspend, p2p_receiver_suspend, c2s_receiver_suspend, ");
        insertQueryBuff.append("p2p_sender_allowed_status, p2p_sender_denied_status, p2p_receiver_allowed_status, ");
        insertQueryBuff.append("p2p_receiver_denied_status, c2s_receiver_allowed_status, c2s_receiver_denied_status )");
        insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        final String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Insert Query= " + insertQuery);
        }
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + insertQuery);
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[addServiceClass]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[addServiceClass]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addServiceClass()", " Exiting addCount " + addCount);
            }
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

        final String methodName = "modifyServiceClass";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering VO " + p_serviceClassVO);
        }

        int updateCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        final boolean modified = this.recordModified(p_con, p_serviceClassVO.getServiceClassId(), p_serviceClassVO.getLastModified());
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE service_classes SET service_class_code=?,service_class_name=?,");
            updateQueryBuff.append("modified_on=?,modified_by=?,status=?,p2p_sender_suspend=?, ");
            updateQueryBuff.append("p2p_receiver_suspend=? , c2s_receiver_suspend=? , p2p_sender_allowed_status=? ");
            updateQueryBuff.append(",p2p_sender_denied_status=? , p2p_receiver_allowed_status=? , ");
            updateQueryBuff.append("p2p_receiver_denied_status=? ,c2s_receiver_allowed_status=? ,c2s_receiver_denied_status=? ");
            updateQueryBuff.append(" WHERE interface_id=? AND service_class_id=?");
            final String updateQuery = updateQueryBuff.toString();
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
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();

        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[modifyServiceClass]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[modifyServiceClass]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount " + updateCount);
            }
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

        final String methodName = "deleteServiceClass";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering VO " + p_serviceClassVO);
        }

        int deleteCount = -1;
        PreparedStatement pstmtDelete = null;

        final boolean modified = this.recordModified(p_con, p_serviceClassVO.getServiceClassId(), p_serviceClassVO.getLastModified());
        try {
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }

            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE service_classes SET");
            updateQueryBuff.append(" status=?,modified_on=?,modified_by=?");
            updateQueryBuff.append(" WHERE service_class_id=?");
            final String updateQuery = updateQueryBuff.toString();
            pstmtDelete = p_con.prepareStatement(updateQuery);
            pstmtDelete.setString(1, PretupsI.SERVICE_CLASS_STATUS_DELETE);
            pstmtDelete.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_serviceClassVO.getModifiedOn()));
            pstmtDelete.setString(3, p_serviceClassVO.getModifiedBy());
            pstmtDelete.setString(4, p_serviceClassVO.getServiceClassId());
            deleteCount = pstmtDelete.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + updateQuery);
            }
        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            _log.error(methodName, " SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[deleteServiceClass]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "modifyServiceClass", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[deleteServiceClass]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "modifyServiceClass", "error.general.processing");
        }

        finally {
            try {
                if (pstmtDelete != null) {
                    pstmtDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting deleteCount " + deleteCount);
            }
        }

        return deleteCount;
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

        final String methodName = "isExistsServiceNameForAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered VO::" + p_serviceVO);
        }

        boolean found = false;
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes ss,interface_types it,interfaces i ");
        sqlBuff.append("WHERE UPPER(service_class_name)=UPPER(?) AND it.interface_category=? ");
        sqlBuff.append("AND it.interface_type_id=i.interface_type_id AND ss.interface_id=i.interface_id AND ss.status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            // commented for DB2
            // pstmtSelect=(OraclePreparedStatement)p_con.prepareStatement(selectQuery);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            // commented for
            // DB2pstmtSelect.setFormOfUse(1,OraclePreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(1, p_serviceVO.getServiceClassName());
            pstmtSelect.setString(2, p_serviceVO.getInterfaceCategory());
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceNameForAdd]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceNameForAdd]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }
        return found;
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
        final String sqlRecordModified = "SELECT modified_on FROM service_classes WHERE service_class_id=?";
        Timestamp newLastModified = null;

        if ((p_oldLastModified) == 0) {
            return false;
        }
        final String methodName = "recordModified()";
        try {
            _log.info(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            pstmt = p_con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_serviceClassId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " old=" + p_oldLastModified);
                _log.debug(methodName, " new=" + newLastModified.getTime());
            }

            if (newLastModified.getTime() != p_oldLastModified) {
                modified = true;
            }

        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[recordModified]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[recordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng modified=" + modified);
            }
        }
        return modified;
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

        final String methodName = "isExistsServiceNameForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered VO::" + p_serviceVO);
        }

        boolean found = false;
        // commented for DB2 OraclePreparedStatement pstmtSelect=null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes ss,interface_types it,interfaces i ");
        sqlBuff.append("WHERE UPPER(service_class_name)=UPPER(?) AND it.interface_category=? ");
        sqlBuff.append("AND it.interface_type_id=i.interface_type_id AND ss.interface_id=i.interface_id AND ss.service_class_id!=? AND ss.status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

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

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceNameForModify]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceNameForModify]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
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
        final String methodName = "loadServiceClassList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_interfaceCategory=" + p_interfaceCategory);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        ArrayList serviceClassVOList = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT service_class_code,service_class_name,interface_category,service_class_id ");
            selectQuery.append("FROM service_classes S,interfaces I,interface_types IT ");
            selectQuery.append("WHERE S.interface_id = I.interface_id AND ");
            selectQuery.append("I.interface_type_id = IT.interface_type_id AND interface_category IN(" + p_interfaceCategory + ") AND S.status <> ? ");
            selectQuery.append("ORDER BY interface_category ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, PretupsI.SERVICE_CLASS_STATUS_DELETE);

            rs = pstmtSelect.executeQuery();
            serviceClassVOList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("service_class_name")) + "(" +
                		       SqlParameterEncoder.encodeParams(rs.getString("service_class_code")) + ")",
                			   SqlParameterEncoder.encodeParams(rs.getString("interface_category")) + ":" + 
                			   SqlParameterEncoder.encodeParams(rs.getString("service_class_id")));
                // listValueVO.setStatusType(rs.getString("statustype"));
                serviceClassVOList.add(listValueVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:serviceClassVOList.size()=" + serviceClassVOList.size());
            }
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
        final String methodName = "isTransferRulesExistsForServiceClass";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering VO " + p_serviceClassVO);
        }

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer selectQueryBuff = new StringBuffer("SELECT 1 FROM transfer_rules WHERE ");
        selectQueryBuff.append("sender_service_class_id=? OR receiver_service_class_id=? ");
        selectQueryBuff.append("AND network_code=? AND status<>'N'");
        final String insertQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Query= " + insertQuery);
        }

        try {
            pstmtSelect = p_con.prepareStatement(insertQuery);
            pstmtSelect.setString(1, p_serviceClassVO.getServiceClassId());
            pstmtSelect.setString(2, p_serviceClassVO.getServiceClassId());
            pstmtSelect.setString(3, p_serviceClassVO.getNetworkCode());

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ServiceClassDAO[isTransferRulesExistsForServiceClass]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ServiceClassDAO[isTransferRulesExistsForServiceClass]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting found= " + found);
            }
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

        final String methodName = "isExistsServiceCodeForAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered VO::" + p_serviceVO);
        }

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes WHERE interface_id=?");
        sqlBuff.append(" AND service_class_code=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceVO.getInterfaceCode());
            pstmtSelect.setString(2, p_serviceVO.getServiceClassCode());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceCodeForAdd]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceCodeForAdd]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
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

        final String methodName = "isExistsServiceCodeForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered VO::" + p_serviceVO);
        }

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes WHERE interface_id=?");
        sqlBuff.append(" AND service_class_code=? AND service_class_id !=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceVO.getInterfaceCode());
            pstmtSelect.setString(2, p_serviceVO.getServiceClassCode());
            pstmtSelect.setString(3, p_serviceVO.getServiceClassId());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceCodeForModify]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isExistsServiceCodeForModify]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }
        return found;
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

        final String methodName = "loadServiceClassList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering ");
        }

        ArrayList serviceClassList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer selectQueryBuff = new StringBuffer("SELECT sc.service_class_id,sc.service_class_code, ");
        selectQueryBuff.append("lk.lookup_name,sc.service_class_name,sc.interface_id,sc.created_on,sc.created_by,sc.status,");
        selectQueryBuff.append("sc.modified_on,sc.modified_by,sc.p2p_sender_suspend, sc.p2p_receiver_suspend, ");
        selectQueryBuff.append("sc.c2s_receiver_suspend, sc.p2p_sender_allowed_status, sc.p2p_sender_denied_status, ");
        selectQueryBuff.append("sc.p2p_receiver_allowed_status, sc.p2p_receiver_denied_status, ");
        selectQueryBuff.append("sc.c2s_receiver_allowed_status, sc.c2s_receiver_denied_status, I.external_id ");
        selectQueryBuff.append("FROM service_classes sc,lookups lk, interfaces I WHERE  I.interface_id=SC.interface_id ");
        selectQueryBuff.append("AND sc.status<>'N' AND lk.lookup_type=? AND lk.lookup_code=sc.status ");
        selectQueryBuff.append("ORDER BY service_class_name");
        final String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadInterfaceList", "Select Query " + selectQuery);
        }
        try {
            serviceClassList = new ArrayList();
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.STATUS_TYPE);
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("service_class_name") + "(" + rs.getString("service_class_code") + ")", rs.getString("interface_id") + ":" + rs
                    .getString("service_class_code"));
                listValueVO.setOtherInfo(rs.getString("external_id"));
                serviceClassList.add(listValueVO);
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceList", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Size() " + serviceClassList.size());
            }
        }
        return serviceClassList;
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
        final String methodName = "loadServiceClassNames";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_serviceStr:" + p_serviceStr);
        }
        ArrayList serviceClassList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final String serviceStr = p_serviceStr.replaceAll("'", "");
        final String ss = serviceStr.replaceAll("\" ", "");
        final String m_serviceStr[] = ss.split(",");
        final StringBuffer selectQueryBuff = new StringBuffer("SELECT service_class_id,service_class_code,service_class_name ");
        selectQueryBuff.append("FROM service_classes where service_class_id in (");
        for (int i = 0; i < m_serviceStr.length; i++) {
            selectQueryBuff.append(" ?");
            if (i != m_serviceStr.length - 1) {
                selectQueryBuff.append(",");
            }
        }
        selectQueryBuff.append(")");
        final String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadInterfaceList", "Select Query " + selectQuery);
        }
        try {
            int i = 0;
            serviceClassList = new ArrayList();
            pstmtSelect = p_con.prepareStatement(selectQuery);
            for (int x = 0; x < m_serviceStr.length; x++) {
                pstmtSelect.setString(++i, m_serviceStr[x]);
            }
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
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassNames]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassNames]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Size() " + serviceClassList.size());
            }
        }
        return serviceClassList;
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
        final String methodName = "isServiceCodeExists";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered service class code::" + p_serviceClassCode);
        }

        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM service_classes WHERE ");
        sqlBuff.append(" service_class_code=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceClassCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isServiceCodeExists]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[isServiceCodeExists]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }
        return found;
    }
	
		public ArrayList loadServiceClassList(Connection p_con,String p_networkCode,String p_interfaceCategory)throws BTSLBaseException
	{
		final String methodName = "loadServiceClassList";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered p_interfaceCategory="+p_interfaceCategory);
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		ListValueVO listValueVO=null;
		ArrayList serviceClassVOList=null;
		try
		{
			StringBuffer selectQuery = new StringBuffer();
			selectQuery.append("SELECT service_class_code,service_class_name,interface_category,service_class_id ");
			selectQuery.append("FROM service_classes S,interfaces I,interface_types IT ,Interface_network_mapping IMP ");
			selectQuery.append("WHERE S.interface_id = I.interface_id AND IMP.INTERFACE_ID = I.interface_id AND ");
			selectQuery.append("IMP.network_code = "+"'"+p_networkCode +"'"+" AND ");
			selectQuery.append("I.interface_type_id = IT.interface_type_id AND interface_category IN("+p_interfaceCategory+") AND S.status <> ? ");
			
			selectQuery.append("ORDER BY interface_category ");
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Query=" + selectQuery);
			pstmtSelect = p_con.prepareStatement(selectQuery.toString());
			pstmtSelect.setString(1,PretupsI.SERVICE_CLASS_STATUS_DELETE);
			
			rs = pstmtSelect.executeQuery();
			serviceClassVOList=new ArrayList();
			while(rs.next())
			{
				listValueVO=new ListValueVO(rs.getString("service_class_name")+"("+rs.getString("service_class_code")+")",rs.getString("interface_category")+":"+rs.getString("service_class_id"));
				//listValueVO.setStatusType(rs.getString("statustype"));
				serviceClassVOList.add(listValueVO);
			}
			
		}
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException:"+ sqe.getMessage());
			_log.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ServiceClassDAO[loadServiceClassList]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
		}
		catch (Exception e)
		{
			_log.error(methodName, "Exception:"+ e.getMessage());
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ServiceClassDAO[loadServiceClassList]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.processing");
		}
		finally
		{
			try{if (rs != null)rs.close();}catch (Exception ex){_log.errorTrace(methodName,ex);}
			try{if (pstmtSelect != null)pstmtSelect.close();}catch (Exception ex){_log.errorTrace(methodName,ex);}
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Exiting:serviceClassVOList.size()="+serviceClassVOList.size());
		}
		return serviceClassVOList;
	}
		
		
		
		
		
		
		public ArrayList validateServiceClassList(Connection p_con, String p_interfaceCategory,String serviceClassId) throws BTSLBaseException {
	        final String methodName = "loadServiceClassList";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered p_interfaceCategory=" + p_interfaceCategory);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        ListValueVO listValueVO = null;
	        ArrayList serviceClassVOList = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer();
	            selectQuery.append("SELECT service_class_code,service_class_name,interface_category,service_class_id ");
	            selectQuery.append("FROM service_classes S,interfaces I,interface_types IT ");
	            selectQuery.append("WHERE S.interface_id = I.interface_id AND ");
	            selectQuery.append("I.interface_type_id = IT.interface_type_id AND interface_category IN(" + p_interfaceCategory + ") AND S.status <> ?  AND service_class_id =? " );
	            selectQuery.append(" ORDER BY interface_category ");
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            pstmtSelect.setString(1, PretupsI.SERVICE_CLASS_STATUS_DELETE);
	            pstmtSelect.setString(2, serviceClassId);
	            rs = pstmtSelect.executeQuery();
	            serviceClassVOList = new ArrayList();
	            while (rs.next()) {
	                listValueVO = new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("service_class_name")) + "(" +
	                		       SqlParameterEncoder.encodeParams(rs.getString("service_class_code")) + ")",
	                			   SqlParameterEncoder.encodeParams(rs.getString("interface_category")) + ":" + 
	                			   SqlParameterEncoder.encodeParams(rs.getString("service_class_id")));
	                // listValueVO.setStatusType(rs.getString("statustype"));
	                serviceClassVOList.add(listValueVO);
	            }

	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "",
	                "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassList]", "", "", "",
	                "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            try {
	                if (pstmtSelect != null) {
	                    pstmtSelect.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting:serviceClassVOList.size()=" + serviceClassVOList.size());
	            }
	        }
	        return serviceClassVOList;
	    }
	
		
		
}
