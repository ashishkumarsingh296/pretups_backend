package com.btsl.pretups.channel.profile.businesslogic;

import java.util.List;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * CBCcommSlabDetVO
 *

 */
public class AdditionalcommSlabVO  {

	private String service;
	private String subService;
	private String applicableFrom;
	private String applicableTo;
	private String timeSlab;
	private String gateWaySelected;
	private String minTransferValue;
	private String maxTransferValue;
	private String  status;
	private List<AdditionalcommSlabDetails> listAdditionalCommSlabDetails;
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getSubService() {
		return subService;
	}
	public void setSubService(String subService) {
		this.subService = subService;
	}
	public String getApplicableFrom() {
		return applicableFrom;
	}
	public void setApplicableFrom(String applicableFrom) {
		this.applicableFrom = applicableFrom;
	}
	public String getApplicableTo() {
		return applicableTo;
	}
	public void setApplicableTo(String applicableTo) {
		this.applicableTo = applicableTo;
	}
	public String getTimeSlab() {
		return timeSlab;
	}
	public void setTimeSlab(String timeSlab) {
		this.timeSlab = timeSlab;
	}
	public String getGateWaySelected() {
		return gateWaySelected;
	}
	public void setGateWaySelected(String gateWaySelected) {
		this.gateWaySelected = gateWaySelected;
	}
	public String getMinTransferValue() {
		return minTransferValue;
	}
	public void setMinTransferValue(String minTransferValue) {
		this.minTransferValue = minTransferValue;
	}
	public String getMaxTransferValue() {
		return maxTransferValue;
	}
	public void setMaxTransferValue(String maxTransferValue) {
		this.maxTransferValue = maxTransferValue;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<AdditionalcommSlabDetails> getListAdditionalCommSlabDetails() {
		return listAdditionalCommSlabDetails;
	}
	public void setListAdditionalCommSlabDetails(List<AdditionalcommSlabDetails> listAdditionalCommSlabDetails) {
		this.listAdditionalCommSlabDetails = listAdditionalCommSlabDetails;
	}
	
	
	@Override
	public String toString() {
		return "AdditionalcommSlabVO [service=" + service + ", subService=" + subService + ", applicableFrom=" + applicableFrom
				+ "applicableTo=" + applicableTo + ", timeSlab=" + timeSlab + "gateWaySelected"+ gateWaySelected + "]";
	}

	
	 
	
}
