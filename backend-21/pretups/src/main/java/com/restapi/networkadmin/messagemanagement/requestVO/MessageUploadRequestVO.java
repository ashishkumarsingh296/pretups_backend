package com.restapi.networkadmin.messagemanagement.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageUploadRequestVO {
    private String fileType;
    private String fileName;
    private String fileAttachment;
}
