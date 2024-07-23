package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserGeographiesVO;


/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @PersonalDetailsVO
 * @LoginDetailsVO
 * @GroupedUserRolesVO
 * @PaymentAndServiceDetailsVO
 * @ProfileDetailsVO

 */
public class FetchUserDetailsResponseVO extends BaseResponse {
	private String service;
	private PersonalDetailsVO personalDetails;
	private LoginDetailsVO loginDetails;
	private GroupedUserRolesVO groupedUserRoles;
	private PaymentAndServiceDetailsVO paymentAndServiceDetails;
	private ProfileDetailsVO profileDetails;
	private BarredUserDetailsVO barredUserDetails;
	private List netOrGeoCodes;
	private List<UserGeographiesVO> geographies;
	private List domainCodes;
	private List domainList;
	private List servicesTypes;
	private List servicesList;
	private List parentservicesList;
	private List productsCodes;
	private List productsList;
	private List voucherTypes;
	private List voucherList;
	private List segments;
	private List segmentList;

	private String categoryAuthenticationType;

	public String getCategoryAuthenticationType(){
		return categoryAuthenticationType;
	}

	public void setCategoryAuthenticationType(String categoryAuthenticationType){
		this.categoryAuthenticationType=categoryAuthenticationType;
	}

	
	
	public BarredUserDetailsVO getBarredUserDetails() {
		return barredUserDetails;
	}
	public void setBarredUserDetails(BarredUserDetailsVO barredUserDetails) {
		this.barredUserDetails = barredUserDetails;
	}
	public PersonalDetailsVO getPersonalDetails() {
		return personalDetails;
	}
	public void setPersonalDetails(PersonalDetailsVO personalDetails) {
		this.personalDetails = personalDetails;
	}
	public LoginDetailsVO getLoginDetails() {
		return loginDetails;
	}
	public void setLoginDetails(LoginDetailsVO loginDetails) {
		this.loginDetails = loginDetails;
	}
	public GroupedUserRolesVO getGroupedUserRoles() {
		return groupedUserRoles;
	}
	public void setGroupedUserRoles(GroupedUserRolesVO groupedUserRoles) {
		this.groupedUserRoles = groupedUserRoles;
	}
	public PaymentAndServiceDetailsVO getPaymentAndServiceDetails() {
		return paymentAndServiceDetails;
	}
	public void setPaymentAndServiceDetails(PaymentAndServiceDetailsVO paymentAndServiceDetails) {
		this.paymentAndServiceDetails = paymentAndServiceDetails;
	}
	public ProfileDetailsVO getProfileDetails() {
		return profileDetails;
	}
	public void setProfileDetails(ProfileDetailsVO profileDetails) {
		this.profileDetails = profileDetails;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	
	
	public List getNetOrGeoCodes() {
		return netOrGeoCodes;
	}
	public void setNetOrGeoCodes(List netOrGeoCodes) {
		this.netOrGeoCodes = netOrGeoCodes;
	}
	public List<UserGeographiesVO> getGeographies() {
		return geographies;
	}
	public void setGeographies(List<UserGeographiesVO> geographies) {
		this.geographies = geographies;
	}
	
	
	public List getDomainCodes() {
		return domainCodes;
	}
	public void setDomainCodes(List domainCodes) {
		this.domainCodes = domainCodes;
	}
	public List getDomainList() {
		return domainList;
	}
	public void setDomainList(List domainList) {
		this.domainList = domainList;
	}
	public List getServicesTypes() {
		return servicesTypes;
	}
	public void setServicesTypes(List servicesTypes) {
		this.servicesTypes = servicesTypes;
	}
	public List getServicesList() {
		return servicesList;
	}
	public void setServicesList(List servicesList) {
		this.servicesList = servicesList;
	}
	public List getProductsCodes() {
		return productsCodes;
	}
	public void setProductsCodes(List productsCodes) {
		this.productsCodes = productsCodes;
	}
	public List getProductsList() {
		return productsList;
	}
	public void setProductsList(List productsList) {
		this.productsList = productsList;
	}
	public List getVoucherTypes() {
		return voucherTypes;
	}
	public void setVoucherTypes(List voucherTypes) {
		this.voucherTypes = voucherTypes;
	}
	public List getVoucherList() {
		return voucherList;
	}
	public void setVoucherList(List voucherList) {
		this.voucherList = voucherList;
	}
	public List getSegments() {
		return segments;
	}
	public void setSegments(List segments) {
		this.segments = segments;
	}
	public List getSegmentList() {
		return segmentList;
	}
	public void setSegmentList(List segmentList) {
		this.segmentList = segmentList;
	}
	@Override
	public String toString() {
		return "FetchUserDetailsResponseVO [service=" + service + ", personalDetails=" + personalDetails
				+ ", loginDetails=" + loginDetails + ", groupedUserRoles=" + groupedUserRoles
				+ ", paymentAndServiceDetails=" + paymentAndServiceDetails + ", profileDetails=" + profileDetails
				+ ", barredUserDetails=" + barredUserDetails + "]";
	}
	public List getParentservicesList() {
		return parentservicesList;
	}
	public void setParentservicesList(List parentservicesList) {
		this.parentservicesList = parentservicesList;
	}
	

	
	
}
