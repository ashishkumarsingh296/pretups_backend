package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class C2CStockTransferMultRequestVO extends OAuthUser {
  
	@JsonProperty("data")
    private List<DataStockMul> dataStkTrf = null;
	@JsonProperty("data")
	    public List<DataStockMul> getDataStkTrfMul() {
		return dataStkTrf;
	}
	@JsonProperty("data")
	public void setDataStkTrfMul(List<DataStockMul> dataStkTrf) {
		this.dataStkTrf = dataStkTrf;
	}
		@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("dataStkTrf = ").append( dataStkTrf)).toString();
    }
}
	    class DataStockMul {
	        @JsonProperty("refnumber")
	        private String refnumber;
	        @JsonProperty("msisdn2")
	        private String msisdn2;
	        @JsonProperty("loginid2")
	        private String loginid2;
	        @JsonProperty("extcode2")
	        private String extcode2;
	        @JsonProperty("paymentdetails")
	        private List<PaymentDetails> paymentdetails;
	        @JsonProperty("products")
	        private List<Products> products = null;
	        @JsonProperty("language1")
	        private String language1;
	        @JsonProperty("remarks")
	        private String remarks;
	        @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
	        @JsonProperty("refnumber")
	        private int row;
	        
	        /**
			 * @return the row
			 */
			public int getRow() {
				return row;
			}
			/**
			 * @param row the row to set
			 */
			public void setRow(int row) {
				this.row = row;
			}
			public String getRefnumber() {
	            return refnumber;
	        }
	        @JsonProperty("refnumber")
	        public void setRefnumber(String refnumber) {
	            this.refnumber = refnumber;
	        }
	        @io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */)
	        @JsonProperty("msisdn2")
	        public String getMsisdn2() {
	            return msisdn2;
	        }
	        @JsonProperty("msisdn2")
	        public void setMsisdn2(String msisdn2) {
	            this.msisdn2 = msisdn2;
	        }
	        @io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */)
	        @JsonProperty("loginid2")
	        public String getLoginid2() {
	            return loginid2;
	        }
	        @JsonProperty("loginid2")
	        public void setLoginid2(String loginid2) {
	            this.loginid2 = loginid2;
	        }
	        @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
	        @JsonProperty("extcode2")
	        public String getExtcode2() {
	            return extcode2;
	        }
	        @JsonProperty("extcode2")
	        public void setExtcode2(String extcode2) {
	            this.extcode2 = extcode2;
	        }
	        @io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */)
	        @JsonProperty("language1")
	        public String getLanguage1() {
	            return language1;
	        }
	        @JsonProperty("language1")
	        public void setLanguage1(String language1) {
	            this.language1 = language1;
	        }
	        @JsonProperty("products")
	        public List<Products> getProducts() {
	            return products;
	        }
	        @JsonProperty("products")
	        public void setProducts(List<Products> products) {
	            this.products = products;
	        }
	        @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
	        @JsonProperty("paymentdetails")
	        public List<PaymentDetails> getPaymentDetails() {
	            return paymentdetails;
	        }
	        @JsonProperty("paymentdetails")
	        public void setPaymentDetails(List<PaymentDetails> paymentdetails) {
	            this.paymentdetails = paymentdetails;
	        }
	        @io.swagger.v3.oas.annotations.media.Schema(example = "remarks", required = true/* , defaultValue = "" */)
	        @JsonProperty("remarks")
	        public String getRemarks() {
	            return remarks;
	        }
	        @JsonProperty("remarks")
	        public void setRemarks(String remarks) {
	            this.remarks = remarks;
	        }
	        @Override
	        public String toString() {
	        	StringBuilder sb = new StringBuilder();
	            return (sb.append("language1").append( language1)
	            		.append("products = ").append( products)
	            		.append("remarks").append( remarks)
	            		.append("msisdn2 = ").append(msisdn2)
	            		.append("loginid2 = ").append(loginid2)
	            		.append("extcode2 = ").append(extcode2)
	            		.append("paymentdetails = ").append(paymentdetails)
	            		).toString();
	        }
	    }
	     class Products {
	        @JsonProperty("productcode")
	        private String productcode;
	        @JsonProperty("qty")
	        private String qty;
	        @io.swagger.v3.oas.annotations.media.Schema(example = "102", required = true/* , defaultValue = "" */)
	        @JsonProperty("productcode")
	        public String getProductcode() {
	            return productcode;
	        }
	        @JsonProperty("productcode")
	        public void setProductcode(String productcode) {
	            this.productcode = productcode;
	        }
	        @io.swagger.v3.oas.annotations.media.Schema(example = "23", required = true/* , defaultValue = "" */)
	        @JsonProperty("qty")
	        public String getQty() {
	            return qty;
	        }
	        @JsonProperty("qty")
	        public void setQty(String qty) {
	            this.qty = qty;
	        }
	        @Override
	        public String toString() {
	        	StringBuilder sb = new StringBuilder();
	            return (sb.append("productcode").append(productcode)
	            		.append("qty = ").append(qty)
	            		).toString();
	        }
	    }
	     class PaymentDetails {
	    	    @JsonProperty("paymenttype")
	    	    private String paymenttype;
	    	    @JsonProperty("paymentinstnumber")
	    	    private String paymentinstnumber;
	    	    @JsonProperty("paymentdate")
	    	    private String paymentdate;
	    	  
	    	    @io.swagger.v3.oas.annotations.media.Schema(example = "102", required = true/* , defaultValue = "" */)
	    	    @JsonProperty("paymenttype")
	    	    public String getPaymenttype() {
	    	        return paymenttype;
	    	    }
	    	    @JsonProperty("paymenttype")
	    	    public void setPaymenttype(String paymenttype) {
	    	        this.paymenttype = paymenttype;
	    	    }
	    	    @io.swagger.v3.oas.annotations.media.Schema(example = "23/12/20", required = true/* , defaultValue = "" */)
	    	    @JsonProperty("paymentdate")
	    	    public String getPaymentdate() {
	    	        return paymentdate;
	    	    }
	    	    @JsonProperty("paymentdate")
	    	    public void setPaymentdate(String paymentdate) {
	    	        this.paymentdate = paymentdate;
	    	    }
	    	    @io.swagger.v3.oas.annotations.media.Schema(example = "23", required = true/* , defaultValue = "" */)
	    	    @JsonProperty("paymentinstnumber")
	    	    public String getPaymentinstnumber() {
	    	        return paymentinstnumber;
	    	    }
	    	    @JsonProperty("paymentinstnumber")
	    	    public void setPaymentinstnumber(String paymentinstnumber) {
	    	        this.paymentinstnumber = paymentinstnumber;
	    	    }
	    	   
	    	   
	    	    @Override
	    	    public String toString() {
	    	    	StringBuilder sb = new StringBuilder();
	    	        return (sb.append("paymenttype").append(paymenttype)
	    	        		.append("paymentinstnumber = ").append(paymentinstnumber)
	    	        		.append("paymentdate = ").append(paymentdate)
	    	        		).toString();
	    	    }
}
