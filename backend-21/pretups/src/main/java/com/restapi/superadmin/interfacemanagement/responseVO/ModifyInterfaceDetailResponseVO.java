package com.restapi.superadmin.interfacemanagement.responseVO;

import com.btsl.pretups.interfaces.businesslogic.InterfaceNodeDetailsVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class ModifyInterfaceDetailResponseVO extends InterfaceDetailResponseVO {
    private ArrayList<InterfaceNodeDetailsVO> nodeDetailList;
    private String deleteMessage;
}
