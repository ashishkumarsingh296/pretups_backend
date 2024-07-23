package com.restapi.batchC2STransferRule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchC2STransferRuleRequestVO {
    @NotNull
    private String fileType;
    @NotNull
    private String fileName;
    @NotNull
    private String fileAttachment;
    @NotNull
    @Size(max = 20)
    private String batchName;
}
