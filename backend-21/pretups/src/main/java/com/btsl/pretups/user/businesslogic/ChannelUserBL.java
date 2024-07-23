/**
 * @(#)ChannelUserBL.java
 *                        Copyright(c) 2005, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        <description>
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        avinash.kamthan Oct 25, 2005 Initital Creation
 *                        Sandeep Goel Nov 10, 2005 Modification,Customization
 *                        Sandeep Goel 05/08/2006 Modification ID USD001
 *                        Sandeep Goel 09/10/2006 Modification ID RECON001
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 * 
 */

package com.btsl.pretups.user.businesslogic;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.adjustments.businesslogic.AdjustmentsVO;
import com.btsl.pretups.adjustments.businesslogic.DiffCalBL;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.LastRechargeTransfer;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.loyaltymgmt.businesslogic.LMSProfileCache;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetDetailsLMSVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgDAO;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.loyaltymgmt.businesslogic.LoyaltyTxnDAO;
import com.btsl.user.businesslogic.UserLoanVO;
/**
 * 
 * @author sandeep.goel
 * @version $Revision: 1.0 $
 */
public class ChannelUserBL {

	/**
	 * Field _log.
	 */
	private static Log _log = LogFactory.getLog(ChannelUserBL.class.getName());
	/**
	 * Field _channelUserDAO.
	 */
	private static ChannelUserDAO _channelUserDAO = new ChannelUserDAO();
	/**
	 * Field _userBalancesDAO.
	 */
	private static UserBalancesDAO _userBalancesDAO = new UserBalancesDAO();

	private static long NEW_BAL=0;
	
	/**
	 * to ensure no class instantiation 
	 */
    private ChannelUserBL() {
        
    }
    
