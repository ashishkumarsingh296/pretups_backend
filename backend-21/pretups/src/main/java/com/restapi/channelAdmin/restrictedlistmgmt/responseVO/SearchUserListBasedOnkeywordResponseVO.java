package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchUserListBasedOnkeywordResponseVO extends BaseResponse{
	private ArrayList userList;
	private int userListSize;

}
