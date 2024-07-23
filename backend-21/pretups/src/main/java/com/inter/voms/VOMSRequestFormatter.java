package com.inter.voms;

import java.util.HashMap;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.voms.vomscommon.VOMSI;

public class VOMSRequestFormatter {

    public static Log _log = LogFactory.getLog(VOMSRequestFormatter.class);
    String lineSep = null;
    String _soapAction = "";
    public static OperatorUtilI _operatorUtil = null;

    public VOMSRequestFormatter() {
        // lineSep = System.getProperty("line.separator")+"\r";
        lineSep = System.getProperty("line.separator") + "";
    }

    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("VoucherConsController", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * This method is used to parse the response string based on the type of
     * Action.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String.
     * @throws Exception
     */
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action::" + p_action + " map::" + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            String pin = "";
            try {
                pin = p_map.get("PIN").toString();
                pin = _operatorUtil.encryptPINPassword(pin);
            } catch (Exception e) {
                pin = "";
            }
            p_map.put("PIN", pin);

            switch (p_action) {

            case VOMSI.ACTION_SERIALNO_PIN_DETAILS: {
                _soapAction = "RetrieveVoucherDetails";
                str = generateGetVoucherInfoRequest(p_map);
                break;
            }
            case VOMSI.ACTION_VOUCHER_INFO: {
                _soapAction = "RetrievePinInfo";
                str = generateGetPinInfoRequest(p_map);
                break;
            }
            case VOMSI.ACTION_VOUCHER_CONSUMPTION: {
                _soapAction = "VoucherConsumption";
                str = generateVoucherConsumptionRequest(p_map);
                break;
            }
            case VOMSI.ACTION_VOUCHER_DETAILS_AGAIN: {
                _soapAction = "RetrieveVoucherDetailsAgain";
                str = generateVoucherRetRequestByTxnId(p_map);
                break;
            }
            case VOMSI.ACTION_VOUCHER_ROLLBACK: {
                _soapAction = "VoucherRollback";
                str = generateVoucherRollbackRequest(p_map);
                break;
            }
            case VOMSI.ACTION_VOUCHER_RET_ROLLBACK: {
                _soapAction = "ACTION_VOUCHER_RET_ROLLBACK";
                str = generateVoucherRetrivalRollbackRequest(p_map);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e ::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: str::" + str);
        }
        return str;
    }

    /**
     * This method is used to generate the request for getting account details
     * along with AccountStatus.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateGetVoucherInfoRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;

        try {
            StringBuffer body = new StringBuffer();
            body.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            body.append("<TYPE>" + p_requestMap.get("TYPE").toString() + "</TYPE>");
            body.append("<SUBID>" + p_requestMap.get("SUBID").toString() + "</SUBID>");
            body.append("<MRP>" + p_requestMap.get("MRP").toString() + "</MRP>");
            body.append("<SERVICE>" + p_requestMap.get("SERVICE").toString() + "</SERVICE>");
            body.append("<SELECTOR>" + p_requestMap.get("SELECTOR").toString() + "</SELECTOR>");
            if(p_requestMap.containsKey("QUANTITY")){
            	body.append("<QUANTITY>" + p_requestMap.get("QUANTITY").toString() + "</QUANTITY>");
            }
            body.append("<TXNID>" + p_requestMap.get("TRANSACTION_ID").toString() + "</TXNID>");
            body.append("</COMMAND>");
            requestStr = body.toString().trim();
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting Request String:requestStr::" + requestStr);
        }
        return requestStr;
    }

    /**
     * This method is used to generate the request for getting pin details.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateGetPinInfoRequest(HashMap p_requestMap) throws Exception {
        String method = "generateGetPinInfoRequest";
        if (_log.isDebugEnabled())
            _log.debug(method, "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        try {

            StringBuffer body = new StringBuffer();
            body.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            body.append("<TYPE>" + p_requestMap.get("TYPE").toString() + "</TYPE>");
            body.append("<SUBID>" + p_requestMap.get("MSISDN").toString() + "</SUBID>");
            body.append("<PIN>" + p_requestMap.get("PIN").toString() + "</PIN>");
            if (p_requestMap.get("VTYPE") != null)
                body.append("<VTYPE>" + p_requestMap.get("VTYPE").toString() + "</VTYPE>");
            body.append("</COMMAND>");
            requestStr = body.toString().trim();
        } catch (Exception e) {
            _log.error(method, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(method, "Exiting Request String:requestStr::" + requestStr);
        }
        return requestStr;
    }

    /**
     * This method is used to generate the request for getting pin details.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateVoucherConsumptionRequest(HashMap p_requestMap) throws Exception {
        String method = "generateVoucherConsumptionRequest";
        if (_log.isDebugEnabled())
            _log.debug(method, "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;

        try {
            StringBuffer body = new StringBuffer();
            body.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            body.append("<TYPE>" + p_requestMap.get("TYPE").toString() + "</TYPE>");
            body.append("<SUBID>" + p_requestMap.get("SUBID").toString() + "</SUBID>");
            body.append("<PIN>" + p_requestMap.get("PIN").toString() + "</PIN>");
            if (p_requestMap.get("VTYPE") != null)
                body.append("<VTYPE>" + p_requestMap.get("VTYPE").toString() + "</VTYPE>");
            body.append("</COMMAND>");
            requestStr = body.toString().trim();
        } catch (Exception e) {
            _log.error(method, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(method, "Exiting Request String:requestStr::" + requestStr);
        }
        return requestStr;
    }

    /**
     * This method is used to generate the request for getting pin details.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateVoucherRetRequestByTxnId(HashMap p_requestMap) throws Exception {
        String method = "generateVoucherRetRequestByTxnId";
        if (_log.isDebugEnabled())
            _log.debug(method, "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;

        try {
            StringBuffer body = new StringBuffer();
            body.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            body.append("<TYPE>" + p_requestMap.get("TYPE").toString() + "</TYPE>");
            body.append("<SUBID>" + p_requestMap.get("SUBID").toString() + "</SUBID>");
            body.append("<TXNID>" + p_requestMap.get("TRANSACTION_ID").toString() + "</TXNID>");
            body.append("</COMMAND>");
            requestStr = body.toString().trim();
        } catch (Exception e) {
            _log.error(method, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(method, "Exiting Request String:requestStr::" + requestStr);
        }
        return requestStr;
    }

    /**
     * This method is used to generate the request for getting pin details.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateVoucherRetrivalRollbackRequest(HashMap p_requestMap) throws Exception {
        String method = "generateVoucherRetRequestByTxnId";
        if (_log.isDebugEnabled())
            _log.debug(method, "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;

        try {
            StringBuffer body = new StringBuffer();
            body.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            body.append("<TYPE>" + p_requestMap.get("TYPE").toString() + "</TYPE>");
            body.append("<SUBID>" + p_requestMap.get("SUBID").toString() + "</SUBID>");
            body.append("<TXNID>" + p_requestMap.get("TRANSACTION_ID").toString() + "</TXNID>");
            body.append("</COMMAND>");
            requestStr = body.toString().trim();
        } catch (Exception e) {
            _log.error(method, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(method, "Exiting Request String:requestStr::" + requestStr);
        }
        return requestStr;
    }

    /**
     * This method is used to generate the request for getting pin details.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateVoucherRollbackRequest(HashMap p_requestMap) throws Exception {
        String method = "generateVoucherRollbackRequest";
        if (_log.isDebugEnabled())
            _log.debug(method, "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;

        try {
            StringBuffer body = new StringBuffer();
            body.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            body.append("<TYPE>" + p_requestMap.get("TYPE").toString() + "</TYPE>");
            body.append("<SUBID>" + p_requestMap.get("SUBID").toString() + "</SUBID>");
            body.append("<PIN>" + p_requestMap.get("PIN").toString() + "</PIN>");
            if (p_requestMap.get("VTYPE") != null)
                body.append("<VTYPE>" + p_requestMap.get("VTYPE").toString() + "</VTYPE>");
            else
                body.append("<VTYPE>" + "" + "</VTYPE>");
            body.append("</COMMAND>");
            requestStr = body.toString().trim();
        } catch (Exception e) {
            _log.error(method, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(method, "Exiting Request String:requestStr::" + requestStr);
        }
        return requestStr;
    }

}
