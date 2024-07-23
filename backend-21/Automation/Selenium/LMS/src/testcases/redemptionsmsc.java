package testcases;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.testng.annotations.DataProvider;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Read_Properties_File;
import common_util_script.Read_file;

public class redemptionsmsc extends ExtentReportMultipleClasses {
		
		static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
		
		@Test
		public void script(String mob) throws IOException{
			 test = extent.createTest("To verify that user "+mob+" is able to Optin", "Script should be run successfully from backend");
			common_util_script.Linux_Connect.serverConn("curl 'http://172.16.10.43:5588/pretups/C2SReceiver?MSISDN="+mob+"&MESSAGE=OPTIN+1356&REQUEST_GATEWAY_CODE=SMSC7578&REQUEST_GATEWAY_TYPE=SMSC&SERVICE_PORT=37578&LOGIN=pretups&PASSWORD=pretups123&SOURCE_TYPE=SMS'");
			 
		}
		
		@DataProvider(name = "DP")
	 	 public static String[][] excelRead() throws Exception {
	 		
	 		//read the excel file for invalid credentials
	 		return Read_file.excelRead("demo_data.xlsx","optinout");
	 	}	

	}



