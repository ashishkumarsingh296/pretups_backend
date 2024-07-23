package com.btsl.pretups.processes.clientprocesses.businesslogic;

import java.io.Serializable;




public class GreetingMsgVO implements Serializable{


	private String _MSISDN = null;
	
	private String _greetingMsg = null;
	private String _status = null;
	
	
	
	public String toString() {

		StringBuffer sbf = new StringBuffer();
		sbf.append("MSISDN ="+_MSISDN);
		
		sbf.append(",greetingMsg ="+_greetingMsg);
		sbf.append(",status"+_status);
		
		return sbf.toString();
	
	}
	public String getStatus() {
		return _status;
	}
	public void setStatus(String status) {
		this._status = status;
	}
	public String getMSISDN() {
		return _MSISDN;
	}
	public void setMSISDN(String msisdn) {
		this._MSISDN = msisdn;
	}
	public String getGreetingMsg() {
		return _greetingMsg;
	}
	public void setGreetingMsg(String greetingMsg) {
		this._greetingMsg = greetingMsg;
	}
	
	
}
