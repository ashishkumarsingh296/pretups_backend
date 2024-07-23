package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.json.simple.parser.JSONParser;

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
import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
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
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;


public class C2CTrfApprovalController implements ServiceKeywordControllerI {

	private static Log _log = LogFactory.getLog(C2CTrfApprovalController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	private String _allowedSendMessGatw = null;
	private boolean _receiverMessageSendReq=false;
	private boolean _ussdReceiverMessageSendReq=false;

	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					" C2CTrfApprovalController [initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	
		
		
	
	
	public void process(RequestVO p_requestVO) {


		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled()) {
			_log.debug("process", "Entered p_requestVO: " + p_requestVO);
		}
		String _serviceType="";
		ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
		UserPhoneVO userPhoneVO = null;
		String approverRejector = null;
		if (!senderVO.isStaffUser()) {
			userPhoneVO = senderVO.getUserPhoneVO();
		} else {
			userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
		}
		
		approverRejector = senderVO.getUserID();
		if (_log.isDebugEnabled()) {
			_log.debug("process", "Entered Sender VO: " + senderVO);
		}
		final HashMap requestMap = p_requestVO.getRequestMap();
		Connection con = null;
		MComConnectionI mcomCon = null;

		ChannelTransferDAO channelTransferDAO = null;
		ChannelTransferVO channelTransferVO1 = null;
		UserDAO userDAO = null;
		try {
			if (senderVO != null && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(senderVO.getOutSuspened())) {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayCode())))
				{
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
                            "error.user.transfer.channel.out.suspended", 0, null);
				}
			}
			_serviceType=p_requestVO.getServiceType();
			_ussdReceiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW,senderVO.getNetworkCode(),_serviceType)).booleanValue();
			_receiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,senderVO.getNetworkCode(),_serviceType)).booleanValue();

			
			final Date curDate = new Date();
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			ChannelUserVO receiverChannelUserVO = null;
			boolean isUserDetailLoad = false;
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserPhoneVO PrimaryPhoneVO_R = null;
			UserPhoneVO phoneVO = null;
			boolean receiverAllowed = false;
			UserStatusVO receiverStatusVO = null;
			channelTransferVO1 = new ChannelTransferVO();
			channelTransferDAO = new ChannelTransferDAO();
			userDAO = new UserDAO();
			final String messageArr[] = p_requestVO.getRequestMessageArray();
			final int messageLen = messageArr.length;

			final int msgLen = messageArr.length;
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
			String txnId=null;
			String currentStatus=null;
			String apprRejStatus = null;
			String apprRemarks = null;
			String products = null;
			String refNumber = null;
			JSONParser parser = new JSONParser();	
			ArrayList productQuantityList = new ArrayList();
			HashMap reqMap = p_requestVO.getRequestMap();
			//if(PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()))
			if(PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()) || PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayCode()))
			{
				String reqMessage = p_requestVO.getRequestMessage().replace("{","");
				Gson gson = new Gson();	
				C2CTrfReqMessage resMsg = gson.fromJson(p_requestVO.getRequestMessage(), C2CTrfReqMessage.class);	
				Products[] productsArr = resMsg.getProducts();
				PaymentDetails[] paymentDetails = resMsg.getPaymentdetails();
				reqMessage=reqMessage.replace("}", "");
				String[] values = (reqMessage).split(",");
				for(int i=0;i<values.length;i++)
				{
					String[] values1 = values[i].split(":");
					if(values1[0].equals("\"txnid\""))
					{
						txnId = values1[1];
						txnId = txnId.replace("\"", "");
					}
					
					else if(values1[0].equals("\"currentstatus\""))
					{
						currentStatus = values1[1];
						currentStatus = currentStatus.replace("\"", "");
					}
					
					else if(values1[0].equals("\"status\""))
					{
						apprRejStatus = values1[1];
						apprRejStatus = apprRejStatus.replace("\"", "");
					}
					
					else if(values1[0].equals("\"refnumber\""))
					{
						refNumber = values1[1];
						refNumber = refNumber.replace("\"", "");
					}
					
					else if(values1[0].equals("\"remarks\""))
					{
						apprRemarks = values1[1];
						apprRemarks = apprRemarks.replace("\"", "");
						
					}
				}
				
				channelTransferVO1.setTransferID(txnId);
				Products p = new Products();	
				for(int i=0;i<productsArr.length;i++)	
				{	
					p = productsArr[i];	
					productQuantityList.add(p.getQty());	
					productQuantityList.add(p.getProductcode());	
				}
				p_requestVO.setPaymentDate(paymentDetails[0].getPaymentdate());
				p_requestVO.setPaymentInstNumber(paymentDetails[0].getPaymentinstnumber());
				p_requestVO.setPaymentType(paymentDetails[0].getPaymenttype());
				p_requestVO.setReferenceNumber(refNumber);
				if(!BTSLUtil.isPaymentTypeValid(p_requestVO.getPaymentType())){
					p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTTYPE});
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);
				}
				if(!BTSLUtil.isNullString(p_requestVO.getPaymentType()) && !(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equals(p_requestVO.getPaymentType())))
				{
					if(BTSLUtil.isNullString(p_requestVO.getPaymentInstNumber()))
					{
						p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER});
						p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						throw new BTSLBaseException("C2CTrfApprovalController", "process",
								PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
					}
				}
			}
			
			else if(PretupsI.GATEWAY_TYPE_SMSC.equals(p_requestVO.getRequestGatewayCode()))
			{
				channelTransferVO1.setTransferID(p_requestVO.getRequestMessageArray()[2]);
				currentStatus = p_requestVO.getRequestMessageArray()[3];
				txnId = channelTransferVO1.getTransferID();
				apprRejStatus= p_requestVO.getRequestMessageArray()[4];
				if((p_requestVO.getRequestMessageArray()).length > 6)
				{
					productQuantityList.add(p_requestVO.getRequestMessageArray()[5]);
					productQuantityList.add(p_requestVO.getRequestMessageArray()[6]);
				}
			}
			else if(PretupsI.MOBILE_APP_GATEWAY.equals(p_requestVO.getRequestGatewayCode())){
				String paymentInstNum = null;
				String paymentInstDate = null;
				String paymentInstCode = null;
				if (messageArr.length < 2) {
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0,
							new String[] { p_requestVO.getActualMessageFormat() }, null);
				}

				if (!BTSLUtil.isNumeric(messageArr[1])) {
					p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
				}

				final String productArray[] = ChannelTransferBL.validateUserProductsFormatForSMS(messageArr, p_requestVO);
				for(int i=0;i<productArray.length;i++)	
				{	
						
					productQuantityList.add(productArray[i]);
					i++;
					productQuantityList.add(productArray[i]);	
				}
				
				txnId = (String) reqMap.get("TRANSFERID");
				apprRemarks = (String) reqMap.get("REMARKS");
				apprRejStatus = (String) reqMap.get("STATUS");
				currentStatus = (String) reqMap.get("CURRENTSTATUS");
				paymentInstNum = (String)reqMap.get("PAYMENTINSTNUM");
				paymentInstDate = (String) reqMap.get("PAYMENTDATE");
				paymentInstCode = (String) reqMap.get("PAYMENTINSTCODE");
				refNumber = (String) reqMap.get("REFNUM");
				
				channelTransferVO1.setTransferID(txnId);
				if(BTSLUtil.isNullString(txnId)){
					p_requestVO.setMessageArguments(new String[] {"TRANSFERID"});
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
				}
				if(BTSLUtil.isNullString(apprRejStatus)){
					p_requestVO.setMessageArguments(new String[] {"STATUS"});
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
				}
				if(BTSLUtil.isNullString(currentStatus)){
					p_requestVO.setMessageArguments(new String[] {"CURRENTSTATUS"});
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
				}
				if(BTSLUtil.isNullString(paymentInstDate)){
					p_requestVO.setMessageArguments(new String[] {"PAYMENTDATE"});
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
				}
				if(BTSLUtil.isNullString(paymentInstCode)){
					p_requestVO.setMessageArguments(new String[] {"PAYMENTINSTCODE"});
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
				}
				p_requestVO.setPaymentDate(paymentInstDate);
				p_requestVO.setPaymentInstNumber(paymentInstNum);
				p_requestVO.setPaymentType(paymentInstCode);
				p_requestVO.setReferenceNumber(refNumber);
				if(!BTSLUtil.isPaymentTypeValid(p_requestVO.getPaymentType())){
					p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTTYPE});
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);
				}
				if(!BTSLUtil.isNullString(paymentInstCode) && !(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(paymentInstCode)))
				{
					if(BTSLUtil.isNullString(paymentInstNum))
					{
						p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER});
						p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						throw new BTSLBaseException("C2CTrfApprovalController", "process",
								PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
					}
				}
			
			}
			else
			{
				channelTransferVO1.setTransferID(p_requestVO.getTransactionID());
				currentStatus = p_requestVO.getCurrentStatus();
				txnId = p_requestVO.getTransactionID();
				apprRejStatus= p_requestVO.getStatus();
				apprRemarks = p_requestVO.getRemarks();
				productQuantityList = p_requestVO.getProductQuantityList();
				if(!BTSLUtil.isNullString(p_requestVO.getPaymentType()) && !(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(p_requestVO.getPaymentType())))
				{
					if(BTSLUtil.isNullString(p_requestVO.getPaymentInstNumber()))
					{
						p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER});
						p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						throw new BTSLBaseException("C2CTrfApprovalController", "process",
								PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
					}
				}
			}
			
			
			channelTransferDAO.loadChannelTransferDetail(con, channelTransferVO1,currentStatus);
			if(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(currentStatus) || PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentStatus))
		    {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL_RECORD_CLOSE_CNCL);
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.C2C_TRF_APPROVAL_RECORD_CLOSE_CNCL, 0, new String[] { currentStatus }, null);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
                            "c2c.trf.approval.record.close.cncl", 0,new String[] { currentStatus }, null);

				}
			}
			if(channelTransferVO1.getNetworkCode() == null)
			{
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL_RECORD_NOT_FOUND);
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.C2C_TRF_APPROVAL_RECORD_NOT_FOUND, 0, new String[] { currentStatus }, null);
				}
				else
				{
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						"c2c.trf.approval.record.not.found", 0, new String[] { currentStatus }, null);
				}
			}
			
			//validate user is allowed to authenticate transaction
			String roleCode = "";
			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentStatus)) {
				roleCode = PretupsI.C2C_TRF_APR1;
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentStatus)) {
				roleCode = PretupsI.C2C_TRF_APR2;
			}  else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentStatus)) {
				roleCode = PretupsI.C2C_TRF_APR3;
			}

			if(!channelTransferDAO.validateUserAllowedForApproval(con, channelTransferVO1.getTransferID(), senderVO.getUserID(), roleCode)){
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
                        PretupsErrorCodesI.C2C_TRF_APPROVAL_NOT_ALLOWED, 0,null, null);
			}

			channelTransferVO1.setApprRejStatus(apprRejStatus);
			if(apprRejStatus.equals(PretupsI.C2C_TRF_APPRV_REJ_STATUS))
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL_REJECT);
				channelTransferVO1.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				channelTransferVO1.setCurStatus(currentStatus);
				channelTransferVO1.setCanceledBy(approverRejector);
				channelTransferVO1.setCanceledOn(new Date());
				p_requestVO.setTransactionID(channelTransferVO1.getTransferID());
				int updateCount1 = channelTransferDAO.updateChannelTransferApprovalLevelThreeC2C(con, channelTransferVO1, false);
				if(updateCount1 > 0)
				{
					mcomCon.finalCommit();
					String smsKey = PretupsErrorCodesI.C2C_TRF_APPROVAL_REJECT;
					final String[] array1 = {channelTransferVO1.getTransferID() };
					BTSLMessages messages1 = new BTSLMessages(smsKey, array1);
					String msisdn = userDAO.retrieveMsisdn(channelTransferVO1.getTransferInitatedBy(), con);
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFICATION))).booleanValue())
					{
						PushMessage pushMessage = new PushMessage(msisdn, messages1, channelTransferVO1.getTransferID(), "", p_requestVO.getLocale(), p_requestVO.getNetworkCode());
						pushMessage.push();
					}
					p_requestVO.setMessageArguments(array1);
					p_requestVO.setMessageCode(smsKey);
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_EMAIL_NOTIFICATION))).booleanValue())
					{
						sendEmailNotificationOnC2CReject(con, channelTransferVO1, "c2c.reject.email.subject");
					}
				}
				return;
			}
			ArrayList<ChannelTransferItemsVO> channelTransferItemsVO1 = channelTransferDAO.loadChannelTransferItems(con, txnId);
			senderVO = channelUserDAO.loadChannelUserDetails(con,channelTransferVO1.getUserMsisdn());
			String productArray[] = new String[channelTransferItemsVO1.size()*2];
			int l = 0;
			int m1 = 0;
			while(l<channelTransferItemsVO1.size())
			{ 
				productArray[m1]=PretupsBL.getDisplayAmount(((ChannelTransferItemsVO) channelTransferItemsVO1.get(l)).getApprovedQuantity());
				++m1;
				productArray[m1] = String.valueOf(((ChannelTransferItemsVO) channelTransferItemsVO1.get(l)).getProductShortCode());
				int indexProductInList = productQuantityList.indexOf(productArray[m1]);	
				if(indexProductInList != -1)	
				{	
					String approvalQty = (productQuantityList.get(indexProductInList-1)).toString();	
					if(Double.parseDouble(approvalQty) > Double.parseDouble(productArray[m1-1]))	
						throw new BTSLBaseException("C2CTrfApprovalController", "process", PretupsErrorCodesI.C2C_APPROVAL_QTY_GREATER_THAN_LAST_APPROVED, 0, new String[] { productArray[m1] }, null);	
					else	
						productArray[m1-1] = approvalQty;	
				}
				++m1;
				++l;
			}
			
			String receiverUserCode = channelTransferVO1.getToUserCode();
			receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
			if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							"error.invalid.rec.usercode");
				}
			}
			final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);
			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			if (networkPrefixVO == null) {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
						new String[] { receiverUserCode }, null);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							"error.user.transfer.channel.unsupported.network", 0,
							new String[] { receiverUserCode }, null);
				}
			}

			final BarredUserDAO barredUserDAO = new BarredUserDAO();

			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode,
					PretupsI.USER_TYPE_RECEIVER, null)) {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
						new String[] { receiverUserCode }, null);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							"error.user.transfer.channel.reciever.bar", 0,
							new String[] { receiverUserCode }, null);	
				}
			}

			if (phoneVO == null) {
				phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
			}
			if (!isUserDetailLoad) {
				if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
					receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
							true, curDate, false);
				} else {
					if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
						receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,
								phoneVO.getUserId(), false, curDate, false);
						if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
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
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTrfApprovalController", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST,
						0, args, null);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process", "error.user.not.exist",
							0, args, null);	
				}
			} else if (receiverChannelUserVO.getInSuspend() != null
					&& PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(receiverChannelUserVO.getInSuspend())) {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED, 0, args, null);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							"error.user.transfer.channel.in.suspended", 0, args, null);	
				}
			} else if (receiverStatusVO == null) {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							"error.userstatus.notconfigured");	
				}
			} else if (!receiverAllowed) {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args, null);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							"chnl.error.receiver.notallowed", 0, args, null);	
				}
			} else if (receiverChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
				{
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTrfApprovalController", "process",
						PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args, null);
				}
				else
				{
					throw new BTSLBaseException("C2CTrfApprovalController", "process",
							"error.user.commission.profile.not.applicable", 0, args, null);	
				}
			}

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

			final boolean isOutsideHierarchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, senderVO,
					receiverChannelUserVO, true, null, false, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);

			final ArrayList productList = ChannelTransferBL.validateReqstProdsWithDefinedProdsForXFR(con, senderVO,
					productArray, curDate, p_requestVO.getLocale(), receiverChannelUserVO.getCommissionProfileSetID());

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
							if ((PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType()))) {
								keyArgumentVO.setKey("error.transfer.allowedmaxpct.isless");
								final String arg[] = { channelTransferItemsVO.getShortName(), String.valueOf(maxAllowPct) };
								keyArgumentVO.setArguments(arg);
								minProdResidualbalanceList.add(keyArgumentVO);
							} else {
								keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT);
								final String arg[] = { String.valueOf(maxAllowPct), String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO
										.getRequestedQuantity() };
								keyArgumentVO.setArguments(arg);
								minProdResidualbalanceList.add(keyArgumentVO);
							}
						} else if (transferProfileProductVO
								.getMinResidualBalanceAsLong() > (channelTransferItemsVO.getBalance()
										- channelTransferItemsVO.getRequiredQuantity())) {
							keyArgumentVO = new KeyArgumentVO();
							if ((PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType()))) {
								keyArgumentVO.setKey("error.transfer.minbalance.reached");
								final String arg[] = { channelTransferItemsVO.getShortName() };
								keyArgumentVO.setArguments(arg);
								minProdResidualbalanceList.add(keyArgumentVO);
							} else {
								keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS);
								args = new String[] { transferProfileProductVO.getMinBalance(), productArray[m + 1],
										channelTransferItemsVO.getRequestedQuantity() };
								keyArgumentVO.setArguments(args);
								minProdResidualbalanceList.add(keyArgumentVO);
							}
						} 
						break;
					}
				}
			}
			final ArrayList profileProductLists = transferProfileTxnDAO.loadTrfProfileProductWithCntrlValue(con,
					receiverChannelUserVO.getTransferProfileID());
			for (int i = 0, k = profileProductLists.size(); i < k; i++) {
				transferProfileProductVO = (TransferProfileProductVO) profileProductLists.get(i);
				for (int m = 0, n = productList.size(); m < n; m++) {
					channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
					if (transferProfileProductVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
						 if(transferProfileProductVO.getMaxBalanceAsLong()<(channelTransferItemsVO.getBalance()
										+ channelTransferItemsVO.getApprovedQuantity()))
						{
							keyArgumentVO = new KeyArgumentVO();
							if ((PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType()))) {
								final String arg[] = { channelTransferItemsVO.getShortName() };
								keyArgumentVO.setArguments(arg);
								keyArgumentVO.setKey("error.transfer.maxbalance.reached");
							} else {
								final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(transferProfileProductVO
										.getMaxBalanceAsLong()) };
								keyArgumentVO.setArguments(arg);
								keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_SUBKEY);
							}
							minProdResidualbalanceList.add(keyArgumentVO);
						}
						break;
					}
				}
			}


			UserPhoneVO primaryPhoneVO_S = null;
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
				if (!(senderVO.getMsisdn()).equalsIgnoreCase(p_requestVO.getFilteredMSISDN())) {
					senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
					senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
						primaryPhoneVO_S = userDAO.loadUserAnyPhoneVO(con, senderVO.getPrimaryMsisdn());
					}
				}
				receiverChannelUserVO.setUserCode(receiverUserCode);
			}
			ChannelTransferVO channelTransferVO = this.prepareTransferProfileVO(currentStatus,senderVO, receiverChannelUserVO,
					productList, curDate);
			for(int i=0;i<channelTransferVO.getChannelTransferitemsVOList().size();i++)
			{
				ChannelTransferItemsVO channelTransferItemsVO2=null;
				channelTransferItemsVO2=(ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(i);
				for(int j=0;j<channelTransferItemsVO1.size();j++)
				{
				if(channelTransferItemsVO1.get(j).getProductCode().equals(channelTransferItemsVO2.getProductCode()))
				{
					if(currentStatus.equals("APPRV1"))
					channelTransferItemsVO2.setFirstApprovedQuantity(channelTransferItemsVO1.get(j).getFirstApprovedQuantity());
					if(currentStatus.equals("APPRV2"))
						channelTransferItemsVO2.setSecondApprovedQuantity(channelTransferItemsVO1.get(j).getSecondApprovedQuantity());
				    break;
				}
				}
			}
			if(channelTransferVO1.getTransferInitatedBy().equals(channelTransferVO1.getFromUserID()))
			{
				channelTransferVO.setTransferInitatedBy(senderVO.getUserID());
			}
			channelTransferVO.setActiveUserId(senderVO.getActiveUserID());
			channelTransferVO.setChannelTransferitemsVOList(productList);
			channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			channelTransferVO.setToUserID(receiverChannelUserVO.getUserID());
			channelTransferVO.setOtfFlag(true);
			channelTransferVO.setTransferID(txnId);
			channelTransferVO.setCurStatus(currentStatus);
			channelTransferVO.setApprRejStatus(apprRejStatus);
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setToUserMsisdn(receiverChannelUserVO.getMsisdn());
			}
			channelTransferVO.setPayInstrumentType(p_requestVO.getPaymentType());
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
			if(!PretupsI.GATEWAY_TYPE_SMSC.equals(channelTransferVO.getRequestGatewayCode()) && !PretupsI.REQUEST_SOURCE_TYPE_USSD.equals(p_requestVO.getRequestGatewayCode()))
			{
				if(p_requestVO.getPaymentDate()!=null)
				channelTransferVO.setPayInstrumentDate(BTSLUtil.getDateFromDateString((p_requestVO.getPaymentDate())));
				if(p_requestVO.getPaymentType()!=null)
				channelTransferVO.setPayInstrumentType(p_requestVO.getPaymentType());
				if(p_requestVO.getPaymentInstNumber()!=null)
				channelTransferVO.setPayInstrumentNum(p_requestVO.getPaymentInstNumber());
			}
		      String recLastC2CId="";
	            String recLastC2CAmount="";
	            String recLastC2CSenderMSISDN="";
	            String recLastC2CPostStock="";
	            String recLastC2CProductName="";
	            Date recLastC2CTime=null ;
	            ChannelTransferDAO channelTransferDAO1=new ChannelTransferDAO();
	            boolean lastInFoFlag=false;
	            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_C2C_ENQ_MSG_REQ))).booleanValue()){
	                ArrayList transfersList =null;
	    	        
	            try{
	            	int xLastTxn =1;
	            	String serviceType="C2C:T";
	            	int noDays=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DAYS_NO))).intValue();		//fetch only data for last these days.
	            	
	            	transfersList=channelTransferDAO1.loadLastXTransfersForReceiver(con,receiverChannelUserVO.getUserID(),xLastTxn, serviceType, noDays);
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
			if (requestMap != null && isTagReq) {
				final String remarks = (String) requestMap.get("REMARKS");
				channelTransferVO.setChannelRemarks(remarks);
				final String info1 = (String) requestMap.get("INFO1");
				final String info2 = (String) requestMap.get("INFO2");
				channelTransferVO.setInfo1(info1);
				channelTransferVO.setInfo2(info2);
			}
			int updateCount = 0 ;
			
			if(channelTransferVO.getTransferInitatedBy().equals(channelTransferVO.getFromUserID()))
			{
				int level=((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_APPROVAL_LEVEL_C2C_TRANSFER, senderVO.getNetworkID(), senderVO.getCategoryCode())).intValue();
				channelTransferVO.setModifiedOn(new Date());
				if(PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentStatus))
				{
					channelTransferVO.setLevelOneApprovedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));
					channelTransferVO.setFirstApprovalRemark(apprRemarks);
					channelTransferVO.setFirstApprovedOn(new Date());
					channelTransferVO.setFirstApprovedBy(approverRejector);
					if(level==1)
					{
						if (minProdResidualbalanceList.size() > 0) {
							final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), minProdResidualbalanceList) };
							p_requestVO.setMessageArguments(array);
							p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG);
							if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
							{
							throw new BTSLBaseException(this, "processTransfer",
									PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
							}
							else
							{
								throw new BTSLBaseException(this, "processTransfer",
										"error.user.transfer.product.residual.balance.less.msg", 0, array, null);
							}
						}
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	            	ChnlToChnlTransferTransactionCntrl.updateUserBalance(con,
	    					channelTransferVO, isOutsideHierarchy, true, null, curDate);
	            	updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelThreeC2C(con,
							channelTransferVO, true);
					}
					else
					{
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
					updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelOneC2C(con,
							channelTransferVO, false);
						
					}
				}
				else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentStatus))
				{
					channelTransferVO.setLevelTwoApprovedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));
					channelTransferVO.setSecondApprovalRemark(apprRemarks);
					channelTransferVO.setSecondApprovedBy(approverRejector);
					channelTransferVO.setSecondApprovedOn(new Date());
					if(level==2)
					{
						if (minProdResidualbalanceList.size() > 0) {
							final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), minProdResidualbalanceList) };
							p_requestVO.setMessageArguments(array);
							p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG);
							if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
							{
							throw new BTSLBaseException(this, "processTransfer",
									PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
							}
							else
							{
								throw new BTSLBaseException(this, "processTransfer",
										"error.user.transfer.product.residual.balance.less.msg", 0, array, null);
							}
						}
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	            	ChnlToChnlTransferTransactionCntrl.updateUserBalance(con,
	    					channelTransferVO, isOutsideHierarchy, true, null, curDate);
	            	updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelThreeC2C(con,
							channelTransferVO, true);
					}
					else
					{
						channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
						updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelTwoC2C(con,
									channelTransferVO, false);
					}
				}
				else
				{
					if (minProdResidualbalanceList.size() > 0) {
						final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), minProdResidualbalanceList) };
						p_requestVO.setMessageArguments(array);
						p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG);
						if(!(PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getSourceType())))
						{
						throw new BTSLBaseException(this, "processTransfer",
								PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
						}
						else
						{
							throw new BTSLBaseException(this, "processTransfer",
									"error.user.transfer.product.residual.balance.less.msg", 0, array, null);
						}
					}
					channelTransferVO.setLevelThreeApprovedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));
					channelTransferVO.setThirdApprovalRemark(apprRemarks);
					channelTransferVO.setThirdApprovedOn(new Date());
					channelTransferVO.setThirdApprovedBy(approverRejector);
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	            	ChnlToChnlTransferTransactionCntrl.updateUserBalance(con,
	    					channelTransferVO, isOutsideHierarchy, true, null, curDate);
	            	updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelThreeC2C(con,
							channelTransferVO, true);
				}
			}
			else if(channelTransferVO.getTransferInitatedBy().equals(channelTransferVO.getToUserID()))
			{
				int level=((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_APPROVAL_LEVEL_C2C_INITIATE, senderVO.getNetworkID(), senderVO.getCategoryCode())).intValue();
				channelTransferVO.setModifiedOn(new Date());
				if(PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentStatus))
				{
					channelTransferVO.setLevelOneApprovedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));
					channelTransferVO.setFirstApprovalRemark(apprRemarks);
					channelTransferVO.setFirstApprovedBy(approverRejector);
					channelTransferVO.setFirstApprovedOn(new Date());
					if(level==1)
					{
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	            	ChnlToChnlTransferTransactionCntrl.updateUserBalance(con,
	    					channelTransferVO, isOutsideHierarchy, false, null, curDate);
	            	updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelThreeC2C(con,
							channelTransferVO, true);
					}
					else
					{
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
					updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelOneC2C(con,
							channelTransferVO, false);
						
					}
				}
				else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentStatus))
				{
					channelTransferVO.setLevelTwoApprovedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));
					channelTransferVO.setSecondApprovalRemark(apprRemarks);
					channelTransferVO.setSecondApprovedBy(approverRejector);
					channelTransferVO.setSecondApprovedOn(new Date());
					if(level==2)
					{
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	            	ChnlToChnlTransferTransactionCntrl.updateUserBalance(con,
	    					channelTransferVO, isOutsideHierarchy, false, null, curDate);
	            	updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelThreeC2C(con,
							channelTransferVO, true);
	            	
					}
					else
					{
						channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
						updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelTwoC2C(con,
									channelTransferVO, false);
					}
				}
				else
				{
					channelTransferVO.setLevelThreeApprovedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));
					channelTransferVO.setThirdApprovalRemark(apprRemarks);
					channelTransferVO.setThirdApprovedOn(new Date());
					channelTransferVO.setThirdApprovedBy(approverRejector);
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	            	ChnlToChnlTransferTransactionCntrl.updateUserBalance(con,
	    					channelTransferVO, isOutsideHierarchy, false, null, curDate);
	            	updateCount = channelTransferDAO1.updateChannelTransferApprovalLevelThreeC2C(con,
							channelTransferVO, true);
				}
			}
          
			
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

				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
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
				if (!(channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE))) {	
					if(channelTransferVO.getCurStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1))
					{
						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_EMAIL_NOTIFICATION))).booleanValue())
							sendEmailNotificationTrf(con, "C2CTRFAPR2",channelTransferVO.getTransferID() ,"c2c.transfer.initiate.email.notification", channelTransferVO);
						String smsKey = PretupsErrorCodesI.C2C_TRANSFER_APPROVAL;
						final String[] array1 = {channelTransferVO.getTransferID() };
						BTSLMessages messages1 = new BTSLMessages(smsKey, array1);
						String msisdns = channelTransferDAO.getMsisdnOfApprovers(con, "C2CTRFAPR2", senderVO.getUserID());
						if(!BTSLUtil.isNullString(msisdns) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFICATION))).booleanValue())
						{
							String[] arrSplit = msisdns.split(",");
							for(int i=0;i<arrSplit.length;i++)
							{
								String msisdn = arrSplit[i];
								pushMessage = new PushMessage(msisdn, messages1, channelTransferVO.getTransferID(), "", p_requestVO.getLocale(), p_requestVO.getNetworkCode());
								pushMessage.push();
							}
						}
					}
					else if(channelTransferVO.getCurStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2))
					{
						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_EMAIL_NOTIFICATION))).booleanValue())
							sendEmailNotificationTrf(con, "C2CTRFAPR3",channelTransferVO.getTransferID() ,"c2c.transfer.initiate.email.notification", channelTransferVO);
						String smsKey = PretupsErrorCodesI.C2C_TRANSFER_APPROVAL;
						final String[] array1 = {channelTransferVO.getTransferID() };
						BTSLMessages messages1 = new BTSLMessages(smsKey, array1);
						String msisdns = channelTransferDAO.getMsisdnOfApprovers(con, "C2CTRFAPR3", senderVO.getUserID());
						if(!BTSLUtil.isNullString(msisdns) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFICATION))).booleanValue())
						{
							String[] arrSplit = msisdns.split(",");
							for(int i=0;i<arrSplit.length;i++)
							{
								String msisdn = arrSplit[i];
								pushMessage = new PushMessage(msisdn, messages1, channelTransferVO.getTransferID(), "", p_requestVO.getLocale(), p_requestVO.getNetworkCode());
								pushMessage.push();
							}
						}
					}
					//Approval level message and email notification.
					final String[] array1 = {channelTransferVO.getTransferID(), channelTransferVO.getStatus() };
					p_requestVO.setMessageArguments(array1);
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL_API);
					p_requestVO.setTransactionID(channelTransferVO.getTransferID());
					return;
			}
				if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
					try {
						if (mcomCon == null) {
							mcomCon = new MComConnection();
							con = mcomCon.getConnection();
						}
						PretupsBL.chkAllwdStatusToBecomeActive(con, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG),
								senderVO.getUserID(), senderVO.getStatus());
						PretupsBL.chkAllwdStatusToBecomeActive(con, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG)),
								receiverChannelUserVO.getUserID(), receiverChannelUserVO.getStatus());
						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_EMAIL_NOTIFICATION))).booleanValue())
							sendEmailNotificationCloseTxn(con, channelTransferVO, null, "c2c.transfer.initiate.email.notification");
					} catch (Exception ex) {
						_log.error("process", "Exception while changing user state to active  " + ex.getMessage());
						_log.errorTrace(METHOD_NAME, ex);
					} finally {
						if (mcomCon != null) {
							mcomCon.finalCommit();
						}
					}
				} 
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
				{

					USSDPushMessage ussdPushMessage = null;

					Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con,
							channelTransferVO, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY,
							PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
					String[] array1 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
							BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO.getTransferID(),
							PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),
							p_requestVO.getFilteredMSISDN() };
					BTSLMessages messages = new BTSLMessages(smsKey, array1);
					ussdPushMessage = new USSDPushMessage(phoneVO.getMsisdn(), messages,
							channelTransferVO.getTransferID(), reqruestGW, locale, channelTransferVO.getNetworkCode());
					ussdPushMessage.push();
					if (PrimaryPhoneVO_R != null) {
						locale = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(), PrimaryPhoneVO_R.getCountry());
						String[] array2 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),
								channelTransferVO.getTransferID(),
								PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),
								p_requestVO.getFilteredMSISDN() };
						messages = new BTSLMessages(smsKey, array2);
						ussdPushMessage = new USSDPushMessage(receiverChannelUserVO.getPrimaryMsisdn(), messages,
								channelTransferVO.getTransferID(), reqruestGW, locale,
								channelTransferVO.getNetworkCode());
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

					 if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_C2C_ENQ_MSG_REQ))).booleanValue() && lastInFoFlag){
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
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_C2C_ENQ_MSG_REQ))).booleanValue() && lastInFoFlag){
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
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
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
					"C2CTrfApprovalController[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			return;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CTrfApprovalController#process");
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
	private ChannelTransferVO prepareTransferProfileVO(String currentStatus,ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO,
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
		channelTransferVO.setCreatedBy(p_senderVO.getUserID());
		channelTransferVO.setModifiedOn(p_curDate);
		channelTransferVO.setModifiedBy(p_senderVO.getUserID());
		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		channelTransferVO.setTransferInitatedBy(p_receiverVO.getUserID());
		channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
		channelTransferVO.setReceiverTxnProfile(p_receiverVO.getTransferProfileID());
		channelTransferVO.setReceiverCategoryCode(p_receiverVO.getCategoryCode());
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
			channelTransferItemsVO.setProductTotalMRP(channelTransferItemsVO.getApprovedQuantity());
			if(currentStatus.equals("NEW"))
				channelTransferItemsVO.setFirstApprovedQuantity(channelTransferItemsVO.getRequestedQuantity());
				if(currentStatus.equals("APPRV1"))
					channelTransferItemsVO.setSecondApprovedQuantity(channelTransferItemsVO.getRequestedQuantity());
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
	
	private void setAmountsAfterCalculation(ChannelTransferVO channelTransferVO , ArrayList p_productList , ChannelUserVO p_receiverVO) throws BTSLBaseException{
		if (_log.isDebugEnabled()) {
			_log.debug("setAmountsAfterCalculation", " Entered  channelTransferVO: " + channelTransferVO + " p_receiverVO:"
					+ p_receiverVO + " p_productList:" + p_productList.size());
		}
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
			channelTransferItemsVO.setProductTotalMRP(channelTransferItemsVO.getApprovedQuantity());
			/*if(currentStatus.equals("NEW"))
				channelTransferItemsVO.setFirstApprovedQuantity(channelTransferItemsVO.getRequestedQuantity());
				if(currentStatus.equals("APPRV1"))
					channelTransferItemsVO.setSecondApprovedQuantity(channelTransferItemsVO.getRequestedQuantity());*/
		}
		
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
	
	private void sendEmailNotificationTrf(Connection p_con,String p_roleCode,String transferID,String p_subject,ChannelTransferVO channelTransferVO) {
		final String methodName = "sendEmailNotificationTrf";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		try {
			final Locale locale = BTSLUtil.getSystemLocaleForEmail();
			final String from = BTSLUtil.getMessage(locale,"email.notification.changestatus.log.file.from");
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = "";
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			String to = channelTransferDAO.getEmailIdOfApprovers(p_con, p_roleCode, channelTransferVO.getFromUserID());
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String messages = null;
			subject =PretupsRestUtil.getMessageString(p_subject);
			messages = PretupsRestUtil.getMessageString("c2c.transfer.initiate.email.notification.content") + " " + transferID +PretupsRestUtil.getMessageString("c2c.transfer.initiate.email.notification.content1");
			if (!BTSLUtil.isNullString(p_roleCode)) {
				EMailSender.sendMail(to, from, bcc, cc, subject, messages, isAttachment, pathofFile, fileNameTobeDisplayed);
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
			ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
			String to = "";
			ArrayList arrayList = new ArrayList();
            arrayList = channelUserWebDAO.loadUserNameAndEmail(p_con, p_channelTransferVO.getTransferInitatedBy());
            to = (String)(arrayList.get(2));
            if(p_channelTransferVO.getFirstApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, p_channelTransferVO.getFirstApprovedBy())).get(2);
            if(p_channelTransferVO.getSecondApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, p_channelTransferVO.getSecondApprovedBy())).get(2);
            if(p_channelTransferVO.getThirdApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, p_channelTransferVO.getThirdApprovedBy())).get(2);
            
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String messages = "";
			subject =PretupsRestUtil.getMessageString(p_subject);
			final Locale locale = BTSLUtil.getSystemLocaleForEmail();
			final String from = BTSLUtil.getMessage(locale,"email.notification.changestatus.log.file.from");			
			
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
                              
                   message1 = message1 + "<tr>" +  
                            "<td style='width: 7%;'>" + p_channelTransferVO.getTransferID() + "</td>" +  
                            "<td style='width: 7%;'>" + p_channelTransferVO.getCategoryCode() + "</td>" +  
                            "<td style='width: 7%;'>" + p_channelTransferVO.getFromUserID() + "</td>" + 
                            "<td style='width: 7%;'>" + p_channelTransferVO.getReceiverCategoryCode() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getToUserID() + "</td>" +
                            "<td style='width: 7%;'>" + p_channelTransferVO.getProductCode() + "</td>" +
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
	                              
	                   message1 = message1 + "<tr>" +  
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getTransferID() + "</td>" +  
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getCategoryCode() + "</td>" +  
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getFromUserID() + "</td>" + 
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getReceiverCategoryCode() + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getToUserID() + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getProductCode() + "</td>" +
	                            "<td style='width: 9%;'>" + PretupsBL.getDisplayAmount(p_channelTransferVO.getRequestedQuantity()) + "</td>" +
  	                            "<td style='width: 9%;'>" + PretupsBL.getDisplayAmount(p_channelTransferVO.getTransferMRP()) + "</td>" +
	                            "<td style='width: 9%;'>" + PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount()) + "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getRecieverPostBalance()+ "</td>" +
	                            "<td style='width: 9%;'>" + p_channelTransferVO.getStatus() + "</td>" +
	                             "</tr>";
	                        
	                        
	                  messages = messages + message1 + "</table>";
			}
			
			EMailSender.sendMail(to, from, bcc, cc, subject, messages, isAttachment, pathofFile, fileNameTobeDisplayed);
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
	
	private void sendEmailNotificationOnC2CReject(Connection p_con, ChannelTransferVO channelTransferVO, String p_subject) {
		final String METHOD_NAME = "sendEmailNotificationOnC2CReject";
        
		final Locale locale = BTSLUtil.getSystemLocaleForEmail();
        
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}

		try {
			final String from = BTSLUtil.getMessage(locale,"email.notification.changestatus.log.file.from");
			//final String from = "System";
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = BTSLUtil.getMessage(locale,p_subject);
            ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
			StringBuilder message = new StringBuilder();
			message.append(PretupsRestUtil.getMessageString("c2c.transfer.initiate.email.notification.content"));
			message.append(channelTransferVO.getTransferID()).append(" is rejected by ");
			if(channelTransferVO.getFirstApprovedBy() == null)
				message.append("approver 1");
			else if(channelTransferVO.getSecondApprovedBy() == null)
				message.append("approver 2");
			else
				message.append("approver 3");

            String to = "";
            //For getting name, msisdn, email of initiator
            to = (String)((channelUserWebDAO.loadUserNameAndEmail(p_con, channelTransferVO.getTransferInitatedBy())).get(2));
            if(channelTransferVO.getFirstApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, channelTransferVO.getFirstApprovedBy())).get(2);
            if(channelTransferVO.getSecondApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, channelTransferVO.getSecondApprovedBy())).get(2);
            if(channelTransferVO.getThirdApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, channelTransferVO.getThirdApprovedBy())).get(2);
            
			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT",message);
			}
			boolean isAttachment = false;
			String pathofFile = "";
			String fileNameTobeDisplayed = "";
			// Send email
			EMailSender.sendMail(to, from, bcc, cc, subject, message.toString(), isAttachment, pathofFile, fileNameTobeDisplayed);
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error(METHOD_NAME, " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(METHOD_NAME, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exiting ....");
		}
	}
}
