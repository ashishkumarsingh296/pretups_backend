package com.apicontrollers.extgw.VMS.VoucherRollback;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_VoucherRollback extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	
	@Test
	public void _01_voucherRollback() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERROLL01");
		EXTGW_VoucherRollback_API rollbackAPI = new EXTGW_VoucherRollback_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRollback_DP.getAPIdata();
		String API = rollbackAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rollbackAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rollbackAPI.SERIALNO).toString(), apiData.get(rollbackAPI.SNO));
		Validator.messageCompare(xmlPath.get(rollbackAPI.PRE_STATE).toString(), PretupsI.UNDER_PROCESS);
		Validator.messageCompare(xmlPath.get(rollbackAPI.CUR_STATE).toString(), PretupsI.ENABLE);
	}
}