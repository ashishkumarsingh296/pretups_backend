package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ICCIDMSISDNHistoryResponseVO extends BaseResponse{

	private List<ICCIDMSISDNHistoryDetailsVO> iccidList;
	private List<ICCIDMSISDNHistoryDetailsVO> iccidHistoryList;
	private List<ICCIDMSISDNHistoryDetailsVO> msisdnList;
	private List<ICCIDMSISDNHistoryDetailsVO> msisdnHistoryList;
}
