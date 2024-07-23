package com.restapi.networkadmin.updatesimtxnid.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class BulkSimTXNIdResponseVO extends BaseResponse {
    private ArrayList errorList;
    private String fileAttachment;
    private String fileName;
    private String fileType;
    private int totalRecords = 0;
    private int validRecords = 0;
}
