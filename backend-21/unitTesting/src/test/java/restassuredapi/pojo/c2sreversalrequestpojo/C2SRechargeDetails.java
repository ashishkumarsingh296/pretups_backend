package restassuredapi.pojo.c2sreversalrequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class C2SRechargeDetails {
	
	
	@JsonProperty("extnwcode")
	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	public String extnwcode;
	
	
    
	@JsonProperty("txnid")
	public String txnid;

	@JsonProperty("txnid")
	public String getTxnid() {
		return txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}


}