	/**
	 * Method to load the channel user details based on the msisdn passed. Also
	 * checks whether user is suspended or not
	 * 
	 * @param con
	 * @param p_requestVO
	 *            RequestVO
	 * @param p_msisdn
	 * @param p_gatewayType
	 *            String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public static ChannelUserVO validateChannelUserDetails(Connection con, RequestVO p_requestVO, String p_msisdn, String p_gatewayType) throws BTSLBaseException {
		final String METHOD_NAME = "validateChannelUserDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, " Entered msisdn:" + p_msisdn + " p_gatewayType= " + p_gatewayType);
		}
		ChannelUserVO channelUserVO = null;

		try {
			channelUserVO = _channelUserDAO.loadChannelUserDetails(con, p_msisdn);
			if (channelUserVO == null) {
				throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_NO_SUCH_USER);
			} else {
				p_requestVO.setLocale(new Locale(channelUserVO.getUserPhoneVO().getPhoneLanguage(), channelUserVO.getUserPhoneVO()
						.getCountry()));
				if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_ACTIVE)) {
					if (!channelUserVO.getCategoryVO().getAllowedGatewayTypes().contains(p_gatewayType)) {
						throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_CAT_GATETYPENOTALLOWED);
					} else if (channelUserVO.getGeographicalCodeStatus().equals(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND)) {
						throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_GEODOMAIN_SUSPEND);
					}
					return channelUserVO;
				} else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_SUSPEND);
				} else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BLOCK)) {
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
				} else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_NEW)) {
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_STATUS_NEW);
				} else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_APPROVED)) {
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_STATUS_APPROVED);
				} else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETE_REQUEST)) {
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_DELETE_REQUEST);
				} else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_SUSPEND_REQUEST);
				}
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[validateChannelUserDetails]", "",
					p_msisdn, "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, " Exiting for msisdn:" + p_msisdn + " channelUserVO=" + channelUserVO);
		}
		return channelUserVO;
	}

	/**
	 * Method to check whether the Msisdn last request was under process or not,
	 * also marks the request as under process and un marks also
	 * 
	 * @param con
	 * @param p_requestID
	 * @param p_userPhoneVO
	 * @param mark
	 * @throws BTSLBaseException
	 */
	public static void checkRequestUnderProcess(Connection con, String p_requestID, UserPhoneVO p_userPhoneVO, boolean mark) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("checkRequestUnderProcess", p_requestID, "Entered senderVO msisdn:" + p_userPhoneVO.getMsisdn());
		}
		int count = 0;
		final String METHOD_NAME = "checkRequestUnderProcess";
		try {
			// Lock USER_PHONES table
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			p_userPhoneVO.setCurrentModifiedOn(p_userPhoneVO.getModifiedOn());
			if (mark) {
				
				channelUserDAO.lockUserPhonesTable(con, p_userPhoneVO);
				// Condition Added of time so that after expiry of 5 minutes it
				// will be reset
				// (new
				// Date().getTime()-p_userPhoneVO.getModifiedOn().getTime())<=300000)
				// added for rejecting second request if two parallel requests
				// comes for the same user.
				// if (p_userPhoneVO.getLastTransactionStatus() != null &&
				// p_userPhoneVO.getLastTransactionStatus().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)
				// &&
				// ((p_userPhoneVO.getModifiedOn().getTime()-p_userPhoneVO.getLastTransactionOn().getTime()))<=300000)
				if (p_userPhoneVO.getLastTransactionStatus() != null && p_userPhoneVO.getLastTransactionStatus().equals(PretupsI.TXN_STATUS_UNDER_PROCESS) && ((p_userPhoneVO
						.getCurrentModifiedOn().getTime() - p_userPhoneVO.getLastTransactionOn().getTime()) <= 300000 || (new Date().getTime() - p_userPhoneVO
								.getModifiedOn().getTime()) <= 300000)) {
					throw new BTSLBaseException("ChannelUserBL", "checkRequestUnderProcess", PretupsErrorCodesI.CHNL_ERROR_SENDER_REQ_UNDERPROCESS);
				}
				p_userPhoneVO.setModifiedOn(p_userPhoneVO.getCurrentModifiedOn());
				p_userPhoneVO.setLastTransactionOn(p_userPhoneVO.getModifiedOn());
				count = _channelUserDAO.markRequestUnderProcess(con, p_requestID, p_userPhoneVO);
			} else {
				count = _channelUserDAO.unmarkRequestUnderProcess(con, p_requestID, p_userPhoneVO);
			}
			if (count <= 0) {
				throw new BTSLBaseException("ChannelUserBL", "checkRequestUnderProcess", PretupsErrorCodesI.CHNL_ERROR_SENDER_REQUNDERPROCESS_NOTUPDATED);
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error("checkRequestUnderProcess", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[checkRequestUnderProcess]", p_requestID,
					p_userPhoneVO.getMsisdn(), "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "checkRequestUnderProcess", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("checkRequestUnderProcess", "Exiting count:" + count);
		}
	}

	/**
	 * Method to validate the PIN that is sent by user and that stored in
	 * database
	 * 
	 * @param p_con
	 * @param p_channelUserVO
	 * @param p_requestPin
	 * @throws BTSLBaseException
	 */
	public static void validatePIN(Connection p_con, ChannelUserVO p_channelUserVO, String p_requestPin) throws BTSLBaseException {
		final String METHOD_NAME = "validatePIN";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered with p_channelUserVO:" + p_channelUserVO.toString() + " p_requestPin=" + BTSLUtil.maskParam(p_requestPin));
		}

		try {
			OperatorUtilI operatorUtili = null;
			try {
				final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
				operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[validatePIN]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}
			// Changing for passowrd security
			String p_requestPinde = "";
			try {
				//Decrypting pin and updating same variable
				p_requestPin = AESEncryptionUtil.aesDecryptor(p_requestPin, Constants.A_KEY);
				p_requestPinde = operatorUtili.decryptPINPassword(p_requestPin);
				if (BTSLUtil.isNullString(p_requestPinde)) {
					p_requestPinde = p_requestPin;
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				p_requestPinde = p_requestPin;
			}

			p_requestPin = p_requestPinde;
			if (!p_channelUserVO.isStaffUser()) {
				operatorUtili.validatePIN(p_con, p_channelUserVO, p_requestPin);
			} else {
				final UserPhoneVO userPhoneVO = (p_channelUserVO.getStaffUserDetails()).getUserPhoneVO();
				 if(userPhoneVO!=null && !PretupsI.NOT_AVAILABLE.equals(userPhoneVO.getSmsPin()))
                 {
                	 ChannelUserVO staffUserVo=p_channelUserVO.getStaffUserDetails();
                	 staffUserVo.setServiceTypes(p_channelUserVO.getServiceTypes());
                     operatorUtili.validatePIN(p_con, staffUserVo, p_requestPin);
                 } else {
					operatorUtili.validatePIN(p_con, p_channelUserVO, p_requestPin);
				}
			}
		} catch (BTSLBaseException bex) {
			throw bex;
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			/* EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[validatePIN]", "", "", "",
                            "Exception:" + e.getMessage());*/
			throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exiting ");
			/*
			 * if (_log.isDebugEnabled()) _log.debug("validatePIN",
			 * "Entered with p_userPhoneVO:" + p_channelUserVO.toString() +
			 * " p_requestPin=" + p_requestPin);
			 * int updateStatus = 0;
			 * boolean increaseInvalidPinCount = false;
			 * boolean isUserBarred=false;
			 * int mintInDay=24*60;
			 * try
			 * {
			 * UserPhoneVO userPhoneVO=new UserPhoneVO();
			 * userPhoneVO=p_channelUserVO.getUserPhoneVO();
			 * //Force the user to change PIN if he has not changed the same in
			 * the
			 * defined no of days
			 * if (_log.isDebugEnabled()) _log.debug("validatePIN",
			 * "Modified Time=:" + userPhoneVO.getModifiedOn() +
			 * " userPhoneVO.getPinModifiedOn()="
			 * +userPhoneVO.getPinModifiedOn()+"userPhoneVO.getCreatedOn()"
			 * +userPhoneVO.getCreatedOn());
			 * 
			 * //added for OCI changes regarding to change PIN on 1st request
			 * if(userPhoneVO.isForcePinCheckReqd() &&
			 * (userPhoneVO.getPinModifiedOn(
			 * ).getTime())==(userPhoneVO.getCreatedOn().getTime()))
			 * throw new BTSLBaseException("ChannelUserBL", "validatePIN",
			 * PretupsErrorCodesI.CHNL_FIRST_REQUEST_PIN_CHANGE);
			 * 
			 * int
			 * daysAfterChngPn=((Integer)PreferenceCache.getControlPreference(
			 * PreferenceI
			 * .C2S_DAYS_AFTER_CHANGE_PIN,p_channelUserVO.getNetworkID(),
			 * p_channelUserVO
			 * .getCategoryCode())).intValue();
			 * if(userPhoneVO.isForcePinCheckReqd() &&
			 * userPhoneVO.getPinModifiedOn()!=null &&
			 * ((userPhoneVO.getModifiedOn().
			 * getTime()-userPhoneVO.getPinModifiedOn().getTime())/(24*60*60*1000
			 * ))
			 * > daysAfterChngPn)
			 * {
			 * //Force the user to change PIN if he has not changed the same in
			 * the
			 * defined no of days
			 * if (_log.isDebugEnabled()) _log.debug("validatePIN",
			 * "Modified Time=:" + userPhoneVO.getModifiedOn() +
			 * " userPhoneVO.getPinModifiedOn()="
			 * +userPhoneVO.getPinModifiedOn()+" Difference="
			 * +((userPhoneVO.getModifiedOn
			 * ().getTime()-userPhoneVO.getPinModifiedOn(
			 * ).getTime())/(24*60*60*1000)));
			 * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
			 * EventStatusI
			 * .RAISED,EventLevelI.INFO,"ChannelUserBL[validatePIN]","",userPhoneVO
			 * .getMsisdn(),"","Force User to change PIN after "+daysAfterChngPn+
			 * " days as last changed on "+userPhoneVO.getPinModifiedOn());
			 * String strArr[]={String.valueOf(daysAfterChngPn)};
			 * throw new BTSLBaseException("ChannelUserBL", "validatePIN",
			 * PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGEPIN,0,strArr,null);
			 * }
			 * else
			 * {
			 * String decryptedPin =
			 * BTSLUtil.decryptText(userPhoneVO.getSmsPin());
			 * if (_log.isDebugEnabled())
			 * _log.debug("validatePIN", "Sender MSISDN:" +
			 * userPhoneVO.getMsisdn()
			 * + " decrypted PIN of database=" + decryptedPin +
			 * " p_requestPin =" +
			 * p_requestPin);
			 * 
			 * if (!decryptedPin.equals(p_requestPin))
			 * {
			 * increaseInvalidPinCount = true;
			 * if(userPhoneVO.getFirstInvalidPinTime()!=null)
			 * {
			 * //Check if PIN counters needs to be reset after the reset
			 * duration
			 * long
			 * pnBlckRstDuration=((Long)PreferenceCache.getControlPreference(
			 * PreferenceI
			 * .
			 * C2S_PIN_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),
			 * p_channelUserVO
			 * .getCategoryCode())).longValue();
			 * if (_log.isDebugEnabled()) _log.debug("validatePIN",
			 * "p_userPhoneVO.getModifiedOn().getTime()="
			 * +userPhoneVO.getModifiedOn()
			 * .getTime()+" p_userPhoneVO.getFirstInvalidPinTime().getTime()="
			 * +userPhoneVO
			 * .getFirstInvalidPinTime().getTime()+" Diff="+((userPhoneVO
			 * .getModifiedOn
			 * ().getTime()-userPhoneVO.getFirstInvalidPinTime().getTime
			 * ())/(60*1000))+" Allowed="+pnBlckRstDuration);
			 * Calendar cal=Calendar.getInstance();
			 * cal.setTime(userPhoneVO.getModifiedOn());
			 * int d1=cal.get(Calendar.DAY_OF_YEAR);
			 * cal.setTime(userPhoneVO.getFirstInvalidPinTime());
			 * int d2=cal.get(Calendar.DAY_OF_YEAR);
			 * if (_log.isDebugEnabled()) _log.debug("validatePIN",
			 * "Day Of year of Modified On="
			 * +d1+" Day Of year of FirstInvalidPinTime="+d2);
			 * if(d1!=d2 && pnBlckRstDuration<=mintInDay)
			 * {
			 * //reset
			 * userPhoneVO.setInvalidPinCount(1);
			 * userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
			 * }
			 * else if(d1!=d2 && pnBlckRstDuration>mintInDay &&
			 * (d1-d2)>=(pnBlckRstDuration/mintInDay))
			 * {
			 * //Reset
			 * userPhoneVO.setInvalidPinCount(1);
			 * userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
			 * }
			 * else if(((userPhoneVO.getModifiedOn().getTime()-userPhoneVO.
			 * getFirstInvalidPinTime().getTime())/(60*1000))<pnBlckRstDuration)
			 * {
			 * int
			 * maxPinBlckCnt=((Integer)PreferenceCache.getControlPreference(
			 * PreferenceI
			 * .C2S_MAX_PIN_BLOCK_COUNT_CODE,p_channelUserVO.getNetworkID(),
			 * p_channelUserVO.getCategoryCode())).intValue();
			 * if (userPhoneVO.getInvalidPinCount() - maxPinBlckCnt == 0)
			 * {
			 * //Set The flag that indicates that we need to bar the user
			 * because of
			 * PIN Change
			 * userPhoneVO.setInvalidPinCount(0);
			 * userPhoneVO.setFirstInvalidPinTime(null);
			 * userPhoneVO.setBarUserForInvalidPin(true);
			 * isUserBarred=true;
			 * }
			 * else
			 * userPhoneVO.setInvalidPinCount(userPhoneVO.getInvalidPinCount() +
			 * 1);
			 * 
			 * if(userPhoneVO.getInvalidPinCount()==0)
			 * userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
			 * }
			 * else
			 * {
			 * userPhoneVO.setInvalidPinCount(1);
			 * userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
			 * }
			 * }
			 * else
			 * {
			 * userPhoneVO.setInvalidPinCount(1);
			 * userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
			 * }
			 * }
			 * else
			 * {
			 * // initilize PIN Counters if ifPinCount>0
			 * if (userPhoneVO.getInvalidPinCount() > 0)
			 * {
			 * userPhoneVO.setInvalidPinCount(0);
			 * userPhoneVO.setFirstInvalidPinTime(null);
			 * updateStatus = _channelUserDAO.updateSmsPinCounter(p_con,
			 * userPhoneVO);
			 * if (updateStatus < 0)
			 * {
			 * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
			 * EventStatusI
			 * .RAISED,EventLevelI.FATAL,"ChannelUserBL[validatePIN]","",
			 * userPhoneVO.
			 * getMsisdn(),"","Not able to update invalid PIN count for users");
			 * throw new BTSLBaseException("ChannelUserBL", "validatePIN",
			 * PretupsErrorCodesI.ERROR_EXCEPTION);
			 * }
			 * }
			 * }
			 * if (increaseInvalidPinCount)
			 * {
			 * updateStatus = _channelUserDAO.updateSmsPinCounter(p_con,
			 * userPhoneVO);
			 * if (updateStatus > 0 && !isUserBarred)
			 * throw new BTSLBaseException("ChannelUserBL", "validatePIN",
			 * PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
			 * else if(updateStatus > 0 && isUserBarred)
			 * throw new BTSLBaseException("ChannelUserBL", "validatePIN",
			 * PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK);
			 * else
			 * {
			 * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
			 * EventStatusI
			 * .RAISED,EventLevelI.FATAL,"ChannelUserBL[validatePIN]","",
			 * userPhoneVO.
			 * getMsisdn(),"","Not able to update invalid PIN count for users");
			 * throw new BTSLBaseException("ChannelUserBL", "validatePIN",
			 * PretupsErrorCodesI.ERROR_EXCEPTION);
			 * }
			 * }
			 * }
			 * 
			 * }
			 * catch (BTSLBaseException bex)
			 * {
			 * throw bex;
			 * }
			 * catch (Exception e)
			 * {
			 * _log.error("validatePIN", "Exception " + e.getMessage());
			 * e._log.errorTrace(METHOD_NAME,e);
			 * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
			 * EventStatusI
			 * .RAISED,EventLevelI.FATAL,"ChannelUserBL[validatePIN]","",
			 * "","","Exception:"+e.getMessage());
			 * throw new BTSLBaseException("ChannelUserBL", "validatePIN",
			 * PretupsErrorCodesI.ERROR_EXCEPTION);
			 * }
			 * finally{
			 * if (_log.isDebugEnabled())
			 * _log.debug("validatePIN",
			 * "Exiting with increase invalid Pin Count flag=" +
			 * increaseInvalidPinCount);
			 * }
			 */
		}
	}

	/**
	 * Checks the user available balance before transferring
	 * 
	 * @param p_con
	 * @param p_transferID
	 * @param p_c2STransferVO
	 * @throws BTSLBaseException
	 */
	public static void checkUserBalanceAvailable(Connection p_con, String p_transferID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "checkUserBalanceAvailable";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_transferID = " + p_transferID);
		}
		boolean useHomeStock = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue();
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		try {
			final TransferProfileProductVO transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(((ChannelUserVO) p_c2STransferVO.getSenderVO())
					.getTransferProfileID(), p_c2STransferVO.getProductCode());
			String[] strArr = null;

			/*
			 * check that the requested quantity must be between min and max
			 * quantity allow with the user profile
			 */
			// 3.
			if (transferProfileProductVO.getC2sMinTxnAmtAsLong() > p_c2STransferVO.getTransferValue() || transferProfileProductVO.getC2sMaxTxnAmtAsLong() < p_c2STransferVO
					.getTransferValue()) {
				strArr = new String[] { PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), transferProfileProductVO.getC2sMinTxnAmt(), transferProfileProductVO
						.getC2sMaxTxnAmt() };
				EventHandler.handle(EventIDI.REQ_AMT_NOT_IN_RANGE,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserBL[checkUserBalanceAvailable]","","","","Reques amt not in the range.");

				throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_AMT_NOTBETWEEN_MINMAX, 0, strArr, null);
			}
			// For Roaming Recharge CR000012
			long userProductBalance = 0;
			/** START: Birendra: */
			boolean flag = true;
			final boolean listAddedForPDA = false;
			final UserBalancesDAO userBalanceDAO = new UserBalancesDAO();
			final ArrayList<UserProductWalletMappingVO> walletList = new ArrayList<UserProductWalletMappingVO>();
			if (userProductMultipleWallet) {

				final List<UserProductWalletMappingVO> walletsForNetAndPrdct = PretupsBL.getPrtSortWalletsForNetIdAndPrdId(p_c2STransferVO.getNetworkCode(), p_c2STransferVO
						.getProductCode());
				final List<UserProductWalletMappingVO> pdaWalletList = PretupsBL.getPDAWalletsVO(walletsForNetAndPrdct);
				ArrayList<UserProductWalletMappingVO> userbalance = new ArrayList<UserProductWalletMappingVO>();
				for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
					final UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();
					if (!(PretupsI.YES.equals(userProductWalletMappingVO.getPartialDedAlwd()))) {
						flag = false;
						break;
					}
				}

				p_c2STransferVO.setPdaWalletList(pdaWalletList);
				if (useHomeStock) {
					userbalance = userBalanceDAO.loadUserBalanceWhenPDAIsN(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
							p_c2STransferVO.getNetworkCode(), p_c2STransferVO.getProductCode());
					if (flag) {
						userBalanceDAO.loadUserBalanceForPDAWallets(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(), p_c2STransferVO
								.getNetworkCode(), p_c2STransferVO.getProductCode(), p_c2STransferVO);
						userProductBalance = p_c2STransferVO.getTotalBalanceAcrossPDAWallets();
					} else {
						userProductBalance = whenAnyWalletPDAIsN(p_con, p_c2STransferVO, methodName, userProductBalance, listAddedForPDA, walletList, pdaWalletList,
								userbalance, transferProfileProductVO.getMinResidualBalanceAsLong());
					}
				} else {
					userbalance = userBalanceDAO.loadUserBalanceWhenPDAIsN(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
							p_c2STransferVO.getReceiverNetworkCode(), p_c2STransferVO.getProductCode());
					if (flag) {
						userBalanceDAO.loadUserBalanceForPDAWallets(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(), p_c2STransferVO
								.getReceiverNetworkCode(), p_c2STransferVO.getProductCode(), p_c2STransferVO);
						userProductBalance = p_c2STransferVO.getTotalBalanceAcrossPDAWallets();
					} else {
						userProductBalance = whenAnyWalletPDAIsN(p_con, p_c2STransferVO, methodName, userProductBalance, listAddedForPDA, walletList, pdaWalletList,
								userbalance, transferProfileProductVO.getMinResidualBalanceAsLong());
					}
				}
				/** STOP: Birendra: */
			} else {

				if (useHomeStock) {
					userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
							p_c2STransferVO.getNetworkCode(), p_c2STransferVO.getProductCode());
				} else {
					userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
							p_c2STransferVO.getReceiverNetworkCode(), p_c2STransferVO.getProductCode());
				}
			}
			boolean negAddCommApply = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NEG_ADD_COMM_APPLY))).booleanValue();
			boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
			if (userProductBalance - p_c2STransferVO.getTransferValue() < 0) 
			{
				if(lrEnabled && PretupsI.YES.equals(p_c2STransferVO.getLRallowed()))
				{

					if((p_c2STransferVO.getTransferValue()- userProductBalance) <= p_c2STransferVO.getLRMaxAmount())
					{
						UserTransferCountsVO countsVO = new UserTransferCountsVO();
						UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
						countsVO = userTransferCountsDAO.loadTransferCounts(p_con, p_c2STransferVO.getSenderID(), false);

						if(!PretupsI.LAST_LR_PENDING_STATUS.equals(countsVO.getLastLrStatus()))
						{
							//last recharge credit request starts here
							long lrAmount= p_c2STransferVO.getTransferValue() - userProductBalance;

							p_c2STransferVO.setLRAmount(lrAmount);

							LastRechargeTransfer lastRechargeTransfer = new LastRechargeTransfer();
							lastRechargeTransfer.processO2CTransaction(p_c2STransferVO);
							p_c2STransferVO.setLRFlag(true);
							userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
									p_c2STransferVO.getReceiverNetworkCode(), p_c2STransferVO.getProductCode());
						}

						else
						{
							LogFactory.printLog(methodName,"channeluser.last.recharge.transfer.pending", _log);
							strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
									.getDisplayAmount(userProductBalance) };
							throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
						}
					}
					else {
						strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
								.getDisplayAmount(userProductBalance) };
						throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
					}
				}

				else
				{
					strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
							.getDisplayAmount(userProductBalance) };
					throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
				}
			}

			if(!p_c2STransferVO.getLRFlag())          //to bypass max allowed percentage check in case of last recharge credit request
			{
				final long allowedMaxTrfAmount = Double.valueOf(((userProductBalance * (double) transferProfileProductVO.getAllowedMaxPercentageInt()) / 100)).longValue();

				if (p_c2STransferVO.getTransferValue() > allowedMaxTrfAmount) {



					strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
							.getDisplayAmount(userProductBalance), String.valueOf(transferProfileProductVO.getAllowedMaxPercentageInt()) };
					throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_MAX_PER_TRF_FAIL, 0, strArr, null);
				}


				// Condition added for checking user balance in case of negative
				// additional commission on 21/04/08
				else if (negAddCommApply && !p_c2STransferVO.isStopAddnCommission()) {
					if (PretupsI.YES.equals(p_c2STransferVO.getDifferentialAllowedForService()))// &&
						// "Y".equals(p_c2STransferVO.getGiveOnlineDifferential())
					{
						final DiffCalBL diffCalBL = new DiffCalBL();
						final AdjustmentsVO adjustmentsVO = diffCalBL.differentialCalculationForBalanceCheck(p_con, p_c2STransferVO);
						if (adjustmentsVO != null) {
							final long marginAmount = Math.abs(adjustmentsVO.getMarginAmount());
							if ((userProductBalance - p_c2STransferVO.getTransferValue() - marginAmount) < (transferProfileProductVO.getMinResidualBalanceAsLong())) {
								strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
										.getDisplayAmount(userProductBalance) };
								throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
							}
						}
						else {
							if ((userProductBalance - p_c2STransferVO.getTransferValue()) < (transferProfileProductVO.getMinResidualBalanceAsLong())) {
								strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
										.getDisplayAmount(userProductBalance), PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong()) };
								throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
							}
						}
					} else {
						if ((userProductBalance - p_c2STransferVO.getTransferValue()) < (transferProfileProductVO.getMinResidualBalanceAsLong())) {
							strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
									.getDisplayAmount(userProductBalance), PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong()) };
							throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
						}
					}
				} else {
					if ((userProductBalance - p_c2STransferVO.getTransferValue()) < (transferProfileProductVO.getMinResidualBalanceAsLong())) {
						strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
								.getDisplayAmount(userProductBalance), PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong()) };
						throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
					}
				}
			}

			if (p_c2STransferVO.isRoam() && p_c2STransferVO.getRoamPenalty() > 0) {
				if (p_c2STransferVO.getTransferValue() + p_c2STransferVO.getRoamPenalty() > (userProductBalance-transferProfileProductVO.getMinResidualBalanceAsLong())) {
					p_c2STransferVO.setPenaltyInsufficientBalance(true);
					throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_ROAM, 0, null, null);
				}
			}
			if (p_c2STransferVO.getTransferItemList() != null) {
				((C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0)).setPreviousBalance(userProductBalance);
				((C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0)).setPostBalance(userProductBalance);
			}
		} catch (BTSLBaseException be) {
			_log.error(methodName, "BTSL Base Exception : " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception : " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[" + methodName + "]", p_transferID,
					p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_transferID : " + p_transferID);
		}
	}

	/**
	 * @param p_con
	 * @param p_c2STransferVO
	 * @param methodName
	 * @param userProductBalance
	 * @param listAddedForPDA
	 * @param walletList
	 * @param pdaWalletList
	 * @param userbalance
	 * @return
	 * @throws BTSLBaseException
	 */
	private static long whenAnyWalletPDAIsN(Connection p_con, C2STransferVO p_c2STransferVO, final String methodName, long userProductBalance, boolean listAddedForPDA, ArrayList<UserProductWalletMappingVO> walletList, List<UserProductWalletMappingVO> pdaWalletList, ArrayList<UserProductWalletMappingVO> userbalance, long p_minResidualBalance) throws BTSLBaseException {
		final DiffCalBL diffCalBL = new DiffCalBL();
		long balanceDeduction = 0;
		boolean negAddCommApply = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NEG_ADD_COMM_APPLY))).booleanValue();
		if (negAddCommApply) {
			final AdjustmentsVO adjustmentsVO = diffCalBL.differentialCalculationForBalanceCheck(p_con, p_c2STransferVO);
			if (adjustmentsVO != null) {
				final long marginAmount = Math.abs(adjustmentsVO.getMarginAmount());
				balanceDeduction = balanceDeduction + marginAmount;
			}
		}
		balanceDeduction = balanceDeduction + p_minResidualBalance;
		int userbalanceSize = userbalance.size();
		for (int i = 0; i < userbalanceSize; i++) {
			// for(int j=0;j<userbalance.size();j++){
			// if(pdaWalletList.get(i).getAccountCode().equals((userbalance.get(j).getBalanceType())))
			// {
			if (userbalance.get(i).getBalance() - balanceDeduction >= p_c2STransferVO.getTransferValue()) {
				walletList.add(pdaWalletList.get(i));
				p_c2STransferVO.setPdaWalletList(walletList);
				listAddedForPDA = true;
				p_c2STransferVO.setTotalBalanceAcrossPDAWallets(userbalance.get(i).getBalance());
				p_c2STransferVO.setTotalPreviousBalanceAcrossPDAWallets(userbalance.get(i).getPreviousBalance());
				userProductBalance = userbalance.get(i).getBalance();
				break;
			}
			// }
			// }
		}
		if (!listAddedForPDA) {
			final String strArr[] = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
					.getDisplayAmount(userProductBalance) };
			throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
		}
		return userProductBalance;
	}

	/**
	 * Debits the user ID for the transfer value
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO debitUserBalanceForProduct(Connection p_con, String p_requestID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "debitUserBalanceForProduct";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_requestID = " + p_requestID);
		}
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		boolean offlineSettleExtUsrBal = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OFFLINE_SETTLE_EXTUSR))).booleanValue();
		UserBalancesVO userBalancesVO = null;
		try {
			userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2STransferVO, PretupsI.TRANSFER_TYPE_TXN, p_c2STransferVO.getSourceType(), PretupsI.DEBIT,
					PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);

			if(offlineSettleExtUsrBal && PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_c2STransferVO.getRequestGatewayType()) &&
					Arrays.asList(Constants.getProperty("ONLINE_SETTLE_EXTUSRID_LIST").split(",")).contains(p_c2STransferVO.getSenderID()) && 
					Arrays.asList(Constants.getProperty("ONLINE_SETTLE_SERVICES_LIST").split(",")).contains(p_c2STransferVO.getServiceType())){
				final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
				c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setPreviousBalance(0);
				c2STransferItemVO.setPostBalance(0);
			}else{
				final int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(p_con, p_c2STransferVO.getTransferDate(), userBalancesVO);
				_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = " + dailyBalanceUpdateCount);

				/** START: Birendra: */
				if (userProductMultipleWallet) {
					final ChannelUserVO senderVO = (ChannelUserVO) p_c2STransferVO.getSenderVO();
					final String senderTransferProfileID = senderVO.getTransferProfileID();
					final String categoryCode = senderVO.getCategoryCode();

					if(p_c2STransferVO.isDebitPenalty()){
						Comparator<UserProductWalletMappingVO> comparator = new Comparator<UserProductWalletMappingVO>(){
							@Override
							public int compare(UserProductWalletMappingVO u1, UserProductWalletMappingVO u2) {
								return u1.getPenaltyAccountPriority() - u2.getPenaltyAccountPriority(); 
							}
						};
						Collections.sort(p_c2STransferVO.getPdaWalletList(),comparator);
					} 

					final int[] updateCount = _userBalancesDAO.debitUserBalancesFromWallets(p_con, userBalancesVO, senderTransferProfileID, p_c2STransferVO.getProductCode(),
							true, categoryCode);

					if (updateCount == null || updateCount.length <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
					}
					// Update Previous and Post balances of sender in sender Item
					final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
					c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					/**
					 * Setting totalBalance and totalPrevBalance of all PDA wallets
					 * in C2STransferVO bean to use it later.
					 */
					p_c2STransferVO.setTotalBalanceAcrossPDAWallets(userBalancesVO.getBalance());
					p_c2STransferVO.setTotalPreviousBalanceAcrossPDAWallets(userBalancesVO.getPreviousBalance());
					/** Birendra:Added */

				} else {

					// _userBalancesDAO.updateUserDailyBalances(p_con,p_c2STransferVO.getTransferDate(),userBalancesVO);
					final int updateCount = _userBalancesDAO.debitUserBalances(p_con, userBalancesVO, ((ChannelUserVO) p_c2STransferVO.getSenderVO()).getTransferProfileID(),
							p_c2STransferVO.getProductCode(), true, ((ChannelUserVO) p_c2STransferVO.getSenderVO()).getCategoryCode());
					if (updateCount <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
					}
					// Update Previous and Post balances of sender in sender Item
					final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
					c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
				}
			}

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(be);
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[" + methodName + "]", p_requestID,
					p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_requestID : " + p_requestID);
		}
		return userBalancesVO;
	}
	
	
	
	
	/**
	 * Debits the user ID for the transfer value
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO debitUserBalanceForProductModified(Connection p_con, String p_requestID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "debitUserBalanceForProduct";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_requestID = " + p_requestID);
		}
		boolean dailyBalanceUpdated = false;
		boolean balanceUpdated = false;
		UserBalancesVO userBalancesVO = null;
		try {
			userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2STransferVO, PretupsI.TRANSFER_TYPE_TXN, p_c2STransferVO.getSourceType(), PretupsI.DEBIT,
					PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.OFFLINE_SETTLE_EXTUSR)).booleanValue() && PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_c2STransferVO.getRequestGatewayType()) &&
					Arrays.asList(Constants.getProperty("ONLINE_SETTLE_EXTUSRID_LIST").split(",")).contains(p_c2STransferVO.getSenderID()) && 
					Arrays.asList(Constants.getProperty("ONLINE_SETTLE_SERVICES_LIST").split(",")).contains(p_c2STransferVO.getServiceType())){
				final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
				c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setPreviousBalance(0);
				c2STransferItemVO.setPostBalance(0);
			}else{
				// select for update 1st 
				final ArrayList<UserBalancesVO> dailyBalanceUpdateCountList = _userBalancesDAO.getUserDailyBalancesList(p_con, p_c2STransferVO.getTransferDate(), userBalancesVO);
				/** START: Birendra: */
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
					final ChannelUserVO senderVO = (ChannelUserVO) p_c2STransferVO.getSenderVO();
					final String senderTransferProfileID = senderVO.getTransferProfileID();
					final String categoryCode = senderVO.getCategoryCode();

					if(p_c2STransferVO.isDebitPenalty()){
						Comparator<UserProductWalletMappingVO> comparator = new Comparator<UserProductWalletMappingVO>(){
							@Override
							public int compare(UserProductWalletMappingVO u1, UserProductWalletMappingVO u2) {
								return u1.getPenaltyAccountPriority() - u2.getPenaltyAccountPriority(); 
							}
						};
						Collections.sort(p_c2STransferVO.getPdaWalletList(),comparator);
					} 
                    // Select for update called for 2nd time with updates in user balance. Its Commit exist within debitUserBalancesFromWalletsModified
					final int[] updateCount = _userBalancesDAO.debitUserBalancesFromWalletsModified(p_con, userBalancesVO, senderTransferProfileID, p_c2STransferVO.getProductCode(),
							true, categoryCode);

					if (updateCount == null || updateCount.length <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
					}else {
						balanceUpdated = true;
					}
					// Update Previous and Post balances of sender in sender Item
					final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
					c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					userBalancesVO.setDailyBalanceUpdateCountList(dailyBalanceUpdateCountList);
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					p_c2STransferVO.setTotalBalanceAcrossPDAWallets(userBalancesVO.getBalance());
					p_c2STransferVO.setTotalPreviousBalanceAcrossPDAWallets(userBalancesVO.getPreviousBalance());

				} else {
					// _userBalancesDAO.updateUserDailyBalances(p_con,p_c2STransferVO.getTransferDate(),userBalancesVO);
					 // Select for update called for 2nd time with updates in user balance.
					final long[] update = _userBalancesDAO.debitUserBalancesModified(p_con, userBalancesVO, ((ChannelUserVO) p_c2STransferVO.getSenderVO()).getTransferProfileID(),
							p_c2STransferVO.getProductCode(), true, ((ChannelUserVO) p_c2STransferVO.getSenderVO()).getCategoryCode());
					if (update[0] <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
					}else {
						p_con.commit();
						balanceUpdated = true;
						_userBalancesDAO.insertUserThresholdCounters(p_con, userBalancesVO, TransferProfileProductCache.getTransferProfileDetails(((ChannelUserVO) p_c2STransferVO.getSenderVO()).getTransferProfileID(), p_c2STransferVO.getProductCode()), update[1], update[2], ((ChannelUserVO) p_c2STransferVO.getSenderVO()).getCategoryCode());
					}
					// Update Previous and Post balances of sender in sender Item
					final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
					c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					userBalancesVO.setDailyBalanceUpdateCountList(dailyBalanceUpdateCountList);
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
				}
			}

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
		if(balanceUpdated) {
			userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2STransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferVO.getSourceType(), PretupsI.CREDIT,
					PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				final int[] updateCounts = _userBalancesDAO.creditUserBalancesForWalletsModified(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
				if (updateCounts == null || updateCounts.length <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				} else {
					try {
						p_con.commit();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			} else {
				final int updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
				if (updateCount <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				}else {
					try {
						p_con.commit();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		}
			throw new BTSLBaseException(be);
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
		if(balanceUpdated) {
			userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2STransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferVO.getSourceType(), PretupsI.CREDIT,
					PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				final int[] updateCounts = _userBalancesDAO.creditUserBalancesForWalletsModified(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
				if (updateCounts == null || updateCounts.length <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				}else {
					try {
						p_con.commit();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				final int updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
				if (updateCount <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				}else {
					try {
						p_con.commit();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[" + methodName + "]", p_requestID,
					p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_requestID : " + p_requestID);
		}
		return userBalancesVO;
	}
	
	
	
	/**
	 * Credits the user back for the failed transaction and also make an entry
	 * in C2S Items table
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO creditUserBalanceForProductModified(Connection p_con, String p_requestID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "creditUserBalanceForProductModified";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_requestID : " + p_requestID);
		}
		UserBalancesVO userBalancesVO = null;
		try {

			PrivateRchrgVO prvo=null;
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW)).booleanValue())
			{
				PrivateRchrgDAO prdao= new PrivateRchrgDAO();
				prvo=prdao.loadSubscriberSIDDetails(p_con,p_c2STransferVO.getReceiverMsisdn());				
			}
			C2STransferItemVO c2STransferItemVO = new C2STransferItemVO();
			userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2STransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferVO.getSourceType(), PretupsI.CREDIT,
					PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.OFFLINE_SETTLE_EXTUSR)).booleanValue() && PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_c2STransferVO.getRequestGatewayType()) &&
					Arrays.asList(Constants.getProperty("ONLINE_SETTLE_EXTUSRID_LIST").split(",")).contains(p_c2STransferVO.getSenderID()) && 
					Arrays.asList(Constants.getProperty("ONLINE_SETTLE_SERVICES_LIST").split(",")).contains(p_c2STransferVO.getServiceType())){
				c2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
				c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setPreviousBalance(0);
				c2STransferItemVO.setPostBalance(0);
			}else{
				final int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(p_con, p_c2STransferVO.getTransferDate(), userBalancesVO);
				_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = " + dailyBalanceUpdateCount);
				
				if(dailyBalanceUpdateCount > 0) {
					p_con.commit();
				}
				// Credit the sender
				// int
				// updateCount=_userBalancesDAO.creditUserBalances(p_con,userBalancesVO);

				/** START: Birendra: 01FEB2015 */
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
					final int[] updateCounts = _userBalancesDAO.creditUserBalancesForWallets(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
					if (updateCounts == null || updateCounts.length <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}
				} else {
					final int updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
					if (updateCount <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}

				}
				/** START: Birendra: 01FEB2015 */

				// Update Previous and Post balances of sender in sender Item,
				// creating new Items VO for credit back
				final C2STransferItemVO oldC2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);

				c2STransferItemVO.setMsisdn(oldC2STransferItemVO.getMsisdn());
				c2STransferItemVO.setRequestValue(oldC2STransferItemVO.getRequestValue());
				c2STransferItemVO.setSubscriberType(oldC2STransferItemVO.getSubscriberType());
				c2STransferItemVO.setTransferDate(oldC2STransferItemVO.getTransferDate());
				c2STransferItemVO.setTransferDateTime(oldC2STransferItemVO.getTransferDateTime());
				c2STransferItemVO.setTransferID(oldC2STransferItemVO.getTransferID());
				c2STransferItemVO.setUserType(oldC2STransferItemVO.getUserType());
				c2STransferItemVO.setEntryDate(oldC2STransferItemVO.getEntryDate());
				c2STransferItemVO.setEntryDateTime(oldC2STransferItemVO.getEntryDateTime());
				c2STransferItemVO.setPrefixID(oldC2STransferItemVO.getPrefixID());
				c2STransferItemVO.setTransferValue(oldC2STransferItemVO.getTransferValue());
				c2STransferItemVO.setInterfaceID(oldC2STransferItemVO.getInterfaceID());
				c2STransferItemVO.setInterfaceType(oldC2STransferItemVO.getInterfaceType());
				c2STransferItemVO.setServiceClass(oldC2STransferItemVO.getServiceClass());
				c2STransferItemVO.setServiceClassCode(oldC2STransferItemVO.getServiceClassCode());
				c2STransferItemVO.setInterfaceHandlerClass(oldC2STransferItemVO.getInterfaceHandlerClass());
				// added by vikram
				c2STransferItemVO.setLanguage(oldC2STransferItemVO.getLanguage());
				c2STransferItemVO.setCountry(oldC2STransferItemVO.getCountry());

				c2STransferItemVO.setSNo(3);
				c2STransferItemVO.setEntryType(PretupsI.CREDIT);
				c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
				c2STransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
				c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
			}
			final ArrayList itemList = new ArrayList();
			itemList.add(0, c2STransferItemVO);
			final int addCount = new C2STransferDAO().addC2STransferItemDetails(p_con, itemList, p_c2STransferVO.getTransferID());
			if (addCount < 0) {
				throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_MAKECREDIT_ENTRY);
			}
			p_c2STransferVO.getTransferItemList().addAll(itemList);
			p_c2STransferVO.setCreditAmount(p_c2STransferVO.getTransferValue());
			p_c2STransferVO.setCreditBackStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			if (!PretupsI.IAT_TRANSACTION_TYPE.equals(p_c2STransferVO.getExtCreditIntfceType())) {
				final String[] messageArgArray = { prvo!=null ? (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue() ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2STransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2STransferVO.getRequestedAmount()), p_c2STransferVO
						.getTransferID(), PretupsBL.getDisplayAmount(userBalancesVO.getBalance()) };
				p_c2STransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) p_c2STransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
						PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS, messageArgArray));
			} else {
				final String[] messageArgArray = { p_c2STransferVO.getIatTransferItemVO().getIatRcvrCountryCode() + (prvo!=null ? (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue() ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()):p_c2STransferVO.getReceiverMsisdn()), PretupsBL
						.getDisplayAmount(p_c2STransferVO.getRequestedAmount()), p_c2STransferVO.getTransferID(), PretupsBL.getDisplayAmount(userBalancesVO
								.getBalance()) };
				p_c2STransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) p_c2STransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
						PretupsErrorCodesI.IAT_C2S_SENDER_CREDIT_SUCCESS, messageArgArray));
			}

		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[creditUserBalanceForProduct]",
					p_requestID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_requestID : " + p_requestID);
		}
		return userBalancesVO;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Credits the user back for the failed transaction and also make an entry
	 * in C2S Items table
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO creditUserBalanceForProduct(Connection p_con, String p_requestID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "creditUserBalanceForProduct";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_requestID : " + p_requestID);
		}
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		boolean privateSidServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW);
		boolean offlineSettleExtUsrBal = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OFFLINE_SETTLE_EXTUSR))).booleanValue();
		boolean sidEncryptionAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED);
		UserBalancesVO userBalancesVO = null;
		try {

			PrivateRchrgVO prvo=null;
			if(privateSidServiceAllow)
			{
				PrivateRchrgDAO prdao= new PrivateRchrgDAO();
				prvo=prdao.loadSubscriberSIDDetails(p_con,p_c2STransferVO.getReceiverMsisdn());				
			}
			C2STransferItemVO c2STransferItemVO = new C2STransferItemVO();
			userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2STransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferVO.getSourceType(), PretupsI.CREDIT,
					PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			if(offlineSettleExtUsrBal && PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_c2STransferVO.getRequestGatewayType()) &&
					Arrays.asList(Constants.getProperty("ONLINE_SETTLE_EXTUSRID_LIST").split(",")).contains(p_c2STransferVO.getSenderID()) && 
					Arrays.asList(Constants.getProperty("ONLINE_SETTLE_SERVICES_LIST").split(",")).contains(p_c2STransferVO.getServiceType())){
				c2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
				c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setPreviousBalance(0);
				c2STransferItemVO.setPostBalance(0);
			}else{
				final int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(p_con, p_c2STransferVO.getTransferDate(), userBalancesVO);
				_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = " + dailyBalanceUpdateCount);
				// Credit the sender
				// int
				// updateCount=_userBalancesDAO.creditUserBalances(p_con,userBalancesVO);

				/** START: Birendra: 01FEB2015 */
				if (userProductMultipleWallet) {
					final int[] updateCounts = _userBalancesDAO.creditUserBalancesForWallets(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
					if (updateCounts == null || updateCounts.length <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}
				} else {
					final int updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
					if (updateCount <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}

				}
				/** START: Birendra: 01FEB2015 */

				// Update Previous and Post balances of sender in sender Item,
				// creating new Items VO for credit back
				final C2STransferItemVO oldC2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);

				c2STransferItemVO.setMsisdn(oldC2STransferItemVO.getMsisdn());
				c2STransferItemVO.setRequestValue(oldC2STransferItemVO.getRequestValue());
				c2STransferItemVO.setSubscriberType(oldC2STransferItemVO.getSubscriberType());
				c2STransferItemVO.setTransferDate(oldC2STransferItemVO.getTransferDate());
				c2STransferItemVO.setTransferDateTime(oldC2STransferItemVO.getTransferDateTime());
				c2STransferItemVO.setTransferID(oldC2STransferItemVO.getTransferID());
				c2STransferItemVO.setUserType(oldC2STransferItemVO.getUserType());
				c2STransferItemVO.setEntryDate(oldC2STransferItemVO.getEntryDate());
				c2STransferItemVO.setEntryDateTime(oldC2STransferItemVO.getEntryDateTime());
				c2STransferItemVO.setPrefixID(oldC2STransferItemVO.getPrefixID());
				c2STransferItemVO.setTransferValue(oldC2STransferItemVO.getTransferValue());
				c2STransferItemVO.setInterfaceID(oldC2STransferItemVO.getInterfaceID());
				c2STransferItemVO.setInterfaceType(oldC2STransferItemVO.getInterfaceType());
				c2STransferItemVO.setServiceClass(oldC2STransferItemVO.getServiceClass());
				c2STransferItemVO.setServiceClassCode(oldC2STransferItemVO.getServiceClassCode());
				c2STransferItemVO.setInterfaceHandlerClass(oldC2STransferItemVO.getInterfaceHandlerClass());
				// added by vikram
				c2STransferItemVO.setLanguage(oldC2STransferItemVO.getLanguage());
				c2STransferItemVO.setCountry(oldC2STransferItemVO.getCountry());

				c2STransferItemVO.setSNo(3);
				c2STransferItemVO.setEntryType(PretupsI.CREDIT);
				c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
				c2STransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
				c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
			}
			final ArrayList itemList = new ArrayList();
			itemList.add(0, c2STransferItemVO);
			final int addCount = new C2STransferDAO().addC2STransferItemDetails(p_con, itemList, p_c2STransferVO.getTransferID());
			if (addCount < 0) {
				throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_MAKECREDIT_ENTRY);
			}
			p_c2STransferVO.getTransferItemList().addAll(itemList);
			p_c2STransferVO.setCreditAmount(p_c2STransferVO.getTransferValue());
			p_c2STransferVO.setCreditBackStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			if (!PretupsI.IAT_TRANSACTION_TYPE.equals(p_c2STransferVO.getExtCreditIntfceType())) {
				final String[] messageArgArray = { prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2STransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2STransferVO.getRequestedAmount()), p_c2STransferVO
						.getTransferID(), PretupsBL.getDisplayAmount(userBalancesVO.getBalance()) };
				p_c2STransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) p_c2STransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
						PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS, messageArgArray));
			} else {
				final String[] messageArgArray = { p_c2STransferVO.getIatTransferItemVO().getIatRcvrCountryCode() + (prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()):p_c2STransferVO.getReceiverMsisdn()), PretupsBL
						.getDisplayAmount(p_c2STransferVO.getRequestedAmount()), p_c2STransferVO.getTransferID(), PretupsBL.getDisplayAmount(userBalancesVO
								.getBalance()) };
				p_c2STransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) p_c2STransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
						PretupsErrorCodesI.IAT_C2S_SENDER_CREDIT_SUCCESS, messageArgArray));
			}

		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[creditUserBalanceForProduct]",
					p_requestID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_requestID : " + p_requestID);
		}
		return userBalancesVO;
	}

	/**
	 * Method to validate the sender available controls like balance check and
	 * Transfer Out counts
	 * 
	 * @param p_con
	 * @param p_transferID
	 * @param p_c2STransferVO
	 * @throws BTSLBaseException
	 */
	public static void validateSenderAvailableControls(Connection p_con, String p_transferID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "validateSenderAvailableControls";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_transferID: " + p_transferID);
		}
		try {
			checkUserBalanceAvailable(p_con, p_transferID, p_c2STransferVO);
			// UserTransferCountsVO
			// userTransferCountsVO=ChannelTransferBL.checkC2STransferOutCounts(p_con,p_c2STransferVO,isLockRecordForUpdate,true);
		} catch (BTSLBaseException be) {
			_log.error(methodName, "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[validateSenderAvailableControls]",
					p_transferID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_transferID : " + p_transferID);
		}
	}

	/**
	 * Method to prepare the User balance VO from the C2S Transfer VO for
	 * updation of balances
	 * 
	 * @param p_c2sTransferVO
	 * @param p_transferType
	 * @param p_source
	 * @param p_entryType
	 * @param p_transType
	 * @param p_transferCategory
	 * @return UserBalancesVO
	 */
	private static UserBalancesVO prepareUserBalanceVOFromTransferVO(C2STransferVO p_c2sTransferVO, String p_transferType, String p_source, String p_entryType, String p_transType, String p_transferCategory) {
		final UserBalancesVO userBalancesVO = new UserBalancesVO();
		userBalancesVO.setUserID(p_c2sTransferVO.getSenderID());
		userBalancesVO.setProductCode(p_c2sTransferVO.getProductCode());
		userBalancesVO.setProductName(p_c2sTransferVO.getProductName());
		userBalancesVO.setProductShortName(p_c2sTransferVO.getProductName());
		userBalancesVO.setNetworkCode(p_c2sTransferVO.getNetworkCode());
		boolean useHomeStock = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue();
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		// Roam Recharge CR 000012
		if (useHomeStock) {
			userBalancesVO.setNetworkFor(p_c2sTransferVO.getNetworkCode());
		} else {
			userBalancesVO.setNetworkFor(p_c2sTransferVO.getReceiverNetworkCode());
		}

		userBalancesVO.setLastTransferID(p_c2sTransferVO.getTransferID());
		userBalancesVO.setSource(p_source);
		userBalancesVO.setCreatedBy(p_c2sTransferVO.getCreatedBy());
		userBalancesVO.setEntryType(p_entryType);
		userBalancesVO.setType(p_transType);
		userBalancesVO.setTransferCategory(p_transferCategory);
		userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getRequestedAmount()));
		userBalancesVO.setLastTransferType(p_transferType);
		userBalancesVO.setLastTransferOn(p_c2sTransferVO.getCreatedOn());


		userBalancesVO.setQuantityToBeUpdated(((C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0)).getTransferValue());


		// Added to log user MSISDN on 13/02/2008
		userBalancesVO.setUserMSISDN(p_c2sTransferVO.getSenderMsisdn());
		/** START: Birendra: */
		if (userProductMultipleWallet) {
			userBalancesVO.setPdaWalletList(p_c2sTransferVO.getPdaWalletList());
			userBalancesVO.setBalance(p_c2sTransferVO.getTotalBalanceAcrossPDAWallets());
			userBalancesVO.setPreviousBalance(p_c2sTransferVO.getTotalPreviousBalanceAcrossPDAWallets());
		}
		/** STOP: Birendra: */

		userBalancesVO.setQuantityToBeUpdated(userBalancesVO.getQuantityToBeUpdated());
		if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE) && p_c2sTransferVO.getUserLoanVOList()!=null ) {
			userBalancesVO.setUserLoanVOList(p_c2sTransferVO.getUserLoanVOList());
		
		}
		
		
		if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)&&p_c2sTransferVO.getChannelSoSVOList()!=null){
			ArrayList<ChannelSoSVO>  chnlSoSVOList = new ArrayList<> ();
			int c2sTransferVOListSize = p_c2sTransferVO.getChannelSoSVOList().size();
			for(int index = 0 ; index< c2sTransferVOListSize;index++){
				ChannelSoSVO chnlSOSVO = (ChannelSoSVO) p_c2sTransferVO.getChannelSoSVOList().get(0);
				chnlSoSVOList.add(new ChannelSoSVO(chnlSOSVO.getUserId(),chnlSOSVO.getMsisdn(),chnlSOSVO.getSosAllowed(),chnlSOSVO.getSosAllowedAmount(),chnlSOSVO.getSosThresholdLimit()));
			}
			userBalancesVO.setChannelSoSVOList(chnlSoSVOList);
		}
		boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
		if(lrEnabled)
		{
			userBalancesVO.setLRFlag(p_c2sTransferVO.getLRFlag());
		}

		return userBalancesVO;
	}

	/**
	 * Method loadChannelUserForReturnWithXfrRule()
	 * This method is to load the user list for the C2C Return
	 * 
	 * @param p_con
	 * @param p_channelTransferRuleVO
	 * @param p_toCategoryCode
	 * @param p_userName
	 * @param p_fromUserID
	 * @param p_channelUserVO
	 *            ChannelUserVO
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadChannelUserForReturnWithXfrRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO, String p_toCategoryCode, String p_userName, String p_fromUserID, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("loadChannelUserForReturnWithXfrRule",
					"Entered  p_channelTransferRuleVO=" + p_channelTransferRuleVO + ", To Category Code: " + p_toCategoryCode + " User Name: " + p_userName + " p_fromUserID: " + p_fromUserID + ",p_channelUserVO=" + p_channelUserVO);
		}
		final ArrayList arrayList = loadUserListForXfr(p_con, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN, p_channelTransferRuleVO, p_toCategoryCode, p_userName,
				p_channelUserVO);
		if (_log.isDebugEnabled()) {
			_log.debug("loadChannelUserForReturnWithXfrRule", "Exiting:  arrayList Size =" + arrayList.size());
		}
		return arrayList;
	}

	/**
	 * Method loadChannelUserForWithdrawWithXfrRule()
	 * This method is to load the user list for the C2C Withdraw
	 * 
	 * @param p_con
	 * @param p_channelTransferRuleVO
	 * @param p_toCategoryCode
	 * @param p_userName
	 * @param p_fromUserID
	 * @param p_channelUserVO
	 *            ChannelUserVO
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadChannelUserForWithdrawWithXfrRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO, String p_toCategoryCode, String p_userName, String p_fromUserID, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("loadChannelUserForWithdrawWithXfrRule",
					"Entered p_channelTransferRuleVO=" + p_channelTransferRuleVO + ", To Category Code: " + p_toCategoryCode + " User Name: " + p_userName + " p_fromUserID: " + p_fromUserID + ",p_channelUserVO=" + p_channelUserVO);
		}

		final ArrayList arrayList = loadUserListForXfr(p_con, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW, p_channelTransferRuleVO, p_toCategoryCode, p_userName,
				p_channelUserVO);
		if (_log.isDebugEnabled()) {
			_log.debug("loadChannelUserForWithdrawWithXfrRule", "Exiting:  arrayList Size =" + arrayList.size());
		}
		return arrayList;
	}

	/**
	 * Method loadChannelUserForXfrWithXfrRule()
	 * This method is to load the user list for the C2C Transfer.
	 * 
	 * @param p_con
	 * @param p_channelTransferRuleVO
	 * @param p_toCategoryCode
	 * @param p_userName
	 * @param p_channelUserVO
	 *            ChannelUserVO
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList loadChannelUserForXfrWithXfrRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO, String p_toCategoryCode, String p_userName, ChannelUserVO p_channelUserVO) throws BTSLBaseException {

		if (_log.isDebugEnabled()) {
			_log.debug("loadChannelUserForXfrWithXfrRule",
					"Entered p_channelTransferRuleVO=" + p_channelTransferRuleVO + ", To Category Code: " + p_toCategoryCode + " User Name: " + p_userName + ",p_channelTransferRuleVO=" + p_channelTransferRuleVO + ",p_channelUserVO=" + p_channelUserVO);
		}
		final ArrayList arrayList = loadUserListForXfr(p_con, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER, p_channelTransferRuleVO, p_toCategoryCode, p_userName,
				p_channelUserVO);
		if (_log.isDebugEnabled()) {
			_log.debug("loadChannelUserForXfrWithXfrRule", "Exiting:  arrayList Size =" + arrayList.size());
		}
		return arrayList;
	}

	/**
	 * Method to bar the sender MSISDN
	 * 
	 * @param p_con
	 * @param p_channelUserVO
	 * @param p_barredType
	 * @param p_currentDate
	 * @param p_module
	 * @throws BTSLBaseException
	 */
	public static void barSenderMSISDN(Connection p_con, ChannelUserVO p_channelUserVO, String p_barredType, Date p_currentDate, String p_module) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("barSenderMSISDN",
					"Entered with Sender MSISDN=" + p_channelUserVO.getUserPhoneVO().getMsisdn() + " p_currentDate=" + p_currentDate + " p_module=" + p_module);
		}
		final String METHOD_NAME = "barSenderMSISDN";
		try {
			int addCount = 0;
			final BarredUserVO barredUserVO = prepareBarredUserVO(p_channelUserVO, p_barredType, PretupsI.BARRED_USER_TYPE_SENDER,
					PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN, PretupsI.SYSTEM_USER, p_module);

			addCount = new BarredUserDAO().addBarredUser(p_con, barredUserVO);
			if (addCount < 0) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[barSenderMSISDN]", "",
						p_channelUserVO.getUserPhoneVO().getMsisdn(), p_channelUserVO.getNetworkID(), "Not able to bar the sender MSISDN");
				throw new BTSLBaseException("ChannelUserBL", "barSenderMSISDN", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
		} catch (BTSLBaseException be) {
			// EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserBL[barSenderMSISDN]","",p_channelUserVO.getUserPhoneVO().getMsisdn(),p_channelUserVO.getNetworkID(),"Not able to bar the sender MSISDN");
			throw be;
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error("barSenderMSISDN", "Exception :" + e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[barSenderMSISDN]", "", p_channelUserVO
					.getUserPhoneVO().getMsisdn(), p_channelUserVO.getNetworkID(), "Not able to bar the sender MSISDN,getting Exception=" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "barSenderMSISDN", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
	}

	/**
	 * Method to prepare the VO for barring the user
	 * 
	 * @param p_channelUserVO
	 * @param p_barredType
	 * @param p_userType
	 * @param p_reason
	 * @param p_createdBy
	 * @param p_module
	 * @return BarredUserVO
	 */
	public static BarredUserVO prepareBarredUserVO(ChannelUserVO p_channelUserVO, String p_barredType, String p_userType, String p_reason, String p_createdBy, String p_module) {
		final String METHOD_NAME = "prepareBarredUserVO";
		final BarredUserVO barredUserVO = new BarredUserVO();
		barredUserVO.setModule(p_module);
		if (!PretupsI.NOT_AVAILABLE.equals(p_channelUserVO.getUserPhoneVO().getMsisdn())) {
			barredUserVO.setMsisdn(p_channelUserVO.getUserPhoneVO().getMsisdn());
		} else {
			barredUserVO.setMsisdn(p_channelUserVO.getLoginID());
		}
		barredUserVO.setBarredType(p_barredType);
		barredUserVO.setCreatedBy(p_createdBy);
		barredUserVO.setCreatedOn(p_channelUserVO.getModifiedOn());
		barredUserVO.setNetworkCode(p_channelUserVO.getNetworkID());
		barredUserVO.setModifiedBy(p_createdBy);
		barredUserVO.setModifiedOn(p_channelUserVO.getModifiedOn());
		barredUserVO.setUserType(p_userType);
		try {
			barredUserVO.setBarredReason(URLDecoder.decode(BTSLUtil.getMessage(p_channelUserVO.getUserPhoneVO().getLocale(), p_reason, null), "UTF16"));
		} catch (Exception e) {
			_log.error("prepareBarredUserVO", "Exception while decoding message e=" + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			barredUserVO.setBarredReason("N.A.");
		}
		return barredUserVO;
	}

	/**
	 * Method to prepare the new List that needs to be added in the items table
	 * at the time of reconciliation
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_c2sTransferVO
	 *            C2STransferVO
	 * @param p_status
	 *            String
	 * @param p_forwardPath
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public static ArrayList prepareNewC2SReconList(Connection p_con, C2STransferVO p_c2sTransferVO, String p_status, String p_forwardPath) throws BTSLBaseException {
		final String METHOD_NAME = "prepareNewC2SReconList";
		ArrayList newList = null;
		if (_log.isDebugEnabled()) {
			_log.debug("prepareNewC2SReconList",
					" Entered Transfer ID:" + p_c2sTransferVO.getTransferID() + " p_status to be made=" + p_status + ", p_forwardPath=" + p_forwardPath);
		}
		try {
			final ArrayList p_oldItemsList = p_c2sTransferVO.getTransferItemList();
			final int listSize = p_oldItemsList.size();
			C2STransferItemVO senderItemVO = null;
			C2STransferItemVO receiverItemVO = null;
			C2STransferItemVO senderCreditBackItemVO = null;

			String receiverStatus = null;

			final Date currentDate = new Date();
			boolean creditedBackInAmb = false;
			/*
			 * here we check if orginal list size is 2 then the creditBack is
			 * not done yet other wise if list size
			 * is 3 then sender's creditBack is done at the time of txn.
			 */
			switch (listSize) {
			case 2: {
				senderItemVO = (C2STransferItemVO) p_oldItemsList.get(0);
				receiverItemVO = (C2STransferItemVO) p_oldItemsList.get(1);

				receiverStatus = receiverItemVO.getTransferStatus();
				/*
				 * here we are checking only AMBIGOUS case only in future
				 * UNDERPROCESS case may be considered
				 */
				if (receiverStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
					/*
					 * make that creditBack is not done and call other method to
					 * handle Ambiguous case
					 */
					creditedBackInAmb = false;
					newList = handleReceiverAmbigousCase(p_con, p_c2sTransferVO, p_status, listSize, currentDate, creditedBackInAmb);
				}
			}
			break;
			case 3: {
				senderItemVO = (C2STransferItemVO) p_oldItemsList.get(0);
				receiverItemVO = (C2STransferItemVO) p_oldItemsList.get(1);
				senderCreditBackItemVO = (C2STransferItemVO) p_oldItemsList.get(2);

				receiverStatus = receiverItemVO.getTransferStatus();

				/*
				 * make that creditBack is done and call other method to handle
				 * Ambiguous case
				 */
				creditedBackInAmb = true;
				/*
				 * At this time if creditBack is done then if the Txn is be make
				 * as success then we have to
				 * debit the user otherwise we have to credit the user.
				 */
				if (receiverStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
					newList = handleReceiverAmbigousCase(p_con, p_c2sTransferVO, p_status, listSize, currentDate, creditedBackInAmb);
				}
			}
			break;
			}
		} catch (BTSLBaseException be) {
			_log.error("prepareNewC2SReconList", "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("prepareNewC2SReconList", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[prepareNewC2SReconList]",
					p_c2sTransferVO.getTransferID(), "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "prepareNewC2SReconList", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("prepareNewC2SReconList", " Exiting for Transfer ID:" + p_c2sTransferVO.getTransferID() + " With Size=" + newList.size());
		}
		return newList;
	}

	/**
	 * Method to handle the Receiver Ambigous case in the C2S module
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_c2sTransferVO
	 *            C2STransferVO
	 * @param p_status
	 * @param p_size
	 * @param p_date
	 * @param p_isAlreadyCreditBack
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	private static ArrayList handleReceiverAmbigousCase(Connection p_con, C2STransferVO p_c2sTransferVO, String p_status, int p_size, Date p_date, boolean p_isAlreadyCreditBack) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("handleReceiverAmbigousCase",
					" Entered Transfer ID:" + p_c2sTransferVO.getTransferID() + " p_status to be made=" + p_status + " p_size=" + p_size + " p_date=" + p_date + "p_isAlreadyCreditBack=" + p_isAlreadyCreditBack);
		}
		final ArrayList transList = new ArrayList();
		final ArrayList p_transferItemList = p_c2sTransferVO.getTransferItemList();
		int listSize = p_transferItemList.size();
		UserBalancesVO userBalancesVO = null;
		UserDAO userDAO= new UserDAO();
		final String METHOD_NAME = "handleReceiverAmbigousCase";
		LoyaltyTxnDAO loyaltytxnDAO = null;
		boolean privateSidServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW);
		boolean offlineSettleExtUsrBal = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OFFLINE_SETTLE_EXTUSR))).booleanValue();
		boolean sidEncryptionAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED);
		try {
			loyaltytxnDAO = new LoyaltyTxnDAO();
			C2STransferItemVO c2sTransferItemVO = null;
			if(String.valueOf(p_c2sTransferVO.getRoamPenalty())!=null){
				p_c2sTransferVO.setIsRoam(true);
			}

			PrivateRchrgVO prvo=null;
			if(privateSidServiceAllow)
			{
				PrivateRchrgDAO prdao= new PrivateRchrgDAO();
				prvo=prdao.loadSubscriberSIDDetails(p_con,p_c2sTransferVO.getReceiverMsisdn());				
			}

			/*
			 * if request if for fail then check creditBack status of the txn if
			 * not done then credit back the sender
			 * balance and update also users daily balances
			 */
			if ("Fail".equals(p_status)) {
				new RestrictedSubscriberDAO().decreaseRestrictedSubscriberThresholds(p_con, p_c2sTransferVO);
				if (!p_isAlreadyCreditBack && !p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
					UserTransferCountsVO userTransferCountsVO = null;

					userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2sTransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2sTransferVO.getSourceType(), PretupsI.CREDIT,
							PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
					// updation of the date as only date was coming not the time
					userBalancesVO.setLastTransferOn(p_c2sTransferVO.getModifiedOn());
					// update user's daily balances
					int updateCount = new UserBalancesDAO().updateUserDailyBalances(p_con, p_c2sTransferVO.getModifiedOn(), userBalancesVO);
					if (updateCount <= 0) {
						throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}

					updateCount = new UserBalancesDAO().creditUserBalances(p_con, userBalancesVO, p_c2sTransferVO.getCategoryCode());
					if (updateCount <= 0) {
						throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}
		
					userTransferCountsVO = ChannelTransferBL.decreaseC2STransferOutCounts(p_con, p_c2sTransferVO, true, p_date);
					// }

					if (userTransferCountsVO != null) {
						updateCount = (new UserTransferCountsDAO()).updateUserTransferCounts(p_con, userTransferCountsVO, true);
						if (_log.isDebugEnabled()) {
							_log.debug("handleReceiverAmbigousCase", "Exited  updateCount " + updateCount);
						}
						if (updateCount <= 0) {
							throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase", PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
						}
					}

					// By sandeep goel ID RECON001
					// set the credit_back_status for the successful txn
					p_c2sTransferVO.setCreditBackStatus(PretupsI.TXN_STATUS_SUCCESS);
					// ends here
					/*
					 * Update Previous and Post balances of sender in sender
					 * Item,
					 * creating new Items VO for credit back
					 */
					final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
					listSize = listSize + 1;
					c2STransferItemVO.setSNo(listSize);
					c2STransferItemVO.setEntryType(PretupsI.CREDIT);
					c2STransferItemVO.setEntryDate(p_date);
					c2STransferItemVO.setEntryDateTime(p_date);
					c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
					c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
					transList.add(0, c2STransferItemVO);

					// 6.4
					if (p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
						final String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())),prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2sTransferVO
								.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(userBalancesVO
										.getBalance()) };
						p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
								PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_REVERSAL_MSG, messageArgArray));
						userBalancesVO.setOtherInfo("IN C2S RECONCILIATION PROCESS REVERSAL IS MADE SUCCESSFUL AND CHANNEL USER ACCOUNT IS CREDITED BACK");
						userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getTransferValue()));
						p_c2sTransferVO.setOtherInfo1(userBalancesVO);
					} else {
						// By sandeep goel ID RECON001
						// to send SMS to the channel user.
						// "Transaction ID {0} of date {1} for customer {2} of amount {3} is made failed, your account is credited back and your new balance is {4}"
						final String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())),prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2sTransferVO
								.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(userBalancesVO
										.getBalance()) };
						p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
								PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_FAIL_MSG2, messageArgArray));
						// ends here
						// By sandeep goel ID RECON001
						// set other informaiton for the balance logger
						// informaiton.
						userBalancesVO.setOtherInfo("IN C2S RECONCILIATION PROCESS TXN IS MADE FAIL AND CHANNEL USER ACCOUNT IS CREDITED BACK");
						userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getTransferValue()));
						p_c2sTransferVO.setOtherInfo1(userBalancesVO);
					}
				} else {
					// By sandeep goel ID RECON001
					// to send SMS to the channel user.
					// �Transaction ID {0} of date {1} for customer {2} of
					// amount {3} is made fail�
					/*UserTransferCountsVO userTransferCountsVO = null;
					int updateCount=0;
					userTransferCountsVO = ChannelTransferBL.decreaseC2STransferOutCounts(p_con, p_c2sTransferVO, true, p_date);
					// }
					if (userTransferCountsVO != null) {
						updateCount = (new UserTransferCountsDAO()).updateUserTransferCounts(p_con, userTransferCountsVO, true);
						if (_log.isDebugEnabled()) {
							_log.debug("handleReceiverAmbigousCase", "Exited  updateCount " + updateCount);
						}
						if (updateCount <= 0) {
							throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase", PretupsErrorCodesI.C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT);
						}
					}*/
					final String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())), prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2sTransferVO
							.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()) };
					p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
							PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_FAIL_MSG1, messageArgArray));
					// ends here
				}
				// 6.4
				if (p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {

					final UserTransferCountsVO userTransferCountsVO = null;

					userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2sTransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2sTransferVO.getSourceType(), PretupsI.CREDIT,
							PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
					// updation of the date as only date was coming not the time
					userBalancesVO.setLastTransferOn(p_c2sTransferVO.getModifiedOn());
					// update user's daily balances
					int updateCount = new UserBalancesDAO().updateUserDailyBalances(p_con, p_c2sTransferVO.getModifiedOn(), userBalancesVO);
					if (updateCount <= 0) {
						throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}

					// Debit the sender balance
					// updateCount=new
					// UserBalancesDAO().creditUserBalances(p_con,userBalancesVO);
					updateCount = new UserBalancesDAO().creditUserBalances(p_con, userBalancesVO, p_c2sTransferVO.getCategoryCode());
					if (updateCount <= 0) {
						throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}

					p_c2sTransferVO.setCreditBackStatus(PretupsI.TXN_STATUS_SUCCESS);

					final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
					/*listSize = listSize + 1;
                    c2STransferItemVO.setSNo(listSize);
                    c2STransferItemVO.setEntryType(PretupsI.CREDIT);
                    c2STransferItemVO.setEntryDate(p_date);
                    c2STransferItemVO.setEntryDateTime(p_date);
                    c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
                    c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);*/
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
					transList.add(0, c2STransferItemVO);

					/*    final String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate()),prvo!=null ? prvo.getUserSID(): p_c2sTransferVO
                                    .getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(userBalancesVO
                                    .getBalance()) };
                    p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
                                    PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_REVERSAL_MSG, messageArgArray));
                    userBalancesVO.setOtherInfo("IN C2S RECONCILIATION PROCESS REVERSAL IS MADE SUCCESSFUL AND CHANNEL USER ACCOUNT IS CREDITED BACK");
                    userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getTransferValue()));
                    p_c2sTransferVO.setOtherInfo1(userBalancesVO);*/

					// --------------------------------------------------------------------------------

					c2sTransferItemVO = prepareC2STransferItemsVO((C2STransferItemVO) p_transferItemList.get(1), PretupsErrorCodesI.TXN_STATUS_SUCCESS, listSize, p_date,
							PretupsI.CREDIT);

					p_c2sTransferVO.setOldTxnId(p_c2sTransferVO.getReverseTransferID());
					p_c2sTransferVO.setModule(PretupsI.C2S_MODULE);
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
					p_c2sTransferVO.setPostBalance(userBalancesVO.getBalance());
					

					DiffCalBL diffCalBL = new DiffCalBL();
					final ArrayList diffList = diffCalBL.loadDifferentialCalculationsReversal(p_con, p_c2sTransferVO, PretupsI.C2S_MODULE);
					if (diffList != null && diffList.size() > 0) {
						try {
							diffCalBL = new DiffCalBL();
							updateCount = diffCalBL.differentialAdjustmentForReversal(p_con, p_c2sTransferVO, diffList);
							if(updateCount>0)
							{
								int diffListSize = diffList.size();
					          for (int j = 0; j < diffListSize; j++) {
					        	  AdjustmentsVO adjustmentsVO = (AdjustmentsVO) diffList.get(j);
							p_c2sTransferVO.setPostBalance(userBalancesVO.getBalance() - adjustmentsVO.getTransferValue());
					          }
							}
							p_c2sTransferVO.setRequestedAmount(p_c2sTransferVO.getQuantity());
							 if(p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) 
								{						
									ChannelTransferBL.decreaseUserOTFCounts(p_con, p_c2sTransferVO, (ChannelUserVO)p_c2sTransferVO.getSenderVO());
								}
						} catch (Exception e) {
							_log.error(METHOD_NAME, "Exception:" + e.getMessage());
							_log.errorTrace(METHOD_NAME, e);
							try {
								p_con.rollback();
							} catch (Exception ec) {
								_log.error(METHOD_NAME, "Exception:" + ec.getMessage());
								_log.errorTrace(METHOD_NAME, ec);
							}
						} finally {
							if (updateCount > 0) {
								try {
									p_con.commit();
								} catch (Exception e) {
									_log.error(METHOD_NAME, "Exception:" + e.getMessage());
									_log.errorTrace(METHOD_NAME, e);
								}
							} else {
								try {
									p_con.rollback();
								} catch (Exception ec) {
									_log.error(METHOD_NAME, "Exception:" + ec.getMessage());
									_log.errorTrace(METHOD_NAME, ec);
								}
							}
						}
					}
					if(!p_c2sTransferVO.getReceiverNetworkCode().equalsIgnoreCase(p_c2sTransferVO.getNetworkCode()))
					{
						updateCount=0;
						final ArrayList diffPenaltyList= new DiffCalBL().loadDifferentialCalculationsReversalPenalty(p_con, p_c2sTransferVO,  PretupsI.C2S_MODULE);

						if(diffPenaltyList!=null && diffPenaltyList.size()>0)
						{
							try {
								diffCalBL = new DiffCalBL();
								//((AdjustmentsVO) diffPenaltyList.get(0)).setSubService(p_c2sTransferVO.getSelectorCode());
								updateCount = diffCalBL.differentialAdjustmentForReversalPenalty(p_con, p_c2sTransferVO, diffPenaltyList);
							} catch (Exception e) {
								_log.error(METHOD_NAME, "Exception:" + e.getMessage());
								_log.errorTrace(METHOD_NAME, e);
								try {
									p_con.rollback();
								} catch (Exception ec) {
									_log.error(METHOD_NAME, "Exception:" + ec.getMessage());
									_log.errorTrace(METHOD_NAME, ec);
								}
							} finally {
								if (updateCount > 0) {
									try {
										p_con.commit();
									} catch (Exception e) {
										_log.error(METHOD_NAME, "Exception:" + e.getMessage());
										_log.errorTrace(METHOD_NAME, e);
									}
								} else {
									try {
										p_con.rollback();
									} catch (Exception ec) {
										_log.error(METHOD_NAME, "Exception:" + ec.getMessage());
										_log.errorTrace(METHOD_NAME, ec);
									}
								}
							}
						}

					}
					if(p_c2sTransferVO.getRoamPenalty()>0){					 
						String[] messageArgArray1= {PretupsBL.getDisplayAmount(p_c2sTransferVO.getRoamPenalty()),p_c2sTransferVO.getTransferID()};
						p_c2sTransferVO.setSenderRoamReconCreditMessage(BTSLUtil.getMessage((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),PretupsErrorCodesI.USER_ROAM_RCREV_CREDIT_RECON,messageArgArray1));
					}
					if(p_c2sTransferVO.getRoamPenaltyOwner()>0)
					{
						ChannelUserVO ownerUserVO = userDAO.loadUserDetailsFormUserID(p_con, p_c2sTransferVO.getOwnerUserID());
						Locale ownerLocale = new Locale(ownerUserVO.getLanguage(), ownerUserVO.getCountryCode());
						p_c2sTransferVO.setOwnerUserVO(ownerUserVO);
						String[] messageArgArray2= {PretupsBL.getDisplayAmount(p_c2sTransferVO.getRoamPenaltyOwner()),p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderName() };
						p_c2sTransferVO.setSenderOwnerRoamReconCreditMessage(BTSLUtil.getMessage(ownerLocale,PretupsErrorCodesI.USER_OWNER__RCREV_ROAM_CREDIT_RECON,messageArgArray2));
					}

					final String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())),prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2sTransferVO
							.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(p_c2sTransferVO
									.getPostBalance()) };
					p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
							PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_REVERSAL_MSG, messageArgArray));
					userBalancesVO.setOtherInfo("IN C2S RECONCILIATION PROCESS REVERSAL IS MADE SUCCESSFUL AND CHANNEL USER ACCOUNT IS CREDITED BACK");
					userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getTransferValue()));
					p_c2sTransferVO.setOtherInfo1(userBalancesVO);

				} else {
					c2sTransferItemVO = prepareC2STransferItemsVO((C2STransferItemVO) p_transferItemList.get(1), PretupsErrorCodesI.TXN_STATUS_FAIL, listSize, p_date,
							PretupsI.CREDIT);
				}
				transList.add(c2sTransferItemVO);

				if( !p_isAlreadyCreditBack && !p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL))
				{


					if( p_c2sTransferVO.getRoamPenalty()>0){
						UserBalancesVO userBalVO = null;
						((C2STransferItemVO) (p_c2sTransferVO.getTransferItemList().get(0))).setTransferValue(p_c2sTransferVO.getRoamPenalty());
						creditRoamPenalty(p_con,userBalVO,p_c2sTransferVO,false);

						String[] messageArgArray= {PretupsBL.getDisplayAmount(p_c2sTransferVO.getRoamPenalty()),p_c2sTransferVO.getTransferID()};
						p_c2sTransferVO.setSenderRoamReconCreditMessage(BTSLUtil.getMessage((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),PretupsErrorCodesI.USER_ROAM_CREDIT_RECON,messageArgArray));
						String[] messageArgArray1={p_c2sTransferVO.getTransferID(),BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())),prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2sTransferVO.getReceiverMsisdn(),PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(NEW_BAL)};
						p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_FAIL_MSG2,messageArgArray1));
					}
					if(p_c2sTransferVO.getRoamPenaltyOwner()>0){
						ChannelUserVO ownerUserVO = userDAO.loadUserDetailsFormUserID(p_con, p_c2sTransferVO.getOwnerUserID());
						UserBalancesVO userBalOwnerVO = null;
						//  ChannelTransferItemsVO channelTransferItemsOwnerVO= null;
						C2STransferVO c2STransferOwnerVO = new C2STransferVO();
						c2STransferOwnerVO.setOwnerUserVO(ownerUserVO);
						p_c2sTransferVO.setOwnerUserVO(ownerUserVO);
						copyRetailerVOtoOwnerVO(c2STransferOwnerVO, p_c2sTransferVO);
						((C2STransferItemVO) (c2STransferOwnerVO.getTransferItemList().get(0))).setTransferValue(p_c2sTransferVO.getRoamPenaltyOwner());
						creditRoamPenalty(p_con,userBalOwnerVO,c2STransferOwnerVO,true);
						Locale ownerLocale = new Locale(c2STransferOwnerVO.getOwnerUserVO().getLanguage(), c2STransferOwnerVO.getOwnerUserVO().getCountryCode());
						String[] messageArgArray= {PretupsBL.getDisplayAmount(c2STransferOwnerVO.getRoamPenalty()),c2STransferOwnerVO.getTransferID(), p_c2sTransferVO.getSenderName() };
						p_c2sTransferVO.setSenderOwnerRoamReconCreditMessage(BTSLUtil.getMessage(ownerLocale,PretupsErrorCodesI.USER_OWNER_ROAM_CREDIT_RECON,messageArgArray));

					}

				}




			}
			/*
			 * if request if for success then creating the new object for the
			 * receiver's entry.
			 * and check that if user is already credit back then debit the user
			 * balance and update user's
			 * daily balance and also update the thresholds
			 */
			else if (p_status.equals("Success")) {
				if (p_isAlreadyCreditBack && !p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
					userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2sTransferVO, PretupsI.TRANSFER_TYPE_RCH_DEBIT, p_c2sTransferVO.getSourceType(), PretupsI.DEBIT,
							PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
					// updation of the date as only date was coming not the time
					userBalancesVO.setLastTransferOn(p_c2sTransferVO.getModifiedOn());
					_userBalancesDAO.updateUserDailyBalances(p_con, p_c2sTransferVO.getModifiedOn(), userBalancesVO);
					// Debit the sender
					// int
					// updateCount=_userBalancesDAO.debitUserBalances(p_con,userBalancesVO,((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getTransferProfileID(),p_c2sTransferVO.getProductCode(),false);
					final int updateCount = _userBalancesDAO.debitUserBalances(p_con, userBalancesVO, ((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getTransferProfileID(),
							p_c2sTransferVO.getProductCode(), false, ((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getCategoryCode());
					if (updateCount <= 0) {
						throw new BTSLBaseException("ChannelUserBL", "debitUserBalanceForProduct", PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
					}

					/*
					 * Now increase the countes of the channel users if
					 * applicable as DAY,WEEK,MONTH,YEAR changes
					 */
					p_c2sTransferVO.setCreatedOn(p_date);// Done expicilty so
					// that checks in
					// increase counters
					// work
					p_c2sTransferVO.setRequestedAmount(p_c2sTransferVO.getQuantity());// Done
					// expicilty
					// so
					// that
					// checks
					// in
					// increase
					// counters
					// work
					ChannelTransferBL.increaseC2STransferOutCounts(p_con, p_c2sTransferVO, false);




					/*
					 * Update Previous and Post balances of sender in sender
					 * Item,
					 * creating new Items VO for credit back
					 */
					final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
					listSize = listSize + 1;
					c2STransferItemVO.setSNo(listSize);
					c2STransferItemVO.setEntryType(PretupsI.DEBIT);
					c2STransferItemVO.setEntryDate(p_date);
					c2STransferItemVO.setEntryDateTime(p_date);
					c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
					c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
					transList.add(0, c2STransferItemVO);
					// By sandeep goel ID RECON001
					// set other informaiton for the balance logger informaiton.
					userBalancesVO.setOtherInfo("IN C2S RECONCILIATION PROCESS TXN IS MADE SUCCESS AND CHANNEL USER ACCOUNT IS DEBITED");
					userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getTransferValue()));
					p_c2sTransferVO.setOtherInfo1(userBalancesVO);
				}
				if (p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
					userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2sTransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2sTransferVO.getSourceType(), PretupsI.CREDIT,
							PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
					p_c2sTransferVO.setCreatedOn(p_date);// Done expicilty so
					// that checks in
					// increase counters
					// work
					p_c2sTransferVO.setRequestedAmount(p_c2sTransferVO.getQuantity());// Done
					// expicilty
					// so
					// that
					// checks
					// in
					// increase
					// counters
					// work

					// added by satakshi
					userBalancesVO.setLastTransferOn(p_c2sTransferVO.getModifiedOn());
					// ChannelTransferBL.increaseC2STransferOutCounts(p_con,p_c2sTransferVO,false);
					ChannelTransferBL.decreaseC2STransferInCounts(p_con, p_c2sTransferVO, false);


					/*
					 * Update Previous and Post balances of sender in sender
					 * Item,
					 * creating new Items VO for credit back
					 */
					final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
					listSize = listSize + 1;
					c2STransferItemVO.setSNo(listSize);
					c2STransferItemVO.setEntryType(PretupsI.CREDIT);
					c2STransferItemVO.setEntryDate(p_date);
					c2STransferItemVO.setEntryDateTime(p_date);
					c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
					c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
					c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
					transList.add(0, c2STransferItemVO);
					// By sandeep goel ID RECON001
					// set other informaiton for the balance logger informaiton.
					userBalancesVO.setOtherInfo("IN C2S RECONCILIATION PROCESS TXN IS MADE SUCCESS AND CHANNEL USER ACCOUNT IS CREDITED");
					userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getTransferValue()));
					p_c2sTransferVO.setOtherInfo1(userBalancesVO);

				}
				// By sandeep goel ID RECON001
				// check if differential commission is applicable then give it
				// to the channel user.
				// if differential commission is applicable with the service
				// type then the commission would be given
				// to the channel user otherwise no differential commission
				// would be given
				String srvcProdMappingAllowed = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED);
				if (srvcProdMappingAllowed.contains(p_c2sTransferVO.getServiceType())) {
					p_c2sTransferVO.setSelectorCode(p_c2sTransferVO.getSubService());
				} else {
					p_c2sTransferVO.setSelectorCode(PretupsI.DEFAULT_SUBSERVICE);
				}
				final ListValueVO productServiceTypeVO = NetworkProductServiceTypeCache.getProductServiceValueVO(p_c2sTransferVO.getServiceType(), p_c2sTransferVO
						.getSelectorCode());
				if(BTSLUtil.isNullString(p_c2sTransferVO.getPenaltyDetails())){				
					if ("Y".equalsIgnoreCase(productServiceTypeVO.getValue()) && !p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
						p_c2sTransferVO.setDifferentialAllowedForService(productServiceTypeVO.getValue());
						p_c2sTransferVO.setGiveOnlineDifferential(productServiceTypeVO.getType());
						final DiffCalBL diffCalBL = new DiffCalBL();
						try {
							diffCalBL.differentialCalculationsForRecon(p_con, p_c2sTransferVO, PretupsI.C2S_MODULE);
						} catch (BTSLBaseException be) {
							if (!("UNIQUE_CONSTRAINT".equals(be.getMessage()) && PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(p_c2sTransferVO.getTxnStatus()))) {
								throw be;
							}
						}

					}
					
				
				}
				long points;
				// By Brajesh For LMS Ambiguous Case Handling
				final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
				if (!BTSLUtil.isNullString(c2STransferItemVO.getLmsProfile()) && !p_c2sTransferVO.getServiceType()
						.equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
					final Date _currentDate = new Date();
					ArrayList lmsProfileList = null;
					final PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
					lmsProfileList = (ArrayList) LMSProfileCache.getObject(c2STransferItemVO.getLmsProfile(), c2STransferItemVO.getLmsVersion(), c2STransferItemVO
							.getTransferValue());
					if (lmsProfileList == null) {
						LMSProfileCache.loadLMSProfilesDetails(c2STransferItemVO.getLmsProfile(), c2STransferItemVO.getLmsVersion(), c2STransferItemVO.getTransferValue());
						lmsProfileList = (ArrayList) LMSProfileCache.getObject(c2STransferItemVO.getLmsProfile(), c2STransferItemVO.getLmsVersion(), c2STransferItemVO
								.getTransferValue());
					}

					// promotionDetailsVO=
					// Case if transaction amount does not lies as per
					// associated lms profile in case of reconsilation
					if (lmsProfileList != null && lmsProfileList.size() > 0) {
						final ProfileSetDetailsLMSVO profileSetDetailsLMSVO = (ProfileSetDetailsLMSVO) lmsProfileList.get(0);
						if (profileSetDetailsLMSVO.getDetailType().equals(PretupsI.PROFILE_TRANS)) {
							if ((profileSetDetailsLMSVO.getPointsTypeCode()).equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
								final String creditPoints = profileSetDetailsLMSVO.getPointsAsString();
								final double calPoints = Double.parseDouble(PretupsBL
										.getDisplayAmount(c2STransferItemVO.getTransferValue() * Long.parseLong(creditPoints) / 100));
								//points = (long) PretupsBL.Round(calPoints, 0);
								points = BTSLUtil.parseDoubleToLong(PretupsBL.Round(calPoints, 0));
							} else {
								points = Long.parseLong(profileSetDetailsLMSVO.getPointsAsString());
							}
							if (c2STransferItemVO.getTransferDate().compareTo(profileSetDetailsLMSVO.getApplicableTo()) < 0 && c2STransferItemVO.getTransferDate().compareTo(
									profileSetDetailsLMSVO.getApplicableFrom()) > 0) {

								final LoyaltyVO loyaltyVO = new LoyaltyVO();
								Connection con = null;
								MComConnectionI mcomCon = null;
								mcomCon = new MComConnection();
								con=mcomCon.getConnection();
								loyaltyVO.setProductCode(profileSetDetailsLMSVO.getProductCode());
								loyaltyVO.setProfileType("LMS");
								loyaltyVO.setUserid(p_c2sTransferVO.getCreatedBy());
								loyaltyVO.setLoyaltyPoint(String.valueOf(points));
								loyaltyVO.setTotalCrLoyaltyPoint((points));
								loyaltyVO.setBucketCode("1");
								loyaltyVO.setPointsDate(_currentDate);
								loyaltyVO.setCreatedOn(_currentDate);
								loyaltyVO.setTransferId(p_c2sTransferVO.getTransferID());
								loyaltyVO.setVersion(c2STransferItemVO.getLmsVersion());
								loyaltyVO.setSetId(c2STransferItemVO.getLmsProfile());
								loyaltyVO.setNetworkCode(p_c2sTransferVO.getSenderNetworkCode());
								loyaltyVO.setCreatedBy(p_c2sTransferVO.getCreatedBy());

								PretupsBL.generateLMSTransferID(loyaltyVO);
								loyaltyVO.setTransferId(loyaltyVO.getLmstxnid());

								final int creditSuccess = loyaltytxnDAO.creditLoyaltyPoint(con, loyaltyVO);

								if (creditSuccess > 0) {
									if (con != null) {
										mcomCon.finalCommit();
									}
									if(mcomCon != null)
									{
										mcomCon.close("ChannelUserBL#handleReceiverAmbigousCase");
										mcomCon=null;
										}
								} else {
									throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase", PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
								}

							}
						}
					}
				}
				// following message would be send if differential commission is
				// not applicable or differential
				// commission is 0
				DiffCalBL penCalBL= new DiffCalBL();
				UserBalancesVO userBalVO = null;
				UserBalancesVO userBalOwnerVO = null;
				if( !p_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL))
				{
					if(  p_c2sTransferVO.getRoamPenalty()>0){
						AdjustmentsVO penaltyVODebit= new AdjustmentsVO();
						AdjustmentsVO penaltyVOCredit= new AdjustmentsVO();

						// ChannelTransferItemsVO channelTransferItemsVO= null;
						if(p_isAlreadyCreditBack ){
							penCalBL.calculateRoamPenaltyReconciliation(p_c2sTransferVO, penaltyVODebit, penaltyVOCredit, false);
							((C2STransferItemVO) (p_c2sTransferVO.getTransferItemList().get(0))).setTransferValue(p_c2sTransferVO.getRoamPenalty());
							userBalVO = debitUserBalanceForProduct(p_con, p_c2sTransferVO.getTransferID(), p_c2sTransferVO);
							penaltyVOCredit.setPreviousBalance(userBalVO.getPreviousBalance());
							penaltyVOCredit.setPostBalance(userBalVO.getBalance());

						}else{
							penCalBL.calculateRoamPenaltyReconciliation(p_c2sTransferVO, penaltyVODebit, penaltyVOCredit, false);
							penaltyVOCredit.setPreviousBalance(0);
							penaltyVOCredit.setPostBalance(0);
						}


						penCalBL.insertRoamPenaltyAdjustments(p_con, penaltyVODebit, penaltyVOCredit);
						p_c2sTransferVO.setDifferentialGiven(PretupsI.YES);
						p_c2sTransferVO.setDifferentialApplicable(PretupsI.YES);
						String[] messageArgArray= {PretupsBL.getDisplayAmount(p_c2sTransferVO.getRoamPenalty()),p_c2sTransferVO.getTransferID()};
						p_c2sTransferVO.setSenderRoamReconDebitMessage(BTSLUtil.getMessage((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),PretupsErrorCodesI.USER_ROAM_DEBIT_RECON,messageArgArray));

					}
					if( p_c2sTransferVO.getRoamPenaltyOwner()>0){
						ChannelUserVO ownerUserVO = userDAO.loadUserDetailsFormUserID(p_con, p_c2sTransferVO.getOwnerUserID());

						//  ChannelTransferItemsVO channelTransferItemsOwnerVO= null;
						AdjustmentsVO penaltyVODebitOwner = new AdjustmentsVO();
						AdjustmentsVO penaltyVOCreditOwner = new AdjustmentsVO();
						C2STransferVO c2STransferOwnerVO = new C2STransferVO();
						c2STransferOwnerVO.setOwnerUserVO(ownerUserVO);
						p_c2sTransferVO.setOwnerUserVO(ownerUserVO);
						copyRetailerVOtoOwnerVO(c2STransferOwnerVO, p_c2sTransferVO);
						if(p_isAlreadyCreditBack ){
							penCalBL.calculateRoamPenaltyReconciliation(c2STransferOwnerVO, penaltyVODebitOwner, penaltyVOCreditOwner, true);
							userBalOwnerVO = debitUserBalanceForProductRoamOwner(p_con, c2STransferOwnerVO.getTransferID(), c2STransferOwnerVO);
							penaltyVOCreditOwner.setPreviousBalance(userBalOwnerVO.getPreviousBalance());
							penaltyVOCreditOwner.setPostBalance(userBalOwnerVO.getBalance());

						}else{
							penCalBL.calculateRoamPenaltyReconciliation(c2STransferOwnerVO, penaltyVODebitOwner, penaltyVOCreditOwner, true);
							penaltyVOCreditOwner.setPreviousBalance(0);
							penaltyVOCreditOwner.setPostBalance(0);
						}

						penCalBL.insertRoamPenaltyAdjustments(p_con, penaltyVODebitOwner, penaltyVOCreditOwner);
						Locale ownerLocale = new Locale(c2STransferOwnerVO.getOwnerUserVO().getLanguage(), c2STransferOwnerVO.getOwnerUserVO().getCountryCode());
						String[] messageArgArray= {PretupsBL.getDisplayAmount(c2STransferOwnerVO.getRoamPenalty()),c2STransferOwnerVO.getTransferID(), p_c2sTransferVO.getSenderName()};
						p_c2sTransferVO.setSenderOwnerRoamReconDebitMessage(BTSLUtil.getMessage(ownerLocale,PretupsErrorCodesI.USER_OWNER_ROAM_DEBIT_RECON,messageArgArray));
					}
				}
				if (!"Y".equalsIgnoreCase(productServiceTypeVO.getValue()) || BTSLUtil.isNullString(p_c2sTransferVO.getSenderReturnMessage())) {
					if (p_isAlreadyCreditBack) {

						// By sandeep goel ID RECON001
						// to send SMS to the channel user.
						// "Transaction ID {0} of date {1} for customer {2} of amount {3} is made successful, and your new balance is {4}"
						final String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())),prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2sTransferVO
								.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(userBalancesVO
										.getBalance()) };
						p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
								PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_MSG2, messageArgArray));
						if(userBalVO!=null){
							String[] messageArgArray1={p_c2sTransferVO.getTransferID(),BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())),prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2sTransferVO.getReceiverMsisdn(),PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(userBalVO.getBalance())};
							p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_MSG2,messageArgArray1));
						}

						// ends here
					} else {
						final String[] messageArgArray = { p_c2sTransferVO.getTransferID(), BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_c2sTransferVO.getTransferDate())),prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()):  p_c2sTransferVO
								.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getTransferValue()) };

						// 6.4
						if (p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
							p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
									PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_FAIL_REVERSAL_MSG, messageArgArray));
			
						} else {
							p_c2sTransferVO.setSenderReturnMessage(BTSLUtil.getMessage((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),
									PretupsErrorCodesI.RECON_C2S_ADJUSTMENT_SUCCESS_MSG1, messageArgArray));
							// ends here
						}
					}
				}
				// ends here
				// 6.4
				if (p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
					c2sTransferItemVO = prepareC2STransferItemsVO((C2STransferItemVO) p_transferItemList.get(1), PretupsErrorCodesI.TXN_STATUS_FAIL, listSize, p_date,
							PretupsI.CREDIT);
				} else {
					c2sTransferItemVO = prepareC2STransferItemsVO((C2STransferItemVO) p_transferItemList.get(1), PretupsErrorCodesI.TXN_STATUS_SUCCESS, listSize, p_date,
							PretupsI.CREDIT);
				}

				transList.add(c2sTransferItemVO);
			}
		} catch (BTSLBaseException be) {
			_log.error("handleReceiverAmbigousCase", "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("handleReceiverAmbigousCase", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[handleReceiverAmbigousCase]",
					p_c2sTransferVO.getTransferID(), "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("handleReceiverAmbigousCase", " Exiting transList=" + transList);
		}
		return transList;
	}

	/**
	 * Method to prepare the VO that needs to be inserted in database
	 * 
	 * @param p_c2sTransferItemVO
	 *            C2STransferItemVO
	 * @param p_status
	 * @param p_size
	 * @param p_date
	 * @param p_entryType
	 * @return TransferItemVO
	 */
	private static C2STransferItemVO prepareC2STransferItemsVO(C2STransferItemVO p_c2sTransferItemVO, String p_status, int p_size, Date p_date, String p_entryType) {
		final C2STransferItemVO c2sTransferItemVO = new C2STransferItemVO();
		c2sTransferItemVO.setTransferID(p_c2sTransferItemVO.getTransferID());
		c2sTransferItemVO.setMsisdn(p_c2sTransferItemVO.getMsisdn());
		c2sTransferItemVO.setEntryDate(p_date);
		c2sTransferItemVO.setRequestValue(p_c2sTransferItemVO.getRequestValue());
		c2sTransferItemVO.setUserType(p_c2sTransferItemVO.getUserType());
		c2sTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
		c2sTransferItemVO.setEntryType(p_entryType);
		c2sTransferItemVO.setTransferValue(p_c2sTransferItemVO.getTransferValue());
		c2sTransferItemVO.setSubscriberType(p_c2sTransferItemVO.getSubscriberType());
		c2sTransferItemVO.setServiceClassCode(p_c2sTransferItemVO.getServiceClassCode());
		c2sTransferItemVO.setTransferStatus(p_status);
		c2sTransferItemVO.setTransferDate(p_c2sTransferItemVO.getTransferDate());
		c2sTransferItemVO.setTransferDateTime(p_c2sTransferItemVO.getTransferDateTime());
		c2sTransferItemVO.setEntryDateTime(p_date);
		c2sTransferItemVO.setSNo(p_size + 1);
		c2sTransferItemVO.setPrefixID(p_c2sTransferItemVO.getPrefixID());
		c2sTransferItemVO.setServiceClass(p_c2sTransferItemVO.getServiceClass());

		c2sTransferItemVO.setPreviousBalance(p_c2sTransferItemVO.getPreviousBalance());
		c2sTransferItemVO.setPostBalance(p_c2sTransferItemVO.getPostBalance());
		c2sTransferItemVO.setValidationStatus(p_c2sTransferItemVO.getValidationStatus());
		c2sTransferItemVO.setUpdateStatus(p_c2sTransferItemVO.getUpdateStatus());
		c2sTransferItemVO.setUpdateStatus1(p_c2sTransferItemVO.getUpdateStatus1());
		c2sTransferItemVO.setUpdateStatus2(p_c2sTransferItemVO.getUpdateStatus2());
		c2sTransferItemVO.setInterfaceType(p_c2sTransferItemVO.getInterfaceType());
		c2sTransferItemVO.setInterfaceID(p_c2sTransferItemVO.getInterfaceID());
		c2sTransferItemVO.setInterfaceReferenceID(p_c2sTransferItemVO.getInterfaceReferenceID());
		c2sTransferItemVO.setInterfaceResponseCode(p_c2sTransferItemVO.getInterfaceResponseCode());
		c2sTransferItemVO.setPreviousExpiry(p_c2sTransferItemVO.getPreviousExpiry());
		c2sTransferItemVO.setNewExpiry(p_c2sTransferItemVO.getNewExpiry());
		c2sTransferItemVO.setAccountStatus(p_c2sTransferItemVO.getAccountStatus());
		c2sTransferItemVO.setProtocolStatus(p_c2sTransferItemVO.getProtocolStatus());
		return c2sTransferItemVO;
	}

	/**
	 * Method loadUserListForXfr
	 * This method is to load the users list for c2c transactions on the basis
	 * of various controlling values
	 * associated with the transfer rule.
	 * 
	 * @param p_con
	 * @param p_txnType
	 * @param p_channelTransferRuleVO
	 * @param p_toCategoryCode
	 * @param p_userName
	 * @param p_channelUserVO
	 * @return ArrayList
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	private static ArrayList loadUserListForXfr(Connection p_con, String p_txnType, ChannelTransferRuleVO p_channelTransferRuleVO, String p_toCategoryCode, String p_userName, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("loadUserListForXfr",
					"Entered p_txnType=" + p_txnType + ", ToCategoryCode: " + p_toCategoryCode + " User Name: " + p_userName + ",p_channelTransferRuleVO=" + p_channelTransferRuleVO + ",p_channelUserVO=" + p_channelUserVO);
		}
		ArrayList arrayList = new ArrayList();
		boolean uncontrollAllowed = false;
		boolean fixedLevelParent = false;
		boolean fixedLevelHierarchy = false;
		String fixedCatStr = null;
		boolean directAllowed = false;
		boolean chnlByPassAllowed = false;
		String unctrlLevel = null;
		String ctrlLevel = null;
		// added for user level transfer rule
		boolean isUserLevelTrfRuleAllow = false;
		String userLevelTrfRuleCode = null;
		// if txn is for transfer then get the value of the transfer paramenters
		final String METHOD_NAME = "loadUserListForXfr";
		if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
			isUserLevelTrfRuleAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, p_channelUserVO.getNetworkID(), p_channelUserVO
					.getCategoryCode())).booleanValue();
			userLevelTrfRuleCode = p_channelUserVO.getTrannferRuleTypeId();
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedTransferLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedTransferLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getDirectTransferAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getTransferChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlTransferAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlTransferLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlTransferLevel();
		}
		// else if txn is for return then get the value of the return
		// paramenters
		else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedReturnLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedReturnCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedReturnLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedReturnCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getReturnAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getReturnChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlReturnAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlReturnLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlReturnLevel();
		}
		// else if txn is for withdraw then get the value of the withdraw
		// paramenters
		else // if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnType))
		{
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedWithdrawLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedWithdrawLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlWithdrawAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlWithdrawLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlWithdrawLevel();
		}

		// /
		// to load the user list we will have to apply the check of the fixed
		// level and fixed category in each
		// and every case.
		// Now we divide the whole conditions in various sub conditions as
		//
		// /
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		// added for user level transfer rule
		if (isUserLevelTrfRuleAllow && !BTSLUtil.isNullString(userLevelTrfRuleCode)) {
			OperatorUtilI operatorUtili = null;
			try {
				final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
				operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[loadUserListForXfr]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}
			arrayList = operatorUtili.loadUserListForTrfRuleTypeByUserLevel(p_con, p_channelUserVO, p_toCategoryCode, p_txnType, p_userName);
		} else {
			if (uncontrollAllowed) {
				if (PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(unctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(unctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN
						.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system without any check
						// of the fixed category
						arrayList = channelUserDAO.loadUsersOutsideHireacrhy(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_userName, p_channelUserVO.getUserID(),
								p_txnType);
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system, which are in the
						// hierarchy of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system, which are in the
						// direct child of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// uncontrol domain check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// sender'owner hierarchy
						// without any check of the fixed category
						arrayList = channelUserDAO.loadUsersByOwnerID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName,
								p_channelUserVO.getUserID(), p_txnType);
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the
						// sender'owner hierarchy
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is OWNER
						// pass value 2 for this parameter and OWNERID for the
						// p_parentUserID since here list is to be
						// loaded by owner.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the
						// sender'owner hierarchy
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is OWNER
						// pass value 2 for this parameter and OWNERID for the
						// p_parentUserID since here list is to be
						// loaded by owner.

						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// owner level uncontroll check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// sender's parent hierarchy
						// without any check of the fixed category
						arrayList = channelUserDAO.loadUsersByParentIDRecursive(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_userName, p_channelUserVO.getUserID(), p_txnType);
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the
						// sender's parent hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is PARENT
						// pass value 1 for this parameter and PARENTID for the
						// p_parentUserID since here list is to be
						// loaded by parent.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the
						// sender's parent hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is PARENT
						// pass value 1 for this parameter and PARENTID for the
						// p_parentUserID since here list is to be
						// loaded by parent.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// parent level uncontroll check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the sender
						// hierarchy
						// without any check of the fixed category so here
						// sender's userID is passed in the calling
						// method as the parentID to load all the users under
						// sender recursively
						arrayList = channelUserDAO.loadUsersByParentIDRecursive(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_userName, p_channelUserVO.getUserID(), p_txnType);
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the sender
						// hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is SELF but sender user
						// have to be considered as the parent of all the
						// requested users so
						// pass value 1 for this parameter and sener's userID
						// for the p_parentUserID since here list is to be
						// loaded by senderID.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the sender
						// hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is SELF but sender user
						// have to be considered as the parent of all the
						// requested users so
						// pass value 1 for this parameter and sener's userID
						// for the p_parentUserID since here list is to be
						// loaded by senderID.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// Self level uncontroll check
			}// uncontrol transfer allowed check
			else {
				if (PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(ctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(ctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN
						.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// receiver domain for the direct child of the owner
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system
							// which are direct child of the owner
							// Sandeep goel ID USD001
							// method is changed to remove the problem as login
							// user is also coming in the list

							arrayList = channelUserDAO.loadUsersByDomainID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO.getToDomainCode(),
									p_userName, p_channelUserVO.getUserID(), p_txnType);
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system
							// which are not direct child of the owner
							// Sandeep goel ID USD001
							// method is changed to remove the problem as login
							// user is also coming in the list
							arrayList.addAll(channelUserDAO.loadUsersChnlBypassByDomainID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO
									.getToDomainCode(), p_userName, p_channelUserVO.getUserID(), p_txnType));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the sender
						// domain,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the sender
						// domain,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// domain level control check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// sender'owner hierarchy
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system within the
							// sender'owner hierarchy
							// which are direct child of the owner so here in
							// this method calling we are sending sender's
							// ownerID to considered as the parentID in the
							// method
							arrayList = channelUserDAO.loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName,
									p_channelUserVO.getUserID(), p_txnType);
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system within the
							// sender'owner hierarchy
							// which are not direct child of the owner so here
							// in this method calling we are sending sender's
							// ownerID to considered as the parentID in the
							// method
							arrayList.addAll(channelUserDAO.loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
									p_userName, p_channelUserVO.getUserID(), p_txnType));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the
						// sender's owner hierarchy
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is OWNER
						// pass value 2 for this parameter and OWNERID for the
						// p_parentUserID since here list is to be
						// loaded by owner.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the
						// sender's owner hierarchy
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is OWNER
						// pass value 2 for this parameter and OWNERID for the
						// p_parentUserID since here list is to be
						// loaded by owner.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// owner level control check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// sender's parent hierarchy
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system within the
							// sender's parent hierarchy
							// which are direct child of the parent
							arrayList = channelUserDAO.loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_userName,
									p_channelUserVO.getUserID(), p_txnType);
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system within the
							// sender's parent hierarchy
							// which are not direct child of the parent
							arrayList.addAll(channelUserDAO.loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
									p_userName, p_channelUserVO.getUserID(), p_txnType));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the
						// sender's parent hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is PARENT
						// pass value 1 for this parameter and PARENTID for the
						// p_parentUserID since here list is to be
						// loaded by parent.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the
						// sender's parent hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is PARENT
						// pass value 1 for this parameter and PARENTID for the
						// p_parentUserID since here list is to be
						// loaded by parent.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// parent level control check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the sender
						// hierarchy
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system within the
							// sender's hierarchy
							// which are direct child of the sender so here in
							// this method calling we are sending sender's
							// userID to considered as the parentID in the
							// method
							arrayList = channelUserDAO.loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_userName,
									p_channelUserVO.getUserID(), p_txnType);
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system within the
							// sender's hierarchy
							// which are not direct child of the sender so here
							// in this method calling we are sending sender's
							// userID to considered as the parentID in the
							// method
							arrayList.addAll(channelUserDAO.loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
									p_userName, p_channelUserVO.getUserID(), p_txnType));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the sender
						// hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is SELF but sender user
						// have to be considered as the parent of all the
						// requested users so
						// pass value 1 for this parameter and sener's userID
						// for the p_parentUserID since here list is to be
						// loaded by senderID.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the sender
						// hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is SELF but sender user
						// have to be considered as the parent of all the
						// requested users so
						// pass value 1 for this parameter and sener's userID
						// for the p_parentUserID since here list is to be
						// loaded by senderID.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_userName, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// Self level control check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_GEOGRAPHY.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// receiver domain for the direct child of the owner
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system
							// which are direct child of the owner
							// Sandeep goel ID USD001
							// method is changed to remove the problem as login
							// user is also coming in the list

							arrayList = channelUserDAO.loadUsersByGeo(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO.getToDomainCode(),
									p_userName, p_channelUserVO.getUserID());
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system
							// which are not direct child of the owner
							// Sandeep goel ID USD001
							// method is changed to remove the problem as login
							// user is also coming in the list
							arrayList.addAll(channelUserDAO.loadUsersChnlBypassByGeo(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO
									.getToDomainCode(), p_userName, p_channelUserVO.getUserID()));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the sender
						// domain,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the sender
						// domain,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// Geography level control check
			}// control transaction check
		}
		if (_log.isDebugEnabled()) {
			_log.debug("loadUserListForXfr", "Exited userList.size() = " + arrayList.size());
		}
		return arrayList;
	}

	/**
	 * Method validateUserForXfr
	 * This method validate the receiver and give the information that
	 * transaction will be considered as CONTROL
	 * or UNCONTROL transaction.
	 * 
	 * @param p_con
	 * @param p_txnSubType
	 *            String
	 * @param p_channelTransferRuleVO
	 * @param p_senderVO
	 *            ChannelUserVO
	 * @param p_receiverVO
	 *            ChannelUserVO
	 * @param p_isUserCode
	 *            boolean
	 * @param p_forwardPath
	 *            String
	 * @param p_isFromWeb
	 *            boolean
	 * @return boolean
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public static boolean validateUserForXfr(Connection p_con, String p_txnSubType, ChannelTransferRuleVO p_channelTransferRuleVO, ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO, boolean p_isUserCode, String p_forwardPath, boolean p_isFromWeb) throws BTSLBaseException {
		final String METHOD_NAME = "validateUserForXfr";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,
					"Entered p_txnSubType=" + p_txnSubType + ", p_channelTransferRuleVO=" + p_channelTransferRuleVO + ",p_senderVO " + p_senderVO + " p_receiverVO " + p_receiverVO + " IsUserCode: " + p_isUserCode + " forwardPath: " + p_forwardPath + " p_isFromWeb: " + p_isFromWeb);
		}
		boolean isOutsideHierarchy = false;
		final String networkCode = p_senderVO.getNetworkID();
		String senderUserID = p_senderVO.getUserID();
		String senderParentID = p_senderVO.getParentID();
		String senderOwnerID = p_senderVO.getOwnerID();
		String receiverUserCode = p_receiverVO.getUserCode();
		String receiverCategoryCode = p_receiverVO.getCategoryCode();

		boolean fixedLevelParent = false;
		boolean fixedLevelHierarchy = false;
		String fixedCatStr = null;
		boolean directAllowed = false;
		boolean chnlByPassAllowed = false;
		String unctrlLevel = null;
		String ctrlLevel = null;
		boolean uncontrollAllowed = false;
		// Added for User level Transfer Rule Type
		boolean isUserLevelTrfRuleAllow = false;
		String userLevelTrfRuleCode = null;
		boolean isValidUserForXfr = false;
		// if txn is for transfer then loads all the parameters for the transfer
		// only

		if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnSubType)|| PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_txnSubType)
				) {
			isUserLevelTrfRuleAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, p_senderVO.getNetworkID(), p_senderVO
					.getCategoryCode())).booleanValue();
			userLevelTrfRuleCode = p_senderVO.getTrannferRuleTypeId();

			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedTransferLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedTransferLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getDirectTransferAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getTransferChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlTransferAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlTransferLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlTransferLevel();
		}
		// else if txn is for return then loads all the parameters for the
		// return only
		else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnSubType)) {
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedReturnLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedReturnCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedReturnLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedReturnCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getReturnAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getReturnChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlReturnAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlReturnLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlReturnLevel();
		}
		// else if txn is for withdraw then loads all the parameters for the
		// withdraw only
		else // if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType))
		{
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedWithdrawLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedWithdrawLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlWithdrawAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlWithdrawLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlWithdrawLevel();

			// In case of withdraw we swap all the information of sender and
			// receiver in the action class
			// but here we have to again swap some information to verify the
			// sender/receiver.
			senderUserID = p_receiverVO.getUserID();
			senderParentID = p_receiverVO.getParentID();
			senderOwnerID = p_receiverVO.getOwnerID();
			receiverUserCode = p_senderVO.getUserCode();
			receiverCategoryCode = p_senderVO.getCategoryCode();
		}

		// /
		// to validate the user form the user list we will have to apply the
		// check of the fixed level
		// and fixed category in each and every case.
		// Now we divide the whole conditions in various sub conditions.
		// Here first we check existance of the user in the controlling level if
		// user exist then the TXN will be
		// considered as controlled if user not exist then we will check that
		// "is uncontroll allow?" if yes
		// then existance of the user will be check as uncontroll levels and
		// then TXN will be considered as uncontrolld
		// /
		boolean checkAgain = true;
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		// added for user level transfer rule
		if (isUserLevelTrfRuleAllow && !BTSLUtil.isNullString(userLevelTrfRuleCode)) {
			OperatorUtilI operatorUtili = null;
			try {
				final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
				operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[loadUserListForXfr]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}
			isValidUserForXfr = operatorUtili.validateUserForTrfRuleTypeByUserLevel(p_con, p_senderVO, p_receiverVO, p_isFromWeb);
			if (!isValidUserForXfr) {
				if (!p_isFromWeb) {
					final String args[] = { receiverUserCode };
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOT_ALLOWED, args);
				}
				throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, "message.channeltransfer.transferrulenotdefine", p_forwardPath);

			}
		}

		else {
			if (PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(ctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(ctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN
					.equals(ctrlLevel)) {
				if (BTSLUtil.isNullString(fixedCatStr)) {
					// validate user form the system
					// without any check of the fixed category
					if (directAllowed) {
						// validate user form the system
						// which are direct child of the owner
						if (checkAgain && channelUserDAO.isUserExistsByDomainID(p_con, networkCode, receiverCategoryCode, p_channelTransferRuleVO.getToDomainCode(),
								receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// direct transfer check
					if (checkAgain && chnlByPassAllowed) {
						// validate user form the system which are not direct
						// child of the owner
						if (channelUserDAO.isUsersExistChnlBypassByDomainID(p_con, networkCode, receiverCategoryCode, p_channelTransferRuleVO.getToDomainCode(),
								receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// channel by pass check
				}// fixed category null check
				else if (fixedLevelHierarchy) {
					// validate user form the system within the sender domain,
					// which are in the hierarchy of the users of fixedCatStr
					// categories
					// p_ctrlLvl (last parameter) here if value of this
					// parameter is 1 then check will be done
					// by parentID, if value of this parameter is 2 then check
					// will be done by ownerID
					// other wise no check will be required. So here as controll
					// level is DOMAIN OR DOMAINTYPE
					// pass value 0 for this parameter and null for the
					// p_parentUserID since here no parent and
					// no owner exist for the DOMAIN OR DOMAINTYPE level.
					if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, null, receiverUserCode, fixedCatStr, 0,
							p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level hierarchy check
				else if (fixedLevelParent) {
					// validate user form the system within the sender domain,
					// which are direct child of the users of fixedCatStr
					// categories
					// p_ctrlLvl (last parameter) here if value of this
					// parameter is 1 then check will be done
					// by parentID, if value of this parameter is 2 then check
					// will be done by ownerID
					// other wise no check will be required. So here as controll
					// level is DOMAIN OR DOMAINTYPE
					// pass value 0 for this parameter and null for the
					// p_parentUserID since here no parent and
					// no owner exist for the DOMAIN OR DOMAINTYPE level.
					if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, null, receiverUserCode, fixedCatStr, 0,
							p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level parent check
			}// domain level control check
			else if (PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(ctrlLevel)) {
				if (BTSLUtil.isNullString(fixedCatStr)) {
					// validate user form the system within the sender'owner
					// hierarchy
					// without any check of the fixed category
					if (directAllowed) {
						// validate user form the system within the sender'owner
						// hierarchy
						// which are direct child of the owner
						if (checkAgain && channelUserDAO.isUserExistByParentID(p_con, networkCode, receiverCategoryCode, senderOwnerID, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// direct transfer check
					if (checkAgain && chnlByPassAllowed) {
						// validate user form the system within the sender'owner
						// hierarchy
						// which are not direct child of the owner
						if (channelUserDAO.isUserExistForChannelByPass(p_con, networkCode, receiverCategoryCode, senderOwnerID, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// channel by pass check
				}// fixed category null check
				else if (fixedLevelHierarchy) {
					// validate user form the system within the sender's owner
					// hierarchy
					// which are in the hierarchy of the users of fixedCatStr
					// categories
					if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, senderOwnerID, receiverUserCode, fixedCatStr,
							2, p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level hierarchy check
				else if (fixedLevelParent) {
					// validate user form the system within the sender's owner
					// hierarchy
					// which are child of the users of fixedCatStr categories
					if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, senderOwnerID, receiverUserCode, fixedCatStr, 2,
							p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level parent check
			}// owner level control check
			else if (PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(ctrlLevel)) {
				if (BTSLUtil.isNullString(fixedCatStr)) {
					// validate user form the system within the sender's parent
					// hierarchy
					// without any check of the fixed category
					if (directAllowed) {
						// validate user from the system within the sender's
						// parent hierarchy
						// which are direct child of the owner
						if (checkAgain && channelUserDAO.isUserExistByParentID(p_con, networkCode, receiverCategoryCode, senderParentID, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}

					}// direct transfer check
					if (checkAgain && chnlByPassAllowed) {
						// validate user form the system within the sender's
						// parent hierarchy
						// which are not direct child of the owner
						if (channelUserDAO.isUserExistForChannelByPass(p_con, networkCode, receiverCategoryCode, senderParentID, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// channel by pass check
				}// fixed category null check
				else if (fixedLevelHierarchy) {
					// validate user form the system within the sender's parent
					// hierarchy,
					// which are in the hierarchy of the users of fixedCatStr
					// categories
					if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, senderParentID, receiverUserCode, fixedCatStr,
							1, p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level hierarchy check
				else if (fixedLevelParent) {
					// validate user form the system within the sender's parent
					// hierarchy,
					// which are in the direct child of the users of fixedCatStr
					// categories
					if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, senderParentID, receiverUserCode, fixedCatStr, 1,
							p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level parent check
			}// parent level control check
			else if (PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(ctrlLevel)) {
				if (BTSLUtil.isNullString(fixedCatStr)) {
					// validate user form the system within the sender hierarchy
					// without any check of the fixed category
					if (directAllowed) {
						// validate user form the system within the sender's
						// hierarchy
						// which are direct child of the sender
						if (checkAgain && channelUserDAO.isUserExistByParentID(p_con, networkCode, receiverCategoryCode, senderUserID, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}

					}// direct transfer check
					if (checkAgain && chnlByPassAllowed) {
						// validate user form the system within the sender's
						// hierarchy
						// which are not direct child of the sender
						if (channelUserDAO.isUserExistForChannelByPass(p_con, networkCode, receiverCategoryCode, senderUserID, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// channel by pass check
				}// fixed category null check
				else if (fixedLevelHierarchy) {
					// validate user form the system within the sender
					// hierarchy,
					// which are in the hierarchy of the users of fixedCatStr
					// categories
					if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, senderUserID, receiverUserCode, fixedCatStr, 1,
							p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level hierarchy check
				else if (fixedLevelParent) {
					// validate user form the system within the sender
					// hierarchy,
					// which are in the direct child of the users of fixedCatStr
					// categories
					if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, senderUserID, receiverUserCode, fixedCatStr, 1,
							p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level parent check
			}// Self level control check

			else if (PretupsI.CHANNEL_TRANSFER_LEVEL_GEOGRAPHY.equals(ctrlLevel)) {
				if (BTSLUtil.isNullString(fixedCatStr)) {
					// validate user form the system
					// without any check of the fixed category
					if (directAllowed) {
						// validate user form the system
						// which are direct child of the owner
						if (checkAgain && channelUserDAO.isUserExistsByGeo(p_con, networkCode, receiverCategoryCode, p_channelTransferRuleVO.getToDomainCode(),
								receiverUserCode, senderUserID)) {
							checkAgain = false;
						}
					}// direct transfer check
					if (checkAgain && chnlByPassAllowed) {
						// validate user form the system which are not direct
						// child of the owner
						// if(channelUserDAO.isUsersExistChnlBypassByDomainID(p_con,networkCode,receiverCategoryCode,p_channelTransferRuleVO.getToDomainCode(),receiverUserCode))
						if (channelUserDAO.isUserExistsChnlByPassByGeo(p_con, networkCode, receiverCategoryCode, p_channelTransferRuleVO.getToDomainCode(), receiverUserCode,
								senderUserID)) {
							checkAgain = false;
						}
					}// channel by pass check
				}// fixed category null check
				else if (fixedLevelHierarchy) {
					// validate user form the system within the sender domain,
					// which are in the hierarchy of the users of fixedCatStr
					// categories
					// p_ctrlLvl (last parameter) here if value of this
					// parameter is 1 then check will be done
					// by parentID, if value of this parameter is 2 then check
					// will be done by ownerID
					// other wise no check will be required. So here as controll
					// level is DOMAIN OR DOMAINTYPE
					// pass value 0 for this parameter and null for the
					// p_parentUserID since here no parent and
					// no owner exist for the DOMAIN OR DOMAINTYPE level.
					if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, null, receiverUserCode, fixedCatStr, 0,
							p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level hierarchy check
				else if (fixedLevelParent) {
					// validate user form the system within the sender domain,
					// which are direct child of the users of fixedCatStr
					// categories
					// p_ctrlLvl (last parameter) here if value of this
					// parameter is 1 then check will be done
					// by parentID, if value of this parameter is 2 then check
					// will be done by ownerID
					// other wise no check will be required. So here as controll
					// level is DOMAIN OR DOMAINTYPE
					// pass value 0 for this parameter and null for the
					// p_parentUserID since here no parent and
					// no owner exist for the DOMAIN OR DOMAINTYPE level.
					if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, null, receiverUserCode, fixedCatStr, 0,
							p_txnSubType)) {
						checkAgain = false;
					}
				}// fixed level parent check
			}// geography level control check

			if (checkAgain && uncontrollAllowed) {
				isOutsideHierarchy = true;
				if (PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(unctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(unctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN
						.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// validate user form the system without any check of
						// the fixed category
						if (checkAgain && channelUserDAO.isUserExist(p_con, networkCode, receiverCategoryCode, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// validate user form the system, which are in the
						// hierarchy of the users of
						// fixedCatStr categories
						if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, null, receiverUserCode, fixedCatStr, 0,
								p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// validate user form the system, which are in the
						// direct child of the users of
						// fixedCatStr categories
						if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, null, receiverUserCode, fixedCatStr, 0,
								p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed level parent check
				}// uncontrol domain check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// validate user form the system within the sender'owner
						// hierarchy
						// without any check of the fixed category
						if (checkAgain && channelUserDAO.isUserExistByOwnerID(p_con, networkCode, receiverCategoryCode, senderOwnerID, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// validate user form the system within the sender'owner
						// hierarchy
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, senderOwnerID, receiverUserCode,
								fixedCatStr, 2, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// validate user form the system within the sender'owner
						// hierarchy
						// which are in the direct child of the users of
						// fixedCatStr categories
						if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, senderOwnerID, receiverUserCode, fixedCatStr,
								2, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed level parent check
				}// owner level uncontroll check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// validate user form the system within the sender's
						// parent hierarchy
						// without any check of the fixed category
						if (checkAgain && channelUserDAO.isUserExistByParentIDRecursive(p_con, networkCode, receiverCategoryCode, senderParentID, receiverUserCode,
								p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// validate user form the system within the sender's
						// parent hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, senderParentID, receiverUserCode,
								fixedCatStr, 1, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// validate user form the system within the sender's
						// parent hierarchy,
						// which are direct child of the users of fixedCatStr
						// categories
						if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, senderParentID, receiverUserCode, fixedCatStr,
								1, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed level parent check
				}// parent level uncontroll check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the sender
						// hierarchy
						// without any check of the fixed category
						if (checkAgain && channelUserDAO
								.isUserExistByParentIDRecursive(p_con, networkCode, receiverCategoryCode, senderUserID, receiverUserCode, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// validate user form the system within the sender
						// hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						if (checkAgain && channelUserDAO.isUserExistForHierarchyFixedCat(p_con, networkCode, receiverCategoryCode, senderUserID, receiverUserCode,
								fixedCatStr, 1, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// validate user form the system within the sender
						// hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						if (checkAgain && channelUserDAO.isUserExistForParentFixedCat(p_con, networkCode, receiverCategoryCode, senderUserID, receiverUserCode, fixedCatStr,
								1, p_txnSubType)) {
							checkAgain = false;
						}
					}// fixed level parent check
				}// Self level uncontroll check
			}// uncontrol transaction check
			// if user is not found in the above conditions then transaction
			// can not be go further and send the error message
			if (checkAgain) {
				if (!p_isFromWeb) {
					final String args[] = { receiverUserCode };
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.ERROR_USER_TRANSFER_NOT_ALLOWED, args);
				}
				throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, "message.channeltransfer.transferrulenotdefine", p_forwardPath);
			}
		}// ends here user level transfer rule type
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exited isOutsideHierarchy= " + isOutsideHierarchy);
		}
		return isOutsideHierarchy;
	}

	/**
	 * Method getCategoryStrValue.
	 * This method evaluvate entered string and parse it in the form that value
	 * can be passed in the database
	 * query for IN condition as it convert a,b to 'a','b' format.
	 * 
	 * @param p_catString
	 *            String
	 * @return String
	 */
	private static String getCategoryStrValue(String p_catString) {
		if (_log.isDebugEnabled()) {
			_log.debug("getCategoryStrValue", "Entered p_catString = " + p_catString);
		}

		final StringBuffer fixedCatStrBuf = new StringBuffer();
		final String tempArr[] = p_catString.split(",");
		for (int i = 0; i < tempArr.length; i++) {
			fixedCatStrBuf.append("'");
			fixedCatStrBuf.append(tempArr[i]);
			fixedCatStrBuf.append("',");
		}
		final String fixedCatStr = fixedCatStrBuf.substring(0, fixedCatStrBuf.length() - 1);
		if (_log.isDebugEnabled()) {
			_log.debug("getCategoryStrValue", "Exited fixedCatStr= " + fixedCatStr);
		}
		return fixedCatStr;
	}

	/**
	 * Method to validate the sender available controls like balance check and
	 * Transfer Out counts
	 * 
	 * @param p_con
	 * @param p_transferID
	 * @param p_c2STransferVO
	 * @param p_quantityRequired
	 * @throws BTSLBaseException
	 */
	public static void validateSenderAvailableControls(Connection p_con, String p_transferID, C2STransferVO p_c2STransferVO, int p_quantityRequired) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("validateSenderAvailableControls", "Entered p_transferID " + p_transferID + " p_quantityRequired=" + p_quantityRequired);
		}
		final boolean isLockRecordForUpdate = false;
		final String METHOD_NAME = "validateSenderAvailableControls";
		try {
			// setting the transfer value as sum of all the vouchers
			checkUserBalanceAvailable(p_con, p_transferID, p_c2STransferVO, p_quantityRequired);
			// reverting the transfer value back for one voucher
			ChannelTransferBL.checkC2STransferOutCounts(p_con, p_c2STransferVO, isLockRecordForUpdate, true, p_quantityRequired);
		} catch (BTSLBaseException be) {
			_log.error("validateSenderAvailableControls", "BTSL Base Exception " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("validateSenderAvailableControls", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[validateSenderAvailableControls]",
					p_transferID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "validateSenderAvailableControls", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("validateSenderAvailableControls", "Exiting p_transferID:" + p_transferID);
		}
	}

	/**
	 * Credits the user back for the failed transaction and also make an entry
	 * in C2S Items table
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @param p_transferIdList
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO creditUserBalanceForProduct(Connection p_con, String p_requestID, C2STransferVO p_c2STransferVO, ArrayList p_transferIdList) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("creditUserBalanceForProduct", "Entered p_requestID " + p_requestID + " p_transferIdList size=" + p_transferIdList.size());
		}
		UserBalancesVO userBalancesVO = null;
		ArrayList itemList = null;
		final String METHOD_NAME = "creditUserBalanceForProduct";
		try {
			itemList = new ArrayList();
			userBalancesVO = prepareUserBalanceVOFromTransferVO(p_c2STransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferVO.getSourceType(), PretupsI.CREDIT,
					PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			_userBalancesDAO.updateUserDailyBalances(p_con, p_c2STransferVO.getTransferDate(), userBalancesVO);

			// Credit the sender
			// int
			// updateCount=_userBalancesDAO.creditUserBalances(p_con,userBalancesVO);
			final int updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
			if (updateCount <= 0) {
				throw new BTSLBaseException("ChannelUserBL", "creditUserBalanceForProduct", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
			}

			final C2STransferItemVO oldC2STransferItemVO = (C2STransferItemVO) p_c2STransferVO.getTransferItemList().get(0);
			// Update Previous and Post balances of sender in sender Item,
			// creating new Items VO for credit back
			for (int i = 0, size = p_transferIdList.size(); i < size; i++) {
				final C2STransferItemVO c2STransferItemVO = new C2STransferItemVO();
				c2STransferItemVO.setMsisdn(oldC2STransferItemVO.getMsisdn());
				c2STransferItemVO.setRequestValue(oldC2STransferItemVO.getRequestValue());
				c2STransferItemVO.setSubscriberType(oldC2STransferItemVO.getSubscriberType());
				c2STransferItemVO.setTransferDate(oldC2STransferItemVO.getTransferDate());
				c2STransferItemVO.setTransferDateTime(oldC2STransferItemVO.getTransferDateTime());
				c2STransferItemVO.setTransferID((String) p_transferIdList.get(i));
				c2STransferItemVO.setUserType(oldC2STransferItemVO.getUserType());
				c2STransferItemVO.setEntryDate(oldC2STransferItemVO.getEntryDate());
				c2STransferItemVO.setEntryDateTime(oldC2STransferItemVO.getEntryDateTime());
				c2STransferItemVO.setPrefixID(oldC2STransferItemVO.getPrefixID());
				c2STransferItemVO.setTransferValue(oldC2STransferItemVO.getTransferValue());
				c2STransferItemVO.setInterfaceID(oldC2STransferItemVO.getInterfaceID());
				c2STransferItemVO.setInterfaceType(oldC2STransferItemVO.getInterfaceType());
				c2STransferItemVO.setServiceClass(oldC2STransferItemVO.getServiceClass());
				c2STransferItemVO.setServiceClassCode(oldC2STransferItemVO.getServiceClassCode());
				c2STransferItemVO.setInterfaceHandlerClass(oldC2STransferItemVO.getInterfaceHandlerClass());
				c2STransferItemVO.setSNo(3);
				c2STransferItemVO.setEntryType(PretupsI.CREDIT);
				c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
				c2STransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance() + oldC2STransferItemVO.getRequestValue() * i);
				c2STransferItemVO.setPostBalance(userBalancesVO.getBalance() + oldC2STransferItemVO.getRequestValue() * i);

				itemList.add(0, c2STransferItemVO);
			}
			final int addCount = new C2STransferDAO().addC2STransferItemDetails(p_con, itemList, p_c2STransferVO.getTransferID());
			if (addCount < 0) {
				throw new BTSLBaseException("ChannelUserBL", "creditUserBalanceForProduct", PretupsErrorCodesI.C2S_ERROR_NOT_MAKECREDIT_ENTRY);
			}
			p_c2STransferVO.getTransferItemList().addAll(itemList);
			p_c2STransferVO.setCreditAmount(p_c2STransferVO.getTransferValue());
			p_c2STransferVO.setCreditBackStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			final String[] messageArgArray = { p_c2STransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2STransferVO.getRequestedAmount()), p_c2STransferVO
					.getTransferID(), PretupsBL.getDisplayAmount(userBalancesVO.getBalance()), p_c2STransferVO.getLastTransferId(), String.valueOf(p_transferIdList
							.size()) };
			p_c2STransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) p_c2STransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
					PretupsErrorCodesI.MVD_C2S_SENDER_CREDIT_SUCCESS, messageArgArray));
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error("creditUserBalanceForProduct", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[creditUserBalanceForProduct]",
					p_requestID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "creditUserBalanceForProduct", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("creditUserBalanceForProduct", "Exiting p_requestID:" + p_requestID);
		}
		return userBalancesVO;
	}

	/**
	 * Checks the user available balance before transferring
	 * 
	 * @param p_con
	 * @param p_transferID
	 * @param p_c2STransferVO
	 * @param p_quantityRequired
	 * @throws BTSLBaseException
	 */
	public static void checkUserBalanceAvailable(Connection p_con, String p_transferID, C2STransferVO p_c2STransferVO, int p_quantityRequired) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("checkUserBalanceAvailable", "Entered p_transferID " + p_transferID + " p_quantityRequired=" + p_quantityRequired);
		}
		final String METHOD_NAME = "checkUserBalanceAvailable";
		boolean useHomeStock = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue();
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		try {
			// TransferProfileProductVO transferProfileProductVO=new
			// TransferProfileDAO().loadTransferProfileProducts(p_con,((ChannelUserVO)p_c2STransferVO.getSenderVO()).getTransferProfileID(),p_c2STransferVO.getProductCode());
			final TransferProfileProductVO transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(((ChannelUserVO) p_c2STransferVO.getSenderVO())
					.getTransferProfileID(), p_c2STransferVO.getProductCode());
			String[] strArr = null;

			/*
			 * check that the requested quantity must be between min and max
			 * quantity allow with the user profile
			 */
			// 3.
			if (transferProfileProductVO.getC2sMinTxnAmtAsLong() > p_c2STransferVO.getTransferValue() || transferProfileProductVO.getC2sMaxTxnAmtAsLong() < p_c2STransferVO
					.getTransferValue()) {
				strArr = new String[] { PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), transferProfileProductVO.getC2sMinTxnAmt(), transferProfileProductVO
						.getC2sMaxTxnAmt(), String.valueOf(p_quantityRequired) };
				throw new BTSLBaseException("ChannelUserBL", "checkUserBalanceAvailable", PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_AMT_NOTBETWEEN_MINMAX, 0, strArr, null);
			}
			// For Roaming Recharge CR000012
			long userProductBalance = 0;
			boolean flag = true;
			final boolean listAddedForPDA = false;
			final UserBalancesDAO userBalanceDAO = new UserBalancesDAO();
			final ArrayList<UserProductWalletMappingVO> walletList = new ArrayList<UserProductWalletMappingVO>();
						if (userProductMultipleWallet) {
				final List<UserProductWalletMappingVO> walletsForNetAndPrdct = PretupsBL.getPrtSortWalletsForNetIdAndPrdId(p_c2STransferVO.getNetworkCode(), p_c2STransferVO
						.getProductCode());
				final List<UserProductWalletMappingVO> pdaWalletList = PretupsBL.getPDAWalletsVO(walletsForNetAndPrdct);
				ArrayList<UserProductWalletMappingVO> userbalance = new ArrayList<UserProductWalletMappingVO>();
				for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
					final UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();
					if (!(PretupsI.YES.equals(userProductWalletMappingVO.getPartialDedAlwd()))) {
						flag = false;
						break;
					}
				}
				p_c2STransferVO.setPdaWalletList(pdaWalletList);
				if (useHomeStock) {
					userbalance = userBalanceDAO.loadUserBalanceWhenPDAIsN(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
							p_c2STransferVO.getNetworkCode(), p_c2STransferVO.getProductCode());
					if (flag) {
						userBalanceDAO.loadUserBalanceForPDAWallets(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(), p_c2STransferVO
								.getNetworkCode(), p_c2STransferVO.getProductCode(), p_c2STransferVO);
						userProductBalance = p_c2STransferVO.getTotalBalanceAcrossPDAWallets();
					} else {
						userProductBalance = whenAnyWalletPDAIsN(p_con, p_c2STransferVO, METHOD_NAME, userProductBalance, listAddedForPDA, walletList, pdaWalletList,
								userbalance, transferProfileProductVO.getMinResidualBalanceAsLong());
					}
				} else {
					userbalance = userBalanceDAO.loadUserBalanceWhenPDAIsN(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
							p_c2STransferVO.getReceiverNetworkCode(), p_c2STransferVO.getProductCode());
					if (flag) {
						userBalanceDAO.loadUserBalanceForPDAWallets(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(), p_c2STransferVO
								.getReceiverNetworkCode(), p_c2STransferVO.getProductCode(), p_c2STransferVO);
						userProductBalance = p_c2STransferVO.getTotalBalanceAcrossPDAWallets();
					} else {
						userProductBalance = whenAnyWalletPDAIsN(p_con, p_c2STransferVO, METHOD_NAME, userProductBalance, listAddedForPDA, walletList, pdaWalletList,
								userbalance, transferProfileProductVO.getMinResidualBalanceAsLong());
					}
				}
			} else {
			if (useHomeStock) {
				userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
						p_c2STransferVO.getNetworkCode(), p_c2STransferVO.getProductCode());
			} else {
				userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_transferID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
						p_c2STransferVO.getReceiverNetworkCode(), p_c2STransferVO.getProductCode());
			}
			}

			if (userProductBalance - p_c2STransferVO.getTransferValue() * p_quantityRequired < 0) {
				strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
						.getDisplayAmount(userProductBalance), String.valueOf(p_quantityRequired) };
				throw new BTSLBaseException("ChannelUserBL", "checkUserBalanceAvailable", PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
			}

			final long allowedMaxTrfAmount = Double.valueOf(((userProductBalance * (double) transferProfileProductVO.getAllowedMaxPercentageInt()) / 100)).longValue();
			if (p_c2STransferVO.getTransferValue() * p_quantityRequired > allowedMaxTrfAmount) {
				strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
						.getDisplayAmount(userProductBalance), String.valueOf(transferProfileProductVO.getAllowedMaxPercentageInt()), String
						.valueOf(p_quantityRequired) };
				throw new BTSLBaseException("ChannelUserBL", "checkUserBalanceAvailable", PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_MAX_PER_TRF_FAIL, 0, strArr, null);
			} else if (userProductBalance - (p_c2STransferVO.getTransferValue() * p_quantityRequired) < transferProfileProductVO.getMinResidualBalanceAsLong()) {
				strArr = new String[] { p_c2STransferVO.getProductName(), PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()), PretupsBL
						.getDisplayAmount(userProductBalance), PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong()), String
						.valueOf(p_quantityRequired) };
				throw new BTSLBaseException("ChannelUserBL", "checkUserBalanceAvailable", PretupsErrorCodesI.MVD_CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
			}
		} catch (BTSLBaseException be) {
			_log.error("checkUserBalanceAvailable", "BTSL Base Exception " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("checkUserBalanceAvailable", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[checkUserBalanceAvailable]",
					p_transferID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "checkUserBalanceAvailable", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("checkUserBalanceAvailable", "Exiting p_transferID:" + p_transferID);
		}
	}

	/**
	 * Update parent user information
	 * @param pCon TODO
	 * @param p_requestVO
	 *            RequestVO
	 * 
	 * @throws BTSLBaseException
	 */
	public static void updateUserInfo(Connection pCon, RequestVO p_requestVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("ChannelUserBL", "updateUserInfo Entered p_requestVO: " + p_requestVO);
		}
		final String METHOD_NAME = "updateUserInfo";
		//Connection con = null;
		try {
			//con = OracleUtil.getConnection();
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

			if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
				final ChannelUserVO userVO = channelUserDAO.loadActiveUserId(pCon, p_requestVO.getSenderLoginID(), "LOGINID");
				if (userVO != null) {
					// if sender is staff user then load msisdn of parent channel
					// user and set it in requestMsisdn field,
					// otherwise set msisdn of channel user in the field
					  String parentMsisdn=channelUserDAO.loadParentUserMsisdn(pCon,p_requestVO.getSenderLoginID(),"LOGINID");
					  if(!BTSLUtil.isNullString(parentMsisdn)){
						  p_requestVO.setActiverUserId(userVO.getUserID());
						  p_requestVO.setRequestMSISDN(parentMsisdn);
						  p_requestVO.setIsStaffUser(true);
					  }
					  else{
						  p_requestVO.setActiverUserId(userVO.getUserID());
						  p_requestVO.setRequestMSISDN(userVO.getMsisdn());
						  p_requestVO.setIsStaffUser(false);
					  }
					  p_requestVO.setCategoryCode(userVO.getCategoryCode());
					  
				} else {
					ChannelUserVO CAuserVO = new UserDAO().loadAllUserDetailsByLoginID(pCon, p_requestVO.getSenderLoginID());
					if (CAuserVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
						p_requestVO.setActiverUserId(CAuserVO.getUserID());
						p_requestVO.setRequestMSISDN(CAuserVO.getMsisdn());
						p_requestVO.setIsStaffUser(false);
					} else
						throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.NO_USER_EXIST);
				}
				
			} else if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
				final String filteredMsisdn = PretupsBL.getFilteredMSISDN(p_requestVO.getRequestMSISDN());
				final ChannelUserVO userVO = channelUserDAO.loadActiveUserId(pCon, filteredMsisdn, "MSISDN");
				if (userVO != null) {
					// if sender is staff user then load msisdn of parent channel
					// user and set it in requestMsisdn field,
					// otherwise set msisdn of channel user in the field
					  String parentMsisdn=channelUserDAO.loadParentUserMsisdn(pCon,filteredMsisdn,"MSISDN");
					  if(!BTSLUtil.isNullString(parentMsisdn)){
						  p_requestVO.setRequestMSISDN(parentMsisdn);
						  p_requestVO.setMessageSentMsisdn(parentMsisdn);
						  p_requestVO.setIsStaffUser(true);
					  }
					  else{
						  p_requestVO.setRequestMSISDN(userVO.getMsisdn());
						  p_requestVO.setMessageSentMsisdn(userVO.getMsisdn());
						  p_requestVO.setIsStaffUser(false);
					  }
					  p_requestVO.setActiverUserId(userVO.getUserID());
					  p_requestVO.setCategoryCode(userVO.getCategoryCode());
				} else {
					 throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.NO_USER_EXIST);
				}
			} else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
				final ChannelUserVO userVO = channelUserDAO.loadActiveUserId(pCon, p_requestVO.getSenderExternalCode(), "EXTGWCODE");
				if (userVO != null) {
					 String parentMsisdn=channelUserDAO.loadParentUserMsisdn(pCon,p_requestVO.getSenderExternalCode(),"EXTGWCODE");
					  if(!BTSLUtil.isNullString(parentMsisdn))
						  p_requestVO.setRequestMSISDN(parentMsisdn);
					  else
						  p_requestVO.setRequestMSISDN(userVO.getMsisdn());
					  
						p_requestVO.setActiverUserId(userVO.getUserID());
						p_requestVO.setCategoryCode(userVO.getCategoryCode());
				} else {
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.NO_USER_EXIST);
				}
			}
		} catch (BTSLBaseException be) {
			_log.error(METHOD_NAME, " BTSL Exception while updating parent user info :" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error(METHOD_NAME, " Exception while updating parent user info :" + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[updateUserInfo]", "", "", "",
					"Exception :" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
		} finally {
			/*if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME, e);
				}
			}*/
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exit");
			}
		}

	}

	/**
	 * Method to check whether the Msisdn last request was under process or not,
	 * also marks the request as under process and un marks also
	 * 
	 * @param con
	 * @param p_requestID
	 * @param p_userPhoneVO
	 * @param mark
	 * @throws BTSLBaseException
	 */
	public static void checkRequestUnderProcessPOS(Connection con, String p_requestID, UserPhoneVO p_userPhoneVO, boolean mark) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("checkRequestUnderProcessPOS", p_requestID, "Entered senderVO msisdn:" + p_userPhoneVO.getMsisdn());
		}
		int count = 0;
		final String METHOD_NAME = "checkRequestUnderProcessPOS";
		try {
			// Lock USER_PHONES table
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			p_userPhoneVO.setCurrentModifiedOn(p_userPhoneVO.getModifiedOn());
			channelUserDAO.lockUserPhonesTable(con, p_userPhoneVO);
			if (mark) {
				// Condition Added of time so that after expiry of 5 minutes it
				// will be reset
				// (new
				// Date().getTime()-p_userPhoneVO.getModifiedOn().getTime())<=300000)
				// added for rejecting second request if two parallel requests
				// comes for the same user.
				// if (p_userPhoneVO.getLastTransactionStatus() != null &&
				// p_userPhoneVO.getLastTransactionStatus().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)
				// &&
				// ((p_userPhoneVO.getModifiedOn().getTime()-p_userPhoneVO.getLastTransactionOn().getTime()))<=300000)
				if (p_userPhoneVO.getLastTransactionStatus() != null && p_userPhoneVO.getLastTransactionStatus().equals(PretupsI.TXN_STATUS_UNDER_PROCESS) && ((p_userPhoneVO
						.getCurrentModifiedOn().getTime() - p_userPhoneVO.getLastTransactionOn().getTime()) <= 300000 || (new Date().getTime() - p_userPhoneVO
								.getModifiedOn().getTime()) <= 300000)) {
					throw new BTSLBaseException("ChannelUserBL", "checkRequestUnderProcessPOS", PretupsErrorCodesI.CHNL_ERROR_SENDER_REQ_UNDERPROCESS);
				}
				p_userPhoneVO.setModifiedOn(p_userPhoneVO.getCurrentModifiedOn());
				p_userPhoneVO.setLastTransactionOn(p_userPhoneVO.getModifiedOn());
				count = _channelUserDAO.markRequestUnderProcess(con, p_requestID, p_userPhoneVO);
			} else {
				count = _channelUserDAO.unmarkRequestUnderProcessPOS(con, p_requestID, p_userPhoneVO);
			}
			if (count <= 0) {
				throw new BTSLBaseException("ChannelUserBL", "checkRequestUnderProcessPOS", PretupsErrorCodesI.CHNL_ERROR_SENDER_REQUNDERPROCESS_NOTUPDATED);
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error("checkRequestUnderProcessPOS", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[checkRequestUnderProcessPOS]",
					p_requestID, p_userPhoneVO.getMsisdn(), "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "checkRequestUnderProcessPOS", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("checkRequestUnderProcessPOS", "Exiting count:" + count);
		}
	}

	/**
	 * Credits the user back for the failed transaction and also make an entry
	 * in C2S Items table
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static int creditUserBalanceForProductReversal(Connection p_con, String p_requestID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("creditUserBalanceForProductReversal", "Entered p_requestID " + p_requestID);
		}
		int updateCount = 0;
		UserBalancesVO userBalancesVO = null;
		final String METHOD_NAME = "creditUserBalanceForProductReversal";
		try {
			if (PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equals(p_c2STransferVO.getServiceType())) {
				userBalancesVO = prepareUserBalanceVOFromTransferVOReversal(p_c2STransferVO, PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL, p_c2STransferVO.getSourceType(),
						PretupsI.CREDIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
				userBalancesVO.setQuantityToBeUpdated(p_c2STransferVO.getSenderTransferValue());
			} else {
				userBalancesVO = prepareUserBalanceVOFromTransferVOReversal(p_c2STransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferVO.getSourceType(),
						PretupsI.CREDIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			}
			_userBalancesDAO.updateUserDailyBalances(p_con, p_c2STransferVO.getTransferDate(), userBalancesVO);

			// Credit the sender
			// int
			// updateCount=_userBalancesDAO.creditUserBalances(p_con,userBalancesVO);
			updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
			if (updateCount <= 0) {
				p_c2STransferVO.setPreviousBalance(0);
				p_c2STransferVO.setPostBalance(0);
				throw new BTSLBaseException("ChannelUserBL", "creditUserBalanceForProductReversal", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
			} else {
				p_c2STransferVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
				p_c2STransferVO.setPostBalance(userBalancesVO.getBalance());

			}
		} catch (BTSLBaseException be) {
			updateCount = 0;
			throw be;
		} catch (Exception e) {
			updateCount = 0;
			_log.error("creditUserBalanceForProduct", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[creditUserBalanceForProductReversal]",
					p_requestID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", "creditUserBalanceForProductReversal", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("creditUserBalanceForProductReversal", "Exiting p_requestID:" + p_requestID);
		}
		return updateCount;
	}

	/**
	 * Method to prepare the User balance VO from the C2S Transfer VO for
	 * updation of balances
	 * 
	 * @param p_c2sTransferVO
	 * @param p_transferType
	 * @param p_source
	 * @param p_entryType
	 * @param p_transType
	 * @param p_transferCategory
	 * @return UserBalancesVO
	 */
	private static UserBalancesVO prepareUserBalanceVOFromTransferVOReversal(C2STransferVO p_c2sTransferVO, String p_transferType, String p_source, String p_entryType, String p_transType, String p_transferCategory) {
		final UserBalancesVO userBalancesVO = new UserBalancesVO();
		userBalancesVO.setUserID(p_c2sTransferVO.getSenderID());
		userBalancesVO.setProductCode(p_c2sTransferVO.getProductCode());
		userBalancesVO.setProductName(p_c2sTransferVO.getProductName());
		userBalancesVO.setProductShortName(p_c2sTransferVO.getProductName());
		userBalancesVO.setNetworkCode(p_c2sTransferVO.getNetworkCode());
		boolean useHomeStock = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue();
		// Roam Recharge CR 000012
		if (useHomeStock) {
			userBalancesVO.setNetworkFor(p_c2sTransferVO.getNetworkCode());
		} else {
			userBalancesVO.setNetworkFor(p_c2sTransferVO.getReceiverNetworkCode());
		}

		userBalancesVO.setLastTransferID(p_c2sTransferVO.getTransferID());
		userBalancesVO.setSource(p_source);
		userBalancesVO.setCreatedBy(p_c2sTransferVO.getCreatedBy());
		userBalancesVO.setEntryType(p_entryType);
		userBalancesVO.setType(p_transType);
		userBalancesVO.setTransferCategory(p_transferCategory);
		userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferVO.getRequestedAmount()));
		userBalancesVO.setLastTransferType(p_transferType);
		userBalancesVO.setLastTransferOn(p_c2sTransferVO.getCreatedOn());
		userBalancesVO.setQuantityToBeUpdated(p_c2sTransferVO.getTransferValue());
		// Added to log user MSISDN on 13/02/2008
		userBalancesVO.setUserMSISDN(p_c2sTransferVO.getSenderMsisdn());
		return userBalancesVO;
	}

	/**
	 * Method to validate the sender available controls like balance check and
	 * Transfer Out counts
	 * 
	 * @param p_con
	 * @param p_transferID
	 * @param p_c2STransferVO
	 * @throws BTSLBaseException
	 */
	public static void validateOwnerAvailableControlsRoam(Connection p_con, String p_transferID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "validateOwnerAvailableControlsRoam";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_transferID: " + p_transferID);
		}
		try {
			checkOwnerBalanceAvailableRoam(p_con, p_transferID, p_c2STransferVO);
			// UserTransferCountsVO
			// userTransferCountsVO=ChannelTransferBL.checkC2STransferOutCounts(p_con,p_c2STransferVO,isLockRecordForUpdate,true);
		} catch (BTSLBaseException be) {
			_log.error(methodName, "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[validateSenderAvailableControls]",
					p_transferID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_transferID : " + p_transferID);
		}
	}

	/**
	 * Checks the user available balance before transferring
	 * 
	 * @param p_con
	 * @param p_transferID
	 * @param p_c2STransferVO
	 * @throws BTSLBaseException
	 */
	public static void checkOwnerBalanceAvailableRoam(Connection p_con, String p_transferID, C2STransferVO p_c2STransferOwnerVO) throws BTSLBaseException {
		final String methodName = "checkOwnerBalanceAvailableRoam";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_transferID = " + p_transferID);
		}
		boolean useHomeStock = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue();
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		try {
			final TransferProfileProductVO transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(p_c2STransferOwnerVO.getOwnerUserVO()
					.getTransferProfileID(), p_c2STransferOwnerVO.getProductCode());

			long userProductBalance = 0;
			/** START: Birendra: */

			final UserBalancesDAO userBalanceDAO = new UserBalancesDAO();
			if (userProductMultipleWallet) {

				final List<UserProductWalletMappingVO> walletsForNetAndPrdct = PretupsBL.getPrtSortWalletsForNetIdAndPrdId(p_c2STransferOwnerVO.getNetworkCode(),
						p_c2STransferOwnerVO.getProductCode());
				final List<UserProductWalletMappingVO> pdaWalletList = PretupsBL.getPDAWalletsVO(walletsForNetAndPrdct);

				p_c2STransferOwnerVO.setPdaWalletList(pdaWalletList);

				if (useHomeStock) {
					userBalanceDAO.loadUserBalanceForPDAWallets(p_con, p_transferID, p_c2STransferOwnerVO.getOwnerUserVO().getUserID(), p_c2STransferOwnerVO
							.getNetworkCode(), p_c2STransferOwnerVO.getNetworkCode(), p_c2STransferOwnerVO.getProductCode(), p_c2STransferOwnerVO);
					userProductBalance = p_c2STransferOwnerVO.getTotalBalanceAcrossPDAWallets();

				} else {
					userBalanceDAO.loadUserBalanceForPDAWallets(p_con, p_transferID, p_c2STransferOwnerVO.getOwnerUserVO().getUserID(), p_c2STransferOwnerVO
							.getNetworkCode(), p_c2STransferOwnerVO.getReceiverNetworkCode(), p_c2STransferOwnerVO.getProductCode(), p_c2STransferOwnerVO);
					userProductBalance = p_c2STransferOwnerVO.getTotalBalanceAcrossPDAWallets();
				}
				/** STOP: Birendra: */
			} else {

				if (useHomeStock) {
					userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_transferID, p_c2STransferOwnerVO.getOwnerUserVO()
							.getUserID(), p_c2STransferOwnerVO.getNetworkCode(), p_c2STransferOwnerVO.getNetworkCode(), p_c2STransferOwnerVO.getProductCode());
				} else {
					userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_transferID, p_c2STransferOwnerVO.getOwnerUserVO()
							.getUserID(), p_c2STransferOwnerVO.getNetworkCode(), p_c2STransferOwnerVO.getReceiverNetworkCode(), p_c2STransferOwnerVO.getProductCode());
				}
			}

			if (p_c2STransferOwnerVO.getRoamPenalty() > (userProductBalance-transferProfileProductVO.getMinResidualBalanceAsLong())) {
				// strArr= new String[] {userProductBalance};
				p_c2STransferOwnerVO.setPenaltyInsufficientBalanceOwner(true);
				throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_OWNR_BAL_LESS_ROAM, 0, null, null);

			}

		} catch (BTSLBaseException be) {
			_log.error(methodName, "BTSL Base Exception : " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception : " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[" + methodName + "]", p_transferID,
					p_c2STransferOwnerVO.getOwnerUserVO().getUserID(), p_c2STransferOwnerVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_transferID : " + p_transferID);
		}
	}

	/**
	 * Debits the user ID for the transfer value
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO debitUserBalanceForProductRoamOwner(Connection p_con, String p_requestID, C2STransferVO p_c2STransferOwnerVO) throws BTSLBaseException {
		final String methodName = "debitUserBalanceForProduct";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_requestID = " + p_requestID);
		}
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		UserBalancesVO userBalancesVO = null;
		try {
			userBalancesVO = prepareUserBalanceVOFromTransferVORoamOwner(p_c2STransferOwnerVO, PretupsI.TRANSFER_TYPE_TXN, p_c2STransferOwnerVO.getSourceType(),
					PretupsI.DEBIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);

			final int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(p_con, p_c2STransferOwnerVO.getTransferDate(), userBalancesVO);
			_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = " + dailyBalanceUpdateCount);

			/** START: Birendra: */
			if (userProductMultipleWallet) {
				final ChannelUserVO senderVO = p_c2STransferOwnerVO.getOwnerUserVO();
				final String senderTransferProfileID = senderVO.getTransferProfileID();
				final String categoryCode = senderVO.getCategoryCode();

				Comparator<UserProductWalletMappingVO> comparator = new Comparator<UserProductWalletMappingVO>(){
					@Override
					public int compare(UserProductWalletMappingVO u1, UserProductWalletMappingVO u2) {
						return u1.getPenaltyAccountPriority() - u2.getPenaltyAccountPriority(); 
					}
				};
				Collections.sort(p_c2STransferOwnerVO.getPdaWalletList(),comparator);

				final int[] updateCount = _userBalancesDAO.debitUserBalancesFromWallets(p_con, userBalancesVO, senderTransferProfileID, p_c2STransferOwnerVO.getProductCode(),
						true, categoryCode);

				if (updateCount == null || updateCount.length <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
				}

				p_c2STransferOwnerVO.setTotalBalanceAcrossPDAWallets(userBalancesVO.getBalance());
				p_c2STransferOwnerVO.setTotalPreviousBalanceAcrossPDAWallets(userBalancesVO.getPreviousBalance());
				/** Birendra:Added */

			} else {

				// _userBalancesDAO.updateUserDailyBalances(p_con,p_c2STransferVO.getTransferDate(),userBalancesVO);
				final int updateCount = _userBalancesDAO.debitUserBalances(p_con, userBalancesVO, p_c2STransferOwnerVO.getOwnerUserVO()
						.getTransferProfileID(), p_c2STransferOwnerVO.getProductCode(), true, (p_c2STransferOwnerVO.getOwnerUserVO()).getCategoryCode());
				if (updateCount <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
				}

			}
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL)||be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE)){
				p_c2STransferOwnerVO.setPenaltyInsufficientBalanceOwner(true); 
				throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_OWNR_BAL_LESS_ROAM, 0, null, null);	
			}else{
				throw be;
			}
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[" + methodName + "]", p_requestID,
					p_c2STransferOwnerVO.getOwnerUserVO().getMsisdn(), p_c2STransferOwnerVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_requestID : " + p_requestID);
		}
		return userBalancesVO;
	}

	/**
	 * Debits the user ID for the transfer value
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO debitUserBalanceForProductRoamOwnerModified(Connection p_con, String p_requestID, C2STransferVO p_c2STransferOwnerVO) throws BTSLBaseException {
		final String methodName = "debitUserBalanceForProductRoamOwnerModified";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_requestID = " + p_requestID);
		}

		UserBalancesVO userBalancesVO = null;
		boolean balanceUpdated = false;
		try {
			userBalancesVO = prepareUserBalanceVOFromTransferVORoamOwner(p_c2STransferOwnerVO, PretupsI.TRANSFER_TYPE_TXN, p_c2STransferOwnerVO.getSourceType(),
					PretupsI.DEBIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);

			final ArrayList<UserBalancesVO> dailyBalanceUpdateCountList = _userBalancesDAO.getUserDailyBalancesList(p_con, p_c2STransferOwnerVO.getTransferDate(), userBalancesVO);

			/** START: Birendra: */
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				final ChannelUserVO senderVO = p_c2STransferOwnerVO.getOwnerUserVO();
				final String senderTransferProfileID = senderVO.getTransferProfileID();
				final String categoryCode = senderVO.getCategoryCode();

				Comparator<UserProductWalletMappingVO> comparator = new Comparator<UserProductWalletMappingVO>(){
					@Override
					public int compare(UserProductWalletMappingVO u1, UserProductWalletMappingVO u2) {
						return u1.getPenaltyAccountPriority() - u2.getPenaltyAccountPriority(); 
					}
				};
				Collections.sort(p_c2STransferOwnerVO.getPdaWalletList(),comparator);

				final int[] updateCount = _userBalancesDAO.debitUserBalancesFromWalletsModified(p_con, userBalancesVO, senderTransferProfileID, p_c2STransferOwnerVO.getProductCode(),
						true, categoryCode);

				if (updateCount == null || updateCount.length <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
				}else {
					balanceUpdated = true;
				}
				userBalancesVO.setDailyBalanceUpdateCountList(dailyBalanceUpdateCountList);
				p_c2STransferOwnerVO.setTotalBalanceAcrossPDAWallets(userBalancesVO.getBalance());
				p_c2STransferOwnerVO.setTotalPreviousBalanceAcrossPDAWallets(userBalancesVO.getPreviousBalance());
				/** Birendra:Added */
			} else {
				// _userBalancesDAO.updateUserDailyBalances(p_con,p_c2STransferVO.getTransferDate(),userBalancesVO);...
				final long[] update = _userBalancesDAO.debitUserBalancesModified(p_con, userBalancesVO, p_c2STransferOwnerVO.getOwnerUserVO()
						.getTransferProfileID(), p_c2STransferOwnerVO.getProductCode(), true, (p_c2STransferOwnerVO.getOwnerUserVO()).getCategoryCode());
				if (update[0] <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
				}else {
					 p_con.commit();
					 balanceUpdated = true;
					_userBalancesDAO.insertUserThresholdCounters(p_con, userBalancesVO, TransferProfileProductCache.getTransferProfileDetails(((ChannelUserVO) p_c2STransferOwnerVO.getSenderVO()).getTransferProfileID(), p_c2STransferOwnerVO.getProductCode()), update[1], update[2], ((ChannelUserVO) p_c2STransferOwnerVO.getSenderVO()).getCategoryCode());
					userBalancesVO.setDailyBalanceUpdateCountList(dailyBalanceUpdateCountList);
				}
			}
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
				
				if(balanceUpdated) {
					userBalancesVO = prepareUserBalanceVOFromTransferVORoamOwner(p_c2STransferOwnerVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferOwnerVO.getSourceType(), PretupsI.CREDIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
					int[] updateCounts = _userBalancesDAO.creditUserBalancesForWalletsModified(p_con, userBalancesVO, p_c2STransferOwnerVO.getCategoryCode());
					if (updateCounts == null || updateCounts.length <= 0) {
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					}else {
						try {
							p_con.commit();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				} else {
					int updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferOwnerVO.getCategoryCode());
					if (updateCount <= 0)
						throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
					else {
						try {
							p_con.commit();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
			if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL)||be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE)){
				p_c2STransferOwnerVO.setPenaltyInsufficientBalanceOwner(true); 
				throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.CHNL_ERROR_OWNR_BAL_LESS_ROAM, 0, null, null);	
			}else{
				throw be;
			}
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			
			if(balanceUpdated) {
			userBalancesVO = prepareUserBalanceVOFromTransferVORoamOwner(p_c2STransferOwnerVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferOwnerVO.getSourceType(), PretupsI.CREDIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				int[] updateCounts = _userBalancesDAO.creditUserBalancesForWalletsModified(p_con, userBalancesVO, p_c2STransferOwnerVO.getCategoryCode());
				if (updateCounts == null || updateCounts.length <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				}else {
					try {
						p_con.commit();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				int updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferOwnerVO.getCategoryCode());
				if (updateCount <= 0)
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				else {
					try {
						p_con.commit();
					} catch (SQLException e1) {
						e.printStackTrace();
					}
				}
			}
		}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[" + methodName + "]", p_requestID,
					p_c2STransferOwnerVO.getOwnerUserVO().getMsisdn(), p_c2STransferOwnerVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_requestID : " + p_requestID);
		}
		return userBalancesVO;
	}
	
	
	/**
	 * Method to prepare the User balance VO from the C2S Transfer VO for
	 * updation of balances
	 * 
	 * @param p_c2sTransferVO
	 * @param p_transferType
	 * @param p_source
	 * @param p_entryType
	 * @param p_transType
	 * @param p_transferCategory
	 * @return UserBalancesVO
	 */
	private static UserBalancesVO prepareUserBalanceVOFromTransferVORoamOwner(C2STransferVO p_c2sTransferOwnerVO, String p_transferType, String p_source, String p_entryType, String p_transType, String p_transferCategory) {
		final UserBalancesVO userBalancesVO = new UserBalancesVO();
		userBalancesVO.setUserID(p_c2sTransferOwnerVO.getOwnerUserVO().getUserID());
		userBalancesVO.setProductCode(p_c2sTransferOwnerVO.getProductCode());
		userBalancesVO.setProductName(p_c2sTransferOwnerVO.getProductName());
		userBalancesVO.setProductShortName(p_c2sTransferOwnerVO.getProductName());
		userBalancesVO.setNetworkCode(p_c2sTransferOwnerVO.getNetworkCode());
		boolean useHomeStock = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue();
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		// Roam Recharge CR 000012
		if (useHomeStock) {
			userBalancesVO.setNetworkFor(p_c2sTransferOwnerVO.getNetworkCode());
		} else {
			userBalancesVO.setNetworkFor(p_c2sTransferOwnerVO.getReceiverNetworkCode());
		}

		userBalancesVO.setLastTransferID(p_c2sTransferOwnerVO.getTransferID());
		userBalancesVO.setSource(p_source);
		// userBalancesVO.setCreatedBy(p_c2sTransferVO.getCreatedBy());
		userBalancesVO.setEntryType(p_entryType);
		userBalancesVO.setType(p_transType);
		userBalancesVO.setTransferCategory(p_transferCategory);
		userBalancesVO.setRequestedQuantity(String.valueOf(p_c2sTransferOwnerVO.getRequestedAmount()));
		userBalancesVO.setLastTransferType(p_transferType);
		userBalancesVO.setLastTransferOn(p_c2sTransferOwnerVO.getCreatedOn());

		// Added to log user MSISDN on 13/02/2008
		userBalancesVO.setUserMSISDN(p_c2sTransferOwnerVO.getOwnerUserVO().getMsisdn());
		/** START: Birendra: */
		if (userProductMultipleWallet) {
			userBalancesVO.setPdaWalletList(p_c2sTransferOwnerVO.getPdaWalletList());
			userBalancesVO.setBalance(p_c2sTransferOwnerVO.getTotalBalanceAcrossPDAWallets());
			userBalancesVO.setPreviousBalance(p_c2sTransferOwnerVO.getTotalPreviousBalanceAcrossPDAWallets());
		}
		/** STOP: Birendra: */

		userBalancesVO.setQuantityToBeUpdated(p_c2sTransferOwnerVO.getRoamPenalty());
		return userBalancesVO;
	}

	public static void copyRetailerVOtoOwnerVO(C2STransferVO p_ownerVO, C2STransferVO p_retailerVO) {
		p_ownerVO.setTransferValue(p_retailerVO.getTransferValue());
		p_ownerVO.setNetworkCode(p_retailerVO.getNetworkCode());
		p_ownerVO.setReceiverNetworkCode(p_retailerVO.getReceiverNetworkCode());
		p_ownerVO.setProductCode(p_retailerVO.getProductCode());
		// p_ownerVO.setRoamPenaltyOwner(p_retailerVO.getRoamPenaltyOwner());
		p_ownerVO.setSourceType(p_retailerVO.getSourceType());
		p_ownerVO.setTransferDate(p_retailerVO.getTransferDate());
		p_ownerVO.setCreatedOn(p_retailerVO.getCreatedOn());
		p_ownerVO.setTransferID(p_retailerVO.getTransferID());
		p_ownerVO.setRequestedAmount(p_retailerVO.getRequestedAmount());
		p_ownerVO.setProductName(p_retailerVO.getProductName());
		p_ownerVO.setModule(p_retailerVO.getModule());
		p_ownerVO.setServiceType(p_retailerVO.getServiceType());
		p_ownerVO.setSubService(p_retailerVO.getSubService());
		p_ownerVO.setSenderMsisdn(p_ownerVO.getOwnerUserVO().getMsisdn());
		p_ownerVO.setPdaWalletList(p_retailerVO.getPdaWalletList());
		p_ownerVO.setRoamPenalty(p_retailerVO.getRoamPenaltyOwner());
		p_ownerVO.setPenaltyDetails(p_retailerVO.getPenaltyDetails());
		p_ownerVO.setTransferItemList(p_retailerVO.getTransferItemList());
		p_ownerVO.setModifiedOn(p_retailerVO.getModifiedOn());
		// p_ownerVO.setCreatedBy(p_retailerVO.getCreatedBy());

	}


	public static long getUserBalanceAfterC2SReversal(Connection p_con,C2STransferVO p_c2STransferVO , String p_requestID) throws BTSLBaseException
	{
		final String methodName = "getUserBalanceAfterReversal";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_requestID = " + p_requestID);
		}
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		boolean useHomeStock = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue();
		final UserBalancesDAO userBalanceDAO = new UserBalancesDAO();	
		long userProductBalance=0;
		if (userProductMultipleWallet) {
			final List<UserProductWalletMappingVO> walletsForNetAndPrdct = PretupsBL.getPrtSortWalletsForNetIdAndPrdId(p_c2STransferVO.getNetworkCode(), p_c2STransferVO
					.getProductCode());
			final List<UserProductWalletMappingVO> pdaWalletList = PretupsBL.getPDAWalletsVO(walletsForNetAndPrdct);
			p_c2STransferVO.setPdaWalletList(pdaWalletList);
			if (useHomeStock) {
				userBalanceDAO.loadUserBalanceForPDAWallets(p_con, p_requestID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(), p_c2STransferVO
						.getNetworkCode(), p_c2STransferVO.getProductCode(), p_c2STransferVO);
				userProductBalance = p_c2STransferVO.getTotalBalanceAcrossPDAWallets();
			} 
			else {
				userBalanceDAO.loadUserBalanceForPDAWallets(p_con, p_requestID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(), p_c2STransferVO
						.getReceiverNetworkCode(), p_c2STransferVO.getProductCode(), p_c2STransferVO);
				userProductBalance = p_c2STransferVO.getTotalBalanceAcrossPDAWallets();
			}
		} else {

			if (useHomeStock) {
				userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_requestID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
						p_c2STransferVO.getNetworkCode(), p_c2STransferVO.getProductCode());
			} else {
				userProductBalance = new UserBalancesDAO().loadUserBalanceForProduct(p_con, p_requestID, p_c2STransferVO.getSenderID(), p_c2STransferVO.getNetworkCode(),
						p_c2STransferVO.getReceiverNetworkCode(), p_c2STransferVO.getProductCode());
			}

		}
		return userProductBalance;

	}
	/**
	 * Credits the user back for the failed transaction and also make an entry
	 * in C2S Items table
	 * 
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO creditUserBalanceForProductRoamOwner(Connection p_con, String p_requestID, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "creditUserBalanceForProduct";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_requestID : " + p_requestID);
		}
		UserBalancesVO userBalancesVO = null;
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		try {
			userBalancesVO = prepareUserBalanceVOFromTransferVORoamOwner(p_c2STransferVO, PretupsI.TRANSFER_TYPE_RCH_CREDIT, p_c2STransferVO.getSourceType(), PretupsI.CREDIT, PretupsI.TRANSFER_TYPE_C2S, PretupsI.TRANSFER_CATEGORY_SALE);
			int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(p_con, p_c2STransferVO.getTransferDate(), userBalancesVO);
			_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = " + dailyBalanceUpdateCount);
			// Credit the sender
			// int
			// updateCount=_userBalancesDAO.creditUserBalances(p_con,userBalancesVO);

			/** START: Birendra: 01FEB2015 */
			if (userProductMultipleWallet) {
				int[] updateCounts = _userBalancesDAO.creditUserBalancesForWallets(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
				if (updateCounts == null || updateCounts.length <= 0) {
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				}
			} else {
				int updateCount = _userBalancesDAO.creditUserBalances(p_con, userBalancesVO, p_c2STransferVO.getCategoryCode());
				if (updateCount <= 0)
					throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);

			}
			/** START: Birendra: 01FEB2015 */


		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[creditUserBalanceForProductRoamOwner]", p_requestID, p_c2STransferVO.getSenderMsisdn(), p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_requestID : " + p_requestID);
		}
		return userBalancesVO;
	}

	/**
	 * Credits the user back for the failed transaction in case of roam penalty
	 * @param p_con
	 * @param p_requestID
	 * @param p_c2STransferVO
	 * @return UserBalancesVO	 
	 * @throws BTSLBaseException
	 */
	public static UserBalancesVO creditUserBalanceForProductRoam(Connection p_con, String p_requestID,C2STransferVO p_c2STransferVO) throws BTSLBaseException
	{
		final String methodName = "creditUserBalanceForProduct";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered p_requestID : "+p_requestID);
		}
		UserBalancesVO userBalancesVO=null;
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		boolean privateSidServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW);
		boolean sidEncryptionAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED);
		try
		{
			PrivateRchrgVO prvo=null;
			if(privateSidServiceAllow)
			{
				PrivateRchrgDAO prdao= new PrivateRchrgDAO();
				prvo=prdao.loadSubscriberSIDDetails(p_con,p_c2STransferVO.getReceiverMsisdn());				
			}
			userBalancesVO=prepareUserBalanceVOFromTransferVO(p_c2STransferVO,PretupsI.TRANSFER_TYPE_RCH_CREDIT,p_c2STransferVO.getSourceType(),PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_C2S,PretupsI.TRANSFER_CATEGORY_SALE);
			int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(p_con,p_c2STransferVO.getTransferDate(),userBalancesVO);
			_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = "+dailyBalanceUpdateCount);
			//Credit the sender
			//int updateCount=_userBalancesDAO.creditUserBalances(p_con,userBalancesVO);
			/** START: Birendra: 01FEB2015 */
			if(userProductMultipleWallet) {
				int [] updateCounts = _userBalancesDAO.creditUserBalancesForWallets(p_con,userBalancesVO,p_c2STransferVO.getCategoryCode());
				if(updateCounts ==null || updateCounts.length <=0 ){
					throw new BTSLBaseException("ChannelUserBL", methodName,PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				}
			}else{
				int updateCount =_userBalancesDAO.creditUserBalances(p_con,userBalancesVO,p_c2STransferVO.getCategoryCode());
				if(updateCount<=0)
					throw new BTSLBaseException("ChannelUserBL", methodName,PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
			}
			/** START: Birendra: 01FEB2015 */
			/*//Update Previous and Post balances of sender in sender Item, creating new Items VO for credit back
   			C2STransferItemVO oldC2STransferItemVO=(C2STransferItemVO)p_c2STransferVO.getTransferItemList().get(0);
   			C2STransferItemVO c2STransferItemVO=new C2STransferItemVO();
   			c2STransferItemVO.setMsisdn(oldC2STransferItemVO.getMsisdn());
   			c2STransferItemVO.setRequestValue(oldC2STransferItemVO.getRequestValue());
   			c2STransferItemVO.setSubscriberType(oldC2STransferItemVO.getSubscriberType());
   			c2STransferItemVO.setTransferDate(oldC2STransferItemVO.getTransferDate());
   			c2STransferItemVO.setTransferDateTime(oldC2STransferItemVO.getTransferDateTime());
   			c2STransferItemVO.setTransferID(oldC2STransferItemVO.getTransferID());
   			c2STransferItemVO.setUserType(oldC2STransferItemVO.getUserType());
   			c2STransferItemVO.setEntryDate(oldC2STransferItemVO.getEntryDate());
   			c2STransferItemVO.setEntryDateTime(oldC2STransferItemVO.getEntryDateTime());
   			c2STransferItemVO.setPrefixID(oldC2STransferItemVO.getPrefixID());
   			c2STransferItemVO.setTransferValue(oldC2STransferItemVO.getTransferValue());
   			c2STransferItemVO.setInterfaceID(oldC2STransferItemVO.getInterfaceID());
   			c2STransferItemVO.setInterfaceType(oldC2STransferItemVO.getInterfaceType());
   			c2STransferItemVO.setServiceClass(oldC2STransferItemVO.getServiceClass());
   			c2STransferItemVO.setServiceClassCode(oldC2STransferItemVO.getServiceClassCode());
   			c2STransferItemVO.setInterfaceHandlerClass(oldC2STransferItemVO.getInterfaceHandlerClass());
   			//added by vikram
   			c2STransferItemVO.setLanguage(oldC2STransferItemVO.getLanguage());
   			c2STransferItemVO.setCountry(oldC2STransferItemVO.getCountry());
   			c2STransferItemVO.setSNo(3);
   			c2STransferItemVO.setEntryType(PretupsI.CREDIT);
   			c2STransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
   			c2STransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
   			c2STransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
   			c2STransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
   			c2STransferItemVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
   			c2STransferItemVO.setPostBalance(userBalancesVO.getBalance());
   			ArrayList itemList=new ArrayList();
   			itemList.add(0,c2STransferItemVO);
   			int addCount=new C2STransferDAO().addC2STransferItemDetails(p_con,itemList,p_c2STransferVO.getTransferID());
   			if(addCount<0)
   				throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_NOT_MAKECREDIT_ENTRY);
   			p_c2STransferVO.getTransferItemList().addAll(itemList);
   			p_c2STransferVO.setCreditAmount(p_c2STransferVO.getTransferValue());
   			p_c2STransferVO.setCreditBackStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
   			if(!PretupsI.IAT_TRANSACTION_TYPE.equals(p_c2STransferVO.getExtCreditIntfceType()))
   			{
   			    String[] messageArgArray={p_c2STransferVO.getReceiverMsisdn(),PretupsBL.getDisplayAmount(p_c2STransferVO.getRequestedAmount()),p_c2STransferVO.getTransferID(),PretupsBL.getDisplayAmount(userBalancesVO.getBalance())};
   				p_c2STransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((UserPhoneVO)((ChannelUserVO)p_c2STransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS,messageArgArray));
   			}
   			else
   			{
   			    String[] messageArgArray={p_c2STransferVO.getIatTransferItemVO().getIatRcvrCountryCode()+p_c2STransferVO.getReceiverMsisdn(),PretupsBL.getDisplayAmount(p_c2STransferVO.getRequestedAmount()),p_c2STransferVO.getTransferID(),PretupsBL.getDisplayAmount(userBalancesVO.getBalance())};
   				p_c2STransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((UserPhoneVO)((ChannelUserVO)p_c2STransferVO.getSenderVO()).getUserPhoneVO()).getLocale(),PretupsErrorCodesI.IAT_C2S_SENDER_CREDIT_SUCCESS,messageArgArray));
   			}*/
			final String[] messageArgArray = {prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): p_c2STransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_c2STransferVO.getRequestedAmount()), p_c2STransferVO
					.getTransferID(), PretupsBL.getDisplayAmount(userBalancesVO.getBalance()) };
			p_c2STransferVO.setSenderReturnMessage(BTSLUtil.getMessage(((ChannelUserVO) p_c2STransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
					PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS, messageArgArray));
		} 
		catch (BTSLBaseException be)
		{
			throw be;
		}
		catch (Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserBL[creditUserBalanceForProduct]",p_requestID,p_c2STransferVO.getSenderMsisdn(),p_c2STransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_requestID : " + p_requestID);
		}
		return userBalancesVO;
	}

	public static void  creditRoamPenalty(Connection p_con, UserBalancesVO balVO, C2STransferVO p_c2stransferVO, boolean isOwner) throws BTSLBaseException{
		if(!isOwner){
			balVO=prepareUserBalanceVOFromTransferVO(p_c2stransferVO,PretupsI.TRANSFER_TYPE_RCH_CREDIT,p_c2stransferVO.getSourceType(),PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_C2S,PretupsI.TRANSFER_CATEGORY_SALE);
		}
		else{
			balVO=prepareUserBalanceVOFromTransferVORoamOwner(p_c2stransferVO,PretupsI.TRANSFER_TYPE_RCH_CREDIT,p_c2stransferVO.getSourceType(),PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_C2S,PretupsI.TRANSFER_CATEGORY_SALE);	
		}
		// updation of the date as only date was coming not the time
		balVO.setLastTransferOn(p_c2stransferVO.getModifiedOn());

		//update user's daily balances
		int updateCount=new UserBalancesDAO().updateUserDailyBalances(p_con,p_c2stransferVO.getModifiedOn(),balVO);
		if(updateCount<=0)
			throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase",PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);

		//Debit the sender balance
		//updateCount=new UserBalancesDAO().creditUserBalances(p_con,userBalancesVO);
		updateCount=new UserBalancesDAO().creditUserBalances(p_con,balVO,p_c2stransferVO.getCategoryCode());
		if(updateCount<=0)
			throw new BTSLBaseException("ChannelUserBL", "handleReceiverAmbigousCase",PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
		if(!isOwner){
			NEW_BAL=balVO.getBalance();
		}
	}
	    public static void loadAllowedTransferForCategory(Connection con, String p_categoryCode, RequestVO p_requestVO) throws BTSLBaseException 
              {
                             final String METHOD_NAME = "loadAllowedTransferForCategory";
                             if (_log.isDebugEnabled()) {
                                           _log.debug(METHOD_NAME, p_categoryCode, "Entered p_requestVO:" + p_requestVO);
                             }
                             String transferAllowed=null;
                             try 
                             {
                                           ChannelUserDAO channelUserDAO= new ChannelUserDAO();
                                           transferAllowed=channelUserDAO.loadTransferRuleFlagForCategory(con, p_categoryCode);
                                           p_requestVO.setInfo1(transferAllowed);
                             } catch (BTSLBaseException be) {
                                           throw be;
                             } catch (Exception e) {
                                           _log.error(METHOD_NAME, "Exception " + e.getMessage());
                                           _log.errorTrace(METHOD_NAME, e);
                                           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME,"","", "", "Exception:" + e.getMessage());
                                           throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                             }
                             if (_log.isDebugEnabled()) {
                                           _log.debug(METHOD_NAME, "Exiting Allowed Transfer:" + p_requestVO.getInfo1()," Category Code:" + p_categoryCode);
                             }
              }
	    /**
		 * Method to Validate the current sequence id and user external code, passed as an argument. Creates a new sequence ID.
		 * @author birendra.mishra
		 * @param p_currentSequIdStr
		 * @param p_chnlUsrExtCode
		 * @return
		 * @throws BTSLBaseException
		 */
		public static String generateSeqId(String p_currentSequIdStr, String p_chnlUsrExtCode) throws BTSLBaseException {
			final String methodName = "generateSeqId";
			if (_log.isDebugEnabled()) 	{
				_log.debug (methodName, "Entered with p_currentSequIdStr := "+p_currentSequIdStr+", p_chnlUsrExtCode := "+p_chnlUsrExtCode);
			}
			
			int seqNumInt = 0;
			String seqNumStr = "";
			String prefixUserIdStr = "";
			int prefixLenghtLess = 0;
			int prefixLen=0;
			int suffixLen=0;
			int seqIdMaxLimit=0;
			try
			{
			prefixLen=Integer.parseInt(Constants.getProperty("C2S_SEQ_ID_PREFIX_LEN"));
			}
			catch(Exception e)
			{
				prefixLen=4;
			}
			try
			{
				suffixLen=Integer.parseInt(Constants.getProperty("C2S_SEQ_ID_SUFFIX_LEN"));
			}
			catch(Exception e)
			{
				suffixLen=7;
			}
			try
			{
				seqIdMaxLimit=Integer.parseInt(Constants.getProperty("C2S_SEQ_ID_MAX_VALUE"));
			}
			catch(Exception e)
			{
				seqIdMaxLimit=9999999;
			}
			int zeroPaddingInSuffix = suffixLen;
			
			if(BTSLUtil.isNullString(p_chnlUsrExtCode)){
				prefixLenghtLess = prefixLen;
				zeroPaddingInSuffix = zeroPaddingInSuffix + prefixLenghtLess;
				
			}else if(p_chnlUsrExtCode.length()< prefixLen){
				prefixLenghtLess = prefixLen - p_chnlUsrExtCode.length();
				zeroPaddingInSuffix = zeroPaddingInSuffix + prefixLenghtLess;
				prefixUserIdStr = p_chnlUsrExtCode.substring(0, prefixLen-prefixLenghtLess);
				
			}else{
				prefixUserIdStr = p_chnlUsrExtCode.substring(0, prefixLen);
			}
			
			if(BTSLUtil.isNullString(p_currentSequIdStr) || p_currentSequIdStr.length() != (prefixLen+ suffixLen)){
				seqNumStr="0";
			}else{
				seqNumStr = p_currentSequIdStr.substring(p_currentSequIdStr.length()-suffixLen, p_currentSequIdStr.length());
			}
				
			try{
				seqNumInt = Integer.valueOf(seqNumStr);
			}catch(NumberFormatException nfe){
				seqNumStr="0";
				_log.error(methodName,"Exception while Parsing Existing sequence ID. Exception := "+nfe.getMessage());
			}
			
			if(seqNumInt >= seqIdMaxLimit){
				seqNumInt = 0;
			}
			seqNumInt++;
			
			String sequence = String.format("%0" +String.valueOf(zeroPaddingInSuffix)+ "d", seqNumInt);
			sequence = prefixUserIdStr+sequence;
			
			if (_log.isDebugEnabled()) 	{
				_log.debug (methodName, "Exiting with sequence id := "+sequence);
			}
			return sequence;
		}
		
	public static void validateSenderTransferProfile(Connection p_con, String p_transferID,
			C2STransferVO p_c2STransferVO) throws BTSLBaseException {
		final String methodName = "validateSenderTransferProfile";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_transferID = " + p_transferID);
		}
		try {
			final TransferProfileProductVO transferProfileProductVO = TransferProfileProductCache
					.getTransferProfileDetails(((ChannelUserVO) p_c2STransferVO.getSenderVO()).getTransferProfileID(),
							p_c2STransferVO.getProductCode());
			String[] strArr = null;

			/*
			 * check that the requested quantity must be between min and max
			 * quantity allow with the user profile
			 */
			if (transferProfileProductVO.getC2sMinTxnAmtAsLong() > p_c2STransferVO.getTransferValue()
					|| transferProfileProductVO.getC2sMaxTxnAmtAsLong() < p_c2STransferVO.getTransferValue()) {
				strArr = new String[] { PretupsBL.getDisplayAmount(p_c2STransferVO.getTransferValue()),
						transferProfileProductVO.getC2sMinTxnAmt(), transferProfileProductVO.getC2sMaxTxnAmt() };
				EventHandler.handle(EventIDI.REQ_AMT_NOT_IN_RANGE, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "ChannelUserBL[validateSenderTransferProfile]", "", "", "",
						"Reques amt not in the range.");

				throw new BTSLBaseException(ChannelUserBL.class, methodName,
						PretupsErrorCodesI.CHNL_ERROR_SNDR_AMT_NOTBETWEEN_MINMAX, 0, strArr, null);
			}
		} catch (BTSLBaseException be) {
			_log.error(methodName, "BTSL Base Exception : " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception : " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserBL[validateSenderTransferProfile]", p_transferID, p_c2STransferVO.getSenderMsisdn(),
					p_c2STransferVO.getNetworkCode(), "Exception:" + e.getMessage());
			throw new BTSLBaseException(ChannelUserBL.class, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting p_transferID : " + p_transferID);
		}
	}
	
	public static ArrayList loadChannelUserForXfrWithXfrRuleForLoginID(Connection p_con,
			ChannelTransferRuleVO p_channelTransferRuleVO, String p_toCategoryCode, String p_loginID,
			ChannelUserVO p_channelUserVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("loadChannelUserForXfrWithXfrRuleForLoginID",
					"Entered p_channelTransferRuleVO=" + p_channelTransferRuleVO + ", To Category Code: " + p_toCategoryCode + " Login ID: " + p_loginID + ",p_channelTransferRuleVO=" + p_channelTransferRuleVO + ",p_channelUserVO=" + p_channelUserVO);
		}
		final ArrayList arrayList = loadUserListForXfrForLoginID(p_con, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER, p_channelTransferRuleVO, p_toCategoryCode, p_loginID,
				p_channelUserVO);
		if (_log.isDebugEnabled()) {
			_log.debug("loadChannelUserForXfrWithXfrRuleForLoginID", "Exiting:  arrayList Size =" + arrayList.size());
		}
		return arrayList;
	}

	private static ArrayList loadUserListForXfrForLoginID(Connection p_con, String p_txnType, ChannelTransferRuleVO p_channelTransferRuleVO, String p_toCategoryCode, String p_loginID, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
		ArrayList arrayList = new ArrayList();
		boolean uncontrollAllowed = false;
		boolean fixedLevelParent = false;
		boolean fixedLevelHierarchy = false;
		String fixedCatStr = null;
		boolean directAllowed = false;
		boolean chnlByPassAllowed = false;
		String unctrlLevel = null;
		String ctrlLevel = null;
		// added for user level transfer rule
		boolean isUserLevelTrfRuleAllow = false;
		String userLevelTrfRuleCode = null;
		// if txn is for transfer then get the value of the transfer paramenters
		final String METHOD_NAME = "loadUserListForXfr";
		if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
			isUserLevelTrfRuleAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, p_channelUserVO.getNetworkID(), p_channelUserVO
					.getCategoryCode())).booleanValue();
			userLevelTrfRuleCode = p_channelUserVO.getTrannferRuleTypeId();
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedTransferLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedTransferLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getDirectTransferAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getTransferChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlTransferAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlTransferLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlTransferLevel();
		}
		// else if txn is for return then get the value of the return
		// paramenters
		else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedReturnLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedReturnCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedReturnLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedReturnCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getReturnAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getReturnChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlReturnAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlReturnLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlReturnLevel();
		}
		// else if txn is for withdraw then get the value of the withdraw
		// paramenters
		else // if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnType))
		{
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedWithdrawLevel())) {
				fixedLevelParent = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
			} else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedWithdrawLevel())) {
				fixedLevelHierarchy = true;
				fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawAllowed())) {
				directAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed())) {
				chnlByPassAllowed = true;
			}
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlWithdrawAllowed())) {
				uncontrollAllowed = true;
				unctrlLevel = p_channelTransferRuleVO.getUncntrlWithdrawLevel();
			}
			ctrlLevel = p_channelTransferRuleVO.getCntrlWithdrawLevel();
		}

		// /
		// to load the user list we will have to apply the check of the fixed
		// level and fixed category in each
		// and every case.
		// Now we divide the whole conditions in various sub conditions as
		//
		// /
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		// added for user level transfer rule
		if (isUserLevelTrfRuleAllow && !BTSLUtil.isNullString(userLevelTrfRuleCode)) {
			OperatorUtilI operatorUtili = null;
			try {
				final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
				operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[loadUserListForXfr]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}
			arrayList = operatorUtili.loadUserListForTrfRuleTypeByUserLevelForLoginID(p_con, p_channelUserVO, p_toCategoryCode, p_txnType, p_loginID);
		} else {
			if (uncontrollAllowed) {
				if (PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(unctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(unctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN
						.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system without any check
						// of the fixed category
						arrayList = channelUserDAO.loadUsersOutsideHireacrhyForLoginID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_loginID, p_channelUserVO.getUserID(),
								p_txnType);
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system, which are in the
						// hierarchy of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_loginID, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system, which are in the
						// direct child of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_loginID, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// uncontrol domain check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// sender'owner hierarchy
						// without any check of the fixed category
						arrayList = channelUserDAO.loadUsersByOwnerID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_loginID,
								p_channelUserVO.getUserID(), p_txnType);
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the
						// sender'owner hierarchy
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is OWNER
						// pass value 2 for this parameter and OWNERID for the
						// p_parentUserID since here list is to be
						// loaded by owner.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the
						// sender'owner hierarchy
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is OWNER
						// pass value 2 for this parameter and OWNERID for the
						// p_parentUserID since here list is to be
						// loaded by owner.

						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// owner level uncontroll check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// sender's parent hierarchy
						// without any check of the fixed category
						arrayList = channelUserDAO.loadUsersByParentIDRecursive(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_loginID, p_channelUserVO.getUserID(), p_txnType);
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the
						// sender's parent hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is PARENT
						// pass value 1 for this parameter and PARENTID for the
						// p_parentUserID since here list is to be
						// loaded by parent.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the
						// sender's parent hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is PARENT
						// pass value 1 for this parameter and PARENTID for the
						// p_parentUserID since here list is to be
						// loaded by parent.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// parent level uncontroll check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(unctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the sender
						// hierarchy
						// without any check of the fixed category so here
						// sender's userID is passed in the calling
						// method as the parentID to load all the users under
						// sender recursively
						arrayList = channelUserDAO.loadUsersByParentIDRecursive(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_loginID, p_channelUserVO.getUserID(), p_txnType);
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the sender
						// hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is SELF but sender user
						// have to be considered as the parent of all the
						// requested users so
						// pass value 1 for this parameter and sener's userID
						// for the p_parentUserID since here list is to be
						// loaded by senderID.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the sender
						// hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// uncontroll level is SELF but sender user
						// have to be considered as the parent of all the
						// requested users so
						// pass value 1 for this parameter and sener's userID
						// for the p_parentUserID since here list is to be
						// loaded by senderID.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// Self level uncontroll check
			}// uncontrol transfer allowed check
			else {
				if (PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(ctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(ctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN
						.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// receiver domain for the direct child of the owner
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system
							// which are direct child of the owner
							// Sandeep goel ID USD001
							// method is changed to remove the problem as login
							// user is also coming in the list

							arrayList = channelUserDAO.loadUsersByDomainID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO.getToDomainCode(),
									p_loginID, p_channelUserVO.getUserID(), p_txnType);
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system
							// which are not direct child of the owner
							// Sandeep goel ID USD001
							// method is changed to remove the problem as login
							// user is also coming in the list
							arrayList.addAll(channelUserDAO.loadUsersChnlBypassByDomainID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO
									.getToDomainCode(), p_loginID, p_channelUserVO.getUserID(), p_txnType));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the sender
						// domain,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_loginID, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the sender
						// domain,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_loginID, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// domain level control check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// sender'owner hierarchy
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system within the
							// sender'owner hierarchy
							// which are direct child of the owner so here in
							// this method calling we are sending sender's
							// ownerID to considered as the parentID in the
							// method
							arrayList = channelUserDAO.loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_loginID,
									p_channelUserVO.getUserID(), p_txnType);
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system within the
							// sender'owner hierarchy
							// which are not direct child of the owner so here
							// in this method calling we are sending sender's
							// ownerID to considered as the parentID in the
							// method
							arrayList.addAll(channelUserDAO.loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
									p_loginID, p_channelUserVO.getUserID(), p_txnType));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the
						// sender's owner hierarchy
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is OWNER
						// pass value 2 for this parameter and OWNERID for the
						// p_parentUserID since here list is to be
						// loaded by owner.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the
						// sender's owner hierarchy
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is OWNER
						// pass value 2 for this parameter and OWNERID for the
						// p_parentUserID since here list is to be
						// loaded by owner.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// owner level control check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// sender's parent hierarchy
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system within the
							// sender's parent hierarchy
							// which are direct child of the parent
							arrayList = channelUserDAO.loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_loginID,
									p_channelUserVO.getUserID(), p_txnType);
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system within the
							// sender's parent hierarchy
							// which are not direct child of the parent
							arrayList.addAll(channelUserDAO.loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
									p_loginID, p_channelUserVO.getUserID(), p_txnType));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the
						// sender's parent hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is PARENT
						// pass value 1 for this parameter and PARENTID for the
						// p_parentUserID since here list is to be
						// loaded by parent.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the
						// sender's parent hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is PARENT
						// pass value 1 for this parameter and PARENTID for the
						// p_parentUserID since here list is to be
						// loaded by parent.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// parent level control check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the sender
						// hierarchy
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system within the
							// sender's hierarchy
							// which are direct child of the sender so here in
							// this method calling we are sending sender's
							// userID to considered as the parentID in the
							// method
							arrayList = channelUserDAO.loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_loginID,
									p_channelUserVO.getUserID(), p_txnType);
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system within the
							// sender's hierarchy
							// which are not direct child of the sender so here
							// in this method calling we are sending sender's
							// userID to considered as the parentID in the
							// method
							arrayList.addAll(channelUserDAO.loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
									p_loginID, p_channelUserVO.getUserID(), p_txnType));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the sender
						// hierarchy,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is SELF but sender user
						// have to be considered as the parent of all the
						// requested users so
						// pass value 1 for this parameter and sener's userID
						// for the p_parentUserID since here list is to be
						// loaded by senderID.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the sender
						// hierarchy,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is SELF but sender user
						// have to be considered as the parent of all the
						// requested users so
						// pass value 1 for this parameter and sener's userID
						// for the p_parentUserID since here list is to be
						// loaded by senderID.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(),
								p_loginID, p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// Self level control check
				else if (PretupsI.CHANNEL_TRANSFER_LEVEL_GEOGRAPHY.equals(ctrlLevel)) {
					if (BTSLUtil.isNullString(fixedCatStr)) {
						// load all the users form the system within the
						// receiver domain for the direct child of the owner
						// without any check of the fixed category
						if (directAllowed) {
							// load all the users form the system
							// which are direct child of the owner
							// Sandeep goel ID USD001
							// method is changed to remove the problem as login
							// user is also coming in the list

							arrayList = channelUserDAO.loadUsersByGeo(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO.getToDomainCode(),
									p_loginID, p_channelUserVO.getUserID());
						}// direct transfer check
						if (chnlByPassAllowed) {
							// load all the users form the system
							// which are not direct child of the owner
							// Sandeep goel ID USD001
							// method is changed to remove the problem as login
							// user is also coming in the list
							arrayList.addAll(channelUserDAO.loadUsersChnlBypassByGeo(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO
									.getToDomainCode(), p_loginID, p_channelUserVO.getUserID()));
						}// channel by pass check
						return arrayList;
					}// fixed category null check
					else if (fixedLevelHierarchy) {
						// load all the users form the system within the sender
						// domain,
						// which are in the hierarchy of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_loginID, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level hierarchy check
					else if (fixedLevelParent) {
						// load all the users form the system within the sender
						// domain,
						// which are in the direct child of the users of
						// fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this
						// parameter is 1 then check will be done
						// by parentID, if value of this parameter is 2 then
						// check will be done by ownerID
						// other wise no check will be required. So here as
						// controll level is DOMAIN OR DOMAINTYPE
						// pass value 0 for this parameter and null for the
						// p_parentUserID since here no parent and
						// no owner exist for the DOMAIN OR DOMAINTYPE level.
						arrayList = channelUserDAO.loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_loginID, p_channelUserVO
								.getUserID(), fixedCatStr, 0, p_txnType);
						return arrayList;
					}// fixed level parent check
				}// Geography level control check
			}// control transaction check
		}
		if (_log.isDebugEnabled()) {
			_log.debug("loadUserListForXfr", "Exited userList.size() = " + arrayList.size());
		}
		return arrayList;
	}
	
}
