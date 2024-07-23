package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.loyaltymgmt.businesslogic.ActivationBonusLMSWebDAO;

public class LMSProfileCache extends LinkedHashMap implements Runnable {
    private static Log _log = LogFactory.getLog(LMSProfileCache.class.getName());
    private static HashMap<String, ArrayList<ProfileSetDetailsLMSVO>> _lmsProfileMap = new HashMap<String, ArrayList<ProfileSetDetailsLMSVO>>();
    private static final String CLASS_NAME = "LMSProfileCache";
    // private static HashMap<String,ArrayList> _commProfileDetailMap=new
    // HashMap<String,ArrayList>();
    private static int capacity = 0;
    
    public void run() {
    	_log.info("ConfigServlet", "ConfigServlet Start LMSProfileCache loading ...................... ");
        try {
            Thread.sleep(50);
            loadLMSProfilesStartup();
        } catch (Exception e) {
        	_log.error("LMSProfileCache init() Exception ", e);
        }
        _log.info("ConfigServlet", "ConfigServlet End lmsProfileCache loading......................... ");
    }
    static {
        final String methodName = "Static Block";

        final String sCapacity = Constants.getProperty("MAX_LMS_PROFILES");
        if (BTSLUtil.isNullString(sCapacity)) {
            capacity = 100;
        } else if (!BTSLUtil.isNullString(sCapacity)) {
            try {
                capacity = Integer.parseInt(sCapacity);
            } catch (NumberFormatException e) {
                _log.errorTrace(methodName, e);
                capacity = 100;
            }
        }

    }

    public LMSProfileCache(
                    int capacity) {
        super(capacity + 1, 1.1f, true);
        this.capacity = capacity;
    }
    public LMSProfileCache() {
    }

    private static LMSProfileCache lmsProfileCache = new LMSProfileCache(capacity);

    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > this.capacity;
    }

    public static void loadLMSProfilesAtStartup() throws BTSLBaseException {
    	final String methodName = "loadLMSProfilesAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        _lmsProfileMap = loadLMSProfilesStartup();

        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading LMS Profiles At Startup.",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    public static void loadLMSProfilesDetails(String setId, String profileVersion, long transferVal) {
        final String methodName = "loadLMSProfilesDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "entered");
        }
        try {
            final Set<String> keys = lmsProfileCache.keySet();
            for (final String key : keys) {
                final String[] mapKey = key.split("_");
                if (mapKey[0].equals(setId)) {
                    lmsProfileCache.remove(key);
                }
            }
            lmsProfileCache = (LMSProfileCache) loadLMSProfiles(setId, profileVersion, transferVal, lmsProfileCache);
            if (_log.isDebugEnabled()) {
                _log.debug("loadLMSProfilesDetails", "exited");
            }
        } catch (Exception e) {
            _log.error("loadMapping", "Exception e:" + e.getMessage());
            _log.errorTrace(methodName, e);
        }

    }

    public static Object getObject(String setID, Date date) {
        ProfileSetDetailsLMSVO closestVO = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("getObject", "Entered:setID=" + setID + "Date" + date);
            }

            ArrayList<ProfileSetDetailsLMSVO> lmsProfileList = new ArrayList<ProfileSetDetailsLMSVO>();

            final Date currenttimestamp = BTSLUtil.getSQLDateTimeFromUtilDate(date);
            lmsProfileList = _lmsProfileMap.get(setID);

            long min = 0;
            if (lmsProfileList == null || lmsProfileList.isEmpty()) {
                updateLmsProfileMapping();
                lmsProfileList = _lmsProfileMap.get(setID);

            }

            if (lmsProfileList != null && !lmsProfileList.isEmpty()) {
                for (final ProfileSetDetailsLMSVO lmsProfileVO : lmsProfileList) {
                    if (((min == 0) || (currenttimestamp.getTime() - lmsProfileVO.getApplicableFrom().getTime() < min)) && (currenttimestamp.compareTo(lmsProfileVO
                                    .getApplicableFrom()) >= 0)) {
                        min = (long) currenttimestamp.getTime() - lmsProfileVO.getApplicableFrom().getTime();
                        closestVO = lmsProfileVO;
                    }
                }
            }
        } catch (Exception e) {
            _log.errorTrace("getObject", e);
        }
        return closestVO;

    }

    public static Object getObject(String setID, String profileVersion, long transferVal) {
        final String methodName = "getObject";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:setID=" + setID + "profileVersion" + profileVersion);
        }
        ArrayList al = null;
        try {
            al = (ArrayList) lmsProfileCache.get(setID + "_" + profileVersion + "_" + transferVal);
        } catch (Exception e) {
            _log.error("loadMapping", "Exception e:" + e.getMessage());
            _log.errorTrace(methodName, e);
        }
        return al;
    }

    public static HashMap<String, ArrayList<ProfileSetDetailsLMSVO>> loadLMSProfilesStartup() throws BTSLBaseException {
    	final String methodName = "loadLMSProfilesStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}

        final ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
        HashMap<String, ArrayList<ProfileSetDetailsLMSVO>> map = null;

        try {
            map = activationBonusLMSWebDAO.loadLmsProfileCache();
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading LMS Profiles At Startup.",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
        return map;

    }

    public static Map loadLMSProfiles(String setId, String profileVersion, long transferVal, Map map) {
        if (_log.isDebugEnabled()) {
            _log.debug("loadLMSProfiles", "entered");
        }

        final ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();

        try {
            activationBonusLMSWebDAO.loadLmsProfileDetailsCache(setId, profileVersion, transferVal, map);
        } catch (BTSLBaseException e) {
            _log.errorTrace("loadLMSProfiles", e);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("loadLMSProfiles", "exited");
        }

        return map;
    }

    public static void updateLmsProfileMapping() throws BTSLBaseException {
    	final String methodName = "updateLmsProfileMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
            
        final HashMap<String, ArrayList<ProfileSetDetailsLMSVO>> currentMap = loadLMSProfilesStartup();

        _lmsProfileMap = currentMap;
    	}
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in updating LMS Profiles At Startup.",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + _lmsProfileMap.size());
        	}
        }
    }

}