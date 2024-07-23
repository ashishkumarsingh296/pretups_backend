/*
 * Created on Jun 17, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.comversetr;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Date;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.comversetr.comversetrstub.BalanceCreditAccount;
import com.btsl.pretups.inter.comversetr.comversetrstub.BalanceEntity;
import com.btsl.pretups.inter.comversetr.comversetrstub.SubscriberRetrieve;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class ComverseTRRequestFormatter {
    public Log _log = LogFactory.getLog("ComverseTRRequestFormatter".getClass().getName());
    private String _interfaceID = null;// Contains the interfaceID

    /**
     * This method will return of MML request message.
     * This method internally calls private method to get request object.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return Request
     * @throws Exception
     */
    public void setRequestObjectInMap(int p_action, HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequestObject", "Entered p_action=" + p_action + " map: " + p_map);
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            case ComverseTRI.ACTION_ACCOUNT_DETAILS: {
                setAccountInfoReqestObjectInMap(p_map);
                break;
            }
            case ComverseTRI.ACTION_RECHARGE_CREDIT: {
                setRechargeReqestObjectInMap(p_map);
                break;
            }
            case ComverseTRI.ACTION_CREDIT_ADJUST: {
                setCreditReqestObjectInMap(p_map);
                break;
            }
            case ComverseTRI.ACTION_DEBIT_ADJUST: {
                setDebitReqestObjectInMap(p_map);
                break;
            }
            case ComverseTRI.ACTION_LMB_DEBIT_ADJUST: {
                setLMBDebitReqestObjectInMap(p_map);
                break;
            }
            // FOR LMB CREDIT ADJUST
            case ComverseTRI.ACTION_LMB_CREDIT_ADJUST: {
                setLMBCreditReqestObjectInMap(p_map);
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
    }// end of generateRequestObject

    /**
     * This method internally calls methods (according to p_action parameter) to
     * get response HashMap and returns it.
     * 
     * @param int action
     * @param HashMap
     *            p_map
     * @return HashMap map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public HashMap parseResponseObject(int p_action, HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponseObject", "Entered p_action" + p_action + " p_map" + p_map);
        HashMap map = null;
        try {
            switch (p_action) {
            case ComverseTRI.ACTION_ACCOUNT_DETAILS: {
                map = parseAccountInfoResponseObject(p_map);
                break;
            }
            case ComverseTRI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponseObject(p_map);
                break;
            }
            case ComverseTRI.ACTION_CREDIT_ADJUST: {
                map = parseCreditAdjustResponseObject(p_map);
                break;
            }
            case ComverseTRI.ACTION_DEBIT_ADJUST: {
                map = parseDebitAdjustResponseObject(p_map);
                break;
            }
            case ComverseTRI.ACTION_LMB_DEBIT_ADJUST: {
                map = parseLMBDebitAdjustResponseObject(p_map);
                break;
            }
            case ComverseTRI.ACTION_LMB_CREDIT_ADJUST: {
                map = parseLMBCreditAdjustResponseObject(p_map);
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
    }// end of parseResponseObject

    /**
     * This method will return request object for Account info (validate
     * action).
     * 
     * @param HashMap
     *            p_map
     * @return Request
     * @throws Exception
     */
    private void setAccountInfoReqestObjectInMap(HashMap p_map) throws Exception {
    }

    /**
     * This method parse the response for Acount INfo from Response object and
     * puts into HashMap and returns it
     * 
     * @param Response
     *            p_respObj
     * @return HashMap map
     * @throws Exception
     */
    private HashMap parseAccountInfoResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered ");
        HashMap map = null;
        HashMap balanceMap = null;
        int status = 0;
        SubscriberRetrieve subscriberRetrieve = null;

        try {
            map = new HashMap();
            balanceMap = new HashMap();
            map.put("RESP_STATUS", ComverseTRI.RESULT_OK);
            subscriberRetrieve = (SubscriberRetrieve) p_map.get("ACCINFO_RESP_OBJ");
            BalanceEntity be[] = subscriberRetrieve.getSubscriberData().getBalances();
            map.put("IN_LANG", subscriberRetrieve.getSubscriberData().getNotificationLanguage());
            map.put("IN_CURRENCY", subscriberRetrieve.getSubscriberData().getCurrencyCode());
            map.put("ACCOUNT_STATUS", subscriberRetrieve.getSubscriberData().getCurrentState());
            map.put("SERVICE_CLASS", subscriberRetrieve.getSubscriberData().getCOSName());
            // Lohit for age on network parameter
            if (subscriberRetrieve.getSubscriberData().getDateEnterActive() != null)
                map.put("AON", Long.toString(subscriberRetrieve.getSubscriberData().getDateEnterActive().getTimeInMillis()));
            else
                map.put("AON", "");
            for (int i = 0, j = be.length; i < j; i++) {
                if (be[i].getBalanceName().equals((String) p_map.get("CORE_BAL_NAME"))) {
                    map.put("OLD_EXPIRY_DATE", be[i].getAccountExpiration());
                    map.put("CAL_OLD_EXPIRY_DATE", be[i].getAccountExpiration());
                    map.put("RESP_BALANCE", Double.valueOf(be[i].getBalance()));
                    continue;
                }
                if (be[i].getBalanceName().equals((String) p_map.get("LMB_BAL_NAME"))) {
                    map.put("LMB_ALLOWED_VALUE", Double.valueOf(be[i].getBalance()));
                    map.put("LMB_ACC_MISSING", "N");
                    continue;
                }
            }
            // need to get lmb allowed at any cost
            if (map.get("LMB_ALLOWED_VALUE") == null) {
                map.put("LMB_ALLOWED_VALUE", 0.0);
                map.put("LMB_ACC_MISSING", "Y");
            }
            // Lohit for getting all balance map
            for (int i = 0, j = be.length; i < j; i++) {
                // balanceMap.put(be[i].getBalanceName()+"_RESP_BALANCE",String.valueOf(Double.valueOf(be[i].getBalance())));
                balanceMap.put(be[i].getBalanceName() + "_RESP_BALANCE", String.valueOf(PretupsBL.getSystemAmount(Double.valueOf(be[i].getBalance()))));
            }
            map.put("BALANCE_MAP", balanceMap);
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            p_map.remove("ACCINFO_RESP_OBJ");
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exit  map:" + map);
        }
        return map;
    }

    /**
     * This method will return request Object for Credit action.
     * 
     * @param HashMap
     *            p_map
     * @return Request
     * @throws Exception
     */
    private void setRechargeReqestObjectInMap(HashMap p_map) throws Exception {
    }

    /**
     * This method parse the response for Credit from Response Object and puts
     * into HashMap and returns it
     * 
     * @param Response
     *            p_respObj
     * @return HashMap map
     * @throws Exception
     */
    private HashMap parseRechargeCreditResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponseObject", "Entered ");
        HashMap map = null;
        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTRI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    private HashMap parseCreditAdjustResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseCreditAdjustResponseObject", "Entered ");
        HashMap map = null;

        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTRI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseCreditAdjustResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseCreditAdjustResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    private HashMap parseDebitAdjustResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseDebitAdjustResponseObject", "Entered ");
        HashMap map = null;
        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTRI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseDebitAdjustResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {

            if (_log.isDebugEnabled())
                _log.debug("parseDebitAdjustResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    private void setCreditReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setCreditReqestObjectInMap", "Entered p_map: " + p_map);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(((String) p_map.get("CAL_OLD_EXPIRY_DATE"))) + (Long.parseLong(((String) p_map.get("VALIDITY_DAYS")))) * 24 * 60 * 60 * 1000);
            BalanceCreditAccount bc = new BalanceCreditAccount();
            bc.setCreditValue(Double.parseDouble((String) p_map.get("transfer_amount")));
            bc.setBalanceName((String) p_map.get("CORE_BAL_NAME"));
            bc.setExpirationDate(cal);
            BalanceCreditAccount bcarr[] = new BalanceCreditAccount[] { bc };
            p_map.put("CR_ADJ_REQ_OBJ", bcarr);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setCreditReqestObjectInMap", "Exited p_map: " + p_map);
        }

    }

    private void setDebitReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setDebitReqestObjectInMap", "Entered p_map: " + p_map);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(((String) p_map.get("CAL_OLD_EXPIRY_DATE"))));
            BalanceCreditAccount bc = new BalanceCreditAccount();
            bc.setCreditValue(-Double.parseDouble((String) p_map.get("transfer_amount")));
            bc.setBalanceName((String) p_map.get("CORE_BAL_NAME"));
            bc.setExpirationDate(cal);
            BalanceCreditAccount bcarr[] = new BalanceCreditAccount[] { bc };
            p_map.put("DR_ADJ_REQ_OBJ", bcarr);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setDebitReqestObjectInMap", "Exited p_map: " + p_map);
        }
    }

    private void setLMBDebitReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setLMBDebitReqestObjectInMap", "Entered p_map: " + p_map);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(((String) p_map.get("CAL_OLD_EXPIRY_DATE"))));
            BalanceCreditAccount bc = new BalanceCreditAccount();
            // bc.setCreditValue(-Double.parseDouble((String)p_map.get("LMB_FLAG_DEBIT_AMT")));
            bc.setCreditValue(-Double.parseDouble((String) p_map.get("lmb_transfer_amount")));
            bc.setBalanceName((String) p_map.get("LMB_BAL_NAME"));
            bc.setExpirationDate(cal);
            BalanceCreditAccount bcarr[] = new BalanceCreditAccount[] { bc };
            p_map.put("DR_LMB_ADJ_REQ_OBJ", bcarr);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setLMBDebitReqestObjectInMap", "Exited p_map: " + p_map);
        }
    }

    private HashMap parseLMBDebitAdjustResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLMBDebitAdjustResponseObject", "Entered ");
        HashMap map = null;
        try {
            map = new HashMap();
            map.put("LMB_RESP_STATUS", ComverseTRI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseLMBDebitAdjustResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {

            if (_log.isDebugEnabled())
                _log.debug("parseLMBDebitAdjustResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    /**
     * Method :setLMBCreditReqestObjectInMap
     * 
     * @param p_map
     * @throws Exception
     */
    private void setLMBCreditReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setLMBCreditReqestObjectInMap", "Entered p_map: " + p_map);
        try {
            Calendar cal = Calendar.getInstance();
            _interfaceID = (String) p_map.get("INTERFACE_ID");
            // changed
            // line/cal.setTimeInMillis(Long.parseLong(((String)p_map.get("CAL_OLD_EXPIRY_DATE")))+(Long.parseLong(((String)p_map.get("VALIDITY_DAYS"))))*24*60*60*1000);
            cal.setTimeInMillis(System.currentTimeMillis() + (Long.parseLong((String) FileCache.getValue(_interfaceID, "LMB_EXPIRY_DATE"))));
            BalanceCreditAccount bc = new BalanceCreditAccount();
            // bc.setCreditValue(Double.parseDouble((String)p_map.get("LMB_FLAG_CREDIT_AMT")));
            bc.setCreditValue(Double.parseDouble((String) p_map.get("lmb_transfer_amount")));
            bc.setBalanceName((String) p_map.get("LMB_BAL_NAME"));
            bc.setExpirationDate(cal);
            BalanceCreditAccount bcarr[] = new BalanceCreditAccount[] { bc };
            p_map.put("CR_LMB_ADJ_REQ_OBJ", bcarr);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setLMBCreditReqestObjectInMap", "Exited p_map: " + p_map);
        }
    }

    /**
     * Method : parseLMBCreditAdjustResponseObject
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private HashMap parseLMBCreditAdjustResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLMBCreditAdjustResponseObject", "Entered ");
        HashMap map = null;

        try {
            map = new HashMap();
            map.put("LMB_RESP_STATUS", ComverseTRI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseLMBCreditAdjustResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLMBCreditAdjustResponseObject", "Exit  map:" + map);
        }
        return map;
    }
}
