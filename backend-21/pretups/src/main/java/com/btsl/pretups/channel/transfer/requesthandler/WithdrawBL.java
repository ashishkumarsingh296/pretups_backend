package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgList;
import com.btsl.common.RowErrorMsgLists;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChnlToChnlTransferTransactionCntrl;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OneLineTXNLog;
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
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.XmlTagValueConstant;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;
import com.web.user.businesslogic.UserWebDAO;

public class WithdrawBL {
	protected final static Log _log = LogFactory.getLog(TransferBL.class.getName());
	private static String _allowedSendMessGatw = null;
    public static OperatorUtilI _operatorUtil = null;
    private static boolean _receiverMessageSendReq=false;
	private static boolean _ussdReceiverMessageSendReq=false;
  
	final static String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
    
	public static void c2cWithdrawService(Connection con,OAuthUser authUser,RequestVO p_requestVO,DataStockMul dataStkTrfMul,ChannelUserVO senderVO,Boolean fileOrno ) throws Exception {
		  
		try {
	        _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	    } catch (Exception e) {
	        _log.errorTrace("static", e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
	            "Exception while loading the class at the call:" + e.getMessage());
	    }
	
	
	     
    

    UserPhoneVO userPhoneVO = null;
    if (!senderVO.isStaffUser()) {
        userPhoneVO = senderVO.getUserPhoneVO();
    } else {
        userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
    }
    p_requestVO.setSenderVO(senderVO);
    int level=((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.C2C_BATCH_APPROVAL_LEVEL)).intValue();
    
    // ///
    // message format
    // [keyword][usercode] [qty] [productcode] [qty] [productcode]
    // [password]
    // 1.) if password is PASSWRD the not to validate the user password
    // 2.) usercode should be numeric
    // 3.) product code should be numeric
    // 4.) qty should be numeric 5.) product code and qty should be always
    // with each
    // other 6.) load the receiver information on the base of usercode Check
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
       
    ChannelUserTxnDAO channelUserTxnDAO = null;
    	 
        channelUserTxnDAO = new ChannelUserTxnDAO();
        // Validate the user is IN suspended or not, if user is IN suspended
        // then show error message

        if (senderVO != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(senderVO.getInSuspend())) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED);
            throw new BTSLBaseException("WithdrawBL", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED,new String[]{senderVO.getUserCode()});
        }

		final HashMap requestMap = p_requestVO.getRequestMap();
   
		_receiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,senderVO.getNetworkCode(),p_requestVO.getServiceType())).booleanValue();
		_ussdReceiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW,senderVO.getNetworkCode(),p_requestVO.getServiceType())).booleanValue();

        final Date curDate = new Date();
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        ChannelUserVO receiverChannelUserVO = null;
        ChannelUserVO receiverChannelUserVO2 = null;
        
        boolean isUserDetailLoad = false;
        UserPhoneVO PrimaryPhoneVO_R = null;
        final UserDAO userDAO = new UserDAO();
        UserPhoneVO phoneVO = null;
        boolean receiverAllowed = false;
        boolean senderAllowed = false;
        UserStatusVO senderStatusVO = null;
        UserStatusVO receiverStatusVO = null;
        if(BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2()) && BTSLUtil.isNullString(dataStkTrfMul.getExtcode2()) && BTSLUtil.isNullString(dataStkTrfMul.getLoginid2())){
			throw new BTSLBaseException("WithdrawBL", "process",
					PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, 0, null);
		}
        if(BTSLUtil.isNullorEmpty(dataStkTrfMul.getProducts()))
		{
			throw new BTSLBaseException("WithdrawBL", "process",
					PretupsErrorCodesI.PRODUCT_NOT_EXIST, 0, null);
		}
		for(int i=0;i<dataStkTrfMul.getProducts().size();i++)
		{
			if(BTSLUtil.isNullString(dataStkTrfMul.getProducts().get(i).getProductcode()))
			{
				throw new BTSLBaseException("WithdrawBL", "process",
						PretupsErrorCodesI.PRODUCT_NOT_EXIST, 0, null);
			}
		}
        if (!BTSLUtil.isNullString(dataStkTrfMul.getExtcode2())) {
            receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, dataStkTrfMul.getExtcode2(), null, curDate);
            if (receiverChannelUserVO == null) {
                throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
            }

            isUserDetailLoad = true;
        } else if (!BTSLUtil.isNullString(dataStkTrfMul.getLoginid2())) {
            receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null, dataStkTrfMul.getLoginid2(), curDate);
            if (receiverChannelUserVO == null) {
                throw new BTSLBaseException("C2CWithdrawController", "C2CWithdrawService", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
            }

            isUserDetailLoad = true;
        }
        else if(!BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2()))
        {      
        	receiverChannelUserVO = channelUserDAO.loadChannelUserDetails(con, dataStkTrfMul.getMsisdn2());
        if (receiverChannelUserVO == null) {
            throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
        }
        isUserDetailLoad = true;
        }
        
        if (receiverChannelUserVO!= null && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(receiverChannelUserVO.getOutSuspened())) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
            throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.USER_TRANSFER_CHANNEL_OUT_SUSPENDED,new String[]{receiverChannelUserVO2.getUserCode()});
        }
        if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
            if (!BTSLUtil.isNullString(dataStkTrfMul.getExtcode2())) {
                if (!dataStkTrfMul.getExtcode2().equalsIgnoreCase(receiverChannelUserVO.getExternalCode())) {
                    throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
                }
            }
            if (!BTSLUtil.isNullString(dataStkTrfMul.getLoginid2())) {
                if (!dataStkTrfMul.getLoginid2().equalsIgnoreCase(receiverChannelUserVO.getLoginID())) {
                    throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
                }
            }
            if (!BTSLUtil.isNullString(dataStkTrfMul.getMsisdn2())) {
                if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
                    phoneVO = userDAO.loadUserAnyPhoneVO(con, dataStkTrfMul.getMsisdn2());
                    if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
                        if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED && ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                            PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
                        }
                        receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
                        receiverChannelUserVO.setMsisdn(dataStkTrfMul.getMsisdn2());
                    } else {
                        throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
                    }
                } else if (!dataStkTrfMul.getMsisdn2().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
                    throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
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
			
            /*if (BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn()) && BTSLUtil.isNullString(((String)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)))) {
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
            }*/
        }
        final String messageArr[] = p_requestVO.getRequestMessageArray();
        final int messageLen = messageArr.length;

        if (messageArr.length < 2) {
            throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { p_requestVO
                .getActualMessageFormat() }, null);
        }

        if (!BTSLUtil.isNumeric(messageArr[1])) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
            throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
        }// end if

        final String productArray[] = ChannelTransferBL.validateUserProductsFormatForSMS(messageArr, p_requestVO);
        final int msgLen = messageArr.length;
       /* if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
            try {
                // validating PIN
                ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[msgLen - 1]);
            } catch (BTSLBaseException be) {
                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                  // commiting transaction if validate PIN
                	mcomCon.finalCommit();
                }
                // is failed
                throw be;
            }
        }*/// end if

        String receiverUserCode = messageArr[1];

        receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
        if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
            throw new BTSLBaseException("C2CReturnController", "C2CWithdrawService", PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
        }
        final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);

        // Getting network details
        final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
        if (networkPrefixVO == null) {
            throw new BTSLBaseException("C2CReturnController", "C2CWithdrawService", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
                new String[] { receiverUserCode }, null);
        }

        final BarredUserDAO barredUserDAO = new BarredUserDAO();

        if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode, PretupsI.USER_TYPE_RECEIVER, null)) {
            throw new BTSLBaseException("C2CReturnController", "C2CWithdrawService", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
                new String[] { receiverUserCode }, null);
        }

        if (phoneVO == null) {
            phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
        }

        if (!isUserDetailLoad) {
            if (!SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
                receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode, true, curDate,false);
            } else {
                if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                    receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, phoneVO.getUserId(), false, curDate,false);
                    if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
                        PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
                    }
                    receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
                    receiverChannelUserVO.setMsisdn(receiverUserCode);
                } else {
                    receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode, true, curDate,false);
                }
            }
        }

        KeyArgumentVO keyArgumentVO = null;
        boolean isOutsideHierarchy = false;
        ChannelTransferItemsVO channelTransferItemsVO = null;

        if (receiverChannelUserVO != null) {
            receiverAllowed = false;
            receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverChannelUserVO.getNetworkID(), receiverChannelUserVO.getCategoryCode(),
                receiverChannelUserVO.getUserType(), p_requestVO.getRequestGatewayType());
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
        String args[] = { receiverUserCode };

        if (receiverChannelUserVO == null) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
            p_requestVO.setMessageArguments(args);
            throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, args, null);
        } else if (receiverStatusVO == null) {
            throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
        } else if (!receiverAllowed) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
            p_requestVO.setMessageArguments(args);
            throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args, null);
        } else if (receiverChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
            p_requestVO.setMessageArguments(args);
            throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args, null);
        }// end if

        final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getLocale());
        if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
            senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang1Msg());
            receiverChannelUserVO.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang1Msg());
        } else {
            senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang2Msg());
            receiverChannelUserVO.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang2Msg());
        }// end if en

        isOutsideHierarchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, receiverChannelUserVO, senderVO, true, null, false,
            PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);

        final ArrayList productList = ChannelTransferBL
            .validReqstProdsWithDfndProdsForWdAndRet(con, receiverChannelUserVO, productArray, curDate, p_requestVO.getLocale());
        ChannelTransferVO channelTransferVO = new ChannelTransferVO();
        channelTransferVO.setChannelTransferitemsVOList(productList);
        channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        channelTransferVO.setToUserID(receiverChannelUserVO.getUserID());
        channelTransferVO.setOtfFlag(false);
        
        ChannelTransferBL.loadAndCalculateTaxOnProducts(con, receiverChannelUserVO.getCommissionProfileSetID(), receiverChannelUserVO.getCommissionProfileSetVersion(),
            channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_C2C);

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
        channelTransferVO = prepareTransferProfileVO(senderVO, receiverChannelUserVO, productList, curDate);
        if (isOutsideHierarchy) {
            channelTransferVO.setControlTransfer(PretupsI.NO);
        } else {
            channelTransferVO.setControlTransfer(PretupsI.YES);
        }
        channelTransferVO.setDefaultLang(p_requestVO.getSmsDefaultLang());
		channelTransferVO.setSecondLang(p_requestVO.getSmsSecondLang());
        channelTransferVO.setSource(p_requestVO.getSourceType());
        channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        // for Ussd
        channelTransferVO.setCellId(p_requestVO.getCellId());
        channelTransferVO.setSwitchId(p_requestVO.getSwitchId());
        channelTransferVO.setReferenceNum(p_requestVO.getExternalReferenceNum());
        p_requestVO.setChannelTransferVO(channelTransferVO);
		final Boolean isTagReq=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED))).booleanValue();
		if(requestMap!=null && isTagReq)
		{
			if(requestMap.containsKey("REMARKS"))
			{
				final String remarks = (String) requestMap.get("REMARKS");
				channelTransferVO.setChannelRemarks(remarks);
			}
			if(requestMap.containsKey("INFO1"))
			{
				final String info1 = (String) requestMap.get("INFO1");
				channelTransferVO.setInfo1(info1);
			}
			if(requestMap.containsKey("INFO2"))
			{			
				final String info2 = (String) requestMap.get("INFO2");
				channelTransferVO.setInfo2(info2);
			} 
		}
		 if(fileOrno)
         {
         	channelTransferVO.setIsFileC2C("Y");
         }
		   
	   if((channelTransferVO.getIsFileC2C().equals("Y")&&level==0)||channelTransferVO.getIsFileC2C().equals("N"))
	   {
        final int updateCount = ChnlToChnlTransferTransactionCntrl.withdrawAndReturnChannelToChannel(con, channelTransferVO, isOutsideHierarchy, false, null, curDate);

        if (!senderVO.isStaffUser()) {
            (senderVO.getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
            (senderVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
        } else {
            (senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
            (senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
        }

        if (updateCount > 0) {
        	
        	 if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue())
 	           { 
	             	UserEventRemarksVO userRemarskVO=null;
					ArrayList<UserEventRemarksVO> c2cRemarks=null;
					if(channelTransferVO!=null)
		    		   {
		    			   int insertCount=0;
		    			   c2cRemarks=new ArrayList<UserEventRemarksVO>();
	                  	userRemarskVO=new UserEventRemarksVO();
	                  	userRemarskVO.setCreatedBy(channelTransferVO.getCreatedBy());
	                  	userRemarskVO.setCreatedOn(new Date());
	                  	userRemarskVO.setEventType(PretupsI.TRANSFER_TYPE_C2C);
	                  	userRemarskVO.setRemarks(channelTransferVO.getChannelRemarks());
	                  	userRemarskVO.setMsisdn(channelTransferVO.getFromUserCode());
	                  	userRemarskVO.setUserID(channelTransferVO.getFromUserID());
	                  	userRemarskVO.setUserType("SENDER");
	                  	userRemarskVO.setModule(PretupsI.C2C_MODULE);
	                  	c2cRemarks.add(userRemarskVO);
	                  	insertCount=new UserWebDAO().insertEventRemark(con, c2cRemarks);
	                  	if(insertCount<=0)
	                  	{
	     	                 _log.error("C2CWithdrawService","Error: while inserting into userEventRemarks Table");
	     	                 throw new BTSLBaseException("C2CWithdrawService","save","error.general.processing");
	                  	}
	                  	
		    		   }
 	           }
        	
        	 if(channelTransferVO.getIsFileC2C().equals("N"))
 			{
            if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                try {
                    senderAllowed = false;
                    senderStatusVO = (UserStatusVO) UserStatusCache.getObject(senderVO.getNetworkID(), senderVO.getCategoryCode(), senderVO.getUserType(), p_requestVO
                        .getRequestGatewayType());
                    if (senderStatusVO == null) {
                        throw new BTSLBaseException("WithdrawBL", "C2CWithdrawService", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
                    } else {
                        final String senderStatusAllowed = senderStatusVO.getUserSenderAllowed();
                        final String status[] = senderStatusAllowed.split(",");
                        for (int i = 0; i < status.length; i++) {
                            if (status[i].equals(senderVO.getStatus())) {
                                senderAllowed = true;
                            }
                        }

                        PretupsBL.chkAllwdStatusToBecomeActive(con, SystemPreferences.TXN_SENDER_USER_STATUS_CHANG, senderVO.getUserID(), senderVO.getStatus());
                        PretupsBL.chkAllwdStatusToBecomeActive(con, SystemPreferences.TXN_RECEIVER_USER_STATUS_CHANG, receiverChannelUserVO.getUserID(),
                            receiverChannelUserVO.getStatus());
                    }

                } catch (Exception ex) {
                    _log.error("C2CWithdrawService", "Exception while changing user state to active  " + ex.getMessage());
                    _log.errorTrace("C2CWithdrawService", ex);
                } 
            }// end of changes
            //Added for OneLine Logging for Channel
			OneLineTXNLog.log(channelTransferVO, null);

            ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);

            final String receiverTxnSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER_TXNSUBKEY;
            final String receiverBalSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER_BALSUBKEY;
            String smsKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER;
            if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(senderVO.getTransferCategory())) {
                smsKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_RECEIVER_AGENT;
            }
            args = null;
            final ArrayList txnList = new ArrayList();
            final ArrayList balList = new ArrayList();
            final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
            final int lSize = itemsList.size();
            for (int i = 0; i < lSize; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(receiverTxnSubKey);
                args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO.getRequestedQuantity() };
                keyArgumentVO.setArguments(args);
                txnList.add(keyArgumentVO);

                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(receiverBalSubKey);
                // args= new
                // String[]{String.valueOf(channelTransferItemsVO.getShortName()),PretupsBL.getDisplayAmount(channelTransferItemsVO.getBalance()-channelTransferItemsVO.getRequiredQuantity())};
                if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()){
                args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferItemsVO
                    .getAfterTransSenderPreviousStock() - channelTransferItemsVO.getRequiredQuantity()) };
                }
                else{
                	args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferItemsVO.getTotalSenderBalance() - channelTransferItemsVO.getRequiredQuantity()) };
                }
                keyArgumentVO.setArguments(args);
                balList.add(keyArgumentVO);
            }// end of for

            // generating message
            final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), p_requestVO.getFilteredMSISDN() };

            final Locale locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
            final BTSLMessages messages = new BTSLMessages(smsKey, array);
        	PushMessage pushMessage=null;
			/*USSDPushMessage ussdPushMessage=null;*/
		
			//Pushing message to receiver as SMS
			if(_receiverMessageSendReq)
			{   // Pushing message to receiver
             pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), p_requestVO.getRequestGatewayCode(), locale,
                channelTransferVO.getNetworkCode());
            pushMessage.push();
            if (PrimaryPhoneVO_R != null) {
                final Locale locale1 = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(), PrimaryPhoneVO_R.getCountry());
                pushMessage = new PushMessage(receiverChannelUserVO.getPrimaryMsisdn(), messages, channelTransferVO.getTransferID(), p_requestVO.getRequestGatewayCode(),
                    locale1, channelTransferVO.getNetworkCode());
                pushMessage.push();
            }

			}
			//Pushing message to receiver as USSD Flash
			/*if(_ussdReceiverMessageSendReq)
			{
				ussdPushMessage=new USSDPushMessage(phoneVO.getMsisdn(),messages,channelTransferVO.getTransferID(),p_requestVO.getRequestGatewayCode(),locale,channelTransferVO.getNetworkCode()); 
				ussdPushMessage.push();
				if(PrimaryPhoneVO_R != null)
				{
					Locale locale1 = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(),PrimaryPhoneVO_R.getCountry());
					ussdPushMessage=new USSDPushMessage(receiverChannelUserVO.getPrimaryMsisdn(),messages,channelTransferVO.getTransferID(),p_requestVO.getRequestGatewayCode(),locale1,channelTransferVO.getNetworkCode());
					ussdPushMessage.push();
				}
			}*/

            // Preparing Sender message to sender
            final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO,
                PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_TXNSUBKEY, PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_BALSUBKEY);
            String[] array1 = null;
            if (senderVO.isStaffUser() && senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn())) {
                smsKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_STAFF;
                array1 = new String[] { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                    .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(), senderVO.getStaffUserDetails()
                    .getUserName() };
            }

            else {
                smsKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS;
                array1 = new String[] { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                    .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn() };
            }

            if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(senderVO.getTransferCategory())) {
                smsKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_SENDER_AGENT;
            }
            p_requestVO.setMessageArguments(array1);
            p_requestVO.setMessageCode(smsKey);
            p_requestVO.setTransactionID(channelTransferVO.getTransferID());
            _allowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2C_SEN_MSG_REQD_GW"));
            if (SystemPreferences.SECONDARY_NUMBER_ALLOWED && SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(p_requestVO.getServiceType()) && p_requestVO.isSenderMessageRequired()) {
                    if (primaryPhoneVO_S != null) {
                        final Locale locale1 = new Locale(primaryPhoneVO_S.getPhoneLanguage(), primaryPhoneVO_S.getCountry());
                        final String senderMessage = BTSLUtil.getMessage(locale1, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                        pushMessage = new PushMessage(senderVO.getPrimaryMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
                            locale1);
                        pushMessage.push();
                    }
                }
            }
            if (senderVO.isStaffUser() && (!senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn()) || p_requestVO.getMessageGatewayVO()
                .getGatewayType().equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS)))
            // if(senderVO.isStaffUser() &&
            // !senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn()))
            {
                final Locale parentLocale = new Locale(senderVO.getUserPhoneVO().getPhoneLanguage(), senderVO.getUserPhoneVO().getCountry());
                final String[] arrMsg = { BTSLUtil.getMessage(parentLocale, txnList), BTSLUtil.getMessage(parentLocale, balList), channelTransferVO.getTransferID(), PretupsBL
                    .getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(), senderVO.getStaffUserDetails().getUserName() };
                final String senderMessage = BTSLUtil.getMessage(parentLocale, PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_STAFF, arrMsg);
                pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
                    parentLocale);
                pushMessage.push();
            }
			
			if(p_requestVO.getRequestMap()!= null)
            { 	
			p_requestVO.getRequestMap().put("USERID2",receiverChannelUserVO.getUserID() );
			p_requestVO.getRequestMap().put("PREBAL2", PretupsBL.getDisplayAmount(PretupsBL.getSystemAmount(args[1]) + channelTransferItemsVO.getRequiredQuantity()));
            p_requestVO.getRequestMap().put("POSTBAL2", args[1]);
			p_requestVO.getRequestMap().put("PREBAL", PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()));
            p_requestVO.getRequestMap().put("POSTBAL", PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()+channelTransferItemsVO.getRequiredQuantity()));
			  p_requestVO.getRequestMap().put("AMOUNT", PretupsBL.getDisplayAmount(channelTransferItemsVO.getRequiredQuantity()));
            }  
			
            if (BTSLUtil.isStringIn(p_requestVO.getRequestGatewayCode(), _allowedSendMessGatw)) {
                final String senderMessage = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
                    p_requestVO.getLocale());
                pushMessage.push();
            }
            return;
        
        }
       }
	   }
    }// end of try
    

	/**
     * Method prepareTransferProfileVO
     * This method construct the VO for the Txn
     * 
     * @param p_senderVO
     * @param p_receiverVO
     * @param p_productList
     * @param p_curDate
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */
    private static ChannelTransferVO prepareTransferProfileVO(ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO, ArrayList p_productList, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( " Entered  p_senderVO: ");
        	loggerValue.append(p_senderVO);
        	loggerValue.append(" p_receiverVO:");
        	loggerValue.append(p_receiverVO);
        	loggerValue.append(" p_productList:" );
        	loggerValue.append(p_productList.size());
        	loggerValue.append(" p_curDate:" );
        	loggerValue.append(p_curDate);
            _log.debug("prepareTransferProfileVO",loggerValue);
        }

        final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

        channelTransferVO.setNetworkCode(p_senderVO.getNetworkID());
        channelTransferVO.setNetworkCodeFor(p_senderVO.getNetworkID());
        channelTransferVO.setDomainCode(p_receiverVO.getDomainID());
        channelTransferVO.setGraphicalDomainCode(p_receiverVO.getGeographicalCode());
        channelTransferVO.setReceiverCategoryCode(p_senderVO.getCategoryCode());
        channelTransferVO.setCategoryCode(p_receiverVO.getCategoryCode());
        channelTransferVO.setReceiverGradeCode(p_senderVO.getUserGrade());
        channelTransferVO.setSenderGradeCode(p_receiverVO.getUserGrade());
        channelTransferVO.setFromUserID(p_receiverVO.getUserID());
        // channelTransferVO.setFromUserCode(p_receiverVO.getUserCode());
        channelTransferVO.setToUserID(p_senderVO.getUserID());
        // channelTransferVO.setToUserCode(p_senderVO.getUserCode());
        channelTransferVO.setTransferDate(p_curDate);
        channelTransferVO.setCommProfileSetId(p_receiverVO.getCommissionProfileSetID());
        channelTransferVO.setCommProfileVersion(p_receiverVO.getCommissionProfileSetVersion());
        channelTransferVO.setDualCommissionType(p_receiverVO.getDualCommissionType());
        channelTransferVO.setCreatedOn(p_curDate);
        channelTransferVO.setCreatedBy(p_senderVO.getUserID());
        channelTransferVO.setModifiedOn(p_curDate);
        channelTransferVO.setModifiedBy(p_senderVO.getUserID());
        channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        channelTransferVO.setTransferInitatedBy(p_senderVO.getUserID());
        channelTransferVO.setReceiverTxnProfile(p_senderVO.getTransferProfileID());
        channelTransferVO.setSenderTxnProfile(p_receiverVO.getTransferProfileID());
        // channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_STK);

        // adding the some additional information for sender/reciever
        channelTransferVO.setReceiverGgraphicalDomainCode(p_senderVO.getGeographicalCode());
        channelTransferVO.setReceiverDomainCode(p_senderVO.getDomainID());
   
