package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherPinResetDetailResponseVO extends BaseResponse {

	ArrayList transferVOList;
	ArrayList transferItemsVOList;

}
