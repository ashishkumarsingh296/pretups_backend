package com.testscripts.uap;

import org.testng.annotations.Test;

import com.Features.NetworkPrefix;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.utils.Log;

public class UAP_NetworkPrefix extends BaseTest {

	@Test
	public void a_addPrepaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker  ();
		test = extent.createTest("[UAP]Network Prefix");
		currentNode = test.createNode("To verify that user is able to add a Prepaid prefix");
		currentNode.assignCategory("UAP");
		String series = uniqueChecker.UC_PrefixData();
		String result[] = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test.createNode("To verify that proper message is displayed on adding Prepaid prefix");
		currentNode.assignCategory("UAP");
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.successmessage");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg + "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		currentNode = test.createNode("To verify that added Prepaid prefix appears in Prefix Service Mapping");
		currentNode.assignCategory("UAP");
		String[] prefixes = result[1].split(",");
		int flag = 0;
		for (int i = 0; i < prefixes.length; i++) {
			if (series.equals(prefixes[i])){
				flag = 1;
				break;
			}
			else
				flag = 0;
		}

		if (flag == 1)
			currentNode.log(Status.PASS, "Successfully Added Prefix to Prefix Service Mapping");
		else
			currentNode.log(Status.FAIL, "Couldn't Add Prefix to Prefix Service Mapping");

		Log.endTestCase(this.getClass().getName());
	}

	@Test
	public void b_addPostpaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		currentNode = test.createNode("To verify that user is able to add a Postpaid prefix");
		currentNode.assignCategory("UAP");
		String series = uniqueChecker.UC_PrefixData();
		String result[] = netwrokPrefix.addSeries(series, "Postpaid");
		currentNode = test.createNode("To verify that proper message is displayed on adding Postpaid prefix");
		currentNode.assignCategory("UAP");
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.successmessage");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg + "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		currentNode = test.createNode("To verify that added Postpaid prefix appears in Prefix Service Mapping");
		currentNode.assignCategory("UAP");
		String[] prefixes = result[1].split(",");
		int flag = 0;
		for (int i = 0; i < prefixes.length; i++) {
			if (series.equals(prefixes[i])){
				flag = 1;
				break;
			}
			else
				flag = 0;
		}

		if (flag == 1)
			currentNode.log(Status.PASS, "Successfully Added Prefix to Prefix Service Mapping");
		else
			currentNode.log(Status.FAIL, "Couldn't Add Prefix to Prefix Service Mapping");

		Log.endTestCase(this.getClass().getName());
	}

	@Test
	public void c_addOtherPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		currentNode = test.createNode("To verify that user is able to add a Other prefix");
		currentNode.assignCategory("UAP");
		String series = uniqueChecker.UC_PrefixData();
		String result[] = netwrokPrefix.addSeries(series, "Other");
		currentNode = test.createNode("To verify that proper message is displayed on adding Other prefix");
		currentNode.assignCategory("UAP");
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.successmessage");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg + "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

	@Test
	public void d_duplicatePrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		currentNode = test.createNode("To verify that user is unable to add duplicate prefix");
		currentNode.assignCategory("UAP");
		String series = uniqueChecker.UC_PrefixData();
		netwrokPrefix.addSeries(series, "Prepaid");
		String[] result = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test.createNode("To verify that proper message is displayed on adding duplicate prefix");
		currentNode.assignCategory("UAP");
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.duplicate","Prepaid series",series);
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg + "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
}
