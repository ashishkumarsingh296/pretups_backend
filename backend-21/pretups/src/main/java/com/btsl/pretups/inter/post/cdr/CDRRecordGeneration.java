/*
 * CDRRecordGeneration.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Ruwali 24/05/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * This class is used to generate the CDR Record. This class implements
 * CDRRecordGeneratorI
 */

package com.btsl.pretups.inter.post.cdr;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.postqueue.QueueTableVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.util.BTSLUtil;

public class CDRRecordGeneration implements CDRRecordGeneratorI {
    private static Log _log = LogFactory.getLog(CDRRecordGeneration.class.getName());
    private SimpleDateFormat _callDateStr = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat _callTimeStr = new SimpleDateFormat("HHmmss");
    // Values for e-TopUp is defined under the comments below. The number of
    // bytes are indicated under [] at the end
    private static long _fileSequence; // The file sequence Number
    private static long _sequenceNumber; // The first record within a file
                                         // initaizied to '0000000001' [10]
    private String _version; // fix value always 1.0 [3]
    private String _switchType; // fix value always T [1]
    private String _callRecordType;// fix value always 1 [1]
    private String _subscriptionType; // fix value always 1 [1]
    private String _callTerminationType; // fix value always 1 [1]
    private String _callTerminationErrCode; // fix value always 1 [1]
    private String _mobileSubscriberID; // IMSI of the payer from the whitelist
                                       // [15]
    private String _callerMsisdnType; // fix value always 1 [1]
    private String _callerMsisdn; // Msisdn of the payer without country code
                                  // and prefixed with 0 [15]
    private String _callPartnerIdType; // value->1(National) ;
                                       // ->2(International) ; ''-> Not defined
                                       // [1]
    private String _callPartnerID; // Msisdn of the recipient [15]
    private String _basicService;// fix value always 11 means Telephony [2]
    private String _bearerCapability; // fix value is 00(International standard
                                      // code for bearer service) [2]
    private String _callDate; // Transaction date [8]
    private String _callTime; // Transaction Time [6]
    private String _callDuration; // Fix value always 000001 [6]
    private String _mscIDType; // blank space with specified byte size[1:
                               // National, 2:International,'':Not Defined] [1]
    private String _mscID;// blank space with specified byte size [15]
    private String _msLocationType;// blank space with specified byte size [1]
    private String _msLocation;// blank space with specified byte size [18]
    private String _msLocationExtnType;// blank space with specified byte size
                                       // [1]
    private String _msLocationExtn;// blank space with specified byte size [15]
    private String _equipmentID;// blank space with specified byte size [15]
    private String _equipmentStatus;// blank space with specified byte size [1]
    private String _callOrigin;// blank space with specified byte size [1]
    private String _channelType;// blank space with specified byte size [1]
    private String _linkID;// blank space with specify byte size [5 bytes]
    private String _pstnCharge;// For TopUp CDR value is topup amount[tax
                               // excluded] & for txn fee CDR val is txn fee [8
                               // bytes]
    private String _supplServices;// USSD0001[8 bytes]
    private String _outgoingTrunkGrp;// blank space with specified byte size[10]
    private String _incomingTrunkGrp;// blank space with specified byte size[10]
    private String _filler;// blank space with specified byte size [57 bytes]
    // Variables for trailer record
    private String _fillerTrailerStart;// blank space with specified byte size
                                       // [14 bytes]
    private String _recordType; // charcter that denots that the record is a
                                // trailer record val=T [1]
    // File Name Format:: Year-Month-Day-Hour-SequenceNumber [Eg:
    // CDRNM0607181704.DAT], (file extn is also included in size) [19]
    private String _fileName;
    private String _recordCount; // number of records [10]
    private String _fillerTrailerEnd;// blank space with specified byte size
                                     // [212 bytes]
    private String _msisdnPrefix;

    /**
     * Method loadConstants
     * Method to laod all constants from IN files which will be used at the time
     * of CDR generation. It will
     * also be used to load the size of one record form IN file
     * 
     * @param p_con
     *            Connection
     * @param p_interfaceID
     *            String
     * @return void
     */

