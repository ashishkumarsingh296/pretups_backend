/**
 * @(#)C2CTransferController.java
 *                                Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 * 
 *                                <description>
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                avinash.kamthan Aug 26, 2005 Initital Creation
 *                                Sandeep Goel Nov 26,2005 Modification &
 *                                customization
 *                                Sandeep Goel May 20,2006 Modification &
 *                                customization
 *                                Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 *                                Ashish Kumar May 11,2007 Modification.
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                This is the controller class for the c2c
 *                                transfer. process method of this class is
 *                                called by the channel receiver
 *                                class and response of the processing is send
 *                                back to the channel receiver with the required
 *                                message key.
 */

package com.btsl.pretups.channel.transfer.requesthandler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTrfReqMessage;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChnlToChnlTransferTransactionCntrl;
import com.btsl.pretups.channel.transfer.businesslogic.PaymentDetails;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionCache;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
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
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.util.XmlTagValueConstant;
import com.client.pretups.gateway.businesslogic.USSDPushMessage;
import com.google.gson.Gson;
import com.txn.pretups.channel.profile.businesslogic.TransferProfileTxnDAO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
/**
 * @author avinash.kamthan
 */
public class C2CTransferController implements ServiceKeywordControllerI {

	private static Log _log = LogFactory.getLog(C2CTransferController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	private String _allowedSendMessGatw = null;
	private boolean _receiverMessageSendReq=false;
	private boolean _ussdReceiverMessageSendReq=false;

	/*static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					" C2CTransferController [initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}*/

	public void process(RequestVO p_requestVO) {

		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled()) {
			_log.debug("process", "Entered p_requestVO: " + p_requestVO);
		}
		String _serviceType="";
		final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
		// manisha
		UserPhoneVO userPhoneVO = null;
		if (!senderVO.isStaffUser()) {
			userPhoneVO = senderVO.getUserPhoneVO();
		} else {
			userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
		}
		ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
		if (_log.isDebugEnabled()) {
			_log.debug("process", "Entered Sender VO: " + senderVO);
		}
		final HashMap requestMap = p_requestVO.getRequestMap();
		// ///
		// [keyword][usercode] [qty] [productcode] [qty] [productcode]
		// [password]
		// 1.) if password is PASSWRD the not to validate the user password
		// 2.) usercode should be numeric
		// 3.) product code should be numeric
		// 4.) qty should be numeric
		// 5.) product code and qty should be always with each other
		// 5.1) If order is for more than one product than product code is
		// required for both of the quantity otherwise
		// for the single product order product code can be optional in this
		// case default product will be taken.
		// 6.) load the receiver information on the base of usercode Check
		// the networkcode of sender and receiver user. both should be same
		// 7.) check the transfer rule. whether transfer is allowed between
		// sender category to receiver category.
		// 8.) check the product code existance.
		// 9.) check product associated with the receiver user.
		// 10.) check the min transfer and max transfer value of the selected
		// product
		// 10.) check the receiver max balance for the product/s.
		// 11.) check the sender min residual balance for the product/s.
		// /

		Connection con = null;
		MComConnectionI mcomCon = null;

		ChannelUserTxnDAO channelUserTxnDAO = null;
		final C2CFileUploadService c2CFileUploadService = new C2CFileUploadService();
		final C2CFileUploadVO c2cFileUploadVO = new C2CFileUploadVO();
		CurrencyConversionVO currencyVO;

		try {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        } catch (Exception e) {
	            _log.errorTrace("static", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }
			// /
			// Validate the user is out suspended or not if user is out
			// suspended then show error message
			// /
			if (senderVO != null && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(senderVO.getOutSuspened())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
			}
			_serviceType=p_requestVO.getServiceType();
			_ussdReceiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW,senderVO.getNetworkCode(),_serviceType)).booleanValue();
			_receiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,senderVO.getNetworkCode(),_serviceType)).booleanValue();
			if(PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()) || PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayCode()))
			{
				Gson gson = new Gson();	
				C2CTrfReqMessage resMsg = gson.fromJson(p_requestVO.getRequestMessage(), C2CTrfReqMessage.class);	
				p_requestVO.setRemarks(resMsg.getRemarks());
				PaymentDetails[] paymentDetails = resMsg.getPaymentdetails();	
				p_requestVO.setPaymentDate(paymentDetails[0].getPaymentdate());
				p_requestVO.setPaymentInstNumber(paymentDetails[0].getPaymentinstnumber());
				p_requestVO.setPaymentType(paymentDetails[0].getPaymenttype());
				if(!BTSLUtil.isPaymentTypeValid(p_requestVO.getPaymentType())){
					p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTTYPE});
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);
				}
				if(!BTSLUtil.isNullString(p_requestVO.getPaymentType()) && !(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(p_requestVO.getPaymentType())))
				{
					if(BTSLUtil.isNullString(p_requestVO.getPaymentInstNumber()))
					{
						p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER});
						p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						throw new BTSLBaseException("C2CTransferController", "process",
								PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
					}
				}
				
				
				//added by md.sohail
				//for FileUpload functionality
				c2cFileUploadVO.setFileAttachment(resMsg.getFileAttachment());
				c2cFileUploadVO.setFileName(resMsg.getFileName());
				c2cFileUploadVO.setFileType(resMsg.getFileType());
				c2cFileUploadVO.setFileUploaded(resMsg.getFileUploaded());
			}
			final Date curDate = new Date();
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserTxnDAO = new ChannelUserTxnDAO();
			ChannelUserVO receiverChannelUserVO = null;
			boolean isUserDetailLoad = false;
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserPhoneVO PrimaryPhoneVO_R = null;
			final UserDAO userDAO = new UserDAO();
			UserPhoneVO phoneVO = null;
			boolean receiverAllowed = false;
			final boolean senderAllowed = false;
			final UserStatusVO senderStatusVO = null;
			UserStatusVO receiverStatusVO = null;
			boolean secondaryNumberAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
			boolean messageToPrimaryRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED);
			
			if(PretupsI.MOBILE_APP_GATEWAY.equals(p_requestVO.getRequestGatewayCode()))
			{
				String paymentInstNum = null;
				String paymentInstDate = null;
				String paymentInstCode = null;
				String remarks = null;
				String refNum = null;
				p_requestVO.setReceiverMsisdn(p_requestVO.getRequestMessageArray()[1]);
				/*remarks = (String) requestMap.get("REMARKS");
				refNum = (String) requestMap.get("REFNUM");
				if(!BTSLUtil.isNullString(refNum)){
					p_requestVO.setReferenceNumber(refNum);
				}
				if(BTSLUtil.isNullString(remarks)){
					p_requestVO.setMessageArguments(new String[] {"REMARKS"});
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
				}
				paymentInstNum = (String)requestMap.get("PAYMENTINSTNUM");
				paymentInstDate = (String) requestMap.get("PAYMENTDATE");
				paymentInstCode = (String) requestMap.get("PAYMENTINSTCODE");
				if(BTSLUtil.isNullString(paymentInstDate)){
					p_requestVO.setMessageArguments(new String[] {"PAYMENTDATE"});
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
				}
				if(BTSLUtil.isNullString(paymentInstCode)){
					p_requestVO.setMessageArguments(new String[] {"PAYMENTINSTCODE"});
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
				}
				p_requestVO.setPaymentDate(paymentInstDate);
				p_requestVO.setPaymentInstNumber(paymentInstNum);
				p_requestVO.setPaymentType(paymentInstCode);
				
				if(!BTSLUtil.isPaymentTypeValid(p_requestVO.getPaymentType())){
					p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTTYPE});
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);
				}
				if(!BTSLUtil.isNullString(paymentInstCode) && !(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(paymentInstCode)))
				{
					if(BTSLUtil.isNullString(paymentInstNum))
					{
						p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER});
						p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						throw new BTSLBaseException("C2CTransferController", "process",
								PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
					}
				}
				*/
			}
			if(PretupsI.GATEWAY_TYPE_SMSC.equals(p_requestVO.getRequestGatewayCode()))
			{
				p_requestVO.setReceiverMsisdn((p_requestVO.getRequestMessageArray())[1]);
			}			
			if(BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn()) && BTSLUtil.isNullString(p_requestVO.getReceiverExtCode()) && BTSLUtil.isNullString(p_requestVO.getReceiverLoginID()) && !PretupsI.GATEWAY_TYPE_SMSC.equals(p_requestVO.getRequestGatewayType())){
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, 0, null);
			}
			if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
				receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con,
						p_requestVO.getReceiverExtCode(), null, curDate);
				if (receiverChannelUserVO == null) {
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
				}

				isUserDetailLoad = true;

			} else if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
				receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null,
						p_requestVO.getReceiverLoginID(), curDate);
				if (receiverChannelUserVO == null) {
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
				}

				isUserDetailLoad = true;
			}

			if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
					if (!p_requestVO.getReceiverExtCode().equalsIgnoreCase(receiverChannelUserVO.getExternalCode())) {
						throw new BTSLBaseException("C2CTransferController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
					}
				}
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
					if (!p_requestVO.getReceiverLoginID().equalsIgnoreCase(receiverChannelUserVO.getLoginID())) {

						throw new BTSLBaseException("C2CTransferController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
					}
				}
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())) {
					if (secondaryNumberAllow) {
						phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getReceiverMsisdn());
						if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
							if (messageToPrimaryRequired
									&& ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
								PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
							}
							receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
							receiverChannelUserVO.setMsisdn(p_requestVO.getReceiverMsisdn());
						} else {
							throw new BTSLBaseException("C2CTransferController", "process",
									PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
						}
					} else if (!p_requestVO.getReceiverMsisdn().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
						throw new BTSLBaseException("C2CTransferController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
					}
				}
				String chnlPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
				// To set the msisdn in the request message array...
				if (BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())&& BTSLUtil.isNullString(chnlPlainSmsSeparator)) {
					final String message[] = p_requestVO.getRequestMessageArray();
					final String[] newMessageArr = new String[message.length + 1];
					for (int j = 0; j < newMessageArr.length - 1; j++) {
						newMessageArr[j] = message[j];
					}
					for (int i = newMessageArr.length; i > 0; i--) {
						String temp;
						if (i < newMessageArr.length - 1) {
							temp = newMessageArr[i];
							newMessageArr[i + 1] = newMessageArr[i];
							newMessageArr[i] = temp;
						}
					}
					newMessageArr[1] = receiverChannelUserVO.getMsisdn();
					p_requestVO.setRequestMessageArray(newMessageArr);
				} else {
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
			
			String multiCurrencyDetail = null;
			
			if (p_requestVO.getRequestMap() != null && p_requestVO.getRequestMap().get("CURRENCY") != null && ((String)p_requestVO.getRequestMap().get("CURRENCY")).trim().length()!=0)
            {
            	long mult;
            	double temp;
            	double finalValue;
            	String defaultCurrency = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY);
            	currencyVO = (CurrencyConversionVO)CurrencyConversionCache.getObject((String)p_requestVO.getRequestMap().get("CURRENCY"), defaultCurrency, senderVO.getNetworkID());
            	int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
            	mult = amountMultFactor;
            	String prdCode = null;
            	String prdQty = null;
            	
            	for(int count=0;count<productArray.length;count=+2) {            		
            		prdQty = productArray[count];
            		prdCode = productArray[count + 1];

            		temp = (Double.parseDouble(BigDecimal.valueOf(currencyVO.getConversion()).toPlainString())/currencyVO.getMultFactor()) * Double.parseDouble(prdQty) ;
            		finalValue = Math.round(Double.parseDouble(BigDecimal.valueOf(temp * mult).toPlainString()))/(double)mult;
            		multiCurrencyDetail = ((String)p_requestVO.getRequestMap().get("CURRENCY")+":"+Double.parseDouble(BigDecimal.valueOf(currencyVO.getConversion()).toPlainString())/currencyVO.getMultFactor()+":"+prdQty);
            		productArray[count] = String.valueOf(finalValue);
            	}
            }
			
			
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
			
			String requesterMsisdn = p_requestVO.getRequestMSISDN();
			
			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), requesterMsisdn,
					PretupsI.USER_TYPE_SENDER, null)) {
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_SENDER_BAR, 0,
						new String[] { requesterMsisdn }, null);
			}

			if (phoneVO == null) {
				phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
			}
			if (!isUserDetailLoad) {
				if (!secondaryNumberAllow) {
					receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
							true, curDate, false);
				} else {
					if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
						receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,
								phoneVO.getUserId(), false, curDate, false);
						if (messageToPrimaryRequired) {
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
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(receiverChannelUserVO.getStatus())) {
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
			final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getLocale());
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
			for (int i = 0, k = profileProductList.size(); i < k; i++) {
				transferProfileProductVO = (TransferProfileProductVO) profileProductList.get(i);
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
				throw new BTSLBaseException(this, "processTransfer",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
			}

			UserPhoneVO primaryPhoneVO_S = null;
			if (secondaryNumberAllow) {
				if (!(senderVO.getMsisdn()).equalsIgnoreCase(p_requestVO.getFilteredMSISDN())) {
					senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
					senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
					if (messageToPrimaryRequired) {
						primaryPhoneVO_S = userDAO.loadUserAnyPhoneVO(con, senderVO.getPrimaryMsisdn());
					}
				}
				receiverChannelUserVO.setUserCode(receiverUserCode);
			}
			ChannelTransferVO channelTransferVO = this.prepareTransferProfileVO(senderVO, receiverChannelUserVO,
					productList, curDate);
			channelTransferVO.setActiveUserId(senderVO.getActiveUserID());
			channelTransferVO.setChannelTransferitemsVOList(productList);
			channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			channelTransferVO.setToUserID(receiverChannelUserVO.getUserID());
			channelTransferVO.setOtfFlag(true);
			channelTransferVO.setPayInstrumentType(p_requestVO.getPaymentType());
			boolean othComChnl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
			if(othComChnl){
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setToUserMsisdn(receiverChannelUserVO.getMsisdn());
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", "Calculate Tax of products Start ");
			}

			ChannelTransferBL.loadAndCalculateTaxOnProducts(con, receiverChannelUserVO.getCommissionProfileSetID(),
					receiverChannelUserVO.getCommissionProfileSetVersion(), channelTransferVO, false, null,
					PretupsI.TRANSFER_TYPE_C2C);
			
			this.setAmountsAfterCalculation(channelTransferVO, channelTransferVO.getChannelTransferitemsVOList(), receiverChannelUserVO);
			
			if (isOutsideHierarchy) {
				channelTransferVO.setControlTransfer(PretupsI.NO);
			} else {
				channelTransferVO.setControlTransfer(PretupsI.YES);
			}

			channelTransferVO.setSource(p_requestVO.getSourceType());
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setReferenceNum(p_requestVO.getReferenceNumber());
			channelTransferVO.setCellId(p_requestVO.getCellId());
			channelTransferVO.setSwitchId(p_requestVO.getSwitchId());
			
			
			
			
            // adding check based on system preference to make ext ref number mandatory for particular gateway codes
            
            //start here
			String extTxnNumber = p_requestVO.getExternalReferenceNum();
            if(_log.isDebugEnabled()) {
            	_log.debug(METHOD_NAME, "Preference code EXTREFNUM_MANDATORY_GATEWAYS=" + SystemPreferences.EXTREFNUM_MANDATORY_GATEWAYS + " ,Request Gateway code" + p_requestVO.getRequestGatewayCode());
            }
            String extrefnumMandaoryGateways[] = null;
            boolean extrefMandatoryCheck = false;
            if(SystemPreferences.EXTREFNUM_MANDATORY_GATEWAYS !=null) {
            	extrefnumMandaoryGateways = SystemPreferences.EXTREFNUM_MANDATORY_GATEWAYS.split(",");
                for(String code: extrefnumMandaoryGateways) {
                	if(code.equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
                		extrefMandatoryCheck = true;
                	}
                }
            
            }
            

            
            if(extrefMandatoryCheck) {
                if(_log.isDebugEnabled()) {
                	_log.debug(METHOD_NAME, "Ext number=" + extTxnNumber);
                }
                
                if(BTSLUtil.isNullString(extTxnNumber)) {
                	throw new BTSLBaseException(ChannelTransferBL.class, METHOD_NAME, PretupsErrorCodesI.ERROR_EXT_TXN_NO_BLANK);
                }
                
                if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
                    final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                    final boolean isExternalTxnExists = channelTransferDAO.isExtTxnExistsC2C(con, extTxnNumber);
                    if (isExternalTxnExists) {
                        throw new BTSLBaseException(ChannelTransferBL.class, METHOD_NAME, PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_UNIQUE);
                    }

                }
                
                channelTransferVO.setExternalTxnNum(extTxnNumber);
            }
            

            
            // 
			
			
			
			//Validate MRP && Successive Block for channel transaction
			long successiveReqBlockTime4ChnlTxn = ((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_C2C)).longValue();
			ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(con, channelTransferVO, curDate, successiveReqBlockTime4ChnlTxn);
			
		      String recLastC2CId="";
	            String recLastC2CAmount="";
	            String recLastC2CSenderMSISDN="";
	            String recLastC2CPostStock="";
	            String recLastC2CProductName="";
	            Date recLastC2CTime=null ;
	            boolean lastC2cEnqMsgReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_C2C_ENQ_MSG_REQ);
	            boolean lastInFoFlag=false;
	            if(lastC2cEnqMsgReq){
	                ArrayList transfersList =null;
	    	        
	            try{
	            	int xLastTxn =1;
	            	String serviceType="C2C:T";
	            	int lastXTrfDaysNo = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DAYS_NO))).intValue();
	            	int noDays=lastXTrfDaysNo;		//fetch only data for last these days.
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
			boolean channelTransferInfoRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
			final Boolean isTagReq = channelTransferInfoRequired;
			if (requestMap != null && isTagReq) {
				final String remarks = (String) requestMap.get("REMARKS");
				channelTransferVO.setChannelRemarks(remarks);
				final String info1 = (String) requestMap.get("INFO1");
				final String info2 = (String) requestMap.get("INFO2");
				channelTransferVO.setInfo1(info1);
				channelTransferVO.setInfo2(info2);
			}
			channelTransferVO.setMultiCurrencyDetail(multiCurrencyDetail);
			channelTransferVO.setChannelRemarks(p_requestVO.getRemarks());
			
			if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(channelTransferVO.getRequestGatewayCode()) || PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()) || PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayCode()))
			{
				if(p_requestVO.getPaymentDate()!=null)
				channelTransferVO.setPayInstrumentDate(BTSLUtil.getDateFromDateString((p_requestVO.getPaymentDate())));
				if(p_requestVO.getPaymentType()!=null)
				channelTransferVO.setPayInstrumentType(p_requestVO.getPaymentType());
				if(p_requestVO.getPaymentInstNumber()!=null)
				channelTransferVO.setPayInstrumentNum(p_requestVO.getPaymentInstNumber());
			}

			
			//added by  md.sohail
			//Added for file upload functionality
			if (PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()) || PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayCode())) {
				c2CFileUploadService.uploadFileToServer( c2cFileUploadVO, channelTransferVO);
			}
			
			final int updateCount = ChnlToChnlTransferTransactionCntrl.approveChannelToChannelTransfer(con,
					channelTransferVO, isOutsideHierarchy, false, null, curDate);
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
				if (mcomCon != null) {
					mcomCon.partialCommit();
				}
				boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
				if (lmsAppl) {
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
						_log.errorTrace(METHOD_NAME, ex);
					}

				}
				PushMessage pushMessage= null;
				ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
				if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_NEW)) {				
						sendEmailNotificationTrf(con, "C2CTRFAPR1",channelTransferVO.getTransferID() ,"c2c.transfer.initiate.email.notification",channelTransferVO.getFromUserID());	
						  String smsKey = PretupsErrorCodesI.C2C_TRANSFER_APPROVAL;
							final String[] array1 = {channelTransferVO.getTransferID() };
							BTSLMessages messages1 = new BTSLMessages(smsKey, array1);
							String msisdns = channelTransferDAO.getMsisdnOfApprovers(con, "C2CTRFAPR1", channelTransferVO.getFromUserID());
							if(!BTSLUtil.isNullString(msisdns))
							{
								String[] arrSplit = msisdns.split(",");
								for(int i=0;i<arrSplit.length;i++)
								{
									String msisdn = arrSplit[i];
									pushMessage = new PushMessage(msisdn, messages1,channelTransferVO.getTransferID(),"",p_requestVO.getLocale(),p_requestVO.getNetworkCode());
									pushMessage.push();
								}
							}
							p_requestVO.setMessageArguments(array1);
							p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL);
							p_requestVO.setTransactionID(channelTransferVO.getTransferID());
							return;
					
				}
				if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
					try {
						/*Commented for  connection leak issue
						 * if (mcomCon == null) {
							mcomCon = new MComConnection();
							con = mcomCon.getConnection();
						}*/
						String txnSenderUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG));
						String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
						PretupsBL.chkAllwdStatusToBecomeActive(con, txnSenderUserStatusChang, senderVO.getUserID(), senderVO.getStatus());
						PretupsBL.chkAllwdStatusToBecomeActive(con, txnReceiverUserStatusChang,	receiverChannelUserVO.getUserID(), receiverChannelUserVO.getStatus());
					} catch (Exception ex) {
						_log.error("process", "Exception while changing user state to active  " + ex.getMessage());
						_log.errorTrace(METHOD_NAME, ex);
					} finally {
						if (mcomCon != null) {
							mcomCon.finalCommit();
						}
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

				Locale locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
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
                {    USSDPushMessage ussdPushMessage= null;
	            
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

    			}
				 
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
				for (int i = 0; i < lSize; i++) {
					channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
					keyArgumentVO = new KeyArgumentVO();
					keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY);
					args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()),
							channelTransferItemsVO.getRequestedQuantity() };
					keyArgumentVO.setArguments(args);
					txnList.add(keyArgumentVO);

					 if(lastC2cEnqMsgReq && lastInFoFlag){
                 		try{
                 			recLastC2CPostStock=Double.toString(channelTransferItemsVO.getReceiverPostStock()/100);
                 		}catch (Exception e) {
                 			lastInFoFlag=false;
                 			_log.error("process", "Not able to convert post stock info Exception: "+e.getMessage());
                 		}
                 }

					keyArgumentVO = new KeyArgumentVO();
					keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
					boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
					if (!userProductMultipleWallet) {
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
				if(lastC2cEnqMsgReq && lastInFoFlag){
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
				if (secondaryNumberAllow && messageToPrimaryRequired) {
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
				return;
			}
			}
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException " + esql);
				_log.errorTrace(METHOD_NAME, esql);
			}
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_USER_TRANSFER);
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} 
			
			catch (SQLException esql) {
				_log.error(METHOD_NAME,"SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + be.getMessage());
			if (be.getMessageList() != null && be.getMessageList().size() > 0) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
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
			_log.errorTrace(METHOD_NAME, be);
			return;
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} 
			
			catch (SQLException esql) {
				_log.error(METHOD_NAME,"SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CTransferController[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			return;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CTransferController#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", " Exited ");
			}
		} // end of finally
	}// end of process

	/**
	 * Method prepareTransferProfileVO This method construct the VO for the Txn
	 * 
	 * @param p_senderVO
	 * @param p_receiverVO
	 * @param p_productList
	 * @param p_curDate
	 * @return ChannelTransferVO
	 * @throws BTSLBaseException
	 */
	private ChannelTransferVO prepareTransferProfileVO(ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO,
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
		String productType = null;
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
			productType = channelTransferItemsVO.getProductType();
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
		channelTransferVO.setProductType(productType);
		channelTransferVO.setChannelTransferitemsVOList(p_productList);
		channelTransferVO.setActiveUserId(p_senderVO.getActiveUserID());
		
	    if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE )) {
	   			ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
	   			if(p_senderVO.getUserLoanVOList()!=null)
	   				userLoanVOList.addAll(p_senderVO.getUserLoanVOList());
	   			if(p_receiverVO.getUserLoanVOList()!=null)
	   				userLoanVOList.addAll(p_receiverVO.getUserLoanVOList());
	   	   			
	   			channelTransferVO.setUserLoanVOList(userLoanVOList);
	   		
	   	} 
		
		
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		if (channelSosEnable) {
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
	
	private void setAmountsAfterCalculation(ChannelTransferVO channelTransferVO , ArrayList p_productList , ChannelUserVO p_receiverVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("setAmountsAfterCalculation", " Entered  channelTransferVO: " + channelTransferVO + " p_receiverVO:"
					+ p_receiverVO + " p_productList:" + p_productList.size());
		}
		ChannelTransferItemsVO channelTransferItemsVO = null;
		long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
		long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
		String productCode = null;
		String productType = null;
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
			productType = channelTransferItemsVO.getProductType();
		} // end of for
		channelTransferVO.setRequestedQuantity(totRequestQty);
		channelTransferVO.setTransferMRP(totMRP);
		channelTransferVO.setPayableAmount(totPayAmt);
		channelTransferVO.setNetPayableAmount(totNetPayAmt);
		channelTransferVO.setTotalTax1(totTax1);
		channelTransferVO.setTotalTax2(totTax2);
		channelTransferVO.setTotalTax3(totTax3);
		channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
		channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
		
		if (_log.isDebugEnabled()) {
			_log.debug("setAmountsAfterCalculation", " Exited  ");
		}
	}
	
	private void sendEmailNotificationTrf(Connection p_con,String p_roleCode,String transferID,String p_subject,String userId) {
		final String methodName = "sendEmailNotificationTrf";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		try {
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = "";
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			String to = channelTransferDAO.getEmailIdOfApprovers(p_con, p_roleCode, userId);
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
	private void sendEmailNotificationCloseTxn(Connection p_con, ChannelTransferVO p_channelTransferVO,String p_roleCode,  String p_subject) {
		final String methodName = "sendEmailNotificationCloseTxn";
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
			
			if(p_channelTransferVO.getFromUserID().equals(p_channelTransferVO.getTransferInitatedBy()))
            {
				String message1 = "<br>" + "<table><tr>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("o2ctranfer.onlinetransfer.label.transferid") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("pretups.c2s.query.c2stransferenquirydetails.label.sendername") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("transferenquiry.c2cenquirytransferview.label.reccat") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.touserid") + "</td>"                                          
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.productname") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("pretups.channeltransfer.enquirytransferlist.label.requestedqty") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.transfer.amount.label") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.net.receiver.qty") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.current.balance") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.first.approval.quantity") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.second.approval.quantity") + "</td>"
                      + " <td style='width: 7%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.third.approval.quantity") + "</td>"
                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("voucherbundle.viewvoucherbundles.label.status") + "</td>"
                      + "</tr>";
                              
                   message1 = message1 + "<tr><td style='width: 5%;'>" + "</td>" + 
                            "<td style='width: 7%;'>" + p_channelTransferVO.getTransferID() + "</td>" +  
                            "<td style='width: 7%;'>" + p_channelTransferVO.getSenderCategory() + "</td>" +  
                            "<td style='width: 7%;'>" + p_channelTransferVO.getFromUserName() + "</td>" + 
                            "<td style='width: 7%;'>" + p_channelTransferVO.getReceiverCategoryDesc() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getToUserName() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getProductName() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getRequestedQuantity() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getTransferMRP() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getNetPayableAmount() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getSenderPostbalance() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getLevelOneApprovedQuantity() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getLevelTwoApprovedQuantity() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getLevelThreeApprovedQuantity() + "</td>" + 
                            "<td style='width: 9%;'>" + p_channelTransferVO.getStatus() + "</td>" +
                             "</tr>";
                        
                        
                  messages = messages + message1 + "</table>";
            }
			else
			{
				String message1 = "<br>" + "<table><tr>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("o2ctranfer.onlinetransfer.label.transferid") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("pretups.c2s.query.c2stransferenquirydetails.label.sendername") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("transferenquiry.c2cenquirytransferview.label.reccat") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.touserid") + "</td>"                                          
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("pretups.c2s.query.staff.label.productname") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("pretups.channeltransfer.enquirytransferlist.label.requestedqty") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.transfer.amount.label") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.net.receiver.qty") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("c2c.transfer.current.balance") + "</td>"
	                      + " <td style='width: 9%;'>"+ PretupsRestUtil.getMessageString("voucherbundle.viewvoucherbundles.label.status") + "</td>"
	                      + "</tr>";
	                              
	                   message1 = message1 + "<tr><td style='width: 5%;'>" + "</td>" + 
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getTransferID() + "</td>" +  
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getSenderCategory() + "</td>" +  
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getFromUserName() + "</td>" + 
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getReceiverCategoryDesc() + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getToUserName() + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getProductName() + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getRequestedQuantity() + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getTransferMRP() + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getNetPayableAmount() + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getRecieverPostBalance()+ "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getStatus() + "</td>" +
	                             "</tr>";
	                        
	                        
	                  messages = messages + message1 + "</table>";
			}
			if (!BTSLUtil.isNullString(p_roleCode)) {
				EMailSender.sendMail(to, "", bcc, cc, subject, messages, isAttachment, pathofFile, fileNameTobeDisplayed);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT ", messages);
			}
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error("sendEmailNotificationCloseTxn ", " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting ....");
		}
	}
}
