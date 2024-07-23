package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoucherTransferValueResponseVO extends BaseResponse{
	
	private String newValidDate;
	private String receiverTransferValuesTax1;
	private String receiverTransferValuesTax2;
	private String receiverTransferValue;
	private String receiverTransferValuesProcessingValue;
	private int rowIndex;
	private ViewVoucherCardGroupResponseVO viewVoucherCardGroupResponseVO;
	private ArrayList bonusBundleList = null;
	private String cardGroupSubServiceName;
	private String cardGroupSubServiceID;
	private String serviceTypeId;
	private String serviceTypeDesc;
	private String setType;
	private String setTypeName;
	private String cardGroupSetName;
	private String profileId;
	private Double denomination;
	
}
