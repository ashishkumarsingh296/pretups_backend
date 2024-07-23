package com.txn.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.ServiceClassVO;

public class ServiceClassTxnDAO {

    private Log _log = LogFactory.getFactory().getInstance(ServiceClassTxnDAO.class.getName());

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
        final String methodName = "loadServiceClassInfoByCode";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_serviceClass:" + p_serviceClass + " p_interfaceID:" + p_interfaceID);
        }
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
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
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCode]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassDAO[loadServiceClassInfoByCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting serviceClassVO:" + serviceClassVO);
            }
        }// end of finally
    }

}
