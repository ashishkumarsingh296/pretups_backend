package com.restapi.networkadmin.loyaltymanagement.responseVO;

import com.btsl.common.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDetailsVersionsResponseVO extends BaseResponse {
    List<ProfileDetailsSet> profileDetailsSetList;

}
