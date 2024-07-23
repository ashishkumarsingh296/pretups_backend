package com.btsl.pretups.interfaces.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

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
import com.btsl.pretups.logging.NetworkInterfacesLog;


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
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading Network List.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param pLookupType
     *            String//to know thw name of the interface Category defined in
     *            the lookups table
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadInterfaceNetworkMappingList(Connection pCon, String pNetworkCode, String pLookupType) throws BTSLBaseException {
        final String methodName = "loadInterfaceNetworkMappingList";
        if (log.isDebugEnabled()) {
            log.debug("loadInterfaceNetworkMappingList()", "Entered pNetworkCode" + pNetworkCode + " pLookupType=" + pLookupType);
        }

         

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT inm.network_code,inm.interface_id,inm.queue_size,inm.queue_time_out,");
        strBuff.append(" inm.request_time_out,inm.next_check_que_req_sec,");
        strBuff.append(" inm.created_by, inm.modified_by, inm.created_on, inm.modified_on,");
        strBuff.append(" it.interface_name,i.interface_description, it.interface_type_id,l.lookup_name ");
        strBuff.append(" FROM interface_network_mapping inm,interfaces i,interface_types it,lookups l ");
        strBuff.append(" WHERE i.status!='N' AND inm.network_code = ? AND inm.interface_id = i.interface_id ");
        strBuff.append(" AND i.interface_type_id = it.interface_type_id AND it.interface_type_id = l.lookup_code");
        strBuff.append(" AND l.lookup_type = ? ");
        strBuff.append(" ORDER BY it.interface_name");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadInterfaceNetworkMappingList()", "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pNetworkCode);
            pstmt.setString(2, pLookupType);
            try(ResultSet rs = pstmt.executeQuery();)
            {
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
                interfaceNetworkMappingVO.setLastModifiedOn(rs.getTimestamp("modified_on").getTime());
                interfaceNetworkMappingVO.setInterfaceName(rs.getString("interface_name"));
                interfaceNetworkMappingVO.setInterfaceIDDesc(rs.getString("interface_description"));
                interfaceNetworkMappingVO.setInterfaceCategoryID(rs.getString("interface_type_id"));
                interfaceNetworkMappingVO.setInterfaceCategoryIDDesc(rs.getString("lookup_name"));

                list.add(interfaceNetworkMappingVO);
            }
            }
        } catch (SQLException sqe) {
            log.error("loadInterfaceNetworkMappingList()", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[loadInterfaceNetworkMappingList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceNetworkMappingList()", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadInterfaceNetworkMappingList()", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[loadInterfaceNetworkMappingList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceNetworkMappingList()", "error.general.processing");
        } finally {
        	   

            if (log.isDebugEnabled()) {
                log.debug("loadInterfaceNetworkMappingList()", "Exiting: networkMap size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for Inserting Interface Network Mapping Detail.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pInterfaceNetworkMappingVO
     *            InterfaceNetworkMappingVO
     * 
     * @return addCount int
     * @exception BTSLBaseException
     */
    public int addInterfaceNetworkMapping(Connection pCon, InterfaceNetworkMappingVO pInterfaceNetworkMappingVO) throws BTSLBaseException {
        
        int addCount = 0;

        final String methodName = "addInterfaceNetworkMapping";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: InterfaceNetworkMappingVO= " + pInterfaceNetworkMappingVO);
        }

        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO interface_network_mapping (network_code, ");
            strBuff.append(" interface_id,queue_size,queue_time_out,request_time_out, ");
            strBuff.append("next_check_que_req_sec,created_by,modified_by,created_on,modified_on)");
            strBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?)");

            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlInsert:" + insertQuery);
            }

           try(PreparedStatement pstmtInsert = pCon.prepareStatement(insertQuery);)
           {

            pstmtInsert.setString(1, pInterfaceNetworkMappingVO.getNetworkCode());
            pstmtInsert.setString(2, pInterfaceNetworkMappingVO.getInterfaceID());
            pstmtInsert.setLong(3, pInterfaceNetworkMappingVO.getQueueSize());
            pstmtInsert.setLong(4, pInterfaceNetworkMappingVO.getQueueTimeOut());
            pstmtInsert.setLong(5, pInterfaceNetworkMappingVO.getRequestTimeOut());
            pstmtInsert.setLong(6, pInterfaceNetworkMappingVO.getNextCheckQueueReqSec());
            pstmtInsert.setString(7, pInterfaceNetworkMappingVO.getCreatedBy());
            pstmtInsert.setString(8, pInterfaceNetworkMappingVO.getModifiedBy());
            pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(pInterfaceNetworkMappingVO.getCreatedOn()));
            pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(pInterfaceNetworkMappingVO.getModifiedOn()));

            addCount = pstmtInsert.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[addInterfaceNetworkMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[addInterfaceNetworkMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: addCount=" + addCount);
            }
        } // end of finally

        return addCount;
    }

    /**
     * Method for Updating Interface Network Mapping Detail.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pInterfaceNetworkMappingVO
     *            InterfaceNetworkMappingVO
     * 
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int updateInterfaceNetworkMapping(Connection pCon, InterfaceNetworkMappingVO pInterfaceNetworkMappingVO) throws BTSLBaseException {
         
        int updateCount = 0;

        final String methodName = "updateInterfaceNetworkMapping";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: pInterfaceNetworkMappingVO= " + pInterfaceNetworkMappingVO);
        }
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("Update interface_network_mapping SET queue_size = ?,");
            strBuff.append("queue_time_out = ?,request_time_out = ?,next_check_que_req_sec = ?, ");
            strBuff.append("modified_by = ?, modified_on= ? WHERE network_code = ? AND interface_id = ?");

            String updateQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            // check wehther the record already updated or not
            boolean modified = this.recordModified(pCon, pInterfaceNetworkMappingVO.getNetworkCode(), pInterfaceNetworkMappingVO.getInterfaceID(), pInterfaceNetworkMappingVO.getLastModifiedOn());

            // call the DAO method to Update the network Detail
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }

            try(PreparedStatement psmtUpdate = pCon.prepareStatement(updateQuery);)
            {

            psmtUpdate.setLong(1, pInterfaceNetworkMappingVO.getQueueSize());
            psmtUpdate.setLong(2, pInterfaceNetworkMappingVO.getQueueTimeOut());
            psmtUpdate.setLong(3, pInterfaceNetworkMappingVO.getRequestTimeOut());
            psmtUpdate.setLong(4, pInterfaceNetworkMappingVO.getNextCheckQueueReqSec());
            psmtUpdate.setString(5, pInterfaceNetworkMappingVO.getModifiedBy());
            psmtUpdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(pInterfaceNetworkMappingVO.getModifiedOn()));
            psmtUpdate.setString(7, pInterfaceNetworkMappingVO.getNetworkCode());
            psmtUpdate.setString(8, pInterfaceNetworkMappingVO.getInterfaceID());

            updateCount = psmtUpdate.executeUpdate();
        }
        }// end of try
        catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException:" + be.toString());
            log.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[updateInterfaceNetworkMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[updateInterfaceNetworkMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
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
     * @param pInterfaceID
     *            String
     * @param oldLastModified
     *            long
     * 
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String networkCode, String pInterfaceID, long oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: networkCode= " + networkCode + " pInterfaceID=" + pInterfaceID + "oldLastModified= " + oldLastModified);
        }

        
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM interface_network_mapping WHERE network_code=? and interface_id = ?";
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try (PreparedStatement pstmt = con.prepareStatement(sqlRecordModified);){
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            }
            // create a prepared statement and execute it
           
            pstmt.setString(1, networkCode);
            pstmt.setString(2, pInterfaceID);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " old=" + oldLastModified);
                if (newLastModified != null) {
                    log.debug(methodName, " new=" + newLastModified.getTime());
                } else {
                    log.debug(methodName, " new=null");
                }
            }
            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

            return modified;
        } 
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch

        finally {
        	

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method for loading Network Prefix for a particular Network.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadInterfaceNetworkPrefix(Connection pCon, String pNetworkCode) throws BTSLBaseException {
        final String methodName = "loadInterfaceNetworkPrefix";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered netwrkCode=" + pNetworkCode);
        }

         
        ArrayList networkList = new ArrayList();
        InterfaceNetworkMappingQry interfaceNetworkMappingQry = (InterfaceNetworkMappingQry)ObjectProducer.getObject(QueryConstants.INTERFACE_NTW_MAPP_QRY, QueryConstants.QUERY_PRODUCER);
        String sqlSelect = interfaceNetworkMappingQry.loadInterfaceNetworkPrefixQry();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try (PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);){
            
            pstmt.setString(1, pNetworkCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {

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
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[loadInterfaceNetworkPrefix]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception: " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[loadInterfaceNetworkPrefix]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: List size=" + networkList.size());
            }
        }
    }

    /**
     * Method for inserting Interface Network Prefix Mapping.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pVoList
     *            ArrayList
     * 
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int insertInterfaceNetworkPrefix(Connection pCon, ArrayList pVoList) throws BTSLBaseException {
         
        int insertCount = 0;
        final String methodName = "insertInterfaceNetworkPrefix";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: pVoList List Size= " + pVoList.size());
        }
        try {
            if (pVoList != null) {
                StringBuilder strBuff = new StringBuilder();

                strBuff.append("INSERT INTO intf_ntwrk_prfx_mapping (network_code,interface_id,");
                strBuff.append("prefix_id,action,method_type,");
                strBuff.append("created_by,modified_by,created_on,modified_on) values ");
                strBuff.append("(?,?,?,?,?,?,?,?,?)");

                String insertQuery = strBuff.toString();

                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Query sqlInsert:" + insertQuery);
                }

                try(PreparedStatement psmtInsert = pCon.prepareStatement(insertQuery);)
                {
                InterfaceNetworkPrefixMappingVO interfaceNetworkPrefixMappingVO = null;
                for (int i = 0, j = pVoList.size(); i < j; i++) {
                    interfaceNetworkPrefixMappingVO = (InterfaceNetworkPrefixMappingVO) pVoList.get(i);

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
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }

            }
            }
        } // end of try
        catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[insertInterfaceNetworkPrefix]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[insertInterfaceNetworkPrefix]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
       

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for deleting Interface Network Prefix Mapping
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * 
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteInterfaceNetworkPrefix(Connection pCon, String pNetworkCode) throws BTSLBaseException {
        
        int deleteCount = 0;

        final String methodName = "deleteInterfaceNetworkPrefix";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: pNetworkCode = " + pNetworkCode);
        }

        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM intf_ntwrk_prfx_mapping where ");
            strBuff.append("network_code = ?");
            String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            try(PreparedStatement psmtDelete = pCon.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, pNetworkCode);
            deleteCount = psmtDelete.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[deleteInterfaceNetworkPrefix]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[deleteInterfaceNetworkPrefix]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for checking if interface is associated with network prefix or not
     * This method is to be called before deletion of interface network mapping
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param pInterfaceID
     *            String
     * 
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isExistsInterfaceNetworkPrefixMapping(Connection pCon, String pNetworkCode, String pInterfaceID) throws BTSLBaseException {
        final String methodName = "isExistsInterfaceNetworkPrefixMapping";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:: pNetworkCode = " + pNetworkCode + " pInterfaceID= " + pInterfaceID);
        }
         
        
        boolean found = false;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT 1 FROM intf_ntwrk_prfx_mapping WHERE network_code=? AND interface_id=? ");
            String selectQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query selectQuery:" + selectQuery);
            }
            try(PreparedStatement psmtSelect = pCon.prepareStatement(selectQuery);)
            {
            psmtSelect.setString(1, pNetworkCode);
            psmtSelect.setString(2, pInterfaceID);
            try(ResultSet rs = psmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            }
        }
            // end of try
            }
        }
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[isExistsInterfaceNetworkPrefixMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[isExistsInterfaceNetworkPrefixMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: found=" + found);
            }
        } // end of finally
        return found;
    }

    /**
     * Method for deleting Interface Network Mapping
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param pInterfaceID
     * @return int
     * @throws BTSLBaseException
     */
   
    public int deleteInterfaceNetworkMapping(Connection pCon, String pNetworkCode, String pInterfaceID) throws BTSLBaseException {
        final String methodName = "deleteInterfaceNetworkMapping";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered :: pNetworkCode= " + pNetworkCode + " pInterfaceID= " + pInterfaceID);
        }
         
        int deleteCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM interface_network_mapping WHERE ");
            strBuff.append("network_code = ? AND interface_id=? ");
            String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            try(PreparedStatement psmtDelete = pCon.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, pNetworkCode);
            psmtDelete.setString(2, pInterfaceID);
            deleteCount = psmtDelete.executeUpdate();
            }
        } // end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[deleteInterfaceNetworkMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteInterfaceNetworkPrefix", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO[deleteInterfaceNetworkMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteInterfaceNetworkPrefix", "error.general.processing");
        } // end of catch
        finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

     //Finalatix
    /**
     * Method for loading Interface Network Mapping Details By Id.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param pLookupType
     *            String//to know thw name of the interface Category defined in
     *            the lookups table
     * @param interfaceId
     *            String
     * 
     * @return InterfaceNetworkMappingVO
     * @exception BTSLBaseException
     */
	public InterfaceNetworkMappingVO loadInterfaceNetworkMappingById(Connection pCon, String pNetworkCode,
			String pLookupType, String interfaceId) throws BTSLBaseException {
		 final String METHOD_NAME = "loadInterfaceNetworkMappingById";
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, "Entered pNetworkCode" + pNetworkCode + " pLookupType=" + pLookupType);
	        }

	        InterfaceNetworkMappingVO interfaceNetworkMappingVO = null;

	        StringBuilder strBuff = new StringBuilder();
	        strBuff.append(" SELECT inm.network_code,inm.interface_id,inm.queue_size,inm.queue_time_out,");
	        strBuff.append(" inm.request_time_out,inm.next_check_que_req_sec,");
	        strBuff.append(" inm.created_by, inm.modified_by, inm.created_on, inm.modified_on,");
	        strBuff.append(" it.interface_name,i.interface_description, it.interface_type_id,l.lookup_name ");
	        strBuff.append(" FROM interface_network_mapping inm,interfaces i,interface_types it,lookups l ");
	        strBuff.append(" WHERE i.status!='N' AND inm.network_code = ? AND inm.interface_id = i.interface_id ");
	        strBuff.append(" AND i.interface_type_id = it.interface_type_id AND it.interface_type_id = l.lookup_code");
	        strBuff.append(" AND l.lookup_type = ? ");
	        strBuff.append(" AND i.interface_id = ? ");
	        strBuff.append(" ORDER BY it.interface_name");

	        String sqlSelect = strBuff.toString();
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
	        }
	        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
	            
	            pstmt.setString(1, pNetworkCode);
	            pstmt.setString(2, pLookupType);
	            pstmt.setString(3, interfaceId);
	            
	            try(ResultSet rs = pstmt.executeQuery();)
	            {
	           
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
	                interfaceNetworkMappingVO.setLastModifiedOn(rs.getTimestamp("modified_on").getTime());
	                interfaceNetworkMappingVO.setInterfaceName(rs.getString("interface_name"));
	                interfaceNetworkMappingVO.setInterfaceIDDesc(rs.getString("interface_description"));
	                interfaceNetworkMappingVO.setInterfaceCategoryID(rs.getString("interface_type_id"));
	                interfaceNetworkMappingVO.setInterfaceCategoryIDDesc(rs.getString("lookup_name"));
	            }
	            NetworkInterfacesLog.log(interfaceNetworkMappingVO);
	          }
	        } 
	       
	        catch (SQLException sqe) {
	            log.error(METHOD_NAME, "SQLException : " + sqe);
	            log.errorTrace(METHOD_NAME, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO["+METHOD_NAME+"]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
	        } 
	        
	        catch (Exception ex) {
	            log.error(METHOD_NAME, "Exception : " + ex);
	            log.errorTrace(METHOD_NAME, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceNetworkMappingDAO["+METHOD_NAME+"]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
	        } finally {
	        	   

	            if (log.isDebugEnabled()) {
	                log.debug(METHOD_NAME, "Exiting:" );
	            }
	        }
	        return interfaceNetworkMappingVO;
	}
	//Finalatix

}
