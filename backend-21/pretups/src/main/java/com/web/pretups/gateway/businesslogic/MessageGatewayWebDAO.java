package com.web.pretups.gateway.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.AllowedSourceVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayMappingVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.RequestGatewayVO;
import com.btsl.pretups.gateway.businesslogic.ResponseGatewayVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.superadminVO.MessGatewayVO;
import com.restapi.superadminVO.ReqGatewayVO;
import com.restapi.superadminVO.ResGatewayVO;

public class MessageGatewayWebDAO {
    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private Log _log = LogFactory.getLog(MessageGatewayWebDAO.class.getName());

    /**
     * Method loadClassHandlerList.
     * This method is to load the handler classes of the message gateway type.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_handlerType
     *            String
     * @param p_handlerSubType
     *            String
     * @return ListValueVO
     * @throws BTSLBaseException
     */

    public ListValueVO loadClassHandlerList(Connection p_con, String p_handlerType, String p_handlerSubType) throws BTSLBaseException {
        final String methodName = "loadClassHandlerList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_handlerType : " + p_handlerType);
        	msg.append(", p_handlerSubType : " + p_handlerSubType);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT handler_name, handler_class FROM class_handlers WHERE handler_type=? AND handler_subtype=? ORDER BY handler_name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_handlerType);
            pstmtSelect.setString(2, p_handlerSubType);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("handler_name"), rs.getString("handler_class"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadClassHandlerList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadClassHandlerList]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting:listValueVO : " + listValueVO);
                _log.debug(methodName, msg.toString());
            }
        }
        return listValueVO;
    }

    /**
     * Method loadGatewayTypeList.
     * 
     * @param p_con
     *            Connection
     * @param p_displayAllowed
     *            String
     * @param p_modifiedAllowed
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadGatewayTypeList(Connection p_con, String p_displayAllowed, String p_modifiedAllowed) throws BTSLBaseException {
        final String methodName = "loadGatewayTypeList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_modifiedAllowed : " + p_modifiedAllowed);
        	msg.append(", p_displayAllowed : " + p_displayAllowed);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        ArrayList gatewaySubTypeList = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT gateway_type, gateway_type_name FROM message_gateway_types ");
            selectQuery.append("WHERE display_allowed = ? ");
            if (!BTSLUtil.isNullString(p_modifiedAllowed)) {
                selectQuery.append(" AND modify_allowed=? ");
            }
            selectQuery.append(" ORDER BY gateway_type_name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            int i = 1;
            pstmtSelect.setString(i++, p_displayAllowed);
            if (!BTSLUtil.isNullString(p_modifiedAllowed)) {
                pstmtSelect.setString(i++, p_modifiedAllowed);
            }
            rs = pstmtSelect.executeQuery();
            gatewaySubTypeList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("gateway_type_name"), rs.getString("gateway_type"));
                gatewaySubTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadGatewayTypeList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadGatewayTypeList]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: gatewaySubTypeList.size() : " + gatewaySubTypeList.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return gatewaySubTypeList;
    }

    /**
     * Method loadGatewaySubTypeList.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadGatewaySubTypeList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadGatewaySubTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        ArrayList gatewaySubTypeList = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT gateway_subtype, gateway_type, gateway_subtype_name FROM message_gateway_subtypes ORDER BY gateway_subtype_name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            gatewaySubTypeList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("gateway_subtype_name"), rs.getString("gateway_type") + ":" + rs.getString("gateway_subtype"));
                gatewaySubTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadGatewaySubTypeList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadGatewaySubTypeList]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: gatewaySubTypeList.size() : " + gatewaySubTypeList.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return gatewaySubTypeList;
    }

    /**
     * Method addMessageGateway.
     * This method for the insertion of the record in the MESSAGE_GATEWAY table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_messageGatewayVO
     *            MessageGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addMessageGateway(Connection p_con, MessageGatewayVO p_messageGatewayVO) throws BTSLBaseException {
        final String methodName = "addMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_messageGatewayVO : " + p_messageGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO message_gateway(gateway_code,gateway_name,gateway_type,gateway_subtype, ");
            insertQuery.append("host,protocol,handler_class,network_code,created_on,created_by,modified_on,modified_by,status,req_password_plain) ");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            pstmtInsert.setString(1, p_messageGatewayVO.getGatewayCode());
            pstmtInsert.setString(2, p_messageGatewayVO.getGatewayName());
            pstmtInsert.setString(3, p_messageGatewayVO.getGatewayType());
            pstmtInsert.setString(4, p_messageGatewayVO.getGatewaySubType());
            pstmtInsert.setString(5, p_messageGatewayVO.getHost());
            pstmtInsert.setString(6, p_messageGatewayVO.getProtocol());
            pstmtInsert.setString(7, p_messageGatewayVO.getHandlerClass());
            pstmtInsert.setString(8, p_messageGatewayVO.getNetworkCode());
            pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_messageGatewayVO.getCreatedOn()));
            pstmtInsert.setString(10, p_messageGatewayVO.getCreatedBy());
            pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_messageGatewayVO.getModifiedOn()));
            pstmtInsert.setString(12, p_messageGatewayVO.getModifiedBy());
            pstmtInsert.setString(13, p_messageGatewayVO.getStatus());
            pstmtInsert.setString(14, p_messageGatewayVO.getReqpasswordtype());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addMessageGateway]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addMessageGateway]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: return : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }

    /**
     * Method addRequestMessageGateway.
     * This method for the insertion of the record in the REQ_MESSAGE_GATEWAY
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_requestGatewayVO
     *            RequestGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addRequestMessageGateway(Connection p_con, RequestGatewayVO p_requestGatewayVO) throws BTSLBaseException {
        final String methodName = "addRequestMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_requestGatewayVO : " + p_requestGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO req_message_gateway(gateway_code,port,service_port,login_id,password, ");
            insertQuery.append("encryption_level,encryption_key,content_type,auth_type,status,created_on, ");
            insertQuery.append("created_by,modified_on,modified_by,underprocess_check_reqd)");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            pstmtInsert.setString(1, p_requestGatewayVO.getGatewayCode());
            pstmtInsert.setString(2, p_requestGatewayVO.getPort());
            pstmtInsert.setString(3, p_requestGatewayVO.getServicePort());
            pstmtInsert.setString(4, p_requestGatewayVO.getLoginID());
            pstmtInsert.setString(5, BTSLUtil.encryptText(p_requestGatewayVO.getPassword()));
            pstmtInsert.setString(6, p_requestGatewayVO.getEncryptionLevel());
            pstmtInsert.setString(7, p_requestGatewayVO.getEncryptionKey());
            pstmtInsert.setString(8, p_requestGatewayVO.getContentType());
            pstmtInsert.setString(9, p_requestGatewayVO.getAuthType());
            pstmtInsert.setString(10, p_requestGatewayVO.getStatus());
            pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_requestGatewayVO.getCreatedOn()));
            pstmtInsert.setString(12, p_requestGatewayVO.getCreatedBy());
            pstmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(p_requestGatewayVO.getModifiedOn()));
            pstmtInsert.setString(14, p_requestGatewayVO.getModifiedBy());
            pstmtInsert.setString(15, p_requestGatewayVO.getUnderProcessCheckReqd());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addRequestMessageGateway]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addRequestMessageGateway]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: return : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }

    /**
     * Method addResponseMessageGateway.
     * This method for the insertion of the record in the RES_MESSAGE_GATEWAY
     * table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_responseGatewayVO
     *            ResponseGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addResponseMessageGateway(Connection p_con, ResponseGatewayVO p_responseGatewayVO) throws BTSLBaseException {
        final String methodName = "addResponseMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_responseGatewayVO : " + p_responseGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO res_message_gateway(gateway_code,port,service_port,login_id,password, ");
            insertQuery.append("dest_no,status,path,timeout,created_on,created_by,modified_on,modified_by) ");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            pstmtInsert.setString(1, p_responseGatewayVO.getGatewayCode());
            pstmtInsert.setString(2, p_responseGatewayVO.getPort());
            pstmtInsert.setString(3, p_responseGatewayVO.getServicePort());
            pstmtInsert.setString(4, p_responseGatewayVO.getLoginID());
            pstmtInsert.setString(5, BTSLUtil.encryptText(p_responseGatewayVO.getPassword()));
            pstmtInsert.setString(6, p_responseGatewayVO.getDestNo());
            pstmtInsert.setString(7, p_responseGatewayVO.getStatus());
            pstmtInsert.setString(8, p_responseGatewayVO.getPath());
            pstmtInsert.setInt(9, p_responseGatewayVO.getTimeOut());
            pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_responseGatewayVO.getCreatedOn()));
            pstmtInsert.setString(11, p_responseGatewayVO.getCreatedBy());
            pstmtInsert.setTimestamp(12, BTSLUtil.getTimestampFromUtilDate(p_responseGatewayVO.getModifiedOn()));
            pstmtInsert.setString(13, p_responseGatewayVO.getModifiedBy());

            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addResponseMessageGateway]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addResponseMessageGateway]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: return : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }

    /**
     * Method updateMessageGateway.
     * 
     * @param p_con
     *            Connection
     * @param p_messageGatewayVO
     *            MessageGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateMessageGateway(Connection p_con, MessageGatewayVO p_messageGatewayVO) throws BTSLBaseException {
        final String methodName = "updateMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_messageGatewayVO : " + p_messageGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuffer updateQuery = new StringBuffer();
            updateQuery
                .append("UPDATE message_gateway SET gateway_name=?,gateway_type=?,gateway_subtype=?,host=?,protocol=?,handler_class=?,modified_on=?,modified_by=?,req_password_plain=? ");
            updateQuery.append("WHERE gateway_code=? ");
            final String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);
            pstmtUpdate.setString(1, p_messageGatewayVO.getGatewayName());
            pstmtUpdate.setString(2, p_messageGatewayVO.getGatewayType());
            pstmtUpdate.setString(3, p_messageGatewayVO.getGatewaySubType());
            pstmtUpdate.setString(4, p_messageGatewayVO.getHost());
            pstmtUpdate.setString(5, p_messageGatewayVO.getProtocol());
            pstmtUpdate.setString(6, p_messageGatewayVO.getHandlerClass());
            pstmtUpdate.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_messageGatewayVO.getModifiedOn()));
            pstmtUpdate.setString(8, p_messageGatewayVO.getModifiedBy());
            pstmtUpdate.setString(9, p_messageGatewayVO.getReqpasswordtype());
            pstmtUpdate.setString(10, p_messageGatewayVO.getGatewayCode());
            final boolean modified = this.isRecordModified(p_con, p_messageGatewayVO.getLastModifiedTime(), p_messageGatewayVO.getGatewayCode(), 1);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateMessageGateway]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateMessageGateway]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Method updateRequestMessageGateway.
     * This method for the updateion of the record in the REQ_MESSAGE_GATEWAY
     * table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_requestGatewayVO
     *            RequestGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateRequestMessageGateway(Connection p_con, RequestGatewayVO p_requestGatewayVO) throws BTSLBaseException {
        final String methodName = "updateRequestMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_requestGatewayVO : " + p_requestGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        int i = 1;
        try {
            final StringBuffer updateQuery = new StringBuffer();
            /*
             * done by ashishT
             * done during hashing implementation changes.
             * before updating the database table checking the condition for
             * updating password in db depending upon updatepassword field.
             */

            updateQuery
                .append("UPDATE req_message_gateway SET port=?,service_port=?,login_id=?,encryption_level=?,encryption_key=?,content_type=?,auth_type=?,status=?,modified_on=?,modified_by=?,underprocess_check_reqd=? ");
            if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(p_requestGatewayVO.getUpdatePassword())) {
                updateQuery.append(",password=?");
            }

            updateQuery.append(" WHERE gateway_code=?");
            final String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);
            pstmtUpdate.setString(i++, p_requestGatewayVO.getPort());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getServicePort());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getLoginID());

            pstmtUpdate.setString(i++, p_requestGatewayVO.getEncryptionLevel());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getEncryptionKey());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getContentType());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getAuthType());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_requestGatewayVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_requestGatewayVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getUnderProcessCheckReqd());
            if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(p_requestGatewayVO.getUpdatePassword())) {
                pstmtUpdate.setString(i++, BTSLUtil.encryptText(p_requestGatewayVO.getPassword()));
                pstmtUpdate.setString(i++, p_requestGatewayVO.getGatewayCode());
            } else {
                pstmtUpdate.setString(i++, p_requestGatewayVO.getGatewayCode());
            }
            final boolean modified = this.isRecordModified(p_con, p_requestGatewayVO.getLastModifiedTime(), p_requestGatewayVO.getGatewayCode(), 2);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateRequestMessageGateway]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateRequestMessageGateway]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Method updateResponseMessageGateway.
     * This method for the update of the record in the RES_MESSAGE_GATEWAY table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_responseGatewayVO
     *            ResponseGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateResponseMessageGateway(Connection p_con, ResponseGatewayVO p_responseGatewayVO) throws BTSLBaseException {
        final String methodName = "updateResponseMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_responseGatewayVO : " + p_responseGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        int i = 1;
        try {
            final StringBuffer updateQuery = new StringBuffer();

            updateQuery.append("UPDATE res_message_gateway SET port=?,service_port=?,login_id=?,dest_no=?,status=?,path=?,timeout=?,modified_on=?,modified_by=? ");
            if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(p_responseGatewayVO.getUpdatePassword())) {
                updateQuery.append(",password=?");
            }

            updateQuery.append(" WHERE gateway_code=? ");
            final String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);
            pstmtUpdate.setString(i++, p_responseGatewayVO.getPort());
            pstmtUpdate.setString(i++, p_responseGatewayVO.getServicePort());
            pstmtUpdate.setString(i++, p_responseGatewayVO.getLoginID());

            pstmtUpdate.setString(i++, p_responseGatewayVO.getDestNo());
            pstmtUpdate.setString(i++, p_responseGatewayVO.getStatus());
            pstmtUpdate.setString(i++, p_responseGatewayVO.getPath());
            pstmtUpdate.setInt(i++, p_responseGatewayVO.getTimeOut());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_responseGatewayVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_responseGatewayVO.getModifiedBy());
            if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(p_responseGatewayVO.getUpdatePassword())) {
                pstmtUpdate.setString(i++, BTSLUtil.encryptText(p_responseGatewayVO.getPassword()));
                pstmtUpdate.setString(i++, p_responseGatewayVO.getGatewayCode());
            } else {
                pstmtUpdate.setString(i++, p_responseGatewayVO.getGatewayCode());
            }
            final boolean modified = this.isRecordModified(p_con, p_responseGatewayVO.getLastModifiedTime(), p_responseGatewayVO.getGatewayCode(), 3);
            if (modified) {
                throw new BTSLBaseException(this, "updateRequestMessageGateway", "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateResponseMessageGateway]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateResponseMessageGateway]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Method addMessageGatewayMapping.
     * This method is to insert the new record in the message gateway mapping
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_resCode
     *            String
     * @param p_reqCode
     *            String
     * @param p_altCode
     *            String
     * @param p_modifiedOn
     *            java.util.Date
     * @return int
     * @throws BTSLBaseException
     */
    public int addMessageGatewayMapping(Connection p_con, String p_resCode, String p_reqCode, String p_altCode, Date p_modifiedOn) throws BTSLBaseException {
        final String methodName = "addMessageGatewayMapping";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_reqCode : " + p_reqCode);
        	msg.append(", p_altCode : " + p_altCode);
        	msg.append(", p_resCode : " +  p_resCode);
        	msg.append(", p_modifiedOn : " + p_modifiedOn);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO message_req_resp_mapping(req_code,res_code,alt_code,modified_on) ");
            insertQuery.append("VALUES(?,?,?,?)");
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            pstmtInsert.setString(1, p_reqCode);
            pstmtInsert.setString(2, p_resCode);
            pstmtInsert.setString(3, p_altCode);
            pstmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));

            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addMessageGatewayMapping]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addMessageGatewayMapping]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: addCount : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }

    /**
     * Method loadMessageGatewayList.
     * this method is to load the list of all the message gateway of the
     * specified network code
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadMessageGatewayList(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadMessageGatewayList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_networkCode : " + p_networkCode);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList messageGatewayList = null;
        MessageGatewayVO messageGatewayVO = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT gateway_code,gateway_name,gateway_type,gateway_subtype,protocol,handler_class,host,modified_on,req_password_plain ");
            selectQuery.append("FROM message_gateway ");
            selectQuery.append("WHERE network_code=? AND status='Y'");
            selectQuery.append("ORDER BY gateway_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_networkCode);
            rs = pstmtSelect.executeQuery();
            messageGatewayList = new ArrayList();
            while (rs.next()) {
                messageGatewayVO = new MessageGatewayVO();
                messageGatewayVO.setGatewayCode(rs.getString("gateway_code"));
                messageGatewayVO.setGatewayName(rs.getString("gateway_name"));
                messageGatewayVO.setGatewayType(rs.getString("gateway_type"));
                messageGatewayVO.setGatewaySubType(rs.getString("gateway_subtype"));
                messageGatewayVO.setProtocol(rs.getString("protocol"));
                messageGatewayVO.setHandlerClass(rs.getString("handler_class"));
                messageGatewayVO.setHost(rs.getString("host"));
                messageGatewayVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                messageGatewayVO.setReqpasswordtype(rs.getString("req_password_plain"));
                messageGatewayList.add(messageGatewayVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadMessageGatewayList()", "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMessageGatewayList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMessageGatewayList]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: messageGatewayList size : " + messageGatewayList.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return messageGatewayList;
    }

    /**
     * Method loadRequestMessageGateway.
     * this method is to load the data of request message gateway of the
     * specified gateway code
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_gatewayCode
     *            String
     * @return RequestGatewayVO
     * @throws BTSLBaseException
     */
    public RequestGatewayVO loadRequestMessageGateway(Connection p_con, String p_gatewayCode) throws BTSLBaseException {
        final String methodName = "loadRequestMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_gatewayCode : " + p_gatewayCode);
            _log.debug(methodName, msg.toString());

        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RequestGatewayVO requestGatewayVO = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery
                .append("SELECT port,service_port,login_id,password,encryption_level,encryption_key,content_type,auth_type,status,modified_on,underprocess_check_reqd ");
            selectQuery.append("FROM req_message_gateway ");
            selectQuery.append("WHERE gateway_code=? AND (status='Y' OR status='S') ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_gatewayCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                requestGatewayVO = new RequestGatewayVO();
                requestGatewayVO.setAuthType(rs.getString("auth_type"));
                requestGatewayVO.setContentType(rs.getString("content_type"));
                requestGatewayVO.setPort(rs.getString("port"));
                requestGatewayVO.setPassword(BTSLUtil.decryptText(rs.getString("password")));
                requestGatewayVO.setOldPassword(BTSLUtil.decryptText(rs.getString("password")));
                requestGatewayVO.setServicePort(rs.getString("service_port"));
                requestGatewayVO.setLoginID(rs.getString("login_id"));
                requestGatewayVO.setEncryptionLevel(rs.getString("encryption_level"));
                requestGatewayVO.setEncryptionKey(rs.getString("encryption_key"));
                requestGatewayVO.setStatus(rs.getString("status"));
                requestGatewayVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                requestGatewayVO.setUnderProcessCheckReqd(rs.getString("underprocess_check_reqd"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadRequestMessageGateway]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadRequestMessageGateway]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: requestGatewayVO : " + requestGatewayVO);
                _log.debug(methodName, msg.toString());
            }
        }
        return requestGatewayVO;
    }

    /**
     * Method loadResponseMessageGateway.
     * this method is to load the data of response message gateway of the
     * specified gateway code
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_gatewayCode
     *            String
     * @return ResponseGatewayVO
     * @throws BTSLBaseException
     */
    public ResponseGatewayVO loadResponseMessageGateway(Connection p_con, String p_gatewayCode) throws BTSLBaseException {
        final String methodName = "loadResponseMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_gatewayCode : " + p_gatewayCode);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ResponseGatewayVO responseGatewayVO = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT port,service_port,login_id,password,dest_no,status,modified_on,path,timeout ");
            selectQuery.append("FROM res_message_gateway ");
            selectQuery.append("WHERE gateway_code=? AND (status='Y' OR status='S') ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_gatewayCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                responseGatewayVO = new ResponseGatewayVO();
                responseGatewayVO.setPort(rs.getString("port"));
                responseGatewayVO.setPassword(BTSLUtil.decryptText(rs.getString("password")));
                responseGatewayVO.setOldPassword(BTSLUtil.decryptText(rs.getString("password")));
                responseGatewayVO.setServicePort(rs.getString("service_port"));
                responseGatewayVO.setLoginID(rs.getString("login_id"));
                responseGatewayVO.setStatus(rs.getString("status"));
                responseGatewayVO.setDestNo(rs.getString("dest_no"));
                responseGatewayVO.setPath(rs.getString("path"));
                responseGatewayVO.setTimeOut(rs.getInt("timeout"));
                responseGatewayVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadResponseMessageGateway]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadResponseMessageGateway]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: responseGatewayVO : " + responseGatewayVO);
                _log.debug(methodName, msg.toString());
            }
        }
        return responseGatewayVO;
    }

    /**
     * Method isResponseMessageGatewayExist.
     * this method is to check that is response message gateway exist of the
     * specified gateway code
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_gatewayCode
     *            String
     * @param p_status
     *            TODO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isResponseMessageGatewayExist(Connection p_con, String p_gatewayCode, String p_status) throws BTSLBaseException {
        final String methodName = "isResponseMessageGatewayExist";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_gatewayCode : " + p_gatewayCode);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean status = false;
        try {
            final StringBuffer selectQuery = new StringBuffer("SELECT 1 FROM res_message_gateway WHERE UPPER(gateway_code) = UPPER(?)  ");
            if (!BTSLUtil.isNullString(p_status)) {
                selectQuery.append(" AND status=? ");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            int i = 1;
            pstmtSelect.setString(i++, p_gatewayCode);
            if (!BTSLUtil.isNullString(p_status)) {
                pstmtSelect.setString(i++, p_status);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                status = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isResponseMessageGatewayExist]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isResponseMessageGatewayExist]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: status : " + status);
                _log.debug(methodName, msg.toString());
            }
        }
        return status;
    }

    /**
     * Method isRequestMessageGatewayExist.
     * this method is to check that is request message gateway exist of the
     * specified gateway code
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_gatewayCode
     *            String
     * @param p_status
     *            TODO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isRequestMessageGatewayExist(Connection p_con, String p_gatewayCode, String p_status) throws BTSLBaseException {
        final String methodName = "isRequestMessageGatewayExist";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_gatewayCode : " + p_gatewayCode);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean status = false;
        try {
            final StringBuffer selectQuery = new StringBuffer("SELECT 1 FROM req_message_gateway WHERE UPPER(gateway_code) = UPPER(?)  ");

            if (!BTSLUtil.isNullString(p_status)) {
                selectQuery.append(" AND status=? ");
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            int i = 1;
            pstmtSelect.setString(i++, p_gatewayCode);
            if (!BTSLUtil.isNullString(p_status)) {
                pstmtSelect.setString(i++, p_status);
            }

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                status = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isRequestMessageGatewayExist]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isRequestMessageGatewayExist]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: status : " + status);
                _log.debug(methodName, msg.toString());
            }
        }
        return status;
    }

    /**
     * Method isMessageGatewayExist.
     * This method is to check the existance to the gateway code for the
     * uniqueness of the key
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_gatewayCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isMessageGatewayExist(Connection p_con, String p_gatewayCode) throws BTSLBaseException {
        final String methodName = "isMessageGatewayExist";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_gatewayCode : " + p_gatewayCode);
            _log.debug(methodName, msg.toString());

        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        String query;
        try {
            query = "SELECT 1 FROM message_gateway WHERE UPPER(gateway_code) = UPPER(?)  ";
            if (_log.isDebugEnabled()) {
                _log.debug("isMessageGatewayExist()", "QUERY=" + query);
            }
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_gatewayCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayExist]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isExist : " + isExist);
                _log.debug(methodName, msg.toString());
            }
        }// end of finally
        return isExist;
    }// end isMessageGatewayExist

    /**
     * Method isMessageGatewayMappingExist.
     * This method is to check the existance to the Request gateway code for the
     * uniqueness of the key
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_reqGatewayCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isMessageGatewayMappingExist(Connection p_con, String p_reqGatewayCode) throws BTSLBaseException {
        final String methodName = "isMessageGatewayMappingExist";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_reqGatewayCode : " + p_reqGatewayCode);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        String query;
        try {
            query = "SELECT 1 FROM message_req_resp_mapping WHERE UPPER(req_code) = UPPER(?) ";
            if (_log.isDebugEnabled()) {
                _log.debug("isMessageGatewayMappingExist()", "QUERY=" + query);
            }
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_reqGatewayCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayMappingExist]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayMappingExist]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isExist : " + isExist);
                _log.debug(methodName, msg.toString());

            }
        }// end of finally
        return isExist;
    }// end isMessageGatewayMappingExist

    /**
     * Method isMessageGatewayMappingExist.
     * This method is to check the existance to the gateway code for the
     * uniqueness of the key
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_reqGatewayCode
     *            String
     * @param p_resGatewayCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isMessageGatewayMappingExist(Connection p_con, String p_reqGatewayCode, String p_resGatewayCode) throws BTSLBaseException {
        final String methodName = "isMessageGatewayMappingExist";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_reqGatewayCode : " + p_reqGatewayCode);
        	msg.append(", p_resGatewayCode : " + p_resGatewayCode);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        String query;
        try {
            query = "SELECT 1 FROM message_req_resp_mapping WHERE UPPER(req_code)=UPPER(?) OR UPPER(res_code)=UPPER(?) OR UPPER(alt_code)=UPPER(?) ";
            if (_log.isDebugEnabled()) {
                _log.debug("isMessageGatewayMappingExist()", "QUERY=" + query);
            }
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_reqGatewayCode);
            pstmtSelect.setString(2, p_resGatewayCode);
            pstmtSelect.setString(3, p_resGatewayCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayMappingExist]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayMappingExist]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isExist : " + isExist);
                _log.debug(methodName, msg.toString());
            }
        }// end of finally
        return isExist;
    }// end isMessageGatewayMappingExist

    /**
     * Method isRecordModified.
     * 
     * @author sandeep.goel
     * @modified by:Amit
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            long
     * @param p_gatewayCode
     *            String
     * @param p_tableFlag
     *            int
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_gatewayCode, int p_tableFlag) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_oldlastModified : " + p_oldlastModified);
        	msg.append(", p_gatewayCode : " + p_gatewayCode);
        	msg.append(", p_tableFlag : " +  p_tableFlag);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified;
        if (p_tableFlag == 1) {
            sqlRecordModified = "SELECT modified_on FROM message_gateway WHERE gateway_code=? ";
        } else if (p_tableFlag == 2) {
            sqlRecordModified = "SELECT modified_on FROM req_message_gateway WHERE gateway_code=? ";
        } else if (p_tableFlag == 3) {
            sqlRecordModified = "SELECT modified_on FROM res_message_gateway WHERE gateway_code=? ";
        } else {
            // if(p_tableFlag==4)//for table name MESSAGE_REQ_RESP_MAPPING
            sqlRecordModified = "SELECT modified_on FROM message_req_resp_mapping WHERE req_code=? ";
        }

        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            pstmtSelect = p_con.prepareStatement(sqlRecordModified);
            pstmtSelect.setString(1, p_gatewayCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record may be deleted
            // during the transaction .
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isRecordModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isRecordModified]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: modified : " + modified);
                _log.debug(methodName, msg.toString());
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method loadRequestGateway.
     * This method is used to load the request gateway code and name
     * 
     * @author amit.ruwali
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadRequestGateway(Connection p_con) throws BTSLBaseException {

        final String methodName = "loadRequestGateway";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList arrRequestGatewayList = null;
        ArrayList mappingList = null;
        MessageGatewayMappingVO messageGatewayVO = null;

        try {
            arrRequestGatewayList = new ArrayList();
            mappingList = new ArrayList();
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT DISTINCT mg.gateway_name,rqmg.gateway_code");
            selectQuery.append(" FROM message_gateway mg,req_message_gateway rqmg");
            selectQuery.append(" WHERE mg.status <> 'N' AND mg.gateway_code=rqmg.gateway_code ORDER BY mg.gateway_name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            // Load all the rows from mapping table in the arraylist of vo's
            mappingList = this.loadMappingDetails(p_con);
            String reqGatewayCode;

            while (rs.next()) {
                reqGatewayCode = rs.getString("gateway_code");
                if ((messageGatewayVO = this.getRequestGatewayCode(mappingList, reqGatewayCode)) != null) {
                    messageGatewayVO.setResponseGatewayCode(messageGatewayVO.getResponseGatewayCode());
                    messageGatewayVO.setAltresponseGatewayCode(messageGatewayVO.getAltresponseGatewayCode());
                    messageGatewayVO.setResponseGatewayName(messageGatewayVO.getResponseGatewayName());
                    messageGatewayVO.setAltresponseGatewayName(messageGatewayVO.getAltresponseGatewayName());
                } else {
                    messageGatewayVO = new MessageGatewayMappingVO();
                    // No record found in mapping table now record is avaliable
                    // for insertion
                    messageGatewayVO.setModifyFlag("false");
                }
                messageGatewayVO.setRequestGatewayCode(reqGatewayCode);
                messageGatewayVO.setRequestGatewayName(rs.getString("gateway_name"));
                arrRequestGatewayList.add(messageGatewayVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadRequestGateway]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadRequestGateway]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: list size : " + arrRequestGatewayList.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return arrRequestGatewayList;
    }

    /**
     * Method getRequestGatewayCode.
     * This method is used for default selection of Response gateway and
     * alternate
     * response gateway combo if the request gateway code exists in
     * message_req_resp_mapping table
     * 
     * @param mappingList
     *            ArrayList
     * @param requestGatewayCode
     *            String
     * @return MessageGatewayMappingVO
     * @throws Exception
     */

    private MessageGatewayMappingVO getRequestGatewayCode(ArrayList mappingList, String requestGatewayCode) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("getRequestGatewayCode", "Entered");
        }

        MessageGatewayMappingVO messageGatewayVO = null;
        final Iterator itr = mappingList.iterator();
        StringBuffer msg=new StringBuffer("");
        while (itr.hasNext()) {
            messageGatewayVO = (MessageGatewayMappingVO) itr.next();
            if (messageGatewayVO.getRequestGatewayCode().equalsIgnoreCase(requestGatewayCode)) {
                if (_log.isDebugEnabled()) {
                	msg.setLength(0);
                	msg.append("Exiting: VO : " + messageGatewayVO);
                    _log.debug("getRequestGatewayCode", msg.toString());
                }
                // Record exists in the mapping table so the record is avaliable
                // for updation
                messageGatewayVO.setModifyFlag("true");
                return messageGatewayVO;
            }
        }

        if (_log.isDebugEnabled()) {
        	msg.setLength(0);
        	msg.append("Exiting: VO : " + messageGatewayVO);
            _log.debug("getRequestGatewayCode", msg.toString());
        }
        return null;
    }

    /**
     * Method loadMappingDetails.
     * This method is used to load mapping details from message_req_resp_mapping
     * table
     * and set it into ArrayList of VO's
     * 
     * @author amit.ruwali
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadMappingDetails(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadMappingDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        ArrayList mappingList = null;
        final String selectQuery = "SELECT req_code,res_code,alt_code,modified_on FROM message_req_resp_mapping";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query=" + selectQuery);
        }

        try {
            mappingList = new ArrayList();
            try (PreparedStatement pstmt = p_con.prepareStatement(selectQuery);){
            	try (ResultSet rs = pstmt.executeQuery();){
		            MessageGatewayMappingVO mappingVO = null;
		            while (rs.next()) {
		                mappingVO = new MessageGatewayMappingVO();
		                mappingVO.setRequestGatewayCode(rs.getString("req_code"));
		                mappingVO.setResponseGatewayCode(rs.getString("res_code"));
		                mappingVO.setAltresponseGatewayCode(rs.getString("alt_code"));
		                mappingVO.setLastModified(rs.getTimestamp("modified_on").getTime());
		                mappingList.add(mappingVO);
		            }
		            OracleUtil.closeQuietly(rs);
            	}
            	OracleUtil.closeQuietly(pstmt);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMappingDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMappingDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: list size : " + mappingList.size());
	            _log.debug(methodName, msg.toString());
            }
        }
        return mappingList;
    }

    /**
     * Method loadResponseGateway.
     * This method is used to load the response gateway code and name
     * 
     * @author amit.ruwali
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadResponseGateway(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadResponseGateway";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList arrRequestGatewayList = null;
        try {
            arrRequestGatewayList = new ArrayList();
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT DISTINCT mg.gateway_name,rsmg.gateway_code");
            selectQuery.append(" FROM message_gateway mg,res_message_gateway rsmg");
            selectQuery.append(" WHERE mg.status <> 'N' AND mg.gateway_code=rsmg.gateway_code ORDER BY mg.gateway_name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                arrRequestGatewayList.add(new ListValueVO(rs.getString("gateway_name") + "(" + rs.getString("gateway_code") + ")".trim(), rs.getString("gateway_code")));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadResponseGateway]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadResponseGateway]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: list size : " + arrRequestGatewayList.size());
	            _log.debug(methodName, msg.toString());
            }
        }
        return arrRequestGatewayList;
    }

    /**
     * Method saveGatewayMapping.
     * This method is used to save the request and response gateway mapping in
     * the
     * message_req_resp_gateway table and if the record already exists update
     * the
     * record
     * 
     * @author amit.ruwali
     * @param p_con
     *            Connection
     * @param p_gatewayMappingList
     *            ArrayList
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public int saveGatewayMapping(Connection p_con, ArrayList p_gatewayMappingList) throws BTSLBaseException {
        final String methodName = "saveGatewayMapping";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_gatewayMappingList size : " + p_gatewayMappingList.size());
            _log.debug(methodName, msg.toString());
        }

        int listSize = 0;
        int saveCount = -1;
        final ResultSet rs = null;
        PreparedStatement pstmt = null;
        int updateCount = -1;
        int updatedRecords = 0;

        // Check for the existense of the request gateway code in the mapping
        // table
        // if the entry didn't exists fire the insert query else fire update
        // query.
        try {

            if (p_gatewayMappingList != null) {
                listSize = p_gatewayMappingList.size();
            }

            StringBuffer sqlQuery = new StringBuffer();
            for (int count = 0; count < listSize; count++) {
                final MessageGatewayMappingVO mappingVO = (MessageGatewayMappingVO) p_gatewayMappingList.get(count);
                if ("true".equalsIgnoreCase(mappingVO.getModifyFlag())) {
                    // Request is for modify
                	sqlQuery.setLength(0);
                    sqlQuery.append("UPDATE message_req_resp_mapping");
                    sqlQuery.append(" SET res_code=?,alt_code=?,modified_on=? WHERE req_code=?");
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Update Query=" + sqlQuery.toString());
                    }
                    pstmt = p_con.prepareStatement(sqlQuery.toString());
                    pstmt.setString(1, mappingVO.getResponseGatewayCode());
                    pstmt.setString(2, mappingVO.getAltresponseGatewayCode());
                    pstmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(mappingVO.getModifiedOn()));
                    pstmt.setString(4, mappingVO.getRequestGatewayCode());
                    updateCount = pstmt.executeUpdate();
                    pstmt.clearParameters();

                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Update Query Executed updateCount=" + updateCount);
                    }

                    if (updateCount > 0) {
                        updatedRecords++;
                    }
                }

                else if ("false".equalsIgnoreCase(mappingVO.getModifyFlag())) {
                    // request is for insert
                	sqlQuery.setLength(0);
                    sqlQuery.append("INSERT INTO message_req_resp_mapping");
                    sqlQuery.append("(req_code,res_code,alt_code,modified_on) VALUES(?,?,?,?)");
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Insert Query=" + sqlQuery.toString());
                    }
                    pstmt = p_con.prepareStatement(sqlQuery.toString());
                    pstmt.setString(1, mappingVO.getRequestGatewayCode());
                    pstmt.setString(2, mappingVO.getResponseGatewayCode());
                    pstmt.setString(3, mappingVO.getAltresponseGatewayCode());
                    pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(mappingVO.getModifiedOn()));
                    updateCount = pstmt.executeUpdate();
                    pstmt.clearParameters();
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Insert Query Executed updateCount=" + updateCount);
                    }
                    if (updateCount > 0) {
                        updatedRecords++;
                    }
                }

            } // end for loop
            if (p_gatewayMappingList != null && updatedRecords == p_gatewayMappingList.size()) {
                saveCount = 1;
            } else {
                saveCount = 0;
            }
        } // end try

        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[saveGatewayMapping]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[saveGatewayMapping]", "", "", "",
                "Exception:" + e.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: saveCount : " + saveCount);
	            _log.debug(methodName, msg.toString());
            }
        }

        return saveCount;

    }

    /**
     * Method deleteMapping.
     * 
     * @param p_con
     *            Connection
     * @param p_arrVO
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteMapping(Connection p_con, ArrayList p_arrVO) throws BTSLBaseException {
        final String methodName = "deleteMapping";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_arrVO : " + p_arrVO);
            _log.debug(methodName, msg.toString());
        }

        PreparedStatement pstmtDelete = null;
        int deleteCount = 0;
        int deleteListSize;
        try {
            deleteListSize = p_arrVO.size();
            boolean modified = false;
            int count = 0;
            for (int i = 0; i < deleteListSize; i++) {
                final MessageGatewayMappingVO mappingVO = (MessageGatewayMappingVO) p_arrVO.get(i);
                modified = this.isRecordModified(p_con, mappingVO.getLastModified(), mappingVO.getRequestGatewayCode(), 4);

                // if modified = true mens record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                } else {
                    count++;
                }
            }
            count = p_arrVO.size();

            // if count== p_voList means no record is updated
            if ((count == p_arrVO.size())) {
                count = 0;
                final StringBuffer deleteQuery = new StringBuffer("DELETE FROM message_req_resp_mapping");
                deleteQuery.append(" WHERE req_code IN(?)");
                final String delQuery = deleteQuery.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query delQuery:" + delQuery);
                }

                pstmtDelete = p_con.prepareStatement(delQuery);
                for (int i = 0; i < deleteListSize; i++) {
                    final MessageGatewayMappingVO mappingVO = (MessageGatewayMappingVO) p_arrVO.get(i);
                    pstmtDelete.setString(1, mappingVO.getRequestGatewayCode());
                    deleteCount = pstmtDelete.executeUpdate();
                    pstmtDelete.clearParameters();

                    // check the status of the delete
                    if (deleteCount > 0) {
                        count++;
                    }
                }
                if (count == p_arrVO.size()) {
                    deleteCount = 1;
                } else {
                    deleteCount = 0;
                }
            }
        }// end of try

        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateMessageGateway]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateMessageGateway]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: updateCount : " + deleteCount);
	            _log.debug(methodName, msg.toString());
            }
        }
        return deleteCount;
    }

    /**
     * Method loadMessageGatewayTypeList.
     * this method is to load the list of all the message gateway Type list form
     * message_gateway_types
     * by manoj
     * 
     * @param p_con
     *            Connection\
     * @param p_displayAllowed
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadMessageGatewayTypeList(Connection p_con, String p_displayAllowed) throws BTSLBaseException {
        final String methodName = "loadMessageGatewayTypeList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_displayAllowed : " + p_displayAllowed);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList messageGatewayTypeList = null;
        MessageGatewayVO messageGatewayVO = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT gateway_type,gateway_type_name,access_from ");
            selectQuery.append(" FROM message_gateway_types ");
            selectQuery.append("WHERE display_allowed=?  ORDER BY gateway_type_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_displayAllowed);
            rs = pstmtSelect.executeQuery();
            messageGatewayTypeList = new ArrayList();
            while (rs.next()) {
                messageGatewayVO = new MessageGatewayVO();
                messageGatewayVO.setGatewayType(rs.getString("gateway_type"));
                messageGatewayVO.setGatewayName(rs.getString("gateway_type_name"));
                messageGatewayVO.setAccessFrom(rs.getString("access_from"));
                messageGatewayTypeList.add(messageGatewayVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMessageGatewayTypeList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadMessageGatewayTypeList]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: messageGatewayTypeList size : " + messageGatewayTypeList.size());
	            _log.debug(methodName, msg.toString());
            }
        }
        return messageGatewayTypeList;
    }

    /**
     * Method isMessageGatewayNameExist.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_gatewayName
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isMessageGatewayNameExist(Connection p_con, String p_gatewayCode, String p_gatewayName, boolean p_checkGatewayCode) throws BTSLBaseException {
        final String methodName = "isMessageGatewayNameExist";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_gatewayName : " + p_gatewayName);
        	msg.append(", p_gatewayCode : " + p_gatewayCode);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;

        try {
            final StringBuffer query = new StringBuffer("SELECT 1 FROM message_gateway WHERE  status='Y' AND gateway_name = ? ");

            if (p_checkGatewayCode) {
                query.append("AND gateway_code <> ? ");
            }

            if (_log.isDebugEnabled()) {
                _log.debug("isMessageGatewayNameExist()", "QUERY=" + query);
            }
            pstmtSelect = p_con.prepareStatement(query.toString());
            pstmtSelect.setString(1, p_gatewayName);
            if (p_checkGatewayCode) {
                pstmtSelect.setString(2, p_gatewayCode);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayNameExist]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[isMessageGatewayNameExist]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: isExist : " + isExist);
	            _log.debug(methodName, msg.toString());
            }
        }// end of finally
        return isExist;
    }// end isMessageGatewayNameExist

    /**
     * Method deleteMessageGateway.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_messageGatewayVO
     *            MessageGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteMessageGateway(Connection p_con, MessageGatewayVO p_messageGatewayVO) throws BTSLBaseException {
        final String methodName = "deleteMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_messageGatewayVO : " + p_messageGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE message_gateway SET status=?,modified_on=?,modified_by=? ");
            updateQuery.append("WHERE gateway_code=? ");
            final String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);
            pstmtUpdate.setString(1, p_messageGatewayVO.getStatus());
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_messageGatewayVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_messageGatewayVO.getModifiedBy());
            pstmtUpdate.setString(4, p_messageGatewayVO.getGatewayCode());
            final boolean modified = this.isRecordModified(p_con, p_messageGatewayVO.getLastModifiedTime(), p_messageGatewayVO.getGatewayCode(), 1);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[deleteMessageGateway]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[deleteMessageGateway]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: updateCount : " + updateCount);
	            _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /*****************************************/
    /**
     * Method loadGatewayList.
     * 
     * @author Ashutosh
     * @param p_con
     *            Connection
     * @param p_networkCode
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<MessageGatewayVO> loadGatewayList(Connection p_con, String p_networkCode, String p_categoryCode) throws BTSLBaseException {
        final String[] str;
        final String methodName = "loadGatewayList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_networkCode : " + p_networkCode);
        	msg.append(", p_categoryCode : " + p_categoryCode);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("select mg.gateway_code, mg.gateway_type, mg.gateway_name, crgt.category_code from message_gateway mg, CATEGORY_REQ_GTW_TYPES crgt ");
        strBuff
            .append("where mg.gateway_type=crgt.gateway_type and crgt.category_code=CASE ? WHEN 'ALL' THEN category_code ELSE ? END and mg.status ='Y' and mg.network_code = ?");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        MessageGatewayVO msgGateVO = null;MessageGatewayVO msgGateVO1 = null;
        ArrayList<MessageGatewayVO> list = null;
        HashMap hm = new HashMap();
        try {
            int i = 0;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(++i, p_categoryCode);
            pstmt.setString(++i, p_categoryCode);
            pstmt.setString(++i, p_networkCode);
            rs = pstmt.executeQuery();
            list = new ArrayList<MessageGatewayVO>();
            
            while (rs.next()) {
                msgGateVO = new MessageGatewayVO();
                msgGateVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                msgGateVO.setGatewayCode(SqlParameterEncoder.encodeParams(rs.getString("gateway_code")));
                msgGateVO.setGatewayType(SqlParameterEncoder.encodeParams(rs.getString("gateway_type")));
                msgGateVO.setGatewayName(SqlParameterEncoder.encodeParams(rs.getString("gateway_name")));
                if(hm.get(msgGateVO.getCategoryCode()) == null){
                	hm.put(msgGateVO.getCategoryCode(),msgGateVO);
                	msgGateVO1 = new MessageGatewayVO();
                    msgGateVO1.setCategoryCode(msgGateVO.getCategoryCode());
                    msgGateVO1.setGatewayCode("ALL");
                    msgGateVO1.setGatewayType(msgGateVO.getGatewayType());
                    msgGateVO1.setGatewayName("ALL");
                    list.add(msgGateVO1);
            	}
                list.add(msgGateVO);
            }
            hm = null;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadGatewayList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[loadGatewayList]", "", "", "",
                "Exception:" + ex.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: gatewayList size() : " + list.size());
	            _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }
    
    
    public ArrayList loadGatewayCodeList(Connection p_con)throws BTSLBaseException
	{
		final String methodName = "loadGatewayCodeList";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered ");
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		ListValueVO listValueVO =null;
		ArrayList gatewaySubTypeList=null;
		try
		{
			StringBuffer selectQuery = new StringBuffer();
			selectQuery.append("SELECT gateway_code, gateway_name FROM message_gateway ");
			selectQuery.append("WHERE status = ? ");
			selectQuery.append("ORDER BY gateway_name");
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Query=" + selectQuery);
			pstmtSelect = p_con.prepareStatement(selectQuery.toString());
			int i=1;
			pstmtSelect.setString(i++,PretupsI.GATEWAY_STATUS_ACTIVE);
			rs = pstmtSelect.executeQuery();
			gatewaySubTypeList = new ArrayList();
			while(rs.next())
			{
				listValueVO =new ListValueVO (rs.getString("gateway_name"),rs.getString("gateway_code"));
				gatewaySubTypeList.add(listValueVO);
			}
		}
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException:"+ sqe.getMessage());
			_log.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"MessageGatewayDAO[loadGatewayCodeList]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
		}
		catch (Exception e)
		{
			_log.error(methodName, "Exception:"+ e.getMessage());
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"MessageGatewayDAO[loadGatewayCodeList]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.processing");
		}
		finally
		{
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
			{
				StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: loadGatewayCodeList.size() : " + gatewaySubTypeList.size());
	            _log.debug(methodName, msg.toString());
			}
		}
		return gatewaySubTypeList;
	}
		
    
    /**
     * Method addMessGateway.
     * This method for the insertion of the record in the MESSAGE_GATEWAY table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_messageGatewayVO
     *            MessageGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addMessGateway(Connection p_con, MessGatewayVO p_messageGatewayVO) throws BTSLBaseException {
        final String methodName = "addMessGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_messageGatewayVO : " + p_messageGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO message_gateway(gateway_code,gateway_name,gateway_type,gateway_subtype, ");
            insertQuery.append("host,protocol,handler_class,network_code,created_on,created_by,modified_on,modified_by,status,req_password_plain) ");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            pstmtInsert.setString(1, p_messageGatewayVO.getGatewayCode());
            pstmtInsert.setString(2, p_messageGatewayVO.getGatewayName());
            pstmtInsert.setString(3, p_messageGatewayVO.getGatewayType());
            pstmtInsert.setString(4, p_messageGatewayVO.getGatewaySubType());
            pstmtInsert.setString(5, p_messageGatewayVO.getHost());
            pstmtInsert.setString(6, p_messageGatewayVO.getProtocol());
            pstmtInsert.setString(7, p_messageGatewayVO.getHandlerClass());
            pstmtInsert.setString(8, p_messageGatewayVO.getNetworkCode());
            pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_messageGatewayVO.getCreatedOn()));
            pstmtInsert.setString(10, p_messageGatewayVO.getCreatedBy());
            pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_messageGatewayVO.getModifiedOn()));
            pstmtInsert.setString(12, p_messageGatewayVO.getModifiedBy());
            pstmtInsert.setString(13, p_messageGatewayVO.getStatus());
            pstmtInsert.setString(14, p_messageGatewayVO.getReqpasswordtype());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addMessageGateway]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addMessageGateway]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: return : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }
    
    /**
     * Method addReqMessageGateway.
     * This method for the insertion of the record in the REQ_MESSAGE_GATEWAY
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_requestGatewayVO
     *            RequestGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addReqMessageGateway(Connection p_con, ReqGatewayVO p_requestGatewayVO) throws BTSLBaseException {
        final String methodName = "addReqMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_requestGatewayVO : " + p_requestGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO req_message_gateway(gateway_code,port,service_port,login_id,password, ");
            insertQuery.append("encryption_level,encryption_key,content_type,auth_type,status,created_on, ");
            insertQuery.append("created_by,modified_on,modified_by,underprocess_check_reqd)");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            pstmtInsert.setString(1, p_requestGatewayVO.getGatewayCode());
            pstmtInsert.setString(2, p_requestGatewayVO.getServicePort());
            pstmtInsert.setString(3, p_requestGatewayVO.getServicePort());
            pstmtInsert.setString(4, p_requestGatewayVO.getLoginID());
            pstmtInsert.setString(5, BTSLUtil.encryptText(p_requestGatewayVO.getPassword()));
            pstmtInsert.setString(6, p_requestGatewayVO.getEncryptionLevel());
            pstmtInsert.setString(7, p_requestGatewayVO.getEncryptionKey());
            pstmtInsert.setString(8, p_requestGatewayVO.getContentType());
            pstmtInsert.setString(9, p_requestGatewayVO.getAuthType());
            pstmtInsert.setString(10, p_requestGatewayVO.getStatus());
            pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_requestGatewayVO.getCreatedOn()));
            pstmtInsert.setString(12, p_requestGatewayVO.getCreatedBy());
            pstmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(p_requestGatewayVO.getModifiedOn()));
            pstmtInsert.setString(14, p_requestGatewayVO.getModifiedBy());
            pstmtInsert.setString(15, p_requestGatewayVO.getUnderProcessCheckReqd());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addRequestMessageGateway]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addRequestMessageGateway]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: return : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }
    
    /**
     * Method addResMessageGateway.
     * This method for the insertion of the record in the RES_MESSAGE_GATEWAY
     * table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_responseGatewayVO
     *            ResponseGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addResMessageGateway(Connection p_con, ResGatewayVO p_responseGatewayVO) throws BTSLBaseException {
        final String methodName = "addResMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_responseGatewayVO : " + p_responseGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO res_message_gateway(gateway_code,port,service_port,login_id,password, ");
            insertQuery.append("dest_no,status,path,timeout,created_on,created_by,modified_on,modified_by) ");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            pstmtInsert.setString(1, p_responseGatewayVO.getGatewayCode());
            pstmtInsert.setString(2, p_responseGatewayVO.getPort());
            pstmtInsert.setString(3, p_responseGatewayVO.getPort());
            pstmtInsert.setString(4, p_responseGatewayVO.getLoginID());
            pstmtInsert.setString(5, BTSLUtil.encryptText(p_responseGatewayVO.getPassword()));
            pstmtInsert.setString(6, p_responseGatewayVO.getDestNo());
            pstmtInsert.setString(7, p_responseGatewayVO.getStatus());
            pstmtInsert.setString(8, p_responseGatewayVO.getPath());
            pstmtInsert.setInt(9, p_responseGatewayVO.getTimeOut());
            pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_responseGatewayVO.getCreatedOn()));
            pstmtInsert.setString(11, p_responseGatewayVO.getCreatedBy());
            pstmtInsert.setTimestamp(12, BTSLUtil.getTimestampFromUtilDate(p_responseGatewayVO.getModifiedOn()));
            pstmtInsert.setString(13, p_responseGatewayVO.getModifiedBy());

            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addResponseMessageGateway]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addResponseMessageGateway]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: return : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }
    
    
    /**
     * Method addMessGatewayMapping.
     * This method is to insert the new record in the message gateway mapping
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_resCode
     *            String
     * @param p_reqCode
     *            String
     * @param p_altCode
     *            String
     * @param p_modifiedOn
     *            java.util.Date
     * @return int
     * @throws BTSLBaseException
     */
    public int addMessGatewayMapping(Connection p_con, String p_resCode, String p_reqCode, String p_altCode, Date p_modifiedOn) throws BTSLBaseException {
        final String methodName = "addMessGatewayMapping";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_reqCode : " + p_reqCode);
        	msg.append(", p_altCode : " + p_altCode);
        	msg.append(", p_resCode : " +  p_resCode);
        	msg.append(", p_modifiedOn : " + p_modifiedOn);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO message_req_resp_mapping(req_code,res_code,alt_code,modified_on) ");
            insertQuery.append("VALUES(?,?,?,?)");
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            pstmtInsert.setString(1, p_reqCode);
            pstmtInsert.setString(2, p_resCode);
            pstmtInsert.setString(3, p_altCode);
            pstmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));

            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addMessageGatewayMapping]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[addMessageGatewayMapping]", "", "",
                "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: addCount : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }
    
    
    /**
     * Method updateReqMessageGateway.
     * This method for the updateion of the record in the REQ_MESSAGE_GATEWAY
     * table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_requestGatewayVO
     *            RequestGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateReqMessageGateway(Connection p_con, ReqGatewayVO p_requestGatewayVO) throws BTSLBaseException {
        final String methodName = "updateRequestMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_requestGatewayVO : " + p_requestGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        int i = 1;
        try {
            final StringBuffer updateQuery = new StringBuffer();
            /*
             * done by ashishT
             * done during hashing implementation changes.
             * before updating the database table checking the condition for
             * updating password in db depending upon updatepassword field.
             */

            updateQuery
                .append("UPDATE req_message_gateway SET port=?,service_port=?,login_id=?,encryption_level=?,encryption_key=?,content_type=?,auth_type=?,status=?,modified_on=?,modified_by=?,underprocess_check_reqd=? ");
            if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(p_requestGatewayVO.getUpdatePassword())) {
                updateQuery.append(",password=?");
            }

            updateQuery.append(" WHERE gateway_code=?");
            final String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);
            pstmtUpdate.setString(i++, p_requestGatewayVO.getServicePort());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getServicePort());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getLoginID());

            pstmtUpdate.setString(i++, p_requestGatewayVO.getEncryptionLevel());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getEncryptionKey());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getContentType());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getAuthType());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_requestGatewayVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_requestGatewayVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_requestGatewayVO.getUnderProcessCheckReqd());
            if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(p_requestGatewayVO.getUpdatePassword())) {
                pstmtUpdate.setString(i++, BTSLUtil.encryptText(p_requestGatewayVO.getPassword()));
                pstmtUpdate.setString(i++, p_requestGatewayVO.getGatewayCode());
            } else {
                pstmtUpdate.setString(i++, p_requestGatewayVO.getGatewayCode());
            }
            final boolean modified = this.isRecordModified(p_con, p_requestGatewayVO.getLastModifiedTime(), p_requestGatewayVO.getGatewayCode(), 2);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateRequestMessageGateway]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateRequestMessageGateway]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }
    
    /**
     * Method updateMessGateway.
     * 
     * @param p_con
     *            Connection
     * @param p_messageGatewayVO
     *            MessageGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateMessGateway(Connection p_con, MessGatewayVO p_messageGatewayVO) throws BTSLBaseException {
        final String methodName = "updateMessGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_messageGatewayVO : " + p_messageGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuffer updateQuery = new StringBuffer();
            updateQuery
                .append("UPDATE message_gateway SET gateway_name=?,gateway_type=?,gateway_subtype=?,host=?,protocol=?,handler_class=?,modified_on=?,modified_by=?,req_password_plain=? ");
            updateQuery.append("WHERE gateway_code=? ");
            final String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);
            pstmtUpdate.setString(1, p_messageGatewayVO.getGatewayName());
            pstmtUpdate.setString(2, p_messageGatewayVO.getGatewayType());
            pstmtUpdate.setString(3, p_messageGatewayVO.getGatewaySubType());
            pstmtUpdate.setString(4, p_messageGatewayVO.getHost());
            pstmtUpdate.setString(5, p_messageGatewayVO.getProtocol());
            pstmtUpdate.setString(6, p_messageGatewayVO.getHandlerClass());
            pstmtUpdate.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_messageGatewayVO.getModifiedOn()));
            pstmtUpdate.setString(8, p_messageGatewayVO.getModifiedBy());
            pstmtUpdate.setString(9, p_messageGatewayVO.getReqpasswordtype());
            pstmtUpdate.setString(10, p_messageGatewayVO.getGatewayCode());
            final boolean modified = this.isRecordModified(p_con, p_messageGatewayVO.getLastModifiedTime(), p_messageGatewayVO.getGatewayCode(), 1);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateMessageGateway]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateMessageGateway]", "", "", "",
                "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }
    
    
    /**
     * Method updateResMessageGateway.
     * This method for the update of the record in the RES_MESSAGE_GATEWAY table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_responseGatewayVO
     *            ResponseGatewayVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateResMessageGateway(Connection p_con, ResGatewayVO p_responseGatewayVO) throws BTSLBaseException {
        final String methodName = "updateResMessageGateway";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_responseGatewayVO : " + p_responseGatewayVO);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        int i = 1;
        try {
            final StringBuffer updateQuery = new StringBuffer();

            updateQuery.append("UPDATE res_message_gateway SET port=?,service_port=?,login_id=?,dest_no=?,status=?,path=?,timeout=?,modified_on=?,modified_by=? ");
            if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(p_responseGatewayVO.getUpdatePassword())) {
                updateQuery.append(",password=?");
            }

            updateQuery.append(" WHERE gateway_code=? ");
            final String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);
            pstmtUpdate.setString(i++, p_responseGatewayVO.getPort());
            pstmtUpdate.setString(i++, p_responseGatewayVO.getPort());
            pstmtUpdate.setString(i++, p_responseGatewayVO.getLoginID());

            pstmtUpdate.setString(i++, p_responseGatewayVO.getDestNo());
            pstmtUpdate.setString(i++, p_responseGatewayVO.getStatus());
            pstmtUpdate.setString(i++, p_responseGatewayVO.getPath());
            pstmtUpdate.setInt(i++, p_responseGatewayVO.getTimeOut());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_responseGatewayVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_responseGatewayVO.getModifiedBy());
            if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(p_responseGatewayVO.getUpdatePassword())) {
                pstmtUpdate.setString(i++, BTSLUtil.encryptText(p_responseGatewayVO.getPassword()));
                pstmtUpdate.setString(i++, p_responseGatewayVO.getGatewayCode());
            } else {
                pstmtUpdate.setString(i++, p_responseGatewayVO.getGatewayCode());
            }
            final boolean modified = this.isRecordModified(p_con, p_responseGatewayVO.getLastModifiedTime(), p_responseGatewayVO.getGatewayCode(), 3);
            if (modified) {
                throw new BTSLBaseException(this, "updateRequestMessageGateway", "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateResponseMessageGateway]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[updateResponseMessageGateway]", "",
                "", "", "Exception:" + e.getMessage());
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
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    
    /**
     * Method fetchAllowedSourceList.
     * this method is to load the list of all the message gateway Type list form
     * message_gateway_types
     * by manoj
     * 
     * @param p_con
     *            Connection\
     * @param p_displayAllowed
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<AllowedSourceVO> fetchAllowedSourceList(Connection p_con, String p_displayAllowed) throws BTSLBaseException {
        final String methodName = "fetchAllowedSourceList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_displayAllowed : " + p_displayAllowed);
            _log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList messageGatewayTypeList = null;
        AllowedSourceVO allowedSourceVO = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT gateway_type,gateway_type_name,access_from ");
            selectQuery.append(" FROM message_gateway_types ");
            selectQuery.append("WHERE display_allowed=?  ORDER BY gateway_type_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_displayAllowed);
            rs = pstmtSelect.executeQuery();
            messageGatewayTypeList = new ArrayList();
            while (rs.next()) {
            	allowedSourceVO = new AllowedSourceVO();
            	allowedSourceVO.setGatewayType(rs.getString("gateway_type"));
            	allowedSourceVO.setGatewayName(rs.getString("gateway_type_name"));
                messageGatewayTypeList.add(allowedSourceVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[fetchAllowedSourceList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayDAO[fetchAllowedSourceList]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
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
            	StringBuffer msg=new StringBuffer("");
	        	msg.append("Exiting: messageGatewayTypeList size : " + messageGatewayTypeList.size());
	            _log.debug(methodName, msg.toString());
            }
        }
        return messageGatewayTypeList;
    }

    
    
    
    

}
