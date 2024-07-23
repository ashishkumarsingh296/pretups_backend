package com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class DeleteICCIDBulkRequestVO {
    @NotNull
    @NotBlank(message = "fileAttachment is missing. fileAttachment is required")
    private String fileType;

    @NotNull
    @NotBlank(message = "fileName is missing. fileName is required")
    private String fileName;

    @NotNull
    @NotBlank(message = "fileAttachment is missing. fileAttachment is required")
    private String fileAttachment;
}
