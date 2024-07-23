package restassuredapi.pojo.txncalculationvoucherstockrequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Products {

   	 @JsonProperty("productcode")
   	private String productcode;	
   	 @JsonProperty("qty")
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
