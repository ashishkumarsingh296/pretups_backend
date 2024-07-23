package com.restapi.superadmin.interfacemanagement.requestVO;

import com.btsl.pretups.interfaces.businesslogic.InterfaceNodeDetailsVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString
public class InterfaceDetailRequestVO {

    private String interfaceType;
    private String interfaceName;
    private String externalId;
    private String nodeSize;
    private String SingleStageTransaction;
    private String validityExpiryTime;
    private String topupExpiryTime;
    private String language1;
    private String language2;
    private String Status;
    private ArrayList<InterfaceNodeDetailsVO> nodeList;
}
