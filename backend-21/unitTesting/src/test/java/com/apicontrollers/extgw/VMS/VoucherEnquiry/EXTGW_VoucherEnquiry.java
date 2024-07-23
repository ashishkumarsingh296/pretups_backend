package com.apicontrollers.extgw.VMS.VoucherEnquiry;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_VoucherEnquiry extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	
	@Test
	public void _01_voucherEnquiry() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERENQ01");
		EXTGW_VoucherEnquiry_API enquiryAPI = new EXTGW_VoucherEnquiry_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherEnquiry_DP.getAPIdata();
		String serialno = apiData.get(enquiryAPI.SNO);
		apiData.put(enquiryAPI.PIN, "");
		String API = enquiryAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "expiry_date","mrp","previous_status","current_status","product_id");
			
		/*Date date=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String exp_date = new SimpleDateFormat("dd/MM/yyyy").format(date);*/
		
		Validator.messageCompare(xmlPath.get(enquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(enquiryAPI.SERIALNO).toString(), serialno);
		Validator.messageCompare(xmlPath.get(enquiryAPI.STATUS).toString(), values[3]);
		Validator.messageCompare(xmlPath.get(enquiryAPI.STATUS_DESCRIPTION).toString(), DBHandler.AccessHandler.getLookUpName(values[3],PretupsI.VOMS_STATUS_LOOKUPS));
		Validator.messageCompare(xmlPath.get(enquiryAPI.VOUCHERPROFILEID).toString(), values[4]);
		/*Validator.messageCompare(xmlPath.get(enquiryAPI.VOUCHER_EXPIRY_DATE).toString(), exp_date.toString());*/
	}
	
	@Test
	public void _02_voucherEnquiry() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERENQ02");
		EXTGW_VoucherEnquiry_API enquiryAPI = new EXTGW_VoucherEnquiry_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherEnquiry_DP.getAPIdata();
		String serialno = apiData.get(enquiryAPI.SNO);
		apiData.put(enquiryAPI.SNO, "");
		String API = enquiryAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialno, "expiry_date","mrp","previous_status","current_status","product_id");
		
		/*Date date=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String exp_date = new SimpleDateFormat("dd/MM/yyyy").format(date);*/
		
		Validator.messageCompare(xmlPath.get(enquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(enquiryAPI.SERIALNO).toString(), serialno);
		Validator.messageCompare(xmlPath.get(enquiryAPI.STATUS).toString(), values[3]);
		Validator.messageCompare(xmlPath.get(enquiryAPI.STATUS_DESCRIPTION).toString(), DBHandler.AccessHandler.getLookUpName(values[3],PretupsI.VOMS_STATUS_LOOKUPS));
		Validator.messageCompare(xmlPath.get(enquiryAPI.VOUCHERPROFILEID).toString(), values[4]);
		/*Validator.messageCompare(xmlPath.get(enquiryAPI.VOUCHER_EXPIRY_DATE).toString(), exp_date.toString());*/
	}
	
}