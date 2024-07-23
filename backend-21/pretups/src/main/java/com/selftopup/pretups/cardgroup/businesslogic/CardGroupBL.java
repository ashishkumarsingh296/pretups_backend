package com.selftopup.pretups.cardgroup.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.selftopup.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.selftopup.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.transfer.businesslogic.TransferItemVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;

import org.apache.commons.beanutils.BeanUtils;

/*
 * CardGroupBL.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 28/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Card Group Business Logic class to calculate the sender and reciever amounts
 * and taxes
 */

public class CardGroupBL {

    private static Log _log = LogFactory.getLog(CardGroupBL.class.getName());
    public static CardGroupDAO _cardGroupDAO = new CardGroupDAO();
    public static OperatorUtilI calculatorI = null;
    // calculate the tax
    static {
        String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public static CardGroupDetailsVO loadCardGroupDetails(Connection p_con, String p_cardGroupSetID, long p_requestAmount, java.util.Date p_applicableDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupDetails", "Entered p_cardGroupSetID=" + p_cardGroupSetID + " p_requestAmount=" + p_requestAmount + " p_applicableDate=" + p_applicableDate);
        CardGroupDetailsVO cardGroupDetailsVO = null;
        try {
            cardGroupDetailsVO = CardGroupCache.getCardGroupDetails(p_cardGroupSetID, p_requestAmount, p_applicableDate);
            // cardGroupDetailsVO=CardGroupCache.getCardGroupDetails(p_cardGroupSetID,p_requestAmount,p_applicableDate);
        } catch (BTSLBaseException bex) {
            _log.error("loadCardGroupDetails", "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("loadCardGroupDetails: Exception print stack trace:", e);
            _log.error("loadCardGroupDetails", "Exception " + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupBL[loadCardGroupDetails]","","","","Exception while get the loading the card group details:"+e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "loadCardGroupDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupDetails", "Exiting");
        return cardGroupDetailsVO;
    }

    /**
     * Method to load the card group details and calculate the users access fee,
     * talk time etc
     * 
     * @param p_transferVO
     * @param p_cardGroupDetailsVO
     * @param p_checkMultipleOf
     * @throws BTSLBaseException
     */
    public static void calculateCardGroupDetails(Connection p_con, TransferVO p_transferVO, String p_module, boolean p_checkMultipleOf) throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("calculateCardGroupDetails","Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID="+p_transferVO.getRequestID()+" Sub Service="+p_transferVO.getSubService()+"p_checkMultipleOf="+p_checkMultipleOf);
        if (_log.isDebugEnabled())
            _log.debug("calculateCardGroupDetails", "Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID=" + p_transferVO.getRequestID() + " Sub Service=" + p_transferVO.getSubService() + "p_checkMultipleOf=" + p_checkMultipleOf + "p_transferVO::" + p_transferVO);
        try {
            CardGroupDetailsVO cardGroupDetailsVO = loadCardGroupDetails(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getRequestedAmount(), p_transferVO.getTransferDateTime());
            p_transferVO.setVersion(cardGroupDetailsVO.getVersion());
            p_transferVO.setCardGroupID(cardGroupDetailsVO.getCardGroupID());
            p_transferVO.setCardGroupCode(cardGroupDetailsVO.getCardGroupCode());
            p_transferVO.setMinCardGroupAmount(cardGroupDetailsVO.getStartRange());

            // added for card group suspend/resume
            p_transferVO.setStatus(cardGroupDetailsVO.getStatus());

            p_transferVO.setReceiverCreditBonusValidity(cardGroupDetailsVO.getBonusValidityValue());
            p_transferVO.setBoth(cardGroupDetailsVO.getBoth());
            p_transferVO.setOnline(cardGroupDetailsVO.getOnline());

            // added for card group slab suspend/resume
            if (cardGroupDetailsVO.getStatus().equals(PretupsI.SUSPEND))
                throw new BTSLBaseException("CardGroupBL", "calculateCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_SLAB_SUSPENDED);

            // 100 because all requets should go through if multiple of is 1.
            if (p_checkMultipleOf && cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % cardGroupDetailsVO.getMultipleOf() != 0)
                    throw new BTSLBaseException("CardGroupBL", "calculateCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()) }, null);
            }

            // Set sender and receiver's conversion factor in transfer vo by
            // Vinay on 01-July-09.
            p_transferVO.setSenderConvFactor(Double.parseDouble(cardGroupDetailsVO.getSenderConvFactor()));
            p_transferVO.setReceiverConvFactor(Double.parseDouble(cardGroupDetailsVO.getReceiverConvFactor()));

            // Set the bonus account details into the transfer VO by Vinay on
            // 01-July-09.
            // setBonusAccountDetails(cardGroupDetailsVO,p_transferVO);

            if (p_module.equalsIgnoreCase(PretupsI.P2P_MODULE)) {
                calculateP2PSenderValues((P2PTransferVO) p_transferVO, cardGroupDetailsVO, p_transferVO.getSubService(), p_checkMultipleOf);
                calculateP2PReceiverValues((P2PTransferVO) p_transferVO, cardGroupDetailsVO, p_transferVO.getSubService(), p_checkMultipleOf);
            }

            // added by gaurav for COS change
            // if(SystemPreferences.COS_REQUIRED )

        } catch (BTSLBaseException bex) {
            _log.error("calculateCardGroupDetails", "BTSLException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("calculateCardGroupDetails: Exception print stack trace:", e);
            _log.error("calculateCardGroupDetails", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateCardGroupDetails]", p_transferVO.getTransferID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateCardGroupDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("calculateCardGroupDetails", "Exiting");
    }

    /**
     * Method to calculate the sender related values
     * 
     * @param p_transferItemsVO
     * @param p_cardGroupDetailsVO
     * @param p_checkMultipleOf
     * @throws BTSLBaseException
     */
    public static void calculateP2PSenderValues(P2PTransferVO p_transferVO, CardGroupDetailsVO p_cardGroupDetailsVO, String p_subService, boolean p_checkMultipleOf) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("calculateP2PSenderValues", "Entered with p_transferVO and p_cardGroupDetailsVO with p_subService=" + p_subService + "p_checkMultipleOf=" + p_checkMultipleOf);

        TransferItemVO transferItemVO = null;
        // access fee, taxes
        try {
            // 100 because all requets should go through if multiple of is 1.
            if (p_checkMultipleOf && p_cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && p_cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % p_cardGroupDetailsVO.getMultipleOf() != 0)
                    throw new BTSLBaseException("CardGroupBL", "calculateP2PSenderValues", SelfTopUpErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(p_cardGroupDetailsVO.getMultipleOf()) }, null);
            }

            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(0);

            long requestedValue = p_transferVO.getRequestedAmount();
            double tax1Rate = p_cardGroupDetailsVO.getSenderTax1Rate();
            double tax2Rate = p_cardGroupDetailsVO.getSenderTax2Rate();
            double accessFeeRate = p_cardGroupDetailsVO.getSenderAccessFeeRate();
            long calculatedAccessFee = 0;
            long calculatedTax1Value = 0;
            long calculatedTax2Value = 0;
            long transferValue = 0;

            if (_log.isDebugEnabled())
                _log.debug("calculateP2PSenderValues", "Before Setting RequestedValue=" + requestedValue + " Access fee Type=" + p_cardGroupDetailsVO.getSenderAccessFeeType() + " Access fee Rate=" + accessFeeRate + " senderTax1 Type=" + p_cardGroupDetailsVO.getSenderTax1Type() + " Sender Tax 1 Rate=" + tax1Rate + " senderTax2 Type=" + p_cardGroupDetailsVO.getSenderTax2Type() + " Sender Tax 2 Rate=" + tax2Rate);

            calculatedAccessFee = calculatorI.calculateAccessFee(accessFeeRate, p_cardGroupDetailsVO.getSenderAccessFeeType(), requestedValue, p_cardGroupDetailsVO.getMinSenderAccessFee(), p_cardGroupDetailsVO.getMaxSenderAccessFee());

            p_transferVO.setSenderAccessFee(calculatedAccessFee);

            calculatedTax1Value = calculatorI.calculateCardGroupTax1(p_cardGroupDetailsVO.getSenderTax1Type(), tax1Rate, requestedValue);
            p_transferVO.setSenderTax1Type(p_cardGroupDetailsVO.getSenderTax1Type());
            p_transferVO.setSenderTax1Rate(tax1Rate);
            p_transferVO.setSenderTax1Value(calculatedTax1Value);

            if (p_cardGroupDetailsVO.getSenderTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT))
                calculatedTax2Value = (long) tax2Rate;
            else // If percentage
            {
                if (SystemPreferences.IS_TAX2_ON_TAX1)
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getSenderTax2Type(), tax2Rate, calculatedTax1Value);
                else
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getSenderTax2Type(), tax2Rate, requestedValue);
            }
            p_transferVO.setSenderTax2Type(p_cardGroupDetailsVO.getSenderTax2Type());
            p_transferVO.setSenderTax2Rate(tax2Rate);
            p_transferVO.setSenderTax2Value(calculatedTax2Value);

            transferValue = calculatorI.calculateSenderTransferValue(requestedValue, calculatedTax1Value, calculatedTax2Value, calculatedAccessFee);
            // Divide the transfer amount by conversion factor.
            double senderConvFactor = p_transferVO.getSenderConvFactor();
            if (senderConvFactor != 0)
                transferValue = (long) ((double) transferValue / senderConvFactor);

            p_transferVO.setSenderTransferValue(transferValue);
            transferItemVO.setTransferValue(transferValue);
            if (_log.isDebugEnabled())
                _log.debug("calculateP2PSenderValues", "After Setting transferValue=" + transferValue + " calculatedAccessFee=" + calculatedAccessFee + " Access fee Type=" + p_cardGroupDetailsVO.getSenderAccessFeeType() + " calculatedTax1Value=" + calculatedTax1Value + " calculatedTax2Value=" + calculatedTax2Value);

        } catch (BTSLBaseException bex) {
            _log.error("calculateCardGroupDetails", "BTSLException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateP2PSenderValues]", transferItemVO.getTransferID(), transferItemVO.getMsisdn(), "", "Not able to calculate the sender talk values getting Exception=" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateP2PSenderValues", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("calculateP2PSenderValues", "Exiting");

    }

    /**
     * Method to calculate the reciever related values
     * 
     * @param p_transferItemsVO
     * @param p_cardGroupDetailsVO
     * @param p_checkMultipleOf
     * @throws BTSLBaseException
     */
    public static void calculateP2PReceiverValues(P2PTransferVO p_transferVO, CardGroupDetailsVO p_cardGroupDetailsVO, String p_subService, boolean p_checkMultipleOf) throws BTSLBaseException {
        // validity and grace period,taxes,bonus talk value,bonus
        // validity,access fee
        if (_log.isDebugEnabled())
            _log.debug("calculateP2PReceiverValues", "Entered with p_subService" + p_subService + " p_checkMultipleOf=" + p_checkMultipleOf);

        TransferItemVO transferItemVO = null;
        try {
            // 100 because all requets should go through if multiple of is 1.
            if (p_checkMultipleOf && p_cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && p_cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % p_cardGroupDetailsVO.getMultipleOf() != 0)
                    throw new BTSLBaseException("CardGroupBL", "calculateP2PReceiverValues", SelfTopUpErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(p_cardGroupDetailsVO.getMultipleOf()) }, null);
            }

            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
            // Bonus Talk time on Requested Value
            setBonusAccountDetails(p_cardGroupDetailsVO, p_transferVO);
            long requestedValue = p_transferVO.getRequestedAmount();
            double bonusTalkTimeRate = p_cardGroupDetailsVO.getBonusTalkTimeRate();
            double accessFeeRate = p_cardGroupDetailsVO.getReceiverAccessFeeRate();
            double tax1Rate = p_cardGroupDetailsVO.getReceiverTax1Rate();
            double tax2Rate = p_cardGroupDetailsVO.getReceiverTax2Rate();
            long calculatedAccessFee = 0;
            long calculatedBonusTalkTimeValue = 0;
            long calculatedTax1Value = 0;
            long calculatedTax2Value = 0;
            long transferValue = 0;
            long creditbonusvalidityP2p = p_cardGroupDetailsVO.getBonusValidityValue();
            String onLine = p_cardGroupDetailsVO.getOnline();
            String both = p_cardGroupDetailsVO.getBoth();

            // _operatorUtil.checkRechargeInGraceAllowed(p_transferVO.getReceiverMsisdn(),p_cardGroupDetailsVO.getGracePeriod(),transferItemVO.getPreviousExpiry(),transferItemVO.getTransferDate());

            calculatedAccessFee = calculatorI.calculateAccessFee(accessFeeRate, p_cardGroupDetailsVO.getReceiverAccessFeeType(), requestedValue, p_cardGroupDetailsVO.getMinReceiverAccessFee(), p_cardGroupDetailsVO.getMaxReceiverAccessFee());

            p_transferVO.setReceiverAccessFee(calculatedAccessFee);

            // Bonus Talk time on Requested Value
            calculatedBonusTalkTimeValue = calculatorI.calculateCardGroupBonus(p_cardGroupDetailsVO.getBonusTalkTimeType(), bonusTalkTimeRate, requestedValue);

            calculatedTax1Value = calculatorI.calculateCardGroupTax1(p_cardGroupDetailsVO.getReceiverTax1Type(), tax1Rate, requestedValue);
            p_transferVO.setReceiverTax1Type(p_cardGroupDetailsVO.getReceiverTax1Type());
            p_transferVO.setReceiverTax1Rate(tax1Rate);
            p_transferVO.setReceiverTax1Value(calculatedTax1Value);

            if (p_cardGroupDetailsVO.getReceiverTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT))
                calculatedTax2Value = (long) tax2Rate;
            else // If percentage
            {
                if (SystemPreferences.IS_TAX2_ON_TAX1)
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getReceiverTax2Type(), tax2Rate, calculatedTax1Value);
                else
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getReceiverTax2Type(), tax2Rate, requestedValue);
            }
            p_transferVO.setReceiverTax2Type(p_cardGroupDetailsVO.getReceiverTax2Type());
            p_transferVO.setReceiverTax2Rate(tax2Rate);
            p_transferVO.setReceiverTax2Value(calculatedTax2Value);

            p_transferVO.setReceiverValPeriodType(p_cardGroupDetailsVO.getValidityPeriodType());
            // Divide the requested amount by receiver's conversion factor.
            double receiverConvFactor = p_transferVO.getReceiverConvFactor();
            if (receiverConvFactor != 0)
                requestedValue = (long) ((double) requestedValue / receiverConvFactor);
            transferValue = calculatorI.calculateReceiverTransferValue(requestedValue, calculatedAccessFee, calculatedTax1Value, calculatedTax2Value, calculatedBonusTalkTimeValue);

            p_cardGroupDetailsVO.setBonusTalkTimeValue(calculatedBonusTalkTimeValue);
            p_cardGroupDetailsVO.setTransferValue(transferValue);

            // set the values in transfer VO. According to operator's transfer
            // tax calculator
            calculatorI.setCalculatedCardGroupValues(p_subService, p_cardGroupDetailsVO, p_transferVO);

            p_transferVO.setCardGroupID(p_cardGroupDetailsVO.getCardGroupID());
            p_transferVO.setReceiverCreditBonusValidity(creditbonusvalidityP2p);
            p_transferVO.setOnline(onLine);
            p_transferVO.setBoth(both);
            if (_log.isDebugEnabled())
                _log.debug("calculateP2PReceiverValues", "Values Set Access fee=" + calculatedAccessFee + " BonusTalkTimeValue=" + calculatedBonusTalkTimeValue + " Tax1Value=" + calculatedTax1Value + " Tax2Value=" + calculatedTax2Value + " validityPeriodValue=" + p_cardGroupDetailsVO.getValidityPeriod());
        } catch (BTSLBaseException bex) {
            _log.error("calculateP2PReceiverValues", "BTSLException " + bex.getMessage());
            _log.errorTrace("calculateP2PReceiverValues: Exception print stack trace:", bex);
            throw bex;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateP2PReceiverValues]", transferItemVO.getTransferID(), transferItemVO.getMsisdn(), " ", "Not able to calculate the receiver talk values getting Exception=" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateP2PReceiverValues", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("calculateP2PReceiverValues", "Exiting");
    }

    /**
     * To calculate C2S Transfer value for sender
     * 
     * @param p_transferVO
     * @param p_cardGroupDetailsVO
     * @param p_subService
     * @param p_checkMultipleOf
     * @throws BTSLBaseException
     */

    /**
     * Method to get the card group details and calculate the users access fee,
     * talk time etc
     * Date:19/12/2007
     * 
     * @param p_con
     * @param p_transferVO
     * @return TODO
     * @throws BTSLBaseException
     */
    public static ArrayList getCardGroupDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getCardGroupDetails", "Entered with p_transferVO:" + p_transferVO.getCardGroupSetID());
        ArrayList cardGroupDetailsVOList = null;
        try {
            CardGroupDAO cardGroupDAO = new CardGroupDAO();
            cardGroupDetailsVOList = cardGroupDAO.loadCardGroupSlab(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getTransferDateTime());
        } catch (BTSLBaseException bex) {
            _log.error("getCardGroupDetails", "BTSLException " + bex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[getCardGroupDetails]", p_transferVO.getCardGroupSetID(), "", "", "Exception while calculating talk time for users:" + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("getCardGroupDetails: Exception print stack trace:", e);
            _log.error("getCardGroupDetails", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[getCardGroupDetails]", p_transferVO.getCardGroupSetID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "getCardGroupDetails", SelfTopUpErrorCodesI.NO_SLAB_FOR_CARD_GROUP_SETID);
        }
        if (_log.isDebugEnabled())
            _log.debug("getCardGroupDetails", "Exiting ");
        return cardGroupDetailsVOList;
    }

    /**
     * Method to get the card group details and calculate the users access fee,
     * talk time etc
     * Date:19/12/2007
     * 
     * @param p_con
     * @param p_transferVO
     * 
     * @throws BTSLBaseException
     */
    public static void getCardGroupDetails(Connection p_con, TransferVO p_transferVO, ArrayList p_cardGroupDetailsVOList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getCardGroupDetails", "Entered with p_transferVO:" + p_transferVO.getCardGroupSetID());
        try {
            CardGroupDAO cardGroupDAO = new CardGroupDAO();
            ArrayList cardGroupDetailsVOList = null;
            CardGroupDetailsVO cardGroupDetailsVO = null;
            cardGroupDetailsVOList = cardGroupDAO.loadCardGroupSlab(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getTransferDateTime());
            Iterator itr = cardGroupDetailsVOList.iterator();
            while (itr.hasNext()) {
                cardGroupDetailsVO = new CardGroupDetailsVO();
                BeanUtils.copyProperties(cardGroupDetailsVO, (CardGroupDetailsVO) itr.next());
                p_cardGroupDetailsVOList.add(cardGroupDetailsVO);

            }

        } catch (BTSLBaseException bex) {
            _log.error("getCardGroupDetails", "BTSLException " + bex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[getCardGroupDetails]", p_transferVO.getCardGroupSetID(), "", "", "Exception while calculating talk time for users:" + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("getCardGroupDetails: Exception print stack trace:", e);
            _log.error("getCardGroupDetails", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[getCardGroupDetails]", p_transferVO.getCardGroupSetID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "getCardGroupDetails", SelfTopUpErrorCodesI.NO_SLAB_FOR_CARD_GROUP_SETID);
        }
        if (_log.isDebugEnabled())
            _log.debug("getCardGroupDetails", "Exiting ");
    }

    /**
     * Method to get the card group bonus details.
     * 
     * @param CardGroupDetailsVO
     *            p_cardGroupVO
     * @param TransferVO
     *            p_transferVO
     * @throws BTSLBaseException
     */
    public static void setBonusAccountDetails(CardGroupDetailsVO p_cardGroupVO, TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("setBonusAccountDetails", "Entered");
        ArrayList list = p_cardGroupVO.getBonusAccList();
        BonusAccountDetailsVO bundleVO = null;
        BonusAccountDetailsVO selectorAccBundleVO = null;
        // ServiceSelectorMappingCache serviceMappingCache=null;
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        String serviceSelectorKey = null;
        String receiverBonusID = null;
        String bundleTypes = "";
        String bonusIds = "";
        String bonusValuesStr = "";
        String bonusValidityStr = "";
        double bonusValueDouble;
        double bonusValidityDouble;
        double bonusConvFactor;
        double bonusTalkTimeAfterConversion;
        // String bundleAllowedOnIN=null;
        String bonusValues = null;
        String bonusValidity = null;
        ArrayList bonusBundleList = null;
        BonusBundleDetailVO bonusBundleDetlVO = null;
        // String is set here intentionaly, to avoid null setting
        String bonusNames = "";
        String bonusCodes = "";
        String bonusRates = "";
        try {
            serviceSelectorKey = p_transferVO.getServiceType() + "_" + p_transferVO.getSubService();
            if (isNull(p_transferVO.getServiceType()) && isNull(p_transferVO.getSubService()))
                serviceSelectorKey = p_cardGroupVO.getServiceTypeSelector();
            /*
             * else if(isNull(p_transferVO.getSelectorCode()))
             * serviceSelectorKey=p_transferVO.getServiceType()+"_"+p_transferVO.
             * getSubService();
             */
            if (_log.isDebugEnabled())
                _log.debug("setBonusAccountDetails", "serviceSelectorKey=" + serviceSelectorKey);
            serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(serviceSelectorKey);
            receiverBonusID = serviceSelectorMappingVO.getReceiverBundleID();
            if (_log.isDebugEnabled())
                _log.debug("setBonusAccountDetails", "receiverBonusID=" + receiverBonusID);
            BonusBundleCache bonusBundleCache = null;
            bonusBundleList = (ArrayList) bonusBundleCache.getBonusBundleList();
            if (_log.isDebugEnabled())
                _log.debug("setBonusAccountDetails", "bonusBundleList=" + bonusBundleList);
            for (int i = 0; i < list.size(); i++) {
                bundleVO = (BonusAccountDetailsVO) list.get(i);
                if (!BTSLUtil.isNullString(bundleVO.getBundleID())) {
                    for (int j = 0; j < bonusBundleList.size(); j++) {
                        bonusBundleDetlVO = (BonusBundleDetailVO) bonusBundleList.get(j);
                        if (bundleVO.getBundleID().equals(bonusBundleDetlVO.getBundleID())) {
                            bundleVO.setRestrictedOnIN(bonusBundleDetlVO.getResINStatus());
                            break;
                        }
                    }
                    if (bundleVO != null && bundleVO.getRestrictedOnIN().equals("Y")) {
                        // If receiver bonus id and bous id is same, then set
                        // its value in to the selectorBundleVO.
                        if (receiverBonusID.equals(bundleVO.getBundleID()))
                            selectorAccBundleVO = bundleVO;
                        // else place all the values in pipe separated.
                        else {
                            bonusConvFactor = Double.parseDouble(bundleVO.getMultFactor());
                            bonusValueDouble = Double.parseDouble(bundleVO.getBonusValue());
                            bonusTalkTimeAfterConversion = bonusValueDouble / bonusConvFactor;
                            long requestedAmt = p_transferVO.getRequestedAmount();
                            bonusValueDouble = calculatorI.calculateCardGroupBonus(bundleVO.getType(), bonusTalkTimeAfterConversion, requestedAmt);
                            // bonusValues=(PretupsBL.getDisplayAmount((long)bonusValueDouble));
                            if (PretupsI.SYSTEM_AMOUNT.equals(bundleVO.getType()))
                                bonusValues = String.valueOf((PretupsBL.getSystemAmount(bonusValueDouble)));
                            else
                                bonusValues = String.valueOf(bonusValueDouble);

                            // get the bonus validity from the
                            // BonusAccountDetailsVO
                            bonusValidity = bundleVO.getBonusValidity();
                            // Get the Restricted bundle on IN
                            // bundleAllowedOnIN=bundleVO.getRestrictedOnIN();

                            bonusValueDouble = Double.parseDouble(bonusValues);
                            bonusValidityDouble = Double.parseDouble(bonusValidity);
                            // Set only those bonus bundles in the VO, if bundle
                            // is not restriced on IN or it value and validity
                            // is not zero.
                            // if(bundleAllowedOnIN.equals("Y") &&
                            // (bonusValueDouble!=0 || bonusValidityDouble!=0))
                            if (bonusValueDouble != 0 || bonusValidityDouble != 0) {
                                // get the bonus id from the
                                // BonusAccountDetailsVO
                                bonusIds += bundleVO.getBundleID() + "|";
                                bonusNames += bundleVO.getBonusName() + "|";
                                bonusCodes += bundleVO.getBonusCode() + "|";
                                bonusRates += bundleVO.getMultFactor() + "|";
                                // get the bundle type from the
                                // BonusAccountDetailsVO
                                bundleTypes += bundleVO.getBundleType() + "|";
                                // get the bonus value from the
                                // BonusAccountDetailsVO by deviding it by
                                // conversion factor.
                                bonusValuesStr += bonusValues + "|";
                                bonusValidityStr += bonusValidity + "|";
                            }
                        }
                    }
                }
            }
            // long requestedValue=p_transferVO.getRequestedAmount();
            // Calculate the selector bonus
            if (selectorAccBundleVO != null && selectorAccBundleVO.getRestrictedOnIN().equals("Y")) {
                bonusConvFactor = Double.parseDouble(selectorAccBundleVO.getMultFactor());
                bonusValueDouble = Double.parseDouble(selectorAccBundleVO.getBonusValue());
                bonusTalkTimeAfterConversion = bonusValueDouble / bonusConvFactor;

                // set the calculate selector bonus rate in the card group
                // detail vo.
                // p_cardGroupVO.setBonusTalkTimeRate(Double.parseDouble(PretupsBL.getDisplayAmount((long)bonusTalkTimeAfterConversion)));
                if (PretupsI.SYSTEM_AMOUNT.equals(selectorAccBundleVO.getType()))
                    p_cardGroupVO.setBonusTalkTimeRate(PretupsBL.getSystemAmount(bonusTalkTimeAfterConversion));
                else
                    p_cardGroupVO.setBonusTalkTimeRate(bonusTalkTimeAfterConversion);
                p_cardGroupVO.setBonusTalkTimeType(selectorAccBundleVO.getType());
                p_cardGroupVO.setBonusTalkTimeValidity(selectorAccBundleVO.getBonusValidity());
                p_cardGroupVO.setBonusTalkTimeConvFactor(Double.parseDouble(selectorAccBundleVO.getMultFactor()));
                p_cardGroupVO.setBonusTalkTimeBundleType(selectorAccBundleVO.getBundleType());
            }
            // remove the last pipe before setting it in to the transfer vo.
            if (!BTSLUtil.isNullString(bonusIds))
                bonusIds = bonusIds.substring(0, bonusIds.length() - 1);

            if (!BTSLUtil.isNullString(bonusNames))
                bonusNames = bonusNames.substring(0, bonusNames.length() - 1);

            if (!BTSLUtil.isNullString(bonusCodes))
                bonusCodes = bonusCodes.substring(0, bonusCodes.length() - 1);

            if (!BTSLUtil.isNullString(bonusRates))
                bonusRates = bonusRates.substring(0, bonusRates.length() - 1);

            if (!BTSLUtil.isNullString(bundleTypes))
                bundleTypes = bundleTypes.substring(0, bundleTypes.length() - 1);

            if (!BTSLUtil.isNullString(bonusValuesStr))
                bonusValuesStr = bonusValuesStr.substring(0, bonusValuesStr.length() - 1);

            if (!BTSLUtil.isNullString(bonusValidityStr))
                bonusValidityStr = bonusValidityStr.substring(0, bonusValidityStr.length() - 1);

            // Set the bonus bundles in to the transfer vo.
            if (_log.isDebugEnabled())
                _log.debug("setBonusAccountDetails", "selectorAccBundleVO=" + selectorAccBundleVO);
            if (selectorAccBundleVO != null) {
                p_transferVO.setSelectorBundleId(selectorAccBundleVO.getBundleID());
                p_transferVO.setSelectorBundleType(selectorAccBundleVO.getBundleType());
                p_transferVO.setBonusTalkTimeValue((long) Double.parseDouble(selectorAccBundleVO.getBonusValue()));
                p_transferVO.setBonusBundleIdS(bonusIds);
                p_transferVO.setBonusBundleTypes(bundleTypes);
                p_transferVO.setBonusBundleValues(bonusValuesStr);
                p_transferVO.setBonusBundleValidities(bonusValidityStr);
                p_transferVO.setBonusBundleNames(bonusNames);
                p_transferVO.setBonusBundleCode(bonusCodes);
                p_transferVO.setBonusBundleRate(bonusRates);

                int creditbonusvalidity = Integer.parseInt(selectorAccBundleVO.getBonusValidity());
                p_transferVO.setReceiverCreditBonusValidity(creditbonusvalidity);
            } else {
                p_cardGroupVO.setBonusTalkTimeType(PretupsI.SYSTEM_AMOUNT);
                p_cardGroupVO.setBonusTalkTimeValidity("0");
                p_cardGroupVO.setBonusTalkTimeConvFactor(Double.parseDouble("1"));
            }
        } catch (BTSLBaseException bex) {
            _log.errorTrace("setBonusAccountDetails: Exception print stack trace:", bex);
            _log.error("setBonusAccountDetails", "BTSLException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("setBonusAccountDetails: Exception print stack trace:", e);
            _log.error("setBonusAccountDetails", "BTSLException " + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "setBonusAccountDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("setBonusAccountDetails", "Exiting");
    }

    public static boolean isNull(String p_string) {
        _log.debug("isNull", "p_string=" + p_string);
        boolean isNull = false;
        if (BTSLUtil.isNullString(p_string) || p_string == "null")
            isNull = true;
        return isNull;
    }

    /**
     * Method to load the card group details and calculate the users access fee,
     * talk time etc
     * 
     * @param p_transferVO
     * @param p_cardGroupDetailsVO
     * @param p_checkMultipleOf
     * @throws BTSLBaseException
     */
    public static void calculateCardGroupDetailsP2PCreditCardransfer(Connection p_con, TransferVO p_transferVO, String p_module, boolean p_checkMultipleOf) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("calculateCardGroupDetailsP2PCreditCardransfer", "Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID=" + p_transferVO.getRequestID() + " Sub Service=" + p_transferVO.getSubService() + "p_checkMultipleOf=" + p_checkMultipleOf);
        try {
            CardGroupDetailsVO cardGroupDetailsVO = loadCardGroupDetails(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getRequestedAmount(), p_transferVO.getTransferDateTime());

            // added for card group slab suspend/resume
            if (cardGroupDetailsVO.getStatus().equals(PretupsI.SUSPEND))
                throw new BTSLBaseException("CardGroupBL", "calculateCardGroupDetailsP2PCreditCardransfer", SelfTopUpErrorCodesI.CARD_GROUP_SLAB_SUSPENDED);

            // 100 because all requets should go through if multiple of is 1.
            if (p_checkMultipleOf && cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % cardGroupDetailsVO.getMultipleOf() != 0)
                    throw new BTSLBaseException("CardGroupBL", "calculateCardGroupDetailsP2PCreditCardransfer", SelfTopUpErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()) }, null);
            }
            p_transferVO.setVersion(cardGroupDetailsVO.getVersion());
            p_transferVO.setCardGroupID(cardGroupDetailsVO.getCardGroupID());
            p_transferVO.setCardGroupCode(cardGroupDetailsVO.getCardGroupCode());
            p_transferVO.setMinCardGroupAmount(cardGroupDetailsVO.getStartRange());

            // added for card group suspend/resume
            p_transferVO.setStatus(cardGroupDetailsVO.getStatus());

            p_transferVO.setReceiverCreditBonusValidity(cardGroupDetailsVO.getBonusValidityValue());
            p_transferVO.setBoth(cardGroupDetailsVO.getBoth());
            p_transferVO.setOnline(cardGroupDetailsVO.getOnline());

            // Set sender and receiver's conversion factor in transfer vo by
            // Vinay on 01-July-09.
            p_transferVO.setSenderConvFactor(Double.parseDouble(cardGroupDetailsVO.getSenderConvFactor()));
            p_transferVO.setReceiverConvFactor(Double.parseDouble(cardGroupDetailsVO.getReceiverConvFactor()));
            // Set the bonus account details into the transfer VO by Vinay on
            // 01-July-09.
            setBonusAccountDetails(cardGroupDetailsVO, p_transferVO);

            if (p_module.equalsIgnoreCase(PretupsI.P2P_MODULE)) {
                p_transferVO.setSenderTax1Type(PretupsI.NOT_AVAILABLE);
                p_transferVO.setSenderTax2Type(PretupsI.NOT_AVAILABLE);
                p_transferVO.setSenderTransferValue(p_transferVO.getTransferValue());
                calculateP2PReceiverValues((P2PTransferVO) p_transferVO, cardGroupDetailsVO, p_transferVO.getSubService(), p_checkMultipleOf);
            }

        } catch (BTSLBaseException bex) {
            _log.error("calculateCardGroupDetailsP2PCreditCardransfer", "BTSLException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("calculateCardGroupDetailsP2PCreditCardransfer: Exception print stack trace:", e);
            _log.error("calculateCardGroupDetailsP2PCreditCardransfer", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateCardGroupDetailsP2PCreditCardransfer]", p_transferVO.getTransferID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateCardGroupDetailsP2PCreditCardransfer", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("calculateCardGroupDetailsP2PCreditCardransfer", "Exiting");
    }

    // @@ankuj
    /**
     * Method to load the SOS card group details and calculate the subscriber
     * service tax.
     * 
     * @param p_transferVO
     * @param p_cardGroupDetailsVO
     * @param p_checkMultipleOf
     * @throws BTSLBaseException
     */
    public static void calculateSOSCardGroupDetails(Connection p_con, TransferVO p_transferVO, String p_module, boolean p_checkMultipleOf) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("calculateSOSCardGroupDetails", "Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID=" + p_transferVO.getRequestID() + " Sub Service=" + p_transferVO.getSubService() + "p_checkMultipleOf=" + p_checkMultipleOf);
        try {
            CardGroupDetailsVO cardGroupDetailsVO = loadCardGroupDetails(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getRequestedAmount(), p_transferVO.getTransferDateTime());
            if (cardGroupDetailsVO.getStatus().equals(PretupsI.SUSPEND))
                throw new BTSLBaseException("CardGroupBL", "calculateSOSCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_SLAB_SUSPENDED);
            cardGroupDetailsVO.setBonus1(Double.parseDouble(PretupsBL.getDisplayAmount((long) cardGroupDetailsVO.getBonus1())));
            cardGroupDetailsVO.setBonus2(Double.parseDouble(PretupsBL.getDisplayAmount((long) cardGroupDetailsVO.getBonus2())));
            if (p_checkMultipleOf && cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % cardGroupDetailsVO.getMultipleOf() != 0)
                    throw new BTSLBaseException("CardGroupBL", "calculateSOSCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()) }, null);
            }
            p_transferVO.setVersion(cardGroupDetailsVO.getVersion());
            p_transferVO.setCardGroupID(cardGroupDetailsVO.getCardGroupID());
            p_transferVO.setCardGroupCode(cardGroupDetailsVO.getCardGroupCode());
            p_transferVO.setMinCardGroupAmount(cardGroupDetailsVO.getStartRange());
            p_transferVO.setStatus(cardGroupDetailsVO.getStatus());
            p_transferVO.setBoth(cardGroupDetailsVO.getBoth());
            p_transferVO.setOnLine(cardGroupDetailsVO.getOnline());

            TransferItemVO transferItemVO = null;

            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(0);

            long requestedValue = p_transferVO.getRequestedAmount();
            double tax1Rate = cardGroupDetailsVO.getSenderTax1Rate();
            double tax2Rate = cardGroupDetailsVO.getSenderTax2Rate();
            double accessFeeRate = cardGroupDetailsVO.getSenderAccessFeeRate();

            double bonusTalkTimeRate = cardGroupDetailsVO.getBonusTalkTimeRate();
            double bonus1 = cardGroupDetailsVO.getBonus1();
            double bonus2 = cardGroupDetailsVO.getBonus2();
            long bonus1validityP2p = cardGroupDetailsVO.getBonus1validity();
            long bonus2validityP2p = cardGroupDetailsVO.getBonus2validity();
            long creditbonusvalidityP2p = cardGroupDetailsVO.getBonusTalktimevalidity();

            long calculatedAccessFee = 0;
            long calculatedTax1Value = 0;
            long calculatedTax2Value = 0;
            long transferValue = 0;
            long calculatedBonusTalkTimeValue = 0;

            // Is Bonus Talk time on Requested Value ??
            cardGroupDetailsVO.setBonusTalkTimeType(PretupsI.SYSTEM_AMOUNT);// added
                                                                            // by
                                                                            // ankuj
                                                                            // as
                                                                            // this
                                                                            // value
                                                                            // is
                                                                            // coming
                                                                            // to
                                                                            // be
                                                                            // null
            calculatedBonusTalkTimeValue = calculatorI.calculateCardGroupBonus(cardGroupDetailsVO.getBonusTalkTimeType(), bonusTalkTimeRate, requestedValue);
            cardGroupDetailsVO.setBonusTalkTimeValue(calculatedBonusTalkTimeValue);

            if (_log.isDebugEnabled())
                _log.debug("calculateSOSCardGroupDetails", "Before Setting RequestedValue=" + requestedValue + " Access fee Type=" + cardGroupDetailsVO.getSenderAccessFeeType() + " Access fee Rate=" + accessFeeRate + " senderTax1 Type=" + cardGroupDetailsVO.getSenderTax1Type() + " Sender Tax 1 Rate=" + tax1Rate + " senderTax2 Type=" + cardGroupDetailsVO.getSenderTax2Type() + " Sender Tax 2 Rate=" + tax2Rate);

            calculatedAccessFee = calculatorI.calculateAccessFee(accessFeeRate, cardGroupDetailsVO.getSenderAccessFeeType(), requestedValue, cardGroupDetailsVO.getMinSenderAccessFee(), cardGroupDetailsVO.getMaxSenderAccessFee());
            p_transferVO.setSenderAccessFeeRate(accessFeeRate);
            p_transferVO.setSenderAccessFeeType(cardGroupDetailsVO.getSenderAccessFeeType());
            p_transferVO.setSenderAccessFee(calculatedAccessFee);

            calculatedTax1Value = calculatorI.calculateCardGroupTax1(cardGroupDetailsVO.getSenderTax1Type(), tax1Rate, requestedValue);
            p_transferVO.setSenderTax1Type(cardGroupDetailsVO.getSenderTax1Type());
            p_transferVO.setSenderTax1Rate(tax1Rate);
            p_transferVO.setSenderTax1Value(calculatedTax1Value);

            if (cardGroupDetailsVO.getSenderTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT))
                calculatedTax2Value = (long) tax2Rate;
            else // If percentage
            {
                if (SystemPreferences.IS_TAX2_ON_TAX1)
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(cardGroupDetailsVO.getSenderTax2Type(), tax2Rate, calculatedTax1Value);
                else
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(cardGroupDetailsVO.getSenderTax2Type(), tax2Rate, requestedValue);
            }
            p_transferVO.setSenderTax2Type(cardGroupDetailsVO.getSenderTax2Type());
            p_transferVO.setSenderTax2Rate(tax2Rate);
            p_transferVO.setSenderTax2Value(calculatedTax2Value);

            boolean isUpfrontServiceTaxApplicable = ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_ST_DEDUCT_UPFRONT, p_transferVO.getNetworkCode())).booleanValue();
            if (isUpfrontServiceTaxApplicable) {
                transferValue = (requestedValue + calculatedBonusTalkTimeValue) - (calculatedTax1Value + calculatedTax2Value + calculatedAccessFee);
                transferItemVO.setTransferValue(transferValue);
                p_transferVO.setSenderTransferValue(transferValue);
                p_transferVO.setSenderSettlementValue(requestedValue);
            } else {
                transferValue = requestedValue + (calculatedTax1Value + calculatedTax2Value + calculatedAccessFee);
                transferItemVO.setTransferValue(requestedValue + calculatedBonusTalkTimeValue);
                p_transferVO.setSenderTransferValue(requestedValue + calculatedBonusTalkTimeValue);
                p_transferVO.setSenderSettlementValue(transferValue);
            }
            p_transferVO.setReceiverBonus1(bonus1);
            p_transferVO.setReceiverBonus2(bonus2);
            p_transferVO.setBonusTalkTimeValue(calculatedBonusTalkTimeValue);
            p_transferVO.setReceiverBonus1Validity(bonus1validityP2p);
            p_transferVO.setReceiverBonus2Validity(bonus2validityP2p);
            p_transferVO.setReceiverCreditBonusValidity(creditbonusvalidityP2p);
            p_transferVO.setReceiverValPeriodType(cardGroupDetailsVO.getValidityPeriodType());
            // Lohit
            p_transferVO.setValidityDaysToExtend(cardGroupDetailsVO.getValidityPeriod());
            calculatorI.setCalculatedCardGroupValues(p_transferVO.getSubService(), cardGroupDetailsVO, p_transferVO);
            if (_log.isDebugEnabled())
                _log.debug("calculateSOSCardGroupDetails", "After Setting transferValue=" + transferValue + " calculatedAccessFee=" + calculatedAccessFee + " Access fee Type=" + cardGroupDetailsVO.getSenderAccessFeeType() + " calculatedTax1Value=" + calculatedTax1Value + " calculatedTax2Value=" + calculatedTax2Value + ", Settlement Value" + p_transferVO.getSenderTransferValue());
        } catch (BTSLBaseException bex) {
            _log.errorTrace("calculateSOSCardGroupDetails: Exception print stack trace:", bex);
            _log.error("calculateSOSCardGroupDetails", "BTSLException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("calculateSOSCardGroupDetails: Exception print stack trace:", e);
            _log.error("calculateSOSCardGroupDetails", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateSOSCardGroupDetails]", p_transferVO.getTransferID(), "", "", "Exception while calculating SOS recharge value" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateSOSCardGroupDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("calculateSOSCardGroupDetails", "Exiting...");
    }

    /**
     * Method to load the card group Slab amount
     * 
     * @param p_transferVO
     * @param p_cardGroupDetailsVO
     * @param p_checkMultipleOf
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public static void calculateCardGroupSlab(Connection p_con, TransferVO p_transferVO, String p_module, boolean p_checkMultipleOf) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("calculateCardGroupSlab", "Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID=" + p_transferVO.getRequestID() + " Sub Service=" + p_transferVO.getSubService() + "p_checkMultipleOf=" + p_checkMultipleOf);
        try {
            CardGroupDetailsVO cardGroupDetailsVO = loadCardGroupSlab(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getTransferDateTime());
            // added for card group slab suspend/resume
            if (PretupsI.SUSPEND.equals(cardGroupDetailsVO.getStatus()))
                throw new BTSLBaseException("CardGroupBL", "calculateCardGroupSlab", SelfTopUpErrorCodesI.CARD_GROUP_SLAB_SUSPENDED);
            p_transferVO.setVersion(cardGroupDetailsVO.getVersion());
            p_transferVO.setCardGroupID(cardGroupDetailsVO.getCardGroupID());
            p_transferVO.setCardGroupCode(cardGroupDetailsVO.getCardGroupCode());
            p_transferVO.setMinCardGroupSlabAmount(PretupsBL.getDisplayAmount(cardGroupDetailsVO.getStartRange()));
            p_transferVO.setMaxCardGroupSlabAmount(PretupsBL.getDisplayAmount(cardGroupDetailsVO.getEndRange()));

            // added for card group suspend/resume
            p_transferVO.setStatus(cardGroupDetailsVO.getStatus());
            p_transferVO.setBoth(cardGroupDetailsVO.getBoth());
            p_transferVO.setOnline(cardGroupDetailsVO.getOnline());

        } catch (BTSLBaseException bex) {
            _log.error("calculateCardGroupSlab", "BTSLException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("calculateCardGroupSlab: Exception print stack trace:", e);
            _log.error("calculateCardGroupSlab", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateCardGroupSlab]", p_transferVO.getTransferID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateCardGroupSlab", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("calculateCardGroupSlab", "Exiting");
    }

    /**
     * Method to load the CardGroupDetailsVO
     * 
     * @param p_con
     * @param p_cardGroupSetID
     * @param java
     *            .util.Date p_applicableDate
     * @throws BTSLBaseException
     * @author sonali.garg
     */

    public static CardGroupDetailsVO loadCardGroupSlab(Connection p_con, String p_cardGroupSetID, java.util.Date p_applicableDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupDetails", "Entered p_cardGroupSetID=" + p_cardGroupSetID + " p_applicableDate=" + p_applicableDate);
        CardGroupDetailsVO cardGroupDetailsVO = null;
        try {
            cardGroupDetailsVO = _cardGroupDAO.loadCardGroupMinMax(p_con, p_cardGroupSetID, p_applicableDate);
        } catch (BTSLBaseException bex) {
            _log.error("loadCardGroupSlab", "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace("loadCardGroupSlab: Exception print stack trace:", e);
            _log.error("loadCardGroupSlab", "Exception " + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupBL[loadCardGroupDetails]","","","","Exception while get the loading the card group details:"+e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "loadCardGroupSlab", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupSlab", "Exiting");
        return cardGroupDetailsVO;
    }
}
