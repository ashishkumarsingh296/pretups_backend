package com.Features.mapclasses;

import java.util.HashMap;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class VMSMap extends BaseTest {

	public HashMap<String, String> defaultMap() {
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		HashMap<String, String> paraMeterMap = new HashMap<>();
		int rowCountTransfer = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCountTransfer; i++) {
			String denominationName = ExcelUtility.getCellData(0, "DENOMINATION_NAME", i);
			String shortName = ExcelUtility.getCellData(0, "SHORT_NAME", i);
			String mrp1 = ExcelUtility.getCellData(0, "MRP", i);
			String payableAmount = ExcelUtility.getCellData(0, "PAYABLE_AMOUNT", i);
			String profileName = ExcelUtility.getCellData(0, "PROFILE_NAME", i);
			if (denominationName != null && shortName != null && mrp1 != null && payableAmount != null
					&& profileName != null) {
				paraMeterMap.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				paraMeterMap.put("type", ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
				paraMeterMap.put("service", ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i));
				paraMeterMap.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
				paraMeterMap.put("denominationName", ExcelUtility.getCellData(0, ExcelI.VOMS_DENOMINATION_NAME, i));
				paraMeterMap.put("shortName", ExcelUtility.getCellData(0, ExcelI.VOMS_SHORT_NAME, i));
				paraMeterMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				paraMeterMap.put("payableAmount", ExcelUtility.getCellData(0, ExcelI.VOMS_PAYABLE_AMOUNT, i));
				paraMeterMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
				// paraMeterMap.put("payableAmount", String.valueOf(10));
				paraMeterMap.put("description", "Automation Testing");
				paraMeterMap.put("minQuantity", "1");
				paraMeterMap.put("segment", "LC");
				paraMeterMap.put("maxQuantity", "60");
				paraMeterMap.put("talkTime", "5");
				paraMeterMap.put("validity", "80");
				paraMeterMap.put("threshold", "10");
				paraMeterMap.put("quantity", "10");
				paraMeterMap.put("expiryPeriod", "90");
				paraMeterMap.put("remarks", "test");
				String denomination = mrp + ".0";
				paraMeterMap.put("denomination", denomination);
				String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
				String productID = activeProfile + "(" + denomination + ")";
				paraMeterMap.put("productID", productID);
				paraMeterMap.put("batchType", "printing");
				paraMeterMap.put("viewBatchFor", "N");
				paraMeterMap.put("voucherStatus", PretupsI.WAREHOUSE);
				String product = DBHandler.AccessHandler.fetchProductID(paraMeterMap.get("activeProfile"));
				String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(product);
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(product, "EN");
				String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(product, "EN");
				long fromSrNo = 0;
				long toSrNo = 0;
				long vouchers = 0;
				if (fromSerialNumber != null)
					fromSrNo = Long.parseLong(fromSerialNumber);
				if (toSerialNumber != null)
					toSrNo = Long.parseLong(toSerialNumber);
				if (fromSerialNumber != null && toSerialNumber != null)
					vouchers = (toSrNo - fromSrNo) + 1;
				String numberOfVouchers = Long.toString(vouchers);
				paraMeterMap.put("fromSerialNumber", fromSerialNumber);
				paraMeterMap.put("toSerialNumber", toSerialNumber);
				paraMeterMap.put("numberOfVouchers", numberOfVouchers);
				break;
			}
		}

		return paraMeterMap;
	}

	public HashMap<String, String> defaultMapWithVoucherType(HashMap<String, String> initiateMap, String mrp2) {
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCountTransfer = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCountTransfer; i++) {
				String mrp1 = ExcelUtility.getCellData(0, "MRP", i);
				if (mrp1.equals(mrp2)) {
				initiateMap.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				break;
			}
				else {
					String categoryID = DBHandler.AccessHandler.getCategoryIDFromVOMSProduct(initiateMap.get("activeProfile"));
					String voucher_type = DBHandler.AccessHandler.getProducTypeFromVOMSCategory(categoryID);
					String type = DBHandler.AccessHandler.getType(voucher_type);
					initiateMap.put("voucherType", voucher_type);
					initiateMap.put("type", type);
					
				}
		}
		return initiateMap;
	}
}
