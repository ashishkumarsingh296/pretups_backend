

package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

/**
 * @author Shaina Sahni
 */
public class SOSTransferRequestHandler implements ServiceKeywordControllerI {

	private static Log log = LogFactory.getLog(SOSTransferRequestHandler.class.getName());
	private static OperatorUtilI operatorUtil = null;
	private static final String className="SOSTransferRequestHandler";
	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " SOSTransferRequestHandler [initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	public void process(RequestVO requestVO) {

		final String methodName = "process";
		if (log.isDebugEnabled()) {
			log.debug("process", "Entered requestVO: " + requestVO);
		}

		final ChannelUserVO senderVO = (ChannelUserVO) requestVO.getSenderVO();
		UserPhoneVO userPhoneVO = null;
		if (!senderVO.isStaffUser()) {
			userPhoneVO = senderVO.getUserPhoneVO();
		} else {
			userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
		}

		LogFactory.printLog(methodName,  "Entered Sender VO: " + senderVO, log);


		Connection con = null;MComConnectionI mcomCon = null;

		try
		{
			if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())
			{
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.SOS_NOT_ENABLE);
			}

				if (senderVO != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(senderVO.getInSuspend())) {
				requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_IN_SUSPENDED);
				throw new BTSLBaseException(className, "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_IN_SUSPENDED);
			}

			final Date curDate = new Date();
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
			ChannelTransferItemsVO channelTrfItemsVO = new ChannelTransferItemsVO();
			UserTransferCountsVO countsVO = new UserTransferCountsVO();
			CategoryVO categoryVO =  new CategoryVO();
			UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
			CategoryDAO categoryDAO =  new CategoryDAO();
			UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
			UserPhoneVO receiverPrimaryPhoneVO = null;
			ChannelTransferItemsVO channelTransferItemsVO = null;
			KeyArgumentVO keyArgumentVO = null;


			mcomCon = new MComConnection();con=mcomCon.getConnection();
			final UserDAO userDAO = new UserDAO();
			UserPhoneVO phoneVO = null;
			boolean receiverAllowed = false;
			boolean senderAllowed = false;
			UserStatusVO receiverStatusVO = null;
			UserStatusVO senderStatusVO = null;
			ArrayList prdList = null;
			Date currentDate = null;
			long userBalance = 0;
			long sosThreshold = 0;

			if(requestVO.getSourceType().equals("SMSC"))
			{
				final String message[] = requestVO.getRequestMessageArray();
				final int messageLen = message.length;
				if (log.isDebugEnabled()) {
					log.debug(className, "messageLen: " + messageLen+", requestVO.getSourceType()="+requestVO.getSourceType());
	            }
				final String[] newMessageArr = new String[message.length + 1];

				// To set the msisdn in the request message array
				if (BTSLUtil.isNullString(requestVO.getReceiverMsisdn()) && BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {


					for(int j = 0; j < newMessageArr.length - 1; j++) {
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
					newMessageArr[2] = requestVO.getFilteredMSISDN();
					requestVO.setRequestMessageArray(newMessageArr);
				} else {
					final String[] mesgArr = requestVO.getRequestMessageArray();
					mesgArr[2] = requestVO.getFilteredMSISDN();
					requestVO.setRequestMessageArray(mesgArr);
				}

				//Set request sender language code



				switch (newMessageArr.length){
				case 4: {

					final String[] newMessageArr2 = new String[newMessageArr.length + 1];

					for(int j = 0; j < newMessageArr2.length - 1; j++) {
						newMessageArr2[j] = newMessageArr[j];
					}

					requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					int i= newMessageArr2.length - 1;
					newMessageArr2[i]=requestVO.getSenderLocale().toString();
					requestVO.setRequestMessageArray(newMessageArr2);
					break;             
				} 
				case 5:
				{
					final String[] newMessageArr2 = new String[newMessageArr.length];

					for(int j = 0; j < newMessageArr2.length - 1; j++) {
						newMessageArr2[j] = newMessageArr[j];
					}
					String language1 = newMessageArr[4];
					if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language1)) == null) {
						throw new BTSLBaseException(this, "validateC2SReverrsalRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					}
					requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language1)));
					break;
				}

				default :
				{
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
				}
				}

				requestVO.setReqContentType("PLAIN");
			}

			final String messageArr[] = requestVO.getRequestMessageArray();

			if (messageArr.length < 4) {
				throw new BTSLBaseException(className, "process", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);

			}

			if (!BTSLUtil.isNumeric(messageArr[2])) {
				requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
				throw new BTSLBaseException(className, "process", PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
			}

			String receiverUserCode = messageArr[2];
			receiverUserCode = operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));

			final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);

			// Getting network details
			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			if (networkPrefixVO == null) {
				throw new BTSLBaseException(className ,"process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
						new String[] { receiverUserCode }, null);
			}

			final BarredUserDAO barredUserDAO = new BarredUserDAO();

			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode, PretupsI.USER_TYPE_RECEIVER, null)) {
				throw new BTSLBaseException(className, "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
						new String[] { receiverUserCode }, null);
			}



			try {
				ChannelUserBL.validatePIN(con, ((ChannelUserVO) requestVO.getSenderVO()), messageArr[3]);
			} catch (BTSLBaseException be) {
				if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
						.equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
					con.commit();
				}
				throw be;
			}


			final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, requestVO.getFilteredMSISDN(), true, curDate,false);
			final String categoryCode= channelUserVO.getCategoryCode();


			countsVO = userTransferCountsDAO.loadTransferCounts(con, channelUserVO.getUserID(), false);



			//user life cycle check
			if (channelUserVO != null) {
				receiverAllowed = false;
				receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(),
						channelUserVO.getUserType(), requestVO.getRequestGatewayType());
				if (receiverStatusVO != null) {
					final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
					final String status[] = receiverStatusAllowed.split(",");
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(channelUserVO.getStatus())) {
							receiverAllowed = true;
						}
					}
				}
			}



			String args[] = { receiverUserCode };
			if (channelUserVO == null) {
				requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
				requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, args, null);
			} else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
				requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_IN_SUSPENDED);
				requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_IN_SUSPENDED, 0, args, null);
			} else if (receiverStatusVO == null) {
				throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
			} else if (!receiverAllowed) {
				requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
				requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args, null);
			}


			//start transfer process

			if (log.isDebugEnabled()) {
				log.debug("process", "Start Transfer Process ");
			}


			if(!(PretupsI.SOS_ALLOWED_FLAG_YES.equals(channelUserVO.getSosAllowed())))
			{
				requestVO.setMessageCode(PretupsErrorCodesI.SOS_NOT_ENABLED_USER);
				final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
				final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage , requestVO.getRequestIDStr(), requestVO
						.getRequestGatewayCode(), requestVO.getLocale());
				pushMessage.push();
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.SOS_NOT_ENABLED_USER);


			}
			if(PretupsI.SOS_PENDING_STATUS.equals(countsVO.getLastSOSTxnStatus()))
			{
				requestVO.setMessageCode(PretupsErrorCodesI.SOS_PENDING);
				final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
				final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage , requestVO.getRequestIDStr(), requestVO
						.getRequestGatewayCode(), requestVO.getLocale());
				pushMessage.push();
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.SOS_PENDING);
			}

			//code for SOS from network
			if((PretupsI.SOS_NETWORK.equalsIgnoreCase((String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, networkPrefixVO.getNetworkCode()))))

			{

				ChannelTransferVO channelTransferVO = new ChannelTransferVO();
				channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
				channelTransferVO.setControlTransfer(PretupsI.YES);
				channelTrfItemsVO.setApprovedQuantity(channelUserVO.getSosAllowedAmount());
				channelTrfItemsVO.setReceiverCreditQty(channelUserVO.getSosAllowedAmount());


				currentDate = new Date();
				// validate the channel user
				final UserVO userVO = channelUserDAO.loadOptUserForO2C(con, networkPrefixVO.getNetworkCode());


				final HashMap productMap = ChannelTransferBL.validateSOSMessageContent(con, requestVO, channelTransferVO, true);


				prdList = ChannelTransferBL.loadAndValidateProductsforSOS(con, requestVO, productMap, channelUserVO, true);

				prepareChannelTransferVO(requestVO, channelTransferVO, currentDate, channelUserVO, prdList, userVO);




				// generate transfer ID for the O2C transfer
				ChannelTransferBL.genrateTransferID(channelTransferVO);

				final UserBalancesDAO userBalDAO = new UserBalancesDAO();

				UserBalancesVO userBalanceVO = new UserBalancesVO();
				userBalanceVO.setUserID(channelUserVO.getUserID());
				userBalanceVO.setProductCode(channelTransferVO.getProductCode());
				userBalanceVO.setNetworkCode(channelTransferVO.getNetworkCode());
				userBalanceVO.setNetworkFor(channelTransferVO.getNetworkCodeFor());
				userBalanceVO.setLastTransferID(channelTransferVO.getTransferID());
				userBalanceVO.setLastTransferType(channelTransferVO.getTransferType());
				userBalanceVO.setLastTransferOn(channelTransferVO.getTransferDate());
				userBalanceVO.setPreviousBalance(userBalanceVO.getBalance());
				userBalanceVO.setQuantityToBeUpdated(channelUserVO.getSosAllowedAmount());
				userBalanceVO.setUserMSISDN(channelUserVO.getMsisdn());
				
				sosThreshold=channelUserVO.getSosThresholdLimit();
				userBalance=userBalancesDAO.loadUserBalanceForProduct(con, channelTransferVO.getTransferID(), channelUserVO.getUserID(), channelUserVO.getNetworkID(), channelUserVO.getNetworkID(), channelTransferVO.getProductCode());

				if(userBalance>sosThreshold)
				{
					requestVO.setMessageCode(PretupsErrorCodesI.SOS_THRESHOLD_NOT_REACHED);
					final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
					final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage , requestVO.getRequestIDStr(), requestVO
							.getRequestGatewayCode(), requestVO.getLocale());
					pushMessage.push();
					throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.SOS_THRESHOLD_NOT_REACHED);

				}

				int updateCount = userBalDAO.updateUserDailyBalances(con, currentDate, userBalanceVO);
				if (updateCount < 1) {
					throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
				}

				channelTransferVO.setSosProductCode(channelTransferVO.getProductCode());
				channelTransferVO.setTransactionMode(PretupsI.SOS_TRANSACTION_MODE);
				channelTransferVO.setSosStatus(PretupsI.SOS_PENDING_STATUS);
				channelTransferVO.setToUserName(channelUserVO.getUserName());
				channelTransferVO.setSosFlag(true);
				networkTransaction(con, channelTransferVO, channelUserVO.getUserID(), currentDate);

				final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

				// insert the channelTransferVO in the database

				int insertCount = 0;
				insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
				if (insertCount < 0) {
					con.rollback();
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
				}


				con.commit();
				requestVO.setSuccessTxn(true);

				ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
			    UserPhoneVO primaryPhoneVO = null;
				phoneVO = userDAO.loadUserAnyPhoneVO(con, requestVO.getRequestMSISDN());
				if (phoneVO != null) {
					channelUserVO.setPrimaryMsisdn(channelTransferVO.getToUserCode());
					channelTransferVO.setToUserCode(requestVO.getRequestMSISDN());
					primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, channelUserVO.getPrimaryMsisdn());

				}


				// sending message to receiver
				final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
				String smsKey = null;

				final ArrayList txnList = new ArrayList();
				final ArrayList balList = new ArrayList();




				final int lSize = itemsList.size();
				for (int i = 0; i < lSize; i++) {
					channelTrfItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
					keyArgumentVO = new KeyArgumentVO();
					keyArgumentVO.setKey(PretupsErrorCodesI.O2C_DIRECT_TRANSFER_SUCCESS_TXNSUBKEY);
					args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()),  PretupsBL.getDisplayAmount(channelUserVO.getSosAllowedAmount()) };
					keyArgumentVO.setArguments(args);
					txnList.add(keyArgumentVO);

					keyArgumentVO = new KeyArgumentVO();
					keyArgumentVO.setKey(PretupsErrorCodesI.O2C_DIRECT_TRANSFER_SUCCESS_BALSUBKEY);
					args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTrfItemsVO.getBalance() + channelUserVO.getSosAllowedAmount()) };
					keyArgumentVO.setArguments(args);
					balList.add(keyArgumentVO);
				}// end of for
				if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE))) {
					final String[] msgArray = { BTSLUtil.getMessage(requestVO.getSenderLocale(), txnList), BTSLUtil.getMessage(requestVO.getSenderLocale(), balList), channelTransferVO
							.getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), "operator" };
					requestVO.setMessageArguments(msgArray);
					smsKey = PretupsErrorCodesI.O2C_DIRECT_TRANSFER_RECEIVER;
				} 
				requestVO.setMessageCode(smsKey);
				requestVO.setTransactionID(channelTransferVO.getTransferID());

				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue())
				{
					if (primaryPhoneVO != null) {
						final Locale locale = new Locale(primaryPhoneVO.getPhoneLanguage(), primaryPhoneVO.getCountry());
						final String senderMessage = BTSLUtil.getMessage(locale, requestVO.getMessageCode(), requestVO.getMessageArguments());
						final PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), senderMessage, requestVO.getRequestIDStr(), requestVO
								.getRequestGatewayCode(), locale);
						pushMessage.push();
					}
				}

			}


			//code for SOS from parent or owner

			else if((PretupsI.SOS_PARENT.equalsIgnoreCase((String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, networkPrefixVO.getNetworkCode()))) || (PretupsI.SOS_OWNER.equalsIgnoreCase((String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, networkPrefixVO.getNetworkCode()))))
			{

				ChannelTransferVO channelTransferVO = new ChannelTransferVO();
				String parentMsisdn = null;

				categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con, categoryCode);

				if(categoryVO.getSequenceNumber()==1)
				{
					throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.SOS_NOT_ALLOWED_HIERARCHY);
				}

				if((PretupsI.SOS_PARENT.equalsIgnoreCase((String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, networkPrefixVO.getNetworkCode())))) 
				{
					parentMsisdn =  channelUserDAO.loadParentMsisdn(con, channelUserVO.getParentID());
				}
				else if((PretupsI.SOS_OWNER.equalsIgnoreCase((String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, networkPrefixVO.getNetworkCode())))) 

				{
					parentMsisdn =  channelUserDAO.loadParentMsisdn(con, channelUserVO.getOwnerID());
				}
				final ChannelUserVO parentVO =  channelUserDAO.loadChannelUserDetails(con, parentMsisdn);
				
				if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), parentMsisdn, PretupsI.USER_TYPE_SENDER, null)) {
					throw new BTSLBaseException(className, "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_PARENT_OWNER_BAR, 0,
							new String[] { parentMsisdn }, null);
				}

				String senderUserCode = parentMsisdn;
				senderUserCode = operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(parentMsisdn));


				//user life cycle check
				if (parentVO != null) {
					senderAllowed = false;
					senderStatusVO = (UserStatusVO) UserStatusCache.getObject(parentVO.getNetworkID(), parentVO.getCategoryCode(),
							parentVO.getUserType(), requestVO.getRequestGatewayType());
					if (senderStatusVO != null) {
						final String senderStatusAllowed = senderStatusVO.getUserSenderAllowed();
						final String status[] = senderStatusAllowed.split(",");
						for (int i = 0; i < status.length; i++) {
							if (status[i].equals(parentVO.getStatus())) {
								senderAllowed = true;
							}
						}
					}
				}



				String args1[] = { senderUserCode };
				if (parentVO == null) {
					requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
					requestVO.setMessageArguments(args1);
					throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, args1, null);
				} else if (parentVO.getOutSuspened() != null && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(parentVO.getOutSuspened())) {
					requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PARENT_OWNER_OUT_SUSPENDED);
					requestVO.setMessageArguments(args1);
					throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_PARENT_OWNER_OUT_SUSPENDED, 0, args1, null);
				} else if (senderStatusVO == null) {
					throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
				} else if (!senderAllowed) {
					requestVO.setMessageCode(PretupsErrorCodesI.CHNL_SENDER_NOTALLOWED);
					requestVO.setMessageArguments(args1);
					throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.CHNL_SENDER_NOTALLOWED, 0, args1, null);
				}



				LogFactory.printLog(methodName,  "Start Transfer Process", log);

				final String productArray[] = ChannelTransferBL.validateUserProductsFormatForSOS(messageArr, requestVO);
				final long quantity= channelUserVO.getSosAllowedAmount();

				final ArrayList productList = ChannelTransferBL.validateReqstProdsWithDefinedProdsForSOS(con, parentVO, productArray, curDate, requestVO.getLocale(),
						quantity);


				channelTransferVO.setActiveUserId(parentVO.getActiveUserID());
				channelTransferVO.setChannelTransferitemsVOList(productList);
				channelTransferVO.setFromUserName(parentVO.getUserName());
				channelTransferVO.setToUserName(channelUserVO.getUserName());

				channelTransferVO = this.prepareTransferProfileVO(parentVO, channelUserVO, productList, curDate);

				//generate transfer id
				ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);


				sosThreshold=channelUserVO.getSosThresholdLimit();
				userBalance=userBalancesDAO.loadUserBalanceForProduct(con, channelTransferVO.getTransferID(), channelUserVO.getUserID(), channelUserVO.getNetworkID(), channelUserVO.getNetworkID(), channelTransferVO.getProductCode());


				if(userBalance>sosThreshold)
				{
					requestVO.setMessageCode(PretupsErrorCodesI.SOS_THRESHOLD_NOT_REACHED);
					final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
					final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage , requestVO.getRequestIDStr(), requestVO
							.getRequestGatewayCode(), requestVO.getLocale());
					pushMessage.push();
					throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.SOS_THRESHOLD_NOT_REACHED);

				}



				final UserBalancesVO userBalancesVO = new UserBalancesVO();
				userBalancesVO.setLastTransferType(channelTransferVO.getTransferType());
				userBalancesVO.setLastTransferID(channelTransferVO.getTransferID());
				userBalancesVO.setLastTransferOn(channelTransferVO.getTransferDate());
				userBalancesVO.setUserID(parentVO.getUserID());
				userBalancesDAO.updateUserDailyBalances(con, curDate, userBalancesVO);
				userBalancesVO.setUserID(channelTransferVO.getToUserID());
				userBalancesDAO.updateUserDailyBalances(con, curDate, userBalancesVO);
				boolean fromWEB= false;
				String forwardPath= null;
				channelTransferVO.setSosFlag(true);

				//Debit the parent/owner and credit the child channel user
				int updateCount = channelUserDAO.debitUserBalances(con, channelTransferVO , fromWEB, forwardPath );
				updateCount = channelUserDAO.creditUserBalances(con, channelTransferVO , fromWEB, forwardPath);


				//update user phones after transaction
				updateCount = channelUserTxnDAO.updateUserPhoneAfterTxn(con, channelTransferVO, channelTransferVO.getToUserCode(), channelTransferVO.getToUserID(), false);

				// insert the TXN data in the parent and child tables.
				final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
				channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				channelTransferVO.setSource(requestVO.getSourceType());
				channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
				channelTransferVO.setControlTransfer(PretupsI.YES);
				channelTransferVO.setSosProductCode(channelTransferVO.getProductCode());
				channelTransferVO.setSosStatus(PretupsI.SOS_PENDING_STATUS);
				channelTransferVO.setTransactionMode(PretupsI.SOS_TRANSACTION_MODE);
				channelTransferVO.setToUserName(channelUserVO.getUserName());
				channelTransferVO.setChannelRemarks(PretupsI.SOS_TRANSFER);
				channelTransferVO.setFirstApprovedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
				channelTransferVO.setFirstApprovedOn(channelTransferVO.getTransferDate());
				channelTransferVO.setRequestGatewayCode(requestVO.getRequestGatewayCode());
				channelTransferVO.setRequestGatewayType(requestVO.getRequestGatewayType());
				channelTransferVO.setFirstApproverLimit(channelUserVO.getSosAllowedAmount());
				channelTransferVO.setSecondApprovalLimit(channelUserVO.getSosAllowedAmount());


				updateCount = channelTransferDAO.addChannelTransfer(con, channelTransferVO);

				//update transfer counts
				updateCount = ChannelTransferBL.updateChannelToChannelTransferCounts(con, channelTransferVO, curDate,  fromWEB, forwardPath);
				updateCount = userTransferCountsDAO.updateUserTransferCountsforSOS(con,channelTransferVO,channelTransferVO.getToUserID());



				// commit and prepare message if updatecount>0
				if (updateCount > 0) {
					if (log.isDebugEnabled()) {
						log.debug("process", "Commit the data ");
					}
					con.commit();

					requestVO.setSuccessTxn(true);

					// prepare balance logger
					ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);

					//sms handling

					if (!BTSLUtil.isNullString(requestVO.getMsisdn())) {
						if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
							phoneVO = userDAO.loadUserAnyPhoneVO(con, requestVO.getMsisdn());
							if (phoneVO != null && ((channelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
								if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue() && ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
									receiverPrimaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, channelUserVO.getMsisdn());
								}
								channelUserVO.setPrimaryMsisdn(channelUserVO.getMsisdn());
								channelUserVO.setMsisdn(requestVO.getMsisdn());
							} else {
								throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
							}
						} else if (!requestVO.getMsisdn().equalsIgnoreCase(channelUserVO.getMsisdn())) {
							throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
						}
					}

					// sending sms to sender
					final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
					final ArrayList txnList = new ArrayList();
					final ArrayList balList = new ArrayList();
					UserPhoneVO senderPrimaryPhoneVO = null;
					requestVO.setReceiverLocale(new Locale((parentVO.getUserPhoneVO()).getPhoneLanguage(), (parentVO.getUserPhoneVO()).getCountry()));
					String reqruestGW = requestVO.getRequestGatewayCode();
					args = null;
					String smsKey =  null;
					if (parentVO.isStaffUser() && parentVO.getUserPhoneVO().getMsisdn().equals(requestVO.getMessageSentMsisdn())) {
						smsKey = PretupsErrorCodesI.CHNL_TRF_SUCCESS_STAFF;
					} else {
						smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS;
					}
					if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(channelTransferVO.getTransferCategory())) {
						smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_SENDER_AGENT;
					}

					if (phoneVO == null) {
						phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
					}

					final int lSize = itemsList.size();
					for (int i = 0; i < lSize; i++) {
						channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY);
						args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()) };
						keyArgumentVO.setArguments(args);
						txnList.add(keyArgumentVO);

						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
						if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()){
							args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferItemsVO
									.getAfterTransSenderPreviousStock() - channelTransferItemsVO.getRequiredQuantity()) };
						}else{
							args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferItemsVO.getTotalSenderBalance() - channelTransferItemsVO.getRequiredQuantity()) };
						}
						keyArgumentVO.setArguments(args);
						balList.add(keyArgumentVO);
					}// end of for
					String[] array = null;
					if (parentVO.isStaffUser() && parentVO.getUserPhoneVO().getMsisdn().equals(requestVO.getMessageSentMsisdn())) {
						array = new String[] { BTSLUtil.getMessage(requestVO.getReceiverLocale(), txnList), BTSLUtil.getMessage(requestVO.getLocale(), balList), channelTransferVO
								.getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(), parentVO.getStaffUserDetails()
								.getUserName() };
					} else {
						array = new String[] { BTSLUtil.getMessage(requestVO.getReceiverLocale(), txnList), BTSLUtil.getMessage(requestVO.getLocale(), balList), channelTransferVO
								.getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn() };
					}

					requestVO.setMessageArguments(array);
					requestVO.setMessageCode(smsKey);
					requestVO.setTransactionID(channelTransferVO.getTransferID());
					String allowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2C_SEN_MSG_REQD_GW"));
					if (parentVO.isStaffUser() && (!parentVO.getUserPhoneVO().getMsisdn().equals(requestVO.getMessageSentMsisdn()) || requestVO.getMessageGatewayVO()
							.getGatewayType().equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS))) {
						final String[] arrMsg = { BTSLUtil.getMessage(requestVO.getReceiverLocale(), txnList), BTSLUtil.getMessage(requestVO.getReceiverLocale(), balList), channelTransferVO.getTransferID(), PretupsBL
								.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(), parentVO.getStaffUserDetails().getUserName() };
						final String senderMessage = BTSLUtil.getMessage(requestVO.getSenderLocale(), PretupsErrorCodesI.CHNL_TRF_SUCCESS_STAFF, arrMsg);
						PushMessage pushMessage = new PushMessage(senderUserCode, senderMessage, requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(),
								requestVO.getReceiverLocale());
						pushMessage.push();
					}

					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
						senderPrimaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, parentVO.getPrimaryMsisdn());
					}
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
						if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(requestVO.getServiceType()) && requestVO.isSenderMessageRequired()) {

							if (senderPrimaryPhoneVO != null) {
								final Locale locale1 = new Locale(senderPrimaryPhoneVO.getPhoneLanguage(), senderPrimaryPhoneVO.getCountry());
								final String senderMessage = BTSLUtil.getMessage(locale1, requestVO.getMessageCode(), requestVO.getMessageArguments());
								PushMessage pushMessage = new PushMessage(parentVO.getPrimaryMsisdn(), senderMessage, requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(),
										locale1);
								pushMessage.push();
							}
						}
					}

					String senderMessage = BTSLUtil.getMessage(requestVO.getReceiverLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
					PushMessage pushMessage = new PushMessage(senderUserCode, senderMessage, requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(),
							requestVO.getReceiverLocale());
					pushMessage.push();

					int messageLength = 0;
					final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
					if (!BTSLUtil.isNullString(messLength)) {
						messageLength = (new Integer(messLength)).intValue();
					}
					if (!reqruestGW.equalsIgnoreCase(requestVO.getRequestGatewayCode())) {
						senderMessage = BTSLUtil.getMessage(requestVO.getReceiverLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
						pushMessage = new PushMessage(senderUserCode, senderMessage, requestVO.getRequestIDStr(), reqruestGW, requestVO.getReceiverLocale());
						if ((messageLength > 0) && (senderMessage.length() < messageLength)) {
							pushMessage.push();
						}
					}
					// sending sms to receiver

					smsKey = PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER;
					if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(parentVO.getTransferCategory())) {
						smsKey = PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_RECEIVER_AGENT;
					}

					final String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
					if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
						if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
							reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();

							LogFactory.printLog(methodName, "process: Receiver Message push through alternate GW" + reqruestGW + "Requested GW is:" + requestVO.getRequestGatewayCode(), log);
						}
					}

					Locale locale = requestVO.getSenderLocale();
					final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO,
							PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
					final String[] array1 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
							.getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), senderUserCode };
					requestVO.setMessageArguments(array1);
					requestVO.setMessageCode(smsKey);

					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
						receiverPrimaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, channelUserVO.getMsisdn());
					}

					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue())
					{

						BTSLMessages messages = new BTSLMessages(smsKey, array1);
						pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), reqruestGW, locale, channelTransferVO
								.getNetworkCode());
						pushMessage.push();
					}
					return;


				}

			}





		}

		catch (BTSLBaseException be) {
			requestVO.setSuccessTxn(false);
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			log.error(methodName, "BTSLBaseException " + be.getMessage());
			log.errorTrace(methodName, be);
			if (be.getMessageList() != null && !(be.getMessageList().isEmpty())) {
				final String[] array = { BTSLUtil.getMessage(requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				requestVO.setMessageArguments(be.getArgs());
			}
			if (be.getMessageKey() != null) {
				requestVO.setMessageCode(be.getMessageKey());
			} else {
				requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			return;
		} catch (Exception ex) {
			requestVO.setSuccessTxn(false);
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception ee) {
				log.errorTrace(methodName, ee);
			}
			log.error(methodName, "BTSLBaseException " + ex.getMessage());
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CDirectTransferController[process]", "", "", "",
					"Exception:" + ex.getMessage());
			requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			return;
		} finally {
			// closing database connection
			if (mcomCon != null) {
				mcomCon.close("SOSTransferRequestHandler#process");
				mcomCon = null;
			}
			LogFactory.printLog(methodName, "Exiting:", log);
		}
	}


	/**
	 * Method networkTransaction
	 * This method responsible to Approve the O2C transaction and update
	 * the network stock, update the user balances and user counts
	 * 
	 * @param con
	 *            Connection
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 * @param date
	 *            Date
	 * @param userID
	 *            String
	 * @throws BTSLBaseException
	 */
	private void networkTransaction(Connection con, ChannelTransferVO channelTransferVO, String userID, Date date) throws BTSLBaseException
	{
		final String methodName="networkTransaction";
		LogFactory.printLog(methodName, "Entering  : channelTransferVO:" + channelTransferVO + "userID:" + userID + "date:" + date, log);

		try {

			int updateCount = -1;
			int updateCount2 = -1;
			UserTransferCountsDAO userTransferCountsDAO= new UserTransferCountsDAO();

			updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con, channelTransferVO, userID, date, true);
			if (updateCount < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}

			updateCount = -1;
			updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(con, channelTransferVO, userID, date);
			if (updateCount < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}

			channelTransferVO.setStockUpdated(TypesI.YES);
			channelTransferVO.setChannelRemarks(PretupsI.SOS_TRANSFER);
			channelTransferVO.setFirstApprovedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
			channelTransferVO.setFirstApprovedOn(channelTransferVO.getTransferDate());
			channelTransferVO.setFirstApproverLimit(channelTransferVO.getTransferMRP());
			channelTransferVO.setSecondApprovalLimit(channelTransferVO.getTransferMRP());


			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(con, channelTransferVO, false, null);
			} else {
				updateCount = channelUserDAO.creditUserBalances(con, channelTransferVO, false, null);
			}

			if (updateCount < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}



			updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(con, channelTransferVO, null, date);
			if (updateCount < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}
			updateCount2 = userTransferCountsDAO.updateUserTransferCountsforSOS(con,channelTransferVO,userID);
			if (updateCount2 < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			} 
		} finally {
			LogFactory.printLog(methodName, "Exiting:", log);
		}

	}

	/**
	 * Method prepareChannelTransferVO
	 * This method used to construct the VO for channel transfer
	 * 
	 * @param requestVO
	 *            RequestVO
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 * @param curDate
	 *            Date
	 * @param channelUserVO
	 *            ChannelUserVO
	 * @param prdList
	 *            ArrayList
	 * @throws BTSLBaseException
	 */
	private void prepareChannelTransferVO(RequestVO requestVO, ChannelTransferVO channelTransferVO, Date curDate, ChannelUserVO channelUserVO, ArrayList prdList, UserVO p_userVO) throws BTSLBaseException
	{
		String methodName="prepareChannelTransferVO";

		LogFactory.printLog(methodName, "Entering  : requestVO " + requestVO + "channelTransferVO:" + channelTransferVO + "curDate:" + curDate + "channelUserVO:" + channelUserVO + "prdList:" + prdList + "p_userVO:" + p_userVO
				+ "sourceType: " + requestVO.getSourceType(), log);

		channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
		channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
		channelTransferVO.setDomainCode(channelUserVO.getDomainID());
		channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
		channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
		channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);

		channelTransferVO.setReceiverGradeCode(channelUserVO.getUserGrade());
		channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
		channelTransferVO.setToUserID(channelUserVO.getUserID());
		channelTransferVO.setToUserCode(channelUserVO.getUserCode());
		channelTransferVO.setUserMsisdn(channelUserVO.getUserCode());
		channelTransferVO.setTransferDate(curDate);
		channelTransferVO.setCommProfileSetId(channelUserVO.getCommissionProfileSetID());
		channelTransferVO.setCommProfileVersion(channelUserVO.getCommissionProfileSetVersion());
		channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());
		channelTransferVO.setCreatedOn(curDate);
		channelTransferVO.setCreatedBy(p_userVO.getUserID());
		channelTransferVO.setModifiedOn(curDate);
		channelTransferVO.setModifiedBy(p_userVO.getUserID());
		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
		channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
		channelTransferVO.setSource(requestVO.getSourceType());

		channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
		channelTransferVO.setReceiverDomainCode(channelUserVO.getDomainID());
		channelTransferVO.setToUserCode(channelUserVO.getUserCode());
		channelTransferVO.setRequestGatewayCode(requestVO.getRequestGatewayCode());
		channelTransferVO.setRequestGatewayType(requestVO.getRequestGatewayType());
		channelTransferVO.setActiveUserId(p_userVO.getUserID());

		ChannelTransferItemsVO channelTransferItemsVO = null;
		String productType = null;
		String productCode =  null;
		long totRequestQty = 0;
		long totPayAmt = 0;
		long totNetPayAmt = 0;
		long totTax1 = 0;
		long totTax2 = 0;
		long totTax3 = 0;
		long commissionQty = 0; 
		long senderDebitQty = 0;
		long receiverCreditQty = 0;
		for (int i = 0, k = prdList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) prdList.get(i);
			totRequestQty += channelUserVO.getSosAllowedAmount();

			totPayAmt += channelUserVO.getSosAllowedAmount();
			totNetPayAmt += channelUserVO.getSosAllowedAmount();
			totTax1 += channelTransferItemsVO.getTax1Value();
			totTax2 += channelTransferItemsVO.getTax2Value();
			totTax3 += channelTransferItemsVO.getTax3Value();

			productType = channelTransferItemsVO.getProductType();
			productCode =  channelTransferItemsVO.getProductCode();
			commissionQty += channelTransferItemsVO.getCommQuantity();
			senderDebitQty += channelUserVO.getSosAllowedAmount();
			receiverCreditQty += channelUserVO.getSosAllowedAmount();
		}

		channelTransferVO.setPayableAmount(totPayAmt);
		channelTransferVO.setTotalTax1(totTax1);
		channelTransferVO.setTotalTax2(totTax2);
		channelTransferVO.setTotalTax3(totTax3);
		channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		channelTransferVO.setChannelTransferitemsVOList(prdList);
		channelTransferVO.setPayInstrumentAmt(channelUserVO.getSosAllowedAmount());
		channelTransferVO.setProductType(productType);
		channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		channelTransferVO.setSenderDrQty(channelUserVO.getSosAllowedAmount());
		channelTransferVO.setReceiverCrQty(channelUserVO.getSosAllowedAmount());
		channelTransferVO.setRequestedQuantity(channelUserVO.getSosAllowedAmount());
		channelTransferVO.setTransferMRP(channelUserVO.getSosAllowedAmount());
		channelTransferVO.setNetPayableAmount(channelUserVO.getSosAllowedAmount());
		channelTransferVO.setProductCode(productCode);


		LogFactory.printLog(methodName, "Exiting : ", log);
	}

	/**
	 * Method prepareTransferProfileVO
	 * This method construct the VO for the Txn
	 * 
	 * @param senderVO
	 * @param receiverVO
	 * @param productList
	 * @param curDate
	 * @return ChannelTransferVO
	 * @throws BTSLBaseException
	 */
	private ChannelTransferVO prepareTransferProfileVO(ChannelUserVO senderVO, ChannelUserVO receiverVO, ArrayList productList, Date curDate) throws BTSLBaseException
	{
		String methodName="prepareTransferProfileVO";

		LogFactory.printLog(methodName," Entered  senderVO: " + senderVO + " receiverVO:" + receiverVO + " productList:" + productList.size() + " curDate:" + curDate, log);


		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

		channelTransferVO.setNetworkCode(senderVO.getNetworkID());
		channelTransferVO.setNetworkCodeFor(senderVO.getNetworkID());
		channelTransferVO.setGraphicalDomainCode(senderVO.getGeographicalCode());
		channelTransferVO.setDomainCode(senderVO.getDomainID());
		channelTransferVO.setCategoryCode(senderVO.getCategoryCode());
		channelTransferVO.setSenderGradeCode(senderVO.getUserGrade());
		channelTransferVO.setReceiverGradeCode(receiverVO.getUserGrade());
		channelTransferVO.setFromUserID(senderVO.getUserID());
		channelTransferVO.setToUserID(receiverVO.getUserID());
		channelTransferVO.setTransferDate(curDate);
		channelTransferVO.setCommProfileSetId(receiverVO.getCommissionProfileSetID());
		channelTransferVO.setCommProfileVersion(receiverVO.getCommissionProfileSetVersion());
		channelTransferVO.setDualCommissionType(receiverVO.getDualCommissionType());
		channelTransferVO.setCreatedOn(curDate);
		channelTransferVO.setCreatedBy(senderVO.getUserID());
		channelTransferVO.setModifiedOn(curDate);
		channelTransferVO.setModifiedBy(senderVO.getUserID());
		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		channelTransferVO.setTransferInitatedBy(senderVO.getUserID());
		channelTransferVO.setSenderTxnProfile(senderVO.getTransferProfileID());
		channelTransferVO.setReceiverTxnProfile(receiverVO.getTransferProfileID());
		channelTransferVO.setReceiverCategoryCode(receiverVO.getCategoryCode());
		channelTransferVO.setTransferCategory(senderVO.getTransferCategory());
		channelTransferVO.setReceiverGgraphicalDomainCode(receiverVO.getGeographicalCode());
		channelTransferVO.setReceiverDomainCode(receiverVO.getDomainID());
		channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(receiverVO.getUserCode()));
		channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(senderVO.getMsisdn()));

		long totRequestQty = 0;
		long totPayAmt = 0;
		long totNetPayAmt = 0;
		long totTax1 = 0;
		long totTax2 = 0;
		long totTax3 = 0;
		long commissionQty = 0; 
		long senderDebitQty = 0;
		long receiverCreditQty = 0;
		long totMRP = 0;
		String productCode =  null;

		for (int i = 0, k = productList.size(); i < k; i++) {
			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
			totRequestQty += receiverVO.getSosAllowedAmount();
			totMRP  += receiverVO.getSosAllowedAmount();
			totPayAmt += receiverVO.getSosAllowedAmount();
			totNetPayAmt += receiverVO.getSosAllowedAmount();
			totTax1 += channelTransferItemsVO.getTax1Value();
			totTax2 += channelTransferItemsVO.getTax2Value();
			totTax3 += channelTransferItemsVO.getTax3Value();
			commissionQty += channelTransferItemsVO.getCommQuantity();
			senderDebitQty += receiverVO.getSosAllowedAmount();
			receiverCreditQty += receiverVO.getSosAllowedAmount();
			productCode =  channelTransferItemsVO.getProductCode();
		}// end of for
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

		channelTransferVO.setChannelTransferitemsVOList(productList);
		channelTransferVO.setActiveUserId(senderVO.getActiveUserID());

		LogFactory.printLog(methodName, "Exiting : ", log);

		return channelTransferVO;
	}


}

