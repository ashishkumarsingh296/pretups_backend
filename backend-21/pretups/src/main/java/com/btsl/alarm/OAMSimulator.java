
package com.btsl.alarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;
import com.btsl.util.OamConstants;

import libSNMPGetSetReqHandler.HandleCallback;
import libSNMPGetSetReqHandler.dataVariables;
import libSNMPGetSetReqHandler.snmpMainHandler;

/**
 * @author samna.soin
 * 
 */
public class OAMSimulator implements HandleCallback 
{
    private static final Log _log = LogFactory.getLog(OAMSimulator.class.getName());
    private  static snmpMainHandler data = null;
    private  String getData = "1";
    private  static final String RAISED = "RAISED";
    private   static final  String CLEARED = "CLEARED";
    private  static final String CLEARED_BY_USER = "CLEARED BY USER";
    private static final int AS_OK = 0;


    private static HashMap<String, Integer> serviceTypeMap = new HashMap<>(); 
                                                                                    
    private  HashMap<Integer, String> serviceTypeValueMap = new HashMap<>();                                                                                      
    private static HashMap<String, Integer> countertypeMap = new HashMap<>();
    private static HashMap<String, Integer> networkTypeMap = new HashMap<>();
    private static HashMap<Integer, String> networkTypeValueMap = new HashMap<>();
    private static Properties properties = new Properties();
    private static HashMap<String, Integer> regData = new HashMap<>();
    private static String instanceRegValue = "1"; 
  /**
   *   
   */
    public OAMSimulator()
    {
    	/**
    	 * default Constructor
    	 */
    }
/***
 * 
 * @param pconstantspropsfile
 */

    public OAMSimulator(String pconstantspropsfile) {
        super();
        final String methodName = "OAMSimulator";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append("Contructor from Configservlet pconstantspropsfile=").append(pconstantspropsfile);
        	_log.debug(methodName, loggerValue);
    	}
        HashMap<String, Integer> mymap = new HashMap<>();
        serviceTypeMap = new HashMap<>();
        countertypeMap = new HashMap<>();
        networkTypeMap = new HashMap<>();
        String mapKey = null;
        try {
            Constants.load(pconstantspropsfile);
        } catch (IOException e) {
            _log.errorTrace(methodName, e);
        } catch (URISyntaxException e) {
        	_log.errorTrace(methodName, e);
		}
        String filePath = Constants.getProperty("COUNTER_CONF_FILE");
        try {
            load(filePath);
        } catch (IOException e) {
            _log.errorTrace(methodName, e);
        }
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            mymap.put(key, Integer.valueOf(value));
        }
        Iterator<String> itr = mymap.keySet().iterator();
        while (itr.hasNext()) {
            mapKey =  itr.next();
            String[] keys = mapKey.split("_");
            if (keys[0].equals(PretupsI.OAM_SERVICE_TYPE)) {
                serviceTypeMap.put(keys[1], mymap.get(mapKey));
                serviceTypeValueMap.put(mymap.get(mapKey), keys[1]);
            } else if (keys[0].equals(PretupsI.OAM_COUNTER_TYPE)) {
                countertypeMap.put(keys[1], mymap.get(mapKey));
            } else if (keys[0].equals(PretupsI.OAM_NETWORK_TYPE)) {
                networkTypeMap.put(keys[1], mymap.get(mapKey));
                networkTypeValueMap.put(mymap.get(mapKey), keys[1]);
            } else if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append(ENTRY_KEY);  
            	loggerValue.append("Configuration for counters key not added, mapKey=").append(mapKey);
            	_log.debug(methodName, loggerValue);
        	
            }
        }
    }
