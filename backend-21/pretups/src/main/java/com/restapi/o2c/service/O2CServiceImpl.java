package com.restapi.o2c.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
//mport org.apache.struts.action.ActionMapping;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.EMailSender;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.btsl.voms.voucher.businesslogic.VoucherChangeStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
//import com.web.pretups.channel.transfer.web.ChannelTransferApprovalForm;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;

@Service("O2CServiceI")
public class O2CServiceImpl implements O2CServiceI {

	private final Log _log = LogFactory.getLog(this.getClass().getName());
	final Date curDate = new Date();
	Locale locale= null;
	String gatewayType = "";
	String gatewayCode = "";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void processWithdrawRequest(ChannelUserVO loggedUserVO, O2CWithdrawlRequestVO o2CWithdrawlRequestVO
			,BaseResponseMultiple<JsonNode> apiResponse, HttpServletResponse response1)
			throws BTSLBaseException {
		
		
		final String methodName = "processWithdrawRequest";
		UserWebDAO userwebDAO = new UserWebDAO();
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<BaseResponse> successList = new ArrayList<BaseResponse>();
		apiResponse.setSuccessList(successList);
		ErrorMap errorMap = new ErrorMap();
		apiResponse.setErrorMap(errorMap);
		gatewayType = o2CWithdrawlRequestVO.getReqGatewayType();
		gatewayCode =  o2CWithdrawlRequestVO.getReqGatewayCode();
		
		
		
		ChannelUserVO fromUserVO = null;
		OperatorUtilI operatorUtili = null;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		try {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("transferReturned", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"O2CServiceImpl[processWithdrawRequest]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
		try {
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			userwebDAO = new UserWebDAO();

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			int rowNum =0;

			for (O2CWithdrawData requestVO : o2CWithdrawlRequestVO.getO2CInitiateReqData()) {
				
				BaseResponse baseResponse = new BaseResponse();
				
				rowNum++;
				
				final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

				UserEventRemarksVO remarkVO = null;
				ArrayList<UserEventRemarksVO> withDrawReturnRemarkList = null;

				fromUserVO =(ChannelUserVO) ((new ChannelUserDAO()).
						loadChannelUserByUserID(con, requestVO.getFromUserId()));
				
				fromUserVO = (ChannelUserVO) ((new ChannelUserDAO()).
						loadChannelUserDetails(con, fromUserVO.getMsisdn()));
				
				RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
				
				rowErrorMsgLists.setRowName("Data:" + rowNum);
				rowErrorMsgLists.setRowValue(String.valueOf(rowNum));
				
				this.loadProductList(con, channelTransferVO, loggedUserVO, fromUserVO, requestVO, rowErrorMsgLists);

				if(rowErrorMsgLists.getMasterErrorList().size() > 0)
				{
					if(errorMap.getRowErrorMsgLists() == null)
					{
						List<RowErrorMsgLists> rowErrorMsgListsMain = new ArrayList<RowErrorMsgLists>();
						errorMap.setRowErrorMsgLists(rowErrorMsgListsMain);
					}
					errorMap.getRowErrorMsgLists().add(rowErrorMsgLists);
					continue;
				}
				
				this.orderReturnedProcessStart(con, channelTransferVO, loggedUserVO.getUserID(), curDate,
						"myRestO2CReturn");

				OneLineTXNLog.log(channelTransferVO, null);
				final int updateCount = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
				if (updateCount > 0) {

					int insertCount = 0;
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))
							.booleanValue()) {

						withDrawReturnRemarkList = new ArrayList();
						remarkVO = new UserEventRemarksVO();
						remarkVO.setCreatedBy(loggedUserVO.getCreatedBy());
						remarkVO.setCreatedOn(new Date());
						remarkVO.setEventType(PretupsI.CHUSER_WITHDRAW);
						remarkVO.setMsisdn(loggedUserVO.getMsisdn());
						remarkVO.setRemarks(requestVO.getRemarks());
						remarkVO.setUserID(loggedUserVO.getCategoryCode());
						remarkVO.setUserType(loggedUserVO.getUserType());
						remarkVO.setModule(PretupsI.C2S_MODULE);
						withDrawReturnRemarkList.add(remarkVO);
						insertCount = userwebDAO.insertEventRemark(con, withDrawReturnRemarkList);
						if (insertCount <= 0) {
							con.rollback();
							_log.error("saveDeleteSuspend", "Error: while inserting into userEventRemarks Table");
							throw new BTSLBaseException(this, "save", "error.general.processing");
						}
					}
					// Addition By Babu Kunwar ends
					mcomCon.partialCommit();
					// user life cycle
					if (channelTransferVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
						if (!PretupsI.USER_STATUS_ACTIVE.equals(loggedUserVO.getStatus())) {
							// int
							// updatecount=operatorUtili.changeUserStatusToActive(
							// con,channelTransferVO.getFromUserID(),channelUserVO.getStatus());
							int updatecount = 0;
							final String str[] = SystemPreferences.TXN_SENDER_USER_STATUS_CHANG.split(","); // "CH:Y,EX:Y".split(",");
							String newStatus[] = null;
							boolean changeStatusRequired = false;
							for (int i = 0; i < str.length; i++) {
								newStatus = str[i].split(":");
								if (newStatus[0].equals(loggedUserVO.getStatus())) {
									changeStatusRequired = true;
									updatecount = operatorUtili.changeUserStatusToActive(con,
											channelTransferVO.getFromUserID(), loggedUserVO.getStatus(), newStatus[1]);
									break;
								}
							}
							if (changeStatusRequired) {
								if (updatecount > 0) {
									con.commit();
									mcomCon.finalCommit();
									
									
										
						            OneLineTXNLog.log(channelTransferVO, null);
									
									
								} else {
									mcomCon.finalRollback();
									throw new BTSLBaseException(this, "transferReturned",
											"channeltransfer.approval.msg.unsuccess", "confirmback");
								}
							}
						}
					}
					
					
					baseResponse.setStatus(200);
					baseResponse.setTransactionId(channelTransferVO.getTransferID());
					successList.add(baseResponse);
					
					ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
					Object[] smsListArr = null;

					UserPhoneVO phoneVO = null;

					if (!loggedUserVO.isStaffUser()) {
						phoneVO = fromUserVO.getUserPhoneVO();
					} else {
						phoneVO = fromUserVO.getStaffUserDetails().getUserPhoneVO();
					}
					String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
					String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
					// Object[] smsListArr = null;
					if (phoneVO != null) {
						country = phoneVO.getCountry();
						language = phoneVO.getPhoneLanguage();
						final Locale locale = new Locale(language, country);
						channelTransferVO.setToUserID(channelTransferVO.getFromUserID());
						smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO,
								PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_TXNSUBKEY,
								PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_BALSUBKEY);
						final String[] array = { channelTransferVO.getTransferID(),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
								PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
						final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_SMS1,
								array);
						final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages,
								channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
						pushMessage.push();
					} else {
						final String arr[] = { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() };
						final BTSLMessages messages = new BTSLMessages("userreturn.withdraw.msg.success.nophoneinfo",
								arr, "withdrawsuccess");
					}

					// user for withdarw user name and withdraw with user code
					final BTSLMessages messages = new BTSLMessages("userreturn.withdraw.msg.success",
							new String[] { channelTransferVO.getTransferID() }, "withdrawsuccess");

				} else {
					con.rollback();
					throw new BTSLBaseException(this, "transferReturned", "userreturn.withdraw.msg.unsuccess",
							"confirmback");
				}
			}
			
