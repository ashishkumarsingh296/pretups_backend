package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ViewC2SCardGroupResponseVO extends BaseResponse {
	private String viewSlabCopy;
	private ArrayList validityTypeList;
	private String selectCardGroupSetId;
	private String selectCardGroupSetVersionId;
	private String cardGroupSetName;
	private String cardGroupSubServiceID;
	private String cardGroupSubServiceName;
	private String serviceTypeId;
	private String serviceTypedesc;
	private String setType;
	private String setTypeName;
	private String defaultCardGroupRequired;
	private String applicableFromDate;
	private String applicableFromHour;
    private String oldApplicableFromDate;
    private String oldApplicableFromHour;
    private String version;
    private ArrayList<CardGroupDetailsVO> cardGroupList;
    private Boolean deleteAllowed;
    
    private String CardGroupID;
    private String CardGroupCode;
    private String StartRange;
    private String EndRange;
    private String ValidityPeriodType;
    private String ValidityPeriod;
    private String GracePeriod;
    // theForm.setMultipleOf(String.valueOf(cardVO.getMultipleOf()));
    private String MultipleOf;
    private String receiverTax1Name;
    private String receiverTax1Type;
    private String receiverTax1Rate;
    private String receiverTax2Name;
    private String receiverTax2Type;
    private String receiverTax2Rate;
    private String receiverAccessFeeType;
    private String receiverAccessFeeRate;
    private String minReceiverAccessFee;
    private String maxReceiverAccessFee;
    // added for card group slab suspend/resume
    private String bonusValidityValue;
    private String online;
    private String both;
    private String receiverConvFactor;
    private String cardName;
    private String reversalPermitted;
    private Date reversalModifiedDate;

   private String cGStatus;
    private String reversalModifiedDateAsString;
    private ArrayList tempAccList;
    // added for cos by gaurav
    	private String cosRequired;
    	private String inPromo;
   

}
