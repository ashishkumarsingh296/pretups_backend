package com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MSISDNWithICCIDFileRequestVO {
    @NotNull
    @NotBlank
    @NotBlank(message = "fileAttachment is missing. fileAttachment is required")
    private String fileType;
    @NotNull
    @NotBlank(message = "fileName is missing. fileName is required")
    private String fileName;
    @NotBlank(message = "fileAttachment is missing. fileAttachment is required")
    @NotNull
    private String fileAttachment;
}
