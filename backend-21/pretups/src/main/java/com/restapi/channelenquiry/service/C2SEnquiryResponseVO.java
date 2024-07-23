package com.restapi.channelenquiry.service;

import com.fasterxml.jackson.annotation.JsonProperty;



import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
public class C2SEnquiryResponseVO extends BaseResponse {
	
	@JsonProperty("c2sEnquiryDetails")
	private ArrayList<C2STransferVO> c2sEnquiryDetails;

	/**
	 * @return the c2sEnquiryDetails
	 */
	public ArrayList<C2STransferVO> getC2sEnquiryDetails() {
		return c2sEnquiryDetails;
	}

	/**
	 * @param c2sEnquiryDetails the c2sEnquiryDetails to set
	 */
	public void setC2sEnquiryDetails(ArrayList<C2STransferVO> c2sEnquiryDetails) {
		this.c2sEnquiryDetails = c2sEnquiryDetails;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2SEnquiryResponseVO [c2sEnquiryDetails=").append(c2sEnquiryDetails).append("]");
		return builder.toString();
	}
	

	
	
}
