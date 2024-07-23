package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlacklistSubscriberErrorList {
    private String errorCode;
    private String lineNumber;
    private String msisdn;
    private long failCount;
}
