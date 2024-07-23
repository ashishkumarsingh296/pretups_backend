package com.testscripts.sit;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.VMS;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_VOUCHER_STATUS_CHANGE)
public class SIT_VoucherStatusChange extends BaseTest{

	static String moduleCode;
	static String assignCategory="SIT";
	
	@Test(dataProvider = "positivedatavoucherstatuschange")
	@TestManager(TestKey = "PRETUPS-2000")
	public void _01_ChangeVoucherStatus(String fromStatus, String toStatus) throws InterruptedException {
		final String methodName = "_01_ChangeVoucherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERSTATUSCHANGE01");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),fromStatus,toStatus));
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(fromStatus);
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status "+fromStatus+" in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		String mrp1 =  _parser.getDisplayAmount(_parser.getSystemAmount(MRP));
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", toStatus);
		initiateMap.put("voucherType","");
		if(fromStatus.equals(PretupsI.GENERATED))
			initiateMap = vms.changeGeneratedStatus(initiateMap,"");
		else
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "negativedatavoucherstatuschange")
	@TestManager(TestKey = "PRETUPS-2001")
	public void _02_ChangeVoucherStatus(String fromStatus, String toStatus) throws InterruptedException {
		final String methodName = "_01_ChangeVoucherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERSTATUSCHANGE02");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),fromStatus,toStatus));
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(fromStatus);
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status "+fromStatus+" in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		String mrp1 =  _parser.getDisplayAmount(_parser.getSystemAmount(MRP));
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", toStatus);
		initiateMap.put("voucherType","");
		if(fromStatus.equals(PretupsI.GENERATED))
			initiateMap = vms.changeGeneratedStatus(initiateMap,"");
		else
			initiateMap = vms.changeOtherStatusNegative(initiateMap);

		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, fromStatus);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@DataProvider(name="positivedatavoucherstatuschange")
	public Object[][] dataVoucherStatus(){
		
		Object[][] vStatusData = new Object[][]{
				{PretupsI.GENERATED,PretupsI.STOLEN},
				{PretupsI.GENERATED, PretupsI.SUSPENDED},
				{PretupsI.ENABLE, PretupsI.UNDER_PROCESS},
				{PretupsI.ENABLE, PretupsI.SUSPENDED},
				{PretupsI.UNDER_PROCESS, PretupsI.ENABLE},
				{PretupsI.SUSPENDED, PretupsI.GENERATED},
				{PretupsI.STOLEN, PretupsI.GENERATED}
		};
		return vStatusData;
	}
	
	@DataProvider(name="negativedatavoucherstatuschange")
	public Object[][] dataVoucherStatusneg(){
		
		Object[][] vStatusData = new Object[][]{
				{PretupsI.GENERATED,PretupsI.ENABLE},
				{PretupsI.GENERATED, PretupsI.EXPIRED},
				{PretupsI.ENABLE, PretupsI.EXPIRED},
				{PretupsI.UNDER_PROCESS, PretupsI.CONSUMED},
				{PretupsI.UNDER_PROCESS, PretupsI.ENABLE},
				{PretupsI.SUSPENDED, PretupsI.ENABLE},
				{PretupsI.STOLEN, PretupsI.ENABLE},
		};
		
		
		return vStatusData;
		
	}

}
