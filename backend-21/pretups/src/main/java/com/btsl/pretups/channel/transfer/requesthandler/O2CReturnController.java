/**
 * * @(#)O2CReturnController.java
 * Copyright(c) 2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Pankaj K Namdev Nov. 9, 2006 Initial Creation
 * 
 * This class parses the request received on the basis of format for
 * the O2C Return.
 * 
 */

package com.btsl.pretups.channel.transfer.requesthandler;

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
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.user.businesslogic.UserLoanVO;

public class O2CReturnController implements ServiceKeywordControllerI {
    // Get the logger object, which is used to write different types of logs.
    private static Log log = LogFactory.getLog(O2CReturnController.class.getName());
    private static OperatorUtilI _operatorUtil = null;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * This method will be used to process the request.
     * This is the only public method in this class. RequestVO will be passed
     * from the controller
     * 
     * @param p_requestVO
     */
    @Override
	public void process(RequestVO p_requestVO) {
        // Make the enter logger
        final String methodName = "process";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_requestVO: " + p_requestVO);
        }

        // Make the connection object
        Connection con = null;MComConnectionI mcomCon = null;
        final Date currentDate = new Date();

        // ///
        // message format
        // [request_type] [qty] [productcode]_ _ _ _[qty] [productcode][pin]
        // FLOW OF EXECUTION :-------->
        // 1. Get the coonection from pool and take the msg array from requestVO
        // 2. Get channel user's details
        // 3. validate the pin if it is in the request
        // 4. validate channel user like user is bareed or not ,user is ctive or
        // not etc. .
        // 5. validate the requested message like checks on ext txn no,ext
        // network code etc. .
        // 6. validate and filter requested product like if it is associated to
        // latest comm. profile or not
        // ,if it is associated to transfer rules or not etc. .
        // 7. loading and calculation of tax on product
        // 8. load the information of network admin which returns information in
        // UserVO
        // 9. prepare the channel transfer VO for the O2C return
        // 10.generate transfer ID for the O2C return
        // 11.set the transfer ID in each ChannelTransferItemsVO of productList
        // 12.update all tables according to transaction
        // 13.insert the channelTransferVO in the database andStart the operator
        // to channel transfer
        // 12.if txn is successful than send true in setSuccessTransaction() as
        // a parameter of requestVO after commit.
        // 14.Prepare message to receiver

