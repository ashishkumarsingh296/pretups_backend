package com.btsl.pretups.inter.module;

/**
 * @(#)InterfaceModule.java
 *                          Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                          All Rights Reserved
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Abhijit Chauhan June 25,2005 Initial Creation
 *                          ----------------------------------------------------
 *                          --------------------------------------------
 */
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.InterfaceTransactionLog;
// import com.btsl.pretups.interfaces.businesslogic.InterfaceHandler;
import com.btsl.util.BTSLUtil;

public class InterfaceModule implements InterfaceModuleI {

    private HashMap _map = null;
    private String _interfaceID = null;
    private String _interfaceHandler = null;
    private String _transcationID = null;
    private String _action = null;
    private String _adjust = null;
    private String _status = null;
    private String _txnType = null;
    private static String TXN_TYPE_VALIDATE = "VALIDATE";
    private static String TXN_TYPE_CREDIT = "CREDIT";
    private static String TXN_TYPE_CREDITADJUST = "CREDITADJUST";
    private static String TXN_TYPE_DEBITADJUST = "DEBITADJUST";
    private static String TXN_TYPE_VALIDITYADJUST = "VALIDITYADJUST";
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public InterfaceModule() {
    }

    public String process(String p_requestStr) {
        if (_log.isDebugEnabled())
            _log.debug("process", "Entered p_requestStr" + p_requestStr);
        String responseStr = null;
        // String communicationType=null;
        // boolean db=true;
        Connection con = null;
        InterfaceModuleDAO interfaceModuleDAO = null;
        InterfaceModuleVO interfaceModuleVO = null;
        try {
            _map = BTSLUtil.getStringToHash(p_requestStr, "&", "=");
            _transcationID = (String) _map.get("TRANSACTION_ID");
            if (_log.isDebugEnabled())
                _log.debug("process", _transcationID, InterfaceUtil.getPrintMap(_map));
            _interfaceID = (String) _map.get("INTERFACE_ID");
            _interfaceHandler = (String) _map.get("INTERFACE_HANDLER");
            _action = (String) _map.get("INTERFACE_ACTION");
            InterfaceHandler interfaceHandler = getHandlerObj(_interfaceHandler);

            // Validation
            if (_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                interfaceHandler.validate(_map);
                _txnType = TXN_TYPE_VALIDATE;

            }
            // Credit
            else if (_action.equals(PretupsI.INTERFACE_CREDIT_ACTION)) {
                _adjust = BTSLUtil.NullToString((String) _map.get("ADJUST"));
                if (_adjust.equals("Y")) {
                    // credit amount as adjustment
                    _txnType = TXN_TYPE_CREDITADJUST;
                    interfaceHandler.creditAdjust(_map);
                } else {
                    _txnType = TXN_TYPE_CREDIT;
                    interfaceHandler.credit(_map);
                }
            }
            // Debit
            else if (_action.equals(PretupsI.INTERFACE_DEBIT_ACTION)) {
                _txnType = TXN_TYPE_DEBITADJUST;
                interfaceHandler.debitAdjust(_map);
            } else if (_action.equals(PretupsI.INTERFACE_UPDATE_VALIDITY_ACTION)) {
                _txnType = TXN_TYPE_VALIDITYADJUST;
                interfaceHandler.validityAdjust(_map);
            }
            _status = InterfaceErrorCodesI.SUCCESS;
        } catch (BTSLBaseException be) {
            _status = be.getMessage();

        } catch (Exception e) {
            _status = InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION;
            _log.error("process", _transcationID, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceModule[process]", _transcationID, "", "", "Exception in Interface Module:" + e.getMessage());
            e.printStackTrace();
        }
        if (_map != null) {
            _map.put("TRANSACTION_STATUS", _status);
            interfaceModuleVO = InterfaceUtil.getVOFromMap(_txnType, _map, "C");
            InterfaceTransactionLog.log(interfaceModuleVO);
            responseStr = BTSLUtil.getStringFromHash(_map);
        }
        if (_log.isDebugEnabled())
            _log.debug("process", _transcationID, "Exiting responseStr:" + responseStr);
        return responseStr;
    }
    public Map process1(String p_requestStr) {
        if (_log.isDebugEnabled())
            _log.debug("process", "Entered p_requestStr" + p_requestStr);
        String responseStr = null;
        // String communicationType=null;
        // boolean db=true;
        Connection con = null;
        InterfaceModuleDAO interfaceModuleDAO = null;
        InterfaceModuleVO interfaceModuleVO = null;
        try {
            _map = BTSLUtil.getStringToHash(p_requestStr, "&", "=");
            _transcationID = (String) _map.get("TRANSACTION_ID");
            if (_log.isDebugEnabled())
                _log.debug("process", _transcationID, InterfaceUtil.getPrintMap(_map));
            _interfaceID = (String) _map.get("INTERFACE_ID");
            _interfaceHandler = (String) _map.get("INTERFACE_HANDLER");
            _action = (String) _map.get("INTERFACE_ACTION");
            InterfaceHandler interfaceHandler = getHandlerObj(_interfaceHandler);

            // Validation
            if (_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                interfaceHandler.validate(_map);
                _txnType = TXN_TYPE_VALIDATE;

            }
            // Credit
            else if (_action.equals(PretupsI.INTERFACE_CREDIT_ACTION)) {
                _adjust = BTSLUtil.NullToString((String) _map.get("ADJUST"));
                if (_adjust.equals("Y")) {
                    // credit amount as adjustment
                    _txnType = TXN_TYPE_CREDITADJUST;
                    interfaceHandler.creditAdjust(_map);
                } else {
                    _txnType = TXN_TYPE_CREDIT;
                    interfaceHandler.credit(_map);
                }
            }
            // Debit
            else if (_action.equals(PretupsI.INTERFACE_DEBIT_ACTION)) {
                _txnType = TXN_TYPE_DEBITADJUST;
                interfaceHandler.debitAdjust(_map);
            } else if (_action.equals(PretupsI.INTERFACE_UPDATE_VALIDITY_ACTION)) {
                _txnType = TXN_TYPE_VALIDITYADJUST;
                interfaceHandler.validityAdjust(_map);
            }
            _status = InterfaceErrorCodesI.SUCCESS;
        } catch (BTSLBaseException be) {
            _status = be.getMessage();

        } catch (Exception e) {
            _status = InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION;
            _log.error("process", _transcationID, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceModule[process]", _transcationID, "", "", "Exception in Interface Module:" + e.getMessage());
            e.printStackTrace();
        }
        if (_map != null) {
            _map.put("TRANSACTION_STATUS", _status);
            interfaceModuleVO = InterfaceUtil.getVOFromMap(_txnType, _map, "C");
            InterfaceTransactionLog.log(interfaceModuleVO);
        }
        if (_log.isDebugEnabled())
            _log.debug("process", _transcationID, "Exiting responseStr:" + responseStr);
        return _map;
    }

    /**
     * Get Handler Object
     * 
     * @param handlerClassName
     * @return
     */
    public InterfaceHandler getHandlerObj(String handlerClassName) {
        if (_log.isDebugEnabled())
            _log.debug("getHandlerObj", _transcationID, "Entered handlerClassName:" + handlerClassName);
        InterfaceHandler handlerObj = null;
        try {
            handlerObj = (InterfaceHandler) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceModule[getHandlerObj]", _transcationID, "", "", "Exception in getting handler object:" + e.getMessage());
            _log.error("getHandlerObj", _transcationID, "Exception " + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getHandlerObj", _transcationID, "Exiting");
        }
        return handlerObj;
    }// end of getHandlerObj

    /**
     * Populate IN TransactionID
     * 
     */
    public String getINTransactionID() throws BTSLBaseException {
        // sqlResp = "select seq_inresponse_id.nextval from dual";
        // id
        String id = InterfaceUtil.getINTransactionID();
        // get unique ID
        if (id.length() < 6) {
            int paddingLength = 6 - id.length();
            for (int i = 0; i < paddingLength; i++) {
                id = "0" + id;
            }
        }
        return id;
    }
}
