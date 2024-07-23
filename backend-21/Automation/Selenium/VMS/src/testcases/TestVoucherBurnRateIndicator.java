package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Read_Excel_file;
import common_util_script.Read_Properties_File;

public class TestVoucherBurnRateIndicator extends common_util_script.ExtentReportMultipleClasses {

	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	
	@BeforeClass
	public void loginAsSuperadmin() throws Exception{
		String user = cacheMap.get("sausername");
   	 	String password = cacheMap.get("sapassword");
   		String URL = cacheMap.get("url");
   		testcases.TC1_Login_with_validcredentials.login_with_valid_credentials(user, password, URL);
	}
	
	@AfterClass
	public void logout() throws Exception{
		testcases.TC_Logout.logout();
	}
	
	@DataProvider(name = "DP")
	 public static String[][] excelRead() throws Exception {		
		return Read_Excel_file.excelRead(cacheMap.get("inputfile"),cacheMap.get("BurnRateIndicator"));
	 	
	}
	
	@Test(dataProvider = "DP")
	public static void burnRateIndicator(String msisdn, String voucherDenomination, String voucherProfile, String distributedFromDate, String distributedToDate, String rechargeFromDate, String rechargeToDate, String scenario) throws IOException{
		FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
	 	Properties prop1 = new Properties();
	 	prop1.load(fileInput1);
	 	test = extent.createTest("Burn Rate Indicator by: "+scenario);
	 	common_features.BurnRateIndicatorFeature.clicklink("Voucher reports");
	 	common_features.BurnRateIndicatorFeature.clicklink("Voucher Burn Rate Indicator");
	 	if("MSISDN".equalsIgnoreCase(scenario)){
	 		common_features.BurnRateIndicatorFeature.enterMsisdn(msisdn);
	 	}
	 	else if("Voucher".equalsIgnoreCase(scenario)){
	 		common_features.BurnRateIndicatorFeature.selectVoucherDetails(voucherDenomination, voucherProfile);
	 	}
	 	else if("Combination".equalsIgnoreCase(scenario)){
	 		common_features.BurnRateIndicatorFeature.enterCombinedDetails(msisdn, voucherDenomination, voucherProfile);
	 	}
	 	common_features.BurnRateIndicatorFeature.enterDates(distributedFromDate, distributedToDate, rechargeFromDate, rechargeToDate);
	 	common_features.BurnRateIndicatorFeature.clickSubmit();
	 	if("MSISDN".equalsIgnoreCase(scenario)){
	 		common_features.BurnRateIndicatorFeature.assertMSISDN(msisdn);
	 	}
	 	else if("Voucher".equalsIgnoreCase(scenario)){
	 		common_features.BurnRateIndicatorFeature.assertVoucherProfile(voucherProfile);
	 	}
	 	else if("Combination".equalsIgnoreCase(scenario)){
	 		common_features.BurnRateIndicatorFeature.assertVoucherProfileMSISDN(msisdn, voucherProfile);
	 	}
	}
	
	
}
