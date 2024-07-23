package com.inter.vodaidea.vodafone.ericson;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.Constants;

public class ComverseTestClient {
	private static Log _log = LogFactory.getLog(ComverseTestClient.class.getName());

	public static void main(String args[]) 
	{
		HashMap requestMap = new HashMap();
		String propertiesFilePath = null;

		try{
			propertiesFilePath = args[0].trim();
			String action = args[1];
			Properties properties = new Properties();
			File file= new File(propertiesFilePath);//Absolute path
			properties.load(new FileInputStream(file));
			requestMap.put("MSISDN",properties.getProperty("MSISDN"));
			requestMap.put("VALIDITY_DAYS",properties.getProperty("VALIDITY"));
			requestMap.put("INTERFACE_AMOUNT",properties.getProperty("AMOUNT"));
		}
		catch(Exception e){
			//e.printStackTrace();
			System.out.println("Properties file not found: "+propertiesFilePath);
			requestMap.put("ACTION","2");
			requestMap.put("MSISDN","07000150020");
			requestMap.put("INTERFACE_AMOUNT","100");
			requestMap.put("VALIDITY_DAYS","10");
		}
		requestMap.put("IN_TXN_ID","C586666");
		requestMap.put("INTERFACE_ID","INTID00001");
		requestMap.put("MODULE","C2S");
		requestMap.put("TRANSACTION_ID","1500150015001500");
		requestMap.put("INTERFACE_PREV_BALANCE","50");
		requestMap.put("OLD_EXPIRY_DATE","21/08/10");
		requestMap.put("INT_ST_TYPE","A");
		requestMap.put("USER_TYPE","S");
		requestMap.put("REQ_SERVICE","PRC");
		requestMap.put("NETWORK_CODE","MU");
		try{
			Constants.load("C:\\Workspace\\vodafone6\\src\\configfiles\\Constants.props");

			//System.out.println("INTERFACE_DIRECTORY===="+Constants.getProperty("INTERFACE_DIRECTORY"));
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("ComverseTestClient.main() while loading Constants.props Exception e="+e.getMessage());
		}
		try{
			FileCache.loadAtStartUp();
			//System.out.println("INTFCE_CLSR_SUPPORT===="+FileCache.getValue(requestMap.get("INTERFACE_ID").toString(), "INTFCE_CLSR_SUPPORT"));
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("ComverseTestClient.main() while loading IN File Exception e="+e.getMessage());
		}
		//System.out.println("ComverseTestClient.main() before IN requestMap="+requestMap);

		sendRequesttoIN(requestMap);

		System.out.println("ComverseTestClient.main() after IN requestMap="+requestMap);

		System.out.println("ComverseTestClient.main() End...########################");
	}


	public static void sendRequesttoIN(HashMap p_requestMap)
	{
		if(_log.isDebugEnabled()) _log.debug("ComverseTestClient.sendRequesttoIN()","Entered...");
		//System.out.println("ComverseTestClient.sendRequesttoIN() Entered...");

		UCIP4xINHandler inHandler = new UCIP4xINHandler();
		try
		{
			if(p_requestMap.get("ACTION").toString().equalsIgnoreCase("1")){
				inHandler.validate(p_requestMap);
			}
			else if(p_requestMap.get("ACTION").toString().equalsIgnoreCase("2")){
				inHandler.credit(p_requestMap);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("ComverseTestClient.sendRequesttoIN() Exception e="+e.getMessage());
		}
		if(_log.isDebugEnabled()) _log.debug("ComverseTestClient.sendRequesttoIN()","Exited...");
		//System.out.println("ComverseTestClient.sendRequesttoIN() Exited...");
	}


}
