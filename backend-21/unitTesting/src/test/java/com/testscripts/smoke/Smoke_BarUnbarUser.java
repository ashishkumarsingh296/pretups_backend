package com.testscripts.smoke;
import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.BarUnbar;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class Smoke_BarUnbarUser extends BaseTest{
	
	static String homepage1;	
	String module = PretupsI.C2S_MODULE;
	String userType = PretupsI.BARRING_SENDER_TYPE;
	String assignCategory = "Smoke";
	static HashMap<String, String> map, map1 = null;
	HashMap<String, String> channelresultMap;
	static boolean TestCaseCounter = false;
	
	@Test
	public void channelUserCreation() throws InterruptedException {
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SBARUNBARCHNLUSER1").getModuleCode());
			TestCaseCounter = true;
		}

		String msisdn = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, 1);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SBARUNBARCHNLUSER1").getExtentCase(), msisdn,module,userType));
		currentNode.assignCategory(assignCategory);
		BarUnbar barunbarUser = new BarUnbar(driver);
		barunbarUser.barringUser(module, userType,msisdn);
		String actual= new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected= MessagesDAO.prepareMessageByKey("subscriber.barreduser.add.mobile.success",msisdn);
		Validator.messageCompare(actual, expected);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SBARUNBARCHNLUSER3").getExtentCase(), msisdn));
		currentNode.assignCategory(assignCategory);
		barunbarUser.viewBarredList(module, userType, msisdn, true);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SBARUNBARCHNLUSER2").getExtentCase(), msisdn,module,userType));
		currentNode.assignCategory(assignCategory);
		barunbarUser.unBarringUser(module, userType, msisdn);
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected= MessagesDAO.prepareMessageByKey("subscriber.unbaruser.add.mobile.success",msisdn);
		Validator.messageCompare(actual, expected);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SBARUNBARCHNLUSER4").getExtentCase(), msisdn));
		currentNode.assignCategory(assignCategory);
		barunbarUser.viewBarredList(module, userType, msisdn, false);
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected= MessagesDAO.prepareMessageByKey("subscriber.viewbaruser.notexists",msisdn);
		Validator.messageCompare(actual, expected);
		}
}
