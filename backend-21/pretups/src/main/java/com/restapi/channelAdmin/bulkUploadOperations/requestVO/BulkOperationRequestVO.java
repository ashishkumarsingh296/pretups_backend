package com.restapi.channelAdmin.bulkUploadOperations.requestVO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkOperationRequestVO {
    @io.swagger.v3.oas.annotations.media.Schema(required = true)
    private String file;
    @io.swagger.v3.oas.annotations.media.Schema(required = true)
    private String fileName;
    @io.swagger.v3.oas.annotations.media.Schema(required = true)
    private String fileType;
    @io.swagger.v3.oas.annotations.media.Schema(example = "DR", required = true)
    private String operationType;
}
