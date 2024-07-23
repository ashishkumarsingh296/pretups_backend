package com.txn.pretups.routing.subscribermgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.RoutingVO;
import com.btsl.util.BTSLUtil;

public class RoutingTxnDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * load the the subscriber control details according to msisdn.
     * method loadSubscriberRoutingList
     * 
     * @param p_con
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     * @author ved.sharma
     */
    public ArrayList loadSubscriberRoutingList(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "loadSubscriberRoutingList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_msisdn=" + p_msisdn);
        }
        PreparedStatement pstmtSelect = null;
        ArrayList list = new ArrayList();
        ResultSet rs = null;
        try {
            StringBuffer strBuff = new StringBuffer(250);
            strBuff.append("SELECT SR.msisdn, SR.interface_id, L.lookup_name subscriber_type, SR.external_interface_id,");
            strBuff.append("SR.status, U1.user_name created_by, SR.created_on, U2.user_name modified_by, SR.modified_on, text1, text2");
            strBuff.append(" FROM subscriber_routing sr, users u1, users u2, lookups L ");
            strBuff.append(" WHERE U1.user_id=SR.created_by AND U2.user_id=SR.modified_by ");
            strBuff.append(" AND L.lookup_code=SR.subscriber_type AND L.lookup_type=? AND SR.msisdn=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + strBuff.toString());
            }
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            pstmtSelect.setString(1, PretupsI.SUBSRICBER_TYPE);
            pstmtSelect.setString(2, p_msisdn);
            rs = pstmtSelect.executeQuery();
            RoutingVO routingVO = null;
            while (rs.next()) {
                routingVO = new RoutingVO();
                routingVO.setMsisdn(rs.getString("msisdn"));
                routingVO.setInterfaceID(rs.getString("interface_id"));
                routingVO.setSubscriberType(rs.getString("subscriber_type"));
                routingVO.setExternalInterfaceID(rs.getString("external_interface_id"));
                routingVO.setStatus(rs.getString("status"));
                routingVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("created_on") != null) {
                    routingVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                }
                routingVO.setModifiedBy(rs.getString("modified_by"));
                if (rs.getTimestamp("modified_on") != null) {
                    routingVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                }
                routingVO.setText1(rs.getString("text1"));
                routingVO.setText2(rs.getString("text2"));
                list.add(routingVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[loadSubscriberRoutingList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[loadSubscriberRoutingList]", "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting:return list=" + list.size());
            }
        }
        return list;
    }
    public ListValueVO loadInterfaceIDForMNP(Connection p_con,String p_Msisdn,String p_subscriberType) throws BTSLBaseException
	{
		final String methodName = "loadInterfaceIDForMNP";
		if(_log.isDebugEnabled()) {
			StringBuilder loggerValue= new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append("Entered p_Msisdn:");
			loggerValue.append(p_Msisdn);
			loggerValue.append("p_subscriberType");
			loggerValue.append(p_subscriberType);
			_log.debug(methodName,loggerValue);
		}
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		ListValueVO listValueVO=null;
		try
        {
			RoutingTxnQry routingTxnQry=(RoutingTxnQry)ObjectProducer.getObject(QueryConstants.ROUTING_TXN_QRY, QueryConstants.QUERY_PRODUCER);
			pstmt=routingTxnQry.loadInterfaceIDForMNP(p_con,p_Msisdn,p_subscriberType);
			
            rs= pstmt.executeQuery();
            if(rs.next())
			{
				listValueVO=new ListValueVO(rs.getString("handler_class"),rs.getString("interface_id"));
				listValueVO.setCodeName(rs.getString("network_code"));//added by Pankit
				listValueVO.setType(rs.getString("underprocess_msg_reqd"));
				listValueVO.setTypeName(rs.getString("service_class_id"));
				listValueVO.setIDValue(rs.getString("external_id"));
				listValueVO.setStatus(rs.getString("status"));
				listValueVO.setStatusType(rs.getString("statustype"));
				listValueVO.setOtherInfo(rs.getString("message_language1"));
				listValueVO.setOtherInfo2(rs.getString("message_language2"));
                listValueVO.setSingleStep(rs.getString("single_state_transaction"));
			} 
            
		}//end of try
        catch (SQLException sqe)
		{
        	_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RoutingTxnDAO[loadInterfaceID]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
        catch (Exception ex)
		{
        	_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RoutingTxnDAO[loadInterfaceID]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} 
        finally
		{
			try{if (rs != null){rs.close();}} catch (Exception e){_log.errorTrace(methodName, e);}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){_log.errorTrace(methodName, e);}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: listValueVO=" + listValueVO);
			}
		}//end of finally.
		return listValueVO;
		
	}
    /**
     * Method addSubscriberRoutingInfo.
     * This method is used to insert the routing information of subscriber
     * into the routing database
     * 
     * @param p_con
     *            Connection
     * @param p_routingVO
     *            RoutingVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addSubscriberRoutingInfo(Connection p_con, RoutingVO p_routingVO) throws BTSLBaseException {
        final String methodName = "addSubscriberRoutingInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_routingVO=" + p_routingVO);
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO subscriber_routing (msisdn, interface_id, subscriber_type, ");
            insertQuery.append("external_interface_id, status, created_by, created_on, modified_by, modified_on, ");
            insertQuery.append("text1, text2)");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery.toString());
            int i = 1;
            pstmtInsert.setString(i++, p_routingVO.getMsisdn());
            pstmtInsert.setString(i++, p_routingVO.getInterfaceID());
            pstmtInsert.setString(i++, p_routingVO.getSubscriberType());
            pstmtInsert.setString(i++, p_routingVO.getExternalInterfaceID());
            pstmtInsert.setString(i++, p_routingVO.getStatus());
            pstmtInsert.setString(i++, p_routingVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_routingVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_routingVO.getModifiedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_routingVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_routingVO.getText1());
            pstmtInsert.setString(i++, p_routingVO.getText2());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            int errorCode = sqe.getErrorCode();
            // Ignore Eventhandling if SQL Error corresponds to Unique
            // Constraint voilation, because this can occur if sender sends
            // second request and first request is not still processed
            if (errorCode != 00001) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[addSubscriberRoutingInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            }
            _log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[addSubscriberRoutingInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + addCount);
            }
        }
        return addCount;
    }

    /**
     * Method loadInterfaceID.
     * This method is to get the interface id on the basis of the subscriber
     * type and the msisdn.
     * 
     * @param p_con
     *            Connection
     * @param p_Msisdn
     *            String
     * @param p_subscriberType
     *            String
     * @return ListValueVO
     * @throws BTSLBaseException
     */
    public ListValueVO loadInterfaceID(Connection p_con, String p_Msisdn, String p_subscriberType) throws BTSLBaseException {
        final String methodName = "loadInterfaceID";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_Msisdn:");
        	loggerValue.append(p_Msisdn);
        	loggerValue.append("p_subscriberType:");
        	loggerValue.append(p_subscriberType);
            _log.debug(methodName,loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
          try {
        	  RoutingTxnQry routingTxnQry=(RoutingTxnQry)ObjectProducer.getObject(QueryConstants.ROUTING_TXN_QRY, QueryConstants.QUERY_PRODUCER);
        	  pstmt=routingTxnQry.loadInterfaceID(p_con,p_Msisdn,p_subscriberType);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("handler_class"), rs.getString("interface_id"));
                listValueVO.setType(rs.getString("underprocess_msg_reqd"));
                listValueVO.setTypeName(rs.getString("service_class_id"));
                listValueVO.setIDValue(rs.getString("external_id"));
                listValueVO.setStatus(rs.getString("status"));
                listValueVO.setStatusType(rs.getString("statustype"));
                listValueVO.setOtherInfo(rs.getString("message_language1"));
                listValueVO.setOtherInfo2(rs.getString("message_language2"));
                listValueVO.setSingleStep(rs.getString("single_state_transaction"));
            }

        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[loadInterfaceID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[loadInterfaceID]", "", "", "", "Exception:" + ex.getMessage());
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: listValueVO=" + listValueVO);
            }
        }// end of finally.
        return listValueVO;
    }

    /**
     * Method updateSubscriberRoutingInfo.
     * This method is used to update the routing information of subscriber
     * The updation is performed based on msisdn and subscriber type
     * 
     * @param p_con
     *            Connection
     * @param p_routingVO
     *            RoutingVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSubscriberAilternateRoutingInfo(Connection p_con, RoutingVO p_routingVO) throws BTSLBaseException {
        final String methodName = "updateSubscriberRoutingInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_routingVO=" + p_routingVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
            updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ,subscriber_type=? ");
            updateQuery.append("WHERE msisdn =? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            int i = 1;
            pstmtUpdate.setString(i++, p_routingVO.getInterfaceID());
            pstmtUpdate.setString(i++, p_routingVO.getExternalInterfaceID());
            pstmtUpdate.setString(i++, p_routingVO.getStatus());
            pstmtUpdate.setString(i++, p_routingVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_routingVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_routingVO.getText1());
            pstmtUpdate.setString(i++, p_routingVO.getText2());
            pstmtUpdate.setString(i++, p_routingVO.getSubscriberType());
            pstmtUpdate.setString(i++, p_routingVO.getMsisdn());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[updateSubscriberAilternateRoutingInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberAilternateRoutingInfo", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[updateSubscriberAilternateRoutingInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateSubscriberAilternateRoutingInfo", "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method updateSubscriberRoutingInfo.
     * This method is used to update the routing information of subscriber
     * The updation is performed based on msisdn and subscriber type
     * 
     * @param p_con
     *            Connection
     * @param p_routingVO
     *            RoutingVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSubscriberRoutingInfo(Connection p_con, RoutingVO p_routingVO) throws BTSLBaseException {
        final String methodName = "updateSubscriberRoutingInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_routingVO=" + p_routingVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE subscriber_routing SET interface_id = ?, external_interface_id = ?, ");
            updateQuery.append("status = ?, modified_by = ?, modified_on = ?, text1 = ?, text2 = ? ");
            updateQuery.append("WHERE msisdn =? AND subscriber_type=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            int i = 1;
            pstmtUpdate.setString(i++, p_routingVO.getInterfaceID());
            pstmtUpdate.setString(i++, p_routingVO.getExternalInterfaceID());
            pstmtUpdate.setString(i++, p_routingVO.getStatus());
            pstmtUpdate.setString(i++, p_routingVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_routingVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_routingVO.getText1());
            pstmtUpdate.setString(i++, p_routingVO.getText2());
            pstmtUpdate.setString(i++, p_routingVO.getMsisdn());
            pstmtUpdate.setString(i++, p_routingVO.getSubscriberType());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[updateSubscriberRoutingInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[updateSubscriberRoutingInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method deleteSubscriberRoutingInfo.
     * This method is used to delete the routing information of subscriber from
     * the routing database
     * the deletion is performed based on msisdn and subscriber type
     * 
     * @param p_con
     *            Connection
     * @param p_routingVO
     *            RoutingVO
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteSubscriberRoutingInfo(Connection p_con, RoutingVO p_routingVO) throws BTSLBaseException {
        final String methodName = "deleteSubscriberRoutingInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_routingVO=" + p_routingVO);
        }
        PreparedStatement pstmtDelete = null;
        int deleteCount = 0;
        try {
            StringBuffer deleteQuery = new StringBuffer();
            deleteQuery.append("DELETE FROM subscriber_routing ");
            deleteQuery.append("WHERE msisdn =? AND subscriber_type=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + deleteQuery);
            }
            pstmtDelete = p_con.prepareStatement(deleteQuery.toString());
            int i = 1;
            pstmtDelete.setString(i++, p_routingVO.getMsisdn());
            pstmtDelete.setString(i++, p_routingVO.getSubscriberType());
            deleteCount = pstmtDelete.executeUpdate();
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[deleteSubscriberRoutingInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingTxnDAO[deleteSubscriberRoutingInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtDelete != null) {
                    pstmtDelete.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + deleteCount);
            }
        }
        return deleteCount;
    }
}
