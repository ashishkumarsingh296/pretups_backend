package com.Features.mapclasses;

import java.util.HashMap;
import java.util.Map;

import com.Features.GeogaphicalDomainManagement;
import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.commons.PretupsI;

public class GeograpichalDomainManagementMap extends BaseTest{
	
	String[] domainData;

	public Map<String, String> defaultMap() {
		
		UniqueChecker uniqueChecker = new UniqueChecker();
		GeogaphicalDomainManagement geogaphicalDomainManagement = new GeogaphicalDomainManagement(driver);
		String geographicalDomainTypeName[] = geogaphicalDomainManagement.getGeographyTypes();
		int size = geographicalDomainTypeName.length;
		String domainTypeName = geographicalDomainTypeName[2];
		String parentGeography = geographicalDomainTypeName[0];
		
		Map<String, String> paraMeterMap = new HashMap<>();
		domainData = uniqueChecker.UC_DomainData();
		paraMeterMap.put("domainType", domainTypeName);
		paraMeterMap.put("parentGeography", parentGeography);
		paraMeterMap.put("domainCode", domainData[0].trim());
		paraMeterMap.put("domainName", domainData[1].trim());
		paraMeterMap.put("domainShortName", domainData[2].trim());
		paraMeterMap.put("status",PretupsI.STATUS_SUSPENDED_LOOKUPS);
		return paraMeterMap;
	   }
	
	public Map<String, String> getGeographicalDomainManagementMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
}

}
