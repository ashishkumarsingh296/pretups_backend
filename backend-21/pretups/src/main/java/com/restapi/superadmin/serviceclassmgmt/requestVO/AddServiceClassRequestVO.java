package com.restapi.superadmin.serviceclassmgmt.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddServiceClassRequestVO {

    @JsonProperty("interfaceCode")
    private String interfaceCode;

    @JsonProperty("serviceClassName")
    private String serviceClassName;

    @JsonProperty("serviceClassCode")
    private String serviceClassCode;

    @JsonProperty("serviceClassId")
    private String serviceClassId;

    @JsonProperty("p2pSenderSuspend")
    private String p2pSenderSuspend;

    @JsonProperty("p2pReceiverSuspend")
    private String p2pReceiverSuspend;

    @JsonProperty("c2sReceiverSuspend")
    private String c2sReceiverSuspend;

    @JsonProperty("p2pSenderAllowedStatus")
    private String p2pSenderAllowedStatus;

    @JsonProperty("p2pSenderDeniedStatus")
    private String p2pSenderDeniedStatus;

    @JsonProperty("p2pReceiverAllowedStatus")
    private String p2pReceiverAllowedStatus;

    @JsonProperty("p2pReceiverDeniedStatus")
    private String p2pReceiverDeniedStatus;

    @JsonProperty("c2sReceiverAllowedStatus")
    private String c2sReceiverAllowedStatus;

    @JsonProperty("c2sReceiverDeniedStatus")
    private String c2sReceiverDeniedStatus;

    @JsonProperty("interfaceCategory")
    private String interfaceCategory;

    @JsonProperty("status")
    private String status;

    @JsonProperty("interfaceName")
    private String interfaceName;



}
