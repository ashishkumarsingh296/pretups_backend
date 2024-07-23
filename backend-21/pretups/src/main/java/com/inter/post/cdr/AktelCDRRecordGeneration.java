/*
 * AktelCDRRecordGeneration.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Dhiraj Tiwari 20/04/2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * This class is used to generate the CDR Record. This class implements
 * CDRRecordGeneratorI
 */

package com.inter.post.cdr;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.post.cdr.CDRRecordGeneratorI;
import com.btsl.pretups.inter.postqueue.QueueTableVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.util.BTSLUtil;

public class AktelCDRRecordGeneration implements CDRRecordGeneratorI {
    private static Log _log = LogFactory.getLog(AktelCDRRecordGeneration.class.getName());
    private static long _sequenceNumber;
    private String _dateFormatFileName;
    private String _dateFormatInCDR;
    private String _delimeter;
    private String _comments;
    private SimpleDateFormat _callDateTimeFrmt;
    private String _interfaceID = null;

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
        _interfaceID = p_interfaceID;
        _sequenceNumber = Long.parseLong(FileCache.getValue(p_interfaceID, "SEQUENCE_NUMBER"));
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

            String fileCreationSleepTimeStr = FileCache.getValue(p_interfaceID, "FILE_CREATE_SLEEP_TIME");
            long fileCreationSleepTime = 60000;
            if (!BTSLUtil.isNullString(fileCreationSleepTimeStr) && BTSLUtil.isNumeric(fileCreationSleepTimeStr))
                fileCreationSleepTime = Long.parseLong(fileCreationSleepTimeStr);

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
                        for (int i = 0; i < mainListSize; i++) {
                            fileArr[i] = writeCDRData((List) mainList.get(i), generateFileName(p_interfaceID), p_interfaceID, recordSize);
                            // Below sleep is added to avoid overwrite of the
                            // previous file if there are more than one files to
                            // be generated and file name is upto the minute.
                            if (_log.isDebugEnabled())
                                _log.debug("processIntervals", "Process will continue after " + fileCreationSleepTime + "milliseconds to avoid overwriting");
                            Thread.sleep(fileCreationSleepTime);
                        }
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
        return "";
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
            _dateFormatFileName = FileCache.getValue(p_interfaceID, "DATE_FORMAT_FILE_NAME");
            strBuffFileName = new StringBuffer(FileCache.getValue(p_interfaceID, "INITIAL_FILE_NAME"));
            SimpleDateFormat callDate = new SimpleDateFormat(_dateFormatFileName);
            Date date = Calendar.getInstance().getTime();
            strBuffFileName.append(callDate.format(date)); // Transaction Date
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("CDRRecordGeneration", "generateFileName", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateFileName", "Exited fileName=" + strBuffFileName.toString());
        }
        return strBuffFileName.toString();// POST2PRE_0704162145
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
            cdrBuff = new StringBuffer(p_queueVO.getAccountID());
            cdrBuff.append(_delimeter);
            cdrBuff.append(InterfaceUtil.getFilterMSISDN(_interfaceID, p_queueVO.getSenderMsisdn()));
            cdrBuff.append(_delimeter);
            cdrBuff.append(InterfaceUtil.getFilterMSISDN(_interfaceID, p_queueVO.getReceiverMsisdn()));
            cdrBuff.append(_delimeter);
            cdrBuff.append(p_queueVO.getInterfaceAmountStr());
            cdrBuff.append(_delimeter);
            cdrBuff.append(p_queueVO.getTransferID());
            cdrBuff.append(_delimeter);
            cdrBuff.append(_callDateTimeFrmt.format(p_queueVO.getCreatedOn()));
            cdrBuff.append(_delimeter);
            cdrBuff.append(_comments);
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
        if (_log.isDebugEnabled())
            _log.debug("writeCDRData", "Entered with p_queueVOList= " + p_queueVOList + " p_queueVOList.size=" + p_queueVOList.size() + "p_fileName=" + p_fileName + "p_interfaceID=" + p_interfaceID + "p_recordSize=" + p_recordSize);
        PrintWriter pw = null;
        File file = null;
        String filePath = null;
        String fullFileName = null;
        String multFactor = null;
        String roundFlag = null;
        double mulFactor = 1;
        try {
            QueueTableVO queueVO = null;
            String cdrStr = null;
            filePath = FileCache.getValue(p_interfaceID, "FILE_PATH");
            fullFileName = p_fileName + "." + FileCache.getValue(p_interfaceID, "FILE_EXTENTION");
            multFactor = FileCache.getValue(p_interfaceID, "MULTIPLICATION_FACTOR");
            roundFlag = FileCache.getValue(p_interfaceID, "ROUND_FLAG");
            _dateFormatInCDR = FileCache.getValue(p_interfaceID, "CDR_REC_DATE_FRMT");
            _comments = FileCache.getValue(p_interfaceID, "COMMENTS");
            _delimeter = FileCache.getValue(p_interfaceID, "REC_DELIMETER");
            if (_log.isDebugEnabled())
                _log.debug("writeCDRData", "Entered with _dateFormatInCDR= " + _dateFormatInCDR);
            _callDateTimeFrmt = new SimpleDateFormat(_dateFormatInCDR);

            if (!BTSLUtil.isNullString(multFactor))
                mulFactor = Double.parseDouble(multFactor);
            // mulFactor = Integer.parseInt(multFactor);

            file = new File(filePath + fullFileName);
            pw = new PrintWriter(file);
            _sequenceNumber = 1;
            Date processDate = new Date();
            if (p_queueVOList != null) {
                int size = p_queueVOList.size();
                for (int i = 0; i < size; i++) {
                    cdrStr = null;
                    queueVO = (QueueTableVO) p_queueVOList.get(i);
                    // String
                    // amountStr=InterfaceUtil.getDisplayAmount((long)queueVO.getInterfaceAmount(),mulFactor);
                    /*
                     * String amountStr=String.valueOf(InterfaceUtil.
                     * getINAmountFromSystemAmountToIN
                     * (queueVO.getInterfaceAmount(),mulFactor));
                     * double amountDouble = Double.parseDouble(amountStr);
                     */
                    double amountDouble = queueVO.getInterfaceAmount();// Changed
                                                                       // by
                                                                       // Dhiraj
                                                                       // on
                                                                       // 01/05/2007
                    if (PretupsI.YES.equals(roundFlag)) {
                        queueVO.setInterfaceAmountStr("" + Math.round(amountDouble));
                    } else {
                        queueVO.setInterfaceAmountStr("" + amountDouble);
                    }

                    cdrStr = generateCDRRecordString(queueVO);

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

                    }
                }
                pw.flush();
            }
        } catch (BTSLBaseException be) {
            if (_log.isDebugEnabled())
                _log.error("writeCDRData", "BTSLBaseException::" + be.getMessage());
            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception e) {
                }
            }
            try {
                if (file != null)
                    file.delete();
            } catch (Exception e) {
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
                }
            }
            try {
                if (file != null)
                    file.delete();
            } catch (Exception ex1) {
            }
            throw new BTSLBaseException("CDRRecordGeneration", "writeCDRData", "Exception e:" + e.getMessage());
        } finally {
            file = null;
            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception e) {
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
