package com.selftopup.loadcontroller;

import java.sql.Timestamp;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;

public class ReqNetworkServiceLoadController {
    private static Log _log = LogFactory.getLog(ReqNetworkServiceLoadController.class.getName());

    /**
     * Method to increase the intermediate counters for requests
     * 
     * @param p_instanceID
     * @param p_reqType
     * @param p_networkID
     * @param p_serviceType
     * @param p_requestID
     * @param p_event
     * @param p_startTime
     * @param p_endTime
     * @param p_isSuccessTxn
     * @param p_increaseFinalCounts
     *            (Added to remove the Service type hardcoding while handling
     *            counters
     */
    public static void increaseIntermediateCounters(String p_instanceID, String p_reqType, String p_networkID, String p_serviceType, String p_requestID, int p_event, long p_startTime, long p_endTime, boolean p_isSuccessTxn, boolean p_increaseFinalCounts) {
        if (_log.isDebugEnabled())
            _log.debug("increaseIntermediateCounters", "Entered with p_instanceID=" + p_instanceID + " p_reqType:" + p_reqType + "p_networkID=" + p_networkID + "p_serviceType=" + p_serviceType + "p_requestID=" + p_requestID + "p_event=" + p_event + "p_isSuccessTxn=" + p_isSuccessTxn + "p_increaseFinalCounts=" + p_increaseFinalCounts);
        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        long serviceTime = 0;
        try {
            p_instanceID = LoadControllerCache.getInstanceID();
            serviceTime = p_endTime - p_startTime;
            NetworkServiceLoadVO networkServiceLoadVO = (NetworkServiceLoadVO) LoadControllerCache.getNetworkServiceLoadHash().get(p_instanceID + "_" + p_reqType + "_" + p_networkID + "_" + p_serviceType);
            if (networkServiceLoadVO == null)
                networkServiceLoadVO = (NetworkServiceLoadVO) LoadControllerCache.getNetworkServiceLoadHash().get(p_instanceID + "_OTHERS");

            if (_log.isDebugEnabled())
                _log.debug("increaseIntermediateCounters", "p_requestID=" + p_requestID + "p_event=" + p_event + "p_isSuccessTxn=" + p_isSuccessTxn + " networkServiceLoadVO=" + networkServiceLoadVO);

            if (BTSLUtil.isNullString(p_reqType))
                p_event = LoadControllerI.COUNTER_BEF_GTW_FAIL_REQUEST;
            else if (BTSLUtil.isNullString(p_networkID))
                p_event = LoadControllerI.COUNTER_BEF_NET_FAIL_REQUEST;
            else if (BTSLUtil.isNullString(p_serviceType))
                p_event = LoadControllerI.COUNTER_BEF_SER_FAIL_REQUEST;

            if (p_event == LoadControllerI.COUNTER_NEW_REQUEST) {
                networkServiceLoadVO.setRecievedCount(networkServiceLoadVO.getRecievedCount() + 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
                networkServiceLoadVO.setLastReceievedTime(timeStampVal);
                if (!p_increaseFinalCounts) {
                    if (p_isSuccessTxn)
                        increaseIntermediateCounters(p_instanceID, p_reqType, p_networkID, p_serviceType, p_requestID, LoadControllerI.COUNTER_UNDERPROCESS_REQUEST, p_startTime, p_endTime, p_isSuccessTxn, p_increaseFinalCounts);
                    else
                        increaseIntermediateCounters(p_instanceID, p_reqType, p_networkID, p_serviceType, p_requestID, LoadControllerI.COUNTER_FAIL_REQUEST, p_startTime, p_endTime, p_isSuccessTxn, p_increaseFinalCounts);
                }

                else {
                    if (p_isSuccessTxn)
                        increaseIntermediateCounters(p_instanceID, p_reqType, p_networkID, p_serviceType, p_requestID, LoadControllerI.COUNTER_SUCCESS_REQUEST, p_startTime, p_endTime, p_isSuccessTxn, p_increaseFinalCounts);
                    else
                        increaseIntermediateCounters(p_instanceID, p_reqType, p_networkID, p_serviceType, p_requestID, LoadControllerI.COUNTER_FAIL_REQUEST, p_startTime, p_endTime, p_isSuccessTxn, p_increaseFinalCounts);
                }

            } else if (p_event == LoadControllerI.COUNTER_BEF_GTW_FAIL_REQUEST) {
                networkServiceLoadVO.setRecievedCount(networkServiceLoadVO.getRecievedCount() + 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
                networkServiceLoadVO.setLastReceievedTime(timeStampVal);
                networkServiceLoadVO.setBeforeGatewayFoundError(networkServiceLoadVO.getBeforeGatewayFoundError() + 1);
                networkServiceLoadVO.setLastRequestServiceTime(serviceTime);
                networkServiceLoadVO.setAverageServiceTime((networkServiceLoadVO.getAverageServiceTime() + serviceTime) / 2);
            } else if (p_event == LoadControllerI.COUNTER_BEF_NET_FAIL_REQUEST) {
                networkServiceLoadVO.setRecievedCount(networkServiceLoadVO.getRecievedCount() + 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
                networkServiceLoadVO.setLastReceievedTime(timeStampVal);
                networkServiceLoadVO.setBeforeNetworkFoundError(networkServiceLoadVO.getBeforeNetworkFoundError() + 1);
                networkServiceLoadVO.setLastRequestServiceTime(serviceTime);
                networkServiceLoadVO.setAverageServiceTime((networkServiceLoadVO.getAverageServiceTime() + serviceTime) / 2);
            } else if (p_event == LoadControllerI.COUNTER_BEF_SER_FAIL_REQUEST) {
                networkServiceLoadVO.setRecievedCount(networkServiceLoadVO.getRecievedCount() + 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
                networkServiceLoadVO.setLastReceievedTime(timeStampVal);
                networkServiceLoadVO.setBeforeServiceTypeFoundError(networkServiceLoadVO.getBeforeServiceTypeFoundError() + 1);
                networkServiceLoadVO.setLastRequestServiceTime(serviceTime);
                networkServiceLoadVO.setAverageServiceTime((networkServiceLoadVO.getAverageServiceTime() + serviceTime) / 2);
            } else if (p_event == LoadControllerI.COUNTER_OTHER_FAIL_REQUEST) {
                networkServiceLoadVO.setRecievedCount(networkServiceLoadVO.getRecievedCount() + 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
                networkServiceLoadVO.setLastReceievedTime(timeStampVal);
                networkServiceLoadVO.setOthersFailCount(networkServiceLoadVO.getOthersFailCount() + 1);
                networkServiceLoadVO.setLastRequestServiceTime(serviceTime);
                networkServiceLoadVO.setAverageServiceTime((networkServiceLoadVO.getAverageServiceTime() + serviceTime) / 2);
            } else if (p_event == LoadControllerI.COUNTER_SUCCESS_REQUEST) {
                networkServiceLoadVO.setSuccessCount(networkServiceLoadVO.getSuccessCount() + 1);
                networkServiceLoadVO.setLastRequestServiceTime(serviceTime);
                networkServiceLoadVO.setAverageServiceTime((networkServiceLoadVO.getAverageServiceTime() + serviceTime) / 2);

            } else if (p_event == LoadControllerI.COUNTER_FAIL_REQUEST) {
                networkServiceLoadVO.setFailCount(networkServiceLoadVO.getFailCount() + 1);
                networkServiceLoadVO.setLastRequestServiceTime(serviceTime);
                networkServiceLoadVO.setAverageServiceTime((networkServiceLoadVO.getAverageServiceTime() + serviceTime) / 2);
            } else if (p_event == LoadControllerI.COUNTER_UNDERPROCESS_REQUEST) {
                networkServiceLoadVO.setUnderProcessCount(networkServiceLoadVO.getUnderProcessCount() + 1);
                networkServiceLoadVO.setLastRequestServiceTime(serviceTime);
                networkServiceLoadVO.setAverageServiceTime((networkServiceLoadVO.getAverageServiceTime() + serviceTime) / 2);
            }
            if (_log.isDebugEnabled())
                _log.debug("increaseIntermediateCounters", "Exiting For p_requestID=" + p_requestID + "p_event=" + p_event + "p_isSuccessTxn=" + p_isSuccessTxn + " networkServiceLoadVO=" + networkServiceLoadVO);
        } catch (Exception e) {
            _log.errorTrace("increaseIntermediateCounters Exception print stack trace:=", e);
            _log.error("increaseIntermediateCounters", "Getting exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ReqNetworkServiceLoadController[increaseIntermediateCounters]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to increase counters if roam request
     * 
     * @param p_instanceID
     * @param p_reqType
     * @param p_networkID
     * @param p_serviceType
     * @param p_requestID
     * @param p_event
     * @param p_serviceTime
     * @param p_isRequestSuccess
     */
    public static void increaseRechargeCounters(String p_instanceID, String p_reqType, String p_networkID, String p_serviceType, String p_requestID, int p_event, long p_serviceTime, boolean p_isRequestSuccess, String p_networkFor) {
        if (_log.isDebugEnabled())
            _log.debug("increaseRechargeCounters", "Entered with p_instanceID=" + p_instanceID + " p_reqType:" + p_reqType + "p_networkID=" + p_networkID + "p_serviceType=" + p_serviceType + "p_requestID=" + p_requestID + "p_event=" + p_event + "p_isRequestSuccess=" + p_isRequestSuccess + "p_serviceTime=" + p_serviceTime + "p_networkFor=" + p_networkFor);
        try {
            p_instanceID = LoadControllerCache.getInstanceID();

            NetworkServiceLoadVO networkServiceLoadVO = (NetworkServiceLoadVO) LoadControllerCache.getNetworkServiceLoadHash().get(p_instanceID + "_" + p_reqType + "_" + p_networkID + "_" + p_serviceType);
            if (networkServiceLoadVO == null)
                networkServiceLoadVO = (NetworkServiceLoadVO) LoadControllerCache.getNetworkServiceLoadHash().get(p_instanceID + "_OTHERS");
            if (!p_networkID.equals(p_networkFor))
                p_event = LoadControllerI.COUNTER_ROAM_REQUEST;

            if (_log.isDebugEnabled())
                _log.debug("increaseRechargeCounters", "p_requestID=" + p_requestID + "p_event=" + p_event + " networkServiceLoadVO=" + networkServiceLoadVO);

            if (p_event == LoadControllerI.COUNTER_ROAM_REQUEST) {
                networkServiceLoadVO.setOtherNetworkReqCount(networkServiceLoadVO.getOtherNetworkReqCount() + 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
                if (p_isRequestSuccess)
                    networkServiceLoadVO.setSuccessCount(networkServiceLoadVO.getSuccessCount() + 1);
                else
                    networkServiceLoadVO.setFailCount(networkServiceLoadVO.getFailCount() + 1);
            } else if (p_event == LoadControllerI.COUNTER_SUCCESS_REQUEST) {
                networkServiceLoadVO.setSuccessCount(networkServiceLoadVO.getSuccessCount() + 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
            } else if (p_event == LoadControllerI.COUNTER_FAIL_REQUEST) {
                networkServiceLoadVO.setFailCount(networkServiceLoadVO.getFailCount() + 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
            }
            try {
                increaseRechargeHourlyCounters(p_instanceID, p_reqType, p_networkID, p_serviceType, p_requestID, p_event, p_serviceTime, p_isRequestSuccess, p_networkFor);
            } catch (Exception e) {
                _log.errorTrace("increaseRechargeCounters: Exception print stack trace:=", e);
                _log.error("increaseRechargeHourlyCounters", "Getting exception =" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ReqNetworkServiceLoadController[increaseRechargeHourlyCounters]", "", "", "", "Exception:" + e.getMessage());

            }
            if (_log.isDebugEnabled())
                _log.debug("increaseRechargeHourlyCounters", "Exiting for p_requestID=" + p_requestID + "p_event=" + p_event + " networkServiceLoadVO=" + networkServiceLoadVO);
        } catch (Exception e) {
            _log.errorTrace("increaseRechargeCounters: Exception print stack trace:=", e);
            _log.error("increaseRechargeCounters", "Getting exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ReqNetworkServiceLoadController[increaseRechargeCounters]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to increase counters if roam request
     * 
     * @param p_instanceID
     * @param p_reqType
     * @param p_networkID
     * @param p_serviceType
     * @param p_requestID
     * @param p_event
     * @param p_serviceTime
     * @param p_isRequestSuccess
     */
    public static void increaseRechargeHourlyCounters(String p_instanceID, String p_reqType, String p_networkID, String p_serviceType, String p_requestID, int p_event, long p_serviceTime, boolean p_isRequestSuccess, String p_networkFor) {
        if (_log.isDebugEnabled())
            _log.debug("increaseRechargeHourlyCounters", "Entered with p_instanceID=" + p_instanceID + " p_reqType:" + p_reqType + "p_networkID=" + p_networkID + "p_serviceType=" + p_serviceType + "p_requestID=" + p_requestID + "p_event=" + p_event + "p_isRequestSuccess=" + p_isRequestSuccess + "p_serviceTime=" + p_serviceTime + "p_networkFor=" + p_networkFor);
        try {
            p_instanceID = LoadControllerCache.getInstanceID();
            String[] testArray = p_requestID.split("\\.");
            String hourForTxn = (String) testArray[1].substring(0, 2);
            NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO = (NetworkServiceHourlyLoadVO) LoadControllerCache.getNetworkServiceHourlyLoadHash().get(p_instanceID + "_" + p_reqType + "_" + p_networkID + "_" + p_serviceType + "_" + hourForTxn);
            if (networkServiceHourlyLoadVO == null)
                networkServiceHourlyLoadVO = (NetworkServiceHourlyLoadVO) LoadControllerCache.getNetworkServiceHourlyLoadHash().get(p_instanceID + "_OTHERS" + hourForTxn);

            if (!p_networkID.equals(p_networkFor))
                p_event = LoadControllerI.COUNTER_ROAM_REQUEST;

            if (_log.isDebugEnabled())
                _log.debug("increaseRechargeHourlyCounters", "p_requestID=" + p_requestID + "p_event=" + p_event + " networkServiceHourlyLoadVO=" + networkServiceHourlyLoadVO);

            if (p_event == LoadControllerI.COUNTER_ROAM_REQUEST) {
                networkServiceHourlyLoadVO.setOtherNetworkReqCount(networkServiceHourlyLoadVO.getOtherNetworkReqCount() + 1);
                networkServiceHourlyLoadVO.setLastRequestID(p_requestID);
                if (p_isRequestSuccess)
                    networkServiceHourlyLoadVO.setSuccessCount(networkServiceHourlyLoadVO.getSuccessCount() + 1);
                else
                    networkServiceHourlyLoadVO.setFailCount(networkServiceHourlyLoadVO.getFailCount() + 1);
            }

            if (p_event == LoadControllerI.COUNTER_SUCCESS_REQUEST) {
                networkServiceHourlyLoadVO.setSuccessCount(networkServiceHourlyLoadVO.getSuccessCount() + 1);
                networkServiceHourlyLoadVO.setLastRequestID(p_requestID);
            } else if (p_event == LoadControllerI.COUNTER_FAIL_REQUEST) {
                networkServiceHourlyLoadVO.setFailCount(networkServiceHourlyLoadVO.getFailCount() + 1);
                networkServiceHourlyLoadVO.setLastRequestID(p_requestID);
            }
            if (_log.isDebugEnabled())
                _log.debug("increaseRechargeHourlyCounters", "Exiting for p_requestID=" + p_requestID + "p_event=" + p_event + " networkServiceHourlyLoadVO=" + networkServiceHourlyLoadVO);
        } catch (Exception e) {
            _log.errorTrace("increaseRechargeHourlyCounters: Exception print stack trace:=", e);
            _log.error("increaseRechargeHourlyCounters", "Getting exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ReqNetworkServiceLoadController[increaseRechargeHourlyCounters]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    public String getHourFromTransactionID(String txnID) {

        String[] testArray = txnID.split("\\.");
        return (String) testArray[1].substring(0, 2);

    }

    public static void decrementUnderProcessCounters(String p_instanceID, String p_reqType, String p_networkID, String p_serviceType, String p_requestID, int p_event, long p_serviceTime, boolean p_isRequestSuccess, String p_networkFor) {
        if (_log.isDebugEnabled())
            _log.debug("decrementUnderProcessCounters", "Entered with p_instanceID=" + p_instanceID + " p_reqType:" + p_reqType + "p_networkID=" + p_networkID + "p_serviceType=" + p_serviceType + "p_requestID=" + p_requestID + "p_event=" + p_event + "p_isRequestSuccess=" + p_isRequestSuccess + "p_serviceTime=" + p_serviceTime + "p_networkFor=" + p_networkFor);
        try {
            p_instanceID = LoadControllerCache.getInstanceID();

            NetworkServiceLoadVO networkServiceLoadVO = (NetworkServiceLoadVO) LoadControllerCache.getNetworkServiceLoadHash().get(p_instanceID + "_" + p_reqType + "_" + p_networkID + "_" + p_serviceType);
            if (networkServiceLoadVO == null)
                networkServiceLoadVO = (NetworkServiceLoadVO) LoadControllerCache.getNetworkServiceLoadHash().get(p_instanceID + "_OTHERS");

            if (_log.isDebugEnabled())
                _log.debug("decrementUnderProcessCounters", "p_requestID=" + p_requestID + "p_event=" + p_event + " networkServiceLoadVO=" + networkServiceLoadVO);

            if (p_event == LoadControllerI.COUNTER_UNDERPROCESS_REQUEST) {
                networkServiceLoadVO.setUnderProcessCount(networkServiceLoadVO.getUnderProcessCount() - 1);
                networkServiceLoadVO.setLastRequestID(p_requestID);
            }
            if (_log.isDebugEnabled())
                _log.debug("decrementUnderProcessCounters", "Exiting for p_requestID=" + p_requestID + "p_event=" + p_event + " networkServiceLoadVO=" + networkServiceLoadVO);
        } catch (Exception e) {
            _log.errorTrace("decrementUnderProcessCounters: Exception print stack trace:=", e);
            _log.error("decrementUnderProcessCounters", "Getting exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ReqNetworkServiceLoadController[decrementUnderProcessCounters]", "", "", "", "Exception:" + e.getMessage());
        }
    }
}
