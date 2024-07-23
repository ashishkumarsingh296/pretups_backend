package com.testscripts.sit;

import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.VMS;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_DAY2)
public class SIT_VMS_Day2 extends BaseTest {

	String MasterSheetPath;
	static String directO2CPreference;
	
	@Test
	@TestManager(TestKey = "PRETUPS-771")
		public void CASE1_VoucherBurnRateScript() throws InterruptedException {
			
			final String methodName = "SIT_VMS_DAY2";
			Log.startTestCase(methodName);
			VMS vms = new VMS(driver);
			
			currentNode = test.createNode("Script: VoucherBurnRateSummary.sh");
			currentNode.assignCategory("SIT");

			vms.voucherBurnRateScript();

			Log.endTestCase(methodName);
		}
		
	  @Test(dataProvider="VOMSDENOMPROFILES")
	  @TestManager(TestKey = "PRETUPS-772")
		public void CASE2_VoucherBurnRate(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException, ParseException {
			
		  	final String methodName = "SIT_VMS_DAY2";
	        Log.startTestCase(methodName);
			VMS vms = new VMS(driver);

			currentNode = test.createNode("To verify that Super Admin is able to perform Voucher Burn Rate Indicator for voucherType" + initiateMap.get("voucherType")+", service " + initiateMap.get("service")+" and sub-service "+initiateMap.get("subService"));
			currentNode.assignCategory("SIT");
			initiateMap = vms.voucherBurnRate(initiateMap);
			
			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				Log.info(" Burn Rate Indicator executed successfully with Following Message: " + initiateMap.get("Message"));
				
				//Message Validation Here
			} else 
			{
				Assertion.assertFail("Burn Rate failed with Following Message: " + initiateMap.get("Message"));
				Assertion.completeAssertions();
			}

			Log.endTestCase(methodName);
		}
		
	  @DataProvider(name="VOMSDENOMPROFILES")
		public Object[][] VOMSDenominationProfilesDP() {
			
		  	String PHYSCIAL_VOUCHER_TYPE = "physical";
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
			int rowCount = ExcelUtility.getRowCount();
			Object[][] dataObj = new Object[rowCount][2];
			int objCounter = 0;
			
			for (int i = 1; i <= rowCount; i++) {
				if (ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i).equalsIgnoreCase(PHYSCIAL_VOUCHER_TYPE)) {
					HashMap<String, String> VomsData = new HashMap<String, String>();
					VomsData.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
					VomsData.put("service", ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i));
					VomsData.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
					VomsData.put("denominationName", ExcelUtility.getCellData(0, ExcelI.VOMS_DENOMINATION_NAME, i));
					VomsData.put("shortName", ExcelUtility.getCellData(0, ExcelI.VOMS_SHORT_NAME, i));
					VomsData.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
					String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
					VomsData.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
					String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i);
					String denomination = mrp + ".0";
					VomsData.put("denomination", denomination);
					String productID = activeProfile+"("+denomination+")";
					VomsData.put("productID", productID);
					VomsData.put("payableAmount", String.valueOf(10));
					VomsData.put("description", "Automation Testing");
					VomsData.put("remarks", "Automation Testing");
					VomsData.put("minQuantity", "1");
					VomsData.put("maxQuantity", "60");
					VomsData.put("talkTime", "5");
					VomsData.put("validity", "80");
					VomsData.put("threshold", "10");
					VomsData.put("quantity", "10");
					VomsData.put("expiryPeriod", "90");
					VomsData.put("batchType", "printing");
					VomsData.put("voucherStatus", "Warehouse");
					VomsData.put("viewBatchFor", "N");
					dataObj[objCounter][0] = VomsData.clone();
					dataObj[objCounter][1] = ++objCounter;
					}
			}
			
			return dataObj;
		}
}
