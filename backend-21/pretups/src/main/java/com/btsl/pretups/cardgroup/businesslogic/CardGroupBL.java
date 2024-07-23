package com.btsl.pretups.cardgroup.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.beanutils.BeanUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.cosmgmt.businesslogic.CosDAO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;

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
    private static CardGroupDAO _cardGroupDAO = new CardGroupDAO();
    private static OperatorUtilI calculatorI = null;
    private static final float EPSILON=0.0000001f;
    // calculate the tax
    static {
        final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.CARD_GROUP_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
	 * ensures no instantiation
	 */
    private CardGroupBL(){
    	
    }
    
    public static CardGroupDetailsVO loadCardGroupDetails(Connection p_con, String p_cardGroupSetID, long p_requestAmount, java.util.Date p_applicableDate) throws BTSLBaseException {
        final String methodName = "loadCardGroupDetails";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_cardGroupSetID=");
    	loggerValue.append(p_cardGroupSetID);
		loggerValue.append(" p_requestAmount=");
    	loggerValue.append(p_requestAmount);
    	loggerValue.append(" p_applicableDate=");
    	loggerValue.append(p_applicableDate);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
        CardGroupDetailsVO cardGroupDetailsVO = null;
        try {
            cardGroupDetailsVO = CardGroupCache.getCardGroupDetails(p_cardGroupSetID, p_requestAmount, p_applicableDate);
        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.CARD_GROUP_NOT_LOADED,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupBL[loadCardGroupDetails]","","","","Exception while get the loading the card group details:"+bex.getMessage());
            throw bex;
        } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.CARD_GROUP_NOT_LOADED,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupBL[loadCardGroupDetails]","","","","Exception while get the loading the card group details:"+e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
        return cardGroupDetailsVO;
    }
    
    public static CardGroupDetailsVO loadCardGroupDetails(Connection p_con, String p_cardGroupSetID, long p_requestAmount, java.util.Date p_applicableDate, 
    		String pVoucherType, String pVoucherSegment, String pProductId) throws BTSLBaseException {
        final String methodName = "loadCardGroupDetails";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_cardGroupSetID=");
    	loggerValue.append(p_cardGroupSetID);
		loggerValue.append(" p_requestAmount=");
    	loggerValue.append(p_requestAmount);
    	loggerValue.append(" p_applicableDate=");
    	loggerValue.append(p_applicableDate);
    	loggerValue.append(" pVoucherType=");
    	loggerValue.append(pVoucherType);
    	loggerValue.append(" pVoucherSegment=");
    	loggerValue.append(pVoucherSegment);
    	loggerValue.append(" pProductId=");
    	loggerValue.append(pProductId);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
        CardGroupDetailsVO cardGroupDetailsVO = null;
        try {
            cardGroupDetailsVO = CardGroupCache.getCardGroupDetails(p_cardGroupSetID, p_requestAmount, p_applicableDate, pVoucherType, pVoucherSegment, pProductId);
        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.CARD_GROUP_NOT_LOADED,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupBL[loadCardGroupDetails]","","","","Exception while get the loading the card group details:"+bex.getMessage());
            throw bex;
        } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.CARD_GROUP_NOT_LOADED,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupBL[loadCardGroupDetails]","","","","Exception while get the loading the card group details:"+e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
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
       final String methodName = "calculateCardGroupDetails";
       StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
		loggerValue.append("Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID=" );
		loggerValue.append(p_transferVO.getRequestID());
		loggerValue.append(" Sub Service=");
		loggerValue.append(p_transferVO.getSubService());
		loggerValue.append("p_checkMultipleOf=");
		loggerValue.append(p_checkMultipleOf);
		loggerValue.append("p_transferVO::");
		loggerValue.append(p_transferVO);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
        try {
        	CardGroupDetailsVO cardGroupDetailsVO = null;
            if ("VCN".equals(p_transferVO.getServiceType()) && !BTSLUtil.isNullString(p_transferVO.getVoucherType()) && !BTSLUtil.isNullString(p_transferVO.getVoucherSegment())) { 
            		cardGroupDetailsVO = getCardDetailsVo(p_con, p_transferVO);
            } else {
				cardGroupDetailsVO = loadCardGroupDetails(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getRequestedAmount(), p_transferVO
		                .getTransferDateTime());
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

            // added for card group slab suspend/resume
            if (cardGroupDetailsVO.getStatus().equals(PretupsI.SUSPEND)) {
            	EventHandler.handle(EventIDI.CARD_GROUP_SUSPENDED, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                        .getTransferID(), "", "", "Card Group Slab is suspended.");
            	String messageKey = PretupsI.VOUCHER_CONS_SERVICE.equals(p_transferVO.getServiceType()) ? PretupsErrorCodesI.VOUCHER_CARD_GROUP_SLAB_SUSPENDED : 
                	PretupsErrorCodesI.CARD_GROUP_SLAB_SUSPENDED;
                throw new BTSLBaseException("CardGroupBL", methodName, messageKey);
            }

            // 100 because all requets should go through if multiple of is 1.
            if (p_checkMultipleOf && cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % cardGroupDetailsVO.getMultipleOf() != 0) {
                    throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL
                        .getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()) }, null);
                }
            }

            // Set sender and receiver's conversion factor in transfer vo by
            // Vinay on 01-July-09.
            p_transferVO.setSenderConvFactor(Double.parseDouble(cardGroupDetailsVO.getSenderConvFactor()));
            p_transferVO.setReceiverConvFactor(Double.parseDouble(cardGroupDetailsVO.getReceiverConvFactor()));

            // Set the bonus account details into the transfer VO by Vinay on
            // 01-July-09.
            // setBonusAccountDetails(cardGroupDetailsVO,p_transferVO);

            if (p_module.equalsIgnoreCase(PretupsI.P2P_MODULE)) {
            	if (!PretupsI.VOUCHER_CONS_SERVICE.equals(p_transferVO.getServiceType())) {
            		calculateP2PSenderValues((P2PTransferVO) p_transferVO, cardGroupDetailsVO, p_transferVO.getSubService(), p_checkMultipleOf);            		
            	}
                calculateP2PReceiverValues((P2PTransferVO) p_transferVO, cardGroupDetailsVO, p_transferVO.getSubService(), p_checkMultipleOf);
            } else {
                // TO DO: WILL THERE BE TAXES IN SENDER ????????
                // TO DO: Make it more generalize the commonly used code
                calculateC2SSenderValues((C2STransferVO) p_transferVO, cardGroupDetailsVO, p_transferVO.getSubService(), p_checkMultipleOf);
                calculateC2SReceiverValues((C2STransferVO) p_transferVO, cardGroupDetailsVO, p_transferVO.getSubService(), p_checkMultipleOf);
            }
            // added by gaurav for COS change
            // if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue() )

            if (BTSLUtil.NullToString(p_transferVO.getModule()).equalsIgnoreCase(PretupsI.C2S_MODULE)) {
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    if (cardGroupDetailsVO.getCosRequired().equalsIgnoreCase(PretupsI.YES)) {
                        final C2STransferItemVO c2sTransferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(1);
                        // select NEW_COS from COS_MASTERS table on the basis of
                        // requested amount range
                        // set the ReceiverAllServiceclassId in TransferVO as we
                        // get from COS_MASTERS table where status = 'A'
                        // In New Cos ,the serviceClassId from COS_MASTERS is to
                        // be set
                        p_transferVO.setCosRequired(cardGroupDetailsVO.getCosRequired());
                        p_transferVO.setNewCos(new CosDAO().loadNewCOSWithinRange(p_con, String.valueOf(p_transferVO.getRequestedAmount()), c2sTransferItemVO
                            .getServiceClassCode()));
                    }
                    if (cardGroupDetailsVO.getCosRequired().equalsIgnoreCase(PretupsI.NO)) {
                        p_transferVO.setCosRequired(cardGroupDetailsVO.getCosRequired());
                        p_transferVO.setNewCos(null);
                    }
                }

                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                    final double calculatedBonusValue = calculatorI.calculateINPromo(cardGroupDetailsVO.getInPromo(), cardGroupDetailsVO.getBonusTalkTimeValue());

                    p_transferVO.setInPromo(calculatedBonusValue);
                    // p_transferVO.setReceiverBonusValue((long) calculatedBonusValue);
                    p_transferVO.setReceiverBonusValue( BTSLUtil.parseDoubleToLong(calculatedBonusValue));
                    p_transferVO.setRechargeComment(calculatorI.getRechargeComment(p_transferVO.getRequestedAmount(), cardGroupDetailsVO.getBonusTalkTimeValue()));
                }

            }

        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
    		loggerValue.append("BTSLException ");
    		loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            throw bex;
        } catch (Exception e) {
    		loggerValue.setLength(0);
    		loggerValue.append("Exception ");
    		loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.CARD_GROUP_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getTransferID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
    }

	
    /**
     * Method to load the card group details for special cases and calculate the users access fee,
     * talk time etc
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    private static CardGroupDetailsVO getCardDetailsVo(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
    	final String methodName = "getCardDetailsVo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,"Entered");
        }
		VomsProductDAO vomsProductDAO=new VomsProductDAO();
		CardGroupDetailsVO cardGroupDetailsVO;
		try {
		if(VOMSI.VOUCHER_SEGMENT_NATIONAL.equals(p_transferVO.getVoucherSegment()) && p_transferVO.getNetworkCode()!=null && p_transferVO.getReceiverNetworkCode()!=null) {
			TransactionLog.log("Swapping productId", PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
			"ServiceType="+p_transferVO.getServiceType() + "Sender Network Code"+ p_transferVO.getNetworkCode() +"Receiver Network code"+p_transferVO.getReceiverNetworkCode()  , PretupsI.TXN_LOG_STATUS_SUCCESS, "", null, null);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "get Info 2 "+p_transferVO.getInfo2()+"Network Code = "+p_transferVO.getNetworkCode()+" Receiver Network Code " + p_transferVO.getReceiverNetworkCode());
			}
			String productName=p_transferVO.getInfo2();
			if((productName.substring(0, 2)).equals(p_transferVO.getNetworkCode())) {
				productName=productName.replaceFirst(p_transferVO.getNetworkCode(), p_transferVO.getReceiverNetworkCode());
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "New product name"+productName+"Profile First two digit :"+(productName.substring(0, 2)));
			}
			String recProductId=vomsProductDAO.getProfileID( p_con, productName,p_transferVO.getReceiverNetworkCode());

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Receiver product id"+recProductId);
			}
			cardGroupDetailsVO = loadCardGroupDetails(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getRequestedAmount(), p_transferVO
	            .getTransferDateTime(), p_transferVO.getVoucherType(), p_transferVO.getVoucherSegment(), recProductId);
			}
		else {
			cardGroupDetailsVO = loadCardGroupDetails(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getRequestedAmount(), p_transferVO.getTransferDateTime(), p_transferVO.getVoucherType(), p_transferVO.getVoucherSegment(), p_transferVO.getProductId());
		}
		} catch (BTSLBaseException e) {
            _log.error(methodName,"Exiting with exception:");
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.CARD_GROUP_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getNetworkCode(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw e;
		} 
		catch (Exception e) {
            _log.error(methodName,"Exiting with exception:");
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.CARD_GROUP_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getNetworkCode(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.PRODUCT_ID_NOT_FOUND,e);
		}
		return cardGroupDetailsVO;
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
    	StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered with p_transferVO and p_cardGroupDetailsVO with p_subService=");
    	loggerValue.append(p_subService);
		loggerValue.append("p_checkMultipleOf=");
    	loggerValue.append(p_checkMultipleOf);
        if (_log.isDebugEnabled()) {
            _log.debug("calculateP2PSenderValues",loggerValue );
        }
        final String methodName = "calculateP2PSenderValues";
        TransferItemVO transferItemVO = null;
        // access fee, taxes
        try {
            // 100 because all requets should go through if multiple of is 1.
            if (p_checkMultipleOf && p_cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && p_cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % p_cardGroupDetailsVO.getMultipleOf() != 0) {
                    throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL
                        .getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(p_cardGroupDetailsVO.getMultipleOf()) }, null);
                }
            }

            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(0);

            final long requestedValue = p_transferVO.getRequestedAmount();
            final double tax1Rate = p_cardGroupDetailsVO.getSenderTax1Rate();
            final double tax2Rate = p_cardGroupDetailsVO.getSenderTax2Rate();
            final double accessFeeRate = p_cardGroupDetailsVO.getSenderAccessFeeRate();
            long calculatedAccessFee = 0;
            long calculatedTax1Value = 0;
            long calculatedTax2Value = 0;
            long transferValue = 0;

            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Before Setting RequestedValue=");
            	loggerValue.append(requestedValue);
        		loggerValue.append(" Access fee Type=");
            	loggerValue.append(p_cardGroupDetailsVO.getSenderAccessFeeType());
            	loggerValue.append(" Access fee Rate=");
            	loggerValue.append(accessFeeRate);
        		loggerValue.append(" senderTax1 Type=" );
            	loggerValue.append(p_cardGroupDetailsVO.getSenderTax1Type());
            	loggerValue.append(" Sender Tax 1 Rate=");
            	loggerValue.append(tax1Rate);
        		loggerValue.append(" senderTax2 Type=");
            	loggerValue.append(p_cardGroupDetailsVO.getSenderTax2Type());
            	loggerValue.append(" Sender Tax 2 Rate=");
            	loggerValue.append(tax2Rate);
                _log.debug(methodName,loggerValue);
            }

            calculatedAccessFee = calculatorI.calculateAccessFee(accessFeeRate, p_cardGroupDetailsVO.getSenderAccessFeeType(), requestedValue, p_cardGroupDetailsVO
                .getMinSenderAccessFee(), p_cardGroupDetailsVO.getMaxSenderAccessFee());

            p_transferVO.setSenderAccessFee(calculatedAccessFee);

            calculatedTax1Value = calculatorI.calculateCardGroupTax1(p_cardGroupDetailsVO.getSenderTax1Type(), tax1Rate, requestedValue);
            p_transferVO.setSenderTax1Type(p_cardGroupDetailsVO.getSenderTax1Type());
            p_transferVO.setSenderTax1Rate(tax1Rate);
            p_transferVO.setSenderTax1Value(calculatedTax1Value);

            if (p_cardGroupDetailsVO.getSenderTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
                // calculatedTax2Value = (long) tax2Rate;
                calculatedTax2Value = BTSLUtil.parseDoubleToLong(tax2Rate);
            } else // If percentage
            {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getSenderTax2Type(), tax2Rate, calculatedTax1Value);
                } else {
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getSenderTax2Type(), tax2Rate, requestedValue);
                }
            }
            p_transferVO.setSenderTax2Type(p_cardGroupDetailsVO.getSenderTax2Type());
            p_transferVO.setSenderTax2Rate(tax2Rate);
            p_transferVO.setSenderTax2Value(calculatedTax2Value);

            transferValue = calculatorI.calculateSenderTransferValue(requestedValue, calculatedTax1Value, calculatedTax2Value, calculatedAccessFee);
            // Divide the transfer amount by conversion factor.
            final double senderConvFactor = p_transferVO.getSenderConvFactor();
            if (Math.abs(senderConvFactor-0) >EPSILON) {
                // transferValue = (long) ((double) transferValue / senderConvFactor);
                transferValue = BTSLUtil.parseDoubleToLong(transferValue / senderConvFactor);
            }

            p_transferVO.setSenderTransferValue(transferValue);
            transferItemVO.setTransferValue(transferValue);
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("After Setting transferValue=" );
            	loggerValue.append(transferValue);
        		loggerValue.append(" calculatedAccessFee=" );
            	loggerValue.append(calculatedAccessFee);
            	loggerValue.append(" Access fee Type=");
            	loggerValue.append(p_cardGroupDetailsVO.getSenderAccessFeeType());
        		loggerValue.append(" calculatedTax1Value=");
            	loggerValue.append(calculatedTax1Value);
            	loggerValue.append(" calculatedTax2Value=" );
            	loggerValue.append(calculatedTax2Value);
                _log.debug(methodName,loggerValue);
            }

        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", transferItemVO
                .getTransferID(), transferItemVO.getMsisdn(), "", "Not able to calculate the sender talk values getting Exception=" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }

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
        final String methodName = "calculateP2PReceiverValues";
        StringBuilder loggerValue= new StringBuilder(); 
    	loggerValue.append("Entered with p_subService" );
    	loggerValue.append(p_subService);
		loggerValue.append(" p_checkMultipleOf=");
    	loggerValue.append(p_checkMultipleOf);
        // validity and grace period,taxes,bonus talk value,bonus
        // validity,access fee
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }

        TransferItemVO transferItemVO = null;
        try {
            // 100 because all requets should go through if multiple of is 1.
            if (p_checkMultipleOf && p_cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && p_cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % p_cardGroupDetailsVO.getMultipleOf() != 0) {
                    throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL
                        .getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(p_cardGroupDetailsVO.getMultipleOf()) }, null);
                }
            }

            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
            // Bonus Talk time on Requested Value
            setBonusAccountDetails(p_cardGroupDetailsVO, p_transferVO);
            long requestedValue = p_transferVO.getRequestedAmount();
            final double bonusTalkTimeRate = p_cardGroupDetailsVO.getBonusTalkTimeRate();
            final double accessFeeRate = p_cardGroupDetailsVO.getReceiverAccessFeeRate();
            final double tax1Rate = p_cardGroupDetailsVO.getReceiverTax1Rate();
            final double tax2Rate = p_cardGroupDetailsVO.getReceiverTax2Rate();
            long calculatedAccessFee = 0;
            long calculatedBonusTalkTimeValue = 0;
            long calculatedTax1Value = 0;
            long calculatedTax2Value = 0;
            long transferValue = 0;
            final long creditbonusvalidityP2p = p_cardGroupDetailsVO.getBonusValidityValue();
            final String onLine = p_cardGroupDetailsVO.getOnline();
            final String both = p_cardGroupDetailsVO.getBoth();

            calculatedAccessFee = calculatorI.calculateAccessFee(accessFeeRate, p_cardGroupDetailsVO.getReceiverAccessFeeType(), requestedValue, p_cardGroupDetailsVO
                .getMinReceiverAccessFee(), p_cardGroupDetailsVO.getMaxReceiverAccessFee());

            p_transferVO.setReceiverAccessFee(calculatedAccessFee);

            // Bonus Talk time on Requested Value
            calculatedBonusTalkTimeValue = calculatorI.calculateCardGroupBonus(p_cardGroupDetailsVO.getBonusTalkTimeType(), bonusTalkTimeRate, requestedValue);

            calculatedTax1Value = calculatorI.calculateCardGroupTax1(p_cardGroupDetailsVO.getReceiverTax1Type(), tax1Rate, requestedValue);
            p_transferVO.setReceiverTax1Type(p_cardGroupDetailsVO.getReceiverTax1Type());
            p_transferVO.setReceiverTax1Rate(tax1Rate);
            p_transferVO.setReceiverTax1Value(calculatedTax1Value);

            if (p_cardGroupDetailsVO.getReceiverTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
                // calculatedTax2Value = (long) tax2Rate;
                calculatedTax2Value = BTSLUtil.parseDoubleToLong(tax2Rate);
            } else // If percentage
            {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getReceiverTax2Type(), tax2Rate, calculatedTax1Value);
                } else {
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getReceiverTax2Type(), tax2Rate, requestedValue);
                }
            }
            p_transferVO.setReceiverTax2Type(p_cardGroupDetailsVO.getReceiverTax2Type());
            p_transferVO.setReceiverTax2Rate(tax2Rate);
            p_transferVO.setReceiverTax2Value(calculatedTax2Value);

            p_transferVO.setReceiverValPeriodType(p_cardGroupDetailsVO.getValidityPeriodType());
            // Divide the requested amount by receiver's conversion factor.
            final double receiverConvFactor = p_transferVO.getReceiverConvFactor();
            if (Math.abs(receiverConvFactor-0) >EPSILON) {
                // requestedValue = (long) ((double) requestedValue / receiverConvFactor);
                requestedValue =  BTSLUtil.parseDoubleToLong(requestedValue / receiverConvFactor);
            }
            transferValue = calculatorI.calculateReceiverTransferValue(requestedValue, calculatedAccessFee, calculatedTax1Value, calculatedTax2Value,
                calculatedBonusTalkTimeValue);

            p_cardGroupDetailsVO.setBonusTalkTimeValue(calculatedBonusTalkTimeValue);
            p_cardGroupDetailsVO.setTransferValue(transferValue);

            // set the values in transfer VO. According to operator's transfer
            // tax calculator
            calculatorI.setCalculatedCardGroupValues(p_subService, p_cardGroupDetailsVO, p_transferVO);

            p_transferVO.setCardGroupID(p_cardGroupDetailsVO.getCardGroupID());
            p_transferVO.setReceiverCreditBonusValidity(creditbonusvalidityP2p);
            p_transferVO.setOnline(onLine);
            p_transferVO.setBoth(both);
			p_transferVO.setValidityDaysToExtend(p_cardGroupDetailsVO.getValidityPeriod());
			transferItemVO.setValidity(p_cardGroupDetailsVO.getValidityPeriod());
			
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Values Set Access fee=");
            	loggerValue.append(calculatedAccessFee);
        		loggerValue.append(" BonusTalkTimeValue=");
            	loggerValue.append(calculatedBonusTalkTimeValue);
            	loggerValue.append(" Tax1Value=");
            	loggerValue.append(calculatedTax1Value);
        		loggerValue.append(" Tax2Value=");
            	loggerValue.append(calculatedTax2Value);
            	loggerValue.append(" validityPeriodValue=");
            	loggerValue.append(p_cardGroupDetailsVO.getValidityPeriod());
                _log.debug(methodName,loggerValue);
            }
        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", transferItemVO
                .getTransferID(), transferItemVO.getMsisdn(), " ", "Not able to calculate the receiver talk values getting Exception=" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
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
    public static void calculateC2SSenderValues(C2STransferVO p_transferVO, CardGroupDetailsVO p_cardGroupDetailsVO, String p_subService, boolean p_checkMultipleOf) throws BTSLBaseException {
        p_transferVO.setSenderTransferValue(p_transferVO.getTransferValue());
    }

    /**
     * Method to calculate C2S Transfer value for receiver
     * 
     * @param p_transferVO
     * @param p_cardGroupDetailsVO
     * @param p_subService
     * @param p_checkMultipleOf
     * @throws BTSLBaseException
     */
    public static void calculateC2SReceiverValues(C2STransferVO p_transferVO, CardGroupDetailsVO p_cardGroupDetailsVO, String p_subService, boolean p_checkMultipleOf) throws BTSLBaseException {
        final String methodName = "calculateC2SReceiverValues";
        StringBuilder loggerValue= new StringBuilder(); 
        // validity and grace period,taxes,bonus talk value,bonus
        // validity,access fee
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered with p_subService=");
        	loggerValue.append(p_subService);
    		loggerValue.append("p_checkMultipleOf=");
        	loggerValue.append(p_checkMultipleOf);
            _log.debug(methodName,loggerValue);
        }

        try {
            // 100 because all requets should go through if multiple of is 1.
			   if (p_checkMultipleOf && p_cardGroupDetailsVO.getMultipleOf() != 0){
                if (p_transferVO.getRequestedAmount() % p_cardGroupDetailsVO.getMultipleOf() != 0) {
                    throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL
                        .getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(p_cardGroupDetailsVO.getMultipleOf()) }, null);
                }
            }

            // Set the bonus bundle values
            setBonusAccountDetails(p_cardGroupDetailsVO, p_transferVO);

            long requestedValue = p_transferVO.getRequestedAmount();
            final double accessFeeRate = p_cardGroupDetailsVO.getReceiverAccessFeeRate();
            final double bonusTalkTimeRate = p_cardGroupDetailsVO.getBonusTalkTimeRate();
            final double tax1Rate = p_cardGroupDetailsVO.getReceiverTax1Rate();
            final double tax2Rate = p_cardGroupDetailsVO.getReceiverTax2Rate();
            long calculatedAccessFee = 0;
            long calculatedBonusTalkTimeValue = 0;
            long calculatedTax1Value = 0;
            long calculatedTax2Value = 0;
            long transferValue = 0;
            final long creditbonusvalidity = p_cardGroupDetailsVO.getBonusValidityValue();
            final String online = p_cardGroupDetailsVO.getOnline();
            final String both = p_cardGroupDetailsVO.getBoth();

            calculatedAccessFee = calculatorI.calculateAccessFee(accessFeeRate, p_cardGroupDetailsVO.getReceiverAccessFeeType(), requestedValue, p_cardGroupDetailsVO
                .getMinReceiverAccessFee(), p_cardGroupDetailsVO.getMaxReceiverAccessFee());
            p_transferVO.setReceiverAccessFee(calculatedAccessFee);

            // Is Bonus Talk time on Requested Value ??
            if (p_cardGroupDetailsVO.getBonusTalkTimeType() != null) {
                calculatedBonusTalkTimeValue = calculatorI.calculateCardGroupBonus(p_cardGroupDetailsVO.getBonusTalkTimeType(), bonusTalkTimeRate, requestedValue);
            }

            calculatedTax1Value = calculatorI.calculateCardGroupTax1(p_cardGroupDetailsVO.getReceiverTax1Type(), tax1Rate, requestedValue);
            p_transferVO.setReceiverTax1Type(p_cardGroupDetailsVO.getReceiverTax1Type());
            p_transferVO.setReceiverTax1Rate(tax1Rate);
            p_transferVO.setReceiverTax1Value(calculatedTax1Value);

            if (p_cardGroupDetailsVO.getReceiverTax2Type().equals(PretupsI.SYSTEM_AMOUNT)) {
                // calculatedTax2Value = (long) tax2Rate;
                calculatedTax2Value = BTSLUtil.parseDoubleToLong(tax2Rate);
            } else // If percentage
            {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getReceiverTax2Type(), tax2Rate, calculatedTax1Value);
                } else {
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(p_cardGroupDetailsVO.getReceiverTax2Type(), tax2Rate, requestedValue);
                }
            }
            p_transferVO.setReceiverTax2Type(p_cardGroupDetailsVO.getReceiverTax2Type());
            p_transferVO.setReceiverTax2Rate(tax2Rate);
            p_transferVO.setReceiverTax2Value(calculatedTax2Value);

            p_transferVO.setReceiverValPeriodType(p_cardGroupDetailsVO.getValidityPeriodType());

            // Divide the requested amount by sender's conversion factor.
            final double receiverConvFactor = p_transferVO.getReceiverConvFactor();
            if (Math.abs(receiverConvFactor-0) >EPSILON) {
                // requestedValue = (long) ((double) requestedValue / receiverConvFactor);
                requestedValue = BTSLUtil.parseDoubleToLong( requestedValue / receiverConvFactor);
            }
            transferValue = calculatorI.calculateReceiverTransferValue(requestedValue, calculatedAccessFee, calculatedTax1Value, calculatedTax2Value,
                calculatedBonusTalkTimeValue);

            p_cardGroupDetailsVO.setBonusTalkTimeValue(calculatedBonusTalkTimeValue);
            p_cardGroupDetailsVO.setTransferValue(transferValue);
            // set the values in transfer VO. According to operator's transfer
            // tax calculator
            calculatorI.setCalculatedCardGroupValues(p_subService, p_cardGroupDetailsVO, p_transferVO);

            p_transferVO.setCardGroupID(p_cardGroupDetailsVO.getCardGroupID());
            p_transferVO.setBonusTalkTimeValue(calculatedBonusTalkTimeValue);
            
            // cast issue fix
            // p_transferVO.setReceiverBonusValidity((int) creditbonusvalidity);
            p_transferVO.setReceiverBonusValidity(Integer.valueOf(String.valueOf(creditbonusvalidity)));
            p_transferVO.setOnline(online);
            p_transferVO.setBoth(both);

            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Values Set Access fee=");
            	loggerValue.append(calculatedAccessFee);
        		loggerValue.append(" BonusTalkTimeValue=");
            	loggerValue.append(calculatedBonusTalkTimeValue);
            	loggerValue.append(" Tax1Value=");
            	loggerValue.append(calculatedTax1Value);
        		loggerValue.append(" Tax2Value=");
            	loggerValue.append(calculatedTax2Value);
            	loggerValue.append(" validityPeriodValue=");
            	loggerValue.append(p_cardGroupDetailsVO.getValidityPeriod());
                _log.debug(methodName,loggerValue);
            }
        } catch (BTSLBaseException bex) {
            _log.error(methodName, "BTSLException " + bex.getMessage());
            throw bex;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getTransferID(), p_transferVO.getReceiverMsisdn(), " ", "Not able to calculate the receiver talk values getting Exception=" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
    }

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
    	StringBuilder loggerValue= new StringBuilder();
    	loggerValue.append("Entered with p_transferVO:");
    	loggerValue.append( p_transferVO.getCardGroupSetID());
        final String methodName = "getCardGroupDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
        ArrayList cardGroupDetailsVOList = null;
        try {
            final CardGroupDAO cardGroupDAO = new CardGroupDAO();
            cardGroupDetailsVOList = cardGroupDAO.loadCardGroupSlab(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getTransferDateTime());
        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getCardGroupSetID(), "", "", "Exception while calculating talk time for users:" + bex.getMessage());
            throw bex;
        } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getCardGroupSetID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.NO_SLAB_FOR_CARD_GROUP_SETID,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting ");
        }
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
        final String methodName = "getCardGroupDetails";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered with p_transferVO:");
    	loggerValue.append(p_transferVO.getCardGroupSetID());
        if (_log.isDebugEnabled()) {
            _log.debug("getCardGroupDetails",loggerValue);
        }
        try {
            final CardGroupDAO cardGroupDAO = new CardGroupDAO();
            ArrayList cardGroupDetailsVOList = null;
            CardGroupDetailsVO cardGroupDetailsVO = null;
            cardGroupDetailsVOList = cardGroupDAO.loadCardGroupSlab(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getTransferDateTime());
            final Iterator itr = cardGroupDetailsVOList.iterator();
            while (itr.hasNext()) {
                cardGroupDetailsVO = new CardGroupDetailsVO();
                BeanUtils.copyProperties(cardGroupDetailsVO, (CardGroupDetailsVO) itr.next());
                p_cardGroupDetailsVOList.add(cardGroupDetailsVO);

            }

        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, bex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getCardGroupSetID(), "", "", "Exception while calculating talk time for users:" + bex.getMessage());
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
    		loggerValue.setLength(0);
        	loggerValue.append("Exception " );
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getCardGroupSetID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.NO_SLAB_FOR_CARD_GROUP_SETID,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting ");
        }
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
        final String methodName = "setBonusAccountDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug("setBonusAccountDetails", "Entered");
        }
        final ArrayList list = p_cardGroupVO.getBonusAccList();
        BonusAccountDetailsVO bundleVO = null;
        BonusAccountDetailsVO selectorAccBundleVO = null;
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        String serviceSelectorKey = null;
        StringBuilder serviceSelectorKeyBuilder=new StringBuilder();
        String receiverBonusID = null;
        String bundleTypes = "";
        String bonusIds = "";
        String bonusValuesStr = "";
        String bonusValidityStr = "";
        double bonusValueDouble = 0.0;
        double bonusValidityDouble;
        double bonusConvFactor;
        double bonusTalkTimeAfterConversion;
        String bonusValues = null;
        String bonusValidity = null;
        ArrayList bonusBundleList = null;
        BonusBundleDetailVO bonusBundleDetlVO = null;
        // String is set here intentionaly, to avoid null setting
        String bonusNames = "";
        String bonusCodes = "";
        String bonusRates = "";
        try {
        	serviceSelectorKeyBuilder.append(p_transferVO.getServiceType());
        	serviceSelectorKeyBuilder.append("_");
        	serviceSelectorKeyBuilder.append(p_transferVO.getSubService());
            serviceSelectorKey = serviceSelectorKeyBuilder.toString();
            if (isNull(p_transferVO.getServiceType()) && isNull(p_transferVO.getSubService())) {
                serviceSelectorKey = p_cardGroupVO.getServiceTypeSelector();
            }

            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("serviceSelectorKey=");
            	loggerValue.append(serviceSelectorKey);
                _log.debug(methodName,loggerValue);
            }
            serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(serviceSelectorKey);
            receiverBonusID = serviceSelectorMappingVO.getReceiverBundleID();
            receiverBonusID=BTSLUtil.NullToString(receiverBonusID);
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("receiverBonusID=");
            	loggerValue.append(receiverBonusID);
                _log.debug(methodName,loggerValue);
            }
            final BonusBundleCache bonusBundleCache = null;
            bonusBundleList = (ArrayList) bonusBundleCache.getBonusBundleList();
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("bonusBundleList=");
            	loggerValue.append(bonusBundleList);
                _log.debug(methodName,loggerValue);
            }
            for (int i = 0; i < list.size(); i++) {
                bundleVO = (BonusAccountDetailsVO) list.get(i);
                if (bundleVO != null && !BTSLUtil.isNullString(bundleVO.getBundleID())) {
                	int bonusBundlesLists=bonusBundleList.size();
                    for (int j = 0; j < bonusBundlesLists; j++) {
                        bonusBundleDetlVO = (BonusBundleDetailVO) bonusBundleList.get(j);
                        if (bundleVO.getBundleID().equals(bonusBundleDetlVO.getBundleID())) {
                            bundleVO.setRestrictedOnIN(bonusBundleDetlVO.getResINStatus());
                            break;
                        }
                    }
                    if (bundleVO != null && "Y".equals(bundleVO.getRestrictedOnIN())) {
                        // If receiver bonus id and bous id is same, then set
                        // its value in to the selectorBundleVO.
                        if (receiverBonusID.equals(bundleVO.getBundleID())) {
                            selectorAccBundleVO = bundleVO;
                        } else {
                            bonusConvFactor = Double.parseDouble(bundleVO.getMultFactor());
                            // changes done by harsh for bundle type String
                            if (PretupsI.BONUS_BUNDLETYPE_STRING.equalsIgnoreCase(bundleVO.getBundleType())) {
                                bonusValuesStr = bundleVO.getBonusValue();
                            } else {
                                bonusValueDouble = Double.parseDouble(bundleVO.getBonusValue());
                            }
                            bonusTalkTimeAfterConversion = bonusValueDouble / bonusConvFactor;
                            final long requestedAmt = p_transferVO.getRequestedAmount();
                            bonusValueDouble = calculatorI.calculateCardGroupBonus(bundleVO.getType(), bonusTalkTimeAfterConversion, requestedAmt);
                            if (PretupsI.SYSTEM_AMOUNT.equals(bundleVO.getType())) {
                                if (PretupsI.BONUS_BUNDLETYPE_STRING.equalsIgnoreCase(bundleVO.getBundleType())) {
                                    bonusValues = bonusValuesStr;
                                } else {
                                    bonusValues = String.valueOf((PretupsBL.getSystemAmount(bonusValueDouble)));
                                }
                            } else {
                                if (PretupsI.BONUS_BUNDLETYPE_STRING.equalsIgnoreCase(bundleVO.getBundleType())) {
                                    bonusValues = bonusValuesStr;
                                } else {
                                    bonusValues = String.valueOf(bonusValueDouble);
                                }
                            }

                            // get the bonus validity from the
                            // BonusAccountDetailsVO
                            bonusValidity = bundleVO.getBonusValidity();
                            // Get the Restricted bundle on IN
                            // bundleAllowedOnIN=bundleVO.getRestrictedOnIN();
                            // added by harsh
                            if (!PretupsI.BONUS_BUNDLETYPE_STRING.equalsIgnoreCase(bundleVO.getBundleType())) {
                                bonusValueDouble = Double.parseDouble(bonusValues);
                            }
                            bonusValidityDouble = Double.parseDouble(bonusValidity);
                            // Set only those bonus bundles in the VO, if bundle
                            // is not restriced on IN or it value and validity
                            // is not zero.
                            // if(bundleAllowedOnIN.equals("Y") &&
                            // (bonusValueDouble!=0 || bonusValidityDouble!=0))
                            if (Math.abs(bonusValueDouble-0) >EPSILON || Math.abs(bonusValidityDouble-0) >EPSILON) {
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

            // Calculate the selector bonus
            if (selectorAccBundleVO != null && "Y".equals(selectorAccBundleVO.getRestrictedOnIN())) {
                bonusConvFactor = Double.parseDouble(selectorAccBundleVO.getMultFactor());
                if (!PretupsI.BONUS_BUNDLETYPE_STRING.equalsIgnoreCase(bundleVO.getBundleType())) {
                    bonusValueDouble = Double.parseDouble(selectorAccBundleVO.getBonusValue());
                }
                bonusTalkTimeAfterConversion = bonusValueDouble / bonusConvFactor;

                // set the calculate selector bonus rate in the card group
                // detail vo.
                // p_cardGroupVO.setBonusTalkTimeRate(Double.parseDouble(PretupsBL.getDisplayAmount((long)bonusTalkTimeAfterConversion)));
                if (PretupsI.SYSTEM_AMOUNT.equals(selectorAccBundleVO.getType())) {
                    p_cardGroupVO.setBonusTalkTimeRate(PretupsBL.getSystemAmount(bonusTalkTimeAfterConversion));
                } else {
                    p_cardGroupVO.setBonusTalkTimeRate(bonusTalkTimeAfterConversion);
                }
                p_cardGroupVO.setBonusTalkTimeType(selectorAccBundleVO.getType());
                p_cardGroupVO.setBonusTalkTimeValidity(selectorAccBundleVO.getBonusValidity());
                p_cardGroupVO.setBonusTalkTimeConvFactor(Double.parseDouble(selectorAccBundleVO.getMultFactor()));
                p_cardGroupVO.setBonusTalkTimeBundleType(selectorAccBundleVO.getBundleType());
            }
            // remove the last pipe before setting it in to the transfer vo.
            if (!BTSLUtil.isNullString(bonusIds)) {
                bonusIds = bonusIds.substring(0, bonusIds.length() - 1);
            }

            if (!BTSLUtil.isNullString(bonusNames)) {
                bonusNames = bonusNames.substring(0, bonusNames.length() - 1);
            }

            if (!BTSLUtil.isNullString(bonusCodes)) {
                bonusCodes = bonusCodes.substring(0, bonusCodes.length() - 1);
            }

            if (!BTSLUtil.isNullString(bonusRates)) {
                bonusRates = bonusRates.substring(0, bonusRates.length() - 1);
            }

            if (!BTSLUtil.isNullString(bundleTypes)) {
                bundleTypes = bundleTypes.substring(0, bundleTypes.length() - 1);
            }

            if (!BTSLUtil.isNullString(bonusValuesStr)) {
                bonusValuesStr = bonusValuesStr.substring(0, bonusValuesStr.length() - 1);
            }

            if (!BTSLUtil.isNullString(bonusValidityStr)) {
                bonusValidityStr = bonusValidityStr.substring(0, bonusValidityStr.length() - 1);
            }

            // Set the bonus bundles in to the transfer vo.
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectorAccBundleVO=" + selectorAccBundleVO);
            }
            if (selectorAccBundleVO != null) {
                p_transferVO.setSelectorBundleId(selectorAccBundleVO.getBundleID());
                p_transferVO.setSelectorBundleType(selectorAccBundleVO.getBundleType());
                // p_transferVO.setBonusTalkTimeValue((long) Double.parseDouble(selectorAccBundleVO.getBonusValue()));
                p_transferVO.setBonusTalkTimeValue( BTSLUtil.parseDoubleToLong(Double.parseDouble(selectorAccBundleVO.getBonusValue())) );
                p_transferVO.setBonusBundleIdS(bonusIds);
                p_transferVO.setBonusBundleTypes(bundleTypes);
                p_transferVO.setBonusBundleValues(bonusValuesStr);
                p_transferVO.setBonusBundleValidities(bonusValidityStr);
                p_transferVO.setBonusBundleNames(bonusNames);
                p_transferVO.setBonusBundleCode(bonusCodes);
                p_transferVO.setBonusBundleRate(bonusRates);

                final int creditbonusvalidity = Integer.parseInt(selectorAccBundleVO.getBonusValidity());
                p_transferVO.setReceiverCreditBonusValidity(creditbonusvalidity);
            } else {
                p_cardGroupVO.setBonusTalkTimeType(PretupsI.SYSTEM_AMOUNT);
                p_cardGroupVO.setBonusTalkTimeValidity("0");
                p_cardGroupVO.setBonusTalkTimeConvFactor(Double.parseDouble("1"));
            }
        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
    }

    public static boolean isNull(String p_string) {
        _log.debug("isNull", "p_string=" + p_string);
        boolean isNull = false;
        if (BTSLUtil.isNullString(p_string) || "null".equals(p_string)) {
            isNull = true;
        }
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
        final String methodName = "calculateCardGroupDetailsP2PCreditCardransfer";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID=");
    	loggerValue.append(p_transferVO.getRequestID());
		loggerValue.append(" Sub Service=");
    	loggerValue.append(p_transferVO.getSubService());
    	loggerValue.append("p_checkMultipleOf=");
    	loggerValue.append(p_checkMultipleOf);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
        try {
            final CardGroupDetailsVO cardGroupDetailsVO = loadCardGroupDetails(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getRequestedAmount(), p_transferVO
                .getTransferDateTime());

            // added for card group slab suspend/resume
            if (cardGroupDetailsVO.getStatus().equals(PretupsI.SUSPEND)) {
                throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_SLAB_SUSPENDED);
            }

            // 100 because all requets should go through if multiple of is 1.
            if (p_checkMultipleOf && cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % cardGroupDetailsVO.getMultipleOf() != 0) {
                    throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL
                        .getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()) }, null);
                }
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
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getTransferID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
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
        final String methodName = "calculateSOSCardGroupDetails";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID=");
    	loggerValue.append(p_transferVO.getRequestID());
		loggerValue.append(" Sub Service=");
    	loggerValue.append(p_transferVO.getSubService());
		loggerValue.append("p_checkMultipleOf=");
    	loggerValue.append(p_checkMultipleOf);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
        try {
            final CardGroupDetailsVO cardGroupDetailsVO = loadCardGroupDetails(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getRequestedAmount(), p_transferVO
                .getTransferDateTime());
            if (cardGroupDetailsVO.getStatus().equals(PretupsI.SUSPEND)) {
                throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_SLAB_SUSPENDED);
            }
            // cardGroupDetailsVO.setBonus1(Double.parseDouble(PretupsBL.getDisplayAmount((long) cardGroupDetailsVO.getBonus1())));
            // cardGroupDetailsVO.setBonus2(Double.parseDouble(PretupsBL.getDisplayAmount((long) cardGroupDetailsVO.getBonus2())));
            cardGroupDetailsVO.setBonus1(Double.parseDouble(PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getBonus1()) )));
            cardGroupDetailsVO.setBonus2(Double.parseDouble(PretupsBL.getDisplayAmount( BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getBonus2()) )));
            
            if (p_checkMultipleOf && cardGroupDetailsVO.getMultipleOf() != PretupsBL.getSystemAmount(1) && cardGroupDetailsVO.getMultipleOf() != 0) {
                if (p_transferVO.getRequestedAmount() % cardGroupDetailsVO.getMultipleOf() != 0) {
                    throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE, 0, new String[] { PretupsBL
                        .getDisplayAmount(p_transferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()) }, null);
                }
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

            final long requestedValue = p_transferVO.getRequestedAmount();
            final double tax1Rate = cardGroupDetailsVO.getSenderTax1Rate();
            final double tax2Rate = cardGroupDetailsVO.getSenderTax2Rate();
            final double accessFeeRate = cardGroupDetailsVO.getSenderAccessFeeRate();

            final double bonusTalkTimeRate = cardGroupDetailsVO.getBonusTalkTimeRate();
            final double bonus1 = cardGroupDetailsVO.getBonus1();
            final double bonus2 = cardGroupDetailsVO.getBonus2();
            final long bonus1validityP2p = cardGroupDetailsVO.getBonus1validity();
            final long bonus2validityP2p = cardGroupDetailsVO.getBonus2validity();
            final long creditbonusvalidityP2p = cardGroupDetailsVO.getBonusTalktimevalidity();

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

            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Before Setting RequestedValue=");
            	loggerValue.append(requestedValue);
        		loggerValue.append(" Access fee Type=");
            	loggerValue.append(cardGroupDetailsVO.getSenderAccessFeeType());
            	loggerValue.append(" Access fee Rate=");
            	loggerValue.append(accessFeeRate);
        		loggerValue.append(" senderTax1 Type=");
            	loggerValue.append(cardGroupDetailsVO.getSenderTax1Type());
            	loggerValue.append(" Sender Tax 1 Rate=");
            	loggerValue.append(tax1Rate);
        		loggerValue.append(" senderTax2 Type=");
            	loggerValue.append(cardGroupDetailsVO.getSenderTax2Type());
            	loggerValue.append(" Sender Tax 2 Rate=");
            	loggerValue.append(tax2Rate);
                _log.debug(methodName,loggerValue);
            }

            calculatedAccessFee = calculatorI.calculateAccessFee(accessFeeRate, cardGroupDetailsVO.getSenderAccessFeeType(), requestedValue, cardGroupDetailsVO
                .getMinSenderAccessFee(), cardGroupDetailsVO.getMaxSenderAccessFee());
            p_transferVO.setSenderAccessFeeRate(accessFeeRate);
            p_transferVO.setSenderAccessFeeType(cardGroupDetailsVO.getSenderAccessFeeType());
            p_transferVO.setSenderAccessFee(calculatedAccessFee);

            calculatedTax1Value = calculatorI.calculateCardGroupTax1(cardGroupDetailsVO.getSenderTax1Type(), tax1Rate, requestedValue);
            p_transferVO.setSenderTax1Type(cardGroupDetailsVO.getSenderTax1Type());
            p_transferVO.setSenderTax1Rate(tax1Rate);
            p_transferVO.setSenderTax1Value(calculatedTax1Value);

            if (cardGroupDetailsVO.getSenderTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
                // calculatedTax2Value = (long) tax2Rate;
                calculatedTax2Value =BTSLUtil.parseDoubleToLong(tax2Rate);
            } else // If percentage
            {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(cardGroupDetailsVO.getSenderTax2Type(), tax2Rate, calculatedTax1Value);
                } else {
                    calculatedTax2Value = calculatorI.calculateCardGroupTax2(cardGroupDetailsVO.getSenderTax2Type(), tax2Rate, requestedValue);
                }
            }
            p_transferVO.setSenderTax2Type(cardGroupDetailsVO.getSenderTax2Type());
            p_transferVO.setSenderTax2Rate(tax2Rate);
            p_transferVO.setSenderTax2Value(calculatedTax2Value);

            final boolean isUpfrontServiceTaxApplicable = ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_ST_DEDUCT_UPFRONT, p_transferVO
                .getNetworkCode())).booleanValue();
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
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("After Setting transferValue=" );
            	loggerValue.append(transferValue);
        		loggerValue.append(" calculatedAccessFee=" );
            	loggerValue.append(calculatedAccessFee);
            	loggerValue.append(" Access fee Type=");
            	loggerValue.append(cardGroupDetailsVO.getSenderAccessFeeType());
        		loggerValue.append(" calculatedTax1Value=");
            	loggerValue.append(calculatedTax1Value);
            	loggerValue.append(" calculatedTax2Value=");
            	loggerValue.append(calculatedTax2Value);
        		loggerValue.append(", Settlement Value");
            	loggerValue.append(p_transferVO.getSenderTransferValue());
                _log.debug(
                    methodName,loggerValue);
            }
        } catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getTransferID(), "", "", "Exception while calculating SOS recharge value" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateSOSCardGroupDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting...");
        }
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
        final String methodName = "calculateCardGroupSlab";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered with p_transferVO and p_cardGroupDetailsVO with Transfer ID=");
    	loggerValue.append(p_transferVO.getRequestID());
		loggerValue.append(" Sub Service=");
    	loggerValue.append(p_transferVO.getSubService());
    	loggerValue.append("p_checkMultipleOf=");
    	loggerValue.append(p_checkMultipleOf);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, loggerValue);
        }
        try {
            final CardGroupDetailsVO cardGroupDetailsVO = loadCardGroupSlab(p_con, p_transferVO.getCardGroupSetID(), p_transferVO.getTransferDateTime());
            // added for card group slab suspend/resume
            if (PretupsI.SUSPEND.equals(cardGroupDetailsVO.getStatus())) {
                throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.CARD_GROUP_SLAB_SUSPENDED);
            }
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
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[" + methodName + "]", p_transferVO
                .getTransferID(), "", "", "Exception while calculating talk time for users:" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
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
        final String methodName = "loadCardGroupSlab";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_cardGroupSetID=" + p_cardGroupSetID + " p_applicableDate=" + p_applicableDate);
        }
        CardGroupDetailsVO cardGroupDetailsVO = null;
        try {
            cardGroupDetailsVO = _cardGroupDAO.loadCardGroupMinMax(p_con, p_cardGroupSetID, p_applicableDate);
        } catch (BTSLBaseException bex) {
            _log.error(methodName, "BTSLBaseException " + bex.getMessage());
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("loadCardGroupSlab", "Exception " + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
        return cardGroupDetailsVO;
    }
}
