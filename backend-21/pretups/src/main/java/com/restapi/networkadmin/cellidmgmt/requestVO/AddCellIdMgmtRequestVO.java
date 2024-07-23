package com.restapi.networkadmin.cellidmgmt.requestVO;

import com.btsl.pretups.common.SchemaConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCellIdMgmtRequestVO {
    @Schema(maxLength = SchemaConstants.STRING_MAX_SIZE, pattern = SchemaConstants.STRING_INPUT_PATTERN)
    @JsonProperty("groupName")
    @Pattern(regexp = SchemaConstants.STRING_INPUT_PATTERN, message = "Group name is invalid")
    @NotNull
    @NotBlank
    private String groupName;
    @Schema(maxLength = SchemaConstants.STRING_MAX_SIZE, pattern = SchemaConstants.STRING_INPUT_PATTERN)
    @JsonProperty("groupCode")
    @NotNull
    @NotBlank
    @Pattern(regexp = SchemaConstants.STRING_INPUT_PATTERN, message = "Group Code is invalid")
    private String groupCode;
    @Schema(maxLength = SchemaConstants.STRING_MAX_SIZE, pattern = SchemaConstants.STATUS_INPUT_PATTERN)
    @JsonProperty("status")
    @NotNull
    @NotBlank
    @Pattern(regexp = SchemaConstants.STATUS_INPUT_PATTERN, message = "Invalid status")
    private String status;
}
