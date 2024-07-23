package com.btsl.alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.loadcontroller.NetworkLoadVO;
import com.btsl.loadcontroller.NetworkServiceLoadVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author ayush.abhijeet
 */
public class Counter extends Thread {
    private final Log LOG = LogFactory.getLog(this.getClass().getName());
    private static ArrayList<HashMap<String, String>> networkServiceCounterStack = null;
    private static ArrayList<HashMap<String, String>> instanceCounterStack = null;
    private static ArrayList<HashMap<String, String>> networkCounterStack = null;
    public static final Properties properties = new Properties();
    private Date date = null;
    private static SimpleDateFormat sdf = null;
    private String time = null;
    private boolean _running = true;

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
        final String METHOD_NAME = "run";
        if (LOG.isDebugEnabled()) {
            LOG.debug("Counter run method", "Entered");
        }
        Date currDate = null;
        String currtime = null;
        while (_running) {
            HashMap<String, String> networkServiceCounter = null;
            HashMap<String, String> instanceCounter = null;
            HashMap<String, String> networkCounter = null;
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            try {
                currDate = new Date();
                currtime = sdf.format(currDate);
                Hashtable networkServiceLoadHash = LoadControllerCache.getNetworkServiceLoadHash();
                Set keySet = networkServiceLoadHash.keySet();
                Iterator tempItr = keySet.iterator();
                NetworkServiceLoadVO networkServiceLoadVO = null;
                String tempkey = null;
                networkServiceCounter = new HashMap<String, String>();
                while (tempItr.hasNext()) {
                    networkServiceCounter.clear();
                    tempkey = (String) tempItr.next();
                    networkServiceLoadVO = (NetworkServiceLoadVO) networkServiceLoadHash.get(tempkey);
                    key.setLength(0);
                    key.append(networkServiceLoadVO.getInstanceID())
                    .append("_")
                    .append(networkServiceLoadVO.getNetworkCode())
                    .append("_")
                    .append(networkServiceLoadVO.getServiceType())
                    .append("_")
                    .append(currtime);
                    
                    value.setLength(0);
                    value.append(networkServiceLoadVO.getRecievedCount())
                    .append("_")
                    .append(networkServiceLoadVO.getSuccessCount())
                    .append("_")
                    .append( networkServiceLoadVO.getFailCount())
                    .append("_")
                    .append(networkServiceLoadVO.getOthersFailCount());
                    
                    networkServiceCounter.put(key.toString(), value.toString());

                    if (networkServiceCounterStack.size() >= Integer.parseInt(Constants.getProperty("COUNTER_TIME_LIMIT")) * 60) {
                        networkServiceCounterStack.remove(0);
                        networkServiceCounterStack.add(networkServiceCounter);
                    } else {
                        networkServiceCounterStack.add(networkServiceCounter);
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("networkServiceCounterStack size", networkServiceCounterStack.size());
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("networkServiceCounterStack last value ", networkServiceCounterStack.get(networkServiceCounter.size() - 1));
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
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("instanceCounterStack size", instanceCounterStack.size());
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("instanceCounterStack last value ", instanceCounterStack.get(instanceCounter.size() - 1));
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
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("networkCounterStack size", networkCounterStack.size());
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("networkCounterStack last value ", networkCounterStack.get(networkCounter.size() - 1));
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                try {
                    Thread.sleep(200);
                } catch (Exception ex) {
                    LOG.errorTrace(METHOD_NAME, ex);
                }
            }
        }
    }

    public String getDetails(HashMap<Integer, String> p_networkTypeValueMap, HashMap<Integer, String> p_serviceTypeValueMap, HashMap<String, Integer> countertypeMap, String p_instanceId, String p_varName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("get details of counter", "Entered");
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
            int sizes =instanceCounterStack.size();
            HashMap<String, String> instanceCounter = new HashMap<String, String>();
            String value = null;
            for (int i = 0; i < sizes; i++) {
            	instanceCounter.clear();
                instanceCounter = instanceCounterStack.get(i);
                value = instanceCounter.get(key);
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
            StringBuilder keyvalue=new StringBuilder("");
            keyvalue.append(p_instanceId);
            keyvalue.append("_");
            keyvalue.append(p_networkTypeValueMap.get(Integer.parseInt(varValue[1])));
            keyvalue.append("_");
            keyvalue.append(currtime);
            key = keyvalue.toString();
            int networkSize=networkCounterStack.size();
            HashMap<String, String> networkCounter = new HashMap<String, String>(); 
            StringBuilder cnt=new StringBuilder("");
            String value = null;
            for (int i = 0; i < networkSize; i++) {
            	networkCounter.clear();
                networkCounter = networkCounterStack.get(i);
                value = networkCounter.get(key);
                if (!BTSLUtil.isNullString(value)) {
                    values = value.split("_");
                    if (!BTSLUtil.isNullArray(values)){
                    	cnt.setLength(0);
                    	cnt.append("Total = ");
                    	cnt.append(values[0]);
                    	cnt.append("Success = ");
                    	cnt.append(values[1]);
                    	cnt.append("Fail = ");
                    	cnt.append(values[2]);
                    	cnt.append("Refused = ");
                    	cnt.append(values[3]);
                        counters = cnt.toString();
                    }
                }
            }
            break;
        }
        case 3: {
            String[] varValue = p_varName.split("\\.");
            StringBuilder keyvalue=new StringBuilder("");
            keyvalue.append(p_instanceId);
            keyvalue.append("_");
            keyvalue.append(p_networkTypeValueMap.get(Integer.parseInt(varValue[1])));
            keyvalue.append("_");
            keyvalue.append(p_serviceTypeValueMap.get(Integer.parseInt(varValue[2])));
            keyvalue.append("_");
            keyvalue.append(currtime);
            key =  keyvalue.toString();
            int networkSizeService=networkServiceCounterStack.size();
            StringBuilder cnt=new StringBuilder("");
            HashMap<String, String> networkServiceCounter = new HashMap<String, String>();
            String value = null;
            for (int i = 0; i <networkSizeService ; i++) {
            	networkServiceCounter.clear();
                networkServiceCounter = networkServiceCounterStack.get(i);
                value = networkServiceCounter.get(key);
                if (!BTSLUtil.isNullString(value)) {
                    values = value.split("_");
                    if (!BTSLUtil.isNullArray(values)){
                    	 cnt.setLength(0);
                	cnt.append("Total = ");
                	cnt.append(values[0]);
                	cnt.append("Success = ");
                	cnt.append(values[1]);
                	cnt.append("Fail = ");
                	cnt.append(values[2]);
                	cnt.append("Refused = ");
                	cnt.append(values[3]);
                    counters = cnt.toString();
                }
                }
            }
            break;
        }
        case 4: {
            String[] varValue = p_varName.split("\\.");
            int size = varValue.length - 1;
            StringBuilder keyvalue=new StringBuilder("");
            keyvalue.append(p_instanceId);
            keyvalue.append("_");
            keyvalue.append(p_networkTypeValueMap.get(Integer.parseInt(varValue[1])));
            keyvalue.append("_");
            keyvalue.append(p_serviceTypeValueMap.get(Integer.parseInt(varValue[2])));
            keyvalue.append("_");
            keyvalue.append(currtime);
            key =  keyvalue.toString();
            int networkSizeServiceCount= networkServiceCounterStack.size();
            HashMap<String, String> networkServiceCounter = new HashMap<String, String>();
            String value =  null;
            for (int i = 0; i <networkSizeServiceCount; i++) {
            	networkServiceCounter.clear();
                networkServiceCounter = networkServiceCounterStack.get(i);
                value = networkServiceCounter.get(key);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("get details of counter exiting with counter value ", counters);
        }
        return counters;
    }

}