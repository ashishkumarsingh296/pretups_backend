package com.restapi.batchC2STransferRule;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchC2STransferRuleFileDownloadResponse extends BaseResponse {
    private String fileName;
    private String fileAttachment;
    private String fileType;
}
