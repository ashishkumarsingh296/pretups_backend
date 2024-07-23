package com.commons;

import com.classes.BaseTest;
import com.utils.Log;
import com.utils._masterVO;

public class CacheController extends BaseTest{
	
	public static CacheI CacheI;
	
	public CacheI CacheController() {
		return CacheI;
	}
	
	public void getCacheDAO() {

	String cacheImplementationValue = _masterVO.getClientDetail("UPDATECACHE_VER");
	
	if (cacheImplementationValue.equals("0")) {
		CacheI = new Caches_701();
		Log.info("Old Cache DAO Loaded  :: UPDATECACHE_VER = 0");
	} else if (cacheImplementationValue.equals("1")) {
		CacheI = new Caches_702();
		Log.info("New Cache DAO Loaded :: UPDATECACHE_VER = 1");
	} else if (cacheImplementationValue.equals("2")) {
		CacheI = new Caches_702();
		Log.info("New Cache DAO Loaded :: UPDATECACHE_VER = 2");
	} else {
	    Log.error("Invalid Cache Property set: " + cacheImplementationValue);
	}
	}
	
	
}
