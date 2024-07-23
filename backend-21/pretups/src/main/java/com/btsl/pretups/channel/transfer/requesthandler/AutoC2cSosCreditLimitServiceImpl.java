package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

@Service
public class AutoC2cSosCreditLimitServiceImpl implements AutoC2cSosCreditLimitServiceI {
	public static final String classname = "AutoC2cSosCreditLimitServiceImpl";

	protected final Log log = LogFactory.getLog(getClass().getName());

	@Override
	public void loadAutoC2cSosCreditLimitDetails(MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, AutoC2CSOSViewResponseVO responseVO) {

		final String methodName = "loadAutoC2cSosCreditLimitDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		ChannelUserWebDAO channelUserWebDAO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserTransferVO uservo = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {

			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

//			responseVO.setLoginUserCatCode(oAuthUserData.get);

			channelUserWebDAO = new ChannelUserWebDAO();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			if (!BTSLUtil.isNullString(responseVO.getMsisdn())) {
				channelUserVO = channelUserDAO.loadChannelUserDetails(con, responseVO.getMsisdn());
				if (channelUserVO == null) {
					if (log.isDebugEnabled()) {
						log.debug(methodName, "User Category is not allowed Auto C2C" + responseVO.getMsisdn());
					}
					throw new BTSLBaseException(this, methodName,
							"autoc2c.associatesubscriberdetails.msg.selectCategory", "singleAssociationMode");
				}
			} else if (!BTSLUtil.isNullString(responseVO.getLoginUserID())) {
				channelUserVO = channelUserDAO.loadChnlUserDetailsByLoginID(con, responseVO.getLoginUserID());
				if (channelUserVO == null) {
					if (log.isDebugEnabled()) {
						log.debug(methodName, "User Category is not allowed Auto C2C" + responseVO.getLoginUserID());
					}
					throw new BTSLBaseException(this, methodName,
							"autoc2c.associatesubscriberdetails.msg.selectCategory", "singleAssociationMode");
				}
			} else {

				throw new BTSLBaseException(this, methodName, "autoc2c.associatesubscriberdetails.msg.selectUser",
						"singleAssociationMode");

			}

			responseVO.setGeoDomainName(channelUserVO.getGeographicalDesc());
			final boolean exist = channelUserWebDAO.verifyCategory(con, channelUserVO.getCategoryCode());
			if (!exist) {// responseVO.flushradio();
				if (log.isDebugEnabled()) {
					log.debug(methodName, "User Category is not allowed Auto C2C" + channelUserVO.getCategoryCode());
				}
				throw new BTSLBaseException(this, methodName, "autoC2C.associatesubscriberdetails.categorynotallowed",
						"singleAssociationMode");
			}
			setValuesPreferenceBased(responseVO, channelUserVO);
			responseVO.setDomainCode(channelUserVO.getDomainID());
			responseVO.setDomainName(channelUserVO.getDomainName());
			responseVO.setCategoryCode(channelUserVO.getCategoryCode());
			responseVO.setCategoryName(channelUserVO.getCategoryVO().getCategoryName());
			responseVO.setUserName(channelUserVO.getUserName());
			responseVO.setUserID(channelUserVO.getUserID());
			responseVO.setMsisdn(channelUserVO.getMsisdn());
			responseVO.setAutoo2callowed(channelUserVO.getAutoo2callowed());
			responseVO.setAutoO2CThresholdLimit(PretupsBL.getDisplayAmount(channelUserVO.getAutoO2CThresholdLimit()));
			responseVO.setAutoO2CTxnAmunt(PretupsBL.getDisplayAmount(channelUserVO.getAutoO2CTxnValue()));

			responseVO.setStatus(HttpStatus.SC_OK);
			responseVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			responseVO.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {

			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			responseVO.setMessageCode(be.getMessage());
			responseVO.setMessage(resmsg);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseVO.setMessageCode(e.toString());
			responseVO.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("FetchChannelUserDetailsController#" + "fetchChannelUserDetails");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			if (log.isDebugEnabled()) {
				log.debug(methodName, responseVO);
				log.debug(methodName, "Exiting ");
			}
		}

	}

	private void setValuesPreferenceBased(final AutoC2CSOSViewResponseVO responseVO, ChannelUserVO channelUserVO) {
		boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		boolean channelAutoC2cEnabled = ((Boolean) (PreferenceCache
				.getSystemPreferenceValue(PreferenceI.CHANNEL_AUTOC2C_ENABLE))).booleanValue();
		if (channelAutoC2cEnabled) {
			responseVO.setAutoc2callowed(channelUserVO.getAutoc2callowed());
			responseVO.setMaxTxnAmount(PretupsBL.getDisplayAmount((long)(Double.parseDouble(channelUserVO.getAutoc2cquantity()))));
		}
		if (channelSosEnable) {
			responseVO.setSosAllowed(channelUserVO.getSosAllowed());
			responseVO.setSosAllowedAmount(PretupsBL.getDisplayAmount(channelUserVO.getSosAllowedAmount()));
			responseVO.setSosThresholdLimit(PretupsBL.getDisplayAmount(channelUserVO.getSosThresholdLimit()));
		}
		if (lrEnabled) {
			responseVO.setLrAllowed(channelUserVO.getLrAllowed());
			responseVO.setLrMaxAmount(PretupsBL.getDisplayAmount(channelUserVO.getLrMaxAmount()));
		}
	}

