package com.selftopup.pretups.inter.module;

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
import java.util.Date;
import java.util.HashMap;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.CommonClient;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.InterfaceTransactionLog;
// import com.selftopup.pretups.interfaces.businesslogic.InterfaceHandler;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

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
            // communicationType=(String)_map.get("INT_MOD_COMM_TYPE");
            /*
             * if(communicationType.equalsIgnoreCase(CommonClient.
             * COMM_TYPE_SINGLEJVM))
             * {
             * db=true;
             * }
             */
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
            be.printStackTrace();
            _status = be.getMessage();

        } catch (Exception e) {
            _status = InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION;
            _log.error("process", _transcationID, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceModule[process]", _transcationID, "", "", "Exception in Interface Module:" + e.getMessage());
            e.printStackTrace();
        }
        if (_map != null) {
            _map.put("TRANSACTION_STATUS", _status);
            // added for logging details present in interface_transaction table
            // (Manisha 04/02/08)
            interfaceModuleVO = InterfaceUtil.getVOFromMap(_txnType, _map, "C");
            // print in log for all entries of interface transaction table
            InterfaceTransactionLog.log(interfaceModuleVO);
            /*
             * try
             * {
             * if(db &&
             * (InterfaceUtil.isNullString((String)_map.get("DATABASE_ENTRY_REQD"
             * )) || !"N".equals((String)_map.get("DATABASE_ENTRY_REQD"))))
             * {
             * interfaceModuleDAO=new InterfaceModuleDAO();
             * con=OracleUtil.getConnection();
             * interfaceModuleDAO.addInterfaceModuleDetails(con,InterfaceUtil.
             * getVOFromMap(_txnType,_map,"C"));
             * }
             * }
             * catch(BTSLBaseException be)
             * {
             * _log.error("process",_transcationID,
             * "Exception while adding in database:"+be.getMessage());
             * be.printStackTrace();
             * }
             * catch(Exception e)
             * {
             * _log.error("process",_transcationID,
             * "Exception while adding in database:"+e.getMessage());
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.FATAL,"InterfaceModule[process]",_transcationID
             * ,"","",
             * "Exception in adding Interface Transaction details in database:"
             * +e.getMessage());
             * e.printStackTrace();
             * }
             * finally
             * {
             * if(con!=null)
             * {
             * try{con.commit();}catch(Exception e){}
             * try{con.close();}catch(Exception e){}
             * }
             * }
             */
            // construct response string from HashMap
            responseStr = BTSLUtil.getStringFromHash(_map);
        }
        if (_log.isDebugEnabled())
            _log.debug("process", _transcationID, "Exiting responseStr:" + responseStr);
        return responseStr;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

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
