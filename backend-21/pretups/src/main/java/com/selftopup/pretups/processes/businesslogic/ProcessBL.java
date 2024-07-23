package com.selftopup.pretups.processes.businesslogic;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceDAO;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceNetworkMappingDAO;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceVO;
import com.selftopup.pretups.master.businesslogic.ServiceClassDAO;
import com.selftopup.pretups.master.businesslogic.ServiceClassVO;
import com.selftopup.pretups.network.businesslogic.NetworkCache;
import com.selftopup.pretups.network.businesslogic.NetworkVO;

/**
 * @(#)ProcessBL.java
 *                    Name Date History
 *                    ----------------------------------------------------------
 *                    --------------
 *                    Ashish Kumar May 17, 2006 Initial Creation
 *                    Ankit Singhal Sep 25, 2006 Modification ID PRS001
 * 
 *                    ----------------------------------------------------------
 *                    --------------
 *                    Copyright (c) 2006 Bharti Telesoft Ltd.
 *                    This class is used to check the status of the process
 * 
 */
public class ProcessBL {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _networkMap; // Contains the detail of network and the key
                                 // will be the network code.
    private ArrayList _networkList; // Contains the detail of network.
    private HashMap _interfaceNtwkMap;// Contains the detail of all interface
                                      // that are mapped with the network.
    private ArrayList _postpaidInterfaceList;// Contains the detail of all post
                                             // paid interface.
    private HashMap _serviceClassMap;// Contains All the service class based on
                                     // the network code and post paid
                                     // interfaceID.

