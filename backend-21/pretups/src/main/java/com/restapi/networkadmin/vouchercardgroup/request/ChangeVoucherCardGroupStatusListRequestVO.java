package com.restapi.networkadmin.vouchercardgroup.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangeVoucherCardGroupStatusListRequestVO {
 private List<ChangeVoucherCardGroupStatusRequest> changeVoucherCardGroupStatusRequestList;
}
