package com.testscripts.sit;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelDomain;
import com.Features.UserStatusConfiguration;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_UserStatusConfiguration extends BaseTest{


	static boolean TestCaseCounter = false;
	String assignCategory="SIT";
	
	@Test
	public void a_AddUserStatusConfiguration() throws InterruptedException{

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITUSERSTATUSCONF1").getModuleCode());
			TestCaseCounter = true;
		}

		ChannelDomain ChannelDomain = new ChannelDomain(driver);
		UserStatusConfiguration UserStatusConfiguration = new UserStatusConfiguration(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITUSERSTATUSCONF1").getExtentCase());
		currentNode.assignCategory(assignCategory);

		ExtentI.Markup(ExtentColor.TEAL, "Adding Domain");

		HashMap<String,String> resultMap = ChannelDomain.add_domain_WEB();

		String domain= resultMap.get("DomainName");
		String catCode = resultMap.get("categoryName");
		String actual0= resultMap.get("ChannelDomainCreationMsg");
		String expected0= MessagesDAO.prepareMessageByKey("domain.add.success","");
		Validator.messageCompare(actual0, expected0);

		ExtentI.Markup(ExtentColor.INDIGO, "Setting User Status Configuration");

		String actual = UserStatusConfiguration.AddUserStatusConfiguration(domain, catCode);
		String expected = MessagesDAO.prepareMessageByKey("userstatus.allowed.label.userAdded");

		Validator.messageCompare(actual, expected);


		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITUSERSTATUSCONF2").getExtentCase());
		currentNode.assignCategory(assignCategory);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying User Status");

		String actual1 = UserStatusConfiguration.ModifyUserStatusConfiguration(domain, catCode);
		String expected1 = MessagesDAO.prepareMessageByKey("userstatus.allowed.label.userModified");

		Validator.messageCompare(actual1, expected1);
		
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITUSERSTATUSCONF3").getExtentCase());
		currentNode.assignCategory(assignCategory);

		ExtentI.Markup(ExtentColor.TEAL, "View User Status");

		String actual2 = UserStatusConfiguration.ViewUserStatusConfiguration(domain, catCode);
		String expected2 = MessagesDAO.prepareMessageByKey("profile.selectUserStatusAllowed.heading");

		Validator.messageCompare(actual2, expected2);
		
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITUSERSTATUSCONF4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Deleting Category");

		String actual3 = ChannelDomain.deleteCategory(domain, catCode);
		String expected3 = MessagesDAO.prepareMessageByKey("channeldomains.deletechannelcategory.deletesuccess");

		Validator.messageCompare(actual3, expected3);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITUSERSTATUSCONF5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Deleting Domain");

		String actual4 = ChannelDomain.deleteDomain(domain);
		String expected4 = MessagesDAO.prepareMessageByKey("domains.deletedomain.deletesuccess");

		Validator.messageCompare(actual4, expected4);

	}
}
