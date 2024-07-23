package com.restapi.networkadmin;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.restapi.networkadmin.requestVO.ChannelTransferDeleteRequestVO;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import com.web.pretups.transfer.businesslogic.TransferWebDAO;

@Service
public class C2STransferRuleServiceImpl implements C2STransferRuleService {

	public static final Log LOG = LogFactory.getLog(C2STransferRuleServiceImpl.class.getName());
	public static final String classname = "C2STransferRuleServiceImpl";

	public C2STransferRuleResponseVO viewC2SList(Connection con, String loginID, String domainCode, String categoryCode,
			String gradeCode, String statusCode, String gatewayCode, HttpServletResponse response1) {

		final String METHOD_NAME = "viewC2SList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		C2STransferRuleResponseVO response = new C2STransferRuleResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		TransferWebDAO transferwebDAO = null;
		ArrayList finalList = new ArrayList<>();
		ArrayList rulesList = new ArrayList<>();

		try {
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			transferwebDAO = new TransferWebDAO();
			rulesList = transferwebDAO.loadTransferRuleList1(con, userVO.getNetworkID(), PretupsI.C2S_MODULE,
					statusCode, gatewayCode, domainCode, categoryCode, gradeCode);

			if (BTSLUtil.isNullOrEmptyList(rulesList)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.C2S_FAIL, 0, null);
			}

			else {
				for (int i = 0; i < rulesList.size(); i++) {
					finalList.add(rulesList.get(i));
				}

			}

			response.setResultList(finalList);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2S_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.C2S_SUCCESS);
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
					PretupsErrorCodesI.C2S_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.C2S_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public C2STransferRuleResponseVO1 viewC2SDropdownList(Connection con, String loginID,
			HttpServletResponse response1) {
		final String METHOD_NAME = "viewC2SDropdownList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		C2STransferRuleResponseVO1 response = new C2STransferRuleResponseVO1();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		ArrayList serviceClassListfinal = new ArrayList<>();
		ArrayList subServiceTypeIdListfinal = new ArrayList<>();
		ArrayList serviceTypeIdListfinal = new ArrayList<>();
		ArrayList<ListValueVO> cardGroupListfinal = new ArrayList<>();
		ArrayList<ListValueVO> gatewayListFinal = new ArrayList<>();
		CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
		ServiceClassWebDAO serviceClasswebDAO = new ServiceClassWebDAO();
		CardGroupDAO cardGroupDAO = new CardGroupDAO();
		MessageGatewayWebDAO msgGatewaywebDAO = new MessageGatewayWebDAO();

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			serviceTypeIdListfinal = cardGroupDAO.loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
			if (!BTSLUtil.isNullOrEmptyList(serviceTypeIdListfinal)) {
				response.setServiceTypeList(serviceTypeIdListfinal);
			}

			gatewayListFinal = msgGatewaywebDAO.loadGatewayCodeList(con);
			if (!BTSLUtil.isNullOrEmptyList(gatewayListFinal)) {
				response.setGatewayList(gatewayListFinal);
			}
			cardGroupListfinal = cardGroupSetDAO.loadCardGroupSetForTransferRule(con, userVO.getNetworkID(),
					PretupsI.C2S_MODULE, PretupsI.TRANSFER_RULE_NORMAL);
			if (!BTSLUtil.isNullOrEmptyList(cardGroupListfinal)) {
				response.setCardGroupList(cardGroupListfinal);
			}
			subServiceTypeIdListfinal = ServiceSelectorMappingCache.loadSelectorDropDownForTrfRule();
			if (!BTSLUtil.isNullOrEmptyList(subServiceTypeIdListfinal)) {
				response.setSubServiceTypeList(subServiceTypeIdListfinal);
			}
			String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','"
					+ PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "','"
					+ PretupsI.SERVICE_TYPE_IAT + "'";
			serviceClassListfinal = serviceClasswebDAO.loadServiceClassList(con, interfaceCategory);
			if (!BTSLUtil.isNullOrEmptyList(serviceClassListfinal)) {
				response.setServiceClassList(serviceClassListfinal);
			}
			if (BTSLUtil.isNullOrEmptyList(cardGroupListfinal) && BTSLUtil.isNullOrEmptyList(serviceClassListfinal)
					&& BTSLUtil.isNullOrEmptyList(subServiceTypeIdListfinal)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.C2S_FAIL, 0, null);
			}

