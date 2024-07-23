package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import com.btsl.pretups.common.SchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadFileRequestVO {
    @NotNull(message = "fileType cannot be missing or empty")
    @JsonProperty(value = "fileType", required = true)
    @Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN, maxLength = SchemaConstants.STRING_MAX_SIZE)
    private String fileType;
    @NotNull(message = "fileName cannot be missing or empty")
    @JsonProperty(value = "fileName", required = true)
    @Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN, maxLength = SchemaConstants.STRING_MAX_SIZE)
    private String fileName;
    @NotNull(message = "fileAttachment cannot be missing or empty")
    @JsonProperty(value = "fileAttachment", required = true)
    @Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN, maxLength = SchemaConstants.FILE_ATTACHMENT_MAX_SIZE)
    private String fileAttachment;

}
