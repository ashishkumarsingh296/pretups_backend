package com.btsl.pretups.channel.transfer.requesthandler;

import java.math.BigDecimal;
/**
 * * @(#)O2CDirectTransferController.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Amit singh Nov. 12, 2006 Initial Creation
 * 
 * This class parses the received request on the basis of format for
 * initiate and approve O2C transfer.
 * 
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionCache;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OneLineTXNLog;import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.user.businesslogic.UserLoanVO;


public class O2CDirectTransferController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(O2CDirectTransferController.class.getName());
    private static final String className="O2CDirectTransferController";
    private ArrayList prdList = null;
    private static OperatorUtilI _operatorUtil = null;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * Method Process
     * This method is the entry point of the class.
     * This method performs all the work related to O2C Transfer
     * 
     * 1. PIN validation
     * 2. validate message contents
     * 3. validate the basic checks on channel user
     * 4. load & validate the products
     * 5. calculate the taxes of products
     * 6. prepare the ChannelTransferVO
     * 7. generate the transfer ID
     * 8. approve the transaction
     * 9. add channel transfer in database
     * 
     * @param p_requestVO
     */
    @Override
	public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
	        loggerValue.setLength(0);
        	loggerValue.append("Entered p_requestVO: ");
        	loggerValue.append(p_requestVO);
            _log.debug(METHOD_NAME, loggerValue );
        }

		Connection con = null;
		MComConnectionI mcomCon = null;
        int insertCount = 0;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        Date currentDate = null;
        int msgLen = 0;
        CurrencyConversionVO currencyVO;

        try {
            // getting the oracle connection
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            // getting the msgArray from the request
            final String[] messageArr = p_requestVO.getRequestMessageArray();

            msgLen = messageArr.length;

            // validate the PIN if it is in the request
            if ((PretupsI.YES.equals(userPhoneVO.getPinRequired())) && p_requestVO.isPinValidationRequired()) {
                try {
                    if (p_requestVO.getRequestMap() != null && p_requestVO.getRequestMap().get("REMARKS") != null && !PretupsI.LMSFOCO2C.equalsIgnoreCase((String) p_requestVO.getRequestMap().get("REMARKS"))) { 
                        ChannelUserBL.validatePIN(con, channelUserVO, messageArr[msgLen - 1]);
                    }
                } catch (BTSLBaseException be) {
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        OracleUtil.commit(con);
                    }
                    throw be;
                }
            }

            final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

            currentDate = new Date();
            // validate the channel user
            
            ChannelTransferBL.o2cTransferUserValidate(con, p_requestVO, channelTransferVO, currentDate);

            // Meditel changes.....checking for receiver allowed
            UserStatusVO receiverStatusVO = null;
            boolean receiverAllowed = false;
            if (channelUserVO != null) {
                receiverAllowed = false;
                receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO.getUserType(),
                    p_requestVO.getRequestGatewayType());
                if (receiverStatusVO != null) {
                    final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
                    final String status[] = receiverStatusAllowed.split(",");
                    int st=status.length;
                    for (int i = 0; i < st; i++) {
                        if (status[i].equals(channelUserVO.getStatus())) {
                            receiverAllowed = true;
                        }
                    }
                }
            }

            if (receiverStatusVO == null) {
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
            } else if (!receiverAllowed) {
                /*
                 * p_requestVO.setMessageCode(PretupsErrorCodesI.
                 * CHNL_ERROR_RECEIVER_NOTALLOWED);
                 * p_requestVO.setMessageArguments(args);
                 */
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
            }
            
            final HashMap productMap = ChannelTransferBL.validateO2CMessageContent(con, p_requestVO, channelTransferVO, true);
            String defaultCurrency = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY);
            if (p_requestVO.getRequestMap() != null && p_requestVO.getRequestMap().get("CURRENCY") != null && ((String)p_requestVO.getRequestMap().get("CURRENCY")).trim().length()!=0)
            {
            	long mult;
            	double temp;
            	double finalValue;
            	currencyVO = (CurrencyConversionVO)CurrencyConversionCache.getObject((String)p_requestVO.getRequestMap().get("CURRENCY"), defaultCurrency, channelUserVO.getNetworkID());
            	int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
            	mult = amountMultFactor;

            	final Iterator prdItr = (productMap.entrySet()).iterator();
            	String prdCode = null;
            	String prdQty = null;
            	while (prdItr.hasNext()) {
            		Entry ent = (Entry)prdItr.next();
            		prdCode = (String)(ent).getKey();
            		prdQty = (String)(ent).getValue();  	

            		temp = (Double.parseDouble(BigDecimal.valueOf(currencyVO.getConversion()).toPlainString())/currencyVO.getMultFactor()) * Double.parseDouble(prdQty) ;
            		finalValue = Math.round(Double.parseDouble(BigDecimal.valueOf(temp * mult).toPlainString()))/(double)mult;
            		channelTransferVO.setMultiCurrencyDetail((String)p_requestVO.getRequestMap().get("CURRENCY")+":"+Double.parseDouble(BigDecimal.valueOf(currencyVO.getConversion()).toPlainString())/currencyVO.getMultFactor()+":"+prdQty);
            		productMap.put(prdCode,finalValue);
            	}
            }
            
            boolean transactionTypeAlwd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
            String type = (transactionTypeAlwd)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
            String pmtMode = (p_requestVO.getRequestMap() !=null)?((p_requestVO.getRequestMap().get("PAYMENTTYPE") != null)? (String)p_requestVO.getRequestMap().get("PAYMENTTYPE"):PretupsI.ALL):PretupsI.ALL;
    		String paymentMode = (transactionTypeAlwd && transactionTypeAlwd && PretupsI.GATEWAY_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType()))?pmtMode:PretupsI.ALL;
            prdList = ChannelTransferBL.loadAndValidateProducts(con, p_requestVO, productMap, channelUserVO, true, type, paymentMode);
            boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
            if (userProductMultipleWallet) {
                ChannelTransferBL.loadAndValidateWallets(con, p_requestVO, prdList);
            } else {
                ChannelTransferBL.assignDefaultWallet(con, p_requestVO, prdList);
            }
            
            channelTransferVO.setChannelTransferitemsVOList(prdList);
            channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            channelTransferVO.setToUserID(channelUserVO.getUserID());
            channelTransferVO.setOtfFlag(true);
            channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
            channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());

            // calculate the taxes for the diff. products
            // based on the transfer category
            boolean othComChnl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
			if(othComChnl){
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setToUserMsisdn(p_requestVO.getFilteredMSISDN());
			}
            if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE))) {
                ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), channelUserVO.getCommissionProfileSetVersion(),
                    channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_O2C);
            } else if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_TRANSFER))) {
                ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), channelUserVO.getCommissionProfileSetVersion(),
                    channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_FOC);
            }

            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

            // load the user's information (network admin)
            final UserVO userVO = channelUserDAO.loadOptUserForO2C(con, channelUserVO.getNetworkID());

            // prepares the ChannelTransferVO by populating its fields from the
            // passed ChannelUserVO and filteredList of products for O2C
            // transfer
            prepareChannelTransferVO(p_requestVO, channelTransferVO, currentDate, channelUserVO, prdList, userVO);
            UserPhoneVO phoneVO = null;
            UserPhoneVO primaryPhoneVO_R = null;
            boolean secondaryNumberAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
            boolean messageToPrimaryRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED);
            if (secondaryNumberAllow) {
                final UserDAO userDAO = new UserDAO();
                phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getRequestMSISDN());
                if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                    channelUserVO.setPrimaryMsisdn(channelTransferVO.getToUserCode());
                    channelTransferVO.setToUserCode(p_requestVO.getRequestMSISDN());
                    if (messageToPrimaryRequired) {
                        primaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, channelUserVO.getPrimaryMsisdn());
                    }
                }

            }
            
            //Validate MRP && Successive Block for channel transaction
			long successiveReqBlockTime4ChnlTxn = ((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C)).longValue();
			ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(con, channelTransferVO, currentDate, successiveReqBlockTime4ChnlTxn);
			
            // generate transfer ID for the O2C transfer
            ChannelTransferBL.genrateTransferID(channelTransferVO);

            // set the transfer ID in each ChannelTransferItemsVO of productList
            for (int i = 0, j = prdList.size(); i < j; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) prdList.get(i);
                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
            }

            // the transfer is controlled by default, so set to 'Y'
            channelTransferVO.setControlTransfer(PretupsI.YES);
           
            // performs all the approval transactions for the transfer opertaion
           transactionApproval(con, channelTransferVO, userVO.getUserID(), currentDate, channelUserVO);

            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

            // insert the channelTransferVO in the database
            
            
            /*if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
    		{
           	 final ArrayList<ChannelTransferItemsVO> list = new ArrayList<>();
           	 if(channelTransferVO.getChannelTransferitemsVOListforOTF()!=null && channelTransferVO.getChannelTransferitemsVOList()!=null )
           	 {
           		 int channelTransferVOChannelTransferitemsVOLists=channelTransferVO.getChannelTransferitemsVOList().size();
            for(int i=0; i < channelTransferVOChannelTransferitemsVOLists; i++){
         	  ChannelTransferItemsVO ctiVO =  (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(i);
         	  ChannelTransferItemsVO ctiOTFVO =  (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOListforOTF().get(i);
         	  ctiVO.setOtfApplicable(ctiOTFVO.isOtfApplicable());
         	   list.add(ctiVO);
            }
            channelTransferVO.setChannelTransferitemsVOList(list);
           	 }
    		
    		}*/
         	   
            insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
            if (insertCount < 0) {
            	OracleUtil.rollbackConnection(con, O2CDirectTransferController.class.getName(), METHOD_NAME);
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            OracleUtil.commit(con);
            p_requestVO.setSuccessTxn(true);
            //Added for OneLine Logging for Channel
            OneLineTXNLog.log(channelTransferVO, null);
			
            ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);

            // sending msg to receiver
            final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
            // String smsKey=PretupsErrorCodesI.O2C_DIRECT_TRANSFER_RECEIVER;
            String smsKey = null;
            final ArrayList txnList = new ArrayList();
            final ArrayList balList = new ArrayList();
            String args[] = null;
            ChannelTransferItemsVO channelTrfItemsVO = null;
            KeyArgumentVO keyArgumentVO = null;

            final int lSize = itemsList.size();
            for (int i = 0; i < lSize; i++) {
                channelTrfItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_DIRECT_TRANSFER_SUCCESS_TXNSUBKEY);
                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), channelTrfItemsVO.getRequestedQuantity() };
                keyArgumentVO.setArguments(args);
                txnList.add(keyArgumentVO);

                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_DIRECT_TRANSFER_SUCCESS_BALSUBKEY);
                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTrfItemsVO.getBalance() + channelTrfItemsVO
                    .getReceiverCreditQty()) };
                keyArgumentVO.setArguments(args);
                balList.add(keyArgumentVO);
            }// end of for
            if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE))) {
                final String[] msgArray = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                    .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), "operator" };
                p_requestVO.setMessageArguments(msgArray);
                smsKey = PretupsErrorCodesI.O2C_DIRECT_TRANSFER_RECEIVER;
            } else if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_TRANSFER))) {
                final String[] msgArray = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                    .getTransferID() };
                p_requestVO.setMessageArguments(msgArray);
                smsKey = PretupsErrorCodesI.FOC_TRANSFER_EXTGW_RECEIVER;
            }
            p_requestVO.setMessageCode(smsKey);
            p_requestVO.setTransactionID(channelTransferVO.getTransferID());
           if(p_requestVO.getRequestMap()!=null){
            p_requestVO.getRequestMap().put("PREBAL", PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()));
            p_requestVO.getRequestMap().put("POSTBAL", args[1]);
           }

            if (secondaryNumberAllow && messageToPrimaryRequired) {
                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(p_requestVO.getServiceType()) && p_requestVO.isSenderMessageRequired()) {
                    if (primaryPhoneVO_R != null) {
                        final Locale locale = new Locale(primaryPhoneVO_R.getPhoneLanguage(), primaryPhoneVO_R.getCountry());
                        final String senderMessage = BTSLUtil.getMessage(locale, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                        final PushMessage pushMessage = new PushMessage(primaryPhoneVO_R.getMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO
                            .getRequestGatewayCode(), locale);
                        pushMessage.push();
                    }
                }
            }
            boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
            if (lmsAppl) {
                try {
                    if (p_requestVO.isSuccessTxn()) {
                        final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                        final Date date = new Date();
                        final LoyaltyVO loyaltyVO = new LoyaltyVO();
                        PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
                        final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                        final ArrayList arr = new ArrayList();
                        // con = OracleUtil.getConnection(); DB connection leakage.
                        loyaltyVO.setServiceType(PretupsI.O2C_MODULE);
                        loyaltyVO.setModuleType(PretupsI.O2C_MODULE);
                        if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                            loyaltyVO.setTransferamt(channelTransferVO.getSenderDrQty());
                        } else {
                            loyaltyVO.setTransferamt(channelTransferVO.getTransferMRP());
                        }
                        loyaltyVO.setCategory(channelTransferVO.getCategoryCode());
                        loyaltyVO.setUserid(channelTransferVO.getToUserID());
                        loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
                        loyaltyVO.setSenderMsisdn(channelTransferVO.getToUserCode());
                        loyaltyVO.setTxnId(channelTransferVO.getTransferID());
                        loyaltyVO.setCreatedOn(date);
                        loyaltyVO.setProductCode(channelTransferVO.getProductCode());
                        arr.add(loyaltyVO.getUserid());
                        promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
                        loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                        if (loyaltyVO.getSetId() == null) {
                            _log.error(METHOD_NAME, "Exception durign LMS Module Profile Details are not found");
                        } else {
                            _loyaltyBL.distributeLoyaltyPoints(PretupsI.O2C_MODULE, channelTransferVO.getTransferID(), loyaltyVO);
                        }

                    }
                } catch (Exception ex) {
                	loggerValue.setLength(0);
                	loggerValue.append("Exception durign LMS Module ");
                	loggerValue.append(ex.getMessage());
                    _log.error(METHOD_NAME,  loggerValue );
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }

            // Meditel changes by Ashutosh
            if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                try {
                	/* Commented for  connection leak issue
                    if (mcomCon == null) {
                    	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    }*/
                    boolean statusAllowed = false;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
                        .getUserType(), p_requestVO.getRequestGatewayType());
                    if (userStatusVO == null) {
                        throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
                    } else {
                        final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                        final String status[] = userStatusAllowed.split(",");
                        for (int i = 0; i < status.length; i++) {
                            if (status[i].equals(channelUserVO.getStatus())) {
                                statusAllowed = true;
                            }
                        }

                        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
                        PretupsBL.chkAllwdStatusToBecomeActive(con, txnReceiverUserStatusChang, channelUserVO.getUserID(), channelUserVO.getStatus());
                    }

                } catch (Exception ex) {
                	loggerValue.setLength(0);
                	loggerValue.append("Exception while changing user state to active  ");
                	loggerValue.append(ex.getMessage());
                    _log.error(METHOD_NAME,  loggerValue);
                    _log.errorTrace(METHOD_NAME, ex);
                } finally {
                    if (con != null) {
                        try {
                        	OracleUtil.commit(con);
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
						}
					/*	if (mcomCon != null) {
							mcomCon.close("O2CDirectTransferController#process");
							mcomCon = null;
						}
                        con = null;*/
                    }

                }
            }
            // end of changes

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            OracleUtil.rollbackConnection(con, O2CDirectTransferController.class.getName(), METHOD_NAME);
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(be.getMessage());
            _log.error(METHOD_NAME,  loggerValue );
            _log.errorTrace(METHOD_NAME, be);
            if (be.getMessageList() != null && !be.getMessageList().isEmpty()) {
                final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
                p_requestVO.setMessageArguments(array);
            }
            if (be.getArgs() != null) {
                p_requestVO.setMessageArguments(be.getArgs());
            }
            if (be.getMessageKey() != null) {
                p_requestVO.setMessageCode(be.getMessageKey());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            }
            return;
        } catch (Exception ex) {
            p_requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            OracleUtil.rollbackConnection(con, O2CDirectTransferController.class.getName(), METHOD_NAME);
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException " );
        	loggerValue.append(ex.getMessage());
            _log.error(METHOD_NAME, loggerValue );
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CDirectTransferController[process]", "", "", "",
            		loggerValue.toString());
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            return;
        } finally {
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("O2CDirectTransferController#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited.. ");
            }
        }
    }

    /**
     * Method prepareChannelTransferVO
     * This method used to construct the VO for channel transfer
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_curDate
     *            Date
     * @param p_channelUserVO
     *            ChannelUserVO
     * @param p_prdList
     *            ArrayList
     * @throws BTSLBaseException
     */
    private void prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList, UserVO p_userVO) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
	        loggerValue.setLength(0);
        	loggerValue.append( "Entering  : requestVO ");
        	loggerValue.append(p_requestVO);
        	loggerValue.append("p_channelTransferVO:");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_curDate:" );
        	loggerValue.append(p_curDate);
        	loggerValue.append("p_channelUserVO:");
        	loggerValue.append(p_channelUserVO);
        	loggerValue.append("p_prdList:");
        	loggerValue.append(p_prdList);
        	loggerValue.append("p_userVO:");
        	loggerValue.append(p_userVO);
        	loggerValue.append( "sourceType: ");
        	loggerValue.append(p_requestVO.getSourceType());
            _log.debug("prepareChannelTransferVO",loggerValue );
        }
        
        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        /*user name set to display user name in message for transfer in counts DEF51 claro*/
        p_channelTransferVO.setToUserName(p_channelUserVO.getUserName());
        // who initaite the order.
        p_channelTransferVO.setReceiverGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        // To display MSISDN in balance log
        p_channelTransferVO.setUserMsisdn(p_channelUserVO.getUserCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_userVO.getUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_userVO.getUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(p_requestVO.getSourceType());

        // adding the some additional information of sender/reciever
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        p_channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        p_channelTransferVO.setActiveUserId(p_userVO.getUserID());

        ChannelTransferItemsVO channelTransferItemsVO = null;
        String productType = null;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        for (int i = 0, k = p_prdList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_prdList.get(i);
            totRequestQty += channelTransferItemsVO.getRequiredQuantity();
            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                totMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
            } else {
                totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
            }
            totPayAmt += channelTransferItemsVO.getPayableAmount();
            totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
            totTax1 += channelTransferItemsVO.getTax1Value();
            totTax2 += channelTransferItemsVO.getTax2Value();
            totTax3 += channelTransferItemsVO.getTax3Value();

            productType = channelTransferItemsVO.getProductType();
            commissionQty += channelTransferItemsVO.getCommQuantity();
            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
        }

        p_channelTransferVO.setRequestedQuantity(totRequestQty);
        p_channelTransferVO.setTransferMRP(totMRP);
        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        p_channelTransferVO.setPayInstrumentAmt(totNetPayAmt);
        p_channelTransferVO.setProductType(productType);
        p_channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        p_channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        p_channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
        //Added by lalit to fix bug DEF528 for GP 6.6.1
        boolean multipleWalletApply = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
        if (multipleWalletApply && p_requestVO.getRequestMap() != null) {
        	Map<String, String> requestMap = p_requestVO.getRequestMap();
        	if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase((requestMap.get("TRFCATEGORY")!=null?requestMap.get("TRFCATEGORY").toString():""))){
        		p_channelTransferVO.setWalletType(requestMap.get("TRFCATEGORY").toString());
        	}
        }
        final long firstApprovalLimit = p_channelTransferVO.getFirstApproverLimit();
        final long secondApprovalLimit = p_channelTransferVO.getSecondApprovalLimit();

        if (p_channelTransferVO.getRequestedQuantity() > secondApprovalLimit) {
            p_channelTransferVO.setThirdApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setThirdApprovedOn(p_curDate);
            p_channelTransferVO.setSecondApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setSecondApprovedOn(p_curDate);
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        } else if (p_channelTransferVO.getRequestedQuantity() <= secondApprovalLimit && p_channelTransferVO.getRequestedQuantity() > firstApprovalLimit) {
            p_channelTransferVO.setSecondApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setSecondApprovedOn(p_curDate);
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        } else if (p_channelTransferVO.getRequestedQuantity() <= firstApprovalLimit) {
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        }
        
        if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)  ) {
           	
   				p_channelTransferVO.setUserLoanVOList(p_channelUserVO.getUserLoanVOList());
   		
        }
        
        if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)){
        	ArrayList chnlSoSVOList = new ArrayList();
        	chnlSoSVOList.add(new ChannelSoSVO(p_channelUserVO.getUserID(),p_channelUserVO.getMsisdn(),p_channelUserVO.getSosAllowed(),p_channelUserVO.getSosAllowedAmount(),p_channelUserVO.getSosThresholdLimit()));
        	p_channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }
        
        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting : ");
        }
    }

    /**
     * Method transactionApproval
     * This method responcible to Approve the O2C transaction and update
     * the network stock, update the user balances and user counts
     * 
     * @param p_con
     *            Connection
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_date
     *            Date
     * @param p_userID
     *            String
     * @throws BTSLBaseException
     */
    private void transactionApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userID, Date p_date, ChannelUserVO channelUserVO) throws BTSLBaseException {
    	final String methodName="transactionApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering  : p_channelTransferVO:" + p_channelTransferVO + "p_userID:" + p_userID + "p_date:" + p_date);
        }

        try {
        	int updateCount = -1;
        	//added for o2c direct transfer
        	boolean o2cDirectTransfer = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_DIRECT_TRANSFER);
            if(o2cDirectTransfer)
            {
            updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userID, p_date, true);
            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;
            updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userID, p_date);
            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;
            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO, p_userID, p_date, true);
                if (updateCount < 1) {
                    throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                }
                updateCount = -1;
                updateCount = ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, p_userID, p_date);
                if (updateCount < 1) {
                    throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                }
            }
            p_channelTransferVO.setStockUpdated(TypesI.YES);
            }
            else
            {
            	 p_channelTransferVO.setStockUpdated(TypesI.NO);
            }
            
            UserBalancesVO userBalanceVO = null;
            ChannelTransferItemsVO chnlTrfItemsVO = null;
            final UserBalancesDAO userBalDAO = new UserBalancesDAO();

            for (int x = 0, y = prdList.size(); x < y; x++) {
                chnlTrfItemsVO = (ChannelTransferItemsVO) prdList.get(x);
                userBalanceVO = new UserBalancesVO();

                userBalanceVO.setUserID(p_channelTransferVO.getToUserID());
                userBalanceVO.setProductCode(chnlTrfItemsVO.getProductCode());
                userBalanceVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                userBalanceVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
                userBalanceVO.setLastTransferID(p_channelTransferVO.getTransferID());
                userBalanceVO.setLastTransferType(p_channelTransferVO.getTransferType());
                userBalanceVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
                userBalanceVO.setPreviousBalance(userBalanceVO.getBalance());
                userBalanceVO.setQuantityToBeUpdated(chnlTrfItemsVO.getRequiredQuantity());
                // Added on 13/02/2008
                userBalanceVO.setUserMSISDN(p_channelTransferVO.getToUserCode());
            }

            updateCount = userBalDAO.updateUserDailyBalances(p_con, p_date, userBalanceVO);
            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;

            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
            if (userProductMultipleWallet) {
                updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true, null);
            } else {
                updateCount = channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, true, null);
            }

            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

           if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase(p_channelTransferVO.getWalletType())){
        	   p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_FOC);
           }
           
           if(PretupsI.TRANSFER_TYPE_O2C.equalsIgnoreCase(p_channelTransferVO.getWalletType())){
        	   p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);
           }
            updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }


        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exit  : ");
            }
        }
    }
}
