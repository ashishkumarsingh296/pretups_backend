package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;
import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;


public class AreaSearchResponseVO extends BaseResponse{
	@JsonProperty("areaList")
    private List<AreaData> areaList = null;

	@JsonProperty("hierarchyLength")
    private Integer hierarchyLength = 0;
	
	private String outletAllow=null;
	
	
    public List<AreaData> getAreaList() {
		return areaList;
	}

	public Integer getHierarchyLength() {
		return hierarchyLength;
	}

	public void setHierarchyLength(Integer hierarchyLength) {
		this.hierarchyLength = hierarchyLength;
	}

	public void setAreaList(List<AreaData> areaList) {
		this.areaList = areaList;
	}


	public Integer hierarchyLength() {
		return hierarchyLength;
	}

	public String getOutletAllow() {
		return outletAllow;
	}

	public void setOutletAllow(String outletAllow) {
		this.outletAllow = outletAllow;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AreaSearchResponseVO [areaList=").append(areaList).append(", hierarchyLength=")
				.append(hierarchyLength).append(", outletAllow=").append(outletAllow).append("]");
		return builder.toString();
	}

	
}

