package com.btsl.pretups.common;

import java.util.List;
import java.util.Locale;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;

public class LocalUpdateCacheBL implements Runnable {

	private static Log _log = LogFactory.getLog(LocalUpdateCacheBL.class.getName());
	
	private List arrayList = null;
	private List arrayListF = null;
	private String cacheId = null;
	private Locale locale = null;
	private String serverName = null;
	private String instanceId = null;

	public LocalUpdateCacheBL(List _ar,List _arF,  String _cacheId, Locale _locale, String _serverName, String _instanceID) {
		this.arrayList = _ar;
		this.arrayListF = _arF;
		this.cacheId = _cacheId;
		this.locale = _locale;
		this.serverName = _serverName;
		this.instanceId = _instanceID;
	}

	public void run() {
		_log.info("LocalUpdateCacheBL", "LocalUpdateCacheBL run Start  ................... ");
		try {
			long sleepTime = Long.parseLong(Constants.getProperty("UPDATE_CACHE_THREAD_SLEEP_TIME"));
			Thread.sleep(sleepTime);
			
			String updateCacheErrMsg = (new UpdateCacheServlet()).updateCache(cacheId, locale.toString());
            if (updateCacheErrMsg != null) {
                arrayListF.add(serverName+":"+cacheId);
            }else{
            	arrayList.add(serverName+":"+cacheId);
            }
		} catch (Exception e) {
			_log.error("LocalUpdateCacheBL run() Exception ", e);
		}
		_log.info("LocalUpdateCacheBL", "LocalUpdateCacheBL run End ................... ");
	}
}
