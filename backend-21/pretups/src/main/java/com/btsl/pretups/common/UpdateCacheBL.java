package com.btsl.pretups.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class UpdateCacheBL implements Runnable {

	private static Log _log = LogFactory.getLog(UpdateCacheBL.class.getName());
	private String cacheId = null;
	private String urlString = null;
	private List<String> arrayList = null;
	private List<String> arrayListF = null;
	private InstanceLoadVO instanceLoadVO = null;
	

	public UpdateCacheBL(List _ar, List _arF, String _urlString, InstanceLoadVO _instanceLoadVO, String _cacheId) {
		this.urlString = _urlString;
		this.arrayList = _ar;
		this.arrayListF = _arF;
		this.instanceLoadVO = _instanceLoadVO;
		this.cacheId = _cacheId;
	}

	public void run() {
		_log.info("UpdateCacheBL", "UpdateCacheBL run Start  ................... ");
		try {
			long sleepTime = Long.parseLong(Constants.getProperty("UPDATE_CACHE_THREAD_SLEEP_TIME"));
			Thread.sleep(sleepTime);
			updateCache();
		} catch (Exception e) {
			_log.error("UpdateCacheBL run() Exception ", e);
		}
		_log.info("UpdateCacheBL", "UpdateCacheBL run End ................... ");
	}

	public void updateCache() {
		final String methodName = "updateCache";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		InputStreamReader inputStreamReader = null;
		BufferedReader br = null;
		HttpURLConnection con = null;
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "URL STING= " + urlString);
			}
			URLConnection uc = null;
			URL url = new URL(urlString.toString());
			Boolean httpsEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE);
			if (httpsEnable) {
				con = BTSLUtil.getConnection(url);
			} else {
				uc = url.openConnection();
				con = (HttpURLConnection) uc;
			}
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			String encoding = BTSLUtil.encode((instanceLoadVO.getInstanceID() + ":" + instanceLoadVO.getAuthPass()).getBytes());
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "encoding  :: " + encoding);
			}
			con.setRequestProperty("Authorization", "Basic " + encoding);
			inputStreamReader = new InputStreamReader(con.getInputStream());
			br = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer message = new StringBuffer();
			while ((str = br.readLine()) != null) {
				message.append(str);
			}
			String messageStr = message.toString();
			messageStr = messageStr.replaceAll("XXXXXXX", cacheId + " of [" + instanceLoadVO.getInstanceID() + "] " + instanceLoadVO.getInstanceName() + " ");
			arrayList.add("[" + instanceLoadVO.getInstanceID() + "] " + instanceLoadVO.getInstanceName() + ":"+cacheId);
		} catch (Exception e) {
			arrayListF.add("[" + instanceLoadVO.getInstanceID() + "] " + instanceLoadVO.getInstanceName() + ":"+cacheId);
			_log.error(methodName, " Fail to connect the instance " + urlString.toString());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UpdateCacheServlet[doPost]", "", "", "", "Exception:" + e.getMessage());
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					_log.errorTrace(methodName, e);
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					_log.errorTrace(methodName, e);
				}
			}
			if (con != null) {
				con.disconnect();
			}
		}
	}

}
