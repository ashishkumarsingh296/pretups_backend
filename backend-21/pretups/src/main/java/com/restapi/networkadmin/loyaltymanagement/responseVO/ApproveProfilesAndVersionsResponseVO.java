package com.restapi.networkadmin.loyaltymanagement.responseVO;

import com.btsl.common.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveProfilesAndVersionsResponseVO extends BaseResponse {
    private List<ApproveProfilesAndVersionsVO> approveProfilesAndVersionsVOList;
}
