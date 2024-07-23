package com.testscripts.sit;

import java.text.MessageFormat;

import org.testng.annotations.Test;

import com.Features.O2CWithdraw;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.testscripts.smoke.Smoke_O2CWithdraw;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;

public class SIT_O2CWithdraw extends BaseTest{
	static boolean TestCaseCounter = false;
	String assignCategory="SIT";
	
	@Test(dataProvider = "Data", dataProviderClass = Smoke_O2CWithdraw.class)
	public void _001_O2CWithdraw(String parentCategory, String Category, String MSISDN, String ProductType, String ProductCode){
		final String methodname = "o2cWithdrawal";
		Log.startTestCase(methodname, parentCategory, Category, MSISDN, ProductType, ProductCode);
		
		O2CWithdraw o2cWithdraw = new O2CWithdraw(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CWITHDRAWAL1").getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITO2CWITHDRAWAL1").getExtentCase(), Category,MSISDN,ProductType));
		currentNode.assignCategory(assignCategory);
		
		String shortName = ExtentI.getValueofCorrespondingColumns(ExcelI.PRODUCT_SHEET, ExcelI.SHORT_NAME, new String[]{ExcelI.PRODUCT_CODE}, new String[]{ProductCode});
		String usrBalance = DBHandler.AccessHandler.getUserBalance(ProductCode,MSISDN);
		long reqAmount = _parser.getSystemAmount(_parser.getDisplayAmount(Long.parseLong(usrBalance)))+_parser.getSystemAmount(1);
		String amount = _parser.getDisplayAmount(reqAmount);
		
		Log.info("Amount to be entered: "+amount);
		String actual=null;
		try{
			o2cWithdraw.o2cWithdraw(MSISDN, ProductType, amount);}
		catch(Exception e){actual = new AddChannelUserDetailsPage(driver).getActualMessage();}
		String expected = MessagesDAO.prepareMessageByKey("userreturn.withdrawreturn.error.qtymorenbalance", shortName);
		Validator.messageCompare(actual, expected);
	}
	
}
