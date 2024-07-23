package com.restapi.superadmin.domainmanagement.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DomainManagementListResponseVO extends BaseResponse {
    private List displayList;
}
