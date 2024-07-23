package com.restapi.networkadmin.messagemanagement.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.messages.businesslogic.MessageArgumentVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageResponseVO extends BaseResponse {

    private String messageDetailCode;
    private String defaultMessage;
    private String message1;
    private String message2;
    private String message3;
    private String message4;
    private String message5;
    private List<MessagesArgumentVO> argumentList = null;
    private List<MessageLanguageVO> languageList = null;
}