    /**
     * This method is used to check the process status and does following based
     * on the status.
     * 1.If the Process status is 'C-Complete' then set the true value in
     * ProcessStatusVO.
     * 2.If the Process status is 'U-UnderProcess' then it also checks the
     * Expiry time for the Process.
     * 3.If expiry reached, boolean true value is set in the processStatusVO
     * else set false.
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processID
     * @return ProcessStatusVO
     * @throws BTSLBaseException
     */
    public ProcessStatusVO checkProcessUnderProcess(Connection p_con, String p_processID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkProcessUnderProcess", "Entered with p_processID=" + p_processID);
        long dateDiffInMinute = 0;
        int successC = 0;
        ProcessStatusDAO processStatusDAO = null;
        ProcessStatusVO processStatusVO = null;
        Date date = new Date();
        try {
            processStatusDAO = new ProcessStatusDAO();
            // load the Scheduler information - start date and status of
            // scheduler
            // processStatusVO =
            // processStatusDAO.loadProcessDetail(p_con,p_processID);
            processStatusVO = processStatusDAO.lockProcessStatusTable(p_con, p_processID);

            // Check Process Entry,if no entry for the process throw the
            // exception and stop the process
            if (processStatusVO == null) {
                // PRS001 ProcessId has been added in the OAM message to know
                // which process is throwing the exception
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessunderProcess]", "", "", "", "No entry found in the process_status table for processId=" + p_processID);
                throw new BTSLBaseException("ProcessBL", "checkProcessUnderProcess", SelfTopUpErrorCodesI.PROCESS_ENTRY_NOT_FOUND);
            }

            // If the process status is 'C-Complete' set the _processStatusOK as
            // True and update mark the status as 'U-Underprocess'.
            else if (ProcessI.STATUS_COMPLETE.equals(processStatusVO.getProcessStatus())) {
                // set the current date while updating the start date of process
                processStatusVO.setStartDate(date);
                processStatusVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                successC = processStatusDAO.updateProcessDetail(p_con, processStatusVO);
                if (successC > 0)
                    processStatusVO.setStatusOkBool(true);
                else {
                    // PRS001 ProcessId has been added in the OAM message to
                    // know which process is throwing the exception
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessunderProcess]", "", "", "", "Entry in the process_status table could not be updated from 'Complete' to 'Underprocess' for processId=" + p_processID);
                    throw new BTSLBaseException("ProcessBL", "checkProcessUnderProcess", SelfTopUpErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
                }
            }
            // if the scheduler status is UnderProcess check the expiry of
            // scheduler.
            else if (ProcessI.STATUS_UNDERPROCESS.equals(processStatusVO.getProcessStatus())) {
                // set the current date while updating the start date of process
                if (processStatusVO.getStartDate() != null)
                    dateDiffInMinute = getDiffOfDateInMinute(date, processStatusVO.getStartDate());
                else {
                    // PRS001 ProcessId has been added in the OAM message to
                    // know which process is throwing the exception
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessunderProcess]", "", "", "", "Process start date is null for processId=" + p_processID);
                    throw new BTSLBaseException("ProcessBL", "checkProcessUnderProcess", "Process Start Date is NULL");
                }
                if (_log.isDebugEnabled())
                    _log.debug("checkProcessUnderProcess", "startDate = " + processStatusVO.getStartDate() + "dateDiffInMinute= " + dateDiffInMinute + " expiryTime = " + processStatusVO.getExpiryTime());
                // Checking for the exipry time of the process.
                if (dateDiffInMinute >= processStatusVO.getExpiryTime()) {
                    processStatusVO.setStartDate(date);
                    successC = processStatusDAO.updateProcessDetail(p_con, processStatusVO);
                    if (successC > 0)
                        processStatusVO.setStatusOkBool(true);
                    else {
                        // PRS001 ProcessId has been added in the OAM message to
                        // know which process is throwing the exception
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessunderProcess]", "", "", "", "The entry in the process_status could not be updated to 'Underprocess' after the expiry of underprocess time limit for processId=" + p_processID);
                        throw new BTSLBaseException("ProcessBL", "checkProcessUnderProcess", SelfTopUpErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
                    }
                } else {
                    // PRS001 ProcessId has been added in the OAM message to
                    // know which process is throwing the exception
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessunderProcess]", "", "", "", "Process is already running for processId=" + p_processID);
                    throw new BTSLBaseException("ProcessBL", "checkProcessUnderProcess", SelfTopUpErrorCodesI.PROCESS_ALREADY_RUNNING);
                }
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("checkProcessUnderProcess", "BTSLBaseException while loading process detail" + be);
            // PRS001 It has been commented to avoid two OAM alerts for one
            // event
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessBL[checkProcessUnderProcess]","processStatusVO.getProcessID()"+processStatusVO.getProcessID(),"",""," BTSLBaseException while loading process detail"+be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("checkProcessUnderProcess", "Exception while loading process detail " + e);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessUnderProcess]", "processStatusVO.getProcessID()" + processStatusVO.getProcessID(), "", "", " Exception while loadng process detail " + e.getMessage());
            throw new BTSLBaseException(this, "checkProcessUnderProcess", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("checkProcessUnderProcess", "Exiting processStatusVO=" + processStatusVO);
        }// end of finally
        return processStatusVO;
    }// end of checkProcessUnderProcess

    /**
     * Used to get the difference of date in Minutes
     * 
     * @param p_currentDate
     *            Date
     * @param p_startDate
     *            Date
     * @return long
     * @throws BTSLBaseException
     */
    private long getDiffOfDateInMinute(Date p_currentDate, Date p_startDate) {
        if (_log.isDebugEnabled())
            _log.debug("getDiffOfDateInHour", "Entered p_currentDate=" + p_currentDate + " p_startDate: " + p_startDate);
        long diff = 0;
        try {
            // Getting the difference between current date and start date of
            // process in Minutes.
            diff = ((p_currentDate.getTime() - p_startDate.getTime()) / (1000 * 60));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getDiffOfDateInHour", "Exception : " + e.getMessage());
        }// end of catch-Exception
        if (_log.isDebugEnabled())
            _log.debug("getDiffOfDateInHour", "Exiting diff=" + diff);
        return diff;
    }// end of getDiffOfDateInMinute

    /**
     * This method is invoked by the FileProcess class to load the following
     * 1.From Network cache get the network map and set a network list.
     * 2.Post paid interfaceList
     * 3.Network Mapping
     * 4.Service Class list
     * 
     * @param Connection
     *            p_con
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadNetwkInterfaceServiceDetails(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadNetwkInterfaceServiceDetails", "Entered");
        HashMap ntwkInterfaceServiceMap = null;
        try {
            ntwkInterfaceServiceMap = new HashMap();
            _networkMap = NetworkCache.getNetworkMap();
            if (_networkMap == null)
                throw new BTSLBaseException(this, "loadNetwkInterfaceServiceDetails", SelfTopUpErrorCodesI.WLIST_ERROR_NO_NETWORK_AVALAIBLE);
            // Set networkList
            setNetworkList();

            // load postpaid interface list
            loadPostpaidInterfaceList(p_con);

            // load interfacenetwork map.
            loadInterfacesNtwkMap(p_con);

            // load service class map whose key is "network_code:interfaceId"
            // and value is list of all the service class
            loadServiceClassMap(p_con);

            // set the postpaid interface list,InterfaceNetworkmap,Service class
            // into a map
            ntwkInterfaceServiceMap.put("NETWORK_LIST", _networkList);
            ntwkInterfaceServiceMap.put("POST_PAID_INTF_LIST", _postpaidInterfaceList);
            ntwkInterfaceServiceMap.put("INTF_NETWK_MAPPING", _interfaceNtwkMap);
            ntwkInterfaceServiceMap.put("SERVICE_CLASS_MAP", _serviceClassMap);
        } catch (BTSLBaseException be) {
            _log.error("loadNetwkInterfaceServiceDetails", "BTSLBaseException be= " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[loadNetwkInterfaceServiceDetails]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("loadNetwkInterfaceServiceDetails", "Exception e= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[loadNetwkInterfaceServiceDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadNetwkInterfaceServiceDetails", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("loadNetwkInterfaceServiceDetails", "ntwkInterfaceServiceMap = " + ntwkInterfaceServiceMap);
        }// end of finally
        return ntwkInterfaceServiceMap;
    }

    /**
     * This method is used to set the networkList
     * 
     * @throws BTSLBaseException
     */
    private void setNetworkList() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("setNetworkList", "Entered");
        _networkList = new ArrayList();
        try {
            Set set = _networkMap.keySet();
            Iterator iter = set.iterator();
            NetworkVO networkVO = null;
            while (iter.hasNext()) {
                Object key = iter.next();
                networkVO = (NetworkVO) _networkMap.get(key);
                _networkList.add(networkVO);
            }// end of while
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setNetworkList", "Exception e = " + e.getMessage());
            throw new BTSLBaseException(this, "setNetworkList", e.getMessage());
        }// //end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setNetworkList", "Exited");
        }// end of finally
    }

    /**
     * This method is used to load all the interface whose category is POSTPAID
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private void loadPostpaidInterfaceList(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadPostpaidInterfaceList", "Entered");
        InterfaceDAO interfaceDAO = null;
        try {
            interfaceDAO = new InterfaceDAO();
            // Load all the post paid interface details.
            _postpaidInterfaceList = interfaceDAO.loadInterfaceDetails(p_con, PretupsI.INTERFACE_CATEGORY_POST);
            if (_postpaidInterfaceList == null)
                throw new BTSLBaseException(this, "loadPostpaidInterfaceList", SelfTopUpErrorCodesI.WLIST_ERROR_NO_POST_PAID_INTERFACE);
        }// end of try-block.
        catch (BTSLBaseException be) {
            _log.error("loadPostpaidInterfaceList", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("loadPostpaidInterfaceList", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[loadPostpaidInterfaceList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadPostpaidInterfaceList", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("loadPostpaidInterfaceList", "Exited");
        }// end of finally
    }// end of loadPostpaidInterfaceList

    /**
     * This method is used to load interface network mapping for the post paid
     * interface.
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private void loadInterfacesNtwkMap(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadInterfacesNtwkMap", "Entered");
        _interfaceNtwkMap = new HashMap();
        InterfaceNetworkMappingDAO interfaceNetworkMappingDAO = null;
        InterfaceNetworkMappingVO interfaceNetworkMappingVO = null;
        ArrayList interfaceNtwkMappingList = null;
        ArrayList postpaidInterfaceNtwkMapList = new ArrayList();
        try {
            Set set = _networkMap.keySet();
            Iterator iter = set.iterator();
            interfaceNetworkMappingDAO = new InterfaceNetworkMappingDAO();
            while (iter.hasNext()) {
                Object key = iter.next();
                interfaceNtwkMappingList = interfaceNetworkMappingDAO.loadInterfaceNetworkMappingList(p_con, key.toString(), PretupsI.INTERFACE_CATEGORY);
                if (interfaceNtwkMappingList == null || interfaceNtwkMappingList.isEmpty())
                    continue;
                // Add interfaceMapping for the post paid interface only
                for (int i = 0, size = interfaceNtwkMappingList.size(); i < size; i++) {
                    interfaceNetworkMappingVO = (InterfaceNetworkMappingVO) interfaceNtwkMappingList.get(i);
                    if (PretupsI.INTERFACE_CATEGORY_POST.equals(interfaceNetworkMappingVO.getInterfaceCategoryID()))
                        postpaidInterfaceNtwkMapList.add(interfaceNetworkMappingVO);
                }// end of interface network mapping iteration
                _interfaceNtwkMap.put(key.toString(), postpaidInterfaceNtwkMapList);
            }// end of while
            if (_interfaceNtwkMap == null || _interfaceNtwkMap.isEmpty())
                throw new BTSLBaseException(this, "loadInterfacesNtwkMap", SelfTopUpErrorCodesI.WLIST_ERROR_NO_INTERFACE_MAPPED);
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("loadInterfacesNtwkMap", "BTSLBaseException be= " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("loadInterfacesNtwkMap", "Exception e= " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[loadInterfacesNtwkMap]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadInterfacessList", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("loadInterfacesNtwkMap", "Exited");
        }// end of finally
    }

    /**
     * This method is used to load the service class List
     * ServiceClass list is stored in a map and key is NetworkCode:InterfaceID
     * For this iterate _interfaceNtwkMap that stores the Interface list of each
     * network(network code is the key value)
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private void loadServiceClassMap(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceClassMap", "Entered");
        ServiceClassDAO serviceClassDAO = null;
        ArrayList interfaceList = null;
        _serviceClassMap = new HashMap();
        ArrayList serviceClassList = new ArrayList();
        InterfaceNetworkMappingVO interfaceNetworkMappingVO = null;
        try {
            serviceClassDAO = new ServiceClassDAO();
            Set set = _interfaceNtwkMap.keySet();
            Iterator iter = set.iterator();
            while (iter.hasNext()) {
                // getting the key(Network Code) of the map
                Object key = iter.next();
                String keyStr = key.toString();
                // Getting the postpaid interface list corresponding to the
                // network code.
                interfaceList = (ArrayList) _interfaceNtwkMap.get(key);

                // check if network list is null or its size is zero
                if (interfaceList == null || interfaceList.isEmpty())
                    continue;
                // Iterating the interface list to load all the service class
                // corresponding to each interface.
                // Each service class list is put into _serviceClassMap whose
                // key will be "NetworkCode:InterfaceID"
                for (int l = 0, size = interfaceList.size(); l < size; l++) {
                    interfaceNetworkMappingVO = (InterfaceNetworkMappingVO) interfaceList.get(l);
                    serviceClassList = serviceClassDAO.loadServiceClassDetails(p_con, interfaceNetworkMappingVO.getInterfaceID());
                    // Put the service class list in _serviceClassMap whose key
                    // is "NetworkCode:InterfaceID"
                    _serviceClassMap.put(keyStr + ":" + interfaceNetworkMappingVO.getInterfaceID(), serviceClassList);
                }// end of interface list iteration
            }// end of while.
            if (_serviceClassMap == null)
                throw new BTSLBaseException(this, "loadServiceClassMap", SelfTopUpErrorCodesI.WLIST_ERROR_NO_SERVICE_CLASS);
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("loadServiceClassMap", "BTSLBaseException e= " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("loadServiceClassMap", "Exception e= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[loadServiceClassMap]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassMap", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("loadServiceClassMap", "Exited");
        }// end of finally
    }// end of loadServiceClassList

    /**
     * This method is used to validate the network code against the network
     * list.
     * 
     * @param String
     *            p_networkCode
     * @param ArrayList
     *            p_networkList
     * @throws BTSLBaseException
     */
    public void validateNetworkCode(String p_networkCode, ArrayList p_networkList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateNetworkCode", "Entered p_networkCode = " + p_networkCode + " p_networkList.size() = " + p_networkList.size());
        NetworkVO networkVO = null;
        boolean validNetworkCode = false;
        try {
            // From the network list check that the network code exist or not,if
            // not then show error message.
            for (int i = 0, size = p_networkList.size(); i < size; i++) {
                networkVO = (NetworkVO) p_networkList.get(i);
                if (p_networkCode.equals(networkVO.getNetworkCode())) {
                    validNetworkCode = true;
                    break;
                }
            }
            if (!validNetworkCode)
                throw new BTSLBaseException(this, "validateNetworkCode", SelfTopUpErrorCodesI.WLIST_ERROR_INVALID_NETWORK_CODE);
        }// end of try block
        catch (BTSLBaseException be) {
            _log.error("validateNetworkCode", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validateNetworkCode", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[validateNetworkCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateNetworkCode", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("validateNetworkCode", "Exited");
        }// end of finally
    }// end of validateNetworkCode

    /**
     * This method is used to get the interfaceID based on the ExternalID
     * 
     * @param String
     *            p_externalInterfaceID
     * @param ArrayList
     *            p_postpaidInterfaceList
     * @return String
     * @throws BTSLBaseException
     */
    public String getInterfaceID(String p_externalInterfaceID, ArrayList p_postpaidInterfaceList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getInterfaceID", "Entered p_externalInterfaceID = " + p_externalInterfaceID + " p_postpaidInterfaceList.size() = " + p_postpaidInterfaceList.size());
        InterfaceVO interfaceVO = null;
        String interfaceID = null;
        try {
            // Iterate the interfacelist upto its size
            for (int i = 0, size = p_postpaidInterfaceList.size(); i < size; i++) {
                interfaceVO = (InterfaceVO) p_postpaidInterfaceList.get(i);
                // Get the interfaceID based on the unique ExternalID,if found
                // then break the loop
                if (p_externalInterfaceID.equals(interfaceVO.getExternalId())) {
                    interfaceID = interfaceVO.getInterfaceId();
                    break;
                }
            }
            // If the interfaceID is not found then raise the error that
            // externalID does not exist in POST Paid Category.
            if (interfaceID == null)
                throw new BTSLBaseException(this, "getInterfaceID", SelfTopUpErrorCodesI.WLIST_ERROR_INTERFACEID_NOT_POST_PAID);
            if (_log.isDebugEnabled())
                _log.debug("getInterfaceID", "interfaceID = " + interfaceID);
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("getInterfaceID", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("getInterfaceID", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[getInterfaceID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getInterfaceID", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getInterfaceID", "Exited interfaceID = " + interfaceID);
        }// end of finally
        return interfaceID;
    }// end of getInterfaceID

    /**
     * This method is used to set the network code when it is not provided in
     * the header information
     * 
     * @param ArrayList
     *            p_networkList
     * @throws BTSLBaseException
     */
    public String getNetworkCode(ArrayList p_networkList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getNetworkCode", "Entered p_networkList.size() = " + p_networkList.size());
        String networkCode = null;
        try {
            if (p_networkList.size() > 1)
                throw new BTSLBaseException(this, "getNetworkCode", SelfTopUpErrorCodesI.WLIST_ERROR_MULTIPLE_NETWK_SUPPORT);
            networkCode = ((NetworkVO) p_networkList.get(0)).getNetworkCode();
            if (networkCode == null)
                throw new BTSLBaseException(this, "getNetworkCode", SelfTopUpErrorCodesI.WLIST_ERROR_NETWK_NOT_FOUND);
            if (_log.isDebugEnabled())
                _log.debug("getNetworkCode", " networkCode  = " + networkCode);
        }// end of try block
        catch (BTSLBaseException be) {
            _log.error("getNetworkCode", "BTSLBaseException be = " + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getNetworkCode", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ProcessBL[getNetworkCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getNetworkCode", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getNetworkCode", "Exited networkCode = " + networkCode);
        }// end of finally
        return networkCode;
    }// end of setNetworkCode

    /**
     * This method is used to validate the Interface and its mapping with the
     * network code.
     * 
     * @param String
     *            p_interfaceID
     * @param String
     *            p_networkCode
     * @param HashMap
     *            p_interfaceNtwkMap
     * @throws BTSLBaseException
     */
    public void validateInterfaceID(String p_interfaceID, String p_networkCode, HashMap p_interfaceNtwkMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateInterfaceID", "Entered p_interfaceID= " + p_interfaceID + " p_networkCode = " + p_networkCode + " p_interfaceNtwkMap = " + p_interfaceNtwkMap);
        ArrayList interfaceLst = null;
        boolean mappingFound = false;
        InterfaceNetworkMappingVO interfaceNetworkMapVO = null;
        try {
            // Check whether the interface category is post paid
            /*
             * for(int i=0,size=_postpaidInterfaceList.size();i<size;i++)
             * {
             * interfaceVO =(InterfaceVO)_postpaidInterfaceList.get(i);
             * if(p_interfaceID.equals(interfaceVO.getInterfaceId()))
             * {
             * isPostpaidInterface=true;
             * break;
             * }
             * }
             * if(!isPostpaidInterface)
             * throw new
             * BTSLBaseException(this,"validateInterfaceID",PretupsErrorCodesI
             * .WLIST_ERROR_INTERFACEID_NOT_POST_PAID);
             */
            // Check whether the postpaid interface is mapped with network or
            // not.
            interfaceLst = (ArrayList) p_interfaceNtwkMap.get(p_networkCode);
            if (interfaceLst == null || interfaceLst.isEmpty())
                throw new BTSLBaseException(this, "validateInterfaceID", SelfTopUpErrorCodesI.WLIST_ERROR_NO_INTERFACE_MAPPED);
            for (int i = 0, size = interfaceLst.size(); i < size; i++) {
                interfaceNetworkMapVO = (InterfaceNetworkMappingVO) interfaceLst.get(i);
                if (p_interfaceID.equals(interfaceNetworkMapVO.getInterfaceID())) {
                    mappingFound = true;
                    break;
                }
            }// end of for loop
            if (!mappingFound)
                throw new BTSLBaseException(this, "validateInterfaceID", SelfTopUpErrorCodesI.WLIST_ERROR_INTERFACE_NTWK_MAPPING_NOT_FOUND);
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("validateInterfaceID", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-Exception
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validateInterfaceID", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[validateInterfaceID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateInterfaceID", e.getMessage());
        }// end of catch-BTSLBaseException
        finally {
            if (_log.isDebugEnabled())
                _log.debug("validateInterfaceID", "Exited");
        }// end of finally
    }// end of validateInterfaceID

    /**
     * This method is used to validate the service class by loading service
     * class list.
     * 
     * @param String
     *            p_serviceClass
     * @param String
     *            p_networkCode
     * @param String
     *            p_interfaceID
     * @param HashMap
     *            p_serviceClassMap
     * @throws BTSLBaseException
     */
    public void validateServiceClass(String p_serviceClassCode, String p_networkCode, String p_interfaceID, HashMap p_serviceClassMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateServiceClass", "Entered p_serviceClassCode = " + p_serviceClassCode + " p_networkCode = " + p_networkCode + " p_interfaceID = " + p_interfaceID + " p_serviceClassMap = " + p_serviceClassMap);
        ArrayList tempServiceClasslist = null;
        ServiceClassVO serviceClassVO = null;
        boolean serviceClassCodeFound = false;
        try {
            // Get the service class list based on the network code and
            // InterfaceID
            tempServiceClasslist = (ArrayList) p_serviceClassMap.get(p_networkCode + ":" + p_interfaceID);
            // Iterate the service class list upto its size
            for (int i = 0, size = tempServiceClasslist.size(); i < size; i++) {
                serviceClassVO = (ServiceClassVO) tempServiceClasslist.get(i);
                // If service class is found in the list break the loop.
                if (p_serviceClassCode.equals(serviceClassVO.getServiceClassCode())) {
                    serviceClassCodeFound = true;
                    break;
                }
            }
            // If service class is not found raise error
            if (!serviceClassCodeFound)
                throw new BTSLBaseException(this, "validateServiceClass", SelfTopUpErrorCodesI.WLIST_ERROR_NO_SERVICE_CLASS);
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("validateServiceClass", "BTSLBaseException be = " + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateServiceClass", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[validateServiceClass]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateServiceClass", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("validateServiceClass", "Exited");
        }// end of finally
    }// end of validateServiceClass

    /**
     * This meethod is used to move the file in destination location with new
     * name after the file has been Processed.
     * 
     * @param File
     *            p_inputFile
     * @param String
     *            p_moveLocation
     * @param String
     *            p_newFileName
     * @throws BTSLBaseException
     */
    public void moveFile(File p_inputFile, String p_moveLocation, String p_newFileName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("moveFile", " Entered p_inputFile = " + p_inputFile);
        boolean success = false;
        try {
            File destFile = new File(p_moveLocation + File.separator);
            if (!destFile.exists())
                throw new BTSLBaseException(this, "moveFile", SelfTopUpErrorCodesI.WLIST_ERROR_MOVE_LOCATION_NOT_EXIST);
            // Move file to new directory
            success = p_inputFile.renameTo(new File(destFile, p_newFileName));
            if (!success)
                throw new BTSLBaseException(this, "moveFile", SelfTopUpErrorCodesI.WLIST_ERROR_FILE_NOT_MOVED_SUCCESSFULLY);
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("moveFile", "BTSLBaseException be=" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("moveFile", "Exception e=" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ProcessBL[moveFile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "moveFile", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("moveFile", "Exited success = " + success);
        }// end of finally
    }// end of moveFile

    /**
     * This method is used to check the process status on the basis of
     * process_id and network_code,
     * and does following based on the status.
     * 1.If the Process status is 'C-Complete' then set the true value in
     * ProcessStatusVO.
     * 2.If the Process status is 'U-UnderProcess' then it also checks the
     * Expiry time for the Process.
     * 3.If expiry reached, boolean true value is set in the processStatusVO
     * else set false.
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processID
     * @param String
     *            p_networkCode
     * @return ProcessStatusVO
     * @throws BTSLBaseException
     * @author Vinay Singh
     */
    public ProcessStatusVO checkProcessUnderProcessNetworkWise(Connection p_con, String p_processID, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkProcessUnderProcessNetworkWise:", " Entered with Process ID=" + p_processID + " Network code=" + p_networkCode);
        long dateDiffInMinute = 0;
        int successC = 0;
        ProcessStatusDAO processStatusDAO = null;
        ProcessStatusVO processStatusVO = null;
        Date date = new Date();
        try {
            processStatusDAO = new ProcessStatusDAO();
            // load the Scheduler information - start date and status of
            // scheduler
            processStatusVO = processStatusDAO.loadProcessDetailNetworkWise(p_con, p_processID, p_networkCode);

            // Check Process Entry,if no entry for the process throw the
            // exception and stop the process
            if (processStatusVO == null) {
                // ProcessId and Network Code has been added in the OAM message
                // to know which process is throwing the exception
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessUnderProcessNetworkWise]", "", "", "No entry found in the process_status table for processId=" + p_processID, "Network code=" + p_networkCode);
                throw new BTSLBaseException("ProcessBL:", " checkProcessUnderProcessNetworkWise ", SelfTopUpErrorCodesI.PROCESS_ENTRY_NOT_FOUND);
            }

            // If the process status is 'C-Complete' set the _processStatusOK as
            // True and update mark the status as 'U-Underprocess'.
            else if (ProcessI.STATUS_COMPLETE.equals(processStatusVO.getProcessStatus())) {
                // set the current date while updating the start date of process
                processStatusVO.setStartDate(date);
                processStatusVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                successC = processStatusDAO.updateProcessDetailNetworkWise(p_con, processStatusVO);
                if (successC > 0)
                    processStatusVO.setStatusOkBool(true);
                else {
                    // ProcessId and Network Code has been added in the OAM
                    // message to know which process is throwing the exception
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessUnderProcessNetworkWise]", "", "", "Entry in the process_status table could not be updated from 'Complete' to 'Underprocess' for processId=" + p_processID, "Network code=" + p_networkCode);
                    throw new BTSLBaseException("ProcessBL:", " checkProcessUnderProcessNetworkWise ", SelfTopUpErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
                }
            }
            // if the scheduler status is UnderProcess check the expiry of
            // scheduler.
            else if (ProcessI.STATUS_UNDERPROCESS.equals(processStatusVO.getProcessStatus())) {
                // set the current date while updating the start date of process
                if (processStatusVO.getStartDate() != null)
                    dateDiffInMinute = getDiffOfDateInMinute(date, processStatusVO.getStartDate());
                else {
                    // ProcessId and Network Code has been added in the OAM
                    // message to know which process is throwing the exception
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessUnderProcessNetworkWise]", "", "", "Process start date is null for processId=" + p_processID, "Network code=" + p_networkCode);
                    throw new BTSLBaseException("ProcessBL:", " checkProcessUnderProcessNetworkWise", " Process Start Date is NULL");
                }
                if (_log.isDebugEnabled())
                    _log.debug("checkProcessUnderProcessNetworkWise: ", "startDate = " + processStatusVO.getStartDate() + "dateDiffInMinute= " + dateDiffInMinute + " expiryTime = " + processStatusVO.getExpiryTime());
                // Checking for the exipry time of the process.
                if (dateDiffInMinute >= processStatusVO.getExpiryTime()) {
                    processStatusVO.setStartDate(date);
                    successC = processStatusDAO.updateProcessDetail(p_con, processStatusVO);
                    if (successC > 0)
                        processStatusVO.setStatusOkBool(true);
                    else {
                        // ProcessId and Network Code has been added in the OAM
                        // message to know which process is throwing the
                        // exception
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessUnderProcessNetworkWise]", "", "", "The entry in the process_status could not be updated to 'Underprocess' after the expiry of underprocess time limit for processId=" + p_processID, "Network code=" + p_networkCode);
                        throw new BTSLBaseException("ProcessBL:", " checkProcessUnderProcessNetworkWise ", SelfTopUpErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
                    }
                } else {
                    // ProcessId and Network Code has been added in the OAM
                    // message to know which process is throwing the exception
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessUnderProcessNetworkWise]", "", "", "Process is already running for processId and network code==" + p_processID, "Network code=" + p_networkCode);
                    throw new BTSLBaseException("ProcessBL", "checkProcessUnderProcessNetworkWise: ", SelfTopUpErrorCodesI.PROCESS_ALREADY_RUNNING);
                }
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("checkProcessUnderProcessNetworkWise: ", "BTSLBaseException while loading process detail" + be);
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("checkProcessUnderProcessNetworkWise: ", "Exception while loading process detail " + e);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessBL[checkProcessUnderProcessNetworkWise]", " Process ID=" + processStatusVO.getProcessID(), " and network code=" + p_networkCode, "", " Exception while loadng process detail " + e.getMessage());
            throw new BTSLBaseException(this, "checkProcessUnderProcessNetworkWise: ", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("checkProcessUnderProcessNetworkWise: ", "Exiting processStatusVO=" + processStatusVO);
        }// end of finally
        return processStatusVO;
    }// end of checkProcessUnderProcessNetworkWise
}
