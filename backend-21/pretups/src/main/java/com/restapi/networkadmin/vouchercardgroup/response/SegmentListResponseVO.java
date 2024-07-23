package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SegmentListResponseVO extends BaseResponse{
	private ArrayList segmentList;
}
