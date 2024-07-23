package com.businesscontrollers;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.commissionprofile.ChannelTransferBL;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;

public class businessController extends BaseTest {

	private String _txnType;
	
	private String _toMSISDN;
	private String _fromMSISDN;
	
	public businessController(String txnType, String FromMSISDN, String ToMSISDN) {
		_txnType = txnType;
		_fromMSISDN = FromMSISDN;
		_toMSISDN = ToMSISDN;
	}
	
	public TransactionVO preparePreTransactionVO() {
		final String methodname = "preparePreTransactionVO";
		Log.debug("Entered " + methodname + "()");
		
		TransactionVO TransactionVO = new TransactionVO();
		TransactionVO.set_txntype(_txnType);
		TransactionVO.set_fromMSISDN(_fromMSISDN);
		TransactionVO.set_toMSISDN(_toMSISDN);
		
		TransactionVO.set_multiWalletStatus(DBHandler.AccessHandler.getSystemPreference(CONSTANT.MULTIWALLET_SYSTEM_STATUS));
		/*if (_masterVO.getClientDetail("DUAL_COMMISSION_FieldType").equalsIgnoreCase("0"))
			TransactionVO.setDualCommissioningType(DBHandler.AccessHandler.getApplicableDualCommissioningType(_toMSISDN));*/
		
		/* krishan.chawla
		 * Auto Network Stock Handling Added 20th June 2018
		 */
		TransactionVO.setautoNetworkStockSystemStatus(DBHandler.AccessHandler.getPreference(null, _masterVO.getMasterValue(MasterI.NETWORK_CODE), CONSTANT.AUTO_NWSTK_CRTN_ALWD));
		TransactionVO.setAutoNetworkStockSystemThreshold(DBHandler.AccessHandler.getPreference(null, _masterVO.getMasterValue(MasterI.NETWORK_CODE), CONSTANT.AUTO_NWSTK_CRTN_THRESHOLD));
		
		TransactionVO.setSalePreBalances(_parser.convertToHashMap(DBHandler.AccessHandler.getProductsDetails(_masterVO.getMasterValue(MasterI.NETWORK_CODE), PretupsI.SALE_WALLET_LOOKUP), 0, 2));
		if (TransactionVO.get_multiWalletStatus().equalsIgnoreCase("true")) {
			TransactionVO.setFocPreBalances(_parser.convertToHashMap(DBHandler.AccessHandler.getProductsDetails(_masterVO.getMasterValue(MasterI.NETWORK_CODE), PretupsI.FOC_WALLET_LOOKUP), 0, 2));
			TransactionVO.setIncentivePreBalances(_parser.convertToHashMap(DBHandler.AccessHandler.getProductsDetails(_masterVO.getMasterValue(MasterI.NETWORK_CODE), PretupsI.INCENTIVE_WALLET_LOOKUP), 0, 2));
		}
		
		if (_txnType.equalsIgnoreCase(_masterVO.getProperty("O2CTransferCode"))) {
			TransactionVO.set_toUserPreBalances(DBHandler.AccessHandler.getUserBalances(_toMSISDN));
			if (!_masterVO.getClientDetail("DUAL_COMMISSION_FieldType").equalsIgnoreCase("0"))
				TransactionVO.setDualCommissioningType(DBHandler.AccessHandler.getApplicableDualCommissioningType(_toMSISDN));
		} else if (_txnType.equalsIgnoreCase(_masterVO.getProperty("FOCCode"))) {
			TransactionVO.set_toUserPreBalances(DBHandler.AccessHandler.getUserBalances(_toMSISDN));
			if (!_masterVO.getClientDetail("DUAL_COMMISSION_FieldType").equalsIgnoreCase("0"))
				TransactionVO.setDualCommissioningType(DBHandler.AccessHandler.getApplicableDualCommissioningType(_toMSISDN));
		} else if (_txnType.equalsIgnoreCase(_masterVO.getProperty("O2CReturnCode")) || _txnType.equalsIgnoreCase(_masterVO.getProperty("O2CWithdrawCode"))) {
			TransactionVO.set_fromUserPreBalances(DBHandler.AccessHandler.getUserBalances(_fromMSISDN));
			if (!_masterVO.getClientDetail("DUAL_COMMISSION_FieldType").equalsIgnoreCase("0"))
				TransactionVO.setDualCommissioningType(DBHandler.AccessHandler.getApplicableDualCommissioningType(_fromMSISDN));
		} else if (_txnType.equalsIgnoreCase(_masterVO.getProperty("C2CTransferCode")) || _txnType.equalsIgnoreCase(_masterVO.getProperty("C2CWithdrawCode")) || _txnType.equalsIgnoreCase(_masterVO.getProperty("C2CReturnCode"))) {
			TransactionVO.set_fromUserPreBalances(DBHandler.AccessHandler.getUserBalances(_fromMSISDN));
			TransactionVO.set_toUserPreBalances(DBHandler.AccessHandler.getUserBalances(_toMSISDN));
			if (!_masterVO.getClientDetail("DUAL_COMMISSION_FieldType").equalsIgnoreCase("0"))
				TransactionVO.setDualCommissioningType(DBHandler.AccessHandler.getApplicableDualCommissioningType(_toMSISDN));
		} else if (_txnType.equalsIgnoreCase(_masterVO.getProperty("CustomerRechargeCode"))) {
			TransactionVO.set_fromUserPreBalances(DBHandler.AccessHandler.getUserBalances(_fromMSISDN));
			if (!_masterVO.getClientDetail("DUAL_COMMISSION_FieldType").equalsIgnoreCase("0"))
				TransactionVO.setDualCommissioningType(DBHandler.AccessHandler.getApplicableDualCommissioningType(_fromMSISDN));
		}
		
		Log.debug("Exiting " + methodname + "(" + TransactionVO.toString() + ")");
		return TransactionVO;
	}
	
