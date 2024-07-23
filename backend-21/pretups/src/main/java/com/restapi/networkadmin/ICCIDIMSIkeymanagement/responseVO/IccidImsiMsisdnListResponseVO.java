package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class IccidImsiMsisdnListResponseVO extends BaseResponse {
    private PosKeyVO posKeyVO;
    private String noChannelUser;
    private String noPosKey;
    private List<PosKeyVOList> posKeyVOList;
    private UserDetailsVO userDetailsVO;
}

