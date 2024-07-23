package com.restapi.networkadmin.loyaltymanagement.requestVO;

import com.restapi.networkadmin.loyaltymanagement.responseVO.VolumeProfileDetailsVO;
import lombok.*;

import java.util.List;

@Setter
@Getter
public class AddProfileDetailsRequestVO {
    private String promotionType;
    private String profileName;
    private String applicableFromDate;
    private String applicableToDate;
    private String optInOutService;
    private String msgConfigEnabled;
    private String operatorContribution;
    private String parentContribution;
    private String referenceBased;
    private String referencefromDate;
    private String referenceToDate;
    private List<AddVolumeProfileDetailsVO> transDetailsVOList;
}
