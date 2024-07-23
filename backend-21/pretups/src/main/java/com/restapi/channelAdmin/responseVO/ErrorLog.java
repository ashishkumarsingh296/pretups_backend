package com.restapi.channelAdmin.responseVO;

public class ErrorLog{
	private String  lineNo;
	private String  labelName;
	private String  reason;
	
	
	
	public ErrorLog(String  lineNo, String labelName, String reason) {
		super();
		this.lineNo = lineNo;
		this.labelName = labelName;
		this.reason = reason;
	}
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String  lineNo) {
		this.lineNo = lineNo;
	}
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	@Override
	public String toString() {
		StringBuilder br=new StringBuilder();
		br.append("ErrorLog [lineNo=" + lineNo +
				", labelName=" + labelName + ", reason=" + reason + "]"); 
		return br.toString();
	}
}