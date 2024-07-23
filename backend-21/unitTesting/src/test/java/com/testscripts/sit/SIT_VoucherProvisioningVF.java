package com.testscripts.sit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.sshmanager.SSHService;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Decrypt;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.VoucherFileCreation;
import com.utils._APIUtil;
import com.utils._masterVO;

@ModuleManager(name = "[SIT]Voucher_Provisioning_Activation")
public class SIT_VoucherProvisioningVF extends BaseTest{
	String executedDate = null;
	String provProcessID = "VOULIST";
	String actProcessID = "VOUEN";
	String currentDate = null;
	String assignCategory = "SIT";
	String provisioningScript = "VomsProvisioningprocess.sh";
	String activationScript = "VomsActivationProcess.sh";
	
	@Test
	@TestManager(TestKey = "PRETUPS-2008")
	public void _01_provisioningscriptexecution() throws ParseException{
		
		final String methodname = "_01_provisioningscriptexecution";
		Log.startTestCase(methodname);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITVOUCHERPROVISIONING01").getExtentCase());
		currentNode.assignCategory(assignCategory);
				
		/*DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();*/
		currentDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(_APIUtil.getCurrentTimeStamp()));
		System.out.println(currentDate);
		String currentDate1 = _APIUtil.getCurrentTimeStamp();
		String expiryDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(new NetworkAdminHomePage(driver).addDaysToCurrentDate(currentDate1, 1)));
		
		String records="10";
		String groupID = new RandomGeneration().randomAlphaNumeric(4);
		String provisionFile = "GND_PIN"+new SimpleDateFormat("HHmmss").format(new Date());
		String data[]  = new VoucherFileCreation().createFileforProvisioning(provisionFile, records, "VF", expiryDate,groupID);
		
		Log.info("Executing Script: "+provisioningScript+" | location :"+_masterVO.getMasterValue(MasterI.SCRIPTPATH));
		String pOutpath = SSHService.executeScript(provisioningScript);
		currentNode.log(Status.PASS, "<a href='"+ pOutpath +"'><b><h6><font color='green'>Script Log</font></h6></b></a>");
		executedDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00").parse(DBHandler.AccessHandler.getExecutedDate(provProcessID)));

		System.out.println(currentDate);

		if(!executedDate.equalsIgnoreCase(currentDate))
		{
			Assertion.assertFail("Provisioning Script not executed Properly");
		}else{
			Assertion.assertEquals(executedDate, currentDate);
		}
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITVOUCHERPROVISIONING02").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String groupExist = DBHandler.AccessHandler.checkForProfileName(groupID);
		
		Log.info("Checking for product name in VOMS_VOUCHERS : ");
		if(!groupExist.equals("Y")){
			Assertion.assertFail("Product Name "+groupID+" does not exist in DB.");
		}else{
			Assertion.assertEquals(groupExist, "Y");
		}
		
		Log.info("Validating Voucher data in DB ");
		String SerialNo = data[0]+data[1];
		String validateData[];
		
		for(int i=0;i<Integer.parseInt(records);i++){
		validateData = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(SerialNo, "serial_no","pin_no","current_status","status");
		
		Log.info("Validating Serial No.:");
		Assertion.assertEquals(validateData[0], SerialNo);
		Log.info("Validating PINNO. :");
		Assertion.assertEquals(Decrypt.decryptionVMS(validateData[1]),data[4]);
		Log.info("Validating Current_status: ");
		Assertion.assertEquals(validateData[2], PretupsI.GENERATED);
		Log.info("Validating Status:");
		Assertion.assertEquals(validateData[3],PretupsI.GENERATED);
		
		long serial = Long.parseLong(SerialNo);
		long pin = Long.parseLong(data[4]);
		serial++;pin++;
		SerialNo = String.valueOf(serial);
		data[4]  = String.valueOf(pin);
		}
		
		Assertion.completeAssertions();
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2009")
	public void _02_activationscriptexecution() throws ParseException{
		
		final String methodname = "_02_activationscriptexecution";
		Log.startTestCase(methodname);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITVOUCHERACTIVATION01").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		/*DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		currentDate = dateFormat.format(date);*/
		currentDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(_APIUtil.getCurrentTimeStamp()));
		System.out.println(currentDate);
		String currentDate1 = _APIUtil.getCurrentTimeStamp();
		
		System.out.println(currentDate);
		String expiryDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(new NetworkAdminHomePage(driver).addDaysToCurrentDate(currentDate1, 1)));
		
		String records="10";
		String groupID = new RandomGeneration().randomAlphaNumeric(4);
		String provisionFile = "GND_PIN"+new SimpleDateFormat("HHmmss").format(new Date());
		String data[]  = new VoucherFileCreation().createFileforProvisioning(provisionFile, records, "VF", expiryDate,groupID);
		String activationFile = "GND_RCC"+new SimpleDateFormat("HHmmss").format(new Date())+".ACT";
		 new VoucherFileCreation().createFileforActivation(activationFile, data, "1");

		 Log.info("Executing Script: "+provisioningScript+" | location :"+_masterVO.getMasterValue(MasterI.SCRIPTPATH));
		String pOutpath = SSHService.executeScript(provisioningScript);
		currentNode.log(Status.PASS, "<a href='"+ pOutpath +"'><b><h6><font color='green'>Script Log</font></h6></b></a>");
		executedDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00").parse(DBHandler.AccessHandler.getExecutedDate(provProcessID)));

		System.out.println(currentDate);

		if(!executedDate.equalsIgnoreCase(currentDate))
		{
			Assertion.assertFail("Provisioning Script not executed Properly");
		}else{
			Assertion.assertEquals(executedDate, currentDate);
		}
		
		Log.info("Executing Script: "+activationScript+" | location :"+_masterVO.getMasterValue(MasterI.SCRIPTPATH));
		String aOutpath = SSHService.executeScript(activationScript);
		currentNode.log(Status.PASS, "<a href='"+ aOutpath +"'><b><h6><font color='green'>Script Log</font></h6></b></a>");
		executedDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00").parse(DBHandler.AccessHandler.getExecutedDate(actProcessID)));

		if(!executedDate.equalsIgnoreCase(currentDate))
		{
			Assertion.assertFail("Activation Script not executed Properly");
		}else{
			Assertion.assertEquals(executedDate, currentDate);
		}
		
		Log.info("Validating Voucher data in DB ");
		String SerialNo = data[0]+data[1];
		String validateData[];
		
		for(int i=0;i<Integer.parseInt(records);i++){
		validateData = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(SerialNo, "serial_no","pin_no","current_status","status","previous_status");
		
		Log.info("Validating Serial No.:");
		Assertion.assertEquals(validateData[0], SerialNo);
		Log.info("Validating PINNO. :");
		Assertion.assertEquals(Decrypt.decryptionVMS(validateData[1]),data[4]);
		Log.info("Validating Current_status: ");
		Assertion.assertEquals(validateData[2], PretupsI.ENABLE);
		Log.info("Validating Status:");
		Assertion.assertEquals(validateData[3],PretupsI.ENABLE);
		Log.info("Validating Previous Status:");
		Assertion.assertEquals(validateData[4],PretupsI.GENERATED);
		
		long serial = Long.parseLong(SerialNo);
		long pin = Long.parseLong(data[4]);
		serial++;pin++;
		SerialNo = String.valueOf(serial);
		data[4]  = String.valueOf(pin);
		}
		
		Assertion.completeAssertions();
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2010")
	public void _03_activationscriptexecution() throws ParseException{
		
		final String methodname = "_03_activationscriptexecution";
		Log.startTestCase(methodname);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITVOUCHERACTIVATION02").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		/*DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		currentDate = dateFormat.format(date);*/
		currentDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(_APIUtil.getCurrentTimeStamp()));
		System.out.println(currentDate);
		String currentDate1 = _APIUtil.getCurrentTimeStamp();
		System.out.println(currentDate);
		String expiryDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(new NetworkAdminHomePage(driver).addDaysToCurrentDate(currentDate1, 1)));
		
		String records="10";
		String groupID = new RandomGeneration().randomAlphaNumeric(4);
		String provisionFile = "GND_PIN"+new SimpleDateFormat("HHmmss").format(new Date());
		String data[]  = new VoucherFileCreation().createFileforProvisioning(provisionFile, records, "VF", expiryDate,groupID);
		String activationFile = "GND_RCC"+new SimpleDateFormat("HHmmss").format(new Date());
		 new VoucherFileCreation().createFileforActivation(activationFile, data, "1");

		Log.info("Executing Script: "+provisioningScript+" | location :"+_masterVO.getMasterValue(MasterI.SCRIPTPATH));
		String pOutpath = SSHService.executeScript(provisioningScript);
		currentNode.log(Status.PASS, "<a href='"+ pOutpath +"'><b><h6><font color='green'>Script Log</font></h6></b></a>");
		
		executedDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00").parse(DBHandler.AccessHandler.getExecutedDate(provProcessID)));

		System.out.println(currentDate);

		if(!executedDate.equalsIgnoreCase(currentDate))
		{
			Assertion.assertFail("Provisioning Script not executed Properly");
		}else{
			Assertion.assertEquals(executedDate, currentDate);
		}
		
		Log.info("Executing Script: "+activationScript+" | location :"+_masterVO.getMasterValue(MasterI.SCRIPTPATH));
		String aOutpath = SSHService.executeScript(activationScript);
		currentNode.log(Status.PASS, "<a href='"+ aOutpath +"'><b><h6><font color='green'>Script Log</font></h6></b></a>");
		
		executedDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00").parse(DBHandler.AccessHandler.getExecutedDate(actProcessID)));

		if(!executedDate.equalsIgnoreCase(currentDate))
		{
			Assertion.assertFail("Activation Script not executed Properly");
		}else{
			Assertion.assertEquals(executedDate, currentDate);
		}
		
		Log.info("Validating Voucher data in DB ");
		String SerialNo = data[0]+data[1];
		String validateData[];
		
		for(int i=0;i<Integer.parseInt(records);i++){
		validateData = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(SerialNo, "serial_no","pin_no","current_status","status");
		
		Log.info("Validating Serial No.:");
		Assertion.assertEquals(validateData[0], SerialNo);
		Log.info("Validating PINNO. :");
		Assertion.assertEquals(Decrypt.decryptionVMS(validateData[1]),data[4]);
		Log.info("Validating Current_status: ");
		Assertion.assertEquals(validateData[2], PretupsI.ENABLE);
		Log.info("Validating Status:");
		Assertion.assertEquals(validateData[3],PretupsI.ENABLE);
		
		long serial = Long.parseLong(SerialNo);
		long pin = Long.parseLong(data[4]);
		serial++;pin++;
		SerialNo = String.valueOf(serial);
		data[4]  = String.valueOf(pin);
		}
		
		Assertion.completeAssertions();
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2011")
	public void _04_activationscriptexecution() throws ParseException{
		
		final String methodname = "_04_activationscriptexecution";
		Log.startTestCase(methodname);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITVOUCHERACTIVATION03").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		/*DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		currentDate = dateFormat.format(date);*/
		currentDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(_APIUtil.getCurrentTimeStamp()));
		System.out.println(currentDate);
		String currentDate1 = _APIUtil.getCurrentTimeStamp();
		System.out.println(currentDate);
		String expiryDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(new NetworkAdminHomePage(driver).addDaysToCurrentDate(currentDate1, 1)));
		
		String records="10";
		String groupID = new RandomGeneration().randomAlphaNumeric(4);
		String provisionFile = "GND_PIN"+new SimpleDateFormat("HHmmss").format(new Date());
		String data[]  = new VoucherFileCreation().createFileforProvisioning(provisionFile, records, "VF", expiryDate,groupID);
		
		Log.info("Executing Script: "+provisioningScript+" | location :"+_masterVO.getMasterValue(MasterI.SCRIPTPATH));
		String pOutpath = SSHService.executeScript(provisioningScript);
		currentNode.log(Status.PASS, "<a href='"+ pOutpath +"'><b><h6><font color='green'>Script Log</font></h6></b></a>");
		
		executedDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00").parse(DBHandler.AccessHandler.getExecutedDate(provProcessID)));

		System.out.println(currentDate);

		if(!executedDate.equalsIgnoreCase(currentDate))
		{
			Assertion.assertFail("Provisioning Script not executed Properly");
		}else{
			Assertion.assertEquals(executedDate, currentDate);
		}
		
		Log.info("Validating Voucher data in DB ");
		String SerialNo = data[0]+data[1];
		String validateData[];
		
		for(int i=0;i<Integer.parseInt(records);i++){
		validateData = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(SerialNo, "serial_no","pin_no","current_status","status");
		
		Log.info("Validating Serial No.:");
		Assertion.assertEquals(validateData[0], SerialNo);
		Log.info("Validating PINNO. :");
		Assertion.assertEquals(Decrypt.decryptionVMS(validateData[1]),data[4]);
		Log.info("Validating Current_status: ");
		Assertion.assertEquals(validateData[2], PretupsI.GENERATED);
		Log.info("Validating Status:");
		Assertion.assertEquals(validateData[3],PretupsI.GENERATED);
		
		long serial = Long.parseLong(SerialNo);
		long pin = Long.parseLong(data[4]);
		serial++;pin++;
		SerialNo = String.valueOf(serial);
		data[4]  = String.valueOf(pin);
		}
		
		data[0] = UniqueChecker.UC_SerialNumber();
		String activationFile = "GND_RCC"+new SimpleDateFormat("HHmmss").format(new Date())+".ACT";
		 new VoucherFileCreation().createFileforActivation(activationFile, data, "1");
		
		Log.info("Executing Script: "+activationScript+" | location :"+_masterVO.getMasterValue(MasterI.SCRIPTPATH));
		String aOutpath = SSHService.executeScript(activationScript);
		currentNode.log(Status.PASS, "<a href='"+ aOutpath +"'><b><h6><font color='green'>Script Log</font></h6></b></a>");
		
		executedDate =  new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00").parse(DBHandler.AccessHandler.getExecutedDate(actProcessID)));

		if(!executedDate.equalsIgnoreCase(currentDate))
		{
			Assertion.assertFail("Activation Script not executed Properly");
		}else{
			Assertion.assertEquals(executedDate, currentDate);
		}
		
		Log.info("Validating Voucher data in DB ");
		SerialNo = data[0]+data[1];
		
		validateData = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(SerialNo, "serial_no","pin_no","current_status","status");
		
		Log.info("Validating Serial No.:");
		if(BTSLUtil.isNullString(validateData[0])){
			Assertion.assertPass("Serial no. does not exist in database with batch number in activation file different from batch number in provisioning file");
		}
		
		
		Assertion.completeAssertions();
	}
	
	
}
