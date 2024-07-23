package com.restapi.user.service;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherConRequestVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "External Reference Id ", requiredMode = Schema.RequiredMode.REQUIRED, required = false/* , position = 1 */, example = "12234")
	protected String externalRefId;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "External Network Code", required = false/* , position = 2 */, example = "NG")
    protected String extnwcode;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "Msisdn", required = true/* , position = 3 */, example = "722211213")
    protected String msisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "pin", required = true/* , position = 4 */, example = "1357")
    protected String pin;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "Selector", required = true/* , position = 5 */, example = "1")
    protected String selector;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "Msisdn2", required = true/* , position = 6 */, example = "7200012301")
    protected String msisdn2;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "Language1", required = true/* , position = 7 */, example = "1")
    protected String language1;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "Language2", required = true/* , position = 8 */, example = "1")
    protected String language2;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "Vouchercode", required = true/* , position = 9 */, example = "392830764372")
    protected String vouchercode;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "serialnumber", required = true/* , position = 10*/, example = "9930390000000003")
    protected String serialnumber;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "info1", required = true/* , position = 11*/, example = "voda")
    protected String info1;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "info2", required = true/* , position = 12*/, example = "ziggo")
    protected String info2;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "info3", required = true/* , position = 13*/, example = "Voda")
    protected String info3;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "info4", required = true/* , position = 14*/, example = "ziggo")
    protected String info4;
	
	@io.swagger.v3.oas.annotations.media.Schema(description = "info5", required = true/* , position = 15*/, example = "voucher")
    protected String info5;

	@io.swagger.v3.oas.annotations.media.Schema(description = "amount", required = true/* , position = 16*/, example = "100.0")
    protected String amount;

	public String getExtrefnum() {
		return extrefnum;
	}

	public void setExtrefnum(String extrefnum) {
		this.extrefnum = extrefnum;
	}

	protected String extrefnum;
	
    public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getExternalRefId() {
		return externalRefId;
	}

	public void setExternalRefId(String externalRefId) {
		this.externalRefId = externalRefId;
	}

	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getMsisdn2() {
		return msisdn2;
	}

	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}

	public String getLanguage1() {
		return language1;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	public String getLanguage2() {
		return language2;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}

	public String getVouchercode() {
		return vouchercode;
	}

	public void setVouchercode(String vouchercode) {
		this.vouchercode = vouchercode;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	public String getInfo5() {
		return info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}
	
	
	
}
