package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlacklistMultipleSubscribersRequestVO {

    private String geographicalDomain;
    private String domain;
    private String category;
    private String userName;
    private String userID;
    private String p2pPayer;
    private String p2pPayee;
    private String c2sPayee;
    private String selectionType;
    private UploadFileRequestVO uploadFileRequestVO;
}
