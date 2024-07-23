package com.apicontrollers.extgw.VMS.VoucherStatusChange;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.utils.Assertion;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_VoucherStatusChange extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	
	@Test(dataProvider="positivedatavoucherstatuschange")
	public void _01_voucherStatusChange(String fromStatus,String toStatus, String caseID) throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseID);
		EXTGW_VoucherStatusChange_API statusChangeAPI = new EXTGW_VoucherStatusChange_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherStatusChange_DP.getAPIdata();
		
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(fromStatus);
		if(BTSLUtil.isNullString(sno)){
			Assertion.assertSkip("Voucher with ["+fromStatus+"] status not found in the system.");
		}
		else{
		apiData.put(statusChangeAPI.FROM_SERIALNO, sno);
		apiData.put(statusChangeAPI.TO_SERIALNO, sno);
		apiData.put(statusChangeAPI.STATUS,toStatus);
			

		String API = statusChangeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			
		Validator.messageCompare(xmlPath.get(statusChangeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(statusChangeAPI.PRE_STATUS).toString(), fromStatus);
		Validator.messageCompare(xmlPath.get(statusChangeAPI.REQ_STATUS).toString(), toStatus);
		Validator.messageCompare(xmlPath.get(statusChangeAPI.FROM_SERIALNO_RESP).toString(), apiData.get(statusChangeAPI.FROM_SERIALNO));
		Validator.messageCompare(xmlPath.get(statusChangeAPI.TO_SERIALNO_RESP).toString(), apiData.get(statusChangeAPI.TO_SERIALNO));}
		}
	
	@Test(dataProvider="negativedatavoucherstatuschange")
	public void _02_voucherStatusChange(String fromStatus,String toStatus, String caseID) throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseID);
		EXTGW_VoucherStatusChange_API statusChangeAPI = new EXTGW_VoucherStatusChange_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherStatusChange_DP.getAPIdata();
		
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(fromStatus);
		if(BTSLUtil.isNullString(sno)){
			Assertion.assertSkip("Voucher with ["+fromStatus+"] status not found in the system.");
		}
		else{
		apiData.put(statusChangeAPI.FROM_SERIALNO, sno);
		apiData.put(statusChangeAPI.TO_SERIALNO, sno);
		apiData.put(statusChangeAPI.STATUS,toStatus);
			

		String API = statusChangeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			
		Validator.messageCompare(xmlPath.get(statusChangeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(statusChangeAPI.PRE_STATUS).toString(), fromStatus);
		Validator.messageCompare(xmlPath.get(statusChangeAPI.REQ_STATUS).toString(), toStatus);
		Validator.messageCompare(xmlPath.get(statusChangeAPI.FROM_SERIALNO_RESP).toString(), apiData.get(statusChangeAPI.FROM_SERIALNO));
		Validator.messageCompare(xmlPath.get(statusChangeAPI.TO_SERIALNO_RESP).toString(), apiData.get(statusChangeAPI.TO_SERIALNO));}
		}
	
	
	
	@DataProvider(name="positivedatavoucherstatuschange")
	public Object[][] dataVoucherStatus(){
		
		Object[][] vStatusData = new Object[][]{
				{PretupsI.GENERATED,PretupsI.STOLEN,"EXTGWVOUCHERSTCH01"},
				{PretupsI.GENERATED, PretupsI.SUSPENDED,"EXTGWVOUCHERSTCH02"},
				{PretupsI.ENABLE, PretupsI.UNDER_PROCESS,"EXTGWVOUCHERSTCH03"},
				{PretupsI.ENABLE, PretupsI.SUSPENDED,"EXTGWVOUCHERSTCH04"},
				{PretupsI.UNDER_PROCESS, PretupsI.ENABLE,"EXTGWVOUCHERSTCH05"},
				{PretupsI.SUSPENDED, PretupsI.GENERATED,"EXTGWVOUCHERSTCH06"},
				{PretupsI.STOLEN, PretupsI.GENERATED,"EXTGWVOUCHERSTCH07"}
		};
		return vStatusData;
	}
	
	@DataProvider(name="negativedatavoucherstatuschange")
	public Object[][] dataVoucherStatusneg(){
		
		Object[][] vStatusData = new Object[][]{
				{PretupsI.GENERATED,PretupsI.ENABLE,"EXTGWVOUCHERSTCH08"},
				{PretupsI.GENERATED, PretupsI.EXPIRED,"EXTGWVOUCHERSTCH09"},
				{PretupsI.ENABLE, PretupsI.EXPIRED,"EXTGWVOUCHERSTCH10"},
				{PretupsI.UNDER_PROCESS, PretupsI.CONSUMED,"EXTGWVOUCHERSTCH11"},
				{PretupsI.UNDER_PROCESS, PretupsI.ENABLE,"EXTGWVOUCHERSTCH12"},
				{PretupsI.SUSPENDED, PretupsI.ENABLE,"EXTGWVOUCHERSTCH13"},
				{PretupsI.STOLEN, PretupsI.ENABLE,"EXTGWVOUCHERSTCH14"},
		};
		
		
		return vStatusData;
		
	}
	
}