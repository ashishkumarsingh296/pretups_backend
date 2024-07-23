package com.restapi.o2c.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CApprovalListVO extends BaseResponse{
	@JsonProperty("O2C Approval List")
    private ArrayList<HashMap<String, ArrayList<ChannelTransferVO>>> o2cApprovalList = new ArrayList<HashMap<String, ArrayList<ChannelTransferVO>>>();

	@JsonProperty("Bulk Approval List")
    private HashMap<String, ArrayList<O2CBatchMasterVO>> bulkApprovalList = new HashMap<String, ArrayList<O2CBatchMasterVO>>();

	
	public ArrayList<HashMap<String, ArrayList<ChannelTransferVO>>> getO2cApprovalList() {
		return o2cApprovalList;
	}


	public void setO2cApprovalList(
			ArrayList<HashMap<String, ArrayList<ChannelTransferVO>>> o2cApprovalList) {
		this.o2cApprovalList = o2cApprovalList;
	}

	public HashMap<String, ArrayList<O2CBatchMasterVO>> getBulkApprovalList() {
		return bulkApprovalList;
	}


	public void setBulkApprovalList(HashMap<String, ArrayList<O2CBatchMasterVO>> bulkApprovalList) {
		this.bulkApprovalList = bulkApprovalList;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CApprovalListVO [o2cApprovalList=").append(o2cApprovalList).append(", bulkApprovalList=")
				.append(bulkApprovalList).append("]");
		return builder.toString();
	}
	
}
