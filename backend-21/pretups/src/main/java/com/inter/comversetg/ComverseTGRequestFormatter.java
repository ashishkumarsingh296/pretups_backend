/*
 * Created on Jun 17, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.comversetg;

import java.util.Calendar;
import java.util.HashMap;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.comversetg.comversetgstub.BalanceCreditAccount;
import com.btsl.pretups.inter.comversetg.comversetgstub.BalanceEntity;
import com.btsl.pretups.inter.comversetg.comversetgstub.ChangeCOSRequest;
import com.btsl.pretups.inter.comversetg.comversetgstub.SubscriberModify;
import com.btsl.pretups.inter.comversetg.comversetgstub.SubscriberPPS;
import com.btsl.pretups.inter.comversetg.comversetgstub.SubscriberRetrieve;

public class ComverseTGRequestFormatter {
    public Log _log = LogFactory.getLog("ComverseTGRequestFormatter".getClass().getName());

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

            case ComverseTGI.ACTION_PROMOTION_CREDIT: {
                setPromotionReqestObjectInMap(p_map);
                break;
            }
            case ComverseTGI.ACTION_COS_UPDATE: {
                setCOSReqestObjectInMap(p_map);
                break;
            }
            case ComverseTGI.ACTION_CREDIT_ADJUST: {
                setCreditReqestObjectInMap(p_map);
                break;
            }
            case ComverseTGI.ACTION_DEBIT_ADJUST: {
                setDebitReqestObjectInMap(p_map);
                break;
            }
            case ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION: {
                setActivationReqestObjectInMap(p_map);
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
            case ComverseTGI.ACTION_ACCOUNT_DETAILS: {
                map = parseAccountInfoResponseObject(p_map);
                break;
            }
            case ComverseTGI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponseObject(p_map);
                break;
            }
            case ComverseTGI.ACTION_PROMOTION_CREDIT: {
                map = parsePromotionResponseObject(p_map);
                break;
            }
            case ComverseTGI.ACTION_COS_UPDATE: {
                map = parseCOSResponseObject(p_map);
                break;
            }
            case ComverseTGI.ACTION_CREDIT_ADJUST: {
                map = parseCreditAdjustResponseObject(p_map);
                break;
            }
            case ComverseTGI.ACTION_DEBIT_ADJUST: {
                map = parseDebitAdjustResponseObject(p_map);
                break;
            }
            case ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION: {
                map = parseActivationResponseObject(p_map);
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
        int status = 0;
        SubscriberRetrieve subscriberRetrieve = null;

        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTGI.RESULT_OK);
            subscriberRetrieve = (SubscriberRetrieve) p_map.get("ACCINFO_RESP_OBJ");
            BalanceEntity be[] = subscriberRetrieve.getSubscriberData().getBalances();
            map.put("IN_LANG", subscriberRetrieve.getSubscriberData().getNotificationLanguage());
            map.put("IN_CURRENCY", subscriberRetrieve.getSubscriberData().getCurrencyCode());
            map.put("ACCOUNT_STATUS", subscriberRetrieve.getSubscriberData().getCurrentState());
            map.put("SERVICE_CLASS", subscriberRetrieve.getSubscriberData().getCOSName());
            map.put("PREV_SERVICE_CLASS", subscriberRetrieve.getSubscriberData().getCOSName());
            int count = 0;
            for (int i = 0, j = be.length; i < j; i++) {
                if (be[i].getBalanceName().equals((String) p_map.get("CORE_BAL_NAME"))) {

                    map.put("OLD_EXPIRY_DATE", be[i].getAccountExpiration());
                    map.put("CAL_OLD_EXPIRY_DATE", be[i].getAccountExpiration());
                    map.put("RESP_BALANCE", Double.valueOf(be[i].getBalance()));
                    ++count;

                } else if (be[i].getBalanceName().equals((String) p_map.get("PROMO_BAL_NAME"))) {
                    map.put("PROMO_OLD_EXPIRY_DATE", be[i].getAccountExpiration());
                    map.put("PROMO_CAL_OLD_EXPIRY_DATE", be[i].getAccountExpiration());
                    map.put("PROMO_RESP_BALANCE", Double.valueOf(be[i].getBalance()));
                    ++count;
                } else if (count == 2)
                    break;
            }
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
            map.put("RESP_STATUS", ComverseTGI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private HashMap parsePromotionResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parsePromotionResponseObject", "Entered ");
        HashMap map = null;

        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTGI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parsePromotionResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parsePromotionResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private HashMap parseCOSResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseCOSResponseObject", "Entered ");
        HashMap map = null;

        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTGI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseCOSResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseCOSResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    private HashMap parseCreditAdjustResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseCreditAdjustResponseObject", "Entered ");
        HashMap map = null;

        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTGI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseCreditAdjustResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseCreditAdjustResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private HashMap parseDebitAdjustResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseDebitAdjustResponseObject", "Entered ");
        HashMap map = null;
        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTGI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseDebitAdjustResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseDebitAdjustResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private HashMap parseActivationResponseObject(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseActivationAdjustResponseObject", "Entered ");
        HashMap map = null;
        try {
            map = new HashMap();
            map.put("RESP_STATUS", ComverseTGI.RESULT_OK);
        } catch (Exception e) {
            _log.error("parseActivationAdjustResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseActivationAdjustResponseObject", "Exit  map:" + map);
        }
        return map;
    }

    /**
     * 
     * @param p_map
     * @throws Exception
     */
    private void setCOSReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setCOSReqestObjectInMap", "Entered p_map: " + p_map);
        try {
            ChangeCOSRequest cosr = new ChangeCOSRequest();
            cosr.setNewCOS((String) p_map.get("NEW_COS_SERVICE_CLASS"));
            cosr.setSubscriberId((String) p_map.get("MSISDN"));
            p_map.put("COS_REQ_OBJ", cosr);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setCOSReqestObjectInMap", "Exited p_map: " + p_map);
        }

    }

    private void setPromotionReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setPromotionReqestObjectInMap", "Entered p_map: " + p_map);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(((String) p_map.get("PROMO_CAL_OLD_EXPIRY_DATE"))));
            BalanceCreditAccount bc = new BalanceCreditAccount();
            bc.setCreditValue(Double.parseDouble((String) p_map.get("promotion_transfer_amount")));
            bc.setBalanceName((String) p_map.get("PROMO_BAL_NAME"));
            bc.setExpirationDate(cal);
            BalanceCreditAccount bcarr[] = new BalanceCreditAccount[] { bc };
            p_map.put("PROMO_REQ_OBJ", bcarr);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setPromotionReqestObjectInMap", "Exited p_map: " + p_map);
        }

    }

    private void setCreditReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setCreditReqestObjectInMap", "Entered p_map: " + p_map);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(((String) p_map.get("CAL_OLD_EXPIRY_DATE"))));
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

    private void setActivationReqestObjectInMap(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setActivationReqestObjectInMap", "Entered p_map: " + p_map);
        try {
            SubscriberModify sm = new SubscriberModify();
            sm.setSubscriberID((String) p_map.get("MSISDN"));
            SubscriberPPS spps = new SubscriberPPS();
            spps.setCurrentState((String) p_map.get("SUBSCRIBER_P2P_ACTIVE_STATE"));
            sm.setSubscriber(spps);
            p_map.put("AC_ADJ_REQ_OBJ", sm);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setActivationReqestObjectInMap", "Exited p_map: " + p_map);
        }
    }

}
