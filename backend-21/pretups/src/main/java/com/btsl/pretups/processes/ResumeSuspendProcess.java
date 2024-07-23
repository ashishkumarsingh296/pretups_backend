package com.btsl.pretups.processes;

/*
 * @# ResumeSuspendProcess.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * 09/01/2007 dhiraj.tiwari Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 */

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.network.businesslogic.NetworkWebDAO;

public class ResumeSuspendProcess extends Thread {
    private static Log _logger = LogFactory.getLog(ResumeSuspendProcess.class.getName());

    private static String _serviceType = null; // Service Type which has to be
    // suspended or resumed.
    private static String _service = null; // Services those have to be suspended
    // or resumed.
    // private static String _action = null; //Action Value (Resume/Suspend).
    private static String _key = PretupsErrorCodesI.PROCESS_RESUMESUSPEND_SUSPEND_MSG; // key
    // corresponding
    // to
    // message
    // which
    // has
    // to
    // be
    // stored
    // in
    // DB
    private static String _action = null;
    private static String _statusType = null;

    // Service type
    public static final String _INTERFACES = "INT"; // Service Type = Interface
    public static final String _NETWORKS = "NET"; // Service Type = Network
    public static final String _SERVICE = "SER"; // Service Type = Service

    // Action
    public static final String _ACTION_SUSPEND = "S";
    public static final String _ACTION_RESUME = "Y";

    private ArrayList _updateList = null;
    private static String _prevStatusType = null;
    private static AutoProcessBL _autoProcessBL = null;

    public ResumeSuspendProcess() {
    }

