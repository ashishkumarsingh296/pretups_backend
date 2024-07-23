package com.restapi.networkadmin.commissionprofile.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommissionProfileProductListResponseVO extends BaseResponse {
	private ArrayList productList;
}
