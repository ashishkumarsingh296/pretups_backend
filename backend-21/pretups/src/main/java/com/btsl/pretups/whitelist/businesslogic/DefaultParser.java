package com.btsl.pretups.whitelist.businesslogic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * @(#)DefaultParser.java
 *                        Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ashish Kumar May 16,2006 Initial Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 *                        This class is operator specific and provide the
 *                        following functionalities.
 *                        1.Contains the operator specific informations for
 *                        example
 *                        a. Numbers of columns in record
 *                        b. Sequence of columns in the White List File
 *                        c. Deleminator of White list file
 *                        2.Validation of each record and set error code if
 *                        validation fails.
 *                        3.Provide the implementation of data conversion that
 *                        is to be inserted in the white list.
 *                        a.Amount
 *                        b.MSISDN to Filtered MSISDN etc.
 *                        4.Contains the information about the default values
 *                        for optional coloumn
 * 
 */
public class DefaultParser implements ParserI {
    // private String _NETWORK_CODE="0";
    private int _msisdn; // Used to define the column position of MSISDN based
                         // on the column format.
    private int _accountStatus;// Used to define the column position of
                               // ACCOUNT_STATUS based on the column format.
    private int _creditLimit;// Used to define the column position of
                             // CREDIT_LIMIT based on the column format.
    private int _imsi;// Used to define the column position of IMSI based on the
                      // column format.
    private int _accountID;// Used to define the column position of ACCOUNT_ID
                           // based on the column format.
    private int _serviceClass;// Used to define the column position of
                              // SERVICE_CLASS based on the column format.
    private int _movementCode;// Used to define the column position of
                              // MOVEMENT_CODE based on the column format.
    private int _numberOfColumns; // Used to define the maximum number of column
                                  // exist in white list file.
    private boolean _movementCodeGiven; // While processing of white list record
                                        // this flag is used to decide whether
                                        // the movement code is given or not.
    private String _networkCode; // Network code for the file.
    private String _interfaceID;// InterfaceID for the file.
    private String _externalInterfaceID;// External Interface for the file.
    private String _language;// Used for the Language and its default value is
                             // picked up from the SYSTEM_PREFERENCE.
    private String _country;// Used for the Country and its default value is
                            // picked up from the SYSTEM_PREFERENCE.
    private String _delete;// This is used to match the movement code(Delete) of
                           // record and defined in INFile.
    private String _insert;// This is used to match the movement code(Insert) of
                           // record and defined in INFile.
    private String _update;// This is used to match the movement code(Update) of
                           // record and defined in INFile.
    private int _imsiLength;// Defines the mximum length of the IMSI.
    private int _multFactor;// Defined the multiplication factor for the amount.
    private Date _date = new Date();
    private String _delimiter;// Defines the delimeter by which the columns of
                              // record will be separated.
    private String _columnFormat;// Defines the format(sequence of columns) by
                                 // which the record will appear in white list
                                 // file.
    private ArrayList _columnFormatList; // Used to store the column positions.
    private HashMap _serviceClassMap; // Contains the service class mapping,and
                                      // key of hash map is "Networkcode:
    private ProcessBL _processBL;
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Contstructor to initialize the default language and country
     * 
     */
    public DefaultParser() {
        _language = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
        _country = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
        _processBL = new ProcessBL();
        if (_log.isDebugEnabled()) {
            _log.debug("DefaultParser", "Default _language = " + _language + " _country = " + _country);
        }
    }

    /**
     * This method is to load the network List that status is not deleted and
     * includes following.
     * 1.Internally calls the method initializeINParams.
     * 2.Internally calls the method initializeColumnFormat
     * 
     * @param HashMap
     *            p_netInterfaceServiceMap
     * @throws BTSLBaseException
     */
    public void init(HashMap p_netInterfaceServiceMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("init", "Entered p_netInterfaceServiceMap = " + p_netInterfaceServiceMap);
        }
        // Initialize all the class variable each time when init method is
        // called for each input file.
        _networkCode = null;// Initialize the
        _interfaceID = null;
        _externalInterfaceID = null;
        final String METHOD_NAME = "init";
        try {
            // Get the network code
            _networkCode = (String) p_netInterfaceServiceMap.get("NETWORK_CODE");
            // Get the interfaceID
            _interfaceID = (String) p_netInterfaceServiceMap.get("INTERFACE_ID");

            // Get the external interfaceID
            _externalInterfaceID = (String) p_netInterfaceServiceMap.get("EXT_INTERFACE_ID");
            // Get the service class map.
            _serviceClassMap = (HashMap) p_netInterfaceServiceMap.get("SERVICE_CLASS_MAP");

            if (_log.isDebugEnabled()) {
                _log.debug("init", " _networkCode= " + _networkCode + " _interfaceID = " + _interfaceID + " _externalInterfaceID = " + _externalInterfaceID + " _serviceClassMap = " + _serviceClassMap);
            }

            // After getting the interface ID initialize the IN parameter and
            // column format
            // initialize INParameters
            initializeINParams();
            // Initialize the position of columns
            initializeColumnFormat();
        }// end of try-block
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("init", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[init]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "init", e.getMessage());

        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("init", "Exited");
            }
        }// end of finally
    }// end of init

    /**
     * This mehtod is used to initialize INParameters.
     * 
     * @throws BTSLBaseException
     */
    private void initializeINParams() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("initializeINParams", "Entered");
        }
        final String METHOD_NAME = "initializeINParams";
        try {
            try {
                // Getting the maximum allowed length of IMSI and check whether
                // it is defined or not
                _imsiLength = Integer.parseInt(FileCache.getValue(_interfaceID, "IMSI_LENGTH") == null || FileCache.getValue(_interfaceID, "IMSI_LENGTH").equals("") ? "0" : FileCache.getValue(_interfaceID, "IMSI_LENGTH"));
                if (_imsiLength == 0) {
                    throw new BTSLBaseException(this, "initializeINParams", PretupsErrorCodesI.WLIST_ERROR_IMSI_LENGTH_NULL);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(this, "initializeINParams", e.getMessage());
            }
            // Getting the Movement code of Insert and checks its value.
            _insert = FileCache.getValue(_interfaceID, "MCODE_INSERT");
            if (_insert == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeINParams]", "Parameters of INFile", "", "", "MCODE_INSERT key is not found in INFile");
                throw new BTSLBaseException(this, "initializeINParams", PretupsErrorCodesI.WLIST_ERROR_MCODE_INSERT_NULL);
            }
            // Getting the movement code of delete and checks its value.
            _delete = FileCache.getValue(_interfaceID, "MCODE_DELETE");
            if (_delete == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeINParams]", "Parameters of INFile", "", "", "MCODE_DELETE key is not found in INFile");
                throw new BTSLBaseException(this, "initializeINParams", PretupsErrorCodesI.WLIST_ERROR_MCODE_DELETE_NULL);
            }
            // Getting the movement code of update and check its value.
            _update = FileCache.getValue(_interfaceID, "MCODE_UPDATE");
            if (_update == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeINParams]", "Parameters of INFile", "", "", "MCODE_UPDATE key is not found in INFile");
                throw new BTSLBaseException(this, "initializeINParams", PretupsErrorCodesI.WLIST_ERROR_MCODE_UPDATE_NULL);
            }
            // Getting the delimiter by which column's of record is separated in
            // white list
            _delimiter = FileCache.getValue(_interfaceID, "DELIMITER");
            if (_delimiter == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeINParams]", "Parameters of INFile", "", "", "DELIMITER  is not found in INFile");
                throw new BTSLBaseException(this, "initializeINParams", PretupsErrorCodesI.WLIST_ERROR_DELIMITER_NULL);
            }
            // Getting the format of record(sequence of columns)
            _columnFormat = FileCache.getValue(_interfaceID, "FORMAT");
            if (_columnFormat == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeINParams]", "Parameters of INFile", "", "", "Column format is not defined in INFile");
                throw new BTSLBaseException(this, "initializeINParams", PretupsErrorCodesI.WLIST_ERROR_FORMAT_NULL);
            }
            try {
                // Getting the multiplication factor by which the amount will be
                // multiplied.
                _multFactor = Integer.parseInt(FileCache.getValue(_interfaceID, "MUL_FACTOR"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeINParams]", "Parameters of INFile", "", "", "Multiple factor shoud be numeric");
                throw new BTSLBaseException(this, "initializeINParams", PretupsErrorCodesI.WLIST_ERROR_MUL_FACTOR_NOT_NUMERIC);
            }
            try {
                // Getting the mximum number of columns will be present in the
                // white list record.
                _numberOfColumns = Integer.parseInt(FileCache.getValue(_interfaceID, "COLUMN_NUMBERS"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeINParams]", "Parameters of INFile", "", "", "Number of columns factor shoud be numeric");
                throw new BTSLBaseException(this, "initializeINParams", PretupsErrorCodesI.WLIST_ERROR_COL_NUM_NOT_NUMERIC);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("initializeINParams", "_insert =" + _insert + " _delete = " + _delete + " _update" + _update + " _delimiter" + _delimiter + " _columnFormat = " + _columnFormat);
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("initializeINParams", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("initializeINParams", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeINParams]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "initializeINParams", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("initializeINParams", "Exited");
            }
        }// end of finally
    }// end of initializeINParams

    /**
     * This method is used to initialize column format.
     * 1.Initializes the position of column that will be present in white list.
     * 
     * @throws BTSLBaseException
     */
    private void initializeColumnFormat() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("initializeColomnFormat", "Entered");
        }
        final String METHOD_NAME = "initializeColumnFormat";
        _columnFormatList = new ArrayList(); // Contains the sequence of columns
                                             // for white list record.
        try {
            // For some special character ($,- etc..) we have to concatenate \\
            // with the delimiter
            String[] tempArray = _columnFormat.split("\\" + _delimiter);
            for (int i = 0, l = tempArray.length; i < l; i++) {
                _columnFormatList.add(tempArray[i]);
            }
            try {
                _msisdn = _columnFormatList.indexOf("MSISDN");
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeColumnFormat]", "Format of column", "Invalid column position", "", "Position of msisdn in column format is not found");
                throw new BTSLBaseException(this, "initializeColumnFormat", PretupsErrorCodesI.WLIST_ERROR_FORMAT_MSISDN);
            }
            try {
                _accountStatus = _columnFormatList.indexOf("ACCOUNT_STATUS");
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeColumnFormat]", "Format of column", "Invalid column position", "", "Position of account_status in column format is not found");
                throw new BTSLBaseException(this, "initializeColumnFormat", PretupsErrorCodesI.WLIST_ERROR_FORMAT_ACCOUNT_STATUS);
            }
            try {
                _creditLimit = _columnFormatList.indexOf("CREDIT_LIMIT");
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeColumnFormat]", "Format of column", "Invalid column position", "", "Position of credit_limit in column format is not found");
                throw new BTSLBaseException(this, "initializeColumnFormat", PretupsErrorCodesI.WLIST_ERROR_FORMAT_CREDIT_LIMIT);
            }
            try {
                _imsi = _columnFormatList.indexOf("IMSI");
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeColumnFormat]", "Format of column", "Invalid column position", "", "Position of imsi in column format is not found");
                throw new BTSLBaseException(this, "initializeColumnFormat", PretupsErrorCodesI.WLIST_ERROR_FORMAT_IMSI);
            }
            try {
                _accountID = _columnFormatList.indexOf("ACCOUNT_ID");
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeColumnFormat]", "Format of column", "Invalid column position", "", "Position of account_id in column format is not found");
                throw new BTSLBaseException(this, "initializeColumnFormat", PretupsErrorCodesI.WLIST_ERROR_FORMAT_ACCOUNT_ID);
            }
            try {
                _serviceClass = _columnFormatList.indexOf("SERVICE_CLASS");
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeColumnFormat]", "Format of column", "Invalid column position", "", "Position of service_class in column format is not found");
                throw new BTSLBaseException(this, "initializeColumnFormat", PretupsErrorCodesI.WLIST_ERROR_FORMAT_SERVICE_CLASS);
            }
            try {
                _movementCode = _columnFormatList.indexOf("MOVEMENT_CODE");
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeColumnFormat]", "Format of column", "Invalid column position", "", "Position of movement_code in column format is not found");
                throw new BTSLBaseException(this, "initializeColumnFormat", PretupsErrorCodesI.WLIST_ERROR_FORMAT_MOVEMNET_CODE);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("initializeColumnFormat", "Position of :: MSISDN = " + _msisdn + " ACCOUNT_STATUS = " + _accountStatus + "  CREDIT_LIMIT = " + _creditLimit + " IMSI = " + _imsi + " ACCOUNT_ID = " + _accountID + " SERVICE_CLASS = " + _serviceClass + " MOVEMENT_CODE = " + _movementCode);
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("initializeColumnFormat", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("initializeColumnFormat", "Ecxeption e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[initializeColumnFormat]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "initializeColumnFormat", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("initializeColumnFormat", "Exited");
            }
        }// end of finally
    }// end of initializeColumnFormat

    /**
     * 
     * 1.Accept record as string and responsible to generate white_list VO.
     * 2.Internally calls validateWhiteListInfo method to validate the columns
     * of record
     * 3.Internally calls convertData method, that is converts input columns
     * into database specific that has to inserted into data store.
     * 
     * @param WhiteListVO
     *            p_wListVO
     * @return void
     * @throws BTSLBaseException
     * 
     */
    public void generateWhiteListVO(WhiteListVO p_wListVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("generateWhiteListVO", "Entered wListVO =" + p_wListVO);
        }
        final String METHOD_NAME = "generateWhiteListVO";
        String[] columnValue;
        String reqStr = null;
        int recordSize = 0;
        try {
            // store the column value that is separated by deleminator into
            // string array
            if (_log.isDebugEnabled()) {
                _log.debug("generateWhiteListVO", "Requested string = " + p_wListVO.getRequestString());
            }
            // columnValue =
            // BTSLUtil.split(p_wListVO.getRequestString(),"\\"+_delimiter);
            reqStr = p_wListVO.getRequestString();
            if (reqStr == null) {
                throw new BTSLBaseException(this, "generateWhiteListVO", "record is null");
            }
            columnValue = reqStr.split("\\" + _delimiter);
            recordSize = columnValue.length;

            // Check whether the last column has any momnet code, if not then
            // set the optional 'INS'
            if (_insert.equalsIgnoreCase(columnValue[recordSize - 1]) || _update.equalsIgnoreCase(columnValue[recordSize - 1]) || _delete.equalsIgnoreCase(columnValue[recordSize - 1])) {
                _movementCodeGiven = true;
            } else {
                _movementCodeGiven = false;
            }
            if (columnValue.length == 0) {
                throw new BTSLBaseException(this, "generateWhiteListVO", PretupsErrorCodesI.WLIST_ERROR_NO_COLUMN);
            }

            // Check the number of columns for the record
            if (recordSize != _numberOfColumns) {
                throw new BTSLBaseException(this, "generateWhiteListVO", PretupsErrorCodesI.WLIST_ERROR_INVALID_COL_NUMBER);
            }

            // Remove the blank space if any column associates
            for (int i = 0, j = columnValue.length; i < j; i++) {
                columnValue[i] = columnValue[i].trim();
            }

            // Populate the whiteListVO
            populateWhiteListVO(columnValue, p_wListVO);

            // validate the whiteListVO
            validateWhiteListVO(p_wListVO);
        }// end of try block
        catch (BTSLBaseException be) {
            p_wListVO.setErrorCode(be.getMessage());
            _log.error("generateWhiteListVO", "BTSLBaseException be=" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateWhiteListVO", "Exception be=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[generateWhiteListVO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "generateWhiteListVO", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateWhiteListVO", "Exited wListVO = " + p_wListVO);
            }
        }// end of finally
    }// end of generateWhiteListVO

    /**
     * This method is used to populate the whiteListVO fied values from the
     * string array.
     * 
     * @param String
     *            [] p_columnValue
     * @param WhiteListVO
     *            p_wListVO
     * @throws BTSLBaseException
     */
    private void populateWhiteListVO(String[] p_columnValue, WhiteListVO p_wListVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("populateWhiteListVO", "Entered");
        }
        final String METHOD_NAME = "populateWhiteListVO";
        try {
            p_wListVO.setMsisdn(PretupsBL.getFilteredMSISDN(p_columnValue[_msisdn]));
            p_wListVO.setAccountStatus(p_columnValue[_accountStatus]);
            p_wListVO.setCreditLimitStr(p_columnValue[_creditLimit]);
            p_wListVO.setImsi(p_columnValue[_imsi]);
            p_wListVO.setAccountID(p_columnValue[_accountID]);

            if (_serviceClass != -1) {
                p_wListVO.setServiceClassCode(p_columnValue[_serviceClass]);
            }

            // Check whether the movement code is given in the record,if not set
            // the insert as default.
            if (_movementCodeGiven) {
                p_wListVO.setMovementCode(p_columnValue[_movementCode]);
            } else {
                p_wListVO.setMovementCode(_insert);
            }
            p_wListVO.setNetworkCode(_networkCode);
            p_wListVO.setExternalInterfaceCode(_externalInterfaceID);
            p_wListVO.setInterfaceID(_interfaceID);
            p_wListVO.setLanguage(_language);
            p_wListVO.setCountry(_country);
            p_wListVO.setDelete(_delete);
            p_wListVO.setInsert(_insert);
            p_wListVO.setUpdate(_update);
            p_wListVO.setEntryDate(_date);
            p_wListVO.setCreatedOn(_date);
            p_wListVO.setCreatedBy(PretupsI.SYSTEM_USER);
            p_wListVO.setModifiedOn(_date);
            p_wListVO.setModifiedBy(PretupsI.SYSTEM_USER);
            p_wListVO.setActivatedOn(_date);
            p_wListVO.setActivatedBy(PretupsI.SYSTEM_USER);
            p_wListVO.setStatus(PretupsI.STATUS_ACTIVE);
        }// end of try block
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("populateWhiteListVO", "Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[populateWhiteListVO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "populateWhiteListVO", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("populateWhiteListVO", "Exited p_wListVO = " + p_wListVO);
            }
        }// end of finally
    }// end of populateWhiteListVO

    /**
     * This method is used to convert the amount into actual ammount by
     * multiplying the amounts to multiplication factor
     * 
     * @param String
     *            p_creditLimitStr
     * @throws BTSLBaseException
     */
    private String getActualAmount(String p_creditLimitStr) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getActualAmount", "Entered p_creditLimitStr=" + p_creditLimitStr);
        }
        final String METHOD_NAME = "getActualAmount";
        String actualAmount = null;
        long amount = 0;
        try {
            double amt = Double.parseDouble(p_creditLimitStr);
            amount = BTSLUtil.parseDoubleToLong((amt * _multFactor));
            actualAmount = String.valueOf(amount);
        }// end of try block
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getActualAmount", "Exception e" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[getActualAmount]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getActualAmount", e.getMessage());
        }// end of Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getActualAmount", "Exiting amount:" + amount);
            }
        }// end of finally
        return actualAmount;
    }// end of getActualAmount

    /**
     * This method is used to validate the whiteList information.And set the
     * optional values if required
     * 1.MSISDN validation
     * 2.Ammount validation
     * 3.Check the existance of AccountID if not found set it to MSISDN
     * 4.Check the existance of Profile(service class0 if not found set it to
     * default 'ALL'
     * 
     * @param WhiteListVO
     *            p_wListVO
     * @throws BTSLBaseException
     */
    private void validateWhiteListVO(WhiteListVO p_wListVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateWhiteListVO", "Entered p_wListVO = " + p_wListVO);
        }
        final String METHOD_NAME = "validateWhiteListVO";
        NetworkPrefixVO networkPrefixVO = null;
        try {
            // Validate the IMSI length
            if (!BTSLUtil.NullToString(p_wListVO.getImsi()).equals(p_wListVO.getImsi())) {
                int length = p_wListVO.getImsi().length();
                if (length != _imsiLength) {
                    _log.error("validateWhiteListVO", "Invalid IMSI Length = " + length + " required Length = " + _imsiLength);
                    throw new BTSLBaseException(this, "validateWhiteListVO", PretupsErrorCodesI.WLIST_ERROR_INVALID_IMSI_LENGTH);
                }
            }
            // Validate the mobile number if valid set it to as filtered and if
            // not throw exception with information
            if (BTSLUtil.isValidMSISDN(p_wListVO.getMsisdn())) {
                p_wListVO.setMsisdn(PretupsBL.getFilteredMSISDN(p_wListVO.getMsisdn()));
            } else {
                throw new BTSLBaseException(this, "validateWhiteListVO", PretupsErrorCodesI.WLIST_ERROR_INVALID_MSISDN);
            }
            // Validate the msisdn prefix whether it exist in the specified
            // network or not.
            String msisdnPrefix = PretupsBL.getMSISDNPrefix(p_wListVO.getMsisdn());
            // networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);
            // only postpaid series no can be uploaded.
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, PretupsI.SERIES_TYPE_POSTPAID);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException(this, "validateWhiteListVO", PretupsErrorCodesI.WLIST_ERROR_MSISDN_PREFIX);
            }

            // Validate the amount, set it to as actual if it is valid and throw
            // exception if it is invalid
            if (BTSLUtil.isValidAmount(p_wListVO.getCreditLimitStr())) {
                p_wListVO.setCreditLimitStr(getActualAmount(p_wListVO.getCreditLimitStr()));
            } else {
                throw new BTSLBaseException(this, "validateWhiteListVO", PretupsErrorCodesI.WLIST_ERROR_INVALID_AMOUNT);
            }

            // Validate the AccountID if it is not provided set it as the MSISDN
            // if(BTSLUtil.NullToString(p_wListVO.getAccountID()).equals(p_wListVO.getAccountID()))
            if (BTSLUtil.isNullString(p_wListVO.getAccountID())) {
                p_wListVO.setAccountID(p_wListVO.getMsisdn());
            }

            // Validate the Profile of Subscriber,if not provided set it to
            // default 'ALL'
            // if(BTSLUtil.NullToString(p_wListVO.getServiceClassCode()).equals(p_wListVO.getServiceClassCode()))
            if (BTSLUtil.isNullString(p_wListVO.getServiceClassCode())) {
                p_wListVO.setServiceClassCode(PretupsI.ALL);
            } else {
                _processBL.validateServiceClass(p_wListVO.getServiceClassCode(), _networkCode, _interfaceID, _serviceClassMap);
            }

            // Validate the Movement code if it does not match with movement
            // code described in the INFile mark invalid record.
            // if(!_insert.equals(p_wListVO.getMovementCode())||!_delete.equals(p_wListVO.getMovementCode()||_update.equals(p_wListVO.getMovementCode()));

        }// end of try block
        catch (BTSLBaseException be) {
            // Set the errorCode and Information
            p_wListVO.setErrorCode(be.getMessage());
            _log.error("validateWhiteListVO", "BTSLBaseException be=" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateWhiteListVO", "Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DefaultParser[validateWhiteListVO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateWhiteListVO", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validateWhiteListVO", "Exited");
            }
        }// end of finally
    }

    /**
     * This method will be implemented as per requirement
     */
    public HashMap parseHeaderInformation(String p_headerStr) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseHeaderInformation", "Entered p_headerStr = " + p_headerStr);
        }
        HashMap headerMap = new HashMap();

        return headerMap;
    }// end of parseHeaderInformation
}
