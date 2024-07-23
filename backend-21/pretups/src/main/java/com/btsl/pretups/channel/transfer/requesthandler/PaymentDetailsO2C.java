package com.btsl.pretups.channel.transfer.requesthandler;


import com.fasterxml.jackson.annotation.JsonProperty;


public class PaymentDetailsO2C {
	@JsonProperty("paymentinstnumber")
    private String paymentinstnumber;
	
	@JsonProperty("paymentdate")
    private String paymentdate;
	
	@JsonProperty("paymenttype")
    private String paymenttype;
	
	@JsonProperty("paymentgatewaytype")
    private String paymentgatewaytype;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "8768", required = true, defaultValue = "Payment Number")
    public String getPaymentinstnumber() {
        return paymentinstnumber;
    }

    public void setPaymentinstnumber(String paymentinstnumber) {
        this.paymentinstnumber = paymentinstnumber;
    }
    @JsonProperty("paymentdate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "24/09/20", required = true, defaultValue = "Payment Date")
    public String getPaymentdate() {
        return paymentdate;
    }

    public void setPaymentdate(String paymentdate) {
        this.paymentdate = paymentdate;
    }

    @JsonProperty("paymenttype")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Cash", required = true, defaultValue = "Payment type")
    public String getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    @JsonProperty("paymentgatewaytype")
	@io.swagger.v3.oas.annotations.media.Schema(example = "PaymenGateway1", required = true, defaultValue = "Payment Gateway Type")
	public String getPaymentgatewayType() {
		return paymentgatewaytype;
	}

	public void setPaymentgatewayType(String paymentgatewaytype) {
		this.paymentgatewaytype = paymentgatewaytype;
	}

	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("paymenttype = ").append(paymenttype)
        		.append("paymentdate").append(paymentdate)
        		.append("paymentinstnumber").append(paymentinstnumber)
        		.append("paymentgatewaytype").append(paymentgatewaytype)).toString();
    }
}
