/**
 * @(#)AddBuddyController.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *                             Controller for handling add buddy request for a
 *                             subscriber
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan Mar 23, 2005 Initital Creation
 *                             Gurjeet Singh Bedi 26/06/06 Modified
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

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
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * @author avinash.kamthan
 */
public class AddBuddyController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(AddBuddyController.class.getName());
    private static OperatorUtilI _operatorUtil = null;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " AddBuddyController [initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    @Override
	public void process(RequestVO p_requestVO) {
    	final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered Request ID");
        	loggerValue.append(p_requestVO.getRequestID());
        	loggerValue.append(" Msisdn=");
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
        	_log.debug(METHOD_NAME,loggerValue );
        }
        
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            // <Keyword> <Name> <MSISDN> <Preferred Amount> <PIN>
            // <Keyword> <Name> <MSISDN> <Preferred Amount>
            String name = null;
            String msisdn = null;
            String amount = null;
            final String[] args = p_requestVO.getRequestMessageArray();
            final String actualPin = senderVO.getPin();
            final int messageLength = args.length;
            String filderedMSISDN = null;

            final int NAME_ALLOWED_LENGTH = 30;// Max Length of the Buddy Name

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            final SubscriberDAO subscriberDAO = new SubscriberDAO();
            final P2PBuddiesDAO p2PBuddiesDAO = new P2PBuddiesDAO();
            final ArrayList buddyList = p2PBuddiesDAO.loadBuddyList(con, senderVO.getUserID());
            final long allowedBuddySize = ((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.MAX_NO_OF_BUDDIES_ALLOWED, senderVO.getNetworkCode())).longValue();
            if (_log.isDebugEnabled()) {
                _log.debug(this, " BuddList Size=" + buddyList.size() + " Max No of Buddies Allowed=" + allowedBuddySize);
            }
            if (buddyList.size() >= allowedBuddySize) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_MAX_NO_OF_ALLOWED_BUDDY_RCHD, 0, new String[] { String.valueOf(allowedBuddySize) }, null);
            }
            switch (messageLength) {
            /*case (PretupsI.MESSAGE_LENGTH_ADD_BUDDY - 1): {
                if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin))) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_ADDBUDDY_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
                }
                name = args[1];
                msisdn = args[2];
                amount = args[3];
                break;
            }*/
            case (PretupsI.MESSAGE_LENGTH_ADD_BUDDY): {
                if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[4]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                name = args[1];
                msisdn = args[2];
                amount = args[3];
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_ADDBUDDY_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }

            // check whether name is null or its length should not be more
            // than 30 charactes and all character should not be numeric
            validateBuddyName(name, NAME_ALLOWED_LENGTH, senderVO);

            // to check the buddy MSISDN user sends is numeric or not
            if (BTSLUtil.isNullString(msisdn)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.INVALID_MSISDN_NULL);
            }

            // TO CHECK THE BUDDY WHICH WILL BE ADDED in his
            // buddy list, Is BELONGS TO SAME NETWORK
            name = name.trim();

            filderedMSISDN = PretupsBL.getFilteredMSISDN(msisdn.trim());
            filderedMSISDN = _operatorUtil.addRemoveDigitsFromMSISDN(filderedMSISDN);
            if (!BTSLUtil.isValidMSISDN(filderedMSISDN)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_MSISDN,new String[]{filderedMSISDN});
            }

            final long buddyPrefixID = validateAndCheckBuddyInSameNetwork(con, filderedMSISDN, senderVO);

            // to check amount is numeric
            PretupsBL.isValidAmount(p_requestVO.getRequestIDStr(), amount, true, 10);

            // to check name is already exist or not
            final BuddyVO buddyVOExist = p2PBuddiesDAO.subscriberBuddyExist(con, senderVO.getUserID(), name, filderedMSISDN);
            if (buddyVOExist != null) {
                if (name.equalsIgnoreCase(buddyVOExist.getName())) {
                    final String msgargs[] = { name };
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BUDDY_NAME_ALREADYEXIST, 0, msgargs, null);
                } else if (msisdn.equals(buddyVOExist.getMsisdn())) {
                    final String msgargs[] = { msisdn };
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BUDDY_MSISDN_ALREADYEXIST, 0, msgargs, null);
                }
            }

            // if all check is fine then add the buddy with requested user

            final Date currentDate = new Date();

            final BuddyVO buddyVO = prepareBuddyVO(name, filderedMSISDN, amount, buddyPrefixID, currentDate, senderVO);

            // Adding Buddy
            senderVO.setBuddySeqNumber(senderVO.getBuddySeqNumber() + 1);
            senderVO.setModifiedOn(currentDate);
            senderVO.setModifiedBy(senderVO.getUserID());
            int status = p2PBuddiesDAO.addBuddy(con, buddyVO);
            if (status > 0) {
                status = 0;
                status = subscriberDAO.updateSubscriberBuddySequenceNum(con, senderVO);
                if (status > 0) {
                    try {
                        if (con != null) {
                        	mcomCon.finalCommit();
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                    final String msgArr[] = { buddyVO.getName(), buddyVO.getMsisdn(), String.valueOf(buddyList.size() + 1) };
                    p_requestVO.setMessageArguments(msgArr);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.ADD_BUDDY_SUCCESS);
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ADD_BUDDY_FAILED);
                }
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ADD_BUDDY_FAILED);
            }

        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ADD_BUDDY_FAILED);
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AddBuddyController[process]", "", "", "",
                "Getting Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("AddBuddyController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * Method to validate Buddy MSISDN and also checks it should belong in the
     * same sender Network
     * 
     * @param p_filteredMSISDN
     * @param p_senderVO
     * @return Prefix ID of the buddy
     * @throws BTSLBaseException
     *             21/04/07 : Added p_con for MNP
     */
    public long validateAndCheckBuddyInSameNetwork(Connection p_con, String p_filteredMSISDN, SenderVO p_senderVO) throws BTSLBaseException {
    	final String METHOD_NAME = "validateAndCheckBuddyInSameNetwork";
    	StringBuilder loggerValue= new StringBuilder();
    	loggerValue.setLength(0);
    	loggerValue.append("Entered filteredMSISDN:");
    	loggerValue.append(p_filteredMSISDN);
    	loggerValue.append(" Sender MSISDN=");
    	loggerValue.append(p_senderVO.getMsisdn());
    	loggerValue.append(" p_sender Network =");
    	loggerValue.append(p_senderVO.getNetworkCode());
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME,
            		loggerValue);
        }
        
        NetworkPrefixVO networkPrefixVO = null;
        try {
            final String msisdnPrefix = PretupsBL.getMSISDNPrefix(p_filteredMSISDN);
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            final NumberPortDAO numberPortDAO = new NumberPortDAO();
            if (networkPrefixVO == null) {
                throw new BTSLBaseException(this, "validateAndCheckBuddyInSameNetwork", PretupsErrorCodesI.ERROR_BUDDY_NETWORK_NOTFOUND, 0, new String[] { p_filteredMSISDN },
                    null);
            }
            if (!networkPrefixVO.getNetworkCode().equals(p_senderVO.getNetworkCode())) {
                throw new BTSLBaseException(this, "validateAndCheckBuddyInSameNetwork", PretupsErrorCodesI.BUDDY_NAME_FROM_DIFF_NETWORK);
            }
            /*
             * 21/04/07 Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If yes
             * then throw error, else continue
             */
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                boolean numberAllowed = false;
                if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = numberPortDAO.isExists(p_con, p_filteredMSISDN, "", PretupsI.PORTED_IN);
                    if (!numberAllowed) {
                        throw new BTSLBaseException(this, "validateAndCheckBuddyInSameNetwork", PretupsErrorCodesI.ERROR_BUDDY_NETWORK_NOTFOUND, 0,
                            new String[] { p_filteredMSISDN }, null);
                    }
                } else {
                    numberAllowed = numberPortDAO.isExists(p_con, p_filteredMSISDN, "", PretupsI.PORTED_OUT);
                    if (numberAllowed) {
                        throw new BTSLBaseException(this, "validateAndCheckBuddyInSameNetwork", PretupsErrorCodesI.ERROR_BUDDY_NETWORK_NOTFOUND, 0,
                            new String[] { p_filteredMSISDN }, null);
                    }
                }
            }
            // 21/04/07: MNP Code End
            return networkPrefixVO.getPrefixID();
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AddBuddyController[validateAndCheckBuddyInSameNetwork]", "", p_filteredMSISDN, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateAndCheckBuddyInSameNetwork", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validateAndCheckBuddyInSameNetwork", "Exiting ");
            }
        }
    }

    /**
     * Method to validate the buddy name. It does following checks
     * 1) Not Null
     * 2) Length should be less than allowed
     * 3) Should not have any special characters like %,^, ,(,),~,$,",@,+,,,
     * 
     * @param p_buddyName
     * @param p_allowedNameLength
     * @param p_senderVO
     * @throws BTSLBaseException
     */
    public void validateBuddyName(String p_buddyName, int p_allowedNameLength, SenderVO p_senderVO) throws BTSLBaseException {
    	 final String METHOD_NAME = "validateBuddyName";
    	 StringBuilder loggerValue= new StringBuilder();
    	 loggerValue.setLength(0);
    	 loggerValue.append("Entered p_buddyName:");
    	 loggerValue.append(p_buddyName);
    	 loggerValue.append(" Sender MSISDN=");
    	 loggerValue.append(p_senderVO.getMsisdn());
    	 loggerValue.append( " p_sender Network =");
    	 loggerValue.append(p_senderVO);
    	 
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, loggerValue);
        }
       
        try {
            if (BTSLUtil.isNullString(p_buddyName)) {
                throw new BTSLBaseException(this, "validateBuddyName", PretupsErrorCodesI.ERROR_BUDDY_NAME_MANDATORY);
            }

            p_buddyName = p_buddyName.trim();

            if (p_buddyName.length() > p_allowedNameLength) {
                throw new BTSLBaseException(this, "validateBuddyName", PretupsErrorCodesI.ERROR_BUDDY_NAME_EXCEED_LENGTH, 0, new String[] { p_buddyName, String
                    .valueOf(p_allowedNameLength) }, null);
            }

            if (p_buddyName.indexOf("%") != -1 || p_buddyName.indexOf("^") != -1 || p_buddyName.indexOf(" ") != -1 || p_buddyName.indexOf("(") != -1 || p_buddyName
                .indexOf(")") != -1 || p_buddyName.indexOf("~") != -1 || p_buddyName.indexOf("$") != -1 || p_buddyName.indexOf("\"") != -1 || p_buddyName.indexOf("@") != -1 || p_buddyName
                .indexOf("+") != -1 || p_buddyName.indexOf(",") != -1 ||p_buddyName.indexOf(".") != -1) {
                throw new BTSLBaseException(this, "validateBuddyName", PretupsErrorCodesI.ERROR_BUDDY_NAME_SP_CHARACTERS);
            }
            
            if(!BTSLUtil.isAlphaNumeric(p_buddyName)){
            	throw new BTSLBaseException(this, "validateBuddyName", PretupsErrorCodesI.ERROR_INVALID_BUDDY_NAME);
            }

        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AddBuddyController[validateBuddyName]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateBuddyName", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validateBuddyName", "Exiting ");
            }
        }
    }

    /**
     * Method to prepare the Buddy VO
     * 
     * @param p_buddyName
     * @param p_buddyMsisdn
     * @param p_amount
     * @param p_buddyPrefixID
     * @param p_currentDate
     * @param p_senderVO
     * @return
     * @throws BTSLBaseException
     */
    public BuddyVO prepareBuddyVO(String p_buddyName, String p_buddyMsisdn, String p_amount, long p_buddyPrefixID, Date p_currentDate, SenderVO p_senderVO) throws BTSLBaseException {
    	final String METHOD_NAME = "prepareBuddyVO";
    	if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_buddyName:");
        	loggerValue.append(p_buddyName);
        	loggerValue.append(" p_buddyMsisdn=");
        	loggerValue.append(p_buddyMsisdn);
        	loggerValue.append(" p_amount=");
        	loggerValue.append(p_amount);
        	loggerValue.append(" Sender MSISDN=");
        	loggerValue.append(p_senderVO
                    .getMsisdn());
        	loggerValue.append(" p_sender Network =");
        	loggerValue.append(p_senderVO.getNetworkCode());
            _log.debug(METHOD_NAME, loggerValue);
        }
        
        BuddyVO buddyVO = null;
        try {
            buddyVO = new BuddyVO();
            buddyVO.setName(p_buddyName);
            buddyVO.setMsisdn(p_buddyMsisdn);
            buddyVO.setOwnerUser(p_senderVO.getUserID());
            buddyVO.setPreferredAmount(PretupsBL.getSystemAmount(p_amount));
            buddyVO.setSeqNumber((p_senderVO.getBuddySeqNumber() + 1));
            buddyVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            buddyVO.setPrefixID(p_buddyPrefixID);
            buddyVO.setCreatedOn(p_currentDate);
            buddyVO.setModifiedOn(p_currentDate);
            buddyVO.setModifiedBy(p_senderVO.getUserID());
            buddyVO.setCreatedBy(p_senderVO.getUserID());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AddBuddyController[prepareBuddyVO]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "prepareBuddyVO", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
        return buddyVO;
    }
}
