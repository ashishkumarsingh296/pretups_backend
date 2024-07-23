package com.restapi.channelAdmin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomsreport.businesslogic.VomsVoucherResendPinVO;
import com.btsl.voms.vomsreport.businesslogic.VoucherResendDAO;
import com.web.user.businesslogic.UserWebDAO;

@Service("VoucherPinResendService")
public class VoucherPinResendSeriveImpl implements VoucherPinResendService {

	public static final Log LOG = LogFactory.getLog(VoucherPinResendSeriveImpl.class.getName());
	public static final String classname = "VoucherPinResendSeriveImpl";

	@Override
	public VoucherPinResendResponseVO viewVoucherList(Connection con, String loginID, HttpServletResponse response1,
			VoucherPinResendRequestVO requestVO) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "viewVoucherList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		VoucherPinResendResponseVO response = new VoucherPinResendResponseVO();
		Date transDate = null;
		String msisdn = requestVO.getSubscriberMsisdn();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		VomsVoucherResendPinVO resendVO = new VomsVoucherResendPinVO();
		VoucherResendDAO resendDAO = new VoucherResendDAO();
		ArrayList transList = null;
		ArrayList transList1 = new ArrayList<>();

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			resendVO.setServiceType(PretupsI.SERVICE_TYPE_EVD);

			if (!BTSLUtil.isNullString(requestVO.getTransactionid())) {
				resendVO.setTransferID(requestVO.getTransactionid());
				transList = resendDAO.getTransactionDetails1(con, resendVO);
				if (transList.isEmpty()) {
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_FAIL,null);
					response.setMessage(msg);
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.VOUCHER_FAIL, 0, null);
				} else {

					response.setTransList(transList);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_SUCCESS, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.VOUCHER_SUCCESS);

				}

			}

			else {
				transDate = BTSLUtil.getDateFromDateString(requestVO.getDate());
				int noOfDays = BTSLUtil.getDifferenceInUtilDates(transDate, new Date());
				int dayDiff = 0;
				dayDiff = Integer.parseInt(Constants.getProperty("VALID_DAY_DIFF"));
				String day = Integer.toString(dayDiff);
				if (noOfDays > dayDiff) {
					String args[] = { String.valueOf(dayDiff) };
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_DATE,new String[]{day});
					response.setMessage(msg);
					throw new BTSLBaseException(PretupsErrorCodesI.VOUCHER_DATE, args);
				}

				isValidMSISDN(msisdn, userVO, PretupsI.MSISDN_CHECK_RETA);
				resendVO.setRetailerMSISDN(msisdn);
				resendVO.setTransferDate(requestVO.getDate());
				transList = resendDAO.getTransactionDetails(con, resendVO);
				Integer size = transList.size();
				if (transList.isEmpty()) {
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_FAIL,null);
					response.setMessage(msg);
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.VOUCHER_FAIL, 0, null);
				} else {
					for (int i = 0; i < size; i++) {
						transList1.add(transList.get(i));
					}

					response.setTransList(transList1);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_SUCCESS, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.VOUCHER_SUCCESS);

				}

			}
		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessage(msg);
			}
				
				response.setMessageCode(be.getMessage());
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.VOUCHER_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;

	}

	public void isValidMSISDN(String msisdn, UserVO userVO, String type) throws BTSLBaseException {
		String METHOD_NAME = "isValidMSISDN";
		String _filteredMsisdn;
		String _msisdnPrefix;
		String _networkCode;
		NetworkPrefixVO _networkPrefixVO = null;

		_filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
		_msisdnPrefix = PretupsBL.getMSISDNPrefix(_filteredMsisdn);
		_networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_msisdnPrefix);
		if (_networkPrefixVO == null) {
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN, 0, null);

		}
		_networkCode = _networkPrefixVO.getNetworkCode();
		if (!_networkCode.equals(userVO.getNetworkID())) {
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN, 0, null);
		}

	}

	public VoucherPinResetDetailResponseVO viewVoucherDetailList(Connection con, String loginID,
			HttpServletResponse response1, VoucherPinResendRequestVO requestVO) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "viewVoucherDetailList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		VoucherPinResetDetailResponseVO response = new VoucherPinResetDetailResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		Date transDate = null;
		C2STransferDAO c2STransferDAO = null;
		ArrayList c2sTransferItemsVOList = new ArrayList();

		try {

			c2STransferDAO = new C2STransferDAO();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			transDate = BTSLUtil.getDateFromDateString(requestVO.getDate());
			response.setTransferVOList(c2STransferDAO.loadC2STransferVOList(con, userVO.getNetworkID(), transDate,
					transDate, null, null, requestVO.getTransactionid(), PretupsI.SERVICE_TYPE_FOR_EVD));
			c2sTransferItemsVOList = c2STransferDAO.loadC2STransferItemsVOList(con, requestVO.getTransactionid(),
					transDate, null);
			response.setTransferItemsVOList(c2sTransferItemsVOList);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_SUCCESS);

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.VOUCHER_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;

	}

	public BaseResponse sendPin(Connection con, String loginID, HttpServletResponse response1,
			VoucherPinResendRequestVO request) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "sendPin";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		BaseResponse response = new BaseResponse();
		UserWebDAO userwebDAO = null;
		VoucherResendDAO resendDAO = null;
		String[] arr = null;

		try {

			BTSLMessages btslMessage = null;
			userwebDAO = new UserWebDAO();
			UserEventRemarksVO userRemarksVO = null;
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			StringBuffer successfullySendPin = new StringBuffer();
			Date currentDate = new Date();
			StringBuffer pinIsNull = new StringBuffer();
			resendDAO = new VoucherResendDAO();
			String pin = VomsUtil.decryptText(resendDAO.getPin(con, request.getSerialNo()));
			if (BTSLUtil.isNullString(pin)) {
				pinIsNull.append(request.getSerialNo());
			}
			arr = new String[3];
			arr[0] = pin;
			arr[1] = request.getSerialNo();
			arr[2] = request.getTransactionid();
			if(!BTSLUtil.isNullString(request.getRemarks())) {
			int suspendRemarkCount = 0;
			ArrayList<UserEventRemarksVO> deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
			deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
			userRemarksVO = new UserEventRemarksVO();
			userRemarksVO.setCreatedBy(userVO.getUserID());
			userRemarksVO.setCreatedOn(currentDate);
			userRemarksVO.setEventType(PretupsI.VOUCHER_RESEND);
			userRemarksVO.setMsisdn(userVO.getMsisdn());
			userRemarksVO.setRemarks(request.getRemarks());
			userRemarksVO.setUserID(userVO.getUserID());
			userRemarksVO.setUserType(userVO.getUserType());
			userRemarksVO.setModule(PretupsI.TRANSFER_STOCK_TYPE_HOME);
			deleteSuspendRemarkList.add(userRemarksVO);
			suspendRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
			con.commit();
			}
			successfullySendPin.append(request.getSerialNo());
			successfullySendPin.append(",");
			btslMessage = new BTSLMessages(PretupsErrorCodesI.VOUCHER_PIN_RESEND, arr);
			PushMessage pushMsg = new PushMessage(request.getCustomerMsisdn(), btslMessage,
					request.getTransactionid(), request.getRequestGatewayCode(), locale, userVO.getNetworkID());
			pushMsg.push();
			if((pinIsNull.length() > 0)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PIN_FAIL, 0, null);
			}
			else {
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PIN_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.PIN_SUCCESS);
			}

		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.PIN_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.PIN_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;

	}

}
