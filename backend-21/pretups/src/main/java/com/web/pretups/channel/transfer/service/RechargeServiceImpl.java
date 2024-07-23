package com.web.pretups.channel.transfer.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.loadcontroller.NetworkLoadVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.WebRechargeLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.util.KeyGeneration;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.transfer.businesslogic.EnquiryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.web.pretups.channel.transfer.web.C2SRechargeModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import nl.captcha.Captcha;

@Service("rechargeService")
public class RechargeServiceImpl implements RechargeService {

	private Log _log = LogFactory.getLog(this.getClass().getName());
	private static final String SUCCESS_KEY = "success";
	private static final String FAIL_KEY = "fail";
	private static final String ERROR_MAP = "errorMap";

	@Override
	public C2SRechargeModel loadServicesBalance(ChannelUserVO channelUserVO, C2SRechargeModel c2sRechModel, Model model)
			throws Exception {
		ArrayList subServiceTypeList = null;
		ArrayList currencyList = null;
		ArrayList languageList = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		C2SRechargeModel c2sRechargeModel = c2sRechModel;
		String defaultCurrency = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY);
		String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		try {

			subServiceTypeList = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroupPPEL();
			Collections.sort(subServiceTypeList);
			final LocaleMasterDAO localeMasterDAO = new LocaleMasterDAO();
			languageList = localeMasterDAO.loadLocaleMasterDetails();
			currencyList = CurrencyConversionCache.loadTargetCurrencyMapping(defaultCurrency, defaultCountry);
			c2sRechargeModel.setCurrencyList(currencyList);
			Collections.sort(languageList);

			c2sRechargeModel.setLanguageList(languageList);

			c2sRechargeModel.setSubServiceTypeList(subServiceTypeList);

			if (c2sRechargeModel.getCurrencyListSize() >= 1)
				c2sRechargeModel.setMultiCurrencyRecharge(true);

			final ArrayList assignedserviceList = channelUserVO.getAssociatedServiceTypeList();
			if (assignedserviceList == null || assignedserviceList.isEmpty()) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.noserviceassign"));
				return c2sRechargeModel;
			}
			c2sRechargeModel.setLoginUserMsisdn(channelUserVO.getMsisdn());
			if (!BTSLUtil.isNullString(channelUserVO.getActiveUserMsisdn())
					&& !PretupsI.NOT_AVAILABLE.equals(channelUserVO.getActiveUserMsisdn())
					&& !channelUserVO.getMsisdn().equals(channelUserVO.getActiveUserMsisdn())) {
				c2sRechargeModel.setDispalyMsisdn(channelUserVO.getActiveUserMsisdn());
			} else {
				c2sRechargeModel.setDispalyMsisdn(channelUserVO.getMsisdn());
			}
			if (!PretupsI.YES.equals(channelUserVO.getPinRequired())) {
				c2sRechargeModel.setPinRequired(PretupsI.NO);
				String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
				c2sRechargeModel.setPin(c2sDefaultSmsPin);
			}

			final ArrayList serviceKeywordList = new ArrayList();

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			final ArrayList serviceKeywordCacheList = new ServiceKeywordDAO().loadServiceCache(con);
			Iterator iterator = serviceKeywordCacheList.iterator();
			String key = null;

			final ArrayList serviceTypeList = new ArrayList();

			int i = 0;
			ServiceKeywordCacheVO serviceKeywordCacheVO2 = null;
			while (iterator.hasNext()) {
				serviceKeywordCacheVO2 = (ServiceKeywordCacheVO) iterator.next();
				if (PretupsI.C2S_MODULE.equals(serviceKeywordCacheVO2.getModule())
						&& PretupsI.YES.equals(serviceKeywordCacheVO2.getExternalInterface())
						&& PretupsI.GATEWAY_TYPE_WEB.equals(serviceKeywordCacheVO2.getRequestInterfaceType())) {
					serviceKeywordList.add(serviceKeywordCacheVO2);
				}
			}

			iterator = serviceKeywordList.iterator();
			final int assignServiceSize = assignedserviceList.size();
			ListValueVO listVO = null;
			while (iterator.hasNext()) {
				serviceKeywordCacheVO2 = (ServiceKeywordCacheVO) iterator.next();
				for (i = 0; i < assignServiceSize; i++) {
					key = serviceKeywordCacheVO2.getServiceType() + ":"
							+ serviceKeywordCacheVO2.getUseInterfaceLanguage() + ":" + serviceKeywordCacheVO2.getType();

					if (key.split(":")[0].equalsIgnoreCase(((ListValueVO) assignedserviceList.get(i)).getValue())) {
						listVO = new ListValueVO(serviceKeywordCacheVO2.getName(), key);

						if ((listVO.getValue().split(":")[0]).equals(PretupsI.GIFT_RECHARGE_CODE)) {
							c2sRechargeModel.setGiftServiceExist(true);
						}
						if ((listVO.getValue().split(":")[0]).equals(PretupsI.SERVICE_TYPE_MVD)) {
							c2sRechargeModel.setMultipleVoucherDownloadExist(true);
						}
						if ((listVO.getValue().split(":")[0]).equals(PretupsI.SERVICE_TYPE_EXT_CHNL_RECHARGE_PSTN)) {
							c2sRechargeModel.setPstnRechargeExist(true);
						}
						if ((listVO.getValue().split(":")[0]).equals(PretupsI.SERVICE_TYPE_EXT_CHNL_RECHARGE_INTR)) {
							c2sRechargeModel.setPstnRechargeExist(true);
						}
						if ((listVO.getValue().split(":")[0])
								.equals(PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE)) {
							c2sRechargeModel.setIatIntRechargeExist(true);
						}
						if (((listVO.getValue().split(":")[0]).equals(PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE))
								|| ((listVO.getValue().split(":")[0])
										.equals(PretupsI.IAT_SERVICE_TYPE_ROAM_RECHARGE))) {
							c2sRechargeModel.setIatRecLangRecharge(true);
						}

						if (!(listVO.getValue().split(":")[0]).equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
							serviceTypeList.add(listVO);
						}
					}
				}
			}
			c2sRechargeModel.setServiceKeywordList(serviceKeywordList);
			c2sRechargeModel.setServiceTypeList(serviceTypeList);

