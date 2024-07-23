package com.btsl.pretups.channel.transfer.businesslogic;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "totalOrderAmount",
    "totalPayableAmount",
    "totalReceiverQuantity",
    "totalSenderQuantity",
    "totalNetPayableAmount",
    "totalNetCommissionQuanitity"
})
public class TotalDetails {

    @JsonProperty("totalOrderAmount")
    private String totalOrderAmount;
    @JsonProperty("totalPayableAmount")
    private String totalPayableAmount;
    @JsonProperty("totalReceiverQuantity")
    private String totalReceiverQuantity;
    @JsonProperty("totalSenderQuantity")
    private String totalSenderQuantity;
    @JsonProperty("totalNetPayableAmount")
    private String totalNetPayableAmount;
    @JsonProperty("totalNetCommissionQuanitity")
    private String totalNetCommissionQuanitity;
    @JsonProperty("totalNetCommissionQuanitity")
    public String getTotalNetCommissionQuanitity() {
		return totalNetCommissionQuanitity;
	}
    @JsonProperty("totalNetCommissionQuanitity")
	public void setTotalNetCommissionQuanitity(String totalNetCommissionQuanitity) {
		this.totalNetCommissionQuanitity = totalNetCommissionQuanitity;
	}
	@JsonProperty("totalOrderAmount")
    public String getTotalOrderAmount() {
        return totalOrderAmount;
    }
    @JsonProperty("totalOrderAmount")
    public void setTotalOrderAmount(String totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    @JsonProperty("totalPayableAmount")
    public String getTotalPayableAmount() {
        return totalPayableAmount;
    }

    @JsonProperty("totalPayableAmount")
    public void setTotalPayableAmount(String totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    @JsonProperty("totalReceiverQuantity")
    public String getTotalReceiverQuantity() {
        return totalReceiverQuantity;
    }

    @JsonProperty("totalReceiverQuantity")
    public void setTotalReceiverQuantity(String totalReceiverQuantity) {
        this.totalReceiverQuantity = totalReceiverQuantity;
    }

    @JsonProperty("totalSenderQuantity")
    public String getTotalSenderQuantity() {
        return totalSenderQuantity;
    }

    @JsonProperty("totalSenderQuantity")
    public void setTotalSenderQuantity(String totalSenderQuantity) {
        this.totalSenderQuantity = totalSenderQuantity;
    }
    public String getTotalNetPayableAmount() {
		return totalNetPayableAmount;
	}
	public void setTotalNetPayableAmount(String totalNetPayableAmount) {
		this.totalNetPayableAmount = totalNetPayableAmount;
	}
	@Override
	public String toString() {
		return "ChannelTransferItemsVO [totalSenderQuantity=" + totalSenderQuantity
				+ ", totalReceiverQuantity=" + totalReceiverQuantity + ", totalPayableAmount="
				+ totalPayableAmount + ", totalOrderAmount="
				+ totalOrderAmount + "]";
	}


}

