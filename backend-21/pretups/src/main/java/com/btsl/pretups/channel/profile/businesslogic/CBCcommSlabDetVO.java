package com.btsl.pretups.channel.profile.businesslogic;

import java.util.List;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * CBCcommSlabDetVO
 *

 */
public class CBCcommSlabDetVO  {

	private String product;
	private String cbcTimeSlab;
	private String cbcApplicableFromNTo;
	List<CommisionCBCDetails> listCBCCommsionDetails;

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getCbcTimeSlab() {
		return cbcTimeSlab;
	}

	public void setCbcTimeSlab(String cbcTimeSlab) {
		this.cbcTimeSlab = cbcTimeSlab;
	}

	public String getCbcApplicableFromNTo() {
		return cbcApplicableFromNTo;
	}

	public void setCbcApplicableFromNTo(String cbcApplicableFromNTo) {
		this.cbcApplicableFromNTo = cbcApplicableFromNTo;
	}

	
	public List<CommisionCBCDetails> getListCBCCommsionDetails() {
		return listCBCCommsionDetails;
	}

	public void setListCBCCommsionDetails(List<CommisionCBCDetails> listCBCCommsionDetails) {
		this.listCBCCommsionDetails = listCBCCommsionDetails;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" CommissionSlabDetVO : [ ParentName :")

				.append("]");
		return sb.toString();
	}

}
