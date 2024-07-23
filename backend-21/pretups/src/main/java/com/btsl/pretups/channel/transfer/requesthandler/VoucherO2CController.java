package com.btsl.pretups.channel.transfer.requesthandler;

/**
 * * @(#)VoucherO2CController.java
 * Copyright(c) 2019, Comviva.
 * All Rights Reserved
 */
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
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
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.btsl.voms.voucher.businesslogic.VoucherChangeStatus;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;

public class VoucherO2CController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(VoucherO2CController.class.getName());
    private static final String className="VoucherO2CController";
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
        final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
        final VomsVoucherDAO vomsVoucherDAO= new VomsVoucherDAO();
        VomsBatchVO vomsBatchVO =new VomsBatchVO();
        VomsVoucherVO vomsVoucherVO=null;
        p_requestVO.getRequestMap().put("VOUCHERMRP",""); 
        p_requestVO.getRequestMap().put("VOUCHERQUANTITY",""); 
        p_requestVO.getRequestMap().put("TOTALAMOUNT",""); 
        p_requestVO.getRequestMap().put("NETPAYABLEAMNT",""); 

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
            final HashMap requestMap = p_requestVO.getRequestMap();
            String fromSerial=(String)requestMap.get("FROM_SERIALNO");
            String toSerial=(String)requestMap.get("TO_SERIALNO");
            String productCode=(String)requestMap.get("PRODUCTCODE");
            
            vomsVoucherVO=vomsVoucherDAO.getVoucherDetails(con,fromSerial);
            if(vomsVoucherVO==null) 
            {
            	throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.VOMS_O2C_VOUCHER_NOT_FOUND);
            }
            else {
            	vomsVoucherVO.setToSerialNo(toSerial);
            }
            if (!vomsVoucherVO.getUserLocationCode().equals(p_requestVO.getExternalNetworkCode())) 
            {
            	throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.VOMS_O2C_VOUCHERS_FROM_DIFFERENT_NETWORK);
            }
            
            long requestedVoucherCount=(Long.parseLong(toSerial)) - (Long.parseLong(fromSerial)) + 1 ;
            long dbVoucherCount=vomsVoucherDAO.validateAllVoucherDetails(con,vomsVoucherVO,vomsVoucherVO);
            
			if(requestedVoucherCount!=dbVoucherCount){
				throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.VOMS_O2C_SOME_VOUCHERS_DIFFERENT_STATUS);
			}
			
			final double denomination =(vomsVoucherVO.getMRP()/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
			final Long quantity = (Long.parseLong(toSerial)) - (Long.parseLong(fromSerial)) + 1 ;
			final double requestedMrp = denomination * quantity;
			
			vomsBatchVO.setDenomination(""+denomination);
			vomsBatchVO.setQuantity(String.valueOf(quantity));
			vomsBatchVO.setCreatedBy(channelUserVO.getUserID());
			vomsBatchVO.set_NetworkCode(channelUserVO.getNetworkID());
			vomsBatchVO.setFromSerialNo(fromSerial);
			vomsBatchVO.setToSerialNo(toSerial);
			vomsBatchVO.setCreatedDate(currentDate);
			vomsBatchVO.setModifiedDate(currentDate);
			vomsBatchVO.setModifiedOn(currentDate);
			vomsBatchVO.setCreatedOn(currentDate);
			vomsBatchVO.setToUserID(channelTransferVO.getToUserID()); 
			vomsBatchVO.setVoucherType(vomsVoucherVO.getVoucherType());
			vomsBatchVO.setProductID(vomsVoucherVO.getProductID());
			vomsBatchVO.setToUserID(channelUserVO.getUserID());
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_EN_ON_TRACKING))).booleanValue()) {
					vomsBatchVO.setBatchType(VOMSI.BATCH_ENABLED);
				} 
			else {
					vomsBatchVO.setBatchType(VOMSI.VOMS_PRE_ACTIVE_STATUS);
				}
			String batch_no = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
			vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batch_no));
			
			
            if (channelTransferWebDAO.validateVoucherSerialNo(con, vomsBatchVO.getFromSerialNo(),"null",PretupsI.TRANSFER_TYPE_O2C)) {
				throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL);
			}
			if (channelTransferWebDAO.validateVoucherSerialNo(con, vomsBatchVO.getToSerialNo(), "null",PretupsI.TRANSFER_TYPE_O2C)) {
				throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.VOMS_O2C_TO_SERIAL_PENDING_APPROVAL);
			}
			if (!channelTransferWebDAO.validateSerialNo(con, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getProductID(),vomsBatchVO.getVoucherType())) {
				throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.VOMS_O2C_FROM_SERIAL_INVALID);
			}
			if (!channelTransferWebDAO.validateSerialNo(con, vomsBatchVO.getToSerialNo(), vomsBatchVO.getProductID(),vomsBatchVO.getVoucherType())) {
				throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.VOMS_O2C_TO_SERIAL_INVALID);
			}
			final ArrayList<VomsBatchVO> usedBatches = channelTransferWebDAO.validateBatch(con, vomsBatchVO);
			if (usedBatches != null && !usedBatches.isEmpty()) {
				throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.VOMS_O2C_INVALID_BATCH);
			}
            
            final HashMap productMap = validateVoucherO2CMessageContent(con, p_requestVO, channelTransferVO, true,productCode,""+requestedMrp);

            String type = (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
            String pmtMode = (p_requestVO.getRequestMap() !=null)?((p_requestVO.getRequestMap().get("PAYMENTTYPE") != null)? (String)p_requestVO.getRequestMap().get("PAYMENTTYPE"):PretupsI.ALL):PretupsI.ALL;
    		String paymentMode = (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue() && PretupsI.GATEWAY_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType()))?pmtMode:PretupsI.ALL;
            prdList = ChannelTransferBL.loadAndValidateProducts(con, p_requestVO, productMap, channelUserVO, true, type, paymentMode);

            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
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
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
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
            prepareChannelTransferVO(p_requestVO, channelTransferVO, currentDate, channelUserVO, prdList, userVO,vomsBatchVO,vomsVoucherVO);
            UserPhoneVO phoneVO = null;
            UserPhoneVO primaryPhoneVO_R = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                final UserDAO userDAO = new UserDAO();
                phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getRequestMSISDN());
                if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
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

            // the transfer is controlled by default, so set to 'Y'
            channelTransferVO.setControlTransfer(PretupsI.YES);

            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

            insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
            if (insertCount < 0) {
            	OracleUtil.rollbackConnection(con, VoucherO2CController.class.getName(), METHOD_NAME);
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }
            vomsBatchVO.setExtTxnNo(channelTransferVO.getTransferID());
            final int insert_count = channelTransferWebDAO.insertVomsBatches(con, vomsBatchVO);
            if (insert_count < 0) {
            	OracleUtil.rollbackConnection(con, VoucherO2CController.class.getName(), METHOD_NAME);
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }
            ArrayList batchVO_list = new ArrayList();
            batchVO_list.add(vomsBatchVO);
            final VoucherChangeStatus voucherChangeStatus = new VoucherChangeStatus(batchVO_list);
			voucherChangeStatus.start();
			
            OracleUtil.commit(con);
            p_requestVO.setSuccessTxn(true);
            
            p_requestVO.getRequestMap().put("VOUCHERMRP",""+denomination); 
	        p_requestVO.getRequestMap().put("VOUCHERQUANTITY",""+quantity); 
	        p_requestVO.getRequestMap().put("TOTALAMOUNT",channelTransferVO.getRequestedQuantityAsString()); 
	        p_requestVO.getRequestMap().put("NETPAYABLEAMNT",channelTransferVO.getPayableAmountAsString()); 
            
            String smsKey=PretupsErrorCodesI.VOMS_O2C_SUCCESSFUL;
            
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
            // end of changes

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            OracleUtil.rollbackConnection(con, VoucherO2CController.class.getName(), METHOD_NAME);
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
                p_requestVO.setMessageCode(PretupsErrorCodesI.VOMS_O2C_ERROR);
            }
            return;
        } catch (Exception ex) {
            p_requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            OracleUtil.rollbackConnection(con, VoucherO2CController.class.getName(), METHOD_NAME);
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException " );
        	loggerValue.append(ex.getMessage());
            _log.error(METHOD_NAME, loggerValue );
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherO2CController[process]", "", "", "",
            		loggerValue.toString());
            p_requestVO.setMessageCode(PretupsErrorCodesI.VOMS_O2C_ERROR);
            return;
        } finally {
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("VoucherO2CController#process");
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
    private void prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList, UserVO p_userVO,VomsBatchVO vomsBatchVO,VomsVoucherVO vomsVoucherVO ) throws BTSLBaseException {
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
            channelTransferItemsVO.setSenderDebitQty(0);
            channelTransferItemsVO.setReceiverCreditQty(0);
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
        final ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList = new ArrayList<ChannelVoucherItemsVO>();
        ChannelVoucherItemsVO channelVoucherItemsVO = new ChannelVoucherItemsVO();
		channelVoucherItemsVO.setTransferId(p_channelTransferVO.getTransferID());
		channelVoucherItemsVO.setTransferDate(p_curDate);
		//channelVoucherItemsVO.setTransferMRP((long)Double.parseDouble(vomsBatchVO.getDenomination()));
		channelVoucherItemsVO.setTransferMRP(BTSLUtil.parseDoubleToLong(Double.parseDouble(vomsBatchVO.getDenomination())));
		
		channelVoucherItemsVO.setRequiredQuantity(BTSLUtil.parseDoubleToLong(Double.parseDouble(vomsBatchVO.getQuantity())));
		channelVoucherItemsVO.setVoucherType(vomsVoucherVO.getVoucherType());
		channelVoucherItemsVO.setFromSerialNum(vomsBatchVO.getFromSerialNo());
		channelVoucherItemsVO.setToSerialNum(vomsBatchVO.getToSerialNo());
		channelVoucherItemsVO.setProductId(vomsBatchVO.getProductID());
		channelVoucherItemsVO.setProductName(vomsBatchVO.getProductName());
		channelVoucherItemsVO.setNetworkCode(p_channelUserVO.getNetworkID());
		channelVoucherItemsVO.setSegment(vomsVoucherVO.getVoucherSegment());
		channelVoucherItemsVO.setType(PretupsI.CHANNEL_TYPE_O2C);
		channelVoucherItemsVO.setFromUser(PretupsI.CATEGORY_TYPE_OPT);
		channelVoucherItemsVO.setToUser(p_channelUserVO.getUserID());
		channelVoucherItemsVOList.add(channelVoucherItemsVO);
		
		p_channelTransferVO.setChannelVoucherItemsVoList(channelVoucherItemsVOList);
        p_channelTransferVO.setRequestedQuantity(totRequestQty);
        p_channelTransferVO.setTransferMRP(totMRP);
        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER);
        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        p_channelTransferVO.setPayInstrumentAmt(totNetPayAmt);
        p_channelTransferVO.setProductType(productType);
        p_channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        p_channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        p_channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
        //Added by lalit to fix bug DEF528 for GP 6.6.1
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue() && p_requestVO.getRequestMap() != null) {
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
        
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList chnlSoSVOList = new ArrayList();
        	chnlSoSVOList.add(new ChannelSoSVO(p_channelUserVO.getUserID(),p_channelUserVO.getMsisdn(),p_channelUserVO.getSosAllowed(),p_channelUserVO.getSosAllowedAmount(),p_channelUserVO.getSosThresholdLimit()));
        	p_channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }
        
        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting : ");
        }
    }

    
    private HashMap validateVoucherO2CMessageContent(Connection con, RequestVO requestVO, ChannelTransferVO channelTransferVO, boolean isTransfer,String productCode,String reqQuantity) throws BTSLBaseException {
		final String methodName = "validateO2CMessageContent";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered requestVO : ");
			loggerValue.append(requestVO);
			loggerValue.append(", channelTransferVO : ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", isTransfer : ");
			loggerValue.append(isTransfer);
			_log.debug(methodName,  loggerValue );
		}
		HashMap productMap =new HashMap();
		productMap.put(productCode, reqQuantity);
		
		final HashMap requestMap = requestVO.getRequestMap();
		// take the requestMap from the requset
		// & validate the data from this hashMap
		if (requestMap != null) {
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("entered for requestMap not null :requestMap = ");
				loggerValue.append(requestMap.size());
				_log.debug(methodName,  loggerValue);
			}

			final String extTxnNumber = (String) requestMap.get("EXTTXNNUMBER");

			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("extTxnNumber from requestMap = " );
				loggerValue.append(extTxnNumber);
				_log.debug(methodName, loggerValue );
			}

			// checks on external transaction id that
			if (!BTSLUtil.isNullString(extTxnNumber)) {
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_NUMERIC))).booleanValue()) {
					long externalTxnIDLong = 0;
					if (BTSLUtil.isNumeric(extTxnNumber)) {
						externalTxnIDLong = Long.parseLong(extTxnNumber);
						if (externalTxnIDLong < 0) {
							throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_POSITIVE);
						}

					} else {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_NUMERIC);
					}

				}

				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue()) {
					final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
					final boolean isExternalTxnExists = channelTransferDAO.isExtTxnExists(con, extTxnNumber, null);
					if (isExternalTxnExists) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_UNIQUE);
					}

				}
				channelTransferVO.setExternalTxnNum(extTxnNumber);
			} else {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_EXT_TXN_NO_BLANK);
			}

			final String extTxnDate = (String) requestMap.get("EXTTXNDATE");

			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("extTxnDate from requestMap = ");
				loggerValue.append(extTxnDate);
				_log.debug(methodName,  loggerValue);
			}
			// check on ext. txn. date
			if (BTSLUtil.isNullString(extTxnDate)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_EXT_DATE_BLANK);
			}

			try {
				final String extDateFormat = PretupsI.DATE_FORMAT;
				if (extDateFormat.length() != extTxnDate.length()) {
					throw new ParseException(extDateFormat, 0);
				}
				final SimpleDateFormat sdf = new SimpleDateFormat(extDateFormat);
				sdf.setLenient(false); // this is required else it will convert
				channelTransferVO.setExternalTxnDate(sdf.parse(extTxnDate));

			} catch (java.text.ParseException e1) {
				_log.errorTrace(methodName, e1);
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER);
			}

			String trfCategory = null;
			// this check is for, if the request is for O2C
			// transfer otherwise O2C withdrawal (i.e.- isTransfer=true is O2C
			// transfer otherwise O2C Withdrawal)
			if(isTransfer)
			{
				// check on transfer category
				trfCategory = (String) requestMap.get("TRFCATEGORY");

				if (_log.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("trfCategory from requestMap = ");
					loggerValue.append(trfCategory);
					_log.debug(methodName,  loggerValue );
				}

				if (BTSLUtil.isNullString(trfCategory)) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_CATEGORY_NOT_ALLOWED);
				}

				// set the trf. cat.-> 'SALE' or 'FOC' in the ChannelTransferVO
				if (trfCategory.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE)) {
					channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
				} else if (trfCategory.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_FOC)) {
					channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
				} else {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_CATEGORY_NOT_ALLOWED);
				}

				// check on reference no.
				final String refNumber = (String) requestMap.get("REFNUMBER");

				if (!BTSLUtil.isNullString(refNumber) && (refNumber.length() > 10)) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_REFERENCE_NO_LENGTH_NOT_VALID);
				} else {
					channelTransferVO.setReferenceNum(refNumber);
				}

				if (trfCategory.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE)) {
					ArrayList paymentTypeList = new ArrayList();
					final String paymentType = (String) requestMap.get("PAYMENTTYPE");
					final String paymentInstNo = (String) requestMap.get("PAYMENTINSTNUMBER");
					final String paymentDateString = (String) requestMap.get("PAYMENTDATE");

					if (_log.isDebugEnabled()) {
						loggerValue.setLength(0);
						loggerValue.append("From requestMap : paymentType = ");
						loggerValue.append(paymentType);
						loggerValue.append(", paymentInstNo = ");
						loggerValue.append(paymentInstNo);
						loggerValue.append(", paymentDateString = ");
						loggerValue.append(paymentDateString);
						_log.debug(methodName,loggerValue);
					}

					Date paymentDate = null;
					if (BTSLUtil.isNullString(paymentType)) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PAYMENTTYPE_BLANK);
					}

					paymentTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
					ListValueVO listValueVO = null;
					boolean pmtTypeExist = false;

					// check that the payment type from request should be
					// present in the payment type list from the lookup cache
					for (int i = 0, k = paymentTypeList.size(); i < k; i++) {
						listValueVO = (ListValueVO) paymentTypeList.get(i);
						if (paymentType.equalsIgnoreCase(listValueVO.getValue())) {
							pmtTypeExist = true;
							break;
						}
					}
					if (!pmtTypeExist) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PAYMENTTYPE_NOTFOUND);
					}

					// if payment type is CASH then no need of payment of
					// instrument no.
					if (!paymentType.equalsIgnoreCase(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH) && BTSLUtil.isNullString(paymentInstNo)) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_NUM_INVALID);
					}
					// check the lenght of payment instrument no.
					else if (!paymentType.equalsIgnoreCase(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH) && paymentInstNo.length() > 15) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_NUM_INVALID);
					}

					// checks on payment date
					if (BTSLUtil.isNullString(paymentDateString)) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_DATE_BLANK);
					}

					try {
						final String paymentDateFormat = PretupsI.DATE_FORMAT;
						if (paymentDateFormat.length() != paymentDateString.length()) {
							throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_DATE_NOT_PROPER);
						}
						paymentDate = BTSLUtil.getDateFromDateString(paymentDateString, PretupsI.DATE_FORMAT);
					} catch (java.text.ParseException e1) {
						_log.errorTrace(methodName, e1);
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_DATE_NOT_PROPER);
					}

					channelTransferVO.setPayInstrumentType(paymentType);
					channelTransferVO.setPayInstrumentNum(paymentInstNo);
					channelTransferVO.setPayInstrumentDate(paymentDate);

				}
			}
			// checks on payment date
			final String remarks = (String) requestMap.get("REMARKS");
			if (!BTSLUtil.isNullString(remarks) && remarks.length() > 100) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_CHANNEL_REAMRK_NOT_PROPER);
			}
			channelTransferVO.setChannelRemarks(remarks);
			final Boolean isTagReq=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED))).booleanValue();
			if(isTagReq)
			{
				final String info1 = (String) requestMap.get("INFO1");
				final String info2 = (String) requestMap.get("INFO2");
				channelTransferVO.setInfo1(info1);
				channelTransferVO.setInfo2(info2);
			}
		}
        // if request map is null (i.e. SMS request) then set the transfer
        // category to 'SALE'
		  else {
	            channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
	        }

		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting : productMap.size():: ");
			loggerValue.append(productMap.size());
			_log.debug(methodName,  loggerValue );
		}

		return productMap;
	}
}
