package com.btsl.pretups.channel.transfer.requesthandler;

/**
 * * @(#)O2CWithdrawController.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Siddhartha Srivastava Nov.9, 2006 Initial Creation
 * 
 * This class parses the request received on the basis of format for the O2C
 * Withdraw.
 * After the request is parsed, all the transaction are performed for the O2C
 * Withdraw operation
 * which are relevant
 * 
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OneLineTXNLog;
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
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.web.user.businesslogic.UserWebDAO;
import com.btsl.user.businesslogic.UserLoanVO;

public class O2CWithdrawController implements ServiceKeywordControllerI {
    private static Log log = LogFactory.getLog(O2CWithdrawController.class.getName());
    private static OperatorUtilI _operatorUtil = null;
    private ArrayList filteredPrdList = null; // global since being directly in
    // other function
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace("static", e);
            StringBuilder loggerValue= new StringBuilder(); 
    		        loggerValue.setLength(0);
                	loggerValue.append("Exception while loading the class at the call:");
                	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[initialize]", "", "", "",
            		loggerValue.toString());
        }
    }

    /**
     * This method is the entry point of the class. This method performs all the
     * work related to O2CWithdraw
     * by calling the other private methods of the class.The various checks
     * involved are
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
        final String methodName = "process";
        StringBuilder loggerValue= new StringBuilder(); 
        loggerValue.setLength(0);
    	loggerValue.append("Entered p_requestVO: ");
    	loggerValue.append(p_requestVO);
        if (log.isDebugEnabled()) {
            log.debug("process",  loggerValue);
        }

        UserVO userVO = null;

		Connection con = null;
		MComConnectionI mcomCon = null;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        int insertCount = 0;
        final Date currentDate = new Date(); // this same date object should be
        // used
        // throughout

        try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

            // this retreives the request in the form of string array
            final String[] messageArr = p_requestVO.getRequestMessageArray();

            final int msgLen = messageArr.length;

            // validates the pin if in the requestVO, pin required is set to
            // true
            if ((PretupsI.YES.equals((((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired())) && p_requestVO.isPinValidationRequired()) {
                try {
                    ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[msgLen - 1]);
                } catch (BTSLBaseException be) {
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        con.commit();
                    }
                    throw be;
                }// end of try-catch
            }// end of if

            final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

            // this method call validates the user
            ChannelTransferBL.o2cWithdrawUserValidate(con, p_requestVO, currentDate);

            // this call to the method validates the message passed in the
            // request and returns the HashMap which
            // contains the product code as key and the corresponding quantity
            // as value
            final HashMap productMap = ChannelTransferBL.validateO2CMessageContent(con, p_requestVO, channelTransferVO, false);

            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            String type = (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
    		String paymentMode = PretupsI.ALL;
            filteredPrdList = ChannelTransferBL.loadAndValidateProducts(con, p_requestVO, productMap, channelUserVO, false, type, paymentMode);
            channelTransferVO.setChannelTransferitemsVOList(filteredPrdList);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
            channelTransferVO.setOtfFlag(false);

            ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), channelUserVO.getCommissionProfileSetVersion(), channelTransferVO,
                false, null, PretupsI.TRANSFER_TYPE_O2C);

            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            // this method returns the first network admin for the network id
            // passed
            userVO = channelUserDAO.loadOptUserForO2C(con, channelUserVO.getNetworkID());

            // prepares the ChannelTransferVO by populating its fields from the
            // passed ChannelUserVO and filteredList of products
            prepareChannelTransferVO(p_requestVO, channelTransferVO, currentDate, channelUserVO, filteredPrdList, userVO);
            // generates a unique id for this withdraw

            UserPhoneVO phoneVO = null;
            UserPhoneVO primaryPhoneVO_R = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                final UserDAO userDAO = new UserDAO();
                phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getRequestMSISDN());
                if (phoneVO != null && !(phoneVO.getPrimaryNumber()).equalsIgnoreCase("Y")) {
                    channelUserVO.setPrimaryMsisdn(channelUserVO.getUserCode());
                    channelTransferVO.setFromUserCode(p_requestVO.getRequestMSISDN());
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
                        primaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, channelUserVO.getPrimaryMsisdn());
                    }
                }

            }

            ChannelTransferBL.genrateWithdrawID(channelTransferVO);

            // setting the transfer id for each channelTransferItemsVO
            for (int i = 0, j = filteredPrdList.size(); i < j; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) filteredPrdList.get(i);
                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
            }// end of for
             // the transfer is controlled by default, so set to 'Y'
            channelTransferVO.setControlTransfer(PretupsI.YES);

            // performs all the transaction for the withdraw opertaion
            transactionApproval(con, channelTransferVO, userVO.getUserID(), currentDate);

            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

            // updates the channel_transfer and channel_transfer_items table
            insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
            if (insertCount < 1) {
                con.rollback();
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOT_ALLOWED_NOW);
            }
            if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue())
	        { 
				UserEventRemarksVO userRemarskVO=null;
				ArrayList<UserEventRemarksVO> o2cRemarks=null;
				if(channelTransferVO!=null)
	    		   {
	    			 insertCount=0;
	    			o2cRemarks=new ArrayList<UserEventRemarksVO>();
	              	userRemarskVO=new UserEventRemarksVO();
	              	userRemarskVO.setCreatedBy(channelTransferVO.getCreatedBy());
	              	userRemarskVO.setCreatedOn(new Date());
	              	userRemarskVO.setEventType(PretupsI.TRANSFER_TYPE_O2C);
	              	userRemarskVO.setRemarks(channelTransferVO.getChannelRemarks());
	              	userRemarskVO.setMsisdn(channelTransferVO.getFromUserCode());
	              	userRemarskVO.setUserID(channelTransferVO.getFromUserID());
	              	userRemarskVO.setUserType("SENDER");
	              	userRemarskVO.setModule(PretupsI.O2C_MODULE);
	              	o2cRemarks.add(userRemarskVO);
	              	insertCount=new UserWebDAO().insertEventRemark(con, o2cRemarks);
	              	if(insertCount<=0)
	              	{
	              		con.rollback();
	 	                 log.error(methodName,"Error: while inserting into userEventRemarks Table");
	 	                 throw new BTSLBaseException(this,"save","error.general.processing");
	              	} 	
	              	
	    		  }
	        }
            
            con.commit();
            p_requestVO.setSuccessTxn(true);
            //Added for OneLine Logging for Channel
            OneLineTXNLog.log(channelTransferVO, null);
			//
            // Meditel changes by Ashutosh
            if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                try {
                    if (mcomCon == null) {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
                    }
                    boolean statusAllowed = false;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
                        .getUserType(), p_requestVO.getRequestGatewayType());
                    if (userStatusVO == null) {
                        throw new BTSLBaseException("O2CWithdrawController",methodName, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
                    } else {
                        final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
                        final String status[] = userStatusAllowed.split(",");
                        for (int i = 0; i < status.length; i++) {
                            if (status[i].equals(channelUserVO.getStatus())) {
                                statusAllowed = true;
                            }
                        }
                        
                        PretupsBL.chkAllwdStatusToBecomeActive(con, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG), channelUserVO.getUserID(), channelUserVO.getStatus());
                    }

                } catch (Exception ex) {
                	loggerValue.setLength(0);
                	loggerValue.append("Exception while changing user state to active  " );
                	loggerValue.append( ex.getMessage());
                    log.error(methodName, loggerValue);
                    log.errorTrace(methodName, ex);
                } finally {
                    if (con != null) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
						if (mcomCon != null) {
							mcomCon.close("O2CWithdrawController#process");
							mcomCon = null;
						}
                        con = null;
                    }

                }
            }
            // end of changes
            //
            ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);

            // Preparing Sender message to sender

            // preparing message to sender
            final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
            final String smsKey = PretupsErrorCodesI.O2C_WITHDRAW_SUCCESS;

            final ArrayList txnList = new ArrayList();
            final ArrayList balList = new ArrayList();
            String args[] = null;
            ChannelTransferItemsVO channelTrfItemsVO = null;
            KeyArgumentVO keyArgumentVO = null;

            final int lSize = itemsList.size();
            for (int i = 0; i < lSize; i++) {
                channelTrfItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_WITHDRAW_SUCCESS_TXNSUBKEY);
                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), channelTrfItemsVO.getRequestedQuantity() };
                keyArgumentVO.setArguments(args);
                txnList.add(keyArgumentVO);

                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_WITHDRAW_SUCCESS_BALSUBKEY);
                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTrfItemsVO.getBalance() - channelTrfItemsVO
                    .getRequiredQuantity()) };
                keyArgumentVO.setArguments(args);
                balList.add(keyArgumentVO);
            }// end of for
            final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()) ,"operator"};
            p_requestVO.setMessageArguments(array);
            p_requestVO.setMessageCode(smsKey);
            p_requestVO.setTransactionID(channelTransferVO.getTransferID());
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
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
            

        }// end of try
        catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException " );
        	loggerValue.append(be.getMessage());
            log.error(methodName, loggerValue );
            log.errorTrace(methodName, be);
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

        }// end of BTSLBaseException
        catch (Exception ex) {
            p_requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                log.errorTrace(methodName, ee);
            }
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(ex.getMessage());
            log.error(methodName,  loggerValue.toString() );
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CWithdrawController[process]", "", "", "",
            		loggerValue.toString() );
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            return;

        }// end of Exception
        finally {
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("O2CWithdrawController#process");
				mcomCon = null;
			}
            filteredPrdList = null;
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }// end of finally
    }

    /**
     * This method prepares the ChannelTransferVO from the arguments
     * channelTransferVO, requestVO,
     * channelUserVO, filteredPrdList and userVO
     * 
     * @param p_requestVO
     * @param p_channelTransferVO
     * @param p_curDate
     * @param p_channelUserVO
     * @param p_prdList
     * @param p_userVO
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */

    private ChannelTransferVO prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList, UserVO p_userVO) throws BTSLBaseException {
        String methodName="prepareChannelTransferVO";
    	
    	if (log.isDebugEnabled()) {
            log.debug(methodName,
                "Entering  : requestVO " + p_requestVO + "p_channelTransferVO" + p_channelTransferVO + "p_channelUserVO" + p_channelUserVO + "p_userVO" + p_userVO);
        }

        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        p_channelTransferVO.setCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setReceiverGradeCode("");
        p_channelTransferVO.setSenderGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setFromUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserCode(p_userVO.getUserCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_userVO.getUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_userVO.getUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile("");
        p_channelTransferVO.setSenderTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_SMS);
        p_channelTransferVO.setActiveUserId(p_userVO.getUserID());
        // adding the some additional information for sender/reciever

        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        p_channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_TYPE_SALE);

        ChannelTransferItemsVO channelTransferItemsVO = null;
        String productType = null;
        long totRequestQty = 0;
        long totMRP = 0;
        long totPayAmt = 0;
        long totNetPayAmt = 0;
        long totTax1 = 0;
        long totTax2 = 0;
        long totTax3 = 0;
        long commissionQty = 0;
        long senderDebitQty = 0;
        long receiverCreditQty = 0;
        for (int i = 0, k = p_prdList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_prdList.get(i);
            totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelUserVO.getDualCommissionType())) {
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
            productType = channelTransferItemsVO.getProductType();
        }// end of for
        p_channelTransferVO.setRequestedQuantity(totRequestQty);
        p_channelTransferVO.setTransferMRP(totMRP);
        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        p_channelTransferVO.setProductType(channelTransferItemsVO.getProductType());
        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        p_channelTransferVO.setProductType(productType);
        p_channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        p_channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        p_channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
        
        
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue()  ) {
    		
				p_channelTransferVO.setUserLoanVOList(p_channelUserVO.getUserLoanVOList());
		
        } 
        

        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList<ChannelSoSVO> chnlSoSVOList = new ArrayList<> ();
        	chnlSoSVOList.add(new ChannelSoSVO(p_channelUserVO.getUserID(),p_channelUserVO.getMsisdn(),p_channelUserVO.getSosAllowed(),p_channelUserVO.getSosAllowedAmount(),p_channelUserVO.getSosThresholdLimit()));
        	p_channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }
        
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting .....  :p_channelTransferVO" + p_channelTransferVO);
        }

        return p_channelTransferVO;

    }

    /**
     * This method performs all the transaction for the O2CWithdraw operation.
     * This method prepares the Network Stock, credit the network stock, updates
     * the channel User balance, updates the
     * transfer in values(it will not update the tranfer in count as it is
     * withdraw).
     * Also it updates the daily balance for the Channel User
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_userID
     * @param p_date
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */

    private void transactionApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userID, Date p_date) throws BTSLBaseException {
    	 String methodName="transactionApproval";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering  : p_channelTransferVO " + p_channelTransferVO);
        }

        int updateCount = -1;

        updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userID, p_date, false);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawController", methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }// end of if
        updateCount = -1;
        // this method updates the network stock and also updates the network
        // transaction details
        updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userID, p_date);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawController", methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }// end of if

        UserBalancesVO userBalanceVO = null;
        ChannelTransferItemsVO chnlTrfItemsVO = null;
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        int filteredListSize = filteredPrdList.size();
        for (int x = 0; x < filteredListSize; x++) {
            chnlTrfItemsVO = (ChannelTransferItemsVO) filteredPrdList.get(x);
            userBalanceVO = new UserBalancesVO();

            userBalanceVO.setUserID(p_channelTransferVO.getFromUserID());
            userBalanceVO.setProductCode(chnlTrfItemsVO.getProductCode());
            userBalanceVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
            userBalanceVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
            userBalanceVO.setLastTransferID(p_channelTransferVO.getTransferID());
            userBalanceVO.setLastTransferType(p_channelTransferVO.getTransferType());
            userBalanceVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
            userBalanceVO.setPreviousBalance(userBalanceVO.getBalance());
            userBalanceVO.setQuantityToBeUpdated(chnlTrfItemsVO.getRequiredQuantity());
            // Added on 13/02/2008
            userBalanceVO.setUserMSISDN(p_channelTransferVO.getFromUserCode());
        }// end of for loop

        updateCount = -1;
        // this method updates the user balances performing debit/credit on his
        // balance ar applicable
        updateCount = userBalancesDAO.updateUserDailyBalances(p_con, p_date, userBalanceVO);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawController", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }// end of if

        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue())
        {
        	channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, false, null);
        }
        else
        {
        	channelUserDAO.debitUserBalances(p_con, p_channelTransferVO, false, null);
        }
        updateCount = -1;
        // this call updates the counts/values for daily, weekly and monthly IN
        updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
        if (updateCount < 1) {
            throw new BTSLBaseException("O2CWithdrawController", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }// end of if

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting...... : p_channelTransferVO " + p_channelTransferVO);
        }

    }
}