        // Start the try block
        try {
            // Get the connection from pool.
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            // getting the msgArray from the request
            final String[] messageArr = p_requestVO.getRequestMessageArray();

            final int msgLen = messageArr.length;

            // getting details of channel user

            // validate the PIN if it is in the request
            if ((PretupsI.YES.equals(userPhoneVO.getPinRequired())) && p_requestVO.isPinValidationRequired()) {
                try {
                    ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[msgLen - 1]);
                }// try
                catch (BTSLBaseException be) {
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        con.commit();
                    }
                    throw be;
                }// catch
            }// if

            // validate the channel user
            ChannelTransferBL.o2cReturnUserValidate(con, p_requestVO, currentDate);

            final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
            // validate requested message contents
            final HashMap productMap = ChannelTransferBL.validateO2CMessageContent(con, p_requestVO, channelTransferVO, false);

            String type = (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
    		String paymentMode = PretupsI.ALL;
            final ArrayList productList = ChannelTransferBL.loadAndValidateProducts(con, p_requestVO, productMap, channelUserVO, false, type, paymentMode);

            channelTransferVO.setChannelTransferitemsVOList(productList);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
            channelTransferVO.setOtfFlag(false);

            // loading and calculation of tax on product
            ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), channelUserVO.getCommissionProfileSetVersion(), channelTransferVO,
                false, null, PretupsI.TRANSFER_TYPE_O2C);

            // load the information of network admin which returns information
            // in UserVO
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final UserVO userVO = channelUserDAO.loadOptUserForO2C(con, channelUserVO.getNetworkID());


            // prepare the channel transfer VO for the O2C return
            prepareChannelTransferVO(p_requestVO, channelTransferVO, currentDate, channelUserVO, productList, userVO);

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

            // generate transfer ID for the O2C return
            ChannelTransferBL.genrateReturnID(channelTransferVO);

            String productType = null;
            // set the transfer ID in each ChannelTransferItemsVO of productList
            for (int i = 0, j = productList.size(); i < j; i++) {
                final ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
                productType = channelTransferItemsVO.getProductType();
            }// for
            channelTransferVO.setProductType(productType);
            channelTransferVO.setControlTransfer(PretupsI.YES);

            // update all tables according to transaction
            transactionApproval(con, channelTransferVO, userVO.getUserID(), currentDate, productList);

            // insert the channelTransferVO in the database
            // Start the operator to channel transfer
            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();
            final int insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
            if (insertCount < 1) {
                con.rollback();
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOT_ALLOWED_NOW);
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
                    	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    }
                    boolean statusAllowed = false;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
                        .getUserType(), p_requestVO.getRequestGatewayType());
                    if (userStatusVO == null) {
                        throw new BTSLBaseException("O2CReturnController",methodName, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
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
                    log.error("process", "Exception while changing user state to active  " + ex.getMessage());
                    log.errorTrace(methodName, ex);
                } finally {
                    if (con != null) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
						if (mcomCon != null) {
							mcomCon.close("O2CReturnController#process");
							mcomCon = null;
						}
						con = null;
                    }

                }
            }
            // end of changes

            // preparing message to sender
            final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
            final String smsKey = PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS;

            final ArrayList txnList = new ArrayList();
            final ArrayList balList = new ArrayList();
            String args[] = null;
            ChannelTransferItemsVO channelTransferItemsVO = null;
            KeyArgumentVO keyArgumentVO = null;

            final int lSize = itemsList.size();
            for (int i = 0; i < lSize; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS_TXNSUBKEY);
                args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO.getRequestedQuantity() };
                keyArgumentVO.setArguments(args);
                txnList.add(keyArgumentVO);
                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS_BALSUBKEY);
                args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL
                    .getDisplayAmount(channelTransferItemsVO.getBalance() - channelTransferItemsVO.getRequiredQuantity()) };
                keyArgumentVO.setArguments(args);
                balList.add(keyArgumentVO);
            }// end of for
            final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()) };
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
            return;

        }// try
        catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            }// try
            catch (Exception e) {
                log.errorTrace(methodName, e);
            } // catch
            log.error(methodName, "BTSLBaseException " + be.getMessage());
            log.errorTrace(methodName, be);
            if (be.getMessageList() != null && !be.getMessageList().isEmpty()) {
                final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
                p_requestVO.setMessageArguments(array);
            } // if
            if (be.getArgs() != null) {
                p_requestVO.setMessageArguments(be.getArgs());
            } // if
            if (be.getMessageKey() != null) {
                p_requestVO.setMessageCode(be.getMessageKey());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            }

            return;

        }// catch
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
            log.error(methodName, "BTSLBaseException " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CReturnController[process]", "", "", "",
                "Exception:" + ex.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            return;

        }// catch
        finally {
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("O2CReturnController#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }// finally
    }

    /**
     * This method is used to prepare the Channel TransferVO.
     * 
     * @param p_requestVO
     * @param channelTransferVO
     * @param p_curDate
     * @param p_channelUserVO
     * @param p_prdList
     * @param p_userVO
     * @throws BTSLBaseException
     */
    private void prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList, UserVO p_userVO) throws BTSLBaseException {
    	 String methodName="prepareChannelTransferVO";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering  : requestVO " + p_requestVO);
        }

        channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        channelTransferVO.setReceiverCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
        channelTransferVO.setCategoryCode(p_channelUserVO.getCategoryCode());
        channelTransferVO.setReceiverGradeCode("");
        channelTransferVO.setSenderGradeCode(p_channelUserVO.getUserGrade());
        channelTransferVO.setFromUserID(p_channelUserVO.getUserID());
        channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
        channelTransferVO.setToUserCode(p_userVO.getUserCode());
        channelTransferVO.setTransferDate(p_curDate);
        channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        channelTransferVO.setCreatedOn(p_curDate);
        channelTransferVO.setCreatedBy(p_channelUserVO.getUserID());
        channelTransferVO.setModifiedOn(p_curDate);
        channelTransferVO.setModifiedBy(p_channelUserVO.getUserID());
        channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        channelTransferVO.setReceiverTxnProfile("");
        channelTransferVO.setSenderTxnProfile(p_channelUserVO.getTransferProfileID());
        channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_SMS);

        // adding the some additional information for sender/reciever
        channelTransferVO.setReceiverGgraphicalDomainCode("");
        channelTransferVO.setReceiverDomainCode("");
        channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        channelTransferVO.setTransferCategory(PretupsI.TRANSFER_TYPE_SALE);
        channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        if (!p_channelUserVO.isStaffUser()) {
            channelTransferVO.setActiveUserId(p_channelUserVO.getUserID());
        } else {
            channelTransferVO.setActiveUserId(p_channelUserVO.getActiveUserID());
        }

        // calculate total requested quantity(totRequestQty),total
        // MRP(totMRP),total pay amount(totPayAmt),
        // Total net pay amount(totNetPayAmt) ,toatal
        // tax1(totTax1),tax2(totTax2)and tax3(totTax3) for all
        // products.
        ChannelTransferItemsVO channelTransferItemsVO = null;
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
        }// for
        channelTransferVO.setRequestedQuantity(totRequestQty);
        channelTransferVO.setTransferMRP(totMRP);
        channelTransferVO.setPayableAmount(totPayAmt);
        channelTransferVO.setNetPayableAmount(totNetPayAmt);
        channelTransferVO.setTotalTax1(totTax1);
        channelTransferVO.setTotalTax2(totTax2);
        channelTransferVO.setTotalTax3(totTax3);
        channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
        channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
        channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue() ) {
   			
   				channelTransferVO.setUserLoanVOList(p_channelUserVO.getUserLoanVOList());
   		
        } 
        
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList<ChannelSoSVO> chnlSoSVOList = new ArrayList<> ();
        	chnlSoSVOList.add(new ChannelSoSVO(p_channelUserVO.getUserID(),p_channelUserVO.getMsisdn(),p_channelUserVO.getSosAllowed(),p_channelUserVO.getSosAllowedAmount(),p_channelUserVO.getSosThresholdLimit()));
        	channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting ");
        }
    }// exit of method prepareChannelTransferVO

    /**
     * This method is used to updates table according to request
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_userID
     * @param p_date
     * @param p_product_list
     * @throws BTSLBaseException
     */
    public void transactionApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userID, Date p_date, ArrayList p_product_list) throws BTSLBaseException {
    	 String methodName="transactionApproval";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering  : p_channelTransferVO " + p_channelTransferVO);
        }

        try {
            int updateCount = -1;
            updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userID, p_date, false);
            if (updateCount < 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_NOT_CREDIT_NETWORK_STOCK);
            }// if

            updateCount = -1;
            updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userID, p_date);
            if (updateCount < 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_IN_NETWORK_STOCT_TRANSACTION);
            }// if

            final ArrayList userBalanceList = null;
            UserBalancesVO userBalanceVO = null;
            ChannelTransferItemsVO chnlTrfItemsVO = null;
            final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
            int product_lists=p_product_list.size();
            for (int x = 0; x <product_lists ; x++) {
                chnlTrfItemsVO = (ChannelTransferItemsVO) p_product_list.get(x);
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
            }// for

            updateCount = -1;
            updateCount = userBalancesDAO.updateUserDailyBalances(p_con, p_date, userBalanceVO);
            if (updateCount < 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_UPDATION_USERDAILYBALANCE);
            }// if

            updateCount = -1;
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            
            if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue())
            {
            	updateCount = channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, false, null);
            }
            else
            {
            	updateCount = channelUserDAO.debitUserBalances(p_con, p_channelTransferVO, false, null);
            }
            if (updateCount < 1) {
                throw new BTSLBaseException(this, methodName ,PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
            }// if

            updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);

            if (updateCount < 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_UPDATION_OPT_CHANNEL_USER_IN_COUNT);
            }// if
        }// try
        finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting ");
            }
        }// finally
    }// exit of processTransaction
}// End of Class O2CReturnController

