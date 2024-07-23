package com.btsl.pretups.channel.transfer.requesthandler;

/**
 * * @(#)O2CInitiateTransferController.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Amit singh Nov. 9, 2006 Initial Creation
 * 
 * This class parses the received request on the basis of format for
 * initiate O2C transfer.
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;

public class O2CInitiateTransferController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(O2CInitiateTransferController.class.getName());
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
     * 8. add channel transfer in database
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
    	StringBuilder loggerValue= new StringBuilder(); 
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
         	loggerValue.append("Entered p_requestVO: ");
         	loggerValue.append(p_requestVO);
            _log.debug("process",  loggerValue);
        }

        Connection con = null;MComConnectionI mcomCon = null;

        int insertCount = 0;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        Date currentDate = null;
        int msgLen = 0;

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            UserPhoneVO userPhoneVO = null;
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = (UserPhoneVO) channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = (UserPhoneVO) channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            // getting the msgArray from the request
            final String[] messageArr = p_requestVO.getRequestMessageArray();

            msgLen = messageArr.length;

            // validate the PIN if it is in the request
            if ((PretupsI.YES.equals((((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired())) && p_requestVO.isPinValidationRequired()) {
                try {
                    ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[msgLen - 1]);
                } catch (BTSLBaseException be) {
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        con.commit();
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
                    for (int i = 0; i < status.length; i++) {
                        if (status[i].equals(channelUserVO.getStatus())) {
                            receiverAllowed = true;
                        }
                    }
                }
            }

            if (receiverStatusVO == null) {
                throw new BTSLBaseException("O2CInitiateTransferController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
            } else if (!receiverAllowed) {
                /*
                 * p_requestVO.setMessageCode(PretupsErrorCodesI.
                 * CHNL_ERROR_RECEIVER_NOTALLOWED);
                 * p_requestVO.setMessageArguments(args);
                 */
                throw new BTSLBaseException("O2CInitiateTransferController", "process", PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
            }
            //

            // to validates the message passed in the request and returns the
            // HashMap which
            // contains the product short code as key and the corresponding
            // quantity as value
            final HashMap productMap = ChannelTransferBL.validateO2CMessageContent(con, p_requestVO, channelTransferVO, true);

            String type = (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
            String pmtMode = (p_requestVO.getRequestMap() !=null)?((p_requestVO.getRequestMap().get("PAYMENTTYPE") != null)? (String)p_requestVO.getRequestMap().get("PAYMENTTYPE"):PretupsI.ALL):PretupsI.ALL;
    		String paymentMode = (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())?pmtMode:PretupsI.ALL;
            final ArrayList prdList = ChannelTransferBL.loadAndValidateProducts(con, p_requestVO, productMap, channelUserVO, true, type, paymentMode);
            // make a new channel TransferVO to transfer into the method during
            // tax calculataion
            channelTransferVO.setChannelTransferitemsVOList(prdList);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            channelTransferVO.setOtfFlag(true);


            // calculate the taxes for the diff. products
            // based on the transfer category
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
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                final UserDAO userDAO = new UserDAO();
                phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getRequestMSISDN());
                if (phoneVO != null && !(phoneVO.getPrimaryNumber()).equalsIgnoreCase("Y")) {
                    channelUserVO.setPrimaryMsisdn(channelTransferVO.getToUserCode());
                    channelTransferVO.setToUserCode(p_requestVO.getRequestMSISDN());
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
                        primaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, channelUserVO.getPrimaryMsisdn());
                    }
                }

            }
            // generate transfer ID for the O2C transfer
            ChannelTransferBL.genrateTransferID(channelTransferVO);

            // set the transfer ID in each ChannelTransferItemsVO of productList
            for (int i = 0, j = prdList.size(); i < j; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) prdList.get(i);
                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
            }

            channelTransferVO.setControlTransfer(PretupsI.YES);

            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

            // insert the channelTransferVO in the database
            insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
            if (insertCount < 0) {
                con.rollback();
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            con.commit();
            p_requestVO.setSuccessTxn(true);
            //Added for OneLine Logging for Channel
            OneLineTXNLog.log(channelTransferVO, null);
			
			ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);

            // sending msg to receiver
            final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
            // String smsKey=PretupsErrorCodesI.O2C_INITIATE_TRANSFER_RECEIVER;
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
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_INITIATE_TRANSFER_SUCCESS_TXNSUBKEY);
                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), channelTrfItemsVO.getRequestedQuantity() };
                keyArgumentVO.setArguments(args);
                txnList.add(keyArgumentVO);

                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_INITIATE_TRANSFER_SUCCESS_BALSUBKEY);
                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTrfItemsVO.getBalance() + channelTrfItemsVO
                    .getRequiredQuantity()) };
                keyArgumentVO.setArguments(args);
                balList.add(keyArgumentVO);
            }// end of for
            if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE))) {
                final String[] msgArray = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                    .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()) };
                p_requestVO.setMessageArguments(msgArray);
                smsKey = PretupsErrorCodesI.O2C_INITIATE_TRANSFER_RECEIVER;
            } else if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_TRANSFER))) {
                final String[] msgArray = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                    .getTransferID() };
                p_requestVO.setMessageArguments(msgArray);
                smsKey = PretupsErrorCodesI.FOC_INITIATE_TRANSFER_EXTGW_RECEIVER;
            }
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

            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
                try {
                    if (p_requestVO.isSuccessTxn()) {
                        final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                        final LoyaltyVO loyaltyVO = new LoyaltyVO();
                        PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
                        final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                        final ArrayList arr = new ArrayList();
                        if(mcomCon == null){
                        mcomCon = new MComConnection();con=mcomCon.getConnection();}
                        loyaltyVO.setServiceType(PretupsI.O2C_MODULE);
                        loyaltyVO.setModuleType(PretupsI.O2C_MODULE);

                        if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                            loyaltyVO.setTransferamt(channelTransferVO.getSenderDrQty());
                        } else {
                            loyaltyVO.setTransferamt(channelTransferVO.getTransferMRP());
                        }

                        loyaltyVO.setCategory(channelTransferVO.getCategoryCode());
                        loyaltyVO.setUserid(channelTransferVO.getActiveUserId());
                        loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
                        loyaltyVO.setSenderMsisdn(channelTransferVO.getUserMsisdn());
                        loyaltyVO.setTxnId(channelTransferVO.getTransferID());
                        loyaltyVO.setCreatedOn(channelTransferVO.getCreatedOn());
                        loyaltyVO.setProductCode(channelTransferVO.getProductCode());
                        arr.add(loyaltyVO.getUserid());
                        promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
                        loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                        if (loyaltyVO.getSetId() == null) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
                        }
                        _loyaltyBL.distributeLoyaltyPoints(PretupsI.O2C_MODULE, channelTransferVO.getTransferID(), loyaltyVO);

                    }
                } catch (Exception ex) {
                	loggerValue.setLength(0);
                	loggerValue.append("Exception durign LMS Module ");
                	loggerValue.append(ex.getMessage());
                    _log.error("process",  loggerValue);
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
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
                        throw new BTSLBaseException("O2CInitiateTransferController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
                    } else {
                        final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
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
                	loggerValue.append("Exception while changing user state to active  ");
                	loggerValue.append( ex.getMessage());
                    _log.error("process", loggerValue);
                    _log.errorTrace(METHOD_NAME, ex);
                } finally {
                    if (con != null) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _log.errorTrace("process", e);
                        }
						if (mcomCon != null) {
							mcomCon.close("O2CInitiateTransferController#process");
							mcomCon = null;
						}
                        con = null;
                    }

                }
            }
            // end of changes

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(be.getMessage());
            _log.error("process",  loggerValue );
            // be.printStackTrace();
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
            _log.errorTrace(METHOD_NAME, be);
            return;
        } catch (Exception ex) {
            p_requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException " );
        	loggerValue.append(ex.getMessage());
            _log.error("process", loggerValue );
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CInitiateTransferController[process]", "", "", "",
            		loggerValue.toString());
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            return;
        } finally {
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("O2CInitiateTransferController#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited.. ");
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
         	loggerValue.append("Entering  : requestVO ");
         	loggerValue.append(p_requestVO);
         	loggerValue.append("p_channelTransferVO:" );
         	loggerValue.append(p_channelTransferVO);
         	loggerValue.append("p_curDate:");
         	loggerValue.append(p_curDate);
         	loggerValue.append( "p_channelUserVO:");
         	loggerValue.append(p_channelUserVO);
         	loggerValue.append("p_prdList:" );
         	loggerValue.append(p_prdList);
         	loggerValue.append("p_userVO:" );
         	loggerValue.append(p_userVO);
            _log.debug("prepareChannelTransferVO",loggerValue );
        }

        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);

        // who initaite the order.
        p_channelTransferVO.setReceiverGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_userVO.getUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_userVO.getUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_SMS);

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

        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting : ");
        }
    }
}
