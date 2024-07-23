package com.restapi.networkadmin.messagemanagement.responseVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessagesArgumentVO {

    private String argument;
    private String argumentDesc;
    private String ArgumentsWithBraces;

    public String getArgumentsWithBraces() {

        String str = null;

        str = "\n" + "{" +argument +"}"+ "=" + argumentDesc + ",";

        return str;
    }
}
