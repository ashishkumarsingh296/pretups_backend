package com.restapi.superadmin.subscriberrouting.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class BulkResponseVO extends BaseResponse {
    private ArrayList errorList;
    private String totalFailCount;
    private String processedRecords;
    private String errorFlag;
    private String numberOfRecords;
    private String fileAttachment;
    private String fileName;
    private String fileType;
}