    public void loadConstants(String p_interfaceID) {
        _sequenceNumber = Long.parseLong(FileCache.getValue(p_interfaceID, "SEQUENCE_NUMBER"));
        _version = FileCache.getValue(p_interfaceID, "VERSION");
        _switchType = FileCache.getValue(p_interfaceID, "SWITCH_TYPE");
        _callRecordType = FileCache.getValue(p_interfaceID, "CALL_RECORD_TYPE");
        _subscriptionType = FileCache.getValue(p_interfaceID, "SUBSCRIPTION_TYPE");
        _callTerminationType = FileCache.getValue(p_interfaceID, "CALL_TERMINATION_TYPE");
        _callTerminationErrCode = FileCache.getValue(p_interfaceID, "CALL_TERMINATION_ERR_CODE");
        _mobileSubscriberID = FileCache.getValue(p_interfaceID, "MOBILE_SUBSCRIBER_ID");
        _callerMsisdnType = FileCache.getValue(p_interfaceID, "CALLER_MSISDN_TYPE");
        _callerMsisdn = FileCache.getValue(p_interfaceID, "CALLER_MSISDN");
        _callPartnerIdType = FileCache.getValue(p_interfaceID, "CALL_PARTNER_ID_TYPE");
        _callPartnerID = FileCache.getValue(p_interfaceID, "CALL_PARTNERID");
        _basicService = FileCache.getValue(p_interfaceID, "BASIC_SERVICE");
        _bearerCapability = FileCache.getValue(p_interfaceID, "BEARER_CAPABILITY");
        _callDate = FileCache.getValue(p_interfaceID, "CALL_DATE");
        _callTime = FileCache.getValue(p_interfaceID, "CALL_TIME");
        _callDuration = FileCache.getValue(p_interfaceID, "CALL_DURATION");
        _mscIDType = FileCache.getValue(p_interfaceID, "MSC_ID_TYPE");
        _mscID = FileCache.getValue(p_interfaceID, "MSC_ID");
        _msLocationType = FileCache.getValue(p_interfaceID, "MS_LOCATION_TYPE");
        _msLocation = FileCache.getValue(p_interfaceID, "MS_LOCATION");
        _msLocationExtnType = FileCache.getValue(p_interfaceID, "MS_LOCATION_EXTN_TYPE");
        _msLocationExtn = FileCache.getValue(p_interfaceID, "MS_LOCATION_EXTN");
        _equipmentID = FileCache.getValue(p_interfaceID, "EQUIPMENT_ID");
        _equipmentStatus = FileCache.getValue(p_interfaceID, "EQUIPMENT_STATUS");
        _callOrigin = FileCache.getValue(p_interfaceID, "CALL_ORIGIN");
        _channelType = FileCache.getValue(p_interfaceID, "CHANNEL_TYPE");
        _linkID = FileCache.getValue(p_interfaceID, "LINK_ID");
        _pstnCharge = FileCache.getValue(p_interfaceID, "PSTN_CHARGE");
        _supplServices = FileCache.getValue(p_interfaceID, "SUPPL_SERVICES");
        _outgoingTrunkGrp = FileCache.getValue(p_interfaceID, "OUTGNG_TRUNK_GRP");
        _incomingTrunkGrp = FileCache.getValue(p_interfaceID, "INCMG_TRUNK_GRP");
        _filler = FileCache.getValue(p_interfaceID, "FILLER");
        _fillerTrailerStart = FileCache.getValue(p_interfaceID, "FILLER_TRAILER_START");
        _recordType = FileCache.getValue(p_interfaceID, "RECORD_TYPE");
        _fileName = FileCache.getValue(p_interfaceID, "FILE_NAME");
        _recordCount = FileCache.getValue(p_interfaceID, "RECORD_COUNT");
        _fillerTrailerEnd = FileCache.getValue(p_interfaceID, "FILLER_TRAILER_END");
        _msisdnPrefix = FileCache.getValue(p_interfaceID, "MSISDN_PREFIX_CDR");
        if (BTSLUtil.isNullString(_msisdnPrefix))
            _msisdnPrefix = new String();
    }

    /**
     * Method generateCDRRecords
     * Method used to generate the CDR records. Get the FILE_SIZE_RECORD from
     * FileCache if it is Y then
     * the file size will depend upon the number of records.If value is 'N' then
     * the number of files
     * generated will depend upon the file size mentioned in MB.
     * 
     * @param p_interfaceID
     *            String
     * @param p_queueVOList
     *            ArrayList
     * @return String[]
     * @throws BTSLBaseException
     */

