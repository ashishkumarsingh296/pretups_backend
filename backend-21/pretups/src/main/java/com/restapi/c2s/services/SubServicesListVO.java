package com.restapi.c2s.services;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubServicesListVO extends BaseResponse{
    @JsonProperty("Sub Services List")
    private HashMap<String, List<SubServiceVO> > subServicesList = null;

    public HashMap<String, List<SubServiceVO> > getSubServicesList() {
        return subServicesList;
    }

    public void setSubServicesList(HashMap<String, List<SubServiceVO> > subServicesList) {
        this.subServicesList = subServicesList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("subServicesList", subServicesList).toString();
    }
}

class SubServiceVO {
    @JsonProperty("subServiceName")
    private String subServiceName;
    @JsonProperty("subServiceCode")
    private String subServiceCode;

    public String getSubServiceName() {
        return subServiceName;
    }

    public void setSubServiceName(String subServiceName) {
        this.subServiceName = subServiceName;
    }

    public String getSubServiceCode() {
        return subServiceCode;
    }

    public void setSubServiceCode(String subServiceCode) {
        this.subServiceCode = subServiceCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("subServiceName", subServiceName).append("subServiceCode", subServiceCode).toString();
    }
}