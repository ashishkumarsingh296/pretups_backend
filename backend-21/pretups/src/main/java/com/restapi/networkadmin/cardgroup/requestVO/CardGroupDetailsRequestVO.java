package com.restapi.networkadmin.cardgroup.requestVO;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.cardgroup.businesslogic.BonusAccountDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CardGroupDetailsRequestVO {
	
		private String cardGroupSetID;
	    private String version;
	    private String cardGroupID;
	    private String cardGroupCode;
	    private long startRange;
	    private long endRange;
	    private String validityPeriodType;
	    private int validityPeriod;
	    private long gracePeriod;
	    private String receiverTax1Name;
	    private String receiverTax1Type;
	    private double receiverTax1Rate;
	    private String receiverTax2Name;
	    private String receiverTax2Type;
	    private double receiverTax2Rate;
	    private String receiverAccessFeeType;
	    private double receiverAccessFeeRate;
	    private long minReceiverAccessFee;
	    private long maxReceiverAccessFee;
	    private long multipleOf;
	    private String cardGroupSetName;
	    private String cardGroupSubServiceIdDesc;
	    private String serviceTypeId;
	    private String serviceTypeDesc;
	    private String setType;
	    private String setTypeName;
	    private String status;
	    private int rowIndex = 0;
	    private long bonusValidityValue;
	    private String online;
	    private String both;
	    private String networkCode;
	    private String LastVersion;
	    private String receiverConvFactor = null;
	    private ArrayList bonusAccList = null;
	    private ArrayList<BonusAccountDetailsVO> tempAccList;
	    private int locationIndex;
		private long bonusTalktimevalidity;
	    private String cosRequired;
	    private double inPromo;
	    private String reversalPermitted;
	    private String cardName;
	    private Date reversalModifiedDate;
	    private String reversalModifiedDateAsString;
	    private String maxReceiverAccessFeeAsString;
	    private String startRangeAsString;
	    private String validityPeriodAsString;
	    private String endRangeAsString;
	    private String minReceiverAccessFeeAsString;
	    private String multipleOfAsString;
	    private String inPromoAsString;
	    private String receiverAccessFeeRateAsString;
	    private String receiverTax1RateAsString;
	    private String receiverTax2RateAsString;
	    private String validityPeriodTypeDesc;
	    private String cardGroupSubServiceID;
	    private ArrayList<CardGroupDetailsVO> cardGroupList;
	    private String editDetail;
	    private String request;
	    
	    //private static  float EPSILON;//=0.0000001f;
	    private String cardGroupType;
	   
	    

		@Override
		public String toString() {
			final StringBuilder sbd = new StringBuilder();
			sbd.append("CardGroupDetailsRequestVO [cardGroupSetID=");
			sbd.append(cardGroupSetID);
			sbd.append(", version=");
			sbd.append(version);
			sbd.append( ", cardGroupID=");
			sbd.append(cardGroupID);
			sbd.append(", cardGroupCode=");
			sbd.append(cardGroupCode);
			sbd.append(", startRange=");
			sbd.append(startRange);
			sbd.append( ", endRange=");
			sbd.append(endRange);
			sbd.append(", validityPeriodType=");
			sbd.append(validityPeriodType);
			sbd.append( ", validityPeriod=");
			sbd.append(validityPeriod);
			sbd.append(", gracePeriod=");
			sbd.append(gracePeriod);
			sbd.append(", receiverTax1Name=");
			sbd.append(receiverTax1Name);
			sbd.append(", receiverTax1Type=");
			sbd.append(receiverTax1Type);
			sbd.append(", receiverTax1Rate=");
			sbd.append(receiverTax1Rate);
			sbd.append(", receiverTax2Name=");
			sbd.append(receiverTax2Name);
			sbd.append(", receiverTax2Type=");
			sbd.append(receiverTax2Type);
			sbd.append(", receiverTax2Rate=");
			sbd.append(receiverTax2Rate);
			sbd.append(", receiverAccessFeeType=");
			sbd.append(receiverAccessFeeType);
			sbd.append(", receiverAccessFeeRate=");
			sbd.append(receiverAccessFeeRate);
			sbd.append(", minReceiverAccessFee=");
			sbd.append(minReceiverAccessFee);
			sbd.append(", maxReceiverAccessFee=");
			sbd.append(maxReceiverAccessFee);
			sbd.append(", multipleOf=");
			sbd.append( multipleOf);
			sbd.append(", cardGroupSetName=");
			sbd.append(cardGroupSetName);
			sbd.append(", cardGroupSubServiceIdDesc=");
			sbd.append(cardGroupSubServiceIdDesc);
			sbd.append(", serviceTypeId=");
			sbd.append(serviceTypeId);
			sbd.append(", serviceTypeDesc=");
			sbd.append(serviceTypeDesc);
			sbd.append(", setType=");
			sbd.append(setType);
			sbd.append(", setTypeName=");
			sbd.append(setTypeName);
			sbd.append(", status=");
			sbd.append(status);
			sbd.append(", rowIndex=");
			sbd.append(rowIndex);
			sbd.append(", bonusValidityValue=");
			sbd.append( bonusValidityValue);
			sbd.append(", online=");
			sbd.append(online);
			sbd.append(", both=");
			sbd.append(both);
			sbd.append(", networkCode=");
			sbd.append(networkCode);
			sbd.append(", LastVersion=");
			sbd.append(LastVersion);
			sbd.append(", receiverConvFactor=");
			sbd.append(receiverConvFactor);
			sbd.append(", bonusAccList=");
			sbd.append(bonusAccList);
			sbd.append(", tempAccList=");
			sbd.append(tempAccList);
			sbd.append(", locationIndex=");
			sbd.append(locationIndex);
			sbd.append(", bonusTalktimevalidity=");
			sbd.append(bonusTalktimevalidity);
			sbd.append(", cosRequired=" );
			sbd.append(cosRequired);
			sbd.append(", inPromo=");
			sbd.append(inPromo);
			sbd.append(", reversalPermitted=");
			sbd.append(reversalPermitted);
			sbd.append(", cardName=");
			sbd.append(cardName);
			sbd.append(", reversalModifiedDate=");
			sbd.append(reversalModifiedDate);
			sbd.append(", reversalModifiedDateAsString=");
			sbd.append(reversalModifiedDateAsString);
			sbd.append(", maxReceiverAccessFeeAsString=" );
			sbd.append(maxReceiverAccessFeeAsString);
			sbd.append(", startRangeAsString=");
			sbd.append(startRangeAsString);
			sbd.append(", validityPeriodAsString=");
			sbd.append(validityPeriodAsString);
			sbd.append(", endRangeAsString=");
			sbd.append(endRangeAsString);
			sbd.append(", minReceiverAccessFeeAsString=");
			sbd.append(minReceiverAccessFeeAsString);
			sbd.append(", multipleOfAsString=");
			sbd.append(multipleOfAsString);
			sbd.append(", inPromoAsString=");
			sbd.append(inPromoAsString);
			sbd.append(", receiverAccessFeeRateAsString=");
			sbd.append(receiverAccessFeeRateAsString);
			
			sbd.append(", receiverTax1RateAsString=");
			sbd.append(receiverTax1RateAsString);
			sbd.append(", receiverTax2RateAsString=" );
			sbd.append(receiverTax2RateAsString);
			sbd.append(", validityPeriodTypeDesc=" );
			sbd.append(validityPeriodTypeDesc);
			sbd.append(", cardGroupSubServiceID=");
			sbd.append(cardGroupSubServiceID);
			sbd.append(", cardGroupList=");
			sbd.append(cardGroupList);
			sbd.append(", editDetail=");
			sbd.append(editDetail);
			sbd.append(", cardGroupType=");
			sbd.append(cardGroupType);
			sbd.append( "]" );

		
			return sbd.toString();
		}
	    		
			    

	    
		

}
