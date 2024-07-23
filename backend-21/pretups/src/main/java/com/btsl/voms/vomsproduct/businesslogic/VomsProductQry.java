package com.btsl.voms.vomsproduct.businesslogic;

public interface VomsProductQry {
	public String loadProductDetailsListQry(String pStatusStr, boolean pUseALL,String pType);
	public String loadProductDetailsListQuery(String pStatusStr,boolean pUseALL, String pType, String networkCode,String voucher_segment);
	public String loadActiveProductDetailsListQry();
	public String loadProductsByProductionLocationQry(String psubCategory);
	public String loadProductsListQry(String psubCategory);
	public String loadProductsListForPhysicalQry(String vType);
	public String getProductIDQry();
	public String getProductIDQuery(String profileName);
	public String loadProductsListbyCategoryQry();
	public String addUserVoucherTypeListQry(String p_userId, String[] p_voucherType, String p_status);
	public String addUserVoucherSegmentListQry(String p_userId, String[] p_voucherSegment, String p_status);
	public String loadVoucherTypeListQry();
	public String loadUserVoucherTypeListQry(String p_userId);
	public String loadUserVoucherSegmentListQry();
	public String loadUserVoucherTypeListForVoucherGenerationQry(String p_userId);
	public String loadActiveProductDetailsListForUserQry(String pNetworkCode, String pStatus,String p_userId);
	public String loadProductListForActiveProductForUserQry(String pStatus,String pType,String pUserid,VomsActiveProductVO pVomsActiveProductVO );
	public String loadProductsListForUserQry(String userId);
	public String loadProductDetailsListForMrpQuery(String pStatusStr,boolean pUseALL, String pType, String networkCode,String voucher_segment,String mrp);
	public String loadProductDetailsListForMrpQuery(String pStatusStr,boolean pUseALL, String networkCode);
}
