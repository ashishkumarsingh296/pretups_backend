package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.CacheUpdate;
import com.Features.NetworkPrefix;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.commons.SystemPreferences;
import com.utils.Log;
import com.utils.RandomGeneration;

public class SIT_NetworkPrefix extends BaseTest {
	
	public static boolean testCaseCounter = false;
	CacheUpdate cacheUpdate = new CacheUpdate(driver);

	@Test
	public void a_addPrepaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is able to add a Prepaid prefix");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		String result[] = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test
				.createNode("To verify that proper message is displayed on adding Prepaid prefix");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO
				.prepareMessageByKey("network.networkprefix.successmessage");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		currentNode = test
				.createNode("To verify that added Prepaid prefix appears in Prefix Service Mapping");
		currentNode.assignCategory("SIT");
	
		String[] prefixes = result[1].split(",");
		int flag = 0;
		for (int i = 0; i < prefixes.length; i++) {
			if (series.equals(prefixes[i])) {
				flag = 1;
				break;
			} else
				flag = 0;
		}

		if (flag == 1)
			currentNode.log(Status.PASS,
					"Successfully Added Prefix to Prefix Service Mapping");
		else
			currentNode.log(Status.FAIL,
					"Couldn't Add Prefix to Prefix Service Mapping");

		Log.endTestCase(this.getClass().getName());
	}

	@Test
	public void b_addPostpaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is able to add a Postpaid prefix");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		String result[] = netwrokPrefix.addSeries(series, "Postpaid");
		currentNode = test
				.createNode("To verify that proper message is displayed on adding Postpaid prefix");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO
				.prepareMessageByKey("network.networkprefix.successmessage");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		currentNode = test
				.createNode("To verify that added Postpaid prefix appears in Prefix Service Mapping");
		currentNode.assignCategory("SIT");
		
		String[] prefixes = result[1].split(",");
		int flag = 0;
		for (int i = 0; i < prefixes.length; i++) {
			if (series.equals(prefixes[i])) {
				flag = 1;
				break;
			} else
				flag = 0;
		}

		if (flag == 1)
			currentNode.log(Status.PASS,
					"Successfully Added Prefix to Prefix Service Mapping");
		else
			currentNode.log(Status.FAIL,
					"Couldn't Add Prefix to Prefix Service Mapping");

		Log.endTestCase(this.getClass().getName());
	}

	@Test
	public void c_addOtherPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is able to add a Other prefix");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		String result[] = netwrokPrefix.addSeries(series, "Other");
		currentNode = test
				.createNode("To verify that proper message is displayed on adding Other prefix");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO
				.prepareMessageByKey("network.networkprefix.successmessage");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

	@Test
	public void d_duplicatePrepaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add duplicate Prepaid prefix");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		netwrokPrefix.addSeries(series, "Prepaid");
		String[] result = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test
				.createNode("To verify that proper message is displayed on adding duplicate prefix");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.duplicate", "Prepaid series",
				series);
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

	@Test
	public void e_duplicatePostpaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add duplicate postpaid prefix");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		netwrokPrefix.addSeries(series, "Postpaid");
		String[] result = netwrokPrefix.addSeries(series, "Postpaid");
		currentNode = test
				.createNode("To verify that proper message is displayed on adding duplicate prefix");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.duplicate", "Postpaid series",
				series);
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test
	public void f_duplicateOtherPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add duplicate Other prefix");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		netwrokPrefix.addSeries(series, "Other");
		String[] result = netwrokPrefix.addSeries(series, "Other");
		currentNode = test
				.createNode("To verify that proper message is displayed on adding duplicate prefix");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.duplicate", "Other series",
				series);
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
	
	@Test
	public void g_invalidLengthPostpaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add postpaid prefix of greater than preference length");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		String additionalData = randomGeneration.randomNumberWithoutZero(1);
		String seriesData = series.concat(additionalData);
		String[] result = netwrokPrefix.addSeries(seriesData, "Postpaid");
		currentNode = test
				.createNode("To verify that proper message is displayed if prefix length is larger than preference value");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.invalid", "Postpaid series",
				seriesData, String.valueOf(SystemPreferences.MSISDN_PREFIX_LENGTH));
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test
	public void h_invalidLengthPrepaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add Prepaid prefix of greater than preference length");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		String additionalData = randomGeneration.randomNumberWithoutZero(1);
		String seriesData = series.concat(additionalData);
		String[] result = netwrokPrefix.addSeries(seriesData, "Prepaid");
		currentNode = test
				.createNode("To verify that proper message is displayed if prefix length is larger than preference value");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.invalid", "Prepaid series",
				seriesData, String.valueOf(SystemPreferences.MSISDN_PREFIX_LENGTH));
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

	
	@Test
	public void i_invalidLengthOtherPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add Other prefix of greater than preference length");
		currentNode.assignCategory("SIT");
		String series = uniqueChecker.UC_PrefixData();
		String additionalData = randomGeneration.randomNumberWithoutZero(1);
		String seriesData = series.concat(additionalData);
		String[] result = netwrokPrefix.addSeries(seriesData, "Other");
		currentNode = test
				.createNode("To verify that proper message is displayed if prefix length is larger than preference value");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.invalid", "Other series",
				seriesData, String.valueOf(SystemPreferences.MSISDN_PREFIX_LENGTH));
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test
	public void j_invalidPrepaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add blank Prepaid prefix");
		currentNode.assignCategory("SIT");
		String series = " ,";
		String[] result = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test
				.createNode("To verify that proper message is displayed if prefix is blank");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.numeric", "Prepaid","series");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
	
	@Test
	public void k_invalidPostpaidPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add blank Postpaid prefix");
		currentNode.assignCategory("SIT");
		String series = " ,";
		String[] result = netwrokPrefix.addSeries(series, "Postpaid");
		currentNode = test
				.createNode("To verify that proper message is displayed if prefix is blank");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.numeric", "Postpaid","series");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
	
	@Test
	public void l_invalidOtherPrefix() {

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT] Network Prefix");
			testCaseCounter = true;
		}
		currentNode = test
				.createNode("To verify that user is unable to add blank Other prefix");
		currentNode.assignCategory("SIT");
		String series = " ,";
		String[] result = netwrokPrefix.addSeries(series, "Other");
		currentNode = test
				.createNode("To verify that proper message is displayed if prefix is blank");
		currentNode.assignCategory("SIT");
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.numeric", "Other","series");
		if (expectedMsg.equals(result[0]))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expectedMsg
					+ "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
}
