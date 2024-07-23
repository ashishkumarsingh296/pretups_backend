/*
 * @# RoutingDAO.java
 * This class is the controller class of the Channel user Module.
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Oct 27, 2005 Ankit Zindal Initial creation
 * Dec 19, 2006 Ankit Zindal Modified Change ID=ACCOUNTID
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.routing.subscribermgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

/**
 * @author simarnoor.bains
 *
 */

public class RoutingDAO {
    /**
     * Field log.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for checking Is record exist or not.
     * 
     * @param pCon
     * @param pMsisdn
     * @param pServiceType
     * @param pServiceClassCode
     * @param pStatus
     * @return flag boolean
     * @throws BTSLBaseException
     */
    /**
     * @param pCon
     * @param pMsisdn
     * @param pServiceType
     * @param pServiceClassCode
     * @param pStatus
     * @param pModule
     * @param pUserType
     * @return
     * @throws BTSLBaseException
     */
    public boolean isMsisdnServiceClassMapped(Connection pCon, String pMsisdn, String pServiceType, String pServiceClassCode, String pStatus, String pModule, String pUserType) throws BTSLBaseException {
        final String methodName = "isMsisdnServiceClassMapped";
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pMsisdn:");
        	loggerValue.append(pMsisdn);
        	loggerValue.append(" pServiceType=");
        	loggerValue.append(pServiceType);
        	loggerValue.append(" pServiceClassCode=");
        	loggerValue.append(pServiceClassCode);
        	loggerValue.append(" pStatus=");
        	loggerValue.append(pStatus);
        	loggerValue.append(" pModule=");
        	loggerValue.append(pModule);
        	loggerValue.append(" pUserType=");
        	loggerValue.append(pUserType);
            log.debug(methodName, loggerValue);
        }
       
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder("SELECT 1 FROM srv_class_mapped_msisdn ");
        strBuff.append(" WHERE msisdn = ? AND service_type=? AND service_class_code=? and status=? and module=? and user_type=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + strBuff.toString());
        }
        try(PreparedStatement  pstmt = pCon.prepareStatement(strBuff.toString());) {
          
            int i = 0;
            pstmt.setString(++i, pMsisdn);
            pstmt.setString(++i, pServiceType);
            pstmt.setString(++i, pServiceClassCode);
            pstmt.setString(++i, pStatus);
            pstmt.setString(++i, pModule);
            pstmt.setString(++i, pUserType);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        }
        }// end of try
        catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[isMsisdnServiceClassMapped]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingDAO[isMsisdnServiceClassMapped]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }// end of finally.
    }
		/**
		 * @param pCon
		 * @param pMsisdn
		 * @param pSubscriberType
		 * @return
		 * @throws BTSLBaseException
		 */
		public ListValueVO loadInterfaceIDForMNP(Connection pCon,String pMsisdn,String pSubscriberType) throws BTSLBaseException
		{
			final String methodName = "loadInterfaceIDForMNP";
			if(log.isDebugEnabled()) {
				StringBuilder loggerValue= new StringBuilder();
				loggerValue.setLength(0);
				loggerValue.append("Entered pMsisdn:");
				loggerValue.append(pMsisdn);
				loggerValue.append("pSubscriberType");
				loggerValue.append(pSubscriberType);
				log.debug(methodName,loggerValue);
			}
			PreparedStatement pstmt=null;
			ResultSet rs = null;
			ListValueVO listValueVO=null;
			try
	        {
				RoutingQry routingQuery = (RoutingQry)ObjectProducer.getObject(QueryConstants.ROUTING_QRY, QueryConstants.QUERY_PRODUCER);
				pstmt   = routingQuery.loadInterfaceIDForMNPQry(pCon, pMsisdn, pSubscriberType);
	        	
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
	        	log.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RoutingTxnDAO[loadInterfaceID]","","","","SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}
	        catch (Exception ex)
			{
	        	log.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RoutingTxnDAO[loadInterfaceID]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			} 
	        finally
			{
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
	            	log.error("An error occurred closing statement.", e);
	            }
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting: listValueVO=" + listValueVO);
				}
			}//end of finally.
			return listValueVO;
		}
}
