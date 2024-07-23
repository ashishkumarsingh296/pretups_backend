/*
 * @# NetworkServiceDAO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Aug 16, 2005 Initial creation
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

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;
// import oracle.jdbc.OraclePreparedStatement;
import com.selftopup.common.BTSLBaseException;

/**
 * 
 */

public class NetworkServiceDAO {
    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());


    public HashMap loadNetworkServicesList() throws BTSLBaseException {
        final String methodName = "loadNetworkServicesList";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        PreparedStatement pstmtSelect = null;
        HashMap networkServiceMap = new HashMap();
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT module_code,service_type, sender_network, receiver_network, status, language1_message, language2_message,  ");
            selectQueryBuff.append(" created_by, created_on, modified_by, modified_on FROM network_services  ");
            String selectQuery = selectQueryBuff.toString();
            NetworkServiceVO networkServiceVO = null;
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Select Query= " + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                networkServiceVO = new NetworkServiceVO();
                networkServiceVO.setModuleCode(rs.getString("module_code"));
                networkServiceVO.setServiceType(rs.getString("service_type"));
                networkServiceVO.setSenderNetwork(rs.getString("sender_network"));
                networkServiceVO.setReceiverNetwork(rs.getString("receiver_network"));
                networkServiceVO.setStatus(rs.getString("status"));
                networkServiceVO.setLanguage1Message(rs.getString("language1_message"));
                networkServiceVO.setLanguage2Message(rs.getString("language2_message"));
                networkServiceVO.setCreatedBy(rs.getString("created_by"));
                networkServiceVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                networkServiceVO.setModifiedBy(rs.getString("modified_by"));
                networkServiceVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                networkServiceMap.put(networkServiceVO.getModuleCode() + "_" + networkServiceVO.getSenderNetwork() + "_" + networkServiceVO.getReceiverNetwork() + "_" + networkServiceVO.getServiceType(), networkServiceVO);
            }
            return networkServiceMap;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting networkServiceMap.size:" + networkServiceMap.size());
        }// end of finally
    }

    /**
     * Method loadNetworkServicesList.
     * 
     * @param p_con
     *            Connection
     * @param p_moduleCode
     *            String
     * @param p_serviceType
     *            String
     * @param p_receiverNetwork
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadNetworkServicesList(Connection p_con, String p_moduleCode, String p_serviceType, String p_receiverNetwork) throws BTSLBaseException {
        final String methodName = "loadNetworkServicesList";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered:p_receiverNetwork=" + p_receiverNetwork + ",p_serviceType=" + p_serviceType + ",p_moduleCode=" + p_moduleCode);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList networkServiceList = null;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_name,network_code,NTS.language1_message,NTS.language2_message, ");
        strBuff.append("NTS.status,NTS.modified_on modified_on1,NT.modified_on modified_on2 ");
        strBuff.append("FROM networks NT,network_services NTS ");
        strBuff.append("WHERE NTS.receiver_network(+)=? AND NTS.module_code(+)=? ");
        strBuff.append("AND NTS.service_type(+)=? AND NTS.sender_network(+)=NT.network_code ");

        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(1, p_receiverNetwork);
            pstmt.setString(2, p_moduleCode);
            pstmt.setString(3, p_serviceType);
            rs = pstmt.executeQuery();
            NetworkServiceVO networkServiceVO = null;
            networkServiceList = new ArrayList();
            while (rs.next()) {
                networkServiceVO = new NetworkServiceVO();
                networkServiceVO.setSenderNetwork(rs.getString("network_code"));
                networkServiceVO.setSenderNetworkDes(rs.getString("network_name"));
                networkServiceVO.setLanguage1Message(rs.getString("language1_message"));
                networkServiceVO.setLanguage2Message(rs.getString("language2_message"));
                networkServiceVO.setStatus(rs.getString("status"));
                if (rs.getTimestamp("modified_on1") != null)
                    networkServiceVO.setLastModifiedTime(rs.getTimestamp("modified_on1").getTime());
                else
                    networkServiceVO.setLastModifiedTime(0);
                networkServiceVO.setModuleCode(p_moduleCode);
                networkServiceVO.setReceiverNetwork(p_receiverNetwork);
                networkServiceVO.setServiceType(p_serviceType);
                networkServiceList.add(networkServiceVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException: " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[loadNetworkServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception: " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[loadNetworkServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting: networkServiceList size=" + networkServiceList.size());
        }
        return networkServiceList;
    }

    /**
     * Method updateNetworkServices.
     * This method perform dual activity it may be insert the new record or
     * update the existing record if exist.
     * 
     * @param p_con
     *            Connection
     * @param p_networkServiceVO
     *            NetworkServiceVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateNetworkServices(Connection p_con, NetworkServiceVO p_networkServiceVO) throws BTSLBaseException {
        final String methodName = "updateNetworkServices";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered:p_networkServiceVO=" + p_networkServiceVO);
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtSelect = null;

        ResultSet rs = null;

        int updateCount = 0;

        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT 1 FROM network_services ");
        selectQuery.append("WHERE module_code=? AND service_type=? AND sender_network=? AND receiver_network =? ");

        StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("UPDATE network_services SET status=?, language1_message=?, language2_message=?, modified_by=?, modified_on=? ");
        updateQuery.append("WHERE module_code=? AND service_type=? AND sender_network=? AND receiver_network =? ");

        StringBuilder insertQuery = new StringBuilder();
        insertQuery.append("INSERT INTO network_services (module_code, service_type, sender_network, ");
        insertQuery.append("receiver_network,status, language1_message, language2_message, created_by, created_on, ");
        insertQuery.append("modified_by, modified_on) ");
        insertQuery.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?)");

        ArrayList networkServiceVOList = p_networkServiceVO.getNetworkServicesVOList();
        NetworkServiceVO networkServiceVO = null;
        try {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "selectQUERY=" + selectQuery);

            if (_log.isDebugEnabled())
                _log.debug(methodName, "updateQUERY=" + updateQuery);

            if (_log.isDebugEnabled())
                _log.debug(methodName, "insertQUERY=" + insertQuery);

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            pstmtInsert = p_con.prepareStatement(insertQuery.toString());
            if (networkServiceVOList != null && !networkServiceVOList.isEmpty()) {
                for (int i = 0, j = networkServiceVOList.size(); i < j; i++) {
                    networkServiceVO = (NetworkServiceVO) networkServiceVOList.get(i);

                    pstmtSelect.setString(1, networkServiceVO.getModuleCode());
                    pstmtSelect.setString(2, networkServiceVO.getServiceType());
                    pstmtSelect.setString(3, networkServiceVO.getSenderNetwork());
                    pstmtSelect.setString(4, networkServiceVO.getReceiverNetwork());
                    rs = pstmtSelect.executeQuery();

                    pstmtSelect.clearParameters();

                    // if record exist then update its information
                    if (rs.next()) {
                        pstmtUpdate.setString(1, networkServiceVO.getStatus());
                        pstmtUpdate.setString(2, networkServiceVO.getLanguage1Message());

                        // for multilanguage support
                        // pstmtUpdate.setFormOfUse(3,
                        // OraclePreparedStatement.FORM_NCHAR);
                        pstmtUpdate.setString(3, networkServiceVO.getLanguage2Message());

                        pstmtUpdate.setString(4, p_networkServiceVO.getModifiedBy());
                        pstmtUpdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_networkServiceVO.getModifiedOn()));

                        pstmtUpdate.setString(6, networkServiceVO.getModuleCode());
                        pstmtUpdate.setString(7, networkServiceVO.getServiceType());
                        pstmtUpdate.setString(8, networkServiceVO.getSenderNetwork());
                        pstmtUpdate.setString(9, networkServiceVO.getReceiverNetwork());

                        // for checking that is the record is modified during
                        // the transaction.
                        boolean modified = isRecordModified(p_con, networkServiceVO);
                        if (modified)
                            throw new BTSLBaseException(this, methodName, "error.modify.true");
                        else
                            updateCount = pstmtUpdate.executeUpdate();

                        pstmtUpdate.clearParameters();
                    }
                    // if record is not exist then insert the new record.
                    else {
                        pstmtInsert.setString(1, networkServiceVO.getModuleCode());
                        pstmtInsert.setString(2, networkServiceVO.getServiceType());
                        pstmtInsert.setString(3, networkServiceVO.getSenderNetwork());
                        pstmtInsert.setString(4, networkServiceVO.getReceiverNetwork());

                        pstmtInsert.setString(5, networkServiceVO.getStatus());
                        pstmtInsert.setString(6, networkServiceVO.getLanguage1Message());

                        // for multilanguage support
                        // pstmtInsert.setFormOfUse(7,
                        // OraclePreparedStatement.FORM_NCHAR);
                        pstmtInsert.setString(7, networkServiceVO.getLanguage2Message());

                        pstmtInsert.setString(8, p_networkServiceVO.getCreatedBy());
                        pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_networkServiceVO.getCreatedOn()));
                        pstmtInsert.setString(10, p_networkServiceVO.getModifiedBy());
                        pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_networkServiceVO.getModifiedOn()));
                        updateCount = pstmtInsert.executeUpdate();

                        pstmtInsert.clearParameters();
                    }
                    if (updateCount <= 0)
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[updateNetworkServices]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[updateNetworkServices]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:return=" + updateCount);
        }
        return updateCount;
    }

    /**
     * Method isRecordModified.
     * 
     * @param p_con
     *            Connection
     * @param p_networkServiceVO
     *            NetworkServiceVO
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, NetworkServiceVO p_networkServiceVO) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered:p_networkServiceVO=" + p_networkServiceVO);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        StringBuilder sqlRecordModified = new StringBuilder();
        try {
            sqlRecordModified.append("SELECT modified_on FROM network_services ");
            sqlRecordModified.append("WHERE module_code=? AND service_type=? AND sender_network=? AND receiver_network=? ");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_networkServiceVO.getModuleCode());
            pstmtSelect.setString(2, p_networkServiceVO.getServiceType());
            pstmtSelect.setString(3, p_networkServiceVO.getSenderNetwork());
            pstmtSelect.setString(4, p_networkServiceVO.getReceiverNetwork());

            Timestamp newlastModified = null;
            if (p_networkServiceVO.getLastModifiedTime() == 0) {
                return false;
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            if (newlastModified.getTime() != p_networkServiceVO.getLastModifiedTime()) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[isRecordModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[isRecordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exititng:modified=" + modified);
        }// end of finally
        return modified;
    }// end recordModified

}
