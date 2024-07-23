package com.restapi.networkadmin.operatorUser.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AddOperatorUserRequestVO {

    @JsonProperty("prevLoginId")
    private String prevLoginId;

    @JsonProperty("contactPerson")
    private String contactPerson;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("categoryCode")
    private String categoryCode;

    @JsonProperty("userCode")
    private String userCode;

    @JsonProperty("status")
    private String status;

    @JsonProperty("company")
    private String company;

    @JsonProperty("fax")
    private String fax;

    @JsonProperty("allowedUserTypeCreation")
    private String allowedUserTypeCreation;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("msisdn")
    private String msisdn;

    @JsonProperty("rsaAuthentication")
    private String rsaAuthentication;

    @JsonProperty("authTypeAllowed")
    private String authTypeAllowed;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("shortName")
    private String shortName;

    @JsonProperty("userNamePrefix")
    private String userNamePrefix;

    @JsonProperty("externalCode")
    private String externalCode;

    @JsonProperty("subscriberCode")
    private String subscriberCode;

    @JsonProperty("ssn")
    private String ssn;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("designation")
    private String designation;

    @JsonProperty("divisionCode")
    private String divisionCode;

    @JsonProperty("departmentCode")
    private String departmentCode;

    @JsonProperty("address1")
    private String address1;

    @JsonProperty("address2")
    private String address2;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("emailid")
    private String emailid;

    @JsonProperty("appointmentdate")
    private String appointmentdate;

    @JsonProperty("webloginid")
    private String webloginid;

    @JsonProperty("webpassword")
    private String webpassword;

    @JsonProperty("confirmwebpassword")
    private String confirmwebpassword;

    @JsonProperty("allowedip")
    private String allowedip;

    @JsonProperty("alloweddays")
    private ArrayList alloweddays;

    @JsonProperty("allowedTimeFrom")
    private String allowedTimeFrom;

    @JsonProperty("allowedTimeTo")
    private String allowedTimeTo;

    @JsonProperty("geographyCode")
    private String geographyCode;


    @JsonProperty("roleType")
    private String roleType;

    private ArrayList geographicalCodeList;

    private ArrayList roleList;

    private ArrayList domainCodeList;

    private ArrayList serviceTypeList;

    private ArrayList<OperatorUserPhoneVO> msisdnList;

    private ArrayList productList;

    private ArrayList voucherTypeList;

    private ArrayList voucherSegmentList;

}
