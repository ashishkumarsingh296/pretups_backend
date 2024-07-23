package com.btsl.pretups.channel.profile.businesslogic;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class CommissionSlabDetails  {
	
	private String fromRange;
	private String toRange;
	private String commission;
	private String tax1;
	private String tax2;
	private String tax3;
	
	//
	private String commissionType;
	private String tax1Type;
	private String tax2Type;
	private String tax3Type;
	
	public String getCommissionType() {
		return commissionType;
	}
	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}
	public String getTax1Type() {
		return tax1Type;
	}
	public void setTax1Type(String tax1Type) {
		this.tax1Type = tax1Type;
	}
	public String getTax2Type() {
		return tax2Type;
	}
	public void setTax2Type(String tax2Type) {
		this.tax2Type = tax2Type;
	}
	public String getTax3Type() {
		return tax3Type;
	}
	public void setTax3Type(String tax3Type) {
		this.tax3Type = tax3Type;
	}
	//
	
	
	
	
	public String getFromRange() {
		return fromRange;
	}
	
	public void setFromRange(String fromRange) {
		this.fromRange = fromRange;
	}
	public String getToRange() {
		return toRange;
	}
	public void setToRange(String toRange) {
		this.toRange = toRange;
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	
	public String getTax2() {
		return tax2;
	}
	public void setTax2(String tax2) {
		this.tax2 = tax2;
	}
	public String getTax3() {
		return tax3;
	}
	public void setTax3(String tax3) {
		this.tax3 = tax3;
	}
	public String getTax1() {
		return tax1;
	}
	public void setTax1(String tax1) {
		this.tax1 = tax1;
	}
	
	
	@Override
	public String toString() {
		return "CommissionSlabDetails [fromRange=" + fromRange + ", toRange=" + toRange + ", commission=" + commission
			+ ",tax1=" + tax1 + ",tax2=" + tax2  + ",tax3=" + tax3    + "]";
	}
	
}
