package com.btsl.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;

/**
 * @(#)RestrictedSubscriberBL.java
 *                                 Copyright(c) 2005, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 This class holds the Business Logic for
 *                                 restricted MSISDNs in Pretups system.
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Ankit Singhal March 24, 2006 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 */

public class RestrictedSubscriberBL {
    private static final  Log log = LogFactory.getLog(RestrictedSubscriberBL.class.getName());
    /**
     * To genrate the operator to channel transfer id
     */
    private static OperatorUtilI calculatorI = null;
    // calculate the tax
    static {
        String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }
    
    /**
	 * to ensure no class instantiation 
	 */
    private RestrictedSubscriberBL() {
        
    }

    /**
     * Checks whether the MSISDN exists in the restricted MSISDN list of channel
     * user
     * 
     * @param p_con
     * @param _c2sTransferVO
     * @param p_channelUserVO
     * @param p_msisdn
     * @param p_amount
     * @return boolean
     * @throws BTSLBaseException
     */
    public static boolean isRestrictedMsisdnExist(Connection p_con, C2STransferVO _c2sTransferVO, ChannelUserVO p_channelUserVO, String p_msisdn, long p_amount) throws BTSLBaseException {
        final String methodName = "isRestrictedMsisdnExist";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "_c2sTransferVO " + _c2sTransferVO + " p_channelUserVO" + p_channelUserVO + " p_msisdn" + p_msisdn + " p_amount" + p_amount);
        }
        boolean isExist = false;
        RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        ChannelTransferRuleDAO channelTransferRuleDAO = null;
        ChannelTransferRuleVO channelTransferRulesVO = null;
        Date currentDate = new Date();
        Date lastTransefrOn = new Date();
        int lastTransferMonth = 0;
        int lastTransferYear = 0;
        int currentMonth = 0;
        int currentYear = 0;
        try {
            restrictedSubscriberVO = restrictedSubscriberDAO.loadRestrictedSubscriberDetails(p_con, p_channelUserVO.getOwnerID(), p_msisdn, false);
            if (restrictedSubscriberVO != null) {
                ((ReceiverVO) _c2sTransferVO.getReceiverVO()).setRestrictedSubscriberVO(restrictedSubscriberVO);
                if (PretupsI.STATUS_SUSPEND.equalsIgnoreCase(restrictedSubscriberVO.getStatus())) {
                    throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_SUSPEND, 0, null, null);
                }
                restrictedSubscriberVO.setAmount(p_amount);

                //check if subscriber is balck listed
				if (PretupsI.STATUS_ACTIVE.equals(restrictedSubscriberVO.getBlackListStatus()))
			        throw new BTSLBaseException("RestrictedSubscriberBL",methodName,PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_BLACKLISTED,0,null,null);
				//black list check ends	
                // code block to check that if month is changed then
                // transfer count and amount of restricted subscriber need to be
                // reset
                if (restrictedSubscriberVO.getLastTransferOn() != null) {
                    lastTransefrOn = restrictedSubscriberVO.getLastTransferOn();
                    if (log.isDebugEnabled()) {
                        log.debug(methodName, "lastTransefrOn " + lastTransefrOn);
                    }
                    currentYear = currentDate.getYear();
                    currentMonth = currentDate.getMonth();
                    lastTransferYear = lastTransefrOn.getYear();
                    lastTransferMonth = lastTransefrOn.getMonth();
                    if (currentYear == lastTransferYear) {
                        if (currentMonth - lastTransferMonth >= 1) {
                            restrictedSubscriberVO.setMonthlyTransferCount(0);
                            restrictedSubscriberVO.setMonthlyTransferAmount(0);
                        }
                    } else {
                        restrictedSubscriberVO.setMonthlyTransferCount(0);
                        restrictedSubscriberVO.setMonthlyTransferAmount(0);
                    }
                } else {
                    restrictedSubscriberVO.setMonthlyTransferCount(0);
                    restrictedSubscriberVO.setMonthlyTransferAmount(0);
                }
                restrictedSubscriberVO.setLastTransferOn(_c2sTransferVO.getTransferDate());

                if (PretupsI.STATUS_ACTIVE.equalsIgnoreCase(restrictedSubscriberVO.getStatus())) {
                    if (p_channelUserVO.getUserID().equals(restrictedSubscriberVO.getChannelUserID())) {
                        isExist = true;
                    } else {
                        channelTransferRuleDAO = new ChannelTransferRuleDAO();
                        channelTransferRulesVO = channelTransferRuleDAO.loadTransferRule(p_con, _c2sTransferVO.getNetworkCode(), _c2sTransferVO.getDomainCode(), p_channelUserVO.getCategoryCode(), restrictedSubscriberVO.getCategory(), PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
                        if (channelTransferRulesVO != null && (PretupsI.NO.equals(channelTransferRulesVO.getRestrictedMsisdnAccess()))) {
                            throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.C2S_ERROR_TRANSFER_RULE_NOTEXIST, 0, null, null);
                        }
                        isExist = true;
                    }
                }
            } else {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_DOESNOTEXIST, 0, null, null);
            }
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException : " + be);
            log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception : " + e);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberBL[isRestrictedMsisdnExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "returning isExist=" + isExist);
            }
        }
        return isExist;
    }

    /**
     * This method checks the receiver's limits in case of restricted MSISDN
     * 
     * @param p_transferVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public static boolean validateRestrictedSubscriberLimits(TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "validateRestrictedSubscriberLimits";
        if (log.isDebugEnabled()) {
            log.debug(methodName, p_transferVO);
        }
        boolean isValidated = false;
        try {
            long requestedAmount = p_transferVO.getRequestedAmount();
            RestrictedSubscriberVO restrictedSubscriberVO = (RestrictedSubscriberVO) ((ReceiverVO) p_transferVO.getReceiverVO()).getRestrictedSubscriberVO();
            if (restrictedSubscriberVO == null) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION, 0, null, null);
            }

            if (requestedAmount < restrictedSubscriberVO.getMinTxnAmount()) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_AMOUNT_LESSTHANMINIMUM, 0, null, null);
            }
            if (requestedAmount > restrictedSubscriberVO.getMaxTxnAmount()) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_AMOUNT_MORETHANMAXIMUM, 0, null, null);
            }
            if (requestedAmount + restrictedSubscriberVO.getMonthlyTransferAmount() > restrictedSubscriberVO.getMonthlyLimit()) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_AMOUNT_MONTHLYLIMIT_CROSSED, 0, null, null);
            }
            isValidated = true;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberBL[validateRestrictedSubscriberLimits]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "returning isValidated " + isValidated);
            }
        }

        return isValidated;
    }

    /**
     * Method generateScheduleBatchID.
     * This method generate the batchID for the corporate module for the
     * scheduleing purpose
     * Format of it is described in the comman file where all the IDs are
     * formatting.
     * 
     * @param p_scheduleMasterVO
     *            ScheduleBatchMasterVO
     * @throws BTSLBaseException
     */
    public static void generateScheduleBatchID(ScheduleBatchMasterVO p_scheduleMasterVO) throws BTSLBaseException {

        final String methodName = "generateScheduleBatchID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_scheduleMasterVO =" + p_scheduleMasterVO);
        }
        try {
            p_scheduleMasterVO.setBatchID(calculatorI.formatScheduleBatchID(p_scheduleMasterVO, PretupsI.SCHEDULE_BATCH_ID, IDGenerator.getNextID(PretupsI.SCHEDULE_BATCH_ID, BTSLUtil.getFinancialYear(), p_scheduleMasterVO.getNetworkCode(), p_scheduleMasterVO.getCreatedOn()), p_scheduleMasterVO.getNetworkCode()));
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited  ID =" + p_scheduleMasterVO.getBatchID());
            }
        }

    }

    /**
     * Checks whether the MSISDN exists in the restricted MSISDN list of channel
     * user
     * 
     * @param p_con
     * @param p_msisdn
     * @return boolean
     * @throws BTSLBaseException
     */
    public static boolean isSubscriberBlacklisted(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "isSubscriberBlacklisted";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "p_msisdn " + p_msisdn);
        }
        boolean subscriberBlacklisted = false;
        RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
        try {
            subscriberBlacklisted = restrictedSubscriberDAO.isSubscriberBlacklisted(p_con, p_msisdn);
            if (subscriberBlacklisted) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_BLACKLISTED, 0, null, null);
            }
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException : " + be);
            log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception : " + e);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberBL[isSubscriberBlacklisted]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RestrictedSubscriberBL", "isRestrictedMsisdnExist", PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "returning subscriberBlacklisted=" + subscriberBlacklisted);
            }
        }
        return subscriberBlacklisted;
    }

    /**
     * This method checks the receiver's limits in case of restricted MSISDN
     * 
     * @param p_transferVO
     * @param p_quantityRequired
     * @return boolean
     * @throws BTSLBaseException
     */
    public static boolean validateRestrictedSubscriberLimits(TransferVO p_transferVO, int p_quantityRequired) throws BTSLBaseException {
        final String methodName = "validateRestrictedSubscriberLimits";
        if (log.isDebugEnabled()) {
            log.debug(methodName, p_transferVO + " p_quantityRequired=" + p_quantityRequired);
        }
        boolean isValidated = false;
        try {
            long requestedAmount = p_transferVO.getRequestedAmount() * p_quantityRequired;
            RestrictedSubscriberVO restrictedSubscriberVO = (RestrictedSubscriberVO) ((ReceiverVO) p_transferVO.getReceiverVO()).getRestrictedSubscriberVO();
            if (restrictedSubscriberVO == null) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION, 0, null, null);
            }

            if (requestedAmount < restrictedSubscriberVO.getMinTxnAmount()) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_AMOUNT_LESSTHANMINIMUM, 0, null, null);
            }
            if (requestedAmount > restrictedSubscriberVO.getMaxTxnAmount()) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_AMOUNT_MORETHANMAXIMUM, 0, null, null);
            }
            if (requestedAmount + restrictedSubscriberVO.getMonthlyTransferAmount() > restrictedSubscriberVO.getMonthlyLimit()) {
                throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_AMOUNT_MONTHLYLIMIT_CROSSED, 0, null, null);
            }
            isValidated = true;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberBL[validateRestrictedSubscriberLimits]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "returning isValidated " + isValidated);
            }
        }
        return isValidated;
    }

    /**
     * Checks whether the MSISDN exists in the restricted MSISDN list of channel
     * user
     * 
     * @param p_con
     * @param _c2sTransferVO
     * @param p_channelUserVO
     * @param p_msisdn
     * @param p_amount
     * @return boolean
     * @throws BTSLBaseException
     */
    public static boolean isRestrictedMsisdnExistForC2S(Connection p_con, C2STransferVO _c2sTransferVO, ChannelUserVO p_channelUserVO, String p_msisdn, long p_amount) throws BTSLBaseException {
        final String methodName = "isRestrictedMsisdnExistForC2S";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "_c2sTransferVO " + _c2sTransferVO + " p_channelUserVO" + p_channelUserVO + " p_msisdn" + p_msisdn + " p_amount" + p_amount);
        }
        boolean isExist = false;
        RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        ChannelTransferRuleDAO channelTransferRuleDAO = null;
        ChannelTransferRuleVO channelTransferRulesVO = null;
        Date currentDate = new Date();
        Date lastTransefrOn = new Date();
        int lastTransferMonth = 0;
        int lastTransferYear = 0;
        int currentMonth = 0;
        int currentYear = 0;
        try {
            restrictedSubscriberVO = restrictedSubscriberDAO.loadRestrictedSubscriberDetailsForC2S(p_con, p_channelUserVO.getOwnerID(), p_msisdn, false);
            // Check whether the receiver is in the restricted list or not.
            if (restrictedSubscriberVO != null) {

                ((ReceiverVO) _c2sTransferVO.getReceiverVO()).setRestrictedSubscriberVO(restrictedSubscriberVO);
                if (PretupsI.STATUS_SUSPEND.equalsIgnoreCase(restrictedSubscriberVO.getStatus())) {
                    throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_SUSPEND, 0, null, null);
                }
                restrictedSubscriberVO.setAmount(p_amount);
                
                if (PretupsI.STATUS_ACTIVE.equals(restrictedSubscriberVO.getBlackListStatus()))
			        throw new BTSLBaseException("RestrictedSubscriberBL",methodName,PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_BLACKLISTED,0,null,null);

                // code block to check that if month is changed then
                // transfer count and amount of restricted subscriber need to be
                // reset
                if (restrictedSubscriberVO.getLastTransferOn() != null) {
                    lastTransefrOn = restrictedSubscriberVO.getLastTransferOn();
                    if (log.isDebugEnabled()) {
                        log.debug(methodName, "lastTransefrOn " + lastTransefrOn);
                    }
                    currentYear = currentDate.getYear();
                    currentMonth = currentDate.getMonth();
                    lastTransferYear = lastTransefrOn.getYear();
                    lastTransferMonth = lastTransefrOn.getMonth();
                    if (currentYear == lastTransferYear) {
                        if (currentMonth - lastTransferMonth >= 1) {
                            restrictedSubscriberVO.setMonthlyTransferCount(0);
                            restrictedSubscriberVO.setMonthlyTransferAmount(0);
                        }
                    } else {
                        restrictedSubscriberVO.setMonthlyTransferCount(0);
                        restrictedSubscriberVO.setMonthlyTransferAmount(0);
                    }
                } else {
                    restrictedSubscriberVO.setMonthlyTransferCount(0);
                    restrictedSubscriberVO.setMonthlyTransferAmount(0);
                }
                restrictedSubscriberVO.setLastTransferOn(_c2sTransferVO.getTransferDate());

                if (PretupsI.STATUS_ACTIVE.equals((p_channelUserVO.getCategoryVO()).getRestrictedMsisdns())) {
                    if (PretupsI.STATUS_ACTIVE.equals((p_channelUserVO.getCategoryVO()).getTransferToListOnly())) {
                        if (!p_channelUserVO.getOwnerID().equals(restrictedSubscriberVO.getOwnerID())) {
                            throw new BTSLBaseException("RestrictedSubscriberBL", "isRestrictedMsisdnExist", PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_RECHARGE_NOT_ALLOWED, 0, null, null);
                        }
                    }
                }
                if (PretupsI.YES.equals(restrictedSubscriberVO.getRechargeThroughParent())) { // If
                                                                                              // Sender
                                                                                              // and
                                                                                              // receiver
                                                                                              // are
                                                                                              // same
                                                                                              // Recharge
                                                                                              // the
                                                                                              // receiver.
                    if (p_channelUserVO.getUserID().equals(restrictedSubscriberVO.getChannelUserID())) {
                        if (log.isDebugEnabled()) {
                            log.debug("isRestrictedMsisdnExistForC2S1", "If Sender and receiver are same Recharge the receiver");
                        }
                        isExist = true;
                    }
                    // If Sender and Receiver belong to same domain but belong
                    // to different category, then check the transfer rule
                    // between sender and receiver category.
                    else if (p_channelUserVO.getDomainID().equals(restrictedSubscriberVO.getSubscriberDomainCode()) && (!p_channelUserVO.getCategoryCode().equals(restrictedSubscriberVO.getChannelUserCategory()))) {
                        if (log.isDebugEnabled()) {
                            log.debug("isRestrictedMsisdnExistForC2S2", "If Sender and Receiver belong to same domain but belong to different category, then check the transfer rule between sender and receiver category.");
                        }
                        channelTransferRuleDAO = new ChannelTransferRuleDAO();
                        channelTransferRulesVO = channelTransferRuleDAO.loadTransferRule(p_con, _c2sTransferVO.getNetworkCode(), p_channelUserVO.getDomainID(), p_channelUserVO.getCategoryCode(), restrictedSubscriberVO.getChannelUserCategory(), PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
                        if ((channelTransferRulesVO != null && (PretupsI.YES.equals(channelTransferRulesVO.getRestrictedRechargeAccess())))) {
                            isExist = true;
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug(methodName, "channelTransferRulesVO= " + channelTransferRulesVO);
                            }
                            throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.C2S_ERROR_TRANSFER_RULE_NOTEXIST, 0, null, null);
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "FINAL ELSE= ");
                        }
                        throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_RECHARGE_NOT_ALLOWED, 0, null, null);
                    }
                }
            } else {
                if (PretupsI.STATUS_ACTIVE.equals((p_channelUserVO.getCategoryVO()).getRestrictedMsisdns())) {
                    if (PretupsI.STATUS_ACTIVE.equals((p_channelUserVO.getCategoryVO()).getTransferToListOnly())) {
                        throw new BTSLBaseException("RestrictedSubscriberBL", "isRestrictedMsisdnExist", PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_RECHARGE_NOT_ALLOWED, 0, null, null);
                    }
                }
            }
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException : " + be);
            log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception : " + e);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberBL[isRestrictedMsisdnExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RestrictedSubscriberBL", "isRestrictedMsisdnExist", PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "returning isExist=" + isExist);
            }
        }
        return isExist;
    }

    /**
     * Checks whether the Channel user exist's or not
     * 
     * @param p_con
     * @param p_msisdn
     * @return boolean
     * @throws BTSLBaseException
     */
    public static boolean isChannelUserExistForC2SViaSms(Connection p_con, String p_msisdn, String s_msisdn) throws BTSLBaseException {
        final String methodName = "isChannelUserExistForC2SViaSms";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " p_msisdn" + p_msisdn);
        }
        boolean isExist = false;
        RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();

        try {
            isExist = restrictedSubscriberDAO.loadChannelUserDetailsForC2SViaSms(p_con, p_msisdn, s_msisdn);
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException : " + be);
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception : " + e);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberBL[isChannelUserExistForC2SViaSms]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RestrictedSubscriberBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "returning isExist=" + isExist);
            }
        }
        return isExist;
    }
}