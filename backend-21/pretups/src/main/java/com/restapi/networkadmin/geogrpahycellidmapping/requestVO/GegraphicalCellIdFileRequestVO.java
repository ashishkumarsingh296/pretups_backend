package com.restapi.networkadmin.geogrpahycellidmapping.requestVO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GegraphicalCellIdFileRequestVO {
    @NotNull
    @NotBlank(message = "fileType is missing. fileType is required")
    private String fileType;
    @NotNull
    @NotBlank(message = "fileName is missing. fileName is required")
    private String fileName;
    @NotNull
    @NotBlank(message = "fileAttachment is missing. fileAttachment is required")
    private String fileAttachment;
}