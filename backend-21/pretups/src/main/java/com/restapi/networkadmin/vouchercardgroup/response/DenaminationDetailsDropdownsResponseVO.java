package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DenaminationDetailsDropdownsResponseVO extends BaseResponse{

	 private ArrayList<VoucherTypeVO> voucherTypeList;
	 private ArrayList<VomsCategoryVO> denominationList;
	 private ArrayList segmentList;
	 private ArrayList<VomsProductVO> denominationProfileList;
}
