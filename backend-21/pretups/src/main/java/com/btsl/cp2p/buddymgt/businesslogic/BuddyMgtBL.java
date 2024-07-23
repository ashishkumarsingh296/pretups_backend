package com.btsl.cp2p.buddymgt.businesslogic;

import java.sql.Connection;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class BuddyMgtBL {

    private static Log log = LogFactory.getLog(SubscriberBL.class.getName());

    /**
	 * ensures no instantiation
	 */
    private BuddyMgtBL(){
    	
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
    public static void validateBuddyName(String p_buddyName, int p_allowedNameLength, SenderVO p_senderVO) throws BTSLBaseException {

        if (log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_buddyName:");
        	msg.append(p_buddyName);
        	msg.append(" Sender MSISDN=");
        	msg.append(p_senderVO.getMsisdn());
        	msg.append(" p_sender Network =");
        	msg.append(p_senderVO.getNetworkCode());
        	
        	String message=msg.toString();
            log.debug("validateBuddyName", message);
        }
        
        final String METHOD_NAME = "validateBuddyName";
        try {
            if (BTSLUtil.isNullString(p_buddyName))
                throw new BTSLBaseException("BuddyMgtBL", "validateBuddyName", PretupsErrorCodesI.ERROR_BUDDY_NAME_MANDATORY);

            p_buddyName = p_buddyName.trim();

            if (p_buddyName.length() > p_allowedNameLength)
                throw new BTSLBaseException("BuddyMgtBL", "validateBuddyName", PretupsErrorCodesI.ERROR_BUDDY_NAME_EXCEED_LENGTH, 0, new String[] { p_buddyName, String.valueOf(p_allowedNameLength) }, null);

            if (p_buddyName.indexOf("%") != -1 || p_buddyName.indexOf("^") != -1 || p_buddyName.indexOf(" ") != -1 || p_buddyName.indexOf("(") != -1 || p_buddyName.indexOf(")") != -1 || p_buddyName.indexOf("~") != -1 || p_buddyName.indexOf("$") != -1 || p_buddyName.indexOf("\"") != -1 || p_buddyName.indexOf("@") != -1 || p_buddyName.indexOf("+") != -1 || p_buddyName.indexOf(",") != -1)
                throw new BTSLBaseException("BuddyMgtBL", "validateBuddyName", "p2p.buddmgt.add.buddy.name.not.valid", "loadAddBuddyJsp");
        } catch (BTSLBaseException be) {
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AddBuddyController[validateBuddyName]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("BuddyMgtBL", "validateBuddyName", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled())
                log.debug("validateBuddyName", "Exiting ");
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
     */
    public static long validateAndCheckBuddyInSameNetwork(Connection p_con, String p_filteredMSISDN, SenderVO p_senderVO) throws BTSLBaseException {
        if (log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered filteredMSISDN:");
        	msg.append(p_filteredMSISDN);
        	msg.append(" Sender MSISDN=");
        	msg.append(p_senderVO.getMsisdn());
        	msg.append(" p_sender Network =");
        	msg.append(p_senderVO.getNetworkCode());
        	
        	String message=msg.toString();
            log.debug("validateAndCheckBuddyInSameNetwork", message);
        }
        
        final String METHOD_NAME = "validateAndCheckBuddyInSameNetwork";
        NetworkPrefixVO networkPrefixVO = null;
        try {
            String msisdnPrefix = PretupsBL.getMSISDNPrefix(p_filteredMSISDN);
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            NumberPortDAO numberPortDAO = new NumberPortDAO();
            if (networkPrefixVO == null)
                throw new BTSLBaseException("BuddyMgtBL", "validateAndCheckBuddyInSameNetwork", "p2p.user.addbuddy.buddy.network.notfound", 0, new String[] { p_filteredMSISDN }, "loadAddBuddyJsp");
            if (!networkPrefixVO.getNetworkCode().equals(p_senderVO.getNetworkCode()))
                throw new BTSLBaseException("BuddyMgtBL", "validateAndCheckBuddyInSameNetwork", "p2p.user.addbuddy.buddymsisdn.diff.network", "loadAddBuddyJsp");

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                boolean numberAllowed = false;
                if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = numberPortDAO.isExists(p_con, p_filteredMSISDN, "", PretupsI.PORTED_IN);
                    if (!numberAllowed)
                        throw new BTSLBaseException("BuddyMgtBL", "validateAndCheckBuddyInSameNetwork", "p2p.user.addbuddy.buddy.network.notfound", 0, new String[] { p_filteredMSISDN }, "loadAddBuddyJsp");
                } else {
                    numberAllowed = numberPortDAO.isExists(p_con, p_filteredMSISDN, "", PretupsI.PORTED_OUT);
                    if (numberAllowed)
                        throw new BTSLBaseException("BuddyMgtBL", "validateAndCheckBuddyInSameNetwork", "p2p.user.addbuddy.buddy.network.notfound", 0, new String[] { p_filteredMSISDN }, "loadAddBuddyJsp");
                }
            }
            return networkPrefixVO.getPrefixID();
        } catch (BTSLBaseException be) {
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "validateAndCheckBuddyInSameNetwork", "", p_filteredMSISDN, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("BuddyMgtBL", "validateAndCheckBuddyInSameNetwork", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            if (log.isDebugEnabled())
                log.debug("validateAndCheckBuddyInSameNetwork", "Exiting ");
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
    public static BuddyVO prepareBuddyVO(String p_buddyName, String p_buddyMsisdn, String p_amount, long p_buddyPrefixID, Date p_currentDate, SenderVO p_senderVO) throws BTSLBaseException {
        if (log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_buddyName:");
        	msg.append(p_buddyName);
        	msg.append(" p_buddyMsisdn=");
        	msg.append(p_buddyMsisdn);
        	msg.append(" p_amount=");
        	msg.append(p_amount);
        	msg.append(" Sender MSISDN=");
        	msg.append(p_senderVO.getMsisdn());
        	msg.append(" p_sender Network =");
        	msg.append(p_senderVO.getNetworkCode());
        	
        	String message=msg.toString();
            log.debug("prepareBuddyVO;", message);
        }
        
        final String METHOD_NAME = "prepareBuddyVO";
        BuddyVO buddyVO = null;
        try {
            buddyVO = new BuddyVO();
            buddyVO.setName(p_buddyName);
            buddyVO.setMsisdn(p_buddyMsisdn);
            buddyVO.setOwnerUser(p_senderVO.getUserID());
            if (!BTSLUtil.isNullString(p_amount)) {
                buddyVO.setPreferredAmount(PretupsBL.getSystemAmount(p_amount));
            }
            buddyVO.setSeqNumber((p_senderVO.getBuddySeqNumber() + 1));
            buddyVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            buddyVO.setPrefixID(p_buddyPrefixID);
            buddyVO.setCreatedOn(p_currentDate);
            buddyVO.setModifiedOn(p_currentDate);
            buddyVO.setModifiedBy(p_senderVO.getUserID());
            buddyVO.setCreatedBy(p_senderVO.getUserID());
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "prepareBuddyVO", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("BuddyMgtBL", "prepareBuddyVO", "p2p.user.addbuddy.failed",e);
        }
        return buddyVO;
    }

    /**
     * Method to return the diaplayAmount
     * 
     * @param p_databaseAmount
     * @throws BTSLBaseException
     */
    public static long getDisplayAmount(long p_databaseAmount) throws BTSLBaseException {
        if (log.isDebugEnabled())
            log.debug("getDisplayAmount", "Entered databaseAmount:" + p_databaseAmount);
        final String METHOD_NAME = "getDisplayAmount";
        long amount = 0;
        int multiplicationFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
        try {
            amount = Math.abs(p_databaseAmount / multiplicationFactor);
        } catch (Exception bex) {
            log.errorTrace(METHOD_NAME, bex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "getDisplayAmount", "", "", "", "Exception:" + bex.getMessage());
            throw new BTSLBaseException("BuddyMgtBL", "getDisplayAmount", "p2p.user.addbuddy.failed",bex);
        }
        return amount;
    }

    /**
     * Method to return the diaplayAmount
     * 
     * @param p_databaseAmount
     * @throws BTSLBaseException
     */
    public static double getDisplayAmountAsDouble(double p_databaseAmount) throws BTSLBaseException {
        if (log.isDebugEnabled())
            log.debug("getDisplayAmount", "Entered databaseAmount:" + p_databaseAmount);
        final String METHOD_NAME = "getDisplayAmountAsDouble";
        double amount = 0;
        int multiplicationFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
        try {
            amount = Math.abs(p_databaseAmount / multiplicationFactor);
        } catch (Exception bex) {
            log.errorTrace(METHOD_NAME, bex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "getDisplayAmount", "", "", "", "Exception:" + bex.getMessage());
            throw new BTSLBaseException("BuddyMgtBL", "getDisplayAmountAsDouble", "p2p.user.addbuddy.failed",bex);
        }
        return amount;
    }

}
