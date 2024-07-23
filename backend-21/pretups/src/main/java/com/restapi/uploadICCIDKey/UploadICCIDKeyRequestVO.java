package com.restapi.uploadICCIDKey;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadICCIDKeyRequestVO {
    @NotNull
    private String fileType;
    @NotNull
    private String fileName;
    @NotNull
    private String fileAttachment;
}
