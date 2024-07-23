package com.restapi.batchC2STransferRule;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchC2STransferRuleResponseVO extends BaseResponse {
    private String fileAttachment;
    private int totalRecords = 0;
    private String fileName;
    private String _noOfRecords;
    private String fileType;
}
