package com.testscripts.prerequisites;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.Features.GeogaphicalDomainManagement;
import com.utils.Log;

/**
 * @author Ayush Abhijeet This class creates Geographical Domains. Dependency:
 *         Database Connectivity for Geographical Domains Types.
 **/
public class PreRequisite_GeographicalDomainManagement extends BaseTest {

	/**
	 * <h1>Geographical Domain Management Test Method</h1>
	 */
	@Test
	public void geographicalDomainManagement() {
		
		// Pushing Test Case start to Logger
		Log.startTestCase(this.getClass().getName());
		
		// Test Case creation on Extent Report
		test = extent.createTest("[Pre-Requisite]Geographical Domain Management");
		
		// Initializing the Division Department Feature Class with current Driver
		GeogaphicalDomainManagement geogaphicalDomainManagement = new GeogaphicalDomainManagement(driver);
		
		//Creating WorkSheet in DataProvider
		geogaphicalDomainManagement.createGeographicalSheetHeader();
		String geographicalDomainTypeName[] = geogaphicalDomainManagement.getGeographyTypes();
		int size = geographicalDomainTypeName.length;
		String domainTypeName;
		String parentGeography = geographicalDomainTypeName[0];
		for (int i = 0; i < size; i++) {
			
			/*
			 * Test Case execution
			 */
			currentNode = test.createNode("To verify that Operator User is able to create " + geographicalDomainTypeName[i]);
			currentNode.assignCategory("Pre-Requisite");
			domainTypeName = geographicalDomainTypeName[i];
			String[] domainData = geogaphicalDomainManagement.addGeographicalDomain(parentGeography, domainTypeName);
			geogaphicalDomainManagement.writeGeographicalData(domainData);
			
		}
		
		//Pushing Test Case end to Logger
		Log.endTestCase(this.getClass().getName());
	}
}