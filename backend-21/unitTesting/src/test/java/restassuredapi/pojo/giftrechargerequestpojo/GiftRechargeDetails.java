package restassuredapi.pojo.giftrechargerequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GiftRechargeDetails {
	
	@JsonProperty("date")
	private String date;
	
	@JsonProperty("date")
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	@JsonProperty("extnwcode")
	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	
	
	//@JsonProperty("msisdn")
	//@ApiModelProperty(example = "726576538", required = true, value = "")
	
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	@JsonProperty("pin")
	//@ApiModelProperty(example = "1357", required = true, value = "")
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	
	//@JsonProperty("loginid")
	//@ApiModelProperty(example = "deepadist", required = true, value = "")
	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	
	//@JsonProperty("password")
	//@ApiModelProperty(example = "1357", required = true, value = "")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	//@JsonProperty("extcode")
	//@ApiModelProperty(example = "22435", required = true, value = "")
	public String getExtcode() {
		return extcode;
	}

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}
	@JsonProperty("extrefnum")
	public String getExtrefnum() {
		return extrefnum;
	}

	public void setExtrefnum(String extrefnum) {
		this.extrefnum = extrefnum;
	}
	@JsonProperty("msisdn2")
	public String getMsisdn2() {
		return msisdn2;
	}

	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}
	@JsonProperty("amount")
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	@JsonProperty("language1")
	public String getLanguage1() {
		return language1;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}
	@JsonProperty("language2")
	public String getLanguage2() {
		return language2;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}
	@JsonProperty("selector")
	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	@JsonProperty("extnwcode")
	private String extnwcode;
	
	//@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("pin")
	private String pin;
	
	private String loginid;
	
	private String password;
	
	private String extcode;
	
	@JsonProperty("extrefnum")
	private String extrefnum;
	
	@JsonProperty("msisdn2")
	private String msisdn2;
	
	@JsonProperty("amount")
	private String amount;
	
	@JsonProperty("language1")
	private String language1;
	
	@JsonProperty("language2")
	private String language2;
	
	@JsonProperty("selector")
	private String selector;
	
	@JsonProperty("gifterName")
	private String gifterName;
	
	@JsonProperty("gifterName")
	public String getGifterName() {
		return gifterName;
	}

	public void setGifterName(String gifterName) {
		this.gifterName = gifterName;
	}
	@JsonProperty("gifterMsisdn")
	public String getGifterMsisdn() {
		return gifterMsisdn;
	}

	public void setGifterMsisdn(String gifterMsisdn) {
		this.gifterMsisdn = gifterMsisdn;
	}
	@JsonProperty("gifterLang")
	public String getGifterLang() {
		return gifterLang;
	}

	public void setGifterLang(String gifterLang) {
		this.gifterLang = gifterLang;
	}
	
	@JsonProperty("gifterMsisdn")
	private String gifterMsisdn;
	
	@JsonProperty("gifterLang")
	private String gifterLang;
	
	private String notifMsisdn;

	public String getNotifMsisdn() {
		return notifMsisdn;
	}

	public void setNotifMsisdn(String notifMsisdn) {
		this.notifMsisdn = notifMsisdn;
	}
}
