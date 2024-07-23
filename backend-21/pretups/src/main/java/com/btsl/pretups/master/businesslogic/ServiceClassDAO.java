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
package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.util.OracleUtil;

public class ServiceClassDAO {
    private Log _log = LogFactory.getFactory().getInstance(ServiceClassDAO.class.getName());

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

        final String methodName = "loadServiceClassDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering parms  p_interfaceCode:" + p_interfaceCode);
        }

        ArrayList serviceClassList = null;
         
        
        StringBuilder selectQueryBuff = new StringBuilder("SELECT sc.service_class_id,sc.service_class_code, ");
        selectQueryBuff.append("lk.lookup_name,sc.service_class_name,sc.interface_id,sc.created_on,sc.created_by,sc.status,");
        selectQueryBuff.append("sc.modified_on,sc.modified_by,sc.p2p_sender_suspend, sc.p2p_receiver_suspend, ");
        selectQueryBuff.append("sc.c2s_receiver_suspend, sc.p2p_sender_allowed_status, sc.p2p_sender_denied_status, ");
        selectQueryBuff.append("sc.p2p_receiver_allowed_status, sc.p2p_receiver_denied_status, ");
        selectQueryBuff.append("sc.c2s_receiver_allowed_status, sc.c2s_receiver_denied_status ");
        selectQueryBuff.append("FROM service_classes sc,lookups lk WHERE ");
        selectQueryBuff.append("interface_id=? AND sc.status<>'N' AND lk.lookup_type=? AND lk.lookup_code=sc.status ");
        selectQueryBuff.append("ORDER BY service_class_name");
        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadInterfaceList", "Select Query " + selectQuery);
        }
        serviceClassList = new ArrayList();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
           
            
            pstmtSelect.setString(1, p_interfaceCode);
            pstmtSelect.setString(2, PretupsI.STATUS_TYPE);
           try( ResultSet rs = pstmtSelect.executeQuery();)
           {
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
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceList", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
        	_log.debug(methodName, "Exiting..");
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
        final String methodName = "loadInterfaceDetailsForServiceClassID";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_serviceClass:" + p_serviceClass);
        }
        
        InterfaceVO interafceVO = new InterfaceVO();
         
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT i.interface_type_id FROM interfaces i, service_classes sc");
            selectQueryBuff.append(" WHERE sc.service_class_id=? AND sc.interface_id=i.interface_id ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_serviceClass);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                interafceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
            }
            return interafceVO;
        }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceDetailsForServiceClassID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadInterfaceDetailsForServiceClassID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting interafceVO:" + interafceVO);
            }
        }// end of finally
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
        final String methodName = "loadServiceClassInfoByCodeWithAll";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_serviceClass:" + p_serviceClass + " p_interfaceID:" + p_interfaceID);
        }
        
        ServiceClassVO serviceClassVO = null;
        HashMap<String, ServiceClassVO> serviceMap = new HashMap<>();
         
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT service_class_id,service_class_code,");
            selectQueryBuff.append(" service_class_name,status,p2p_sender_suspend, p2p_receiver_suspend, ");
            selectQueryBuff.append(" c2s_receiver_suspend, p2p_sender_allowed_status, p2p_sender_denied_status, ");
            selectQueryBuff.append(" p2p_receiver_allowed_status, p2p_receiver_denied_status, ");
            selectQueryBuff.append(" c2s_receiver_allowed_status, c2s_receiver_denied_status");
            selectQueryBuff.append(" FROM service_classes");
            selectQueryBuff.append(" WHERE (service_class_code=? OR service_class_code=? ) AND interface_id=? AND status<> ? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            int i = 1;
            pstmtSelect.setString(i, p_serviceClass);
            pstmtSelect.setString(++i, PretupsI.ALL);
            pstmtSelect.setString(++i, p_interfaceID);
            pstmtSelect.setString(++i, PretupsI.NO);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCodeWithAll]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCodeWithAll]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting serviceClassVO:" + serviceClassVO);
            }
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
        final String methodName = "loadServiceClassInfoByCodeWithAll";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        Connection con = null;
        ServiceClassVO serviceClassVO = null;
        HashMap<String, ServiceClassVO> serviceMap = new HashMap<String, ServiceClassVO>();
        ResultSet rs = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT service_class_id, service_class_code,");
            selectQueryBuff.append(" service_class_name, interface_id, status, p2p_sender_suspend, p2p_receiver_suspend, ");
            selectQueryBuff.append(" c2s_receiver_suspend, p2p_sender_allowed_status, p2p_sender_denied_status, ");
            selectQueryBuff.append(" p2p_receiver_allowed_status, p2p_receiver_denied_status, ");
            selectQueryBuff.append(" c2s_receiver_allowed_status, c2s_receiver_denied_status");
            selectQueryBuff.append(" FROM service_classes");
            selectQueryBuff.append(" WHERE status<> ? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
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
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCodeWithAll]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCodeWithAll]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
           OracleUtil.closeQuietly(con);
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting serviceClassVO:" + serviceClassVO);
            }
        }// end of finally
    }
}
