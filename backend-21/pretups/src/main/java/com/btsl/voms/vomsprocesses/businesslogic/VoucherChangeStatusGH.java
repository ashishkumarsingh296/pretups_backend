package com.btsl.voms.vomsprocesses.businesslogic;

/**
 * @(#)VoucherLoaderProcess.java
 *                               Copyright(c) 2014, Comviva Int. Public Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Vipan 08/04/15 Created
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 */

import java.io.File;
import java.io.FilenameFilter;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;

public class VoucherChangeStatusGH {
    private static final Log _log = LogFactory.getLog(VoucherChangeStatusGH.class.getName());

    private String _filePath; // File path name
    private ArrayList _fileList; // Contain all the file object thats name start
                                 // with file prefix.

    private File _inputFile; // File object for input file
    private File _destFile; // Destination File object
    private String _moveLocation; // Input file is move to this location After
                                  // successfull proccessing
    private String _fileExt = "txt"; // Files are picked up only this extention
                                     // from the specified directory
    private static Date _currentDate = null;
    private String _fileName = null;

    public VoucherChangeStatusGH() {
        _currentDate = new Date();
    }

    /**
     * Main starting point for the process
     * 
     * @param args
     */
    public static void main(String[] args) {
        final String methodName = "main";
        long startTime = (new Date()).getTime();
        try {
            int argSize = args.length;

            if (argSize != 2) {
                _log.info(methodName, "Usage : VoucherChangeStatusGH [Constants file] [ProcessLogConfig file]");
                throw new BTSLBaseException("VoucherChangeStatusGH ", " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
            }
            new VoucherChangeStatusGH().process(args);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }// end of outer Exception
        finally {
            long endTime = (new Date()).getTime();
            ConfigServlet.destroyProcessCache();
            _log.info(methodName, "Exit : VoucherChangeStatusGH Total Time To execute Process" + Long.toString(endTime - startTime));

        }
    }

    /**
     * Method that handle the complete flow of the process
     * 
     * @param p_args
     * @throws BTSLBaseException
     */
    private void process(String[] p_args) throws BTSLBaseException {
        final String methodName = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered ");
        }
        Connection con = null;
        try {
            VoucherFileUploaderUtil.loadCachesAndLogFiles(p_args[0], p_args[1]);
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                _log.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                throw new BTSLBaseException("VoucherChangeStatusGH", "main", PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
            }

            loadConstantValues();
            loadFilesFromDir();
             int fileListSize = _fileList.size();
            for (int l = 0, size = fileListSize; l < size; l++) {
                _inputFile = null;
                _fileName = null;
                _inputFile = (File) _fileList.get(l);
                _fileName = _inputFile.getName();

                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, " FileNamne =" + _fileName);
                }
                String str = null;
                StringBuffer buffer = new StringBuffer();
                try {
                    String oldProductId = null;
                    String oldStatus = null;
                    String oldSerialNo = null;
                    ArrayList vomsBatchList = new ArrayList();
                    VomsBatchVO vomsBatchVO1 = null;
                    boolean restartFlag = false;
                    ArrayList fileData = new ArrayList();
                    int i = 0;
                    RandomAccessFile raf = new RandomAccessFile(_inputFile, "r");
                    while ((str = raf.readLine()) != null) {
                        str = str.trim();
                        fileData.add(str);
                    }
                    raf.close();
                    int maplelngth = fileData.size();
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, " FileSize= " + maplelngth);
                    }
                    Iterator maplelngthIte = fileData.iterator();
                    VomsBatchVO vomsBatchVODB = null;

