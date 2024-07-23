package com.restapi.networkadmin.messagemanagement.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessagesBulkResponseVO extends BaseResponse {
    private String fileName;
    private String fileType;
    private String fileAttachment;
    private List<ListValueVO> errorList;
    private String errorFlag;
    private int totalRecords = 0;
    private int failCount = 0;
}
