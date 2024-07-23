package com.btsl.pretups.subscriber.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

/*
 * This class provides methods for validating Business Rules for Bar User
 */
@Component
public class BarredUserValidator {

	public static final Log _log = LogFactory.getLog(BarredUserValidator.class.getName());

	/**
	 * Validate request data using common validator api
	 *
	 * @param barredUserVO
	 *            The BarredUserVO object having data to be processed
	 * @param type type of validation
	 *            
	 * @param response
	 *            PretupsResponse for storing response
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws ValidatorException
	 */
	public void validateRequestData(String type, PretupsResponse<?> response, BarredUserVO barredUserVO, String referenceName) throws ValidatorException, IOException, SAXException {
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserValidator#validateRequestData", "Entered ");
		}
		
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
				.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, barredUserVO, referenceName);
		Map<String, String> errorMessages = commonValidator.validateModel();
		response.setFieldError(errorMessages);
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserValidator#validateRequestData", "Exiting");
		}

	}

	/**
	 * Validate business rules for Bar user
	 *
	 * @param barUser
	 *            The BarredUserVO object having data to be processed
	 * @param userVO
	 *            UserVO object
	 * @param connection
	 *            Connection object
	 * @param pretupsResponse
	 *            PretupsResponse for storing response
	 * @return
	 * @throws BTSLBaseException,
	 *             SQLException
	 */
	@SuppressWarnings("unchecked")
	public void validateBarredUser(BarredUserVO barUser, UserVO userVO, Connection connection,
			PretupsResponse<?> pretupsResponse, String validationType) throws SQLException, BTSLBaseException {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("BarredUserValidator#validateBarredUser", "Entered ");
			}
			String msisdnPrefix = PretupsBL.getMSISDNPrefix(barUser.getMsisdn());
			NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			if (networkPrefixVO != null) {
				if (userVO.getNetworkID().equalsIgnoreCase(networkPrefixVO.getNetworkCode())) {
					barUser.setNetworkCode(networkPrefixVO.getNetworkCode());
				} else {
					pretupsResponse.setFormError("subscriber.barreduser.notauthorized");
					pretupsResponse.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BARRED_USER_UNAUTHORIZED);
					return;
				}
			} else {
				pretupsResponse.setFormError("subscriber.barreduser.unsupportednetwork");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BARRED_USER_UNSUPPORTED_NETWORK);
				return;
			}
			BarredUserDAO barredUserDAO = new BarredUserDAO();
			if (validationType.equalsIgnoreCase(PretupsI.BARUSER)) {
				if (barredUserDAO.isExists(connection, barUser.getModule(), barUser.getNetworkCode(),
						barUser.getMsisdn(), barUser.getUserType(), barUser.getBarredType())) {
					pretupsResponse.setFormError("subscriber.barreduser.alreadyexists");
					pretupsResponse.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BARRED_USER_ALREADY_EXIST);
					return;
				}
			} else {
				if (!barredUserDAO.isExists(connection, barUser.getModule(), barUser.getNetworkCode(),
						barUser.getMsisdn(), barUser.getUserType(), barUser.getBarredType())) {
					pretupsResponse.setFormError("subscriber.unbaruser.notexists");
					pretupsResponse.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_UNBARRED_USER_NOT_EXIST);
				}
			}

			if (PretupsI.C2S_MODULE.equals(barUser.getModule())
					&& PretupsI.USER_TYPE_SENDER.equals(barUser.getUserType())) {
				if (!BTSLUtil.isNullString(barUser.getMsisdn())) {
					ChannelUserDAO userDAO = new ChannelUserDAO();
					if (!userDAO.isPhoneExists(connection, barUser.getMsisdn())) {
						pretupsResponse.setFormError("subscriber.barreduser.c2snoactiveuser",
								new String[] { barUser.getMsisdn() });
						pretupsResponse.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BARRED_USER_C2S_NO_ACTIVE_USER);
						return;
					}
				}

			}
			if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)
					&& !(userVO.getMsisdn().equals(barUser.getMsisdn()))) {
				boolean isExist = false;
				String userID = null;
				if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO().getCategoryType())
						&& PretupsI.NO.equals(userVO.getCategoryVO().getHierarchyAllowed())) {
					userID = userVO.getParentID();
				} else {
					userID = userVO.getUserID();
				}
				List<ListValueVO> childUserList = null;

				UserWebDAO userwebDao = new UserWebDAO();
				ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();

				childUserList = userwebDao.loadUserListByLogin(connection, userVO.getUserID(), PretupsI.STAFF_USER_TYPE,
						"%");

				if (!(BTSLUtil.isNullString(barUser.getMsisdn()))) {

					List<ChannelUserVO> hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(connection, userID,
							false);
					ChannelUserVO channelUserVO = null;

					String filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(barUser.getMsisdn());

					if (hierarchyList != null && !hierarchyList.isEmpty()) {

						for (int i = 0, j = hierarchyList.size(); i < j; i++) {
							channelUserVO = hierarchyList.get(i);
							if (channelUserVO.getMsisdn().equals(filteredMsisdn)) {
								isExist = true;
								break;
							}
						}
					}
				}

				if (!isExist) {
					for (int i = 0; i < childUserList.size(); i++) {
						ListValueVO childUser = childUserList.get(i);
						if (barUser.getMsisdn().equals(childUser.getOtherInfo2())) {
							isExist = true;
							break;
						}
					}
				}
				if (!isExist) {
					pretupsResponse.setFormError("subscriber.barreduser.msg.mobileno.notauthorise",
							new String[] { barUser.getMsisdn() });
					pretupsResponse.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BARRED_USER_MOBILE_NOT_AUTHORISE);
					return;
				}
			}
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("BarredUserValidator#validateBarredUser", "Exiting");
			}
		}

	}
}
