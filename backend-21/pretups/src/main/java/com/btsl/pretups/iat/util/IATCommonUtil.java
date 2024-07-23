/**
 * 
 * IATRestrictedSubscriberDAO.java
 * ----------------------------------------------------------------------------
 * ------
 * Name Date History
 * ----------------------------------------------------------------------------
 * ------
 * Babu Kunwar 27/09/2011 Initial Creation
 * ----------------------------------------------------------------------------
 * ------
 * Copyright (c) 2011 Comviva Technologies Ltd.
 * This class is responsible for the validate rule for MSISDN of Corporate IAT
 * Recharge
 * of the restricted subscribers.
 */
package com.btsl.pretups.iat.util;

import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterCache;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterVO;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.iat.businesslogic.IATNetworkCountryMappingVO;
import com.btsl.pretups.logging.IATRestrictedMSISDNLog;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;

public class IATCommonUtil extends OperatorUtil {

    private static Log _log = LogFactory.getLog(IATCommonUtil.class.getName());

    /**
     * This method is defined in IATCommonUtil file. It is used to validates the
     * MSISDN
     * on the basis of the IAT rules.
     * 
     * @param p_receiverVO
     * @param p_msisdn
     * @throws BTSLBaseException
     *             p_requestID
     */
    public static IATCountryMasterVO validateIATMsisdn(String[] p_messageArray, String p_msisdn, ReceiverVO p_receiverVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateIATMsisdn", "Entered p_msisdn:" + p_msisdn);
        }
        final String METHOD_NAME = "validateIATMsisdn";
        String[] strArr = null;
        boolean isMsisdnSet = false;
        IATCountryMasterVO masCountryVO = null;
        try {
            if (BTSLUtil.isNullString(p_msisdn)) {
                throw new BTSLBaseException("PretupsBL", "validateIATMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            // If Receiver msisdn contains '0', '00' or '+', remove these.
            if (p_msisdn.startsWith("00")) {
                p_msisdn = p_msisdn.substring(2);
            } else if (p_msisdn.startsWith("0")) {
                p_msisdn = p_msisdn.substring(1);
            } else if (p_msisdn.startsWith("+")) {
                p_msisdn = p_msisdn.substring(1);
            }

            if (!BTSLUtil.isNumeric(p_msisdn)) {
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", "validateIATMsisdn", PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTNUMERIC, 0, strArr, null);
            }
            // Get receiver country VO.
            masCountryVO = getIATCountryVO(p_messageArray, p_msisdn, p_receiverVO);
            if (masCountryVO != null) {
                // Check IAT country status is active or not.
                if (!PretupsI.YES.equals(masCountryVO.getCountryStatus())) {
                    throw new BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_NOT_ACTIVE, new String[] { String.valueOf(masCountryVO.getRecCountryCode()), masCountryVO.getRecCountryShortName(), masCountryVO.getRecCountryName(), p_msisdn });
                }
                if (p_msisdn.length() < masCountryVO.getPrefixLength()) {
                    IATRestrictedMSISDNLog.log("", p_msisdn, "IAT MSISDN", "is greater than defined prefix length");
                    strArr = new String[] { p_msisdn, String.valueOf(masCountryVO.getMinMsisdnLength()), String.valueOf(masCountryVO.getMaxMsisdnLength()) };
                    throw new BTSLBaseException("PretupsBL", "validateIATMsisdn", PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
                }
                if ((p_msisdn.length() < masCountryVO.getMinMsisdnLength() || p_msisdn.length() > masCountryVO.getMaxMsisdnLength())) {
                    IATRestrictedMSISDNLog.log("", p_msisdn, "IAT MSISDN", "is not in range of Max and Min Length");
                    strArr = new String[] { p_msisdn, String.valueOf(masCountryVO.getMinMsisdnLength()), String.valueOf(masCountryVO.getMaxMsisdnLength()) };
                    throw new BTSLBaseException("PretupsBL", "validateIATMsisdn", PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
                }
                ArrayList networkCntryCache = IATNWServiceCache.getNetworkCountryArrObject();
                IATNetworkCountryMappingVO networkCntryVO = null;
                if (networkCntryCache != null) {
                    p_msisdn = p_msisdn.substring(String.valueOf(masCountryVO.getRecCountryCode()).length());
                    int cacheLen = networkCntryCache.size();
                    if (cacheLen > 0) {
                        for (int i = 0; i < cacheLen; i++) {
                            networkCntryVO = (IATNetworkCountryMappingVO) networkCntryCache.get(i);
                            if (networkCntryVO != null) {
                                String[] prefixList = networkCntryVO.getRecNetworkPrefix().split(",");
                                for (int m = 0, n = prefixList.length; m < n; m++) {
                                    String nwPrfx = prefixList[m];
                                    if (p_msisdn.startsWith(nwPrfx) && networkCntryVO.getRecCountryShortName().equals(masCountryVO.getRecCountryShortName())) {
                                        p_receiverVO.setMsisdn(p_msisdn);
                                        p_receiverVO.setErrorMsgFound(false);
                                        isMsisdnSet = true;
                                        if (!PretupsI.YES.equals(networkCntryVO.getStatus())) {
                                            String[] strMsg = new String[] { masCountryVO.getRecCountryName(), networkCntryVO.getRecNetworkCode(), networkCntryVO.getRecNetworkName(), networkCntryVO.getStatus(), p_msisdn };
                                            throw new BTSLBaseException("PretupsBL", "validateIATMsisdn", PretupsErrorCodesI.IAT_NW_SUSPENDED, 0, strMsg, null);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        if (!isMsisdnSet) {
                            p_receiverVO.setMsisdn(p_msisdn);
                            p_receiverVO.setNtworkErrorMsg(p_messageArray[0]);
                            p_receiverVO.setErrorMsgFound(true);
                        }
                        if (networkCntryVO.getRecNetworkCode() == null) {
                            String[] strMsg = new String[] { "+" + masCountryVO.getRecCountryCode() + p_msisdn, networkCntryVO.getRecNetworkPrefix(), masCountryVO.getRecCountryShortName(), masCountryVO.getRecCountryName() };
                            throw new BTSLBaseException("PretupsBL", "validateIATMsisdn", PretupsErrorCodesI.IAT_NW_PRFX_NOT_FOUND, 0, strMsg, null);
                        }
                    } else {
                        _log.error("validateIATMsisdn", "Network Country mapping for IAT are not defined in system");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateIATMsisdn]", "", p_msisdn, "", "Network Country mapping for IAT are not defined in system");
                        throw new BTSLBaseException(PretupsErrorCodesI.IAT_NW_CNTRY_MAPPING_NOT_FOUND, new String[] { masCountryVO.getRecCountryCode() + p_msisdn, networkCntryVO.getRecNetworkPrefix(), masCountryVO.getRecCountryShortName(), masCountryVO.getRecCountryName() });
                    }
                } else {
                    _log.error("validateIATMsisdn", "Network Country mapping for IAT are not defined in system");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateIATMsisdn]", "", p_msisdn, "", "Network Country mapping for IAT are not defined in system");
                    throw new BTSLBaseException(PretupsErrorCodesI.IAT_NW_CNTRY_MAPPING_NOT_FOUND, new String[] { masCountryVO.getRecCountryCode() + p_msisdn, networkCntryVO.getRecNetworkPrefix(), masCountryVO.getRecCountryShortName(), masCountryVO.getRecCountryName() });
                }
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateIATMsisdn", "  Exception while validating msisdn :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateIATMsisdn]", "", "", "", "Exception while validating msisdn" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateIATMsisdn", PretupsErrorCodesI.IAT_C2S_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateIATMsisdn", "Exiting for p_msisdn= " + p_msisdn);
        }
        return masCountryVO;
    }

    private static IATCountryMasterVO getIATCountryVO(String[] p_messageArray, String p_msisdn, ReceiverVO p_receiverVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getIATCountryVO", "Entered p_msisdn:" + p_msisdn);
        }
        final String METHOD_NAME = "getIATCountryVO";
        IATCountryMasterVO masCountryVO = null;
        boolean isErrorMsgFound = true;
        // Get Country master cache object. Iterate through all VOs in the cache
        // object.
        // If country code of any vo matches with the starting digits of the
        // MSISDN. Pick the VO and return. If not found such VO, THROW
        // EXCEPTION.
        try {
            ArrayList iatCountryCache = IATCountryMasterCache.getIATCountryMasterObject();
            if (iatCountryCache != null) {
                int cacheLen = iatCountryCache.size();
                if (cacheLen > 0) {
                    for (int i = 0; i < cacheLen; i++) {
                        IATCountryMasterVO countryVO = (IATCountryMasterVO) iatCountryCache.get(i);
                        if (countryVO != null) {
                            if (p_msisdn.startsWith(String.valueOf(countryVO.getRecCountryCode()))) {
                                masCountryVO = countryVO;
                                isErrorMsgFound = false;
                                break;
                            }
                        }
                    }
                    if (isErrorMsgFound) {
                        p_receiverVO.setCountryCodeMatchError(p_messageArray[1]);
                        p_receiverVO.setErrorMsgFound(isErrorMsgFound);
                    }
                    if (masCountryVO == null) {
                        _log.error("getIATCountryVO", "MSISDN country code does not match with the list of country code maintained in PreTUPS ");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getCountryCode]", "", p_msisdn, "", "MSISDN country code does not match with the list of country code maintained in system");
                        // throw new
                        // BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_CODE_NOT_FOUND,new
                        // String[]{p_msisdn});
                    }
                } else {
                    _log.error("getIATCountryVO", "IAT country codes are not defined in system");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getCountryCode]", "", p_msisdn, "", "IAT country codes are not defined in system");
                    throw new BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_CODE_NOT_FOUND, new String[] { p_msisdn });
                }
            } else {
                _log.error("getIATCountryVO", "IAT country codes are not defined in system. Cache not available");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getCountryCode]", "", p_msisdn, "", "IAT country codes are not defined in system");
                throw new BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_CODE_NOT_FOUND, new String[] { p_msisdn });
            }
            /*
             * if(_log.isDebugEnabled())
             * _log.debug("getIATCountryVO","masCountryVO="+
             * masCountryVO.toString());
             */
            return masCountryVO;

        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getIATCountryVO", "Exception while getting IAT COUNTRY CODE. Exception: " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getCountryCode]", "", p_msisdn, "", "Exception while getting IAT COUNTRY CODE. Exception: " + e.getMessage());
            throw new BTSLBaseException(PretupsErrorCodesI.IAT_C2S_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getIATCountryVO", "Exited");
            }
        }
    }
}
