package com.btsl.pretups.iat.businesslogic;

/**
 * @(#)IATNWServiceCache.java
 *                            Copyright(c) 2009, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            Give the country master list for IAT
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            vikas.yadav July 06, 2009 Initital Creation
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class IATNWServiceCache implements Runnable{
    private static Log logger = LogFactory.getLog(IATNWServiceCache.class.getName());
    private static final String CLASS_NAME = "IATNWServiceCache";
    private static ArrayList _iatNetworkCountryList = new ArrayList();
    private static HashMap _iatNetworkServiceMap = new HashMap();
    private static HashMap _iatNetworkServiceMapWithIP = new HashMap();
    private static HashMap _iatNetworkServiceMapWithIATID = new HashMap();
@Override
    public void run() {
        try {
            Thread.sleep(50);
            loadIATNWServiceCache();
        } catch (Exception e) {
        	 logger.error("IATNWServiceCache init() Exception ", e);
        }
    }
    
    /**
     * @throws BTSLBaseException
     */
    public static void loadIATNWServiceCache() throws BTSLBaseException {
    	final String methodName = "loadIATNWServiceCache";
    	if (logger.isDebugEnabled()) {
    		logger.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        _iatNetworkCountryList = loadIATNetworkCountryMappingCache();// key
                                                                     // nw,country
        _iatNetworkServiceMap = loadIATNetworkServiceMappingCache();// key
                                                                    // country,network,service
                                                                    // type
        _iatNetworkServiceMapWithIP = loadIATNetworkServiceMappingCacheWithIP();// key
                                                                                // IP,
                                                                                // Port
        _iatNetworkServiceMapWithIATID = loadIATNetworkServiceMappingCacheWithIATID();// key
                                                                                      // interfaceid
        }
        catch(BTSLBaseException be) {
        	logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	logger.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	logger.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	logger.errorTrace(methodName, e);
        	new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (logger.isDebugEnabled()) {
        		logger.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To load the network country Mapping details
     * 
     * @return
     *         HashMap
     * @throws Exception 
     */
    private static ArrayList loadIATNetworkCountryMappingCache() throws BTSLBaseException {
    	final String methodName = "loadIATNetworkCountryMappingCache";
    	if (logger.isDebugEnabled()) {
    		logger.debug(methodName, PretupsI.ENTERED);
    	}
        IATDAO iatDAO = new IATDAO();
        ArrayList arr = new ArrayList();
        try {
            arr = iatDAO.loadIATNetworkCountryList();

        }
        catch(BTSLBaseException be) {
        	logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	logger.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	logger.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	logger.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (logger.isDebugEnabled()) {
        		logger.debug(methodName, PretupsI.EXITED);
        	}
        }
        return arr;
    }

    /**
     * To load the netwok service Mapping details
     * 
     * @return
     *         HashMap
     * @throws Exception 
     */
    private static HashMap loadIATNetworkServiceMappingCache() throws BTSLBaseException {
    	final String methodName = "loadIATNetworkServiceMappingCache";
    	if (logger.isDebugEnabled()) {
    		logger.debug(methodName, PretupsI.ENTERED);
    	}
        IATDAO iatDAO = new IATDAO();
        HashMap map = null;
        try {
            map = iatDAO.loadIATNetworkServiceMap();

        }
        catch(BTSLBaseException be) {
        	logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	logger.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	logger.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	logger.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (logger.isDebugEnabled()) {
        		logger.debug(methodName, PretupsI.EXITED);
        	}
        }
        return map;
    }

    /**
     * To load the netwok service Mapping details
     * 
     * @return
     *         HashMap
     * @throws Exception 
     */
    private static HashMap loadIATNetworkServiceMappingCacheWithIP() throws BTSLBaseException {
    	final String methodName = "loadIATNetworkServiceMappingCacheWithIP";
    	if (logger.isDebugEnabled()) {
    		logger.debug(methodName, PretupsI.ENTERED);
    	}
        IATDAO iatDAO = new IATDAO();
        HashMap map = null;
        try {
            map = iatDAO.loadIATNetworkServiceMapWithIP();

        }
        catch(BTSLBaseException be) {
        	logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	logger.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	logger.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	logger.errorTrace(methodName, e);
        	new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (logger.isDebugEnabled()) {
        		logger.debug(methodName, PretupsI.EXITED);
        	}
        }
        return map;
    }

    /**
     * To load the netwok service Mapping details
     * 
     * @return
     *         HashMap
     * @throws Exception 
     */
    private static HashMap loadIATNetworkServiceMappingCacheWithIATID() throws BTSLBaseException {
    	final String methodName = "loadIATNetworkServiceMappingCacheWithIATID";
    	if (logger.isDebugEnabled()) {
    		logger.debug(methodName, PretupsI.ENTERED);
    	}
        IATDAO iatDAO = new IATDAO();
        HashMap map = null;
        try {
            map = iatDAO.loadIATNetworkServiceMapWithIATID();

        }
        catch(BTSLBaseException be) {
        	logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	logger.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	logger.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	logger.errorTrace(methodName, e);
        	new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (logger.isDebugEnabled()) {
        		logger.debug(methodName, PretupsI.EXITED);
        	}
        }
        return map;
    }

    /**
     * IATNetworkCountryMappingVO
     * 
     * @param p_countryNetwork
     * @return iatNetworkCountryMappingVO
     */
    public static ArrayList getNetworkCountryArrObject() {

        if (logger.isDebugEnabled()) {
            logger.debug("getNetworkCountryVOObject()", "entered ");
        }
        return _iatNetworkCountryList;
    }

    /**
     * IATNetworkCountryMappingVO
     * 
     * @param p_countryNetwork
     * @return iatNetworkCountryMappingVO
     */
    public static IATNetworkServiceMappingVO getNetworkServiceIPObject(String p_ipPort) {
        if (logger.isDebugEnabled()) {
            logger.debug("getNetworkServiceIPObject()", "entered " + p_ipPort);
        }
        IATNetworkServiceMappingVO iatNetworkServiceMappingVO = null;
        iatNetworkServiceMappingVO = (IATNetworkServiceMappingVO) _iatNetworkServiceMapWithIP.get(p_ipPort);
        if (logger.isDebugEnabled()) {
            logger.debug("getNetworkServiceIPObject()", "exited " + iatNetworkServiceMappingVO);
        }
        return iatNetworkServiceMappingVO;
    }

    /**
     * IATNetworkCountryMappingVO
     * 
     * @param p_countryNetwork
     * @return iatNetworkCountryMappingVO
     */
    public static IATNetworkServiceMappingVO getNetworkServiceIATIDObject(String p_IATID) {
        if (logger.isDebugEnabled()) {
            logger.debug("getNetworkServiceIATIDObject()", "entered " + p_IATID);
        }
        IATNetworkServiceMappingVO iatNetworkServiceMappingVO = null;
        iatNetworkServiceMappingVO = (IATNetworkServiceMappingVO) _iatNetworkServiceMapWithIATID.get(p_IATID);
        if (logger.isDebugEnabled()) {
            logger.debug("getNetworkServiceIATIDObject()", "exited " + iatNetworkServiceMappingVO);
        }
        return iatNetworkServiceMappingVO;
    }

    /**
     * IATNetworkCountryMappingVO
     * 
     * @param p_countryNetwork
     * @return iatNetworkCountryMappingVO
     */
    public static IATNetworkServiceMappingVO getNetworkServiceObject(String p_countryNwService) {
        if (logger.isDebugEnabled()) {
            logger.debug("getNetworkServiceObject()", "entered " + p_countryNwService);
        }
        IATNetworkServiceMappingVO iatNetworkServiceMappingVO = null;
        iatNetworkServiceMappingVO = (IATNetworkServiceMappingVO) _iatNetworkServiceMap.get(p_countryNwService);
        if (logger.isDebugEnabled()) {
            logger.debug("getNetworkServiceObject()", "exited " + iatNetworkServiceMappingVO);
        }
        return iatNetworkServiceMappingVO;
    }

}
