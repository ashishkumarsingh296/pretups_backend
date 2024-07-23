package com.btsl.pretups.common;

import java.util.List;
import java.util.Locale;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;

public class UpdateRedisCache implements Runnable{

	private static Log _log = LogFactory.getLog(LocalUpdateCacheBL.class.getName());
	
	private List arrayList = null;
	private List arrayListF = null;
	private String cacheId = null;
	private Locale locale = null;

	public UpdateRedisCache(List _ar,List _arF,  String _cacheId, Locale _locale) {
		this.arrayList = _ar;
		this.arrayListF = _arF;
		this.cacheId = _cacheId;
		this.locale = _locale;
	}
	@Override
	public void run() {
		_log.info("UpdateRedisCache", "UpdateRedisCache run Start  ................... ");
		try {
			long sleepTime = Long.parseLong(Constants.getProperty("UPDATE_CACHE_THREAD_SLEEP_TIME"));
			Thread.sleep(sleepTime);
			
			String updateCacheErrMsg = (new UpdateRedisCacheServlet()).updateCache(cacheId, locale.toString());
            if (updateCacheErrMsg != null) {
                arrayListF.add(cacheId);
            }else{
            	arrayList.add(cacheId);
            }
		} catch (Exception e) {
			_log.error("UpdateRedisCache run() Exception ", e);
		}
		_log.info("UpdateRedisCache", "UpdateRedisCache run End ................... ");
	}

}
