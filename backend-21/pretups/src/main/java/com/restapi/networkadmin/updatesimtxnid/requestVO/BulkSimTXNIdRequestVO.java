package com.restapi.networkadmin.updatesimtxnid.requestVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BulkSimTXNIdRequestVO {
    @io.swagger.v3.oas.annotations.media.Schema(required = true)
    private String fileAttachment;
    @io.swagger.v3.oas.annotations.media.Schema(required = true)
    private String fileName;
    @io.swagger.v3.oas.annotations.media.Schema(required = true)
    private String fileType;

}
