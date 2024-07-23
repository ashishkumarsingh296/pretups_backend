package com.testmanagement.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestResult;

import com.commons.MasterI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.testmanagement.core.TestManager;
import com.testmanagement.entity.XRayTest;
import com.utils._masterVO;

public class JIRAUtil {
		
    public String getSession(Method method) {
    	TestManager manager = method.getAnnotation(TestManager.class);
        if (manager == null) {
            return null;
        }
        return manager.TestKey();
    }
    
    public static String toXrayStatus(int status) {
		if (status == ITestResult.SUCCESS) {
			return "Pass";
		} else if (status == ITestResult.FAILURE) {
			return "Fail";
		} else if (status == ITestResult.SKIP) {
			return "Skip";
		} else {
			return "";
		}
    }
    
    public static JSONObject mapToJSON(HashMap<String, XRayTest> map,String category) {
    	
    	JSONObject jsonObject = new JSONObject();
    	JSONArray jsonArray = new JSONArray();
    	
    	Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            XRayTest TestInstance = (XRayTest) pair.getValue();
            
            JSONObject testObject = new JSONObject();
            testObject.put(XRayTest.TestKey, TestInstance.getTestkey());
            testObject.put(XRayTest.StartTime, TestInstance.getStarttime());
            testObject.put(XRayTest.EndTime, TestInstance.getEndtime());
            testObject.put(XRayTest.TestComment, TestInstance.getTestcomment());
            testObject.put(XRayTest.TestStatus, TestInstance.getTeststatus());
	   	        
            jsonArray.put(testObject);
            it.remove();
        }
        
        jsonObject.put("tests", jsonArray);
        if(MasterI.PreRequisite_TEST_EXECUTION_ID.contains(category)) {
    	jsonObject.put("testExecutionKey", _masterVO.getMasterValue(MasterI.PreRequisite_TEST_EXECUTION_ID));
        }
        else if(MasterI.MAPPGW_TEST_EXECUTION_ID.contains(category)) {
    	jsonObject.put("testExecutionKey", _masterVO.getMasterValue(MasterI.MAPPGW_TEST_EXECUTION_ID));
        }
        else if(MasterI.REST_TEST_EXECUTION_ID.contains(category)) {
        	jsonObject.put("testExecutionKey", _masterVO.getMasterValue(MasterI.REST_TEST_EXECUTION_ID));
            }
        else if(MasterI.SMOKE_TEST_EXECUTION_ID.contains(category)) {
        	jsonObject.put("testExecutionKey", _masterVO.getMasterValue(MasterI.SMOKE_TEST_EXECUTION_ID));
            }
        else if(MasterI.UAP_TEST_EXECUTION_ID.contains(category)) {
        	jsonObject.put("testExecutionKey", _masterVO.getMasterValue(MasterI.UAP_TEST_EXECUTION_ID));
            }
        else if(MasterI.SIT_TEST_EXECUTION_ID.contains(category)) {
        	jsonObject.put("testExecutionKey", _masterVO.getMasterValue(MasterI.SIT_TEST_EXECUTION_ID));
            }
    	return jsonObject;
    }
    
    public static String prettyJSON(String json) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(json);

		return gson.toJson(je);
    }
}
