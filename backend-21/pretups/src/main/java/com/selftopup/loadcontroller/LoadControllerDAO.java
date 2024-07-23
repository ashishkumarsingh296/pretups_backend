package com.selftopup.loadcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;

import com.selftopup.common.BTSLBaseException;
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
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

/*
 * LoadControllerDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Data Access Object class for interaction with the load related data
 */

public class LoadControllerDAO {

    private static Log _log = LogFactory.getLog(LoadControllerDAO.class.getName());

    /**
     * Method to load the instance load
     * 
     * @param p_instanceID
     * @param p_instanceloadType
     * @return Hashtable
     * @throws SQLException
     * @throws Exception
     */
    public Hashtable loadInstanceLoadDetails(String p_instanceID) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadInstanceLoadDetails", "Entered p_instanceID:" + p_instanceID);
        PreparedStatement pstmtSelect = null;
        Hashtable instanceLoadTable = new Hashtable();
        java.util.Date newDate = new Date();
        Timestamp time = new Timestamp(newDate.getTime());
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            InstanceLoadVO instanceLoadVO = null;

            // Changed by Dhiraj on 01/03/2007 to get module.
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT instance_id, instance_name, current_status, ip, port, instance_load, instance_tps, ");
            selectQueryBuff.append(" instance_load max_trans_load,instance_tps max_trans_tps,request_time_out, instance_type, load_type_tps, module, is_dr,authentication_pass, MAX_ALLOWED_LOAD ");
            selectQueryBuff.append(" FROM instance_load  WHERE   instance_id=? ");
            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT tload.instance_id instance_id,iload.instance_name instance_name,iload.current_status current_status,iload.ip ip,iload.port port,iload.instance_load instance_load, "
             * );
             * selectQueryBuff.append(
             * " iload.request_time_out request_time_out,iload.instance_tps instance_tps,sum(tload.max_transaction_load) max_trans_load,sum(tload.transaction_tps) max_trans_tps "
             * );
             * selectQueryBuff.append(
             * " FROM transaction_load tload,instance_load iload ");
             * selectQueryBuff.append(
             * " WHERE tload.instance_id=? AND tload.instance_id=iload.instance_id "
             * );
             * selectQueryBuff.append(
             * " GROUP BY tload.instance_id,iload.instance_name,iload.current_status,iload.ip,iload.port, "
             * );
             * selectQueryBuff.append(
             * " iload.instance_load,iload.request_time_out,iload.instance_tps "
             * );
             */

            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT instance_id, instance_name,current_status,ip,port,instance_txn_load,request_time_out,instance_tps "
             * );
             * selectQueryBuff.append(" FROM instance_load WHERE instance_id= ? "
             * );
             */

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadInstanceLoadDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                instanceLoadVO = new InstanceLoadVO();
                instanceLoadVO.setInstanceID(rs.getString("instance_id"));
                instanceLoadVO.setInstanceName(rs.getString("instance_name"));
                instanceLoadVO.setCurrentStatus(rs.getString("current_status"));
                instanceLoadVO.setHostAddress(rs.getString("ip"));
                instanceLoadVO.setHostPort(rs.getString("port"));
                instanceLoadVO.setDefinedTransactionLoad(rs.getLong("instance_load"));
                instanceLoadVO.setTransactionLoad(rs.getLong("max_trans_load"));
                instanceLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                instanceLoadVO.setDefinedTPS(rs.getLong("instance_tps"));
                instanceLoadVO.setDefualtTPS(rs.getLong("max_trans_tps"));
                instanceLoadVO.setCurrentTPS(rs.getLong("max_trans_tps"));
                if (TypesI.YES.equals(rs.getString("load_type_tps")))
                    instanceLoadVO.setLoadType(LoadControllerI.LOAD_CONTROLLER_TPS_TYPE);
                else
                    instanceLoadVO.setLoadType(LoadControllerI.LOAD_CONTROLLER_TXN_TYPE);
                instanceLoadVO.setModule(rs.getString("module"));
                instanceLoadVO.setLastInitializationTime(newDate);
                instanceLoadVO.setLastRefusedTime(time);
                instanceLoadVO.setLastReceievedTime(time);
                instanceLoadVO.setLastTxnProcessStartTime(time);
                instanceLoadVO.setIsDR(rs.getString("is_dr"));
                instanceLoadVO.setAuthPass(rs.getString("authentication_pass"));
                instanceLoadVO.setMaxAllowedLoad(rs.getLong("MAX_ALLOWED_LOAD"));

                if ((LoadControllerI.STATUS_N).equalsIgnoreCase(rs.getString("current_status")))
                    instanceLoadVO.setInstanceLoadStatus(false);
                instanceLoadTable.put(p_instanceID, instanceLoadVO);

                if (_log.isDebugEnabled())
                    _log.debug("loadInstanceLoadDetails", "Values loaded for instance_id=" + p_instanceID + " instanceLoadVO=" + instanceLoadVO.toString());
            }// end while
            return instanceLoadTable;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadInstanceLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadInstanceLoadDetails]", "", "", "", "SQL Exception while loading load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadInstanceLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadInstanceLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadInstanceLoadDetails]", "", "", "", "Exception while loading load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadInstanceLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInstanceLoadDetails", "Exiting instanceLoadTable.size:" + instanceLoadTable.size());
        }// end of finally
    }

    /**
     * Method to load the Network loads
     * 
     * @param p_instanceID
     * @param p_instanceloadType
     * @return Hashtable
     * @throws SQLException
     * @throws Exception
     */
    public Hashtable loadNetworkLoadDetails(String p_instanceID, String p_instanceloadType, boolean p_addInObjectMap) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkLoadDetails", "Entered p_instanceID:" + p_instanceID + " p_instanceloadType=" + p_instanceloadType + "p_addInObjectMap=" + p_addInObjectMap);
        PreparedStatement pstmtSelect = null;
        Hashtable networkLoadTable = new Hashtable();
        Connection con = null;
        ResultSet rs = null;
        java.util.Date newDate = new Date();
        Timestamp time = new Timestamp(newDate.getTime());

        try {
            HashMap hashMap = LoadControllerCache.getNetworkLoadObjectMap();

            con = OracleUtil.getSingleConnection();
            NetworkLoadVO networkLoadVO = null;
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT network_code, network_load, request_time_out, min_process_time, network_tps, ");
            selectQueryBuff.append(" network_load max_trans_load,network_tps max_trans_tps,c2s_instance_id,p2p_instance_id ");
            selectQueryBuff.append(" FROM network_load WHERE instance_id=? ");

            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT nload.network_code network_code,nload.network_load network_load,nload.request_time_out request_time_out,nload.network_tps network_tps, "
             * );
             * selectQueryBuff.append(
             * " sum(tload.max_transaction_load) max_trans_load,sum(tload.transaction_tps) max_trans_tps "
             * );
             * selectQueryBuff.append(
             * " FROM transaction_load tload,network_load nload ");
             * selectQueryBuff.append(
             * " WHERE tload.instance_id=? AND tload.instance_id=nload.instance_id "
             * );
             * selectQueryBuff.append(
             * " GROUP BY nload.network_code,nload.network_load,nload.request_time_out,nload.network_tps "
             * );
             */
            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT instance_id, network_code,network_load,instance_tps,request_time_out "
             * );
             * selectQueryBuff.append(" FROM network_load WHERE instance_id= ? ")
             * ;
             */
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkLoadDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                networkLoadVO = new NetworkLoadVO();
                networkLoadVO.setInstanceID(p_instanceID);
                networkLoadVO.setNetworkCode(rs.getString("network_code"));
                networkLoadVO.setDefinedTransactionLoad(rs.getLong("network_load"));
                networkLoadVO.setTransactionLoad(rs.getLong("max_trans_load"));
                networkLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                networkLoadVO.setDefinedTPS(rs.getLong("network_tps"));
                networkLoadVO.setDefualtTPS(rs.getLong("max_trans_tps"));
                networkLoadVO.setCurrentTPS(rs.getLong("max_trans_tps"));
                networkLoadVO.setMinimumProcessTime(rs.getLong("min_process_time"));

                // Added on 20/07/06 for handling Web recharge request for C2S
                // and P2P in case different servers are there
                networkLoadVO.setC2sInstanceID(rs.getString("c2s_instance_id"));
                networkLoadVO.setP2pInstanceID(rs.getString("p2p_instance_id"));

                networkLoadVO.setLoadType(p_instanceloadType);
                networkLoadVO.setLastInitializationTime(newDate);
                networkLoadVO.setLastRefusedTime(time);
                networkLoadVO.setLastReceievedTime(time);
                networkLoadVO.setLastTxnProcessStartTime(time);

                networkLoadTable.put(p_instanceID + "_" + rs.getString("network_code"), networkLoadVO);

                if (p_addInObjectMap) {
                    if (!hashMap.containsKey(p_instanceID + "_" + rs.getString("network_code")))
                        hashMap.put(p_instanceID + "_" + rs.getString("network_code"), new NetworkLoadController());
                }

                if (_log.isDebugEnabled())
                    _log.debug("loadNetworkLoadDetails", "Values loaded for network code=" + networkLoadVO.getNetworkCode() + " networkLoadVO=" + networkLoadVO.toString());
            }// end while
            if (p_addInObjectMap)
                LoadControllerCache.setNetworkLoadObjectMap(hashMap);
            return networkLoadTable;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadNetworkLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadNetworkLoadDetails]", "", "", "", "Exception while loading network load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadNetworkLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadNetworkLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadNetworkLoadDetails]", "", "", "", "Exception while loading network load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadNetworkLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkLoadDetails", "Exiting networkLoadTable.size:" + networkLoadTable.size());
        }// end of finally
    }

    /**
     * Method to load the interface loads of the instance
     * 
     * @param p_instanceID
     * @param p_instanceloadType
     * @return Hashtable
     * @throws SQLException
     * @throws Exception
     */
    public Hashtable loadInterfaceLoadDetails(String p_instanceID, String p_instanceloadType, boolean p_addInObjectMap) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceLoadDetails", "Entered p_instanceID:" + p_instanceID + " p_instanceloadType=" + p_instanceloadType + "p_addInObjectMap=" + p_addInObjectMap);
        PreparedStatement pstmtSelect = null;
        Hashtable interfaceLoadTable = new Hashtable();

        Connection con = null;
        ResultSet rs = null;
        java.util.Date newDate = new Date();
        Timestamp time = new Timestamp(newDate.getTime());

        try {
            HashMap hashMap = LoadControllerCache.getInterfaceLoadObjectMap();
            InterfaceLoadVO interfaceLoadVO = null;
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT tload.network_code network_code,iload.interface_id interface_id,iload.request_time_out request_time_out, iload.queue_size queue_size,iload.queue_time_out queue_time_out, ");
            selectQueryBuff.append(" iload.next_check_que_req_sec next_check_que_req_sec,sum(tload.max_transaction_load) max_trans_load,sum(tload.transaction_tps) max_trans_tps ");
            selectQueryBuff.append(" FROM transaction_load tload,interface_network_mapping iload ");
            selectQueryBuff.append(" WHERE tload.instance_id=? AND  tload.network_code=iload.network_code AND tload.interface_id=iload.interface_id ");
            selectQueryBuff.append(" GROUP BY tload.network_code,iload.interface_id,iload.request_time_out, iload.queue_size,iload.queue_time_out,iload.next_check_que_req_sec ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceLoadDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                interfaceLoadVO = new InterfaceLoadVO();
                interfaceLoadVO.setInstanceID(p_instanceID);
                interfaceLoadVO.setNetworkCode(rs.getString("network_code"));
                interfaceLoadVO.setInterfaceID(rs.getString("interface_id"));
                interfaceLoadVO.setTransactionLoad(rs.getLong("max_trans_load"));
                interfaceLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                interfaceLoadVO.setDefualtTPS(rs.getLong("max_trans_tps"));
                interfaceLoadVO.setCurrentTPS(rs.getLong("max_trans_tps"));
                interfaceLoadVO.setQueueSize(rs.getLong("queue_size"));
                interfaceLoadVO.setQueueTimeOut(rs.getLong("queue_time_out"));
                interfaceLoadVO.setNextQueueCheckCaseAfterSec(rs.getLong("next_check_que_req_sec"));
                interfaceLoadVO.setLoadType(p_instanceloadType);
                interfaceLoadVO.setLastInitializationTime(newDate);
                interfaceLoadVO.setLastRefusedTime(time);
                interfaceLoadVO.setLastReceievedTime(time);
                interfaceLoadVO.setLastTxnProcessStartTime(time);
                interfaceLoadVO.setLastQueueAdditionTime(time);
                interfaceLoadVO.setLastQueueCaseCheckTime(newDate.getTime());
                interfaceLoadVO.setQueueList(new ArrayList());
                interfaceLoadTable.put(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id"), interfaceLoadVO);

                if (p_addInObjectMap) {
                    if (!hashMap.containsKey(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id")))
                        hashMap.put(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id"), new InterfaceLoadController());
                }

                if (_log.isDebugEnabled())
                    _log.debug("loadInterfaceLoadDetails", "Values loaded for Interface code=" + interfaceLoadVO.getInterfaceID() + " interfaceLoadVO=" + interfaceLoadVO.toString());
            }// end while
            if (p_addInObjectMap)
                LoadControllerCache.setInterfaceLoadObjectMap(hashMap);

            return interfaceLoadTable;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadInterfaceLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadInterfaceLoadDetails]", "", "", "", "Exception while loading interface load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadInterfaceLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadInterfaceLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadInterfaceLoadDetails]", "", "", "", "Exception while loading interface load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadInterfaceLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceLoadDetails", "Exiting interfaceLoadTable.size:" + interfaceLoadTable.size());
        }// end of finally
    }

    /**
     * Method to load the transaction load in an instance
     * 
     * @param p_instanceID
     * @param p_instanceloadType
     * @return Hashtable
     * @throws BTSLBaseException
     */
    public Hashtable loadTransactionLoadDetails(String p_instanceID, String p_instanceloadType, boolean p_addInObjectMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadTransactionLoadDetails", "Entered p_instanceID:" + p_instanceID + " p_instanceloadType=" + p_instanceloadType + " p_addInObjectMap=" + p_addInObjectMap);
        PreparedStatement pstmtSelect = null;
        Hashtable transactionLoadTable = new Hashtable();
        java.util.Date newDate = new Date();
        Timestamp time = new Timestamp(newDate.getTime());

        Connection con = null;
        ResultSet rs = null;
        try {
            HashMap hashMap = LoadControllerCache.getTransactionLoadObjectMap();
            TransactionLoadVO transactionLoadVO = null;
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT tload.network_code network_code,tload.interface_id interface_id,tload.service_type service_type,tload.request_time_out request_time_out, ");
            selectQueryBuff.append(" tload.min_service_timeout min_service_timeout,tload.over_flow_count over_flow_count,tload.max_transaction_load max_transaction_load,tload.transaction_tps transaction_tps,tload.next_check_timeout_sec next_check_timeout_sec ");
            selectQueryBuff.append(" FROM transaction_load tload ");
            selectQueryBuff.append(" WHERE tload.instance_id=? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadTransactionLoadDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                transactionLoadVO = new TransactionLoadVO();
                transactionLoadVO.setInstanceID(p_instanceID);
                transactionLoadVO.setNetworkCode(rs.getString("network_code"));
                transactionLoadVO.setInterfaceID(rs.getString("interface_id"));
                transactionLoadVO.setServiceType(rs.getString("service_type"));
                transactionLoadVO.setDefinedTransactionLoad(rs.getLong("max_transaction_load"));
                transactionLoadVO.setTransactionLoad(rs.getLong("max_transaction_load"));
                transactionLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                transactionLoadVO.setDefinedTPS(rs.getLong("transaction_tps"));
                transactionLoadVO.setDefualtTPS(rs.getLong("transaction_tps"));
                transactionLoadVO.setCurrentTPS(rs.getLong("transaction_tps"));
                transactionLoadVO.setMinimumServiceTime(rs.getLong("min_service_timeout"));
                transactionLoadVO.setDefinedOverFlowCount(rs.getInt("over_flow_count"));
                transactionLoadVO.setNextCheckTimeOutCaseAfterSec(rs.getLong("next_check_timeout_sec"));
                transactionLoadVO.setLoadType(p_instanceloadType);
                transactionLoadVO.setAlternateServiceLoadType(loadAlternateServiceLoadType(con, p_instanceloadType, p_instanceID, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), transactionLoadVO.getServiceType()));
                transactionLoadVO.setLastInitializationTime(newDate);
                transactionLoadVO.setLastRefusedTime(time);
                transactionLoadVO.setLastReceievedTime(time);
                transactionLoadVO.setLastTxnProcessStartTime(time);
                transactionLoadVO.setLastTimeOutCaseCheckTime(newDate.getTime());
                transactionLoadVO.setTransactionListMap(new HashMap());
                transactionLoadTable.put(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id") + "_" + rs.getString("service_type"), transactionLoadVO);

                if (p_addInObjectMap) {
                    if (!hashMap.containsKey(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id") + "_" + rs.getString("service_type")))
                        hashMap.put(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id") + "_" + rs.getString("service_type"), new TransactionLoadController());
                }
                if (_log.isDebugEnabled())
                    _log.debug("loadTransactionLoadDetails", "Values loaded for Interface code=" + transactionLoadVO.getInterfaceID() + " Service=" + transactionLoadVO.getServiceType() + " transactionLoadVO=" + transactionLoadVO.toString());
            }// end while
            if (p_addInObjectMap)
                LoadControllerCache.setTransactionLoadObjectMap(hashMap);

            return transactionLoadTable;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadTransactionLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadTransactionLoadDetails]", "", "", "", "Exception while loading transaction load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadTransactionLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadTransactionLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadTransactionLoadDetails]", "", "", "", "Exception while loading transaction load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadTransactionLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadTransactionLoadDetails", "Exiting transactionLoadTable.size:" + transactionLoadTable.size());
        }// end of finally
    }

    /**
     * Method to load the alternate service types of each service
     * 
     * @param p_con
     * @param p_instanceloadType
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     * @param p_serviceType
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList loadAlternateServiceLoadType(Connection p_con, String p_instanceloadType, String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadAlternateServiceLoadType", "Entered p_instanceID:" + p_instanceID + " p_instanceloadType=" + p_instanceloadType);
        PreparedStatement pstmtSelect = null;
        ArrayList alternateServiceTypeList = null;
        TransactionLoadVO alternateTransactionLoadVO = null;
        ResultSet rs = null;
        int counter = 1;
        java.util.Date newDate = new Date();
        Timestamp time = new Timestamp(newDate.getTime());
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT tload.service_type service_type,tload.request_time_out request_time_out, ");
            selectQueryBuff.append(" tload.min_service_timeout min_service_timeout,tload.over_flow_count over_flow_count,tload.max_transaction_load max_transaction_load,tload.transaction_tps transaction_tps,tload.next_check_timeout_sec next_check_timeout_sec ");
            selectQueryBuff.append(" FROM transaction_load tload ");
            selectQueryBuff.append(" WHERE tload.instance_id=? AND tload.network_code=? AND tload.interface_id=? AND tload.service_type<>? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadAlternateServiceLoadType", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            pstmtSelect.setString(2, p_networkID);
            pstmtSelect.setString(3, p_interfaceID);
            pstmtSelect.setString(4, p_serviceType);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                if (counter == 1)
                    alternateServiceTypeList = new ArrayList();
                alternateTransactionLoadVO = new TransactionLoadVO();
                alternateTransactionLoadVO.setServiceType(rs.getString("service_type"));
                alternateTransactionLoadVO.setDefinedTransactionLoad(rs.getLong("max_transaction_load"));
                alternateTransactionLoadVO.setTransactionLoad(rs.getLong("max_transaction_load"));
                alternateTransactionLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                alternateTransactionLoadVO.setDefinedTPS(rs.getLong("transaction_tps"));
                alternateTransactionLoadVO.setDefualtTPS(rs.getLong("transaction_tps"));
                alternateTransactionLoadVO.setCurrentTPS(rs.getLong("transaction_tps"));
                alternateTransactionLoadVO.setMinimumServiceTime(rs.getLong("min_service_timeout"));
                alternateTransactionLoadVO.setDefinedOverFlowCount(rs.getInt("over_flow_count"));
                alternateTransactionLoadVO.setNextCheckTimeOutCaseAfterSec(rs.getLong("next_check_timeout_sec"));
                alternateTransactionLoadVO.setLoadType(p_instanceloadType);
                alternateTransactionLoadVO.setLastInitializationTime(newDate);
                alternateTransactionLoadVO.setLastRefusedTime(time);
                alternateTransactionLoadVO.setLastReceievedTime(time);
                alternateTransactionLoadVO.setLastTxnProcessStartTime(time);
                alternateTransactionLoadVO.setLastTimeOutCaseCheckTime(newDate.getTime());
                alternateTransactionLoadVO.setTransactionListMap(new HashMap());
                alternateServiceTypeList.add(alternateTransactionLoadVO);
                counter++;

                if (_log.isDebugEnabled())
                    _log.debug("loadAlternateServiceLoadType", "Values loaded for Alternate Service Type for Interface code=" + p_interfaceID + " Service=" + p_serviceType + " alternateServiceTypeList=" + alternateServiceTypeList.size());

            }// end while
            return alternateServiceTypeList;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadAlternateServiceLoadType", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadAlternateServiceLoadType]", "", "", "", "Exception while loading alternate service for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadAlternateServiceLoadType", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadAlternateServiceLoadType", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadAlternateServiceLoadType]", "", "", "", "Exception while loading alternate service for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadAlternateServiceLoadType", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadAlternateServiceLoadType", "Exiting alternateServiceTypeList:" + alternateServiceTypeList);
        }// end of finally
    }

    /**
     * Method used to load the instance loads details.
     * 
     * @return ArrayList list
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadInstanceLoadDetails(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadInstanceLoadDetails", "Entered ");

        PreparedStatement pstmtSelect = null;
        ArrayList list = new ArrayList();
        String instanceType = Constants.getProperty("INSTANCE_RUNNING_ON");
        String intType = null;
        ResultSet rs = null;
        try {
            InstanceLoadVO instanceLoadVO = null;
            if ("DR".equalsIgnoreCase(instanceType)) {
                intType = "('D','C')";
            } else if ("PR".equalsIgnoreCase(instanceType)) {
                intType = "('P','C')";
            }
            // Changed by Dhiraj on 01/03/2007 to get module.
            // Changed for not loading the Instance Type DUMMY(used for Apache).
            // So only WEB,SMS and OAM instance type will be available for
            // monitoring & not DUMMY Instance(used for Apache).
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT ILOAD.instance_id, ILOAD.instance_name, LK.lookup_name,");
            selectQueryBuff.append(" ILOAD.ip ,ILOAD.port,instance_type, ILOAD.instance_load, ILOAD.instance_tps, ILOAD.request_time_out, ILOAD.load_type_tps,");
            selectQueryBuff.append(" ILOAD.max_allowed_load, ILOAD.max_allowed_tps,ILOAD.module,ILOAD.show_oam_logs,ILOAD.show_smsc_stat,ILOAD.is_dr,ILOAD.authentication_pass FROM instance_load ILOAD, lookups LK WHERE current_status <> 'N'");
            selectQueryBuff.append(" AND LK.lookup_code = ILOAD.current_status AND LK.lookup_type='STAT' AND ILOAD.instance_type<>'DUMMY'");
            selectQueryBuff.append(" AND ILOAD.is_dr IN " + intType + "");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadInstanceLoadDetails", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                instanceLoadVO = new InstanceLoadVO();
                instanceLoadVO.setInstanceID(rs.getString("instance_id"));
                instanceLoadVO.setInstanceName(rs.getString("instance_name"));
                instanceLoadVO.setCurrentStatusDesc(rs.getString("lookup_name"));
                instanceLoadVO.setHostAddress(rs.getString("ip"));
                instanceLoadVO.setHostPort(rs.getString("port"));
                instanceLoadVO.setInstanceType(rs.getString("instance_type"));
                instanceLoadVO.setTransactionLoad(rs.getLong("instance_load"));
                instanceLoadVO.setDefinedTPS(rs.getLong("instance_tps"));
                instanceLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                instanceLoadVO.setLoadTypeTps(rs.getString("load_type_tps"));
                instanceLoadVO.setMaxAllowedLoad(rs.getLong("max_allowed_load"));
                instanceLoadVO.setMaxAllowedTps(rs.getLong("max_allowed_tps"));
                instanceLoadVO.setModule(rs.getString("module"));
                instanceLoadVO.setShowOamlogs(rs.getString("show_oam_logs"));// based
                                                                             // on
                                                                             // this
                                                                             // flag
                                                                             // OAM
                                                                             // link
                                                                             // will
                                                                             // be
                                                                             // shown
                                                                             // on
                                                                             // Monitor
                                                                             // server
                                                                             // screen
                                                                             // for
                                                                             // given
                                                                             // Instance.
                instanceLoadVO.setShowSmscStatus(rs.getString("show_smsc_stat"));// based
                                                                                 // on
                                                                                 // this
                                                                                 // flag
                                                                                 // SMSC
                                                                                 // link
                                                                                 // will
                                                                                 // be
                                                                                 // shown
                                                                                 // on
                                                                                 // Monitor
                                                                                 // server
                                                                                 // screen
                                                                                 // for
                                                                                 // given
                                                                                 // Instance.
                instanceLoadVO.setIsDR(rs.getString("is_dr"));
                instanceLoadVO.setAuthPass(rs.getString("authentication_pass"));
                list.add(instanceLoadVO);

            }// end while

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadInstanceLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadInstanceLoadDetails]", "", "", "", "Exception while loading instances  Getting =" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadInstanceLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadInstanceLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadInstanceLoadDetails]", "", "", "", "Exception while loading instances  Getting =" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadInstanceLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("loadInstanceLoadDetails", "Exiting instanceLoadList.size:" + list.size());
        }// end of finally

        return list;
    }

    public void updateInstanceLoad(Connection p_con, InstanceLoadVO p_instanceLoadVO, String p_instanceID) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("updateInstanceLoad", "Entered p_instanceID:" + p_instanceID);
        PreparedStatement pstmtSelect = null;
        java.util.Date newDate = new Date();
        Timestamp time = new Timestamp(newDate.getTime());
        ResultSet rs = null;
        try {
            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT instance_id, instance_name, current_status, ip, port, instance_load, instance_tps, "
             * );
             * selectQueryBuff.append(
             * " instance_load max_trans_load,instance_tps max_trans_tps,request_time_out, instance_type, load_type_tps"
             * );
             * selectQueryBuff.append(
             * " FROM instance_load  WHERE   instance_id=? AND instance_type=? "
             * );
             */

            // Changed by Dhiraj on 01/03/2007 to get module.
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT instance_id, instance_name, current_status, ip, port, instance_load, instance_tps, ");
            selectQueryBuff.append(" instance_load max_trans_load,instance_tps max_trans_tps,request_time_out, instance_type, load_type_tps ,module, is_dr ");
            selectQueryBuff.append(" FROM instance_load  WHERE   instance_id=? AND instance_type=? ");

            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT tload.instance_id instance_id,iload.instance_name instance_name,iload.current_status current_status,iload.ip ip,iload.port port,iload.instance_load instance_load, "
             * );
             * selectQueryBuff.append(
             * " iload.request_time_out request_time_out,iload.instance_tps instance_tps,sum(tload.max_transaction_load) max_trans_load,sum(tload.transaction_tps) max_trans_tps "
             * );
             * selectQueryBuff.append(
             * " FROM transaction_load tload,instance_load iload ");
             * selectQueryBuff.append(
             * " WHERE tload.instance_id=? AND tload.instance_id=iload.instance_id "
             * );
             * selectQueryBuff.append(
             * " GROUP BY tload.instance_id,iload.instance_name,iload.current_status,iload.ip,iload.port, "
             * );
             * selectQueryBuff.append(
             * " iload.instance_load,iload.request_time_out,iload.instance_tps "
             * );
             */

            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT instance_id, instance_name,current_status,ip,port,instance_txn_load,request_time_out,instance_tps "
             * );
             * selectQueryBuff.append(" FROM instance_load WHERE instance_id= ? "
             * );
             */String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateInstanceLoad", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            pstmtSelect.setString(2, PretupsI.REQUEST_SOURCE_TYPE_SMS);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                p_instanceLoadVO.setInstanceID(rs.getString("instance_id"));
                p_instanceLoadVO.setInstanceName(rs.getString("instance_name"));
                p_instanceLoadVO.setCurrentStatus(rs.getString("current_status"));
                p_instanceLoadVO.setHostAddress(rs.getString("ip"));
                p_instanceLoadVO.setHostPort(rs.getString("port"));
                p_instanceLoadVO.setDefinedTransactionLoad(rs.getLong("instance_load"));
                p_instanceLoadVO.setTransactionLoad(rs.getLong("max_trans_load"));
                p_instanceLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                p_instanceLoadVO.setDefinedTPS(rs.getLong("instance_tps"));
                p_instanceLoadVO.setDefualtTPS(rs.getLong("max_trans_tps"));
                p_instanceLoadVO.setCurrentTPS(rs.getLong("max_trans_tps"));
                if (TypesI.YES.equals(rs.getString("load_type_tps")))
                    p_instanceLoadVO.setLoadType(LoadControllerI.LOAD_CONTROLLER_TPS_TYPE);
                else
                    p_instanceLoadVO.setLoadType(LoadControllerI.LOAD_CONTROLLER_TXN_TYPE);
                p_instanceLoadVO.setModule(rs.getString("module"));
                p_instanceLoadVO.setIsDR(rs.getString("is_dr"));
                if ((LoadControllerI.STATUS_N).equalsIgnoreCase(rs.getString("current_status")))
                    p_instanceLoadVO.setInstanceLoadStatus(false);

                if (_log.isDebugEnabled())
                    _log.debug("updateInstanceLoad", "Values loaded for instance_id=" + p_instanceID + " p_instanceLoadVO=" + p_instanceLoadVO.toString());
            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateInstanceLoad", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateInstanceLoad]", "", "", "", "SQL Exception while loading load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateInstanceLoad", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("updateInstanceLoad", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateInstanceLoad]", "", "", "", "Exception while loading load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateInstanceLoad", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateInstanceLoad", "Exiting ");
        }// end of finally
    }

    public void updateNetworkLoadDetails(Connection p_con, NetworkLoadVO p_networkLoadVO, String p_instanceID, String p_networkID, String p_instanceloadType) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("updateNetworkLoadDetails", "Entered p_instanceID:" + p_instanceID + " p_instanceloadType=" + p_instanceloadType);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            HashMap hashMap = LoadControllerCache.getNetworkLoadObjectMap();

            StringBuffer selectQueryBuff = new StringBuffer(" SELECT network_code, network_load, request_time_out, min_process_time, network_tps, ");
            selectQueryBuff.append(" network_load max_trans_load,network_tps max_trans_tps,c2s_instance_id,p2p_instance_id ");
            selectQueryBuff.append(" FROM network_load WHERE instance_id=? AND network_code=? ");
            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT nload.network_code network_code,nload.network_load network_load,nload.request_time_out request_time_out,nload.network_tps network_tps, "
             * );
             * selectQueryBuff.append(
             * " sum(tload.max_transaction_load) max_trans_load,sum(tload.transaction_tps) max_trans_tps "
             * );
             * selectQueryBuff.append(
             * " FROM transaction_load tload,network_load nload ");
             * selectQueryBuff.append(
             * " WHERE tload.instance_id=? AND nload.network_code=? AND tload.instance_id=nload.instance_id "
             * );
             * selectQueryBuff.append(
             * " GROUP BY nload.network_code,nload.network_load,nload.request_time_out,nload.network_tps "
             * );
             */

            /*
             * StringBuffer selectQueryBuff=new StringBuffer(
             * " SELECT instance_id, network_code,network_load,instance_tps,request_time_out "
             * );
             * selectQueryBuff.append(" FROM network_load WHERE instance_id= ? ")
             * ;
             */
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateNetworkLoadDetails", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            pstmtSelect.setString(2, p_networkID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                p_networkLoadVO.setInstanceID(p_instanceID);
                p_networkLoadVO.setNetworkCode(rs.getString("network_code"));
                p_networkLoadVO.setDefinedTransactionLoad(rs.getLong("network_load"));
                p_networkLoadVO.setTransactionLoad(rs.getLong("max_trans_load"));
                p_networkLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                p_networkLoadVO.setDefinedTPS(rs.getLong("network_tps"));
                p_networkLoadVO.setDefualtTPS(rs.getLong("max_trans_tps"));
                p_networkLoadVO.setCurrentTPS(rs.getLong("max_trans_tps"));
                p_networkLoadVO.setLoadType(p_instanceloadType);

                // Added on 20/07/06 for handling Web recharge request for C2S
                // and P2P in case different servers are there
                p_networkLoadVO.setC2sInstanceID(rs.getString("c2s_instance_id"));
                p_networkLoadVO.setP2pInstanceID(rs.getString("p2p_instance_id"));

                if (_log.isDebugEnabled())
                    _log.debug("updateNetworkLoadDetails", "Values loaded for network code=" + p_networkLoadVO.getNetworkCode() + " networkLoadVO=" + p_networkLoadVO.toString());
            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateNetworkLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateNetworkLoadDetails]", "", "", "", "Exception while loading network load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateNetworkLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("updateNetworkLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateNetworkLoadDetails]", "", "", "", "Exception while loading network load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateNetworkLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateNetworkLoadDetails", "Exiting ");
        }// end of finally
    }

    public void updateInterfaceLoadDetails(Connection p_con, InterfaceLoadVO p_interfaceLoadVO, String p_instanceID, String p_networkID, String p_interfaceID, String p_instanceloadType) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("updateInterfaceLoadDetails", "Entered p_instanceID:" + p_instanceID + " p_instanceloadType=" + p_instanceloadType);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            HashMap hashMap = LoadControllerCache.getInterfaceLoadObjectMap();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT tload.network_code network_code,iload.interface_id interface_id,iload.request_time_out request_time_out, iload.queue_size queue_size,iload.queue_time_out queue_time_out, ");
            selectQueryBuff.append(" iload.next_check_que_req_sec next_check_que_req_sec,sum(tload.max_transaction_load) max_trans_load,sum(tload.transaction_tps) max_trans_tps ");
            selectQueryBuff.append(" FROM transaction_load tload,interface_network_mapping iload ");
            selectQueryBuff.append(" WHERE tload.instance_id=? AND tload.network_code=? AND iload.interface_id=? AND  tload.network_code=iload.network_code AND tload.interface_id=iload.interface_id ");
            selectQueryBuff.append(" GROUP BY tload.network_code,iload.interface_id,iload.request_time_out, iload.queue_size,iload.queue_time_out,iload.next_check_que_req_sec ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateInterfaceLoadDetails", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            pstmtSelect.setString(2, p_networkID);
            pstmtSelect.setString(3, p_interfaceID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                p_interfaceLoadVO.setInstanceID(p_instanceID);
                p_interfaceLoadVO.setNetworkCode(rs.getString("network_code"));
                p_interfaceLoadVO.setInterfaceID(rs.getString("interface_id"));
                p_interfaceLoadVO.setTransactionLoad(rs.getLong("max_trans_load"));
                p_interfaceLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                p_interfaceLoadVO.setDefualtTPS(rs.getLong("max_trans_tps"));
                p_interfaceLoadVO.setCurrentTPS(rs.getLong("max_trans_tps"));
                p_interfaceLoadVO.setQueueSize(rs.getLong("queue_size"));
                p_interfaceLoadVO.setQueueTimeOut(rs.getLong("queue_time_out"));
                p_interfaceLoadVO.setNextQueueCheckCaseAfterSec(rs.getLong("next_check_que_req_sec"));
                p_interfaceLoadVO.setLoadType(p_instanceloadType);

                if (!hashMap.containsKey(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id")))
                    hashMap.put(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id"), new InterfaceLoadController());

                if (_log.isDebugEnabled())
                    _log.debug("updateInterfaceLoadDetails", "Values loaded for Interface code=" + p_interfaceID);
            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateInterfaceLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateInterfaceLoadDetails]", "", "", "", "Exception while loading interface load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateInterfaceLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("updateInterfaceLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateInterfaceLoadDetails]", "", "", "", "Exception while loading interface load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateInterfaceLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateInterfaceLoadDetails", "Exiting ");
        }// end of finally
    }

    public void updateTransactionLoadDetails(Connection p_con, TransactionLoadVO p_transactionLoadVO, String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType, String p_instanceloadType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateTransactionLoadDetails", "Entered p_instanceID:" + p_instanceID + " p_networkID=" + p_networkID + " p_interfaceID=" + p_interfaceID + " p_serviceType=" + p_serviceType + "p_instanceloadType=" + p_instanceloadType);
        PreparedStatement pstmtSelect = null;
        Hashtable transactionLoadTable = new Hashtable();
        ResultSet rs = null;
        try {
            HashMap hashMap = LoadControllerCache.getTransactionLoadObjectMap();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT tload.network_code network_code,tload.interface_id interface_id,tload.service_type service_type,tload.request_time_out request_time_out, ");
            selectQueryBuff.append(" tload.min_service_timeout min_service_timeout,tload.over_flow_count over_flow_count,tload.max_transaction_load max_transaction_load,tload.transaction_tps transaction_tps,tload.next_check_timeout_sec next_check_timeout_sec ");
            selectQueryBuff.append(" FROM transaction_load tload ");
            selectQueryBuff.append(" WHERE tload.instance_id=? AND tload.network_code=? AND tload.interface_id=? AND tload.service_type=? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateTransactionLoadDetails", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            pstmtSelect.setString(2, p_networkID);
            pstmtSelect.setString(3, p_interfaceID);
            pstmtSelect.setString(4, p_serviceType);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                p_transactionLoadVO.setInstanceID(p_instanceID);
                p_transactionLoadVO.setNetworkCode(rs.getString("network_code"));
                p_transactionLoadVO.setInterfaceID(rs.getString("interface_id"));
                p_transactionLoadVO.setServiceType(rs.getString("service_type"));
                p_transactionLoadVO.setDefinedTransactionLoad(rs.getLong("max_transaction_load"));
                p_transactionLoadVO.setTransactionLoad(rs.getLong("max_transaction_load"));
                p_transactionLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                p_transactionLoadVO.setDefinedTPS(rs.getLong("transaction_tps"));
                p_transactionLoadVO.setDefualtTPS(rs.getLong("transaction_tps"));
                p_transactionLoadVO.setCurrentTPS(rs.getLong("transaction_tps"));
                p_transactionLoadVO.setMinimumServiceTime(rs.getLong("min_service_timeout"));
                p_transactionLoadVO.setDefinedOverFlowCount(rs.getInt("over_flow_count"));
                p_transactionLoadVO.setNextCheckTimeOutCaseAfterSec(rs.getLong("next_check_timeout_sec"));
                p_transactionLoadVO.setLoadType(p_instanceloadType);
                updateAlternateServiceLoadType(p_con, p_transactionLoadVO, p_instanceloadType, p_instanceID, p_transactionLoadVO.getNetworkCode(), p_transactionLoadVO.getInterfaceID(), p_transactionLoadVO.getServiceType());
                if (!hashMap.containsKey(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id") + "_" + rs.getString("service_type")))
                    hashMap.put(p_instanceID + "_" + rs.getString("network_code") + "_" + rs.getString("interface_id") + "_" + rs.getString("service_type"), new TransactionLoadController());
                if (_log.isDebugEnabled())
                    _log.debug("updateTransactionLoadDetails", "Values loaded for Interface code=" + p_transactionLoadVO.getInterfaceID() + " Service=" + p_transactionLoadVO.getServiceType() + " p_transactionLoadVO=" + p_transactionLoadVO.toString());
            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateTransactionLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateTransactionLoadDetails]", "", "", "", "Exception while loading transaction load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateTransactionLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("updateTransactionLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateTransactionLoadDetails]", "", "", "", "Exception while loading transaction load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateTransactionLoadDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateTransactionLoadDetails", "Exiting ");
        }// end of finally
    }

    private void updateAlternateServiceLoadType(Connection p_con, TransactionLoadVO p_transactionLoadVO, String p_instanceloadType, String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateAlternateServiceLoadType", "Entered p_instanceID:" + p_instanceID + " p_instanceloadType=" + p_instanceloadType);
        PreparedStatement pstmtSelect = null;
        ArrayList alternateServiceTypeList = null;
        TransactionLoadVO alternateTransactionLoadVO = null;
        ResultSet rs = null;
        try {
            alternateServiceTypeList = p_transactionLoadVO.getAlternateServiceLoadType();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT tload.service_type service_type,tload.request_time_out request_time_out, ");
            selectQueryBuff.append(" tload.min_service_timeout min_service_timeout,tload.over_flow_count over_flow_count,tload.max_transaction_load max_transaction_load,tload.transaction_tps transaction_tps,tload.next_check_timeout_sec next_check_timeout_sec ");
            selectQueryBuff.append(" FROM transaction_load tload ");
            selectQueryBuff.append(" WHERE tload.instance_id=? AND tload.network_code=? AND tload.interface_id=? AND tload.service_type<>? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateAlternateServiceLoadType", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            pstmtSelect.setString(2, p_networkID);
            pstmtSelect.setString(3, p_interfaceID);
            pstmtSelect.setString(4, p_serviceType);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < alternateServiceTypeList.size(); i++) {
                    alternateTransactionLoadVO = (TransactionLoadVO) alternateServiceTypeList.get(i);
                    if (alternateTransactionLoadVO.getServiceType().equals(rs.getString("service_type"))) {
                        alternateTransactionLoadVO.setDefinedTransactionLoad(rs.getLong("max_transaction_load"));
                        alternateTransactionLoadVO.setTransactionLoad(rs.getLong("max_transaction_load"));
                        alternateTransactionLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                        alternateTransactionLoadVO.setDefinedTPS(rs.getLong("transaction_tps"));
                        alternateTransactionLoadVO.setDefualtTPS(rs.getLong("transaction_tps"));
                        alternateTransactionLoadVO.setCurrentTPS(rs.getLong("transaction_tps"));
                        alternateTransactionLoadVO.setMinimumServiceTime(rs.getLong("min_service_timeout"));
                        alternateTransactionLoadVO.setDefinedOverFlowCount(rs.getInt("over_flow_count"));
                        alternateTransactionLoadVO.setNextCheckTimeOutCaseAfterSec(rs.getLong("next_check_timeout_sec"));
                        alternateTransactionLoadVO.setLoadType(p_instanceloadType);
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("updateAlternateServiceLoadType", "Values loaded for Alternate Service Type for Interface code=" + p_interfaceID + " Service=" + p_serviceType + " alternateServiceTypeList=" + alternateServiceTypeList.size());
            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateAlternateServiceLoadType", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateAlternateServiceLoadType]", "", "", "", "Exception while loading alternate service for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateAlternateServiceLoadType", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("updateAlternateServiceLoadType", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[updateAlternateServiceLoadType]", "", "", "", "Exception while loading alternate service for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "updateAlternateServiceLoadType", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadAlternateServiceLoadType", "Exiting ");
        }// end of finally
    }

    public Hashtable loadInstanceLoadDetailsForNetwork() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadInstanceLoadDetails", "Entered ");

        PreparedStatement pstmtSelect = null;
        Hashtable instanceLoadTable = new Hashtable();
        ResultSet rs = null;
        Connection con = null;
        try {
            InstanceLoadVO instanceLoadVO = null;

            con = OracleUtil.getConnection();

            // Changed by Dhiraj on 01/03/2007 to get module.
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT instload.instance_id, instload.instance_name, ");
            selectQueryBuff.append(" instload.ip , instload.port,instload.instance_type , instload.module, netload.network_code, instload.is_dr ");
            selectQueryBuff.append(" FROM instance_load instload,network_load netload WHERE current_status <> 'N' AND instload.instance_id=netload.instance_id");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadInstanceLoadDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                instanceLoadVO = new InstanceLoadVO();
                instanceLoadVO.setInstanceID(rs.getString("instance_id"));
                instanceLoadVO.setInstanceName(rs.getString("instance_name"));
                instanceLoadVO.setHostAddress(rs.getString("ip"));
                instanceLoadVO.setHostPort(rs.getString("port"));
                instanceLoadVO.setInstanceType(rs.getString("instance_type"));
                instanceLoadVO.setModule(rs.getString("module"));
                instanceLoadVO.setIsDR(rs.getString("is_dr"));
                // Changed the key on 20/07/06 for handling different Servers
                // for Web C2S recharge and P2P requests
                instanceLoadTable.put(rs.getString("instance_id") + "_" + rs.getString("network_code") + "_" + rs.getString("instance_type"), instanceLoadVO);
            }// end while

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadInstanceLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadInstanceLoadDetailsForNetwork]", "", "", "", "Exception while loading instances  Getting =" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadInstanceLoadDetailsForNetwork", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadInstanceLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadInstanceLoadDetailsForNetwork]", "", "", "", "Exception while loading instances  Getting =" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadInstanceLoadDetailsForNetwork", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("loadInstanceLoadDetailsForNetwork", "Exiting instanceLoadTable.size:" + instanceLoadTable.size());
        }// end of finally

        return instanceLoadTable;
    }

    /**
     * Method to load network and service wise load counters
     * OTHERS will be used if some error comes before validating gateway ,
     * network or service
     * 
     * @param p_instanceID
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public Hashtable loadNetworkSeriveDetails(String p_instanceID) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkSeriveDetails", "Entered p_instanceID:" + p_instanceID);
        PreparedStatement pstmtSelect = null;
        Hashtable networkServiceTable = new Hashtable();
        Connection con = null;
        ResultSet rs = null;
        java.util.Date newDate = new Date();
        Timestamp time = new Timestamp(newDate.getTime());

        try {
            con = OracleUtil.getSingleConnection();
            NetworkServiceLoadVO networkServiceLoadVO = null;

            // Changed by Dhiraj on 01/03/2007 to get specific modules supported
            // by an instance
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT M.module,nvl(M.seq_no,'9999') SEQ_NO,M.reqtype reqtype, M.stype stype,M.name name, N.network_code network_code");
            selectQueryBuff.append(" FROM instance_load il, networks N,network_load NL,(SELECT REQ_INTERFACE_TYPE REQTYPE, SK.SERVICE_TYPE STYPE,sty.name NAME,sty.module MODULE,sty.seq_no ");
            selectQueryBuff.append(" FROM SERVICE_KEYWORDS SK,SERVICE_TYPE STY WHERE SK.service_type=STY.service_type AND STY.STATUS<>'N'");
            selectQueryBuff.append(" GROUP BY sty.module,sty.seq_no,SK.SERVICE_TYPE,REQ_INTERFACE_TYPE, sty.name) M WHERE N.network_code=NL.network_code AND IL.instance_id=?");
            selectQueryBuff.append(" AND NL.instance_id=il.instance_id AND M.MODULE=decode(il.module,'ALL',M.module,il.module) GROUP BY N.network_code,M.module,M.seq_no,M.name,reqtype, stype");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkSeriveDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                networkServiceLoadVO = new NetworkServiceLoadVO();
                networkServiceLoadVO.setInstanceID(p_instanceID);
                networkServiceLoadVO.setNetworkCode(rs.getString("network_code"));
                networkServiceLoadVO.setServiceType(rs.getString("stype"));
                networkServiceLoadVO.setGatewayType(rs.getString("reqtype"));
                networkServiceLoadVO.setServiceName(rs.getString("name"));
                networkServiceLoadVO.setModuleCode(rs.getString("module"));
                networkServiceLoadVO.setSeqNo(rs.getInt("seq_no"));
                networkServiceLoadVO.setLastInitializationTime(newDate);
                networkServiceLoadVO.setLastReceievedTime(time);
                networkServiceLoadVO.setLastRequestID("0");
                networkServiceTable.put(p_instanceID + "_" + rs.getString("reqtype") + "_" + rs.getString("network_code") + "_" + rs.getString("stype"), networkServiceLoadVO);
                if (_log.isDebugEnabled())
                    _log.debug("loadNetworkSeriveDetails", "Values loaded for network code=" + networkServiceLoadVO.getNetworkCode() + " networkServiceLoadVO=" + networkServiceLoadVO.toString());
            }// end while
            networkServiceLoadVO = new NetworkServiceLoadVO();
            networkServiceLoadVO.setInstanceID(p_instanceID);
            networkServiceLoadVO.setNetworkCode("N.A");
            networkServiceLoadVO.setServiceType("N.A");
            networkServiceLoadVO.setGatewayType("N.A");
            networkServiceLoadVO.setServiceName("N.A");
            networkServiceLoadVO.setLastInitializationTime(newDate);
            networkServiceLoadVO.setLastReceievedTime(time);
            networkServiceLoadVO.setLastRequestID("0");
            networkServiceTable.put(p_instanceID + "_OTHERS", networkServiceLoadVO);
            return networkServiceTable;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadNetworkSeriveDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadNetworkSeriveDetails]", "", "", "", "Exception while loading network service load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadNetworkSeriveDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadNetworkLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadNetworkSeriveDetails]", "", "", "", "Exception while loading network service load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadNetworkSeriveDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkSeriveDetails", "Exiting networkServiceTable.size:" + networkServiceTable.size());
        }// end of finally
    }

    /**
     * Method to load the Network loads details on the basis of instance ID
     * 
     * @author amit.singh
     * @param p_instanceID
     * @return ArrayList networkLoadLists
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadNetworkLoadDetails(Connection p_con, String p_instanceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkLoadDetails", "Entered p_instanceID:" + p_instanceID);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList networkLoadList = null;

        try {
            NetworkLoadVO networkLoadVO = null;
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT NLOAD.instance_id, NLOAD.network_code, NLOAD.network_load,");
            selectQueryBuff.append(" NLOAD.request_time_out, NLOAD.min_process_time, NLOAD.network_tps ,NLOAD.c2s_instance_id,NLOAD.p2p_instance_id ");
            selectQueryBuff.append(" FROM network_load NLOAD");
            selectQueryBuff.append(" WHERE NLOAD.instance_id = ?");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkLoadDetails", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_instanceID);
            rs = pstmtSelect.executeQuery();
            networkLoadList = new ArrayList();
            while (rs.next()) {
                networkLoadVO = new NetworkLoadVO();
                networkLoadVO.setInstanceID(rs.getString("instance_id"));
                networkLoadVO.setNetworkCode(rs.getString("network_code"));
                networkLoadVO.setTransactionLoad(rs.getLong("network_load"));
                networkLoadVO.setTransactionLoadStr(String.valueOf(rs.getLong("network_load")));
                networkLoadVO.setRequestTimeoutSec(rs.getLong("request_time_out"));
                networkLoadVO.setRequestTimeOutSecStr(String.valueOf(rs.getLong("request_time_out")));
                networkLoadVO.setDefinedTPS(rs.getLong("network_tps"));
                networkLoadVO.setDefinedTPSStr(String.valueOf(rs.getLong("network_tps")));
                networkLoadVO.setMinimumProcessTime(rs.getLong("min_process_time"));
                networkLoadVO.setMinimumProcessTimeStr(String.valueOf(rs.getLong("min_process_time")));

                // Added on 20/07/06 for handling Web recharge request for C2S
                // and P2P in case different servers are there
                networkLoadVO.setC2sInstanceID(rs.getString("c2s_instance_id"));
                networkLoadVO.setP2pInstanceID(rs.getString("p2p_instance_id"));

                networkLoadList.add(networkLoadVO);
            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadNetworkLoadDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadNetworkLoadDetails]", "", "", "", "Exception while loading network load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadNetworkLoadDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadNetworkLoadDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadNetworkLoadDetails]", "", "", "", "Exception while loading network load for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadNetworkLoadDetails", "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkLoadDetails", "Exiting networkLoadList.size:" + networkLoadList.size());
        }// end of finally
        return networkLoadList;
    }

    /**
     * Method: modifyInstanceLoad
     * Method for Updating instance load Detail.
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_instanceLoadVO
     *            InstanceLoadVO
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int modifyInstanceLoad(Connection p_con, InstanceLoadVO p_instanceLoadVO) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        if (_log.isDebugEnabled())
            _log.debug("modifyInstanceLoad", "Entered: p_instanceLoadVO= " + p_instanceLoadVO);
        try {
            StringBuffer strBuff = new StringBuffer("UPDATE instance_load SET instance_name = ?, ip = ? , port = ?, instance_load = ?,");
            strBuff.append(" instance_tps = ?, request_time_out = ?, modified_on = ?, modified_by = ?, load_type_tps = ?,show_smsc_stat=?,show_oam_logs=? WHERE instance_id = ?");

            String updateQuery = strBuff.toString();

            if (_log.isDebugEnabled())
                _log.debug("modifyInstanceLoad", "Query sqlUpdate:" + updateQuery);

            // commented for DB2 psmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(updateQuery);
            psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            // commented for DB2 psmtUpdate.setFormOfUse(1,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(1, p_instanceLoadVO.getInstanceName());
            psmtUpdate.setString(2, p_instanceLoadVO.getHostAddress());
            psmtUpdate.setString(3, p_instanceLoadVO.getHostPort());
            psmtUpdate.setLong(4, p_instanceLoadVO.getTransactionLoad());
            psmtUpdate.setLong(5, p_instanceLoadVO.getDefinedTPS());
            psmtUpdate.setLong(6, p_instanceLoadVO.getRequestTimeoutSec());
            psmtUpdate.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_instanceLoadVO.getModifiedOn()));
            psmtUpdate.setString(8, p_instanceLoadVO.getModifiedBy());
            psmtUpdate.setString(9, p_instanceLoadVO.getLoadTypeTps());
            psmtUpdate.setString(10, p_instanceLoadVO.getShowSmscStatus());
            psmtUpdate.setString(11, p_instanceLoadVO.getShowOamlogs());
            psmtUpdate.setString(12, p_instanceLoadVO.getInstanceID());

            updateCount = psmtUpdate.executeUpdate();

        } // end of try
        catch (SQLException sqle) {
            _log.error("modifyInstanceLoad", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[modifyInstanceLoad]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "modifyInstanceLoad", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("modifyInstanceLoad", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[modifyInstanceLoad]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "modifyInstanceLoad", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("modifyInstanceLoad", "Exiting: updateCount=" + updateCount);
        } // end of finally

        return updateCount;
    }

    /**
     * Method: modifyNetworkLoad
     * Method for Updating network load Detail.
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_networksOfInstanceList
     *            ArrayList
     * @param p_modifiedOn
     *            Date
     * @param p_modifiedBy
     *            String
     * @return isNetworkLoadUpdate boolean
     * @throws BTSLBaseException
     */
    public boolean modifyNetworkLoad(Connection p_con, ArrayList p_networksOfInstanceList, Date p_modifiedOn, String p_modifiedBy) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        NetworkLoadVO networkLoadVO = null;
        boolean isNetworkLoadUpdate = false;

        if (_log.isDebugEnabled())
            _log.debug("modifyNetworkLoad", "Entered: p_networkLoadVO = " + p_networksOfInstanceList);

        try {
            StringBuffer strBuff = new StringBuffer("");
            strBuff.append("UPDATE network_load SET network_load = ?, request_time_out = ? , min_process_time = ?,");
            strBuff.append(" network_tps = ?, modified_on = ?, modified_by = ? ,c2s_instance_id=? ,p2p_instance_id=? WHERE instance_id = ? AND network_code = ?");

            String updateQuery = strBuff.toString();

            if (_log.isDebugEnabled())
                _log.debug("modifyNetworkLoad", "Query sqlUpdate:" + updateQuery);

            psmtUpdate = p_con.prepareStatement(updateQuery);

            for (int i = 0, j = p_networksOfInstanceList.size(); i < j; i++) {
                networkLoadVO = (NetworkLoadVO) p_networksOfInstanceList.get(i);

                psmtUpdate.setLong(1, networkLoadVO.getTransactionLoad());
                psmtUpdate.setLong(2, networkLoadVO.getRequestTimeoutSec());
                psmtUpdate.setLong(3, networkLoadVO.getMinimumProcessTime());
                psmtUpdate.setLong(4, networkLoadVO.getDefinedTPS());
                psmtUpdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                psmtUpdate.setString(6, p_modifiedBy);

                // Added on 20/07/06 for handling Web recharge request for C2S
                // and P2P in case different servers are there
                psmtUpdate.setString(7, networkLoadVO.getC2sInstanceID());
                psmtUpdate.setString(8, networkLoadVO.getP2pInstanceID());

                psmtUpdate.setString(9, networkLoadVO.getInstanceID());
                psmtUpdate.setString(10, networkLoadVO.getNetworkCode());

                updateCount = psmtUpdate.executeUpdate();
                if (updateCount <= 0) {
                    isNetworkLoadUpdate = false;
                    break;
                } else
                    isNetworkLoadUpdate = true;

                psmtUpdate.clearParameters();
            }

        } // end of try
        catch (SQLException sqle) {
            _log.error("modifyNetworkLoad", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[modifyNetworkLoad]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "modifyNetworkLoad", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("modifyNetworkLoad", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[modifyNetworkLoad]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "modifyNetworkLoad", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("modifyNetworkLoad", "Exiting: isNetworkLoadUpdate=" + isNetworkLoadUpdate);
        } // end of finally
        return isNetworkLoadUpdate;
    }

    /**
     * Method to load network and service wise load counters
     * OTHERS will be used if some error comes before validating gateway ,
     * network or service
     * 
     * @param p_instanceID
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public Hashtable loadNetworkServiceHourlyDetails(String p_instanceID) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkServiceHourlyDetails", "Entered p_instanceID:" + p_instanceID);
        PreparedStatement pstmtSelect = null;
        Hashtable networkServiceHourlyTable = new Hashtable();
        Connection con = null;
        ResultSet rs = null;
        java.util.Date newDate = new Date();
        Timestamp time = new Timestamp(newDate.getTime());

        try {
            con = OracleUtil.getSingleConnection();
            NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO = null;
            NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO1 = null;

            StringBuffer selectQueryBuff = new StringBuffer(" SELECT M.module,nvl(M.seq_no,'9999') SEQ_NO,M.reqtype reqtype, M.stype stype,M.name name, N.network_code network_code");
            selectQueryBuff.append(" FROM instance_load il, networks N,network_load NL,(SELECT REQ_INTERFACE_TYPE REQTYPE, SK.SERVICE_TYPE STYPE,sty.name NAME,sty.module MODULE,sty.seq_no ");
            selectQueryBuff.append(" FROM SERVICE_KEYWORDS SK,SERVICE_TYPE STY,NETWORK_SERVICES NS ,SERVICE_TYPE_SELECTOR_MAPPING stsm  WHERE SK.service_type=STY.service_type AND STY.STATUS<>'N'");
            selectQueryBuff.append(" AND STY.service_type=ns.service_type and ns.status <> 'N' and sk.status <> 'N' and ns.service_type=stsm.SERVICE_TYPE and sty.module=? ");
            selectQueryBuff.append(" GROUP BY sty.module,sty.seq_no,SK.SERVICE_TYPE,REQ_INTERFACE_TYPE, sty.name) M WHERE N.network_code=NL.network_code AND IL.instance_id=?");
            selectQueryBuff.append(" AND NL.instance_id=il.instance_id AND M.MODULE=decode(il.module,'ALL',M.module,il.module) GROUP BY N.network_code,M.module,M.seq_no,M.name,reqtype, stype");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkServiceHourlyDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.P2P_MODULE);
            pstmtSelect.setString(2, p_instanceID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                networkServiceHourlyLoadVO = new NetworkServiceHourlyLoadVO();
                networkServiceHourlyLoadVO.setInstanceID(p_instanceID);
                networkServiceHourlyLoadVO.setNetworkCode(rs.getString("network_code"));
                networkServiceHourlyLoadVO.setServiceType(rs.getString("stype"));
                networkServiceHourlyLoadVO.setGatewayType(rs.getString("reqtype"));
                networkServiceHourlyLoadVO.setServiceName(rs.getString("name"));
                networkServiceHourlyLoadVO.setModuleCode(rs.getString("module"));
                networkServiceHourlyLoadVO.setSeqNo(rs.getInt("seq_no"));
                networkServiceHourlyLoadVO.setLastInitializationTime(newDate);
                networkServiceHourlyLoadVO.setLastReceievedTime(time);
                networkServiceHourlyLoadVO.setLastRequestID("0");

                for (int i = 0; i < 24; i++) {
                    if (i < 10)
                        networkServiceHourlyTable.put(p_instanceID + "_" + rs.getString("reqtype") + "_" + rs.getString("network_code") + "_" + rs.getString("stype") + "_" + "0" + i, getCopyOfNetworkServiceHourlyLoadVO(networkServiceHourlyLoadVO));
                    else
                        networkServiceHourlyTable.put(p_instanceID + "_" + rs.getString("reqtype") + "_" + rs.getString("network_code") + "_" + rs.getString("stype") + "_" + i, getCopyOfNetworkServiceHourlyLoadVO(networkServiceHourlyLoadVO));
                }

            }// end while
            /*
             * networkServiceHourlyLoadVO = new NetworkServiceHourlyLoadVO();
             * networkServiceHourlyLoadVO.setInstanceID(p_instanceID);
             * networkServiceHourlyLoadVO.setNetworkCode("N.A");
             * networkServiceHourlyLoadVO.setServiceType("N.A");
             * networkServiceHourlyLoadVO.setGatewayType("N.A");
             * networkServiceHourlyLoadVO.setServiceName("N.A");
             * networkServiceHourlyLoadVO.setLastInitializationTime(newDate);
             * networkServiceHourlyLoadVO.setLastReceievedTime(time);
             * networkServiceHourlyLoadVO.setLastRequestID("0");
             * for(int i=0;i<24;i++)
             * {if(i<10)
             * networkServiceHourlyTable.put(p_instanceID+"_OTHERS"+"0"+i,
             * networkServiceHourlyLoadVO);
             * else
             * networkServiceHourlyTable.put(p_instanceID+"_OTHERS"+i,
             * networkServiceHourlyLoadVO);
             * }
             */
            return networkServiceHourlyTable;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadNetworkServiceHourlyDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadNetworkServiceHourlyDetails]", "", "", "", "Exception while loading network service hourly load for instance=" + p_instanceID + " Getting=" + sqle.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadNetworkServiceHourlyDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadNetworkServiceHourlyDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoadControllerDAO[loadNetworkServiceHourlyDetails]", "", "", "", "Exception while loading network service load hourly for instance=" + p_instanceID + " Getting=" + e.getMessage());
            throw new BTSLBaseException("LoadControllerDAO", "loadNetworkServiceHourlyDetails", TypesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadNetworkServiceHourlyDetails", "Exiting networkServiceHourlyTable.size:" + networkServiceHourlyTable.size());
        }// end of finally
    }

    public NetworkServiceHourlyLoadVO getCopyOfNetworkServiceHourlyLoadVO(NetworkServiceHourlyLoadVO p_networkServiceHourlyLoadVO) {
        NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO = new NetworkServiceHourlyLoadVO();
        networkServiceHourlyLoadVO.setInstanceID(p_networkServiceHourlyLoadVO.getInstanceID());
        networkServiceHourlyLoadVO.setServiceName(p_networkServiceHourlyLoadVO.getServiceName());
        networkServiceHourlyLoadVO.setServiceType(p_networkServiceHourlyLoadVO.getServiceType());
        networkServiceHourlyLoadVO.setSuccessCount(p_networkServiceHourlyLoadVO.getSuccessCount());
        networkServiceHourlyLoadVO.setFailCount(p_networkServiceHourlyLoadVO.getFailCount());
        networkServiceHourlyLoadVO.setGatewayType(p_networkServiceHourlyLoadVO.getGatewayType());
        networkServiceHourlyLoadVO.setNetworkCode(p_networkServiceHourlyLoadVO.getNetworkCode());
        networkServiceHourlyLoadVO.setModuleCode(p_networkServiceHourlyLoadVO.getModuleCode());
        networkServiceHourlyLoadVO.setLastReceievedTime(p_networkServiceHourlyLoadVO.getLastReceievedTime());
        networkServiceHourlyLoadVO.setLastInitializationTime(p_networkServiceHourlyLoadVO.getLastInitializationTime());
        return networkServiceHourlyLoadVO;
    }

}