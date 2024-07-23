package com.restapi.networkadmin.messagemanagement.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageLanguageVO {
    private int _sequenceNo = 0;
    private String name;
    private String status;
    private String message;
    private String language_code;
}
