package com.restapi.networkadmin.loyaltymanagement.responseVO;

import com.btsl.common.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionsResponseVO extends BaseResponse {
    private String setId;
    private List versions;
}
