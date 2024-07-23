package com.restapi.preferences.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SystemPreferencesResponseVO extends BaseResponse{

	ArrayList<PreferenceCacheVO> preferenceList;
	
}
