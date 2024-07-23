package com.apicontrollers.extgw.VMS.VoucherExpiryExtension;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.apicontrollers.extgw.VMS.EXTGW_VOUCHEREXPIRYEXTENSION;
import com.apicontrollers.extgw.VMS.EXTGW_VOUCHEREXPIRYEXT_DP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pretupsControllers.BTSLUtil;
import com.utils.Assertion;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_VoucherExpiryExtension extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	
	@Test
	public void _01_voucherExpiry() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHEREXPEXT01");
		EXTGW_VOUCHEREXPIRYEXTENSION expiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		apiData.put(expiryExtAPI.LOGINID, "");
		apiData.put(expiryExtAPI.PASSWORD, "");	
		//apiData.put(expiryExtAPI.VOUCHER_TYPE, "VF");	
		String serialno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.GENERATED);
		apiData.put(expiryExtAPI.VOUCHER_TYPE,DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "voucher_type")[0]);
		apiData.put(expiryExtAPI.FROM_SERIALNO, serialno);
		apiData.put(expiryExtAPI.TO_SERIALNO,serialno);
		
		String newdate = new NetworkAdminHomePage(driver).addDaysToCurrentDate(_APIUtil.getCurrentTimeStamp(),2);
		
		apiData.put(expiryExtAPI.NEW_EXPIRY_DATE,new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(newdate)));
		apiData.put(expiryExtAPI.DATE,_APIUtil.getCurrentTimeStamp());
		
		String API = expiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "expiry_date","consume_before");
			
		Date date=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String exp_date = new SimpleDateFormat("dd/MM/yyyy").format(date);
		String cons_date = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[1]));
		
		Validator.messageCompare(xmlPath.get(expiryExtAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(exp_date, apiData.get(expiryExtAPI.NEW_EXPIRY_DATE));
		Validator.messageCompare(cons_date, apiData.get(expiryExtAPI.NEW_EXPIRY_DATE));
		}
	
	
	@Test
	public void _02_voucherExpiry() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHEREXPEXT02");
		EXTGW_VOUCHEREXPIRYEXTENSION expiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		apiData.put(expiryExtAPI.LOGINID, "");
		apiData.put(expiryExtAPI.PASSWORD, "");	
		//apiData.put(expiryExtAPI.VOUCHER_TYPE, "VF");
		String serialno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.CONSUMED);
		if(BTSLUtil.isNullString(serialno)){
			Assertion.assertSkip("Voucher with [Consumed] status not found in the system.");
		}
		else{
		apiData.put(expiryExtAPI.VOUCHER_TYPE,DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "voucher_type")[0]);
		apiData.put(expiryExtAPI.FROM_SERIALNO, serialno);
		apiData.put(expiryExtAPI.TO_SERIALNO,serialno);
		String newdate = new NetworkAdminHomePage(driver).addDaysToCurrentDate(_APIUtil.getCurrentTimeStamp(),2);
		apiData.put(expiryExtAPI.NEW_EXPIRY_DATE,new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(newdate)));
		apiData.put(expiryExtAPI.DATE,_APIUtil.getCurrentTimeStamp());
		
		String API = expiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			
		Validator.messageCompare(xmlPath.get(expiryExtAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());}
		
		}
	
	@Test
	public void _03_voucherExpiry() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHEREXPEXT03");
		EXTGW_VOUCHEREXPIRYEXTENSION expiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		apiData.put(expiryExtAPI.LOGINID, "");
		apiData.put(expiryExtAPI.PASSWORD, "");		
		//apiData.put(expiryExtAPI.VOUCHER_TYPE, "VF");
		String serialno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.STOLEN);
		if(BTSLUtil.isNullString(serialno)){
			Assertion.assertSkip("Voucher with [Stolen] status not found in the system.");
		}
		else{
		apiData.put(expiryExtAPI.VOUCHER_TYPE,DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "voucher_type")[0]);
		apiData.put(expiryExtAPI.FROM_SERIALNO, serialno);
		apiData.put(expiryExtAPI.TO_SERIALNO,serialno);
		String newdate = new NetworkAdminHomePage(driver).addDaysToCurrentDate(_APIUtil.getCurrentTimeStamp(),2);
		apiData.put(expiryExtAPI.NEW_EXPIRY_DATE,new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(newdate)));
		apiData.put(expiryExtAPI.DATE,_APIUtil.getCurrentTimeStamp());
		
		String API = expiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			
		Validator.messageCompare(xmlPath.get(expiryExtAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());}
		
		}
	
	@Test
	public void _04_voucherExpiry() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHEREXPEXT04");
		EXTGW_VOUCHEREXPIRYEXTENSION expiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		apiData.put(expiryExtAPI.LOGINID, "");
		apiData.put(expiryExtAPI.PASSWORD, "");	
		//apiData.put(expiryExtAPI.VOUCHER_TYPE, "VF");
		String serialno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ENABLE);
		apiData.put(expiryExtAPI.VOUCHER_TYPE,DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "voucher_type")[0]);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "expiry_date","consume_before");
		
		Date date=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String exp_date = new SimpleDateFormat("dd/MM/yyyy").format(date);
		
		String newdate = new NetworkAdminHomePage(driver).addDaysToCurrentDate(exp_date, 2);
		
		String extDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(newdate));
		
		apiData.put(expiryExtAPI.FROM_SERIALNO, serialno);
		apiData.put(expiryExtAPI.TO_SERIALNO,serialno);
		apiData.put(expiryExtAPI.NEW_EXPIRY_DATE,extDate);
		apiData.put(expiryExtAPI.DATE,_APIUtil.getCurrentTimeStamp());
		
		String API = expiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			
		String values1[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "expiry_date","consume_before");
		
		Date date1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[0]);  
		String exp_date1 = new SimpleDateFormat("dd/MM/yyyy").format(date1);
		String cons_date = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[1]));
		
		Validator.messageCompare(xmlPath.get(expiryExtAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(exp_date1, apiData.get(expiryExtAPI.NEW_EXPIRY_DATE));
		Validator.messageCompare(cons_date, apiData.get(expiryExtAPI.NEW_EXPIRY_DATE));
		
		}
	
	@Test
	public void _05_voucherExpiry() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHEREXPEXT05");
		EXTGW_VOUCHEREXPIRYEXTENSION expiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		apiData.put(expiryExtAPI.LOGINID, "");
		apiData.put(expiryExtAPI.PASSWORD, "");	
		//apiData.put(expiryExtAPI.VOUCHER_TYPE, "VF");
		String serialno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ENABLE);
		apiData.put(expiryExtAPI.VOUCHER_TYPE,DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "voucher_type")[0]);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "expiry_date","consume_before");
		
		Date date=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String exp_date = new SimpleDateFormat("dd/MM/yyyy").format(date);
		
		String newdate = new NetworkAdminHomePage(driver).addDaysToCurrentDate(exp_date, -1);
		
		String extDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(newdate));
		
		apiData.put(expiryExtAPI.FROM_SERIALNO, serialno);
		apiData.put(expiryExtAPI.TO_SERIALNO,serialno);
		apiData.put(expiryExtAPI.NEW_EXPIRY_DATE,extDate);
		apiData.put(expiryExtAPI.DATE,_APIUtil.getCurrentTimeStamp());
		
		String API = expiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			
		String values1[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "expiry_date","consume_before");
		
		Date date1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[0]);  
		String exp_date1 = new SimpleDateFormat("dd/MM/yyyy").format(date1);
		String cons_date = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[1]));
		
		Validator.messageCompare(xmlPath.get(expiryExtAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(exp_date1, apiData.get(expiryExtAPI.NEW_EXPIRY_DATE));
		Validator.messageCompare(cons_date, apiData.get(expiryExtAPI.NEW_EXPIRY_DATE));
		
		}
	
	@Test(dataProvider  = "positiveData")
	public void _06_voucherExpiry(String status) throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHEREXPEXT06");
		EXTGW_VOUCHEREXPIRYEXTENSION expiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),status));
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		apiData.put(expiryExtAPI.LOGINID, "");
		apiData.put(expiryExtAPI.PASSWORD, "");	
		//apiData.put(expiryExtAPI.VOUCHER_TYPE, "VF");
		String serialno = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
		if(BTSLUtil.isNullString(serialno)){
			Assertion.assertSkip("Voucher with ["+status+"] status not found in the system.");
		}
		else{
		apiData.put(expiryExtAPI.VOUCHER_TYPE,DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "voucher_type")[0]);
		String newdate = new NetworkAdminHomePage(driver).addDaysToCurrentDate(_APIUtil.getCurrentTimeStamp(),2);
				
		String extDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yy").parse(newdate));
		
		apiData.put(expiryExtAPI.FROM_SERIALNO, serialno);
		apiData.put(expiryExtAPI.TO_SERIALNO,serialno);
		apiData.put(expiryExtAPI.NEW_EXPIRY_DATE,extDate);
		apiData.put(expiryExtAPI.DATE,_APIUtil.getCurrentTimeStamp());
		
		String API = expiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			
		String values1[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "expiry_date","consume_before");
		
		Date date1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[0]);  
		String exp_date1 = new SimpleDateFormat("dd/MM/yyyy").format(date1);
		String cons_date = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[1]));
		
		Validator.messageCompare(xmlPath.get(expiryExtAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(exp_date1, apiData.get(expiryExtAPI.NEW_EXPIRY_DATE));
		Validator.messageCompare(cons_date, apiData.get(expiryExtAPI.NEW_EXPIRY_DATE));}
		
		}
	
	@DataProvider(name="positiveData")
	public String[] dataMethod(){
		String [] data = new String[]{
				PretupsI.GENERATED,
				PretupsI.SUSPENDED,
				PretupsI.ONHOLD,
				PretupsI.UNDER_PROCESS
		};
		
		return data;
	}
	
}