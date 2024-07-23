/*
 * Created on Apr 30, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.alepoogn;

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.inter.alepoogn.alepoognstub.DataItem;

/**
 * @author shashank.shukla
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class AlepoOGNRequestFormatter {

    /**
	 * 
	 */
    public AlepoOGNRequestFormatter() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static Log _log = LogFactory.getLog(AlepoOGNRequestFormatter.class.getName());

    protected DataItem[] getRequestDataItem(int action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered map: " + p_map);
        DataItem[] reqDataItem = null;
        p_map.put("action", String.valueOf(action));
        switch (action) {
        case AlepoOGNI.ACTION_ACCOUNT_INFO: {
            reqDataItem = getAccountInfoRequestDataItem(p_map);
            break;
        }
        case AlepoOGNI.ACTION_RECHARGE_CREDIT: {
            reqDataItem = getRechargeCreditRequestDataItem(p_map);
            break;
        }
        case AlepoOGNI.ACTION_TRANSFER_CREDIT: {
            reqDataItem = getCreditAdjustRequestDataItem(p_map);
            break;
        }
        case AlepoOGNI.ACTION_IMMEDIATE_DEBIT: {
            reqDataItem = getImmediateDebitRequestDataItem(p_map);
            break;
        }
        }
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Exited Request DateItem:  " + reqDataItem);
        return reqDataItem;
    }

    /**
     * this method parse the response from XML String into HashMap
     * 
     * @param action
     *            int
     * @param responseStr
     *            java.lang.String
     * @return map java.util.HashMap
     */

    protected HashMap parseResponseDateItem(int action, DataItem[] p_dataItem) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered Response Data Item:  " + p_dataItem);
        HashMap map = null;
        switch (action) {
        case AlepoOGNI.ACTION_ACCOUNT_INFO: {
            map = parseAccountInfoResponseDateItem(p_dataItem);
            break;
        }
        case AlepoOGNI.ACTION_RECHARGE_CREDIT: {
            map = parseRechargeCreditResponseDateItem(p_dataItem);
            break;
        }
        case AlepoOGNI.ACTION_TRANSFER_CREDIT: {
            map = parseCreditAdjustResponseDateItem(p_dataItem);
        }
        case AlepoOGNI.ACTION_IMMEDIATE_DEBIT: {
            map = parseImmediateDebitResponseDateItem(p_dataItem);
            break;
        }
        }
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Exiting map: " + map);
        return map;
    }

    /**
     * This Method generate account information request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private DataItem[] getAccountInfoRequestDataItem(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getAccountInfoRequestDataItem", "Entered p_map=" + p_map);
        DataItem[] reqDataItem = new DataItem[3];

        try {
            DataItem userId = new DataItem("UserID", (String) p_map.get("MSISDN"));
            DataItem inTxnId = new DataItem("TransactionID", (String) p_map.get("IN_RECON_ID"));
            DataItem method = new DataItem("Method", "GET");
            DataItem template = new DataItem("Template", "AuthorizeUserAccount.hts");
            DataItem otherInfo = new DataItem("OtherInfo", "");

            reqDataItem = new DataItem[] { userId, inTxnId, method, template, otherInfo };

            return reqDataItem;
        } catch (Exception e) {
            _log.error("getAccountInfoRequestDataItem", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getAccountInfoRequestDataItem", "Exiting reqDataItem: " + reqDataItem);
        }
    }

    /**
     * This Method parse GetAccount Info Response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseAccountInfoResponseDateItem(DataItem[] responseDataItem) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseAccountInfoResponseDateItem", "Entered responseDataItem: " + responseDataItem);
        HashMap map = null;
        DataItem dataItem = null;
        try {
            map = new HashMap();
            for (int i = 0, j = responseDataItem.length; i < j; i++) {
                dataItem = responseDataItem[i];
                String key = dataItem.getKey();
                String value = dataItem.getValue().trim();
                map.put(key, value);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseAccountInfoResponseDateItem", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseAccountInfoResponseDateItem", "Exiting map: " + map);
        }
    }

    /**
     * @param map
     * @return requestStr java.lang.String
     * @throws Exception
     */
    private DataItem[] getRechargeCreditRequestDataItem(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getRechargeCreditRequestDataItem", "Entered p_map=" + p_map);
        DataItem[] reqDataItem = null;

        try {
            DataItem userId = new DataItem("UserID", (String) p_map.get("MSISDN"));
            DataItem update = new DataItem("UPDATE", "UPDATE");
            DataItem validityDays = new DataItem("ValidityDays", (String) p_map.get("VALIDITY_DAYS"));
            DataItem graceDays = new DataItem("GraceDays", (String) p_map.get("GRACE_DAYS"));
            DataItem xferAmount = new DataItem("Amount", (String) p_map.get("transfer_amount"));
            DataItem inTxnId = new DataItem("TransactionID", (String) p_map.get("IN_RECON_ID"));
            DataItem otherInfo = new DataItem("OtherInfo", (String) p_map.get("OTHER_INFO"));

            // reqDataItem = new
            // DataItem[]{userId,update,validityDays,graceDays,xferAmount,inTxnId,otherInfo};
            reqDataItem = new DataItem[] { userId, update, validityDays, xferAmount, inTxnId, otherInfo };

            return reqDataItem;
        } catch (Exception e) {
            _log.error("getRechargeCreditRequestDataItem", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getRechargeCreditRequestDataItem", "Exiting reqDataItem: " + reqDataItem);
        }
    }

    /**
     * This Method parse recharge credit response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    private HashMap parseRechargeCreditResponseDateItem(DataItem[] responseDataItem) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponseDateItem", "Entered responseDataItem: " + responseDataItem);
        HashMap map = null;
        DataItem dataItem = null;
        try {
            map = new HashMap();
            for (int i = 0, j = responseDataItem.length; i < j; i++) {
                dataItem = responseDataItem[i];
                String key = dataItem.getKey();
                String value = dataItem.getValue().trim();
                map.put(key, value);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponseDateItem", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponseDateItem", "Exiting map: " + map);
        }
    }

    /**
     * @param map
     * @return requestStr java.lang.String
     * @throws Exception
     */
    private DataItem[] getCreditAdjustRequestDataItem(HashMap map) throws Exception {
        DataItem[] reqDataItem = null;
        return reqDataItem;
    }

    /**
     * This Method parse recharge credit response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    private HashMap parseCreditAdjustResponseDateItem(DataItem[] p_dataItem) throws Exception {
        HashMap map = null;
        return map;
    }

    /**
     * This Method Generate immediate Debit Request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private DataItem[] getImmediateDebitRequestDataItem(HashMap map) throws Exception {
        DataItem[] reqDataItem = null;
        return reqDataItem;
    }

    /**
     * This Method parse Immediate Debit Response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseImmediateDebitResponseDateItem(DataItem[] p_dataItem) throws Exception {
        HashMap map = null;
        return map;
    }

    protected static String getINTransactionID(HashMap p_map) {
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "Entered");
        String userType = (String) p_map.get("USER_TYPE");
        String inTxnId = (String) p_map.get("TRANSACTION_ID");
        if (!InterfaceUtil.isNullString(userType))
            inTxnId = inTxnId + userType;

        p_map.put("IN_RECON_ID", inTxnId);
        p_map.put("IN_TXN_ID", inTxnId);
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "exited");
        return inTxnId;
    }

}
