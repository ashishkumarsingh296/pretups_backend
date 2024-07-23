package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardGroupCalculateC2STransferValueResponseVO  extends BaseResponse{
		private String newValidDate;
		private String receiverTransferValuesTax1;
		private String receiverTransferValuesTax2;
		private String receiverTransferValue;
		private String receiverTransferValuesProcessingValue;
		private int rowIndex;
		private ViewC2SCardGroupResponseVO viewC2SCardGroupResponseVO;
		private ArrayList bonusBundleList = null;
		private String cardGroupSubServiceName;
		private String cardGroupSubServiceID;
		private String serviceTypeId;
		private String serviceTypeDesc;
		private String setType;
		private String setTypeName;
		private String cardGroupSetName;
		private List validityLookupList;
		
		}


