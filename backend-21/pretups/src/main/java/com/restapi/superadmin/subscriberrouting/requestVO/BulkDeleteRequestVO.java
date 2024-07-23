package com.restapi.superadmin.subscriberrouting.requestVO;

import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.UploadFileRequestVO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkDeleteRequestVO {
    @NotNull
    private String interfaceCategory;
    @NotNull
    private UploadFileRequestVO uploadFileRequestVO;
}
