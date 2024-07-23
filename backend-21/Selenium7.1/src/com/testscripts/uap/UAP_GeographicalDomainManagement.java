package com.testscripts.uap;

import org.testng.annotations.Test;

import com.Features.GeogaphicalDomainManagement;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.utils.Log;
import com.utils.RandomGeneration;

public class UAP_GeographicalDomainManagement extends BaseTest {

	String[] domainData;
	String MasterSheetPath;
	RandomGeneration RandomNum = new RandomGeneration();

	/**
	 * <h1>Geographical Domain Management Test Method</h1>
	 */
	@Test
	public void addGeographicalDomain() {

		Log.startTestCase("Geographical Domain Management");
		test = extent.createTest("[UAP]Geographical Domain Management");
		GeogaphicalDomainManagement geogaphicalDomainManagement = new GeogaphicalDomainManagement(driver);
		String geographicalDomainTypeName[] = geogaphicalDomainManagement.getGeographyTypes();
		int size = geographicalDomainTypeName.length;
		String domainTypeName;
		String parentGeography = geographicalDomainTypeName[0];
		for (int i = 0; i < size; i++) {
			currentNode = test.createNode("To verify that Operator is able to create Geographical Domains");
			currentNode.assignCategory("UAP");	
			domainTypeName = geographicalDomainTypeName[i];
			String[] domainData = geogaphicalDomainManagement.addGeographicalDomain(parentGeography, domainTypeName);
			String addGeographySuccessMsg = MessagesDAO.prepareMessageByKey("grphdomain.addgrphdomain.msg.addsuccess");
			if (addGeographySuccessMsg.equals(domainData[4]))
				test.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL,
						"Expected [" + addGeographySuccessMsg + "] but found [" + domainData[4] + "]");
				test.log(Status.FAIL, "Message Validation Unsuccessful");
			}
		}
	}
}