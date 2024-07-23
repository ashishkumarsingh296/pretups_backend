package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import java.util.ArrayList;

import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnBlackListSelectedSubscriberRequestVO extends UnBlackListAllSubscriberRequestVO{
	private ArrayList<RestrictedSubVO> confirmListForUnBlack;
}