    public String[] generateCDRRecords(ArrayList p_queueVOList, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generateCDRRecords", "Entered with p_queueVOList= " + p_queueVOList + "p_interfaceID=" + p_interfaceID);

        String fileArr[] = null; // stores the name of the CDR files generated
        ArrayList mainList = new ArrayList();
        int fileSize = 0;
        int recordSize = 0;
        try {
            String fileSizeRecord = FileCache.getValue(p_interfaceID, "FILE_SIZE_RECORD");
            if (BTSLUtil.isNullString(fileSizeRecord))
                throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecords", "FILE_SIZE_RECORD should be Y or N");
            try {
                recordSize = Integer.parseInt(FileCache.getValue(p_interfaceID, "RECORD_SIZE"));
            } catch (Exception e) {
                throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecords", "RECORD_SIZE is invalid in IN file");
            }
            try {
                fileSize = Integer.parseInt(FileCache.getValue(p_interfaceID, "FILE_SIZE"));
                if (fileSize < 1) // the size of the file should be at least 1
                                  // MB
                {
                    throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecords", "FILE_SIZE should be greater than or equal to 1");
                }
            } catch (Exception e) {
                throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecords", "Invalid FILE_SIZE in IN File");
            }
            if (p_queueVOList != null) {
                int queueSize = p_queueVOList.size();
                int maxRecs = 0;
                // if fileSizeRecord='N' then Convert MB into number of
                // records,means if the file size is 1 mb then
                // the maximum number of records in one file is 4096
                if (fileSizeRecord.equals("Y"))
                    maxRecs = fileSize;
                else
                    maxRecs = (fileSize * 1024 * 1024) / recordSize;
                // If the QueueSize>File size defined in IN files split the
                // arraylists..
                if (queueSize > maxRecs) {
                    int arrListCount = queueSize / maxRecs; // It is the number
                                                            // of arraylists
                    if (queueSize % maxRecs > 0)
                        arrListCount = arrListCount + 1;
                    if (_log.isDebugEnabled())
                        _log.debug("generateCDRRecords", "Number of arraylist=" + arrListCount);
                    // This logic will prepare sub arraylists and these lists
                    // will be added in mainList
                    int k = 0;
                    int startElement = 0;
                    int endElement = 0;
                    // ArrayList subList=null;
                    List subList = null;
                    for (int j = 0; j < arrListCount; j++) {
                        startElement = k;
                        endElement = (maxRecs + maxRecs * j) - 1;// If FILE_SIZE
                                                                 // is even
                        if (endElement >= queueSize)
                            endElement = queueSize - 1; // If FILE_SIZE is odd
                        // Prepare the sublists from the mainList
                        subList = p_queueVOList.subList(startElement, endElement + 1);
                        // Add the subLists in the mainList
                        mainList.add(subList);
                        k = endElement + 1;
                    }
                    int mainListSize = mainList.size();
                    fileArr = new String[mainListSize];
                    if (mainListSize > 1) {
                        for (int i = 0; i < mainListSize; i++)
                            fileArr[i] = writeCDRData((List) mainList.get(i), generateFileName(p_interfaceID), p_interfaceID, recordSize);
                    } else {
                        fileArr[0] = writeCDRData(p_queueVOList, generateFileName(p_interfaceID), p_interfaceID, recordSize);
                    }
                } else if (queueSize <= maxRecs) {
                    fileArr = new String[1];
                    fileArr[0] = writeCDRData(p_queueVOList, generateFileName(p_interfaceID), p_interfaceID, recordSize);
                }
            }
        } catch (BTSLBaseException be) {
            if (_log.isDebugEnabled())
                _log.error("generateCDRRecords", " " + be.getMessage());
            throw be;
        } catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.error("generateCDRRecords", " " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecords", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateCDRRecords", "Exited fileArr=" + fileArr);
        }
        return fileArr;
    }

    /**
     * Method generateTrailerRecord
     * Method used to generate the trailer record of CDR File. It consistes of
     * the information about the
     * file. The value will be picked form IN Files and check the specified byte
     * size mentioned in the doc.
     * If the total string size>255 then throw BTSLBase
     * Exception(CDR_TRAILER_RCRD_FORMAT_IMPROPER) and event
     * will be raised.
     * 
     * @param p_fileName
     *            String
     * @param p_numOfRecs
     *            long
     * @return String
     * @throws BTSLBaseException
     */

    public String generateTrailerRecord(String p_fileName, long p_numOfRecs) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generateTrailerRecord", "Entered with p_fileName= " + p_fileName + " p_numOfRecs:" + p_numOfRecs);
        StringBuffer strBuffTrailer = null;
        try {
            strBuffTrailer = new StringBuffer(padCharactersWithValue(' ', _fillerTrailerStart, 14, true));
            strBuffTrailer.append(_recordType);
            strBuffTrailer.append(p_fileName);
            strBuffTrailer.append(padCharactersWithValue('0', String.valueOf(p_numOfRecs), 10, true));
            strBuffTrailer.append(padCharactersWithValue(' ', _fillerTrailerEnd, 212, true));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("CDRRecordGeneration", "generateTrailerRecord", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateTrailerRecord", "Exited strBuffTrailer=" + strBuffTrailer.toString());
        }
        return strBuffTrailer.toString();
    }

    /**
     * Method padCharactersWithValue
     * Method used to pad the specified character against the specified value.
     * The direction
     * of padding is by default left. If boolen value is false then pad the
     * charactrs to the
     * right side of the value.
     * 
     * @param p_paddingChar
     *            char
     * @param p_strValue
     *            String
     * @param p_strLength
     *            int
     * @param p_leftDir
     *            boolean
     * @return String
     * @throws BTSLBaseException
     */

    private String padCharactersWithValue(char p_paddingChar, String p_strValue, int p_strLength, boolean p_leftDir) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("padCharactersWithValue", "Entered with p_strValue= " + p_strValue + " p_strLength:" + p_strLength + "p_leftDir=" + p_leftDir);
        try {
            if (BTSLUtil.isNullString(p_strValue))
                p_strValue = "";
            int cntr = p_strLength - p_strValue.length();
            if (cntr > 0) {
                for (int i = 0; i < cntr; i++) {
                    if (p_leftDir)
                        p_strValue = p_paddingChar + p_strValue;
                    else
                        p_strValue = p_strValue + p_paddingChar;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("CDRRecordGeneration", "padCharactersWithValue", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("padCharactersWithValue", "Exiting with p_strValue= " + p_strValue);
        }
        return p_strValue;
    }

    /**
     * Method generateFileName
     * Method used generate the name of CDR file. File name, initial constant &
     * extension of the file
     * will be picked from IN Files. The file format is YYMMDDHH after constant.
     * 
     * @param p_fileName
     *            String
     * @return String
     * @throws BTSLBaseException
     */

    private String generateFileName(String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generateFileName", "Entered");
        StringBuffer strBuffFileName = null;
        try {
            strBuffFileName = new StringBuffer(FileCache.getValue(p_interfaceID, "INITIAL_FILE_NAME"));
            strBuffFileName.append(FileCache.getValue(p_interfaceID, "SUBSCRIBER_RECORD_TYPE"));
            _fileSequence = _fileSequence + 1;
            // YYMMDDHH format file
            SimpleDateFormat callDate = new SimpleDateFormat("yyMMddHH");
            Date date = Calendar.getInstance().getTime();
            strBuffFileName.append(callDate.format(date)); // Transaction Date
            String fileSeq = padCharactersWithValue('0', String.valueOf(_fileSequence), 2, true);
            strBuffFileName.append(fileSeq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("CDRRecordGeneration", "generateFileName", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateFileName", "Exited fileName=" + strBuffFileName.toString());
        }
        return strBuffFileName.toString();
    }

    /**
     * Method generateCDRRecordString
     * Method used to generate the CDR Record string according to the specified
     * CDR format specified in
     * the CDR DOC. if the total size>Max record size defined in IN File then
     * throw
     * BTSLBase Exception(CDR_RCRD_FORMAT_IMPROPER) and event
     * will be raised.
     * 
     * @param p_fileName
     *            String
     * @return String
     * @throws BTSLBaseException
     */

    private String generateCDRRecordString(QueueTableVO p_queueVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generateCDRRecordString", "Entered with p_queueVO= " + p_queueVO);
        StringBuffer cdrBuff = null;
        try {
            cdrBuff = new StringBuffer(padCharactersWithValue('0', String.valueOf(_sequenceNumber), 10, true));
            cdrBuff.append(_version);
            cdrBuff.append(_switchType);
            cdrBuff.append(_callRecordType);
            cdrBuff.append(_subscriptionType);
            cdrBuff.append(_callTerminationType);
            cdrBuff.append(_callTerminationErrCode);
            cdrBuff.append(p_queueVO.getImsi()); // IMSI of the payer
            cdrBuff.append(_callerMsisdnType);
            cdrBuff.append(padCharactersWithValue(' ', _msisdnPrefix + p_queueVO.getMsisdn(), 15, false)); // MSISDN
                                                                                                           // of
                                                                                                           // payer(without
                                                                                                           // country
                                                                                                           // code
                                                                                                           // &
                                                                                                           // prefixed
                                                                                                           // with
                                                                                                           // 0)
            cdrBuff.append(_callPartnerIdType);
            cdrBuff.append(padCharactersWithValue(' ', _callPartnerID, 15, true)); // MSISDN
                                                                                   // of
                                                                                   // the
                                                                                   // recipient
                                                                                   // ???
            cdrBuff.append(_basicService);
            cdrBuff.append(_bearerCapability);
            // SimpleDateFormat callDate = new SimpleDateFormat("yyyyMMdd");
            // SimpleDateFormat callTime = new SimpleDateFormat("HHmmss");
            Date date = Calendar.getInstance().getTime();
            cdrBuff.append(_callDateStr.format(date)); // Transaction Date
            cdrBuff.append(_callTimeStr.format(date)); // Transaction Time
            cdrBuff.append(_callDuration);
            cdrBuff.append(padCharactersWithValue(' ', _mscIDType, 1, true));
            cdrBuff.append(padCharactersWithValue(' ', _mscID, 15, true));
            cdrBuff.append(padCharactersWithValue(' ', _msLocationType, 1, true));
            cdrBuff.append(padCharactersWithValue(' ', _msLocation, 18, true));
            cdrBuff.append(padCharactersWithValue(' ', _msLocationExtnType, 1, true));
            cdrBuff.append(padCharactersWithValue(' ', _msLocationExtn, 15, true));
            cdrBuff.append(padCharactersWithValue(' ', _equipmentID, 15, true));
            cdrBuff.append(padCharactersWithValue(' ', _equipmentStatus, 1, true));
            cdrBuff.append(padCharactersWithValue(' ', _callOrigin, 1, true));
            cdrBuff.append(padCharactersWithValue(' ', _channelType, 1, true));
            cdrBuff.append(padCharactersWithValue(' ', _linkID, 5, true));
            cdrBuff.append(padCharactersWithValue(' ', p_queueVO.getInterfaceAmountStr(), 8, false)); // Top
                                                                                                      // Up
                                                                                                      // Amount
            cdrBuff.append(padCharactersWithValue(' ', _supplServices, 8, false));
            cdrBuff.append(padCharactersWithValue(' ', _outgoingTrunkGrp, 10, true));
            cdrBuff.append(padCharactersWithValue(' ', _incomingTrunkGrp, 10, true));
            cdrBuff.append(padCharactersWithValue(' ', _filler, 57, true));
            _sequenceNumber = _sequenceNumber + 1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateCDRRecordString", "Exited CDR Record generated is=|" + cdrBuff.toString() + "|");
        }
        return cdrBuff.toString();
    }

    /**
     * Method writeCDRData
     * Method used to generate the CDR Record if the number of files generated
     * depends upon number of records.
     * 
     * @param p_queueVOList
     *            ArrayList
     * @param p_fileName
     *            String
     * @param p_interfaceID
     *            String
     * @param p_recordSize
     *            int
     * @return String
     * @throws BTSLBaseException
     */

    private String writeCDRData(List p_queueVOList, String p_fileName, String p_interfaceID, int p_recordSize) throws BTSLBaseException {
         String methodName="writeCDRData";
    	if (_log.isDebugEnabled())
            _log.debug("writeCDRData", "Entered with p_queueVOList= " + p_queueVOList + " p_queueVOList.size=" + p_queueVOList.size() + "p_fileName=" + p_fileName + "p_interfaceID=" + p_interfaceID + "p_recordSize=" + p_recordSize);
        PrintWriter pw = null;
        File file = null;
        String filePath = null;
        String fullFileName = null;
        String multFactor = null;
        String roundFlag = null;
        int mulFactor = 1;
        try {
            QueueTableVO queueVO = null;
            String cdrStr = null;
            filePath = FileCache.getValue(p_interfaceID, "FILE_PATH");
            fullFileName = p_fileName + "." + FileCache.getValue(p_interfaceID, "FILE_EXTENTION");
            multFactor = FileCache.getValue(p_interfaceID, "MULTIPLICATION_FACTOR");
            roundFlag = FileCache.getValue(p_interfaceID, "ROUND_FLAG");

            if (!BTSLUtil.isNullString(multFactor))
                mulFactor = Integer.parseInt(multFactor);

            file = new File(filePath + fullFileName);
            pw = new PrintWriter(file);
            _sequenceNumber = 1;
            String trailerStr = null;
            Date processDate = new Date();
            if (p_queueVOList != null) {
                int size = p_queueVOList.size();
                for (int i = 0; i < size; i++) {
                    cdrStr = null;
                    queueVO = (QueueTableVO) p_queueVOList.get(i);
                    String amountStr = InterfaceUtil.getDisplayAmount(BTSLUtil.parseDoubleToLong(queueVO.getInterfaceAmount()), mulFactor);
                    double amountDouble = Double.parseDouble(amountStr);
                    if (PretupsI.YES.equals(roundFlag)) {
                        queueVO.setInterfaceAmountStr("" + Math.round(amountDouble));
                    } else {
                        queueVO.setInterfaceAmountStr("" + amountDouble);
                    }

                    cdrStr = generateCDRRecordString(queueVO);
                    if (cdrStr.length() != p_recordSize)
                        throw new BTSLBaseException("CDRRecordGeneration", "writeCDRData", PretupsErrorCodesI.CDR_RCRD_FORMAT_IMPROPER);
                    queueVO.setStatus("2");// Means CDR successfully processed,
                                           // Transaction successful.
                    queueVO.setCdrFileName(p_fileName);
                    queueVO.setProcessID(String.valueOf(_sequenceNumber - 1));
                    queueVO.setProcessDate(processDate);
                    queueVO.setProcessStatus(ProcessI.STATUS_COMPLETE); // Process
                                                                        // status
                                                                        // is
                                                                        // complete
                    pw.println(cdrStr);
                    try {
                        Thread.sleep(25);
                    } catch (Exception exx) {
                    	_log.error(methodName,exx);
                    }
                }
                trailerStr = generateTrailerRecord(fullFileName, size);
                if (trailerStr.length() != p_recordSize)
                    throw new BTSLBaseException("CDRRecordGeneration", "writeCDRData", PretupsErrorCodesI.CDR_TRAILER_RCRD_FORMAT_IMPROPER);
                pw.print(trailerStr);
                pw.flush();
            }
        } catch (BTSLBaseException be) {
            if (_log.isDebugEnabled())
                _log.error("writeCDRData", "BTSLBaseException::" + be.getMessage());
            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception e) {
                	_log.error(methodName,e);
                }
            }
            try {
                if (file != null)
                    file.delete();
            } catch (Exception e) {
            	_log.error(methodName,e);
            }
            throw be;
        } catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.error("writeCDRData", "Exception::" + e.getMessage());
            e.printStackTrace();
            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception ex) {
                	_log.error(methodName,ex);
                }
            }
            try {
                if (file != null)
                    file.delete();
            } catch (Exception ex1) {
            	_log.error(methodName,ex1);
            }
            throw new BTSLBaseException("CDRRecordGeneration", "writeCDRData", "Exception e:" + e.getMessage());
        } finally {
            file = null;
            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception e) {
                	_log.error(methodName,e);
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("writeCDRData", "Exited file Name=" + filePath + fullFileName);
        }
        return filePath + fullFileName;
    }

    /**
     * Method generateHeaderRecord
     * Method used to generate the header record of CDR File. [Used for future
     * reference ]
     * 
     * @param p_headerName
     *            String
     * @param p_numOfRecs
     *            long
     * @return String
     * @throws BTSLBaseException
     */

    public String generateHeaderRecord(String p_headerName, long p_numOfRecords) throws BTSLBaseException {
        return "";
    }
}