			if(apiResponse.getErrorMap()!=null && apiResponse.getErrorMap().getRowErrorMsgLists()!=null)
			{
				apiResponse.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				
			}
			else
			{
				apiResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
				apiResponse.setMessage("All records processed successfully");
				response1.setStatus(HttpStatus.SC_OK);
			}
			
		}
			 catch (Exception e) {
			_log.error("transferWithdrawn", "Exception:e=" + e);
			_log.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2cServiceImpl#processWithdrawRequest");
				mcomCon = null;
			}
		
		}

	}

	

	public void orderReturnedProcessStart(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId,
			Date p_date, String p_forwardPath) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("orderReturnedProcessStart", "Entered p_channelTransferVO  " + p_channelTransferVO + " p_userId "
					+ p_userId + " p_date " + p_date + " p_forwardPath: " + p_forwardPath);
		}
		final boolean credit = false;
		// prepare networkStockList credit the network stock
		ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date,
				credit);
		ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userId, p_date);

		// update user daily balances
		final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
		userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));

		// channel debit the user balances
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
			channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true, p_forwardPath);
		} else {
			channelUserDAO.debitUserBalances(p_con, p_channelTransferVO, true, p_forwardPath);
		}
		ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, p_forwardPath, p_date);
		if (_log.isDebugEnabled()) {
			_log.debug("orderReturnedProcessStart", "Exiting");
		}
	}

	private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
		if (_log.isDebugEnabled()) {
			_log.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
		}
		final UserBalancesVO userBalancesVO = new UserBalancesVO();
		userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
		userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
		userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
		userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
		// Added to log user MSISDN on 13/02/2008
		userBalancesVO.setUserMSISDN(p_channelTransferVO.getFromUserCode());
		if (_log.isDebugEnabled()) {
			_log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
		}
		return userBalancesVO;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadProductList(Connection con, ChannelTransferVO channelTransferVO, ChannelUserVO loggedUserVO,
			ChannelUserVO fromUserVO,O2CWithdrawData requestVO, RowErrorMsgLists rowErrorMsgLists) throws Exception
	{
		String methodName = "loadProductList";
		String arguments = fromUserVO.getUserName();
		RequestVO p_requestVO = new RequestVO();
		String args[] = null;
		boolean isContinue = false;
		UserPhoneVO userPhoneVO = new UserDAO().loadUserPhoneVO(con, loggedUserVO.getUserID());
		loggedUserVO.setUserPhoneVO(userPhoneVO);
	
		List<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
		rowErrorMsgLists.setMasterErrorList(masterErrorListMain);

				
		O2CReturnRestController o2cReturnRestController = new O2CReturnRestController();
		
		final ChannelTransferRuleVO channelTransferRuleVO = new ChannelTransferRuleDAO().loadTransferRule(con, fromUserVO.getNetworkID(), fromUserVO.getDomainID(),
				PretupsI.CATEGORY_TYPE_OPT, fromUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		
		if 	(PretupsI.YES.equals(loggedUserVO.getCategoryVO().getSmsInterfaceAllowed())) {
			try {
				ChannelUserBL.validatePIN(con, loggedUserVO,  requestVO.getPin());
			} catch (BTSLBaseException be) {
				if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
						|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
					OracleUtil.commit(con);
				}
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
						
			}
		}
		
		if(masterErrorListMain.size() > 0)
			return;
		if (channelTransferRuleVO == null) {
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		} else if (PretupsI.NO.equals(channelTransferRuleVO.getWithdrawAllowed())) {
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_WITHDRAW_NOT_ALLOWED, new String[] {loggedUserVO.getUserName()});
			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USER_WITHDRAW_NOT_ALLOWED);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		} else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH, new String[] {loggedUserVO.getUserName()});
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		if (fromUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		
		if (!PretupsI.YES.equals(fromUserVO.getCommissionProfileStatus())) {
			MasterErrorList masterErrorList = new MasterErrorList();
            args = new String[] { arguments, loggedUserVO.getCommissionProfileLang2Msg() };
            final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                args = new String[] { arguments, loggedUserVO.getCommissionProfileLang1Msg() };
            }
			String msg = RestAPIStringParser.getMessage(locale, "commissionprofile.notactive.msg", args);
			masterErrorList.setErrorCode("commissionprofile.notactive.msg");
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
        } 
		if (!PretupsI.YES.equals(fromUserVO.getTransferProfileStatus())) {
        	MasterErrorList masterErrorList = new MasterErrorList();
            args = new String[] { arguments };
			String msg = RestAPIStringParser.getMessage(locale, "transferprofile.notactive.msg", args);
			masterErrorList.setErrorCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
        if (BTSLUtil.isEmpty(requestVO.getRemarks())) {
        	MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REMARKS_REQUIRED, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.REMARKS_REQUIRED);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
			
			ArrayList walletList = LookupsCache.loadLookupDropDown(PretupsI.MULTIPLE_WALLET_TYPE, true);

			if (BTSLUtil.isEmpty(requestVO.getWalletType())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.WALLETTYPE_REQUIRED, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.WALLETTYPE_REQUIRED);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
			
			/*if 	(PretupsI.YES.equals(fromUserVO.getCategoryVO().getSmsInterfaceAllowed())) {
				try {
					ChannelUserBL.validatePIN(con, loggedUserVO, requestVO.getPin());
				} catch (BTSLBaseException be) {
					MasterErrorList masterErrorList = new MasterErrorList();
					String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
					masterErrorList.setErrorCode(be.getMessageKey());
					masterErrorList.setErrorMsg(msg);
					masterErrorListMain.add(masterErrorList);
					isContinue = true;
				}
			}*/
		/*	if (!BTSLUtil.isEmpty(requestVO.getWalletType())) {
				
				
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.WALLETTYPE_REQUIRED, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.WALLETTYPE_REQUIRED);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}*/
		}
        List<Products> products = requestVO.getProducts();
		if (products == null || products.size() == 0) {
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_PRODUCT_DETAILS, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_PRODUCT_DETAILS);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
			isContinue = true;
		}
		
		
		
		if (PretupsI.YES.equals(fromUserVO.getCategoryVO().getProductTypeAllowed())) {
			fromUserVO.setAssociatedProductTypeList(new ProductTypeDAO().loadUserProductsListForLogin(con, fromUserVO.getUserID()));
        } else {
        	fromUserVO.setAssociatedProductTypeList(LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));
        }
		final ArrayList prodTypListtemp = fromUserVO.getAssociatedProductTypeList();
		HashMap<String, ArrayList> productsAssociated = new HashMap<String, ArrayList>();
		for(int i=0;i<prodTypListtemp.size();i++)
		{
			ListValueVO lvVO = (ListValueVO)(prodTypListtemp.get(i));
			final ArrayList prod = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, fromUserVO.getUserID(), fromUserVO.getNetworkID(), fromUserVO
	                .getCommissionProfileSetID(), curDate, channelTransferRuleVO.getTransferRuleID(), null, false, arguments, locale, lvVO.getValue(), PretupsI.TRANSFER_TYPE_O2C);
			//ArrayList prod = ChannelTransferBL.loadO2CXfrProductList(con, lvVO.getValue(), loggedUserVO.getNetworkID(), loggedUserVO.getCommissionProfileSetID(), curDate, "");
			
			for(Products reqProduct : products)
			{
				Boolean isFound = false;
				
				for (int item = 0; item < prod.size(); item++) {

					ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) (prod.get(item));
					
					if(channelTransferItemsVO.getProductShortCode() == Long.valueOf(reqProduct.getProductcode()))
					{
						isFound = true;
						
						if(BTSLUtil.getDisplayAmount(channelTransferItemsVO.getBalance()) < Long.valueOf(reqProduct.getQty()))
						{
							MasterErrorList masterErrorList = new MasterErrorList();
							final String argsNew[] = { channelTransferItemsVO.getProductCode() };
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_WITHDRAW_QUANTITY, argsNew);
							masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_WITHDRAW_QUANTITY);
							masterErrorList.setErrorMsg(msg);
							masterErrorListMain.add(masterErrorList);
							isContinue = true;
						
						}
						
					}
					
					ArrayList value = new ArrayList();
					value.add(channelTransferItemsVO);
					productsAssociated.put(String.valueOf(channelTransferItemsVO.getProductShortCode()), value);
				}
			}
			
			if(masterErrorListMain.size() > 0)
				return;
			
		
		}
		channelTransferVO.setChannelRemarks(requestVO.getRemarks());
		channelTransferVO.setWalletType(requestVO.getWalletType());
		
		if(o2cReturnRestController.validateProductDetails(products, productsAssociated, channelTransferVO, rowErrorMsgLists))
			isContinue = true;
				
		if(!isContinue)
		{
			try
			{
/*				channelTransferVO.setFirstApproverLimit(channelTransferRuleVO.getFirstApprovalLimit());
				channelTransferVO.setSecondApprovalLimit(channelTransferRuleVO.getSecondApprovalLimit());*/
				channelTransferVO.setOtfFlag(true);
				channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
				channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
				
				ChannelTransferBL.loadAndCalculateTaxOnProducts(con, fromUserVO.getCommissionProfileSetID(), fromUserVO.getCommissionProfileSetVersion(),
                    channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_O2C);

				
				
				
				this.prepareChannelTransferVO(p_requestVO, channelTransferVO, curDate, loggedUserVO, fromUserVO, channelTransferVO.getChannelTransferitemsVOList());
				
			}
			catch(BTSLBaseException be)
			{
				_log.error(methodName, "BTSLBaseException " + be.getMessage());
				_log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
						null);
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorCode(be.getMessageKey());
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
				isContinue = true;
			}
		}
		else
		{
			rowErrorMsgLists.setMasterErrorList(masterErrorListMain);
		}
	
		
	}
	
	public void prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO p_channelTransferVO, Date p_curDate,ChannelUserVO loginUserVO, ChannelUserVO p_channelUserVO, ArrayList p_prdList) throws BTSLBaseException {
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
            _log.debug("prepareChannelTransferVO",loggerValue );
        }
    	
    	p_channelTransferVO.setCreatedBy(loginUserVO.getActiveUserID());
    	p_channelTransferVO.setModifiedBy(loginUserVO.getActiveUserID());
    	p_channelTransferVO.setTransferInitatedBy(loginUserVO.getUserID());
    	p_channelTransferVO.setActiveUserId(loginUserVO.getActiveUserID());
    
    	
    	p_channelTransferVO.setControlTransfer(PretupsI.YES);
    	
    	
		/*if (loginUserVO.getSessionInfoVO().getMessageGatewayVO() != null)
		{
			p_channelTransferVO
					.setRequestGatewayCode(loginUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayCode());
			p_channelTransferVO
					.setRequestGatewayType(loginUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayType());
		}*/
		
    	p_channelTransferVO.setRequestGatewayCode("REST");
    	p_channelTransferVO.setRequestGatewayType("REST");
    	
    	
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()) 
		{
			ArrayList<ChannelSoSVO> channelSoSVOList = new ArrayList<>();
			ChannelSoSVO chnlSOsVO = new ChannelSoSVO();
			chnlSOsVO.setSosAllowed(loginUserVO.getSosAllowed());
			chnlSOsVO.setSosAllowedAmount(loginUserVO.getSosAllowedAmount());
			chnlSOsVO.setSosThresholdLimit(loginUserVO.getSosThresholdLimit());
			channelSoSVOList.add(chnlSOsVO);

			if (BTSLUtil.isNullOrEmptyList(channelSoSVOList)) {

				ChannelSoSVO chnlSOSVO = channelSoSVOList.get(0);
				List<ChannelSoSVO> chnlSoSVOList = new ArrayList<ChannelSoSVO>();
				chnlSoSVOList.add(new ChannelSoSVO(loginUserVO.getUserID(), loginUserVO.getMsisdn(),
						chnlSOSVO.getSosAllowed(), chnlSOSVO.getSosAllowedAmount(), chnlSOSVO.getSosThresholdLimit()));

				p_channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
			}
		}


		ChannelTransferBL.genrateWithdrawID(p_channelTransferVO);

    	p_channelTransferVO.setSenderGradeCode(p_channelUserVO.getUserGrade());
    	p_channelTransferVO.setToUserName(p_channelUserVO.getUserName());
        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        // who initaite the order.
        p_channelTransferVO.setFromUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setToUserCode(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setSenderTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_SMS);
        p_channelTransferVO.setReceiverTxnProfile(null);
        // adding the some additional information of sender/reciever
       
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelTransferVO.getGraphicalDomainCode());
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
            	totMRP = channelTransferItemsVO.getRequiredQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
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
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
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
	
	
	
	/**
	 * method for aprroving or rejecting an o2c voucher transaction
	 */
	
	@SuppressWarnings({ "unchecked", "rawtypes", "null" })
	@Override
	public void processVoucherApprvRequest(ChannelUserVO loginUserVO, O2CVoucherApprovalRequestVO o2CVoucherApprovalRequestVO,
			BaseResponseMultiple<JsonNode> apiResponse, HttpServletResponse response1)
			throws BTSLBaseException {
		final String methodName = "processVoucherApprvRequest";
		UserWebDAO userwebDAO = new UserWebDAO();
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<BaseResponse> successList = new ArrayList<BaseResponse>();
		apiResponse.setSuccessList(successList);
		ErrorMap errorMap = new ErrorMap();
		apiResponse.setErrorMap(errorMap);
		gatewayType = o2CVoucherApprovalRequestVO.getReqGatewayType();
		gatewayCode =  o2CVoucherApprovalRequestVO.getReqGatewayCode();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		
		
		
		ChannelUserVO fromUserVO = null;
		OperatorUtilI operatorUtili = null;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		try {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("transferReturned", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"O2CServiceImpl[processVoucherApprvRequest]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
		try {
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			userwebDAO = new UserWebDAO();

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			int rowNum =0;

			for (O2CVoucherApprvData requestVO : o2CVoucherApprovalRequestVO.getO2CInitiateReqData())
			{
				BaseResponse baseResponse = new BaseResponse();
				//ChannelUserVO receiverChannelUserVO = new ChannelUserVO();
				ArrayList<VomsBatchVO> vomsBatchList = new ArrayList<VomsBatchVO>();
				//receiverChannelUserVO = channelUserDAO.loadChannelUserByUserID(con,
						//requestVO.getToUserId());
				
				
				rowNum++;
				
				 ChannelTransferVO channelTransferVO = new ChannelTransferVO();
				 
				 
				 //setting row no for multiple req
				RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
				
				rowErrorMsgLists.setRowName("Data:" + rowNum);
				rowErrorMsgLists.setRowValue(String.valueOf(rowNum));
				
				
								

			

				//method for basic validations of request
				Boolean flag = o2CValidate(con, requestVO, loginUserVO, rowErrorMsgLists, vomsBatchList);

				//if no basic violation we proceed for approval or rejection
				//else go to next iteration of the mult req
				if(!flag){
					
					ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
					

	            	  VomsBatchVO vomsOrderVO = null;
					//final int length = Integer.parseInt(Constants.getProperty("VOMS_ORDER_SLAB_LENGTH"));
					ArrayList slabslist = new ArrayList();
				
					ArrayList vomsCategoryList = null;
					VomsCategoryWebDAO vomsCategorywebDAO = null;
					ChannelVoucherItemsVO voucherItemVO = null;
					VomsCategoryVO vomsCategoryVO = null;
					//List<PaymentDetailsO2C> paymentDetails= (List<PaymentDetailsO2C>)requestVO.getPaymentDetails();
					
					HashMap<String,Object> reqData = new HashMap<String,Object>();
					reqData.put("remarks", requestVO.getRemarks());
					reqData.put("refNumber", requestVO.getExternalTxnNum());
					reqData.put("paymentDetails", requestVO.getPaymentDetails());


					List<PaymentDetailsO2C> paymentDetails = (List<PaymentDetailsO2C>)reqData.get("paymentDetails");
					
					
					PaymentDetailsO2C paymentDetailsReq=paymentDetails.get(0);
					
					String mrp = null;
					ListValueVO lv = null;
					final VomsProductDAO vomsProductDAO = new VomsProductDAO();
					String activemrpstr = null;
					ArrayList voucherTypeList = new ArrayList<VomsCategoryVO>();
					vomsCategorywebDAO = new VomsCategoryWebDAO();
					String apprvlLevel="";
					int apprvLevelInt=0;;
					//UserVO userVO = this.getUserFormSession(request);
					
					
					
					
					
					String message = null;
					final ArrayList batchVO_list = new ArrayList();
					VomsBatchVO vomsBatchVO = null;
					
				/*	String[] fromSerialNo = null;
					String[] toSerialNo = null;*/
					
					
					Date dt = new Date();
					final ChannelTransferDAO channelTransferDAOI = new ChannelTransferDAO();
					ChannelTransferVO p_channelTransferVO = new ChannelTransferVO();
					List<VoucherDetailsApprv> voucherDetails=requestVO.getVoucherDetails();
				
					
				
					p_channelTransferVO.setTransferID(requestVO.getTransactionId());
					p_channelTransferVO.setNetworkCode(loginUserVO.getNetworkID());
					p_channelTransferVO.setNetworkCodeFor(loginUserVO.getNetworkID());
					p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER);
					channelTransferDAOI.loadChannelTransfersVO(con, p_channelTransferVO);
			
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
				    Date paymentDate = sdf.parse(paymentDetailsReq.getPaymentdate());
				   
					Date transferdate1=BTSLUtil.getDateFromDateString(requestVO.getTransferDate());
				

					
	                ArrayList channleVoucherItemList = channelTransferDAO.loadChannelVoucherItemsList(con, requestVO.getTransactionId(),
	                		transferdate1 );
	                ArrayList channelVoucherItemListTemp=channleVoucherItemList;
					//status=y implies transaction for approve else req for reject
					if (BTSLUtil.isNullOrEmptyList(channleVoucherItemList) ) {
					
						
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_DETAILS_FOR_TXNID, new String[] {requestVO.getTransactionId()});
						masterErrorList.setErrorCode(PretupsErrorCodesI.NO_DETAILS_FOR_TXNID);
						masterErrorList.setErrorMsg(msg);
						masterErrorListMain.add(masterErrorList);
					}
					
				

					
					//p_channelTransferVO.setChannelVoucherItemsVoList(channleVoucherItemList);
					
					
					
					
					if(requestVO.getApprovalLevel().equalsIgnoreCase(PretupsI.APPRVL_LEVEL_1)) 
					{
						apprvlLevel=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
						apprvLevelInt=1;
					}
					else if(requestVO.getApprovalLevel().equalsIgnoreCase(PretupsI.APPRVL_LEVEL_2))
					{
						
						apprvlLevel=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
						apprvLevelInt=2;
						
					}
					else if(requestVO.getApprovalLevel().equalsIgnoreCase(PretupsI.APPRVL_LEVEL_3))
					{
						apprvlLevel=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
						apprvLevelInt=3;
					}else {
						
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_O2C_APPROVAL_LEVEL, new String[] {requestVO.getApprovalLevel()});
						masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_O2C_APPROVAL_LEVEL);
						masterErrorList.setErrorMsg(msg);
						masterErrorListMain.add(masterErrorList);
					}
					
					
					
				
					


					  String status="";
					  
					 if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(apprvlLevel)) {
						 status=PretupsI.NEW;;
					 }
					 else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(apprvlLevel ))
					 {
						 status=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
					 }
					 else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(apprvlLevel) )
					 {
						 status=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
					 }
					 channelTransferVO.setTransferID(requestVO.getTransactionId());
					 
					 
					 
					 //for setting the channeltranserVO with basic detais
					 channelTransferDAOI.loadChannelTransferDetail(con, channelTransferVO, status);
					 
					 if(BTSLUtil.isNullObject(channelTransferVO)) {
						 
						 
						
						 MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_DETAILS_FOR_TXNID_STATUS, new String[] {requestVO.getTransactionId(),status});
							masterErrorList.setErrorCode(PretupsErrorCodesI.NO_DETAILS_FOR_TXNID_STATUS);
							masterErrorList.setErrorMsg(msg);
							masterErrorListMain.add(masterErrorList);
						 
					 }
					
										
				
					
					

					
					
	                int length  = channleVoucherItemList.size();
	                channelTransferVO.setChannelVoucherItemsVoList(channleVoucherItemList);
	                channelTransferVO.setChannelTransferitemsVOList(channelTransferDAO.loadChannelTransferItems(con, requestVO.getTransactionId()));
	                channelTransferVO.setNetworkCode(loginUserVO.getNetworkID());
					channelTransferVO.setNetworkCodeFor(loginUserVO.getNetworkID());
	                
		            if(length>0){
		            	for(int i=0;i<length;i++) 
		            	{
		            		vomsCategoryVO = new VomsCategoryVO();
			            	vomsCategoryVO.setVoucherType(((ChannelVoucherItemsVO)channleVoucherItemList.get(i)).getVoucherType());
			            	vomsCategoryVO.setName(((ChannelVoucherItemsVO)channleVoucherItemList.get(i)).getVoucherType());
			            	vomsCategoryVO.setStatus("Y");
			            	voucherTypeList.add(vomsCategoryVO);
		            	}
		            	
		            }
		            
	                
			        ArrayList mrplist = new ArrayList();
					if (voucherTypeList.isEmpty()) {

						 MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_PARENT_DENOMINATION_EXIST, null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.NO_PARENT_DENOMINATION_EXIST);
							masterErrorList.setErrorMsg(msg);
							masterErrorListMain.add(masterErrorList);
					}
					
					
					Boolean noProdList=false;
					String forVoucherType="";
					if (!voucherTypeList.isEmpty()) {
						
						ArrayList<VomsProductVO>vomsProductlistemp=null;
						ArrayList vomsProductlist = new ArrayList<>(); ;
						for(int i =0;i<voucherTypeList.size();i++)
						{	 vomsProductlistemp= new ArrayList<>();
							vomsProductlistemp = vomsProductDAO.loadProductDetailsList(con, ((VomsCategoryVO)voucherTypeList.get(i)).getVoucherType(), "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "", ((ChannelVoucherItemsVO)channleVoucherItemList.get(0)).getNetworkCode(), ((ChannelVoucherItemsVO)channleVoucherItemList.get(i)).getSegment());
							if(!BTSLUtil.isNullOrEmptyList(vomsProductlistemp)) {
								vomsProductlist.add(vomsProductlistemp);
							}else {
								
								noProdList=true;
								forVoucherType=((VomsCategoryVO)voucherTypeList.get(i)).getVoucherType();
								break;
							}
							
						}
						
						if(noProdList) {
							
							MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_PROD_LIST, new String[] {forVoucherType});
							masterErrorList.setErrorCode(PretupsErrorCodesI.NO_PROD_LIST);
							masterErrorList.setErrorMsg(msg);
							masterErrorListMain.add(masterErrorList);
							
						}
					
		
						Boolean noDenom=false;
						String noDenomVoucher="";
						for(int i =0;i<voucherTypeList.size();i++)
						{	
						vomsCategoryList = vomsCategorywebDAO.loadCategoryList(con,((VomsCategoryVO)voucherTypeList.get(i)).getVoucherType(), VOMSI.VOMS_STATUS_ACTIVE, VOMSI.EVD_CATEGORY_TYPE_FIXED, true, channelTransferVO.getNetworkCode(),null);
						if (vomsCategoryList.isEmpty()) {
							noDenom=true;
							noDenomVoucher=((VomsCategoryVO)voucherTypeList.get(i)).getVoucherType();
							break;
							
						}
						}
						
						
						if(noDenom) {
							
							MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_ACTIVE_DENOM_LIST, new String[] {forVoucherType});
							masterErrorList.setErrorCode(PretupsErrorCodesI.NO_ACTIVE_DENOM_LIST);
							masterErrorList.setErrorMsg(msg);
							masterErrorListMain.add(masterErrorList);
							
						}
					
						
						
						
						/*for (int i = 0; i < vomsCategoryList.size(); i++) {
							vomsCategoryVO = (VomsCategoryVO) vomsCategoryList.get(i);
							if (BTSLUtil.isNullString(activemrpstr)) {
								activemrpstr = Double.toString(vomsCategoryVO.getMrp());
								mrp = Double.toString(vomsCategoryVO.getMrp());
								lv = new ListValueVO(mrp, mrp);
								mrplist.add(lv);
							} else {
								activemrpstr = activemrpstr + "," + vomsCategoryVO.getMrp();
								mrp = Double.toString(vomsCategoryVO.getMrp());
								lv = new ListValueVO(mrp, mrp);
								mrplist.add(lv);
							}
						}
					}
					*/
			
					VomsProductVO vo = null;
					ArrayList arList = null;
					slabslist = new ArrayList();
					Iterator itr = null;
					
					for (int i = 0; i < length; i++) {
						vomsOrderVO = new VomsBatchVO();
						voucherItemVO = (ChannelVoucherItemsVO)channleVoucherItemList.get(i);
						vomsOrderVO.setSeq_id(BTSLUtil.parseLongToInt(voucherItemVO.getSNo()));
						vomsOrderVO.setDenomination(voucherItemVO.getTransferMrp()+".0");
						vomsOrderVO.setQuantity(voucherItemVO.getRequiredQuantity()+"");
						vomsOrderVO.setFromSerialNo(voucherItemVO.getFromSerialNum());
						vomsOrderVO.setToSerialNo(voucherItemVO.getToSerialNum());
						
						boolean isContinurPrd=true;
						for(int j=0;j<vomsProductlist.size();j++){
							if(!isContinurPrd){
								break;
							}
							itr = ((ArrayList) vomsProductlist.get(j)).iterator();
							arList = new ArrayList();
							while(itr.hasNext()){
								vo = (VomsProductVO)itr.next();
								if(voucherItemVO.getVoucherType().equals(vo.getVoucherType())) {
									if(BTSLUtil.floatEqualityCheck((double) voucherItemVO.getTransferMrp(), (double) vo.getMrp(), "==")){
										arList.add(vo);
										vomsOrderVO.setProductName(vo.getProductName());
										isContinurPrd=false;
										break;
										
									}
								}
							
							}
						}
						
						vomsOrderVO.setPreQuantity(voucherItemVO.getRequiredQuantity()+"");
						vomsOrderVO.setPreFromSerialNo(voucherItemVO.getFromSerialNum());
						vomsOrderVO.setPreToSerialNo(voucherItemVO.getToSerialNum());
						vomsOrderVO.setPreProductId(voucherItemVO.getProductId());
						vomsOrderVO.setProductlist(arList);
						slabslist.add(vomsOrderVO);
					}
					
					
					
					if(BTSLUtil.isNullOrEmptyList(slabslist)) {
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVLID_ACTION, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.INVLID_ACTION);
						masterErrorList.setErrorMsg(msg);
						masterErrorListMain.add(masterErrorList);
						
						
					}
					
				
					//if errors exist a we dont proceed further  
					//and go to next itteration 
					if(masterErrorListMain.size()>0) {
						
						rowErrorMsgLists.setMasterErrorList(masterErrorListMain);

						if(errorMap.getRowErrorMsgLists() == null)
						{
							List<RowErrorMsgLists> rowErrorMsgListsMain = new ArrayList<RowErrorMsgLists>();
							errorMap.setRowErrorMsgLists(rowErrorMsgListsMain);								
							}
						errorMap.getRowErrorMsgLists().add(rowErrorMsgLists);
						continue;
						
						
					}
				
					
					// for setting error of individual slabs
					ArrayList<RowErrorMsgList> rowErrorMsgListsVoucher = new ArrayList<RowErrorMsgList>(); 
						
					
					
					this.confirmVoucherProductDetalis(con, channelTransferVO,slabslist,vomsProductlist,vomsCategoryList,
							voucherTypeList, voucherDetails, curDate,apprvlLevel,rowErrorMsgListsVoucher,requestVO.getStatus());
					
					//if errors exist after confirmVoucherProductDetalis we dont approve the transaction req 
					//and go to next itteration 
					if(rowErrorMsgListsVoucher.size()>0) {
						
						 rowErrorMsgLists.setRowErrorMsgList(rowErrorMsgListsVoucher);

							if(errorMap.getRowErrorMsgLists() == null)
							{
								List<RowErrorMsgLists> rowErrorMsgListsMain = new ArrayList<RowErrorMsgLists>();
								errorMap.setRowErrorMsgLists(rowErrorMsgListsMain);								
								}
						
						 errorMap.getRowErrorMsgLists().add(rowErrorMsgLists);
						 
						  continue;
						
					}
	                
					if(requestVO.getStatus().equalsIgnoreCase("R")) {
						channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
					}
					
					int saved=this.saveVoucherProductDetalis(slabslist,channelTransferVO,apprvLevelInt,requestVO,paymentDetailsReq,
							voucherDetails,loginUserVO,channelVoucherItemListTemp);
					
					
					if(saved==1) {
						baseResponse.setStatus(200);
						String msg="";
						if(requestVO.getStatus().equalsIgnoreCase("Y")) {
							msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_TRF_APPROVAL_API, new String[] {channelTransferVO.getTransferID(),requestVO.getApprovalLevel()});
							baseResponse.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL_API);
						}
						else {
							msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_TRF_APPROVAL_REJECT, new String[] {channelTransferVO.getTransferID()});
							baseResponse.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL_REJECT);
						}
						baseResponse.setMessage(msg);
						
						baseResponse.setTransactionId(channelTransferVO.getTransferID());
						successList.add(baseResponse);
					}
				
					
					
						
						
					}else {
						
						if(masterErrorListMain.size()>0) {
							
							rowErrorMsgLists.setMasterErrorList(masterErrorListMain);

							if(errorMap.getRowErrorMsgLists() == null)
							{
								List<RowErrorMsgLists> rowErrorMsgListsMain = new ArrayList<RowErrorMsgLists>();
								errorMap.setRowErrorMsgLists(rowErrorMsgListsMain);								
								}
							errorMap.getRowErrorMsgLists().add(rowErrorMsgLists);
							continue;
							
							
						}
					
					}

