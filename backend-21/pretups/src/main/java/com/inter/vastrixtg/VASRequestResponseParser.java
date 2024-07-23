package com.inter.vastrixtg;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.vastrixtg.vastrixtgstub.VASTrixRechargeRequestParms;
import com.btsl.pretups.inter.vastrixtg.vastrixtgstub.VASTrixRechargeResponseParms;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class VASRequestResponseParser {
    public Log _log = LogFactory.getLog("VASRequestResponseParser".getClass().getName());

    public VASTrixRechargeRequestParms generateVASRequest(int p_action, HashMap p_map) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("generateRequestObject", "Entered p_action=" + p_action + " map: " + p_map);
        VASTrixRechargeRequestParms vasRequest = new VASTrixRechargeRequestParms();
        try {
            switch (p_action) {

            case VASTGI.ACTION_RECHARGE_CREDIT: {
                vasRequest = generateCreditReqestObjectInMap(p_map);
                break;
            }

            }// end of switch block
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("generateRequestObject", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequestObject", "Exited");
        }// end of finally
        return vasRequest;
    }

    public HashMap parseResponseObject(int p_action, VASTrixRechargeResponseParms responseObject) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseResponseObject", "Entered p_action" + p_action + " responseObject" + responseObject);
        HashMap map = null;
        try {
            switch (p_action) {
            case VASTGI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponseObject(responseObject);
                break;
            }

            }// end of switch block
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("parseResponseObject", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponseObject", "Exiting map: " + map);
        }// end of finally
        return map;

    }

    private VASTrixRechargeRequestParms generateCreditReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setPromotionReqestObjectInMap", "Entered p_map: " + p_map);
        VASTrixRechargeRequestParms vasRequest = new VASTrixRechargeRequestParms();
        try {

            // vasRequest.setPhonenumber((String)p_map.get("MSISDN"));
            vasRequest.setPhonenumber(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            vasRequest.setTypeofrecharge((String) p_map.get("CARD_GROUP_SELECTOR"));
            vasRequest.setAmount((String) p_map.get("TRANSFER_AMOUNT"));
            vasRequest.setTransactionid((String) p_map.get("TRANSACTION_ID"));
            ;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setPromotionReqestObjectInMap", "Exited p_map: " + p_map);
        }
        return vasRequest;
    }

    private HashMap parseRechargeCreditResponseObject(VASTrixRechargeResponseParms responseObject) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponseObject", "Entered ");
        HashMap map = null;
        try {

            map = new HashMap();
            String responseCode = responseObject.getResponse();
            String responseMessgage = responseObject.getMessage();
            String wapID = responseObject.getTransactionid();
            map.put("RESP_CODE", responseCode);
            map.put("RESP_MSG", responseMessgage);
            map.put("WAP_ID", wapID);
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponseObject", "Exit  map:" + map);
        }
        return map;
    }

}