	@Override
	public AutoC2CSOSUpdateResponseVO processIndividualRecord(MultiValueMap<String, String> headers, HttpServletResponse responseSwag,
			AutoC2CSOSRequestVO requestVO) {
		final String methodName = "processIndividualRecord";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		ChannelUserWebDAO channelUserWebDAO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		String senderMessage;
		PushMessage push = null;
		String arr[] = new String[] { "0", "0" };
		int unprocessedMsisdn = 0;
		int unprocessedMsisdn1 = 0;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		AutoC2CSOSUpdateResponseVO responseVO = new AutoC2CSOSUpdateResponseVO();
		ArrayList<String> successList = null;

		try {

			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			
			successList = new ArrayList<>();

			ChannelUserVO chnlUserVO = null;
			final Date date = new Date();
			// set the values in VO for association
			chnlUserVO = new ChannelUserVO();
			UserTransferCountsVO UserTransferCountsVO = new UserTransferCountsVO();
			UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();

			// Change ID=ACCOUNTID
			// FilteredMSISDN is replaced by getFilteredIdentificationNumber
			// This is done because this field can contains msisdn or
			// account id
			chnlUserVO.setMsisdn(requestVO.getMsisdn());
			chnlUserVO.setChannelUserID(requestVO.getUserID());
			boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
			boolean channelSosEnable = (boolean) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
			boolean channelAutoC2cEnabled = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHANNEL_AUTOC2C_ENABLE))).booleanValue();
			if (channelAutoC2cEnabled) {
				chnlUserVO.setMaxTxnAmount(PretupsBL.getSystemAmount(requestVO.getMaxTxnAmount()));
				chnlUserVO.setAutoc2callowed(requestVO.getAutoc2callowed());
			}
			if (channelSosEnable) {
				chnlUserVO.setSosAllowedAmount(PretupsBL.getSystemAmount(requestVO.getSosAllowedAmount()));
				chnlUserVO.setSosAllowed(requestVO.getSosAllowed());
				chnlUserVO.setSosThresholdLimit(PretupsBL.getSystemAmount(requestVO.getSosThresholdLimit()));
			}
			if (lrEnabled) {
				chnlUserVO.setLrAllowed(requestVO.getLrAllowed());
				chnlUserVO.setLrMaxAmount(PretupsBL.getSystemAmount(requestVO.getLrMaxAmount()));
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserTransferCountsVO = userTransferCountsDAO.selectLastSOSTxnID(chnlUserVO.getChannelUserID(), con, false,
					null);

			channelUserWebDAO = new ChannelUserWebDAO();

			// check if record is still approved or not	

			// associate the number
			if (channelAutoC2cEnabled && chnlUserVO.getAutoc2callowed().equals(PretupsI.NO)) {
				chnlUserVO.setMaxTxnAmount(0);
			}
			
			if (channelAutoC2cEnabled && requestVO.getAutoc2callowed().equals(PretupsI.YES)) {
				if (Integer.valueOf(requestVO.getMaxTxnAmount()) <= 0) {
					throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.C2C_ALLOWED, 0, null);
				}
			}
			
			if (channelSosEnable && chnlUserVO.getSosAllowed().equals(PretupsI.NO)) {
				chnlUserVO.setSosAllowedAmount(0);
				chnlUserVO.setSosThresholdLimit(0);
			}
			if (channelSosEnable && chnlUserVO.getSosAllowed().equals(PretupsI.YES)) {
				if (Integer.valueOf(requestVO.getSosAllowedAmount()) <= 0) {
					throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SOS_ALLOWED, 0, null);
				}
				if (Integer.valueOf(requestVO.getSosThresholdLimit()) <= 0) {
					throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SOS_THRESHOLD, 0, null);
				}

			}
			if (channelSosEnable) {
				chnlUserVO.setSosAllowedAmount(PretupsBL.getSystemAmount(requestVO.getSosAllowedAmount()));
				chnlUserVO.setSosAllowed(requestVO.getSosAllowed());
				chnlUserVO.setSosThresholdLimit(PretupsBL.getSystemAmount(requestVO.getSosThresholdLimit()));
			}
			if (lrEnabled && chnlUserVO.getLrAllowed().equals(PretupsI.YES)) {
				if (Integer.valueOf(requestVO.getLrMaxAmount()) <= 0) {
					throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.LAST_RECHARGE, 0, null);
				}
			}
			if (lrEnabled && chnlUserVO.getLrAllowed().equals(PretupsI.NO)) {
				chnlUserVO.setLrMaxAmount(0);
			}
			if (channelSosEnable && channelAutoC2cEnabled) {
				if (chnlUserVO.getSosAllowed().equals(PretupsI.YES)) {
					arr[0] = PretupsBL.getDisplayAmount(chnlUserVO.getSosThresholdLimit());
					arr[1] = PretupsBL.getDisplayAmount(chnlUserVO.getSosAllowedAmount());
					senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.SOS_ENABLE_SUCCESS, arr);
					push = new PushMessage(chnlUserVO.getMsisdn(), senderMessage, "", "", locale);
				} else if (chnlUserVO.getSosAllowed().equals(PretupsI.NO)) {

					if (UserTransferCountsVO != null) {
						unprocessedMsisdn1 = channelUserWebDAO.autoc2cupdate(con, chnlUserVO);

						mcomCon.partialCommit();
						throw new BTSLBaseException(this, methodName, "sos.pending.disable.failed", "firstPage");
					}
					senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.SOS_DISABLE_SUCCESS, null);
					push = new PushMessage(chnlUserVO.getMsisdn(), senderMessage, "", "", locale);
				}

				if (UserTransferCountsVO != null) {
					unprocessedMsisdn1 = channelUserWebDAO.autoc2cupdate(con, chnlUserVO);
					successList.add(RestAPIStringParser.getMessage(locale, "max.txn.amount.updated.only.as.users.sos.is.pending", null));
				} else {
					unprocessedMsisdn1 = channelUserWebDAO.autoc2cupdate(con, chnlUserVO);
					unprocessedMsisdn = channelUserWebDAO.sosUpdate(con, chnlUserVO);
					successList.add(RestAPIStringParser.getMessage(locale, "channeluser.sos.auto.c2c.transfer.success", null));
					
				}

				mcomCon.finalCommit();
				push.push();

			} else if (channelSosEnable && !channelAutoC2cEnabled) {
				if (chnlUserVO.getSosAllowed().equals(PretupsI.YES)) {
					if (UserTransferCountsVO != null) {
						throw new BTSLBaseException(this, methodName, "sos.pending.disable.failed", "firstPage");
					}
					arr[0] = PretupsBL.getDisplayAmount(chnlUserVO.getSosThresholdLimit());
					arr[1] = PretupsBL.getDisplayAmount(chnlUserVO.getSosAllowedAmount());
					senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.SOS_ENABLE_SUCCESS, arr);
					push = new PushMessage(chnlUserVO.getMsisdn(), senderMessage, "", "", locale);
				} else {
					if (UserTransferCountsVO != null) {
						throw new BTSLBaseException(this, methodName, "sos.pending.disable.failed", "firstPage");
					}
					senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.SOS_DISABLE_SUCCESS, null);
					push = new PushMessage(chnlUserVO.getMsisdn(), senderMessage, "", "", locale);
				}
				unprocessedMsisdn = channelUserWebDAO.sosUpdate(con, chnlUserVO);

				mcomCon.finalCommit();
				push.push();
				successList.add(RestAPIStringParser.getMessage(locale, "channeluser.sos.transfer.successs", null));
			} else if (!channelSosEnable && channelAutoC2cEnabled) {

				unprocessedMsisdn1 = channelUserWebDAO.autoc2cupdate(con, chnlUserVO);

				mcomCon.finalCommit();
				successList.add(RestAPIStringParser.getMessage(locale, "autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess", null));
			}
			if (lrEnabled) {
				UserTransferCountsVO = userTransferCountsDAO.selectLastLRTxnID(chnlUserVO.getChannelUserID(), con,
						false, null);
				if (chnlUserVO.getLrAllowed().equals(PretupsI.YES)) {
					arr[0] = PretupsBL.getDisplayAmount(chnlUserVO.getLrMaxAmount());
					senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LAST_RECHARGE_ENABLE_SUCCESS, arr);
					push = new PushMessage(chnlUserVO.getMsisdn(), senderMessage, "", "", locale);
				} else if (chnlUserVO.getLrAllowed().equals(PretupsI.NO)) {
					if (UserTransferCountsVO != null) {
						throw new BTSLBaseException(this, methodName, "last.recharge.pending.disable.failed",
								"firstPage");
					}
					senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LAST_RECHARGE_DISABLE_SUCCESS, null);
					push = new PushMessage(chnlUserVO.getMsisdn(), senderMessage, "", "", locale);
				}
				unprocessedMsisdn = channelUserWebDAO.lastRechargeUpdate(con, chnlUserVO);

				mcomCon.finalCommit();
				if (!requestVO.getLrAllowed().equals(requestVO.getCurrentLrAllowedValue())) {					
					push.push();
				}
			}

			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			responseVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
			responseVO.setMessage(resmsg);
			responseVO.setSuccessMessages(successList);
			responseVO.setStatus(HttpStatus.SC_OK);
			
			

		} catch (BTSLBaseException be) {

			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			responseVO.setMessageCode(be.getMessage());
			responseVO.setMessage(resmsg);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseVO.setMessageCode(e.toString());
			responseVO.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close(this.getClass().getName() + "#" + methodName);
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			if (log.isDebugEnabled()) {
				log.debug(methodName, responseVO);
				log.debug(methodName, "Exiting ");
			}
		}
		return responseVO;
	}

}
