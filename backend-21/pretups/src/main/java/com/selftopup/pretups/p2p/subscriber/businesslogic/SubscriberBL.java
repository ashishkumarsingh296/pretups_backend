package com.selftopup.pretups.p2p.subscriber.businesslogic;

/*
 * SubscriberBL.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.IDGenerator;
import com.selftopup.common.TypesI;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsVO;
import com.selftopup.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.subscriber.businesslogic.BarredUserDAO;
import com.selftopup.pretups.subscriber.businesslogic.BarredUserVO;
import com.selftopup.pretups.subscriber.businesslogic.PostPaidControlParametersVO;
import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.subscriber.businesslogic.SubscriberVO;
import com.selftopup.pretups.transfer.businesslogic.TransferItemVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.pretups.util.OperatorUtil;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;

public class SubscriberBL {

    private static Log _log = LogFactory.getLog(SubscriberBL.class.getName());

    private static SubscriberDAO _subscriberDAO = new SubscriberDAO();

    /**
     * Get Subscriber Details
     * 
     * @param con
     * @param msisdn
     * @return
     * @throws Exception
     */
    public static SenderVO validateSubscriberDetails(Connection con, String msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateSubscriberDetails", " Entered msisdn:" + msisdn);
        SenderVO senderVO = null;
        try {
            senderVO = _subscriberDAO.loadSubscriberDetailsByMsisdn(con, msisdn);
            if (senderVO != null) {
                if (senderVO.getStatus().equals(PretupsI.USER_STATUS_ACTIVE))
                    return senderVO;
                else if (senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BLOCK))
                    throw new BTSLBaseException("SubscriberBL", "checkRequestUnderProcess", SelfTopUpErrorCodesI.P2P_ERROR_SENDER_BLOCKED);
                // else
                // if(senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_NEW))
                // throw new BTSLBaseException("SubscriberBL",
                // "checkRequestUnderProcess",
                // PretupsErrorCodesI.P2P_ERROR_SENDER_STATUS_NEW);
            }
        } catch (BTSLBaseException be) {
            _log.error("validateSubscriberDetails", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validateSubscriberDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[validateSubscriberDetails]", "", msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "validateSubscriberDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateSubscriberDetails", " Exiting for msisdn:" + msisdn + " senderVO=" + senderVO);
        return senderVO;
    }

    /**
     * Check Request Under Process
     * 
     * @param con
     * @param subscriberVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void checkRequestUnderProcess(Connection con, String requestID, SenderVO senderVO, boolean mark) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkRequestUnderProcess", requestID, "Entered senderVO msisdn:" + senderVO.getMsisdn());
        try {
            int count = 0;
            Date currentDate = new Date();
            if (mark) {
                if (senderVO.getRequestStatus() != null && senderVO.getRequestStatus().equals(PretupsI.TXN_STATUS_UNDER_PROCESS) && ((currentDate.getTime() - senderVO.getModifiedOn().getTime())) <= 300000)
                    throw new BTSLBaseException("SubscriberBL", "checkRequestUnderProcess", SelfTopUpErrorCodesI.P2P_ERROR_SUBS_REQ_UNDERPROCESS);

                senderVO.setModifiedOn(currentDate);
                count = _subscriberDAO.markRequestUnderProcess(con, senderVO);
            } else
                count = _subscriberDAO.unmarkRequestUnderProcess(con, senderVO);
            if (count <= 0) {
                throw new Exception(SelfTopUpErrorCodesI.P2P_ERROR_SUBS_REQUNDERPROCESS_NOTUPDATED);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("checkRequestUnderProcess", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[checkRequestUnderProcess]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "checkRequestUnderProcess", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("checkRequestUnderProcess", requestID, "Exiting");
    }

    /**
     * Check PIN
     * 
     * @param con
     * @param subscriberVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void checkPIN(Connection con, SenderVO senderVO) throws BTSLBaseException {
        if (senderVO.getRequestStatus().equals(PretupsI.TXN_STATUS_UNDER_PROCESS))
            throw new BTSLBaseException("SubscriberBL", "checkRequestUnderProcess", SelfTopUpErrorCodesI.P2P_ERROR_SUBS_REQ_UNDERPROCESS);
        else {
            try {
                int count = _subscriberDAO.markRequestUnderProcess(con, senderVO);
                if (count <= 0) {
                    throw new BTSLBaseException("SubscriberBL", "checkRequestUnderProcess", SelfTopUpErrorCodesI.P2P_ERROR_SUBS_REQUNDERPROCESS_NOTUPDATED);
                }
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                _log.error("checkPIN", "Exception " + e.getMessage());
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[checkPIN]", "", "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("SubscriberBL", "checkPIN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        }
    }

    /**
     * To register the p2p subscriber in our system
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public static int regsiterP2Psubscriber(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("regsiterP2Psubscriber", "Entered p_msisdn=" + p_senderVO);

        int status = 0;

        // genrate unique user id
        try {
            String idType = PretupsI.P2P_USER_ID;
            StringBuffer uniqueP2PsubscriberId = new StringBuffer();
            // generating the unique key of the table service_keywords .
            long interfaceId = IDGenerator.getNextID(p_con, PretupsI.P2P_USER_ID, TypesI.ALL);
            int zeroes = 15 - (idType.length() + Long.toString(interfaceId).length());
            for (int count = 0; count < zeroes; count++) {
                uniqueP2PsubscriberId.append(0);
            }
            uniqueP2PsubscriberId.insert(0, idType);
            uniqueP2PsubscriberId.append(Long.toString(interfaceId));

            p_senderVO.setUserID(uniqueP2PsubscriberId.toString());

            Date _currentDate = new Date(System.currentTimeMillis());
            p_senderVO.setRegisteredOn(_currentDate);
            p_senderVO.setCreatedOn(_currentDate);
            p_senderVO.setModifiedOn(_currentDate);
            if (p_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_PREPAID))
                p_senderVO.setActivatedOn(_currentDate);
            p_senderVO.setModifiedBy(TypesI.SYSTEM_USER);
            p_senderVO.setCreatedBy(TypesI.SYSTEM_USER);

            // Call the DAO method to register the user
            status = _subscriberDAO.registerSubscriber(p_con, p_senderVO);

        } catch (BTSLBaseException be) {
            _log.error("regsiterP2Psubscriber", "Exception " + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            _log.error("regsiterP2Psubscriber", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[regsiterP2Psubscriber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "regsiterP2Psubscriber", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("regsiterP2Psubscriber", "Exited  status = " + status);
        }
        return status;
    }

    /**
     * This method validates the requested PIN with that available in DB, also
     * checks whether to block user or reset the counter or not
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_requestPin
     * @throws BTSLBaseException
     * @author gurjeet singh
     */
    public static void validatePIN(Connection p_con, SenderVO p_senderVO, String p_requestPin) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validatePIN", "Entered with p_senderVO:" + p_senderVO.toString() + " p_requestPin=" + p_requestPin);
        try {
            OperatorUtilI operatorUtili = null;
            try {
                String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[validatePIN]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
            }
            operatorUtili.validatePIN(p_con, p_senderVO, p_requestPin);
        } catch (BTSLBaseException bex) {
            throw bex;
        } catch (Exception e) {
            _log.error("validatePIN", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[validatePIN]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "validatePIN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validatePIN", "Exiting ");
    }

    /**
     * Method deleteSubscriber. Added by Sandeep Goel Created On 22-06-2005 This
     * method
     * called a method of DAO to insert the record of subscriber in the history
     * table if insertion is successful the delete the record form the table.
     * 
     * @param con
     *            Connection
     * @param p_senderVO
     *            SenderVO
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static int deleteSubscriber(Connection con, SenderVO p_senderVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("deleteSubscriber", "Entered:SenderVO=" + p_senderVO);
        ArrayList buddyList = null;
        int subDeleteCount = 0;
        int deleteCount = 0;
        try {
            buddyList = p_senderVO.getVoList();
            if (buddyList != null && buddyList.size() > 0) {
                deleteCount = _subscriberDAO.deleteBuddiesList(con, p_senderVO);
                if (deleteCount <= 0)
                    throw new BTSLBaseException("SubscriberBL", "deleteSubscriber");
            }
            subDeleteCount = _subscriberDAO.deleteSubscriber(con, p_senderVO);
            if (subDeleteCount <= 0)
                throw new BTSLBaseException("SubscriberBL", "deleteSubscriber");
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("deleteSubscriber", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[deleteSubscriber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "deleteSubscriber");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("deleteSubscriber", "Exited:subDeleteCount =" + subDeleteCount);
        }

        return subDeleteCount;
    }

    /**
     * to get the buddy list of selected user.
     * 
     * @param p_con
     * @param p_parentID
     * @param p_buddy
     * @return String[]
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public static String loadBuddyListForSMS(ArrayList p_list) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadBuddyListForSMS", "Entered: ParentID:" + p_list);

        StringBuffer buddyListArr = new StringBuffer();
        try {

            buddyListArr = new StringBuffer();
            StringBuffer sbf = null;
            for (int i = 0, k = p_list.size(); i < k; i++) {
                BuddyVO buddyVO = (BuddyVO) p_list.get(i);

                sbf = new StringBuffer();
                sbf.append(buddyVO.getName());
                sbf.append(" ");
                sbf.append(buddyVO.getMsisdn());

                buddyListArr.append(sbf);
                buddyListArr.append(", ");
            }
        } catch (Exception e) {
            _log.error("loadBuddyListForSMS", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[loadBuddyListForSMS]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "loadBuddyListForSMS", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled() && buddyListArr != null)
                _log.debug("loadBuddyListForSMS", "Exited: buddyListArr size =" + buddyListArr.toString());
            else
                _log.debug("loadBuddyListForSMS", "Exited: ");
        }
        return buddyListArr.substring(0, buddyListArr.lastIndexOf(","));
    }

    /**
     * To check whether user transction stats is ambiguous or not
     * 
     * @param p_con
     * @param p_filteredMSISDN
     * @return boolean true if status is ambiguous
     * @throws BTSLBaseException
     */
    public static boolean checkAmbiguousTranscationStatus(Connection p_con, String p_filteredMSISDN) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("checkAmbiguousTranscationStatus", "Entered: Filtered MSISDN :" + p_filteredMSISDN);
        boolean status = false;

        try {

            status = _subscriberDAO.checkAmbiguousTransfer(p_con, p_filteredMSISDN);

        } catch (BTSLBaseException be) {
            _log.error("checkAmbiguousTranscationStatus", "Exception " + be.getMessage());
            be.printStackTrace();
            throw new BTSLBaseException("SubscriberBL", "checkAmbiguousTranscationStatus", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        catch (Exception e) {
            _log.error("checkAmbiguousTranscationStatus", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[checkAmbiguousTranscationStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "checkAmbiguousTranscationStatus", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkAmbiguousTranscationStatus", "Exited: Status: " + status);
        }
        return status;
    }

    /**
     * Method to validate the sender limits at various stages of the transaction
     * 
     * @param p_con
     * @param p_transferVO
     * @param p_stage
     * @throws BTSLBaseException
     */
    public static void validateSenderLimits(Connection p_con, P2PTransferVO p_transferVO, int p_stage) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateSenderLimits", p_transferVO.getRequestID(), "Entered p_stage:" + p_stage);
        String service_class = null;
        Object serviceObjVal = null;
        TransferItemVO senderItemVO = null;
        int addCount = 0;
        int updateCount = 0;
        SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        SubscriberVO subscriberVO = (SubscriberVO) p_transferVO.getReceiverVO();
        Date currentDate = p_transferVO.getCreatedOn();
        try {
            int updateCounters = 0;
            boolean isCounterReInitalizingReqd = isResetCountersAfterPeriodChange(senderVO, p_transferVO.getTransferID(), currentDate);

            if (p_stage == PretupsI.TRANS_STAGE_BEFORE_INVAL) {
                if (_log.isDebugEnabled())
                    _log.debug("validateSenderLimits", p_transferVO.getRequestID(), "p_stage TRANS_STAGE_BEFORE_INVAL");
                service_class = p_transferVO.getSenderAllServiceClassID();
                if (service_class != null && senderVO.getSubscriberType().equalsIgnoreCase(PretupsI.SERIES_TYPE_PREPAID)) {
                    checkTransactionControls(p_con, senderVO, service_class, p_transferVO.getRequestedAmount(), true, service_class);
                }
            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_INVAL) {
                if (_log.isDebugEnabled())
                    _log.debug("validateSenderLimits", p_transferVO.getRequestID(), "p_stage TRANS_STAGE_AFTER_INVAL");
                ArrayList transferItems = p_transferVO.getTransferItemList();
                senderItemVO = (TransferItemVO) transferItems.get(0);

                if (senderVO.getTransactionStatus().equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS)) {
                    service_class = senderItemVO.getServiceClass();
                    senderVO.setServiceClassCode(service_class);

                    checkTransactionControls(p_con, senderVO, service_class, p_transferVO.getRequestedAmount(), false, p_transferVO.getSenderAllServiceClassID());

                    if (senderItemVO.getPreviousGraceDate() != null) {
                        long currentDateLong = currentDate.getTime();
                        if (currentDateLong > senderItemVO.getPreviousGraceDate().getTime()) {
                            // Expired MSISDN
                            if (_log.isDebugEnabled())
                                _log.debug("validateSenderLimits", p_transferVO.getRequestID(), "Expired Grace Current Date=" + currentDate + " Long Time=" + currentDateLong + " Grace Period=" + senderItemVO.getPreviousGraceDate() + " Long time=" + senderItemVO.getPreviousGraceDate().getTime());
                            throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_ERROR_SENDER_MSISDN_EXPIRED);
                        }
                    }

                    /*
                     * TO BE DISCUSSED WITH SANJAY Commented for time being
                     * int minValidityDays=((Integer)PreferenceCache.
                     * getServicePreference
                     * (PreferenceI.MIN_VALIDITY_DAYS_CODE,senderVO
                     * .getNetworkCode
                     * (),PretupsI.P2P_MODULE,service_class)).intValue();
                     * if(senderItemVO.getValidity()<=minValidityDays)
                     * {
                     * String
                     * strArr[]={String.valueOf(senderItemVO.getValidity()
                     * ),String.valueOf(minValidityDays)};
                     * throw new
                     * BTSLBaseException("SubscriberBL","validateSenderLimits"
                     * ,PretupsErrorCodesI
                     * .P2P_MIN_VALIDITY_CHECK_FAILED,0,strArr,null);
                     * }
                     */
                } else {
                    if (!BTSLUtil.isNullString(senderVO.getInterfaceResponseCode())) {
                        // Here Based on the status of IN Validation this block
                        // will be performed whether consecutive failures and
                        // total
                        // Fail are to be increased and whether user has to be
                        // barred

                        String errorCodesForFail = BTSLUtil.NullToString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES));
                        if (errorCodesForFail == null || BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberBL[validateSenderLimits]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
                        } else if (!BTSLUtil.isNullString(errorCodesForFail) && (errorCodesForFail.indexOf(senderVO.getInterfaceResponseCode()) != -1)) // Means
                                                                                                                                                        // need
                                                                                                                                                        // to
                                                                                                                                                        // consider
                                                                                                                                                        // in
                                                                                                                                                        // fail
                                                                                                                                                        // case,
                                                                                                                                                        // then
                                                                                                                                                        // increase
                                                                                                                                                        // the
                                                                                                                                                        // fail
                                                                                                                                                        // count
                        {
                            senderVO.setTotalConsecutiveFailCount(senderVO.getTotalConsecutiveFailCount() + 1);
                            senderVO.setLastTransferStatus(p_transferVO.getTransferStatus());
                            senderVO.setLastTransferOn(currentDate);
                            senderVO.setModifiedOn(currentDate);
                            senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
                            senderVO.setLastTransferAmount(p_transferVO.getTransferValue());
                            senderVO.setLastTransferID(p_transferVO.getTransferID());
                            senderVO.setLastTransferType(p_transferVO.getServiceType());
                            senderVO.setLastTransferMSISDN(subscriberVO.getMsisdn());
                            senderVO.setLastTransferStatus(p_transferVO.getTransferStatus());
                        }
                    }
                }
            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_FIND_CGROUP) {
                if (_log.isDebugEnabled())
                    _log.debug("validateSenderLimits", p_transferVO.getRequestID(), "p_stage TRANS_STAGE_AFTER_FIND_CGROUP");
                senderItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(0);
                // This check is added by Ankit Z on date 07/03/2007
                // This is required because if IN does not send the balance in
                // validation response then balance check should not be done.
                if (senderItemVO.isBalanceCheckReq()) {
                    service_class = senderVO.getServiceClassCode();
                    serviceObjVal = PretupsBL.getServiceClassObject(service_class, PreferenceI.P2P_MAX_PTAGE_TRANSFER_CODE, senderVO.getNetworkCode(), PretupsI.P2P_MODULE, false, p_transferVO.getSenderAllServiceClassID());
                    Object serviceObjVal1 = PretupsBL.getServiceClassObject(service_class, PreferenceI.MIN_RESIDUAL_BAL_CODE, senderVO.getNetworkCode(), PretupsI.P2P_MODULE, false, p_transferVO.getSenderAllServiceClassID());
                    boolean flag = true;
                    int perTransfer = ((Integer) serviceObjVal).intValue();
                    long maxSenderAmt = (long) ((senderItemVO.getPreviousBalance() * ((double) (perTransfer) / (double) 100)));

                    if (_log.isDebugEnabled())
                        _log.debug("validateSenderLimits", p_transferVO.getRequestID(), "senderItemVO.getTransferValue(): " + senderItemVO.getTransferValue() + " maxSenderAmt: " + maxSenderAmt + " senderItemVO.getPreviousBalance(): " + senderItemVO.getPreviousBalance() + " perTransfer: " + perTransfer);
                    if (serviceObjVal1 != null) {
                        long resvalue = ((Long) serviceObjVal1).longValue();
                        if (resvalue < 0) {
                            maxSenderAmt = (long) (((senderItemVO.getPreviousBalance() - resvalue) * ((double) (perTransfer) / (double) 100)));
                        }
                    }
                    /*
                     * if(serviceObjVal1!=null)
                     * {
                     * long resvalue=((Long)serviceObjVal1).longValue();
                     * System.out.println("Ashish resvalue-->"+resvalue);
                     * if(resvalue<0)
                     * {
                     * flag =false;
                     * if(senderItemVO.getTransferValue()>senderItemVO.
                     * getPreviousBalance()-resvalue)
                     * //if(senderItemVO.getTransferValue()>maxSenderAmt-resvalue
                     * )
                     * {
                     * String strArr[]={PretupsBL.getDisplayAmount(senderItemVO.
                     * getTransferValue
                     * ()),PretupsBL.getDisplayAmount(maxSenderAmt
                     * ),String.valueOf(perTransfer)};
                     * if (PretupsI.SERIES_TYPE_PREPAID.equals(senderVO.
                     * getSubscriberType()))
                     * throw new
                     * BTSLBaseException("SubscriberBL","validateSenderLimits"
                     * ,PretupsErrorCodesI
                     * .P2P_MAX_PCT_TRANS_FAILED,0,strArr,null);
                     * else
                     * throw new
                     * BTSLBaseException("SubscriberBL","validateSenderLimits"
                     * ,PretupsErrorCodesI
                     * .P2P_POST_MAX_PCT_TRANS_FAILED,0,strArr,null);
                     * }
                     * }
                     * else
                     * {
                     * 
                     * }
                     * }
                     */
                    // if(senderItemVO.getTransferValue()>maxSenderAmt & flag)
                    if (senderItemVO.getTransferValue() > maxSenderAmt) {
                        String strArr[] = { PretupsBL.getDisplayAmount(senderItemVO.getTransferValue()), PretupsBL.getDisplayAmount(maxSenderAmt), String.valueOf(perTransfer) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(senderVO.getSubscriberType())) {
                            if (p_transferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER)) {
                                if (p_transferVO.getRequestVO().getMcdScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER))
                                    throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_SENDER_MAX_PCT_TRANS_FAILED_W, 0, strArr, null);
                                if (p_transferVO.getRequestVO().getMcdScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER))
                                    throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_SENDER_MAX_PCT_TRANS_FAILED_M, 0, strArr, null);
                            } else
                                throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_MAX_PCT_TRANS_FAILED, 0, strArr, null);
                        } else {
                            if (p_transferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER)) {
                                if (p_transferVO.getRequestVO().getMcdScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER))
                                    throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_SENDER_MAX_PCT_TRANS_FAILED_W, 0, strArr, null);
                                if (p_transferVO.getRequestVO().getMcdScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER))
                                    throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_SENDER_MAX_PCT_TRANS_FAILED_M, 0, strArr, null);
                            } else
                                throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_POST_MAX_PCT_TRANS_FAILED, 0, strArr, null);
                        }
                    }
                    // For minmum Residual Balance
                    // Not required in case of value 0
                    serviceObjVal = PretupsBL.getServiceClassObject(service_class, PreferenceI.MIN_RESIDUAL_BAL_CODE, senderVO.getNetworkCode(), PretupsI.P2P_MODULE, false, p_transferVO.getSenderAllServiceClassID());
                    long minResidualValue = ((Long) serviceObjVal).longValue();
                    // long
                    // minResidualValue=((Long)PreferenceCache.getServicePreference(PreferenceI.MIN_RESIDUAL_BAL_CODE,senderVO.getNetworkCode(),PretupsI.P2P_MODULE,service_class)).longValue();
                    if (_log.isDebugEnabled())
                        _log.debug("validateSenderLimits", p_transferVO.getRequestID(), " senderItemVO.getPreviousBalance(): " + senderItemVO.getPreviousBalance() + " senderItemVO.getTransferValue(): " + senderItemVO.getTransferValue() + " senderItemVO.getTransferValue(): " + senderItemVO.getTransferValue() + " minResidualValue: " + minResidualValue);
                    if (minResidualValue != 0) {
                        if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_RESIDUAL_BAL_TYPE).equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
                            if (senderItemVO.getPreviousBalance() - senderItemVO.getTransferValue() < minResidualValue) {
                                String strArr[] = { PretupsBL.getDisplayAmount(senderItemVO.getPreviousBalance()), PretupsBL.getDisplayAmount(minResidualValue), PretupsBL.getDisplayAmount(senderItemVO.getTransferValue()) };
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(senderVO.getSubscriberType())) {
                                    if (p_transferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER)) {
                                        if (p_transferVO.getRequestVO().getMcdScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER))
                                            throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_SENDER_MIN_RESI_BAL_CHECK_FAILED_W, 0, strArr, null);
                                        if (p_transferVO.getRequestVO().getMcdScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER))
                                            throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_SENDER_MIN_RESI_BAL_CHECK_FAILED_M, 0, strArr, null);
                                    } else
                                        throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_MIN_RESI_BAL_CHECK_FAILED, 0, strArr, null);
                                } else {
                                    if (p_transferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER)) {
                                        if (p_transferVO.getRequestVO().getMcdScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER))
                                            throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_SENDER_MIN_RESI_BAL_CHECK_FAILED_W, 0, strArr, null);
                                        if (p_transferVO.getRequestVO().getMcdScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER))
                                            throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_SENDER_MIN_RESI_BAL_CHECK_FAILED_M, 0, strArr, null);
                                    } else
                                        throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_POST_MIN_RESI_BAL_CHECK_FAILED, 0, strArr, null);
                                }
                            }
                        } else // if percentage
                        {
                            // if(((senderItemVO.getPreviousBalance()-senderItemVO.getTransferValue())/senderItemVO.getPreviousBalance())*100<minResidualValue)
                            if ((long) (((double) ((senderItemVO.getPreviousBalance() - senderItemVO.getTransferValue()) / (double) senderItemVO.getPreviousBalance())) * 100) < minResidualValue) {
                                String strArr[] = { PretupsBL.getDisplayAmount(senderItemVO.getPreviousBalance()), PretupsBL.getDisplayAmount(minResidualValue), PretupsBL.getDisplayAmount(senderItemVO.getTransferValue()) };
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(senderVO.getSubscriberType()))
                                    throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_MIN_RESI_BAL_CHECK_FAILED, 0, strArr, null);
                                else
                                    throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_POST_MIN_RESI_BAL_CHECK_FAILED, 0, strArr, null);
                            }
                        }
                    }
                }
            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_INTOP) {
                if (_log.isDebugEnabled())
                    _log.debug("validateSenderLimits", p_transferVO.getRequestID(), "p_stage TRANS_STAGE_AFTER_INTOP");
                senderVO.setLastTransferOn(currentDate);
                senderVO.setLastTransferID(p_transferVO.getTransferID());
                senderVO.setLastTransferAmount(p_transferVO.getTransferValue());
                senderVO.setLastTransferStatus(p_transferVO.getTransferStatus());
                senderVO.setModifiedOn(currentDate);
                senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
                senderVO.setLastTransferType(p_transferVO.getServiceType());
                senderVO.setLastTransferMSISDN(subscriberVO.getMsisdn());

                ArrayList transferItems = p_transferVO.getTransferItemList();
                senderItemVO = (TransferItemVO) transferItems.get(0);

                if (senderVO.getTransactionStatus().equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                    if (!BTSLUtil.isNullString(senderVO.getInterfaceResponseCode())) {
                        // Here Based on the status of IN Validation this block
                        // will be performed whether consecutive failures and
                        // total
                        // Fail are to be increased and whether user has to be
                        // barred

                        String errorCodesForFail = BTSLUtil.NullToString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES));
                        if (errorCodesForFail == null || BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberBL[validateSenderLimits]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
                        } else if (!BTSLUtil.isNullString(errorCodesForFail) && (errorCodesForFail.indexOf(senderVO.getInterfaceResponseCode()) >= 0)) // Means
                                                                                                                                                       // need
                                                                                                                                                       // to
                                                                                                                                                       // consider
                                                                                                                                                       // in
                                                                                                                                                       // fail
                                                                                                                                                       // case,
                                                                                                                                                       // then
                                                                                                                                                       // increase
                                                                                                                                                       // the
                                                                                                                                                       // fail
                                                                                                                                                       // count
                        {
                            senderVO.setTotalConsecutiveFailCount(senderVO.getTotalConsecutiveFailCount() + 1);
                        }
                    }
                }
            }
        } catch (BTSLBaseException bex) {
            bex.printStackTrace();
            _log.error("validateSenderLimits", "BTSLBaseException :" + bex);
            throw bex;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateSenderLimits", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[validateSenderLimits]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Not able to update subscriber details in users table");
            throw new BTSLBaseException("SubscriberBL", "validateSenderLimits", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateSenderLimits", "Exiting ");
    }

    /**
     * Method to check the controls the daily, weekly, monthly counts and amount
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_serviceClassCode
     * @param p_requestValue
     * @param p_isSystemPrefValRequired
     *            //Whether to get the System pref value if service preference
     *            value is not found
     * @throws BTSLBaseException
     *             Method modified for sender message with remaining threshold
     *             amount and count Manisha(01/02/08)
     */
    public static void checkTransactionControls(Connection p_con, SenderVO p_senderVO, String p_serviceClassCode, long p_requestValue, boolean p_isCheckMoreThanAllReqd, String p_allServiceClassID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkTransactionControls", "Entered with msisdn=" + p_senderVO.getMsisdn() + " p_serviceClassCode=" + p_serviceClassCode + " p_requestValue=" + p_requestValue + " p_isCheckMoreThanAllReqd=" + p_isCheckMoreThanAllReqd + "p_allServiceClassID=" + p_allServiceClassID);
        Object serviceObjVal = null;
        boolean isMoreServiceClassDefined = false;
        boolean checkControls = true;
        try {

            if (!p_senderVO.isMinTransferAmountCheckDone()) {
                serviceObjVal = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.P2P_MINTRNSFR_AMOUNT, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, p_isCheckMoreThanAllReqd, p_allServiceClassID);
                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setMinTransferAmountCheckDone(true);
                    if (p_requestValue < ((Long) serviceObjVal).longValue()) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_requestValue), PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MIN_TRANS_AMT_LESS, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MIN_TRANS_AMT_LESS, 0, strArr, null);
                    }
                }
            }
            serviceObjVal = null;

            if (!p_senderVO.isMaxTransferAmountCheckDone()) {
                serviceObjVal = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.P2P_MAXTRNSFR_AMOUNT, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, p_isCheckMoreThanAllReqd, p_allServiceClassID);
                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setMaxTransferAmountCheckDone(true);
                    if (p_requestValue > ((Long) serviceObjVal).longValue()) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_requestValue), PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MAX_TRANS_AMT_MORE, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MAX_TRANS_AMT_MORE, 0, strArr, null);
                    }
                }
            }

            // Will be used for Prepaid as well as if we have post paid Service
            // class wise
            serviceObjVal = null;
            if (!p_senderVO.isDailyTotalTransCountCheckDone()) {
                serviceObjVal = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.DAILY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, p_isCheckMoreThanAllReqd, p_allServiceClassID);

                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_MAX_TRFR_NUM_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setDailyTotalTransCountCheckDone(true);
                    if (p_senderVO.getDailyTransferCount() >= ((Integer) serviceObjVal).intValue()) {
                        String strArr[] = { String.valueOf(p_senderVO.getDailyTransferCount()), String.valueOf(((Integer) serviceObjVal).intValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_DAY_MAX_TRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_DAY_MAX_TRANS_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setDailyMaxTransCountThreshold(((Integer) serviceObjVal).intValue());
                }
            }
            serviceObjVal = null;

            if (!p_senderVO.isWeeklyTotalTransCountCheckDone()) {
                serviceObjVal = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.WEEKLY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, p_isCheckMoreThanAllReqd, p_allServiceClassID);
                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.WEEKLY_MAX_TRFR_NUM_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setWeeklyTotalTransCountCheckDone(true);
                    if (p_senderVO.getWeeklyTransferCount() >= ((Integer) serviceObjVal).intValue()) {
                        String strArr[] = { String.valueOf(p_senderVO.getWeeklyTransferCount()), String.valueOf(((Integer) serviceObjVal).intValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_WEEK_MAX_TRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_WEEK_MAX_TRANS_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setWeeklyMaxTransCountThreshold(((Integer) serviceObjVal).intValue());
                }
            }
            serviceObjVal = null;
            if (!p_senderVO.isMonthlyTotalTransCountCheckDone()) {
                serviceObjVal = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, p_isCheckMoreThanAllReqd, p_allServiceClassID);
                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setMonthlyTotalTransCountCheckDone(true);
                    if (p_senderVO.getMonthlyTransferCount() >= ((Integer) serviceObjVal).intValue()) {
                        String strArr[] = { String.valueOf(p_senderVO.getMonthlyTransferCount()), String.valueOf(((Integer) serviceObjVal).intValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MONTH_MAX_TRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MONTH_MAX_TRANS_THRESHOLD, 0, strArr, null);
                    }

                    p_senderVO.setMonthlyMaxTransCountThreshold(((Integer) serviceObjVal).intValue());
                }
            }

            serviceObjVal = null;
            if (!p_senderVO.isDailyTotalTransAmtCheckDone()) {

                serviceObjVal = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, p_isCheckMoreThanAllReqd, p_allServiceClassID);
                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setDailyTotalTransAmtCheckDone(true);
                    if (p_senderVO.getDailyTransferAmount() + p_requestValue > ((Long) serviceObjVal).longValue()) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getDailyTransferAmount()), PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()), PretupsBL.getDisplayAmount(p_requestValue) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_DAY_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setDailyMaxTransAmtThreshold(((Long) serviceObjVal).longValue());
                }
            }
            serviceObjVal = null;

            if (!p_senderVO.isWeeklyTotalTransAmtCheckDone()) {
                serviceObjVal = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.WEEKLY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, p_isCheckMoreThanAllReqd, p_allServiceClassID);
                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.WEEKLY_MAX_TRFR_AMOUNT_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setWeeklyTotalTransAmtCheckDone(true);
                    if (p_senderVO.getWeeklyTransferAmount() + p_requestValue > ((Long) serviceObjVal).longValue()) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getWeeklyTransferAmount()), PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()), PretupsBL.getDisplayAmount(p_requestValue) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setWeeklyMaxTransAmtThreshold(((Long) serviceObjVal).longValue());
                }
            }
            serviceObjVal = null;
            if (!p_senderVO.isMonthlyTotalTransAmtCheckDone()) {
                serviceObjVal = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, p_isCheckMoreThanAllReqd, p_allServiceClassID);
                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setMonthlyTotalTransAmtCheckDone(true);
                    long comareWithValue = ((Long) serviceObjVal).longValue();
                    // Changed to handle post paid Credit Limit Validations
                    boolean usingPrefValue = true;
                    if (p_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_POSTPAID)) {
                        if (p_senderVO.getCreditLimit() < comareWithValue) {
                            comareWithValue = p_senderVO.getCreditLimit();
                            usingPrefValue = false;
                        }
                        if (_log.isDebugEnabled())
                            _log.debug("checkTransactionControls", "For msisdn=" + p_senderVO.getMsisdn() + " p_serviceClassCode=" + p_serviceClassCode + " p_requestValue=" + p_requestValue + " Using compare value=" + comareWithValue + " for Preference MON_SDR_MX_TRANS_AMT for Post Paid User");
                    }
                    Object serviceObjVal1 = PretupsBL.getServiceClassObject(p_serviceClassCode, PreferenceI.MIN_RESIDUAL_BAL_CODE, p_senderVO.getNetworkCode(), PretupsI.P2P_MODULE, false, p_allServiceClassID);
                    boolean flag = true;
                    if (serviceObjVal1 != null) {
                        if (p_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_POSTPAID)) {
                            long comareWithValue1 = ((Long) serviceObjVal1).longValue();
                            if (comareWithValue1 < 0) {
                                flag = false;
                                if (_log.isDebugEnabled())
                                    _log.debug("checkTransactionControls", "DDDDDDDDDDDDDDDDDDDDDp_senderVO.getMonthlyTransferAmount()" + p_senderVO.getMonthlyTransferAmount() + " p_requestValue:" + p_requestValue + "comareWithValue" + comareWithValue + "comareWithValue1 " + comareWithValue1);
                                if (p_senderVO.getMonthlyTransferAmount() + p_requestValue > comareWithValue - comareWithValue1) {
                                    String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), PretupsBL.getDisplayAmount(comareWithValue - comareWithValue1), PretupsBL.getDisplayAmount(p_requestValue) };
                                    if (usingPrefValue)
                                        throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                                    else
                                        throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_NEGATIVE_RESIDUAL_BAL_THRESHOLD, 0, strArr, null);
                                }

                            }
                        }
                    }
                    if (p_senderVO.getMonthlyTransferAmount() + p_requestValue > comareWithValue & flag) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), PretupsBL.getDisplayAmount(comareWithValue), PretupsBL.getDisplayAmount(p_requestValue) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                        else if (usingPrefValue)
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MONTH_MAX_AMTTRANS_CCLMT_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setMonthlyMaxTransAmtThreshold(((Long) serviceObjVal).longValue());
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkTransactionControls", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[checkTransactionControls]", "", p_senderVO.getMsisdn(), "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to load the post paid control parameters that are stored
     * separately
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_requestValue
     * @throws BTSLBaseException
     */
    public static void checkPostPaidTransactionControls(Connection p_con, SenderVO p_senderVO, long p_requestValue) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkPostPaidTransactionControls", "Entered with MSISDN=" + p_senderVO.getMsisdn() + " p_requestValue=" + p_requestValue);
        try {
            PostPaidControlParametersVO postPaidControlParametersVO = _subscriberDAO.loadPostPaidControlParameters(p_con, p_senderVO.getMsisdn());
            if (postPaidControlParametersVO != null) {
                if (p_senderVO.getDailyTransferCount() >= postPaidControlParametersVO.getDailyTransferAllowed()) {
                    String strArr[] = { String.valueOf(p_senderVO.getDailyTransferCount()), String.valueOf(postPaidControlParametersVO.getDailyTransferAllowed()) };
                    throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_DAY_MAX_TRANS_THRESHOLD, 0, strArr, null);
                }
                if (p_senderVO.getWeeklyTransferCount() >= postPaidControlParametersVO.getWeeklyTransferAllowed()) {
                    String strArr[] = { String.valueOf(p_senderVO.getWeeklyTransferCount()), String.valueOf(postPaidControlParametersVO.getWeeklyTransferAllowed()) };
                    throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_WEEK_MAX_TRANS_THRESHOLD, 0, strArr, null);
                }
                if (p_senderVO.getMonthlyTransferCount() >= postPaidControlParametersVO.getMonthlyTransferAllowed()) {
                    String strArr[] = { String.valueOf(p_senderVO.getMonthlyTransferCount()), String.valueOf(postPaidControlParametersVO.getMonthlyTransferAllowed()) };
                    throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MONTH_MAX_TRANS_THRESHOLD, 0, strArr, null);
                }
                if (p_senderVO.getDailyTransferAmount() + p_requestValue > postPaidControlParametersVO.getDailyTransferAmountAllowed()) {
                    String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getDailyTransferAmount()), PretupsBL.getDisplayAmount(postPaidControlParametersVO.getDailyTransferAmountAllowed()) };
                    throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                }
                if (p_senderVO.getWeeklyTransferAmount() + p_requestValue > postPaidControlParametersVO.getWeeklyTransferAmountAllowed()) {
                    String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getDailyTransferAmount()), PretupsBL.getDisplayAmount(postPaidControlParametersVO.getWeeklyTransferAmountAllowed()) };
                    throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                }
                if (p_senderVO.getMonthlyTransferAmount() + p_requestValue > postPaidControlParametersVO.getMonthlyTransferAmountAllowed()) {
                    String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getDailyTransferAmount()), PretupsBL.getDisplayAmount(postPaidControlParametersVO.getMonthlyTransferAmountAllowed()) };
                    throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                }
                // added for updating sender message with remaining threshold
                // amount and count Manisha(01/02/08)
                p_senderVO.setMonthlyMaxTransAmtThreshold(postPaidControlParametersVO.getMonthlyTransferAmountAllowed());
                p_senderVO.setMonthlyMaxTransCountThreshold(postPaidControlParametersVO.getMonthlyTransferAllowed());
                p_senderVO.setDailyMaxTransAmtThreshold(postPaidControlParametersVO.getDailyTransferAmountAllowed());
                p_senderVO.setDailyMaxTransCountThreshold(postPaidControlParametersVO.getDailyTransferAllowed());
                p_senderVO.setWeeklyMaxTransAmtThreshold(postPaidControlParametersVO.getWeeklyTransferAmountAllowed());
                p_senderVO.setWeeklyMaxTransCountThreshold(postPaidControlParametersVO.getWeeklyTransferAllowed());

            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[checkPostPaidTransactionControls]", "", p_senderVO.getMsisdn(), "", "Not able to get the post paid control parameters");
                throw new BTSLBaseException("SubscriberBL", "checkPostPaidTransactionControls", SelfTopUpErrorCodesI.P2P_POSTPAID_USER_CTL_PARM_NOTDEFINED);
            }
        } catch (BTSLBaseException be) {
            _log.error("checkPostPaidTransactionControls", "Base Exception for MSISDN=" + p_senderVO.getMsisdn() + "= " + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkPostPaidTransactionControls", "Exception for MSISDN=" + p_senderVO.getMsisdn() + "= " + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "checkPostPaidTransactionControls", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("checkPostPaidTransactionControls", "Exiting for MSISDN=" + p_senderVO.getMsisdn());
    }

    /**
     * This method checks whether the period between two util dates needs to be
     * changed or not
     */
    public static boolean isResetCountersAfterPeriodChange(SenderVO p_senderVO, String p_transferID, java.util.Date p_newDate) {
        if (_log.isDebugEnabled())
            _log.debug("isResetCountersAfterPeriodChange", "Entered with p_transferID=" + p_transferID + " MSISDN=" + p_senderVO.getMsisdn() + " p_senderVO.getLastTransferOn()" + p_senderVO.getLastTransferOn());
        boolean isCounterChange = false;
        boolean isDayCounterChange = false;
        boolean isWeekCounterChange = false;
        boolean isMonthCounterChange = false;

        Date previousDate = p_senderVO.getLastSuccessTransferDate();

        if (previousDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(p_newDate);
            int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int presentMonth = cal.get(Calendar.MONTH);
            int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(previousDate);
            int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            int lastTrxMonth = cal.get(Calendar.MONTH);
            int lastTrxYear = cal.get(Calendar.YEAR);
            if (presentDay != lastTrxDay)
                isDayCounterChange = true;
            if (presentWeek != lastWeek)
                isWeekCounterChange = true;
            if (presentMonth != lastTrxMonth) {
                isDayCounterChange = true;
                isWeekCounterChange = true;
                isMonthCounterChange = true;
            }
            if (presentYear != lastTrxYear) {
                isDayCounterChange = true;
                isWeekCounterChange = true;
                isMonthCounterChange = true;
            }
            if (isDayCounterChange) {
                p_senderVO.setPrevDailyTransferCount(p_senderVO.getDailyTransferCount());
                p_senderVO.setPrevDailyTransferAmount(p_senderVO.getDailyTransferAmount());
                p_senderVO.setPrevTransferDate(p_senderVO.getLastSuccessTransferDate());
                p_senderVO.setDailyTransferCount(0);
                p_senderVO.setDailyTransferAmount(0);
                isCounterChange = true;
            }
            if (isWeekCounterChange) {
                p_senderVO.setPrevWeeklyTransferCount(p_senderVO.getWeeklyTransferCount());
                p_senderVO.setPrevWeeklyTransferAmount(p_senderVO.getWeeklyTransferAmount());
                p_senderVO.setPrevTransferWeekDate(p_senderVO.getLastSuccessTransferDate());
                p_senderVO.setWeeklyTransferCount(0);
                p_senderVO.setWeeklyTransferAmount(0);
                isCounterChange = true;
            }
            if (isMonthCounterChange) {
                p_senderVO.setPrevMonthlyTransferCount(p_senderVO.getMonthlyTransferCount());
                p_senderVO.setPrevMonthlyTransferAmount(p_senderVO.getMonthlyTransferAmount());
                p_senderVO.setPrevTransferMonthDate(p_senderVO.getLastSuccessTransferDate());
                p_senderVO.setMonthlyTransferCount(0);
                p_senderVO.setMonthlyTransferAmount(0);
                isCounterChange = true;
            }
        } else
            isCounterChange = true;

        if (_log.isDebugEnabled())
            _log.debug("isResetCountersAfterPeriodChange", "Exiting with isCounterChange=" + isCounterChange + " For MSISDN=" + p_senderVO.getMsisdn());
        return isCounterChange;
    }

    /**
     * Method to update the subscriber last transaction details
     * 
     * @param p_con
     * @param p_transferVO
     * @param p_senderVO
     * @param p_currentDate
     * @throws BTSLBaseException
     */
    public static void updateSubscriberLastDetails(Connection p_con, TransferVO p_transferVO, SenderVO p_senderVO, Date p_currentDate, String p_status) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateSubscriberLastDetails", "Entered with Transfer ID=" + p_transferVO.getTransferID() + " Msisdn=" + p_senderVO.getMsisdn() + " p_currentDate=" + p_currentDate);
        try {
            int addCount = 0;
            if (p_senderVO.getTotalConsecutiveFailCount() > SystemPreferences.DAILY_CNSTIVE_FAIL_COUNT_BEFOREBAR) {
                // Bar the user and update the reciever Consecutive Fail count=0
                BarredUserVO barredUserVO = prepareBarredUserVO(p_senderVO, PretupsI.BARRED_TYPE_SYSTEM, PretupsI.BARRED_USER_TYPE_SENDER, SelfTopUpErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN, PretupsI.SYSTEM_USER);

                addCount = new BarredUserDAO().addBarredUser(p_con, barredUserVO);
                if (addCount < 0) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateSubscriberLastDetails]", p_transferVO.getTransferID(), p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Not able to update subscriber details in users table");
                    throw new BTSLBaseException("SubscriberBL", "updateSubscriberLastDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            } else
                addCount = 1;

            int updateCounters = 0;
            ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            p_senderVO.setLastTransferOn(p_currentDate);
            p_senderVO.setModifiedOn(p_currentDate);
            p_senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
            p_senderVO.setLastTransferAmount(p_transferVO.getTransferValue());
            p_senderVO.setLastTransferID(p_transferVO.getTransferID());
            p_senderVO.setLastTransferType(p_transferVO.getServiceType());
            if (p_transferVO.getReceiverVO() != null)
                p_senderVO.setLastTransferMSISDN(receiverVO.getMsisdn());
            p_senderVO.setLastTransferStatus(p_status);
            p_senderVO.setLastTransferType(p_transferVO.getServiceType());
            if (p_status.equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS) && p_senderVO.isDefUserRegistration()) {
                p_senderVO.setActivateStatusReqd(true);
                p_senderVO.setActivatedOn(p_currentDate);
                p_senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            } else if (p_status.equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS) && p_senderVO.isActivateStatusReqd()) {
                p_senderVO.setActivatedOn(p_currentDate);
                p_senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            } else if (p_status.equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS) && p_senderVO.getStatus().equals(PretupsI.USER_STATUS_NEW)) {
                p_senderVO.setActivateStatusReqd(true);
                p_senderVO.setActivatedOn(p_currentDate);
                p_senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            }
            if (p_status.equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS) && p_senderVO.isPinUpdateReqd())
                p_senderVO.setPinUpdateReqd(true);
            else
                p_senderVO.setPinUpdateReqd(false);

            updateCounters = _subscriberDAO.updateSubscriberLastDetails(p_con, p_senderVO);
            if (updateCounters <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateSubscriberLastDetails]", p_transferVO.getTransferID(), p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Not able to update subscriber details in users table");
                throw new BTSLBaseException("SubscriberBL", "updateSubscriberLastDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            } else {
                Object objectVO = p_transferVO.getReceiverVO();
                if (objectVO instanceof BuddyVO) {
                    // Update the buddy counts
                    updateBuddyDetails(p_con, p_transferVO, p_currentDate, false, true);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("updateSubscriberLastDetails", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateSubscriberLastDetails]", p_transferVO.getTransferID(), "", "", "Not able to update subscriber details,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "updateSubscriberLastDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("updateSubscriberLastDetails", "Exiting for Transfer ID=" + p_transferVO.getTransferID());
    }

    /**
     * Method to update the buddy details
     * 
     * @param p_con
     * @param p_transferVO
     * @param p_currentDate
     * @throws BTSLBaseException
     */
    public static void updateBuddyDetails(Connection p_con, TransferVO p_transferVO, Date p_currentDate, boolean p_checkCounters, boolean p_increaseCounters) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateBuddyDetails", "Entered with Transfer ID=" + p_transferVO.getTransferID() + " p_currentDate=" + p_currentDate + "p_checkCounters=" + p_checkCounters + "p_increaseCounters=" + p_increaseCounters);
        BuddyVO buddyVO = (BuddyVO) p_transferVO.getReceiverVO();
        try {
            int updateCounters = 0;
            // Update the Buddy Success ful transfer Counts
            buddyVO.setLastTransferOn(p_currentDate);
            buddyVO.setModifiedOn(p_currentDate);
            buddyVO.setModifiedBy(PretupsI.SYSTEM_USER);
            buddyVO.setLastTransferID(p_transferVO.getTransferID());
            buddyVO.setLastTransferAmount(p_transferVO.getTransferValue());
            buddyVO.setLastTransferType(p_transferVO.getServiceType());
            if (p_checkCounters) {
                if (p_increaseCounters) {
                    buddyVO.setBuddyTotalTransfers(buddyVO.getBuddyTotalTransfers() + 1);
                    buddyVO.setBuddyTotalTransferAmount(buddyVO.getBuddyTotalTransferAmount() + p_transferVO.getRequestedAmount());
                } else {
                    if (buddyVO.getBuddyTotalTransfers() > 0)
                        buddyVO.setBuddyTotalTransfers(buddyVO.getBuddyTotalTransfers() - 1);
                    if (buddyVO.getBuddyTotalTransferAmount() >= p_transferVO.getRequestedAmount())
                        buddyVO.setBuddyTotalTransferAmount(buddyVO.getBuddyTotalTransferAmount() - p_transferVO.getRequestedAmount());
                }
            }
            updateCounters = _subscriberDAO.updateBuddyDetails(p_con, buddyVO);
            if (updateCounters <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateBuddyDetails]", p_transferVO.getTransferID(), buddyVO.getMsisdn(), buddyVO.getNetworkCode(), "Not able to update buddy details in users table");
                throw new BTSLBaseException("SubscriberBL", "updateBuddyDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            _log.error("updateBuddyDetails", "Exception :" + be);
            throw new BTSLBaseException("SubscriberBL", "updateBuddyDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        catch (Exception e) {
            e.printStackTrace();
            _log.error("updateBuddyDetails", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateBuddyDetails]", p_transferVO.getTransferID(), "", "", "Not able to update buddy details,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "updateBuddyDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("updateBuddyDetails", "Exiting for Transfer ID=" + p_transferVO.getTransferID());
    }

    /**
     * Increases the Subscriber and Buddy Counters
     * 
     * @param p_con
     * @param p_serviceClass
     * @param p_p2pTransferVO
     * @throws BTSLBaseException
     */
    public static void increaseTransferOutCounts(Connection p_con, String p_serviceClass, P2PTransferVO p_p2pTransferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("increaseTransferOutCounts", p_p2pTransferVO.getTransferID() + " Entered User ID " + p_p2pTransferVO.getSenderID());
        SenderVO senderVO = null;
        try {
            // Storing the Credit Limit as this will not be loaded from DB but
            // is needed for checkTransactionControls()
            long oldCreditLimit = ((SenderVO) p_p2pTransferVO.getSenderVO()).getCreditLimit();
            senderVO = _subscriberDAO.loadSubscriberDetailsByIDForUpdate(p_con, p_p2pTransferVO.getTransferID(), p_p2pTransferVO.getSenderID());
            Date currentDate = p_p2pTransferVO.getTransferDate();
            senderVO.setCreditLimit(oldCreditLimit);
            boolean isCounterReInitalizingReqd = isResetCountersAfterPeriodChange(senderVO, p_p2pTransferVO.getTransferID(), currentDate);
            checkSenderTransactionControls(p_con, senderVO, p_serviceClass, p_p2pTransferVO.getRequestedAmount(), false, p_p2pTransferVO.getSenderAllServiceClassID());
            senderVO.setDailyTransferCount(senderVO.getDailyTransferCount() + 1);
            senderVO.setDailyTransferAmount(senderVO.getDailyTransferAmount() + p_p2pTransferVO.getRequestedAmount());
            senderVO.setWeeklyTransferCount(senderVO.getWeeklyTransferCount() + 1);
            senderVO.setWeeklyTransferAmount(senderVO.getWeeklyTransferAmount() + p_p2pTransferVO.getRequestedAmount());
            senderVO.setMonthlyTransferCount(senderVO.getMonthlyTransferCount() + 1);
            senderVO.setMonthlyTransferAmount(senderVO.getMonthlyTransferAmount() + p_p2pTransferVO.getRequestedAmount());
            senderVO.setTotalTransfers(senderVO.getTotalTransfers() + 1);
            senderVO.setTotalTransferAmount(senderVO.getTotalTransferAmount() + p_p2pTransferVO.getRequestedAmount());
            senderVO.setLastTransferOn(currentDate);
            senderVO.setModifiedOn(currentDate);
            senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
            senderVO.setLastTransferAmount(p_p2pTransferVO.getTransferValue());
            senderVO.setLastTransferID(p_p2pTransferVO.getTransferID());
            senderVO.setLastTransferType(p_p2pTransferVO.getServiceType());
            senderVO.setLastTransferMSISDN(p_p2pTransferVO.getReceiverMsisdn());
            senderVO.setLastTransferStatus(SelfTopUpErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
            senderVO.setLastTransferType(p_p2pTransferVO.getServiceType());
            senderVO.setModifiedBy(p_p2pTransferVO.getSenderID());
            senderVO.setModifiedOn(p_p2pTransferVO.getModifiedOn());
            int updateCounters = _subscriberDAO.updateSubscriberCountersDetails(p_con, senderVO);
            if (updateCounters <= 0) {
                throw new BTSLBaseException("SubscriberBL", "increaseTransferOutCounts", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            } else {
                Object objectVO = p_p2pTransferVO.getReceiverVO();
                if (objectVO instanceof BuddyVO) {
                    // Update the Buddy Success ful transfer Counts
                    updateBuddyDetails(p_con, p_p2pTransferVO, currentDate, true, true);
                }
            }

        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("increaseTransferOutCounts", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateBuddyDetails]", p_p2pTransferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Not able to update buddy details,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "increaseTransferOutCounts", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Decreases the Subscriber and Buddy Counters
     * 
     * @param p_con
     * @param p_p2pTransferVO
     * @throws BTSLBaseException
     */
    public static void decreaseTransferOutCounts(Connection p_con, P2PTransferVO p_p2pTransferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("decreaseTransferOutCounts", p_p2pTransferVO.getTransferID() + " Entered User ID " + p_p2pTransferVO.getSenderID());
        String[] strArr = null;
        boolean isLockRecordForUpdate = true;
        boolean isInsertRecord = false;
        SenderVO senderVO = null;
        try {
            senderVO = _subscriberDAO.loadSubscriberDetailsByIDForUpdate(p_con, p_p2pTransferVO.getTransferID(), p_p2pTransferVO.getSenderID());
            Date currentDate = p_p2pTransferVO.getTransferDate();
            boolean isCounterReInitalizingReqd = isResetCountersAfterPeriodChange(senderVO, p_p2pTransferVO.getTransferID(), currentDate);
            if (senderVO.getDailyTransferCount() > 0)
                senderVO.setDailyTransferCount(senderVO.getDailyTransferCount() - 1);
            if (senderVO.getDailyTransferAmount() >= p_p2pTransferVO.getTransferValue())
                senderVO.setDailyTransferAmount(senderVO.getDailyTransferAmount() - p_p2pTransferVO.getTransferValue());
            if (senderVO.getWeeklyTransferCount() > 0)
                senderVO.setWeeklyTransferCount(senderVO.getWeeklyTransferCount() - 1);
            if (senderVO.getWeeklyTransferAmount() >= p_p2pTransferVO.getTransferValue())
                senderVO.setWeeklyTransferAmount(senderVO.getWeeklyTransferAmount() - p_p2pTransferVO.getTransferValue());
            if (senderVO.getMonthlyTransferCount() > 0)
                senderVO.setMonthlyTransferCount(senderVO.getMonthlyTransferCount() - 1);
            if (senderVO.getMonthlyTransferAmount() >= p_p2pTransferVO.getTransferValue())
                senderVO.setMonthlyTransferAmount(senderVO.getMonthlyTransferAmount() - p_p2pTransferVO.getTransferValue());
            if (senderVO.getTotalTransfers() > 0)
                senderVO.setTotalTransfers(senderVO.getTotalTransfers() - 1);
            if (senderVO.getTotalTransferAmount() >= p_p2pTransferVO.getTransferValue())
                senderVO.setTotalTransferAmount(senderVO.getTotalTransferAmount() - p_p2pTransferVO.getTransferValue());
            senderVO.setLastTransferOn(currentDate);
            senderVO.setModifiedOn(currentDate);
            senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
            senderVO.setLastTransferAmount(p_p2pTransferVO.getTransferValue());
            senderVO.setLastTransferID(p_p2pTransferVO.getTransferID());
            senderVO.setLastTransferType(p_p2pTransferVO.getServiceType());
            senderVO.setLastTransferMSISDN(p_p2pTransferVO.getReceiverMsisdn());
            senderVO.setLastTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            senderVO.setLastTransferType(p_p2pTransferVO.getServiceType());

            // Set intentionally to get the latest count
            ((SenderVO) p_p2pTransferVO.getSenderVO()).setConsecutiveFailures(senderVO.getConsecutiveFailures());

            int updateCounters = _subscriberDAO.updateSubscriberCountersDetails(p_con, senderVO);
            if (updateCounters <= 0) {
                throw new BTSLBaseException("SubscriberBL", "increaseTransferOutCounts", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            } else {
                Object objectVO = p_p2pTransferVO.getReceiverVO();
                if (objectVO instanceof BuddyVO) {
                    // Update the Buddy Success ful transfer Counts
                    updateBuddyDetails(p_con, p_p2pTransferVO, currentDate, true, false);
                }
            }

        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("increaseTransferOutCounts", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateBuddyDetails]", p_p2pTransferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Not able to update buddy details,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "increaseTransferOutCounts", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to bar the MSISDN
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_currentDate
     * @param p_barredType
     * @throws BTSLBaseException
     */
    public static void barSenderMSISDN(Connection p_con, SenderVO p_senderVO, String p_barredType, Date p_currentDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("barSenderMSISDN", "Entered with Sender MSISDN=" + p_senderVO.getMsisdn() + " p_currentDate=" + p_currentDate);
        try {
            int addCount = 0;
            BarredUserVO barredUserVO = prepareBarredUserVO(p_senderVO, p_barredType, PretupsI.BARRED_USER_TYPE_SENDER, SelfTopUpErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN, PretupsI.SYSTEM_USER);

            addCount = new BarredUserDAO().addBarredUser(p_con, barredUserVO);
            if (addCount < 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[barSenderMSISDN]", "", p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Not able to bar the sender MSISDN");
                throw new BTSLBaseException("SubscriberBL", "barSenderMSISDN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberBL[barSenderMSISDN]","",p_senderVO.getMsisdn(),p_senderVO.getNetworkCode(),"Not able to bar the sender MSISDN");
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("increaseTransferOutCounts", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[barSenderMSISDN]", "", p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Not able to bar the sender MSISDN,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "barSenderMSISDN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * 
     * @param p_senderVO
     * @param p_barredType
     *            it can be CC,Self, System
     * @param p_userType
     *            it can be Sender,Receiver
     * @param p_reason
     *            reason to barred the user
     * @return BarredUserVO
     */
    public static BarredUserVO prepareBarredUserVO(SenderVO p_senderVO, String p_barredType, String p_userType, String p_reason, String p_createdBy) {
        BarredUserVO barredUserVO = new BarredUserVO();
        barredUserVO.setModule(p_senderVO.getModule());
        barredUserVO.setMsisdn(p_senderVO.getMsisdn());
        barredUserVO.setBarredType(p_barredType);
        barredUserVO.setCreatedBy(p_createdBy);
        barredUserVO.setCreatedOn(p_senderVO.getModifiedOn());
        barredUserVO.setNetworkCode(p_senderVO.getNetworkCode());
        barredUserVO.setModifiedBy(p_senderVO.getModifiedBy());
        barredUserVO.setModifiedOn(p_senderVO.getModifiedOn());
        barredUserVO.setUserType(p_userType);
        try {
            barredUserVO.setBarredReason(URLDecoder.decode(BTSLUtil.getMessage(p_senderVO.getLocale(), p_reason, null), "UTF16"));
        } catch (Exception e) {
            _log.error("prepareBarredUserVO", "Exception while decoding message e=" + e.getMessage());
            e.printStackTrace();
            barredUserVO.setBarredReason("N.A.");
        }
        return barredUserVO;
    }

    /**
     * Update network prefix ID of the registered P2P subscriber
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_prefixID
     * @throws BTSLBaseException
     * @author sanjeew.kumar
     * @Date 25/01/08
     */
    public static void updateSubscriberPrefixID(Connection p_con, SenderVO p_senderVO, long p_prefixID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateSubscriberPrefixID", "Entered with Msisdn=" + p_senderVO.getMsisdn() + " p_prefixID=" + p_prefixID);
        int updateCounters = 0;
        try {
            String msisdnPrefix = PretupsBL.getMSISDNPrefix(p_senderVO.getMsisdn());
            p_senderVO.setMsisdnPrefix(msisdnPrefix);

            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, p_senderVO.getSubscriberType());

            if (networkPrefixVO.getPrefixID() != p_senderVO.getPrefixID()) {
                p_senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                updateCounters = _subscriberDAO.updateSubscriberPrefixID(p_con, p_senderVO, networkPrefixVO.getPrefixID());
                if (updateCounters <= 0) {
                    throw new BTSLBaseException("SubscriberBL", "updateSubscriberPrefixID", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            _log.error("updateSubscriberPrefixID", "Exception :" + be);
            throw new BTSLBaseException("SubscriberBL", "updateSubscriberPrefixID", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        catch (Exception e) {
            e.printStackTrace();
            _log.error("updateSubscriberPrefixID", "Exception :" + e);
            throw new BTSLBaseException("SubscriberBL", "updateSubscriberPrefixID", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("updateBuddyDetails", "Exiting for Msisdn=" + p_senderVO.getMsisdn() + " p_prefixID=" + p_prefixID + " updateCounters" + updateCounters);
    }

    /**
     * Get Subscriber Details to check P2P registeration expiry,,,ranjana
     * 
     * @param con
     * @param msisdn
     * @return
     * @throws Exception
     */
    public static boolean validateSusbscriberExpiry(Connection con, String msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateSusbscriberExpiry", " Entered msisdn:" + msisdn);
        SenderVO senderVO = null;
        boolean regAllowed = false;
        try {
            senderVO = _subscriberDAO.loadSubscriberDetailsByMsisdn(con, msisdn);
            // System.out.println(""+senderVO.getLastTransferOn());
            if (senderVO != null) {
                Date currentdate = new Date();
                // long
                // idledays=BTSLUtil.getDifferenceOfDate(senderVO.getLastTransferOn(),currentdate);
                long idledays = BTSLUtil.getDifferenceInUtilDates(senderVO.getLastTransferOn(), currentdate);
                long expirePeriod = SystemPreferences.P2P_REG_EXPIRY_PERIOD;

                if (idledays > expirePeriod)
                    regAllowed = true;
                else {
                    regAllowed = false;
                    throw new BTSLBaseException("SubscriberBL", "validateSusbscriberExpiry", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("validateSusbscriberExpiry", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validateSusbscriberExpiry", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[validateSusbscriberExpiry]", "", msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "validateSusbscriberExpiry", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateSusbscriberExpiry", " Exiting for msisdn:" + msisdn + "  regAllowed=" + regAllowed);
        return regAllowed;
    }

    /**
     * Method validateSenderEligibilityCriteria
     * Used to validate the subscriber eligibility criteria for SOS recharge
     * service
     * 
     * @param con
     *            Connection
     * @param p_transferVO
     *            P2PTransferVO
     * @param p_stage
     *            int
     * @return
     * @throws Exception
     */
    public static void validateSenderEligibilityCriteria(Connection p_con, P2PTransferVO p_transferVO, int p_stage) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateSenderEligibilityCriteria", p_transferVO.getRequestID(), "Entered p_stage:" + p_stage);
        TransferItemVO senderItemVO = null;
        SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        HashMap creteriaMap = null;
        long systemEligibiltyBalValue = 0;
        long subscriberBalValue = 0;
        try {
            ArrayList transferItems = p_transferVO.getTransferItemList();
            senderItemVO = (TransferItemVO) transferItems.get(0);
            // Lohit for eleigibilty
            String eligiblityAccString[] = ((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_ELIBILITY_ACCOUNT, senderVO.getNetworkCode())).toString().split(",");
            Long eligiblityBalString = ((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_ALLOWED_MAX_BALANCE, senderVO.getNetworkCode())).longValue();
            creteriaMap = new HashMap();
            for (int i = 0; i < eligiblityAccString.length; i++) {
                if (!BTSLUtil.isNullString(Long.toString(eligiblityBalString)))
                    creteriaMap.put(eligiblityAccString[i], Long.toString(eligiblityBalString));
                else
                    creteriaMap.put(eligiblityAccString[i], "0");
            }

            // End of change for eligibilty
            if (senderItemVO.getPreviousExpiry() != null) {
                int minValiditydays = ((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_MIN_VALIDITY_DAYS, p_transferVO.getNetworkCode())).intValue();
                Date currentDate = new Date();
                if (BTSLUtil.getDifferenceInUtilDates(currentDate, senderItemVO.getPreviousExpiry()) < minValiditydays) {
                    String arr[] = { BTSLUtil.getDateStringFromDate(senderItemVO.getPreviousExpiry()), String.valueOf(SystemPreferences.SOS_MIN_VALIDITY_DAYS) };
                    if (_log.isDebugEnabled())
                        _log.debug("validateSenderEligibilityCriteria", p_transferVO.getRequestID(), "Expiry Date=" + senderItemVO.getPreviousExpiry() + ", Current date=" + currentDate + ", Minimum Allowed validity days=" + minValiditydays);
                    throw new BTSLBaseException("SubscriberBL", "validateSenderEligibilityCriteria", SelfTopUpErrorCodesI.SOS_LESS_VAL_DAYS, arr);
                }
            }
            if (senderItemVO.getAccountStatus() != null && !senderItemVO.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
                if (_log.isDebugEnabled())
                    _log.debug("validateSenderEligibilityCriteria", p_transferVO.getRequestID(), "Account Status=" + senderItemVO.getAccountStatus());
                throw new BTSLBaseException("SubscriberBL", "validateSenderEligibilityCriteria", SelfTopUpErrorCodesI.SOS_SUBS_NOT_ACTIVE);
            }
            if (senderItemVO.getPreviousBalance() < 0.0) {
                if (_log.isDebugEnabled())
                    _log.debug("validateSenderEligibilityCriteria", p_transferVO.getRequestID(), "Lmb previous balance=" + senderItemVO.getPreviousBalance());
                throw new BTSLBaseException("SubscriberBL", "validateSenderEligibilityCriteria", SelfTopUpErrorCodesI.SOS_SUBS_CORE_BAL_NEGATIVE);
            }
            // Lohit
            if (p_transferVO.getRequestedAmount() > SystemPreferences.SOS_RECHARGE_AMOUNT) {
                if (_log.isDebugEnabled())
                    _log.debug("validateSenderEligibilityCriteria", p_transferVO.getRequestID(), "Lmb requestd amount=" + p_transferVO.getRequestedAmount() + ",maximum lmb allowed:" + SystemPreferences.SOS_RECHARGE_AMOUNT);
                throw new BTSLBaseException("SubscriberBL", "validateSenderEligibilityCriteria", SelfTopUpErrorCodesI.SOS_REQ_AMT_MORE);
            }
            for (Object key : senderItemVO.getBalanceMap().keySet()) {
                subscriberBalValue = Long.parseLong((String) senderItemVO.getBalanceMap().get(key));
                systemEligibiltyBalValue = Long.parseLong((String) creteriaMap.get(key));
                if (subscriberBalValue > systemEligibiltyBalValue) {
                    String arr[] = { senderItemVO.getPreviousBalanceAsString(), PretupsBL.getDisplayAmount(systemEligibiltyBalValue), (String) key };

                    if (_log.isDebugEnabled())
                        _log.debug("validateSenderEligibilityCriteria", p_transferVO.getRequestID(), "previous balance name=" + key + ",balance value=" + PretupsBL.getDisplayAmount(subscriberBalValue).toString() + ",eligibilty value of balance=" + PretupsBL.getDisplayAmount(systemEligibiltyBalValue).toString());
                    throw new BTSLBaseException("SubscriberBL", "validateSenderEligibilityCriteria", SelfTopUpErrorCodesI.SOS_MAX_BAL_ALLOWED, arr);
                }
            }
        } catch (BTSLBaseException bex) {
            bex.printStackTrace();
            _log.error("validateSenderEligibilityCriteria", "BTSLBaseException :" + bex);
            throw bex;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateSenderEligibilityCriteria", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[validateSenderEligibilityCriteria]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception while validity the subscriber eligibility criteria");
            throw new BTSLBaseException("SubscriberBL", "validateSenderEligibilityCriteria", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateSenderEligibilityCriteria", "Exiting ");
    }

    public static void updateMCDSubscriberLastDetails(Connection p_con, SenderVO p_senderVO, Date p_currentDate, String p_status) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateMCDSubscriberLastDetails", " Msisdn=" + p_senderVO.getMsisdn() + " p_currentDate=" + p_currentDate);
        try {
            int addCount = 0;

            int updateCounters = 0;

            if (p_status.equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS) && p_senderVO.isDefUserRegistration()) {
                p_senderVO.setActivateStatusReqd(true);
                p_senderVO.setActivatedOn(p_currentDate);
                p_senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            } else if (p_status.equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS) && p_senderVO.getStatus().equals(PretupsI.USER_STATUS_NEW)) {
                p_senderVO.setActivateStatusReqd(true);
                p_senderVO.setActivatedOn(p_currentDate);
                p_senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            }
            if (p_status.equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS) && p_senderVO.isPinUpdateReqd())
                p_senderVO.setPinUpdateReqd(true);
            else
                p_senderVO.setPinUpdateReqd(false);

            updateCounters = _subscriberDAO.updateSubscriberLastDetails(p_con, p_senderVO);
            if (updateCounters <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateMCDSubscriberLastDetails]", null, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Not able to update subscriber details in users table");
                throw new BTSLBaseException("SubscriberBL", "updateMCDSubscriberLastDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("updateMCDSubscriberLastDetails", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[updateMCDSubscriberLastDetails]", null, "", "", "Not able to update subscriber details,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "updateMCDSubscriberLastDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("updateMCDSubscriberLastDetails", null);
    }

    /**
     * To register the p2p subscriber during self topup in our system
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public static int regsiterP2PSelfTopUpsubscriber(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("regsiterP2PSelfTopUpsubscriber", "Entered p_msisdn=" + p_senderVO);

        int status = 0;
        String subscriberPassword = null;
        String randomPassword = null;
        OperatorUtil operatorUtil = new OperatorUtil();

        // genrate unique user id
        try {
            String idType = PretupsI.P2P_USER_ID;
            StringBuffer uniqueP2PsubscriberId = new StringBuffer();
            // generating the unique key of the table service_keywords .
            long interfaceId = IDGenerator.getNextID(p_con, PretupsI.P2P_USER_ID, TypesI.ALL);
            int zeroes = 15 - (idType.length() + Long.toString(interfaceId).length());
            for (int count = 0; count < zeroes; count++) {
                uniqueP2PsubscriberId.append(0);
            }
            uniqueP2PsubscriberId.insert(0, idType);
            uniqueP2PsubscriberId.append(Long.toString(interfaceId));

            p_senderVO.setUserID(uniqueP2PsubscriberId.toString());
            randomPassword = operatorUtil.randomPwdGenerate();
            subscriberPassword = BTSLUtil.encryptText(randomPassword);
            p_senderVO.setPassword(subscriberPassword);

            Date _currentDate = new Date(System.currentTimeMillis());
            p_senderVO.setRegisteredOn(_currentDate);
            p_senderVO.setCreatedOn(_currentDate);
            p_senderVO.setModifiedOn(_currentDate);
            if (p_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_PREPAID))
                p_senderVO.setActivatedOn(_currentDate);
            p_senderVO.setModifiedBy(TypesI.SYSTEM_USER);
            p_senderVO.setCreatedBy(TypesI.SYSTEM_USER);
            p_senderVO.setEncryptionKey(BTSLUtil.genrateAESKey());

            // Call the DAO method to register the user
            status = _subscriberDAO.registerSelfTopUpSubscriber(p_con, p_senderVO);

        } catch (BTSLBaseException be) {
            _log.error("regsiterP2PSelfTopUpsubscriber", "Exception " + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            _log.error("regsiterP2PSelfTopUpsubscriber", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[regsiterP2PSelfTopUpsubscriber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "regsiterP2PSelfTopUpsubscriber", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("regsiterP2PSelfTopUpsubscriber", "Exited  status = " + status);
        }
        return status;
    }

    /**
     * @param p_list
     * @return
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public static String loadCreditCardListForSMS(ArrayList p_list) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadCreditCardListForSMS", "Entered: ParentID:" + p_list);

        StringBuffer creditCardListArr = new StringBuffer();
        try {

            creditCardListArr = new StringBuffer();
            StringBuffer sbf = null;
            for (int i = 0, k = p_list.size(); i < k; i++) {
                CardDetailsVO cardDetailsVO = (CardDetailsVO) p_list.get(i);

                sbf = new StringBuffer();
                sbf.append(cardDetailsVO.getDisplayCardNumber());
                sbf.append(";");
                sbf.append(cardDetailsVO.getNameOfEmbossing());
                sbf.append(";");
                sbf.append(cardDetailsVO.getCardType());
                sbf.append(";");
                sbf.append(cardDetailsVO.getExpiryDate());
                sbf.append(";");
                sbf.append(cardDetailsVO.getCardNickName());
                sbf.append(";");

                creditCardListArr.append(sbf);
                creditCardListArr.append("#");
            }
        } catch (Exception e) {
            _log.error("loadCreditCardListForSMS", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[loadCreditCardListForSMS]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "loadCreditCardListForSMS", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled() && creditCardListArr != null)
                _log.debug("loadCreditCardListForSMS", "Exited: buddyListArr size =" + creditCardListArr.toString());
            else
                _log.debug("loadCreditCardListForSMS", "Exited: ");
        }
        return creditCardListArr.substring(0, creditCardListArr.lastIndexOf("#"));
    }

    public static void checkSenderTransactionControls(Connection p_con, SenderVO p_senderVO, String p_serviceClassCode, long p_requestValue, boolean p_isCheckMoreThanAllReqd, String p_allServiceClassID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkTransactionControls", "Entered with msisdn=" + p_senderVO.getMsisdn() + " p_serviceClassCode=" + p_serviceClassCode + " p_requestValue=" + p_requestValue + " p_isCheckMoreThanAllReqd=" + p_isCheckMoreThanAllReqd + "p_allServiceClassID=" + p_allServiceClassID);
        Object serviceObjVal = null;
        try {

            if (!p_senderVO.isMinTransferAmountCheckDone()) {
                serviceObjVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_MINTRNSFR_AMOUNT, p_senderVO.getNetworkCode());
                // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE,p_senderVO.getNetworkCode(),PretupsI.P2P_MODULE,p_serviceClassCode,p_isSystemPrefValRequired);
                if (serviceObjVal != null) {
                    p_senderVO.setMinTransferAmountCheckDone(true);
                    if (p_requestValue < ((Long) serviceObjVal).longValue()) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_requestValue), PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MIN_TRANS_AMT_LESS, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MIN_TRANS_AMT_LESS, 0, strArr, null);
                    }
                }
            }
            serviceObjVal = null;

            if (!p_senderVO.isMaxTransferAmountCheckDone()) {
                serviceObjVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_MAXTRNSFR_AMOUNT, p_senderVO.getNetworkCode());
                if (serviceObjVal != null) {
                    p_senderVO.setMaxTransferAmountCheckDone(true);
                    if (p_requestValue > ((Long) serviceObjVal).longValue()) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_requestValue), PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MAX_TRANS_AMT_MORE, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MAX_TRANS_AMT_MORE, 0, strArr, null);
                    }
                }
            }

            // Will be used for Prepaid as well as if we have post paid Service
            // class wise
            serviceObjVal = null;
            if (!p_senderVO.isDailyTotalTransCountCheckDone()) {
                serviceObjVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DAILY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode());
                if (serviceObjVal != null) {
                    p_senderVO.setDailyTotalTransCountCheckDone(true);
                    if (p_senderVO.getDailyTransferCount() >= ((Integer) serviceObjVal).intValue()) {
                        String strArr[] = { String.valueOf(p_senderVO.getDailyTransferCount()), String.valueOf(((Integer) serviceObjVal).intValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_DAY_MAX_TRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_DAY_MAX_TRANS_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setDailyMaxTransCountThreshold(((Integer) serviceObjVal).intValue());
                }
            }
            serviceObjVal = null;

            if (!p_senderVO.isWeeklyTotalTransCountCheckDone()) {
                serviceObjVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.WEEKLY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode());
                if (serviceObjVal != null) {
                    p_senderVO.setWeeklyTotalTransCountCheckDone(true);
                    if (p_senderVO.getWeeklyTransferCount() >= ((Integer) serviceObjVal).intValue()) {
                        String strArr[] = { String.valueOf(p_senderVO.getWeeklyTransferCount()), String.valueOf(((Integer) serviceObjVal).intValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_WEEK_MAX_TRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_WEEK_MAX_TRANS_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setWeeklyMaxTransCountThreshold(((Integer) serviceObjVal).intValue());
                }
            }
            serviceObjVal = null;
            if (!p_senderVO.isMonthlyTotalTransCountCheckDone()) {
                serviceObjVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode());
                if (serviceObjVal != null) {
                    p_senderVO.setMonthlyTotalTransCountCheckDone(true);
                    if (p_senderVO.getMonthlyTransferCount() >= ((Integer) serviceObjVal).intValue()) {
                        String strArr[] = { String.valueOf(p_senderVO.getMonthlyTransferCount()), String.valueOf(((Integer) serviceObjVal).intValue()) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MONTH_MAX_TRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MONTH_MAX_TRANS_THRESHOLD, 0, strArr, null);
                    }

                    p_senderVO.setMonthlyMaxTransCountThreshold(((Integer) serviceObjVal).intValue());
                }
            }

            serviceObjVal = null;
            if (!p_senderVO.isDailyTotalTransAmtCheckDone()) {
                serviceObjVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode());
                if (serviceObjVal != null) {
                    p_senderVO.setDailyTotalTransAmtCheckDone(true);
                    if (p_senderVO.getDailyTransferAmount() + p_requestValue > ((Long) serviceObjVal).longValue()) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getDailyTransferAmount()), PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()), PretupsBL.getDisplayAmount(p_requestValue) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_DAY_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setDailyMaxTransAmtThreshold(((Long) serviceObjVal).longValue());
                }
            }
            serviceObjVal = null;

            if (!p_senderVO.isWeeklyTotalTransAmtCheckDone()) {
                serviceObjVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.WEEKLY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode());
                if (serviceObjVal != null) {
                    p_senderVO.setWeeklyTotalTransAmtCheckDone(true);
                    if (p_senderVO.getWeeklyTransferAmount() + p_requestValue > ((Long) serviceObjVal).longValue()) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getWeeklyTransferAmount()), PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()), PretupsBL.getDisplayAmount(p_requestValue) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setWeeklyMaxTransAmtThreshold(((Long) serviceObjVal).longValue());
                }
            }
            serviceObjVal = null;
            if (!p_senderVO.isMonthlyTotalTransAmtCheckDone()) {
                serviceObjVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode());
                if (serviceObjVal != null) {
                    p_senderVO.setMonthlyTotalTransAmtCheckDone(true);
                    long comareWithValue = ((Long) serviceObjVal).longValue();
                    // Changed to handle post paid Credit Limit Validations
                    boolean usingPrefValue = true;
                    if (p_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_POSTPAID)) {
                        if (p_senderVO.getCreditLimit() < comareWithValue) {
                            comareWithValue = p_senderVO.getCreditLimit();
                            usingPrefValue = false;
                        }
                        if (_log.isDebugEnabled())
                            _log.debug("checkTransactionControls", "For msisdn=" + p_senderVO.getMsisdn() + " p_serviceClassCode=" + p_serviceClassCode + " p_requestValue=" + p_requestValue + " Using compare value=" + comareWithValue + " for Preference MON_SDR_MX_TRANS_AMT for Post Paid User");
                    }
                    Object serviceObjVal1 = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.MIN_RESIDUAL_BAL_CODE, p_senderVO.getNetworkCode());
                    boolean flag = true;
                    if (serviceObjVal1 != null) {
                        if (p_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_POSTPAID)) {
                            long comareWithValue1 = ((Long) serviceObjVal1).longValue();
                            if (comareWithValue1 < 0) {
                                flag = false;
                                if (_log.isDebugEnabled())
                                    _log.debug("checkTransactionControls", "p_senderVO.getMonthlyTransferAmount()" + p_senderVO.getMonthlyTransferAmount() + " p_requestValue:" + p_requestValue + "comareWithValue" + comareWithValue + "comareWithValue1 " + comareWithValue1);
                                if (p_senderVO.getMonthlyTransferAmount() + p_requestValue > comareWithValue - comareWithValue1) {
                                    String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), PretupsBL.getDisplayAmount(comareWithValue - comareWithValue1), PretupsBL.getDisplayAmount(p_requestValue) };
                                    if (usingPrefValue)
                                        throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                                    else
                                        throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_NEGATIVE_RESIDUAL_BAL_THRESHOLD, 0, strArr, null);
                                }

                            }
                        }
                    }
                    if (p_senderVO.getMonthlyTransferAmount() + p_requestValue > comareWithValue & flag) {
                        String strArr[] = { PretupsBL.getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), PretupsBL.getDisplayAmount(comareWithValue), PretupsBL.getDisplayAmount(p_requestValue) };
                        if (PretupsI.SERIES_TYPE_PREPAID.equals(p_senderVO.getSubscriberType()))
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                        else if (usingPrefValue)
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD, 0, strArr, null);
                        else
                            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_POST_SNDR_MONTH_MAX_AMTTRANS_CCLMT_THRESHOLD, 0, strArr, null);
                    }
                    p_senderVO.setMonthlyMaxTransAmtThreshold(((Long) serviceObjVal).longValue());
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkTransactionControls", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberBL[checkTransactionControls]", "", p_senderVO.getMsisdn(), "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("SubscriberBL", "checkTransactionControls", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }
}