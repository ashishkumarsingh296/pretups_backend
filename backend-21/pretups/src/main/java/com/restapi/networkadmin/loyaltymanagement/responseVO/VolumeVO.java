package com.restapi.networkadmin.loyaltymanagement.responseVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeVO {
    private String targetType;
    private String targetTypeDesc;
    private long target;
    private String frequency;
    private String frequencyDesc;
    private String rewardsType;
    private String rewardsTypeDesc;
    private long points;
    private long fromRange;
    private long toRange;
}