//        channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
//        channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getMsisdn()));
        
        if(p_receiverVO.isStaffUser()) {
        	channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getOwnerMsisdn()));
        }else	channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
        if(p_senderVO.isStaffUser()) {
        	channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getOwnerMsisdn()));
        }else channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getMsisdn()));

        channelTransferVO.setTransferCategory(p_senderVO.getTransferCategory());

        ChannelTransferItemsVO channelTransferItemsVO = null;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        for (int i = 0, k = p_productList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(i);
            totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
            if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                totMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
            } else {
                totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
            }
            totPayAmt += channelTransferItemsVO.getPayableAmount();
            totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
            totTax1 += channelTransferItemsVO.getTax1Value();
            totTax2 += channelTransferItemsVO.getTax2Value();
            totTax3 += channelTransferItemsVO.getTax3Value();
            commissionQty += channelTransferItemsVO.getCommQuantity();
            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
        }
        channelTransferVO.setRequestedQuantity(totRequestQty);
        channelTransferVO.setTransferMRP(totMRP);
        channelTransferVO.setPayableAmount(totPayAmt);
        channelTransferVO.setNetPayableAmount(totNetPayAmt);
        channelTransferVO.setTotalTax1(totTax1);
        channelTransferVO.setTotalTax2(totTax2);
        channelTransferVO.setTotalTax3(totTax3);
        channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
        channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));

        channelTransferVO.setChannelTransferitemsVOList(p_productList);
        channelTransferVO.setActiveUserId(p_senderVO.getActiveUserID());
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList<ChannelSoSVO> chnlSoSVOList = new ArrayList<> ();
        	chnlSoSVOList.add(new ChannelSoSVO(p_senderVO.getUserID(),p_senderVO.getMsisdn(),p_senderVO.getSosAllowed(),p_senderVO.getSosAllowedAmount(),p_senderVO.getSosThresholdLimit()));
        	chnlSoSVOList.add(new ChannelSoSVO(p_receiverVO.getUserID(),p_receiverVO.getMsisdn(),p_receiverVO.getSosAllowed(),p_receiverVO.getSosAllowedAmount(),p_receiverVO.getSosThresholdLimit()));
        	channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("prepareTransferProfileVO", " Exited  ");
        }
        return channelTransferVO;
    }// end of



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
		/*throw new BTSLBaseException("C2CTransferController", "C2CWithdrawService",
				PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, 0, null);*/
	}
	
    RowErrorMsgList rowErrorMsgListPayment = new RowErrorMsgList();
   
	ArrayList<RowErrorMsgLists> rowErrorMsgLists1Payment= new ArrayList<RowErrorMsgLists>();
	
	rowErrorMsgListPayment.setRowErrorMsgLists(rowErrorMsgLists1Payment);
	
	ArrayList<RowErrorMsgLists> rowErrorMsgLists2Products = new ArrayList<RowErrorMsgLists>();
	RowErrorMsgList rowErrorMsgList2Products = new RowErrorMsgList();
	for(int k=0;k<dataStkTrfMul.getProducts().size();k++)
	{
		int row = k+1;
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
		else{

			String qtStr=dataStkTrfMul.getProducts().get(k).getQty();
			Integer qtNum=null;
			try {
		     qtNum=Integer.parseInt(qtStr); 
			}
			catch (NumberFormatException e) {
				_log.errorTrace("Error occured: ", e);
				
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
