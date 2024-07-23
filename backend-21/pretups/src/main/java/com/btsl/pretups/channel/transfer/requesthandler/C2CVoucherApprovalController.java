package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.common.IDGenerator;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2CVoucherReqMessage;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.VoucherDetails;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.CorePreferenceI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.XmlTagValueConstant;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VoucherChangeUserId;
import com.google.gson.Gson;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;

/**
 * Controller - REST Controller for C2C Voucher approval/rejecion
 * 
 * @author akhilesh.mittal1
 *
 */
public class C2CVoucherApprovalController implements ServiceKeywordControllerI, Runnable {

	Connection con = null;
	MComConnectionI mcomCon = null;
	String p_roleCode = null;

	

	protected final Log _log = LogFactory.getLog(getClass().getName());

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	
	/**
	 * @param p_con
	 * @param p_roleCode
	 * @param transferID
	 * @param p_subject
	 */
	private void sendEmailNotificationTrf(Connection p_con,String p_roleCode,String transferID,String p_subject,String parentUserId, String status,String receiverId) {
		final String methodName = "sendEmailNotificationTrf";
		if (_log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
        	loggerValue.append("Entered p_roleCode: ");
        	loggerValue.append(p_roleCode);
        	loggerValue.append("transferID: ");
        	loggerValue.append(transferID);
        	loggerValue.append("p_subject: ");
        	loggerValue.append(p_subject);
            _log.debug(methodName,loggerValue );
		}
		try {
			
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			String to = channelTransferDAO.getEmailIdOfApprovers(p_con, p_roleCode, parentUserId );
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String messages = null;
			
			Locale locale = BTSLUtil.getSystemLocaleForEmail();
			String from = BTSLUtil.getMessage(locale,"email.notification.c2c.log.file.from");
				
			
			if ("N".equalsIgnoreCase(status)) {

				messages = PretupsRestUtil.getMessageString("c2c.voucher.rejection.email.notification.content");
				messages = messages.replaceAll("\\[Transfer ID\\]", transferID);
				messages = messages.replaceAll("\\[Approver\\]", parentUserId);
				
				to = to + new UserDAO().retrieveEmail(receiverId, con);


			} else {

				messages = PretupsRestUtil.getMessageString("c2c.voucher.transfer.initiate.email.notification.content")
						+ " " + transferID + " "
						+ PretupsRestUtil.getMessageString("c2c.voucher.transfer.initiate.email.notification.content1");
			}			
			
			EMailSender.sendMail(to, from, bcc, cc, p_subject, messages, isAttachment, pathofFile, fileNameTobeDisplayed);

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

	
	private void sendEmailAndSMS(RequestVO p_requestVO, ChannelTransferVO channelTransferVO, String roleCode, String inputStatus) {

		ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		final String methodName = "sendEmailAndSMS";
		
		try {
			
			String[] msgArgs = { channelTransferVO.getTransferID() };
			p_requestVO.setMessageCode(PretupsErrorCodesI.VOMS_C2C_SUCCESSFUL);
			p_requestVO.setMessageArguments(msgArgs);
			p_requestVO.setTransactionID(channelTransferVO.getTransferID());

			
			
			sendEmailNotificationTrf(con, roleCode, channelTransferVO.getTransferID(),
					PretupsI.C2C_VOUCHER_COMPLETION_SUBJECT_SENDER, channelTransferVO.getFromUserID(), inputStatus,channelTransferVO.getToUserID());
			
			String messageKey = null;
			
			
			    if ("Y".equalsIgnoreCase(inputStatus)) {
					messageKey = PretupsErrorCodesI.C2C_TRANSFER_VOUCHER_APPROVED_SENDER;
				}else {
					messageKey = PretupsErrorCodesI.C2C_TRANSFER_VOUCHER_CANCEL_SENDER;
				}
		
			
			    // channelTransferVO.getTransferID()
			    
			String[] array = new String[2];
			
			if ("Y".equalsIgnoreCase(inputStatus)) {

				array[0] = channelTransferVO.getNetPayableAmountAsString();
				array[1] = channelTransferVO.getTransferID();
				
			} else {

				array[0] = channelTransferVO.getTransferID();
				array[1] = channelTransferVO.getSenderLoginID();
			}
			
			
			BTSLMessages messages1 = new BTSLMessages(messageKey, array);
			
			String msisdns = channelTransferDAO.getMsisdnOfApprovers(con, roleCode, channelTransferVO.getFromUserID());
			if (!BTSLUtil.isNullString(msisdns)) {
				String[] arrSplit = msisdns.split(",");
				for (int i = 0; i < arrSplit.length; i++) {
					
					String msisdn = arrSplit[i];
					final PushMessage pushMessage = new PushMessage(msisdn, messages1, channelTransferVO.getTransferID(), "",
							p_requestVO.getLocale(), p_requestVO.getNetworkCode());
					pushMessage.push();
				}
			}
			p_requestVO.setMessageArguments(array);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL);
			p_requestVO.setTransactionID(channelTransferVO.getTransferID());
			return;
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error("sendEmailAndSMS ", " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting ....");
		}

	}

	/**
	 * @param p_con
	 * @param p_roleCode
	 * @param transferID
	 * @param p_subject
	 */
	private void sendEmailNotification(Connection p_con, String to, String transferID, String p_subject, String senderOrReceiver, ChannelTransferVO channelTransferVO) {
		final String methodName = "sendEmailNotification";
		if (_log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append("transferID: ");
			loggerValue.append(transferID);
			loggerValue.append("p_subject: ");
			loggerValue.append(p_subject);
			_log.debug(methodName, loggerValue);
		}
		try {

			String cc = PretupsI.EMPTY;
			final String bcc = "";
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String preMessages = null;
			String messages = null;
			String postMessages = null;
			String middleMessage = "";
			Locale locale = BTSLUtil.getSystemLocaleForEmail();
			String from = BTSLUtil.getMessage(locale,"email.notification.c2c.log.file.from");

			if("N".equalsIgnoreCase(channelTransferVO.getStatus())) {
				
				messages = PretupsRestUtil
						.getMessageString("c2c.voucher.rejection.email.notification.content");
				messages = messages.replaceAll("\\[Transfer ID\\]", channelTransferVO.getTransferID());
				messages = messages.replaceAll("\\[Approver\\]", channelTransferVO.getSenderLoginID());

				
				EMailSender.sendMail(to, from, bcc, cc, p_subject, messages, isAttachment, pathofFile,
						fileNameTobeDisplayed);
			}else {
			
			
			
			if (senderOrReceiver != null && senderOrReceiver.equals("SENDER")) {
				preMessages = PretupsRestUtil
						.getMessageString("c2c.voucher.completion.initiate.email.notification.content.sender.pre");
				
				messages = PretupsRestUtil
						.getMessageString("c2c.voucher.completion.initiate.email.notification.content.sender");
			} else {
				preMessages = PretupsRestUtil
						.getMessageString("c2c.voucher.completion.initiate.email.notification.content.receiver.pre");
				
				messages = PretupsRestUtil
						.getMessageString("c2c.voucher.completion.initiate.email.notification.content.receiver");
			}
			
			postMessages = PretupsRestUtil
					.getMessageString("c2c.voucher.completion.initiate.email.notification.content.senderreceiver.post");
			
			
			_log.debug("preMessages ", preMessages);
			_log.debug("messages ", messages);
			_log.debug("postMessages ", postMessages);
			
			ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList =  channelTransferVO.getChannelVoucherItemsVoList();
			
			for(ChannelVoucherItemsVO channelVoucherItemsVoObj: channelVoucherItemsVOList  ) {

				
				middleMessage = middleMessage+""+messages;
				middleMessage = middleMessage.replaceAll("\\[Transfer ID\\]", channelTransferVO.getTransferID());
				middleMessage = middleMessage.replaceAll("\\[Sender Category\\]", channelTransferVO.getDomainCode());
				middleMessage = middleMessage.replaceAll("\\[Sender Name\\]", channelTransferVO.getFromUserName());
				middleMessage = middleMessage.replaceAll("\\[Receiver Category\\]", channelTransferVO.getReceiverCategoryCode());
				middleMessage = middleMessage.replaceAll("\\[Receiver Name\\]", channelTransferVO.getToUserName());//
				middleMessage = middleMessage.replaceAll("\\[Denomination\\]", channelVoucherItemsVoObj.getTransferMrp()+"");
				middleMessage = middleMessage.replaceAll("\\[Requested quantity\\]", channelVoucherItemsVoObj.getInitiatedQuantity()+"");
				
				long quantity = Long.parseLong(channelVoucherItemsVoObj.getToSerialNum()) - Long.parseLong(channelVoucherItemsVoObj.getFromSerialNum()) + 1;
				
				middleMessage = middleMessage.replaceAll("\\[Transferred quantity\\]", quantity + "");
				middleMessage = middleMessage.replaceAll("\\[Net receivable amount\\]", PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount())+"");
				middleMessage = middleMessage.replaceAll("\\[Net payablee amount\\]",  PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount())+"");//
				
				
				
				middleMessage = middleMessage.replaceAll("\\[Status\\]", channelTransferVO.getStatus());
				middleMessage = middleMessage.replaceAll("\\[Approval 1 quantity\\]", channelVoucherItemsVoObj.getFirstLevelApprovedQuantity()+"");
				middleMessage = middleMessage.replaceAll("\\[Approval 2 quantity\\]", channelVoucherItemsVoObj.getSecondLevelApprovedQuantity()+"");
				middleMessage = middleMessage.replaceAll("\\[Approval 3 quantity\\]", quantity + "");

				
				
			}
			
			_log.debug("messages ", middleMessage);
			_log.debug("compete messages ", preMessages+""+middleMessage+postMessages);
			EMailSender.sendMail(to, from, bcc, cc, p_subject, preMessages+""+middleMessage+postMessages, isAttachment, pathofFile,
					fileNameTobeDisplayed);
			}
				

			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT ", messages);
			}
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error("sendEmailNotification ", " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting ....");
		}
	}

	private void saveVoucherProductDetalis(RequestVO p_requestVO) throws Exception {

		
		ArrayList slabsList;
		String message = null;
		final ArrayList batchVO_list = new ArrayList();
		VomsBatchVO vomsBatchVO = null;
		ChannelVoucherItemsVO voucherItemVO = null;
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		final String methodName = "saveVoucherProductDetalis";
		Date dt = new Date();
		final ChannelTransferDAO channelTransferDAOI = new ChannelTransferDAO();
		ChannelTransferVO p_channelTransferVO = new ChannelTransferVO();
		ChannelUserVO userVO = null;
		Gson gson = new Gson();
		String transferId = null;
		String remarks = null;
		String[] fromSerialNo = null;
		String[] toSerialNo = null;
		String voucherDetailTag = null;
		VoucherDetails[] voucherDetails = null;
		String inputStatus = null;

		long totalQuantity = 0;
		Map<Long, Long> voucherQuantity = new HashMap<Long, Long>();
		double totalmrp = 0.0;
		String paymentInstNum = null;
		String paymentInstDate = null;
		String paymentInstCode = null;
		
		String extNwCode = null;
		// long quantity = 0;
		String currentApproalLevel = null;
		String newApprovalLevel = null;
		int approvalLevel = 0;
		boolean sendOrderToApproval = false;
		final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
		final Date currentDate = new Date();

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}

		userVO = (ChannelUserVO) p_requestVO.getSenderVO();

		// inputStatus = "N";
		/*
		 * UserPhoneVO userPhoneVO1 = null; if (!userVO.isStaffUser()) { userPhoneVO =
		 * userVO.getUserPhoneVO(); } else { userPhoneVO =
		 * userVO.getStaffUserDetails().getUserPhoneVO(); }
		 */

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Could not create Database connection", "Exception " + e);
		}

		
		HashMap reqMap = p_requestVO.getRequestMap();
		if(PretupsI.MOBILE_APP_GATEWAY.equals(p_requestVO.getRequestGatewayCode())){
			if (reqMap != null){
				C2CVoucherReqMessage reqMsgObj = null;
				fromSerialNo = new String[2];
				toSerialNo = new String[2];
				transferId = (String) reqMap.get("TRANSFERID");
				remarks = (String) reqMap.get("REMARKS");
				extNwCode = (String) reqMap.get("EXTNWCODE");
				inputStatus = (String) reqMap.get("STATUS");
				fromSerialNo[0] = (String) reqMap.get("FROMSERIALNUM");
				toSerialNo[0] = (String) reqMap.get("TOSERIALNUM");
				paymentInstNum = (String)reqMap.get("PAYMENTINSTNUM");
				paymentInstDate = (String) reqMap.get("PAYMENTDATE");
				paymentInstCode = (String) reqMap.get("PAYMENTINSTCODE");
				voucherQuantity.put( 1L , Long.parseLong(toSerialNo[0])-Long.parseLong(fromSerialNo[0])+1 ); // indexSerial : 1, 2
				totalQuantity +=Long.parseLong(toSerialNo[0])-Long.parseLong(fromSerialNo[0])+1;
			}
		}
		else{
			String requestMsg = p_requestVO.getRequestMessage();

			if(requestMsg != null && !requestMsg.trim().startsWith("{")) {
				
				requestMsg = requestMsg.trim().substring(requestMsg.trim().indexOf(" "));
			}
			if (requestMsg != null) {
			if (reqMap == null || reqMap.get("VOUCHER_DETAILS") == null) {

				C2CVoucherReqMessage reqMsgObj = gson.fromJson(requestMsg, C2CVoucherReqMessage.class);

				if (reqMsgObj != null) {
					transferId = reqMsgObj.getTransferId();
					remarks = reqMsgObj.getRemarks();
					inputStatus = reqMsgObj.getStatus();
					
					
					paymentInstNum = reqMsgObj.getPaymentinstnum();
					paymentInstDate = reqMsgObj.getPaymentinstdate();
					paymentInstCode = reqMsgObj.getPaymentinstcode();
					
					// fromserialno = reqMsgObj.getVoucherDetails();
					// toserialno = reqMsgObj.getToSerialNo();
					voucherDetails = reqMsgObj.getVoucherDetails();

					fromSerialNo = new String[voucherDetails.length];
					toSerialNo = new String[voucherDetails.length];

					int indexSerial = 0;
					int i=1;
					for (VoucherDetails voucherObj : voucherDetails) {
						if(BTSLUtil.isNullString(voucherObj.getFromSerialNum()))
						{
							String[] msg= {String.valueOf(i)}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.FROM_SNO_EMPTY,msg);
						}
						if(BTSLUtil.isNullString(voucherObj.getToSerialNum()))
						{
							String[] msg= {String.valueOf(i)}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TO_SNO_EMPTY,msg);
						}
						if(!BTSLUtil.isNumeric(voucherObj.getFromSerialNum()))
						{
							String[] msg= {String.valueOf(i)}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.FROM_SNO_NOT_NUMERIC,msg);
						}
						if(!BTSLUtil.isNumeric(voucherObj.getToSerialNum()))
						{
							String[] msg= {String.valueOf(i)}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TO_SNO_NOT_NUMERIC,msg);
						}
						fromSerialNo[indexSerial] = voucherObj.getFromSerialNum();
						toSerialNo[indexSerial] = voucherObj.getToSerialNum();
						indexSerial++;
						i++;
						voucherQuantity.put( (long) indexSerial , Long.parseLong(voucherObj.getToSerialNum())-Long.parseLong(voucherObj.getFromSerialNum())+1); // indexSerial : 1, 2
						totalQuantity +=Long.parseLong(voucherObj.getToSerialNum())-Long.parseLong(voucherObj.getFromSerialNum())+1; //5
					}

					extNwCode = reqMsgObj.getExtnwcode();
				}
			}
		}

		if (reqMap != null) {

			if (reqMap.get("VOUCHER_DETAILS") != null) {
				transferId = (String) reqMap.get("TRANSFERID");
				remarks = (String) reqMap.get("REMARKS");
				// fromserialno = (String) reqMap.get("FROMSERIALNO");
				// toserialno = (String) reqMap.get("TOSERIALNO");
				extNwCode = (String) reqMap.get("EXTNWCODE");
				voucherDetailTag = reqMap.get("VOUCHER_DETAILS").toString();
				
				paymentInstNum = reqMap.get("PAYMENTINSTNUM").toString();
				paymentInstDate = reqMap.get("PAYMENTDATE").toString();
				paymentInstCode = reqMap.get("PAYMENTINSTCODE").toString();
			}
			if (voucherDetailTag != null) {

				String[] voucherDetailsstr = (reqMap.get("VOUCHER_DETAILS").toString()).split(",");

				fromSerialNo = new String[voucherDetailsstr.length];
				toSerialNo = new String[voucherDetailsstr.length];

				int indexSerial = 0;
				int i = 1;
				for (String voucherObj : voucherDetailsstr) {
					if (voucherObj != null && voucherObj.contains(":")) {
						String[] voucherObjI = voucherObj.split(":");
						if(BTSLUtil.isNullString(voucherObjI[0]))
						{
							String[] msg= {String.valueOf(i)}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.FROM_SNO_EMPTY,msg);
						}
						if(BTSLUtil.isNullString(voucherObjI[1]))
						{
							String[] msg= {String.valueOf(i)}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TO_SNO_EMPTY,msg);
						}
						if(!BTSLUtil.isNumeric(voucherObjI[0]))
						{
							String[] msg= {String.valueOf(i)}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.FROM_SNO_NOT_NUMERIC,msg);
						}
						if(BTSLUtil.isNumeric(voucherObjI[1]))
						{
							String[] msg= {String.valueOf(i)}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TO_SNO_NOT_NUMERIC,msg);
						}
						fromSerialNo[indexSerial] = voucherObjI[0];
						toSerialNo[indexSerial] = voucherObjI[1];
						indexSerial++;
						i++;
						voucherQuantity.put( (long) indexSerial , Long.parseLong(voucherObjI[1])-Long.parseLong(voucherObjI[0])+1 ); // indexSerial : 1, 2
						totalQuantity +=Long.parseLong(voucherObjI[1])-Long.parseLong(voucherObjI[0])+1;
					}
				}

			}

		}
	}
		if(!BTSLUtil.isPaymentTypeValid(paymentInstCode)){
			throw new BTSLBaseException("VoucherC2CInitiateController", "process",
					PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);
		}
		if(!PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(paymentInstCode)){
			if(BTSLUtil.isNullString(paymentInstNum))
			{
				p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER});
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				throw new BTSLBaseException("VoucherC2CInitiateController", "process",
						PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
			}
		}
		// add these line to a method
		p_channelTransferVO.setTransferID(transferId);
		p_channelTransferVO.setNetworkCode(extNwCode);
		p_channelTransferVO.setNetworkCodeFor(extNwCode);
		p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER);
		channelTransferDAOI.loadChannelTransfersVO(con, p_channelTransferVO);
		
		currentApproalLevel = p_channelTransferVO.getStatus();
		if(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(currentApproalLevel) || PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentApproalLevel))
	    {
			throw new BTSLBaseException(this,methodName,
                    PretupsErrorCodesI.C2C_TXN_CLOSE_CNCL, 0,new String[] { currentApproalLevel }, "confirmback");
		}
		//validate user is allowed to authenticate transaction
		String roleCode = "";
		if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentApproalLevel)) {
			roleCode = PretupsI.C2C_VOUCHER_TRF_APR1;
		} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApproalLevel)) {
			roleCode = PretupsI.C2C_VOUCHER_TRF_APR2;
		}  else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentApproalLevel)) {
			roleCode = PretupsI.C2C_VOUCHER_TRF_APR3;
		}

		if(!channelTransferDAOI.validateUserAllowedForApproval(con, transferId, userVO.getUserID(), roleCode)){
			throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.C2C_TRF_APPROVAL_NOT_ALLOWED,"confirmback");
		}

		p_requestVO.setTransactionID(transferId);
		p_channelTransferVO.setActiveUserId(userVO.getActiveUserID());
		p_channelTransferVO.setModifiedBy(userVO.getActiveUserID());
		
		// Loading channel_transfers_items details
		ArrayList<ChannelTransferItemsVO> channelTransferitemsList = channelTransferDAOI.loadChannelTransferItems(con,
				transferId);
	  
		
		// Loading channel_voucher_items details
		ArrayList<ChannelVoucherItemsVO> channelTransferitemsVOList = channelTransferDAOI.loadChannelVoucherItemsList(
				con, transferId, fromSerialNo, toSerialNo, p_channelTransferVO.getFromUserID());
		if (channelTransferitemsVOList == null) {
			throw new BTSLBaseException(this, methodName,
					"Voucher range can not be approved, either range is not associated with User or approved quantity is greater than requested quantity",
					"confirmback");
		}
		
		//to check C2S sold status for approval case
		if("Y".equalsIgnoreCase(inputStatus) && channelTransferDAOI.isVoucherAlreadySoldInRange(con,fromSerialNo, toSerialNo)) {
			throw new BTSLBaseException(this, methodName,
					/*"Voucher range can not be approved, one or more vouchers in range already sold.",*/
					PretupsErrorCodesI.VOMS_SERIAL_NO_ALREADY_SOLD_C2S_APPRVL,
					"confirmback");
		}
		
		long voucherQuantityKey = 1;
		for(ChannelVoucherItemsVO channelVoucherItemsVO :channelTransferitemsVOList){
			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentApproalLevel)) {
				if(voucherQuantity.get(voucherQuantityKey) <= channelVoucherItemsVO.getInitiatedQuantity()) {
					totalmrp +=channelVoucherItemsVO.getTransferMrp() * voucherQuantity.get(voucherQuantityKey); // totalmrp = 10 ( display amount, alredy in db)
				} else {
					throw new BTSLBaseException("VoucherC2CInitiateController", "process",
							PretupsErrorCodesI.INVALID_VOUCHER_QUANTITY, 0, null);
				}
			}
			else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApproalLevel)) {
				if(voucherQuantity.get(voucherQuantityKey) <= channelVoucherItemsVO.getFirstLevelApprovedQuantity()) {
					totalmrp +=channelVoucherItemsVO.getTransferMrp() * voucherQuantity.get(voucherQuantityKey);
				} else {
					throw new BTSLBaseException("VoucherC2CInitiateController", "process",
							PretupsErrorCodesI.INVALID_VOUCHER_QUANTITY, 0, null);
				}
			}
			 else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentApproalLevel)) {
				 if(voucherQuantity.get(voucherQuantityKey) <= channelVoucherItemsVO.getSecondLevelApprovedQuantity()) {
						totalmrp +=channelVoucherItemsVO.getTransferMrp() * voucherQuantity.get(voucherQuantityKey);
					} else {
						throw new BTSLBaseException("VoucherC2CInitiateController", "process",
								PretupsErrorCodesI.INVALID_VOUCHER_QUANTITY, 0, null);
					}
			 }
			
			voucherQuantityKey++;
		}
		
		for(ChannelTransferItemsVO transferItemsVO:channelTransferitemsList){
			double mrp = 0.0;
			transferItemsVO.setVoucherQuantity(totalQuantity); // totalQuantity = 3
			transferItemsVO.setSenderDebitQty(0);
			transferItemsVO.setReceiverCreditQty(0);
			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentApproalLevel)) {
				mrp = totalmrp;
				transferItemsVO.setFirstApprovedQuantity( String.valueOf(mrp) );
				transferItemsVO.setRequiredQuantity( BTSLUtil.parseStringToLong( transferItemsVO.getFirstApprovedQuantity()) );
				transferItemsVO.setApprovedQuantity(BTSLUtil.parseStringToLong(transferItemsVO.getFirstApprovedQuantity()));
				newApprovalLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApproalLevel)) {
				mrp = totalmrp;
				transferItemsVO.setSecondApprovedQuantity( String.valueOf(mrp) );
				transferItemsVO.setRequiredQuantity(BTSLUtil.parseStringToLong( transferItemsVO.getSecondApprovedQuantity()) );
				transferItemsVO.setApprovedQuantity(BTSLUtil.parseStringToLong( transferItemsVO.getSecondApprovedQuantity()) );
				newApprovalLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
			}  else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentApproalLevel)) {
				mrp = totalmrp;
				transferItemsVO.setThirdApprovedQuantity( String.valueOf(mrp) );
				transferItemsVO.setRequiredQuantity( BTSLUtil.parseStringToLong( transferItemsVO.getThirdApprovedQuantity()) );
				transferItemsVO.setApprovedQuantity( BTSLUtil.parseStringToLong( transferItemsVO.getThirdApprovedQuantity()) );
				newApprovalLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
			}
			
		}
		p_channelTransferVO.setChannelTransferitemsVOList(channelTransferitemsList);
		p_channelTransferVO.setChannelVoucherItemsVoList(channelTransferitemsVOList);

		long totTax1 = 0, totTax2 = 0, totTax3 = 0, totRequestedQty = 0, payableAmount = 0, netPayableAmt = 0, totTransferedAmt = 0, totalMRP = 0, totcommission = 0;
		long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0,otfValue=0,firAppQty=0,secAppQty=0,thrAppQty=0;
		ChannelTransferBL.loadAndCalculateTaxOnDenominations(con, p_channelTransferVO.getCommProfileSetId(), p_channelTransferVO.getCommProfileVersion(),
				p_channelTransferVO, true, "confirmback", PretupsI.TRANSFER_TYPE_C2C);
		ChannelTransferBL.calculateTotalMRPFromTaxAndDiscount(p_channelTransferVO.getChannelTransferitemsVOList(), PretupsI.TRANSFER_TYPE_O2C, newApprovalLevel,
				p_channelTransferVO);
		ChannelTransferItemsVO transferItemsVO = null;
		int itemsLists = p_channelTransferVO.getChannelTransferitemsVOList().size();
		
		for (int k = 0; k < itemsLists; k++) {
			transferItemsVO = (ChannelTransferItemsVO) p_channelTransferVO.getChannelTransferitemsVOList().get(k);
			totTax1 += transferItemsVO.getTax1Value();
			totTax2 += transferItemsVO.getTax2Value();
			totTax3 += transferItemsVO.getTax3Value();
			totcommission += transferItemsVO.getCommValue();
			if (transferItemsVO.getRequestedQuantity() != null && BTSLUtil.isDecimalValue(transferItemsVO.getRequestedQuantity())) {
				totRequestedQty += PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity());
				if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {

					totTransferedAmt += transferItemsVO.getReceiverCreditQty() * Long.parseLong(PretupsBL.getDisplayAmount(transferItemsVO.getUnitValue()));
				} else {
					totTransferedAmt += (Double.parseDouble(transferItemsVO.getRequestedQuantity()) * transferItemsVO.getUnitValue());
				}
			}
			payableAmount += transferItemsVO.getPayableAmount();
			netPayableAmt += transferItemsVO.getNetPayableAmount();
			totalMRP += transferItemsVO.getProductTotalMRP();
			commissionQty += transferItemsVO.getCommQuantity();
			otfValue +=transferItemsVO.getOtfAmount();
	
			// Added to get approved quantity in system format
			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentApproalLevel)) 
			{
				transferItemsVO.setFirstApprovedQuantity( String.valueOf( (PretupsBL.getSystemAmount( transferItemsVO.getFirstApprovedQuantity() ))) );
				transferItemsVO.setApprovedQuantity(BTSLUtil.parseStringToLong( transferItemsVO.getFirstApprovedQuantity()) );
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApproalLevel)) 
			{
				transferItemsVO.setSecondApprovedQuantity( String.valueOf( (PretupsBL.getSystemAmount( transferItemsVO.getSecondApprovedQuantity() ))) );
				transferItemsVO.setApprovedQuantity(BTSLUtil.parseStringToLong( transferItemsVO.getSecondApprovedQuantity()) );
			}  else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentApproalLevel)) 
			{
				transferItemsVO.setThirdApprovedQuantity( String.valueOf( (PretupsBL.getSystemAmount( transferItemsVO.getThirdApprovedQuantity() ))) );
				transferItemsVO.setApprovedQuantity( BTSLUtil.parseStringToLong( transferItemsVO.getThirdApprovedQuantity()) );
			}

		} // end
		
		p_channelTransferVO.setTransferMRP(totalMRP);
		p_channelTransferVO.setNetPayableAmount(netPayableAmt);
		p_channelTransferVO.setPayableAmount(payableAmount);
		p_channelTransferVO.setReqQuantity(String.valueOf(totRequestedQty));
		p_channelTransferVO.setTransferAmt(String.valueOf(totTransferedAmt));
		p_channelTransferVO.setTotalTax1(totTax1);
		p_channelTransferVO.setTotalTax2(totTax2);
		p_channelTransferVO.setTotalTax3(totTax3);
		String str = PretupsBL.getDisplayAmount(totRequestedQty-payableAmount);
		p_channelTransferVO.setCommissionValue(PretupsBL.getDisplayAmount(commissionQty));

		p_channelTransferVO.setPayInstrumentType(paymentInstCode);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
	    Date paymentDate = sdf.parse(paymentInstDate);
	    
		p_channelTransferVO.setPayInstrumentDate(BTSLUtil.getSQLDateFromUtilDate(paymentDate));
		p_channelTransferVO.setPayInstrumentNum(paymentInstNum);


		channelTransferVO = p_channelTransferVO;
		if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApproalLevel)) {
			channelTransferVO.setSecondApprovedBy(userVO.getUserID());
			channelTransferVO.setSecondApprovedOn(dt);
			approvalLevel = 2;

		} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentApproalLevel)) {

			channelTransferVO.setThirdApprovedBy(userVO.getUserID());
			channelTransferVO.setThirdApprovedOn(dt);
			approvalLevel = 3;
		} else {
			channelTransferVO.setFirstApprovedBy(userVO.getUserID());
			channelTransferVO.setFirstApprovedOn(dt);
			approvalLevel = 1;
		}

		try {
			int level = ((Integer) PreferenceCache.getControlPreference(
					CorePreferenceI.MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER, p_channelTransferVO.getNetworkCode(),
					userVO.getCategoryCode())).intValue();

			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentApproalLevel)) {
				// channelTransferVO.setLevelOneApprovedQuantity(String.valueOf(quantity));
				channelTransferVO
						.setLevelTwoApprovedQuantity(String.valueOf(p_channelTransferVO.getApprovedQuantity()));
				channelTransferVO
						.setLevelThreeApprovedQuantity(String.valueOf(p_channelTransferVO.getApprovedQuantity()));
				channelTransferVO.setFirstApprovalRemark(remarks);
				channelTransferVO.setFirstApprovedBy(userVO.getUserID());
				channelTransferVO.setFirstApprovedOn(new Date());
				message = "channeltransfer.approval.levelone.msg.success";

				if ("Y".equalsIgnoreCase(inputStatus)) {
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
				} else {
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				}

				if (level == 1) {
					sendOrderToApproval = true;
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				}

			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApproalLevel)) {

				channelTransferVO
						.setLevelOneApprovedQuantity(String.valueOf(p_channelTransferVO.getApprovedQuantity()));
				// channelTransferVO.setLevelTwoApprovedQuantity(String.valueOf(quantity));
				channelTransferVO
						.setLevelThreeApprovedQuantity(String.valueOf(p_channelTransferVO.getApprovedQuantity()));
				channelTransferVO.setSecondApprovalRemark(remarks);
				channelTransferVO.setSecondApprovedBy(userVO.getUserID());
				channelTransferVO.setSecondApprovedOn(new Date());
				message = "channeltransfer.approval.leveltwo.msg.success";

				if ("Y".equalsIgnoreCase(inputStatus)) {
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
				} else {
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				}

				if (level == 2) {
					sendOrderToApproval = true;
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				}

			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentApproalLevel)) {
				channelTransferVO
						.setLevelOneApprovedQuantity(String.valueOf(p_channelTransferVO.getApprovedQuantity()));
				channelTransferVO
						.setLevelTwoApprovedQuantity(String.valueOf(p_channelTransferVO.getApprovedQuantity()));
				// channelTransferVO.setLevelThreeApprovedQuantity(String.valueOf(quantity));

				channelTransferVO.setThirdApprovalRemark(remarks);
				channelTransferVO.setThirdApprovedBy(userVO.getUserID());
				channelTransferVO.setThirdApprovedOn(new Date());
				message = "channeltransfer.approval.msg.success";
				sendOrderToApproval = true;

				if ("Y".equalsIgnoreCase(inputStatus)) {
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);

				} else {
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				}

			} else {

				throw new BTSLBaseException(this, methodName,
						"Not a valid status for approval, current status: " + currentApproalLevel);

			}

			/*
			 * channelTransferVO.setLevelTwoApprovedQuantity(String.valueOf(
			 * p_channelTransferVO.getRequestedQuantity()));
			 * channelTransferVO.setLevelThreeApprovedQuantity(String.valueOf(
			 * p_channelTransferVO.getRequestedQuantity()));
			 */
			channelTransferVO.setPayableAmount(p_channelTransferVO.getPayableAmount());
			channelTransferVO.setNetPayableAmount(p_channelTransferVO.getNetPayableAmount());
			channelTransferVO.setPayInstrumentAmt(p_channelTransferVO.getPayInstrumentAmt());

			channelTransferVO.setTotalTax1(p_channelTransferVO.getTotalTax1());
			channelTransferVO.setTotalTax2(p_channelTransferVO.getTotalTax2());
			channelTransferVO.setTotalTax3(p_channelTransferVO.getTotalTax3());

			channelTransferVO.setExternalTxnNum(p_channelTransferVO.getExternalTxnNum());

			String extTransDate = p_channelTransferVO.getExternalTxnDateAsString();
			if (extTransDate != null) {
				channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(extTransDate));
			}
			channelTransferVO.setReferenceNum(p_channelTransferVO.getReferenceNum());

			if (PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(p_channelTransferVO.getPaymentInstType())) {
				sendOrderToApproval = true;
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			}

			ChannelVoucherItemsVO channelVoucherItemsVO = null;
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();

			slabsList = p_channelTransferVO.getChannelVoucherItemsVoList();
			final ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList = new ArrayList<ChannelVoucherItemsVO>();
			long requestedQnty = 0L;
			String startSerialNo = null;
			String batch_no = null;
			String endSerialNo = null;
			VomsProductDAO vomsProductDAO = null;
			int slabLists = slabsList.size();
			for (int i = 0; ( (i < slabLists) && (fromSerialNo != null && fromSerialNo.length > 0 && BTSLUtil.isEmpty(fromSerialNo[0]) == false) ); i++) {

				voucherItemVO = (ChannelVoucherItemsVO) slabsList.get(i);

				
				// if (voucherItemVO.getProductId() != null) {
				vomsBatchVO = new VomsBatchVO();
				startSerialNo = fromSerialNo[i];// voucherItemVO.getFromSerialNum();
				endSerialNo = toSerialNo[i];// voucherItemVO.getToSerialNum();

				channelTransferVO.setFrom_serial_no(startSerialNo);
				channelTransferVO.setTo_serial_no(endSerialNo);

				vomsBatchVO.setFromSerialNo(startSerialNo);
				vomsBatchVO.setToSerialNo(endSerialNo);
				vomsBatchVO.setVoucherType(voucherItemVO.getVoucherType());

				long quantity = Long.parseLong(toSerialNo[i]) - Long.parseLong(fromSerialNo[i]) + 1;

				vomsBatchVO.setQuantity(quantity + "");

				if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentApproalLevel)) {
					channelTransferVO.setLevelOneApprovedQuantity(String.valueOf(quantity));
				} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApproalLevel)) {
					channelTransferVO.setLevelTwoApprovedQuantity(String.valueOf(quantity));

				} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentApproalLevel)) {
					channelTransferVO.setLevelThreeApprovedQuantity(String.valueOf(quantity));
				}

				vomsBatchVO.setProductid(voucherItemVO.getProductId());
				vomsBatchVO.setProductID(voucherItemVO.getProductId());

				vomsBatchVO.setProductName(voucherItemVO.getProductName());
				vomsBatchVO.setDenomination(voucherItemVO.getTransferMrp() + "");
				//vomsBatchVO.setSeq_id((int) voucherItemVO.getSNo());
				vomsBatchVO.setSeq_id(BTSLUtil.parseLongToInt(voucherItemVO.getSNo()));
				
				vomsBatchVO.setCreatedBy(channelUserVO.getUserID());
				vomsBatchVO.set_NetworkCode(channelUserVO.getNetworkID());
				vomsBatchVO.setFromSerialNo(startSerialNo);
				vomsBatchVO.setToSerialNo(endSerialNo);
				vomsBatchVO.setCreatedDate(currentDate);
				vomsBatchVO.setModifiedDate(currentDate);
				vomsBatchVO.setModifiedOn(currentDate);
				vomsBatchVO.setCreatedOn(currentDate);
				vomsBatchVO.setToUserID(channelTransferVO.getToUserID());
				// TODO vomsBatchVO.setVoucherType(theForm.getVoucherType());
				vomsBatchVO.setExtTxnNo(channelTransferVO.getTransferID());
				// TODO vomsBatchVO.setSegment(theForm.getSegment());
				if (sendOrderToApproval) {
					vomsProductDAO = new VomsProductDAO();
					String type = vomsProductDAO.getTypeFromVoucherType(con, vomsBatchVO.getVoucherType());

					if (VOMSI.VOUCHER_TYPE_DIGITAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_DIGITAL.equals(type)) {
						vomsBatchVO.setBatchType(VomsUtil.getNextVoucherLifeStatus(VOMSI.VOUCHER_NEW, type));
					} else if (VOMSI.VOUCHER_TYPE_PHYSICAL.equals(type)
							|| VOMSI.VOUCHER_TYPE_TEST_PHYSICAL.equals(type)) {
						vomsBatchVO.setBatchType(VomsUtil.getNextVoucherLifeStatus(VOMSI.VOMS_WARE_HOUSE_STATUS, type));
					}
					batch_no = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE,
							String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
					vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batch_no));
					final int insert_count = channelTransferWebDAO.insertVomsBatches(con, vomsBatchVO);
					if (insert_count > 0) {
						vomsBatchVO.setTransferId(transferId);
						batchVO_list.add(vomsBatchVO);

					} else {
						mcomCon.finalRollback();
						throw new BTSLBaseException(this, methodName,
								"channeltransfer.transferdetailssuccess.msg.unsuccess", "confirmback");
					}
				}

				requestedQnty = requestedQnty + Long.parseLong(vomsBatchVO.getQuantity());
				channelVoucherItemsVO = new ChannelVoucherItemsVO();
				channelVoucherItemsVO.setTransferId(channelTransferVO.getTransferID());
				channelVoucherItemsVO.setTransferDate(channelTransferVO.getTransferDate());
				channelVoucherItemsVO.setTransferMRP(BTSLUtil.parseStringToLong( vomsBatchVO.getDenomination()));
				channelVoucherItemsVO.setRequiredQuantity( BTSLUtil.parseStringToLong( vomsBatchVO.getQuantity()));
				channelVoucherItemsVO.setVoucherType(vomsBatchVO.getVoucherType());
				channelVoucherItemsVO.setFromSerialNum(startSerialNo);
				channelVoucherItemsVO.setToSerialNum(endSerialNo);
				channelVoucherItemsVO.setProductId(vomsBatchVO.getProductid());
				channelVoucherItemsVO.setProductName(vomsBatchVO.getProductName());
				channelVoucherItemsVO.setSNo(vomsBatchVO.getSeq_id());
				
				String productId = (new ChannelTransferDAO()).retreiveProductId(con, startSerialNo);
				channelVoucherItemsVO.setProductId(productId);

				if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(currentApproalLevel)) {
					p_roleCode = "C2CVCTRFAPR2";
					channelVoucherItemsVO.setFirstLevelApprovedQuantity(quantity);
					channelVoucherItemsVO.setInitiatedQuantity(voucherItemVO.getInitiatedQuantity());

				} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApproalLevel)) {
					p_roleCode = "C2CVCTRFAPR3";
					channelVoucherItemsVO.setSecondLevelApprovedQuantity(quantity);

					channelVoucherItemsVO.setFirstLevelApprovedQuantity(voucherItemVO.getFirstLevelApprovedQuantity());
					channelVoucherItemsVO.setInitiatedQuantity(voucherItemVO.getInitiatedQuantity());

				} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentApproalLevel)) {
					// channelTransferVO.setLevelThreeApprovedQuantity(String.valueOf(quantity));
					p_roleCode = null;
					channelVoucherItemsVO
							.setSecondLevelApprovedQuantity(voucherItemVO.getSecondLevelApprovedQuantity());

					channelVoucherItemsVO.setFirstLevelApprovedQuantity(voucherItemVO.getFirstLevelApprovedQuantity());
					channelVoucherItemsVO.setInitiatedQuantity(voucherItemVO.getInitiatedQuantity());

				}

				channelVoucherItemsVOList.add(channelVoucherItemsVO);
				channelTransferVO.setChannelVoucherItemsVoList(channelVoucherItemsVOList);
				// }
			}
			if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
					channelTransferVO.getNetworkCode())
					&& PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus())) {
				ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
			}
			int updateCount = 0;
			updateCount = channelTransferDAO.updateChannelTransferVoucherApproval(con, channelTransferVO,
					approvalLevel);
			if (sendOrderToApproval) {
				final Date date = new Date();
				ChannelTransferBL.updateOptToChannelUserInCounts(con, channelTransferVO, "searchadomain", date);
			}
			
			if (updateCount > 0) {
				mcomCon.finalCommit();	
				try {
					boolean _receiverMessageSendReq = false;
					String _serviceType = PretupsI.SERVICE_TYPE_CHNL_C2C_INTR;
					_receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(
							PreferenceI.REC_MSG_SEND_ALLOW_C2C, channelUserVO.getNetworkCode(), _serviceType))
									.booleanValue();

					if (sendOrderToApproval) {

						final UserDAO userDAO = new UserDAO();
						String toUserMsisdn = userDAO.retrieveMsisdn(channelTransferVO.getToUserID(), con);
						String fromUserMsisdn = userDAO.retrieveMsisdn(channelTransferVO.getFromUserID(), con);

						String approver1 = null;
						if(channelTransferVO.getFirstApprovedBy() != null) {
							 approver1 = userDAO.retrieveMsisdn(channelTransferVO.getFirstApprovedBy(), con);
						}
						
						String approver2 = null;
						if(channelTransferVO.getSecondApprovedBy() != null) {
							approver2 = userDAO.retrieveMsisdn(channelTransferVO.getSecondApprovedBy(), con);
						}
						
						String approver3 = null;
						
						if(channelTransferVO.getThirdApprovedBy() != null) {
							approver3 = userDAO.retrieveMsisdn(channelTransferVO.getThirdApprovedBy(), con);
						}
						
						String[] msisdnArr = { fromUserMsisdn,approver1, approver2, approver3 , toUserMsisdn};
						String[] userIdArr = { channelTransferVO.getFromUserID(),channelTransferVO.getFirstApprovedBy(), channelTransferVO.getSecondApprovedBy(), channelTransferVO.getThirdApprovedBy() , channelTransferVO.getToUserID()};
						
						
						int msisdnCounter = 0;
						HashSet<String> msisdnMap = new HashSet<String>();
						for (String msisdn : msisdnArr) {

							msisdnCounter++;
							if (msisdn != null) {

								String p_subject = null;
								String senderOrReceiver = null;
								String emailId = null;
								if (msisdnCounter <= 4) {
									p_subject = PretupsI.C2C_VOUCHER_COMPLETION_SUBJECT_SENDER;
									senderOrReceiver = "SENDER";
									if(!msisdnMap.contains(msisdn) && userIdArr[msisdnCounter-1] != null) {
										emailId = userDAO.retrieveEmail(userIdArr[msisdnCounter-1], con);
										sendEmailNotification(con, emailId, channelTransferVO.getTransferID(), p_subject, senderOrReceiver, channelTransferVO);
									}
									
								} else {
									p_subject = PretupsI.C2C_VOUCHER_COMPLETION_SUBJECT_RECEIVER;
									senderOrReceiver = "RECEIVER";
									if(userIdArr[msisdnCounter-1] != null) {
										emailId = userDAO.retrieveEmail(userIdArr[msisdnCounter-1], con);
										sendEmailNotification(con, emailId, channelTransferVO.getTransferID(), p_subject, senderOrReceiver, channelTransferVO);
									}	
								}

								

								UserPhoneVO primaryPhoneVO = null;
								if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()
										&& (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue())) {
									primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, msisdn);
								}
								final UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, msisdn);
								String country = (String) PreferenceCache
										.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
								String language = (String) PreferenceCache
										.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);

								if (_receiverMessageSendReq) {

									String messageKey = null;

									if (msisdnCounter <= 4) {
										if ("Y".equalsIgnoreCase(inputStatus)) {
											messageKey = PretupsErrorCodesI.C2C_TRANSFER_VOUCHER_APPROVED_SENDER;
										}else {
											messageKey = PretupsErrorCodesI.C2C_TRANSFER_VOUCHER_CANCEL_SENDER;
										}
									} else {
										if ("Y".equalsIgnoreCase(inputStatus)) {
											messageKey = PretupsErrorCodesI.C2C_TRANSFER_VOUCHER_APPROVED_RECEIVER;
										}else {
											messageKey = PretupsErrorCodesI.C2C_TRANSFER_VOUCHER_CANCEL_RECEIVER;
										}
									}
									if (primaryPhoneVO != null) {
										country = primaryPhoneVO.getCountry();
										language = primaryPhoneVO.getPhoneLanguage();
										final Locale locale = new Locale(language, country);
										// final Object[] smsListArr = prepareSMSMessageListForVoucher(con,
										// channelTransferVO);
										 String[] array = new String[2];
										
										if ("Y".equalsIgnoreCase(inputStatus)) {

											array[0] = channelTransferVO.getNetPayableAmountAsString();
											array[1] = channelTransferVO.getTransferID();
										} else {

											array[0] = channelTransferVO.getTransferID();
											array[1] = userVO.getUserID();
										}
										 
										
										final BTSLMessages messages = new BTSLMessages(messageKey, array);
										final PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(),
												messages, channelTransferVO.getTransferID(), null, locale,
												channelTransferVO.getNetworkCode());
										if(!msisdnMap.contains(msisdn)){
											pushMessage.push();
										}
									}
									if (phoneVO != null) {
										country = phoneVO.getCountry();
										language = phoneVO.getPhoneLanguage();
										final Locale locale = new Locale(language, country);
										// final Object[] smsListArr = prepareSMSMessageListForVoucher(con,
										// channelTransferVO);
										final String[] array = { channelTransferVO.getNetPayableAmountAsString(),
												channelTransferVO.getTransferID() };
										final BTSLMessages messages = new BTSLMessages(messageKey, array);
										final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages,
												channelTransferVO.getTransferID(), null, locale,
												channelTransferVO.getNetworkCode());
										if(!msisdnMap.contains(msisdn)){
											pushMessage.push();
										}
									}
								}
							}
							msisdnMap.add(msisdn);
						}

						//

					}else {
						
						if(p_roleCode != null && !sendOrderToApproval) {
							sendEmailAndSMS(p_requestVO, channelTransferVO, p_roleCode, inputStatus);
						}
						
						
					}
				} catch (Exception e) {
					_log.error(methodName, " SMS notification failed" + e.getMessage());
					_log.errorTrace(methodName, e);
				}
				if (_log.isDebugEnabled()) {
					_log.debug(approvalLevel, "CurrentApprovalLevel" + " " + sendOrderToApproval);
				}

			} else {
				sendOrderToApproval = false;
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName, "channeltransfer.transferdetailssuccess.msg.unsuccess",
						"confirmback");
			}

			if (sendOrderToApproval) {
				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
						channelTransferVO.getNetworkCode())) {
					if (channelTransferVO.isTargetAchieved()
							&& PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus())) {
						// Message handling for OTF
						TargetBasedCommissionMessages tbcm = new TargetBasedCommissionMessages();
						tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con, channelTransferVO.getToUserID(),
								channelTransferVO.getMessageArgumentList());
					}
				}
				message = "channeltransfer.approval.msg.success";

				VoucherChangeUserId voucherChangeStatus = new VoucherChangeUserId(batchVO_list);
				voucherChangeStatus.start();
			}
			final String args[] = { channelTransferVO.getTransferID() };
			final BTSLMessages messages = new BTSLMessages(message, args, "o2cfinalvoucher");

		} catch (BTSLBaseException be) {
			_log.error(methodName, "Exception:e=" + be);
			_log.errorTrace(methodName, be);

			throw be;
		} catch (Exception e) {

			_log.error(methodName, "Exception:e=" + e);
			_log.errorTrace(methodName, e);

			try {
				mcomCon.finalRollback();
			} catch (Exception e1) {
				_log.errorTrace("saveUserProductsOrder", e1);
			}

			//throw new BTSLBaseException(this, "saveVoucherProductDetalis", "Exception "+e);

			//throw e;
			throw new BTSLBaseException(e.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CVoucherApprovalController#saveVoucherProductDetalis");
				mcomCon = null;
			}

		}

	}

	@Override
	public void process(RequestVO p_requestVO) {
		// TODO Auto-generated method stub
		final String METHOD_NAME = "process";

		
		try {

			saveVoucherProductDetalis(p_requestVO);
			p_requestVO.setSuccessTxn(true);
			p_requestVO.setMessageCode("20000");
			
			p_requestVO.setSenderReturnMessage("Transaction has been completed!");
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Transaction has been failed!");

			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException : ", esql.getMessage());
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

			p_requestVO.setSenderReturnMessage("Transaction has been failed!");
			p_requestVO.setSuccessTxn(false);

			_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CTrfApprovalController[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);

			return;
		}

	}

}
