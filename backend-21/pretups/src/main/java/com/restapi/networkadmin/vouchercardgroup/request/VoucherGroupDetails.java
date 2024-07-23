package com.restapi.networkadmin.vouchercardgroup.request;

import java.util.ArrayList;

import com.btsl.pretups.cardgroup.businesslogic.BonusAccountDetailsVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoucherGroupDetails {

	private String cardGroupName;
	private String cardGroupCode;
	private String voucherType;
	private String voucherTypeDesc;
	private String voucherSegment;
	private String voucherSegmentDesc;
	private String denomination;
	private String status;
	private String denominationProfile;
	private String profileDesc;
	private String tax1Name;
	private String tax1Type;
	private double tax1Rate;
	private String tax2Name;
	private String tax2Type;
	private double tax2Rate;
	private String processingFeeType;
	private double processingfee;
	private double minAmount;
	private double maxAmount;
	private String validityType;
	private String validityDays;
	private String gracePeriodDays;
	private ArrayList<BonusAccountDetailsVO> bonusBandleList;
	
}
