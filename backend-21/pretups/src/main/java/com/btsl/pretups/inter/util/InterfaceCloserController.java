package com.btsl.pretups.inter.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.util.Constants;

/**
 * @InterfaceCloserController.java
 *                                 Copyright(c) 2007, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Ashish Kumar Mar 16, 2007 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 *                                 This class would be responsible for the
 *                                 following activity
 *                                 1. Maintains the synchronized data
 *                                 structure(Hash table).
 *                                 2.
 *                                 3.
 */
public class InterfaceCloserController {
    private static Log _log = LogFactory.getLog(InterfaceCloserController.class.getName());
    public static Hashtable _interfaceCloserVOTable = new Hashtable();// Contains
                                                                      // the
                                                                      // list of
                                                                      // free
                                                                      // client
                                                                      // objects,associated
                                                                      // with
                                                                      // interface
                                                                      // id.

    /**
     * This method instantiate the InterfaceCloserMap at the server start-up.
     * 
     * @param p_interfaceIDs
     * @throws BTSLBaseException
     */
    public static void initialize(String p_interfaceIDs) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered p_interfaceIDs::" + p_interfaceIDs);
        String interfaceId = null;
        InterfaceCloserVO interfaceCloserVO = null;
        String[] inStrArray = null;
        try {
            inStrArray = p_interfaceIDs.split(",");
            if (InterfaceUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            HashMap interfaceVOMap = interfaceDAO.loadInterfaceByID();

            for (int i = 0, size = inStrArray.length; i < size; i++) {
                InterfaceVO interfaceVO = null;
                // Create a list in which store the client object equal to
                // configured number.
                interfaceId = inStrArray[i].trim();
                try {

                    interfaceCloserVO = new InterfaceCloserVO();
                    interfaceVO = (InterfaceVO) interfaceVOMap.get(interfaceId);
                    _log.error("InterfaceCloserController[initialize]", "interfaceVO.toString " + interfaceVO.toString());

                    if ("N".equals(interfaceVO.getStatusCode()))
                        continue;

                    // Fetch the number of ambiguous transaction allowed for
                    // certain duration from the INFile.
                    String numberOfAmbguousTxnAllowedStr = FileCache.getValue(interfaceId, "NO_ALLWD_AMB_TXN");
                    if (InterfaceUtil.isNullString(numberOfAmbguousTxnAllowedStr) || !InterfaceUtil.isNumeric(numberOfAmbguousTxnAllowedStr.trim())) {
                        _log.error("InterfaceCloserController[initialize]", "NO_ALLWD_AMB_TXN is either not defined in the INFile or its value is not numeric");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[initialize]", "", "_interfaceID:" + interfaceId, "", "NO_ALLWD_AMB_TXN is either not defined in the INFile or its value is not numeric");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);
                    }
                    interfaceCloserVO.setNumberOfAmbguousTxnAllowed(Integer.parseInt(numberOfAmbguousTxnAllowedStr.trim()));
                    String thresholdTimeStr = FileCache.getValue(interfaceId, "THRESHOLD_TIME");
                    if (InterfaceUtil.isNullString(thresholdTimeStr) || !InterfaceUtil.isNumeric(thresholdTimeStr.trim())) {
                        _log.error("InterfaceCloserController[initialize]", "THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[initialize]", "", "_interfaceID:" + interfaceId, "", "THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);
                    }
                    interfaceCloserVO.setThresholdTime(Long.parseLong(thresholdTimeStr.trim()));

                    String expiryTimeStr = FileCache.getValue(interfaceId, "EXPIRY_TIME");
                    if (InterfaceUtil.isNullString(expiryTimeStr) || !InterfaceUtil.isNumeric(expiryTimeStr.trim())) {
                        _log.error("InterfaceCloserController[initialize]", "EXPIRY_TIME is either not defined in the INFile or its value is not numeric");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[initialize]", "", "_interfaceID:" + interfaceId, "", "EXPIRY_TIME is either not defined in the INFile or its value is not numeric");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);
                    }
                    interfaceCloserVO.setExpiryTime(Long.parseLong(expiryTimeStr.trim()));
                    interfaceCloserVO.setExpiryFlag(false);
                    // Set the instance of InterfaceClser
                    interfaceCloserVO.setInterfaceCloser(new InterfaceCloser());

                    if ("S".equals(interfaceVO.getStatusCode())) {
                        // At the start time set the status of interface as
                        // S(suspended)
                        interfaceCloserVO.setInterfaceStatus(InterfaceCloserI.INTERFACE_SUSPEND);
                        interfaceCloserVO.setInterfacePrevStatus(InterfaceCloserI.INTERFACE_SUSPEND);
                        interfaceCloserVO.setTimeOfFirstAmbiguousTxn(System.currentTimeMillis());
                        interfaceCloserVO.setCurrentAmbTxnTime(System.currentTimeMillis());
                        interfaceCloserVO.setSuspendAt(System.currentTimeMillis());
                        interfaceCloserVO.setCurrentAmbTxnCounter(interfaceCloserVO.getNumberOfAmbguousTxnAllowed());
                    } else if ("Y".equals(interfaceVO.getStatusCode())) {
                        // At the start time set the status of interface as
                        // Y(not suspended)
                        interfaceCloserVO.setInterfaceStatus(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
                    }
                    // Associate the interfaceCloserVO into map with key as
                    // interfaceID.
                    _interfaceCloserVOTable.put(interfaceId, interfaceCloserVO);
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("initialize", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[initialize]", "", "", "", "While initialization of interface closer parameters, got the Exception " + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);
                }
            }// end of for loop.
        } catch (BTSLBaseException be) {
            _log.error("initialize", "BTSLBaseException be:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[initialize]", "String of interface ids=" + p_interfaceIDs, "", "", "While initialization of interface closer parameters for the INTERFACE_ID =" + interfaceId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);// New
                                                                                          // Error
                                                                                          // Code-Confirm
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("initialize", "Exited _interfaceCloserVOTable::" + _interfaceCloserVOTable + _interfaceCloserVOTable.size() + interfaceCloserVO);
        }// end of finally
    }

    /**
     * This method implements the following logic
     * 1. Update the interfaceCloserVO, If the parameters are updated in the
     * INFile.
     * 2. Add the interfaceCloserVO, If the new Interface is added.
     * 
     * @param String
     *            p_interfaceIDs
     * @throws BTSLBaseException
     */
    public static void update(String p_interfaceIDs) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("update", "Entered p_interfaceIDs::" + p_interfaceIDs);
        String interfaceId = null;
        String dbInterfaceID = null;
        InterfaceCloserVO interfaceCloserVO = null;
        String[] inStrArray = null;
        String[] inStrArrayConstantProps = null;
        try {
            inStrArray = p_interfaceIDs.split(",");
            if (InterfaceUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_INTERFACE_CLOSER);
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            HashMap interfaceVOMap = interfaceDAO.loadInterfaceByID();

            // Iterator it1=interfaceVOMap.entrySet().iterator();
            Iterator it1 = interfaceVOMap.keySet().iterator();
            while (it1.hasNext()) {
                dbInterfaceID = (String) it1.next();
                InterfaceVO interfaceVO = null;
                // Create a list in which store the client object equal to
                // configured number.
                interfaceId = dbInterfaceID;
                try {
                    // Check if the interfaceId is exist in the
                    // _interfaceCloserVOMap then update the required INFile
                    // parameters.
                    if ("ALL".equalsIgnoreCase(p_interfaceIDs) || ((Arrays.asList(inStrArray)).contains(dbInterfaceID))) {
                        interfaceVO = (InterfaceVO) interfaceVOMap.get(interfaceId);
                        if ("N".equals(interfaceVO.getStatusCode()))
                            continue;

                        if (_interfaceCloserVOTable.containsKey(dbInterfaceID)) {
                            interfaceCloserVO = (InterfaceCloserVO) _interfaceCloserVOTable.get(interfaceId);
                            String numberOfAmbguousTxnAllowedStr = FileCache.getValue(interfaceId, "NO_ALLWD_AMB_TXN");
                            if (InterfaceUtil.isNullString(numberOfAmbguousTxnAllowedStr) || !InterfaceUtil.isNumeric(numberOfAmbguousTxnAllowedStr.trim())) {
                                _log.error("InterfaceCloserController[update]", "NO_ALLWD_AMB_TXN is either not defined in the INFile or its value is not numeric");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[update]", "", "_interfaceID:" + interfaceId, "", "NO_ALLWD_AMB_TXN is either not defined in the INFile or its value is not numeric");
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UPDATE_INTERFACE_CLOSER);
                            }
                            interfaceCloserVO.setNumberOfAmbguousTxnAllowed(Integer.parseInt(numberOfAmbguousTxnAllowedStr.trim()));
                            String thresholdTimeStr = FileCache.getValue(interfaceId, "THRESHOLD_TIME");
                            if (InterfaceUtil.isNullString(thresholdTimeStr) || !InterfaceUtil.isNumeric(thresholdTimeStr.trim())) {
                                _log.error("InterfaceCloserController[update]", "THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[update]", "", "_interfaceID:" + interfaceId, "", "THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UPDATE_INTERFACE_CLOSER);
                            }
                            interfaceCloserVO.setThresholdTime(Long.parseLong(thresholdTimeStr.trim()));
                            String expiryTimeStr = FileCache.getValue(interfaceId, "EXPIRY_TIME");
                            if (InterfaceUtil.isNullString(expiryTimeStr) || !InterfaceUtil.isNumeric(expiryTimeStr.trim())) {
                                _log.error("InterfaceCloserController[update]", "EXPIRY_TIME is either not defined in the INFile or its value is not numeric");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[update]", "", "_interfaceID:" + interfaceId, "", "EXPIRY_TIME is either not defined in the INFile or its value is not numeric");
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UPDATE_INTERFACE_CLOSER);
                            }
                            interfaceCloserVO.setExpiryTime(Long.parseLong(expiryTimeStr.trim()));
                        } else {
                            // Get the INID's from the Contstant property file
                            String inStr = Constants.getProperty("INTERFACE_CLOSER_IN_IDS");
                            inStrArrayConstantProps = inStr.split(",");
                            if (!InterfaceUtil.isNullArray(inStrArrayConstantProps) && !Arrays.asList(inStrArrayConstantProps).contains(interfaceId))
                                continue;

                            interfaceCloserVO = new InterfaceCloserVO();

                            // At the start time set the status of interface as
                            // Y(not suspended)
                            // interfaceCloserVO.setInterfaceStatus(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
                            // Fetch the number of ambiguous transaction allowed
                            // for certain duration from the INFile.
                            String numberOfAmbguousTxnAllowedStr = FileCache.getValue(interfaceId, "NO_ALLWD_AMB_TXN");
                            if (InterfaceUtil.isNullString(numberOfAmbguousTxnAllowedStr) || !InterfaceUtil.isNumeric(numberOfAmbguousTxnAllowedStr.trim())) {
                                _log.error("InterfaceCloserController[update]", "NO_ALLWD_AMB_TXN is either not defined in the INFile or its value is not numeric");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[update]", "", "_interfaceID:" + interfaceId, "", "NO_ALLWD_AMB_TXN is either not defined in the INFile or its value is not numeric");
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UPDATE_INTERFACE_CLOSER);
                            }
                            interfaceCloserVO.setNumberOfAmbguousTxnAllowed(Integer.parseInt(numberOfAmbguousTxnAllowedStr.trim()));
                            String thresholdTimeStr = FileCache.getValue(interfaceId, "THRESHOLD_TIME");
                            if (InterfaceUtil.isNullString(thresholdTimeStr) || !InterfaceUtil.isNumeric(thresholdTimeStr.trim())) {
                                _log.error("InterfaceCloserController[update]", "THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[update]", "", "_interfaceID:" + interfaceId, "", "THRESHOLD_TIME is either not defined in the INFile or its value is not numeric");
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UPDATE_INTERFACE_CLOSER);
                            }
                            interfaceCloserVO.setThresholdTime(Long.parseLong(thresholdTimeStr.trim()));

                            String expiryTimeStr = FileCache.getValue(interfaceId, "EXPIRY_TIME");
                            if (InterfaceUtil.isNullString(expiryTimeStr) || !InterfaceUtil.isNumeric(expiryTimeStr.trim())) {
                                _log.error("InterfaceCloserController[update]", "EXPIRY_TIME is either not defined in the INFile or its value is not numeric");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[update]", "", "_interfaceID:" + interfaceId, "", "EXPIRY_TIME is either not defined in the INFile or its value is not numeric");
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UPDATE_INTERFACE_CLOSER);
                            }
                            interfaceCloserVO.setExpiryTime(Long.parseLong(expiryTimeStr.trim()));
                            // Set the instance of InterfaceClser
                            interfaceCloserVO.setInterfaceCloser(new InterfaceCloser());
                            interfaceCloserVO.setExpiryFlag(false);

                        }
                        if ("S".equals(interfaceVO.getStatusCode())) {
                            // At the start time set the status of interface as
                            // S(suspended)
                            interfaceCloserVO.setInterfaceStatus(InterfaceCloserI.INTERFACE_SUSPEND);
                            interfaceCloserVO.setInterfacePrevStatus(InterfaceCloserI.INTERFACE_SUSPEND);
                            interfaceCloserVO.setTimeOfFirstAmbiguousTxn(System.currentTimeMillis());
                            interfaceCloserVO.setCurrentAmbTxnTime(System.currentTimeMillis());
                            interfaceCloserVO.setSuspendAt(System.currentTimeMillis());
                            interfaceCloserVO.setCurrentAmbTxnCounter(interfaceCloserVO.getNumberOfAmbguousTxnAllowed());
                        } else if ("Y".equals(interfaceVO.getStatusCode())) {
                            // At the start time set the status of interface as
                            // Y(not suspended)
                            interfaceCloserVO.setInterfaceStatus(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
                        }
                        // Associate the interfaceCloserVO into map with key as
                        // interfaceID.
                        _interfaceCloserVOTable.put(interfaceId, interfaceCloserVO);
                    }
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("update", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[update]", "", "", "", "While update of interface closer parameters for INTERFACE_ID =" + interfaceId + " got the Exception " + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UPDATE_INTERFACE_CLOSER);
                }
            }// end of for loop.
        } catch (BTSLBaseException be) {
            _log.error("update", "BTSLBaseException be:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("update", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceCloserController[update]", "String of interface ids=" + p_interfaceIDs, "", "", "While update of interface closer parameters for INTERFACE_ID =" + interfaceId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UPDATE_INTERFACE_CLOSER);// New
                                                                                            // Error
                                                                                            // Code-Confirm
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("update", "Exited _interfaceCloserVOTable::" + _interfaceCloserVOTable);
        }// end of finally
    }

    /**
     * This method is used to destoy the parameters related to interface closer.
     * 
     * @param String
     *            p_interfaceId
     * @return void
     */
    public void destroy(String p_interfaceIds) {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered p_interfaceId:" + p_interfaceIds);
        try {
            StringTokenizer strTokens = new StringTokenizer(p_interfaceIds, ",");
            while (strTokens.hasMoreElements()) {
                Object tempObj = strTokens.nextElement();
                if (_interfaceCloserVOTable.containsKey(tempObj))
                    _interfaceCloserVOTable.remove(tempObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("destroy", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting");
        }
    }

    /*
     * public static void main(String[] arg)
     * {
     * InterfaceCloserController obj = new InterfaceCloserController();
     * 
     * _interfaceCloserVOTable.put("A","yiueuiofse");
     * _interfaceCloserVOTable.put("B","yiueuiofse");
     * _interfaceCloserVOTable.put("C","yiueuiofse");
     * _interfaceCloserVOTable.put("D","yiueuiofse");
     * _interfaceCloserVOTable.put("E","yiueuiofse");
     * 
     * obj.destroy("A,B,C,D,E,F");
     * 
     * }
     */
}
