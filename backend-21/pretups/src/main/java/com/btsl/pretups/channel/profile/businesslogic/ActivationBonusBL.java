package com.btsl.pretups.channel.profile.businesslogic;

/**
 * @(#)ActivationBonusBL.java
 *                            Copyright(c) 2009, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Amit Singh 18/2/2009 Initial Creation
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ActivationBonusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * @author 
 *
 */
public class ActivationBonusBL {

    private static Log log = LogFactory.getLog(ActivationBonusBL.class.getName());

  
    /**
     * 
     */
    public ActivationBonusBL() {
        super();
    }

    /**
     * This method returns the list of profile from activationBonusDAO which
     * populates the list from profile_set table
     * also we set a none entry in profile list.
     * 
     * @param pCon
     *            Connection
     * @param pNetworkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author amit.singh
     */

    public ArrayList loadProfileSetVO(Connection pCon, String pNetworkCode) throws BTSLBaseException {
    	  final String methodName = "loadProfileSetVO";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
      
        ArrayList profileList = null;
        profileList = new ArrayList();
        ProfileSetVO profileSetVO = null;
        try {
            profileSetVO = new ProfileSetVO();
            profileSetVO.setProfileType(PretupsI.PROFILE_TYPE_ACTIVATION_BONUS);
           
            final ProfileDAO profileDAO = new ProfileDAO();
            profileList = profileDAO.loadProfileList(pCon, pNetworkCode, profileList);
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusBL[loadProfileSetVO]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited  ");
            }
        }
        return profileList;
    }

    /**
     * Method to convert points into transferable amount according to profile
     * type.
     * 
     * @author chetan.kothari
     * @param pBonusVO
     * @return double
     * @throws BTSLBaseException
     */
    public static double convertPointsToAmount(ActivationBonusVO pBonusVO) throws BTSLBaseException {
        double amount = 0;
        final String methodName = "convertPointsToAmount";
        try {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Entered pBonusVO : " + pBonusVO);
            }
            if (pBonusVO.getProfileType().equals(PretupsI.PROFILE_TYPE_ACTIVATION)) {
                amount = pBonusVO.getPointsToRedeem() * ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.POINT_CONVERSION_FACTOR))).intValue();
            }
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusRedemption[convertPointsToAmount]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption",methodName, "An error has occured in Activation Bonus Redemption process  cannot continue.");
        }// end of catch
        finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting convertedAmount:" + amount);
            }
        }// end of finally
        return amount;
    }

    /**
     * This method populates the map from the list of records retrieved from the
     * database with addition , deletion and correction list as values
     * and addition, deletion and correction as keys.
     * 
     * @param map
     *            HashMap
     * @param p_list
     *            ArrayList
     * @param listOfRetailerSubsMappingVO
     *            ArrayList
     * @return void
     * @author amit.singh
     */
    public void loadAddDeleteCorrectionList(HashMap map, ArrayList p_list, ArrayList listOfRetailerSubsMappingVO) {
        ArrayList addList = null;
        ArrayList delList = null;
        ArrayList correctList = null;
        int y = 0;
        int a = 0;
        int c = 0;
        RetSubsMappingVO retSubsMappingVO = null;
        String str[] = null;
        str = new String[p_list.size()];
        String str1;
        for (int i = 0, p = p_list.size(); i < p; i++) {
            retSubsMappingVO = (RetSubsMappingVO) p_list.get(i);
            str1 = retSubsMappingVO.getSubscriberMsisdn();
            if (i == 0) {
                str[i] = str1;
            }
            y = 0;
            while (!BTSLUtil.isNullString(str[y])) {
                if (str1.equalsIgnoreCase(str[y])) {
                    break;
                }
                y++;
                if (BTSLUtil.isNullString(str[y])) {
                    str[y] = str1;
                }
            }
        }
        addList = new ArrayList();
        delList = new ArrayList();
        correctList = new ArrayList();
        int j = 0;
        if (!p_list.isEmpty()) {
            while (j < str.length && !BTSLUtil.isNullString(str[j])) {
                a = 0;
                c = 0;
                int lists=p_list.size();
                for (int i = 0; i <lists ; i++) {
                    retSubsMappingVO = (RetSubsMappingVO) p_list.get(i);
                    if ((str[j].equalsIgnoreCase(retSubsMappingVO.getSubscriberMsisdn())) && ("W".equalsIgnoreCase(retSubsMappingVO.getStatus()))) {
                        a++;
                    } else if (str[j].equalsIgnoreCase(retSubsMappingVO.getSubscriberMsisdn()) && ("S".equalsIgnoreCase(retSubsMappingVO.getStatus()))) {
                        c++;
                    }
                }
                if (a == 1 && c == 0) {
                    for (int l = 0; l < p_list.size(); l++) {
                        retSubsMappingVO = (RetSubsMappingVO) p_list.get(l);
                        if (retSubsMappingVO.getSubscriberMsisdn().equalsIgnoreCase(str[j]) && "W".equalsIgnoreCase(retSubsMappingVO.getStatus())) {
                            // retSubsMappingVO.setAllowAction(retSubsMappingVO.getSubscriberMsisdn()+","+retSubsMappingVO.getRetailerMsisdn()+","+"add");
                            listOfRetailerSubsMappingVO.add(retSubsMappingVO);
                            addList.add(retSubsMappingVO);
                            break;
                        }
                    }
                } else if (a == 0 && c == 1) {
                    for (int l = 0; l < p_list.size(); l++) {
                        retSubsMappingVO = (RetSubsMappingVO) p_list.get(l);
                        if (retSubsMappingVO.getSubscriberMsisdn().equalsIgnoreCase(str[j]) && "S".equalsIgnoreCase(retSubsMappingVO.getStatus())) {
                            // retSubsMappingVO.setAllowAction(retSubsMappingVO.getSubscriberMsisdn()+","+retSubsMappingVO.getRetailerMsisdn()+","+"del");
                            listOfRetailerSubsMappingVO.add(retSubsMappingVO);
                            delList.add(retSubsMappingVO);
                            break;
                        }
                    }

                } else if (a == 1 && c == 1) {
                    for (int l = 0; l < p_list.size(); l++) {
                        retSubsMappingVO = (RetSubsMappingVO) p_list.get(l);
                        if (retSubsMappingVO.getSubscriberMsisdn().equalsIgnoreCase(str[j])) {
                            correctList.add(retSubsMappingVO);
                        }
                    }
                }
                j++;
            }
        }
        map.put("addList", addList);
        map.put("delList", delList);
        map.put("correctList", correctList);
    }

    /**
     * ankit.singhal
     * modified by rahul.dutt
     * added to update the subscriber retailer's previous mapping and add new
     * mapping with new retailer
     * 
     * @param pCon
     * @param pRetSubsMappingList
     * @param pNewRetailerID
     * @param pUserID
     * @return
     * @throws BTSLBaseException
     */
    public int updateSubsRetMapping(Connection pCon, ArrayList pRetSubsMappingList, String pNewRetailerID, String pUserID, Date pCurrentDate, String pType) throws BTSLBaseException {
    	 final String methodName = "updateSubsRetMapping";
    	 StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append( "convertPointsToAmount");
    		loggerValue.append( "Entered pRetSubsMappingList : ");
    		loggerValue.append (pRetSubsMappingList);
    		loggerValue.append ( "pRetSubsMappingList.size() : ");
    		loggerValue.append ( pRetSubsMappingList.size());
    		loggerValue.append ( " pNewRetailerID : " );
    		loggerValue.append ( pNewRetailerID );
    		loggerValue.append ( "pUserID : " );
    		loggerValue.append ( pUserID );
    		loggerValue.append ( "pCurrentDate :" );
    		loggerValue.append ( pCurrentDate );
    		loggerValue.append ( "pType" );
    		loggerValue.append ( pType );
            log.debug(methodName,loggerValue);
        }
       
        ActivationBonusDAO activationBonusDAO = null;
        RetSubsMappingVO retSubsMappingVO = null;
        long noOfSubs = 0;
        int insertUpdateCount = 0;
        int listSize = 0;
        try {
            activationBonusDAO = new ActivationBonusDAO();
            if (pRetSubsMappingList != null) {
                listSize = pRetSubsMappingList.size();
            }
            if (!"add".equals(pType)) {
                if (pRetSubsMappingList != null && !pRetSubsMappingList.isEmpty()) {
                    retSubsMappingVO = (RetSubsMappingVO) pRetSubsMappingList.get(0);
                    insertUpdateCount = activationBonusDAO.updateSubsMapping(pCon, retSubsMappingVO.getSubscriberMsisdn(), pUserID);
                }
            }
            insertUpdateCount = activationBonusDAO.insertSubsMapping(pCon, pRetSubsMappingList, pNewRetailerID, pUserID);
            retSubsMappingVO = activationBonusDAO.loadMappingSummary(pCon, pNewRetailerID, false, BTSLUtil.getDateStringFromDate(pCurrentDate), BTSLUtil
                .getDateStringFromDate(pCurrentDate));

            if (retSubsMappingVO != null) {
                insertUpdateCount = 0;
                noOfSubs = retSubsMappingVO.getNoOfActivatedSubs();
                if (noOfSubs > 0) {
                    insertUpdateCount = activationBonusDAO.updateMappingSummary(pCon, noOfSubs + listSize, pNewRetailerID);
                } else {
                    insertUpdateCount = activationBonusDAO.insertMappingSummary(pCon, pNewRetailerID, listSize);
                }
            }

        } catch (Exception e) {
        	loggerValue.setLength(0);
    		loggerValue.append( "Exception: " );
    		loggerValue.append(e.getMessage() );
            log.error("updateSubsRetMapping",  loggerValue );
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append( "Exception:" );
            loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusBL[updateSubsMapping]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "updateSubsRetMapping", "error.general.processing");
        } // end of catch
        finally {
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
        		loggerValue.append( "Exiting: insertUpdateCount" );
        		loggerValue.append( insertUpdateCount );
                log.debug("updateSubsRetMapping",  loggerValue );
            }
        } // end of finally
        return insertUpdateCount;
    }

    /**
     * This method is used to CHECK same MSISDN exist in MSISDN String
     * 
     * @param pMsisdn
     * @return boolean value true or false
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public boolean compareMsisdnFromString(String pMsisdn) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("convertPointsToAmount", "Entered pMsisdn : " + pMsisdn);
        }
        final String methodName = "compareMsisdnFromString";
        boolean isFound = false;
        final StringTokenizer tokenizer = new StringTokenizer(pMsisdn, ",");
        StringBuilder sameMsisdnBuff = null;
        String msisdn = null;
        String filteredMsisdn = null;
        String previousMsisdn = null;
        try {
            sameMsisdnBuff = new StringBuilder();
            while (tokenizer.hasMoreTokens()) {
                msisdn = tokenizer.nextToken().trim();
                filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn); //
                if (filteredMsisdn.equalsIgnoreCase(previousMsisdn)) {
                    sameMsisdnBuff.append(filteredMsisdn);
                }
                previousMsisdn = filteredMsisdn;
            }
            if (sameMsisdnBuff.toString() != null) {
                isFound = true;
            }
            if ("".equals(sameMsisdnBuff.toString())) {

                return false;
            }
        }// END OF TRY BLOCK
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusBL[compareMsisdnFromString]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: isFound" + isFound);
            }
        } // end of finally
        return isFound;
    }

    /**
     * this method is used to check whether same Msisdn exist in Msisdn comma
     * seprated string
     * 
     * @param pMsisdn
     * @return boolean value
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public boolean isSameMsisdnContains(String pMsisdn) throws BTSLBaseException

    {
    	final String methodName = "isSameMsisdnContains";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered pMsisdn::=" + pMsisdn);
        }
        
        StringTokenizer tokenizer = null;
        String msisdn = null;
        ArrayList tempList = null;
        String stri = null;
        String strj = null;
        boolean isExist = false;
        int listSize = 0;
        try {
            tokenizer = new StringTokenizer(pMsisdn, ",");
            tempList = new ArrayList();
            stri = new String();
            strj = new String();
            while (tokenizer.hasMoreTokens()) {
                msisdn = tokenizer.nextToken().trim();
                // filteredMsisdn =
                
                tempList.add(msisdn);
            }
            listSize = tempList.size();
            if (listSize > 1) {
                for (int i = 0; i <= listSize; i++) {
                    for (int j = i + 1; j < listSize; j++) {
                        stri = (String) tempList.get(i);
                        strj = (String) tempList.get(j);
                        if (stri.equalsIgnoreCase(strj)) {
                            isExist = true;
                            break;
                        }
                    }
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusBL[isSameMsisdnContains]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: isExist : :" + isExist);
            }
        } // end of finally
        return isExist;
    }
}