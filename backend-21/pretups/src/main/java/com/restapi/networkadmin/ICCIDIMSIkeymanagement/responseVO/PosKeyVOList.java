package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PosKeyVOList {
    private PosKeyVO posKeyVO;
    private UserDetailsVO userDetailsVO;
    private String noChannelUser;
    private String noPosKey;
}
