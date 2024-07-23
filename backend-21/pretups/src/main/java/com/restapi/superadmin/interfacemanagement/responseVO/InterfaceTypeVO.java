package com.restapi.superadmin.interfacemanagement.responseVO;

import com.btsl.common.ListValueVO;
import com.btsl.util.BTSLUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class InterfaceTypeVO implements Serializable{
    String interfaceTypeId;
    String interfaceTypeName;
    String uriRequired;
}
