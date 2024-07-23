package com.restapi.networkadmin.loyaltymanagement.responseVO;

import com.btsl.common.BaseResponse;
import com.restapi.networkadmin.loyaltymanagement.requestVO.SuspendRequestVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveProfilesAndVersionsVO {
    private String profileName;
    private List<SuspendRequestVO> profileIdAndVersions;
}
