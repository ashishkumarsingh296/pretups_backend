package com.restapi.networkadmin.loyaltymanagement.responseVO;

import com.btsl.user.businesslogic.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailsVO extends BaseResponse {
    private String validUpToDate;
    private String networkName;
    private String promotionType;
    private String promotionTypeName;
    private String profileName;
    private String setId;
    private String shortCode;
    private int version;
    private String currentServerDateAndTime;
    private String applicableFromDate;
    private String applicableFromHour;
    private String oldApplicableFromDate;
    private String oldApplicableFromHour;
    private String applicableToDate;
    private String oldApplicableToDate;
    private String applicableToHour;
    private String oldApplicableToHour;
    private String optInOutService;
    private String optInOutServiceDes;
    private String optInOutTarget;
    private String optInOutTargetDes;
    private String msgConfigEnabled;
    private String msgConfigEnabledDes;
    private String operatorContribution;
    private String parentContribution;
    private String referenceBased;
    private String referenceBasedDes;
    private String referencefromDate;
    private String referenceToDate;
    private String bonusDuration;
    private String bonusPerActivation;
    private int lastVersion;
    private String profileExpiredFlag;
    private String transactionProfileSubType;
    private String volumeProfileSubType;
    private List<VolumeProfileDetailsVO> transDetailsVOList;
    private List<VolumeProfileDetailsVO> volumeProfileList;


}