    public ResumeSuspendProcess(
                    String p_serviceType, String p_serviceId, String p_action, String p_key, String statusType) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("ResumeSuspendProcess constructor Entered",
                "p_serviceType=" + p_serviceType + ", p_serviceId =" + p_serviceId + ",p_action=" + p_action + ",p_key=" + p_key + ",statusType=" + statusType);
        }
        _serviceType = p_serviceType;
        _service = p_serviceId;
        _action = p_action;
        _statusType = statusType;
        if (!BTSLUtil.isNullString(p_key)) {
            _key = p_key;
        }
    }

    /*
     * Process first validates and identifies arguments passed except services
     * validation.
     * After validation and identification, process method is called which in
     * turn calls
     * ResumeSuspendINinterface method to update status in database. if update
     * is successfull
     * then main method calls updateCache method to update cache else process
     * returns. After
     * execution of updateCache, process sends notification message to admin
     * numbers whether
     * cache successfully updated or not.
     */
    public static void main(String arg[]) {
        Connection con = null;
        String message = null;
        String action = null;
        Locale locale = null;
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 5) {
                _logger.info(METHOD_NAME, "Usage : ResumeSuspendProcess [Constants file] [LogConfig file] [Service Type ] [Service Id] [Action(Y/S)]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(arg[0]);
            if (!constantsFile.exists()) {
                _logger.debug(METHOD_NAME, "ResumeSuspendProcess" + " Constants File " + constantsFile + " Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(arg[1]);
            if (!logconfigFile.exists()) {
                _logger.debug(METHOD_NAME, "ResumeSuspendProcess" + " Logconfig File " + logconfigFile + " Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            _serviceType = arg[2];
            _service = arg[3];
            action = arg[4];

            if (BTSLUtil.isNullString(_serviceType)) {
                _logger.info(METHOD_NAME, "ResumeSuspendProcess" + " Service Type Not Found .");
                _logger.error(METHOD_NAME, "ResumeSuspendProcess : Service Type Not Found .............");
                return;
            }
            if (BTSLUtil.isNullString(_service)) {
                _logger.info(METHOD_NAME, "ResumeSuspendProcess" + " Service ID Not Found .............");
                _logger.error(METHOD_NAME, "ResumeSuspendProcess : Service ID Not Found .............");
                return;
            }
            if (BTSLUtil.isNullString(action)) {
                _logger.info(METHOD_NAME, "ResumeSuspendProcess" + " Action(Y/S) Not Found .............");
                _logger.error(METHOD_NAME, "ResumeSuspendProcess : Action(Y/S) Not Found .............");
                return;
            }
            if (!(_ACTION_SUSPEND.equalsIgnoreCase(action) || _ACTION_RESUME.equalsIgnoreCase(action))) {
                _logger.info(METHOD_NAME, "ResumeSuspendProcess" + " Action(Y/S) Not Found .............");
                _logger.error(METHOD_NAME, "ResumeSuspendProcess : Action(Y/S) Not Found .............");
                return;
            }
            if (BTSLUtil.isNullString(_key)) {
                _logger.info(METHOD_NAME, "ResumeSuspendProcess" + " Key Not Found .............");
                _logger.error(METHOD_NAME, "ResumeSuspendProcess : Key Not Found .............");
                return;
            }
            _statusType = PretupsI.INTERFACE_STATUS_TYPE_MANUAL;
            final ResumeSuspendProcess resumeSuspendProcess = new ResumeSuspendProcess();
            if (!resumeSuspendProcess.isServiceTypeValid()) {
                _logger.info(METHOD_NAME, "ResumeSuspendProcess" + " Service Type provided is not valid, Values Supported are INT/NET/SER");
                _logger.error(METHOD_NAME, "ResumeSuspendProcess : Service Type provided is not valid, Values Supported are INT/NET/SER");
                return;
            }

            con = OracleUtil.getSingleConnection();

            final ArrayList localeList = LocaleMasterCache.getLocaleListForSMS();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "ArrayList size returned by LocaleMasterCache.getLocaleListForSMS()  =" + localeList.size() + "\nlocaleList =" + localeList);
            }
            Locale secondLocale = null;
            if (localeList != null) {
                secondLocale = (Locale) localeList.get(0);
            } else {
                secondLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }

            // get messages to be stored in DB while updating status.
            final String message1 = BTSLUtil.getMessage(locale, _key, null);
            final String message2 = BTSLUtil.getMessage(secondLocale, _key, null);
            final String i = resumeSuspendProcess.process(con, action, message1, message2);
            con.commit();
            _autoProcessBL = new AutoProcessBL();
            message = _autoProcessBL.updateCache(con, String.valueOf(i), _serviceType, locale);
        }

        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "Error occurred during execution of resume/suspend process. So Cache not updated. BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[main]", "", "", "",
                "Error occurred during execution of resume/suspend process. So status not changed. BTSLBaseException:" + be.getMessage());
            if (BTSLUtil.isNullString(message) || message.trim().length() > 0) {
                message = BTSLUtil.getMessage(locale, PretupsErrorCodesI.AUTO_CACHEUPDATE_FAIL, new String[] { _service });
            }
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.errorTrace(METHOD_NAME, be);
            return;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Error occurred during execution of resume/suspend process. So Cache not updated. Exception : " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[main]", "", "", "",
                "Error occurred during execution of resume/suspend process. So status not changed. Exception:" + e.getMessage());
            if (BTSLUtil.isNullString(message) || message.trim().length() > 0) {
                message = BTSLUtil.getMessage(locale, PretupsErrorCodesI.AUTO_CACHEUPDATE_FAIL, new String[] { _service });
            }
            _logger.errorTrace(METHOD_NAME, e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Exception : " + e.getMessage());
            }
            return;
        }// end of catch
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "Exception : " + ex.getMessage());
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " Exception : " + ex.getMessage());
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }

            if (!BTSLUtil.isNullString(message)) {
                // Send Message to admin numbers after execution of process.
                final String msisdnString = new String(Constants.getProperty("adminmobile"));
                final String[] msisdn = msisdnString.split(",");
                // Locale defaultLocale =new
                // Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                final int noOfmsisdn = msisdn.length;
                for (int j = 0; j < noOfmsisdn; j++) {
                    final PushMessage pushMessage = new PushMessage(msisdn[j], message, null, null, locale);
                    pushMessage.push();
                }
            }
            ConfigServlet.destroyProcessCache();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * Method to update the appropriate Service in DB and update the Cache at
     * all nodes
     * 
     * @param p_serviceType
     * @param p_serviceId
     * @param p_action
     * @param p_key
     */
    public void run() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("ResumeSuspendProcess run Entered", "_serviceType=" + _serviceType + ", _service =" + _service + ",_action=" + _action + ",_key=" + _key);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        String message = null;
        String action = null;
        Locale locale = null;

        final String METHOD_NAME = "resumeSuspendProcess";
        try {

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            action = _action;
            if (_autoProcessBL == null) {
                _autoProcessBL = new AutoProcessBL();
            }
            final ArrayList localeList = LocaleMasterCache.getLocaleListForSMS();
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            Locale secondLocale = null;
            if (!localeList.isEmpty()) {
                secondLocale = (Locale) localeList.get(0);
            } else {
                secondLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }

            // get messages to be stored in DB while updating status.
            final String message1 = BTSLUtil.getMessage(locale, _key, null);
            final String message2 = BTSLUtil.getMessage(secondLocale, _key, null);
            final String i = process(con, action, message1, message2);
            con.commit();
            updateLocalInstanceCache(i);

            message = _autoProcessBL.updateCache(con, String.valueOf(i), _serviceType, locale);
        }

        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess", "", "", "",
                "Auto Update of cache failed." + be.getMessage());
            if (BTSLUtil.isNullString(message) || message.trim().length() > 0) {
                message = BTSLUtil.getMessage(locale, PretupsErrorCodesI.AUTO_CACHEUPDATE_FAIL, new String[] { _service });
            }
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.errorTrace(METHOD_NAME, be);
            return;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess", "", "", "",
                "Auto Update of cache failed." + e.getMessage());
            if (BTSLUtil.isNullString(message) || message.trim().length() > 0) {
                message = BTSLUtil.getMessage(locale, PretupsErrorCodesI.AUTO_CACHEUPDATE_FAIL, new String[] { _service });
            }
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            return;
        }// end of catch
        finally {
			if (mcomCon != null) {
				mcomCon.close("ResumeSuspendProcess#run");
				mcomCon = null;
			}

            if (!BTSLUtil.isNullString(message)) {
                // Send Message to admin numbers after execution of process.
                final String msisdnString = new String(Constants.getProperty("adminmobile"));
                final String[] msisdn = msisdnString.split(",");
                final int noOfmsisdn = msisdn.length;
                for (int j = 0; j < noOfmsisdn; j++) {
                    final PushMessage pushMessage = new PushMessage(msisdn[j], message, null, null, locale);
                    pushMessage.push();
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * This method identifies and validates the service type and calls
     * resumeSuspendINinterface method
     * to resume or suspend services provided.
     * 
     * @param p_con
     *            Connection
     * @param p_action
     *            String
     * @param p_message1
     *            String
     * @param p_message2
     *            String
     * @return int
     * @throws BTSLBaseException
     */

    private String process(Connection p_con, String p_action, String p_message1, String p_message2) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME,
                "Entering with p_action=" + p_action + " _serviceType=" + _serviceType + " _service=" + _service + " p_message1=" + p_message1 + " p_message2=" + p_message2);
        }
        String i = null;
        try {
            if (_serviceType.equalsIgnoreCase(_INTERFACES)) {
                i = PretupsI.CACHE_MSISDNPrefixInterfaceMappingCache;
                resumeSuspendINinterface(p_con, p_action, p_message1, p_message2);
            } else if (_serviceType.equalsIgnoreCase(_NETWORKS)) {
                i = PretupsI.CACHE_NETWORK;
                resumeSuspendNetwork(p_con, p_action, p_message1, p_message2);
            } else if (_serviceType.equalsIgnoreCase(_SERVICE)) {
                i = PretupsI.CACHE_NETWORK_SERVICE_CACHE;
                resumeSuspendService(p_con, p_action, p_message1, p_message2);
            }

            if (BTSLUtil.isNullString(i)) {
                throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INVALID_SERVICE);

            }
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "Error occured while while executing process." + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Error occured while while executing process." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[ResumeSuspend]", "", "", "",
                "Service provided are not valid");
            throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);

        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting with value " + i);
            }
        }
        return i;
    }

    /**
     * This method validates service type passed as argument.
     * 
     * @return boolean
     */

    private boolean isServiceTypeValid() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("isServiceTypeValid", "Entering..");
        }
        boolean isServiceTypeValid = false;
        if (_INTERFACES.equalsIgnoreCase(_serviceType)) {
            isServiceTypeValid = true;
        } else if (_NETWORKS.equalsIgnoreCase(_serviceType)) {
            isServiceTypeValid = true;
        } else if (_SERVICE.equalsIgnoreCase(_serviceType)) {
            isServiceTypeValid = true;
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("isServiceTypeValid", "Exiting with isServiceTypeValid value = " + isServiceTypeValid);
        }
        return isServiceTypeValid;
    }

    /**
     * This method will update Status of services in DB.
     * 
     * @param p_con
     *            Connection
     * @param p_networkDetailsList
     *            ArrayList
     * @return void
     * @throws BTSLBaseException
     */

    private void updateNetworkDetailsList(Connection p_con, ArrayList p_networkDetailsList) throws BTSLBaseException {
        final String METHOD_NAME = "updateNetworkDetailsList";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered..");
        }
        int updateCount = 0;
        try {
            final NetworkWebDAO networkwebDAO = new NetworkWebDAO();
            final NetworkVO networkVO = null;

            /*
             * for(int i=0; i<p_networkDetailsList.size(); i++)
             * {
             * updateCount = -1;
             * if(_logger.isDebugEnabled())_logger.debug("updateNetworkDetailsList"
             * ,
             * "got item "+i+" from p_networkDetailsList("+p_networkDetailsList
             * .size()+")");
             * networkVO=(NetworkVO)p_networkDetailsList.get(i);
             * updateCount = networkDAO.updateNetwork(p_con, networkVO);
             * if(updateCount == -1)
             * {
             * _logger.error("updateInterfaceDetailsList",
             * "Error occured while updating InterfaceDetails in DB through InterfaceVO"
             * );
             * throw new
             * BTSLBaseException("ResumeSuspendProcess","updateNetworkDetailsList"
             * ,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_DB_NOT_UPDATED);
             * }
             * }
             */
            updateCount = networkwebDAO.updateNetworkStatus(p_con, p_networkDetailsList);
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Network Details updateCount = " + updateCount);
            }
            if (updateCount == 0) {
                _logger.error(METHOD_NAME, "Error occured while updating InterfaceDetails in DB through InterfaceVO");
                throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_DB_NOT_UPDATED);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Network Details updated in DB");
            }
        } catch (BTSLBaseException bte) {
            _logger.error(METHOD_NAME, "Error occured while updating Network Details");
            _logger.errorTrace(METHOD_NAME, bte);
            throw bte;
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Error occured while updating Network Details");
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[updateNetworkDetailsList]", "",
                "", "", "Service provided are not valid");
            throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION,ex);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting with updateCount =" + updateCount);
            }
        }

    }

    /**
     * This method will update Status of services in DB.
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceDetailsList
     *            ArrayList
     * @return void
     * @throws BTSLBaseException
     */

    private void updateInterfaceDetailsList(Connection p_con, ArrayList p_interfaceDetailsList) throws BTSLBaseException {
        final String METHOD_NAME = "updateInterfaceDetailsList";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered..");
        }
        int updateCount = -1;
        try {
            final InterfaceDAO interfaceDAO = new InterfaceDAO();
            InterfaceVO interfaceVO = null;
            final int interfaceListSize = p_interfaceDetailsList.size();
            for (int i = 0; i < interfaceListSize; i++) {
                updateCount = -1;
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "got item " + i + " from p_interfaceDetailsList(" + p_interfaceDetailsList.size() + ")");
                }
                interfaceVO = (InterfaceVO) p_interfaceDetailsList.get(i);
                updateCount = interfaceDAO.modifyInterfaceDetails(p_con, interfaceVO);
                if (updateCount == -1) {
                    _logger.error(METHOD_NAME, "Error occured while updating InterfaceDetails in DB through InterfaceVO");
                    throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_DB_NOT_UPDATED);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "InterfaceDetails updated in DB");
            }
        } catch (BTSLBaseException bte) {
            _logger.error(METHOD_NAME, "Error occured while updating InterfaceDetails");
            _logger.errorTrace(METHOD_NAME, bte);
            throw bte;
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Error occured while updating InterfaceDetails");
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[ResumeSuspend]", "", "", "",
                "Service provided are not valid");
            throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION,ex);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting with updateCount =" + updateCount);
            }
        }

    }

    /**
     * This method will validate all interface services provided.
     * 
     * @param p_interfaceDetailsList
     *            ArrayList
     * @return ArrayList
     */

    private ArrayList validateInterfaceServices(ArrayList p_interfaceDetailsList) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("validateInterfaceServices", "p_interfaceDetailsList: " + p_interfaceDetailsList);
        }
        boolean isServiceValid = false;
        final ArrayList newList = new ArrayList();
        final String[] services = _service.split(",");
        if (_logger.isDebugEnabled()) {
            _logger.debug("validateInterfaceServices", "Entered ..");
        }
        InterfaceVO interfaceVO = null;
        final int noOfServices = services.length;
        final int noOfinterface = p_interfaceDetailsList.size();
        for (int j = 0; j < noOfServices; j++) {
            isServiceValid = false;
            for (int i = 0; i < noOfinterface; i++) {
                interfaceVO = (InterfaceVO) p_interfaceDetailsList.get(i);
                if ((services[j].equalsIgnoreCase(interfaceVO.getInterfaceId()))) {
                    isServiceValid = true;
                    break;
                }
            }
            if (isServiceValid) {
                newList.add(interfaceVO);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("validateInterfaceServices", services[j] + " validated " + isServiceValid + ". Added to List");
                }
            } else {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("validateInterfaceServices", services[j] + " validated " + isServiceValid);
                }
            }

        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("validateInterfaceServices", " Exiting with newList.size= " + newList.size());
        }
        return newList;
    }

    /**
     * This method will validate all network services provided.
     * 
     * @param p_networkDetailsList
     *            ArrayList
     * @return ArrayList
     */

    private ArrayList validateNetworkServices(ArrayList p_networkDetailsList) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("validateNetworkServices", "Entered ..p_networkDetailsList" + p_networkDetailsList);
        }
        boolean isServiceValid = false;
        final ArrayList newList = new ArrayList();
        final String[] services = _service.split(",");

        NetworkVO networkVO = null;
        final int noOfServices = services.length;
        final int noOfNetwork = p_networkDetailsList.size();
        for (int j = 0; j < noOfServices; j++) {
            isServiceValid = false;
            for (int i = 0; i < noOfNetwork; i++) {
                networkVO = (NetworkVO) p_networkDetailsList.get(i);
                if ((services[j].equalsIgnoreCase(networkVO.getNetworkCode()))) {
                    isServiceValid = true;
                    break;
                }
            }
            if (isServiceValid) {
                newList.add(networkVO);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("validateNetworkServices", services[j] + " validated " + isServiceValid + ". Added to List");
                }
            } else {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("validateNetworkServices", services[j] + " validated " + isServiceValid);
                }
            }

        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("validateNetworkServices", " Exiting with newList.size= " + newList.size());
        }
        return newList;
    }

    /**
     * This method loads, validates and updates network details.
     * 
     * @param p_con
     *            Connection
     * @param p_action
     *            String
     * @return void
     * @throws BTSLBaseException
     */

    private void resumeSuspendNetwork(Connection p_con, String p_action, String p_message1, String p_message2) throws BTSLBaseException {
        final String METHOD_NAME = "resumeSuspendNetwork";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entering with p_action=" + p_action);
        }
        try {
            ArrayList networkDetailsList = null;
            final String p_status = "'N'";

            // load all networks deails
            networkDetailsList = new NetworkDAO().loadNetworkList(p_con, p_status);

            // Validate Each Network Passed Other than ALL
            ArrayList newList = null;
            int noOfIDs = 0;
            if (!(TypesI.ALL.equalsIgnoreCase(_service))) {
                newList = validateNetworkServices(networkDetailsList);
                final String[] services = _service.split(",");
                noOfIDs = services.length;
            } else {
                noOfIDs = networkDetailsList.size();
                newList = networkDetailsList;
                // newList=new ArrayList();
                // newList.addAll(networkDetailsList);
            }

            if (newList.size() < noOfIDs) {
                _logger.error(METHOD_NAME, "Service provided are not valid....wrong Network Code(s) passed");
                throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INVALID_SERVICE);
            }

            // Update Networks Details
            final int newListSize = newList.size();
            for (int i = 0; i < newListSize; i++) {
                final NetworkVO networkVO = (NetworkVO) newList.get(i);
                networkVO.setStatus(p_action);
                networkVO.setLanguage1Message(p_message1);
                networkVO.setLanguage2Message(p_message2);
                networkVO.setModifiedBy(TypesI.SYSTEM_USER);
                networkVO.setModifiedOn(new Date());
            }
            updateNetworkDetailsList(p_con, newList);
        } catch (BTSLBaseException bte) {
            _logger.error(METHOD_NAME, "Exception : " + bte.getMessage());
            _logger.errorTrace(METHOD_NAME, bte);
            throw bte;
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[resumeSuspendNetwork]", "", "",
                "", "Exception=" + ex.getMessage());
            throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION,ex);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Exiting..");
            }
        }

    }

    /**
     * This method loads, validates and updates interface details.
     * 
     * @param p_con
     *            Connection
     * @param p_action
     *            String
     * @return void
     * @throws BTSLBaseException
     */

    private void resumeSuspendINinterface(Connection p_con, String p_action, String p_message1, String p_message2) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("resumeSuspendINinterface", " Entering with p_action=" + p_action);
        }
        final String METHOD_NAME = "ResumeSuspendINinterface";
        try {
            ArrayList interfaceDetailsList = new ArrayList();
            ArrayList newList = null;
            if (_updateList == null) {
                // load all interfaces deails
                interfaceDetailsList = new InterfaceDAO().loadInterfaceDetailsList(p_con);

                // Validate Each Interface Passed Other than ALL

                int noOfIDs = 0;

                if (!(TypesI.ALL.equalsIgnoreCase(_service))) {
                    newList = validateInterfaceServices(interfaceDetailsList);
                    final String[] services = _service.split(",");
                    noOfIDs = services.length;
                } else {
                    noOfIDs = interfaceDetailsList.size();
                    newList = new ArrayList();
                    newList.addAll(interfaceDetailsList);
                }

                if (newList.size() < noOfIDs) {
                    _logger.error(METHOD_NAME, "Service provided are not valid....wrong Interface IDs ");
                    throw new BTSLBaseException("ResumeSuspendProcess", "resumeSuspendINinterface", PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INVALID_SERVICE);
                }

                _updateList = new ArrayList();
            } else {
                newList = new ArrayList();
                newList.addAll(_updateList);
                _updateList.clear();
            }
            InterfaceVO interfaceVO = null;
            // Update interfaces Details
            final int newListSize = newList.size();
            for (int i = 0; i < newListSize; i++) {
                interfaceVO = (InterfaceVO) newList.get(i);
                // it will be null in first attempt. If in attempt to update
                // cache, error comes previous status type is restored in
                // database.
                if (_prevStatusType == null) {
                    _prevStatusType = interfaceVO.getStatusType();
                }
                if (interfaceVO.getStatusCode().equalsIgnoreCase(p_action)) {
                    continue;
                }
                if (BTSLUtil.isNullString(interfaceVO.getLanguage1Message())) {
                    interfaceVO.setLanguage1Message(p_message1);
                }
                if (BTSLUtil.isNullString(interfaceVO.getLanguage2Message())) {
                    interfaceVO.setLanguage2Message(p_message2);
                }
                interfaceVO.setStatusCode(p_action);
                interfaceVO.setModifiedBy(TypesI.SYSTEM_USER);
                interfaceVO.setModifiedOn(new Date());
                interfaceVO.setStatusType(_statusType);
                _updateList.add(interfaceVO);
            }
            updateInterfaceDetailsList(p_con, _updateList);
        } catch (BTSLBaseException bte) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + bte.getMessage());
            _logger.errorTrace(METHOD_NAME, bte);
            throw bte;
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[resumeSuspendINinterface]", "",
                "", "", "Exception=" + ex.getMessage());
            throw new BTSLBaseException("ResumeSuspendProcess", "resumeSuspendINinterface", PretupsErrorCodesI.ERROR_EXCEPTION,ex);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("resumeSuspendINinterface", " Exiting..");
            }
        }

    }

    /**
     * This method will update cache for the services which are passed through
     * cacheItems parameter.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceType
     *            String
     * @return void
     * @throws BTSLBaseException
     */

    /*
     * private void updateCache(Connection p_con,String p_serviceType) throws
     * BTSLBaseException
     * {
     * if(_logger.isDebugEnabled())_logger.debug("updateCache",
     * " Entering with _serviceType = "+p_serviceType);
     * String cacheParam = "cacheParam";
     * StringBuffer urlString = new StringBuffer();
     * LoadControllerDAO controlDAO = new LoadControllerDAO();
     * InstanceLoadVO instanceLoadVO = null;
     * String ip=null;
     * String port=null;
     * boolean isWebInstanceFound=false;
     * try
     * {
     * ArrayList instanceList = controlDAO.loadInstanceLoadDetails(p_con);
     * for(int i = 0 , k = instanceList.size() ; i < k ; i++)
     * {
     * instanceLoadVO = (InstanceLoadVO) instanceList.get(i);
     * if(instanceLoadVO.getInstanceType().equals(PretupsI.INSTANCE_TYPE_WEB))
     * {
     * ip=instanceLoadVO.getHostAddress();
     * port=instanceLoadVO.getHostPort();
     * isWebInstanceFound=true;
     * break;
     * }
     * }
     * if(!isWebInstanceFound)
     * {
     * _logger.error("updateCache", "WebInstance not found");
     * throw new
     * BTSLBaseException("ResumeSuspendProcess","updateCache",PretupsErrorCodesI
     * .PROCESS_RESUMESUSPEND_CACHE_NOT_UPDATED);
     * }
     * 
     * urlString = new StringBuffer();
     * urlString.append("http://");
     * urlString.append(ip);
     * urlString.append(":");
     * urlString.append(port);
     * urlString.append("/pretups/UpdateCacheServlet?fromWeb=WEB&");
     * urlString.append(cacheParam);
     * urlString.append("=");
     * urlString.append(p_serviceType);
     * try
     * {
     * hitWebInstance(urlString.toString());
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * //If in first try cache is not updated (Exception is thrown by
     * hitWebInstance method),
     * //we have to Try once again to update cache.So again calling
     * hitWebInstance.
     * //If it also fails then process will throw exception .
     * try
     * {
     * hitWebInstance(urlString.toString());
     * }
     * catch(Exception ex)
     * {
     * ex.printStackTrace();
     * _logger.error("updateCache", "error while executing hitWebInstance");
     * throw new
     * BTSLBaseException("ResumeSuspendProcess","updateCache",PretupsErrorCodesI
     * .PROCESS_RESUMESUSPEND_CACHE_NOT_UPDATED);
     * }
     * }
     * }
     * catch(BTSLBaseException be)
     * {
     * _logger.error("updateCache", "WebInstance not found");
     * be.printStackTrace();
     * throw be;
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _logger.error("updateCache", "WebInstance not found");
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "UpdateCacheServlet[updateCache]", "", "", "", "Exception:" +
     * e.getMessage());
     * throw new
     * BTSLBaseException("ResumeSuspendProcess","updateCache",PretupsErrorCodesI
     * .ERROR_EXCEPTION);
     * }
     * }
     */

    /**
     * This method will update cache on local IP.
     * 
     * @param p_serviceType
     *            int
     * @return void
     */
    private void updateLocalInstanceCache(String p_serviceType) {
        final String METHOD_NAME = "updateLocalInstanceCache";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entering with p_serviceType = " + p_serviceType);
        }
        if (PretupsI.CACHE_MSISDNPrefixInterfaceMappingCache.equals(p_serviceType)) {
            try {
                MSISDNPrefixInterfaceMappingCache.updatePrefixInterfaceMapping();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                _logger.error(METHOD_NAME, "Unable to update Interface Cache on local");
            }
        } else if (PretupsI.CACHE_NETWORK.equals(p_serviceType)) {
            try {
                NetworkCache.updateNetwork();
            } catch (Exception e) {
                _logger.error(METHOD_NAME, "Unable to update Network Cache on local");
                _logger.errorTrace(METHOD_NAME, e);
            }
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Exited");
        }
    }

    /**
     * This method loads, validates and updates Service details.
     * 
     * @param p_con
     *            Connection
     * @param p_action
     *            String
     * @return void
     * @throws BTSLBaseException
     */

    private void resumeSuspendService(Connection p_con, String p_action, String p_message1, String p_message2) throws BTSLBaseException {
        final String METHOD_NAME = "resumeSuspendService";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entering with p_action=" + p_action);
        }
        try {
            ArrayList serviceDetailsList = null;
            String p_status = "N";
            if (p_action.equalsIgnoreCase(PretupsI.SUSPEND)) {
                p_status = "Y";
            }
            // load all Service deails
            serviceDetailsList = new ServiceKeywordDAO().loadAllServiceTypeList(p_con, p_status);

            // Validate Each Service Passed Other than ALL
            ArrayList newList = null;
            int noOfIDs = 0;
            if (!(TypesI.ALL.equalsIgnoreCase(_service))) {
                newList = validateAllServices(serviceDetailsList);
                final String[] services = _service.split(",");
                noOfIDs = services.length;
            } else {
                noOfIDs = serviceDetailsList.size();
                newList = serviceDetailsList;
            }
            if (newList.size() < noOfIDs) {
                _logger.error(METHOD_NAME, "Service provided are not valid....wrong Service Code(s) passed");
                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INVALID_SERVICE);
            }

            // Update Networks Details
            final int newListSize = newList.size();
            for (int i = 0; i < newListSize; i++) {
                final ServiceKeywordVO serviceKeywordVO = (ServiceKeywordVO) newList.get(i);
                if (p_action.equalsIgnoreCase(PretupsI.SUSPEND)) {
                    serviceKeywordVO.setStatus(PretupsI.NO);
                } else {
                    serviceKeywordVO.setStatus(p_action);
                }
                serviceKeywordVO.setLanguage1Message(p_message1);
                serviceKeywordVO.setLanguage2Message(p_message2);
                serviceKeywordVO.setModifiedBy(TypesI.SYSTEM_USER);
                serviceKeywordVO.setModifiedOn(new Date());
            }
            updateServiceList(p_con, newList);
        } catch (BTSLBaseException bte) {
            _logger.error(METHOD_NAME, "Exception : " + bte.getMessage());
            _logger.errorTrace(METHOD_NAME, bte);
            throw bte;
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[resumeSuspendService]", "", "",
                "", "Exception=" + ex.getMessage());
            throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION,ex);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Exiting..");
            }
        }

    }

    /**
     * This method will validate all network services provided.
     * 
     * @param p_networkDetailsList
     *            ArrayList
     * @return ArrayList
     */

    private ArrayList validateAllServices(ArrayList p_serviceDetailsList) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("validateAllServices", "Entered ..p_serviceDetailsList" + p_serviceDetailsList);
        }
        boolean isServiceValid = false;
        final ArrayList newList = new ArrayList();
        final String[] services = _service.split(",");

        ServiceKeywordVO serviceKeywordVO = null;
        final int noOfServices = services.length;
        final int noOfAllServices = p_serviceDetailsList.size();

        for (int j = 0; j < noOfServices; j++) {
            isServiceValid = false;
            for (int i = 0; i < noOfAllServices; i++) {
                serviceKeywordVO = (ServiceKeywordVO) p_serviceDetailsList.get(i);
                services[j] = services[j].trim();
                if ((services[j].equalsIgnoreCase(serviceKeywordVO.getSender_network() + ":" + serviceKeywordVO.getModuleCode() + ":" + serviceKeywordVO.getServiceType()))) {
                    isServiceValid = true;
                    break;
                }
            }
            if (isServiceValid) {
                newList.add(serviceKeywordVO);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("validateAllServices", services[j] + " validated " + isServiceValid + ". Added to List");
                }
            } else {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("validateAllServices", services[j] + " validated " + isServiceValid);
                }
            }

        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("validateAllServices", " Exiting with newList.size= " + newList.size());
        }
        return newList;
    }

    /**
     * This method will update Status of services in DB.
     * 
     * @param p_con
     *            Connection
     * @param p_networkDetailsList
     *            ArrayList
     * @return void
     * @throws BTSLBaseException
     */

    private void updateServiceList(Connection p_con, ArrayList p_serviceDetailsList) throws BTSLBaseException {
        final String METHOD_NAME = "updateServiceList";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered..");
        }
        int updateCount = 0;
        try {
            final ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
            final ServiceKeywordVO serviceKeywordVO = null;

            updateCount = serviceKeywordDAO.updateServiceTypeStatus(p_con, p_serviceDetailsList);
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Service Details updateCount = " + updateCount);
            }
            if (updateCount == 0) {
                _logger.error(METHOD_NAME, "Error occured while updating InterfaceDetails in DB through InterfaceVO");
                throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_DB_NOT_UPDATED);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Service Details updated in DB");
            }
        } catch (BTSLBaseException bte) {
            _logger.error(METHOD_NAME, "Error occured while updating Service Details");
            _logger.errorTrace(METHOD_NAME, bte);
            throw bte;
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Error occured while updating Service Details");
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeSuspendProcess[updateServiceList]", "", "", "",
                "Service provided are not valid");
            throw new BTSLBaseException("ResumeSuspendProcess", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION,ex);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting with updateCount =" + updateCount);
            }
        }

    }
}
