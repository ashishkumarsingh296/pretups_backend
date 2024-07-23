package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "This is a Product field")
public class Products{
	@JsonProperty("productcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true, description= "Product Code")
	private String productcode;
	@JsonProperty("qty")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true, description= "Product Quantity")
	private String qty;	
	@JsonProperty("productcode")
	public String getProductcode() {	
		return productcode;	
	}	
	@JsonProperty("productcode")
	public void setProductcode(String productcode) {	
		this.productcode = productcode;	
	}	
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
		return "Products [productcode=" + productcode + ", qty=" + qty + "]";
	}
}
