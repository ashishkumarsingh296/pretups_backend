package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.clientprocesses.ProcessExpiryChangeThread;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsBatchExpiryVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;

/**
 * @author karun.sood
 *
 */
public class VoucherExpiryChangeHandler implements ServiceKeywordControllerI {
	private static Log _log = LogFactory.getLog(VoucherExpiryChangeHandler.class.getName());
	private static OperatorUtilI _operatorUtil = null;
	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"C2SPrepaidController[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	public VoucherExpiryChangeHandler() {
	}

	public void process(RequestVO p_requestVO) {
		final String methodName = "process";
		_log.debug(methodName, p_requestVO.getRequestIDStr(),
				"Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN());
		Connection con = null;
		MComConnectionI mcomCon = null;
		HashMap<String, Object> responseMap = new HashMap<>();
		responseMap.put("RESPONSEPARAM", (String) p_requestVO.getResponseMap().get("RESPONSEPARAM"));
		p_requestVO.setResponseMap(responseMap);
		VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
		String decryptedMessage = p_requestVO.getDecryptedMessage();
		String[] reqArr = null;
		VomsBatchExpiryVO vomsBatchExpiryVO = null;
		String networkCode = null;
		String reasonForChange = null;
		HashMap requestMap = null;

		try {
			String sep = " ";
			if (((String)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)) != null)
				sep = ((String)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR));

			reqArr = decryptedMessage.split(sep);
			requestMap = p_requestVO.getRequestMap();
			networkCode = (String) requestMap.get("EXTNWCODE");
			reasonForChange = (String) requestMap.get("STATE_CHANGE_REASON");
			responseMap.put(VOMSI.EXTERNAL_NETWORKCODE, networkCode);

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			Date currentDate = new Date();
			long noOfVouchers = Long.parseLong(reqArr[2]) - Long.parseLong(reqArr[1]) + 1;
			vomsBatchExpiryVO = new VomsBatchExpiryVO();
			vomsBatchExpiryVO.setFromSerialNo(reqArr[1]);
			vomsBatchExpiryVO.setToSerialNo(reqArr[2]);
			vomsBatchExpiryVO.setExpiryDate(new SimpleDateFormat("dd-MM-yyyy").parse(reqArr[3]));
			vomsBatchExpiryVO.setBatchNo(generateVoucherExpiryChangeID());
			vomsBatchExpiryVO.setNoOfVoucher(noOfVouchers);
			vomsBatchExpiryVO.setExecutionStatus(VOMSI.SCHEDULED);
			vomsBatchExpiryVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(currentDate));
			vomsBatchExpiryVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(currentDate));
			vomsBatchExpiryVO.setCreatedBy(PretupsI.SYSTEM);
			vomsBatchExpiryVO.setModifiedBy(PretupsI.SYSTEM);
			vomsBatchExpiryVO.setSuccessCount(0);
			vomsBatchExpiryVO.setFailCount(0);
			vomsBatchExpiryVO.setStatus(VOMSI.VOMS_CLOSED_STATUS);
			vomsBatchExpiryVO.setFilename("");
			vomsBatchExpiryVO.setVoucherType(reqArr[4]);
			
			if (Long.parseLong(vomsBatchExpiryVO.getFromSerialNo()) > Long
					.parseLong(vomsBatchExpiryVO.getToSerialNo())) {
				_log.debug(methodName, p_requestVO.getRequestIDStr(),
						"From Serial Number cannot be greater than To Serial Number");
				responseMap.put(VOMSI.FROM_SERIAL_NO, reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO, reqArr[2]);
				responseMap.put(VOMSI.REQ_EXPIRY_DATE, reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG, "From Serial Number cannot be greater than To Serial Number");
				responseMap.put(VOMSI.ERROR_TAG, PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				responseMap.put(VOMSI.TXNSTATUS_TAG, PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				throw new BTSLBaseException("FAILED");
			}

			vomsVoucherDAO = new VomsVoucherDAO();
			int insertCount = vomsVoucherDAO.insertVomsBatchesExpiry(con, vomsBatchExpiryVO);
			if (insertCount > 0) {
				con.commit();
			}

			if (noOfVouchers <= Long.parseLong(Constants.getProperty("VOMS_EXPIRY_VOUCHER_COUNT"))) {
				ProcessExpiryChangeThread obj = new ProcessExpiryChangeThread();
				HashMap<String, ArrayList<String>> map = obj.normalProcess(vomsBatchExpiryVO);
				responseMap.put(VOMSI.FROM_SERIAL_NO, reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO, reqArr[2]);
				responseMap.put(VOMSI.REQ_EXPIRY_DATE, reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG, "SuccessFully updated with success count = "
						+ map.get("successList").size() + " and failure count = " + map.get("failureList").size());
				responseMap.put(VOMSI.TXNSTATUS_TAG, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				p_requestVO.setResponseMap(responseMap);
			} else {
				responseMap.put(VOMSI.FROM_SERIAL_NO, reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO, reqArr[2]);
				responseMap.put(VOMSI.REQ_EXPIRY_DATE, reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,
						"Voucher expiry date updation is scheduled to be performed in non-peak hours");
				responseMap.put(VOMSI.TXNSTATUS_TAG, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				p_requestVO.setResponseMap(responseMap);
			}

		} catch (BTSLBaseException be) {
			try {
				mcomCon.finalRollback();
			} catch (Exception e) {
				_log.error(methodName, "Exception:e=" + e);
				_log.errorTrace(methodName, e);

			}
			if (!be.getMessage().equalsIgnoreCase("FAILED")) {
				responseMap.put(VOMSI.FROM_SERIAL_NO, reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO, reqArr[2]);
				responseMap.put(VOMSI.REQ_EXPIRY_DATE, reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG, "Not able to Change Voucher Status");
				responseMap.put(VOMSI.ERROR_TAG, PretupsErrorCodesI.ERROR_VOMS_ERROR);
				responseMap.put(VOMSI.TXNSTATUS_TAG, PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
			}
			p_requestVO.setResponseMap(responseMap);
			_log.error("processRequest", "BTSLBaseException" + be);
		} catch (Exception e) {
			e.printStackTrace();
			// added while Voucher Retrieval RollBack Request
			try {
				mcomCon.finalRollback();
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}

			responseMap.put(VOMSI.FROM_SERIAL_NO, reqArr[1]);
			responseMap.put(VOMSI.TO_SERIAL_NO, reqArr[2]);
			responseMap.put(VOMSI.REQ_EXPIRY_DATE, reqArr[3]);
			responseMap.put(VOMSI.MESSAGE_TAG, "Not able to Change Voucher Status");
			responseMap.put(VOMSI.ERROR_TAG, PretupsErrorCodesI.ERROR_VOMS_ERROR);
			responseMap.put(VOMSI.TXNSTATUS_TAG, PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
			p_requestVO.setResponseMap(responseMap);
			_log.error("processRequest", "Exception" + e);
		} finally {
			try {
				mcomCon.finalCommit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (mcomCon != null) {
				mcomCon.close("VoucherExpiryChangeHandler#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}

	}// end of finally

	static synchronized String generateVoucherExpiryChangeID() {

		String batchID = null;
		final String methodName = "generateVoucherExpiryChangeID";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		try {

			batchID = _operatorUtil.formatVoucherExpiryChangeID();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting");
		}
		return batchID;
	}
}
