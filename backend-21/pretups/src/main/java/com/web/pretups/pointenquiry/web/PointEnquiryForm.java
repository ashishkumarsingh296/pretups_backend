package com.web.pretups.pointenquiry.web;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.btsl.common.ListValueVO;
import com.web.pretups.pointenquiry.businesslogic.PointEnquiryVO;

@Component
@Scope(value = "session")
public class PointEnquiryForm {

    private String msisdn;
    private String domain;
    private String category;
    private String userName;
    private String accumulatedPoint;
    private String profileType;
    private String status;
    private String geography;
    private String networkCode;
    private String userType;
    private String domainCode;
    private ArrayList<ListValueVO> domainList;
    private ArrayList<ListValueVO> geographyList;
    private String zoneCode;
    private ArrayList<ListValueVO> categoryList;
    private String networkName;
    private String categoryCode;
    private ArrayList<ListValueVO> userList;
    private String userID;
    private ArrayList<PointEnquiryVO> allUsersList;

    public ArrayList<PointEnquiryVO> getAllUsersList() {
        return allUsersList;
    }

    public void setAllUsersList(ArrayList<PointEnquiryVO> allUsersList) {
        this.allUsersList = allUsersList;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<ListValueVO> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<ListValueVO> userList) {
        this.userList = userList;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public ArrayList<ListValueVO> getGeographyList() {
        return geographyList;
    }

    public void setGeographyList(ArrayList<ListValueVO> geographyList) {
        this.geographyList = geographyList;
    }

    public ArrayList<ListValueVO> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<ListValueVO> categoryList) {
        this.categoryList = categoryList;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    private String zoneName;

    // to retrieve values from jsp select
    private String domainType;

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public ArrayList<ListValueVO> getDomainList() {
        return domainList;
    }

    public void setDomainList(ArrayList<ListValueVO> domainList) {
        this.domainList = domainList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccumulatedPoint() {
        return accumulatedPoint;
    }

    public void setAccumulatedPoint(String accumulatedPoint) {
        this.accumulatedPoint = accumulatedPoint;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}
