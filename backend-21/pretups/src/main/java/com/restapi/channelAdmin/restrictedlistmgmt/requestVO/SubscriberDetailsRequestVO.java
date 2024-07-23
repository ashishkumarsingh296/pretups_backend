package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public  class SubscriberDetailsRequestVO{

    @NotNull(message="status cannot be missing or empty")
	@JsonProperty(value = "status", required = true)
    @Size(min=1, max=1, message="status must be in A-Approve or R-Reject")
	private String status;
	private String approvedBy;
	private String modifiedBy;
	@NotNull(message="ownerID cannot be missing or empty")
	@JsonProperty(value = "ownerID", required = true)
	@Size(min=14, max=14, message="ownerID must be valid ownerID")
	private String ownerID;
	@NotNull(message="msisdn cannot be missing or empty")
	@JsonProperty(value = "msisdn", required = true)
	@Size(min=10, max=10, message="msisdn must not be valid mobile number")
	private String msisdn;
}
