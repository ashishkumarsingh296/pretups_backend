package com.btsl.pretups.channel.transfer.businesslogic;
public class C2STotalTrnsVO {
   private String msisdn;
   private String loginId;
   private String fromDate;
   private String toDate;
   private Long tnxCount;

public Long getTnxCount() {
	return tnxCount;
}
public void setTnxCount(Long tnxCount) {
	this.tnxCount = tnxCount;
}
public String getMsisdn() {
	return msisdn;
}
public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
}
public String getLoginId() {
	return loginId;
}
public void setLoginId(String loginId) {
	this.loginId = loginId;
}
public String getFromDate() {
	return fromDate;
}
public void setFromDate(String fromDate) {
	this.fromDate = fromDate;
}
public String getToDate() {
	return toDate;
}
public void setToDate(String toDate) {
	this.toDate = toDate;
}
@Override
public String toString() {
	return "TotalTrnsVO [msisdn=" + msisdn + ", loginId=" + loginId + ", fromDate=" + fromDate + ", toDate=" + toDate
			+ ", tnxCount=" + tnxCount + "]";
}


}
