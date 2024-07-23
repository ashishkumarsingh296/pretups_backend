package com.selftopup.pretups.interfaces.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;

/**
 * @(#)InterfaceNetworkMappingVO.java
 *                                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                    All Rights Reserved
 * 
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Mohit Goel 21/09/2005 Initial Creation
 * 
 *                                    This class is used for Interface Network
 *                                    Mapping
 * 
 */

public class InterfaceNetworkMappingDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading Network List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_lookupType
     *            String//to know thw name of the interface Category defined in
     *            the lookups table
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadInterfaceNetworkMappingList(Connection p_con, String p_networkCode, String p_lookupType) throws BTSLBaseException {

        final String methodName = "loadInterfaceNetworkMappingList()";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_networkCode" + p_networkCode + " p_lookupType=" + p_lookupType);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT inm.network_code,inm.interface_id,inm.queue_size,inm.queue_time_out,");
        strBuff.append(" inm.request_time_out,inm.next_check_que_req_sec,");
        strBuff.append(" inm.created_by, inm.modified_by, inm.created_on, inm.modified_on,");
        strBuff.append(" it.interface_name,i.interface_description, it.interface_category,l.lookup_name ");
        strBuff.append(" FROM interface_network_mapping inm,interfaces i,interface_types it,lookups l ");
        strBuff.append(" WHERE i.status!='N' AND inm.network_code = ? AND inm.interface_id = i.interface_id ");
        strBuff.append(" AND i.interface_type_id = it.interface_type_id AND it.interface_category = l.lookup_code");
        strBuff.append(" AND l.lookup_type = ? ");
        strBuff.append(" ORDER BY it.interface_name");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_lookupType);
            rs = pstmt.executeQuery();
            InterfaceNetworkMappingVO interfaceNetworkMappingVO = null;
            while (rs.next()) {
                interfaceNetworkMappingVO = new InterfaceNetworkMappingVO();
                interfaceNetworkMappingVO.setNetworkCode(rs.getString("network_code"));
                interfaceNetworkMappingVO.setInterfaceID(rs.getString("interface_id"));
                interfaceNetworkMappingVO.setQueueSize(rs.getLong("queue_size"));
                interfaceNetworkMappingVO.setQueueTimeOut(rs.getLong("queue_time_out"));
                interfaceNetworkMappingVO.setRequestTimeOut(rs.getLong("request_time_out"));
                interfaceNetworkMappingVO.setNextCheckQueueReqSec(rs.getLong("next_check_que_req_sec"));
                interfaceNetworkMappingVO.setCreatedBy(rs.getString("created_by"));
                interfaceNetworkMappingVO.setModifiedBy(rs.getString("modified_by"));
                interfaceNetworkMappingVO.setCreatedOn(rs.getDate("created_on"));
                interfaceNetworkMappingVO.setModifiedOn(rs.getDate("modified_on"));
                interfaceNetworkMappingVO.setLastModifiedOn((rs.getTimestamp("modified_on").getTime()));
                interfaceNetworkMappingVO.setInterfaceName(rs.getString("interface_name"));
                interfaceNetworkMappingVO.setInterfaceIDDesc(rs.getString("interface_description"));
                interfaceNetworkMappingVO.setInterfaceCategoryID(rs.getString("interface_category"));
                interfaceNetworkMappingVO.setInterfaceCategoryIDDesc(rs.getString("lookup_name"));

                list.add(interfaceNetworkMappingVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[loadInterfaceNetworkMappingList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[loadInterfaceNetworkMappingList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: networkMap size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for Inserting Interface Network Mapping Detail.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_interfaceNetworkMappingVO
     *            InterfaceNetworkMappingVO
     * 
     * @return addCount int
     * @exception BTSLBaseException
     */
    public int addInterfaceNetworkMapping(Connection p_con, InterfaceNetworkMappingVO p_interfaceNetworkMappingVO) throws BTSLBaseException {
        PreparedStatement pstmtInsert = null;
        int addCount = 0;

        if (_log.isDebugEnabled()) {
            _log.debug("addInterfaceNetworkMapping", "Entered: InterfaceNetworkMappingVO= " + p_interfaceNetworkMappingVO);
        }

        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO interface_network_mapping (network_code, ");
            strBuff.append(" interface_id,queue_size,queue_time_out,request_time_out, ");
            strBuff.append("next_check_que_req_sec,created_by,modified_by,created_on,modified_on)");
            strBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?)");

            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addInterfaceNetworkMapping", "QUERY sqlInsert:" + insertQuery);
            }

            pstmtInsert = p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, p_interfaceNetworkMappingVO.getNetworkCode());
            pstmtInsert.setString(2, p_interfaceNetworkMappingVO.getInterfaceID());
            pstmtInsert.setLong(3, p_interfaceNetworkMappingVO.getQueueSize());
            pstmtInsert.setLong(4, p_interfaceNetworkMappingVO.getQueueTimeOut());
            pstmtInsert.setLong(5, p_interfaceNetworkMappingVO.getRequestTimeOut());
            pstmtInsert.setLong(6, p_interfaceNetworkMappingVO.getNextCheckQueueReqSec());
            pstmtInsert.setString(7, p_interfaceNetworkMappingVO.getCreatedBy());
            pstmtInsert.setString(8, p_interfaceNetworkMappingVO.getModifiedBy());
            pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_interfaceNetworkMappingVO.getCreatedOn()));
            pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_interfaceNetworkMappingVO.getModifiedOn()));

            addCount = pstmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error("addInterfaceNetworkMapping", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[addInterfaceNetworkMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addInterfaceNetworkMapping", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addInterfaceNetworkMapping", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[addInterfaceNetworkMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addInterfaceNetworkMapping", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("addInterfaceNetworkMapping", "Exiting: addCount=" + addCount);
            }
        } // end of finally

        return addCount;
    }

    /**
     * Method for Updating Interface Network Mapping Detail.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_interfaceNetworkMappingVO
     *            InterfaceNetworkMappingVO
     * 
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int updateInterfaceNetworkMapping(Connection p_con, InterfaceNetworkMappingVO p_interfaceNetworkMappingVO) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        if (_log.isDebugEnabled()) {
            _log.debug("updateInterfaceNetworkMapping", "Entered: p_interfaceNetworkMappingVO= " + p_interfaceNetworkMappingVO);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("Update interface_network_mapping SET queue_size = ?,");
            strBuff.append("queue_time_out = ?,request_time_out = ?,next_check_que_req_sec = ?, ");
            strBuff.append("modified_by = ?, modified_on= ? WHERE network_code = ? AND interface_id = ?");

            String updateQuery = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("updateInterfaceNetworkMapping", "Query sqlUpdate:" + updateQuery);
            }

            // check wehther the record already updated or not
            boolean modified = this.recordModified(p_con, p_interfaceNetworkMappingVO.getNetworkCode(), p_interfaceNetworkMappingVO.getInterfaceID(), p_interfaceNetworkMappingVO.getLastModifiedOn());

            // call the DAO method to Update the network Detail
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }

            psmtUpdate = p_con.prepareStatement(updateQuery);

            psmtUpdate.setLong(1, p_interfaceNetworkMappingVO.getQueueSize());
            psmtUpdate.setLong(2, p_interfaceNetworkMappingVO.getQueueTimeOut());
            psmtUpdate.setLong(3, p_interfaceNetworkMappingVO.getRequestTimeOut());
            psmtUpdate.setLong(4, p_interfaceNetworkMappingVO.getNextCheckQueueReqSec());
            psmtUpdate.setString(5, p_interfaceNetworkMappingVO.getModifiedBy());
            psmtUpdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_interfaceNetworkMappingVO.getModifiedOn()));
            psmtUpdate.setString(7, p_interfaceNetworkMappingVO.getNetworkCode());
            psmtUpdate.setString(8, p_interfaceNetworkMappingVO.getInterfaceID());

            updateCount = psmtUpdate.executeUpdate();
        } // end of try
        catch (BTSLBaseException be) {
            _log.error("updateInterfaceNetworkMapping", "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error("updateInterfaceNetworkMapping", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[updateInterfaceNetworkMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateInterfaceNetworkMapping", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("updateInterfaceNetworkMapping", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[updateInterfaceNetworkMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateInterfaceNetworkMapping", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("updateInterfaceNetworkMapping", "Exiting: updateCount=" + updateCount);
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
     * @param networkCode
     *            String
     * @param p_interfaceID
     *            String
     * @param oldLastModified
     *            long
     * 
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String networkCode, String p_interfaceID, long oldLastModified) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("recordModified", "Entered: networkCode= " + networkCode + " p_interfaceID=" + p_interfaceID + "oldLastModified= " + oldLastModified);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM interface_network_mapping WHERE network_code=? and interface_id = ?";
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
            pstmt.setString(1, networkCode);
            pstmt.setString(2, p_interfaceID);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("recordModified", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
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
     * Method for loading Network Prefix for a particular Network.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadInterfaceNetworkPrefix(Connection p_con, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadInterfaceNetworkPrefix", "Entered netwrkCode=" + p_networkCode);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList networkList = new ArrayList();
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT inpm.network_code,inpm.interface_id,np.series_type, ");
        strBuff.append("inpm.prefix_id,inpm.action,inpm.method_type,np.series ");
        strBuff.append(" FROM intf_ntwrk_prfx_mapping inpm,network_prefixes np ");
        strBuff.append(" where inpm.network_code = ? AND inpm.network_code = np.network_code ");
        strBuff.append(" AND inpm.prefix_id = np.prefix_id order by interface_id ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadInterfaceNetworkPrefix", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            rs = pstmt.executeQuery();

            InterfaceNetworkPrefixMappingVO interfaceNetworkPrefixMappingVO = null;

            while (rs.next()) {
                interfaceNetworkPrefixMappingVO = new InterfaceNetworkPrefixMappingVO();
                interfaceNetworkPrefixMappingVO.setNetworkCode(rs.getString("network_code"));
                interfaceNetworkPrefixMappingVO.setInterfaceID(rs.getString("interface_id"));
                interfaceNetworkPrefixMappingVO.setAction(rs.getString("action"));
                interfaceNetworkPrefixMappingVO.setMethodType(rs.getString("method_type"));
                interfaceNetworkPrefixMappingVO.setSeries(rs.getString("series"));
                interfaceNetworkPrefixMappingVO.setSeriesType(rs.getString("series_type"));

                networkList.add(interfaceNetworkPrefixMappingVO);
            }

            return networkList;
        } catch (SQLException sqe) {
            _log.error("loadInterfaceNetworkPrefix", "SQLException: " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[loadInterfaceNetworkPrefix]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceNetworkPrefix", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadInterfaceNetworkPrefix", "Exception: " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[loadInterfaceNetworkPrefix]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceNetworkPrefix", "error.general.processing");
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

            if (_log.isDebugEnabled()) {
                _log.debug("loadInterfaceNetworkPrefix", "Exiting: List size=" + networkList.size());
            }
        }
    }

    /**
     * Method for inserting Interface Network Prefix Mapping.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            ArrayList
     * 
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int insertInterfaceNetworkPrefix(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        if (_log.isDebugEnabled()) {
            _log.debug("insertInterfaceNetworkPrefix", "Entered: p_voList List Size= " + p_voList.size());
        }
        try {
            // int count = 0;

            if ((p_voList != null)) {
                StringBuffer strBuff = new StringBuffer();

                strBuff.append("INSERT INTO intf_ntwrk_prfx_mapping (network_code,interface_id,");
                strBuff.append("prefix_id,action,method_type,");
                strBuff.append("created_by,modified_by,created_on,modified_on) values ");
                strBuff.append("(?,?,?,?,?,?,?,?,?)");

                String insertQuery = strBuff.toString();

                if (_log.isDebugEnabled()) {
                    _log.debug("insertInterfaceNetworkPrefix", "Query sqlInsert:" + insertQuery);
                }

                psmtInsert = p_con.prepareStatement(insertQuery);
                InterfaceNetworkPrefixMappingVO interfaceNetworkPrefixMappingVO = null;
                for (int i = 0, j = p_voList.size(); i < j; i++) {
                    interfaceNetworkPrefixMappingVO = (InterfaceNetworkPrefixMappingVO) p_voList.get(i);

                    psmtInsert.setString(1, interfaceNetworkPrefixMappingVO.getNetworkCode());
                    psmtInsert.setString(2, interfaceNetworkPrefixMappingVO.getInterfaceID());
                    psmtInsert.setLong(3, interfaceNetworkPrefixMappingVO.getPrefixID());
                    psmtInsert.setString(4, interfaceNetworkPrefixMappingVO.getAction());
                    psmtInsert.setString(5, interfaceNetworkPrefixMappingVO.getMethodType());
                    psmtInsert.setString(6, interfaceNetworkPrefixMappingVO.getCreatedBy());
                    psmtInsert.setString(7, interfaceNetworkPrefixMappingVO.getModifiedBy());
                    psmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(interfaceNetworkPrefixMappingVO.getCreatedOn()));
                    psmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(interfaceNetworkPrefixMappingVO.getModifiedOn()));

                    insertCount = psmtInsert.executeUpdate();

                    psmtInsert.clearParameters();
                    // check the status of the update
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this, "insertInterfaceNetworkPrefix", "error.general.sql.processing");
                    }
                }

            }
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error("insertInterfaceNetworkPrefix", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[insertInterfaceNetworkPrefix]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "insertInterfaceNetworkPrefix", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("insertInterfaceNetworkPrefix", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[insertInterfaceNetworkPrefix]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "insertInterfaceNetworkPrefix", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("insertInterfaceNetworkPrefix", "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for deleting Interface Network Prefix Mapping
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * 
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteInterfaceNetworkPrefix(Connection p_con, String p_networkCode) throws BTSLBaseException {
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;

        if (_log.isDebugEnabled()) {
            _log.debug("deleteInterfaceNetworkPrefix", "Entered: p_networkCode= " + p_networkCode);
        }

        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("DELETE FROM intf_ntwrk_prfx_mapping where ");
            strBuff.append("network_code = ?");
            String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("deleteInterfaceNetworkPrefix", "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_networkCode);
            deleteCount = psmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error("deleteInterfaceNetworkPrefix", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[deleteInterfaceNetworkPrefix]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteInterfaceNetworkPrefix", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("deleteInterfaceNetworkPrefix", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[deleteInterfaceNetworkPrefix]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteInterfaceNetworkPrefix", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deleteInterfaceNetworkPrefix", "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for checking if interface is associated with network prefix or not
     * This method is to be called before deletion of interface network mapping
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_interfaceID
     *            String
     * 
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isExistsInterfaceNetworkPrefixMapping(Connection p_con, String p_networkCode, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isExistsInterfaceNetworkPrefixMapping", "Entered: p_networkCode= " + p_networkCode + " p_interfaceID= " + p_interfaceID);
        PreparedStatement psmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT 1 FROM intf_ntwrk_prfx_mapping WHERE network_code=? AND interface_id=? ");
            String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("isExistsInterfaceNetworkPrefixMapping", "Query selectQuery:" + selectQuery);
            psmtSelect = p_con.prepareStatement(selectQuery);
            psmtSelect.setString(1, p_networkCode);
            psmtSelect.setString(2, p_interfaceID);
            rs = psmtSelect.executeQuery();
            if (rs.next())
                found = true;
        } // end of try
        catch (SQLException sqle) {
            _log.error("isExistsInterfaceNetworkPrefixMapping", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[isExistsInterfaceNetworkPrefixMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsInterfaceNetworkPrefixMapping", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("isExistsInterfaceNetworkPrefixMapping", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[isExistsInterfaceNetworkPrefixMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExistsInterfaceNetworkPrefixMapping", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (psmtSelect != null) {
                    psmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isExistsInterfaceNetworkPrefixMapping", "Exiting: found=" + found);
        } // end of finally
        return found;
    }

    /**
     * Method for deleting Interface Network Mapping
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * 
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteInterfaceNetworkMapping(Connection p_con, String p_networkCode, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteInterfaceNetworkMapping", "Entered: p_networkCode= " + p_networkCode + " p_interfaceID= " + p_interfaceID);
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("DELETE FROM interface_network_mapping WHERE ");
            strBuff.append("network_code = ? AND interface_id=? ");
            String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("deleteInterfaceNetworkMapping", "Query sqlDelete:" + deleteQuery);
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_networkCode);
            psmtDelete.setString(2, p_interfaceID);
            deleteCount = psmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error("deleteInterfaceNetworkMapping", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[deleteInterfaceNetworkMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteInterfaceNetworkPrefix", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("deleteInterfaceNetworkMapping", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[deleteInterfaceNetworkMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteInterfaceNetworkPrefix", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteInterfaceNetworkMapping", "Exiting: deleteCount=" + deleteCount);
        } // end of finally

        return deleteCount;
    }

}
