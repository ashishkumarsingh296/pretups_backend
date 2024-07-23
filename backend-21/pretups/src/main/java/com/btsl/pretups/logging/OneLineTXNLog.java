package com.btsl.pretups.logging;

/*
 * @(#)TransactionLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Nilesh Kumar 14/05/2012 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the transaction log
 */

import java.util.ArrayList;
import java.util.Date;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class OneLineTXNLog {
    private static final Log _logC2S = LogFactory.getFactory().getInstance(OneLineTXNLog.class.getName() + "C2S");
    private static final Log _logP2P = LogFactory.getFactory().getInstance(OneLineTXNLog.class.getName() + "P2P");
    private static final Log _logCHNL = LogFactory.getFactory().getInstance(OneLineTXNLog.class.getName() + "CHNL");
	private static final Log _logVMS= LogFactory.getFactory().getInstance(OneLineTXNLog.class.getName()+"VMS");
    private static final Log _log = LogFactory.getLog(OneLineTXNLog.class.getName());
	//Added for Handling of separator for P2P, C2S & CHNL TXNS 
	private static String p2PTxnLogSep = "|";
	private static String c2STxnLogSep = "|";
	private static String cHNLTxnLogSep = "|";
	private static Boolean isAliasToBeEncrypted = false;
	private static Boolean isMultipleWalletApply = false;
	private static Boolean isDPAllowed = false;
	private static Boolean isUserProductMultipleWallet = false;
	private static String defaultWallet = "SALE";
	
	static{
          try{
        	  String p2pTxnLogSep = Constants.getProperty("P2P_TXN_LOG_SEPARATOR");
        	  if(BTSLUtil.isNullString(p2pTxnLogSep) || "null".equalsIgnoreCase(p2pTxnLogSep))
                	p2PTxnLogSep = "|";
              else 
            	  p2PTxnLogSep = p2pTxnLogSep.trim();
              
        	  String c2sTxnLogSep = Constants.getProperty("C2S_TXN_LOG_SEPARATOR");
        	  if(BTSLUtil.isNullString(c2sTxnLogSep) || "null".equalsIgnoreCase(c2sTxnLogSep))
            	  c2STxnLogSep = "|";
              else 
            	  c2STxnLogSep = c2sTxnLogSep.trim();
        	  
        	  String chnlTxnLogSep = Constants.getProperty("CHNL_TXN_LOG_SEPARATOR");
        	  if(BTSLUtil.isNullString(chnlTxnLogSep) || "null".equalsIgnoreCase(chnlTxnLogSep))
            	  cHNLTxnLogSep = "|";
              else 
            	  cHNLTxnLogSep = chnlTxnLogSep.trim();
          }catch(Exception e)
          {
                _log.errorTrace("static",e);
          }
          isAliasToBeEncrypted = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALIAS_TO_BE_ENCRYPTED);
          isMultipleWalletApply = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
          isDPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_ALLOWED);
          isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
          defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
     }

    	 /**
    	 * ensures no instantiation
    	 */
    	private OneLineTXNLog(){
    		
    	}
    
    	public static void log(C2STransferVO c2sTransferVO, TransferItemVO senderTransferItemVO, TransferItemVO receiverTransferItemVO) {
        final String methodName = "log";
        try {
            String fullTDR = "Y";
            try {

                if (!BTSLUtil.isNullString(Constants.getProperty("CHANNEL_USER_TXN_FULL_TDR_ALLOWED")) && Constants.getProperty("CHANNEL_USER_TXN_FULL_TDR_ALLOWED").equalsIgnoreCase(PretupsI.NO)) {
                    fullTDR = "N";
                } else {
                    fullTDR = "Y";
                }

            } catch (Exception e) {
                fullTDR = "Y";
                _log.errorTrace(methodName, e);
            }

            String userSpecificTDR = "N";
            try {

                if (!BTSLUtil.isNullString(Constants.getProperty("CHANNEL_USER_TXN_USR_SPEC_TDR_ALLOWED")) && Constants.getProperty("CHANNEL_USER_TXN_USR_SPEC_TDR_ALLOWED").equalsIgnoreCase(PretupsI.YES)) {
                    userSpecificTDR = "Y";
                } else {
                    userSpecificTDR = "N";
                }

            } catch (Exception e) {
                userSpecificTDR = "N";
                _log.errorTrace(methodName, e);
            }

            if (fullTDR.equalsIgnoreCase(PretupsI.YES)) {

                ChannelUserVO channelUserVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
                C2STransferItemVO creditBackVO = null;
                C2STransferItemVO reconcileVO = null;
				//Added for EVD..
				if(c2sTransferVO.getTransferItemList()!=null) {
	                for (int l = 0, m = c2sTransferVO.getTransferItemList().size(); l < m; l++) {
	                    Object obj = c2sTransferVO.getTransferItemList().get(l);
						if(obj instanceof C2STransferItemVO){
		                    C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) obj;
		                    if (c2STransferItemVO.getSNo() > 3 || (c2STransferItemVO.getSNo() == 3 && c2STransferItemVO.getTransferType().equals(PretupsI.TRANSFER_TYPE_RECON))) {
		                        reconcileVO = c2STransferItemVO;
		                    } else if (c2STransferItemVO.getSNo() == 3) {
		                        creditBackVO = c2STransferItemVO;
		                    }
						}
	                }
				}
                StringBuilder strBuff = new StringBuilder();
                strBuff.append(c2STxnLogSep);
                strBuff.append("TID=" ).append(c2sTransferVO.getTransferID() );
                strBuff.append(c2STxnLogSep);
                strBuff.append("TRFDT=" ).append(BTSLDateUtil.getSystemLocaleDate(c2sTransferVO.getTransferDate(), true));//TRANSFER_DATE
                strBuff.append(c2STxnLogSep);
                strBuff.append("TRF_DT_TIME=" ).append(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(c2sTransferVO.getTransferDateTime()) ));//TRANSFER_DATE_TIME
                strBuff.append(c2STxnLogSep);
                strBuff.append("SNW=" ).append(c2sTransferVO.getNetworkCode());
                strBuff.append(c2STxnLogSep);
				strBuff.append("SID=" ).append(c2sTransferVO.getSenderID() );
				strBuff.append(c2STxnLogSep);
				strBuff.append("SCAT=" ).append(channelUserVO.getCategoryCode());
				strBuff.append(c2STxnLogSep);
				strBuff.append("PRD=" ).append(c2sTransferVO.getProductCode());
				strBuff.append(c2STxnLogSep);
				strBuff.append("S_MSISDN=" ).append(c2sTransferVO.getSenderMsisdn() );
				strBuff.append(c2STxnLogSep);
				strBuff.append("RMSISDN=" ).append(c2sTransferVO.getReceiverMsisdn());
				strBuff.append(c2STxnLogSep);
				if(PretupsI.IAT_TRANSACTION_TYPE.equals(c2sTransferVO.getExtCreditIntfceType()))
					strBuff.append("RNW=" ).append(c2sTransferVO.getIatTransferItemVO().getIatRecNWCode());
		        else
		        	strBuff.append("RNW=" ).append(c2sTransferVO.getReceiverNetworkCode());
				strBuff.append(c2STxnLogSep);
				strBuff.append("TRFVAL=" ).append(c2sTransferVO.getRequestedAmount());
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(c2sTransferVO.getErrorCode()))
					strBuff.append("E_CODE=" ).append(c2sTransferVO.getErrorCode());
				else
					strBuff.append("E_CODE=");
				strBuff.append(c2STxnLogSep);
                strBuff.append("REQ_GW_TYP=" ).append(c2sTransferVO.getRequestGatewayType() );
                strBuff.append(c2STxnLogSep);
                strBuff.append("REQ_GW_CODE=" ).append(c2sTransferVO.getRequestGatewayCode() );
                strBuff.append(c2STxnLogSep);
                strBuff.append("REFID=" );
                if(!BTSLUtil.isNullString(c2sTransferVO.getReferenceID()))
                	strBuff.append(c2sTransferVO.getReferenceID() );
                strBuff.append(c2STxnLogSep);
				strBuff.append("SRVC_TYPE=" ).append(c2sTransferVO.getServiceType());
				strBuff.append(c2STxnLogSep);
				strBuff.append("DIFF_APPLICABLE=" ).append(c2sTransferVO.getDifferentialApplicable());
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(c2sTransferVO.getPinSentToMsisdn()))
					strBuff.append("PINTOMSISDN=" ).append(c2sTransferVO.getPinSentToMsisdn());
				else
					strBuff.append("PINTOMSISDN=");
				strBuff.append(c2STxnLogSep);
				strBuff.append("LANG=" ).append(c2sTransferVO.getLanguage());//LANGUAGE
				strBuff.append(c2STxnLogSep);
                strBuff.append("COUNTRY=" ).append(c2sTransferVO.getCountry());//COUNTRY
                strBuff.append(c2STxnLogSep);
                strBuff.append("SKEY=" ).append(c2sTransferVO.getSkey());//SKEY
                strBuff.append(c2STxnLogSep);
                if (c2sTransferVO.getSkeyGenerationTime() == null)
                	strBuff.append("SKEYGENTIME=");
                else 
                	strBuff.append("SKEYGENTIME=" ).append(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(c2sTransferVO.getSkeyGenerationTime())));
                strBuff.append(c2STxnLogSep);
                if(!BTSLUtil.isNullString(c2sTransferVO.getSkeySentToMsisdn())) {
                	strBuff.append("SKEYTOMSISDN=" ).append(c2sTransferVO.getSkeySentToMsisdn());
                } else {
                	strBuff.append("SKEYTOMSISDN=");
                }
                strBuff.append(c2STxnLogSep);
                if(!BTSLUtil.isNullString(c2sTransferVO.getRequestThroughQueue())) {
                	strBuff.append("REQ_THRGH_QUE=" ).append(c2sTransferVO.getRequestThroughQueue());
                } else {
                	strBuff.append("REQ_THRGH_QUE=");
                }
                strBuff.append(c2STxnLogSep);
				if(creditBackVO!=null) {
					strBuff.append("CR_BK_ST=" ).append(creditBackVO.getTransferStatus());
				} else {
					strBuff.append("CR_BK_ST=");
				}
				strBuff.append(c2STxnLogSep);
				strBuff.append("QTY=" ).append(c2sTransferVO.getQuantity());
				strBuff.append(c2STxnLogSep);
				if(reconcileVO!=null)
				{
					strBuff.append("REC_FLG=" ).append(PretupsI.YES);//RECONCILIATION_FLAG
					strBuff.append(c2STxnLogSep);
                    strBuff.append("REC_DT=" ).append(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(c2sTransferVO.getModifiedOn())));//RECONCILIATION_DATE
                    strBuff.append(c2STxnLogSep);
                    strBuff.append("REC_BY=" ).append(c2sTransferVO.getModifiedBy());//RECONCILIATION_BY
                }
				else
				{
					strBuff.append("REC_FLG=");//RECONCILIATION_FLAG
					strBuff.append(c2STxnLogSep);
                    strBuff.append("REC_DT=");//RECONCILIATION_DATE
                    strBuff.append(c2STxnLogSep);
                    strBuff.append("REC_BY=");
                }
				strBuff.append(c2STxnLogSep);
                strBuff.append("CREATEDON=" ).append(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(c2sTransferVO.getCreatedOn())));//CREATED_ON
                strBuff.append(c2STxnLogSep);
                strBuff.append("CREATEDBY=" ).append(c2sTransferVO.getCreatedBy());//CREATED_BY
                strBuff.append(c2STxnLogSep);
                strBuff.append("MODIFIEDON=" ).append(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(c2sTransferVO.getModifiedOn())));//MODIFIED_ON
                strBuff.append(c2STxnLogSep);
                strBuff.append("MODIFIEDBY=" ).append(c2sTransferVO.getModifiedBy());//MODIFIED_BY
                strBuff.append(c2STxnLogSep);
                strBuff.append("TRF_ST=" ).append(c2sTransferVO.getTransferStatus());
                strBuff.append(c2STxnLogSep);
				strBuff.append("CDGRP_SETID=" ).append(c2sTransferVO.getCardGroupSetID());
				strBuff.append(c2STxnLogSep);
				strBuff.append("VER=" ).append(c2sTransferVO.getVersion());
				strBuff.append(c2STxnLogSep);
				strBuff.append("CDGRP_ID=" ).append(c2sTransferVO.getCardGroupID());
				strBuff.append(c2STxnLogSep);
				strBuff.append("S_TRFVAL=" ).append(c2sTransferVO.getSenderTransferValue());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_ACCESSFEE=" ).append(c2sTransferVO.getReceiverAccessFee());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_TX1TYP=" ).append(c2sTransferVO.getReceiverTax1Type());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_TX1RT=" ).append(c2sTransferVO.getReceiverTax1Rate());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_TX1VAL=" ).append(c2sTransferVO.getReceiverTax1Value());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_TX2TYP=" ).append(c2sTransferVO.getReceiverTax2Type());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_TX2RT=" ).append(c2sTransferVO.getReceiverTax2Rate());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_TX2VAL=" ).append(c2sTransferVO.getReceiverTax2Value());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_VAL=" ).append(c2sTransferVO.getReceiverValidity());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_TRFVAL=" ).append(c2sTransferVO.getReceiverTransferValue());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_BNSVAL=" ).append(c2sTransferVO.getReceiverBonusValue());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_GRACE=" ).append(c2sTransferVO.getReceiverGracePeriod());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_BNSVLDTY=" ).append(c2sTransferVO.getReceiverBonusValidity());
				strBuff.append(c2STxnLogSep);
				strBuff.append("CDGRP_CODE=" ).append(c2sTransferVO.getCardGroupCode());
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_VLDTY_PD_TYP=" ).append(c2sTransferVO.getReceiverValPeriodType());
				strBuff.append(c2STxnLogSep);
			    strBuff.append("TMP_TRFID=" ).append(channelUserVO.getUserPhoneVO().getTempTransferID());//TEMP_TRANSFER_ID
			    strBuff.append(c2STxnLogSep);
				strBuff.append("TRF_PROFID=" ).append(channelUserVO.getTransferProfileID());
				strBuff.append(c2STxnLogSep);
				strBuff.append("COM_PROFID=" ).append(channelUserVO.getCommissionProfileSetID());
				strBuff.append(c2STxnLogSep);
				strBuff.append("DIFF_GVN=" ).append(c2sTransferVO.getDifferentialGiven());
				strBuff.append(c2STxnLogSep);
				strBuff.append("GPH_DM_CD=" ).append(c2sTransferVO.getGrphDomainCode());
				strBuff.append(c2STxnLogSep);
				strBuff.append("SRC_TYP=" ).append(c2sTransferVO.getSourceType());//SOURCE_TYPE
				strBuff.append(c2STxnLogSep);
                strBuff.append("SUBSRVC=" ).append(c2sTransferVO.getSubService() );
                strBuff.append(c2STxnLogSep);
                strBuff.append("START_TIME=" ).append(c2sTransferVO.getRequestStartTime());
                strBuff.append(c2STxnLogSep);
				strBuff.append("END_TIME=" ).append(System.currentTimeMillis());
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(c2sTransferVO.getSerialNumber())) {
					strBuff.append("SNO=" ).append(c2sTransferVO.getSerialNumber());
				} else {
					strBuff.append("SNO=");
				}
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(c2sTransferVO.getExtCreditIntfceType())){
					strBuff.append("EX_CR_ITYP=" ).append(c2sTransferVO.getExtCreditIntfceType());
				} else {
					strBuff.append("EX_CR_ITYP=");
				}
				strBuff.append(c2STxnLogSep);
				strBuff.append("BNS_DET=" ).append(c2sTransferVO.getBonusSummarySting());
				strBuff.append(c2STxnLogSep);
				strBuff.append("ACTUSRID=" ).append(c2sTransferVO.getActiveUserId());
				strBuff.append(c2STxnLogSep);
				strBuff.append("TXNTYP=" ).append(receiverTransferItemVO.getTransferType());
				strBuff.append(c2STxnLogSep);
				strBuff.append("VAL_ST=" ).append(receiverTransferItemVO.getValidationStatus());
				strBuff.append(c2STxnLogSep);
				strBuff.append("INT_TYP=" ).append(receiverTransferItemVO.getInterfaceType());
				strBuff.append(c2STxnLogSep);
				strBuff.append("INT_ID=" ).append(receiverTransferItemVO.getInterfaceID());
				strBuff.append(c2STxnLogSep);
				strBuff.append("IN_RESP_CD=" ).append(receiverTransferItemVO.getInterfaceResponseCode());
				strBuff.append(c2STxnLogSep);
				strBuff.append("IN_REFID=" ).append(receiverTransferItemVO.getInterfaceReferenceID());
				strBuff.append(c2STxnLogSep);
				strBuff.append("SUBTYP=" ).append(receiverTransferItemVO.getSubscriberType());
				strBuff.append(c2STxnLogSep);
				strBuff.append("SRVCCLS_CODE=" ).append(receiverTransferItemVO.getServiceClassCode());
				strBuff.append(c2STxnLogSep);
				strBuff.append("MSISDN_PREEXP=" ).append(BTSLDateUtil.getSystemLocaleDate(receiverTransferItemVO.getPreviousExpiry(), true));
				strBuff.append(c2STxnLogSep);
				if(receiverTransferItemVO.getNewExpiry()!=null){
					strBuff.append("MSISDN_NEWEXP=" ).append(BTSLDateUtil.getSystemLocaleDate(receiverTransferItemVO.getNewExpiry(), true));
				} else {
					strBuff.append("MSISDN_NEWEXP=");
				}
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(receiverTransferItemVO.getFirstCall())) {
					strBuff.append("FRST_CALL=" ).append(receiverTransferItemVO.getFirstCall());
				} else {
					strBuff.append("FRST_CALL=");
				}
				strBuff.append(c2STxnLogSep);
				strBuff.append("PRFX_ID=" ).append(receiverTransferItemVO.getPrefixID());
				strBuff.append(c2STxnLogSep);
				strBuff.append("SRVC_CLS_ID=" ).append(receiverTransferItemVO.getServiceClass());
				strBuff.append(c2STxnLogSep);
				strBuff.append("PROTO_ST=" ).append(receiverTransferItemVO.getProtocolStatus());
				strBuff.append(c2STxnLogSep);
				strBuff.append("ACC_ST=" ).append(receiverTransferItemVO.getAccountStatus());
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(receiverTransferItemVO.getTransferType2())){
					strBuff.append("ADJ_DR_TXNTYPE=" ).append(receiverTransferItemVO.getTransferType2());
				} else {
					strBuff.append("ADJ_DR_TXNTYPE=");
				}
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(receiverTransferItemVO.getInterfaceReferenceID2()))  {  					
					strBuff.append("ADJ_DR_TXNID=" ).append(receiverTransferItemVO.getInterfaceReferenceID2());
				} else {
					strBuff.append("ADJ_DR_TXNID=");
				}
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(receiverTransferItemVO.getUpdateStatus2())){
					strBuff.append("ADJ_DR_UPST=" ).append(receiverTransferItemVO.getUpdateStatus2());
				} else {
					strBuff.append("ADJ_DR_UPST=");
				}
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(receiverTransferItemVO.getTransferType1())){
					strBuff.append("ADJ_CR_TXNTYP=" ).append(receiverTransferItemVO.getTransferType1());
				} else {
					strBuff.append("ADJ_CR_TXNTYP=");
				}
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(receiverTransferItemVO.getInterfaceReferenceID1())){
					strBuff.append("ADJ_CR_TXNID=" ).append(receiverTransferItemVO.getInterfaceReferenceID1());
				} else {
					strBuff.append("ADJ_CR_TXNID=");
				}
				strBuff.append(c2STxnLogSep);
				if(!BTSLUtil.isNullString(receiverTransferItemVO.getUpdateStatus1())){
					strBuff.append("ADJ_CR_UPST=" ).append(receiverTransferItemVO.getUpdateStatus1());
				} else {
					strBuff.append("ADJ_CR_UPST=");
				}
				strBuff.append(c2STxnLogSep);
				strBuff.append("ADJVAL=" ).append(receiverTransferItemVO.getAdjustValue());
				strBuff.append(c2STxnLogSep);
				if(senderTransferItemVO!=null) {
					strBuff.append("S_PREBAL=" ).append(senderTransferItemVO.getPreviousBalance());
				} else {
					strBuff.append("S_PREBAL=");
				}
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_PREBAL=" );
				strBuff.append(receiverTransferItemVO.getPreviousBalance());
				strBuff.append(c2STxnLogSep);
				strBuff.append("S_POSTBAL=");
				if(senderTransferItemVO!=null){
					strBuff.append(senderTransferItemVO.getPostBalance());
				}
				strBuff.append(c2STxnLogSep);
				strBuff.append("R_POSTBAL=");
				strBuff.append(receiverTransferItemVO.getPostBalance());
				strBuff.append(c2STxnLogSep);
				if(creditBackVO!=null)
				{
					strBuff.append("S_CR_BK_PREBAL=" ).append(creditBackVO.getPreviousBalance());
					strBuff.append(c2STxnLogSep);
					strBuff.append("S_CR_BK_POSTBAL=" ).append(creditBackVO.getPostBalance());
                }
				else
				{
					strBuff.append("S_CR_BK_PREBAL=");
					strBuff.append(c2STxnLogSep);
					strBuff.append("S_CR_BK_POSTBAL=");
                }
				strBuff.append(c2STxnLogSep);
				if(reconcileVO!=null)
				{
					strBuff.append("S_CR_SL_PREBAL=" ).append(reconcileVO.getPreviousBalance());
					strBuff.append(c2STxnLogSep);
					strBuff.append("S_CR_SL_POSTBAL=" ).append(reconcileVO.getPostBalance());    					
                }
				else
				{
					strBuff.append("S_CR_SL_PREBAL=");
					strBuff.append(c2STxnLogSep);
					strBuff.append("S_CR_SL_POSTBAL=");
                }
				strBuff.append(c2STxnLogSep);
				if(senderTransferItemVO!=null){
					strBuff.append("DEB_ST=" ).append(senderTransferItemVO.getTransferStatus());
				} else {
					strBuff.append("DEB_ST=");
				}
				strBuff.append(c2STxnLogSep);
				if(reconcileVO!=null){
					strBuff.append("REC_ENTYP=" ).append(reconcileVO.getEntryType());
				} else {
					strBuff.append("REC_ENTYP=");
				}
				strBuff.append(c2STxnLogSep);
				strBuff.append("CRED_ST=" ).append(receiverTransferItemVO.getTransferStatus());
				strBuff.append(c2STxnLogSep);
				
				if(!BTSLUtil.isNullString(c2sTransferVO.getSubscriberSID())){
					strBuff.append("SUBS_SID="+c2sTransferVO.getSubscriberSID());
						try{
							if(isAliasToBeEncrypted){
				              	  if(!BTSLUtil.isNullString(c2sTransferVO.getSubscriberSID()) && !c2sTransferVO.getSubscriberSID().matches("[0-9]+"))
				              		c2sTransferVO.setSubscriberSID(new CryptoUtil().decrypt(c2sTransferVO.getSubscriberSID(),Constants.KEY));
							}
						}catch(Exception e){
							  _log.errorTrace(methodName, e);
						}
				}
				else
					strBuff.append("SUBS_SID=");
				
				
				/*if(!BTSLUtil.isNullString(c2sTransferVO.getSubscriberSID())){
					strBuff.append("SUBS_SID=" ).append(c2sTransferVO.getSubscriberSID());
				} else {
					strBuff.append("SUBS_SID=");
				}*/
				strBuff.append(c2STxnLogSep);
				if(reconcileVO!=null){
					strBuff.append("REC_ST=" ).append(reconcileVO.getTransferStatus());	
				} else {
					strBuff.append("REC_ST=");
				}
				strBuff.append(c2STxnLogSep);
				strBuff.append("CELLID=");
				if(!BTSLUtil.isNullString(c2sTransferVO.getCellId())) {
					strBuff.append(c2sTransferVO.getCellId());
                }
				strBuff.append(c2STxnLogSep);
				strBuff.append("SWITCHID=");
				if(!BTSLUtil.isNullString(c2sTransferVO.getSwitchId())) {
					strBuff.append(c2sTransferVO.getSwitchId());
                }
				strBuff.append(c2STxnLogSep);
				strBuff.append("Additional Com=" ).append(c2sTransferVO.getTotalCommission());

                _logC2S.info("", strBuff.toString());

            }
            if (userSpecificTDR.equalsIgnoreCase(PretupsI.YES)) {
                ChannelUserTxnLog.log(c2sTransferVO, senderTransferItemVO, receiverTransferItemVO);
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
    }

    /**
     * @param p2pTransferVO
     * @param senderTransferItemVO
     * @param receiverTransferItemVO
     */
    public static void log(P2PTransferVO p2pTransferVO, TransferItemVO senderTransferItemVO, TransferItemVO receiverTransferItemVO) {
        final String methodName = "log";
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append(p2PTxnLogSep);
            strBuff.append("TID=" ).append(p2pTransferVO.getTransferID());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SID=" ).append(p2pTransferVO.getSenderID());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("GWTYP=" ).append(p2pTransferVO.getRequestGatewayType());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("GW=" ).append(p2pTransferVO.getRequestGatewayCode());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SMNO=" ).append(p2pTransferVO.getSenderMsisdn());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SNW=" ).append(p2pTransferVO.getNetworkCode());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("PRD=" ).append(p2pTransferVO.getProductCode());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RMNO=" ).append(p2pTransferVO.getReceiverMsisdn());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RNW=" ).append(p2pTransferVO.getReceiverNetworkCode());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("TRFVAL=" ).append(p2pTransferVO.getTransferValue());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("TRFST=" ).append(p2pTransferVO.getTransferStatus());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("E=" ).append(p2pTransferVO.getErrorCode());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SVALST=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getValidationStatus() :"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SINSTV=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getValidationStatus() :"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SINSTU=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getUpdateStatus() :"");
            strBuff.append(p2PTxnLogSep);
            if (BTSLUtil.isNullString(senderTransferItemVO != null ? senderTransferItemVO.getUpdateStatus() :"")) {
            	strBuff.append("SINRESPCDU=");
            	strBuff.append(p2PTxnLogSep);
                strBuff.append("SINIDU=");
                strBuff.append(p2PTxnLogSep);
                strBuff.append("SPROTOCOLSTU=");
            } else {
	        	 strBuff.append("SINRESPCDU=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getInterfaceResponseCode() :"");
	        	 strBuff.append(p2PTxnLogSep);
	             strBuff.append("SINIDU=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getInterfaceID():"");
	             strBuff.append(p2PTxnLogSep);
	             strBuff.append("SPROTOCOLSTU=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getProtocolStatus() :"");
            }
            strBuff.append(p2PTxnLogSep);
            strBuff.append("STRFST=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getTransferStatus():"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RINSTV=" ).append(senderTransferItemVO != null ? receiverTransferItemVO.getValidationStatus():"");
            strBuff.append(p2PTxnLogSep);                    
            strBuff.append("RINSTU=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getUpdateStatus() :"");
            strBuff.append(p2PTxnLogSep);
            if (BTSLUtil.isNullString(receiverTransferItemVO != null ? receiverTransferItemVO.getUpdateStatus() :"")) {
            	strBuff.append("RINRESPCDU=");
            	strBuff.append(p2PTxnLogSep);
                strBuff.append("RINIDU=");
                strBuff.append(p2PTxnLogSep);
                strBuff.append("RPROTOCOLSTU=");
            } else {
            	 strBuff.append("RINRESPCDU=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getInterfaceResponseCode() :"");
            	 strBuff.append(p2PTxnLogSep);
                 strBuff.append("RINIDU=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getInterfaceID() :"");
                 strBuff.append(p2PTxnLogSep);
                 strBuff.append("RPROTOCOLSTU=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getProtocolStatus() :"");
            }
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RINREFID=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getReferenceID():"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RSVCCLSCD=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getServiceClass():"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RINTRFST=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getTransferStatus():"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("REFID=" ).append(p2pTransferVO.getReferenceID());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SRVC=" ).append(p2pTransferVO.getServiceType());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SUB=" ).append(p2pTransferVO.getSubService());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("CRBK=" ).append(p2pTransferVO.getCreditBackStatus());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("QTY=" ).append(p2pTransferVO.getQuantity());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("CDGRP=" ).append(p2pTransferVO.getCardGroupID());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("CDGRPSETID=" ).append(p2pTransferVO.getCardGroupSetID());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("VER=" ).append(p2pTransferVO.getVersion());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("CDGRPCD=" ).append(p2pTransferVO.getCardGroupCode());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SFEE=" ).append(p2pTransferVO.getSenderAccessFee());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("STAX1=" ).append(p2pTransferVO.getSenderTax1Value());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("STAX2=" ).append(p2pTransferVO.getSenderTax2Value());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("STRF=" ).append(p2pTransferVO.getSenderTransferValue());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RFEE=" ).append(p2pTransferVO.getReceiverAccessFee());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RTAX1=" ).append(p2pTransferVO.getReceiverTax1Value());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RTAX2=" ).append(p2pTransferVO.getReceiverTax2Value());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RTRF=" ).append(p2pTransferVO.getReceiverTransferValue());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RVAL=" ).append(p2pTransferVO.getReceiverValidity());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RBONUS=" ).append(p2pTransferVO.getReceiverBonusValue());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RGRC=" ).append(p2pTransferVO.getReceiverGracePeriod());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RBONUSV=" ).append(p2pTransferVO.getReceiverBonusValidity());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SPREBAL=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getPreviousBalance() :"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("SPOSTBAL=" ).append(senderTransferItemVO != null ? senderTransferItemVO.getPostBalance() :"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RPREBAL=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getPreviousBalance():"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("RPOSTBAL=" ).append(receiverTransferItemVO != null ? receiverTransferItemVO.getPostBalance():"");
            strBuff.append(p2PTxnLogSep);
            strBuff.append("TRFCAT=" ).append(p2pTransferVO.getTransferCategory());
            strBuff.append(p2PTxnLogSep);
            strBuff.append("CELLID=" ).append(p2pTransferVO.getCellId());
            strBuff.append(p2PTxnLogSep);
			strBuff.append("SWITCHID=" ).append(p2pTransferVO.getSwitchId());
            strBuff.append(p2PTxnLogSep);
			strBuff.append("TT=" ).append(System.currentTimeMillis() - p2pTransferVO.getRequestStartTime()).append("ms");

            _logP2P.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
    }

    /**
     * @param channelTransferVO
     * @param focBatchItemVO
     */
    public static void log(ChannelTransferVO channelTransferVO, FOCBatchItemsVO focBatchItemVO) {
        final String methodName = "log";
        try {
            ArrayList channelTransferList = channelTransferVO.getChannelTransferitemsVOList();
            ChannelTransferItemsVO transferItemsVO = null;
            Date currentDate = new Date();
            StringBuilder strBuff = new StringBuilder();
    		strBuff.append(cHNLTxnLogSep);
            strBuff.append("TID=" ).append(channelTransferVO.getTransferID() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("NW=" ).append(channelTransferVO.getNetworkCode() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("NW_FOR=" ).append(channelTransferVO.getNetworkCodeFor() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("G_DM_CDE=" ).append(channelTransferVO.getGraphicalDomainCode() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("DM_CDE=" ).append(channelTransferVO.getDomainCode());
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("S_CATCODE=" ).append(channelTransferVO.getCategoryCode() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("S_GRAD=" ).append(BTSLUtil.NullToString(channelTransferVO.getSenderGradeCode()) );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("R_GRAD=" ).append(BTSLUtil.NullToString(channelTransferVO.getReceiverGradeCode() ));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("F_USRID=" ).append(channelTransferVO.getFromUserID() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("T_USRID=" ).append(channelTransferVO.getToUserID() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TRFDATE=" ).append(BTSLDateUtil.getSystemLocaleDate(channelTransferVO.getTransferDate(), true));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("REF_NO=" ).append(BTSLUtil.NullToString(channelTransferVO.getReferenceNum() ));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("EXT_TXN_NO=" ).append(BTSLUtil.NullToString(channelTransferVO.getExternalTxnNum() ));
            strBuff.append(cHNLTxnLogSep);
			if(channelTransferVO.getExternalTxnDate()!=null){
                strBuff.append("EXT_TXN_DT=" ).append(BTSLDateUtil.getSystemLocaleDate(channelTransferVO.getExternalTxnDate(), true));
			} else {
                strBuff.append("EXT_TXN_DT=");
			}
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("COMM_PROF_ID=" ).append(channelTransferVO.getCommProfileSetId());
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("COMM_PROF_VER=" ).append(channelTransferVO.getCommProfileVersion());
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("QTY=" ).append(channelTransferVO.getRequestedQuantity());
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("CHUSR_REM=" ).append(BTSLUtil.NullToString(channelTransferVO.getChannelRemarks()));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("FRST_APPRVL_REM=" ).append(BTSLUtil.NullToString(channelTransferVO.getFirstApprovalRemark()));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("SCND_APPRVL_REM=" ).append(BTSLUtil.NullToString(channelTransferVO.getSecondApprovalRemark()) );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("THRD_APPRVL_REM=" ).append(BTSLUtil.NullToString(channelTransferVO.getThirdApprovalRemark()));
            strBuff.append(cHNLTxnLogSep);
			strBuff.append("FRST_APPRVL_BY=" ).append(BTSLUtil.NullToString(channelTransferVO.getFirstApprovedBy()) );
			strBuff.append(cHNLTxnLogSep);
			if(channelTransferVO.getFirstApprovedOn()!=null){
                strBuff.append("FRST_APPRVL_ON=" ).append(BTSLDateUtil.getLocaleDateTimeFromDate(channelTransferVO.getFirstApprovedOn()));
			} else {
                strBuff.append("FRST_APPRVL_ON=");
			}
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("SCND_APPRVL_BY=" ).append(BTSLUtil.NullToString(channelTransferVO.getSecondApprovedBy()) );
            strBuff.append(cHNLTxnLogSep);
			if(channelTransferVO.getSecondApprovedOn()!=null){
                strBuff.append("SCND_APPRVL_ON=" ).append(BTSLDateUtil.getLocaleDateTimeFromDate(channelTransferVO.getSecondApprovedOn()));
			} else {
                strBuff.append("SCND_APPRVL_ON=");
			}
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("THRD_APPRVL_BY=" ).append(BTSLUtil.NullToString(channelTransferVO.getThirdApprovedBy()));
            strBuff.append(cHNLTxnLogSep);
			if(channelTransferVO.getThirdApprovedOn()!=null){
                strBuff.append("THRD_APPRVL_ON=" ).append(BTSLDateUtil.getLocaleDateTimeFromDate(channelTransferVO.getThirdApprovedOn()));
			} else {
                strBuff.append("THRD_APPRVL_ON=");
			}
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("CNCLD_BY=" ).append(BTSLUtil.NullToString(channelTransferVO.getCanceledBy()) );
            strBuff.append(cHNLTxnLogSep);
			if(channelTransferVO.getCanceledOn()!=null){
                strBuff.append("CNCLD_ON=" ).append(BTSLUtil.NullToString(BTSLDateUtil.getSystemLocaleDate(channelTransferVO.getCanceledOn(), true)));
			} else {
               	strBuff.append("CNCLD_ON=");
			}
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("CREATED_ON=" ).append(BTSLDateUtil.getLocaleDateTimeFromDate(channelTransferVO.getCreatedOn()) );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("CREATED_BY=" ).append(channelTransferVO.getCreatedBy() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("MODIFIED_BY=" ).append(channelTransferVO.getModifiedBy() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("MODIFIED_ON=" ).append(BTSLDateUtil.getLocaleDateTimeFromDate(channelTransferVO.getModifiedOn()) );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("STATUS=" ).append(channelTransferVO.getStatus());
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TYPE=" ).append(channelTransferVO.getType() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TRF_INITIATED_BY=" ).append(channelTransferVO.getTransferInitatedBy() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TRF_MRP=" ).append(channelTransferVO.getTransferMRP() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("FRST_APPRVL_LIMIT=" ).append(channelTransferVO.getFirstApproverLimit() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("SCND_APPRVL_LIMIT=" ).append(channelTransferVO.getSecondApprovalLimit() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("PAYABLE_AMT=" ).append(channelTransferVO.getPayableAmount() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("NET_PAYABLE_AMT=" ).append(channelTransferVO.getNetPayableAmount() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("PYMNT_INST_TYPE=" ).append(BTSLUtil.NullToString(channelTransferVO.getPayInstrumentType()));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("PYMNT_INST_NO=" ).append(BTSLUtil.NullToString(channelTransferVO.getPayInstrumentNum()));
            strBuff.append(cHNLTxnLogSep);
			if(channelTransferVO.getPayInstrumentDate()!=null){
                strBuff.append("PYMNT_INST_DT=" ).append(BTSLDateUtil.getSystemLocaleDate(channelTransferVO.getPayInstrumentDate(), true));
			} else {
                strBuff.append("PYMNT_INST_DT=");
			}
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("PYMNT_INST_AMT=" ).append(channelTransferVO.getPayInstrumentAmt());
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("S_TXN_PROF=" ).append(BTSLUtil.NullToString(channelTransferVO.getSenderTxnProfile()));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("R_TXN_PROF=" ).append(BTSLUtil.NullToString(channelTransferVO.getReceiverTxnProfile()));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TTAX1=" ).append(channelTransferVO.getTotalTax1() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TTAX2=" ).append(channelTransferVO.getTotalTax2() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TTAX3=" ).append(channelTransferVO.getTotalTax3() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("SOURCE=" ).append(channelTransferVO.getSource() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("R_CATCODE=" ).append(channelTransferVO.getReceiverCategoryCode() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("REQ_GTW_CODE=" ).append(channelTransferVO.getRequestGatewayCode() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("REQ_GTW_TYPE=" ).append(channelTransferVO.getRequestGatewayType() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("PYMNT_INST_SRC=" ).append(BTSLUtil.NullToString(channelTransferVO.getPaymentInstSource()));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("PRD_TYPE=" ).append(BTSLUtil.NullToString(channelTransferVO.getProductType()));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TRF_CAT=" ).append(channelTransferVO.getTransferCategory() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TRF_TYPE=" ).append(channelTransferVO.getTransferType());
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TRF_SUB_TYPE=" ).append(channelTransferVO.getTransferSubType() );
            strBuff.append(cHNLTxnLogSep);
			if(!BTSLUtil.isNullString(channelTransferVO.getThirdApprovedBy())){
                strBuff.append("CLOSE_DT=" ).append(BTSLDateUtil.getLocaleDateTimeFromDate(channelTransferVO.getThirdApprovedOn()));
			} else if(!BTSLUtil.isNullString(channelTransferVO.getSecondApprovedBy())){
                strBuff.append("CLOSE_DT=" ).append(BTSLDateUtil.getLocaleDateTimeFromDate(channelTransferVO.getSecondApprovedOn()));
			} else if(!BTSLUtil.isNullString(channelTransferVO.getFirstApprovedBy())){
                strBuff.append("CLOSE_DT=" ).append(BTSLDateUtil.getLocaleDateTimeFromDate(channelTransferVO.getFirstApprovedOn()));   
			} else {
                strBuff.append("CLOSE_DT=" ).append(BTSLDateUtil.getSystemLocaleCurrentDate());
			}
           	strBuff.append(cHNLTxnLogSep);
           	strBuff.append("BATCH_NO=" ).append(BTSLUtil.NullToString(channelTransferVO.getBatchNum()) );
           	strBuff.append(cHNLTxnLogSep);
			if(channelTransferVO.getBatchDate()!=null){
               	strBuff.append("BATCH_DT=" ).append(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.NullToString(BTSLUtil.getDateStringFromDate(channelTransferVO.getBatchDate()))));
			} else {
               	strBuff.append("BATCH_DT=");
			}
           	strBuff.append(cHNLTxnLogSep);
            strBuff.append("CTRL_TRF=" ).append(BTSLUtil.NullToString(channelTransferVO.getControlTransfer()));
            strBuff.append(cHNLTxnLogSep);
			if(PretupsI.CHANNEL_TYPE_O2C.equalsIgnoreCase(channelTransferVO.getType()) && PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferType())){
                strBuff.append("MSISDN=");
			} else {
                strBuff.append("MSISDN=" ).append(channelTransferVO.getFromUserCode() );
			}
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TO_MSISDN=" ).append(BTSLUtil.NullToString(channelTransferVO.getToUserCode()) );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TO_DM_CODE=" ).append(BTSLUtil.NullToString(channelTransferVO.getReceiverDomainCode()));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TO_GRH_DOMAIN_CODE=" ).append(BTSLUtil.NullToString(channelTransferVO.getReceiverGgraphicalDomainCode() ));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("SMS_DFLT_LANG=" ).append(BTSLUtil.NullToString(channelTransferVO.getDefaultLang()) );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("SMS_SCND_LANG=" ).append(BTSLUtil.NullToString(channelTransferVO.getSecondLang() ));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("FOC_BONUS_BATCH_DT="); // not used anywhere
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("FOC_BONUS_BATCH_NO=");  // not used anywhere
            strBuff.append(cHNLTxnLogSep);
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equalsIgnoreCase(channelTransferVO.getStatus()) && PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equalsIgnoreCase(channelTransferVO.getStatusDesc()))
            {
            	 strBuff.append("FRST_APRVD_QTY=" ).append(BTSLUtil.NullToString(Long.toString(PretupsBL.getSystemAmount(channelTransferVO.getLevelOneApprovedQuantity())))) ;
            }
            else
            strBuff.append("FRST_APRVD_QTY=" ).append(BTSLUtil.NullToString(channelTransferVO.getLevelOneApprovedQuantity()));
            strBuff.append(cHNLTxnLogSep);
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equalsIgnoreCase(channelTransferVO.getStatus()) && PretupsI.CHANNEL_TRANSFER_APPROVAL_1.equalsIgnoreCase(channelTransferVO.getStatusDesc()))
            {
            	 strBuff.append("SCND_APRVD_QTY=" ).append(BTSLUtil.NullToString(Long.toString(PretupsBL.getSystemAmount(channelTransferVO.getLevelTwoApprovedQuantity()))));
            }
            else
            strBuff.append("SCND_APRVD_QTY=" ).append(BTSLUtil.NullToString(channelTransferVO.getLevelTwoApprovedQuantity()));
            strBuff.append(cHNLTxnLogSep);
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equalsIgnoreCase(channelTransferVO.getStatus()) && PretupsI.CHANNEL_TRANSFER_APPROVAL_2.equalsIgnoreCase(channelTransferVO.getStatusDesc()))
            {
            	 strBuff.append("THRD_APRVD_QTY=" ).append(BTSLUtil.NullToString(Long.toString(PretupsBL.getSystemAmount(channelTransferVO.getLevelThreeApprovedQuantity()))));
            }
            else
            strBuff.append("THRD_APRVD_QTY=" ).append(BTSLUtil.NullToString(channelTransferVO.getLevelThreeApprovedQuantity()));
			if(isMultipleWalletApply)
			{   
            	 strBuff.append(cHNLTxnLogSep);
            	 if(!BTSLUtil.isNullString(channelTransferVO.getWalletType()))
            		strBuff.append("TXN_WALLET=" ).append(BTSLUtil.NullToString(channelTransferVO.getWalletType()));
            	 else
					strBuff.append("TXN_WALLET=");
                }
            strBuff.append(cHNLTxnLogSep);
            if (focBatchItemVO != null) {
                if (!(focBatchItemVO.getUserId().equalsIgnoreCase(channelTransferVO.getToUserID())) && (isDPAllowed)) {
            		strBuff.append("OWNR_TRF_MRP=" ).append( focBatchItemVO.getRequestedQuantity()); // this values are not present in the select query for any transaction
            		strBuff.append(cHNLTxnLogSep);                                                                                                
                	strBuff.append("OWNER_DBT_MRP="  ).append(focBatchItemVO.getRequestedQuantity());// this values are not present in the select query for any transaction
				} else {
            		strBuff.append("OWNR_TRF_MRP=0"); // this values are not present in the select query for any transaction
            		strBuff.append(cHNLTxnLogSep);
                	strBuff.append("OWNER_DBT_MRP=0");// this values are not present in the select query for any transaction
                }
          		strBuff.append(cHNLTxnLogSep);
            	strBuff.append("BONUS_TYPE=" ).append(BTSLUtil.NullToString(focBatchItemVO.getBonusType()) );// this values are not present in the select query for any transaction
			} else {
            	strBuff.append("OWNR_TRF_MRP=" ); // this values are not present in the select query for any transaction
            	strBuff.append(cHNLTxnLogSep);
            	strBuff.append("OWNER_DBT_MRP=" );// this values are not present in the select query for any transaction
            	strBuff.append(cHNLTxnLogSep);
            	strBuff.append("BONUS_TYPE=" );// this values are not present in the select query for any transaction
            }
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("ACTVE_USR_ID=" ).append(BTSLUtil.NullToString(channelTransferVO.getActiveUserId() ));
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("TXN_MODE=" ).append(channelTransferVO.getTransactionMode() );
            strBuff.append(cHNLTxnLogSep);
            strBuff.append("REF_TRF_ID=" ).append(BTSLUtil.NullToString(channelTransferVO.getRefTransferID()) );
			for (int i = 0, k = channelTransferList.size(); i <k; i++)
			{
                transferItemsVO = (ChannelTransferItemsVO) channelTransferList.get(i);
            	strBuff.append(cHNLTxnLogSep);
            	strBuff.append("SNO=" ).append((i+1) );
            	strBuff.append(cHNLTxnLogSep);
                strBuff.append("PROD_CODE=" ).append(transferItemsVO.getProductCode() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("REQURD_QTY=" ).append(transferItemsVO.getRequiredQuantity() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("APPRVED_QTY=" ).append(transferItemsVO.getApprovedQuantity() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("USR_UNIT_PRICE=" ).append(transferItemsVO.getUnitValue() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("COMM_PROF_DTAIL_ID=" ).append(transferItemsVO.getCommProfileDetailID() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("COMM_TYPE=" ).append(transferItemsVO.getCommType() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("COMM_RATE=" ).append(transferItemsVO.getCommRate() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("COMM_VALUE=" ).append(transferItemsVO.getCommValue() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX1_TYPE=" ).append(transferItemsVO.getTax1Type() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX1_RATE=" ).append(transferItemsVO.getTax1Rate() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX1_VALUE=" ).append(transferItemsVO.getTax1Value() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX2_TYPE=" ).append(transferItemsVO.getTax2Type() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX2_RATE=" ).append(transferItemsVO.getTax2Rate() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX2_VALUE=" ).append(transferItemsVO.getTax2Value() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("S_PREV_STOCK=" ).append(transferItemsVO.getAfterTransSenderPreviousStock() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("R_PREV_STOCK=" ).append(transferItemsVO.getAfterTransReceiverPreviousStock() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX3_TYPE=" ).append(transferItemsVO.getTax3Type() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX3_RATE=" ).append(transferItemsVO.getTax3Rate() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("TAX3_VALUE=" ).append(transferItemsVO.getTax3Value() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("MRP=" ).append(transferItemsVO.getProductTotalMRP());
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("S_DR_QTY=" ).append(transferItemsVO.getSenderDebitQty() );
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("R_CR_QTY=" ).append(transferItemsVO.getReceiverCreditQty() );
                strBuff.append(cHNLTxnLogSep);
				if(transferItemsVO.getCommQuantity()!=0){
                    strBuff.append("COMM_QTY=" ).append(transferItemsVO.getCommQuantity());
				} else {
                    strBuff.append("COMM_QTY=");
				}
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("S_POST_STOCK=" ).append((transferItemsVO.getAfterTransSenderPreviousStock()-transferItemsVO.getSenderDebitQty()));
                strBuff.append(cHNLTxnLogSep);
                strBuff.append("R_POST_STOCK=" ).append((transferItemsVO.getAfterTransReceiverPreviousStock()+transferItemsVO.getReceiverCreditQty()));       					

                /** START: Birendra: 28JAN2015 */
                if (isUserProductMultipleWallet) {
					strBuff.append(cHNLTxnLogSep);
					strBuff.append("WALLET_CODE=" ).append(transferItemsVO.getUserWallet());
                } else {
					strBuff.append(cHNLTxnLogSep);
					strBuff.append("WALLET_CODE=" ).append(defaultWallet);
                }
                /** STOP: Birendra: 28JAN2015 */
            }
            _logCHNL.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
    }
    public static void log(P2PTransferVO p_p2pTransferVO,TransferItemVO p_senderTransferItemVO,TransferItemVO p_receiverTransferItemVO,VomsVoucherVO _vomsVO)
	{
		final String METHOD_NAME="log";
		try
		{
			StringBuilder strBuff = new StringBuilder();
			strBuff.append("[TID:").append(p_p2pTransferVO.getTransferID()).append("]");
			strBuff.append("[SID:").append(p_p2pTransferVO.getSenderID() ).append("]");
			strBuff.append("[GWTYP:").append(p_p2pTransferVO.getRequestGatewayType()).append("]");
			strBuff.append("[GW:").append(p_p2pTransferVO.getRequestGatewayCode()).append("]");
			strBuff.append("[SMNO:").append(p_p2pTransferVO.getSenderMsisdn()).append("]");
			strBuff.append("[SNW:").append(p_p2pTransferVO.getNetworkCode()).append("]");
			strBuff.append("[PRD:").append(p_p2pTransferVO.getProductCode()).append("]");
			strBuff.append("[TRFVAL:").append(p_receiverTransferItemVO.getTransferValue()).append("]");
			strBuff.append("[TRFST:").append(p_p2pTransferVO.getTransferStatus()).append("]");
			strBuff.append("[E:").append(p_p2pTransferVO.getErrorCode()).append("]");
			strBuff.append("[RINSTU:").append(p_receiverTransferItemVO.getUpdateStatus()).append("]");
			if(BTSLUtil.isNullString(p_receiverTransferItemVO.getUpdateStatus())){
				strBuff.append("[RINRESPCDU:]");
				strBuff.append("[RINIDU:]");
				strBuff.append("[RPROTOCOLSTU:]");
			}else{
				strBuff.append("[RINRESPCDU:").append(p_receiverTransferItemVO.getInterfaceResponseCode()).append("]");
				strBuff.append("[RINIDU:").append(p_receiverTransferItemVO.getInterfaceID()).append("]");
				strBuff.append("[RPROTOCOLSTU:").append(p_receiverTransferItemVO.getProtocolStatus()).append("]");
			}
			strBuff.append("[SRVC:").append(p_p2pTransferVO.getServiceType()).append("]");
			strBuff.append("[SUB:").append(p_p2pTransferVO.getSubService()).append("]");
			strBuff.append("[TRFCAT:").append(p_p2pTransferVO.getTransferCategory()).append("]");
			strBuff.append("[CELLID:").append(p_p2pTransferVO.getCellId()).append("]");
			strBuff.append("[SWITCHID:").append(p_p2pTransferVO.getSwitchId()).append("]");
			strBuff.append("[VSNO:").append(_vomsVO.getSerialNo()).append("]");
			strBuff.append("[VSTATUS:").append(_vomsVO.getCurrentStatus()).append("]");
			strBuff.append("[VEXPIRYDATE:").append(BTSLDateUtil.getSystemLocaleDate(_vomsVO.getExpiryDateStr())).append("]");
			strBuff.append("[VINTERFACESTATUS:").append(_vomsVO.getLastErrorMessage()).append("]");
			strBuff.append("[VMRP:").append(Double.toString(_vomsVO.getMRP())).append("]");
			strBuff.append("[VTALKTIME:").append(Long.toString(_vomsVO.getMaxReqQuantity())).append("]");
			strBuff.append("[VMESSAGE:").append(_vomsVO.getMessage()).append("]");
			strBuff.append("[TT:").append(System.currentTimeMillis() - p_p2pTransferVO.getRequestStartTime()).append("ms]");

			_logVMS.info("",strBuff.toString());
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			_logVMS.error("log",p_p2pTransferVO.getTransferID()," Not able to log info, getting Exception :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransactionLog[log]",p_p2pTransferVO.getTransferID(),"","","Not able to log info for Transfer ID:"+p_p2pTransferVO.getTransferID()+" ,getting Exception="+e.getMessage());
		}
	} 

    public static Log getLogger() {
        return _logC2S;
    }
}