			else {

				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2S_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.C2S_SUCCESS);
			}

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
					PretupsErrorCodesI.C2S_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.C2S_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}

	public C2STransferListAddResponseVO addTransfer(Connection con, String loginID, C2STransferRuleRequestVO requestVO,
			HttpServletResponse response1) {

		final String METHOD_NAME = "addTransfe";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		C2STransferListAddResponseVO response = new C2STransferListAddResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		Date currentDate = new Date();
		int updateCount = 0;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		TransferWebDAO transferwebDAO = new TransferWebDAO();
		KeyArgumentVO argumentVO = null;
		final ArrayList errorList = new ArrayList();
		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			int transferRulesVOListSize = requestVO.getTransferList().size();
			for (int i = 0; i < transferRulesVOListSize; i++) {
				TransferRulesVO transferRulesVO = new TransferRulesVO();
				transferRulesVO.setSenderServiceClassID(PretupsI.ALL);
				transferRulesVO.setModule(PretupsI.C2S_MODULE);
				transferRulesVO.setNetworkCode(userVO.getNetworkID());
				transferRulesVO.setSenderSubscriberType(requestVO.getTransferList().get(i).getSenderSubscriberType());
				transferRulesVO
						.setReceiverSubscriberType(requestVO.getTransferList().get(i).getRecieverSubscriberType());
				transferRulesVO
						.setReceiverServiceClassID(requestVO.getTransferList().get(i).getRecieverServiceClassId());
				transferRulesVO.setSubServiceTypeId(requestVO.getTransferList().get(i).getSubServiceId());
				transferRulesVO.setServiceType(requestVO.getTransferList().get(i).getServiceType());
				transferRulesVO.setGatewayCode(requestVO.getTransferList().get(i).getGatewayCode());
				transferRulesVO.setGradeCode1(requestVO.getTransferList().get(i).getGradeCode());
				transferRulesVO.setCategoryCode1(requestVO.getTransferList().get(i).getCategoryCode());
				if (transferwebDAO.isTransferRuleExist(con, transferRulesVO)) {
					final String[] rowIDArr = new String[1];
					rowIDArr[0] = String.valueOf(i + 1);
					argumentVO = new KeyArgumentVO();
					argumentVO.setKey(PretupsI.TRANSFER);
					argumentVO.setArguments(rowIDArr);
					errorList.add(argumentVO);
				}
			}
			if (!errorList.isEmpty()) {
				response.setErrorList(errorList);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TRANSFER_RULE_EXIXTS, 0, null);
			}

			else {
				for (int i = 0; i < transferRulesVOListSize; i++) {

					TransferRulesVO transferRulesVO = new TransferRulesVO();
					transferRulesVO.setModifiedBy(userVO.getUserID());
					transferRulesVO.setModifiedOn(currentDate);
					transferRulesVO.setCreatedBy(userVO.getUserID());
					transferRulesVO.setCreatedOn(currentDate);
					transferRulesVO.setCellGroupId(PretupsI.ALL);
					transferRulesVO.setSenderServiceClassID(PretupsI.ALL);
					transferRulesVO.setModule(PretupsI.C2S_MODULE);
					transferRulesVO.setNetworkCode(userVO.getNetworkID());
					transferRulesVO
							.setSenderSubscriberType(requestVO.getTransferList().get(i).getSenderSubscriberType());
					transferRulesVO
							.setReceiverSubscriberType(requestVO.getTransferList().get(i).getRecieverSubscriberType());
					transferRulesVO
							.setReceiverServiceClassID(requestVO.getTransferList().get(i).getRecieverServiceClassId());
					transferRulesVO.setCardGroupSetID(requestVO.getTransferList().get(i).getCardGroupSet());
					transferRulesVO.setStatus(PretupsI.USER_STATUS_RESUMED);
					transferRulesVO.setSubServiceTypeId(requestVO.getTransferList().get(i).getSubServiceId());
					transferRulesVO.setServiceType(requestVO.getTransferList().get(i).getServiceType());
					transferRulesVO.setGatewayCode(requestVO.getTransferList().get(i).getGatewayCode());
					transferRulesVO.setGradeCode1(requestVO.getTransferList().get(i).getGradeCode());
					transferRulesVO.setCategoryCode1(requestVO.getTransferList().get(i).getCategoryCode());
					updateCount += transferwebDAO.addTransferRule(con, transferRulesVO);
				}
			}
			if (updateCount >= 1) {
				con.commit();
				response.setStatus((HttpStatus.SC_OK));
				response.setErrorList(null);
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRANSFER_ADD_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.TRANSFER_ADD_SUCCESS);
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TRANSFER_ADD_FAIL, 0, null);
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
					PretupsErrorCodesI.TRANSFER_ADD_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TRANSFER_ADD_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public BaseResponse modifyTransfer(Connection con, String loginID, ChannelTransferModifyRequestVO requestVO,
			HttpServletResponse response1) {

		final String METHOD_NAME = "modifyTransfer";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		BaseResponse response = new BaseResponse();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		Date currentDate = new Date();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		TransferWebDAO transferwebDAO = new TransferWebDAO();

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			int transferRulesVOListSize = requestVO.getTransferList().size();
			int updateCount = 0;

			for (int i = 0; i < transferRulesVOListSize; i++) {

				TransferRulesVO transferRulesVO = new TransferRulesVO();
				transferRulesVO.setModifiedBy(userVO.getUserID());
				transferRulesVO.setModifiedOn(currentDate);
				transferRulesVO.setCellGroupId(PretupsI.ALL);
				transferRulesVO.setSenderServiceClassID(PretupsI.ALL);
				transferRulesVO.setModule(PretupsI.C2S_MODULE);
				transferRulesVO.setNetworkCode(userVO.getNetworkID());
				transferRulesVO.setSenderSubscriberType(requestVO.getTransferList().get(i).getSenderSubscriberType());
				transferRulesVO
						.setReceiverSubscriberType(requestVO.getTransferList().get(i).getRecieverSubscriberType());
				transferRulesVO
						.setReceiverServiceClassID(requestVO.getTransferList().get(i).getRecieverServiceClassId());
				transferRulesVO.setCardGroupSetID(requestVO.getTransferList().get(i).getCardGroupSet());
				transferRulesVO.setStatus(requestVO.getTransferList().get(i).getStatus());
				transferRulesVO.setSubServiceTypeId(requestVO.getTransferList().get(i).getSubServiceId());
				transferRulesVO.setServiceType(requestVO.getTransferList().get(i).getServiceType());
				transferRulesVO.setGatewayCode(requestVO.getTransferList().get(i).getGatewayCode());
				transferRulesVO.setGradeCode(requestVO.getTransferList().get(i).getGradeCode());
				transferRulesVO.setCategoryCode(requestVO.getTransferList().get(i).getCategoryCode());
				updateCount += transferwebDAO.updateTransferRule(con, transferRulesVO);

			}

			if (updateCount >= 1) {
				con.commit();
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRANSFER_MODIFY_SUCCESS,
						null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.TRANSFER_ADD_SUCCESS);
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TRANSFER_MODIFY_FAIL, 0, null);
			}
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
					PretupsErrorCodesI.TRANSFER_MODIFY_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TRANSFER_MODIFY_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	/**
	 * @author sarthak.saini
	 * @param con
	 * @param loginID
	 * @param requestVO
	 * @param response1
	 * @return
	 */
	public BaseResponse deleteTransfer(Connection con, String loginID, ChannelTransferDeleteRequestVO requestVO,
			HttpServletResponse response1) {
		final String METHOD_NAME = "deleteTransfer";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		BaseResponse response = new BaseResponse();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		Date currentDate = new Date();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		TransferWebDAO transferwebDAO = new TransferWebDAO();
		int deleteCount = 0;
		int deleteRequired = 0;
		try {
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

			for (int i = 0; i < requestVO.getTransferList().size(); i++) {
				deleteRequired++;
				TransferRulesVO transferRulesVO = new TransferRulesVO();
				transferRulesVO.setNetworkCode(userVO.getNetworkID());
				transferRulesVO.setModifiedBy(userVO.getUserID());
				transferRulesVO.setModifiedOn(currentDate);
				transferRulesVO.setStatus(PretupsI.TRANSFER_RULE_STATUS_DELETE);
				transferRulesVO.setModule(PretupsI.C2S_MODULE);
				transferRulesVO.setSenderSubscriberType(requestVO.getTransferList().get(i).getSenderSubscriberType());
				transferRulesVO.setReceiverSubscriberType(requestVO.getTransferList().get(i).getRecieverSubscriberType());
				transferRulesVO.setSenderServiceClassID(PretupsI.ALL);
				transferRulesVO.setReceiverServiceClassID(requestVO.getTransferList().get(i).getRecieverServiceClassId());
				transferRulesVO.setSubServiceTypeId(requestVO.getTransferList().get(i).getSubServiceId());
				transferRulesVO.setServiceType(requestVO.getTransferList().get(i).getServiceType());
				transferRulesVO.setGatewayCode(requestVO.getTransferList().get(i).getGatewayCode());
				transferRulesVO.setGradeCode(requestVO.getTransferList().get(i).getGradeCode());
				transferRulesVO.setCategoryCode(requestVO.getTransferList().get(i).getCategoryCode());
				deleteCount += transferwebDAO.deleteTransferRule(con, transferRulesVO);
			}
			if(deleteCount==deleteRequired) {
				con.commit();
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TRANSFER_DELETE_SUCCESS,
						null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.TRANSFER_DELETE_SUCCESS);
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TRANSFER_DELETE_FAIL, 0, null);
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
					PretupsErrorCodesI.TRANSFER_DELETE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TRANSFER_DELETE_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;

	}

}
