package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;

public class LastRechargeTransfer {

	private String className= "LastRechargeTransfer";

	private static Log log = LogFactory.getLog(LastRechargeTransfer.class.getName());
	public void processO2CTransaction(C2STransferVO c2STransferVO) throws BTSLBaseException {



		String methodName= "processO2CTransaction";

		Connection con = null;
		MComConnectionI mcomCon = null;

		try{
			mcomCon = new MComConnection();
			try {
				con = mcomCon.getConnection();
			} catch (SQLException e) {
				log.error(methodName, "SQLException " + e);
				log.errorTrace(methodName, e);
			}
			ChannelTransferVO channelTransferVO = new ChannelTransferVO();
			ChannelTransferItemsVO channelTrfItemsVO = new ChannelTransferItemsVO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			ChannelUserVO channelUserVO =  new ChannelUserVO();
			UserPhoneVO phoneVO = null;
			final UserDAO userDAO = new UserDAO();
			ProductVO productVO = new ProductVO();
			NetworkProductDAO networkProductDAO = new NetworkProductDAO(); 

			UserBalancesDAO userBalDAO = new UserBalancesDAO();
			channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
			channelTransferVO.setControlTransfer(PretupsI.YES);
			channelTrfItemsVO.setApprovedQuantity(c2STransferVO.getLRAmount());
			channelTrfItemsVO.setReceiverCreditQty(c2STransferVO.getLRAmount());
			Date currentDate = new Date();
			// validate the channel user
			final UserVO userVO = channelUserDAO.loadOptUserForO2C(con, c2STransferVO.getNetworkCode());
			channelUserVO= channelUserDAO.loadChannelUserDetails(con, c2STransferVO.getSenderMsisdn());
			productVO= networkProductDAO.loadProductDetailsOnUserDel(con, c2STransferVO.getProductCode(), null);
			
			final HashMap<Long, String> productMap = new HashMap<Long, String>();

			productMap.put(productVO.getProductShortCode(), productVO.getProductCode());

			ArrayList prdList = ChannelTransferBL.loadAndValidateProductsforLastRecharge(con, c2STransferVO, productMap, channelUserVO, true);

			prepareChannelTransferVO( channelTransferVO,  c2STransferVO,  currentDate,  channelUserVO,  prdList,  userVO);

			// generate transfer ID for the O2C transfer
			ChannelTransferBL.genrateTransferID(channelTransferVO);


			UserBalancesVO userBalanceVO = new UserBalancesVO();
			userBalanceVO.setUserID(c2STransferVO.getSenderID());
			userBalanceVO.setProductCode(c2STransferVO.getProductCode());
			userBalanceVO.setNetworkCode(c2STransferVO.getNetworkCode());
			userBalanceVO.setNetworkFor(c2STransferVO.getReceiverNetworkCode());
			userBalanceVO.setLastTransferID(c2STransferVO.getTransferID());
			userBalanceVO.setLastTransferOn(c2STransferVO.getTransferDate());
			userBalanceVO.setQuantityToBeUpdated(c2STransferVO.getLRAmount());
			userBalanceVO.setUserMSISDN(c2STransferVO.getSenderMsisdn());
			int updateCount = userBalDAO.updateUserDailyBalances(con, currentDate, userBalanceVO);
			if (updateCount < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}

			networkTransaction(con, channelTransferVO,c2STransferVO.getSenderID(), currentDate);

			final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

			// insert the channelTransferVO in the database

			int insertCount = 0;
			insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
			if (insertCount < 0) {
			     try {
			    	 mcomCon.finalRollback();
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		            }
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}


		     try {
	                if (con != null) {
	                	mcomCon.finalCommit();
	                }
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
			ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
			UserPhoneVO primaryPhoneVO = null;
				phoneVO = userDAO.loadUserAnyPhoneVO(con, c2STransferVO.getSenderMsisdn());
			if (phoneVO != null) {
				channelUserVO.setPrimaryMsisdn(channelTransferVO.getToUserCode());
				channelTransferVO.setToUserCode(c2STransferVO.getSenderMsisdn());
				primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, channelUserVO.getPrimaryMsisdn());

			}


			// sending message to receiver
			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			String smsKey = null;

			final ArrayList txnList = new ArrayList();
			final ArrayList balList = new ArrayList();
			KeyArgumentVO keyArgumentVO = null;




			final int lSize = itemsList.size();
			String args[] = { channelUserVO.getPrimaryMsisdn() };
			for (int i = 0; i < lSize; i++) {
				channelTrfItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				keyArgumentVO = new KeyArgumentVO();
				keyArgumentVO.setKey(PretupsErrorCodesI.O2C_DIRECT_TRANSFER_SUCCESS_TXNSUBKEY);
				args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()),  PretupsBL.getDisplayAmount(c2STransferVO.getLRAmount()) };
				keyArgumentVO.setArguments(args);
				txnList.add(keyArgumentVO);

				keyArgumentVO = new KeyArgumentVO();
				keyArgumentVO.setKey(PretupsErrorCodesI.O2C_DIRECT_TRANSFER_SUCCESS_BALSUBKEY);
				args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTrfItemsVO.getBalance() + c2STransferVO.getLRAmount()) };
				keyArgumentVO.setArguments(args);
				balList.add(keyArgumentVO);
			}// end of for
			if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE))) {
				final String[] msgArray = { BTSLUtil.getMessage(c2STransferVO.getLocale(), txnList), BTSLUtil.getMessage(c2STransferVO.getLocale(), balList), channelTransferVO
						.getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), "operator" };
				c2STransferVO.setMessageArguments(msgArray);
				smsKey = PretupsErrorCodesI.O2C_DIRECT_TRANSFER_RECEIVER;
			} 
			c2STransferVO.setMessageCode(smsKey);
			c2STransferVO.setTransferID(channelTransferVO.getTransferID());
			
			 String senderMessage = BTSLUtil.getMessage(c2STransferVO.getLocale(), c2STransferVO.getMessageCode(), c2STransferVO.getMessageArguments());
			 PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), senderMessage, c2STransferVO.getRequestID(), c2STransferVO
					.getRequestGatewayCode(), c2STransferVO.getLocale());
			pushMessage.push();

			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue())
			{
				if (primaryPhoneVO != null) {
					final Locale locale = new Locale(primaryPhoneVO.getPhoneLanguage(), primaryPhoneVO.getCountry());
					  senderMessage = BTSLUtil.getMessage(locale, c2STransferVO.getMessageCode(), c2STransferVO.getMessageArguments());
					  pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), senderMessage, c2STransferVO.getRequestID(), c2STransferVO
							.getRequestGatewayCode(), locale);
					pushMessage.push();
				}
			}  
		}

		catch (BTSLBaseException be) {

			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			log.error(methodName, "BTSLBaseException " + be.getMessage());
			log.errorTrace(methodName, be);

			return;
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("LastRechargeTransfer#processO2CTransaction");
				mcomCon = null;
			}
		}

	}

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


			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(con, channelTransferVO, true, null);
			} else {
				updateCount = channelUserDAO.creditUserBalances(con, channelTransferVO, true, null);
			}

			if (updateCount < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}



			updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(con, channelTransferVO, null, date);
			if (updateCount < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}
			updateCount2 = userTransferCountsDAO.updateUserTransferCountsforLR(con,channelTransferVO,userID);
			if (updateCount2 < 1) {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			} 
		} finally {
			LogFactory.printLog(methodName, "Exiting:", log);
		}

	}

	private void prepareChannelTransferVO(ChannelTransferVO channelTransferVO, C2STransferVO c2STransferVO, Date curDate, ChannelUserVO channelUserVO, ArrayList prdList, UserVO userVO) throws BTSLBaseException
	{
		String methodName="prepareChannelTransferVO";


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
		channelTransferVO.setCreatedOn(curDate);
		channelTransferVO.setCreatedBy(userVO.getUserID());
		channelTransferVO.setModifiedOn(curDate);
		channelTransferVO.setModifiedBy(userVO.getUserID());
		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		channelTransferVO.setTransferInitatedBy(userVO.getUserID());
		channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
		channelTransferVO.setSource(c2STransferVO.getSourceType());

		channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
		channelTransferVO.setReceiverDomainCode(channelUserVO.getDomainID());
		channelTransferVO.setToUserCode(channelUserVO.getUserCode());
		channelTransferVO.setRequestGatewayCode(c2STransferVO.getRequestGatewayCode());
		channelTransferVO.setRequestGatewayType(c2STransferVO.getRequestGatewayType());
		channelTransferVO.setActiveUserId(userVO.getUserID());

		ChannelTransferItemsVO channelTransferItemsVO = null;
		String productType = null;
		String productCode =  null;
	
		long totPayAmt = 0;

		long totTax1 = 0;
		long totTax2 = 0;
		long totTax3 = 0;
		long commissionQty = 0; 
	
		
		for (int i = 0, k = prdList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) prdList.get(i);
		

			totPayAmt += c2STransferVO.getLRAmount();
			totTax1 += channelTransferItemsVO.getTax1Value();
			totTax2 += channelTransferItemsVO.getTax2Value();
			totTax3 += channelTransferItemsVO.getTax3Value();

			productType = channelTransferItemsVO.getProductType();
			productCode =  channelTransferItemsVO.getProductCode();
			commissionQty += c2STransferVO.getLRAmount();
	
		
		}

		channelTransferVO.setPayableAmount(totPayAmt);
		channelTransferVO.setTotalTax1(totTax1);
		channelTransferVO.setTotalTax2(totTax2);
		channelTransferVO.setTotalTax3(totTax3);
		channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		channelTransferVO.setChannelTransferitemsVOList(prdList);
		channelTransferVO.setPayInstrumentAmt(c2STransferVO.getLRAmount());
		channelTransferVO.setProductType(productType);
		channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		channelTransferVO.setSenderDrQty(c2STransferVO.getLRAmount());
		channelTransferVO.setReceiverCrQty(c2STransferVO.getLRAmount());
		channelTransferVO.setRequestedQuantity(c2STransferVO.getLRAmount());
		channelTransferVO.setNetPayableAmount(c2STransferVO.getLRAmount());
		channelTransferVO.setProductCode(productCode);
		channelTransferVO.setProductCode(c2STransferVO.getProductCode());
		channelTransferVO.setTransactionMode(PretupsI.LR_TRANSACTION_MODE);
		channelTransferVO.setLRStatus(PretupsI.LAST_LR_PENDING_STATUS);
		channelTransferVO.setToUserName(channelUserVO.getUserName());
		channelTransferVO.setNetworkCode(c2STransferVO.getNetworkCode());
		channelTransferVO.setNetworkCodeFor(c2STransferVO.getReceiverNetworkCode());
		channelTransferVO.setTransferType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
		channelTransferVO.setRequestedQuantity(c2STransferVO.getLRAmount());
		channelTransferVO.setTransferMRP(c2STransferVO.getLRAmount());
		channelTransferVO.setStockUpdated(TypesI.YES);
		channelTransferVO.setChannelRemarks(PretupsI.LR_TRANSFER);
		channelTransferVO.setFirstApprovedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
		channelTransferVO.setFirstApprovedOn(channelTransferVO.getTransferDate());
		channelTransferVO.setFirstApproverLimit(channelTransferVO.getTransferMRP());
		channelTransferVO.setSecondApprovalLimit(channelTransferVO.getTransferMRP());
		channelTransferVO.setModifiedOn(channelTransferVO.getTransferDate());
		channelTransferVO.setSosStatus(PretupsI.LAST_LR_PENDING_STATUS);
		channelTransferVO.setLRFlag(true);


		LogFactory.printLog(methodName, "Exiting : ", log);
	}  
}
