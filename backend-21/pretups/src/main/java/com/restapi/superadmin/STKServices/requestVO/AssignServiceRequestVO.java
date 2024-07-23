package com.restapi.superadmin.STKServices.requestVO;

import com.restapi.superadmin.STKServices.responseVO.STKServicesVO;
import com.restapi.superadmin.STKServices.responseVO.STKUserServicesVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AssignServiceRequestVO {
    private String categoryCode;
    private String profileCode;
    private String simProfileCode;
    private Integer position;
    private STKServicesVO serviceDetails;
    private String userServiceStatus;
    private String offset;
    private ArrayList<STKUserServicesVO> freeOffsetList;
}
