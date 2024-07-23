package com.web.voms.vomscategory.businesslogic;


public interface VomsCategoryWebQry{
	public String loadSubCategoryListForView(boolean isSubCategory, String segment);
	public String loadUserCategoryList(String p_userid,String[] vouchersType);
	public String loadCategoryListForUser(String p_status, String p_userId);
	
	}
