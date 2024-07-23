package com.restapi.networkadmin.loyaltymanagement.responseVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeProfileDetailsVO {

    private String product;
    private String productDesc;
    private String module;
    private String moduleDesc;
    private String service;
    private String serviceDesc;
    private List<VolumeVO> volumeVOList;
}
