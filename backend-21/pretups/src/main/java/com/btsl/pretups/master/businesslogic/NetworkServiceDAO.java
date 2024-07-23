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
package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
/**
 * 
 * class NetworkServiceDAO
 *
 */

public class NetworkServiceDAO {
    /**
     * Field log.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for NetworkServiceDAO.
     */
    public NetworkServiceDAO() {

    }
/**
 * method loadNetworkServicesList
 * @return
 * @throws BTSLBaseException
 */
    public HashMap loadNetworkServicesList() throws BTSLBaseException {
        final String methodName = "loadNetworkServicesList";
        LogFactory.printLog(methodName, "Entered ", log);
        
         
        HashMap networkServiceMap = new HashMap();
        Connection con = null;
         
        try {
            con = OracleUtil.getSingleConnection();
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT module_code,service_type, sender_network, receiver_network, status, language1_message, language2_message,  ");
            selectQueryBuff.append(" created_by, created_on, modified_by, modified_on FROM network_services  ");
            String selectQuery = selectQueryBuff.toString();
            NetworkServiceVO networkServiceVO = null;
            LogFactory.printLog(methodName, "Select Query= " + selectQuery, log);
            
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
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
        } 
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	OracleUtil.closeQuietly(con);
        	LogFactory.printLog(methodName, "Exiting networkServiceMap.size:" + networkServiceMap.size(), log);
            
        }// end of finally
    }

    /**
     * Method loadNetworkServicesList.
     * 
     * @param conn
     *            Connection
     * @param moduleCode
     *            String
     * @param serviceType
     *            String
     * @param receiverNetwork
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadNetworkServicesList(Connection conn, String moduleCode, String serviceType, String receiverNetwork) throws BTSLBaseException {
        final String methodName = "loadNetworkServicesList";
        LogFactory.printLog(methodName, "Entered:p_receiverNetwork=" + receiverNetwork + ",p_serviceType=" + serviceType + ",p_moduleCode=" + moduleCode, log);
       
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList networkServiceList = null;
        
        NetworkServiceQry networkServiceQry = (NetworkServiceQry)ObjectProducer.getObject(QueryConstants.NW_SERVICE_QRY, QueryConstants.QUERY_PRODUCER);
        String strBuff = networkServiceQry.loadNetworkServicesListQry();
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + strBuff, log);

        try {
            pstmt = conn.prepareStatement(strBuff.toString());
            pstmt.setString(1, receiverNetwork);
            pstmt.setString(2, moduleCode);
            pstmt.setString(3, serviceType);
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
                if (rs.getTimestamp("modified_on1") != null) {
                    networkServiceVO.setLastModifiedTime(rs.getTimestamp("modified_on1").getTime());
                } else {
                    networkServiceVO.setLastModifiedTime(0);
                }
                networkServiceVO.setModuleCode(moduleCode);
                networkServiceVO.setReceiverNetwork(receiverNetwork);
                networkServiceVO.setServiceType(serviceType);
                networkServiceList.add(networkServiceVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[loadNetworkServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception: " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[loadNetworkServicesList]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	if(networkServiceList!=null)
        	{
        	LogFactory.printLog(methodName, "Exiting: networkServiceList size=" + networkServiceList.size(), log);
        	}
        }
        return networkServiceList;
    }

    /**
     * Method updateNetworkServices.
     * This method perform dual activity it may be insert the new record or
     * update the existing record if exist.
     * 
     * @param conn
     *            Connection
     * @param pnetworkServiceVO
     *            NetworkServiceVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateNetworkServices(Connection conn, NetworkServiceVO pnetworkServiceVO) throws BTSLBaseException {
        final String methodName = "updateNetworkServices";
        LogFactory.printLog(methodName, "Entered:p_networkServiceVO=" + pnetworkServiceVO, log);
        
         

       

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

        ArrayList networkServiceVOList = pnetworkServiceVO.getNetworkServicesVOList();
        NetworkServiceVO networkServiceVO = null;
        try( PreparedStatement pstmtSelect = conn.prepareStatement(selectQuery.toString());
        		PreparedStatement pstmtUpdate = conn.prepareStatement(updateQuery.toString());
        		PreparedStatement  pstmtInsert =  conn.prepareStatement(insertQuery.toString());) {
        	LogFactory.printLog(methodName, "selectQUERY=" + selectQuery, log);
        	LogFactory.printLog(methodName, "updateQUERY=" + updateQuery, log);
        	LogFactory.printLog(methodName, "insertQUERY=" + insertQuery, log);
            

           
            if (networkServiceVOList != null && !networkServiceVOList.isEmpty()) {
                for (int i = 0, j = networkServiceVOList.size(); i < j; i++) {
                    networkServiceVO = (NetworkServiceVO) networkServiceVOList.get(i);

                    pstmtSelect.setString(1, networkServiceVO.getModuleCode());
                    pstmtSelect.setString(2, networkServiceVO.getServiceType());
                    pstmtSelect.setString(3, networkServiceVO.getSenderNetwork());
                    pstmtSelect.setString(4, networkServiceVO.getReceiverNetwork());
                    try(ResultSet rs = pstmtSelect.executeQuery();)
                    {

                    pstmtSelect.clearParameters();

                    // if record exist then update its information
                    if (rs.next()) {
                        pstmtUpdate.setString(1, networkServiceVO.getStatus());
                        pstmtUpdate.setString(2, networkServiceVO.getLanguage1Message());

                     

                        pstmtUpdate.setString(3, networkServiceVO.getLanguage2Message());

                        pstmtUpdate.setString(4, pnetworkServiceVO.getModifiedBy());
                        pstmtUpdate.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(pnetworkServiceVO.getModifiedOn()));

                        pstmtUpdate.setString(6, networkServiceVO.getModuleCode());
                        pstmtUpdate.setString(7, networkServiceVO.getServiceType());
                        pstmtUpdate.setString(8, networkServiceVO.getSenderNetwork());
                        pstmtUpdate.setString(9, networkServiceVO.getReceiverNetwork());

                        // for checking that is the record is modified during
                        // the transaction.
                        boolean modified = isRecordModified(conn, networkServiceVO);
                        if (modified) {
                            throw new BTSLBaseException(this,methodName, "error.modify.true");
                        } else {
                            updateCount = pstmtUpdate.executeUpdate();
                        }

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

                        pstmtInsert.setString(7, networkServiceVO.getLanguage2Message());

                        pstmtInsert.setString(8, pnetworkServiceVO.getCreatedBy());
                        pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(pnetworkServiceVO.getCreatedOn()));
                        pstmtInsert.setString(10, pnetworkServiceVO.getModifiedBy());
                        pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(pnetworkServiceVO.getModifiedOn()));
                        updateCount = pstmtInsert.executeUpdate();

                        pstmtInsert.clearParameters();
                    }
                    if (updateCount <= 0) {
                        throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
                    }
                }
            }
        }
        }catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[updateNetworkServices]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[updateNetworkServices]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
            LogFactory.printLog(methodName, "Exiting:return=" + updateCount, log);
            
        }
        return updateCount;
    }

    /**
     * Method isRecordModified.
     * 
     * @param conn
     *            Connection
     * @param pnetworkServiceVO
     *            NetworkServiceVO
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection conn, NetworkServiceVO pnetworkServiceVO) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        LogFactory.printLog(methodName, "Entered:p_networkServiceVO=" + pnetworkServiceVO, log);
       
         
        
        boolean modified = false;
        StringBuilder sqlRecordModified = new StringBuilder();
        try {
            sqlRecordModified.append("SELECT modified_on FROM network_services ");
            sqlRecordModified.append("WHERE module_code=? AND service_type=? AND sender_network=? AND receiver_network=? ");
            LogFactory.printLog(methodName, "QUERY=" + sqlRecordModified, log);
          
            String query = sqlRecordModified.toString();
            try(PreparedStatement pstmtSelect = conn.prepareStatement(query);)
            {
            pstmtSelect.setString(1, pnetworkServiceVO.getModuleCode());
            pstmtSelect.setString(2, pnetworkServiceVO.getServiceType());
            pstmtSelect.setString(3, pnetworkServiceVO.getSenderNetwork());
            pstmtSelect.setString(4, pnetworkServiceVO.getReceiverNetwork());

            Timestamp newlastModified = null;
            if (pnetworkServiceVO.getLastModifiedTime() == 0) {
                return false;
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            if (newlastModified != null && newlastModified.getTime() != pnetworkServiceVO.getLastModifiedTime()) {
                modified = true;
            }
        }
            }
        }// end of try
        catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[isRecordModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServiceDAO[isRecordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName, "Exititng:modified=" + modified, log);
          
        }// end of finally
        return modified;
    }// end recordModified

}