//				
				
			}else {
				
				
				
				
				if(errorMap.getRowErrorMsgLists() == null)
				{
					List<RowErrorMsgLists> rowErrorMsgListsMain = new ArrayList<RowErrorMsgLists>();
					errorMap.setRowErrorMsgLists(rowErrorMsgListsMain);								
					}
				errorMap.getRowErrorMsgLists().add(rowErrorMsgLists);
				continue;
			
			
				}
				
	/*			
				
		if(rowErrorMsgLists.getMasterErrorList().size()>0 || rowErrorMsgLists.getRowErrorMsgList().size()>0)
		{
			if(errorMap.getRowErrorMsgLists() == null)
			{
				List<RowErrorMsgLists> rowErrorMsgListsMain = new ArrayList<RowErrorMsgLists>();
				errorMap.setRowErrorMsgLists(rowErrorMsgListsMain);								
				}
			errorMap.getRowErrorMsgLists().add(rowErrorMsgLists);
			continue;
	
		}*/
	
		
			}
			
			
			if(apiResponse.getErrorMap()!=null && apiResponse.getErrorMap().getRowErrorMsgLists()!=null)
			{
				apiResponse.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				
				apiResponse.setMessageCode("MULTI_VALIDATION_ERROR");
				
				String badReq=Integer.toString(HttpStatus.SC_BAD_REQUEST) ;
				apiResponse.setStatus(badReq);
				
			}
			else
			{
				apiResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
				apiResponse.setMessage(PretupsI.RECORD_SUCESS);
				response1.setStatus(HttpStatus.SC_OK);
			}
			
		
		}
		
		catch (Exception e) {
				_log.error("processVoucherApprvRequest", "Exception:e=" + e);
				_log.errorTrace(methodName, e);
				apiResponse.setService("O2CVOUCHERAPPRVRESP");
				apiResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
		        String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null);
		        apiResponse.setMessage(resmsg);
		        if (Arrays.asList(PretupsI.OAUTHCODES).contains(resmsg)) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					apiResponse.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
				} else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					apiResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				}
		    
			}
		
		finally {
				if (mcomCon != null) {
					mcomCon.close("O2cServiceImpl#processVoucherApprvRequest");
					mcomCon = null;
				}
			
			}

	}
	
	/**
	 * To get products in the system
	 * @param p_con
	 * @param p_userVO
	 * @param p_response
	 * @throws BTSLBaseException
	 */
	@Override
	public void processO2CProductDownlaod(Connection p_con , ChannelUserVO p_userVO , O2CProductsResponseVO p_response) throws BTSLBaseException{
		
//		public static final Log log = LogFactory.getLog(O2CServiceImpl.class.getName());

		ArrayList<ProductVO> productList = null;
		ListValueVO listValueVO = null;
		String productType = "'";
		OperatorUtilI _operatorUtil = null;
		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("searchattribute", e);
		}
	       if (PretupsI.YES.equals(p_userVO.getCategoryVO().getProductTypeAllowed())) {
               p_userVO.setAssociatedProductTypeList(
                             new ProductTypeDAO().loadUserProductsListForLogin(p_con, p_userVO.getUserID()));
         } else {
                p_userVO.setAssociatedProductTypeList(_operatorUtil.loadProductCodeList());
         }

		final ArrayList prodTypList = new ArrayList(p_userVO.getAssociatedProductTypeList());
        
        if (prodTypList != null) {
            for (int i = 0, j = prodTypList.size(); i < j; i++) {
                listValueVO = (ListValueVO) prodTypList.get(i);
                if (PretupsI.P2P_MODULE.equals(listValueVO.getValue())) {
                    prodTypList.remove(i);
                    i--;
                    j--;
                } else {
                    productType = productType + listValueVO.getValue() + "','";
                }
            }
        }
        if (productType.length() > 1) {
            productType = productType.substring(0, productType.length() - 2);
        }
        final NetworkProductDAO networkProductDAO = new NetworkProductDAO();

        final String status = "'" + PretupsI.YES + "'";
     
        if (prodTypList == null || prodTypList.isEmpty()) {
            // not in session
            // then load from
            // module base
            productList = networkProductDAO.loadProductList(p_con, null, status, PretupsI.C2S_MODULE, p_userVO.getNetworkID());
        } else {
            // product load on product type base
            productList = networkProductDAO.loadProductList(p_con, productType, status, null, p_userVO.getNetworkID());
        }
        
        ArrayList<O2CProductResponseData> productsList = new ArrayList<O2CProductResponseData>();
        
        for(ProductVO prod: productList) {
        	O2CProductResponseData o2cProductResponseData = new O2CProductResponseData();
        	o2cProductResponseData.setCode(prod.getProductCode());
        	o2cProductResponseData.setName(prod.getProductName());
        	o2cProductResponseData.setShortCode(prod.getProductShortCode());
        	productsList.add(o2cProductResponseData);
	       }
        
			p_response.setProductsList(productsList);
	}
	
	
	/**Validate the basic  details before the transaction goes to approval
	 * @param con
	 * @param o2CVoucherTransferReqData
	 * @param senderVO
	 * @param receiverVO
	 * @param errorMap
	 * @param vomsBatchList
	 * @return
	 * @throws Exception
	 */
	public static Boolean o2CValidate(Connection con,O2CVoucherApprvData o2CVoucherTransferReqData,ChannelUserVO senderVO,
			RowErrorMsgLists rowErrorMsgLists, ArrayList<VomsBatchVO> vomsBatchList) throws Exception {
		ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
		ArrayList<RowErrorMsgList> rowErrorMsgListsVoucher = new ArrayList<RowErrorMsgList>();
		Locale locale;
		if(!BTSLUtil.isNullObject(senderVO.getUserPhoneVO()))
			locale = new Locale(senderVO.getUserPhoneVO().getPhoneLanguage(), senderVO.getUserPhoneVO().getCountry());
		else
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		Boolean error = false;
		ChannelTransferDAO  channelTransferDAOVal = new  ChannelTransferDAO();
		
		boolean isContinue =true;
		
		//pin functionality 
		/*if(PretupsI.YES.equals(senderVO.getCategoryVO().getSmsInterfaceAllowed())) {
			if (SystemPreferences.PIN_REQUIRED && senderVO.getUserPhoneVO().getPinRequired().equals(PretupsI.YES)) {
				try {
					ChannelUserBL.validatePIN(con, senderVO, o2CVoucherTransferReqData.getPin());
				} catch (BTSLBaseException be) {
					MasterErrorList masterErrorList = new MasterErrorList();
					String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
					masterErrorList.setErrorCode(be.getMessageKey());
					masterErrorList.setErrorMsg(msg);
					masterErrorListMain.add(masterErrorList);
					isContinue = false;
				}
			}
			
		}*/
		
		
		String apprvlLevelVal="";
	
		if(o2CVoucherTransferReqData.getApprovalLevel().equalsIgnoreCase(PretupsI.APPRVL_LEVEL_1)) 
		{
			apprvlLevelVal=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
		
		}
		else if(o2CVoucherTransferReqData.getApprovalLevel().equalsIgnoreCase(PretupsI.APPRVL_LEVEL_2))
		{
			
			apprvlLevelVal=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
			
			
		}
		else if(o2CVoucherTransferReqData.getApprovalLevel().equalsIgnoreCase(PretupsI.APPRVL_LEVEL_3))
		{
			apprvlLevelVal=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
	
		}else {
			
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_O2C_APPROVAL_LEVEL, 
					new String[] {o2CVoucherTransferReqData.getApprovalLevel()});
			masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_O2C_APPROVAL_LEVEL);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
			isContinue = false;
			error=true;
			
			
		}
		
		
		
	
		


		  String statusVal="";
		  
		 if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(apprvlLevelVal)) {
			 statusVal=PretupsI.NEW;
		 }
		 else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(apprvlLevelVal ))
		 {
			 statusVal=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
		 }
		 else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(apprvlLevelVal) )
		 {
			 statusVal=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
		 }
		 
		
		 
		 
		
		
		
		 ChannelTransferVO  channelTransferVOVal= new ChannelTransferVO();
		
		if(BTSLUtil.isNullString(o2CVoucherTransferReqData.getTransactionId())){
			error = true;
			isContinue = false;
		MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRF_ID_EMPTY, null);
		masterErrorList.setErrorCode(PretupsErrorCodesI.TRF_ID_EMPTY);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}else {
			
			channelTransferVOVal.setTransferID(o2CVoucherTransferReqData.getTransactionId());
			channelTransferDAOVal.loadChannelTransferDetail(con, channelTransferVOVal, statusVal);
			
			
			if(BTSLUtil.isNullString(channelTransferVOVal.getStatus())) {
				error = true;
				isContinue = false;
			MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_DETAILS_FOR_TXNID_STATUS,
						new String[] {o2CVoucherTransferReqData.getTransactionId(),statusVal});
			masterErrorList.setErrorCode(PretupsErrorCodesI.NO_DETAILS_FOR_TXNID_STATUS);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
			
		}
		
		
		if(BTSLUtil.isNullString(o2CVoucherTransferReqData.getToUserId())){
			error = true;
			isContinue = false;
		MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_TOUSERID, null);
		masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_TOUSERID);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		
		if(o2CVoucherTransferReqData.getStatus().equalsIgnoreCase("y"))
		{
			if(BTSLUtil.isNullString(o2CVoucherTransferReqData.getExternalTxnDate())){
				error = true;
				isContinue = false;
			MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_EXT_DATE_BLANK, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_EXT_DATE_BLANK);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}else {
				if(!BTSLUtil.isValidDatePattern(o2CVoucherTransferReqData.getExternalTxnDate())){
					error = true;
					isContinue = false;
				MasterErrorList masterErrorList = new MasterErrorList();
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER);
					masterErrorList.setErrorMsg(msg);
					masterErrorListMain.add(masterErrorList);
				}
			}
		}
		
		
		
		if(BTSLUtil.isNullString(o2CVoucherTransferReqData.getTransferDate())){
			error = true;
			isContinue = false;
		MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_TRANSFER_DATE, null);
		masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_TRANSFER_DATE);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}else {
			if(!BTSLUtil.isValidDatePattern(o2CVoucherTransferReqData.getTransferDate())){
				error = true;
				isContinue = false;
			MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_TRANSFER_DATE, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_TRANSFER_DATE);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		
		
	
		
		if(BTSLUtil.isNullString(o2CVoucherTransferReqData.getStatus())){
			error = true;
			isContinue = false;
		MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE, null);
		masterErrorList.setErrorCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}else {
			
			if(!(o2CVoucherTransferReqData.getStatus().equalsIgnoreCase("y")) 
					&& !(o2CVoucherTransferReqData.getStatus().equalsIgnoreCase("r")))
			{
				error = true;
				isContinue = false;
			MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		
	
		if (!BTSLUtil.isNullString(o2CVoucherTransferReqData.getExternalTxnNum())) {
			if (SystemPreferences.EXTERNAL_TXN_NUMERIC) {
				
				if(BTSLUtil.isNumeric(o2CVoucherTransferReqData.getExternalTxnNum())) {
					final long externalTxnIDLong = Long.parseLong(o2CVoucherTransferReqData.getExternalTxnNum());
					
					if (externalTxnIDLong < 0 ||  !BTSLUtil.isNumeric(o2CVoucherTransferReqData.getExternalTxnNum())) {
						error = true;
						isContinue = false;
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_NUMERIC, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_NUMERIC);
						masterErrorList.setErrorMsg(msg);
						masterErrorListMain.add(masterErrorList);
					}
					
				}else {
					
					error = true;
					isContinue = false;
					MasterErrorList masterErrorList = new MasterErrorList();
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_NUMERIC, null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_NUMERIC);
					masterErrorList.setErrorMsg(msg);
					masterErrorListMain.add(masterErrorList);
				}
				
					
				
			}

			if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
				final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
				final boolean isExternalTxnNotUnique = channelTransferDAO.isExtTxnExists(con,o2CVoucherTransferReqData.getExternalTxnNum(), o2CVoucherTransferReqData.getTransactionId());
				if (isExternalTxnNotUnique) {
					error = true;
					isContinue = false;
					MasterErrorList masterErrorList = new MasterErrorList();
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_UNIQUE, null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_UNIQUE);
					masterErrorList.setErrorMsg(msg);
					masterErrorListMain.add(masterErrorList);
				}
			}
		}else {
			
			if(o2CVoucherTransferReqData.getStatus().equalsIgnoreCase("y")) {
				error = true;
				isContinue = false;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_EXT_TXN_NO_BLANK, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_EXT_TXN_NO_BLANK);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
			
		}
		
		
		
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		ChannelUserVO receiverChannelUserVO = new ChannelUserVO();
		receiverChannelUserVO = channelUserDAO.loadChannelUserByUserID(con,
				o2CVoucherTransferReqData.getToUserId());
		
		if(BTSLUtil.isNullObject(receiverChannelUserVO) && !BTSLUtil.isNullString(o2CVoucherTransferReqData.getToUserId())){
			error = true;
			isContinue = false;
		MasterErrorList masterErrorList = new MasterErrorList();
		String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_USERDET_TOUSERID, null);
		masterErrorList.setErrorCode(PretupsErrorCodesI.NO_USERDET_TOUSERID);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		
		
		
		
		
		
		
		
		if(!isContinue) {
			rowErrorMsgLists.setMasterErrorList(masterErrorListMain);	
			 return true;
		}
		
		
		HashMap<String,Object> reqData = new HashMap<String,Object>();
		reqData.put("remarks", o2CVoucherTransferReqData.getRemarks());
		reqData.put("refNumber", o2CVoucherTransferReqData.getRefrenceNumber());
		reqData.put("paymentDetails", o2CVoucherTransferReqData.getPaymentDetails());
		reqData.put("voucherDetails", o2CVoucherTransferReqData.getVoucherDetails());
		
		boolean reqError=false;;
		if(o2CVoucherTransferReqData.getStatus().equalsIgnoreCase("y")) {
			 reqError =validateRequestData(masterErrorListMain, locale,reqData);
		}
		
		if(reqError){
			rowErrorMsgLists.setMasterErrorList(masterErrorListMain);
			 }
		
		
		if(o2CVoucherTransferReqData.getStatus().equalsIgnoreCase("y")) {
			error = validateVoucher(con, vomsBatchList, o2CVoucherTransferReqData.getVoucherDetails(), rowErrorMsgListsVoucher, receiverChannelUserVO.getUserID(), senderVO.getNetworkID(),o2CVoucherTransferReqData.getTransactionId());

		}
		 
		 if(error){
			 rowErrorMsgLists.setRowErrorMsgList(rowErrorMsgListsVoucher);
			 
			 }
		 if(reqError||error){
			 return true;
		 }
		 return false;
	}
	
	
	
	
	/**
	 * @param con
	 * @param vomsBatchlist
	 * @param voucherDetailsList
	 * @param rowErrorMsgListsVoucher
	 * @param userId
	 * @param toUserId
	 * @param networkCode
	 * @return
	 * @throws BTSLBaseException
	 */public static boolean validateVoucher(Connection con,ArrayList<VomsBatchVO> vomsBatchlist,List<VoucherDetailsApprv> voucherDetailsList,
				ArrayList<RowErrorMsgList> rowErrorMsgListsVoucher,String toUserId,String networkCode,String transferId) throws BTSLBaseException{
		 Log log = LogFactory.getLog(O2CServiceImpl.class.getName());	
		 final float EPSILON=0.0000001f;
		 String methodName = "validateVoucher";
			if (log.isDebugEnabled()) {
				StringBuilder loggerValue = new StringBuilder();
				loggerValue.setLength(0);
	        	loggerValue.append("Entered voucherDetailsList.size(): ");
	        	loggerValue.append(voucherDetailsList.size());
	        	loggerValue.append("toUserId: ");
	        	loggerValue.append(toUserId);
	        	loggerValue.append("networkCode: ");
	        	loggerValue.append(networkCode);
	            log.debug(methodName,loggerValue );
			}
			
			boolean error = false;
			Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			try{
				ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
				
				final Map<String, String> denomMap = new HashMap<String, String>();
				int i =0;
				Date currentDate = new Date();
				RowErrorMsgList rowErrorMsgListsVouchesddtemp=new RowErrorMsgList();
				ArrayList<RowErrorMsgLists> rowErrorMsgListstemp = new  ArrayList<>();
			
				for(VoucherDetailsApprv voucherDetails : voucherDetailsList){
					
					
					VomsBatchVO vomsBatchVO = new VomsBatchVO();
					long quantity = 0;
					int row = i+1;
					error = false;
					RowErrorMsgLists rowErrorMsgListssVoucher= new RowErrorMsgLists();
					rowErrorMsgListssVoucher.setRowValue(String.valueOf(row));
					rowErrorMsgListssVoucher.setRowName("Voucher "+voucherDetails.getVoucherType());
					
					
					
					
					
					

				
					ArrayList<MasterErrorList> masterErrorLists1 = new ArrayList<MasterErrorList>();
					
					if (BTSLUtil.isNullString(voucherDetails.getReqQuantity())){
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.BLANK_REQ_QTY);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_REQ_QTY, null));
						masterErrorLists1.add(masterErrorListss);
						
						
					}
					if (BTSLUtil.isNullString(voucherDetails.getVoucherType())){
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.VOUCHER_TYPE_REQUIRED);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_TYPE_REQUIRED, null));
						masterErrorLists1.add(masterErrorListss);
					}
					if (BTSLUtil.isNullString(voucherDetails.getVouchersegment())){
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.VOUCHER_SEGMENT_REQUIRED);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_SEGMENT_REQUIRED, null));
						masterErrorLists1.add(masterErrorListss);
					}
					if(BTSLUtil.isNullString(voucherDetails.getDenomination())){
						error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2_DENO_BLANK);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2_DENO_BLANK, null));
						masterErrorLists1.add(masterErrorListss);
					}
					else if(!BTSLUtil.isDecimalValue(voucherDetails.getDenomination())){
						error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_DENO_INVALID);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_DENO_INVALID, null));
						masterErrorLists1.add(masterErrorListss);
					}

				   if (BTSLUtil.isNullString(voucherDetails.getFromSerialNo()) && Integer.parseInt( voucherDetails.getReqQuantity()) !=0 ){
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_FROMSNO_REQ);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_FROMSNO_REQ, null));
						masterErrorLists1.add(masterErrorListss);
					}
				   else if (!BTSLUtil.isNumeric(voucherDetails.getFromSerialNo()) ){
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_FROMSNO_NUMERIC);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_FROMSNO_NUMERIC, null));
						masterErrorLists1.add(masterErrorListss);
					}
				   if (BTSLUtil.isNullString(voucherDetails.getToSerialNo()) && Integer.parseInt( voucherDetails.getReqQuantity()) !=0) {
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_TOSNO_REQ);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_TOSNO_REQ, null));
						masterErrorLists1.add(masterErrorListss);
					}
				   else if (!BTSLUtil.isNumeric(voucherDetails.getToSerialNo())) {
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_TOSNO_NUMERIC);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_TOSNO_NUMERIC, null));
						masterErrorLists1.add(masterErrorListss);
					}
					if (!BTSLUtil.isNullString(voucherDetails.getToSerialNo()) && !BTSLUtil.isNullString(voucherDetails.getFromSerialNo()) 
							&& BTSLUtil.isNumeric(voucherDetails.getToSerialNo()) && BTSLUtil.isNumeric(voucherDetails.getFromSerialNo()) 
							&& !BTSLUtil.isNullString(voucherDetails.getDenomination()) && BTSLUtil.isDecimalValue(voucherDetails.getDenomination())) {
	                    if (Long.parseLong(voucherDetails.getToSerialNo()) < Long.parseLong(voucherDetails.getFromSerialNo())) {
	                    	error=true;
							MasterErrorList masterErrorListss = new MasterErrorList();
							masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_FROM_TO_INVALID);
							masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_FROM_TO_INVALID, null));
							masterErrorLists1.add(masterErrorListss);
	                    }
	                    
	                    quantity = Long.parseLong(voucherDetails.getToSerialNo()) - Long.parseLong(voucherDetails.getFromSerialNo()) + 1;
	    				 if(channelTransferWebDAO.doesRangeContainMultipleProfilesforO2c(con, voucherDetails.getFromSerialNo(), voucherDetails.getToSerialNo(), quantity,voucherDetails.getVoucherType(),voucherDetails.getVouchersegment(),voucherDetails.getDenomination(),networkCode)){
	    					error=true;
							MasterErrorList masterErrorListss = new MasterErrorList();
							masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_SNO_MULTPROF);
							masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_SNO_MULTPROF, new String[] {voucherDetails.getFromSerialNo(), voucherDetails.getToSerialNo()}));
							masterErrorLists1.add(masterErrorListss);
	    				}
	    				else{
	    					vomsBatchVO.setQuantity(String.valueOf(quantity));
	    				}
					  if (denomMap.containsKey(voucherDetails.getDenomination())) {
	                      if (Long.parseLong(voucherDetails.getFromSerialNo()) <= Long.parseLong(denomMap.get(voucherDetails.getDenomination()))) {
	                    	  error=true;
							  MasterErrorList masterErrorListss = new MasterErrorList();
							  masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_SNO_SEQUENTIAL);
							  masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_SNO_SEQUENTIAL, null));
							  masterErrorLists1.add(masterErrorListss);
	                      } else {
	                          denomMap.put(voucherDetails.getDenomination(), voucherDetails.getToSerialNo());
	                      }
	                  } else {
	                      denomMap.put(voucherDetails.getDenomination(), voucherDetails.getToSerialNo());
	                  }
	    				  
	    				VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
	    				VomsVoucherVO vomsVoucherVO=vomsVoucherDAO.getVoucherDetails(con,voucherDetails.getFromSerialNo());
			            if(vomsVoucherVO!=null){
			            	vomsBatchVO.setFromSerialNo(voucherDetails.getFromSerialNo());
		    				vomsBatchVO.setToSerialNo(voucherDetails.getToSerialNo());
		    				if(BTSLUtil.isNullString(voucherDetails.getVoucherProfileId()))
		    				{voucherDetails.setVoucherProfileId(vomsVoucherVO.getProductID());}
				            if (!vomsVoucherVO.getUserLocationCode().equals(networkCode)) 
				            {
			            	  error=true;
							  MasterErrorList masterErrorListss = new MasterErrorList();
							  masterErrorListss.setErrorCode(PretupsErrorCodesI.VOMS_O2C_VOUCHERS_FROM_DIFFERENT_NETWORK);
							  masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOMS_O2C_VOUCHERS_FROM_DIFFERENT_NETWORK, null));
							  masterErrorLists1.add(masterErrorListss);
			            	}
		    		           
							final double denomination =(vomsVoucherVO.getMRP()/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
		    					
							
							vomsBatchVO.setProductID(vomsVoucherVO.getProductID());
		    				vomsBatchVO.setVoucherType(vomsVoucherVO.getVoucherType());
		    				vomsBatchVO.setVouchersegment(vomsVoucherVO.getVoucherSegment());
		    				if(denomination!= Double.parseDouble(voucherDetails.getDenomination())){
		    					 error=true;
								 MasterErrorList masterErrorListss = new MasterErrorList();
								 masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_SNO_DENO_DIFF);
								 masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_SNO_DENO_DIFF, new String[]{voucherDetails.getDenomination()}));
								 masterErrorLists1.add(masterErrorListss);
				            }else{
				            	vomsBatchVO.setDenomination(""+denomination);
				            }
			            }
	    				vomsBatchVO.setCreatedBy(PretupsI.CATEGORY_TYPE_OPT);
	    				vomsBatchVO.set_NetworkCode(networkCode);
	    				vomsBatchVO.setCreatedDate(currentDate);
	    				vomsBatchVO.setModifiedDate(currentDate);
	    				vomsBatchVO.setModifiedOn(currentDate);
	    				vomsBatchVO.setCreatedOn(currentDate);
	    				vomsBatchVO.setToUserID(toUserId); 
	    				if (SystemPreferences.VOUCHER_EN_ON_TRACKING) {
	    						vomsBatchVO.setBatchType(VOMSI.BATCH_ENABLED);
	    					} 
	    				else {
	    						vomsBatchVO.setBatchType(VOMSI.VOMS_PRE_ACTIVE_STATUS);
	    					}
	    				String batch_no = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
	    				vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batch_no));

	    				if(BTSLUtil.isNullOrEmptyList(masterErrorLists1)){
	        				VomsProductDAO vomsProductDAO = new VomsProductDAO();

	    					// load product for vouchertype
		    				ArrayList<VomsProductVO>vomsProductlist = vomsProductDAO.loadProductDetailsList(con, vomsBatchVO.getVoucherType(), "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "", networkCode, vomsBatchVO.getSegment());
		    				if(!BTSLUtil.isNullOrEmptyList(vomsProductlist)) {
		    					ArrayList<VomsProductVO> itemlist = new ArrayList<>();
		    					for (VomsProductVO vomsProductVO : vomsProductlist) {
		                     		if (Math.abs(Double.parseDouble(vomsBatchVO.getDenomination())-vomsProductVO.getMrp()) < EPSILON) {
		                     			itemlist.add(vomsProductVO);
		                     		}
		                     	}
		                     	vomsBatchVO.setProductlist(itemlist);
		    				} 

					    	String type = vomsProductDAO.getTypeFromVoucherType(con, vomsBatchVO.getVoucherType());
							
		    				if (channelTransferWebDAO.validateVoucherSerialNo(con, voucherDetails.getFromSerialNo(),transferId,PretupsI.TRANSFER_TYPE_O2C)) {
		     					 error=true;
								 MasterErrorList masterErrorListss = new MasterErrorList();
								 masterErrorListss.setErrorCode(PretupsErrorCodesI.FROM_SERIAL_PENDING);
								 masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FROM_SERIAL_PENDING, new String[]{voucherDetails.getDenomination()}));
								 masterErrorLists1.add(masterErrorListss);
		    				
							}
		    				
							if (channelTransferWebDAO.validateVoucherSerialNo(con, voucherDetails.getToSerialNo(), transferId,PretupsI.TRANSFER_TYPE_O2C)) {
								 error=true;
								MasterErrorList masterErrorListss = new MasterErrorList();
								masterErrorListss.setErrorCode(PretupsErrorCodesI.TO_SERIAL_PENDING);
								masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TO_SERIAL_PENDING, new String[]{voucherDetails.getDenomination()}));
								masterErrorLists1.add(masterErrorListss);
		    				
							}
							
	    				}
					}
					
					if(error){
						rowErrorMsgListssVoucher.setMasterErrorList(masterErrorLists1);
						rowErrorMsgListstemp.add(rowErrorMsgListssVoucher);
						
						
						
						
					}
					vomsBatchlist.add(vomsBatchVO);
					i++;
				  }
				if(rowErrorMsgListstemp.size()>0) {
					error=true;
					rowErrorMsgListsVouchesddtemp.setRowErrorMsgLists(rowErrorMsgListstemp);
					rowErrorMsgListsVoucher.add(rowErrorMsgListsVouchesddtemp);
				}
				
				

		}catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.error(methodName, "Exception in validation of voucher list" + e.getMessage());
				}
				  log.errorTrace(methodName, e);
		          throw new BTSLBaseException("O2CVoucherTransferService", methodName, e.getMessage());
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting ....");
			}
			return error;
		}
	 
	 
	 /**
	  * Method used to confirm voucher details and also sets ChannelTransferitemsVOList
	  * @param con
	  * @param channelTransferVO
	  * @param slabsList
	  * @param vomsProductList
	  * @param vomsCategoryList
	  * @param voucherTypeList
	  * @param voucherDetails
	  * @param p_date
	  * @param approvalLevel
	  * @param rowErrorMsgListsVoucher
	  * @throws BTSLBaseException
	  * @throws ParseException
	  */
	 public void confirmVoucherProductDetalis(Connection con,ChannelTransferVO channelTransferVO, ArrayList slabsList,ArrayList vomsProductList,
			 ArrayList vomsCategoryList,ArrayList voucherTypeList,List<VoucherDetailsApprv> voucherDetails,
			 Date p_date,String approvalLevel,ArrayList<RowErrorMsgList> rowErrorMsgListsVoucher,String reqAcceptReject) throws BTSLBaseException, ParseException {
			final String methodName = "confirmVoucherProductDetalis";
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Entered");
			}
			
			VomsProductVO vomsProductVO = null;
			String[] arr = null;
			//Connection con = null;
			
			ListValueVO errorVO = null;
			ArrayList<ListValueVO> fileErrorList = null;
			arr = new String[3];
			
				//final ChannelTransferApprovalForm theForm = (ChannelTransferApprovalForm) form;
							VomsBatchVO vomsBatchVO = null;
							VomsPackageVO vomsPackageVO = null;
				

			
				
				fileErrorList = new ArrayList<ListValueVO>();
				
				
				RowErrorMsgList rowErrorMsgListsVouchesddtemp=new RowErrorMsgList();
				ArrayList<RowErrorMsgLists> rowErrorMsgListstemp = new  ArrayList<>();
				
			if (slabsList != null && !slabsList.isEmpty()) {
					double totalRequestedQuantity = 0;
					long totalVoucherQty = 0;
					ChannelTransferItemsVO transferItemsVO = null;
					ArrayList arrList = new ArrayList();
					ArrayList transferItemList=	(ArrayList)channelTransferVO.getChannelTransferitemsVOList().clone();
					Iterator itr = null;
					Boolean error=false;
						for (int i = 0; i < slabsList.size(); i++) {
							
							
							RowErrorMsgLists rowErrorMsgListssVoucher= new RowErrorMsgLists();
							rowErrorMsgListssVoucher.setRowValue(String.valueOf(i+1));
							rowErrorMsgListssVoucher.setRowName("Voucher "+voucherDetails.get(i).getVoucherType());
							ArrayList<MasterErrorList> masterErrorLists1 = new ArrayList<MasterErrorList>();
							
							vomsBatchVO = (VomsBatchVO) slabsList.get(i);
							vomsBatchVO.setProductID(voucherDetails.get(i).getVoucherProfileId());
							//vomsBatchVO.setPreQuantity(vomsBatchVO.getProductName());
							vomsBatchVO.setQuantity(voucherDetails.get(i).getReqQuantity());
							vomsBatchVO.setVoucherType(voucherDetails.get(i).getVoucherType());
							vomsBatchVO.setFromSerialNo(voucherDetails.get(i).getFromSerialNo());
							vomsBatchVO.setToSerialNo(voucherDetails.get(i).getToSerialNo());
							int vomsProductLists=vomsProductList.size();
							if (!vomsBatchVO.getProductlist().isEmpty() && vomsProductLists!=0) { 
								 VomsProductDAO vomsProductDAO = new VomsProductDAO();
								 String type = vomsProductDAO.getTypeFromVoucherType(con,((VomsCategoryVO)voucherTypeList.get(i)).getVoucherType());
								Boolean isContinue=true;
								 for (int j = 0; j <vomsProductLists ; j++) {
									 if(!isContinue) {
											break;
										}
									itr = ((ArrayList) vomsProductList.get(j)).iterator();
									
									
									while(itr.hasNext()){
										vomsProductVO = (VomsProductVO)itr.next();
										
										
										
										//vomsProductVO = (VomsProductVO) vomsProductList.get(j);
										if (vomsProductVO.getProductName().equals(vomsBatchVO.getProductName())&&vomsProductVO.getVoucherType().equals(vomsBatchVO.getVoucherType())) {
											if(vomsBatchVO.getQuantity() == null || vomsBatchVO.getQuantity().isEmpty() ){
//											
												error=true;
												MasterErrorList masterErrorListss = new MasterErrorList();
												masterErrorListss.setErrorCode(PretupsErrorCodesI.BLANK_REQ_QTY);
												masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_REQ_QTY, null));
												masterErrorLists1.add(masterErrorListss);

												
											}
											final double denomination = Double.parseDouble(vomsBatchVO.getDenomination());
											final Long voucherQty = Long.parseLong(vomsBatchVO.getQuantity());
											totalVoucherQty = totalVoucherQty+voucherQty;
											final double requestedMrp = denomination * voucherQty;
											totalRequestedQuantity = totalRequestedQuantity + requestedMrp;
										
												final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
												if (voucherQty > 0 && (vomsBatchVO.getQuantityLong() > vomsBatchVO.getPreQuantityLong()) && ( reqAcceptReject.equalsIgnoreCase("y"))) {
													error=true;
													MasterErrorList masterErrorListss = new MasterErrorList();
													String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.QTY_ERROR,
													new String[] {Long.toString(vomsBatchVO.getQuantityLong()),Long.toString(vomsBatchVO.getPreQuantityLong())});
													masterErrorListss.setErrorCode(PretupsErrorCodesI.QTY_ERROR);
													masterErrorListss.setErrorMsg(msg);
													masterErrorLists1.add(masterErrorListss);
												}
												
												
												//do validations only if request is for approval
												if( reqAcceptReject.equalsIgnoreCase("y"))
												{
													if (voucherQty > 0 
															&& (Long.parseLong(vomsBatchVO.getQuantity()) != (Long.parseLong(vomsBatchVO.getToSerialNo())-Long.parseLong(vomsBatchVO.getFromSerialNo())+1)) 
															) {
															
															error=true;
															MasterErrorList masterErrorListss = new MasterErrorList();
															String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERIAL_QTY_NOT_EQUAL,null);
															masterErrorListss.setErrorCode(PretupsErrorCodesI.SERIAL_QTY_NOT_EQUAL);
															masterErrorListss.setErrorMsg(msg);
															masterErrorLists1.add(masterErrorListss);

														}
														if (voucherQty > 0
															&& channelTransferWebDAO.validateVoucherSerialNo(con, vomsBatchVO.getFromSerialNo(),channelTransferVO.getTransferID(),PretupsI.TRANSFER_TYPE_O2C)
															) {
															
															error=true;
															MasterErrorList masterErrorListss = new MasterErrorList();
															String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL,
														new String[] {Integer.toString(i+1),vomsBatchVO.getProductName(),vomsBatchVO.getDenomination()});
											
															masterErrorListss.setErrorCode(PretupsErrorCodesI.VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL);
															masterErrorListss.setErrorMsg(msg);
															masterErrorLists1.add(masterErrorListss);
														

														}
														
														
														
														if (voucherQty > 0 
																&& channelTransferWebDAO.validateVoucherSerialNo(con, vomsBatchVO.getToSerialNo(), channelTransferVO.getTransferID(),PretupsI.TRANSFER_TYPE_O2C)
																&&( reqAcceptReject.equalsIgnoreCase("y"))) {
																
																
																error=true;
																
																MasterErrorList masterErrorListss = new MasterErrorList();
																String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOMS_O2C_TO_SERIAL_PENDING_APPROVAL,
															new String[] {Integer.toString(i+1),vomsBatchVO.getProductName(),vomsBatchVO.getDenomination()});
												
																masterErrorListss.setErrorCode(PretupsErrorCodesI.VOMS_O2C_TO_SERIAL_PENDING_APPROVAL);
																masterErrorListss.setErrorMsg(msg);
																masterErrorLists1.add(masterErrorListss);
												

																
															}
														
														
														if(voucherQty > 0) {
															if (!channelTransferWebDAO.validateSerialNo(con, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getProductID(),type)  && reqAcceptReject.equalsIgnoreCase("y")) {
																
																
																error=true;
																MasterErrorList masterErrorListss = new MasterErrorList();
																String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL1,
															new String[] {Integer.toString(i+1),vomsBatchVO.getProductName(),vomsBatchVO.getDenomination()});
												
																masterErrorListss.setErrorCode(PretupsErrorCodesI.VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL1);
																masterErrorListss.setErrorMsg(msg);
																masterErrorLists1.add(masterErrorListss);
												


															}
															if (!channelTransferWebDAO.validateSerialNo(con, vomsBatchVO.getToSerialNo(), vomsBatchVO.getProductID(),type)  && reqAcceptReject.equalsIgnoreCase("y")) {
																
																error=true;
																MasterErrorList masterErrorListss = new MasterErrorList();
																String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOMS_O2C_TO_SERIAL_PENDING_APPROVAL,
															new String[] {Integer.toString(i+1),vomsBatchVO.getProductName(),vomsBatchVO.getDenomination()});
												
																masterErrorListss.setErrorCode(PretupsErrorCodesI.VOMS_O2C_TO_SERIAL_PENDING_APPROVAL);
																masterErrorListss.setErrorMsg(msg);
																masterErrorLists1.add(masterErrorListss);

																
															}
															final ArrayList<VomsBatchVO> usedBatches = channelTransferWebDAO.validateBatch(con, vomsBatchVO);
															if (usedBatches != null && !usedBatches.isEmpty() && reqAcceptReject.equalsIgnoreCase("y")) {
																int usedBatche=usedBatches.size();
																error=true;
																for (int k = 0; k <usedBatche ; k++) {
																	arr[0] = "" + (i + 1);
																	arr[1] = usedBatches.get(k).getUsedFromSerialNo();
																	arr[2] = usedBatches.get(k).getUsedToSerialNo();
																	MasterErrorList masterErrorListss = new MasterErrorList();
																	String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOMS_O2C_INVALID_BATCH,
																new String[] {Integer.toString(i+1),arr[1],arr[2]});
													
																	masterErrorListss.setErrorCode(PretupsErrorCodesI.VOMS_O2C_INVALID_BATCH);
																	masterErrorListss.setErrorMsg(msg);
																	masterErrorLists1.add(masterErrorListss);
																}
															
															
															}
														}
														

												}

												
											transferItemsVO = (ChannelTransferItemsVO)transferItemList.get(0);
											transferItemsVO.setRequestedQuantity(Double.toString(totalRequestedQuantity));
											transferItemsVO.setVoucherQuantity(totalVoucherQty);
											transferItemsVO.setSenderDebitQty(0);
											transferItemsVO.setReceiverCreditQty(0);
											if (approvalLevel.equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1)) {
												transferItemsVO.setFirstApprovedQuantity(Double.toString(totalRequestedQuantity));
												//theForm.setFirstLevelApprovedQuantity(transferItemsVO.getRequestedQuantity());
											} else if (approvalLevel.equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2)) {
												transferItemsVO.setSecondApprovedQuantity(Double.toString(totalRequestedQuantity));
												//theForm.setSecondLevelApprovedQuantity(transferItemsVO.getRequestedQuantity());
											} else if (approvalLevel.equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3)) {
												transferItemsVO.setThirdApprovedQuantity(Double.toString(totalRequestedQuantity));
												
											}
											vomsBatchVO.setChannelTransferItemsVO(transferItemsVO);
											vomsBatchVO.setProductName(vomsProductVO.getProductName());
											isContinue=false;
											break;
											
										}
									
										}

								}
							}
							else
							{
						
								throw new BTSLBaseException("channeltransfer.transferdetails.error.productblank");
							}
							
							
								rowErrorMsgListssVoucher.setMasterErrorList(masterErrorLists1);
								rowErrorMsgListstemp.add(rowErrorMsgListssVoucher);
								
								
								
								
							
						}
						
						if(error) {
							
							rowErrorMsgListsVouchesddtemp.setRowErrorMsgLists(rowErrorMsgListstemp);
							rowErrorMsgListsVoucher.add(rowErrorMsgListsVouchesddtemp);
							return;
						}
						
						if (totalVoucherQty == 0 ) {
						/*	errorVO = new ListValueVO();
							errorVO.setOtherInfo(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request),"channeltransfer.transferdetails.error.totalquantityzero"));
							fileErrorList.add(errorVO);*/
							throw new BTSLBaseException("channeltransfer.transferdetails.error.totalquantityzero");
						}
				
					
					arrList.add(transferItemsVO);
				
					channelTransferVO.setChannelTransferitemsVOList(arrList);
					
					 ChannelTransferDAO channelTransferDAOI = new ChannelTransferDAO();
					
					 ChannelTransferVO p_channelTransferVO=new ChannelTransferVO();
					 
					Double d = Double.parseDouble(transferItemsVO.getRequestedQuantity().trim());
					channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(d.longValue()));
					
					//channelTransferVO.setTransferMRP(Long.parseLong(transferItemsVO.getRequestedQuantity().trim()));
					
					boolean closeTransaction = false;
					if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
						if (channelTransferVO.getTransferMRP() <= channelTransferVO.getFirstApproverLimit()) {
							closeTransaction=true;
							//theForm.setCloseTransaction(true);
						}
					}
					else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel )) {
						if (channelTransferVO.getTransferMRP() <= channelTransferVO.getSecondApprovalLimit()) {
							//theForm.setCloseTransaction(true);
							closeTransaction=true;
						}
					}
					else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(approvalLevel) ) {
						//theForm.setCloseTransaction(true);
						closeTransaction=true;
					}
