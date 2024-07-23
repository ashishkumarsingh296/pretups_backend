package com.restapi.networkadmin.loyaltymanagement.requestVO;

import lombok.*;

@Setter
@Getter
public class AddVolumeVO {
        private String targetType;
        private long target;
        private String frequency;
        private String rewardsType;
        private long points;
        private long fromRange;
        private long toRange;
}

