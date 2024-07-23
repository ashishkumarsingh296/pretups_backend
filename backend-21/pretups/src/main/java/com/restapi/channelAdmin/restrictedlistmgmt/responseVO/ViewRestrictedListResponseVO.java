package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ViewRestrictedListResponseVO extends BaseResponse{
	private ArrayList<ViewRestrictedResponseVO> subRestrictedList;
}