//					if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(theForm.getPaymentInstrumentCode()))
//					{
//						theForm.setCloseTransaction(true);
//					}
					if(closeTransaction)
					{
						if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode())){ 
							ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO); 
						}
					}
					this.constructConfirmDetails(con, transferItemList,channelTransferVO,approvalLevel);
					
					
				}
			
					

		
		}

	 
	 
	/**
	 * Constructs the VO for after confirmed details	
	 * @param p_con
	 * @param transferItemList
	 * @param channelTransferVO
	 * @param approvalLevel2
	 * @throws ParseException
	 * @throws BTSLBaseException
	 */
	public void constructConfirmDetails(Connection p_con, ArrayList transferItemList,
			ChannelTransferVO channelTransferVO, String approvalLevel2) throws ParseException, BTSLBaseException {

		if (_log.isDebugEnabled()) {
			_log.debug("constructConfirmFormDetails", "  channelTransferVO  " + channelTransferVO);
		}
		long totTax1 = 0, totTax2 = 0, totTax3 = 0, totRequestedQty = 0, payableAmount = 0, netPayableAmt = 0, totTransferedAmt = 0, totalMRP = 0, totcommission = 0;
		long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0,otfValue=0,firAppQty=0,secAppQty=0,thrAppQty=0;
		try {
			
			ChannelTransferBL.loadAndCalculateTaxOnDenominations(p_con, channelTransferVO.getCommProfileSetId(), channelTransferVO.getCommProfileVersion(),
					channelTransferVO, true, "confirmback", PretupsI.TRANSFER_TYPE_O2C);
			ChannelTransferBL.calculateTotalMRPFromTaxAndDiscount(transferItemList, PretupsI.TRANSFER_TYPE_O2C, approvalLevel2,
					channelTransferVO);
			ChannelTransferItemsVO transferItemsVO = null;
			int itemsLists = channelTransferVO.getChannelTransferitemsVOList().size();
			for (int k = 0; k < itemsLists; k++) {
				transferItemsVO = (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(k);
				totTax1 += transferItemsVO.getTax1Value();
				totTax2 += transferItemsVO.getTax2Value();
				totTax3 += transferItemsVO.getTax3Value();
				totcommission += transferItemsVO.getCommValue();
				if (transferItemsVO.getRequestedQuantity() != null && BTSLUtil.isDecimalValue(transferItemsVO.getRequestedQuantity())) {
					totRequestedQty += PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity());
					if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {

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
				if (!BTSLUtil.isNullString(transferItemsVO.getFirstApprovedQuantity())) {
					firAppQty += Double.parseDouble(transferItemsVO.getFirstApprovedQuantity());
				}
				if (!BTSLUtil.isNullString(transferItemsVO.getSecondApprovedQuantity())) {
					secAppQty +=  Double.parseDouble(transferItemsVO.getSecondApprovedQuantity());
				}
				if (!BTSLUtil.isNullString(transferItemsVO.getThirdApprovedQuantity())) {
					thrAppQty +=  Double.parseDouble(transferItemsVO.getThirdApprovedQuantity());
				}
			}
			
			channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(totalMRP))); //200
			transferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(totalMRP));
			transferItemsVO.setSenderDebitQty(0);
			transferItemsVO.setReceiverCreditQty(0);
			channelTransferVO.setNetPayableAmount(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(netPayableAmt)));
			channelTransferVO.setPayableAmount(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(payableAmount)));
			
			//theForm.setTotalReqQty(PretupsBL.getDisplayAmount(totRequestedQty));
			channelTransferVO.setPayInstrumentAmt(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(totTransferedAmt)));
			channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(totTax1)));
			channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(totTax2)));
			channelTransferVO.setTotalTax3(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(totTax3)));
			channelTransferVO.setCommissionValue(PretupsBL.getDisplayAmount(totcommission));
			String str = PretupsBL.getDisplayAmount(totRequestedQty-payableAmount);
			channelTransferVO.setCommissionValue(PretupsBL.getDisplayAmount(commissionQty));
			
			channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(senderDebitQty)));
			channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(PretupsBL.getDisplayAmount(receiverCreditQty)));
			//theForm.setTotalOtfValue(PretupsBL.getDisplayAmount(otfValue));
			
			channelTransferVO.setLevelOneApprovedQuantity(String.valueOf(PretupsBL.getSystemAmount(firAppQty)));
			channelTransferVO.setLevelTwoApprovedQuantity(String.valueOf(PretupsBL.getSystemAmount(secAppQty)));
			channelTransferVO.setLevelThreeApprovedQuantity(String.valueOf(PretupsBL.getSystemAmount(thrAppQty)));
			//theForm.setTransferItemList(channelTransferVO.getChannelTransferitemsVOList());
			
		} catch (Exception e) {
			_log.error("", "Exceptin:e=" + e);

		}
	
		
	}

	
	/**
	 * This method is used for making insertion into DB for the transaction to be approved
	 * and also for sending sms and email
	 * @param slabsList
	 * @param channelTransferVO
	 * @param r_approvalLevel
	 * @param o2CVoucherTransferReqData
	 * @param paymentDetailsReq
	 * @param voucherDetails
	 * @param loginUserVO
	 * @param failure
	 */
	
	
	public int  saveVoucherProductDetalis(ArrayList slabsList, ChannelTransferVO channelTransferVO,
			int r_approvalLevel,O2CVoucherApprvData o2CVoucherTransferReqData,PaymentDetailsO2C paymentDetailsReq,
			List<VoucherDetailsApprv> voucherDetails,ChannelUserVO loginUserVO, ArrayList channelVoucherItemListTemp) {
		final String methodName = "saveVoucherProductDetalis";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		
		
		
		String message = null;
		final ArrayList batchVO_list = new ArrayList();
		VomsBatchVO vomsBatchVO = null;
		ChannelVoucherItemsVO voucherItemVO = null;
		String[] fromSerialNo = null;
		String[] toSerialNo = null;
		Connection con1 = null;
		MComConnectionI mcomCon1 = null;
		
		Date dt = new Date();
		final ChannelTransferDAO channelTransferDAOI = new ChannelTransferDAO();
				
		//final ChannelTransferApprovalForm theForm = (ChannelTransferApprovalForm) form;
		

		//ChannelTransferVO channelTransferVO = theForm.getChannelTransferVO();
		
		int approvalLevel = r_approvalLevel;
		String apprvlLevel="";
		if(approvalLevel == 1){
			channelTransferVO.setFirstApprovedBy(loginUserVO.getUserID());
			channelTransferVO.setFirstApprovedOn(dt);
			apprvlLevel=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;

		}
		else if(approvalLevel == 2){
			channelTransferVO.setSecondApprovedBy(loginUserVO.getUserID());
			channelTransferVO.setSecondApprovedOn(dt);
			apprvlLevel=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
		}
		else if(approvalLevel == 3){
			channelTransferVO.setThirdApprovedBy(loginUserVO.getUserID());
			channelTransferVO.setThirdApprovedOn(dt);
			apprvlLevel=PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
		}


		
		
		
		
		boolean sendOrderToApproval = false;
		try{
			
			
			if (channelTransferVO.getStatus()!=null && channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL)) {	
				int count=this.cancelVoucherOrder(loginUserVO,channelTransferVO,apprvlLevel,o2CVoucherTransferReqData);
				if(count==1) {
					return 1;
				}
				else {
					throw new BTSLBaseException(this, methodName, "channeltransfer.transferdetailssuccess.msg.unsuccess", "cancelVoucher");
				}
				
			}
			
			
			
			mcomCon1 = new MComConnection();
			con1 = mcomCon1.getConnection();
			
			if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(apprvlLevel)) {
				channelTransferVO.setFirstApprovalRemark(o2CVoucherTransferReqData.getRemarks());
				channelTransferVO.setFirstApprovedBy(loginUserVO.getUserID());
				channelTransferVO.setFirstApprovedOn(new Date());
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
				message = "channeltransfer.approval.levelone.msg.success";
				// for o2c transfer quantity change
				
				channelTransferVO.setExternalTxnNum(o2CVoucherTransferReqData.getExternalTxnNum());
				channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(o2CVoucherTransferReqData.getExternalTxnDate()));
				channelTransferVO.setReferenceNum(paymentDetailsReq.getPaymentinstnumber());

				if (channelTransferVO.getTransferMRP() <= channelTransferVO.getFirstApproverLimit()) {
					sendOrderToApproval = true;
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(apprvlLevel)) {
				channelTransferVO.setSecondApprovalRemark(o2CVoucherTransferReqData.getRemarks());
				channelTransferVO.setSecondApprovedBy(loginUserVO.getUserID());
				channelTransferVO.setSecondApprovedOn(new Date());
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
				message = "channeltransfer.approval.leveltwo.msg.success";
				// for o2c transfer quantity change

				channelTransferVO.setExternalTxnNum(o2CVoucherTransferReqData.getExternalTxnNum());
				channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(o2CVoucherTransferReqData.getExternalTxnDate()));
				channelTransferVO.setReferenceNum(paymentDetailsReq.getPaymentinstnumber());
				if (channelTransferVO.getTransferMRP() <= channelTransferVO.getSecondApprovalLimit()) {
					sendOrderToApproval = true;
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(apprvlLevel)) {
				channelTransferVO.setThirdApprovalRemark(o2CVoucherTransferReqData.getRemarks());
				channelTransferVO.setThirdApprovedBy(loginUserVO.getUserID());
				channelTransferVO.setThirdApprovedOn(new Date());
				message = "channeltransfer.approval.msg.success";
				// for o2c transfer quantity change

				channelTransferVO.setExternalTxnNum(o2CVoucherTransferReqData.getExternalTxnNum());
				channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(o2CVoucherTransferReqData.getExternalTxnDate()));
				channelTransferVO.setReferenceNum(paymentDetailsReq.getPaymentinstnumber());
				sendOrderToApproval = true;
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			}
			if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(paymentDetailsReq.getPaymentgatewayType()))
			{
				sendOrderToApproval = true;
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			}
			
			channelTransferVO.setPayInstrumentNum(paymentDetailsReq.getPaymentinstnumber());
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
			
			 Date paymentDateTemp = sdf.parse(paymentDetailsReq.getPaymentdate());
			channelTransferVO.setPayInstrumentDate(paymentDateTemp);

			
			
			
			ChannelVoucherItemsVO channelVoucherItemsVO = null;
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
			ChannelUserWebDAO channelUserWebDAO = null;

			
			channelUserWebDAO = new ChannelUserWebDAO();

			
		

			final Date currentDate = new Date();
			
			final ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList = new ArrayList<ChannelVoucherItemsVO>();
			long requestedQnty = 0L;
			String startSerialNo = null;
			String batch_no = null;
			String endSerialNo = null;
			VomsProductDAO 	vomsProductDAO=null;
			int slabLists=slabsList.size();
			if(voucherDetails.size()!=0) {
				fromSerialNo = new String[voucherDetails.size()];
				toSerialNo = new String[voucherDetails.size()];
			}
		
		
			
			for (int i = 0; i < slabLists; i++) {
				vomsBatchVO = (VomsBatchVO) slabsList.get(i);
				
				
				//voucherItemVO = (ChannelVoucherItemsVO) slabsList.get(i);
				vomsBatchVO.setProductID(voucherDetails.get(i).getVoucherProfileId());
				vomsBatchVO.setQuantity(voucherDetails.get(i).getReqQuantity());
				vomsBatchVO.setFromSerialNo(voucherDetails.get(i).getFromSerialNo());
				vomsBatchVO.setToSerialNo(voucherDetails.get(i).getToSerialNo());
				if (vomsBatchVO.getProductlist() != null) {
					startSerialNo = voucherDetails.get(i).getFromSerialNo();
					endSerialNo = voucherDetails.get(i).getToSerialNo();
					vomsBatchVO.setCreatedBy(loginUserVO.getUserID());
					vomsBatchVO.set_NetworkCode(loginUserVO.getNetworkID());
					vomsBatchVO.setFromSerialNo(startSerialNo);
					vomsBatchVO.setToSerialNo(endSerialNo);
					vomsBatchVO.setCreatedDate(currentDate);
					vomsBatchVO.setModifiedDate(currentDate);
					vomsBatchVO.setModifiedOn(currentDate);
					vomsBatchVO.setCreatedOn(currentDate);
					vomsBatchVO.setToUserID(channelTransferVO.getToUserID()); 
					vomsBatchVO.setVoucherType(vomsBatchVO.getVoucherType());
					vomsBatchVO.setExtTxnNo(channelTransferVO.getTransferID());
					//vomsBatchVO.setSegment(segment);
					if(sendOrderToApproval){
						vomsProductDAO = new VomsProductDAO();
				        String type=vomsProductDAO.getTypeFromVoucherType(con1,vomsBatchVO.getVoucherType());
						if(VOMSI.VOUCHER_TYPE_DIGITAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_DIGITAL.equals(type)){
							vomsBatchVO.setBatchType(VomsUtil.getNextVoucherLifeStatus(VOMSI.VOUCHER_NEW,type ));
					    }
					    else if(VOMSI.VOUCHER_TYPE_PHYSICAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_PHYSICAL.equals(type)){
					    	vomsBatchVO.setBatchType(VomsUtil.getNextVoucherLifeStatus(VOMSI.VOMS_WARE_HOUSE_STATUS,type ));
					    }
						batch_no = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
						vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batch_no));
						final int insert_count = channelTransferWebDAO.insertVomsBatches(con1, vomsBatchVO);
						if (insert_count > 0){
							batchVO_list.add(vomsBatchVO);
							//theForm.setBatchList(batchVO_list);
							vomsBatchVO.setTransferId(o2CVoucherTransferReqData.getTransactionId());
						} else {
							mcomCon1.finalRollback();
							throw new BTSLBaseException(this, methodName, "channeltransfer.transferdetailssuccess.msg.unsuccess", "confirmback");
						}
					}
					requestedQnty = requestedQnty + Long.parseLong(vomsBatchVO.getQuantity());
					channelVoucherItemsVO = new ChannelVoucherItemsVO();
					channelVoucherItemsVO.setTransferId(channelTransferVO.getTransferID());
					channelVoucherItemsVO.setTransferDate(channelTransferVO.getTransferDate());
					channelVoucherItemsVO.setTransferMRP((long)Double.parseDouble(vomsBatchVO.getDenomination()));
					channelVoucherItemsVO.setRequiredQuantity((long)Double.parseDouble(vomsBatchVO.getQuantity()));
					channelVoucherItemsVO.setVoucherType(vomsBatchVO.getVoucherType());
					channelVoucherItemsVO.setFromSerialNum(startSerialNo);
					channelVoucherItemsVO.setToSerialNum(endSerialNo);
					channelVoucherItemsVO.setProductId(vomsBatchVO.getProductID());
					channelVoucherItemsVO.setProductName(vomsBatchVO.getProductName());
					channelVoucherItemsVO.setSNo(vomsBatchVO.getSeq_id());
					if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(apprvlLevel)) {
						channelVoucherItemsVO.setInitiatedQuantity(vomsBatchVO.getPreQuantityLong());
						channelVoucherItemsVO.setFirstLevelApprovedQuantity(Long.parseLong(voucherDetails.get(i).getReqQuantity()));
					}else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(apprvlLevel)) {
						channelVoucherItemsVO.setInitiatedQuantity(((ChannelVoucherItemsVO)channelVoucherItemListTemp.get(i)).getInitiatedQuantity());
						channelVoucherItemsVO.setFirstLevelApprovedQuantity(vomsBatchVO.getPreQuantityLong());
						channelVoucherItemsVO.setSecondLevelApprovedQuantity(Long.parseLong(voucherDetails.get(i).getReqQuantity()));
					}
					
					
					
					
					channelVoucherItemsVOList.add(channelVoucherItemsVO);
					channelTransferVO.setChannelVoucherItemsVoList(channelVoucherItemsVOList);
				}
			}
			
			
			if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()) 
					&& PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus()) ){ 
				ChannelTransferBL.increaseOptOTFCounts(con1, channelTransferVO); 
			}
			int updateCount = 0;
		
			
			
				updateCount = channelTransferDAO.updateChannelTransferVoucherApproval(con1, channelTransferVO, approvalLevel);
			
			if(sendOrderToApproval){
				final Date date = new Date();
				ChannelTransferBL.updateOptToChannelUserInCounts(con1, channelTransferVO, "searchadomain", date);
			}
			
			
			if (updateCount > 0) {    
				mcomCon1.finalCommit();
				try{
					
					String primaryNumber="";
					Boolean isPrimaryNumber=false;
					
					if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
						final UserDAO userDAO = new UserDAO();
						UserPhoneVO phoneVO1 = userDAO.loadUserAnyPhoneVO(con1, channelTransferVO.getUserMsisdn());
						if ("Y".equalsIgnoreCase(phoneVO1.getPrimaryNumber())) {
							primaryNumber = phoneVO1.getMsisdn();
							isPrimaryNumber = true;
						} else {
							phoneVO1 = userDAO.loadUserPhoneVO(con1, phoneVO1.getUserId());
							primaryNumber = phoneVO1.getMsisdn();
							isPrimaryNumber= false;
						}
					}
					
					
					
					
					boolean _receiverMessageSendReq=false;
					String _serviceType=PretupsI.SERVICE_TYPE_CHNL_O2C_INTR;
					_receiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,loginUserVO.getNetworkID(),_serviceType)).booleanValue();
					String totalCommission = PretupsBL.getDisplayAmount(((ChannelTransferItemsVO)channelTransferVO.getChannelTransferitemsVOList().get(0)).getCommQuantity());
					if(sendOrderToApproval){
						final UserDAO userDAO = new UserDAO();
						UserPhoneVO primaryPhoneVO = null;
						if (SystemPreferences.SECONDARY_NUMBER_ALLOWED && (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED && !isPrimaryNumber)) {
							primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con1, primaryNumber);
						}
						final UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con1, channelTransferVO.getToUserCode());
						String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
						String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);

						if(_receiverMessageSendReq){
							String messageKey = PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_APPROVED;
							if(PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
								messageKey = PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_APPROVED_OUTSIDE_SETTLEMENT;
							}
							if (primaryPhoneVO != null) {
								country = primaryPhoneVO.getCountry();
								language = primaryPhoneVO.getPhoneLanguage();
								final Locale locale = new Locale(language, country);
								final Object[] smsListArr = prepareSMSMessageListForVoucher(con1, channelTransferVO);
								final String[] array = { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), totalCommission };
								final BTSLMessages messages = new BTSLMessages(messageKey, array);
								final PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale,
										channelTransferVO.getNetworkCode());
								pushMessage.push();
							}
							if (phoneVO != null) {
								country = phoneVO.getCountry();
								language = phoneVO.getPhoneLanguage();
								final Locale locale = new Locale(language, country);
								final Object[] smsListArr = prepareSMSMessageListForVoucher(con1, channelTransferVO);
								final String[] array = { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), totalCommission };
								final BTSLMessages messages = new BTSLMessages(messageKey, array);
								final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO
										.getNetworkCode());
								pushMessage.push();
							} 
						}
					}
				}catch (Exception e) {
					_log.error(methodName, " SMS notification failed" + e.getMessage());
					_log.errorTrace(methodName, e);
				}
				if (_log.isDebugEnabled()) {
					_log.debug(approvalLevel, "CurrentApprovalLevel" + " " + sendOrderToApproval);
				}
				
				
				if (SystemPreferences.O2C_EMAIL_NOTIFICATION) {
					final String email = channelUserWebDAO.loadUserEmail(con1, channelTransferVO.getToUserID());
					channelTransferVO.setEmail(email);
					if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(apprvlLevel)) {
						if(!sendOrderToApproval) {
							sendEmailNotification(con1, channelTransferVO, loginUserVO, channelTransferDAO, "APV2O2CTRF", "", "o2c.email.notification.subject.approver");
						} else {
							sendEmailNotification(con1, channelTransferVO, loginUserVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, "o2c.email.notification.content.transfer.completed");
						}
					} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(apprvlLevel)) {
						if(!sendOrderToApproval) {
							sendEmailNotification(con1, channelTransferVO, loginUserVO, channelTransferDAO, "APV3O2CTRF", "", "o2c.email.notification.subject.approver");
						} else {
							sendEmailNotification(con1, channelTransferVO, loginUserVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2, "o2c.email.notification.content.transfer.completed");
						}
					} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(apprvlLevel)) {
						sendEmailNotification(con1, channelTransferVO, loginUserVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3, "o2c.email.notification.content.transfer.completed");
					}
				}
			}else {
				sendOrderToApproval = false;
				mcomCon1.finalRollback();
				throw new BTSLBaseException(this, methodName, "channeltransfer.transferdetailssuccess.msg.unsuccess", "confirmback");
			}

			if(sendOrderToApproval){
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelTransferVO.getNetworkCode()))
				{
					if(channelTransferVO.isTargetAchieved() && PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus()))
					{
						//Message handling for OTF
						TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
						tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con1,channelTransferVO.getToUserID(),channelTransferVO.getMessageArgumentList());
					}
				}
				message = "channeltransfer.approval.msg.success";
				final VoucherChangeStatus voucherChangeStatus = new VoucherChangeStatus(batchVO_list);
				voucherChangeStatus.start();
			}
			final String args[] = { channelTransferVO.getTransferID() };
			//final BTSLMessages messages = new BTSLMessages(message, args, "o2cfinalvoucher");
			BTSLMessages messages = null;
		
				messages = new BTSLMessages(message, args, "o2cfinalvoucher");
		
		}catch (SQLException e) {
			
			
			return 0;
		} catch (BTSLBaseException e) {
			
			return 0;
		}
			catch (Exception e) {
			_log.error(methodName, "Exception:e=" + e);
			_log.errorTrace(methodName, e);
			return 0;
			
			
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting ");
			}
			if(mcomCon1 != null)
			{
				mcomCon1.close("O2CServiceImpl#saveVoucherProductDetalis");
				mcomCon1=null;
			}
			
		
		}
		
		return 1;
		
		
	}
	
	private Object[] prepareSMSMessageListForVoucher(Connection con, ChannelTransferVO channelTransferVO) {
		final String methodName = "prepareSMSMessageListForVoucher";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered channelTransferVO =  : ");
			loggerValue.append(channelTransferVO);
			_log.debug(methodName,  loggerValue);
		}
		final ArrayList txnSmsMessageList = new ArrayList();
		KeyArgumentVO keyArgumentVO = null;
		String argsArr[] = null;
		final ArrayList channelVoucherItemsVOList = channelTransferVO.getChannelVoucherItemsVoList();
		ChannelVoucherItemsVO channelVoucherItemsVO = null;
		for (int i = 0, k = channelVoucherItemsVOList.size(); i < k; i++) {
			channelVoucherItemsVO = (ChannelVoucherItemsVO)channelVoucherItemsVOList.get(i);
			keyArgumentVO = new KeyArgumentVO();
			argsArr = new String[2];
			argsArr[0] = String.valueOf(channelVoucherItemsVO.getTransferMrp());
			argsArr[1] = String.valueOf(channelVoucherItemsVO.getRequiredQuantity());
			keyArgumentVO.setKey(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2);
			keyArgumentVO.setArguments(argsArr);
			txnSmsMessageList.add(keyArgumentVO);
		}
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited txnSmsMessageList.size() = ");
			loggerValue.append(txnSmsMessageList.size());
			_log.debug(methodName,  loggerValue );
		}

		return (new Object[] { txnSmsMessageList });
	}
	
	
	
	
	private int cancelVoucherOrder(ChannelUserVO channelUserVO, ChannelTransferVO channelTransferVO,
			String approvalLevel,O2CVoucherApprvData o2CVoucherTransferReqData) {
		final String METHOD_NAME = "cancelVoucherOrder";
		if (_log.isDebugEnabled()) {
			_log.debug("cancelOrder", "Entered");
		}
		//ActionForward forward = null;
		Connection con2 = null;
		MComConnectionI mcomCon2 = null;
		String forwardPath = "cancellevelone";
		
		try {
			
			
			

			
			String failLevel = null;
			String message = "channeltransfer.approval.levelone.msg.cancel";
			if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
				channelTransferVO.setFirstApprovalRemark(o2CVoucherTransferReqData.getRemarks());
				failLevel = "one";
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
				channelTransferVO.setSecondApprovalRemark(o2CVoucherTransferReqData.getRemarks());
				/*forwardPath = "cancelleveltwo";
				nextForwardPath = "viewlistleveltwo";*/
				message = "channeltransfer.approval.leveltwo.msg.cancel";
				failLevel = "two";
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(approvalLevel)) {
				channelTransferVO.setThirdApprovalRemark(o2CVoucherTransferReqData.getRemarks());
				/*forwardPath = "cancellevelthree";
				nextForwardPath = "viewlistlevelthree";*/
				message = "channeltransfer.approval.levelthree.msg.cancel";
				failLevel = "three";
			}
          
			final Date date = new Date();
			/* if(theForm.getReconcilationFlag())
	           {
	        	   channelTransferVO.setFirstApprovedBy(channelUserVO.getUserID());
					channelTransferVO.setFirstApprovedOn(date);
	        	   
	           }*/
			channelTransferVO.setCanceledBy(channelUserVO.getUserID());
			channelTransferVO.setCanceledOn(date);
			channelTransferVO.setModifiedBy(channelUserVO.getUserID());
			channelTransferVO.setModifiedOn(date);
			String prevStatus = channelTransferVO.getStatus();
			channelTransferVO.setPreviousStatus(prevStatus);
			channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			//channelTransferVO.setReconciliationFlag(theForm.getReconcilationFlag());
			mcomCon2 = new MComConnection();con2=mcomCon2.getConnection();
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			
				final int updateCount = channelTransferDAO.cancelTransferOrder(con2, channelTransferVO, approvalLevel);
				
			if (updateCount > 0) {
		
				mcomCon2.finalCommit();
				final UserDAO userDAO = new UserDAO();
				final UserPhoneVO phoneVO = userDAO.loadUserPhoneVO(con2, channelTransferVO.getToUserID());
				String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
				if (phoneVO != null) {
					//channelTransferVO.setChannelTransferitemsVOList(theForm.getTransferItemList());
					country = phoneVO.getCountry();
					language = phoneVO.getPhoneLanguage();
				} else {
					final String arr[] = { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() };
					final BTSLMessages messages = new BTSLMessages("channeltransfer.cancelorder.phoneinfo.notexist.msg", arr, forwardPath);
					return 0 ;
				}

				
				final Locale locale = new Locale(language, country); 
                final Object[] smsListArr = prepareSMSMessageListForVoucher(con2, channelTransferVO);
                final String[] args = { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]) };
                final BTSLMessages btslmessages = new BTSLMessages(PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_CANCELLED, args);
                
				final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), btslmessages, channelTransferVO.getTransferID(), null, locale, channelTransferVO
						.getNetworkCode());
				pushMessage.push();
				
				//sending channel user email notification on cancel order
				ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
				if (SystemPreferences.O2C_EMAIL_NOTIFICATION) {
					final String email = channelUserWebDAO.loadUserEmail(con2, channelTransferVO.getToUserID());
					channelTransferVO.setEmail(email);
					sendEmailNotification(con2, channelTransferVO, channelUserVO, channelTransferDAO, "", "", "o2c.email.notification.subject.failed");
				}
				

			} else {
				//con.rollback();
				mcomCon2.finalRollback();
				throw new BTSLBaseException(this, "cancelVoucherOrder", "channeltransfer.cancel.msg.unsuccess", forwardPath);
			}
		} catch (SQLException e) {
			_log.error("cancelVoucherOrder", "SQLException:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
			return 0;
		} catch (BTSLBaseException e) {
			_log.error("cancelVoucherOrder", "BTSLBaseException:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
			return 0;
		} catch (Exception e) {
			_log.error("cancelVoucherOrder", "BTSLBaseException:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
			return 0;
		} finally {
			if (mcomCon2 != null) {
				mcomCon2.close("ChannelTransferApprovalAction#cancelVoucherOrder");
				mcomCon2 = null;
			}
			/*if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exiting forward:=" + forward);
			}*/
		}
		return 1 ;
	}
	
	
	
	
	private void sendEmailNotification(Connection p_con, ChannelTransferVO p_channelTransferVO, ChannelUserVO userVO, ChannelTransferDAO p_channelTransferDAO, String p_roleCode, String approvalLevel, String p_subject) {
		final String METHOD_NAME = "sendEmailNotification";
		//final MessageResources messages = ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY));
        final Locale locale = BTSLUtil.getSystemLocaleForEmail();
        
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}

		try {
			
			final String from = BTSLUtil.getMessage(locale,"o2c.email.notification.from");
			String cc = PretupsI.EMPTY;
			String message1 = null;
			final String bcc = "";
			String subject = "";
			boolean isHeaderAdded = false;
			p_channelTransferVO.setToUserMsisdn(p_channelTransferVO.getUserMsisdn());
			String notifyContent = "";
			if (!BTSLUtil.isNullString(p_roleCode) && "APV1O2CTRF".equals(p_roleCode)) { 
				notifyContent = BTSLUtil.getMessage(locale,"o2c.email.notification.content");
			}
			else if (!BTSLUtil.isNullString(p_roleCode) && "APV2O2CTRF".equals(p_roleCode)) { 
				notifyContent = BTSLUtil.getMessage(locale,"o2c.email.notification.content");
			} else if (!BTSLUtil.isNullString(p_roleCode) && "APV3O2CTRF".equals(p_roleCode)) { 
				notifyContent = BTSLUtil.getMessage(locale,"o2c.email.notification.content");
			}
			else if(p_subject.equalsIgnoreCase("o2c.email.notification.subject.initiate"))
				notifyContent = BTSLUtil.getMessage(locale,"o2c.email.notification.subject.initiate");
			else if(p_subject.equalsIgnoreCase("o2c.email.notification.subject.failed"))
				notifyContent = BTSLUtil.getMessage(locale,"o2c.email.notification.subject.failed");
			else{ 
				notifyContent = BTSLUtil.getMessage(locale,"o2c.email.notification.content.transfer.completed");
			}
			String appr1Quan = null;
			String appr2Quan = null;
			String appr3Quan = null;
			if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_channelTransferVO.getTransferSubType())) {
				if(p_channelTransferVO.getLevelOneApprovedQuantity()!=null)
					appr1Quan = PretupsBL.getDisplayAmount(Double.parseDouble(p_channelTransferVO.getLevelOneApprovedQuantity()));
				if(p_channelTransferVO.getLevelTwoApprovedQuantity()!=null)
					appr2Quan = PretupsBL.getDisplayAmount(Double.parseDouble(p_channelTransferVO.getLevelTwoApprovedQuantity()));
				if(p_channelTransferVO.getLevelThreeApprovedQuantity()!=null)
					appr3Quan = PretupsBL.getDisplayAmount(Double.parseDouble(p_channelTransferVO.getLevelThreeApprovedQuantity()));
			} else {
				appr1Quan = p_channelTransferVO.getLevelOneApprovedQuantity();
				appr2Quan = p_channelTransferVO.getLevelTwoApprovedQuantity();
				appr3Quan = p_channelTransferVO.getLevelThreeApprovedQuantity();
			}
			
			//For getting name and msisdn of initiator
            ArrayList arrayList = new ArrayList();
            ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
            arrayList = channelUserWebDAO.loadUserNameAndEmail(p_con, p_channelTransferVO.getCreatedBy());

            String message = null;
            
            message = notifyContent + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.channeluser.details") + "<br>" +
            		BTSLUtil.getMessage(locale,"o2c.email.transferid") + " " + p_channelTransferVO.getTransferID() + 
        			"<br>" + BTSLUtil.getMessage(locale,"o2c.email.channeluser.name") + " " + p_channelTransferVO.getToUserName() + 
        			"<br>" + BTSLUtil.getMessage(locale,"o2c.email.channeluser.msisdn") + " " + p_channelTransferVO.getUserMsisdn() +
        			"<br>" + BTSLUtil.getMessage(locale,"o2c.email.transfer.mrp") + " " + p_channelTransferVO.getTransferMRPAsString() +
        			"<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.req.amount")+" " + PretupsBL.getDisplayAmount(p_channelTransferVO.getRequestedQuantity());
            
            
            /*Message Content exclusively for transfer rejects
            	Showing only Fail Subject, TranferID, Reject User, Fail Remarks
            */
            
            if(p_subject.equalsIgnoreCase("o2c.email.notification.subject.failed"))
            {
            	message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.transfer.type") + " " +
            			p_channelTransferVO.getType() + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.initiator.name") + " " + arrayList.get(0) + 
            			"<br>" + BTSLUtil.getMessage(locale,"o2c.email.initiator.msisdn") + " " + arrayList.get(1) +
            			"<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.rejected.by") + 
            			userVO.getUserName() +
            			"<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.rejection.remarks");
            			if("three".equals(approvalLevel))	
            				message += p_channelTransferVO.getThirdApprovalRemark();
            	
            			else if("two".equals(approvalLevel))
            				message += p_channelTransferVO.getSecondApprovalRemark();
            	
            			else if("one".equals(approvalLevel))
            				message += p_channelTransferVO.getFirstApprovalRemark();        	
            }
 
            else{
			message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.net.payable.amount")+ " " + PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount());
			if (!BTSLUtil.isNullString(p_roleCode) && "APV2O2CTRF".equals(p_roleCode)) {
				message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.appr.one.quantity") + " " + appr1Quan;
			} else if (!BTSLUtil.isNullString(p_roleCode) && "APV3O2CTRF".equals(p_roleCode)) { 
				message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.appr.one.quantity") + " " + appr1Quan + 
				"<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.appr.two.quantity") + " " + appr2Quan;
			} else {
				if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
					message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.appr.quantity") + " " + appr1Quan;					
				} else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
					message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.appr.quantity") + " " + appr2Quan;	
				} else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(approvalLevel)) {
					message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.notification.content.appr.quantity") + " " + appr3Quan;
				}
			}
			
			message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.transfer.type") + " " + p_channelTransferVO.getType() + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.initiator.name") + " " + arrayList.get(0) + 
                    "<br>" + BTSLUtil.getMessage(locale,"o2c.email.initiator.msisdn") + " " + arrayList.get(1);
            }
            
			if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_channelTransferVO.getTransferSubType()))
            {
				  if(!(p_subject.equalsIgnoreCase("o2c.email.notification.subject.failed"))){
	                  String totalCommission = PretupsBL.getDisplayAmount(((ChannelTransferItemsVO)p_channelTransferVO.getChannelTransferitemsVOList().get(0)).getCommQuantity());
	                  message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.total.commission") + " " + totalCommission;
	                  if(PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
	                        message = message + "<br>" + BTSLUtil.getMessage(locale,"o2c.email.offline.settlement");
	                  }
				  }
				  
                  if(p_channelTransferVO.getChannelVoucherItemsVoList() != null)
                  {
                        for(int i=0 ;i < p_channelTransferVO.getChannelVoucherItemsVoList().size();i++)
                        {
                              if(((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getToSerialNum() != null && ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getFromSerialNum() != null)
                              {
                                    if(!isHeaderAdded) {
                                          isHeaderAdded = true;
                                          message1 = "<br>" + "<table><tr>"
                                        		  	  + " <td style='width: 5%;'>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.serialNumber") + "</td>"
                                                      + " <td style='width: 10%;'>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.denomination") + "</td>"
                                                      + " <td style='width: 10%;'>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.quantity") + "</td>"
                                                      + " <td style='width: 25%;'>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.fromSerialNo") + "</td>"
                                                      + " <td style='width: 25%;'>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.toSerialNo") + "</td>"
//                                                      + " <td style='width: 13%;'>"+ messages.getMessage(locale, "o2c.email.notification.product") + "</td>"
                                                      + " <td style='width: 12%;'>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.voucherType") + "</td>"
                                                      + "</tr>";
                                    }
                                    message1 = message1 + "<tr><td style='width: 5%;'>" +(i + 1) + "</td>" + 
                                                   "<td style='width: 10%;'>" +((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getTransferMrp() + "</td>" +  
                                                   "<td style='width: 10%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getRequiredQuantity() + "</td>" +  
                                                   "<td style='width: 25%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getFromSerialNum() + "</td>" + 
                                                   "<td style='width: 25%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getToSerialNum() + "</td>" +
//                                                   "<td style='width: 13%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getProductName() + "</td>" +
                                                   "<td style='width: 12%;'>" + new VomsProductDAO().getNameFromVoucherType(p_con,((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getVoucherType()) + "</td>" +
                                                   "</tr>";
                              }
                              else
                              {
                                    if(!isHeaderAdded) {
                                          isHeaderAdded = true;
                                          message1 = "<br>" + "<table><tr>"
                                                      + "   <td> S.No. </td>"
                                                      + "   <td>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.denomination") + "</td>"
                                                      + " <td>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.quantity") + "</td>"
                                                      + "<td>"+ BTSLUtil.getMessage(locale, "o2c.email.notification.voucherType") + "</td>"
                                                      + "</tr>";
                                    }
                                    message1 = message1 + "<tr><td>" +(i + 1) + "</td>" + 
                                                   "<td>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getTransferMrp() + "</td>" +  
                                                   "<td>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getRequiredQuantity() + "</td>" +
                                                   "<td>" + new VomsProductDAO().getNameFromVoucherType(p_con,((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getVoucherType()) + "</td>" +
                                                   "</tr>";
                              }
                        }
                        message = message + message1 + "</table>";
                  }
            }
			
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String to = "";
			if (!BTSLUtil.isNullString(p_roleCode)) {
				//subject = messages.getMessage(locale,"o2c.email.notification.content");
				subject = BTSLUtil.getMessage(locale,p_subject);
				to = p_channelTransferDAO.getEmailIdOfApprover(p_con, p_roleCode, p_channelTransferVO.getToUserID());
			} else {
				//subject = messages.getMessage(locale,"o2c.email.notification.subject.user");
				subject = BTSLUtil.getMessage(locale,p_subject);
				to = p_channelTransferVO.getEmail();
			}
			//subject =  messages.getMessage(locale,p_subject);

			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT",message );
			}
			// Send email
			EMailSender.sendMail(to, from, bcc, cc, subject, message, isAttachment, pathofFile, fileNameTobeDisplayed);
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
	
	
	
	/**
	 * @param masterErrorListMain
	 * @param locale
	 * @param reqData
	 * @return
	 */
	public static boolean validateRequestData(ArrayList<MasterErrorList> masterErrorListMain,Locale locale,HashMap<String, Object> reqData){
		Boolean error = false;
		
		if(!BTSLUtil.isNullString((String)reqData.get("refNumber")) && !BTSLUtil.isNumeric((String)reqData.get("refNumber"))){
			error = true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_REFNO_NOT_NUMERIC, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.O2C_REFNO_NOT_NUMERIC);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		
    	try{
			List<PaymentDetailsO2C> paymentDetails = (List<PaymentDetailsO2C>)reqData.get("paymentDetails");
			if(validatePaymentDetails(paymentDetails.get(0), masterErrorListMain))
				error= true;
    	}catch(BTSLBaseException be){
    		error = true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			masterErrorList.setErrorCode(be.getMessageKey());
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
    	}
     return error;
	}
	
	
	
	

	/**
	 * @param paymentDetails
	 * @param masterErrorListMain
	 * @throws BTSLBaseException
	 */
	public static boolean validatePaymentDetails(PaymentDetailsO2C paymentDetails,ArrayList<MasterErrorList> masterErrorListMain) throws BTSLBaseException
	{
		
    	boolean error = false;
		ArrayList<ListValueVO> instTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
        ArrayList<ListValueVO> paymentGatewayList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_GATEWAY_TYPE, true);
		Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		if(BTSLUtil.isNullString(paymentDetails.getPaymenttype())){
			error= true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
	    }
		else
		{
			boolean isPaymentTypeValid = false;
			for(ListValueVO lvo : instTypeList)
			{	if(lvo.getValue().equalsIgnoreCase(paymentDetails.getPaymenttype()))
				{	
					isPaymentTypeValid = true;
					break;
				}
			}
			if(!isPaymentTypeValid)
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		if(!BTSLUtil.isNullString(paymentDetails.getPaymenttype()) && (!(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(paymentDetails.getPaymenttype())) 
				&& !(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentDetails.getPaymenttype()))))
		{
			if(BTSLUtil.isNullString(paymentDetails.getPaymentinstnumber()))
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK, new String[]{"Payment Instrument Number"});
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		if(!BTSLUtil.isNullString(paymentDetails.getPaymenttype()) 
				&& PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentDetails.getPaymenttype()))
		{
			if(BTSLUtil.isNullString(paymentDetails.getPaymentgatewayType()))
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
			boolean isPaymentGatewayValid = false;
			for(ListValueVO lvo : paymentGatewayList)
			{	if(lvo.getValue().equalsIgnoreCase(paymentDetails.getPaymentgatewayType()))
				{	
					isPaymentGatewayValid = true;
					break;
				}
			}
			if(!isPaymentGatewayValid)
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_GATEWAY, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_GATEWAY);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		if(BTSLUtil.isNullString(paymentDetails.getPaymentdate()))
		{
			error= true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		else
		{
			if(!BTSLUtil.isValidDatePattern(paymentDetails.getPaymentdate()))
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DATE_FORMAT_INVALID, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.DATE_FORMAT_INVALID);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		
		return error;
	}


	
	


	


	
	
	}
