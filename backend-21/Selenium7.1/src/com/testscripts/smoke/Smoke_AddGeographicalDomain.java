package com.testscripts.smoke;

import org.testng.annotations.Test;

import com.Features.GeogaphicalDomainManagement;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.utils.Log;
import com.utils.RandomGeneration;

public class Smoke_AddGeographicalDomain extends BaseTest {

	String[] domainData;
	String MasterSheetPath;
	RandomGeneration RandomNum = new RandomGeneration();

	/**
	 * <h1>Geographical Domain Management Test Method</h1>
	 */
	@Test
	public void addGeographicalDomain() {

		Log.startTestCase(this.getClass().getName());
		
		test = extent.createTest("[Smoke]Geographical Domain Management");
		GeogaphicalDomainManagement geogaphicalDomainManagement = new GeogaphicalDomainManagement(driver);
		String geographicalDomainTypeName[] = geogaphicalDomainManagement.getGeographyTypes();
		int size = geographicalDomainTypeName.length;
		String domainTypeName;
		String parentGeography = geographicalDomainTypeName[0];
		for (int i = 0; i < size; i++) {	
			
			/*
			 * Test Case Number 1: To verify Operator is able to create Zone / Area / Sub Area
			 */
			domainTypeName = geographicalDomainTypeName[i];
			currentNode = test.createNode("To verify that Operator is able to create " + domainTypeName);
			currentNode.assignCategory("Smoke");
			String[] domainData = geogaphicalDomainManagement.addGeographicalDomain(parentGeography, domainTypeName);
			
			/*
			 * Case Number 2: Message Validation
			 */
			currentNode = test.createNode("To verify that proper message is displayed on successful " + domainTypeName + " creation");
			currentNode.assignCategory("Smoke");
			String addGeographySuccessMsg = MessagesDAO.prepareMessageByKey("grphdomain.addgrphdomain.msg.addsuccess");
			if (addGeographySuccessMsg.equals(domainData[4]))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL,
						"Expected [" + addGeographySuccessMsg + "] but found [" + domainData[4] + "]");
				currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
			}
		}
		
		Log.endTestCase(this.getClass().getName());
	}
}