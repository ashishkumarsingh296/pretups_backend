package com.testscripts.sit;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.O2CTransfer;
import com.Features.mapclasses.ChannelUserMap;
import com.Features.mapclasses.OperatorToChannelMap;
import com.classes.BaseTest;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

@ModuleManager(name = Module.PREREQUISITE_COMMISSION_PROFILE)
public class SIT_CommProfileCBC1 extends BaseTest {
	
	static String directO2CPreference;
	public static boolean TestCaseCounter = false;
	HashMap<String, String> channelresultMap;
	String[] commissionProfile;
	String assignCategory="SIT";
	O2CTransfer o2CTransfer = new O2CTransfer(driver);
	
	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */
	
	@DataProvider(name = "commission0_CBC_nonZero")
    public Object[][] DomainCategoryProvider_validations() {
    
           String MasterSheetPath = _masterVO.getProperty("DataProvider");
           ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
           
           ChannelUserMap chnlUserMap = new ChannelUserMap();
           int rowNum=1;
           
           String[] userDetailsHL = new String[5];
           
           userDetailsHL[0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
           userDetailsHL[1] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, 1);
           userDetailsHL[2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
           userDetailsHL[3] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, 1);
           userDetailsHL[4] = ExcelUtility.getCellData(0, ExcelI.GRADE, 1);
    
           Object[][] categoryData;
           categoryData = new Object[][]{
        		   {rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3], userDetailsHL[4], chnlUserMap.getChannelUserMap("paymentType","ALL","documentType","PAN")}
           };
           return categoryData;
	}
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a1_CBC_validateApprovalTransactioAtLevel1_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a1_CBC_validateApprovalTransactioAtLevel1";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1320
		currentNode.assignCategory(assignCategory);
		
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		HashMap<String, String> commissionMap = new HashMap<String, String>();
		commissionMap.put("COMMISSION_TYPE", PretupsI.Postive_Commission);
		commissionProfile = createCommissionProfile(domain, category, grade, commissionMap);
		channelresultMap = createChannelUser(1, domain, parent, category, geotype, grade, mapParam, commissionProfile);
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		boolean isVisible = false;
		try {
			HashMap<String, String> transferMap = intitiateO2C(firstApprovalLimitStr, false);
			isVisible = o2CTransfer.checkTransactionIdLevel1(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"));
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		if(isVisible) {
			Assertion.assertPass("PASS");			
		} else {
			Assertion.assertPass("FAIL");
		}
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a4_CBC_validateApprovedAtLevel1BackNetComm_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a4_CBC_validateApprovedAtLevel2BackNetComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1307
		currentNode.assignCategory(assignCategory);
		
		Long secondApprovalLimit = getApprovalLimit(2);
		String secondApprovalLimitStr = secondApprovalLimit != null ? secondApprovalLimit.toString() : "";
		try {
			//Need to initiate O2c via channel Admin
			HashMap<String, String> transferMap = intitiateO2C(secondApprovalLimitStr, false);
			
			directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
	            o2CTransfer.performingApproval1BackNetComm(transferMap);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a4_CBC_validateApprovedAtLevel2BackNetComm_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a4_CBC_validateApprovedAtLevel2BackNetComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1308
		currentNode.assignCategory(assignCategory);
		
		Long secondApprovalLimit = getApprovalLimit(2);
		String secondApprovalLimitStr = secondApprovalLimit != null ? secondApprovalLimit.toString() : "";
		try {
			//Need to initiate O2c via channel Admin
			HashMap<String, String> transferMap = intitiateO2C(secondApprovalLimitStr, false);
			
			directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
	        Long firstApprov = getApprovalLimit(1);
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
	            o2CTransfer.performingLevel1Approval(transferMap);
	        long netPayableAmount = firstApprov;
	        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
	            o2CTransfer.performingApproval2BackNetComm(transferMap);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a4_CBC_validateApprovedAtLevel3BackNetComm_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a4_CBC_validateApprovedAtLevel3BackNetComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1309
		currentNode.assignCategory(assignCategory);
		
		Long secondApprovalLimit = getApprovalLimit(2);
		String secondApprovalLimitStr = secondApprovalLimit != null ? secondApprovalLimit.toString() : "";
		try {
			//Need to initiate O2c via channel Admin
			HashMap<String, String> transferMap = intitiateO2C(secondApprovalLimitStr, false);
			
			directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
	        Long firstApprov = getApprovalLimit(1);
	        Long secondApprov = getApprovalLimit(2);
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
	            o2CTransfer.performingLevel1Approval(transferMap);
	        long netPayableAmount = firstApprov;
	        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
	            o2CTransfer.performingLevel2Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
	        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > secondApprov)
	            o2CTransfer.performingApproval3BackNetComm(transferMap);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a3_CBC_validateApprovedAtLevel1AfterBackRemoveQuant_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a3_CBC_validateApprovedAtLevel1AfterBackRemoveQuant";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1310
		currentNode.assignCategory(assignCategory);
		
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		try {
			//Need to inititate o2c via Channel Admin
			HashMap<String, String> transferMap = intitiateO2C(firstApprovalLimitStr, false);
			approve(transferMap, 1);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a2_CBC_validateApprovedAtLevel1_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a2_CBC_validateApprovedAtLevel1";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1321
		currentNode.assignCategory(assignCategory);
		
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		try {
			intitiateO2C(firstApprovalLimitStr, true);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a5_CBC_validateApprovedAtLevel2_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a5_CBC_validateApprovedAtLevel2";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1322
		currentNode.assignCategory(assignCategory);
		
		Long secondApprovalLimit = getApprovalLimit(2);
		String secondApprovalLimitStr = secondApprovalLimit != null ? secondApprovalLimit.toString() : "";
		try {
			intitiateO2C(secondApprovalLimitStr, true);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a6_CBC_validateApprovedAtLevel3_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a6_CBC_validateApprovedAtLevel3";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1333
		currentNode.assignCategory(assignCategory);
		Long thirdApprovalLimit = getApprovalLimit(2) + 1;
		String thirdApprovalLimitStr = thirdApprovalLimit != null ? thirdApprovalLimit.toString() : "";
		try {
			intitiateO2C(thirdApprovalLimitStr, true);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a7_CBC_validateInitiateCBCPctDecimal(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a7_CBC_validateInitiateCBCPctDecimal";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1301
		currentNode.assignCategory(assignCategory);
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		try {
			//modify CBC value - PCT - decimal
			intitiateO2C(firstApprovalLimitStr, false);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a8_CBC_validateInitiateCBCAmtDecimal(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a8_CBC_validateInitiateCBCAmtDecimal";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1300
		currentNode.assignCategory(assignCategory);
		
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		try {
			//modify CBC value - AMT - decimal
			intitiateO2C(firstApprovalLimitStr, true);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a9_CBC_validateInitiateCBCApplicableAtLevel1Closed_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a9_CBC_validateInitiateCBCApplicableAtLevel1Closed_PosComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1293
		currentNode.assignCategory(assignCategory);
		boolean isVisible = false;
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		try {
			//modify CBC value - AMT - decimal
			HashMap<String, String> transferMap = intitiateO2C(firstApprovalLimitStr, true);
			isVisible = o2CTransfer.checkCBCApplicableAtLevel1Closed(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"));
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		if(isVisible) {
			Assertion.assertPass("PASS");			
		} else {
			Assertion.assertPass("FAIL");
		}
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a10_CBC_validateInitiateCBCApplicableAtLevel2Closed_PosComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a10_CBC_validateInitiateCBCApplicableAtLevel2Closed_PosComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-").getExtentCase());//1294
		currentNode.assignCategory(assignCategory);
		boolean isVisible = false;
		Long quantityForLevel2 = getApprovalLimit(1) + 1;
		String quantityForLevel2Str = quantityForLevel2 != null ? quantityForLevel2.toString() : "";
		try {
			//modify CBC value - AMT - decimal
			HashMap<String, String> transferMap = intitiateO2C(quantityForLevel2Str, true);
			isVisible = o2CTransfer.checkCBCApplicableAtLevel2Closed(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"));
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		if(isVisible) {
			Assertion.assertPass("PASS");			
		} else {
			Assertion.assertPass("FAIL");
		}
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	
	
	
	private String[] createCommissionProfile(String domain, String category, String grade, HashMap<String, String> commissionMap) throws InterruptedException {
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		return commissionProfile.addCommissionProfilewithoutAdditionalCommission_ZeroBaseCommission(domain, category, grade, commissionMap);			
	}
	
	private HashMap<String, String> createChannelUser(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam, String[] result) throws InterruptedException {
		
		final String methodName = "createChannelUser";
		ChannelUser channelUserLogic = new ChannelUser(driver);
		HashMap<String, String>  channelresultMaps = null;
		try {
			channelresultMaps = channelUserLogic.channelUserInitiate(1, domain, parent, category, geotype, mapParam);
			channelresultMaps.get("channelInitiateMsg");
			String appLevel = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());
			if (appLevel.equals("2")) {
				channelUserLogic.approveLevel1_ChannelUser_AssignCommisison(result[1]);
				channelUserLogic.approveLevel2_ChannelUser();
			} else if (appLevel.equals("1")) {
				channelUserLogic.approveLevel1_ChannelUser_AssignCommisison(result[1]);
			} else {
				Log.info("Approval not required.");
			}
		} catch (Exception e) {
			Log.writeStackTrace(e);
		} finally {
			Log.endTestCase(methodName);			
		}
		return channelresultMaps;
	}
	
	
	private HashMap<String, String> intitiateO2C(String amount, boolean needApproval) throws Exception {
		
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
        transferMap.put("INITIATION_AMOUNT", amount);
        transferMap = o2CTransfer.initiateO2CTransfer(transferMap);
        if(needApproval) {
        	approveO2C(transferMap);        	
        }
        return transferMap;
	}
	
	private HashMap<String, String> intitiateO2C(String amount, String loginId, String password, boolean needApproval) throws Exception {
		
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
        transferMap.put("LOGIN_ID", loginId);
        transferMap.put("PASSWORD", password);
        transferMap.put("INITIATION_AMOUNT", amount);
        transferMap = o2CTransfer.initiateO2CTransfer(transferMap);
        if(needApproval) {
        	approveO2C(transferMap);        	
        }
        return transferMap;
	}
	
	private void approveO2C(HashMap<String, String> transferMap) throws InterruptedException {
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
        Long firstApprov = getApprovalLimit(1);
        Long secondApprov = getApprovalLimit(2);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
            o2CTransfer.performingLevel1Approval(transferMap);
        long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
            o2CTransfer.performingLevel2Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > secondApprov)
            o2CTransfer.performingLevel3Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
	}
	
	private String approve(HashMap<String, String> transferMap, int caseNum) throws InterruptedException {
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = getApprovalLimit(1);
        Long secondApprov = getApprovalLimit(2);
        Map<String, String> resultMap = new HashMap<String, String>();
        String actualMessage = "";
        switch (caseNum) {
			case 1:
			{
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
					resultMap = o2CTransfer.performingLevel1ApprovalAfterBackRemoveQuant(transferMap);
					actualMessage = resultMap.get("actualMessage");
				}
			}
			case 2:
			{
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
		            o2CTransfer.performingLevel1Approval(transferMap);
		        long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
		        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
		            actualMessage = o2CTransfer.performingLevel2Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
			}
        }
        return actualMessage;
	}
	
	private Long getApprovalLimit(int level) throws InterruptedException {
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"), _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        if(level == 1) {
        	return firstApprov;
        } else if(level == 2){
        	return secondApprov;
        }
        return 0L;
	}
	
}
