package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AreaData {

	@JsonProperty("geoName")
    private String geoName = null;
	
	@JsonProperty("geoCode")
    private String geoCode = null;
	
	@JsonProperty("geoDomainSequenceNo")
    private String geoDomainSequenceNo = null;
	

	@JsonProperty("geoDomainName")
    private String geoDomainName = null;
	
	@JsonProperty("isDefault")
    private String isDefault = "N";
	
	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}



	@JsonProperty("geoList")
    private ArrayList<AreaData> geoList = null;
	
	public String getGeoDomainName() {
		return geoDomainName;
	}

	public ArrayList<AreaData> getGeoList() {
		return geoList;
	}

	public void setGeoList(ArrayList<AreaData> geoList) {
		this.geoList = geoList;
	}

	public void setGeoDomainName(String geoDomainName) {
		this.geoDomainName = geoDomainName;
	}

	public String getGeoDomainSequenceNo() {
		return geoDomainSequenceNo;
	}

	public void setGeoDomainSequenceNo(String geoDomainSequenceNo) {
		this.geoDomainSequenceNo = geoDomainSequenceNo;
	}

	public String getGeoName() {
		return geoName;
	}

	public void setGeoName(String geoName) {
		this.geoName = geoName;
	}

	public String getGeoCode() {
		return geoCode;
	}

	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
	}
	
	

	@Override
    public String toString() {
		StringBuilder builder = new StringBuilder();
        builder.append("geoName: "); 
        builder.append(geoName);
        builder.append("geoCode: "); 
        builder.append(geoCode);
        builder.append("geoDomainSequenceNo: "); 
        builder.append(geoDomainSequenceNo);
        builder.append("geoDomainName: "); 
        builder.append(geoDomainName);
        return builder.toString();
    }
}
