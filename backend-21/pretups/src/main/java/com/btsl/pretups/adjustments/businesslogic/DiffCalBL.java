package com.btsl.pretups.adjustments.businesslogic;

/*
 * @(#)DiffCalBL.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Diff Calculation Class for Transfers
 */

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.logging.DiffCreditLog;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileMinCache;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOTFCountsVO;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.ibm.icu.util.Calendar;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

public class DiffCalBL {
	private static Log _log = LogFactory.getLog(DiffCalBL.class.getName());
	private Date _currentDate = null;
	private AdjustmentsDAO _adjustmentsDAO = null;
	private CommissionProfileDAO _commissionProfileDAO = null;
	private CommissionProfileTxnDAO _commissionProfileTxnDAO = null;
	private AdjustmentsVO _adjustmentVODebit = null;
	private AdjustmentsVO _adjustmentVOCredit = null;
	private String _adjustmentDebitID = null;
	private String _adjustmentCreditID = null;
	private long _requestedAmount = 0;
	private String _source = null;
	private UserBalancesVO _userBalancesVO = null;
	private boolean _creditBackEntryDone = false;
	private final ArrayList _itemsList = new ArrayList();
	private  static OperatorUtilI calculatorI = null;
	private static UserBalancesDAO _userBalancesDAO = new UserBalancesDAO();
	private AdjustmentsVO _adjustmentOwnerVODebit=null;
	private AdjustmentsVO _adjustmentOwnerVOCredit=null;
	private String _adjustmentOwnerDebitID=null;
	private String _adjustmentOwnerCreditID=null;
	private static final float EPSILON=0.0000001f;
	// calculate the tax
	static {
		String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("staticblock", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	public DiffCalBL() {
		_currentDate = new Date();
		_commissionProfileDAO = new CommissionProfileDAO();
		_commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		_adjustmentsDAO = new AdjustmentsDAO();
	}

	private AdditionalProfileDeatilsVO loadAdditionCommissionDetails(C2STransferVO c2sTransferVO, String commissionProfileSetID) throws BTSLBaseException {
		final String methodName = "differentialCalculations";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
		loggerValue.append("Entered commissionProfileSetID:");
		loggerValue.append(commissionProfileSetID);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}	
		AdditionalProfileDeatilsVO additionalProfileDetailsVO = null;
		try {
			CommissionProfileSetVO commissionProfileSetVO = (CommissionProfileSetVO) CommissionProfileCache.getObject(commissionProfileSetID, c2sTransferVO.getTransferDateTime());
			String commissionProfileVersion = commissionProfileSetVO.getCommProfileVersion();

			ArrayList commProfileList = (ArrayList) CommissionProfileCache.getObject(commissionProfileSetID, commissionProfileVersion);
			if (commProfileList == null) {
				CommissionProfileCache.loadCommissionProfilesDetails(commissionProfileSetID, commissionProfileVersion);
				commProfileList = (ArrayList) CommissionProfileCache.getObject(commissionProfileSetID, commissionProfileVersion);
			}

			for (Object additionalProfileSlabVO : commProfileList) {
				if (additionalProfileSlabVO instanceof AdditionalProfileDeatilsVO) {

					if (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getStartRange() <= _requestedAmount && ((AdditionalProfileDeatilsVO) additionalProfileSlabVO)
							.getEndRange() >= _requestedAmount && ((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType().equals(c2sTransferVO.getServiceType())
									&& (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getSubServiceCode() == null||((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getSubServiceCode().equals(c2sTransferVO.getSubService()))) {

						if (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getGatewayCode().equals(c2sTransferVO.getRequestGatewayCode()) ||
								PretupsI.ALL.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getGatewayCode())){

							additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
						}
					}
				}

			}

			Date tempDate=_currentDate;
			tempDate = DateUtils.truncate(tempDate, Calendar.DATE);
			if(additionalProfileDetailsVO != null){
				if(!BTSLUtil.isNullString(additionalProfileDetailsVO.getApplicableFromAdditional())){		    		

					if(!(BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableFromAdditional()).before(tempDate) 
							|| BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableFromAdditional()).equals(tempDate))){  
						additionalProfileDetailsVO = null;
					}
				}

				if(additionalProfileDetailsVO != null && !BTSLUtil.isNullString(additionalProfileDetailsVO.getApplicableToAdditional())){

					if(!((BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableToAdditional()).after(tempDate) 
							|| BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableToAdditional()).equals(tempDate)))){
						additionalProfileDetailsVO = null;
					}
				}

			}
			if (additionalProfileDetailsVO != null) {
				if (!((additionalProfileDetailsVO.getGatewayCode().equals(c2sTransferVO.getRequestGatewayCode()) || PretupsI.ALL.equals(additionalProfileDetailsVO
						.getGatewayCode())) && BTSLUtil.timeRangeValidation(additionalProfileDetailsVO.getAdditionalCommissionTimeSlab(), new Date()))) {
					additionalProfileDetailsVO = null;
				}
			} else {
				additionalProfileDetailsVO = null;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception Transfer ID:");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
			loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "differentialCalculations", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
		}
		return additionalProfileDetailsVO;
	}
	
	/**
	 * Method to calculate the Differential Calculation for the request
	 * 
	 * @param c2sTransferVO
	 * @param p_module
	 * @throws BTSLBaseException
	 */
	public void differentialCalculations(C2STransferVO c2sTransferVO, String p_module) throws BTSLBaseException {
		final String methodName = "differentialCalculations";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_module:");
    	loggerValue.append(p_module + c2sTransferVO.getSubService());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ChannelUserVO channelUserVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String otherInfo = null;
		StringBuilder otherInfoBuilder=new StringBuilder();
		ArrayList commProfileList = null;
		AdditionalProfileDeatilsVO additionalProfileDetailsVO = null;




		try {
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			_source = c2sTransferVO.getSourceType();
			_requestedAmount = c2sTransferVO.getRequestedAmount();
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			String commissionProfileSetID = channelUserVO.getCommissionProfileSetID();
			// Commission profile cache by akanksha
			CommissionProfileSetVO commissionProfileSetVO = (CommissionProfileSetVO) CommissionProfileCache.getObject(commissionProfileSetID, _currentDate);
			String commissionProfileVersion = commissionProfileSetVO.getCommProfileVersion();

			commProfileList = (ArrayList) CommissionProfileCache.getObject(commissionProfileSetID, commissionProfileVersion);
			if (commProfileList == null) {
				CommissionProfileCache.loadCommissionProfilesDetails(commissionProfileSetID, commissionProfileVersion);
				commProfileList = (ArrayList) CommissionProfileCache.getObject(commissionProfileSetID, commissionProfileVersion);
			}
			for (Object additionalProfileSlabVO : commProfileList) {
                if (additionalProfileSlabVO instanceof AdditionalProfileDeatilsVO) {

					if (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getStartRange() <= _requestedAmount && ((AdditionalProfileDeatilsVO) additionalProfileSlabVO)
                                  .getEndRange() >= _requestedAmount && ((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType().equals(c2sTransferVO.getServiceType()) && (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getSubServiceCode() == null || ((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getSubServiceCode().equals(c2sTransferVO.getSubService()))) {
                            if (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getGatewayCode().equals(c2sTransferVO.getRequestGatewayCode()) ||
                                        PretupsI.ALL.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getGatewayCode())){

                                  additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
                            }
                      }
                }

          }


			Date tempDate=_currentDate;
			tempDate = DateUtils.truncate(tempDate, Calendar.DATE);
			if(additionalProfileDetailsVO != null){
				if(!BTSLUtil.isNullString(additionalProfileDetailsVO.getApplicableFromAdditional())){		    		

					if(!(BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableFromAdditional()).before(tempDate) 
							|| BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableFromAdditional()).equals(tempDate))){  
						additionalProfileDetailsVO = null;
					}
				}

				if(additionalProfileDetailsVO != null && !BTSLUtil.isNullString(additionalProfileDetailsVO.getApplicableToAdditional())){

					if(!((BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableToAdditional()).after(tempDate) 
							|| BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableToAdditional()).equals(tempDate)))){
						additionalProfileDetailsVO = null;
					}
				}

			}
			if (additionalProfileDetailsVO != null) {
				if (!((additionalProfileDetailsVO.getGatewayCode().equals(c2sTransferVO.getRequestGatewayCode()) || PretupsI.ALL.equals(additionalProfileDetailsVO
						.getGatewayCode())) && BTSLUtil.timeRangeValidation(additionalProfileDetailsVO.getAdditionalCommissionTimeSlab(), new Date()))) {
					additionalProfileDetailsVO = null;
				}
			} else {
				additionalProfileDetailsVO = null;
			}

			// cache
			// AdditionalProfileDeatilsVO
			String _senderNetworkCode = c2sTransferVO.getSenderNetworkCode();
			if (_senderNetworkCode == null)
			{
				_senderNetworkCode=c2sTransferVO.getNetworkCode();
			}
			String _receiverNetworkCode = c2sTransferVO.getReceiverNetworkCode();
			if (additionalProfileDetailsVO != null) {
				if ((_senderNetworkCode.equals(_receiverNetworkCode)) || ((!_senderNetworkCode.equals(_receiverNetworkCode)) && (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM))).booleanValue()))) {
					c2sTransferVO.setDifferentialApplicable(PretupsI.YES);
				}
				calculateDifferential(con, c2sTransferVO, additionalProfileDetailsVO);

