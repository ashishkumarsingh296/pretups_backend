package com.testscripts.sit;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.O2CReturn;
import com.Features.mapclasses.OperatorToChannelMap;
import com.businesscontrollers.UserTransferCountsVO;
import com.classes.BaseTest;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_UserTransferCountValidationO2CReturn extends BaseTest {
	
	static boolean testCaseCounter = false, directO2CPreference;
	
	long initiationAmount = 0;
	UserTransferCountsVO preUserTrfCountsVO = new UserTransferCountsVO();
	UserTransferCountsVO postUserTrfCountsVO = new UserTransferCountsVO();
	String assignCategory="SIT";
	
	@Test(priority=1)
	public void CASEA_DailyOutTransferCount() throws InterruptedException {
		final String methodname = "CASEA_DailyOutTransferCount";
		Log.startTestCase(methodname);

		if (!testCaseCounter) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2CRET1").getModuleCode());
			testCaseCounter = true;
		}
		
		O2CReturn O2CReturnFeature = new O2CReturn(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2CRET1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CReturnCode"));
		preUserTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(transferMap.get("TO_USER_NAME"));
		
		initiationAmount = 100;
		O2CReturnFeature.performO2CReturn(transferMap.get("TO_PARENT_CATEGORY"), transferMap.get("TO_CATEGORY"), null, transferMap.get("PRODUCT_TYPE"), String.valueOf(initiationAmount), "SIT User TransferCount Testing");	
		postUserTrfCountsVO  = DBHandler.AccessHandler.getUserTransferCounts(transferMap.get("TO_USER_NAME"));
		
		Validator.messageCompare(String.valueOf(postUserTrfCountsVO.getWeeklyTransferOutCount()), String.valueOf(preUserTrfCountsVO.getWeeklyTransferOutCount()));
		Log.endTestCase(methodname);		
	}
	
	@Test(priority=2)
	public void CASEB_WeeklyOutTransferCount() throws InterruptedException {
		final String methodname = "CASEB_WeeklyOutTransferCount";
		Log.startTestCase(methodname);

		if (!testCaseCounter) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2CRET1").getModuleCode());
			testCaseCounter = true;
		}
		 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2CRET2").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Validator.messageCompare(String.valueOf(postUserTrfCountsVO.getMonthlyTransferOutCount()), String.valueOf(preUserTrfCountsVO.getMonthlyTransferOutCount()));
		Log.endTestCase(methodname);		
	}
	
	@Test(priority=3)
	public void CASEB_MonthlyOutTransferCount() throws InterruptedException {
		final String methodname = "CASEC_MonthlyOutTransferCount";
		Log.startTestCase(methodname);

		if (!testCaseCounter) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2CRET1").getModuleCode());
			testCaseCounter = true;
		}
		 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2CRET3").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Validator.messageCompare(String.valueOf(postUserTrfCountsVO.getDailyTransferOutCount()), String.valueOf(preUserTrfCountsVO.getDailyTransferOutCount()));
		Log.endTestCase(methodname);		
	}
}