			if (languageList != null && languageList.size() == 1) {
				c2sRechargeModel.setLanguageCode(((ListValueVO) languageList.get(0)).getValue());
				c2sRechargeModel.setLanguageCodeDesc(
						BTSLUtil.getOptionDesc(c2sRechargeModel.getLanguageCode(), c2sRechargeModel.getLanguageList())
								.getLabel());
			}
			if (subServiceTypeList != null && subServiceTypeList.size() == 1) {
				c2sRechargeModel.setSubServiceType(((ListValueVO) subServiceTypeList.get(0)).getValue());
				c2sRechargeModel.setSubServiceTypeDes(BTSLUtil
						.getOptionDesc(c2sRechargeModel.getSubServiceType(), c2sRechargeModel.getSubServiceTypeList())
						.getLabel());
			}
			final String showbalance = new ChannelUserWebDAO()
					.loadChannelUserBalanceServiceWise(channelUserVO.getUserID());
			c2sRechargeModel.setShowBalance(showbalance);
			String[] serviceBal = null;
			if (!serviceTypeList.isEmpty()) {
				c2sRechargeModel.setServiceType(((ListValueVO) serviceTypeList.get(0)).getValue());
				c2sRechargeModel.setServiceTypeDes(
						BTSLUtil.getOptionDesc(c2sRechargeModel.getServiceType(), c2sRechargeModel.getServiceTypeList())
								.getLabel());
				final String service = c2sRechargeModel.getServiceType().split(":")[0];
				if (!BTSLUtil.isNullString(showbalance)) {

					serviceBal = showbalance.split(",");
					for (int a = 0; a < serviceBal.length; a++) {
						final String[] serviceType = serviceBal[a].split(":");
						if (serviceType[0].equals(service)) {
							c2sRechargeModel.setCurrentBalance(BTSLUtil.parseStringToLong(serviceType[1]));
							break;
						}

					}
				} else {
					c2sRechargeModel.setCurrentBalance(0);
				}
			} else {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.noserviceassign"));
				return c2sRechargeModel;
			}
			String c2sRechargeMultipleEntry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_RECHARGE_MULTIPLE_ENTRY);
			c2sRechargeModel.setMultipleEntry(c2sRechargeMultipleEntry);
			c2sRechargeModel.setCountryCode(Constants.getProperty("RC_DISPLAY_COUNTRY_CODE"));
			c2sRechargeModel.setDenominationList(VOMSVoucherDAO.loadDenominationForBulkVoucherDistribution(false));
		} catch (Exception e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("RechargeServiceImpl#loadServicesBalance");
				mcomCon = null;
			}
		}
		return c2sRechargeModel;
	}

	/**
	 * method called for loading balance on back button
	 * 
	 * @param channelUserVO
	 * @param c2sRechargeModel
	 * @throws BTSLBaseException
	 */
	public void balance(ChannelUserVO channelUserVO, C2SRechargeModel c2sRechargeModel) throws BTSLBaseException {
		final String showbalance = new ChannelUserWebDAO().loadChannelUserBalanceServiceWise(channelUserVO.getUserID());
		c2sRechargeModel.setShowBalance(showbalance);
		String[] serviceBal = null;
		final String service = c2sRechargeModel.getServiceType().split(":")[0];
		if (!BTSLUtil.isNullString(showbalance)) {

			serviceBal = showbalance.split(",");
			for (int a = 0; a < serviceBal.length; a++) {
				final String[] serviceType = serviceBal[a].split(":");
				if (serviceType[0].equals(service)) {
					c2sRechargeModel.setCurrentBalance(BTSLUtil.parseStringToLong(serviceType[1]));
					break;
				}

			}
		} else {
			c2sRechargeModel.setCurrentBalance(0);
		}

	}

	@Override
	public boolean confirmC2SRecharge(ChannelUserVO channelUserVO, C2SRechargeModel c2sRechModel,
			HttpServletRequest request, Model model, BindingResult bindingResult) throws BTSLBaseException {
		final String methodName = "confirmC2SRecharge#RechargeServiceImpl";
		String filteredMsisdn;
		String msisdnPrefix;
		NetworkPrefixVO networkPrefixVO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		C2SRechargeModel c2sRechargeModel = c2sRechModel;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			c2sRechargeModel.setServiceTypeDes(
					BTSLUtil.getOptionDesc(c2sRechargeModel.getServiceType(), c2sRechargeModel.getServiceTypeList())
							.getLabel());
			c2sRechargeModel.setSubServiceTypeDes(BTSLUtil
					.getOptionDesc(c2sRechargeModel.getSubServiceType(), c2sRechargeModel.getSubServiceTypeList())
					.getLabel());
			c2sRechargeModel.setCurrencyServiceCode(null);

			if (!validate(request, c2sRechargeModel, model, bindingResult)) {
				return false;
			}

			if (PretupsI.MULTI_CURRENCY_SERVICE_TYPE.equals((c2sRechargeModel.getServiceType()).split(":")[0])) {
				c2sRechargeModel.setCurrencyCodeDesc(
						BTSLUtil.getOptionDesc(c2sRechargeModel.getCurrencyCode(), c2sRechargeModel.getCurrencyList())
								.getLabel());
				c2sRechargeModel.setCurrencyServiceCode((c2sRechargeModel.getServiceType()).split(":")[0]);
			}

			// gifter case
			c2sRechargeModel.setGifterServiceCode(c2sRechargeModel.getServiceType().split(":")[0]);
			if (PretupsI.GIFT_RECHARGE_CODE.equals((c2sRechargeModel.getServiceType()).split(":")[0])) {
				c2sRechargeModel.setGifterLanguageCodeDesc(BTSLUtil
						.getOptionDesc(c2sRechargeModel.getGifterLanguageCode(), c2sRechargeModel.getLanguageList())
						.getLabel());
			}

			if ("Y".equals((c2sRechargeModel.getServiceType()).split(":")[1])) {
				c2sRechargeModel.setLanguageCodeDesc(
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.label.notapplicable"));
			} else {
				c2sRechargeModel.setLanguageCodeDesc(
						BTSLUtil.getOptionDesc(c2sRechargeModel.getLanguageCode(), c2sRechargeModel.getLanguageList())
								.getLabel());
			}
			if (PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR
					.equalsIgnoreCase(c2sRechargeModel.getServiceType().split(":")[0])) {
				c2sRechargeModel.setPstnOrIntrServiceCode(true);
			} else if (PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN
					.equalsIgnoreCase(c2sRechargeModel.getServiceType().split(":")[0])) {
				c2sRechargeModel.setPstnOrIntrServiceCode(true);
			} else if (PretupsI.IAT_SERVICE_TYPE_ROAM_RECHARGE
					.equalsIgnoreCase(c2sRechargeModel.getServiceType().split(":")[0])) {
				c2sRechargeModel.setIatRoamRecharge(true);
			} else if (PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE
					.equalsIgnoreCase(c2sRechargeModel.getServiceType().split(":")[0])) {
				c2sRechargeModel.setIatIntRecharge(true);
			}
			if (c2sRechargeModel.isIatRoamRecharge() || c2sRechargeModel.isIatIntRecharge()) {
				c2sRechargeModel.setLanguageCodeDesc(
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.label.notapplicable"));
			}

			if ((!BTSLUtil.isNullString(c2sRechargeModel.getSubscriberMsisdn()))
					&& (!(c2sRechargeModel.isIatRoamRecharge() || c2sRechargeModel.isIatIntRecharge()))) {
				// FilteredMSISDN is replaced by
				// getFilteredIdentificationNumber. This is done because this
				// field can contains msisdn or account id
				if (BTSLUtil.isNullString(c2sRechargeModel.getSubscriberTmpMsisdn())) {
					filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(c2sRechargeModel.getSubscriberMsisdn());// get
																														// the
																														// filtered
																														// MSISDN
																														// from
																														// the
																														// entered
																														// MSISDN
				} else {
					filteredMsisdn = PretupsBL
							.getFilteredIdentificationNumber(c2sRechargeModel.getSubscriberTmpMsisdn());
				}
				msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);// get
																			// the
																			// MSISDN
																			// prefix
																			// from
																			// the
																			// filtered
																			// MSISDN
				networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

				if (networkPrefixVO == null) {
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.nonetworkprefix"));
					return false;
				}
			}
			boolean showCaptcha = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA))).booleanValue();
			if (showCaptcha) {

				final String parm = request.getParameter("j_captcha_response");
				Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
				final String jcaptchaCode1 = captcha.getAnswer();
				if (parm != null && jcaptchaCode1 != null) {
					if (!parm.equals(jcaptchaCode1)) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("captcha.error.wrongentry"));
						return false;
					}
				}
				if (parm == null || parm.isEmpty()) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("captcha.error.wrongentry"));
					return false;
				}
			}

			BarredUserDAO barredUserDAO = new BarredUserDAO();
			boolean isBarred = barredUserDAO.isExists(con, "C2S", channelUserVO.getNetworkID(),
					channelUserVO.getMsisdn(), PretupsI.CHANEL_BARRED_USER_TYPE_SENDER,
					PretupsI.BARRED_TYPE_PIN_INVALID);
			if (isBarred) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.pin.blocked"));
				return false;
			}
			c2sRechargeModel.setDisplayPin(BTSLUtil.getDefaultPasswordText(c2sRechargeModel.getPin()));

			return true;

		} catch (Exception e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("RechargeServiceImpl#balance");
				mcomCon = null;
			}
		}
	}

	@Override
	public C2SRechargeModel recharge(ChannelUserVO channelUserVO, C2SRechargeModel c2sRechargeModel,
			Locale senderLanguage, HttpServletRequest request, Model model) throws BTSLBaseException {
		String methodName = "recharge";
		HttpURLConnection _con = null;
		BufferedReader in = null;
		InstanceLoadVO instanceLoadVO = null;
		StringBuffer loggerMessage = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		HashMap _map = null;
		HashMap map = null;
		String txn_id = null;
		String urlToSend = null;
		String gifterLanguage = null;
		String msisdnPrefix = null;
		String httpURLPrefix = "http://";
		String pin = null;
		String currency = null;

		EnquiryVO _enquiryVO = null;
		ArrayList _enquiryList = null;
		String finalResponse = "";
		C2SRechargeModel c2sRechargeModel1 = c2sRechargeModel;
		try {

			loggerMessage = new StringBuffer();
			final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
			if (messageGatewayVO == null) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.sessiondatanotfound"));
				return c2sRechargeModel1;
			}
			if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.messagegatewaynotactive"));
				return c2sRechargeModel1;
			} else if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus())) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.reqmessagegatewaynotactive"));
				return c2sRechargeModel1;
			}
			// changes for userlifecycle user allowed status check -- start
			String[] strArr = new String[] { c2sRechargeModel1.getSubscriberMsisdn() };
			boolean statusAllowed = false;
			final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(),
					channelUserVO.getCategoryCode(), channelUserVO.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
			if (userStatusVO == null) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString(PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED));
				return c2sRechargeModel1;
			} else {
				final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
				final String status[] = userStatusAllowed.split(",");
				int statusleg=status.length;
				for (int i = 0; i < statusleg; i++) {
					if (status[i].equals(channelUserVO.getStatus())) {
						statusAllowed = true;
					}
				}
				if (statusAllowed == false) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("sender.not.allowed", strArr));
					return c2sRechargeModel1;
				}
			}
			// changes for userlifecycle user allowed status check -- end
			String subServiceValue = " ";
			if (!BTSLUtil.isNullString(c2sRechargeModel1.getSubServiceType())
					&& c2sRechargeModel1.getSubServiceType().contains(":")) {
				subServiceValue = c2sRechargeModel1.getSubServiceType().split(":")[1];
			}
			if (PretupsI.NOT_APPLICABLE.equals(subServiceValue)) {
				subServiceValue = " ";
			}
			String chnlPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			String saperator = null;
			if (!BTSLUtil.isNullString(chnlPlainSmsSeparator)) {
				saperator = chnlPlainSmsSeparator;
			} else {
				saperator = " ";
			}
			/*
			 * done by ashishT during hashing implementation. calculating pin
			 * based on algo used SHA/AES/DES.
			 */
			String pinPasswordEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
			String msgGWPass = null;
			if ("SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
				pin = BTSLUtil.encryptText(c2sRechargeModel1.getPin());
			} else {
				pin = c2sRechargeModel1.getPin();
			}

			// If Encrypted Password check box is not checked. i.e. send
			// password in request as plain.
			if (messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
				msgGWPass = BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword());
			} else {
				// If Encrypted Password check box is checked. i.e. send
				// password in request as encrypted.
				msgGWPass = messageGatewayVO.getRequestGatewayVO().getPassword();
			}

			// FilteredMSISDN is replaced by getFilteredIdentificationNumber
			// This is done because this field can contains msisdn or account id
			if (c2sRechargeModel1.getServiceType().split(":")[0].equalsIgnoreCase(PretupsI.SERVICE_TYPE_MVD)) {
				c2sRechargeModel1.setSubscriberMsisdn(c2sRechargeModel1.getLoginUserMsisdn());
			}
			if ((c2sRechargeModel1.isIatRoamRecharge() || c2sRechargeModel1.isIatIntRecharge())) {
				msisdnPrefix = PretupsBL.getMSISDNPrefix(
						PretupsBL.getFilteredIdentificationNumber(c2sRechargeModel1.getLoginUserMsisdn()));

			} else {
				// Logic change for SID
				String filteredMsisdn = null;
				if (BTSLUtil.isNullString(c2sRechargeModel1.getSubscriberTmpMsisdn())) {
					filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(c2sRechargeModel1.getSubscriberMsisdn());// get
																														// the
																														// filtered
																														// MSISDN
																														// from
																														// the
																														// entered
																														// MSISDN
				} else {
					filteredMsisdn = PretupsBL
							.getFilteredIdentificationNumber(c2sRechargeModel1.getSubscriberTmpMsisdn());
				}

				msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

			}
			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

			currency = c2sRechargeModel1.getCurrencyCode();
			c2sRechargeModel1.setIatIntRecharge(false);
			c2sRechargeModel1.setIatRoamRecharge(false);
			if (networkPrefixVO == null) {
				strArr = new String[] { c2sRechargeModel1.getSubscriberMsisdn() };
				model.addAttribute(FAIL_KEY, PretupsRestUtil
						.getMessageString(PretupsErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, strArr));
				return c2sRechargeModel1;
			}
			final String networkCode = networkPrefixVO.getNetworkCode();
			String smsInstanceID = null;

			// Changed to handle multiple SMS servers for C2S and P2P on
			// 20/07/06
			if (LoadControllerCache.getNetworkLoadHash() != null && LoadControllerCache.getNetworkLoadHash()
					.containsKey(LoadControllerCache.getInstanceID() + "_" + networkCode)) {
				smsInstanceID = ((NetworkLoadVO) (LoadControllerCache.getNetworkLoadHash()
						.get(LoadControllerCache.getInstanceID() + "_" + networkCode))).getC2sInstanceID();
			} else {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.unsuccess"));
				return c2sRechargeModel1;
			}
			instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(
					smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
			if (instanceLoadVO == null) {
				instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(
						smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
			}
			if (instanceLoadVO == null) {
				instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(
						smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
			}
			boolean httpsEnabled = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE))).booleanValue();
			// for https enabling
			if (httpsEnabled) {
				httpURLPrefix = "https://";
			}
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final LocaleMasterDAO localeMasterDAO = new LocaleMasterDAO();
			final String senderlanguageCode = localeMasterDAO.loadLocaleMasterCode(con, senderLanguage.getLanguage(),
					senderLanguage.getCountry());
			String receiverLanguage = null;
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			if ("Y".equals((c2sRechargeModel1.getServiceType()).split(":")[1])) {
				receiverLanguage = localeMasterDAO.loadLocaleMasterCode(con, defaultLanguage, defaultCountry);
			} else {
				receiverLanguage = c2sRechargeModel1.getLanguageCode();
			}
			if (BTSLUtil.isNullString(c2sRechargeModel1.getAmount())) {
				c2sRechargeModel1.setAmount("0");
			}
			boolean multiAmountEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED);
			// for gift recharge
			if ((PretupsI.GIFT_RECHARGE_CODE).equalsIgnoreCase(c2sRechargeModel1.getGifterServiceCode())) {
				if ("Y".equals((c2sRechargeModel1.getServiceType()).split(":")[1])) {
					gifterLanguage = localeMasterDAO.loadLocaleMasterCode(con, defaultLanguage, defaultCountry);
				} else {
					gifterLanguage = c2sRechargeModel1.getGifterLanguageCode();
				}

				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + subServiceValue + saperator + receiverLanguage + saperator
								+ senderlanguageCode + saperator + c2sRechargeModel1.getGifterMsisdn() + saperator
								+ URLEncoder.encode(c2sRechargeModel1.getGifterName(), "UTF16") + saperator
								+ gifterLanguage + saperator + pin,java.nio.charset.StandardCharsets.UTF_8.toString());// done
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();

			}
			// Added for Multiple voucher download by amit
			else if ((PretupsI.SERVICE_TYPE_MVD).equalsIgnoreCase(c2sRechargeModel1.getGifterServiceCode())) {
				urlToSend = "http://" + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator
								+ c2sRechargeModel1.getDenomination() + saperator + c2sRechargeModel1.getNoOfVouchers()
								+ saperator + c2sRechargeModel1.getPin());
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB;
			} else if ((PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR)
					.equalsIgnoreCase((c2sRechargeModel1.getServiceType()).split(":")[0])
					|| (PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN)
							.equalsIgnoreCase((c2sRechargeModel1.getServiceType()).split(":")[0])) {
				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + c2sRechargeModel1.getNotificationMsisdn() + saperator + subServiceValue
								+ saperator + senderlanguageCode + saperator + receiverLanguage + saperator + pin);
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();
			} else if ((PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE)
					.equalsIgnoreCase((c2sRechargeModel1.getServiceType()).split(":")[0])
					&& (!BTSLUtil.isNullString(c2sRechargeModel1.getNotificationMsisdn()))) {

				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + c2sRechargeModel1.getNotificationMsisdn() + saperator + receiverLanguage
								+ saperator + pin);
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();

			} else if ((PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE)
					.equalsIgnoreCase((c2sRechargeModel1.getServiceType()).split(":")[0])
					&& (BTSLUtil.isNullString(c2sRechargeModel1.getNotificationMsisdn()))) {

				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + pin);
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();

			} else if ((PretupsI.IAT_SERVICE_TYPE_ROAM_RECHARGE)
					.equalsIgnoreCase((c2sRechargeModel1.getServiceType()).split(":")[0])) {

				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + pin);// done
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();

			}

			else if ((PretupsI.MULTI_CURRENCY_SERVICE_TYPE)
					.equalsIgnoreCase((c2sRechargeModel1.getServiceType()).split(":")[0])) {

				receiverLanguage = localeMasterDAO.loadLocaleMasterCode(con, defaultLanguage, defaultCountry);
				String language1 = receiverLanguage;
				String language2 = receiverLanguage;
				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + c2sRechargeModel1.getSubServiceType().split(":")[1] + saperator
								+ language1 + saperator + language2 + saperator + pin + saperator + currency);// done
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();

			}
			/*
			 * else if((PretupsI.COLLECTION_ENQUIRY).equalsIgnoreCase((
			 * c2sRechargeModel .getServiceType()).split(":")[0])){
			 * c2sRechargeModel.setAmount("0");
			 * urlToSend=httpURLPrefix+instanceLoadVO.getHostAddress()+":"+
			 * instanceLoadVO.getHostPort()+Constants.getProperty(
			 * "CHANNEL_WEB_RECHARGE_SERVLET")+"?MSISDN="; urlToSend
			 * =urlToSend+c2sRechargeModel.getLoginUserMsisdn()+"&MESSAGE="
			 * +URLEncoder
			 * .encode((c2sRechargeModel.getServiceType()).split(":")[0]+
			 * saperator +c2sRechargeModel.getSubscriberMsisdn()+saperator
			 * +subServiceValue+saperator +senderlanguageCode+saperator
			 * +receiverLanguage +saperator +pin);//done by ashishT for passing
			 * hashvalue in url. urlToSend =urlToSend
			 * +"&REQUEST_GATEWAY_CODE="+messageGatewayVO
			 * .getGatewayCode()+"&REQUEST_GATEWAY_TYPE="
			 * +messageGatewayVO.getGatewayType(); urlToSend =urlToSend
			 * +"&SERVICE_PORT="+messageGatewayVO.getRequestGatewayVO
			 * ().getServicePort
			 * ()+"&LOGIN="+messageGatewayVO.getRequestGatewayVO ().getLoginID()
			 * ; urlToSend =urlToSend +"&PASSWORD="+msgGWPass
			 * +"&SOURCE_TYPE="+PretupsI.GATEWAY_TYPE_WEB
			 * +"&ACTIVE_USER_ID="+channelUserVO.getActiveUserID() ; }
			 */
			else if (!BTSLUtil.isNullString(c2sRechargeModel1.getInvoiceno())) {

				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + subServiceValue + saperator + senderlanguageCode + saperator
								+ receiverLanguage + saperator + pin + saperator + c2sRechargeModel1.getInvoiceno()); // done
																														// by
																														// ashishT
																														// for
																														// passing
																														// hashvalue
																														// in
																														// url.
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();
			} else if (!BTSLUtil.isNullString(c2sRechargeModel1.getBillPaymentTxnID())) {

				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + subServiceValue + saperator + senderlanguageCode + saperator
								+ receiverLanguage + saperator + pin + saperator
								+ c2sRechargeModel1.getBillPaymentTxnID());
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();
			}

			else if ((PretupsI.SERVICE_TYPE_VAS_RECHARGE)
					.equalsIgnoreCase((c2sRechargeModel1.getServiceType()).split(":")[0])
					&& (multiAmountEnabled && "0".equals(c2sRechargeModel1.getAmount()))) {

				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + PretupsI.VAS_BLANK_SLCTR_AMNT
								+ saperator + subServiceValue + saperator + receiverLanguage + saperator + pin);// done
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();
			}

			else {

				urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort()
						+ Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
				urlToSend = urlToSend + c2sRechargeModel1.getLoginUserMsisdn() + "&MESSAGE="
						+ URLEncoder.encode((c2sRechargeModel1.getServiceType()).split(":")[0] + saperator
								+ c2sRechargeModel1.getSubscriberMsisdn() + saperator + c2sRechargeModel1.getAmount()
								+ saperator + subServiceValue + saperator + senderlanguageCode + saperator
								+ receiverLanguage + saperator + pin);// done
				// done by ashishT for passing hashvalue in url.
				urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode()
						+ "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
				urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort()
						+ "&LOGIN=" + messageGatewayVO.getRequestGatewayVO().getLoginID();
				urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB
						+ "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();
			}

			loggerMessage.append("[SUBSERVICE : " + subServiceValue + "]");
			loggerMessage.append("[SUBSCRIBER N/W : " + networkCode + "]");
			loggerMessage.append("[URL : " + urlToSend + "]");
			loggerMessage.append("[SENDER ID : " + channelUserVO.getUserID() + "]");
			loggerMessage.append("[REQUEST DATE : " + new Date() + "]");
			URL url = null;
			url = new URL(urlToSend);

			try {
				if (httpsEnabled) {
					_con = BTSLUtil.getConnection(url);
				} else {
					_con = (HttpURLConnection) url.openConnection();
				}
				_con.setDoInput(true);
				_con.setDoOutput(true);
				_con.setRequestMethod("GET");
				in = new BufferedReader(new InputStreamReader(_con.getInputStream()));
			} catch (Exception e) {
				_log.error(methodName, "Exception " + e.getMessage());
				_log.errorTrace(methodName, e);
				loggerMessage.append("[RESULT : EXCEPTION]");
				loggerMessage.append("[EXCEPTION : " + e.getMessage() + "]");
				loggerMessage.append("[RESPONSE : FAIL]");
				final String arr[] = new String[2];
				arr[0] = instanceLoadVO.getHostAddress();
				arr[1] = instanceLoadVO.getHostPort();
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "C2SRechargeAction[recharge]", "", "", "", "Exception:" + e.getMessage());
				throw new BTSLBaseException(this, methodName, "c2stranfer.c2srecharge.error.connectionfailed", 0, arr,
						"confirmC2SRechargePage");
			}
			String responseStr = null;

			while ((responseStr = in.readLine()) != null) {
				finalResponse = finalResponse + responseStr;
			}

			String txn_status = finalResponse(finalResponse, c2sRechargeModel1, channelUserVO, request, con);

			c2sRechargeModel1.setTxnStatus(txn_status);
			loggerMessage.append("[RESULT : COMPLETE]");
			loggerMessage.append("[RESPONSE : " + finalResponse + "]");

		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			loggerMessage.append("[RESULT : EXCEPTION]");
			loggerMessage.append("[EXCEPTION : " + e.getMessage() + "]");
			loggerMessage.append("[RESPONSE : FAIL]");
		} finally {

			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				_log.error(methodName, e);
			}
			if (mcomCon != null) {
				mcomCon.close("RechargeServiceImpl#recharge");
				mcomCon = null;
			}
			try {
				if (_con != null) {
					_con.disconnect();
				}
			} catch (Exception e) {
				_log.error(methodName, e);
			}

			WebRechargeLogger.log(loggerMessage.toString());

		}
		return c2sRechargeModel1;
	}

	/**
	 * Method returns the final response message
	 * 
	 * @param finalResponse
	 * @param c2sRechargeModel
	 * @param channelUserVO
	 * @param request
	 * @param con
	 * @return
	 * @throws BTSLBaseException
	 */
	public String finalResponse(String finalResponse, C2SRechargeModel c2sRechargeModel, ChannelUserVO channelUserVO,
			HttpServletRequest request, Connection con) throws BTSLBaseException {

		final String methodName = "finalResponse";
		HashMap _map = null;
		HashMap map = null;
		String txn_id = null;
		EnquiryVO _enquiryVO = null;
		ArrayList _enquiryList = null;
		String txn_status = null;
		try {
			_map = BTSLUtil.getStringToHash(finalResponse, "&", "=");
			finalResponse = URLDecoder.decode((String) _map.get("MESSAGE"), "UTF16");
			map = BTSLUtil.getStringToHash(finalResponse, "&", "=");

			txn_id = (String) _map.get("TXN_ID");
			txn_status = (String) _map.get("TXN_STATUS");
			if (BTSLUtil.isNullString(txn_status)) {
				txn_status = (String) _map.get("txnstatus");
			}
			c2sRechargeModel.setCurrentBalance(new UserBalancesDAO().loadUserBalanceForProduct(con, "",
					channelUserVO.getUserID(), channelUserVO.getNetworkCode(), channelUserVO.getNetworkCode(),
					channelUserVO.getProductCode()));
			c2sRechargeModel.setSaleBatchNo((String) _map.get("SALE_BATCH_NO"));
			c2sRechargeModel.setTxnID(txn_id);
			if (PretupsI.COLLECTION_ENQUIRY.equalsIgnoreCase(c2sRechargeModel.getServiceType().split(":")[0])
					&& txn_status.equals(PretupsI.TXN_STATUS_SUCCESS)) {
				_enquiryList = new ArrayList();
				c2sRechargeModel.setInvoiceSize(Integer.parseInt((String) map.get("INVOICE_SIZE")));
				c2sRechargeModel.setxMontoDeudaTotal((String) map.get("TOTAL_PENDING_BALANCE"));
				c2sRechargeModel.setxOpcionRecaudacion((String) map.get("SERVICE_NAME"));
				c2sRechargeModel.setxCodTipoServicio((String) map.get("SERVICE_CODE"));
				finalResponse = "Success";
				int c2sRechargeModelsInvoiceSize=c2sRechargeModel.getInvoiceSize();
				for (int i = 0; i < c2sRechargeModelsInvoiceSize; i++) {
					_enquiryVO = new EnquiryVO();
					_enquiryVO.setxDescripServ((String) map.get("SERVICE_NAME_" + i));
					_enquiryVO.setxTipoServicio((String) map.get("SERVICE_CODE_" + i));
					_enquiryVO.setxNumeroDoc((String) map.get("INVOICE_NUM_" + i));
					_enquiryVO.setxMontoDebe((String) map.get("PERIOD_PENDING_BALANCE_" + i));
					_enquiryVO.setxMontoFact((String) map.get("MIN_PENDING_BALANCE_" + i));
					_enquiryVO.setxImportePagoMin((String) map.get("INVOICED_PENDING_BALANCE_" + i));
					_enquiryVO.setxFechaEmision(URLDecoder.decode((String) map.get("BILL_PERIOD_START_" + i)));
					_enquiryVO.setxFechaVenc(URLDecoder.decode((String) map.get("BILL_PERIOD_END_" + i)));
					_enquiryList.add(i, _enquiryVO);
				}
				c2sRechargeModel.setEnquiryItemList(_enquiryList);
			}
			if (!BTSLUtil.isNullString(finalResponse) && finalResponse.indexOf("mclass^") > -1
					&& finalResponse.indexOf(":") > -1) {
				finalResponse = finalResponse.substring(finalResponse.indexOf(":") + 1);
			}
			c2sRechargeModel.setFinalResponse(finalResponse);

		} catch (Exception e) {
			_log.error(methodName, e);
		}
		return txn_status;
	}

	/**
	 * Method added for validations (this is same as validate method of
	 * C2SRechargeform)
	 * 
	 * @param request
	 * @param c2sRechargeModel
	 * @param model
	 * @param bindingResult
	 * @return
	 * @throws ValidatorException
	 * @throws IOException
	 * @throws SAXException
	 */
	public boolean validate(HttpServletRequest request, C2SRechargeModel c2sRechargeModel, Model model,
			BindingResult bindingResult) throws ValidatorException, IOException, SAXException {

		final String METHOD_NAME = "validate";
		boolean flag = false;
		boolean validateflag = true;
		boolean invoiceFlag = false;
		boolean txnIdFlag = false;
		int i;
		String[] blockAmtSrvcTyp = null;
		String[] invoiceNoSrvcTyp = null;
		String[] txnIDSrvcTyp = null;
		String c2sTrnsfrAmtBlckSrvCtyp = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRNSFR_AMTBLCK_SRVCTYP);
		if (!BTSLUtil.isNullString(c2sTrnsfrAmtBlckSrvCtyp)) {
			blockAmtSrvcTyp = c2sTrnsfrAmtBlckSrvCtyp.split(",");
		}
		String c2sTrnsfrInvNoSrvCtyp = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRNSFR_INVNO_SRVCTYP);
		if (!BTSLUtil.isNullString(c2sTrnsfrInvNoSrvCtyp)) {
			invoiceNoSrvcTyp = c2sTrnsfrInvNoSrvCtyp.split(",");
		}
		String c2sReversalTxnIdSrvCtyp = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_REVERSAL_TXNID_SRVCTYP);
		if (!BTSLUtil.isNullString(c2sReversalTxnIdSrvCtyp)) {
			txnIDSrvcTyp = c2sReversalTxnIdSrvCtyp.split(",");
		}

		for (i = 0; i < blockAmtSrvcTyp.length; i++) {
			if ((c2sRechargeModel.getServiceType().split(":")[0]).equalsIgnoreCase(blockAmtSrvcTyp[i])) {
				flag = true;
				break;
			}
		}
		for (i = 0; i < invoiceNoSrvcTyp.length; i++) {
			if ((c2sRechargeModel.getServiceType().split(":")[0]).equalsIgnoreCase(invoiceNoSrvcTyp[i])) {
				invoiceFlag = true;
				break;
			}
		}
		for (i = 0; i < txnIDSrvcTyp.length; i++) {
			if ((c2sRechargeModel.getServiceType().split(":")[0]).equalsIgnoreCase(txnIDSrvcTyp[i])) {
				txnIdFlag = true;
				break;
			}
		}

		if (flag) {
			c2sRechargeModel.setAmount("");
		}
		if (!invoiceFlag) {
			c2sRechargeModel.setInvoiceno("");
		}
		/*
		 * if (!txnIdFlag) { errors = new ActionErrors(); }
		 */
		Map<String, String> map = validatorBasedOnService(c2sRechargeModel, model);
		if (!map.isEmpty() && map != null) {
			model.addAttribute(ERROR_MAP, map);
			return false;
		}

		/*
		 * Commented because while server side validation error messages were
		 * distorting the form plus number format exception was coming, so we
		 * decided to use map instead of binding result PretupsRestUtil prUtil =
		 * new PretupsRestUtil(); if(!prUtil.processFieldError(map,
		 * bindingResult)){ if(bindingResult.hasFieldErrors()){ return false; }
		 * }
		 */

		if (null != c2sRechargeModel.getServiceType()
				&& !PretupsI.SERVICE_TYPE_MVD.equals(c2sRechargeModel.getServiceType().split(":")[0])) {
			if (BTSLUtil.isNullString(c2sRechargeModel.getAmount())) {
				if (flag) {
					c2sRechargeModel.setAmount("");
				} else {
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.amount"));
					validateflag = false;
					return validateflag;
				}
			}
		}
		if (null != c2sRechargeModel.getServiceType()
				&& PretupsI.MULTI_CURRENCY_SERVICE_TYPE.equals(c2sRechargeModel.getServiceType().split(":")[0])
				&& c2sRechargeModel.getCurrencyList() != null && !c2sRechargeModel.getCurrencyList().isEmpty()) {
			_log.debug(METHOD_NAME, "c2sRechargeModel.getServiceType():" + c2sRechargeModel.getServiceType()
					+ ",currencyCode:" + c2sRechargeModel.getCountryCode());
			if (BTSLUtil.isNullString(c2sRechargeModel.getCurrencyCode())) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.currencyrequired"));
				validateflag = false;
				return validateflag;
			}
		}

		if (BTSLUtil.isNullString(c2sRechargeModel.getInvoiceno()) && null != c2sRechargeModel.getServiceType()) {
			if (!invoiceFlag) {
				c2sRechargeModel.setInvoiceno("");
			} else {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.invoiceno"));
				validateflag = false;
				return validateflag;
			}
		}
		if (BTSLUtil.isNullString(c2sRechargeModel.getBillPaymentTxnID())
				&& null != c2sRechargeModel.getServiceType()) {
			if (!txnIdFlag) {
				c2sRechargeModel.setBillPaymentTxnID("");
			} else {
				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.txnid"));
				validateflag = false;
				return validateflag;
			}
		}
		int minMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue();
		int maxMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue();
		if (null != c2sRechargeModel.getServiceType()
				&& PretupsI.GIFT_RECHARGE_CODE.equals(c2sRechargeModel.getServiceType().split(":")[0])) {

			if (!BTSLUtil.isNullString(c2sRechargeModel.getGifterMsisdn().trim())) {
				long lng = 0;
				try {

					final String p_gifterMsisdn = PretupsBL.getFilteredMSISDN(c2sRechargeModel.getGifterMsisdn());
					if ((p_gifterMsisdn.length() < minMsisdnLength || p_gifterMsisdn.length() > maxMsisdnLength)) {
						model.addAttribute(FAIL_KEY,
								PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.invalidgiftermsisdn"));
						validateflag = false;
						return validateflag;
					} else if (c2sRechargeModel.getGifterMsisdn().trim()
							.equalsIgnoreCase(c2sRechargeModel.getSubscriberMsisdn())) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("c2stranfer.c2srecharge.error.recivergiftermsisdnsame"));
						validateflag = false;
						return validateflag;
					} else if (c2sRechargeModel.getGifterMsisdn().trim()
							.equalsIgnoreCase(c2sRechargeModel.getLoginUserMsisdn())) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("c2stranfer.c2srecharge.error.sendergiftermsisdnsame"));
						validateflag = false;
						return validateflag;
					} else if (c2sRechargeModel.getSubscriberMsisdn().trim()
							.equalsIgnoreCase(c2sRechargeModel.getLoginUserMsisdn())) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("c2stranfer.c2srecharge.error.reciversendermsisdnsame"));
						validateflag = false;
						return validateflag;
					} else {
						lng = Long.parseLong(p_gifterMsisdn);
					}

				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.giftermsisdnnumeric"));
					validateflag = false;
					return validateflag;
				}
			} else {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.giftermsisdn"));
				validateflag = false;
				return validateflag;
			}

			if (BTSLUtil.isNullString(c2sRechargeModel.getGifterName().trim())) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.giftername"));
				validateflag = false;
				return validateflag;
			} else {
				boolean validate = false;
				int c2sRechargeModels=c2sRechargeModel.getGifterName().trim().length();
				for (int w = 0; w < c2sRechargeModels; w++) {
					if (Character.isSpaceChar(c2sRechargeModel.getGifterName().charAt(w))
							|| Character.isWhitespace(c2sRechargeModel.getGifterName().charAt(w))) {

						validate = true;
						break;
					}
				}
				if (validate) {
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.giternamewsvalidation"));
					validateflag = false;
					return validateflag;
				}

			}
		}

		if (null != c2sRechargeModel.getServiceType()
				&& PretupsI.SERVICE_TYPE_MVD.equals(c2sRechargeModel.getServiceType().split(":")[0])) {
			if (!BTSLUtil.isNullString(c2sRechargeModel.getNoOfVouchers().trim())) {
				long lng = 0;
				try {
					lng = Long.parseLong(c2sRechargeModel.getNoOfVouchers());
					if (lng <= 0) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("c2stranfer.c2srecharge.error.noofvouchersgreaterthanzero"));
						validateflag = false;
						return validateflag;
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.noofvouchersnumeric"));
					validateflag = false;
					return validateflag;
				}
			} else {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.noofvouchers"));
				validateflag = false;
				return validateflag;
			}
			if (BTSLUtil.isNullString(c2sRechargeModel.getDenomination())) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.denomination"));
				validateflag = false;
				return validateflag;
			}
		}
		if (null != c2sRechargeModel.getServiceType()
				&& !PretupsI.SERVICE_TYPE_MVD.equals(c2sRechargeModel.getServiceType().split(":")[0])) {
			if (!BTSLUtil.isNullString(c2sRechargeModel.getSubscriberMsisdn().trim())) {
				long lng = 0;
				try {
					{
						final OperatorUtilI _operatorUtil = (OperatorUtilI) Class.forName(
								(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS))
								.newInstance();
						Connection con = null;
						MComConnectionI mcomCon = null;
						try {
							mcomCon = new MComConnection();
							con = mcomCon.getConnection();
							PrivateRchrgVO prvo = null;
							c2sRechargeModel.setSubscriberSid(c2sRechargeModel.getSubscriberMsisdn());
							c2sRechargeModel.setSubscriberTmpMsisdn(c2sRechargeModel.getSubscriberMsisdn());
							if ((prvo = _operatorUtil.getPrivateRechargeDetails(con,
									c2sRechargeModel.getSubscriberMsisdn())) != null) {
								c2sRechargeModel.setSubscriberTmpMsisdn(prvo.getMsisdn());
							}

						} finally {
							if (mcomCon != null) {
								mcomCon.close("RechargeServiceImpl#validate");
								mcomCon = null;
							}
							con = null;
						}
					}
					final String p_subscriberMsisdn = PretupsBL
							.getFilteredMSISDN(c2sRechargeModel.getSubscriberTmpMsisdn());
					if ((p_subscriberMsisdn.length() < minMsisdnLength || p_subscriberMsisdn.length() > maxMsisdnLength)) {
						model.addAttribute(FAIL_KEY,
								PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.invalidmsisdn"));
						validateflag = false;
						return validateflag;
					} else {
						lng = Long.parseLong(p_subscriberMsisdn);
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					model.addAttribute(FAIL_KEY,
							PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.msisdnnumeric"));
					validateflag = false;
					return validateflag;
				}
			} else {
				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.msisdn"));
				validateflag = false;
				return validateflag;
			}
		}

		if (c2sRechargeModel.getServiceType() != null && (PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR
				.equalsIgnoreCase(c2sRechargeModel.getServiceType().split(":")[0])
				|| PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN
						.equalsIgnoreCase(c2sRechargeModel.getServiceType().split(":")[0]))) {
			if (!BTSLUtil.isNullString(c2sRechargeModel.getNotificationMsisdn().trim())) {
				long lng = 0;
				try {
					final String p_notificationMsisdn = PretupsBL
							.getFilteredMSISDN(c2sRechargeModel.getNotificationMsisdn());
					if ((p_notificationMsisdn.length() < minMsisdnLength || p_notificationMsisdn.length() > maxMsisdnLength)) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("c2stranfer.c2srecharge.error.invalidnotificationmsisdn"));
						validateflag = false;
						return validateflag;
					} else {
						lng = Long.parseLong(p_notificationMsisdn);
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("c2stranfer.c2srecharge.error.notificationmsisdnnotnumeric"));
					validateflag = false;
					return validateflag;
				}
			} else {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.notificationisrequired"));
				validateflag = false;
				return validateflag;
			}
		}
		if (c2sRechargeModel.getServiceType() != null && (PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE
				.equalsIgnoreCase(c2sRechargeModel.getServiceType().split(":")[0]))) {
			if (!BTSLUtil.isNullString(c2sRechargeModel.getNotificationMsisdn().trim())) {
				long lng = 0;
				try {
					final String p_notificationMsisdn = PretupsBL
							.getFilteredMSISDN(c2sRechargeModel.getNotificationMsisdn());
					if ((p_notificationMsisdn.length() < minMsisdnLength || p_notificationMsisdn.length() > maxMsisdnLength)) {
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("c2stranfer.c2srecharge.error.invalidnotificationmsisdn"));
						validateflag = false;
						return validateflag;
					} else {
						lng = Long.parseLong(p_notificationMsisdn);
					}
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("c2stranfer.c2srecharge.error.notificationmsisdnnotnumeric"));
					validateflag = false;
					return validateflag;
				}
			}
		}
		if (PretupsI.MULTIPLE_ENTRY_ALLOWED.equals(c2sRechargeModel.getMultipleEntry())) {
			if (BTSLUtil.isNullString(c2sRechargeModel.getConfirmSubscriberMSISDN())) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.confirmsubscribermsisdnreq"));
				validateflag = false;
				return validateflag;
			} else if (!(c2sRechargeModel.getSubscriberMsisdn().trim()
					.equals(c2sRechargeModel.getConfirmSubscriberMSISDN().trim()))) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.confirmsubscribermsisdn"));
				validateflag = false;
				return validateflag;
			}
			if (BTSLUtil.isNullString(c2sRechargeModel.getConfirmAmount())) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.confirmamountreq"));
				validateflag = false;
				return validateflag;
			} else if (!(c2sRechargeModel.getAmount().trim().equals(c2sRechargeModel.getConfirmAmount().trim()))) {
				model.addAttribute(FAIL_KEY,
						PretupsRestUtil.getMessageString("c2stranfer.c2srecharge.error.confirmamount"));
				validateflag = false;
				return validateflag;
			}
		}
		return validateflag;

	}

	/**
	 * Method is used for creating map of errors based on services
	 * 
	 * @param c2sRechargeModel
	 * @param model
	 * @return
	 * @throws ValidatorException
	 * @throws IOException
	 * @throws SAXException
	 */
	public Map<String, String> validatorBasedOnService(C2SRechargeModel c2sRechargeModel, Model model)
			throws ValidatorException, IOException, SAXException {
		Map<String, String> map = new HashMap<String, String>();
		CommonValidator commonValidator;
		switch (c2sRechargeModel.getServiceType().split(":")[0]) {
		case "RC":
			commonValidator = new CommonValidator("/configfiles/transfer/validator-transfer.xml", c2sRechargeModel,
					"RCForm");
			map = commonValidator.validateModel();
			break;

		case "GRC":
			commonValidator = new CommonValidator("/configfiles/transfer/validator-transfer.xml", c2sRechargeModel,
					"GRCForm");
			map = commonValidator.validateModel();
			break;

		case "PPB":
			commonValidator = new CommonValidator("/configfiles/transfer/validator-transfer.xml", c2sRechargeModel,
					"PPBForm");
			map = commonValidator.validateModel();
			break;

		case "EVD":
			commonValidator = new CommonValidator("/configfiles/transfer/validator-transfer.xml", c2sRechargeModel,
					"EVDForm");
			map = commonValidator.validateModel();
			break;

		case "MVD":
			commonValidator = new CommonValidator("/configfiles/transfer/validator-transfer.xml", c2sRechargeModel,
					"MVDForm");
			map = commonValidator.validateModel();
			break;

		case "INTRRC":
			commonValidator = new CommonValidator("/configfiles/transfer/validator-transfer.xml", c2sRechargeModel,
					"InternetRCForm");
			map = commonValidator.validateModel();
			break;

		case "PSTNRC":
			commonValidator = new CommonValidator("/configfiles/transfer/validator-transfer.xml", c2sRechargeModel,
					"FixLineRCForm");
			map = commonValidator.validateModel();
			break;

		default:
			map.clear();
		}

		return map;

	}

	@Override
	public String notify(HttpServletRequest request, C2SRechargeModel theForm, ChannelUserVO channelUserVO)
			throws BTSLBaseException {
		String printMessage = null;
		VOMSVoucherVO voucherVO1 = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final String txnID = theForm.getTxnID();
			final C2STransferDAO c2STransferDAO = new C2STransferDAO();
			final C2STransferVO transferVO = c2STransferDAO.loadC2STransferDetails(con, txnID);
			transferVO.setReceiverMsisdn(theForm.getSubscriberMsisdn());
			theForm.setTransferVO(transferVO);
			balance(channelUserVO, theForm);
			printMessage = request.getParameter("printMessage");

			if ((PretupsI.SERVICE_TYPE_MVD).equalsIgnoreCase(theForm.getGifterServiceCode())) {
				theForm.setDecryptionKey(
						KeyGeneration.getGeneratedKey(Integer.parseInt(Constants.getProperty("KEY_LENGTH"))));
				theForm.setVoucherSerialAndPinList(
						VOMSVoucherDAO.loadPINAndSerialNumberForMVDFileDownload(con, theForm.getSaleBatchNo()));
				for (int i = 0, j = theForm.getVoucherSerialAndPinList().size(); i < j; i++) {
					voucherVO1 = (VOMSVoucherVO) (theForm.getVoucherSerialAndPinList()).get(i);
					if (i == 0) {
						theForm.setFirstSerialNo(voucherVO1.getSerialNo());
					} else if (i == j - 1) {
						theForm.setLastSerialNo(voucherVO1.getSerialNo());
					}
				}

				  /*
				 * theForm.setNoOfVouchers("1"); theForm.setDenomination("20");
				  */

				WebRechargeLogger.log("[SERVICE TYPE : Bulk Voucher Distribution]" + "[VOUCHER BATCH NO :"
						+ theForm.getSaleBatchNo() + "] [DECRYPTION KEY :" + theForm.getDecryptionKey() + "]");
				return "c2stransfer/notificationMessageForMVDView";
			} else if (!BTSLUtil.isNullString(printMessage) && printMessage.equals("true")) {
				if (theForm.getServiceType() != null
						&& PretupsI.SERVICE_TYPE_EVD.equals(theForm.getServiceType().split(":")[0])) {
					final VomsVoucherDAO voucherDAO = new VomsVoucherDAO();
					final VomsVoucherVO voucherVO = voucherDAO.loadVomsVoucherVO(con, transferVO);
					theForm.setPrintVoucherPin(VomsUtil.decryptText(voucherVO.getPinNo()));
					theForm.setSerialNo(voucherVO.getSerialNo());
					return "c2stransfer/printNotificationMessageEVDView";
				} else if (PretupsI.COLLECTION_ENQUIRY.equalsIgnoreCase(theForm.getServiceType().split(":")[0])) {
					return "c2stransfer/printNotificationMessageCEView";
				} else {
					return "c2stransfer/printNotificationMessageView";
				}

			} else if (PretupsI.COLLECTION_ENQUIRY.equalsIgnoreCase(theForm.getServiceType().split(":")[0])) {
				return "c2stransfer/CollectionEnquiryView";
			} else {
				 return "c2stransfer/notificationMessageView";
			}
		} catch (Exception e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("RechargeServiceImpl#notify");
				mcomCon = null;
			}
		}

	}
}