				try {
					mcomCon.finalCommit();
					// Set message only if Adjustment value is greater than 0
					if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getGiveOnlineDifferential()) && _adjustmentVOCredit.getTransferValue() > 0) {
						if(!BTSLUtil.isNullString(c2sTransferVO.getSubscriberSID())){
							String[] messageArgArray = { _adjustmentCreditID, c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), c2sTransferVO.getSubscriberSID(), c2sTransferVO.getTransferValueStr() };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
									PretupsErrorCodesI.C2S_ADJUSTMENT_SUCCESS, messageArgArray));
						}else{
							String[] messageArgArray = { _adjustmentCreditID, c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), c2sTransferVO.getReceiverMsisdn(), c2sTransferVO.getTransferValueStr() };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
									PretupsErrorCodesI.C2S_ADJUSTMENT_SUCCESS, messageArgArray));
						}                    	
					}
					otherInfoBuilder.append("Addition Commission=");
					otherInfoBuilder.append(_adjustmentVOCredit.getTransferValue());
					otherInfoBuilder.append(" ID=");
					otherInfoBuilder.append(_adjustmentCreditID);
					otherInfoBuilder.append(" Stock Updated=");
					otherInfoBuilder.append(_adjustmentVOCredit.getStockUpdated());
					otherInfo=otherInfoBuilder.toString();
					// Log the details of the differential
					if (_creditBackEntryDone) {
						BalanceLogger.log(_userBalancesVO);
					}
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					loggerValue.setLength(0);
			    	loggerValue.append("Exception Transfer ID:");
			    	loggerValue.append(c2sTransferVO.getTransferID());
			    	loggerValue.append(" Exception:");
			    	loggerValue.append(e.getMessage());
					_log.error(methodName,loggerValue);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]",
							c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
				}
			} else {
				// if additioal commission is applicable then only if condition
				// will true

				c2sTransferVO.setDifferentialApplicable(PretupsI.NO);
				c2sTransferVO.setDifferentialGiven(PretupsI.NO);
				otherInfo = "No additional taxes found";
				loggerValue.setLength(0);
		    	loggerValue.append("No additional and Roam taxes found for transfer ID=");
		    	loggerValue.append(c2sTransferVO.getTransferID());
				_log.debug(methodName,loggerValue);
			}
		} catch (BTSLBaseException be) {
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e1) {
				_log.errorTrace(methodName, e1);
			}
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e1) {
				_log.errorTrace(methodName, e1);
			}
			loggerValue.setLength(0);
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "differentialCalculations", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("DiffCalBL#differentialCalculations");
				mcomCon = null;
			}
			DiffCreditLog.log(c2sTransferVO, otherInfo);
		}
	}

	/**
	 * Method to give the differential for reconcilation operations . Only
	 * difference between this and above one is
	 * that this method is passes with a connection to it. Rest of logic is
	 * same.
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_module
	 * @throws BTSLBaseException
	 */
	public void differentialCalculationsForRecon(Connection con, C2STransferVO c2sTransferVO, String p_module) throws BTSLBaseException {
		final String methodName = "differentialCalculationsForRecon";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_module:");
    	loggerValue.append(p_module);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ChannelUserVO channelUserVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
		String otherInfo = null;
		StringBuilder otherInfoBuilder=new StringBuilder();	
		try {
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			_source = c2sTransferVO.getSourceType();
			String commissionProfileSetID = channelUserVO.getCommissionProfileSetID();
			// set requested amount by the transfer value since there is no
			// information of the requested amount form
			// the database
			c2sTransferVO.setRequestedAmount(c2sTransferVO.getTransferValue());
			_requestedAmount = c2sTransferVO.getRequestedAmount();

			/*AdditionalProfileDeatilsVO additionalProfileDetailsVO = _commissionProfileTxnDAO.loadAdditionCommissionDetails(con, c2sTransferVO.getTransferID(),
					commissionProfileSetID, c2sTransferVO.getRequestedAmount(), c2sTransferVO.getTransferDateTime(), c2sTransferVO.getServiceType(), c2sTransferVO
					.getSubService(), c2sTransferVO.getRequestGatewayCode());*/
			AdditionalProfileDeatilsVO additionalProfileDetailsVO = loadAdditionCommissionDetails( c2sTransferVO, commissionProfileSetID);
			if (additionalProfileDetailsVO != null) {
				c2sTransferVO.setDifferentialApplicable(PretupsI.YES);
				calculateDifferential(con, c2sTransferVO, additionalProfileDetailsVO);
				try {

					// Set message only if Adjustment value is greater than 0
					if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getGiveOnlineDifferential()) && _adjustmentVOCredit.getTransferValue() > 0) {
						
						if(c2sTransferVO.getSubscriberSID()!=null){
						String[] messageArgArray = { c2sTransferVO.getTransferID(), BTSLUtil.getDateStringFromDate(c2sTransferVO.getTransferDate()), c2sTransferVO
								.getSubscriberSID(), PretupsBL.getDisplayAmount(_requestedAmount), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
								.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), _adjustmentCreditID };
						c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
								PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_MSG3, messageArgArray));
						}
						else
						{
							String[] messageArgArray = { c2sTransferVO.getTransferID(), BTSLUtil.getDateStringFromDate(c2sTransferVO.getTransferDate()), c2sTransferVO
									.getReceiverMsisdn(), PretupsBL.getDisplayAmount(_requestedAmount), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), _adjustmentCreditID };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
									PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_MSG3, messageArgArray));
						}
						
					}

					c2sTransferVO.setOtherInfo2(_userBalancesVO);
					otherInfoBuilder.setLength(0);
					otherInfoBuilder.append("Addition Commission=");
					otherInfoBuilder.append(_adjustmentVOCredit.getTransferValue());
					otherInfoBuilder.append(" ID=");
					otherInfoBuilder.append(_adjustmentCreditID);
					otherInfoBuilder.append(" Stock Updated=");
					otherInfoBuilder.append(_adjustmentVOCredit.getStockUpdated());
					otherInfo=otherInfoBuilder.toString();

				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					_log.error(methodName, "Exception Transfer ID:" + c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculationsForRecon]",
							c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
				}
			} else {
				c2sTransferVO.setDifferentialApplicable(PretupsI.NO);
				c2sTransferVO.setDifferentialGiven(PretupsI.NO);
				otherInfo = "No additional taxes found";
				loggerValue.setLength(0);
		    	loggerValue.append("No additional taxes found for transfer ID=");
		    	loggerValue.append(c2sTransferVO.getTransferID());
				_log.debug(methodName,loggerValue);
			}
			
			if(c2sTransferVO.getPromoBonus()>0)
			{
			    new PromoBonusCalBL(con).promoBonusAdjustment(c2sTransferVO, PretupsI.C2S_MODULE);
				if(!BTSLUtil.isNullString(c2sTransferVO.getSenderReturnPromoMessage()))
				{
					new PushMessage(c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderReturnPromoMessage(), c2sTransferVO.getTransferID(),c2sTransferVO.getRequestGatewayCode(), (((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale()).push();
				}
				
				
			}
			
		} catch (BTSLBaseException be) {
			otherInfo = "Not able to calculate Differential for transfer ID=" + c2sTransferVO.getTransferID();
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			otherInfo = "Not able to calculate Differential for transfer ID=" + c2sTransferVO.getTransferID();
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
	    	loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue );
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculationsForRecon]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "differentialCalculationsForRecon", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
			// Log the information in Diff Credit Log
			DiffCreditLog.log(c2sTransferVO, otherInfo);
		}
	}

	/**
	 * Method to calculate Differential and controls the flow of process
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	private void calculateDifferential(Connection con, C2STransferVO c2sTransferVO, AdditionalProfileDeatilsVO additionalProfileDetailsVO) throws BTSLBaseException {
		final String methodName = "calculateDifferential";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered c2sTransferVO:");
    	loggerValue.append(c2sTransferVO);
		loggerValue.append("p_additionalProfileDetails:");
    	loggerValue.append(additionalProfileDetailsVO);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		try {

			UserOTFCountsVO userOTFCountsVO;
			CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
			List<AdditionalProfileDeatilsVO> otfSlabList;
		
			DateFormat df = new SimpleDateFormat(PretupsI.DATE_FORMAT);
			Date dateobj = new Date();
			Date tempDate = df.parse(df.format(dateobj));
			AdditionalProfileDeatilsVO otfDetailsVO = null;


			populateAdjustmentDebit(c2sTransferVO, additionalProfileDetailsVO);
			populateAdjustmentCredit(c2sTransferVO, additionalProfileDetailsVO);


			additionalProfileDetailsVO.setSequenceNo(_commissionProfileDAO.loadsequenceNo(con, ((ChannelUserVO)c2sTransferVO.getSenderVO()).getCategoryCode()));

			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()  && !additionalProfileDetailsVO.getSequenceNo().equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))) {
				populateAdjustmentOwnerDebit(con, c2sTransferVO,additionalProfileDetailsVO);
				populateAdjustmentOwnerCredit(con, c2sTransferVO,additionalProfileDetailsVO);
			}
			String senderNetworkCode = c2sTransferVO.getSenderNetworkCode();
			if(senderNetworkCode == null)
			{
				senderNetworkCode=c2sTransferVO.getNetworkCode();
			}
			String receiverNetworkCode = c2sTransferVO.getReceiverNetworkCode();
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				long amountAdnlComToBeCalculated = 0;
				for (Iterator<UserProductWalletMappingVO> iterator = c2sTransferVO.getPdaWalletList().iterator(); iterator.hasNext();) {
					UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();

					if (PretupsI.YES.equalsIgnoreCase(userProductWalletMappingVO.getAddnlComAlwd())) {
						if (userProductWalletMappingVO.getDebitBalance() > 0) {
							amountAdnlComToBeCalculated = amountAdnlComToBeCalculated + userProductWalletMappingVO.getDebitBalance();
						}
					}
				}
				if ((!senderNetworkCode.equals(receiverNetworkCode)) && (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM))).booleanValue())) {
					calculateAdditionalRoamCommission(additionalProfileDetailsVO, amountAdnlComToBeCalculated);
					c2sTransferVO.setTotalCommission(_adjustmentVOCredit.getTransferValue());
				} else {
					calculateAdditionalCommission(additionalProfileDetailsVO, amountAdnlComToBeCalculated);
					c2sTransferVO.setTotalCommission(_adjustmentVOCredit.getTransferValue());
				}
			} else {
				if ((!senderNetworkCode.equals(receiverNetworkCode)) && (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM))).booleanValue())) {
					calculateAdditionalRoamCommission(additionalProfileDetailsVO, c2sTransferVO.getRequestedAmount());
					c2sTransferVO.setTotalCommission(_adjustmentVOCredit.getTransferValue());
				} else {
					calculateAdditionalCommission(additionalProfileDetailsVO, c2sTransferVO.getRequestedAmount());
					c2sTransferVO.setTotalCommission(_adjustmentVOCredit.getTransferValue());
				}

			}



			if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()))
			{
				calculateUserOTFCounts(con, c2sTransferVO,additionalProfileDetailsVO);
			}


			if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getGiveOnlineDifferential()) ) {
				boolean validExtUserId = Arrays.asList(Constants.getProperty("ONLINE_SETTLE_EXTUSRID_LIST").split(",")).contains(c2sTransferVO.getSenderID());
				boolean validServiceName = Arrays.asList(Constants.getProperty("ONLINE_SETTLE_SERVICES_LIST").split(",")).contains(c2sTransferVO.getServiceType());
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OFFLINE_SETTLE_EXTUSR))).booleanValue() && PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(c2sTransferVO.getRequestGatewayType()) 
						&& validExtUserId && validServiceName){
					_adjustmentVOCredit.setStockUpdated(PretupsI.NO);
					c2sTransferVO.setDifferentialGiven(PretupsI.YES);
					if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()  && !additionalProfileDetailsVO.getSequenceNo().equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))) {
						_adjustmentOwnerVODebit.setStockUpdated(PretupsI.NO);
						_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.NO);
					}
				}else{
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
						creditUserBalanceToWallet(con, _adjustmentVOCredit, c2sTransferVO.getCategoryCode(), c2sTransferVO);
					} else {
						creditUserBalanceForProduct(con, _adjustmentVOCredit, c2sTransferVO.getCategoryCode());
						if(_adjustmentVOCredit.getPostBalance()>0)
							c2sTransferVO.setSenderPostBalance(_adjustmentVOCredit.getPostBalance());
						C2STransferItemVO reconcileVO= (C2STransferItemVO)c2sTransferVO.getTransferItemList().get(0);
						reconcileVO.setPreviousBalance(_adjustmentVOCredit.getPreviousBalance());
						reconcileVO.setPostBalance(_adjustmentVOCredit.getPostBalance());
						c2sTransferVO.getTransferItemList().set(0, reconcileVO);
					}
					try {
						if(c2sTransferVO.getRequestVO()!=null && (ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()!=null && (!((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerID().equals(c2sTransferVO.getSenderID())) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER).equals(additionalProfileDetailsVO.getSequenceNo())){
							debitAndCreditOwnerBalanceForProduct(con,_adjustmentVOCredit, _adjustmentOwnerVOCredit);
							_adjustmentOwnerVODebit.setStockUpdated(PretupsI.NO);
							_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.YES);
						}
					} catch (Exception e) {
						_log.errorTrace(methodName,e);
					}
					_adjustmentVOCredit.setStockUpdated(PretupsI.YES);
					c2sTransferVO.setDifferentialGiven(PretupsI.YES);				
				}
			} else {
				_adjustmentVOCredit.setStockUpdated(PretupsI.NO);
				c2sTransferVO.setDifferentialGiven(PretupsI.NO);
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()  && !additionalProfileDetailsVO.getSequenceNo().equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))) {
					_adjustmentOwnerVODebit.setStockUpdated(PretupsI.NO);
					_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.NO);
				}
			}

			//added for owner commision
			try {
				if((!((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerID().equals(c2sTransferVO.getSenderID())) && (additionalProfileDetailsVO.getAddOwnerCommRate() > 0 || additionalProfileDetailsVO.getAddOwnerCommRate() < 0) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !additionalProfileDetailsVO.getSequenceNo().equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER)))
				{
					_itemsList.add(_adjustmentOwnerVOCredit);
					_itemsList.add(_adjustmentOwnerVODebit);
				}
			} catch (Exception e) {
				_log.errorTrace(methodName,e);
			}


			_adjustmentVOCredit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
			_itemsList.add(_adjustmentVODebit);
			_itemsList.add(_adjustmentVOCredit);

			int addCount = _adjustmentsDAO.addAdjustmentEntries(con, _itemsList, c2sTransferVO.getTransferID());
			if(addCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()) && c2sTransferVO.getUserOTFCountsVO() != null)
			{
				int updateCount1=new UserTransferCountsDAO().updateUserOTFCounts(con, c2sTransferVO.getUserOTFCountsVO());
				if (updateCount1 <= 0) {
					throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION);
				}
			
				/*if(addCount>0 && _adjustmentVOCredit!= null && _adjustmentVOCredit.getOtfAmount() > 0 && c2sTransferVO.isTargetAchieved())
				{
					ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
					channelUserVO.setCommissionProfileSetID(((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getCommissionProfileSetID());  
					channelUserVO.setUserID(((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getUserID());

					//Message handelling for OTF
					TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
					tbcm.loadAddnlCommissionProfileDetailsForTargetMessages(con,channelUserVO,_adjustmentVOCredit);
				}*/
			}


			if (addCount <= 0) {
				throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION);
			} else if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && (additionalProfileDetailsVO.getAddOwnerCommRate() > 0 || additionalProfileDetailsVO.getAddOwnerCommRate() < 0) && !additionalProfileDetailsVO.getSequenceNo().equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))){
				String flag=Constants.getProperty("OWNER_COMMISSION_MESSAGE_ALLOWED");
				if(flag.equals(PretupsI.STATUS_ACTIVE)){
					String arr[]=new String[4];
					double a=(_adjustmentOwnerVOCredit.getTransferValue())/100.00;
					arr[0]=""+a ;
					arr[1]=""+PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount());
					arr[2]=c2sTransferVO.getTransferID();
					arr[3]=""+PretupsBL.getDisplayAmount(_adjustmentOwnerVOCredit.getPostBalance());
					Locale locale =(((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale();
					String senderMessage=BTSLUtil.getMessage(locale,PretupsErrorCodesI.SMS_DIFFCAL_SUCCESS,arr);
					String _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(c2sTransferVO.getRequestGatewayCode(),c2sTransferVO.getNetworkCode());
					PushMessage pushMessage=new PushMessage(_adjustmentOwnerVOCredit.getUserMSISDN(),senderMessage,null,null,locale);
					pushMessage.push(_lowBalRequestCode,null);
				}
			}

		}
		catch(BTSLBaseException be)
		{
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			throw new BTSLBaseException(be);
		}
		catch(Exception e)
		{
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			_log.errorTrace(methodName ,e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			
		}
	}

	/**
	 * Populates the Adjustment VO for Debit entry
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	private void populateAdjustmentDebit(C2STransferVO c2sTransferVO,AdditionalProfileDeatilsVO additionalProfileDetailsVO) throws BTSLBaseException
	{	
		final String methodName = "populateAdjustmentDebit";	
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered Transfer ID:");
    	loggerValue.append(c2sTransferVO.getTransferID());
		loggerValue.append(" AddCommProfileDetailID=");
    	loggerValue.append(additionalProfileDetailsVO.getAddCommProfileDetailID());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		try
		{
			_adjustmentDebitID=c2sTransferVO.getTransferID()+PretupsI.SUFIX_ADJUST_TXN_ID_NW;
			_adjustmentVODebit=new AdjustmentsVO();
			_adjustmentVODebit.setAdjustmentID(_adjustmentDebitID);
			_adjustmentVODebit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVODebit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setCreatedOn(_currentDate);
			_adjustmentVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setModifiedOn(_currentDate);
			_adjustmentVODebit.setDifferentialFactor(additionalProfileDetailsVO.getDiffrentialFactor());
			_adjustmentVODebit.setEntryType(PretupsI.DEBIT);
			_adjustmentVODebit.setMarginRate(additionalProfileDetailsVO.getAddCommRate());
			_adjustmentVODebit.setMarginType(additionalProfileDetailsVO.getAddCommType());
			_adjustmentVODebit.setModule(c2sTransferVO.getModule());
			_adjustmentVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());
			//			Add to log user MSISDN on 20/02/2008
			_adjustmentVODebit.setUserMSISDN(c2sTransferVO.getReceiverMsisdn());

			//		Roam Recharge CR 000012
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());

			_adjustmentVODebit.setStockUpdated(PretupsI.NO);
			_adjustmentVODebit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVODebit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVODebit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVODebit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			_adjustmentVODebit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			_adjustmentVODebit.setTax1Rate(additionalProfileDetailsVO.getTax1Rate());
			_adjustmentVODebit.setTax1Type(additionalProfileDetailsVO.getTax1Type());
			_adjustmentVODebit.setTax2Rate(additionalProfileDetailsVO.getTax2Rate());
			_adjustmentVODebit.setTax2Type(additionalProfileDetailsVO.getTax2Type());
			_adjustmentVODebit.setAddnlCommProfileDetailID(additionalProfileDetailsVO.getAddCommProfileDetailID());
			_adjustmentVODebit.setSubService(c2sTransferVO.getSubService());
			_adjustmentVODebit.setCommisssionType(PretupsI.NORMAL_COMMISSION);

		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
			loggerValue.setLength(0);
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentDebit]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentDebit",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	/**
	 * Populates the Adjustment VO for Credit entry
	 * 
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	private void populateAdjustmentCredit(C2STransferVO c2sTransferVO, AdditionalProfileDeatilsVO additionalProfileDetailsVO) throws BTSLBaseException {
		final String methodName = "populateAdjustmentCredit";
		try {
			_adjustmentCreditID = c2sTransferVO.getTransferID() + PretupsI.SUFIX_ADJUST_TXN_ID_USER;
			_adjustmentVOCredit = new AdjustmentsVO();
			_adjustmentVOCredit.setAdjustmentID(_adjustmentCreditID);
			_adjustmentVOCredit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setCreatedOn(_currentDate);
			_adjustmentVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setModifiedOn(_currentDate);
			_adjustmentVOCredit.setDifferentialFactor(additionalProfileDetailsVO.getDiffrentialFactor());
			_adjustmentVOCredit.setEntryType(PretupsI.CREDIT);
			_adjustmentVOCredit.setMarginRate(additionalProfileDetailsVO.getAddCommRate());
			_adjustmentVOCredit.setMarginType(additionalProfileDetailsVO.getAddCommType());
			_adjustmentVOCredit.setModule(c2sTransferVO.getModule());
			_adjustmentVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());
			// Add to log user MSISDN on 20/02/2008
			_adjustmentVOCredit.setUserMSISDN(c2sTransferVO.getSenderMsisdn());
			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			} else {
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			}

			_adjustmentVOCredit.setStockUpdated(PretupsI.NO);
			_adjustmentVOCredit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVOCredit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVOCredit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVOCredit.setUserID(c2sTransferVO.getSenderID());
			_adjustmentVOCredit.setUserCategory(((ChannelUserVO) c2sTransferVO.getSenderVO()).getCategoryCode());
			_adjustmentVOCredit.setTax1Rate(additionalProfileDetailsVO.getTax1Rate());
			_adjustmentVOCredit.setTax1Type(additionalProfileDetailsVO.getTax1Type());
			_adjustmentVOCredit.setTax2Rate(additionalProfileDetailsVO.getTax2Rate());
			_adjustmentVOCredit.setTax2Type(additionalProfileDetailsVO.getTax2Type());
			_adjustmentVOCredit.setAddnlCommProfileDetailID(additionalProfileDetailsVO.getAddCommProfileDetailID());
			_adjustmentVOCredit.setSubService(c2sTransferVO.getSubService());
			_adjustmentVOCredit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
     


		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			_log.error(methodName, "Exception Transfer ID:" + c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[populateAdjustmentCredit]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "populateAdjustmentCredit", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	/**
	 * Calculates the value to be transferred to the user after taxes etc
	 * 
	 * @param additionalProfileDetailsVO
	 * @param p_requestAmount
	 */
	private void calculateAdditionalCommission(AdditionalProfileDeatilsVO additionalProfileDetailsVO, long p_requestAmount) throws BTSLBaseException {
		final String methodName = "calculateAdditionalCommission";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered with p_requestAmount :");
    	loggerValue.append(p_requestAmount);
		loggerValue.append(",additionalProfileDetailsVO : ");
    	loggerValue.append(additionalProfileDetailsVO.getAddCommType());
    	loggerValue.append(",");
    	loggerValue.append(additionalProfileDetailsVO.getAddOwnerCommType());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		try {
			long amountTemp = 0;
			long calculatedTax1Value = 0;
			long calculatedTax2Value = 0;
			long transferValue = 0;
			//added for owner commision
			long ownerTransferValue=0;
			amountTemp = calculatorI.calculateDifferentialComm(additionalProfileDetailsVO.getAddCommType(), additionalProfileDetailsVO.getAddCommRate(), p_requestAmount);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, " margin amt :" + amountTemp);
			}

			if (Math.abs(additionalProfileDetailsVO.getDiffrentialFactor()-0)<EPSILON) {
				_log.error(methodName, " Differential factor: multipleFactor:" + additionalProfileDetailsVO.getDiffrentialFactor());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[calculateAdditionalCommission]", "", "",
						"", "Differential factor: multipleFactor cannot be zero");
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERR_DIFF_FACTOR_CANNOT_BE_ZERO);
			}
			
			// cast issue fix
			// long afterMultipleFact = (long) (amountTemp * additionalProfileDetailsVO.getDiffrentialFactor());
			long afterMultipleFact = BTSLUtil.parseDoubleToLong(amountTemp * additionalProfileDetailsVO.getDiffrentialFactor());

			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
		    	loggerValue.append("afterMultipleFact :");
		    	loggerValue.append(afterMultipleFact);
				_log.debug(methodName,loggerValue);
			}
			if (PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(additionalProfileDetailsVO.getAddCommType())) {

				_adjustmentVOCredit.setMarginRate(Double.parseDouble(PretupsBL.getDisplayAmount(additionalProfileDetailsVO.getAddCommRate())));
				_adjustmentVODebit.setMarginRate(Double.parseDouble(PretupsBL.getDisplayAmount(additionalProfileDetailsVO.getAddCommRate())));

			}
			else
			{
				_adjustmentVOCredit.setMarginRate(additionalProfileDetailsVO.getAddCommRate());
				_adjustmentVODebit.setMarginRate(additionalProfileDetailsVO.getAddCommRate());

			}
			_adjustmentVOCredit.setMarginAmount(afterMultipleFact);
			_adjustmentVODebit.setMarginAmount(afterMultipleFact);

			calculatedTax1Value = calculatorI.calculateDifferentialTax1(additionalProfileDetailsVO.getTax1Type(), additionalProfileDetailsVO.getTax1Rate(), afterMultipleFact, p_requestAmount);

			_adjustmentVODebit.setTax1Value(calculatedTax1Value);
			_adjustmentVOCredit.setTax1Value(calculatedTax1Value);

			if (additionalProfileDetailsVO.getTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
				// calculatedTax2Value = (long) additionalProfileDetailsVO.getTax2Rate();
				calculatedTax2Value = BTSLUtil.parseDoubleToLong(additionalProfileDetailsVO.getTax2Rate());
			} else // If percentage
			{
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
					calculatedTax2Value = calculatorI.calculateDifferentialTax2(additionalProfileDetailsVO.getTax2Type(), additionalProfileDetailsVO.getTax2Rate(),
							calculatedTax1Value);
				} else {
					calculatedTax2Value = calculatorI.calculateDifferentialTax2(additionalProfileDetailsVO.getTax2Type(), additionalProfileDetailsVO.getTax2Rate(),
							afterMultipleFact);
				}
			}
			_adjustmentVODebit.setTax2Value(calculatedTax2Value);
			_adjustmentVOCredit.setTax2Value(calculatedTax2Value);

			transferValue = calculatorI.calculateDifferentialTransferValue(p_requestAmount, afterMultipleFact, calculatedTax1Value, calculatedTax2Value);

			_adjustmentVOCredit.setTransferValue(transferValue);
			_adjustmentVODebit.setTransferValue(transferValue);
			//For additional commission through owner--Start						
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !BTSLUtil.isNullString(additionalProfileDetailsVO.getSequenceNo()) && !additionalProfileDetailsVO.getSequenceNo().equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER)))
			{
				if(BTSLUtil.isNullString(additionalProfileDetailsVO.getAddOwnerCommType())) {
					additionalProfileDetailsVO.setAddOwnerCommType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
				}
				amountTemp=calculatorI.calculateDifferentialComm(additionalProfileDetailsVO.getAddOwnerCommType(),additionalProfileDetailsVO.getAddOwnerCommRate(),p_requestAmount);

				if(_log.isDebugEnabled()) {
					loggerValue.setLength(0);
			    	loggerValue.append(" owner margin amt :");
			    	loggerValue.append(amountTemp);
					_log.debug(methodName,loggerValue);
				}

				// long afterMultipleFact1=(long)(amountTemp * additionalProfileDetailsVO.getDiffrentialFactor());
				long afterMultipleFact1= BTSLUtil.parseDoubleToLong(amountTemp * additionalProfileDetailsVO.getDiffrentialFactor());
				if(_log.isDebugEnabled()) {
					loggerValue.setLength(0);
			    	loggerValue.append(" afterMultipleFact Owner :");
			    	loggerValue.append(afterMultipleFact1);
					_log.debug(methodName,loggerValue);
				}

				_adjustmentOwnerVOCredit.setMarginAmount(afterMultipleFact1);
				_adjustmentOwnerVODebit.setMarginAmount(afterMultipleFact1);

				if (PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(additionalProfileDetailsVO.getAddCommType())) {
					_adjustmentOwnerVOCredit.setMarginRate(Double.parseDouble(PretupsBL.getDisplayAmount(additionalProfileDetailsVO.getAddOwnerCommRate())));
					_adjustmentOwnerVODebit.setMarginRate(Double.parseDouble(PretupsBL.getDisplayAmount(additionalProfileDetailsVO.getAddOwnerCommRate())));
				} else {
					_adjustmentOwnerVOCredit.setMarginRate(additionalProfileDetailsVO.getAddOwnerCommRate());
					_adjustmentOwnerVODebit.setMarginRate(additionalProfileDetailsVO.getAddOwnerCommRate());

				}
				if(BTSLUtil.isNullString(additionalProfileDetailsVO.getOwnerTax1Type())) {
					additionalProfileDetailsVO.setOwnerTax1Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
				}				
				calculatedTax1Value=calculatorI.calculateDifferentialTax1(additionalProfileDetailsVO.getOwnerTax1Type(),additionalProfileDetailsVO.getOwnerTax1Rate(),afterMultipleFact1,p_requestAmount);		
				_adjustmentOwnerVODebit.setTax1Value(calculatedTax1Value);
				_adjustmentOwnerVOCredit.setTax1Value(calculatedTax1Value);

				if(BTSLUtil.isNullString(additionalProfileDetailsVO.getOwnerTax2Type())) {
					additionalProfileDetailsVO.setOwnerTax2Type(PretupsI.AMOUNT_TYPE_PERCENTAGE);
				}
				if(additionalProfileDetailsVO.getOwnerTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
					// calculatedTax2Value=(long)additionalProfileDetailsVO.getOwnerTax2Rate();
					calculatedTax2Value= BTSLUtil.parseDoubleToLong(additionalProfileDetailsVO.getOwnerTax2Rate());
				} else { 
					//If percentage
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
						calculatedTax2Value=calculatorI.calculateDifferentialTax2(additionalProfileDetailsVO.getOwnerTax2Type(),additionalProfileDetailsVO.getOwnerTax2Rate(),calculatedTax1Value);
					} else {
						calculatedTax2Value=calculatorI.calculateDifferentialTax2(additionalProfileDetailsVO.getOwnerTax2Type(),additionalProfileDetailsVO.getOwnerTax2Rate(),afterMultipleFact1);
					}
				}
				_adjustmentOwnerVODebit.setTax2Value(calculatedTax2Value);
				_adjustmentOwnerVOCredit.setTax2Value(calculatedTax2Value);
				ownerTransferValue=calculatorI.calculateDifferentialTransferValue(p_requestAmount,afterMultipleFact1,calculatedTax1Value,calculatedTax2Value);
				_adjustmentOwnerVOCredit.setTransferValue(ownerTransferValue);
				_adjustmentOwnerVODebit.setTransferValue(ownerTransferValue);
			}
			//For additional commission through owner--End

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception :");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[calculateAdditionalCommission]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	// added for roam commmission calculation

	private void calculateAdditionalRoamCommission(AdditionalProfileDeatilsVO additionalProfileDetailsVO, long p_requestAmount) throws BTSLBaseException {
		final String methodName = "calculateAdditionalRoamCommission";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered with p_requestAmount :");
    	loggerValue.append(p_requestAmount);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		try {
			long amountTemp = 0;
			long calculatedTax1Value = 0;
			long calculatedTax2Value = 0;
			long transferValue = 0;
			//added for owner commision
			long ownerTransferValue=0;
			
			amountTemp = calculatorI.calculateDifferentialComm(additionalProfileDetailsVO.getAddRoamCommType(), additionalProfileDetailsVO.getAddRoamCommRate(),
					p_requestAmount);
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
		    	loggerValue.append(" margin amt :");
		    	loggerValue.append(amountTemp);
			}

			if (Math.abs(additionalProfileDetailsVO.getDiffrentialFactor()-0)<EPSILON) {
				_log.error(methodName, " Differential factor: multipleFactor:" + additionalProfileDetailsVO.getDiffrentialFactor());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[calculateAdditionalRoamCommission]", "",
						"", "", "Differential factor: multipleFactor cannot be zero");
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERR_DIFF_FACTOR_CANNOT_BE_ZERO);
			}
			long afterMultipleFact = BTSLUtil.parseDoubleToLong(amountTemp * additionalProfileDetailsVO.getDiffrentialFactor());

			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
		    	loggerValue.append(" afterMultipleFact :");
		    	loggerValue.append(afterMultipleFact);
				_log.debug(methodName,loggerValue);
			}

			_adjustmentVOCredit.setMarginAmount(afterMultipleFact);
			_adjustmentVODebit.setMarginAmount(afterMultipleFact);
			if (PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(additionalProfileDetailsVO.getAddRoamCommType())) {

				_adjustmentVOCredit.setMarginRate(Double.parseDouble(PretupsBL.getDisplayAmount(additionalProfileDetailsVO.getAddRoamCommRate())));
				_adjustmentVODebit.setMarginRate(Double.parseDouble(PretupsBL.getDisplayAmount(additionalProfileDetailsVO.getAddRoamCommRate())));

			}
			else
			{
				_adjustmentVOCredit.setMarginRate(additionalProfileDetailsVO.getAddRoamCommRate());
				_adjustmentVODebit.setMarginRate(additionalProfileDetailsVO.getAddRoamCommRate());

			}
			_adjustmentVOCredit.setMarginType(additionalProfileDetailsVO.getAddRoamCommType());
			_adjustmentVODebit.setMarginType(additionalProfileDetailsVO.getAddRoamCommType());

			calculatedTax1Value = calculatorI.calculateDifferentialTax1(additionalProfileDetailsVO.getTax1Type(), additionalProfileDetailsVO.getTax1Rate(),
					afterMultipleFact, p_requestAmount);

			_adjustmentVODebit.setTax1Value(calculatedTax1Value);
			_adjustmentVOCredit.setTax1Value(calculatedTax1Value);

			if (additionalProfileDetailsVO.getTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
				calculatedTax2Value = BTSLUtil.parseDoubleToLong(additionalProfileDetailsVO.getTax2Rate());
			} else  {
				// If percentage
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
					calculatedTax2Value = calculatorI.calculateDifferentialTax2(additionalProfileDetailsVO.getTax2Type(), additionalProfileDetailsVO.getTax2Rate(),
							calculatedTax1Value);
				} else {
					calculatedTax2Value = calculatorI.calculateDifferentialTax2(additionalProfileDetailsVO.getTax2Type(), additionalProfileDetailsVO.getTax2Rate(),
							afterMultipleFact);
				}
			}
			_adjustmentVODebit.setTax2Value(calculatedTax2Value);
			_adjustmentVOCredit.setTax2Value(calculatedTax2Value);
			transferValue = calculatorI.calculateDifferentialTransferValue(p_requestAmount, afterMultipleFact, calculatedTax1Value, calculatedTax2Value);
			_adjustmentVOCredit.setTransferValue(transferValue);
			_adjustmentVODebit.setTransferValue(transferValue);
			//For additional commission through owner--Start						
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()  && !additionalProfileDetailsVO.getSequenceNo().equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER)))
			{
				amountTemp=calculatorI.calculateDifferentialComm(additionalProfileDetailsVO.getAddOwnerCommType(),additionalProfileDetailsVO.getAddOwnerCommRate(),p_requestAmount);
				if(_log.isDebugEnabled()) {
					loggerValue.setLength(0);
			    	loggerValue.append(" owner margin amt :");
			    	loggerValue.append(amountTemp);
					_log.debug("calculateAdditionalRoamCommission: ",loggerValue);
				}			
				afterMultipleFact= BTSLUtil.parseDoubleToLong(amountTemp * additionalProfileDetailsVO.getDiffrentialFactor());

				if(_log.isDebugEnabled()) {
					loggerValue.setLength(0);
			    	loggerValue.append(" afterMultipleFact Owner :");
			    	loggerValue.append(afterMultipleFact);
					_log.debug("calculateAdditionalRoamCommission: ",loggerValue);
				}						
				_adjustmentOwnerVOCredit.setMarginAmount(afterMultipleFact);
				_adjustmentOwnerVODebit.setMarginAmount(afterMultipleFact);
				calculatedTax1Value=calculatorI.calculateDifferentialTax1(additionalProfileDetailsVO.getOwnerTax1Type(),additionalProfileDetailsVO.getOwnerTax1Rate(),afterMultipleFact,p_requestAmount);
				_adjustmentOwnerVODebit.setTax1Value(calculatedTax1Value);
				_adjustmentOwnerVOCredit.setTax1Value(calculatedTax1Value);

				if(additionalProfileDetailsVO.getOwnerTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
					calculatedTax2Value=BTSLUtil.parseDoubleToLong(additionalProfileDetailsVO.getOwnerTax2Rate());
				} else { 
					//If percentage
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
						calculatedTax2Value=calculatorI.calculateDifferentialTax2(additionalProfileDetailsVO.getOwnerTax2Type(),additionalProfileDetailsVO.getOwnerTax2Rate(),calculatedTax1Value);
					} else {
						calculatedTax2Value=calculatorI.calculateDifferentialTax2(additionalProfileDetailsVO.getOwnerTax2Type(),additionalProfileDetailsVO.getOwnerTax2Rate(),afterMultipleFact);
					}
				}
				_adjustmentOwnerVODebit.setTax2Value(calculatedTax2Value);
				_adjustmentOwnerVOCredit.setTax2Value(calculatedTax2Value);
				ownerTransferValue=calculatorI.calculateDifferentialTransferValue(p_requestAmount,afterMultipleFact,calculatedTax1Value,calculatedTax2Value);
				_adjustmentOwnerVOCredit.setTransferValue(ownerTransferValue);
				_adjustmentOwnerVODebit.setTransferValue(ownerTransferValue);		

			}
			//For additional commission through owner--End
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception :");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[calculateAdditionalCommission]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	/**
	 * Credits the user for the value if online differential has to be given
	 * 
	 * @param con
	 * @param p_adjustmentsVO
	 * @param p_categoryCode
	 * @throws BTSLBaseException
	 */
	public void creditUserBalanceForProduct(Connection con, AdjustmentsVO p_adjustmentsVO, String p_categoryCode) throws BTSLBaseException {
		final String methodName = "creditUserBalanceForProduct";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered adjustments ID : " + p_adjustmentsVO.getAdjustmentID());
		}
		try {
			_userBalancesVO = prepareUserBalanceVOFromTransferVO(p_adjustmentsVO, PretupsI.TRANSFER_TYPE_DIFFCR, _source, p_adjustmentsVO.getEntryType(),
					PretupsI.TRANSFER_TYPE_DIFFCR, "");

			_userBalancesDAO.updateUserDailyBalances(con, p_adjustmentsVO.getCreatedOn(), _userBalancesVO);
			// Credit the sender
			int updateCount = 0;
			updateCount = _userBalancesDAO.creditUserBalances(con, _userBalancesVO, p_categoryCode);
			if (updateCount <= 0) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
			}
			_creditBackEntryDone = true;
			// Update Previous and Post balances of Reciever            
			p_adjustmentsVO.setPreviousBalance(_userBalancesVO.getPreviousBalance());
			p_adjustmentsVO.setPostBalance(_userBalancesVO.getBalance());
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[creditUserBalanceForProduct]",
					p_adjustmentsVO.getAdjustmentID(), "", p_adjustmentsVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION,e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting adjustments ID : " + p_adjustmentsVO.getAdjustmentID());
		}
	}

	/**
	 * Prepares the balance VO for crediting the user
	 * 
	 * @param p_adjustmentsVO
	 * @param p_transferType
	 * @param p_source
	 * @param p_entryType
	 * @param p_transType
	 * @param p_transferCategory
	 * @return UserBalancesVO
	 */
	private UserBalancesVO prepareUserBalanceVOFromTransferVO(AdjustmentsVO p_adjustmentsVO, String p_transferType, String p_source, String p_entryType, String p_transType, String p_transferCategory) {
		UserBalancesVO userBalancesVO = UserBalancesVO.getInstance();
		userBalancesVO.setUserID(p_adjustmentsVO.getUserID());
		userBalancesVO.setProductCode(p_adjustmentsVO.getProductCode());
		userBalancesVO.setNetworkCode(p_adjustmentsVO.getNetworkCode());
		userBalancesVO.setNetworkFor(p_adjustmentsVO.getNetworkCodeFor());
		userBalancesVO.setLastTransferID(p_adjustmentsVO.getAdjustmentID());
		userBalancesVO.setLastTransferType(p_transferType);
		userBalancesVO.setLastTransferOn(p_adjustmentsVO.getCreatedOn());
		userBalancesVO.setQuantityToBeUpdated(p_adjustmentsVO.getTransferValue());
		userBalancesVO.setSource(p_source);
		userBalancesVO.setCreatedBy(p_adjustmentsVO.getCreatedBy());
		userBalancesVO.setEntryType(p_entryType);
		userBalancesVO.setType(p_transType);
		userBalancesVO.setTransferCategory(p_transferCategory);
		userBalancesVO.setRequestedQuantity(String.valueOf(_requestedAmount));
		userBalancesVO.setOtherInfo("Reference ID=" + p_adjustmentsVO.getReferenceID());
		// Add to log user MSISDN on 20/02.2008
		userBalancesVO.setUserMSISDN(p_adjustmentsVO.getUserMSISDN());
		if (_log.isDebugEnabled()) {
			_log.debug("prepareUserBalanceVOFromTransferVO", " userBalancesVO=" + userBalancesVO.toString());
		}
		return userBalancesVO;
	}

	/**
	 * Method to calculate the Differential Calculation for the request
	 * 
	 * @param c2sTransferVO
	 * @param p_module
	 * @param p_quantityRequired
	 * @param p_voucherList
	 * @throws BTSLBaseException
	 */
	public void differentialCalculations(C2STransferVO c2sTransferVO, String p_module, int p_quantityRequired, ArrayList p_voucherList) throws BTSLBaseException {
		final String methodName = "differentialCalculations";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append(" Entered p_module:");
    	loggerValue.append(p_module);
		loggerValue.append(" p_quantityRequired:");
    	loggerValue.append(p_quantityRequired);
    	loggerValue.append(" p_voucherList size=");
        loggerValue.append(p_voucherList.size());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ChannelUserVO channelUserVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String otherInfo = null;
		StringBuilder otherInfoBuilder=new StringBuilder();
		try {
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			_source = c2sTransferVO.getSourceType();
			_requestedAmount = c2sTransferVO.getRequestedAmount();
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			String commissionProfileSetID = channelUserVO.getCommissionProfileSetID();
			// commision will be calculated on total amount i.e. mrp*quantity as
			// discussed with Sanjay Sir
			AdditionalProfileDeatilsVO additionalProfileDetailsVO = _commissionProfileTxnDAO.loadAdditionCommissionDetails(con, c2sTransferVO.getTransferID(),
					commissionProfileSetID, c2sTransferVO.getRequestedAmount() * p_quantityRequired, c2sTransferVO.getTransferDateTime(), c2sTransferVO.getServiceType(),
					c2sTransferVO.getSubService(), c2sTransferVO.getRequestGatewayCode());
			if (additionalProfileDetailsVO != null) {
				c2sTransferVO.setDifferentialApplicable(PretupsI.YES);
				calculateDifferential(con, c2sTransferVO, additionalProfileDetailsVO, p_quantityRequired, p_voucherList);
				try {
					mcomCon.finalCommit();
					// Set message only if Adjustment value is greater than 0
					if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getGiveOnlineDifferential()) && _adjustmentVOCredit.getTransferValue() > 0) {
						if(!BTSLUtil.isNullString(c2sTransferVO.getSubscriberSID())){
							String[] messageArgArray = { _adjustmentCreditID, c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), c2sTransferVO.getSubscriberSID(), c2sTransferVO.getTransferValueStr() };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
									PretupsErrorCodesI.C2S_ADJUSTMENT_SUCCESS, messageArgArray));
						}else{
							String[] messageArgArray = { _adjustmentCreditID, c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), c2sTransferVO.getReceiverMsisdn(), c2sTransferVO.getTransferValueStr() };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
									PretupsErrorCodesI.C2S_ADJUSTMENT_SUCCESS, messageArgArray));
						}
					}
					otherInfoBuilder.append("Addition Commission=");
					otherInfoBuilder.append(_adjustmentVOCredit.getTransferValue());
					otherInfoBuilder.append(" ID=");
					otherInfoBuilder.append(_adjustmentCreditID);
					otherInfoBuilder.append(" Stock Updated=");
					otherInfoBuilder.append(_adjustmentVOCredit.getStockUpdated());
					otherInfo=otherInfoBuilder.toString();
					// Log the details of the differential
					if (_creditBackEntryDone) {
						BalanceLogger.log(_userBalancesVO);
					}
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					loggerValue.setLength(0);
	            	loggerValue.append("Exception Transfer ID:");
	            	loggerValue.append(c2sTransferVO.getTransferID());
					loggerValue.append(" Exception:");
	            	loggerValue.append(e.getMessage());
					_log.error(methodName,loggerValue);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]",
							c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
				}
			} else {
				c2sTransferVO.setDifferentialApplicable(PretupsI.NO);
				c2sTransferVO.setDifferentialGiven(PretupsI.NO);
				otherInfo = "No additional taxes found";
				loggerValue.setLength(0);
            	loggerValue.append("No additional taxes found for transfer ID=");
            	loggerValue.append(c2sTransferVO.getTransferID());
				_log.debug(methodName,loggerValue);
			}
		} catch (BTSLBaseException be) {
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e1) {
				_log.errorTrace(methodName, e1);
			}
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e1) {
				_log.errorTrace(methodName, e1);
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "differentialCalculations", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("DiffCalBL#differentialCalculations");
				mcomCon = null;
			}
			// Log the information in Diff Credit Log
			DiffCreditLog.log(c2sTransferVO, otherInfo);
			_log.debug(methodName, " Exiting...");
		}
	}

	/**
	 * Method to calculate Differential and controls the flow of process
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @param p_quantityRequired
	 * @param p_voucherList
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void calculateDifferential(Connection con, C2STransferVO c2sTransferVO, AdditionalProfileDeatilsVO additionalProfileDetailsVO, int p_quantityRequired, ArrayList p_voucherList) throws BTSLBaseException{
		final String methodName = "calculateDifferential";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
	    	loggerValue.append("Entered Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" p_quantityRequired=");
	    	loggerValue.append(p_quantityRequired);
	    	loggerValue.append(" p_voucherList size=");
	    	loggerValue.append(p_voucherList.size());
			_log.debug(methodName,loggerValue);
		}

		AdjustmentsVO newAdjustmentVODebit = null;
		AdjustmentsVO newAdjustmentVOCredit = null;
		ArrayList newItemList = null;
		int addCount = 0;
		try {

			_adjustmentVODebit = new AdjustmentsVO();
			_adjustmentVODebit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVODebit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setCreatedOn(_currentDate);
			_adjustmentVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setModifiedOn(_currentDate);
			_adjustmentVODebit.setDifferentialFactor(additionalProfileDetailsVO.getDiffrentialFactor());
			_adjustmentVODebit.setEntryType(PretupsI.DEBIT);
			_adjustmentVODebit.setMarginRate(additionalProfileDetailsVO.getAddCommRate());
			_adjustmentVODebit.setMarginType(additionalProfileDetailsVO.getAddCommType());
			_adjustmentVODebit.setModule(c2sTransferVO.getModule());
			_adjustmentVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());

			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			} else {
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			}

			_adjustmentVODebit.setStockUpdated(PretupsI.NO);
			_adjustmentVODebit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVODebit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVODebit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVODebit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			_adjustmentVODebit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			_adjustmentVODebit.setTax1Rate(additionalProfileDetailsVO.getTax1Rate());
			_adjustmentVODebit.setTax1Type(additionalProfileDetailsVO.getTax1Type());
			_adjustmentVODebit.setTax2Rate(additionalProfileDetailsVO.getTax2Rate());
			_adjustmentVODebit.setTax2Type(additionalProfileDetailsVO.getTax2Type());
			_adjustmentVODebit.setAddnlCommProfileDetailID(additionalProfileDetailsVO.getAddCommProfileDetailID());
			_adjustmentVODebit.setSubService(c2sTransferVO.getSubService());
			_adjustmentVODebit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
			_adjustmentVOCredit = new AdjustmentsVO();
			_adjustmentVOCredit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setCreatedOn(_currentDate);
			_adjustmentVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setModifiedOn(_currentDate);
			_adjustmentVOCredit.setDifferentialFactor(additionalProfileDetailsVO.getDiffrentialFactor());
			_adjustmentVOCredit.setEntryType(PretupsI.CREDIT);
			_adjustmentVOCredit.setMarginRate(additionalProfileDetailsVO.getAddCommRate());
			_adjustmentVOCredit.setMarginType(additionalProfileDetailsVO.getAddCommType());
			_adjustmentVOCredit.setModule(c2sTransferVO.getModule());
			_adjustmentVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());

			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			} else {
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			}

			_adjustmentVOCredit.setStockUpdated(PretupsI.NO);
			_adjustmentVOCredit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVOCredit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVOCredit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVOCredit.setUserID(c2sTransferVO.getSenderID());
			_adjustmentVOCredit.setUserCategory(((ChannelUserVO) c2sTransferVO.getSenderVO()).getCategoryCode());
			_adjustmentVOCredit.setTax1Rate(additionalProfileDetailsVO.getTax1Rate());
			_adjustmentVOCredit.setTax1Type(additionalProfileDetailsVO.getTax1Type());
			_adjustmentVOCredit.setTax2Rate(additionalProfileDetailsVO.getTax2Rate());
			_adjustmentVOCredit.setTax2Type(additionalProfileDetailsVO.getTax2Type());
			_adjustmentVOCredit.setAddnlCommProfileDetailID(additionalProfileDetailsVO.getAddCommProfileDetailID());
			// Add to log user MSISDN on 20/02/2008
			_adjustmentVOCredit.setUserMSISDN(c2sTransferVO.getSenderMsisdn());
			_adjustmentVOCredit.setSubService(c2sTransferVO.getSubService());
			_adjustmentVOCredit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
			
			additionalProfileDetailsVO.setSequenceNo(_commissionProfileDAO.loadsequenceNo(con, ((ChannelUserVO)c2sTransferVO.getSenderVO()).getCategoryCode()));
			calculateAdditionalCommission(additionalProfileDetailsVO, c2sTransferVO.getRequestedAmount());

			// String onlineCreditFlag=new
			if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getGiveOnlineDifferential())) {
				// setting the transfer value equal to total transfer value for
				// all the vouchers
				_adjustmentVOCredit.setTransferValue(_adjustmentVOCredit.getTransferValue() * p_quantityRequired);
				creditUserBalanceForProduct(con, _adjustmentVOCredit, c2sTransferVO.getCategoryCode());
				// reverting back the value as before i.e. for one transaction
				_adjustmentVOCredit.setTransferValue(_adjustmentVOCredit.getTransferValue() / p_quantityRequired);
				_adjustmentVOCredit.setStockUpdated(PretupsI.YES);
				c2sTransferVO.setDifferentialGiven(PretupsI.YES);
			} else {
				_adjustmentVOCredit.setStockUpdated(PretupsI.NO);
				c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			}

			newItemList = new ArrayList();
			for (int i = 0, size = p_voucherList.size(); i < size; i++) {
				newAdjustmentVODebit = new AdjustmentsVO();
				newAdjustmentVODebit.setReferenceID(((VomsVoucherVO) p_voucherList.get(i)).getTransactionID());
				newAdjustmentVODebit.setAdjustmentID(((VomsVoucherVO) p_voucherList.get(i)).getTransactionID() + PretupsI.SUFIX_ADJUST_TXN_ID_NW);
				newAdjustmentVODebit.setNetworkCode(_adjustmentVODebit.getNetworkCode());
				newAdjustmentVODebit.setNetworkCodeFor(_adjustmentVODebit.getNetworkCodeFor());
				newAdjustmentVODebit.setAdjustmentType(_adjustmentVODebit.getAdjustmentType());
				newAdjustmentVODebit.setEntryType(_adjustmentVODebit.getEntryType());
				newAdjustmentVODebit.setAdjustmentDate(_adjustmentVODebit.getAdjustmentDate());
				newAdjustmentVODebit.setUserID(_adjustmentVODebit.getUserID());
				newAdjustmentVODebit.setUserCategory(_adjustmentVODebit.getUserCategory());
				newAdjustmentVODebit.setProductCode(_adjustmentVODebit.getProductCode());
				newAdjustmentVODebit.setServiceType(_adjustmentVODebit.getServiceType());
				newAdjustmentVODebit.setTransferValue(_adjustmentVODebit.getTransferValue());
				newAdjustmentVODebit.setMarginType(_adjustmentVODebit.getMarginType());
				newAdjustmentVODebit.setMarginRate(_adjustmentVODebit.getMarginRate());
				newAdjustmentVODebit.setMarginAmount(_adjustmentVODebit.getMarginAmount());
				newAdjustmentVODebit.setTax1Type(_adjustmentVODebit.getTax1Type());
				newAdjustmentVODebit.setTax1Rate(_adjustmentVODebit.getTax1Rate());
				newAdjustmentVODebit.setTax1Value(_adjustmentVODebit.getTax1Value());
				newAdjustmentVODebit.setTax2Type(_adjustmentVODebit.getTax2Type());
				newAdjustmentVODebit.setTax2Rate(_adjustmentVODebit.getTax2Rate());
				newAdjustmentVODebit.setTax2Value(_adjustmentVODebit.getTax2Value());
				newAdjustmentVODebit.setTax3Type(_adjustmentVODebit.getTax3Type());
				newAdjustmentVODebit.setTax3Rate(_adjustmentVODebit.getTax3Rate());
				newAdjustmentVODebit.setTax3Value(_adjustmentVODebit.getTax3Value());
				newAdjustmentVODebit.setDifferentialFactor(_adjustmentVODebit.getDifferentialFactor());
				// previous balance=previous
				// blance+((post-previous)/quantity*(i))
				// post balance=previous blance+((post-previous)/quantity*(i+1))
				newAdjustmentVODebit.setPreviousBalance((_adjustmentVODebit.getPreviousBalance() + ((_adjustmentVODebit.getPostBalance() - _adjustmentVODebit
						.getPreviousBalance()) / p_quantityRequired * i)));
				newAdjustmentVODebit
				.setPostBalance((_adjustmentVODebit.getPreviousBalance() + ((_adjustmentVODebit.getPostBalance() - _adjustmentVODebit.getPreviousBalance()) / p_quantityRequired * (i + 1))));

				newAdjustmentVODebit.setPreviousBalance(_adjustmentVODebit.getPreviousBalance());
				newAdjustmentVODebit.setPostBalance(_adjustmentVODebit.getPostBalance());
				newAdjustmentVODebit.setCreatedBy(_adjustmentVODebit.getCreatedBy());
				newAdjustmentVODebit.setCreatedOn(_adjustmentVODebit.getCreatedOn());
				newAdjustmentVODebit.setModifiedBy(_adjustmentVODebit.getModifiedBy());
				newAdjustmentVODebit.setModifiedOn(_adjustmentVODebit.getModifiedOn());
				newAdjustmentVODebit.setModule(_adjustmentVODebit.getModule());
				newAdjustmentVODebit.setStockUpdated(_adjustmentVODebit.getStockUpdated());
				newAdjustmentVODebit.setAddnlCommProfileDetailID(_adjustmentVODebit.getAddnlCommProfileDetailID());
				newAdjustmentVODebit.setSubService(_adjustmentVODebit.getSubService());
				 newAdjustmentVODebit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
				 
				newAdjustmentVOCredit = new AdjustmentsVO();
				newAdjustmentVOCredit.setReferenceID(((VomsVoucherVO) p_voucherList.get(i)).getTransactionID());
				newAdjustmentVOCredit.setAdjustmentID(((VomsVoucherVO) p_voucherList.get(i)).getTransactionID() + PretupsI.SUFIX_ADJUST_TXN_ID_USER);
				newAdjustmentVOCredit.setNetworkCode(_adjustmentVOCredit.getNetworkCode());
				newAdjustmentVOCredit.setNetworkCodeFor(_adjustmentVOCredit.getNetworkCodeFor());
				newAdjustmentVOCredit.setAdjustmentType(_adjustmentVOCredit.getAdjustmentType());
				newAdjustmentVOCredit.setEntryType(_adjustmentVOCredit.getEntryType());
				newAdjustmentVOCredit.setAdjustmentDate(_adjustmentVOCredit.getAdjustmentDate());
				newAdjustmentVOCredit.setUserID(_adjustmentVOCredit.getUserID());
				newAdjustmentVOCredit.setUserCategory(_adjustmentVOCredit.getUserCategory());
				newAdjustmentVOCredit.setProductCode(_adjustmentVOCredit.getProductCode());
				newAdjustmentVOCredit.setServiceType(_adjustmentVOCredit.getServiceType());
				newAdjustmentVOCredit.setTransferValue(_adjustmentVOCredit.getTransferValue());
				newAdjustmentVOCredit.setMarginType(_adjustmentVOCredit.getMarginType());
				newAdjustmentVOCredit.setMarginRate(_adjustmentVOCredit.getMarginRate());
				newAdjustmentVOCredit.setMarginAmount(_adjustmentVOCredit.getMarginAmount());
				newAdjustmentVOCredit.setTax1Type(_adjustmentVOCredit.getTax1Type());
				newAdjustmentVOCredit.setTax1Rate(_adjustmentVOCredit.getTax1Rate());
				newAdjustmentVOCredit.setTax1Value(_adjustmentVOCredit.getTax1Value());
				newAdjustmentVOCredit.setTax2Type(_adjustmentVOCredit.getTax2Type());
				newAdjustmentVOCredit.setTax2Rate(_adjustmentVOCredit.getTax2Rate());
				newAdjustmentVOCredit.setTax2Value(_adjustmentVOCredit.getTax2Value());
				newAdjustmentVOCredit.setTax3Type(_adjustmentVOCredit.getTax3Type());
				newAdjustmentVOCredit.setTax3Rate(_adjustmentVOCredit.getTax3Rate());
				newAdjustmentVOCredit.setTax3Value(_adjustmentVOCredit.getTax3Value());
				newAdjustmentVOCredit.setDifferentialFactor(_adjustmentVOCredit.getDifferentialFactor());
				// previous balance=previous
				// blance+((post-previous)/quantity*(i))
				// post balance=previous blance+((post-previous)/quantity*(i+1))
				newAdjustmentVOCredit.setPreviousBalance((_adjustmentVOCredit.getPreviousBalance() + ((_adjustmentVOCredit.getPostBalance() - _adjustmentVOCredit
						.getPreviousBalance()) / p_quantityRequired * i)));
				newAdjustmentVOCredit.setPostBalance((_adjustmentVOCredit.getPreviousBalance() + ((_adjustmentVOCredit.getPostBalance() - _adjustmentVOCredit
						.getPreviousBalance()) / p_quantityRequired * (i + 1))));
				newAdjustmentVOCredit.setCreatedBy(_adjustmentVOCredit.getCreatedBy());
				newAdjustmentVOCredit.setCreatedOn(_adjustmentVOCredit.getCreatedOn());
				newAdjustmentVOCredit.setModifiedBy(_adjustmentVOCredit.getModifiedBy());
				newAdjustmentVOCredit.setModifiedOn(_adjustmentVOCredit.getModifiedOn());
				newAdjustmentVOCredit.setModule(_adjustmentVOCredit.getModule());
				newAdjustmentVOCredit.setStockUpdated(_adjustmentVOCredit.getStockUpdated());
				newAdjustmentVOCredit.setAddnlCommProfileDetailID(_adjustmentVOCredit.getAddnlCommProfileDetailID());
				newAdjustmentVOCredit.setSubService(_adjustmentVOCredit.getSubService());
				 newAdjustmentVOCredit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
				newItemList.add(newAdjustmentVODebit);
				newItemList.add(newAdjustmentVOCredit);
			}
			addCount = _adjustmentsDAO.addAdjustmentEntries(con, newItemList, c2sTransferVO.getTransferID());
			if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()))
			{
				calculateUserOTFCounts(con, c2sTransferVO,additionalProfileDetailsVO);
			}

			if(addCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()) && c2sTransferVO.getUserOTFCountsVO() != null)
			{
				int updateCount1=new UserTransferCountsDAO().updateUserOTFCounts(con, c2sTransferVO.getUserOTFCountsVO());
				if (updateCount1 <= 0) {
					throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION);
				}
			}
		} catch (BTSLBaseException be) {
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			throw be;
		} catch (Exception e) {
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting...");
		}
	}

	/**
	 * Populates the Adjustment VO for Debit entry
	 * 
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @param p_quantityRequired
	 * @throws BTSLBaseException
	 */
	private ArrayList populateAdjustmentDebit(C2STransferVO c2sTransferVO, AdditionalProfileDeatilsVO additionalProfileDetailsVO, int p_quantityRequired) throws BTSLBaseException {
		final String methodName = "populateAdjustmentDebit";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered Transfer ID:");
    	loggerValue.append(c2sTransferVO.getTransferID());
		loggerValue.append(" p_quantityRequired=");
    	loggerValue.append(p_quantityRequired);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ArrayList adjustmentDebitIDList = null;
		try {
			adjustmentDebitIDList = PretupsBL.generateAdjustmentID(c2sTransferVO.getReceiverNetworkCode(), _currentDate, p_quantityRequired);
			_adjustmentDebitID = (String) adjustmentDebitIDList.get(0);
			_adjustmentVODebit = new AdjustmentsVO();
			_adjustmentVODebit.setAdjustmentID(_adjustmentDebitID);
			_adjustmentVODebit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVODebit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setCreatedOn(_currentDate);
			_adjustmentVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setModifiedOn(_currentDate);
			_adjustmentVODebit.setDifferentialFactor(additionalProfileDetailsVO.getDiffrentialFactor());
			_adjustmentVODebit.setEntryType(PretupsI.DEBIT);
			_adjustmentVODebit.setMarginRate(additionalProfileDetailsVO.getAddCommRate());
			_adjustmentVODebit.setMarginType(additionalProfileDetailsVO.getAddCommType());
			_adjustmentVODebit.setModule(c2sTransferVO.getModule());
			_adjustmentVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());
			// Add to log user MSISDN on 20/02/2008
			_adjustmentVODebit.setUserMSISDN(c2sTransferVO.getReceiverMsisdn());
			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			} else {
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			}

			_adjustmentVODebit.setStockUpdated(PretupsI.NO);
			_adjustmentVODebit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVODebit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVODebit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVODebit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			_adjustmentVODebit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			_adjustmentVODebit.setTax1Rate(additionalProfileDetailsVO.getTax1Rate());
			_adjustmentVODebit.setTax1Type(additionalProfileDetailsVO.getTax1Type());
			_adjustmentVODebit.setTax2Rate(additionalProfileDetailsVO.getTax2Rate());
			_adjustmentVODebit.setTax2Type(additionalProfileDetailsVO.getTax2Type());
			_adjustmentVODebit.setAddnlCommProfileDetailID(additionalProfileDetailsVO.getAddCommProfileDetailID());
			_adjustmentVODebit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
			return (adjustmentDebitIDList);
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			;
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[populateAdjustmentDebit]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "populateAdjustmentDebit", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting...");
			}
		}
	}

	/**
	 * Populates the Adjustment VO for Credit entry
	 * 
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @param p_quantityRequired
	 * @throws BTSLBaseException
	 */
	private ArrayList populateAdjustmentCredit(C2STransferVO c2sTransferVO, AdditionalProfileDeatilsVO additionalProfileDetailsVO, int p_quantityRequired) throws BTSLBaseException {
		final String methodName = "populateAdjustmentCredit";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered Transfer ID:");
    	loggerValue.append(c2sTransferVO.getTransferID());
		loggerValue.append(" p_quantityRequired=");
    	loggerValue.append(p_quantityRequired);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ArrayList adjustmentCreditIDList = null;
		try {
			adjustmentCreditIDList = PretupsBL.generateAdjustmentID(c2sTransferVO.getReceiverNetworkCode(), _currentDate, p_quantityRequired);
			_adjustmentCreditID = (String) adjustmentCreditIDList.get(0);
			_adjustmentVOCredit = new AdjustmentsVO();
			_adjustmentVOCredit.setAdjustmentID(_adjustmentCreditID);
			_adjustmentVOCredit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setCreatedOn(_currentDate);
			_adjustmentVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setModifiedOn(_currentDate);
			_adjustmentVOCredit.setDifferentialFactor(additionalProfileDetailsVO.getDiffrentialFactor());
			_adjustmentVOCredit.setEntryType(PretupsI.CREDIT);
			_adjustmentVOCredit.setMarginRate(additionalProfileDetailsVO.getAddCommRate());
			_adjustmentVOCredit.setMarginType(additionalProfileDetailsVO.getAddCommType());
			_adjustmentVOCredit.setModule(c2sTransferVO.getModule());
			_adjustmentVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());
			// Add to log user MSISDN on 20/02/2008
			_adjustmentVOCredit.setUserMSISDN(c2sTransferVO.getSenderMsisdn());
			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			} else {
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			}

			_adjustmentVOCredit.setStockUpdated(PretupsI.NO);
			_adjustmentVOCredit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVOCredit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVOCredit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVOCredit.setUserID(c2sTransferVO.getSenderID());
			_adjustmentVOCredit.setUserCategory(((ChannelUserVO) c2sTransferVO.getSenderVO()).getCategoryCode());
			_adjustmentVOCredit.setTax1Rate(additionalProfileDetailsVO.getTax1Rate());
			_adjustmentVOCredit.setTax1Type(additionalProfileDetailsVO.getTax1Type());
			_adjustmentVOCredit.setTax2Rate(additionalProfileDetailsVO.getTax2Rate());
			_adjustmentVOCredit.setTax2Type(additionalProfileDetailsVO.getTax2Type());
			_adjustmentVOCredit.setAddnlCommProfileDetailID(additionalProfileDetailsVO.getAddCommProfileDetailID());
			return (adjustmentCreditIDList);
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[populateAdjustmentCredit]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "populateAdjustmentCredit", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting...");
			}
		}
	}

	/**
	 * Method to calculate the negative additional commission
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public AdjustmentsVO differentialCalculationForBalanceCheck(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "differentialCalculationForBalanceCheck";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered Sender MSISDN:");
    	loggerValue.append(c2sTransferVO.getSenderMsisdn());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ChannelUserVO channelUserVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
	
		AdditionalProfileDeatilsVO additionalProfileDetailsVO = null;
		try {
			additionalProfileDetailsVO = _commissionProfileTxnDAO.loadAdditionCommissionDetails(con, c2sTransferVO.getTransferID(), channelUserVO
					.getCommissionProfileSetID(), c2sTransferVO.getRequestedAmount(), c2sTransferVO.getTransferDateTime(), c2sTransferVO.getServiceType(), c2sTransferVO
					.getSubService(), c2sTransferVO.getRequestGatewayCode());

			if (additionalProfileDetailsVO != null) {
				_adjustmentVOCredit = new AdjustmentsVO();
				_adjustmentVODebit = new AdjustmentsVO();
				additionalProfileDetailsVO.setSequenceNo(_commissionProfileDAO.loadsequenceNo(con, ((ChannelUserVO)c2sTransferVO.getSenderVO()).getCategoryCode()));

				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !additionalProfileDetailsVO.getSequenceNo().equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))) {
					populateAdjustmentOwnerDebit(con, c2sTransferVO,additionalProfileDetailsVO);
					populateAdjustmentOwnerCredit(con, c2sTransferVO,additionalProfileDetailsVO);
				}
				else{
					_adjustmentOwnerVODebit=new AdjustmentsVO();
					_adjustmentOwnerVOCredit=new AdjustmentsVO();
				}
				calculateAdditionalCommission(additionalProfileDetailsVO, c2sTransferVO.getRequestedAmount());
				c2sTransferVO.setDifferentialApplicable(PretupsI.YES);
				c2sTransferVO.setDifferentialGiven(PretupsI.YES);
			} else {
				c2sTransferVO.setDifferentialApplicable(PretupsI.NO);
				c2sTransferVO.setDifferentialGiven(PretupsI.NO);
				loggerValue.setLength(0);
		    	loggerValue.append("No additional taxes found for transfer ID=");
		    	loggerValue.append(c2sTransferVO.getTransferID());
				_log.debug(methodName,loggerValue);
			}
		} catch (BTSLBaseException be) {
			
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		
			_log.error("differentialCalculationForBalanceCheck", "Exception Transfer ID:" + c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculationForBalanceCheck]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "differentialCalculationForBalanceCheck", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, " Exiting...");
			}
		}

		return _adjustmentVOCredit;

	}

	/**
	 * Method to calculate the Differential Calculation for the request
	 * 
	 * @param c2sTransferVO
	 * @param p_module
	 * @throws BTSLBaseException
	 */
	public void differentialCalculations(Connection con, C2STransferVO c2sTransferVO, String p_module) throws BTSLBaseException {
		final String methodName = "differentialCalculations";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_module:");
    	loggerValue.append(p_module);
		
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ChannelUserVO channelUserVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
		String otherInfo = null;
		StringBuilder otherInfoBuilder=new StringBuilder();
		try {
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			_source = c2sTransferVO.getSourceType();
			_requestedAmount = c2sTransferVO.getRequestedAmount();
			String commissionProfileSetID = channelUserVO.getCommissionProfileSetID();
			AdditionalProfileDeatilsVO additionalProfileDetailsVO = _commissionProfileTxnDAO.loadAdditionCommissionDetails(con, c2sTransferVO.getTransferID(),
					commissionProfileSetID, c2sTransferVO.getRequestedAmount(), c2sTransferVO.getTransferDateTime(), c2sTransferVO.getServiceType(), c2sTransferVO
					.getSubService(), c2sTransferVO.getRequestGatewayCode());
			if (additionalProfileDetailsVO != null) {
				c2sTransferVO.setDifferentialApplicable(PretupsI.YES);
				calculateDifferential(con, c2sTransferVO, additionalProfileDetailsVO);
				try {
					con.commit();
					// Set message only if Adjustment value is greater than 0
					if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getGiveOnlineDifferential()) && _adjustmentVOCredit.getTransferValue() > 0) {
						if(!BTSLUtil.isNullString(c2sTransferVO.getSubscriberSID())){
							String[] messageArgArray = { _adjustmentCreditID, c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), c2sTransferVO.getSubscriberSID(), c2sTransferVO.getTransferValueStr() };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
									PretupsErrorCodesI.C2S_ADJUSTMENT_SUCCESS, messageArgArray));
						}else{
							String[] messageArgArray = { _adjustmentCreditID, c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), c2sTransferVO.getReceiverMsisdn(), c2sTransferVO.getTransferValueStr() };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
									PretupsErrorCodesI.C2S_ADJUSTMENT_SUCCESS, messageArgArray));	
						}
					}
					otherInfoBuilder.append("Addition Commission=");
					otherInfoBuilder.append(_adjustmentVOCredit.getTransferValue());
					otherInfoBuilder.append(" ID=");
					otherInfoBuilder.append(_adjustmentCreditID);
					otherInfoBuilder.append(" Stock Updated=");
					otherInfoBuilder.append(_adjustmentVOCredit.getStockUpdated());
					otherInfo=otherInfoBuilder.toString();
					// Log the details of the differential
					if (_creditBackEntryDone) {
						BalanceLogger.log(_userBalancesVO);
					}
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					_log.error("differentialCalculations", "Exception Transfer ID:" + c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]",
							c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
				}
			} else {
				c2sTransferVO.setDifferentialApplicable(PretupsI.NO);
				c2sTransferVO.setDifferentialGiven(PretupsI.NO);
				otherInfo = "No additional taxes found";
				_log.debug(methodName, "No additional taxes found for transfer ID=" + c2sTransferVO.getTransferID());
			}
		} catch (BTSLBaseException be) {
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception e1) {
				_log.errorTrace(methodName, e1);
			}
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception e1) {
				_log.errorTrace(methodName, e1);
			}
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
	    	loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "differentialCalculations", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
			// Log the information in Diff Credit Log
			DiffCreditLog.log(c2sTransferVO, otherInfo);
		}
	}

	/**
	 * Method to calculate the Differential Calculation for the request
	 * 
	 * @param c2sTransferVO
	 * @param p_module
	 * @param p_quantityRequired
	 * @param p_voucherList
	 * @throws BTSLBaseException
	 */
	public void differentialCalculations(Connection con, C2STransferVO c2sTransferVO, String p_module, int p_quantityRequired, ArrayList p_voucherList) throws BTSLBaseException {
		final String methodName = "differentialCalculations";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append(" Entered p_module:");
    	loggerValue.append(p_module);
		loggerValue.append(" p_quantityRequired:");
    	loggerValue.append(p_quantityRequired);
    	loggerValue.append(" p_voucherList size=");
    	loggerValue.append( p_voucherList.size());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ChannelUserVO channelUserVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
		String otherInfo = null;
		StringBuilder otherInfoBuilder=new StringBuilder();
		try {
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			_source = c2sTransferVO.getSourceType();
			_requestedAmount = c2sTransferVO.getRequestedAmount();
			String commissionProfileSetID = channelUserVO.getCommissionProfileSetID();
			// commision will be calculated on total amount i.e. mrp*quantity as
			// discussed with Sanjay Sir
			AdditionalProfileDeatilsVO additionalProfileDetailsVO = _commissionProfileTxnDAO.loadAdditionCommissionDetails(con, c2sTransferVO.getTransferID(),
					commissionProfileSetID, c2sTransferVO.getRequestedAmount() * p_quantityRequired, c2sTransferVO.getTransferDateTime(), c2sTransferVO.getServiceType(),
					c2sTransferVO.getSubService(), c2sTransferVO.getRequestGatewayCode());
			if (additionalProfileDetailsVO != null) {
				c2sTransferVO.setDifferentialApplicable(PretupsI.YES);
				calculateDifferential(con, c2sTransferVO, additionalProfileDetailsVO, p_quantityRequired, p_voucherList);
				try {
					con.commit();
					// Set message only if Adjustment value is greater than 0
					if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getGiveOnlineDifferential()) && _adjustmentVOCredit.getTransferValue() > 0) {
						if(!BTSLUtil.isNullString(c2sTransferVO.getSubscriberSID())){
							String[] messageArgArray = { _adjustmentCreditID, c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), c2sTransferVO.getSubscriberSID(), c2sTransferVO.getTransferValueStr() };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
									PretupsErrorCodesI.C2S_ADJUSTMENT_SUCCESS, messageArgArray));
						}else{
							String[] messageArgArray = { _adjustmentCreditID, c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()), PretupsBL
									.getDisplayAmount(_adjustmentVOCredit.getPostBalance()), c2sTransferVO.getReceiverMsisdn(), c2sTransferVO.getTransferValueStr() };
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
									PretupsErrorCodesI.C2S_ADJUSTMENT_SUCCESS, messageArgArray));
						}
					}
					otherInfoBuilder.append("Addition Commission=");
					otherInfoBuilder.append(_adjustmentVOCredit.getTransferValue());
					otherInfoBuilder.append(" ID=");
					otherInfoBuilder.append(_adjustmentCreditID);
					otherInfoBuilder.append(" Stock Updated=");
					otherInfoBuilder.append(_adjustmentVOCredit.getStockUpdated());
					otherInfo=otherInfoBuilder.toString();
					// Log the details of the differential
					if (_creditBackEntryDone) {
						BalanceLogger.log(_userBalancesVO);
					}
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					loggerValue.setLength(0);
			    	loggerValue.append("Exception Transfer ID:");
			    	loggerValue.append(c2sTransferVO.getTransferID());
					loggerValue.append(" Exception:" );
			    	loggerValue.append(e.getMessage());
					_log.error(methodName,loggerValue);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]",
							c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
				}
			} else {
				c2sTransferVO.setDifferentialApplicable(PretupsI.NO);
				c2sTransferVO.setDifferentialGiven(PretupsI.NO);
				otherInfo = "No additional taxes found";
				_log.debug(methodName, "No additional taxes found for transfer ID=" + c2sTransferVO.getTransferID());
			}
		} catch (BTSLBaseException be) {
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception e1) {
				_log.errorTrace(methodName, e1);
			}
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception e1) {
				_log.errorTrace(methodName, e1);
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[differentialCalculations]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "differentialCalculations", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} finally {
			// Log the information in Diff Credit Log
			DiffCreditLog.log(c2sTransferVO, otherInfo);
			_log.debug(methodName, " Exiting...");
		}
	}

	/**
	 * Method to fetch the given Differential
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	public ArrayList loadDifferentialCalculationsReversal(Connection con, C2STransferVO c2sTransferVO, String p_module) throws BTSLBaseException {
		final String methodName = "loadDifferentialCalculationsReversal";
		StringBuilder loggerValue= new StringBuilder(); 

		ArrayList itemsList = null;
		try {
			AdjustmentsDAO adjustmentsDAO = new AdjustmentsDAO();
			itemsList = adjustmentsDAO.loadAdditionalCommisionDetails(con, c2sTransferVO);
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" BTSL Exception:");
	    	loggerValue.append(be.getMessage());
			_log.error(methodName,loggerValue);
			throw be;
		} 
		return itemsList;
	}

	/**
	 * Method to calculate Differential and controls the flow of process
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	public int differentialAdjustmentForReversal(Connection con, C2STransferVO c2sTransferVO, ArrayList adjustmentList) throws BTSLBaseException {
		final String METHOD_NAME = "differentialAdjustmentForReversal";	
		int updateCount=0;
		try
		{
			if(adjustmentList.size()==1)
			{
		
			_source = c2sTransferVO.getSourceType();
			_requestedAmount = c2sTransferVO.getRequestedAmount();
			populateAdjustmentCreditforReversal(c2sTransferVO, (AdjustmentsVO) adjustmentList.get(0));
			populateAdjustmentDebitforReversal(c2sTransferVO, (AdjustmentsVO) adjustmentList.get(0));

			if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getDifferentialGiven())) {
				debitUserBalanceForProduct(con, _adjustmentVODebit, c2sTransferVO);
				_adjustmentVODebit.setStockUpdated(PretupsI.YES);
			} else {
				_adjustmentVODebit.setStockUpdated(PretupsI.NO);

			}

			_adjustmentVODebit.setPreviousBalance(c2sTransferVO.getPreviousBalance());
			_adjustmentVODebit.setPostBalance(c2sTransferVO.getPostBalance());
			_itemsList.add(_adjustmentVODebit);
			_itemsList.add(_adjustmentVOCredit);
			
		}else if(adjustmentList.size()==2){
			_source=c2sTransferVO.getSourceType();
			_requestedAmount=c2sTransferVO.getRequestedAmount();
			_adjustmentVODebit=new AdjustmentsVO(); 
			_adjustmentOwnerVOCredit=new AdjustmentsVO();
			_adjustmentOwnerVODebit=new AdjustmentsVO();
			_adjustmentVOCredit=new AdjustmentsVO();
			
			populateAdjustmentforReversalNew(_adjustmentVODebit, c2sTransferVO,(AdjustmentsVO)adjustmentList.get(1),"U",PretupsI.DEBIT,PretupsI.TRANSFER_TYPE_DIFFDR,((AdjustmentsVO)adjustmentList.get(1)).getUserCategory(),((AdjustmentsVO)adjustmentList.get(1)).getUserID());
			populateAdjustmentforReversalNew(_adjustmentOwnerVOCredit, c2sTransferVO,(AdjustmentsVO)adjustmentList.get(1),"O",PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_DIFFCR,PretupsI.OPERATOR_CATEGORY,PretupsI.OPERATOR_USER_TYPE);
			populateAdjustmentforReversalNew(_adjustmentOwnerVODebit, c2sTransferVO,(AdjustmentsVO)adjustmentList.get(0),"D",PretupsI.DEBIT,PretupsI.TRANSFER_TYPE_DIFFDR,((AdjustmentsVO)adjustmentList.get(0)).getUserCategory(),((AdjustmentsVO)adjustmentList.get(0)).getUserID());
			populateAdjustmentforReversalNew(_adjustmentVOCredit, c2sTransferVO,(AdjustmentsVO)adjustmentList.get(0),"N",PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_DIFFCR,PretupsI.OPERATOR_CATEGORY,PretupsI.OPERATOR_USER_TYPE);
			
			if(PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getDifferentialGiven()))
			{
				debitUserBalanceForProduct(con,_adjustmentVODebit,c2sTransferVO);
				long balUser=c2sTransferVO.getPostBalance();
				_adjustmentVODebit.setPostBalance(balUser);
				_adjustmentVODebit.setPreviousBalance((balUser+_adjustmentVODebit.getMarginAmount()));
				_adjustmentVODebit.setStockUpdated(PretupsI.YES);
			
              /*
               Credit to not to be called in Reversal
               creditUserBalanceForProduct(con, _adjustmentOwnerVOCredit, _adjustmentOwnerVOCredit.getUserCategory());
				
				 long balOwner=_userBalancesVO.getPreviousBalance();
				_adjustmentOwnerVOCredit.setPreviousBalance(balOwner);
				balOwner=_userBalancesVO.getBalance();
				_adjustmentOwnerVOCredit.setPostBalance(balOwner);
				_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.YES);
               */
				
				debitUserBalanceForProduct(con,_adjustmentOwnerVODebit,c2sTransferVO);
				long balOwner=c2sTransferVO.getPostBalance();
				_adjustmentOwnerVODebit.setPostBalance(balOwner);
				balOwner=balOwner+_adjustmentOwnerVODebit.getMarginAmount();
				_adjustmentOwnerVODebit.setPreviousBalance(balOwner);
				_adjustmentOwnerVODebit.setStockUpdated(PretupsI.YES);
				
				_adjustmentVOCredit.setPostBalance(0);
				_adjustmentVOCredit.setPreviousBalance(0);
				

				c2sTransferVO.setPostBalance(balUser);
				
			}
			else
			{
				_adjustmentVODebit.setStockUpdated(PretupsI.NO);
				_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.NO);
				_adjustmentOwnerVODebit.setStockUpdated(PretupsI.NO);
				
			}
			_itemsList.add(_adjustmentVODebit);
			_itemsList.add(_adjustmentVOCredit);
			_itemsList.add(_adjustmentOwnerVOCredit);
			_itemsList.add(_adjustmentOwnerVODebit);
			
		}
		c2sTransferVO.setPostBalance(_adjustmentVODebit.getPostBalance());
		c2sTransferVO.setPreviousBalance(_adjustmentVODebit.getPreviousBalance());
		c2sTransferVO.setRetAdjAmt(_adjustmentVODebit.getTransferValue());
		c2sTransferVO.setAdjustmentID(_adjustmentVODebit.getAdjustmentID());
		
			updateCount = _adjustmentsDAO.addAdjustmentEntries(con, _itemsList, c2sTransferVO.getTransferID());
			if(updateCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()) && c2sTransferVO.getUserOTFCountsVO() != null)
			{
				int updateCount1=new UserTransferCountsDAO().updateUserOTFCounts(con, c2sTransferVO.getUserOTFCountsVO());
				if (updateCount1 <= 0) {
					throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION);
				}
			}
			
		} catch (BTSLBaseException be) {
			updateCount = 0;
			throw new BTSLBaseException(be);
		} catch (Exception e) {
			updateCount = 0;
			_log.errorTrace("differentialAdjustmentForReversal", e);
			_log.error("differentialAdjustmentForReversal", "Exception Transfer ID:" + c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
			throw new BTSLBaseException(e);
		}
		return updateCount;
	}

	/**
	 * Populates the Adjustment VO for Debit entry
	 * 
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	private void populateAdjustmentCreditforReversal(C2STransferVO c2sTransferVO, AdjustmentsVO p_AdjustmentsVO) throws BTSLBaseException {
		final String methodName = "populateAdjustmentCreditforReversal";
		try {
			// The adjustment id for the debit would be the refferenced as the
			// transaction id with suffix as N
			_adjustmentCreditID = c2sTransferVO.getTransferID() + PretupsI.SUFIX_ADJUST_TXN_ID_NW;
			_adjustmentVOCredit = new AdjustmentsVO();
			_adjustmentVOCredit.setAdjustmentID(_adjustmentCreditID);
			_adjustmentVOCredit.setAdjustmentDate(BTSLUtil.getTimestampFromUtilDate(c2sTransferVO.getTransferDateTime()));
			_adjustmentVOCredit.setAdjustmentType(PretupsI.TRANSFER_TYPE_DIFFDR);
			_adjustmentVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(_currentDate));
			_adjustmentVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(_currentDate));
			_adjustmentVOCredit.setDifferentialFactor(p_AdjustmentsVO.getDifferentialFactor());
			_adjustmentVOCredit.setEntryType(PretupsI.CREDIT);
			_adjustmentVOCredit.setMarginRate(p_AdjustmentsVO.getMarginRate());
			_adjustmentVOCredit.setMarginAmount(p_AdjustmentsVO.getMarginAmount());
			_adjustmentVOCredit.setAddCommProfileOTFDetailID(p_AdjustmentsVO.getAddCommProfileOTFDetailID());
			_adjustmentVOCredit.setMarginType(p_AdjustmentsVO.getMarginType());
			_adjustmentVOCredit.setCommisssionType(p_AdjustmentsVO.getCommisssionType());
            
			_adjustmentVOCredit.setModule(c2sTransferVO.getModule());
			_adjustmentVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());
			// Add to log user MSISDN on 20/02/2008
			_adjustmentVOCredit.setUserMSISDN(c2sTransferVO.getReceiverMsisdn());

			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			} else {
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			}

			_adjustmentVOCredit.setStockUpdated(PretupsI.NO);
			_adjustmentVOCredit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVOCredit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVOCredit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVOCredit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			_adjustmentVOCredit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			_adjustmentVOCredit.setTax1Rate(p_AdjustmentsVO.getTax1Rate());
			_adjustmentVOCredit.setTax1Type(p_AdjustmentsVO.getTax1Type());
			_adjustmentVOCredit.setTax2Rate(p_AdjustmentsVO.getTax2Rate());
			_adjustmentVOCredit.setTax2Type(p_AdjustmentsVO.getTax2Type());
			_adjustmentVOCredit.setTax1Value(p_AdjustmentsVO.getTax1Value());
			_adjustmentVOCredit.setTax1Value(p_AdjustmentsVO.getTax2Value());
			_adjustmentVOCredit.setTax3Rate(p_AdjustmentsVO.getTax3Rate());
			_adjustmentVOCredit.setTax3Type(p_AdjustmentsVO.getTax3Type());
			_adjustmentVOCredit.setTax3Value(p_AdjustmentsVO.getTax3Value());
			_adjustmentVOCredit.setAddnlCommProfileDetailID(p_AdjustmentsVO.getAddnlCommProfileDetailID());
			_adjustmentVOCredit.setSubService(c2sTransferVO.getSubService());
			_adjustmentVOCredit.setTransferValue(p_AdjustmentsVO.getTransferValue());
			_adjustmentVOCredit.setOtfTypePctOrAMt(p_AdjustmentsVO.getOtfTypePctOrAMt());
			_adjustmentVOCredit.setOtfRate(p_AdjustmentsVO.getOtfRate());
			_adjustmentVOCredit.setOtfAmount(p_AdjustmentsVO.getOtfAmount());
			_adjustmentVOCredit.setCommisssionType(p_AdjustmentsVO.getCommisssionType());
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			StringBuilder loggerValue= new StringBuilder(); 
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[populateAdjustmentCreditforCancallation]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "populateAdjustmentCreditforCancallation", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	/**
	 * Populates the Adjustment VO for Credit entry
	 * 
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	private void populateAdjustmentDebitforReversal(C2STransferVO c2sTransferVO, AdjustmentsVO p_AdjustmentsVO) throws BTSLBaseException {
		final String methodName = "populateAdjustmentDebitforReversal";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered Transfer ID:");
    	loggerValue.append(c2sTransferVO.getTransferID());
		loggerValue.append(" AddnlCommProfileDetailID=" );
    	loggerValue.append(p_AdjustmentsVO.getAddnlCommProfileDetailID());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		try {
			_adjustmentDebitID = c2sTransferVO.getTransferID() + PretupsI.SUFIX_ADJUST_TXN_ID_USER;
			_adjustmentVODebit = new AdjustmentsVO();
			_adjustmentVODebit.setAdjustmentID(_adjustmentDebitID);
			_adjustmentVODebit.setAdjustmentDate( BTSLUtil.getTimestampFromUtilDate(c2sTransferVO.getTransferDateTime()));
			_adjustmentVODebit.setAdjustmentType(PretupsI.TRANSFER_TYPE_DIFFDR);
			_adjustmentVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(_currentDate));
			_adjustmentVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(_currentDate));
			_adjustmentVODebit.setDifferentialFactor(p_AdjustmentsVO.getDifferentialFactor());
			_adjustmentVODebit.setEntryType(PretupsI.DEBIT);
			_adjustmentVODebit.setMarginRate(p_AdjustmentsVO.getMarginRate());
			_adjustmentVODebit.setMarginType(p_AdjustmentsVO.getMarginType());
			_adjustmentVODebit.setModule(c2sTransferVO.getModule());
			_adjustmentVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());
			// Add to log user MSISDN on 20/02/2008
			_adjustmentVODebit.setUserMSISDN(c2sTransferVO.getSenderMsisdn());
			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			} else {
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			}

			_adjustmentVODebit.setStockUpdated(PretupsI.NO);
			_adjustmentVODebit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVODebit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVODebit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVODebit.setUserID(c2sTransferVO.getSenderID());
			_adjustmentVODebit.setUserCategory(((ChannelUserVO) c2sTransferVO.getSenderVO()).getCategoryCode());
			_adjustmentVODebit.setTax1Rate(p_AdjustmentsVO.getTax1Rate());
			_adjustmentVODebit.setTax1Type(p_AdjustmentsVO.getTax1Type());
			_adjustmentVODebit.setTax2Rate(p_AdjustmentsVO.getTax2Rate());
			_adjustmentVODebit.setTax2Type(p_AdjustmentsVO.getTax2Type());
			_adjustmentVODebit.setAddnlCommProfileDetailID(p_AdjustmentsVO.getAddnlCommProfileDetailID());
			_adjustmentVODebit.setAddCommProfileOTFDetailID(p_AdjustmentsVO.getAddCommProfileOTFDetailID());

			_adjustmentVODebit.setSubService(c2sTransferVO.getSubService());
			_adjustmentVODebit.setTransferValue(p_AdjustmentsVO.getTransferValue());
			_adjustmentVODebit.setOtfTypePctOrAMt(p_AdjustmentsVO.getOtfTypePctOrAMt());
			_adjustmentVODebit.setOtfRate(p_AdjustmentsVO.getOtfRate());
			_adjustmentVODebit.setOtfAmount(p_AdjustmentsVO.getOtfAmount());
			_adjustmentVODebit.setCommisssionType(p_AdjustmentsVO.getCommisssionType());
			
		} catch (Exception e) {
			_log.errorTrace(methodName, e); 
			loggerValue.setLength(0);
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[populateAdjustmentCredit]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "populateAdjustmentCredit", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	/**
	 * debit the user for the value if online differential has to be given
	 * 
	 * @param con
	 * @param p_adjustmentsVO
	 * @param p_categoryCode
	 * @throws BTSLBaseException
	 */
	public void debitUserBalanceForProduct(Connection con, AdjustmentsVO p_adjustmentsVO, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "debitUserBalanceForProduct";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered adjustments ID:" + p_adjustmentsVO.getAdjustmentID());
		}
		try {
			_userBalancesVO = prepareUserBalanceVOFromTransferVO(p_adjustmentsVO, PretupsI.TRANSFER_TYPE_DIFFDR, _source, PretupsI.DEBIT, PretupsI.TRANSFER_TYPE_DIFFCR, "");
			_userBalancesDAO.updateUserDailyBalances(con, p_adjustmentsVO.getCreatedOn(), _userBalancesVO);
			int updateCount = 0;
			updateCount = _userBalancesDAO.debitUserBalances(con, _userBalancesVO, ((ChannelUserVO) c2sTransferVO.getSenderVO()).getTransferProfileID(),
					c2sTransferVO.getProductCode(), true, ((ChannelUserVO) c2sTransferVO.getSenderVO()).getCategoryCode());

			if (updateCount <= 0) {
				throw new BTSLBaseException(this, "debitUserBalanceForProduct", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
			}
			c2sTransferVO.setPreviousBalance(_userBalancesVO.getPreviousBalance());
			c2sTransferVO.setPostBalance(_userBalancesVO.getBalance());
			p_adjustmentsVO.setPostBalance(_userBalancesVO.getBalance());
			p_adjustmentsVO.setPreviousBalance(_userBalancesVO.getPreviousBalance());

		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[debitUserBalanceForProduct]",
					p_adjustmentsVO.getAdjustmentID(), "", p_adjustmentsVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "debitUserBalanceForProduct", PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting adjustments ID:" + p_adjustmentsVO.getAdjustmentID());
		}
	}

	/**
	 * 1. Prepares UserBalanceVO from AdjustmentVO and C2STransferVO.
	 * 2. Updates UserDailyBalance if not already.
	 * 3. Credit the Adjustment transfer value in BONUS account.
	 * 
	 * @author birendra.mishra
	 * @param con
	 * @param p_adjustmentsVO
	 * @param p_categoryCode
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public void creditUserBalanceToWallet(Connection con, AdjustmentsVO p_adjustmentsVO, String p_categoryCode, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "creditUserBalanceForProduct";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered adjustments ID : ");
    	loggerValue.append(p_adjustmentsVO.getAdjustmentID());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		try {
			_userBalancesVO = prepareUserBalanceVOFromTransferVO(p_adjustmentsVO, PretupsI.TRANSFER_TYPE_DIFFCR, _source, p_adjustmentsVO.getEntryType(),
					PretupsI.TRANSFER_TYPE_DIFFCR, "");

			/**
			 * Populating some required info from C2STransferVO to
			 * UserBalanceVO.
			 */
			_userBalancesVO.setPdaWalletList(c2sTransferVO.getPdaWalletList());
			_userBalancesVO.setBalance(c2sTransferVO.getTotalBalanceAcrossPDAWallets());
			_userBalancesVO.setPreviousBalance(c2sTransferVO.getTotalPreviousBalanceAcrossPDAWallets());

			_userBalancesDAO.updateUserDailyBalancesForWallets(con, p_adjustmentsVO.getCreatedOn(), _userBalancesVO, c2sTransferVO);
			// Credit the sender
			int updateCount = 0;
			// No need to credit the user in case stock updation is zero as
			// discussed with Sanjay on 06/10/06
			// The condition is changed from greater than 0 to not equal to zero
			// for negative additional commission on 21/04/08

			if (p_adjustmentsVO.getTransferValue() != 0) {
				updateCount = _userBalancesDAO.creditUserBalanceForBonusAcc(con, _userBalancesVO, p_categoryCode);
			} else {
				updateCount = 1;
				_userBalancesVO.setOtherInfo(_userBalancesVO.getOtherInfo() + ", BALANCE UPDATION IS NOT APPLICABLE AS TRANSFER VALUE IS 0");
			}
			if (updateCount <= 0) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
			}
			_creditBackEntryDone = true;
			// Update Previous and Post balances of Reciever
			_adjustmentVOCredit.setPreviousBalance(_userBalancesVO.getPreviousBalance());
			_adjustmentVOCredit.setPostBalance(_userBalancesVO.getBalance());
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			loggerValue.setLength(0);
	    	loggerValue.append("Exception ");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[" + methodName + "]", p_adjustmentsVO
					.getAdjustmentID(), "", p_adjustmentsVO.getNetworkCode(), "Exception : " + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION,e);
		}
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
	    	loggerValue.append("Exiting adjustments ID : ");
	    	loggerValue.append(p_adjustmentsVO.getAdjustmentID());
			_log.debug(methodName,loggerValue);
		}
	}


	public void calculateRoamPenalty(C2STransferVO c2sTransferVO, AdjustmentsVO p_penaltyVODebit, AdjustmentsVO p_penaltyVOCredit, long p_differentialAmount, boolean p_isOwner,String categoryCode) throws BTSLBaseException {
		String methodName = "calculateRoamPenalty";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered"+"p_differentialAmount= ");
    	loggerValue.append(p_differentialAmount);
		long penalty = 0;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}

		ChannelTransferItemsVO channelTransferItemsVO = null;
		ChannelUserVO userVO = null;
		try {
			if (!p_isOwner) {
				userVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
			} else {
				userVO=c2sTransferVO.getOwnerUserVO();
			}
			String commissionProfileSetID = userVO.getCommissionProfileSetID();

			CommissionProfileSetVO commissionProfileSetVO = (CommissionProfileSetVO) CommissionProfileCache.getObject(commissionProfileSetID, new Date());
			String commissionProfileVersion = commissionProfileSetVO.getCommProfileVersion();

			channelTransferItemsVO=(ChannelTransferItemsVO)CommissionProfileMinCache.getObject(commissionProfileSetID, commissionProfileVersion);
			if(!channelTransferItemsVO.getProductCode().equalsIgnoreCase(c2sTransferVO.getProductCode())){
				throw new BTSLBaseException(this , methodName, PretupsErrorCodesI.CHNL_ROAM_COMM_SLAB); 
			}
			long commission = calculatorI.calculateCommission(channelTransferItemsVO.getCommType(), channelTransferItemsVO.getCommRate(), p_differentialAmount);
			int pp = 0;
			if (!p_isOwner) {
				pp = ((Integer) PreferenceCache.getControlPreference(PreferenceI.ROAM_RECHARGE_PENALTY_PERCENTAGE, userVO.getNetworkID(), categoryCode))
						.intValue();
			} else {
				pp = ((Integer) PreferenceCache.getControlPreference(PreferenceI.ROAM_PENALTY_OWNER_PERCENTAGE, userVO.getNetworkID(), categoryCode)).intValue();
				c2sTransferVO.setRoamPenaltyPercentageOwner(pp);
			}
			double calculatedPenalty=((double)pp/100)*commission;
			penalty=Math.round(calculatedPenalty);
			long tax1 = calculatorI.calculatePenaltyTax1(channelTransferItemsVO.getTax1Type(), channelTransferItemsVO.getTax1Rate(), penalty);
			long tax2 = calculatorI.calculatePenaltyTax2(channelTransferItemsVO.getTax2Type(), channelTransferItemsVO.getTax2Rate(), penalty);
			penalty = penalty + tax1 + tax2;
			c2sTransferVO.setRoamPenalty(penalty);
			c2sTransferVO.setRoamPenaltyPercentage(pp);
			c2sTransferVO.setTax1onRoamPenalty(tax1);
			c2sTransferVO.setTax2onRoamPenalty(tax2);
			if (penalty > 0) {
				populateAdjustmentDebitRoam(c2sTransferVO, p_penaltyVODebit, channelTransferItemsVO, p_isOwner);
				populateAdjustmentCreditRoam(c2sTransferVO, p_penaltyVOCredit, channelTransferItemsVO, p_isOwner);
			}
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
		    	loggerValue.append("Exited penalty= ");
		    	loggerValue.append(penalty);
				_log.debug(methodName,loggerValue);
			}

		}catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[" + methodName + "]", c2sTransferVO
					.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}

	}

	public void insertRoamPenaltyAdjustments(Connection con, AdjustmentsVO p_penaltyVODebit, AdjustmentsVO p_penaltyVOCredit) throws BTSLBaseException {

		ArrayList<AdjustmentsVO> penaltyVOList = new ArrayList<AdjustmentsVO>();
		penaltyVOList.add(p_penaltyVODebit);
		penaltyVOList.add(p_penaltyVOCredit);

		int addCount = _adjustmentsDAO.addAdjustmentEntries(con, penaltyVOList, p_penaltyVODebit.getReferenceID());
		if (addCount <= 0) {
			throw new BTSLBaseException(this, "insertRoamPenaltyAdjustments", PretupsErrorCodesI.ERROR_EXCEPTION);
		}
	}

	public void populateAdjustmentDebitRoam(C2STransferVO c2sTransferVO, AdjustmentsVO p_penaltyVODebit, ChannelTransferItemsVO p_channelTransferItemsVO, Boolean p_isOwner) throws BTSLBaseException {
		final String methodName = "populateAdjustmentDebitRoam";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered Transfer ID:");
    	loggerValue.append(c2sTransferVO.getTransferID());
		loggerValue.append(" CommProfileDetailID=");
    	loggerValue.append(p_channelTransferItemsVO.getCommProfileDetailID());
    	loggerValue.append("p_isOwner= ");
    	loggerValue.append(p_isOwner);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}

		p_penaltyVODebit.setAdjustmentDate(_currentDate);
		p_penaltyVODebit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
		p_penaltyVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
		p_penaltyVODebit.setCreatedOn(_currentDate);
		p_penaltyVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
		p_penaltyVODebit.setModifiedOn(_currentDate);
		p_penaltyVODebit.setDifferentialFactor(1);
		p_penaltyVODebit.setEntryType(PretupsI.DEBIT);
		p_penaltyVODebit.setMarginType("PCT");
		p_penaltyVODebit.setModule(c2sTransferVO.getModule());
		p_penaltyVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());
		// Add to log user MSISDN on 20/02/2008
		p_penaltyVODebit.setUserMSISDN(c2sTransferVO.getSenderMsisdn());

		// Roam Recharge CR 000012
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
			p_penaltyVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
		} else {
			p_penaltyVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
		}

		p_penaltyVODebit.setStockUpdated(PretupsI.NO);
		p_penaltyVODebit.setProductCode(c2sTransferVO.getProductCode());
		p_penaltyVODebit.setReferenceID(c2sTransferVO.getTransferID());
		p_penaltyVODebit.setServiceType(c2sTransferVO.getServiceType());

		p_penaltyVODebit.setTax1Rate(p_channelTransferItemsVO.getTax1Rate());
		p_penaltyVODebit.setTax1Type(p_channelTransferItemsVO.getTax1Type());
		p_penaltyVODebit.setTax2Rate(p_channelTransferItemsVO.getTax2Rate());
		p_penaltyVODebit.setTax2Type(p_channelTransferItemsVO.getTax2Type());
		p_penaltyVODebit.setTax2Value(c2sTransferVO.getTax2onRoamPenalty());
		p_penaltyVODebit.setTax3Rate(0);
		p_penaltyVODebit.setTax3Value(0);
		// normal comm here
		p_penaltyVODebit.setAddnlCommProfileDetailID(p_channelTransferItemsVO.getCommProfileDetailID());
		p_penaltyVODebit.setSubService(c2sTransferVO.getSubService());
		p_penaltyVODebit.setCommisssionType(PretupsI.COMMISSION_TYPE_PENALTY);
		p_penaltyVODebit.setMarginAmount((c2sTransferVO.getRoamPenalty() - c2sTransferVO.getTax1onRoamPenalty() - c2sTransferVO.getTax2onRoamPenalty()));
		p_penaltyVODebit.setUserID(PretupsI.OPERATOR_USER_TYPE);
		p_penaltyVODebit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
		p_penaltyVODebit.setMarginRate(c2sTransferVO.getRoamPenaltyPercentage());
		p_penaltyVODebit.setTransferValue(-(c2sTransferVO.getRoamPenalty()));
		p_penaltyVODebit.setTax1Value(c2sTransferVO.getTax1onRoamPenalty());
		p_penaltyVODebit.setTax2Value(c2sTransferVO.getTax2onRoamPenalty());
		p_penaltyVODebit.setPreviousBalance(0);
		p_penaltyVODebit.setPostBalance(0);
		if (!p_isOwner) {

			_adjustmentDebitID = c2sTransferVO.getTransferID() + PretupsI.SUFIX_ADJUST_TXN_ID_NW + "P";
		} else {

			_adjustmentDebitID = c2sTransferVO.getTransferID() + PretupsI.SUFIX_ADJUST_TXN_ID_NW + "OP";
		}
		p_penaltyVODebit.setAdjustmentID(_adjustmentDebitID);
	} 


	public void populateAdjustmentCreditRoam(C2STransferVO c2sTransferVO, AdjustmentsVO p_penaltyVOCredit, ChannelTransferItemsVO p_channelTransferItemsVO, Boolean p_isOwner) throws BTSLBaseException {
		

		p_penaltyVOCredit.setAdjustmentDate(_currentDate);
		p_penaltyVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
		p_penaltyVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
		p_penaltyVOCredit.setCreatedOn(_currentDate);
		p_penaltyVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
		p_penaltyVOCredit.setModifiedOn(_currentDate);
		p_penaltyVOCredit.setDifferentialFactor(1);
		p_penaltyVOCredit.setEntryType(PretupsI.CREDIT);

		p_penaltyVOCredit.setMarginType("PCT");

		p_penaltyVOCredit.setModule(c2sTransferVO.getModule());
		p_penaltyVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());
		// Add to log user MSISDN on 20/02/2008
		p_penaltyVOCredit.setUserMSISDN(c2sTransferVO.getSenderMsisdn());

		// Roam Recharge CR 000012
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
			p_penaltyVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
		} else {
			p_penaltyVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
		}

		p_penaltyVOCredit.setStockUpdated(PretupsI.YES);
		p_penaltyVOCredit.setProductCode(c2sTransferVO.getProductCode());
		p_penaltyVOCredit.setReferenceID(c2sTransferVO.getTransferID());
		p_penaltyVOCredit.setServiceType(c2sTransferVO.getServiceType());

		if (!p_isOwner) {
			_adjustmentCreditID = c2sTransferVO.getTransferID() + PretupsI.SUFIX_ADJUST_TXN_ID_USER + "P";
			p_penaltyVOCredit.setUserID(((ChannelUserVO) (c2sTransferVO.getSenderVO())).getUserID());
			p_penaltyVOCredit.setUserCategory(((ChannelUserVO) (c2sTransferVO.getSenderVO())).getCategoryCode());
		} else {
			p_penaltyVOCredit.setUserID((c2sTransferVO.getOwnerUserVO()).getUserID());
			p_penaltyVOCredit.setUserCategory((c2sTransferVO.getOwnerUserVO()).getCategoryCode());
			_adjustmentCreditID = c2sTransferVO.getTransferID() + PretupsI.SUFIX_ADJUST_TXN_ID_USER + "OP";

		}
		p_penaltyVOCredit.setTax1Rate(p_channelTransferItemsVO.getTax1Rate());
		p_penaltyVOCredit.setTax1Type(p_channelTransferItemsVO.getTax1Type());

		p_penaltyVOCredit.setTax2Rate(p_channelTransferItemsVO.getTax2Rate());
		p_penaltyVOCredit.setTax2Type(p_channelTransferItemsVO.getTax2Type());

		p_penaltyVOCredit.setTax3Rate(0);
		p_penaltyVOCredit.setTax3Value(0);
		// normal comm here
		p_penaltyVOCredit.setAddnlCommProfileDetailID(p_channelTransferItemsVO.getCommProfileDetailID());
		p_penaltyVOCredit.setSubService(c2sTransferVO.getSubService());
		p_penaltyVOCredit.setCommisssionType(PretupsI.COMMISSION_TYPE_PENALTY);
		p_penaltyVOCredit.setMarginAmount((c2sTransferVO.getRoamPenalty() - c2sTransferVO.getTax1onRoamPenalty() - c2sTransferVO.getTax2onRoamPenalty()));
		p_penaltyVOCredit.setMarginRate(c2sTransferVO.getRoamPenaltyPercentage());
		p_penaltyVOCredit.setTransferValue(-(c2sTransferVO.getRoamPenalty()));
		p_penaltyVOCredit.setTax1Value(c2sTransferVO.getTax1onRoamPenalty());
		p_penaltyVOCredit.setTax2Value(c2sTransferVO.getTax2onRoamPenalty());

		p_penaltyVOCredit.setAdjustmentID(_adjustmentCreditID);
	} 


	public ArrayList loadDifferentialCalculationsReversalPenalty(Connection con, C2STransferVO c2sTransferVO, String p_module) throws BTSLBaseException {
		final String methodName = "loadDifferentialCalculationsReversal";
		ArrayList itemsList = null;
		StringBuilder loggerValue= new StringBuilder(); 
		try {
			AdjustmentsDAO adjustmentsDAO = new AdjustmentsDAO();
			itemsList = adjustmentsDAO.loadExtraCommisionDetails(con, c2sTransferVO, PretupsI.COMMISSION_TYPE_PENALTY, PretupsI.CREDIT);
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			loggerValue.setLength(0);
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" BTSL Exception:");
        	loggerValue.append(be.getMessage());
			_log.error(methodName,loggerValue);
			throw new BTSLBaseException(be);
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			throw new BTSLBaseException(e);
		}
		return itemsList;
	}

	public int differentialAdjustmentForReversalPenalty(Connection con, C2STransferVO c2sTransferVO, ArrayList adjustmentList) throws BTSLBaseException{
		final String methodName = "differentialAdjustmentForReversalPenalty";
		int updateCount = 0;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered c2sTransferVO:" + c2sTransferVO);
		}
		try {

			if (adjustmentList.size() == 1) {
				_source = c2sTransferVO.getSourceType();
				_requestedAmount = c2sTransferVO.getRequestedAmount();  
				String adjustmentId=((AdjustmentsVO) adjustmentList.get(0)).getPreviousAdjustmentId();

				if(adjustmentId!=null && adjustmentId.indexOf("UOP")>-1)
				{
					_adjustmentOwnerVOCredit = new AdjustmentsVO();
					_adjustmentOwnerVODebit = new AdjustmentsVO();
					populateAdjustmentforReversalNew(_adjustmentOwnerVODebit, c2sTransferVO, (AdjustmentsVO) adjustmentList.get(0), "UOP", PretupsI.DEBIT,
							PretupsI.TRANSFER_TYPE_DIFFCR, ((AdjustmentsVO) adjustmentList.get(0)).getUserCategory(), ((AdjustmentsVO) adjustmentList.get(0)).getUserID());
					populateAdjustmentforReversalNew(_adjustmentOwnerVOCredit, c2sTransferVO, (AdjustmentsVO) adjustmentList.get(0), "NOP", PretupsI.CREDIT,
							PretupsI.TRANSFER_TYPE_DIFFDR, PretupsI.OPERATOR_CATEGORY, PretupsI.OPERATOR_USER_TYPE);
					if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getDifferentialGiven())) {

						long adjustmentOwnPenalty = _adjustmentOwnerVODebit.getTransferValue();
						_adjustmentOwnerVODebit.setTransferValue(c2sTransferVO.getRoamPenaltyOwner());
						creditUserBalanceForProduct(con, _adjustmentOwnerVODebit, _adjustmentOwnerVODebit.getUserCategory());
						_adjustmentOwnerVODebit.setStockUpdated(PretupsI.YES);
						_adjustmentOwnerVODebit.setTransferValue(adjustmentOwnPenalty);

					} else {
						_adjustmentOwnerVODebit.setStockUpdated(PretupsI.NO);
					}
					_itemsList.add(_adjustmentOwnerVOCredit);
					_itemsList.add(_adjustmentOwnerVODebit);
				}
				else{

					_adjustmentVODebit = new AdjustmentsVO();
					_adjustmentVOCredit = new AdjustmentsVO();
					populateAdjustmentforReversalNew(_adjustmentVODebit, c2sTransferVO, (AdjustmentsVO) adjustmentList.get(0), "UP", PretupsI.DEBIT,
							PretupsI.TRANSFER_TYPE_DIFFCR, ((AdjustmentsVO) adjustmentList.get(0)).getUserCategory(), c2sTransferVO.getSenderID());
					populateAdjustmentforReversalNew(_adjustmentVOCredit, c2sTransferVO, (AdjustmentsVO) adjustmentList.get(0), "NP", PretupsI.CREDIT,
							PretupsI.TRANSFER_TYPE_DIFFDR, PretupsI.OPERATOR_CATEGORY, PretupsI.OPERATOR_USER_TYPE);
					if (PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getDifferentialGiven())) {
						long adjustmentPenalty = _adjustmentVODebit.getTransferValue();
						_adjustmentVODebit.setTransferValue(c2sTransferVO.getRoamPenalty());

						creditUserBalanceForProduct(con, _adjustmentVODebit, _adjustmentVODebit.getUserCategory());
						c2sTransferVO.setPostBalance(_adjustmentVODebit.getPostBalance());
						_adjustmentVODebit.setTransferValue(adjustmentPenalty);
						_adjustmentVODebit.setStockUpdated(PretupsI.YES);
					} else {
						_adjustmentVOCredit.setStockUpdated(PretupsI.NO);

					}
					_itemsList.add(_adjustmentVODebit);
					_itemsList.add(_adjustmentVOCredit);
					c2sTransferVO.setPostBalance(_adjustmentVODebit.getPostBalance());
					c2sTransferVO.setPreviousBalance(_adjustmentVODebit.getPreviousBalance()); 

				}




			} else if (adjustmentList.size() == 2) {
				_source = c2sTransferVO.getSourceType();
				_requestedAmount = c2sTransferVO.getRequestedAmount();
				_adjustmentVODebit = new AdjustmentsVO();
				_adjustmentVOCredit = new AdjustmentsVO();
				_adjustmentOwnerVOCredit = new AdjustmentsVO();
				_adjustmentOwnerVODebit = new AdjustmentsVO();

				populateAdjustmentforReversalNew(_adjustmentVODebit, c2sTransferVO, (AdjustmentsVO) adjustmentList.get(1), "UP", PretupsI.DEBIT,
						PretupsI.TRANSFER_TYPE_DIFFCR, ((AdjustmentsVO) adjustmentList.get(1)).getUserCategory(), ((AdjustmentsVO) adjustmentList.get(1)).getUserID());
				populateAdjustmentforReversalNew(_adjustmentVOCredit, c2sTransferVO, (AdjustmentsVO) adjustmentList.get(1), "NP", PretupsI.CREDIT,
						PretupsI.TRANSFER_TYPE_DIFFDR, PretupsI.OPERATOR_CATEGORY, PretupsI.OPERATOR_USER_TYPE);
				populateAdjustmentforReversalNew(_adjustmentOwnerVODebit, c2sTransferVO, (AdjustmentsVO) adjustmentList.get(0), "UOP", PretupsI.DEBIT,
						PretupsI.TRANSFER_TYPE_DIFFCR, ((AdjustmentsVO) adjustmentList.get(0)).getUserCategory(), ((AdjustmentsVO) adjustmentList.get(0)).getUserID());
				populateAdjustmentforReversalNew(_adjustmentOwnerVOCredit, c2sTransferVO, (AdjustmentsVO) adjustmentList.get(0), "NOP", PretupsI.CREDIT,
						PretupsI.TRANSFER_TYPE_DIFFDR, PretupsI.OPERATOR_CATEGORY, PretupsI.OPERATOR_USER_TYPE);

				long adjustmentPenalty = _adjustmentVODebit.getTransferValue();
				_adjustmentVODebit.setTransferValue(c2sTransferVO.getRoamPenalty());
				creditUserBalanceForProduct(con, _adjustmentVODebit, _adjustmentVODebit.getUserCategory());
				_adjustmentVODebit.setTransferValue(adjustmentPenalty);
				_adjustmentVODebit.setStockUpdated(PretupsI.YES);
				long adjustmentOwnPenalty = _adjustmentOwnerVODebit.getTransferValue();
				_adjustmentOwnerVODebit.setTransferValue(c2sTransferVO.getRoamPenaltyOwner());

				creditUserBalanceForProduct(con, _adjustmentOwnerVODebit, _adjustmentOwnerVODebit.getUserCategory());
				_adjustmentOwnerVODebit.setStockUpdated(PretupsI.YES);
				_adjustmentOwnerVODebit.setTransferValue(adjustmentOwnPenalty);

				_itemsList.add(_adjustmentVOCredit);
				_itemsList.add(_adjustmentVODebit);
				_itemsList.add(_adjustmentOwnerVOCredit);
				_itemsList.add(_adjustmentOwnerVODebit);


			}

			c2sTransferVO.setPostBalance(_adjustmentVODebit.getPostBalance());
			c2sTransferVO.setPreviousBalance(_adjustmentVODebit.getPreviousBalance());
			updateCount = _adjustmentsDAO.addAdjustmentEntries(con, _itemsList, c2sTransferVO.getTransferID());
			if(updateCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()) && c2sTransferVO.getUserOTFCountsVO() != null)
			{
				int updateCount1=new UserTransferCountsDAO().updateUserOTFCounts(con, c2sTransferVO.getUserOTFCountsVO());
				if (updateCount1 <= 0) {
					throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION);
				}
			}
			
		} catch (BTSLBaseException be) {
			updateCount = 0;
			throw new BTSLBaseException(be);
		} catch (Exception e) {
			updateCount = 0;
			_log.errorTrace(methodName, e);
			_log.error(methodName, "Exception Transfer ID:" + c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "differentialAdjustmentForReversalPenalty", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
		return updateCount;
	}
	public int differentialBonusAdjustmentForReversal(Connection con,C2STransferVO c2sTransferVO,ArrayList adjustmentList) throws BTSLBaseException
	{	
		final String methodName = "differentialBonusAdjustmentForReversal";	
		int updateCount=0;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered c2sTransferVO:" + c2sTransferVO +" adjustmentList.size() == "+adjustmentList.size());
		}
		try
		{
			if(adjustmentList.size()==1)
			{
				_source=c2sTransferVO.getSourceType();
				_requestedAmount=c2sTransferVO.getRequestedAmount();
				populateAdjustmentBonusCreditforReversal(c2sTransferVO,(AdjustmentsVO)adjustmentList.get(0));
				populateAdjustmentBonusDebitforReversal(c2sTransferVO,(AdjustmentsVO)adjustmentList.get(0));

				if(PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getDifferentialGiven()))
				{
					debitUserBalanceForProduct(con,_adjustmentVODebit,c2sTransferVO);
					_adjustmentVODebit.setStockUpdated(PretupsI.YES);
				}
				else
				{
					

                 if (_log.isDebugEnabled()) {
                         _log.debug(methodName, "Handling for Bonus Reversal " + _adjustmentVODebit);
                 }
                 if(_adjustmentVODebit!=null && PretupsI.OTF_COMMISSION.equals(_adjustmentVODebit.getCommisssionType()) ) {
                 debitUserBalanceForProduct(con,_adjustmentVODebit,c2sTransferVO);
                 _adjustmentVODebit.setStockUpdated(PretupsI.YES);
                 }
                 else
                 {
					_adjustmentVODebit.setStockUpdated(PretupsI.NO);
                 }


				}

				_adjustmentVODebit.setPreviousBalance(c2sTransferVO.getPreviousBalance());
				_adjustmentVODebit.setPostBalance(c2sTransferVO.getPostBalance());


				_itemsList.add(_adjustmentVODebit);
				_itemsList.add(_adjustmentVOCredit);

			}else if(adjustmentList.size()==2){
				_source=c2sTransferVO.getSourceType();
				_requestedAmount=c2sTransferVO.getRequestedAmount();
				_adjustmentVODebit=new AdjustmentsVO(); 
				_adjustmentOwnerVOCredit=new AdjustmentsVO();
				_adjustmentOwnerVODebit=new AdjustmentsVO();
				_adjustmentVOCredit=new AdjustmentsVO();

				populateAdjustmentBonusforReversalNew(_adjustmentVODebit, c2sTransferVO,(AdjustmentsVO)adjustmentList.get(1),"UB",PretupsI.DEBIT,PretupsI.TRANSFER_TYPE_DIFFDR,((AdjustmentsVO)adjustmentList.get(1)).getUserCategory(),((AdjustmentsVO)adjustmentList.get(1)).getUserID());
				populateAdjustmentBonusforReversalNew(_adjustmentOwnerVOCredit, c2sTransferVO,(AdjustmentsVO)adjustmentList.get(1),"OB",PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_DIFFCR,PretupsI.OPERATOR_CATEGORY,PretupsI.OPERATOR_USER_TYPE);
				populateAdjustmentBonusforReversalNew(_adjustmentOwnerVODebit, c2sTransferVO,(AdjustmentsVO)adjustmentList.get(0),"DB",PretupsI.DEBIT,PretupsI.TRANSFER_TYPE_DIFFDR,((AdjustmentsVO)adjustmentList.get(0)).getUserCategory(),((AdjustmentsVO)adjustmentList.get(0)).getUserID());
				populateAdjustmentBonusforReversalNew(_adjustmentVOCredit, c2sTransferVO,(AdjustmentsVO)adjustmentList.get(0),"NB",PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_DIFFCR,PretupsI.OPERATOR_CATEGORY,PretupsI.OPERATOR_USER_TYPE);


				if(PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getDifferentialGiven()))
				{
					debitUserBalanceForProduct(con,_adjustmentVODebit,c2sTransferVO);
					long balUser=c2sTransferVO.getPostBalance();
					_adjustmentVODebit.setPostBalance(balUser);
					_adjustmentVODebit.setPreviousBalance((balUser+_adjustmentVODebit.getMarginAmount()));
					_adjustmentVODebit.setStockUpdated(PretupsI.YES);

				/*
                    Commented for No Credit Required in Reversal for OwNER
					creditUserBalanceForProduct(con, _adjustmentOwnerVOCredit, _adjustmentOwnerVOCredit.getUserCategory());
					long balOwner=_userBalancesVO.getPreviousBalance();
					_adjustmentOwnerVOCredit.setPreviousBalance(balOwner);
					balOwner=_userBalancesVO.getBalance();
					_adjustmentOwnerVOCredit.setPostBalance(balOwner);
					_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.YES);
                */

					debitUserBalanceForProduct(con,_adjustmentOwnerVODebit,c2sTransferVO);
					long balOwner=c2sTransferVO.getPostBalance();
					_adjustmentOwnerVODebit.setPostBalance(balOwner);
					balOwner=balOwner+_adjustmentOwnerVODebit.getMarginAmount();
					_adjustmentOwnerVODebit.setPreviousBalance(balOwner);
					_adjustmentOwnerVODebit.setStockUpdated(PretupsI.YES);

					_adjustmentVOCredit.setPostBalance(0);
					_adjustmentVOCredit.setPreviousBalance(0);

					c2sTransferVO.setPostBalance(balUser);

				}
				else
				{
					
					if(_adjustmentVODebit!=null && _adjustmentOwnerVODebit!=null &&
							PretupsI.OTF_COMMISSION.equals(_adjustmentVODebit.getCommisssionType()) ) {
	
						debitUserBalanceForProduct(con,_adjustmentVODebit,c2sTransferVO);
						long balUser=c2sTransferVO.getPostBalance();
						_adjustmentVODebit.setPostBalance(balUser);
						_adjustmentVODebit.setPreviousBalance((balUser+_adjustmentVODebit.getMarginAmount()));
						_adjustmentVODebit.setStockUpdated(PretupsI.YES);
	
						debitUserBalanceForProduct(con,_adjustmentOwnerVODebit,c2sTransferVO);
						long balOwner=c2sTransferVO.getPostBalance();
						_adjustmentOwnerVODebit.setPostBalance(balOwner);
						balOwner=balOwner+_adjustmentOwnerVODebit.getMarginAmount();
						_adjustmentOwnerVODebit.setPreviousBalance(balOwner);
						_adjustmentOwnerVODebit.setStockUpdated(PretupsI.YES);
	
						_adjustmentVOCredit.setPostBalance(0);
						_adjustmentVOCredit.setPreviousBalance(0);
	
						c2sTransferVO.setPostBalance(balUser);
					}
					else 
					{
					_adjustmentVODebit.setStockUpdated(PretupsI.NO);
					_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.NO);
					_adjustmentOwnerVODebit.setStockUpdated(PretupsI.NO);
                    }

				}

				_itemsList.add(_adjustmentVODebit);
				_itemsList.add(_adjustmentVOCredit);
				_itemsList.add(_adjustmentOwnerVOCredit);
				_itemsList.add(_adjustmentOwnerVODebit);


			}

			c2sTransferVO.setPostBalance(_adjustmentVODebit.getPostBalance());
			c2sTransferVO.setPreviousBalance(_adjustmentVODebit.getPreviousBalance());
			c2sTransferVO.setRetAdjAmt(_adjustmentVODebit.getTransferValue());
			c2sTransferVO.setAdjustmentID(_adjustmentVODebit.getAdjustmentID());

			updateCount=_adjustmentsDAO.addAdjustmentEntries(con,_itemsList,c2sTransferVO.getTransferID());
			if(updateCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()) && c2sTransferVO.getUserOTFCountsVO() != null)
			{
				int updateCount1=new UserTransferCountsDAO().updateUserOTFCounts(con, c2sTransferVO.getUserOTFCountsVO());
				if (updateCount1 <= 0) {
					throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION);
				}
			}

		}
		catch(BTSLBaseException be)
		{
			updateCount=0;
			throw new BTSLBaseException(be);
		}
		catch(Exception e)
		{
			updateCount=0;
			_log.errorTrace(methodName ,e);
			_log.error(methodName,"Exception Transfer ID:"+c2sTransferVO.getTransferID()+" Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"calculateDifferentialforCancellation",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
		return updateCount;
	}
	private void populateAdjustmentBonusCreditforReversal(C2STransferVO c2sTransferVO,AdjustmentsVO p_AdjustmentsVO) throws BTSLBaseException
	{	
		final String methodName = "populateAdjustmentBonusCreditforReversal";	
		try
		{
			//The adjustment id for the debit would be the refferenced as the transaction id with suffix as N
			_adjustmentCreditID=c2sTransferVO.getTransferID()+PretupsI.SUFIX_ADJUST_TXN_ID_NW+"B";
			_adjustmentVOCredit=new AdjustmentsVO();
			_adjustmentVOCredit.setAdjustmentID(_adjustmentCreditID);
			_adjustmentVOCredit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setCreatedOn(_currentDate);
			_adjustmentVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setModifiedOn(_currentDate);
			_adjustmentVOCredit.setDifferentialFactor(p_AdjustmentsVO.getDifferentialFactor());
			_adjustmentVOCredit.setEntryType(PretupsI.CREDIT);
			_adjustmentVOCredit.setMarginRate(p_AdjustmentsVO.getMarginRate());
			_adjustmentVOCredit.setMarginType(p_AdjustmentsVO.getMarginType());
			_adjustmentVOCredit.setModule(c2sTransferVO.getModule());
			_adjustmentVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());
			//Add to log user MSISDN on 20/02/2008
			_adjustmentVOCredit.setUserMSISDN(c2sTransferVO.getReceiverMsisdn());

			//Roam Recharge CR 000012
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());

			_adjustmentVOCredit.setStockUpdated(PretupsI.NO);
			_adjustmentVOCredit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVOCredit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVOCredit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVOCredit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			_adjustmentVOCredit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			_adjustmentVOCredit.setTax1Rate(p_AdjustmentsVO.getTax1Rate());
			_adjustmentVOCredit.setTax1Type(p_AdjustmentsVO.getTax1Type());
			_adjustmentVOCredit.setTax2Rate(p_AdjustmentsVO.getTax2Rate());
			_adjustmentVOCredit.setTax2Type(p_AdjustmentsVO.getTax2Type());
			_adjustmentVOCredit.setAddnlCommProfileDetailID(p_AdjustmentsVO.getAddnlCommProfileDetailID());
			_adjustmentVOCredit.setSubService(c2sTransferVO.getSubService());
			_adjustmentVOCredit.setTransferValue(p_AdjustmentsVO.getTransferValue());
			_adjustmentVOCredit.setCommisssionType(PretupsI.OTF_COMMISSION);


		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
			_log.error(methodName,"Exception Transfer ID:"+c2sTransferVO.getTransferID()+" Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentCreditforCancallation]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentCreditforCancallation",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	private void populateAdjustmentBonusDebitforReversal(C2STransferVO c2sTransferVO,AdjustmentsVO p_AdjustmentsVO) throws BTSLBaseException
	{	
		final String methodName = "populateAdjustmentBonusDebitforReversal";	
		StringBuilder loggerValue= new StringBuilder();
		try
		{
			//Adjustment id for the credit would be referenced as transaction id with suffix as U
			_adjustmentDebitID=c2sTransferVO.getTransferID()+PretupsI.SUFIX_ADJUST_TXN_ID_USER+"B";
			_adjustmentVODebit=new AdjustmentsVO();
			_adjustmentVODebit.setAdjustmentID(_adjustmentDebitID);
			_adjustmentVODebit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVODebit.setAdjustmentType(PretupsI.TRANSFER_TYPE_DIFFDR);
			_adjustmentVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setCreatedOn(_currentDate);
			_adjustmentVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setModifiedOn(_currentDate);
			_adjustmentVODebit.setDifferentialFactor(p_AdjustmentsVO.getDifferentialFactor());
			_adjustmentVODebit.setEntryType(PretupsI.DEBIT);
			_adjustmentVODebit.setMarginRate(p_AdjustmentsVO.getMarginRate());
			_adjustmentVODebit.setMarginType(p_AdjustmentsVO.getMarginType());
			_adjustmentVODebit.setModule(c2sTransferVO.getModule());
			_adjustmentVODebit.setCommisssionType(PretupsI.OTF_COMMISSION);
			_adjustmentVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());
			//			Add to log user MSISDN on 20/02/2008
			_adjustmentVODebit.setUserMSISDN(c2sTransferVO.getSenderMsisdn());
			//		Roam Recharge CR 000012
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());

			_adjustmentVODebit.setStockUpdated(PretupsI.NO);
			_adjustmentVODebit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVODebit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVODebit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVODebit.setUserID(c2sTransferVO.getSenderID());
			_adjustmentVODebit.setUserCategory(((ChannelUserVO)c2sTransferVO.getSenderVO()).getCategoryCode());
			_adjustmentVODebit.setTax1Rate(p_AdjustmentsVO.getTax1Rate());
			_adjustmentVODebit.setTax1Type(p_AdjustmentsVO.getTax1Type());
			_adjustmentVODebit.setTax2Rate(p_AdjustmentsVO.getTax2Rate());
			_adjustmentVODebit.setTax2Type(p_AdjustmentsVO.getTax2Type());
			_adjustmentVODebit.setAddnlCommProfileDetailID(p_AdjustmentsVO.getAddnlCommProfileDetailID());

			_adjustmentVODebit.setSubService(c2sTransferVO.getSubService());
			_adjustmentVODebit.setTransferValue(p_AdjustmentsVO.getTransferValue());
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentCredit]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentCredit",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}
	private void populateAdjustmentBonusforReversalNew(AdjustmentsVO p_adjustmentDrCr, C2STransferVO c2sTransferVO,AdjustmentsVO p_AdjustmentsVO, String prefix,String p_DrCr,String p_transferTypeDiff,String p_categoryCode, String p_userID) throws BTSLBaseException
	{	
		final String methodName = "populateAdjustmentOwnerDebitforReversal";	
		try
		{

			String _id=c2sTransferVO.getTransferID() + prefix;
			p_adjustmentDrCr.setAdjustmentID(_id);
			p_adjustmentDrCr.setAdjustmentDate(_currentDate);
			p_adjustmentDrCr.setAdjustmentType(p_transferTypeDiff);
			p_adjustmentDrCr.setCreatedBy(PretupsI.SYSTEM_USER);
			p_adjustmentDrCr.setCreatedOn(_currentDate);
			p_adjustmentDrCr.setModifiedBy(PretupsI.SYSTEM_USER);
			p_adjustmentDrCr.setModifiedOn(_currentDate);
			p_adjustmentDrCr.setDifferentialFactor(p_AdjustmentsVO.getDifferentialFactor());
			p_adjustmentDrCr.setEntryType(p_DrCr);
			p_adjustmentDrCr.setMarginRate(p_AdjustmentsVO.getMarginRate());
			p_adjustmentDrCr.setMarginAmount(p_AdjustmentsVO.getMarginAmount());
			p_adjustmentDrCr.setMarginType(p_AdjustmentsVO.getMarginType());
			p_adjustmentDrCr.setModule(c2sTransferVO.getModule());
			p_adjustmentDrCr.setNetworkCode(c2sTransferVO.getNetworkCode());
			p_adjustmentDrCr.setUserMSISDN(c2sTransferVO.getSenderMsisdn());
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				p_adjustmentDrCr.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				p_adjustmentDrCr.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());

			p_adjustmentDrCr.setStockUpdated(PretupsI.NO);
			p_adjustmentDrCr.setProductCode(c2sTransferVO.getProductCode());
			p_adjustmentDrCr.setReferenceID(c2sTransferVO.getTransferID());
			p_adjustmentDrCr.setServiceType(c2sTransferVO.getServiceType());
			p_adjustmentDrCr.setUserID(p_userID);
			p_adjustmentDrCr.setUserCategory(p_categoryCode);
			p_adjustmentDrCr.setTax1Rate(p_AdjustmentsVO.getTax1Rate());
			p_adjustmentDrCr.setTax1Type(p_AdjustmentsVO.getTax1Type());
			p_adjustmentDrCr.setTax2Rate(p_AdjustmentsVO.getTax2Rate());
			p_adjustmentDrCr.setTax2Type(p_AdjustmentsVO.getTax2Type());
			p_adjustmentDrCr.setAddnlCommProfileDetailID(p_AdjustmentsVO.getAddnlCommProfileDetailID());

			p_adjustmentDrCr.setSubService(c2sTransferVO.getSubService());
			p_adjustmentDrCr.setTransferValue(p_AdjustmentsVO.getTransferValue());
			p_adjustmentDrCr.setCommisssionType(PretupsI.OTF_COMMISSION);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
			_log.error(methodName,"Exception Transfer ID:"+c2sTransferVO.getTransferID()+" Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentforReversalNew]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentforReversalNew",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	private void populateAdjustmentforReversalNew(AdjustmentsVO p_adjustmentDrCr, C2STransferVO c2sTransferVO, AdjustmentsVO p_AdjustmentsVO, String prefix, String p_DrCr, String p_transferTypeDiff, String p_categoryCode, String p_userID) throws BTSLBaseException {
		final String methodName = "populateAdjustmentOwnerDebitforReversal";
		try {

			String _id = c2sTransferVO.getTransferID() + prefix;
			p_adjustmentDrCr.setAdjustmentID(_id);
			p_adjustmentDrCr.setAdjustmentDate(BTSLUtil.getTimestampFromUtilDate(_currentDate));
			p_adjustmentDrCr.setAdjustmentType(p_transferTypeDiff);
			p_adjustmentDrCr.setCreatedBy(PretupsI.SYSTEM_USER);
			p_adjustmentDrCr.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(_currentDate));
			p_adjustmentDrCr.setModifiedBy(PretupsI.SYSTEM_USER);
			p_adjustmentDrCr.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(_currentDate));
			p_adjustmentDrCr.setDifferentialFactor(p_AdjustmentsVO.getDifferentialFactor());
			p_adjustmentDrCr.setEntryType(p_DrCr);
			p_adjustmentDrCr.setMarginRate(p_AdjustmentsVO.getMarginRate());
			p_adjustmentDrCr.setMarginAmount(p_AdjustmentsVO.getMarginAmount());
			p_adjustmentDrCr.setMarginType(p_AdjustmentsVO.getMarginType());
			p_adjustmentDrCr.setModule(c2sTransferVO.getModule());
			p_adjustmentDrCr.setNetworkCode(c2sTransferVO.getNetworkCode());
			p_adjustmentDrCr.setUserMSISDN(c2sTransferVO.getSenderMsisdn());
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
				p_adjustmentDrCr.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			} else {
				p_adjustmentDrCr.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			}

			p_adjustmentDrCr.setStockUpdated(PretupsI.NO);
			p_adjustmentDrCr.setProductCode(c2sTransferVO.getProductCode());
			p_adjustmentDrCr.setReferenceID(c2sTransferVO.getTransferID());
			p_adjustmentDrCr.setServiceType(c2sTransferVO.getServiceType());
			p_adjustmentDrCr.setUserID(p_userID);
			p_adjustmentDrCr.setUserCategory(p_categoryCode);
			p_adjustmentDrCr.setTax1Rate(p_AdjustmentsVO.getTax1Rate());
			p_adjustmentDrCr.setTax1Type(p_AdjustmentsVO.getTax1Type());
			p_adjustmentDrCr.setTax2Rate(p_AdjustmentsVO.getTax2Rate());
			p_adjustmentDrCr.setTax2Type(p_AdjustmentsVO.getTax2Type());
			p_adjustmentDrCr.setTax1Value(p_AdjustmentsVO.getTax1Value());
			p_adjustmentDrCr.setTax2Value(p_AdjustmentsVO.getTax2Value());
			p_adjustmentDrCr.setTax3Rate(p_AdjustmentsVO.getTax3Rate());
			p_adjustmentDrCr.setTax3Type(p_AdjustmentsVO.getTax3Type());
			p_adjustmentDrCr.setTax3Value(p_AdjustmentsVO.getTax3Value());
			p_adjustmentDrCr.setAddnlCommProfileDetailID(p_AdjustmentsVO.getAddnlCommProfileDetailID());
			p_adjustmentDrCr.setAddCommProfileOTFDetailID(p_AdjustmentsVO.getAddCommProfileOTFDetailID());
			p_adjustmentDrCr.setSubService(p_AdjustmentsVO.getSubService());
			p_adjustmentDrCr.setTransferValue(p_AdjustmentsVO.getTransferValue());
			p_adjustmentDrCr.setCommisssionType(p_AdjustmentsVO.getCommisssionType());
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			_log.error(methodName, "Exception Transfer ID:" + c2sTransferVO.getTransferID() + " Exception:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[populateAdjustmentforReversalNew]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "populateAdjustmentforReversalNew", PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}
	public void calculateRoamPenaltyReconciliation( C2STransferVO c2sTransferVO,AdjustmentsVO p_penaltyVODebit,AdjustmentsVO p_penaltyVOCredit,boolean p_isOwner) throws BTSLBaseException {
		String methodName = "calculateRoamPenalty";
		long penalty = 0;
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered penaltyDetails= ");
    	loggerValue.append(c2sTransferVO.getPenaltyDetails());
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		ChannelTransferItemsVO channelTransferItemsVO = null;
		int pp=0;
		String  commissionProfileSetID=null;
		String[] penaltyDetails=new String[4];
		penaltyDetails=c2sTransferVO.getPenaltyDetails().split(":");
		try {
			if(!p_isOwner){
				ChannelUserVO userVO= (ChannelUserVO)c2sTransferVO.getSenderVO();
				commissionProfileSetID=userVO.getCommissionProfileSetID();
				pp=Integer.parseInt(penaltyDetails[1]);
			}else{
				commissionProfileSetID=penaltyDetails[3];
				pp=Integer.parseInt(penaltyDetails[2]);
			}

			CommissionProfileSetVO commissionProfileSetVO = (CommissionProfileSetVO) CommissionProfileCache.getObject(commissionProfileSetID, new Date());
			String commissionProfileVersion = commissionProfileSetVO.getCommProfileVersion();

			channelTransferItemsVO=(ChannelTransferItemsVO)CommissionProfileMinCache.getObject(commissionProfileSetID, commissionProfileVersion);
			if(!channelTransferItemsVO.getProductCode().equalsIgnoreCase(c2sTransferVO.getProductCode())){
				throw new BTSLBaseException(this , methodName, PretupsErrorCodesI.CHNL_ROAM_COMM_SLAB); 
			}
			long commission= calculatorI.calculateCommission(channelTransferItemsVO.getCommType(), channelTransferItemsVO.getCommRate(),Long.parseLong(penaltyDetails[0]));

			double calculatedPenalty=((double)pp/100)*commission;
			penalty=Math.round(calculatedPenalty);
			long tax1=calculatorI.calculatePenaltyTax1(channelTransferItemsVO.getTax1Type(), channelTransferItemsVO.getTax1Rate(), penalty);
			long tax2=calculatorI.calculatePenaltyTax2(channelTransferItemsVO.getTax2Type(), channelTransferItemsVO.getTax2Rate(), penalty);
			penalty=penalty+tax1+tax2;
			c2sTransferVO.setRoamPenalty(penalty);
			c2sTransferVO.setRoamPenaltyPercentage(pp);
			c2sTransferVO.setTax1onRoamPenalty(tax1);
			c2sTransferVO.setTax2onRoamPenalty(tax2);
			if(penalty>0){
				populateAdjustmentDebitRoam(c2sTransferVO,p_penaltyVODebit,channelTransferItemsVO,p_isOwner);
				populateAdjustmentCreditRoam(c2sTransferVO,p_penaltyVOCredit,channelTransferItemsVO,p_isOwner);
			}

			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
		    	loggerValue.append("Exited penalty= ");
		    	loggerValue.append(penalty);
				_log.debug(methodName,loggerValue);
			}


		}catch(BTSLBaseException be){
			throw be;
		}
		catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL["+methodName+"]", c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
		} 

	}

	public ArrayList loadDifferentialBonusCalculationsReversal(Connection con,C2STransferVO c2sTransferVO,String p_module) throws BTSLBaseException
	{	
		final String methodName = "loadDifferentialBonusCalculationsReversal";
		StringBuilder loggerValue= new StringBuilder(); 
	
		ArrayList itemsList=null;
		try
		{
			AdjustmentsDAO adjustmentsDAO =new AdjustmentsDAO();
			itemsList = adjustmentsDAO.loadBonusCommisionDetails(con,c2sTransferVO);
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName ,be);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" BTSL Exception:");
	    	loggerValue.append(be.getMessage());
			_log.error(methodName,loggerValue);
			throw new BTSLBaseException(be);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			throw new BTSLBaseException(e);
		}
		return itemsList;
	}
	//PromoVas
	public void differentialCalculationsBonus(C2STransferVO c2sTransferVO,String p_module) throws BTSLBaseException
	{
		StringBuilder loggerValue= new StringBuilder(); 
		final String methodName = "differentialCalculations";
		if (_log.isDebugEnabled())
		{
			_log.debug(methodName, "Entered p_module:"+p_module);
		}
		Connection con=null;
		MComConnectionI mcomCon = null;
		String otherInfo=null;
		StringBuilder otherInfoBuilder=new StringBuilder();
		try
		{
			_source=c2sTransferVO.getSourceType();
			_requestedAmount=Long.parseLong(c2sTransferVO.getCommission());

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			calculateDifferentialBonus(con,c2sTransferVO);
			try
			{
				mcomCon.finalCommit();
				if(PretupsI.YES.equalsIgnoreCase(c2sTransferVO.getCommissionGiven()) && _adjustmentVOCredit.getTransferValue()>0)
				{
					String msISDNOrSID;
					if(c2sTransferVO.getSID() !=null) {
						msISDNOrSID=c2sTransferVO.getSID();
					}
					else {
						msISDNOrSID = c2sTransferVO.getReceiverMsisdn();
					}
					String[] messageArgArray={msISDNOrSID,c2sTransferVO.getTransferID(),PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(_adjustmentVOCredit.getPostBalance()),PretupsBL.getDisplayAmount(_adjustmentVOCredit.getTransferValue()),c2sTransferVO.getReceiverMsisdn(),c2sTransferVO.getTransferValueStr()};
					if(PretupsI.SERVICE_TYPE_PVAS_RECHARGE.equals(c2sTransferVO.getServiceType())) {
						if(c2sTransferVO.getSenderReturnMessage()!=null) {
							c2sTransferVO.setSenderReturnPromoMessage(BTSLUtil.getMessage(((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),PretupsErrorCodesI.PROMO_VAS_ADJUSTMENT_SUCCESS,messageArgArray));
						}
						else {
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),PretupsErrorCodesI.PROMO_VAS_ADJUSTMENT_SUCCESS,messageArgArray));
						}
					} else {
						if(c2sTransferVO.getSenderReturnMessage()!=null) {
							c2sTransferVO.setSenderReturnPromoMessage(BTSLUtil.getMessage(((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),PretupsErrorCodesI.C2S_PROMO_COMMISSION_SUCCESS,messageArgArray));
						}
						else {
							c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),PretupsErrorCodesI.C2S_PROMO_COMMISSION_SUCCESS,messageArgArray));
						}
					}
				}
				otherInfoBuilder.append("Addition Commission=");
				otherInfoBuilder.append(_adjustmentVOCredit.getTransferValue());
				otherInfoBuilder.append(" ID=");
				otherInfoBuilder.append(_adjustmentCreditID);
				otherInfoBuilder.append(" Stock Updated=");
				otherInfoBuilder.append(c2sTransferVO.getCommissionGiven());
				otherInfo=otherInfoBuilder.toString();
				BalanceLogger.log(_userBalancesVO);
			}
			catch (Exception e) 
			{
				_log.errorTrace(methodName ,e);
				loggerValue.setLength(0);
		    	loggerValue.append("Exception Transfer ID:");
		    	loggerValue.append(c2sTransferVO.getTransferID());
				loggerValue.append(" Exception:");
		    	loggerValue.append(e.getMessage());
				_log.error(methodName,loggerValue);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[differentialCalculations]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			}
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName ,be);
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			try
			{
				if(con!=null)
					mcomCon.finalRollback();
			}
			catch(Exception e1){_log.errorTrace(methodName ,e1);}
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
			otherInfoBuilder.setLength(0);
			otherInfoBuilder.append("Not able to calculate Differential for transfer ID=");
			otherInfoBuilder.append(c2sTransferVO.getTransferID());
			otherInfo=otherInfoBuilder.toString();
			try
			{
				if(con!=null)
					mcomCon.finalRollback();
			}
			catch(Exception e1){_log.errorTrace(methodName ,e1);}
			_log.error(methodName,"Exception Transfer ID:"+c2sTransferVO.getTransferID()+" Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[differentialCalculations]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
		}
		finally
		{
			if(mcomCon != null){mcomCon.close("DiffCalBL#differentialCalculationsBonus");mcomCon=null;}
			DiffCreditLog.bonusLog(c2sTransferVO,otherInfo);
		}
	}
	private void calculateDifferentialBonus(Connection con,C2STransferVO c2sTransferVO) throws BTSLBaseException,Exception
	{	
		final String methodName = "calculateDifferentialBonus";	
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered c2sTransferVO:" + c2sTransferVO);
		}
		try
		{
			populateAdjustmentBonusDebit(c2sTransferVO);
			populateAdjustmentBonusCredit(c2sTransferVO);
			String sequenceNo=new CommissionProfileDAO().loadsequenceNo(con, ((ChannelUserVO)c2sTransferVO.getSenderVO()).getCategoryCode());
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !sequenceNo.equalsIgnoreCase(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))) {
				populateAdjustmentOwnerBonusDebit(c2sTransferVO);
				populateAdjustmentOwnerBonusCredit(c2sTransferVO);
			}
			else{
				_adjustmentOwnerVODebit=new AdjustmentsVO();
				_adjustmentOwnerVOCredit=new AdjustmentsVO();
			}
			creditUserBalanceForProduct(con,_adjustmentVOCredit,c2sTransferVO.getCategoryCode());
			c2sTransferVO.setSenderPostBalance(_adjustmentVOCredit.getPostBalance());

			c2sTransferVO.setSenderPostBalance(_adjustmentVOCredit.getPostBalance());
			if((!((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerID().equals(c2sTransferVO.getSenderID())) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !sequenceNo.equalsIgnoreCase(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))){
				debitAndCreditOwnerBalanceForProduct(con,_adjustmentOwnerVODebit, _adjustmentOwnerVOCredit);
			}
			_itemsList.add(_adjustmentVODebit);
			_itemsList.add(_adjustmentVOCredit);
			if((!((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerID().equals(c2sTransferVO.getSenderID())) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !sequenceNo.equalsIgnoreCase(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER)))
			{
				_itemsList.add(_adjustmentOwnerVOCredit);
				_itemsList.add(_adjustmentOwnerVODebit);
			}
			int addCount=_adjustmentsDAO.addAdjustmentEntries(con,_itemsList,c2sTransferVO.getTransferID());
			if(addCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()) && c2sTransferVO.getUserOTFCountsVO() != null)
			{
				c2sTransferVO.setCommissionGiven(PretupsI.NO);
				int updateCount1=new UserTransferCountsDAO().updateUserOTFCounts(con, c2sTransferVO.getUserOTFCountsVO());
				if (updateCount1 <= 0) {
					throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION);
				}
			}
			if(addCount<=0)
			{
				c2sTransferVO.setCommissionGiven(PretupsI.NO);
				throw new BTSLBaseException(this,"calculateDifferential",PretupsErrorCodesI.ERROR_EXCEPTION);
			}
			c2sTransferVO.setCommissionGiven(PretupsI.YES);
		}
		catch(BTSLBaseException be)
		{
			con.rollback();
			_log.errorTrace(methodName ,be);
		}
		catch(Exception e)
		{
			con.rollback();
			_log.errorTrace(methodName ,e);
			_log.error(methodName,"Exception Transfer ID:"+c2sTransferVO.getTransferID()+" Exception:"+e.getMessage());
		}
	}
	private void populateAdjustmentBonusDebit(C2STransferVO c2sTransferVO) throws BTSLBaseException
	{	
		final String methodName = "populateAdjustmentDebit";
		StringBuilder loggerValue= new StringBuilder();
		try
		{	
			_adjustmentDebitID=c2sTransferVO.getTransferID()+PretupsI.SUFIX_ADJUST_TXN_ID_NW+"B";
			_adjustmentVODebit=new AdjustmentsVO();
			_adjustmentVODebit.setAdjustmentID(_adjustmentDebitID);
			_adjustmentVODebit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVODebit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setCreatedOn(_currentDate);
			_adjustmentVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVODebit.setModifiedOn(_currentDate);
			_adjustmentVODebit.setDifferentialFactor(1);
			_adjustmentVODebit.setEntryType(PretupsI.DEBIT);
			_adjustmentVODebit.setMarginRate(0);
			_adjustmentVODebit.setMarginAmount(Long.parseLong(c2sTransferVO.getCommission()));
			_adjustmentVODebit.setTransferValue(Long.parseLong(c2sTransferVO.getCommission()));
			_adjustmentVODebit.setCommisssionType(PretupsI.OTF_COMMISSION);
			_adjustmentVODebit.setMarginType(PretupsI.SYSTEM_AMOUNT);
			_adjustmentVODebit.setModule(c2sTransferVO.getModule());
			_adjustmentVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());
			_adjustmentVODebit.setUserMSISDN(c2sTransferVO.getReceiverMsisdn());
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			_adjustmentVODebit.setStockUpdated(PretupsI.NO);
			_adjustmentVODebit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVODebit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVODebit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVODebit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			_adjustmentVODebit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			_adjustmentVODebit.setTax1Rate(0);
			_adjustmentVODebit.setTax1Type(PretupsI.SYSTEM_AMOUNT);
			_adjustmentVODebit.setTax2Rate(0);
			_adjustmentVODebit.setTax2Type(PretupsI.SYSTEM_AMOUNT);
			_adjustmentVODebit.setAddnlCommProfileDetailID("0");
			_adjustmentVODebit.setSubService(c2sTransferVO.getSubService());
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
			loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentDebit]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentDebit",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}
	private void populateAdjustmentBonusCredit(C2STransferVO c2sTransferVO) throws BTSLBaseException
	{	
		final String methodName = "populateAdjustmentCredit";	
		try
		{
			_adjustmentCreditID=c2sTransferVO.getTransferID()+PretupsI.SUFIX_ADJUST_TXN_ID_USER+"B";
			_adjustmentVOCredit=new AdjustmentsVO();
			_adjustmentVOCredit.setAdjustmentID(_adjustmentCreditID);
			_adjustmentVOCredit.setAdjustmentDate(c2sTransferVO.getTransferDate());
			_adjustmentVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setCreatedOn(_currentDate);
			_adjustmentVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentVOCredit.setModifiedOn(_currentDate);
			_adjustmentVOCredit.setDifferentialFactor(1);
			_adjustmentVOCredit.setEntryType(PretupsI.CREDIT);
			_adjustmentVOCredit.setMarginRate(0);
			_adjustmentVOCredit.setMarginAmount(Long.parseLong(c2sTransferVO.getCommission()));
			_adjustmentVOCredit.setTransferValue(Long.parseLong(c2sTransferVO.getCommission()));
			_adjustmentVOCredit.setCommisssionType(PretupsI.OTF_COMMISSION);
			_adjustmentVOCredit.setStockUpdated(PretupsI.YES);
			_adjustmentVOCredit.setMarginType(PretupsI.SYSTEM_AMOUNT);
			_adjustmentVOCredit.setModule(c2sTransferVO.getModule());
			_adjustmentVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());
			_adjustmentVOCredit.setUserMSISDN(c2sTransferVO.getSenderMsisdn());
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			_adjustmentVOCredit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentVOCredit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentVOCredit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentVOCredit.setUserID(c2sTransferVO.getSenderID());
			_adjustmentVOCredit.setUserCategory(((ChannelUserVO)c2sTransferVO.getSenderVO()).getCategoryCode());
			_adjustmentVOCredit.setTax1Rate(0);
			_adjustmentVOCredit.setTax1Type(PretupsI.SYSTEM_AMOUNT);
			_adjustmentVOCredit.setTax2Rate(0);
			_adjustmentVOCredit.setTax2Type(PretupsI.SYSTEM_AMOUNT);
			_adjustmentVOCredit.setAddnlCommProfileDetailID("0");
			_adjustmentVOCredit.setSubService(c2sTransferVO.getSubService());
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
			StringBuilder loggerValue= new StringBuilder(); 
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentCredit]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentCredit",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}
	private void populateAdjustmentOwnerBonusDebit(C2STransferVO c2sTransferVO) throws BTSLBaseException
	{
		final String methodName="populateAdjustmentOwnerBonusDebit";
		StringBuilder loggerValue= new StringBuilder();
		if(_log.isDebugEnabled())
		{
			loggerValue.setLength(0);
        	loggerValue.append(" Entered Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			_log.debug(methodName,loggerValue);
		}
		try
		{
			_adjustmentOwnerDebitID=c2sTransferVO.getTransferID()+"DB";
			_adjustmentOwnerVODebit=new AdjustmentsVO();
			_adjustmentOwnerVODebit.setAdjustmentID(_adjustmentOwnerDebitID);
			_adjustmentOwnerVODebit.setAdjustmentDate(_currentDate);
			_adjustmentOwnerVODebit.setAdjustmentType(PretupsI.TRANSFER_TYPE_DIFFDR);
			_adjustmentOwnerVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentOwnerVODebit.setCreatedOn(_currentDate);
			_adjustmentOwnerVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentOwnerVODebit.setModifiedOn(_currentDate);
			_adjustmentOwnerVODebit.setDifferentialFactor(1);
			_adjustmentOwnerVODebit.setEntryType(PretupsI.DEBIT);
			_adjustmentOwnerVODebit.setMarginRate(0);
			_adjustmentOwnerVODebit.setMarginAmount(Long.parseLong(c2sTransferVO.getCommission()));
			_adjustmentOwnerVODebit.setTransferValue(Long.parseLong(c2sTransferVO.getCommission()));
			_adjustmentOwnerVODebit.setCommisssionType(PretupsI.OTF_COMMISSION);
			_adjustmentOwnerVODebit.setStockUpdated(PretupsI.YES);
			_adjustmentOwnerVODebit.setMarginType(PretupsI.SYSTEM_AMOUNT);
			_adjustmentOwnerVODebit.setModule(c2sTransferVO.getModule());
			_adjustmentOwnerVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());
		  //_adjustmentOwnerVODebit.setUserID(((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerID());
            _adjustmentOwnerVODebit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			_adjustmentOwnerVODebit.setUserMSISDN(((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerMsisdn());
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentOwnerVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentOwnerVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			_adjustmentOwnerVODebit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentOwnerVODebit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentOwnerVODebit.setServiceType(c2sTransferVO.getServiceType());
		  //_adjustmentOwnerVODebit.setUserCategory(((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerCategoryName());
            _adjustmentOwnerVODebit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			_adjustmentOwnerVODebit.setTax1Rate(0);
			_adjustmentOwnerVODebit.setTax1Type(PretupsI.SYSTEM_AMOUNT);
			_adjustmentOwnerVODebit.setTax2Rate(0);
			_adjustmentOwnerVODebit.setTax2Type(PretupsI.SYSTEM_AMOUNT);
			_adjustmentOwnerVODebit.setAddnlCommProfileDetailID("0");
			_adjustmentOwnerVODebit.setSubService(c2sTransferVO.getSubService());
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName ,e);
			loggerValue.setLength(0);
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentDebit]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
		if(_log.isDebugEnabled())
		{
			loggerValue.setLength(0);
        	loggerValue.append(" Exit: adjustmentVO");
        	loggerValue.append(_adjustmentOwnerVODebit);
			_log.debug(methodName,loggerValue);
		}
	}
	private void populateAdjustmentOwnerBonusCredit(C2STransferVO c2sTransferVO) throws BTSLBaseException
	{
		final String methodName="populateAdjustmentOwnerBonusCredit";
		StringBuilder loggerValue= new StringBuilder(); 
		
		if(_log.isDebugEnabled())
		{
			loggerValue.setLength(0);
	    	loggerValue.append(" Entered Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			_log.debug("populateAdjustmentOwnerCredit",loggerValue);
		}
		try
		{
			_adjustmentOwnerCreditID=c2sTransferVO.getTransferID()+"OB";
			_adjustmentOwnerVOCredit=new AdjustmentsVO();
			_adjustmentOwnerVOCredit.setAdjustmentID(_adjustmentOwnerCreditID);
			_adjustmentOwnerVOCredit.setAdjustmentDate(_currentDate);
			_adjustmentOwnerVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentOwnerVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentOwnerVOCredit.setCreatedOn(_currentDate);
			_adjustmentOwnerVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentOwnerVOCredit.setModifiedOn(_currentDate);
			_adjustmentOwnerVOCredit.setDifferentialFactor(1);
			_adjustmentOwnerVOCredit.setEntryType(PretupsI.CREDIT);
			_adjustmentOwnerVOCredit.setMarginRate(0);
			_adjustmentOwnerVOCredit.setMarginAmount(Long.parseLong(c2sTransferVO.getCommission()));
			_adjustmentOwnerVOCredit.setTransferValue(Long.parseLong(c2sTransferVO.getCommission()));
			_adjustmentOwnerVOCredit.setCommisssionType(PretupsI.OTF_COMMISSION);
			_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.YES);
			_adjustmentOwnerVOCredit.setMarginType(PretupsI.SYSTEM_AMOUNT);
			_adjustmentOwnerVOCredit.setModule(c2sTransferVO.getModule());
			_adjustmentOwnerVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());
			_adjustmentOwnerVOCredit.setUserID(((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerID());
			_adjustmentOwnerVOCredit.setUserMSISDN(((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerMsisdn());
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentOwnerVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentOwnerVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());
			_adjustmentOwnerVOCredit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentOwnerVOCredit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentOwnerVOCredit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentOwnerVOCredit.setUserCategory(((ChannelUserVO)c2sTransferVO.getRequestVO().getSenderVO()).getOwnerCategoryName());
			_adjustmentOwnerVOCredit.setTax1Rate(0);
			_adjustmentOwnerVOCredit.setTax1Type(PretupsI.SYSTEM_AMOUNT);
			_adjustmentOwnerVOCredit.setTax2Rate(0);
			_adjustmentOwnerVOCredit.setTax2Type(PretupsI.SYSTEM_AMOUNT);
			_adjustmentOwnerVOCredit.setAddnlCommProfileDetailID("0");
			_adjustmentOwnerVOCredit.setSubService(c2sTransferVO.getSubService());
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error("populateAdjustmentOwnerCredit",loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentCredit]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentOwnerCredit",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
		if(_log.isDebugEnabled())
		{
			_log.debug("populateAdjustmentOwnerCredit"," Exit: adjustmentVO"+_adjustmentOwnerVOCredit);
		}
	}
	public void debitAndCreditOwnerBalanceForProduct(Connection con, AdjustmentsVO p_adjustmentsOwnerVODebit, AdjustmentsVO p_adjustmentsOwnerVOCredit) throws BTSLBaseException
	{
		final String methodName="debitAndCreditOwnerBalanceForProduct";	
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_adjustmentsOwnerVODebit:");
    	loggerValue.append(p_adjustmentsOwnerVODebit);
		loggerValue.append("Entered p_adjustmentsOwnerVOCredit:");
    	loggerValue.append(p_adjustmentsOwnerVOCredit);
		if(_log.isDebugEnabled())
		{
			_log.debug("debitAndCreditOwnerBalanceForProduct",loggerValue);
		}
		try
		{
			UserBalancesVO _userBalancesVO=prepareUserBalanceVOFromTransferVO(p_adjustmentsOwnerVOCredit,PretupsI.TRANSFER_TYPE_DIFFCR,_source,PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_DIFFCR,"");
			UserBalancesVO _userBalancesDebitVO=prepareUserBalanceVOFromTransferVO(p_adjustmentsOwnerVODebit,PretupsI.TRANSFER_TYPE_DIFFDR,_source,PretupsI.DEBIT,PretupsI.TRANSFER_TYPE_DIFFDR,"");
			_userBalancesDAO.updateUserDailyBalances(con,p_adjustmentsOwnerVOCredit.getCreatedOn(),_userBalancesVO);
			int updateCount=0;
			updateCount=new UserBalancesDAO().diffCreditAndDebitUserBalances(con, _userBalancesDebitVO,_userBalancesVO);

			if(updateCount<=0)
				throw new BTSLBaseException(this, "debitAndCreditOwnerBalanceForProduct",PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
			_adjustmentOwnerVOCredit.setPreviousBalance(_userBalancesVO.getPreviousBalance());
			_adjustmentOwnerVOCredit.setPostBalance(_userBalancesVO.getBalance());

			BalanceLogger.log(_userBalancesVO);
			BalanceLogger.log(_userBalancesDebitVO);
		} 
		catch (BTSLBaseException be)
		{
			_log.errorTrace(methodName, be);
			throw be;
		}
		catch (Exception e)
		{
			loggerValue.setLength(0);
	    	loggerValue.append("Exception ");
	    	loggerValue.append(e.getMessage());
			_log.error("debitAndCreditOwnerBalanceForProduct",loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[debitAndCreditOwnerBalanceForProduct]",p_adjustmentsOwnerVODebit.getAdjustmentID(),"",p_adjustmentsOwnerVODebit.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "debitAndCreditOwnerBalanceForProduct", PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
		}
		if(_log.isDebugEnabled())
		{
			loggerValue.setLength(0);
	    	loggerValue.append("Exiting p_adjustmentsOwnerVODebit:");
	    	loggerValue.append(p_adjustmentsOwnerVODebit);
			loggerValue.append("Entered p_adjustmentsOwnerVOCredit:");
	    	loggerValue.append(p_adjustmentsOwnerVOCredit);
			_log.debug("debitAndCreditOwnerBalanceForProduct",loggerValue);
		}
	}

	//added for owner commision
	private void populateAdjustmentOwnerCredit(Connection con,C2STransferVO c2sTransferVO,AdditionalProfileDeatilsVO additionalProfileDetailsVO) throws BTSLBaseException {
		final String methodName="populateAdjustmentOwnerCredit";	
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append(" Entered Transfer ID:");
    	loggerValue.append(c2sTransferVO.getTransferID());
		if(_log.isDebugEnabled())
		{
			_log.debug("populateAdjustmentOwnerCredit",loggerValue);
		}
		try
		{
			//Adjustment id for the credit would be referenced as transaction id with suffix as U
			_adjustmentOwnerCreditID=c2sTransferVO.getTransferID()+"O";

			_adjustmentOwnerVOCredit=new AdjustmentsVO();
			_adjustmentOwnerVOCredit.setAdjustmentID(_adjustmentOwnerCreditID);

			_adjustmentOwnerVOCredit.setAdjustmentDate(_currentDate);
			_adjustmentOwnerVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentOwnerVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentOwnerVOCredit.setCreatedOn(_currentDate);
			_adjustmentOwnerVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentOwnerVOCredit.setModifiedOn(_currentDate);
			_adjustmentOwnerVOCredit.setDifferentialFactor(additionalProfileDetailsVO.getDiffrentialFactor());
			_adjustmentOwnerVOCredit.setEntryType(PretupsI.CREDIT);
			_adjustmentOwnerVOCredit.setMarginRate(additionalProfileDetailsVO.getAddOwnerCommRate());
			_adjustmentOwnerVOCredit.setMarginType(additionalProfileDetailsVO.getAddOwnerCommType());
			_adjustmentOwnerVOCredit.setModule(c2sTransferVO.getModule());
			_adjustmentOwnerVOCredit.setNetworkCode(c2sTransferVO.getNetworkCode());
			_adjustmentOwnerVOCredit.setUserID(((ChannelUserVO)c2sTransferVO.getSenderVO()).getOwnerID());
			//		Add to log user MSISDN on 20/02/2008
			_adjustmentOwnerVOCredit.setUserMSISDN(((ChannelUserVO)c2sTransferVO.getSenderVO()).getOwnerMsisdn());
			_adjustmentOwnerVOCredit.setUserCategory(((ChannelUserVO)c2sTransferVO.getSenderVO()).getOwnerCategoryName());
			//		Roam Recharge CR 000012
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentOwnerVOCredit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentOwnerVOCredit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());

			_adjustmentOwnerVOCredit.setStockUpdated(PretupsI.NO);
			_adjustmentOwnerVOCredit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentOwnerVOCredit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentOwnerVOCredit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentOwnerVOCredit.setUserCategory(((ChannelUserVO)c2sTransferVO.getSenderVO()).getOwnerCategoryName());
			_adjustmentOwnerVOCredit.setTax1Rate(additionalProfileDetailsVO.getOwnerTax1Rate());
			_adjustmentOwnerVOCredit.setTax1Type(additionalProfileDetailsVO.getOwnerTax1Type());
			_adjustmentOwnerVOCredit.setTax2Rate(additionalProfileDetailsVO.getOwnerTax2Rate());
			_adjustmentOwnerVOCredit.setTax2Type(additionalProfileDetailsVO.getOwnerTax2Type());
			_adjustmentOwnerVOCredit.setAddnlCommProfileDetailID(additionalProfileDetailsVO.getAddCommProfileDetailID());
			_adjustmentOwnerVOCredit.setSubService(c2sTransferVO.getSubService());
			_adjustmentOwnerVOCredit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error("populateAdjustmentOwnerCredit",loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentCredit]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentOwnerCredit",PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
		if(_log.isDebugEnabled())
		{
			loggerValue.setLength(0);
	    	loggerValue.append(" Exit: adjustmentVO");
	    	loggerValue.append(_adjustmentOwnerVOCredit);
			_log.debug("populateAdjustmentOwnerCredit",loggerValue);
		}
	}

	//added for owner commision
	private void populateAdjustmentOwnerDebit(Connection con,C2STransferVO c2sTransferVO,AdditionalProfileDeatilsVO additionalProfileDetailsVO) throws BTSLBaseException {
		final String methodName="populateAdjustmentOwnerDebit";
		if(_log.isDebugEnabled())
		{
			_log.debug(methodName," Entered Transfer ID:"+c2sTransferVO.getTransferID());
		}
		try
		{
			//The adjustment id for the debit would be the refferenced as the transaction id with suffix as N
			_adjustmentOwnerDebitID=c2sTransferVO.getTransferID()+"NO";
			_adjustmentOwnerVODebit=new AdjustmentsVO();
			_adjustmentOwnerVODebit.setAdjustmentID(_adjustmentOwnerDebitID);
			_adjustmentOwnerVODebit.setAdjustmentDate(_currentDate);
			_adjustmentOwnerVODebit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			_adjustmentOwnerVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			_adjustmentOwnerVODebit.setCreatedOn(_currentDate);
			_adjustmentOwnerVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			_adjustmentOwnerVODebit.setModifiedOn(_currentDate);
			_adjustmentOwnerVODebit.setDifferentialFactor(additionalProfileDetailsVO.getDiffrentialFactor());
			_adjustmentOwnerVODebit.setEntryType(PretupsI.DEBIT);
			_adjustmentOwnerVODebit.setMarginRate(additionalProfileDetailsVO.getAddOwnerCommRate());
			_adjustmentOwnerVODebit.setMarginType(additionalProfileDetailsVO.getAddOwnerCommType());
			_adjustmentOwnerVODebit.setModule(c2sTransferVO.getModule());
			_adjustmentOwnerVODebit.setNetworkCode(c2sTransferVO.getNetworkCode());
			_adjustmentOwnerVODebit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			_adjustmentOwnerVODebit.setUserCategory(PretupsI.OPERATOR_CATEGORY);

			//		Add to log user MSISDN on 20/02/2008
			_adjustmentOwnerVODebit.setUserMSISDN(((ChannelUserVO)c2sTransferVO.getSenderVO()).getOwnerMsisdn());
			//		Roam Recharge CR 000012
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				_adjustmentOwnerVODebit.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
			else
				_adjustmentOwnerVODebit.setNetworkCodeFor(c2sTransferVO.getReceiverNetworkCode());

			_adjustmentOwnerVODebit.setStockUpdated(PretupsI.NO);
			_adjustmentOwnerVODebit.setProductCode(c2sTransferVO.getProductCode());
			_adjustmentOwnerVODebit.setReferenceID(c2sTransferVO.getTransferID());
			_adjustmentOwnerVODebit.setServiceType(c2sTransferVO.getServiceType());
			_adjustmentOwnerVODebit.setTax1Rate(additionalProfileDetailsVO.getOwnerTax1Rate());
			_adjustmentOwnerVODebit.setTax1Type(additionalProfileDetailsVO.getOwnerTax1Type());
			_adjustmentOwnerVODebit.setTax2Rate(additionalProfileDetailsVO.getOwnerTax2Rate());
			_adjustmentOwnerVODebit.setTax2Type(additionalProfileDetailsVO.getOwnerTax2Type());
			_adjustmentOwnerVODebit.setAddnlCommProfileDetailID(additionalProfileDetailsVO.getAddCommProfileDetailID());
			_adjustmentOwnerVODebit.setSubService(c2sTransferVO.getSubService());
			_adjustmentOwnerVODebit.setCommisssionType(PretupsI.NORMAL_COMMISSION);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			StringBuilder loggerValue= new StringBuilder(); 
			loggerValue.setLength(0);
        	loggerValue.append("Exception Transfer ID:");
        	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
        	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCalBL[populateAdjustmentDebit]",c2sTransferVO.getTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
		if(_log.isDebugEnabled())
		{
			_log.debug(methodName," Exit: adjustmentVO"+_adjustmentOwnerVODebit);
		}

	}

	/**
	 * Method to calculate OTF and controls the flow of process
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	private void calculateOTF( C2STransferVO c2sTransferVO, AdditionalProfileDeatilsVO additionalProfileDetailsVO) throws BTSLBaseException {
		final String methodName = "calculateOTF";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered c2sTransferVO:");
    	loggerValue.append(c2sTransferVO);
		loggerValue.append("p_additionalProfileDetails:");
    	loggerValue.append(additionalProfileDetailsVO);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,loggerValue);
		}
		try {

			long otfAmount;

			otfAmount = calculatorI.calculateOTFComm(additionalProfileDetailsVO.getOtfTypePctOrAMt(), additionalProfileDetailsVO.getOtfRate(), c2sTransferVO.getRequestedAmount());

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "OTF amount" + otfAmount);
			}
			if (PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(additionalProfileDetailsVO.getOtfTypePctOrAMt())) {

				_adjustmentVOCredit.setOtfTypePctOrAMt(PretupsI.AMOUNT_TYPE_AMOUNT);
				_adjustmentVODebit.setOtfTypePctOrAMt(PretupsI.AMOUNT_TYPE_AMOUNT);

			}
			else if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(additionalProfileDetailsVO.getOtfTypePctOrAMt()))
			{
				_adjustmentVOCredit.setOtfTypePctOrAMt(PretupsI.AMOUNT_TYPE_PERCENTAGE);
				_adjustmentVODebit.setOtfTypePctOrAMt(PretupsI.AMOUNT_TYPE_PERCENTAGE);

			}
			_adjustmentVOCredit.setOtfAmount(otfAmount);
			_adjustmentVODebit.setOtfAmount(otfAmount);
			_adjustmentVOCredit.setOtfRate(additionalProfileDetailsVO.getOtfRate());
			_adjustmentVODebit.setOtfRate(additionalProfileDetailsVO.getOtfRate());
			_adjustmentVOCredit.setAddCommProfileOTFDetailID(additionalProfileDetailsVO.getAddCommProfileOTFDetailID());
			_adjustmentVODebit.setAddCommProfileOTFDetailID(additionalProfileDetailsVO.getAddCommProfileOTFDetailID());

		}
		catch(BTSLBaseException be)
		{
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			throw new BTSLBaseException(be);
		}
		catch(Exception e)
		{
			c2sTransferVO.setDifferentialGiven(PretupsI.NO);
			_log.errorTrace(methodName ,e);
			loggerValue.setLength(0);
	    	loggerValue.append("Exception Transfer ID:");
	    	loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" Exception:");
	    	loggerValue.append(e.getMessage());
			_log.error(methodName,loggerValue);
			throw new BTSLBaseException(e);
		}
	}

	public void calculateUserOTFCounts(Connection con, C2STransferVO c2sTransferVO, AdditionalProfileDeatilsVO adnlprfdtlvo) throws BTSLBaseException {
		final String methodName = "calculateUserOTFCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered C2STransferVO= ");
			loggerValue.append(c2sTransferVO);
			_log.debug(methodName,loggerValue );
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		UserOTFCountsVO userOTFCountsVO1= null;
		boolean timeFlag=true;

		UserOTFCountsVO userOTFCountsVO = new UserOTFCountsVO();
		AdditionalProfileDeatilsVO otfDetailsVO = null;
		List<AdditionalProfileDeatilsVO> otfSlabList;
		try {    
			DateFormat df = new SimpleDateFormat(PretupsI.DATE_FORMAT);
			Date dateobj = new Date();
			Date currentDate = df.parse(df.format(dateobj));

			if(PretupsI.YES.equals(c2sTransferVO.getDifferentialAllowedForService()))
			{
				if(adnlprfdtlvo!=null)
				{
					c2sTransferVO.setTargetAchieved(false);
					Date tempDate=currentDate;
					tempDate = DateUtils.truncate(tempDate, Calendar.DATE);
				
					if(!(adnlprfdtlvo.getOtfApplicableFrom()== null && adnlprfdtlvo.getOtfApplicableTo()==null))
					{
						if(!((adnlprfdtlvo.getOtfApplicableFrom().before(currentDate) 
								|| adnlprfdtlvo.getOtfApplicableFrom().equals(currentDate)) && (adnlprfdtlvo.getOtfApplicableTo().after(currentDate) 
										|| adnlprfdtlvo.getOtfApplicableTo().equals(currentDate))))
						{
							timeFlag = false;
						}
					}


					if(!BTSLUtil.isNullString(adnlprfdtlvo.getOtfTimeSlab()))
					{
						if(!BTSLUtil.timeRangeValidation(adnlprfdtlvo.getOtfTimeSlab(), new Date()))
						{
							timeFlag=false;
						}
					}
					
					boolean otfFlag=true;
					if(adnlprfdtlvo.getOtfApplicableFrom()== null && adnlprfdtlvo.getOtfApplicableTo()==null && timeFlag){
						otfFlag=false;
					}
					
					if(timeFlag && otfFlag)
					{
						boolean order= true;
						boolean addnl= true;
						String[] msg = null;
						otfSlabList= commissionProfileDAO.getAddCommOtfDetails(con, adnlprfdtlvo.getAddCommProfileDetailID(), order);
						
						if(!otfSlabList.isEmpty())
						{
							userOTFCountsVO = userTransferCountsDAO.loadUserOTFCounts(con, c2sTransferVO.getSenderID(),adnlprfdtlvo.getAddCommProfileDetailID(), addnl);
							if(userOTFCountsVO!=null)
							{
								boolean flag = true; 
								userOTFCountsVO1 = new UserOTFCountsVO();
								otfDetailsVO = new AdditionalProfileDeatilsVO();
								for (AdditionalProfileDeatilsVO adnlprfOTFSlabVO : otfSlabList)
								{
									if(PretupsI.OTF_TYPE_COUNT.equals(adnlprfdtlvo.getOtfType()))
									{
										if (userOTFCountsVO.getOtfCount() + 1 >= adnlprfOTFSlabVO.getOtfValue())
										{flag = false; 
											userOTFCountsVO1.setAdnlComOTFDetailId(adnlprfOTFSlabVO.getAddCommProfileOTFDetailID());
											userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
											userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
										}
										if (userOTFCountsVO.getOtfCount() >= adnlprfOTFSlabVO.getOtfValue())
										{flag = false; 
											userOTFCountsVO1.setAdnlComOTFDetailId(adnlprfOTFSlabVO.getAddCommProfileOTFDetailID());
											userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
											userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
											otfDetailsVO =  adnlprfOTFSlabVO;
										}
										if(userOTFCountsVO.getOtfCount() < adnlprfOTFSlabVO.getOtfValue()  && adnlprfOTFSlabVO.getOtfValue()  <= userOTFCountsVO.getOtfCount() + 1){
											c2sTransferVO.setTargetAchieved(true);
											msg = new String[2];
											msg[0]=adnlprfdtlvo.getOtfType()+":"+((PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfdtlvo.getOtfType()))?PretupsBL.getDisplayAmount(adnlprfOTFSlabVO.getOtfValue()):adnlprfOTFSlabVO.getOtfValue());
											msg[1]=adnlprfOTFSlabVO.getOtfTypePctOrAMt()+":"+((PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfOTFSlabVO.getOtfTypePctOrAMt()))?PretupsBL.getDisplayAmount(adnlprfOTFSlabVO.getOtfRate()):adnlprfOTFSlabVO.getOtfRate());
										}
									}
									if(PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfdtlvo.getOtfType()))
									{
										if (userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount() >= adnlprfOTFSlabVO.getOtfValue())
										{flag = false; 
											userOTFCountsVO1.setAdnlComOTFDetailId(adnlprfOTFSlabVO.getAddCommProfileOTFDetailID());
											userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
											userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
										}
										if (userOTFCountsVO.getOtfValue() >= adnlprfOTFSlabVO.getOtfValue())
										{flag = false; 
											userOTFCountsVO1.setAdnlComOTFDetailId(adnlprfOTFSlabVO.getAddCommProfileOTFDetailID());
											userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
											userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
											otfDetailsVO =  adnlprfOTFSlabVO;
										}
										if(userOTFCountsVO.getOtfValue() < adnlprfOTFSlabVO.getOtfValue()  && adnlprfOTFSlabVO.getOtfValue()  <= userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount()){
											c2sTransferVO.setTargetAchieved(true);
											msg = new String[2];
											msg[0]=adnlprfdtlvo.getOtfType()+":"+((PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfdtlvo.getOtfType()))?PretupsBL.getDisplayAmount(adnlprfOTFSlabVO.getOtfValue()):adnlprfOTFSlabVO.getOtfValue());
											msg[1]=adnlprfOTFSlabVO.getOtfTypePctOrAMt()+":"+((PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfOTFSlabVO.getOtfTypePctOrAMt()))?PretupsBL.getDisplayAmount(adnlprfOTFSlabVO.getOtfRate()):adnlprfOTFSlabVO.getOtfRate());
										}
									}
								}
								if(flag){
									userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
									userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
									userOTFCountsVO1.setAdnlComOTFDetailId(userOTFCountsVO.getAdnlComOTFDetailId());
								}
								if(otfDetailsVO!=null){
									calculateOTF( c2sTransferVO, otfDetailsVO);
									long valueAfterOTF= _adjustmentVOCredit.getTransferValue() + _adjustmentVOCredit.getOtfAmount();
									c2sTransferVO.setTotalCommission(valueAfterOTF);
									_adjustmentVODebit.setTransferValue(valueAfterOTF);
									_adjustmentVOCredit.setTransferValue(valueAfterOTF);
									_adjustmentVOCredit.setPostBalance(_adjustmentVOCredit.getPostBalance() + _adjustmentVOCredit.getOtfAmount());
								}
							}
							else
							{
								userOTFCountsVO1 = new UserOTFCountsVO();
								userOTFCountsVO1.setOtfCount(1);
								userOTFCountsVO1.setOtfValue(c2sTransferVO.getRequestedAmount());
								for (AdditionalProfileDeatilsVO adnlprfOTFSlabVO : otfSlabList)
								{
									if(PretupsI.OTF_TYPE_COUNT.equals(adnlprfdtlvo.getOtfType()))
									{
										if (1 == adnlprfOTFSlabVO.getOtfValue()){
											userOTFCountsVO1.setAdnlComOTFDetailId(adnlprfOTFSlabVO.getAddCommProfileOTFDetailID());
											c2sTransferVO.setTargetAchieved(true);
											msg = new String[2];
											msg[0]=adnlprfdtlvo.getOtfType()+":"+((PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfdtlvo.getOtfType()))?PretupsBL.getDisplayAmount(adnlprfOTFSlabVO.getOtfValue()):adnlprfOTFSlabVO.getOtfValue());
											msg[1]=adnlprfOTFSlabVO.getOtfTypePctOrAMt()+":"+((PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfOTFSlabVO.getOtfTypePctOrAMt()))?PretupsBL.getDisplayAmount(adnlprfOTFSlabVO.getOtfRate()):adnlprfOTFSlabVO.getOtfRate());

										}
									}
									if(PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfdtlvo.getOtfType()))
									{
										if (c2sTransferVO.getRequestedAmount() >= adnlprfOTFSlabVO.getOtfValue()){
											userOTFCountsVO1.setAdnlComOTFDetailId(adnlprfOTFSlabVO.getAddCommProfileOTFDetailID());
											c2sTransferVO.setTargetAchieved(true);
											msg = new String[2];
											msg[0]=adnlprfdtlvo.getOtfType()+":"+((PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfdtlvo.getOtfType()))?PretupsBL.getDisplayAmount(adnlprfOTFSlabVO.getOtfValue()):adnlprfOTFSlabVO.getOtfValue());
											msg[1]=adnlprfOTFSlabVO.getOtfTypePctOrAMt()+":"+((PretupsI.OTF_TYPE_AMOUNT.equals(adnlprfOTFSlabVO.getOtfTypePctOrAMt()))?PretupsBL.getDisplayAmount(adnlprfOTFSlabVO.getOtfRate()):adnlprfOTFSlabVO.getOtfRate());

										}
									}
								}
								if(userOTFCountsVO1.getAdnlComOTFDetailId()==null){
									userOTFCountsVO1.setAdnlComOTFDetailId(((AdditionalProfileDeatilsVO)otfSlabList.get(0)).getAddCommProfileOTFDetailID());
								}
							}
						}
						if(userOTFCountsVO1 != null){
							userOTFCountsVO1.setAddnl(addnl);
							userOTFCountsVO1.setUserID(c2sTransferVO.getSenderID());
							c2sTransferVO.setUserOTFCountsVO(userOTFCountsVO1);
							c2sTransferVO.setOtfApplicable(PretupsI.YES);
						}
						if(c2sTransferVO.isTargetAchieved()){
							 PushMessage pushMessage =new PushMessage(c2sTransferVO.getSenderMsisdn(), new BTSLMessages(PretupsErrorCodesI.TARGET_BASED_CAC_MESSAGES,msg), "", "", new Locale(c2sTransferVO.getLanguage(), c2sTransferVO.getCountry()),"");
							 pushMessage.push();
						}
					}
				}
			}

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			_log.error(methodName,  loggerValue);
			
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[calculateUserOTFCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exited...");
		}
	}
}
