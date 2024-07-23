package com.btsl.pretups.channel.transfer.requesthandler;

import com.fasterxml.jackson.annotation.JsonProperty;



public class DataTrfApp {
	
	@JsonProperty("transactionId")
	private String transactionId;
    
	
	@JsonProperty("extnwcode")
	private String extnwcode;
    
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
	@JsonProperty("extcode")
    private String extcode;
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("loginid")
    private String loginid;
    
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("password")
    private String password;
    
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("pin")
    private String pin;
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("msisdn")
    private String msisdn;
    
    
    @JsonProperty("extrefnum")
    private String extrefnum;
    @JsonProperty("msisdn2")
    private String msisdn2;
    @JsonProperty("geographicalDomain")
    private String geographicalDomain;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("category")
    private String category;
    @JsonProperty("approvalLevel")
    private String approvalLevel;
    @JsonProperty("transferType")
    private String transferType;
    @JsonProperty("usernameToSearch")
    private String usernameToSearch;
    @JsonProperty("pageNumber")
    private String pageNumber;
    @JsonProperty("entriesPerPage")
    private String entriesPerPage;
    @JsonProperty("requestType")
    private String requestType;
    
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "BUY", required = true/* , defaultValue = "" */, description ="requestType")
    @JsonProperty("requestType")
    public String getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "234", required = true/* , defaultValue = "" */, description ="External Code")
    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */, description ="Login Id")
    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }

    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }
    
    
    
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "transactionId", required = true/* , defaultValue = "" */, description ="Transaction ID")
    @JsonProperty("transactionId")
    public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description ="External Network Code")
    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "7234535", required = true/* , defaultValue = "" */, description ="Msisdn2")
    @JsonProperty("msisdn2")
    public String getMsisdn2() {
        return msisdn2;
    }

    @JsonProperty("msisdn2")
    public void setMsisdn2(String msisdn2) {
        this.msisdn2 = msisdn2;
    }
    
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description = "Password")
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = true/* , defaultValue = "" */, description ="Pin")
    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */, description = "Msisdn")
    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "T", required = true/* , defaultValue = "" */, description = "Transfer Type")
    @JsonProperty("transferType")
    public String getTransferType() {
        return transferType;
    }

    @JsonProperty("transferType")
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "Haryana", required = true/* , defaultValue = "" */, description = "Geographical Domain")
    @JsonProperty("geographicalDomain")
    public String getGeographicalDomain() {
        return geographicalDomain;
    }

    @JsonProperty("geographicalDomain")
    public void setGeographicalDomain(String geographicalDomain) {
        this.geographicalDomain = geographicalDomain;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */, description ="Domain")
    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */, description ="Category")
    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */, description ="Approval Level 1")
    @JsonProperty("approvalLevel")
    public String getApprovalLevel() {
        return approvalLevel;
    }

    @JsonProperty("approvalLevel")
    public void setApprovalLevel(String approvalLevel) {
        this.approvalLevel = approvalLevel;
    }
    
    
    
    
    public String getExtrefnum() {
		return extrefnum;
	}

	public void setExtrefnum(String extrefnum) {
		this.extrefnum = extrefnum;
	}

	
	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public String getEntriesPerPage() {
		return entriesPerPage;
	}

	public void setEntriesPerPage(String entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}
	
	

	

	public String getUsernameToSearch() {
		return usernameToSearch;
	}

	public void setUsernameToSearch(String usernameToSearch) {
		this.usernameToSearch = usernameToSearch;
	}

	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("approvalLevel = ").append(approvalLevel)
        		.append("transferType = ").append( transferType).append("msisdn2").append( msisdn2)
        		.append("geographicalDomain = ").append( geographicalDomain)
        		.append("domain = ").append( domain)
        		.append("category = ").append( category)
        		.append("extnwcode = ").append(extnwcode)
        		.append("msisdn = ").append(msisdn)
        		.append("pin = ").append(pin)
        		.append("loginid = ").append(loginid)
        		.append("password = ").append(password)
        		.append("extcode = ").append(extcode)
        		.append("transactionId = ").append(transactionId)).toString();
    }    
	
}
