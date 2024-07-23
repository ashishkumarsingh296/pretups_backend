/**
Oin * @(#)ChannelTransferBL.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            avinash.kamthan Aug 8, 2005 Initital Creation
 *                            Sandeep Goel Nov 10,2005 Modification,
 *                            customization
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.time.DateUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

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
import com.btsl.kafka.PretupsKafkaProducerBL;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingCache;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.NetworkStockLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LMSProfileCache;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetDetailsLMSVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.transfer.businesslogic.TransferDAO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.redis.util.RedisActivityLog;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.icu.util.Calendar;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.btsl.user.businesslogic.UserLoanVO;

/**
 * @author avinash.kamthan
 * 
 * @version $Revision: 1.0 $
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ChannelTransferBL {

	/**
	 * Field log.
	 */
	private static Log log = LogFactory.getLog(ChannelTransferBL.class.getName());
	private final static ObjectMapper mapper = new ObjectMapper();
	private static int _transactionIDCounter = 0;
	private static int _prevMinut = 0;

	private static int _transactionIDCtrC2C_withoutCon = 0;
	private static int _C2C_ReturnIDCtr_withoutCon = 0;
	private static int _C2C_withdraw_withoutCon = 0;

	private static int _O2C_TransferIDCtr_voms = 0;
	private static int _O2C_TransferIDCtr = 0;
	private static int _prevMinut_TransferID_voms = 0;
	private static int _prevMinut_TransferID = 0;

	private static int _O2C_ReturnIDCtr = 0;
	private static int _prevMinut_ReturnID = 0;
	private static int _O2C_WithdrawIDCtr = 0;
	private static int _prevMinut_WithdrawID = 0;

	private static int _prevMinut_genC2C_withoutCon = 0;
	private static int _prevMinut_return_withoutCon = 0;
	private static int _prevMinut_withdraw_withoutCon = 0;

	private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
	private static int MAX_COUNTER = 9999;
	private static boolean isNoSlabIns = false;
	private static String hkeytransferId = "transferId";
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
	private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
	private static Boolean trfIdCheck =false;

	/**
	 * To genrate the operator to channel transfer id
	 */
	public static OperatorUtilI calculatorI = null;
	// calculate the tax
	static {
		final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
		} catch (Exception e) {
			log.errorTrace(ChannelTransferBL.class, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}
	
	private ChannelTransferBL(){
	   
   }

	/**
	 * Method genrateTransferID.
	 * 
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 * @throws BTSLBaseException
	 */
	public static synchronized void genrateTransferID(ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "genrateTransferID";
		String minut2Compare = null;
		Date mydate = null;
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ChannelTransferVO = " + channelTransferVO);
		}
		boolean voucherTrackingAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_TRACKING_ALLOWED);
		try {
			if (PretupsI.VOUCHER_PRODUCT_O2C.equalsIgnoreCase(channelTransferVO.getProductType()) && voucherTrackingAllowed) {
				mydate = new Date();
				channelTransferVO.setCreatedOn(mydate);
				minut2Compare = _sdfCompare.format(mydate);
				final int currentMinut = Integer.parseInt(minut2Compare);
				if (currentMinut != _prevMinut_TransferID_voms) {
					_O2C_TransferIDCtr_voms = 1;
					_prevMinut_TransferID_voms = currentMinut;
				} else if (_O2C_TransferIDCtr_voms >= 65535) {
					_O2C_TransferIDCtr_voms = 1;
				} else {
					_O2C_TransferIDCtr_voms++;
				}
				if (_O2C_TransferIDCtr_voms == 0) {
					throw new BTSLBaseException("ChannelTransferBL", "genrateTransferID", PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
				}
				channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.CHANNEL_TRANSFER_O2C_VOMS_ID, _O2C_TransferIDCtr_voms));
			} else {
				mydate = new Date();
				channelTransferVO.setCreatedOn(mydate);
				minut2Compare = _sdfCompare.format(mydate);
				final int currentMinut = Integer.parseInt(minut2Compare);
				if (currentMinut != _prevMinut_TransferID) {
					_O2C_TransferIDCtr = 1;
					_prevMinut_TransferID = currentMinut;
				} else if (_O2C_TransferIDCtr >= 65535) {
					_O2C_TransferIDCtr = 1;
				} else {
					_O2C_TransferIDCtr++;
				}
				if (_O2C_TransferIDCtr == 0) {
					throw new BTSLBaseException("ChannelTransferBL", "genrateTransferID", PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
				}
				channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.CHANNEL_TRANSFER_O2C_ID, _O2C_TransferIDCtr));

			}

		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}

	}

	/**
	 * Genrate the operator to channel return ID
	 * 
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	public static synchronized void genrateReturnID(ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "genrateReturnID";
		String minut2Compare = null;
		Date mydate = null;

		try {

			mydate = new Date();
			channelTransferVO.setCreatedOn(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);
			if (currentMinut != _prevMinut_ReturnID) {
				_O2C_ReturnIDCtr = 1;
				_prevMinut_ReturnID = currentMinut;
			} else if (_O2C_ReturnIDCtr >= 65535) {
				_O2C_ReturnIDCtr = 1;
			} else {
				_O2C_ReturnIDCtr++;
			}
			if (_O2C_ReturnIDCtr == 0) {
				throw new BTSLBaseException("ChannelTransferBL", "genrateReturnID", PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
			}
			channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.CHANNEL_RETURN_O2C_ID, _O2C_ReturnIDCtr));

		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}

	}


	/**
	 * Genrate the operator to channel withdraw ID
	 * 
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	public static synchronized void genrateWithdrawID(ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "genrateWithdrawID";
		String minut2Compare = null;
		Date mydate = null;
		try {
			mydate = new Date();
			channelTransferVO.setCreatedOn(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);
			if (currentMinut != _prevMinut_WithdrawID) {
				_O2C_WithdrawIDCtr = 1;
				_prevMinut_WithdrawID = currentMinut;
			} else if (_O2C_WithdrawIDCtr >= 65535) {
				_O2C_WithdrawIDCtr = 1;
			} else {
				_O2C_WithdrawIDCtr++;
			}
			if (_O2C_WithdrawIDCtr == 0) {
				throw new BTSLBaseException("ChannelTransferBL", "genrateWithdrawID", PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
			}
			channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.CHANNEL_WITHDRAW_O2C_ID, _O2C_WithdrawIDCtr));

		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}
	}



	/**
	 * Genrate the channel to Channel transfer ID
	 * 
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	public static synchronized void genrateChnnlToChnnlTrfID(ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		// Changes added on 28-01-2015 to change format of C2C Transferid
		String minut2Compare = null;
		Date mydate = null;
		final String methodName = "genrateChnnlToChnnlTrfID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ChannelTransferVO = " + channelTransferVO);
		}

		try {
			mydate = new Date();
			channelTransferVO.setCreatedOn(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);

			if (currentMinut != _prevMinut_genC2C_withoutCon) {
				_transactionIDCtrC2C_withoutCon = 1;
				_prevMinut_genC2C_withoutCon = currentMinut;
			} else if (_transactionIDCtrC2C_withoutCon >= 65535) {
				_transactionIDCtrC2C_withoutCon = 1;
			} else {
				_transactionIDCtrC2C_withoutCon++;
			}
			if (_transactionIDCtrC2C_withoutCon == 0) {
				throw new BTSLBaseException("ChannelTransferBL", "genrateChnnlToChnnlTrfID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}

			channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID,
					_transactionIDCtrC2C_withoutCon));

		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}
		// changes ended
	}

	/**
	 * Genrate channel to channel return id
	 * 
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	public static synchronized void genrateChnnlToChnnlReturnID(ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "genrateChnnlToChnnlReturnID";
		// Changes added to change format of C2C ReturnID
		String minut2Compare = null;
		Date mydate = null;
		if (log.isDebugEnabled()) {
			log.debug("genrateChnnlToChnnlReturnID", "Entered ChannelTransferVO =" + channelTransferVO);
		}

		try {
			mydate = new Date();
			channelTransferVO.setCreatedOn(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);

			if (currentMinut != _prevMinut_return_withoutCon) {
				_C2C_ReturnIDCtr_withoutCon = 1;
				_prevMinut_return_withoutCon = currentMinut;
			} else if (_C2C_ReturnIDCtr_withoutCon >= 65535) {
				_C2C_ReturnIDCtr_withoutCon = 1;
			} else {
				_C2C_ReturnIDCtr_withoutCon++;
			}
			if (_C2C_ReturnIDCtr_withoutCon == 0) {
				throw new BTSLBaseException("ChannelTransferBL", "genrateChnnlToChnnlReturnID", PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
			}

			channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.CHANNEL_TO_CHANNEL_RETURN_ID, _C2C_ReturnIDCtr_withoutCon));

		}

		catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}

	}

	/**
	 * Genrate the chanel to channel withdraw id
	 * 
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	public static void genrateChnnlToChnnlWithdrawID(ChannelTransferVO channelTransferVO) throws BTSLBaseException {

		final String methodName = "genrateChnnlToChnnlWithdrawID";
		String minut2Compare = null;
		Date mydate = null;
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ChannelTransferVO = " + channelTransferVO);
		}

		try {
			mydate = new Date();
			channelTransferVO.setCreatedOn(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);

			if (currentMinut != _prevMinut_withdraw_withoutCon) {
				_C2C_withdraw_withoutCon = 1;
				_prevMinut_withdraw_withoutCon = currentMinut;
			} else if (_C2C_withdraw_withoutCon >= 65535) {
				_C2C_withdraw_withoutCon = 1;
			} else {
				_C2C_withdraw_withoutCon++;
			}
			if (_C2C_withdraw_withoutCon == 0) {
				throw new BTSLBaseException("ChannelTransferBL", "genrateChnnlToChnnlWithdrawID", PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
			}

			channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.CHANNEL_TO_WITHDRAW_RETURN_ID, _C2C_withdraw_withoutCon));

		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}
	}

	/**
	 * update the Network Stock Transaction
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param userID
	 * @param curDate
	 * @return int
	 * @throws BTSLBaseException
	 */
	public static int updateNetworkStockTransactionDetails(Connection con, ChannelTransferVO channelTransferVO, String userID, Date curDate) throws BTSLBaseException {
		final String methodName = "updateNetworkStockTransactionDetails";
		StringBuilder loggerValue= new StringBuilder(); 
		
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", USER ID = ");
			loggerValue.append(userID);
			loggerValue.append(", Curdate = ");
			loggerValue.append(curDate);
			log.debug(methodName,  loggerValue );
		}
		int updateCount = 0;

		final NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
		networkStockTxnVO.setNetworkCode(channelTransferVO.getNetworkCode());
		networkStockTxnVO.setNetworkFor(channelTransferVO.getNetworkCodeFor());
		if (channelTransferVO.getNetworkCode().equals(channelTransferVO.getNetworkCodeFor())) {
			networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
		} else {
			networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
		}
		networkStockTxnVO.setReferenceNo(channelTransferVO.getReferenceNum());
		networkStockTxnVO.setTxnDate(channelTransferVO.getModifiedOn());
		networkStockTxnVO.setRequestedQuantity(channelTransferVO.getRequestedQuantity());

		networkStockTxnVO.setInitiaterRemarks(channelTransferVO.getChannelRemarks());
		networkStockTxnVO.setFirstApprovedRemarks(channelTransferVO.getFirstApprovalRemark());
		networkStockTxnVO.setSecondApprovedRemarks(channelTransferVO.getSecondApprovalRemark());
		networkStockTxnVO.setFirstApprovedBy(channelTransferVO.getFirstApprovedBy());
		networkStockTxnVO.setSecondApprovedBy(channelTransferVO.getSecondApprovedBy());
		networkStockTxnVO.setFirstApprovedOn(channelTransferVO.getFirstApprovedOn());
		networkStockTxnVO.setSecondApprovedOn(channelTransferVO.getSecondApprovedOn());
		networkStockTxnVO.setCancelledBy(channelTransferVO.getCanceledBy());
		networkStockTxnVO.setCancelledOn(channelTransferVO.getCanceledOn());
		networkStockTxnVO.setCreatedBy(userID);
		networkStockTxnVO.setCreatedOn(curDate);
		networkStockTxnVO.setModifiedOn(curDate);
		networkStockTxnVO.setModifiedBy(userID);
		// for mali---- +ve commision apply
		networkStockTxnVO.setTax3value(channelTransferVO.getTotalTax3());

		networkStockTxnVO.setTxnStatus(channelTransferVO.getStatus());
		networkStockTxnVO.setTxnNo(NetworkStockBL.genrateStockTransctionID(con, networkStockTxnVO));
		channelTransferVO.setReferenceID(networkStockTxnVO.getTxnNo());

		if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
			networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
			networkStockTxnVO.setTxnType(PretupsI.DEBIT);
		} else if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(channelTransferVO.getTransferType())) {
			networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_RETURN);
			networkStockTxnVO.setTxnType(PretupsI.CREDIT);
		}

		networkStockTxnVO.setInitiatedBy(userID);
		networkStockTxnVO.setFirstApproverLimit(channelTransferVO.getFirstApproverLimit());
		networkStockTxnVO.setUserID(channelTransferVO.getFromUserID());
		networkStockTxnVO.setTxnMrp(channelTransferVO.getTransferMRP());

		final ArrayList list = channelTransferVO.getChannelTransferitemsVOList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkStockTxnItemsVO networkItemsVO = null;

		final ArrayList arrayList = new ArrayList();
		int j = 1;
		long approvedQty = 0L;
		for (int i = 0, k = list.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) list.get(i);

			networkItemsVO = new NetworkStockTxnItemsVO();
			networkItemsVO.setSNo(j++);
			networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
			networkItemsVO.setRequiredQuantity(channelTransferItemsVO.getRequiredQuantity());
			networkItemsVO.setApprovedQuantity(channelTransferItemsVO.getApprovedQuantity());
			networkItemsVO.setMrp(channelTransferItemsVO.getApprovedQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));
			networkItemsVO.setAmount(channelTransferItemsVO.getPayableAmount());
			// Added on 07/02/08
			networkItemsVO.setDateTime(curDate);

			if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
				networkItemsVO.setStock(channelTransferItemsVO.getAfterTransSenderPreviousStock());
			} else {
				networkItemsVO.setStock(channelTransferItemsVO.getAfterTransReceiverPreviousStock());
			}

			networkItemsVO.setProductCode(channelTransferItemsVO.getProductCode());
			approvedQty += channelTransferItemsVO.getApprovedQuantity();
			arrayList.add(networkItemsVO);
		}
		networkStockTxnVO.setNetworkStockTxnItemsList(arrayList);
		networkStockTxnVO.setApprovedQuantity(approvedQty);
		// changed becoz in first txn network stock debit by requested quantity
		// then commision will be deducted in
		// second transaction
		if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
			networkStockTxnVO.setTxnMrp(channelTransferVO.getRequestedQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));
		} else {
			networkStockTxnVO.setTxnMrp(channelTransferVO.getTransferMRP());
		}

		final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
		// call the dao to update the newtorkstoock tarnsaction
		networkStockTxnVO.setTxnWallet(channelTransferVO.getWalletType());
		networkStockTxnVO.setRefTxnID(channelTransferVO.getTransferID());
		updateCount = networkStockDAO.addNetworkStockTransaction(con, networkStockTxnVO);

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited updateCount = ");
			loggerValue.append(updateCount);
			log.debug(methodName,  loggerValue);
		}

		return updateCount;
	}

	/**
	 * It Preapre the network stock from channel transfer vo and debit the
	 * network stock
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param userID
	 * @param p_modifiedDate
	 * @param p_isDebit
	 *            boolean
	 * @return int
	 * @throws BTSLBaseException
	 */
	public static int prepareNetworkStockListAndCreditDebitStock(Connection con, ChannelTransferVO channelTransferVO, String userID, Date p_modifiedDate, boolean p_isDebit) throws BTSLBaseException {
		final String methodName = "prepareNetworkStockListAndCreditDebitStock";
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Entered ChannelTransferVO = " + channelTransferVO + ", User ID = " + userID + ", Date = " + p_modifiedDate + ", isDebit = " + p_isDebit);
		}

		int updateCount = 0;
		boolean multipleWalletApply = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
		final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
		final ArrayList networkStockList = new ArrayList();
		final ArrayList itemList = channelTransferVO.getChannelTransferitemsVOList();
		if (log.isDebugEnabled()) {
			log.debug(methodName,
					"Entered itemList.size() = " + itemList.size() + ", User ID = " + userID + ", Date = " + p_modifiedDate + ", isDebit = " + p_isDebit);
		}
		ChannelTransferItemsVO itemsVO = null;
		NetworkStockVO networkStocksVO = null;
		for (int i = 0, k = itemList.size(); i < k; i++) {
			networkStocksVO = new NetworkStockVO();
			itemsVO = (ChannelTransferItemsVO) itemList.get(i);

			networkStocksVO.setProductCode(itemsVO.getProductCode());
			networkStocksVO.setNetworkCode(channelTransferVO.getNetworkCode());
			networkStocksVO.setNetworkCodeFor(channelTransferVO.getNetworkCodeFor());
			networkStocksVO.setLastTxnNum(channelTransferVO.getTransferID());
			networkStocksVO.setLastTxnBalance(itemsVO.getApprovedQuantity());
			networkStocksVO.setWalletBalance(itemsVO.getApprovedQuantity());
			networkStocksVO.setLastTxnType(channelTransferVO.getTransferType());
			networkStocksVO.setModifiedBy(userID);
			networkStocksVO.setModifiedOn(p_modifiedDate);
			if (!multipleWalletApply) {
				networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
			} else {
				networkStocksVO.setWalletType(channelTransferVO.getWalletType());
			}
			networkStockList.add(networkStocksVO);
		}
		if(networkStocksVO!=null)
		{
			updateCount = networkStockDAO.updateNetworkDailyStock(con, networkStocksVO);

			if (p_isDebit) {
				updateCount = networkStockDAO.debitNetworkStock(con, networkStockList);
			} else {
				updateCount = networkStockDAO.creditNetworkStock(con, networkStockList);
			}
		}
		/*
		 * Set the network previous stock on channel transfer items vo
		 */
		for (int i = 0, k = networkStockList.size(); i < k; i++) {
			networkStocksVO = (NetworkStockVO) networkStockList.get(i);
			itemsVO = (ChannelTransferItemsVO) itemList.get(i);
			/**
			 * Note:
			 * getting the sender items previous stock by adding ordered n/w
			 * stock with n/w previous stock.
			 * 
			 * Debit case: PreviousN/WSTOCK = totalstcok - requested stock (
			 * refer debit methgod of NetworkStockDao )
			 * Credit Case: PreviousN/WSTOCK = totalstcok ( refer credit method
			 * of NetworkStockDao )
			 * 
			 */
			if (p_isDebit) {
				itemsVO.setAfterTransSenderPreviousStock(networkStocksVO.getPreviousBalance() + networkStocksVO.getWalletbalance());
				itemsVO.setSenderPreviousStock(networkStocksVO.getPreviousBalance() + networkStocksVO.getWalletbalance());
			} else {
				itemsVO.setAfterTransReceiverPreviousStock(networkStocksVO.getPreviousBalance());
				itemsVO.setReceiverPreviousStock(networkStocksVO.getPreviousBalance());
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited updateCount = " + updateCount);
		}
		return updateCount;
	}

	/**
	 * Check the Transfer in counts of the user against his profile whcih is
	 * defined
	 * earlier while registeration.
	 * 
	 * If his limit for transfer in count is over than, he is not allowed to
	 * transfer further
	 * 
	 * @param con
	 * @param userID
	 * @param profileID
	 * @param networkCode
	 * @param isLockForUpdate
	 * @param curDate
	 *            Date
	 * @param p_totalRequestedQtuantity
	 *            long
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public static String checkTransferINCounts(Connection con, String userID, String profileID, String networkCode, boolean isLockForUpdate, Date curDate, long p_totalRequestedQtuantity) throws BTSLBaseException {
		final String methodName = "checkTransferINCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered profileID = " );
			loggerValue.append(profileID);
			loggerValue.append(", User ID = ");
			loggerValue.append(userID);
			loggerValue.append(", curDate = ");
			loggerValue.append(curDate);
			loggerValue.append(", p_totalRequestedQtuantity = ");
			loggerValue.append(p_totalRequestedQtuantity);
			log.debug(methodName,loggerValue) ;
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

		UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, userID, isLockForUpdate);
		/*
		 * This situation will accour in the case of the first Transaction of
		 * the user
		 */
		if (userTransferCountsVO == null) {
			userTransferCountsVO = new UserTransferCountsVO();
		}

		checkResetCountersAfterPeriodChange(userTransferCountsVO, curDate);
		final String countMessage = transferInCountsCheck(con, userTransferCountsVO, profileID, networkCode, p_totalRequestedQtuantity);
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited = ");
			loggerValue.append(countMessage);
			log.debug(methodName, loggerValue);
		}

		return countMessage;
	}

	/**
	 * Check the transfer in counts and value of the user against the defined
	 * value in tranhsfer profile
	 * 
	 * @param con
	 * @param p_userTransferCountsVO
	 * @param profileID
	 * @param networkCode
	 * @param p_totalRequestedQtuantity
	 *            long
	 * @return String
	 * @throws BTSLBaseException
	 */
	public static String transferInCountsCheck(Connection con, UserTransferCountsVO p_userTransferCountsVO, String profileID, String networkCode, long p_totalRequestedQtuantity) throws BTSLBaseException {
		final String methodName = "transferInCountsCheck";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered UserTransferCountsVO = ");
			loggerValue.append(p_userTransferCountsVO);
			loggerValue.append(", profileID ");
			loggerValue.append(profileID);
			loggerValue.append(", networkCode = ");
			loggerValue.append(networkCode);
			loggerValue.append(", p_totalRequestedQtuantity : ");
			loggerValue.append(p_totalRequestedQtuantity);
			log.debug(methodName,loggerValue );
		}

		/*
		 * Now load the transferProfileVO, which contains the LEAST/GREATEST (as
		 * per required) values form
		 * USER LEVEL PROFILE and CATEGORY LEVEL PROFILE.
		 */
		final TransferProfileVO transferProfileVO = TransferProfileCache.getTransferProfileDetails(profileID, networkCode);
		if (transferProfileVO.getDailyInCount() <= p_userTransferCountsVO.getDailyInCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_DAILY_IN_COUNT;
		} else if (transferProfileVO.getDailyInValue() < (p_userTransferCountsVO.getDailyInValue() + p_totalRequestedQtuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_DAILY_IN_VALUE;
		} else if (transferProfileVO.getWeeklyInCount() <= p_userTransferCountsVO.getWeeklyInCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_WEEKLY_IN_COUNT;
		} else if (transferProfileVO.getWeeklyInValue() < (p_userTransferCountsVO.getWeeklyInValue() + p_totalRequestedQtuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_WEEKLY_IN_VALUE;
		} else if (transferProfileVO.getMonthlyInCount() <= p_userTransferCountsVO.getMonthlyInCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_MONTHLY_IN_COUNT;
		} else if (transferProfileVO.getMonthlyInValue() < (p_userTransferCountsVO.getMonthlyInValue() + p_totalRequestedQtuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_MONTHLY_IN_VALUE;
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
		return null;
	}

	/**
	 * Check the user transfer out side counts
	 * 
	 * @param con
	 * @param userID
	 * @param profileID
	 * @param networkCode
	 * @param isLockForUpdate
	 * @param curDate
	 *            Date
	 * @param p_totalRequestedQuantity
	 *            long
	 * @return String
	 * @throws BTSLBaseException
	 */
	public static String checkTransferOutCounts(Connection con, String userID, String profileID, String networkCode, boolean isLockForUpdate, Date curDate, long p_totalRequestedQuantity) throws BTSLBaseException {
		final String methodName = "checkTransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered profileID = ");
			loggerValue.append(profileID);
			loggerValue.append(", User ID = ");
			loggerValue.append(userID);
			loggerValue.append(", curDate : ");
			loggerValue.append(curDate);
			loggerValue.append(", p_totalRequestedQuantity = ");
			loggerValue.append(p_totalRequestedQuantity);
			log.debug(methodName,loggerValue );
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

		UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, userID, isLockForUpdate);
		/*
		 * This situation will accour in the case of the first Transaction of
		 * the user
		 */
		if (userTransferCountsVO == null) {
			userTransferCountsVO = new UserTransferCountsVO();
		}
		checkResetCountersAfterPeriodChange(userTransferCountsVO, curDate);
		final String countMessage = transferOutCountsCheck(con, userTransferCountsVO, profileID, networkCode, p_totalRequestedQuantity);
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited = ");
			loggerValue.append(countMessage);
			log.debug(methodName, countMessage);
		}
		return countMessage;
	}

	/**
	 * Check the transfer out counts and value of the user against the defined
	 * value in tranhsfer profile
	 * 
	 * @param con
	 * @param p_userTransferCountsVO
	 * @param profileID
	 * @param networkCode
	 * @param p_totalRequestedQuantity
	 *            long
	 * @return String
	 * @throws BTSLBaseException
	 */
	private static String transferOutCountsCheck(Connection con, UserTransferCountsVO p_userTransferCountsVO, String profileID, String networkCode, long p_totalRequestedQuantity) throws BTSLBaseException {
		final String methodName = "transferOutCountsCheck";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered UserTransferCountsVO = ");
			loggerValue.append(p_userTransferCountsVO);
			loggerValue.append(", profileID ");
			loggerValue.append(profileID);
			loggerValue.append(", networkCode  ");
			loggerValue.append(networkCode);
			loggerValue.append(", p_totalRequestedQuantity : ");
			loggerValue.append(p_totalRequestedQuantity);
			log.debug(methodName,loggerValue );
		}

		/*
		 * Now load the transferProfileVO, which contains the LEAST/GREATEST (as
		 * per required) values form
		 * USER LEVEL PROFILE and CATEGORY LEVEL PROFILE.
		 */
		final TransferProfileVO transferProfileVO = TransferProfileCache.getTransferProfileDetails(profileID, networkCode);
		if (transferProfileVO.getDailyOutCount() <= p_userTransferCountsVO.getDailyOutCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_DAILY_OUT_COUNT;
		} else if (transferProfileVO.getDailyOutValue() < (p_userTransferCountsVO.getDailyOutValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_DAILY_OUT_VALUE;
		} else if (transferProfileVO.getWeeklyOutCount() <= p_userTransferCountsVO.getWeeklyOutCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_WEEKLY_OUT_COUNT;
		} else if (transferProfileVO.getWeeklyOutValue() < (p_userTransferCountsVO.getWeeklyOutValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_WEEKLY_OUT_VALUE;
		} else if (transferProfileVO.getMonthlyOutCount() <= p_userTransferCountsVO.getMonthlyOutCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_MONTHLY_OUT_COUNT;
		} else if (transferProfileVO.getMonthlyOutValue() < (p_userTransferCountsVO.getMonthlyOutValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_MONTHLY_OUT_VALUE;
		}
		return null;
	}

	/**
	 * Check the Transfer IN counts for Outside Hierarchy
	 * 
	 * @param con
	 * @param userID
	 * @param profileID
	 * @param networkCode
	 * @param isLockForUpdate
	 * @param curDate
	 *            Date
	 * @param p_totalRequestedQuantity
	 *            long
	 * @return String
	 * @throws BTSLBaseException
	 */
	public static String checkOutsideTransferINCounts(Connection con, String userID, String profileID, String networkCode, boolean isLockForUpdate, Date curDate, long p_totalRequestedQuantity) throws BTSLBaseException {
		final String methodName = "checkOutsideTransferINCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered profileID = ");
			loggerValue.append(profileID);
			loggerValue.append("User ID = ");
			loggerValue.append(userID);
			loggerValue.append(", NetworkCode = ");
			loggerValue.append(networkCode);
			loggerValue.append(", curDate = ");
			loggerValue.append(curDate);
			loggerValue.append(", p_totalRequestedQuantity = ");
			loggerValue.append(p_totalRequestedQuantity);
			log.debug(methodName,loggerValue);
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

		UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, userID, isLockForUpdate);
		/*
		 * This situation will accour in the case of the first Transaction of
		 * the user
		 */
		if (userTransferCountsVO == null) {
			userTransferCountsVO = new UserTransferCountsVO();
		}
		checkResetCountersAfterPeriodChange(userTransferCountsVO, curDate);
		final String countMessage = outsideTransferINCounts(con, userTransferCountsVO, profileID, networkCode, p_totalRequestedQuantity);
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited = ");
			loggerValue.append(countMessage);
			log.debug(methodName,  loggerValue);
		}

		return countMessage;
	}

	/**
	 * Check the transfer outside in counts and value of the user against the
	 * defined value in tranhsfer profile
	 * 
	 * @param con
	 * @param p_userTransferCountsVO
	 * @param profileID
	 * @param networkCode
	 * @param p_totalRequestedQuantity
	 *            long
	 * @return String
	 * @throws BTSLBaseException
	 */
	private static String outsideTransferINCounts(Connection con, UserTransferCountsVO p_userTransferCountsVO, String profileID, String networkCode, long p_totalRequestedQuantity) throws BTSLBaseException {
		final String methodName = "outsideTransferINCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered UserTransferCountsVO = ");
			loggerValue.append(p_userTransferCountsVO);
			loggerValue.append(", profileID " );
			loggerValue.append(profileID);
			loggerValue.append(", networkCode = ");
			loggerValue.append(networkCode);
			loggerValue.append(", p_totalRequestedQuantity : ");
			loggerValue.append(p_totalRequestedQuantity);
			log.debug(methodName,loggerValue );
		}

		/*
		 * Now load the transferProfileVO, which contains the LEAST/GREATEST (as
		 * per required) values form
		 * USER LEVEL PROFILE and CATEGORY LEVEL PROFILE.
		 */
		final TransferProfileVO transferProfileVO = TransferProfileCache.getTransferProfileDetails(profileID, networkCode);
		if (transferProfileVO.getUnctrlDailyInCount() <= p_userTransferCountsVO.getUnctrlDailyInCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_DAILY_IN_COUNT;
		} else if (transferProfileVO.getUnctrlDailyInValue() < (p_userTransferCountsVO.getUnctrlDailyInValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_DAILY_IN_VALUE;
		} else if (transferProfileVO.getUnctrlWeeklyInCount() <= p_userTransferCountsVO.getUnctrlWeeklyInCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_WEEKLY_IN_COUNT;
		} else if (transferProfileVO.getUnctrlWeeklyInValue() < (p_userTransferCountsVO.getUnctrlWeeklyInValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_WEEKLY_IN_VALUE;
		} else if (transferProfileVO.getUnctrlMonthlyInCount() <= p_userTransferCountsVO.getUnctrlMonthlyInCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_MONTHLY_IN_COUNT;
		} else if (transferProfileVO.getUnctrlMonthlyInValue() < (p_userTransferCountsVO.getUnctrlMonthlyInValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_MONTHLY_IN_VALUE;
		}
		return null;
	}

	/**
	 * Check the Transfer OUT counts for Outside Hierarchy
	 * 
	 * @param con
	 * @param userID
	 * @param profileID
	 * @param networkCode
	 * @param isLockForUpdate
	 * @param curDate
	 *            Date
	 * @param p_totalRequestedQuantity
	 *            long
	 * @return String
	 * @throws BTSLBaseException
	 */
	public static String checkOutsideTransferOutCounts(Connection con, String userID, String profileID, String networkCode, boolean isLockForUpdate, Date curDate, long p_totalRequestedQuantity) throws BTSLBaseException {
		final String methodName = "checkOutsideTransferOutCounts";
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Entered profileID = " + profileID + ", User ID = " + userID + ", Networkcode : " + networkCode + ", CurDate " + curDate + ", p_totalRequestedQuantity : " + p_totalRequestedQuantity);
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

		UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, userID, isLockForUpdate);
		/*
		 * This situation will accour in the case of the first Transaction of
		 * the user
		 */
		if (userTransferCountsVO == null) {
			userTransferCountsVO = new UserTransferCountsVO();
		}
		checkResetCountersAfterPeriodChange(userTransferCountsVO, curDate);
		final String countMessage = outsideTransferOutCounts(con, userTransferCountsVO, profileID, networkCode, p_totalRequestedQuantity);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited = " + countMessage);
		}

		return countMessage;
	}

	/**
	 * Check the outside transfer out counts and value of the user against the
	 * defined value in tranhsfer profile
	 * 
	 * @param con
	 * @param p_userTransferCountsVO
	 * @param profileID
	 * @param networkCode
	 * @param p_totalRequestedQuantity
	 *            long
	 * @return String
	 * @throws BTSLBaseException
	 */
	private static String outsideTransferOutCounts(Connection con, UserTransferCountsVO p_userTransferCountsVO, String profileID, String networkCode, long p_totalRequestedQuantity) throws BTSLBaseException {
		final String methodName = "outsideTransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered UserTransferCountsVO = ");
			loggerValue.append(p_userTransferCountsVO);
			loggerValue.append(", profileID = ");
			loggerValue.append(profileID);
			loggerValue.append(", networkCode = ");
			loggerValue.append(networkCode);
			loggerValue.append(", p_totalRequestedQuantity : ");
			loggerValue.append(p_totalRequestedQuantity);
			log.debug(methodName,loggerValue);
		}

		/*
		 * Now load the transferProfileVO, which contains the LEAST/GREATEST (as
		 * per required) values form
		 * USER LEVEL PROFILE and CATEGORY LEVEL PROFILE.
		 */
		final TransferProfileVO transferProfileVO = TransferProfileCache.getTransferProfileDetails(profileID, networkCode);
		if (transferProfileVO.getUnctrlDailyOutCount() <= p_userTransferCountsVO.getUnctrlDailyOutCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_DAILY_OUT_COUNT;
		} else if (transferProfileVO.getUnctrlDailyOutValue() < (p_userTransferCountsVO.getUnctrlDailyOutValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_DAILY_OUT_VALUE;
		} else if (transferProfileVO.getUnctrlWeeklyOutCount() <= p_userTransferCountsVO.getUnctrlWeeklyOutCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_WEEKLY_OUT_COUNT;
		} else if (transferProfileVO.getUnctrlWeeklyOutValue() < (p_userTransferCountsVO.getUnctrlWeeklyOutValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_WEEKLY_OUT_VALUE;
		} else if (transferProfileVO.getUnctrlMonthlyOutCount() <= p_userTransferCountsVO.getUnctrlMonthlyOutCount()) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_MONTHLY_OUT_COUNT;
		} else if (transferProfileVO.getUnctrlMonthlyOutValue() < (p_userTransferCountsVO.getUnctrlMonthlyOutValue() + p_totalRequestedQuantity)) {
			return PretupsErrorCodesI.CHANNEL_TRANSFER_OUTSIDE_MONTHLY_OUT_VALUE;
		}
		return null;
	}

	/**
	 * Update the user Transfer Incounts.Its update the how much
	 * daily,Weekly,monthly count and transfer
	 * allowed
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param p_forwardPath
	 *            *
	 * @param curDate
	 *            Date
	 * @return int
	 * @throws BTSLBaseException
	 */
	public static int updateOptToChannelUserInCounts(Connection con, ChannelTransferVO channelTransferVO, String p_forwardPath, Date curDate) throws BTSLBaseException {
		final String methodName = "updateOptToChannelUserInCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", Forward Path : ");
			loggerValue.append(p_forwardPath);
			loggerValue.append(", CurDate = ");
			loggerValue.append(curDate);
			log.debug(methodName,  loggerValue );
		}
		int updateCount = 0;


		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		String userID = null;
		if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
			userID = channelTransferVO.getToUserID();
		} else {
			userID = channelTransferVO.getFromUserID();
		}

		UserTransferCountsVO countsVO = userTransferCountsDAO.loadTransferCounts(con, userID, true);
		boolean flag = true;
		if (countsVO == null) {
			flag = false;
			countsVO = new UserTransferCountsVO();
		}
		checkResetCountersAfterPeriodChange(countsVO, curDate);

		if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {

			
			if(SystemPreferences.USERWISE_LOAN_ENABLE) {

				Map hashmap = checkUserLoanstatusAndAmount(con, channelTransferVO);
				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
					final String args[] = { PretupsBL.getDisplayAmount((long)hashmap.get(PretupsI.WITHDRAW_AMOUNT)) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.LOAN_SETTLEMENT_PENDING,args);
						}

				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
					UserLoanWithdrawBL  userLoanWithdrawBL = new UserLoanWithdrawBL();
					userLoanWithdrawBL.autoChannelLoanSettlement(channelTransferVO, PretupsI.USER_LOAN_REQUEST_TYPE,(long)hashmap.get(PretupsI.WITHDRAW_AMOUNT));
				}
				
			}
			else {
				Map hashmap = checkSOSstatusAndAmount(con, countsVO,
						channelTransferVO);
				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
					final String args[] = { channelTransferVO.getToUserName() };
					throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.SOS_PENDING_FOR_SETTLEMENT, 0, args,p_forwardPath);
				}

				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
					ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
					channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
				}
				Map lrHashMap = checkLRstatusAndAmount(con, countsVO, channelTransferVO);
				if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
					ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
					channelTransferVO.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
					channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
				}
			}
			final String transferCountsMessage = transferInCountsCheck(con, countsVO, channelTransferVO.getReceiverTxnProfile(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getTransferMRP());
			if (transferCountsMessage != null) {
				final String args[] = { channelTransferVO.getToUserName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, transferCountsMessage, 0, args, p_forwardPath);
			}
		}

		if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
			countsVO.setUserID(channelTransferVO.getToUserID());
			countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
			countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
			countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
			countsVO.setDailyInValue(countsVO.getDailyInValue() + channelTransferVO.getTransferMRP());
			countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + channelTransferVO.getTransferMRP());
			countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + channelTransferVO.getTransferMRP());
			countsVO.setLastInTime(curDate);
		} else if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(channelTransferVO.getTransferType())) {
			// As per SRS
			countsVO.setUserID(channelTransferVO.getFromUserID());
			if (countsVO.getDailyInValue() >= channelTransferVO.getTransferMRP()) {
				countsVO.setDailyInValue(countsVO.getDailyInValue() - channelTransferVO.getTransferMRP());
			}
			if (countsVO.getWeeklyInValue() >= channelTransferVO.getTransferMRP()) {
				countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() - channelTransferVO.getTransferMRP());
			}
			if (countsVO.getMonthlyInValue() >= channelTransferVO.getTransferMRP()) {
				countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() - channelTransferVO.getTransferMRP());
			}
			countsVO.setLastInTime(curDate);
		}

		countsVO.setLastTransferID(channelTransferVO.getTransferID());
		countsVO.setLastTransferDate(curDate);

		updateCount = userTransferCountsDAO.updateUserTransferCounts(con, countsVO, flag);
		if(!"myRestO2CReturn".equals(p_forwardPath) && updateCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()) && channelTransferVO.getUserOTFCountsVO() != null && !PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(channelTransferVO.getTransferCategory()) && PretupsI.TRANSFER_TYPE_O2C.equals(channelTransferVO.getType() ))
		{
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Entered UserOTFCountsVO = ");
				loggerValue.append(channelTransferVO.getUserOTFCountsVO());
			}
			int updateCount1=userTransferCountsDAO.updateUserOTFCounts(con, channelTransferVO.getUserOTFCountsVO());
			
			if (updateCount1 <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
			}
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited updateCount = ");
			loggerValue.append(updateCount);
			log.debug(methodName,  loggerValue );
		}

		return updateCount;
	}

	/**
	 * 
	 * Load the product with taxes, discount and commission
	 * 
	 * @param con
	 * @param p_commissionProfileID
	 *            String
	 * @param p_commissionProfileVersion
	 *            String
	 * @param p_transferItemsList
	 * @param isWeb
	 * @param p_forwardPath
	 * @param p_txnType
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public static void loadAndCalculateTaxOnProducts(Connection con, String p_commissionProfileID, String p_commissionProfileVersion, ChannelTransferVO channelTransferVO, boolean isWeb, String p_forwardPath, String p_txnType) throws BTSLBaseException, Exception {
		final String methodName = "loadAndCalculateTaxOnProducts";

        StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered CommissiojnProfileID : ");
			loggerValue.append(p_commissionProfileID);
			loggerValue.append(", p_commissionProfileVersion = ");
			loggerValue.append(p_commissionProfileVersion);
			loggerValue.append(", channelTransferVO : ");
			loggerValue.append( channelTransferVO.toString());
			loggerValue.append(", ForwardPath : ");
			loggerValue.append(p_forwardPath);
			loggerValue.append(", isWeb : ");
			loggerValue.append(isWeb);
			loggerValue.append(", p_txnType = ");
			loggerValue.append(p_txnType);
			log.debug(methodName,loggerValue);
		}

		// load the tax and commission of the products from data bas
		// according to the user commission profile
		final ArrayList transferItemsList = channelTransferVO.getChannelTransferitemsVOList();
		channelTransferVO.setCommProfileSetId(p_commissionProfileID);
		channelTransferVO.setCommProfileVersion(p_commissionProfileVersion);
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		boolean PAYMENT_MODE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD);
		
		String type = (PretupsI.TRANSFER_TYPE_FOC.equals(p_txnType) || PretupsI.TRANSFER_TYPE_DP.equals(p_txnType))?PretupsI.TRANSFER_TYPE_O2C:p_txnType;
		String paymentMode = (PAYMENT_MODE_ALWD && (PretupsI.TRANSFER_TYPE_O2C.equals(p_txnType)|| PretupsI.TRANSFER_TYPE_C2C.equals(p_txnType)))?channelTransferVO.getPayInstrumentType():PretupsI.ALL;
		commissionProfileDAO.loadProductListWithTaxes(con, p_commissionProfileID, p_commissionProfileVersion, transferItemsList, type, paymentMode);

		ChannelTransferItemsVO channelTransferItemsVO = null;
		KeyArgumentVO argumentVO = null;
		channelTransferVO.setWeb(isWeb);
		final ArrayList errorList = new ArrayList();
		channelTransferVO.setCommProfileSetId(p_commissionProfileID);
		channelTransferVO.setCommProfileVersion(p_commissionProfileVersion);
		if(BTSLUtil.isNullString(channelTransferVO.getType()))
		{
			channelTransferVO.setType(type);
		}
		for (int i = 0, k = transferItemsList.size(); i < k; i++) {

			channelTransferItemsVO = (ChannelTransferItemsVO) transferItemsList.get(i);
			if (!channelTransferItemsVO.isSlabDefine()) {
				argumentVO = new KeyArgumentVO();

				if (isWeb) {
					argumentVO.setKey("channeltransfer.transferdetails.error.commissionprofile.product.notdefine");
					argumentVO.setArguments(new String[] { channelTransferItemsVO.getProductName(), channelTransferItemsVO.getRequestedQuantity() });
				} else {
					argumentVO.setKey(PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE_SUBKEY);
					argumentVO.setArguments(new String[] { channelTransferItemsVO.getShortName(), channelTransferItemsVO.getRequestedQuantity() });
				}
				errorList.add(argumentVO);
			}
			else if ((PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()) % channelTransferItemsVO.getTransferMultipleOf()) != 0) {
				argumentVO = new KeyArgumentVO();
				argumentVO.setKey("channeltransfer.transferdetails.error.multipleof");
				argumentVO.setArguments(new String[] { channelTransferItemsVO.getProductName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getTransferMultipleOf()) });
				errorList.add(argumentVO);
			}
		}
		if (!errorList.isEmpty()) {
			if (isWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, errorList, p_forwardPath);
			} else {
				throw new BTSLBaseException(ChannelTransferBL.class.getName(), methodName, PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE, errorList);
			}
		}


		if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC))
		{
			if((p_txnType.equals(PretupsI.TRANSFER_TYPE_O2C) && !isWeb) || !p_txnType.equals(PretupsI.TRANSFER_TYPE_O2C) || (PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(channelTransferVO.getPayInstrumentType()) && (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType())||PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(channelTransferVO.getTransferSubType()))))
			{
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()) && channelTransferVO.isOtfFlag())
				{
					ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
				}
			}
			}



		// to calculate commission and tax
		calculateMRPWithTaxAndDiscount(channelTransferVO, p_txnType);

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method to check the Channel to subscriber transfer Out counts.
	 * A preference is there that will decide that whether separate C2S transfer
	 * out will be used or C2C transfer out count to be used
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_isLockRecordForUpdate
	 * @param p_isCheckThresholds
	 *            boolean
	 * @return UserTransferCountsVO
	 * @throws BTSLBaseException
	 */
	public static UserTransferCountsVO checkC2STransferOutCounts(Connection con, C2STransferVO c2sTransferVO, boolean p_isLockRecordForUpdate, boolean p_isCheckThresholds) throws BTSLBaseException {
		final String methodName = "checkC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			loggerValue.append(", p_isLockRecordForUpdate = ");
			loggerValue.append(p_isLockRecordForUpdate);
			loggerValue.append(", p_isCheckThresholds = ");
			loggerValue.append(p_isCheckThresholds);
			log.debug(methodName,loggerValue);
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

		UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, c2sTransferVO.getSenderID(), p_isLockRecordForUpdate);
		if (!p_isCheckThresholds) {
			return userTransferCountsVO;
		}
		final TransferProfileVO transferProfileVO = TransferProfileCache.getTransferProfileDetails(((ChannelUserVO) c2sTransferVO.getSenderVO()).getTransferProfileID(),
				c2sTransferVO.getNetworkCode());
		String[] strArr = null;
		try {
			// Done so as if someone has defined in Transfer Profile as Allowed
			// Transfer as 0
			if (userTransferCountsVO == null) {
				userTransferCountsVO = new UserTransferCountsVO();
				userTransferCountsVO.setUpdateRecord(false);
			}

			userTransferCountsVO.setLastTransferID(c2sTransferVO.getTransferID());

			// To check whether Counters needs to be reinitialized or not
			final boolean isCounterReInitalizingReqd = checkResetCountersAfterPeriodChange(userTransferCountsVO, c2sTransferVO.getCreatedOn());
			c2sTransferVO.setTransferProfileCtInitializeReqd(isCounterReInitalizingReqd);
			boolean useC2sSeparateTransferCounts = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS);
			if (useC2sSeparateTransferCounts) {

				// added by akanksha gor ethiotelecom
				if (transferProfileVO.getDailySubscriberOutCount() < userTransferCountsVO.getDailySubscriberOutCount() + 1) {
					// strArr=new
					strArr = new String[] { String.valueOf(transferProfileVO.getDailySubscriberOutCount()) };
					EventHandler.handle(EventIDI.REQFAIL_DAYMAXLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Number of transfers for day exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_DAILY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getDailySubscriberOutValue() < userTransferCountsVO.getDailySubscriberOutValue()
						+ c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),
							PretupsBL.getDisplayAmount(userTransferCountsVO.getDailySubscriberOutValue()),
							PretupsBL.getDisplayAmount(transferProfileVO.getDailySubscriberOutValue()) };
					EventHandler.handle(EventIDI.REQFAIL_DAYMAX_AMTLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Amt transfer for day exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_DAILY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getWeeklySubscriberOutCount() < userTransferCountsVO.getWeeklySubscriberOutCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getWeeklySubscriberOutCount()) };
					EventHandler.handle(EventIDI.REQFAIL_WEEKMAXLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Number of transfers for week exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getWeeklySubscriberOutValue() < userTransferCountsVO.getWeeklySubscriberOutValue()
						+ c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),
							PretupsBL.getDisplayAmount(userTransferCountsVO.getWeeklySubscriberOutValue()),
							PretupsBL.getDisplayAmount(transferProfileVO.getWeeklySubscriberOutValue()) };
					EventHandler.handle(EventIDI.REQFAIL_WEEKMAX_AMTLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Amt transfer for week exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getMonthlySubscriberOutCount() < userTransferCountsVO.getMonthlySubscriberOutCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getMonthlySubscriberOutCount()) };
					EventHandler.handle(EventIDI.REQFAIL_MONTHMAXLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Number of transfers for month exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getMonthlySubscriberOutValue() < userTransferCountsVO.getMonthlySubscriberOutValue()
						+ c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),
							PretupsBL.getDisplayAmount(userTransferCountsVO.getMonthlySubscriberOutValue()),
							PretupsBL.getDisplayAmount(transferProfileVO.getMonthlySubscriberOutValue()) };
					EventHandler.handle(EventIDI.REQFAIL_MONTHMAX_AMTLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Amt of transfer for month exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				}
			} else {
				if (transferProfileVO.getDailySubscriberOutCount() < userTransferCountsVO.getDailySubscriberOutCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getDailySubscriberOutCount()) };
					EventHandler.handle(EventIDI.REQFAIL_DAYMAXLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Number of transfers for day exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_DAILY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getDailySubscriberOutValue() < userTransferCountsVO.getDailySubscriberOutValue()
						+ c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),
							PretupsBL.getDisplayAmount(userTransferCountsVO.getDailySubscriberOutValue()),
							PretupsBL.getDisplayAmount(transferProfileVO.getDailySubscriberOutValue()) };
					EventHandler.handle(EventIDI.REQFAIL_DAYMAX_AMTLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Amt transfer for day exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_DAILY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getWeeklySubscriberOutCount() < userTransferCountsVO.getWeeklySubscriberOutCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getWeeklySubscriberOutCount()) };
					EventHandler.handle(EventIDI.REQFAIL_WEEKMAXLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Number of transfers for week exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getWeeklySubscriberOutValue() < userTransferCountsVO.getWeeklySubscriberOutValue()
						+ c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),
							PretupsBL.getDisplayAmount(userTransferCountsVO.getWeeklySubscriberOutValue()),
							PretupsBL.getDisplayAmount(transferProfileVO.getWeeklySubscriberOutValue()) };
					EventHandler.handle(EventIDI.REQFAIL_WEEKMAX_AMTLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Amt transfer for week exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getMonthlySubscriberOutCount() < userTransferCountsVO.getMonthlySubscriberOutCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getMonthlySubscriberOutCount()) };
					EventHandler.handle(EventIDI.REQFAIL_MONTHMAXLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Number of transfers for month exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO
						.getMonthlySubscriberOutValue() < userTransferCountsVO.getMonthlySubscriberOutValue()
						+ c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),
							PretupsBL.getDisplayAmount(userTransferCountsVO.getMonthlySubscriberOutValue()),
							PretupsBL.getDisplayAmount(transferProfileVO.getMonthlySubscriberOutValue()) };
					EventHandler.handle(EventIDI.REQFAIL_MONTHMAX_AMTLIMIT, EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]", "", "", "",
							"Reques Amt of transfer for month exceeded.");
					throw new BTSLBaseException(ChannelTransferBL.class, methodName,
							PretupsErrorCodesI.MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				}

			}
		} catch (BTSLBaseException be) {
			loggerValue.setLength(0);
			loggerValue.append("BTSL Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" ");
			loggerValue.append(be.getMessage());
			log.error(methodName,  loggerValue );
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception:" );
			loggerValue.append(e.getMessage());
			log.error(methodName, loggerValue );
			loggerValue.setLength(0);
			loggerValue.append("Exception : ");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with userTransferCountsVO = " );
			loggerValue.append(userTransferCountsVO);
			log.debug(methodName, loggerValue );
		}
		return userTransferCountsVO;
	}

	/**
	 * Method to check whether Counters needs to be reinitialized or not
	 * 
	 * @param p_userTransferCountsVO
	 * @param p_newDate
	 * @return boolean
	 */
	public static boolean checkResetCountersAfterPeriodChange(UserTransferCountsVO p_userTransferCountsVO, java.util.Date p_newDate) {
		final String methodName = "checkResetCountersAfterPeriodChange";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered with transferID = ");
			loggerValue.append(p_userTransferCountsVO.getLastTransferID());
			loggerValue.append(", USER ID = ");
			loggerValue.append(p_userTransferCountsVO.getUserID());
			log.debug(methodName,  loggerValue );
		}
		boolean isCounterChange = false;
		boolean isDayCounterChange = false;
		boolean isWeekCounterChange = false;
		boolean isMonthCounterChange = false;

		final Date previousDate = p_userTransferCountsVO.getLastTransferDate();

		if (previousDate != null) {
			final Calendar cal = BTSLDateUtil.getInstance();
			cal.setTime(p_newDate);
			final int presentDay = cal.get(Calendar.DAY_OF_MONTH);
			final int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
			final int presentMonth = cal.get(Calendar.MONTH);
			final int presentYear = cal.get(Calendar.YEAR);
			cal.setTime(previousDate);
			final int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
			final int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
			final int lastTrxMonth = cal.get(Calendar.MONTH);
			final int lastTrxYear = cal.get(Calendar.YEAR);
			if (presentDay != lastTrxDay) {
				isDayCounterChange = true;
			}
			if (presentWeek != lastWeek) {
				isWeekCounterChange = true;
			}
			if (presentMonth != lastTrxMonth) {
				isDayCounterChange = true;
				isWeekCounterChange = true;
				isMonthCounterChange = true;
			}
			if (presentYear != lastTrxYear) {
				isDayCounterChange = true;
				isWeekCounterChange = true;
				isMonthCounterChange = true;
			}

			if (isDayCounterChange) {
				p_userTransferCountsVO.setDailyInCount(0);
				p_userTransferCountsVO.setDailyInValue(0);
				p_userTransferCountsVO.setDailyOutCount(0);
				p_userTransferCountsVO.setDailyOutValue(0);
				p_userTransferCountsVO.setUnctrlDailyInCount(0);
				p_userTransferCountsVO.setUnctrlDailyInValue(0);
				p_userTransferCountsVO.setUnctrlDailyOutCount(0);
				p_userTransferCountsVO.setUnctrlDailyOutValue(0);
				p_userTransferCountsVO.setDailySubscriberOutCount(0);
				p_userTransferCountsVO.setDailySubscriberOutValue(0);
				p_userTransferCountsVO.setDailyC2STransferOutCount(0);
				p_userTransferCountsVO.setDailyC2STransferOutValue(0);
				p_userTransferCountsVO.setDailySubscriberInCount(0);
				p_userTransferCountsVO.setDailySubscriberInValue(0);
				p_userTransferCountsVO.setDailyRoamAmount(0);

				isCounterChange = true;
			}
			if (isWeekCounterChange) {
				p_userTransferCountsVO.setWeeklySubscriberOutValue(0);
				p_userTransferCountsVO.setWeeklyInCount(0);
				p_userTransferCountsVO.setWeeklyInValue(0);
				p_userTransferCountsVO.setWeeklyOutCount(0);
				p_userTransferCountsVO.setWeeklyOutValue(0);
				p_userTransferCountsVO.setUnctrlWeeklyInCount(0);
				p_userTransferCountsVO.setUnctrlWeeklyInValue(0);
				p_userTransferCountsVO.setUnctrlWeeklyOutValue(0);
				p_userTransferCountsVO.setWeeklySubscriberOutCount(0);
				p_userTransferCountsVO.setUnctrlWeeklyOutCount(0);
				p_userTransferCountsVO.setWeeklyC2STransferOutCount(0);
				p_userTransferCountsVO.setWeeklyC2STransferOutValue(0);
				p_userTransferCountsVO.setWeeklySubscriberInCount(0);
				p_userTransferCountsVO.setWeeklySubscriberInValue(0);
				p_userTransferCountsVO.setDailyRoamAmount(0);
				isCounterChange = true;
			}
			if (isMonthCounterChange) {
				p_userTransferCountsVO.setMonthlyInCount(0);
				p_userTransferCountsVO.setMonthlyInValue(0);
				p_userTransferCountsVO.setMonthlyOutCount(0);
				p_userTransferCountsVO.setMonthlyOutValue(0);
				p_userTransferCountsVO.setUnctrlMonthlyInCount(0);
				p_userTransferCountsVO.setUnctrlMonthlyInValue(0);
				p_userTransferCountsVO.setUnctrlMonthlyOutCount(0);
				p_userTransferCountsVO.setUnctrlMonthlyOutValue(0);
				p_userTransferCountsVO.setMonthlySubscriberOutCount(0);
				p_userTransferCountsVO.setMonthlySubscriberOutValue(0);
				p_userTransferCountsVO.setMonthlyC2STransferOutCount(0);
				p_userTransferCountsVO.setMonthlyC2STransferOutValue(0);
				p_userTransferCountsVO.setMonthlySubscriberInCount(0);
				p_userTransferCountsVO.setMonthlySubscriberInValue(0);
				p_userTransferCountsVO.setDailyRoamAmount(0);
				isCounterChange = true;
			}
		} else {
			isCounterChange = true;
		}

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting with isCounterChange = ");
			loggerValue.append(isCounterChange);
			loggerValue.append( ", For transferID = ");
			loggerValue.append(p_userTransferCountsVO.getLastTransferID());
			loggerValue.append(", USER ID=");
			loggerValue.append(p_userTransferCountsVO.getUserID());
			log.debug(methodName,loggerValue);
		}
		return isCounterChange;
	}

	/**
	 * Method to increase the Channel to subscriber transfer out counts and
	 * values
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_isCheckThresholds
	 *            boolean
	 * @throws BTSLBaseException
	 */
	public static void increaseC2STransferOutCounts(Connection con, C2STransferVO c2sTransferVO, boolean p_isCheckThresholds) throws BTSLBaseException {
		final String methodName = "increaseC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			loggerValue.append(", p_isCheckThresholds = ");
			loggerValue.append(p_isCheckThresholds);
			log.debug(methodName,loggerValue);
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		final boolean isLockRecordForUpdate = true;
		try {
			final UserTransferCountsVO userTransferCountsVO = checkC2STransferOutCounts(con, c2sTransferVO, isLockRecordForUpdate, p_isCheckThresholds);
			boolean useC2sSeparateTransferCounts = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS);
			if (useC2sSeparateTransferCounts) {
				userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
				userTransferCountsVO.setDailyC2STransferOutCount(userTransferCountsVO.getDailyC2STransferOutCount() + 1);
				userTransferCountsVO.setDailyC2STransferOutValue(userTransferCountsVO.getDailyC2STransferOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setWeeklyC2STransferOutCount(userTransferCountsVO.getWeeklyC2STransferOutCount() + 1);
				userTransferCountsVO.setWeeklyC2STransferOutValue(userTransferCountsVO.getWeeklyC2STransferOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setMonthlyC2STransferOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() + 1);
				userTransferCountsVO.setMonthlyC2STransferOutValue(userTransferCountsVO.getMonthlyC2STransferOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
				userTransferCountsVO.setLastOutTime(c2sTransferVO.getCreatedOn());
				// updated by akanksha for ethiopia telecom
				userTransferCountsVO.setDailySubscriberOutCount(userTransferCountsVO.getDailySubscriberOutCount() + 1);
				userTransferCountsVO.setDailySubscriberOutValue(userTransferCountsVO.getDailySubscriberOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setWeeklySubscriberOutCount(userTransferCountsVO.getWeeklySubscriberOutCount() + 1);
				userTransferCountsVO.setWeeklySubscriberOutValue(userTransferCountsVO.getWeeklySubscriberOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setMonthlySubscriberOutCount(userTransferCountsVO.getMonthlySubscriberOutCount() + 1);
				userTransferCountsVO.setMonthlySubscriberOutValue(userTransferCountsVO.getMonthlySubscriberOutValue() + c2sTransferVO.getRequestedAmount());

			} else {
				userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
				userTransferCountsVO.setDailyOutCount(userTransferCountsVO.getDailyOutCount() + 1);
				userTransferCountsVO.setDailyOutValue(userTransferCountsVO.getDailyOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setWeeklyOutCount(userTransferCountsVO.getWeeklyOutCount() + 1);
				userTransferCountsVO.setWeeklyOutValue(userTransferCountsVO.getWeeklyOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setMonthlyOutCount(userTransferCountsVO.getMonthlyOutCount() + 1);
				userTransferCountsVO.setMonthlyOutValue(userTransferCountsVO.getMonthlyOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
				userTransferCountsVO.setLastOutTime(c2sTransferVO.getCreatedOn());
				userTransferCountsVO.setDailySubscriberOutCount(userTransferCountsVO.getDailySubscriberOutCount() + 1);
				userTransferCountsVO.setDailySubscriberOutValue(userTransferCountsVO.getDailySubscriberOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setWeeklySubscriberOutCount(userTransferCountsVO.getWeeklySubscriberOutCount() + 1);
				userTransferCountsVO.setWeeklySubscriberOutValue(userTransferCountsVO.getWeeklySubscriberOutValue() + c2sTransferVO.getRequestedAmount());
				userTransferCountsVO.setMonthlySubscriberOutCount(userTransferCountsVO.getMonthlySubscriberOutCount() + 1);
				userTransferCountsVO.setMonthlySubscriberOutValue(userTransferCountsVO.getMonthlySubscriberOutValue() + c2sTransferVO.getRequestedAmount());

			}
			if (c2sTransferVO.isRoam()) {
				userTransferCountsVO.setDailyRoamAmount(userTransferCountsVO.getDailyRoamAmount() + c2sTransferVO.getRequestedAmount());
			}
			final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, userTransferCountsVO.isUpdateRecord());

			/*if(updateCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,c2sTransferVO.getNetworkCode()) && c2sTransferVO.getUserOTFCountsVO() != null)
			{
				int updateCount1=userTransferCountsDAO.updateUserOTFCounts(con, c2sTransferVO.getUserOTFCountsVO());*/
				
				if (updateCount <= 0) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
				}
			//}
			
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited updateCount = " + updateCount);
			}
			if (updateCount <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
			}

		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception : " );
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue);
			
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[increaseC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method that will call Data access object class to add the Channel to
	 * subscriber transfer details in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_voucherList
	 * @throws BTSLBaseException
	 */
	public static void addC2STransferDetails(Connection con, C2STransferVO c2sTransferVO, List p_voucherList) throws BTSLBaseException {
		final String methodName = "addC2STransferDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with  p_voucherList size = " + p_voucherList.size());
		}
		try {
			final Date _currentDate = new Date();
			final ChannelUserVO senderVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
			final String lmsProfileSetId = senderVO.getLmsProfile();
			if (!BTSLUtil.isNullString(lmsProfileSetId)) {
				// LMS profile cache by brajesh
				final ProfileSetDetailsLMSVO profileSetDetailsLMSVO = (ProfileSetDetailsLMSVO) LMSProfileCache.getObject(lmsProfileSetId, _currentDate);
				String lmsProfileVersion = null;
				if (!BTSLUtil.isNullObject(profileSetDetailsLMSVO)) {
					lmsProfileVersion = profileSetDetailsLMSVO.getVersion();
					c2sTransferVO.setLmsVersion(lmsProfileVersion);
				}
			}   
			final int updateCount = new C2STransferDAO().addC2STransferDetails(con, c2sTransferVO, p_voucherList);
			if (updateCount <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			log.error(methodName, "Exception p_transferID : " + c2sTransferVO.getTransferID() + ", Exception:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[addC2STransferDetails]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception :" + e.getMessage());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method that will call Data access object class to update the Channel to
	 * subscriber transfer details in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static void updateC2STransferDetails(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "updateC2STransferDetails";
		Jedis jedis =null;
		try {
	        final ChannelUserVO senderVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
	        if (senderVO.getLmsProfile() != null) {
	        	c2sTransferVO.setLmsProfile(senderVO.getLmsProfile());
            } else {
            	c2sTransferVO.setLmsProfile("");
            }
	        
	        final C2STransferItemVO receiverItemVO = (C2STransferItemVO) c2sTransferVO.getTransferItemList().get(1);
	        c2sTransferVO.setPreviousBalance(receiverItemVO.getPreviousBalance());
	        c2sTransferVO.setPostBalance(receiverItemVO.getPostBalance());
	        c2sTransferVO.setValidationStatus(receiverItemVO.getValidationStatus());
	        c2sTransferVO.setUpdateStatus(receiverItemVO.getUpdateStatus());
	        c2sTransferVO.setAccountStatus(receiverItemVO.getAccountStatus());
	        c2sTransferVO.setInterfaceResponseCode(receiverItemVO.getInterfaceResponseCode());
	        c2sTransferVO.setPreviousExpiry(receiverItemVO.getPreviousExpiry());
	        c2sTransferVO.setNewExpiry(receiverItemVO.getNewExpiry());
	        c2sTransferVO.setFirstCall(receiverItemVO.getFirstCall());
	        c2sTransferVO.setInterfaceReferenceId(receiverItemVO.getInterfaceReferenceID());
	        c2sTransferVO.setProtocolStatus(receiverItemVO.getProtocolStatus());
	        c2sTransferVO.setTransferType2(receiverItemVO.getTransferType2());
	        c2sTransferVO.setInterfaceReferenceID2(receiverItemVO.getInterfaceReferenceID2());
	        c2sTransferVO.setUpdateStatus2(receiverItemVO.getUpdateStatus2());
	        c2sTransferVO.setTransferType1(receiverItemVO.getTransferType1());
	        c2sTransferVO.setInterfaceReferenceID1(receiverItemVO.getInterfaceReferenceID1());
	        c2sTransferVO.setUpdateStatus1(receiverItemVO.getUpdateStatus1());
	        c2sTransferVO.setAdjustValue(receiverItemVO.getAdjustValue());
	        int updateCount = 0;
	        if("Y".equals(BTSLUtil.NullToString(Constants.getProperty("KAFKA_ENABLE"))))
				PretupsKafkaProducerBL.c2sTransfersUpdateProducer( c2sTransferVO);
			else{
			   if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim()) && trfIdCheck) {
				   RedisActivityLog.log("ChannelTransferBL->updateC2STransferDetails->Start");
	   			   jedis = RedisConnectionPool.getPoolInstance().getResource();
	   			   jedis.del(hkeytransferId,c2sTransferVO.getTransferID());
				   RedisActivityLog.log("ChannelTransferBL->updateC2STransferDetails->End");
					updateCount = new C2STransferDAO().addC2STransferDetailsfromRedis(con, c2sTransferVO,true);
				}else{
					updateCount = new C2STransferDAO().updateC2STransferDetails(con, c2sTransferVO);
				}
			}
			if (updateCount < 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
			}
		} catch (BTSLBaseException be) {
			throw be;
		}catch(JedisConnectionException je){
	 		log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		    log.errorTrace(methodName, je);
   	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[updateC2STransferDetails]",
						c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "JedisConnectionException :" + je.getMessage());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, je.getMessage());
		}catch(NoSuchElementException  ex){
	 		log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        log.errorTrace(methodName, ex);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[updateC2STransferDetails]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "NoSuchElementException :" + ex.getMessage());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, ex.getMessage());
		 } catch (Exception e) {
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[updateC2STransferDetails]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception :" + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		}finally {
	    	if (jedis != null) {
	    	jedis.close();
	    	}
		 }
	}

	/**
	 * Method to decrease the Channel to subscriber transfer out counts and
	 * values in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static void decreaseC2STransferOutCounts(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "decreaseC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = " );
			loggerValue.append(c2sTransferVO.getSenderID());
			log.debug(methodName, loggerValue);
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		final boolean isLockRecordForUpdate = true;
		final boolean isUpdateRecord = true;
		try {
			final UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, c2sTransferVO.getSenderID(), isLockRecordForUpdate);
			if (!BTSLUtil.isNullObject(userTransferCountsVO)) {
				userTransferCountsVO.setLastTransferID(c2sTransferVO.getTransferID());
				final boolean isCounterReInitalizingReqd = checkResetCountersAfterPeriodChange(userTransferCountsVO, c2sTransferVO.getCreatedOn());
				if (isCounterReInitalizingReqd) {
					setC2STransferOutCountsForDecrease(c2sTransferVO, userTransferCountsVO);
					final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, isUpdateRecord);

					if (log.isDebugEnabled()) {
						log.debug(methodName, "Exited updateCount = " + updateCount);
					}
					if (updateCount <= 0) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
					}
				} else {
					// Do we need to decrease the counters here also
					setC2STransferOutCountsForDecrease(c2sTransferVO, userTransferCountsVO);
					final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, isUpdateRecord);

					if (log.isDebugEnabled()) {
						loggerValue.setLength(0);
						loggerValue.append("Exited updateCount = ");
						loggerValue.append(updateCount);
						log.debug(methodName,loggerValue);
					}
					if (updateCount <= 0) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
					}
				}
			}
		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append( e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[decreaseC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method to increase the Channel to subscriber transfer in counts and
	 * values in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static void increaseC2STransferInCounts(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "decreaseC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());;
			log.debug(methodName, loggerValue );
		}


		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		final boolean isLockRecordForUpdate = true;
		final boolean isUpdateRecord = true;
		try {
			final UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, c2sTransferVO.getSenderID(), isLockRecordForUpdate);
			if (!BTSLUtil.isNullObject(userTransferCountsVO)) {
				userTransferCountsVO.setLastTransferID(c2sTransferVO.getTransferID());
				final boolean isCounterReInitalizingReqd = checkResetCountersAfterPeriodChange(userTransferCountsVO, c2sTransferVO.getCreatedOn());
				if (isCounterReInitalizingReqd) {
					setC2STransferInCountsForIncrease(c2sTransferVO, userTransferCountsVO);
					final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, isUpdateRecord);

					if (log.isDebugEnabled()) {
						loggerValue.setLength(0);
						loggerValue.append("Exited updateCount = ");
						loggerValue.append(updateCount);
						log.debug(methodName,  loggerValue);
					}
					if (updateCount <= 0) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
					}
				} else {
					// setC2STransferOutCountsForDecrease(c2sTransferVO,userTransferCountsVO);
					setC2STransferInCountsForIncrease(c2sTransferVO, userTransferCountsVO);
					final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, isUpdateRecord);

					if (log.isDebugEnabled()) {
						log.debug(methodName, "Exited updateCount = " + updateCount);
					}
					if (updateCount <= 0) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
					}
				}
			}
		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception:");
			loggerValue.append(e.getMessage());
			log.error(methodName, loggerValue);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[decreaseC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Set the appropriate value in the transfer Counts VO
	 * 
	 * @param c2sTransferVO
	 * @param userTransferCountsVO
	 */
	public static void setC2STransferOutCountsForDecrease(C2STransferVO c2sTransferVO, UserTransferCountsVO userTransferCountsVO) {
		boolean useC2sSeparateTransferCounts = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS);
		if (useC2sSeparateTransferCounts) {
			userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
			if (userTransferCountsVO.getDailyC2STransferOutCount() > 0) {
				userTransferCountsVO.setDailyC2STransferOutCount(userTransferCountsVO.getDailyC2STransferOutCount() - 1);
			}
			if (userTransferCountsVO.getDailyC2STransferOutValue() > 0) {
				userTransferCountsVO.setDailyC2STransferOutValue(userTransferCountsVO.getDailyC2STransferOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getWeeklyC2STransferOutCount() > 0) {
				userTransferCountsVO.setWeeklyC2STransferOutCount(userTransferCountsVO.getWeeklyC2STransferOutCount() - 1);
			}
			if (userTransferCountsVO.getWeeklyC2STransferOutValue() > 0) {
				userTransferCountsVO.setWeeklyC2STransferOutValue(userTransferCountsVO.getWeeklyC2STransferOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getMonthlyC2STransferOutCount() > 0) {
				userTransferCountsVO.setMonthlyC2STransferOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() - 1);
			}
			if (userTransferCountsVO.getMonthlyC2STransferOutValue() > 0) {
				userTransferCountsVO.setMonthlyC2STransferOutValue(userTransferCountsVO.getMonthlyC2STransferOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getDailySubscriberOutCount() > 0) {
				userTransferCountsVO.setDailySubscriberOutCount(userTransferCountsVO.getDailySubscriberOutCount() - 1);
			}
			if (userTransferCountsVO.getDailySubscriberOutValue()> 0) {
				userTransferCountsVO.setDailySubscriberOutValue(userTransferCountsVO.getDailySubscriberOutValue() - c2sTransferVO.getTransferValue());
			}
			if (userTransferCountsVO.getWeeklySubscriberOutCount() > 0) {
				userTransferCountsVO.setWeeklySubscriberOutCount(userTransferCountsVO.getWeeklySubscriberOutCount() - 1);
			}
			if (userTransferCountsVO.getWeeklySubscriberOutValue()> 0) {
				userTransferCountsVO.setWeeklySubscriberOutValue(userTransferCountsVO.getWeeklySubscriberOutValue() - c2sTransferVO.getTransferValue());
			}
			if (userTransferCountsVO.getMonthlySubscriberOutCount() > 0) {
				userTransferCountsVO.setMonthlySubscriberOutCount(userTransferCountsVO.getMonthlySubscriberOutCount() - 1);
			}
			if(userTransferCountsVO.getMonthlySubscriberOutValue()> 0) {
				userTransferCountsVO.setMonthlySubscriberOutValue(userTransferCountsVO.getMonthlySubscriberOutValue() - c2sTransferVO.getTransferValue());
			}
			userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
		} else {
			userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
			if (userTransferCountsVO.getDailyOutCount() > 0) {
				userTransferCountsVO.setDailyOutCount(userTransferCountsVO.getDailyOutCount() - 1);
			}
			if (userTransferCountsVO.getDailyOutValue() > 0) {
				userTransferCountsVO.setDailyOutValue(userTransferCountsVO.getDailyOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getWeeklyOutCount() > 0) {
				userTransferCountsVO.setWeeklyOutCount(userTransferCountsVO.getWeeklyOutCount() - 1);
			}
			if (userTransferCountsVO.getWeeklyOutValue() > 0) {
				userTransferCountsVO.setWeeklyOutValue(userTransferCountsVO.getWeeklyOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getMonthlyOutCount() > 0) {
				userTransferCountsVO.setMonthlyOutCount(userTransferCountsVO.getMonthlyOutCount() - 1);
			}
			if (userTransferCountsVO.getMonthlyOutValue() > 0) {
				userTransferCountsVO.setMonthlyOutValue(userTransferCountsVO.getMonthlyOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getDailySubscriberOutCount() > 0) {
				userTransferCountsVO.setDailySubscriberOutCount(userTransferCountsVO.getDailySubscriberOutCount() - 1);
			}
			if (userTransferCountsVO.getDailySubscriberOutValue()> 0) {
				userTransferCountsVO.setDailySubscriberOutValue(userTransferCountsVO.getDailySubscriberOutValue() - c2sTransferVO.getTransferValue());
			}
			if (userTransferCountsVO.getWeeklySubscriberOutCount() > 0) {
				userTransferCountsVO.setWeeklySubscriberOutCount(userTransferCountsVO.getWeeklySubscriberOutCount() - 1);
			}
			if (userTransferCountsVO.getWeeklySubscriberOutValue()> 0) {
				userTransferCountsVO.setWeeklySubscriberOutValue(userTransferCountsVO.getWeeklySubscriberOutValue() - c2sTransferVO.getTransferValue());
			}
			if (userTransferCountsVO.getMonthlySubscriberOutCount() > 0) {
				userTransferCountsVO.setMonthlySubscriberOutCount(userTransferCountsVO.getMonthlySubscriberOutCount() - 1);
			}
			if(userTransferCountsVO.getMonthlySubscriberOutValue()> 0) {
				userTransferCountsVO.setMonthlySubscriberOutValue(userTransferCountsVO.getMonthlySubscriberOutValue() - c2sTransferVO.getTransferValue());
			}
			userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
		}

		if (c2sTransferVO.isRoam() && userTransferCountsVO.getDailyRoamAmount() > 0) {
			userTransferCountsVO.setDailyRoamAmount(userTransferCountsVO.getDailyRoamAmount() - c2sTransferVO.getRequestedAmount());
		}

	}

	/**
	 * Set the appropriate value in the transfer Counts VO : increased IN counts
	 * for the Channel User (Receiver)
	 * 
	 * @param c2sTransferVO
	 * @param userTransferCountsVO
	 * 
	 */
	public static void setC2STransferInCountsForIncrease(C2STransferVO c2sTransferVO, UserTransferCountsVO userTransferCountsVO) {
		final String methodName = "setC2STransferInCountsForIncrease";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered Transfer ID = " + c2sTransferVO.getTransferID() + ", User ID = " + c2sTransferVO.getSenderID());
		}

		userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
		if (userTransferCountsVO.getDailySubscriberInCount() >= 0) {
			userTransferCountsVO.setDailySubscriberInCount(userTransferCountsVO.getDailySubscriberInCount() + 1);
		}
		if (userTransferCountsVO.getDailySubscriberInValue() >= 0) {
			userTransferCountsVO.setDailySubscriberInValue(userTransferCountsVO.getDailySubscriberInValue() + c2sTransferVO.getSenderTransferValue());
		}
		if (userTransferCountsVO.getWeeklySubscriberInCount() >= 0) {
			userTransferCountsVO.setWeeklySubscriberInCount(userTransferCountsVO.getWeeklySubscriberInCount() + 1);
		}
		if (userTransferCountsVO.getWeeklySubscriberInValue() >= 0) {
			userTransferCountsVO.setWeeklySubscriberInValue(userTransferCountsVO.getWeeklySubscriberInValue() + c2sTransferVO.getSenderTransferValue());
		}
		if (userTransferCountsVO.getMonthlySubscriberInCount() >= 0) {
			userTransferCountsVO.setMonthlySubscriberInCount(userTransferCountsVO.getMonthlySubscriberInCount() + 1);
		}
		if (userTransferCountsVO.getMonthlySubscriberInValue() >= 0) {
			userTransferCountsVO.setMonthlySubscriberInValue(userTransferCountsVO.getMonthlySubscriberInValue() + c2sTransferVO.getSenderTransferValue());
		}

		if (userTransferCountsVO.getDailyRoamAmount() >= 0 && !c2sTransferVO.getReceiverNetworkCode().equals(c2sTransferVO.getSenderNetworkCode())) {
			userTransferCountsVO.setDailyRoamAmount(userTransferCountsVO.getDailyRoamAmount() - c2sTransferVO.getRequestedAmount());
		}

		userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited: userTransferCountsVO = " + userTransferCountsVO);
		}
	}

	/**
	 * Method updateChannelToChannelTransferCounts
	 * this method is to update counts and values for both (Sender and Receiver)
	 * of the users.
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param curDate
	 * @param isFromWeb
	 * @param p_forwardPath
	 * @return int
	 * @throws BTSLBaseException
	 */
	public static int updateChannelToChannelTransferCounts(Connection con, ChannelTransferVO channelTransferVO, Date curDate, boolean isFromWeb, String p_forwardPath) throws BTSLBaseException {
		final String methodName = "updateChannelToChannelTransferCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", curDate : ");
			loggerValue.append(curDate);
			loggerValue.append(", isFromWeb : ");
			loggerValue.append(isFromWeb);
			loggerValue.append(", p_forwardPath : ");
			loggerValue.append(p_forwardPath);
			log.debug(methodName,loggerValue );
		}
		int updateCount = 0;

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		String fromUserID = null;
		String toUserID = null;

		fromUserID = channelTransferVO.getFromUserID();
		toUserID = channelTransferVO.getToUserID();

		UserTransferCountsVO fromUserCountsVO = userTransferCountsDAO.loadTransferCounts(con, channelTransferVO.getFromUserID(), true);
		UserTransferCountsVO toUserCountsVO = userTransferCountsDAO.loadTransferCounts(con, channelTransferVO.getToUserID(), true);

		boolean toFlag = true;
		boolean fromFlag = true;
		/*
		 * This case arise if it is the first transaction of the user
		 */
		if (BTSLUtil.isNullObject(toUserCountsVO)) {
			toFlag = false;
			toUserCountsVO = new UserTransferCountsVO();

		}
		/*
		 * This case arise if it is the first transaction of the user
		 */
		if (BTSLUtil.isNullObject(fromUserCountsVO)) {
			fromFlag = false;
			fromUserCountsVO = new UserTransferCountsVO();
		}
		if(SystemPreferences.USERWISE_LOAN_ENABLE) {

			Map hashmap = checkUserLoanstatusAndAmount(con, channelTransferVO);
			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
				final String args[] = { PretupsBL.getDisplayAmount((long)hashmap.get(PretupsI.WITHDRAW_AMOUNT)) };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.LOAN_SETTLEMENT_PENDING,args);
			}

			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
				UserLoanWithdrawBL  userLoanWithdrawBL = new UserLoanWithdrawBL();
				userLoanWithdrawBL.autoChannelLoanSettlement(channelTransferVO, PretupsI.USER_LOAN_REQUEST_TYPE,(long)hashmap.get(PretupsI.WITHDRAW_AMOUNT));
			}

		}
		else {
			Map hashmap = checkSOSstatusAndAmount(con, toUserCountsVO,channelTransferVO);
			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
				final String args[] = { channelTransferVO.getToUserName() };
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.SOS_PENDING_FOR_SETTLEMENT, 0, args,	p_forwardPath);
			}
			if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
			}
			Map lrHashMap = checkLRstatusAndAmount(con, toUserCountsVO, channelTransferVO);
			if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
				channelTransferVO.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
			}

		}

		if (channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)) {
			/*
			 * check and reset counters for the FROM USER.
			 */
			checkResetCountersAfterPeriodChange(fromUserCountsVO, curDate);
			final String outCountMessage = transferOutCountsCheck(con, fromUserCountsVO, channelTransferVO.getSenderTxnProfile(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getTransferMRP());
			if (outCountMessage != null) {
				final String args[] = { channelTransferVO.getFromUserName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, outCountMessage, 0, args, p_forwardPath);
			}
			fromUserCountsVO.setLastTransferDate(curDate);

			/*
			 * check and reset counters for the TO USER.
			 */
			checkResetCountersAfterPeriodChange(toUserCountsVO, curDate);
			final String inCountMessage = transferInCountsCheck(con, toUserCountsVO, channelTransferVO.getReceiverTxnProfile(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getTransferMRP());
			if (inCountMessage != null) {
				final String args[] = { channelTransferVO.getToUserName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, inCountMessage, 0, args, p_forwardPath);
			}
			toUserCountsVO.setLastTransferDate(curDate);

			/*
			 * set the counts and value information for both TO/FROM user.
			 */
			fromUserCountsVO.setUserID(fromUserID);
			fromUserCountsVO.setDailyOutCount(fromUserCountsVO.getDailyOutCount() + 1);
			fromUserCountsVO.setWeeklyOutCount(fromUserCountsVO.getWeeklyOutCount() + 1);
			fromUserCountsVO.setMonthlyOutCount(fromUserCountsVO.getMonthlyOutCount() + 1);
			fromUserCountsVO.setDailyOutValue(fromUserCountsVO.getDailyOutValue() + channelTransferVO.getTransferMRP());
			fromUserCountsVO.setWeeklyOutValue(fromUserCountsVO.getWeeklyOutValue() + channelTransferVO.getTransferMRP());
			fromUserCountsVO.setMonthlyOutValue(fromUserCountsVO.getMonthlyOutValue() + channelTransferVO.getTransferMRP());
			fromUserCountsVO.setLastOutTime(curDate);

			toUserCountsVO.setUserID(toUserID);
			toUserCountsVO.setDailyInCount(toUserCountsVO.getDailyInCount() + 1);
			toUserCountsVO.setWeeklyInCount(toUserCountsVO.getWeeklyInCount() + 1);
			toUserCountsVO.setMonthlyInCount(toUserCountsVO.getMonthlyInCount() + 1);
			toUserCountsVO.setDailyInValue(toUserCountsVO.getDailyInValue() + channelTransferVO.getTransferMRP());
			toUserCountsVO.setWeeklyInValue(toUserCountsVO.getWeeklyInValue() + channelTransferVO.getTransferMRP());
			toUserCountsVO.setMonthlyInValue(toUserCountsVO.getMonthlyInValue() + channelTransferVO.getTransferMRP());
			toUserCountsVO.setLastInTime(curDate);
		}
		/*
		 * In case of RETURN (as both Return and Withdraw) we have to check only
		 * the values not the counts.
		 */
		else if (channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN)) {
			fromUserCountsVO.setUserID(fromUserID);

			checkResetCountersAfterPeriodChange(fromUserCountsVO, curDate);

			if (fromUserCountsVO.getDailyInValue() >= channelTransferVO.getTransferMRP()) {
				fromUserCountsVO.setDailyInValue(fromUserCountsVO.getDailyInValue() - channelTransferVO.getTransferMRP());
			}
			if (fromUserCountsVO.getWeeklyInValue() >= channelTransferVO.getTransferMRP()) {
				fromUserCountsVO.setWeeklyInValue(fromUserCountsVO.getWeeklyInValue() - channelTransferVO.getTransferMRP());
			}
			if (fromUserCountsVO.getMonthlyInValue() >= channelTransferVO.getTransferMRP()) {
				fromUserCountsVO.setMonthlyInValue(fromUserCountsVO.getMonthlyInValue() - channelTransferVO.getTransferMRP());
			}
			fromUserCountsVO.setLastInTime(curDate);

			toUserCountsVO.setUserID(toUserID);
			checkResetCountersAfterPeriodChange(toUserCountsVO, curDate);

			if (toUserCountsVO.getDailyOutValue() >= channelTransferVO.getTransferMRP()) {
				toUserCountsVO.setDailyOutValue(toUserCountsVO.getDailyOutValue() - channelTransferVO.getTransferMRP());
			}
			if (toUserCountsVO.getWeeklyOutValue() >= channelTransferVO.getTransferMRP()) {
				toUserCountsVO.setWeeklyOutValue(toUserCountsVO.getWeeklyOutValue() - channelTransferVO.getTransferMRP());
			}
			if (toUserCountsVO.getMonthlyOutValue() >= channelTransferVO.getTransferMRP()) {
				toUserCountsVO.setMonthlyOutValue(toUserCountsVO.getMonthlyOutValue() - channelTransferVO.getTransferMRP());
			}
			toUserCountsVO.setLastOutTime(curDate);
		}

		fromUserCountsVO.setLastTransferID(channelTransferVO.getTransferID());
		fromUserCountsVO.setLastTransferDate(curDate);
		toUserCountsVO.setLastTransferID(channelTransferVO.getTransferID());
		toUserCountsVO.setLastTransferDate(curDate);
		updateCount = userTransferCountsDAO.updateUserTransferCounts(con, fromUserCountsVO, fromFlag);
		updateCount = userTransferCountsDAO.updateUserTransferCounts(con, toUserCountsVO, toFlag);
        
		if(updateCount > 0 && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()) && channelTransferVO.getUserOTFCountsVO() != null)
		{
			ArrayList itemList = channelTransferVO.getChannelTransferitemsVOList();
			ChannelTransferItemsVO channleTransferItemsVO = null;
			int updateCount1 = 0;
			for(int i=0; i<itemList.size();i++ ){
				channleTransferItemsVO = (ChannelTransferItemsVO)itemList.get(i);
				updateCount1=userTransferCountsDAO.updateUserOTFCounts(con, channleTransferItemsVO.getUserOTFCountsVO());
				if (updateCount1 <= 0) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
				}
			}
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited updateCount = " );
			loggerValue.append(updateCount);
			log.debug(methodName, loggerValue);
		}

		return updateCount;
	}

	/**
	 * Method updateChannelToChannelTransferOutSideCounts()
	 * This method is to update OUTSIDE counts and values for both (Sender and
	 * Receiver) of the users.
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param curDate
	 * @param isFromWeb
	 * @param p_forwardPath
	 * @return int
	 * @throws BTSLBaseException
	 */
	public static int updateChannelToChannelTransferOutSideCounts(Connection con, ChannelTransferVO channelTransferVO, Date curDate, boolean isFromWeb, String p_forwardPath) throws BTSLBaseException {
		final String methodName = "updateChannelToChannelTransferOutSideCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", curDate : ");
			loggerValue.append(curDate);
			loggerValue.append(", isFromWeb : ");
			loggerValue.append(isFromWeb);
			loggerValue.append(", p_forwardPath : ");
			loggerValue.append(p_forwardPath);
			log.debug(methodName,loggerValue );
		}
		int updateCount = 0;
		// ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

		UserTransferCountsVO fromUserCountsVO = userTransferCountsDAO.loadTransferCounts(con, channelTransferVO.getFromUserID(), true);
		UserTransferCountsVO toUserCountsVO = userTransferCountsDAO.loadTransferCounts(con, channelTransferVO.getToUserID(), true);

		boolean toFlag = true;
		boolean fromFlag = true;
		/*
		 * This case arise if it is the first transaction of the user
		 */
		if(BTSLUtil.isNullObject(toUserCountsVO)) {
			toFlag = false;
			toUserCountsVO = new UserTransferCountsVO();
		}
		/*
		 * This case arise if it is the first transaction of the user
		 */
		if (BTSLUtil.isNullObject(fromUserCountsVO)) {
			fromFlag = false;
			fromUserCountsVO = new UserTransferCountsVO();
		}

		final String fromUserID = channelTransferVO.getFromUserID();
		final String toUserID = channelTransferVO.getToUserID();
		if (channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)) {
			/*
			 * check and reset counters for the FROM USER.
			 */
			checkResetCountersAfterPeriodChange(fromUserCountsVO, curDate);
			final String outCountMessage = outsideTransferOutCounts(con, fromUserCountsVO, channelTransferVO.getSenderTxnProfile(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getTransferMRP());
			if (outCountMessage != null) {
				final String args[] = { channelTransferVO.getFromUserName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, outCountMessage, 0, args, p_forwardPath);
			}
			/*
			 * check and reset counters for the TO USER.
			 */
			checkResetCountersAfterPeriodChange(toUserCountsVO, curDate);
			final String inCountMessage = outsideTransferINCounts(con, toUserCountsVO, channelTransferVO.getReceiverTxnProfile(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getTransferMRP());
			if (inCountMessage != null) {
				final String args[] = { channelTransferVO.getToUserName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, inCountMessage, 0, args, p_forwardPath);
			}
			/*
			 * set the counts and value information for both TO/FROM user.
			 */
			fromUserCountsVO.setUserID(fromUserID);
			fromUserCountsVO.setUnctrlDailyOutCount(fromUserCountsVO.getUnctrlDailyOutCount() + 1);
			fromUserCountsVO.setUnctrlWeeklyOutCount(fromUserCountsVO.getUnctrlWeeklyOutCount() + 1);
			fromUserCountsVO.setUnctrlMonthlyOutCount(fromUserCountsVO.getUnctrlMonthlyOutCount() + 1);
			fromUserCountsVO.setUnctrlDailyOutValue(fromUserCountsVO.getUnctrlDailyOutValue() + channelTransferVO.getTransferMRP());
			fromUserCountsVO.setUnctrlWeeklyOutValue(fromUserCountsVO.getUnctrlWeeklyOutValue() + channelTransferVO.getTransferMRP());
			fromUserCountsVO.setUnctrlMonthlyOutValue(fromUserCountsVO.getUnctrlMonthlyOutValue() + channelTransferVO.getTransferMRP());
			fromUserCountsVO.setOutsideLastOutTime(curDate);

			toUserCountsVO.setUserID(toUserID);
			toUserCountsVO.setUnctrlDailyInCount(toUserCountsVO.getUnctrlDailyInCount() + 1);
			toUserCountsVO.setUnctrlWeeklyInCount(toUserCountsVO.getUnctrlWeeklyInCount() + 1);
			toUserCountsVO.setUnctrlMonthlyInCount(toUserCountsVO.getUnctrlMonthlyInCount() + 1);
			toUserCountsVO.setUnctrlDailyInValue(toUserCountsVO.getUnctrlDailyInValue() + channelTransferVO.getTransferMRP());
			toUserCountsVO.setUnctrlWeeklyInValue(toUserCountsVO.getUnctrlWeeklyInValue() + channelTransferVO.getTransferMRP());
			toUserCountsVO.setUnctrlMonthlyInValue(toUserCountsVO.getUnctrlMonthlyInValue() + channelTransferVO.getTransferMRP());
			toUserCountsVO.setOutsideLastInTime(curDate);
		}
		/*
		 * In case of RETURN (as both Return and Withdraw) we have to check only
		 * the values not the counts.
		 */
		else if (channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN)) {
			/*
			 * check and reset counters for the FROM USER.
			 */
			fromUserCountsVO.setUserID(fromUserID);
			checkResetCountersAfterPeriodChange(fromUserCountsVO, curDate);
			if (fromUserCountsVO.getUnctrlDailyInValue() >= channelTransferVO.getTransferMRP()) {
				fromUserCountsVO.setUnctrlDailyInValue(fromUserCountsVO.getUnctrlDailyInValue() - channelTransferVO.getTransferMRP());
			}
			if (fromUserCountsVO.getUnctrlWeeklyInValue() >= channelTransferVO.getTransferMRP()) {
				fromUserCountsVO.setUnctrlWeeklyInValue(fromUserCountsVO.getUnctrlWeeklyInValue() - channelTransferVO.getTransferMRP());
			}
			if (fromUserCountsVO.getUnctrlMonthlyInValue() >= channelTransferVO.getTransferMRP()) {
				fromUserCountsVO.setUnctrlMonthlyInValue(fromUserCountsVO.getUnctrlMonthlyInValue() - channelTransferVO.getTransferMRP());
			}
			fromUserCountsVO.setOutsideLastInTime(curDate);

			toUserCountsVO.setUserID(toUserID);
			checkResetCountersAfterPeriodChange(toUserCountsVO, curDate);
			if (toUserCountsVO.getUnctrlDailyOutValue() >= channelTransferVO.getTransferMRP()) {
				toUserCountsVO.setUnctrlDailyOutValue(toUserCountsVO.getUnctrlDailyOutValue() - channelTransferVO.getTransferMRP());
			}
			if (toUserCountsVO.getUnctrlWeeklyOutValue() >= channelTransferVO.getTransferMRP()) {
				toUserCountsVO.setUnctrlWeeklyOutValue(toUserCountsVO.getUnctrlWeeklyOutValue() - channelTransferVO.getTransferMRP());
			}
			if (toUserCountsVO.getUnctrlMonthlyOutValue() >= channelTransferVO.getTransferMRP()) {
				toUserCountsVO.setUnctrlMonthlyOutValue(toUserCountsVO.getUnctrlMonthlyOutValue() - channelTransferVO.getTransferMRP());
			}
			toUserCountsVO.setOutsideLastOutTime(curDate);
		}
		fromUserCountsVO.setLastTransferID(channelTransferVO.getTransferID());
		fromUserCountsVO.setLastTransferDate(curDate);
		toUserCountsVO.setLastTransferID(channelTransferVO.getTransferID());
		toUserCountsVO.setLastTransferDate(curDate);
		updateCount = userTransferCountsDAO.updateUserTransferCounts(con, fromUserCountsVO, fromFlag);
		updateCount = userTransferCountsDAO.updateUserTransferCounts(con, toUserCountsVO, toFlag);

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited updateCount = " );
			loggerValue.append(updateCount);
			log.debug(methodName, loggerValue);
		}

		return updateCount;
	}

	/**
	 * Method loadChannelTransferItemsWithBalances()
	 * First this method load the transfer items list of the specified
	 * transferID.
	 * Than it loads the user's balance and then modify the transfer item list
	 * with userbalance of the
	 * corresponding product code.
	 * 
	 * @param con
	 * @param p_transferId
	 * @param networkCode
	 * @param p_networkCodeFor
	 * @param userID
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList<ChannelTransferItemsVO> loadChannelTransferItemsWithBalances(Connection con, String p_transferId, String networkCode, String p_networkCodeFor, String userID) throws BTSLBaseException {
		final String methodName = "loadChannelTransferItemsWithBalances";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered p_transferId : ");
			loggerValue.append(p_transferId);
			loggerValue.append(", networkCode : ");
			loggerValue.append(networkCode);
			loggerValue.append(", p_roamNetworkCode : ");
			loggerValue.append(p_networkCodeFor);
			loggerValue.append(", userID : ");
			loggerValue.append(userID);
			log.debug(methodName,loggerValue);
		}
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
		final ArrayList<ChannelTransferItemsVO> itemsList = channelTransferDAO.loadChannelTransferItems(con, p_transferId);
		final ArrayList<UserBalancesVO> balancesList = userBalancesDAO.loadUserBalanceList(con, userID, networkCode, p_networkCodeFor);
		ChannelTransferItemsVO channelTransferItemsVO = null;
		UserBalancesVO balancesVO = null;
		long balance=0;
		boolean mwFlag=false;
		if (balancesList != null && !balancesList.isEmpty()) {
			for (int i = 0, k = itemsList.size(); i < k; i++) {
				channelTransferItemsVO = itemsList.get(i);
				for (int m = 0, n = balancesList.size(); m < n; m++) {
					balancesVO = balancesList.get(m);
					/** START: Birendra: 28JAN2015 */  //&& balancesVO.getWalletCode().equals(channelTransferItemsVO.getUserWallet())
					if (userProductMultipleWallet) {
						if (balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
							balance+=balancesVO.getBalance();
							mwFlag=true;
						}
					} else {
						if (balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
							channelTransferItemsVO.setBalance(balancesVO.getBalance());
						}
					}
				}
				if(mwFlag){
					channelTransferItemsVO.setBalance(balance);
				}
			}
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited  ");
			loggerValue.append(itemsList.size());
			log.debug(methodName,  loggerValue );
		}
		return itemsList;
	}

	/**
	 * Method calculateMRPWithTaxAndDiscount()
	 * To calculate the taxes on the selected product list
	 * 
	 * @param p_transferItemsList
	 * @param p_txnTypen
	 *            String
	 * @throws SQLException 
	 * @throws BTSLBaseException 
	 * @throws ParseException 
	 * @throws Exception
	 */
	public static void calculateMRPWithTaxAndDiscount(ChannelTransferVO channelTransferVO, String p_txnType) throws SQLException, BTSLBaseException, ParseException {
		final String methodName = "calculateMRPWithTaxAndDiscount";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered  channelTransferVO = " + channelTransferVO.toString() + ", p_txnType = " + p_txnType);
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		CommissionProfileDeatilsVO  commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
		UserTransferCountsDAO userTransferCountsDAO= new UserTransferCountsDAO();
		long productCost = 0;
		long value = 0;
		ChannelUserVO userVO = null;
		final ArrayList p_transferItemsList = channelTransferVO.getChannelTransferitemsVOList();
		boolean othComChnl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
		String dualWalletAllowedGateways = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DW_ALLOWED_GATEWAYS);
		String dualWalletCommissionCal = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DW_COMMISSION_CAL);
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
		ArrayList otherCommissionTypeList = new ArrayList();
		if(othComChnl){
			if(!p_txnType.equals(PretupsI.TRANSFER_CATEGORY_FOC)&&
                    !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(channelTransferVO.getTransferSubType())
                    &&!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(channelTransferVO.getTransferSubType())) {
				commissionProfileDAO.loadOtherCommissionProfile(con, channelTransferVO.getCommProfileSetId(),channelTransferVO.getCommProfileVersion() ,p_transferItemsList,p_txnType);
				if (log.isDebugEnabled()) {
					log.debug(methodName, "channelTransferVO.getToUserMsisdn() = " + channelTransferVO.getToUserMsisdn());
				}
				if(channelTransferVO.getToUserMsisdn()!=null) {
						userVO = new ChannelUserWebDAO().loadChannelUserDetailsByLoginIDANDORMSISDN(con, channelTransferVO.getToUserMsisdn(), "");
					//Validation Based on defined and configured for Other Commission as per user profile
					for(int i = 0 , k = p_transferItemsList.size() ; i < k ; i++)
					{
						channelTransferItemsVO = (ChannelTransferItemsVO) p_transferItemsList.get(i);
						if (log.isDebugEnabled()) {
							log.debug(methodName, "For Loop Check :: channelTransferItemsVO.isOthSlabDefine() = " + channelTransferItemsVO.isOthSlabDefine());
						}
						if(channelTransferItemsVO.isOthSlabDefine() && userVO!=null) {
							otherCommissionTypeList.clear();
							otherCommissionTypeList = LookupsCache.loadLookupDropDown(PretupsI.OTHER_COMMISSION_TYPE,true);
							if(otherCommissionTypeList!=null && otherCommissionTypeList.size()> 0) {
								for(int index=0;index<otherCommissionTypeList.size();index++) {
									ListValueVO listValueVO = (ListValueVO)otherCommissionTypeList.get(index);
									if (log.isDebugEnabled()) {
										log.debug(methodName, "channelTransferItemsVO.getOthCommProfType() = " + channelTransferItemsVO.getOthCommProfType()+", channelTransferVO.getRequestGatewayCode()="+channelTransferVO.getRequestGatewayCode()+", channelTransferItemsVO.getTransferID()="+channelTransferItemsVO.getTransferID());
									}
									if(channelTransferItemsVO.getOthCommProfType().equalsIgnoreCase(PretupsI.OTHER_COMMISSION_TYPE_GATEWAY) && listValueVO.getValue().equalsIgnoreCase(PretupsI.OTHER_COMMISSION_TYPE_GATEWAY)) {
										if(dualWalletAllowedGateways.contains(channelTransferVO.getRequestGatewayCode()) && channelTransferItemsVO.getOthCommProfValue().equalsIgnoreCase(channelTransferVO.getRequestGatewayCode())) {
											channelTransferItemsVO.setOthSlabDefine(true);
										}//Handling of Reversal transaction
										else if(channelTransferItemsVO.getTransferID()!=null) {
											//Handling of  O2C Transactions
											if(p_txnType.equalsIgnoreCase(PretupsI.TRANSFER_TYPE_O2C))  {
												channelTransferItemsVO.setOthSlabDefine(false);
											} else {
												channelTransferItemsVO.setOthSlabDefine(true);
											}
										} else {
											channelTransferItemsVO.setOthSlabDefine(false);
										}
										if (log.isDebugEnabled()) {
											log.debug(methodName, "channelTransferItemsVO.isOthSlabDefine() = " + channelTransferItemsVO.isOthSlabDefine());
										}
										break;
									} else if(channelTransferItemsVO.getOthCommProfType().equalsIgnoreCase(PretupsI.OTHER_COMMISSION_TYPE_CATEGORY) && listValueVO.getValue().equalsIgnoreCase(PretupsI.OTHER_COMMISSION_TYPE_CATEGORY)) {
										if(channelTransferItemsVO.getOthCommProfValue().equalsIgnoreCase(userVO.getCategoryCode())) {
											channelTransferItemsVO.setOthSlabDefine(true);
										} else {
											channelTransferItemsVO.setOthSlabDefine(false);
										}
										if (log.isDebugEnabled()) {
											log.debug(methodName, "channelTransferItemsVO.isOthSlabDefine() = " + channelTransferItemsVO.isOthSlabDefine());
										}
										break;
									} else if(channelTransferItemsVO.getOthCommProfType().equalsIgnoreCase(PretupsI.OTHER_COMMISSION_TYPE_GRADE) && listValueVO.getValue().equalsIgnoreCase(PretupsI.OTHER_COMMISSION_TYPE_GRADE)){
										if(channelTransferItemsVO.getOthCommProfValue().equalsIgnoreCase(userVO.getUserGrade())) {
											channelTransferItemsVO.setOthSlabDefine(true);
										} else {
											channelTransferItemsVO.setOthSlabDefine(false);
										}
										if (log.isDebugEnabled()) {
											log.debug(methodName, "channelTransferItemsVO.isOthSlabDefine() = " + channelTransferItemsVO.isOthSlabDefine());
										}
										break;
									}
								}//End of Loop
							}//End of IF
						}
					}//End of Loop
				}
			}
		}


		Boolean addnl=false;
		DateFormat df = new SimpleDateFormat(PretupsI.DATE_FORMAT);
		Date dateobj = new Date();
		Double defaultDouble = new Double("0");
		for (int i = 0, k = p_transferItemsList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) p_transferItemsList.get(i);
			if(othComChnl){
				if(!channelTransferItemsVO.isOthSlabDefine()){
						channelTransferItemsVO.setOthCommSetId("");
						channelTransferItemsVO.setOthCommRate(0);
						channelTransferItemsVO.setOthCommType("AMT");
				} else {
					if (log.isDebugEnabled()){
                            log.debug(methodName, "Before Checking other commission default value channelTransferVO="+channelTransferVO);
                    }
                	//if Other commission profile is defined
					if(userVO!=null){
						if(channelTransferItemsVO.getOthCommValue()<=0) {
							if(PretupsI.OTHER_COMMISSION_TYPE_CATEGORY.equalsIgnoreCase(channelTransferItemsVO.getOthCommProfType()) && !userVO.getCategoryCode().equalsIgnoreCase(channelTransferItemsVO.getOthCommProfValue())){
									channelTransferItemsVO.setOthCommSetId("");
									channelTransferItemsVO.setOthCommRate(0);
							}
							else if(PretupsI.OTHER_COMMISSION_TYPE_GRADE.equalsIgnoreCase(channelTransferItemsVO.getOthCommProfType()) && !userVO.getUserGrade().equalsIgnoreCase(channelTransferItemsVO.getOthCommProfValue())){
									channelTransferItemsVO.setOthCommSetId("");
									channelTransferItemsVO.setOthCommRate(0);
							}
							else if(PretupsI.OTHER_COMMISSION_TYPE_GATEWAY.equalsIgnoreCase(channelTransferItemsVO.getOthCommProfType()) && !channelTransferVO.getRequestGatewayCode().equalsIgnoreCase(channelTransferItemsVO.getOthCommProfValue())){
									channelTransferItemsVO.setOthCommSetId("");
									channelTransferItemsVO.setOthCommRate(0);
							}
						}
					}
				}
				if (log.isDebugEnabled()){
                        log.debug(methodName, "After Checking other commission default value channelTransferVO="+channelTransferVO+", channelTransferItemsVO.isOthSlabDefine()="+channelTransferItemsVO.isOthSlabDefine());
                }
			}
			productCost = (long) Math.round(Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());

			// In case of FOC Commission will not be calculated
			if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(channelTransferVO.getTransferSubType()) && !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(channelTransferVO.getTransferSubType())) {
				if(othComChnl) {
					if(channelTransferItemsVO.isOthSlabDefine()) {
						if(PretupsI.DW_COMMISSION_CAL_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal) ) {
							//Default case - 
							value = 0;
							channelTransferItemsVO.setCommRate(0);
						} else if(PretupsI.DW_COMMISSION_CAL_BASE_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal) ) {
							value = calculatorI.calculateCommission(channelTransferItemsVO.getCommType(), channelTransferItemsVO.getCommRate(), productCost);
						} 
					} else {
						//Default case -
						if (log.isDebugEnabled()){
	                        log.debug(methodName, "Default case channelTransferItemsVO.getCommType()="+channelTransferItemsVO.getCommType()+", channelTransferItemsVO.getCommRate()="+channelTransferItemsVO.getCommRate());
						}
						value = calculatorI.calculateCommission(channelTransferItemsVO.getCommType(), channelTransferItemsVO.getCommRate(), productCost);
					}
				} else {
					//If Other Commission Flag is false
					value = calculatorI.calculateCommission(channelTransferItemsVO.getCommType(), channelTransferItemsVO.getCommRate(), productCost);
				}

			}
			if(value < 0) {
				channelTransferItemsVO.setCommValue(0);
				channelTransferItemsVO.setCommissionValuePosi(0);
			}
			else {
				channelTransferItemsVO.setCommValue(value);
				channelTransferItemsVO.setCommissionValuePosi(value);
			}
			
			
			value = 0;
			value = calculatorI.calculateDiscount(channelTransferItemsVO.getDiscountType(), channelTransferItemsVO.getDiscountRate(), productCost);
			channelTransferItemsVO.setDiscountValue(value);
			value = 0;
			if(othComChnl && channelTransferItemsVO.isOthSlabDefine()) {
				if(!p_txnType.equals(PretupsI.TRANSFER_CATEGORY_FOC)&&
                        !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(channelTransferVO.getTransferSubType())
                        &&!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(channelTransferVO.getTransferSubType())) {
					value = calculatorI.calculateCommission(channelTransferItemsVO.getOthCommType(),channelTransferItemsVO.getOthCommRate(),productCost);
					channelTransferItemsVO.setOthCommValue(value);
					value = 0;
				}
			}
			/*
			 * To check whether tax is applicable for transaction or not.We have
			 * the following three type of the
			 * transaction and following corresponding check condition.
			 * 1. In case of O2C transfer Tax calculation is mandatory.
			 * 2. In case of C2C transfer Tax calculation is based on the
			 * TAX_APPLICABLE_ON_C2C flag of the
			 * commission profile.
			 * 3. In case of FOC transfer Tax calculation is based on the
			 * TAX_APPLICABLE_ON_FOC flag of the
			 * commission profile.
			 */
			if (p_txnType.equals(PretupsI.TRANSFER_TYPE_O2C) || (p_txnType.equals(PretupsI.TRANSFER_TYPE_C2C) && PretupsI.YES.equals(channelTransferItemsVO
					.getTaxOnChannelTransfer())) || (p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && PretupsI.YES.equals(channelTransferItemsVO.getTaxOnFOCTransfer()))) {
				if(othComChnl) {
					if(channelTransferItemsVO.isOthSlabDefine()) {
						if(PretupsI.DW_COMMISSION_CAL_BASE_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
							value = calculatorI.calculateTax1(channelTransferItemsVO.getTax1Type(), channelTransferItemsVO.getTax1Rate(), productCost);							
						} else if(PretupsI.DW_COMMISSION_CAL_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
							channelTransferItemsVO.setTax1Rate(defaultDouble);
						}
					} else {
						value = calculatorI.calculateTax1(channelTransferItemsVO.getTax1Type(), channelTransferItemsVO.getTax1Rate(), productCost);						
					}
				} else {
					value = calculatorI.calculateTax1(channelTransferItemsVO.getTax1Type(), channelTransferItemsVO.getTax1Rate(), productCost);
				}
				channelTransferItemsVO.setTax1Value(value);
				value = 0;
				if(othComChnl) {
					if(channelTransferItemsVO.isOthSlabDefine()) {
						if(PretupsI.DW_COMMISSION_CAL_BASE_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
							value = calculatorI.calculateTax2(channelTransferItemsVO.getTax2Type(), channelTransferItemsVO.getTax2Rate(), channelTransferItemsVO.getTax1Value());
						} else if(PretupsI.DW_COMMISSION_CAL_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
							channelTransferItemsVO.setTax2Rate(defaultDouble);
						}
					} else {
						value = calculatorI.calculateTax2(channelTransferItemsVO.getTax2Type(), channelTransferItemsVO.getTax2Rate(), channelTransferItemsVO.getTax1Value());
					}
				} else {
					value = calculatorI.calculateTax2(channelTransferItemsVO.getTax2Type(), channelTransferItemsVO.getTax2Rate(), channelTransferItemsVO.getTax1Value());
				}
				channelTransferItemsVO.setTax2Value(value);
				value = 0;
				// set commision value as 0 , becoz commision not calculated on
				// the commision in case pf withdraw and return.
				if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferSubType())) {
					channelTransferItemsVO.setCommValue(0);
				}
				// In case of FOC Tax3 will not be calculated
				if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
					if(othComChnl) {
						if(channelTransferItemsVO.isOthSlabDefine()) {
							if(PretupsI.DW_COMMISSION_CAL_BASE_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
								value = calculatorI.calculateTax3(channelTransferItemsVO.getTax3Type(), channelTransferItemsVO.getTax3Rate(), channelTransferItemsVO.getCommValue()+channelTransferItemsVO.getOthCommValue());
							}  else if(PretupsI.DW_COMMISSION_CAL_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
								channelTransferItemsVO.setTax3Rate(defaultDouble);
							}
						} else {
							value = calculatorI.calculateTax3(channelTransferItemsVO.getTax3Type(), channelTransferItemsVO.getTax3Rate(), channelTransferItemsVO.getCommValue());
						}
					} else {
						value = calculatorI.calculateTax3(channelTransferItemsVO.getTax3Type(), channelTransferItemsVO.getTax3Rate(), channelTransferItemsVO.getCommValue());
					}
				}
				if(value < 0) {
					channelTransferItemsVO.setTax3Value(0);
				}
				else {
				channelTransferItemsVO.setTax3Value(value);
				}
				value = 0;
			}
			if(othComChnl) {
				 if(!p_txnType.equals(PretupsI.TRANSFER_CATEGORY_FOC)&&
                         !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(channelTransferVO.getTransferSubType())
                         &&!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(channelTransferVO.getTransferSubType())) {
					 if(channelTransferItemsVO.isOthSlabDefine()) {
						 if(PretupsI.DW_COMMISSION_CAL_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)) {
							 value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getOthCommValue(),channelTransferItemsVO.getUnitValue(),channelTransferItemsVO.getTax3Value());
							 channelTransferItemsVO.setCommRate(0);
						 } else  if(PretupsI.DW_COMMISSION_CAL_BASE_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
							 value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getCommValue()+channelTransferItemsVO.getOthCommValue(),channelTransferItemsVO.getUnitValue(),channelTransferItemsVO.getTax3Value());
						 }
				 	} else {
				 		value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getCommValue(), channelTransferItemsVO.getUnitValue(), channelTransferItemsVO.getTax3Value());
				 		channelTransferItemsVO.setOthCommRate(0);
				 	}
				 }
			} else {
				if(!p_txnType.equals(PretupsI.TRANSFER_CATEGORY_FOC)) {
					value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getCommValue(), channelTransferItemsVO.getUnitValue(), channelTransferItemsVO
					.getTax3Value());
				}
			}

			if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC))
			{
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{ 
					if (log.isDebugEnabled()){
                        log.debug(methodName, "channelTransferItemsVO.getOtfAmount()="+channelTransferItemsVO.getOtfAmount());
					}
					value= value+ channelTransferItemsVO.getOtfAmount();

				}
			}

			channelTransferItemsVO.setCommQuantity(value);
			long payableAmount = 0;
			long netPayableAmount = 0;

			if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
					.equalsIgnoreCase(channelTransferVO.getTransferSubType())) {
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(channelTransferItemsVO.getRequiredQuantity());
				channelTransferItemsVO.setProductTotalMRP(productCost);
				channelTransferItemsVO.setPayableAmount(productCost);
				channelTransferItemsVO.setNetPayableAmount(productCost);
			}// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
			// true
			else if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && !p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
					.equalsIgnoreCase(channelTransferVO.getTransferSubType())) {
				value = 0;
				value = calculatorI.calculateReceiverCreditQuantity(channelTransferItemsVO.getRequestedQuantity(), channelTransferItemsVO.getUnitValue(),
						channelTransferItemsVO.getCommQuantity());
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(value);
				channelTransferItemsVO.setPayableAmount(productCost);
				channelTransferItemsVO.setNetPayableAmount(productCost);
				channelTransferItemsVO
				.setProductTotalMRP(channelTransferItemsVO.getReceiverCreditQty() * channelTransferItemsVO.getUnitValue() / (amountMultFactor));
			}// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
			// is false
			else if (!(PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) && !p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{ 
					if(othComChnl) {
						if(channelTransferItemsVO.isOthSlabDefine()) {
							 if(PretupsI.DW_COMMISSION_CAL_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)) {
								 payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()), channelTransferItemsVO.getOtfAmount() + channelTransferItemsVO.getOthCommValue(), channelTransferItemsVO.getDiscountValue()); 
							 } else if(PretupsI.DW_COMMISSION_CAL_BASE_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
								 payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()),channelTransferItemsVO.getCommValue() + channelTransferItemsVO.getOtfAmount() + channelTransferItemsVO.getOthCommValue(), channelTransferItemsVO.getDiscountValue());
							 }	
						} else {
							payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()),channelTransferItemsVO.getCommValue() + channelTransferItemsVO.getOtfAmount(), channelTransferItemsVO.getDiscountValue());
						}
					
					} else {
						payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()),channelTransferItemsVO.getCommValue() + channelTransferItemsVO.getOtfAmount(), channelTransferItemsVO.getDiscountValue());
					}
				}
				else
				{
					if(othComChnl)
					payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(),Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()),channelTransferItemsVO.getCommValue()+channelTransferItemsVO.getOthCommValue(),channelTransferItemsVO.getDiscountValue());
					else
					payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()),
							channelTransferItemsVO.getCommValue(), channelTransferItemsVO.getDiscountValue());
				}
				netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, channelTransferItemsVO.getTax3Value());
				channelTransferItemsVO.setPayableAmount(payableAmount);
				channelTransferItemsVO.setNetPayableAmount(netPayableAmount);
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(channelTransferItemsVO.getRequiredQuantity());
				channelTransferItemsVO.setProductTotalMRP(productCost);
			} else {
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(channelTransferItemsVO.getRequiredQuantity());
				channelTransferItemsVO.setProductTotalMRP(productCost);
				channelTransferItemsVO.setPayableAmount(payableAmount);
				channelTransferItemsVO.setNetPayableAmount(netPayableAmount);
			}
			channelTransferItemsVO.setApprovedQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
		}
		if(!channelTransferVO.isWeb() && (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()) 
				&& channelTransferVO.isTargetAchieved() && PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus()))
		{
			TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
			/*ChannelUserVO channelUserVO = new ChannelUserVO();
			channelUserVO.setUserID(channelTransferVO.getToUserID());
			tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con,channelUserVO);*/
			tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con,channelTransferVO.getToUserID(),channelTransferVO.getMessageArgumentList());
		} 
		}finally{
			if (mcomCon != null) {
				mcomCon.close("ChannelTransferBL#calculateMRPWithTaxAndDiscount");
				mcomCon = null;
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				log.error(methodName, "Exception " + e.getMessage());
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method prepareUserBalancesListForLogger.
	 * This method writes user balances in the logger file.
	 * 
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 */
	public static void prepareUserBalancesListForLogger(ChannelTransferVO channelTransferVO) {
		final String methodName = "prepareUserBalancesListForLogger";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ChannelTransferVO = " + channelTransferVO);
		}
		if (PretupsI.TRANSFER_TYPE_C2C.equals(channelTransferVO.getType())) {
			prepareC2CTxnBalanceLogger(channelTransferVO);
		} else {
			prepareO2CTxnBalanceLogger(channelTransferVO);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}

	}

	/**
	 * Method prepareO2CTxnBalanceLogger.
	 * 
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 */
	private static void prepareO2CTxnBalanceLogger(ChannelTransferVO channelTransferVO) {
		final String methodName = "prepareO2CTxnBalanceLogger";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			log.debug(methodName,  loggerValue );
		}

		final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();

		UserBalancesVO balancesVO = null;
		ChannelTransferItemsVO itemsVO = null;
		NetworkStockTxnVO networkStockTxnVO = null;
		UserBalancesVO balancesVoObj = new UserBalancesVO();
		NetworkStockTxnVO networkStockTxnVoObj = new NetworkStockTxnVO();
		for (int i = 0, k = itemsList.size(); i < k; i++) {
			itemsVO = (ChannelTransferItemsVO) itemsList.get(i);
			balancesVO = balancesVoObj;
			networkStockTxnVO = networkStockTxnVoObj;

			balancesVO.setLastTransferID(channelTransferVO.getTransferID());
			networkStockTxnVO.setReferenceNo(channelTransferVO.getTransferID());
			networkStockTxnVO.setTxnNo(channelTransferVO.getReferenceID());
			balancesVO.setNetworkCode(channelTransferVO.getNetworkCode());
			networkStockTxnVO.setNetworkCode(channelTransferVO.getNetworkCode());
			balancesVO.setNetworkFor(channelTransferVO.getNetworkCodeFor());
			networkStockTxnVO.setNetworkFor(channelTransferVO.getNetworkCodeFor());
			balancesVO.setProductCode(itemsVO.getProductCode());
			networkStockTxnVO.setProductCode(itemsVO.getProductCode());
			balancesVO.setRequestedQuantity(String.valueOf(itemsVO.getRequiredQuantity()));
			networkStockTxnVO.setRequestedQuantity(itemsVO.getRequiredQuantity());
			networkStockTxnVO.setApprovedQuantity(itemsVO.getRequiredQuantity());
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && PretupsI.TRANSFER_CATEGORY_SALE.equalsIgnoreCase(channelTransferVO.getTransferCategory())) {
				balancesVO.setQuantityToBeUpdated(itemsVO.getReceiverCreditQty());
			} else {
				balancesVO.setQuantityToBeUpdated(itemsVO.getRequiredQuantity());
			}

			balancesVO.setLastTransferType(channelTransferVO.getTransferSubType());

			networkStockTxnVO.setEntryType(channelTransferVO.getTransferSubType());

			balancesVO.setTransferCategory(channelTransferVO.getTransferCategory());
			networkStockTxnVO.setTxnCategory(channelTransferVO.getTransferCategory());
			balancesVO.setType(channelTransferVO.getType());
			networkStockTxnVO.setStockType(channelTransferVO.getType());
			balancesVO.setSource(channelTransferVO.getSource());
			balancesVO.setCreatedBy(channelTransferVO.getModifiedBy());
			balancesVO.setLastTransferOn(channelTransferVO.getModifiedOn());
			networkStockTxnVO.setCreatedBy(channelTransferVO.getTransferInitatedBy());
			networkStockTxnVO.setCreatedOn(channelTransferVO.getModifiedOn());
			networkStockTxnVO.setModifiedBy(channelTransferVO.getModifiedBy());
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && PretupsI.TRANSFER_CATEGORY_SALE.equalsIgnoreCase(channelTransferVO.getTransferCategory())) {
				balancesVO.setNetAmount(itemsVO.getReceiverCreditQty() * Long.parseLong(PretupsBL.getDisplayAmount(itemsVO.getUnitValue())));
			} else {
				balancesVO.setNetAmount(itemsVO.getNetPayableAmount());
			}
			if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
				balancesVO.setUserID(channelTransferVO.getToUserID());
				balancesVO.setPreviousBalance(itemsVO.getAfterTransReceiverPreviousStock());
				balancesVO.setEntryType(PretupsI.CREDIT);
				if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && PretupsI.TRANSFER_CATEGORY_SALE.equalsIgnoreCase(channelTransferVO.getTransferCategory())) {
					balancesVO.setBalance(balancesVO.getPreviousBalance() + itemsVO.getReceiverCreditQty());
				} else {
					balancesVO.setBalance(balancesVO.getPreviousBalance() + itemsVO.getRequiredQuantity());
				}
				balancesVO.setOtherInfo("COMMISSION PROFILE ID=" + channelTransferVO.getCommProfileSetId() + ",COMMISSION PROFILE VERSION=" + channelTransferVO
						.getCommProfileVersion() + ",APPROVED BY=" + channelTransferVO.getModifiedBy() + ", SECOND USER ID=" + channelTransferVO.getFromUserID());
				// Added to log user msisdn on 13/02/2008
				balancesVO.setUserMSISDN(channelTransferVO.getUserMsisdn());
				networkStockTxnVO.setUserID(channelTransferVO.getFromUserID());
				networkStockTxnVO.setPreviousStock(itemsVO.getAfterTransSenderPreviousStock());
				networkStockTxnVO.setTxnType(PretupsI.DEBIT);

				networkStockTxnVO.setPostStock(itemsVO.getAfterTransSenderPreviousStock() - itemsVO.getRequiredQuantity());
				networkStockTxnVO.setOtherInfo("COMMISSION PROFILE ID=" + channelTransferVO.getCommProfileSetId() + ",COMMISSION PROFILE VERSION=" + channelTransferVO
						.getCommProfileVersion() + ", SECOND USER ID=" + channelTransferVO.getToUserID() + ", TXN TYPE=" + channelTransferVO.getTransferType());
			} else {
				balancesVO.setUserID(channelTransferVO.getFromUserID());
				balancesVO.setPreviousBalance(itemsVO.getAfterTransSenderPreviousStock());
				balancesVO.setEntryType(PretupsI.DEBIT);
				balancesVO.setBalance(balancesVO.getPreviousBalance() - itemsVO.getRequiredQuantity());
				balancesVO.setOtherInfo("COMMISSION PROFILE ID=" + channelTransferVO.getCommProfileSetId() + ",COMMISSION PROFILE VERSION=" + channelTransferVO
						.getCommProfileVersion() + ",APPROVED BY=" + channelTransferVO.getModifiedBy() + ", SECOND USER ID=" + channelTransferVO.getToUserID());
				// Added to log user msisdn on 13/02/2008
				balancesVO.setUserMSISDN(channelTransferVO.getFromUserCode());
				networkStockTxnVO.setUserID(channelTransferVO.getToUserID());
				networkStockTxnVO.setPreviousStock(itemsVO.getAfterTransReceiverPreviousStock());
				networkStockTxnVO.setTxnType(PretupsI.CREDIT);
				networkStockTxnVO.setPostStock(itemsVO.getAfterTransReceiverPreviousStock() + itemsVO.getRequiredQuantity());
				networkStockTxnVO.setOtherInfo("COMMISSION PROFILE ID=" + channelTransferVO.getCommProfileSetId() + ",COMMISSION PROFILE VERSION=" + channelTransferVO
						.getCommProfileVersion() + ", SECOND USER ID=" + channelTransferVO.getFromUserID() + ", TXN TYPE=" + channelTransferVO.getTransferType());
			}

			BalanceLogger.log(balancesVO);
			NetworkStockLog.log(networkStockTxnVO);
			// Network stock transaction logger for commision
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && "T".equalsIgnoreCase(channelTransferVO.getTransferSubType()) && PretupsI.TRANSFER_CATEGORY_SALE
					.equalsIgnoreCase(channelTransferVO.getTransferCategory()) && !("S".equalsIgnoreCase(channelTransferVO.getTransactionMode())) && !(channelTransferVO.getSosFlag())) {
				ChannelTransferBL.prepareNetworkTxnCommisionBalanceLogger(channelTransferVO);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}

	}

	/**
	 * Method prepareC2CTxnBalanceLogger.
	 * 
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 */
	private static void prepareC2CTxnBalanceLogger(ChannelTransferVO channelTransferVO) {
		final String methodName = "prepareC2CTxnBalanceLogger";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			log.debug(methodName,  loggerValue);
		}

		final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
		UserBalancesVO balancesVoObj = new UserBalancesVO();
		UserBalancesVO balancesVO = null;
		ChannelTransferItemsVO itemsVO = null;
		for (int i = 0, k = itemsList.size(); i < k; i++) {
			itemsVO = (ChannelTransferItemsVO) itemsList.get(i);
			balancesVO = balancesVoObj;
			balancesVO.setLastTransferID(channelTransferVO.getTransferID());
			balancesVO.setUserID(channelTransferVO.getFromUserID());
			balancesVO.setNetworkCode(channelTransferVO.getNetworkCode());
			balancesVO.setNetworkFor(channelTransferVO.getNetworkCodeFor());
			balancesVO.setProductCode(itemsVO.getProductCode());
			balancesVO.setRequestedQuantity(String.valueOf(itemsVO.getRequiredQuantity()));
			balancesVO.setQuantityToBeUpdated(itemsVO.getRequiredQuantity());
			balancesVO.setLastTransferType(channelTransferVO.getTransferSubType());
			balancesVO.setTransferCategory(channelTransferVO.getTransferCategory());
			balancesVO.setType(channelTransferVO.getType());
			balancesVO.setSource(channelTransferVO.getSource());
			balancesVO.setCreatedBy(channelTransferVO.getCreatedBy());
			balancesVO.setLastTransferOn(channelTransferVO.getModifiedOn());
			balancesVO.setNetAmount(itemsVO.getNetPayableAmount());
			balancesVO
			.setOtherInfo("COMMISSION PROFILE ID=" + channelTransferVO.getCommProfileSetId() + ", PROFILE VERSION=" + channelTransferVO.getCommProfileVersion() + ", SECOND USER ID=" + channelTransferVO
					.getToUserID());
			balancesVO.setPreviousBalance(itemsVO.getAfterTransSenderPreviousStock());
			balancesVO.setEntryType(PretupsI.DEBIT);
			balancesVO.setBalance(balancesVO.getPreviousBalance() - itemsVO.getRequiredQuantity());
			// Added to log user msisdn on 13/02/2008
			balancesVO.setUserMSISDN(channelTransferVO.getFromUserCode());
			BalanceLogger.log(balancesVO);

			balancesVO.setUserID(channelTransferVO.getToUserID());
			balancesVO.setPreviousBalance(itemsVO.getAfterTransReceiverPreviousStock());
			balancesVO.setEntryType(PretupsI.CREDIT);
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
				balancesVO.setBalance(balancesVO.getPreviousBalance() + itemsVO.getReceiverCreditQty());
				balancesVO.setQuantityToBeUpdated(itemsVO.getReceiverCreditQty());
				balancesVO.setNetAmount(itemsVO.getReceiverCreditQty() * Long.parseLong(PretupsBL.getDisplayAmount(itemsVO.getUnitValue())));
			} else {
				balancesVO.setBalance(balancesVO.getPreviousBalance() + itemsVO.getRequiredQuantity());
			}
			balancesVO
			.setOtherInfo("COMMISSION PROFILE ID=" + channelTransferVO.getCommProfileSetId() + ", PROFILE VERSION=" + channelTransferVO.getCommProfileVersion() + ", SECOND USER ID=" + channelTransferVO
					.getFromUserID());
			// Added to log user msisdn on 13/02/2008
			balancesVO.setUserMSISDN(channelTransferVO.getToUserCode());
			BalanceLogger.log(balancesVO);

			// Balance logger is added for commision
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && "T".equalsIgnoreCase(channelTransferVO.getTransferSubType()) && !("S".equals(channelTransferVO.getTransactionMode())) && !channelTransferVO.getSosFlag()) {
				prepareNetworkTxnCommisionBalanceLogger(channelTransferVO);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}

	}

	/**
	 * Method validateUserProductsFormatForSMS()
	 * To get the product from the message Array and validate them whether
	 * product code and
	 * product quantity is valid or not.
	 * 
	 * @param prodArray
	 * @param requestVO
	 *            RequestVO
	 * @return String[]
	 * @throws BTSLBaseException
	 */
	public static String[] validateUserProductsFormatForSMS(String[] prodArray, RequestVO requestVO) throws BTSLBaseException {
		final String methodName = "validateUserProductsFormatForSMS";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with prodArray = " + prodArray);
		}

		String productArray[] = null;

		/**
		 * [keyword] [usercode] [qty] [productcode] [qty] [productcode]
		 * [password]
		 * at the index of 2 the product details starts
		 * 
		 * different combiniation of the message format
		 * [keyword] [usercode] [qty] [password]
		 * 
		 * [keyword] [usercode] [qty] [productcode] [password]
		 * 
		 * [keyword] [usercode] [qty] [productcode] [qty] [productcode]
		 * [password]
		 * 
		 * If user sends only quantity the piclked the default product from the
		 * system preferences.
		 * In this case the array length is 4.
		 * 
		 * if array length is not 4 then it means user also sends product code
		 * in his message
		 * he can also sends request for multiple products. In this case
		 * validate user sends
		 * products quantity products code with each other.
		 * 
		 */
		final int arrLength = prodArray.length;
		boolean defaultProduct = false;
		boolean validateProduct = false;
		String defaultProductValue = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT);
		/*
		 * Message parsing is changed by ankit zindal on date 1/08/06
		 * This will check the following cases
		 * If user PIN is required then message can be
		 * [keyword][usercode][qty][pin]
		 * [keyword][usercode][qty][productcode][pin]
		 * Here min length can be 4. In case of lenth 4 default product will be
		 * checked otherwise qty and product pair will be chekced
		 * 
		 * If user PIN is not required then message can be
		 * [keyword][usercode][qty]
		 * [keyword][usercode][qty][productcode]
		 * Here min length can be 3. In case of lenth 3 default product will be
		 * checked otherwise qty and product pair will be chekced
		 * In this case if PIN is send then it will be invalid message format
		 */
		
		//changes made for PIN required 'N' for new UI
		if ((((ChannelUserVO) requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) || (((ChannelUserVO) requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.NO) ) {
			/*
			 * if length is 4 then no product code is comming with message so
			 * use the default product for the txn
			 * else check that user is entered the product code or not (checking
			 * it by neglecting the 3 paramenters as
			 * [keyword] [usercode][password])
			 * else message is not in the proper format
			 */
			if (arrLength < 4) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { requestVO
					.getActualMessageFormat() }, null);
			}

			if (arrLength == 4) {
				defaultProduct = true;
			} else if ((arrLength - 3) % 2 == 0) {
				validateProduct = true;
			} else {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { requestVO
					.getActualMessageFormat() }, null);
			}
		} else {
			/*
			 * if length is 3 then no product code is comming with message so
			 * use the default product for the txn
			 * else check that user is entered the product code or not (checking
			 * it by neglecting the 3 paramenters as
			 * [keyword] [usercode][password])
			 * else message is not in the proper format
			 * [keyword] [usercode] [qty]
			 * 
			 * [keyword] [usercode] [qty] [productcode]
			 * 
			 * [keyword] [usercode] [qty] [productcode] [qty] [productcode]
			 * If PIN is send then invalid message format will be treated
			 */
			if (arrLength < 3) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { requestVO
					.getActualMessageFormat() }, null);
			}

			if (arrLength == 3) {
				defaultProduct = true;
			} else if ((arrLength - 2) % 2 == 0) {
				validateProduct = true;
			} else {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { requestVO
					.getActualMessageFormat() }, null);
			}
		}

		/*
		 * check that the qty for the default product is numeric or not if no
		 * then give error else set the default
		 * product in the array for txn
		 */
		if (defaultProduct) {
			if (!BTSLUtil.isDecimalValue(prodArray[2])) {
				final String[] args = { prodArray[2] };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_DEFAULT_PRODUCT_QUANTITY, args);
			}
			if (Double.parseDouble(prodArray[2]) <= 0) {
				final String[] args = { prodArray[2] };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_LESS_DEFAULT_PRODUCT_QUANTITY, args);
			}
			productArray = new String[2];
			productArray[0] = prodArray[2];
			productArray[1] = defaultProductValue;
		}
		/*
		 * if user entered the produt code then check that the qty for the
		 * product, is numeric or not
		 * if no then give error else check that the product code of the
		 * product, is numeric or not
		 * if no then give error else set the qty and product code in the array
		 * for txn
		 */
		else if (validateProduct) {
			//changes made for PIN required 'N' for new UI
			int size = 0;
			if ((((ChannelUserVO) requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) || (((ChannelUserVO) requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.NO)) {
				productArray = new String[(arrLength - 3)];
				size = arrLength - 1;
			} else {
				productArray = new String[(arrLength - 2)];
				size = arrLength;
			}

			int j = 0;
			for (int i = 2; i < size; i += 2, j += 2) {
				if (!BTSLUtil.isDecimalValue(prodArray[i])) {
					final String[] args = { prodArray[i + 1] };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_QUANTITY, args);
				}
				if (Double.parseDouble(prodArray[i]) <= 0) {
					final String[] args = { prodArray[i + 1] };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_LESS_DEFAULT_PRODUCT_QUANTITY, args);
				}
				if (!BTSLUtil.isNumeric(prodArray[i + 1])) {
					final String[] args = { prodArray[i + 1] };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_CODE_FORMAT, args);
				}
				productArray[j] = prodArray[i];
				productArray[j + 1] = prodArray[i + 1];
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited with productArray.length = " + productArray.length);
		}

		return productArray;
	}

	/**
	 * Method loadC2CXfrProductsWithXfrRule()
	 * This method find the filtered list of the products in the C2C transfer by
	 * the following checks (mainly commission profile)
	 * 1. find the products mapped with network.
	 * 2. get the product having active network product mapping.(filter product
	 * list)
	 * 3. load user balance list.
	 * 4. Now filter list with having the balance >0.
	 * 5. check the latest commission profile assocated with the user.
	 * 6. load the list of the products associated with the commission profile.
	 * 7. load the list of the products associated with the transfer rule.
	 * 8. get the filtered product list from all the above cases.
	 * 
	 * @param con
	 * @param userID
	 * @param networkCode
	 * @param p_commProfileSetId
	 * @param currentDate
	 * @param transferRuleID
	 * @param p_forwardPath
	 * @param isFromWeb
	 * @param userNameCode
	 *            it can be userName in case of web and in case of SMS it will
	 *            be User Code
	 * @param locale
	 *            Locale
	 * @param p_productType
	 *            TODO
	 * @param p_txnType TODO
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadC2CXfrProductsWithXfrRule(Connection con, String userID, String networkCode, String p_commProfileSetId, Date currentDate, String transferRuleID, String p_forwardPath, boolean isFromWeb, String userNameCode, Locale locale, String p_productType, String p_txnType) throws BTSLBaseException {
		final String methodName = "loadC2CXfrProductsWithXfrRule";

        StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered UserID : ");
			loggerValue.append(userID);
			loggerValue.append(", NetworkCode : ");
			loggerValue.append(networkCode);
			loggerValue.append(", CommissionProfileSetID : ");
			loggerValue.append(p_commProfileSetId);
			loggerValue.append(", CurrentDate : " );
			loggerValue.append(currentDate);
			loggerValue.append(", transferRuleID : ");
			loggerValue.append(transferRuleID);
			loggerValue.append(", isFromWeb : ");
			loggerValue.append(isFromWeb);
			loggerValue.append(", p_userIDCODE : ");
			loggerValue.append(userNameCode);
			loggerValue.append(", locale = " );
			loggerValue.append(locale);
			loggerValue.append(", p_productType = " );
			loggerValue.append(p_productType);
			log.debug(methodName,loggerValue );
		}
		final UserProductWalletMappingCache userProductWalletMappingCache = null;
		final ArrayList productList = new ArrayList();
		final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
		final String args[] = { userNameCode };
		final ArrayList prodList = networkProductDAO.loadProductListForXfr(con, p_productType, networkCode);
		/*
		 * 1. check whether product exist or not of the input productType
		 */
		if (prodList.isEmpty()) {
			if (isFromWeb) {
				if (!BTSLUtil.isNullString(p_productType)) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.producttype", 0, new String[] { p_productType }, p_forwardPath);
				}
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.product", p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_EXIST);
		}

		/*
		 * 2.
		 * checking that the status of network product mapping is active and
		 * also construct the new arrayList of
		 * channelTransferItemsVOs containg required list.
		 */
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		int i, j, m, n;
		int prodListSizes=prodList.size();
		for (i = 0, j = prodListSizes; i < j; i++) {
			networkProductVO = (NetworkProductVO) prodList.get(i);
			if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
				channelTransferItemsVO = new ChannelTransferItemsVO();
				channelTransferItemsVO.setProductType(networkProductVO.getProductType());
				channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
				channelTransferItemsVO.setProductName(networkProductVO.getProductName());
				channelTransferItemsVO.setShortName(networkProductVO.getShortName());
				channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
				channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
				channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
				channelTransferItemsVO.setStatus(networkProductVO.getStatus());
				channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
				channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
				channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
				productList.add(channelTransferItemsVO);
			}
		}
		if (productList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.networkproductmapping", p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOTMAPPED_NETWORK);
		}

		/*
		 * 3. load the product's BALANCE
		 */
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final ArrayList userBalancesList = channelUserDAO.loadUserBalances(con, networkCode, networkCode, userID);
		if (userBalancesList == null || userBalancesList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.c2c.noproductassigned", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE, args);
		}
		/*
		 * 4. find the products having the balance >0 and set the balance to the
		 * product list
		 */

		boolean validProductFound = false;
		boolean productFound = false;
		final ArrayList errorList = new ArrayList();
		KeyArgumentVO keyArgumentVO = null;
		UserBalancesVO balancesVO = null;
		String errArgs[] = null;
		long currentBalance = 0;
		long previousBalance = 0;
		final long balance = 0;
		String previousProductType = null;
		String currentProductType = null;
		boolean userProductMultipleWallet =  ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET))).booleanValue();
		if (userProductMultipleWallet) {
			int userBalancesListsizes=userBalancesList.size();
			for (i = 0; i < userBalancesListsizes; i++) {
				balancesVO = (UserBalancesVO) userBalancesList.get(i);
				currentProductType = balancesVO.getProductCode();
				currentBalance = balancesVO.getBalance();
				if (currentProductType.equals(previousProductType)) {
					currentBalance = currentBalance + previousBalance;
					balancesVO.setBalance(currentBalance);
					userBalancesList.remove(i - 1);
					i--;
					userBalancesListsizes--;
				}
				previousBalance = currentBalance;
				previousProductType = currentProductType;
			}
		}

		for (i = 0, j = userBalancesList.size(); i < j; i++) {
			balancesVO = (UserBalancesVO) userBalancesList.get(i);
			int productsListSize=productList.size();
			for (m = 0, n = productsListSize; m < n; m++) {

				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);

				if (channelTransferItemsVO.getProductCode().equals(balancesVO.getProductCode())) {
					productFound = true;
					if (balancesVO.getBalance() <= 0) {
						userBalancesList.remove(i);
						i--;
						j--;
						// add "product balance <=0" message in the list
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY);
						/*
						 * if(isFromWeb)
						 * errArgs = new
						 * String[]{balancesVO.getProductShortName()};
						 * else
						 */
						errArgs = new String[] { balancesVO.getProductShortName() };
						keyArgumentVO.setArguments(errArgs);
						errorList.add(keyArgumentVO);

						continue;
					} else {
						validProductFound = true;
						channelTransferItemsVO.setBalance(balancesVO.getBalance());
						break;
					}
				}
			}

		}

		if (!productFound) {
			// add "product is suspended" message in the list
			keyArgumentVO = new KeyArgumentVO();
			keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_PRODUCT_SUSPENDED_SUBKEY);
			/*
			 * if(isFromWeb)
			 * errArgs = new String[]{channelTransferItemsVO.getProductCode()};
			 * else
			 */
			errArgs = new String[] { channelTransferItemsVO.getProductCode() };
			keyArgumentVO.setArguments(errArgs);
			errorList.add(keyArgumentVO);

		}
		if (!validProductFound) {
			final String[] array = { userNameCode, BTSLUtil.getMessage(locale, errorList) };
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.nobalance", 0, array, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE_IN_COMMPROFILE_NTWMAPPING, array);

		}
		// reomve the products form the list having no balance
		for (m = 0, n = productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
			if (channelTransferItemsVO.getBalance() <= 0) {
				productList.remove(m);
				m--;
				n--;
			}
		}

		// if no product have the balance >0 show the error message
		/*
		 * if(productList.isEmpty())
		 * {
		 * if(isFromWeb)
		 * throw new
		 * BTSLBaseException(ChannelTransferBL.class,"loadC2CXfrProductList"
		 * ,"message.transferc2c.nodata.nobalance",0,args,p_forwardPath);
		 * throw new
		 * BTSLBaseException(ChannelTransferBL.class,"loadC2CXfrProductsWithXfrRule"
		 * ,PretupsErrorCodesI.
		 * ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE_IN_COMMPROFILE_NTWMAPPING
		 * ,args);
		 * }
		 */

		/*
		 * 5. load the latest version of the commission profile set id
		 */
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		String latestCommProfileVersion = null;
		try {
			final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, p_commProfileSetId, currentDate);
			latestCommProfileVersion = commissionProfileSetVO.getCommProfileVersion();
		} catch (BTSLBaseException bex) {
			if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
				if (isFromWeb) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.commprofilever", 0, args, p_forwardPath);
				}
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED, args);
			}
			log.error("loadO2CXfrProductList", "BTSLBaseException " + bex.getMessage());
			throw bex;
		}

		// if there is no commission profile version exist upto the current date
		// show the error message.
		if (BTSLUtil.isNullString(latestCommProfileVersion)) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.commprofilever", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED, args);
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		String type = (TRANSACTION_TYPE_ALWD)?p_txnType:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(con, p_commProfileSetId, latestCommProfileVersion, type, paymentMode);

		// if list is empty send the error message
		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.commprofileproduct", 0,
						new String[] { userNameCode, latestCommProfileVersion }, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_WITH_COMM_PROFILE, args);
		}

		// filterize the product list with the products of the commission
		// profile products
		CommissionProfileProductsVO commissionProfileProductsVO = null;
		int commissionProfileProductListSizes=commissionProfileProductList.size();
		for (i = 0, j = commissionProfileProductListSizes; i < j; i++) {
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileDetailID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}
			}
		}
		for (m = 0, n = productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
			if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
				productList.remove(m);
				m--;
				n--;
			}
		}
		// if list size is zero than send the error message.
		if (productList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.networkcommprofileproduct", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_SAME_PRODUCT_IN_COMMPROFILE_NTWMAPPING, args);
		}

		/*
		 * 7. load the product list associated with the transfer rule.
		 */
		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
		final ArrayList prodWithXfrRuleList = channelTransferRuleDAO.loadProductVOList(con, transferRuleID);
		if (prodWithXfrRuleList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.noproductassigned.transferrule", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTDEFINE, args);
		}
		/*
		 * 8. filter the list with the products associated with the transfer
		 * rule. (final filteration)
		 */
		ListValueVO listValueVO = null;
		final ArrayList c2cXfrProductList = new ArrayList();
		for (i = 0, j = prodWithXfrRuleList.size(); i < j; i++) {
			listValueVO = (ListValueVO) prodWithXfrRuleList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
					c2cXfrProductList.add(channelTransferItemsVO);
				}
			}
		}
		// if list is of size =0. show error message.
		if (c2cXfrProductList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.transferrule", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH, args);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with c2cXfrProductList.size() = ");
			loggerValue.append(c2cXfrProductList.size());
			loggerValue.append(c2cXfrProductList);
			log.debug(methodName,  loggerValue);
		}
		return c2cXfrProductList;
	}

	/**
	 * Method loadO2CXfrProductList.
	 * This method is to load the product list for the O2C transfer and checks
	 * for various condition and send the message
	 * This method find the filtered list of the products in the O2C transfer by
	 * the following checks (mainly commission profile)
	 * 1. find the products mapped with network.
	 * 2. get the product having active network product mapping.(filter product
	 * list)
	 * 3. check that this product ever had stock or not.
	 * 4. check the latest commission profile assocated with the user.
	 * 5. load the list of the products assocated with the commission profile.
	 * 6. get the filtered produt list from all the above cases.
	 * 
	 * @param con
	 *            Connection
	 * @param p_productType
	 *            String
	 * @param networkCode
	 *            String
	 * @param p_commProfileSetId
	 *            String
	 * @param currentDate
	 *            Date
	 * @param p_forwardPath
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 * @throws ParseException
	 */
	public static ArrayList loadO2CXfrProductList(Connection con, String p_productType, String networkCode, String p_commProfileSetId, Date currentDate, String p_forwardPath) throws BTSLBaseException {
		final String methodName = "loadO2CXfrProductList";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered   p_productType : ");
			loggerValue.append(p_productType);
			loggerValue.append(", NetworkCode : ");
			loggerValue.append(networkCode);
			loggerValue.append(", CommissionProfileSetID : ");
			loggerValue.append(p_commProfileSetId);
			loggerValue.append(", CurrentDate : " );
			loggerValue.append(currentDate);
			loggerValue.append(", p_forwardPath = ");
			loggerValue.append(p_forwardPath);
			log.debug(methodName,loggerValue );
		}
		final ArrayList productList = new ArrayList();

		final NetworkProductDAO networkProductDAO = new NetworkProductDAO();

		// load the product list mapped with the network.
		final ArrayList prodList = networkProductDAO.loadProductListForXfr(con, p_productType, networkCode);

		// check whether product exist or not of the input productType
		if (prodList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.producttype", 0, new String[] { p_productType }, p_forwardPath);
		}

		// checking that the status of network product mapping is active and
		// also construct the new arrayList of
		// channelTransferItemsVOs containg required list.
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		int i, j, m, n;
		for (i = 0, j = prodList.size(); i < j; i++) {
			networkProductVO = (NetworkProductVO) prodList.get(i);
			if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
				channelTransferItemsVO = new ChannelTransferItemsVO();
				channelTransferItemsVO.setProductType(networkProductVO.getProductType());
				channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
				channelTransferItemsVO.setProductName(networkProductVO.getProductName());
				channelTransferItemsVO.setShortName(networkProductVO.getShortName());
				channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
				channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
				channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
				channelTransferItemsVO.setStatus(networkProductVO.getStatus());
				channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
				channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
				channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
				productList.add(channelTransferItemsVO);
			}
		}
		if (productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.networkproductmapping", 0, new String[] { p_productType }, p_forwardPath);
		}


		// load the latest version of the commission profile set id
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		String latestCommProfileVersion = null;
		try {
			final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, p_commProfileSetId, currentDate);
			latestCommProfileVersion = commissionProfileSetVO.getCommProfileVersion();
		} catch (BTSLBaseException bex) {
			if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofilever", p_forwardPath);
			}
			loggerValue.setLength(0);
			loggerValue.append("BTSLBaseException ");
			loggerValue.append(bex.getMessage());
			log.error(methodName, loggerValue );
			throw bex;
		}

		// if there is no commission profile version exist upto the current date
		// show the error message.
		if (BTSLUtil.isNullString(latestCommProfileVersion)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofilever", p_forwardPath);
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		String type = (TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
		final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(con, p_commProfileSetId, latestCommProfileVersion, type);

		// if list is empty send the error message
		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofileproduct", 0, new String[] { latestCommProfileVersion },
					p_forwardPath);
		}

		// filterize the product list with the products of the commission
		// profile products
		CommissionProfileProductsVO commissionProfileProductsVO = null;
		for (i = 0, j = commissionProfileProductList.size(); i < j; i++) {
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileDetailID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}
			}
		}
		for (m = 0, n = productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
			if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
				productList.remove(m);
				m--;
				n--;
			}
		}

		if (productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.networkcommprofileproduct", p_forwardPath);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with productList.size() = ");
			loggerValue.append(productList.size());
			log.debug(methodName,  loggerValue );
		}
		return productList;
	}

	/**
	 * Method validateReqstProdsWithDefinedProdsForXFR()
	 * This method validate the inputed product list with the associated product
	 * with the user for the transfer.
	 * 
	 * @param con
	 * @param senderVO
	 * @param productArr
	 * @param curDate
	 * @param locale
	 * @param p_commProfileID
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList validateReqstProdsWithDefinedProdsForXFR(Connection con, ChannelUserVO senderVO, String[] productArr, Date curDate, Locale locale, String p_commProfileID) throws BTSLBaseException {
		final String methodName = "validateReqstProdsWithDefinedProdsForXFR";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered productArr Length = ");
		//	loggerValue.append(productArr.length);
			loggerValue.append(", SenderVO ");
			loggerValue.append(senderVO);
			loggerValue.append(", curDate : ");
			loggerValue.append(curDate);
			loggerValue.append(", p_commProfileID = ");
			loggerValue.append(p_commProfileID);
			log.debug(methodName,loggerValue);
		}

		final ArrayList tempProductList = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, senderVO.getUserID(), senderVO.getNetworkID(), p_commProfileID,
				curDate, senderVO.getTransferRuleID(), null, false, senderVO.getUserCode(), locale, null, PretupsI.TRANSFER_TYPE_C2C);
		ChannelTransferItemsVO channelTransferItemsVO = null;
		final ArrayList notMatchedProdList = new ArrayList();
		final ArrayList minLessProdList = new ArrayList();
		final ArrayList maxMoreProdList = new ArrayList();
		final ArrayList balanceList = new ArrayList();
		final ArrayList multipleOfList = new ArrayList();
		final ArrayList productList = new ArrayList();

		boolean exist = false;
		KeyArgumentVO keyArgumentVO = null;
		int prodCode = 0;
		int m, n;
		for (int i = 0, j = tempProductList.size(); i < j; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) tempProductList.get(i);
			/*
			 * To check whether product selected by user exists in his
			 * productlist or not.
			 */
			for (m = 0, n = productArr.length; m < n; m += 2) {
				prodCode = Integer.parseInt(productArr[m + 1]);
				if (channelTransferItemsVO.getProductShortCode() == prodCode) {
					if (channelTransferItemsVO.getMinTransferValue() > (PretupsBL.getSystemAmount(productArr[m]))) {
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MIN_TRANSFER_SUBKEY);
						final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getMinTransferValue()) };
						keyArgumentVO.setArguments(args);
						minLessProdList.add(keyArgumentVO);
					} else if (channelTransferItemsVO.getMaxTransferValue() < (PretupsBL.getSystemAmount(productArr[m]))) {
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_TRANSFER_SUBKEY);
						final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getMaxTransferValue()) };
						keyArgumentVO.setArguments(args);
						maxMoreProdList.add(keyArgumentVO);
					} else if ((PretupsBL.getSystemAmount(productArr[m]) % channelTransferItemsVO.getTransferMultipleOf()) != 0) {
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MULTIPLE_OF_SUBKEY);
						final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getTransferMultipleOf()) };
						keyArgumentVO.setArguments(args);
						multipleOfList.add(keyArgumentVO);
					} else if (channelTransferItemsVO.getBalance() < (PretupsBL.getSystemAmount(productArr[m]))) {// balanceList
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE_SUBKEY);
						final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getBalance()) };
						keyArgumentVO.setArguments(args);
						balanceList.add(keyArgumentVO);
					}
					exist = true;
					channelTransferItemsVO.setRequestedQuantity(productArr[m]);
					channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(productArr[m]));
					channelTransferItemsVO.setPayableAmount(Double.valueOf((channelTransferItemsVO.getUnitValue() * Double.parseDouble(productArr[m]))).longValue());
					channelTransferItemsVO.setNetPayableAmount(Double.valueOf((channelTransferItemsVO.getUnitValue() * Double.parseDouble(productArr[m]))).longValue());
					productList.add(channelTransferItemsVO);
					break;
				}
			}
			if (!exist) {
				notMatchedProdList.add(String.valueOf(prodCode));
			}
		}
		if (notMatchedProdList.size() == tempProductList.size()) {
			final String prodArr[] = new String[notMatchedProdList.size()];
			notMatchedProdList.toArray(prodArr);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NOT_ALLOWED, prodArr);
		} else if (minLessProdList != null && !minLessProdList.isEmpty()) {
			final String[] array = { BTSLUtil.getMessage(locale, minLessProdList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MIN_TRANSFER, array);
		} else if (maxMoreProdList != null && !maxMoreProdList.isEmpty()) {
			final String[] array = { BTSLUtil.getMessage(locale, maxMoreProdList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_TRANSFER, array);
		} else if (multipleOfList != null && !multipleOfList.isEmpty()) {
			final String[] array = { BTSLUtil.getMessage(locale, multipleOfList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MULTIPLE_OF, array);
		} else if (balanceList != null && !balanceList.isEmpty()) {
			final String[] array = { BTSLUtil.getMessage(locale, balanceList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE1, array);
		}

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with productList.size() = ");
			loggerValue.append(productList.size());
			log.debug(methodName,  loggerValue);
		}
		return productList;
	}

	/**
	 * Method validReqstProdsWithDfndProdsForWdAndRet()
	 * This method validate the inputed product list with the associated product
	 * with the user for the Return/Withdraw
	 * 
	 * @param con
	 * @param senderVO
	 * @param productArr
	 * @param curDate
	 * @param locale
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList validReqstProdsWithDfndProdsForWdAndRet(Connection con, ChannelUserVO senderVO, String[] productArr, Date curDate, Locale locale) throws BTSLBaseException {
		final String methodName = "validReqstProdsWithDfndProdsForWdAndRet";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered productArr Length = ");
			loggerValue.append(productArr.length);
			loggerValue.append(", SenderVO = ");
			loggerValue.append(senderVO);
			loggerValue.append(", curDate : ");
			loggerValue.append(curDate);
			log.debug(methodName,  loggerValue );
		}
		final ArrayList tempProductList = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, senderVO.getUserID(), senderVO.getNetworkID(), senderVO
				.getCommissionProfileSetID(), curDate, senderVO.getTransferRuleID(), null, false, senderVO.getMsisdn(), locale, null, PretupsI.TRANSFER_TYPE_C2C);
		ChannelTransferItemsVO channelTransferItemsVO = null;
		final ArrayList notMatchedProdList = new ArrayList();
		final ArrayList balanceList = new ArrayList();
		final ArrayList productList = new ArrayList();
		boolean exist = false;
		KeyArgumentVO keyArgumentVO = null;
		int prodCode = 0;
		for (int i = 0; i < tempProductList.size(); i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) tempProductList.get(i);
			/*
			 * To check whether product selected by user exists in his
			 * productlist or not.
			 */
			for (int m = 0; m < productArr.length; m += 2) {
				prodCode = Integer.parseInt(productArr[m + 1]);
				if (channelTransferItemsVO.getProductShortCode() == prodCode) {
					if (channelTransferItemsVO.getBalance() < (PretupsBL.getSystemAmount(productArr[m]))) {
						// balanceList
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE_SUBKEY);
						final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getBalance()) };
						keyArgumentVO.setArguments(args);
						balanceList.add(keyArgumentVO);
					}
					exist = true;
					channelTransferItemsVO.setRequestedQuantity(productArr[m]);
					channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(productArr[m]));
					channelTransferItemsVO.setPayableAmount(Double.valueOf((channelTransferItemsVO.getUnitValue() * Double.parseDouble(productArr[m]))).longValue());
					productList.add(channelTransferItemsVO);
					break;
				}
			}
			if (!exist) {
				// notMatchedProdList.add(channelTransferItemsVO.getProductShortCode()+"");
				notMatchedProdList.add(String.valueOf(prodCode));
			}
		}
		if (notMatchedProdList.size() == tempProductList.size()) {
			final String prodArr[] = new String[notMatchedProdList.size()];
			notMatchedProdList.toArray(prodArr);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NOT_ALLOWED, prodArr);
		} else if (balanceList != null && !balanceList.isEmpty()) {
			final String[] array = { BTSLUtil.getMessage(locale, balanceList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE1, array);
		}

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with productList.size() = ");
			loggerValue.append(productList.size());
			log.debug(methodName,  loggerValue);
		}
		return productList;
	}

	/**
	 * Method validateSenderAndReceiverWithXfrRule()
	 * This method is used to validate the sender and receiver in the C2C
	 * transaction(T,R,W) mainly in the case of
	 * when transaction is going on by the userCode (i.e. user's mobile
	 * number.).
	 * 
	 * @param con
	 * @param senderVO
	 * @param p_receiverVO
	 * @param p_isUserCode
	 * @param p_forwardPath
	 * @param p_isFromWeb
	 * @param p_txnSubType
	 *            String
	 * @return boolean
	 * @throws BTSLBaseException
	 *             boolean
	 */
	public static boolean validateSenderAndReceiverWithXfrRule(Connection con, ChannelUserVO senderVO, ChannelUserVO p_receiverVO, boolean p_isUserCode, String p_forwardPath, boolean p_isFromWeb, String p_txnSubType) throws BTSLBaseException {
		final String methodName = "validateSenderAndReceiverWithXfrRule";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" Entered senderVO = ");
			loggerValue.append(senderVO);
			loggerValue.append(", p_receiverVO = ");
			loggerValue.append(p_receiverVO);
			loggerValue.append(", IsUserCode : ");
			loggerValue.append(p_isUserCode);
			loggerValue.append(", forwardPath : " );
			loggerValue.append(p_forwardPath);
			loggerValue.append(", p_isFromWeb : ");
			loggerValue.append(p_isFromWeb);
			loggerValue.append(", p_txnSubType = ");
			loggerValue.append(p_txnSubType);
			log.debug(methodName,loggerValue );
		}

		if (senderVO.getUserID().equals(p_receiverVO.getUserID())) {
			if (!p_isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_SAME_USER);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "channeltransfer.sameuser.error.msg", p_forwardPath);
		}

		if (!PretupsI.YES.equals(senderVO.getCommissionProfileStatus())) {
			String arugment = senderVO.getUserName();
			if (p_isUserCode) {
				arugment = senderVO.getUserCode();
			}

			final String args[] = { arugment, senderVO.getCommissionProfileSuspendMsg() };
			if (!p_isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_SUSPENDED, args);
			}

			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "commissionprofile.notactive.msg", 0, args, p_forwardPath);
		} else if (!PretupsI.YES.equals(senderVO.getTransferProfileStatus())) {
			String arugment = senderVO.getUserName();
			if (p_isUserCode) {
				arugment = senderVO.getUserCode();
			}
			final String args[] = { arugment };
			if (!p_isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED, args);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "transferprofile.notactive.msg", 0, args, p_forwardPath);
		}

		// this can be arsies in the case when user from diffrent network or his
		// commission profile is suspended or not exists
		if (!PretupsI.YES.equals(p_receiverVO.getCommissionProfileStatus())) {
			String arugment = p_receiverVO.getUserName();
			if (p_isUserCode) {
				arugment = p_receiverVO.getUserCode();
			}

			final String args[] = { arugment, p_receiverVO.getCommissionProfileSuspendMsg() };
			if (!p_isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_SUSPENDED, args);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "commissionprofile.notactive.msg", 0, args, p_forwardPath);
		} else if (!PretupsI.YES.equals(p_receiverVO.getTransferProfileStatus())) {
			String arugment = p_receiverVO.getUserName();
			if (p_isUserCode) {
				arugment = p_receiverVO.getUserCode();
			}
			final String args[] = { arugment };
			if (!p_isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED, args);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "transferprofile.notactive.msg", 0, args, p_forwardPath);
		}
		String toCategory = p_receiverVO.getCategoryCode();
		String fromCategory = senderVO.getCategoryCode();
		String domainCode = senderVO.getDomainID();
		if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
			toCategory = senderVO.getCategoryCode();
			fromCategory = p_receiverVO.getCategoryCode();
			domainCode = p_receiverVO.getDomainID();
		}
		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, senderVO.getNetworkID(), domainCode, fromCategory, toCategory,
				PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);

		if (BTSLUtil.isNullObject(channelTransferRuleVO)) {
			if (!p_isFromWeb) {
				final String args[] = { senderVO.getCategoryVO().getCategoryName(), p_receiverVO.getCategoryVO().getCategoryName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE, args);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.channeltransfer.transferrulenotexist", p_forwardPath);
		} else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnSubType) && PretupsI.NO.equals(channelTransferRuleVO.getDirectTransferAllowed()) && PretupsI.NO
				.equals(channelTransferRuleVO.getTransferChnlBypassAllowed())) {
			if (!p_isFromWeb) {
				final String args[] = { p_receiverVO.getCategoryVO().getCategoryName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED, args);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.channeltransfer.transferrulenotdefine", p_forwardPath);
		} else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType) && PretupsI.NO.equals(channelTransferRuleVO.getWithdrawAllowed()) && PretupsI.NO
				.equals(channelTransferRuleVO.getWithdrawChnlBypassAllowed())) {
			if (!p_isFromWeb) {
				final String args[] = { senderVO.getCategoryVO().getCategoryName(), p_receiverVO.getCategoryVO().getCategoryName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_WITHDRAW_NOT_ALLOWED, args);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.channeltransfer.withdrawnotallowed.msg", p_forwardPath);
		} else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnSubType) && PretupsI.NO.equals(channelTransferRuleVO.getReturnAllowed()) && PretupsI.NO
				.equals(channelTransferRuleVO.getReturnChnlBypassAllowed())) {
			if (!p_isFromWeb) {
				final String args[] = { senderVO.getCategoryVO().getCategoryName(), p_receiverVO.getCategoryVO().getCategoryName() };
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED, args);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.channeltransfer.returnnotallowed.msg", p_forwardPath);
		}
		// following code is added as if we are supporting the concept of
		// "C2C RETURN TO PARENT ONLY"
		// then check if in the preferences it is defined as true and
		// controlling level is PARENT and
		// uncontroll level is NA or PARENT then if sender's parent ID is not
		// equal to the receiver's
		// userID then give the error message as return not allowed.
		else {
			boolean c2cRetailerParentOnly = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_RET_PARENT_ONLY);
			if (c2cRetailerParentOnly && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnSubType)) {
				if (PretupsI.YES.equals(channelTransferRuleVO.getReturnAllowed()) && PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT
						.equals(channelTransferRuleVO.getCntrlReturnLevel()) && (PretupsI.NOT_APPLICABLE.equals(channelTransferRuleVO.getUncntrlReturnLevel()) || PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT
								.equals(channelTransferRuleVO.getUncntrlReturnLevel()))) {
					if (!senderVO.getParentID().equals(p_receiverVO.getUserID())) {
						if (!p_isFromWeb) {
							final String args[] = { p_receiverVO.getCategoryVO().getCategoryName(), senderVO.getCategoryVO().getCategoryName() };
							throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED, args);
						}
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.channeltransfer.returnnotallowed.msg", p_forwardPath);
					}
				}
			}
		}
		// ends here
		senderVO.setTransferRuleID(channelTransferRuleVO.getTransferRuleID());
		p_receiverVO.setTransferRuleID(channelTransferRuleVO.getTransferRuleID());

		// to set the transfer category in the channelTransfer Table
		senderVO.setTransferCategory(channelTransferRuleVO.getTransferType());
		p_receiverVO.setTransferCategory(channelTransferRuleVO.getTransferType());

		// call the method form the channelUserBL to validate the user
		// information

		final boolean isOutsideHierarchy = ChannelUserBL.validateUserForXfr(con, p_txnSubType, channelTransferRuleVO, senderVO, p_receiverVO, p_isUserCode, p_forwardPath,
				p_isFromWeb);
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited isOutsideHireacrchy : ");
			loggerValue.append(isOutsideHierarchy);
			log.debug(methodName,  loggerValue);
		}
		return isOutsideHierarchy;
	}

	/**
	 * Prepare the SMS message which we have to send the receiver user as SMS
	 * 
	 * @param con
	 *            Connection
	 * @param channelTransferVO
	 * @param p_txnSubKey
	 *            String
	 * @param p_balSubKey
	 *            String
	 * @return Object[] at the index 0 txnSMSList and at index 1 balSMSList.
	 * @throws BTSLBaseException
	 */
	public static Object[] prepareSMSMessageListForReceiver(Connection con, ChannelTransferVO channelTransferVO, String p_txnSubKey, String p_balSubKey) throws BTSLBaseException {
		final String methodName = "prepareSMSMessageListForReceiver";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered channelTransferVO =  : ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", p_txnSubKey = ");
			loggerValue.append(p_txnSubKey);
			loggerValue.append(", p_balSubKey = ");
			loggerValue.append(p_balSubKey);
		
			log.debug(methodName,  loggerValue);
		}
		// This code is commented off because there is no need to hit the
		// database for receiver's balance
		/*
		 * ArrayList userBalanceList=null;
		 * if("W".equalsIgnoreCase(channelTransferVO.getTransferSubType()) &&
		 * "WEB".equalsIgnoreCase(channelTransferVO.getRequestGatewayType()))
		 * userBalanceList=(new
		 * UserBalancesDAO()).loadUserBalanceList(con,channelTransferVO
		 * .getFromUserID
		 * (),channelTransferVO.getNetworkCode(),channelTransferVO
		 * .getNetworkCodeFor());
		 * else
		 * userBalanceList=(new
		 * UserBalancesDAO()).loadUserBalanceList(con,channelTransferVO
		 * .getToUserID
		 * (),channelTransferVO.getNetworkCode(),channelTransferVO
		 * .getNetworkCodeFor());
		 */
		final ArrayList txnSmsMessageList = new ArrayList();
		final ArrayList balSmsMessageList = new ArrayList();
		KeyArgumentVO keyArgumentVO = null;
		String argsArr[] = null;
		final ArrayList productList = channelTransferVO.getChannelTransferitemsVOList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		String currentBalance = null;
		for (int i = 0, k = productList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
			keyArgumentVO = new KeyArgumentVO();
			argsArr = new String[2];
			argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity());
			argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
			keyArgumentVO.setKey(p_txnSubKey);
			keyArgumentVO.setArguments(argsArr);
			txnSmsMessageList.add(keyArgumentVO);

			// This code is added to retrieve the receiver balance without
			// hitting the database.
			argsArr = new String[2];
			final long previousBalance = channelTransferItemsVO.getPreviousBalance();
			long transferedQuantity = 0;
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && "T".equalsIgnoreCase(channelTransferVO.getTransferSubType())) {
				transferedQuantity = channelTransferItemsVO.getReceiverCreditQty();
			} else {
				transferedQuantity = channelTransferItemsVO.getApprovedQuantity();
			}
			if (("W".equalsIgnoreCase(channelTransferVO.getTransferSubType()) || "R".equalsIgnoreCase(channelTransferVO.getTransferSubType())) && ("WEB".equalsIgnoreCase(channelTransferVO.getRequestGatewayType()) || "REST".equalsIgnoreCase(channelTransferVO.getRequestGatewayType())) ) {
				currentBalance = PretupsBL.getDisplayAmount(previousBalance - transferedQuantity);
			} else {
				currentBalance = PretupsBL.getDisplayAmount(previousBalance + transferedQuantity);
			}
			argsArr[1] = currentBalance;
			argsArr[0] = channelTransferItemsVO.getShortName();
			keyArgumentVO = new KeyArgumentVO();
			keyArgumentVO.setKey(p_balSubKey);
			keyArgumentVO.setArguments(argsArr);
			balSmsMessageList.add(keyArgumentVO);

		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited txnSmsMessageList.size() = ");
			loggerValue.append(txnSmsMessageList.size());
			loggerValue.append( ", balSmsMessageList.size() = " );
			loggerValue.append(balSmsMessageList.size());
			log.debug(methodName,  loggerValue );
		}

		return (new Object[] { txnSmsMessageList, balSmsMessageList });
	}

	/**
	 * Method decreaseC2STransferOutCounts.
	 * This method is to decrease the C2S transaction counters on the basis of
	 * the YEAR/MONTH/WEEK/DAY changes.
	 * 
	 * @param con
	 *            Connection
	 * @param c2sTransferVO
	 *            C2STransferVO
	 * @param p_isLockRecordForUpdate
	 *            boolean
	 * @param p_newDate
	 *            Date
	 * @return UserTransferCountsVO
	 * @throws BTSLBaseException
	 */
	public static UserTransferCountsVO decreaseC2STransferOutCounts(Connection con, C2STransferVO c2sTransferVO, boolean p_isLockRecordForUpdate, Date p_newDate) throws BTSLBaseException {
		final String methodName = "decreaseC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append("User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			loggerValue.append(", p_isLockRecordForUpdate = ");
			loggerValue.append(p_isLockRecordForUpdate);
			loggerValue.append(", p_newDate = ");
			loggerValue.append(p_newDate);
			log.debug(methodName,loggerValue );
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

		UserTransferCountsVO userTransferCountsVO = null;
		try {
			userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, c2sTransferVO.getSenderID(), p_isLockRecordForUpdate);

			/*
			 * This condition will never true but for caution we are checking
			 * it.
			 * if there is no userTransferCountsVO then return null;
			 */
			if (BTSLUtil.isNullObject(userTransferCountsVO)) {
				return userTransferCountsVO;
			}
			/*
			 * To check which Counters needs to be reinitialized.
			 */
			boolean isDayCounterChange = false;
			boolean isWeekCounterChange = false;
			boolean isMonthCounterChange = false;

			final Date previousDate = c2sTransferVO.getTransferDate();
			/*
			 * This condition will never true but for caution we are checking
			 * it.
			 * if transferDate is null then return back null
			 */
			if (previousDate != null) {
				final Calendar cal = BTSLDateUtil.getInstance();
				cal.setTime(p_newDate);
				final int presentDay = cal.get(Calendar.DAY_OF_MONTH);
				final int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
				final int presentMonth = cal.get(Calendar.MONTH);
				final int presentYear = cal.get(Calendar.YEAR);
				cal.setTime(previousDate);
				final int lastTrxWeek = cal.get(Calendar.WEEK_OF_MONTH);
				final int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
				final int lastTrxMonth = cal.get(Calendar.MONTH);
				final int lastTrxYear = cal.get(Calendar.YEAR);
				if (presentYear != lastTrxYear) {
					return null;
				} else if (presentMonth != lastTrxMonth) {
					return null;
				} else if (presentWeek != lastTrxWeek) {
					isMonthCounterChange = true;
				} else if (presentDay != lastTrxDay) {
					isMonthCounterChange = true;
					isWeekCounterChange = true;
				} else {
					isMonthCounterChange = true;
					isWeekCounterChange = true;
					isDayCounterChange = true;
				}
			} else {
				return null;
			}

			userTransferCountsVO.setLastTransferID(c2sTransferVO.getTransferID());
			userTransferCountsVO.setLastTransferDate(p_newDate);
			/*
			 * Check which counter need to be updated since there may be the
			 * condition that we are useing same
			 * counter for C2C and C2S transactions. This is based on the system
			 * preferences.
			 */
			boolean useC2sSeparateTransferCounts = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS);
			if (useC2sSeparateTransferCounts) {
				if (isDayCounterChange) {
					if (userTransferCountsVO.getDailyC2STransferOutCount() > 0) {
						userTransferCountsVO.setDailyC2STransferOutCount(userTransferCountsVO.getDailyC2STransferOutCount() - 1);
					}
					if (userTransferCountsVO.getDailyC2STransferOutValue() > 0) {
						userTransferCountsVO.setDailyC2STransferOutValue(userTransferCountsVO.getDailyC2STransferOutValue() - c2sTransferVO.getTransferValue());
					}
					if(c2sTransferVO.isRoam()){
						if (userTransferCountsVO.getDailyRoamAmount() > 0) {
							userTransferCountsVO.setDailyRoamAmount(userTransferCountsVO.getDailyRoamAmount()-c2sTransferVO.getTransferValue());
						}
					}
					if (userTransferCountsVO.getDailySubscriberOutCount() > 0) {
						userTransferCountsVO.setDailySubscriberOutCount(userTransferCountsVO.getDailySubscriberOutCount() - 1);
					}
					if (userTransferCountsVO.getDailySubscriberOutValue()> 0) {
						userTransferCountsVO.setDailySubscriberOutValue(userTransferCountsVO.getDailySubscriberOutValue() - c2sTransferVO.getTransferValue());
					}
				}
				if (isWeekCounterChange) {
					if (userTransferCountsVO.getWeeklyC2STransferOutCount() > 0) {
						userTransferCountsVO.setWeeklyC2STransferOutCount(userTransferCountsVO.getWeeklyC2STransferOutCount() - 1);
					}
					if (userTransferCountsVO.getWeeklyC2STransferOutValue() > 0) {
						userTransferCountsVO.setWeeklyC2STransferOutValue(userTransferCountsVO.getWeeklyC2STransferOutValue() - c2sTransferVO.getTransferValue());
					}

					if (userTransferCountsVO.getWeeklySubscriberOutCount() > 0) {
						userTransferCountsVO.setWeeklySubscriberOutCount(userTransferCountsVO.getWeeklySubscriberOutCount() - 1);
					}
					if (userTransferCountsVO.getWeeklySubscriberOutValue()> 0) {
						userTransferCountsVO.setWeeklySubscriberOutValue(userTransferCountsVO.getWeeklySubscriberOutValue() - c2sTransferVO.getTransferValue());
					}
				}
				if (isMonthCounterChange) {
					if (userTransferCountsVO.getMonthlyC2STransferOutCount() > 0) {
						userTransferCountsVO.setMonthlyC2STransferOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() - 1);
					}
					if (userTransferCountsVO.getMonthlyC2STransferOutValue() > 0) {
						userTransferCountsVO.setMonthlyC2STransferOutValue(userTransferCountsVO.getMonthlyC2STransferOutValue() - c2sTransferVO.getTransferValue());
					}

					if (userTransferCountsVO.getMonthlySubscriberOutCount() > 0) {
						userTransferCountsVO.setMonthlySubscriberOutCount(userTransferCountsVO.getMonthlySubscriberOutCount() - 1);
					}
					if (userTransferCountsVO.getMonthlySubscriberOutValue()> 0) {
						userTransferCountsVO.setMonthlySubscriberOutValue(userTransferCountsVO.getMonthlySubscriberOutValue() - c2sTransferVO.getTransferValue());
					}
				}
			} else {
				if (isDayCounterChange) {
					if (userTransferCountsVO.getDailyOutCount() > 0) {
						userTransferCountsVO.setDailyOutCount(userTransferCountsVO.getDailyOutCount() - 1);
					}
					if (userTransferCountsVO.getDailyOutValue() > 0) {
						userTransferCountsVO.setDailyOutValue(userTransferCountsVO.getDailyOutValue() - c2sTransferVO.getTransferValue());
					}
					if(c2sTransferVO.isRoam()){
						if (userTransferCountsVO.getDailyRoamAmount() > 0) {
							userTransferCountsVO.setDailyRoamAmount(userTransferCountsVO.getDailyRoamAmount()-c2sTransferVO.getTransferValue());
						}
					}

					if (userTransferCountsVO.getDailySubscriberOutCount() > 0) {
						userTransferCountsVO.setDailySubscriberOutCount(userTransferCountsVO.getDailySubscriberOutCount() - 1);
					}
					if (userTransferCountsVO.getDailySubscriberOutValue()> 0) {
						userTransferCountsVO.setDailySubscriberOutValue(userTransferCountsVO.getDailySubscriberOutValue() - c2sTransferVO.getTransferValue());
					}
				}
				if (isWeekCounterChange) {
					if (userTransferCountsVO.getWeeklyOutCount() > 0) {
						userTransferCountsVO.setWeeklyOutCount(userTransferCountsVO.getWeeklyOutCount() - 1);
					}
					if (userTransferCountsVO.getWeeklyOutValue() > 0) {
						userTransferCountsVO.setWeeklyOutValue(userTransferCountsVO.getWeeklyOutValue() - c2sTransferVO.getTransferValue());
					}
					if (userTransferCountsVO.getWeeklySubscriberOutCount() > 0) {
						userTransferCountsVO.setWeeklySubscriberOutCount(userTransferCountsVO.getWeeklySubscriberOutCount() - 1);
					}
					if (userTransferCountsVO.getWeeklySubscriberOutValue()> 0) {
						userTransferCountsVO.setWeeklySubscriberOutValue(userTransferCountsVO.getWeeklySubscriberOutValue() - c2sTransferVO.getTransferValue());
					}
				}
				if (isMonthCounterChange) {
					if (userTransferCountsVO.getMonthlyOutCount() > 0) {
						userTransferCountsVO.setMonthlyOutCount(userTransferCountsVO.getMonthlyOutCount() - 1);
					}
					if (userTransferCountsVO.getMonthlyOutValue() > 0) {
						userTransferCountsVO.setMonthlyOutValue(userTransferCountsVO.getMonthlyOutValue() - c2sTransferVO.getTransferValue());
					}

					if (userTransferCountsVO.getMonthlySubscriberOutCount() > 0) {
						userTransferCountsVO.setMonthlySubscriberOutCount(userTransferCountsVO.getMonthlySubscriberOutCount() - 1);
					}
					if(userTransferCountsVO.getMonthlySubscriberOutValue()> 0) {
						userTransferCountsVO.setMonthlySubscriberOutValue(userTransferCountsVO.getMonthlySubscriberOutValue() - c2sTransferVO.getTransferValue());
					}
				}
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception:" );
			loggerValue.append(e.getMessage());
			log.error(methodName, "Exception p_transferID : " + c2sTransferVO.getTransferID() + ", Exception : " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[decreaseC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with userTransferCountsVO = ");
			loggerValue.append(userTransferCountsVO);
			log.debug(methodName,  loggerValue );
		}
		return userTransferCountsVO;
	}

	// for O2C
	/**
	 * Method o2cTransferUserValidate.
	 * This method is to validate the channel user for O2C transfer
	 * 
	 * @param con
	 *            Connection
	 * @param requestVO
	 *            RequestVO
	 * @param p_channeltrnasferVO
	 *            ChanneltrnasferVO
	 * @param p_currDate
	 *            Date
	 * @throws BTSLBaseException
	 */
	public static void o2cTransferUserValidate(Connection con, RequestVO requestVO, ChannelTransferVO channelTransferVO, Date p_currDate) throws BTSLBaseException {
		final String methodName = "o2cTransferUserValidate";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered requestVO = ");
			loggerValue.append(requestVO);
			loggerValue.append(", channelTransferVO = " );
			loggerValue.append(channelTransferVO);
			loggerValue.append(", p_currDate = ");
			loggerValue.append(p_currDate);
			log.debug(methodName, loggerValue);
		}

		final ChannelUserVO channelUserVO = (ChannelUserVO) requestVO.getSenderVO();
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" channelUserVO = ");
			loggerValue.append(channelUserVO);
			log.debug(methodName,  loggerValue );
		}

		BarredUserDAO barredUserDAO = null;
		barredUserDAO = new BarredUserDAO();

		// check that the channel user is barred or not
		if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, ((ChannelUserVO) requestVO.getSenderVO()).getNetworkID(), ((ChannelUserVO) requestVO.getSenderVO())
				.getMsisdn(), PretupsI.USER_TYPE_RECEIVER, null)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR);
		}

		// check that the channel user should not be in suspended
		if (channelUserVO.getInSuspend().equalsIgnoreCase(PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED,new String[]{channelUserVO.getUserCode()});
		}

		// check that the commission profile of channel user
		// should be active
		else if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCommissionProfileStatus())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_SUSPENDED,new String[]{channelUserVO.getUserCode()," Commission Profile is Suspended"});
		} else if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getTransferProfileStatus())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED);
		}

		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();

		// load the transfe rules asociated with
		// channel user
		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(),
				PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

		if (BTSLUtil.isNullObject(channelTransferRuleVO)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE);
		}

		ArrayList productList = null;
		// load the product list associated with the transfer rule
		productList = channelTransferRuleVO.getProductVOList();

		if (productList == null || productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_PRODUCT_NOT_ASSOCIATED);
		}// end of if

		// If transfer category (SALE or FOC) is not defined
		// in request then consider the request as Transfer
		final HashMap requestMap = requestVO.getRequestMap();
		if (requestMap != null) {
			final String transferCategory = BTSLUtil.NullToString((String) requestMap.get("TRFCATEGORY"));

			// check the transfer allowed if trf. category is 'SALE'
			if ((transferCategory.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE)) && (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getTransferAllowed()))) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED);
			}

			// check the FOC allowed if trf. category is 'FOC'
			else if ((transferCategory.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_FOC)) && (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getFocAllowed()))) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED);
			}

			// set the 1st & 2nd approval limits
			// in the channelTransferVO
			if ((transferCategory.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE)) && (PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getTransferAllowed()))) {
				channelTransferVO.setFirstApproverLimit(channelTransferRuleVO.getFirstApprovalLimit());
				channelTransferVO.setSecondApprovalLimit(channelTransferRuleVO.getSecondApprovalLimit());
			}

		} else {
			// check the transfer allowed for the channel user
			if (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getTransferAllowed())) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method loadAndValidateProducts.
	 * This method is used to filter the products associated to
	 * the channel user with the products which are comming in the request
	 * The boolean isTransfer determines whether any check on min and max
	 * value for transfer is applicable
	 * @param con
	 *            Connection
	 * @param requestVO
	 *            RequestVO
	 * @param prdCodeQtyMap
	 *            HashMap
	 * @param pchannelUserVO
	 *            ChannelUserVO
	 * @param isTransfer
	 * @param p_transactionType TODO
	 * @param p_paymentMode TODO
	 * 
	 * @return tmpPrdList ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadAndValidateProducts(Connection con, RequestVO requestVO, HashMap prdCodeQtyMap, ChannelUserVO pchannelUserVO, boolean isTransfer, String p_transactionType, String p_paymentMode) throws BTSLBaseException {
		final String methodName = "loadAndValidateProducts";
         StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entering  : prdCodeQtyMap - ");
			loggerValue.append(prdCodeQtyMap);
			loggerValue.append(", pchannelUserVO = ");
			loggerValue.append(pchannelUserVO);
			loggerValue.append(", isTransfer = ");
			loggerValue.append(isTransfer);
			log.debug(methodName,  loggerValue );
		}

		boolean firstCheckForPrdType = true; // for setting of product type once
		String prdType = null;
		ArrayList productListFromDao = null;
		final ArrayList tmpPrdList = new ArrayList();
		ArrayList filteredProductList = new ArrayList();
		ArrayList commPrdList = null;
		ArrayList trfPrdList = new ArrayList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		final HashMap prdShortCodeMap = new HashMap();

		// filter the products from the n/w's
		// products & the request's products
		final NetworkProductDAO networkDAO = new NetworkProductDAO();
		productListFromDao = networkDAO.loadProductListForXfr(con, null, pchannelUserVO.getNetworkID());

		final Iterator nwItr = (prdCodeQtyMap.keySet()).iterator();
		String mapPrdShortCode = null;
		while (nwItr.hasNext()) {
			mapPrdShortCode = (String) nwItr.next();
			for (int i = 0, j = productListFromDao.size(); i < j; i++) {
				networkProductVO = (NetworkProductVO) productListFromDao.get(i);
				if ((mapPrdShortCode).equals(String.valueOf(networkProductVO.getProductShortCode()))) {
					// for checking whether all the products in the request have
					// the same type
					if (firstCheckForPrdType) {
						prdType = networkProductVO.getProductType();
						firstCheckForPrdType = false;
					}
					// creating and populating the ChannelTransferItemsVO from
					// the NetworkProductVO
					channelTransferItemsVO = new ChannelTransferItemsVO();

					if (!prdType.equals(networkProductVO.getProductType())) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PRODUCT_TYPE_NOT_SAME);
					}

					channelTransferItemsVO.setProductType(networkProductVO.getProductType());
					channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
					channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
					channelTransferItemsVO.setProductName(networkProductVO.getProductName());
					channelTransferItemsVO.setShortName(networkProductVO.getShortName());
					channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
					channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
					channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
					channelTransferItemsVO.setStatus(networkProductVO.getStatus());
					channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
					channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
					channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());

					prdShortCodeMap.put(networkProductVO.getProductCode(), channelTransferItemsVO);
					tmpPrdList.add(channelTransferItemsVO);
					break;
				}
			}
		}

		// check whether after filteration the filtered product list
		// contains the same no. of products as we receive from the request
		if (prdCodeQtyMap.size() != tmpPrdList.size()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NETWORK_PRODUCTS_NOT_MATCHING);
		}

		tmpPrdList.clear();

		final Date currDate = new Date();
		final CommissionProfileDAO commPrDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();

		// load the latest version of commission profile
		final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, pchannelUserVO.getCommissionProfileSetID(), currDate);
		final String commProfileLatestVer = commissionProfileSetVO.getCommProfileVersion();
		if (BTSLUtil.isNullString(commProfileLatestVer)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_LATEST_COMMISSION_PROFILE_ASSOCIATED);
		}

		pchannelUserVO.setCommissionProfileSetVersion(commProfileLatestVer);

		commPrdList = commPrDAO.loadCommissionProfileProductsList(con, pchannelUserVO.getCommissionProfileSetID(), pchannelUserVO.getCommissionProfileSetVersion(), p_transactionType, p_paymentMode);
		if (commPrdList == null || commPrdList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_COMMISION_PRODUCT_ASSOCIATED);
		}

		// filter the products of commission profile
		// with the products of request
		CommissionProfileProductsVO commProfilePrdVO = null;

		final Iterator mapItr = (prdShortCodeMap.keySet()).iterator();
		String mapPrdCode = null;
		while (mapItr.hasNext()) {
			mapPrdCode = (String) mapItr.next();
			for (int x = 0, z = commPrdList.size(); x < z; x++) {
				commProfilePrdVO = (CommissionProfileProductsVO) commPrdList.get(x);
				final String productCode = commProfilePrdVO.getProductCode();
				if ((mapPrdCode).equals(productCode)) {
					channelTransferItemsVO = (ChannelTransferItemsVO) prdShortCodeMap.get(productCode);
					// long qty =
					// Long.parseLong(prdCodeQtyMap.get(String.valueOf(channelTransferItemsVO.getProductShortCode())).toString());;
					final double qty = Double.parseDouble(prdCodeQtyMap.get(String.valueOf(channelTransferItemsVO.getProductShortCode())).toString());
					final long qty1 = PretupsBL.getSystemAmount(prdCodeQtyMap.get(String.valueOf(channelTransferItemsVO.getProductShortCode())).toString());
					if (isTransfer) {
						// if(((qty >=
						// Long.parseLong(PretupsBL.getDisplayAmount(commProfilePrdVO.getMinTransferValue())))
						// && (qty<=
						// Long.parseLong(PretupsBL.getDisplayAmount(commProfilePrdVO.getMaxTransferValue())))))
						if (qty1 >= commProfilePrdVO.getMinTransferValue() && qty1 <= commProfilePrdVO.getMaxTransferValue()) {
							// int
							// multiple=Integer.parseInt(commProfilePrdVO.getTransferMultipleOffAsString());
							final long multiple = commProfilePrdVO.getTransferMultipleOff();
							if ((qty1 % multiple) == 0) {
								channelTransferItemsVO.setRequestedQuantity(String.valueOf(qty));
								tmpPrdList.add(channelTransferItemsVO);
							} else {
								throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_QTY_INVALID);
							}
						} else {
							throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_QTY_INVALID);
						}
					} else {
						channelTransferItemsVO.setRequestedQuantity(String.valueOf(qty));
						tmpPrdList.add(channelTransferItemsVO);
					}
					channelTransferItemsVO.setTaxOnChannelTransfer(commProfilePrdVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commProfilePrdVO.getTaxOnFOCApplicable());
					break;
				}
			}
		}

		if (tmpPrdList.size() != prdCodeQtyMap.size()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_PRODUCTS_NOT_MATCHING);
		}

		filteredProductList = null;
		filteredProductList = new ArrayList(tmpPrdList);
		tmpPrdList.clear();

		// load the transfer profile product list
		final TransferProfileDAO transferPrfDAO = new TransferProfileDAO();
		TransferProfileVO transferProfileVO = new TransferProfileVO();
		transferProfileVO = transferPrfDAO.loadTransferProfileThroughProfileID(con, pchannelUserVO.getTransferProfileID(), pchannelUserVO.getNetworkID(), pchannelUserVO
				.getCategoryCode(), true);
		trfPrdList = transferProfileVO.getProfileProductList();
		if (trfPrdList == null || trfPrdList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_TRANSFERPROFILE_PRODUCT_ASSOCIATED);
		}

		// filter the products of transfer profile
		// with the products of request
		TransferProfileProductVO transferPrfPrdVO = null;

		for (int x = 0, z = filteredProductList.size(); x < z; x++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) filteredProductList.get(x);
			for (int y = 0, i = trfPrdList.size(); y < i; y++) {
				transferPrfPrdVO = (TransferProfileProductVO) trfPrdList.get(y);
				if ((transferPrfPrdVO.getProductCode()).equals(channelTransferItemsVO.getProductCode())) {
					tmpPrdList.add(channelTransferItemsVO);
					break;
				}

			}
		}
		if (tmpPrdList.size() != prdCodeQtyMap.size()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_PRODUCTS_NOT_MATCHING);
		}

		final ChannelUserVO channelUserVO = (ChannelUserVO) requestVO.getSenderVO();
		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();

		// the call to the method loads the transfer rule between the Operator
		// and the passed category code for the domain and network.
		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(),
				PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		final ArrayList transferRulePrdList = channelTransferRuleVO.getProductVOList();
		ListValueVO listValueVO = null;

		filteredProductList = null;
		filteredProductList = new ArrayList(tmpPrdList);
		tmpPrdList.clear();

		// filter the products associated with the transfer rule
		// with the products of request
		for (int a = 0, b = filteredProductList.size(); a < b; a++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) filteredProductList.get(a);
			for (int c = 0, d = transferRulePrdList.size(); c < d; c++) {
				listValueVO = (ListValueVO) transferRulePrdList.get(c);
				if ((listValueVO.getValue()).equals(channelTransferItemsVO.getProductCode())) {
					tmpPrdList.add(channelTransferItemsVO);
					break;
				}
			}
		}
		if (tmpPrdList.size() != prdCodeQtyMap.size()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_RULE_PRODUCTS_NOT_MATCHING);
		}

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting : tmpPrdList :: ");
			loggerValue.append(tmpPrdList.size());
			log.debug(methodName,  loggerValue );
		}

		return filteredProductList;
	}

	/**
	 * This method checks the message sequence and validates
	 * whether the request contents are valid or not.Also the boolean
	 * isTransfer decides whether the request is for
	 * transfer or withdraw.
	 * 
	 * @param con
	 *            Connection
	 * @param requestVO
	 *            RequestVO
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 * @param isTransfer
	 *            Boolean
	 * @return productMap HashMap
	 */
	public static HashMap validateO2CMessageContent(Connection con, RequestVO requestVO, ChannelTransferVO channelTransferVO, boolean isTransfer) throws BTSLBaseException {
		final String methodName = "validateO2CMessageContent";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered requestVO : ");
			loggerValue.append(requestVO);
			loggerValue.append(", channelTransferVO : ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", isTransfer : ");
			loggerValue.append(isTransfer);
			log.debug(methodName,  loggerValue );
		}
		boolean externalTxnNumeric = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_NUMERIC);
		boolean externalTxnUnique = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
		boolean channelTransferInfoRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
		// take the messageArray from the request and filter the product short
		// code
		// and product qty from the array and return a hashMap
		final HashMap productMap = checkO2CTrfReqMsgSyntax(requestVO.getRequestMessageArray(), (((ChannelUserVO) requestVO.getSenderVO()).getUserPhoneVO())
				.getPinRequired());

		final HashMap requestMap = requestVO.getRequestMap();

		// take the requestMap from the requset
		// & validate the data from this hashMap
		if (requestMap != null) {
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("entered for requestMap not null :requestMap = ");
				loggerValue.append(requestMap.size());
				log.debug(methodName,  loggerValue);
			}

			final String extTxnNumber = (String) requestMap.get("EXTTXNNUMBER");

			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("extTxnNumber from requestMap = " );
				loggerValue.append(extTxnNumber);
				log.debug(methodName, loggerValue );
			}

			// checks on external transaction id that
			if (!BTSLUtil.isNullString(extTxnNumber)) {
				if (externalTxnNumeric) {
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

				if (externalTxnUnique) {
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

			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("extTxnDate from requestMap = ");
				loggerValue.append(extTxnDate);
				log.debug(methodName,  loggerValue);
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
				log.errorTrace(methodName, e1);
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

				if (log.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("trfCategory from requestMap = ");
					loggerValue.append(trfCategory);
					log.debug(methodName,  loggerValue );
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

					if (log.isDebugEnabled()) {
						loggerValue.setLength(0);
						loggerValue.append("From requestMap : paymentType = ");
						loggerValue.append(paymentType);
						loggerValue.append(", paymentInstNo = ");
						loggerValue.append(paymentInstNo);
						loggerValue.append(", paymentDateString = ");
						loggerValue.append(paymentDateString);
						log.debug(methodName,loggerValue);
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
						log.errorTrace(methodName, e1);
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
			final Boolean isTagReq=channelTransferInfoRequired;
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

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting : productMap.size():: ");
			loggerValue.append(productMap.size());
			log.debug(methodName,  loggerValue );
		}

		return productMap;
	}

	/**
	 * This method checks the message sequence and finally return
	 * a Map of the product short code and product codes.
	 * 
	 * @param messageArray
	 * @param userPinRequired
	 * @return HashMap ProductMap
	 */
	private static HashMap checkO2CTrfReqMsgSyntax(String[] messageArray, String userPinRequired) throws BTSLBaseException {
		final String methodName = "checkO2CTrfReqMsgSyntax";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered with messageArray : ");
			loggerValue.append(messageArray);
			loggerValue.append(", userPinRequired : ");
			loggerValue.append(userPinRequired);
			log.debug(methodName,  loggerValue );
		}

		for (int i = 0, j = messageArray.length; i < j; i++) {
			log.debug(methodName, "messageArray[" + i + "] : " + messageArray[i]);
		}
		String defaultProductValue = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT);
		final HashMap productMap = new HashMap();

		// the message format:
		// [keyword] [productcode] [qty] [productcode] [qty] [pin]

		// if no product in the messageArray then set the default product
		if (!(PretupsI.YES.equals(userPinRequired)) && messageArray.length == 2) {
			log.debug(methodName, "messageArray[1] : " + messageArray[1]);
			productMap.put(defaultProductValue, messageArray[1]);
		} else if (messageArray.length == 3 && ! (messageArray[0].equals("O2CRET"))) {
			log.debug(methodName, "messageArray[1]  :: " + messageArray[1]);
			//Handling of multi-product
            if(!BTSLUtil.isNullString(messageArray[2]) && !defaultProductValue.equals(messageArray[2])){
            	productMap.put(messageArray[2],messageArray[1]);
            } else {
            	productMap.put(defaultProductValue,messageArray[1]);            	
            }
		} else
			// get the product short code & qty and
			// make a HashMap from these two parameters

			if (messageArray.length > 3 || (messageArray.length == 3 && messageArray[0].equals("O2CRET")) ) {
				log.debug(methodName, "arrlength " + messageArray.length);
				final int startIndex = 1;
				int endIndex = 0;
				if ((messageArray.length % 2) == 0) {
					endIndex = (messageArray.length - 1);
				} else {
					endIndex = messageArray.length;
				}

				for (int i = startIndex; i < endIndex; i += 2) {
					if (productMap.size() > 0) {
						if (productMap.containsKey(messageArray[i + 1])) {
							throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_MESSAGE_FORMAT_NOT_PROPER, 0, messageArray, null);
						}
					}

					productMap.put(messageArray[i + 1], messageArray[i]);
				}
			} else {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName,
						PretupsErrorCodesI.ERROR_MESSAGE_FORMAT_NOT_PROPER);
			}

		// validate the product short codes and
		// quantities which are comming in the request
		String prdShortCode = null;
		String prdQty = null;
		final Collection keySet = productMap.keySet();
		final Iterator itr = keySet.iterator();
		while (itr.hasNext()) {
			prdShortCode = (String) itr.next();
			prdQty = (String) productMap.get(prdShortCode);

			if (!BTSLUtil.isDecimalValue(prdQty)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_QUANTITY);
			} else if (Double.parseDouble(prdQty) <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_LESS_DEFAULT_PRODUCT_QUANTITY);
			} else if (BTSLUtil.isNullString(prdShortCode)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_CODE_FORMAT);
			} else if (!BTSLUtil.isNumeric(prdShortCode)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_CODE_FORMAT);
			}
		}

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting : productMap.size():: ");
			loggerValue.append(productMap.size());
			log.debug(methodName,  loggerValue);
		}

		return productMap;
	}

	// added by Siddhartha for O2CWithdrawController

	/**
	 * Method o2cWithdrawUserValidate.
	 * This method is to validate the channel user for O2C withdraw
	 * 
	 * @param con
	 *            Connection
	 * @param requestVO
	 *            RequestVO
	 * @throws BTSLBaseException
	 */
	public static void o2cWithdrawUserValidate(Connection con, RequestVO requestVO, Date p_currDate) throws BTSLBaseException {
		final String methodName = "o2cWithdrawUserValidate";

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered requestVO = " + requestVO + ", p_currDate = " + p_currDate);
		}

		final ChannelUserVO channelUserVO = (ChannelUserVO) requestVO.getSenderVO();

		// checks whether the commission profile associated with the user is
		// active
		if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCommissionProfileStatus())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_SUSPENDED,new String[]{channelUserVO.getUserCode()," Commission Profile is Suspended"});
		}

		if (PretupsI.LOCALE_LANGAUGE_EN.equals(requestVO.getLocale().getLanguage())) {
			channelUserVO.setCommissionProfileSuspendMsg(channelUserVO.getCommissionProfileLang1Msg());
		} else {
			channelUserVO.setCommissionProfileSuspendMsg(channelUserVO.getCommissionProfileLang2Msg());
		}

		// checks if the transfer profile associated with the user is active or
		// not
		if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getTransferProfileStatus())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED);
		}// end of if-else-if

		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();

		// the call to the method loads the transfer rule between the Operator
		// and the passed category code for the domain and network.
		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(),
				PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

		if (BTSLUtil.isNullObject(channelTransferRuleVO)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE);
		}
		// checks if the withdraw is allowed or not
		else if (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getWithdrawAllowed())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_WITHDRAW_NOT_ALLOWED);
		}// end of second if-else-if
		ArrayList productList = null;
		// load the product list associated with the transfer rule
		productList = channelTransferRuleVO.getProductVOList();

		if (productList == null || productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_PRODUCT_NOT_ASSOCIATED);
		}// end of if

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting.....");
		}
	}

	// added by Pankaj Namdev for O2CReturn Controller

	/**
	 * This method is used to validate the channel user for O2C return
	 * 
	 * @param con
	 * @param requestVO
	 * @param p_currDate
	 * @throws BTSLBaseException
	 */
	public static void o2cReturnUserValidate(Connection con, RequestVO requestVO, Date p_currDate) throws BTSLBaseException {
		final String methodName = "o2cReturnUserValidate";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered requestVO = " + requestVO + ", p_currDate = " + p_currDate);
		}

		final ChannelUserVO channelUserVO = (ChannelUserVO) requestVO.getSenderVO();

		if (requestVO.getMessageGatewayVO().getAccessFrom() != null && requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_LOGIN)) {
			// Check whether user is barred in system or not.
			final BarredUserDAO barredUserDAO = new BarredUserDAO();
			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, channelUserVO.getNetworkID(), channelUserVO.getMsisdn(), PretupsI.USER_TYPE_SENDER, null)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_SENDER_BAR);
			}
		}

		// meditel changes
		boolean statusAllowed = false;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO.getUserType(),
				requestVO.getRequestGatewayType());
		if (BTSLUtil.isNullObject(userStatusVO)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
		} else {
			final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
			final String status[] = userStatusAllowed.split(",");
			for (int i = 0; i < status.length; i++) {
				if (status[i].equals(channelUserVO.getStatus())) {
					statusAllowed = true;
				}
			}
			if (!statusAllowed) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED);
			}
		}

		// check channel user's out suspend status
		if (!BTSLUtil.isNullObject(channelUserVO) && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
		}

		// checks whether the comission profile associated with the user is
		// active
		if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCommissionProfileStatus())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_SUSPENDED,new String[]{channelUserVO.getUserCode()," Commission Profile is Suspended"});
		}

		// checks if the transfer profile associated with the user is active or
		// not
		if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getTransferProfileStatus())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED);
		}

		// loading the transfer rule between the Operator and the passed
		// category code for the domain and network.
		final ChannelTransferRuleDAO channelTransferRulesDAO = new ChannelTransferRuleDAO();
		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRulesDAO.loadTransferRule(con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(),
				PretupsI.TRANSFER_RULE_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		if (BTSLUtil.isNullObject(channelTransferRuleVO)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE);
		}// if

		// check if return is allowed or not
		if (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getReturnAllowed())) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED);
		}// if

		// load the product list associated with the transfer rule
		final ArrayList tempProductList = channelTransferRuleVO.getProductVOList();
		if (tempProductList == null || tempProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_PRODUCT_NOT_ASSOCIATED);
		}// if

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting...");
		}
	}// End of o2cReturnUserValidate

	/**
	 * Method to increase the Channel to subscriber transfer out counts and
	 * values
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_isCheckThresholds
	 *            boolean
	 * @param p_quantityRequired
	 *            int
	 * @throws BTSLBaseException
	 */
	public static void increaseC2STransferOutCounts(Connection con, C2STransferVO c2sTransferVO, boolean p_isCheckThresholds, int p_quantityRequired) throws BTSLBaseException {
		final String methodName = "increaseC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			loggerValue.append(", p_isCheckThresholds = " );
			loggerValue.append( p_isCheckThresholds);
			loggerValue.append(", p_quantityRequired = " );
			loggerValue.append(p_quantityRequired);
			log.debug(methodName,loggerValue );
		}

		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		final boolean isLockRecordForUpdate = true;
		try {
			final UserTransferCountsVO userTransferCountsVO = checkC2STransferOutCounts(con, c2sTransferVO, isLockRecordForUpdate, p_isCheckThresholds, p_quantityRequired);
			boolean useC2sSeparateTransferCounts = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS);
			if (useC2sSeparateTransferCounts) {
				userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
				userTransferCountsVO.setDailyC2STransferOutCount(userTransferCountsVO.getDailyC2STransferOutCount() + p_quantityRequired);
				userTransferCountsVO
				.setDailyC2STransferOutValue(userTransferCountsVO.getDailyC2STransferOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setWeeklyC2STransferOutCount(userTransferCountsVO.getWeeklyC2STransferOutCount() + p_quantityRequired);
				userTransferCountsVO
				.setWeeklyC2STransferOutValue(userTransferCountsVO.getWeeklyC2STransferOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setMonthlyC2STransferOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() + p_quantityRequired);
				userTransferCountsVO
				.setMonthlyC2STransferOutValue(userTransferCountsVO.getMonthlyC2STransferOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
				userTransferCountsVO.setLastOutTime(c2sTransferVO.getCreatedOn());
				// updated by akanksha for ethiopia telecom
				userTransferCountsVO.setDailySubscriberOutCount(userTransferCountsVO.getDailySubscriberOutCount() + p_quantityRequired);
				userTransferCountsVO.setDailySubscriberOutValue(userTransferCountsVO.getDailySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setWeeklySubscriberOutCount(userTransferCountsVO.getWeeklySubscriberOutCount() + p_quantityRequired);
				userTransferCountsVO
				.setWeeklySubscriberOutValue(userTransferCountsVO.getWeeklySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setMonthlySubscriberOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() + p_quantityRequired);
				userTransferCountsVO
				.setMonthlySubscriberOutValue(userTransferCountsVO.getMonthlySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);

			} else {
				userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
				userTransferCountsVO.setDailyOutCount(userTransferCountsVO.getDailyOutCount() + p_quantityRequired);
				userTransferCountsVO.setDailyOutValue(userTransferCountsVO.getDailyOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setWeeklyOutCount(userTransferCountsVO.getWeeklyOutCount() + p_quantityRequired);
				userTransferCountsVO.setWeeklyOutValue(userTransferCountsVO.getWeeklyOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setMonthlyOutCount(userTransferCountsVO.getMonthlyOutCount() + p_quantityRequired);
				userTransferCountsVO.setMonthlyOutValue(userTransferCountsVO.getMonthlyOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
				userTransferCountsVO.setLastOutTime(c2sTransferVO.getCreatedOn());
				// updated by akanksha for ethiopia telecom
				userTransferCountsVO.setDailySubscriberOutCount(userTransferCountsVO.getDailySubscriberOutCount() + p_quantityRequired);
				userTransferCountsVO.setDailySubscriberOutValue(userTransferCountsVO.getDailySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setWeeklySubscriberOutCount(userTransferCountsVO.getWeeklySubscriberOutCount() + p_quantityRequired);
				userTransferCountsVO
				.setWeeklySubscriberOutValue(userTransferCountsVO.getWeeklySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);
				userTransferCountsVO.setMonthlySubscriberOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() + p_quantityRequired);
				userTransferCountsVO
				.setMonthlySubscriberOutValue(userTransferCountsVO.getMonthlySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired);

			}
			if (c2sTransferVO.isRoam()) {
				userTransferCountsVO.setDailyRoamAmount(userTransferCountsVO.getDailyRoamAmount() + c2sTransferVO.getRequestedAmount()* p_quantityRequired );
			}

			final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, userTransferCountsVO.isUpdateRecord());

			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exited updateCount = ");
				loggerValue.append(updateCount);
				log.debug(methodName,  loggerValue);
			}
			if (updateCount <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
			}

		} catch (BTSLBaseException be) {
			// b_log.errorTrace(methodName ,ex);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue);
			
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[increaseC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method to check the Channel to subscriber transfer Out counts.
	 * A preference is there that will decide that whether separate C2S transfer
	 * out will be used or C2C transfer out count to be used
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_isLockRecordForUpdate
	 * @param p_isCheckThresholds
	 *            boolean
	 * @param p_quantityRequired
	 *            int
	 * @return UserTransferCountsVO
	 * @throws BTSLBaseException
	 */
	public static UserTransferCountsVO checkC2STransferOutCounts(Connection con, C2STransferVO c2sTransferVO, boolean p_isLockRecordForUpdate, boolean p_isCheckThresholds, int p_quantityRequired) throws BTSLBaseException {
		final String methodName = "checkC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			loggerValue.append(", p_isLockRecordForUpdate = " );
			loggerValue.append(p_isLockRecordForUpdate);
			loggerValue.append(", p_isCheckThresholds = ");
			loggerValue.append(p_isCheckThresholds);
			loggerValue.append(", p_quantityRequired = ");
			loggerValue.append(p_quantityRequired);
			log.debug(methodName,loggerValue );
		}

		ChannelUserDAO channelUserDAO = null;

		UserTransferCountsVO userTransferCountsVO = null;
		TransferProfileVO transferProfileVO = null;
		String[] strArr = null;
		try {
			channelUserDAO = new ChannelUserDAO();

			final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

			userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, c2sTransferVO.getSenderID(), p_isLockRecordForUpdate);

			if (!p_isCheckThresholds) {
				return userTransferCountsVO;
			}
			transferProfileVO = TransferProfileCache.getTransferProfileDetails(((ChannelUserVO) c2sTransferVO.getSenderVO()).getTransferProfileID(), c2sTransferVO
					.getNetworkCode());

			// Done so as if someone has defined in Transfer Profile as Allowed
			// Transfer as 0
			if (BTSLUtil.isNullObject(userTransferCountsVO)) {
				userTransferCountsVO = new UserTransferCountsVO();
				userTransferCountsVO.setUpdateRecord(false);
			}

			userTransferCountsVO.setLastTransferID(c2sTransferVO.getLastTransferId());

			// To check whether Counters needs to be reinitialized or not
			final boolean isCounterReInitalizingReqd = checkResetCountersAfterPeriodChange(userTransferCountsVO, c2sTransferVO.getCreatedOn());
			c2sTransferVO.setTransferProfileCtInitializeReqd(isCounterReInitalizingReqd);
			boolean useC2sSeparateTransferCounts = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS);
			if (useC2sSeparateTransferCounts) {
				if (transferProfileVO.getDailyC2STransferOutCount() < userTransferCountsVO.getDailyC2STransferOutCount() + p_quantityRequired) {
					strArr = new String[] { String.valueOf(transferProfileVO.getDailyC2STransferOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_DAILY_OUT_CTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getDailyC2STransferOutValue() < userTransferCountsVO.getDailyC2STransferOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getDailyC2STransferOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getDailyC2STransferOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_DAILY_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklyC2STransferOutCount() < userTransferCountsVO.getWeeklyC2STransferOutCount() + p_quantityRequired) {
					strArr = new String[] { String.valueOf(transferProfileVO.getWeeklyC2STransferOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_WEEKLY_OUT_CTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklyC2STransferOutValue() < userTransferCountsVO.getWeeklyC2STransferOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getWeeklyC2STransferOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklyC2STransferOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_WEEKLY_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlyC2STransferOutCount() < userTransferCountsVO.getMonthlyC2STransferOutCount() + p_quantityRequired) {
					strArr = new String[] { String.valueOf(transferProfileVO.getMonthlyC2STransferOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_MONTHLY_OUT_CTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlyC2STransferOutValue() < userTransferCountsVO.getMonthlyC2STransferOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getMonthlyC2STransferOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlyC2STransferOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_MONTHLY_OUT_VALREACHED, 0, strArr, null);
				}
				// added by akanksha gor ethiotelecom
				else if (transferProfileVO.getDailySubscriberOutCount() < userTransferCountsVO.getDailySubscriberOutCount() + p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getDailySubscriberOutCount()), PretupsBL.getDisplayAmount(transferProfileVO.getDailySubscriberOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_DAILY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getDailySubscriberOutValue() < userTransferCountsVO.getDailySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getDailySubscriberOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getDailySubscriberOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_DAILY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklySubscriberOutCount() < userTransferCountsVO.getWeeklySubscriberOutCount() + p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getWeeklySubscriberOutCount()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklySubscriberOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklySubscriberOutValue() < userTransferCountsVO.getWeeklySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getWeeklySubscriberOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklySubscriberOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlySubscriberOutCount() < userTransferCountsVO.getMonthlySubscriberOutCount() + p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getMonthlySubscriberOutCount()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlySubscriberOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlySubscriberOutValue() < userTransferCountsVO.getMonthlySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getMonthlySubscriberOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlySubscriberOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				}
			} else {
				if (transferProfileVO.getDailyOutCount() < userTransferCountsVO.getDailyOutCount() + p_quantityRequired) {
					strArr = new String[] { String.valueOf(transferProfileVO.getDailyOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_DAILY_OUT_CTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getDailyOutValue() < userTransferCountsVO.getDailyOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getDailyOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getDailyOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_DAILY_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklyOutCount() <= userTransferCountsVO.getWeeklyOutCount() + p_quantityRequired) {
					strArr = new String[] { String.valueOf(transferProfileVO.getWeeklyOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_WEEKLY_OUT_CTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklyOutValue() < userTransferCountsVO.getWeeklyOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getWeeklyOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklyOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_WEEKLY_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlyOutCount() <= userTransferCountsVO.getMonthlyOutCount() + p_quantityRequired) {
					strArr = new String[] { String.valueOf(transferProfileVO.getMonthlyOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_MONTHLY_OUT_CTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlyOutValue() < userTransferCountsVO.getMonthlyOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getMonthlyOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlyOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_MONTHLY_OUT_VALREACHED, 0, strArr, null);
				}
				// added by akanksha gor ethiotelecom
				else if (transferProfileVO.getDailySubscriberOutCount() < userTransferCountsVO.getDailySubscriberOutCount() + p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getDailySubscriberOutCount()), PretupsBL.getDisplayAmount(transferProfileVO.getDailySubscriberOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_DAILY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getDailySubscriberOutValue() < userTransferCountsVO.getDailySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getDailySubscriberOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getDailySubscriberOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_DAILY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklySubscriberOutCount() < userTransferCountsVO.getWeeklySubscriberOutCount() + p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getWeeklySubscriberOutCount()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklySubscriberOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklySubscriberOutValue() < userTransferCountsVO.getWeeklySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getWeeklySubscriberOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklySubscriberOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlySubscriberOutCount() < userTransferCountsVO.getMonthlySubscriberOutCount() + p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getMonthlySubscriberOutCount()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlySubscriberOutCount()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlySubscriberOutValue() < userTransferCountsVO.getMonthlySubscriberOutValue() + c2sTransferVO.getRequestedAmount() * p_quantityRequired) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getMonthlySubscriberOutValue()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlySubscriberOutValue()), String.valueOf(p_quantityRequired) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_VALREACHED, 0, strArr, null);
				}
			}
		} catch (BTSLBaseException be) {
			loggerValue.setLength(0);
			loggerValue.append("BTSL BaseException p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" ");
			loggerValue.append(be.getMessage());
			log.error(methodName,  loggerValue);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID() );
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			log.error(methodName, loggerValue );
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with userTransferCountsVO =" );
			loggerValue.append(userTransferCountsVO);
			log.debug(methodName, loggerValue);
		}
		return userTransferCountsVO;
	}

	/**
	 * Method that will call Data access object class to add the Channel to
	 * subscriber transfer details in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static void addC2STransferDetails(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "addC2STransferDetails";
		final ArrayList lmsProfileList = null;
		 Jedis jedis = null;
		try {
			Boolean c2sSeqIDAlwd = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_ALWD);
            String c2sSeqIDForGWC = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_FOR_GWC);
            String c2sSeqIDApplSer = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_APPL_SER);
			final Date _currentDate = new Date();
			final ChannelUserVO senderVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
			UserPhoneVO userPhoneVO = senderVO.getUserPhoneVO();
			final String lmsProfileSetId = senderVO.getLmsProfile();
			if (!BTSLUtil.isNullString(lmsProfileSetId)) {
				// LMS profile cache by brajesh
				final ProfileSetDetailsLMSVO profileSetDetailsLMSVO = (ProfileSetDetailsLMSVO) LMSProfileCache.getObject(lmsProfileSetId, _currentDate);
				String lmsProfileVersion = null;
				if (!BTSLUtil.isNullObject(profileSetDetailsLMSVO)) {
					lmsProfileVersion = profileSetDetailsLMSVO.getVersion();
					c2sTransferVO.setLmsVersion(lmsProfileVersion);
				}
			}
			try {
				if (c2sSeqIDAlwd && BTSLUtil.isStringIn(userPhoneVO.getRequestGatewayCode(), c2sSeqIDForGWC)) {

					ChannelUserDAO channelUserDAO = new ChannelUserDAO();

					String ownerId = senderVO.getOwnerID();
					ChannelUserVO ownerChannelUserVO = channelUserDAO.loadOwnerChannelUserByUserID(con, ownerId);
					channelUserDAO.lockUserPhonesTable(con,ownerChannelUserVO.getUserPhoneVO());
					if(BTSLUtil.isStringIn(senderVO.getServiceTypes(), c2sSeqIDApplSer))
					{
						if(!BTSLUtil.isNullString(ownerChannelUserVO.getEmpCode()))
						{
							String generatedSquenceID = ChannelUserBL.generateSeqId(ownerChannelUserVO.getUserPhoneVO().getTempTransferID(), ownerChannelUserVO.getEmpCode());
							userPhoneVO.setOwnerTempTransferId(generatedSquenceID);
							channelUserDAO.setTempTransferIdOfOwner(con, generatedSquenceID, ownerId);
						}
					}
					else
					{
						if(!BTSLUtil.isNullString(ownerChannelUserVO.getEmpCode()))
						{
							userPhoneVO.setOwnerTempTransferId(ownerChannelUserVO.getUserPhoneVO().getTempTransferID());
							channelUserDAO.setTempTransferIdOfOwner(con, ownerChannelUserVO.getUserPhoneVO().getTempTransferID(), ownerId);
						}
					}
				}
			}catch(Exception e){
				log.error(methodName,"Exception while Generating sequence ID. Exception : "+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelTransferBL[addC2STransferDetails]",senderVO.getUserPhoneVO().getTempTransferID(),c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getNetworkCode(),"Exception : "+e.getMessage());

			}
			ChannelUserVO channelUserVO = (ChannelUserVO) c2sTransferVO.getSenderVO();
			c2sTransferVO.setSenderCategoryCode(channelUserVO.getCategoryCode());
			c2sTransferVO.setTransferProfileID(channelUserVO.getTransferProfileID());
			c2sTransferVO.setCommissionProfileSetID(channelUserVO.getCommissionProfileSetID());
			if(c2sSeqIDAlwd && BTSLUtil.isStringIn(channelUserVO.getServiceTypes(), c2sSeqIDApplSer) && BTSLUtil.isStringIn(((UserPhoneVO) channelUserVO.getUserPhoneVO()).getRequestGatewayCode(), c2sSeqIDForGWC)){
				c2sTransferVO.setTempId(((UserPhoneVO) channelUserVO.getUserPhoneVO()).getOwnerTempTransferId());
	        }else{
	        	c2sTransferVO.setTempId(((UserPhoneVO) channelUserVO.getUserPhoneVO()).getTempTransferID());
	        }
			
			int updateCount = 0;
			if("Y".equals(BTSLUtil.NullToString(Constants.getProperty("KAFKA_ENABLE"))))
			{
				PretupsKafkaProducerBL.c2sTransfersInsertProducer( c2sTransferVO);
				updateCount = 1;
			}
			else{
				 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim()) && trfIdCheck) {
					   RedisActivityLog.log("ChannelTransferBL->addC2STransferDetails->DEEPS->Start");
		   			   jedis = RedisConnectionPool.getPoolInstance().getResource();
					   RedisActivityLog.log("ChannelTransferBL->addC2STransferDetails1->DEEPS->Start");
					   jedis.hset(hkeytransferId,c2sTransferVO.getTransferID(),gson.toJson(c2sTransferVO));
					   RedisActivityLog.log("ChannelTransferBL->addC2STransferDetails->DEEPS->End");
					   updateCount = 1;
					}else{
						updateCount = new C2STransferDAO().addC2STransferDetails(con, c2sTransferVO, true);
					}
			}
			if (updateCount <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
			}
			
		} catch (BTSLBaseException be) {
			throw new BTSLBaseException(be);
		}catch(JedisConnectionException je){
	 		log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[addC2STransferDetails]", "", "", "", "JedisConnectionException :" + je.getMessage());
		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			        log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[addC2STransferDetails]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[addC2STransferDetails]",
						c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception :" + e.getMessage());
				log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	log.errorTrace(methodName, e);
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
			}finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
	}

	/**
	 * Method to decrease the Channel to subscriber transfer out counts and
	 * values in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_quantityRequired
	 * @throws BTSLBaseException
	 */
	public static void decreaseC2STransferOutCounts(Connection con, C2STransferVO c2sTransferVO, int p_quantityRequired) throws BTSLBaseException {
		final String methodName = "decreaseC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			loggerValue.append(", p_quantityRequired = ");
			loggerValue.append(p_quantityRequired);
			log.debug(methodName,loggerValue);
		}

		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		boolean isCounterReInitalizingReqd = false;
		try {
			final UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, c2sTransferVO.getSenderID(), true);
			if (!BTSLUtil.isNullObject(userTransferCountsVO)) {
				userTransferCountsVO.setLastTransferID(c2sTransferVO.getTransferID());
				isCounterReInitalizingReqd = checkResetCountersAfterPeriodChange(userTransferCountsVO, c2sTransferVO.getCreatedOn());
				if (isCounterReInitalizingReqd) {
					setC2STransferOutCountsForDecrease(c2sTransferVO, userTransferCountsVO, p_quantityRequired);
					final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, true);

					if (log.isDebugEnabled()) {
						loggerValue.setLength(0);
						loggerValue.append("Exited updateCount = ");
						loggerValue.append(updateCount);
						log.debug(methodName,  loggerValue);
					}
					if (updateCount <= 0) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
					}
				} else {
					// Do we need to decrease the counters here also
					setC2STransferOutCountsForDecrease(c2sTransferVO, userTransferCountsVO, p_quantityRequired);
					final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, true);

					if (log.isDebugEnabled()) {
						loggerValue.setLength(0);
						loggerValue.append("Exited updateCount = ");
						loggerValue.append(updateCount);
						log.debug(methodName, loggerValue);
					}
					if (updateCount <= 0) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
					}
				}
			}

		} catch (BTSLBaseException be) {
			// b_log.errorTrace(methodName ,ex);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			log.error(methodName, loggerValue );
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[decreaseC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Set the appropriate value in the transfer Counts VO
	 * 
	 * @param c2sTransferVO
	 * @param userTransferCountsVO
	 * @param p_quantityRequired
	 */
	public static void setC2STransferOutCountsForDecrease(C2STransferVO c2sTransferVO, UserTransferCountsVO userTransferCountsVO, int p_quantityRequired) {
		final String methodName = "setC2STransferOutCountsForDecrease";

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with p_quantityRequired = " + p_quantityRequired);
		}
		boolean useC2sSeparateTransferCounts = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS);
		if (useC2sSeparateTransferCounts) {
			userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
			if (userTransferCountsVO.getDailyC2STransferOutCount() > 0) {
				userTransferCountsVO.setDailyC2STransferOutCount(userTransferCountsVO.getDailyC2STransferOutCount() - p_quantityRequired);
			}
			if (userTransferCountsVO.getDailyC2STransferOutValue() > 0) {
				userTransferCountsVO.setDailyC2STransferOutValue(userTransferCountsVO.getDailyC2STransferOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getWeeklyC2STransferOutCount() > 0) {
				userTransferCountsVO.setWeeklyC2STransferOutCount(userTransferCountsVO.getWeeklyC2STransferOutCount() - p_quantityRequired);
			}
			if (userTransferCountsVO.getWeeklyC2STransferOutValue() > 0) {
				userTransferCountsVO.setWeeklyC2STransferOutValue(userTransferCountsVO.getWeeklyC2STransferOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getMonthlyC2STransferOutCount() > 0) {
				userTransferCountsVO.setMonthlyC2STransferOutCount(userTransferCountsVO.getMonthlyC2STransferOutCount() - p_quantityRequired);
			}
			if (userTransferCountsVO.getMonthlyC2STransferOutValue() > 0) {
				userTransferCountsVO.setMonthlyC2STransferOutValue(userTransferCountsVO.getMonthlyC2STransferOutValue() - c2sTransferVO.getRequestedAmount());
			}
			userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
		} else {
			userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
			if (userTransferCountsVO.getDailyOutCount() > 0) {
				userTransferCountsVO.setDailyOutCount(userTransferCountsVO.getDailyOutCount() - p_quantityRequired);
			}
			if (userTransferCountsVO.getDailyOutValue() > 0) {
				userTransferCountsVO.setDailyOutValue(userTransferCountsVO.getDailyOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getWeeklyOutCount() > 0) {
				userTransferCountsVO.setWeeklyOutCount(userTransferCountsVO.getWeeklyOutCount() - p_quantityRequired);
			}
			if (userTransferCountsVO.getWeeklyOutValue() > 0) {
				userTransferCountsVO.setWeeklyOutValue(userTransferCountsVO.getWeeklyOutValue() - c2sTransferVO.getRequestedAmount());
			}
			if (userTransferCountsVO.getMonthlyOutCount() > 0) {
				userTransferCountsVO.setMonthlyOutCount(userTransferCountsVO.getMonthlyOutCount() - p_quantityRequired);
			}
			if (userTransferCountsVO.getMonthlyOutValue() > 0) {
				userTransferCountsVO.setMonthlyOutValue(userTransferCountsVO.getMonthlyOutValue() - c2sTransferVO.getRequestedAmount());
			}
			userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method that will call Data access object class to update the Channel to
	 * subscriber transfer details in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_transferIdList
	 * @throws BTSLBaseException
	 */
	public static void updateC2STransferDetails(Connection con, C2STransferVO c2sTransferVO, List<String> p_transferIdList) throws BTSLBaseException {
		final String methodName = "updateC2STransferDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with p_transferIdList size = " + p_transferIdList.size());
		}
		try {
			int listCount = p_transferIdList.size();
			C2STransferDAO c2sTransferDAO =new C2STransferDAO();
			for(int i =0 ;i < listCount ;i ++){
				final int updateCount = c2sTransferDAO.updateC2STransferDetails(con, c2sTransferVO, p_transferIdList.get(i));
				if (updateCount <= 0) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
				}
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[updateC2STransferDetails]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception :" + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method to get the card group set id from the transfer rules on the basis
	 * of receiver service class id
	 * * @param con
	 * 
	 * @param p_transferID
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static void getCardGroupSetIdFromTransferRule(Connection con, String p_receiverServiceClassId, String p_enquiryServiceType, TransferVO p_transferVO, RequestVO requestVO, ChannelUserVO pchannelUserVO) throws BTSLBaseException {
		final String methodName = "getCardGroupSetIdFromTransferRule";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered p_receiverServiceClassId = ");
			loggerValue.append(p_receiverServiceClassId);
			loggerValue.append(", p_enquiryServiceType = ");
			loggerValue.append(p_enquiryServiceType);
			log.debug(methodName, loggerValue );
		}
		TransferVO transferVO = null;
		TransferDAO transferDAO = null;
		try {
			transferDAO = new TransferDAO();
			transferVO = transferDAO.loadCardGroupSetIdFromTransferRule(con, requestVO.getReceiverServiceClassId(), requestVO.getEnquiryServiceType(), requestVO
					.getRequestNetworkCode(), pchannelUserVO.getCategoryVO().getDomainCodeforCategory(), p_transferVO.getSubService());
			p_transferVO.setCardGroupSetID(transferVO.getCardGroupSetID());
			p_transferVO.setNetworkCode(transferVO.getNetworkCode());
		} catch (BTSLBaseException be) {
			loggerValue.setLength(0);
			loggerValue.append("BTSLBase Exception ");
			loggerValue.append(be.getMessage());
			log.error(methodName,  loggerValue );
			throw be;
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception ");
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue);
			log.errorTrace(methodName, e);
			// EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserBL[getCardGroupSetIdFromTransferRule]",p_receiverAllServiceClassId,p_serviceType,"Exception:"+e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.CARD_GROUP_SET_IDNOT_FOUND);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting p_receiverAllServiceClassId : ");
			loggerValue.append(p_receiverServiceClassId);
			log.debug(methodName,  loggerValue );
		}
	}

	/**
	 * Prepare the SMS message which we have to send the receiver user as SMS
	 * 
	 * @param con
	 *            Connection
	 * @param channelTransferVO
	 * @param p_txnSubKey
	 *            String
	 * @param p_balSubKey
	 *            String
	 * @return Object[] at the index 0 txnSMSList and at index 1 balSMSList.
	 * @throws BTSLBaseException
	 */
	public static Object[] prepareSMSMessageListForReceiverForC2C(Connection con, ChannelTransferVO channelTransferVO, String p_txnSubKey, String p_balSubKey) throws BTSLBaseException {
		final String methodName = "prepareSMSMessageListForReceiverForC2C";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered channelTransferVO = ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", p_txnSubKey = " );
			loggerValue.append(p_txnSubKey);
			loggerValue.append(", p_balSubKey = ");
			loggerValue.append(p_balSubKey);
			log.debug(methodName,  loggerValue);
		}
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		final ArrayList txnSmsMessageList = new ArrayList();
		final ArrayList balSmsMessageList = new ArrayList();
		KeyArgumentVO keyArgumentVO = null;
		String argsArr[] = null;
		final ArrayList productList = channelTransferVO.getChannelTransferitemsVOList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		for (int i = 0, k = productList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
			keyArgumentVO = new KeyArgumentVO();
			argsArr = new String[2];
			argsArr[1] = channelTransferItemsVO.getRequestedQuantity();
			argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
			keyArgumentVO.setKey(p_txnSubKey);
			keyArgumentVO.setArguments(argsArr);
			txnSmsMessageList.add(keyArgumentVO);

			argsArr = new String[2];
			long preBalance=0;
			if(!userProductMultipleWallet){
				preBalance = channelTransferItemsVO.getPreviousBalance();
			}
			else{
				preBalance = channelTransferItemsVO.getTotalReceiverBalance();
			}

			// changed for mali-- Transfer (req qty+comm), return and
			// withdraw(req qty) only.
			final long transferedQuantity = channelTransferItemsVO.getReceiverCreditQty();
			String currentBalance = null;

			currentBalance = PretupsBL.getDisplayAmount(preBalance + transferedQuantity);

			argsArr[1] = currentBalance;
			argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
			keyArgumentVO = new KeyArgumentVO();
			keyArgumentVO.setKey(p_balSubKey);
			keyArgumentVO.setArguments(argsArr);
			balSmsMessageList.add(keyArgumentVO);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited  txnSmsMessageList.size() = ");
			loggerValue.append(txnSmsMessageList.size());
			loggerValue.append(", balSmsMessageList.size() = ");
			loggerValue.append(balSmsMessageList.size());
			log.debug(methodName,  loggerValue);
		}

		return (new Object[] { txnSmsMessageList, balSmsMessageList });
	}

	/**
	 * This method is used to get redemption Id for redemptions of activation
	 * bonus points.
	 * 
	 * @author chetan.kothari
	 * @param con
	 *            Connection
	 * @param p_processId
	 *            String
	 * @throws BTSLBaseException
	 * @return int
	 */
	public static synchronized String getRedemptionId() throws BTSLBaseException {
		final String methodName = "getRedemptionId";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered : ");
		}
		String redemptionId = null;
		final SimpleDateFormat date = new SimpleDateFormat("yyMMdd");
		final SimpleDateFormat time = new SimpleDateFormat("HHmm");
		final Date currentDate = new Date();
		final String minut2Compare = _sdfCompare.format(currentDate);
		final int currentMinut = Integer.parseInt(minut2Compare);

		if (currentMinut != _prevMinut) {
			_transactionIDCounter = 1;
			_prevMinut = currentMinut;
		} else if (_transactionIDCounter > MAX_COUNTER) {
			_transactionIDCounter = 1;
		} else {
			_transactionIDCounter++;
		}
		final String transactionId = BTSLUtil.padZeroesToLeft(String.valueOf(_transactionIDCounter), 4);
		try {
			redemptionId = "RD" + date.format(currentDate) + "." + time.format(currentDate) + "." + transactionId;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			log.error(methodName, "Exception = " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ActivationBonusRedemption[markProcessStatusAsComplete]", "", "", "",  loggerValue.toString() );
			throw new BTSLBaseException("ActivationBonusRedemption", methodName, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
		} finally {
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: redemptionId = ");
				loggerValue.append(redemptionId);
				log.debug(methodName,  loggerValue );
			}
		} // end of finally
		return redemptionId;
	}

	/**
	 * It Preapre the network stock from channel transfer vo and debit the
	 * network stock
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param userID
	 * @param p_modifiedDate
	 * @param p_isDebit
	 *            boolean
	 * @return int
	 * @throws BTSLBaseException
	 */
	public static int prepareNetworkStockListAndCreditDebitStockForCommision(Connection con, ChannelTransferVO channelTransferVO, String userID, Date p_modifiedDate, boolean p_isDebit) throws BTSLBaseException {
		final String methodName = "prepareNetworkStockListAndCreditDebitStockForCommision";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", User ID = ");
			loggerValue.append(userID);
			loggerValue.append(", Date = ");
			loggerValue.append(p_modifiedDate);
			loggerValue.append(", isDebit = ");
			loggerValue.append(p_isDebit);
			log.debug(methodName,loggerValue );
		}

		int updateCount = 0;
		boolean multipleWalletApply = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
		final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
		final ArrayList networkStockList = new ArrayList();
		final ArrayList itemList = channelTransferVO.getChannelTransferitemsVOList();
		ChannelTransferItemsVO itemsVO = null;
		NetworkStockVO networkStocksVO = null;
		for (int i = 0, k = itemList.size(); i < k; i++) {
			networkStocksVO = new NetworkStockVO();
			itemsVO = (ChannelTransferItemsVO) itemList.get(i);
			networkStocksVO.setProductCode(itemsVO.getProductCode());
			networkStocksVO.setNetworkCode(channelTransferVO.getNetworkCode());
			networkStocksVO.setNetworkCodeFor(channelTransferVO.getNetworkCodeFor());
			networkStocksVO.setLastTxnNum(channelTransferVO.getTransferID());
			// commision quantity is already converted in system amount
			networkStocksVO.setLastTxnBalance(itemsVO.getCommQuantity());
			networkStocksVO.setWalletBalance(itemsVO.getCommQuantity());
			networkStocksVO.setLastTxnType(channelTransferVO.getTransferType());
			networkStocksVO.setModifiedBy(userID);
			networkStocksVO.setModifiedOn(p_modifiedDate);
			if (multipleWalletApply) {
				networkStocksVO.setWalletType(PretupsI.INCENTIVE_WALLET_TYPE);
			} else {
				networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
			}
			networkStockList.add(networkStocksVO);
		}

		updateCount = networkStockDAO.updateNetworkDailyStock(con, networkStocksVO);

		if (p_isDebit) {
			updateCount = networkStockDAO.debitNetworkStock(con, networkStockList);
		} else {
			updateCount = networkStockDAO.creditNetworkStock(con, networkStockList);
		}
		/*
		 * Set the network previous stock on channel transfer items vo
		 */
		for (int i = 0, k = networkStockList.size(); i < k; i++) {
			networkStocksVO = (NetworkStockVO) networkStockList.get(i);
			itemsVO = (ChannelTransferItemsVO) itemList.get(i);
			/**
			 * Note:
			 * getting the sender items previous stock by adding ordered n/w
			 * stock with n/w previous stock.
			 * 
			 * Debit case: PreviousN/WSTOCK = totalstcok - requested stock (
			 * refer debit methgod of NetworkStockDao )
			 * Credit Case: PreviousN/WSTOCK = totalstcok ( refer credit method
			 * of NetworkStockDao )
			 * 
			 */
			if (p_isDebit) {
				itemsVO.setAfterTransCommisionSenderPreviousStock(networkStocksVO.getPreviousBalance() + networkStocksVO.getWalletbalance());
				itemsVO.setSenderPreviousStock(networkStocksVO.getPreviousBalance() + networkStocksVO.getWalletbalance());
			}
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited updateCount = ");
			loggerValue.append(updateCount);
			log.debug(methodName,  loggerValue );
		}
		return updateCount;
	}

	/**
	 * update the Network Stock Transaction details
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param userID
	 * @param curDate
	 * @return int
	 * @throws BTSLBaseException
	 */
	public static int updateNetworkStockTransactionDetailsForCommision(Connection con, ChannelTransferVO channelTransferVO, String userID, Date curDate) throws BTSLBaseException {
		final String methodName = "updateNetworkStockTransactionDetailsForCommision";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			loggerValue.append(", USER ID = ");
			loggerValue.append(userID);
			loggerValue.append(", Curdate = ");
			loggerValue.append(curDate);
			log.debug(methodName, loggerValue);
		}
		int updateCount = 0;

		final NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
		networkStockTxnVO.setNetworkCode(channelTransferVO.getNetworkCode());
		networkStockTxnVO.setNetworkFor(channelTransferVO.getNetworkCodeFor());
		if (channelTransferVO.getNetworkCode().equals(channelTransferVO.getNetworkCodeFor())) {
			networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
		} else {
			networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
		}
		networkStockTxnVO.setCreatedOn(curDate);
		networkStockTxnVO.setReferenceNo(channelTransferVO.getTransferID());
		networkStockTxnVO.setTxnDate(curDate);
		networkStockTxnVO.setCreatedBy(userID);
		networkStockTxnVO.setTax3value(channelTransferVO.getTotalTax3());
		networkStockTxnVO.setModifiedOn(curDate);
		networkStockTxnVO.setModifiedBy(userID);
		networkStockTxnVO.setTxnStatus(channelTransferVO.getStatus());
		networkStockTxnVO.setTxnNo(NetworkStockBL.genrateStockTransctionID(con, networkStockTxnVO));

		// new field for comision txn id
		channelTransferVO.setCommisionTxnId(networkStockTxnVO.getTxnNo());

		if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType()) || PretupsI.C2C_MODULE.equalsIgnoreCase(channelTransferVO.getTransferType())) {
			networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
			networkStockTxnVO.setTxnType(PretupsI.DEBIT);
		} else if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(channelTransferVO.getTransferType())) {
			networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_RETURN);
			networkStockTxnVO.setTxnType(PretupsI.CREDIT);
		}


		networkStockTxnVO.setInitiatedBy(userID);
		networkStockTxnVO.setUserID(channelTransferVO.getFromUserID());

		final ArrayList list = channelTransferVO.getChannelTransferitemsVOList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkStockTxnItemsVO networkItemsVO = null;

		final ArrayList arrayList = new ArrayList();
		int j = 1;
		for (int i = 0, k = list.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) list.get(i);

			networkItemsVO = new NetworkStockTxnItemsVO();
			networkItemsVO.setSNo(j++);
			networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
			networkItemsVO.setRequiredQuantity(channelTransferItemsVO.getCommQuantity());
			networkItemsVO.setApprovedQuantity(channelTransferItemsVO.getCommQuantity());
			networkItemsVO.setMrp(channelTransferItemsVO.getCommQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));
			networkItemsVO.setAmount(channelTransferItemsVO.getCommQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));

			// Added becoz in case of c2c we did not get the commision quantity
			// from channelTransferVO
			networkStockTxnVO.setRequestedQuantity(channelTransferItemsVO.getCommQuantity());
			networkStockTxnVO.setApprovedQuantity(channelTransferItemsVO.getCommQuantity());
			networkStockTxnVO.setTxnMrp(channelTransferItemsVO.getCommQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));
			networkItemsVO.setDateTime(curDate);

			if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
				networkItemsVO.setStock(channelTransferItemsVO.getAfterTransCommisionSenderPreviousStock());
			}
			networkItemsVO.setProductCode(channelTransferItemsVO.getProductCode());
			arrayList.add(networkItemsVO);
		}
		networkStockTxnVO.setNetworkStockTxnItemsList(arrayList);
		boolean MULTIPLE_WALLET_APPLY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY))).booleanValue();
		if (MULTIPLE_WALLET_APPLY) {
			networkStockTxnVO.setTxnWallet(PretupsI.INCENTIVE_WALLET_TYPE);
		} else {
			networkStockTxnVO.setTxnWallet(PretupsI.SALE_WALLET_TYPE);
		}
		final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
		// call the dao to update the newtork stock transaction
		updateCount = networkStockDAO.addNetworkStockTransaction(con, networkStockTxnVO);
		// Logger for network stock tranasction

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited updateCount = ");
			loggerValue.append(updateCount);
			log.debug(methodName,  loggerValue);
		}

		return updateCount;
	}

	/**
	 * Method prepareC2CNetworkTxnCommisionBalanceLogger.
	 * 
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 */
	private static void prepareNetworkTxnCommisionBalanceLogger(ChannelTransferVO channelTransferVO) {
		final String methodName = "prepareNetworkTxnCommisionBalanceLogger";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO = ");
			loggerValue.append(channelTransferVO);
			log.debug(methodName,  loggerValue );
		}

		NetworkStockTxnVO networkStockTxnVO = null;
		final ArrayList itemList = channelTransferVO.getChannelTransferitemsVOList();
		ChannelTransferItemsVO itemsVO = null;
		final Date curdate = new Date();
		NetworkStockTxnVO networkStockTxnVoOnj = new NetworkStockTxnVO();
		for (int j = 0; j < itemList.size(); j++) {
			itemsVO = (ChannelTransferItemsVO) itemList.get(j);
			networkStockTxnVO = networkStockTxnVoOnj;
			networkStockTxnVO.setRequestedQuantity(itemsVO.getCommQuantity());
			networkStockTxnVO.setApprovedQuantity(itemsVO.getCommQuantity());
			networkStockTxnVO.setTxnMrp(itemsVO.getCommQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(itemsVO.getUnitValue())));
			networkStockTxnVO.setProductCode(itemsVO.getProductCode());
			networkStockTxnVO.setTxnType(PretupsI.DEBIT);
			networkStockTxnVO.setPreviousStock(itemsVO.getAfterTransCommisionSenderPreviousStock());
			networkStockTxnVO.setPostStock(itemsVO.getAfterTransCommisionSenderPreviousStock() - itemsVO.getCommQuantity());

		}
		networkStockTxnVO.setNetworkCode(channelTransferVO.getNetworkCode());
		networkStockTxnVO.setNetworkFor(channelTransferVO.getNetworkCodeFor());
		networkStockTxnVO.setReferenceNo(channelTransferVO.getTransferID());
		networkStockTxnVO.setTxnNo(channelTransferVO.getCommisionTxnId());
		networkStockTxnVO.setStockType(channelTransferVO.getType());
		networkStockTxnVO.setTxnDate(channelTransferVO.getModifiedOn());
		networkStockTxnVO.setCreatedBy(channelTransferVO.getFromUserID());
		networkStockTxnVO.setUserID(channelTransferVO.getFromUserID());
		networkStockTxnVO.setCreatedOn(curdate);
		networkStockTxnVO.setEntryType(channelTransferVO.getTransferSubType());
		networkStockTxnVO.setTax3value(channelTransferVO.getTotalTax3());
		networkStockTxnVO.setTxnStatus(channelTransferVO.getStatus());
		networkStockTxnVO.setTxnCategory(channelTransferVO.getTransferCategory());
		networkStockTxnVO.setOtherInfo("COMMISSION PROFILE ID = " + channelTransferVO.getCommProfileSetId() + ", COMMISSION PROFILE VERSION = " + channelTransferVO
				.getCommProfileVersion() + ", TXN TYPE = " + PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
		NetworkStockLog.log(networkStockTxnVO);
	}

	/**
	 * Method calculateTotalMRPFromTaxAndDiscount()
	 * To calculate the MRP on the selected product list
	 * 
	 * @param p_transferItemsList
	 * @param p_txnType
	 *            String
	 * @param p_approvalLevel
	 *            String
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 * @throws Exception
	 */
	public static void calculateTotalMRPFromTaxAndDiscount(ArrayList p_transferItemsList, String p_txnType, String p_approvalLevel, ChannelTransferVO channelTransferVO) throws Exception {
		final String methodName = "calculateTotalMRPFromTaxAndDiscount";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered  p_transferItemsList.size = ");
			loggerValue.append(p_transferItemsList.size());
			loggerValue.append(", p_txnType = ");
			loggerValue.append(p_txnType );
			loggerValue.append(", p_approvalLevel = ");
			loggerValue.append(p_approvalLevel);
			loggerValue.append(", channelTransferVO = ");
			loggerValue.append(channelTransferVO);
			log.debug(methodName,loggerValue);
		}
		ChannelTransferItemsVO channelTransferItemsVO = null;
		OtfProfileVO  otfDetailVO = new OtfProfileVO();
		UserTransferCountsDAO userTransferCountsDAO= new UserTransferCountsDAO();
		long productCost = 0;
		long value = 0;
		UserOTFCountsVO userOTFCountsVO;
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		List<OTFDetailsVO> otfSlabList;
		OTFDetailsVO otfDetailsVO = null;
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		boolean othComChnl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
		String dualWalletCommissionCal = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DW_COMMISSION_CAL);
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
		Boolean order= true;
		Boolean addnl=false;
		DateFormat df = new SimpleDateFormat(PretupsI.DATE_FORMAT);
		Date dateobj = new Date();
		Date tempDate = df.parse(df.format(dateobj));
		double quantity = 0;
		Connection con = null;
		MComConnectionI mcomCon = null;
		try
		{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			for (int i = 0, k = p_transferItemsList.size(); i < k; i++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) p_transferItemsList.get(i);
				if (p_approvalLevel.equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1)) {
					if (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity()) && !channelTransferItemsVO.getFirstApprovedQuantity().equalsIgnoreCase("0")) {
						quantity = Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity());
					} else {
						quantity = Double.parseDouble(channelTransferItemsVO.getRequestedQuantity());
						channelTransferItemsVO.setFirstApprovedQuantity(String.valueOf(quantity));
					}
				} else if (p_approvalLevel.equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2)) {
					if (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity()) && !channelTransferItemsVO.getSecondApprovedQuantity().equalsIgnoreCase("0")) {
						quantity = Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity());
					} else {
						quantity = Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity());
						channelTransferItemsVO.setSecondApprovedQuantity(String.valueOf(quantity));
					}
				} else if (p_approvalLevel.equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3)) {
					if (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity()) && !channelTransferItemsVO.getThirdApprovedQuantity().equalsIgnoreCase("0")) {
						quantity = Double.parseDouble(channelTransferItemsVO.getThirdApprovedQuantity());
					} else {
						quantity = Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity());
						channelTransferItemsVO.setThirdApprovedQuantity(String.valueOf(quantity));
					}
				}

				productCost = Double.valueOf((quantity * channelTransferItemsVO.getUnitValue())).longValue();

				if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && channelTransferVO.isOtfFlag())
				{
					if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
					{
						otfDetailVO = commissionProfileTxnDAO.loadOtfProfileDetails(con, channelTransferVO.getCommProfileSetId(), channelTransferVO.getCommProfileVersion(), channelTransferItemsVO.getProductCode());
						boolean timeFlag=true;
						if(otfDetailVO!=null){
						if(!(otfDetailVO.getOtfApplicableFromDate()== null && otfDetailVO.getOtfApplicableToDate()==null))
						{
							if(!((otfDetailVO.getOtfApplicableFromDate().before(tempDate) 
									|| otfDetailVO.getOtfApplicableFromDate().equals(tempDate)) && (otfDetailVO.getOtfApplicableToDate().after(tempDate) 
											|| otfDetailVO.getOtfApplicableToDate().equals(tempDate))))
							{
								timeFlag = false;
							}
						}

						if(otfDetailVO.getOtfTimeSlab()!=null 
								&& !BTSLUtil.timeRangeValidation(otfDetailVO.getOtfTimeSlab(), new Date()))
							timeFlag=false;
						boolean otfFlag=true;
						if(otfDetailVO.getOtfApplicableFrom()== null && otfDetailVO.getOtfApplicableTo()==null && timeFlag){
							otfFlag=false;
						}
						
						if(timeFlag && otfFlag)
						{
							otfSlabList= commissionProfileDAO.getBaseCommOtfDetails(con, otfDetailVO.getCommProfileOtfID(), "COMM", order);
							userOTFCountsVO = userTransferCountsDAO.loadUserOTFCounts(con, channelTransferVO.getToUserID(),otfDetailVO.getCommProfileOtfID(), addnl);
							for (OTFDetailsVO otfSlabVO : otfSlabList)
							{
								if(userOTFCountsVO!=null)
								{
									if (userOTFCountsVO.getOtfValue() >= otfSlabVO.getOtfValueLong())
										otfDetailsVO =  otfSlabVO;
									else
										break;
								}
							}
							if(otfDetailsVO!=null)
							{
								channelTransferItemsVO.setRequiredQuantity(productCost);
								calculateOTFforOPT(channelTransferItemsVO, otfDetailsVO);
							}
						}
					}
					}
				}
				
				if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {

					value = calculatorI.calculateCommission(channelTransferItemsVO.getCommType(), channelTransferItemsVO.getCommRate(), productCost);

				}
				channelTransferItemsVO.setCommValue(value);
				if (!BTSLUtil.isNullString(channelTransferItemsVO.getDiscountType())) {
					value = calculatorI.calculateDiscount(channelTransferItemsVO.getDiscountType(), channelTransferItemsVO.getDiscountRate(), productCost);
					channelTransferItemsVO.setDiscountValue(value);
					value = 0;
				}
				if(othComChnl){
				value = calculatorI.calculateCommission(channelTransferItemsVO.getOthCommType(),channelTransferItemsVO.getOthCommRate(),productCost);
	            channelTransferItemsVO.setOthCommValue(value);
        	    value = 0;
				}
				if(othComChnl) {
					if(channelTransferItemsVO.isOthSlabDefine()) {
						if(PretupsI.DW_COMMISSION_CAL_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)) {
							value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getOthCommValue(),channelTransferItemsVO.getUnitValue(),channelTransferItemsVO.getTax3Value());
						} else if(PretupsI.DW_COMMISSION_CAL_BASE_OTH_COMMISSION.equalsIgnoreCase(dualWalletCommissionCal)){
							value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getCommValue()+channelTransferItemsVO.getOthCommValue(),channelTransferItemsVO.getUnitValue(),channelTransferItemsVO.getTax3Value());
						}
					} else {
						value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getCommValue(),channelTransferItemsVO.getUnitValue(),channelTransferItemsVO.getTax3Value());
					}
				} else {
					value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getCommValue(), channelTransferItemsVO.getUnitValue(), channelTransferItemsVO
						.getTax3Value());
				}
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{ 
					value= value+ channelTransferItemsVO.getOtfAmount();

				}
				channelTransferItemsVO.setCommQuantity(value);
				long payableAmount = 0, netPayableAmount = 0;
				if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && 
						!(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferSubType()) ||
						PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equalsIgnoreCase(channelTransferVO.getTransferSubType()))) {
					channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(quantity));
					channelTransferItemsVO.setReceiverCreditQty(PretupsBL.getSystemAmount(quantity));
					channelTransferItemsVO.setProductTotalMRP(productCost);
					channelTransferItemsVO.setPayableAmount(productCost);
					channelTransferItemsVO.setNetPayableAmount(productCost);
				}// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
				// true
				else if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && !p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && 
						(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferSubType()) || 
						PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equalsIgnoreCase(channelTransferVO.getTransferSubType()))) {
					value = 0;
					value = calculatorI.calculateReceiverCreditQuantity(String.valueOf(quantity), channelTransferItemsVO.getUnitValue(), channelTransferItemsVO.getCommQuantity());
					channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(quantity));
					channelTransferItemsVO.setReceiverCreditQty(value);
					channelTransferItemsVO.setPayableAmount(productCost);
					channelTransferItemsVO.setNetPayableAmount(productCost);
					channelTransferItemsVO
					.setProductTotalMRP(channelTransferItemsVO.getReceiverCreditQty() * channelTransferItemsVO.getUnitValue() / (amountMultFactor));
				}// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
				// is false
				else if (!(PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) && !p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
					if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
					{ 
						if(othComChnl)
						payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), quantity, channelTransferItemsVO.getCommValue() + channelTransferItemsVO.getOtfAmount() + channelTransferItemsVO.getOthCommValue(),channelTransferItemsVO.getDiscountValue());
						else
						payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), quantity, channelTransferItemsVO.getCommValue() + channelTransferItemsVO.getOtfAmount(),channelTransferItemsVO.getDiscountValue());
					}
					else
					{
					if(othComChnl)
					payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), quantity, channelTransferItemsVO.getCommValue() + channelTransferItemsVO.getOthCommValue(),channelTransferItemsVO.getDiscountValue());
					else
					payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), quantity, channelTransferItemsVO.getCommValue(),
							channelTransferItemsVO.getDiscountValue());
					}
					netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, channelTransferItemsVO.getTax3Value());
					channelTransferItemsVO.setPayableAmount(payableAmount);
					channelTransferItemsVO.setNetPayableAmount(netPayableAmount);
					channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(quantity));
					channelTransferItemsVO.setReceiverCreditQty(PretupsBL.getSystemAmount(quantity));
					channelTransferItemsVO.setProductTotalMRP(productCost);
				} else {
					channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(quantity));
					channelTransferItemsVO.setReceiverCreditQty(PretupsBL.getSystemAmount(quantity));
					channelTransferItemsVO.setProductTotalMRP(productCost);
					channelTransferItemsVO.setPayableAmount(payableAmount);
					channelTransferItemsVO.setNetPayableAmount(netPayableAmount);
				}

				channelTransferItemsVO.setApprovedQuantity(PretupsBL.getSystemAmount(quantity));
			}
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("ChannelTransferBL#calculateTotalMRPFromTaxAndDiscount");
				mcomCon = null;
			}
		}


		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Genrate channel to channel reverse trn id
	 * 
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	public static void genrateChnnlToChnnlReversalTrx(ChannelTransferVO channelTransferVO) throws BTSLBaseException {

		final String methodName = "genrateChnnlToChnnlReversalTrx";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ChannelTransferVO = " + channelTransferVO);
		}

		try {

			channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.TRANSFER_TYPE_REVERSE_ID_TYPE, IDGenerator.getNextID(
					PretupsI.TRANSFER_TYPE_REVERSE_ID_TYPE, BTSLUtil.getFinancialYear(), channelTransferVO)));
		} catch (Exception e) {
			log.error(methodName, "Exception = " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}

	}

	/**
	 * Method calculateMRPWithTaxAndDiscount()
	 * To calculate the taxes on the selected product list
	 * 
	 * @param p_transferItemsList
	 * @param p_txnType
	 *            String
	 * @added for direct payout
	 * @throws Exception
	 */
	public static void calculateMRPWithTaxAndDiscount(ArrayList p_transferItemsList, String p_txnType) throws Exception {
		final String methodName = "calculateMRPWithTaxAndDiscount";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered  p_transferItemsList.size = " + p_transferItemsList.size() + ", p_txnType = " + p_txnType);
		}
		ChannelTransferItemsVO channelTransferItemsVO = null;
		long productCost = 0, value = 0;
		for (int i = 0, k = p_transferItemsList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) p_transferItemsList.get(i);

			productCost = Double.valueOf((Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue())).longValue();
			// In case of FOC Commission will not be calculated
			if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
				value = calculatorI.calculateCommission(channelTransferItemsVO.getCommType(), channelTransferItemsVO.getCommRate(), productCost);
			}
			channelTransferItemsVO.setCommValue(value);
			value = 0;
			if(channelTransferItemsVO.getDiscountType() != null)
			{
				value = calculatorI.calculateDiscount(channelTransferItemsVO.getDiscountType(), channelTransferItemsVO.getDiscountRate(), productCost);
				channelTransferItemsVO.setDiscountValue(value);
				value = 0;
			}
			/*
			 * To check whether tax is applicable for transaction or not.We have
			 * the following three type of the
			 * transaction and following corresponding check condition.
			 * 1. In case of O2C transfer Tax calculation is mandatory.
			 * 2. In case of C2C transfer Tax calculation is based on the
			 * TAX_APPLICABLE_ON_C2C flag of the
			 * commission profile.
			 * 3. In case of FOC transfer Tax calculation is based on the
			 * TAX_APPLICABLE_ON_FOC flag of the
			 * commission profile.
			 */
			if (p_txnType.equals(PretupsI.TRANSFER_TYPE_O2C) || (p_txnType.equals(PretupsI.TRANSFER_TYPE_C2C) && PretupsI.YES.equals(channelTransferItemsVO
					.getTaxOnChannelTransfer())) || (p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && PretupsI.YES.equals(channelTransferItemsVO.getTaxOnFOCTransfer()))) {
				value = calculatorI.calculateTax1(channelTransferItemsVO.getTax1Type(), channelTransferItemsVO.getTax1Rate(), productCost);
				channelTransferItemsVO.setTax1Value(value);
				value = 0;
				value = calculatorI.calculateTax2(channelTransferItemsVO.getTax2Type(), channelTransferItemsVO.getTax2Rate(), channelTransferItemsVO.getTax1Value());
				channelTransferItemsVO.setTax2Value(value);
				value = 0;
				// In case of FOC Tax3 will not be calculated
				if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
					value = calculatorI.calculateTax3(channelTransferItemsVO.getTax3Type(), channelTransferItemsVO.getTax3Rate(), channelTransferItemsVO.getCommValue());
				}
				channelTransferItemsVO.setTax3Value(value);
				value = 0;
			}

			long payableAmount = 0;
			// In case of FOC payableAmount will not be calculated
			if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
				payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()),
						channelTransferItemsVO.getCommValue(), channelTransferItemsVO.getDiscountValue());
			}
			long netPayableAmount = 0;
			// In case of FOC netPayableAmount will not be calculated
			if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
				netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, channelTransferItemsVO.getTax3Value());
			}
			channelTransferItemsVO.setPayableAmount(payableAmount);
			channelTransferItemsVO.setNetPayableAmount(netPayableAmount);
			channelTransferItemsVO.setProductTotalMRP(productCost);
			channelTransferItemsVO.setApprovedQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}




	/**
	 * Method generateSaleBatchNumberForMVD.
	 * This method generates the sale batch number for the list of vouchers
	 * downloaded through bulk voucher distribution
	 * Format of it is described in the comman file where all the IDs are
	 * formatting.
	 * 
	 * @param p_transferVO
	 *            C2STransferVO
	 * @throws BTSLBaseException
	 */
	public static String generateSaleBatchNumberForMVD(C2STransferVO p_transferVO) throws BTSLBaseException {
		final String methodName = "generateSaleBatchNumberForMVD";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered p_transferVO = " + p_transferVO);
		}
		String tempSaleBatchNumber = null;
		try {
			return tempSaleBatchNumber = calculatorI.formatSaleBatchNumber(PretupsI.SALE_BATCH_NUMBER, IDGenerator.getNextID(PretupsI.SALE_BATCH_NUMBER, BTSLUtil
					.getFinancialYear(), p_transferVO.getNetworkCode(), p_transferVO.getCreatedOn()));
		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + tempSaleBatchNumber);
			}
		}

	}

	/**
	 * 
	 * @param con
	 * @param prdCodeQtyMap
	 * @param pchannelUserVO
	 * @param isTransfer
	 * @return
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadAndValidateProducts(Connection con, HashMap prdCodeQtyMap, ChannelUserVO pchannelUserVO, boolean isTransfer) throws BTSLBaseException {
		final String methodName = "LoadAndValidateProducts";

       StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entering : prdCodeQtyMap : ");
			loggerValue.append(prdCodeQtyMap);
			loggerValue.append(", pchannelUserVO : ");
			loggerValue.append(pchannelUserVO);
			loggerValue.append(", isTransfer = ");
			loggerValue.append(isTransfer);
			log.debug(methodName,  loggerValue );
		}

		boolean firstCheckForPrdType = true; // for setting of product type once
		String prdType = null;
		ArrayList productListFromDao = null;
		final ArrayList tmpPrdList = new ArrayList();
		ArrayList filteredProductList = new ArrayList();
		ArrayList commPrdList = null;
		ArrayList trfPrdList = new ArrayList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		final HashMap prdShortCodeMap = new HashMap();

		// filter the products from the n/w's
		// products & the request's products
		final NetworkProductDAO networkDAO = new NetworkProductDAO();
		productListFromDao = networkDAO.loadProductListForXfr(con, null, pchannelUserVO.getNetworkID());

		final Iterator nwItr = (prdCodeQtyMap.keySet()).iterator();
		String mapPrdShortCode = null;
		while (nwItr.hasNext()) {
			mapPrdShortCode = (String) nwItr.next();
			for (int i = 0, j = productListFromDao.size(); i < j; i++) {
				networkProductVO = (NetworkProductVO) productListFromDao.get(i);
				if ((mapPrdShortCode).equals(String.valueOf(networkProductVO.getProductShortCode()))) {
					// for checking whether all the products in the request have
					// the same type
					if (firstCheckForPrdType) {
						prdType = networkProductVO.getProductType();
						firstCheckForPrdType = false;
					}
					// creating and populating the ChannelTransferItemsVO from
					// the NetworkProductVO
					channelTransferItemsVO = new ChannelTransferItemsVO();

					if (!prdType.equals(networkProductVO.getProductType())) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PRODUCT_TYPE_NOT_SAME);
					}

					channelTransferItemsVO.setProductType(networkProductVO.getProductType());
					channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
					channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
					channelTransferItemsVO.setProductName(networkProductVO.getProductName());
					channelTransferItemsVO.setShortName(networkProductVO.getShortName());
					channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
					channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
					channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
					channelTransferItemsVO.setStatus(networkProductVO.getStatus());
					channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
					channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
					channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());

					prdShortCodeMap.put(networkProductVO.getProductCode(), channelTransferItemsVO);
					tmpPrdList.add(channelTransferItemsVO);
					break;
				}
			}
		}

		// check whether after filteration the filtered product list
		// contains the same no. of products as we receive from the request
		if (prdCodeQtyMap.size() != tmpPrdList.size()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NETWORK_PRODUCTS_NOT_MATCHING);
		}

		tmpPrdList.clear();

		final Date currDate = new Date();
		final CommissionProfileDAO commPrDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();

		// load the latest version of commission profile
		final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, pchannelUserVO.getCommissionProfileSetID(), currDate);
        final String commProfileLatestVer = commissionProfileSetVO.getCommProfileVersion();
		if (BTSLUtil.isNullString(commProfileLatestVer)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_LATEST_COMMISSION_PROFILE_ASSOCIATED);
		}

		pchannelUserVO.setCommissionProfileSetVersion(commProfileLatestVer);
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		String type = (TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		commPrdList = commPrDAO.loadCommissionProfileProductsList(con, pchannelUserVO.getCommissionProfileSetID(), pchannelUserVO.getCommissionProfileSetVersion(), type, paymentMode);
		if (commPrdList == null || commPrdList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_COMMISION_PRODUCT_ASSOCIATED);
		}

		// filter the products of commission profile
		// with the products of request
		CommissionProfileProductsVO commProfilePrdVO = null;

		final Iterator mapItr = (prdShortCodeMap.keySet()).iterator();
		String mapPrdCode = null;
		while (mapItr.hasNext()) {
			mapPrdCode = (String) mapItr.next();
			for (int x = 0, z = commPrdList.size(); x < z; x++) {
				commProfilePrdVO = (CommissionProfileProductsVO) commPrdList.get(x);
				final String productCode = commProfilePrdVO.getProductCode();
				if ((mapPrdCode).equals(productCode)) {
					channelTransferItemsVO = (ChannelTransferItemsVO) prdShortCodeMap.get(productCode);
					final long qty = Long.parseLong(prdCodeQtyMap.get(String.valueOf(channelTransferItemsVO.getProductShortCode())).toString());
					;

					final long qty1 = Long.parseLong(prdCodeQtyMap.get(String.valueOf(channelTransferItemsVO.getProductShortCode())).toString());
					if (isTransfer) {

						if (qty1 >= commProfilePrdVO.getMinTransferValue() && qty1 <= commProfilePrdVO.getMaxTransferValue()) {

							final long multiple = commProfilePrdVO.getTransferMultipleOff();
							if ((qty1 % multiple) == 0) {
								channelTransferItemsVO.setRequestedQuantity(String.valueOf(qty));
								tmpPrdList.add(channelTransferItemsVO);
							} else {
								throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_QTY_INVALID);
							}
						} else {
							throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_QTY_INVALID);
						}
					} else {
						channelTransferItemsVO.setRequestedQuantity(String.valueOf(qty));
						tmpPrdList.add(channelTransferItemsVO);
					}
					channelTransferItemsVO.setTaxOnChannelTransfer(commProfilePrdVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commProfilePrdVO.getTaxOnFOCApplicable());
					break;
				}
			}
		}

		if (tmpPrdList.size() != prdCodeQtyMap.size()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_PRODUCTS_NOT_MATCHING);
		}

		filteredProductList = null;
		filteredProductList = new ArrayList(tmpPrdList);
		tmpPrdList.clear();

		// load the transfer profile product list
		final TransferProfileDAO transferPrfDAO = new TransferProfileDAO();
		TransferProfileVO transferProfileVO = new TransferProfileVO();
		transferProfileVO = transferPrfDAO.loadTransferProfileThroughProfileID(con, pchannelUserVO.getTransferProfileID(), pchannelUserVO.getNetworkID(), pchannelUserVO
				.getCategoryCode(), true);
		trfPrdList = transferProfileVO.getProfileProductList();
		if (trfPrdList == null || trfPrdList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_TRANSFERPROFILE_PRODUCT_ASSOCIATED);
		}

		// filter the products of transfer profile
		// with the products of request
		TransferProfileProductVO transferPrfPrdVO = null;

		for (int x = 0, z = filteredProductList.size(); x < z; x++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) filteredProductList.get(x);
			for (int y = 0, i = trfPrdList.size(); y < i; y++) {
				transferPrfPrdVO = (TransferProfileProductVO) trfPrdList.get(y);
				if ((transferPrfPrdVO.getProductCode()).equals(channelTransferItemsVO.getProductCode())) {
					tmpPrdList.add(channelTransferItemsVO);
					break;
				}

			}
		}
		if (tmpPrdList.size() != prdCodeQtyMap.size()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_PRODUCTS_NOT_MATCHING);
		}


		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();

		// the call to the method loads the transfer rule between the Operator
		// and the passed category code for the domain and network.
		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, pchannelUserVO.getNetworkID(), pchannelUserVO.getDomainID(),
				PretupsI.CATEGORY_TYPE_OPT, pchannelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		final ArrayList transferRulePrdList = channelTransferRuleVO.getProductVOList();
		ListValueVO listValueVO = null;

		filteredProductList = null;
		filteredProductList = new ArrayList(tmpPrdList);
		tmpPrdList.clear();

		// filter the products associated with the transfer rule
		// with the products of request
		for (int a = 0, b = filteredProductList.size(); a < b; a++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) filteredProductList.get(a);
			for (int c = 0, d = transferRulePrdList.size(); c < d; c++) {
				listValueVO = (ListValueVO) transferRulePrdList.get(c);
				if ((listValueVO.getValue()).equals(channelTransferItemsVO.getProductCode())) {
					tmpPrdList.add(channelTransferItemsVO);
					break;
				}
			}
		}
		if (tmpPrdList.size() != prdCodeQtyMap.size()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_RULE_PRODUCTS_NOT_MATCHING);
		}

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting : tmpPrdList :: ");
			loggerValue.append(tmpPrdList.size());
			log.debug(methodName,  loggerValue );
		}

		return filteredProductList;
	}

	/**
	 * 
	 * Load the product with taxes, discount and commission
	 * 
	 * @param con
	 * @param p_commissionProfileID
	 *            String
	 * @param p_commissionProfileVersion
	 *            String
	 * @param p_transferItemsList
	 * @param isWeb
	 * @param p_forwardPath
	 * @param p_txnType
	 * @throws BTSLBaseException
	 * @throws ParseException 
	 * @throws SQLException 
	 * @throws Exception
	 */
	public static boolean loadAndCalculateTaxOnProducts(Connection con, String p_commissionProfileID, String p_commissionProfileVersion, ChannelTransferVO channelTransferVO, String p_txnType) throws BTSLBaseException, SQLException, ParseException {
		final String methodName = "loadAndCalculateTaxOnProducts";

        StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered CommissiojnProfileID : " );
			loggerValue.append(p_commissionProfileID);
			loggerValue.append(", p_commissionProfileVersion = ");
			loggerValue.append(p_commissionProfileVersion);
			loggerValue.append(", channelTransferVO : " );
			loggerValue.append(channelTransferVO.toString());
			loggerValue.append(", p_txnType = ");
			loggerValue.append(p_txnType);
			log.debug(methodName,loggerValue);
		}

		// load the tax and commission of the products frrom data bas
		// according to the user commission profile
		final ArrayList transferItemsList = channelTransferVO.getChannelTransferitemsVOList();
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		channelTransferVO.setCommProfileSetId(p_commissionProfileID);
		channelTransferVO.setCommProfileVersion(p_commissionProfileVersion);
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		boolean PAYMENT_MODE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD);
		
		String type = (TRANSACTION_TYPE_ALWD)?channelTransferVO.getType():PretupsI.ALL;
		String paymentMode = (PAYMENT_MODE_ALWD &&  (PretupsI.TRANSFER_TYPE_O2C.equals(type)|| PretupsI.TRANSFER_TYPE_C2C.equals(type)))?channelTransferVO.getPaymentInstType():PretupsI.ALL;
		commissionProfileDAO.loadProductListWithTaxes(con, p_commissionProfileID, p_commissionProfileVersion, transferItemsList, type, paymentMode);

		ChannelTransferItemsVO channelTransferItemsVO = null;
		boolean isSlabFlag = false;
		for (int i = 0, k = transferItemsList.size(); i < k; i++) {

			channelTransferItemsVO = (ChannelTransferItemsVO) transferItemsList.get(i);
			if (channelTransferItemsVO.isSlabDefine()) {
				isSlabFlag = true;
				// to calculate tax

				//2
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{
					ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
				}
				calculateMRPWithTaxAndDiscount(channelTransferVO, p_txnType);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}

		return isSlabFlag;
	}

	public static void genrateDrCrChnnlTrfID(Connection con, ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "genrateDrCrChnnlTrfID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ChannelTransferVO = " + channelTransferVO);
		}
		try {
			final long tmpId = IDGenerator.getNextID(con, PretupsI.DRCR_CHANNEL_USER_ID, BTSLUtil.getFinancialYear(), channelTransferVO);
			channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.DRCR_CHANNEL_USER_ID, tmpId));
		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}
	}

	/**
	 * 
	 * @param con
	 * @param p_productType
	 * @param networkCode
	 * @param p_commProfileSetId
	 * @param currentDate
	 * @param p_forwardPath
	 * @return
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadO2CWdrProductList(Connection con, String p_productType, String networkCode, String p_commProfileSetId, String p_domainCode, String p_categoryCode, Date currentDate, String p_forwardPath) throws BTSLBaseException {
		final String methodName = "loadO2CWdrProductList";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered p_productType : ");
			loggerValue.append(p_productType);
			loggerValue.append(", NetworkCode : " );
			loggerValue.append(networkCode);
			loggerValue.append(", CommissionProfileSetID : ");
			loggerValue.append(p_commProfileSetId);
			loggerValue.append(", CurrentDate : ");
			loggerValue.append(currentDate);
			loggerValue.append(", p_forwardPath = ");
			loggerValue.append(p_forwardPath);
			log.debug(methodName,loggerValue );
		}
		final ArrayList productList = new ArrayList();

		final NetworkProductDAO networkProductDAO = new NetworkProductDAO();

		// load the product list mapped with the network.
		final ArrayList prodList = networkProductDAO.loadProductListForXfr(con, p_productType, networkCode);

		// check whether product exist or not of the input productType
		if (prodList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.producttype", 0, new String[] { p_productType }, p_forwardPath);
		}

		// checking that the status of network product mapping is active and
		// also construct the new arrayList of
		// channelTransferItemsVOs containg required list.
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		int i, j, m, n;
		for (i = 0, j = prodList.size(); i < j; i++) {
			networkProductVO = (NetworkProductVO) prodList.get(i);
			if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
				channelTransferItemsVO = new ChannelTransferItemsVO();
				channelTransferItemsVO.setProductType(networkProductVO.getProductType());
				channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
				channelTransferItemsVO.setProductName(networkProductVO.getProductName());
				channelTransferItemsVO.setShortName(networkProductVO.getShortName());
				channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
				channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
				channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
				channelTransferItemsVO.setStatus(networkProductVO.getStatus());
				channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
				channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
				channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
				productList.add(channelTransferItemsVO);
			}
		}
		if (productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.networkproductmapping", 0, new String[] { p_productType }, p_forwardPath);
		}


		// load the latest version of the commission profile set id
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		String latestCommProfileVersion = null;
		try {
			final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, p_commProfileSetId, currentDate);
			latestCommProfileVersion = commissionProfileSetVO.getCommProfileVersion();
		} catch (BTSLBaseException bex) {
			if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofilever", p_forwardPath);
			}
			log.error(methodName, "BTSLBaseException " + bex.getMessage());
			throw bex;
		}

		// if there is no commission profile version exist upto the current date
		// show the error message.
		if (BTSLUtil.isNullString(latestCommProfileVersion)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofilever", p_forwardPath);
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		String type = (TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(con, p_commProfileSetId, latestCommProfileVersion, type, paymentMode);

		// if list is empty send the error message
		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofileproduct", 0, new String[] { latestCommProfileVersion },
					p_forwardPath);
		}

		// filterize the product list with the products of the commission
		// profile products
		CommissionProfileProductsVO commissionProfileProductsVO = null;
		for (i = 0, j = commissionProfileProductList.size(); i < j; i++) {
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileDetailID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}
			}
		}
		for (m = 0, n = productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
			if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
				productList.remove(m);
				m--;
				n--;
			}
		}

		if (productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.networkcommprofileproduct", p_forwardPath);
		}

		/*
		 * Now further filter the list with the transfer rules list and the
		 * above list
		 * of commission profile products.
		 */
		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
		// load the approval limit of the user

		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, networkCode, p_domainCode, PretupsI.CATEGORY_TYPE_OPT,
				p_categoryCode, PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		final ArrayList transferRulePrdList = channelTransferRuleVO.getProductVOList();
		ListValueVO listValueVO = null;
		final ArrayList tempList = new ArrayList();

		for (int p = 0, q = productList.size(); p < q; p++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(p);
			for (int l = 0, k = transferRulePrdList.size(); l < k; l++) {
				listValueVO = (ListValueVO) transferRulePrdList.get(l);
				if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
					tempList.add(channelTransferItemsVO);
					break;
				}
			}
		}

		/*
		 * This case arises
		 * suppose in transfer rule products A and B are associated
		 * In commission profile product C and D are associated.
		 * We load product with intersection of transfer rule products and
		 * commission profile products.
		 * if no product found then display below message
		 */
		if (tempList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.transferrule.noproductmatch", p_forwardPath);
		}

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with tempList.size = ");
			loggerValue.append(tempList.size());
			log.debug(methodName, loggerValue );
		}
		// return productList;

		return tempList;
	}

	/**
	 * Genrate channel to channel reverse trn id
	 * 
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	public static void genrateO2CReversalTrx(ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "genrateO2CReversalTrx";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ChannelTransferVO = " + channelTransferVO);
		}
		try {
			channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C_REVERSE_ID_TYPE, IDGenerator.getNextID(
					PretupsI.TRANSFER_TYPE_O2C_REVERSE_ID_TYPE, BTSLUtil.getFinancialYear(), channelTransferVO)));
		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_IN_IDS_GENERATE);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + channelTransferVO.getTransferID());
			}
		}

	}

	/**
	 * 
	 * Load the product with taxes, discount and commission
	 * 
	 * @param con
	 * @param p_commissionProfileID
	 *            String
	 * @param p_commissionProfileVersion
	 *            String
	 * @param p_transferItemsList
	 * @param isWeb
	 * @param p_forwardPath
	 * @param p_txnType
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public static void loadAndCalculateTaxOnDenominations(Connection con, String p_commissionProfileID, String p_commissionProfileVersion, ChannelTransferVO channelTransferVO, boolean isWeb, String p_forwardPath, String p_txnType) throws BTSLBaseException, Exception {
		final String methodName = "loadAndCalculateTaxOnDenominations";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered CommissiojnProfileID : ");
			loggerValue.append(p_commissionProfileID);
			loggerValue.append(", p_commissionProfileVersion = ");
			loggerValue.append(p_commissionProfileVersion);
			loggerValue.append(", channelTransferVO : ");
			loggerValue.append(channelTransferVO.toString());
			loggerValue.append(", ForwardPath : ");
			loggerValue.append(p_forwardPath);
			loggerValue.append(", isWeb : ");
			loggerValue.append(isWeb);
			loggerValue.append(", p_txnType = ");
			loggerValue.append(p_txnType);
			log.debug(methodName,loggerValue );
		}

		// load the tax and commission of the products frrom data bas
		// according to the user commission profile
		final ArrayList transferItemsList = channelTransferVO.getChannelTransferitemsVOList();
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		boolean PAYMENT_MODE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD);
		
		String paymentMode = (PAYMENT_MODE_ALWD &&  (PretupsI.TRANSFER_TYPE_O2C.equals(p_txnType) || PretupsI.TRANSFER_TYPE_C2C.equals(p_txnType)))?channelTransferVO.getPayInstrumentType():PretupsI.ALL;
		
		for (int i = 0, k = transferItemsList.size(); i < k; i++) {
			 Object ob = transferItemsList.get(i);
			 ChannelTransferItemsVO channelTransferItemVO = (ChannelTransferItemsVO)ob;
		}
		commissionProfileDAO.loadProductListWithTaxes(con, p_commissionProfileID, p_commissionProfileVersion, transferItemsList, p_txnType, paymentMode);

		ChannelTransferItemsVO channelTransferItemsVO = null;
		KeyArgumentVO argumentVO = null;
		final ArrayList errorList = new ArrayList();
		for (int i = 0, k = transferItemsList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) transferItemsList.get(i);
			if (!channelTransferItemsVO.isSlabDefine()) {
				argumentVO = new KeyArgumentVO();

				if (isWeb) {
					argumentVO.setKey("channeltransfer.transferdetails.error.commissionprofile.product.notdefine");
					argumentVO.setArguments(new String[] { channelTransferItemsVO.getProductName(), channelTransferItemsVO.getRequestedQuantity() });
				} else {
					argumentVO.setKey(PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE_SUBKEY);
					argumentVO.setArguments(new String[] { channelTransferItemsVO.getShortName(), channelTransferItemsVO.getRequestedQuantity() });
				}
				errorList.add(argumentVO);
			}
		}
		if (!errorList.isEmpty()) {
			if (isWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, errorList, p_forwardPath);
			} else {
				throw new BTSLBaseException(ChannelTransferBL.class.getName(), methodName, PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE, errorList);
			}
		}

		// to calculate tax
		calculateMRPWithTaxAndDiscountForDenomination(channelTransferVO, p_txnType);

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method calculateMRPWithTaxAndDiscount()
	 * To calculate the taxes on the selected product list
	 * 
	 * @param p_transferItemsList
	 * @param p_txnType
	 *            String
	 * @throws Exception
	 */
	public static void calculateMRPWithTaxAndDiscountForDenomination(ChannelTransferVO channelTransferVO, String p_txnType) throws Exception {
		final String methodName = "calculateMRPWithTaxAndDiscountForDenomination";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered  channelTransferVO = " + channelTransferVO.toString() + ", p_txnType = " + p_txnType);
		}
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		long productCost = 0, value = 0;
		final ArrayList p_transferItemsList = channelTransferVO.getChannelTransferitemsVOList();
		for (int i = 0, k = p_transferItemsList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) p_transferItemsList.get(i);
			productCost = Double.valueOf((Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue())).longValue();
			// In case of FOC Commission will not be calculated
			if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
				value = calculatorI.calculateCommission(channelTransferItemsVO.getCommType(), channelTransferItemsVO.getCommRate(), productCost);
			}
			channelTransferItemsVO.setCommValue(value);
			value = 0;
			value = calculatorI.calculateDiscount(channelTransferItemsVO.getDiscountType(), channelTransferItemsVO.getDiscountRate(), productCost);
			channelTransferItemsVO.setDiscountValue(value);
			value = 0;
			/*
			 * To check whether tax is applicable for transaction or not.We have
			 * the following three type of the
			 * transaction and following corresponding check condition.
			 * 1. In case of O2C transfer Tax calculation is mandatory.
			 * 2. In case of C2C transfer Tax calculation is based on the
			 * TAX_APPLICABLE_ON_C2C flag of the
			 * commission profile.
			 * 3. In case of FOC transfer Tax calculation is based on the
			 * TAX_APPLICABLE_ON_FOC flag of the
			 * commission profile.
			 */
			if (p_txnType.equals(PretupsI.TRANSFER_TYPE_O2C) || (p_txnType.equals(PretupsI.TRANSFER_TYPE_C2C) && PretupsI.YES.equals(channelTransferItemsVO
					.getTaxOnChannelTransfer())) || (p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && PretupsI.YES.equals(channelTransferItemsVO.getTaxOnFOCTransfer()))) {
				value = calculatorI.calculateTax1(channelTransferItemsVO.getTax1Type(), channelTransferItemsVO.getTax1Rate(), productCost);
				channelTransferItemsVO.setTax1Value(value);
				value = 0;
				value = calculatorI.calculateTax2(channelTransferItemsVO.getTax2Type(), channelTransferItemsVO.getTax2Rate(), channelTransferItemsVO.getTax1Value());
				channelTransferItemsVO.setTax2Value(value);
				value = 0;
				// set commision value as 0 , becoz commision not calculated on
				// the commision in case pf withdraw and return.
				if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && !(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferSubType()) || 
						PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equalsIgnoreCase(channelTransferVO.getTransferSubType()))) {
					channelTransferItemsVO.setCommValue(0);
				}
				// In case of FOC Tax3 will not be calculated
				if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
					value = calculatorI.calculateTax3(channelTransferItemsVO.getTax3Type(), channelTransferItemsVO.getTax3Rate(), channelTransferItemsVO.getCommValue());
				}
				channelTransferItemsVO.setTax3Value(value);
				value = 0;
			}
			value = calculatorI.calculateCommissionQuantity(channelTransferItemsVO.getCommValue(), channelTransferItemsVO.getUnitValue(), channelTransferItemsVO
					.getTax3Value());
			channelTransferItemsVO.setCommQuantity(value);
			long payableAmount = 0;
			long netPayableAmount = 0;

			if (!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC) && PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) 
					&& !(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferSubType()) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equalsIgnoreCase(channelTransferVO.getTransferSubType()))) {
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(channelTransferItemsVO.getRequiredQuantity());
				channelTransferItemsVO.setProductTotalMRP(productCost);
				channelTransferItemsVO.setPayableAmount(productCost);
				channelTransferItemsVO.setNetPayableAmount(productCost);
			}// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
			// true
			else if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && !p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)
					&& (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferSubType()) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equalsIgnoreCase(channelTransferVO.getTransferSubType()))) {
				value = 0;
				value = calculatorI.calculateReceiverCreditQuantity(channelTransferItemsVO.getRequestedQuantity(), channelTransferItemsVO.getUnitValue(),channelTransferItemsVO.getCommQuantity());
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(value);
				channelTransferItemsVO.setPayableAmount(productCost);
				channelTransferItemsVO.setNetPayableAmount(productCost);
				channelTransferItemsVO.setProductTotalMRP(channelTransferItemsVO.getReceiverCreditQty() * channelTransferItemsVO.getUnitValue() / (amountMultFactor));
			}// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
			// is false
			else if (!(PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) && !p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC)) {
				payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()),
						channelTransferItemsVO.getCommValue(), channelTransferItemsVO.getDiscountValue());
				netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, channelTransferItemsVO.getTax3Value());
				channelTransferItemsVO.setPayableAmount(payableAmount);
				channelTransferItemsVO.setNetPayableAmount(netPayableAmount);
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(channelTransferItemsVO.getRequiredQuantity());
				channelTransferItemsVO.setProductTotalMRP(productCost);
			} else {
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(channelTransferItemsVO.getRequiredQuantity());
				channelTransferItemsVO.setProductTotalMRP(productCost);
				channelTransferItemsVO.setPayableAmount(payableAmount);
				channelTransferItemsVO.setNetPayableAmount(netPayableAmount);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	/**
	 * Method that will call Data access object class to update the Channel to
	 * subscriber transfer details in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static int updateC2STransferDetailsReversal(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "updateC2STransferDetailsReversal";
		int updateCount = 0;
		try {
			updateCount = new C2STransferDAO().updateC2STransferDetailsReversal(con, c2sTransferVO);
			if (updateCount <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
			}
		} catch (BTSLBaseException be) {
			updateCount = 0;
			throw be;
		} catch (Exception e) {
			updateCount = 0;
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[updateC2STransferDetailsReversal]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception :" + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		}
		return updateCount;
	}

	/**
	 * Method that will call Data access object class to update the Channel to
	 * subscriber transfer details in the database
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static int updateOldC2STransferDetailsReversal(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "updateOldC2STransferDetailsReversal";
		int updateCount = 0;
		try {
			updateCount = new C2STransferDAO().updateOldC2STransferDetailsReversal(con, c2sTransferVO);
			if (updateCount <= 0) {
				if (PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equals(c2sTransferVO.getServiceType())) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_PRE_REVERSAL_ALREADY_DONE);
				} else {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_REVERSAL_ALREADY_DONE);
				}
			}

		} catch (BTSLBaseException be) {
			updateCount = 0;
			throw be;
		} catch (Exception e) {
			updateCount = 0;

			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[updateC2STransferDetailsReversal]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "Exception :" + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, "updateC2STransferDetailsReversal", PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		}
		return updateCount;
	}

	/**
	 * @author birendra.mishra
	 *         Validates if the WalletCode received in the request does exist in
	 *         the system or not.
	 *         If Not, throws an exception. Else put it into
	 *         ChannelTransferItemVO.
	 * @param con
	 * @param requestVO
	 * @param p_prdList
	 * @throws BTSLBaseException
	 */
	public static void loadAndValidateWallets(Connection con, RequestVO requestVO, ArrayList p_prdList) throws BTSLBaseException {

		final String methodName = "loadAndValidateWallets";

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered...");
		}
		boolean walletExist = false;
		ChannelTransferItemsVO chnlTransferItemVO = null;
		String balanceType = null;
		if(requestVO.getRequestMap() != null && requestVO.getRequestMap().get("WALLET") != null)
			balanceType=(String)requestVO.getRequestMap().get("WALLET");
		final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) requestVO.getValueObject();
		List<UserProductWalletMappingVO> prtSortWalletsForNetAndPrdct = null;
		String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
		for (final Iterator<ChannelTransferItemsVO> productIterator = p_prdList.iterator(); productIterator.hasNext();) {
			chnlTransferItemVO = productIterator.next();

			if (defaultWallet.equals(balanceType)) {
				chnlTransferItemVO.setUserWallet(balanceType);
				walletExist = true;
				break;

			} else {
				prtSortWalletsForNetAndPrdct = PretupsBL.getPrtSortWalletsForNetIdAndPrdId(networkPrefixVO.getNetworkCode(), chnlTransferItemVO.getProductCode());

				for (final Iterator<UserProductWalletMappingVO> walletIterator = prtSortWalletsForNetAndPrdct.iterator(); walletIterator.hasNext();) {
					final UserProductWalletMappingVO userProductWalletMappingVO = walletIterator.next();
					if (userProductWalletMappingVO.getAccountCode().equals(balanceType)) {
						chnlTransferItemVO.setUserWallet(balanceType);
						walletExist = true;
						break;
					}
				}
			}
			if (!BTSLUtil.isNullString(balanceType)) {
				chnlTransferItemVO.setUserWallet(balanceType);
				walletExist = true;
			} else {
				chnlTransferItemVO.setUserWallet(defaultWallet);
				walletExist = true;
			}

		}

		if (!walletExist) {
			final String strArr[] = { balanceType, networkPrefixVO.getNetworkCode(), chnlTransferItemVO.getProductCode() };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_BALANCE_TYPE_INVALID, 0, strArr, null);
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting...");
		}
	}

	/**
	 * @author birendra.mishra
	 * @param con
	 * @param requestVO
	 * @param p_prdList
	 */
	public static void assignDefaultWallet(Connection con, RequestVO requestVO, ArrayList p_prdList) {
		final String methodName = "assignDefaultWallet";

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered...");
		}
		ChannelTransferItemsVO chnlTransferItemVO = null;
		String balanceType = null;
		if(requestVO.getRequestMap() != null && requestVO.getRequestMap().get("WALLET") != null)
			balanceType=(String)requestVO.getRequestMap().get("WALLET");
		String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
		for (final Iterator<ChannelTransferItemsVO> iterator = p_prdList.iterator(); iterator.hasNext();) {
			chnlTransferItemVO = iterator.next();

			if (!BTSLUtil.isNullString(balanceType)) {
				chnlTransferItemVO.setUserWallet(balanceType);
			} else {
				chnlTransferItemVO.setUserWallet(defaultWallet);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting...");
		}
	}

	public static void increaseC2STransferInCounts(Connection con, C2STransferVO c2sTransferVO, boolean p_isCheckThresholds) throws BTSLBaseException {
		final String methodName = "increaseC2STransferInCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = " );
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			log.debug(methodName, loggerValue);
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		final boolean isLockRecordForUpdate = true;
		final boolean isUpdateRecord = true;
		try {


			final UserTransferCountsVO userTransferCountsVO = checkC2STransferINCounts(con, c2sTransferVO, isLockRecordForUpdate, p_isCheckThresholds);

			if (!BTSLUtil.isNullObject(userTransferCountsVO)) {

				ChannelTransferVO channelTransferVO =new ChannelTransferVO();
				channelTransferVO.setTransferID(userTransferCountsVO.getLastSOSTxnID());
				channelTransferVO.setNetworkCode(c2sTransferVO.getNetworkCode());
				channelTransferVO.setNetworkCodeFor(c2sTransferVO.getNetworkCode());
				channelTransferVO.setProductCode(c2sTransferVO.getProductCode());
				channelTransferVO.setToUserID(c2sTransferVO.getSenderID());    
				channelTransferVO.setToUserMsisdn(c2sTransferVO.getSenderMsisdn());

				if(SystemPreferences.USERWISE_LOAN_ENABLE) {

					Map hashmap = checkUserLoanstatusAndAmount(con, channelTransferVO);
					if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
						final String args[] = { PretupsBL.getDisplayAmount((long)hashmap.get(PretupsI.WITHDRAW_AMOUNT)) };
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.LOAN_SETTLEMENT_PENDING,args);
						}

					if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
						UserLoanWithdrawBL  userLoanWithdrawBL = new UserLoanWithdrawBL();
						userLoanWithdrawBL.autoChannelLoanSettlement(channelTransferVO, PretupsI.USER_LOAN_REQUEST_TYPE,(long)hashmap.get(PretupsI.WITHDRAW_AMOUNT));
					}
				}
				else {
					Map hashmap = checkSOSstatusAndAmount(con, userTransferCountsVO,channelTransferVO);
					if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
						final String args[] = { channelTransferVO.getToUserName() };
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.SOS_SETTLEMENT_PENDING);
					}

					if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
						ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
						ChannelTransferDAO channelTransferDAO =new ChannelTransferDAO();
						channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
						channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
						channelTransferVO.setChannelTransferitemsVOList(channelTransferDAO.loadChannelTransferItems(con, userTransferCountsVO.getLastSOSTxnID()));
						channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
					}
				}
				
				
				
				userTransferCountsVO.setLastTransferID(c2sTransferVO.getTransferID());
				if (c2sTransferVO.isTransferProfileCtInitializeReqd()) {

					setC2STransferInCountsForIncrease(c2sTransferVO, userTransferCountsVO);
					final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, isUpdateRecord);
					
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Exited updateCount = " + updateCount);
					}
					if (updateCount <= 0) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
					}
				} else {

					setC2STransferInCountsForIncrease(c2sTransferVO, userTransferCountsVO);
					final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, isUpdateRecord);

					if (log.isDebugEnabled()) {
						log.debug(methodName, "Exited updateCount = " + updateCount);
					}
					if (updateCount <= 0) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
					}
				}
			}
		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception:");
			loggerValue.append(e.getMessage());
			log.error(methodName, loggerValue);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[decreaseC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}

	public static UserTransferCountsVO checkC2STransferINCounts(Connection con, C2STransferVO c2sTransferVO, boolean p_isLockRecordForUpdate, boolean p_isCheckThresholds) throws BTSLBaseException {
		final String methodName = "checkC2STransferInCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			loggerValue.append(", p_isLockRecordForUpdate = ");
			loggerValue.append(p_isLockRecordForUpdate );
			loggerValue.append(", p_isCheckThresholds = ");
			loggerValue.append(p_isCheckThresholds);
			log.debug(methodName,loggerValue );
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

		UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, c2sTransferVO.getSenderID(), p_isLockRecordForUpdate);
		if (!p_isCheckThresholds) {
			return userTransferCountsVO;
		}
		final TransferProfileVO transferProfileVO = TransferProfileCache.getTransferProfileDetails(((ChannelUserVO) c2sTransferVO.getSenderVO()).getTransferProfileID(),
				c2sTransferVO.getNetworkCode());
		String[] strArr = null;
		try {
			// Done so as if someone has defined in Transfer Profile as Allowed
			// Transfer as 0
			if (BTSLUtil.isNullObject(userTransferCountsVO)) {
				userTransferCountsVO = new UserTransferCountsVO();
				userTransferCountsVO.setUpdateRecord(false);
			}

			userTransferCountsVO.setLastTransferID(c2sTransferVO.getTransferID());

			// To check whether Counters needs to be reinitialized or not
			final boolean isCounterReInitalizingReqd = checkResetCountersAfterPeriodChange(userTransferCountsVO, c2sTransferVO.getCreatedOn());
			c2sTransferVO.setTransferProfileCtInitializeReqd(isCounterReInitalizingReqd);
			boolean useC2sSeparateTransferCounts = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS);
			if (useC2sSeparateTransferCounts) {

				if (transferProfileVO.getDailySubscriberInCount() < userTransferCountsVO.getDailySubscriberInCount() + 1) {

					strArr = new String[] { String.valueOf(transferProfileVO.getDailySubscriberInCount()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_DAILY_SUBSCRIBER_IN_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getDailySubscriberInValue() < userTransferCountsVO.getDailySubscriberInValue() + c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getDailySubscriberInValue()), PretupsBL.getDisplayAmount(transferProfileVO.getDailySubscriberInValue()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_DAILY_SUBSCRIBER_IN_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklySubscriberInCount() < userTransferCountsVO.getWeeklySubscriberInCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getWeeklySubscriberInCount()) };

					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_WEEKLY_SUBSCRIBER_IN_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklySubscriberInValue() < userTransferCountsVO.getWeeklySubscriberInValue() + c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getWeeklySubscriberInValue()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklySubscriberInValue()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_WEEKLY_SUBSCRIBER_IN_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlySubscriberInCount() < userTransferCountsVO.getMonthlySubscriberInCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getMonthlySubscriberInCount()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_MONTHLY_SUBSCRIBER_IN_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlySubscriberInValue() < userTransferCountsVO.getMonthlySubscriberInValue() + c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getMonthlySubscriberInValue()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlySubscriberInValue()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_IN_VALREACHED, 0, strArr, null);
				}
			} else {
				if (transferProfileVO.getDailySubscriberInCount() < userTransferCountsVO.getDailySubscriberInCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getDailySubscriberInCount()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_DAILY_SUBSCRIBER_IN_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getDailySubscriberInValue() < userTransferCountsVO.getDailySubscriberInValue() + c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getDailySubscriberInValue()), PretupsBL.getDisplayAmount(transferProfileVO.getDailySubscriberInValue()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_DAILY_SUBSCRIBER_IN_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklySubscriberInCount() < userTransferCountsVO.getWeeklySubscriberInCount() + 1) {
					strArr = new String[] { String.valueOf(transferProfileVO.getWeeklySubscriberInCount()) };

					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_WEEKLY_SUBSCRIBER_IN_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getWeeklySubscriberInValue() < userTransferCountsVO.getWeeklySubscriberInValue() + c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getWeeklySubscriberInValue()), PretupsBL.getDisplayAmount(transferProfileVO.getWeeklySubscriberInValue()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_WEEKLY_SUBSCRIBER_IN_VALREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlySubscriberInCount() < userTransferCountsVO.getMonthlySubscriberInCount() + 1) {

					strArr = new String[] { String.valueOf(transferProfileVO.getMonthlySubscriberInCount()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_MONTHLY_SUBSCRIBER_IN_COUNTREACHED, 0, strArr, null);
				} else if (transferProfileVO.getMonthlySubscriberInValue() < userTransferCountsVO.getMonthlySubscriberInValue() + c2sTransferVO.getRequestedAmount() * 1) {
					strArr = new String[] { PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(userTransferCountsVO
							.getMonthlySubscriberInValue()), PretupsBL.getDisplayAmount(transferProfileVO.getMonthlySubscriberInValue()) };
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_IN_VALREACHED, 0, strArr, null);
				}

			}
		} catch (BTSLBaseException be) {
			loggerValue.setLength(0);
			loggerValue.append("BTSL Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(" ");
			loggerValue.append(be.getMessage());
			log.error(methodName,  loggerValue );
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception:");
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue );
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferInCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with userTransferCountsVO = " );
			loggerValue.append(userTransferCountsVO);
			log.debug(methodName, loggerValue);
		}
		return userTransferCountsVO;

	}

	public static int updateC2STransferForAmbigousReversal(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "updateC2STransferForAmbigousReversal";
		int updateCount = 0;
		try {
			updateCount = new C2STransferDAO().updateC2STransferForAmbigousReversal(con, c2sTransferVO);
			if (updateCount <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
			}
		} catch (BTSLBaseException be) {
			updateCount = 0;
			throw be;
		} catch (Exception e) {
			updateCount = 0;
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelTransferBL[updateC2STransferForAmbigousReversal]", c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO
					.getNetworkCode(), "Exception :" + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		}
		return updateCount;
	}

	/**
	 * Method to check whether Counters needs to be reinitialized or not for
	 * Roam Daily Amount
	 * 
	 * @param p_userTransferCountsVO
	 * @param p_newDate
	 */
	public static void checkRoamResetCounterAfterPeriodChange(UserTransferCountsVO p_userTransferCountsVO, java.util.Date p_newDate) {
		final String methodName = "checkResetCountersAfterPeriodChange";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered with transferID = ");
			loggerValue.append(p_userTransferCountsVO.getLastTransferID());
			loggerValue.append(", USER ID = ");
			loggerValue.append(p_userTransferCountsVO.getUserID());
			log.debug(methodName,  loggerValue);
		}

		final Date previousDate = p_userTransferCountsVO.getLastTransferDate();

		if (previousDate != null) {
			final Calendar cal = BTSLDateUtil.getInstance();
			cal.setTime(p_newDate);
			final int presentDay = cal.get(Calendar.DAY_OF_MONTH);
			final int presentMonth = cal.get(Calendar.MONTH);
			final int presentYear = cal.get(Calendar.YEAR);
			cal.setTime(previousDate);
			final int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
			final int lastTrxMonth = cal.get(Calendar.MONTH);
			final int lastTrxYear = cal.get(Calendar.YEAR);
			if ((presentDay != lastTrxDay) || (presentMonth != lastTrxMonth) || (presentYear != lastTrxYear)) {
				p_userTransferCountsVO.setDailyRoamAmount(0);
			}

			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("For transferID = ");
				loggerValue.append(p_userTransferCountsVO.getLastTransferID() );
				loggerValue.append(", USER ID=");
				loggerValue.append(p_userTransferCountsVO.getUserID());
				log.debug(methodName, "Exiting with Roam Counter check",loggerValue );
			}
		}
	}

	public static long checkC2SRoamCount(Connection con, C2STransferVO c2sTransferVO) throws BTSLBaseException {
		final String methodName = "increaseC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append( ", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			log.debug(methodName, loggerValue );
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		UserTransferCountsVO userTransferCountsVO = null;
		final boolean isLockRecordForUpdate = true;
		try {
			userTransferCountsVO = checkC2SRoamOutCount(con, c2sTransferVO, isLockRecordForUpdate);

			userTransferCountsVO.setDailyRoamAmount(userTransferCountsVO.getDailyRoamAmount());

			final int updateCount = userTransferCountsDAO.updateUserTransferCountRoam(con, userTransferCountsVO, userTransferCountsVO.isUpdateRecord());

			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exited updateCount = ");
				loggerValue.append(updateCount);
				log.debug(methodName,  loggerValue );
			}
			if (updateCount <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
			}

		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue );
			
			loggerValue.setLength(0);
			loggerValue.append( "Exception:" );
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[increaseC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),loggerValue.toString());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
		return userTransferCountsVO.getDailyRoamAmount();
	}

	public static UserTransferCountsVO checkC2SRoamOutCount(Connection con, C2STransferVO c2sTransferVO, boolean p_isLockRecordForUpdate) throws BTSLBaseException {
		final String methodName = "checkC2STransferOutCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID() );
			loggerValue.append(", p_isLockRecordForUpdate = " );
			loggerValue.append(p_isLockRecordForUpdate);
			log.debug(methodName,loggerValue);
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadRoamTransferCount(con, c2sTransferVO.getSenderID(), p_isLockRecordForUpdate);

		try {

			if (BTSLUtil.isNullObject(userTransferCountsVO)) {
				userTransferCountsVO = new UserTransferCountsVO();
				userTransferCountsVO.setUpdateRecord(false);
				userTransferCountsVO.setUserID(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserID());

			}

			userTransferCountsVO.setLastTransferID(c2sTransferVO.getTransferID());
			// To check whether Counters needs to be reinitialized or not
			checkRoamResetCounterAfterPeriodChange(userTransferCountsVO, c2sTransferVO.getCreatedOn());


		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception:");
			loggerValue.append( e.getMessage());
			log.error(methodName, loggerValue);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[checkC2STransferOutCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with userTransferCountsVO = " );
			loggerValue.append(userTransferCountsVO);
			log.debug(methodName,loggerValue );
		}
		return userTransferCountsVO;
	}

	public static void decreaseC2STransferInCounts(Connection con, C2STransferVO c2sTransferVO, boolean p_isCheckThresholds) throws BTSLBaseException {
		final String methodName = "decreaseC2STransferInCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered Transfer ID = ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", User ID = ");
			loggerValue.append(c2sTransferVO.getSenderID());
			loggerValue.append(", p_isCheckThresholds = ");
			loggerValue.append(p_isCheckThresholds);
			log.debug(methodName,loggerValue );
		}


		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		final boolean isLockRecordForUpdate = true;
		try {
			final UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(con, c2sTransferVO.getSenderID(), true);

			userTransferCountsVO.setUserID(c2sTransferVO.getSenderID());
			userTransferCountsVO.setLastTransferDate(c2sTransferVO.getCreatedOn());
			userTransferCountsVO.setLastOutTime(c2sTransferVO.getCreatedOn());

			userTransferCountsVO.setDailySubscriberInCount(userTransferCountsVO.getDailySubscriberInCount() - 1);
			userTransferCountsVO.setDailySubscriberInValue(userTransferCountsVO.getDailySubscriberInValue() - c2sTransferVO.getRequestedAmount());
			userTransferCountsVO.setWeeklySubscriberInCount(userTransferCountsVO.getWeeklySubscriberInCount() - 1);
			userTransferCountsVO.setWeeklySubscriberInValue(userTransferCountsVO.getWeeklySubscriberInValue() - c2sTransferVO.getRequestedAmount());
			userTransferCountsVO.setMonthlySubscriberInCount(userTransferCountsVO.getMonthlySubscriberInCount() - 1);
			userTransferCountsVO.setMonthlySubscriberInValue(userTransferCountsVO.getMonthlySubscriberInValue() - c2sTransferVO.getRequestedAmount());
			if (c2sTransferVO.isRoam()){
				userTransferCountsVO.setDailyRoamAmount(userTransferCountsVO.getDailyRoamAmount() + c2sTransferVO.getRequestedAmount());
			}
			final int updateCount = userTransferCountsDAO.updateUserTransferCounts(con, userTransferCountsVO, userTransferCountsVO.isUpdateRecord());

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited updateCount = " + updateCount);
			}
			if (updateCount <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
			}

		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append( ", Exception : ");
			loggerValue.append( e.getMessage());
			log.error(methodName, loggerValue );
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[decreaseC2STransferInCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}

	}

	/**
	 * Method loadC2CXfrProducts()
	 * This method find the filtered list of the products in the C2C transfer by the following checks (mainly commission profile)
	 * 1.	find the products mapped with network.
	 * 2.	get the product having active network product mapping.(filter product list)
	 * 3.	load user balance list.
	 * 4.	Now filter list with having the balance >0.
	 * 5.	check the latest commission profile assocated with the user.
	 * 6.	load the list of the products associated with the commission profile.
	 * 7.	load the list of the products associated with the transfer rule. 
	 * 8.	get the filtered product list from all the above cases.
	 * @param con
	 * @param userID
	 * @param networkCode
	 * @param p_commProfileSetId
	 * @param currentDate
	 * @param p_forwardPath
	 * @param isFromWeb
	 * @param userNameCode it can be userName in case of web and in case of SMS it will be User Code
	 * @param locale Locale
	 * @param p_productType TODO
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static List loadC2CXfrProducts(Connection con,String userID,String networkCode, String commProfileSetId, Date currentDate,String forwardPath,boolean isFromWeb,String userNameCode,Locale locale, String productType,String adminUserID) throws BTSLBaseException
	{
		final String methodName = "loadC2CXfrProductsWithXfrRule";

		if (log.isDebugEnabled()){
			log.debug(methodName, "Entered UserID : "+userID+", NetworkCode : "+networkCode+", CommissionProfileSetID : "+commProfileSetId+", CurrentDate : "+currentDate+", isFromWeb : "+isFromWeb+", userIDCODE : "+userNameCode+", locale = "+locale+", productType = "+productType+"adminUserID"+adminUserID);
		}

		List productList = new ArrayList();
		NetworkProductDAO networkProductDAO = new NetworkProductDAO();
		String args[] = {userNameCode};
		List prodList = networkProductDAO.loadProductListForXfr(con,productType,networkCode);
		/*
		 * 1. check whether product exist or not of the input productType
		 */
		if(prodList==null || prodList.isEmpty())
		{
			if(isFromWeb)
			{
				if(!BTSLUtil.isNullString(productType)){
					throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transfer.nodata.producttype",0,new String[]{productType},forwardPath);
				}
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transferc2c.nodata.product",forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_EXIST);
		}

		/*
		 * 2.
		 * checking that the status of network product mapping is active and also construct the new arrayList of 
		 * channelTransferItemsVOs containg required list.
		 */
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO;
		int i,j,m,n;
		for(i=0,j=prodList.size();i<j;i++)
		{
			networkProductVO = (NetworkProductVO)prodList.get(i);
			if(networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE))
			{
				channelTransferItemsVO = new ChannelTransferItemsVO();
				channelTransferItemsVO.setProductType(networkProductVO.getProductType());
				channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
				channelTransferItemsVO.setProductName(networkProductVO.getProductName());
				channelTransferItemsVO.setShortName(networkProductVO.getShortName()); 
				channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode()); 
				channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory()); 
				channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode()); 
				channelTransferItemsVO.setStatus(networkProductVO.getStatus()); 
				channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());				
				channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
				channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
				productList.add(channelTransferItemsVO);
			}
		}
		if(productList.isEmpty())
		{
			if(isFromWeb){
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transferc2c.nodata.networkproductmapping",forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_NOTMAPPED_NETWORK);
		}

		/*
		 * 3. load the product's BALANCE
		 */ 
		ChannelUserDAO channelUserDAO= new ChannelUserDAO();
		List userBalancesList = channelUserDAO.loadUserBalances(con,networkCode,networkCode,userID);
		if(userBalancesList ==null || userBalancesList.isEmpty())
		{
			if(isFromWeb){
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transfer.c2c.noproductassigned",0,args,forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE,args);
		}
		/*
		 * 4. find the products having the balance >0 and set the balance to the product list
		 */

		boolean validProductFound=false;
		boolean productFound=false;
		ArrayList errorList = new ArrayList(); 
		KeyArgumentVO keyArgumentVO ;
		UserBalancesVO balancesVO ;
		String errArgs[]; 		
		for(i = 0 , j = userBalancesList.size(); i < j; i++)
		{
			balancesVO = (UserBalancesVO) userBalancesList.get(i);


			for(m=0,n = productList.size(); m < n ; m++)
			{
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if(channelTransferItemsVO.getProductCode().equals(balancesVO.getProductCode()))
				{
					productFound=true;
					if(balancesVO.getBalance()<=0)
					{
						userBalancesList.remove(i);
						i--;
						j--;
						//add "product balance <=0" message in the list
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY);
						errArgs = new String[]{balancesVO.getProductShortName()};
						keyArgumentVO.setArguments(errArgs);
						errorList.add(keyArgumentVO);

						continue;
					}else{
						validProductFound=true;
						channelTransferItemsVO.setBalance(balancesVO.getBalance());
						break;
					}
				}                
			}

		}

		if(!productFound)
		{
			//add "product is suspended" message in the list
			keyArgumentVO = new KeyArgumentVO();
			keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_PRODUCT_SUSPENDED_SUBKEY);
			errArgs = new String[]{channelTransferItemsVO.getProductCode()};
			keyArgumentVO.setArguments(errArgs);
			errorList.add(keyArgumentVO);

		}
		if(!validProductFound)
		{
			String[] array= {userNameCode,BTSLUtil.getMessage(locale,errorList)};                
			if(isFromWeb){
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transferc2c.nodata.nobalance",0,array,forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE_IN_COMMPROFILE_NTWMAPPING,array);

		}
		//reomve the products form the list having no balance
		for(m=0,n = productList.size(); m < n ; m++)
		{
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);                
			if(channelTransferItemsVO.getBalance()<=0)
			{
				productList.remove(m);
				m--;
				n--;
			}
		}



		/*
		 * 5. load the latest version of the commission profile set id
		 */
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		String latestCommProfileVersion=null;
		try
		{
			final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, commProfileSetId, currentDate);
			latestCommProfileVersion= commissionProfileSetVO.getCommProfileVersion();
		}
		catch(BTSLBaseException bex)
		{
			if(PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage()))
			{
				if(isFromWeb){
					throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transferc2c.nodata.commprofilever",0,args,forwardPath);
				}
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED,args);
			}
			if(log.isDebugEnabled()){
				log.error("loadO2CXfrProductList","BTSLBaseException "+bex.getMessage());
			}
			throw bex;
		}

		//if there is no commission profile version exist upto the current date show the error message.
		if(BTSLUtil.isNullString(latestCommProfileVersion))
		{
			if(isFromWeb){
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transferc2c.nodata.commprofilever",0,args,forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED,args);
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		
		String type = (TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_C2C:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		List commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(con,commProfileSetId,latestCommProfileVersion, type, paymentMode);

		//if list is empty send the error message
		if(commissionProfileProductList == null || commissionProfileProductList.isEmpty())
		{
			if(isFromWeb){
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transferc2c.nodata.commprofileproduct",0,new String[]{userNameCode,latestCommProfileVersion},forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_WITH_COMM_PROFILE,args);
		}

		//filterize the product list with the products of the commission profile products	
		CommissionProfileProductsVO commissionProfileProductsVO ;
		for(i = 0 , j = commissionProfileProductList.size(); i < j; i++)
		{
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for(m=0,n = productList.size(); m < n ; m++)
			{
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);                
				if(channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode()))
				{
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff()); 
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileDetailID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}                
			}
		}
		for(m=0,n = productList.size(); m < n ; m++)
		{
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);                
			if(BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID()))
			{
				productList.remove(m);
				m--;
				n--;
			}
		}
		// if list size is zero than send the error message.
		if(productList==null || productList.isEmpty())
		{
			if(isFromWeb){
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transferc2c.nodata.networkcommprofileproduct",0,args,forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_SAME_PRODUCT_IN_COMMPROFILE_NTWMAPPING,args);
		}

		/*
		 * 7. load the product list associated with the transfer rule.
		 */
		ProductTypeDAO productTypeDAO = new ProductTypeDAO();
		List prodWithXfrRuleList = productTypeDAO.loadUserProductsListForWithdrawViaAdmin(con, adminUserID); 

		if(prodWithXfrRuleList==null || prodWithXfrRuleList.isEmpty() )
		{
			if(isFromWeb){
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transfer.noproductassigned.transferrule",0,args,forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTDEFINE,args);
		}
		/*
		 * 8. filter the list with the products associated with the transfer rule. (final filteration)
		 */
		List c2cXfrProductList=new ArrayList();
		for(i = 0 , j = prodWithXfrRuleList.size(); i < j; i++)
		{
			String productCode=(String)prodWithXfrRuleList.get(i);
			for(m=0,n = productList.size(); m < n ; m++)
			{
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);                
				if(channelTransferItemsVO.getProductCode().equals(productCode))
				{
					c2cXfrProductList.add(channelTransferItemsVO);
				}                
			}
		}
		//if list is of size =0. show error message.
		if(c2cXfrProductList.isEmpty())
		{
			if(isFromWeb){
				throw new BTSLBaseException(ChannelTransferBL.class,methodName,"message.transferc2c.nodata.transferrule",0,args,forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH,args);
		}
		if (log.isDebugEnabled()){
			log.debug(methodName, "Exited with c2cXfrProductList.size() = "+c2cXfrProductList.size());
		}
		return c2cXfrProductList;
	}

	/**
	 * Method loadAndValidateProductsforSOS
	 * This method is used to filter the products associated to
	 * the channel user with the products which are comming in the request
	 * The boolean isTransfer determines whether any check on min and max
	 * value for transfer is applicable
	 * 
	 * @param requestVO
	 *            RequestVO
	 * @param con
	 *            Connection
	 * @param prdCodeQtyMap
	 *            HashMap
	 * @param pchannelUserVO
	 *            ChannelUserVO
	 * @param isTransfer
	 * @return tmpPrdList ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadAndValidateProductsforSOS(Connection con, RequestVO requestVO, HashMap prdCodeQtyMap, ChannelUserVO pchannelUserVO, boolean isTransfer) throws BTSLBaseException {
		final String methodName = "loadAndValidateProducts";

         StringBuilder loggerValue= new StringBuilder(); 
         loggerValue.setLength(0);
         loggerValue.append("Entering  : prdCodeQtyMap - ");
         loggerValue.append(prdCodeQtyMap);
         loggerValue.append(", pchannelUserVO = ");
         loggerValue.append(pchannelUserVO);
         loggerValue.append(", isTransfer = ");
         loggerValue.append(isTransfer);
         
		LogFactory.printLog(methodName, loggerValue.toString() , log);


		boolean firstCheckForPrdType = true; // for setting of product type once
		String prdType = null;
		ArrayList productListFromDao = null;
		final ArrayList tmpPrdList = new ArrayList();
		ArrayList commPrdList = null;
		ArrayList filteredProductList = new ArrayList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		final HashMap prdShortCodeMap = new HashMap();
		final String args[] = { pchannelUserVO.getUserCode() };
		int m,n;

		// filter the products from the n/w's
		// products & the request's products
		final NetworkProductDAO networkDAO = new NetworkProductDAO();
		productListFromDao = networkDAO.loadProductListForXfr(con, null, pchannelUserVO.getNetworkID());

		final Iterator nwItr = (prdCodeQtyMap.keySet()).iterator();
		String mapPrdShortCode = null;
		while (nwItr.hasNext()) {
			mapPrdShortCode = (String) nwItr.next();
			for (int i = 0, j = productListFromDao.size(); i < j; i++) {
				networkProductVO = (NetworkProductVO) productListFromDao.get(i);
				if ((mapPrdShortCode).equals(String.valueOf(networkProductVO.getProductShortCode()))) {
					// for checking whether all the products in the request have
					// the same type
					if (firstCheckForPrdType) {
						prdType = networkProductVO.getProductType();
						firstCheckForPrdType = false;
					}
					// creating and populating the ChannelTransferItemsVO from
					// the NetworkProductVO
					channelTransferItemsVO = new ChannelTransferItemsVO();

					if (!prdType.equals(networkProductVO.getProductType())) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PRODUCT_TYPE_NOT_SAME);
					}

					channelTransferItemsVO.setProductType(networkProductVO.getProductType());
					channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
					channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
					channelTransferItemsVO.setProductName(networkProductVO.getProductName());
					channelTransferItemsVO.setShortName(networkProductVO.getShortName());
					channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
					channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
					channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
					channelTransferItemsVO.setStatus(networkProductVO.getStatus());
					channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
					channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
					channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
					channelTransferItemsVO.setApprovedQuantity(pchannelUserVO.getSosAllowedAmount());
					channelTransferItemsVO.setReceiverCreditQty(pchannelUserVO.getSosAllowedAmount());
					channelTransferItemsVO.setNetPayableAmount(pchannelUserVO.getSosAllowedAmount());
					channelTransferItemsVO.setPayableAmount(pchannelUserVO.getSosAllowedAmount());
					channelTransferItemsVO.setSenderDebitQty(pchannelUserVO.getSosAllowedAmount());
					channelTransferItemsVO.setRequiredQuantity(pchannelUserVO.getSosAllowedAmount());


					prdShortCodeMap.put(networkProductVO.getProductCode(), channelTransferItemsVO);
					filteredProductList.add(channelTransferItemsVO);
					break;
				}

			}
			if (!((mapPrdShortCode).equals(String.valueOf(networkProductVO.getProductShortCode()))))
			{
				throw new BTSLBaseException("ChannelTransferBL", methodName, PretupsErrorCodesI.SOS_INVALID_PRODUCT_CODE);
			}
		}

		final Date currDate = new Date();
		final CommissionProfileDAO commPrDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();

		// load the latest version of commission profile
		final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, pchannelUserVO.getCommissionProfileSetID(), currDate);
		final String commProfileLatestVer = commissionProfileSetVO.getCommProfileVersion();
		if (BTSLUtil.isNullString(commProfileLatestVer)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_LATEST_COMMISSION_PROFILE_ASSOCIATED);
		}

		pchannelUserVO.setCommissionProfileSetVersion(commProfileLatestVer);
		String latestCommProfileVersion = null;
		final Date currentDate = new Date();
		try {
			final CommissionProfileSetVO latestCommissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, pchannelUserVO.getCommissionProfileSetID(), currentDate);
			latestCommProfileVersion = latestCommissionProfileSetVO.getCommProfileVersion();
		} catch (BTSLBaseException bex) {
			if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED, args);
			}
			loggerValue.setLength(0);
			loggerValue.append("BTSLBaseException ");
			loggerValue.append(bex.getMessage());
			log.error("loadO2CXfrProductList",  loggerValue );
			throw bex;
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		
		String type = (TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		commPrdList = commPrDAO.loadCommissionProfileProductsList(con, pchannelUserVO.getCommissionProfileSetID(), pchannelUserVO.getCommissionProfileSetVersion(), type, paymentMode);
		if (commPrdList == null || commPrdList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_COMMISION_PRODUCT_ASSOCIATED);
		}

		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final ArrayList commissionProfileProductList = commissionProfileDAO .loadCommissionProfileProductsList(con, pchannelUserVO.getCommissionProfileSetID(), latestCommProfileVersion, type, paymentMode);

		// if list is empty send the error message
		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_WITH_COMM_PROFILE, args);
		}

		// filterize the product list with the products of the commission
		// profile products
		CommissionProfileProductsVO commissionProfileProductsVO = null;
		for (int i = 0,  j = commissionProfileProductList.size(); i < j; i++) {
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for ( m = 0,  n = filteredProductList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) filteredProductList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileProductID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}
			}

		}
		CommissionProfileDeatilsVO commissionProfileDeatilsVO = commissionProfileTxnDAO.loadCommissionProfileDetails(con, channelTransferItemsVO.getCommProfileProductID());
		if (commissionProfileDeatilsVO!=null)
		{
			channelTransferItemsVO.setCommProfileDetailID(commissionProfileDeatilsVO.getCommProfileDetailID());
		}
		else 
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_MVD, args);


		for (m = 0, n = filteredProductList.size(); m < n; m++) {

			if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
				filteredProductList.remove(m);
				m--;
				n--;
			}
		}
		// if list size is zero than send the error message.
		if (filteredProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_SAME_PRODUCT_IN_COMMPROFILE_NTWMAPPING, args);
		}




		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting : tmpPrdList :: " );
			loggerValue.append(tmpPrdList.size());
			log.debug(methodName, loggerValue );
		}
		loggerValue.setLength(0);
		loggerValue.append("Exiting : tmpPrdList :: ");
		loggerValue.append(tmpPrdList.size());
		LogFactory.printLog(methodName,  loggerValue.toString() , log);

		return filteredProductList;
	}



	/**
	 * This method checks the message sequence and validates
	 * whether the request contents are valid or not.Also the boolean
	 * isTransfer decides whether the request is for
	 * transfer or withdraw.
	 * 
	 * @param con
	 *            Connection
	 * @param requestVO
	 *            RequestVO
	 * @param channelTransferVO
	 *            ChannelTransferVO
	 * @param isTransfer
	 *            Boolean
	 * @return productMap HashMap
	 */
	public static HashMap validateSOSMessageContent(Connection con, RequestVO requestVO, ChannelTransferVO channelTransferVO, boolean isTransfer) throws BTSLBaseException {
		final String methodName = "validateO2CMessageContent";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
		loggerValue.append("Entered requestVO : ");
		loggerValue.append(requestVO);
		loggerValue.append(", channelTransferVO : ");
		loggerValue.append(channelTransferVO);
		loggerValue.append(", isTransfer : ");
		loggerValue.append(isTransfer);				
		LogFactory.printLog(methodName, loggerValue.toString() , log);

		// take the messageArray from the request and filter the product short
		// code
		// and product qty from the array and return a hashMap
		final HashMap productMap = checkSOSTrfReqMsgSyntax(requestVO.getRequestMessageArray(), (((ChannelUserVO) requestVO.getSenderVO()).getUserPhoneVO())
				.getPinRequired());

		final HashMap requestMap = requestVO.getRequestMap();

		// take the requestMap from the requset
		// & validate the data from this hashMap
		if (requestMap != null) {
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("entered for requestMap not null :requestMap = ");
				loggerValue.append(requestMap.size());
				log.debug(methodName, loggerValue );
			}


			channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
           loggerValue.setLength(0);
           loggerValue.append("Exiting : productMap.size():: ");
           loggerValue.append(productMap.size());
		   LogFactory.printLog(methodName,loggerValue.toString() , log);



		}
		return productMap;
	}


	/**
	 * This method checks the message sequence and finally return
	 * a Map of the product short code and product codes.
	 * 
	 * @param messageArray
	 * @param userPinRequired
	 * @return HashMap ProductMap
	 */
	private static HashMap checkSOSTrfReqMsgSyntax(String[] messageArray, String userPinRequired) throws BTSLBaseException {
		final String methodName = "checkSOSTrfReqMsgSyntax";
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
		loggerValue.append("Entered with messageArray : ");
		loggerValue.append(messageArray);
		loggerValue.append(", userPinRequired : ");
		loggerValue.append(userPinRequired);
		LogFactory.printLog(methodName, loggerValue.toString(), log);

		for (int i = 0, j = messageArray.length; i < j; i++) {
			log.debug(methodName, "messageArray[" + i + "] : " + messageArray[i]);
		}

		final HashMap productMap = new HashMap();

		if (messageArray.length > 3  && messageArray[0].equals("SOSTRF")) {
			log.debug(methodName, "arrlength " + messageArray.length);
			final int startIndex = 1;
			int endIndex = 0;
			if ((messageArray.length % 2) == 0) {
				endIndex = (messageArray.length - 1);
			} else {
				endIndex = messageArray.length;
			}

			for (int i = startIndex; i < endIndex; i += 2) {
				if (productMap.size() > 0) {
					if (productMap.containsKey(messageArray[i + 1])) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_MESSAGE_FORMAT_NOT_PROPER, 0, messageArray, null);
					}
				}
			}
			productMap.put(messageArray[1], messageArray[1]);

		}


		else

		{
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_MESSAGE_FORMAT_NOT_PROPER);
		}

		// validate the product short codes and
		// quantities which are comming in the request
		String prdShortCode = null;
		final Collection keySet = productMap.keySet();
		final Iterator itr = keySet.iterator();
		while (itr.hasNext()) {
			prdShortCode = (String) itr.next();

			if (BTSLUtil.isNullString(prdShortCode)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_CODE_FORMAT);
			} else if (!BTSLUtil.isNumeric(prdShortCode)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.SOS_INVALID_PRODUCT_CODE);

			}
		}

       loggerValue.setLength(0);
       loggerValue.append("Exiting : productMap.size():: ");
       loggerValue.append(productMap.size());
		LogFactory.printLog(methodName, loggerValue.toString() , log);


		return productMap;
	}

	/**
	 * Method validateUserProductsFormatForSOS()
	 * To get the product from the message Array and validate them whether
	 * product code and
	 * product quantity is valid or not.
	 * 
	 * @param prodArray
	 * @param requestVO
	 *            RequestVO
	 * @return String[]
	 * @throws BTSLBaseException
	 */
	public static String[] validateUserProductsFormatForSOS(String[] prodArray, RequestVO requestVO) throws BTSLBaseException {
		final String methodName = "validateUserProductsFormatForSOS";

		LogFactory.printLog(methodName, "Entered with prodArray = " + prodArray, log);


		String productArray[] = null;


		final int arrLength = prodArray.length;
		boolean defaultProduct = false;
		boolean validateProduct = false;

		if ((((ChannelUserVO) requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {

			if (arrLength < 5) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { requestVO
					.getActualMessageFormat() }, null);
			}
			else
				validateProduct = true;

		} else {

			if (arrLength < 4) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { requestVO
					.getActualMessageFormat() }, null);
			}

			else
				validateProduct = true;
		}

		if (validateProduct) {
			int size = arrLength;
			productArray = new String[1];
			productArray[0] = prodArray[1];

		}

		LogFactory.printLog(methodName, "Exited with productArray.length = " + productArray.length, log);

		return productArray;
	}


	public static ArrayList validateReqstProdsWithDefinedProdsForSOS(Connection con, ChannelUserVO senderVO, String[] productArr, Date curDate, Locale locale, long qty) throws BTSLBaseException {
		final String methodName = "validateReqstProdsWithDefinedProdsForXFR";

		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
		loggerValue.append("Entered productArr Length = ");
		loggerValue.append(productArr.length);
		loggerValue.append(", SenderVO " );
		loggerValue.append(senderVO);
		loggerValue.append(", curDate : ");
		loggerValue.append(curDate);
		loggerValue.append(", Quantity = ");
		loggerValue.append(qty);
		
		LogFactory.printLog(methodName,  loggerValue.toString() , log);
		final ArrayList tempProductList = ChannelTransferBL.loadC2CXfrProductsWithXfrRuleForSOS(con, senderVO.getUserID(), senderVO.getNetworkID(),
				senderVO.getCommissionProfileSetID(),curDate, senderVO.getUserCode(), locale, null);
		ChannelTransferItemsVO channelTransferItemsVO = null;
		final ArrayList notMatchedProdList = new ArrayList();
		final ArrayList minLessProdList = new ArrayList();
		final ArrayList maxMoreProdList = new ArrayList();
		final ArrayList balanceList = new ArrayList();
		final ArrayList multipleOfList = new ArrayList();
		final ArrayList productList = new ArrayList();

		boolean exist = false;
		KeyArgumentVO keyArgumentVO = null;
		int prodCode = 0;
		int m, n;

		for (int i = 0, j = tempProductList.size(); i < j; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) tempProductList.get(i);
			/*
			 * To check whether product selected by user exists in his
			 * productlist or not.
			 */
			for (m = 0, n = productArr.length; m < n; m += 2) {
				prodCode = Integer.parseInt(productArr[m]);
				if (channelTransferItemsVO.getProductShortCode() == prodCode) {
					if (channelTransferItemsVO.getBalance() < (qty)) {// balanceList
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE_SUBKEY);
						final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getBalance()) };
						keyArgumentVO.setArguments(args);
						balanceList.add(keyArgumentVO);
					}
					exist = true;
					channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(qty));
					channelTransferItemsVO.setRequiredQuantity(qty);
					channelTransferItemsVO.setPayableAmount(qty);
					channelTransferItemsVO.setApprovedQuantity(qty);
					channelTransferItemsVO.setReceiverCreditQty(qty);
					channelTransferItemsVO.setNetPayableAmount(qty);
					channelTransferItemsVO.setSenderDebitQty(qty);

					productList.add(channelTransferItemsVO);
					break;
				}
			}
			if (!exist) {
				notMatchedProdList.add(String.valueOf(prodCode));
			}
		}



		if (notMatchedProdList.size() == tempProductList.size()) {
			final String prodArr[] = new String[notMatchedProdList.size()];
			notMatchedProdList.toArray(prodArr);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NOT_ALLOWED, prodArr);
		} else if (minLessProdList != null && !minLessProdList.isEmpty()) {
			final String[] array = { BTSLUtil.getMessage(locale, minLessProdList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MIN_TRANSFER, array);
		} else if (maxMoreProdList!= null && !maxMoreProdList.isEmpty()) {
			final String[] array = { BTSLUtil.getMessage(locale, maxMoreProdList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_TRANSFER, array);
		} else if (multipleOfList!= null && !multipleOfList.isEmpty()) {
			final String[] array = { BTSLUtil.getMessage(locale, multipleOfList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MULTIPLE_OF, array);
		} else if (balanceList!= null && !balanceList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.SOS_SENDER_BALANCE_LESS);
		}
        loggerValue.setLength(0);
        loggerValue.append("Exited with productList.size() = ");
        loggerValue.append(productList.size());
		LogFactory.printLog(methodName, loggerValue.toString(), log);

		return productList;
	}

	public static ArrayList loadC2CXfrProductsWithXfrRuleForSOS(Connection con, String userID, String networkCode, String commProfileSetId ,Date currentDate, String userNameCode, Locale locale, String productType) throws BTSLBaseException {
		final String methodName = "loadC2CXfrProductsWithXfrRule";

        StringBuilder loggerValue= new StringBuilder(); 
        loggerValue.setLength(0);
        loggerValue.append("Entered UserID : ");
        loggerValue.append(userID);
        loggerValue.append(", NetworkCode : " );
        loggerValue.append(networkCode);
        loggerValue.append( ", CurrentDate : " );
        loggerValue.append(currentDate);
        loggerValue.append(", p_userIDCODE : ");
        loggerValue.append(userNameCode);
        loggerValue.append(", locale = ");
        loggerValue.append(locale);
        loggerValue.append(", productType = ");
        loggerValue.append(productType);
		LogFactory.printLog(methodName,   loggerValue.toString(), log);
		final UserProductWalletMappingCache userProductWalletMappingCache = null;
		final ArrayList productList = new ArrayList();
		final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
		final String args[] = { userNameCode };
		final ArrayList prodList = networkProductDAO.loadProductListForXfr(con, productType, networkCode);
		/*
		 * 1. check whether product exist or not of the input productType
		 */
		if (prodList.isEmpty()) {

			throw new BTSLBaseException( "message.transferc2c.nodata.product");
		}


		/*
		 * 2.
		 * checking that the status of network product mapping is active and
		 * also construct the new arrayList of
		 * channelTransferItemsVOs containg required list.
		 */
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		int i, j, m, n;
		for (i = 0, j = prodList.size(); i < j; i++) {
			networkProductVO = (NetworkProductVO) prodList.get(i);
			if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
				channelTransferItemsVO = new ChannelTransferItemsVO();
				channelTransferItemsVO.setProductType(networkProductVO.getProductType());
				channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
				channelTransferItemsVO.setProductName(networkProductVO.getProductName());
				channelTransferItemsVO.setShortName(networkProductVO.getShortName());
				channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
				channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
				channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
				channelTransferItemsVO.setStatus(networkProductVO.getStatus());
				channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
				channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
				channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
				productList.add(channelTransferItemsVO);
			}
		}
		if (productList.isEmpty()) {

			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOTMAPPED_NETWORK);
		}

		/*
		 * 3. load the product's BALANCE
		 */
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final ArrayList userBalancesList = channelUserDAO.loadUserBalances(con, networkCode, networkCode, userID);
		if (userBalancesList == null || userBalancesList.isEmpty()) {

			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE, args);
		}
		/*
		 * 4. find the products having the balance >0 and set the balance to the
		 * product list
		 */

		boolean validProductFound = false;
		boolean productFound = false;
		final ArrayList errorList = new ArrayList();
		KeyArgumentVO keyArgumentVO = null;
		UserBalancesVO balancesVO = null;
		String errArgs[] = null;
		long currentBalance = 0;
		long previousBalance = 0;
		final long balance = 0;
		String previousProductType = null;
		String currentProductType = null;
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		if (userProductMultipleWallet) {
			for (i = 0; i < userBalancesList.size(); i++) {
				balancesVO = (UserBalancesVO) userBalancesList.get(i);
				currentProductType = balancesVO.getProductCode();
				currentBalance = balancesVO.getBalance();
				if (currentProductType.equals(previousProductType)) {
					currentBalance = currentBalance + previousBalance;
					balancesVO.setBalance(currentBalance);
					userBalancesList.remove(i - 1);
					i--;
				}
				previousBalance = currentBalance;
				previousProductType = currentProductType;
			}
		}

		for (i = 0, j = userBalancesList.size(); i < j; i++) {
			balancesVO = (UserBalancesVO) userBalancesList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {

				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);

				if (channelTransferItemsVO.getProductCode().equals(balancesVO.getProductCode())) {
					productFound = true;
					if (balancesVO.getBalance() <= 0) {
						userBalancesList.remove(i);
						i--;
						j--;

						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY);

						errArgs = new String[] { balancesVO.getProductShortName() };
						keyArgumentVO.setArguments(errArgs);
						errorList.add(keyArgumentVO);

						continue;
					} else {
						validProductFound = true;
						channelTransferItemsVO.setBalance(balancesVO.getBalance());
						break;
					}
				}
			}

		}

		if (!productFound) {

			keyArgumentVO = new KeyArgumentVO();
			keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_PRODUCT_SUSPENDED_SUBKEY);

			errArgs = new String[] { channelTransferItemsVO.getProductCode() };
			keyArgumentVO.setArguments(errArgs);
			errorList.add(keyArgumentVO);

		}
		if (!validProductFound) {
			final String[] array = { userNameCode, BTSLUtil.getMessage(locale, errorList) };

			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE_IN_COMMPROFILE_NTWMAPPING, array);

		}
		// reomve the products form the list having no balance
		for (m = 0, n = productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
			if (channelTransferItemsVO.getBalance() <= 0) {
				productList.remove(m);
				m--;
				n--;
			}
		}

		/*
		 * 5. load the latest version of the commission profile set id
		 */
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		String latestCommProfileVersion = null;
		try {
			final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, commProfileSetId, currentDate);
			latestCommProfileVersion = commissionProfileSetVO.getCommProfileVersion();
		} catch (BTSLBaseException bex) {
			if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED, args);
			}
			loggerValue.setLength(0);
			loggerValue.append("BTSLBaseException ");
			loggerValue.append(bex.getMessage());
			log.error("loadO2CXfrProductList",  loggerValue);
			throw bex;
		}

		// if there is no commission profile version exist upto the current date
		// show the error message.
		if (BTSLUtil.isNullString(latestCommProfileVersion)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED, args);
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		
		String type = (TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_C2C:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(con, commProfileSetId, latestCommProfileVersion, type, paymentMode);

		// if list is empty send the error message
		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_WITH_COMM_PROFILE, args);
		}

		// filterize the product list with the products of the commission
		// profile products
		CommissionProfileProductsVO commissionProfileProductsVO = null;
		CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
		for (i = 0, j = commissionProfileProductList.size(); i < j; i++) {
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileProductID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}
			}
		}

		commissionProfileDeatilsVO= commissionProfileTxnDAO.loadCommissionProfileDetails(con, channelTransferItemsVO.getCommProfileProductID());
		channelTransferItemsVO.setCommProfileDetailID(commissionProfileDeatilsVO.getCommProfileDetailID());

		for (m = 0, n = productList.size(); m < n; m++) {
			if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
				productList.remove(m);
				m--;
				n--;
			}
		}
		// if list size is zero than send the error message.
		if (productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_SAME_PRODUCT_IN_COMMPROFILE_NTWMAPPING, args);
		}

		/*
		 * 7. load the product list associated with the transfer rule.
		 */

		final ArrayList c2cXfrProductList = new ArrayList();


		for (m = 0, n = productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);

			c2cXfrProductList.add(channelTransferItemsVO);

		}

		// if list is of size =0. show error message.
		if (c2cXfrProductList.isEmpty()) {

			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH, args);
		}

		LogFactory.printLog(methodName, "Exited with c2cXfrProductList.size() = " + c2cXfrProductList.size()+c2cXfrProductList, log);
		return c2cXfrProductList;
	}

	public static Map<String, Object> checkSOSstatusAndAmount(Connection con,UserTransferCountsVO p_userTransferCountsVO,ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "checkSOSstatusAndAmount";
		LogFactory.printLog(methodName,"p_userTransferCountsVO" + p_userTransferCountsVO + "ChannelTransferVO" + channelTransferVO, log);
		final HashMap sosStatus = new HashMap();
		/* SET product code in case of C2C withdraw */
		final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		if (itemsList != null && !itemsList.isEmpty()) {
			for (int i = 0, k = itemsList.size(); i < k; i++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				channelTransferVO.setProductCode(channelTransferItemsVO.getProductCode());
			}
		}
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		String sosSettlementType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLEMENT_TYPE);
		String allowTransactionIfSosSettlementFail = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL);
		
		/* Check System Level prerferences */
		if (channelSosEnable) {

			if ((PretupsI.SOS_SETTLEMENT_TYPE_AUTO.equalsIgnoreCase(sosSettlementType)
					|| (PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(
							PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, channelTransferVO.getNetworkCode()))))) {
				/* Check user level SOS Status as Pending */
				if (!BTSLUtil.isNullString(p_userTransferCountsVO.getLastSOSTxnStatus())) {
					if (p_userTransferCountsVO.getLastSOSTxnStatus().equalsIgnoreCase(PretupsI.SOS_PENDING_STATUS)) {
						String product = null;

						ChannelUserDAO channelUserDAO = new ChannelUserDAO();
						ChannelUserVO channelUserVO;
						Connection conn1 = null;
						MComConnectionI mcomCon1 = null;
						try {
							mcomCon1 = new MComConnection();
							try {
								conn1 = mcomCon1.getConnection();
							} 
							
							catch (SQLException e) {
								log.error(methodName, "Exception " + e.getMessage());								
							}
							channelUserVO = channelUserDAO.loadChannelUserForSOS(conn1, channelTransferVO.getToUserID(),
									channelTransferVO.getProductType(), channelTransferVO.getProductCode(), PretupsI.SOS_REQUEST_TYPE);
						} finally {
							if (mcomCon1 != null) {
								mcomCon1.close("ChannelTransferBL#checkSOSstatusAndAmount");
								mcomCon1 = null;
							}
						}


						if (BTSLUtil.isNullObject(channelUserVO)) {
							sosStatus.put(PretupsI.DO_WITHDRAW, false);
							sosStatus.put(PretupsI.BLOCK_TRANSACTION, false);
						} else {
							List<String> constantsList = Arrays.asList(allowTransactionIfSosSettlementFail.trim().split("\\s*,\\s*"));

							/* Compare Quantity for SOS */
							LogFactory.printLog(methodName, "SOS MRP plus OLD Balance === " +channelTransferVO.getTransferMRP() + ", " + channelUserVO.getBalance() , log);
							LogFactory.printLog(methodName, "SOS Allowed Amount " + channelUserVO.getSosAllowedAmount() , log);
							LogFactory.printLog(methodName,	"Preference Code" + constantsList, log);
							if ((channelTransferVO.getTransferMRP() + channelUserVO.getBalance()) < channelUserVO
									.getSosAllowedAmount()) {
								if (constantsList.contains(channelTransferVO.getTransactionCode())
										|| PretupsI.CHANNEL_TRANSFER_TYPE_RETURN
										.equals(channelTransferVO.getTransferType())
										|| PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL
										.equals(channelTransferVO.getTransferType()) || PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equalsIgnoreCase(channelTransferVO.getTransactionCode())) {
									sosStatus.put(PretupsI.BLOCK_TRANSACTION, false);
									sosStatus.put(PretupsI.DO_WITHDRAW, false);
								} else {
									sosStatus.put(PretupsI.DO_WITHDRAW, false);
									sosStatus.put(PretupsI.BLOCK_TRANSACTION, true);
								}

							} else {
								sosStatus.put(PretupsI.DO_WITHDRAW, true);
								sosStatus.put(PretupsI.BLOCK_TRANSACTION, false);
								sosStatus.put(PretupsI.WITHDRAW_AMOUNT, channelUserVO.getSosAllowedAmount());
								channelTransferVO.setProductCode(channelUserVO.getProductCode());
							}
						}

					}

				}
			} else {
				if (!BTSLUtil.isNullString(p_userTransferCountsVO.getLastSOSTxnStatus())) {
					if (p_userTransferCountsVO.getLastSOSTxnStatus().equalsIgnoreCase(PretupsI.SOS_PENDING_STATUS)) {
						List<String> constantsList = Arrays.asList(allowTransactionIfSosSettlementFail.trim().split("\\s*,\\s*"));
						if (constantsList.contains(channelTransferVO.getTransactionCode()) || channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN)	
								|| channelTransferVO.getTransferType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
							sosStatus.put(PretupsI.BLOCK_TRANSACTION, false);
							sosStatus.put(PretupsI.DO_WITHDRAW, false);
						} else {
							sosStatus.put(PretupsI.DO_WITHDRAW, false);
							sosStatus.put(PretupsI.BLOCK_TRANSACTION, true);
						}
					} else {
						sosStatus.put(PretupsI.BLOCK_TRANSACTION, false);
						sosStatus.put(PretupsI.DO_WITHDRAW, false);
					}

				}

			}

		}
		
		LogFactory.printLog(methodName, "Final Return>>>>> " + sosStatus.toString() , log);
		return sosStatus;

	}
	/**
	 * 
	 * @param con
	 * @param userTransferCountsVO
	 * @param channelTransferVO
	 * @return statusAndAmount
	 * @throws BTSLBaseException
	 */
	public static Map<String, Object> checkLRstatusAndAmount(Connection con,UserTransferCountsVO userTransferCountsVO,ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "checkLRstatusAndAmount";
		LogFactory.printLog(methodName, "userTransferCountsVO"+ userTransferCountsVO+"channelTransferVO"+channelTransferVO, log);
		final Map<String, Object> lrstatus = new HashMap();


		/* Check Network Level prerferences */
		if ((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LR_ENABLED, channelTransferVO.getNetworkCode())){
			/* Check user level value for LR Status if not null LR Pending perform withdraw*/
			if (!BTSLUtil.isNullString(userTransferCountsVO.getLastLrStatus()) && userTransferCountsVO.getLastLrStatus().equalsIgnoreCase(PretupsI.LAST_LR_PENDING_STATUS)) {
				ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserForSOS(con,channelTransferVO.getToUserID(),channelTransferVO.getProductType(),channelTransferVO.getProductCode(),PretupsI.LR_REQUEST_TYPE);
				if ((channelTransferVO.getTransferMRP() + channelUserVO.getBalance()) > channelUserVO.getLrTransferAmount()) {
					lrstatus.put(PretupsI.DO_WITHDRAW, true);
					lrstatus.put(PretupsI.BLOCK_TRANSACTION, false);
					lrstatus.put(PretupsI.WITHDRAW_AMOUNT,channelUserVO.getLrTransferAmount());


				}


			}}

		return lrstatus;
	}




	/**
	 * Method loadAndValidateProductsforLastRecharge
	 * This method is used to filter the products associated to
	 * the channel user with the products which are comming in the request
	 * The boolean isTransfer determines whether any check on min and max
	 * value for transfer is applicable
	 * 

	 * @param con
	 *            Connection
	 * @param prdCodeQtyMap
	 *            HashMap
	 * @param c2sTransferVO
	 *           C2STransferVO            
	 * @param pchannelUserVO
	 *            ChannelUserVO
	 * @param isTransfer
	 * @return tmpPrdList ArrayList
	 * @throws BTSLBaseException
	 */

	public static ArrayList loadAndValidateProductsforLastRecharge(Connection con,C2STransferVO c2sTransferVO, HashMap prdCodeQtyMap, ChannelUserVO pchannelUserVO, boolean isTransfer) throws BTSLBaseException {
		final String methodName = "loadAndValidateProducts";

        StringBuilder loggerValue= new StringBuilder(); 
        loggerValue.setLength(0);
        loggerValue.append("Entering  : prdCodeQtyMap - ");
        loggerValue.append(prdCodeQtyMap);
        loggerValue.append(", pchannelUserVO = ");
        loggerValue.append(pchannelUserVO);
        loggerValue.append(", isTransfer = ");
        loggerValue.append(isTransfer);
        
		LogFactory.printLog(methodName, loggerValue.toString(), log);


		boolean firstCheckForPrdType = true; // for setting of product type once
		String prdType = null;
		ArrayList productListFromDao = null;
		final ArrayList tmpPrdList = new ArrayList();
		ArrayList commPrdList = null;
		ArrayList filteredProductList = new ArrayList();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		final HashMap prdShortCodeMap = new HashMap();
		final String args[] = { pchannelUserVO.getUserCode() };
		int m,n;

		// filter the products from the n/w's
		// products & the request's products
		final NetworkProductDAO networkDAO = new NetworkProductDAO();
		productListFromDao = networkDAO.loadProductListForXfr(con, null, pchannelUserVO.getNetworkID());

		final Iterator nwItr = (prdCodeQtyMap.keySet()).iterator();
		long mapPrdShortCode;
		while (nwItr.hasNext()) {
			mapPrdShortCode =  (long) nwItr.next();
			for (int i = 0, j = productListFromDao.size(); i < j; i++) {
				networkProductVO = (NetworkProductVO) productListFromDao.get(i);
				if (mapPrdShortCode==networkProductVO.getProductShortCode()) {
					// for checking whether all the products in the request have
					// the same type
					if (firstCheckForPrdType) {
						prdType = networkProductVO.getProductType();
						firstCheckForPrdType = false;
					}
					// creating and populating the ChannelTransferItemsVO from
					// the NetworkProductVO
					channelTransferItemsVO = new ChannelTransferItemsVO();

					if (!prdType.equals(networkProductVO.getProductType())) {
						throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_PRODUCT_TYPE_NOT_SAME);
					}

					channelTransferItemsVO.setProductType(networkProductVO.getProductType());
					channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
					channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
					channelTransferItemsVO.setProductName(networkProductVO.getProductName());
					channelTransferItemsVO.setShortName(networkProductVO.getShortName());
					channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
					channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
					channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
					channelTransferItemsVO.setStatus(networkProductVO.getStatus());
					channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
					channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
					channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
					channelTransferItemsVO.setApprovedQuantity(c2sTransferVO.getLRAmount());
					channelTransferItemsVO.setReceiverCreditQty(c2sTransferVO.getLRAmount());
					channelTransferItemsVO.setNetPayableAmount(c2sTransferVO.getLRAmount());
					channelTransferItemsVO.setPayableAmount(c2sTransferVO.getLRAmount());
					channelTransferItemsVO.setSenderDebitQty(c2sTransferVO.getLRAmount());
					channelTransferItemsVO.setRequiredQuantity(c2sTransferVO.getLRAmount());


					prdShortCodeMap.put(networkProductVO.getProductCode(), channelTransferItemsVO);
					filteredProductList.add(channelTransferItemsVO);
					break;
				}

			}

		}

		final Date currDate = new Date();
		final CommissionProfileDAO commPrDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();

		// load the latest version of commission profile
		final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, pchannelUserVO.getCommissionProfileSetID(), currDate);
        final String commProfileLatestVer = commissionProfileSetVO.getCommProfileVersion();
		if (BTSLUtil.isNullString(commProfileLatestVer)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_LATEST_COMMISSION_PROFILE_ASSOCIATED);
		}

		pchannelUserVO.setCommissionProfileSetVersion(commProfileLatestVer);
		String latestCommProfileVersion = null;
		final Date currentDate = new Date();
		try {
			final CommissionProfileSetVO latestCommissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, pchannelUserVO.getCommissionProfileSetID(), currentDate);
			latestCommProfileVersion = latestCommissionProfileSetVO.getCommProfileVersion();
		} catch (BTSLBaseException bex) {
			if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED, args);
			}
			loggerValue.setLength(0);
			loggerValue.append("BTSLBaseException ");
			loggerValue.append(bex.getMessage());
			log.error("loadO2CXfrProductList",  loggerValue );
			throw bex;
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		
		String type = (TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		commPrdList = commPrDAO.loadCommissionProfileProductsList(con, pchannelUserVO.getCommissionProfileSetID(), pchannelUserVO.getCommissionProfileSetVersion(), type, paymentMode);
		if (commPrdList == null || commPrdList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_NO_COMMISION_PRODUCT_ASSOCIATED);
		}

		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final ArrayList commissionProfileProductList = commissionProfileDAO .loadCommissionProfileProductsList(con, pchannelUserVO.getCommissionProfileSetID(), latestCommProfileVersion, type, paymentMode);

		// if list is empty send the error message
		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_WITH_COMM_PROFILE, args);
		}

		// filterize the product list with the products of the commission
		// profile products
		CommissionProfileProductsVO commissionProfileProductsVO = null;
		for (int i = 0,  j = commissionProfileProductList.size(); i < j; i++) {
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for ( m = 0,  n = filteredProductList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) filteredProductList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileProductID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}
			}

		}
		CommissionProfileDeatilsVO commissionProfileDeatilsVO = commissionProfileTxnDAO.loadCommissionProfileDetails(con, channelTransferItemsVO.getCommProfileProductID());
		if (commissionProfileDeatilsVO!=null)
		{
			channelTransferItemsVO.setCommProfileDetailID(commissionProfileDeatilsVO.getCommProfileDetailID());
		}
		else 
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_MVD, args);


		for (m = 0, n = filteredProductList.size(); m < n; m++) {

			if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
				filteredProductList.remove(m);
				m--;
				n--;
			}
		}
		// if list size is zero than send the error message.
		if (filteredProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_SAME_PRODUCT_IN_COMMPROFILE_NTWMAPPING, args);
		}




		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting : tmpPrdList :: " );
			loggerValue.append(tmpPrdList.size());
			log.debug(methodName, loggerValue );
		}
		loggerValue.setLength(0);
		loggerValue.append("Exiting : tmpPrdList :: ");
		loggerValue.append(tmpPrdList.size());
		LogFactory.printLog(methodName,  loggerValue.toString() , log);

		return filteredProductList;
	}

	/**
	 * Method to increase the Channel user OTF counts and
	 * values
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_isCheckThresholds
	 *            boolean
	 * @throws BTSLBaseException
	 */
	public static void increaseUserOTFCounts(Connection con, C2STransferVO c2sTransferVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
		final String methodName = "increaseUserOTFCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered C2STransferVO= ");
			loggerValue.append(c2sTransferVO);
			log.debug(methodName,loggerValue );
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		ArrayList commProfileList = null;
		AdditionalProfileDeatilsVO additionalProfileDetailsVO = null;
		UserOTFCountsVO userOTFCountsVO1= null;
		boolean timeFlag=true;




		final boolean isLockRecordForUpdate = true;
		UserOTFCountsVO userOTFCountsVO = null;
		List<AdditionalProfileDeatilsVO> otfSlabList;
		try {    
			DateFormat df = new SimpleDateFormat(PretupsI.DATE_FORMAT);
			Date dateobj = new Date();
			Date currentDate = df.parse(df.format(dateobj));
			Date date = new Date();

			if(PretupsI.YES.equals(c2sTransferVO.getDifferentialAllowedForService()))
			{

				String commissionProfileSetID = channelUserVO.getCommissionProfileSetID();

				CommissionProfileSetVO commissionProfileSetVO = (CommissionProfileSetVO) CommissionProfileCache.getObject(commissionProfileSetID, date);
				String commissionProfileVersion = commissionProfileSetVO.getCommProfileVersion();

				commProfileList = (ArrayList) CommissionProfileCache.getObject(commissionProfileSetID, commissionProfileVersion);
				if (commProfileList == null) {
					CommissionProfileCache.loadCommissionProfilesDetails(commissionProfileSetID, commissionProfileVersion);
					commProfileList = (ArrayList) CommissionProfileCache.getObject(commissionProfileSetID, commissionProfileVersion);
				}

				for (Object additionalProfileSlabVO : commProfileList) {
					if (additionalProfileSlabVO instanceof AdditionalProfileDeatilsVO) {

						if (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getStartRange() <= c2sTransferVO.getRequestedAmount() && ((AdditionalProfileDeatilsVO) additionalProfileSlabVO)
								.getEndRange() >= c2sTransferVO.getRequestedAmount()) {

							if (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getGatewayCode().equals(c2sTransferVO.getRequestGatewayCode()) ||
									PretupsI.ALL.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getGatewayCode())){
								if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHNL_RECHARGE) || c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL))
								{
									if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE))
								{
									if(PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHNL_BILLPAY))
								{
									if(PretupsI.SERVICE_TYPE_CHNL_BILLPAY.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR))
								{
									if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE))
								{
									if(PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN))
								{
									if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
								}
							    else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVD))
                                {
                                    if(PretupsI.SERVICE_TYPE_EVD.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
                                        additionalProfileDetailsVO = ((AdditionalProfileDeatilsVO) additionalProfileSlabVO);
                                }
							}
						}
					}

				}

				if(additionalProfileDetailsVO!=null)
				{
					c2sTransferVO.setTargetAchieved(false);
					Date tempDate=currentDate;
					tempDate = DateUtils.truncate(tempDate, Calendar.DATE);
				
						/*if(!BTSLUtil.isNullString(additionalProfileDetailsVO.getApplicableFromAdditional())){		    		

							if(!(BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableFromAdditional()).before(tempDate) 
									|| BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableFromAdditional()).equals(tempDate))){  
								timeFlag = false;
							}
						}

						if(!BTSLUtil.isNullString(additionalProfileDetailsVO.getApplicableToAdditional())){

							if(!((BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableToAdditional()).after(tempDate) 
									|| BTSLUtil.getDateFromDateString(additionalProfileDetailsVO.getApplicableToAdditional()).equals(tempDate)))){
								timeFlag = false;
							}
						}
						
						if(!(BTSLUtil.isNullString(additionalProfileDetailsVO.getAdditionalCommissionTimeSlab())) || !("").equals(additionalProfileDetailsVO.getAdditionalCommissionTimeSlab()))
						{
							if(!BTSLUtil.timeRangeValidation(additionalProfileDetailsVO.getAdditionalCommissionTimeSlab(), new Date()))
							{
								timeFlag=false;
							}
						}*/

					if(!(additionalProfileDetailsVO.getOtfApplicableFrom()== null && additionalProfileDetailsVO.getOtfApplicableTo()==null))
					{
						if(!((additionalProfileDetailsVO.getOtfApplicableFrom().before(currentDate) 
								|| additionalProfileDetailsVO.getOtfApplicableFrom().equals(currentDate)) && (additionalProfileDetailsVO.getOtfApplicableTo().after(currentDate) 
										|| additionalProfileDetailsVO.getOtfApplicableTo().equals(currentDate))))
						{
							timeFlag = false;
						}
					}


					if(!BTSLUtil.isNullString(additionalProfileDetailsVO.getOtfTimeSlab()))
					{
						if(!BTSLUtil.timeRangeValidation(additionalProfileDetailsVO.getOtfTimeSlab(), new Date()))
						{
							timeFlag=false;
						}
					}
					boolean otfFlag=true;
					if(additionalProfileDetailsVO.getOtfApplicableFrom()== null && additionalProfileDetailsVO.getOtfApplicableTo()==null && timeFlag){
						otfFlag=false;
					}
					if(timeFlag  && otfFlag)
					{

						boolean order= true;
						boolean addnl= true;
						otfSlabList= commissionProfileDAO.getAddCommOtfDetails(con, additionalProfileDetailsVO.getAddCommProfileDetailID(), order);
						
						if(!otfSlabList.isEmpty())
						{
							userOTFCountsVO = userTransferCountsDAO.loadUserOTFCounts(con, c2sTransferVO.getSenderID(),additionalProfileDetailsVO.getAddCommProfileDetailID(), addnl);
							if(userOTFCountsVO!=null)
							{
								boolean flag = true; 
								userOTFCountsVO1 = new UserOTFCountsVO();
								for (AdditionalProfileDeatilsVO additionalProfileOTFSlabVO : otfSlabList)
								{
									if(PretupsI.OTF_TYPE_COUNT.equals(additionalProfileDetailsVO.getOtfType()))
									{
										if (userOTFCountsVO.getOtfCount() + 1 >= additionalProfileOTFSlabVO.getOtfValue())
										{flag = false; 
											userOTFCountsVO1.setAdnlComOTFDetailId(additionalProfileOTFSlabVO.getAddCommProfileOTFDetailID());
											userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
											userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
										}
										if (userOTFCountsVO.getOtfCount() >= additionalProfileOTFSlabVO.getOtfValue())
										{flag = false; 
											userOTFCountsVO1.setAdnlComOTFDetailId(additionalProfileOTFSlabVO.getAddCommProfileOTFDetailID());
											userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
											userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
										}
									}
									if(PretupsI.OTF_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getOtfType()))
									{
										if (userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount() >= additionalProfileOTFSlabVO.getOtfValue())
										{flag = false; 
											userOTFCountsVO1.setAdnlComOTFDetailId(additionalProfileOTFSlabVO.getAddCommProfileOTFDetailID());
											userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
											userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
										}
										if (userOTFCountsVO.getOtfValue() >= additionalProfileOTFSlabVO.getOtfValue())
										{flag = false; 
											userOTFCountsVO1.setAdnlComOTFDetailId(additionalProfileOTFSlabVO.getAddCommProfileOTFDetailID());
											userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
											userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
										}
									}
								}
								if(flag){
									userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
									userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount());
									userOTFCountsVO1.setAdnlComOTFDetailId(userOTFCountsVO.getAdnlComOTFDetailId());
								}
							}
							else
							{
								userOTFCountsVO1 = new UserOTFCountsVO();
								userOTFCountsVO1.setOtfCount(1);
								userOTFCountsVO1.setOtfValue(c2sTransferVO.getRequestedAmount());
								for (AdditionalProfileDeatilsVO additionalProfileOTFSlabVO : otfSlabList)
								{
									if(PretupsI.OTF_TYPE_COUNT.equals(additionalProfileDetailsVO.getOtfType()))
									{
										if (1 <= additionalProfileOTFSlabVO.getOtfValue())
											userOTFCountsVO1.setAdnlComOTFDetailId(additionalProfileOTFSlabVO.getAddCommProfileOTFDetailID());
									}
									if(PretupsI.OTF_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getOtfType()))
									{
										if (c2sTransferVO.getRequestedAmount() <= additionalProfileOTFSlabVO.getOtfValue())
											userOTFCountsVO1.setAdnlComOTFDetailId(additionalProfileOTFSlabVO.getAddCommProfileOTFDetailID());
									}
								}
								if(userOTFCountsVO1.getAdnlComOTFDetailId()==null){
									userOTFCountsVO1.setAdnlComOTFDetailId(((AdditionalProfileDeatilsVO)otfSlabList.get(0)).getAddCommProfileOTFDetailID());
								}
							}
						}
						
						userOTFCountsVO1.setAddnl(addnl);
						userOTFCountsVO1.setUserID(c2sTransferVO.getSenderID());
						c2sTransferVO.setUserOTFCountsVO(userOTFCountsVO1);
						c2sTransferVO.setOtfApplicable(PretupsI.YES);
						
						//For target based messages
						if(!otfSlabList.isEmpty() && userOTFCountsVO1.getAdnlComOTFDetailId()!=null)
						{
							for (AdditionalProfileDeatilsVO additionalProfileOTFSlabVO : otfSlabList)
							{

								if (userOTFCountsVO!=null)
								{
									if(PretupsI.OTF_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getOtfType()))
									{
										if(userOTFCountsVO.getOtfValue() < additionalProfileOTFSlabVO.getOtfValue()  && additionalProfileOTFSlabVO.getOtfValue()  <= userOTFCountsVO.getOtfValue() + c2sTransferVO.getRequestedAmount())
										{
											c2sTransferVO.setTargetAchieved(true);
										}
									}
									else if(PretupsI.OTF_TYPE_COUNT.equals(additionalProfileDetailsVO.getOtfType()))
									{
										if(userOTFCountsVO.getOtfCount() < additionalProfileOTFSlabVO.getOtfValue()  && additionalProfileOTFSlabVO.getOtfValue()  <= userOTFCountsVO.getOtfCount() + 1)
										{
											c2sTransferVO.setTargetAchieved(true);
										}
									}

								}
								else
								{
									if(PretupsI.OTF_TYPE_AMOUNT.equals(additionalProfileDetailsVO.getOtfType()))
									{
										if(additionalProfileOTFSlabVO.getOtfValue()  <= c2sTransferVO.getRequestedAmount())
										{
											c2sTransferVO.setTargetAchieved(true);
										}
									}
									else if(PretupsI.OTF_TYPE_COUNT.equals(additionalProfileDetailsVO.getOtfType()))
									{
										if(additionalProfileOTFSlabVO.getOtfValue()  <= 1)
										{
											c2sTransferVO.setTargetAchieved(true);
										}
									}
								}

							}
						}
					}
				}
			}

		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue);
			
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[increaseUserOTFCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}


	/**
	 * Method to decrease the Channel user OTF counts and
	 * values
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_isCheckThresholds
	 *            boolean
	 * @throws BTSLBaseException
	 */
	public static void decreaseUserOTFCounts(Connection con, C2STransferVO c2sTransferVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
		final String methodName = "decreaseUserOTFCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered C2STransferVO= ");
			loggerValue.append(c2sTransferVO);
			log.debug(methodName,loggerValue );
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		ArrayList commProfileList = null;
		AdditionalProfileDeatilsVO additionalProfileDetailsVO = null;
		Date currentDate = new Date();
		boolean delete=false;
		UserOTFCountsVO userOTFCountsVO01;
		UserOTFCountsVO userOTFCountsVO02;
		List<UserOTFCountsVO> userOtfCountsList;
		try {   
			
			String commissionProfileSetID = channelUserVO.getCommissionProfileSetID();
			CommissionProfileSetVO commissionProfileSetVO = (CommissionProfileSetVO) CommissionProfileCache.getObject(commissionProfileSetID, currentDate);
			String commissionProfileVersion = commissionProfileSetVO.getCommProfileVersion();

			commProfileList = (ArrayList) CommissionProfileCache.getObject(commissionProfileSetID, commissionProfileVersion);
			if (commProfileList == null) {
				CommissionProfileCache.loadCommissionProfilesDetails(commissionProfileSetID, commissionProfileVersion);
				commProfileList = (ArrayList) CommissionProfileCache.getObject(commissionProfileSetID, commissionProfileVersion);
			}
			String gatewayCode = "";
			if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)){
				gatewayCode = c2sTransferVO.getGatewayCodeForReversal();
			}else{
				gatewayCode = c2sTransferVO.getRequestGatewayCode();
			}

			for (Object additionalProfileSlabVO : commProfileList) {
				if (additionalProfileSlabVO instanceof AdditionalProfileDeatilsVO) {

					if (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getStartRange() <= c2sTransferVO.getRequestedAmount() && ((AdditionalProfileDeatilsVO) additionalProfileSlabVO)
							.getEndRange() >= c2sTransferVO.getRequestedAmount()) {
						if (((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getGatewayCode().equals(gatewayCode) ||
								PretupsI.ALL.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getGatewayCode()))
						{
							if(additionalProfileSlabVO!=null)
							{
								if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHNL_RECHARGE) || c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL))
								{
									if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = (AdditionalProfileDeatilsVO) additionalProfileSlabVO;
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE))
								{
									if(PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = (AdditionalProfileDeatilsVO) additionalProfileSlabVO;
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHNL_BILLPAY))
								{
									if(PretupsI.SERVICE_TYPE_CHNL_BILLPAY.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = (AdditionalProfileDeatilsVO) additionalProfileSlabVO;
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR))
								{
									if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = (AdditionalProfileDeatilsVO) additionalProfileSlabVO;
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE))
								{
									if(PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = (AdditionalProfileDeatilsVO) additionalProfileSlabVO;
								}
								else if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN))
								{
									if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN.equals(((AdditionalProfileDeatilsVO) additionalProfileSlabVO).getServiceType()))
										additionalProfileDetailsVO = (AdditionalProfileDeatilsVO) additionalProfileSlabVO;
								}

							}
						}
					}
				}

			}

			if(additionalProfileDetailsVO!=null)
			{

				boolean addnl= true;
				userOtfCountsList=userTransferCountsDAO.loadUserOTFCountsList(con, c2sTransferVO.getSenderID(),additionalProfileDetailsVO.getAddCommProfileDetailID(), addnl);
				Collections.sort(userOtfCountsList);
				Collections.reverse(userOtfCountsList);
				if(!userOtfCountsList.isEmpty())
				{
					if(userOtfCountsList.size()>1)
					{
						for(int x = userOtfCountsList.size()-1; x>=1 ;x--)
						{
							userOTFCountsVO01 = userOtfCountsList.get(x-1);
							userOTFCountsVO02 = userOtfCountsList.get(x);
							if(userOTFCountsVO01.getOtfValue() > userOTFCountsVO02.getOtfValue()+c2sTransferVO.getRequestedAmount())
							{
								//execute update query
								userOTFCountsVO01.setOtfValue(userOTFCountsVO01.getOtfValue()-c2sTransferVO.getRequestedAmount());
								userOTFCountsVO01.setOtfCount(userOTFCountsVO01.getOtfCount()-1);
								userOTFCountsVO01.setAddnl(addnl);
								c2sTransferVO.setUserOTFCountsVO(userOTFCountsVO01);
								break;
							}
							else
							{
								userTransferCountsDAO.deleteUserOTFCounts(con, userOTFCountsVO01, true, addnl);
								userOTFCountsVO02.setOtfValue(userOTFCountsVO01.getOtfValue()- c2sTransferVO.getRequestedAmount());
								userOTFCountsVO02.setOtfCount(userOTFCountsVO01.getOtfCount()-1);
								userOTFCountsVO02.setAddnl(addnl);
								c2sTransferVO.setUserOTFCountsVO(userOTFCountsVO02);
								break;
							}
						}
					}
					else
					{
						userOTFCountsVO01 = userOtfCountsList.get(0);
						userOTFCountsVO01.setOtfValue(userOTFCountsVO01.getOtfValue()- c2sTransferVO.getRequestedAmount());
						userOTFCountsVO01.setOtfCount(userOTFCountsVO01.getOtfCount()-1);
						userOTFCountsVO01.setAddnl(addnl);
						c2sTransferVO.setUserOTFCountsVO(userOTFCountsVO01);
					}
				}
				c2sTransferVO.setOtfCountsDecreased(true);
			}
		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(c2sTransferVO.getTransferID());
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue );
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[decreaseUserOTFCounts]",
					c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),  loggerValue.toString());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}



	/**
	 * Method to increase the Channel user OTF counts for O2C and
	 * values
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */

	public static void increaseOptOTFCounts(Connection con, ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "increaseOptOTFCounts";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered ChannelTransferVO= ");
			loggerValue.append(channelTransferVO);
			log.debug(methodName,loggerValue);
		}

		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		ChannelTransferItemsVO channelTransferItemsVO = null;
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();


		UserOTFCountsVO userOTFCountsVO1= null;
		boolean timeFlag=true;


		final boolean isLockRecordForUpdate = true;
		UserOTFCountsVO userOTFCountsVO = null;
		OTFDetailsVO otfDetailsVO = null;
		List<OTFDetailsVO> otfSlabList;
		channelTransferVO.setTargetAcheived(false);

		try {            

			DateFormat df = new SimpleDateFormat(PretupsI.DATE_FORMAT);
			Date dateobj = new Date();
			Date currentDate = df.parse(df.format(dateobj));


			final ArrayList transferItemsList = channelTransferVO.getChannelTransferitemsVOList();
			final ArrayList transferItemsListForOTF = new ArrayList();
			boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
			boolean PAYMENT_MODE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD);
			
			String type = (TRANSACTION_TYPE_ALWD)?channelTransferVO.getType():PretupsI.ALL;
			String paymentMode = (PAYMENT_MODE_ALWD &&  (PretupsI.TRANSFER_TYPE_O2C.equals(type)|| PretupsI.TRANSFER_TYPE_C2C.equals(type)))?channelTransferVO.getPayInstrumentType():PretupsI.ALL;
			final ArrayList list= commissionProfileDAO.loadProductListWithTaxes(con, channelTransferVO.getCommProfileSetId(), channelTransferVO.getCommProfileVersion(), transferItemsList, type, paymentMode);

			for (int i = 0, k = list.size(); i < k; i++) {
				channelTransferItemsVO = new ChannelTransferItemsVO();
				ChannelTransferItemsVO itemsVO = (ChannelTransferItemsVO) list.get(i);
				channelTransferItemsVO.setCommProfileDetailID(itemsVO.getCommProfileDetailID());
				channelTransferItemsVO.setRequiredQuantity(itemsVO.getRequiredQuantity());
				channelTransferItemsVO.setProductCode(itemsVO.getProductCode());
				channelTransferItemsVO.setRequestedQuantity(itemsVO.getRequestedQuantity());

				OtfProfileVO otfProfileVO = commissionProfileTxnDAO.loadOtfProfileDetails(con, channelTransferVO.getCommProfileSetId(), channelTransferVO.getCommProfileVersion(), channelTransferItemsVO.getProductCode());
				if(itemsVO.isReversalRequest())
				{
					otfProfileVO = null;
				}
				if(otfProfileVO!=null)
				{

					if(!(otfProfileVO.getOtfApplicableFromDate()== null && otfProfileVO.getOtfApplicableToDate()==null))
					{
						if(!((otfProfileVO.getOtfApplicableFromDate().before(currentDate) 
								|| otfProfileVO.getOtfApplicableFromDate().equals(currentDate)) && (otfProfileVO.getOtfApplicableToDate().after(currentDate) 
										|| otfProfileVO.getOtfApplicableToDate().equals(currentDate))))
						{
							timeFlag = false;
						}
					}

					if(otfProfileVO.getOtfTimeSlab()!=null || "".equals(otfProfileVO.getOtfTimeSlab()))
					{
						if(!BTSLUtil.timeRangeValidation(otfProfileVO.getOtfTimeSlab(), new Date()))
						{
							timeFlag=false;
						}
					}  
					if(timeFlag)
					{
						boolean order= true;
						boolean addnl= false;	
						String[] msg = null;
						otfSlabList= commissionProfileDAO.getBaseCommOtfDetails(con, otfProfileVO.getCommProfileOtfID(), "COMM", order);

						if(!otfSlabList.isEmpty())
						{
							userOTFCountsVO = userTransferCountsDAO.loadUserOTFCounts(con, channelTransferVO.getToUserID(),otfProfileVO.getCommProfileOtfID(), addnl);
							if(userOTFCountsVO!=null)
							{
								boolean flag = true; 
								userOTFCountsVO1= new UserOTFCountsVO();
								otfDetailsVO = null;
								for (OTFDetailsVO otfDetailsVO1 : otfSlabList)
								{
									if (userOTFCountsVO.getOtfValue() + channelTransferItemsVO.getRequiredQuantity() >= otfDetailsVO1.getOtfValueLong())
									{	
										flag = false;
										userOTFCountsVO1.setBaseComOTFDetailId(otfDetailsVO1.getOtfDetailID());
										userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
										userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + channelTransferItemsVO.getRequiredQuantity());
									}
									if (userOTFCountsVO.getOtfValue() >= otfDetailsVO1.getOtfValueLong())
									{	
										flag = false;
										userOTFCountsVO1.setBaseComOTFDetailId(otfDetailsVO1.getOtfDetailID());
										userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
										userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + channelTransferItemsVO.getRequiredQuantity());
										otfDetailsVO =  otfDetailsVO1;
									}
									if(userOTFCountsVO.getOtfValue() < otfDetailsVO1.getOtfValueLong()  && otfDetailsVO1.getOtfValueLong()  <= userOTFCountsVO.getOtfValue() + channelTransferItemsVO.getRequiredQuantity()){
										channelTransferVO.setTargetAcheived(true);
										msg = new String[2];
										msg[0]=PretupsI.AMOUNT_TYPE_AMOUNT+":"+PretupsBL.getDisplayAmount(otfDetailsVO1.getOtfValueLong());
										if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(otfDetailsVO1.getOtfTypePctOrAMt()))
										{
											msg[1]=otfDetailsVO1.getOtfTypePctOrAMt()+":"+PretupsBL.getDisplayAmount(otfDetailsVO1.getOtfRateDouble());
										}
										else
										{
											msg[1]=otfDetailsVO1.getOtfTypePctOrAMt()+":"+otfDetailsVO1.getOtfRateDouble();
										}
										channelTransferVO.setMessageArgumentList(msg);
									}
								}
								if(flag){
									userOTFCountsVO1.setOtfCount(userOTFCountsVO.getOtfCount() + 1);
									userOTFCountsVO1.setOtfValue(userOTFCountsVO.getOtfValue() + channelTransferItemsVO.getRequiredQuantity());
									userOTFCountsVO1.setBaseComOTFDetailId(userOTFCountsVO.getBaseComOTFDetailId());
								}
								if(otfDetailsVO!=null)
									if(userOTFCountsVO.getOtfCount() >= otfSlabList.get(0).getOtfValueLong()/100 ) {

									calculateOTFforOPT(itemsVO, otfDetailsVO);
									channelTransferItemsVO.setOtfApplicable(PretupsI.YES);
									itemsVO.setOtfApplicable(PretupsI.YES);
									}
									else {
										channelTransferItemsVO.setOtfApplicable(PretupsI.NO);
										itemsVO.setOtfApplicable(PretupsI.NO);
										}							}
							else
							{
								userOTFCountsVO1 = new UserOTFCountsVO();
								OTFDetailsVO otfSlabVO1 = null;
								userOTFCountsVO1.setOtfCount(1);
								userOTFCountsVO1.setOtfValue(channelTransferItemsVO.getRequiredQuantity());
								for (OTFDetailsVO otfSlabVO : otfSlabList)
								{
									if (channelTransferItemsVO.getRequiredQuantity() >= otfSlabVO.getOtfValueLong())
									{
										userOTFCountsVO1.setBaseComOTFDetailId(otfSlabVO.getOtfDetailID());
										channelTransferVO.setTargetAcheived(true);
										msg = new String[2];
										msg[0]=PretupsI.AMOUNT_TYPE_AMOUNT+":"+PretupsBL.getDisplayAmount(otfSlabVO.getOtfValueLong());
										if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(otfSlabVO.getOtfTypePctOrAMt()))
										{
											msg[1]=otfSlabVO.getOtfTypePctOrAMt()+":"+PretupsBL.getDisplayAmount(otfSlabVO.getOtfRateDouble());
										}
										else
										{
											msg[1]=otfSlabVO.getOtfTypePctOrAMt()+":"+otfSlabVO.getOtfRateDouble();
										}
										channelTransferVO.setMessageArgumentList(msg);
										otfSlabVO1 = otfSlabVO;
									}
								}
								if(userOTFCountsVO1.getBaseComOTFDetailId()==null){
									userOTFCountsVO1.setBaseComOTFDetailId(((OTFDetailsVO)otfSlabList.get(0)).getOtfDetailID());
								}
								if(otfSlabVO1!=null){
									itemsVO.setOtfTypePctOrAMt(otfSlabVO1.getOtfTypePctOrAMt());
									itemsVO.setOtfRate(otfSlabVO1.getOtfRateDouble());
								}
							}
						}

						if(!BTSLUtil.isNullObject(userOTFCountsVO1)){
							userOTFCountsVO1.setAddnl(addnl);
							userOTFCountsVO1.setUserID(channelTransferVO.getToUserID());
							channelTransferVO.setUserOTFCountsVO(userOTFCountsVO1);
							itemsVO.setUserOTFCountsVO(userOTFCountsVO1);
							
								
							
						}
					}
					else
					{
						channelTransferItemsVO.setOtfApplicable(PretupsI.NO);
						itemsVO.setOtfApplicable(PretupsI.NO);
					}
				}
				else
				{
					channelTransferItemsVO.setOtfApplicable(PretupsI.NO);
					itemsVO.setOtfApplicable(PretupsI.NO);
				}
				transferItemsListForOTF.add(channelTransferItemsVO);
			}
			channelTransferVO.setChannelTransferitemsVOListforOTF(transferItemsListForOTF);
		}

		catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception p_transferID : ");
			loggerValue.append(channelTransferVO.getTransferID());
			loggerValue.append(", Exception : ");
			loggerValue.append(e.getMessage());
			log.error(methodName, loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[increaseOptOTFCounts]",
					channelTransferVO.getTransferID(), channelTransferVO.getSenderLoginID(), channelTransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		} 
	} 

	/**
	 * Method to calculate OTF for OPT and controls the flow of process
	 * 
	 * @param con
	 * @param channelTransferItemsVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	private static void calculateOTFforOPT(ChannelTransferItemsVO channelTransferItemsVO, OTFDetailsVO otfDetailsVO) throws BTSLBaseException {
		final String methodName = "calculateOTFforOPT";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered channelTransferItemsVO:" + channelTransferItemsVO +"commissionProfileDeatilsVO:"+otfDetailsVO);
		}
		try {

			long otfAmount;

			otfAmount = calculatorI.calculateOTFComm(otfDetailsVO.getOtfTypePctOrAMt(), otfDetailsVO.getOtfRateDouble(), channelTransferItemsVO.getRequiredQuantity());

			if (log.isDebugEnabled()) {
				log.debug(methodName, "OTF amount=" + otfAmount);
			}
			if (PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(otfDetailsVO.getOtfTypePctOrAMt())) {

				channelTransferItemsVO.setOtfTypePctOrAMt(PretupsI.AMOUNT_TYPE_AMOUNT);

			}
			else if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(otfDetailsVO.getOtfTypePctOrAMt()))
			{
				channelTransferItemsVO.setOtfTypePctOrAMt(PretupsI.AMOUNT_TYPE_PERCENTAGE);
			}
			channelTransferItemsVO.setOtfAmount(otfAmount);
			channelTransferItemsVO.setOtfRate(otfDetailsVO.getOtfRateDouble());

		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName ,e);
			throw new BTSLBaseException("ChannelTransferBL", methodName, "");
		}
	}

	/**
	 * Method to decrease the Channel user OTF counts and
	 * values for O2C and c2C
	 * 
	 * @param con
	 * @param c2sTransferVO
	 * @param p_isCheckThresholds
	 *            boolean
	 * @throws BTSLBaseException
	 */
	public static void decreaseOptOTFCounts(Connection con, ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "decreaseOptOTFCounts";
		if (log.isDebugEnabled()) {
			log.debug(methodName,
					"Entered ChannelTransferVO= " + channelTransferVO);
		}
		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		final ArrayList transferItemsListForOTF = new ArrayList();

		UserOTFCountsVO userOTFCountsVO1= null;
		List<UserOTFCountsVO> userOtfCountsList;
		boolean delete= false;
		boolean decrease = false;


		final boolean isLockRecordForUpdate = true;
		UserOTFCountsVO userOTFCountsVO01;
		UserOTFCountsVO userOTFCountsVO02;

		try {   
			if(channelTransferVO.getChannelTransferitemsVOListforOTF() == null)
			{
				channelTransferVO.setChannelTransferitemsVOListforOTF(channelTransferVO.getChannelTransferitemsVOList());
			}
			final ArrayList transferItemsList = channelTransferVO.getChannelTransferitemsVOListforOTF();
			boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
			boolean PAYMENT_MODE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD);
			String type = (TRANSACTION_TYPE_ALWD)?channelTransferVO.getType():PretupsI.ALL;
			String paymentMode = (PAYMENT_MODE_ALWD && (PretupsI.TRANSFER_TYPE_O2C.equals(type)|| PretupsI.TRANSFER_TYPE_C2C.equals(type)))?channelTransferVO.getPayInstrumentType():PretupsI.ALL;
			final ArrayList list= commissionProfileDAO.loadProductListWithTaxes(con, channelTransferVO.getCommProfileSetId(), channelTransferVO.getCommProfileVersion(), transferItemsList, type, paymentMode);

			for (int i = 0, k = list.size(); i < k; i++) {
				ChannelTransferItemsVO itemsVO = (ChannelTransferItemsVO) list.get(i);
				channelTransferItemsVO.setCommProfileDetailID(itemsVO.getCommProfileDetailID());
				channelTransferItemsVO.setProductCode(itemsVO.getProductCode());
				if(channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN )|| channelTransferVO.getTransferType().equals(PretupsI.C2C_MODULE) || (channelTransferVO.getTransferType().equals(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION ) && !channelTransferVO.getType().equals(PretupsI.O2C_MODULE)))
				{
					channelTransferItemsVO.setRequiredQuantity(itemsVO.getApprovedQuantity());

				}
				else
				{
					channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(itemsVO.getApprovedQuantity()));
				}
				if(PretupsI.YES.equals(itemsVO.isOtfApplicable()))
				{
					decrease = true;
				}

				OtfProfileVO otfProfileVO = commissionProfileTxnDAO.loadOtfProfileDetails(con, channelTransferVO.getCommProfileSetId(), channelTransferVO.getCommProfileVersion(), channelTransferItemsVO.getProductCode());
				if(otfProfileVO!=null && decrease)
				{

					boolean addnl= false;
					String userId= channelTransferVO.getToUserID();
					if(userId.equals(PretupsI.OPT_MODULE))
					{
						userId = channelTransferVO.getFromUserID();
					}

					
					userOtfCountsList = userTransferCountsDAO.loadUserOTFCountsList(con, userId,otfProfileVO.getCommProfileOtfID(), addnl);
				    Collections.sort(userOtfCountsList);
					Collections.reverse(userOtfCountsList);
					if(!userOtfCountsList.isEmpty())
					{
						if(userOtfCountsList.size()>1)
						{
							for(int x = userOtfCountsList.size()-1; x>=1 ;x--)
							{
								userOTFCountsVO01 = userOtfCountsList.get(x-1);
								userOTFCountsVO02 = userOtfCountsList.get(x);
								if(userOTFCountsVO01.getOtfValue() > userOTFCountsVO02.getOtfValue()+channelTransferItemsVO.getRequiredQuantity())
								{
									//execute update query
									userOTFCountsVO01.setOtfValue(userOTFCountsVO01.getOtfValue()-channelTransferItemsVO.getRequiredQuantity());
									userOTFCountsVO01.setOtfCount(userOTFCountsVO01.getOtfCount()-1);
									userTransferCountsDAO.updateUserOTFCounts(con, userOTFCountsVO01);
									break;
								}
								else
								{
									userTransferCountsDAO.deleteUserOTFCounts(con, userOTFCountsVO01, true, addnl);
									userOTFCountsVO02.setOtfValue(userOTFCountsVO01.getOtfValue()- channelTransferItemsVO.getRequiredQuantity());
									userOTFCountsVO02.setOtfCount(userOTFCountsVO01.getOtfCount()-1);
									userTransferCountsDAO.updateUserOTFCounts(con, userOTFCountsVO02);
									break;
								}
							}
						}
						else
						{
							userOTFCountsVO01 = userOtfCountsList.get(0);
							userOTFCountsVO01.setOtfValue(userOTFCountsVO01.getOtfValue()- channelTransferItemsVO.getRequiredQuantity());
							userOTFCountsVO01.setOtfCount(userOTFCountsVO01.getOtfCount()-1);
							userTransferCountsDAO.updateUserOTFCounts(con, userOTFCountsVO01);
						}
					}
				}


				transferItemsListForOTF.add(channelTransferItemsVO);

			}
			channelTransferVO.setChannelTransferitemsVOListforOTF(transferItemsListForOTF);
		}


		catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			log.error(methodName, "Exception p_transferID : " + channelTransferVO.getTransferID() + ", Exception : " + e.getMessage());
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}


	/**
	 * @param con
	 * @param senderVO
	 * @param msgArr
	 * @param curDate
	 * @param locale
	 * @param p_commProfileID
	 * @param vomsBatchList
	 * @return
	 * @throws BTSLBaseException
	 */
	public static ArrayList<ChannelTransferItemsVO> validateVomsReqstProdsWithDefinedProdsForXFR(Connection con, ChannelUserVO senderVO, String []msgArr, Date curDate, Locale locale, String p_commProfileID,List<VomsBatchVO> vomsBatchList) throws BTSLBaseException {
		final String methodName = "validateReqstProdsWithDefinedProdsForXFR";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered msgarr Length = ");
			loggerValue.append(msgArr.length);
			loggerValue.append(", SenderVO ");
			loggerValue.append(senderVO);
			loggerValue.append(", curDate : ");
			loggerValue.append(curDate);
			loggerValue.append(", p_commProfileID = ");
			loggerValue.append(p_commProfileID);
			log.debug(methodName,loggerValue);
		}

		String product = VOMSI.DEFAULT_PRODUCT_CODE;
		final ArrayList tempProductList = ChannelTransferBL.loadC2CProductsWithTfrRule(con, senderVO.getUserID(), senderVO.getNetworkID(), p_commProfileID,
				curDate, senderVO.getTransferRuleID(), null, false, senderVO.getUserCode(), locale, product, PretupsI.TRANSFER_TYPE_C2C);
		ChannelTransferItemsVO channelTransferItemsVO = null;
		final ArrayList minLessProdList = new ArrayList();
		final ArrayList maxMoreProdList = new ArrayList();
		final ArrayList multipleOfList = new ArrayList();
		KeyArgumentVO keyArgumentVO = null;
		int m, n;
		final ArrayList<ChannelTransferItemsVO> channelTransferItemsList = new ArrayList();
		for (int i = 0, j = tempProductList.size(); i < j; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) tempProductList.get(i);
			/*
			 * To check whether product selected by user exists in his
			 * channelTransferItemsList or not.
			 */
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Inside SystemPreference "+((String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT_CODE))));
			}
			if(((String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT_CODE))).equals(channelTransferItemsVO.getProductCode()))
			{
				String noOfVoms = "";
			
			Double totalAmount = 0.0 ;
			for(VomsBatchVO vomsBatchVO : vomsBatchList){
				boolean exist = false;
				if (product.equals(channelTransferItemsVO.getProductType())){
					 noOfVoms = vomsBatchVO.getQuantity();
					 totalAmount += Long.parseLong(noOfVoms)*Double.parseDouble(vomsBatchVO.getDenomination());
					 exist = true;
				}
				if (!exist) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_DOMAIN_NOT_FOUND);
				}
			}
			if (channelTransferItemsVO.getMinTransferValue() > (PretupsBL.getSystemAmount(totalAmount))) {
				keyArgumentVO = new KeyArgumentVO();
				keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MIN_TRANSFER_SUBKEY);
				final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getMinTransferValue()) };
				keyArgumentVO.setArguments(args);
				minLessProdList.add(keyArgumentVO);
			}else if (channelTransferItemsVO.getMaxTransferValue() < (PretupsBL.getSystemAmount(totalAmount))) {
				keyArgumentVO = new KeyArgumentVO();
				keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_TRANSFER_SUBKEY);
				final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getMaxTransferValue()) };
				keyArgumentVO.setArguments(args);
				maxMoreProdList.add(keyArgumentVO);
			} else if ((PretupsBL.getSystemAmount(totalAmount) % channelTransferItemsVO.getTransferMultipleOf()) != 0) {
				keyArgumentVO = new KeyArgumentVO();
				keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MULTIPLE_OF_SUBKEY);
				final String args[] = { channelTransferItemsVO.getShortName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getTransferMultipleOf()) };
				keyArgumentVO.setArguments(args);
				multipleOfList.add(keyArgumentVO);
			} 
			channelTransferItemsVO.setRequestedQuantity(String.valueOf(totalAmount));
			channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(totalAmount));
			channelTransferItemsVO.setPayableAmount(Double.valueOf((channelTransferItemsVO.getUnitValue() * totalAmount)).longValue());
			channelTransferItemsVO.setNetPayableAmount(Double.valueOf((channelTransferItemsVO.getUnitValue() * totalAmount)).longValue());
			channelTransferItemsList.add(channelTransferItemsVO);
			break;
		}
		}
		if (!BTSLUtil.isNullOrEmptyList(minLessProdList)) {
			final String[] array = { BTSLUtil.getMessage(locale, minLessProdList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MIN_TRANSFER, array);
		} else if (!BTSLUtil.isNullOrEmptyList(maxMoreProdList)) {
			final String[] array = { BTSLUtil.getMessage(locale, maxMoreProdList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_TRANSFER, array);
		} else if (!BTSLUtil.isNullOrEmptyList(multipleOfList)) {
			final String[] array = { BTSLUtil.getMessage(locale, multipleOfList) };
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MULTIPLE_OF, array);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with channelTransferItemsList.size() = ");
			loggerValue.append(channelTransferItemsList.size());
			log.debug(methodName,  loggerValue);
		}
		return channelTransferItemsList;
	}
	
	/**
	 * @param con
	 * @param receiverChannelUserVO
	 * @param p_requestVO
	 * @param curDate
	 * @param receiverUserCode
	 * @throws BTSLBaseException
	 */
	public static void c2cTransferUserValidateReceiver(Connection con,ChannelUserVO receiverChannelUserVO, RequestVO p_requestVO, Date curDate,String receiverUserCode) throws BTSLBaseException {
		String methodName = "c2cTransferUserValidateReceiver";
		final BarredUserDAO barredUserDAO = new BarredUserDAO();
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);
		boolean receiverAllowed = false;
		UserStatusVO receiverStatusVO = null;
		
		// Getting network details
		final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		if (networkPrefixVO == null) {
			if(!p_requestVO.getSourceType().equals("WEB")) {
			throw new BTSLBaseException("ChannelTransferBL", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
					new String[] { receiverUserCode }, null);}
			else {

				throw new BTSLBaseException("ChannelTransferBL", "process",
						"c2c.vouchers.userfromunsupportednetwork.error", 0,
						new String[] { receiverUserCode }, null);
			}
		}
		if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode,
				PretupsI.USER_TYPE_RECEIVER, null)) {
			if(!p_requestVO.getSourceType().equals("WEB")) {
			throw new BTSLBaseException("ChannelTransferBL", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
					new String[] { receiverUserCode }, null);} else {throw new BTSLBaseException("ChannelTransferBL", "process",
							"c2c.vouchers.userbarredasreciver.error", 0,
							new String[] { receiverUserCode }, null);}
		}

		
		// 1. is user exist or not
		// 2. is user active or not
		// 3. is there any applicable commission profile with user or not
		// 4. is user is IN suspended or not if suspended then show error
		// message
		if (receiverChannelUserVO != null) {
			receiverAllowed = false;
			receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverChannelUserVO.getNetworkID(),
					receiverChannelUserVO.getCategoryCode(), receiverChannelUserVO.getUserType(),
					p_requestVO.getRequestGatewayType());
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
			if(!p_requestVO.getSourceType().equals("WEB")) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
			p_requestVO.setMessageArguments(args);
			throw new BTSLBaseException("ChannelTransferBL", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST,
					0, args, null);
			} else {

				p_requestVO.setMessageCode("c2c.vouchers.userdeatilnotfound.error");
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("ChannelTransferBL", "process","c2c.vouchers.userdeatilnotfound.error",
						0, args, null);
			}
		} else if (receiverChannelUserVO.getInSuspend() != null
				&& PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(receiverChannelUserVO.getInSuspend())) {
			if(!p_requestVO.getSourceType().equals("WEB")) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED);
			p_requestVO.setMessageArguments(args);
			throw new BTSLBaseException("ChannelTransferBL", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED, 0, args, null);
			} else {

				p_requestVO.setMessageCode("c2c.vouchers.userdsuspendedasrec.error");
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("ChannelTransferBL", "process",
						"c2c.vouchers.userdsuspendedasrec.error", 0, args, null);
				
			}
		} else if (receiverStatusVO == null) {
			if(!p_requestVO.getSourceType().equals("WEB")) {

			throw new BTSLBaseException("ChannelTransferBL", "process",
					PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);} else {
						throw new BTSLBaseException("ChannelTransferBL", "process",
								"c2c.vouchers.userdsatusnotconfig.error");
					}
		} else if (!receiverAllowed) { 
			if(!p_requestVO.getSourceType().equals("WEB")) {

			p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
			p_requestVO.setMessageArguments(args);
			throw new BTSLBaseException("ChannelTransferBL", "process",
					PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args, null); } else {
						p_requestVO.setMessageCode("c2c.vouchers.usernotallowedaschannelreceiver.error");
						p_requestVO.setMessageArguments(args);
						throw new BTSLBaseException("ChannelTransferBL", "process",
								"c2c.vouchers.usernotallowedaschannelreceiver.error", 0, args, null); 
						
					}
		} else if (receiverChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
			if(!p_requestVO.getSourceType().equals("WEB")) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
			p_requestVO.setMessageArguments(args);
			throw new BTSLBaseException("ChannelTransferBL", "process",
					PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args, null); } else {

						p_requestVO.setMessageCode("c2c.vouchers.commprofnotapp.error");
						p_requestVO.setMessageArguments(args);
						throw new BTSLBaseException("ChannelTransferBL", "process",
								"c2c.vouchers.commprofnotapp.error", 0, args, null); 
						
					}
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited...");
		}
	}
	
	
	/**
	 * @param con
	 * @param userID
	 * @param networkCode
	 * @param p_commProfileSetId
	 * @param currentDate
	 * @param transferRuleID
	 * @param p_forwardPath
	 * @param isFromWeb
	 * @param userNameCode
	 * @param locale
	 * @param p_productType
	 * @param p_txnType
	 * @return
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadC2CProductsWithTfrRule(Connection con, String userID, String networkCode, String p_commProfileSetId, Date currentDate, String transferRuleID, String p_forwardPath, boolean isFromWeb, String userNameCode, Locale locale, String p_productType, String p_txnType) throws BTSLBaseException {
		final String methodName = "loadC2CProductsWithTfrRule";

        StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered UserID : ");
			loggerValue.append(userID);
			loggerValue.append(", NetworkCode : ");
			loggerValue.append(networkCode);
			loggerValue.append(", CommissionProfileSetID : ");
			loggerValue.append(p_commProfileSetId);
			loggerValue.append(", CurrentDate : " );
			loggerValue.append(currentDate);
			loggerValue.append(", transferRuleID : ");
			loggerValue.append(transferRuleID);
			loggerValue.append(", isFromWeb : ");
			loggerValue.append(isFromWeb);
			loggerValue.append(", p_userIDCODE : ");
			loggerValue.append(userNameCode);
			loggerValue.append(", locale = " );
			loggerValue.append(locale);
			loggerValue.append(", p_productType = " );
			loggerValue.append(p_productType);
			log.debug(methodName,loggerValue );
		}
		final ArrayList productList = new ArrayList();
		final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
		final String args[] = { userNameCode };
		final ArrayList prodList = networkProductDAO.loadProductListForXfr(con, p_productType, networkCode);
		/*
		 * 1. check whether product exist or not of the input productType
		 */
		if (prodList.isEmpty()) {
			if (isFromWeb) {
				if (!BTSLUtil.isNullString(p_productType)) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.producttype", 0, new String[] { p_productType }, p_forwardPath);
				}
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.product", p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_EXIST);
		}

		/*
		 * 2.
		 * checking that the status of network product mapping is active and
		 * also construct the new arrayList of
		 * channelTransferItemsVOs containg required list.
		 */
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		int i, j, m, n;
		int prodListSizes=prodList.size();
		for (i = 0, j = prodListSizes; i < j; i++) {
			networkProductVO = (NetworkProductVO) prodList.get(i);
			if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
				channelTransferItemsVO = new ChannelTransferItemsVO();
				channelTransferItemsVO.setProductType(networkProductVO.getProductType());
				channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
				channelTransferItemsVO.setProductName(networkProductVO.getProductName());
				channelTransferItemsVO.setShortName(networkProductVO.getShortName());
				channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
				channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
				channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
				channelTransferItemsVO.setStatus(networkProductVO.getStatus());
				channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
				channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
				channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
				productList.add(channelTransferItemsVO);
			}
		}
		if (productList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.networkproductmapping", p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOTMAPPED_NETWORK);
		}

		final ArrayList errorList = new ArrayList();
		KeyArgumentVO keyArgumentVO = null;
		UserBalancesVO balancesVO = null;
		String errArgs[] = null;
		long currentBalance = 0;
		long previousBalance = 0;
		final long balance = 0;
		String previousProductType = null;
		String currentProductType = null;

		/*
		 * 3. load the latest version of the commission profile set id
		 */
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		String latestCommProfileVersion = null;
		try {
			final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, p_commProfileSetId, currentDate);
			latestCommProfileVersion = commissionProfileSetVO.getCommProfileVersion();
		} catch (BTSLBaseException bex) {
			if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
				if (isFromWeb) {
					throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.commprofilever", 0, args, p_forwardPath);
				}
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED, args);
			}
			log.error("loadO2CXfrProductList", "BTSLBaseException " + bex.getMessage());
			throw bex;
		}

		// if there is no commission profile version exist upto the current date
		// show the error message.
		if (BTSLUtil.isNullString(latestCommProfileVersion)) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.commprofilever", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED, args);
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		
		String type = (TRANSACTION_TYPE_ALWD)?p_txnType:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(con, p_commProfileSetId, latestCommProfileVersion, type, paymentMode);

		// if list is empty send the error message
		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.commprofileproduct", 0,
						new String[] { userNameCode, latestCommProfileVersion }, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_WITH_COMM_PROFILE, args);
		}

		// filterize the product list with the products of the commission
		// profile products
		CommissionProfileProductsVO commissionProfileProductsVO = null;
		int commissionProfileProductListSizes=commissionProfileProductList.size();
		for (i = 0, j = commissionProfileProductListSizes; i < j; i++) {
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileDetailID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}
			}
		}
		for (m = 0, n = productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
			if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
				productList.remove(m);
				m--;
				n--;
			}
		}
		// if list size is zero than send the error message.
		if (productList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.networkcommprofileproduct", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_NO_SAME_PRODUCT_IN_COMMPROFILE_NTWMAPPING, args);
		}

		/*
		 * 4. load the product list associated with the transfer rule.
		 */
		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
		final ArrayList prodWithXfrRuleList = channelTransferRuleDAO.loadProductVOList(con, transferRuleID);
		if (prodWithXfrRuleList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.noproductassigned.transferrule", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTDEFINE, args);
		}
		/*
		 * 5. filter the list with the products associated with the transfer
		 * rule.
		 */
		ListValueVO listValueVO = null;
		final ArrayList c2cXfrProductList = new ArrayList();
		for (i = 0, j = prodWithXfrRuleList.size(); i < j; i++) {
			listValueVO = (ListValueVO) prodWithXfrRuleList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
					c2cXfrProductList.add(channelTransferItemsVO);
				}
			}
		}
		// if list is of size =0. show error message.
		if (c2cXfrProductList.isEmpty()) {
			if (isFromWeb) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transferc2c.nodata.transferrule", 0, args, p_forwardPath);
			}
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH, args);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with c2cXfrProductList.size() = ");
			loggerValue.append(c2cXfrProductList.size());
			loggerValue.append(c2cXfrProductList);
			log.debug(methodName,  loggerValue);
		}
		return c2cXfrProductList;
	}

	public static String generateSaleBatchNumberForDVD(C2STransferVO p_transferVO) throws BTSLBaseException {
		final String methodName = "generateSaleBatchNumberForDVD";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered p_transferVO = " + p_transferVO);
		}
		String tempSaleBatchNumber = null;
		try {
			return tempSaleBatchNumber = calculatorI.formatSaleBatchNumber(PretupsI.SALE_BATCH_NUMBER, IDGenerator.getNextID(PretupsI.SALE_BATCH_NUMBER, BTSLUtil
					.getFinancialYear(), p_transferVO.getNetworkCode(), p_transferVO.getCreatedOn()));
		} catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_DVD);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited ID = " + tempSaleBatchNumber);
			}
		}

	}
	
	/**
	 * Method validateChannelLastTransferMrpSuccessiveBlockTimeout.
	 * This method is to validate the channel user for O2C/C2C transfer
	 * 
	 * @param p_con Connection
	 * @param p_requestVO RequestVO
	 * @param p_channeltrnasferVO ChanneltrnasferVO
	 * @param p_currDate Date
	 * @param p_successiveReqBlockTime 
	 * @throws BTSLBaseException
	 */
	public static void validateChannelLastTransferMrpSuccessiveBlockTimeout(Connection p_con, ChannelTransferVO p_channelTransferVO,Date p_currDate, long p_successiveReqBlockTime) throws BTSLBaseException	
	{
		final String methodName = "validateChannelLastTransferMrpSuccessiveBlockTimeout";
		if (log.isDebugEnabled()) 	{
			log.debug (methodName, "Entered p_channelTransferVO = "+p_channelTransferVO+", p_currDate = "+p_currDate);
		}
		ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		
		//Load preference value for 
		boolean chnlTxnMrpBlockTimeoutAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MRP_BLOCK_TIME_ALLOWED_CHNL_TXN);
		String chnlMrpBlockTimeoutServicesGatewayCodes = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_MRP_BLOCK_TIMEOUT_SERVICES_GATEWAY_CODES);
		boolean isChnlTxnSuccessiveMrpBlockTimeoutApplicable = false;
		boolean requestGatewayCodeCheckRequired = false;
		boolean lastServiceTypeCheck =  false;
		String []chnlMrpBlockTimeoutServicesGatewayCodesArr = null;
		String []chnlMrpBlockTimeoutServicesGatewayCodesArr2 = null;
		String []chnlMrpBlockTimeoutGatewayCodesArr = null;
				
		//If TRUE then go for successive MRP block timeout 
		if(chnlTxnMrpBlockTimeoutAllowed) {
			//Check for configured channel service type && request gateway type
			if(!BTSLUtil.isNullString(chnlMrpBlockTimeoutServicesGatewayCodes)) {
				chnlMrpBlockTimeoutServicesGatewayCodesArr = BTSLUtil.split(chnlMrpBlockTimeoutServicesGatewayCodes, ",");
				for(int index=0;index<chnlMrpBlockTimeoutServicesGatewayCodesArr.length;index++){
					chnlMrpBlockTimeoutServicesGatewayCodesArr2 = BTSLUtil.split(chnlMrpBlockTimeoutServicesGatewayCodesArr[index], "-");
					//Check for configured channel service type
					if(BTSLUtil.isStringContain(chnlMrpBlockTimeoutServicesGatewayCodesArr2[0], p_channelTransferVO.getType())) {
						lastServiceTypeCheck =  true;
						if(chnlMrpBlockTimeoutServicesGatewayCodesArr2.length>=2 && !BTSLUtil.isNullString(chnlMrpBlockTimeoutServicesGatewayCodesArr2[1])) {
							chnlMrpBlockTimeoutGatewayCodesArr = BTSLUtil.split(chnlMrpBlockTimeoutServicesGatewayCodesArr2[1], "\\|");
							for(int index2=0;index2<chnlMrpBlockTimeoutGatewayCodesArr.length;index2++) {
								//Check for configured channel request gateway code
								if(p_channelTransferVO.getRequestGatewayCode().equalsIgnoreCase(chnlMrpBlockTimeoutGatewayCodesArr[index2])){
									requestGatewayCodeCheckRequired = true;
									break;
								}		
							}
						}
						//Break 1'st loop if found 
						if(requestGatewayCodeCheckRequired){
							break;
						}
					}
				}
				if(lastServiceTypeCheck) {
					//Load the channel transfer details based on configured value related to successive MRP block time out
					isChnlTxnSuccessiveMrpBlockTimeoutApplicable = channelTransferDAO.loadChannelTransfersDetails(p_con, p_channelTransferVO, chnlTxnMrpBlockTimeoutAllowed, requestGatewayCodeCheckRequired, p_currDate, p_successiveReqBlockTime);
					if(isChnlTxnSuccessiveMrpBlockTimeoutApplicable){
						 String[] array= {p_channelTransferVO.getToUserCode(),String.valueOf(p_successiveReqBlockTime), PretupsBL.getDisplayAmount(p_channelTransferVO.getTransferMRP())};                
			             throw new BTSLBaseException(ChannelTransferBL.class,methodName,PretupsErrorCodesI.CHNL_TXN_REC_LAST_SUCCESS_REQ_BLOCK_RECEIVER,0,array,null);
					}
				}	
			}
		}
		if (log.isDebugEnabled()) 	{
			log.debug(methodName, "Exited :: isChnlTxnSuccessiveMrpBlockTimeoutApplicable = "+isChnlTxnSuccessiveMrpBlockTimeoutApplicable);
		}
	}	
	/**
	 * 
	 * @param con
	 * @param p_productType
	 * @param networkCode
	 * @param p_commProfileSetId
	 * @param currentDate
	 * @param p_forwardPath
	 * @return
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadO2CWdrProductListRest(Connection con, String p_productType, String networkCode, String p_commProfileSetId, String p_domainCode, String p_categoryCode, Date currentDate) throws BTSLBaseException {
		final String methodName = "loadO2CWdrProductList";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered p_productType : ");
			loggerValue.append(p_productType);
			loggerValue.append(", NetworkCode : " );
			loggerValue.append(networkCode);
			loggerValue.append(", CommissionProfileSetID : ");
			loggerValue.append(p_commProfileSetId);
			loggerValue.append(", CurrentDate : ");
			loggerValue.append(currentDate);
			log.debug(methodName,loggerValue );
		}
		final ArrayList productList = new ArrayList();

		final NetworkProductDAO networkProductDAO = new NetworkProductDAO();

		// load the product list mapped with the network.
		final ArrayList prodList = networkProductDAO.loadProductListForXfr(con, p_productType, networkCode);

		// check whether product exist or not of the input productType
		if (prodList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.producttype", new String[] { p_productType });
		}

		// checking that the status of network product mapping is active and
		// also construct the new arrayList of
		// channelTransferItemsVOs containg required list.
		ChannelTransferItemsVO channelTransferItemsVO = null;
		NetworkProductVO networkProductVO = null;
		int i, j, m, n;
		for (i = 0, j = prodList.size(); i < j; i++) {
			networkProductVO = (NetworkProductVO) prodList.get(i);
			if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
				channelTransferItemsVO = new ChannelTransferItemsVO();
				channelTransferItemsVO.setProductType(networkProductVO.getProductType());
				channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
				channelTransferItemsVO.setProductName(networkProductVO.getProductName());
				channelTransferItemsVO.setShortName(networkProductVO.getShortName());
				channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
				channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
				channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
				channelTransferItemsVO.setStatus(networkProductVO.getStatus());
				channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
				channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
				channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
				productList.add(channelTransferItemsVO);
			}
		}
		if (productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.networkproductmapping", new String[] { p_productType });
		}


		// load the latest version of the commission profile set id
		final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		String latestCommProfileVersion = null;
		try {
			final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, p_commProfileSetId, currentDate);
			latestCommProfileVersion = commissionProfileSetVO.getCommProfileVersion();
		} catch (BTSLBaseException bex) {
			if (PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED.equals(bex.getMessage())) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofilever");
			}
			log.error(methodName, "BTSLBaseException " + bex.getMessage());
			throw bex;
		}

		// if there is no commission profile version exist upto the current date
		// show the error message.
		if (BTSLUtil.isNullString(latestCommProfileVersion)) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofilever");
		}
		boolean TRANSACTION_TYPE_ALWD = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		String type = (TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
		String paymentMode = PretupsI.ALL;
		final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(con, p_commProfileSetId, latestCommProfileVersion, type, paymentMode);

		// if list is empty send the error message
		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.commprofileproduct", new String[] { latestCommProfileVersion });
		}

		// filterize the product list with the products of the commission
		// profile products
		CommissionProfileProductsVO commissionProfileProductsVO = null;
		for (i = 0, j = commissionProfileProductList.size(); i < j; i++) {
			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
			for (m = 0, n = productList.size(); m < n; m++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
					channelTransferItemsVO.setTransferMultipleOf(commissionProfileProductsVO.getTransferMultipleOff());
					channelTransferItemsVO.setDiscountType(commissionProfileProductsVO.getDiscountType());
					channelTransferItemsVO.setDiscountRate(commissionProfileProductsVO.getDiscountRate());
					channelTransferItemsVO.setCommProfileDetailID(commissionProfileProductsVO.getCommProfileProductID());
					channelTransferItemsVO.setTaxOnChannelTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
					channelTransferItemsVO.setTaxOnFOCTransfer(commissionProfileProductsVO.getTaxOnFOCApplicable());
					break;
				}
			}
		}
		for (m = 0, n = productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
			if (BTSLUtil.isNullString(channelTransferItemsVO.getCommProfileDetailID())) {
				productList.remove(m);
				m--;
				n--;
			}
		}

		if (productList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.nodata.networkcommprofileproduct");
		}

		/*
		 * Now further filter the list with the transfer rules list and the
		 * above list
		 * of commission profile products.
		 */
		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
		// load the approval limit of the user

		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, networkCode, p_domainCode, PretupsI.CATEGORY_TYPE_OPT,
				p_categoryCode, PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		final ArrayList transferRulePrdList = channelTransferRuleVO.getProductVOList();
		ListValueVO listValueVO = null;
		final ArrayList tempList = new ArrayList();

		for (int p = 0, q = productList.size(); p < q; p++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(p);
			for (int l = 0, k = transferRulePrdList.size(); l < k; l++) {
				listValueVO = (ListValueVO) transferRulePrdList.get(l);
				if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
					tempList.add(channelTransferItemsVO);
					break;
				}
			}
		}

		/*
		 * This case arises
		 * suppose in transfer rule products A and B are associated
		 * In commission profile product C and D are associated.
		 * We load product with intersection of transfer rule products and
		 * commission profile products.
		 * if no product found then display below message
		 */
		if (tempList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class, methodName, "message.transfer.transferrule.noproductmatch");
		}

		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited with tempList.size = ");
			loggerValue.append(tempList.size());
			log.debug(methodName, loggerValue );
		}
		// return productList;

		return tempList;
	}
	
	
	public static Map<String, Object> checkUserLoanstatusAndAmount(Connection con,ChannelTransferVO channelTransferVO) throws BTSLBaseException {
		final String methodName = "checkUserLoanstatusAndAmount";
		LogFactory.printLog(methodName, "ChannelTransferVO" + channelTransferVO, log);
		final HashMap loanStatus = new HashMap();
		/* SET product code in case of C2C withdraw */
		final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
		ChannelUserVO channelUserVO = null;
		UserLoanVO    userLoanVO        =null;
		int profileID =0;
		boolean isLoanExist = false;
		
		try {
		List<String> gatewayCodeList = Arrays.asList(SystemPreferences.BLOCK_GATEWAYCODE_FOR_LOAN_SETTLEMENT.trim().split("\\s*,\\s*"));

		boolean allowedGatewayForSettlement = false;

		for (String allowedGateway: gatewayCodeList) {
			if(allowedGateway.equals(channelTransferVO.getRequestGatewayCode()))
			{
				allowedGatewayForSettlement = true;
				break;
			}
		}
		
		if(allowedGatewayForSettlement) {
			/* Check System Level prerferences */
			if (SystemPreferences.USERWISE_LOAN_ENABLE) {
				ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				channelUserVO = channelUserDAO.loadChannelUserByUserID(con, channelTransferVO.getToUserID());

				if (channelUserVO == null) {
					loanStatus.put(PretupsI.DO_WITHDRAW, false);
					loanStatus.put(PretupsI.BLOCK_TRANSACTION, false);
				}
				else
				{
					if(channelUserVO.getUserLoanVOList()!= null)
					{
						
						
						for (int i =0; i <channelTransferVO.getChannelTransferitemsVOList().size();i++){
							{
								for (UserLoanVO loanVO : channelUserVO.getUserLoanVOList()) {
									
									ChannelTransferItemsVO itemsVO= (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(i);
									if(channelUserVO.getUserID().equals(loanVO.getUser_id()) && loanVO.getProduct_code().equals(itemsVO.getProductCode())&& PretupsI.YES.equals(loanVO.getLoan_given()))
									{
										userLoanVO = loanVO;
										isLoanExist = true;
										break;
									}
									else
										continue;
								}
							}
							
						}
						
						if(userLoanVO!= null && isLoanExist) {
							
								if (userLoanVO.getProfile_id() == 0) {
									loanStatus.put(PretupsI.DO_WITHDRAW, false);
									loanStatus.put(PretupsI.BLOCK_TRANSACTION, true);
								} else {

									List<String> constantsList = Arrays.asList(SystemPreferences.ALLOW_TRANSACTION_IF_LOAN_SETTLEMENT_FAIL.trim().split("\\s*,\\s*"));
									LoanProfileDAO  loanProfileDAO= new LoanProfileDAO();
									ArrayList<LoanProfileDetailsVO>  loanProfileList = loanProfileDAO.loadLoanProfileSlabs(con,String.valueOf(userLoanVO.getProfile_id()));

									long premiumAmount= calculatorI.calculatePremium(userLoanVO, loanProfileList);
									/* Compare Quantity for SOS */
									LogFactory.printLog(methodName, "Transfer MRP === " +channelTransferVO.getTransferMRP() + ",premiumAmount= " +premiumAmount , log);
									LogFactory.printLog(methodName, " userLoanVO.getLoan_amount() " + userLoanVO.getLoan_amount() , log);
									LogFactory.printLog(methodName,	"Preference Code" + constantsList, log);

									if ((channelTransferVO.getTransferMRP() < userLoanVO.getLoan_amount()+premiumAmount))  {
										if (constantsList.contains(channelTransferVO.getTransactionCode())
												|| PretupsI.CHANNEL_TRANSFER_TYPE_RETURN
												.equals(channelTransferVO.getTransferType())
												|| PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL
												.equals(channelTransferVO.getTransferType()) || PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equalsIgnoreCase(channelTransferVO.getTransactionCode())) {
											loanStatus.put(PretupsI.BLOCK_TRANSACTION, false);
											loanStatus.put(PretupsI.DO_WITHDRAW, false);
										} else {

											loanStatus.put(PretupsI.DO_WITHDRAW, false);
											loanStatus.put(PretupsI.BLOCK_TRANSACTION, true);
											loanStatus.put(PretupsI.WITHDRAW_AMOUNT, userLoanVO.getLoan_amount()+premiumAmount);
											
										}

									} else {
										loanStatus.put(PretupsI.DO_WITHDRAW, true);
										loanStatus.put(PretupsI.BLOCK_TRANSACTION, false);
										loanStatus.put(PretupsI.WITHDRAW_AMOUNT, userLoanVO.getLoan_amount()+premiumAmount);
										channelTransferVO.setProductCode(userLoanVO.getProduct_code());

									}
								}
							
						}
						else {
							loanStatus.put(PretupsI.DO_WITHDRAW, false);
							loanStatus.put(PretupsI.BLOCK_TRANSACTION, false);
						}
					}
					else {
						loanStatus.put(PretupsI.DO_WITHDRAW, false);
						loanStatus.put(PretupsI.BLOCK_TRANSACTION, false);
					}
				}
			}

		}
		else {
			loanStatus.put(PretupsI.DO_WITHDRAW, false);
			loanStatus.put(PretupsI.BLOCK_TRANSACTION, false);
		}
		}
		catch (Exception e) {
			log.errorTrace(methodName, e);
			
			
		}
		LogFactory.printLog(methodName, "Final Return>>>>> " + loanStatus.toString() , log);
		return loanStatus;

	}
	
}