/***
 * public method to start call back
 */
    public void startCallback() {
        final String methodName = "startCallback";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append("Entered call back");
        	_log.debug(methodName, loggerValue);
    	}
        data = new snmpMainHandler(this);        
        String filePath = Constants.getProperty("COUNTER_CONF_FILE");
        String filePathForOAM=Constants.getProperty("OAM_EVENT_PATH");
        try {
            load(filePath);
            OamConstants.load(filePathForOAM);
        } catch (IOException e) {
            _log.errorTrace(methodName, e);
        }
        data.setConfFilePath(Constants.getProperty("COUNTER_SYS_CONF_FILE"));
        data.snmpReqHandler();
        int size = serviceTypeMap.size() * countertypeMap.size() * networkTypeMap.size() + networkTypeMap.size() * serviceTypeMap.size() + networkTypeMap.size() + 1;
        dataVariables dataVars[] = new dataVariables[size];
        dataVars[0] = new dataVariables(snmpMainHandler.CHAR, getData, instanceRegValue);
        regData.put(instanceRegValue, 0);
        int i = 1;
        Iterator<String> netwrkIterator;
        Iterator<String> servIterator ;
        Iterator<String> counterIterator;
        String netwrkKey;
        String servkey;
        String counterkey;
        String reg ;
        netwrkIterator = networkTypeMap.keySet().iterator();
        while (netwrkIterator.hasNext()) {
            netwrkKey = netwrkIterator.next();
            servIterator = serviceTypeMap.keySet().iterator();
            while (servIterator.hasNext()) {
                servkey = servIterator.next();
                counterIterator = countertypeMap.keySet().iterator();
                while (counterIterator.hasNext()) {
                    counterkey = counterIterator.next();
                    StringBuilder regvalue=new StringBuilder("");
                    regvalue.append(instanceRegValue);
                    regvalue.append(".");
                    regvalue.append(networkTypeMap.get(netwrkKey));
                    regvalue.append(".");
                    regvalue.append(serviceTypeMap.get(servkey));
                    regvalue.append(".");
                    regvalue.append(countertypeMap.get(counterkey));
                    reg = regvalue.toString();
                    dataVars[i] = new dataVariables(snmpMainHandler.CHAR, getData, reg);
                    regData.put(reg, i);
                    i++;
                }
            }
        }
        netwrkIterator = networkTypeMap.keySet().iterator();
        while (netwrkIterator.hasNext()) {
            netwrkKey = netwrkIterator.next();
            servIterator = serviceTypeMap.keySet().iterator();
            while (servIterator.hasNext()) {
                servkey = servIterator.next();
                StringBuilder regvalue=new StringBuilder("");
                regvalue.append(instanceRegValue);
                regvalue.append(".");
                regvalue.append(networkTypeMap.get(netwrkKey));
                regvalue.append(".");
                regvalue.append(serviceTypeMap.get(servkey));
                reg = regvalue.toString();
                dataVars[i] =new dataVariables(snmpMainHandler.CHAR, getData, reg);
                regData.put(reg, i);
                i++;
            }
        }
        netwrkIterator = networkTypeMap.keySet().iterator();
        while (netwrkIterator.hasNext()) {
            netwrkKey = netwrkIterator.next();
            reg = instanceRegValue + "." + networkTypeMap.get(netwrkKey);
            dataVars[i] = new dataVariables(snmpMainHandler.CHAR, getData, reg);
            regData.put(reg, i);
            i++;
        }
        // Going to call Register Param
        if (data.snmpRegisterParam(dataVars) == -1) {
            loggerValue.setLength(0);  
        	loggerValue.append("Error in Registering Parameters");
        	_log.info(methodName, loggerValue);
        } else {
            loggerValue.setLength(0);  
        	loggerValue.append("Registering Parameters is Successfully");
        	_log.info(methodName, loggerValue);
        }
    }
/***
 * 
 * @param fileName
 * @throws IOException
 */
    public static void load(String fileName) throws IOException {
    	String methodName = "load";
    	_log.debug(methodName, fileName);
    	File file = null;
    	FileInputStream fileInputStream = null;
    	try{
    		file = new File(fileName);	
    		fileInputStream = new FileInputStream(file);
    		properties.load(fileInputStream);
    	}finally{
    		try {
    			if(fileInputStream != null){
    				fileInputStream.close();
    			}
    			} catch(Exception e) {
				_log.error(methodName, "Exception:e=" + e);
	            _log.errorTrace(methodName, e);
			}
    		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
    	}
    }
    
 
    /***
     * 
     * @param operationId
     * @param varId
     * @param varName
     * @param snmpVarType
     */
    @Override
    public void callBackFunction(int operationId, int varId, String varName, int snmpVarType) {
    	final String methodName = "callBackFunction";
    	final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" operationId=").append(operationId);
        	loggerValue.append(" varId=").append(varId);
        	loggerValue.append(" varName=").append(varName);
        	loggerValue.append(" snmpVarType=").append(snmpVarType);
        	_log.debug(methodName, loggerValue);
    	}
        Counter counter = new Counter();
        String paramName = varName;
        if (operationId == HandleCallback.GET_REQUEST) {
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);  
            	loggerValue.append(" paramName=").append(paramName);
            	_log.debug(methodName, loggerValue);
            }
            getData = counter.getDetails(networkTypeValueMap, serviceTypeValueMap, countertypeMap, (String) properties.get("INSTANCE_ID"), varName);
            Iterator<String> regItr = regData.keySet().iterator();
            while (regItr.hasNext()) {
                String index = regItr.next();
                if (index.equals(varName)) {                    
                    data.snmpUpdateParam(regData.get(index), getData);
                    break;
                }
            }
        }
        
    }
    
    /**
     * 
     * @param pComponentName
     * @param pAlarmID
     * @param pAlarmState
     * @param pAlarmMessage
     * @throws IOException
     */

    public void sendTrap(String pComponentName, int pAlarmID, String pAlarmState, String pAlarmMessage) throws IOException {
        final String methodName = "sendTrap";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" pComponentName=").append(pComponentName);
        	loggerValue.append(" pAlarmID=").append(pAlarmID);
        	loggerValue.append(" pAlarmState=").append(pAlarmState);
        	loggerValue.append(" pAlarmMessage=").append(pAlarmMessage);
        	_log.debug(methodName, loggerValue);
    	}
        try {
            int status = 0;
            int alarmType;
            alarmType=Integer.parseInt(OamConstants.getProperty(String.valueOf(pAlarmID)));
            if (RAISED.equalsIgnoreCase(pAlarmState)) {
                status = 1;
            } else if (CLEARED.equalsIgnoreCase(pAlarmState) || CLEARED_BY_USER.equalsIgnoreCase(pAlarmState)) {
                status = 2;
                alarmType=AS_OK;
            }
            
            
            int ret = data.snmpSendTrap(pAlarmID, alarmType, pComponentName, status, alarmType, pAlarmMessage);

            if (ret == 0) {
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
                	loggerValue.append("Trap sent");
                	_log.debug(methodName, loggerValue);
                } 
            }
            else if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Trap not sent");
            	_log.debug(methodName, loggerValue);
            }

        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
        } finally {
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append("Trap Sent End");
            	_log.debug(methodName, loggerValue);
            }
        }// end of finally
    }
}