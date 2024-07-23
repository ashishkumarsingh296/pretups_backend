package com.restapi.channelAdmin.bulkUploadOperations.ResponseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BulkOperationResponseVO extends BaseResponse {
    private String fileName;
    private String fileAttachment;
}
