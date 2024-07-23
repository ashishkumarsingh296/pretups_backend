package com.restapi.networkadmin.cellidmgmt.responseVO;

import com.btsl.pretups.common.SchemaConstants;
import com.btsl.util.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CellGroupVO {
    @Schema(maxLength = SchemaConstants.STRING_MAX_SIZE, pattern = SchemaConstants.STRING_INPUT_PATTERN)
    @JsonProperty("groupName")
    private String groupName;
    @Schema(maxLength = SchemaConstants.STRING_MAX_SIZE, pattern = SchemaConstants.STRING_INPUT_PATTERN)
    @JsonProperty("groupCode")
    private String groupCode;
    @Schema(maxLength = SchemaConstants.STRING_MAX_SIZE, pattern = SchemaConstants.STRING_INPUT_PATTERN)
    @JsonProperty("groupID")
    private String groupID;
    @Schema(maxLength = SchemaConstants.STRING_MAX_SIZE, pattern = SchemaConstants.STRING_INPUT_PATTERN)
    @JsonProperty("status")
    private String status;
    @Schema(maxLength = SchemaConstants.STRING_MAX_SIZE, pattern = SchemaConstants.STRING_INPUT_PATTERN)
    @JsonProperty("statusDesc")
    private String statusDesc;
}
