package com.restapi.networkadmin.operatorUser.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.requesthandler.GroupedUserRolesVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class OperatorUserRolesResponseVO extends BaseResponse {
    private GroupedUserRolesVO groupedUserRoles;
}
