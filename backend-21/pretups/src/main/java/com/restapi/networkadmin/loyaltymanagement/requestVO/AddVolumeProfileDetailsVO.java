package com.restapi.networkadmin.loyaltymanagement.requestVO;

import lombok.*;

import java.util.List;

@Setter
@Getter
public class AddVolumeProfileDetailsVO {

        private String product;
        private String module;
        private String service;
        private List<AddVolumeVO> volumeVOList;

}
