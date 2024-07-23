package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.loyalty.transaction.LoyaltyDAO;
import com.btsl.pretups.loyalty.transaction.LoyaltyVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;

import jxl.Cell;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class UploadLoyaltyPonitsThroughFile {

    private static final Log _logger = LogFactory.getLog(UploadLoyaltyPonitsThroughFile.class.getName());
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO = null;

    private WritableFont heading11font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat heading11format = new WritableCellFormat(heading11font);
    private WritableFont text11font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true);
    private WritableCellFormat text11format = new WritableCellFormat(text11font);

    public static void main(String[] args) {
        String file = null;
        final String METHOD_NAME = "main";
        try {
            if (args.length != 3) {
                _logger.info(METHOD_NAME, "Usage : UploadLoyaltyPonitsThroughFile [Constants file] [LogConfig file] [Upload File Path]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                _logger.info(METHOD_NAME, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                _logger.info(METHOD_NAME, " Logconfig File Not Found .............");
                return;
            }
            file = args[2];
            if (file == null) {
                _logger.info(METHOD_NAME, " Uploaded File Not Found .............");
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        }// end try
        catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final UploadLoyaltyPonitsThroughFile uploadLoyaltyPonitsThroughFile = new UploadLoyaltyPonitsThroughFile();
            uploadLoyaltyPonitsThroughFile.process(file);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    public void process(String p_file) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug("UploadLoyaltyPonitsThroughFile", " Entered:  p_file= " + p_file);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean statusOk = false;
        Date processedUpto = null;
        final Date currentDate = new Date();
        ArrayList uploadedDataList = null;
        ArrayList processedDataList = null;
        String downloadFilePath = null;
        String downloadFileName = null;
        String fileName = null;
        final String methodName = "process";
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, ProcessI.LMSUPLOADLP);
            statusOk = _processStatusVO.isStatusOkBool();
            // check process status.
            if (statusOk) {
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(processedUpto));
                    final int diffDate = BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate);
                    if (diffDate <= 1) {
                        _logger.error("UploadLoyaltyPonitsThroughFile", " Loyalty Points Upload Process has been already executed.....");
                        throw new BTSLBaseException("UploadLoyaltyPonitsThroughFile", methodName, PretupsErrorCodesI.LOYALTY_POINTS_UPLOAD_PROCESS_ALREADY_EXECUTED);
                    }
                }
            } else {
                throw new BTSLBaseException("UploadLoyaltyPonitsThroughFile", methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }

            // Read data from excel file and store in Arraylist

            uploadedDataList = readUploadedSheetData(p_file);
            if (uploadedDataList.size() > 0) {
                processedDataList = validateAndUploadData(uploadedDataList);
            } else {
                _logger.error("UploadLoyaltyPonitsThroughFile", " No records have been found to process");
                throw new BTSLBaseException("UploadLoyaltyPonitsThroughFile", methodName, PretupsErrorCodesI.LOYALTY_POINTS_UPLOAD_FILE_BLANK);
            }

            if (processedDataList.size() > 0) {
                downloadFilePath = Constants.getProperty("LOYALTY_POINTS_PROCESS_DOWNLOADED_FILE_PATH");
                downloadFileName = Constants.getProperty("LOYALTY_POINTS_PROCESS_DOWNLOADED_FILE_NAME") + "_" + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
                fileName = downloadFilePath + downloadFileName;
                // Write Processed Data in Excel File
                try {
                    writeUploadedtExcel(processedDataList, fileName);
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            } else {
                _logger.error("UploadLoyaltyPonitsThroughFile", " Error in data validation and uploading process");
                throw new BTSLBaseException("UploadLoyaltyPonitsThroughFile", methodName, PretupsErrorCodesI.LOYALTY_POINTS_UPLOAD_FILE_PROCESS_ERROR);
            }
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, ProcessI.LMSUPLOADLP) == 1) {
                        try {
                        	mcomCon.finalCommit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                        	mcomCon.finalRollback();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("UploadLoyaltyPonitsThroughFile#process");
					mcomCon = null;
				}
            } catch (Exception ex) {
                _logger.errorTrace(methodName, ex);
            }
        }
    }

    public ArrayList readUploadedSheetData(String p_file) {
        final String METHOD_NAME = "readUploadedSheetData";
        if (_logger.isDebugEnabled()) {
            _logger.debug("readZoneDetailsSheetData()", " Entered");
        }
        Workbook workbook = null;
        Sheet excelsheet = null;
        String strArr[][] = null;
        LoyaltyVO loyaltyVO = null;
        ArrayList uploadData = null;
        try {
            workbook = Workbook.getWorkbook(new File(p_file));
            excelsheet = workbook.getSheet(0);
            final int noOfRows = excelsheet.getRows();
            final int noOfcols = excelsheet.getColumns();
            strArr = new String[noOfRows][noOfcols];

            Cell cell = null;
            String content = null;
            String key = null;
            final int[] indexMapArray = new int[noOfcols];
            String indexStr = null;
            for (int col = 0; col < noOfcols; col++) {
                indexStr = null;
                key = String.valueOf(col);
                indexStr = String.valueOf(col);
                indexMapArray[col] = Integer.parseInt(indexStr);
                strArr[0][indexMapArray[col]] = key;

            }
            for (int row = 1; row < noOfRows; row++) {
                for (int col = 0; col < noOfcols; col++) {
                    cell = excelsheet.getCell(col, row);
                    content = cell.getContents();
                    strArr[row][indexMapArray[col]] = content;
                }
            }
            uploadData = new ArrayList();
            for (int i = 1; i < strArr.length; i++) {
                loyaltyVO = new LoyaltyVO();
                loyaltyVO.setReciverMsisdn(strArr[i][0]); // MSISDN
                loyaltyVO.setLoyaltyPoint(strArr[i][1]); // Loyalty Points
                uploadData.add(loyaltyVO);
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("readZoneDetailsSheetData()", " Exited");
            }
        }
        return uploadData;
    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        _processStatusVO.setExecutedOn(currentDate);
        _processStatusVO.setExecutedUpto(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    public ArrayList validateAndUploadData(ArrayList p_uploadedData) throws BTSLBaseException {
        final String METHOD_NAME = "validateAndUploadData";
        if (_logger.isDebugEnabled()) {
            _logger.debug("validateAndUploadData", " Entered:  p_uploadedData size:" + p_uploadedData.size());
        }
        ArrayList dataList = null;
        LoyaltyVO dataProcessVO = null;
        LoyaltyDAO loyaltyDAO = null;
        // boolean errorFleg = false;
        String errorComment = null;
        try {
            loyaltyDAO = new LoyaltyDAO();
            dataList = new ArrayList();
            int uploadedDataSize = p_uploadedData.size();
            for (int i = 0; i < uploadedDataSize; i++) {
                dataProcessVO = (LoyaltyVO) p_uploadedData.get(i);
                // Check for MSISDN
                if (BTSLUtil.isNullString(dataProcessVO.getReciverMsisdn())) {
                    dataProcessVO.setErrorFlag(true);
                    dataProcessVO.setComments("Please enter MSISDN");
                } else if (!BTSLUtil.isNumeric(dataProcessVO.getReciverMsisdn())) {
                    dataProcessVO.setErrorFlag(true);
                    dataProcessVO.setComments("MSISDN should be Numeric value");
                }

                if (BTSLUtil.isNullString(dataProcessVO.getLoyaltyPoint())) {

                    if (!BTSLUtil.isNullString(dataProcessVO.getComments())) {
                        errorComment = dataProcessVO.getComments() + " ,Please enter loyalty points value";
                        dataProcessVO.setComments(errorComment);
                        dataProcessVO.setErrorFlag(true);
                    } else {
                        dataProcessVO.setComments(" ,Please enter loyalty points value");
                        dataProcessVO.setErrorFlag(true);
                    }
                } else if (!BTSLUtil.isNumeric(dataProcessVO.getLoyaltyPoint())) {
                    if (!BTSLUtil.isNullString(dataProcessVO.getComments())) {
                        errorComment = dataProcessVO.getComments() + " ,Loyalty points should be Numeric value";
                        dataProcessVO.setComments(errorComment);
                        dataProcessVO.setErrorFlag(true);
                    } else {
                        dataProcessVO.setComments(" ,Loyalty points should be Numeric value");
                        dataProcessVO.setErrorFlag(true);
                    }
                }
                if (!dataProcessVO.getErrorFlag()) {
                    try {
                        dataProcessVO = loyaltyDAO.validateUserAndUploadPoints(dataProcessVO);
                    } catch (Exception exe) {
                        _logger.errorTrace(METHOD_NAME, exe);
                    }
                }
                dataList.add(dataProcessVO);
            }
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("validateAndUploadData", "Exiting: dataList Size=" + dataList.size());
            }
        }
        return dataList;
    }

    public void writeUploadedtExcel(ArrayList p_dataList, String p_fileName) throws Exception {
        final String METHOD_NAME = "writeUploadedtExcel";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeUploadedtExcel()", "Entered: p_dataList::" + p_dataList.size() + "p_fileName::" + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        SheetSettings sheetSetting = null;
        int col = 0, row = 0;
        Label label = null;
        String keyName = null;
        LoyaltyVO loyaltyVO = null;
        try {
            sheetSetting = new SheetSettings();
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet = workbook.createSheet("Downloaded File", 0);
            sheetSetting = worksheet.getSettings();
            sheetSetting.setShowGridLines(true);

            col = 0;
            row = 0;
            keyName = "MSISDN";
            label = new Label(col, row, keyName, heading11format);
            worksheet.addCell(label);

            col = 1;
            row = 0;
            keyName = "Loyalty_Points";
            label = new Label(col, row, keyName, heading11format);
            worksheet.addCell(label);

            col = 2;
            row = 0;
            keyName = "COMMENTS";
            label = new Label(col, row, keyName, heading11format);
            worksheet.addCell(label);

            // Heading of Sheet//
            if (p_dataList.size() > 0) {
                col = 0;
                row = 1;
                for (int i = 0, j = p_dataList.size(); i < j; i++) {
                    loyaltyVO = (LoyaltyVO) p_dataList.get(i);
                    col = 0;
                    keyName = loyaltyVO.getReciverMsisdn();
                    label = new Label(col, row, keyName, text11format);
                    worksheet.addCell(label);

                    col = 1;
                    keyName = loyaltyVO.getLoyaltyPoint();
                    label = new Label(col, row, keyName, text11format);
                    worksheet.addCell(label);

                    col = 2;
                    keyName = loyaltyVO.getComments();
                    label = new Label(col, row, keyName, text11format);
                    worksheet.addCell(label);

                    col = 0;
                    row++;
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("writeUploadedtExcel()", "Now of Rows written i:: " + i);
                    }
                }
            } else {
                col = 0;
                row = 2;
                keyName = "No record to write in excel";
                label = new Label(col, row, keyName, heading11format);
                worksheet.addCell(label);
            }
            workbook.write();
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            worksheet = null;
            workbook = null;
            try {
                p_dataList.clear();
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeUploadedtExcel()", "Exited");
            }
        }
    }
}