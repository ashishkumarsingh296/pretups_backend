package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadSubscriberListForUnBlackResponseVO  extends BaseResponse{
	private ArrayList listForUnBlack;
	private ArrayList errorList;
}
