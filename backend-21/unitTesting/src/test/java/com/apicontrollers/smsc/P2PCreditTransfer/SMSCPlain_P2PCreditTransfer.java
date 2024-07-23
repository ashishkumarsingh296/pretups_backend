package com.apicontrollers.smsc.P2PCreditTransfer;

import java.sql.SQLException;
import java.text.ParseException;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

public class SMSCPlain_P2PCreditTransfer extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC1_PositiveEVDAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC01");//Correct CASE ID
        SMSCPlain_P2PCreditTransfer_API EVDAPI = new SMSCPlain_P2PCreditTransfer_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        String apiData = SMSCPlain_P2PCreditTransfer_DP.getAPIdata();

        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainSMSCAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

}
