package com.Features;

import java.util.HashMap;
import java.util.Map;

import com.utils._masterVO;

public class Map_TCPValues {


	public Map<String, String> DataMap_TCPCategoryLevel() {

		Map<String, String> dataMap = new HashMap<>();
		
		dataMap.put("MinResidualBalance1",(_masterVO.getProperty("MinResidualBalance1")));
		dataMap.put("MaximumResidualBalance1",(_masterVO.getProperty("MaximumResidualBalance1")));
		dataMap.put("MinimumBalance1",(_masterVO.getProperty("MinimumBalance1")));
		dataMap.put("MaximumBalance1",(_masterVO.getProperty("MaximumBalance1")));
		dataMap.put("AlertingBalance1",(_masterVO.getProperty("AlertingBalance1")));
		dataMap.put("AllowedMaxPercentage1",(_masterVO.getProperty("AllowedMaxPercentage1")));

		dataMap.put("MinResidualBalance",(_masterVO.getProperty("MinResidualBalance")));
		dataMap.put("MaximumResidualBalance",(_masterVO.getProperty("MaximumResidualBalance")));
		dataMap.put("MinimumBalance",(_masterVO.getProperty("MinimumBalance")));
		dataMap.put("MaximumBalance",(_masterVO.getProperty("MaximumBalance")));
		dataMap.put("AlertingBalance",(_masterVO.getProperty("AlertingBalance")));
		dataMap.put("AllowedMaxPercentage",(_masterVO.getProperty("AllowedMaxPercentage")));


		// Transfer Control Profile----Daily.
		
		dataMap.put("DailyInCount",(_masterVO.getProperty("DailyInCount")));
		dataMap.put("DailyInAlertingCount",(_masterVO.getProperty("DailyInAlertingCount")));
		dataMap.put("DailyInTransferValue",(_masterVO.getProperty("DailyInTransferValue")));
		dataMap.put("DailyInAlertingValue",(_masterVO.getProperty("DailyInAlertingValue")));
		dataMap.put("DailyOutCount",(_masterVO.getProperty("DailyOutCount")));
		dataMap.put("DailyOutAlertingCount",(_masterVO.getProperty("DailyOutAlertingCount")));
		dataMap.put("DailyOutTransferValue",(_masterVO.getProperty("DailyOutTransferValue")));
		dataMap.put("DailyOutAlertingValue",(_masterVO.getProperty("DailyOutAlertingValue")));
		dataMap.put("DailySubscriberInCount",(_masterVO.getProperty("DailySubscriberInCount")));
		dataMap.put("DailySubscriberInAlertingCount",(_masterVO.getProperty("DailySubscriberInAlertingCount")));
		dataMap.put("DailySubscriberTransferInValue",(_masterVO.getProperty("DailySubscriberTransferInValue")));
		dataMap.put("DailySubscriberTransferInAlertingValue",(_masterVO.getProperty("DailySubscriberTransferInAlertingValue")));
		dataMap.put("DailySubscriberTransferOutCount",(_masterVO.getProperty("DailySubscriberTransferOutCount")));
		dataMap.put("DailySubscriberTransferOutAlertingCount",(_masterVO.getProperty("DailySubscriberTransferOutAlertingCount")));
		dataMap.put("DailySubscriberTransferOutValue",(_masterVO.getProperty("DailySubscriberTransferOutValue")));
		dataMap.put("DailySubscriberTransferOutAlertingValue",(_masterVO.getProperty("DailySubscriberTransferOutAlertingValue")));
		
		// Transfer Control Profile----Weekly.
		
		dataMap.put("WeeklyInCount",(_masterVO.getProperty("WeeklyInCount")));
		dataMap.put("WeeklyInAlertingCount",(_masterVO.getProperty("WeeklyInAlertingCount")));
		dataMap.put("WeeklyInTransferValue",(_masterVO.getProperty("WeeklyInTransferValue")));
		dataMap.put("WeeklyInAlertingValue",(_masterVO.getProperty("WeeklyInAlertingValue")));
		dataMap.put("WeeklyOutCount",(_masterVO.getProperty("WeeklyOutCount")));
		dataMap.put("WeeklyOutAlertingCount",(_masterVO.getProperty("WeeklyOutAlertingCount")));
		dataMap.put("WeeklyOutTransferValue",(_masterVO.getProperty("WeeklyOutTransferValue")));
		dataMap.put("WeeklyOutAlertingValue",(_masterVO.getProperty("WeeklyOutAlertingValue")));
		dataMap.put("WeeklySubscriberInCount",(_masterVO.getProperty("WeeklySubscriberInCount")));
		dataMap.put("WeeklySubscriberInAlertingCount",(_masterVO.getProperty("WeeklySubscriberInAlertingCount")));
		dataMap.put("WeeklySubscriberTransferInValue",(_masterVO.getProperty("WeeklySubscriberTransferInValue")));
		dataMap.put("WeeklySubscriberTransferInAlertingValue",(_masterVO.getProperty("WeeklySubscriberTransferInAlertingValue")));
		dataMap.put("WeeklySubscriberTransferOutCount",(_masterVO.getProperty("WeeklySubscriberTransferOutCount")));
		dataMap.put("WeeklySubscriberTransferOutAlertingCount",(_masterVO.getProperty("WeeklySubscriberTransferOutAlertingCount")));
		dataMap.put("WeeklySubscriberTransferOutValue",(_masterVO.getProperty("WeeklySubscriberTransferOutValue")));
		dataMap.put("WeeklySubscriberTransferOutAlertingValue",(_masterVO.getProperty("WeeklySubscriberTransferOutAlertingValue")));

		// Transfer Control Profile----Monthly.
		
		dataMap.put("MonthlyInCount",(_masterVO.getProperty("MonthlyInCount")));
		dataMap.put("MonthlyInAlertingCount",(_masterVO.getProperty("MonthlyInAlertingCount")));
		dataMap.put("MonthlyInTransferValue",(_masterVO.getProperty("MonthlyInTransferValue")));
		dataMap.put("MonthlyInAlertingValue",(_masterVO.getProperty("MonthlyInAlertingValue")));
		dataMap.put("MonthlyOutCount",(_masterVO.getProperty("MonthlyOutCount")));
		dataMap.put("MonthlyOutAlertingCount",(_masterVO.getProperty("MonthlyOutAlertingCount")));
		dataMap.put("MonthlyOutTransferValue",(_masterVO.getProperty("MonthlyOutTransferValue")));
		dataMap.put("MonthlyOutAlertingValue",(_masterVO.getProperty("MonthlyOutAlertingValue")));
		dataMap.put("MonthlySubscriberInCount",(_masterVO.getProperty("MonthlySubscriberInCount")));
		dataMap.put("MonthlySubscriberInAlertingCount",(_masterVO.getProperty("MonthlySubscriberInAlertingCount")));
		dataMap.put("MonthlySubscriberTransferInValue",(_masterVO.getProperty("MonthlySubscriberTransferInValue")));
		dataMap.put("MonthlySubscriberTransferInAlertingValue",(_masterVO.getProperty("MonthlySubscriberTransferInAlertingValue")));
		dataMap.put("MonthlySubscriberTransferOutCount",(_masterVO.getProperty("MonthlySubscriberTransferOutCount")));
		dataMap.put("MonthlySubscriberTransferOutAlertingCount",(_masterVO.getProperty("MonthlySubscriberTransferOutAlertingCount")));
		dataMap.put("MonthlySubscriberTransferOutValue",(_masterVO.getProperty("MonthlySubscriberTransferOutValue")));
		dataMap.put("MonthlySubscriberTransferOutAlertingValue",(_masterVO.getProperty("MonthlySubscriberTransferOutAlertingValue")));


		return dataMap;

	}





}