                    while (maplelngthIte.hasNext()) {
                        String strList = (String) maplelngthIte.next();
                        String[] completeLineInFile = strList.split("#");
                        if (completeLineInFile.length != 2) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, strList + "#Invalid Colum Length");
                            }
                            buffer.append(strList + "#Invalid Colum Length");
                            buffer.append("\n");
                            i++;
                            continue;
                        }

                        vomsBatchVODB = null;

                        String serialNo = completeLineInFile[0];
                        String status = completeLineInFile[1];

                        // ValidateSerialNo

                        vomsBatchVODB = validateSerialNo(con, serialNo);

                        if (vomsBatchVODB == null) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, strList + "#Invalid Serial Number");
                            }
                            buffer.append(strList + "#Invalid Serial Number");
                            buffer.append("\n");
                            i++;
                            continue;
                        }
                        if (oldProductId != null && !oldProductId.equalsIgnoreCase(vomsBatchVODB.getProductid())) {
                            vomsBatchVO1.setToSerialNo(oldSerialNo);
                            vomsBatchList.add(vomsBatchVO1);
                            restartFlag = false;

                        } else if (oldStatus != null && !oldStatus.equalsIgnoreCase(status)) {
                            vomsBatchVO1.setToSerialNo(oldSerialNo);
                            vomsBatchList.add(vomsBatchVO1);
                            restartFlag = false;
                        } else if (oldSerialNo != null && Long.parseLong(serialNo) != Long.parseLong(oldSerialNo) + 1) {
                            vomsBatchVO1.setToSerialNo(oldSerialNo);
                            vomsBatchList.add(vomsBatchVO1);
                            restartFlag = false;
                        }

                        if (!restartFlag) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "New batch VO Created");
                            }
                            vomsBatchVO1 = new VomsBatchVO();
                            vomsBatchVO1.setProductid(vomsBatchVODB.getProductid());
                            vomsBatchVO1.setStatus(status);
                            vomsBatchVO1.setStartSerialNo(serialNo);
                            vomsBatchVO1.setReferenceNo(vomsBatchVODB.getReferenceNo());
                            vomsBatchVO1.setReferenceType(vomsBatchVODB.getReferenceType());
                        }

                        restartFlag = true;
                        oldProductId = vomsBatchVODB.getProductid();
                        oldSerialNo = serialNo;
                        oldStatus = status;
                        i++;

                        if (i == maplelngth) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Last Record of the file");
                            }
                            vomsBatchVO1.setToSerialNo(oldSerialNo);
                            vomsBatchList.add(vomsBatchVO1);
                            restartFlag = false;

                        }
                    }

                    Iterator vomsBatchListFinal = vomsBatchList.iterator();
                    VomsBatchVO vomsBatchVO = null;
                    int recordCount = 0;
                    while (vomsBatchListFinal.hasNext()) {
                        recordCount = 0;
                        VomsBatchVO vomsBatchVOfinal = (VomsBatchVO) vomsBatchListFinal.next();
                        String batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));

                        batchNo = new VomsUtil().formatVomsBatchID(vomsBatchVO, batchNo);

                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "After Id Generation=" + batchNo);
                        }
                        vomsBatchVO = new VomsBatchVO();
                        vomsBatchVO.setProductID(vomsBatchVOfinal.getProductid());
                        vomsBatchVO.setBatchType(vomsBatchVOfinal.getStatus());
                        vomsBatchVO.setNoOfVoucher(Long.parseLong(vomsBatchVOfinal.getToSerialNo()) - Long.parseLong(vomsBatchVOfinal.getStartSerialNo()) + 1);
                        vomsBatchVO.setFromSerialNo(vomsBatchVOfinal.getStartSerialNo());
                        vomsBatchVO.setToSerialNo(vomsBatchVOfinal.getToSerialNo());
                        vomsBatchVO.setFailCount(0);// for setting the no. of
                                                    // failed records
                        vomsBatchVO.setSuccessCount(Long.parseLong(vomsBatchVOfinal.getToSerialNo()) - Long.parseLong(vomsBatchVOfinal.getStartSerialNo()) + 1);
                        vomsBatchVO.setLocationCode("GH");
                        vomsBatchVO.setCreatedBy(TypesI.SYSTEM_USER);
                        vomsBatchVO.setCreatedOn(_currentDate);
                        vomsBatchVO.setBatchNo(batchNo);
                        vomsBatchVO.setModifiedBy(TypesI.SYSTEM_USER);
                        vomsBatchVO.setModifiedOn(_currentDate);
                        vomsBatchVO.setDownloadCount(1);
                        vomsBatchVO.setStatus(VOMSI.EXECUTED);
                        vomsBatchVO.setCreatedDate(_currentDate);
                        vomsBatchVO.setModifiedDate(_currentDate);
                        vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_CHANGE);
                        vomsBatchVO.setReferenceNo(vomsBatchVOfinal.getReferenceNo());
                        vomsBatchVO.setReferenceType(vomsBatchVOfinal.getReferenceType());
                        vomsBatchVO.setMessage(" Status SuccessFully Changed ...........");
                        if ("CU".equalsIgnoreCase(vomsBatchVOfinal.getStatus())) {
                            vomsBatchVO.setBatchType("EN");
                            recordCount = addBatch(con, vomsBatchVO);
                            vomsBatchVO.setBatchType("CU");
                        } else {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Status is =" + vomsBatchVOfinal.getStatus());
                            }
                            recordCount = addBatch(con, vomsBatchVO);
                        }
                        if (recordCount > 0) {
                            recordCount = 0;
                            recordCount = updateVoucherStatus(con, vomsBatchVO);
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "After Update Voms Voucher ");
                            }
                            if (recordCount > 0) {
                                con.commit();
                            }
                        }
                    }

                    if (recordCount > 0) {
                        String[] fileExt = _fileName.split("\\.");
                        String p_fileName1 = (fileExt[0]).concat("_").concat(BTSLUtil.getFileNameStringFromDate(new Date()).concat(".").concat(fileExt[1]));
                        raf = new RandomAccessFile(new File(_moveLocation + File.separator + p_fileName1 + "_FailRecord"), "rw");
                        raf.writeBytes(buffer.toString());
                        raf.close();
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Befor file Movement");
                            // VoucherFileUploaderUtil.moveFileToAnotherDirectory(_fileName,_filePath+File.separator+_fileName,_moveLocation);
                        }

                    }

                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (SQLException e1) {
                            _log.errorTrace(methodName, e1);
                        }
                    }

                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "file Movement Error " + _fileName);
                    }

                }
            }
        } catch (BTSLBaseException be) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    _log.errorTrace(methodName, e1);
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "BTSLBaseException" + be);
            }

            throw be;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                _log.errorTrace(methodName, e1);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exception" + e);
            }
            throw new BTSLBaseException("VoucherChangeStatusGH ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e1) {
                    _log.errorTrace(methodName, e1);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting ");
            }
        }
    }

    private int updateVoucherStatus(Connection con, VomsBatchVO vomsBatchVO) {
        final String methodName = "updateVoucherStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered ");
        }

        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            StringBuffer strBuff = new StringBuffer("update VOMS_VOUCHERS set CURRENT_STATUS=?,STATUS=?,PREVIOUS_STATUS=CURRENT_STATUS,LAST_BATCH_NO=?, MODIFIED_BY=?,MODIFIED_ON=?, SALE_BATCH_NO=? ");

            if ("EN".equalsIgnoreCase(vomsBatchVO.getBatchType())) {
                strBuff.append(", ENABLE_BATCH_NO=?");
            }

            strBuff.append("where SERIAL_NO between ? and ?");

            if (_log.isDebugEnabled()) {
                _log.debug("addBatch()", "addBatch()Query=" + strBuff.toString());
            }
            psmt = con.prepareStatement(strBuff.toString());
            psmt.clearParameters();
            int i = 0;
            psmt.setString(++i, vomsBatchVO.getBatchType());
            psmt.setString(++i, vomsBatchVO.getBatchType());
            psmt.setString(++i, vomsBatchVO.getBatchNo());
            psmt.setString(++i, TypesI.SYSTEM_USER);
            psmt.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getModifiedOn()));
            psmt.setString(++i, vomsBatchVO.getBatchNo());

            if ("EN".equalsIgnoreCase(vomsBatchVO.getBatchType())) {
                psmt.setString(++i, vomsBatchVO.getBatchNo());
            }

            psmt.setString(++i, vomsBatchVO.getFromSerialNo());
            psmt.setString(++i, vomsBatchVO.getToSerialNo());

            updateCount = psmt.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "  SQL Exception =" + sqe);
            _log.errorTrace(methodName, sqe);
        } catch (Exception ex) {
            _log.error(methodName, "  Exception =" + ex);
            _log.errorTrace(methodName, ex);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.error("addBatch()", " Exception while closing rs ex=" + e);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exit ");
        }
        return updateCount;

    }

    public int addBatch(Connection p_con, VomsBatchVO vomsBatchesVO) throws BTSLBaseException {
        final String methodName = "addBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered ");
        }

        PreparedStatement psmt = null;
        int addCount = 0;
        try {
            StringBuffer strBuff = new StringBuffer("INSERT INTO voms_batches (batch_no,product_id,batch_type, reference_no, reference_type,");
            strBuff.append("total_no_of_vouchers,from_serial_no, to_serial_no, network_code,created_date,created_on,created_by, status,process,modified_by,modified_on,modified_date,message,total_no_of_failure,total_no_of_success,DOWNLOAD_COUNT,remarks)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, strBuff.toString());
            }
            psmt = p_con.prepareStatement(strBuff.toString());

            psmt.clearParameters();

            psmt.setString(1, vomsBatchesVO.getBatchNo());
            psmt.setString(2, vomsBatchesVO.getProductID());
            psmt.setString(3, vomsBatchesVO.getBatchType());
            psmt.setString(4, vomsBatchesVO.getReferenceNo());
            psmt.setString(5, vomsBatchesVO.getReferenceType());
            psmt.setLong(6, (vomsBatchesVO.getNoOfVoucher()));
            psmt.setString(7, vomsBatchesVO.getFromSerialNo());
            psmt.setString(8, vomsBatchesVO.getToSerialNo());
            psmt.setString(9, vomsBatchesVO.getLocationCode());
            psmt.setDate(10, BTSLUtil.getSQLDateFromUtilDate(vomsBatchesVO.getCreatedDate()));
            psmt.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(vomsBatchesVO.getCreatedOn()));
            psmt.setString(12, vomsBatchesVO.getCreatedBy());
            psmt.setString(13, vomsBatchesVO.getStatus());
            psmt.setString(14, vomsBatchesVO.getProcess());
            psmt.setString(15, vomsBatchesVO.getModifiedBy());
            psmt.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(vomsBatchesVO.getModifiedOn()));
            psmt.setDate(17, BTSLUtil.getSQLDateFromUtilDate(vomsBatchesVO.getModifiedOn()));
            psmt.setString(18, vomsBatchesVO.getMessage());
            psmt.setLong(19, vomsBatchesVO.getFailCount());
            psmt.setLong(20, vomsBatchesVO.getSuccessCount());
            psmt.setLong(21, vomsBatchesVO.getDownloadCount());
            psmt.setString(22, vomsBatchesVO.getRemarks());
            addCount += psmt.executeUpdate();

        } catch (SQLException sqe) {
            _log.error(methodName, "  SQL Exception =" + sqe);
            _log.errorTrace(methodName, sqe);
        } catch (Exception ex) {
            _log.error(methodName, "  Exception =" + ex);
            _log.errorTrace(methodName, ex);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.error(methodName, " Exception while closing rs ex=" + e);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exit ");
        }
        return addCount;
    }

    private VomsBatchVO validateSerialNo(Connection con, String serialNo) {

        final String methodName = "validateSerialNo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered ");
        }

        VomsBatchVO vomsBatchVODB = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        String sqlUpdate = "select LAST_BATCH_NO,product_id,CURRENT_STATUS from VOMS_VOUCHERS  where SERIAL_NO=?";

        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, sqlUpdate);
            }

            pstmtUpdate = con.prepareStatement(sqlUpdate);
            pstmtUpdate.setString(1, serialNo);
            rs = pstmtUpdate.executeQuery();
            while (rs.next()) {
                vomsBatchVODB = new VomsBatchVO();
                vomsBatchVODB.setReferenceNo(rs.getString("LAST_BATCH_NO"));
                vomsBatchVODB.setProductid(rs.getString("product_id"));
                vomsBatchVODB.setReferenceType(rs.getString("product_id"));

            }
        } catch (SQLException sqe) {
            _log.error(methodName, "  SQLException =" + sqe);
            _log.errorTrace(methodName, sqe);
        } catch (Exception e) {
            _log.error(methodName, "  Exception =" + e);
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exit ");
        }
        return vomsBatchVODB;
    }

    /**
     * Method to load constant values for the process
     * 
     * @throws BTSLBaseException
     */
    public void loadConstantValues() throws BTSLBaseException {
        final String methodName = "loadConstantValues";
        if (_log.isDebugEnabled()) {
            _log.debug("loadConstantValues ", " Entered ");
        }
        try {

            // this is the path of the input voucher file.
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues ", ": reading  _filePath ");
            }

            _filePath = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_PATH_CHANGE_STATUS"));

            // Checking whether the file path provided exist or not.If not,
            // throw an exception
            if (!(new File(_filePath).exists())) {
                _log.debug(methodName, " Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH_CHANGE_STATUS not defined properly");
                throw new BTSLBaseException("VoucherChangeStatusGH", "loadConstantValues", PretupsErrorCodesI.VOUCHER_ERROR_DIR_NOT_EXIST);
            }

            // this is the location where the voucher file will be moved after
            // the vouchers are uploaded
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues :", " reading _moveLocation ");
            }
            _moveLocation = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_MOVE_PATH_CHANGE_STATUS"));

            // Destination location where the input file will be moved after
            // successful reading.
            _destFile = new File(_moveLocation);

            // Checking the destination location for the existence. If it does
            // not exist stop the proccess.
            if (!_destFile.exists()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("loadConstantValues ", " Destination Location checking= " + _moveLocation + " does not exist");
                }
                boolean fileCreation = _destFile.mkdirs();
                if (fileCreation) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadConstantValues ", " New Location = " + _destFile + "has been created successfully");
                    } else {
                        _log.debug(methodName, " Configuration Problem, Could not create the backup directory at the specified location " + _moveLocation);
                        throw new BTSLBaseException("VoucherChangeStatusGH", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be = " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception e=" + e);
            throw new BTSLBaseException("VoucherChangeStatusGH", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadConstantValues ", " Exiting: _filePath = " + _filePath + " _moveLocation= " + _moveLocation);
            }
        }
    }

    /**
     * This method is used to loadAll the files with specified prefix.
     * All these file objects are stored in ArrayList.
     * 
     * @throws BTSLBaseException
     */
    public void loadFilesFromDir() throws BTSLBaseException {
        final String methodName = "loadFilesFromDir";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        File directory = null;
        try {
            directory = new File(_filePath);
            if (directory.list() == null) {
                throw new BTSLBaseException(methodName, "loadFileFromDir", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }

            // This filter is used to filter all the files that start with
            // _filePrefix;
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(_fileExt));
                }
            };

            // List of files that start with the file prefix with specified
            // extantion.
            File[] tempFileArray = directory.listFiles(filter);
            _fileList = new ArrayList();

            // Storing all the files(not dir)to array list
            for (int l = 0, size = tempFileArray.length; l < size; l++) {
                if (tempFileArray[l].isFile()) {
                    _fileList.add(tempFileArray[l]);
                    if (_log.isDebugEnabled()) {
                        _log.debug((methodName), "File = " + tempFileArray[l] + " is added to fileList");
                    }
                }
            }// end of for loop

            // Check whether the directory contains the file start with
            // filePrefix.
            if (_fileList.isEmpty()) {
                throw new BTSLBaseException(methodName, "loadFileFromDir", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, " No files exists at the following (" + _filePath + ") specified, please check the path");
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e = " + e.getMessage());
            throw new BTSLBaseException(this, "loadFilesFromDir", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited");
            }
        }// end of finally
    }// end of loadFilesFromDir
}
