/*
 * Created on Aug 28, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsActiveProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsMasterVO;
import com.ibm.icu.util.Calendar;

/**
 * @author vikas.yadav
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class VomsUtil implements VomsUtilI {

    private static Log _log = LogFactory.getLog(VomsUtil.class.getName());
    protected static String DB_CONN_FAILED = "00000";

    public static Connection getSingleConnection(String datasourceurl, String userid, String passwd) throws BTSLBaseException {
        final String METHOD_NAME = "getSingleConnection";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(DB_CONN_FAILED);
        }
        Connection conn = null;
        try {
            String db_url = datasourceurl;
            String db_user = userid;
            if (db_user != null) {
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            }
            String db_password = passwd;
            if (db_password != null) {
                db_password = BTSLUtil.decrypt3DesAesText(db_password);
            }
            conn = DriverManager.getConnection(db_url, db_user, db_password);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(DB_CONN_FAILED);
        }
        return conn;
    }

    /**
     * Method formatVomsBatchID.
     * 
     * @param p_vomsBatchVO
     *            VomsBatchVO
     * @param p_batchNumber
     *            String
     * @return String
     */
    private int VOMS_BATCH_ID_PAD_LENGTH = 4;

    public String formatVomsBatchID(VomsBatchVO p_vomsBatchVO, String p_batchNumber) {
        final String METHOD_NAME = "formatVomsBatchID";
        String returnStr = null;
        boolean directVoucherEnable = false;
        try {
            // modified for unique constraint violated in case of direct voucher
            // enable (manisha 20/02/08)
            directVoucherEnable = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DIRECT_VOUCHER_ENABLE))).booleanValue();
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(p_batchNumber, VOMS_BATCH_ID_PAD_LENGTH);
            if (VOMSI.BATCH_INTIATED.equals(p_vomsBatchVO.getBatchType())) {
                returnStr = "I" + BTSLDateUtil.getSystemLocaleDate(p_vomsBatchVO.getCreatedOn(), false) + "." + paddedTransferIDStr;
            } else if (directVoucherEnable) {
                returnStr = "D" + BTSLDateUtil.getSystemLocaleDate(p_vomsBatchVO.getCreatedOn(), false) + "." + paddedTransferIDStr;
            } else {
                returnStr = "V" + BTSLDateUtil.getSystemLocaleDate(p_vomsBatchVO.getCreatedOn(), false) + "." + paddedTransferIDStr;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "formatVomsBatchID[]", "", "", "", "Not able to generate Voms batch ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method currentDateTimeFormatString.
     * 
     * @param p_date
     *            Date
     * @return String
     * @throws ParseException
     */
    public String currentDateTimeFormatString(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateString = sdf.format(p_date);
        return dateString;
    }

    /**
     * Method currentTimeFormatString.
     * 
     * @param p_date
     *            Date
     * @return String
     * @throws ParseException
     */
    public String currentTimeFormatString(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        String dateString = sdf.format(p_date);
        return dateString;
    }

    public String formatVomsBatchTransferID(Date p_date, String p_batchNumber, String p_prefix) {
        final String METHOD_NAME = "formatVomsBatchTransferID";
        String returnStr = null;
        boolean directVoucherEnable = false;
        try {
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(p_batchNumber, VOMS_BATCH_ID_PAD_LENGTH);
            returnStr = p_prefix + currentDateTimeFormatString(p_date) + "." + paddedTransferIDStr;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "formatVomsBatchID[]", "", "", "", "Not able to generate Voms batch ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    public String formatVomsSerialnum(long p_counter, String p_activeproductid) {
        String returnStr = null;
        int maxserialnumlength = 12;// by default
        int year = 0;
        String strYear;
        final String METHOD_NAME = "formatVomsSerialnum";
        try {
            maxserialnumlength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            if (maxserialnumlength == 0) {
                maxserialnumlength = 12;
            }
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toString(p_counter), maxserialnumlength - 4);
            p_activeproductid = BTSLUtil.padZeroesToLeft(p_activeproductid, 3);
            Calendar currDate = BTSLDateUtil.getInstance();
            year = currDate.get(Calendar.YEAR);
            strYear = String.valueOf(year);
            strYear = strYear.substring(3, 4);
            returnStr = strYear + p_activeproductid + paddedTransferIDStr;// 2+013+00000000001
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "formatVomsSerialnum[]", "", "", "", "Not able to generate Voms Serial num:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    public int generateFile(String p_filename, String p_filepath, ArrayList p_voucherList) {
        int i = 0;

        return i;
    }

    /**
     * @param p_keystring
     * @return
     * @throws Exception
     * @author rahul.dutt
     *         this method is used to generate key for encryption and decryption
     *         of voms vouchers
     */
    public String genEncDecKey(String p_keystring) throws Exception {
        return new com.btsl.util.CryptoUtil().encrypt(p_keystring, Constants.KEY);
    }

    public String getDecKey(String p_keystring) throws Exception {
        return new com.btsl.util.CryptoUtil().decrypt(p_keystring, Constants.KEY);
    }
    
    public String formatVomsBatchID(VomsMasterVO vomsMasterBatchVO, String p_batchNumber) {
        final String METHOD_NAME = "formatVomsBatchID";
        String returnStr = null;
        boolean directVoucherEnable = false;
        try {
            // modified for unique constraint violated in case of direct voucher
            // enable (manisha 20/02/08)
            directVoucherEnable = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DIRECT_VOUCHER_ENABLE))).booleanValue();
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(p_batchNumber, VOMS_BATCH_ID_PAD_LENGTH);
            /*if (VOMSI.BATCH_INTIATED.equals(rightelMasterBatchVO.getBatchType())) {
                returnStr = "I" + BTSLDateUtil.getSystemLocaleDate(rightelMasterBatchVO.getCreatedOn(), false) + "." + paddedTransferIDStr;
            } else */if (directVoucherEnable) {
                returnStr = "D" + BTSLDateUtil.getSystemLocaleDate(vomsMasterBatchVO.getCreatedOn(), false) + "." + paddedTransferIDStr;
            } else {
                returnStr = "V" + BTSLDateUtil.getSystemLocaleDate(vomsMasterBatchVO.getCreatedOn(), false) + "." + paddedTransferIDStr;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "formatVomsBatchID[]", "", "", "", "Not able to generate Voms batch ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }
    
    public static String decryptText(String p_text) {
        final String METHOD_NAME = "decryptText";
        try {
            if ("AES".equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMSPIN_EN_DE_CRYPTION_TYPE)))) {
                return new AESEncryptionUtil().DecryptAES(p_text);
            } else if ("DES".equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMSPIN_EN_DE_CRYPTION_TYPE)))) {
                return new CryptoUtil().decrypt(p_text, Constants.KEY);
            } else {
                return null;
            }
        } catch (Exception e) {
            _log.error("decryptText", "Exception e=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            return null;
        }
    }// end method

    
    public static String encryptText(String p_text) {
        final String METHOD_NAME = "encryptText";
        try {
            if ("AES".equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMSPIN_EN_DE_CRYPTION_TYPE)))) {
                return new AESEncryptionUtil().EncryptAES(p_text);
            } else if ("DES".equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMSPIN_EN_DE_CRYPTION_TYPE)))) {
                return new CryptoUtil().encrypt(p_text, Constants.KEY);
            } else {
                return null;
            }
        } catch (Exception e) {
            _log.error("encryptText", "Exception e=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            return null;
        }
    }
    
    /**
     * This function will populate the all possible Change Status combination for all Voucher type
     * @param voucherType
     * @return
     */
    public static HashMap<String, String> populateChangeStatusMap(String voucherType) {
        final String methodName = "populateChangeStatusMap";
        HashMap<String, String> changeStatusMap;
        try {
        	changeStatusMap = new HashMap<String, String>();
        	populateMap(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_D_STATUS_CHANGE)), VOMSI.VOUCHER_TYPE_DIGITAL, changeStatusMap);
        	
        	populateMap(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_E_STATUS_CHANGE)), VOMSI.VOUCHER_TYPE_ELECTRONIC, changeStatusMap);
        	
        	populateMap(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_P_STATUS_CHANGE)), VOMSI.VOUCHER_TYPE_PHYSICAL, changeStatusMap);
        } catch (Exception e) {
            _log.error(methodName, "Exception e=" + e.getMessage());
            _log.errorTrace(methodName, e);
            return null;
        }
        return changeStatusMap;
    }
    
    /**
     * This function populates changeStatusMap by iterating SYSTEM_PREFERENCES(allStatus)
     * @param allStatus
     * @param type
     * @param changeStatusMap
     */
    private static void populateMap(String allStatus, String type, HashMap<String, String> changeStatusMap) {
        final String methodName = "populateMap";
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Entering allStatus =  " + allStatus + " type = " + type);
        }
        String[] changeStatusList, fromToStatus;
        StringBuilder changeStatusKey;
        String key;
        if(!BTSLUtil.isNullString(allStatus)) {
    		changeStatusList = allStatus.split(PretupsI.COMMA);
    		for(int i=0; i<changeStatusList.length; i++) {
    			fromToStatus = changeStatusList[i].split(PretupsI.COLON);
    			changeStatusKey = new StringBuilder(type);
    			changeStatusKey.append(PretupsI.UNDERSCORE).append(fromToStatus[0]).append(PretupsI.UNDERSCORE).append(fromToStatus[1]);
    			key = changeStatusKey.toString();
    			changeStatusMap.put(key, key);
        	}
    	}
    }
    
    public static ArrayList<ListValueVO> filterChangeStatusList(String type, ArrayList<ListValueVO> statusList, String curStatus) {
        final String methodName = "filterChangeStatusList";
        ListValueVO listVO;
        String[] allStatus;
        ArrayList<ListValueVO> newStatusList = null;
        try {
        	newStatusList = new ArrayList<ListValueVO>();
        	if(BTSLUtil.isNullString(curStatus))
        		allStatus = getAllStatus(type);
        	else
        		allStatus = getStatusBasedOnVoucherType(type, curStatus);
        	if(!BTSLUtil.isNullArray(allStatus)) {
	        	for (int i = 0, j = statusList.size(); i < j; i++) {
	                listVO = statusList.get(i);
	                for (String element : allStatus) {
	                    if (element.equals(listVO.getValue())) {
	                    	newStatusList.add(listVO);
	                    }
	                }
	            }
        	}
        } catch (Exception e) {
            _log.error(methodName, "Exception e=" + e.getMessage());
            _log.errorTrace(methodName, e);
            return null;
        }
        if (_log.isDebugEnabled()) {
        	int size = BTSLUtil.isNullOrEmptyList(newStatusList) ? 0 : newStatusList.size();
        	_log.debug(methodName, "Exiting newStatusList size =  " + size);
        }
        return newStatusList;
    }
    
    public static String[] getAllStatus(String type) {
        final String methodName = "getAllStatus";
        String allStatus = null;
        try {
        	if(VOMSI.VOUCHER_TYPE_DIGITAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_DIGITAL.equals(type)) {
        		allStatus = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_D_STATUS_CHANGE));
        	} else if(VOMSI.VOUCHER_TYPE_ELECTRONIC.equals(type) || VOMSI.VOUCHER_TYPE_TEST_ELECTRONIC.equals(type)) {
        		allStatus = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_E_STATUS_CHANGE));
        	} else if(VOMSI.VOUCHER_TYPE_PHYSICAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_PHYSICAL.equals(type)) {
        		allStatus = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_P_STATUS_CHANGE));
        	}
        	if(!BTSLUtil.isNullString(allStatus)) {
        		return allStatus.split(PretupsI.COMMA);
        	}
        } catch (Exception e) {
            _log.error(methodName, "Exception e=" + e.getMessage());
            _log.errorTrace(methodName, e);
        }
        return null;
    }
    
    /**
     * This function will check whether the type passed in function is Test Voucher Type
     * i.e. DT, ET, PT
     * @param type
     * @return
     */
    public static boolean isTestVoucherType(String type) {
        final String methodName = "isTestVoucherType";
        boolean isTestVoucherType = VOMSI.VOUCHER_TYPE_TEST_DIGITAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_ELECTRONIC.equals(type)
        		|| VOMSI.VOUCHER_TYPE_TEST_PHYSICAL.equals(type);
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "isTestVoucherType =  " + isTestVoucherType);
        }
        return isTestVoucherType;
    }
    
    /**
     * This function will check whether the type passed in function is Test Voucher Type
     * i.e., D, E, P
     * @param type
     * @return
     */
    public static boolean isNonTestVoucherType(String type) {
        final String methodName = "isNonTestVoucherType";
        boolean isNonTestVoucherType = VOMSI.VOUCHER_TYPE_DIGITAL.equals(type) || VOMSI.VOUCHER_TYPE_ELECTRONIC.equals(type)
        		|| VOMSI.VOUCHER_TYPE_PHYSICAL.equals(type);
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "isNonTestVoucherType =  " + isNonTestVoucherType);
        }
        return isNonTestVoucherType;
    }
    
    /**
     * This function will return only allowed Voucher Type
     * @param allowedVoucherType
     * @param voucherTypeList
     * @return
     */
    public static ArrayList<VomsCategoryVO> getAllowedVoucherType(String[] allowedVoucherType, ArrayList voucherTypeList) {
        final String methodName = "getAllowedVoucherType";
        ArrayList<VomsCategoryVO> newVoucherTypeList = new ArrayList<VomsCategoryVO>();
        VomsCategoryVO vomsCategoryVO;
        /*
        String[] dep = {"D", "E", "P"};
        String[] de = {"D", "E"};
        String[] dp = {"D", "P"};
        String[] d = {"D"};
        String[] p = {"P"};
        switch (screen) {
		case PretupsI.SCREEN_OPT_O2C_INITIATE:
			allowedVoucherType = dp;
			break;

		default:
			allowedVoucherType = dep;
			break;
		}*/
        if(!BTSLUtil.isNullOrEmptyList(voucherTypeList)) {
        	if(!BTSLUtil.isNullArray(allowedVoucherType)) {
	        	for (int i = 0, j = voucherTypeList.size(); i < j; i++) {
	        		vomsCategoryVO = (VomsCategoryVO) voucherTypeList.get(i);
	                for (String element : allowedVoucherType) {
	                    if (element.equals(vomsCategoryVO.getType())) {
	                    	newVoucherTypeList.add(vomsCategoryVO);
	                    }
	                }
	            }
        	}
        }
        if (_log.isDebugEnabled()) {
        	int size = BTSLUtil.isNullOrEmptyList(voucherTypeList) ? 0 : voucherTypeList.size();
        	_log.debug(methodName, "Exiting newVoucherTypeList size = " + size);
        }
        return newVoucherTypeList;
    }
    
    /**
     * 
     * @param allowedVoucherType
     * @param voucherTypeList
     * @return
     */
    public static ArrayList getAllowedVoucherTypes(String[] allowedVoucherType, ArrayList voucherTypeList) {
        final String methodName = "getAllowedVoucherTypes";
        ArrayList<VoucherTypeVO> newVoucherTypeList = new ArrayList<VoucherTypeVO>();
        VoucherTypeVO voucherTypeVO;
        if(!BTSLUtil.isNullOrEmptyList(voucherTypeList)) {
        	if(!BTSLUtil.isNullArray(allowedVoucherType)) {
	        	for (int i = 0, j = voucherTypeList.size(); i < j; i++) {
	        		voucherTypeVO = (VoucherTypeVO) voucherTypeList.get(i);
	                for (String element : allowedVoucherType) {
	                    if (element.equals(voucherTypeVO.getType())) {
	                    	newVoucherTypeList.add(voucherTypeVO);
	                    }
	                }
	            }
        	}
        }
        if (_log.isDebugEnabled()) {
        	int size = BTSLUtil.isNullOrEmptyList(voucherTypeList) ? 0 : voucherTypeList.size();
        	_log.debug(methodName, "Exiting newVoucherTypeList size = " + size);
        }
        return newVoucherTypeList;
    }

    /**
     *  
     * @param screen
     * @return
     */
    public static String[] getAllowedVoucherTypesForScreen(String screen) {
        final String methodName = "getAllowedVoucherTypesForScreen";
        HashMap<String, String[]> screenWiseAllowedVoucherTypeMap = new HashMap<String, String[]>();
        String[] allowedVoucherTypes = {VOMSI.VOUCHER_TYPE_DIGITAL, VOMSI.VOUCHER_TYPE_TEST_DIGITAL, 
                     VOMSI.VOUCHER_TYPE_ELECTRONIC, VOMSI.VOUCHER_TYPE_TEST_ELECTRONIC, VOMSI.VOUCHER_TYPE_PHYSICAL, VOMSI.VOUCHER_TYPE_TEST_PHYSICAL}; 
        
        populateScreenWiseAllowedVoucherTypesMap(screen, screenWiseAllowedVoucherTypeMap);
        
        String[] tempAllowedVoucherTypes = screenWiseAllowedVoucherTypeMap.get(screen);
        if(tempAllowedVoucherTypes != null) {
        	allowedVoucherTypes = tempAllowedVoucherTypes;
        }
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Exiting allowedVoucherTypes  = " + allowedVoucherTypes.toString());
        }
        
        return allowedVoucherTypes;
    }
    
    /**
     * DENO:D,DT,E,ET,P,PT;PROF:D,DT,E,ET,P,PT;ACTIVE_PROF:E,ET;VOUC_GEN:D,DT,E,ET,P,PT;VOUC_APP:D,DT,E,ET,P,PT;VOUC_DOWN:P,PT;CHAN_STATUS:D,DT,E,ET,P,PT;O2C:D,DT,P,PT
     * @param screen
     * @param screenWiseAllowedVoucherTypeMap
     */
    public static void populateScreenWiseAllowedVoucherTypesMap(String screen, HashMap<String, String[]> screenWiseAllowedVoucherTypeMap) {
        final String methodName = "populateScreenWiseAllowedVoucherTypesMap";
        
        String screenWiseAllowedVoucherTypePref = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE));
        if(BTSLUtil.isNullString(screenWiseAllowedVoucherTypePref)) {
        	if (_log.isDebugEnabled()) {
            	_log.debug(methodName, "invalid preference value of screenWiseAllowedVoucherTypePref = " + screenWiseAllowedVoucherTypePref);
            }
        	return;
        }
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "screenWiseAllowedVoucherTypePref = " + screenWiseAllowedVoucherTypePref);
        }
        String[] screens = screenWiseAllowedVoucherTypePref.split(";");
        for (int i = 0; i < screens.length; i++) {
        	if(BTSLUtil.isNullString(screens[i])) {
        		if (_log.isDebugEnabled()) {
                	_log.debug(methodName, "invalid preference value of " + screenWiseAllowedVoucherTypePref);
                }
            	return;
            }
            String[] screenWiseAllowedVoucherType = screens[i].split(PretupsI.COLON);
            screenWiseAllowedVoucherTypeMap.put(screenWiseAllowedVoucherType[0], screenWiseAllowedVoucherType[1].split(PretupsI.COMMA));
        }
        if (_log.isDebugEnabled()) {
              _log.debug(methodName, "Exiting screenWiseAllowedVoucherTypeMap Size = " + screenWiseAllowedVoucherTypeMap.keySet().size());
        }
    }
    
    /**
     * 
     * @param allowedVoucherType
     * @param voucherTypeList
     * @return
     */
    public static ArrayList getActiveVoucherTypes(String[] allowedVoucherType, ArrayList<VomsActiveProductVO> voucherTypeList) {
        final String methodName = "getActiveVoucherTypes";
        ArrayList<VomsActiveProductVO> newVoucherTypeList = new ArrayList<VomsActiveProductVO>();
        VomsActiveProductVO vomsActiveProductVO;
        if(!BTSLUtil.isNullOrEmptyList(voucherTypeList)) {
        	if(!BTSLUtil.isNullArray(allowedVoucherType)) {
	        	for (int i = 0, j = voucherTypeList.size(); i < j; i++) {
	        		vomsActiveProductVO = (VomsActiveProductVO) voucherTypeList.get(i);
	                for (String element : allowedVoucherType) {
	                    if (element.equals(vomsActiveProductVO.getType())) {
	                    	newVoucherTypeList.add(vomsActiveProductVO);
	                    }
	                }
	            }
        	}
        }
        if (_log.isDebugEnabled()) {
        	int size = BTSLUtil.isNullOrEmptyList(voucherTypeList) ? 0 : voucherTypeList.size();
        	_log.debug(methodName, "Exiting newVoucherTypeList size = " + size);
        }
        return newVoucherTypeList;
    }


    /**
     *  
     * @param voucherTypeCode
     * @return String[] of life cycle 
     */
    public static String[] getLifeCycleForVoucherType(String voucherTypeCode) {
        final String methodName = "getLifeCycleForVoucherType";
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Entering getLifeCycleForVoucherType =  " + voucherTypeCode);
        }	
        HashMap<String,String> prefrencemap=new HashMap<String,String>();
        prefrencemap.put(VOMSI.VOUCHER_TYPE_PHYSICAL, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_P_LIFECYCLE)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_TEST_PHYSICAL, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_P_LIFECYCLE)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_TEST_ELECTRONIC, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_E_LIFECYCLE)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_ELECTRONIC, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_E_LIFECYCLE)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_DIGITAL, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_D_LIFECYCLE)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_TEST_DIGITAL, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_D_LIFECYCLE)));
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Exiting getLifeCycleForVoucherType  = " + prefrencemap.get(voucherTypeCode).split(":"));
        }
		return prefrencemap.get(voucherTypeCode).split(":");
       
    }
    
    /**
     *  
     * @param currentVoucherStatus,voucherTypeCode
     * @return String of next voucher status
     */
    public static String getNextVoucherLifeStatus(String currentVoucherStatus,String voucherTypeCode) {
        final String methodName = "getNextVoucherLifeStatus";
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Entering getNextVoucherLifeStatus =  " + currentVoucherStatus + " type = " + voucherTypeCode);
        }
        String[] vmslife=getLifeCycleForVoucherType(voucherTypeCode);
        if(vmslife!=null && vmslife.length!=0)
        {
        	for(int i=0;i<vmslife.length-1;i++)
        	{
        		if(currentVoucherStatus.equals(vmslife[i]))
        		{
        			 if (_log.isDebugEnabled()) {
        		        	_log.debug(methodName, "Exiting getNextVoucherLifeStatus  = " + vmslife[i+1]);
        		        }
        			
        			return vmslife[i+1];
        		}
        	}
        }
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Exiting getNextVoucherLifeStatus  = " + null);
        }
		return null; 
    }
   
    
    

    public static String[] getStatusBasedOnVoucherType(String type, String curStatus) {
        final String methodName = "getStatusBasedOnVoucherType";
        String allStatus = null;
        try {
        	if(VOMSI.VOUCHER_TYPE_DIGITAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_DIGITAL.equals(type)) {
        		allStatus = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_D_STATUS_CHANGE_MAP));
        	}
        	else if(VOMSI.VOUCHER_TYPE_ELECTRONIC.equals(type) || VOMSI.VOUCHER_TYPE_TEST_ELECTRONIC.equals(type)) {
        		allStatus = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_E_STATUS_CHANGE_MAP));
        	} else if(VOMSI.VOUCHER_TYPE_PHYSICAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_PHYSICAL.equals(type)) {
        		allStatus = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_P_STATUS_CHANGE_MAP));
        	}
        	if(!BTSLUtil.isNullString(allStatus)) {
        		ArrayList<String> statusList = new ArrayList<String>();
        		String[] allStatusSplit = allStatus.split(PretupsI.COMMA);
        		for(int i=0;i<allStatusSplit.length;i++)
        		{
        			String pair = allStatusSplit[i];
        			String[] fromTo = pair.split(PretupsI.COLON);
        			if(fromTo[0].equals(curStatus))
        			{
        				statusList.add(fromTo[1]);
        			}
        		}
        		
        		if(statusList.size() > 0)
        		{
        			String[] array = statusList.toArray(new String[statusList.size()]);
        			return array;
        		}
        	}
        } catch (Exception e) {
            _log.error(methodName, "Exception e=" + e.getMessage());
            _log.errorTrace(methodName, e);
        }
        return null;
    }
    
    
    public static String getSystemPrefrenceOfVoucher(String voucherTypeCode) {
        final String methodName = "getSystemPrefrenceOfVoucher";
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Entering getSystemPrefrenceOfVoucher =  " + voucherTypeCode);
        }	
        HashMap<String,String> prefrencemap=new HashMap<String,String>();
        prefrencemap.put(VOMSI.VOUCHER_TYPE_PHYSICAL, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_P_STATUS_CHANGE_MAP)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_TEST_PHYSICAL, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_P_STATUS_CHANGE_MAP)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_TEST_ELECTRONIC, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_E_STATUS_CHANGE_MAP)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_ELECTRONIC, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_E_STATUS_CHANGE_MAP)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_DIGITAL, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_D_STATUS_CHANGE_MAP)));
        prefrencemap.put(VOMSI.VOUCHER_TYPE_TEST_DIGITAL, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_D_STATUS_CHANGE_MAP)));
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Exiting getSystemPrefrenceOfVoucher  = " + prefrencemap.get(voucherTypeCode));
        }
		return prefrencemap.get(voucherTypeCode);
       
    }
    

}
