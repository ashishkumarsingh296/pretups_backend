package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChnlToChnlTransferTransactionCntrl;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.util.XmlTagValueConstant;
import com.txn.pretups.channel.profile.businesslogic.TransferProfileTxnDAO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;


public class TransferBL {

	protected final static Log _log = LogFactory.getLog(TransferBL.class.getName());
	private static String _allowedSendMessGatw = null;
    public static OperatorUtilI _operatorUtil = null;
    
	final static String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
    
	public static void c2cService(Connection con,OAuthUser authUser,RequestVO p_requestVO,DataStockMul dataStkTrfMul,ChannelUserVO senderVO,Boolean fileOrno ) throws Exception {
        
		try {
	        _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	    } catch (Exception e) {
	        _log.errorTrace("static", e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
	            "Exception while loading the class at the call:" + e.getMessage());
	    }
		

		ChannelUserTxnDAO channelUserTxnDAO = null;
		UserDAO userDAO = null;
		final Date curDate = new Date();
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		channelUserTxnDAO = new ChannelUserTxnDAO();
		ChannelUserVO receiverChannelUserVO = null;
		boolean isUserDetailLoad = false;
		UserPhoneVO PrimaryPhoneVO_R = null;
		UserPhoneVO phoneVO = null;
		boolean receiverAllowed = false;
		final boolean senderAllowed = false;
		final UserStatusVO senderStatusVO = null;
		UserStatusVO receiverStatusVO = null;
		boolean _receiverMessageSendReq=false;
    	boolean _ussdReceiverMessageSendReq=false;
    	UserPhoneVO userPhoneVO = null;
    	userDAO = new UserDAO();
    	if (!senderVO.isStaffUser()) {
			userPhoneVO = senderVO.getUserPhoneVO();
		} else {
			userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
		}
    	p_requestVO.setSenderVO(senderVO);
    	String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		_ussdReceiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW,senderVO.getNetworkID(),p_requestVO.getServiceType())).booleanValue();
		_receiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,senderVO.getNetworkID(),p_requestVO.getServiceType())).booleanValue();
		Locale locale = new Locale(lang, country);
		
		if (senderVO != null && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(senderVO.getOutSuspened())) {
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
		}
		if(!BTSLUtil.isNullorEmpty(dataStkTrfMul.getPaymentDetails()))
		{p_requestVO.setPaymentDate(dataStkTrfMul.getPaymentDetails().get(0).getPaymentdate());
		p_requestVO.setPaymentInstNumber(dataStkTrfMul.getPaymentDetails().get(0).getPaymentinstnumber());
		p_requestVO.setPaymentType(dataStkTrfMul.getPaymentDetails().get(0).getPaymenttype());
		}
		if(BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2()) && BTSLUtil.isNullString(dataStkTrfMul.getExtcode2()) && BTSLUtil.isNullString(dataStkTrfMul.getLoginid2())){
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, 0, null);
		}
		if(!BTSLUtil.isNullorEmpty(dataStkTrfMul.getPaymentDetails()))
		{p_requestVO.setPaymentDate(dataStkTrfMul.getPaymentDetails().get(0).getPaymentdate());
		p_requestVO.setPaymentInstNumber(dataStkTrfMul.getPaymentDetails().get(0).getPaymentinstnumber());
		p_requestVO.setPaymentType(dataStkTrfMul.getPaymentDetails().get(0).getPaymenttype());
		}
		if(BTSLUtil.isNullorEmpty(dataStkTrfMul.getProducts()))
		{
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.PRODUCT_NOT_EXIST, 0, null);
		}
		for(int i=0;i<dataStkTrfMul.getProducts().size();i++)
		{
			if(BTSLUtil.isNullString(dataStkTrfMul.getProducts().get(i).getProductcode()))
			{
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.PRODUCT_NOT_EXIST, 0, null);
			}
		}
        
		if (!BTSLUtil.isNullString(dataStkTrfMul.getExtcode2())) {
			receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con,
					dataStkTrfMul.getExtcode2(), null, curDate);
			if (receiverChannelUserVO == null) {
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
			}

			isUserDetailLoad = true;

		} else if (!BTSLUtil.isNullString(dataStkTrfMul.getLoginid2())) {
			receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null,
					dataStkTrfMul.getLoginid2(), curDate);
			if (receiverChannelUserVO == null) {
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
			}

			isUserDetailLoad = true;
		}
		else if(!BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2())) {
			receiverChannelUserVO = channelUserDAO.loadChannelUserDetails(con,
					dataStkTrfMul.getMsisdn2());
			if (receiverChannelUserVO == null) {
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
			}

			isUserDetailLoad = true;
		}
		if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
			if (!BTSLUtil.isNullString(dataStkTrfMul.getExtcode2())) {
				if (!dataStkTrfMul.getExtcode2().equalsIgnoreCase(receiverChannelUserVO.getExternalCode())) {
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
				}
			}
			if (!BTSLUtil.isNullString(dataStkTrfMul.getLoginid2())) {
				if (!dataStkTrfMul.getLoginid2().equalsIgnoreCase(receiverChannelUserVO.getLoginID())) {

					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
				}
			}
			if (!BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2())) {
				if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
					phoneVO = userDAO.loadUserAnyPhoneVO(con, dataStkTrfMul.getMsisdn2());
					if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
						if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED
								&& ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
							PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
						}
						receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
						receiverChannelUserVO.setMsisdn(dataStkTrfMul.getMsisdn2());
					} else {
						throw new BTSLBaseException("C2CTransferController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
					}
				} else if (!dataStkTrfMul.getMsisdn2().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
				}
			}
			
			String str="";
			for(int k=0;k<dataStkTrfMul.getProducts().size();k++)
			{
				Products prod =(Products)dataStkTrfMul.getProducts().get(k);
				str = str + prod.getQty() + " ";
				str = str + prod.getProductcode()+" ";
			}
			String messageArray=p_requestVO.getServiceType()+" "+receiverChannelUserVO.getMsisdn()+" "+str+authUser.getData().getPin();
			p_requestVO.setRequestMessageArray(messageArray.split(" "));
			// To set the msisdn in the request message array...
			{
				final String[] mesgArr = p_requestVO.getRequestMessageArray();
				mesgArr[1] = receiverChannelUserVO.getMsisdn();
				p_requestVO.setRequestMessageArray(mesgArr);
			}
		}

		final String messageArr[] = p_requestVO.getRequestMessageArray();
		final int messageLen = messageArr.length;

		if (messageArr.length < 2) {
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0,
					new String[] { p_requestVO.getActualMessageFormat() }, null);
			// Validate the user code (mobile number in the message) is it
			// numeric or not
		}

		if (!BTSLUtil.isNumeric(messageArr[1])) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
		}

		// /
		// Vlaidte the user products as the quantity and product code both
		// should be numeric value
		// and if user does not send the product code then use the default
		// product as the requested product
		// /
		
		final String productArray[] = ChannelTransferBL.validateUserProductsFormatForSMS(messageArr, p_requestVO);

		final int msgLen = messageArr.length;
		// con = OracleUtil.getConnection();
		// /
		// Validate the user PIN if pin is required and it is not the
		// default pin of the C2S module
		// /
		// if((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
		// &&
		// !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
		// manisha
		if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
			try {
				ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[msgLen - 1]);
			} catch (BTSLBaseException be) {
				if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
						|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
					OracleUtil.commit(con);
				}
				throw be;
			}
		}
		// /
		// Validate the receiver mobile number as the validMSISDN and form
		// the supporting netowk
		// Check that is user is Barred in the system as the RECEIVER or not
		// if so then show error message
		// /
		String receiverUserCode = messageArr[1];
		/*
		 * The following code block is added to check if receiver number is
		 * valid This is added by Ankit Zindal 0n date 1/08/06
		 */
		receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
		if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
		}
		final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);

		// Getting network details
		final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		if (networkPrefixVO == null) {
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
					new String[] { receiverUserCode }, null);
		}

		final BarredUserDAO barredUserDAO = new BarredUserDAO();

		if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode,
				PretupsI.USER_TYPE_RECEIVER, null)) {
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
					new String[] { receiverUserCode }, null);
		}

		if (phoneVO == null) {
			phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
		}
		if (!isUserDetailLoad) {
			if (!SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
				receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
						true, curDate, false);
			} else {
				if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
					receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,
							phoneVO.getUserId(), false, curDate, false);
					if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
						PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
					}
					receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
					receiverChannelUserVO.setMsisdn(receiverUserCode);
				} else {
					receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
							true, curDate, false);
				}
			}
		}
		// 1. is user exist or not
		// 2. is user active or not
		// 3. is there any applicable commission profile with user or not
		// 4. is user is IN suspended or not if suspended then show error
		// message
		// /

		// Meditel changes.....checking for receiver allowed
		if (receiverChannelUserVO != null) {
			receiverAllowed = false;
			receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverChannelUserVO.getNetworkID(),
					receiverChannelUserVO.getCategoryCode(), receiverChannelUserVO.getUserType(),
					p_requestVO.getRequestGatewayType());
			if (receiverStatusVO != null) {
				final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
				final String status[] = receiverStatusAllowed.split(",");
				for (int h = 0; h < status.length; h++) {
					if (status[h].equals(receiverChannelUserVO.getStatus())) {
						receiverAllowed = true;
					}
				}
			}
		}
		//

		String args[] = { receiverUserCode };
		if (receiverChannelUserVO == null) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
			p_requestVO.setMessageArguments(args);
			throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST,
					0, args, null);
		} else if (receiverChannelUserVO.getInSuspend() != null
				&& PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(receiverChannelUserVO.getInSuspend())) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED);
			p_requestVO.setMessageArguments(args);
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED, 0, args, null);
		} else if (receiverStatusVO == null) {
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
		} else if (!receiverAllowed) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
			p_requestVO.setMessageArguments(args);
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args, null);
		} else if (receiverChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
			p_requestVO.setMessageArguments(args);
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args, null);
		}

		// /
		// Now validate the sender and receiver as
		// 1. status of commission profile of both users
		// 2. status of the transfer profile of both users
		// 3. transaction allowed based on the transfer rule
		// 4. validation of the user based on the transfer rule
		// 5. check that transaction is outSide hierarchy or not.
		// /
		// /
		// set the commission profile suspended messages
		// /
		// ChangeID=LOCALEMASTER
		// Check which language message to be sent, from the locale master
		// table for the perticuler locale.
		
		final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(lang, country));
		if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
			senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang1Msg());
			receiverChannelUserVO
					.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang1Msg());
		} else {
			senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang2Msg());
			receiverChannelUserVO
					.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang2Msg());
		}
		// valiadting sender and receiver with the transfer rule in this
		// method we are passing various parameters as
		// con - connection for the database access
		// senderVO - VO contains sender's information
		// receiverChannelUserVO - VO contains receiver's information
		// true (p_isUserCode) - To indicate that validation will be done on
		// the user code not on the user name for message display.
		// null (p_forwardPath) - for forwarding the message only in the
		// case of WEB so null is here
		// false (p_isFromWeb) - To indicate that request is not from WEB
		// i.e. form SMS/USSD.
		// txnSubType - Type of the transaction as TRANSFER, RETURN or
		// WITHDRAW. TRANSFER is here
		// This method return boolean value to indicate that whether this is
		// the controlled TXN or uncontroll TXN.

		final boolean isOutsideHierarchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, senderVO,
				receiverChannelUserVO, true, null, false, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		// /
		// Now validate the requested product as
		// 1. existance in the system
		// 2. mapping with the network
		// 3. having balance >0
		// 4. Applicable commission profile version
		// 5. product associated with the commisssion profile
		// 6. product associated with the transfer rule.
		// 7. requested quantity with the minimum transfer value
		// 8. requested quantity with the maximum transfer value
		// 9. requested quantity with the multiple of factor
		// 10. user balance with the requested quantity
		// /
		final ArrayList productList = ChannelTransferBL.validateReqstProdsWithDefinedProdsForXFR(con, senderVO,
				productArray, curDate, p_requestVO.getLocale(), receiverChannelUserVO.getCommissionProfileSetID());

		// /
		// Now load the thresholds VO to validate the user balance (after
		// subtraction of the requested quantity)
		// with the thresholds as
		// 1. first check it with the MAXIMUM PERCENTAGE ALLOWED
		// 2. if not fail at previous point then check it with the MINIMUM
		// RESEDUAL BALANCE
		// /
		final TransferProfileTxnDAO transferProfileTxnDAO = new TransferProfileTxnDAO();
		final ArrayList profileProductList = transferProfileTxnDAO.loadTrfProfileProductWithCntrlValue(con,
				senderVO.getTransferProfileID());
		TransferProfileProductVO transferProfileProductVO = null;
		final ArrayList minProdResidualbalanceList = new ArrayList();
		KeyArgumentVO keyArgumentVO = null;
		ChannelTransferItemsVO channelTransferItemsVO = null;
		int maxAllowPct = 0;
		long maxAllowBalance = 0;
		for (int p = 0, k = profileProductList.size(); p < k; p++) {
			transferProfileProductVO = (TransferProfileProductVO) profileProductList.get(p);
			for (int m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (transferProfileProductVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
					maxAllowPct = transferProfileProductVO.getAllowedMaxPercentageInt();
					maxAllowBalance = (channelTransferItemsVO.getBalance() * maxAllowPct) / 100;
					if (maxAllowBalance < channelTransferItemsVO.getRequiredQuantity()) {
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT);
						args = new String[] { String.valueOf(maxAllowPct), productArray[m + 1],
								channelTransferItemsVO.getRequestedQuantity() };
						keyArgumentVO.setArguments(args);
						minProdResidualbalanceList.add(keyArgumentVO);
					} else if (transferProfileProductVO
							.getMinResidualBalanceAsLong() > (channelTransferItemsVO.getBalance()
									- channelTransferItemsVO.getRequiredQuantity())) {
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS);
						args = new String[] { transferProfileProductVO.getMinBalance(), productArray[m + 1],
								channelTransferItemsVO.getRequestedQuantity() };
						keyArgumentVO.setArguments(args);
						minProdResidualbalanceList.add(keyArgumentVO);
					}
					break;
				}
			} // end of for
		}
		if (minProdResidualbalanceList.size() > 0) {
			final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), minProdResidualbalanceList) };
			p_requestVO.setMessageArguments(array);
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG);
			throw new BTSLBaseException("TransferBL", "processTransfer",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
		}

		UserPhoneVO primaryPhoneVO_S = null;
		if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
			if (!(senderVO.getMsisdn()).equalsIgnoreCase(p_requestVO.getFilteredMSISDN())) {
				senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
				senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
				if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
					primaryPhoneVO_S = userDAO.loadUserAnyPhoneVO(con, senderVO.getPrimaryMsisdn());
				}
			}
			receiverChannelUserVO.setUserCode(receiverUserCode);
		}
		ChannelTransferVO channelTransferVO = prepareTransferProfileVO(senderVO, receiverChannelUserVO,
				productList, curDate);
		channelTransferVO.setActiveUserId(senderVO.getActiveUserID());
		channelTransferVO.setChannelTransferitemsVOList(productList);
		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		channelTransferVO.setToUserID(receiverChannelUserVO.getUserID());
		channelTransferVO.setOtfFlag(true);
		channelTransferVO.setDefaultLang(p_requestVO.getSmsDefaultLang());
		channelTransferVO.setSecondLang(p_requestVO.getSmsSecondLang());
		if(SystemPreferences.OTH_COM_CHNL){
		channelTransferVO.setRequestGatewayCode(authUser.getReqGatewayCode());
		channelTransferVO.setRequestGatewayType(authUser.getReqGatewayType());
		channelTransferVO.setToUserMsisdn(receiverChannelUserVO.getMsisdn());
		}
		if (_log.isDebugEnabled()) {
			_log.debug("process", "Calculate Tax of products Start ");
		}

		ChannelTransferBL.loadAndCalculateTaxOnProducts(con, receiverChannelUserVO.getCommissionProfileSetID(),
				receiverChannelUserVO.getCommissionProfileSetVersion(), channelTransferVO, false, null,
				PretupsI.TRANSFER_TYPE_C2C);

		if (isOutsideHierarchy) {
			channelTransferVO.setControlTransfer(PretupsI.NO);
		} else {
			channelTransferVO.setControlTransfer(PretupsI.YES);
		}

		channelTransferVO.setSource(authUser.getSourceType());
		channelTransferVO.setRequestGatewayCode(authUser.getReqGatewayCode());
		channelTransferVO.setRequestGatewayType(authUser.getReqGatewayType());
		channelTransferVO.setReferenceNum(dataStkTrfMul.getRefnumber());
		channelTransferVO.setCellId(p_requestVO.getCellId());
		channelTransferVO.setSwitchId(p_requestVO.getSwitchId());
		
		if(!fileOrno)
        {
		//Validate MRP && Successive Block for channel transaction
		long successiveReqBlockTime4ChnlTxn = ((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_C2C)).longValue();
		ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(con, channelTransferVO, curDate, successiveReqBlockTime4ChnlTxn);
        }
	      String recLastC2CId="";
            String recLastC2CAmount="";
            String recLastC2CSenderMSISDN="";
            String recLastC2CPostStock="";
            String recLastC2CProductName="";
            Date recLastC2CTime=null ;
            
            boolean lastInFoFlag=false;
            if(SystemPreferences.LAST_C2C_ENQ_MSG_REQ){
                ArrayList transfersList =null;
    	        
            try{
            	int xLastTxn =1;
            	String serviceType="C2C:T";
            	int noDays=SystemPreferences.LAST_X_TRF_DAYS_NO;		//fetch only data for last these days.
            	ChannelTransferDAO channelTransferDAO=new ChannelTransferDAO();
            	transfersList=channelTransferDAO.loadLastXTransfersForReceiver(con,receiverChannelUserVO.getUserID(),xLastTxn, serviceType, noDays);
            	if(transfersList!=null && transfersList.size()>0){
	            	Iterator transfersListIte=transfersList.iterator();
	            	while (transfersListIte.hasNext()) {
	            		C2STransferVO p_c2sTransferVO = (C2STransferVO) transfersListIte.next();
	            		recLastC2CId=p_c2sTransferVO.getTransferID();
	            		recLastC2CAmount=Double.toString(p_c2sTransferVO.getQuantity()/100);
	            		recLastC2CSenderMSISDN=p_c2sTransferVO.getSenderMsisdn();
	            		recLastC2CTime=p_c2sTransferVO.getTransferDate();
	            		recLastC2CProductName=p_c2sTransferVO.getProductName();
	            	}
	            	lastInFoFlag=true;
            	}
            }catch (Exception e) {
            	lastInFoFlag=false;
            	_log.error("process", "Not able to fetch info Exception: "+e.getMessage());
			}
            }
		if (_log.isDebugEnabled()) {
			_log.debug("process", "Start Transfer Process ");
		}

		final Boolean isTagReq = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED))).booleanValue();
		if (isTagReq) {
			final String remarks = dataStkTrfMul.getRemarks();
			channelTransferVO.setChannelRemarks(remarks);
		}

			if(p_requestVO.getPaymentDate()!=null)
			channelTransferVO.setPayInstrumentDate(BTSLUtil.getDateFromDateString((p_requestVO.getPaymentDate())));
			if(p_requestVO.getPaymentType()!=null)
			channelTransferVO.setPayInstrumentType(p_requestVO.getPaymentType());
			if(p_requestVO.getPaymentInstNumber()!=null)
			channelTransferVO.setPayInstrumentNum(p_requestVO.getPaymentInstNumber());
			p_requestVO.setChannelTransferVO(channelTransferVO);
            if(fileOrno)
            {
            	channelTransferVO.setIsFileC2C("Y");
            }
            int level=0;
            if(channelTransferVO.getIsFileC2C().equalsIgnoreCase("Y"))
            {
            	level=(((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.C2C_BATCH_APPROVAL_LEVEL)).intValue());
                
            }
            if(level==0 || channelTransferVO.getIsFileC2C().equals("N"))
            	{
            	final int updateCount = ChnlToChnlTransferTransactionCntrl.approveChannelToChannelTransfer(con,channelTransferVO, isOutsideHierarchy, false, null, curDate);
        		
		
				
		// manisha
		if (!senderVO.isStaffUser()) {
			(senderVO.getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
			(senderVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
		} else {
			(senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
			(senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
		}

		if (updateCount > 0) {
			if (_log.isDebugEnabled()) {
				_log.debug("process", "Commit the data ");
			}
			if (SystemPreferences.LMS_APPL) {
				try {
					if (p_requestVO.isSuccessTxn()) {
						final LoyaltyBL _loyaltyBL = new LoyaltyBL();
						final LoyaltyVO loyaltyVO = new LoyaltyVO();
						PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
						final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
						final ArrayList arr = new ArrayList();
						loyaltyVO.setModuleType(PretupsI.C2C_MODULE);
						loyaltyVO.setServiceType(PretupsI.C2C_MODULE);
						loyaltyVO.setTransferamt(channelTransferVO.getReceiverCrQty());
						loyaltyVO.setCategory(channelTransferVO.getCategoryCode());
						loyaltyVO.setFromuserId(channelTransferVO.getFromUserID());
						loyaltyVO.setTouserId(channelTransferVO.getToUserID());
						loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
						loyaltyVO.setTxnId(channelTransferVO.getTransferID());
						loyaltyVO.setCreatedOn(channelTransferVO.getCreatedOn());
						loyaltyVO.setSenderMsisdn(senderVO.getMsisdn());
						loyaltyVO.setReciverMsisdn(p_requestVO.getReceiverMsisdn());
						loyaltyVO.setProductCode(channelTransferVO.getProductCode());
						arr.add(loyaltyVO.getFromuserId());
						arr.add(loyaltyVO.getTouserId());
						promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
						loyaltyVO.setSetId(promotionDetailsVO.get_setId());
						loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

						if ((loyaltyVO.getSetId() == null) && (loyaltyVO.getToSetId() == null)) {
							_log.error("process", "Exception during LMS Module.SetId not found");
						} else {
							_loyaltyBL.distributeLoyaltyPoints(PretupsI.C2C_MODULE,
									channelTransferVO.getTransferID(), loyaltyVO);
						}
					}
				} catch (Exception ex) {
					_log.error("process", "Exception durign LMS Module " + ex.getMessage());
					_log.errorTrace("TransferBL", ex);
				}

			}
			PushMessage pushMessage= null;
			
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			if(channelTransferVO.getIsFileC2C().equals("N"))
			{
				if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_NEW)) {				
			
					sendEmailNotificationTrf(con, "C2CTRFAPR1",channelTransferVO.getTransferID() ,"c2c.transfer.initiate.email.notification");	
					  String smsKey = PretupsErrorCodesI.C2C_TRANSFER_APPROVAL;
						final String[] array1 = {channelTransferVO.getTransferID() };
						BTSLMessages messages1 = new BTSLMessages(smsKey, array1);
						String msisdns = channelTransferDAO.getMsisdnOfApprovers(con, "C2CTRFAPR1", senderVO.getUserID());
						if(!BTSLUtil.isNullString(msisdns))
						{
							String[] arrSplit = msisdns.split(",");
							for(int l=0;l<arrSplit.length;l++)
							{
								String msisdn1 = arrSplit[l];
								pushMessage = new PushMessage(msisdn1, messages1,channelTransferVO.getTransferID(),"",p_requestVO.getLocale(),p_requestVO.getNetworkCode());
								pushMessage.push();
							}
						}
						p_requestVO.setMessageArguments(array1);
						p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL);
						p_requestVO.setTransactionID(channelTransferVO.getTransferID());
						return ;
				
			}
			if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
				try {
					/*Commented for  connection leak issue
					 * if (mcomCon == null) {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}*/
					PretupsBL.chkAllwdStatusToBecomeActive(con, SystemPreferences.TXN_SENDER_USER_STATUS_CHANG,
							senderVO.getUserID(), senderVO.getStatus());
					PretupsBL.chkAllwdStatusToBecomeActive(con, SystemPreferences.TXN_RECEIVER_USER_STATUS_CHANG,
							receiverChannelUserVO.getUserID(), receiverChannelUserVO.getStatus());
				} catch (Exception ex) {
					_log.error("process", "Exception while changing user state to active  " + ex.getMessage());
					_log.errorTrace("TransferBL", ex);
				} 
			} 
			//Added for OneLine Logging for Channel
			OneLineTXNLog.log(channelTransferVO, null);
			ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
			if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
			String smsKey = PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER;
			if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(senderVO.getTransferCategory())) {
				smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_RECEIVER_AGENT;
			}

			final String recAlternetGatewaySMS = BTSLUtil
					.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
			String reqruestGW = p_requestVO.getRequestGatewayCode();
			if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
				if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
					reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
					if (_log.isDebugEnabled()) {
						_log.debug("process: Reciver Message push through alternate GW", reqruestGW,
								"Requested GW was:" + p_requestVO.getRequestGatewayCode());
					}
				}
			}

			
		    if(_receiverMessageSendReq)
		    {
		    final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con,
					channelTransferVO, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY,
					PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
			final String[] array1 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
					BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO.getTransferID(),
					PretupsBL.getDisplayAmount(channelTransferItemsVO.getNetPayableAmount()),
					p_requestVO.getFilteredMSISDN() };
			BTSLMessages messages = new BTSLMessages(smsKey, array1);
			 pushMessage = new PushMessage(phoneVO.getMsisdn(), messages,
					channelTransferVO.getTransferID(), reqruestGW, locale, channelTransferVO.getNetworkCode());
			pushMessage.push();

			if (PrimaryPhoneVO_R != null) {
				locale = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(), PrimaryPhoneVO_R.getCountry());
				final String[] array2 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
						BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO.getTransferID(),
						PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),
						p_requestVO.getFilteredMSISDN() };
				messages = new BTSLMessages(smsKey, array2);
				pushMessage = new PushMessage(receiverChannelUserVO.getPrimaryMsisdn(), messages,
						channelTransferVO.getTransferID(), reqruestGW, locale, channelTransferVO.getNetworkCode());
				pushMessage.push();
			}
			}
            if(_ussdReceiverMessageSendReq)
            {/*    USSDPushMessage ussdPushMessage= null;
            
            	Object []smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con,channelTransferVO,PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY,PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
            	String[] array1= {BTSLUtil.getMessage(locale,(ArrayList)smsListArr[0]),BTSLUtil.getMessage(locale,(ArrayList)smsListArr[1]),channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),p_requestVO.getFilteredMSISDN()};
            	BTSLMessages messages=new BTSLMessages(smsKey,array1);
            	ussdPushMessage=new USSDPushMessage(phoneVO.getMsisdn(),messages,channelTransferVO.getTransferID(),reqruestGW,locale,channelTransferVO.getNetworkCode()); 
            	ussdPushMessage.push();
            	if(PrimaryPhoneVO_R != null)
            	{
            		locale = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(),PrimaryPhoneVO_R.getCountry());
            		String[]array2= {BTSLUtil.getMessage(locale,(ArrayList)smsListArr[0]),BTSLUtil.getMessage(locale,(ArrayList)smsListArr[1]),channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),p_requestVO.getFilteredMSISDN()};
            		messages=new BTSLMessages(smsKey,array2);
            		ussdPushMessage=new USSDPushMessage(receiverChannelUserVO.getPrimaryMsisdn(),messages,channelTransferVO.getTransferID(),reqruestGW,locale,channelTransferVO.getNetworkCode()); 
            		ussdPushMessage.push();
            	}

			*/}
			 
			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			final ArrayList txnList = new ArrayList();
			final ArrayList balList = new ArrayList();
			args = null;
			if (senderVO.isStaffUser()
					&& senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn())) {
				smsKey = PretupsErrorCodesI.CHNL_TRF_SUCCESS_STAFF;
			} else
			{
				if(lastInFoFlag){
					smsKey=PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_LASTTXN;
				}else{
					smsKey=PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS;
				}
			}
			if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(channelTransferVO.getTransferCategory())) {
				smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_SENDER_AGENT;
			}

			final int lSize = itemsList.size();
			for (int y = 0; y < lSize; y++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(y);
				keyArgumentVO = new KeyArgumentVO();
				keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY);
				args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()),
						channelTransferItemsVO.getRequestedQuantity() };
				keyArgumentVO.setArguments(args);
				txnList.add(keyArgumentVO);

				 if(SystemPreferences.LAST_C2C_ENQ_MSG_REQ && lastInFoFlag){
             		try{
             			recLastC2CPostStock=Double.toString(channelTransferItemsVO.getReceiverPostStock()/100);
             		}catch (Exception e) {
             			lastInFoFlag=false;
             			_log.error("process", "Not able to convert post stock info Exception: "+e.getMessage());
             		}
             }

				keyArgumentVO = new KeyArgumentVO();
				keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
				if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
					args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()),
							PretupsBL.getDisplayAmount(channelTransferItemsVO.getAfterTransSenderPreviousStock()
									- channelTransferItemsVO.getRequiredQuantity()) };
				} else {
					args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()),
							PretupsBL.getDisplayAmount(channelTransferItemsVO.getTotalSenderBalance()
									- channelTransferItemsVO.getRequiredQuantity()) };
				}
				keyArgumentVO.setArguments(args);
				balList.add(keyArgumentVO);
			} // end of for
			String[] array = null;
			if(SystemPreferences.LAST_C2C_ENQ_MSG_REQ && lastInFoFlag){
				String dateString="";
				try{
					dateString=BTSLUtil.getDateStringFromDate(recLastC2CTime);
				}catch (Exception e) {
					dateString="";
				}
				if(senderVO.isStaffUser() && senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn()))
				{
					array= new String[]{BTSLUtil.getMessage(p_requestVO.getLocale(),txnList),BTSLUtil.getMessage(p_requestVO.getLocale(),balList),channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),phoneVO.getMsisdn(),senderVO.getStaffUserDetails().getUserName(),recLastC2CPostStock,recLastC2CSenderMSISDN,recLastC2CId,recLastC2CAmount,dateString,recLastC2CProductName};
				}
				else{
					array= new String[]{BTSLUtil.getMessage(p_requestVO.getLocale(),txnList),BTSLUtil.getMessage(p_requestVO.getLocale(),balList),channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),phoneVO.getMsisdn(),recLastC2CPostStock,recLastC2CSenderMSISDN,recLastC2CId,recLastC2CAmount,dateString,recLastC2CProductName};
				}
			}else{
				if (senderVO.isStaffUser()
						&& senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn())) {
					array = new String[] { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList),
							BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO.getTransferID(),
							PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(),
							senderVO.getStaffUserDetails().getUserName() };
				} else {
					array = new String[] { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList),
							BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO.getTransferID(),
							PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn() };
				}
			}
			if(p_requestVO.getRequestMap()!= null)
            {	
			p_requestVO.getRequestMap().put("USERID2",receiverChannelUserVO.getUserID() );
			p_requestVO.getRequestMap().put("PREBAL", PretupsBL.getDisplayAmount(PretupsBL.getSystemAmount(args[1]) + channelTransferItemsVO.getRequiredQuantity()));
            p_requestVO.getRequestMap().put("POSTBAL", args[1]);
			p_requestVO.getRequestMap().put("PREBAL2", PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()));
            p_requestVO.getRequestMap().put("POSTBAL2", PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()+channelTransferItemsVO.getRequiredQuantity()));
			  p_requestVO.getRequestMap().put("AMOUNT", PretupsBL.getDisplayAmount(channelTransferItemsVO.getRequiredQuantity()));
            }
			
			p_requestVO.setMessageArguments(array);
			p_requestVO.setMessageCode(smsKey);
			p_requestVO.setTransactionID(channelTransferVO.getTransferID());
			_allowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2C_SEN_MSG_REQD_GW"));
			if (senderVO.isStaffUser()
					&& (!senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn())
							|| p_requestVO.getMessageGatewayVO().getGatewayType()
									.equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS))) {
				final Locale parentLocale = new Locale(senderVO.getUserPhoneVO().getPhoneLanguage(),
						senderVO.getUserPhoneVO().getCountry());
				final String[] arrMsg = { BTSLUtil.getMessage(parentLocale, txnList),
						BTSLUtil.getMessage(parentLocale, balList), channelTransferVO.getTransferID(),
						PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(),
						senderVO.getStaffUserDetails().getUserName() };
				final String senderMessage = BTSLUtil.getMessage(parentLocale,
						PretupsErrorCodesI.CHNL_TRF_SUCCESS_STAFF, arrMsg);
				pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage,
						p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), parentLocale);
				pushMessage.push();
			}
			if (SystemPreferences.SECONDARY_NUMBER_ALLOWED && SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
				if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(p_requestVO.getServiceType())
						&& p_requestVO.isSenderMessageRequired()) {

					if (primaryPhoneVO_S != null) {
						final Locale locale1 = new Locale(primaryPhoneVO_S.getPhoneLanguage(),
								primaryPhoneVO_S.getCountry());
						final String senderMessage = BTSLUtil.getMessage(locale1, p_requestVO.getMessageCode(),
								p_requestVO.getMessageArguments());
						pushMessage = new PushMessage(senderVO.getPrimaryMsisdn(), senderMessage,
								p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), locale1);
						pushMessage.push();
					}
				}
			}
			if (BTSLUtil.isStringIn(p_requestVO.getRequestGatewayCode(), _allowedSendMessGatw)) {
				final String senderMessage = BTSLUtil.getMessage(p_requestVO.getLocale(),
						p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
				pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage,
						p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
						p_requestVO.getLocale());
				pushMessage.push();
				p_requestVO.setSenderMessageRequired(false);
			}
			int messageLength = 0;
			final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
			if (!BTSLUtil.isNullString(messLength)) {
				messageLength = (new Integer(messLength)).intValue();
			}
			if (!reqruestGW.equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				final String senderMessage = BTSLUtil.getMessage(p_requestVO.getLocale(),
						p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
				pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage,
						p_requestVO.getRequestIDStr(), reqruestGW, p_requestVO.getLocale());
				if ((messageLength > 0) && (senderMessage.length() < messageLength)) {
					pushMessage.push();
				}
			}
			
			return ;
			
		}
		}
            	}
            	}
	
	}
	private static ChannelTransferVO prepareTransferProfileVO(ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO,
			ArrayList p_productList, Date p_curDate) throws BTSLBaseException {

		if (_log.isDebugEnabled()) {
			_log.debug("prepareTransferProfileVO", " Entered  p_senderVO: " + p_senderVO + " p_receiverVO:"
					+ p_receiverVO + " p_productList:" + p_productList.size() + " p_curDate:" + p_curDate);
		}

		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

		channelTransferVO.setNetworkCode(p_senderVO.getNetworkID());
		channelTransferVO.setNetworkCodeFor(p_senderVO.getNetworkID());
		channelTransferVO.setGraphicalDomainCode(p_senderVO.getGeographicalCode());
		channelTransferVO.setDomainCode(p_senderVO.getDomainID());
		channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
		channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
		channelTransferVO.setReceiverGradeCode(p_receiverVO.getUserGrade());
		channelTransferVO.setFromUserID(p_senderVO.getUserID());
		channelTransferVO.setToUserID(p_receiverVO.getUserID());
		channelTransferVO.setTransferDate(p_curDate);
		channelTransferVO.setCommProfileSetId(p_receiverVO.getCommissionProfileSetID());
		channelTransferVO.setCommProfileVersion(p_receiverVO.getCommissionProfileSetVersion());
		channelTransferVO.setDualCommissionType(p_receiverVO.getDualCommissionType());
		channelTransferVO.setCreatedOn(p_curDate);
		channelTransferVO.setCreatedBy(p_senderVO.getActiveUserID());
		channelTransferVO.setModifiedOn(p_curDate);
		channelTransferVO.setModifiedBy(p_senderVO.getActiveUserID());
		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		channelTransferVO.setTransferInitatedBy(p_senderVO.getUserID());
		channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
		channelTransferVO.setReceiverTxnProfile(p_receiverVO.getTransferProfileID());
		channelTransferVO.setReceiverCategoryCode(p_receiverVO.getCategoryCode());
		channelTransferVO.setSenderCategory(p_senderVO.getCategoryCode());
		channelTransferVO.setTransferCategory(p_senderVO.getTransferCategory());
		
		channelTransferVO.setReceiverGgraphicalDomainCode(p_receiverVO.getGeographicalCode());
		channelTransferVO.setReceiverDomainCode(p_receiverVO.getDomainID());
		channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
		channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getMsisdn()));

		ChannelTransferItemsVO channelTransferItemsVO = null;
		long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
		long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
		String productCode = null;
		for (int i = 0, k = p_productList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(i);
			totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
			if (PretupsI.COMM_TYPE_POSITIVE.equals(p_receiverVO.getDualCommissionType())) {
				totMRP += (channelTransferItemsVO.getReceiverCreditQty())
						* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
			} else {
				totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity())
						* channelTransferItemsVO.getUnitValue());
			}
			totPayAmt += channelTransferItemsVO.getPayableAmount();
			totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
			totTax1 += channelTransferItemsVO.getTax1Value();
			totTax2 += channelTransferItemsVO.getTax2Value();
			totTax3 += channelTransferItemsVO.getTax3Value();
			commissionQty += channelTransferItemsVO.getCommQuantity();
			senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
			receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
			productCode = channelTransferItemsVO.getProductCode();
		} // end of for
		channelTransferVO.setRequestedQuantity(totRequestQty);
		channelTransferVO.setTransferMRP(totMRP);
		channelTransferVO.setPayableAmount(totPayAmt);
		channelTransferVO.setNetPayableAmount(totNetPayAmt);
		channelTransferVO.setTotalTax1(totTax1);
		channelTransferVO.setTotalTax2(totTax2);
		channelTransferVO.setTotalTax3(totTax3);
		channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
		channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
		channelTransferVO.setProductCode(productCode);
		channelTransferVO.setChannelTransferitemsVOList(p_productList);
		channelTransferVO.setActiveUserId(p_senderVO.getActiveUserID());
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()) {
			ArrayList<ChannelSoSVO> chnlSoSVOList = new ArrayList<>();
			chnlSoSVOList.add(new ChannelSoSVO(p_senderVO.getUserID(), p_senderVO.getMsisdn(),
					p_senderVO.getSosAllowed(), p_senderVO.getSosAllowedAmount(), p_senderVO.getSosThresholdLimit()));
			chnlSoSVOList.add(
					new ChannelSoSVO(p_receiverVO.getUserID(), p_receiverVO.getMsisdn(), p_receiverVO.getSosAllowed(),
							p_receiverVO.getSosAllowedAmount(), p_receiverVO.getSosThresholdLimit()));
			channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("prepareTransferProfileVO", " Exited  ");
		}

		return channelTransferVO;
	}// end of prepareTransferProfileVO
	
	private static void sendEmailNotificationTrf(Connection p_con,String p_roleCode,String transferID,String p_subject) {
		final String methodName = "sendEmailNotificationTrf";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		try {
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = "";
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			String to = channelTransferDAO.getEmailIdOfApprover(p_con, p_roleCode, "");
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String messages = null;
			subject =PretupsRestUtil.getMessageString(p_subject);
			messages = PretupsRestUtil.getMessageString("c2c.transfer.initiate.email.notification.content") + " " + transferID +PretupsRestUtil.getMessageString("c2c.transfer.initiate.email.notification.content1");
			if (!BTSLUtil.isNullString(p_roleCode)) {
				EMailSender.sendMail(to, "", bcc, cc, subject, messages, isAttachment, pathofFile, fileNameTobeDisplayed);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT ", messages);
			}
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error("sendEmailNotificationTrf ", " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting ....");
		}
	}
	public static Boolean C2CValidate(Connection con,DataStockMul dataStkTrfMul,ChannelUserVO senderVO,RowErrorMsgLists rowErrorMsgListsData, Boolean fileOrno) throws Exception {
    	MasterErrorList masterErrorListData = new MasterErrorList();
    	
    	ArrayList<MasterErrorList> masterErrorListsData = new ArrayList<MasterErrorList>();
    	
		//Locale locale = new Locale(senderVO.getUserPhoneVO().getPhoneLanguage(), senderVO.getUserPhoneVO().getCountry());
    	Locale locale =new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		Boolean error = false;
		if(BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2()) && BTSLUtil.isNullString(dataStkTrfMul.getExtcode2()) && BTSLUtil.isNullString(dataStkTrfMul.getLoginid2())){
			error=true;
			masterErrorListData.setErrorCode(PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS);
			masterErrorListData.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, null));
			masterErrorListsData.add(masterErrorListData);
			rowErrorMsgListsData.setMasterErrorList(masterErrorListsData);
			/*throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, 0, null);*/
		}
    	
        RowErrorMsgList rowErrorMsgListPayment = new RowErrorMsgList();
       
    	ArrayList<RowErrorMsgLists> rowErrorMsgLists1Payment= new ArrayList<RowErrorMsgLists>();
    	if(!fileOrno)
		{
    		for(int i=0;i<dataStkTrfMul.getPaymentDetails().size();i++)
		
		{
			int row=i+1;
			RowErrorMsgLists rowErrorMsgListsPayment = new RowErrorMsgLists();
			rowErrorMsgListsPayment.setRowValue(String.valueOf(row));
			rowErrorMsgListsPayment.setRowName("Pay"+row);
			ArrayList<MasterErrorList> masterErrorLists1Payment = new ArrayList<MasterErrorList>();
			if(BTSLUtil.isNullString(dataStkTrfMul.getPaymentDetails().get(0).getPaymenttype())){
				error=true;
				MasterErrorList masterErrorListssPayment = new MasterErrorList();
				masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
				masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, new String[] {XmlTagValueConstant.TAG_PAYMENTTYPE}));
				masterErrorLists1Payment.add(masterErrorListssPayment);
			/*throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);*/
		    }
			else
			{
					if(!BTSLUtil.isPaymentTypeValid(dataStkTrfMul.getPaymentDetails().get(0).getPaymenttype())){
						MasterErrorList masterErrorListssPayment = new MasterErrorList();
						masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
						masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, new String[] {XmlTagValueConstant.TAG_PAYMENTTYPE}));
						masterErrorLists1Payment.add(masterErrorListssPayment);
					}
			}
		if(!BTSLUtil.isNullString(dataStkTrfMul.getPaymentDetails().get(0).getPaymenttype()) && !(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(dataStkTrfMul.getPaymentDetails().get(0).getPaymenttype())))
		{
			if(BTSLUtil.isNullString(dataStkTrfMul.getPaymentDetails().get(0).getPaymentinstnumber()))
			{
				error=true;
				MasterErrorList masterErrorListssPayment = new MasterErrorList();
				masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK, new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER}));
				masterErrorLists1Payment.add(masterErrorListssPayment);
				
				/*p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER});
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.EXTSYS_BLANK, 0, null);*/
			}
		}
		
		if(BTSLUtil.isNullString(dataStkTrfMul.getPaymentDetails().get(0).getPaymentdate()))
		{
			error=true;
			MasterErrorList masterErrorListssPayment = new MasterErrorList();
			masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.DATE_FORMAT_INVALID);
			masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DATE_FORMAT_INVALID, null));
			masterErrorLists1Payment.add(masterErrorListssPayment);
		}
		else
		{
			if(!BTSLUtil.isValidDatePattern(dataStkTrfMul.getPaymentDetails().get(0).getPaymentdate()))
			{
				error=true;
				MasterErrorList masterErrorListssPayment = new MasterErrorList();
				masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.DATE_FORMAT_INVALID);
				masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DATE_FORMAT_INVALID, null));
				masterErrorLists1Payment.add(masterErrorListssPayment);
			}
		}
		rowErrorMsgListsPayment.setMasterErrorList(masterErrorLists1Payment);
		rowErrorMsgLists1Payment.add(rowErrorMsgListsPayment);
		}
		}
		rowErrorMsgListPayment.setRowErrorMsgLists(rowErrorMsgLists1Payment);
		
		ArrayList<RowErrorMsgLists> rowErrorMsgLists2Products = new ArrayList<RowErrorMsgLists>();
		RowErrorMsgList rowErrorMsgList2Products = new RowErrorMsgList();
		for(int k=0;k<dataStkTrfMul.getProducts().size();k++)
		{
			int row = dataStkTrfMul.getRow();
			RowErrorMsgLists rowErrorMsgListssProducts = new RowErrorMsgLists();
			rowErrorMsgListssProducts.setRowValue(String.valueOf(row));
			rowErrorMsgListssProducts.setRowName("Products "+row);
			
			ArrayList<MasterErrorList> masterErrorLists1 = new ArrayList<MasterErrorList>();
			if(BTSLUtil.isNullString(dataStkTrfMul.getProducts().get(k).getQty()))
			{
				error=true;
				MasterErrorList masterErrorListss = new MasterErrorList();
				masterErrorListss.setErrorCode(PretupsErrorCodesI.QUANTITY_NULL);
				masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.QUANTITY_NULL, new String[] {String.valueOf(row)}));
				masterErrorLists1.add(masterErrorListss);
			}
			else
			{   
				String qtStr=dataStkTrfMul.getProducts().get(k).getQty();
				Integer qtNum=null;
				try {
			     qtNum=Integer.parseInt(qtStr); 
				}
				catch (NumberFormatException e) {
				}
				if(qtNum==null) {
					error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.QUANTITY_NOT_NUMERIC);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.QUANTITY_NOT_NUMERIC, new String[] {String.valueOf(row)}));
					masterErrorLists1.add(masterErrorListss);
				}
				else if(Long.valueOf(dataStkTrfMul.getProducts().get(k).getQty())<=0)
				{
					error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.QUANTITY_NEGATIVE);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.QUANTITY_NEGATIVE, new String[] {String.valueOf(row)}));
					masterErrorLists1.add(masterErrorListss);
				}
			}
			if(BTSLUtil.isNullString(dataStkTrfMul.getProducts().get(k).getProductcode()))
			{
				error=true;
				MasterErrorList masterErrorListss = new MasterErrorList();
				masterErrorListss.setErrorCode(PretupsErrorCodesI.PRODUCT_NOT_EXIST);
				masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUCT_NOT_EXIST, new String[] {String.valueOf(row)}));
				masterErrorLists1.add(masterErrorListss);
			}
			rowErrorMsgListssProducts.setMasterErrorList(masterErrorLists1);
			rowErrorMsgLists2Products.add(rowErrorMsgListssProducts);
			if(fileOrno)
			{
				ArrayList<MasterErrorList> al= new ArrayList<MasterErrorList>();
				al.addAll(masterErrorLists1);
				al.addAll(masterErrorListsData);
				rowErrorMsgListsData.setMasterErrorList(al);
			}
		}
		
		rowErrorMsgList2Products.setRowErrorMsgLists(rowErrorMsgLists2Products);
		ArrayList<RowErrorMsgList> rowErrorMsgListFinal = new ArrayList<>();
		rowErrorMsgListFinal.add(rowErrorMsgListPayment);
		rowErrorMsgListFinal.add(rowErrorMsgList2Products);
		rowErrorMsgListsData.setRowErrorMsgList(rowErrorMsgListFinal);
		return error;
		
	}
}
