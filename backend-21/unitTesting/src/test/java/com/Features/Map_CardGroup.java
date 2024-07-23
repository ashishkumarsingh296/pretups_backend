package com.Features;

import java.util.HashMap;
import java.util.Map;

import com.commons.PretupsI;
import com.utils._masterVO;

public class Map_CardGroup {


	public Map<String, String> DataMap_CardGroup() {

		Map<String, String> dataMap = new HashMap<>();

		dataMap.put("CardGroupCode",(_masterVO.getProperty("CardGroupCode")));
		dataMap.put("ValidityType",PretupsI.VLTYP_LOOKUP);
		dataMap.put("ValidityDays",(_masterVO.getProperty("ValidityDays")));
		dataMap.put("GracePeriod",(_masterVO.getProperty("GracePeriod")));
		dataMap.put("MultipleOf",(_masterVO.getProperty("MultipleOf")));
		dataMap.put("Tax1Type",(_masterVO.getProperty("Tax1Type")));
		dataMap.put("Tax1Rate",(_masterVO.getProperty("Tax1Rate")));
		dataMap.put("Tax2Type",(_masterVO.getProperty("Tax2Type")));
		dataMap.put("Tax2Rate",(_masterVO.getProperty("Tax2Rate")));
		dataMap.put("ProcessingFeeType",(_masterVO.getProperty("ProcessingFeeType")));
		dataMap.put("ProcessingFeeRate",(_masterVO.getProperty("ProcessingFeeRate")));
		dataMap.put("ProcessingFeeMinAmount",(_masterVO.getProperty("ProcessingFeeMinAmount")));
		dataMap.put("ProcessingFeeMaxAmount",(_masterVO.getProperty("ProcessingFeeMaxAmount")));
		dataMap.put("SenderConversionFactor",(_masterVO.getProperty("ReceiverConversionFactor")));
		dataMap.put("RecTax1Type",(_masterVO.getProperty("Tax1Type")));
		dataMap.put("RecTax1Rate",(_masterVO.getProperty("Tax1Rate")));
		dataMap.put("RecTax2Type",(_masterVO.getProperty("Tax2Type")));
		dataMap.put("RecTax2Rate",(_masterVO.getProperty("Tax2Rate")));
		dataMap.put("ReceiverProcessingFeeMinAmount",(_masterVO.getProperty("ProcessingFeeMinAmount")));
		dataMap.put("ReceiverProcessingFeeMaxAmount",(_masterVO.getProperty("ProcessingFeeMaxAmount")));
		dataMap.put("ReceiverConversionFactor",(_masterVO.getProperty("ReceiverConversionFactor")));
		dataMap.put("BonusType",(_masterVO.getProperty("BonusType")));
		dataMap.put("BonusValue",(_masterVO.getProperty("BonusValue")));
		dataMap.put("BonusValidity",(_masterVO.getProperty("BonusValidity")));
		dataMap.put("BonusConversionFactor",(_masterVO.getProperty("BonusConversionFactor")));
		dataMap.put("BonusValidityDays",(_masterVO.getProperty("BonusValidityDays")));
		

		return dataMap;

	}





}
