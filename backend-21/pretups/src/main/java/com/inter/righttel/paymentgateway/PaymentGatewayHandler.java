package com.inter.righttel.paymentgateway;
import java.util.HashMap;

import org.apache.axis.client.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;
import com.inter.righttel.paymentgateway.scheduler.NodeManager;
import com.inter.righttel.paymentgateway.scheduler.NodeScheduler;
import com.inter.righttel.paymentgateway.scheduler.NodeVO;
import com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType;


public class PaymentGatewayHandler implements InterfaceHandler {

	private static Log _log = LogFactory.getLog(PaymentGatewayHandler.class.getName());
	private HashMap _responseMap = null;
	private HashMap _requestMap = null;
	private String _interfaceID = null;
	private String _inTXNID = null;
	private String _msisdn = null;
	private String _refNo = null;
	private String _inReconID=null;
	private String _referenceID = null;
	private Stub _stubSuper=null;
	private String _userType = null;
	private static PaymentGatewayRequestFormatter _paymentGatewayRequestFormatter = null;
	private static PaymentGatewayResponseFormatter _paymentGatewayResponseFormatter = null;
	static {
		if (_log.isDebugEnabled())
			_log.debug("PaymentGatewayHandler[static]", "Entered");
		try {
			_paymentGatewayRequestFormatter = new PaymentGatewayRequestFormatter();
			_paymentGatewayResponseFormatter = new PaymentGatewayResponseFormatter();
		} catch (Exception e) {
			_log.error("PaymentGatewayHandler[static]", "While instantiation of PaymentGatewayRequestFormatter get Exception e::" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewayHandler[static]", "", "", "", "While instantiation of PaymentGatewayRequestFormatter get Exception e::" + e.getMessage());
		} finally {
			if (_log.isDebugEnabled())
				_log.debug("PaymentGatewayHandler[static]", "Exited");
		}
	}
	public PaymentGatewayHandler() {

		_responseMap=new HashMap();

	}
	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
		if (_log.isDebugEnabled())
			_log.debug("debitAdjust", " Entered p_requestMap:" + p_requestMap);
		_requestMap = p_requestMap;
		double systemAmtDouble = 0;
		String amountStr = null;
		int validityDays = 0;
		try {
			_userType = (String) _requestMap.get("USER_TYPE");
			_interfaceID = (String) _requestMap.get("INTERFACE_ID");
			_inTXNID = getPGTransactionID(_requestMap);
			_requestMap.put("IN_TXN_ID", _inTXNID);
			_referenceID = (String) _requestMap.get("TRANSACTION_ID");
			_msisdn = (String) _requestMap.get("MSISDN");
			String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
			if (InterfaceUtil.isNullString(multFactor)) {
				_log.error("debitAdjust", "MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewayHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
				throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			setInterfaceParameters();
			_requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			try {
				double multFactorDouble = Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
				amountStr = String.valueOf(systemAmtDouble);
				String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
				if (_log.isDebugEnabled())
					_log.debug("debitAdjust", "From file cache roundFlag = " + roundFlag);
				if (InterfaceUtil.isNullString(roundFlag)) {
					roundFlag = "Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "PaymentGatewayHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
				}
				if ("Y".equals(roundFlag.trim())) {
					amountStr = String.valueOf(Math.round(systemAmtDouble));
					_requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
				}
			} catch (Exception e) {
				e.printStackTrace();
				_log.error("debitAdjust", "Exception e:" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewayHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if (_log.isDebugEnabled())
				_log.debug("debitAdjust", "transfer_amount:" + amountStr + " multFactor:" + multFactor);

			_requestMap.put("transfer_amount", amountStr);

			String requestString = _paymentGatewayRequestFormatter.generateRequest(PaymentGatewayI.ACTION_IMMEDIATE_DEBIT, _requestMap);
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("PG_STR", requestString);

			StringBuffer body=new StringBuffer();
			body.append("?Amount=" + p_requestMap.get("INTERFACE_AMOUNT"));
			body.append("&ResNum=" + p_requestMap.get("TRANSACTION_ID"));
			body.append("&mobile=" + p_requestMap.get("MSISDN"));
			body.append("&RedirectURL=" + p_requestMap.get("CALLBACK_URL")+"|MESSAGE=O2CAPRL+"+p_requestMap.get("TRANSACTION_ID")+"+STATUS+REFNO+"+p_requestMap.get("NETWORK_CODE"));
			body.append("&MID=" + p_requestMap.get("MERCHANT_ID"));
			body.append("&AccountNo=" + p_requestMap.get("TERMINAL_ID"));
			body.append("&TerminalId=" + p_requestMap.get("MERCHANT_ID"));

			_requestMap.put("PG_DATA", body.toString().trim());

			_requestMap.put("CALLBACK_STR",  (String)_requestMap.get("CALLBACK_DATA"));

		} catch (BTSLBaseException be) {
			p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			_log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
			if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
				throw be;

		} catch (Exception e) {
			e.printStackTrace();
			_log.error("debitAdjust", "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewayHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
			throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		} finally {
			if (_log.isDebugEnabled())
				_log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
		}

		try {
			_requestMap.put("IN_START_TIME", "0");
			_requestMap.put("IN_END_TIME", "0");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}// end of debitAdjust.

	protected static String getPGTransactionID(HashMap p_requestMap) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("getPGTransactionID", "Entered");
		String userType = (String) p_requestMap.get("USER_TYPE");
		String inTxnId = (String) p_requestMap.get("TRANSACTION_ID");

		if (!InterfaceUtil.isNullString(userType))
			inTxnId = inTxnId + userType;

		p_requestMap.put("IN_RECON_vaID", inTxnId);
		p_requestMap.put("IN_TXN_ID", inTxnId);
		if (_log.isDebugEnabled())
			_log.debug("getPGTransactionID", "exited");
		return inTxnId;
	}


	private void setInterfaceParameters() throws Exception, BTSLBaseException {
		System.out.println();
		if (_log.isDebugEnabled())
			_log.debug("setInterfaceParameters", "Entered");
		try {
			String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
			if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
				_log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

			String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
			if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
				_log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

			String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
			if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
				_log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

			String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
				_log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

			String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
			if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
				_log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

			String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
			if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
				_log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

			String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
			if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

			String currency = FileCache.getValue(_interfaceID, "CURRENCY");
			if (InterfaceUtil.isNullString(currency)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CURRENCY", currency.trim());

			String merchantName = FileCache.getValue(_interfaceID, "MERCHANT_NAME");
			if (InterfaceUtil.isNullString(merchantName)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "MERCHANT_NAME is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("MERCHANT_NAME", merchantName.trim());

			String productName = FileCache.getValue(_interfaceID, "PRODUCT_NAME");
			if (InterfaceUtil.isNullString(productName)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "PRODUCT_NAME is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("PRODUCT_NAME", productName.trim());

			String billingAddressLine1 = FileCache.getValue(_interfaceID, "BILLING_ADDRESS_LINE1");
			if (InterfaceUtil.isNullString(billingAddressLine1)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "BILLING_ADDRESS_LINE1 is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("BILLING_ADDRESS_LINE1", billingAddressLine1.trim());

			String billingAddressLine2 = FileCache.getValue(_interfaceID, "BILLING_ADDRESS_LINE2");
			if (InterfaceUtil.isNullString(billingAddressLine2)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "BILLING_ADDRESS_LINE2 is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("BILLING_ADDRESS_LINE2", billingAddressLine2.trim());

			String billingCountry = FileCache.getValue(_interfaceID, "BILLING_COUNTRY");
			if (InterfaceUtil.isNullString(billingCountry)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "BILLING_COUNTRY is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("BILLING_COUNTRY", billingCountry.trim());

			String billingState = FileCache.getValue(_interfaceID, "BILLING_STATE");
			if (InterfaceUtil.isNullString(billingState)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "BILLING_STATE is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("BILLING_STATE", billingState.trim());

			String billingCity = FileCache.getValue(_interfaceID, "BILLING_CITY");
			if (InterfaceUtil.isNullString(billingCity)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "BILLING_CITY is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("BILLING_CITY", billingCity.trim());

			String merchantId = FileCache.getValue(_interfaceID, "MERCHANT_ID");
			if (InterfaceUtil.isNullString(merchantId)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "MERCHANT_ID is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("MERCHANT_ID", merchantId.trim());

			String terminalId = FileCache.getValue(_interfaceID, "TERMINAL_ID");
			if (InterfaceUtil.isNullString(terminalId)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "TERMINAL_ID is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("TERMINAL_ID", terminalId.trim());

			String targetURL = FileCache.getValue(_interfaceID, "TARGET_URL");
			if (InterfaceUtil.isNullString(targetURL)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "TARGET_URL is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("TARGET_URL", targetURL.trim());

			String checkSum = FileCache.getValue(_interfaceID, "CHECKSUM");
			if (InterfaceUtil.isNullString(checkSum)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CHECKSUM is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CHECKSUM", checkSum.trim());

			String callbackUrl = FileCache.getValue(_interfaceID, "CALLBACK_URL");
			if (InterfaceUtil.isNullString(callbackUrl)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CALLBACK_URL is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CALLBACK_URL", callbackUrl.trim());

			String stateAllowed = FileCache.getValue(_interfaceID, "Paymnt_Gateway_State");
			if (InterfaceUtil.isNullString(stateAllowed)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Paymnt_Gateway_State is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("Paymnt_Gateway_State", stateAllowed.trim());

			String id = FileCache.getValue(_interfaceID, "COMV_INIT_ID");
			if (InterfaceUtil.isNullString(id)) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "COMV_INIT_ID is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("COMV_INIT_ID", id.trim());

			String pwd = FileCache.getValue(_interfaceID, "COMV_INIT_PASSWORD");
			if (InterfaceUtil.isNullString(pwd)) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "COMV_INIT_PASSWORD is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("COMV_INIT_PASSWORD", pwd.trim());

			String url = FileCache.getValue(_interfaceID, "PG_URL");
			if (InterfaceUtil.isNullString(url)) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "PG_URL is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("PG_URL", url.trim());

			String keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
			if (InterfaceUtil.isNullString(keepAlive)) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "KEEP_ALIVE is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("KEEP_ALIVE", keepAlive.trim());


			String connectTimeout = FileCache.getValue(_interfaceID, "CONNECT_TIME_OUT");
			if (InterfaceUtil.isNullString(connectTimeout)) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PaymentGatewatIHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CONNECT_TIME_OUT is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CONNECT_TIME_OUT", connectTimeout.trim());

		}// end of try block
		catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error("setInterfaceParameters", "Exception e=" + e.getMessage());
			throw e;
		}// end of catch-Exception
		finally {
			if (_log.isDebugEnabled())
				_log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
		}// end of finally
	}

	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
		if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);

		_requestMap = p_requestMap;

		try
		{
			_requestMap.put("IN_START_TIME","0");
			_requestMap.put("IN_END_TIME","0");
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_refNo=(String)_requestMap.get("REFNO"); 	
			_requestMap.put("REF_NO",_refNo);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			
			String paymentTerminal = FileCache.getValue(_interfaceID,"TERMINAL_ID");
		    
			if(InterfaceUtil.isNullString(paymentTerminal))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of TERMINAL_ID in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			_requestMap.put("TERMINAL_ID",paymentTerminal);
			
			
			_inReconID=_referenceID.replace(".", "");
			
			_requestMap.put("IN_TXN_ID",_inReconID);
			
			sendRequestToIN(PaymentGatewayI.ACTION_ACCOUNT_DETAILS);
			
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			
			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
			
			_requestMap.put("RESULT_CODE",(String)_responseMap.get("returnCode"));

		}
		catch (BTSLBaseException be)
		{
			_log.error("validate","BTSLBaseException be="+be.getMessage());

			try
			{
				if(be.getMessage()==null)
				{
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PaymentGatewayHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
				else
				{
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PaymentGatewayHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  " validation exception="+be.getMessage());

					_requestMap.put("INTERFACE_STATUS",be.getMessage());	
					throw be;
				}
			}
			catch (BTSLBaseException be1)
			{
				throw be1;
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PaymentGatewayHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);        	

			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"PaymentGatewayHandler[validate]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
	}

	public void credit(HashMap<String, String> p_map) throws BTSLBaseException, Exception {
		// TODO Auto-generated method stub
	}

	public void creditAdjust(HashMap<String, String> p_requestMap) throws BTSLBaseException, Exception {
		if(_log.isDebugEnabled()) _log.debug("creditAdjust","Entered p_requestMap:"+p_requestMap);

		_requestMap = p_requestMap;

		try
		{
			_requestMap.put("IN_START_TIME","0");
			_requestMap.put("IN_END_TIME","0");
	
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			
			_refNo=(String)_requestMap.get("REFNO"); 	
			
			_requestMap.put("REF_NO",_refNo);
			
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=_referenceID.replace(".", "");
			
			
			String paymentTerminal = FileCache.getValue(_interfaceID,"TERMINAL_ID");
		    
			if(InterfaceUtil.isNullString(paymentTerminal))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of TERMINAL_ID in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			_requestMap.put("TERMINAL_ID",paymentTerminal);
			
			String paymentuser = FileCache.getValue(_interfaceID,"PAYMENT_USERNAME");
		    
			if(InterfaceUtil.isNullString(paymentuser))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of PAYMENT_USERNAME in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			_requestMap.put("PAYMENT_USERNAME",paymentuser);
	
		
			String paymentpassword = FileCache.getValue(_interfaceID,"PAYMENT_PASSWORD");
		    
			if(InterfaceUtil.isNullString(paymentpassword))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of PAYMENT_PASSWORD in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			_requestMap.put("PAYMENT_PASSWORD",paymentpassword);
			
			_requestMap.put("IN_TXN_ID",_inReconID);
			sendRequestToIN(PaymentGatewayI.ACTION_DEBIT_TXN);
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
			_requestMap.put("RESULT_CODE",(String)_responseMap.get("returnCode"));

		}
		catch (BTSLBaseException be)
		{
			_log.error("p_requestMap","BTSLBaseException be="+be.getMessage());

			try
			{
				if(be.getMessage()==null)
				{
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PaymentGatewayHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
				else
				{
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PaymentGatewayHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  " validation exception="+be.getMessage());

					_requestMap.put("INTERFACE_STATUS",be.getMessage());	
					throw be;
				}
			}
			catch (BTSLBaseException be1)
			{
				throw be1;
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("creditAdjust","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PaymentGatewayHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("creditAdjust","Exiting with  _requestMap: "+_requestMap);        	

			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"PaymentGatewayHandler[creditAdjust]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
	}

	public void validityAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception {
		// TODO Auto-generated method stub
	}

	private void sendRequestToIN(int p_action) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+p_action+" __msisdn="+_msisdn);

		String actionLevel="";
		switch(p_action)
		{
		case PaymentGatewayI.ACTION_ACCOUNT_DETAILS:
		{
			actionLevel="ACTION_ACCOUNT_DETAILS";
			break;
		}
		case PaymentGatewayI.ACTION_DEBIT_TXN:
		{
			actionLevel="ACTION_DEBIT_TXN";
			break;
		}
		} 

		long startTime=0,endTime=0,warnTime=0;
		PaymentIFBindingSoap_PortType clientStub=null;

		NodeScheduler nodeScheduler=null;
		NodeVO nodeVO=null;
		int retryNumber=0;

		PaymentGatewayUrlConnection serviceConnection =null;
		try
		{

			nodeScheduler = NodeManager.getScheduler(_interfaceID);

			retryNumber = nodeScheduler.getRetryNum();

			if(nodeScheduler==null)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);


			for(int loop=1;loop<=retryNumber;loop++)
			{
				try
				{
					nodeVO = nodeScheduler.getNodeVO(_inReconID);
					TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"PaymentGatewayHandler[sendRequestToIN]",PretupsI.TXN_LOG_REQTYPE_REQ,"Node information NodeVO:"+nodeVO,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);

					
					if(nodeVO==null)
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND );

					warnTime=nodeVO.getWarnTime();

					serviceConnection = new PaymentGatewayUrlConnection(nodeVO,_interfaceID,_requestMap.get("TRANSACTION_ID").toString());

					clientStub =serviceConnection.getService();			
					if(clientStub==null)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"PaymentGatewayHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Unable to get Client Object");
						_log.error("sendRequestToIN","Unable to get Client Object");
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
					}		            
					try
					{
						startTime=System.currentTimeMillis();
						_requestMap.put("IN_START_TIME",String.valueOf(startTime));
						switch(p_action)
						{	
						case PaymentGatewayI.ACTION_ACCOUNT_DETAILS: 
						{
							double d=clientStub.verifyTransaction(_refNo,(String)_requestMap.get("TERMINAL_ID"));
							_responseMap.put("returnCode",d);
							break;
						}
						case PaymentGatewayI.ACTION_DEBIT_TXN: 
						{
							double d=clientStub.reverseTransaction(_refNo,(String)_requestMap.get("TERMINAL_ID"),(String)_requestMap.get("PAYMENT_USERNAME"),(String)_requestMap.get("PAYMENT_PASSWORD"));
							_responseMap.put("returnCode",d);
							break;
						}
						}	
					}
					catch (java.rmi.RemoteException re) {
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewayHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RemoteException Error Message:" + re.getMessage());
						String respCode = null;
						// parse error code
						String requestStr = re.getMessage();
						int index = requestStr.indexOf("<ErrorCode>");
						if (index == -1) {
							if (re.getMessage().contains("java.net.ConnectException")) {

								_log.error("sendRequestToIN", "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
								EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PaymentGatewayHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection ");
								throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
							} else if (re.getMessage().contains("java.net.SocketTimeoutException")) {
								re.printStackTrace();
								if (re.getMessage().contains("connect")) {
									throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
								}
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewayHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RMI java.net.SocketTimeoutException Message:" + re.getMessage());
								_log.error("sendRequestToIN", "RMI java.net.SocketTimeoutException Error Message :" + re.getMessage());
								throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
							} else if (re.getMessage().contains("java.net.SocketException")) {
								re.printStackTrace();
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PaymentGatewayHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RMI java.net.SocketException Message:" + re.getMessage());
								_log.error("sendRequestToIN", "RMI java.net.SocketException Error Message :" + re.getMessage());
								throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
							} else
								throw new Exception(re);
						}
						respCode = requestStr.substring(index + "<ErrorCode>".length(), requestStr.indexOf("</ErrorCode>", index));
						index = requestStr.indexOf("<ErrorDescription>");
						String respCodeDesc = requestStr.substring(index + "<ErrorDescription>".length(), requestStr.indexOf("</ErrorDescription>", index));
						_log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
						_requestMap.put("INTERFACE_STATUS", respCode);
						_requestMap.put("INTERFACE_DESC", respCodeDesc);
						_log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

					}
					catch(Exception e)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"PaymentGatewayHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Exception Error Message:"+e.getMessage());
						_log.error("sendRequestToIN","Exception Error Message :"+e.getMessage());
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
					}
					finally
					{
						endTime=System.currentTimeMillis();
					}

				}
				catch(BTSLBaseException be)
				{
					throw be;
				}

				catch(Exception e)
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"PaymentGatewayHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Error Message:"+e.getMessage());
					_log.error("sendRequestToIN","Error Message :"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				finally
				{
					if(endTime==0) endTime=System.currentTimeMillis();
					_requestMap.put("IN_END_TIME",String.valueOf(endTime));			        
					_log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime);
				}

				if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Connection of _interfaceID ["+_interfaceID+"] for the Node Number ["+nodeVO.getNodeNumber()+"] created after the attempt number(loop)::"+loop);
				break;
			}

			TransactionLog.log( _interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response Map: "+_requestMap ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
			//Difference of start and end time would be compared against the warn time, if request and response takes more time than that of the warn time, an event with level INFO is handled
			if(endTime-startTime>=warnTime)
			{
				_log.info("sendRequestToIN", "WARN time reaches startTime= "+startTime+" endTime= "+endTime+" warnTime= "+warnTime+" time taken= "+(endTime-startTime));
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"PaymentGatewayHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"CCWS IP= "+nodeVO.getUrl(),"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel," IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
			}
			_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.SUCCESS);
		}
		catch(BTSLBaseException be)
		{
			_log.error("sendRequestToIN","BTSLBaseException be = "+be.getMessage());
			String status=String.valueOf(_requestMap.get("INTERFACE_STATUS"));
			_requestMap.put("INTERFACE_STATUS",status);
			throw be;
		}//end of BTSLBaseException
		catch(Exception e)
		{
			e.printStackTrace();	    
			_log.error("sendRequestToIN","Exception="+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"PaymentGatewayHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			_requestMap.remove("RESPONSE_OBJECT");
			clientStub=null;
			serviceConnection=null;
			if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Exiting p_action="+p_action);
		}//end of finally
	}

}
