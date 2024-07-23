package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Data
@Getter
@Setter
public class DeleteICCIDBulkResponseVO extends BaseResponse {
    private ArrayList errorList;
    private ArrayList errorListFinal;
    private String fileAttachment;
    private String fileName;
    private String fileType;

    private String messageCode;
    private String message;
    private String errorFlag;

}
