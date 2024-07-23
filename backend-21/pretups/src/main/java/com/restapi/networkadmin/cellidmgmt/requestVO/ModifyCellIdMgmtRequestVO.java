package com.restapi.networkadmin.cellidmgmt.requestVO;

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
public class ModifyCellIdMgmtRequestVO {
    @Schema(maxLength = 30, pattern = "^[a-zA-Z0-9]+$")
    @JsonProperty("groupName")
    @Pattern(regexp = "^[a-zA-Z0-9]+$",message = "Group name is invalid")
    @NotNull
    @NotBlank
    private String groupName;
    @Schema(maxLength = 30, pattern = "^[a-zA-Z0-9]+$")
    @JsonProperty("groupCode")
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$",message = "Group Code is invalid")
    private String groupCode;
    @Schema(maxLength = 30, pattern = "^[a-zA-Z0-9]+$")
    @JsonProperty("groupID")
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$",message = "Group Id is invalid")
    private String groupID;
    @Schema(maxLength = 1, pattern = "^[A-Z]$")
    @JsonProperty("status")
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Z]$",message = "Invalid status")
    private String status;
}
