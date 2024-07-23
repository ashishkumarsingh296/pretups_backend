package com.selftopup.alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.selftopup.loadcontroller.InstanceLoadVO;
import com.selftopup.loadcontroller.LoadControllerCache;
import com.selftopup.loadcontroller.NetworkLoadVO;
import com.selftopup.loadcontroller.NetworkServiceLoadVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public class Counter extends Thread {
    public Log _log = LogFactory.getLog(this.getClass().getName());
    static ArrayList<HashMap<String, String>> networkServiceCounterStack = null;
    static ArrayList<HashMap<String, String>> instanceCounterStack = null;
    static ArrayList<HashMap<String, String>> networkCounterStack = null;
    public static Properties properties = new Properties();
    Date date = null;
    static SimpleDateFormat sdf = null;
    String time = null;
    public boolean _running = true;

    public Counter() {
        networkServiceCounterStack = new ArrayList<HashMap<String, String>>();
        instanceCounterStack = new ArrayList<HashMap<String, String>>();
        networkCounterStack = new ArrayList<HashMap<String, String>>();
        date = new Date();
        sdf = new SimpleDateFormat("HH:mm:ss");
        time = sdf.format(date);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Counter counter = new Counter();
        counter.start();
    }

    public void run() {
        if (_log.isDebugEnabled()) {
            _log.debug("Counter run method", "Entered");
        }
        Date currDate = null;
        String currtime = null;
        while (_running) {
            HashMap<String, String> networkServiceCounter = null;
            HashMap<String, String> instanceCounter = null;
            HashMap<String, String> networkCounter = null;
            try {
                currDate = new Date();
                currtime = sdf.format(currDate);
                Hashtable networkServiceLoadHash = LoadControllerCache.getNetworkServiceLoadHash();
                Set keySet = networkServiceLoadHash.keySet();
                Iterator tempItr = keySet.iterator();
                NetworkServiceLoadVO networkServiceLoadVO = null;
                String tempkey = null;
                while (tempItr.hasNext()) {
                    networkServiceCounter = new HashMap<String, String>();
                    tempkey = (String) tempItr.next();
                    networkServiceLoadVO = (NetworkServiceLoadVO) networkServiceLoadHash.get(tempkey);
                    String key = networkServiceLoadVO.getInstanceID() + "_" + networkServiceLoadVO.getNetworkCode() + "_" + networkServiceLoadVO.getServiceType() + "_" + currtime;
                    String value = networkServiceLoadVO.getRecievedCount() + "_" + networkServiceLoadVO.getSuccessCount() + "_" + networkServiceLoadVO.getFailCount() + "_" + networkServiceLoadVO.getOthersFailCount();
                    networkServiceCounter.put(key, value);
                    if (networkServiceCounterStack.size() >= Integer.parseInt(Constants.getProperty("COUNTER_TIME_LIMIT")) * 60) {
                        networkServiceCounterStack.remove(0);
                        networkServiceCounterStack.add(networkServiceCounter);
                    } else {
                        networkServiceCounterStack.add(networkServiceCounter);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("networkServiceCounterStack size", networkServiceCounterStack.size());
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("networkServiceCounterStack last value ", networkServiceCounterStack.get(networkServiceCounter.size() - 1));
                    }
                }

                Hashtable instanceLoadHash = LoadControllerCache.getInstanceLoadHash();
                Set insKeySet = instanceLoadHash.keySet();
                Iterator insTempItr = insKeySet.iterator();
                InstanceLoadVO instanceLoadVO = null;
                String insTempkey = null;
                while (insTempItr.hasNext()) {
                    instanceCounter = new HashMap<String, String>();
                    insTempkey = (String) insTempItr.next();
                    instanceLoadVO = (InstanceLoadVO) instanceLoadHash.get(insTempkey);
                    String inskey = instanceLoadVO.getInstanceID() + "_" + currtime;
                    String insvalue = instanceLoadVO.getRecievedCount() + "_" + instanceLoadVO.getRequestCount() + "_" + (instanceLoadVO.getRecievedCount() - instanceLoadVO.getRequestCount()) + "_" + instanceLoadVO.getTotalRefusedCount();
                    instanceCounter.put(inskey, insvalue);
                    if (instanceCounterStack.size() >= Integer.parseInt(Constants.getProperty("COUNTER_TIME_LIMIT")) * 60) {
                        instanceCounterStack.remove(0);
                        instanceCounterStack.add(instanceCounter);
                    } else {
                        instanceCounterStack.add(instanceCounter);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("instanceCounterStack size", instanceCounterStack.size());
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("instanceCounterStack last value ", instanceCounterStack.get(instanceCounter.size() - 1));
                    }
                }

                Hashtable networkLoadHash = LoadControllerCache.getNetworkLoadHash();
                Set netKeySet = networkLoadHash.keySet();
                Iterator netTempItr = netKeySet.iterator();
                NetworkLoadVO networkLoadVO = null;
                String netTempkey = null;
                while (netTempItr.hasNext()) {
                    networkCounter = new HashMap<String, String>();
                    netTempkey = (String) netTempItr.next();
                    networkLoadVO = (NetworkLoadVO) networkLoadHash.get(netTempkey);
                    String netkey = networkLoadVO.getInstanceID() + "_" + networkLoadVO.getNetworkCode() + "_" + currtime;
                    String netvalue = networkLoadVO.getRecievedCount() + "_" + networkLoadVO.getRequestCount() + "_" + (networkLoadVO.getRecievedCount() - networkLoadVO.getRequestCount()) + "_" + networkLoadVO.getTotalRefusedCount();
                    networkCounter.put(netkey, netvalue);
                    if (networkCounterStack.size() >= Integer.parseInt(Constants.getProperty("COUNTER_TIME_LIMIT")) * 60) {
                        networkCounterStack.remove(0);
                        networkCounterStack.add(networkCounter);
                    } else {
                        networkCounterStack.add(networkCounter);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("networkCounterStack size", networkCounterStack.size());
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("networkCounterStack last value ", networkCounterStack.get(networkCounter.size() - 1));
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                _log.errorTrace("run: Exception print stack trace:e=", e);
                try {
                    Thread.sleep(200);
                } catch (Exception ex) {
                    _log.errorTrace("run: Exception print stack trace:ex=", ex);
                }
            }
        }
    }

    public String getDetails(HashMap<Integer, String> p_networkTypeValueMap, HashMap<Integer, String> p_serviceTypeValueMap, HashMap<String, Integer> countertypeMap, String p_instanceId, String p_varName) {
        if (_log.isDebugEnabled()) {
            _log.debug("get details of counter", "Entered");
        }
        String key = null;
        String[] values = null;
        String[] varName = null;
        String counters = "0";
        if (p_varName.length() == 1) {
            p_varName = p_varName + "\\.";
        }
        varName = p_varName.split("\\.");
        int length = varName.length;
        Date currdate = new Date();
        String currtime = sdf.format(currdate);
        switch (length) {
        case 1: {
            key = p_instanceId + "_" + currtime;
            for (int i = 0; i < instanceCounterStack.size(); i++) {
                HashMap<String, String> instanceCounter = instanceCounterStack.get(i);
                String value = instanceCounter.get(key);
                if (!BTSLUtil.isNullString(value)) {
                    values = value.split("_");
                }
            }
            if (!BTSLUtil.isNullArray(values))
                counters = values[0];
            break;
        }
        case 2: {
            String[] varValue = p_varName.split("\\.");
            key = p_instanceId + "_" + p_networkTypeValueMap.get(Integer.parseInt(varValue[1])) + "_" + currtime;
            for (int i = 0; i < networkCounterStack.size(); i++) {
                HashMap<String, String> networkCounter = networkCounterStack.get(i);
                String value = networkCounter.get(key);
                if (!BTSLUtil.isNullString(value)) {
                    values = value.split("_");
                    if (!BTSLUtil.isNullArray(values))
                        counters = "Total = " + values[0] + "Success = " + values[1] + "Fail = " + values[2] + "Refused = " + values[3];
                }
            }
            break;
        }
        case 3: {
            String[] varValue = p_varName.split("\\.");
            key = p_instanceId + "_" + p_networkTypeValueMap.get(Integer.parseInt(varValue[1])) + "_" + p_serviceTypeValueMap.get(Integer.parseInt(varValue[2])) + "_" + currtime;
            for (int i = 0; i < networkServiceCounterStack.size(); i++) {
                HashMap<String, String> networkServiceCounter = networkServiceCounterStack.get(i);
                String value = networkServiceCounter.get(key);
                if (!BTSLUtil.isNullString(value)) {
                    values = value.split("_");
                    if (!BTSLUtil.isNullArray(values))
                        counters = "Total = " + values[0] + "Success = " + values[1] + "Fail = " + values[2] + "Refused = " + values[3];
                }
            }
            break;
        }
        case 4: {
            String[] varValue = p_varName.split("\\.");
            int size = varValue.length - 1;
            key = p_instanceId + "_" + p_networkTypeValueMap.get(Integer.parseInt(varValue[1])) + "_" + p_serviceTypeValueMap.get(Integer.parseInt(varValue[2])) + "_" + currtime;
            for (int i = 0; i < networkServiceCounterStack.size(); i++) {
                HashMap<String, String> networkServiceCounter = networkServiceCounterStack.get(i);
                String value = networkServiceCounter.get(key);
                if (!BTSLUtil.isNullString(value)) {
                    values = value.split("_");
                }
            }
            if (!BTSLUtil.isNullArray(values))
                counters = values[Integer.parseInt(varValue[size]) - 1];
            break;
        }
        default: {
            counters = "Invalid Request";
            break;
        }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("get details of counter exiting with counter value ", counters);
        }
        return counters;
    }

}