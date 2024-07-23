package com.restapi.networkadmin.loyaltymanagement.responseVO;

import com.btsl.common.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileMessageDetailsVO extends BaseResponse {
    private String welcomeMesage1;
    private String welcomeMesage2;
    private String successMessage1;
    private String successMessage2;
    private String failureMessage1;
    private String failureMessage2;
    private String profileName;
    private String setID;
    private String version;
}
