/**
 * MobinilCDRGeneration.java
 * 
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.post.cdr;

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
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.post.cdr.CDRRecordGeneration;
import com.btsl.pretups.inter.post.cdr.CDRRecordGeneratorI;
import com.btsl.pretups.inter.postqueue.QueueTableVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.util.BTSLUtil;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class MobinilCDRGeneration implements CDRRecordGeneratorI {
    private static Log _log = LogFactory.getLog(CDRRecordGeneration.class.getName());
    private SimpleDateFormat _callDateStr = new SimpleDateFormat("yyyyMMddHHmmss");
    // private SimpleDateFormat _callTimeStr = new SimpleDateFormat("HHmmss");
    private long _fileSequence = 0; // The file sequence Number
    private long _sequenceNumber = 1; // The first record within a file
                                      // initaizied to '1'
    private String _cdrFieldSeperator;// ","
    private String _headerName;// = "Header:";
    private Date date = Calendar.getInstance().getTime();

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
        _headerName = FileCache.getValue(p_interfaceID, "CDR_HEADER_NAME");
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
            _fileSequence = 0;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            date = Calendar.getInstance().getTime();
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
            if (p_queueVOList != null && p_queueVOList.size()>0) {
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
     * Method padCharactersWithValue
     * Method used to pad the specified character against the specified value.
     * The direction
     * of padding is by default left. If boolean value is false then pad the
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
     * @param p_interfaceID
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
            // strBuffFileName.append(FileCache.getValue(p_interfaceID,"SUBSCRIBER_RECORD_TYPE"));
            _fileSequence = _fileSequence + 1;
            // YYMMDDHH format file
            SimpleDateFormat callDate = new SimpleDateFormat("yyyyMMddHHmmss");

            strBuffFileName.append(callDate.format(date)); // Transaction Date
            String fileSeq = padCharactersWithValue('0', String.valueOf(_fileSequence), 6, true);
            strBuffFileName.append("_");
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
     * the CDR DOC. if a mandatory field is missing then throw BTSLBase
     * Exception(CDR_RCRD_FORMAT_IMPROPER)
     * and event will be raised.
     * 
     * @param p_queueVO
     *            QueueTableVO
     * @return String
     * @throws BTSLBaseException
     */

    private String generateCDRRecordString(QueueTableVO p_queueVO, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generateCDRRecordString", "Entered with p_queueVO= " + p_queueVO);

        if (BTSLUtil.isNullString(_cdrFieldSeperator))
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Cannot find constant CDR_FIELD_SEPARATOR");
        // null checks for mandatory fields
        if (BTSLUtil.isNullString(p_queueVO.getSenderMsisdn()))
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Mandatory field Sender Msisdn is missing");
        // if (BTSLUtil.isNullString(p_queueVO.getImsi()))
        // throw new
        // BTSLBaseException("CDRRecordGeneration","generateCDRRecordString","Mandatory field getImsi is missing");
        if (BTSLUtil.isNullString(p_queueVO.getReceiverMsisdn()))
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Mandatory field Receiver Msisdn is missing");
        if (BTSLUtil.isNullString(p_queueVO.getType()))
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Mandatory field ServiceType is missing");
        if (p_queueVO.getAmount() < 0)
            throw new BTSLBaseException("CDRRecordGeneration", "generateCDRRecordString", "Mandatory field getAmount() < 0 ");
        StringBuffer cdrBuff = null;
        try {
            String inerfaceID = p_queueVO.getInterfaceID();
            cdrBuff = new StringBuffer(200);
            cdrBuff.append(FileCache.getValue(inerfaceID, "CDRFILE_" + p_queueVO.getType()));
            cdrBuff.append(_cdrFieldSeperator);
            // Date date = Calendar.getInstance().getTime();
            // cdrBuff.append(_callDateStr.format(date)); // Transaction Date
            cdrBuff.append(_callDateStr.format(p_queueVO.getCreatedOn()));
            cdrBuff.append(_cdrFieldSeperator);// Duration would be blank
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(FileCache.getValue(inerfaceID, "CDRFILE_COUNTRY_CODE"));
            cdrBuff.append(InterfaceUtil.getFilterMSISDN(p_interfaceID, p_queueVO.getSenderMsisdn()));
            cdrBuff.append(_cdrFieldSeperator);
            // cdrBuff.append(p_queueVO.getImsi());
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(_cdrFieldSeperator);// UL data volume would be blank
            cdrBuff.append(_cdrFieldSeperator);// DL data volume would be blank
            cdrBuff.append(FileCache.getValue(inerfaceID, "CDRFILE_COUNTRY_CODE"));
            cdrBuff.append(InterfaceUtil.getFilterMSISDN(p_interfaceID, p_queueVO.getReceiverMsisdn()));
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(getDisplayAmount(p_queueVO.getAmount(), Integer.parseInt(FileCache.getValue(inerfaceID, "CDRFILE_MULTIPLICATION_FACTOR"))));
            cdrBuff.append(_cdrFieldSeperator);
            cdrBuff.append(_cdrFieldSeperator);
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
        try {
            QueueTableVO queueVO = null;
            String cdrStr = null;
            filePath = FileCache.getValue(p_interfaceID, "FILE_PATH");
            fullFileName = p_fileName + "." + FileCache.getValue(p_interfaceID, "FILE_EXTENTION");
            file = new File(filePath + fullFileName);
            pw = new PrintWriter(file);
            _sequenceNumber = 1;
            String headerStr = null;
            Date processDate = new Date();
            if (p_queueVOList != null) {
                int size = p_queueVOList.size();
                // Writing Header String in the CDR File
                // headerStr=generateHeaderRecord(_headerName,size);
                // pw.println(headerStr);

                // Writing Records in the CDR File
                for (int i = 0; i < size; i++) {
                    cdrStr = null;
                    queueVO = (QueueTableVO) p_queueVOList.get(i);
                    cdrStr = generateCDRRecordString(queueVO, p_interfaceID);
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
                        pw.print(cdrStr);
                    try {
                        Thread.sleep(25);
                    } catch (Exception exx) {

                    }
                }
                pw.println();
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
        String headerRecord = p_headerName + p_numOfRecords;
        return headerRecord;
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

    public String generateTrailerRecord(String p_fileName, long p_numOfRecs) throws BTSLBaseException {
        // Not implemented for PreTUPS CDR
        return null;
    }

    /**
     * 
     * @param p_amount
     * @param p_multiplicationFactor
     * @return
     */
    public static String getDisplayAmount(long p_amount, int p_multiplicationFactor) {
        if (_log.isDebugEnabled())
            _log.debug("getDisplayAmount", "Entered p_amount:" + p_amount + " MultiplicationFactor " + p_multiplicationFactor);
        double amount = (double) p_amount / (double) p_multiplicationFactor;
        String amountStr = new DecimalFormat("##############.##").format(amount);
        if (_log.isDebugEnabled())
            _log.debug("getDisplayAmount", "Exiting display amount:" + amountStr);
        return amountStr;
    }

}
