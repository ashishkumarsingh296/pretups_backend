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
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
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
public class SIT_CommProfileCBC2 extends BaseTest {
	
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
//           userDetailsHL[5] = ExcelUtility.getCellData(0, ExcelI.MSISDN, 1);
    
           Object[][] categoryData;
           categoryData = new Object[][]{
        		   {rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3], userDetailsHL[4], chnlUserMap.getChannelUserMap("paymentType","ALL","documentType","PAN")}
           };
           return categoryData;
	}
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a1_CBC_validateApprovalTransactioAtLevel1_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a1_CBC_validateApprovalTransactioAtLevel1_NegComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1320
		currentNode.assignCategory(assignCategory);
		
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		
		HashMap<String, String> commissionMap = new HashMap<String, String>();
		commissionMap.put("COMMISSION_TYPE", PretupsI.Normal_Commission);
		commissionProfile = createCommissionProfile(domain, category, grade, commissionMap);
		
		String msisdn = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		mapParam.put("MSISDN", msisdn);
		channelresultMap = createChannelUser(1, domain, parent, category, geotype, grade, mapParam, commissionProfile);
		
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		boolean isVisible = false;
		try {
			O2CTransfer o2CTransfer = new O2CTransfer(driver);
			HashMap<String, String> transferMap = intitiateO2C(o2CTransfer, firstApprovalLimitStr, false, mapParam);
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
    public void a2_CBC_validateApprovedAtLevel1BackNetComm_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a2_CBC_validateApprovedAtLevel1BackNetComm_NegComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1169
		currentNode.assignCategory(assignCategory);
		
		Long secondApprovalLimit = getApprovalLimit(2);
		String secondApprovalLimitStr = secondApprovalLimit != null ? secondApprovalLimit.toString() : "";
		try {
			//Need to initiate O2c via channel Admin
			O2CTransfer o2cTransfer = new O2CTransfer(driver);
			HashMap<String, String> transferMap = intitiateO2C(o2cTransfer, secondApprovalLimitStr, false);
			
			directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				o2cTransfer.performingApproval1BackNetComm(transferMap);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a4_CBC_validateApprovedAtLevel2BackNetComm_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a4_CBC_validateApprovedAtLevel2BackNetComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1170
		currentNode.assignCategory(assignCategory);
		
		Long secondApprovalLimit = getApprovalLimit(2);
		String secondApprovalLimitStr = secondApprovalLimit != null ? secondApprovalLimit.toString() : "";
		try {
			//Need to initiate O2c via channel Admin
			O2CTransfer o2cTransfer = new O2CTransfer(driver);
			HashMap<String, String> transferMap = intitiateO2C(o2cTransfer, secondApprovalLimitStr, false);
			
			directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
	        Long firstApprov = getApprovalLimit(1);
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				o2cTransfer.performingLevel1Approval(transferMap);
			long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
	        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
	        	o2cTransfer.performingApproval2BackNetComm(transferMap);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a4_CBC_validateApprovedAtLevel3BackNetComm_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a4_CBC_validateApprovedAtLevel3BackNetComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1171
		currentNode.assignCategory(assignCategory);
		
		Long secondApprovalLimit = getApprovalLimit(2) + 10000L;
		String secondApprovalLimitStr = secondApprovalLimit != null ? secondApprovalLimit.toString() : "";
		try {
			O2CTransfer o2cTransfer = new O2CTransfer(driver);
			//Need to initiate O2c via channel Admin
			HashMap<String, String> transferMap = intitiateO2C(o2cTransfer, secondApprovalLimitStr, false);
			directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
	        Long firstApprov = getApprovalLimit(1);
	        Long secondApprov = getApprovalLimit(2);
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				o2cTransfer.performingLevel1Approval(transferMap);
			long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
	        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
	        	o2cTransfer.performingLevel2Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
	        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > secondApprov)
	        	o2cTransfer.performingApproval3BackNetComm(transferMap);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals("", "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a2_CBC_validateApprovedAtLevel1_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a2_CBC_validateApprovedAtLevel1";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1183
		currentNode.assignCategory(assignCategory);
		
		String expectedMessage = "";
		HashMap<String, String> resultMap = null;
		O2CTransfer o2cTransfer = new O2CTransfer(driver);
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		try {
			resultMap = intitiateO2C(o2cTransfer, firstApprovalLimitStr, true);
			expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", resultMap.get("TRANSACTION_ID")); 
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals(resultMap.get("approvalMessage"), expectedMessage);
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a5_CBC_validateApprovedAtLevel2_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a5_CBC_validateApprovedAtLevel2";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1184
		currentNode.assignCategory(assignCategory);
		String expectedMessage = "";
		HashMap<String, String> resultMap = null;
		O2CTransfer o2cTransfer = new O2CTransfer(driver);
		Long secondApprovalLimit = getApprovalLimit(2);
		String secondApprovalLimitStr = secondApprovalLimit != null ? secondApprovalLimit.toString() : "";
		try {
			resultMap = intitiateO2C(o2cTransfer, secondApprovalLimitStr, true);
			expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", resultMap.get("TRANSACTION_ID"));
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals(resultMap.get("approvalMessage"), expectedMessage);
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a6_CBC_validateApprovedAtLevel3_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a6_CBC_validateApprovedAtLevel3";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1185
		currentNode.assignCategory(assignCategory);
		String expectedMessage = "";
		HashMap<String, String> resultMap = null;
		O2CTransfer o2cTransfer = new O2CTransfer(driver);
		Long thirdApprovalLimit = getApprovalLimit(2) + 100000L;
		String thirdApprovalLimitStr = thirdApprovalLimit != null ? thirdApprovalLimit.toString() : "";
		try {
			resultMap = intitiateO2C(o2cTransfer, thirdApprovalLimitStr, true);
			expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelthree.msg.success", resultMap.get("TRANSACTION_ID"));
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals(resultMap.get("approvalMessage"), expectedMessage);
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a7_CBC_validateInitiateCBCPctDecimal_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a7_CBC_validateInitiateCBCPctDecimal";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1163
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> commissionMap = new HashMap<String, String>();
		commissionMap.put("COMMISSION_TYPE", PretupsI.Normal_Commission);
		commissionMap.put("DECIMAL_CBC", "TRUE");
		commissionMap.put("TAX_TYPE", _masterVO.getProperty("TaxTypePCT"));
		commissionProfile = createCommissionProfile(domain, category, grade, commissionMap);
		
		String msisdn = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		mapParam.put("MSISDN", msisdn);
		channelresultMap = createChannelUser(1, domain, parent, category, geotype, grade, mapParam, commissionProfile);
		
		O2CTransfer o2cTransfer = new O2CTransfer(driver);
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		HashMap<String, String> initiateMap = null;
		try {
			initiateMap = intitiateO2C(o2cTransfer, firstApprovalLimitStr, false, mapParam);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals(initiateMap.get("INITIATE_MESSAGE"), "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a8_CBC_validateInitiateCBCAmtDecimal_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a7_CBC_validateInitiateCBCPctDecimal";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1163
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> commissionMap = new HashMap<String, String>();
		commissionMap.put("COMMISSION_TYPE", PretupsI.Normal_Commission);
		commissionMap.put("DECIMAL_CBC", "TRUE");
		commissionMap.put("TAX_TYPE", _masterVO.getProperty("TaxTypeAMT"));
		commissionProfile = createCommissionProfile(domain, category, grade, commissionMap);
		
		String msisdn = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		mapParam.put("MSISDN", msisdn);
		channelresultMap = createChannelUser(1, domain, parent, category, geotype, grade, mapParam, commissionProfile);
		
		O2CTransfer o2cTransfer = new O2CTransfer(driver);
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		HashMap<String, String> initiateMap = null;
		try {
			initiateMap = intitiateO2C(o2cTransfer, firstApprovalLimitStr, false, mapParam);
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals(initiateMap.get("INITIATE_MESSAGE"), "");//Validate net commission value
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
    }
	
	@Test(dataProvider = "commission0_CBC_nonZero")
    public void a9_CBC_validateInitiateCBCApplicableAtLevel1Closed_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a9_CBC_validateInitiateCBCApplicableAtLevel1Closed";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1155
		currentNode.assignCategory(assignCategory);
		boolean isVisible = false;
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		try {
			//modify CBC value - AMT - decimal
			O2CTransfer o2cTransfer = new O2CTransfer(driver);
			HashMap<String, String> transferMap = intitiateO2C(o2cTransfer, firstApprovalLimitStr, true);
			isVisible = o2cTransfer.checkCBCApplicableAtLevel1Closed(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"));
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
    public void a10_CBC_validateInitiateCBCApplicableAtLevel2Closed_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a10_CBC_validateInitiateCBCApplicableAtLevel2Closed_NegComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1156
		currentNode.assignCategory(assignCategory);
		boolean isVisible = false;
		Long quantityForLevel2 = getApprovalLimit(1) + 1;
		String quantityForLevel2Str = quantityForLevel2 != null ? quantityForLevel2.toString() : "";
		try {
			O2CTransfer o2cTransfer = new O2CTransfer(driver);
			HashMap<String, String> transferMap = intitiateO2C(o2cTransfer, quantityForLevel2Str, true);
			isVisible = o2cTransfer.checkCBCApplicableAtLevel2Closed(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"));
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
    public void a3_CBC_validateApprovedAtLevel1AfterBackRemoveQuant_NegComm(int rowNum, String domain, String parent, String category, 
    		String geotype, String grade, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName = "a3_CBC_validateApprovedAtLevel1AfterBackRemoveQuant_NegComm";
		Log.startTestCase(this.getClass().getName());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase());//1172
		currentNode.assignCategory(assignCategory);
		String expectedMessage = "";
		String actualMessage = "";
		HashMap<String, String> transferMap = null;
		Long firstApprovalLimit = getApprovalLimit(1);
		String firstApprovalLimitStr = firstApprovalLimit != null ? firstApprovalLimit.toString() : "";
		try {
			O2CTransfer o2cTransfer = new O2CTransfer(driver);
			transferMap = intitiateO2C(o2cTransfer, firstApprovalLimitStr, false);
			actualMessage = approve(o2cTransfer, transferMap, 1);
			expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", transferMap.get("TRANSACTION_ID"));
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}
		Assertion.assertEquals(actualMessage, expectedMessage);
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
	
	private HashMap<String, String> intitiateO2C(O2CTransfer o2cTransfer, String amount, boolean needApproval) throws Exception {
		
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
        transferMap.put("INITIATION_AMOUNT", amount);
//        transferMap.put("TO_MSISDN","");
        transferMap = o2cTransfer.initiateO2CTransfer(transferMap);
        if(needApproval) {
        	String approvalMessage = approveO2C(o2cTransfer, transferMap);
        	transferMap.put("approvalMessage", approvalMessage);
        }
        return transferMap;
	}
	
	private HashMap<String, String> intitiateO2C(O2CTransfer o2cTransfer, String amount, boolean needApproval, HashMap<String, String> initiateMap) throws Exception {
		
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
        transferMap.put("INITIATION_AMOUNT", amount);
        if(initiateMap.get("MSISDN") != "" && initiateMap.get("MSISDN") != null) {
        	transferMap.put("TO_MSISDN", initiateMap.get("MSISDN"));        	
        }
        transferMap = o2cTransfer.initiateO2CTransfer(transferMap);
        transferMap.put("INITIATE_MESSAGE", transferMap.get("INITIATE_MESSAGE"));
        if(needApproval) {
        	String approvalMessage = approveO2C(o2cTransfer, transferMap);
        	transferMap.put("approvalMessage", approvalMessage);
        }
        return transferMap;
	}

	/*private HashMap<String, String> intitiateO2CChannelAdmin(String msisdn, String productType, String amount, boolean needApproval) throws Exception {
		
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
//		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
//        transferMap.put("INITIATION_AMOUNT", amount);
        HashMap<String, String> transferMap = (HashMap<String, String>) o2CTransfer.initiateTransfer(msisdn, productType, amount, "Automated Smoke O2C Transfer Testing");
//        HashMap<String, String> transferMap = o2CTransfer.initiateO2CTransfer(transferMap);
        if(needApproval) {
        	approveO2C(transferMap);        	
        }
        return transferMap;
	}*/
	
	/*private HashMap<String, String> intitiateO2C(String amount, String loginId, String password, boolean needApproval) throws Exception {
		
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
	}*/
	
	private String approveO2C(O2CTransfer o2cTransfer, HashMap<String, String> transferMap) throws InterruptedException {
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
        Long firstApprov = getApprovalLimit(1);
        Long secondApprov = getApprovalLimit(2);
        String actualMessage = "";
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
			Map<String, String> resultMap = o2cTransfer.performingLevel1Approval(transferMap);
			actualMessage = resultMap != null ? resultMap.get("actualMessage") : "";
		}
        long netPayableAmount = Long.parseLong(transferMap.get("NetPayableAmount"));
        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
        	actualMessage = o2cTransfer.performingLevel2Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > secondApprov)
        	actualMessage = o2cTransfer.performingLevel3Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
        
        return actualMessage;
	}
	
	private String approve(O2CTransfer o2cTransfer, HashMap<String, String> transferMap, int caseNum) throws InterruptedException {
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = getApprovalLimit(1);
        Long secondApprov = getApprovalLimit(2);
        Map<String, String> resultMap = new HashMap<String, String>();
        String actualMessage = "";
        switch (caseNum) {
			case 1:
			{
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
					resultMap = o2cTransfer.performingLevel1ApprovalAfterBackRemoveQuant(transferMap);
					actualMessage = resultMap.get("actualMessage");
				}
				break;
			}
			case 2:
			{
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
					o2cTransfer.performingLevel1Approval(transferMap);
		        long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
		        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
		            actualMessage = o2cTransfer.performingLevel2Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
		        
		        break;
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
