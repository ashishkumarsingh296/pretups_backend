package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadSubscriberListForDeleteResponseVO extends BaseResponse{
	private ArrayList listForDelete;
}
