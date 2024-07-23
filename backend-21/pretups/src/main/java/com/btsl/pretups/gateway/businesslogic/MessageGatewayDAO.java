/*
 * @# MessageGatewayDAO.java
 * This is DAO class for the MessageGateway module
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 7, 2005 Sandeep Goel Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.gateway.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * class MessageGatewayDAO
 */
public class MessageGatewayDAO {
    /**
     * Field log. This field is used to display the logs for debugging purpose.
     */
    private Log log = LogFactory.getLog(MessageGatewayDAO.class.getName());

    /**
     * Method isMessageGatewayInfoExist.
     * This method is to check the existence to the gateway code for the
     * combination of the gateway type,gatewaySubtype
     * 
     * @author sandeep.goel
     * @param conn
     *            Connection
     * @param gatewayCode
     *            String
     * @param gatewayType
     *            String
     * @param gatewaySubType
     *            String
     * @param port
     *            String
     * @param servicePort
     *            String
     * @param checkGatewayCode
     *            boolean
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isMessageGatewayInfoExist(Connection conn, String gatewayCode, String gatewayType, String gatewaySubType, String port, String servicePort, boolean checkGatewayCode) throws BTSLBaseException {
        final String methodName = "isMessageGatewayInfoExist";
        LogFactory.printLog(methodName,  "Entered:gatewayCode=" + gatewayCode + ",gatewaySubType=" + gatewaySubType + ",gatewayType=" + gatewayType + ",port=" + port + ",servicePort=" + servicePort + ",checkGatewayCode=" + checkGatewayCode, log);
        
         
        
        boolean isExist = false;
        StringBuilder query = new StringBuilder();
        try {
            query.append("SELECT 1 FROM message_gateway MG,  req_message_gateway REQMG ");
            query.append("WHERE MG.gateway_code=REQMG.gateway_code ");
            query.append("AND MG.gateway_type=? AND MG.gateway_subtype=? AND REQMG.port=? AND REQMG.service_port=? ");
            if (checkGatewayCode) {
                query.append("AND MG.gateway_code <> ?");
            }
            if (log.isDebugEnabled()) {
                log.debug("isMessageGatewayInfoExist()", "QUERY=" + query);
            }
            try(PreparedStatement pstmtSelect = conn.prepareStatement(query.toString());)
            {
            pstmtSelect.setString(1, gatewayType);
            pstmtSelect.setString(2, gatewaySubType);
            pstmtSelect.setString(3, port);
            pstmtSelect.setString(4, servicePort);
            if (checkGatewayCode) {
                pstmtSelect.setString(5, gatewayCode);
            }

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
        }
            }
        }// end of try
        catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayInfoExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayInfoExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
        	LogFactory.printLog(methodName, "Exititng:isExist=" + isExist, log);
            
        }// end of finally
        return isExist;
    }// end isMessageGatewayInfoExist

    /**
     * Load the message Detail cache. it also include message request
     * information and message response information
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap<String,MessageGatewayVO> loadMessageGatewayCache() throws BTSLBaseException {

        final String methodName = "loadMessageGatewayCache";
        LogFactory.printLog(methodName,"Entered", log);

        HashMap<String,MessageGatewayVO> messageGatewayMap = new HashMap<String,MessageGatewayVO>();

        MessageGatewayQry messageGatewayQry = (MessageGatewayQry)ObjectProducer.getObject(QueryConstants.MESSAGE_GATEWAY_QRY, QueryConstants.QUERY_PRODUCER);
        String sqlSelect = messageGatewayQry.loadMessageGatewayCacheQry();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
            {
            pstmt.setString(1, PretupsI.STATUS_DELETE);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            MessageGatewayVO gatewayVO = null;
            while (rs.next()) {
                gatewayVO = new MessageGatewayVO();

                gatewayVO.setGatewayCode(rs.getString("gateway_code"));
                gatewayVO.setGatewayType(rs.getString("gateway_type"));
                gatewayVO.setGatewaySubType(rs.getString("gateway_subtype"));
                gatewayVO.setProtocol(rs.getString("protocol"));
                gatewayVO.setHandlerClass(rs.getString("handler_class"));
                gatewayVO.setNetworkCode(rs.getString("network_code"));
                gatewayVO.setHost(rs.getString("host"));
                gatewayVO.setModifiedOn(rs.getDate("modified_on"));
                gatewayVO.setModifiedOnTimestamp(rs.getTimestamp("modified_on"));

                gatewayVO.setGatewaySubTypeName(rs.getString("gateway_subtype_name"));
                gatewayVO.setPlainMsgAllowed(rs.getString("plain_msg_allowed"));
                gatewayVO.setBinaryMsgAllowed(rs.getString("binary_msg_allowed"));
                gatewayVO.setAccessFrom(rs.getString("access_from"));
                gatewayVO.setGatewaySubTypeName(rs.getString("gateway_subtype_name"));
                gatewayVO.setFlowType(rs.getString("flow_type"));
                gatewayVO.setResponseType(rs.getString("response_type"));
                gatewayVO.setTimeoutValue(rs.getLong("timeout_value"));
                gatewayVO.setStatus(rs.getString("status"));
                if (PretupsI.NO.equals(rs.getString("authReqd"))) {
                    gatewayVO.setUserAuthorizationReqd(false);
                }
                gatewayVO.setReqpasswordtype(rs.getString("req_password_plain"));
                messageGatewayMap.put(gatewayVO.getGatewayCode(), gatewayVO);
            }

            HashMap requestMap = this.loadRequestMessageGateway(con);
            HashMap responseMap = this.loadResponseMessageGateway(con);

            Iterator iterator = messageGatewayMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                MessageGatewayVO messageGatewayVO = (MessageGatewayVO) messageGatewayMap.get(key);
                messageGatewayVO.setRequestGatewayVO((RequestGatewayVO) requestMap.get(key));
                messageGatewayVO.setResponseGatewayVO((ResponseGatewayVO) responseMap.get(key));
            }

            requestMap = null;
            responseMap = null;

        }
            }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMessageGatewayCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMessageGatewayCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	OracleUtil.closeQuietly(con);
        	LogFactory.printLog(methodName, "Exiting: Messagegateway size=" + messageGatewayMap.size(), log);

            
        }
        return messageGatewayMap;
    }

    /**
     * load the request Message Gateway information
     * 
     * @param conn
     * @return HahsMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    private HashMap loadRequestMessageGateway(Connection conn) throws BTSLBaseException {

        final String methodName = "loadRequestMessageGateway";
        LogFactory.printLog(methodName,"Entered", log);
       

        HashMap requestMap = new HashMap();

        

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT gateway_code, port, service_port, login_id, password, encryption_level, ");
        strBuff.append(" encryption_key, content_type, auth_type, status, modified_on,underprocess_check_reqd ");
        strBuff.append(" FROM req_message_gateway ");
        String sqlSelect = strBuff.toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = conn.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();) {
            
            
            RequestGatewayVO requestGatewayVO = null;
            while (rs.next()) {
                requestGatewayVO = new RequestGatewayVO();

                requestGatewayVO.setGatewayCode(rs.getString("gateway_code"));
                requestGatewayVO.setPort(rs.getString("port"));
                requestGatewayVO.setServicePort(rs.getString("service_port"));
                requestGatewayVO.setLoginID(rs.getString("login_id"));
                requestGatewayVO.setPassword(rs.getString("password"));
                requestGatewayVO.setEncryptionLevel(rs.getString("encryption_level"));
                requestGatewayVO.setEncryptionKey(rs.getString("encryption_key"));
                requestGatewayVO.setContentType(rs.getString("content_type"));
                requestGatewayVO.setAuthType(rs.getString("auth_type"));
                requestGatewayVO.setStatus(rs.getString("status"));
                requestGatewayVO.setModifiedOn(rs.getDate("modified_on"));
                requestGatewayVO.setModifiedOnTimestamp(rs.getTimestamp("modified_on"));
                if (!BTSLUtil.isNullString(requestGatewayVO.getPassword())) {
                    requestGatewayVO.setDecryptedPassword(BTSLUtil.decryptText(requestGatewayVO.getPassword()));
                }

                requestGatewayVO.setUnderProcessCheckReqd(rs.getString("underprocess_check_reqd"));
                requestMap.put(requestGatewayVO.getGatewayCode(), requestGatewayVO);

            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadRequestMessageGateway]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadRequestMessageGateway]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	LogFactory.printLog(methodName, "Exiting: RequestMessageGateway size=" + requestMap.size(), log);

            
        }

        return requestMap;
    }

    /**
     * load the Response Message Gateway information
     * 
     * @param conn
     * @return HahsMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    private HashMap loadResponseMessageGateway(Connection conn) throws BTSLBaseException {

        final String methodName = "loadResponseMessageGateway";
        LogFactory.printLog(methodName,"Entered", log);
        

        HashMap responseMap = new HashMap();

        

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT gateway_code, port, service_port, login_id, password, dest_no, status, path ,timeout, ");
        strBuff.append(" modified_on FROM res_message_gateway ");
        String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName,"QUERY sqlSelect=" + sqlSelect, log);
       

        try(PreparedStatement pstmt = conn.prepareStatement(sqlSelect); ResultSet rs = pstmt.executeQuery();) {
            
           
            ResponseGatewayVO responseGatewayVO = null;
            while (rs.next()) {
                responseGatewayVO = new ResponseGatewayVO();

                responseGatewayVO.setGatewayCode(rs.getString("gateway_code"));
                responseGatewayVO.setPort(rs.getString("port"));
                responseGatewayVO.setServicePort(rs.getString("service_port"));
                responseGatewayVO.setLoginID(rs.getString("login_id"));
                responseGatewayVO.setPassword(rs.getString("password"));
                responseGatewayVO.setDestNo(rs.getString("dest_no"));
                responseGatewayVO.setStatus(rs.getString("status"));
                responseGatewayVO.setPath(rs.getString("path"));
                responseGatewayVO.setModifiedOn(rs.getDate("modified_on"));
                responseGatewayVO.setModifiedOnTimestamp(rs.getTimestamp("modified_on"));
                responseGatewayVO.setTimeOut(rs.getInt("timeout"));
                if (!BTSLUtil.isNullString(responseGatewayVO.getPassword())) {
                    responseGatewayVO.setDecryptedPassword(BTSLUtil.decryptText(responseGatewayVO.getPassword()));
                }
                responseMap.put(responseGatewayVO.getGatewayCode(), responseGatewayVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadResponseMessageGateway]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadResponseMessageGateway]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	 LogFactory.printLog(methodName,"Exiting: Messagegateway size=" + responseMap.size(), log);

            
        }
        return responseMap;
    }

    /**
     * Load the messageMapping Detail cache.
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap<String,MessageGatewayMappingCacheVO>  loadMessageGatewayMappingCache() throws BTSLBaseException {
        final String methodName = "loadMessageGatewayMappingCache";
        LogFactory.printLog(methodName,"Entered", log);
       
        HashMap<String,MessageGatewayMappingCacheVO> messageGatewayMap = new HashMap<String,MessageGatewayMappingCacheVO> ();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT req_code, res_code, alt_code, modified_on ");
        strBuff.append(" FROM ");
        strBuff.append(" message_req_resp_mapping ");

        String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName,"QUERY sqlSelect=" + sqlSelect, log);
       

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();)
            {
            MessageGatewayMappingCacheVO gatewayMappingCacheVO = null;
            while (rs.next()) {
                gatewayMappingCacheVO = new MessageGatewayMappingCacheVO();

                gatewayMappingCacheVO.setRequestCode(rs.getString("req_code"));
                gatewayMappingCacheVO.setResponseCode(rs.getString("res_code"));
                gatewayMappingCacheVO.setAlternateCode(rs.getString("alt_code"));
                gatewayMappingCacheVO.setModifiedOn(rs.getDate("modified_on"));
                gatewayMappingCacheVO.setModifiedOnTimestamp(rs.getTimestamp("modified_on"));

                messageGatewayMap.put(gatewayMappingCacheVO.getRequestCode(), gatewayMappingCacheVO);
            }
        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMessageGatewayMappingCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMessageGatewayMappingCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	OracleUtil.closeQuietly(con);
        	 LogFactory.printLog(methodName, "Exiting: Messagegateway size=" + messageGatewayMap.size(), log);

            
        }
        return messageGatewayMap;
    }

}