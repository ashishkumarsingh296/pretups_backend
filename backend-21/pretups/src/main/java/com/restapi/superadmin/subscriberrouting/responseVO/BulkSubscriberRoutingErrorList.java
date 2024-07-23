package com.restapi.superadmin.subscriberrouting.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkSubscriberRoutingErrorList {
    private String errorMsg;
    private String lineNumber;
    private String msisdn;
}
