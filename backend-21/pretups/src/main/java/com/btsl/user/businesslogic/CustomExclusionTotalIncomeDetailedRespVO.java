package com.btsl.user.businesslogic;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class CustomExclusionTotalIncomeDetailedRespVO implements ExclusionStrategy {

    public boolean shouldSkipField(FieldAttributes f) {
    	boolean shouldSkipField = false;
    	if(f.getDeclaringClass() == TotalDailyUserIncomeResponseVO.class && f.getName().equals("cbc") && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_BASE_COMMISSION))).booleanValue()) {
    		shouldSkipField = true ;
    	} else if(f.getDeclaringClass() == TotalDailyUserIncomeResponseVO.class && f.getName().equals("cac") && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_COMMISSION))).booleanValue()) {
    		shouldSkipField =  true;
    	}
    	return (f.getDeclaringClass() == TotalDailyUserIncomeResponseVO.class && shouldSkipField);
    }
 
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