	public TransactionVO preparePostTransactionVO(TransactionVO _transactionVO, HashMap<String, String> initiatedQuantities) throws ParseException, SQLException {
		final String methodname = "preparePostTransactionVO";
		Log.debug("Entered " + methodname + "(" + _transactionVO.toString() + " | initiatedQuantities= " + Arrays.asList(initiatedQuantities) + ")");
		
		_transactionVO.setInitiatedQty(initiatedQuantities);
		
		if (!(_transactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("O2CReturnCode")) || _txnType.equalsIgnoreCase(_masterVO.getProperty("O2CWithdrawCode")))) {
			_transactionVO.setCommissionVO(ChannelTransferBL.calculateAmountOTF(_transactionVO.get_toMSISDN(), _transactionVO.get_txntype(), initiatedQuantities, _transactionVO.getGatewayType()));
		}
		
		_transactionVO.setSalePostBalances(_parser.convertToHashMap(DBHandler.AccessHandler.getProductsDetails(_masterVO.getMasterValue(MasterI.NETWORK_CODE), PretupsI.SALE_WALLET_LOOKUP), 0, 2));
		if (_transactionVO.get_multiWalletStatus().equalsIgnoreCase("true")) {
			_transactionVO.setFocPostBalances(_parser.convertToHashMap(DBHandler.AccessHandler.getProductsDetails(_masterVO.getMasterValue(MasterI.NETWORK_CODE), PretupsI.FOC_WALLET_LOOKUP), 0, 2));
			_transactionVO.setIncentivePostBalances(_parser.convertToHashMap(DBHandler.AccessHandler.getProductsDetails(_masterVO.getMasterValue(MasterI.NETWORK_CODE), PretupsI.INCENTIVE_WALLET_LOOKUP), 0, 2));
		}
		
		if (_txnType.equalsIgnoreCase(_masterVO.getProperty("O2CTransferCode"))) {
			_transactionVO.set_toUserPostBalances(DBHandler.AccessHandler.getUserBalances(_toMSISDN));
		} else if (_txnType.equalsIgnoreCase(_masterVO.getProperty("FOCCode"))) {
			_transactionVO.set_toUserPostBalances(DBHandler.AccessHandler.getUserBalances(_toMSISDN));
		} else if (_txnType.equalsIgnoreCase(_masterVO.getProperty("O2CReturnCode")) || _txnType.equalsIgnoreCase(_masterVO.getProperty("O2CWithdrawCode"))) {
			_transactionVO.set_fromUserPostBalances(DBHandler.AccessHandler.getUserBalances(_fromMSISDN));
		} else if (_txnType.equalsIgnoreCase(_masterVO.getProperty("C2CTransferCode")) || _txnType.equalsIgnoreCase(_masterVO.getProperty("C2CWithdrawCode")) || _txnType.equalsIgnoreCase(_masterVO.getProperty("C2CReturnCode"))) {
			_transactionVO.set_fromUserPostBalances(DBHandler.AccessHandler.getUserBalances(_fromMSISDN));
			_transactionVO.set_toUserPostBalances(DBHandler.AccessHandler.getUserBalances(_toMSISDN));
		} else if (_txnType.equalsIgnoreCase(_masterVO.getProperty("CustomerRechargeCode"))) {
			_transactionVO.set_fromUserPostBalances(DBHandler.AccessHandler.getUserBalances(_fromMSISDN));
		}
		
		Log.debug("Exiting " + methodname + "(" + _transactionVO.toString() + ")");
		return _transactionVO;
	}
}
