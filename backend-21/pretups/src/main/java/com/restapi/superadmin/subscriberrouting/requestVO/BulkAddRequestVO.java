package com.restapi.superadmin.subscriberrouting.requestVO;

import com.btsl.common.BaseResponse;
import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.UploadFileRequestVO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkAddRequestVO{
    @NotNull
    private String interfaceCategory;
    @NotNull
    private String interfaceType;
    @NotNull
    private String interfaceId;
    @NotNull
    private UploadFileRequestVO uploadFileRequestVO;
}
