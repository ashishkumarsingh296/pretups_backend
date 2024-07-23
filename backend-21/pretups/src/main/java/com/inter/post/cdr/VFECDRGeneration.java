package com.inter.post.cdr;

/**
 * VFECDRGeneration.java
 * Copyright (c) 2012 Comviva Tech Ltd.
 * All Rights Reserved
 * -----------------------------------------------------------------------
 * Name Date History
 * ------------------------------------------------------------------------
 * Rahul Dutt 26/09/2012 Initial Creation
 * ------------------------------------------------------------------------
 * This class is used to generate the CDR Record For VFE. This class implements
 * CDRRecordGeneratorI
 */
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
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
import com.btsl.pretups.inter.post.cdr.CDRRecordGeneration;
import com.btsl.pretups.inter.post.cdr.CDRRecordGeneratorI;
import com.btsl.pretups.inter.postqueue.QueueTableVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class VFECDRGeneration implements CDRRecordGeneratorI {
    private static Log _log = LogFactory.getLog(CDRRecordGeneration.class.getName());
    private SimpleDateFormat _callDateStr = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat _callTimeStr = new SimpleDateFormat("HHmmss");
    private static long _fileSequence; // The file sequence Number
    private static long _sequenceNumber; // The first record within a file
                                         // initaizied to '1'
    private String _cdrFieldSeperator;// ","
    private String _recordType = "001";
    private String _invoiceNum = "0000";
    private String _headerName;// = "Header:";
    private long _totAmount = 0;
    private String _chname;
    private int count = 0;
    private String trailerCont = "";
    private long totRecCount = 0;
    private double totRecAmt = 0;

    /**
     * Method loadConstants
     * Method to load all constants from IN files which will be used at the time
     * of CDR generation. It will
     * also be used to load the size of one record form IN file
     * 
     * @param p_interfaceID
     *            String
     * @return void
     */
    public void loadConstants(String p_interfaceID) {
        _cdrFieldSeperator = FileCache.getValue(p_interfaceID, "CDR_FIELD_SEPARATOR");
        _recordType = FileCache.getValue(p_interfaceID, "RECORD_TYPE");
        _invoiceNum = FileCache.getValue(p_interfaceID, "INVOICE_NUM");
        _headerName = FileCache.getValue(p_interfaceID, "CDR_HEADER_NAME");
        _chname = FileCache.getValue(p_interfaceID, "CHNL_NAME");
        trailerCont = FileCache.getValue(p_interfaceID, "TRLR_CONST");
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
                    throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecords", "FILE_SIZE should be greater than or equal to 1");
            } catch (Exception e) {
                throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecords", "Invalid FILE_SIZE in IN File");
            }
            if (p_queueVOList != null) {
                QueueTableVO queueVO = null;
                String networkCode;
                queueVO = (QueueTableVO) (p_queueVOList.get(0));
                networkCode = queueVO.getNetworkID();
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
                    /*
                     * QueueTableVO queueVO=null;
                     * String networkCode;
                     */
                    if (mainListSize > 1) {
                        for (int i = 0; i < mainListSize; i++) {
                            fileArr[i] = writeCDRData((List) mainList.get(i), generateFileName(p_interfaceID, networkCode), p_interfaceID, recordSize);
                        }
                    } else
                        fileArr[0] = writeCDRData(p_queueVOList, generateFileName(p_interfaceID, networkCode), p_interfaceID, recordSize);
                } else if (queueSize <= maxRecs) {
                    queueVO = (QueueTableVO) (p_queueVOList.get(0));
                    networkCode = queueVO.getNetworkID();
                    fileArr = new String[1];
                    fileArr[0] = writeCDRData(p_queueVOList, generateFileName(p_interfaceID, networkCode), p_interfaceID, recordSize);
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
     * Method generateFileName
     * Method used generate the name of CDR file. File name, initial constant &
     * extension of the file
     * will be picked from IN Files. The file format is YYMMDDHH after constant.
     * 
     * @param p_interfaceID
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    private String generateFileName(String p_interfaceID, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generateFileName", "Entered");
        StringBuffer strBuffFileName = null;
        try {
            Thread.sleep(10);
            strBuffFileName = new StringBuffer();
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat callDateStr = new SimpleDateFormat("yyyyMMdd");
            strBuffFileName.append(callDateStr.format(date) + _callTimeStr.format(date));
            strBuffFileName.append(FileCache.getValue(p_interfaceID, "INITIAL_FILE_NAME"));
            strBuffFileName.append(BTSLUtil.padZeroesToLeft(Integer.toString(count++), 3));
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
     * the CDR DOC. if a mandatory field is missing then throw BTSLBase
     * Exception(CDR_RCRD_FORMAT_IMPROPER)
     * and event will be raised.
     * 
     * @param p_queueVO
     *            QueueTableVO
     * @return String
     * @throws BTSLBaseException
     */
    private String generateCDRRecordString(QueueTableVO p_queueVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generateCDRRecordString", "Entered with p_queueVO= " + p_queueVO);
        if (p_queueVO == null)
            return "";
        // null checks for mandatory fields
        if (BTSLUtil.isNullString(p_queueVO.getSenderMsisdn()))
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Mandatory field Msisdn is missing");
        if (BTSLUtil.isNullString(p_queueVO.getReceiverMsisdn()))
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Mandatory field Msisdn is missing");
        if (p_queueVO.getAmount() < 0)
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Mandatory field getAmount() < 0 ");
        StringBuffer cdrBuff = null;
        try {
            // Date date = Calendar.getInstance().getTime();
            // Transaction date has to be set into the CDR file instead of
            // current date and time.
            Date date = p_queueVO.getCreatedOn();
            cdrBuff = new StringBuffer();
            cdrBuff.append(_recordType);
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(BTSLUtil.padZeroesToLeft(_invoiceNum, 14));
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(BTSLUtil.padZeroesToLeft(p_queueVO.getAccountID(), 20));
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append("            ");
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(BTSLUtil.padZeroesToLeft(PretupsBL.getDisplayAmount(p_queueVO.getAmount()), 16));
            totRecAmt = totRecAmt + Double.parseDouble(PretupsBL.getDisplayAmount(p_queueVO.getAmount()));
            totRecCount++;
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(_callDateStr.format(date));
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(_chname);
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(padSpacesToLeft(p_queueVO.getTransferID(), 20));//
            cdrBuff.append(_cdrFieldSeperator);
            if (p_queueVO.getOwnerID() != null)
                cdrBuff.append(padSpacesToLeft(p_queueVO.getOwnerID(), 40));
            else
                cdrBuff.append(padSpacesToLeft(p_queueVO.getSenderID(), 40));
            /*
             * cdrBuff.append(_cdrFieldSeperator);
             * cdrBuff.append(p_queueVO.getSenderMsisdn());
             * cdrBuff.append(_cdrFieldSeperator);
             * cdrBuff.append(p_queueVO.getReceiverMsisdn());
             * cdrBuff.append(_cdrFieldSeperator);
             * //Instead of current date, now transaction date will be set in to
             * the CDR file.
             * cdrBuff.append(_callDateStr.format(date));
             * cdrBuff.append(_cdrFieldSeperator);
             * //Instead of current time, now transaction time will be set in to
             * the CDR file.
             * cdrBuff.append(_callTimeStr.format(date));
             * cdrBuff.append(_cdrFieldSeperator);
             * cdrBuff.append("Etopup");
             * cdrBuff.append(_cdrFieldSeperator);
             * cdrBuff.append(p_queueVO.getStatus());
             */
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
            multFactor = FileCache.getValue(p_interfaceID, "CDRFILE_MULTIPLICATION_FACTOR");
            roundFlag = FileCache.getValue(p_interfaceID, "ROUND_FLAG");
            // If If multiplication factor is defined in the INFile, then set it
            // in to the mulFactor.
            if (!BTSLUtil.isNullString(multFactor))
                mulFactor = Double.parseDouble(multFactor);
            file = new File(filePath + fullFileName);
            pw = new PrintWriter(file);
            Date processDate = new Date();
            _sequenceNumber = 1;
            String headerStr, trailerStr = null;
            if (p_queueVOList != null) {
                int size = p_queueVOList.size();
                // Writing Header String in the CDR File
                headerStr = generateHeaderRecord(_headerName, size);
                pw.println(headerStr);
                // Writing Records in the CDR File
                for (int i = 0; i < size; i++) {
                    cdrStr = null;
                    queueVO = (QueueTableVO) p_queueVOList.get(i);
                    double amountDouble = queueVO.getInterfaceAmount();// Changed
                                                                       // by
                                                                       // Dhiraj
                                                                       // on
                                                                       // 01/05/2007
                    if (PretupsI.YES.equals(roundFlag))
                        queueVO.setInterfaceAmountStr("" + Math.round(amountDouble * mulFactor));
                    else
                        queueVO.setInterfaceAmountStr("" + (amountDouble * mulFactor));
                    // String
                    // amountStr=InterfaceUtil.getDisplayAmount((long)queueVO.getInterfaceAmount(),mulFactor);
                    // queueVO.setInterfaceAmountStr(amountStr);
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
                    if (i < (size - 1))
                        pw.println(cdrStr);
                    else
                        pw.println(cdrStr);
                    try {
                        Thread.sleep(25);
                    } catch (Exception exx) {
                    }
                }
                trailerStr = generateTrailerRecord(fullFileName, size);
                if (trailerStr != null)
                    pw.println(trailerStr);
            }
            totRecCount = 0;
            totRecAmt = 0;
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
     * Method used to generate the header record of CDR File. It consists of
     * HeaderName and number of records.
     * 
     * @param p_headerName
     *            String
     * @param p_numOfRecs
     *            long
     * @return String
     * @throws BTSLBaseException
     */
    public String generateHeaderRecord(String p_headerName, long p_numOfRecords) throws BTSLBaseException {
        return p_headerName;
    }

    /**
     * Method generateTrailerRecord
     * Not implemented for PreTUPS CDR
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
    public String generateTrailerRecord(String p_totamount, long p_numOfRecs) throws BTSLBaseException {
        return trailerCont + BTSLUtil.padZeroesToLeft(Double.toString(totRecAmt), 16) + BTSLUtil.padZeroesToLeft(Long.toString(totRecCount), 6);
    }

    private String padSpacesToLeft(String p_strValue, int p_strLength) {
        if (_log.isDebugEnabled())
            _log.debug("padZeroesToLeft()", "Entered with p_strValue= " + p_strValue + " p_strLength:" + p_strLength);
        int cntr = p_strLength - p_strValue.length();
        if (cntr > 0) {
            for (int i = 0; i < cntr; i++) {
                p_strValue = " " + p_strValue;
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("padZeroesToLeft()", "Exiting with p_strValue= " + p_strValue);
        return p_strValue;
    }

}
